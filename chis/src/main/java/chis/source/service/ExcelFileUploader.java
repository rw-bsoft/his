/**
 * @(#)ExcelFileUploader.java Created on 2012-6-5 上午11:13:57
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import chis.source.util.MapperUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.account.user.UserController;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.mvc.controller.MVCController;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.security.exception.SecurityException;
import ctd.service.core.Service;
import ctd.util.AppContextHolder;
import ctd.util.JSONProtocol;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.exception.CodedBaseException;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
@Controller("excelFileUploader")
public class ExcelFileUploader extends MVCController {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(AbstractActionService.class);
	private static final String ENCODING = "UTF-8";
//	private static WebApplicationContext wac;
	private static final String DIR_TYPE = "dirType";
	private static final String FILE_NAME = "fileName";
	private static long fileUploadMaxSize;
	private static String tempDirectory;
	private static ServletFileUpload fileUpload;
	private String tempPath;

	@RequestMapping(value = "/excelUploadForm", method = RequestMethod.POST)
	public void init(@RequestParam(value="serviceId",required=false) String serviceId,
			@RequestParam(value="serviceAction",required=false) String serviceAction ,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024*1024);
		fileUpload = new ServletFileUpload(factory);
		fileUpload.setSizeMax(1024*1024*20);
		fileUpload.setFileSizeMax(fileUploadMaxSize);
		
		int code = 200;
		String msg = "Success";
		HashMap<String,Object> res = new HashMap<String,Object>();
		try {
			UserRoleTokenUtils.getUserRoleToken(request);
			res.put(Service.RES_CODE, code);
			res.put(Service.RES_MESSAGE, msg);
			HashMap<String,Object> rec = startUpload(request,serviceId,serviceAction);
			if(rec.size()>0){
				res.putAll(rec);
			}	
		}catch (SecurityException e) {
			res.put(JSONProtocol.CODE, SecurityException.NOT_LOGON);
			res.put(JSONProtocol.MSG, "NotLogon");
			logger.error(e.getMessage());
		}catch(Exception e){
			Throwable t = e.getCause();
			res.put(JSONProtocol.CODE,((CodedBaseException)t).getCode());
			res.put(JSONProtocol.MSG,t.getMessage());
			logger.error(t.getMessage(),t);
		} 
		writeToResponse(response, res);		
//		wac = WebApplicationContextUtils.getRequiredWebApplicationContext(oCfg
//				.getServletContext());
//		String limitSize = oCfg.getInitParameter("fileUploadMaxSize");
//		if (StringUtils.isEmpty(limitSize)) {
//			limitSize = "5M";
//		}
//		fileUploadMaxSize = getByteSizeFromString(limitSize);
//		String tempPath = oCfg.getInitParameter("tempDirectory");
//		if (StringUtils.isEmpty(tempPath)) {
//			tempPath = "resources/temp";
//		}
//		tempDirectory = oCfg.getServletContext().getRealPath(tempPath);
//		DiskFileItemFactory factory = new DiskFileItemFactory();
//		factory.setSizeThreshold(1024 * 1024);
//		fileUpload = new ServletFileUpload(factory);
//		fileUpload.setSizeMax(1024 * 1024 * 20);
//		fileUpload.setFileSizeMax(fileUploadMaxSize);

	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> startUpload(HttpServletRequest req,String serviceId,String serviceAction) {
		HashMap<String,Object> res = new HashMap<String,Object>();		
		HashMap<String,Object> rec = new HashMap<String,Object>();
		String path = tempDirectory;
		tempPath = path;
		String fileName = "";
		Service service = null;
		try {
			List<FileItem> fis = null;
			try {
				fis = fileUpload.parseRequest(req);//如果不是提交表单的情况下不需要fls
			} catch (Exception e) {
			}
			if(fis!=null)
			for (FileItem fi : fis) {
				if (fi.isFormField()) {
					String filed = fi.getFieldName();
					if (DIR_TYPE.equals(filed)) {
//						String tempPath = this.getServletConfig()
//								.getInitParameter(fi.getString());
						if (!StringUtils.isEmpty(tempPath)) {
							path = req.getSession().getServletContext().getRealPath(tempPath);
						}
					}
					if (FILE_NAME.equals(filed)) {
						fileName = fi.getString();
					}
				} else {
					String name = fi.getName();
					if (StringUtils.isEmpty(fileName)) {
						fileName = name.substring(name.lastIndexOf("\\") + 1);
					}
					checkDir(path);
					path = path + File.separator + fileName;
					File file = new File(path);
					fi.write(file);
				}
			}
			Map<String, Object> request = new HashMap<String, Object>();
			request.put("serviceAction", serviceAction);
			request.put("fileName", fileName);
			request.put("path", path);
			service = (Service)AppContextHolder.getBean(serviceId);
			UserRoleToken token = UserRoleTokenUtils.getUserRoleToken(req);
			ContextUtils.put(Context.USER_ROLE_TOKEN, token);
			ContextUtils.put(Context.USER, token);
			ContextUtils.put(Context.HTTP_REQUEST, request);
			service.execute(request, res,ContextUtils.getContext());
		} catch (Exception e) {
			res.put(Service.RES_CODE, 403);
			res.put(Service.RES_MESSAGE, e.getMessage());
			logger.fatal("exception: " + e.getMessage());
			e.printStackTrace();
		}
		return rec;
	}

	private void writeToResponse(HttpServletResponse response,
			HashMap<String, Object> res) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), ENCODING);
		MapperUtil.getJsonMapper().writeValue(out, res);
	}
	


	private void checkDir(String path) {
		File f = new File(path);
		if (!f.exists()) {
			f.mkdir();
		}
	}

	
	public void setTempDirectory(String path){
		tempDirectory = path;
	}
	
	private long getByteSizeFromString(String s){
		long size = 0l;
		String unit = "m";
		Pattern p = Pattern.compile("(\\d+)([mMkK])");
		Matcher m = p.matcher(s);		
		if(!m.find()){
			return size;
		}
		if(m.groupCount() == 2){
			size = Long.parseLong(m.group(1));
			unit = m.group(2).toLowerCase();
			
			if(unit.equals("m")){
				size *= 1024 * 1024; 
			}
			if(unit.equals("k")){
				size *= 1024;
			}
		}	
		return size;
	}
	
	public void setFileUploadMaxSize(String limitSize){
		fileUploadMaxSize = getByteSizeFromString(limitSize);
	}
	
}
