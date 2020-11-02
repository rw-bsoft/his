/**
 * @(#)DefEvaluateJobDetailSchedule.java Created on 2011-7-22 上午11:11:02
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.BSCHISUtil;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 
 * 
 * @author <a href="mailto:yuhua@bsoft.com.cn">yuhua</a>
 */
public class DefEvaluateJobDetailSchedule extends AbstractJobSchedule implements BSCHISEntryNames {

	private static final Log logger = LogFactory
			.getLog(DefEvaluateJobDetailSchedule.class);

//	private SessionFactory sessionFactory = null;
//	
//	BaseDAO dao = null ;

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
//	public void execute1() throws JobExecutionException {
//		Session session = null;
//		Transaction trx = null;
//		try {
//			session = sessionFactory.openSession();
//			trx = session.beginTransaction();
//			
//			processLimbEvaluateTask(session);
//			processBrainEvaluateTask(session);
//			processIntellectEvaluateTask(session);
//			
//			processLimbTrainingRecordTask(session);
//			processBrainTrainingRecordTask(session);
//			processIntellectTrainingRecordTask(session);
//			
//			trx.commit();
//		} catch (Exception e) {
//			trx.rollback();
//			throw new JobExecutionException(e);
//		} finally {
//			if (session != null && session.isOpen()) {
//				session.close();
//			}
//		}
//	}
	
	private void processLimbEvaluateTask(Session session,BaseDAO dao)throws PersistentDataOperationException, ValidateException{
		String sql = "select a.id as recordId,a.empiId as empiId,a.manaUnitId as manaUnitId,a.manaDoctorId as manaDoctorId,a.createDate as createDate,(select max(visitDate) as visitDate from DEF_LimbTrainingEvaluate b where a.id = b.defId) visitDate,c.workId as workId from DEF_LimbDeformityRecord a left join (select workId as workId,recordId as recordId,empiId as empiId,workType as workType , manaUnitId as manaUnitId,doctorId as doctorId from PUB_WorkList where workType = :workType) c on (a.id = c.recordId) where a.status =:status and a.closeFlag =:closeFlag";
		Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setString("status", "0");
		query.setString("closeFlag", "n");
		query.setString("workType", "10");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> list = query.list();
		Date date= new Date();
		for(Map<String,Object> r :list){
			if(null == r.get("WORKID") || r.get("WORKID").equals("")){
				Date visitDate = (Date) r.get("VISITDATE");
				if(visitDate != null){
					if (BSCHISUtil.getMonths(visitDate,date)>3) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("recordId", r.get("RECORDID"));
						data.put("empiId", r.get("EMPIID"));
						data.put("workType", "10");
						data.put("count", "1");
						data.put("manaUnitId", r.get("MANAUNITID"));
						data.put("doctorId", r.get("MANADOCTORID"));
						Calendar c = Calendar.getInstance();
						data.put("beginDate", c.getTime());
						c.add(Calendar.YEAR, 1);
						data.put("endDate", c.getTime());
						dao.doInsert(PUB_WorkList, data, false);
					}	
				}
			}
		}
	}
	
	
	private void processBrainEvaluateTask(Session session,BaseDAO dao)throws PersistentDataOperationException, ValidateException{
		String sql = "select a.id as recordId,a.empiId as empiId,a.manaUnitId as manaUnitId,a.manaDoctorId as manaDoctorId,a.createDate as createDate,(select max(visitDate) as visitDate from DEF_BrainTrainingEvaluate b where a.id = b.defId) visitDate,c.workId as workId from DEF_BrainDeformityRecord a left join (select workId as workId,recordId as recordId,empiId as empiId,workType as workType , manaUnitId as manaUnitId,doctorId as doctorId from PUB_WorkList where workType = :workType) c on (a.id = c.recordId) where a.status =:status and a.closeFlag =:closeFlag";
		Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setString("status", "0");
		query.setString("closeFlag", "n");
		query.setString("workType", "11");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> list = query.list();
		Date date= new Date();
		for(Map<String,Object> r :list){
			if(null == r.get("WORKID") || r.get("WORKID").equals("")){
				Date visitDate = (Date) r.get("VISITDATE");
				if(visitDate != null){
					if (BSCHISUtil.getMonths(visitDate,date)>6) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("recordId", r.get("RECORDID"));
						data.put("empiId", r.get("EMPIID"));
						data.put("workType", "11");
						data.put("count", "1");
						data.put("manaUnitId", r.get("MANAUNITID"));
						data.put("doctorId", r.get("MANADOCTORID"));
						Calendar c = Calendar.getInstance();
						data.put("beginDate", c.getTime());
						c.add(Calendar.YEAR, 1);
						data.put("endDate", c.getTime());
						dao.doInsert(PUB_WorkList, data, false);
					}	
				}
			}
		}
	}
	
	
	private void processIntellectEvaluateTask(Session session,BaseDAO dao)throws PersistentDataOperationException, ValidateException{
		String sql = "select a.id as recordId,a.empiId as empiId,a.manaUnitId as manaUnitId,a.manaDoctorId as manaDoctorId,a.createDate as createDate,(select max(visitDate) as visitDate from DEF_IntellectTrainingEvaluate b where a.id = b.defId) visitDate,c.workId as workId from DEF_IntellectDeformityRecord a left join (select workId as workId,recordId as recordId,empiId as empiId,workType as workType , manaUnitId as manaUnitId,doctorId as doctorId from PUB_WorkList where workType = :workType) c on (a.id = c.recordId) where a.status =:status and a.closeFlag =:closeFlag";
		Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setString("status", "0");
		query.setString("closeFlag", "n");
		query.setString("workType", "12");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> list = query.list();
		Date date= new Date();
		for(Map<String,Object> r :list){
			if(null == r.get("WORKID") || r.get("WORKID").equals("")){
				Date visitDate = (Date) r.get("VISITDATE");
				if(visitDate != null){
					if (BSCHISUtil.getMonths(visitDate,date)>6) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("recordId", r.get("RECORDID"));
						data.put("empiId", r.get("EMPIID"));
						data.put("workType", "12");
						data.put("count", "1");
						data.put("manaUnitId", r.get("MANAUNITID"));
						data.put("doctorId", r.get("MANADOCTORID"));
						Calendar c = Calendar.getInstance();
						data.put("beginDate", c.getTime());
						c.add(Calendar.YEAR, 1);
						data.put("endDate", c.getTime());
						dao.doInsert(PUB_WorkList, data, false);
					}	
				}
			}
		}
	}
	
	private void processLimbTrainingRecordTask(Session session,BaseDAO dao)throws PersistentDataOperationException, ValidateException{
		String sql = "select a.id as recordId,a.empiId as empiId,a.manaUnitId as manaUnitId,a.manaDoctorId as manaDoctorId,a.createDate as createDate,(select max(trainingDate) as trainingDate  from DEF_LimbTrainingRecord d where planId in (select id from DEF_LimbTrainingPlan b where a.id = b.defId)) trainingDate,c.workid as workId from DEF_LimbDeformityRecord a left join (select * from pub_worklist where worktype =:workType) c on (a.id = c.recordId) where a.status =:status and a.closeFlag =:closeFlag";
		Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setString("status", "0");
		query.setString("closeFlag", "n");
		query.setString("workType", "13");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = query.list();
		Date date= new Date();
		for(Map<String,Object> r :list){
			if(null == r.get("WORKID") || r.get("WORKID").equals("") ){
				Date visitDate = (Date) r.get("TRAININGDATE");
				if(visitDate != null){
					if (BSCHISUtil.getMonths(visitDate,date)>1) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("recordId", r.get("RECORDID"));
						data.put("empiId", r.get("EMPIID"));
						data.put("workType", "13");
						data.put("count", "1");
						data.put("manaUnitId", r.get("MANAUNITID"));
						data.put("doctorId", r.get("MANADOCTORID"));
						Calendar c = Calendar.getInstance();
						data.put("beginDate", c.getTime());
						c.add(Calendar.YEAR, 1);
						data.put("endDate", c.getTime());
						dao.doInsert(PUB_WorkList, data, false);
					}
				}
				
			}
		}
	}
	
	private void processBrainTrainingRecordTask(Session session,BaseDAO dao)throws PersistentDataOperationException, ValidateException{
		String sql = "select a.id as recordId,a.empiId as empiId,a.manaUnitId as manaUnitId,a.manaDoctorId as manaDoctorId,a.createDate as createDate,(select max(trainingDate) as trainingDate  from DEF_BrainTrainingRecord d where planId in (select id from DEF_BrainTrainingPlan b where a.id = b.defId)) trainingDate,c.workid as workId from DEF_BrainDeformityRecord a left join (select * from pub_worklist where worktype =:workType) c on (a.id = c.recordId) where a.status =:status and a.closeFlag =:closeFlag";
		Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setString("status", "0");
		query.setString("closeFlag", "n");
		query.setString("workType", "14");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = query.list();
		Date date= new Date();
		for(Map<String,Object> r :list){
			if(null == r.get("WORKID") || r.get("WORKID").equals("") ){
				Date visitDate = (Date) r.get("TRAININGDATE");
				if(visitDate != null){
					if (BSCHISUtil.getMonths(visitDate,date)>2) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("recordId", r.get("RECORDID"));
						data.put("empiId", r.get("EMPIID"));
						data.put("workType", "14");
						data.put("count", "1");
						data.put("manaUnitId", r.get("MANAUNITID"));
						data.put("doctorId", r.get("MANADOCTORID"));
						Calendar c = Calendar.getInstance();
						data.put("beginDate", c.getTime());
						c.add(Calendar.YEAR, 1);
						data.put("endDate", c.getTime());
						dao.doInsert(PUB_WorkList, data, false);
					}
				}
				
			}
		}
	}

	private void processIntellectTrainingRecordTask(Session session,BaseDAO dao)throws PersistentDataOperationException, ValidateException{
		String sql = "select a.id as recordId,a.empiId as empiId,a.manaUnitId as manaUnitId,a.manaDoctorId as manaDoctorId,a.createDate as createDate,(select max(trainingDate) as trainingDate  from DEF_IntellectTrainingRecord d where planId in (select id from DEF_IntellectTrainingPlan b where a.id = b.defId)) trainingDate,c.workid as workId from DEF_IntellectDeformityRecord a left join (select * from pub_worklist where worktype =:workType) c on (a.id = c.recordId) where a.status =:status and a.closeFlag =:closeFlag";
		Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setString("status", "0");
		query.setString("closeFlag", "n");
		query.setString("workType", "15");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = query.list();
		Date date= new Date();
		for(Map<String,Object> r :list){
			if(null == r.get("WORKID") || r.get("WORKID").equals("") ){
				Date visitDate = (Date) r.get("TRAININGDATE");
				if(visitDate != null){
					if (BSCHISUtil.getMonths(visitDate,date)>2) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("recordId", r.get("RECORDID"));
						data.put("empiId", r.get("EMPIID"));
						data.put("workType", "15");
						data.put("count", "1");
						data.put("manaUnitId", r.get("MANAUNITID"));
						data.put("doctorId", r.get("MANADOCTORID"));
						Calendar c = Calendar.getInstance();
						data.put("beginDate", c.getTime());
						c.add(Calendar.YEAR, 1);
						data.put("endDate", c.getTime());
						dao.doInsert(PUB_WorkList, data, false);
					}
				}
				
			}
		}
	}
//	public SessionFactory getSessionFactory() {
//		return sessionFactory;
//	}
//
//	public void setSessionFactory(SessionFactory sessionFactory) {
//		this.sessionFactory = sessionFactory;
//	}

	@Override
	public void doJob(BaseDAO dao) throws ModelDataOperationException {
		Session session = (Session) dao.getContext().get(Context.DB_SESSION);
		try {
			processLimbEvaluateTask(session,dao);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			processBrainEvaluateTask(session,dao);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			processIntellectEvaluateTask(session,dao);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			processLimbTrainingRecordTask(session,dao);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			processBrainTrainingRecordTask(session,dao);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			processIntellectTrainingRecordTask(session,dao);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
