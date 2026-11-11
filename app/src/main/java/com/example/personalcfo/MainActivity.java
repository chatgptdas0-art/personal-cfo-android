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
import android.view.View;
import android.view.WindowInsets;
import android.os.Build;
import android.window.OnBackInvokedDispatcher;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private static final int FILE_CHOOSER = 1001;
    private static final int BACKUP_CREATE = 1002;
    private String pendingBackupName;
    private String pendingBackupMime;
    private byte[] pendingBackupData;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        webView = new WebView(this);
        webView.setFitsSystemWindows(false);
        setContentView(webView);

        webView.setOnApplyWindowInsetsListener((v, insets) -> {
            int top = 0, bottom = 0;
            if (Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets i = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                top = i.top;
                bottom = i.bottom;
            } else {
                top = insets.getSystemWindowInsetTop();
                bottom = insets.getSystemWindowInsetBottom();
            }
            webView.setPadding(0, top, 0, bottom);
            return insets;
        });

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
            @android.webkit.JavascriptInterface
            public void createBackupFile(String name, String mime, String base64){
                try{
                    pendingBackupName = name;
                    pendingBackupMime = mime;
                    pendingBackupData = Base64.decode(base64, Base64.DEFAULT);
                    Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType(mime);
                    i.putExtra(Intent.EXTRA_TITLE, name);
                    startActivityForResult(i, BACKUP_CREATE);
                }catch(Exception e){
                    pendingBackupName=null; pendingBackupMime=null; pendingBackupData=null;
                    webView.post(() -> webView.evaluateJavascript("window.onAndroidBackupResult && window.onAndroidBackupResult(false,'Unable to open save location')", null));
                }
            }
        }, "Android");
        webView.loadUrl("file:///android_asset/index.html");
        webView.requestApplyInsets();

        if (Build.VERSION.SDK_INT >= 33) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    () -> handleBack());
        }
    }

    private void handleBack() {
        webView.evaluateJavascript("document.getElementById('sheet')?.classList.contains('open') ? (window.cancelForm && window.cancelForm()) : false", value -> {
            if ("false".equals(value) && webView.canGoBack()) webView.goBack();
            else if ("false".equals(value)) finish();
        });
    }

    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==FILE_CHOOSER && filePathCallback!=null){
            Uri[] result=(resultCode==RESULT_OK && data!=null && data.getData()!=null)?new Uri[]{data.getData()}:null;
            filePathCallback.onReceiveValue(result); filePathCallback=null;
            return;
        }
        if(requestCode==BACKUP_CREATE){
            final Uri uri = (resultCode==RESULT_OK && data!=null) ? data.getData() : null;
            final String callback;
            if(uri==null || pendingBackupData==null){
                callback = "window.onAndroidBackupResult && window.onAndroidBackupResult(false,'Backup save cancelled')";
            }else{
                boolean ok=false;
                try{
                    try(OutputStream out=getContentResolver().openOutputStream(uri)){
                        if(out==null) throw new Exception("No output stream");
                        out.write(pendingBackupData);
                        out.flush();
                    }
                    ok=true;
                }catch(Exception ignored){}
                callback = ok
                    ? "window.onAndroidBackupResult && window.onAndroidBackupResult(true,'Backup saved to the location you selected')"
                    : "window.onAndroidBackupResult && window.onAndroidBackupResult(false,'Could not write the backup file')";
            }
            pendingBackupName=null; pendingBackupMime=null; pendingBackupData=null;
            webView.post(() -> webView.evaluateJavascript(callback, null));
        }
    }

    @Override public void onBackPressed() { handleBack(); }
    @Override protected void onDestroy() { if (webView != null) webView.destroy(); super.onDestroy(); }
}
