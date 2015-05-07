package com.xkx.goldenox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class BottomFragment extends Fragment {
	private View rootView;// ����Fragment view
	private GifView GifView1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_bottom, null);
		}
		// �����rootView��Ҫ�ж��Ƿ��Ѿ����ӹ�parent��
		// �����parent��Ҫ��parentɾ����Ҫ��Ȼ�ᷢ�����rootview�Ѿ���parent�Ĵ���
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
//		initUI(rootView);

		return rootView;
	}

	
	private void initUI(View rootView) {

		GifView1 = (GifView) rootView.findViewById(R.id.GifView1);
		GifView1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int topleftx = x - 10;

				// left, top, right, bottom
				int y = (int) event.getY();
				int toplefty = y - 10;
				//textView1.setText("x=" + x + ",y=" + y);
				// ���Ͻ�
				int topRightx = x + 10;
				int topRighty = y - 10;

				// ���½�
				int bottomleftx = x - 10;
				int bottomlefty = y + 10;

				// ���Ͻ�
				int bottomRightx = x + 10;
				int bottomRighty = y + 10;

				// LayoutParams lp = new lay
				// textView1.setLayoutParams())
				if (event.getAction() == MotionEvent.ACTION_UP) {

					String title = "";

					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {

						title = "ʵ������";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO �滻frament
						fragTrans.replace(R.id.menu_frame, new ShijinyulanFragment(), "");
						fragTrans.commit();
					}
					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {

						title = "����ӡ��";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO �滻frament
						fragTrans.replace(R.id.menu_frame, new BottomFragment(), "");
						fragTrans.commit();
					}
					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {

						title = "��;�羰";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO �滻frament
						fragTrans.replace(R.id.menu_frame, new YantuFragment(), "");
						fragTrans.commit();
					}

					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {

						title = "�������";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO �滻frament
						fragTrans.replace(R.id.menu_frame, new BianminFragment(), "");
						fragTrans.commit();
					}
					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {

						title = "�οͻ���";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO �滻frament
						fragTrans.replace(R.id.menu_frame, new YoukehudongFragment(), "");
						fragTrans.commit();
					}
					if (x >= x - 20
							&& x <= x + 20
							&& y >= y - 20
							&& y <= y + 20) {
						title = "��ͼ��ѯ";
						FragmentManager fragMgr =getActivity().getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr.beginTransaction();
						// TODO �滻frament
						fragTrans.replace(R.id.menu_frame, new MapserchFragment(), "");
						fragTrans.commit();
					}
					// if (event.getX() >= Integer.parseInt(xMap.get(2))
					// && event.getY() < Integer.parseInt(xMap.get(2))) {
					//
					// mapID = 1;
					// title = "����̨";
					// process(mapID, title);
					// }

				}
				return true;
			}
		});

	}
}
