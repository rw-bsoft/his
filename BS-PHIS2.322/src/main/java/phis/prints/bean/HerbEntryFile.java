package phis.prints.bean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.MapperUtil;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.util.context.Context;

public class HerbEntryFile implements IHandler {

	@SuppressWarnings("unchecked")
	public void getFields(Map<String, Object> request,List<Map<String, Object>> records, Context ctx)throws PrintException {
		String strConfig = (String) request.get("config");
		try {
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
			HashMap<String, Object> config = MapperUtil.getJsonMapper().readValue(strConfig, HashMap.class);
			List<Map<String, Object>> cflist = (List<Map<String, Object>>) config.get("listData");
			for (int i = 0; i < cflist.size(); i = i + 2) {
//			for (int i = 0; i < cflist.size(); i = i + 3) {
				Map<String, Object> cf = new HashMap<String, Object>();
				cf.put("YPMC1",cflist.get(i).get("YZMC")+"");
				if (cflist.get(i).get("YCSL") != null&& cflist.get(i).get("YCSL") != "") {
					cf.put("YPJL1",(int) Math.floor(Double.parseDouble(cflist.get(i).get("YCSL")+""))+ "");
				}
				if (cflist.get(i).get("JLDW") != null && cflist.get(i).get("JLDW") != "") {
					cf.put("JLDW1", cflist.get(i).get("JLDW") + "");
				}
				if (cflist.get(i).get("JZ_text") != null && cflist.get(i).get("JZ_text") != "") {
					if(cflist.get(i).get("JZ_text").equals("煎")){
						cf.put("YFMC1", " ");
					} else {
						cf.put("YFMC1", "(" + cflist.get(i).get("JZ_text") + ")");
					}
				}
				cf.put("JZ1", "(" + cflist.get(i).get("JZ_text") + ")");
				if (i + 1 < cflist.size()) {
					cf.put("YPMC2",cflist.get(i+1).get("YZMC")+"");
					if (cflist.get(i + 1).get("YCSL") != null && cflist.get(i + 1).get("YCSL") != "") {
						cf.put("YPJL2",(int) Math.floor(Double.parseDouble(cflist.get(i + 1).get("YCSL")+"")) + "");
					}
					if (cflist.get(i + 1).get("JLDW") != null && cflist.get(i + 1).get("JLDW") != "") {
						cf.put("JLDW2", cflist.get(i + 1).get("JLDW") + "");
					}
					if (cflist.get(i + 1).get("JZ_text") != null && cflist.get(i + 1).get("JZ_text") != "") {
						if(cflist.get(i + 1).get("JZ_text").equals("煎")){
							cf.put("YFMC2", " ");
						} else {
							cf.put("YFMC2", "(" + cflist.get(i + 1).get("JZ_text") +")");
						}
					}
					cf.put("JZ2","(" + cflist.get(i + 1).get("JZ_text") +")");
				}
//				if (i + 2 < cflist.size()) {
//					cf.put("YPMC3", cflist.get(i+2).get("YZMC")+"");
//					if (cflist.get(i + 2).get("YCSL") != null && cflist.get(i + 2).get("YCSL") != "") {
//						cf.put("YPJL3",(int) Math.floor(Double.parseDouble(cflist.get(i + 2).get("YCSL")+"")) + "");
//					}
//					if (cflist.get(i + 2).get("JLDW") != null && cflist.get(i + 2).get("JLDW") != "") {
//						cf.put("JLDW3", cflist.get(i + 2).get("JLDW") + "");
//					}
//					if (cflist.get(i + 2).get("JZ_text") != null && cflist.get(i + 2).get("JZ_text") != "") {
//						if(cflist.get(i + 2).get("JZ_text").equals("煎服")){
//							cf.put("YFMC3", " ");
//						} else {
//							cf.put("YFMC3", "(" + cflist.get(i + 2).get("JZ_text") +")");
//						}
//					}
//					cf.put("JZ3", "(" + cflist.get(i + 2).get("JZ_text") +")");
//				}
				if(i+1==cflist.size()){
					cf.put("YPMC2", "(以下空白)");
				}
				if(i+2==cflist.size()){
					cf.put("YPMC3", "(以下空白)");
				}
				records.add(cf);
			}
			if(cflist.size()%2==0){
//				if(cflist.size()%3==0){
					Map<String, Object> cfkb = new HashMap<String, Object>();
					cfkb.put("YPMC1", "(以下空白)");
					records.add(cfkb);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("title", user.getManageUnit().getName()+ "处方笺");
		response.put("JZHM", "");
		BaseDAO dao = new BaseDAO(ctx);
		try {
			String strConfig = (String) request.get("config");
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
			HashMap<String, Object> config = MapperUtil.getJsonMapper().readValue(strConfig, HashMap.class);
			Map<String, Object> formData = (Map<String, Object>) config.get("formData");
			long ZYH  = Long.parseLong(config.get("zyh")+"");
			long YZZH  = Long.parseLong(config.get("yzzh")+"");
			Map<String,Object> brxx = dao.doLoad(BSPHISEntryNames.ZY_BRRY, ZYH);
			String sql1 = "select max(a.KSSJ) as KFRQ,count(a.XMLX) as CYLX,sum(a.YCSL*a.YPDJ) as YPJE,sum(a.YPDJ*a.YCJL) as DJHJ,sum(a.YCJL) as YPZL,a.YPYF as YPZS,avg(a.CFTS) as CFTS,avg(a.MRCS) as MRCS from ZY_BQYZ a where a.ZYH=:ZYH and a.YZZH = :YZZH group by a.YPYF";
			response.put("BRCH", brxx.get("BRCH"));
			response.put("BRXM",brxx.get("BRXM"));
			String brxb = "";
			if (brxx.get("BRXB") != null && brxx.get("BRXB") != "") {
				brxb = DictionaryController.instance().getDic("phis.dictionary.gender").getText(brxx.get("BRXB") + "");
			}
			response.put("BRXB",brxb);
			response.put("AGE", brxx.get("RYNL"));
			response.put("CFTS", formData.get("CFTS") + "");
			response.put("PC", formData.get("SYPC") + "");
			response.put("ZDMC", brxx.get("MQZD"));
			response.put("YSDM", user.getUserName());
			String xzmc = "";
			if (brxx.get("BRXZ") != null && brxx.get("BRXZ") != "") {
				xzmc = DictionaryController.instance().getDic("phis.dictionary.patientProperties").getText(brxx.get("BRXZ") + "");
			}
			response.put("BRXZ", xzmc);
			response.put("MZHM", brxx.get("MZHM"));
			response.put("DZ", brxx.get("LXDZ"));
			response.put("DH", brxx.get("LXDH"));
			String ksmc = "";
			if (brxx.get("BRKS") != null && brxx.get("BRKS") != "") {
				ksmc = DictionaryController.instance().getDic("phis.dictionary.department_zy").getText(brxx.get("BRKS") + "");
			}
			response.put("KB", ksmc);
			Map<String,Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", ZYH);
			parameters.put("YZZH", YZZH);
			Map<String,Object> BQYZ = dao.doLoad(sql1, parameters);
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			response.put("CYLX", BQYZ.get("CYLX"));
			response.put("YPZL", BQYZ.get("YPZL"));
			response.put("DJHJ", BQYZ.get("DJHJ"));
			response.put("YPJE", BQYZ.get("YPJE"));
			response.put("CFTS", BQYZ.get("CFTS"));
			response.put("MRCS", BQYZ.get("MRCS"));
			response.put("KFRQ", sf.format(BQYZ.get("KFRQ")));
			response.put("JZHM", brxx.get("BAHM"));
			response.put("CFBH", brxx.get("BRCH"));
			if (BQYZ!=null) {//add by lizhi 2017-11-21给药途径
				String ypzs = DictionaryController.instance().getDic("phis.dictionary.drugWay").getText(BQYZ.get("YPZS")+"");
				response.put("YPZS", "".equals(ypzs)?"":"药品用法:" + ypzs);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
