package phis.application.lis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.lis.source.rpc.HISException;
import ctd.util.context.Context;

/**
 *  获取费用信息
 * @description
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class QueryHISFeeInfoService extends HISAbstractService {

	@Override
	public Map<String, Object> process(Map<String, Object> body,
			Context ctx) throws HISException {
		Map<String, Object> res = new HashMap<String, Object>();

		/**验证数据完整性*/
		if (!checkData(body, res)) {
			return res;
		}
		
		PHISCommonModel model = new PHISCommonModel(ctx);
		
		try {
			Map<String, Object> result = model.getzfbl(body);
			res.put("body",result);
		} catch (ModelOperationException e) {
			e.printStackTrace();
			throw new HISException(e.getMessage(), e);
		}
		return res;
	}

	/**必要参数验证*/
	protected List<String> getNotNullFields() {
		List<String> list = new ArrayList<String>();
		list.add("TYPE");
		list.add("BRXZ");
		list.add("FYXH");
		list.add("FYGB");
		return list;
	}
}
