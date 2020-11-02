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

public class InpatientCertificateFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

	@SuppressWarnings("deprecation")
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
		try {
			parameters.put("YYRQ", matter.parse(request.get("yyrq") + ""));
			parameters.put("JGBZ", 0);
			Map<String, Object> infoMap = dao.doLoad("select SBXH as SBXH,MZHM as MZHM,BRXM as BRXM,BRXB as BRXB,BRDHHM as BRDHHM," +
					" BRXZ as BRXZ,YYKS as YYKS,CZGH as CZGH,YYRQ as YYRQ,CSNY as CSNY,LXGX as LXGX,BQDM as BQDM,JTDH as JTDH," +
					" LXRM as LXRM,LXDZ as LXDZ,LXDH as LXDH,BRCH as BRCH,YZ as YZ,REMARK as REMARK,INHOSMETHOD as INHOSMETHOD" +
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
			
			String lxgx = infoMap.get("LXGX")!=null?DictionaryController.instance().getDic("phis.dictionary.GB_T4761")
					.getText(infoMap.get("LXGX") + ""):"";
			String bqmc = infoMap.get("BQDM")!=null?DictionaryController.instance().getDic("phis.dictionary.department_bq")
					.getText(infoMap.get("BQDM") + ""):"";

			response.put("TITLE",user.getManageUnitName());
			response.put("MZHM", infoMap.get("MZHM") + "");
			response.put("BRXM", infoMap.get("BRXM") + "");
			response.put("BRXB", brxb);
			response.put("BRXZ", brxz);
			response.put("YYKS", yyks);
			response.put("CZGH", yyys);
			response.put("YYRQ", request.get("yyrq") + "");
			response.put("SBXH", infoMap.get("SBXH") + "");
			
			response.put("LXGX", lxgx);
			response.put("BRBQ", bqmc);
			response.put("LXRM", infoMap.get("LXRM")!=null?(infoMap.get("LXRM") + ""):"");
			response.put("LXDZ", infoMap.get("LXDZ")!=null?(infoMap.get("LXDZ") + ""):"");
			response.put("LXDH", infoMap.get("LXDH")!=null?(infoMap.get("LXDH") + ""):"");
			response.put("BRCH", infoMap.get("BRCH")!=null?(infoMap.get("BRCH") + ""):"");
			response.put("YZ", infoMap.get("YZ")!=null?(infoMap.get("YZ") + ""):"");
			response.put("BRDHHM", infoMap.get("BRDHHM")!=null?(infoMap.get("BRDHHM") + ""):"");
			String remarkStr = "";
			if(infoMap.get("REMARK")!=null){
				if("1".equals(infoMap.get("REMARK") + "")){
					remarkStr = "REMARK1";
				}else if("2".equals(infoMap.get("REMARK") + "")){
					remarkStr = "REMARK2";
				}else if("3".equals(infoMap.get("REMARK") + "")){
					remarkStr = "REMARK3";
				}else if("4".equals(infoMap.get("REMARK") + "")){
					remarkStr = "REMARK4";
				}else if("5".equals(infoMap.get("REMARK") + "")){
					remarkStr = "REMARK5";
				}
			}
			response.put(remarkStr, "√");
			
			String inHosMethodStr = "";
			if(infoMap.get("INHOSMETHOD")!=null){
				if("1".equals(infoMap.get("INHOSMETHOD") + "")){
					inHosMethodStr = "INHOSMETHOD1";
				}else if("2".equals(infoMap.get("INHOSMETHOD") + "")){
					inHosMethodStr = "INHOSMETHOD2";
				}else if("3".equals(infoMap.get("INHOSMETHOD") + "")){
					inHosMethodStr = "INHOSMETHOD3";
				}else if("9".equals(infoMap.get("INHOSMETHOD") + "")){
					inHosMethodStr = "INHOSMETHOD9";
				}
			}
			response.put(inHosMethodStr, "√");
			
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
				response.put("NL", ageMap.get("ages") + "");
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
