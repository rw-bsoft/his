package phis.prints.bean; 

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.utils.BSPEMRUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class EMRMedicalRecordsFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}

	// 参数
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		if (request.get("ZYH") == null) {
			throw new RuntimeException("住院号不能为空！");
		}
		long ZYH = Long.parseLong(request.get("ZYH") + "");
		List<Object> sscnd = CNDHelper.createSimpleCnd("eq", "JZXH", "i", Integer.parseInt(ZYH + ""));
		List<Object> mzzdcnd = CNDHelper.createArrayCnd("and", sscnd, CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 11));
		List<Object> blzdcnd = CNDHelper.createArrayCnd("and", sscnd, CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 44));
		List<Object> sszdzdcnd = CNDHelper.createArrayCnd("and", sscnd, CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 45));
		List<Object> cyzdcnd = CNDHelper.createArrayCnd("and", sscnd, CNDHelper.createSimpleCnd("eq", "ZDLB", "i", 51));
		parameter.put("ZYH", ZYH);
		try {
			Map<String, Object> JLXH = dao.doLoad("select a.JLXH as JLXH from EMR_BASY a where a.JZXH = :ZYH",parameter);
			Map<String, Object> EMR_BASY = dao.doLoad(BSPHISEntryNames.EMR_BASY, JLXH.get("JLXH"));
			if(EMR_BASY.get("ZKYSQM")==null){EMR_BASY.put("ZKYSQM", "/");}
			if(EMR_BASY.get("ZKHSQM")==null){EMR_BASY.put("ZKHSQM", "/");}
			if(EMR_BASY.get("ZKRQ")==null){EMR_BASY.put("ZKRQ", "/");}
			if(EMR_BASY.get("BAZL")==null){EMR_BASY.put("BAZL", "/");}
			if(EMR_BASY.get("ZRHSQM")==null){EMR_BASY.put("ZRHSQM", "/");}
			if(EMR_BASY.get("ZZYSQM")==null){EMR_BASY.put("ZZYSQM", "/");}
			if(EMR_BASY.get("ZRYSQM")==null){EMR_BASY.put("ZRYSQM", "/");}
			if(EMR_BASY.get("ZYYSQM")==null){EMR_BASY.put("ZYYSQM", "/");}
			if(EMR_BASY.get("KZRQM")==null){EMR_BASY.put("KZRQM", "/");}
			if(EMR_BASY.get("BABMYQM")==null){EMR_BASY.put("BABMYQM", "/");}
			if(EMR_BASY.get("JXYSQM")==null){EMR_BASY.put("JXYSQM", "/");}
			if(EMR_BASY.get("SXYSQM")==null){EMR_BASY.put("SXYSQM", "/");}
			
			if(EMR_BASY.get("LCLJBZ")!=null){
				EMR_BASY.put("LCLJBZ"+EMR_BASY.get("LCLJBZ"), "√");
			}else{
				EMR_BASY.put("LCLJBZ"+EMR_BASY.get("LCLJBZ"), "/");
			}
			
			EMR_BASY.put("RYRQ", (EMR_BASY.get("RYRQ")+"").substring(0, 19));
			EMR_BASY.put("CYRQ", (EMR_BASY.get("CYRQ")+"").substring(0, 19));
			int RYQHMSJ_T = Integer.parseInt(EMR_BASY.get("RYQHMSJ")==null?"0":EMR_BASY.get("RYQHMSJ")+"")/(60*24);
			int RYQHMSJ_S = (Integer.parseInt(EMR_BASY.get("RYQHMSJ")==null?"0":EMR_BASY.get("RYQHMSJ")+"")%(60*24))/60;
			int RYQHMSJ_F = Integer.parseInt(EMR_BASY.get("RYQHMSJ")==null?"0":EMR_BASY.get("RYQHMSJ")+"")%60;
			EMR_BASY.put("RYQHMSJ_T", RYQHMSJ_T);
			EMR_BASY.put("RYQHMSJ_S", RYQHMSJ_S);
			EMR_BASY.put("RYQHMSJ_F", RYQHMSJ_F);
			int RYHHMSJ_T = Integer.parseInt(EMR_BASY.get("RYHHMSJ")==null?"0":EMR_BASY.get("RYHHMSJ")+"")/(60*24);
			int RYHHMSJ_S = (Integer.parseInt(EMR_BASY.get("RYHHMSJ")==null?"0":EMR_BASY.get("RYHHMSJ")+"")%(60*24))/60;
			int RYHHMSJ_F = Integer.parseInt(EMR_BASY.get("RYHHMSJ")==null?"0":EMR_BASY.get("RYHHMSJ")+"")%60;
			EMR_BASY.put("RYHHMSJ_T", RYHHMSJ_T);
			EMR_BASY.put("RYHHMSJ_S", RYHHMSJ_S);
			EMR_BASY.put("RYHHMSJ_F", RYHHMSJ_F);
			SchemaUtil.setDictionaryMassageForList(EMR_BASY,BSPHISEntryNames.EMR_BASY);
			
			UserRoleToken user = UserRoleToken.getCurrent();
			Map<String,Object> parameters = new HashMap<String,Object>();
			String ref = user.getManageUnit().getRef();
			String KSSQL = "select a.OFFICENAME as OFFICENAME from SYS_Office a where a.HOSPITALDEPT='1' and a.ORGANIZCODE=:REF and a.LOGOFF<>'1' and a.id=:ID";
			String QBSQL = "select a.OFFICENAME as OFFICENAME from SYS_Office a where a.HOSPITALAREA='1' and a.ORGANIZCODE=:REF and a.LOGOFF<>'1' and a.id=:ID";
			parameters.put("REF", ref);
			if((EMR_BASY.get("RYKS")+"").length()>0){
				parameters.put("ID", Long.parseLong(EMR_BASY.get("RYKS")+""));
				EMR_BASY.put("RYKS_text", dao.doLoad(KSSQL, parameters).get("OFFICENAME"));
			}else{
				EMR_BASY.put("RYKS_text","/");
			}
			if((EMR_BASY.get("CYKS")+"").length()>0){
				parameters.put("ID", Long.parseLong(EMR_BASY.get("CYKS")+""));
				EMR_BASY.put("CYKS_text", dao.doLoad(KSSQL, parameters).get("OFFICENAME"));
			}else{
				EMR_BASY.put("CYKS_text","/");
			}
			if((EMR_BASY.get("RYBF")+"").length()>0){
				parameters.put("ID", Long.parseLong(EMR_BASY.get("RYBF")+""));
				EMR_BASY.put("RYBF_text", dao.doLoad(QBSQL, parameters).get("OFFICENAME"));
			}else{
				EMR_BASY.put("RYBF_text","/");
			}
			if((EMR_BASY.get("CYBQ")+"").length()>0){
				parameters.put("ID", Long.parseLong(EMR_BASY.get("CYBQ")+""));
				EMR_BASY.put("CYBQ_text", dao.doLoad(QBSQL, parameters).get("OFFICENAME"));
			}else{
				EMR_BASY.put("CYBQ_text","/");
			}
			if(EMR_BASY.get("LYFS")!=null&&(Integer.parseInt(EMR_BASY.get("LYFS")+"")==2 || Integer.parseInt(EMR_BASY.get("LYFS")+"")==3)){
				EMR_BASY.put("NJSYLJLMC"+EMR_BASY.get("LYFS"), EMR_BASY.get("NJSYLJLMC"));
				if(Integer.parseInt(EMR_BASY.get("LYFS")+"")==2){
					EMR_BASY.put("NJSYLJLMC3","/");
				}
				if(Integer.parseInt(EMR_BASY.get("LYFS")+"")==3){
					EMR_BASY.put("NJSYLJLMC2","/");
				}
			}else{
				EMR_BASY.put("NJSYLJLMC2","/");
				EMR_BASY.put("NJSYLJLMC3","/");
			}
			Map<String, Object> EMR_BASY_FY = dao.doLoad(BSPHISEntryNames.EMR_BASY_FY, JLXH.get("JLXH"));
			List<Map<String, Object>> EMR_ZYSSJLS = dao.doList(sscnd, "JLXH",BSPHISEntryNames.EMR_ZYSSJL);
			SchemaUtil.setDictionaryMassageForList(EMR_ZYSSJLS,BSPHISEntryNames.EMR_ZYSSJL);
			if (EMR_ZYSSJLS != null) {
				for (int i = 0; i < EMR_ZYSSJLS.size(); i++) {
					Map<String, Object> EMR_ZYSSJL = EMR_ZYSSJLS.get(i);
					for (Entry<String, Object> entry : EMR_ZYSSJL.entrySet()) {
						//if(entry.getKey().equals("YHDJ_text")){
							//EMR_BASY.put(entry.getKey() + "_" + i, EMR_ZYSSJL.get("QKLB_text")+"/"+entry.getValue());
						if(entry.getKey().equals("YHDJ_text")){	
						EMR_BASY.put(entry.getKey() + "_" + i, EMR_ZYSSJL.get("YHDJ"));
						//} else if(entry.getKey().equals("QKLB_text")){
						} else if(entry.getKey().equals("QKLB_text")){	
							//EMR_BASY.put(entry.getKey() + "_" + i, EMR_ZYSSJL.get("QKLB_text")+"/"+entry.getValue());
							EMR_BASY.put(entry.getKey() + "_" + i, EMR_ZYSSJL.get("QKLB"));
						} else if(entry.getKey().equals("SSLB_text")){	
							EMR_BASY.put(entry.getKey() + "_" + i, EMR_ZYSSJL.get("SSLB")); 	
						}else{
							EMR_BASY.put(entry.getKey() + "_" + i,entry.getValue()==null?"":entry.getValue());
						}
					}
				}
			}
			List<Map<String, Object>> CY_ZYZDJLS = dao.doList(cyzdcnd, "JLXH",BSPHISEntryNames.EMR_ZYZDJL);
			SchemaUtil.setDictionaryMassageForList(CY_ZYZDJLS,BSPHISEntryNames.EMR_ZYZDJL);
			if (CY_ZYZDJLS != null) {
				for (int i = 0; i < CY_ZYZDJLS.size(); i++) {
					Map<String, Object> EMR_ZYZDJL = CY_ZYZDJLS.get(i);
					if("1".equals(EMR_ZYZDJL.get("ZZBZ")+"")){
						if("1".equals(EMR_ZYZDJL.get("CYZGDM")+"")||"2".equals(EMR_ZYZDJL.get("CYZGDM")+"")
						||"3".equals(EMR_ZYZDJL.get("CYZGDM")+"")){
							EMR_BASY.put("ZYZDZLZG"+EMR_ZYZDJL.get("CYZGDM"), "√");
						}
					}
					for (Entry<String, Object> entry : EMR_ZYZDJL.entrySet()) {
						EMR_BASY.put(entry.getKey() + "_" + i, entry.getValue());
					}
				}
			}
			Map<String,Object> MZ_ZYZDJLS = dao.doLoad(mzzdcnd, BSPHISEntryNames.EMR_ZYZDJL);
			if(MZ_ZYZDJLS!=null){
				SchemaUtil.setDictionaryMassageForList(MZ_ZYZDJLS,BSPHISEntryNames.EMR_ZYZDJL);
				EMR_BASY.put("MZZD", MZ_ZYZDJLS.get("MSZD"));
				EMR_BASY.put("MZ_JBBM", MZ_ZYZDJLS.get("JBBM"));
			}else{
				EMR_BASY.put("MZZD","/");
				EMR_BASY.put("MZ_JBBM","/");
			}
			Map<String,Object> BL_ZYZDJLS = dao.doLoad(blzdcnd, BSPHISEntryNames.EMR_ZYZDJL);
			if(BL_ZYZDJLS!=null){
				SchemaUtil.setDictionaryMassageForList(BL_ZYZDJLS,BSPHISEntryNames.EMR_ZYZDJL);
				EMR_BASY.put("BLZD", BL_ZYZDJLS.get("MSZD"));
				EMR_BASY.put("BL_JBBM", BL_ZYZDJLS.get("JBBM"));
			}else{
				EMR_BASY.put("BLZD","/");
				EMR_BASY.put("BL_JBBM","/");
			}
			Map<String,Object> SSZD_ZYZDJLS = dao.doLoad(sszdzdcnd, BSPHISEntryNames.EMR_ZYZDJL);
			if(SSZD_ZYZDJLS!=null){
				SchemaUtil.setDictionaryMassageForList(SSZD_ZYZDJLS,BSPHISEntryNames.EMR_ZYZDJL);
				EMR_BASY.put("SSZD", SSZD_ZYZDJLS.get("MSZD"));
				EMR_BASY.put("SS_JBBM", SSZD_ZYZDJLS.get("JBBM"));
			}else{
				EMR_BASY.put("SSZD","/");
				EMR_BASY.put("SS_JBBM","/");
			}
			Map<String, String> BASY = new HashMap<String, String>();
			BASY = getMaptoString(EMR_BASY_FY);
			BASY.putAll(getMaptoString(EMR_BASY));
			response.putAll(BASY);
			Map<String,Object> record = new HashMap<String, Object>();
			record.put("YWID1", ZYH);//一般填写就诊序号
			record.put("YWID2", JLXH.get("JLXH"));//一般填写病历编号
			record.put("YEID3", JLXH.get("JLXH"));//一般填写业务操作的主键值
			record.put("RZNR", "病案首页");
			BSPEMRUtil.doSaveEmrOpLog(BSPEMRUtil.OP_PRINT, record, dao, ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> getMaptoString(Map<String, Object> map) {
		Map<String, String> reMap = new HashMap<String, String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			String value ="/";
			if(entry.getValue()!=null && !(entry.getValue()+"").equals("null") && (entry.getValue()+"").length()>0 ){
				value=entry.getValue()+"";
			}else{
				reMap.put(key, "/");
				reMap.put(key+"__text", "/");
			}
			reMap.put(key, value);
		}
		return reMap;
	}
}
