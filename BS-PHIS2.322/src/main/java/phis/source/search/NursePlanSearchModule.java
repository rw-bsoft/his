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

public class NursePlanSearchModule extends AbstractSearchModule {
	protected Logger logger = LoggerFactory
			.getLogger(NursePlanSearchModule.class);

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
		.append("select JLBH as JLBH,to_char(KSRQ,'YYYY-MM-DD HH24:MI:SS') as KSRQ," +
				" HLZD as HLZD,HLMB as HLMB,HLCS as HLCS,HLPJ as HLPJ," +
				" to_char(TZRQ,'YYYY-MM-DD HH24:MI:SS') as TZRQ,ZYH as ZYH" +
				" from EMR_HLJH where ZYH="+zyh+" order by KSRQ");
		SQLQuery query = session.createSQLQuery(sqlBuilder.toString());
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> result = (List<Map<String, Object>>) query
				.list();
		session.close();
		return result;
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
				times = String.valueOf(item.get("KSRQ"));
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
					item.put("text", getHourMinByDate((String)m.get("KSRQ")));
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
	
	/**
	 * 获取指定日期的年和月中间用":"间隔
	 * 
	 * @throws ParseException
	 */
	public String getHourMinByDate(String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(sdf.parse(time));
		SimpleDateFormat strTime = new SimpleDateFormat("HH:mm");
		return strTime.format(calendar.getTime());
	}
}
