package com.xkx.yjxm.activity;

import java.util.Locale;

import com.xkx.yjxm.utils.PreferenceUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ʼ��PreferenceUtil
		PreferenceUtil.init(this);
		// �����ϴε��������ã�������������
		switchLanguage(PreferenceUtil.getString("language", "zh"));
		//���л����Ե��¼���,�����Ա��浽prefs��,������ҳ.
	}
	
	@Override
	protected void onRestart() {
		boolean b = PreferenceUtil.getBoolean("change", false);
		if(b)
		{
//			finish();
//			Intent intent = new Intent(this, BaseActivity.class);
//			startActivity(intent);
			Intent it = new Intent(getApplication(), MainActivity.class); //MainActivity������Ҫ������activity
	        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        getApplication().startActivity(it);
	        PreferenceUtil.commitBoolean("change", false);
		}
		super.onRestart();
	}

	/**
	 * �л���������
	 * 
	 * @param language
	 *            ָ����ʾ�����Դ���
	 */
	private void switchLanguage(String language) {
		// ����Ӧ����������
		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		if (language.equalsIgnoreCase("en")) {
			config.locale = Locale.ENGLISH;
		} else if (language.equalsIgnoreCase("zh")) {
			config.locale = Locale.SIMPLIFIED_CHINESE;
		}
		resources.updateConfiguration(config, dm);
	}
}
