package com.example.serving_web_content.service.user;

public class UserOperationResult {

    private final String errorMessage;
    private final Long userID;
    private final Status status;

    private UserOperationResult(Status status, String errorMessage, Long userID) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.userID = userID;
    }

    public enum Status {
        SUCCESS,
        NOT_FOUND,
        OPERATION_NOT_ALLOWED,
        INVALID_DATA,
        USER_ALREADY_EXISTS,
        AUTHENTICATION_FAILED,
        UNKNOWN_ERROR
    }

    public static UserOperationResult success(Long userId) {
        return new UserOperationResult(Status.SUCCESS, null, userId);
    }

    public static UserOperationResult success() {
        return new UserOperationResult(Status.SUCCESS, null, null);
    }

    public static UserOperationResult failure(Status status, Long userID, String errorMessage) {
        if (status == Status.SUCCESS) {
            throw new IllegalArgumentException("Use success() factory method for success status.");
        }
        return new UserOperationResult(status, errorMessage, userID);
    }

    public static UserOperationResult failure(Status status, String errorMessage) {
        if (status == Status.SUCCESS) {
            throw new IllegalArgumentException("Use success() factory method for success status.");
        }
        return new UserOperationResult(status, errorMessage, null);
    }

    public Status getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return this.status == Status.SUCCESS;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Long getUserID() {
        return userID;
    }
}
