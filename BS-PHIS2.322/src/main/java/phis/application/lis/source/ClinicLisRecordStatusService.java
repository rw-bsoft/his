package phis.application.lis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.lis.source.rpc.HISException;
import ctd.util.context.Context;

/**
 * @description 门诊医技状态查询
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class ClinicLisRecordStatusService extends HISAbstractService {

	@Override
	public Map<String, Object> process(Map<String, Object> paramMap,
			Context paramContext) throws HISException {
		Map<String, Object> res = new HashMap<String, Object>();
		
		/**验证数据完整性*/
		if (!checkData(paramMap, res)) {
			return res;
		}
		
		long sqid = Long.parseLong(paramMap.get("sqid")+"");
		String jgid = (String)paramMap.get("jgid");
		PHISLisBillOperationModel lbm = new PHISLisBillOperationModel(paramContext);
		try {
			Map<String, Object> result = lbm.clinicLisRecordStatus(sqid,jgid);
			res.putAll(result);
		} catch (ModelOperationException e) {
			throw new HISException(e.getMessage(), e);
		}
		return res;
	}

	/**必要参数验证*/
	protected List<String> getNotNullFields() {
		List<String> list = new ArrayList<String>();
		list.add("sqid");
		list.add("jgid");
		return list;
	}
}
