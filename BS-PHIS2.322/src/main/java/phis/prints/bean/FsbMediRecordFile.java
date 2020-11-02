package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.account.user.UserRemoteLoader;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FsbMediRecordFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}
 
	// 参数
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
//		System.out.println(request.get("ZYH"));
		if (request.get("ZYH") == null) {
			throw new RuntimeException("住院号不能为空！");
		}
		long ZYH = Long.parseLong(request.get("ZYH")+"");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnitName();
		String JGID = user.getManageUnit().getId();
		response.put("JGID",JGID);
		response.put("YLJG",jgname);
		//response.put("BAH",request.get("BAHM"));
		parameter.put("ZYH", ZYH);
		try {
			Map<String, Object> jc_brry = dao.doLoad(BSPHISEntryNames.JC_BRRY_RYDJ, ZYH);
			SchemaUtil.setDictionaryMassageForList(jc_brry,
					BSPHISEntryNames.JC_BRRY_RYDJ);
			if(jc_brry!=null){
				jc_brry.put("ZRHS", jc_brry.get("ZRHS_text"));
				response.putAll(jc_brry);
			}
			Map<String, Object> jc_tbkk = dao.doLoad(CNDHelper.createSimpleCnd("eq", "ZYH", "i", ZYH),BSPHISEntryNames.JC_TBKK);
			if(jc_tbkk!=null){
				SchemaUtil.setDictionaryMassageForList(jc_tbkk,
						BSPHISEntryNames.JC_TBKK);
				response.putAll(jc_tbkk);
			}
//			Map<String, Object> jc_brsq = dao.doLoad(CNDHelper.createSimpleCnd("eq", "BRID", "i", jc_brry.get("BRID")),BSPHISEntryNames.JC_BRSQ_LIST);
//			SchemaUtil.setDictionaryMassageForList(jc_brsq,
//					BSPHISEntryNames.JC_BRSQ_LIST);
			Map<String,Object> ms_brda = dao.doLoad(CNDHelper.createSimpleCnd("eq", "BRID", "i", jc_brry.get("BRID")),BSPHISEntryNames.MS_BRDA);
//			SchemaUtil.setDictionaryMassageForList(ms_brda,
//					BSPHISEntryNames.MS_BRDA);
			Map<String,Object> mpi = dao.doLoad(CNDHelper.createSimpleCnd("eq", "EMPIID", "s", ms_brda.get("EMPIID")),BSPHISEntryNames.MPI_DemographicInfo);
			SchemaUtil.setDictionaryMassageForList(mpi,
					BSPHISEntryNames.MPI_DemographicInfo);
			jc_brry.putAll(mpi);
			if(jc_brry!=null){
				response.putAll(jc_brry);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
