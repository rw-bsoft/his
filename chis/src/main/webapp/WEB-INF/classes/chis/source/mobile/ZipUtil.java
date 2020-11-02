/**
 * @(#)ZipUtil.java Created on 2013-04-16 上午13:44:07
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mobile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @description
 * 
 * @author <a href="mailto:suny@bsoft.com.cn">suny</a>
 */

public class ZipUtil {
	public static void main(String[] args) {
		getWebRootUrl();
	}

	/**
	 * 
	 * @param txtName
	 * @param zipName
	 */
	public static void toZip(String txtName, String zipName) {
		try {
			File txt = new File(txtName);
			File zip = new File(zipName);
			InputStream input = new FileInputStream(txt);
			ZipOutputStream zipOut = null;
			zipOut = new ZipOutputStream(new FileOutputStream(zip));
			zipOut.putNextEntry(new ZipEntry(txt.getName()));
			int temp = 0;
			while ((temp = input.read()) != -1) {
				zipOut.write(temp);
			}
			zipOut.close();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param data
	 * @param txtName
	 */
	public static void toTxt(String data, String txtName) {
		try {
			File txt = new File(txtName);
			OutputStreamWriter osw = new OutputStreamWriter(
					new FileOutputStream(txt, true), "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(data.toString());
			bw.close();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把数据转换成zip输入流
	 * 
	 * @param data
	 * @param prefix
	 * @return
	 */
	public static ZipInputStream streamInput(String data, String prefix) {
		ZipInputStream zipIn = null;
		String webRootUrl = getWebRootUrl();
		try {
			String zipName = webRootUrl + prefix + ".zip";
			String txtName = webRootUrl + prefix + ".txt";
			File txt = new File(txtName);
			File zip = new File(zipName);
			if (txt.exists()) {
				txt.delete();
			}
			if (zip.exists()) {
				zip.delete();
			}
			toTxt(data, txtName);
			toZip(txtName, zipName);
			InputStream input = new FileInputStream(new File(zipName));
			zipIn = new ZipInputStream(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zipIn;

	}

	/**
	 * 获取项目根目录的绝对路径
	 * 
	 * @return
	 */
	public static String getWebRootUrl() {
		String url = ZipUtil.class.getResource("ZipUtil.class").toString();
		int index = url.indexOf("WEB-INF");
		String root = url.substring(6, index);
		root = root + "mobile/";
		if (root.contains("%20")) {
			root = root.replaceAll("%20", " ");
		}
		return root;
	}

	/**
	 * 获取文件的最后修改时间是否是当天
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean getModifiedTime(String fileName) {
		File file = new File(getWebRootUrl() + fileName + ".zip");
		Calendar cal = Calendar.getInstance();
		long time = file.lastModified();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.setTimeInMillis(time);
		String last = formatter.format(cal.getTime());
		String now = formatter.format(new Date());
		boolean eq = last.equals(now);
		return eq;
	}
}
