package phis.prints.bean;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalInvoiceByxFile implements IHandler {

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// SimpleDateFormat sdfM = new SimpleDateFormat("MM");
		// SimpleDateFormat sdfD = new SimpleDateFormat("dd");
		String fphm = request.get("fphm") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String nf = sdf.format(new Date());
		// String yr = sdfM.format(new Date());
		// String r = sdfD.format(new Date());
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> JSXX = new HashMap<String, Object>();
		List<Map<String, Object>> SFXMS = new ArrayList<Map<String, Object>>();
		Map<String, Object> JKJES = new HashMap<String, Object>();
		Map<String, Object> YBJES = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer(
				"select a.BRXZ as BRXZ,a.JSCS as JSCS,a.JKHJ as JKHJ,d.XZMC as RYLB,c.PERSONNAME as SYY,b.ZYH as ZYH,b.GZDW as GZDW,b.BRCH as BRCH,b.ZYHM as XLH,b.BRXM as XM,to_char(b.RYRQ,'yyyy-mm-dd hh24:mi:ss') as RYRQ,to_char(b.RYRQ,'yyyymmdd') as RYRQ1,to_char(b.CYRQ,'yyyy-mm-dd hh24:mi:ss') as CYRQ,to_char(b.CYRQ,'yyyymmdd') as CYRQ1,to_char(b.RYRQ, 'mm') as RYMM,to_char(b.RYRQ, 'dd') as RYDD,to_char(b.CYRQ, 'mm') as CYMM,to_char(b.CYRQ, 'dd') as CYDD,a.FYHJ as HJJE,a.ZFHJ as ZFJE,to_char(a.JSRQ, 'yyyy') as YYYY,to_char(a.JSRQ, 'mm') as MM,to_char(a.JSRQ, 'dd') as DD from ");
		hql.append("ZY_ZYJS");
		hql.append(" a,");
		hql.append("ZY_BRRY");
		hql.append(" b,");
		hql.append("SYS_Personnel");
		hql.append(" c,");
		hql.append("GY_BRXZ");
		hql.append(" d where a.BRXZ = d.BRXZ and a.CZGH = c.PERSONID and a.ZYH = b.ZYH and a.FPHM = :FPHM and a.JGID = :JGID");
		parameters.put("FPHM", fphm);
		parameters.put("JGID", JGID);
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		StringBuffer hql1 = new StringBuffer(
				"select b.ZYPL as ZYPL,sum(a.ZJJE) as ZJJE from ");
		hql1.append("ZY_FYMX");
		hql1.append(" a,");
		hql1.append("GY_SFXM");
		hql1.append(" b where a.FYXM = b.SFXM and a.ZYH = :ZYH and a.JSCS = :JSCS group by b.ZYPL");
		
		//根据住院归并ZYGB查找收费项目SFXM获得住院排列ZYPL
//		StringBuffer hql3 = new StringBuffer("select c.ZYPL as ZYPL,d.ZJJE as ZJJE from (");		
//		hql3.append(hql1.toString());
//		hql3.append(") d,");
//		hql3.append(BSPHISEntryNames.GY_SFXM);
//		hql3.append(" c where c.SFXM=d.ZYGB");
		
		StringBuffer hql2 = new StringBuffer("select sum(a.JKJE) as JKJE from ");
		hql2.append("ZY_TBKK");
		hql2.append(" a");
		hql2.append(" where a.ZYH = :ZYH and a.JSCS=:JSCS and a.ZFPB=0");
		
		//医保住院结算sql语句
		StringBuffer hql4 = new StringBuffer("select a.JYLSH as JYLSH,a.ZXLSH as ZXLSH,a.ZYH as ZYH,a.FPHM as FPHM,a.JSRQ as JSRQ,a.CZGH as CZGH,a.DWBH as DWBH,a.YLLB as YLLB," +
				"a.RYLB as RYLB,a.YLFZE as YLFZE,a.GRZFJE as GRZFJE,a.YLYPZL as YLYPZL,a.TJZL as TJZL,a.TZZL as TZZL,a.QFZFJE as QFZFJE," +
				"a.QFZHZF as QFZHZF,a.QFXJZF as QFXJZF,a.FDZL as FDZL,a.YSZHZF as YSZHZF,a.YSXJZF as YSXJZF,a.FDZF1 as FDZF1,a.FDZF2 as FDZF2," +
				"a.FDZF3 as FDZF3,a.FDZF4 as FDZF4,a.FDZF5 as FDZF5,a.CDGRZF as CDGRZF,a.BNZHZF as BNZHZF,a.LNZHZF as LNZHZF,a.TCZF as TCZF," +
				"a.GRXJZF as GRXJZF,a.JZJZF as JZJZF,a.GWJJZF as GWJJZF,a.ZFPB as ZFPB,a.ZFRQ as ZFRQ,a.CARDNO as CARDNO,a.BRXZ as BRXZ,a.RYXZ as RYXZ," +
				"a.LJFY as LJFY,a.YYDM as YYDM,a.YYLSH as YYLSH,a.YWZQH as YWZQH,a.KLZJE as KLZJE,a.ZFYWZQH as ZFYWZQH,a.LXJJ as LXJJ," +
				"a.ZNTCJJ as ZNTCJJ,a.LFJJ as LFJJ,a.LXJSJJ as LXJSJJ,a.KNJZJJ as KNJZJJ,a.FYDM as FYDM,a.DWLX as DWLX,a.DYLB as DYLB,a.LNJMJJ as LNJMJJ," +
				"a.SNETJJ as SNETJJ,a.NMGJJ as NMGJJ,a.JCTS as JCTS,a.BBLB as BBLB,a.GFKZJJ as GFKZJJ,a.GFJFJJ as GFJFJJ,a.LFKZJJ as LFKZJJ," +
				"a.LFJFJJ as LJJFJJ,a.FSZJJ as FSZJJ,a.FSJJJ as FSJJJ,a.FTJJJ as FTJJJ,a.FJJJJ as FJJJJ,a.FCJJJ as FCJJJ,a.BJDJ as BJDJ,a.CQZNJJ as CQZNJJ," +
				"a.CQGFJJ as CQGFJJ,a.CQLFJJ as CQLFJJ,a.CQH as CQH,a.ZFLSH as ZFLSH,a.XNHJJ as XNHJJ,a.DXSJJ as DXSJJ,a.EJBJZF as EJBJZF,a.ZYCS as ZYCS," +
				"a.LMJJ as LMJJ,a.LNZHZL as LNZHZL,a.CJDD as CJDD,a.SCZF as SCZF,a.SCZL as SCZL,a.YYCD as YYCD,a.DBBZ as DBBZ,a.QBNZHYE as QBNZHYE," +
				"a.QLNZHYE as QLNZHYE,a.QZHYE as QZHYE,a.HBNZHYE as HBNZHYE,a.HLNZHYE as HLNZHYE,a.HZHYE as HZHYE,a.JGID as JGID,a.JSCS as JSCS" +
				" from ");
//		hql4.append(BSPHISEntryNames.YB_ZYJS + " a");
		hql4.append(" where a.ZYH=:ZYH and a.FPHM=:FPHM and a.JGID=:JGID");
		
		try {
			if (dao.doQuery(hql.toString(), parameters) != null
					&& dao.doQuery(hql.toString(), parameters).size() > 0) {
				JSXX = dao.doQuery(hql.toString(), parameters).get(0);
				parameters1.put("ZYH", JSXX.get("ZYH"));
				parameters1.put("JSCS", JSXX.get("JSCS"));				
				SFXMS = dao.doSqlQuery(hql1.toString(), parameters1);
				JKJES = dao.doQuery(hql2.toString(), parameters1).get(0);
				for (int i = 0; i < SFXMS.size(); i++) {
					Map<String, Object> SFXM = SFXMS.get(i);
					if ("1".equals(SFXM.get("ZYPL") + "")) {						
						response.put("XYF", "西药费 "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));						
					} else if ("2".equals(SFXM.get("ZYPL") + "")) {
						response.put("ZCY", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("3".equals(SFXM.get("ZYPL") + "")) {
						response.put("ZCAOY", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("4".equals(SFXM.get("ZYPL") + "")) {
						response.put("CWF", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("5".equals(SFXM.get("ZYPL") + "")) {
						response.put("ZCF", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("6".equals(SFXM.get("ZYPL") + "")) {
						response.put("JCF", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("7".equals(SFXM.get("ZYPL") + "")) {
						response.put("JYF", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("8".equals(SFXM.get("ZYPL") + "")) {
						response.put("ZLF", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("9".equals(SFXM.get("ZYPL") + "")) {
						response.put("SSF", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("10".equals(SFXM.get("ZYPL") + "")) {
						response.put("SXF", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("11".equals(SFXM.get("ZYPL") + "")) {
						response.put("HLF", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("12".equals(SFXM.get("ZYPL") + "")) {
						response.put("CLF", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
					} else if ("13".equals(SFXM.get("ZYPL") + "")) {
						response.put("QT", String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
						
					} 
				}
				if("1".equals(request.get("flag")))
				{
					response.put("BD","补打");///add zhangch 
				}
				double jkje = 0.00;
				if (JKJES.get("JKJE") != null) {
					jkje = Double.parseDouble(JKJES.get("JKJE") + "");
				}
				response.put("ZYYJJ", String.format("%1$.2f", jkje));
				//年月日
				response.put("N", JSXX.get("YYYY") + "");
				response.put("Y", JSXX.get("MM") + "");
				response.put("R", JSXX.get("DD") + "");
				response.put("ZYHM", JSXX.get("XLH") + "");//住院号码
				response.put("BRXM", JSXX.get("XM") + "");//姓名
				response.put("RYLB", JSXX.get("RYLB") + "");//人员类别
				response.put("GZDW", JSXX.get("GZDW"));//工作单位
				response.put("SYY", JSXX.get("SYY") + "");//收银员
				if (JSXX.get("BRCH") != null) {
					response.put("CWH", JSXX.get("BRCH") + "");
				}
				if (JSXX.get("RYMM") != null) {
					response.put("FM", JSXX.get("RYMM") + "");
				}
				if (JSXX.get("RYDD") != null) {
					response.put("FD", JSXX.get("RYDD") + "");
				}
				if (JSXX.get("CYMM") != null) {
					response.put("TM", JSXX.get("CYMM") + "");
				}
				if (JSXX.get("CYDD") != null) {
					response.put("TD", JSXX.get("CYDD") + "");
				}
				int days = 0;
				if (JSXX.get("RYRQ") != null && JSXX.get("CYRQ") != null) {
					days = BSHISUtil.getDifferDays(
							sdftime.parse((JSXX.get("CYRQ") + "").substring(0, 10)+" 00:00:00"),
							sdftime.parse((JSXX.get("RYRQ") + "").substring(0, 10)+" 00:00:00"));
				}
				response.put("DAYS", days + "");
				
				String zYRQ = "";
				if(JSXX.get("CYRQ1")!=null){
					zYRQ = JSXX.get("RYRQ1")+"-"+JSXX.get("CYRQ1")+"("+days+"天)";
				} else {
					if(JSXX.get("RYRQ") != null){
						days = BSHISUtil.getDifferDays(
								sdftime.parse((sdftime.format(new Date())).substring(0, 10)+" 00:00:00"),
								sdftime.parse((JSXX.get("RYRQ") + "").substring(0, 10)+" 00:00:00"));
					}
					
					zYRQ = JSXX.get("RYRQ1")+"-"+"至今("+days+"天)";
				}				
				response.put("ZYRQ", zYRQ + "");//住院日期
				
				double hjje = 0.00;
				if (JSXX.get("HJJE") != null) {
					hjje = Double.parseDouble(JSXX.get("HJJE") + "");
				}
				response.put("FYHJ", String.format("%1$.2f", hjje));//费用合计
				double zfje = parseDouble(JSXX.get("ZFJE"));//自费金额(自理自费)
				response.put("ZFJE", String.format("%1$.2f", zfje));
				response.put("HJDX", numberToRMB(hjje));
				double jkhj = Double.parseDouble(parseDouble(JSXX.get("JKHJ")) + "");//预缴款
				response.put("JKHJ", String.format("%1$.2f", jkhj));
				
				if(zfje-jkhj>=0){
					response.put("CYBJ", String.format("%1$.2f", (zfje-jkhj)));//补缴
					response.put("BJXJ",String.format("%1$.2f", (zfje-jkhj)));//补缴现金
					response.put("CYTK","0.00");//出院退款
					response.put("TKXJ", "0.00");//退款现金
					
				} else {
					response.put("CYBJ", "0.00");
					response.put("JSMXXJ","0,00");
					response.put("CYTK",String.format("%1$.2f", -(zfje-jkhj)));//出院退款
					StringBuffer hqlTKXJ = new StringBuffer(
							"select -sum(a.FKJE) as TKXJ from ZY_FKXX a,ZY_JSMX b,GY_FKFS c where a.ZYH=b.ZYH and a.JSCS=b.JSCS and a.FKFS=c.FKFS and c.HBWC=0  and a.ZYH = :ZYH and a.JSCS = :JSCS");
					Map<String,Object> parametersTKXJ =new HashMap<String,Object>();
					parametersTKXJ.put("ZYH", JSXX.get("ZYH"));
					parametersTKXJ.put("JSCS", JSXX.get("JSCS"));
					Map<String,Object> TKXJ_map = dao.doLoad(hqlTKXJ.toString(), parametersTKXJ);
					response.put("TKXJ",String.format("%1$.2f", TKXJ_map.get("TKXJ")));//退款现金
				}
				
				
				//医保相关
				String SHIYB = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "SHIYB", "0", "市医保病人性质", ctx);
				String SHENGYB = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "SHENGYB", "0", "省医保病人性质", ctx);
				//此处判断是否为医保用户 JSXX.get("BRXZ")
				if(JSXX.get("BRXZ").toString().equals(SHIYB)){
					parameters1.remove("JSCS");
					parameters1.put("FPHM", fphm);
					parameters1.put("JGID", JGID);
					List<Map<String,Object>> list = dao.doQuery(hql4.toString(), parameters1);
					if(list!=null && list.size()>0){
						YBJES = list.get(0);
					}
					
				} else if(JSXX.get("BRXZ").toString().equals(SHENGYB)){
					
				}
				if(YBJES.size()>0 && YBJES!=null){
					String ybCard = YBJES.get("CARDNO") + "";
					response.put("FPHM",fphm+" 医保卡号："+ybCard);
					double bnzhzf = parseDouble(YBJES.get("BNZHZF"));
					response.put("BNZHZF",String.format("%1$.2f", bnzhzf) );//本年账户
					double lnzhzf = parseDouble(YBJES.get("LNZHZF"));
					response.put("LNZHZF",String.format("%1$.2f", lnzhzf) );//历年账户
					double tczf = parseDouble(YBJES.get("TCZF"));
					double lxjj = parseDouble(YBJES.get("LXJJ"));
					double lfjj = parseDouble(YBJES.get("LFJJ"));
					double zntcjj = parseDouble(YBJES.get("ZNTCJJ"));
					double lxjsjj = parseDouble(YBJES.get("LXJSJJ"));
					double snetjj = parseDouble(YBJES.get("SNETJJ"));
					double lnjmjj = parseDouble(YBJES.get("LNJMJJ"));
					double gwjjzf = parseDouble(YBJES.get("GWJJZF"));
					double nmgjj = parseDouble(YBJES.get("NMGJJ"));
					double gfkzjj = parseDouble(YBJES.get("GFKZJJ"));
					double gfjfjj = parseDouble(YBJES.get("GFJFJJ"));
					double lfkzjj = parseDouble(YBJES.get("LFKZJJ"));
					double lfjfjj = parseDouble(YBJES.get("LFJFJJ"));
					double cqznjj = parseDouble(YBJES.get("CQZNJJ"));
					double cqgfjj = parseDouble(YBJES.get("CQGFJJ"));
					double cqlfjj = parseDouble(YBJES.get("CQLFJJ"));
					double xnhjj = parseDouble(YBJES.get("XNHJJ"));
					double dxsjj = parseDouble(YBJES.get("DXSJJ"));
					double jzjzf = parseDouble(YBJES.get("JZJZF"));
					double knjzjj = parseDouble(YBJES.get("KNJZJJ"));
					double lmjj = parseDouble(YBJES.get("LMJJ"));
					double grxjzf = parseDouble(YBJES.get("GRXJZF"));
					double ylypzl = parseDouble(YBJES.get("YLYPZL"));
					double grzfje  = parseDouble(YBJES.get("GRZFJE"));
					double ybzh = tczf + lxjsjj + lfjj + zntcjj + snetjj + lnjmjj + gwjjzf + nmgjj + gfkzjj
							+ gfjfjj + lfkzjj + lfjfjj + cqznjj + cqgfjj + cqlfjj + xnhjj + dxsjj + jzjzf + knjzjj + lmjj; //医保账户
					response.put("YBZH",String.format("%1$.2f", ybzh));
					double ybhj = zfje + ybzh + bnzhzf + lnzhzf;
					response.put("YBHJ",String.format("%1$.2f", ybhj));
					
					//此处判断为市医保还是省医保 JSXX.get("BRXZ")
					
					if(JSXX.get("BRXZ").toString().equals(SHIYB)){//市医保
						StringBuffer bz = new StringBuffer("");
						bz.append("       本年账户支付:" + String.format("%1$.2f", bnzhzf));
						bz.append(" 本年账户支付:" + String.format("%1$.2f", lnzhzf));
						double lnzhzl = parseDouble(YBJES.get("LNZHZL"));
						bz.append("（其中自理:" + String.format("%1$.2f", lnzhzl));
						bz.append(" 自负:" + String.format("%1$.2f", (lnzhzf-lnzhzl)) + "）");						
						double tcjj = tczf + lxjsjj + lfjj + zntcjj + snetjj + lnjmjj + gwjjzf + nmgjj + gfjfjj + lfkzjj + cqznjj + cqgfjj + cqlfjj + xnhjj + dxsjj + lmjj;
						bz.append("统筹基金：" + String.format("%1$.2f", tcjj));
						bz.append(" 重病补助：" + String.format("%1$.2f", jzjzf));
						bz.append(" 困难补助基金支付:" + String.format("%1$.2f", knjzjj));
						double sczl = parseDouble(YBJES.get("SCZL"));
						double sczf = parseDouble(YBJES.get("SCZF"));
						if(sczl>0 || sczf>0){
							bz.append(" 其中伤残基金支付自负：" + String.format("%1$.2f", sczf));
							bz.append(" 其中伤残基金支付自理：" + String.format("%1$.2f", sczl));
						}
						//double grxjzf = parseDouble(YBJES.get("GRXJZF"));
						bz.append("现金支付：" + String.format("%1$.2f", grxjzf));
						
						double tjzl = parseDouble(YBJES.get("TJZL"));
						double tzzl = parseDouble(YBJES.get("TZZL"));
						
						double zfzl = ylypzl + tjzl + tzzl + grzfje - lnzhzl -sczl;
						bz.append("（自费自理：" + String.format("%1$.2f", zfzl));
						double ld_temp = grxjzf - (ylypzl + tjzl + tzzl - lnzhzl) -  grzfje;
						if(ld_temp>0) {
							bz.append("自负：" + ld_temp + "）");
						} else {
							bz.append("）");
						}
						response.put("BZ",bz.toString());//市医保备注						
					} else {//省医保备注
						StringBuffer bz = new StringBuffer("");
						bz.append("       本年账户支付:" + String.format("%1$.2f", bnzhzf));
						bz.append(" 历年账户支付:" + String.format("%1$.2f", lnzhzf));
						bz.append(" 统筹基金:" + String.format("%1$.2f", tczf));
						bz.append(" 公务基金支付:" + String.format("%1$.2f", gwjjzf));
						bz.append(" 重病补助:" + String.format("%1$.2f", jzjzf));
						bz.append(" 现金支付:" + String.format("%1$.2f", grxjzf));
						bz.append("(自理自费:"+String.format("%1$.2f", ylypzl+grzfje));
						double ld_temp = grxjzf - ylypzl - grzfje;
						if(ld_temp>0){
							bz.append("自负：" + String.format("%1$.2f", ld_temp) + ")");
						} else {
							bz.append(")");
						}
						response.put("BZ", bz.toString());
					}					
				} else {
					response.put("FPHM",fphm);//医疗卡号					
					response.put("BNZHZF","0.00");
					response.put("LNZHZF","0.00");
					response.put("YBZH", "0.00");
					response.put("YBHJ", String.format("%1$.2f", zfje)); //医保合计
					response.put("BZ", "");
				}
				
				
				
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
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
		return strRMB;
	}

	/**
	 * 空转换double
	 */
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0.00);
		}
		return Double.parseDouble(o + "");
	}
}
