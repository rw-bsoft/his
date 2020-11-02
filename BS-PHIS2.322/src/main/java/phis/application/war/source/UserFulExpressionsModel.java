/**
 * @(#)UserFulExpressionsModel.java Created on 2013-5-6 上午9:56:45
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.war.source;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class UserFulExpressionsModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(UserFulExpressionsModel.class);

	public UserFulExpressionsModel(BaseDAO dao) {
		this.dao = dao;
	}

	public List<Map<String, Object>> listUserFulExpressions(String stpType,
			String ygdm, String ksdm) {
		StringBuffer hql = new StringBuffer(
				"SELECT PTID as PTID,PTTYPE as PTTYPE,PTNAME as PTNAME,"
						+ "FRAMEWORKCODE as FRAMEWORKCODE,TEMPLATETYPE as TEMPLATETYPE,"
						+ "SPTTYPE as SPTTYPE,SPTCODE as SPTCODE,TEMPLATECODE as TEMPLATECODE,"
						+ "REGISTRAR as REGISTRAR,PTSTATE as PTSTATE FROM ");
		hql.append(" PRIVATETEMPLATE ");
		hql.append(" where ( PTTYPE = 3 ) AND ( PTSTATE IS NULL OR PTSTATE <> 9 )");
		if (stpType != null) {
			if (stpType.endsWith("0") && ksdm != null) {
				hql.append(" AND (SPTCODE = '" + ksdm + "' AND SPTTYPE = 0)");
			} else if (stpType.equals("1") && ygdm != null) {
				hql.append(" AND (SPTCODE= '" + ygdm + "' AND SPTTYPE = 1)");
			} else if (stpType.equals("2")) {
				hql.append(" AND (SPTTYPE = 2)");
			}else{
				hql.append(" AND (SPTCODE= '" + ygdm + "' or SPTCODE = '" + ksdm + "' or SPTCODE = 'all')");
			}
		}
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		Session session = null;
		try {
			SessionFactory sf = (SessionFactory) AppContextHolder.get()
					.getBean("mySessionFactory");
			session = sf.openSession();
			Query q = session.createQuery(hql.toString());
			List<Object[]> records = q.list();
			for (int i = 0; i < records.size(); i++) {
				Object[] r = records.get(i);
				Map<String, Object> o = new HashMap<String, Object>();
				o.put("PTID", r[0]);
				o.put("PTTYPE", r[1]);
				o.put("PTNAME", r[2]);
				o.put("FRAMEWORKCODE", r[3]);
				o.put("TEMPLATETYPE", r[4]);
				o.put("SPTTYPE", r[5]);
				o.put("SPTCODE", r[6]);
				o.put("TEMPLATECODE", r[7]);
				o.put("REGISTRAR", r[8]);
				o.put("PTSTATE", r[9]);
				rs.add(o);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rs;
	}

	public boolean getCountByPTNAME(String PTNAME, String SPTTYPE,
			String SPTCODE) {
		SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
				"mySessionFactory");
		Session session = sf.openSession();
		boolean flag = true;
		try {
			StringBuffer sql = new StringBuffer()
					.append("select count(*) from PRIVATETEMPLATE where PTName ='")
					.append(PTNAME).append("' AND SPTType = '").append(SPTTYPE)
					.append("' AND SPTCode = '").append(SPTCODE)
					.append("' AND 	( PTSTATE IS NULL OR PTSTATE <> 9 )");
			Query q = session.createQuery(sql.toString());
			List<Object[]> list = q.list();
			Object os = list.get(0);
			if (list != null && (Long) os > 0) {
				flag = false;
			}
		} finally {
			session.close();
		}
		return flag;
	}

	public Map<String, Object> saveUserFulExpressions(
			Map<String, Object> reqBody, Context ctx)
			throws ModelDataOperationException {
		SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
				"mySessionFactory");
		Session session = sf.openSession();
		Map<String, Object> map = null;
		try {
			String PTTEMPLATE = (String) reqBody.get("PTTEMPLATE");
			String PTTEMPLATETEXT = (String) reqBody.get("PTTEMPLATETEXT");
			reqBody.put("PTTEMPLATE", Hibernate.createBlob(PTTEMPLATE.getBytes("UTF-8")));
			reqBody.put("PTTEMPLATETEXT", Hibernate.createBlob(PTTEMPLATETEXT.getBytes("UTF-8")));
			session.beginTransaction();
			BaseDAO emrDao = new BaseDAO(ctx, session);
			map = emrDao.doSave("create",
					BSPHISEntryNames.PRIVATETEMPLATE_USERFUL, reqBody, true);
//			session.flush();
//			insertBlobData(session, map, PTTEMPLATE, "PTTEMPLATE","UTF-8");
//			insertBlobData(session, map, PTTEMPLATETEXT, "PTTEMPLATETEXT","UTF-8");
		} catch (ValidateException e) {
			logger.error("saveUserFulExpressions fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存常用语失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("saveUserFulExpressions fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存常用语失败！");
		} catch (Exception e) {
			logger.error("insertBlobData fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存常用语失败！");
		} finally {
			if (map == null) {
				session.getTransaction().rollback();
			} else {
				session.getTransaction().commit();
			}
			session.close();
		}
		return map;
	}

//	private void insertBlobData(Session session, Map<String, Object> map,
//			String strData, String clName,String charsetName) throws ModelDataOperationException {
//		if (map.get("PTID") == null) {
//			return;
//		}
//		String PTID = map.get("PTID").toString();
//		try {
//			Query q = session.createQuery("update PRIVATETEMPLATE set "
//					+ clName + "=:" + clName + " where PTID=" + PTID);
//			Blob blob = Hibernate.createBlob(strData.getBytes(charsetName));
//			q.setParameter(clName, blob);
//			q.executeUpdate();
//		} catch (Exception e) {
//			logger.error("insertBlobData fails.", e);
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "保存常用语失败！");
//		}
//	}

	public void removeUserFul(String pkey, Context ctx)
			throws ModelDataOperationException {
		SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
				"mySessionFactory");
		Session session = sf.openSession();
		int c = 0;
		Map<String, Object> parameters = null;
		try {
			String hql = "update PRIVATETEMPLATE set PTSTATE='" + 9
					+ "' where PTID=" + pkey;
			session.beginTransaction();
			BaseDAO emrDao = new BaseDAO(ctx, session);
			c = emrDao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("removeUserFul fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销常用语失败！");
		} finally {
			if (c == 0) {
				session.getTransaction().rollback();
			} else {
				session.getTransaction().commit();
			}
			session.close();
		}
	}

	public String getTxtOrXmlData(String pTID, String type)
			throws ModelDataOperationException {
		SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
				"mySessionFactory");
		Session session = sf.openSession();
		String clm = "";
		String charset = "";
		String body = null;
		try {
			if (type.equals("txt")) {
				clm = "PTTEMPLATETEXT";
				charset="UTF-8";
			} else if (type.equals("xml")) {
				clm = "PTTEMPLATE";
				charset="UTF-8";
			}
			StringBuffer sql = new StringBuffer().append("select ").append(clm)
					.append(" from PRIVATETEMPLATE where PTID =").append(pTID);
			Query q = session.createQuery(sql.toString());
			List<Object[]> list = q.list();
			Object os = list.get(0);
			if (os != null) {
				Blob blob = (Blob) os;
				InputStream is = blob.getBinaryStream();
				Reader utf8_reader = new InputStreamReader(is, charset);
				StringBuffer utf8Text = new StringBuffer();
				int charValue = 0;
				while ((charValue = utf8_reader.read()) != -1) {
					utf8Text.append((char) charValue);
				}
				body = utf8Text.toString();
			}
		} catch (SQLException e) {
			logger.error("getTxtOrXmlData fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询常用语文本失败！");
		} catch (IOException e) {
			logger.error("getTxtOrXmlData fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询常用语文本失败！");
		} finally {
			session.close();
		}
		return body;
	}
}
