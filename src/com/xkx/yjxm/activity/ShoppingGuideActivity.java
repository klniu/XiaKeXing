package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//����
public class ShoppingGuideActivity extends Activity {

	private WebView webView;
	private String  url = "http://sz.himall.kuaidiantong.cn/m-Weixin/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//�Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_shopping_guide);
		webView = (WebView) findViewById(R.id.webView1);
		// ����WebView���ԣ��ܹ�ִ��Javascript�ű� 
		webView.getSettings().setJavaScriptEnabled(true);
		// ������Ҫ��ʾ����ҳ
		webView.loadUrl(url);
		// ����Web��ͼ
		webView.setWebViewClient(new HelloWebViewClient());
		//��ֹ�򲻿���ҳ
		WebSettings webSettings = webView.getSettings();
		webSettings.setDomStorageEnabled(true);
	}

	// Web��ͼ ���������ӻ����������⣬��������ϵͳ���û����������
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	public void Finsh(View v)
	{
		finish();
	}
	public void back(View v)
	{
		if (webView.canGoBack()) {
			// goBack()��ʾ����WebView����һҳ��
			webView.goBack();
			
		}
		else
		{
			finish();
		}
	}
	// ���û���
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			// goBack()��ʾ����WebView����һҳ��
			webView.goBack();
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			finish();
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shopping_guide, menu);
		return true;
	}

}
