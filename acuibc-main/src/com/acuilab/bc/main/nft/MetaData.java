package com.acuilab.bc.main.nft;

import java.awt.Image;

/**
 *
 * @author admin
 */
public class MetaData {
    private String id;
    private String name;
    private String platform;
    private String imageUrl;
    private String desc;
    private Image image;
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Image getImage() {
	return image;
    }

    public void setImage(Image image) {
	this.image =image;
    }
    
    public String getDesc() {
	return desc;
    }

    public void setDesc(String desc) {
	this.desc = desc;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getPlatform() {
	return platform;
    }

    public void setPlatform(String platform) {
	this.platform = platform;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }
    
    
}
