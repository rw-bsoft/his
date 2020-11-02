package phis.source.controller;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ctd.util.AppContextHolder;
import ctd.util.JSONProtocol;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.exception.CodedBaseException;

@Controller("emrImageLoader")
public class EmrImageLoader {
	private static final Logger logger = LoggerFactory
			.getLogger(EmrImageLoader.class);

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "{fileId}.emrImageLoad", method = RequestMethod.GET)
	public void doLoad(@PathVariable("fileId") String fileId,
			HttpServletRequest request, HttpServletResponse response) {
		// String fileId = request.getParameter("fileId");
		HashMap<String, Object> resData = new HashMap<String, Object>();
		Session ss = null;
		try {
			// UserRoleToken token =
			// UserRoleTokenUtils.getUserRoleToken(request);
			// ContextUtils.put(Context.USER_ROLE_TOKEN, token);
			// ContextUtils.put(Context.USER, token);
			Context ctx = ContextUtils.getContext();
			ss = (Session) ctx.get(Context.DB_SESSION);
			if (ss == null) {
				SessionFactory sf = AppContextHolder.getBean(
						AppContextHolder.DEFAULT_SESSION_FACTORY,
						SessionFactory.class);
				ss = sf.openSession();
				ctx.put(Context.DB_SESSION, ss);
			}
			List<Map<String, Object>> list = ss
					.createQuery("from EMR_BL04 where FJMC=:FJMC")
					.setString("FJMC", fileId).list();
			if (list.size() > 0) {
				HashMap<String, Object> rec = (HashMap<String, Object>) list
						.get(0);
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
				byte[] buf = Base64.decodeBase64(fjnr.toString());
				if (buf != null) {
					response.setHeader("content-Type", "image/jpeg");
					response.getOutputStream().write(buf);
				}
			} else {
				logger.info("file not found for filedId:" + fileId);
			}
		} catch (Exception e) {
			Throwable t = e.getCause();
			if (t instanceof CodedBaseException) {
				resData.put(JSONProtocol.CODE,
						((CodedBaseException) t).getCode());
				resData.put(JSONProtocol.MSG, t.getMessage());
				logger.error(t.getMessage(), t);
			} else {
				logger.error(e.getMessage(), e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
			ContextUtils.clear();
		}

	}
}
