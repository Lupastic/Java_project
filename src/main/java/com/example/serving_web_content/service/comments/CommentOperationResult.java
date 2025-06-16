package com.example.serving_web_content.service.comments;

public class CommentOperationResult {

    public enum Status {
        SUCCESS,
        NOT_FOUND,
        OPERATION_NOT_ALLOWED,
        INVALID_DATA,
        UNKNOWN_ERROR
    }

    private final Status status;
    private final String errorMessage;
    private final Long postIdForRedirect;

    private CommentOperationResult(Status status, Long postIdForRedirect, String errorMessage) {
        this.status = status;
        this.postIdForRedirect = postIdForRedirect;
        this.errorMessage = errorMessage;
    }

    public static CommentOperationResult success(Long postIdForRedirect) {
        if (postIdForRedirect == null) {
            throw new IllegalArgumentException("postIdForRedirect must not be null for a successful result");
        }
        return new CommentOperationResult(Status.SUCCESS, postIdForRedirect, null);
    }

    public static CommentOperationResult failure(Status status, String errorMessage) {
        if (status == Status.SUCCESS) {
            throw new IllegalArgumentException("Cannot use failure() method for SUCCESS status");
        }
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = "Operation failed";
        }
        return new CommentOperationResult(status, null, errorMessage);
    }

    public static CommentOperationResult failure(Status status, Long postIdIfKnown, String errorMessage) {
        if (status == Status.SUCCESS) {
            throw new IllegalArgumentException("Cannot use failure() method for SUCCESS status");
        }
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = "Operation failed";
        }
        return new CommentOperationResult(status, postIdIfKnown, errorMessage);
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

    public Long getPostIdForRedirect() {
        return postIdForRedirect;
    }

    @Override
    public String toString() {
        return "CommentOperationResult{" +
                "status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                ", postIdForRedirect=" + postIdForRedirect +
                '}';
    }
}
