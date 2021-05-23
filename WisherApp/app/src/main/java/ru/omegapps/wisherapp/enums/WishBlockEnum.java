package ru.omegapps.wisherapp.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public enum WishBlockEnum {
    WishBlockOne("Желаю всего самого наилучшего, {имя}!",  new ArrayList<>(Arrays.asList("bb", "любимому")),  new ArrayList<>(Arrays.asList("begin", "man", "woman"))),
    WishBlockTwo("Счастья вечной жизни.",  new ArrayList<>(Arrays.asList("bb", "любимому")),  new ArrayList<>(Arrays.asList("mid", "man", "woman"))),
    WishBlockThree("Отлично уник закончить!",  new ArrayList<>(Arrays.asList("bb", "любимому")),  new ArrayList<>(Arrays.asList("end", "man", "woman"))),
    WishBlockFour("Желаю здоровья родным.",  new ArrayList<>(Arrays.asList("bb", "любимому")),  new ArrayList<>(Arrays.asList("begin", "man", "woman", "unnamed"))),
    WishBlockFive("Чтобы все получалось.",  new ArrayList<>(Arrays.asList("bb", "любимому")),  new ArrayList<>(Arrays.asList("begin", "man", "woman", "unnamed")));

    private String text;
    private ArrayList<String> tags;
    private ArrayList<String> filters;

    public ArrayList<String> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<String> filters) {
        this.filters = filters;
    }

    WishBlockEnum(String text, ArrayList<String> tags, ArrayList<String> filters) {
        this.text = text;
        this.tags = tags;
        this.filters = filters;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
