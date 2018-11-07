package com.semicolon.salonat.singletone;

import android.util.Log;

import com.semicolon.salonat.models.ItemModel;

public class ItemSingleTone {
    
    private static ItemSingleTone instance = null;
    private static ItemModel itemModel = null;

    private ItemSingleTone() {
    }

    public static synchronized ItemSingleTone getInstance()
    {
        if (instance==null)
        {
            instance = new ItemSingleTone();
        }
        return instance;
    }

    public void setItemModel(ItemModel itemModel)
    {
        this.itemModel = itemModel;
    }

    public ItemModel getItemModel() {
        return this.itemModel;
    }

    public void Clear(ItemModel itemModel)
    {
        if (itemModel!=null)
        {
            Log.e("clear1","clear1");

            if (itemModel.getServiceModelList().size()>0)
            {
                itemModel.getServiceModelList().clear();

            }
            this.itemModel=null;
            this.setItemModel(this.getItemModel());

        }else
            {
                Log.e("clear2","clear2");
            }


    }
}
