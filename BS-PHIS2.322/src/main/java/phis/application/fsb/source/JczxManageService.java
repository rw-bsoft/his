package phis.application.fsb.source;

import java.util.Map;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

public class JczxManageService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 家床申请 保存
	 * */
	public void doSaveJcbrsq(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ExpException, PersistentDataOperationException {
		JczxManageModel m = new JczxManageModel(dao);
		m.doSaveJcbrsq(req, res, dao, ctx);
	}

	/**
	 * 家床申请 列表查询
	 * */
	public void doLoadJcsqList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ExpException, PersistentDataOperationException {
		JczxManageModel m = new JczxManageModel(dao);
		m.doLoadJcsqList(req, res, dao, ctx);
	}

	/**
	 * 查询病人档案 根据条件
	 * @throws ModelDataOperationException 
	 * */
	public void doSelectBrdaByMzhm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ExpException, PersistentDataOperationException, ModelDataOperationException {
		JczxManageModel m = new JczxManageModel(dao);
		m.doSelectBrdaByMzhm(req, res, dao, ctx);
	}

	/**
	 * 家床重复申请判断
	 * */
	public void doCheckRepetition(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		JczxManageModel m = new JczxManageModel(dao);
		try {
			m.doCheckRepetition(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doSaveQueryJCHM(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		JczxManageModel m = new JczxManageModel(dao);
		try {
			m.doSaveQueryJCHM(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public void doSelectBrsqByField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		JczxManageModel m = new JczxManageModel(dao);
		try {
			m.doSelectBrsqByField(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doSaveBrdj(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		JczxManageModel m = new JczxManageModel(dao);
		try {
			m.doSaveBrdj(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doSelectZyzdjlByField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ExpException, ModelDataOperationException {
		JczxManageModel m = new JczxManageModel(dao);
		m.doSelectZyzdjlByField(req, res, dao, ctx);
	}
	
	public void doGetExistJCBH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ExpException, ModelDataOperationException {
		JczxManageModel m = new JczxManageModel(dao);
		m.doGetExistJCBH(req, res, dao, ctx);
	}
}
