package com.xkx.yjxm.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEService extends Service {

	private BleBinder bleBinder = new BleBinder();

	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

	public BLEService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return bleBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public class BleBinder extends Binder {
		public BLEService getService() {
			return BLEService.this;
		}
	}

	public void startScanBLE() {
		if (!adapter.isEnabled()) {
			adapter.enable();
		}

		new Thread() {
			public void run() {

			};
		}.start();
	}

	private LeScanCallback callback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			final BluetoothDevice temp = device;
			// String name = verify(device);
			Log.e("blue", ",rssi=" + rssi);
			// if (name.contains("xBeacon")) {
			// if (rssi > -80) {// TODO �㹻����,��ʼ����
			// Log.e("blue", verify(temp) + "������ʾ");
			// SharedPreferences sp = getSharedPreferences("prefs",
			// Context.MODE_PRIVATE);
			// boolean isOnRouteMapActivity = sp.getBoolean(
			// "isOnRouteMapActivity", false);
			// if (isOnRouteMapActivity) {
			// sendBroad(device);
			// } else {
			// showNotification(device);
			// }
			// } else {// ��������������ɨ��
			// Log.e("blue", "����������2�������ɨ��");
			// resetScan();
			// }
			// }
		}
	};

	public void stopScanBLE() {
		adapter.stopLeScan(callback);
	}

}
