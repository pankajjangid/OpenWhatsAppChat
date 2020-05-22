package com.whatsappdirect.direct_chat.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.whatsappdirect.direct_chat.R;

public class TermsAndPrivacyActivity extends AppCompatActivity {

    private WebView webView;
    private Toolbar toolbar;
    private ImageView iv_back;
    private TextView tv_title;

    String url = "file:///android_asset/Terms&PrivacyPolicy.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_privacy);

        getSupportActionBar().hide();

        webView = findViewById(R.id.web_view);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_tooltitle);
        tv_title.setText(R.string.menu_name4);

        webView.loadUrl(url);

        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }
}
