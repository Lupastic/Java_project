package com.example.serving_web_content.DTO.comment;

public class UpdateCommentRequestDTO {
    private Long commentID;
    private String comment;
    public Long getCommentID() {
        return commentID;
    }
    public void setCommentID(Long commentID) {
        this.commentID = commentID;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
