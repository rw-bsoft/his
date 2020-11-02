/**
 * @(#)VindicateLogService.java Created on 2014-6-3 上午10:11:06
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.log;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.util.UserUtil;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class VindicateLogService implements BSCHISEntryNames {
	private static final Logger logger = LoggerFactory
			.getLogger(VindicateLogService.class);

	private HashMap<String, AllocationEntity> allocationMap = new HashMap<String, AllocationEntity>();

	/**
	 * 获得allocationMap
	 * 
	 * @return the allocationMap
	 */
	public HashMap<String, AllocationEntity> getAllocationMap() {
		return allocationMap;
	}

	/**
	 * 设置allocationMap
	 * 
	 * @param allocationMap
	 *            the allocationMap to set
	 */
	public void setAllocationMap(HashMap<String, AllocationEntity> allocationMap) {
		this.allocationMap = allocationMap;
	}

	/**
	 * 
	 * @Description:产生维护数据记录
	 * @param entryName
	 *            操作对象表名（例如：保存家庭档案表时为家庭档案表
	 *            chis.application.fhr.schemas.EHR_FamilyRecord）
	 * @param op
	 *            操作类型（1:create,2:update,3:logout,4:delete）
	 * @param recordId
	 *            操作记录的主键
	 * @param dao
	 *            操作数据库的BaseDao类对象
	 * @param empiId
	 *            如果是个人业务有empiId,要专入empiId参数
	 * @return 返回日志ID logId
	 * @throws ServiceException
	 * @author ChenXianRui 2014-6-3 下午1:45:43
	 * @Modify:
	 */
	public Map<String, Object> saveVindicateLog(String entryName, String op,
			String recordId, BaseDAO dao, String... empiId)
			throws ServiceException {
		Map<String, Object> logMap = new HashMap<String, Object>();
		logMap.put("recordId", recordId);
		AllocationEntity ae = allocationMap.get(entryName);
		if (ae == null) {
			// 取默认值
			ae = new AllocationEntity();
		}
		logMap.put("logType", ae.getLogType());
		logMap.put("operateType", this.getLogTypeKey(op));
		logMap.put("updateTable", entryName);
		logMap.put("createDate", new Date());
		logMap.put("createUser", UserUtil.get(UserUtil.USER_ID));
		logMap.put("createUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		if (empiId.length == 1) {
			logMap.put("empiId", empiId[0]);
		} else {
			logMap.put("empiId", "");
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave("create", ae.getLogStoreTable(), logMap, false);
		} catch (PersistentDataOperationException e) {
			logger.error("Save vindicate log record failure.", e);
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"保存维护日志失败！", e);
		}
		if (empiId.length == 1) {
			String logId = (String) rsMap.get("logId");
			this.saveVindicateNumber(empiId[0], logId, ae.getLogStoreTable(), dao);
		}
		return rsMap;
	}
	
	/**
	 * 
	 * @Description:保存个人的档案记录
	 * @param op
	 *            操作类型（1:create,2:update,3:logout,4:delete）
	 * @param dao
	 *            操作数据库的BaseDao类对象
	 * @param empiId
	 *            如果是个人业务有empiId,要专入empiId参数
	 * @throws ServiceException
	 * @author ChenXianRui 2014-6-3 下午1:45:43
	 * @Modify:
	 */
	public void saveRecords(String businessType, String op,
			BaseDAO dao,String empiId)
			throws ServiceException {
		try {
			int businessValue=0;
			//如果是新建档案，注销所有档案
			if(op.equals("create")||op.equals("logoutAll"))
			{
				businessValue=1;
			}
			//如果是专档注销或删除档案，恢复所有档案
			else if(op.equals("logout")||op.equals("delete")||op.equals("recoverAll"))
			{
				businessValue=0;
			}
			else if(op.equals("update"))
			{
				businessValue=1;
			}
			else
			{
				return;
			}
			// 如果存在档案则更新,不存在则插入
			Map param=new HashMap();
			param.put("empiId", empiId);
			List l=dao.doQuery("select empiId as empiId  from EHR_RecordInfo where empiId=:empiId", param);
			if(l.size()>0)//存在则更新
			{
				param.put(businessType,businessValue);
				dao.doUpdate("update EHR_RecordInfo set "+businessType+"=:"+businessType+" where empiId=:empiId", param);
				
			}else//不存在插入
			{
				param.put(businessType,businessValue);
				dao.doInsert(BSCHISEntryNames.EHR_RecordInfo, param, false);
				//dao.doSq("insert into EHR_RecordInfo(empiId,"+businessType+") values(:empiId,:"+businessType+")", param);
			}

		} catch (Exception e) {
			logger.error("saveRecordInfo failed.", e);
		} finally {
		}
	}

	/**
	 * 
	 * @Description:获取操作类型字典Key
	 * @param op
	 * @return
	 * @author ChenXianRui 2014-6-3 下午3:23:20
	 * @Modify:
	 */
	private String getLogTypeKey(String op) {
		String otKey = "";
		if (op.length() == 1) {
			otKey = op;
		} else {
			try {
				Dictionary dic = DictionaryController.instance().get(
						"chis.dictionary.operateType");
				List<String> kList = dic.getKey(op);
				otKey = kList.get(0);
			} catch (ControllerException e) {
				logger.error(
						"Read chis.dictionary.operateType dictionary failure.",
						e);
				e.printStackTrace();
			}

		}
		return otKey;
	}

	/**
	 * 
	 * @Description:保存健康档案维护数
	 * @param empiId
	 * @param logId
	 * @param logTable
	 * @throws ServiceException
	 * @author ChenXianRui 2014-6-8 下午4:22:09
	 * @Modify:
	 */
	private void saveVindicateNumber(String empiId, String logId,
			String logTable, BaseDAO dao) throws ServiceException {
		if (StringUtils.isNotEmpty(empiId)) {
			String hql = new StringBuffer(" from ")
					.append(LOG_EHR_VindicateNumber)
					.append(" where empiId=:empiId and year=:year").toString();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("empiId", empiId);
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			parameters.put("year", year+"");
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doQuery(hql, parameters);
			} catch (PersistentDataOperationException e) {
				logger.error("Select LOG_EHR_VindicateNumber by empiId["
						+ empiId + "] and year[" + year + "] failure.", e);
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,
						"获取档案维护数记录失败！", e);
			}
			Map<String, Object> rMap = new HashMap<String, Object>();
			rMap.put("empiId", empiId);
			rMap.put("year", year + "");
			rMap.put("logId", logId);
			rMap.put("logTable", logTable);
			if (rsList != null && rsList.size() > 0) {
				Map<String, Object> vMap = rsList.get(0);
				rMap.put("recordId", vMap.get("recordId"));
				try {
					dao.doSave("update", LOG_EHR_VindicateNumber, rMap, true);
				} catch (PersistentDataOperationException e) {
					logger.error("Update LOG_EHR_VindicateNumber failure.", e);
					throw new ServiceException(Constants.CODE_DATABASE_ERROR,
							"更新档案日志维护数失败！", e);
				}
			} else {
				try {
					dao.doSave("create", LOG_EHR_VindicateNumber, rMap, true);
				} catch (PersistentDataOperationException e) {
					logger.error("Inster LOG_EHR_VindicateNumber failure.", e);
					throw new ServiceException(Constants.CODE_DATABASE_ERROR,
							"更新档案日志维护数失败！", e);
				}
			}
		}
	}
}