/**
 * @(#)SignContractRecordModule.java Created on 2017-11-9 下午1:47:37
 *
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.scm;

import java.text.SimpleDateFormat;
import java.util.*;

import chis.source.util.SchemaUtil;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;

/**
 * @description
 *
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class SignContractRecordModule implements BSCHISEntryNames {
	private BaseDAO dao;

	public SignContractRecordModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * @Description:生成签约计划，安排签约任务
	 * @param SCID
	 *            签约编码
	 * @param empiId
	 *            个人主索引
	 * @param beginDate
	 *            签约生效日期
	 * @param endDate
	 *            签约生效截止日期
	 * @param scip
	 *            签约人所属人群
	 * @author ChenXianRui 2017-11-15 下午3:56:46
	 * @throws ServiceException
	 * @Modify:
	 */
	public void genSCPlan(String SCID, String empiId, Date beginDate,
			Date endDate, String scip) throws ServiceException {
		// 获取签约选择的包列表
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.SCID", "s", SCID);
		List<Map<String, Object>> scpList = null;
		try {
			scpList = dao.doList(cnd, null, SCM_SignContractPackage);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"获取签约服务包失败！", e);
		}
		if (scpList != null && scpList.size() > 0) {
			int len = scpList.size();
			String[] scpIDS = new String[len];
			for (int i = 0; i < len; i++) {
				Map<String, Object> scpMap = scpList.get(i);
				scpIDS[i] = (String) scpMap.get("SPID");
			}
			// 查询出签约生效一周内管理的项目
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "b.itemType", "s",
					"4");
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "b.isBottom", "s",
					"y");
			List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "b.isOneWeekWork",
					"s", "y");
			List<?> cnd4 = CNDHelper.createSimpleCnd("in", "a.SPID", "$",
					scpIDS);
			List<?> cnd5 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<?> cnd6 = CNDHelper.createArrayCnd("and", cnd3, cnd4);
			List<?> cnd7 = CNDHelper.createArrayCnd("and", cnd5, cnd6);
			List<Map<String, Object>> spiList = null;
			try {
				spiList = dao.doList(cnd7, "b.itemCode asc",
						SCM_ServicePackageItems);
			} catch (PersistentDataOperationException e) {
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,
						"获取签约服务包中在签约生效一周内完成的项目失败！", e);
			}
			if (spiList != null && spiList.size() > 0) {
				// 重新首条签约计划
				Map<String, Object> spMap = new HashMap<String, Object>();
				spMap.put("empiId", empiId);
				spMap.put("SCID", SCID);
				spMap.put("beginDate", beginDate);
				spMap.put("planDate", beginDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(beginDate);
				cal.add(Calendar.DATE, 7);
				spMap.put("endDate", cal.getTime());
				spMap.put("planStatus", "0");
				Map<String, Object> rpMap = null;
				try {
					rpMap = dao.doInsert(SCM_ServicePlan, spMap, false);
				} catch (PersistentDataOperationException e) {
					throw new ServiceException(Constants.CODE_DATABASE_ERROR,
							"增加签约首条计划失败！", e);
				}
				if (rpMap != null && rpMap.size() > 0) {
					// 获取年龄
					EmpiModel em = new EmpiModel(dao);
					int age = 0;
					try {
						age = em.getAge(empiId);
					} catch (ModelDataOperationException e1) {
						throw new ServiceException(
								Constants.CODE_DATABASE_ERROR, "计算年龄失败！", e1);
					}
					Application app = null;
					try {
						app = ApplicationUtil
								.getApplication(Constants.UTIL_APP_ID);
					} catch (ControllerException e) {
						e.printStackTrace();
					}
					// 获取老年人定义的年龄
					String oldPeopleAge = (String) app
							.getProperty("oldPeopleAge");
					int opAge = BSCHISUtil.parseToInt(oldPeopleAge);
					// 获取儿童注册定义的年龄
					String childrenRegisterAge = (String) app
							.getProperty("childrenRegisterAge");
					int crAge = BSCHISUtil.parseToInt(childrenRegisterAge);
					// 首条签约计划的任务
					Dictionary maDic = null;
					try {
						maDic = DictionaryController.instance().get(
								"chis.dictionary.moduleApp");
					} catch (ControllerException e) {
						e.printStackTrace();
					}
					for (Map<String, Object> spiMap : spiList) {
						String siip = (String) spiMap.get("intendedPopulation");// 项目所属人群
						String moduleAppId = (String) spiMap.get("moduleAppId");
						if (scip.indexOf(siip) != -1) {
							// 签约包项目中有不符合签约人所属人群，跳过
							continue;
						}
						if ("3".equals(siip) && age > crAge) {
							// 项目属性儿童人群，但当前签约人年龄大于儿童管理注册年龄，跳过
							continue;
						}
						if ("5".equals(siip) && age < opAge) {
							// 项目属于老年人人群，但当前签约人年龄没有到达老年人建档年限，跳过
							continue;
						}
						Map<String, Object> sptMap = new HashMap<String, Object>();
						sptMap.put("planId", rpMap.get("planId"));
						sptMap.put("empiId", empiId);
						sptMap.put("SCID", SCID);
						if (maDic != null) {
							sptMap.put("taskName", maDic.getText(moduleAppId));
						}
						sptMap.put("moduleAppId", moduleAppId);
						sptMap.put("status", "0");
						try {
							dao.doInsert(SCM_ServicePlanTask, sptMap, false);
						} catch (PersistentDataOperationException e) {
							e.printStackTrace();
						}
					}
				}
			}
			// 人群判断，原随访计划拷贝
			String[] ips = scip.split(",");
			if (ips.length == 0) {// 单一
				if (!"0".equals(scip) && !"4".equals(scip)) {
					copyPlan(SCID, empiId, beginDate, endDate, scip, scpIDS);
				} else if ("4".equals(scip)) {
					genPWSCPlan(SCID, empiId, beginDate, endDate, scip, scpIDS);
				}
			} else {// 组合
				mergePlan(SCID, empiId, beginDate, endDate, scip, scpIDS);
			}
		}
	}
	/**
	 * @Description:生成签约计划，安排签约任务
	 * @param SCID
	 *            签约编码
	 * @param beginDate
	 *            签约生效日期
	 * @param endDate
	 *            签约生效截止日期
	 * @param scip
	 *            签约人所属人群
	 * @param empiId
	 *            个人主索引
	 * @author guoliang 2018-2-1 下午3:56:46
	 * @throws ServiceException
	 * @Modify:
	 * @return
	 */
	public List<Map<String, Object>> genSignContractPlan(Map<String, Object> req, String empiId, Long scid)
			throws ServiceException {
		List<Map<String,Object>> reqList = (List<Map<String, Object>>) req.get("listData");
		List<Map<String , Object>> records = new ArrayList<Map<String , Object>>();
		Map<String, Object> reqform = (Map<String, Object>) req.get("formData");
		int times = Integer.parseInt(reqform.get("year")+"");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		//当前年份
		int year = Integer.parseInt(sdf.format(date));
		for(int n = 0;n<times;n++){
			for(Map<String,Object> item:reqList){
				if(item!=null){
			    try {
					// 服务包编号 spId
					String spId = (String) item.get("SPID");
					String spIId = (String) item.get("SPIID");
					//检查档案情况更新任务标记
					int status = 3;
					Map<String, Object> mapToDB = new HashMap<String, Object>();
					mapToDB.put("empiId", empiId);
					mapToDB.put("taskName", item.get("itemName"));
					mapToDB.put("moduleAppId", item.get("moduleAppId"));
					mapToDB.put("status", status);
					mapToDB.put("SPID", spId);
					mapToDB.put("SPIID", spIId);
					mapToDB.put("packageName", item.get("packageName"));
					mapToDB.put("SCID", scid);
					mapToDB.put("taskCode", item.get("itemCode"));
					mapToDB.put("sort", item.get("remark"));
					mapToDB.put("year", year);
					// 如果项目类型为GXY
					List<Object> cnd = CNDHelper.createSimpleCnd("eq",
							"a.empiId", "s", empiId);
					List<Object> cnd2 = CNDHelper.createSimpleCnd("eq",
							"a.taskCode", "s", item.get("itemCode") + "");
					List<Object> cnd3 = CNDHelper.createSimpleCnd("eq",
							"a.year", "s", year+ "");
					List<Object> cnd4 = CNDHelper.createSimpleCnd("eq","a.SPID","s",spId);
					List<Object> cndArray = CNDHelper.createArrayCnd("and",
							cnd, cnd2);
					List<Object> cndArray2 = CNDHelper.createArrayCnd("and",
							cndArray, cnd3);
					List<Object> cndArray3 = CNDHelper.createArrayCnd("and",
							cndArray2, cnd4);
					String op = "create";
						// 查看个人是否存在签约计划，并且是否已经签约过相关签约服务包,如果有则更新该签约包
					//前台控制不可更新
						/*Map<String, Object> taskMap = dao.doLoad(cndArray3,
								SCM_ServiceContractPlanTask);
						if (taskMap != null && !taskMap.isEmpty()) {
							op = "update";
							mapToDB.put("taskId", taskMap.get("taskId"));
						}*/
						Map<String , Object> record = dao.doSave(op, SCM_ServiceContractPlanTask, mapToDB, false);
						record.put("SCID", scid);
						record.put("SPID", spId);
						record.put("taskCode", item.get("itemCode"));
						records.add(record);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			}
		//增加年份
		year++;
		}
		return records;
	}
	/**
	 *解约并修改该居民的签约
	 * @param empiId
	 */
	public void stopContractPlanTaskStatus(String scid){
		Map<String,Object> map = new HashMap<String,Object>();
		String upHql = new StringBuffer("update ")
		.append(SCM_ServiceContractPlanTask)
		.append(" set status=:status where SCID=:SCID").toString();
		map.put("status", "2");
		map.put("SCID", Long.parseLong(scid));
        try {
			dao.doUpdate(upHql, map);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 检查是否存在档案标记
	 * @param entryName
	 * @param empiId
	 * @return
	 */
    protected boolean haveOrNotRecord(String entryName,String empiId){
    	boolean flag = false;
    	try {
			List<Map<String,Object>> list = dao.doQuery("empiId", empiId, entryName);
			if(list!=null&&list.size()>0){
				flag = true;
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
    }
	/**
	 * @Description:保存签约所选择的服务包
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2017-11-10 下午2:30:03
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void saveSignContractPackage(String empiId,Long scid,Map<String, Object> req)
			throws ServiceException {
		List<Map<String,Object>> reqList = (List<Map<String, Object>>) req.get("listData");
		Map<String, Object> map = null;
		try {
			//修改服务包时先清空原有服务包内容
			//dao.doRemove("empiId", empiId, SCM_SignContractPackage);
			if (reqList != null && reqList.size() > 0) {
				for (Map<String,Object> item : reqList) {
					if(item!= null){
					 map = new HashMap<String, Object>();
					 map.put("empiId", empiId);
					 map.put("SCID", scid);
					 map.put("SPID", item.get("SPID"));
					 map.put("SPIID", item.get("SPIID"));
					 map.put("SERVICETIMES", item.get("SERVICETIMES"));
					 
					dao.doSave("create", SCM_SignContractPackage, map, false);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description:获取签约包中某父节点下的全部项目
	 * @param scpIDS
	 * @param parentCode
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2017-11-17 下午3:48:49
	 * @Modify:
	 */
	private List<Map<String, Object>> getSCPItemsByParentCode(String[] scpIDS,
			String parentCode) throws ServiceException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "b.isBottom", "s", "y");// 是项目
		List<?> cnd2 = CNDHelper
				.createSimpleCnd("eq", "b.itemNature", "s", "1");// 性质公卫
		List<?> cnd3 = CNDHelper.createSimpleCnd("in", "a.SPID", "$", scpIDS);// 在签约包中
		List<?> cnd4 = CNDHelper.createSimpleCnd("eq", "b.parentCode", "s",
				parentCode);// 在签约包中
		List<?> cnd5 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<?> cnd6 = CNDHelper.createArrayCnd("and", cnd3, cnd4);
		List<?> cnd7 = CNDHelper.createArrayCnd("and", cnd5, cnd6);
		List<Map<String, Object>> spiList = null;
		try {
			spiList = dao.doList(cnd7, "b.itemCode asc",
					SCM_ServicePackageItems);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"获取签约服务包中可以随访中完成的项目失败！", e);
		}
		return spiList;
	}

	/**
	 * @Description:依据原中的计划增加签约计划
	 * @param SCID
	 * @param empiId
	 * @param zpMap
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2017-11-17 下午4:05:44
	 * @Modify:
	 */
	private Map<String, Object> addSCPlan(String SCID, String empiId,
			Map<String, Object> zpMap) throws ServiceException {
		Map<String, Object> spMap = new HashMap<String, Object>();
		spMap.put("empiId", empiId);
		spMap.put("SCID", SCID);
		spMap.put("beginDate", zpMap.get("beginDate"));
		spMap.put("planDate", zpMap.get("planDate"));
		spMap.put("endDate", zpMap.get("endDate"));
		spMap.put("planStatus", "0");
		Map<String, Object> rpMap = null;
		try {
			rpMap = dao.doInsert(SCM_ServicePlan, spMap, false);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"增加签约计划失败！", e);
		}
		return rpMap;
	}

	/**
	 * @Description: 给签约计划 增加 签约任务
	 * @param SCID
	 * @param empiId
	 * @param scPlanId
	 * @param spiList
	 * @throws ValidateException
	 * @author ChenXianRui 2017-11-17 下午4:31:25
	 * @Modify:
	 */
	private void addSCPlanTask(String SCID, String empiId, String scPlanId,
			List<Map<String, Object>> spiList) throws ValidateException {
		if (spiList != null && spiList.size() > 0) {
			Dictionary maDic = null;
			try {
				maDic = DictionaryController.instance().get(
						"chis.dictionary.moduleApp");
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			for (Map<String, Object> spiMap : spiList) {
				Map<String, Object> sptMap = new HashMap<String, Object>();
				sptMap.put("planId", scPlanId);
				sptMap.put("empiId", empiId);
				sptMap.put("SCID", SCID);
				String moduleAppId = (String) spiMap.get("moduleAppId");
				if (maDic != null) {
					sptMap.put("taskName", maDic.getText(moduleAppId));
				}
				sptMap.put("moduleAppId", moduleAppId);
				sptMap.put("status", "0");
				try {
					dao.doInsert(SCM_ServicePlanTask, sptMap, false);
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * 一人一档类型的专档计划处理
	 */
	private void copyPlan(String SCID, String empiId, Date beginDate,
			Date endDate, String scip, String[] scpIDS) throws ServiceException {
		// 判断是否存在对应的档案
		String tableName = "";
		String businessType = "";
		if ("1".equals(scip)) {// 高血压
			tableName = MDC_HypertensionRecord;
			businessType = "1";
		}
		if ("2".equals(scip)) {// 糖尿病
			tableName = MDC_DiabetesRecord;
			businessType = "2";
		}
		if ("3".equals(scip)) {// 儿童
			tableName = CDH_HealthCard;
			businessType = "6";
		}
		if ("5".equals(scip)) {// 老年人
			tableName = MDC_OldPeopleRecord;
			businessType = "4";
		}
		if ("6".equals(scip)) {// 严重精神障碍患者
			tableName = PSY_PsychosisRecord;
			businessType = "10";
		}
		if (S.isNotEmpty(tableName)) {
			StringBuffer hqlWhere = new StringBuffer(
					" status='0' and empiId=:empiId");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("empiId", empiId);
			Long rn = 0L;
			try {
				rn = dao.doCount(tableName, hqlWhere.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,
						"查询传档是否存在失败！", e);
			}
			if (rn.longValue() > 1 && "4".equals(scip)) {
				throw new ServiceException(Constants.CODE_DATE_PASE_ERROR,
						"该名孕妇同时存在多条“未终止妊娠”的孕产妇档案，请处理后再签约！");
			}
			if (rn.longValue() == 1) {
				// 拷贝在签约有效期内的计划并生成计划任务
				String PHSQL = new StringBuffer(
						"select beginDate as beginDate,planDate as planDate,endDate as endDate from ")
						.append(PUB_VisitPlan)
						.append(" where planStatus='0' and empiId=:empiId and businessType=:businessType and beginDate>=:beginDate and endDate<=:endDate")
						.toString();
				Map<String, Object> pMap = new HashMap<String, Object>();
				pMap.put("empiId", empiId);
				pMap.put("businessType", businessType);
				pMap.put("beginDate", beginDate);
				pMap.put("endDate", endDate);
				List<Map<String, Object>> zpList = null;
				try {
					zpList = dao.doQuery(PHSQL, pMap);
				} catch (PersistentDataOperationException e) {
					throw new ServiceException(Constants.CODE_DATABASE_ERROR,
							"查询传档的随访计划失败！", e);
				}
				if (zpList != null && zpList.size() > 0) {
					if ("3".equals(scip)) {// 任务按月龄(EXTEND1)分段
						for (int i = 0, len = zpList.size(); i < len; i++) {

						}
						// TODO
					} else {
						// 先把在随访中可以完成的服务项目查询出来
						List<?> cnd1 = CNDHelper.createSimpleCnd("eq",
								"b.itemType", "s", "4");
						List<?> cnd2 = CNDHelper.createSimpleCnd("eq",
								"b.isBottom", "s", "y");// 是项目
						List<?> cnd3 = CNDHelper.createSimpleCnd("eq",
								"b.isOneWeekWork", "s", "n");// 不是首周内完成
						List<?> cnd4 = CNDHelper.createSimpleCnd("eq",
								"b.itemNature", "s", "1");// 性质公卫
						List<?> cnd5 = CNDHelper.createSimpleCnd("eq",
								"b.intendedPopulation", "s", scip);// 人群
						List<?> cnd6 = CNDHelper.createSimpleCnd("in",
								"a.SPID", "$", scpIDS);// 在签约包中
						List<?> cnd7 = CNDHelper.createArrayCnd("and", cnd1,
								cnd2);
						List<?> cnd8 = CNDHelper.createArrayCnd("and", cnd3,
								cnd4);
						List<?> cnd9 = CNDHelper.createArrayCnd("and", cnd5,
								cnd6);
						List<?> cnd10 = CNDHelper.createArrayCnd("and", cnd7,
								cnd8);
						List<?> cnd11 = CNDHelper.createArrayCnd("and", cnd9,
								cnd10);
						List<Map<String, Object>> spiList = null;
						try {
							spiList = dao.doList(cnd11, "b.itemCode asc",
									SCM_ServicePackageItems);
						} catch (PersistentDataOperationException e) {
							throw new ServiceException(
									Constants.CODE_DATABASE_ERROR,
									"获取签约服务包中可以随访中完成的项目失败！", e);
						}
						// copy计划
						for (int i = 0, len = zpList.size(); i < len; i++) {
							Map<String, Object> zpMap = zpList.get(i);
							Map<String, Object> rpMap = addSCPlan(SCID, empiId,
									zpMap);
							// 为计划增加可以完成的任务
							if (rpMap != null && rpMap.size() > 0) {
								String scPlanId = (String) rpMap.get("planId");// 签约计划编号
								addSCPlanTask(SCID, empiId, scPlanId, spiList);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @Description:生成孕产妇签约计划
	 * @param SCID
	 *            签约编号
	 * @param empiId
	 *            个人主索引
	 * @param beginDate
	 *            签约生效起始日期
	 * @param endDate
	 *            签约生效结束日期
	 * @param scip
	 *            签约人群
	 * @param scpIDS
	 *            签约所选包
	 * @throws ServiceException
	 * @author ChenXianRui 2017-11-17 下午5:00:02
	 * @Modify:
	 */
	private void genPWSCPlan(String SCID, String empiId, Date beginDate,
			Date endDate, String scip, String[] scpIDS) throws ServiceException {
		String tableName = MHC_PregnantRecord;
		String businessType = "8";
		StringBuffer hqlWhere = new StringBuffer(
				" status='0' and empiId=:empiId");
		hqlWhere.append(" and dateOfPrenatal>:beginDate");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		Calendar CalDOP = Calendar.getInstance();
		CalDOP.setTime(beginDate);
		CalDOP.add(Calendar.DATE, 42);
		parameters.put("beginDate", CalDOP.getTime());
		Long rn = 0L;
		try {
			rn = dao.doCount(tableName, hqlWhere.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"查询传档是否存在失败！", e);
		}
		if (rn.longValue() > 1 && "4".equals(scip)) {
			throw new ServiceException(Constants.CODE_DATE_PASE_ERROR,
					"该名孕妇同时存在多条“未终止妊娠”的孕产妇档案，请处理后再签约！");
		}
		if (rn.longValue() == 1) {
			// 拷贝在签约有效期内的计划并生成计划任务
			String PHSQL = new StringBuffer(
					"select beginDate as beginDate,planDate as planDate,endDate as endDate from ")
					.append(PUB_VisitPlan)
					.append(" where planStatus='0' and empiId=:empiId and businessType=:businessType and beginDate>=:beginDate and endDate<=:endDate")
					.toString();
			Map<String, Object> pMap = new HashMap<String, Object>();
			pMap.put("empiId", empiId);
			pMap.put("businessType", businessType);
			pMap.put("beginDate", beginDate);
			pMap.put("endDate", endDate);
			List<Map<String, Object>> zpList = null;
			try {
				zpList = dao.doQuery(PHSQL, pMap);
			} catch (PersistentDataOperationException e) {
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,
						"查询传档的随访计划失败！", e);
			}
			if (zpList != null && zpList.size() > 0) {
				if ("4".equals(scip)) {// 任务按孕周(EXTEND1)分段
					String parentCode = "08";
					for (int i = 0, len = zpList.size(); i < len; i++) {
						Map<String, Object> zpMap = zpList.get(i);
						Integer yzObj = (Integer) zpMap.get("extend1");
						if (yzObj != null) {
							int yz = yzObj.intValue();
							if (yz >= 16 && yz <= 20) {
								parentCode = "0802";
							}
							if (yz >= 21 && yz <= 24) {
								parentCode = "0803";
							}
							if (yz >= 28 && yz <= 36) {
								parentCode = "0804";
							}
							if (yz >= 37 && yz <= 40) {
								parentCode = "0805";
							}
						}
					}
					// TODO
				}
			}
		}
		// 孕产妇 随访后应
		// 增加两条签约计划，一条为终止妊娠日期+7（终止妊娠后一周内做产后访视），一条为终止妊娠日期+42(终止妊娠后42天访视)
		// 终止妊娠日期+7>签约生效日期 或 终止妊娠日期+42>签约生效日期 则需要增加计划
		// TODO
	}

	/**
	 * @Description:合并计划
	 * @param SCID
	 *            签约编号
	 * @param empiId
	 *            个人主索引
	 * @param beginDate
	 *            签约生效起始日期
	 * @param endDate
	 *            签约生效截止日期
	 * @param scip
	 *            签约所属于人群
	 * @param scpIDS
	 *            签约所选择的服务包
	 * @throws ServiceException
	 * @author ChenXianRui 2017-11-17 下午2:04:31
	 * @Modify:
	 */
	private void mergePlan(String SCID, String empiId, Date beginDate,
			Date endDate, String scip, String[] scpIDS) throws ServiceException {
		if (scip.indexOf("1") != -1 && scip.indexOf("2") != -1) {// 高血压、糖尿病
			if (scip.indexOf("5") != -1) {// 老年人

			} else if (scip.indexOf("4") != -1) {// 孕产妇

			} else if (scip.indexOf("6") != -1) {// 严重精神障碍患者
				// 一月一次

				// 三次一次

			} else {// 高血压、2糖尿病 都是三个月一次

			}
		} else if (scip.indexOf("5") != -1) {
			if (scip.indexOf("1") != -1) {// 老年人、高血压

			} else if (scip.indexOf("2") != -1) {// 老年人、糖尿病

			} else if (scip.indexOf("6") != -1) {// 老年人，严重精神障碍患者

			}
		} else if (scip.indexOf("4") != -1) {
			if (scip.indexOf("1") != -1) {// 孕产妇、高血压

			} else if (scip.indexOf("2") != -1) {// 孕产妇、糖尿病

			} else if (scip.indexOf("6") != -1) {// 孕产妇，严重精神障碍患者

			}
		} else if (scip.indexOf("6") != -1) {
			if (scip.indexOf("1") != -1) {// 严重精神障碍患者、高血压

			} else if (scip.indexOf("2") != -1) {// 严重精神障碍患者、糖尿病

			}
		}
	}

    public void doGetHisItems(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer("select a.FYXH as itemCode, a.FYMC as itemName , a.PYDM as pyCode , '22' as itemNature , '4' as itemType from GY_YLMX a left join GY_YLSF b on a.FYXH = b.FYXH");
		List<Map<String,Object>> hisItem = new ArrayList<Map<String, Object>>();
		hisItem = dao.doSqlQueryList(hql.toString(),null);
		hisItem = SchemaUtil.setDictionaryMessageForList(hisItem,req.get("schema").toString());
		res.put("body", hisItem);
    }

    //返回是否有个档 、 挂号记录
    public int doCheckEhrAGh(Map<String, Object> body) throws PersistentDataOperationException {
		int result = 0;
		Map<String , Object> para = new HashMap<String, Object>();
		para.put("empiId" , body.get("empiId"));
		Long l_ehr = dao.doCount(BSCHISEntryNames.EHR_HealthRecord , "empiId =:empiId" , para);
		para.put("jgid" , body.get("jgid"));
		//Long l_gh = dao.doCount("MS_BRDA a , MS_GHMX b" , "a.BRID = b.BRID and a.EMPIID =:empiId and b.JGID =:jgid and b.GHSJ = SYSDATE" ,para);
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) as TOTAL from MS_BRDA a , MS_GHMX b where a.BRID = b.BRID and a.EMPIID =:empiId and b.JGID =:jgid and b.GHSJ = SYSDATE");
		Map<String , Object> gh = dao.doSqlLoad(sb.toString() , para);
		int l_gh = Integer.parseInt(gh.get("TOTAL").toString());

		if(l_ehr == 0){
			if(l_gh == 0) {result = 0 ;}
			else {result = 2;}
		} else if(l_gh == 0){
			result = 1 ;
		}
		return result < 10 ? result : 10;
    }
}
