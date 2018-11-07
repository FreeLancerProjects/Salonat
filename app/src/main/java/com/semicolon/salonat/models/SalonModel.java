package com.semicolon.salonat.models;

import java.io.Serializable;
import java.util.List;

public class SalonModel implements Serializable {
    private String id_salon;
    private String title;
    private String main_photo;
    private String details;
    private String address;
    private String address_google_lat;
    private String address_google_long;
    private String city;
    private String phone;
    private String mobile;
    private String email;
    private String evaluation_count;
    private String clients_count;
    private int salon_stars_num;
    private List<GalleryModel> gallary;

    public String getId_salon() {
        return id_salon;
    }

    public String getTitle() {
        return title;
    }

    public String getMain_photo() {
        return main_photo;
    }

    public String getDetails() {
        return details;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress_google_lat() {
        return address_google_lat;
    }

    public String getAddress_google_long() {
        return address_google_long;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getEvaluation_count() {
        return evaluation_count;
    }

    public String getClients_count() {
        return clients_count;
    }

    public int getSalon_stars_num() {
        return salon_stars_num;
    }

    public List<GalleryModel> getGallary() {
        return gallary;
    }

    public class GalleryModel implements Serializable
    {
        private String photo_name;
        private String id_photo;

        public String getPhoto_name() {
            return photo_name;
        }

        public String getId_photo() {
            return id_photo;
        }
    }
}
