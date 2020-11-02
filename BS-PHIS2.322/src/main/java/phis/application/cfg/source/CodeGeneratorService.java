/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.controller.CharacterEncodingController;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 收费项目维护费用信息Service
 * 
 * @author zhangyq 2012.05
 */
public class CodeGeneratorService extends AbstractActionService implements
		DAOSupportable {

	@SuppressWarnings("unchecked")
	public void doGetCode(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, String> retValue = new HashMap<String, String>();
		List<String> codeTypes = (List<String>) ((Map<String, Object>) req
				.get("body")).get("codeType");
		String value = (String) ((Map<String, Object>) req.get("body"))
				.get("value");
		for (String codeType : codeTypes) {
			// if ("py".equals(codeType)) {
			// retValue.put(codeType,PyConverter.getFirstLetter(value).toUpperCase());
			// } else {
			retValue.put(
					codeType,
					CharacterEncodingController.getCode(value, codeType
							+ "Code", ctx));
			// }
		}
		res.put("body", retValue);
		res.put("code", 200);
		res.put("msg", "Success");
	}

}
