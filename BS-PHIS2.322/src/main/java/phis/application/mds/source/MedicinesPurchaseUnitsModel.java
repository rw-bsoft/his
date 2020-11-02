package phis.application.mds.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
/**
 * 进货单位管理
 * @author caijy
 *
 */
public class MedicinesPurchaseUnitsModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(MedicinesPurchaseUnitsModel.class);
	public MedicinesPurchaseUnitsModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-26
	 * @description 进货单位保存前判断
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> inspectionPurchaseUnits(Map<String, Object> body) throws ModelDataOperationException {
		String op = body.get("op") + "";
		StringBuffer hql_repeat = new StringBuffer();// 查询名称有无重复
		Map<String, Object> map_par_repeat = new HashMap<String, Object>();
		hql_repeat.append(" DWMC=:dwmc");
		map_par_repeat.put("dwmc", body.get("dwmc") + "");
		try {
			if ("update".equals(op)) {
				hql_repeat.append(" and DWXH!=:dwxh");
				map_par_repeat.put("dwxh", MedicineUtils.parseLong(body.get("dwxh")));
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("dwxh", MedicineUtils.parseLong(body.get("dwxh")));
				if (MedicineUtils.parseInt(body.get("zfpb")) == 1) {
					StringBuffer hql_zfpb = new StringBuffer();// 判断有无作废
					hql_zfpb.append(" DWXH=:dwxh and ZFPB=1");
					long l_zfpb = dao.doCount("YK_JHDW",hql_zfpb.toString(), map_par);
					if (l_zfpb > 0) {
						return MedicineUtils.getRetMap("该供货单位已经作废，不能修改！");
					}
				}
				StringBuffer hql_rk = new StringBuffer();
				hql_rk.append("DWXH =:dwxh");
				long l_rk = dao.doCount("YK_RK01", hql_rk.toString(), map_par);
				if (l_rk > 0) {
					return MedicineUtils.getRetMap("该供货单位已经使用，不能进行修改！");
				}
			}
			long l = dao.doCount("YK_JHDW", hql_repeat.toString(), map_par_repeat);
			if (l > 0) {
				return MedicineUtils.getRetMap("同名单位已存在!");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "进货单位保存验证失败!", e);
		}
		return MedicineUtils.getRetMap();
	}
}
