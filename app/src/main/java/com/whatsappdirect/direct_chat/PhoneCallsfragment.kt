package com.whatsappdirect.direct_chat

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.whatsappdirect.direct_chat.Activity.MainActivity
import com.whatsappdirect.direct_chat.Adapter.CallListAdapter
import com.whatsappdirect.direct_chat.Utils.AdapterCallback
import com.whatsappdirect.direct_chat.Utils.CallLogHelper
import com.whatsappdirect.direct_chat.Utils.CallLogItem
import java.text.SimpleDateFormat
import java.util.*

class PhoneCallsfragment : Fragment(), AdapterCallback {
    private var recycleview_call_log: RecyclerView? = null
    var mcallItems = ArrayList<CallLogItem>()
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    var curLog: Cursor? = null
    var callType = 0
    private val callback: AdapterCallback? = null
    private val REQUEST_FOR_ACTIVITY_CODE = 55
    var phone: String? = null
    var progressDialog: ProgressDialog? = null
    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_phone_callsfragment, container, false)

//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Loading..");
//        progressDialog.show();
        //askPermission();
        recycleview_call_log = view.findViewById(R.id.recycleview_call_log)
        curLog = CallLogHelper.getAllCallLogs(activity!!.contentResolver)
        setCallLogs(curLog)
        return view
    }

    private fun setCallLogs(curLog: Cursor?) {
        if (curLog!!.count == 0) {
            val builder: AlertDialog.Builder
            //builder = new AlertDialog.Builder(mcontext, android.R.style.Theme_Material_Dialog_Alert);
            builder = AlertDialog.Builder(activity)
            builder.setTitle("Call History")
                    .setMessage("No any calls from your history!!")
                    .setPositiveButton(android.R.string.yes) { dialog, which -> // continue with delete
                        dialog.cancel()
                    }
                    .setIcon(R.drawable.ic_warning)
                    .setCancelable(false)
                    .show()
            /*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })*/return
        }
        mcallItems.clear()
        while (curLog.moveToNext()) {
            val callNumber = curLog.getString(curLog.getColumnIndex(CallLog.Calls.NUMBER))
            val callName = curLog.getString(curLog.getColumnIndex(CallLog.Calls.CACHED_NAME))
            /*if (callName == null) {
                conNames.add("Unknown");
            } else
                conNames.add(callName);*/
            val callDate = curLog.getString(curLog.getColumnIndex(CallLog.Calls.DATE))
            val formatter = SimpleDateFormat("dd-MMM-yyyy HH:mm aa")
            val dateString = formatter.format(Date(callDate.toLong()))
            //conDate.add(dateString);
            val callTypeCode = curLog.getString(curLog.getColumnIndex(CallLog.Calls.TYPE))
            val callcode = callTypeCode.toInt()
            when (callcode) {
                CallLog.Calls.OUTGOING_TYPE -> callType = 1
                CallLog.Calls.INCOMING_TYPE -> callType = 2
                CallLog.Calls.MISSED_TYPE -> callType = 3
            }
            val duration = curLog.getString(curLog.getColumnIndex(CallLog.Calls.DURATION))
            //conTime.add(duration);
            val callLogItem = CallLogItem()
            callLogItem.phoneNumber = callNumber
            callLogItem.callDate = dateString.toString()
            callLogItem.callType = callType
            callLogItem.callDuration = duration
            mcallItems.add(callLogItem)
            recycleview_call_log!!.setHasFixedSize(true)
            mLayoutManager = LinearLayoutManager(activity)
            recycleview_call_log!!.layoutManager = mLayoutManager
            mAdapter = CallListAdapter(activity, mcallItems, this)
            recycleview_call_log!!.adapter = mAdapter

            //progressDialog.dismiss();
        }
    }

    override fun onPositionClicked(phoneNUmber: String?) {
        phone = phoneNUmber
        setInterstitialAd()

        /*Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("selectNumber", phone);
        getActivity().setResult(REQUEST_FOR_ACTIVITY_CODE, intent);
        getActivity().finish();
        Log.e("SELECT", "-----phoneNUmber-11----" + phoneNUmber);*/
    }

    override fun onResume() {
        super.onResume()
        Log.e("RESUME", "---phone---resume----")
    }

    private fun setInterstitialAd() {
        val adRequest = AdRequest.Builder().addTestDevice("YOUR_TEST_DEVICE_ID").build()
        mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd!!.adUnitId = resources.getString(R.string.Interstitial_Ad)
        mInterstitialAd!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                //Ads loaded
                mInterstitialAd!!.show()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                //Ads closed
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("selectNumber", phone)
                activity!!.setResult(REQUEST_FOR_ACTIVITY_CODE, intent)
                activity!!.finish()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                super.onAdFailedToLoad(errorCode)
                //Ads couldn't loaded
                // Toast.makeText(mcontext, "Please Check Network connection", Toast.LENGTH_LONG).show();
            }
        }
        mInterstitialAd!!.loadAd(adRequest)
    }

    companion object {
        const val REQUEST_READ_CONTACTS = 79
    }
}