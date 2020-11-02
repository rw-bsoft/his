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
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalAppointmentFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat mattertime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> zdmcparameters = new HashMap<String, Object>();
		parameters.put("BRID", Long.parseLong(request.get("brid") + ""));
		parameters.put("YYKS", Long.parseLong(request.get("yyks") + ""));
		parameters.put("TITLE",user.getManageUnitName());
		try {
			parameters.put("YYRQ", matter.parse(request.get("yyrq") + ""));
			parameters.put("JGBZ", 0);
			Map<String, Object> infoMap = dao.doLoad("select SBXH as SBXH,MZHM as MZHM,BRXM as BRXM,BRXB as BRXB," +
					" BRXZ as BRXZ,YYKS as YYKS,CZGH as CZGH,YYRQ as YYRQ,CSNY as CSNY " +
					" from ZY_YYBR where YYKS=:YYKS and BRID=:BRID and YYRQ=:YYRQ and JGBZ=:JGBZ",
							parameters);
			String brxz = DictionaryController.instance().getDic("phis.dictionary.patientProperties_ZY")
					.getText(infoMap.get("BRXZ") + "");
			String brxb = DictionaryController.instance().getDic("phis.dictionary.gender")
					.getText(infoMap.get("BRXB") + "");
			String yyys = DictionaryController.instance().getDic("phis.dictionary.doctor_cfqx")
					.getText(infoMap.get("CZGH") + "");
			String yyks = DictionaryController.instance().getDic("phis.dictionary.department_zy")
					.getText(infoMap.get("YYKS") + "");
			response.put("MZHM", infoMap.get("MZHM") + "");
			response.put("BRXM", infoMap.get("BRXM") + "");
			response.put("BRXB", brxb);
			response.put("BRXZ", brxz);
			response.put("YYKS", yyks);
			response.put("CZGH", yyys);
			response.put("YYRQ", request.get("yyrq") + "");
			response.put("SBXH", infoMap.get("SBXH") + "");
			long jzxh = 0l;
			if (request.get("jzxh") != null) {
				jzxh = Long.parseLong(request.get("jzxh") + "");
			}
			zdmcparameters.put("JZXH", jzxh);
			zdmcparameters.put("ZZBZ", 1);
			zdmcparameters.put("JGID", jgid);
			Map<String, Object> jzdmcMap = dao.doLoad("select ZDMC as ZDMC from MS_BRZD " +
					" where JZXH=:JZXH and ZZBZ=:ZZBZ and JGID=:JGID",zdmcparameters);
			if (jzdmcMap != null) {
				if (jzdmcMap.get("ZDMC") != null) {
					response.put("ZDMC", jzdmcMap.get("ZDMC") + "");
				} else {
					response.put("ZDMC", "");
				}
			} else {
				response.put("ZDMC", "");
			}
			if (infoMap.get("CSNY") != null && infoMap.get("CSNY") != "") {
				Map<String, Object> ageMap = BSPHISUtil.getPersonAge(mattertime.parse(infoMap.get("CSNY") + " 00:00:00"),
					new Date());
				response.put("NL", ageMap.get("age") + "");
			} else {
				response.put("NL", "");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
