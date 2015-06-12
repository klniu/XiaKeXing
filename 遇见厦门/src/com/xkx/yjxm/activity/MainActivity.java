package com.xkx.yjxm.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.xkx.yjxm.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.xkx.yjxm.R;

public class MainActivity extends BaseActivity {
	private boolean isExit;
	private ImageView im_paopao;
	private ImageView im_paopao_x;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		im_paopao = (ImageView) findViewById(R.id.img_paopao);
		im_paopao_x = (ImageView) findViewById(R.id.img_paopao_x);
		StartPaoPao();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getAction() == null) {
			finish();
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			// overridePendingTransition(0, 0);
		} else {
			// �����߼�
		}
		super.onNewIntent(intent);
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

	// ��ת����������������
	public void StartSearch(View v) {
		Intent intent = new Intent(this, SearchActivity.class);
		startActivity(intent);
	}

	// ��ת������(ѡ�����)
	public void StartGuide(View v) {

		Intent intent = new Intent(this, ChoiceActivity.class);
		intent.putExtra("�б�", false);
		// Intent intent = new Intent(this, GuideActivity.class);
		startActivity(intent);
	}

	// ��ת��·���Ƽ�(ѡ�����)
	public void StartRoute(View v) {
		Intent intent = new Intent(this, ChoiceActivity.class);
		intent.putExtra("�б�", true);
		// Intent intent = new Intent(this, RouteActivity.class);
		startActivity(intent);
	}

	// ���ݶ���
	public void StartPaoPao() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int iWidth = dm.widthPixels;
		int iHeight = dm.heightPixels;
		final int runW;
		final int runH;
		runW = iWidth * 20 / 100;
		runH = iHeight * 55 / 100 - 40;
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_paopao);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				//�ƶ�����Ļ�⣬�����Ͳ������ͼƬ����
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				param.setMargins(-100, 0, 0, 0);
				im_paopao_x.setLayoutParams(param);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				im_paopao.clearAnimation();
				im_paopao.setBackgroundResource(R.drawable.ic_paopao);
				int left = im_paopao.getLeft();
				left += runW;
				int top = im_paopao.getTop();
				top -= runH;
				RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				param1.setMargins(left, top, 0, 0);
				im_paopao.setLayoutParams(param1);
				param2.setMargins(left + im_paopao.getWidth() / 3 * 2, top, 0, 0);
				im_paopao_x.setLayoutParams(param2);
				im_paopao_x.setVisibility(View.VISIBLE);
				im_paopao.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						im_paopao.setVisibility(View.INVISIBLE);
						im_paopao_x.setVisibility(View.INVISIBLE);
						StartActivities(v);
					}
				});
				im_paopao_x.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						im_paopao.setVisibility(View.INVISIBLE);
						im_paopao_x.setVisibility(View.INVISIBLE);
					}
				});
			}
		});
		im_paopao.startAnimation(anim);

		// AnimationSet
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
