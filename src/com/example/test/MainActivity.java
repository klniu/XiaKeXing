package com.example.test;



import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.view.KeyEvent;

import android.view.Menu;
import android.view.View;

import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.Toast;


public class MainActivity extends Activity   implements OnClickListener{
    private Button button5;
    private boolean isExit;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }
    private void initUI() {
    	button5 = (Button) findViewById(R.id.button5);
    	button5.setOnClickListener(this);
		
	}
    //����
    public void Backs(View v){
    	finish();
    }
    
    //��ת��������
    public void StartLeft(View v){
    	Intent intent = new Intent(this, LeftActivity.class);
    	startActivity(intent);
    }
    
    //��ת�����Ѷ
    public void StartActivities(View v){
    	Intent intent = new Intent(this, ActivitiesActivity.class);
    	startActivity(intent);
    }

    //��ת��ӳ��
    public void StartReflex (View v){
    	Intent intent = new Intent(this, ReflexActivity.class);
    	startActivity(intent);
    }

    //��ת������
    public void StartShoppingGuide(View v){
    	Intent intent = new Intent(this, ShoppingGuideActivity.class);
    	startActivity(intent);
    }

    //��ת������
    public void StartNavigation(View v){
    	Intent intent = new Intent(this, NavigationActivity.class);
    	startActivity(intent);
    }

    //��ת������
    public void StartGuide(View v){
    	Intent intent = new Intent(this, GuideActivity.class);
    	startActivity(intent);
    }
    
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) 
		{
			isExit = true; // ׼���˳�
			Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() 
			{
				@Override
				public void run() 
				{
					isExit = false; // ȡ���˳�
				}
			}, 2000); // ���2������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е�����
		} 
		else 
		{
			finish();
			System.exit(0);
		}
	} 

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK)  
    	{    
    		exitBy2Click();      //����˫���˳�����  
    	}  

    	return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button5:
			Intent intent = new Intent(this, RouteActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
    
}
  