package com.example.test;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {
	private boolean isExit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		StartPaoPao();
	}

	// ����
	public void Backs(View v) {
		finish();
	}
	
	// ��ת��������
	public void StartLeft(View v) {
		Intent intent = new Intent(this, LeftActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out); 
	}

	// ��ת�����Ѷ
	public void StartActivities(View v) {
		Intent intent = new Intent(this, ActivitiesActivity.class);
		startActivity(intent);
	}

	// ��ת��ӳ��
	public void StartReflex(View v) {
		Intent intent = new Intent(this, ReflexActivity.class);
		startActivity(intent);
	}

	// ��ת������
	public void StartShoppingGuide(View v) {
		Intent intent = new Intent(this, ShoppingGuideActivity.class);
		startActivity(intent);
	}

	// ��ת������
	public void StartNavigation(View v) {
		Intent intent = new Intent(this, NavigationActivity.class);
		startActivity(intent);
	}

	// ��ת������
	public void StartGuide(View v) {
		Intent intent = new Intent(this, GuideActivity.class);
		startActivity(intent);
	}

	// ��ת��·���Ƽ�
	public void StartRoute(View v) {
		Intent intent = new Intent(this, RouteActivity.class);
		startActivity(intent);
	}

	// ���ݶ���
	public void StartPaoPao() {
		final ImageView spaceshipImage = (ImageView)findViewById(R.id.img_paopao);
		Animation hyperspaceJumpAnimation=AnimationUtils.loadAnimation(this, R.anim.anim_paopao);
		hyperspaceJumpAnimation.setFillAfter(true);
		hyperspaceJumpAnimation.setAnimationListener(new AnimationListener() {
			

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
				spaceshipImage.clearAnimation();
				spaceshipImage.setBackgroundResource(R.drawable.ic_paopaos);
				int left = spaceshipImage.getLeft();
				left += 200;
				int top = spaceshipImage.getTop();
				top -= 1000;
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				param.setMargins(left, top, 0, 0);
				spaceshipImage.setLayoutParams(param);
				spaceshipImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						spaceshipImage.setVisibility(View.INVISIBLE);
						
					}
				});
			}
		});
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		
//		AnimationSet
	}
	
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // ׼���˳�
			Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // ȡ���˳�
				}
			}, 2000); // ���2������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е�����
		} else {
			finish();
			System.exit(0);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // ����˫���˳�����
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}