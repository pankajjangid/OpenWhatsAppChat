package com.whatsappdirect.direct_chat.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.textfield.TextInputLayout
import com.hbb20.CountryCodePicker
import com.whatsappdirect.direct_chat.Activity.MainActivity
import com.whatsappdirect.direct_chat.Adapter.CallLogAdapter
import com.whatsappdirect.direct_chat.R
import com.whatsappdirect.direct_chat.Utils.AdapterCallback
import com.whatsappdirect.direct_chat.Utils.CallLogHelper
import com.whatsappdirect.direct_chat.Utils.CallLogItem
import io.fabric.sdk.android.Fabric
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), AdapterCallback {
    var countryCodePicker: CountryCodePicker? = null
    var etPhone: EditText? = null
    var edtTextMsg: EditText? = null
    var tvValidity: TextView? = null
    var send_btn: TextView? = null
    var imgValidity: ImageView? = null
    var iv_call: ImageView? = null
    var mClearText: Button? = null
    var send_cardmsg: CardView? = null
    var valid = false
    var textInputLayout: TextInputLayout? = null
    var mcontext: Context? = null
    var linear_main: LinearLayout? = null
    private var recycleview_call_log: RecyclerView? = null
    var mcallItems = ArrayList<CallLogItem>()
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mAdapter: CallLogAdapter? = null
    var curLog: Cursor? = null
    var callType = 0
    var selectedNumber: String? = null
    private val REQUEST_FOR_ACTIVITY_CODE = 55
    var callback: AdapterCallback? = null
    val appPackageName = "com.whatsappdirect.direct_chat"
    var progressDialog: ProgressDialog? = null
    var dialog: Dialog? = null
    private val TAG = MainActivity::class.java.simpleName
    private val mAdView: AdView? = null
    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)
        setContent()
        setAdmobAds()
        edtTextMsg!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                //do nothing
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length != 0) {
                    mClearText!!.visibility = View.VISIBLE
                } else {
                    mClearText!!.visibility = View.GONE
                }
            }
        })
    }

    private fun setAdmobAds() {
        val mAdView = findViewById<View>(R.id.adView) as AdView
        mAdView.visibility = View.VISIBLE
        loadBannerAdd(mAdView)
    }

    fun setContent() {
        assignViews()
        registerCarrierEditText()
        setClickListener()

        /* etPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etPhone.getRight() - etPhone.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                       */
        /* progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Loading..");
                        progressDialog.show();*/
        /*
                        // openDialog();
                        Intent intent = new Intent(MainActivity.this, CallLogHistoryList.class);
                        startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);

                        return true;
                    }
                }
                return false;
            }
        });*/
    }

    private fun setClickListener() {
        send_cardmsg!!.setOnClickListener {
            if (registerCarrierEditText() == true) {
                message
            } else {
                val phoneno = etPhone!!.text.toString()
                if (!phoneno.isEmpty()) {
                    message
                } else {
                    val builder1 = AlertDialog.Builder(mcontext)
                    builder1.setMessage("Please Enter PhoneNumber.")
                    builder1.setCancelable(false)
                    builder1.setPositiveButton("OK"
                    ) { dialog, id -> dialog.cancel() }
                    val alert11 = builder1.create()
                    alert11.show()
                    //  etPhone.setError("Enter phonenumber");
                }
                //etPhone.setError("Please Enter Valid PhoneNumber");
                //etPhone.setErrorTextAppearance(R.style.TextInputLayout_TextError);
                //tvValidity.setText("Invalid Number");
            }
        }
        iv_call!!.setOnClickListener {
            setInterstitialAd()
            /*Intent intent = new Intent(MainActivity.this, CallLogHistoryList.class);
                startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);*/
        }
    }

    private fun setInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        mInterstitialAd = InterstitialAd(this)
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
                val intent = Intent(this@MainActivity, CallLogHistoryList::class.java)
                startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE)
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                super.onAdFailedToLoad(errorCode)

                //Ads couldn't loaded
                // Toast.makeText(mcontext, "Please Check Network connection", Toast.LENGTH_LONG).show();
            }
        }
        mInterstitialAd!!.loadAd(adRequest)
    }/*String url = "https://www.google.com";
            url = url + "?phoneNumber=" + countryCodePicker.getFullNumber() + "&Message=" + textMsg;*/

    // edtTextMsg.setError("Please Enter Message");
    //  textInputLayout.setError("Please Enter Message");
    //textInputLayout.setErrorTextAppearance(R.style.TextInputLayout_TextError);
    val message: Unit
        get() {
            val textMsg = edtTextMsg!!.text.toString().trim { it <= ' ' }
            if (textMsg.isEmpty()) {
                val builder1 = AlertDialog.Builder(mcontext)
                builder1.setMessage("Please Enter Message.")
                builder1.setCancelable(false)
                builder1.setPositiveButton("OK"
                ) { dialog, id -> dialog.cancel() }
                val alert11 = builder1.create()
                alert11.show()

                // edtTextMsg.setError("Please Enter Message");
                //  textInputLayout.setError("Please Enter Message");
                //textInputLayout.setErrorTextAppearance(R.style.TextInputLayout_TextError);
            } else {
                var url = "https://api.whatsapp.com/send"
                url = url + "?phone=" + countryCodePicker!!.fullNumber + "&text=" + textMsg

                /*String url = "https://www.google.com";
                url = url + "?phoneNumber=" + countryCodePicker.getFullNumber() + "&Message=" + textMsg;*/
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }

    private fun registerCarrierEditText(): Boolean {
        countryCodePicker!!.registerCarrierNumberEditText(etPhone)
        countryCodePicker!!.setPhoneNumberValidityChangeListener { isValidNumber ->
            if (isValidNumber) {
                imgValidity!!.setImageDrawable(resources.getDrawable(R.drawable.ic_assignment_turned_in))
                tvValidity!!.text = "Valid Number"
                tvValidity!!.setTextColor(resources.getColor(R.color.colorPrimary))
                valid = true
            } else {
                imgValidity!!.setImageDrawable(resources.getDrawable(R.drawable.ic_assignment_late))
                tvValidity!!.text = "Invalid Number"
                tvValidity!!.setTextColor(resources.getColor(R.color.colorAccent))
                valid = false
            }
        }
        return valid
    }

    private fun assignViews() {
        mcontext = this@MainActivity
        etPhone = findViewById(R.id.et_phone)
        countryCodePicker = findViewById(R.id.ccp)
        //countryCodePicker.registerCarrierNumberEditText(etPhone);
        tvValidity = findViewById(R.id.tv_validity)
        imgValidity = findViewById(R.id.img_validity)
        edtTextMsg = findViewById(R.id.edt_textmsg)
        //  textInputLayout = findViewById(R.id.edtxt_input);
        linear_main = findViewById(R.id.linear_main)
        iv_call = findViewById(R.id.iv_call)
        mClearText = findViewById(R.id.clearText)
        send_cardmsg = findViewById(R.id.cardsendMessage)
        send_btn = findViewById(R.id.send_button)
    }

    fun openDialog() {
        mcallItems.clear()
        dialog = Dialog(this@MainActivity)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog!!.setContentView(R.layout.activity_call_history)
        dialog!!.setCanceledOnTouchOutside(false)
        // dialog.show();
        /*   if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
               curLog = CallLogHelper.getAllCallLogs(contentResolver)
               setCallLogs(curLog)
               //  dialog.show();
           } else {
               requestLocationPermission()
           }*/
        recycleview_call_log = dialog!!.findViewById(R.id.recycleview_call_log)
        recycleview_call_log!!.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        recycleview_call_log!!.setLayoutManager(mLayoutManager)
        mAdapter = CallLogAdapter(this@MainActivity, mcallItems, this)
        recycleview_call_log!!.setAdapter(mAdapter)
        val dialogButton = dialog!!.findViewById<Button>(R.id.dialog_cancel)
        dialogButton.setOnClickListener {
            dialog!!.dismiss()
            progressDialog!!.dismiss()
        }
    }

    protected fun requestLocationPermission() {
        /*  if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG)) {
              // show UI part if you want here to show some rationale !!!
              Log.e("permission", "==check=requestLocationPermission==2222")
          } else {
              Log.e("permission", "==check=requestLocationPermission==33333")
              ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), REQUEST_READ_CONTACTS)
          }*/
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    curLog = CallLogHelper.getAllCallLogs(contentResolver)
                    setCallLogs(curLog)
                    // mAdapter = new CallLogAdapter(MainActivity.this, mcallItems,callback);
                    mAdapter = CallLogAdapter(this@MainActivity, mcallItems, this)
                    recycleview_call_log!!.adapter = mAdapter
                    progressDialog!!.dismiss()
                } else {
                    dialog!!.dismiss()
                    progressDialog!!.dismiss()
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    private fun setCallLogs(curLog: Cursor?) {
        Log.e("CALLLOGLENTH", "" + curLog!!.count)
        if (curLog.count == 0) {
            progressDialog!!.dismiss()
            dialog!!.dismiss()
            val builder: AlertDialog.Builder
            //builder = new AlertDialog.Builder(mcontext, android.R.style.Theme_Material_Dialog_Alert);
            builder = AlertDialog.Builder(mcontext)
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
        dialog!!.show()
        while (curLog.moveToNext()) {
            val callNumber = curLog.getString(curLog.getColumnIndex(CallLog.Calls.NUMBER))
            val callName = curLog.getString(curLog.getColumnIndex(CallLog.Calls.CACHED_NAME))
            /*if (callName == null) {
                conNames.add("Unknown");
            } else
                conNames.add(callName);*/
            val callDate = curLog.getString(curLog.getColumnIndex(CallLog.Calls.DATE))
            val formatter = SimpleDateFormat("dd-MMM-yyyy hh:mm a")
            val dateString = formatter.format(Date(callDate.toLong()))

            /* dateString = dateString.replace("a.m.", "am").replace("p.m.","pm");
            Log.e("date","==hour=111="+dateString);*/
            val callTypeCode = curLog.getString(curLog.getColumnIndex(CallLog.Calls.TYPE))
            val callcode = callTypeCode.toInt()
            when (callcode) {
                CallLog.Calls.OUTGOING_TYPE -> callType = 1
                CallLog.Calls.INCOMING_TYPE -> callType = 2
                CallLog.Calls.MISSED_TYPE -> callType = 3
            }
            val duration = curLog.getString(curLog.getColumnIndex(CallLog.Calls.DURATION))
            val callLogItem = CallLogItem()
            callLogItem.phoneNumber = callNumber
            callLogItem.callDate = dateString.toString()
            callLogItem.callType = callType
            callLogItem.callDuration = duration
            mcallItems.add(callLogItem)
            progressDialog!!.dismiss()
        }
    }

    override fun onPositionClicked(phoneNUmber: String?) {
        //etPhone.setText(phoneNUmber);
        var countryCode = 0
        val phoneUtil = PhoneNumberUtil.createInstance(this)
        try {
            // phone must begin with '+'
            val numberProto = phoneUtil.parse(phoneNUmber, "")
            countryCode = numberProto.countryCode
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: $e")
        }
        Log.e("countryCode", "===countryCode==$countryCode")
        if (countryCode == 0) {
            etPhone!!.setText(phoneNUmber)
        } else {
            if (phoneNUmber!!.startsWith("+")) {
                if (phoneNUmber.length == 12) {
                    val str_getMOBILE = phoneNUmber.substring(2)
                    etPhone!!.setText(str_getMOBILE)
                } else if (phoneNUmber.length == 13) {
                    val str_getMOBILE = phoneNUmber.substring(3)
                    etPhone!!.setText(str_getMOBILE)
                } else if (phoneNUmber.length == 14) {
                    val str_getMOBILE = phoneNUmber.substring(4)
                    etPhone!!.setText(str_getMOBILE)
                } else {
                    etPhone!!.setText(phoneNUmber)
                }
            }
            countryCodePicker!!.setCountryForPhoneCode(countryCode)
        }
        dialog!!.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_maintoolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share_app -> {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_SUBJECT, "WhatsAppDirect")
                var sAux = "\nLet me recommend you this application\n\n"
                sAux = sAux + "https://play.google.com/store/apps/details?id=" + appPackageName
                i.putExtra(Intent.EXTRA_TEXT, sAux)
                startActivity(Intent.createChooser(i, "choose one"))
            }
            R.id.rate_app -> {
                val uri = Uri.parse("market://details?id=$appPackageName")
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                try {
                    startActivity(goToMarket)
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }
            R.id.terms_privacy -> {
                val intent = Intent(mcontext, TermsAndPrivacyActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    fun clear(view: View?) {
        edtTextMsg!!.setText("")
        mClearText!!.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("SELECTED", "---select--no--data---$data")
        if (requestCode == REQUEST_FOR_ACTIVITY_CODE) {
            if (data != null) {
                selectedNumber = data.getStringExtra("selectNumber")
                Log.e("SELECTED", "---select--no--final---$selectedNumber")
                comparePhoneNumber(selectedNumber)
            } else {
            }
        }
    }

    private fun comparePhoneNumber(selectedNumber: String?) {
        var countryCode = 0
        val phoneUtil = PhoneNumberUtil.createInstance(this)
        try {
            // phone must begin with '+'
            val numberProto = phoneUtil.parse(selectedNumber, "")
            countryCode = numberProto.countryCode
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: $e")
        }
        Log.e("countryCode", "===countryCode==$countryCode")
        if (countryCode == 0) {
            etPhone!!.setText(selectedNumber)
        } else {
            if (selectedNumber!!.startsWith("+")) {
                if (selectedNumber.length == 12) {
                    val str_getMOBILE = selectedNumber.substring(2)
                    etPhone!!.setText(str_getMOBILE)
                } else if (selectedNumber.length == 13) {
                    val str_getMOBILE = selectedNumber.substring(3)
                    etPhone!!.setText(str_getMOBILE)
                } else if (selectedNumber.length == 14) {
                    val str_getMOBILE = selectedNumber.substring(4)
                    etPhone!!.setText(str_getMOBILE)
                } else {
                    etPhone!!.setText(selectedNumber)
                }
            }
            countryCodePicker!!.setCountryForPhoneCode(countryCode)
        }
    }

    companion object {
        //private RecyclerView.Adapter mAdapter;
        const val REQUEST_READ_CONTACTS = 79
        fun loadBannerAdd(mAdView: AdView) {
            val adRequest = AdRequest.Builder() /*
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
*/
                    .build()
            mAdView.adListener = object : AdListener() {
                override fun onAdLoaded() {}
                override fun onAdClosed() {}
                override fun onAdFailedToLoad(errorCode: Int) {}
                override fun onAdLeftApplication() {}
                override fun onAdOpened() {
                    super.onAdOpened()
                }
            }
            mAdView.loadAd(adRequest)
        }
    }
}