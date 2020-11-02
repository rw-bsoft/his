package phis.prints.bean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class InvoiceByxFile implements IHandler {

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		// 测试
		// response.put("YBXX1",
		// "测试医保信息----------------------------------------");
		// response.put("YBXX7",
		// "测试医保信息----------------------------------------");
		// response.put("XYF", "1");
		// response.put("ZYF", "2");
		// response.put("ZCY", "3");
		// response.put("GHF", "4");
		// response.put("ZCF", "5");
		// response.put("JCF", "6");
		// response.put("HYF", "7");
		// response.put("ZLF", "8");
		// response.put("SSF", "9");
		// response.put("SXF", "10");
		// response.put("SYF", "11");
		// response.put("CLF", "12");
		// response.put("QT", "13");
		try {
			BaseDAO dao = new BaseDAO(ctx);
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();
			String rq = sdfdate.format(new Date());
			String fphm = request.get("fphm") + "";
			String flag = request.get("flag") + "";
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> parametersSFXM = new HashMap<String, Object>();
			Map<String, Object> MZXX = new HashMap<String, Object>();
			List<Map<String, Object>> SFXMS = new ArrayList<Map<String, Object>>();
			StringBuffer hql = new StringBuffer(
					"select a.BRID as BRID,nvl(a.HZJE,0) as HZJE,nvl(a.JJZFJE,0) as JJZFJE,nvl(a.MZJZJE,0) as MZJZJE,a.XJJE as XJJE,a.BRXZ as BRXZ,d.XZMC as JSFS,d.XZMC as XZMC,c.PERSONNAME as SFY,a.MZXH as MZXH,b.MZHM as XLH,b.BRXM as XM,a.QTYS as JZ,a.ZJJE as HJJE,a.ZFJE as ZFJE,to_char(a.SFRQ,'yymmdd') as SFRQ,to_char(a.SFRQ,'yyyy') as YYYY,to_char(a.SFRQ,'mm') as MM,to_char(a.SFRQ,'dd') as DD from ");
			hql.append("MS_MZXX a,");
			hql.append("MS_BRDA b,");
			hql.append("SYS_Personnel c,");
			hql.append("GY_BRXZ d where a.BRXZ = d.BRXZ and a.CZGH = c.PERSONID and a.BRID = b.BRID and a.FPHM = :FPHM and a.JGID = :JGID");
			parameters.put("FPHM", fphm);
			parameters.put("JGID", JGID);

			MZXX = dao.doQuery(hql.toString(), parameters).get(0);

			StringBuffer sqlSFXM = new StringBuffer(
					"select b.MZPL as MZPL, sum(a.ZJJE) as ZJJE  from MS_SFMX a, GY_SFXM b"
							+ " where a.SFXM = b.SFXM  and a.MZXH =:MZXH group by b.MZPL ");
			// String cfhm = "";
			// if (MZXX.get("BRID") != null && MZXX.get("SFRQ") != null) {
			// cfhm = MZXX.get("SFRQ") + "" + MZXX.get("BRID") + "";
			// }
			parametersSFXM.put("MZXH", MZXX.get("MZXH"));
			SFXMS = dao.doSqlQuery(sqlSFXM.toString(), parametersSFXM);
			// StringBuffer hql2 = new
			// StringBuffer("select SFMC as SFMC,MZPL as MZPL from ");
			// hql2.append(BSPHISEntryNames.GY_SFXM);
			// hql2.append("  where SFXM =:MZGB");
			// System.out.println("----------------");
			// for (Map<String, Object> sfxm : SFXMS) {
			// // System.out.println(sfxm.toString());
			// }

			// 收费项目
			Double hjje = 0d;
			for (int i = 0; i < SFXMS.size(); i++) {
				Map<String, Object> SFXM = SFXMS.get(i);
				if ("1".equals(SFXM.get("MZPL") + "")) {
					response.put("XYF", SFXM.get("ZJJE") + "");
				} else if ("2".equals(SFXM.get("MZPL") + "")) {
					response.put("ZYF", SFXM.get("ZJJE") + "");
				} else if ("3".equals(SFXM.get("MZPL") + "")) {
					response.put("ZCY", SFXM.get("ZJJE") + "");
				} else if ("4".equals(SFXM.get("MZPL") + "")) {
					response.put("GHF", SFXM.get("ZJJE") + "");
				} else if ("5".equals(SFXM.get("MZPL") + "")) {
					response.put("ZCF", SFXM.get("ZJJE") + "");
				} else if ("6".equals(SFXM.get("MZPL") + "")) {
					response.put("JCF", SFXM.get("ZJJE") + "");
				} else if ("7".equals(SFXM.get("MZPL") + "")) {
					response.put("HYF", SFXM.get("ZJJE") + "");
				} else if ("8".equals(SFXM.get("MZPL") + "")) {
					response.put("ZLF", SFXM.get("ZJJE") + "");
				} else if ("9".equals(SFXM.get("MZPL") + "")) {
					response.put("SSF", SFXM.get("ZJJE") + "");
				} else if ("10".equals(SFXM.get("MZPL") + "")) {
					response.put("SXF", SFXM.get("ZJJE") + "");
				} else if ("11".equals(SFXM.get("MZPL") + "")) {
					response.put("SYF", SFXM.get("ZJJE") + "");
				} else if ("12".equals(SFXM.get("MZPL") + "")) {
					response.put("CLF", SFXM.get("ZJJE") + "");
				} else if ("13".equals(SFXM.get("MZPL") + "")) {
					response.put("QT", SFXM.get("ZJJE") + "");
				}
				hjje += parseDouble(SFXM.get("ZJJE"));

				// else if ("14".equals(SFXM.get("MZPL") + "")) {
				// response.put("TSFWF", SFXM.get("ZJJE") + "");
				// } else if ("15".equals(SFXM.get("MZPL") + "")) {
				// response.put("XY", SFXM.get("ZJJE") + "");
				// } else if ("16".equals(SFXM.get("MZPL") + "")) {
				// response.put("ZCY", SFXM.get("ZJJE") + "");
				// } else if ("17".equals(SFXM.get("MZPL") + "")) {
				// response.put("ZCAOY", SFXM.get("ZJJE") + "");
				// } else if ("18".equals(SFXM.get("MZPL") + "")) {
				// response.put("ZFYP", SFXM.get("ZJJE") + "");
				// }
			}
			response.put("HJ", numberToRMB(hjje));
			// System.out.println( numberToRMB(hjje));
			response.put("YBXX5", "                           合计：" + String.format("%1$.2f", hjje));
			String xm = MZXX.get("XM") + "";
			if (xm.length() > 5) {
				xm = xm.substring(0, 5);
			}
			// 电脑号
			response.put("YBKH", "发票号:" + fphm);
			//门诊号码
			response.put("BRMZHM", "门诊号码:" + MZXX.get("XLH") + "");
			response.put("BRXM", xm);
			response.put("XZMC", MZXX.get("XZMC") + "");
			if (flag.equals("true")) {
				response.put("BD", "补打");
			}
			response.put("SKR", MZXX.get("SFY") + "");
			response.put("RQ", rq);
			response.put("FPHM", fphm);
			response.put("YEAR", MZXX.get("YYYY") + "");
			response.put("MONTH", MZXX.get("MM") + "");
			response.put("DAY", MZXX.get("DD") + "");

			// 收费项目明细
			parameters.put("FYRQ", new Date());
			List<Map<String, Object>> sfmx_list = new ArrayList<Map<String, Object>>();
			//医技
			List<Map<String, Object>> yj_List = dao
					.doSqlQuery(
							"select g.FYDJ as FYDJ,d.SFMC as FYDL,c.FYMC as FYMC,c.FYDW as DW,a.YLDJ as DJ,a.YLSL as SL,a.HJJE as JE,a.ZFBL as ZFBL from "
									+ "MS_YJ02 a left join YB_FYDZ g on g.FYXH=a.YLXH and g.YBPB = 1 and g.KSSJ<=:FYRQ and (g.ZZSJ>=:FYRQ or g.ZZSJ is null),"
									+ "MS_YJ01 b,"
									+ "GY_YLSF c,"
									+ "GY_SFXM d"
									+" where a.YJXH = b.YJXH and a.YLXH=c.FYXH and a.FYGB = d.SFXM and b.FPHM=:FPHM and b.JGID=:JGID",
							parameters);
			//将草药与西药中药分开-liuxy
			List<Map<String, Object>> cf_List = dao
					.doSqlQuery(
							/**
							 * 2013-08-28 modify by gejj 修改2321bug 将e.YPDM
							 * 使用MS_CF02表中的药房单位(YFDW)
							 **/
							// "select d.SFMC as FYDL,e.YPMC as FYMC,e.YPDW as DW,a.YPDJ as DJ,a.YPSL as SL,a.HJJE as JE from "
							"select f.YFMC as YFMC,"
									+ " (select g.CKMC from YF_CKBH g where g.YFSB=b.YFSB and g.CKBH=b.FYCK and g.JGID=b.JGID) as CKMC,"
									+ "  d.SFMC as FYDL,e.YPMC as FYMC,a.YFDW as DW,a.YPDJ as DJ,a.YPSL as SL,a.HJJE as JE,a.ZFBL as ZFBL,h.YPDJ as FYDJ from "
									+ "MS_CF02 a left join YB_YPDZ h on a.YPXH=h.YPXH and a.YPCD=h.YPCD and h.YBPB = 1 and h.KSSJ<=:FYRQ and (h.ZZSJ>=:FYRQ or h.ZZSJ is null),"
									+ "MS_CF01 b,"
									+ "GY_SFXM d,"
									+ "YK_TYPK e,"
									+ "YF_YFLB f"
									+ " where a.CFSB = b.CFSB and a.FYGB = d.SFXM and a.YPXH=e.YPXH and b.FPHM=:FPHM and b.JGID=:JGID"
									+ " and f.YFSB=b.YFSB and f.JGID=b.JGID and a.XMLX in (1,2) order by a.XMLX",
							parameters);
			//草药
			List<Map<String, Object>> cf_List2 = dao
					.doSqlQuery(
							/**
							 * 2013-08-28 modify by gejj 修改2321bug 将e.YPDM
							 * 使用MS_CF02表中的药房单位(YFDW)
							 **/
							// "select d.SFMC as FYDL,e.YPMC as FYMC,e.YPDW as DW,a.YPDJ as DJ,a.YPSL as SL,a.HJJE as JE from "
							"select f.YFMC as YFMC,"
									+ " (select g.CKMC from YF_CKBH g where g.YFSB=b.YFSB and g.CKBH=b.FYCK and g.JGID=b.JGID) as CKMC,"
									+ "  d.SFMC as FYDL,e.YPMC as FYMC,a.YFDW as DW,a.YPDJ as DJ,a.YPSL as SL,a.HJJE as JE,a.ZFBL as ZFBL,a.CFTS as CFTS,h.YPDJ as FYDJ from "
									+ "MS_CF02 a left join YB_YPDZ h on a.YPXH=h.YPXH and a.YPCD=h.YPCD and h.YBPB = 1 and h.KSSJ<=:FYRQ and (h.ZZSJ>=:FYRQ or h.ZZSJ is null),"
									+ "MS_CF01 b,"
									+ "GY_SFXM d,"
									+ "YK_TYPK e,"
									+ "YF_YFLB f"
									+ " where a.CFSB = b.CFSB and a.FYGB = d.SFXM and a.YPXH=e.YPXH and b.FPHM=:FPHM and b.JGID=:JGID"
									+ " and f.YFSB=b.YFSB and f.JGID=b.JGID and a.XMLX=3",
							parameters);
			//顺序放入西药、中药、医技、草药
			sfmx_list.addAll(cf_List);
			sfmx_list.addAll(yj_List);
			//sfmx_list.addAll(cf_List2);
			// 增加取药药房信息
			if (cf_List.size() > 0) {
				String yfmc = "";
				String ckmc = "";
				if (cf_List.get(0).get("YFMC") != null
						&& cf_List.get(0).get("YFMC") != "") {
					yfmc = cf_List.get(0).get("YFMC") + "";
				}
				if (cf_List.get(0).get("CKMC") != null
						&& cf_List.get(0).get("CKMC") != "") {
					ckmc = cf_List.get(0).get("CKMC") + "";
				}
				response.put("QYXX", "请到:" + yfmc + " 药房, " + ckmc + " 窗口取药");
			}
			
			for (int i = 0; i < sfmx_list.size(); i++) {
				Map<String, Object> sfmx_map = sfmx_list.get(i);
				
				response.put("FYMC" + (i + 1), subStringNum((sfmx_map.get("FYMC") + ""),15));
				StringBuffer str = new StringBuffer("");
				//str.append(subStringNum(sfmx_map.get("FYMC") + "",12));
				
				int ypdj = parseInt(sfmx_map.get("FYDJ"));
				str.append("  "+checkLB(ypdj));
				str.append("   "+subStringNum(String.format("%1$.0f", sfmx_map.get("SL")),3));
				str.append(" "+subStringNum(String.format("%1$.2f", sfmx_map.get("JE")),8));
				//根据cf02、yj02的自负比例填充类别
//				Double zfbl = parseDouble(sfmx_map.get("ZFBL"));
//				if (zfbl == 0) {
//					response.put("LB" + (i + 1), "甲");
//				} else if (zfbl == 1) {
//					response.put("LB" + (i + 1), "丙");
//				} else {
//					response.put("LB" + (i + 1), "乙");
//				}
				if(parseDouble(sfmx_map.get("ZFBL"))>=0){
//					response.put("ZLZF" + (i + 1),
//							String.format("%1$.0f%%", parseDouble(sfmx_map.get("ZFBL")) * 100));
					str.append("   "+subStringNum(String.format("%1$.0f%%", parseDouble(sfmx_map.get("ZFBL")) * 100),5));
				}
				response.put("FYXX"+(i+1), str.toString());				
				if (i == 16) {
					response.put("MESSAGE", "项目过多,请到财务科打印详细清单");
					break;
				}
			}
			//草药
			int max = cf_List2.size()%2==0?cf_List2.size()/2:cf_List2.size()/2+1;
			for (int i = 0; i < max; i++) {
				if(i==0){
					response.put("FYMC"+(sfmx_list.size()+1), "   草药总帖数："+cf_List2.get(0).get("CFTS")+"贴");
				}
				Map<String, Object> sfmx_map = cf_List2.get(i*2);
				Map<String, Object> sfmx_map2 = new HashMap<String,Object>();
				if(i*2+1<cf_List2.size()){
					sfmx_map2 = cf_List2.get(i*2+1);
				}
				
				StringBuffer str1 = new StringBuffer("");
				int ypdj = parseInt(sfmx_map.get("FYDJ"));
				str1.append("("+checkLB(ypdj)+")");
				str1.append(" "+subStringNum(sfmx_map.get("FYMC") + ""));
				str1.append("   "+subStringNum(String.format("%1$.0f", sfmx_map.get("SL")),3));
				str1.append("   "+subStringNum(String.format("%1$.2f", sfmx_map.get("JE")),8));
				response.put("FYMC"+(sfmx_list.size()+i+2), str1.toString());
				if(sfmx_map2!=null && sfmx_map2.size()>0){				
					StringBuffer str2 = new StringBuffer("");
					int ypdj2 = parseInt(sfmx_map2.get("FYDJ"));
					str2.append("("+checkLB(ypdj2)+")");
					str2.append(" "+subStringNum(sfmx_map2.get("FYMC") + ""));
					str2.append("   "+subStringNum(String.format("%1$.0f", sfmx_map2.get("SL")),3));
					str2.append("   "+subStringNum(String.format("%1$.2f", sfmx_map2.get("JE")),8));
					response.put("FYXX"+(sfmx_list.size()+i+2), str2.toString());
				}
				if (sfmx_list.size()+i+2 == 16) {
					response.put("MESSAGE", "项目过多,请到财务科打印详细清单");
					break;
				}
			}
			// System.out.println(response.toString());
			// 医保信息
			// String SHIYB=ParameterUtil.getParameter(_JGID, _CSMC, _MRZ, _BZ,
			// ctx)
			
			String brxz=MZXX.get("BRXZ")+"";
			String SHIYB = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "SHIYB", "0", "市医保病人性质", ctx);
			String SHENGYB = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "SHENGYB", "0", "省医保病人性质", ctx);
			String YHYB = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "YHYB", "0", "余杭医保病人性质", ctx);
			if (brxz.equals(SHIYB)) {
				String sqlYBXX = "select a.YLLB as YLLB,a.RYLB as RYLB,a.BNZHZF as BNZHZF,a.LNZHZF as LNZHZF,a.GRZFJE as GRZFJE,"
						+ " a.LNZHZL as LNZHZL,a.HBNZHYE as BNZHYE,a.HLNZHYE as LNZHYE,"
						+ " (a.TCZF+a.LXJJ+a.LFJJ+a.ZNTCJJ+a.LXJSJJ+a.SNETJJ+a.LNJMJJ+a.GWJJZF+a.ZZTCJJ+a.TXTCJJ+a.NMGJJ+a.JZJZF+a.TJJJ"
						+ " +a.DXSJJ+a.LMJJ+a.XNHJJ) as TCJJ,"
						+ " a.KNJZJJ as KNJZJJ,a.GRXJZF as GRXJZF,a.SCZL as SCZL,a.SCZF as SCZF,a.BCQF as BCQF, "
						+ " (a.YLYPZL+a.TJZL+a.TZZL-a.LNZHZL-a.SCZL) as LD_ZLJE,"
						+ " (a.GRXJZF-(a.YLYPZL+a.TJZL+a.TZZL-a.LNZHZL-a.SCZL)-a.GRZFJE) as LD_ZFJE,"
						+ " c.NMGDYXSBZ as NMGDYXSBZ,c.XNHBZ as XNHBZ,c.DXSBZ as DXSBZ,c.PTRYXSBJDYBZ as PTRYXSBJDYBZ,a.MZQFXLJ  as QFLNZF "
						+ " from YB_MZJS a,MS_MZXX b,YB_CBRYJBXX c where a.MZXH=:MZXH and a.MZXH=b.MZXH and c.BRID=b.BRID";
				Map<String, Object> YB_mzJS = dao.doLoad(sqlYBXX, parametersSFXM);
	
				if (YB_mzJS == null || YB_mzJS.size() == 0) {
					response.put("XJZF", "现金支付：" + String.format("%1$.2f", hjje));
					return;
				}
				response.put("XJZF",
						"现金支付：" + String.format("%1$.2f", YB_mzJS.get("GRXJZF")));
	
				String yllb = "";
				String xzlb = "";
				String rylb = "";
	
				if ("11".equals(YB_mzJS.get("YLLB"))) {
					yllb = "普通门诊";
				} else if ("31".equals(YB_mzJS.get("YLLB"))) {
					yllb = "规定病种门诊";
				} else {
					yllb = "药店购药";
				}
	
				if ("111".equals(YB_mzJS.get("RYLB"))
						|| "112".equals(YB_mzJS.get("RYLB"))) {
					xzlb = "城乡医保";
				} else if (parseInt(YB_mzJS.get("NMGDYXSBZ")) == 1) {
					xzlb = "农民工医保";
				} else if ("31".equals(YB_mzJS.get("RYLB"))
						|| "32".equals(YB_mzJS.get("RYLB"))
						|| "33".equals(YB_mzJS.get("RYLB"))
						|| "34".equals(YB_mzJS.get("RYLB"))
						|| "35".equals(YB_mzJS.get("RYLB"))
						|| "36".equals(YB_mzJS.get("RYLB"))
						|| "37".equals(YB_mzJS.get("RYLB"))
						|| "38".equals(YB_mzJS.get("RYLB"))
						|| "39".equals(YB_mzJS.get("RYLB"))
						|| "3A".equals(YB_mzJS.get("RYLB"))
						|| "3B".equals(YB_mzJS.get("RYLB"))
						|| "3C".equals(YB_mzJS.get("RYLB"))
						|| "3D".equals(YB_mzJS.get("RYLB"))) {
					xzlb = "公费医疗";
				} else if (parseInt(YB_mzJS.get("XNHBZ")) == 1) {
					xzlb = "新农合";
				} else if (parseInt(YB_mzJS.get("DXSBZ")) == 1) {
					xzlb = "大学生";
				} else {
					xzlb = "职工医保";
				}
	
				if (parseInt(YB_mzJS.get("PTRYXSBJDYBZ")) > 0) {
					rylb = "保健干部";
				} else {
					Map<String, Object> parametersDMSB = new HashMap<String, Object>();
					parametersDMSB.put("DMSB", YB_mzJS.get("RYLB") + "");
					Map<String, Object> map_DMMC = dao
							.doLoad("select DMMC as RYMC from YB_DMZD where DMLB='AKC021' and DMSB=:DMSB",
									parametersDMSB);
					if (map_DMMC == null || map_DMMC.size() == 0
							|| map_DMMC.get("RYMC") == "") {
						rylb = YB_mzJS.get("RYLB") + "";
					} else {
						rylb = map_DMMC.get("RYMC") + "";
					}
				}
	
				response.put("YBXX1", "医疗类别: " + yllb + "   险种类别:" + xzlb
						+ "   人员类别:" + rylb);
				response.put(
						"YBXX2",
						"当年帐户支付:"
								+ String.format("%1$.2f", YB_mzJS.get("BNZHZF"))
								+ ";历年帐户支付:"
								+ String.format("%1$.2f", YB_mzJS.get("LNZHZF"))
								+ " 其中(自理:"
								+ String.format("%1$.2f", YB_mzJS.get("LNZHZL"))
								+ "自负:"
								+ String.format("%1$.2f", (parseDouble(YB_mzJS
										.get("LNZHZF")) - parseDouble(YB_mzJS
										.get("LNZHZL")))) + ")");
				response.put(
						"YBXX3",
						"当年帐户余额:"
								+ String.format("%1$.2f", (parseDouble(YB_mzJS
										.get("BNZHYE"))))
								+ ";历年帐户余额:"
								+ String.format("%1$.2f", (parseDouble(YB_mzJS
										.get("LNZHYE")))));
				response.put(
						"YBXX4",
						"统筹基金支付:" + String.format("%1$.2f", YB_mzJS.get("TCJJ"))
								+ ";医疗救助基金支付:"
								+ String.format("%1$.2f", YB_mzJS.get("KNJZJJ")));
				response.put(
						"YBXX5",
						"现金支付:"
								+ String.format("%1$.2f",
										parseDouble(YB_mzJS.get("GRXJZF")))
								+ "其中(自理:"
								+ String.format("%1$.2f",
										parseDouble(YB_mzJS.get("LD_ZLJE")))
								+ "自费:"
								+ String.format("%1$.2f",
										parseDouble(YB_mzJS.get("GRZFJE")))
								+ ";自负:"
								+ String.format("%1$.2f",
										parseDouble(YB_mzJS.get("LD_ZFJE"))) + ")");
	
				//
				if (parseDouble(YB_mzJS.get("SCZL")) > 0
						|| parseDouble(YB_mzJS.get("SCZF")) > 0) {
					response.put(
							"YBXX6",
							"其中伤残基金支付自负: "
									+ String.format("%1$.2f", YB_mzJS.get("SCZF"))
									+ " 其中伤残基金支付自理: "
									+ String.format("%1$.2f", YB_mzJS.get("SCZL")));
					if (YB_mzJS.get("YLLB").equals("31")) {
						response.put("YBXX7", "规定病种");
					} else {
						response.put(
								"YBXX7",
								"门诊起付标准支付:"
										+ String.format("%1$.2f",
												YB_mzJS.get("BCQF"))
										+ "门诊起付标准累计:"
										+ String.format(
												"%1$.2f",
												(parseDouble(YB_mzJS.get("QFLNZF")) + parseDouble(YB_mzJS
														.get("BCQF")))));
					}
				} else {
					if ("31".equals(YB_mzJS.get("YLLB"))) {
						response.put("YBXX6", "规定病种");
					} else {
						response.put(
								"YBXX6",
								"门诊起付标准支付:"
										+ String.format("%1$.2f",
												YB_mzJS.get("BCQF"))
										+ "门诊起付标准累计:"
										+ String.format(
												"%1$.2f",
												(parseDouble(YB_mzJS.get("QFLNZF")) + parseDouble(YB_mzJS
														.get("BCQF")))));
					}
				}
			} else if(brxz.equals(SHENGYB)){
				
				String sqlSYB = "select a.BNZH as BNZH,a.WNZH as WNZH,b.BNZHJYJE as BNZHJYJE,b.LNZHJYJE as LNZHJYJE,a.GWBZ as GWBZ,a.GRXJ as GRXJ,a.ZLZE as ZLZE,a.ZFZE as ZFZE,a.TCJJ as TCJJ,a.DBJZ as DBJZ"
						+ " from SJYB_MZJS a,SJYB_BRXX b"
						+ " where a.FPHM=:FPHM and a.JGID=:JGID and a.YBKH=b.YBKH";
				parameters.remove("FYRQ");
				Map<String, Object> SYB_mzJS = dao.doLoad(sqlSYB, parameters);
				if(SYB_mzJS==null || SYB_mzJS.size()<=0){
					response.put("YBXX5", "                         未找到该省医保病人信息");
					return;
				}
				response.put("YBXX3", "本年账户支付："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("BNZH")))+"      历年账户支付："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("WNZH"))));
				response.put("YBXX4", "当年账户余额："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("BNZHJYJE")))+"      历年账户余额："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("LNZHJYJE"))));
				response.put("YBXX5", "公务员补助支付："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("GWBZ")))+"      现金支付："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("GRXJ"))));
				String s = String.format("%1$.2f",(parseDouble(SYB_mzJS.get("GRXJ"))-parseDouble(SYB_mzJS.get("ZFZE"))-parseDouble(SYB_mzJS.get("ZLZE")))>=0?(parseDouble(SYB_mzJS.get("GRXJ"))-parseDouble(SYB_mzJS.get("ZFZE"))-parseDouble(SYB_mzJS.get("ZLZE"))):0);
				response.put("YBXX6", "其中(自理："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("ZLZE")))+"自费："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("ZFZE")))+"自负："+s+")");
				response.put("YBXX7", "统筹基金："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("TCJJ")))+"      救助基金："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("DBJZ"))));
				response.put("XJZF","现金支付："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("GRXJ"))));
			} else if(brxz.equals(YHYB)){
				String sqlYHYB = "select a.DNZH as DNZH,a.LNZH as LNZH,a.GWYBZJJ as GWYBZJJ,a.LXJJ as LXJJ,a.TCJJ as TCJJ,a.BCYL as BCYL," +
						"a.YFJJ as YFJJ,a.JZJJ as JZJJ,a.BLJJ as BLJJ,a.XJHJ as XJHJ" +
						" from YHYB_MZJS a,YHYB_BRXX b" +
						" where a.FPHM=:FPHM and a.JGID=:JGID and a.INDI_ID=b.INDI_ID";
				parameters.remove("FYRQ");
				Map<String, Object> YHYB_mzJS = dao.doLoad(sqlYHYB, parameters);
				if(YHYB_mzJS==null || YHYB_mzJS.size()<=0){
					response.put("YBXX5", "                         未找到该省医保病人信息");
					return;
				}
				response.put("YBXX3", "当年个人帐户支付："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("DNZH")))+"  历年个人帐户支付 ："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("LNZH"))));
				response.put("YBXX4", "医疗保险公务员补助："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("GWYBZJJ")))+"  医疗保险离休基金 ："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("LXJJ"))));
				response.put("YBXX5", "医疗保险统筹基金："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("TCJJ")))+"  医疗保险大病救助基金 ："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("BCYL"))));
				response.put("YBXX6", "民政优抚对象补助："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("YFJJ")))+"  社会医疗救助基金 ："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("JZJJ"))));
				response.put("YBXX7", "医疗保险剥离基金："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("BLJJ")))+"  现金支付(合计) ："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("XJHJ"))));
				response.put("XJZF","现金支付："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("XJHJ"))));
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	private static String numberToZH4(String s) {
		String zhnum_0 = "零壹贰叁肆伍陆柒捌玖";
		String[] zhnum1_0 = { "", "拾", "佰", "仟" };
		StringBuilder sb = new StringBuilder();
		if (s.length() != 4)
			return null;
		for (int i = 0; i < 4; i++) {
			char c1 = s.charAt(i);
			if (c1 == '0' && i > 1 && s.charAt(i - 1) == '0')
				continue;
			if (c1 != '0' && i > 1 && s.charAt(i - 1) == '0')
				sb.append('零');
			if (c1 != '0') {
				sb.append(zhnum_0.charAt(c1 - 48));
				sb.append(zhnum1_0[4 - i - 1]);
			}
		}
		return new String(sb);
	}

	public static String numberToZH(long n) {
		String[] zhnum2 = { "", "万", "亿", "万亿", "亿亿" };
		StringBuilder sb = new StringBuilder();
		String strN = "000" + n;
		int strN_L = strN.length() / 4;
		strN = strN.substring(strN.length() - strN_L * 4);
		for (int i = 0; i < strN_L; i++) {
			String s1 = strN.substring(i * 4, i * 4 + 4);
			String s2 = numberToZH4(s1);
			sb.append(s2);
			if (s2.length() != 0)
				sb.append(zhnum2[strN_L - i - 1]);
		}
		String s = new String(sb);
		if (s.length() != 0 && s.startsWith("零"))
			s = s.substring(1);
		return s;
	}

	public static String numberToZH(double d) {
		return numberToZH("" + d);
	}

	/**
	 * Description: 数字转化成整数
	 * 
	 * @param str
	 * @param fan
	 * @return
	 */
	public static String numberToZH(String str) {
		String zhnum_0 = "零壹贰叁肆伍陆柒捌玖";
		StringBuilder sb = new StringBuilder();
		int dot = str.indexOf(".");
		if (dot < 0)
			dot = str.length();

		String zhengshu = str.substring(0, dot);
		sb.append(numberToZH(Long.parseLong(zhengshu)));
		if (dot != str.length()) {
			sb.append("点");
			String xiaoshu = str.substring(dot + 1);
			for (int i = 0; i < xiaoshu.length(); i++) {
				sb.append(zhnum_0.charAt(Integer.parseInt(xiaoshu.substring(i,
						i + 1))));
			}
		}
		String s = new String(sb);
		if (s.startsWith("零"))
			s = s.substring(1);
		if (s.startsWith("一十"))
			s = s.substring(1);
		while (s.endsWith("零")) {
			s = s.substring(0, s.length() - 1);
		}
		if (s.endsWith("点"))
			s = s.substring(0, s.length() - 1);
		return s;
	}

	public String numberToRMB(double rmb) {
		String strRMB = "" + rmb;
		DecimalFormat nf = new DecimalFormat("#.#");
		nf.setMaximumFractionDigits(2);
		strRMB = nf.format(rmb).toString();
		strRMB = numberToZH(strRMB);
		if (strRMB.indexOf("点") >= 0) {
			strRMB = strRMB + "零";
			strRMB = strRMB.replaceAll("点", "圆");
			String s1 = strRMB.substring(0, strRMB.indexOf("圆") + 1);
			String s2 = strRMB.substring(strRMB.indexOf("圆") + 1);
			strRMB = s1 + s2.charAt(0) + "角" + s2.charAt(1) + "分整";
		} else {
			strRMB = strRMB + "圆整";
		}
		if (rmb < 1) {
			strRMB = "零" + strRMB;
		}
		return strRMB;
	}

	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	public long parseLong(Object o) {
		if (o == null) {
			return 0L;
		}
		return Long.parseLong(o + "");
	}

	public double parseDouble(Object o) {
		if (o == null || "".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
	//判断类型
	public String checkLB(int ypdj){
		if (ypdj == 1) {
			return "甲";
		} else if (ypdj == 2) {
			return "乙";
		} else {
			return "丙";
		}
	}
	//字符串长度控制
	public String subStringNum(String s,int i){
		if(s.length()>i){
			return s.substring(0, i);
		} else {
			return StringUtils.rightPad(s, i, " ");
		}
	}
	//草药长度控制
	public String subStringNum(String s){
		if(s.length()>4){
			s = s.substring(0, 4);
		}
		for(int i=s.length()-2;i<2;i++){
			s+="  ";
		}
		return s;
	}
}
