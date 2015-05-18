package com.xkx.yjxm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTRegion;
import com.brtbeacon.sdk.RangingListener;
import com.brtbeacon.sdk.ServiceReadyCallback;
import com.brtbeacon.sdk.service.RangingResult;

public class BleScanService extends Service {

	private final int SCAN_WAIT_MILLIS = 0;
	private final int SCAN_PERIOD_MILLIS = 1000;

	private BRTRegion region;
	private BRTBeacon freshBeacon;
	private BRTBeaconManager brtBeaconMgr;
	private OnBleScanListener onBleScanListener;
	private BleBinder bleBinder = new BleBinder();
	private CopyOnWriteArrayList<BRTBeacon> rangedBeacon = new CopyOnWriteArrayList<BRTBeacon>();
	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

	public BleScanService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void enableBlueToothIfClosed() {
		if (!adapter.isEnabled()) {
			adapter.enable();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		enableBlueToothIfClosed();
		brtBeaconMgr = new BRTBeaconManager(this);
		brtBeaconMgr.setForegroundScanPeriod(SCAN_PERIOD_MILLIS,
				SCAN_WAIT_MILLIS);

		brtBeaconMgr.setRangingListener(new RangingListener() {

			@Override
			public void onBeaconsDiscovered(RangingResult result) {
				processScanResult(result);
			}

			private void processScanResult(RangingResult result) {
				rangedBeacon.clear();
				rangedBeacon.addAll(result.beacons);
				onBleScanListener.onPeriodScan(rangedBeacon);
				if (rangedBeacon.size() == 0) {
					return;
				}
				BRTBeacon nearBeacon = rangedBeacon.get(0);// ��0λ���ź�ǿ����ߵ�
				onBleScanListener.onNearBle(nearBeacon);
				boolean isMacEqual = (freshBeacon == null ? true
						: freshBeacon.macAddress
								.equalsIgnoreCase(nearBeacon.macAddress));
				if (!isMacEqual && nearBeacon.rssi > -73) {
					onBleScanListener.onNearBleChanged(freshBeacon, nearBeacon);
				}
				freshBeacon = nearBeacon;
			}
		});
		brtBeaconMgr.connect(new ServiceReadyCallback() {

			@Override
			public void onServiceReady() {
				if (region == null) {
					region = new BRTRegion("xkx", null, null, null, null);
				}
				try {
					brtBeaconMgr.startRanging(region);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		return bleBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		try {
			brtBeaconMgr.stopRanging(region);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		brtBeaconMgr.disconnect();
		// �ر�����
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		adapter.disable();
		return super.onUnbind(intent);
	}

	/**
	 * ɨ�����������Ҫɨ���ҳ�棬�󶨴˷��񣬲�ʵ������ӿڡ�
	 * 
	 * @author ztb
	 * 
	 */
	public interface OnBleScanListener {
		/**
		 * ɨ������һ��ble��վ�ı��ˣ��ص��ķ�����
		 * 
		 * @param oriBeacon
		 *            ԭ���������ble
		 * @param desBeacon
		 *            ���ڵ������ble
		 */
		public void onNearBleChanged(BRTBeacon oriBeacon, BRTBeacon desBeacon);

		/**
		 * ������ɨ��ص�����
		 * 
		 * @param scanResultList
		 *            ɨ�赽��beacon�б�
		 */
		public void onPeriodScan(List<BRTBeacon> scanResultList);

		/**
		 * ������ɨ����ȡ�����һ��ble���ص�����
		 * 
		 * @param brtBeacon
		 */
		public void onNearBle(BRTBeacon brtBeacon);
	}

	public class BleBinder extends Binder {

		/**
		 * ����ɨ�������
		 * 
		 * @param uuid
		 */
		public void setRegion(String uuid) {
			region = new BRTRegion("xkx", uuid, null, null, null);
		}

		public BRTBeacon getProximityBeacon() {
			if (rangedBeacon != null && rangedBeacon.size() > 0) {
				return rangedBeacon.get(0);
			} else {
				return null;
			}
		}

		public void setOnBleScanListener(OnBleScanListener listener) {
			onBleScanListener = listener;
		}

		/**
		 * ��ȡ����ָ��������beacons
		 * 
		 * @param num
		 *            Ҫ��ȡ�ĸ���
		 * @return
		 */
		public List<BRTBeacon> getBRTBeacons(Integer num) {
			if (rangedBeacon.size() == 0) {
				return null;
			}
			if (num == null || num < 0) {
				return rangedBeacon;
			} else {
				List<BRTBeacon> temp = new ArrayList<BRTBeacon>();
				int size = rangedBeacon.size();
				num = num > size ? size : num;
				for (int i = 0; i < num; i++) {
					temp.add(rangedBeacon.get(i));
				}
				return temp;
			}
		}
	}
}
