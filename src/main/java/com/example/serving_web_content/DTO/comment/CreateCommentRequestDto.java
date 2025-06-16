package com.example.serving_web_content.DTO.comment;

public class CreateCommentRequestDto {
    private Long postID;
    private String comment;
    public  Long getPostID() {
        return postID;
    }
    public void setPostID(Long postID) {
        this.postID = postID;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
