package com.whatsappdirect.direct_chat.Activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.whatsappdirect.direct_chat.PhoneCallsfragment
import com.whatsappdirect.direct_chat.R
import com.whatsappdirect.direct_chat.Utils.AdapterCallback
import com.whatsappdirect.direct_chat.Utils.CallLogItem
import java.util.*

class CallLogHistoryList : AppCompatActivity(), OnPageChangeListener {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private val tabIcons = intArrayOf(R.drawable.callhistory, R.drawable.ic_message)
    private var iv_back: ImageView? = null
    private var tv_title: TextView? = null
    var progressDialog: ProgressDialog? = null
    private var mcontext: Context? = null
    private val recycleview_call_log: RecyclerView? = null
    var mcallItems = ArrayList<CallLogItem>()
    private val mLayoutManager: RecyclerView.LayoutManager? = null
    private val mAdapter: RecyclerView.Adapter<*>? = null
    var curLog: Cursor? = null
    var callType = 0
    private val callback: AdapterCallback? = null
    private val REQUEST_FOR_ACTIVITY_CODE = 55
    var phone: String? = null
    private var adapter: ViewPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_log_history_list)
        supportActionBar!!.hide()
        init()
        askPermission()
        //tabLayout = (TabLayout) findViewById(R.id.tabs);

        /*  tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);*/
        // setupTabIcons();
        tabLayout!!.setupWithViewPager(viewPager)
        viewPager!!.setOnPageChangeListener(this)
        iv_back!!.visibility = View.VISIBLE
        iv_back!!.setOnClickListener { finish() }
    }

    /* private void setupTabIcons() {

         tabLayout.getTabAt(0).setIcon(tabIcons[0]);
         tabLayout.getTabAt(1).setIcon(tabIcons[1]);
     }
 */
    private fun askPermission() {
        if (!checkPermission()) {
            requestPermission()
        } else {
            setupViewPager(viewPager)
        }
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter!!.addFragment(PhoneCallsfragment(), "Call")
        //   adapter.addFragment(new MessageFragment(), "Message");
        viewPager!!.adapter = adapter

        /*   TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        //tabOne.setText("ONE");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.callhistory, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        //tabTwo.setText("TWO");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_message, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);*/
        /* tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.setupWithViewPager(viewPager);*/
    }

    private fun init() {
        mcontext = this@CallLogHistoryList
        iv_back = findViewById(R.id.iv_back)
        tv_title = findViewById(R.id.tv_tooltitle)
        tv_title!!.setText(R.string.call_list)
        viewPager = findViewById<View>(R.id.viewpager_calls) as ViewPager
        tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        iv_back!!.setVisibility(View.VISIBLE)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        try {
            /* FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach().attach(this).commit();*/
            adapter!!.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}
    private inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

        override fun getItemPosition(`object`: Any): Int {
            //return super.getItemPosition(object);
            return PagerAdapter.POSITION_NONE
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CALL_LOG)
        //    int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS);

        //  return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                //    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                //   if (locationAccepted && cameraAccepted) {
                if (locationAccepted) {
                    setupViewPager(viewPager)
                    // Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                } else {

                    //Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG)) {
                            showMessageOKCancel("You need to allow access to read contacts",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS),
                                                    PERMISSION_REQUEST_CODE)
                                        }
                                    })
                            return
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@CallLogHistoryList)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    companion object {
        const val REQUEST_READ_CONTACTS = 79
        private const val PERMISSION_REQUEST_CODE = 200
    }
}