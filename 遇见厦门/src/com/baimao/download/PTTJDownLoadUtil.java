package com.baimao.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class PTTJDownLoadUtil {
	// ������
	private Context c;
	 private URL connectURL;
	 public static String ERROTAG = "DownLoadUtilError";
	 private String path = "mnt/sdcard";
	 /**
	   * ���캯��
	   * 
	  * @param c
	   *            ��Ҫ�ṩ������,�ڳ��ִ���ʱ��ͨ�����������ṩToast��Ϣ�������ò�Ҫ��ΪNULL
	   */
	 public PTTJDownLoadUtil(Context c) {
	   if (c != null) {
	    this.c = c;
	   } else {
	    Log.w(ERROTAG, "ContextΪNULL");
	   }
	 }
	 /**
	   * ��ȡ���ļ�
	   * 
	  * @param url
	   *            �ļ�url
	   * @return �����ļ����ļ�
	   */
	private InputStream getinputStream(String url) {
	   InputStream input = null;
	   try {
	    connectURL = new URL(url);
	    HttpURLConnection conn = (HttpURLConnection) connectURL
	      .openConnection();
	    conn.setRequestMethod("POST");
	    conn.setReadTimeout(1000);
	    input = conn.getInputStream();
	   } catch (MalformedURLException e) {
	    Log.e(ERROTAG, "��ȷ������ĵ�ַ��ȷ��Ȩ���Ƿ����!!!!!!");
	    e.printStackTrace();
	   } catch (IOException e) {
	    toasterror("����ʧ��");
	    Log.w(ERROTAG, "�����·������");
	    e.printStackTrace();
	   }
	   return input;
	 }
	 /**
	   * ��������ı��ļ�������
	   * 
	  * @param url
	   *            �ı��ļ���url
	   * @return �ı��ļ�������
	   */
	public String gettextfilestring(String url) {
	   InputStream input = getinputStream(url);
	   StringBuffer sb = new StringBuffer("");
	   BufferedReader bfr = new BufferedReader(new InputStreamReader(input));
	   String line = "";
	   try {
	    while ((line = bfr.readLine()) != null) {
	     sb.append(line);
	    }
	   } catch (IOException e) {
	    toasterror("�ļ�����д����");
	    e.printStackTrace();
	   } finally {
	    try {
	     bfr.close();
	    } catch (IOException e) {
	     toasterror("�ļ���δ�������ر�");
	     e.printStackTrace();
	    }
	   }
	   return sb.toString();
	 }
	 /**
	   * �����ļ�ָ��Ŀ¼��
	   * @param url�����ַ
	   * @param filename��������
	   */
	public void downFiletoDecive(String url, String filename) {
	   if ((url != null && !"".equals(url))
	     && (filename != null && !"".equals(filename))) {
	    InputStream input = getinputStream(url);
	    FileOutputStream outStream = null;
	    try {
	     outStream = c.openFileOutput(filename,
	       Context.MODE_WORLD_READABLE
	         | Context.MODE_WORLD_WRITEABLE);
	     int temp = 0;
	     byte[] data = new byte[1024];
	     while ((temp = input.read(data)) != -1) {
	      outStream.write(data, 0, temp);
	     }
	    } catch (FileNotFoundException e) {
	     toasterror("�봫����ȷ��������");
	     e.printStackTrace();
	    } catch (IOException e) {
	     toastemessage("��д����");
	     e.printStackTrace();
	    } finally {
	     try {
	      outStream.flush();
	      outStream.close();
	     } catch (IOException e) {
	      toasterror("�ļ���δ�������ر�");
	      e.printStackTrace();
	     }
	    }
	   }
	   toastemessage("���سɹ�");
	 }
	 /**
	   * �ṩ���صķ���
	   * @param url
	   *            �����ļ�������·��
	   * @param path
	   *            ���ص����ص�·��
	   * @param filename
	   *            ���ص��ļ�����
	   */
	public void downFiletoSDCard(String url, String path, String filename) {
	   if ((url != null && !"".equals(url)) && (path != null)
	     && (filename != null && !"".equals(filename))) {
	    InputStream input = getinputStream(url);
	    downloader(input, path, filename);
	   } else {
	    /*
	     * �Բ��Ϸ��Ĳ���������
	     */
	    if (url == null || "".equals(url)) {
	     toasterror("url����Ϊ�ջ�Ϊ����");
	    }
	    if (path == null) {
	     toasterror("path����Ϊ��");
	    }
	    if (filename == null || "".equals(filename)) {
	     toasterror("filename����Ϊ��");
	    }
	   }
	 }
	 /**
	   * ��ʾ������Ϣ
	   * @param message ������Ϣ
	   */
	private void toasterror(String message) {
	   if (c != null) {
	    Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
	   }
	   try {
	    throw new Exception(message);
	   } catch (Exception e) {
	    Log.w(ERROTAG, "δ�ܲ��������쳣");
	    e.printStackTrace();
	   }
	 }
	 /**
	   * ��ʾ��Ϣ
	   * @param message��Ϣ��ʾ
	   */
	private void toastemessage(String message) {
	   if (c != null) {
	    Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
	   }
	   Log.i(ERROTAG, message);
	 }
	 /**
	   * �ļ�����
	   * 
	  * @param input
	   *            �ļ���
	   * @param path
	   *            �洢·��
	   * @param where
	   *            ���ص��豸�ڴ��洢����
	   * @param name
	   *            ���غ��ļ�������
	   */
	private void downloader(InputStream input, String path, String name) {
	   String realpath = null;
	   path =path;
	   if (Environment.getExternalStorageState().equals(
	     Environment.MEDIA_MOUNTED)) {
	    realpath = path;
	   } else {
	    toasterror("SDCard�쳣�����������Ȩ��");
	   }
	   if (!dirsexits(realpath)) {
	    creatdir(realpath);
	   }
	   if (!fileexits(realpath + "/" + name)) {
	    makefile(input, realpath, name);
	   } else {
	    toasterror("�ļ��Ѵ���");
	   }
	 }
	 /**
	   * �ж��ļ�����ļ���Ŀ¼�Ƿ���ڣ�ͬʱ�����ṩ�ӿ�
	   * 
	  * @param path
	   *            ����ļ�Ŀ¼
	   * @return
	   */
	 public boolean dirsexits(String path) {
	   File file = new File(path);
	   Log.i(ERROTAG, "�ļ���" + path + "���������" + file.exists());
	   return file.exists();
	 }
	 /**
	   * �ж��ļ��Ƿ���ڣ�ͬʱ�����ṩ�ӿ�
	   * 
	  * @param path
	   *            �ļ�·��������
	   * @return
	   */
	 public boolean fileexits(String path) {
	   return dirsexits(path);
	 }
	 /**
	   * ����ָ����Ŀ¼
	   * 
	  * @param path
	   *            Ŀ¼��·��
	   */
	private void creatdir(String path) {
	   File file = new File(path);
	   file.mkdirs();
	 }
	 /**
	   * �����ļ�
	   * 
	  * @param input
	   *            ������
	   * @param realpath
	   *            ·��
	   * @param name
	   *            �ļ���ŵ�����
	   */
	private void makefile(InputStream input, String realpath, String name) {
	   File file = new File(realpath + "/" + name);
	   OutputStream out = null;
	   try {
	    out = new FileOutputStream(file);
	    int temp = 0;
	    byte[] data = new byte[1024];
	    while ((temp = input.read(data)) != -1) {
	     out.write(data, 0, temp);
	    }
	   } catch (FileNotFoundException e) {
	    toasterror("�����ļ�ʧ��");
	    e.printStackTrace();
	   } catch (IOException e) {
	    toasterror("��д����");
	    e.printStackTrace();
	   } finally {
	    try {
	     out.flush();
	     out.close();
	    } catch (IOException e) {
	     toasterror("�ļ���δ�������ر�");
	     e.printStackTrace();
	    }
	   }
	   toastemessage("���سɹ�");
	 }
	 }
