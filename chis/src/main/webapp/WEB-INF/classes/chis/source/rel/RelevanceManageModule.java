package chis.source.rel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;

import ctd.account.UserRoleToken;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.annotation.RpcService;
import ctd.validator.ValidateException;

public class RelevanceManageModule implements BSCHISEntryNames {
	private BaseDAO dao;

	public RelevanceManageModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 根据助理医生加载关联家庭医生
	 * 
	 * @Description:
	 * @param fda
	 * @return
	 * @throws ModelDataOperationException
	 * @author YuBo 2014-4-4 下午2:56:50
	 * @param manageUnitId
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> loadRelevanceManageDoctor(String fda,
			String manageUnitId) throws ModelDataOperationException {

		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		Session ss = null;
		try {
			ss = sf.openSession();
			String sql = new StringBuffer(
					"select distinct a.userId,b.name,a.manageUnitId,a.roleId,c.pyCode from UserRoleToken a,User b,SYS_Personnel c where ((a.userId = b.id and a.userId = c.personId) and a.domain = 'chis' and a.roleId='chis.100') and a.logoff='0' and lower(a.manageUnitId) like '")
			.append(manageUnitId)
			.append("%'").toString();
			Query q = ss.createQuery(sql);
			List<Object[]> records = q.list();
			int rownum = 1;
			for (Iterator<Object[]> it = records.iterator(); it.hasNext();) {
				Object[] u = it.next();
				Map<String, Object> r = new HashMap<String, Object>();
				r.put("XH", rownum);
				r.put("fd", u[0]);
				ls.add(r);
				rownum++;
			}
		} catch (Exception e) {
			throw new ModelDataOperationException("加载家庭医生列表失败。");
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
		return ls;
	}

	@SuppressWarnings("unchecked")
	public String getFdaByFdAndManageUnitId(String fd, String manageUnitId)
			throws ModelDataOperationException {
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		Session ss = null;
		try {
			ss = sf.openSession();
			String sql = new StringBuffer(
					"select distinct a.userId,b.name,a.manageUnitId,a.roleId,c.pyCode from UserRoleToken a,User b,SYS_Personnel c,REL_RelevanceDoctor d where (a.roleId in('gp.101') and (a.userId = b.id and a.userId = c.personId) and a.domain = 'gp') and lower(a.manageUnitId) like'")
					.append(manageUnitId)
					.append("%' and a.userId=d.fd and d.fd='").append(fd)
					.append("'").toString();
			Query q = ss.createQuery(sql);
			List<Object[]> records = q.list();
			Iterator<Object[]> it = records.iterator();
			if (it.hasNext()) {
				Object[] u = it.next();
				return (String) u[1];
			}
			return null;
		} catch (Exception e) {
			throw new ModelDataOperationException("根据家庭医生和机构查询家医助理失败。");
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
	}

	public void deleteRecordByFda(String fda)
			throws ModelDataOperationException {
		try {
			dao.doRemove("fda", fda, REL_RelevanceDoctor);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据家庭医生助理删除关联数据失败。");
		}

	}

	public void saveRelevanceManageDoctor(Map<String, Object> record)
			throws ValidateException, ModelDataOperationException {
		try {
			dao.doInsert(REL_RelevanceDoctor, record, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存关联管理数据失败。");
		}

	}

	public List<Map<String, Object>> loadSelectedRelevanceManageRecords(
			String fda) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "fda", "s", fda);
		try {
			return dao.doList(cnd, null, REL_RelevanceDoctor);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载关联数据失败。");
		}
	}

	/**
	 * 
	 * @Description: 更新机构字典
	 * @param req
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2014-5-16 下午1:25:35
	 * @Modify:
	 */
	@RpcService
	public Map<String, Object> updateJgzd(Map<String, Object> req)
			throws ServiceException {
		Map<String, Object> res = new HashMap<String, Object>();
		Session session = dao.getSession();
		try {
			Dictionary dic = DictionaryController.instance().get(
					"gp.@manageUnit");
			HashMap jgMap = dic.getItems();
			Transaction tx = session.beginTransaction();
			// 删除原表中数据
			Query q = session.createSQLQuery("delete from GP_JGZD");
			q.executeUpdate();
			Gpjgzd gpjgzd = null;
			int i = 0;
			Iterator<Entry<String, DictionaryItem>> it = jgMap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, DictionaryItem> entry = it.next();
				String key = entry.getKey();
				String value = entry.getValue().getText();
				gpjgzd = new Gpjgzd();
				gpjgzd.setJgid(key);
				gpjgzd.setJgname(value);
				session.save(gpjgzd);
				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}
				i++;
			}
			tx.commit();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return res;
	}
	public void saveResponsibleDoctor(Map<String, Object> req,Map<String, Object> res)
			throws ServiceException {
		Map<String, Object> body=(Map<String, Object>)req.get("body");
		String sql="select recordId as recordId from REL_ResponsibleDoctor a where a.assistantId=:assistantId" +
				" and a.doctorId=:doctorId";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("assistantId", body.get("assistantId")+"");
		p.put("doctorId", body.get("doctorId")+"");
		UserRoleToken user = UserRoleToken.getCurrent();
		try {
			Map<String, Object> data=dao.doSqlLoad(sql, p);
			if(data==null || data.size()==0){
				body.put("createDate", new Date());
				body.put("createUser",user.getUserId());
				body.put("createUnit",user.getManageUnitId());
				body.put("updateDate", new Date());
				body.put("updateUser",user.getUserId());
				dao.doSave("create",BSCHISEntryNames.REL_ResponsibleDoctor,body,false);
			}else{
				if((body.get("op")+"").equals("update")){
					body.put("updateDate", new Date());
					body.put("updateUser",user.getUserId());
					dao.doSave("update",BSCHISEntryNames.REL_ResponsibleDoctor,body,false);
				}else{
					res.put("code",300);
					res.put("msg","已存在相同维护记录，请核实！");
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
