package com.xkx.yjxm.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class AudioService extends Service {
	private MediaPlayer mediaPlayer;
	//�ǲ����Ѿ���������Դ
	private boolean isFrist;
	public AudioService() {
	}

	@Override
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		super.onCreate();
	}

	public void play(Uri uri) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(this, uri);
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
				//�����Ҫ������
			}
		});
	}


	public class AudioBinder extends Binder {

		public void audioPlay(Uri uri) {
			play(uri);
		}

		public void audioStart() {
			mediaPlayer.start();
		}
		public void audioPause() {
			mediaPlayer.pause();
			// ����Ϊtrue����ͣ����󲻻ᱻkill��
			stopForeground(true);
		}
		public boolean audioIsPlaying() {
			return mediaPlayer.isPlaying();
		}
		public boolean audioIsFrist() {
			return isFrist;
		}
		
		public void audioPlayNext(Uri uri) {
			isFrist = false;
			play(uri);
		}
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new AudioBinder();
	}
}
