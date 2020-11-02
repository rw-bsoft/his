/**
 * @(#)PlanTypeManage.java Created on 2010-12-20 下午02:23:43
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.conf;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.visitplan.VisitPlanModel;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class PlanTypeManageService extends AbstractActionService implements
		DAOSupportable {

	private static Logger logger = LoggerFactory
			.getLogger(OldPeopleConfigManageService.class);

	public final static String planTypeDic = "planTypeDic";

	/**
	 * 保存计划类型设置相关记录
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req
				.get("body");
		String op = (String) req.get("op");
		if (body == null || body.size() < 1) {
			logger.error("body  is missing!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请求数据丢失！");
			return;
		}
		VisitPlanModel vpm = new VisitPlanModel(dao);
			try {
				Map<String,Object> data = vpm.savePlanType(op, body);
				res.put("body", data);
			} catch (Exception e) {
				logger.error("Failed to insert plan type records!");
				throw new ServiceException(e);
			}
		// ** 重载计划类型字典
			DictionaryController dic = DictionaryController.instance();
		dic.reload(planTypeDic);
	}

}
