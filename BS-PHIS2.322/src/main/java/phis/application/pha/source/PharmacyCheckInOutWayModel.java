package phis.application.pha.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

/**
 * 出入库方式维护model
 * 
 * @author caijy
 * 
 */
public class PharmacyCheckInOutWayModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyCheckInOutWayModel.class);

	public PharmacyCheckInOutWayModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 删除前验证是否被使用(入库方式和出库方式)
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verifiedUsing(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("keyValue", MedicineUtils.parseInt(body.get("keyValue")));
		map_par.put("PKName", "YFSB");
		map_par.put("PKValue", MedicineUtils.parseLong(body.get("yfsb")));
		String fspb = (String) body.get("fspb");
		if ("rk".equals(fspb)) {
			map_par.put("keyName", "RKFS");
			map_par.put("tableName", "YF_RK01");
		} else {
			map_par.put("keyName", "CKFS");
			map_par.put("tableName", "YF_CK01");
		}
		MedicineCommonModel model=new MedicineCommonModel(dao);
		if (model.repeatVerification(map_par)) {
			return MedicineUtils.getRetMap("记录已经被使用");
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 保存前验证名称是否存在
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> repeatInspection(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hqlWhere = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String mc = MedicineUtils.parseString(body.get("mc"));
		int pkey = MedicineUtils.parseInt(body.get("pkey"));
		String tableName = "";
		String pb = MedicineUtils.parseString(body.get("pb"));
		parameters.put("mc", mc);
		if ("rk".equals(pb)) {
			long yfsb = MedicineUtils.parseLong(body.get("yfsb"));
			parameters.put("yfsb", yfsb);
			tableName = "YF_RKFS";
			hqlWhere.append(" FSMC=:mc and YFSB=:yfsb");
			if (pkey != 0) {
				hqlWhere.append(" and RKFS!=:pkey");
				parameters.put("pkey", pkey);
			}
		} else if ("ck".equals(pb)) {
			long yfsb = MedicineUtils.parseLong(body.get("yfsb"));
			parameters.put("yfsb", yfsb);
			tableName = "YF_CKFS";
			hqlWhere.append(" FSMC=:mc and YFSB=:yfsb");
			if (pkey != 0) {
				hqlWhere.append(" and CKFS!=:pkey");
				parameters.put("pkey", pkey);
			}
		} else if ("yf".equals(pb)) {
			Long yfsb = MedicineUtils.parseLong(body.get("pkey"));
			String jgid = MedicineUtils.parseString(body.get("jgid"));
			parameters.put("jgid", jgid);
			tableName = "YF_YFLB";
			hqlWhere.append(" YFMC=:mc and JGID=:jgid");
			if (yfsb != 0) {
				hqlWhere.append(" and YFSB!=:yfsb");
				parameters.put("yfsb", yfsb);
			}
		}
		try {
			long l = dao.doCount(tableName, hqlWhere.toString(), parameters);
			if (l > 0) {
				return MedicineUtils.getRetMap("记录有重复");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询名称重复失败", e);
		}
		return MedicineUtils.getRetMap();
	}
}
