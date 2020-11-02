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

public class InvoiceByxFileNew implements IHandler {

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String fphm = request.get("fphm") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FPHM", fphm);
		parameters.put("JGID", JGID);
		StringBuffer sqlSFXM = new StringBuffer(
				"select b.MZPL as MZPL, sum(a.ZJJE) as ZJJE,sum(a.ZFJE) as ZFJE from MS_SFMX a, GY_SFXM b"
						+ " where a.SFXM = b.SFXM  and a.FPHM =:FPHM and a.JGID=:JGID group by b.MZPL order by b.MZPL");
		StringBuffer ypSQL = new StringBuffer("select f.YFMC as YFMC,"
				+ " (select g.CKMC from YF_CKBH g where g.YFSB=b.YFSB and g.CKBH=b.FYCK and g.JGID=b.JGID) as CKMC,"
				+ "  d.SFMC as FYDL,e.YPMC as FYMC,a.YFDW as DW,a.YPDJ as DJ,a.YPSL as SL,a.HJJE as JE,a.ZFBL as ZFBL,a.CFTS as CFTS,h.YPDJ as FYDJ from "
				+ "MS_CF02 a left join YB_YPDZ h on a.YPXH=h.YPXH and a.YPCD=h.YPCD and h.YBPB = 1 and h.KSSJ<=:FYRQ and (h.ZZSJ>=:FYRQ or h.ZZSJ is null),"
				+ "MS_CF01 b, GY_SFXM d, YK_TYPK e, YF_YFLB f"
				+ " where a.CFSB = b.CFSB and a.FYGB = d.SFXM and a.YPXH=e.YPXH and b.FPHM=:FPHM and b.JGID=:JGID"
				+ " and f.YFSB=b.YFSB and f.JGID=b.JGID and a.XMLX=:XMLX");
		StringBuffer yjSQL = new StringBuffer("select g.FYDJ as FYDJ,d.SFMC as FYDL,c.FYMC as FYMC,c.FYDW as DW,a.YLDJ as DJ,a.YLSL as SL,a.HJJE as JE,a.ZFBL as ZFBL from "
				+ "MS_YJ02 a left join YB_FYDZ g on g.FYXH=a.YLXH and g.YBPB = 1 and g.KSSJ<=:FYRQ and (g.ZZSJ>=:FYRQ or g.ZZSJ is null),"
				+ "MS_YJ01 b,"
				+ "GY_YLSF c,"
				+ "GY_SFXM d"
				+" where a.YJXH = b.YJXH and a.YLXH=c.FYXH and a.FYGB = d.SFXM and b.FPHM=:FPHM and b.JGID=:JGID and d.MZPL=:MZPL");
		try {
			List<Map<String, Object>> SFXMS = new ArrayList<Map<String, Object>>();
			SFXMS = dao.doSqlQuery(sqlSFXM.toString(), parameters);
			
			for (int i = 0; i < SFXMS.size(); i++) {
				Map<String, Object> parameters2 = new HashMap<String, Object>();//条件参数
				parameters2.put("FPHM", fphm);
				parameters2.put("JGID", JGID);
				parameters2.put("FYRQ", new Date());
				Map<String, Object> SFXM = SFXMS.get(i);
				Map<String, Object> map = new HashMap<String,Object>();//收费大项
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();//收费明细
 				if ("1".equals(SFXM.get("MZPL") + "")) {
 					map = putSFLX("【西药费】",String.format("%1$.2f", SFXM.get("ZJJE")));
 					parameters2.put("XMLX", "1");
 					list = dao.doSqlQuery(ypSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
 					
				} else if ("2".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【中药费】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("XMLX", "3");
					list = dao.doSqlQuery(ypSQL.toString(), parameters2);
					map.put("SL", "共" + list.get(0).get("CFTS") + "贴");
 					records.add(map);
 					records.addAll(putSFMX(list));
					
				} else if ("3".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【中成药】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("XMLX", "2");
					list = dao.doSqlQuery(ypSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
					
				} else if ("4".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【挂号费】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("MZPL", 4);
					list = dao.doSqlQuery(yjSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
					
				} else if ("5".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【诊查费】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("MZPL", 5);
					list = dao.doSqlQuery(yjSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
				} else if ("6".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【检查费】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("MZPL", 6);
					list = dao.doSqlQuery(yjSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
				} else if ("7".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【化验费】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("MZPL", 7);
					list = dao.doSqlQuery(yjSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
				} else if ("8".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【治疗费】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("MZPL", 8);
					list = dao.doSqlQuery(yjSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
				} else if ("9".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【手术费】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("MZPL", 9);
					list = dao.doSqlQuery(yjSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
				} else if ("10".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【输血费】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("MZPL", 10);
					list = dao.doSqlQuery(yjSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
				} else if ("11".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【输氧费】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("MZPL", 11);
					list = dao.doSqlQuery(yjSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
				} else if ("12".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【材料费】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("MZPL", 12);
					list = dao.doSqlQuery(yjSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
				} else if ("13".equals(SFXM.get("MZPL") + "")) {
					map = putSFLX("【其  他】",String.format("%1$.2f", SFXM.get("ZJJE")));
					parameters2.put("MZPL", 13);
					list = dao.doSqlQuery(yjSQL.toString(), parameters2);
 					records.add(map);
 					records.addAll(putSFMX(list));
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
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
					"select b.BRXB as BRXB,a.BRID as BRID,nvl(a.HZJE,0) as HZJE,nvl(a.JJZFJE,0) as JJZFJE,nvl(a.MZJZJE,0) as MZJZJE,a.XJJE as XJJE,a.BRXZ as BRXZ,d.XZMC as JSFS,d.XZMC as XZMC,c.PERSONNAME as SFY,a.MZXH as MZXH,b.MZHM as XLH,b.BRXM as XM,a.QTYS as JZ,a.ZJJE as HJJE,a.ZFJE as ZFJE,to_char(a.SFRQ,'yymmdd') as SFRQ,to_char(a.SFRQ,'yyyy') as YYYY,to_char(a.SFRQ,'mm') as MM,to_char(a.SFRQ,'dd') as DD from ");
			hql.append("MS_MZXX a,");
			hql.append("MS_BRDA b,");
			hql.append("SYS_Personnel c,");
			hql.append("GY_BRXZ d where a.BRXZ = d.BRXZ and a.CZGH = c.PERSONID and a.BRID = b.BRID and a.FPHM = :FPHM and a.JGID = :JGID");
			parameters.put("FPHM", fphm);
			parameters.put("JGID", JGID);
			
			MZXX = dao.doQuery(hql.toString(), parameters).get(0);
			
			Double hjje = parseDouble(MZXX.get("HJJE")); 
			response.put("HJXX", String.format("%1$.2f",hjje));
			response.put("HJDX", numberToRMB(hjje));//收费大写 
			response.put("XJZF", String.format("%1$.2f",hjje));
			// System.out.println( numberToRMB(hjje));
//			response.put("YBXX5", "                           合计：" + String.format("%1$.2f", hjje));
			String xm = MZXX.get("XM") + "";
			if (xm.length() > 5) {
				xm = xm.substring(0, 5);
			}
			// 电脑号
			response.put("YBKH", "发票号:" + fphm);
			//门诊号码
			response.put("BRMZHM", MZXX.get("XLH") + "");
			response.put("BRXM", xm);
			response.put("XZMC", MZXX.get("XZMC") + "");
			if (flag.equals("true")) {
				response.put("BD", "补打");
			}
			response.put("BRXB", (MZXX.get("BRXB")+"").equals("1")?"男":"女");
			response.put("SKR", MZXX.get("SFY") + "");
			response.put("RQ", rq);
			response.put("FPHM", fphm);
			response.put("YEAR", MZXX.get("YYYY") + "");
			response.put("MONTH", MZXX.get("MM") + "");
			response.put("DAY", MZXX.get("DD") + "");
			String brxz=MZXX.get("BRXZ")+"";
			String SHIYB = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "SHIYB", "0", "市医保病人性质", ctx);
			String SHENGYB = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "SHENGYB", "0", "省医保病人性质", ctx);
			String YHYB = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "YHYB", "0", "余杭医保病人性质", ctx);
			if (brxz.equals(SHIYB)) {
				parametersSFXM.put("MZXH",parseLong(MZXX.get("MZXH")));
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
					response.put("XJZF", String.format("%1$.2f", hjje));
					return;
				}
				//response.put("XJZF", String.format("%1$.2f", YB_mzJS.get("GRXJZF")));
				response.put(
						"XJZF",
						""
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
				response.put("BNZF", String.format("%1$.2f", YB_mzJS.get("BNZHZF")));
				response.put("LNZF", String.format("%1$.2f", YB_mzJS.get("LNZHZF")));				
				response.put("BNZHYE", String.format("%1$.2f", (parseDouble(YB_mzJS
												.get("BNZHYE")))));
				response.put("LNZHYE", String.format("%1$.2f", (parseDouble(YB_mzJS
														.get("LNZHYE")))));
				response.put("YBXX1", "统筹基金支付:" + String.format("%1$.2f", YB_mzJS.get("TCJJ"))
												+ ";医疗救助基金支付:"
												+ String.format("%1$.2f", YB_mzJS.get("KNJZJJ")));
				if (parseDouble(YB_mzJS.get("SCZL")) > 0
						|| parseDouble(YB_mzJS.get("SCZF")) > 0) {
					response.put(
							"YBXX2",
							"其中伤残基金支付自负: "
									+ String.format("%1$.2f", YB_mzJS.get("SCZF"))
									+ " 其中伤残基金支付自理: "
									+ String.format("%1$.2f", YB_mzJS.get("SCZL")));
					if (YB_mzJS.get("YLLB").equals("31")) {
						response.put("YBXX2", "规定病种");
					} else {
						response.put(
								"YBXX2",
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
						response.put("YBXX2", "规定病种");
					} else {
						response.put(
								"YBXX2",
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
					response.put("YBXX1", "                         未找到该省医保病人信息");
					return;
				}
				response.put("BNZF", String.format("%1$.2f",parseDouble(SYB_mzJS.get("BNZH"))));
				response.put("LNZF", String.format("%1$.2f",parseDouble(SYB_mzJS.get("WNZH"))));		
				response.put("BNZHYE", String.format("%1$.2f",parseDouble(SYB_mzJS.get("BNZHJYJE"))));
				response.put("LNZHYE", String.format("%1$.2f",parseDouble(SYB_mzJS.get("LNZHJYJE"))));
				String s = String.format("%1$.2f",(parseDouble(SYB_mzJS.get("GRXJ"))-parseDouble(SYB_mzJS.get("ZFZE"))-parseDouble(SYB_mzJS.get("ZLZE")))>=0?(parseDouble(SYB_mzJS.get("GRXJ"))-parseDouble(SYB_mzJS.get("ZFZE"))-parseDouble(SYB_mzJS.get("ZLZE"))):0);
				response.put("YBXX1", "公务员补助支付："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("GWBZ")))+
						"其中(自理："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("ZLZE")))+"自费："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("ZFZE")))+"自负："+s+")");
				response.put("YBXX2", "统筹基金："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("TCJJ")))+"      救助基金："+String.format("%1$.2f",parseDouble(SYB_mzJS.get("DBJZ"))));
				response.put("XJZF", String.format("%1$.2f",parseDouble(SYB_mzJS.get("GRXJ"))));
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
				response.put("BNZF", String.format("%1$.2f",parseDouble(YHYB_mzJS.get("DNZH"))));
				response.put("LNZF", String.format("%1$.2f",parseDouble(YHYB_mzJS.get("LNZH"))));		
				response.put("YBXX1", "医疗保险公务员补助："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("GWYBZJJ")))+"  医疗保险离休基金 ："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("LXJJ")))
							+"医疗保险统筹基金："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("TCJJ")))+"  医疗保险大病救助基金 ："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("BCYL"))));
				response.put("YBXX2", "民政优抚对象补助："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("YFJJ")))+"  社会医疗救助基金 ："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("JZJJ")))
							+"医疗保险剥离基金："+String.format("%1$.2f",parseDouble(YHYB_mzJS.get("BLJJ"))));
				response.put("XJZF",String.format("%1$.2f",parseDouble(YHYB_mzJS.get("XJHJ"))));
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
	//大项目类型控制
	public Map<String,Object> putSFLX(String name,String money){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("XMMC", name);
		map.put("JE", money);
		map.put("LX", "");
		map.put("SL", "");
		map.put("ZFBL", "");
		return map;
	}
	//收费明细控制
	public List<Map<String,Object>> putSFMX(List<Map<String,Object>> list){
		List<Map<String,Object>> list2 = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> map = new HashMap<String,Object>();
			Map<String, Object> sfmx_map = list.get(i);
			
			map.put("XMMC", subStringNum((sfmx_map.get("FYMC") + ""),13));
			int ypdj = parseInt(sfmx_map.get("FYDJ"));
			map.put("LX", checkLB(ypdj));
			map.put("SL", String.format("%1$.0f", sfmx_map.get("SL")));
			map.put("JE", String.format("%1$.2f", sfmx_map.get("JE")));
			if(parseDouble(sfmx_map.get("ZFBL"))>=0){
				map.put("ZFBL", String.format("%1$.0f%%", parseDouble(sfmx_map.get("ZFBL")) * 100));
			}
			list2.add(map);
		}
		return list2;
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
