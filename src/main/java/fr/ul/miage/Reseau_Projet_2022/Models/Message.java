package fr.ul.miage.Reseau_Projet_2022.Models;

public class Message {
    private String content;

    @Override
    public String toString() {
        return content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
