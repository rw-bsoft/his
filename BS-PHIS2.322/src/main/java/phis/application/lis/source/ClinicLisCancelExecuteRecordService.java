package phis.application.lis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.lis.source.rpc.HISException;
import ctd.util.context.Context;

/**
 * @description 门诊医技取消执行
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class ClinicLisCancelExecuteRecordService extends HISAbstractService {

	@Override
	public Map<String, Object> process(Map<String, Object> paramMap,
			Context paramContext) throws HISException {
		Map<String, Object> res = new HashMap<String, Object>();
		
		/**验证数据完整性*/
		if (!checkData(paramMap, res)) {
			return res;
		}
		
		List yjxhs = (List) paramMap.get("list");
		PHISLisBillOperationModel lbm = new PHISLisBillOperationModel(paramContext);
		Map<String, Object> result;
		try {
			result = lbm.clinicLisCancelExecuteRecord(yjxhs);
			res.putAll(result);
		} catch (ModelOperationException e) {
			e.printStackTrace();
			throw new HISException(e.getMessage(), e);
		}
		return res;
	}

	/**必要参数验证*/
	protected List<String> getNotNullFields() {
		List<String> list = new ArrayList<String>();
		list.add("list");
		return list;
	}
}
