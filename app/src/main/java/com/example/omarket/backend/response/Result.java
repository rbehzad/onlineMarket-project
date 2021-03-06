package com.example.omarket.backend.response;

// Result.java
public abstract class Result<T> {
    private Result() {}

    public static final class Success<T> extends Result<T> {
        public T data;

        public Success(T data) {
            this.data = data;
        }
    }

    public static final class Error<T> extends Result<T> {
        public Exception exception;
        public T data ;
        public Error(T data) {
            this.data = data;
        }
    }
}