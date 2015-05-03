package com.xkx.yjxm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xkx.yjxm.R;
import com.xkx.yjxm.adpater.FastAdpater;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

//������
public class LeftActivity extends Activity implements OnItemClickListener {

	private Integer[] imgeIDs = { R.drawable.ic_left_ticket,
			R.drawable.ic_left_photo, R.drawable.ic_left_massage,
			R.drawable.ic_left_sos, R.drawable.ic_left_advice,
			R.drawable.ic_left_about, R.drawable.ic_left_setting };
	private String[] title = { "��Ʊ����", "��Ƭ��ӡ", "ȫ��Ħ", "������Ԯ", "��ѶͶ��", "��������",
			"����" };
	private List<Map<String, Object>> listItems;
	private FastAdpater FastAdpater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_left);
		ListView listView = (ListView) findViewById(R.id.left_listView1);
		listItems = getListItems();
		FastAdpater = new FastAdpater(this, listItems);
		listView.setAdapter(FastAdpater);
		listView.setOnItemClickListener(this);
	}

	public void StartMain(View v) {
		finish();
		overridePendingTransition(R.anim.anim_main_in, R.anim.anim_main_out);
		// AnimationSet animationSet = new AnimationSet(true);
		// TranslateAnimation translateAnimation = new
		// TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
		// Animation.RELATIVE_TO_SELF, 0f,Animation.RELATIVE_TO_SELF,
		// 2.0f,Animation.RELATIVE_TO_SELF, 0f);
		// translateAnimation.setDuration(2000);
		// animationSet.addAnimation(translateAnimation);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			finish();
			overridePendingTransition(R.anim.anim_main_in, R.anim.anim_main_out);
		}
		return false;
	}

	public void StartPhoto(View v) {
		Intent intent = new Intent();
		intent.setClass(this, PhotoActivity.class);
		startActivity(intent);
	}

	public void Start(View v) {
		// ͨ��������ȡҪ��ת��app������intent����

		Intent intent = getPackageManager().getLaunchIntentForPackage(
				"com.example.test");
		// Intent intent = new Intent();
		// intent.setAction("xkx");
		// intent.setComponent("com.example.test");

	}

	private List<Map<String, Object>> getListItems() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < title.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", imgeIDs[i]);
			map.put("title", title[i]);
			listItems.add(map);
		}
		return listItems;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.left, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		// Toast.makeText(getApplication(), ""+position,
		// Toast.LENGTH_SHORT).show();
		if (position == 1) {
			// ��ת����Ƭ����
			intent.setClass(getApplication(), PhotoWashActivity.class);
			startActivity(intent);
		}

		else if (position == 2) 
		{
			// ��ת����Ħ����
			// ͨ��������ȡҪ��ת��app������intent����
			intent = getPackageManager().getLaunchIntentForPackage(
					"com.ebwing.mass");
			// �������intentΪ�գ���˵��û�а�װҪ��ת��Ӧ����
			if (intent != null) 
			{
				// �����Activity���ݲ���һ�������Ҫ������ô���ݲ��������н��ղ���Ҳ�Ǹ�Activity��Activity������һ��
				intent.putExtra("name", "XiaKeXing");
				intent.putExtra("app", "123456");
				startActivity(intent);
			} 
			else 
			{
				// û�а�װҪ��ת��appӦ�ã�����һ��
//				Toast.makeText(getApplicationContext(), "Ӵ���Ͻ����ذ�װ���APP��",
//						Toast.LENGTH_LONG).show();
				intent = new Intent();      
				intent.setAction("android.intent.action.VIEW");    
	            Uri content_url = Uri.parse("http://www.ebwing.com/download/appindex.do#");   
	            intent.setData(content_url);  
	            startActivity(intent);

			}
			// intent.setAction("")
			// intent.setClass(getApplication(), MassageActivity.class);
			// startActivity(intent);
		}

		else if (position == 3) {
			// ��ת��SOS����
			intent.setClass(getApplication(), GuideActivity.class);
			startActivity(intent);
		}

	}

}
