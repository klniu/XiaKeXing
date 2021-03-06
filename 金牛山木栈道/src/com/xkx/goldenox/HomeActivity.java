package com.xkx.goldenox;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
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
	private RelativeLayout playlay;
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
	// 是不是已经导入了资源
	private String[] name = { "实景游览", "福道印象", "沿途风景", "便民服务", "游客互动", "地图查询" };
	private int[] m_minX = { 13, 213, 611, 778, 698, 801 };
	private int[] m_maxX = { 228, 360, 735, 973, 865, 1000 };
	private int[] m_minY = { 1181, 900, 727, 886, 1158, 1376 };
	private int[] m_maxY = { 1307, 1073, 895, 1026, 1315, 1524 };
	private int[] m_NowMinX = new int[10];
	private int[] m_NowMaxX = new int[10];
	private int[] m_NowMinY = new int[10];
	private int[] m_NowMaxY = new int[10];
	private boolean isFrist = false;
	private boolean isExit;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUI();
		FragmentManager fragMgr = getFragmentManager();
		FragmentTransaction fragTrans = fragMgr.beginTransaction();
		fragTrans.replace(R.id.menu_frame, new BottomFragment(), "");
		fragTrans.commit();
		m_NowMinX = m_minX;
		m_NowMaxX = m_maxX;
		m_NowMinY = m_minY;
		m_NowMaxY = m_maxY;


	}

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			finish();
			System.exit(0);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}

		return false;
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
		runOnUiThread(new Runnable() {
			public void run() {
				if (!isFrist) {
					start.setBackgroundResource(R.drawable.ic_play);

					File file = new File(
							Environment.getExternalStorageDirectory(), filename);

					try {
						mediaPlayer.reset();// 重置为初始状态
						mediaPlayer
								.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置音乐流的类型
						mediaPlayer.setDisplay(surfaceView.getHolder());// 设置video影片以surfaceviewholder播放
						mediaPlayer.setDataSource(file.getAbsolutePath());
						mediaPlayer.setVolume(1, 1);
						mediaPlayer.prepareAsync();
						mediaPlayer
								.setOnPreparedListener(new OnPreparedListener() {

									@Override
									public void onPrepared(MediaPlayer mp) {
										mediaPlayer.start();
										isFrist = true;
										isPlaying = true;
										start.setBackgroundResource(R.drawable.ic_pause);
										start.setVisibility(View.GONE);
									}
								});
						mediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {

									@Override
									public void onCompletion(MediaPlayer mp) {
										isPlaying = false;
										start.setBackgroundResource(R.drawable.ic_play);
										start.setVisibility(View.VISIBLE);
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
					}// 设置路径
				} else {

					if (isPlaying) {
						start.setBackgroundResource(R.drawable.ic_play);
						mediaPlayer.pause();
						start.setVisibility(View.VISIBLE);
						isPlaying = false;
						// 设置为true后，暂停服务后不会被kill掉
						// stopForeground(true);

					} else {
						start.setBackgroundResource(R.drawable.ic_pause);
						mediaPlayer.start();
						start.setVisibility(View.GONE);
						isPlaying = true;
					}
				}
				// tvContent.setVisibility(View.INVISIBLE);
				// imgdownmouth.setVisibility(View.INVISIBLE);
			}
		});

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
		surfaceView.getHolder().setFixedSize(176, 144);// 设置分辨率
		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置surfaceview不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前
		surfaceView.getHolder().addCallback(new SurceCallBack());// 对surface对象的状态进行监听
		mediaPlayer = new MediaPlayer();

		ButtonOnClikListiner buttonOnClikListinero = new ButtonOnClikListiner();
		start = (ImageButton) findViewById(R.id.playOnHome);
		// ImageButton pause = (ImageButton) findViewById(R.id.pause);
		start.setOnClickListener(buttonOnClikListinero);
		playlay = (RelativeLayout) findViewById(R.id.playlay);
		playlay.setOnClickListener(buttonOnClikListinero);
		// pause.setOnClickListener(buttonOnClikListinero);
		
	}

	// @Override
	// protected void onDestroy() {
	// // TODO Auto-generated method stub
	// super.onDestroy();
	// unbindService(audioConn);
	// // Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
	// }

	private final class ButtonOnClikListiner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {
				Toast.makeText(HomeActivity.this, "sd卡不存在", Toast.LENGTH_SHORT)
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
		// Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
	}
	
	private void play() {
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					filename);
			mediaPlayer.reset();// 重置为初始状态
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置音乐流的类型
			mediaPlayer.setDisplay(surfaceView.getHolder());// 设置video影片以surfaceviewholder播放
			mediaPlayer.setDataSource(file.getAbsolutePath());// 设置路径
			mediaPlayer.prepare();// 缓冲
			mediaPlayer.start();// 播放
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	private final class SurceCallBack implements SurfaceHolder.Callback {
		/**
		 * 画面修改
		 */
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub

		}

		/**
		 * 画面创建
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
		 * 画面销毁
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
