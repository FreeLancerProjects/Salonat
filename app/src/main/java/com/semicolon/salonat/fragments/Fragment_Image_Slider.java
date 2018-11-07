package com.semicolon.salonat.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.salonat.R;
import com.semicolon.salonat.models.SalonModel;
import com.semicolon.salonat.tags.Tags;
import com.squareup.picasso.Picasso;

public class Fragment_Image_Slider extends Fragment {
    private static final String TAG = "data";
    private RoundedImageView image;
    private SalonModel.GalleryModel galleryModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_slider,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        image = view.findViewById(R.id.image);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            galleryModel = (SalonModel.GalleryModel) bundle.getSerializable(TAG);
            UpdateUI(galleryModel);

        }
    }

    private void UpdateUI(SalonModel.GalleryModel galleryModel) {
        Picasso.with(getActivity()).load(Uri.parse(Tags.IMAGE_URL+galleryModel.getPhoto_name())).into(image);
    }

    public static Fragment_Image_Slider getInstance(SalonModel.GalleryModel galleryModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,galleryModel);
        Fragment_Image_Slider fragment_image_slider = new Fragment_Image_Slider();
        fragment_image_slider.setArguments(bundle);
        return fragment_image_slider;
    }

}
