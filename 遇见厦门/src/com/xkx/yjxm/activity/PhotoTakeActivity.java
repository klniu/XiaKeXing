package com.xkx.yjxm.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.xkx.yjxm.R;

public class PhotoTakeActivity extends BaseActivity implements OnClickListener {
	private RelativeLayout upbtn;
	private RelativeLayout cancel;
	private RelativeLayout progresslay;
	private Bitmap bitmap;
	private String QRCODE = "";
	private ImageButton btnback;
	private String newName = "image.jpg";
	private ProgressBar uploadProgressBar;
	private String fileName;
	private Uri selectedImage;
	private String actionUrl = "http://www.xmlyt.cn/ajax/Statistics.ashx?sn=addUserPic";

	private DisplayImageOptions options; // ����ͼƬ���ؼ���ʾѡ��

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_phototake);
		initImageLoader(this);
		initData();
		initUI();
	}

	public void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		// ����ͼƬ���ؼ���ʾѡ�����һЩ���������ã�����doc�ĵ��ɣ�
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_launcher) // ��ImageView���ع�������ʾͼƬ
				.showImageForEmptyUri(R.drawable.ic_launcher) // image���ӵ�ַΪ��ʱ
				.showImageOnFail(R.drawable.ic_launcher) // image����ʧ��
				.cacheInMemory(false) // ����ͼƬʱ�����ڴ��м��ػ���
				.cacheOnDisc(false) // ����ͼƬʱ���ڴ����м��ػ���
				.build();
	}

	public void home(View v) {
		Intent intent = null;

		intent = new Intent(this, MainActivity.class);

		startActivity(intent);
	}

	private void initData() {
		// selectedImage = (Uri) getIntent().getExtras().get("Uri");
		ImageView iv = (ImageView) findViewById(R.id.imageView1);
		String filename = getIntent().getStringExtra("fileName");
		String path = "file:///mnt/sdcard/myImage/" + filename;
		fileName = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/myImage/" + filename;
		ImageLoader.getInstance().displayImage(path, iv, options);
	}

	private void initUI() {
		upbtn = (RelativeLayout) findViewById(R.id.upbtn);
		upbtn.setOnClickListener(this);
		cancel = (RelativeLayout) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);

		progresslay = (RelativeLayout) findViewById(R.id.progresslay);
		uploadProgressBar = (ProgressBar) findViewById(R.id.progress_horizontal);

		btnback = (ImageButton) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.upbtn:

			if (!fileName.equals("")) {
				progresslay.setVisibility(View.VISIBLE);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("arg2", fileName);
				LocationTask task = new LocationTask();
				task.execute(params);
			}
			break;
		case R.id.btnback:
		case R.id.cancel:
			finish();
			break;
		default:
			break;
		}
	}

	/* ��ʾDialog��method */private void showDialog(String mess) {
		new AlertDialog.Builder(PhotoTakeActivity.this).setTitle("Message")
				.setMessage(mess)
				.setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Intent intent = new Intent();
						intent.setClass(PhotoTakeActivity.this,
								PhotoScanningActivity.class);
						intent.putExtra("QRCODE", QRCODE);
						QRCODE = "";
						startActivity(intent);

					}
				}).show();
	}

	private void upload() {
	}

	private class LocationTask extends
			AsyncTask<HashMap<String, String>, Integer, String> {

		// doInBackground�����ڲ�ִ�к�̨����,�����ڴ˷������޸�UI
		@Override
		protected String doInBackground(HashMap<String, String>... params) {
			String filePath = params[0].get("arg2").toString();
			String twoHyphens = "--";
			String boundary = "*****";
			String end = "\r\n";
			try {
				URL url = new URL(actionUrl);
				File f = new File(filePath);
				long totalLength = f.length();
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				/* ����Input��Output����ʹ��Cache */
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setChunkedStreamingMode(0);
				con.setUseCaches(false); /* ���ô��͵�method=POST */
				con.setRequestMethod("POST"); /* setRequestProperty */
				con.setRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Charset", "UTF-8");
				con.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary); /* ����DataOutputStream */
				DataOutputStream dos = new DataOutputStream(
						con.getOutputStream());
				dos.writeBytes(twoHyphens + boundary + end);
				dos.writeBytes("Content-Disposition: form-data; "
						+ "name=\"file\";filename=\"" + newName + "\"" + end);
				dos.writeBytes(end); /* ȡ���ļ���FileInputStream */
				FileInputStream fis = new FileInputStream(filePath); /* ����ÿ��д��1024bytes */
				long transmit = 0;
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int length = -1; /* ���ļ���ȡ������������ */
				int progress = 0;
				while ((length = fis.read(buffer)) != -1) { /* ������д��DataOutputStream�� */
					dos.write(buffer, 0, length);
					// dos.flush();
					transmit += length;
					int temp = (int) (transmit * 100 / totalLength);
					if (temp != progress) {
						Log.e("progress", "" + temp);
						publishProgress(temp);
					}
					progress = temp;
				}
				dos.writeBytes(end);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
				fis.close();
				dos.flush();
				/* ȡ��Response���� */
				InputStream is = con.getInputStream();
				int ch;
				StringBuffer sbResponse = new StringBuffer();
				while ((ch = is.read()) != -1) {
					sbResponse.append((char) ch);
				}
				is.close();
				dos.close();
				return sbResponse.toString();
			} catch (Exception e) {
				showDialog("�ϴ�ʧ��" + e);
			}
			return null;
		}

		// onProgressUpdate�������ڸ��½�����Ϣ
		@Override
		protected void onProgressUpdate(Integer... progress) {
			if (uploadProgressBar != null) {
				uploadProgressBar.setProgress(progress[0]);
			}
		}

		// onCancelled����������ȡ��ִ���е�����ʱ����UI
		@Override
		protected void onCancelled() {
		}

		@Override
		protected void onPostExecute(String Signinfo) {
			try {
				JSONObject jsonObject = new JSONObject(Signinfo);
				if (jsonObject != null) {
					if (jsonObject.getString("code").equals("0000")) {
						QRCODE = jsonObject.getJSONObject("result").optString(
								"QRCODE");
						if (QRCODE.equals("")) {
							return;
						} else {
							showDialog("�ϴ��ɹ�");
							uploadProgressBar.setProgress(100);
							progresslay.setVisibility(View.GONE);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
