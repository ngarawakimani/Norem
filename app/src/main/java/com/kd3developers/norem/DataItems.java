package com.kd3developers.norem;

/**
 * Created by wa kimani on 11/6/2016.
 */

public class DataItems {

    String image;
    String heading;
    String description;
    String price;

    private String bed_space;

    public DataItems(String image, String heading, String description, String price, String bed_space) {
        this.image = image;
        this.heading = heading;
        this.description = description;
        this.price = price;
        this.bed_space = bed_space;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getBed_space() {
        return bed_space;
    }

    public void setBed_space(String bed_space) {
        this.bed_space = bed_space;
    }
}

