package com.xkx.yjxm.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkx.yjxm.R;
import com.xkx.yjxm.service.BLEService;
import com.xkx.yjxm.service.BLEService.BleBinder;

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
	private boolean playstate = false;
	private boolean openstate = false;
	private ImageButton imgswitch;
	private int soundID;
	private int currentStreamId;
	private boolean isFinishedLoad = false;
	private boolean isPausePlay = false;
	private BLEService bleService;

	private final String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
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

	SoundPool mSoundPool = null;

	private boolean isOnRouteActivity = false;

	private Map<Integer, Integer> soundMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routemap);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		initUI();
		bindBleScanService();

		// 设置最多可容纳10个音频流，音频的品质为5

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

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);

		mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				// TODO Auto-generated method stub
				isFinishedLoad = true;
				System.out.println("setOnLoadCompleteListener");
			}
		});
		soundMap = new HashMap<Integer, Integer>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				soundMap.put(1,
						mSoundPool.load(RouteMapActivity.this, R.raw.yindao, 1));
				soundMap.put(2, mSoundPool.load(RouteMapActivity.this,
						R.raw.zi_zhu_fu_wu, 1));
				soundMap.put(3, mSoundPool.load(RouteMapActivity.this,
						R.raw.tiyan3d, 1));
				soundMap.put(4, mSoundPool.load(RouteMapActivity.this,
						R.raw.ying_yong_zhan_shi, 1));
				soundMap.put(5, mSoundPool.load(RouteMapActivity.this,
						R.raw.you_ke_jie_dai, 1));
				soundMap.put(6,
						mSoundPool.load(RouteMapActivity.this, R.raw.anmo, 1));
				soundMap.put(7, mSoundPool.load(RouteMapActivity.this,
						R.raw.bo_fang_ping_mu, 1));
				soundMap.put(8, mSoundPool.load(RouteMapActivity.this,
						R.raw.xinglijicun, 1));
				soundMap.put(9, mSoundPool.load(RouteMapActivity.this,
						R.raw.yi_wu_shi, 1));
				soundMap.put(10, mSoundPool.load(RouteMapActivity.this,
						R.raw.banshouli, 1));
				soundMap.put(11, mSoundPool.load(RouteMapActivity.this,
						R.raw.duo_gong_neng, 1));
				soundMap.put(12, mSoundPool.load(RouteMapActivity.this,
						R.raw.ji_fang, 1));
				soundMap.put(13, mSoundPool.load(RouteMapActivity.this,
						R.raw.yu_jing_zhi_hui, 1));
				soundMap.put(14, mSoundPool.load(RouteMapActivity.this,
						R.raw.bangongqu, 1));
			}
		}).start();

		// Thread.sleep(10000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		// 设置最多可容纳10个音频流，音频的品质为5

	}

	/**
	 * 根据基站地址播放声音
	 * 
	 * @param address
	 */
	private void playSound(String address) {
		if (address.equalsIgnoreCase("CF:01:01:00:02:F0")) {
			// 智慧导览 ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F1")) {
			// 行李寄存 ok
			playSound(8);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F2")) {
			// 3D 互动区 ok ???
			playSound(3);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F3")) {
			// 智慧旅游应用展示 ok ???
			playSound(4);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F4")) {
			// 引导台 ok
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F5")) {
			// 旅客上车处 ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F6")) {
			// 智慧旅游视屏 ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F7")) {
			// 单车租赁 ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F8")) {
			// 休闲自助区???
			playSound(6);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FC")) {
			// 伴手礼超市 ok
			playSound(10);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E1")) {
			// 多功能厅 ok
			playSound(11);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E2")) {
			// 综合服务区 ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E3")) {
			// 呼叫中心 ???
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E4")) {
			// 预警指挥中心 ok
			playSound(13);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E5")) {
			// 办公区 ok
			playSound(14);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E6")) {
			// 婚纱摄影区 no auido
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E7")) {
			// 信息视屏 ??
			playSound(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E8")) {
			// 机房 ok
			playSound(12);
		} else {
			// TODO
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// imageView.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// int x = (int) event.getX();
		// // left, top, right, bottom
		// int y = (int) event.getY();
		//
		// textView1.setPadding(x - 50, y - 50, 0, 0);
		// // LayoutParams lp = new lay
		// // textView1.setLayoutParams())
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// textView1.setText("x=" + x + ",y=" + y);
		// int color = bitmap.getPixel(x, y);
		// mArrayColor[x] = color;
		// // 如果你想做的更细致的话 可以把颜色值的R G B 拿到做响应的处理 笔者在这里就不做更多解释
		// int r = Color.red(color);
		// int g = Color.green(color);
		// int b = Color.blue(color);
		// int a = Color.alpha(color);
		// Log.i(TAG, "r=" + r + ",g=" + g + ",b=" + b);
		//
		// }
		// return true;
		// }
		// });
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}

		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}

		mSoundPool.unload(currentStreamId);
		mSoundPool.release();
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
		matrix.postScale(2.0f, 1.85f); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	private static Bitmap small(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(0.8f, 0.8f); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	// sound hm中的第几个歌曲
	// loop 是否循环 0不循环 -1循环
	public void playSound(int sound) {
		String log;
		if (!isPausePlay) {
			AudioManager mgr = (AudioManager) this
					.getSystemService(Context.AUDIO_SERVICE);

			float streamVolumeCurrent = mgr
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			float streamVolumeMax = mgr
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float volume = streamVolumeCurrent / streamVolumeMax;
			if (isFinishedLoad)
				currentStreamId = mSoundPool.play(soundMap.get(sound), 1, 1, 1,
						0, 1.0f);
			log = "playSound currentStreamId:"
					+ String.valueOf(currentStreamId);
			System.out.println(log);
		} else {
			isPausePlay = false;
			mSoundPool.resume(currentStreamId);
		}
	}

	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imgswitch:
			if (openstate) {
				// 取消摇一摇
				imgswitch.setBackgroundResource(R.drawable.img_autoexplain);
				if (sensorManager != null) {// 取消监听器
					sensorManager.unregisterListener(sensorEventListener);
				}
				openstate = false;
			} else {
				// 注册摇一摇
				imgswitch.setBackgroundResource(R.drawable.img_shake);
				if (sensorManager != null) {// 注册监听器
					sensorManager
							.registerListener(
									sensorEventListener,
									sensorManager
											.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
									SensorManager.SENSOR_DELAY_NORMAL);
					// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
				}
				openstate = true;
			}
			break;
		case R.id.imgmouth:

			if (down) {
				// 不显示文本
				imgmouth.setBackgroundResource(R.drawable.ic_mouth);
				imgdownmouth.setVisibility(View.GONE);
				txtdetail.setVisibility(View.GONE);
				down = false;
			} else {
				// 显示文本
				imgmouth.setBackgroundResource(R.drawable.img_moudown);
				imgdownmouth.setVisibility(View.VISIBLE);
				txtdetail.setVisibility(View.VISIBLE);
				down = true;
			}
			break;
		case R.id.imgplay:
			// disableViewForSeconds(imgplay);
			if (playstate) {
				stopprocess();
			} else {
				int mapID = 0;
				process(mapID);
			}
			break;
		default:
			break;
		}
	}

	// 切换下个音频
	private void stopprocess() {
		// tupian
		imgplay.setBackgroundResource(R.drawable.ic_play);

		isPausePlay = true;
		// 暂停语音
		mSoundPool.pause(currentStreamId);
		isPausePlay = false;

		// mSoundPool.unload(soundID);
		mSoundPool.pause(soundID);

		playstate = false;

	}

	// 播放
	private void process(int mapID) {

		// bofang
		//
		// tupian

		imgplay.setBackgroundResource(R.drawable.ic_pause);
		// 播放语音
		playSound(1);// 播放dudu，dudu文件被解码为16位的PCM数据后超过了SoundPool的1M缓冲区了，循环不了，而且不能播完整个歌曲

		// Intent it = new Intent(Intent.ACTION_VIEW);
		// File file = new File("file:///android_asset/1yindao.m4a");
		// //获取文件file的MIME类型
		// String type = getMIMEType(file);
		// it.setDataAndType(
		// Uri.fromFile(file),
		// type);
		// startActivity(it);
		playstate = true;
	}

	private void trigger(BluetoothDevice device) {
		String address = device.getAddress();
		if (address.equalsIgnoreCase("CF:01:01:00:02:F0")) {
			// 智慧导览 ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F1")) {
			// 行李寄存 ok
			process(8);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F2")) {
			// 3D 互动区 ok ???
			process(3);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F3")) {
			// 智慧旅游应用展示 ok ???
			process(4);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F4")) {
			// 引导台 ok
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F5")) {
			// 旅客上车处 ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F6")) {
			// 智慧旅游视屏 ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F7")) {
			// 单车租赁 ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F8")) {
			// 休闲自助区???
			process(6);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FC")) {
			// 伴手礼超市 ok
			process(10);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E1")) {
			// 多功能厅 ok
			process(11);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E2")) {
			// 综合服务区 ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E3")) {
			// 呼叫中心 ???
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E4")) {
			// 预警指挥中心 ok
			process(13);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E5")) {
			// 办公区 ok
			process(14);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E6")) {
			// TODO 婚纱摄影区 no auido
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E7")) {
			// 信息视屏 ??
			process(1);
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E8")) {
			// 机房 ok
			process(12);
		} else {
			// TODO
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(RouteMapActivity.this, "扫描到其他设备",
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
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	private String getMIMEType(File file) {

		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) { // MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	/**
	 * 重力感应监听
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
			Log.i(TAG, "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
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
	 * 动作执行
	 */
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SENSOR_SHAKE:
				Toast.makeText(RouteMapActivity.this, "检测到摇晃，执行操作！",
						Toast.LENGTH_SHORT).show();
				Log.i(TAG, "检测到摇晃，执行操作！");
				break;
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
						@Override
						public void onProximityBleChanged(BluetoothDevice device) {
							trigger(device);
						}
					});
			bleService.startScanBLE();
		}
	};

}
