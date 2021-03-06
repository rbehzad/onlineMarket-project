package com.example.omarket.backend.user;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.omarket.backend.handlers.validators.UserInfoValidator;
import com.example.omarket.backend.product.Product;
import com.example.omarket.backend.response.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;

public class User extends CloneNotSupportedException {
    public ArrayList<Product> allProducts;
    public static ArrayList<User> allUser;
    public static User currentLoginUser = new User();
    public String token;
    public boolean is_login = false;
    public JSONObject loginOrRgisterErrors;
    public String body;
    public boolean is_in_request = true;
    public String fullName;
    public String emailAddress;
    public String password;
    public String phoneNumber;
    public UserType userType;
    public Bitmap personPhotoBitmap;
    public Uri personPhotoUri;
    public boolean isInProgress = false;

    final UserInfoValidator validator = UserInfoValidator.getInstance();

    public Response creationResponse;

    public User() {
        allProducts = new ArrayList<>();
        allUser = new ArrayList<>();
    }

    public User(String fullName, Uri personPhoto, String emailAddress, UserType userType) {// google sign in constructor
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.personPhotoUri = personPhoto;
        this.userType = userType;
    }

    // getter
    public String getFullName() {
        return fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserType getUserType() {
        return userType;
    }

    public Bitmap getPersonPhoto() {
        return personPhotoBitmap;
    }

    public static User getCurrentLoginUser() {
        if (currentLoginUser != null)
            return currentLoginUser;
        currentLoginUser = new User();
        return currentLoginUser;
    }

    public static int get(String emailAddress){
        for (int i = 0; i < allUser.size(); i++) {
            if (allUser.get(i).emailAddress.equals(emailAddress)) return i;
        }
        return -1;
    }

    @NonNull
    @NotNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
