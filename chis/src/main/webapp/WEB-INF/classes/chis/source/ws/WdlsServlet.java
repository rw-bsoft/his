package chis.source.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import ctd.util.AppContextHolder;

public class WdlsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		String timestamp=request.getParameter("timestamp");
		String idcardtemp="";
		try {
			idcardtemp=URLEncoder.encode(request.getParameter("idcard"),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String idcard=EncryptMethodDesUrl.decrypt(idcardtemp, "zhls4321", "des");
		if(idcard==null){
			System.out.println("接口有请求过来，但传参不对");
			response.setCharacterEncoding("GBK");
			try {
				PrintWriter out = response.getWriter();
				out.print("传参不对");
				out.flush();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JSONObject req=new JSONObject();
		try {
			req.put("idcard", idcard);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			response.setCharacterEncoding("GBK");
			PrintWriter out = response.getWriter();
			out.print(EncryptMethodDesUrl.encrypt(doWork(req.toString()), "zhls4321", "des"));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		try {
			InputStream p = request.getInputStream();
			byte[] buffer = new byte[1024];
			int l;
			StringBuilder str = new StringBuilder();
			while ((l = p.read(buffer)) > 0) {
				str.append(new String(buffer, 0, l));
			}
			System.out.println(EncryptMethodDesUrl.decrypt(str.toString(), "zhls4321", "des"));
			try {
				response.setCharacterEncoding("GBK");
				PrintWriter out = response.getWriter();
				out.print(EncryptMethodDesUrl.encrypt(doWork(EncryptMethodDesUrl.decrypt(str.toString(),
						"zhls4321", "des")), 
						"zhls4321", "des"));
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String doWork(String param) {
		WdlsWsService gs = (WdlsWsService) AppContextHolder.get()
				.getBean("wdlsWsService");
		return gs.execute(param);
	}
}
