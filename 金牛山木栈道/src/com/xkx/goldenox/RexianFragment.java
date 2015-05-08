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
import android.widget.ImageView;
import android.widget.Toast;

public class RexianFragment extends Fragment {
	private View rootView;// ����Fragment view
	private ImageView GifView1;
	private int[] m_minX = { 0 };
	private int[] m_minY = { 0 };
	private int[] m_maxX = { 190 };
	private int[] m_maxY = { 77 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_rexian, null);
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

	private void initUI(View rootView) {

		GifView1 = (ImageView) rootView.findViewById(R.id.GifView1);
		GifView1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				// Toast.makeText(getActivity(), "x=" + x + "y=" + y,
				// Toast.LENGTH_SHORT).show();
				for (int i = 0; i < m_minX.length; i++) {
					int x1 = m_minX[i];
					int x2 = m_maxX[i];
					int y1 = m_minY[i];
					int y2 = m_maxY[i];
					if (x < m_maxX[i] && x > m_minX[i] && y < m_maxY[i]
							&& y > m_minY[i]) {
						FragmentManager fragMgr = getActivity()
								.getSupportFragmentManager();
						FragmentTransaction fragTrans = fragMgr
								.beginTransaction();
						// TODO �滻frament
						Fragment fragment = null;
						switch (i) {
						case 0:
							fragMgr.popBackStack();
							break;
						case 1:
							fragment = new FudaoFragment();
							break;
						case 2:
							fragment = new YantuFragment();
							break;
						case 3:
							fragment = new BianminFragment();
							break;
						case 4:
							fragment = new YoukehudongFragment();
							break;
						case 5:
							fragment = new MapserchFragment();
							break;
						}
						fragTrans.commit();

					}
				}

				return true;
			}
		});
	}

}
