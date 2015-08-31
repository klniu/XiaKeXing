package com.xkx.yjxm.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baimao.download.JsonResponse;
import com.xkx.yjxm.R;
import com.xkx.yjxm.db.MySqlite;

//ѡ�����
public class ChoiceActivity extends BaseActivity implements OnTouchListener,
OnGestureListener {
GestureDetector mGestureDetector;

	private boolean isList;
	private ImageButton imageButton3;
	private ImageButton imageButton2;
	private SQLiteDatabase mDB;
	private String Macresponse ;// MacJSON
	JSONArray Macjsonarray = new JSONArray();// MacJSONArray
	private String Resresponse;// ��ԴJSON
	JSONArray Resjsonarray = new JSONArray();// ��ԴJSONArray
	
	private static final int FLING_MIN_DISTANCE = 300;
	private static final int FLING_MIN_VELOCITY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choice);
		Macresponse = getResources().getString(R.string.macresponse);
		Resresponse = getResources().getString(R.string.response);
		mGestureDetector = new GestureDetector(this);
		// initUI();
		RelativeLayout ll = (RelativeLayout) findViewById(R.id.relay01);
		ll.setOnTouchListener(this);
		ll.setLongClickable(true);  
		Intent intent = getIntent();
		isList = intent.getBooleanExtra("�б�", true);
		insertDB();
	}
	
	// private void initUI()
	// {
	// imageButton3 =(ImageButton)findViewById(R.id.imageButton3);
	// imageButton3.setOnClickListener(this);
	// imageButton2 =(ImageButton)findViewById(R.id.imageButton2);
	// imageButton2.setOnClickListener(this);
	// }
	// ����
	public void Back(View v) {
		finish();
	}

	public void home(View v) {
		Intent intent = null;

		intent = new Intent(this, MainActivity.class);

		startActivity(intent);
	}

	// ��ת������ɽ�� ���� or ·�ߣ�
	public void StartGuanYinShan(View v) {
		Intent intent = null;
		if (isList) {
			intent = new Intent(this, RouteActivity.class);
		} else {
			intent = new Intent(this, RouteMapActivity.class);
		}
		intent.putExtra("name", "����ɽ");
		startActivity(intent);
	}

	// ��ת������ɽ��̨�� ���� or ·�ߣ�
	public void StartHuLiShan(View v) {
		// Intent intent = null;
		// if(isList)
		// {
		// intent = new Intent(this, RouteActivity.class);
		// }
		// else
		// {
		// intent = new Intent(this, RouteMapActivity.class);
		// }
		// intent.putExtra("name", "����ɽ");
		Toast.makeText(this, "��������,�����ڴ�", Toast.LENGTH_SHORT).show();
		// startActivity(intent);
	}

	// ��ת�������죨 ���� or ·�ߣ�
	public void StartGuLangYu(View v) {
		// Intent intent = null;
		// if(isList)
		// {
		// intent = new Intent(this, RouteActivity.class);
		// }
		// else
		// {
		// intent = new Intent(this, RouteMapActivity.class);
		// }
		// intent.putExtra("name", "������");
		Toast.makeText(this, "��������,�����ڴ�", Toast.LENGTH_SHORT).show();
		// startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choice, menu);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			// Fling left
			Toast.makeText(this, "�������� ", Toast.LENGTH_SHORT).show();
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			// Fling right
			finish();
		}
		return false;

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		Log.i("touch", "touch");
		return mGestureDetector.onTouchEvent(event);
	}

	// �������ݿ�
		private void insertDB() {
			// ��������
			MySqlite mySqlite = new MySqlite(this, "yjxm.db", null, 1);
			mDB = mySqlite.getReadableDatabase();
			JsonResponse resp = new JsonResponse();
			JSONObject json = resp.getjson(Macresponse);
			Macjsonarray = json.optJSONArray("result");
			
			JsonResponse resp2 = new JsonResponse();
			JSONObject json2 = resp2.getjson(Resresponse);
			Resjsonarray = json2.optJSONArray("result");
			
			mDB.beginTransaction();
			try {

				for (int i = 0; i < Macjsonarray.length(); i++) {
					String sql = "select * from MacInfo where ID=?";
					Cursor cursor = null;
					try {
						cursor = mDB.rawQuery(sql, new String[] { Macjsonarray
								.getJSONObject(i).optInt("ID") + "" });
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (cursor.getCount() == 0) {
						ContentValues values = new ContentValues();
						try {
							values.put("ID",
									Macjsonarray.getJSONObject(i).optInt("ID"));
							values.put("macName", Macjsonarray.getJSONObject(i)
									.optString("macName"));
							values.put("scenicId", Macjsonarray.getJSONObject(i)
									.optInt("scenicId"));
							values.put("power", Macjsonarray.getJSONObject(i)
									.optDouble("power"));
							values.put("distance", Macjsonarray.getJSONObject(i)
									.optDouble("distance"));
							values.put("editTime", Macjsonarray.getJSONObject(i)
									.optString("editTime"));
							mDB.insert("MacInfo", null, values);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					cursor.close();
				}
				for (int i = 0; i < Resjsonarray.length(); i++) {
					String sql = "select * from ResInfo where ID=?";
					Cursor cursor = null;
					try {
						cursor = mDB.rawQuery(sql, new String[] { Resjsonarray
								.getJSONObject(i).optInt("ID") + "" });
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (cursor.getCount() == 0) {
						ContentValues values = new ContentValues();
						try {
							values.put("ID",
									Resjsonarray.getJSONObject(i).optInt("ID"));
							values.put("title", Resjsonarray.getJSONObject(i)
									.optString("title"));
							values.put("content", Resjsonarray.getJSONObject(i)
									.optString("content"));
							values.put("bgName", Resjsonarray.getJSONObject(i)
									.optString("bgName"));
							values.put("musicName", Resjsonarray.getJSONObject(i)
									.optString("musicName"));
							values.put("mid",
									Resjsonarray.getJSONObject(i).optInt("mid"));
							values.put("editTime", Resjsonarray.getJSONObject(i)
									.optString("editTime"));
							mDB.insert("ResInfo", null, values);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					cursor.close();
				}
				// ���������־Ϊ�ɹ�������������ʱ�ͻ��ύ����
				mDB.setTransactionSuccessful();
			} catch (Exception e) {
				try {
					throw (e);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} finally {
				// ��������
				mDB.endTransaction();
			}

		}
}
