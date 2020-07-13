package com.whatsappdirect.direct_chat.Activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.whatsappdirect.direct_chat.R
import com.whatsappdirect.direct_chat.Utils.PrefManager

class LaunchActivity : AppCompatActivity(), View.OnClickListener {
    private var viewPager: ViewPager? = null
    private var btnSkip: Button? = null
    private var btnNext: Button? = null
    private var dotsLayout: LinearLayout? = null
    private var prefManager: PrefManager? = null
    private lateinit var dots_text: Array<TextView?>
    private lateinit var slide_img: IntArray
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        System.gc()
        supportActionBar!!.hide()
        // Checking for first time launch - before calling setContentView()
        prefManager = PrefManager(this)
        if (!prefManager!!.isFirstTimeLaunch) {
            launchHomeScreen()
            finish()
        }
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        setContentView(R.layout.activity_launch)
        initViews()
        slide_img = intArrayOf(R.drawable.snap1, R.drawable.snap2, R.drawable.snap3, R.drawable.snap4, R.drawable.snap5, R.drawable.snap6)
        addBottomDots(0)
        // changeStatusBarColor();
        myViewPagerAdapter = MyViewPagerAdapter()
        viewPager!!.adapter = myViewPagerAdapter
        viewPager!!.addOnPageChangeListener(viewPagerPageChangeListener)
        btnNext!!.setOnClickListener(this)
        btnSkip!!.setOnClickListener(this)
    }

    /*private void changeStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }*/
    private fun addBottomDots(currentPage: Int) {
        dots_text = arrayOfNulls(slide_img.size)
        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)
        dotsLayout!!.removeAllViews()
        for (i in dots_text.indices) {
            dots_text[i] = TextView(this)
            dots_text[i]!!.text = Html.fromHtml("&#8226;")
            dots_text[i]!!.textSize = 35f
            dots_text[i]!!.setTextColor(colorsInactive[currentPage])
            dotsLayout!!.addView(dots_text[i])
        }
        if (dots_text.size > 0) dots_text[currentPage]!!.setTextColor(colorsActive[currentPage])
    }

    private fun launchHomeScreen() {
        prefManager!!.isFirstTimeLaunch = false
        startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
        finish()
    }

    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            addBottomDots(position)
            if (position == slide_img.size - 1) {
                // last page. make button text to GOT IT
                btnNext!!.text = getString(R.string.start)
                btnSkip!!.visibility = View.GONE
            } else {
                // still pages are left
                btnNext!!.text = getString(R.string.next)
                btnSkip!!.visibility = View.VISIBLE
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun initViews() {
        viewPager = findViewById<View>(R.id.view_pager) as ViewPager
        dotsLayout = findViewById<View>(R.id.layoutDots) as LinearLayout
        btnSkip = findViewById<View>(R.id.btn_skip) as Button
        btnNext = findViewById<View>(R.id.btn_next) as Button
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_skip -> launchHomeScreen()
            R.id.btn_next -> {
                val current = getItem(+1)
                if (current < slide_img.size) {
                    // move to next screen
                    viewPager!!.currentItem = current
                } else {
                    launchHomeScreen()
                }
            }
        }
    }

    private fun getItem(i: Int): Int {
        return viewPager!!.currentItem + i
    }

    private inner class MyViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater!!.inflate(R.layout.slider_image_layout, container, false)
            //View view = layoutInflater.inflate(slide_img[position], container, false);
            val imageView = view.findViewById<View>(R.id.img_slider) as ImageView
            imageView.setImageResource(slide_img[position])
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return slide_img.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}