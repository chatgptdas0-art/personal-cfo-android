package com.example.personalcfo;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient.FileChooserParams;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Environment;
import android.content.ContentValues;
import android.util.Base64;
import android.view.Window;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private static final int FILE_CHOOSER = 1001;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        webView = new WebView(this);
        setContentView(webView);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onShowFileChooser(WebView w, ValueCallback<Uri[]> callback, FileChooserParams params){
                if(filePathCallback!=null) filePathCallback.onReceiveValue(null);
                filePathCallback=callback;
                try{
                    Intent i=params.createIntent();
                    startActivityForResult(i, FILE_CHOOSER);
                    return true;
                }catch(Exception e){ filePathCallback=null; return false; }
            }
        });
        webView.addJavascriptInterface(new Object(){
            @android.webkit.JavascriptInterface
            public void saveTextFile(String name, String mime, String base64){
                try{
                    byte[] data=Base64.decode(base64,Base64.DEFAULT);
                    ContentValues v=new ContentValues();
                    v.put(MediaStore.Downloads.DISPLAY_NAME,name);
                    v.put(MediaStore.Downloads.MIME_TYPE,mime);
                    String folder="Personal_CFO/Exports";
                    if(name.toLowerCase().contains("backup")) folder="Personal_CFO/Backups";
                    else if(name.toLowerCase().contains("report")) folder="Personal_CFO/Reports";
                    v.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS+"/"+folder+"/");
                    v.put(MediaStore.Downloads.IS_PENDING,1);
                    Uri uri=getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,v);
                    if(uri==null) return;
                    try(OutputStream out=getContentResolver().openOutputStream(uri)){ out.write(data); }
                    v.clear(); v.put(MediaStore.Downloads.IS_PENDING,0);
                    getContentResolver().update(uri,v,null,null);
                }catch(Exception ignored){}
            }
        }, "Android");
        webView.loadUrl("file:///android_asset/index.html");
    }
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==FILE_CHOOSER && filePathCallback!=null){
            Uri[] result=(resultCode==RESULT_OK && data!=null && data.getData()!=null)?new Uri[]{data.getData()}:null;
            filePathCallback.onReceiveValue(result); filePathCallback=null;
        }
    }
    @Override public void onBackPressed() { if (webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
    @Override protected void onDestroy() { if (webView != null) webView.destroy(); super.onDestroy(); }
}
