package com.xiakexing.locate;

/**
 * <pre>
 * ���ݿ������˲��㷨д���࣬���ڱ���ÿ��ʱ�̵�������Ϣ��
 * see at http://blog.chinaunix.net/uid-26694208-id-3184442.html
 * </pre>
 * 
 * @author Administrator
 * 
 */
public class Kalman {
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
