/*
 * @(#)AbstractService2.java Created on 2011-12-15 上午11:51:28
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.service;

import java.util.*;

import org.codehaus.jackson.map.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.log.VindicateLogService;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.service.adapter.ServiceAdaptItem;

import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public abstract class AbstractActionService extends AbstractService implements
		ActionSupportable, InitializingBean {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractActionService.class);

	private ActionExecutor actionExecutor = null;
	private List<String> transactedActions;
	private List<String> noDBActions;
	private List<Observer> observers;
	// 维护日志
	protected VindicateLogService vLogService = null;

	/**
	 * @return
	 */
	public List<String> getTransactedActions() {
		return transactedActions;
	}

	/**
	 * 不需要操作数据库或者不需要传递Session的Action。
	 * 
	 * @return
	 */
	public List<String> getNoDBActions() {
		return noDBActions;
	}

	/**
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ServiceException
	 */
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		String action = (String) req.get(P_SERVICE_ACTION);
		if (action == null || action.trim().length() == 0) {
			logger.error("Mandatory property missing: [{}].", P_SERVICE_ACTION);
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "非法的请求，" + P_SERVICE_ACTION + "未找到。");
			return;
		}
		actionExecutor.execute(req, res, ctx, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if (observers != null && observers.size() > 0) {
			for (Observer o : observers) {
				this.addObserver(o);
			}
		}
	}

	public ActionExecutor getActionExecutor() {
		return actionExecutor;
	}

	public void setActionExecutor(ActionExecutor actionExecutor) {
		this.actionExecutor = actionExecutor;
	}

	public void setTransactedActions(List<String> transactedActions) {
		this.transactedActions = transactedActions;
	}

	public void setNoDBActions(List<String> noDBActions) {
		this.noDBActions = noDBActions;
	}

	/**
	 * 获得vLogService
	 * 
	 * @return the vLogService
	 */
	public VindicateLogService getvLogService() {
		return vLogService;
	}

	/**
	 * 设置vLogService
	 * 
	 * @param vLogService
	 *            the vLogService to set
	 */
	public void setvLogService(VindicateLogService vLogService) {
		this.vLogService = vLogService;
	}

	/**
	 * 
	 * @Description:cnd表达式分页查询--列表中返回age[年龄]，前提是有birthday[出生日期字段]
	 * 特殊说明：列表schema名称为：原始schema名称+"ListView"
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-4-24 下午2:17:54
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSimpleQueryPAList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		if (S.isEmpty(schema)) {
			throw new ServiceException(Constants.CODE_BUSINESS_DATA_NULL,
					"没有指定表或scheam!");
		}
		String entryName = schema;
		if(schema.indexOf("ListView") > -1){
			entryName = schema.substring(0,schema.indexOf("ListView"));
		}
		List<?> queryCnd = null;
		if (req.get("cnd") instanceof List) {
			queryCnd = (List<?>) req.get("cnd");
		} else if (req.get("cnd") instanceof String) {
			try {
				queryCnd = CNDHelper.toListCnd((String) req.get("cnd"));
			} catch (ExpException e) {
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,
						"查询表达式转换失败！", e);
			}
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		try {
			Map<String, Object> rsMap = dao.doList(queryCnd, sortInfo,
					entryName, pageNo, pageSize, queryCndsType);
			List<Map<String, Object>> rsList = (List<Map<String, Object>>) rsMap
					.get("body");
			for (Map<String, Object> map : rsList) {
				Date birthday = (Date) map.get("birthday");
				if (birthday != null) {
					map.put("age", BSCHISUtil.calculateAge(birthday, null));
				}
			}
			res.put("totalCount", rsMap.get("totalCount"));
			res.put("body", rsList);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"数据加载失败！", e);
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
	}

	public void finishSCServiceTask(String empiId, String chisService, String awm, BaseDAO dao) throws ServiceException {
		//调用 服务 与 项目 适配器 获取项目代码
		String itemCode = ServiceAdaptItem.getItemCode(chisService, awm, dao);
		if (S.isNotEmpty(itemCode)) {
			//给 签约项目 打上 “完成” 标识
			this.finishSCServiceTask(empiId, itemCode, dao);
		}
	}

	public void finishSCServiceTask(String empiId, String chisService, String awm, String intendedpopulation, BaseDAO dao) throws ServiceException {
		//调用 服务 与 项目 适配器 获取项目代码
		String itemCode = ServiceAdaptItem.getItemCode(chisService, awm, intendedpopulation, dao);
		if (S.isNotEmpty(itemCode)) {
			//给 签约项目 打上 “完成” 标识
			this.finishSCServiceTask(empiId, itemCode, dao);
		}
	}

	public void finishSCServiceTask(String empiId, String itemCode, BaseDAO dao) throws ServiceException {
		if (S.isEmpty(itemCode) || S.isEmpty(empiId)) {
			throw new ServiceException(Constants.CODE_BUSINESS_DATA_NULL, "必须业务参数为空！");
		}
		String HQL = new StringBuffer("select SCID as SCID from ").append(SCM_SignContractRecord)
				.append(" where signFlag='1' and favoreeEmpiId=:empiId and endDate>=:curDate").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("curDate", Calendar.getInstance().getTime());
		int curYear = Calendar.getInstance().get(Calendar.YEAR);
		List<Map<String, Object>> scrList = null;
		try {
			scrList = dao.doQuery(HQL, pMap);
		} catch (PersistentDataOperationException e) {
			logger.error("依据个人主索引获取签约记录失败！", e);
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"依据个人主索引获取签约记录失败！", e);
		}
		if (scrList != null && scrList.size() > 0) {
			Map<String, Object> scrMap = scrList.get(0);
			String SCID = (String) (scrMap.get("SCID")+"");
			//安包分组，得出有那些包中有此项目
				String gHQL = new StringBuffer("select SPID as SPID from ").append(SCM_ServiceContractPlanTask)
					.append(" where status='0' and empiId=:empiId and SCID=:SCID and taskCode=:itemCode and year=:year")
					.append(" group by SPID").toString();
			List<Map<String, Object>> gList = null;
			Map<String, Object> sipMap = new HashMap<String, Object>();
			sipMap.put("empiId", empiId);
			sipMap.put("SCID", Long.valueOf((String)SCID).longValue());
			sipMap.put("itemCode", itemCode);
			sipMap.put("year", curYear);
			try {
				gList = dao.doQuery(gHQL, sipMap);
			} catch (PersistentDataOperationException e) {
				logger.error("安包分组,获取服务任务记录失败！", e);
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,
						"安包分组,获取服务任务记录失败！", e);
			}
			if (gList != null && gList.size() > 0) {
				for (Map<String, Object> gMap : gList) {
					String SPID = (String) gMap.get("SPID");
					sipMap.put("SPID", SPID);
					sipMap.put("year", curYear);
					String getSIHQL = new StringBuffer("select taskId as taskId from ").append(SCM_ServiceContractPlanTask)
							.append(" where status='0' and empiId=:empiId and SCID=:SCID and taskCode=:itemCode and SPID=:SPID and year=:year").toString();
					List<Map<String, Object>> siList = null;
					try {
						siList = dao.doQuery(getSIHQL, sipMap);
					} catch (PersistentDataOperationException e) {
						logger.error("获取服务任务记录失败！", e);
						throw new ServiceException(Constants.CODE_DATABASE_ERROR,
								"获取服务任务记录失败！", e);
					}
					if (siList != null && siList.size() > 0) {
						Map<String, Object> siMap = siList.get(0);
						String taskId = (String) siMap.get("taskId");
						//todo 通过log 辅助标记该服务是否已经被完成
//						String uHQL = new StringBuffer("update ").append(SCM_ServiceContractPlanTask).append(" set status='1'")
//								.append(" where taskId=:taskId").toString();
						String uHQL = new StringBuffer("update ").append(" SCM_INCREASEITEMS ").append(" set SERVICETIMES = SERVICETIMES +1")
								.append(" where SCID =:SCID and TASKCODE =:TASKCODE ").toString();
						Map<String, Object> upMap = new HashMap<String, Object>();

						try {
							Map<String , Object> record = dao.doLoad(SCM_ServiceContractPlanTask , taskId);
							upMap.put("SCID", record.get("SCID"));
							upMap.put("TASKCODE", record.get("taskCode"));
							dao.doUpdate(uHQL, upMap);
//                            Map<String , Object> log1 = new HashMap<String, Object>();
//                            log1.put("empiid", empiId);
//                            Map<String , Object> log2 = new HashMap<String, Object>();
//                            Map result = dao.doSave("create", BSCHISEntryNames.SCM_LOG01, log1 , false);
//                            log2.put("log1Id" , result.get("log1Id"));
//                            log2.put("taskId" , taskId);
							//由于现有业务，以下公卫服务的记录全写死，根据业务改动
//                            log2.put("serviceTimes" , 0);
//                            log2.put("costTimes" , 1);
//                            log2.put("unitPrice" , 0d);
//                            log2.put("totPrice" , 0d);
//                            dao.doSave("create", BSCHISEntryNames.SCM_LOG02, log2 , false);
						} catch (PersistentDataOperationException e) {
							logger.error("标识服务任务完成失败！", e);
							throw new ServiceException(Constants.CODE_DATABASE_ERROR,
									"标识服务任务完成失败！", e);
						}
					}
				}
			}
		}
	}
}
