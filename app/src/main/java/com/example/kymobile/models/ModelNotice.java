package com.example.kymobile.models;

public class ModelNotice {
    String Title,Body,Exco;

    public ModelNotice(){

    }

    public ModelNotice(String Title, String Body, String Exco) {
        this.Title = Title;
        this.Body = Body;
        this.Exco = Exco;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getExco() {
        return Exco;
    }

    public void setExco(String exco) {
        Exco = exco;
    }
}
