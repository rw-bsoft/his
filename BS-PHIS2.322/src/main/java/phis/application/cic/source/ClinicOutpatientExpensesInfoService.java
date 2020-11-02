package phis.application.cic.source;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 医技项目取消Service层
 * 
 * @author bsoft
 * 
 */
public class ClinicOutpatientExpensesInfoService extends AbstractActionService
		implements DAOSupportable {

	/**
	 * 家床业务查询病人列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryFYXXInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicOutpatientExpensesInfoModule coem = new ClinicOutpatientExpensesInfoModule(
				dao);
		try {
			List<Map<String, Object>> dataResult = coem.doQueryFYXXInfo(req, res, ctx);
			res.put("body", dataResult);
			DecimalFormat df = new DecimalFormat("######0.00");
			double CFHJ = 0.00;
			double JCHJ = 0.00;
			if(dataResult.size()>0){
				for(Map<String, Object> data : dataResult){
					if(data.get("CFJE")!=null && !"".equals(data.get("CFJE"))){
						CFHJ += Double.parseDouble(data.get("CFJE")+"");
					}
					if(data.get("JCJE")!=null && !"".equals(data.get("JCJE"))){
						JCHJ += Double.parseDouble(data.get("JCJE")+"");
					}
				}
			}
			res.put("totalCount", dataResult.size());
			res.put("totalcfje", df.format(CFHJ));
			res.put("totaljcje", df.format(JCHJ));
			res.put("totalje", df.format(CFHJ+JCHJ));
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doQueryFYMXInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicOutpatientExpensesInfoModule coem = new ClinicOutpatientExpensesInfoModule(
				dao);
		try {
			res.put("body", coem.doQueryFYMXInfo(req, res, ctx));
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
