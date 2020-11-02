package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class BedCardFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		long zyh = Long.parseLong(request.get("ZYH") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		parameters.put("ZYH", zyh);
		parameters.put("JGID", jgid);
		String hql = "select BRXM as BRXM,BRXB as BRXB,BRKS as BRKS,ZZYS as ZZYS,CSNY as CSNY from ZY_BRRY where JGID=:JGID and ZYH=:ZYH";
		try {
			Map<String,Object> brxx = dao.doLoad(hql, parameters);
			if(brxx!=null){
				if(brxx.get("BRXM")!=null){
					response.put("BRXM", brxx.get("BRXM") + "");
				}
				if(brxx.get("BRXB")!=null){
					response.put("BRXB", DictionaryController.instance()
							.get("phis.dictionary.gender")
							.getText(brxx.get("BRXB").toString()));
				}
				if(brxx.get("BRKS")!=null){
					response.put("BRKS", DictionaryController.instance()
							.get("phis.dictionary.department_zy")
							.getText(brxx.get("BRKS").toString()));
				}
				if(brxx.get("ZZYS")!=null){
					response.put("ZZYS", "床位医生：" + DictionaryController.instance()
							.get("phis.dictionary.doctor")
							.getText(brxx.get("ZZYS").toString()));
				}else{
					response.put("ZZYS", "床位医生：");
				}
				if (brxx.get("CSNY") != null && brxx.get("CSNY") != "") {
					Map<String, Object> ageMap = BSPHISUtil.getPersonAge(matter.parse(brxx.get("CSNY") + " 00:00:00"),
						new Date());
					response.put("AGE", ageMap.get("ages") + "");
				} else {
					response.put("AGE", "");
				}
			}
			response.put("BRCH", request.get("BRCH") + "");
			response.put("ZYHM", request.get("ZYHM") + "");
			String ryrq = request.get("RYRQ") + "";
			response.put("RYRQ", "入院日期：" + matter.format(matter.parse(ryrq.replace("_", " "))));
		} catch (ParseException e) {
			response.put("RYRQ", "入院日期：");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (PersistentDataOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
