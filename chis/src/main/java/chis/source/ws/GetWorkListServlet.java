package chis.source.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ctd.util.AppContextHolder;

public class GetWorkListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		String p = request.getParameter("request");
		try {
			p=new String(p.getBytes("iso8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		try {
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print( doWork(p));
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
			StringBuilder sb = new StringBuilder();
			while ((l = p.read(buffer)) > 0) {
				sb.append(new String(buffer, 0, l));
			}
			System.out.println(getClass().getName()+"调用doPost方法");
			
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
//	        out.print( doWork(sb.toString()));
			out.print( doWork(URLDecoder.decode(sb.toString(), "utf-8")));
	        out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String doWork(String param) {
		GetWorkListService gs = (GetWorkListService) AppContextHolder.get()
				.getBean("getWorkListService");
		return gs.execute(param);
	}
}
