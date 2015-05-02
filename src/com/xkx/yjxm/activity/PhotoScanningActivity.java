package com.xkx.yjxm.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.xkx.yjxm.R;
import com.xkx.yjxm.utils.imgUtils;
import com.xkx.yjxm.utils.imgUtils.OnLoadImageListener;

public class PhotoScanningActivity extends Activity implements OnClickListener {
	private String url;
	private ImageView img;
	private ImageButton btnback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photoscan);
		initData();
		initUI();
	}

	private void initData() {

		url = getIntent().getStringExtra("QRCODE");
	}

	/**
	 * ��ȡ����ͼƬ��Դ
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// �������
			HttpURLConnection conn = (HttpURLConnection) myFileURL
					.openConnection();
			// ���ó�ʱʱ��Ϊ6000���룬conn.setConnectionTiem(0);��ʾû��ʱ������
			conn.setConnectTimeout(6000);
			// �������û��������
			conn.setDoInput(true);
			// ��ʹ�û���
			conn.setUseCaches(false);
			// �����п��ޣ�û��Ӱ��
			// conn.connect();
			// �õ�������
			InputStream is = conn.getInputStream();
			// �����õ�ͼƬ
			bitmap = BitmapFactory.decodeStream(is);
			// �ر�������
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;

	}

	private void initUI() {
		// �õ����õ�ͼƬ
		img = (ImageView) this.findViewById(R.id.img);
		btnback = (ImageButton) this.findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
		URL l;
		try {
			l = new URL(url);
			// ��ʾ
			imgUtils.onLoadImage(l, new OnLoadImageListener() {

				@Override
				public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
					// TODO Auto-generated method stub
					if (bitmap != null) {
						img.setImageBitmap(bitmap);
					}

				}
			});
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// new Thread(new Runnable(){
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// URL imageUrl = url;
		// try {
		// HttpURLConnection conn = (HttpURLConnection)
		// imageUrl.openConnection();
		// InputStream inputStream = conn.getInputStream();
		// Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		// Message msg = new Message();
		// msg.obj = bitmap;
		// handler.sendMessage(msg);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		//
		// }).start();

		// img = new ImageView(this);
		// LinearLayout l = new LinearLayout(this);
		// LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);
		// l.addView(img);
		// addContentView(l, params);
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// //ͼƬ��Դ
		// // 25. String url =
		// "http://s16.sinaimg.cn/orignal/89429f6dhb99b4903ebcf&690";
		// // 26. //�õ����õ�ͼƬ
		// // 27. Bitmap bitmap = getHttpBitmap(url);
		// // 28. imageView = (ImageView)this.findViewById(R.id.imageViewId);
		// // 29. //��ʾ
		// // 30. imageView.setImageBitmap(bitmap);
		//
		// // TODO Auto-generated method stub
		// Drawable drawable = LoadImageFromWebOperations(url);
		// img.setImageDrawable(drawable);
		// }
		// }).start();

	}

	private Drawable LoadImageFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (Exception e) {
			System.out.println("Exc=" + e);
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btnback:
			finish();
			break;
		default:
			break;
		}
	}

}
