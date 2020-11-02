package chis.source.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.dic.YesNo;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import ctd.dao.support.QueryContext;
import ctd.dao.support.QueryResult;
import ctd.net.rpc.Client;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * 查询网格地址
 * 
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class AreaGridService extends AbstractActionService implements
		DAOSupportable {

	@SuppressWarnings("unchecked")
	protected void doQueryAreaGridByRegionCode(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String parentCode = (String) body.get("regionCode");
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "parentCode", "s",
				parentCode);
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		QueryContext qc = new QueryContext();
		try {
			Object[] paras = new Object[] { "area", cnd, qc };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			res.put("body", qr.getItems());
		} catch (Exception e1) {
			throw new ServiceException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务查询关联子节点失败", e1);
		}
	}

	@SuppressWarnings("unchecked")
	protected void doQueryAreaGridByPym(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String pyCode = (String) body.get("pyCode");
		List<?> cnd1 = CNDHelper.createSimpleCnd("like", "pyCode", "s", pyCode
				+ "%");
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "isBottom", "s",
				YesNo.YES);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		HashMap<String, Object> header = BSCHISUtil.getRpcHeader();
		QueryContext qc = new QueryContext();
		try {
			Object[] paras = new Object[] { "area", cnd, qc };
			QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) Client.rpcInvoke(
					AppContextHolder.getConfigServiceId("daoService"), "find",
					paras, header);
			res.put("body", qr.getItems());
		} catch (Exception e1) {
			throw new ServiceException(
					ServiceCode.CODE_DATABASE_ERROR, "RPC服务查询关联子节点失败", e1);
		}
		
	}
}
