/**
 * @(#)ChildrenRecordReportFile.java Created on 10:10:01 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.print.instance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import chis.source.print.base.BSCHISPrint;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.SliceTypes;
import ctd.print.IHandler;
import ctd.util.context.Context;

public class DefectChildrenFile extends BSCHISPrint implements IHandler {
	private static Log logger = LogFactory.getLog(DefectChildrenFile.class);

	@SuppressWarnings("unused")
	private String empiId;

	@SuppressWarnings("unused")
	private HibernateTemplate ht;

	// private String phrId;

	public void getParameters(Map<String, Object> map,
			Map<String, Object> parameters, Context ctx) {
		String manageUnit = (String) map.get("manageUnit");
		String unitName = null;
		try {
			unitName = DictionaryController.instance()
					.get("chis.@manageUnit").getText(manageUnit);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		parameters.put("tbdate", new Date());
		parameters.put("unitName", unitName);
		sqlDate2String(parameters);
	}

	public void getFields(Map<String, Object> requestData,
			List<Map<String, Object>> resultList, Context ctx) {
		SessionFactory factory = getSessionFactory(ctx);
		Session ss = factory.openSession();

		int year = Integer.valueOf((String) requestData.get("year"));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, Calendar.SEPTEMBER);
		c.set(Calendar.DAY_OF_MONTH, 30);
		String date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		String manageUnit = (String) requestData.get("manageUnit");
		try {
			Dictionary dic = DictionaryController.instance().get(
					"chis.@manageUnit");
			List<DictionaryItem> l = dic.getSlice(manageUnit,
					SliceTypes.ALL_LEAF, "");
			if (l.size() == 0 && manageUnit.length() >= 11) {
				l.add(dic.getItem(manageUnit));
			}
			SQLQuery q = null;

			for (int i = 0; i < l.size(); i++) {
				Map<String, Object> m = new HashMap<String, Object>();
				DictionaryItem di = l.get(i);
				String manaUnitId = di.getKey();
				String manaUnitName = di.getText();
				m.put("unit", manaUnitName);

				q = ss.createSQLQuery("select * from "
						+ "(select count(*) as j0 "
						+ "from mpi_demographicinfo c ,ehr_healthrecord a "
						+ "where c.birthday > add_months(to_date('"
						+ date
						+ "','yyyy-MM-dd'),-12) "
						+ "and c.birthday <= to_date('"
						+ date
						+ "','yyyy-MM-dd') "
						+ "and a.empiid = c.empiid "
						+ "and a.manaUnitId like '"
						+ manaUnitId
						+ "%') o ,"
						+ "(select count(*) as j1 from mpi_demographicinfo c ,ehr_healthrecord a "
						+ "where c.birthday > add_months(to_date('"
						+ date
						+ "','yyyy-MM-dd'),-24) "
						+ "and c.birthday <= add_months(to_date('"
						+ date
						+ "','yyyy-MM-dd'),-12) "
						+ "and a.empiid = c.empiid and a.manaUnitId like '"
						+ manaUnitId
						+ "%') p, "
						+ "(select count(*) as j2 from mpi_demographicinfo c ,ehr_healthrecord a "
						+ "where c.birthday > add_months(to_date('"
						+ date
						+ "','yyyy-MM-dd'),-48) "
						+ "and c.birthday <= add_months(to_date('"
						+ date
						+ "','yyyy-MM-dd'),-24) "
						+ "and a.empiid = c.empiid and a.manaUnitId like '"
						+ manaUnitId
						+ "%') q ,"
						+ "(select count(*) as y0 "
						+ "from mpi_demographicinfo a, cdh_defectregister b "
						+ "where a.empiid = b.empiId "
						+ "and b.manaunitid like '"
						+ manaUnitId
						+ "%'"
						+ "and a.birthday > add_months(to_date('"
						+ date
						+ "','yyyy-MM-dd'),-12) "
						+ "and a.birthday <= to_date('"
						+ date
						+ "','yyyy-MM-dd')) x ,"
						+ "(select count(*) as y1 "
						+ "from mpi_demographicinfo a, cdh_defectregister b "
						+ "where a.empiid = b.empiId "
						+ "and b.manaunitid like '"
						+ manaUnitId
						+ "%'"
						+ "and a.birthday > add_months(to_date('"
						+ date
						+ "','yyyy-MM-dd'),-24) "
						+ "and a.birthday <= add_months(to_date('"
						+ date
						+ "','yyyy-MM-dd'),-12)) y ,"
						+ "(select count(*) as y2 "
						+ "from mpi_demographicinfo a, cdh_defectregister b "
						+ "where a.empiid = b.empiId "
						+ "and b.manaunitid like '"
						+ manaUnitId
						+ "%'"
						+ "and a.birthday > add_months(to_date('"
						+ date
						+ "','yyyy-MM-dd'),-48) "
						+ "and a.birthday <= add_months(to_date('"
						+ date
						+ "','yyyy-MM-dd'),-24)) z");
				Object[] cnt = (Object[]) q.list().get(0);
				int j0 = Integer.valueOf(cnt[0].toString());
				int j1 = Integer.valueOf(cnt[1].toString());
				int j2 = Integer.valueOf(cnt[2].toString());
				m.put("j0", j0);
				m.put("j1", j1);
				m.put("j2", j2);
				m.put("j3", j0 + j1 + j2);
				int y0 = Integer.valueOf(cnt[3].toString());
				int y1 = Integer.valueOf(cnt[4].toString());
				int y2 = Integer.valueOf(cnt[5].toString());
				m.put("y0", y0);
				m.put("y1", y1);
				m.put("y2", y2);
				m.put("y3", y0 + y1 + y2);
				resultList.add(m);
			}
			sqlDate2String(resultList);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
	}

}
