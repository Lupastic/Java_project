package com.example.serving_web_content.DTO.blog;

public class UpdateBlogRequestDto {
    private Long postID;
    private String title;
    private String anons;
    private String text;
    public Long getPostID() {
        return postID;
    }
    public void setPostID(Long postID) {
        this.postID = postID;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAnons() {
        return anons;
    }
    public void setAnons(String anons) {
        this.anons = anons;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}

