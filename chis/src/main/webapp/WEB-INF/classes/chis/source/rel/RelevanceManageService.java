package chis.source.rel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import chis.source.util.SendDictionaryReloadSynMsg;

import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;

public class RelevanceManageService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(RelevanceManageService.class);

	/**
	 * 根据助理医生加载关联家庭医生
	 * 
	 * @Description:
	 * @param req
	 * @return
	 * @throws ServiceException
	 * @author YuBo 2014-4-4 下午2:57:11
	 * @return
	 * @Modify:
	 */
	@RpcService
	public void doLoadRelevanceManageDoctor(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody=(Map<String, Object>) req.get("body");
		String fda = (String) reqBody.get("fda");
		String manageUnitId = (String) reqBody.get("manageUnitId");
		RelevanceManageModule rmm = new RelevanceManageModule(dao);
		List<Map<String, Object>> records;
		List<Map<String, Object>> selectedList;
		try {
			records = rmm.loadRelevanceManageDoctor(fda, manageUnitId);
			selectedList = rmm.loadSelectedRelevanceManageRecords(fda);
		} catch (ModelDataOperationException e) {
			logger.error("loadRelevanceManageDoctor is fail", e);
			throw new ServiceException(e);
		}
		records = SchemaUtil.setDictionaryMessageForList(records,
				REL_RelevanceDoctor_list);
		for (int i = 0; i < records.size(); i++) {
			Map<String, Object> record = records.get(i);
			String fd1 = (String) record.get("fd");
			for (int j = 0; j < selectedList.size(); j++) {
				Map<String, Object> selectRecord = selectedList.get(j);
				String fd2 = (String) selectRecord.get("fd");
				if (fd1.equals(fd2)) {
					record.put("selected", true);
					break;
				}
			}
		}
		res.put("body", records);
	}

	@SuppressWarnings("unchecked")
	@RpcService
	public void doSaveRelevanceManageDoctor(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody=(Map<String, Object>) req.get("body");
		String fda = (String) reqBody.get("fda");
		List<Map<String, Object>> list = (List<Map<String, Object>>) reqBody
				.get("records");

		RelevanceManageModule rmm = new RelevanceManageModule(dao);
		try {
			rmm.deleteRecordByFda(fda);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> fd = list.get(i);
				Map<String, Object> saveData = new HashMap<String, Object>();
				saveData.put("fda", fda);
				saveData.put("fd", fd.get("fd"));
				saveData.put("fd_text", fd.get("fd_text"));
				rmm.saveRelevanceManageDoctor(saveData);
			}
			DictionaryController.instance().reload(
					"gp.dictionary.FDARelevanceDoctor");
			SendDictionaryReloadSynMsg.instance().sendSynMsg(
					"gp.dictionary.FDARelevanceDoctor");
		} catch (ModelDataOperationException e) {
			logger.error("saveRelevanceManageDoctor is fail", e);
			throw new ServiceException(e);
		} finally {
		}
	}
	
	/**
	 * 
	 * @Description:更新机构字典
	 * @param req
	 * @return
	 * @throws ServiceException
	 * @author ChenXianRui 2014-5-16 下午1:25:35
	 * @Modify:
	 */	
	@RpcService
	public void doUpdateJgzd(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException{
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			RelevanceManageModule cp=new RelevanceManageModule(dao);
			body=cp.updateJgzd(req);
		} catch (Exception e) {
			logger.error("Failed to get dataInfo .", e);
			throw new ServiceException(e);
		}
		res.put("body", body);
	}
	//保存责任医生助理关联的责任医生
	public void doSaveResponsibleDoctor(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException{
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			RelevanceManageModule cp=new RelevanceManageModule(dao);
			cp.saveResponsibleDoctor(req,res);
		} catch (Exception e) {
			logger.error("Failed to get dataInfo .", e);
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

}
