package com.xiakexing.locate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Fragment;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTRegion;
import com.brtbeacon.sdk.RangingListener;
import com.brtbeacon.sdk.ServiceReadyCallback;
import com.brtbeacon.sdk.service.RangingResult;

public class BleScanService extends Service {

	/** the number indicates the max count of rssi value in the stored list */
	private final int SAMPLE_SIZE = 8;
	private final int SCAN_WAIT_MILLIS = 0;
	private final int SCAN_PERIOD_MILLIS = 250;
	/** the number indicates how often to do actions for UI logical. */
	private static final int TIMES_FOR_LOOP = 4;
	/**
	 * the number indicates over which times the iBeacon will be removed from
	 * scanned list.
	 */
	private static final int TIMES_NO_SCANNED = 10;

	/** ��ȡ�����Ƿ�ʹ���Ż������㷨 */
	private boolean fetchComplex = false;

	/**
	 * fetchMultiBeacon is to use multi-iBeacon at one loop scan,true for
	 * location etc,otherwise for fetching the nearest ibeacon.such as to find
	 * the nearest interest thing.
	 */
	private Boolean fetchMultiBeacon;

	/** �����ִ�,���ѭ�� */
	private int processCount;
	/** ɨ���ִ�,�ڲ�ѭ�� */
	private int loopCount = 0;

	private BRTRegion region;
	/** ��ǰ�ź�����iBeacon */
	private BRTBeacon freshBeacon;
	private BRTBeaconManager brtBeaconMgr;
	private OnBleScanListener onBleScanListener;
	private BleBinder bleBinder = new BleBinder();
	/**
	 * to stored temporary scanned data at frequency of {@link TIMES_FOR_LOOP}
	 * times
	 */
	private ArrayList<BRTBeacon> tempList;
	private ArrayList<BRTBeacon> rangedList;
	private ArrayList<BRTBeacon> optimizedList;
	private ArrayList<BeaconData> allScannedList;

	/** ���濨����������,key:mac��ַ,value:�������˲��������� */
	private HashMap<String, Kalman> kalmanMap;
	/** ������һ��ɨ�赽���ź�ǿ�� ,key:mac��ַ,value:��һ�ε��ź�ǿ�� */
	private HashMap<String, Integer> lastRssiMap;
	/** ����������ε��ź����� ,key:mac��ַ,value:��iBeacon������ε�ɨ���ź� */
	private HashMap<String, ArrayList<Integer>> everyRssimap;

	private Comparator<? super BRTBeacon> rssiComparator = new Comparator<BRTBeacon>() {

		@Override
		public int compare(BRTBeacon lhs, BRTBeacon rhs) {
			return rhs.rssi - lhs.rssi;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		enableBlueToothIfClosed();
		setBeaconManager();
		brtBeaconMgr.connect(new ServiceReadyCallback() {

			@Override
			public void onServiceReady() {
				if (region == null) {
					region = new BRTRegion("xkx", null, null, null, null);
				}
				try {
					brtBeaconMgr.startRanging(region);
				} catch (RemoteException e) {
					// noop.
				}
			}
		});
		return bleBinder;
	}

	private void enableBlueToothIfClosed() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (!adapter.isEnabled()) {
			adapter.enable();
		}
	}

	private int getRssiWithFilter(ArrayList<Integer> list, FilterMode mode) {
		int rssi = 0;
		if (mode == FilterMode.MODE_AVER) {// ƽ��ֵ
			int sum = 0;
			for (int i = 0; i < list.size(); i++) {
				sum += list.get(i);
			}
			rssi = sum / list.size();
		} else if (mode == FilterMode.MODE_MEDIUM) {// ��λ��
			Object[] arr = (Object[]) list.toArray();
			Collections.sort(list);// ��С��������
			int size = list.size();
			if (size % 2 == 0) {
				rssi = Math
						.round((list.get(size / 2 - 1) + list.get(size / 2)) / 2);
			} else {
				rssi = list.get(size / 2);
			}
			list.clear();
			for (int i = 0; i < arr.length; i++) {
				list.add((Integer) arr[i]);
			}
		} else if (mode == FilterMode.MODE_MAX) {
			rssi = list.get(0);
			for (int i = 1; i < list.size(); i++) {
				rssi = list.get(i) > rssi ? list.get(i) : rssi;
			}
		}
		return rssi;
	}

	private void setBeaconManager() {
		brtBeaconMgr = new BRTBeaconManager(this);
		// brtBeaconMgr.setBackgroundScanPeriod(SCAN_PERIOD_MILLIS,
		// SCAN_WAIT_MILLIS);
		brtBeaconMgr.setForegroundScanPeriod(SCAN_PERIOD_MILLIS,
				SCAN_WAIT_MILLIS);

		brtBeaconMgr.setRangingListener(new RangingListener() {
			@Override
			public void onBeaconsDiscovered(RangingResult result) {
				if (fetchMultiBeacon == null) {
					throw new IllegalStateException(
							"setTriggerMode() must be called after service connection established.");
				}
				if (onBleScanListener == null) {
					throw new IllegalStateException(
							"BleScanListener must be set after service connection established.");
				}
				if (fetchMultiBeacon) {
					// filterProcess(result);
					kalmanFilter(result.beacons);
				} else {
					fetchNearOneiBeacon(result);
				}
			}
		});
	}

	/**
	 * ʹ���˲�����ɨ������
	 * 
	 * @param result
	 */
	private void filterProcess(RangingResult result) {
		loopCount++;
		if (everyRssimap == null) {
			everyRssimap = new HashMap<String, ArrayList<Integer>>();
		}
		if (lastRssiMap == null) {
			lastRssiMap = new HashMap<String, Integer>();
		}
		// �ź����ݶ��е���
		for (int i = 0; i < result.beacons.size(); i++) {
			BRTBeacon beacon = result.beacons.get(i);
			if (everyRssimap.containsKey(beacon.macAddress)) {
				// �޷�
				if (Math.abs(lastRssiMap.get(beacon.macAddress) - beacon.rssi) <= 5) {
					ArrayList<Integer> list = everyRssimap
							.get(beacon.macAddress);
					if (list.size() >= SAMPLE_SIZE) {
						list.remove(0);
					}
					list.add(beacon.rssi);
					lastRssiMap.put(beacon.macAddress, beacon.rssi);
				}
			} else {
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(beacon.rssi);
				everyRssimap.put(beacon.macAddress, list);
				lastRssiMap.put(beacon.macAddress, beacon.rssi);
			}
		}
		if (loopCount % TIMES_FOR_LOOP == 0) {
			// �˲�
			List<BRTBeacon> beacons = result.beacons;
			for (int i = 0; i < beacons.size(); i++) {
				BRTBeacon brtBeacon = beacons.get(i);
				brtBeacon.rssi = getRssiWithFilter(
						everyRssimap.get(brtBeacon.macAddress),
						FilterMode.MODE_AVER);
			}
			// ��Ӧ�¼�
			if (beacons.size() >= 1) {
				onBleScanListener.onPeriodScan(beacons);
				BRTBeacon brtBeacon = beacons.get(0);
				onBleScanListener.onNearBeacon(brtBeacon);
				if (freshBeacon == null || !brtBeacon.equals(freshBeacon)) {
					onBleScanListener.onNearBleChanged(freshBeacon, brtBeacon);
				}
				freshBeacon = brtBeacon;
			}
		}
	}

	private void kalmanFilter(List<BRTBeacon> beacons) {
		loopCount++;
		if (everyRssimap == null) {
			everyRssimap = new HashMap<String, ArrayList<Integer>>();
		}
		if (kalmanMap == null) {
			kalmanMap = new HashMap<String, Kalman>();
		}
		for (int i = 0; i < beacons.size(); i++) {
			BRTBeacon brtBeacon = beacons.get(i);
			if (kalmanMap.containsKey(brtBeacon.macAddress)) {
				ArrayList<Integer> list = everyRssimap
						.get(brtBeacon.macAddress);
				if (list.size() >= SAMPLE_SIZE) {
					list.remove(0);
				}
				list.add(brtBeacon.rssi);
				Kalman kalman = kalmanMap.get(brtBeacon.macAddress);
				kalman.valMeas = brtBeacon.rssi;// ����ֵ
				kalman.valEsti = getRssiWithFilter(list, FilterMode.MODE_MEDIUM);// ����ֵ,ȡ��λֵ
				kalman.uncerMeas = Math.abs(kalman.valOpti - brtBeacon.rssi);// ����ֵ��ȷ����,����ֵ�Ͳ���ֵ�ľ���ֵ
				kalman.uncerEsti = Math.abs(kalman.valOpti - kalman.valEsti);// ����ֵ��ȷ����,����ֵ�͹���ֵ�ľ���ֵ
				double uncerEsti = kalman.uncerEsti;
				double uncerMeas = kalman.uncerMeas;
				double devOpti = kalman.devOpti;
				// ����ֵƫ��
				kalman.devEsti = Math.pow(uncerEsti, 2) + Math.pow(devOpti, 2);
				// ����ֵƫ��
				kalman.devMeas = Math.pow(uncerMeas, 2) + Math.pow(devOpti, 2);
				// ����������
				double kalmanGain = Math.sqrt(kalman.devEsti
						/ (kalman.devEsti + kalman.devMeas));
				// ����ֵ
				kalman.valOpti = kalman.valEsti + kalmanGain
						* (kalman.valMeas - kalman.valEsti);
				brtBeacon.rssi = Math.round((float) kalman.valOpti);
				if (brtBeacon.macAddress.equals("EC:98:14:03:87:52")) {
					Log.e("kalman", "����������:" + kalmanGain);
				}
				// ����ֵƫ��
				kalman.devOpti = Math.sqrt((1 - kalmanGain) * kalman.devEsti);
			} else {
				Kalman kalman = new Kalman();
				// ��������ƫ��
				kalman.devOpti = 0;
				kalmanMap.put(brtBeacon.macAddress, kalman);
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(brtBeacon.rssi);
				everyRssimap.put(brtBeacon.macAddress, list);
			}
			if (brtBeacon.macAddress.equals("EC:98:14:03:87:52")) {
				Kalman kalman = kalmanMap.get(brtBeacon.macAddress);
				Log.e("kalman", "����:" + kalman.valMeas + ",����:"
						+ kalman.valEsti + ",����:" + kalman.valOpti
						// + "��ֵƫ��:" + kalman.devEsti
						+ ",����ƫ��:" + kalman.devOpti);
			}
		}
		if (loopCount % TIMES_FOR_LOOP == 0) {
			if (beacons.size() >= 1) {
				BRTBeacon firstBeacon = beacons.get(0);
				onBleScanListener.onPeriodScan(beacons);
				onBleScanListener.onNearBeacon(beacons.get(0));
				if (freshBeacon == null || !firstBeacon.equals(freshBeacon)) {
					onBleScanListener
							.onNearBleChanged(freshBeacon, firstBeacon);
				}
				freshBeacon = firstBeacon;
			}
		}
	}

	class BeaconData implements Comparable<BeaconData> {
		BRTBeacon beacon;
		int selfScanCount = 0;
		int sumRssi = 0;
		int lastScanNum = 0;
		int previousFirstCount = 0;

		public BeaconData(BRTBeacon beacon) {
			this.beacon = beacon;
		}

		@Override
		public int hashCode() {
			return beacon.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BeaconData other = (BeaconData) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (beacon == null) {
				if (other.beacon != null)
					return false;
			} else if (!beacon.equals(other.beacon))
				return false;
			return true;
		}

		private BleScanService getOuterType() {
			return BleScanService.this;
		}

		@Override
		public int compareTo(BeaconData another) {
			return (another.sumRssi / another.selfScanCount)
					- (this.sumRssi / this.selfScanCount);
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		try {
			brtBeaconMgr.stopRanging(region);
		} catch (RemoteException e) {
			// noop
		}
		brtBeaconMgr.disconnect();
		return super.onUnbind(intent);
	}

	/**
	 * ���ھ�ȷ��ȡ���һ������
	 * 
	 * @param result
	 */
	private void fetchNearOneiBeacon(RangingResult result) {
		loopCount++;
		if (tempList == null) {
			tempList = new ArrayList<BRTBeacon>();
		}
		if (rangedList == null) {
			rangedList = new ArrayList<BRTBeacon>();
		}
		List<BRTBeacon> beacons = result.beacons;
		for (int i = 0; i < beacons.size(); i++) {
			BRTBeacon beacon = beacons.get(i);
			int index = tempList.indexOf(beacon);
			if (index == -1) {
				tempList.add(beacon);
			} else {
				BRTBeacon savedBeacon = tempList.get(index);
				boolean rssiLower = savedBeacon.rssi < beacon.rssi;
				savedBeacon.rssi = rssiLower ? beacon.rssi : savedBeacon.rssi;
			}
		}
		if (loopCount % TIMES_FOR_LOOP == 0) {
			processCount++;
			Collections.sort(tempList, rssiComparator);
			rangedList.clear();
			rangedList.addAll(tempList);
			if (fetchComplex) {
				processOptimized();
			} else {
				processRaw();
			}
			tempList.clear();
		}
	}

	/** ����һ��ɨ�赽��iBeaconֱ�Ӵ���,�������ݴ��� */
	private void processRaw() {
		if (rangedList.size() >= 1) {
			onBleScanListener.onPeriodScan(rangedList);
			BRTBeacon currNearBeacon = rangedList.get(0);
			onBleScanListener.onNearBeacon(currNearBeacon);
			if (currNearBeacon != freshBeacon) {
				onBleScanListener.onNearBleChanged(freshBeacon, currNearBeacon);
			}
			freshBeacon = currNearBeacon;
		}
	}

	/** �����޸�ɨ�赽���ź�����,���� */
	@Deprecated
	private void processOptimized() {
		optimizedList.clear();
		if (optimizedList == null) {
			optimizedList = new ArrayList<BRTBeacon>();
		}
		if (allScannedList == null) {
			allScannedList = new ArrayList<BeaconData>();
		}
		for (int i = 0; i < rangedList.size(); i++) {
			BRTBeacon rangedBeacon = rangedList.get(i);
			BeaconData data = new BeaconData(rangedBeacon);
			int index = allScannedList.indexOf(data);
			if (index == -1) {
				data.lastScanNum = processCount;
				data.selfScanCount = data.selfScanCount + 1;
				data.sumRssi += rangedBeacon.rssi;
				allScannedList.add(data);
			} else {
				BeaconData temp = allScannedList.get(index);
				temp.lastScanNum = processCount;
				temp.selfScanCount = temp.selfScanCount + 1;
				temp.sumRssi += rangedBeacon.rssi;
			}
		}

		Iterator<BeaconData> iterator = allScannedList.iterator();
		while (iterator.hasNext()) {
			BeaconData next = iterator.next();
			if (processCount - next.lastScanNum > TIMES_NO_SCANNED) {
				iterator.remove();
			}
		}

		Collections.sort(allScannedList);
		if (allScannedList.size() == 0 || rangedList.size() == 0) {
			return;
		}

		BeaconData allFirst = allScannedList.get(0);
		BRTBeacon rangedFirst = rangedList.get(0);
		// �Ƿ�һ��
		boolean isEqual = allFirst.beacon.equals(rangedFirst);
		// �ź�ǿ�Ⱦ�ֵ�Ƚϵ�һ����
		boolean isRssiHigerThanRanged = allFirst.beacon.rssi > rangedFirst.rssi;
		// ���һ���Ƿ�ɨ�赽
		boolean isLastScanned = processCount == allFirst.lastScanNum;

		final StringBuffer sb = new StringBuffer();

		if (!isEqual && isRssiHigerThanRanged && isLastScanned) {
			int index = rangedList.indexOf(allFirst.beacon);
			BRTBeacon beacon = rangedList.get(index);
			if (Math.abs(beacon.rssi - rangedFirst.rssi) <= 5) {
				optimizedList.add(beacon);
			}
		}

		for (int i = 0; i < rangedList.size(); i++) {
			BRTBeacon rangedBeacon = rangedList.get(i);
			BeaconData data = new BeaconData(rangedBeacon);
			int index = allScannedList.indexOf(data);
			data = allScannedList.get(index);
			int averRssi = data.sumRssi / data.selfScanCount;
			boolean rssiLower = rangedBeacon.rssi < averRssi;
			rangedBeacon.rssi = rssiLower ? averRssi : rangedBeacon.rssi;
			if (!optimizedList.contains(rangedBeacon)) {
				optimizedList.add(rangedBeacon);
			}
		}

		if (optimizedList.size() >= 2) {
			BRTBeacon brtBeacon = optimizedList.get(0);
			int index = allScannedList.indexOf(new BeaconData(brtBeacon));
			BeaconData data = allScannedList.get(index);
			if (!brtBeacon.equals(freshBeacon)) {
				if (processCount - data.previousFirstCount > 1) {
					optimizedList.remove(0);
					optimizedList.add(1, brtBeacon);
				}
				data.previousFirstCount = processCount;
			} else {
				data.previousFirstCount = processCount;
			}
		}

		Collections.sort(optimizedList, rssiComparator);
		if (optimizedList.size() >= 0) {
			if (optimizedList.size() >= 1) {
				onBleScanListener.onPeriodScan(optimizedList);
				BRTBeacon brtBeacon = optimizedList.get(0);
				// onBleScanListener.onNearBeacon(brtBeacon);
				if (freshBeacon == null || !brtBeacon.equals(freshBeacon)) {
					onBleScanListener.onNearBleChanged(freshBeacon, brtBeacon);
					// ��������(�ź�ǿ����ߵ�),��յ����׵������ź��ܺ�,ɨ�����.
					if (!allFirst.beacon.equals(brtBeacon)) {
						BeaconData data = new BeaconData(brtBeacon);
						allScannedList.remove(data);
					}
				}
				freshBeacon = brtBeacon;
			}
		}
	}

	/**
	 * ɨ�����������Ҫɨ���ҳ�棬�󶨴˷��񣬲�ʵ������ӿڡ�
	 * 
	 * @author ztb
	 * 
	 */
	public interface OnBleScanListener {
		/**
		 * ɨ������һ��ibeacon��վ�ı��ˣ��ص��ķ�����
		 * 
		 * @param oriBeacon
		 *            ԭ���������beacon
		 * @param desBeacon
		 *            ���ڵ������beacon
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
		 * ������ɨ����ȡ�����һ��iBeacon,�ص�����
		 * 
		 * @param brtBeacon
		 */
		public void onNearBeacon(BRTBeacon brtBeacon);
	}

	enum FilterMode {
		// ��Щ�˲���ʽ�������1�������������������˲�,�����������˹���.�����˲����������ݲ����д�����.
		// ����ȡ�������iBeaconʱ,�ʺ�ʹ�������˲�֮һ.��ȡ���һ��iBeacon��Ҫʹ���˲�.
		/***************************************************
		 * <pre>
		 * ����ƽ���˲������ֳƻ���ƽ���˲�����
		 * ˵����������N������ֵ����һ�����У����г��ȹ̶�ΪN�� ÿ�β�����һ�������ݷ����β�����ӵ����׵�һ
		 * �����ݡ��Ѷ����е�N�����ݽ���ƽ�����㣬�Ȼ�� �µ��˲������
		 * �ŵ㣺�������Ը��������õ��������ã�ƽ���ȸߣ������ڸ�Ƶ�񵴵�ϵͳ
		 * ȱ�㣺�����ȵͣ���żȻ���ֵ������Ը��ŵ��������ýϲ����������� �Ž����صĳ���
		 * </pre>
		 ****************************************************/
		MODE_AVER,
		/************************************************
		 * <pre>
		 * ��λֵ�˲�
		 * �ŵ㣺����żȻ���ֵ������Ը��ţ���������������Ĳ���ֵƫ� �������Ը��������õ��������ã�ƽ���ȸߣ������ڸ�Ƶ�� ��ϵͳ��
		 * ȱ�㣺�����ٶ���
		 * </pre>
		 *************************************************/
		MODE_MEDIUM,
		/** ��ȡ�����������ֵ */
		MODE_MAX
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

		/**
		 * <pre>
		 * set to false if to trigger one ibeacon ,true to locate with multi-ibeacon. 
		 * must be called to indicate the intent of scan.
		 * </pre>
		 * 
		 * @param isFetchMultiOneTime
		 */
		public void setFetchMultiBeacon(boolean isFetchMultiOneTime) {
			fetchMultiBeacon = isFetchMultiOneTime;
		}

		public void setOnBleScanListener(OnBleScanListener listener) {
			onBleScanListener = listener;
		}
	}
}

/**
 * <pre>
 * ���ݿ������˲��㷨д���࣬���ڱ���ÿ��ʱ�̵�������Ϣ��
 * see at http://blog.chinaunix.net/uid-26694208-id-3184442.html
 * </pre>
 * 
 * @author zhengtianbao
 * 
 */
class Kalman {
	/** ����ֵ */
	int valMeas;
	/** ����ֵ */
	int valEsti;
	/** ����ֵ */
	double valOpti;
	/** ����ֵ��ȷ���� */
	double uncerEsti;
	/** ����ֵ��ȷ���� */
	double uncerMeas;
	/** ����ֵƫ�� */
	double devEsti;
	/** ����ֵƫ�� */
	double devMeas;
	/** ����ֵƫ�� */
	double devOpti;
}
