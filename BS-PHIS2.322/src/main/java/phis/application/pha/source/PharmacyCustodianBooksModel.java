package phis.application.pha.source;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;

public class PharmacyCustodianBooksModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyCustodianBooksModel.class);

	public PharmacyCustodianBooksModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-9
	 * @description 药房保管员账簿明细List查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, Object>> queryPharmacyCustodianBooksDetail(
			Map<String, Object> body) throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		try {
			String qmrq=MedicineUtils.parseString(body.get("ZZSJ")).substring(0,10);
			Date kssj = sdf.parse(body.get("KSSJ") + "");
			Date zzsj = sdf.parse(body.get("ZZSJ") + "");
			double qmsl=MedicineUtils.parseDouble(body.get("QMSL"));
			double qmje=MedicineUtils.parseDouble(body.get("QMJE"));
			long ypxh = MedicineUtils.parseLong(body.get("YPXH"));
			long ypcd = MedicineUtils.parseLong(body.get("YPCD"));
			StringBuffer hql_yfbz = new StringBuffer();// 查询药房包装
			StringBuffer hql_syjc = new StringBuffer();// 统计上月结存
			StringBuffer hql_rksl = new StringBuffer();// 统计入库数量
			StringBuffer hql_cksl = new StringBuffer();// 统计出库数量
			StringBuffer hql_fysl = new StringBuffer();// 统计发药数量
			StringBuffer hql_yk = new StringBuffer();// 统计盈盈亏数据
			StringBuffer hql_bqfy = new StringBuffer();// 统计病区发药
			StringBuffer hql_jcfy = new StringBuffer();// 统计家床发药
			StringBuffer hql_dbrk = new StringBuffer();// 统计调拨入库
			StringBuffer hql_dbck = new StringBuffer();// 统计调拨出库
			StringBuffer hql_ypsl = new StringBuffer();// 统计药品申领和退药
			hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh");
			hql_syjc.append("select '期初结存' as BZ, a.KCSL as SL,a.LSJE as JE,a.YFBZ as YFBZ,to_char(b.ZZSJ,'yyyy-mm-dd') as RQ,to_char(b.ZZSJ,'yyyymmdd') as PX from YF_YJJG a,YF_JZJL b where a.YFSB=b.YFSB and a.CWYF=b.CWYF and a.YFSB=:yfsb  and b.ZZSJ=:begin and a.YPXH=:ypxh and a.YPCD=:ypcd");
			hql_rksl.append("select '药房入库' as BZ, b.RKSL as SL,b.LSJE as JE,b.YFBZ as YFBZ,to_char(a.RKRQ,'yyyy-mm-dd') as RQ,to_char(a.RKRQ,'yyyymmdd') as PX  from YF_RK01 a,YF_RK02 b  where a.YFSB =b.YFSB and a.RKFS =b.RKFS  and a.RKDH =b.RKDH and a.RKPB =1 and a.RKRQ >:begin and a.RKRQ <=:end and a.YFSB=:yfsb and b.YPXH=:ypxh and  b.YPCD=:ypcd");
			hql_cksl.append("select '药房出库' as BZ, -b.CKSL as SL,-b.LSJE as JE,b.YFBZ as YFBZ,to_char(a.CKRQ,'yyyy-mm-dd') as RQ,to_char(a.CKRQ,'yyyymmdd') as PX from YF_CK01 a,YF_CK02 b  where a.YFSB =b.YFSB and a.CKFS =b.CKFS  and a.CKDH =b.CKDH and a.CKPB =1 and a.CKRQ >:begin and a.CKRQ <=:end and a.YFSB=:yfsb and b.YPXH=:ypxh and  b.YPCD=:ypcd");
			hql_fysl.append("select (case when YPSL>0 then '药房发药' else '药房退药' end) as BZ,   -YPSL as SL,  -LSJE as JE,  YFBZ as YFBZ,to_char(  FYRQ,'yyyy-mm-dd') as RQ,to_char(  FYRQ,'yyyymmdd') as PX from YF_MZFYMX  where YFSB=:yfsb and FYRQ >:begin and FYRQ <=:end and YPXH=:ypxh and YPCD=:ypcd ");
			hql_yk.append("select '药房盘点' as BZ,(SPSL-PQSL) as SL,(XLSE-YLSE) as JE,b.YFBZ as YFBZ,to_char(a.WCRQ,'yyyy-mm-dd') as RQ,to_char(a.WCRQ,'yyyymmdd') as PX from YF_YK01 a,YF_YK02 b where a.YFSB=b.YFSB and a.CKBH=b.CKBH and a.PDDH=b.PDDH and a.PDWC=1 and a.WCRQ >:begin and a.WCRQ <=:end  and a.YFSB=:yfsb and b.YPXH=:ypxh and b.YPCD=:ypcd");
			hql_bqfy.append("select (case when YPSL>0 then '病区发药' else '病区退药' end)  as BZ,   -YPSL as SL,  -LSJE as JE,  YFBZ as YFBZ,to_char(  JFRQ,'yyyy-mm-dd') as RQ,to_char(  JFRQ,'yyyymmdd') as PX from YF_ZYFYMX where YFSB=:yfsb and JFRQ >:begin and JFRQ <=:end and YPXH=:ypxh and YPCD=:ypcd ");
			hql_jcfy.append("select (case when YPSL>0 then '家床发药' else '家床退药' end)  as BZ,   -YPSL as SL,  -LSJE as JE,  YFBZ as YFBZ,to_char(  JFRQ,'yyyy-mm-dd') as RQ,to_char(  JFRQ,'yyyymmdd') as PX from YF_JCFYMX where YFSB=:yfsb and JFRQ >:begin and JFRQ <=:end and YPXH=:ypxh and YPCD=:ypcd ");
			hql_dbrk.append("select '调拨入库' as BZ, b.QRSL as SL,b.LSJE as JE,b.YFBZ as YFBZ,to_char(a.RKRQ,'yyyy-mm-dd') as RQ,to_char(a.RKRQ,'yyyymmdd') as PX from YF_DB01  a,YF_DB02  b  where  a.SQYF = b.SQYF and a.SQDH = b.SQDH  and a.RKBZ = 1 and a.SQYF=:yfsb and a.RKRQ >:begin and a.RKRQ <=:end and b.YPXH=:ypxh and  b.YPCD=:ypcd  ");
			hql_dbck.append("select '调拨出库' as BZ, -b.QRSL as SL,-b.LSJE as JE,b.YFBZ as YFBZ,to_char(a.CKRQ,'yyyy-mm-dd') as RQ,to_char(a.CKRQ,'yyyymmdd') as PX from YF_DB01 a,YF_DB02 b  where  a.SQYF = b.SQYF and a.SQDH = b.SQDH  and a.CKBZ = 1 and a.MBYF=:yfsb and a.CKRQ >:begin and a.CKRQ <=:end and b.YPXH=:ypxh and  b.YPCD=:ypcd  ");
			hql_ypsl.append("select '药品申领' as BZ, b.SFSL as SL,b.LSJE as JE,c.ZXBZ as YFBZ,to_char(a.LYRQ,'yyyy-mm-dd') as RQ,to_char(a.LYRQ,'yyyymmdd') as PX from YK_CK01 a,YK_CK02 b,YK_TYPK  c  where b.YPXH=c.YPXH and a.XTSB = b.XTSB and a.CKFS = b.CKFS  and a.CKDH = b.CKDH and a.LYPB = 1 and a.LYRQ >:begin  and a.LYRQ <=:end and a.YFSB =:yfsb and b.YPXH=:ypxh and  b.YPCD=:ypcd ");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("ypxh", ypxh);
			Map<String, Object> map_yfbz = dao.doLoad(hql_yfbz.toString(),
					map_par);
			if (map_yfbz == null || map_yfbz.size() == 0) {
				return list_ret;
			}
			int yfbz = MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
			StringBuffer hql = new StringBuffer();
			hql.append(hql_syjc).append(" union all ").append(hql_rksl)
					.append(" union all ").append(hql_cksl)
					.append(" union all ").append(hql_fysl)
					.append(" union all ").append(hql_yk)
					.append(" union all ").append(hql_bqfy)
					.append(" union all ").append(hql_jcfy)
					.append(" union all ").append(hql_dbrk)
					.append(" union all ").append(hql_dbck)
					.append(" union all ").append(hql_ypsl);
			map_par.put("ypcd", ypcd);
			map_par.put("begin", kssj);
			map_par.put("end", zzsj);
			List<Map<String,Object>> list_sl=dao.doSqlQuery(hql.toString(), map_par);
			if(list_sl==null||list_sl.size()==0){
				return list_ret;
			}
			for(Map<String,Object> map_sl:list_sl){
				if(MedicineUtils.parseDouble(map_sl.get("SL"))==0){
					continue;
				}
				map_sl.put("SL", MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(map_sl.get("SL"))*MedicineUtils.parseInt(map_sl.get("YFBZ"))/yfbz));
				list_ret.add(map_sl);
			}
			Collections.sort(list_ret, new Comparator() {
				@Override
				public int compare(Object o1, Object o2) {
					return MedicineUtils.parseInt(((Map<String,Object>)o1).get("PX"))-MedicineUtils.parseInt(((Map<String,Object>)o2).get("PX"));
				}
			});
			Map<String,Object> m=new HashMap<String,Object>();
			m.put("RQ", qmrq);
			m.put("BZ", "期末结存");
			m.put("SL", qmsl);
			m.put("JE", qmje);
			list_ret.add(m);
		} catch (Exception e) {
			MedicineUtils.throwsException(logger, "查询失败", e);
		}
		return list_ret;
	}
}
