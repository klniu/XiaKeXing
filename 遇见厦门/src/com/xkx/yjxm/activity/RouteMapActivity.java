package com.xkx.yjxm.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brtbeacon.sdk.BRTBeacon;
import com.xkx.yjxm.R;
import com.xkx.yjxm.adpater.BaseListAdapter;
import com.xkx.yjxm.bean.MacInfo;
import com.xkx.yjxm.service.AudioService;
import com.xkx.yjxm.service.AudioService.AudioBinder;
import com.xkx.yjxm.service.AudioService.OnPlayCompleteListener;
import com.xkx.yjxm.service.BleScanService;
import com.xkx.yjxm.service.BleScanService.BleBinder;
import com.xkx.yjxm.service.BleScanService.OnBleScanListener;
import com.xkx.yjxm.utils.CommonUtils;
import com.xkx.yjxm.utils.CrashHandler;

@SuppressLint({ "HandlerLeak", "DefaultLocale", "UseSparseArrays" })
public class RouteMapActivity extends BaseActivity implements OnClickListener {

	protected static final int DIMEN = 20;
	private final int ID_ZHI_HUI_DAO_LAN = 1;
	private final int ID_XING_LI_JI_CUN = 2;
	private final int ID_3D_HU_DONG = 3;
	private final int ID_YING_YONG_ZHAN_SHI = 4;
	private final int ID_YIN_DAO_TAI = 5;
	private final int ID_LV_KE_SHANG_CHE = 6;
	private final int ID_ZHI_HUI_LV_YOU_SHI_PING = 7;
	private final int ID_DAN_CHE_ZU_LIN = 8;
	private final int ID_XIU_XIAN_ZI_ZHU = 9;
	private final int ID_BAN_SHOU_LI_CHAO_SHI = 10;
	private final int ID_DUO_GONG_NENG_TING = 11;
	private final int ID_ZONG_HE_FU_WU_QU = 12;
	private final int ID_HU_JIAO_ZHONG_XIN = 13;
	private final int ID_YU_JING_ZHI_HUI_ZHONG_XIN = 14;
	private final int ID_BAN_GONG_QU = 15;
	private final int ID_YI_WU_SHI = 16;
	private final int ID_XIN_XI_SHI_PING = 17;
	private final int ID_JI_FNAG = 18;
	private final int ID_HUN_SHA_SHE_YING = 19;

	private final int MSG_SENSOR_SHAKE = 10;

	private TextView textView1;
	/** ��˵�� */
	private TextView tvContent;
	private SensorManager sensorManager;
	private Vibrator vibrator;
	private ImageButton imgmouth;
	private ImageButton imgplay;
	/** ��ͷ��˵�� */
	private ImageButton imgdownmouth;

	private String TAG = "RouteMapActivity";
	private boolean isPlaying = false;
	private boolean down = false;
	/**
	 * �Զ������Ƿ���
	 */
	private boolean isAuto = true;
	private long lastTriggerTime;

	private TextView tvTitle;
	private ImageButton imgswitch;
	private ImageButton btnback;
	private ImageView ivMap;
	private ListView listView1;

	private Map<Integer, String> xMap = new HashMap<Integer, String>();
	private Map<Integer, String> yMap = new HashMap<Integer, String>();

	private HashMap<Integer, InnerResInfo> resMap = new HashMap<Integer, InnerResInfo>();// ��Դ

	private HashMap<String, MacInfo> macMap = new HashMap<String, MacInfo>();// mac��ַ
	private CopyOnWriteArrayList<ItemData> titleList = new CopyOnWriteArrayList<ItemData>();
	private HashMap<Integer, Long> idTriggerTimeMap = new HashMap<Integer, Long>();

	private BaseAdapter adapter;

	private RelativeLayout soundlay;

	private SQLiteDatabase mDB;
	private LinearLayout bluetoothlay;
	private Button open_btn;
	private Button close_btn;

	// /**
	// * �洢��Ƶ��ַ
	// */
	// ArrayList<String> listImgPath;
	// String[] imageUriArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routemap);
		bindBleScanService();
		bindAudioService();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		if (sensorManager != null) {// ע�������
			sensorManager.registerListener(sensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_UI);
			// ��һ��������Listener���ڶ������������ô��������ͣ�����������ֵ��ȡ��������Ϣ��Ƶ��
		}
		try {
			initData();
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
		}
		initUI();
	}

	public void hidesoundlay(View v) {
		isPlaying = false;
		audioBinder.audioStop();
		soundlay.setVisibility(View.GONE);
	}

	public void home(View v) {
		Intent intent = null;
		intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private void bindAudioService() {
		Intent service = new Intent(RouteMapActivity.this, AudioService.class);
		bindService(service, audioConn, BIND_AUTO_CREATE);
	}

	private void bindBleScanService() {
		Intent service = new Intent(RouteMapActivity.this, BleScanService.class);
		bindService(service, conn, BIND_AUTO_CREATE);
	}

	private void initUI() {
		soundlay = (RelativeLayout) findViewById(R.id.soundlay);
		imgplay = (ImageButton) findViewById(R.id.imgplay);
		imgplay.setOnClickListener(this);
		imgmouth = (ImageButton) findViewById(R.id.imgmouth);
		imgmouth.setOnClickListener(this);
		imgdownmouth = (ImageButton) findViewById(R.id.imgdown);
		imgswitch = (ImageButton) findViewById(R.id.imgswitch);
		imgswitch.setOnClickListener(this);
		imgplay.setEnabled(false);
		tvContent = (TextView) findViewById(R.id.txtdetail);
		listView1 = (ListView) findViewById(R.id.listView1);
		ivMap = (ImageView) findViewById(R.id.iv_map);
		textView1 = (TextView) findViewById(R.id.textView1);
		tvTitle = (TextView) findViewById(R.id.txt_ti);
		btnback = (ImageButton) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
		bluetoothlay = (LinearLayout) findViewById(R.id.bluetoothlay);
		// �����ѿ�������ʾ����
		try {
			if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
				bluetoothlay.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		open_btn = (Button) findViewById(R.id.open_btn);
		open_btn.setOnClickListener(this);
		close_btn = (Button) findViewById(R.id.close_btn);
		close_btn.setOnClickListener(this);
		initXYMap();
		setIvMap();
		adapter = new MyAdapter();
		listView1.setAdapter(adapter);

	}

	/**
	 * 
	 * ��ȡͼƬ��ַ�б�
	 * 
	 * @return list
	 */
	private void getResource() {
		// Cursor resCursor = mDB.query("ResInfo", null, null, null, null, null,
		// null);
		// Cursor macCursor = mDB.query("MacInfo", null, null, null, null, null,
		// null);
		// boolean hasNext = resCursor.moveToFirst();
		// TODO
		// while (hasNext) {
		// ResInfo rs = new ResInfo(resCursor.getString(resCursor
		// .getColumnIndex("title")), resCursor.getString(resCursor
		// .getColumnIndex("content")), resCursor.getString(resCursor
		// .getColumnIndex("bgName")), resCursor.getString(resCursor
		// .getColumnIndex("musicName")));
		// // ����Դ�ļ���ӵ�list��
		// resMap.put(resCursor.getInt(resCursor.getColumnIndex("mid")), rs);
		// hasNext = resCursor.moveToNext();
		// }
		// resCursor.close();
		//
		// hasNext = macCursor.moveToFirst();
		// TODO
		// while (hasNext) {
		// MacInfo rs = new MacInfo(macCursor.getInt(macCursor
		// .getColumnIndex("ID")), macCursor.getString(macCursor
		// .getColumnIndex("macName")), macCursor.getFloat(macCursor
		// .getColumnIndex("power")), macCursor.getFloat(macCursor
		// .getColumnIndex("distance")));
		// // ��mac��Ϣ��ӵ�list��
		// macMap.put(
		// macCursor.getString(macCursor.getColumnIndex("macName")),
		// rs);
		// hasNext = macCursor.moveToNext();
		// }
		// macCursor.close();

		String[] titles = getResources().getStringArray(R.array.title);
		String[] contents = getResources().getStringArray(R.array.content);
		int[] bgnames = new int[titles.length];
		int[] musics = new int[titles.length];
		TypedArray tamap = getResources().obtainTypedArray(R.array.img_map_bg);
		TypedArray tamusic = getResources().obtainTypedArray(R.array.musicid);
		for (int i = 0; i < tamap.length(); i++) {
			bgnames[i] = tamap.getResourceId(i, 0);
			musics[i] = tamusic.getResourceId(i, 0);
		}
		tamap.recycle();
		tamusic.recycle();

		String[] macs = getResources().getStringArray(R.array.mac);
		resMap.clear();
		macMap.clear();
		for (int j = 0; j < musics.length; j++) {
			resMap.put(j, new InnerResInfo(titles[j], contents[j], bgnames[j],
					musics[j]));
			//TODO
			macMap.put(macs[j], new MacInfo(j, macs[j], -70f, 2.0f));
		}
	}

	/**
	 * 
	 * ��ȡ��Ƶ��ַ�б�
	 * 
	 * @return list
	 */
	private ArrayList<String> getSoundPathList() {
		ArrayList<String> list = new ArrayList<String>();
		// ��ȡSD��·��
		String path = Environment.getExternalStorageDirectory()
				+ "/resource/muisc";
		File file = new File(path);
		// ���SD��Ŀ¼�����ڴ���
		if (!file.exists()) {
			file.mkdir();
		}
		File[] file2 = file.listFiles();
		for (int i = 0; i < file2.length; i++) {
			if (file2[i].isDirectory()) {
			} else {
				list.add(file2[i].getAbsolutePath());// ��ͼƬ·����ӵ�list��
			}
		}
		return list;
	}

	private void initData() {
		// ɨ���ڴ���ͼƬ������list
		// MySqlite mySqlite = new MySqlite(RouteMapActivity.this, "yjxm.db",
		// null, 1);
		// mDB = mySqlite.getReadableDatabase();
		getResource();
		for (int id = 0; id < resMap.size(); id++) {
			idTriggerTimeMap.put(id, 0l);
		}
	}

	/**
	 * ���½���
	 * 
	 * @param id
	 *            ���
	 * @param play
	 *            �Ƿ񲥷�
	 */
	private void updateHead(final int id, final boolean play) {
		runOnUiThread(new Runnable() {
			public void run() {
				String title = resMap.get(id).title;
				String content = resMap.get(id).content;
				if (play) {
					imgplay.setBackgroundResource(R.drawable.ic_pause);
				} else {
					imgplay.setBackgroundResource(R.drawable.ic_play);
				}
				imgplay.setEnabled(true);
				tvTitle.setText(title);
				tvContent.setText(content);
				tvContent.setVisibility(View.VISIBLE);

				// ��ȡSD��·��
				// String path = Environment.getExternalStorageDirectory()
				// + "/resource/map/";
				// BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inSampleSize = 2;
				// Bitmap bm = BitmapFactory.decodeFile(path
				// + resMap.get(id).getBgname(), options);
				// ivMap.setImageBitmap(bm);
				ivMap.setImageResource(resMap.get(id).bgname);
				imgdownmouth.setVisibility(View.VISIBLE);
			}
		});
	}

	private void trigger(BRTBeacon beacon) {
		if (System.currentTimeMillis() - lastTriggerTime < 2000) {
			return;
		}
		try {
			String address = beacon.macAddress.trim();
			if (macMap.containsKey(address)) {
				MacInfo macInfo = macMap.get(address);
				final int id = macInfo.getID();
				if (beacon.rssi < macInfo.getPower()) {// �˴��ǹ���
					return;
				}
				long idLastTriggerTime;
				try {
					idLastTriggerTime = idTriggerTimeMap.get(id);
				} catch (Exception e) {
					idLastTriggerTime = 0l;
					idTriggerTimeMap.put(id, idLastTriggerTime);
					CrashHandler.getInstance().logToFile(e);
				}
				if (isAuto
						&& System.currentTimeMillis() - idLastTriggerTime < 60 * 1000) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if (resMap.containsKey(id)) {
							String title = resMap.get(id).title;
							ItemData data = new ItemData(title, id);
							lastTriggerTime = System.currentTimeMillis();
							idTriggerTimeMap.put(id, lastTriggerTime);
							if (isPlaying) {
								boolean contains = titleList
										.contains(new ItemData("", id));
								if (!contains && titleList.size() < 3) {
									titleList.add(0, data);
									adapter.notifyDataSetChanged();
								}
							} else {
								processPlay(id, true);
							}
						} else {
							// TODO û�����ݵĻ�վ
						}
					}
				});
			} else {
				// TODO ��⵽û�����ݵĻ�վ
			}
		} catch (Exception e) {
			CrashHandler.getInstance().logToFile(e);
		}
	}

	private class MyAdapter extends BaseListAdapter {
		private class ViewHolder {
			private ImageButton img_btnplay;
			private ImageButton img_btndel;
			private TextView txtname;
		}

		@Override
		public int getCount() {
			return titleList.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_sounditem, null);
				holder = new ViewHolder();
				holder.img_btnplay = (ImageButton) convertView
						.findViewById(R.id.img_btnplay);
				holder.img_btndel = (ImageButton) convertView
						.findViewById(R.id.img_btndel);
				holder.txtname = (TextView) convertView
						.findViewById(R.id.txtname);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txtname.setText(titleList.get(position).title);
			holder.img_btnplay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!CommonUtils.isFastDoubleClick()) {
						ItemData data = titleList.get(position);
						processPlay(data.id, true);
					}
				}
			});
			holder.img_btndel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					titleList.remove(position);
					adapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}

	private class ItemData {
		public String title;
		public int id;

		public ItemData(String title, int id) {
			super();
			this.title = title;
			this.id = id;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ItemData other = (ItemData) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id != other.id)
				return false;
			return true;
		}

		private RouteMapActivity getOuterType() {
			return RouteMapActivity.this;
		}

	}

	private String getTitle(String address) {
		address = address.trim();
		if (address.equalsIgnoreCase("CF:01:01:00:02:F0")) {
			return "�ǻ۵���";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F1")) {
			return "����Ĵ�";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F2")) {
			return "3D����";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F3")) {
			return "Ӧ��չʾ";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F4")) {
			return "����̨";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FB")) {
			return "�ÿ��ϳ���";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F6")) {
			return "�ǻ���������";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F7")) {
			return "���г�����";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F8")) {
			return "��������";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FC")) {
			return "��������";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E1")) {
			return "�๦����";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E2")) {
			return "�ۺϷ�����";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E4")) {
			return "��������";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E7")) {
			return "Ԥ��ָ������";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E5")) {
			return "�칫��1";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E6")) {
			return "ҽ����";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F5")) {
			return "��Ϣ����";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E8")) {
			return "����";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FD")) {
			return "��ɴ��Ӱ";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E3")) {
			return "�칫��2";
		}
		return address;
	}

	private int getId(String address) {
		address = address.trim();
		if (address.equalsIgnoreCase("CF:01:01:00:02:F0")) {
			return ID_ZHI_HUI_DAO_LAN;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F1")) {
			return ID_XING_LI_JI_CUN;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F2")) {
			return ID_3D_HU_DONG;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F3")) {
			return ID_YING_YONG_ZHAN_SHI;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F4")) {
			return ID_YIN_DAO_TAI;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FB")) {
			return ID_LV_KE_SHANG_CHE;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F6")) {
			return ID_ZHI_HUI_LV_YOU_SHI_PING;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F7")) {
			return ID_DAN_CHE_ZU_LIN;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F8")) {
			return ID_XIU_XIAN_ZI_ZHU;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FC")) {
			return ID_BAN_SHOU_LI_CHAO_SHI;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E1")) {
			return ID_DUO_GONG_NENG_TING;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E2")) {
			return ID_ZONG_HE_FU_WU_QU;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E4")) {
			return ID_HU_JIAO_ZHONG_XIN;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E7")) {
			return ID_YU_JING_ZHI_HUI_ZHONG_XIN;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E5")) {
			return ID_BAN_GONG_QU;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E6")) {
			return ID_YI_WU_SHI;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F5")) {
			return ID_XIN_XI_SHI_PING;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E8")) {
			return ID_JI_FNAG;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FD")) {
			return ID_HUN_SHA_SHE_YING;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E3")) {
			return ID_BAN_GONG_QU;
		}
		return -1;
	}

	private void processPlay(int id, boolean play) {
		// ��ȡSD��·��
		String path = Environment.getExternalStorageDirectory()
				+ "/resource/music/";
		// Uri uri = Uri.parse(path + resMap.get(id).getMusicname());
		Uri uri = Uri.parse("android.resource://" + getPackageName() + "/"
				+ resMap.get(id).musicname);
		if (uri == null) {
			CrashHandler.getInstance().logStringToFile("uri not exist");
			return;
		}
		soundlay.setVisibility(View.VISIBLE);
		// getSoundPathList()
		isPlaying = true;
		audioBinder.audioPlay(uri);
		updateHead(id, play);
	}

	private Uri getUri(int id) {
		Uri uri = null;
		String parent = "android.resource://com.xkx.yjxm/";
		if (id == ID_BAN_GONG_QU) {
			uri = Uri.parse(parent + R.raw.ban_gong_qu);
		} else if (id == ID_BAN_SHOU_LI_CHAO_SHI) {
			uri = Uri.parse(parent + R.raw.ban_shou_li_chao_shi);
		} else if (id == ID_DUO_GONG_NENG_TING) {
			uri = Uri.parse(parent + R.raw.duo_gong_neng_ting);
		} else if (id == ID_3D_HU_DONG) {
			uri = Uri.parse(parent + R.raw.hu_dong_qu);
		} else if (id == ID_HU_JIAO_ZHONG_XIN) {
			uri = Uri.parse(parent + R.raw.hu_jiao_zhong_xin);
		} else if (id == ID_HUN_SHA_SHE_YING) {
			uri = Uri.parse(parent + R.raw.hun_sha_she_ying);
		} else if (id == ID_JI_FNAG) {
			uri = Uri.parse(parent + R.raw.ji_fang);
		} else if (id == ID_ZHI_HUI_LV_YOU_SHI_PING) {
			uri = Uri.parse(parent + R.raw.lv_you_zhi_hui_ping);
		} else if (id == ID_XIU_XIAN_ZI_ZHU) {
			uri = Uri.parse(parent + R.raw.lv_you_zi_zhu_fu_wu_qu);
		} else if (id == ID_XIN_XI_SHI_PING) {
			uri = Uri.parse(parent + R.raw.xin_xi_bo_fang_ping);
		} else if (id == ID_XING_LI_JI_CUN) {
			uri = Uri.parse(parent + R.raw.xing_li_ji_cun_qu);
		} else if (id == ID_YI_WU_SHI) {
			uri = Uri.parse(parent + R.raw.yi_wu_shi);
		} else if (id == ID_YIN_DAO_TAI) {
			uri = Uri.parse(parent + R.raw.yin_dao_tai);
		} else if (id == ID_YING_YONG_ZHAN_SHI) {
			uri = Uri.parse(parent + R.raw.ying_yong_zhan_shi_qu);
		} else if (id == ID_LV_KE_SHANG_CHE) {
			uri = Uri.parse(parent + R.raw.you_ke_shang_che_qu);
		} else if (id == ID_YU_JING_ZHI_HUI_ZHONG_XIN) {
			uri = Uri.parse(parent + R.raw.yu_jing_zhong_xin);
		} else if (id == ID_DAN_CHE_ZU_LIN) {
			uri = Uri.parse(parent + R.raw.zi_xing_che_zu_lin);
		} else if (id == ID_ZONG_HE_FU_WU_QU) {
			uri = Uri.parse(parent + R.raw.zong_he_fu_wu_qu);
		} else if (id == ID_ZHI_HUI_DAO_LAN) {
			uri = Uri.parse(parent + R.raw.zhi_hui_dao_lan);
		}
		return uri;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindAllService();
		sensorManager.unregisterListener(sensorEventListener);
	}

	private void unbindAllService() {
		unbindService(conn);
		unbindService(audioConn);
	}

	private static Bitmap big(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(2.0f, 1.85f); // ���Ϳ�Ŵ���С�ı���
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	private static Bitmap small(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(0.8f, 0.8f); // ���Ϳ�Ŵ���С�ı���
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	private long btnLastClk;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgswitch:
			if (System.currentTimeMillis() - btnLastClk < 2000) {
				return;
			}
			btnLastClk = System.currentTimeMillis();
			isAuto = !isAuto;
			if (isAuto) {
				// �Զ�����
				Toast.makeText(this, "���潫������������λ��Ϊ�������Զ������������",
						Toast.LENGTH_SHORT).show();
				imgswitch.setBackgroundResource(R.drawable.img_autoexplain);
				// sensorManager.unregisterListener(sensorEventListener);
			} else {
				// ҡһҡ
				Toast.makeText(this, "�ڲ�ͬλ��ҡһҡ�����µķ��֣��������԰ɡ�",
						Toast.LENGTH_SHORT).show();
				imgswitch.setBackgroundResource(R.drawable.img_shake);
				// if (sensorManager != null) {// ע�������
				// sensorManager
				// .registerListener(
				// sensorEventListener,
				// sensorManager
				// .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				// SensorManager.SENSOR_DELAY_NORMAL);
				// // ��һ��������Listener���ڶ������������ô��������ͣ�����������ֵ��ȡ��������Ϣ��Ƶ��
				// }
			}
			break;
		case R.id.imgmouth:
			if (down) {
				// ����ʾ�ı�
				imgmouth.setBackgroundResource(R.drawable.ic_mouth);
				imgdownmouth.setVisibility(View.GONE);
				tvContent.setVisibility(View.GONE);
				down = false;
			} else {
				// ��ʾ�ı�
				imgmouth.setBackgroundResource(R.drawable.img_moudown);
				imgdownmouth.setVisibility(View.VISIBLE);
				tvContent.setVisibility(View.VISIBLE);
				down = true;
			}
			break;
		case R.id.imgplay:
			if (CommonUtils.isFastDoubleClick()) {
				return;
			}
			isPlaying = !isPlaying;
			if (isPlaying) {
				imgplay.setBackgroundResource(R.drawable.ic_pause);
				audioBinder.audioStart();
			} else {
				imgplay.setBackgroundResource(R.drawable.ic_play);
				audioBinder.audioPause();
			}
			break;
		case R.id.btnback:
			finish();
			break;
		case R.id.open_btn:
			BluetoothAdapter.getDefaultAdapter().enable();
			bluetoothlay.setVisibility(View.GONE);
			// �Զ�����
			Toast.makeText(this, "���潫������������λ��Ϊ�������Զ������������", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.close_btn:
			bluetoothlay.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	/**
	 * ������Ӧ����
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// ��������Ϣ�ı�ʱִ�и÷���
			float[] values = event.values;
			float x = values[0]; // x�᷽����������ٶȣ�����Ϊ��
			float y = values[1]; // y�᷽����������ٶȣ���ǰΪ��
			float z = values[2]; // z�᷽����������ٶȣ�����Ϊ��
			// һ����������������������ٶȴﵽ40�ʹﵽ��ҡ���ֻ���״̬��
			if (Math.abs(x) > 15 || Math.abs(y) > 15 || Math.abs(z) > 15) {
				Log.e(TAG, "x��" + x + "��y��" + y + "��z��" + z);
			}
			Log.i(TAG, "x��" + x + "��y��" + y + "��z��" + z);
			int medumValue = 19;// ���� i9250��ô�ζ����ᳬ��20��û�취��ֻ����19��
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
					|| Math.abs(z) > medumValue) {
				if (!isAuto) {
					vibrator.vibrate(200);
					Message msg = new Message();
					msg.what = MSG_SENSOR_SHAKE;
					handler.sendMessage(msg);
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	/**
	 * ����ִ��
	 */
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SENSOR_SHAKE:
				processAfterShake();
				break;
			}
		}

		private void processAfterShake() {
			// BluetoothDevice device = bleService.getProximityBleDevice();
			BRTBeacon beacon = bleBinder.getProximityBeacon();
			if (beacon != null) {
				trigger(beacon);
			}
		}
	};

	private AudioBinder audioBinder;

	private ServiceConnection audioConn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			audioBinder = (AudioBinder) service;
			audioBinder.setOnPlayCompleteListener(new OnPlayCompleteListener() {

				@Override
				public void onPlayComplete() {
					runOnUiThread(new Runnable() {
						public void run() {
							isPlaying = false;
							imgplay.setBackgroundResource(R.drawable.ic_play);
							tvContent.setVisibility(View.INVISIBLE);
							imgdownmouth.setVisibility(View.INVISIBLE);
						}
					});
				}
			});
			if (audioBinder == null) {
				CrashHandler.getInstance().logToFile(
						new Exception("audioBinder null"));
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	private BleBinder bleBinder;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("scan", "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("scan", "onServiceConnected");
			bleBinder = (BleBinder) service;

			bleBinder.setRegion(null);// �մ���ɨ������
			bleBinder.setMacMap(macMap);
			bleBinder.setOnBleScanListener(new OnBleScanListener() {

				@Override
				public void onPeriodScan(List<BRTBeacon> scanResultList) {
				}

				@Override
				public void onNearBleChanged(BRTBeacon oriBeacon,
						BRTBeacon desBeacon) {
					Log.e("scan", "onNearBleChanged,current:"
							+ desBeacon.macAddress);
				}

				@Override
				public void onNearBeacon(BRTBeacon brtBeacon) {
					if (isAuto) {
						trigger(brtBeacon);
					}
				}
			});
		}
	};

	private void initXYMap() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				xMap.put(1, getResources().getString(R.string.point1x));
				yMap.put(1, getResources().getString(R.string.point1y));
				xMap.put(2, getResources().getString(R.string.point2x));
				yMap.put(2, getResources().getString(R.string.point2y));
				xMap.put(3, getResources().getString(R.string.point3x));
				yMap.put(3, getResources().getString(R.string.point3y));
				xMap.put(4, getResources().getString(R.string.point4x));
				yMap.put(4, getResources().getString(R.string.point4y));
				xMap.put(5, getResources().getString(R.string.point5x));
				yMap.put(5, getResources().getString(R.string.point5y));
				xMap.put(6, getResources().getString(R.string.point6x));
				yMap.put(6, getResources().getString(R.string.point6y));
				xMap.put(7, getResources().getString(R.string.point7x));
				yMap.put(7, getResources().getString(R.string.point7y));
				xMap.put(8, getResources().getString(R.string.point8x));
				yMap.put(8, getResources().getString(R.string.point8y));
				xMap.put(9, getResources().getString(R.string.point9x));
				yMap.put(9, getResources().getString(R.string.point9y));
				xMap.put(10, getResources().getString(R.string.point10x));
				yMap.put(10, getResources().getString(R.string.point10y));
				xMap.put(11, getResources().getString(R.string.point11x));
				yMap.put(11, getResources().getString(R.string.point11y));
			}
		}).start();
	}

	private void setIvMap() {

		ivMap.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				if (event.getAction() == MotionEvent.ACTION_UP) {

					String title = "";

					if (x >= Integer.parseInt(xMap.get(2)) - 20
							&& x <= Integer.parseInt(xMap.get(2)) + 20
							&& y >= Integer.parseInt(yMap.get(2)) - 20
							&& y <= Integer.parseInt(yMap.get(2)) + 20) {

						processPlay(5, true);
						title = "����̨";
					}
					if (x >= Integer.parseInt(xMap.get(3)) - 20
							&& x <= Integer.parseInt(xMap.get(3)) + 20
							&& y >= Integer.parseInt(yMap.get(3)) - 20
							&& y <= Integer.parseInt(yMap.get(3)) + 20) {

						processPlay(4, true);
						title = "�ǻ�����Ӧ��չʾ��";
					}
					if (x >= Integer.parseInt(xMap.get(4)) - 20
							&& x <= Integer.parseInt(xMap.get(4)) + 20
							&& y >= Integer.parseInt(yMap.get(4)) - 20
							&& y <= Integer.parseInt(yMap.get(4)) + 20) {

						processPlay(17, true);
						title = "��Ʒ��Ϣ������Ļ";
					}

					if (x >= Integer.parseInt(xMap.get(5)) - 20
							&& x <= Integer.parseInt(xMap.get(5)) + 20
							&& y >= Integer.parseInt(yMap.get(5)) - 20
							&& y <= Integer.parseInt(yMap.get(5)) + 20) {

						processPlay(12, true);
						title = "�ۺϷ�����";
					}

					if (x >= Integer.parseInt(xMap.get(6)) - 20
							&& x <= Integer.parseInt(xMap.get(6)) + 20
							&& y >= Integer.parseInt(yMap.get(6)) - 20
							&& y <= Integer.parseInt(yMap.get(6)) + 20) {

						processPlay(3, true);
						title = "�л���3D�����ƽ���";
					}

					if (x >= Integer.parseInt(xMap.get(8)) - 20
							&& x <= Integer.parseInt(xMap.get(8)) + 20
							&& y >= Integer.parseInt(yMap.get(8)) - 20
							&& y <= Integer.parseInt(yMap.get(8)) + 20) {

						processPlay(2, true);
						title = "��������Ĵ��";
					}

					if (x >= Integer.parseInt(xMap.get(9)) - 20
							&& x <= Integer.parseInt(xMap.get(9)) + 20
							&& y >= Integer.parseInt(yMap.get(9)) - 20
							&& y <= Integer.parseInt(yMap.get(9)) + 20) {

						processPlay(9, true);
						title = "��Ħ���������";
					}

					if (x >= Integer.parseInt(xMap.get(10)) - 20
							&& x <= Integer.parseInt(xMap.get(10)) + 20
							&& y >= Integer.parseInt(yMap.get(10)) - 20
							&& y <= Integer.parseInt(yMap.get(10)) + 20) {

						processPlay(16, true);
						title = "ҽ����";
					}
					if (x >= Integer.parseInt(xMap.get(11)) - 20
							&& x <= Integer.parseInt(xMap.get(11)) + 20
							&& y >= Integer.parseInt(yMap.get(11)) - 20
							&& y <= Integer.parseInt(yMap.get(11)) + 20) {

						processPlay(10, true);
						title = "��������";
					}
					// if (event.getX() >= Integer.parseInt(xMap.get(2))
					// && event.getY() < Integer.parseInt(xMap.get(2))) {
					//
					// mapID = 1;
					// title = "����̨";
					// process(mapID, title);
					// }

				}
				return true;
			}
		});
	}

	class InnerResInfo {
		String title;
		String content;
		int bgname;
		int musicname;

		public InnerResInfo(String title, String content, int bgname,
				int musicname) {
			this.title = title;
			this.content = content;
			this.bgname = bgname;
			this.musicname = musicname;
		}
	}
}
