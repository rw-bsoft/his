package phis.application.sto.source;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;
import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
/**
 * 付款处理
 * @author caijy
 *
 */
public class StorehousePaymentModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehousePaymentModel.class);

	public StorehousePaymentModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 付款处理数据查询
	 * @updateInfo
	 * @param req
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> queryPayment(Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
			StringBuffer hql = new StringBuffer();
			hql.append("select c.DWMC as DWMC,b.DWXH as DWXH,sum(a.JHHJ) as JHZE,sum(a.PFJE) as PFZE,sum(a.LSJE) as LSZE,sum(a.LSJE)-sum(a.JHHJ) as JXCE,c.PYDM as PYDM FROM YK_RK02 a,YK_RK01  b,YK_JHDW c,YK_RKFS d WHERE b.XTSB = :YKSB and b.XTSB = d.XTSB and  b.RKFS = d.RKFS and a.XTSB = b.XTSB and a.RKFS = b.RKFS and a.RKDH = b.RKDH and b.DWXH = c.DWXH and a.FKRQ is null and a.YSRQ is not null AND b.JGID = a.JGID AND b.JGID = :JGID AND EXISTS (SELECT * fROM GY_XTPZ WHERE DMLB = 15000 AND PZBH = 1 AND d.DYFS = PZBH) GROUP BY b.DWXH,c.DWMC,c.PYDM");
			Map<String, Object> map_par = new HashMap<String, Object>();
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 获取JGID
			long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
			map_par.put("YKSB", yksb);
			map_par.put("JGID", jgid);
			hql.append(" order by b.DWXH");
			MedicineCommonModel model=new MedicineCommonModel(dao);
			return model.getPageInfoRecord(req, map_par, hql.toString(), null);
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 付款处理明细查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> queryPaymentDetails(Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
			long dwxh = Long.parseLong(req.get("DWXH") + "");
			StringBuffer hql = new StringBuffer();
			hql.append("select c.DWMC as DWMC,b.DWXH as DWXH,a.YSDH as YSDH,b.PWD as PWD,SUM(a.JHHJ) as JHZE,SUM(a.PFJE) as PFZE,SUM(a.LSJE) as LSZE,sum(a.LSJE)-sum(a.JHHJ) as JXCE,d.RKFS as RKFS,d.FSMC as FSMC,a.YSRQ as YSRQ,a.FKRQ as FKRQ,a.FPHM as FPHM,b.RKDH as RKDH,b.RKBZ as RKBZ,b.RKRQ as RKRQ from  YK_RK02 a,YK_RK01 b,YK_JHDW c,YK_RKFS d where b.XTSB = a.XTSB and b.RKFS = a.RKFS and b.RKDH = a.RKDH and b.DWXH = c.DWXH and b.DWXH = :DWXH and b.XTSB = :YKSB and b.XTSB = d.XTSB and b.RKFS = d.RKFS and a.FKRQ is null and a.YSRQ is not null and EXISTS (SELECT * FROM GY_XTPZ WHERE DMLB = 15000 and PZBH = 1 and d.DYFS = PZBH ) and b.JGID = a.JGID and b.JGID = :JGID GROUP BY c.DWMC,a.YSDH,b.PWD,b.DWXH,d.RKFS,d.FSMC,a.YSRQ,a.FKRQ,a.FPHM,b.RKDH,b.RKBZ,b.RKRQ");
			Map<String, Object> map_par = new HashMap<String, Object>();
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 获取JGID
			long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		    map_par.put("YKSB", yksb);
			map_par.put("JGID", jgid);
			map_par.put("DWXH", dwxh);
			hql.append(" order by a.FPHM");
			MedicineCommonModel model=new MedicineCommonModel(dao);
			return model.getPageInfoRecord(req, map_par, hql.toString(), "phis.application.sto.schemas.YK_FKCL02");
		}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 付款处理窗口
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> queryPaymentProcessing(Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Map<String,Object>> ret=null;
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT a.FPHM as FPHM,c.YPMC as YPMC,b.CDMC as CDMC,a.YSDH as YSDH,a.FKJE as FKJE,a.YFJE as YFJE,a.SBXH as SBXH,a.XTSB as XTSB,a.RKFS as RKFS,a.RKDH as RKDH,a.YPXH as YPXH,a.YPCD as YPCD,a.PZHM as PZHM,a.FKRQ as FKRQ,a.FKGH as FKGH,a.FKJE-a.YFJE as BCJE FROM YK_RK02 a,YK_CDDZ b,YK_TYPK c WHERE a.YPCD = b.YPCD and a.YPXH = c.YPXH and a.FPHM = :FPHM and a.RKFS = :RKFS and a.JGID = :JGID and a.XTSB = :XTSB and a.YSDH=:YSDH");
			Map<String, Object> map_par = new HashMap<String, Object>();
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 获取JGID
			long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
			map_par.put("FPHM", MedicineUtils.parseString(body.get("FPHM")));
			map_par.put("RKFS", MedicineUtils.parseInt(body.get("RKFS")));
			map_par.put("YSDH", MedicineUtils.parseInt(body.get("YSDH")));
			map_par.put("XTSB", yksb);
			map_par.put("JGID", jgid);
			ret= dao.doQuery(hql.toString(),
					map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "付款处理查询失败!", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-8
	 * @description 保存付款信息
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void updatePaymentProcessing(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> YK_FK01 = (Map<String, Object>) body.get("YK_FK01");
		List<Map<String, Object>> YK_FKJL_List = (List<Map<String, Object>>) body
				.get("YK_FKJL");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		String userId = user.getUserId();
		YK_FK01.put("JGID", jgid);
		YK_FK01.put("FKGH", userId);
		YK_FK01.put("FKSJ", new Date());
		YK_FK01.put("YKSB", yksb);
		try {
			Map<String, Object> re_YK_FK01 = dao.doSave("create",
					BSPHISEntryNames.YK_FK01, YK_FK01, false);
			long FKJL =MedicineUtils.parseLong(re_YK_FK01.get("FKJL"));
			String ls_pzhm = YK_FK01.get("PZHM") + "";
			for (int i = 0; i < YK_FKJL_List.size(); i++) {
				Map<String, Object> YK_RK02 = new HashMap<String, Object>();
				YK_RK02.put("PZHM", ls_pzhm + "@" + ls_pzhm);
				Map<String, Object> YK_FKJL = YK_FKJL_List.get(i);
				YK_RK02.put("SBXH", YK_FKJL.get("SBXH"));
				double YFJE =MedicineUtils.parseDouble(YK_FKJL.get("YFJE"))
						+MedicineUtils.parseDouble(YK_FKJL.get("BCJE"));
				YK_RK02.put("YFJE", YFJE);
				double FKJE = Double.parseDouble(YK_FKJL.get("FKJE") + "");
				if (YFJE == FKJE) {
					YK_RK02.put("FKRQ", new Date());
					YK_RK02.put("FKGH", userId);
				}
				YK_FKJL.put("JGID", jgid);
				YK_FKJL.put("FKJL", FKJL);
				YK_FKJL.put("XTSB", yksb);
				dao.doSave("create", BSPHISEntryNames.YK_FKJL, YK_FKJL, false);
				dao.doSave("update", BSPHISEntryNames.YK_RK02, YK_RK02, false);
			}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "付款处理失败!", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "付款处理失败!", e);
		}
	}
	
}
