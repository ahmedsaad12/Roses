package com.creativeshare.roses.activites_fragments.splash_activity.home_activity.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.creativeshare.roses.R;
import com.creativeshare.roses.activites_fragments.splash_activity.home_activity.activity.HomeActivity;
import com.creativeshare.roses.adapter.Catogries_Adapter;
import com.creativeshare.roses.adapter.Shop_Offers_Adapter;
import com.creativeshare.roses.models.Add_Order_Model;
import com.creativeshare.roses.models.Catogries_Model;
import com.creativeshare.roses.models.Offer_Model;
import com.creativeshare.roses.models.Product_Model;
import com.creativeshare.roses.models.Send_Data;
import com.creativeshare.roses.models.UserModel;
import com.creativeshare.roses.preferences.Preferences;
import com.creativeshare.roses.remote.Api;
import com.creativeshare.roses.share.Common;
import com.creativeshare.roses.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Shop_Offers extends Fragment {


    private HomeActivity activity;
    private Preferences preferences;
    private UserModel userModel;
    private Offer_Model.Data data;
    private ProgressBar progBar;
    private RecyclerView rec_depart;
    private List<Offer_Model.Data> dataList;
    private Shop_Offers_Adapter shop_offers_adapter;
    private GridLayoutManager gridLayoutManager;
    private LinearLayout ll_no_store;
    private boolean isLoading = false;
    private int current_page_depart = 1;
    private int market_id;
    private int quantity;
    private String desc;
    public static Fragment_Shop_Offers newInstance() {
        Fragment_Shop_Offers fragment_shop_offers = new Fragment_Shop_Offers();

        return fragment_shop_offers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_offers, container, false);
        initview(view);
        getDepartments();
        return view;
    }


    private void initview(View view) {
        dataList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        market_id = Send_Data.getMarket_id();

        progBar = view.findViewById(R.id.progBar2);
        ll_no_store = view.findViewById(R.id.ll_no_store);
        rec_depart = view.findViewById(R.id.rec_offers);

        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        progBar.setVisibility(View.GONE);
        rec_depart.setDrawingCacheEnabled(true);
        rec_depart.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rec_depart.setItemViewCacheSize(25);


        gridLayoutManager = new GridLayoutManager(activity, 2);
        rec_depart.setLayoutManager(gridLayoutManager);
        shop_offers_adapter = new Shop_Offers_Adapter(dataList, activity, this);
        rec_depart.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > 0) {
                    int total_item = shop_offers_adapter.getItemCount();
                    int last_item_pos = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                    //  Log.e("msg", total_item + "  " + last_item_pos);
                    if (last_item_pos >= (total_item - 10) && !isLoading) {
                        isLoading = true;
                        dataList.add(null);
                        shop_offers_adapter.notifyItemInserted(dataList.size() - 1);
                        int page = current_page_depart + 1;

                        loadMore(page);

                    }
                }
            }
        });

        rec_depart.setAdapter(shop_offers_adapter);
    }

    public void getDepartments() {
        //   Common.CloseKeyBoard(homeActivity, edt_name);
        progBar.setVisibility(View.VISIBLE);
        // rec_sent.setVisibility(View.GONE);
        Log.e("id", market_id + "");
        Api.getService(Tags.base_url)
                .getoffer(1, market_id)
                .enqueue(new Callback<Offer_Model>() {
                    @Override
                    public void onResponse(Call<Offer_Model> call, Response<Offer_Model> response) {
                        progBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            dataList.clear();
                            dataList.addAll(response.body().getData());
                            if (response.body().getData().size() > 0) {
                                // rec_sent.setVisibility(View.VISIBLE);
                                Log.e("data",response.body().getData().get(0).getAr_title());

                               ll_no_store.setVisibility(View.GONE);
                                shop_offers_adapter.notifyDataSetChanged();
                                //   total_page = response.body().getMeta().getLast_page();

                            } else {
                                  ll_no_store.setVisibility(View.VISIBLE);

                            }
                        } else {

                            Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Offer_Model> call, Throwable t) {
                        try {


                                Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });

    }

    private void loadMore(int page) {
        Api.getService(Tags.base_url)
                .getoffer(page, market_id)
                .enqueue(new Callback<Offer_Model>() {
                    @Override
                    public void onResponse(Call<Offer_Model> call, Response<Offer_Model> response) {
                        dataList.remove(dataList.size() - 1);
                        shop_offers_adapter.notifyItemRemoved(dataList.size() - 1);
                        isLoading = false;
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                            dataList.addAll(response.body().getData());
                            // categories.addAll(response.body().getCategories());
                            current_page_depart = response.body().getCurrent_page();
                            shop_offers_adapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Offer_Model> call, Throwable t) {
                        try {
                            dataList.remove(dataList.size() - 1);
                            shop_offers_adapter.notifyItemRemoved(dataList.size() - 1);
                            isLoading = false;
                            //    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }


    public void setproduct(Offer_Model.Data data) {
        this.data=data;
CreateSignAlertDialog(activity);
    }

    public void CreateSignAlertDialog(Context context) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(context).inflate(R.layout.cart_dialog, null);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_login_bg));
        Button doneBtn = view.findViewById(R.id.btn_add);
        EditText edt_quantity = view.findViewById(R.id.edt_quantity);
        EditText edt_cong = view.findViewById(R.id.edt_cong);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desc = edt_cong.getText().toString();
                String quant = edt_quantity.getText().toString();

                if (!TextUtils.isEmpty(desc) && !TextUtils.isEmpty(quant)) {
                    quantity = Integer.parseInt(quant);
                    addtocart();
                    dialog.dismiss();
                    Common.CreateSignAlertDialog(activity,getResources().getString(R.string.add_to_cart_suc));

                } else {
                    if (TextUtils.isEmpty(quant)) {
                        edt_quantity.setError(getResources().getString(R.string.field_req));
                    }
                    if (TextUtils.isEmpty(desc)) {
                        edt_cong.setError(getResources().getString(R.string.field_req));
                    }
                }
            }
        });

        //dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        // dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }

    private void addtocart() {
        Add_Order_Model add_order_model;
        List<Add_Order_Model.Order_details> order_details;
        if (preferences.getUserOrder(activity) != null) {
            add_order_model = preferences.getUserOrder(activity);
        } else {
            add_order_model = new Add_Order_Model();
        }


            add_order_model.setMarket_id(data.getMarket_id());
            if (add_order_model.getOrder_details() != null) {
                order_details = add_order_model.getOrder_details();
            } else {
                order_details = new ArrayList<>();
            }
            Add_Order_Model.Order_details order_details1 = new Add_Order_Model.Order_details();
            order_details1.setAmount(quantity);
            order_details1.setDes(desc);
            order_details1.setProduct_id(data.getProduct_id());
            order_details1.setTotal_price(quantity*(Double.parseDouble(data.getProduct_price())-( ((Double.parseDouble(data.getProduct_price())*data.getValue())/100))));
           // order_details.add(order_details1);
            order_details1.setOffer_id(data.getId());
            order_details1.setImage(data.getImage());
            order_details1.setAr_name(data.getProduct_ar_title());
            order_details1.setEn_name(data.getProduct_en_title());
order_details.add(order_details1);
add_order_model.setOrder_details(order_details);
            preferences.create_update_order(activity, add_order_model);

    }
}
