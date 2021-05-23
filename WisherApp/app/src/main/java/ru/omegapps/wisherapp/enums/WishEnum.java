package ru.omegapps.wisherapp.enums;

public enum WishEnum {
    WishOne("Мария, 20/12/2021", "Поздравляю с ДР! Nennenenenene nen enn en en enne nen en n ennenene en n"),
    WishTwo("Эльвина, 13/07/2021", "Тоже поздравляю с ДР"),
    WishThree("Регина, 15/07/2021", "Ну и тебя поздравляю с ДР");

    WishEnum(String title, String text){
        this.title = title;
        this.text = text;
    }

    private String title;
    private String text;

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
}
