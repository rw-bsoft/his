package platform.security.source;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;

public class LogFilter implements Filter {
	private final static String DEFAULT_USERID = "IDError";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(false);
		if (session == null) {
			MDC.put("userId", DEFAULT_USERID);
		} else {
			// 用户的id
			String uid = (String) session.getAttribute("uid");
			if(StringUtils.isEmpty(uid)){
				MDC.put("userId", DEFAULT_USERID);
			} else {
				MDC.put("userId", uid.toString().split("=")[0]);
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {}
	@Override
	public void destroy() {}

}
