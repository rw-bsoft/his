package phis.application.lis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.lis.source.rpc.HISException;
import phis.source.ModelDataOperationException;
import ctd.util.context.Context;

/**
 * @description LIS获取表自增主键(只适用于单主键)
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class GetHISTableNewPrimaryKeyService extends HISAbstractService {

	@Override
	public Map<String, Object> process(Map<String, Object> paramMap,
			Context paramContext) throws HISException {
		Map<String, Object> res = new HashMap<String, Object>();
		
		/**验证数据完整性*/
		if (!checkData(paramMap, res)) {
			return res;
		}
		
//		PHISLisBillOperationModel dam = new PHISLisBillOperationModel(paramContext);
//		try {
			String entryNames = paramMap.get("tableName")+"";
			String pk = paramMap.get("pkey")+"";
			String primaryKey = "";//dam.getTableID(entryNames, pk);
			res.put("tableName", entryNames);
			res.put("pkey", primaryKey);
//		} catch (ModelDataOperationException e) {
//			throw new HISException(e.getMessage(), e);
//		}
		return res;
	}

	/**必要参数验证*/
	protected List<String> getNotNullFields() {
		List<String> list = new ArrayList<String>();
		list.add("tableName");
		list.add("pkey");
		return list;
	}
}
