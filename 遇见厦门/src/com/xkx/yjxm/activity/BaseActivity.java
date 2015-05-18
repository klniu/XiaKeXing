package com.xkx.yjxm.activity;

import java.util.Locale;

import com.xkx.yjxm.utils.PreferenceUtil;

import android.app.Activity;
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
