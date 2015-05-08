package com.xkx.goldenox;

import java.io.File;
import java.io.IOException;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xkx.yjxm.service.AudioService;
import com.xkx.yjxm.service.AudioService.AudioBinder;
import com.xkx.yjxm.service.AudioService.OnPlayCompleteListener;

public class HomeActivity extends FragmentActivity {

	private final String TAG_FRAG_MENU = "com.xkx.goldenox.menu";

	private RelativeLayout toplay;
	private GifView GifView1;

	private MediaPlayer mediaPlayer;
	private String filename;
	private SurfaceView surfaceView;
	private LinearLayout sufacelay;
	private final static String TAG = "VodeoPlayActivity";
	private int prosition = 0;
	private FrameLayout menu_frame;
	private ImageButton start;
	private Boolean isPlaying = false;
	// �ǲ����Ѿ���������Դ
	private String[] name = { "ʵ������", "����ӡ��", "��;�羰", "�������", "�οͻ���", "��ͼ��ѯ" };
	private int[] m_minX = { 13, 213, 611, 778, 698, 801 };
	private int[] m_maxX = { 228, 360, 735, 973, 865, 1000 };
	private int[] m_minY = { 1181, 900, 727, 886, 1158, 1376 };
	private int[] m_maxY = { 1307, 1073, 895, 1026, 1315, 1524 };
	private int[] m_NowMinX = new int[10];
	private int[] m_NowMaxX = new int[10];
	private int[] m_NowMinY = new int[10];
	private int[] m_NowMaxY = new int[10];
	private boolean isFrist = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUI();
		// bindAudioService();
		FragmentManager fragMgr = getSupportFragmentManager();
		FragmentTransaction fragTrans = fragMgr.beginTransaction();
		// TODO �滻frament
		m_NowMinX = m_minX;
		m_NowMaxX = m_maxX;
		m_NowMinY = m_minY;
		m_NowMaxY = m_maxY;
		
		fragTrans.replace(R.id.menu_frame, new BottomFragment(), "");
		fragTrans.commit();

	}

	private void bindAudioService() {
		Intent service = new Intent(HomeActivity.this, AudioService.class);
		bindService(service, audioConn, BIND_AUTO_CREATE);
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// float x = event.getX();
	// float y = event.getY();
	// Toast.makeText(this, "x=" + x + "y=" + y, Toast.LENGTH_SHORT).show();
	// return false;
	// }

	private void processPlay() {

		if (!isFrist) {
			start.setBackgroundResource(R.drawable.ic_play);

			File file = new File(Environment.getExternalStorageDirectory(),
					filename);
			
			try {
				mediaPlayer.reset();// ����Ϊ��ʼ״̬
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// ����������������
				mediaPlayer.setDisplay(surfaceView.getHolder());// ����videoӰƬ��surfaceviewholder����
				mediaPlayer.setDataSource(file.getAbsolutePath());
				mediaPlayer.setVolume(1, 1);
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						mediaPlayer.start();
						isFrist = true;
						isPlaying = true;
						start.setBackgroundResource(R.drawable.ic_pause);
					}
				});
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						isPlaying = false;
						start.setBackgroundResource(R.drawable.ic_play);
					}
				});
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// ����·��
		} else {

			if (isPlaying) {
				start.setBackgroundResource(R.drawable.ic_play);
				mediaPlayer.pause();
				isPlaying = false;
				// ����Ϊtrue����ͣ����󲻻ᱻkill��
				// stopForeground(true);

			} else {
				start.setBackgroundResource(R.drawable.ic_pause);
				mediaPlayer.start();
				isPlaying = true;
			}
		}

	}

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
							start.setBackgroundResource(R.drawable.ic_play);
							// tvContent.setVisibility(View.INVISIBLE);
							// imgdownmouth.setVisibility(View.INVISIBLE);
						}
					});
				}
			});
			if (audioBinder == null) {
				// CrashHandler.getInstance().logToFile(Thread.currentThread(),
				// new Exception("audioBinder null"));
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	@SuppressWarnings("deprecation")
	private void initUI() {
		// toplay = (RelativeLayout) findViewById(R.id.toplay);
		// sufacelay = (LinearLayout) findViewById(R.id.sufacelay);
		// menu_frame = (FrameLayout) findViewById(R.id.menu_frame);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		surfaceView.getHolder().setFixedSize(176, 144);// ���÷ֱ���
		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// ����surfaceview��ά���Լ��Ļ����������ǵȴ���Ļ����Ⱦ���潫�������͵��û���ǰ
		surfaceView.getHolder().addCallback(new SurceCallBack());// ��surface�����״̬���м���
		mediaPlayer = new MediaPlayer();

		ButtonOnClikListiner buttonOnClikListinero = new ButtonOnClikListiner();
		start = (ImageButton) findViewById(R.id.playOnHome);
		// ImageButton pause = (ImageButton) findViewById(R.id.pause);
		start.setOnClickListener(buttonOnClikListinero);
		// pause.setOnClickListener(buttonOnClikListinero);
	}

	// @Override
	// protected void onDestroy() {
	// // TODO Auto-generated method stub
	// super.onDestroy();
	// unbindService(audioConn);
	// // Activity����ʱֹͣ���ţ��ͷ���Դ�����������������ʹ�˳�������������Ƶ���ŵ�����
	// }

	private final class ButtonOnClikListiner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {
				Toast.makeText(HomeActivity.this, "sd��������", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			filename = "zhlv0804.mp4";
			// switch (v.getId()) {
			// case R.id.play:
			// processPlay();
			// // if (CommonUtils.isFastDoubleClick()) {
			// // return;
			// // }
			//
			// break;
			//
			// }
			processPlay();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.release();
		// Activity����ʱֹͣ���ţ��ͷ���Դ�����������������ʹ�˳�������������Ƶ���ŵ�����
	}

	private void play() {
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					filename);
			mediaPlayer.reset();// ����Ϊ��ʼ״̬
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// ����������������
			mediaPlayer.setDisplay(surfaceView.getHolder());// ����videoӰƬ��surfaceviewholder����
			mediaPlayer.setDataSource(file.getAbsolutePath());// ����·��
			mediaPlayer.prepare();// ����
			mediaPlayer.start();// ����
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	private final class SurceCallBack implements SurfaceHolder.Callback {
		/**
		 * �����޸�
		 */
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub

		}

		/**
		 * ���洴��
		 */
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (prosition > 0 && filename != null) {
				play();
				mediaPlayer.seekTo(prosition);
				prosition = 0;
			}
			

		}

		/**
		 * ��������
		 */
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mediaPlayer.isPlaying()) {
				prosition = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
			}
		}
	}
}
