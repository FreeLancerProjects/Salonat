package com.semicolon.salonat.models;

import java.io.Serializable;
import java.util.List;

public class MyReservationModel implements Serializable {
    private String id_reservation;
    private String salon_id_fk;
    private String reservation_cost;
    private String reservation_date_st;
    private String reservation_time;
    private String approved;
    private String approved_date;
    private String title;
    private String main_photo;
    private String details;
    private String address;
    private String address_google_lat;
    private String address_google_long;
    private String phone;
    private List<Service> reservation_sevice;

    public String getId_reservation() {
        return id_reservation;
    }

    public String getSalon_id_fk() {
        return salon_id_fk;
    }

    public String getReservation_cost() {
        return reservation_cost;
    }

    public String getReservation_date_st() {
        return reservation_date_st;
    }

    public String getReservation_time() {
        return reservation_time;
    }

    public String getApproved() {
        return approved;
    }

    public String getApproved_date() {
        return approved_date;
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

    public String getPhone() {
        return phone;
    }

    public List<Service> getReservation_sevice() {
        return reservation_sevice;
    }

    public class Service implements Serializable
    {
        private String id_service;
        private String service_title;
        private String categories_id_fk;
        private String salon_id_fk;
        private String service_type;
        private String service_details;
        private String home_cost;
        private String salon_cost;
        private String home_time_take;
        private String salon_time_take;

        public String getId_service() {
            return id_service;
        }

        public String getService_title() {
            return service_title;
        }

        public String getCategories_id_fk() {
            return categories_id_fk;
        }

        public String getSalon_id_fk() {
            return salon_id_fk;
        }

        public String getService_type() {
            return service_type;
        }

        public String getService_details() {
            return service_details;
        }

        public String getHome_cost() {
            return home_cost;
        }

        public String getSalon_cost() {
            return salon_cost;
        }

        public String getHome_time_take() {
            return home_time_take;
        }

        public String getSalon_time_take() {
            return salon_time_take;
        }

    }

}
