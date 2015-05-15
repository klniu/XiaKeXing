package com.xkx.yjxm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySqlite extends SQLiteOpenHelper {
	protected static SQLiteDatabase mSQLiteDatabase;
	private final static String DATABASE_NAME = "yjxm.db";
	private final static int VERSION = 1;
	protected static MySqlite mySqlite;

	public MySqlite(Context context, String name, CursorFactory factory,
			int version) {

		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public MySqlite(Context context) {

		super(context, DATABASE_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.e("create", "create");
		// String str = "DROP TABLE alarm";
		// db.execSQL(str);
		String str = "CREATE TABLE ResInfo(SID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "ID INTEGER(4),"
				+ "title VARCHAR(30)," //����
				+ "content TEXT," //��������
				+ "bgname VARCHAR(30)," //ͼƬ����
				+ "musicname VARCHAR(30),"//��������
				+ "mid INTEGER(4),"  //mac��ַ���
				+ "edittime VARCHAR(30));"; //�޸�ʱ��


		String str2 = "CREATE TABLE MacInfo(SID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "ID INTEGER(4),"
				+ "macname VARCHAR(30)," //mac��ַ
				+ "scenicid INTEGER(4)," //����
				+ "power NUMERIC(8,3)," //����ǿ��
				+ "distance NUMERIC(8,3)," //��������
				+ "edittime VARCHAR(30));";//�޸�ʱ��
		db.execSQL(str);
		db.execSQL(str2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public SQLiteDatabase openSQLiteDatabase() {
		if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
			try {

				mSQLiteDatabase = mySqlite.getWritableDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mSQLiteDatabase;
	}

}
