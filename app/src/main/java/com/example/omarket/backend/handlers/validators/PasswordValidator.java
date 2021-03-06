package com.example.omarket.backend.handlers.validators;

import com.example.omarket.backend.response.Response;
import com.example.omarket.backend.response.ResponseErrorType;
import com.example.omarket.backend.user.User;

/**
 * singleton class.
 */
public class PasswordValidator implements ValidatorInterface {

    private static PasswordValidator passwordValidator;

    public final ResponseErrorType validatorType = ResponseErrorType.PASSWORD_VALIDATE;
    private boolean isValid;
    private String error;

    Response response;

    private PasswordValidator() {
        passwordValidator = this;
    }

    @Override
    public Response validate(User user) {
        response = validate(user.getPassword());
        if (!response.success) {
            User newUser = null;
            try {
                newUser = (User) user.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            newUser.creationResponse = response;
        }
        return response;
    }

    public Response validate(String password) {
        error = "Invalid password!";
        return Response.setErrors(ResponseErrorType.PASSWORD_VALIDATE, error);
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public String getValidateError() {
        return error;
    }

    @Override
    public ResponseErrorType getValidatorType() {
        return validatorType;
    }

    public static PasswordValidator getInstance() {
        if (passwordValidator != null)
            return passwordValidator;
        return new PasswordValidator();
    }


}
