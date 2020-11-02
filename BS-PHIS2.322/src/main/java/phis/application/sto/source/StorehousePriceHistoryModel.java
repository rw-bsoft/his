package phis.application.sto.source;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
/**
 * 调价历史model
 * @author caijy
 *
 */
public class StorehousePriceHistoryModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehousePriceHistoryModel.class);

	public StorehousePriceHistoryModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-8
	 * @description 查询调价历史记录
	 * @updateInfo
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> queryPriceHistory(Map<String, Object> req)
			throws ModelDataOperationException {
		Map<String, Object> body = req.get("body") == null ? null: (Map<String, Object>) req.get("body");
		StringBuffer hql=new StringBuffer();
		hql.append("SELECT  b.YPMC as YPMC,  b.YPGG as YPGG, b.YPDW as YPDW, a.CDMC  as CDMC, c.JHJG as JHJG, c.LSJG as LSJG, b.PYDM as PYDM, " +
				"b.WBDM as WBDM, b.JXDM as JXDM, b.QTDM as QTDM, c.YPXH  as YPXH, c.YPCD as YPCD, b.YPDM as YPDM, b.YPSX as YPSX, b.TYPE as TYPE, " +
				"b.DJJB as DJJB, b.YPDC as YPDC,  b.FYFS as FYFS, b.TSYP as TSYP, b.ZBLB as ZBLB , b.ABC as ABC , d.YKZF as YKZF, c.ZFPB as ZFPB, c.PZWH as PZWH " +
				"FROM YK_CDDZ a, YK_TYPK b, YK_CDXX c, YK_YPXX d, YF_TJJL e " +
				"WHERE ( b.YPXH = c.YPXH ) and ( c.YPCD = a.YPCD ) and  ( b.YPXH = d.YPXH ) " +
				"and b.YPXH = e.YPXH and c.YPCD = e.YPCD and c.JGID = e.JGID and d.YKSB = e.YKSB " +
				"and c.JGID = d.JGID and ( c.JGID = :al_jgid ) and ( d.YKSB = :al_yksb ) ");
		if (body != null) {
			if (body.get("PYDM") != null && body.get("PYDM") != "") {
				hql.append(" and b.PYDM like '")
						.append(body.get("PYDM")).append("%' ");
			}
		}
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("al_jgid", UserRoleToken.getCurrent().getManageUnit().getId());
		map_par.put("al_yksb",MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId")) );
		MedicineCommonModel model=new MedicineCommonModel(dao);
		return model.getPageInfoRecord(req, map_par, hql.toString(), null);
	}
	
	/**
	 * 
	 * @author lizhi
	 * @createDate 2017-11-16
	 * @description 查询调价历史记录(药房)
	 * @updateInfo
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> queryPriceHistory2(Map<String, Object> req)
			throws ModelDataOperationException {
		Map<String, Object> body = req.get("body") == null ? null: (Map<String, Object>) req.get("body");
		StringBuffer hql=new StringBuffer();
		hql.append("SELECT  b.YPMC as YPMC,  b.YPGG as YPGG, b.YPDW as YPDW, a.CDMC  as CDMC, c.JHJG as JHJG, c.LSJG as LSJG, b.PYDM as PYDM, " +
				"b.WBDM as WBDM, b.JXDM as JXDM, b.QTDM as QTDM, c.YPXH  as YPXH, c.YPCD as YPCD, b.YPDM as YPDM, b.YPSX as YPSX, b.TYPE as TYPE, " +
				"b.DJJB as DJJB, b.YPDC as YPDC,  b.FYFS as FYFS, b.TSYP as TSYP, b.ZBLB as ZBLB , b.ABC as ABC , d.YKZF as YKZF, c.ZFPB as ZFPB, c.PZWH as PZWH " +
				"FROM YK_CDDZ a, YK_TYPK b, YK_CDXX c, YK_YPXX d, YF_TJJL e " +
				"WHERE ( b.YPXH = c.YPXH ) and ( c.YPCD = a.YPCD ) and  ( b.YPXH = d.YPXH ) " +
				"and b.YPXH = e.YPXH and c.YPCD = e.YPCD and c.JGID = e.JGID and d.YKSB = e.YKSB " +
				"and c.JGID = d.JGID and ( c.JGID = :al_jgid ) and ( e.YFSB = :al_yfsb ) ");
		if (body != null) {
			if (body.get("PYDM") != null && body.get("PYDM") != "") {
				hql.append(" and b.PYDM like '")
						.append(body.get("PYDM")).append("%' ");
			}
		}
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("al_jgid", UserRoleToken.getCurrent().getManageUnit().getId());
		map_par.put("al_yfsb",MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("pharmacyId")) );
		MedicineCommonModel model=new MedicineCommonModel(dao);
		return model.getPageInfoRecord(req, map_par, hql.toString(), null);
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-8
	 * @description 调价历史查询明细
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryPriceHistoryDetails(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		hql.append(" SELECT d.TJDH as TJDH, d.TJFS as TJFS, d.TJRQ as TJRQ, d.TJWH as TJWH, f.YJHJ as YJHJ, f.XJHJ as XJHJ, f.YLSJ as YLSJ, f.XLSJ as XLSJ, f.TJSL as TJSL, e.PERSONNAME as YGXM   FROM YK_TJ01 d, SYS_Personnel e, YK_TJ02 f WHERE ( d.XTSB = f.XTSB ) and  ( d.TJFS = f.TJFS ) and  ( d.TJDH = f.TJDH ) and ( d.XTSB = :ai_xtsb ) and ( f.YFSB = 0 ) and ( f.YPXH = :al_ypxh ) AND  ( f.YPCD = :al_ypcd ) AND ( e.PERSONID = d.ZXGH)  and d.jgid = :al_jgid ");
		List<Map<String, Object>> ret = null;
		try {
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("al_jgid", UserRoleToken.getCurrent().getManageUnit().getId());
				map_par.put("ai_xtsb", MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("storehouseId")));
				map_par
						.put("al_ypxh",MedicineUtils.parseLong(body.get("YPXH")));
				map_par
						.put("al_ypcd",MedicineUtils.parseLong(body.get("YPCD")));
				ret = dao.doSqlQuery(hql.toString(), map_par);
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "调价历史查询明细失败", e);
		}
		return ret;
	}
	
	/**
	 * 
	 * @author lizhi
	 * @createDate 2017-11-16
	 * @description 调价历史查询明细（药房）
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryPriceHistoryDetails2(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		hql.append(" SELECT d.TJDH as TJDH, d.TJFS as TJFS, d.TJRQ as TJRQ, d.TJWH as TJWH, f.YJHJ as YJHJ, f.XJHJ as XJHJ, f.YLSJ as YLSJ, f.XLSJ as XLSJ, f.TJSL as TJSL, e.PERSONNAME as YGXM " +
				"  FROM YK_TJ01 d, SYS_Personnel e, YK_TJ02 f, YF_TJJL a " +
				" WHERE ( d.XTSB = f.XTSB ) and  ( d.TJFS = f.TJFS ) and  ( d.TJDH = f.TJDH ) " +
				" and f.YPXH = a.YPXH and f.YPCD = a.YPCD and f.JGID = a.JGID and d.XTSB = a.YKSB "+
				" and (a.YFSB = :ai_yfsb ) and (f.YPXH = :al_ypxh ) AND  (f.YPCD = :al_ypcd ) AND (e.PERSONID = d.ZXGH) " +
				" and d.jgid = :al_jgid ");
		List<Map<String, Object>> ret = null;
		try {
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("al_jgid", UserRoleToken.getCurrent().getManageUnit().getId());
				map_par.put("ai_yfsb", MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("pharmacyId")));
				map_par
						.put("al_ypxh",MedicineUtils.parseLong(body.get("YPXH")));
				map_par
						.put("al_ypcd",MedicineUtils.parseLong(body.get("YPCD")));
				ret = dao.doSqlQuery(hql.toString(), map_par);
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "调价历史查询明细失败", e);
		}
		return ret;
	}
	
}
