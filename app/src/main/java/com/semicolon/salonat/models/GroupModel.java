package com.semicolon.salonat.models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class GroupModel extends ExpandableGroup {

    public GroupModel(String title, List<ServiceModel.Sub_Service> items) {
        super(title, items);
    }
}
