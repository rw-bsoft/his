/**
 * @(#)SystemUserModel.java Created on 2012-2-1 上午10:53:58
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.admin;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.RolesList;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import ctd.account.user.User;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.net.rpc.Client;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.security.Condition;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class SystemUserModel implements BSCHISEntryNames {
	private static final Logger logger = LoggerFactory
			.getLogger(SystemUserModel.class);
	private BaseDAO dao;

	public SystemUserModel(BaseDAO dao) {
		this.dao = dao;
	}

	public String getManaUnit(String userId, String jobId)
			throws ModelDataOperationException {
		Map<String, Object> user = getUserProp(userId, jobId);
		return (String) user.get("manaUnitId");
	}

	public Map<String, Object> getUserProp(String userId, String jobId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "userId", "s", userId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "jobId", "s", jobId);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		Map<String, Object> prop;
		try {
			prop = dao.doLoad(cnd, SYS_UserProp);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get user message.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取医生信息失败");
		}
		return prop;
	}

	public List<Map<String, Object>> getUserProps(String userId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "userId", "s", userId);
		List<Map<String, Object>> props;
		try {
			props = dao.doQuery(cnd, null, SYS_UserProp);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get user message.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取医生信息失败");
		}
		return props;
	}

	/**
	 * 获取责任医生管理管辖机构
	 * 
	 * @param userId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getManaDoctorUnit(String userId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "userId", "s", userId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "jobId", "s",
				RolesList.ZRYS);
		List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "jobId", "s",
				RolesList.TDZ);
		List<?> cnd4 = CNDHelper.createArrayCnd("or", cnd2, cnd3);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd4);
		Map<String, Object> user;
		try {
			user = dao.doLoad(cnd, SYS_UserProp);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get user message.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取医生信息失败");
		}
		return (String) user.get("manaUnitId");
	}

	public Map<String, Object> getUserByUserId(String userId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "userId", "s", userId);
		Map<String, Object> user;
		try {
			user = dao.doLoad(cnd, SYS_USERS);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get user message.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取医生信息失败");
		}
		return user;
	}

	@SuppressWarnings("rawtypes")
	public boolean checkRoleUse(List roles)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select count(*) as cnt from SYS_UserProp where jobId in (");
		for (int i = 0; i < roles.size(); i++) {
			hql.append("'").append(roles.get(i)).append("',");
		}
		hql.setLength(hql.length() - 1);
		hql.append(")");

		List l = dao.doQuery(hql.toString(), null);
		Map m = (Map) l.get(0);
		Long n = (Long) m.get("cnt");
		if (n > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 通过登录名查询用户信息
	 * 
	 * @param logonName
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Object>> getUserByLogonName(String userId,String... args)
			throws ModelDataOperationException {
		try {
			List<Object> cnd1 = CNDHelper.createInCnds("roleId", args);
			List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "userId", "s",userId);
			List<Object> cnd3 = CNDHelper.createSimpleCnd("eq", "domain", "s", AppContextHolder.getName());
			List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Object> cnds = CNDHelper.createArrayCnd("and", cnd, cnd3);
			List<Map<String,Object>> users = (List<Map<String, Object>>) Client.rpcInvoke(AppContextHolder.getConfigServiceId("userLoader"), "find", cnds);
			if (users != null && users.size() > 0) {
				List manageUnits = new ArrayList();
				Map<String, Object> user = users.get(0);
				List<Map<String,Object>> roles = (List<Map<String, Object>>) user.get("roles");
				if (roles.size() == 1) {
					Map<String,Object> role= roles.get(0);
					String manaUnitId = (String) role.get("manageUnitId");
					Dictionary dic = DictionaryController.instance().get(
							"chis.@manageUnit");
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("key", manaUnitId);
					m.put("text", dic.getText(manaUnitId));
					manageUnits.add(m);
				} else {
					for (Map role : roles) {
						manageUnits.add((String) role.get("manageUnitId"));
					}
				}
				return manageUnits;
			}
		} catch (Exception e) {
			logger.error("failed to get user message.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取医生信息失败");
		}
		return null;
	}
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public Map<String, Object> queryUsers(Map<String, Object> req)
			throws ValidateException, ModelDataOperationException {
		Map<String, Object> reValue = new HashMap<String, Object>();
		try {
			Schema sc = SchemaController.instance().get(SYS_USERSSEARCH);

			User user = (User) dao.getContext().get("user.instance");
//			AuthorizeResult result = user.authorize("storage", sc.getId());
//			Condition c = result.getCondition("filter");
			Condition c = sc.lookupCondition("query");
			String filter = "";
			if (c != null) {
				filter = " and "
						+ ExpressionProcessor.instance().toString((List<Object>)c.getDefine());
			}

			StringBuffer hql = new StringBuffer();
			StringBuffer existsHql = new StringBuffer("select 1 from SYS_UserProp b  where a.userId = b.userId and (a.userId<>'system' or jobId<>'system') ");
			
			if(req != null){
				List cnd = (List) req.get("cnd");
				if (null != cnd) {
					try {
						existsHql.append(" and ").append(
								ExpressionProcessor.instance().toString(cnd));
					} catch (ExpException e) {
						throw new ModelDataOperationException("错误的查询参数.", e);
					}
				}
			}
			hql.append("select userId as userId,userName as userName,gender as gender,status as status,pyCode as pyCode,lastModifyUser as lastModifyUser,lastModifyDate as lastModifyDate,remark as remark from SYS_USERS a where exists ( ").append(existsHql);

			hql.append(")");
			String sql = hql.toString();

			Session session = (Session) dao.getContext()
					.get(Context.DB_SESSION);
			String countHql = "select count(*) from SYS_USERS a where exists (" + existsHql.toString() + " )";
			Query queryCount = session.createQuery(countHql);
			Object count = queryCount.uniqueResult();
			reValue.put("totalCount", count);

			int pageSize = 0;
			int pageNo = 0;
			if (req != null && null != (Integer) req.get("pageSize")) {
				pageNo = (Integer) req.get("pageNo");
				pageSize = (Integer) req.get("pageSize");
			} else {
				pageSize = 25;
				pageNo = 1;
			}
			int first = (pageNo - 1) * pageSize;
			reValue.put("pageSize", pageSize);
			reValue.put("pageNo", pageNo);

			Query query = session.createQuery(sql.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			query.setFirstResult(first);
			query.setMaxResults(pageSize);

			List records = new ArrayList();
			reValue.put("records", records);
			List<?> l = query.list();
			if (l.size() > 0) {
				for (int i = 0; i < l.size(); i++) {
					Map<?, ?> r = (Map<?, ?>) l.get(i);
					Map<String, Object> jsonRec = new HashMap<String, Object>();
					records.add(jsonRec);
					for (int j = 0; j < sc.getItems().size(); j++) {
						String name = sc.getItems().get(j).getId();
						SchemaItem si = sc.getItem(name);
						if (si.getType().equals("text")) {
							Clob clob = (Clob) r.get(name.toUpperCase());
							Reader reader = clob.getCharacterStream();
							BufferedReader br = new BufferedReader(reader);
							StringBuffer sb = new StringBuffer();
							String temp;
							while ((temp = br.readLine()) != null) {
								sb.append(temp);
							}
							jsonRec.put(name, sb.toString());
							br.close();
							continue;
						}
						Object v = r.get(name);
						jsonRec.put(name, v);
						if (si != null && si.isCodedValue()) {
							Object dv = null;
							boolean isCacheDic = false;
							for (int k = 0; k < sc.getItems().size(); k++) {
								String vtext = sc.getItems().get(k)
										.getId();
								if (vtext.equals(name + "_text")) {
									dv = r.get(si.getId());
									isCacheDic = true;
									break;
								}
							}
							if (dv != null) {
								jsonRec.put(name + "_text", dv);
							} else {
								if (!isCacheDic) {
									jsonRec.put(name + "_text",
											si.toDisplayValue(v));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询个人信息失败!", e);
		}
		return reValue;
	}
}
