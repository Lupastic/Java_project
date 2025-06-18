package com.example.serving_web_content.DTO.blog;

public class CreateBlogRequestDto {
    private Long userID;
    private String title;
    private String anons;
    private String text;
    public Long getUserID() {
        return userID;
    }
    public void setUserID(Long userID) {
        this.userID = userID;
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
