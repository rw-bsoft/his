package phis.application.reg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.util.context.Context;

public class DepartmentSchedulingService extends AbstractActionService implements
DAOSupportable{
	protected Logger logger = LoggerFactory.getLogger(DepartmentSchedulingService.class);
	public void doGetModelDataOperation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		DepartmentSchedulingModel dsm = new DepartmentSchedulingModel(dao);
			dsm.doGetModelDataOperation(req, res, ctx);
	}
	//科室排班保存
	public void doSaveDepartmentScheduling(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
		throws ModelDataOperationException {
		DepartmentSchedulingModel dsm = new DepartmentSchedulingModel(dao);
		dsm.doSaveDepartmentScheduling(req,res,dao,ctx);
	}
}
