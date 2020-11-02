package phis.application.yb.source;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class YBDownloadUtil {
	
	/**
	 * 
	 * @param theURL 服务器的IP地址和端口，以及要下载的文件路径
	 * @param filePath 下载到本地的位置
	 * @throws IOException
	 */
	public static void downloadFile(URL theURL, String filePath) throws IOException {  
		   File dirFile = new File(filePath);
		      if(!dirFile.exists()){ 
		        //文件路径不存在时，自动创建目录
		        dirFile.mkdir();
		      }
		  //从服务器上获取图片并保存
		     URLConnection connection = theURL.openConnection();
		     InputStream in = connection.getInputStream();  
		     FileOutputStream os = new FileOutputStream(filePath); 
		     byte[] buffer = new byte[4 * 1024];
		     int read;
		     while ((read = in.read(buffer)) > 0) {
		        os.write(buffer, 0, read);
		          }
		       os.close();
		       in.close();
		  }
	
		  public static void main(String[] args) { 
		      //下面添加服务器的IP地址和端口，以及要下载的文件路径
		      String urlPath = "http://服务器IP地址:端口/image/123.png"; 
		      //下面代码是下载到本地的位置
		      String filePath = "d:\\excel\\123.png";
		      try {
		    	  URL url = new URL(urlPath);
	              downloadFile(url,filePath);
		      } catch (IOException e) {
		    	  e.printStackTrace();
		      }
		  }   
}
