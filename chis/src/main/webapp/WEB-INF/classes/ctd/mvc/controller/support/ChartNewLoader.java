package ctd.mvc.controller.support;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import ctd.account.UserRoleToken;
import ctd.chart.ReportSchema;
import ctd.chart.ReportSchemaController;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.net.rpc.util.ServiceAdapter;
import ctd.schema.SchemaItem;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;

@Controller("mvcChartNewLoader")
public class ChartNewLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartNewLoader.class);
	
	@RequestMapping(value="/**/{id}.chartNew")
	public void doChartService(
			@PathVariable("id") String id,
			HttpServletRequest request,HttpServletResponse response
		){
		try {
			UserRoleToken token = UserRoleTokenUtils.getUserRoleToken(request);
			ContextUtils.put(Context.USER_ROLE_TOKEN, token);
			ContextUtils.put(Context.USER, token);
			HashMap<String,Object> query = null;
			if(request.getMethod().equals("GET")){
				query = parseQueryString(request.getQueryString());
			}else{
				query = parseRequest(request);
			}
			//init context
			Context ctx = ContextUtils.getContext();
			ctx.putCtx("q", query);
			ctx.put(Context.APP_CONTEXT, AppContextHolder.get());
			
			String path=(String)query.get("path");
			if(path!=null && !path.equals("undefined")){
				path=path.replace("_", "/");
				id=path+"/"+id;
			}
			ReportSchema reportSchema = ReportSchemaController.instance().get(id);
			//for result
			
			String serviceId=(String)query.get("serviceId");
			String serviceAction=(String)query.get("serviceAction");
			List parametersList=new ArrayList();
			parametersList.add(query);
			Map reqData=new HashMap();
			Map resData=new HashMap();
			Object result = ServiceAdapter.invokeWithUnconvertedParameters(serviceId, serviceAction, parametersList, query, resData);
			List<Map<String, Object>> rs = (List)((Map)result).get("result");
			int totalCount = (Integer)((Map)result).get("totalCount");
			
			
			if(rs.size() > 0){
				FreeMarkerConfigurer fc = (FreeMarkerConfigurer)AppContextHolder.getBean("freeMarkerConfigurer");
				Configuration freemarker = fc.getConfiguration();
				String chartTemplate = (String)query.get("template");  //added by zhouz
				if(chartTemplate==null || chartTemplate.equals("undefined")){
					chartTemplate=reportSchema.getChartTemplate();
				}
				Template t = freemarker.getTemplate(chartTemplate+".ftl");
				Writer writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream())); 
				response.addHeader("content-type","text/xml;charset=gb2312");
				HashMap<String,Object> data = perpareData(reportSchema,rs,query);
				data.put("chartId", query.get("chartId"));
				data.put("subTitle", reportSchema.getSubTitle(ctx));
				data.put("limit",totalCount);
				String title=(String)query.get("title");
				if(title!=null && !title.equals("undefined")){
					data.put("title", URLDecoder.decode(title, "utf-8")+reportSchema.getTitle());
				}
				t.process(data, writer);
			}
			else{
				try {
					Writer writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream())); 
					response.addHeader("content-type","text/xml;charset=gb2312");
					writer.write("<chart/>");
					writer.close();
					//response.getWriter().write("<chart/>");
				} 
				catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	protected HashMap<String,Object> perpareData(ReportSchema reportSchema,List<Map<String, Object>> rs,HashMap query) throws ControllerException{
		HashMap<String,Object> data = new HashMap<String,Object>();
		List<SchemaItem> scItems = reportSchema.getHeaders();
		List<HashMap<String,Object>> headers=new ArrayList<HashMap<String,Object>>();
		for(SchemaItem scItem:scItems){
			HashMap<String,Object> s=JSONUtils.parse(JSONUtils.toString(scItem), HashMap.class);
			s.putAll((Map) s.get("properties"));
			headers.add(s);
		}
		
		if(query.get("navDic")!=null && query.get("columns")!=null){
			Dictionary dic= DictionaryController.instance().get((String)query.get("navDic"));
			List<Object> cols=null;
			try {
				cols = JSONUtils.parse((String)query.get("columns"), List.class);
			} 
			catch (Exception e) {
				LOGGER.error("parse reportCondition failed:", e);
			}
			
			for(int i=0;i<cols.size();i++){
				HashMap<String,Object> h=(HashMap<String,Object>)headers.get(headers.size()-1).clone();
				String c=(String)cols.get(i);
				int renderIndex= Integer.parseInt(String.valueOf(h.get("renderIndex")))+i+1;
				h.put("id", c);
				h.put("alias", dic.getItem((String)cols.get(i)).getText());
				h.put("renderIndex",String.valueOf(renderIndex));
				h.remove("hidden");
				headers.add(h);
			}
		}
		data.put("title", reportSchema.getTitle());
		data.put("headers",headers );
		int limit = reportSchema.getChartLimit();
		if(limit == 0){
			limit = rs.size();
		}
		HashMap diggers = reportSchema.getDiggers();
		if(diggers != null){
			data.put("diggers", diggers);
		}
		data.put("rs", rs);
		data.put("limit",limit);
		return data;
	}
	
	public static HashMap<String,Object> parseQueryString(String q){
		HashMap<String,Object> query = new  HashMap<String,Object>();
		query.put("chartId", "");
		q = q.substring(2);
		if(q != null){
			int p = q.indexOf("@");
			if(p > -1){
				String[] qus = q.split("@");
				int size = qus.length;
					for(int i = 0; i < size; i ++){
						String[] kv = qus[i].split(";");
						if(kv.length == 2){
							try {
								String key = URLDecoder.decode(kv[0],"utf-8");
								String value = URLDecoder.decode(kv[1],"utf-8").replace("%5B", "[").
								replace("%5D", "]").
								replace("%2B","+");
								query.put(key,value);
							} 
							catch (UnsupportedEncodingException e) {
								LOGGER.error("parseQueryStringFailed:", e);
							}
						}
					}
			}
			else{
				String[] kv = q.split(";");
				if(kv.length == 2){
					try {
						String key = URLDecoder.decode(kv[0],"utf-8");
						String value = URLDecoder.decode(kv[1],"utf-8").replace("%5B", "[").
						replace("%5D", "]").
						replace("%2B","+");
						query.put(key,value);
					} 
					catch (UnsupportedEncodingException e) {
						LOGGER.error("parseQueryStringFailed:", e);
					}
				}
			}
		}
		return query;
	}
	
	public static HashMap<String,Object> parseRequest(HttpServletRequest request){
		try{
			String encoding = request.getCharacterEncoding();
			if(encoding == null){
				encoding = "UTF-8";
				request.setCharacterEncoding(encoding);
			}
			InputStream ins = request.getInputStream();
			return JSONUtils.parse(ins, HashMap.class);
		}
		catch(Exception e){
			LOGGER.error("parseRequest failed:", e);
		}
		return new HashMap<String,Object>();
	}

}
