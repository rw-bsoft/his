package phis.application.lis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.lis.source.rpc.HISException;
import ctd.util.context.Context;

/**
 * @description 住院医嘱提交
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class InhospLisSubmitRecordService extends HISAbstractService {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> process(Map<String, Object> paramMap,
			Context paramContext) throws HISException {
		Map<String, Object> res = new HashMap<String, Object>();
		
		/**验证数据完整性*/
		if (!checkData(paramMap, res)) {
			return res;
		}
		
		List yjxxs = (List) paramMap.get("yzxxs");
		long sqdhId = (Long)paramMap.get("sqdhId");
		String djly = (String) paramMap.get("djly");

		PHISLisBillOperationModel lbm = new PHISLisBillOperationModel(paramContext);
		try {
			Map<String, Object> result = lbm.inhospLisRecordSubmit(yjxxs,
					sqdhId, djly);
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
		list.add("yzxxs");
		list.add("sqdhId");
		list.add("djly");
		return list;
	}
}
