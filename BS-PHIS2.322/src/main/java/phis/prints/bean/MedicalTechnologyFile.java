/**
 * @(#)MedicalTechnologyFile.java Created on 2013-8-22 下午2:35:59
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.prints.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
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
public class MedicalTechnologyFile implements Service {

	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> config = (Map<String, Object>) req
				.get("requestData");
		String TITLE = (String) req.get("title");
		String beginDate = (String) config.get("dateFrom");
		String endDate = (String) config.get("dateTo");
		String KDYS = config.get("KDYS").toString();

		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String sql_mz = "select b.ksdm as ksdm,e.OFFICENAME as ksmc,"
				+ "b.mzxh as brbm,b.brxm as brxm,"
				+ "c.fygb as fygb,d.sfmc as sfmc,c.hjje as hjje "
				+ "from MS_MZXX a,MS_YJ01 b, MS_YJ02 c,GY_SFXM d,SYS_Office e "
				+ "where a.mzxh = b.mzxh and b.yjxh=c.yjxh and c.fygb=d.sfxm "
				+ "and b.ksdm=e.ID  AND  ( b.JGID = :JGID ) "
				+ "AND ( to_char(a.SFRQ,'yyyy-MM-dd HH24:mm:ss')>=:beginDate ) "
				+ "AND (to_char(a.SFRQ ,'yyyy-MM-dd HH24:mm:ss') <=:endDate ) "
				+ "and b.YSDM=:YSDM and a.zfpb<>1";
		String sql_zy = "select a.ksdm as ksdm,d.OFFICENAME as ksmc,a.zyh as brbm,"
				+ "a.brxm as brxm,b.fygb as fygb,c.sfmc as sfmc,"
				+ "b.yldj*b.ylsl as hjje "
				+ "from YJ_ZY01 a, YJ_ZY02 b ,GY_SFXM c ,SYS_Office d  "
				+ "where a.yjxh=b.yjxh " + "and b.fygb=c.sfxm "
				+ "and a.ksdm=d.ID " + "AND  ( a.JGID = :JGID ) "
				+ "AND (to_char(a.ZXRQ,'yyyy-MM-dd HH24:mm:ss')>=:beginDate ) "
				+ "AND (to_char(a.ZXRQ ,'yyyy-MM-dd HH24:mm:ss') <=:endDate ) "
				+ "and a.YSDM=:YSDM ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", JGID);
		parameters.put("beginDate", beginDate);
		parameters.put("endDate", endDate);
		parameters.put("YSDM", KDYS);
		sql_mz += " order by b.ksdm,a.mzxh";
		sql_zy += " order by a.ksdm,a.zyh";
		List<Map<String, Object>> list_MZ = null;
		List<Map<String, Object>> list_ZY = null;
		try {
			list_MZ = dao.doSqlQuery(sql_mz, parameters);
			list_ZY = dao.doSqlQuery(sql_zy, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> list_Alldata = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_data = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_other = new ArrayList<Map<String, Object>>();
		list_Alldata.addAll(list_MZ);
		list_Alldata.addAll(list_ZY);
		List<Object> cfhm_list = new ArrayList<Object>();
		List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < list_Alldata.size(); i++) {
			Map<String, Object> m = list_Alldata.get(i);
			if (m.get("BRBM") != null && cfhm_list.contains(m.get("BRBM"))) {
				list_other.add(m);
				continue;
			}
			m.put("SFXM" + m.get("FYGB"),
					String.format("%1$.2f", praseDouble(m.get("HJJE"))));
			cfhm_list.add(m.get("BRBM"));
			list_data.add(m);
		}

		for (int i = 0; i < list_data.size(); i++) {
			HashMap<String, Object> m = (HashMap<String, Object>) list_data
					.get(i);
			double totalCount = praseDouble(m.get("HJJE"));
			for (int j = 0; j < list_other.size(); j++) {
				Map<String, Object> m1 = list_other.get(j);
				m.put("BRBM",m.get("BRBM")+"");
				if (m.get("BRBM") != null
						&& m1.get("BRBM") != null
						&& (m.get("BRBM").toString()).equals(m1.get("BRBM")
								.toString())) {
					if (m.containsKey("SFXM" + m1.get("FYGB"))) {
						m.put("SFXM" + m1.get("FYGB"),
								String.format(
										"%1$.2f",
										praseDouble(m.get("SFXM"
												+ m1.get("FYGB")))
												+ praseDouble(m1.get("HJJE"))));
					} else {
						m.put("SFXM" + m1.get("FYGB"),
								String.format("%1$.2f",
										praseDouble(m1.get("HJJE"))));
					}
					totalCount += praseDouble(m1.get("HJJE"));
				}
			}
			if (m.get("BRBM") == null) {
				totalCount += praseDouble(m.get("HJJE"));
				m.put("SFXM" + m.get("FYGB"),
						String.format("%1$.2f", praseDouble(m.get("HJJE"))));
			}
			m.put("TOTAL", String.format("%1$.2f", totalCount));
			listData.add(m);
		}
		HashMap<String, Object> totalRecord = new HashMap<String, Object>();
		totalRecord.put("KSMC", "合   计");
		double sum = 0;
		Map<String, Map<String, Object>> colData = new HashMap<String, Map<String, Object>>();
		for (int i = 0; i < list_Alldata.size(); i++) {
			Map<String, Object> m = list_Alldata.get(i);
			sum += praseDouble(m.get("HJJE"));
			if (m.get("FYGB") != null) {
				String SFXM = m.get("FYGB").toString();
				if (totalRecord.containsKey("SFXM" + SFXM)) {
					double je = praseDouble(totalRecord.get("SFXM" + SFXM));
					totalRecord.put(
							"SFXM" + SFXM,
							String.format("%1$.2f", praseDouble(m.get("HJJE"))
									+ je));
				} else {
					totalRecord
							.put("SFXM" + SFXM,
									String.format("%1$.2f",
											praseDouble(m.get("HJJE"))));
				}
				if (colData.get("SFXM" + SFXM) != null) {
					continue;
				}
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("name", "SFXM" + SFXM);
				data.put("text", m.get("SFMC"));
				colData.put("SFXM" + SFXM, data);
			}
		}
		totalRecord.put("TOTAL", String.format("%1$.2f", sum));
		listData.add(totalRecord);
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		ColumnModel cm = new ColumnModel();
		cm.setName("KSMC");
		cm.setText("科 室");
		cm.setWdith(100);
		map.put("KSMC", cm);
		cm = new ColumnModel();
		cm.setName("BRBM");
		cm.setText("编码");
		cm.setWdith(100);
		map.put("BRBM", cm);
		cm = new ColumnModel();
		cm.setName("BRXM");
		cm.setText("姓名");
		cm.setWdith(100);
		map.put("BRXM", cm);
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
		cm.setName("TOTAL");
		cm.setText("合计");
		cm.setWdith(100);
		map.put("TOTAL", cm);
		ColumnModel[] columnModel = map.values().toArray(
				new ColumnModel[map.size()]);

		Map<String, Object> pageHeaderData = getPageHeaderData(beginDate,
				endDate);

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
		map.put("text", "统计时间： " + beginDate + "  至  " + endDate
				+ "       单位：元");
		return map;
	}

}
