package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

//ӳ��
public class ReflexActivity extends Activity {

	private ViewPager pager;
	private RelativeLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//�Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_reflex);
		layout = (RelativeLayout) findViewById(R.id.viewpager_bg);
		layout.setBackgroundResource(R.drawable.img_bg);
		pager = (ViewPager) findViewById(R.id.viewpager);
	}
	
//	class ViewPagerAdapter extends 
	
	//����
    public void Back(View v){
    	finish();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reflex, menu);
		return true;
	}

}
