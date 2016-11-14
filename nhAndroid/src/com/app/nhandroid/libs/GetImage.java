package com.app.nhandroid.libs;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * @˵�� �������ȡͼƬ������
 * @author ����ǿ
 * @version 1.0
 * @since
 */
public class GetImage {
	/**
	 * ����
	 * @param args
	 */
//	public static void main(String[] args) {
//		String url = "http://www.baidu.com/img/baidu_sylogo1.gif";
//		byte[] btImg = getImageFromNetByUrl(url);
//		if(null != btImg && btImg.length > 0){
//			System.out.println("��ȡ����" + btImg.length + " �ֽ�");
//			String fileName = "�ٶ�.gif";
//			writeImageToDisk(btImg, fileName);
//		}else{
//			System.out.println("û�дӸ����ӻ������");
//		}
//	}
	/**
	 * ��ͼƬд�뵽����
	 * @param img ͼƬ������
	 * @param fileName �ļ�����ʱ������
	 */
	public static void writeImageToDisk(byte[] img, String fileName){
		try {
			File file = new File(fileName);
			FileOutputStream fops = new FileOutputStream(file);
			fops.write(img);
			fops.flush();
			fops.close();
//			System.out.println("ͼƬ�Ѿ�д�뵽C��");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ���ݵ�ַ������ݵ��ֽ���
	 * @param strUrl �������ӵ�ַ
	 * @return
	 */
	public static byte[] getImageFromNetByUrl(String strUrl){
		try {
			URL url = new URL(strUrl);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url
					.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			InputStream inStream = conn.getInputStream();//ͨ����������ȡͼƬ����
			byte[] btImg = readInputStream(inStream);//�õ�ͼƬ�Ķ���������
			return btImg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * ���������л�ȡ����
	 * @param inStream ������
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len=inStream.read(buffer)) != -1 ){
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}