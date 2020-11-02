package chis.source.phr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.Constants;

import com.alibaba.fastjson.JSONException;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.security.Condition;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

public class SimpleGrid implements Service, BSCHISEntryNames {

	private static final Logger logger = LoggerFactory
			.getLogger(SimpleGrid.class);

	@SuppressWarnings("rawtypes")
	public void execute(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, Context ctx) {
		String parentKey = (String) jsonReq.get("parentKey");
		Map<String, String> pams = new HashMap<String, String>();
		pams.put("jkdas_regionCode", parentKey);
		pams.put("jtdas_regionCode", parentKey);
		pams.put("gxyrs_regionCode", parentKey);
		pams.put("tnbrs_regionCode", parentKey);
		pams.put("jkdas_status", Constants.CODE_STATUS_NORMAL);
		pams.put("jtdas_status", Constants.CODE_STATUS_NORMAL);
		pams.put("gxyrs_status", Constants.CODE_STATUS_WRITE_OFF);
		pams.put("tnbrs_status", Constants.CODE_STATUS_WRITE_OFF);

		List items = (List) jsonReq.get("items");
		try {
			getRecordCount(parentKey, makeQueryHqls(ctx), pams, items, ctx);
		} catch (ServiceException e) {
			logger.error("", e);
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			jsonRes.put(RES_MESSAGE, "档案数统计失败。");
			return;
		}

		jsonRes.put("body", items);
		jsonRes.put(RES_CODE, 200);
		jsonRes.put(RES_MESSAGE, "Success");
	}

	/**
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 */
	private Map<String, String> makeQueryHqls(Context ctx)
			throws ServiceException {
		String jkdas = new StringBuffer(EHR_HealthRecord)
				.append(" a where a.regionCode like :regionCode || '%' and a.status=:status ")
				.append(getWhere(EHR_HealthRecord, ctx)).toString();
		String jtdas = new StringBuffer(EHR_FamilyRecord)
				.append(" a where a.regionCode like :regionCode || '%' and a.status=:status ")
				.append(getWhere(EHR_FamilyRecord, ctx)).toString();
		String gxyrs = new StringBuffer(EHR_HealthRecord).append(" a, ")
				.append(MDC_HypertensionRecord)
				.append(" b where a.regionCode like :regionCode || '%'")
				.append(" and a.phrId=b.phrId and b.status<>:status")
				.append(getWhere(MDC_HypertensionRecord, ctx)).toString();
		String tnbrs = new StringBuffer(EHR_HealthRecord).append(" a, ")
				.append(MDC_DiabetesRecord)
				.append(" b where a.regionCode like :regionCode || '%' ")
				.append("and a.phrId=b.phrId and b.status<>:status")
				.append(getWhere(MDC_DiabetesRecord, ctx)).toString();

		Map<String, String> map = new HashMap<String, String>();
		map.put("jkdas", jkdas);
		map.put("jtdas", jtdas);
		map.put("gxyrs", gxyrs);
		map.put("tnbrs", tnbrs);

		String part1 = "select a.regionCode, count(*) from ";
		String part2 = " group by a.regionCode";
		for (String key : map.keySet()) {
			map.put(key, part1 + map.get(key) + part2);
		}
		return map;
	}

	/**
	 * @param hqls
	 * @param pams
	 * @param items
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getRecordCount(String parentKey, Map<String, String> hqls,
			Map<String, String> pams, List items, Context ctx)
			throws ServiceException {
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
		Session session = sf.openSession();
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			for (String key : hqls.keySet()) {
				String hql = hqls.get(key);
				List<Object[]> ls;
				Query q = session.createQuery(hql);
				for (String pam : pams.keySet()) {
					if (pam.startsWith(key)) {
						q.setParameter(pam.substring(key.length() + 1),
								pams.get(pam));
					}
				}
				ls = q.list();
				for (int i = 0; i < items.size(); i++) {
					Map<String, Object> item = (Map<String, Object>) items
							.get(i);
					String code1 = (String) item.get("key");
					int count = 0;
					for (Iterator<Object[]> it = ls.iterator(); it.hasNext();) {
						Object[] o = it.next();
						if (o[0] != null) {
							String code2 = o[0].toString().length() <= code1
									.length() ? o[0].toString() : o[0]
									.toString().substring(0, code1.length());
							if (code1.equals(code2)) {
								count += (Long) o[1];
								it.remove();
							}
						}
					}
					item.put(key, count);
				}
				if (items.size() == 0) {
					res.put(key, ls.size() > 0 ? (Long) (ls.get(0))[1] : 0);
				}
			}
		} catch (HibernateException e) {
			throw new ServiceException(e);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		if (items.size() == 0) {
			Map<String, Object> json = (Map<String, Object>) ((HashMap<String, Object>) res)
					.clone();
			json.put("key", parentKey);
			DictionaryItem di = null;
			try {
				di = DictionaryController.instance().get("chis.dictionary.areaGrid")
						.getItem(parentKey);
			} catch (ControllerException e) {
				throw new ServiceException(e);
			}
			json.put("text", di.getText());
			json.put("isBottom", di.getProperty("isBottom"));
			items.add(json);
		}
	}

	/**
	 * @param schemaId
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("rawtypes")
	private String getWhere(String schemaId, Context ctx)
			throws ServiceException {
		StringBuilder where = new StringBuilder(" and ");
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e1) {
			logger.error("failed to get schema["+schemaId+"] ");
			throw new ServiceException("获取表单失败！", e1);
		}
		if (sc.lookupPremission().getMode().isAccessible() == false) {
			where.append("1=2");
		} else {
			Condition c = sc.lookupCondition("query");
			if (c != null) {
				List exp = (List)c.getDefine();
				if (exp != null) {
					try {
						where.append(ExpressionProcessor.instance().toString(
								exp));
					} catch (ExpException e) {
						e.printStackTrace();
						logger.error("error cnd ", exp);
						throw new ServiceException("表达式错误.", e);
					}
				}
			}
		}
		if (where.length() == 5) {
			return "";
		}
		return where.toString();
	}
}