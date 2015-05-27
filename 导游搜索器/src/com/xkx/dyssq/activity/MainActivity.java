package com.xkx.dyssq.activity;

import com.xkx.dyssq.R;
import com.xkx.dyssq.R.layout;
import com.xkx.dyssq.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private boolean isBack;
	private boolean isStart;
	private ImageView m_im1;
	private ImageView m_im2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		m_im1 = (ImageView) findViewById(R.id.imageView1);
		m_im2 = (ImageView) findViewById(R.id.imageView2);
	}

	public void startAnimation(View v)
	{
		//����״̬
		isStart = !isStart;
		v.setClickable(false);
		final ImageView im = (ImageView) v;
		
		if(isStart)
		{
			m_im1.setImageResource(R.drawable.shake2);
			m_im2.setImageResource(R.drawable.people2);
			
			AnimationSet animationSet = new AnimationSet(true);
			
			TranslateAnimation animation1 = new TranslateAnimation(0, 30, 0, 0);
			TranslateAnimation animation2 = new TranslateAnimation(0, -60, 0, 0);
			TranslateAnimation animation3 = new TranslateAnimation(0, 60, 0, 0);
			TranslateAnimation animation4 = new TranslateAnimation(0, -30, 0, 0);
			
			//���ö�������ʱ�� 
			animation1.setDuration(250);
			animation2.setDuration(500);
			animation3.setDuration(500);
			animation4.setDuration(250);
			
			//�ӳٲ���
//			animation1.setStartOffset(2500);  
			animation2.setStartOffset(250);  
			animation3.setStartOffset(500);  
			animation4.setStartOffset(500);  
			
			animationSet.addAnimation(animation1);
			animationSet.addAnimation(animation2);
			animationSet.addAnimation(animation3);
			animationSet.addAnimation(animation4);
			
			animationSet.setRepeatCount(5);
			animationSet.setInterpolator(new LinearInterpolator());
			m_im2.startAnimation(animationSet);
			animationSet.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					isStart = false;
					m_im2.clearAnimation();
					m_im1.setImageResource(R.drawable.shake1);
					m_im2.setImageResource(R.drawable.people1);
					m_im2.setClickable(true);
				}
			});
		}
//		else
//		{
//			m_im2.clearAnimation();
//		}
	}
	
	//��תҳ��
	public void startIn(View v) 
	{
		Intent intent = null;
//		switch (v.getId()) 
//		{
//		case R.id.imageButton1:
//			intent = new Intent(this, SearchActivity.class);
//			break;
//		case R.id.imageButton2:
//			intent = new Intent(this, DestinationListActivity.class);
//			break;
//		case R.id.imageButton3:
//			intent = new Intent(this, MoreDestinationActivity.class);
//			break;
//		case R.id.imageButton4:
//			intent = new Intent(this, RouteDetailsActivity.class);
//			break;
//		case R.id.imageButton5:
//			intent = new Intent(this, ShakeActivity.class);
//			break;
//		case R.id.imageButton6:
//			intent = new Intent(this, PersonalCenterActivity.class);
//			break;
//
//		default:
//			break;
//		}
		intent = new Intent(this, GuideActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			if(isBack){
				finish();
				System.exit(0);
			}else{
				isBack = true;
				Toast.makeText(MainActivity.this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				m_im1.postDelayed(new Runnable() {
					public void run() 
					{
						isBack = false;
					}
				}, 2000);
			}
			
		}
		return false;
	}
}
