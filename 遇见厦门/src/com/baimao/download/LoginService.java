package com.baimao.download;

import java.util.HashMap;

import org.json.JSONObject;

import android.database.sqlite.SQLiteDatabase;

public class LoginService {
	private static SQLiteDatabase mDB;

	/**
	 * ���ص�¼��Ϣ
	 * 
	 * @return �Ƽ�¥���б�
	 */
	public JSONObject inquireUserList(String url, HashMap<String, String> parmes) {

		HttpService http = new HttpService(url);

		http.addParameter(parmes);
		JSONObject response = http.fetchJson();

		// if(response.isOk())
		// userinfo = response.parseData(UserInfo.class);
		// else
		// userinfo = null;
		return response;
	}
	/**
	 *  ����ǩ����Ϣ
	 * 
	 * @return 
	 */
	public JSONObject inquireSignList(String url, HashMap<String, String> parmes) {

		HttpService http = new HttpService(url);

		http.addParameter(parmes);
		JSONObject response = http.fetchJson();

		// if(response.isOk())
		// userinfo = response.parseData(UserInfo.class);
		// else
		// userinfo = null;
		return response;
	}
	
	
}
