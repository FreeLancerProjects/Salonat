package com.semicolon.salonat.models;

import java.io.Serializable;

public class VoteModel implements Serializable {
    private String comment_person;
    private String comment_detail;
    private String stars_num;

    public String getComment_person() {
        return comment_person;
    }

    public String getComment_detail() {
        return comment_detail;
    }

    public String getStars_num() {
        return stars_num;
    }
}
