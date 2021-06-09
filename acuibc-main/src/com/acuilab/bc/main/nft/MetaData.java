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
    // id是nft合约中的id   number是显示的id（读取的meta里的token_id）    
    // 这两个一般一样   但当一个合约中有超过1个系列的时候， 后续的系列要重新从1开始显示  就分开了
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
