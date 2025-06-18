package com.example.serving_web_content.service.blog;


import com.example.serving_web_content.service.comments.CommentOperationResult;

public class blogOperationRresult {
    public boolean isSucces() {
        return this.status == Status.SUCCESS;
    }

    public boolean isSuccess() {
        return this.status == Status.SUCCESS;
    }


    public enum Status {
        SUCCESS,
        NOT_FOUND,
        OPERATION_NOT_ALLOWED,
        INVALID_DATA,
        UNKNOWN_ERROR
    }

    private final blogOperationRresult.Status status;
    private final String errorMessage;
    private final Long postIdForRedirect;
    public blogOperationRresult(blogOperationRresult.Status status,  Long postIdForRedirect,String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.postIdForRedirect = postIdForRedirect;
    }

    public static blogOperationRresult success(Long postIdForRedirect) {
        if (postIdForRedirect == null) {
            throw new IllegalArgumentException("postIdForRedirect must not be null for a successful result");
        }
        return new blogOperationRresult(blogOperationRresult.Status.SUCCESS, postIdForRedirect,null);
    }
    public static blogOperationRresult failure(blogOperationRresult.Status status, Long postIdIfKnown, String errorMessage) {
        if (status == blogOperationRresult.Status.SUCCESS) {
            throw new IllegalArgumentException("Cannot use failure() method for SUCCESS status");
        }
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = "Operation failed";
        }
        return new blogOperationRresult(status, postIdIfKnown, errorMessage);
    }

    public blogOperationRresult.Status getStatus() {
        return status;
    }
    public String getErrorMessage() {
        return errorMessage;
    }

}
