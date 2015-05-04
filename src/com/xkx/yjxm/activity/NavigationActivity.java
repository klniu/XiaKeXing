package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;
import com.xkx.yjxm.custom.MySurfaceView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

//����
public class NavigationActivity extends Activity {

	private MySurfaceView mySurfaceView;
	private ImageView imageView;
	private int lefts;
	private int tops;
	private float peopleX;
	private float peopleY;
	private int bitmapWidth;
	private int bitmapHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//�Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_navigation);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int iWidth  = dm.widthPixels;
		int iHeight = dm.heightPixels;
		mySurfaceView = new MySurfaceView(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addContentView(mySurfaceView, params);
		RelativeLayout layout = new RelativeLayout(this);
		imageView = new ImageView(this);
		imageView.setBackgroundResource(R.drawable.ic_navigation_mine);
		layout.addView(imageView);
		Bitmap b = ((BitmapDrawable)imageView.getBackground()).getBitmap();
		bitmapWidth = b.getWidth();
		bitmapHeight = b.getHeight();
		//�˵ĵ�ǰ����
		peopleX = iWidth / 2 - bitmapWidth / 2;
		peopleY = iHeight / 2 - bitmapHeight;
		params.setMargins((int)peopleX, (int)peopleY, 0, 0);
		imageView.setLayoutParams(params);
		//�ѻ����ƶ����˵Ľ���
		mySurfaceView.MoveTo(iWidth / 2, iHeight / 2);
		addContentView(layout, params);
		init();
	}
	
	public void init()
	{
		imageView.postDelayed(new Runnable() {
			public void run() {
				TranslateAnimation translateAnimation = new TranslateAnimation(
			                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
			                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
				translateAnimation.setDuration(1500);  
		        // ��ʼִ�ж���   
		        imageView.startAnimation(translateAnimation);  
			}
		}, 800);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float getX = mySurfaceView.GetX();
		float getY = mySurfaceView.GetY();
		StartPeople(getX, getY);
		return super.onTouchEvent(event);
	}
	
	// ���ﶯ��
	public void StartPeople(float x, float y) {
		//��ȡ��Ļ�Ŀ��
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int iWidth  = dm.widthPixels;
		int iHeight = dm.heightPixels;
		final int runW;
		final int runH;
		runW = iWidth * 20 / 100 ;
		runH = iHeight * 55 / 100 - 50;
		final float distanceX;
		final float distanceY;
		 // ����һ��AnimationSet����   
        AnimationSet animationSet = new AnimationSet(true);  
        // j���ٲ���   
//        animationSet.setInterpolator(new AccelerateInterpolator());  
        // //����һ��AnimationSet���󵭳���ת����һ   
    	distanceX = x - peopleX;
    	distanceY = y - peopleY;
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1f);
        peopleX = x;
		peopleY = y;
        // ��alphaAnimation������ӵ�animationSet��   
        animationSet.addAnimation(translateAnimation);  
        // ��ʾ��ʱ��Ϊ1s   
        animationSet.setDuration(2000);  
        animationSet.setFillAfter(true);
        // ��ʼִ�ж���   
        imageView.startAnimation(animationSet);  
        // �����ظ��Ĵ���   
//      animationSet.setRepeatCount(4);  

//		Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_paopao);
//        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new AnimationListener() {
			

			@Override
			public void onAnimationStart(Animation animation) {
				final int left = imageView.getLeft();
				final int top = imageView.getTop();
				imageView.postDelayed(new Runnable() {
					

					public void run() {
						if(lefts == 0)
						{
							mySurfaceView.MoveTo(left, top);
							lefts = left;
							tops = top;
							Log.e("0lefts="+ lefts, "0tops="+tops+"distanceX="+distanceX+"distanceY="+distanceY);
						}
						lefts += distanceX / 10;
						tops += distanceY / 10;
						RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						param.setMargins(lefts - bitmapWidth / 2, tops - bitmapHeight, 0, 0);
						imageView.setLayoutParams(param);
						mySurfaceView.QuadTo(left, top, lefts, tops);
						Log.e("lefts="+ lefts, "tops="+tops+"distanceX="+distanceX+"distanceY="+distanceY);
						if(lefts < left + distanceX)
							imageView.postDelayed(this, 100);
						
					}
				},100);
				  
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				imageView.clearAnimation();
//				spaceshipImage.setBackgroundResource(R.drawable.ic_paopaos);
				int left = imageView.getLeft();
				left += 200;
				int top = imageView.getTop();
				top -= 1000;
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				param.setMargins((int)peopleX - bitmapWidth / 2, (int)peopleY - bitmapHeight, 0, 0);
				imageView.setLayoutParams(param);
			}
		});
		peopleX = x;
		peopleY = y;
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation, menu);
		return true;
	}

}
