package com.xiakexing.locate;

public class Utils {
	/**
	 * estimote�ļ�����뷽��
	 * 
	 * @param rssi
	 * @param power
	 * @return
	 */
	public static double estimoteCalDis(int rssi, int power) {
		if (rssi == 0) {
			return -1.0D;
		}

		double ratio = rssi / power;
		double rssiCorrection = 0.96D + Math.pow(Math.abs(rssi), 3.0D) % 10.0D / 150.0D;

		if (ratio <= 1.0D) {
			return Math.pow(ratio, 9.98D) * rssiCorrection;
		}
		return (0.103D + 0.89978D * Math.pow(ratio, 7.71D)) * rssiCorrection;
	}

	/**
	 * ��ʯ�ļ�����뷽��
	 * 
	 * @param rssi
	 * @param power
	 * @return
	 */
	public static double brightCalDis(int rssi, int power) {
		if ((rssi >= 0) || (power >= 0))
			return -1.0D;
		double ratio = rssi * 1.0d / power;
		double rssiCorrection = 0.96D + Math.pow(Math.abs(rssi), 3.0D) % 10.0D / 150.0D;
		if (ratio <= 1.0D)
			return Math.pow(ratio, 9.98D) * rssiCorrection;
		double d3 = Math.max(0.0D, (0.103D + 0.89978D * Math.pow(ratio, 7.5D))
				* rssiCorrection);
		if ((0.0D / 0.0D) == d3)
			return -1.0D;
		return d3;
	}

	/**
	 * altbeacon library �ͺ� Nexus 4 �������Ĺ�ʽ
	 * 
	 * @param txPower
	 * @param rssi
	 * @return
	 */
	// distance = Math.pow(ratio, 10.0D);
	// distance = 0.42093 * Math.pow(ratio, 6.9476) + 0.54992;
	public static double altCalDis(int rssi, int txPower) {
		if (rssi == 0.0D) {
			return -1.0D;
		}
		double ratio = rssi * 1.0D / txPower;
		double distance;
		// TODO
		if (ratio < 1.0D) {
			distance = Math.pow(ratio, 10.0D);
			// double rssiCorrection = 0.96D + Math.pow(Math.abs(rssi), 3.0D) %
			// 10.0D / 150.0D;
			// distance = Math.pow(ratio, 9.98D) * rssiCorrection;
		} else {
			// distance = -32.65 * Math.pow(ratio, -1.119) + 32.71;
			distance = 27.5 * Math.pow(ratio, 1.118) - 27.47;// 6.12 ���
			// distance = 0.42093 * Math.pow(ratio, 6.9476) + 0.54992;
		}
		return distance;
	}

	/**
	 * ����ƻ���ļ�����뷽��
	 * 
	 * @param txPower
	 *            1�״���⵽�Ĺ��ʣ�����ֵ�����������á�
	 * @param rssi
	 *            ��⵽���ź�ǿ��
	 * @return
	 */
	protected static double calculateAccuracy(int rssi, int txPower) {
		if (rssi == 0) {
			return -1.0; // if we cannot determine accuracy, return -1.
		}

		double ratio = rssi * 1.0 / txPower;
		if (ratio < 1.0) {
			return Math.pow(ratio, 10);
		} else {
			double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
			return accuracy;
		}
	}

	/**
	 * �����ֵܵļ�����뷽��
	 * 
	 * @param rssi
	 * @param power
	 * @return
	 */
	public static double aprilCalDis(int rssi, int power) {
		if (rssi == 0) {
			return -1.0D;
		}
		if (power == 0) {
			return -1.0D;
		}
		double d1 = rssi / power;
		double d2 = 0.96D + Math.pow(Math.abs(rssi), 3.0D) % 10.0D / 150.0D;

		if (d1 <= 1.0D) {
			return Math.pow(d1, 9.98D) * d2;
		}
		return (0.103D + 0.89978D * Math.pow(d1, 7.71D)) * d2;
	}

	/**
	 * ��������,see more at:
	 * 
	 * <pre>
	 * http://stackoverflow.com/questions/20416218/understanding-ibeacon-distancing/20434019#20434019
	 * </pre>
	 * 
	 * @param txCalibratedPower
	 *            1�״���⵽�Ĺ���
	 * @param rssi
	 *            ��⵽���ź�ǿ��
	 * @return
	 */
	public static double getRange(int rssi, int txCalibratedPower) {
		int ratio_db = txCalibratedPower - rssi;
		double ratio_linear = Math.pow(10, ratio_db / 10);

		double r = Math.sqrt(ratio_linear);
		return r;
	}
}
