package phis.application.sto.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

public class StoreDepartmentConsumptionModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StoreDepartmentConsumptionModel.class);
	protected SchemaItem item_yk_kc = null;

	// protected SchemaItem item_yk_ls = null;
	public StoreDepartmentConsumptionModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-7-27
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-8-10
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-8-10
	 * @description 数据转换成int
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-8-20
	 * @description double型数据转换
	 * @updateInfo
	 * @param number
	 *            保留小数点后几位
	 * @param data
	 *            数据
	 * @return
	 */
	public double formatDouble(int number, double data) {
		if (number > 8) {
			return 0;
		}
		double x = Math.pow(10, number);
		return Math.round(data * x) / x;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 药库高低储提示查询
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doQuerySUM(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Long yksb = MedicineUtils.parseLong(UserRoleToken.getCurrent()
				.getProperty("storehouseId"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		parameters.put("yksb", yksb);
		StringBuffer hql = new StringBuffer();
		try {
			if (req.containsKey("cnd")) {
				List<Object> listCND = (List<Object>) req.get("cnd");
				String cnd = ExpressionProcessor.instance().toString(listCND);
				hql.append(
						" select a.CKKS as CKKS,e.OFFICENAME as KSMC from YK_CK01 a, YK_CK02 b,YK_CKFS c,YK_TYPK d,SYS_Office e where d.YPXH = b.YPXH and b.XTSB = a.XTSB and b.CKFS = a.CKFS and a.CKKS=e.ID and b.CKDH = a.CKDH and a.CKPB = 1 and a.XTSB = c.XTSB and a.CKFS = c.CKFS and c.KSPB = 1")
						.append(" and c.JGID = :JGID and c.XTSB=:yksb and ")
						.append(cnd);
			}
			if (req.get("zblb") != null && !req.get("zblb").equals("")) {
				hql.append(" and d.TYPE=:zblb ");
				parameters.put("zblb", Integer.parseInt(req.get("zblb") + ""));
			}
			hql.append(" and a.CKKS is not null GROUP BY a.CKKS,e.OFFICENAME ORDER BY a.CKKS ASC");
			List<Map<String, Object>> listSQL = dao.doQuery(hql.toString(),
					parameters);
			res.put("body", listSQL);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ExpException e) {
			e.printStackTrace();
		}

	}

}
