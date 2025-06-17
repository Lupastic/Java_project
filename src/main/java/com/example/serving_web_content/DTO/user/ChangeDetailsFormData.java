package com.example.serving_web_content.DTO.user;
import com.example.serving_web_content.models.City;

public class ChangeDetailsFormData {

    private ChangeUserDetailsDto changeDetailsDto;
    private Long targetUserId;
    private String targetUsername;
    private boolean changingOtherUser;
    private boolean isCurrentUserAdmin;
    private Iterable<City> cities;
    private String errorMessage;

    public ChangeDetailsFormData() {
    }
    public ChangeUserDetailsDto getChangeDetailsDto() {
        return changeDetailsDto;
    }

    public void setChangeDetailsDto(ChangeUserDetailsDto changeDetailsDto) {
        this.changeDetailsDto = changeDetailsDto;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public boolean isChangingOtherUser() {
        return changingOtherUser;
    }

    public void setChangingOtherUser(boolean changingOtherUser) {
        this.changingOtherUser = changingOtherUser;
    }

    public boolean isCurrentUserAdmin() {
        return isCurrentUserAdmin;
    }

    public void setCurrentUserAdmin(boolean currentUserAdmin) {
        isCurrentUserAdmin = currentUserAdmin;
    }

    public Iterable<City> getCities() {
        return cities;
    }

    public void setCities(Iterable<City> cities) {
        this.cities = cities;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Удобный метод для проверки, была ли ошибка
    public boolean hasError() {
        return this.errorMessage != null && !this.errorMessage.isBlank();
    }
}