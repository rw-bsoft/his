package phis.prints.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

public class MedicalRecordHomePageQueryFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		StringBuffer hql = new StringBuffer(
				"select a.YLJGMC as USERNAME,a.BAHM as BAH,a.ZYCS as ZYCS,(case a.YLFYDM when 1 then '01' when 2 then '02' when 3 then '03' when 4 then '04'  when 5 then '05' when 6 then '06' when 7 then '07' when 8 then '08' else '99' end ) as YLFKFS,a.JMJKKH as JKKH,a.BRXM as XM,a.BRXB as XB," +
				" a.CSNY as CSRQ,a.MZDM as MZ,a.SFZJHM as SFZH,(case a.HYDM when 1 then '10' when 2 then '20' when 3 then '30' when 4 then '40' else '90' end )  as HYZK,(case a.ZYDM when '0' then '11' when '1/2' then '13' when '3' then '17' when '4' then '51' when '5' then '27' when '9-9' then '24' when 'X' then '37' else '90' end) as ZYDM,'320117' as XZZ,a.XZZ_DH as DH,a.XZZ_YB as XZZYB," +
				" '320117' as HKDZ,a.HKDZ_YB as HKDZYB,case a.RYTJ when 0 then 9 else a.RYTJ end as RYTJ,a.RYRQ as RYRQ,'02' as RYKBDM,to_date(to_char(a.CYRQ,'yyyy-mm-dd'),'yyyy-mm-dd') as CYRQ,'02' as CYKBDM,a.SJZYYS as ZYTS," +
				" (select mszd from（select * from (select c.jzxh, c.mszd,row_number() over(partition by c.jzxh order by c.jlxh) rn from (select * from EMR_ZYZDJL b where b.zdlb = '11') c) where rn = 1）d  where a.jzxh = d.jzxh) as MJZZD,"+
				" '01'as JBDM,"+
				" '' as GBJBBM,"+
				" (select mszd from（select * from (select c.jzxh, c.mszd,row_number() over(partition by c.jzxh order by c.jlxh) rn from (select * from EMR_ZYZDJL b where b.zdlb = '51') c) where rn = 1）d  where a.jzxh = d.jzxh) as CYSZYZD,"+
				" '01' as JBBM,"+
				" '' as GBJBBM1,"+
				" (select RYBQDM from（select * from (select c.jzxh, c.RYBQDM,row_number() over(partition by c.jzxh order by c.jlxh) rn from (select * from EMR_ZYZDJL b where b.zdlb = '51') c) where rn = 1）d  where a.jzxh = d.jzxh) as RYBQ,"+
				" (select mszd from（select * from (select c.jzxh, c.mszd,row_number() over(partition by c.jzxh order by c.jlxh) rn from (select * from EMR_ZYZDJL b where b.zdlb = '51') c) where rn = 2）d  where a.jzxh = d.jzxh) as CYSQTZD,"+
				" '01' as JBDM1,"+
				" '' as GBJBBM2,"+
				" (select RYBQDM from（select * from (select c.jzxh, c.RYBQDM,row_number() over(partition by c.jzxh order by c.jlxh) rn from (select * from EMR_ZYZDJL b where b.zdlb = '51') c) where rn = 2）d  where a.jzxh = d.jzxh) as RYBQ1,"+
				" (select ssmc from（select * from (select b.jzxh,b.ssmc,row_number() over(partition by b.jzxh order by b.jlxh) rn from EMR_ZYSSJL b) where rn = 1）c where a.jzxh = c.jzxh) as ZYSSYCZMC,"+
				" '' as ZYSSYCZBM,"+
				" (select ssrq from（select * from (select b.jzxh,b.ssrq,row_number() over(partition by b.jzxh order by b.jlxh) rn from EMR_ZYSSJL b) where rn = 1）c where a.jzxh = c.jzxh) as SSJCZRQ,"+
				" (select ssmc from（select * from (select b.jzxh,b.ssmc,row_number() over(partition by b.jzxh order by b.jlxh) rn from EMR_ZYSSJL b) where rn = 2）c where a.jzxh = c.jzxh) as QTSSYCZMC,"+
				" '' as QTSSYCZBM,"+
				" (select ssrq from（select * from (select b.jzxh,b.ssrq,row_number() over(partition by b.jzxh order by b.jlxh) rn from EMR_ZYSSJL b) where rn = 2）c where a.jzxh = c.jzxh) as QTSSJCZRQ,"+
				" a.LYFS as LYFS,"+
				" (select case b.ZYZFY  when 0 then null else b.ZYZFY end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as ZFY,"+
				" (select case b.ZFJE  when 0 then null else b.ZFJE end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as QZZFJE,"+
				" (select case (b.YBYLFWF+b.YBZLCZF+b.HLF+b.QTFY) when 0 then null else (b.YBYLFWF+b.YBZLCZF+b.HLF+b.QTFY) end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as ZHYLFWL,"+
				" (select case b.YBYLFWF when 0 then null else b.YBYLFWF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as YBYLFWF,"+
				" (select case b.YBZLCZF when 0 then null else b.YBZLCZF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as YBZLCZFY,"+
				" (select case b.HLF when 0 then null else b.HLF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as HLF,"+
				" (select case b.QTFY when 0 then null else b.QTFY end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as QTFY,"+
				" (select  case (b.BLZDF+b.SYSZDF+b.YXXZDF+b.LCZDXMF) when 0 then null else round((b.BLZDF+b.SYSZDF+b.YXXZDF+b.LCZDXMF),1) end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as ZDL,"+
				" (select  case ( b.FSSZLXMF+b.SSZLF) when 0 then null else ( b.FSSZLXMF+b.SSZLF) end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as ZLL,"+
				" (select case b.FSSZLXMF when 0 then null else b.FSSZLXMF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as FSSZLXMF,"+
				" (select case b.SSZLF when 0 then null else b.SSZLF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as SSZLF,"+
				" (select case b.KFF when 0 then null else b.KFF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as KFL,"+
				" (select case b.ZYZLF when 0 then null else b.ZYZLF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as ZYL,"+
				" (select  case b.XYF when 0 then null else b.XYF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as XYL,"+
				" (select case b.XYF when 0 then null else b.XYF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as XYF,"+
				" (select  case b.KJYWFY  when 0 then null else b.KJYWFY end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as KJYWFY,"+
				" (select case ( b.ZCYF+b.ZCY) when 0 then null else (b.ZCYF+b.ZCY) end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as ZYLY,"+
				" (select  case b.ZCYF  when 0 then null else b.ZCYF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as ZCYFY,"+
				" (select case b.ZCY  when 0 then null else b.ZCY end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as ZCYF,"+
				" (select case (b.XF+b.BDBLZPF+b.QDBLZPF+b.NXYZLZPF+b.XBYZLZPF ) when 0 then null else (b.XF+b.BDBLZPF+b.QDBLZPF+b.NXYZLZPF+b.XBYZLZPF ) end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as XYHXYZPL,"+
				" (select case (b.JCYCLF+b.ZLYCLF+b.SSYCLF) when 0 then null else b.JCYCLF+b.ZLYCLF+b.SSYCLF end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as HCL,"+
				" (select case (b.ZYZFY-(b.YBYLFWF+b.YBZLCZF+b.HLF+b.QTFY)-(b.BLZDF+b.SYSZDF+b.YXXZDF+b.LCZDXMF)-(b.FSSZLXMF+b.SSZLF)-b.KFF-b.ZYZLF-b.XYF-(b.ZCYF+b.ZCY)-(b.XF+b.BDBLZPF+b.QDBLZPF+b.NXYZLZPF+b.XBYZLZPF)-(b.JCYCLF+b.ZLYCLF+b.SSYCLF)) when 0 then null else (b.ZYZFY-(b.YBYLFWF+b.YBZLCZF+b.HLF+b.QTFY)-(b.BLZDF+b.SYSZDF+b.YXXZDF+b.LCZDXMF)-(b.FSSZLXMF+b.SSZLF)-b.KFF-b.ZYZLF-b.XYF-(b.ZCYF+b.ZCY)-(b.XF+b.BDBLZPF+b.QDBLZPF+b.NXYZLZPF+b.XBYZLZPF)-(b.JCYCLF+b.ZLYCLF+b.SSYCLF)) end from EMR_BASY_FY b where a.JZXH = b.JZXH ) as QTL"+
				" from EMR_BASY a left join zy_brry z on a.jzxh=z.zyh and a.jgid=z.jgid and z.cypb='8'");
		hql.append(" where a.JGID=:JGID and a.cyrq>= to_date(' ");
		hql.append(request.get("kssj") + "");
		hql.append("','yyyy-mm-dd')");
		hql.append(" and a.cyrq<= to_date('");
		hql.append(request.get("jssj") + " 23:59:59");
		hql.append("','yyyy-mm-dd HH24:mi:ss')");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", JGID);
			hql.append(" order by a.JZXH desc");
			List<Map<String, Object>> yp_list = dao.doSqlQuery(hql.toString(),
					parameters);
			if (yp_list.size() > 0 && yp_list != null) {
				for (Map<String, Object> xm : yp_list) {
					Map<String, Object> map = new HashMap<String, Object>();
					if (xm.get("USERNAME") != null) {
					map.put("USERNAME", xm.get("USERNAME") + "");
					} else {
						map.put("USERNAME", "");
					}
					if (xm.get("BAH") != null) {
						map.put("BAH", xm.get("BAH") + "");
						} else {
							map.put("BAH", "");
						}
					if (xm.get("ZYCS") != null) {
						map.put("ZYCS", xm.get("ZYCS") + "");
						} else {
							map.put("ZYCS", "");
						}
					if (xm.get("YLFKFS") != null) {
						map.put("YLFKFS", xm.get("YLFKFS") + "");
						} else {
							map.put("YLFKFS", "");
						}
					if (xm.get("JKKH") != null) {
						map.put("JKKH", xm.get("JKKH") + "");
						} else {
							map.put("JKKH", "");
						}
					if (xm.get("XM") != null) {
						map.put("XM", xm.get("XM") + "");
						} else {
							map.put("XM", "");
						}
					if (xm.get("XB") != null) {
						map.put("XB", xm.get("XB") + "");
						} else {
							map.put("XB", "");
						}
					if (xm.get("CSRQ") != null) {
						map.put("CSRQ", xm.get("CSRQ") + "");
						} else {
							map.put("CSRQ", "");
						}
					if (xm.get("MZ") != null) {
						map.put("MZ", xm.get("MZ") + "");
						} else {
							map.put("MZ", "");
						}
					if (xm.get("SFZH") != null) {
						map.put("SFZH", xm.get("SFZH") + "");
						} else {
							map.put("SFZH", "");
						}
					if (xm.get("HYZK") != null) {
						map.put("HYZK", xm.get("HYZK") + "");
						} else {
							map.put("HYZK", "");
						}
					if (xm.get("ZYDM") != null) {
						map.put("ZYDM", xm.get("ZYDM") + "");
						} else {
							map.put("ZYDM", "");
						}
					if (xm.get("XZZ") != null) {
						map.put("XZZ", xm.get("XZZ") + "");
						} else {
							map.put("XZZ", "");
						}
					if (xm.get("DH") != null) {
						map.put("DH", xm.get("DH") + "");
						} else {
							map.put("DH", "");
						}
					if (xm.get("XZZYB") != null) {
						map.put("XZZYB", xm.get("XZZYB") + "");
						} else {
							map.put("XZZYB", "");
						}
					if (xm.get("HKDZ") != null) {
						map.put("HKDZ", xm.get("HKDZ") + "");
						} else {
							map.put("HKDZ", "");
						}
					if (xm.get("HKDZYB") != null) {
						map.put("HKDZYB", xm.get("HKDZYB") + "");
						} else {
							map.put("HKDZYB", "");
						}
					if (xm.get("RYTJ") != null) {
						map.put("RYTJ", xm.get("RYTJ") + "");
						} else {
							map.put("RYTJ", "");
						}
					if (xm.get("RYRQ") != null) {
						map.put("RYRQ", xm.get("RYRQ") + "");
						} else {
							map.put("RYRQ", "");
						}
					if (xm.get("RYKBDM") != null) {
						map.put("RYKBDM", xm.get("RYKBDM") + "");
						} else {
							map.put("RYKBDM", "");
						}
					if (xm.get("CYRQ") != null) {
						map.put("CYRQ", xm.get("CYRQ") + "");
						} else {
							map.put("CYRQ", "");
						}
					if (xm.get("CYKBDM") != null) {
						map.put("CYKBDM", xm.get("CYKBDM") + "");
						} else {
							map.put("CYKBDM", "");
						}
					if (xm.get("ZYTS") != null) {
						map.put("ZYTS", xm.get("ZYTS") + "");
						} else {
							map.put("ZYTS", "");
						}
					if (xm.get("MJZZD") != null) {
						map.put("MJZZD", xm.get("MJZZD") + "");
						} else {
							map.put("MJZZD", "");
						}
					if ((xm.get("JBBM") != null) && (xm.get("MJZZD") != null)) {
						map.put("JBBM", xm.get("JBBM") + "");
						} else {
							map.put("JBBM", "");
						}
					if (xm.get("GBJBBM") != null) {
						map.put("GBJBBM", xm.get("GBJBBM") + "");
						} else {
							map.put("GBJBBM", "");
						}
					if (xm.get("CYSZYZD") != null) {
						map.put("CYSZYZD", xm.get("CYSZYZD") + "");
						} else {
							map.put("CYSZYZD", "");
						}
					if ((xm.get("JBDM") != null) && (xm.get("CYSZYZD") != null) ) {
						map.put("JBDM", xm.get("JBDM") + "");
						} else {
							map.put("JBDM", "");
						}
					if (xm.get("GBJBBM1") != null) {
						map.put("GBJBBM1", xm.get("GBJBBM1") + "");
						} else {
							map.put("GBJBBM1", "");
						}
					if ((xm.get("RYBQ") != null) && (xm.get("CYSZYZD") != null) ) {
						map.put("RYBQ", xm.get("RYBQ") + "");
						} else {
							map.put("RYBQ", "");
						}
					if (xm.get("CYSQTZD") != null) {
						map.put("CYSQTZD", xm.get("CYSQTZD") + "");
						} else {
							map.put("CYSQTZD", "");
						}
					if (xm.get("JBDM1") != null) {
						map.put("JBDM1", xm.get("JBDM1") + "");
						} else {
							map.put("JBDM1", "");
						}
					if (xm.get("GBJBBM2") != null) {
						map.put("GBJBBM2", xm.get("GBJBBM2") + "");
						} else {
							map.put("GBJBBM2", "");
						}
					if ((xm.get("RYBQ1") != null) && (xm.get("CYSQTZD") != null)) {
						map.put("RYBQ1", xm.get("RYBQ1") + "");
						} else {
							map.put("RYBQ1", "");
						}
					if (xm.get("ZYSSYCZMC") != null) {
						map.put("ZYSSYCZMC", xm.get("ZYSSYCZMC") + "");
						} else {
							map.put("ZYSSYCZMC", "");
						}
					if ((xm.get("ZYSSYCZBM") != null) && (xm.get("ZYSSYCZMC") != null)) {
						map.put("ZYSSYCZBM", xm.get("ZYSSYCZBM") + "");
						} else {
							map.put("ZYSSYCZBM", "");
						}
					if ((xm.get("SSJCZRQ") != null) && (xm.get("ZYSSYCZMC") != null)) {
						map.put("SSJCZRQ", xm.get("SSJCZRQ") + "");
						} else {
							map.put("SSJCZRQ", "");
						}
					if (xm.get("QTSSYCZMC") != null) {
						map.put("QTSSYCZMC", xm.get("QTSSYCZMC") + "");
						} else {
							map.put("QTSSYCZMC", "");
						}
					if ((xm.get("QTSSYCZBM") != null) && (xm.get("QTSSYCZMC") != null)) {
						map.put("QTSSYCZBM", xm.get("QTSSYCZBM") + "");
						} else {
							map.put("QTSSYCZBM", "");
						}
					if ((xm.get("QTSSJCZRQ") != null) && (xm.get("QTSSYCZMC") != null)) {
						map.put("QTSSJCZRQ", xm.get("QTSSJCZRQ") + "");
						} else {
							map.put("QTSSJCZRQ", "");
						}
					if (xm.get("LYFS") != null) {
						map.put("LYFS", xm.get("LYFS") + "");
						} else {
							map.put("LYFS", "");
						}
					if (xm.get("ZFY") != null) {
						map.put("ZFY", xm.get("ZFY") + "");
						} else {
							map.put("ZFY", "");
						}
					if (xm.get("QZZFJE") != null) {
						map.put("QZZFJE", xm.get("QZZFJE") + "");
						} else {
							map.put("QZZFJE", "");
						}
					if (xm.get("ZHYLFWL") != null) {
						map.put("ZHYLFWL", xm.get("ZHYLFWL") + "");
						} else {
							map.put("ZHYLFWL", "");
						}
					if (xm.get("YBYLFWF") != null) {
						map.put("YBYLFWF", xm.get("YBYLFWF") + "");
						} else {
							map.put("YBYLFWF", "");
						}
					if (xm.get("YBZLCZFY") != null) {
						map.put("YBZLCZFY", xm.get("YBZLCZFY") + "");
						} else {
							map.put("YBZLCZFY", "");
						}
					if (xm.get("HLF") != null) {
						map.put("HLF", xm.get("HLF") + "");
						} else {
							map.put("HLF", "");
						}
					if (xm.get("QTFY") != null) {
						map.put("QTFY", xm.get("QTFY") + "");
						} else {
							map.put("QTFY", "");
						}
					if (xm.get("ZDL") != null) {
						map.put("ZDL", xm.get("ZDL") + "");
						} else {
							map.put("ZDL", "");
						}
					if (xm.get("ZLL") != null) {
						map.put("ZLL", xm.get("ZLL") + "");
						} else {
							map.put("ZLL", "");
						}
					if (xm.get("FSSZLXMF") != null) {
						map.put("FSSZLXMF", xm.get("FSSZLXMF") + "");
						} else {
							map.put("FSSZLXMF", "");
						}
					if (xm.get("SSZLF") != null) {
						map.put("SSZLF", xm.get("SSZLF") + "");
						} else {
							map.put("SSZLF", "");
						}
					if (xm.get("KFL") != null) {
						map.put("KFL", xm.get("KFL") + "");
						} else {
							map.put("KFL", "");
						}
					if (xm.get("ZYL") != null) {
						map.put("ZYL", xm.get("ZYL") + "");
						} else {
							map.put("ZYL", "");
						}
					if (xm.get("XYL") != null) {
						map.put("XYL", xm.get("XYL") + "");
						} else {
							map.put("XYL", "");
						}
					if (xm.get("XYF") != null) {
						map.put("XYF", xm.get("XYF") + "");
						} else {
							map.put("XYF", "");
						}
					if (xm.get("KJYWFY") != null) {
						map.put("KJYWFY", xm.get("KJYWFY") + "");
						} else {
							map.put("KJYWFY", "");
						}
					if (xm.get("ZYLY") != null) {
						map.put("ZYLY", xm.get("ZYLY") + "");
						} else {
							map.put("ZYLY", "");
						}
					if (xm.get("ZCYFY") != null) {
						map.put("ZCYFY", xm.get("ZCYFY") + "");
						} else {
							map.put("ZCYFY", "");
						}
					if (xm.get("ZCYF") != null) {
						map.put("ZCYF", xm.get("ZCYF") + "");
						} else {
							map.put("ZCYF", "");
						}
					if (xm.get("XYHXYZPL") != null) {
						map.put("XYHXYZPL", xm.get("XYHXYZPL") + "");
						} else {
							map.put("XYHXYZPL", "");
						}
					if (xm.get("HCL") != null) {
						map.put("HCL", xm.get("HCL") + "");
						} else {
							map.put("HCL", "");
						}
					if (xm.get("QTL") != null) {
						map.put("QTL", xm.get("QTL") + "");
						} else {
							map.put("QTL", "");
						}
					records.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	public int parseInteger(Object o) {
		if (o == null) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}


	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		// TODO Auto-generated method stub
		
	}
}
