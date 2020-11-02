package phis.source.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.util.context.Context;

public class FsbNurseRecordSearchModule extends AbstractSearchModule {
	protected Logger logger = LoggerFactory
			.getLogger(FsbNurseRecordSearchModule.class);

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
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryNurseRecordTree(String zyh,
			Context ctx) {
		Session session = (Session) ctx.get(Context.DB_SESSION);
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("select JLBH as JLBH,to_char(HLRQ,'YYYY-MM-DD') as HLRQ,"
						+ " ZYH as ZYH"
						+ " from JC_HLJL where ZYH="
						+ zyh
						+ " order by HLRQ");
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
		Map<String, Object> item = null;
		if (treeItems != null && treeItems.size() > 0) {
			for (int i = 0; i < tmpTreeList.size(); i++) {
				Map<String, Object> chiMap = new HashMap<String, Object>();
				item = tmpTreeList.get(i);
				String hlrq = String.valueOf(item.get("HLRQ"));
				chiMap.put("id", item.get("JLBH"));
				chiMap.put("text", hlrq);
				chiMap.put("leaf", "true");
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
