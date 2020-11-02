package phis.source.service.remind;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @Author: wy
 * @Date: 2019/10/10 14:09
 */
public class RemindServer {
    public static void sendMsgToRemind(String postxml,String ip) {
        try {
        	 postxml = new String(postxml.getBytes("utf-8"), "utf-8");
             System.out.println("over-----"+getEncoding(postxml));

            Socket socket = new Socket(ip, 5025);
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            pw.write(postxml);
            pw.flush();
            InputStream in = socket.getInputStream();
            byte[] data = new byte[1024];
            int len = in.read(data);
           
            pw.close();
            os.close();
            socket.close();
        } catch (Exception e) {
			if(e.getMessage().contains("timed out")){
				System.out.println("智能提醒socket请求失败:"+ip);
				return;
			}
            e.printStackTrace();
        }
    }
    public static  String getEncoding(String str) { 
		String encode = "GB2312"; 
		try { 
		if (str.equals(new String(str.getBytes(encode), encode))) { //判断是不是GB2312
		String s = encode; 
		return s; //是的话，返回“GB2312“，以下代码同理
		} 
		} catch (Exception exception) { 
		} 
		encode = "ISO-8859-1"; 
		try { 
		if (str.equals(new String(str.getBytes(encode), encode))) { //判断是不是ISO-8859-1
		String s1 = encode; 
		return s1; 
		} 
		} catch (Exception exception1) { 
		} 
		encode = "UTF-8"; 
		try { 
		if (str.equals(new String(str.getBytes(encode), encode))) { //判断是不是UTF-8
		String s2 = encode; 
		return s2; 
		} 
		} catch (Exception exception2) { 
		} 
		encode = "GBK"; 
		try { 
		if (str.equals(new String(str.getBytes(encode), encode))) { //判断是不是GBK
		String s3 = encode; 
		return s3; 
		} 
		} catch (Exception exception3) { 
		} 
		return ""; //如果都不是，说明输入的内容不属于常见的编码格式。
	}
    public static void main(String[] args) {

        sendMsgToRemind("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<YL_ACTIVE_ROOT>\n" +
                "  <CFDDM>3103</CFDDM>           \n" +
                "  <KH>320122196210201632</KH>\n" +
                "  <KLX>01</KLX>           \n" +
                "  <YLJGDM>320111426051227</YLJGDM> \n" +
                "  <JZLX>100</JZLX>\n" +
                "  <YYKSBM>267</YYKSBM>\n" +
                "  <YYYSGH>01311213</YYYSGH>\n" +
                "  <AGENTIP>10.1.7.11</AGENTIP>\n" +
                "  <AGENTMAC>ff-ff-ff-ff-ff</AGENTMAC>\n" +
                "  <YSXM>王平</YSXM>\n" +
                "  <YP> \n" +
                "    <ITEM>        \n" +
                "      <BMLX>10</BMLX>\n" +
                "      <YBYPDM>null</YBYPDM>\n" +
                "    </ITEM>       \n" +
                "  </YP>       \n" +
                "</YL_ACTIVE_ROOT>", "192.168.20.3");
    }
}
