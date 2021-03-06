package com.xkx.goldenox;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class YantuFragment extends Fragment {
	private View rootView;// 缓存Fragment view
	private ImageView GifView1;
	private int[] m_minX = { 481,0};
	private int[] m_minY = { 603 ,0};
	private int[] m_maxX = { 724 ,190};
	private int[] m_maxY = { 791 ,77};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_yantu, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
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
//				Toast.makeText(getActivity(), "x=" + x + "y=" + y,
//						Toast.LENGTH_SHORT).show();
					for (int i = 0; i < m_minX.length; i++) {
						int x1 = m_minX[i];
						int x2 = m_maxX[i];
						int y1 = m_minY[i];
						int y2 = m_maxY[i];
						if (x < m_maxX[i] && x > m_minX[i] && y < m_maxY[i]
								&& y > m_minY[i]) {
							FragmentManager fragMgr = getActivity()
									.getFragmentManager();
							FragmentTransaction fragTrans = fragMgr
									.beginTransaction();
							fragTrans.setCustomAnimations(R.anim.card_flip_vertical_right_in, R.anim.card_flip_vertical_right_out, R.anim.cube_left_in, R.anim.cube_left_out);
							// TODO 替换frament
							Fragment fragment = null;
							switch (i) {
							case 0:
								fragment = new YantuParkFragment();
								fragTrans.replace(R.id.menu_frame, fragment, "");
								fragTrans.addToBackStack(null);
								break;
							case 1:
								fragMgr.popBackStack();
								break;
							case 2:
								break;
							case 3:
								break;
							case 4:
								break;
							case 5:
								break;

							default:
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
