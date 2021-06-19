package com.example.omarket.backend.user;

import android.net.Uri;

import com.example.omarket.backend.Response;
import com.example.omarket.backend.handlers.exepstions.ExceptionChecker;
import com.example.omarket.backend.handlers.validators.EmailValidator;
import com.example.omarket.backend.handlers.validators.PasswordValidator;
import com.example.omarket.backend.handlers.validators.PhoneNumberValidator;
import com.example.omarket.backend.handlers.validators.Validator;

public class UserInformation {
    private String fullName;
    private String emailAddress;
    private String password;
    private String phoneNumber;
    private boolean isSuperAdmin;
    private boolean isAdmin;
    private Uri personPhoto;// TODO

    private static UserInformation currentLoginUser;


    private Validator[] validators;
    public Response creationResponse;

    UserInformation(Response creationResponse) {
        this.creationResponse = creationResponse;
    }

    UserInformation(String emailAddress, String password, boolean isAdmin, boolean isSuperAdmin) {
        setValidators();
        ExceptionChecker.notNullChecker(emailAddress, isAdmin, isSuperAdmin);
        this.emailAddress = emailAddress;
        this.isAdmin = isAdmin;
        this.isSuperAdmin = isSuperAdmin;
        this.password = password;
    }

    UserInformation(String fullName, String emailAddress, String password, String phoneNumber,
                boolean isAdmin, boolean isSuperAdmin) {
        ExceptionChecker.notNullChecker(fullName, emailAddress, password, phoneNumber, isAdmin, isSuperAdmin);
        setValidators();
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
        this.isSuperAdmin = isSuperAdmin;
    }
    UserInformation(String fullName,  String phoneNumber, boolean isAdmin, boolean isSuperAdmin, Uri personPhoto) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
        this.isSuperAdmin = isSuperAdmin;
        this.personPhoto = personPhoto;
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

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
    //

    public static UserInformation getCurrentLoginUser() {
        return currentLoginUser;
    }

    private void setValidators() {
        validators = new Validator[]{
                EmailValidator.getInstance(),
                PasswordValidator.getInstance(),
                PhoneNumberValidator.getInstance()
        };
    }

    public UserInformation createNewUser() {
        creationResponse = Response.successful();
        for (Validator validator :
                validators) {
            validator.validate(currentLoginUser);
            if (!validator.isValid()) {
                creationResponse.success = false;
                creationResponse.errors.add(validator.getValidateError());
            }
        }
        if (creationResponse.success)
            return this;
        else
            return new UserInformation(creationResponse);
    }
}
