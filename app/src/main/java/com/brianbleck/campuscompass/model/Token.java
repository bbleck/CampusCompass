package com.brianbleck.campuscompass.model;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.brianbleck.campuscompass.R;

import java.net.URL;

public class Token {


    private TokenType tokenType;
    private double latitude;
    private double longitude;
    private Drawable image;
    private URL link;
    private String title;
    private String description;
    private String lowercaseTitle;
    private Activity parent;

    public Token(@NonNull TokenType tokenType, @NonNull double latitude, @NonNull double longitude, Drawable image, URL link, @NonNull String title, String description, Activity parent) {
        this.parent = parent;
        this.tokenType = tokenType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.lowercaseTitle = title.toLowerCase();
        if(image!=null){
            this.image = image;
        }else{
            setDefaultImage();
        }
        if(link!=null){
            this.link = link;
        }else{
            setDefaultLink();
        }
        if(description!=null){
            this.description = description;
        }else{
            this.description = parent.getString(R.string.no_description);
        }


    }

    private void setDefaultLink() {
        //todo: implement method
    }

    private void setDefaultImage() {
        //todo: implement method
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLowercaseTitle() {
        return lowercaseTitle;
    }

    public void setLowercaseTitle(String lowercaseTitle) {
        this.lowercaseTitle = lowercaseTitle;
    }
}
