package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.account.user.UserRemoteLoader;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospMediRecordFile implements IHandler {

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
		String sql = "select t.RYNL as RYNL,t.ZYCS as ZYCS,t.JSCS as JSCS,t.YBKH as YBKH,(SELECT t3.cardNo FROM MS_BRDA t2,MPI_Card t3 WHERE t2.BRID=t.BRID and t3.cardTypeCode='01' and t2.EMPIID = t3.empiId) as JKKH, t.BAHM as BAH,t.MZHM as MZHM,(SELECT t1.XZMC FROM GY_BRXZ t1 WHERE t1.BRXZ =t.BRXZ)as FFFS,t.BRXZ as BRXZ,t.BRXM as XM,t.SFZH as SFZH,t.BRXB as XB,t.CSNY as CSRQ,t.GJDM as GJDM,t.MZDM as MZDM,t.HYZK as HY,t.ZYDM as ZYDM,"+
		    "t.CSD_SQS as CSD_SQS,t.CSD_S as CSD_S,t.CSD_X as CSD_X, "+ //出生地
	        "t.JGDM_SQS as JGDM_SQS,t.JGDM_S as JGDM_S,"+ // 籍贯
	        "t.XZZ_DH as XZZ,t.XZZ_YB as XZZ_YB,t.XZZ_DH as XZZ_DH,t.XZZ_SQS as XZZ_SQS,t.XZZ_S as XZZ_S,t.XZZ_X as XZZ_X,t.XZZ_QTDZ as XZZ_QTDZ,"+ //现住址
	        "t.HKDZ_SQS as HKDZ_SQS,t.HKDZ_S as HKDZ_S,t.HKDZ_X as HKDZ_X,t.HKDZ_QTDZ as HKDZ_QTDZ,t.HKYB as HKYB,"+ //户口所在地
	        "t.GZDW as DWDZ,t.DWDH as DWDH,t.DWYB as DWYB,"+ //单位
	        "t.LXRM as LXRXM,t.LXDH as LXRDH,t.LXDZ as LXRDZ,t.LXGX as LXGX,"+ //联系人
	        "'' as RYTJ, "+
	        "t.RYRQ as RYSJ,"+
	        "(SELECT OFFICENAME FROM SYS_Office WHERE ID=t.BRKS) as RYKB,(SELECT k.OFFICENAME FROM SYS_Office k WHERE k.ID =t.BRBQ) as RYBF,t.BRXX as BRXX  "+ //入院
	        "FROM ZY_BRRY t WHERE t.ZYH =:ZYH";
		try {
			List<Map<String, Object>> rklist = dao.doQuery(sql, parameter);
			SchemaUtil.setDictionaryMassageForList(rklist,
					"phis.application.hos.schemas.ZY_BRRY_RYDJ");
			Map<String, Object> brxx= rklist.get(0);
			for(Map.Entry<String, Object> entry: brxx.entrySet()) { 
//				 System.out.print(entry.getKey() + ":" + entry.getValue() + "\t"); 
				if("RYSJ".equals(entry.getKey())){
					brxx.put(entry.getKey(), entry.getValue()!=null?(entry.getValue()+"").substring(0, 19):"");
				}else{
					brxx.put(entry.getKey(), entry.getValue()!=null?entry.getValue()+"":"");
				}
			}
			response.putAll(brxx);
//			if (request.get("GJ") != null) {
//				String str= new String(request.get("GJ").toString().getBytes("iso8859_1"), "UTF-8");
//				response.put("GJ", str);
//			}
//			if (request.get("MZ") != null) {
//				String str= new String(request.get("MZ").toString().getBytes("iso8859_1"), "UTF-8");
//				response.put("MZ", str);
//			}
//			if(rklist.get(0).get("XM") != null){
//			 response.put("XM", rklist.get(0).get("XM")+"");
//			}
//			if(rklist.get(0).get("BAH") != null){
//				response.put("BAH", rklist.get(0).get("BAH")+"");
//			}
//			if(rklist.get(0).get("XB") != null){
//				response.put("XB", rklist.get(0).get("XB")+"");
//			}
//			if(rklist.get(0).get("CSRQ") != null){
//				response.put("CSRQ", rklist.get(0).get("CSRQ")+"");
//			}
//			if(rklist.get(0).get("BRXZ") != null){
//				response.put("BRXZ", rklist.get(0).get("BRXZ")+"");
//			}
//			if(rklist.get(0).get("SFZH") != null){
//				response.put("SFZH", rklist.get(0).get("SFZH")+"");
//			}
			if(brxx.get("HY") != null){
				String HYZK =  brxx.get("HY")+"";
				String HY = HYZK.substring(0, 1);
				response.put("HY",HY);
			}
//			if(rklist.get(0).get("DWDZ") != null){
//				response.put("DWDZ", rklist.get(0).get("DWDZ")+"");
//			}
//			if(rklist.get(0).get("DWDH") != null){
//				response.put("DWDH", rklist.get(0).get("DWDH")+"");
//			}
//			if(rklist.get(0).get("QWYB") != null){
//				response.put("QWYB", rklist.get(0).get("QWYB")+"");
//			}
//			if(rklist.get(0).get("LXRXM") != null){
//				response.put("LXRXM", rklist.get(0).get("LXRXM")+"");
//			}
//			if(rklist.get(0).get("LXRDZ") != null){
//				response.put("LXRDZ", rklist.get(0).get("LXRDZ")+"");
//			}
//			if(rklist.get(0).get("LXRDH") != null){ 
//				response.put("LXRDH", rklist.get(0).get("LXRDH")+"");
//			}
//			if(rklist.get(0).get("HKDZ") != null){ 
//				response.put("HKDZ", rklist.get(0).get("HKDZ")+"");
//			}
//			if(rklist.get(0).get("HKYB") != null){ 
//				response.put("HKYB", rklist.get(0).get("HKYB")+"");
//			}
//			if(rklist.get(0).get("JKKH") != null){ 
//				response.put("JKKH", rklist.get(0).get("JKKH")+"");
//			}
//			response.put("FFFS", rklist.get(0).get("FFFS")+"");
			// 出生日期
//			String CSRQ1= rklist.get(0).get("CSRQ")+"";
//			int CSRQ2 = Integer.parseInt(CSRQ1.substring(0, 4));
//			String rq1 = BSHISUtil.getDate();
//			int rq = Integer.parseInt(rq1.substring(0, 4));
//			int NL = rq - CSRQ2;
//			response.put("NL", NL);
			//response.put("JG", rklist.get(0).get("JGDM_SQS")+""+rklist.get(0).get("JGDM_S")+"");
//			if (rklist.get(0).get("CSD_SQS") != null) {
//				if (rklist.get(0).get("CSD_S") != null) {
//					if (rklist.get(0).get("CSD_X") != null) {
//						response.put("CSD", rklist.get(0).get("CSD_SQS")+""+rklist.get(0).get("CSD_S")+""+rklist.get(0).get("CSD_X")+"");
//					}else {
//						response.put("CSD", rklist.get(0).get("CSD_SQS")+""+rklist.get(0).get("CSD_S")+"");
//					}
//				}else {
//					response.put("CSD", rklist.get(0).get("CSD_SQS"));
//				}
//			}
//			if (rklist.get(0).get("HKDZ_SQS") != null) {
//				if (rklist.get(0).get("HKDZ_S") != null) {
//					if (rklist.get(0).get("HKDZ_X") != null) {
//						response.put("HKDZ", rklist.get(0).get("HKDZ_SQS")+""+rklist.get(0).get("HKDZ_S")+""+rklist.get(0).get("HKDZ_X")+"");
//					}else {
//						response.put("HKDZ", rklist.get(0).get("HKDZ_SQS")+""+rklist.get(0).get("HKDZ_S")+"");
//					}
//				}else {
//					response.put("HKDZ", rklist.get(0).get("HKDZ_SQS"));
//				}
//			}
//			if (rklist.get(0).get("JGDM_SQS") != null) {
//				if (rklist.get(0).get("JGDM_S") != null) {
//					response.put("JG", rklist.get(0).get("JGDM_SQS")+""+rklist.get(0).get("JGDM_S")+"");
//				}else {
//					response.put("JG", rklist.get(0).get("JGDM_SQS"));
//				}
//			}
			//籍贯
			String jgdm = ""+(brxx.get("JGDM_SQS_text")!=null?brxx.get("JGDM_SQS_text"):"")+(brxx.get("JGDM_S_text")!=null?brxx.get("JGDM_S_text"):"");
			response.put("JGDM", jgdm);
			//出生地
			String csd = ""+(brxx.get("CSD_SQS_text")!=null?brxx.get("CSD_SQS_text"):"")+(brxx.get("CSD_S_text")!=null?brxx.get("CSD_S_text"):"")+(brxx.get("CSD_X_text")!=null?brxx.get("CSD_X_text"):"");
			response.put("CSD", csd);
			//现住址
			String xzz = ""+(brxx.get("XZZ_SQS_text")!=null?brxx.get("XZZ_SQS_text"):"")+(brxx.get("XZZ_S_text")!=null?brxx.get("XZZ_S_text"):"")+(brxx.get("XZZ_X_text")!=null?brxx.get("XZZ_X_text"):"")+(brxx.get("XZZ_QTDZ")!=null?brxx.get("XZZ_QTDZ"):"");
			response.put("XZZ", xzz);
			//户口地址
			String hkdz = ""+(brxx.get("HKDZ_SQS_text")!=null?brxx.get("HKDZ_SQS_text"):"")+(brxx.get("HKDZ_S_text")!=null?brxx.get("HKDZ_S_text"):"")+(brxx.get("HKDZ_X_text")!=null?brxx.get("HKDZ_X_text"):"")+(brxx.get("HKDZ_QTDZ")!=null?brxx.get("HKDZ_QTDZ"):"");
			response.put("HKDZ", hkdz);
//			if(rklist.get(0).get("YB") != null){
//				response.put("YB", rklist.get(0).get("YB"));
//			}
//			if(rklist.get(0).get("DWDZ") != null){
//				response.put("DWDZ", rklist.get(0).get("DWDZ"));
//			}
//			if(rklist.get(0).get("DWDH") != null){
//				response.put("DWDH", rklist.get(0).get("DWDH"));
//			}
//			if(rklist.get(0).get("DWYB") != null){
//				response.put("DWYB", rklist.get(0).get("DWYB"));
//			}
			/*if(rklist.get(0).get("LXRGX") != null){
				response.put("GX", rklist.get(0).get("LXRGX")+"");
			}*/
//			if(rklist.get(0).get("XZZ_DH") != null){
//				response.put("DH", rklist.get(0).get("XZZ_DH")+"");
//			}
//			if(rklist.get(0).get("RYSJ") != null){
//				response.put("RYSJ", BSHISUtil.toDate(rklist.get(0).get("RYSJ")+""));
//			}
//			if(rklist.get(0).get("RYKB") != null){
//				response.put("RYKB", rklist.get(0).get("RYKB")+"");
//			}
//			if(rklist.get(0).get("RYBF") != null){
//				response.put("RYBF", rklist.get(0).get("RYBF")+"");
//			}
//			if(rklist.get(0).get("BRXX") != null){
//				response.put("BRXX", rklist.get(0).get("BRXX")+"");
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
