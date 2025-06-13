package com.example.serving_web_content.models;

public class ChangeUserDetailsDto {
    private String newUsername;
    private String newPassword;
    private String confirmNewPassword;
    private String newEmail;
    private Long targetUserId;
    private Long cityId;
    public String getNewUsername() {return newUsername;}
    public void setNewUsername(String newUsername) {this.newUsername = newUsername;}
    public String getNewPassword() {return newPassword;}
    public void setNewPassword(String newPassword) {this.newPassword = newPassword;}
    public String getConfirmNewPassword() {return confirmNewPassword;}
    public void setConfirmNewPassword(String confirmNewPassword) {this.confirmNewPassword = confirmNewPassword;}
    public String getEmail() {return newEmail;}
    public void setEmail(String newEmail) {this.newEmail = newEmail;}
    public Long getTargetUserId() {return targetUserId;}
    public void setTargetUserId(Long targetUserId) {this.targetUserId = targetUserId;}
    public Long getCityId() {return cityId;}
    public void setCityId(Long cityId) {this.cityId = cityId;}
}
