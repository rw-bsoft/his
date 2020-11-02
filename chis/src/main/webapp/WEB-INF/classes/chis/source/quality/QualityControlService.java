package chis.source.quality;

import java.util.Map;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.rvc.RetiredVeteranCadresModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.visitplan.VisitPlanCreator;

public class QualityControlService  extends AbstractActionService implements DAOSupportable {

	private VisitPlanCreator visitPlanCreator;
	/**
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}
	
	/**
	 * 
	 * @param visitPlanCreator
	 *           xuzb
	 *           质控
	 */
	@SuppressWarnings("unchecked")
	protected void doInitQualityModel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String ID =  body.get("ID")+"";
		res.put("body", body);
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.getQualityModel(ID);
			if (record != null) {
				body.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @param visitPlanCreator
	 *           xuzb
	 *         质控QUALITY_ZK_GXSD进行保存
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.onSaveList(body);
			if (record != null) {
				body.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 *  
	 *           xuzb
	 *         质控高血压月份list
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveGxyListYf(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.saveGxyListYf(body);
			if (record != null) {
				res.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
	/**
	 *           xuzb
	 *         质控高血压 list3
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveGxyList3(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.saveGxyList3(body);
			if (record != null) {
				res.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
	/**
	 *           xuzb
	 *         质控高血压初始化
	 */
	@SuppressWarnings("unchecked")
	protected void doCreateZk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.createZk(body);
			if (record != null) {
				res.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
	/**
	 *           xuzb
	 *         保存form
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveFormZk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.saveFormZk(body);
			if (record != null) {
				res.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
	/**
	 *           xuzb
	 *         质控高血压报告对照list
	 */
	@SuppressWarnings("unchecked")
	protected void doCreatGxyDzList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.creatGxyDzList(body);
			if (record != null) {
				res.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}/**
	 *           xuzb
	 *         质控高血压报告对照form
	 */
	@SuppressWarnings("unchecked")
	protected void doCreatGxyDzForm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.creatGxyDzform(body);
			if (record != null) {
				res.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
	/**deleteGxyZkPf
	 *           xuzb
	 *         质控高血压报告对照form
	 */
	@SuppressWarnings("unchecked")
	protected void doCreatGxyZkPf(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.creatGxyZkPf(body);
			if (record != null) {
				res.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
	/**
	 *           xuzb
	 *         质控高血压 delete
	 */
	@SuppressWarnings("unchecked")
	protected void doDeleteGxyZkPf(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.deleteGxyZkPf(body);
			if (record != null) {
				res.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 *  
	 *           xuzb
	 *         质控糖尿病月份list
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveTnbListYf(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.saveTnbListYf(body);
			if (record != null) {
				res.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
	/**
	 *           xuzb
	 *         质控糖尿病 list3
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveTnbList3(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			QualityControlModel rvcModule = new QualityControlModel(dao);
			Map<String, Object> record = rvcModule.saveTnbList3(body);
			if (record != null) {
				res.putAll(record);
			}
		} catch (ModelDataOperationException e) {
		//	logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
}
