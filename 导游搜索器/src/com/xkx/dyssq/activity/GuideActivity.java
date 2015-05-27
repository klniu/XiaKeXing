package com.xkx.dyssq.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xkx.dyssq.R;

public class GuideActivity extends Activity implements OnClickListener {

	private Button button1;
	private Button button2;
	private Button button3;
	GradientDrawable drawable;
	private RelativeLayout backlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		initUI();
	}

	private void initUI() {
		backlay= (RelativeLayout) findViewById(R.id.backlay);
		button1= (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		backlay.setOnClickListener(this);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		drawable = new GradientDrawable();
		drawable.setShape(GradientDrawable.RECTANGLE); // ����
		drawable.setStroke(2, Color.parseColor("#ff4081")); // �߿��ϸ����ɫ
		drawable.setColor(Color.WHITE); // �߿��ڲ���ɫ
		button2.setBackgroundDrawable(drawable); // ���ñ�����Ч�������б߿򼰵�ɫ��
		button3.setBackgroundDrawable(drawable); // ���ñ�����Ч�������б߿򼰵�ɫ��

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guide, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			button1.setBackgroundColor(Color.parseColor("#ff4081"));
			button1.setTextColor(Color.parseColor("#ffffff"));
			button2.setBackgroundDrawable(drawable); // ���ñ�����Ч�������б߿򼰵�ɫ��
			button2.setTextColor(Color.parseColor("#ff4081"));
			button3.setBackgroundDrawable(drawable); // ���ñ�����Ч�������б߿򼰵�ɫ��
			button3.setTextColor(Color.parseColor("#ff4081"));
			break;
		case R.id.button2:
			button2.setBackgroundColor(Color.parseColor("#ff4081"));
			button2.setTextColor(Color.parseColor("#ffffff"));
			button1.setBackgroundDrawable(drawable); // ���ñ�����Ч�������б߿򼰵�ɫ��
			button1.setTextColor(Color.parseColor("#ff4081"));
			button3.setBackgroundDrawable(drawable); // ���ñ�����Ч�������б߿򼰵�ɫ��
			button3.setTextColor(Color.parseColor("#ff4081"));
			break;
		case R.id.button3:
			button3.setBackgroundColor(Color.parseColor("#ff4081"));
			button3.setTextColor(Color.parseColor("#ffffff"));
			button1.setBackgroundDrawable(drawable); // ���ñ�����Ч�������б߿򼰵�ɫ��
			button1.setTextColor(Color.parseColor("#ff4081"));
			button2.setBackgroundDrawable(drawable); // ���ñ�����Ч�������б߿򼰵�ɫ��
			button2.setTextColor(Color.parseColor("#ff4081"));
			break;
		case R.id.backlay:
			finish();
			break;
		default:
			break;
		}
	}

}
