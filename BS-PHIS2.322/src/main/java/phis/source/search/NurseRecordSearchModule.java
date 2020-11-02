package phis.source.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.BSHISUtil;
import ctd.util.context.Context;

public class NurseRecordSearchModule extends AbstractSearchModule {
	protected Logger logger = LoggerFactory
			.getLogger(NurseRecordSearchModule.class);

	/**
	 * 护理记录左边树展示
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		try {
			String zyh = req.get("zyh") + "";
			// System.out.println(zyh);
			res.put("body", getJsonData(zyh, ctx));

		} catch (Exception e) {
			logger.error("load tree failed by Exception.", e);
		} finally {
		}

	}

	/**
	 * 获取json数据
	 * 
	 * @param zyh
	 * @param ctx
	 * @return [{ id: 1, text: 'A leaf Node', leaf: true },{ id: 2, text: 'A
	 *         folder Node', children: [{ id: 3, text: 'A child Node', leaf:
	 *         true }] }]
	 * @throws ParseException
	 */
	public List<Map<String, Object>> getJsonData(String zyh, Context ctx)
			throws ParseException {
		List<Map<String, Object>> treeItems = queryNurseRecordTree(zyh, ctx);
		List<Map<String, Object>> rs = iteratorInfo(treeItems);
		return rs;
	}

	/**
	 * 查询护理记录树
	 * 
	 * @param zyh
	 *            住院号
	 * @param ctx
	 * @return
	 */
	public List<Map<String, Object>> queryNurseRecordTree(String zyh,
			Context ctx) {
		Session session = (Session) ctx.get(Context.DB_SESSION);
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("SELECT JGBH as JGBH FROM ENR_JG01 WHERE JGMC = '一般护理记录单'");
		SQLQuery query = session.createSQLQuery(sqlBuilder.toString());
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> result = (List<Map<String, Object>>) query
				.list();
		String jgbh = "0";
		if (result.size() > 0) {
			jgbh = String.valueOf(result.get(0).get("JGBH"));
		}
		sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("SELECT ENR_JL01.JLBH as JLBH, ENR_JL01.ZYH as ZYH, ENR_JL01.JGBH as JGBH, ENR_JL01.JLMC as JLMC, ENR_JL01.BLLX as BLLX, ENR_JL01.BLLB as BLLB, ");
		sqlBuilder
				.append(" ENR_JL01.MBLB as MBLB, ENR_JL01.MBBH as MBBH, ENR_JL01.DLLB as DLLB, ENR_JL01.DLJ as DLJ, ENR_JL01.JLHS as JLHS, ENR_JL01.HYBZ as HYBZ,");
		sqlBuilder
				.append(" to_char(ENR_JL01.JLSJ,'YYYY-MM-DD HH24:MI:SS') as JLSJ, ENR_JL01.SXSJ as SXSJ, ENR_JL01.XTSJ as XTSJ, ENR_JL01.SXBQ as SXBQ, ENR_JL01.SXHS as SXHS, ENR_JL01.WCQM as WCQM,");
		sqlBuilder
				.append(" ENR_JL01.WCSJ as WCSJ, ENR_JL01.SYBZ as SYBZ,ENR_JL01.SYSJ as SYSJ, ENR_JL01.SYHS as SYHS, ENR_JL01.SYQM as SYQM, ENR_JL01.DYBZ as DYBZ, ");
		sqlBuilder
				.append(" ENR_JL01.JLZT as JLZT, ENR_JL01.ZJBH as ZJBH, ENR_JL01.ZJLX as ZJLX, ENR_JL01.ZJMC as ZJMC, ENR_JL01.KSSJ as KSSJ, ENR_JL01.JSSJ as JSSJ,");
		sqlBuilder
				.append("  0 as ifnew, 0 as KSYM, 0 as JSYM, 0 as SHHH, ENR_JL01.DLHHBZ as DLHHBZ, ENR_JL01.WCDLLJ as WCDLLJ,  ENR_JL01.SYDLLJ as SYDLLJ,");
		sqlBuilder
				.append(" ENR_JL01.JKFL as JKFL, ENR_JL01.JKPF as JKPF, ENR_JL01.JKZG as JKZG  FROM ENR_JL01 ");
		sqlBuilder.append(" WHERE ( ENR_JL01.ZYH = " + zyh
				+ ") AND ( ENR_JL01.JGBH = " + jgbh
				+ " ) AND ENR_JL01.JLZT <> 9 order by JLSJ desc");

		query = session.createSQLQuery(sqlBuilder.toString());
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> treeItems = (List<Map<String, Object>>) query
				.list();
		session.close();
		return treeItems;
	}

	private List<Map<String, Object>> iteratorInfo(
			List<Map<String, Object>> treeItems) throws ParseException {
		List<Map<String, Object>> tmpTreeList = new ArrayList<Map<String, Object>>();
		tmpTreeList.addAll(treeItems);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null, m = null;
		List<Map<String, Object>> childs = null;
		Map<String, Object> chiMap = null;
		String times = "", tmpTime;
		int x = 0;
		if (treeItems != null && treeItems.size() > 0) {
			Map<String, List<Map<String, Object>>> map = new LinkedHashMap<String, List<Map<String, Object>>>();
			List<Map<String, Object>> list = null;
			for (int i = 0; i < tmpTreeList.size(); i++) {
				item = tmpTreeList.get(i);
				times = String.valueOf(item.get("JLSJ"));
				times = getMonthDayByDate(times);
				if (map.get(times) == null) {
					list = new ArrayList<Map<String, Object>>();
					map.put(times, list);
				}
				map.get(times).add(item);
			}
			for (String key : map.keySet()) {
				list = map.get(key);
				chiMap = new HashMap<String, Object>();
				chiMap.put("id", ++x);
				chiMap.put("text", key);
				childs = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < list.size(); i++) {
					m = list.get(i);
					item = new HashMap<String, Object>();
					item.put("id", ++x);
					item.put("text", String.valueOf(m.get("JLMC")));
					item.put("leaf", "true");
					for (String mkey : m.keySet()) {// 调用转换json对象中不支持date类型，因此需转换
						if (m.get(mkey) instanceof Date) {
							if (m.get(mkey) != null) {
								tmpTime = BSHISUtil.toString(
										(Date) m.get(mkey),
										"yyyy-MM-dd HH:mm:ss");
								m.put(mkey, tmpTime);
							} else {
								m.put(mkey, String.valueOf(m.get(mkey)));
							}
						}
					}
					item.put("attributes", m);
					childs.add(item);
				}
				chiMap.put("children", childs);
				result.add(chiMap);
			}

		}
		return result;
	}

	/**
	 * 获取指定日期的年和月中间用"."间隔
	 * 
	 * @param time
	 *            指定日期 2013-03-12
	 * @return 3.12
	 * @throws ParseException
	 */
	public String getMonthDayByDate(String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(sdf.parse(time));
		return calendar.get(Calendar.MONTH) + 1 + "."
				+ calendar.get(Calendar.DATE);
	}
}
