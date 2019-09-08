package com.creativeshare.roses.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.creativeshare.roses.R;
import com.creativeshare.roses.activites_fragments.splash_activity.home_activity.activity.HomeActivity;
import com.creativeshare.roses.models.Markets_Model;
import com.creativeshare.roses.tags.Tags;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Infowindows_Adapter  implements GoogleMap.InfoWindowAdapter {
private String current_lang;
    private Activity context;
    private HomeActivity activity;
List<Markets_Model.Data> mDataList;
    public Infowindows_Adapter(Activity context,List<Markets_Model.Data> mDataList){
        this.context = context;
        this.mDataList=mDataList;
        Paper.init(context);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
activity=(HomeActivity)context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        View view ;
        if(current_lang.equals("ar")){
          view  = context.getLayoutInflater().inflate(R.layout.info_window_map_ar, null);
        }
        else {
         view= context.getLayoutInflater().inflate(R.layout.info_window_map_en, null);
        }
        TextView infotv1 =  view.findViewById(R.id.tv1);
        TextView infotv4 =  view.findViewById(R.id.tv4);
        TextView infoTitle =  view.findViewById(R.id.tv_title);
        TextView infoaddress =  view.findViewById(R.id.tv_address);
        ImageView infim=view.findViewById(R.id.im1);
        Button bt_details=view.findViewById(R.id.btn_detilas);
        Button bt_cancell=view.findViewById(R.id.btn_cancel);
        infotv1.setText(context.getResources().getString(R.string.store_name)+":");
        infotv4.setText(context.getResources().getString(R.string.address)+":");
        infoTitle.setText(mDataList.get((Integer) marker.getTag()).getName());
        infoaddress.setText(mDataList.get((Integer) marker.getTag()).getAddress());
        Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+mDataList.get((Integer)marker.getTag()).getLogo())).fit().placeholder(R.drawable.logo).into(infim);
bt_details.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        activity.DisplayFragmentShopprofile(mDataList.get((Integer) marker.getTag()).getId());
    }
});
bt_cancell.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

    }
});
        return view;
    }
}