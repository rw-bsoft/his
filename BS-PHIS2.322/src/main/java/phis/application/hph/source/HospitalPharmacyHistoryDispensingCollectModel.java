package phis.application.hph.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;

/**
 * 汇总查询model
 * 
 * @author caiijy
 * 
 */
public class HospitalPharmacyHistoryDispensingCollectModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalPharmacyHistoryDispensingCollectModel.class);

	public HospitalPharmacyHistoryDispensingCollectModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> queryCollectRecords(Map<String, Object> body,
			Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("jgid", jgid);
		map_par.put("yfsb", yfsb);
		StringBuffer hql_fyhz = new StringBuffer();
		int fylx = MedicineUtils.parseInt(body.get("fylx"));
		if (fylx == 1 || fylx == 0) {
			hql_fyhz.append("select a.YPGG as YPGG,a.YFDW as YFDW,sum(a.YPSL) as YPSL,a.YFSB as YFSB,a.YPXH as YPXH,a.YPCD as YPCD,d.CDMC as CDMC,b.YPMC as YPMC,b.YPSX as YPSX,a.YPDJ as YPDJ,sum(a.LSJE) as LSJE from YF_ZYFYMX a,YK_TYPK b,ZY_BRRY c,YK_CDDZ d,YF_FYJL e where a.JGID=:jgid and a.YFSB=:yfsb and a.YPXH=b.YPXH and a.ZYH=c.ZYH and a.JGID=c.JGID and a.YPCD=d.YPCD and a.JLID=e.JLID");
			if (body.get("datefrom") != null) {
				hql_fyhz.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
				map_par.put("dateFrom", body.get("datefrom") + "");
			}
			if (body.get("dateTo") != null) {
				hql_fyhz.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
				map_par.put("dateTo", body.get("dateTo") + "");
			}
			if (body.get("lx") != null) {
				hql_fyhz.append(" and e.FYLX=:lx ");
				map_par.put("lx", MedicineUtils.parseInt(body.get("lx")));
			}
			if (body.get("bq") != null) {
				hql_fyhz.append(" and a.LYBQ=:bq ");
				map_par.put("bq", MedicineUtils.parseLong(body.get("bq")));
			}
			if (fylx == 1) {
				hql_fyhz.append(" and a.YPSL>0 ");
			}
			hql_fyhz.append(" group by a.YFSB,a.YPXH,a.YPCD,d.CDMC,b.YPMC,a.YPGG,b.YPSX,a.YFDW,a.YPDJ");
		} else {
			hql_fyhz.append("select a.YPGG as YPGG,a.YFDW as YFDW,sum(a.YPSL) as YPSL,a.YFSB as YFSB,a.YPXH as YPXH,a.YPCD as YPCD,d.CDMC as CDMC,b.YPMC as YPMC,b.YPSX as YPSX,a.YPJG as YPDJ,sum(a.YPJG*YPSL) as LSJE from BQ_TYMX a,YK_TYPK b,ZY_BRRY c,YK_CDDZ d,ZY_BQYZ e where a.JGID=:jgid and a.YFSB=:yfsb and a.YPXH=b.YPXH and a.ZYH=c.ZYH and a.JGID=c.JGID and a.YPCD=d.YPCD and a.YZID=e.JLXH");
			if (body.get("datefrom") != null) {
				hql_fyhz.append(" and to_char(a.TYRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
				map_par.put("dateFrom", body.get("datefrom") + "");
			}
			if (body.get("dateTo") != null) {
				hql_fyhz.append(" and to_char(a.TYRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
				map_par.put("dateTo", body.get("dateTo") + "");
			}
			if (body.get("lx") != null) {
				hql_fyhz.append(" and e.XMLX=:lx ");
				map_par.put("lx", MedicineUtils.parseInt(body.get("lx")));
			}
			if (body.get("bq") != null) {
				hql_fyhz.append(" and a.TYBQ=:bq ");
				map_par.put("bq", MedicineUtils.parseLong(body.get("bq")));
			}
			hql_fyhz.append(" group by a.YFSB,a.YPXH,a.YPCD,d.CDMC,b.YPMC,a.YPGG,b.YPSX,a.YFDW,a.YPJG");
		}
		MedicineCommonModel model = new MedicineCommonModel(dao);
		return model.getPageInfoRecord(req, map_par, hql_fyhz.toString(), null);
	}

	public Map<String, Object> queryCollectRecordsHZ(Map<String, Object> body,
			Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String bqsb = MedicineUtils.parseLong(user.getProperty("wardId"))+"";
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("jgid", jgid);
		//map_par.put("bqsb", bqsb);
		StringBuffer hql_fyhz = new StringBuffer();
		hql_fyhz.append("select a.YPGG as YFGG,a.YFDW as YFDW,sum(a.YPSL) as YCSL,a.YFSB as YFSB,a.YPXH as YPXH,a.YPCD as YPCD,d.CDMC as CDMC,b.YPMC as YPMC,b.YPSX as YPSX," +
				" a.YPDJ as YPDJ,sum(a.LSJE) as FYJE,e.XMMC as YPYF from YF_ZYFYMX a,YK_TYPK b,ZY_BRRY c,YK_CDDZ d,ZY_YPYF e  " +
				" where a.JGID=:jgid and a.LYBQ='"+bqsb+"' and a.YPXH=b.YPXH and a.ZYH=c.ZYH and a.JGID=c.JGID and a.YPCD=d.YPCD and a.YPSL>0 and b.GYFF=e.YPYF");
		if (body.get("dateFrom") != null) {
			hql_fyhz.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>='"+body.get("dateFrom")+"' ");
			//map_par.put("dateFrom", body.get("dateFrom") + "");
		}
		if (body.get("dateTo") != null) {
			hql_fyhz.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<='"+body.get("dateTo")+"' ");
			//map_par.put("dateTo", body.get("dateTo") + "");
		}
		if (body.get("FYFS") != null && !body.get("FYFS").equals("")) {
			hql_fyhz.append(" and a.FYFS='"+body.get("FYFS")+ "' ");
			//map_par.put("FYFS", MedicineUtils.parseInt(body.get("FYFS")));
		}
		if (body.get("YF") != null && !body.get("YF").equals("")) {
			hql_fyhz.append(" and a.YFSB='"+body.get("YF")+"' ");
			//map_par.put("YF", MedicineUtils.parseLong(body.get("YF")));
		}
		if (body.get("YPYF") != null&&!body.get("YPYF").equals("")) {
			hql_fyhz.append(" and e.YPYF='"+body.get("YPYF")+"' ");
			//body.put("YPYF", MedicineUtils.parseLong(body.get("YPYF")));
		}
		hql_fyhz.append(" group by a.YFSB,a.YPXH,a.YPCD,d.CDMC,b.YPMC,a.YPGG,b.YPSX,a.YFDW,a.YPDJ,e.XMMC ");
		MedicineCommonModel model = new MedicineCommonModel(dao);
		return model.getPageInfoRecord(req, map_par, hql_fyhz.toString(), null);
	}

	public Map<String, Object> queryCollectRecordsBackHZ(
			Map<String, Object> body, Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long bqsb = MedicineUtils.parseLong(user.getProperty("wardId"));
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("jgid", jgid);
		map_par.put("bqsb", bqsb);
		StringBuffer hql_fyhz = new StringBuffer();
		hql_fyhz.append("select a.YPGG as YFGG,a.YFDW as YFDW,sum(a.YPSL) as YCSL,a.YFSB as YFSB,a.YPXH as YPXH,a.YPCD as YPCD,d.CDMC as CDMC,b.YPMC as YPMC,b.YPSX as YPSX,a.YPDJ as YPDJ,sum(a.LSJE) as FYJE from YF_ZYFYMX a,YK_TYPK b,ZY_BRRY c,YK_CDDZ d  where a.JGID=:jgid and a.LYBQ=:bqsb and a.YPXH=b.YPXH and a.ZYH=c.ZYH and a.JGID=c.JGID and a.YPCD=d.YPCD and a.YPSL<0 ");
		if (body.get("dateFrom") != null) {
			hql_fyhz.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
			map_par.put("dateFrom", body.get("dateFrom") + "");
		}
		if (body.get("dateTo") != null) {
			hql_fyhz.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
			map_par.put("dateTo", body.get("dateTo") + "");
		}
		if (body.get("YF") != null && !body.get("YF").equals("")) {
			hql_fyhz.append(" and a.YFSB=:YF ");
			map_par.put("YF", MedicineUtils.parseLong(body.get("YF")));
		}
		hql_fyhz.append(" group by a.YFSB,a.YPXH,a.YPCD,d.CDMC,b.YPMC,a.YPGG,b.YPSX,a.YFDW,a.YPDJ");
		MedicineCommonModel model = new MedicineCommonModel(dao);
		return model.getPageInfoRecord(req, map_par, hql_fyhz.toString(), null);
	}
}
