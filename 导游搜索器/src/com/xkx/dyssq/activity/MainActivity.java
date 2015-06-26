package com.xkx.dyssq.activity;

import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brtbeacon.sdk.BRTBeacon;
import com.xkx.dyssq.R;
import com.xkx.dyssq.activity.BleScanService.BleBinder;
import com.xkx.dyssq.activity.BleScanService.OnBleScanListener;

public class MainActivity extends Activity {

	private boolean isBack;
	private boolean isStart;
	private ImageView m_im1;
	private ImageView m_im2;
	private LinearLayout m_ll;
	private int animationCount = 5;
	private int animationCount2 = 3;
	private SensorManager sensorManager;
	private final int MSG_SENSOR_SHAKE = 10;
	private Vibrator vibrator;
	private BleBinder bleBinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		ivHead = (ImageView) findViewById(R.id.imageView3);
		tvName = (TextView) findViewById(R.id.textView2);
		tvPhone = (TextView) findViewById(R.id.textView3);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		m_im1 = (ImageView) findViewById(R.id.imageView1);
		m_im2 = (ImageView) findViewById(R.id.imageView2);
		// m_im2.setEnabled(false);
		m_ll = (LinearLayout) findViewById(R.id.linearLayout);
		m_ll.setVisibility(View.INVISIBLE);
		m_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = null;
				intent = new Intent(MainActivity.this, GuideActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		adapter.enable();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// �ر�����
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		adapter.disable();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ע��
		sensorManager.unregisterListener(sensorEventListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ע��
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
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
				Log.i("123123123123", "x��" + x + "��y��" + y + "��z��" + z);
			}
			int medumValue = 15;// ���� i9250��ô�ζ����ᳬ��20��û�취��ֻ����19��
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
					|| Math.abs(z) > medumValue) {
				vibrator.vibrate(200);
				Message msg = new Message();
				msg.what = MSG_SENSOR_SHAKE;
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
			case MSG_SENSOR_SHAKE:
				processAfterShake();
				break;
			}
		}

		private void processAfterShake() {
			Intent service = new Intent(MainActivity.this, BleScanService.class);
			bindService(service, conn, BIND_AUTO_CREATE);
			startPeople(m_im2);
		}
	};

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("scan", "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("scan", "onServiceConnected");
			bleBinder = (BleBinder) service;

			bleBinder.setRegion("FDA50693-A4E2-4FB1-AFCF-C6EB07647825", 10009);// �մ���ɨ������
			// �մ���ɨ������
			bleBinder.setOnBleScanListener(new OnBleScanListener() {

				public void onPeriodScan(List<BRTBeacon> scanResultList) {
					unbindService(conn);
				}

				public void onNearBleChanged(BRTBeacon oriBeacon,
						BRTBeacon desBeacon) {

				}

				public void onNearBeacon(BRTBeacon brtBeacon) {
					if (brtBeacon == null) {
						Toast.makeText(MainActivity.this, "û��ҡ����,��ҡһ��",
								Toast.LENGTH_LONG).show();
					} else {
						int id = getID(brtBeacon.macAddress);
						if (id != -1) {
							startGuide();
							updateInfo(id);
						} else {
							Toast.makeText(MainActivity.this, "û��ҡ����,��ҡһ��",
									Toast.LENGTH_LONG).show();
						}
					}
				}
			});
		}
	};

	int[] heads = new int[] { R.drawable.img_lixiaohua, R.drawable.img_liuna,
			R.drawable.img_zhangyang, R.drawable.img_zhoutongtong };

	private void updateInfo(int id) {
		String name = "������", phone = "15805934402\t\t\t\t\t\t�鿴", type = "����֤", num = "D-3501-003469";
		int head = heads[0];
		if (id == -1) {
			m_ll.setVisibility(View.INVISIBLE);
			return;
		}

		// TODO ������Ϣ
		if (id == 0) {
			name = "������";
			phone = "15805934402\t\t\t\t\t\t�鿴";
			type = "����֤";
			num = "D-3501-003469";
			head = heads[0];
		} else if (id == 1) {
			name = "����";
			phone = "15805934402\t\t\t\t\t\t�鿴";
			type = "����֤";
			num = "D-3501-003469";
			head = heads[1];
		} else if (id == 2) {
			name = "����";
			phone = "15805934402\t\t\t\t\t\t�鿴";
			type = "����֤";
			num = "D-3501-003469";
			head = heads[2];
		} else if (id == 3) {
			name = "��ͮͮ";
			phone = "15805934402\t\t\t\t\t\t�鿴";
			type = "����֤";
			num = "D-3501-003469";
			head = heads[3];
		} else {
			name = "��ͮͮ";
			phone = "15805934402\t\t\t\t\t\t�鿴";
			type = "����֤";
			num = "D-3501-003469";
			head = heads[3];
		}
		tvName.setText(name);
		tvPhone.setText(phone);
		ivHead.setBackgroundResource(head);
	}

	private int getID(String mac) {
		mac = mac.trim();
		// TODO �����͵���ƥ��
		if (mac.equalsIgnoreCase("54:4A:16:2D:B0:32")) {// ����6.25��ǰ��mac
														// 54:4A:16:2D:B0:7D
			return 2;
		} else if (mac.equalsIgnoreCase("54:4A:16:2D:B0:54")) {// ����6.25��ǰ��mac
																// 54:4A:16:2D:A0:DC
			return 1;
		} else if (mac.equalsIgnoreCase("54:4A:16:2D:B0:45")) {// ��ͮͮ6.25��ǰ��mac
																// 54:4A:16:2D:AD:F9
			return 3;
		} else if (mac.equalsIgnoreCase("54:4A:16:2D:AD:E6")) {// ������6.25��ǰ��mac
																// 54:4A:16:2D:AA:3C
			return 0;
		}
		return -1;
	}

	private ImageView ivHead;
	private TextView tvName;
	private TextView tvPhone;

	// ������
	public static void playVibator(Context context, long timelong) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(timelong);
	}

	public void startPeople(View v) {
		Intent service = new Intent(MainActivity.this, BleScanService.class);
		bindService(service, conn, BIND_AUTO_CREATE);
		// ����״̬
		isStart = !isStart;
		v.setClickable(false);
		m_ll.setVisibility(View.INVISIBLE);
		final ImageView im = (ImageView) v;

		if (isStart) {
			m_im1.setBackgroundResource(R.drawable.shake2);
			m_im2.setBackgroundResource(R.drawable.people2);

			final AnimationSet animationSet = new AnimationSet(true);

			TranslateAnimation animation1 = new TranslateAnimation(0, 30, 0, 0);
			TranslateAnimation animation2 = new TranslateAnimation(0, -60, 0, 0);
			TranslateAnimation animation3 = new TranslateAnimation(0, 60, 0, 0);
			TranslateAnimation animation4 = new TranslateAnimation(0, -30, 0, 0);

			// ���ö�������ʱ��
			animation1.setDuration(70);
			animation2.setDuration(140);
			animation3.setDuration(140);
			animation4.setDuration(70);

			// �ӳٲ���
			// animation1.setStartOffset();
			animation2.setStartOffset(70);
			animation3.setStartOffset(140);
			animation4.setStartOffset(140);

			animationSet.addAnimation(animation1);
			animationSet.addAnimation(animation2);
			animationSet.addAnimation(animation3);
			animationSet.addAnimation(animation4);

			animationSet.setInterpolator(new LinearInterpolator());
			m_im2.startAnimation(animationSet);
			// ������
			playVibator(this, 1000);
			animationSet.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// m_im2.clearAnimation();
					if (animationCount-- != 0) {
						m_im2.startAnimation(animationSet);
					} else {
						m_im2.clearAnimation();
						isStart = false;
						m_im1.setBackgroundResource(R.drawable.shake1);
						m_im2.setBackgroundResource(R.drawable.people1);
						m_im2.setClickable(true);
						animationCount = 5;
						// startGuide();
					}
				}
			});
		}
	}

	public void startGuide() {
		m_ll.setVisibility(View.VISIBLE);
		final AnimationSet animationSet = new AnimationSet(true);

		TranslateAnimation animation1 = new TranslateAnimation(0, 60, 0, 0);
		TranslateAnimation animation2 = new TranslateAnimation(0, -90, 0, 0);
		TranslateAnimation animation3 = new TranslateAnimation(0, 90, 0, 0);
		TranslateAnimation animation4 = new TranslateAnimation(0, -60, 0, 0);

		// ���ö�������ʱ��
		animation1.setDuration(40);
		animation2.setDuration(80);
		animation3.setDuration(80);
		animation4.setDuration(40);

		// �ӳٲ���
		// animation1.setStartOffset();
		animation2.setStartOffset(40);
		animation3.setStartOffset(80);
		animation4.setStartOffset(80);

		animationSet.addAnimation(animation1);
		animationSet.addAnimation(animation2);
		animationSet.addAnimation(animation3);
		animationSet.addAnimation(animation4);

		animationSet.setInterpolator(new LinearInterpolator());
		m_ll.startAnimation(animationSet);
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// m_im2.clearAnimation();
				if (animationCount2-- != 0) {
					m_ll.startAnimation(animationSet);
				} else {
					m_ll.clearAnimation();
					isStart = false;
					animationCount2 = 3;
				}
			}
		});
	}

	// ��תҳ��
	public void startIn(View v) {
		Intent intent = null;
		intent = new Intent(this, GuideActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (isBack) {
				finish();
				System.exit(0);
			} else {
				isBack = true;
				Toast.makeText(MainActivity.this, "�ٰ�һ���˳�����",
						Toast.LENGTH_SHORT).show();
				m_im1.postDelayed(new Runnable() {
					public void run() {
						isBack = false;
					}
				}, 2000);
			}

		}
		return false;
	}
}
