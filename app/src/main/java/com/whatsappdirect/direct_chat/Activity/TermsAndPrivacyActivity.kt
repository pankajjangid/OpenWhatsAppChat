package com.whatsappdirect.direct_chat.Activity

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.whatsappdirect.direct_chat.R

class TermsAndPrivacyActivity : AppCompatActivity() {
    private var webView: WebView? = null
    private val toolbar: Toolbar? = null
    private var iv_back: ImageView? = null
    private var tv_title: TextView? = null
    var url = "file:///android_asset/Terms&PrivacyPolicy.html"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_privacy)
        supportActionBar!!.hide()
        webView = findViewById(R.id.web_view)
        iv_back = findViewById(R.id.iv_back)
        tv_title = findViewById(R.id.tv_tooltitle)
        tv_title!!.setText(R.string.menu_name4)
        webView!!.loadUrl(url)
        iv_back!!.setVisibility(View.VISIBLE)
        iv_back!!.setOnClickListener(View.OnClickListener { finish() })
    }
}