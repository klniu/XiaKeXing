package com.xkx.yjxm.activity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkx.yjxm.R;
import com.xkx.yjxm.adpater.BaseListAdapter;
import com.xkx.yjxm.service.BLEService;
import com.xkx.yjxm.service.BLEService.BleBinder;

@SuppressLint("HandlerLeak")
public class RouteMapActivity extends Activity implements OnClickListener {
	// private Bitmap bitmap;
	int mBitmapWidth = 0;
	int mBitmapHeight = 0;
	int mArrayColor[] = null;
	int mArrayColorLengh = 0;
	private String TAG = "RouteMapActivity";
	private ImageView imageView;
	private TextView textView1;
	private TextView txtdetail;
	private SensorManager sensorManager;
	private Vibrator vibrator;
	private ImageButton imgmouth;
	private ImageButton imgplay;
	private ImageButton imgdownmouth;
	private static final int SENSOR_SHAKE = 10;
	private boolean down = false;
	private boolean openstate = false;
	private ImageButton imgswitch;
	private int soundID;
	private int currentStreamId;
	private MyAdapter myAdapter;
	// private boolean isFinishedLoad = false;
	private boolean isPausePlay = false;
	private BLEService bleService;

	private final String[][] MIME_MapTable = {
			// {��׺����MIME����}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };

	private MediaPlayer mediaPlayer;

	private boolean isOnRouteActivity = false;

	private Map<Integer, String> soundMap;
	private Map<Integer, String> listmap;
	private Map<Integer, String> textMap;
	private ListView listView1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routemap);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		initData();
		initUI();
		// bindBleScanService();

	}

	public void play(View v) {
		process(1);
	}

	private void initData() {
		listmap.put(1, "����̨");
		listmap.put(2, "��������������");
		listmap.put(3, "��л���3D�����ƽ���");
		listmap.put(4, "�ǻ�����Ӧ��չʾ��");
		listmap.put(5, "�οͽӴ�������");
		listmap.put(6, "��Ħ���������");
		listmap.put(7, "��Ʒ��Ϣ������Ļ");
		listmap.put(8, "��������Ĵ��");
		listmap.put(9, "ҽ����");
		listmap.put(10, "��������");
		listmap.put(11, "�๦�ܻ�����");
		listmap.put(12, "����");
		listmap.put(13, "Ԥ��ָ������");
		listmap.put(14, "�칫��");

	}

	private void bindBleScanService() {
		Intent service = new Intent(RouteMapActivity.this, BLEService.class);
		bindService(service, conn, BIND_AUTO_CREATE);
	}

	private void initUI() {

		imgplay = (ImageButton) findViewById(R.id.imgplay);
		imgplay.setOnClickListener(this);
		imgmouth = (ImageButton) findViewById(R.id.imgmouth);
		imgmouth.setOnClickListener(this);
		imgdownmouth = (ImageButton) findViewById(R.id.imgdown);
		imgswitch = (ImageButton) findViewById(R.id.imgswitch);
		imgswitch.setOnClickListener(this);
		txtdetail = (TextView) findViewById(R.id.txtdetail);
		listView1 = (ListView) findViewById(R.id.listView1);
		myAdapter = new MyAdapter();
		listView1.setAdapter(myAdapter);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				/*
				 * �����Դ��MediaPlayer�ĸ�ֵ��ϵ 103. * ����Դ����Ϊ������������
				 */
				mp.release();
			}
		});

		soundMap = new HashMap<Integer, String>();
		textMap = new HashMap<Integer, String>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				soundMap.put(1, "yindao.m4a");
				textMap.put(1, getResources().getString(R.string.txt_yin_dao));
				soundMap.put(2, "zi_zhu_fu_wu.m4a");
				textMap.put(2, getResources()
						.getString(R.string.txt_bo_fang_qu));
				soundMap.put(3, "tiyan3d.m4a");
				textMap.put(3, getResources().getString(R.string.txt_tiyan_3d));
				soundMap.put(4, "ying_yong_zhan_shi.m4a");
				textMap.put(4,
						getResources().getString(R.string.txt_lv_you_zhan_shi));
				soundMap.put(5, "you_ke_jie_dai.m4a");
				textMap.put(5,
						getResources().getString(R.string.txt_jie_dai_fu_wu));
				soundMap.put(6, "anmo.m4a");
				textMap.put(6, getResources().getString(R.string.txt_anmo));
				soundMap.put(7, "bo_fang_ping_mu.m4a");
				textMap.put(7,
						getResources().getString(R.string.txt_lv_you_shi_ping));
				soundMap.put(8, "xinglijicun.m4a");
				textMap.put(8,
						getResources().getString(R.string.txt_xing_li_ji_cun));
				soundMap.put(9, "yi_wu_shi.m4a");
				textMap.put(9, getResources().getString(R.string.txt_yiwu_shi));
				soundMap.put(10, "banshouli.m4a");
				textMap.put(10,
						getResources().getString(R.string.txt_ban_shou_li));
				soundMap.put(11, "duo_gong_neng.m4a");
				textMap.put(11,
						getResources().getString(R.string.txt_duo_gong_neng));
				soundMap.put(12, "ji_fang.m4a");
				textMap.put(12, getResources().getString(R.string.txt_hu_jiao));
				soundMap.put(13, "yu_jing_zhi_hui.m4a");
				textMap.put(13, getResources().getString(R.string.txt_yu_jin));
				soundMap.put(14, "bangongqu.m4a");
				textMap.put(14,
						getResources().getString(R.string.txt_ban_gong_qu));
			}
		}).start();

		// Thread.sleep(10000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		// ������������10����Ƶ������Ƶ��Ʒ��Ϊ5

	}


	private class MyAdapter extends BaseListAdapter {
		/**
		 * ������
		 */
		private class ViewHolder {

			private ImageButton img_btnplay;
			private ImageButton img_btndel;
			private TextView txtname;

		}

		private int selectItem = -1;

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listmap.size();
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

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

				// ���ý�����ɫ
				// int[] arrayOfInt = mColors;
				// int colorLength = mColors.length;
				// int selected = arrayOfInt[position % colorLength];
				//
				// convertView.setBackgroundResource(selected);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txtname.setText(listmap.get(position));
			// if (position == selectItem) { // ѡ��״̬ ����
			// convertView.setBackgroundResource(R.drawable.img_sounditem);
			//
			// } else { // ����״̬
			// convertView.setBackgroundResource(R.drawable.tabli);
			// }

			holder.img_btnplay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			holder.img_btndel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					listmap.remove(position);
					myAdapter.notifyDataSetChanged();
				}
			});
			// holder.imageView1.setBackgroundResource((Integer)
			// list.get(position).get("img"));
			// // Bitmap image =
			// Bitmap.createBitmap(((BitmapDrawable)holder.imageView1.getDrawable()).getBitmap());
			// // imgUtils.getRoundedCornerBitmap(image, 90);
			// // imageLoader.displayImage(
			// // "drawable://" + (Integer) list.get(position).get("img"),
			// // holder.imageView1, options);
			//
			// holder.txttitle.setText((String)
			// list.get(position).get("title"));
			// holder.txttime.setText((String) list.get(position).get("time"));
			// Map<String, Object> map = list.get(position); // distance
			// String object = (String) list.get(position).get("distance");
			// holder.txtdistance.setText(object);

			return convertView;
		}

	}

	/**
	 * ���ݻ�վ��ַ��������
	 * 
	 * @param address
	 */
	private void playSound(String address) {
		if (address.equalsIgnoreCase("CF:01:01:00:02:F0")) {
			// �ǻ۵��� ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F1")) {
			// ����Ĵ� ok
			playSound(8);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F2")) {
			// 3D ������ ok ???
			playSound(3);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F3")) {
			// �ǻ�����Ӧ��չʾ ok ???
			playSound(4);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F4")) {
			// ����̨ ok
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F5")) {
			// �ÿ��ϳ��� ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F6")) {
			// �ǻ��������� ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F7")) {
			// �������� ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F8")) {
			// ����������???
			playSound(6);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FC")) {
			// �������� ok
			playSound(10);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E1")) {
			// �๦���� ok
			playSound(11);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E2")) {
			// �ۺϷ����� ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E3")) {
			// �������� ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E4")) {
			// Ԥ��ָ������ ok
			playSound(13);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E5")) {
			// �칫�� ok
			playSound(14);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E6")) {
			// ��ɴ��Ӱ�� no auido
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E7")) {
			// ��Ϣ���� ??
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E8")) {
			// ���� ok
			playSound(12);
		} else {
			// TODO
		}
	}


	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (sensorManager != null) {// ȡ��������
			sensorManager.unregisterListener(sensorEventListener);
		}

		if (sensorManager != null) {// ȡ��������
			sensorManager.unregisterListener(sensorEventListener);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindBleScanService();
	}

	private void unbindBleScanService() {
		unbindService(conn);
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

	private void play() throws IOException {
		// File audioFile = new
		// File(Environment.getExternalStorageDirectory(),"");
		// mediaPlayer.reset();
		// mediaPlayer.setDataSource("drawable://" + (Integer) R.raw.yindao);
		mediaPlayer.prepare();
		mediaPlayer.start();// ����
	}

	// sound hm�еĵڼ�������
	// loop �Ƿ�ѭ�� 0��ѭ�� -1ѭ��
	public void playSound(final int sound) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				AssetManager assetMg = getApplicationContext().getAssets();
				AssetFileDescriptor fileDescriptor = null;
				try {
					fileDescriptor = assetMg.openFd(soundMap.get(sound));
					mediaPlayer.setDataSource(
							fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());

					play();// ��ʼ��ָ�����

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// // TODO Auto-generated method stub
				// try {
				// Thread.sleep(10000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// Log.e("sound", "before:" + System.currentTimeMillis());
				// pool.play(id, 1, 1, 1, 0, 1f);
				// Log.e("sound", "after:" + System.currentTimeMillis());
			}
		}).start();
	}

	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imgswitch:
			if (openstate) {
				// ȡ��ҡһҡ
				imgswitch.setBackgroundResource(R.drawable.img_autoexplain);
				if (sensorManager != null) {// ȡ��������
					sensorManager.unregisterListener(sensorEventListener);
				}
				openstate = false;
			} else {
				// ע��ҡһҡ
				imgswitch.setBackgroundResource(R.drawable.img_shake);
				if (sensorManager != null) {// ע�������
					sensorManager
							.registerListener(
									sensorEventListener,
									sensorManager
											.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
									SensorManager.SENSOR_DELAY_NORMAL);
					// ��һ��������Listener���ڶ������������ô��������ͣ�����������ֵ��ȡ��������Ϣ��Ƶ��
				}
				openstate = true;
			}
			break;
		case R.id.imgmouth:

			if (down) {
				// ����ʾ�ı�
				imgmouth.setBackgroundResource(R.drawable.ic_mouth);
				imgdownmouth.setVisibility(View.GONE);
				txtdetail.setVisibility(View.GONE);
				down = false;
			} else {
				// ��ʾ�ı�
				imgmouth.setBackgroundResource(R.drawable.img_moudown);
				imgdownmouth.setVisibility(View.VISIBLE);
				txtdetail.setVisibility(View.VISIBLE);
				down = true;
			}
			break;
		case R.id.imgplay:
			// disableViewForSeconds(imgplay);
			// ������ڲ���̬
			int mapID = 0;
			playprocess(mapID);
			break;
		default:
			break;
		}
	}

	// ��ͣ����
	private void playprocess(int mapID) {
		// tupian
		if (mediaPlayer.isPlaying()) {
			// ��ͣ����
			mediaPlayer.pause(); // ������ͣ����
			imgplay.setBackgroundResource(R.drawable.ic_play);
			isPausePlay = true;
		} else {
			if (isPausePlay) {
				mediaPlayer.start(); // ����
				imgplay.setBackgroundResource(R.drawable.ic_pause);
				isPausePlay = false;
			} else {

				process(mapID);
			}
		}
	}

	// ����
	private void process(int mapID) {
		if (mediaPlayer.isPlaying()) {
			return;
		}
		playSound(mapID);// ����dudu��dudu�ļ�������Ϊ16λ��PCM���ݺ󳬹���SoundPool��1M�������ˣ�ѭ�����ˣ����Ҳ��ܲ�����������
		imgplay.setBackgroundResource(R.drawable.ic_pause);
		// TODO ��ͼ����
		txtdetail.setText(textMap.get(mapID));
	}

	private void trigger(BluetoothDevice device) {
		String address = device.getAddress();
		if (address.equalsIgnoreCase("CF:01:01:00:02:F0")) {
			// �ǻ۵��� ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F1")) {
			// ����Ĵ� ok
			process(8);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F2")) {
			// 3D ������ ok ???
			process(3);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F3")) {
			// �ǻ�����Ӧ��չʾ ok ???
			process(4);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F4")) {
			// ����̨ ok
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F5")) {
			// �ÿ��ϳ��� ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F6")) {
			// �ǻ��������� ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F7")) {
			// �������� ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F8")) {
			// ����������???
			process(6);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FC")) {
			// �������� ok
			process(10);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E1")) {
			// �๦���� ok
			process(11);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E2")) {
			// �ۺϷ����� ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E3")) {
			// �������� ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E4")) {
			// Ԥ��ָ������ ok
			process(13);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E5")) {
			// �칫�� ok
			process(14);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E6")) {
			// TODO ��ɴ��Ӱ�� no auido
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E7")) {
			// ��Ϣ���� ??
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E8")) {
			// ���� ok
			process(12);
		} else {
			// TODO
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(RouteMapActivity.this, "ɨ�赽�����豸",
							Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	public void disableViewForSeconds(final View v) {

		v.setClickable(false);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				v.setClickable(true);

			}

		}, 2000);

	}

	/**
	 * �����ļ���׺����ö�Ӧ��MIME���͡�
	 * 
	 * @param file
	 */
	private String getMIMEType(File file) {

		String type = "*/*";
		String fName = file.getName();
		// ��ȡ��׺��ǰ�ķָ���"."��fName�е�λ�á�
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* ��ȡ�ļ��ĺ�׺�� */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// ��MIME���ļ����͵�ƥ������ҵ���Ӧ��MIME���͡�
		for (int i = 0; i < MIME_MapTable.length; i++) { // MIME_MapTable??��������һ�������ʣ����MIME_MapTable��ʲô��
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
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
			Log.i(TAG, "x�᷽����������ٶ�" + x + "��y�᷽����������ٶ�" + y + "��z�᷽����������ٶ�" + z);
			// һ����������������������ٶȴﵽ40�ʹﵽ��ҡ���ֻ���״̬��
			int medumValue = 19;// ���� i9250��ô�ζ����ᳬ��20��û�취��ֻ����19��
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
					|| Math.abs(z) > medumValue) {
				vibrator.vibrate(200);
				Message msg = new Message();
				msg.what = SENSOR_SHAKE;
				handler.sendMessage(msg);
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
			case SENSOR_SHAKE:
				processAfterShake();
				break;
			}
		}

		private void processAfterShake() {
			Toast.makeText(RouteMapActivity.this, "��⵽ҡ�Σ�ִ�в�����",
					Toast.LENGTH_SHORT).show();
			BluetoothDevice device = bleService.getProximityBleDevice();
			if (device != null) {
				trigger(device);
			}
		}
	};
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("scan", "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("scan", "onServiceConnected()");
			BleBinder binder = (BleBinder) service;
			bleService = binder.getService();
			bleService
					.setOnProximityBleChangedListener(new BLEService.OnProximityBleChangedListener() {
						public void onProximityBleChanged(
								BluetoothDevice original,
								BluetoothDevice current) {
							final BluetoothDevice curTemp = current;
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(RouteMapActivity.this,
											"cur" + curTemp.getAddress(),
											Toast.LENGTH_LONG).show();
								}
							});
						}

						public void onConditionTriggerFailed(
								BluetoothDevice device) {
							final BluetoothDevice temp = device;
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(RouteMapActivity.this,
											"�ź�ǿ�Ȳ���,�޷�����" + temp.getAddress(),
											Toast.LENGTH_LONG).show();
								}
							});
						}

						public void onConditionTriggerSuccess(
								BluetoothDevice device) {
							trigger(device);
						}

						@Override
						public void onProximityBleChanged(BluetoothDevice device) {
							// TODO Auto-generated method stub
							
						}
					});
			bleService.startScanBLE();
		}
	};
}
