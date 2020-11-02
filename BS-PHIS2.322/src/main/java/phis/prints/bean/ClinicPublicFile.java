/**
 * @(#)ClinicPublicFile.java Created on 2013-8-21 下午2:55:42
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.prints.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.print.ColumnModel;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class ClinicPublicFile implements Service {

	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> config = (Map<String, Object>) req
				.get("requestData");
		String TITLE = String.valueOf(req.get("title"));
		if(TITLE == null || TITLE.isEmpty()){
			TITLE = "门诊公费明细表";
		}
		String beginDate = (String) config.get("dateFrom");
		String endDate = (String) config.get("dateTo");
		List<String> brxzValue_list = (List<String>) config.get("brxzValue");
		String brxzValue = "(";
		for (int i = 0; i < brxzValue_list.size(); i++) {
			brxzValue += brxzValue_list.get(i) + ",";
		}
		brxzValue = brxzValue.substring(0, brxzValue.length() - 1) + ")";
		String CFHM = (String) config.get("CFHM");
		String BRXM = (String) config.get("BRXM");
		String YSXM = (String) config.get("YSXM");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		// ,d.ghje as ghje
		String sql = "select a.BRID as BRID,a.fphm as cfhm,a.brxm as brxm,case a.brxb when 1 then '男' when 2 then '女' end as brxb,a.fyzh as fyzh,"
				+ "e.xzmc as brxz,b.zjje as zjje,a.xjje as xjje,a.zhje as zhje,"
				+ "a.czgh as czgh,to_char(a.sfrq,'yyyy-MM-dd HH24:mm:ss') as sfrq,b.sfxm as sfxm,g.sfmc as sfmc "
				+ "from ms_mzxx a,  ms_sfmx b ,MS_GHMX d,GY_BRXZ e,GY_SFXM g  "
				+ "where a.mzxh =b.mzxh(+) "
				+ "and a.ghgl=d.sbxh(+) "
				+ "and a.brxz=e.brxz(+) "
				+ "and b.sfxm=g.sfxm(+) "
				+ "AND  ( a.brxz in "
				+ brxzValue
				+ " )  "
				+ "AND  ( a.JGID = :JGID )  "
				+ "AND ( to_char(a.sfrq,'yyyy-MM-dd HH24:mm:ss')>=:beginDate ) "
				+ "AND (to_char(a.sfrq ,'yyyy-MM-dd HH24:mm:ss') <=:endDate ) ";
//		String sql_ylzh = "select a.BRID as BRID,c.GRBH as GRBH from ms_mzxx a,YB_JSXX b,YB_YBKXX c "
//				+ "where a.ywlsh=b.ywlsh "
//				+ "and b.sbkh=c.sbkh "
//				+ "AND  ( a.JGID = :JGID )  "
//				+ "AND ( to_char(a.sfrq,'yyyy-MM-dd HH24:mm:ss')>=:beginDate ) "
//				+ "AND (to_char(a.sfrq ,'yyyy-MM-dd HH24:mm:ss') <=:endDate ) ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", JGID);
		parameters.put("beginDate", beginDate);
		parameters.put("endDate", endDate);
		if (CFHM != null && !"".equals(CFHM)) {
			sql += "and a.fphm like '" + CFHM + "%'";
		}
		if (BRXM != null && !"".equals(BRXM)) {
			sql += "and a.brxm like '" + BRXM + "%'";
		}
		if (YSXM != null && !"".equals(YSXM)) {
			String sql_ys = "select a.userid as userid,a.userName as userName from sys_users a where a.userName like '"
					+ YSXM + "%'";
			List<Map<String, Object>> users = null;
			Map<String, Object> m = new HashMap<String, Object>();
			try {
				users = dao.doSqlQuery(sql_ys, m);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			if (users != null && users.size() > 0) {
				String USERS = "(";
				for (int i = 0; i < users.size(); i++) {
					USERS += (String) users.get(i).get("USERID") + ",";
				}
				USERS = USERS.substring(0, USERS.length() - 1) + ")";
				sql += "and a.czgh in " + USERS;
			}
		}
		sql += " order by a.fphm";
		List<Map<String, Object>> list_allData = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> list_yb = new ArrayList<Map<String,Object>>();
		try {
			list_allData = dao.doSqlQuery(sql, parameters);
			//list_yb = dao.doSqlQuery(sql_ylzh, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ServiceException(2001, "执行查询失败!", e);
		}
		List<HashMap<String, Object>> list_data = new ArrayList<HashMap<String, Object>>();
		List<Object> cfhm_list = new ArrayList<Object>();
		Dictionary doctor;
		try {
			doctor = DictionaryController.instance().get("phis.dictionary.doctor");
		} catch (ControllerException e) {
			e.printStackTrace();
			throw new ServiceException(2001, "解析doctor字典失败!", e);
		}
		List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < list_allData.size(); i++) {
			HashMap<String, Object> m = (HashMap<String, Object>) list_allData
					.get(i);
			for (int j = 0; j < list_yb.size(); j++) {
				Map<String, Object> m1 = list_yb.get(j);
				if(m.get("BRID").toString().equals(m1.get("BRID").toString())){
					m.put("GRBH", m1.get("GRBH"));
					break;
				}
			}
			if (m.get("SFRQ") != null) {
				m.put("SFSJ", m.get("SFRQ").toString().substring(0, 10));
			}
			if (m.get("CZGH") != null) {
				m.put("CZGH", doctor.getText(m.get("CZGH").toString()));
			}
			// if (m.get("YSDM") != null) {
			// m.put("YSDM", doctor.getText(m.get("YSDM").toString()));
			// }
			if (m.get("CFHM") != null && cfhm_list.contains(m.get("CFHM"))) {
				continue;
			}
			cfhm_list.add(m.get("CFHM"));
			list_data.add(m);
		}
		for (int i = 0; i < list_data.size(); i++) {
			HashMap<String, Object> m = list_data.get(i);
			for (int j = 0; j < list_allData.size(); j++) {
				Map<String, Object> m1 = list_allData.get(j);
				if (m.get("CFHM") != null
						&& m1.get("CFHM") != null
						&& (m.get("CFHM").toString()).equals(m1.get("CFHM")
								.toString())) {
					if (m.containsKey("SFXM" + m1.get("SFXM"))) {
						m.put("SFXM" + m1.get("SFXM"),
								String.format(
										"%1$.2f",
										praseDouble(m.get("SFXM"
												+ m1.get("SFXM")))
												+ praseDouble(m1.get("ZJJE"))));
					} else {
						m.put("SFXM" + m1.get("SFXM"),
								String.format("%1$.2f",
										praseDouble(m1.get("ZJJE"))));
					}
					if (!m.get("CFHM").equals(m1.get("CFHM"))
							|| !m.get("SFXM").equals(m1.get("SFXM"))) {
						m.put("ZJJE",
								String.format("%1$.2f",
										praseDouble(m.get("ZJJE"))
												+ praseDouble(m1.get("ZJJE"))));
					}
				}
			}
			// m.put("GHJE", String.format("%1$.2f",
			// praseDouble(m.get("GHJE"))));
			m.put("XJJE", String.format("%1$.2f", praseDouble(m.get("XJJE"))));
			m.put("ZHJE", String.format("%1$.2f", praseDouble(m.get("ZHJE"))));
			if (m.get("CFHM") == null) {
				m.put("SFXM" + m.get("SFXM"),
						String.format("%1$.2f", praseDouble(m.get("ZJJE"))));
			}
			listData.add(m);
		}
		Map<String, Map<String, Object>> colData = new HashMap<String, Map<String, Object>>();
		for (int i = 0; i < list_allData.size(); i++) {
			Map<String, Object> m = list_allData.get(i);
			if (m.get("SFXM") != null) {
				String SFXM = m.get("SFXM").toString();
				if (colData.get("SFXM" + SFXM) != null) {
					continue;
				}
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("name", "SFXM" + SFXM);
				data.put("text", m.get("SFMC"));
				colData.put("SFXM" + SFXM, data);
			}
		}

		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		ColumnModel cm = new ColumnModel();
		cm.setName("CFHM");
		cm.setText("处方号");
		cm.setWdith(100);
		map.put("CFHM", cm);
		cm = new ColumnModel();
		cm.setName("BRXM");
		cm.setText("姓名");
		cm.setWdith(100);
		map.put("BRXM", cm);
		cm = new ColumnModel();
		cm.setName("BRXB");
		cm.setText("性别");
		cm.setWdith(80);
		map.put("BRXB", cm);
		cm = new ColumnModel();
		cm.setName("GRBH");
		cm.setText("医疗证号");
		cm.setWdith(100);
		map.put("GRBH", cm);
		cm = new ColumnModel();
		cm.setName("BRXZ");
		cm.setText("病人性质");
		cm.setWdith(100);
		map.put("BRXZ", cm);
		cm = new ColumnModel();
		cm.setName("ZJJE");
		cm.setText("总费用");
		cm.setWdith(100);
		map.put("ZJJE", cm);
		// cm = new ColumnModel();
		// cm.setName("GHJE");
		// cm.setText("挂号费");
		// cm.setWdith(100);
		// map.put("GHJE", cm);
		cm = new ColumnModel();
		cm.setName("XJJE");
		cm.setText("现金支付");
		cm.setWdith(100);
		map.put("XJJE", cm);
		cm = new ColumnModel();
		cm.setName("ZHJE");
		cm.setText("账户支付");
		cm.setWdith(100);
		map.put("ZHJE", cm);
		cm = new ColumnModel();
		cm.setName("CZGH");
		cm.setText("收费员");
		cm.setWdith(100);
		map.put("CZGH", cm);
		// cm = new ColumnModel();
		// cm.setName("YSDM");
		// cm.setText("医生");
		// cm.setWdith(100);
		// map.put("YSDM", cm);
		cm = new ColumnModel();
		cm.setName("SFRQ");
		cm.setText("收费时间");
		cm.setWdith(180);
		map.put("SFRQ", cm);
		cm = new ColumnModel();
		cm.setName("SFSJ");
		cm.setText("收费日期");
		cm.setWdith(100);
		map.put("SFSJ", cm);
		for (int i = 0; i < colData.values().size(); i++) {
			Map<String, Object> m = (Map<String, Object>) colData.values()
					.toArray()[i];
			ColumnModel cm0 = new ColumnModel();
			cm0.setName((String) m.get("name"));
			cm0.setText((String) m.get("text"));
			cm0.setWdith(100);
			map.put(i + "", cm0);
		}
		ColumnModel[] columnModel = map.values().toArray(
				new ColumnModel[map.size()]);

		Map<String, Object> pageHeaderData = getPageHeaderData(beginDate,
				endDate);
		// title增加机构名称
		String jgname = user.getManageUnitName();
		if (jgname != null)
			TITLE = jgname + TITLE;
		res.put("title", TITLE);
		res.put("pageHeaderData", pageHeaderData);
		res.put("columnModel", columnModel);
		res.put("listData", listData);
	}

	private double praseDouble(Object object) {
		if (object == null) {
			return 0.00;
		}
		return Double.parseDouble(object.toString());
	}

	private Map<String, Object> getPageHeaderData(String beginDate,
			String endDate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "统计时间： " + beginDate + "  至  " + endDate);
		return map;
	}
	 /**
	  * 字符串编码转换的实现方法
	  * @param str  待转换编码的字符串
	  * @param newCharset 目标编码
	  * @return
	  * @throws UnsupportedEncodingException
	  */
	 public String changeCharset(String str, String newCharset)
	   throws UnsupportedEncodingException {
		  if (str != null) {
			   //用默认字符编码解码字符串。
			   byte[] bs = str.getBytes();
			   //用新的字符编码生成字符串
			   return new String(bs, newCharset);
		  }
		  return null;
	 }
}
