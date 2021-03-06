package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;

import ctd.util.context.Context;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;

//DynamicPrint
public class StorageOfInventoryTzFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Long djxh = 0l;
		if (request.get("djxh") != null) {
			djxh = Long.parseLong(request.get("djxh") +"");
		}
		StringBuffer sql = new StringBuffer(
				"SELECT a.JLXH as JLXH,a.KCXH as KCXH,a.ZBXH as ZBXH,a.KSDM as KSDM,a.DJXH as DJXH,a.WZXH as WZXH,a.CJXH as CJXH,c.CJMC as CJMC,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.PCSL as PCSL,a.WZJG as WZJG,a.PCJE as PCJE,a.KCSL as KCSL,a.KCJE as KCJE,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,b.HSLB as HSLB,b.GLFS as GLFS,b.ZBLB as ZBLB,d.WZBH as WZBH,a.CZBZ as CZBZ,d.WZZT as WZZT from WL_PD02 a left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b,WL_ZCZB d where a.WZXH = b.WZXH AND a.ZBXH = d.ZBXH");
		sql.append(" and a.DJXH=").append(djxh);
		sql.append(" ORDER BY a.JLXH");
		try {
			list = dao.doSqlQuery(sql.toString(), null);
			SchemaUtil.setDictionaryMassageForList(list,BSPHISEntryNames.WL_PD02 + "_TZ");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		records.addAll(list);
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> zk = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		String kf = (String) user.getProperty("treasuryName");
		Long djxh = 0l;
		String GLFS_text = "";
		if (request.get("djxh") != null) {
			djxh = Long.parseLong(request.get("djxh") + "");
		}
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "DJXH", "d", djxh);
			list = dao.doList(cnd, null, BSPHISEntryNames.WL_PD01+"_KS_FORM");
			zk = list.get(0);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if("1".equals(String.valueOf(zk.get("GLFS")))){
			GLFS_text = "库存管理";
		}else if("2".equals(String.valueOf(zk.get("GLFS")))){
			GLFS_text = "科室管理";
		}else if("3".equals(String.valueOf(zk.get("GLFS")))){
			GLFS_text = "台帐管理";
		}
		response.put("TITLE", jgname+kf+"盘点单");
		response.put("LSDH", zk.get("LZDH")); //流水单号
		response.put("GLFS_text", GLFS_text); //盘点方式
		if (null != zk.get("JZRQ")) {
			response.put("JZRQ", sdf.format(BSHISUtil.toDate(zk.get("JZRQ")+"")));  //盘点日期
		}
		response.put("DJBZ", zk.get("DJBZ")); //单据备注
		
		Dictionary doctor = null;
		try {
			doctor = DictionaryController.instance().get("phis.dictionary.doctor");
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		if(zk.containsKey("ZDGH")&&zk.get("ZDGH")!="" &&zk.get("ZDGH")!=null){
			response.put("ZDR", doctor.getText(zk.get("ZDGH").toString()));
		}
		if(zk.containsKey("SHGH")&&zk.get("SHGH")!="" &&zk.get("SHGH")!=null){
			response.put("SHR", doctor.getText(zk.get("SHGH").toString()));
		}
		if(zk.containsKey("JZGH")&&zk.get("JZGH")!="" &&zk.get("JZGH")!=null){
			response.put("JZR", doctor.getText(zk.get("JZGH").toString()));
		}
		
	}
}
