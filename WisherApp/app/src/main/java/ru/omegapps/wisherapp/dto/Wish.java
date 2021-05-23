package ru.omegapps.wisherapp.dto;

import ru.omegapps.wisherapp.enums.WishEnum;

public class Wish {

    private String title;
    private String text;
    private String uuid;

    public Wish(){}

    public Wish(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Wish(WishEnum wishEnum){
        this.text = wishEnum.getText();
        this.title = wishEnum.getTitle();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

