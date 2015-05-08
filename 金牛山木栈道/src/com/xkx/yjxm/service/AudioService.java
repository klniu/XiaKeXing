package com.xkx.yjxm.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.view.SurfaceView;

public class AudioService extends Service {
	private MediaPlayer mediaPlayer;
	
	// �ǲ����Ѿ���������Դ
	private boolean isFrist;

	public AudioService() {
	}

	@Override
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mediaPlayer.release();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mediaPlayer.release();
		return super.onUnbind(intent);
	}

	public void play(SurfaceView surfaceView,String uri) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// ����������������
			mediaPlayer.setDisplay(surfaceView.getHolder());// ����videoӰƬ��surfaceviewholder����
			mediaPlayer.setDataSource(uri);
			mediaPlayer.setVolume(1, 1);
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
					isFrist = true;
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				listener.onPlayComplete();
			}
		});
	}

	public class AudioBinder extends Binder {
		public void setOnPlayCompleteListener(OnPlayCompleteListener lis) {
			listener = lis;
		}

		// ��ʼ��������
		public void audioPlay(SurfaceView surfaceView,String uri) {
			play(surfaceView,uri);
		}

		// ��ʼ����
		public void audioStart() {
			mediaPlayer.start();
		}

		// ��ͣ����
		public void audioPause() {
			mediaPlayer.pause();
			// ����Ϊtrue����ͣ����󲻻ᱻkill��
			stopForeground(true);
		}

		// ֹͣ����
		public void audioStop() {
			mediaPlayer.stop();
		}

		// �Ƿ��ڲ���
		public boolean audioIsPlaying() {
			return mediaPlayer.isPlaying();
		}

		// �Ƿ��Ѿ�������Դ
		public boolean audioIsFrist() {
			return isFrist;
		}

		
	}

	private OnPlayCompleteListener listener;

	AudioBinder binder = new AudioBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public interface OnPlayCompleteListener {
		public void onPlayComplete();
	}
}
