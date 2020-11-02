/**
 * @(#)ChineseMedicineManageService.java Created on 2014-6-24 上午9:11:47
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ohr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.hibernate.Session;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.util.Hash;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class ChineseMedicineManageService extends AbstractActionService
		implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(ChineseMedicineManageService.class);
	
	
	protected void doCheckHasOldRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId=(String) req.get("empiId");
		ChineseMedicineManageModel cmmm=new ChineseMedicineManageModel(dao);
		try {
			cmmm.checkHasOldRecord(empiId,res);
			//由于医院太懒，不想建立老年人档案，只能程序去建（qnmlgb懂的人知道啥意思）
			if((Boolean)res.get("hasOldRecord")==false){
				HealthRecordModel hr=new HealthRecordModel(dao);
				Map<String, Object> healthRecord = hr
						.getHealthRecordByEmpiId(empiId);
				Map<String, Object> oldmap= new HashMap<String, Object>();
				if(healthRecord==null){
					
				}else{
					oldmap.put("status", "0");
					Session s = dao.getSession();
					String insertohr="insert into MDC_OldPeopleRecord(phrId,empiId,manaDoctorId,manaUnitId,createUnit,createUser,status,createDate)" +
							" values('"+healthRecord.get("phrId")+"','"+empiId+"','"+healthRecord.get("manaDoctorId")+"'," +
							" '"+healthRecord.get("manaUnitId")+"','"+healthRecord.get("manaUnitId")+"','"+healthRecord.get("manaDoctorId")+"','0',sysdate)";
					s.createSQLQuery(insertohr).executeUpdate();
				}
				res.put("hasOldRecord", true);
			}
		} catch (ModelDataOperationException e) {
			logger.error("do Check Has Old Record is fail");
			throw new ServiceException(e);
		}
	}
	
	protected void doSaveChineseMedicineManage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema=(String) req.get("schema");
		String op=(String) req.get("op");
		Map<String, Object> body=(Map<String, Object>) req.get("body");
		body.put("status", "0");//前台有时传过来的这个值有问题
		ChineseMedicineManageModel cmmm=new ChineseMedicineManageModel(dao);
		try {
			//数据库出现重复记录，下面做一些去重程序
//			String empiId=body.get("empiId").toString();
//			String today=(new java.text.SimpleDateFormat("yyyy-MM-dd")).format(new Date());
//			
//			String findsql="select * from "+schema+" where empiId=:empiId and to_char(createDate,'yyyy-mm-dd')=:today";
//			Map<String, Object> p = new HashMap<String, Object>();
//			p.put("empiId", empiId);
//			p.put("today", today);
//			try {
//				List<Map<String, Object>> list=dao.doSqlQuery(findsql, p);
//				if(list !=null && list.size()>0){
//					cmmm.logoutChineseMedicineManagetoday(empiId, today);
//				}
//			} catch (PersistentDataOperationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			Map<String, Object> result=cmmm.saveChineseMedicineManage(schema,op,body);
			res.put("body", result);
		} catch (ModelDataOperationException e) {
			logger.error("save ChineseMedicineManage is fail");
			throw new ServiceException(e);
		}
	}
	
	protected void doLogoutChineseMedicineManage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema=(String) req.get("schema");
		String pkey=(String) req.get("pkey");
		ChineseMedicineManageModel cmmm=new ChineseMedicineManageModel(dao);
		try {
			cmmm.logoutChineseMedicineManage(schema,pkey);
		} catch (ModelDataOperationException e) {
			logger.error("save ChineseMedicineManage is fail");
			throw new ServiceException(e);
		}
	}
	
}
