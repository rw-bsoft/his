package phis.application.cfg.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;



import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigDoseFrequencyService extends AbstractActionService implements
DAOSupportable{
	protected Logger logger = LoggerFactory.getLogger(ConfigDoseFrequencyService.class);
	@SuppressWarnings("unchecked")
	public void doSaveCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException, ValidateException{
		ConfigDoseFrequencyModel cdfm = new ConfigDoseFrequencyModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		cdfm.doSaveCommit(body,res, dao, ctx);
	}
	
	public void doListQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx)throws ServiceException {
		ConfigDoseFrequencyModel cdfm = new ConfigDoseFrequencyModel(dao);
		try {
			cdfm.doListQuery(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("频次列表查询失败！",e);
		}
	}
	
	public void doMxquery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx)throws ServiceException {
		ConfigDoseFrequencyModel cdfm = new ConfigDoseFrequencyModel(dao);
		try {
			cdfm.doMxquery(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("频次列表查询失败！",e);
		}
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public void doRemoveFrequency(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		    List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		    String PCBM = body.get(0).get("PCBM")+"" ;
		    
		    String sql_BQYZ = "SYPC=:PCBM ";
		    String sql_CF02 = "YPYF=:PCBM ";
		    
		    Map<String, Object> parameters = new HashMap<String, Object>();
		    parameters.put("PCBM", PCBM);
		    
			try {
				Long l_YZ = dao.doCount("ZY_BQYZ",sql_BQYZ, parameters);
				if (l_YZ > 0) {
					throw new RuntimeException("频次在使用，不能删除");
				}
				Long l_CF = dao.doCount("MS_CF02",sql_CF02, parameters);
				if (l_CF > 0) {
					throw new RuntimeException("频次在使用，不能删除");
				}
				dao.doUpdate("delete from GY_SYPC where PCBM='" + PCBM + "'", null);
			} catch (PersistentDataOperationException e) {
				throw new ServiceException("修改失败！", e);
			}
			res.put(RES_CODE, ServiceCode.CODE_OK);
			res.put(RES_MESSAGE, "Success");
	}
}
