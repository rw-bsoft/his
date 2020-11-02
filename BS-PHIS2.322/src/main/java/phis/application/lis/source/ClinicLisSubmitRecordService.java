package phis.application.lis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.lis.source.rpc.HISException;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 门诊医技提交
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class ClinicLisSubmitRecordService extends HISAbstractService {

	protected Logger logger = LoggerFactory
			.getLogger(ClinicLisSubmitRecordService.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> process(Map<String, Object> paramMap,
			Context paramContext) throws HISException {
		Map<String, Object> res = new HashMap<String, Object>();

		/**验证数据完整性*/
		if (!checkData(paramMap, res)) {
			return res;
		}

		List<Object> yjxxs = (List<Object>) paramMap.get("list");
		long sqdhId = (Long) paramMap.get("sqdhId");
		String djly = (String) paramMap.get("djly");
		PHISLisBillOperationModel lbm = new PHISLisBillOperationModel(
				paramContext);
		try {
			Map<String, Object> result = lbm.clinicLisRecordSubmit(yjxxs,
					sqdhId, djly,paramContext);
			res.putAll(result);
		} catch (ModelOperationException e) {
			logger.error("save lis record failed!,error:"+e.getMessage());
			res.put("x-response-code", ServiceCode.CODE_ERROR);
			res.put("x-response-msg", "保存失败");
//			return res;
			throw new HISException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error("save lis record failed!,error:"+e.getMessage());
			res.put("x-response-code", ServiceCode.CODE_ERROR);
			res.put("x-response-msg", "保存失败");
//			return res;
			throw new HISException(e.getMessage(), e);
		} catch (PersistentDataOperationException e) {
			logger.error("save lis record failed!,error:"+e.getMessage());
			res.put("x-response-code", ServiceCode.CODE_ERROR);
			res.put("x-response-msg", "保存失败");
//			return res;
			throw new HISException(e.getMessage(), e);
		} catch (ModelDataOperationException e) {
			logger.error("save lis record failed!,error:"+e.getMessage());
			res.put("x-response-code", ServiceCode.CODE_ERROR);
			res.put("x-response-msg", "保存失败");
//			return res;
			throw new HISException(e.getMessage(), e);
		}
		return res;
	}
	
	/**必要参数验证*/
	protected List<String> getNotNullFields() {
		List<String> list = new ArrayList<String>();
		list.add("list");
		list.add("sqdhId");
		list.add("djly");
		return list;
	}
}
