package com.whatsappdirect.direct_chat

import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
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
import com.whatsappdirect.direct_chat.Adapter.MessageAdapter
import com.whatsappdirect.direct_chat.Utils.AdapterCallback
import com.whatsappdirect.direct_chat.Utils.SMSModel
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MessageFragment : Fragment(), AdapterCallback {
    private var recycleview_message: RecyclerView? = null
    var msmsItems = ArrayList<SMSModel>()
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    var curLog: Cursor? = null
    var callType = 0
    private val callback: AdapterCallback? = null
    private val REQUEST_FOR_ACTIVITY_CODE = 55
    var phone: String? = null
    var progressDialog: ProgressDialog? = null
    private var mInterstitialAd: InterstitialAd? = null
    val appPackageName = "com.whatsappdirect.direct_chat"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_message, container, false)

//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Loading..");
//        progressDialog.show();
        //askPermission();
        recycleview_message = view.findViewById(R.id.recycleview_message)
        recycleview_message!!.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(activity)
        recycleview_message!!.setLayoutManager(mLayoutManager)
        Log.e("MESSAGE", "--msmsItems--0000" + msmsItems.size)
        mAdapter = MessageAdapter(activity, msmsItems, this)
        recycleview_message!!.setAdapter(mAdapter)
        allSms
        return view
    }

    //Uri.parse("content://sms/inbox");
    private val allSms: Unit
        private get() {
            var objSms: SMSModel
            val message = Uri.parse("content://sms/")
            //Uri.parse("content://sms/inbox");
            val cursor = Objects.requireNonNull(activity)!!.contentResolver.query(message, null, null, null, null)
            msmsItems.clear()
            if (cursor != null) if (cursor.moveToFirst()) {
                do {
                    try {
                        objSms = SMSModel()
                        objSms.id = cursor.getString(cursor.getColumnIndexOrThrow("_id"))
                        objSms.address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                        objSms.msg = cursor.getString(cursor.getColumnIndexOrThrow("body"))
                        objSms.readState = cursor.getString(cursor.getColumnIndex("read"))
                        objSms.time = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                        if (cursor.getString(cursor.getColumnIndexOrThrow("type")).contains("1")) {
                            objSms.folderName = "inbox"
                        } else {
                            objSms.folderName = "sent"
                        }
                        msmsItems.add(objSms)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    }
                } while (cursor.moveToNext())
            }
            assert(cursor != null)
            cursor!!.close()
            mAdapter!!.notifyDataSetChanged()
        }

    override fun onPositionClicked(msgnumber: String?) {
        phone = msgnumber
        setInterstitialAd()
        // isNumeric(phone);
        /*Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("selectNumber", phone);
        getActivity().setResult(REQUEST_FOR_ACTIVITY_CODE, intent);
        getActivity().finish();
        Log.e("SELECT", "-----phoneNUmber-msg--11--" + phone);*/
    }

    override fun onResume() {
        super.onResume()
        Log.e("RESUME", "---msg--resume----")
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
                Log.e("SELECT", "-----phoneNUmber-msg--11--$phone")
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
        fun isNumeric(str: String): Boolean {
            try {
                val msgphone = str.toInt()
            } catch (nfe: NumberFormatException) {
                return false
            }
            return true
        }
    }
}