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
import ctd.print.IHandler;
import ctd.print.PrintException;

//DynamicPrint
public class StorageOfInventoryEjFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Long djxh = 0l;
		if (request.get("djxh") != null) {
			djxh = Long.parseLong(request.get("djxh") +"");
		}
		try {
			StringBuffer sql = new StringBuffer(
					"SELECT a.JLXH as JLXH,a.KCXH as KCXH,a.DJXH as DJXH,a.WZXH as WZXH,a.CJXH as CJXH,c.CJMC as CJMC,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.PCSL as PCSL,a.WZJG as WZJG,a.LSJG as LSJG,a.PCJE as PCJE,a.KCSL as KCSL,a.KCJE as KCJE,a.LSJE as LSJE,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,b.HSLB as HSLB,b.GLFS as GLFS,b.ZBLB as ZBLB,r.RKRQ from WL_PD02 a left join wl_rk01 r on a.DJXH = r.DJXH left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b where a.WZXH = b.WZXH");
			sql.append(" and a.DJXH=").append(djxh);
			sql.append(" ORDER BY a.JLXH");
			list = dao.doSqlQuery(sql.toString(), null);
			SchemaUtil.setDictionaryMassageForList(list,BSPHISEntryNames.WL_PD02 + "_KC");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> m = list.get(i);
			m.put("WZJG", String.format("%1$.4f", m.get("WZJG")));
			m.put("KCSL", String.format("%1$.2f", m.get("KCSL")));
			m.put("PCSL", String.format("%1$.2f", m.get("PCSL") == null ? 0.00:m.get("PCSL")));
			if (null != m.get("RKRQ")) {
				m.put("RKRQ", sdf.format(BSHISUtil.toDate(m.get("RKRQ")+"")));  //入库日期
			}
			records.add(m);
		}
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
			list = dao.doList(cnd, null, BSPHISEntryNames.WL_PD01+"_FORM");
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
		
		
	}
}
