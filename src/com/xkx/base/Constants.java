package com.xkx.base;

public class Constants {
	//public static String HTTP_HOST="http://117.25.182.242:28083/mmineapp/v1/";
	public static String HTTP_HOST="http://www.xmlyt.cn/";
	public static String HTTP_LOGIN="index/login.htm";                   //��¼
	public static String HTTP_SIGN="mobile/index/sign.htm";                   //ǩ��
	public static String HTTP_SIGNLIST="mobile/index/sign_list.htm";       //ǩ����ַ�б�
	public static String HTTP_CITYDATA="opportunity/opportunity_manage/subordinate.htm";       //���س�������
	
	public static String HTTP_OPPUNITY="opportunity/opportunity_manage/subordinate_result.htm";       //�����ͻ�����
	public static String HTTP_CHANGEPWD="changePassword.do";      //修改密码             
	public static String HTTP_GETPOSITION="getPosition.do";       //人员定位
	public static String HTTP_GETENVIRONMENT="getEnvironment.do"; //环境监控
	public static String HTTP_GETALARM="getAlarm.do";             //报警统计
	public static String HTTP_VERSION="version.do";               //版本更新
	public static String RESCULTCODE="0";  //请求成功返回�?
	public static final int REFRESH= 1;     //Message
	public static String appVer="1";     //程序版本
	public static String flatVer="1";    //平台版本
	public static int DELAY=50000;//30S轮询
	public static int DELAY2=5000;//30S轮询
	public static String device = "android";
	private Constants() {
	}

	// ����
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	// ������
	public static class Extra {
		public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}
	
}
