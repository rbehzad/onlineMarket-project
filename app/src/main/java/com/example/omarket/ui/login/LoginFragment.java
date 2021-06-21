package com.example.omarket.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.omarket.R;
import com.example.omarket.backend.handlers.loginlogout.Login;
import com.example.omarket.backend.handlers.validators.EmailValidator;
import com.example.omarket.backend.handlers.validators.PasswordValidator;
import com.example.omarket.backend.user.User;
import com.example.omarket.ui.NavigationFragment;
import com.example.omarket.ui.main_fragments.Color;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

public class LoginFragment extends NavigationFragment implements View.OnClickListener, View.OnTouchListener {

    private View currentView;
    private GoogleSignInClient mGoogleSignInClient;
    private Button loginButton;
    private TextView register, warningView;
    private EditText emailText, passwordText;


    // Validators :
    EmailValidator emailValidator;
    PasswordValidator passwordValidator;

    private User currentUser;
    private Login login;

    private Animation shakeAnimation;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        handleSignInResult(task);
                    }
                }
            });


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

        warningView = currentView.findViewById(R.id.login_warning_view);

        loginButton = currentView.findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(this);
        register = currentView.findViewById(R.id.login_text_view_register);
        register.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        login = Login.getInstance();

        shakeAnimation = AnimationUtils.loadAnimation(currentView.getContext(), R.anim.shake);

        // validators.
        emailValidator = EmailValidator.getInstance();
        passwordValidator = PasswordValidator.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // check for an existing signed-in user
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        updateUI(account);
    }

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

    private void updateUI(GoogleSignInAccount acct) {
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            Toast.makeText(getActivity(), "login as " + personName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Could not sign in with google", Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn(View view) {
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

        }
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId", "ClickableViewAccessibility"})
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setDefaultConfig();
        switch (v.getId()) {

            case R.id.login_edit_text_email_address:
                if (passwordText.getText() != null
                        && !"".equals(passwordText.getText().toString().trim())
                        && passwordText.isInTouchMode()) {

                    if (!passwordValidator.validate(passwordText.getText().toString()).success) {
                        warningView.setText(passwordValidator.getValidateError());
                        return false;
                    }
                }
                break;

            case R.id.login_edit_text_password:
                if (emailText.getText() != null && !"".equals(emailText.getText().toString().trim())) {
                    if (!emailValidator.validate(emailText.getText().toString()).success) {
                        warningView.setText(emailValidator.getValidateError());
                        return false;
                    }
                } else {
                    warningView.setText("Email address can't be empty!");
                    return false;
                }
        }

        return false;
    }


    public void setDefaultConfig() {
        Color.changeViewColor(emailText, R.color.black);
        warningView.setText("");
    }

    private void login(View v) {
        updateInput();
        if (!emailValidator.validate(currentUser.getEmailAddress()).success
                || !passwordValidator.validate(currentUser.getPassword()).success) {
            loginFailedAction();
            return;
        }

        currentUser = login.login(currentUser);
        if (currentUser != null) {
            setDefaultConfig();
            navigateFromViewTo(v, R.id.action_loginFragment_to_mainActivity);
        } else {
            loginFailedAction();
        }


    }

    @SuppressLint("SetTextI18n")
    private void loginFailedAction() {
        passwordText.setText("");
        Color.changeViewColor(emailText, R.color.red);
        passwordText.startAnimation(shakeAnimation);
        emailText.startAnimation(shakeAnimation);
        warningView.setText("Email or password is wrong. Try again.");
        warningView.startAnimation(shakeAnimation);

    }

}