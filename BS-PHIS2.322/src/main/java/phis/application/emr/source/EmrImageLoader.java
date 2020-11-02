/**
 * 
 */
package phis.application.emr.source;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import sun.misc.BASE64Decoder;

import com.bsoft.pix.utils.Base64;

/**
 * @author Administrator
 * 
 */
public class EmrImageLoader extends HttpServlet implements Servlet {
	private static final long serialVersionUID = -8260076694457868118L;
	private static Log logger = LogFactory.getLog(EmrImageLoader.class);
	private static WebApplicationContext wac;

	public void init(ServletConfig oCfg) throws ServletException {
		wac = WebApplicationContextUtils.getRequiredWebApplicationContext(oCfg
				.getServletContext());
		logger.info("Servlet init");
	}

	private String getFileId(String url) {
		int p = url.lastIndexOf("/");
		String id = "";
		if (p > -1 && p < url.length()) {
			id = url.substring(p + 1);
		}
		p = id.indexOf(".");
		if (p > -1) {
			id = id.substring(0, p);
		}
		return id;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String fileId = getFileId(request.getRequestURI());
		if (fileId.length() == 0) {
			logger.error("Request Url's arg [fileId] missing");
			return;
		}
		SessionFactory sf = (SessionFactory) wac.getBean("mySessionFactory");
		Session ss = sf.openSession();
		try {
			List<Map<String, Object>> list = ss
					.createQuery("from EMR_BL04 where FJMC=:FJMC")
					.setString("FJMC", fileId).list();
			// ht.find(
			// "", fileId, Hibernate.STRING);
			if (list.size() > 0) {
				HashMap rec = (HashMap) list.get(0);
				Blob blob = (Blob) rec.get("FJNR");
				StringBuffer fjnr = new StringBuffer();
				if (blob != null) {
					InputStream is = blob.getBinaryStream();
					Reader reader = new InputStreamReader(is);

					int charValue = 0;
					while ((charValue = reader.read()) != -1) {
						fjnr.append((char) charValue);
					}
				}
				BASE64Decoder bd = new BASE64Decoder();
				// StrBinaryTurn.StrToBool(fjnr.toString());
				byte[] buf = bd.decodeBuffer(fjnr.toString());
				if (buf != null) {
					response.setHeader("content-Type", "image/jpeg");
					response.getOutputStream().write(buf);
				}
			} else {
				logger.info("file not found for filedId:" + fileId);
				return;
			}

		} catch (Exception e) {
			logger.error("load file from database failed:", e);
		} finally {
			ss.close();
		}
	}

	public static void main(String[] args) {
		System.out.println(Base64.base64Decode("abcd"));
		System.out.println(Base64.base64Encode(Base64.base64Decode("abcd")));

	}

	public static byte[] hex2byte(String str) { // 字符串转二进制
		if (str == null)
			return null;
		str = str.trim();
		int len = str.length();
		if (len == 0 || len % 2 == 1)
			return null;
		byte[] b = new byte[len / 2];
		try {
			for (int i = 0; i < str.length(); i += 2) {
				b[i / 2] = (byte) Integer
						.decode("0X" + str.substring(i, i + 2)).intValue();
			}
			return b;
		} catch (Exception e) {
			return null;
		}
	}

}
