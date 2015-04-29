package com.xkx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;

public class BitmapUtils {
	/** 
	    * ���ر���ͼƬ 
	    * @param url 
	    * @return 
	    */  
	    public static Bitmap getLoacalBitmapByAssets(Context c, String url)  
	    {  
	        Bitmap bitmap = null;  
	        InputStream in = null;  
	        try  
	        {  
	            in = c.getResources().getAssets().open(url);  
	            bitmap = BitmapFactory.decodeStream(in);  
	            
	            
//	          //ͨ��openRawResource��ȡһ��inputStream����  
//	            InputStream inputStream = getResources().openRawResource("file:/"+imageUriArray[position]);  
//	            //ͨ��һ��InputStream����һ��BitmapDrawable����  
//	            BitmapDrawable drawable = new BitmapDrawable(inputStream);  
//	             //ͨ��BitmapDrawable������Bitmap����  
//	             Bitmap bitmap = drawable.getBitmap();  
	            //����Bitmap���󴴽�����ͼ  
	              bitmap = ThumbnailUtils.extractThumbnail(bitmap, 51, 108);  
	            //imageView ��ʾ����ͼ��ImageView  
	              //imageView.setImageBitmap(bitmap);  
	        }  
	        catch (IOException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        finally  
	        {  
	            closeStream(in, null);  
	        }  
	        return bitmap;  
	    }  
	  
	    /** 
	    * �ӷ�����ȡͼƬ 
	    * @param url 
	    * @return 
	    */  
	    public static Bitmap getHttpBitmap(String url)  
	    {  
	        URL myFileUrl = null;  
	        Bitmap bitmap = null;  
	        InputStream in = null;  
	        try  
	        {  
	            myFileUrl = new URL(url);  
	            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();  
	            conn.setConnectTimeout(0);  
	            conn.setDoInput(true);  
	            conn.connect();  
	            in = conn.getInputStream();  
	            bitmap = BitmapFactory.decodeStream(in);  
	        }  
	        catch (IOException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        finally  
	        {  
	            closeStream(in, null);  
	        }  
	        return bitmap;  
	    }  
	  
	    /**  
	     * �ر���������� 
	     * @param in 
	     * @param out 
	     */  
	    public static void closeStream(InputStream in, OutputStream out)  
	    {  
	        try  
	        {  
	            if (null != in)  
	            {  
	                in.close();  
	            }  
	            if (null != out)  
	            {  
	                out.close();  
	            }  
	        }  
	        catch (IOException e)  
	        {  
	            e.printStackTrace();  
	        }  
	    }  
}
