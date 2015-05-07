package com.xkx.goldenox;

import java.io.File;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class VideoFragment extends Fragment {
	private View rootView;// ����Fragment view
	private GifView GifView1;
	
	private MediaPlayer mediaPlayer;
	private String filename;
	private SurfaceView surfaceView;
	private final static String TAG = "VodeoPlayActivity";
	private int prosition = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_video, null);
		}
		// �����rootView��Ҫ�ж��Ƿ��Ѿ����ӹ�parent��
		// �����parent��Ҫ��parentɾ����Ҫ��Ȼ�ᷢ�����rootview�Ѿ���parent�Ĵ���
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		initUI(rootView);

		return rootView;
	}

	@SuppressWarnings("deprecation")
	private void initUI(View rootView) {
		
		surfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceview);
		surfaceView.getHolder().setFixedSize(176, 144);// ���÷ֱ���
		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// ����surfaceview��ά���Լ��Ļ����������ǵȴ���Ļ����Ⱦ���潫�������͵��û���ǰ
		surfaceView.getHolder().addCallback(new SurceCallBack());// ��surface�����״̬���м���
		mediaPlayer = new MediaPlayer();

		ButtonOnClikListiner buttonOnClikListinero = new ButtonOnClikListiner();
		ImageButton start = (ImageButton) rootView.findViewById(R.id.play);
		ImageButton pause = (ImageButton) rootView.findViewById(R.id.pause);
		start.setOnClickListener(buttonOnClikListinero);
		pause.setOnClickListener(buttonOnClikListinero);

	}

//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		if (mediaPlayer.isPlaying()) {
//			mediaPlayer.stop();
//		}
//		mediaPlayer.release();
//		// Activity����ʱֹͣ���ţ��ͷ���Դ�����������������ʹ�˳�������������Ƶ���ŵ�����
//	}

	private final class ButtonOnClikListiner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {
				Toast.makeText(getActivity(), "sd��������",
						Toast.LENGTH_SHORT).show();
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
