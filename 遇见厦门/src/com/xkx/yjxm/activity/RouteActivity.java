package com.xkx.yjxm.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baimao.download.FileDownloadThread;
import com.baimao.download.HttpService;
import com.baimao.download.JsonResponse;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkx.yjxm.R;
import com.xkx.yjxm.adpater.BaseListAdapter;
import com.xkx.yjxm.base.Constants;
import com.xkx.yjxm.db.MySqlite;
import com.xkx.yjxm.utils.HttpUtil;

//����·��
public class RouteActivity extends Activity implements OnClickListener {
	private ImageButton btnlist;
	private ImageButton btndlist;
	private ImageButton btnback;
	private ListView listView1;
	private List<Map<String, Object>> list;
	DisplayImageOptions options; // ����ͼƬ���ؼ���ʾѡ��
	private MyAdapter myAdapter;
	private LinearLayout downloadlay;
	private ImageView imgmap;
	private int img[] = new int[19];
	private String title[] = { "�ǻ۵���", "����̨", "�л���3D�����ƽ���", "�ǻ�����Ӧ��չʾ��",
			"�ۺϷ�����", "��Ħ���������", "��Ʒ��Ϣ������Ļ", "��������Ĵ��", "ҽ����", "��������",
			"�๦�ܻ�����", "����", "Ԥ��ָ������", "�칫��", "�ǻ���������", "��������", "�ÿ��ϳ���", "��������",
			"19" };
	private String time[] = new String[19];
	private int times = 10;
	private int distances = 100;
	private String distance[] = new String[19];
	private String stringExtra;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private TextView txtdetail;
	private ImageButton img_close;
	private FrameLayout txtlay;
	private Map<Integer, String> textMap;
	private String TAG = "RouteActivity";
	private Boolean finstate = false;// �Ƿ��������

	public void home(View v) {
		Intent intent = null;

		intent = new Intent(this, MainActivity.class);

		startActivity(intent);
	}

	downloadTask task;
	downloadTask2 task2;
	/** ��ʾ���ؽ���TextView */
	private TextView mMessageView;
	/** ��ʾ���ؽ���ProgressBar */
	private ProgressBar mProgressbar;
	private URL connectURL;
	private String path;

	private String fistr;
	private boolean downstate = false;// �ж����ص�λ��
	String strFileName;
	private SQLiteDatabase mDB;
	private String Macresponse;// MacJSON
	JSONArray Macjsonarray = new JSONArray();// MacJSONArray
	private String Resresponse;// ��ԴJSON
	JSONArray Resjsonarray = new JSONArray();// ��ԴJSONArray

	private File[] filelist; // ��ȡresouce�µ��ļ����б�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_route);
		Intent intent = getIntent();
		// stringExtra �õ����ǣ�����ɽor����ɽor�����죩
		stringExtra = intent.getStringExtra("name");
		initData();
		initUI();
		for (int i = 0; i < img.length; i++) {
			img[i] = R.drawable.route01 + i;
		}
		Random random = new Random();
		Random random2 = null;
		for (int i = 0; i < img.length; i++) {
			int nextInt = random.nextInt();
			random2 = new Random(nextInt);
			times = random2.nextInt(10) + 5;

			time[i] = "���ʱ����" + times + "����";
		}
		for (int i = 0; i < img.length; i++) {
			distances += 10;
			distance[i] = distances + "m";
		}
	}

	private List<Map<String, Object>> getData() {
		// map.put(��������,����ֵ)
		list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("img", R.drawable.img_feng_qing);
		map.put("title", getResources().getString(R.string.R_title1));
		map.put("time", getResources().getString(R.string.R_time));
		map.put("distance", getResources().getString(R.string.R_dis1));
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("img", R.drawable.img_ri_guang_yan);
		map.put("title", getResources().getString(R.string.R_title2));
		map.put("time", getResources().getString(R.string.R_time));
		map.put("distance", getResources().getString(R.string.R_dis2));
		list.add(map);

		map = new HashMap<String, Object>();

		map.put("img", R.drawable.img_bai_niao_yuan);
		map.put("title", getResources().getString(R.string.R_title3));
		map.put("time", getResources().getString(R.string.R_time));
		map.put("distance", getResources().getString(R.string.R_dis3));
		list.add(map);

		map = new HashMap<String, Object>();

		map.put("img", R.drawable.img_shu_zhuang_hua_yuan);
		map.put("title", getResources().getString(R.string.R_title4));
		map.put("time", getResources().getString(R.string.R_time));
		map.put("distance", getResources().getString(R.string.R_dis4));
		list.add(map);

		map = new HashMap<String, Object>();

		map.put("img", R.drawable.img_hao_yue_yuan);
		map.put("title", getResources().getString(R.string.R_title5));
		map.put("time", getResources().getString(R.string.R_time));
		map.put("distance", getResources().getString(R.string.R_dis5));
		list.add(map);
		return list;
	}

	private void initData() {
		MySqlite mySqlite = new MySqlite(RouteActivity.this, "yjxm.db", null, 1);
		mDB = mySqlite.getReadableDatabase();
		getData();
		// ����ͼƬ���ؼ���ʾѡ�����һЩ���������ã�����doc�ĵ��ɣ�
		options = new DisplayImageOptions.Builder()

		.showStubImage(R.drawable.ic_launcher) // ��ImageView���ع�������ʾͼƬ
				.showImageForEmptyUri(R.drawable.ic_launcher) // image���ӵ�ַΪ��ʱ
				.showImageOnFail(R.drawable.ic_launcher) // image����ʧ��
				.cacheInMemory(true) // ����ͼƬʱ�����ڴ��м��ػ���
				.cacheOnDisc(true) // ����ͼƬʱ���ڴ����м��ػ���
				// �����û�����ͼƬtask(������Բ��ͼƬ��ʾ)

				.build();

	}

	private void initUI() {
		downloadlay = (LinearLayout) findViewById(R.id.downloadlay);
		btndlist = (ImageButton) findViewById(R.id.btndlist);
		btndlist.setOnClickListener(this);
		txtlay = (FrameLayout) findViewById(R.id.txtlay);
		txtdetail = (TextView) findViewById(R.id.txtdetail);
		img_close = (ImageButton) findViewById(R.id.img_close);

		img_close.setOnClickListener(this);

		textMap = new HashMap<Integer, String>();
		textMap.put(0, getResources().getString(R.string.txt_dao_lan_she_bei));
		textMap.put(1, getResources().getString(R.string.txt_yin_dao));

		// mapBgMap.put(2, R.drawable.img_map)

		textMap.put(2, getResources().getString(R.string.txt_tiyan_3d));

		textMap.put(3, getResources().getString(R.string.txt_lv_you_zhan_shi));
		textMap.put(4, getResources().getString(R.string.txt_zhi_hui_lv_you));
		textMap.put(5, getResources().getString(R.string.txt_anmo));
		textMap.put(6, getResources().getString(R.string.txt_xin_xi_bo_fang));

		textMap.put(7, getResources().getString(R.string.txt_xing_li_ji_cun));
		// mapBgMap.put(7, R.drawable.img_map);
		textMap.put(8, getResources().getString(R.string.txt_yiwu_shi));
		textMap.put(9, getResources().getString(R.string.txt_ban_shou_li));
		textMap.put(10, getResources().getString(R.string.txt_duo_gong_neng));
		textMap.put(11, getResources().getString(R.string.txt_yun_shu_ju));
		textMap.put(12, getResources().getString(R.string.txt_yu_jin));
		textMap.put(13, getResources().getString(R.string.txt_ban_gong_qu));
		textMap.put(14, getResources().getString(R.string.txt_xin_xi_bo_fang));

		textMap.put(16, getResources().getString(R.string.txt_hu_jiao));

		btnback = (ImageButton) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
		btnlist = (ImageButton) findViewById(R.id.btnlist);
		btnlist.setOnClickListener(this);

		imgmap = (ImageView) findViewById(R.id.imgmap);
		imgmap.setOnClickListener(this);
		listView1 = (ListView) findViewById(R.id.listView1);
		myAdapter = new MyAdapter();
		listView1.setAdapter(myAdapter);
		listView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				txtdetail.setText(textMap.get(position));
				txtlay.setVisibility(View.VISIBLE);
			}
		});
		findViewById(R.id.download_btn).setOnClickListener(this);
		findViewById(R.id.download_btn).setEnabled(true);
		mMessageView = (TextView) findViewById(R.id.download_message);
		mProgressbar = (ProgressBar) findViewById(R.id.download_progress);

		// ��������
		
			sendRequest();
		
	}

	private void sendRequest() {
		// �����������mac���ж��Ƿ���Ҫ����mac
		requestMacInfo();
		

	}

	/**
	 * ʹ��Handler����UI������Ϣ
	 */
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			mProgressbar.setProgress(msg.getData().getInt("size"));

			float temp = (float) mProgressbar.getProgress()
					/ (float) mProgressbar.getMax();

			int progress = (int) (temp * 100);
			if (progress == 100) {
				Toast.makeText(RouteActivity.this, "�������,�����ڿ��Խ����ͼ�����Զ�������Ȥ�ˣ�",
						6000).show();
				// ��ȡSD��·��
				String path = Environment.getExternalStorageDirectory()
						+ "/resource/" + fistr;
				String folderPath = Environment.getExternalStorageDirectory()
						+ "/resource/";
				File file = new File(path);

				try {
					upZipFile(file, folderPath);
				} catch (ZipException e) {
					// TODO Auto-generated catch block
					Log.e("fdfd", e.toString());
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				downloadlay.setVisibility(View.GONE);
			}
			mMessageView.setText("���ؽ���:" + progress + " %");

		}
	};

	/**
	 * ��ѹ������. ��zipFile�ļ���ѹ��folderPathĿ¼��.
	 * 
	 * @throws Exception
	 */
	public int upZipFile(File zipFile, String folderPath) throws ZipException,
			IOException {
		// public static void upZipFile() throws Exception{
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				Log.d("upZipFile", "ze.getName() = " + ze.getName());
				String dirstr = folderPath + ze.getName();
				// dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "str = " + dirstr);
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			Log.d("upZipFile", "ze.getName() = " + ze.getName());
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					getRealFileName(folderPath, ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		Log.d("upZipFile", "finishssssssssssssssssssss");
		return 0;
	}

	/**
	 * ������Ŀ¼������һ�����·������Ӧ��ʵ���ļ���.
	 * 
	 * @param baseDir
	 *            ָ����Ŀ¼
	 * @param absFileName
	 *            ���·������������ZipEntry�е�name
	 * @return java.io.File ʵ�ʵ��ļ�
	 */
	public static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					// substr.trim();
					substr = new String(substr.getBytes("8859_1"), "GB2312");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ret = new File(ret, substr);

			}
			Log.d("upZipFile", "1ret = " + ret);
			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				// substr.trim();
				substr = new String(substr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "substr = " + substr);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ret = new File(ret, substr);
			Log.d("upZipFile", "2ret = " + ret);
			return ret;
		}
		return ret;
	}

	/**
	 * ���߳��ļ�����
	 * 
	 * @author yangxiaolong
	 * @2014-8-7
	 */
	class downloadTask2 extends Thread {
		private String downloadUrl;// �������ӵ�ַ
		private int threadNum;// �������߳���
		private String filePath;// �����ļ�·����ַ
		private int blockSize;// ÿһ���̵߳�������
		private String filename;

		public downloadTask2(String downloadUrl, int threadNum,
				String fileptah, String filename) {
			this.downloadUrl = downloadUrl;
			this.threadNum = threadNum;
			this.filePath = fileptah;
			this.filename = filename;
		}

		@Override
		public void run() {

			FileDownloadThread[] threads = new FileDownloadThread[threadNum];
			try {
				URL url = new URL(downloadUrl);

				byte[] data = null;
				if ((url != null && !"".equals(url))
						&& (filename != null && !"".equals(filename))) {
					InputStream input = null;
					try {
						connectURL = new URL(downloadUrl);
						HttpURLConnection conn = (HttpURLConnection) connectURL
								.openConnection();
						conn.setRequestMethod("POST");
						conn.setReadTimeout(1000);
						input = conn.getInputStream();
					} catch (MalformedURLException e) {
						// Log.e(ERROTAG, "��ȷ������ĵ�ַ��ȷ��Ȩ���Ƿ����!!!!!!");
						e.printStackTrace();
					} catch (IOException e) {
						// toasterror("����ʧ��");
						// Log.w(ERROTAG, "�����·������");
						e.printStackTrace();
					}
					FileOutputStream outStream = null;
					try {
						outStream = RouteActivity.this.openFileOutput(filename,
								Context.MODE_WORLD_READABLE
										| Context.MODE_WORLD_WRITEABLE);
						int temp = 0;
						data = new byte[1024];
						while ((temp = input.read(data)) != -1) {
							outStream.write(data, 0, temp);
						}
					} catch (FileNotFoundException e) {
						// toasterror("�봫����ȷ��������");
						e.printStackTrace();
					} catch (IOException e) {
						// toastemessage("��д����");
						e.printStackTrace();
					} finally {
						try {
							outStream.flush();
							outStream.close();
						} catch (IOException e) {
							// toasterror("�ļ���δ�������ر�");
							e.printStackTrace();
						}
					}
				}
				// toastemessage("���سɹ�");

				// ��ȡ�����ļ��ܴ�С
				int fileSize = data.length;
				if (fileSize <= 0) {
					System.out.println("��ȡ�ļ�ʧ��");
					return;
				}
				// ����ProgressBar���ĳ���Ϊ�ļ�Size
				mProgressbar.setMax(fileSize);

				// ����ÿ���߳����ص����ݳ���
				blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
						: fileSize / threadNum + 1;

				Log.d(TAG, "fileSize:" + fileSize + "  blockSize:");

				File file = new File(filePath);
				for (int i = 0; i < threads.length; i++) {
					// �����̣߳��ֱ�����ÿ���߳���Ҫ���صĲ���
					threads[i] = new FileDownloadThread(url, file, blockSize,
							(i + 1));
					threads[i].setName("Thread:" + i);
					threads[i].start();
				}

				boolean isfinished = false;
				int downloadedAllSize = 0;
				while (!isfinished) {
					isfinished = true;
					// ��ǰ�����߳���������
					downloadedAllSize = 0;
					for (int i = 0; i < threads.length; i++) {
						downloadedAllSize += threads[i].getDownloadLength();
						if (!threads[i].isCompleted()) {
							isfinished = false;
						}
					}
					// ֪ͨhandlerȥ������ͼ���
					Message msg = new Message();
					msg.getData().putInt("size", downloadedAllSize);
					mHandler.sendMessage(msg);
					// Log.d(TAG, "current downloadSize:" +
					// downloadedAllSize);
					Thread.sleep(1000);// ��Ϣ1����ٶ�ȡ���ؽ���
				}
				Log.d(TAG, " all of downloadSize:" + downloadedAllSize);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * ���߳��ļ�����
	 * 
	 * @author yangxiaolong
	 * @2014-8-7
	 */
	class downloadTask extends Thread {
		private String downloadUrl;// �������ӵ�ַ
		private int threadNum;// �������߳���
		private String filePath;// �����ļ�·����ַ
		private int blockSize;// ÿһ���̵߳�������

		public downloadTask(String downloadUrl, int threadNum, String fileptah) {
			this.downloadUrl = downloadUrl;
			this.threadNum = threadNum;
			this.filePath = fileptah;
		}

		@Override
		public void run() {

			FileDownloadThread[] threads = new FileDownloadThread[threadNum];
			try {
				URL url = new URL(downloadUrl);
				Log.d(TAG, "download file http path:" + downloadUrl);
				URLConnection conn = url.openConnection();
				// ��ȡ�����ļ��ܴ�С
				int fileSize = conn.getContentLength();
				if (fileSize <= 0) {
					System.out.println("��ȡ�ļ�ʧ��");
					return;
				}
				// ����ProgressBar���ĳ���Ϊ�ļ�Size
				mProgressbar.setMax(fileSize);

				// ����ÿ���߳����ص����ݳ���
				blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
						: fileSize / threadNum + 1;

				Log.d(TAG, "fileSize:" + fileSize + "  blockSize:");

				File file = new File(filePath);
				for (int i = 0; i < threads.length; i++) {
					// �����̣߳��ֱ�����ÿ���߳���Ҫ���صĲ���
					threads[i] = new FileDownloadThread(url, file, blockSize,
							(i + 1));
					threads[i].setName("Thread:" + i);
					threads[i].start();
				}

				boolean isfinished = false;
				int downloadedAllSize = 0;
				while (!isfinished) {
					isfinished = true;
					// ��ǰ�����߳���������
					downloadedAllSize = 0;
					for (int i = 0; i < threads.length; i++) {
						downloadedAllSize += threads[i].getDownloadLength();
						if (!threads[i].isCompleted()) {
							isfinished = false;
						}
					}
					// ֪ͨhandlerȥ������ͼ���
					Message msg = new Message();
					msg.getData().putInt("size", downloadedAllSize);
					mHandler.sendMessage(msg);
					// Log.d(TAG, "current downloadSize:" +
					// downloadedAllSize);
					Thread.sleep(1000);// ��Ϣ1����ٶ�ȡ���ؽ���
				}
				Log.d(TAG, " all of downloadSize:" + downloadedAllSize);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	// Mac����
	private void requestMacInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Macresponse = HttpUtil.queryStringForPost(Constants.MACREQURL
						+ "?scenicid=1");
				JsonResponse resp = new JsonResponse();
				JSONObject json = resp.getjson(Macresponse);
				Macjsonarray = json.optJSONArray("result");
				// �������������Դ���ж��Ƿ���Ҫ������Դ
				requestRes();
			}
		}).start();
	}

	// ��Դ����
	private void requestRes() {

		if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {
			Toast.makeText(RouteActivity.this, "sd��������", Toast.LENGTH_SHORT)
					.show();
			if (Environment.getRootDirectory() != null) {
				// ��ȡ����·��
				path = Environment.getRootDirectory() + "/resource/";
				File file = new File(path);
				// �������Ŀ¼�����ڴ���
				if (!file.exists()) {
					file.mkdir();
				}
				filelist = file.listFiles();
				if (filelist.length == 0) {
					// �״�����
					isNOhasResFolder();
				} else {
					// �����״�����
					ishasResFolder();
				}
				downstate = true;
			}
		} else {
			// ��ȡSD��·��
			path = Environment.getExternalStorageDirectory() + "/resource/";
			File file = new File(path);
			// ���SD��Ŀ¼�����ڴ���
			if (!file.exists()) {
				file.mkdir();
			}
			filelist = file.listFiles();
			if (filelist.length == 0) {
				// �״�����
				isNOhasResFolder();
			} else {
				// �����״�����
				ishasResFolder();
			}
			downstate = false;
		}

	}

	// �״�����
	public void isNOhasResFolder() {
		if (isNetworkConnected(this)) {
			
					// TODO Auto-generated method stub
					Resresponse = HttpUtil
							.queryStringForPost(Constants.RESOURCEREQURL);
					JsonResponse resp = new JsonResponse();
					JSONObject json = resp.getjson(Resresponse);
					try {
						Resresponse = json.getString("url")
								+ json.getString("zip");
						fistr = json.getString("zip");

						Resjsonarray = json.optJSONArray("result");

					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
			
		}
		downloadlay.setVisibility(View.VISIBLE);
		// ����progressBar��ʼ��
		mProgressbar.setProgress(0);

	}

	// �����״�����
	public void ishasResFolder() {
		strFileName = "";
		for (int i = 0; i < filelist.length; i++) {
			if (filelist[i].isDirectory()) {
			} else {
				strFileName = filelist[i].getName();
			}
		}
		if (isNetworkConnected(this)) {
			
					// TODO Auto-generated method stub
					Resresponse = HttpUtil
							.queryStringForPost(Constants.RESOURCEREQURL
									+ "?name=" + strFileName);
					if (!Resresponse.equals("false")
							&& !Resresponse.equals("�����쳣��")) {
						JsonResponse resp = new JsonResponse();
						JSONObject json = resp.getjson(Resresponse);
						try {
							Resresponse = json.getString("url")
									+ json.getString("zip");
							fistr = json.getString("zip");

							Resjsonarray = json.optJSONArray("result");
							finstate = false;
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} else {
						finstate = true;
					}
				
			
		}
	}

	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	private class MyAdapter extends BaseListAdapter {
		/**
		 * ������
		 */
		private class ViewHolder {

			private ImageView imageView1;
			private TextView txttitle;
			private TextView txttime;
			private TextView txtdistance;

		}

		private int selectItem = -1;

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return title.length;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder = null;
			if (convertView == null) {

				convertView = getLayoutInflater().inflate(
						R.layout.activity_listitem, null);

				holder = new ViewHolder();

				holder.imageView1 = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.txttitle = (TextView) convertView
						.findViewById(R.id.txttitle);
				holder.txttime = (TextView) convertView
						.findViewById(R.id.txttime);
				holder.txtdistance = (TextView) convertView
						.findViewById(R.id.txtdistance);

				// ���ý�����ɫ
				// int[] arrayOfInt = mColors;
				// int colorLength = mColors.length;
				// int selected = arrayOfInt[position % colorLength];
				//
				// convertView.setBackgroundResource(selected);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.imageView1.setBackgroundResource(img[position]);
			// Bitmap image =
			// Bitmap.createBitmap(((BitmapDrawable)holder.imageView1.getDrawable()).getBitmap());
			// imgUtils.getRoundedCornerBitmap(image, 90);
			// imageLoader.displayImage(
			// "drawable://" + (Integer) list.get(position).get("img"),
			// holder.imageView1, options);

			holder.txttitle.setText(title[position]);
			holder.txttime.setText(time[position]);
			holder.txtdistance.setText(distance[position]);

			return convertView;
		}

	}

	// �������ݿ�
	private void insertDB() {
		// ��������
		mDB.beginTransaction();
		try {

			for (int i = 0; i < Macjsonarray.length(); i++) {
				String sql = "select * from MacInfo where ID=?";
				Cursor cursor = null;
				try {
					cursor = mDB.rawQuery(sql, new String[] { Macjsonarray
							.getJSONObject(i).optInt("ID") + "" });
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (cursor.getCount() == 0) {
					ContentValues values = new ContentValues();
					try {
						values.put("ID",
								Macjsonarray.getJSONObject(i).optInt("ID"));
						values.put("macName", Macjsonarray.getJSONObject(i)
								.optString("macName"));
						values.put("scenicId", Macjsonarray.getJSONObject(i)
								.optInt("scenicId"));
						values.put("power", Macjsonarray.getJSONObject(i)
								.optDouble("power"));
						values.put("distance", Macjsonarray.getJSONObject(i)
								.optDouble("distance"));
						values.put("editTime", Macjsonarray.getJSONObject(i)
								.optString("editTime"));
						mDB.insert("MacInfo", null, values);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				cursor.close();
			}
			for (int i = 0; i < Resjsonarray.length(); i++) {
				String sql = "select * from ResInfo where ID=?";
				Cursor cursor = null;
				try {
					cursor = mDB.rawQuery(sql, new String[] { Resjsonarray
							.getJSONObject(i).optInt("ID") + "" });
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (cursor.getCount() == 0) {
					ContentValues values = new ContentValues();
					try {
						values.put("ID",
								Resjsonarray.getJSONObject(i).optInt("ID"));
						values.put("title", Resjsonarray.getJSONObject(i)
								.optString("title"));
						values.put("content", Resjsonarray.getJSONObject(i)
								.optString("content"));
						values.put("bgName", Resjsonarray.getJSONObject(i)
								.optString("bgName"));
						values.put("musicName", Resjsonarray.getJSONObject(i)
								.optString("musicName"));
						values.put("mid",
								Resjsonarray.getJSONObject(i).optInt("mid"));
						values.put("editTime", Resjsonarray.getJSONObject(i)
								.optString("editTime"));
						mDB.insert("ResInfo", null, values);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				cursor.close();
			}
			// ���������־Ϊ�ɹ�������������ʱ�ͻ��ύ����
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			try {
				throw (e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			// ��������
			mDB.endTransaction();
		}

	}

	// ���ص�SD�����������ݿ�
	private void operSD() {
		// ���ص�SD��
		// ����������Ȱ�URL���ļ�����д������ʵ��Щ������ͨ��HttpHeader��ȡ��
		String downloadUrl = Resresponse;
		String fileName = fistr;
		int threadNum = 5;
		String filepath = path + fileName;
		Log.d(TAG, "download file  path:" + filepath);
		task = new downloadTask(downloadUrl, threadNum, filepath);

		task.start();
		insertDB();

	}

	// ���ص��ֻ��ڴ沢�������ݿ�
	private void operLoacl() {
		findViewById(R.id.download_btn).setEnabled(false);
		// ���ص��ֻ�����
		// ����������Ȱ�URL���ļ�����д������ʵ��Щ������ͨ��HttpHeader��ȡ��
		String downloadUrl = Resresponse;
		String fileName = fistr;
		int threadNum = 5;
		String filepath = path + fileName;
		Log.d(TAG, "download file  path:" + filepath);
		task = new downloadTask(downloadUrl, threadNum, filepath);

		task.start();
		insertDB();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btndlist:

			break;
		case R.id.btnback:
			finish();
			break;

		case R.id.imgmap:

			Intent intent = new Intent();
			intent.setClass(this, RouteMapActivity.class);

			startActivity(intent);
			break;
		case R.id.img_close:
			txtlay.setVisibility(View.GONE);
			break;
		case R.id.download_btn:
			if (finstate) {
				Toast.makeText(RouteActivity.this, "����Դ�Ѿ����ع��ˣ�",
						Toast.LENGTH_LONG).show();
				return;
			}
			// ����progressBar��ʼ��
			if (!downstate) {
				operSD();
			} else {
				// ���ص��ֻ��ڴ沢�������ݿ�
				operLoacl();
			}
			break;
		default:
			break;
		}
	}
}
