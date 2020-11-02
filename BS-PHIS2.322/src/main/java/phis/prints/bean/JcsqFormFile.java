package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bsoft.mpi.util.SchemaUtil;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class JcsqFormFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Long ID=Long.parseLong(request.get("ID") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ID", ID);
		try {
			Map<String, Object> info_map =dao.doLoad(BSPHISEntryNames.JC_BRSQ_LIST, ID);
			info_map=SchemaUtil.setDictionaryMassageForForm(info_map, BSPHISEntryNames.JC_BRSQ_LIST);
			Map<String, Object> BRXB=(Map<String, Object>) info_map.get("BRXB");
			if(BRXB!=null){
			info_map.put("BRXB", BRXB.get("text"));
			}
			Map<String, Object> BRXZ=(Map<String, Object>) info_map.get("BRXZ");
			if(BRXZ!=null){
			info_map.put("BRXZ", BRXZ.get("text"));
			}
			Map<String, Object> YHGX=(Map<String, Object>) info_map.get("YHGX");
			if(YHGX!=null){
			info_map.put("YHGX", YHGX.get("text"));
			}
			Map<String, Object> SQYS=(Map<String, Object>) info_map.get("SQYS");
			if(SQYS!=null){
			info_map.put("SQYS", SQYS.get("text"));
			}
			response.putAll(info_map);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
//		{LXR=teset, SQYS={text=张献身, key=5031}, ZYHM=330329001, LXDH=1888888888, JGID=440402001002, 
//				ICD10=A00.901, CSNY=1999-10-05, ZDRQ=2014-12-26, MZHM=60000, ID=4, SFZH=440400199910057546, 
//				BRXB={text=女, key=2}, SQZT={text=已提交, key=2}, JCZD=霍乱, BRXM=杜甫, SQRQ=2015-02-28, BRID=448,
//				YHGX={text=子, key=2}, BQZY=123, JCYJ=123, LXDZ=水电费全额2额2额是打发斯蒂芬, BRXZ={text=威士茂, key=6096}, SQFS=1, BRNL=16}
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String jgid = user.getManageUnit().getId();
//		Map<String, Object> zdmcparameters = new HashMap<String, Object>();
//		parameters.put("BRID", Long.parseLong(request.get("brid") + ""));
//		parameters.put("YYKS", Long.parseLong(request.get("yyks") + ""));
//		try {
//			parameters.put("YYRQ", matter.parse(request.get("yyrq") + ""));
//			parameters.put("JGBZ", 0);
//			Map<String, Object> infoMap = dao
//					.doLoad("select SBXH as SBXH,MZHM as MZHM,BRXM as BRXM,BRXB as BRXB,BRXZ as BRXZ,YYKS as YYKS,CZGH as CZGH,YYRQ as YYRQ,CSNY as CSNY from ZY_YYBR where YYKS=:YYKS and BRID=:BRID and YYRQ=:YYRQ and JGBZ=:JGBZ",
//							parameters);
//			String brxz = DictionaryController.instance()
//					.getDic("phis.dictionary.patientProperties_ZY")
//					.getText(infoMap.get("BRXZ") + "");
//			String brxb = DictionaryController.instance()
//					.getDic("phis.dictionary.gender")
//					.getText(infoMap.get("BRXB") + "");
//			String yyys = DictionaryController.instance()
//					.getDic("phis.dictionary.doctor_cfqx")
//					.getText(infoMap.get("CZGH") + "");
//			String yyks = DictionaryController.instance()
//					.getDic("phis.dictionary.department_zy")
//					.getText(infoMap.get("YYKS") + "");
//			response.put("MZHM", infoMap.get("MZHM") + "");
//			response.put("BRXM", infoMap.get("BRXM") + "");
//			response.put("BRXB", brxb);
//			response.put("BRXZ", brxz);
//			response.put("YYKS", yyks);
//			response.put("CZGH", yyys);
//			response.put("YYRQ", request.get("yyrq") + "");
//			response.put("SBXH", infoMap.get("SBXH") + "");
//			long jzxh = 0l;
//			if (request.get("jzxh") != null) {
//				jzxh = Long.parseLong(request.get("jzxh") + "");
//			}
//			zdmcparameters.put("JZXH", jzxh);
//			zdmcparameters.put("ZZBZ", 1);
//			zdmcparameters.put("JGID", jgid);
//			Map<String, Object> jzdmcMap = dao
//					.doLoad("select ZDMC as ZDMC from MS_BRZD where JZXH=:JZXH and ZZBZ=:ZZBZ and JGID=:JGID",
//							zdmcparameters);
//			if (jzdmcMap != null) {
//				if (jzdmcMap.get("ZDMC") != null) {
//					response.put("ZDMC", jzdmcMap.get("ZDMC") + "");
//				} else {
//					response.put("ZDMC", "");
//				}
//			} else {
//				response.put("ZDMC", "");
//			}
//			if (infoMap.get("CSNY") != null && infoMap.get("CSNY") != "") {
//				Map<String, Object> ageMap = BSPHISUtil.getPersonAge(
//						mattertime.parse(infoMap.get("CSNY") + " 00:00:00"),
//						new Date());
//				response.put("NL", ageMap.get("age") + "");
//			} else {
//				response.put("NL", "");
//			}
//		} catch (PersistentDataOperationException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
	}
}
