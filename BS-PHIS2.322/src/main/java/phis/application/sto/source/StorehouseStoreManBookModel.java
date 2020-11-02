package phis.application.sto.source;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
/**
 * 保管员账簿model
 * @author caijy
 *
 */
public class StorehouseStoreManBookModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseStoreManBookModel.class);

	public StorehouseStoreManBookModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-8
	 * @description 保管员账簿列表查询
	 * @updateInfo
	 * @param req
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getStoreManBook(Map<String, Object> req)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Long yksb = Long.parseLong(body.get("yksb") + "");
		//zhaojian 2017-09-26 药库-保管员账册查询条件增加账簿类型
		Object zblb = body.get("zblb");
		Object pydm = body.get("pydm");
		List<Object> queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List<Object>)req.get("cnd");
		}
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("YKSB", yksb);
		map_par.put("JGID", jgid);
		StringBuilder hql = new StringBuilder();
		hql.append("select a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,b.CDMC as CDMC,sum(d.KCSL) as SWKC,c.KCSL as KCSL,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,a.YPDM as YPDM,e.KWBM as KWBM,c.YPXH as YPXH,c.YPCD as YPCD,e.YKZF as YKZF,a.ZBLB as ZBLB,c.ZFPB as ZFPB FROM YK_CDDZ b,YK_TYPK a,YK_YPXX e,YK_CDXX c left outer join YK_KCMX d on c.YPXH = d.YPXH and c.YPCD = d.YPCD and c.JGID = d.JGID  where e.YKSB=:YKSB and c.YPCD = b.YPCD  and a.YPXH = c.YPXH and e.JGID = c.JGID and a.YPXH = e.YPXH and e.JGID=:JGID");
		if(zblb!=null && !zblb.toString().equals("") && !zblb.toString().equals("0")){
			map_par.put("ZBLB", zblb);
			hql.append(" and a.ZBLB=:ZBLB");
		}
		if(pydm!=null && !pydm.toString().equals("")){
			hql.append(" and a.PYDM like '"+ pydm.toString().toUpperCase()+"%'");
		}
		/*if(queryCnd != null&&queryCnd.size()>0){
			try {
				hql.append(" and ").append(ExpressionProcessor.instance().toString(queryCnd));
			} catch (ExpException e) {
				MedicineUtils.throwsException(logger, "保管员账簿列表查询失败", e);
			}
		}*/
		hql.append(" group by a.YPMC,a.YPGG,a.YPDW,b.CDMC,c.KCSL,a.PYDM,a.WBDM,a.JXDM,a.QTDM,a.YPDM,e.KWBM,c.YPXH,c.YPCD,"
				+ "e.YKZF,a.ZBLB,c.ZFPB");
		hql.append(" ORDER BY a.YPMC");
		MedicineCommonModel model=new MedicineCommonModel(dao);
		return model.getPageInfoRecord(req, map_par, hql.toString(), null);
	}
	
}
