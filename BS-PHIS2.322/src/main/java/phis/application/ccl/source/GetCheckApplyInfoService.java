package phis.application.ccl.source;

import java.util.HashMap;
import java.util.Map;

import phis.application.lis.source.HISAbstractService;
import phis.application.lis.source.rpc.HISException;
import phis.application.lis.source.ModelOperationException;

import ctd.util.context.Context;
/**
 * 获取检查申请单信息
 * @author gaohan
 *
 */
public class GetCheckApplyInfoService extends HISAbstractService{

	@Override
	public Map<String, Object> process(Map<String, Object> paramMap,
			Context paramContext) throws HISException {
		Map<String, Object> res = new HashMap<String, Object>();
		PHISCheckApplyModel dam = new PHISCheckApplyModel(paramContext);
		try {
			res = dam.getCheckApplyInfo(paramMap);
		} catch (ModelOperationException e) {
			throw new HISException(e.getMessage(), e);
		}
		return res;
	}

}
