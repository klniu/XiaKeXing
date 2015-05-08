package com.xkx.goldenox;

import java.io.File;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xkx.goldenox.utils.Utils;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUI();
		FragmentManager fragMgr = getSupportFragmentManager();
		FragmentTransaction fragTrans = fragMgr.beginTransaction();
		// TODO �滻frament
		

		fragTrans.replace(R.id.menu_frame, new BottomFragment(), "");
		fragTrans.commit();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		Toast.makeText(this, "x=" + x + "y=" + y, Toast.LENGTH_SHORT).show();
		return false;
	}

//	private void processPlay(int id, boolean play) {
//		Uri uri = getUri(id);
//		if (uri == null) {
//			CrashHandler.getInstance().logToFile(Thread.currentThread(),
//					new Exception("Uri null"));
//			return;
//		}
//		updateHead(id, play);
//		hasProcessedMap.put(id, true);
//		isPlaying = true;
//		soundlay.setVisibility(View.VISIBLE);
//		audioBinder.audioPlay(uri);
//	}
	
	
	private void initUI() {
		toplay = (RelativeLayout) findViewById(R.id.toplay);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				Utils.px2dip(1080), Utils.px2dip(100));

		toplay.setLayoutParams(param);

		sufacelay = (LinearLayout) findViewById(R.id.sufacelay);

		RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(
				Utils.px2dip(1080), Utils.px2dip(507));

		sufacelay.setLayoutParams(param2);

		menu_frame = (FrameLayout) findViewById(R.id.menu_frame);

		RelativeLayout.LayoutParams param3 = new RelativeLayout.LayoutParams(
				Utils.px2dip(1080), Utils.px2dip(1313));

		menu_frame.setLayoutParams(param3);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		surfaceView.getHolder().setFixedSize(176, 144);// ���÷ֱ���
		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// ����surfaceview��ά���Լ��Ļ����������ǵȴ���Ļ����Ⱦ���潫�������͵��û���ǰ
		surfaceView.getHolder().addCallback(new SurceCallBack());// ��surface�����״̬���м���
		mediaPlayer = new MediaPlayer();

		ButtonOnClikListiner buttonOnClikListinero = new ButtonOnClikListiner();
		ImageButton start = (ImageButton) findViewById(R.id.play);
//		ImageButton pause = (ImageButton) findViewById(R.id.pause);
		//start.setOnClickListener(buttonOnClikListinero);
		//pause.setOnClickListener(buttonOnClikListinero);
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

	private final class ButtonOnClikListiner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {
				Toast.makeText(HomeActivity.this, "sd��������", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			filename = "2793299.mp4";
			switch (v.getId()) {
			case R.id.play:
				play();
				break;
			case R.id.pause:
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
				} else {
					mediaPlayer.start();
				}
				break;

			}
		}
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
