package phis.application.pha.source;

/**
 * 药房发药统计组装sql工具类
 * @(#)AddTableDictionary.java Created on 2013-10-15 下午4:59:32
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */ 

import java.util.Map;  
import phis.source.utils.BSPHISUtil;  
public class PharmacyDispensingStatisticsUtil {
	/**
	 * 组装窗口统计方式sql
	 * @param body
	 * 		存放YPLX 固定数据：0 全部  1西药 2中成药 3草药       为空时默认查询全部
	 * @return
	 */
	public String CKStatisticalMethod(Map<String, Object> body){
		int ii_yplx = body.get("YPLX") == null ? 0 : Integer.parseInt(body.get("YPLX") + "");
		String format = "YYYY-MM-DD HH24:MI:SS";
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT '' AS YPMC, 0 AS YPXH, '' AS YPGG, '' AS YPDW, a.FYCK AS FYCK,");
//		sqlBuilder.append(" a.CFLX AS CFLX, COUNT(DISTINCT(a.CFSB)) AS CFZS, sum(b.YPSL*b.CFTS) as FYSL,");
		sqlBuilder.append(" COUNT(DISTINCT(a.CFSB)) AS CFZS, sum(b.YPSL*b.CFTS) as FYSL,");//去掉a.CFLX AS CFLX,分组中也去掉
		sqlBuilder.append(" sum(b.HJJE) as FYJE FROM  MS_CF01 a, MS_CF02 b WHERE a.CFSB = b.CFSB AND");
		sqlBuilder.append(" a.ZFPB = 0  AND a.JGID = :al_jgid AND a.YFSB = :al_yfsb AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" >= :adt_start AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format));
		sqlBuilder.append(" <= :adt_end ");
		if(ii_yplx != 0){//固定数据：0 全部  1西药 2中成药 3草药
			sqlBuilder.append(" AND a.CFLX = ").append(ii_yplx);
		}
		sqlBuilder.append(" GROUP BY a.FYCK ");
		return sqlBuilder.toString();
	}
	/**
	 * 组装窗口统计明细sql
	 * @param body
	 * 		存放YPLX 固定数据：0 全部  1西药 2中成药 3草药       为空时默认查询全部
	 * @return
	 */
	public String CKStatisticalDetails(Map<String, Object> body){
		int ii_yplx = body.get("YPLX") == null ? 0 : Integer.parseInt(body.get("YPLX") + "");
		String format = "YYYY-MM-DD HH24:MI:SS";
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT c.YPMC as YPMC, c.YPXH as YPXH, c.YPGG as YPGG, c.YPDW as YPDW,");
		sqlBuilder.append(" a.FYCK as FYCK, a.CFLX as CFLX, 0 as CFZS, sum(b.YPSL*b.CFTS) as FYSL,");
		sqlBuilder.append(" sum(b.HJJE) as FYJE,sum(b.YPSL*b.CFTS*d.JHJG) as JHJE,d.JHJG as JHJG FROM  MS_CF01 a, MS_CF02 b, YK_TYPK c,YK_CDXX d");
		sqlBuilder.append(" WHERE d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0 and a.FYRQ is not null and a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0 AND");
		sqlBuilder.append(" a.JGID = :al_jgid AND a.YFSB = :al_yfsb AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" >= :adt_start AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" <= :adt_end");
		sqlBuilder.append(" AND a.FYCK = ").append(body.get("VIRTUAL_FIELD"));
		if(ii_yplx != 0){//固定数据：0 全部  1西药 2中成药 3草药
			sqlBuilder.append(" AND a.CFLX = ").append(ii_yplx);
		}
		if(body.get("PYDM") != null){
			sqlBuilder.append(" AND c.PYDM like '").append(body.get("PYDM")).append("%' ");
		}
		sqlBuilder.append(" GROUP BY c.YPMC, c.YPXH, c.YPGG, c.YPDW, a.FYCK, a.CFLX ,d.JHJG");
		return sqlBuilder.toString();
	}
	
	/**
	 * 组装窗口数据查询sql
	 * @return
	 */
	public String CKDataSql(){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT CKBH as CKBH,CKMC as CKMC FROM YF_CKBH  WHERE JGID = :al_jgid AND YFSB = :al_yfsb");
		return sqlBuilder.toString();

	}
	/**
	 * 组装病人性质统计方式sql
	 * @param body
	 * 		存放YPLX 固定数据：0 全部  1西药 2中成药 3草药       为空时默认查询全部
	 * @return
	 */
	public String BRXZStatisticalMethod(Map<String, Object> body){
		int ii_yplx = body.get("YPLX") == null ? 0 : Integer.parseInt(body.get("YPLX") + "");
		String format = "YYYY-MM-DD HH24:MI:SS";
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT '' AS YPMC, 0 AS YPXH, '' AS YPGG, '' AS YPDW, c.BRXZ AS BRXZ,");
		sqlBuilder.append(" COUNT(DISTINCT(a.CFSB)) AS CFZS, sum(b.YPSL*b.CFTS) as FYSL, sum(b.HJJE) as FYJE ");
		sqlBuilder.append(" FROM  MS_CF01 a, MS_CF02 b, MS_BRDA c WHERE c.BRID = a.BRID AND a.CFSB = b.CFSB AND");
		sqlBuilder.append(" a.ZFPB = 0  AND a.JGID = :al_jgid AND a.YFSB = :al_yfsb AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" >= :adt_start AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" <= :adt_end");
		if(ii_yplx != 0){//固定数据：0 全部  1西药 2中成药 3草药
			sqlBuilder.append(" AND a.CFLX = ").append(ii_yplx);
		}
		sqlBuilder.append(" GROUP BY c.BRXZ");

		return sqlBuilder.toString();
	}
	/**
	 * 组装病人性质统计明细sql
	 * @param body
	 * 		存放YPLX 固定数据：0 全部  1西药 2中成药 3草药       为空时默认查询全部
	 * @return
	 */
	public String BRXZStatisticalDetails(Map<String, Object>  body){
		int ii_yplx = body.get("YPLX") == null ? 0 : Integer.parseInt(body.get("YPLX") + "");
		String format = "YYYY-MM-DD HH24:MI:SS";
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT c.YPMC as YPMC, c.YPXH as YPXH, c.YPGG as YPGG, c.YPDW as YPDW, d.BRXZ as BRXZ,");
		sqlBuilder.append(" a.CFLX as CFLX, 0 as CFZS, sum(b.YPSL*b.CFTS) as FYSL, sum(b.HJJE) as FYJE,sum(b.YPSL*b.CFTS*e.JHJG) as JHJE,e.JHJG as JHJG  ");
		sqlBuilder.append(" FROM  MS_CF01 a,MS_CF02 b,YK_TYPK c,MS_BRDA d,YK_CDXX e WHERE e.YPCD=b.YPCD and e.YPXH=b.YPXH and e.JGID=b.JGID and a.FYBZ!=0 and a.FYRQ is not null and d.BRID = a.BRID AND a.CFSB = b.CFSB AND");
		sqlBuilder.append(" c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.JGID = :al_jgid AND a.YFSB = :al_yfsb AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" >= :adt_start AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" <= :adt_end");
		sqlBuilder.append(" AND d.BRXZ = ").append(body.get("VIRTUAL_FIELD"));
		if(ii_yplx != 0){//固定数据：0 全部  1西药 2中成药 3草药
			sqlBuilder.append(" AND a.CFLX = ").append(ii_yplx);
		}
		if(body.get("PYDM") != null){
			sqlBuilder.append(" AND c.PYDM like '").append(body.get("PYDM")).append("%' ");
		}
		sqlBuilder.append(" GROUP BY c.YPMC, c.YPXH, c.YPGG, c.YPDW, d.BRXZ, a.CFLX,e.JHJG");

		return sqlBuilder.toString();
	}
	/**
	 * 组装病人性质数据查询sql
	 * @return
	 */
	public String BRXZDataSql(){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT XZMC as XZMC, PYDM as PYDM, BRXZ as BRXZ FROM GY_BRXZ ");
		return sqlBuilder.toString();
	}
	
	/**
	 * 组装特殊药品统计方式sql
	 * @param body
	 * 		存放YPLX 固定数据：0 全部  1西药 2中成药 3草药       为空时默认查询全部
	 * @return
	 */
	public String TSYPStatisticalMethod(Map<String, Object> body){
		int ii_yplx = body.get("YPLX") == null ? 0 : Integer.parseInt(body.get("YPLX") + "");
		String format = "YYYY-MM-DD HH24:MI:SS";
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT '' AS YPMC, 0 AS YPXH, '' AS YPGG, '' AS YPDW, c.TSYP AS TSYP,");
//		sqlBuilder.append(" a.CFLX AS CFLX, COUNT(DISTINCT(a.CFSB)) AS CFZS, sum(b.YPSL*b.CFTS) as FYSL,");
		sqlBuilder.append("  COUNT(DISTINCT(a.CFSB)) AS CFZS, sum(b.YPSL*b.CFTS) as FYSL,");//去掉a.CFLX AS CFLX,分组中也去掉
		sqlBuilder.append(" sum(b.HJJE) as FYJE FROM  MS_CF01 a, MS_CF02 b, YK_TYPK c");
		sqlBuilder.append(" WHERE a.CFSB = b.CFSB AND c.YPXH = b.YPXH AND a.ZFPB = 0  AND a.JGID = :al_jgid AND a.YFSB = :al_yfsb AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" >= :adt_start AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" <= :adt_end");
		if(ii_yplx != 0){//固定数据：0 全部  1西药 2中成药 3草药
			sqlBuilder.append(" AND a.CFLX = ").append(ii_yplx);
		}
		sqlBuilder.append(" GROUP BY c.TSYP");

		return sqlBuilder.toString();
	}
	
	/**
	 * 组装特殊药品统计明细sql
	 * @param body
	 * 		存放YPLX 固定数据：0 全部  1西药 2中成药 3草药       为空时默认查询全部
	 * @return
	 */
	public String TSYPStatisticalDetails(Map<String, Object> body){
		int ii_yplx = body.get("YPLX") == null ? 0 : Integer.parseInt(body.get("YPLX") + "");
		String format = "YYYY-MM-DD HH24:MI:SS";
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT c.YPMC as YPMC, c.YPXH as YPXH, c.YPGG as YPGG, c.YPDW as YPDW, c.TSYP as TSYP,");
		sqlBuilder.append(" a.CFLX as CFLX, 0 as CFZS, sum(b.YPSL*b.CFTS) as FYSL, sum(b.HJJE) as FYJE,sum(b.YPSL*b.CFTS*d.JHJG) as JHJE ,d.JHJG as JHJG ");
		sqlBuilder.append(" FROM MS_CF01 a,MS_CF02 b,YK_TYPK c,YK_CDXX d WHERE d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0 and a.FYRQ is not null and a.CFSB = b.CFSB AND");
		sqlBuilder.append(" c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.JGID = :al_jgid AND a.YFSB = :al_yfsb AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" >= :adt_start AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" <= :adt_end");
		sqlBuilder.append(" AND c.TSYP = ").append(body.get("VIRTUAL_FIELD"));
		if(ii_yplx != 0){//固定数据：0 全部  1西药 2中成药 3草药
			sqlBuilder.append(" AND a.CFLX = ").append(ii_yplx);
		}
		if(body.get("PYDM") != null){
			sqlBuilder.append(" AND c.PYDM like '").append(body.get("PYDM")).append("%' ");
		}
		sqlBuilder.append(" GROUP BY c.YPMC, c.YPXH, c.YPGG, c.YPDW, c.TSYP, a.CFLX,d.JHJG");

		return sqlBuilder.toString();
	}
	/**
	 * 组装特殊药品数据查询sql
	 * 			没有使用，设计有问题
	 * @return
	 */
	public String TSYPDataSql(){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT DMSB as DMSB,DMMC as DMMC FROM GY_DMZD WHERE DMSB <> 0 AND DMLB = 16 ");
		return sqlBuilder.toString();
	}
	
	/**
	 * 组装开单科室统计方式sql
	 * @param body
	 * 		存放YPLX 固定数据：0 全部  1西药 2中成药 3草药       为空时默认查询全部
	 * @return
	 */
	public String KDKSStatisticalMethod(Map<String, Object> body){
		int ii_yplx = body.get("YPLX") == null ? 0 : Integer.parseInt(body.get("YPLX") + "");
		String format = "YYYY-MM-DD HH24:MI:SS";
		StringBuilder sqlBuilder = new StringBuilder();
//		sqlBuilder.append("SELECT '' as YPMC, 0 as YPXH, '' as YPGG, '' as YPDW, a.KSDM as KSDM, a.CFLX as CFLX, ");
		sqlBuilder.append("SELECT '' as YPMC, 0 as YPXH, '' as YPGG, '' as YPDW, a.KSDM as KSDM, ");//去掉a.CFLX AS CFLX,分组中也去掉
		sqlBuilder.append(" COUNT(DISTINCT(a.CFSB)) as CFZS, sum(b.YPSL*b.CFTS) as FYSL, sum(b.HJJE) as FYJE");
		// YK_TYPK.YPXH = b.YPXH AND
		sqlBuilder.append(" FROM  MS_CF01 a, MS_CF02 b WHERE a.CFSB = b.CFSB AND a.ZFPB = 0  AND");
		sqlBuilder.append(" a.JGID = :al_jgid AND a.YFSB = :al_yfsb AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" >= :adt_start AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" <= :adt_end");
		if(ii_yplx != 0){//固定数据：0 全部  1西药 2中成药 3草药
			sqlBuilder.append(" AND a.CFLX = ").append(ii_yplx);
		}
		sqlBuilder.append(" GROUP BY a.KSDM ");

		return sqlBuilder.toString();
	}
	
	/**
	 * 组装开单科室统计明细sql
	 * @param body
	 * 		存放YPLX 固定数据：0 全部  1西药 2中成药 3草药       为空时默认查询全部
	 * @return
	 */
	public String KDKSStatisticalDetails(Map<String, Object> body){
		int ii_yplx = body.get("YPLX") == null ? 0 : Integer.parseInt(body.get("YPLX") + "");
		String format = "YYYY-MM-DD HH24:MI:SS";
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT c.YPMC as YPMC, c.YPXH as YPXH, c.YPGG as YPGG, c.YPDW as YPDW, a.KSDM as KSDM,");
		sqlBuilder.append(" a.CFLX as CFLX, 0 as CFZS, sum(b.YPSL*b.CFTS) as FYSL, sum(b.HJJE) as FYJE,sum(b.YPSL*b.CFTS*d.JHJG) as JHJE,d.JHJG as JHJG  ");
		sqlBuilder.append(" FROM MS_CF01 a,MS_CF02 b,YK_TYPK c,YK_CDXX d WHERE d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0 and a.FYRQ is not null and  a.CFSB = b.CFSB AND");
		sqlBuilder.append(" c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.JGID = :al_jgid AND a.YFSB = :al_yfsb AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" >= :adt_start AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" <= :adt_end");
		sqlBuilder.append(" AND a.KSDM = ").append(body.get("VIRTUAL_FIELD"));
		if(ii_yplx != 0){//固定数据：0 全部  1西药 2中成药 3草药
			sqlBuilder.append(" AND a.CFLX = ").append(ii_yplx);
		}
		if(body.get("PYDM") != null){
			sqlBuilder.append(" AND c.PYDM like '").append(body.get("PYDM")).append("%' ");
		}
		sqlBuilder.append(" GROUP BY c.YPMC, c.YPXH, c.YPGG, c.YPDW, a.KSDM, a.CFLX,d.JHJG");

		return sqlBuilder.toString();
	}
	public String KDKSDataSql(){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT ID as KSDM ,officeName as KSMC FROM SYS_Office WHERE organizCode = :al_jgid ");
		return sqlBuilder.toString();
	}
	
	/**
	 * 组装发药人统计sql
	 * @param body
	 * 		存放YPLX 固定数据：0 全部  1西药 2中成药 3草药       为空时默认查询全部
	 * @return
	 */
	public String FYRStatisticalMethod(Map<String, Object> body){
		int ii_yplx = body.get("YPLX") == null ? 0 : Integer.parseInt(body.get("YPLX") + "");
		String format = "YYYY-MM-DD HH24:MI:SS";
		StringBuilder sqlBuilder = new StringBuilder();
//		sqlBuilder.append("SELECT '' as YPMC, 0 as YPXH, '' as YPGG, '' as YPDW, a.KSDM as KSDM, a.CFLX as CFLX, ");
		sqlBuilder.append("SELECT '' as YPMC, 0 as YPXH, '' as YPGG, '' as YPDW, a.FYGH as FYGH, ");//去掉a.CFLX AS CFLX,分组中也去掉
		sqlBuilder.append(" COUNT(DISTINCT(a.CFSB)) as CFZS, sum(b.YPSL*b.CFTS) as FYSL, sum(b.HJJE) as FYJE");
		// YK_TYPK.YPXH = b.YPXH AND
		sqlBuilder.append(" FROM  MS_CF01 a, MS_CF02 b WHERE a.CFSB = b.CFSB AND a.ZFPB = 0  AND a.FYGH is not null AND");
		sqlBuilder.append(" a.JGID = :al_jgid AND a.YFSB = :al_yfsb AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" >= :adt_start AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" <= :adt_end");
		if(ii_yplx != 0){//固定数据：0 全部  1西药 2中成药 3草药
			sqlBuilder.append(" AND a.CFLX = ").append(ii_yplx);
		}
		sqlBuilder.append(" GROUP BY a.FYGH ");

		return sqlBuilder.toString();
	}
	public String FYRDataSql(){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT PERSONID as YGDM ,PERSONNAME as YGXM FROM SYS_Personnel WHERE ORGANIZCODE = :al_jgid ");
		return sqlBuilder.toString();
	}
	public String FYRStatisticalDetails(Map<String, Object> body){
		int ii_yplx = body.get("YPLX") == null ? 0 : Integer.parseInt(body.get("YPLX") + "");
		String format = "YYYY-MM-DD HH24:MI:SS";
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT c.YPMC as YPMC, c.YPXH as YPXH, c.YPGG as YPGG, c.YPDW as YPDW, a.FYGH as FYGH,");
		sqlBuilder.append(" a.CFLX as CFLX, 0 as CFZS, sum(b.YPSL*b.CFTS) as FYSL, sum(b.HJJE) as FYJE,sum(b.YPSL*b.CFTS*d.JHJG) as JHJE ,d.JHJG as JHJG ");
		sqlBuilder.append(" FROM MS_CF01 a,MS_CF02 b,YK_TYPK c,YK_CDXX d WHERE d.YPCD=b.YPCD and d.YPXH=b.YPXH and d.JGID=b.JGID and a.FYBZ!=0 and a.FYRQ is not null and a.CFSB = b.CFSB AND");
		sqlBuilder.append(" c.YPXH = b.YPXH AND a.ZFPB = 0 AND a.JGID = :al_jgid AND a.YFSB = :al_yfsb AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" >= :adt_start AND ");
		sqlBuilder.append(BSPHISUtil.toChar("a.FYRQ", format)).append(" <= :adt_end");
		sqlBuilder.append(" AND a.FYGH = ").append(body.get("VIRTUAL_FIELD"));
		if(ii_yplx != 0){//固定数据：0 全部  1西药 2中成药 3草药
			sqlBuilder.append(" AND a.CFLX = ").append(ii_yplx);
		}
		if(body.get("PYDM") != null){
			sqlBuilder.append(" AND c.PYDM like '").append(body.get("PYDM")).append("%' ");
		}
		sqlBuilder.append(" GROUP BY c.YPMC, c.YPXH, c.YPGG, c.YPDW, a.FYGH, a.CFLX,d.JHJG");

		return sqlBuilder.toString();
	}
}
