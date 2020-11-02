/**
 * @(#)HERFileDownLoadController.java Created on 2013-11-8 下午2:09:03
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import chis.source.BaseDAO;
import chis.source.her.EducationMaterialModule;
import chis.source.util.BSCHISUtil;
import ctd.mvc.controller.JSONOutputMVCConroller;
import ctd.service.core.ServiceException;
import ctd.util.JSONUtils;
import ctd.util.ServletUtils;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
@Controller("emDownLoadController")
public class EMDownLoadController extends JSONOutputMVCConroller {

	private static final Logger logger = LoggerFactory
			.getLogger(WebServiceLogonController.class);

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "*.emDownLoad", method = RequestMethod.GET)
	public void doDownLoad(HttpServletRequest request, HttpServletResponse response) {
		try {
			String fileId = request.getParameter("fileId");
			BaseDAO dao = new BaseDAO();
			EducationMaterialModule emModule = new EducationMaterialModule(dao);
			Map<String, Object> rsMap = null;
			rsMap = emModule.getEduMaterialByPkey(fileId);
			if (rsMap == null) {
				throw new ServiceException("获取文件信息失败！");
			}
			if (rsMap == null || rsMap.size() == 0) {
				return;
			}

			String path = (String) rsMap.get("fileSite");
			String fileName = (String) rsMap.get("fileName");
			File f = new File(path);
			if (!f.exists()) {
				throw new ServiceException("文件已不存在，下载失败。");
			}
			String suffix = fileName.substring(fileName.lastIndexOf("."));
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			response.reset();
			response.addHeader(
					"Content-Disposition",
					"attachment;filename="
							+ new String((BSCHISUtil.toString(new Date(),
									"yyyyMMddHHmmss") + suffix).getBytes()));
			response.setContentType("application/octet-stream");
			response.setCharacterEncoding(ServletUtils.DEFAULT_ENCODING);
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("file downLoad failed.");
		}

	}

	

}
