package phis.application.cic.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class EMRTreeModule {
	private BaseDAO dao;
	public EMRTreeModule(BaseDAO dao){
		this.dao = dao;
	}
	
	/**
	 * 加载EMR树
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 */
	public Map<String, Object> doLoadNavTree(Map<String, Object> req, Map<String, Object> res,
			 Context ctx){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("tjjyTree",loadTJJYData(ctx));//体检检验节点
		map.put("pacsTree",loadPacsData(req,ctx,1));//pacs节点
		map.put("pacsTree_zy",loadPacsData(req,ctx,2));//pacs节点住院
		return map;
	}
	/**
	 * 添加体检检验树节点
	 * @return
	 */
	public List<Map<String, Object>> loadTJJYData(Context ctx){
		List<Map<String, Object>> zlxxList = new ArrayList<Map<String,Object>>();
		//检验菜单 
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		String lisFlag = ParameterUtil.getParameter(manaUnitId, "QYJYBZ", ctx);
		if(lisFlag.equals("1")){
			Map<String, Object> jylr = new HashMap<String, Object>();
			jylr.put("key", "B06");
			jylr.put("text", "检验录入");
			jylr.put("requireKeys", "empiId");
			jylr.put("ref", "phis.application.cic.CIC/CIC/CIC24");
			jylr.put("readOnlyKey", "EHR_HealthRecord_readOnly");
			jylr.put("leaf", true);
			zlxxList.add(jylr);
			
			Map<String, Object> jybg = new HashMap<String, Object>();
			jybg.put("key", "B07");
			jybg.put("text", "检验报告");
			jybg.put("requireKeys", "empiId");
			jybg.put("ref", "phis.application.cic.CIC/CIC/CIC26");
			jybg.put("readOnlyKey", "EHR_HealthRecord_readOnly");
			jybg.put("leaf", true);
			zlxxList.add(jybg);
		}
		return zlxxList;
	}
	/**
	 * 添加pacs树节点
	 * @return
	 */
	public List<Map<String, Object>> loadPacsData(Map<String, Object> req,Context ctx,int tag){
		List<Map<String, Object>> zlxxList = new ArrayList<Map<String,Object>>();
		//pacs菜单 
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		String lisFlag = ParameterUtil.getParameter(manaUnitId, "QYJCBZ", ctx);
		if(lisFlag.equals("1")){
			Map<String, Object> tjsq = new HashMap<String, Object>();
			tjsq.put("key", "F04");
			tjsq.put("text", "检查录入");
			tjsq.put("requireKeys", "empiId");
			if(tag==1){
				tjsq.put("ref", "phis.application.ccl.CCL/CCL/CCL40");
			}else{
				tjsq.put("ref", "phis.application.ccl.CCL/CCL/CCL22");
			}
			tjsq.put("readOnlyKey", "PACS_FS_readOnly");
			tjsq.put("leaf", true);
			zlxxList.add(tjsq);
			
			Map<String, Object> fsbg = new HashMap<String, Object>();
			fsbg.put("key", "F01");
			fsbg.put("text", "放射报告结果");
			fsbg.put("requireKeys", "empiId");
			if(tag==1){
				fsbg.put("ref", "phis.application.cic.CIC/CIC/CIC44");
			}else{
				fsbg.put("ref", "phis.application.war.WAR/WAR/WAR61");
			}
			fsbg.put("readOnlyKey", "PACS_FS_readOnly");
			fsbg.put("leaf", true);
			zlxxList.add(fsbg);
			Map<String, Object> yxbg = new HashMap<String, Object>();
			yxbg.put("key", "F03");
			yxbg.put("text", "放射影像查看");
			yxbg.put("requireKeys", "empiId");
			if(tag==1){
				yxbg.put("ref", "phis.application.cic.CIC/CIC/CIC46");
			}else{
				yxbg.put("ref", "phis.application.war.WAR/WAR/WAR63");
			}
			yxbg.put("readOnlyKey", "PACS_readOnly");
			yxbg.put("leaf", true);
			zlxxList.add(yxbg);
			Map<String, Object> csbg = new HashMap<String, Object>();
			csbg.put("key", "F02");
			csbg.put("text", "超声报告结果");
			csbg.put("requireKeys", "empiId");
			if(tag==1){
				csbg.put("ref", "phis.application.cic.CIC/CIC/CIC45");
			}else{
				csbg.put("ref", "phis.application.war.WAR/WAR/WAR62");
			}
			csbg.put("readOnlyKey", "PACS_CS_readOnly");
			csbg.put("leaf", true);
			zlxxList.add(csbg);
		
		Map<String, Object> xdt = new HashMap<String, Object>();
		xdt.put("key", "F05");
		xdt.put("text", "查看心电图");
		xdt.put("requireKeys", "empiId");
		if(tag==1){
			xdt.put("ref", "phis.application.cic.CIC/CIC/CIC471");
		}else{
			xdt.put("ref", "phis.application.war.WAR/WAR/WAR641");
		}
		xdt.put("leaf", true);
		zlxxList.add(xdt);
		
		Map<String, Object> tnbbfz = new HashMap<String, Object>();
		tnbbfz.put("key", "F06");
		tnbbfz.put("text", "糖尿病并发症筛查报告");
		tnbbfz.put("requireKeys", "empiId");
		if(tag==1){
			tnbbfz.put("ref", "phis.application.cic.CIC/CIC/CIC48");
		 }else{
			 tnbbfz.put("ref", "phis.application.war.WAR/WAR/WAR65");
		 }
		tnbbfz.put("leaf", true);
		zlxxList.add(tnbbfz);
        }
		
		//体检报告菜单 
		
//		String examReportFlag = ParameterUtil.getParameter(manaUnitId, "QYTJBGBZ", ctx);
//		if(examReportFlag.equals("1")){
//			Map<String, Object> tjbg = new HashMap<String, Object>();
//			tjbg.put("key", "B05");
//			tjbg.put("text", "体检报告");
//			tjbg.put("requireKeys", "empiId");
//			tjbg.put("ref", "phis.application.cic.CIC/CIC/CIC25");
//			tjbg.put("readOnlyKey", "EHR_HealthRecord_readOnly");
//			tjbg.put("leaf", true);
//			zlxxList.add(tjbg);
//		}
		return zlxxList;
	}
}
