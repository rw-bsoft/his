/**
 * @(#)ZHTJPeopleInfoService.java Created on 2014-3-7 下午1:56:49
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.phsa.source;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.dao.SimpleDAO;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class PHSAPeopleInfoService extends AbstractActionService implements
		DAOSupportable {
	public void doQueryPeopleInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schemaId = (String) req.get("schema");
		String BRLB = (String) req.get("BRLB");
		List queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		PHSAPeopleInfoModel zpim = new PHSAPeopleInfoModel(dao);
		Map<String, Object> result;
		try {
			result = zpim.queryPeopleInfo(BRLB, queryCnd,
					queryCndsType, sortInfo, pageSize, pageNo,schemaId);
			res.putAll(result);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	
	public void doCheckHasHealthRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		PHSAPeopleInfoModel zpim = new PHSAPeopleInfoModel(dao);
		try {
			String flag=zpim.checkHasHealthRecord(empiId);
			res.put("flag",flag);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	
	public void doGetMZZYXX(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		PHSAPeopleInfoModel zpim = new PHSAPeopleInfoModel(dao);
		try {
			zpim.getMZZYXX(empiId,res);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	
	
}
