/**
 * @(#)ChargeSummaryFile.java Created on 2013-8-5 下午4:15:41
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import ctd.account.UserRoleToken;
import ctd.print.ColumnModel;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class ChargeSummaryMZFile implements Service {
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		String beginDate = (String) req.get("beginDate");
		String endDate = (String) req.get("endDate");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		String TITLE =user.getManageUnitName();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			String sql = "SELECT a.BRXZ as BRXZ,c.XZMC as XZMC,to_char(a.JZRQ,'yyyy-MM-dd HH24:mm:ss') as JZRQ,"
					+ "b.SFXM as FYXM, d.SFMC as SFMC,sum(a.ZJJE) as ZJJE "
					+ "FROM MS_MZXX a,MS_SFMX b,GY_BRXZ c,GY_SFXM d  "
					+ "WHERE (a.JGID=b.JGID) "
					+ "AND (a.MZXH=b.MZXH) "
					+ "AND (a.FPHM=b.FPHM)  "
					+ "AND (a.BRXZ=c.BRXZ) "
					+ "AND (b.SFXM=d.SFXM) "
					+ "AND ( c.DBPB<>'0' )  "
					+ "AND  ( a.JGID = :manaUnitId )  "
					+ "AND ( to_char(a.JZRQ,'yyyy-MM-dd HH24:mm:ss')>=:beginDate ) "
					+ "AND (to_char(a.JZRQ ,'yyyy-MM-dd HH24:mm:ss') <=:endDate ) "
					+ "GROUP BY ( a.BRXZ,c.XZMC,b.SFXM, d.SFMC,a.JZRQ) ";
			String sql_zf = "SELECT  a.BRXZ as BRXZ,c.XZMC as XZMC,to_char(a.JZRQ,'yyyy-MM-dd HH24:mm:ss') as JZRQ,"
					+ "b.SFXM as FYXM, d.SFMC as SFMC,sum(a.ZJJE) as ZJJE "
					+ "FROM MS_MZXX a,MS_SFMX b,GY_BRXZ c,GY_SFXM d ,MS_ZFFP e "
					+ "WHERE (a.JGID=b.JGID) "
					+ "AND (a.MZXH=b.MZXH) "
					+ "AND (a.FPHM=b.FPHM)  "
					+ "AND (a.BRXZ=c.BRXZ) "
					+ "AND (b.SFXM=d.SFXM) "
					+ "AND (a.JGID=e.JGID) "
					+ "AND (a.MZXH=e.MZXH)  "
					+ "AND (a.FPHM=e.FPHM) "
					+ "AND ( c.DBPB<>'0' )  "
					+ "AND  ( a.JGID = :manaUnitId )  "
					+ "AND ( to_char(a.JZRQ,'yyyy-MM-dd HH24:mm:ss')>=:beginDate ) "
					+ "AND (to_char(a.JZRQ ,'yyyy-MM-dd HH24:mm:ss') <=:endDate ) "
					+ "GROUP BY ( a.BRXZ,c.XZMC,b.SFXM, d.SFMC,a.JZRQ) ";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Query query = ss.createSQLQuery(sql);
			Query query_zf = ss.createSQLQuery(sql_zf);
			query.setParameter("manaUnitId", JGID);
			query.setParameter("beginDate", beginDate);
			query.setParameter("endDate", endDate);
			query_zf.setParameter("manaUnitId", JGID);
			query_zf.setParameter("beginDate", beginDate);
			query_zf.setParameter("endDate", endDate);
			List<Map<String, Object>> list_data = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> list_allData = query
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			List<Map<String, Object>> list_zfData = query_zf
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			Date minDate = sdf.parse(beginDate);
			Date maxDate = sdf.parse(endDate);
			List<String> w = new ArrayList<String>();
			List<Map<String, Object>> list_wData = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> list_cont = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> list_clone = list_allData;
			for (int i = 0; i < list_allData.size(); i++) {
				Map<String, Object> m = list_allData.get(i);
				String BRXZ = m.get("BRXZ").toString();
				String FYXM = m.get("FYXM").toString();
				if (w.contains(BRXZ+"="+FYXM)) {
					continue;
				}
				w.add(BRXZ+"="+FYXM);
				for (int j = 0; j < list_clone.size(); j++) {
					if(i==j){
						continue;
					}
					Map<String, Object> m1 = list_clone.get(j);
					String BRXZ1 = m1.get("BRXZ").toString();
					String FYXM1 = m1.get("FYXM").toString();
					if (BRXZ.equals(BRXZ1) && FYXM.equals(FYXM1)) {
						double ZJJE = Double.parseDouble(m.get("ZJJE")
								.toString());
						double ZJJE1 = Double.parseDouble(m1.get("ZJJE")
								.toString());
						m.put("ZJJE", (ZJJE + ZJJE1));
					}
				}
				list_cont.add(m);
			}
			for (int i = 0; i < list_cont.size(); i++) {
				Map<String, Object> m = list_cont.get(i);
				Date jzrq = sdf.parse(m.get("JZRQ").toString());
				if (i == 0) {
					minDate = jzrq;
					maxDate = jzrq;
				} else {
					if (jzrq.getTime() < minDate.getTime()) {
						minDate = jzrq;
					}
					if (jzrq.getTime() > maxDate.getTime()) {
						maxDate = jzrq;
					}
				}
				for (int j = 0; j < list_zfData.size(); j++) {
					Map<String, Object> m1 = list_zfData.get(j);
					String BRXZ = m1.get("BRXZ").toString();
					String FYXM = m1.get("FYXM").toString();
					if (m.get("BRXZ").toString().equals(BRXZ)
							&& m.get("FYXM").toString().equals(FYXM)) {
						double ZJJE = Double.parseDouble(m.get("ZJJE")
								.toString());
						double ZJJE1 = Double.parseDouble(m1.get("ZJJE")
								.toString());

						m.put("ZJJE", (ZJJE - ZJJE1));
					}
				}
				list_data.add(m);
			}

			Map<String, Map<String, Object>> colData = new HashMap<String, Map<String, Object>>();
			HashMap<String, Object> footHj = new HashMap<String, Object>();
			double totleCount = 0;
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> m = list_data.get(i);
				String FYXM = m.get("FYXM").toString();
				double ZJJE = Double.parseDouble(m.get("ZJJE").toString());
				totleCount += ZJJE;
				if (footHj.containsKey("ZJJE" + FYXM)) {
					double je = Double.parseDouble((String) footHj.get("ZJJE"
							+ FYXM));
					footHj.put("ZJJE" + FYXM,
							String.format("%1$.2f", ZJJE + je));
				} else {
					footHj.put("ZJJE" + FYXM, String.format("%1$.2f", ZJJE));
				}
				if (colData.get(FYXM) != null) {
					continue;
				}
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("name", "ZJJE" + FYXM);
				data.put("text", m.get("SFMC"));
				colData.put(FYXM, data);
			}
			footHj.put("XZMC", "合计");
			footHj.put("FYHJ", String.format("%1$.2f", totleCount));
			List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> m = list_data.get(i);
				String BRXZ = m.get("BRXZ").toString();
				String FYXM = m.get("FYXM").toString();
				HashMap<String, Object> data = new HashMap<String, Object>();
				double je = 0;
				for (int j = 0; j < listData.size(); j++) {
					Map<String, Object> m1 = listData.get(j);
					if (m1.get("BRXZ").toString().equals(BRXZ)) {
						data.putAll(m1);
						je = Double.parseDouble((String) data.get("FYHJ"));
						listData.remove(m1);
					}
				}
				double ZJJE = Double.parseDouble(m.get("ZJJE").toString());
				data.put("ZJJE" + FYXM, String.format("%1$.2f", m.get("ZJJE")));
				data.put("FYHJ", String.format("%1$.2f", ZJJE + je));
				data.put("BRXZ", m.get("BRXZ"));
				data.put("XZMC", m.get("XZMC"));
				listData.add(data);
			}
			listData.add(footHj);
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			ColumnModel cm = new ColumnModel();
			cm.setName("XZMC");
			cm.setText("单位名称");
			cm.setWdith(100);
			map.put("XZMC", cm);
			for (int i = 0; i < colData.values().size(); i++) {
				Map<String, Object> m = (Map<String, Object>) colData.values()
						.toArray()[i];
				ColumnModel cm0 = new ColumnModel();
				cm0.setName((String) m.get("name"));
				cm0.setText((String) m.get("text"));
				cm0.setWdith(100);
				map.put(i + "", cm0);
			}
			cm = new ColumnModel();
			cm.setName("FYHJ");
			cm.setText("合计");
			cm.setWdith(100);
			map.put("FYHJ", cm);
			ColumnModel[] columnModel = map.values().toArray(
					new ColumnModel[map.size()]);

			Map<String, Object> pageHeaderData = getPageHeaderData(
					sdf.format(minDate), sdf.format(maxDate));

			String temp = "";
			for (int i = 0; i < columnModel.length; i++) {
				String cname = columnModel[i].getName();
				if ("XZMC".equals(cname))
					continue;
				for (int j = 0; j < listData.size(); j++) {
					temp = listData.get(j).get(cname) + "";
					if ("null".equals(temp)) {
						listData.get(j).put(cname, "0.00");
					}
				}
			}

			res.put("title", TITLE + "医药费记账汇总");
			res.put("pageHeaderData", pageHeaderData);
			res.put("columnModel", columnModel);
			res.put("listData", listData);
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			ss.close();
		}
	}

	private Map<String, Object> getPageHeaderData(String beginDate,
			String endDate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "统计时间： " + beginDate + "  至  " + endDate
				+ "        类别：门诊");
		return map;
	}

}
