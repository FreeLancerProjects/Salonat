package com.semicolon.salonat.models;

import java.io.Serializable;

public class ResponsModel implements Serializable {
    private  int success_contact;
    private int success_logout;
    private int success_location;
    private int success_rest;
    private int success_token_id;
    private int success_resevation;
    private int success_read;
    private int success_transformation;

    public int getSuccess_contact() {
        return success_contact;
    }

    public int getSuccess_logout() {
        return success_logout;
    }

    public int getSuccess_location() {
        return success_location;
    }

    public int getSuccess_rest() {
        return success_rest;
    }

    public int getSuccess_token_id() {
        return success_token_id;
    }

    public int getSuccess_resevation() {
        return success_resevation;
    }

    public int getSuccess_read() {
        return success_read;
    }

    public int getSuccess_transformation() {
        return success_transformation;
    }
}
