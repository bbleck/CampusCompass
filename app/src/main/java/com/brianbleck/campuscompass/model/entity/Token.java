package com.brianbleck.campuscompass.model.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.drawable.Drawable;

import com.brianbleck.campuscompass.model.TokenType;

import java.net.URL;

@Entity
public class Token implements Cloneable{


    @ColumnInfo(name = "token_id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "token_type", index = true)
    private TokenType tokenType;

    private double latitude;

    private double longitude;

    @Ignore
    private Drawable drawable;//image in open data is building

    private URL link;
    private URL image;

    @ColumnInfo(index = true, collate = ColumnInfo.NOCASE)
    private String title;

    private String description;


    @Override
    public Object clone() throws CloneNotSupportedException {
        Token theClone = new Token();
        theClone.setDescription(getDescription());
        theClone.setImage(getImage());
        theClone.setLink(getLink());
        theClone.setLatitude(getLatitude());
        theClone.setLongitude(getLongitude());
        theClone.setTitle(getTitle());
        theClone.setTokenType(getTokenType());
        theClone.setId(getId());
        theClone.setDrawable(getDrawable());
        return theClone;
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

    public URL getImage() {
        return image;
    }

    public void setImage(URL image) {
        this.image = image;
        //todo:  create drawable object for URL if it is non null
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

}
