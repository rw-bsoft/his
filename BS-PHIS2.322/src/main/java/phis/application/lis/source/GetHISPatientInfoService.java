package phis.application.lis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.lis.source.rpc.HISException;
import ctd.util.context.Context;

/**
 *  病人信息查询
 * @description
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class GetHISPatientInfoService extends HISAbstractService {

	@Override
	public Map<String, Object> process(Map<String, Object> paramMap,
			Context paramContext) throws HISException {
		Map<String, Object> res = new HashMap<String, Object>();
		
		/**验证数据完整性*/
		if (!checkData(paramMap, res)) {
			return res;
		}
		
		PHISPatientModel dam = new PHISPatientModel(paramContext);
		try {
			Map<String, Object> result = dam.getPersonData(paramMap);
			res.put("body",result);
		} catch (ModelOperationException e) {
			throw new HISException(e.getMessage(), e);
		}
		return res;
	}

	/**必要参数验证*/
	protected List<String> getNotNullFields() {
		List<String> list = new ArrayList<String>();
		list.add("al_yjlb");
		list.add("al_brsy");
		return list;
	}
}
