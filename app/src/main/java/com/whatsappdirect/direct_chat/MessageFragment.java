package com.whatsappdirect.direct_chat;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.whatsappdirect.direct_chat.Activity.MainActivity;
import com.whatsappdirect.direct_chat.Adapter.MessageAdapter;
import com.whatsappdirect.direct_chat.Utils.AdapterCallback;
import com.whatsappdirect.direct_chat.Utils.SMSModel;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements AdapterCallback {

    private RecyclerView recycleview_message;
    ArrayList<SMSModel> msmsItems = new ArrayList<>();

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    public static final int REQUEST_READ_CONTACTS = 79;
    Cursor curLog;
    int callType;
    private AdapterCallback callback;
    private int REQUEST_FOR_ACTIVITY_CODE = 55;
    String phone;
    ProgressDialog progressDialog;
    private InterstitialAd mInterstitialAd;

    final String appPackageName = "com.whatsappdirect.direct_chat";

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Loading..");
//        progressDialog.show();
        //askPermission();
        recycleview_message = view.findViewById(R.id.recycleview_message);
        recycleview_message.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recycleview_message.setLayoutManager(mLayoutManager);
        Log.e("MESSAGE", "--msmsItems--0000" + msmsItems.size());
        mAdapter = new MessageAdapter(getActivity(), msmsItems, this);
        recycleview_message.setAdapter(mAdapter);
        getAllSms();
        return view;
    }

    private void getAllSms() {

        SMSModel objSms;
        Uri message = Uri.parse("content://sms/");
        //Uri.parse("content://sms/inbox");

        Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(message, null, null, null, null);

        msmsItems.clear();

        if (cursor != null)
            if (cursor.moveToFirst()) {
                do {
                    try {
                        objSms = new SMSModel();
                        objSms.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                        objSms.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                        objSms.setMsg(cursor.getString(cursor.getColumnIndexOrThrow("body")));
                        objSms.setReadState(cursor.getString(cursor.getColumnIndex("read")));
                        objSms.setTime(cursor.getString(cursor.getColumnIndexOrThrow("date")));

                        if (cursor.getString(cursor.getColumnIndexOrThrow("type")).contains("1")) {
                            objSms.setFolderName("inbox");
                        } else {
                            objSms.setFolderName("sent");
                        }
                        msmsItems.add(objSms);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }


                } while (cursor.moveToNext());
            }


      /*  assert cursor != null;
        if (cursor.moveToFirst()) { // must check the result to prevent exception

            for (int i = 0; i < cursor.getColumnCount(); i++) {

                try {
                    objSms = new SMSModel();
                    objSms.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    objSms.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                    objSms.setMsg(cursor.getString(cursor.getColumnIndexOrThrow("body")));
                    objSms.setReadState(cursor.getString(cursor.getColumnIndex("read")));
                    objSms.setTime(cursor.getString(cursor.getColumnIndexOrThrow("date")));

                    if (cursor.getString(cursor.getColumnIndexOrThrow("type")).contains("1")) {
                        objSms.setFolderName("inbox");
                    } else {
                        objSms.setFolderName("sent");
                    }
                    msmsItems.add(objSms);
                    cursor.moveToNext();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                // use msgData
            }

           *//* recycleview_message.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycleview_message.setLayoutManager(mLayoutManager);

            Log.e("MESSAGE", "--msmsItems--0000" +msmsItems.size());
            mAdapter = new MessageAdapter(getActivity(), msmsItems);
            recycleview_message.setAdapter(mAdapter);*//*
        }*/
        assert cursor != null;
        cursor.close();

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPositionClicked(String msgnumber) {

        phone = msgnumber;
        setInterstitialAd();
        // isNumeric(phone);
        /*Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("selectNumber", phone);
        getActivity().setResult(REQUEST_FOR_ACTIVITY_CODE, intent);
        getActivity().finish();
        Log.e("SELECT", "-----phoneNUmber-msg--11--" + phone);*/
    }

    public static boolean isNumeric(String str) {
        try {
            int msgphone = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("RESUME", "---msg--resume----");
    }

    private void setInterstitialAd() {

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("YOUR_TEST_DEVICE_ID").build();
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial_Ad));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                //Ads loaded
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                //Ads closed
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("selectNumber", phone);
                getActivity().setResult(REQUEST_FOR_ACTIVITY_CODE, intent);
                getActivity().finish();
                Log.e("SELECT", "-----phoneNUmber-msg--11--" + phone);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                //Ads couldn't loaded
                // Toast.makeText(mcontext, "Please Check Network connection", Toast.LENGTH_LONG).show();
            }
        });
        mInterstitialAd.loadAd(adRequest);
    }
}
