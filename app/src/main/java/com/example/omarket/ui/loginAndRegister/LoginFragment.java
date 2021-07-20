package com.example.omarket.ui.loginAndRegister;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.omarket.R;
import com.example.omarket.backend.api.APIHandler;
import com.example.omarket.backend.handlers.PasswordGenerator;
import com.example.omarket.backend.handlers.loginlogout.Login;
import com.example.omarket.backend.handlers.validators.EmailValidator;
import com.example.omarket.backend.handlers.validators.PasswordValidator;
import com.example.omarket.backend.user.User;
import com.example.omarket.backend.user.UserType;
import com.example.omarket.ui.NavigationFragment;
import com.example.omarket.ui.main_fragments.Color;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LoginFragment extends NavigationFragment implements View.OnClickListener, View.OnTouchListener {
    GoogleSignInClient mGoogleSignInClient;
    private View currentView;
    private Button loginButton;
    public TextView register, warningView;
    private EditText emailText, passwordText;
    private ProgressBar progressBar;
    SignInButton signInButton;
    // Validators :
    EmailValidator emailValidator;
    PasswordValidator passwordValidator;

    private User currentUser;
    private Login login;

    private Animation shakeAnimation;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        handleSignInResult(task);
                    }
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // signed in successfully, show authenticated UI
            updateUI(account);
        } catch (ApiException e) {
            // the ApiException status code indicates the detailed failure reason
            updateUI(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUI(GoogleSignInAccount acct) {
        if (acct != null) {
            String personName = acct.getDisplayName();
            new User(acct.getDisplayName(), acct.getPhotoUrl(), acct.getEmail(), UserType.USER);// user login with google
//            User user =
//            emailText.setText(user.emailAddress);
//            passwordText.setText(user.emailAddress);
//
//            // create or login user.
//            HashMap<String, String> body = new HashMap<>();
//            body.put("first_name", user.fullName);
//            body.put("last_name", "      ");
//            String pass = PasswordGenerator.generateStrongPassword();
//            body.put("password", pass);
//            body.put("password2", pass);
//            body.put("email", user.emailAddress);

            Context context = getActivity();
            // save key value data
            SharedPreferences sharedPref = context.getSharedPreferences(
                    getString(R.string.profile_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.name_key), acct.getDisplayName());
            editor.putString(getString(R.string.email_key), acct.getEmail());
            editor.putString(getString(R.string.image_key), acct.getPhotoUrl().toString());
            editor.apply();
            Toast.makeText(getActivity(), "login as " + personName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Could not sign in with google", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = User.getCurrentLoginUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        currentUser = new User();
        currentView = inflater.inflate(R.layout.fragment_login, container, false);
        setUiObjects();

        return currentView;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setUiObjects() {
        emailText = currentView.findViewById(R.id.login_edit_text_email_address);
        emailText.setOnTouchListener(this);
        passwordText = currentView.findViewById(R.id.login_edit_text_password);
        passwordText.setOnTouchListener(this);
        signInButton = currentView.findViewById(R.id.google_login_button);
        signInButton.setOnClickListener(this);
        progressBar = currentView.findViewById(R.id.login_fragment_progressBar);
        warningView = currentView.findViewById(R.id.login_warning_view);
        loginButton = currentView.findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(this);
        register = currentView.findViewById(R.id.login_text_view_register);
        register.setOnClickListener(this);
        login = Login.getInstance();
        shakeAnimation = AnimationUtils.loadAnimation(currentView.getContext(), R.anim.shake);
        // validators.
        emailValidator = EmailValidator.getInstance();
        passwordValidator = PasswordValidator.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        // check for an existing signed-in user
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        updateUI(account);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mStartForResult.launch(signInIntent);
    }


    private void updateInput() {
        if (emailText.getText() != null)
            currentUser.emailAddress = emailText.getText().toString();
        if (passwordText.getText() != null)
            currentUser.password = passwordText.getText().toString();

    }


    @SuppressLint({"ResourceType", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        setDefaultConfig();
        switch (v.getId()) {
            case R.id.login_login_button:
                login(v);
                break;
            case R.id.login_text_view_register:
                navigateFromViewTo(v, R.id.action_loginFragment_to_registerFragment);
                break;
            case R.id.google_login_button:
                signIn();
                break;

        }
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId", "ClickableViewAccessibility"})
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setDefaultConfig();
        switch (v.getId()) {

//            case R.id.login_edit_text_email_address:
//                if (passwordText.getText() != null
//                        && !"".equals(passwordText.getText().toString().trim())
//                        && passwordText.isInTouchMode()) {
//
//                    if (!passwordValidator.validate(passwordText.getText().toString()).success) {
//                        warningView.setText(passwordValidator.getValidateError());
//                        return false;
//                    }
//                }
//                break;

            case R.id.login_edit_text_password:
                if (emailText.getText() == null || "".equals(emailText.getText().toString().trim())) {
                    warningView.setText("Email address can't be empty!");
                    return false;
                }
        }

        return false;
    }


    public void setDefaultConfig() {
        emailText.setHint("Email Address");
        passwordText.setHint("Password");
        Color.changeViewColor(emailText, R.color.black);
        Color.changeViewColor(passwordText, R.color.black);
        Color.changeHintViewColor(emailText, R.color.gray);
        Color.changeHintViewColor(passwordText, R.color.gray);
        warningView.setText("");
    }

    private void login(View v) {
        HashMap<String, String> body = new HashMap<>();
        body.put("email", emailText.getText().toString());
        body.put("password", passwordText.getText().toString());
        Thread thread = new Thread() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                APIHandler.loginOrRegisterApi(getActivity(), body, "login");
                User user;
                do {
                    user = User.getCurrentLoginUser();
                } while (!user.is_login && user.loginOrRgisterErrors == null);
                changeVisibilityTo(progressBar, View.INVISIBLE);
            }
        };
        changeVisibilityTo(progressBar, View.VISIBLE);
        thread.start();
        User user = User.getCurrentLoginUser();
        if (user.is_login) {
            changeVisibilityTo(progressBar, View.VISIBLE);
            APIHandler.getUserInfoApi(getActivity(), null, "C");
            changeVisibilityTo(progressBar, View.INVISIBLE);
            navigateFromViewTo(getView(), R.id.action_loginFragment_to_mainActivity);
            return;
        }

        if (user.loginOrRgisterErrors != null) {
            String email = "", password = "";
            try {
                email = user.loginOrRgisterErrors.get("email").toString();
                if (emailText.getText() == null || emailText.getText().toString().trim().equals("")) {
                    emailText.setText("");
                    emailText.setHint(email);
                    viewFailed(emailText);
                } else {
                    warningView.setText(email);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                password = user.loginOrRgisterErrors.get("password").toString();
                passwordText.setText("");
                passwordText.setHint(password);
                viewFailed(passwordText);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (email.trim().equals("") && password.trim().equals("") && user.loginOrRgisterErrors != null) {
                warningView.setText(user.loginOrRgisterErrors.toString());
            }
        }


    }

    public void changeVisibilityTo(View v, int visibility) {
        v.setVisibility(visibility);
    }

    private void viewFailed(View v) {
        Color.changeViewColor(v, R.color.red);
        Color.changeHintViewColor(v, R.color.red);
        v.startAnimation(shakeAnimation);
    }

    @SuppressLint("SetTextI18n")
    private void loginFailedAction() {
        passwordText.setText("");
        Color.changeViewColor(emailText, R.color.red);
        passwordText.startAnimation(shakeAnimation);
        emailText.startAnimation(shakeAnimation);
        warningView.setText(currentUser.creationResponse.errors.get(0).second);
        warningView.startAnimation(shakeAnimation);

    }

}