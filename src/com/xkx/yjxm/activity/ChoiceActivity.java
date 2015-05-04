package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;
import com.xkx.yjxm.R.layout;
import com.xkx.yjxm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;

//ѡ�����
public class ChoiceActivity extends Activity {

	private boolean isList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choice);
		Intent intent = getIntent();
		isList = intent.getBooleanExtra("�б�", true);
	}
	
	// ����
	public void Back(View v) {
		finish();
	}
	
	// ��ת������ɽ�� ���� or ·�ߣ�
	public void StartGuanYinShan(View v) {
		Intent intent = null;
		if(isList)
		{
			intent = new Intent(this, RouteActivity.class);
		}
		else
		{
			intent = new Intent(this, GuideActivity.class);
		}
		intent.putExtra("name", "����ɽ");
		startActivity(intent);
	}

	// ��ת������ɽ��̨�� ���� or ·�ߣ�
	public void StartHuLiShan(View v) {
		Intent intent = null;
		if(isList)
		{
			intent = new Intent(this, RouteActivity.class);
		}
		else
		{
			intent = new Intent(this, GuideActivity.class);
		}
		intent.putExtra("name", "����ɽ");
//		startActivity(intent);
	}
	
	// ��ת�������죨 ���� or ·�ߣ�
	public void StartGuLangYu(View v) {
		Intent intent = null;
		if(isList)
		{
			intent = new Intent(this, RouteActivity.class);
		}
		else
		{
			intent = new Intent(this, GuideActivity.class);
		}
		intent.putExtra("name", "������");
//		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choice, menu);
		return true;
	}

}
