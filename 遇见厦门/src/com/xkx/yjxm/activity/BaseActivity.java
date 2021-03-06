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
		// 初始化PreferenceUtil
		PreferenceUtil.init(this);
		// 根据上次的语言设置，重新设置语言
		switchLanguage(PreferenceUtil.getString("language", "zh"));
		//在切换语言的事件下,把语言保存到prefs里,启动首页.
	}
	
	@Override
	protected void onRestart() {
		boolean b = PreferenceUtil.getBoolean("change", false);
		if(b)
		{
//			finish();
//			Intent intent = new Intent(this, BaseActivity.class);
//			startActivity(intent);
			Intent it = new Intent(getApplication(), MainActivity.class); //MainActivity是你想要重启的activity
	        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        getApplication().startActivity(it);
	        PreferenceUtil.commitBoolean("change", false);
		}
		super.onRestart();
	}

	/**
	 * 切换界面语言
	 * 
	 * @param language
	 *            指定显示的语言代码
	 */
	private void switchLanguage(String language) {
		// 设置应用语言类型
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
