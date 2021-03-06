package com.example.omarket.backend.api;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.omarket.backend.product.Product;
import com.example.omarket.backend.response.Result;
import com.example.omarket.backend.response.ServerCallback;
import com.example.omarket.backend.user.User;
import com.example.omarket.backend.user.UserType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class APIHandler implements Response.ErrorListener {

    final static String domain = "http://192.168.51.7";

    // user
    final static String loginURL = "/api/user/login/";
    final static String registerURL = "/api/user/register/";
    final static String userInfoByEmailURL = "/api/user/info-by-email/";
    final static String userInfoUpdateURL = "/api/user/update/";
    final static String setFavoriteURL = "/api/user/favorites/set/";
    final static String getAllFavoritesURL = "/api/user/favorites/get/";
    final static String deleteFavoriteProductURL = "/api/user/favorites/delete/";

    //
    final static String forgetPasswordEmailURL = "/api/user/forget-password/";

    // product
    final static String addProductURL = "/api/product/create/";
    final static String updateProductURL = "/api/product/update/";
    final static String deleteProductURL = "/api/product/delete/";
    final static String allProductGetURL = "/api/product/get-all/";


    public static APIHandler apiHandler = new APIHandler();

    public static void loginOrRegisterApi(ServerCallback<User> loginUser, Context context, Map<String, String> body, String requestType) {

        String requestURL = (requestType.equals("login") ? loginURL : registerURL);
        String tag = (requestType.equals("login") ? "login" : "register");

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject bodyJson = new JSONObject(body);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, domain + requestURL, bodyJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    User.currentLoginUser.is_login = true;
                    User.currentLoginUser.token = (String) response.get("token");
                    User.currentLoginUser.emailAddress = response.getString("email");
                    loginUser.onComplete(new Result.Success<>(User.currentLoginUser));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                String body = null;
                //get status code here
                String statusCode;
                User user = new User();
                if (error.networkResponse != null) {
                    statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    if (error.networkResponse.data != null) {
                        body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    try {
                        if (body != null)
                            user.loginOrRgisterErrors = new JSONObject(body);
                        else
                            user.loginOrRgisterErrors = new JSONObject("{\"response\":\"Request failed\"");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loginUser.onComplete(new Result.Error<>(user));
                } else Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        request.setTag(tag);
        requestQueue.add(request);

    }

    public static void getUserInfoApi(ServerCallback<User> userServerCallback, Context context, Map<String, Object> body) {
        // C : current user , N : not current user
        JSONObject jsonBody;
        if (body != null)
            jsonBody = new JSONObject(body);
        else jsonBody = null;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, domain + userInfoByEmailURL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                User user = new User();
                try {
                    user.emailAddress = (String) response.get("email");
                    user.fullName = (String) response.get("first_name") + " " + (String) response.get("last_name");
                    boolean is_admin = response.getBoolean("is_superuser");
                    user.userType = (is_admin ? UserType.SUPER_ADMIN : UserType.USER);
                    // get image:
                    boolean is_null = response.isNull("base64_image");
                    if (!is_null) {
                        String encodedImage = (String) response.get("base64_image");
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        user.personPhotoBitmap = BitmapFactory.decodeByteArray(
                                decodedString,
                                0,
                                decodedString.length
                        );
                    }

                    String phone_number = null;
                    if (!response.isNull("phone_number")) {
                        phone_number = (String) response.get("phone_number");
                    }
                    user.phoneNumber = phone_number;
                    user.isInProgress = false;
                    userServerCallback.onComplete(new Result.Success<>(user));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                String body = null;
                //get status code here
                User user = new User();
                String statusCode;
                if (error.networkResponse != null) {
                    statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    if (error.networkResponse.data != null) {
                        body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    try {
                        if (body != null)
                            user.loginOrRgisterErrors = new JSONObject(body);
                        else
                            user.loginOrRgisterErrors = new JSONObject("{\"response\":\"Request failed\"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        user.loginOrRgisterErrors = new JSONObject("{\"response\":\"Request failed\"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                userServerCallback.onComplete(new Result.Error<>(user));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + User.currentLoginUser.token);
                return params;
            }
        };

        request.setTag("UserInfo");
        requestQueue.add(request);
    }

    public static void sendRequestOrGet(ServerCallback<String> serverCallback, Context context, Map<String, Object> body, String UU_AP_UP_DP_SF_GF_DF) {
        String requestURL = null;
        int method = 0;
        switch (UU_AP_UP_DP_SF_GF_DF) {
            case "UU":
                method = Request.Method.POST;
                requestURL = userInfoUpdateURL;
                break;
            case "AP":
                method = Request.Method.POST;
                requestURL = addProductURL;
                break;
            case "UP":
                method = Request.Method.POST;
                requestURL = updateProductURL;
                break;
            case "DP":
                method = Request.Method.POST;
                requestURL = deleteProductURL;
                break;
            case "SF":
                method = Request.Method.POST;
                requestURL = setFavoriteURL;
                break;
            case "DF":
                method = Request.Method.POST;
                requestURL = deleteFavoriteProductURL;
                break;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject bodyJson = new JSONObject(body);
        JsonObjectRequest request = new JsonObjectRequest(method, domain + requestURL, bodyJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                serverCallback.onComplete(new Result.Success<>("S"));

            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                String body = null;
                //get status code here
                String statusCode;
                if (error.networkResponse != null) {
                    statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    if (error.networkResponse.data != null) {
                        body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
//                        if (!UU_AP_UP_DP_SF_GF_DF.equals("DF")&&!UU_AP_UP_DP_SF_GF_DF.equals("AF"))
                        Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
                    }
                }
                serverCallback.onComplete(new Result.Error<>("F"));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + User.currentLoginUser.token);
                return params;
            }
        };
        requestQueue.add(request);

    }

    public static void getAllProductApi(ServerCallback<ArrayList<Product>> serverCallback, Context context) {
        int method = Request.Method.GET;
        String requestURL = allProductGetURL;

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest request = new JsonArrayRequest(method, domain + requestURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<Product> products = new ArrayList<>();
                try {
                    for (int i = 1; i < response.length(); i++) {
                        Product p = Adapter.productApiAdapter(response.getJSONObject(i));
                        products.add(p);
                    }
                    serverCallback.onComplete(new Result.Success<>(products));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                String body = null;
                //get status code here
                String statusCode;
                if (error.networkResponse != null) {
                    statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    if (error.networkResponse.data != null) {
                        body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
                    }
                }
                serverCallback.onComplete(new Result.Error<>(null));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + User.currentLoginUser.token);
                return params;
            }
        };

        request.setTag("getAllProduct");
        requestQueue.add(request);
    }

    public static void getAllFavoriteApi(ServerCallback<ArrayList<String>> serverCallback, Context context) {
        int method = Request.Method.GET;
        String requestURL = getAllFavoritesURL;

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest request = new JsonArrayRequest(method, domain + requestURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<String> products_id = new ArrayList<>();
                try {
                    for (int i = 1; i < response.length(); i++) {
                        String id = (String) response.getJSONObject(i).get("product");
                        products_id.add(id);
                    }
                    serverCallback.onComplete(new Result.Success<>(products_id));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                String body = null;
                //get status code here
                String statusCode;
                if (error.networkResponse != null) {
                    statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    if (error.networkResponse.data != null) {
                        body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
                    }
                }
                serverCallback.onComplete(new Result.Error<>(null));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + User.currentLoginUser.token);
                return params;
            }
        };

        request.setTag("getAllProduct");
        requestQueue.add(request);
    }

    public static void forgetPasswordApi(ServerCallback<HashMap<String, String>> serverCallback, Context context ,HashMap<String, Object> body) {

        String requestURL = null;
        int method = 0;
        method = Request.Method.POST;
        requestURL = forgetPasswordEmailURL;

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject bodyJson = new JSONObject(body);
        JsonObjectRequest request = new JsonObjectRequest(method, domain + requestURL, bodyJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HashMap<String, String> data = new HashMap<>();
                try{
                    data.put("code",response.getString("code"));
                    data.put("token",response.getString("token"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                serverCallback.onComplete(new Result.Success<>(data));


            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                String body = null;
                //get status code here
                String statusCode;
                if (error.networkResponse != null) {
                    statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    if (error.networkResponse.data != null) {
                        body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
//                        if (!UU_AP_UP_DP_SF_GF_DF.equals("DF")&&!UU_AP_UP_DP_SF_GF_DF.equals("AF"))
                        Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
                    }
                }
                serverCallback.onComplete(new Result.Error<>(null));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + User.currentLoginUser.token);
                return params;
            }
        };
        requestQueue.add(request);


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onErrorResponse(VolleyError error) {
        String body = null;
        //get status code here
        String statusCode;
        if (error.networkResponse != null) {
            statusCode = String.valueOf(error.networkResponse.statusCode);
            //get response body and parse with appropriate encoding
            if (error.networkResponse.data != null) {
                body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
            }
            try {
                if (body != null)
                    User.getCurrentLoginUser().loginOrRgisterErrors = new JSONObject(body);
                else
                    User.getCurrentLoginUser().loginOrRgisterErrors = new JSONObject("{\"response\":\"Request failed\"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
