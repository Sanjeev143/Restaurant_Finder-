package com.anorakstech.sanjeev.restauranttrace;

import java.io.Serializable;

/**
 * Created by RajatSharma on 22-03-2016.
 */
public class Restaurant implements Serializable{


    private int id;
    private String name;
    private String email;
    private String phone;
    private String street;
    private String zip;
    private byte[] image;
    private String cuisine;


    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }


    //getters


    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStreet() {
        return street;
    }

    public String getZip() {
        return zip;
    }

    public byte[] getImage() {
        return image;
    }

    //setters
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
