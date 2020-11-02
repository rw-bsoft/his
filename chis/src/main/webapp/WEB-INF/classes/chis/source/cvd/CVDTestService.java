/**
 * @(#)CVDTestService.java Created on 9:30:50 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.cvd;

import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yao zhenhua</a>
 */
public class CVDTestService extends AbstractActionService implements
		DAOSupportable {

	private static final Log logger = LogFactory.getLog(CVDTestService.class);

	@SuppressWarnings("unchecked")
	protected void doSaveCVDTest(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String inquireId = (String) body.get("inquireId");
		List<Map<String, Object>> data = (List<Map<String, Object>>) body
				.get("data");
		String op = (String) req.get("op");
		CVDServiceModel csm = new CVDServiceModel(dao);
		try {
			if (op.equalsIgnoreCase("update")) {
				csm.deleteCVDTest(inquireId);
			}
			StringBuffer answer = new StringBuffer();
			for (int i = 0; i < data.size(); i++) {
				Map<String, Object> object = (Map<String, Object>) data.get(i);
				Object  testResult = object.get("testResult");
				if (testResult != null && !testResult.toString().equals("")
						&& object.get("testResult")
								.equals(object.get("result"))) {
					answer.append("1");
				} else {
					answer.append("0");
				}
				object.put("empiId", body.get("empiId"));
				object.put("phrId", body.get("phrId"));
				object.put("inquireId", body.get("inquireId"));
				csm.saveCVDTest("create", object);
			}
			csm.updateAssessRegisterAnswer(inquireId, answer.toString());
		} catch (Exception e) {
			logger.error("failed to save user test message.", e);
			res.put(Service.RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(Service.RES_MESSAGE, "用户疾病知识测验列表保存失败!");
			throw new ServiceException(e);
		}

	}

}
