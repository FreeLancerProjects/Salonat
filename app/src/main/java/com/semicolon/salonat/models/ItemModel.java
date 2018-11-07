package com.semicolon.salonat.models;

import java.io.Serializable;
import java.util.List;

public class ItemModel implements Serializable {
    private String salon_id;
    private String salon_main_photo;
    private String salon_name;
    private String salon_rate;
    private String from;
    private List<SalonModel.GalleryModel> galleryModelList;
    private List<com.semicolon.salonat.models.ServiceModel.Sub_Service> serviceModelList;
    private String reservation_cost;

    public ItemModel(String salon_id, String salon_main_photo, String salon_name, String salon_rate, List<SalonModel.GalleryModel> galleryModelList, List<com.semicolon.salonat.models.ServiceModel.Sub_Service> serviceModelList, String reservation_cost,String from) {
        this.salon_id = salon_id;
        this.salon_main_photo = salon_main_photo;
        this.salon_name = salon_name;
        this.salon_rate = salon_rate;
        this.from = from;
        this.galleryModelList = galleryModelList;
        this.serviceModelList = serviceModelList;
        this.reservation_cost = reservation_cost;
    }

    public String getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(String salon_id) {
        this.salon_id = salon_id;
    }

    public String getSalon_main_photo() {
        return salon_main_photo;
    }

    public void setSalon_main_photo(String salon_main_photo) {
        this.salon_main_photo = salon_main_photo;
    }

    public String getSalon_name() {
        return salon_name;
    }

    public void setSalon_name(String salon_name) {
        this.salon_name = salon_name;
    }

    public String getSalon_rate() {
        return salon_rate;
    }

    public void setSalon_rate(String salon_rate) {
        this.salon_rate = salon_rate;
    }

    public List<SalonModel.GalleryModel> getGalleryModelList() {
        return galleryModelList;
    }

    public void setGalleryModelList(List<SalonModel.GalleryModel> galleryModelList) {
        this.galleryModelList = galleryModelList;
    }

    public List<com.semicolon.salonat.models.ServiceModel.Sub_Service> getServiceModelList() {
        return serviceModelList;
    }

    public void setServiceModelList(List<com.semicolon.salonat.models.ServiceModel.Sub_Service> serviceModelList) {
        this.serviceModelList = serviceModelList;
    }

    public String getReservation_cost() {
        return reservation_cost;
    }

    public void setReservation_cost(String reservation_cost) {
        this.reservation_cost = reservation_cost;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void AddItem(ItemModel itemModel, com.semicolon.salonat.models.ServiceModel.Sub_Service subService)
    {

        itemModel.getServiceModelList().add(subService);
    }

    public void RemoveItem(ItemModel itemModel, com.semicolon.salonat.models.ServiceModel.Sub_Service serviceModel)
    {
        for (int i =0;i<itemModel.getServiceModelList().size();i++)
        {
            if (itemModel.getServiceModelList().get(i).getId_service().equals(serviceModel.getId_service()))
            {
                itemModel.getServiceModelList().remove(i);
                break;
            }
        }
    }

    public void Clear()
    {
        this.setSalon_id(null);
        this.setSalon_name(null);
        this.setReservation_cost(null);
        this.setFrom(null);
        this.setSalon_rate(null);
        this.setSalon_main_photo(null);
        this.setGalleryModelList(null);
        this.setServiceModelList(null);
    }
}
