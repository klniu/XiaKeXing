package com.xkx.yjxm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.xkx.yjxm.R;
import com.xkx.yjxm.adpater.BaseListAdapter;
import com.xkx.yjxm.utils.imgUtils;

//����·��
public class RouteActivity extends Activity implements OnClickListener {
	private ImageButton btnlist;
	private ImageButton btndlist;
	private ImageButton btnback;
	private ListView listView1;
	private List<Map<String, Object>> list;
	DisplayImageOptions options; // ����ͼƬ���ؼ���ʾѡ��
	private MyAdapter myAdapter;
	private ImageView imgmap;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �Ƴ�ActionBar����setContent֮ǰ����������䣬��֤û��ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_route);

		initData();
		initUI();
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
		btndlist = (ImageButton) findViewById(R.id.btndlist);
		btndlist.setOnClickListener(this);
		btnback = (ImageButton) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
		btnlist = (ImageButton) findViewById(R.id.btnlist);
		btnlist.setOnClickListener(this);

		imgmap = (ImageView) findViewById(R.id.imgmap);
		imgmap.setOnClickListener(this);
		listView1 = (ListView) findViewById(R.id.listView1);
		myAdapter = new MyAdapter();
		listView1.setAdapter(myAdapter);
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
			return list.size();
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
			
			
			
			
			
			holder.imageView1.setBackgroundResource((Integer) list.get(position).get("img"));
//			Bitmap image = Bitmap.createBitmap(((BitmapDrawable)holder.imageView1.getDrawable()).getBitmap());  
//			imgUtils.getRoundedCornerBitmap(image, 90);
//			imageLoader.displayImage(
//					"drawable://" + (Integer) list.get(position).get("img"),
//					holder.imageView1, options);
			
			holder.txttitle.setText((String) list.get(position).get("title"));
			holder.txttime.setText((String) list.get(position).get("time"));
			Map<String, Object> map = list.get(position); // distance
			String object = (String) list.get(position).get("distance");
			holder.txtdistance.setText(object);

			return convertView;
		}

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
		default:
			break;
		}
	}
}
