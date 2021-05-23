package ru.omegapps.wisherapp.dto;

import java.util.ArrayList;

import ru.omegapps.wisherapp.enums.WishBlockEnum;

public class WishBlock {
    private String userUuid;
    private String uuid;
    private boolean isPublic;
    private String wishText;
    private ArrayList<String> tags;
    private ArrayList<String> filters;

    public ArrayList<String> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<String> filters) {
        this.filters = filters;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public WishBlock(String wishText, ArrayList<String> tags) {
        this.wishText = wishText;
        this.tags = tags;
    }

    public WishBlock(){}

    public String getWishText() {
        return wishText;
    }

    public void setWishText(String wishText) {
        this.wishText = wishText;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public WishBlock(WishBlockEnum wishBlockEnum){
        this.tags = wishBlockEnum.getTags();
        this.wishText = wishBlockEnum.getText();
        this.filters = wishBlockEnum.getFilters();
        this.isPublic = false;
    }

    public WishBlock(String uuid, String wishText, ArrayList<String> tags, ArrayList<String> filters) {
        this.uuid = uuid;
        this.wishText = wishText;
        this.tags = tags;
        this.filters = filters;
    }

    public WishBlock(String uuid, String userUuid, String wishText, ArrayList<String> tags, ArrayList<String> filters, boolean isPublic) {
        this.uuid = uuid;
        this.userUuid = userUuid;
        this.wishText = wishText;
        this.tags = tags;
        this.filters = filters;
        this.isPublic = isPublic;
    }
}
