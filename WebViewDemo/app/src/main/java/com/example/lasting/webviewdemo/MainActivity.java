package com.example.lasting.webviewdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView webView1;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView1 = (WebView) findViewById(R.id.webView1);
        webView1.loadUrl("http://www.baidu.com/");
        webView1.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings webSettings=webView1.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView1.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress==100){
                    closeProgressDialog();
                }else{
                    openProgressDialog(newProgress);
                }
            }
        });
    }

    private void openProgressDialog(int newProgress) {
        if (progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("正在加载");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(newProgress);
            progressDialog.show();
        }else{
            progressDialog.setProgress(newProgress);
        }
    }

    private void closeProgressDialog() {
        if (progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog=null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if (webView1.canGoBack()){
                webView1.goBack();
                return true;
            }else{
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
