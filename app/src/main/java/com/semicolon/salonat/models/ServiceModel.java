package com.semicolon.salonat.models;

import java.io.Serializable;
import java.util.List;

public class ServiceModel implements Serializable {

    private String categories_id_fk;
    private String category_title;
    private String service_type;
    private List<Sub_Service> sub_service;

    public String getCategories_id_fk() {
        return categories_id_fk;
    }

    public String getCategory_title() {
        return category_title;
    }

    public String getService_type() {
        return service_type;
    }

    public List<Sub_Service> getSub_service() {
        return sub_service;
    }

    public class Sub_Service implements Serializable
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
        private int state_checked;

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

        public int getState_checked() {
            return state_checked;
        }
    }

}
