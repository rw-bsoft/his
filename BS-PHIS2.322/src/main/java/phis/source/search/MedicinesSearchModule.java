package phis.source.search;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.Transformers;

import phis.application.mds.source.CalculatorIn;
import phis.source.bean.Medicines;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.JSONUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class MedicinesSearchModule extends AbstractSearchModule {
	/**
	 * 实现出入库药品查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = parseLong(user.getProperty("pharmacyId"));
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = parseLong(user.getProperty("storehouseId"));
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();
		String tag = req.get("tag") + "";
		String SEARCH_TYPE_Set = SEARCH_TYPE;
		if (containsChinese(searchText)) {
			SEARCH_TYPE = "YPMC";
			SEARCH_TYPE_Set = "ZTMC";
		}
		// if
		// (!"tj".equals(tag)&&!"cszc".equals(tag)&&!"jhdw".equals(tag)&&!"ykck".equals(tag))
		// {
		// yfsb = // 用户的药房识别
		// }
		try {
			StringBuffer sql = new StringBuffer();
			if ("rk".equals(tag)) {//药房入库
				sql.append(
						"select distinct yp.YPDW as YPDW,yp.YPGG as YPGG,yp.ZXBZ as ZXBZ, yp.YPXH as YPXH,yp.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,round(jg.LSJG*(yf.YFBZ/yp.ZXBZ),4) as LSJG,round(jg.JHJG*(yf.YFBZ/yp.ZXBZ),4) as JHJG,yf.YFBZ as YFBZ,round(jg.PFJG*(yf.YFBZ/yp.ZXBZ),4) as PFJG from ")
						.append(" YK_TYPK yp, ")
						.append(" YK_YPXX yk,")
						.append(" YF_YPXX yf,")
						.append(" YK_CDXX jg,")
						.append(" YK_CDDZ cd,")
						.append(" YK_YPBM bm,")
						.append(" YK_YPCD gyjg where gyjg.YPCD=jg.YPCD and gyjg.YPXH=jg.YPXH and gyjg.ZFPB!=1 and yp.YPXH=yk.YPXH and yk.YPXH=yf.YPXH and yf.JGID=yk.JGID and yf.JGID=jg.JGID and yf.YPXH=jg.YPXH and jg.YPCD=cd.YPCD and bm.YPXH=yp.YPXH and yp.ZFPB!=1 and yk.YKZF!=1 and yf.YFZF!=1 and jg.ZFPB!=1 and yf.YFSB=")
						.append(yfsb).append(" and bm.").append(SEARCH_TYPE)
						.append(" like '").append(searchText)
						.append("%' order by yp.YPXH,jg.YPCD");
			}else if("pd".equals(tag)) {//盘点
				sql.append(
						"select distinct yp.YPDW as YPDW,yp.YPGG as YPGG,yp.ZXBZ as ZXBZ, yp.YPXH as YPXH,yp.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,round(jg.LSJG*(yf.YFBZ/yp.ZXBZ),4) as LSJG,round(jg.JHJG*(yf.YFBZ/yp.ZXBZ),4) as JHJG,yf.YFBZ as YFBZ,round(jg.PFJG*(yf.YFBZ/yp.ZXBZ),4) as PFJG from ")
						.append(" YK_TYPK yp, ")
						.append(" YK_YPXX yk,")
						.append(" YF_YPXX yf,")
						.append(" YK_CDXX jg,")
						.append(" YK_CDDZ cd,")
						.append(" YK_YPBM bm,")
						.append(" YK_YPCD gyjg where gyjg.YPCD=jg.YPCD and gyjg.YPXH=jg.YPXH  and yp.YPXH=yk.YPXH and yk.YPXH=yf.YPXH and yf.JGID=yk.JGID and yf.JGID=jg.JGID and yf.YPXH=jg.YPXH and jg.YPCD=cd.YPCD and bm.YPXH=yp.YPXH   and yf.YFSB=")
						.append(yfsb).append(" and bm.").append(SEARCH_TYPE)
						.append(" like '").append(searchText)
						.append("%' order by yp.YPXH,jg.YPCD");
				
			}else if ("tj".equals(tag)) {//调价
				sql.append(
						"select distinct yp.YFBZ as YFBZ,yp.YPGG as YFGG,yp.YPDW as YFDW, yp.YPXH as YPXH,yp.YPMC as YPMC,cd.CDMC as CDMC,jg.YPCD as YPCD,round(jg.LSJG,4) as LSJG,round(jg.JHJG,4) as JHJG,round(jg.PFJG,4) as PFJG from ")
						.append(" YK_TYPK yp, ")
						.append(" YK_YPXX yk,")
						.append(" YK_CDXX jg,")
						.append(" YK_CDDZ cd,")
						.append(" YK_YPBM bm,")
						.append(" YK_YPCD gyjg where gyjg.YPCD=jg.YPCD and gyjg.YPXH=jg.YPXH and gyjg.ZFPB!=1 and yp.YPXH=yk.YPXH  and yk.JGID=jg.JGID and yk.YPXH=jg.YPXH and jg.YPCD=cd.YPCD and bm.YPXH=yp.YPXH and yp.ZFPB!=1 and yk.YKZF!=1  and jg.ZFPB!=1 and yk.YKSB='")
						.append(yksb).append("' and bm.").append(SEARCH_TYPE)
						.append(" like '").append(searchText)
						.append("%' order by yp.YPXH,jg.YPCD");
			} else if ("cszc".equals(tag)) {//初始账册,药库入库,采购计划
				sql.append(
						"select distinct yk.JGID as JGID,yp.YFBZ as YFBZ,yp.YPGG as YFGG,yp.YPDW as YFDW, yp.YPXH as YPXH,yp.YPMC as YPMC,cd.CDMC as CDMC,jg.YPCD as YPCD,round(jg.LSJG,4) as LSJG,round(jg.JHJG,4) as JHJG,round(jg.PFJG,4) as PFJG,round(jg.LSJE,2) as LSJE,round(jg.PFJE,2) as PFJE,round(jg.JHJE,2) as JHJE,gyjg.LSJG as GYLJ,gyjg.JHJG as GYJJ,jg.DJFS as DJFS,jg.DJGS as DJGS from ")
						.append(" YK_TYPK yp,")
						.append(" YK_YPXX yk,")
						.append(" YK_CDXX jg,")
						.append(" YK_CDDZ cd,")
						.append(" YK_YPBM bm,")
						.append(" YK_YPCD gyjg where gyjg.YPCD=jg.YPCD and gyjg.YPXH=jg.YPXH and gyjg.ZFPB!=1 and yp.YPXH=yk.YPXH  and yk.JGID=jg.JGID and yk.YPXH=jg.YPXH and jg.YPCD=cd.YPCD and bm.YPXH=yp.YPXH and yp.ZFPB!=1 and yk.YKZF!=1  and jg.ZFPB!=1 and yk.YKSB='")
						.append(yksb).append("' and bm.").append(SEARCH_TYPE)
						.append(" like '").append(searchText)
						.append("%' order by yp.YPXH,jg.YPCD");
			} else if ("jhdw".equals(tag)) {//进货单位
				if (containsChinese(searchText)) {
					SEARCH_TYPE = "DWMC";
				}
				sql.append("select DWXH as DWXH,DWMC as DWMC from  YK_JHDW")
						.append(" where ZFPB=0 and " + SEARCH_TYPE)
						.append(" like '").append(searchText).append("%' ");
			} else if ("ks".equals(tag)) {//科室
				sql.append("select ID as KSDM,OFFICENAME as KSMC from  SYS_Office")
						.append(" where PYDM").append(" like '")
						.append(searchText).append("%' and JGID='")
						.append(jgid).append("'");
			} else if ("ykck".equals(tag)) {//药库出库
				if (req.get("yfsb") != null
						&& !"".equals(req.get("yfsb"))) {
					yfsb = Long.parseLong(req.get("yfsb") + "");
				} else {
					yfsb = 0;
				}
				if (yfsb != 0) {
					sql.append(
							"select  KWBM as KWBM,YPXH as YPXH,YPMC as YPMC,YFGG as YFGG,YFDW as YFDW,CDMC as CDMC,YPCD as YPCD,LSJG as LSJG,JHJG as JHJG,YFBZ as YFBZ,PFJG as PFJG,sum(KCSL) as KCSL from")
							.append("(select  distinct yk.KWBM as KWBM,yp.YPXH as YPXH,yp.YPMC as YPMC,yp.YPGG as YFGG,yp.YPDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,round(jg.LSJG,4) as LSJG,round(jg.JHJG,4) as JHJG,yf.YFBZ as YFBZ,round(jg.PFJG,4) as PFJG,kc.KCSL as KCSL,kc.sbxh as sbxh from ")
							.append(" YK_TYPK yp, ")
							.append(" YK_YPXX yk,")
							.append(" YF_YPXX yf,")
							.append(" YK_CDXX jg ")
							.append("left join yk_kcmx kc on jg.jgid = kc.jgid and jg.ypxh = kc.ypxh and jg.ypcd = kc.ypcd,")
							.append(" YK_CDDZ cd,")
							.append(" YK_YPBM bm,")
							.append(" YK_YPCD gyjg where gyjg.YPCD=jg.YPCD and gyjg.YPXH=jg.YPXH and gyjg.ZFPB!=1 and yp.YPXH=yk.YPXH and yk.YPXH=yf.YPXH and  yk.JGID=jg.JGID and yf.YPXH=jg.YPXH and jg.YPCD=cd.YPCD and bm.YPXH=yp.YPXH and yp.ZFPB!=1 and yk.YKZF!=1 and yf.YFZF!=1 and jg.ZFPB!=1 and yf.YFSB=")
							.append(yfsb)
							.append(" and yk.YKSB=")
							.append(yksb)
							.append(" and bm.")
							.append(SEARCH_TYPE)
							.append(" like '")
							.append(searchText)
							.append("%' ")
							.append(" and kc.kcsl is not null")
							.append(" order by yp.YPXH,jg.YPCD)")
							.append(" group by KWBM,YPXH,YPMC,YFGG,YFDW,CDMC,YPCD,LSJG,JHJG,YFBZ,PFJG");
				} else {
					sql.append(
							"select KWBM as KWBM,YPXH as YPXH,YPMC as YPMC,YFGG as YFGG,YFDW as YFDW,CDMC as CDMC,YPCD as YPCD,LSJG as LSJG,JHJG as JHJG,PFJG as PFJG,sum(KCSL) as KCSL,GCSL as GCSL,DCSL as DCSL from")
							.append("(select distinct yk.KWBM as KWBM,yp.YPXH as YPXH,yp.YPMC as YPMC,yp.YPGG as YFGG,yp.YPDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,round(jg.LSJG,4) as LSJG,round(jg.JHJG,4) as JHJG,round(jg.PFJG,4) as PFJG,kc.KCSL as KCSL,kc.sbxh as sbxh,yk.GCSL as GCSL,yk.DCSL as DCSL from ")
							.append(" YK_TYPK yp, ")
							.append(" YK_YPXX yk,")
							.append(" YK_CDXX jg ")
							.append("left join yk_kcmx kc on jg.jgid = kc.jgid and jg.ypxh = kc.ypxh and jg.ypcd = kc.ypcd,")
							.append(" YK_CDDZ cd,")
							.append(" YK_YPBM bm,")
							.append(" YK_YPCD gyjg where yk.JGID=jg.JGID and gyjg.YPCD=jg.YPCD and gyjg.YPXH=jg.YPXH and gyjg.ZFPB!=1 and yp.YPXH=yk.YPXH  and jg.YPCD=cd.YPCD and jg.YPXH=yp.YPXH  and bm.YPXH=yp.YPXH and yp.ZFPB!=1 and yk.YKZF!=1  and jg.ZFPB!=1 ")
							.append(" and yk.YKSB=")
							.append(yksb)
							.append(" and bm.")
							.append(SEARCH_TYPE)
							.append(" like '")
							.append(searchText)
							.append("%' ")
							.append(" and kc.kcsl is not null")
							.append(" order by yp.YPXH,jg.YPCD)")
							.append(" group by KWBM,YPXH,YPMC,YFGG,YFDW,CDMC,YPCD,LSJG,JHJG,PFJG,GCSL,DCSL");
				}

			} else if ("ypsl".equals(tag)) {//药品申领
				yksb = parseLong(req.get("yksb"));
				sql.append(
						"select distinct yk.KWBM as KWBM,yp.YPXH as YPXH,yp.YPMC as YPMC,yp.YPGG as YFGG,yp.YPDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,round(jg.LSJG,4) as LSJG,round(jg.JHJG,4) as JHJG,yf.YFBZ as YFBZ,round(jg.PFJG,4) as PFJG from ")
						.append(" YK_TYPK yp, ")
						.append(" YK_YPXX yk,")
						.append(" YF_YPXX yf,")
						.append(" YK_CDXX jg,")
						.append(" YK_CDDZ cd,")
						.append(" YK_YPBM bm,")
						.append(" YK_YPCD gyjg where gyjg.YPCD=jg.YPCD and gyjg.YPXH=jg.YPXH and gyjg.ZFPB!=1 and yp.YPXH=yk.YPXH and yk.YPXH=yf.YPXH  and yk.JGID=jg.JGID and yf.YPXH=jg.YPXH and jg.YPCD=cd.YPCD and bm.YPXH=yp.YPXH and yp.ZFPB!=1 and yk.YKZF!=1 and yf.YFZF!=1 and jg.ZFPB!=1 and yf.YFSB=")
						.append(yfsb).append(" and yk.YKSB=").append(yksb)
						.append(" and bm.").append(SEARCH_TYPE)
						.append(" like '").append(searchText)
						.append("%' order by yp.YPXH,jg.YPCD");
			} else if ("slty".equals(tag)) {//申领退药
				yksb = parseLong(req.get("yksb"));
				sql.append(
						"select distinct a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,a.YPXH as YPXH,c.YFBZ as YFBZ,a.ZXBZ as ZXBZ,f.CDMC as CDMC,round(e.JHJG,4) as JHJG,round(e.PFJG,4) as PFJG,round(e.LSJG,4) as LSJG,e.YPCD as YPCD,e.YPPH as YPPH,e.YPXQ as YPXQ,e.YPSL as YPSL,e.SBXH as YFKCSB,e.YKJH as YKJH,e.YKLJ as YKLJ,e.YKPJ as YKPJ,e.YKJJ as YKJJ,e.YKKCSB as KCSB  from ")
						.append(" YK_TYPK a,")
						.append(" YK_YPXX b,")
						.append(" YF_YPXX c,")
						.append(" YK_YPBM d,")
						.append(" YF_KCMX e,")
						.append(" YK_CDDZ f where a.YPXH=b.YPXH and b.YPXH=c.YPXH  and c.YPXH=d.YPXH and b.YPXH=d.YPXH and c.YPXH=e.YPXH and c.YFSB=e.YFSB and e.YPCD=f.YPCD and b.YKZF = 0 and a.ZFPB = 0 and d.")
						.append(SEARCH_TYPE).append(" like '")
						.append(searchText)
						.append("%' and d.BMFL=1 and c.YFSB=").append(yfsb)
						.append(" and b.YKSB=").append(yksb);
			} else if ("db".equals(tag)) {//药房调拨
				yfsb = Long.parseLong(req.get("yfsb") + "");
				sql.append(
						"select distinct kc.SBXH as KCSB,yp.YPXH as YPXH,yp.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,round(kc.LSJG,4) as LSJG,round(kc.JHJG,4) as JHJG,yf.YFBZ as YFBZ,round(kc.PFJG,4) as PFJG,kc.YPXQ as YPXQ,kc.YPPH as YPPH from ")
						.append(" YK_TYPK yp, ")
						.append(" YK_YPXX yk,")
						.append(" YF_YPXX yf,")
						.append(" YK_CDXX jg,")
						.append(" YK_CDDZ cd,")
						.append(" YK_YPBM bm,")
						.append(" YF_KCMX kc ")
						.append("  where yk.YPXH=yp.YPXH and yf.JGID=yk.JGID and yf.YPXH=yk.YPXH and yk.YPXH=jg.YPXH and yk.JGID=jg.JGID and jg.YPCD=cd.YPCD and  yp.YPXH=bm.YPXH and yf.YFSB=kc.YFSB and   kc.YPXH=yp.YPXH and   kc.YPCD=jg.YPCD   and kc.JYBZ = 0 and yp.ZFPB!=1 and yf.YFZF!=1  and yf.YFSB=")
						.append(yfsb).append(" and bm.").append(SEARCH_TYPE)
						.append(" like '").append(searchText)
						.append("%' order by yp.YPXH,jg.YPCD");
			} else if ("dbty".equals(tag)) {//调拨退药
				long mbyf = Long.parseLong(req.get("mbyf") + "");
				sql.append(
						"select distinct kc.YPSL as KCSL, kc.SBXH as KCSB,yp.YPXH as YPXH,yp.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,round(kc.LSJG,4) as LSJG,round(kc.JHJG,4) as JHJG,yf.YFBZ as YFBZ,round(kc.PFJG,4) as PFJG,kc.YPXQ as YPXQ,kc.YPPH as YPPH from ")
						.append(" YK_TYPK yp, ")
						.append(" YK_YPXX yk,")
						.append(" YF_YPXX yf,")
						.append(" YK_CDXX jg,")
						.append(" YK_CDDZ cd,")
						.append(" YK_YPBM bm,")
						.append(" YF_KCMX kc, ")
						.append(" YF_YPXX mbyf ")
						.append("  where mbyf.YPXH=yf.YPXH and yk.YPXH=yp.YPXH and yf.JGID=yk.JGID and yf.YPXH=yk.YPXH and yk.YPXH=jg.YPXH and yk.JGID=jg.JGID and jg.YPCD=cd.YPCD and  yp.YPXH=bm.YPXH and yf.YFSB=kc.YFSB and   kc.YPXH=yp.YPXH and   kc.YPCD=jg.YPCD and   kc.LSJG=round(jg.lsjg*(yf.yfbz/yp.zxbz),4)  and kc.JYBZ = 0 and yp.ZFPB!=1 and yf.YFZF!=1  and yf.YFSB=")
						.append(yfsb).append(" and mbyf.YFSB=").append(mbyf)
						.append(" and bm.").append(SEARCH_TYPE)
						.append(" like '").append(searchText)
						.append("%' order by yp.YPXH,jg.YPCD");
			}else if("jz".equals(tag)){//住院记账
				String type = req.get("type") + "";
//				sql.append(
//						"select FYFS as FYFS,JYLX as JYLX,KCSB as KCSB,KCSL as KCSL,YPXH as YPXH,YPMC as YPMC,YFGG as YFGG,YFDW as YFDW,CDMC as CDMC,YPCD as YPCD,LSJG as LSJG,JHJG as JHJG,YFBZ as YFBZ,PFJG as PFJG,YPXQ as YPXQ,YPPH as YPPH,isZT as isZT from (select distinct yp.FYFS as FYFS,yp.JYLX as JYLX, kc.SBXH as KCSB,kc.YPSL as KCSL,yp.YPXH as YPXH,yp.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,kc.LSJG  as LSJG, kc.JHJG  as JHJG,yf.YFBZ as YFBZ,kc.PFJG  as PFJG ,kc.YPXQ as YPXQ,kc.YPPH as YPPH,0 as isZT from ")
//						.append(" YK_TYPK yp, ")
//						.append(" YK_YPXX yk,")
//						.append(" YF_YPXX yf,")
//						.append(" YK_CDDZ cd,")
//						.append(" YK_YPBM bm,")
//						.append(" YK_CDXX jg  , YF_KCMX kc   ")
//						.append("  where yk.YPXH=yp.YPXH and yf.JGID=yk.JGID and yf.YPXH=yk.YPXH and yk.YPXH=jg.YPXH and yk.JGID=jg.JGID and jg.YPCD=cd.YPCD and kc.YPXH = jg.YPXH  and kc.YPCD = jg.YPCD and kc.YFSB=yf.YFSB and kc.JYBZ = 0 and yp.YPXH=bm.YPXH  and yp.ZFPB!=1 and yf.YFZF!=1  and yf.YFSB=")
//						.append(yfsb).append(" and yp.TYPE=").append(type).append(" and bm.").append(SEARCH_TYPE)
//						.append(" like '").append(searchText)
//						.append("%' ").append(" union all ").
//						append(" select distinct yp.FYFS as FYFS,yp.JYLX as JYLX, 0 as KCSB,0 as KCSL,yp.YPXH as YPXH,yp.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,jg.LSJG  as LSJG, jg.JHJG  as JHJG,yf.YFBZ as YFBZ,jg.PFJG  as PFJG ,null as YPXQ,null as YPPH,0 as isZT from ")
//						.append(" YK_TYPK yp, ")
//						.append(" YK_YPXX yk,")
//						.append(" YF_YPXX yf,")
//						.append(" YK_CDDZ cd,")
//						.append(" YK_YPBM bm,")
//						.append(" YK_CDXX jg  where yk.YPXH=yp.YPXH and yf.JGID=yk.JGID and yf.YPXH=yk.YPXH and yk.YPXH=jg.YPXH and yk.JGID=jg.JGID and jg.YPCD=cd.YPCD  and yp.YPXH=bm.YPXH  and yp.ZFPB!=1 and yf.YFZF!=1 and jg.ZFPB!=1  and yf.YFSB=").append(yfsb).append(" and yp.TYPE=").append(type).append(" and bm.").append(SEARCH_TYPE)
//						.append(" like '").append(searchText)
//						.append("%' union all ")
//						.append("select 0 as FYFS,0 as JYLX, 0 as KCSB,0 as KCSL,a.ZTBH as YPXH,'(组套)'||a.ZTMC as YPMC,'' as YFGG,'' as YFDW,'' as CDMC,0 as YPCD,0 as LSJG,0 as JHJG,0 as YFBZ,0 as PFJG,null as YPXQ,null as YPPH,1 as isZT from ")
//						.append("YS_MZ_ZT01 a where a.SFQY=1 and a.SSLB=3 and a.ZTLB="+type+" AND a.")
//				.append(SEARCH_TYPE_Set)
//				.append(" LIKE '").append(searchText)
//				.append("%' AND a.JGID = '"+jgid+"'")
//						.append(" ) order by length(YPMC),YPXH");
				
//				sql.append(
//						"select FYFS as FYFS,JYLX as JYLX,KCSB as KCSB,KCSL as KCSL,YPXH as YPXH,YPMC as YPMC,YFGG as YFGG,YFDW as YFDW,CDMC as CDMC,YPCD as YPCD,LSJG as LSJG,JHJG as JHJG,YFBZ as YFBZ,PFJG as PFJG,YPXQ as YPXQ,YPPH as YPPH from (select distinct yp.FYFS as FYFS,yp.JYLX as JYLX, kc.SBXH as KCSB,kc.YPSL as KCSL,yp.YPXH as YPXH,yp.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,kc.LSJG  as LSJG, kc.JHJG  as JHJG,yf.YFBZ as YFBZ,kc.PFJG  as PFJG ,kc.YPXQ as YPXQ,kc.YPPH as YPPH from ")
//						.append(" YK_TYPK yp, ")
//						.append(" YK_YPXX yk,")
//						.append(" YF_YPXX yf,")
//						.append(" YK_CDDZ cd,")
//						.append(" YK_YPBM bm,")
//						.append(" YK_CDXX jg  , YF_KCMX kc   ")
//						.append("  where yk.YPXH=yp.YPXH and yf.JGID=yk.JGID and yf.YPXH=yk.YPXH and yk.YPXH=jg.YPXH and yk.JGID=jg.JGID and jg.YPCD=cd.YPCD and kc.YPXH = jg.YPXH  and kc.YPCD = jg.YPCD and kc.YFSB=yf.YFSB and kc.JYBZ = 0 and yp.YPXH=bm.YPXH  and yp.ZFPB!=1 and yf.YFZF!=1  and yf.YFSB=")
//						.append(yfsb).append(" and yp.TYPE=").append(type).append(" and bm.").append(SEARCH_TYPE)
//						.append(" like '").append(searchText)
//						.append("%' ").append(" union all ").
//						append(" select distinct yp.FYFS as FYFS,yp.JYLX as JYLX, 0 as KCSB,0 as KCSL,yp.YPXH as YPXH,yp.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,jg.LSJG  as LSJG, jg.JHJG  as JHJG,yf.YFBZ as YFBZ,jg.PFJG  as PFJG ,null as YPXQ,null as YPPH from ")
//						.append(" YK_TYPK yp, ")
//						.append(" YK_YPXX yk,")
//						.append(" YF_YPXX yf,")
//						.append(" YK_CDDZ cd,")
//						.append(" YK_YPBM bm,")
//						.append(" YK_CDXX jg  where yk.YPXH=yp.YPXH and yf.JGID=yk.JGID and yf.YPXH=yk.YPXH and yk.YPXH=jg.YPXH and yk.JGID=jg.JGID and jg.YPCD=cd.YPCD  and yp.YPXH=bm.YPXH  and yp.ZFPB!=1 and yf.YFZF!=1 and jg.ZFPB!=1  and yf.YFSB=").append(yfsb).append(" and yp.TYPE=").append(type).append(" and bm.").append(SEARCH_TYPE)
//						.append(" like '").append(searchText)
//						.append("%' ")
//						.append(" ) order by YPXH,YPCD");
				//update at 2014-11-24 for 张伟2要求不能这样搞,不能显示0库存的记录, 现在改成显示当前药房所有可用药品 不用关联库存
				sql.append("select FYFS as FYFS,JYLX as JYLX,KCSB as KCSB,KCSL as KCSL,YPXH as YPXH,YPMC as YPMC,YFGG as YFGG,YFDW as YFDW,CDMC as CDMC,YPCD as YPCD,LSJG as LSJG,JHJG as JHJG,YFBZ as YFBZ,PFJG as PFJG,YPXQ as YPXQ,YPPH as YPPH,isZT as isZT from (select distinct yp.FYFS as FYFS,yp.JYLX as JYLX, 0 as KCSB,0 as KCSL,yp.YPXH as YPXH,yp.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,jg.LSJG  as LSJG, jg.JHJG  as JHJG,yf.YFBZ as YFBZ,jg.PFJG  as PFJG ,null as YPXQ,null as YPPH,0 as isZT from ")
				.append(" YK_TYPK yp, ")
				.append(" YK_YPXX yk,")
				.append(" YF_YPXX yf,")
				.append(" YK_CDDZ cd,")
				.append(" YK_YPBM bm,")
				.append(" YK_CDXX jg  where yk.YPXH=yp.YPXH and yf.JGID=yk.JGID and yf.YPXH=yk.YPXH and yk.YPXH=jg.YPXH and yk.JGID=jg.JGID and jg.YPCD=cd.YPCD  and yp.YPXH=bm.YPXH  and yp.ZFPB!=1 and yf.YFZF!=1 and jg.ZFPB!=1  and yf.YFSB=").append(yfsb).append(" and yp.TYPE=").append(type).append(" and bm.").append(SEARCH_TYPE)
				.append(" like '").append(searchText)
				.append("%' union all ")
						.append("select 0 as FYFS,0 as JYLX, 0 as KCSB,0 as KCSL,a.ZTBH as YPXH,'(组套)'||a.ZTMC as YPMC,'' as YFGG,'' as YFDW,'' as CDMC,0 as YPCD,0 as LSJG,0 as JHJG,0 as YFBZ,0 as PFJG,null as YPXQ,null as YPPH,1 as ISZT from ")
						.append("YS_MZ_ZT01 a where a.SFQY=1 and a.SSLB=3 and a.ZTLB="+type+" AND a.")
				.append(SEARCH_TYPE_Set)
				.append(" LIKE '").append(searchText)
				.append("%' AND a.JGID = '"+jgid+"'")
						.append(" ) order by length(YPMC),YPXH");
			} else {
				sql.append(
						"select distinct kc.SBXH as KCSB,kc.YPSL as KCSL,yp.YPXH as YPXH,yp.YPMC as YPMC,yf.YFGG as YFGG,yf.YFDW as YFDW,cd.CDMC as CDMC,jg.YPCD as YPCD,round(kc.LSJG,4) as LSJG,round(kc.JHJG,4) as JHJG,yf.YFBZ as YFBZ,round(kc.PFJG,4) as PFJG,kc.YPXQ as YPXQ,kc.YPPH as YPPH from ")
						.append(" YK_TYPK yp, ")
						.append(" YK_YPXX yk,")
						.append(" YF_YPXX yf,")
						.append(" YK_CDXX jg,")
						.append(" YK_CDDZ cd,")
						.append(" YK_YPBM bm,")
						.append(" YF_KCMX kc ")
						.append("  where yk.YPXH=yp.YPXH and yf.JGID=yk.JGID and yf.YPXH=yk.YPXH and yk.YPXH=jg.YPXH and yk.JGID=jg.JGID and jg.YPCD=cd.YPCD and  yp.YPXH=bm.YPXH and yf.YFSB=kc.YFSB and   kc.YPXH=yp.YPXH and   kc.YPCD=jg.YPCD and kc.JYBZ = 0 and yp.ZFPB!=1 and yf.YFZF!=1  and yf.YFSB=")
						.append(yfsb).append(" and bm.").append(SEARCH_TYPE)
						.append(" like '").append(searchText)
						.append("%' order by yp.YPXH,jg.YPCD");

			}

			StringBuffer sql_count = new StringBuffer();
			sql_count.append("select count(*) as TOTAL from (")
					.append(sql.toString()).append(")");
			List<Map<String, Object>> l = ss
					.createSQLQuery(sql_count.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			Long count = Long.parseLong(l.get(0).get("TOTAL") + "");
			List<Map<String, Object>> meds = ss.createSQLQuery(sql.toString())
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit))
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			List<Medicines> Medicines = new ArrayList<Medicines>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < meds.size(); i++) {
				Medicines m = new Medicines();
				m.setJGID(meds.get(i).get("JGID") == null ? null : meds.get(i)
						.get("JGID") + "");
				m.setDWMC(meds.get(i).get("DWMC") == null ? null : meds.get(i)
						.get("DWMC") + "");
				m.setYPMC(meds.get(i).get("YPMC") == null ? null : meds.get(i)
						.get("YPMC") + "");
				m.setYFGG(meds.get(i).get("YFGG") == null ? "" : meds.get(i)
						.get("YFGG") + "");
				m.setYFDW(meds.get(i).get("YFDW") == null ? "" : meds.get(i)
						.get("YFDW") + "");
				m.setCDMC(meds.get(i).get("CDMC") == null ? null : meds.get(i)
						.get("CDMC") + "");
				m.setYPPH(meds.get(i).get("YPPH") == null ? "" : meds.get(i)
						.get("YPPH") + "");
				m.setKSMC(meds.get(i).get("KSMC") == null ? null : meds.get(i)
						.get("KSMC") + "");
				m.setKWBM(meds.get(i).get("KWBM") == null ? "" : meds.get(i)
						.get("KWBM") + "");
				m.setYPGG(meds.get(i).get("YPGG") == null ? "" : meds.get(i)
						.get("YPGG") + "");
				m.setYPDW(meds.get(i).get("YPDW") == null ? "" : meds.get(i)
						.get("YPDW") + "");
				m.setYPDW(meds.get(i).get("YPDW") == null ? "" : meds.get(i)
						.get("YPDW") + "");
				m.setKCSB(parseLong(meds.get(i).get("KCSB")));
				m.setKCSL(parseDouble(meds.get(i).get("KCSL")));
				m.setLSJE(parseDouble(meds.get(i).get("LSJE")));
				m.setPFJE(parseDouble(meds.get(i).get("PFJE")));
				m.setJHJE(parseDouble(meds.get(i).get("JHJE")));
				m.setGYLJ(parseDouble(meds.get(i).get("GYLJ")));
				m.setGYJJ(parseDouble(meds.get(i).get("GYJJ")));
				m.setDWXH(parseLong(meds.get(i).get("DWXH")));
				m.setNumKey((i + 1 == 10) ? 0 : i + 1);
				m.setYPXH(parseLong(meds.get(i).get("YPXH")));
				m.setYPCD(parseLong(meds.get(i).get("YPCD")));
				m.setLSJG(parseDouble(meds.get(i).get("LSJG")));
				m.setJHJG(parseDouble(meds.get(i).get("JHJG")));
				m.setYFBZ(parseInt(meds.get(i).get("YFBZ")));
				m.setZXBZ(parseInt(meds.get(i).get("ZXBZ")));
				m.setPFJG(parseDouble(meds.get(i).get("PFJG")));
				m.setYPXQ(meds.get(i).get("YPXQ") == null ? "" : sdf
						.format((Date) meds.get(i).get("YPXQ")));
				m.setKSDM(parseLong(meds.get(i).get("KSDM")));
				m.setYPSL(parseDouble(meds.get(i).get("YPSL")));
				m.setYKJH(parseDouble(meds.get(i).get("YKJH")));
				m.setYKLJ(parseDouble(meds.get(i).get("YKLJ")));
				m.setYKPJ(parseDouble(meds.get(i).get("YKPJ")));
				m.setYKJJ(parseDouble(meds.get(i).get("YKJJ")));
				m.setYFKCSB(parseLong(meds.get(i).get("YFKCSB")));
				m.setJYLX(parseInt(meds.get(i).get("JYLX")));
				m.setFYFS(parseLong(meds.get(i).get("FYFS")));
				m.setGCSL(parseDouble(meds.get(i).get("GCSL")));
				m.setDCSL(parseDouble(meds.get(i).get("DCSL")));
				m.setISZT(parseInt(meds.get(i).get("ISZT")));
				//增加以下代码用于采购入库零售价格根据定价方式计算
				if(meds.get(i).containsKey("DJFS")){
					int djfs=parseInt(meds.get(i).get("DJFS"));
					m.setDJFS(djfs);
					if(djfs==2||djfs==1){
						if(meds.get(i).containsKey("DJGS")){
							String djgs=parseString(meds.get(i).get("DJGS"));
							if(djgs.length()>0){
								djgs=djgs.replaceAll("标准零价", meds.get(i).get("LSJG")+"");
								djgs=djgs.replaceAll("实际进价", meds.get(i).get("JHJG")+"");
								 CalculatorIn cal=new CalculatorIn();
								 meds.get(i).put("BZLJ",meds.get(i).get("LSJG"));
								 meds.get(i).put("LSJG",cal.js(djgs));
							}
						}
						
					}
				}
//				Medicines m = new Medicines();
//				m.setJGID(meds.get(i).get("JGID") == null ? null : meds.get(i)
//						.get("JGID") + "");
//				m.setDWMC(meds.get(i).get("DWMC") == null ? null : meds.get(i)
//						.get("DWMC") + "");
//				m.setYPMC(meds.get(i).get("YPMC") == null ? null : meds.get(i)
//						.get("YPMC") + "");
//				m.setYFGG(meds.get(i).get("YFGG") == null ? "" : meds.get(i)
//						.get("YFGG") + "");
//				m.setYFDW(meds.get(i).get("YFDW") == null ? "" : meds.get(i)
//						.get("YFDW") + "");
//				m.setCDMC(meds.get(i).get("CDMC") == null ? null : meds.get(i)
//						.get("CDMC") + "");
//				m.setYPPH(meds.get(i).get("YPPH") == null ? "" : meds.get(i)
//						.get("YPPH") + "");
//				m.setKSMC(meds.get(i).get("KSMC") == null ? null : meds.get(i)
//						.get("KSMC") + "");
//				m.setKWBM(meds.get(i).get("KWBM") == null ? "" : meds.get(i)
//						.get("KWBM") + "");
//				m.setYPGG(meds.get(i).get("YPGG") == null ? "" : meds.get(i)
//						.get("YPGG") + "");
//				m.setYPDW(meds.get(i).get("YPDW") == null ? "" : meds.get(i)
//						.get("YPDW") + "");
//				m.setKCSB(parseLong(meds.get(i).get("KCSB")));
//				m.setKCSL(parseDouble(meds.get(i).get("KCSL")));
//				m.setLSJE(parseDouble(meds.get(i).get("LSJE")));
//				m.setPFJE(parseDouble(meds.get(i).get("PFJE")));
//				m.setJHJE(parseDouble(meds.get(i).get("JHJE")));
//				m.setGYLJ(parseDouble(meds.get(i).get("GYLJ")));
//				m.setGYJJ(parseDouble(meds.get(i).get("GYJJ")));
//				m.setDWXH(parseLong(meds.get(i).get("DWXH")));
//				m.setNumKey((i + 1 == 10) ? 0 : i + 1);
//				m.setYPXH(parseLong(meds.get(i).get("YPXH")));
//				m.setYPCD(parseLong(meds.get(i).get("YPCD")));
//				m.setLSJG(parseDouble(meds.get(i).get("LSJG")));
//				m.setJHJG(parseDouble(meds.get(i).get("JHJG")));
//				m.setYFBZ(parseInt(meds.get(i).get("YFBZ")));
//				m.setZXBZ(parseInt(meds.get(i).get("ZXBZ")));
//				m.setPFJG(parseDouble(meds.get(i).get("PFJG")));
//				m.setYPXQ(meds.get(i).get("YPXQ") == null ? "" : sdf
//						.format((Date) meds.get(i).get("YPXQ")));
//				m.setKSDM(parseLong(meds.get(i).get("KSDM")));
//				m.setYPSL(parseDouble(meds.get(i).get("YPSL")));
//				m.setYKJH(parseDouble(meds.get(i).get("YKJH")));
//				m.setYKLJ(parseDouble(meds.get(i).get("YKLJ")));
//				m.setYKPJ(parseDouble(meds.get(i).get("YKPJ")));
//				m.setYKJJ(parseDouble(meds.get(i).get("YKJJ")));
//				m.setYFKCSB(parseLong(meds.get(i).get("YFKCSB")));
//				m.setJYLX(parseInt(meds.get(i).get("JYLX")));
//				m.setFYFS(parseLong(meds.get(i).get("FYFS")));
//				//增加以下代码用于采购入库零售价格根据定价方式计算
//				if(meds.get(i).containsKey("DJFS")){
//					int djfs=parseInt(meds.get(i).get("DJFS"));
//					m.setDJFS(djfs);
//					if(djfs==2||djfs==1){
//						if(meds.get(i).containsKey("DJGS")){
//							String djgs=parseString(meds.get(i).get("DJGS"));
//							if(djgs.length()>0){
//								m.setDJGS(djgs);
//								djgs=djgs.replaceAll("标准零价", m.getLSJG()+"");
//								djgs=djgs.replaceAll("实际进价", m.getJHJG()+"");
//								 CalculatorIn cal=new CalculatorIn();
//								 m.setBZLJ(m.getLSJG());
//								 m.setLSJG(cal.js(djgs));
//							}
//						}
//						
//					}
//				}
				Medicines.add(m);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			List<Map<String, Object>> reList = JSONUtil.ConvertObjToMapList(Medicines);
			res.put("mds", reList);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-5
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
	 * @createDate 2013-11-5
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
	 * @createDate 2013-11-5
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
	 * @createDate 2013-11-5
	 * @description double型数据转换
	 * @updateInfo
	 * @param number
	 *            保留小数点后几位
	 * @param data
	 *            数据
	 * @return
	 */
	public double formatDouble(int number, double data) {
		// if (number > 8) {
		// return 0;
		// }
		// double x = Math.pow(10, number);
		// return Math.round(data * x) / x;
		return BSPHISUtil.getDouble(data, number);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-11-5
	 * @description 数据转换成String,去空
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public String parseString(Object o) {
		if (o == null) {
			return "";
		}
		return o + "";
	}
}
