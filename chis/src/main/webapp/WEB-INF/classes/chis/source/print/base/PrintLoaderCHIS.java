package chis.source.print.base;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.mvc.controller.support.PrintLoader;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.print.ColumnModel;
import ctd.print.DynaGridPrintUtil;
import ctd.print.PrintExporter;
import ctd.print.PrintUtil;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.security.exception.SecurityException;
import ctd.service.core.ServiceExecutor;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

@Controller("mvcPrintLoaderCHIS")
public class PrintLoaderCHIS {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PrintLoaderCHIS.class);
	private final static String DefaultContent = "text/html;charset=UTF-8";

	@RequestMapping(value = "/**/list.chisprint", method = RequestMethod.GET)
	public void doListPrint(
			@RequestParam(value = "type", required = false, defaultValue = "1") Integer type,
			@RequestParam(value = "config") String config,
			@RequestParam(value = UserRoleTokenUtils.SESSION_UID_KEY, required = false) String uid,
			@RequestParam(value = UserRoleTokenUtils.SESSION_TOKEN_KEY, required = false) Integer token,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType(DefaultContent);
		HttpSession s = request.getSession(false);
		if (s == null) {
			s = request.getSession();
			s.setAttribute(UserRoleTokenUtils.SESSION_UID_KEY, uid);
			s.setAttribute(UserRoleTokenUtils.SESSION_TOKEN_KEY, token);
		}
		try {
			UserRoleToken urt = UserRoleTokenUtils.getUserRoleToken(request);
			ContextUtils.put(Context.USER_ROLE_TOKEN, urt);
			ContextUtils.put(Context.USER, urt);
			HashMap<String, Object> cfg = JSONUtils.parse(
					URLDecoder.decode(config, "UTF-8"), HashMap.class);
			String title = (String) cfg.get("title");
			String page = (String) cfg.get("page");
			HashMap<String, Object> requestData = (HashMap<String, Object>) cfg
					.get("requestData");
			if ("whole".equals(page)) { // print 999 records when whole. 浦口修改 255*255
				requestData.remove("pageNo");
				requestData.put("pageSize", 255*255);
			}
			String schemaId = (String) requestData.get("schema");
			Schema sc = SchemaController.instance().get(schemaId);
			if (S.isEmpty(title)) {
				title = sc.getAlias();
			}
			List<SchemaItem> items = sc.getItems();
			List<String> cname = (List<String>) cfg.get("cname");
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			for (String id : cname) {
				for (int j = 0; j < items.size(); j++) {
					SchemaItem item = items.get(j);
					if (id.equals(item.getId())
							|| id.equals(item.getId() + "_text")) {
						ColumnModel cm = new ColumnModel();
						String valueType = item.getType();
						cm.setType(valueType);
						if ("datetime".equals(valueType)
								|| "timestamp".equals(valueType)) {
							if ("datefield".equals(item.getProperty("xtype"))) {
								cm.setPattern("yyyy/MM/dd");
							}
						}
						if ("date".equals(valueType)) {
							cm.setPattern("yyyy/MM/dd");
						}
						if (!S.isEmpty(S.obj2String(item.getProperty("width")))) {
							cm.setWdith(Integer.parseInt(S.obj2String(item
									.getProperty("width"))));
						}
						if (item.getLength() != null) {
							cm.setLength(item.getLength());
						}
						if (item.getPrecision() != null) {
							int p = item.getPrecision();
							cm.setPrecision(p);
							StringBuilder sb = new StringBuilder("###0.");
							for (int i = 0; i < p; i++) {
								sb.append("0");
							}
							cm.setPattern(sb.toString());
						}
						cm.setName(id);
						cm.setText(S.obj2String(item.getAlias()));
						map.put(id, cm);
						break;
					}
				}
			}
			ColumnModel[] columnModel = map.values().toArray(
					new ColumnModel[map.size()]);
			boolean isSeparate = PrintUtil.REPORT_TYPE_OF_EXCEL == type ? false
					: true;
			List<JasperReport> reports = DynaGridPrintUtil
					.getDynamicJasperReport(new ArrayList<JasperDesign>(),
							title, columnModel, isSeparate);
			List<JasperPrint> prints = new ArrayList<JasperPrint>();
			// data
			HashMap<String, Object> res = new HashMap<String, Object>();
			String serviceId = "simpleQuery";
			if (requestData.get("serviceId") != null) {
				serviceId = (String) requestData.get("serviceId");
			}
			requestData.put("print", "true");//yx 用于判断是导出还是页面查询，修复bug
			ServiceExecutor.execute(serviceId, requestData, res,
					ContextUtils.getContext());
			List<HashMap<String, Object>> data = (List<HashMap<String, Object>>) res
					.get("body");
			for (HashMap<String, Object> r : data) { // record
				List<String> key_dic = new ArrayList<String>();
				for (Iterator<String> it = r.keySet().iterator(); it.hasNext();) {
					String k = it.next();
					int text_index = k.lastIndexOf("_text");
					if (text_index != -1) {
						String dic_key = k.substring(0, text_index);
						if (r.containsKey(dic_key)) {
							key_dic.add(dic_key);
						}
					}
				}
				for (String kd : key_dic) {
					r.put(kd, r.get(kd + "_text"));
				}
			}
			for (JasperReport r : reports) {
				prints.add(PrintUtil.getJasperPrint(r,
						new HashMap<String, Object>(), DynaGridPrintUtil
								.createJRBeanCollectionDataSource(columnModel,
										data)));
			}
			PrintUtil.exportToHttpServletResponse(type, prints, request,
					response, title);
		} catch (ControllerException e) {
			if (e.isInstanceNotFound()) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			LOGGER.error(e.getMessage(), e);
		}

		catch (SecurityException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOGGER.error(e.getMessage(), e);
		} finally {
			ContextUtils.clear();
		}
	}

	@RequestMapping(value = "/**/*.chisprint", method = RequestMethod.GET)
	public void doPagesPrint(
			@RequestParam(value = "type", required = false, defaultValue = "1") Integer type,
			@RequestParam(value = "pages") String pages,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType(DefaultContent);
		try {
			UserRoleToken token = UserRoleTokenUtils.getUserRoleToken(request);
			ContextUtils.put(Context.USER_ROLE_TOKEN, token);
			List<String> pgs = Arrays.asList(pages.split(","));
			HashMap<String, Object> requestMap = parseRequest(request);
			HashMap<String, Object> responseMap = new HashMap<String, Object>();
			PrintExporter.export(requestMap, responseMap,
					ContextUtils.getContext(), pgs, type, request, response,
					null);
		} catch (ControllerException e) {
			if (e.isInstanceNotFound()) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			LOGGER.error(e.getMessage(), e);
		}

		catch (SecurityException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOGGER.error(e.getMessage(), e);
		} finally {
			ContextUtils.clear();
		}
	}

	private HashMap<String, Object> parseRequest(HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			if (key.equalsIgnoreCase("type") || key.equalsIgnoreCase("pages")) {
				continue;
			}
			map.put(key, request.getParameter(key));
		}
		return map;
	}

}
