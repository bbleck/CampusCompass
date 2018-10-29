package com.brianbleck.campuscompass.model;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;

import com.brianbleck.campuscompass.R;

import java.net.URL;
import java.util.Random;

public class Token {


    private TokenType tokenType;
    private double latitude;
    private double longitude;
    private Drawable image;
    private String link;
    private String title;
    private String description;
    private String lowercaseTitle;
    private Activity parent;

    public Token(@NonNull TokenType tokenType, @NonNull double latitude, @NonNull double longitude, Drawable image, String link, @NonNull String title, String description, Activity parent) {
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

    //constructor that should only be used to create testing objects
    public Token (TokenType tokenType, double latitude, double longitude, String title, Activity parent){
        this.tokenType = tokenType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.image = ResourcesCompat.getDrawable(parent.getResources(), R.drawable.ic_building_blue_24dp, null);
        this.description = "This building is a building.\nIt has lots of buildingy sort of things in it.\nA man designed it.\nMen built it.\nNow people use it for stuff.\nTravel here at your own peril.\nThere have been sightings of pirates and dwarves.\nDwarves delve too deeply and, in the dark, uncover things that would be best left where they were sealed away long ago.\nTry telling that to dwarves though, I mean seriously.\n\n\n\n\n\n\n\n\n\n\nD\nw\na\nr\nv\ne\ns\n \nw\ni\nl\nl\n \nnot\nlisten.";
        this.link = "A link will eventually go here.";
        this.parent = parent;
    }

    public Token(Token tokenToCopy){
        this.tokenType = tokenToCopy.getTokenType();
        this.latitude = tokenToCopy.getLatitude();
        this.longitude = tokenToCopy.getLongitude();
        this.title = tokenToCopy.getTitle();
        this.parent = tokenToCopy.getParent();
        this.image = ResourcesCompat.getDrawable(parent.getResources(), R.drawable.ic_building_blue_24dp, null);
        this.description = "This building is a building.\nIt has lots of buildingy sort of things in it.\nA man designed it.\nMen built it.\nNow people use it for stuff.\nTravel here at your own peril.\nThere have been sightings of pirates and dwarves.\nDwarves delve too deeply and, in the dark, uncover things that would be best left where they were sealed away long ago.\nTry telling that to dwarves though, I mean seriously.\n\n\n\n\n\n\n\n\n\n\nD\nw\na\nr\nv\ne\ns\n \nw\ni\nl\nl\n \nnot\nlisten.";
        this.link = "A link will eventually go here.";
    }

    public static Token createTestToken(Activity parent){
        Random rand = new Random();
        String tempTitle = "Building #" + rand.nextInt(1000);
        return new Token(TokenType.BUILDING, 0.0, 0.0, tempTitle, parent);
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
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

    public Activity getParent() {
        return parent;
    }
}
