package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class RegistrationFormFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sfm = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		SimpleDateFormat sfampm = new SimpleDateFormat("a");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();// 用户的机构名称
		response.put("title", jgname + "挂号");
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String sbxh = request.get("sbxh") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", manaUnitId);
		parameters.put("SBXH", Long.parseLong(sbxh));
		String sql = "select brda.BRXZ as BRXZ,mzlb.MZMC as MZMC,ghks.KSMC as KSMC,ghmx.YSDM as YSDM,ghmx.CZGH as CZGH,"
				+ "brda.MZHM as MZHM, ghmx.JZHM as JZHM, brda.BRXM as BRXM, brda.BRXB as BRXB,"
				+ "ghmx.GHJE as GHJE,ghmx.ZLJE as ZLJE,ghmx.JZXH as JZXH,ghmx.GHSJ as GHSJ,"
				+ "ghks.GHXE as GHXE,ghks.YGRS as YGRS,ghks.YYRS as YYRS,ghmx.BLJE as BLJE "// 挂号限额,已挂人数,预约人数
				+ "from MS_BRDA brda,MS_GHKS ghks,MS_GHMX ghmx,MS_MZLB mzlb where mzlb.MZLB=ghmx.MZLB and ghmx.KSDM=ghks.KSDM  and ghmx.BRID=brda.BRID and ghmx.JGID=:JGID  and ghmx.SBXH=:SBXH";
		try {
			Map<String, Object> res = dao.doLoad(sql, parameters);
			if (res != null) {
				String brxb = DictionaryController.instance().getDic("phis.dictionary.gender")
						.getText(res.get("BRXB") + "");
				String ysxm = DictionaryController.instance().getDic("phis.dictionary.doctor")
						.getText(res.get("CZGH") + "");
				response.put("mzhm", res.get("MZHM"));
				response.put("jzhm", res.get("JZHM"));
				response.put("xm", res.get("BRXM"));
				response.put("xb", brxb);
				response.put("ghf", res.get("GHJE") + "");
				response.put("zlf", res.get("ZLJE") + "");
				response.put("mzmc", res.get("MZMC"));
				response.put("rq", sfm.format(res.get("GHSJ")));
				// 上午/下午
				String ampm = sfampm.format(res.get("GHSJ"));
				// System.out.print(ampm);
				if ("上午".equals(ampm) || "下午".equals(ampm)) {
					response.put("ampm", ampm);
				} else {
					response.put("ampm", "");
				}
				response.put("ghks", res.get("KSMC"));
				response.put("ysxm", ysxm);
				response.put("ghxe", res.get("GHXE"));
				// 已挂人数,预约人数
				response.put("ygrs", res.get("YGRS"));
				response.put("yyrs", res.get("YYRS"));
				// 门诊序号
				response.put("mzxh", res.get("JZXH"));
				//病历本费
				response.put("BLJE", "病历费："+String.format("%1$.2f", parseDouble(res.get("BLJE"))));

				// 医保信息
				// 本年账户支付：yb_ghjs.BNZHZF
				// 历年账户支付：yb_ghjs.LNZHZF
				// 统筹基金：yb_ghjs.TCZF + yb_ghjs.lxjj + yb_ghjs.lfjj +
				// yb_ghjs.zntcjj + yb_ghjs.LXJSJJ + yb_ghjs.snetjj +
				// yb_ghjs.lnjmjj + GWJJZF + yb_ghjs.zztcjj + TXTCJJ +
				// yb_ghjs.nmgjj + yb_ghjs.gfkzjj + yb_ghjs.gfjfjj +
				// yb_ghjs.lfkzjj + yb_ghjs.lfjfjj + yb_ghjs.cqznjj +
				// yb_ghjs.cqgfjj + yb_ghjs.cqlfjj + yb_ghjs.xnhjj +
				// yb_ghjs.dxsjj
				// 医疗救助基金支付：yb_ghjs.KNJZJJ
				// 现金支付：yb_ghjs.GRXJZF
				// 注：表关联ms_ghmx.sbxh = yb_ghjs.sbxh
				
				//医保相关
				String brxz = res.get("BRXZ")+"";
				String SHIYB = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "SHIYB", "0", "市医保病人性质", ctx);
				String SHENGYB = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "SHENGYB", "0", "省医保病人性质", ctx);
				String YHYB = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "YHYB", "0", "余杭医保病人性质", ctx);
				Map<String, Object> parametersYB = new HashMap<String, Object>();
				parametersYB.put("SBXH", Long.parseLong(sbxh));
				if(brxz.equals(SHIYB)){
					String sqlYBXX = "select BNZHZF as BNZHZF,LNZHZF as LNZHZF,"
							+ " (TCZF+LXJJ+LFJJ+ZNTCJJ+LXJSJJ+SNETJJ+LNJMJJ+GWJJZF+ZZTCJJ+TXTCJJ+NMGJJ+GFKZJJ+GFJFJJ"
							+ " +LFKZJJ+LFJFJJ+CQZNJJ+CQGFJJ+CQLFJJ+XNHJJ+DXSJJ) as TCJJ,"
							+ " KNJZJJ as KNJZJJ,GRXJZF as GRXJZF from YB_GHJS where SBXH=:SBXH";
					Map<String, Object> map_YB = dao.doLoad(sqlYBXX, parametersYB);
					if (map_YB == null || map_YB.size() == 0) {
						return;
					}
					if (map_YB.containsKey("BNZHZF")
							&& map_YB.get("BNZHZF") != null) {
						response.put("BNZHZF", "本年账户支付:" + map_YB.get("BNZHZF")
								+ "");
					}
					if (map_YB.containsKey("LNZHZF")
							&& map_YB.get("LNZHZF") != null) {
						response.put("LNZHZF", "历年账户支付:" + map_YB.get("LNZHZF")
								+ "");
					}
					if (map_YB.containsKey("TCJJ") && map_YB.get("TCJJ") != null) {
						response.put("TCJJ", "统筹基金:" + map_YB.get("TCJJ") + "");
					}
					if (map_YB.containsKey("KNJZJJ")
							&& map_YB.get("KNJZJJ") != null) {
						response.put("KNJZJJ", "医疗救助基金支付:" + map_YB.get("KNJZJJ")
								+ "");
					}
					if (map_YB.containsKey("GRXJZF")
							&& map_YB.get("GRXJZF") != null) {
						response.put("GRXJZF", "现金支付:" + map_YB.get("GRXJZF") + "");
					}
				} else if (brxz.equals(SHENGYB)){
					String sqlSYB = "select BNZH as BNZH,WNZH as WNZH,TCJJ as TCJJ,DBJZ as DBJZ,GRXJ as GRXJ" +
							" from SJYB_GHJS where SBXH=:SBXH";
					Map<String,Object> map_SYB = dao.doLoad(sqlSYB, parametersYB);
					if (map_SYB == null || map_SYB.size() == 0) {
						return;
					}
					if (map_SYB.containsKey("BNZH")
							&& map_SYB.get("BNZH") != null) {
						response.put("BNZHZF", "本年账户支付:" + map_SYB.get("BNZH")
								+ "");
					}
					if (map_SYB.containsKey("WNZH")
							&& map_SYB.get("WNZH") != null) {
						response.put("LNZHZF", "历年账户支付:" + map_SYB.get("WNZH")
								+ "");
					}
					if (map_SYB.containsKey("TCJJ") && map_SYB.get("TCJJ") != null) {
						response.put("TCJJ", "统筹基金:" + map_SYB.get("TCJJ") + "");
					}
					if (map_SYB.containsKey("DBJZ")
							&& map_SYB.get("DBJZ") != null) {
						response.put("KNJZJJ", "医疗救助基金支付:" + map_SYB.get("DBJZ")
								+ "");
					}
					if (map_SYB.containsKey("GRXJ")
							&& map_SYB.get("GRXJ") != null) {
						response.put("GRXJZF", "现金支付:" + map_SYB.get("GRXJ") + "");
					}
				}else if(brxz.equals(YHYB)){
					String sqlYHYB = "select GRDNZH as GRDNZH,GRLNZH as GRLNZH," +
							"(TCJJ+DBJZJJ+LXJJ+BLJJ+GWYBZJJ+MZFYDXJZ+SHYLJZJJ+JMYBTC) as TCJJ,GRXJZF as GRXJZF from YHYB_GHJS where SBXH=:SBXH";
					Map<String,Object> map_YHYB = dao.doLoad(sqlYHYB, parametersYB);
					if(map_YHYB == null || map_YHYB.size() == 0 ){
						response.put("BNZHZF", "本年账户支付:0.0" );
						response.put("LNZHZF", "历年账户支付:0.0" );
						response.put("TCJJ", "统筹基金:0.0");
						//response.put("KNJZJJ", "医疗救助基金支付:0.0");
						response.put("GRXJZF", "现金支付:0.0");
					}else{
						if (map_YHYB.containsKey("GRDNZH")
								&& map_YHYB.get("GRDNZH") != null) {
							response.put("BNZHZF", "本年账户支付:" + map_YHYB.get("GRDNZH")
									+ "");
						}
						if (map_YHYB.containsKey("GRLNZH")
								&& map_YHYB.get("GRLNZH") != null) {
							response.put("LNZHZF", "历年账户支付:" + map_YHYB.get("GRLNZH")
									+ "");
						}
						if (map_YHYB.containsKey("TCJJ") && map_YHYB.get("TCJJ") != null) {
							response.put("TCJJ", "统筹基金:" + map_YHYB.get("TCJJ") + "");
						}
//						if (map_YHYB.containsKey("DBJZJJ")
//								&& map_YHYB.get("DBJZJJ") != null) {
//							response.put("KNJZJJ", "医疗救助基金支付:" + map_YHYB.get("DBJZJJ")
//									+ "");
//						}
						if (map_YHYB.containsKey("GRXJZF")
								&& map_YHYB.get("GRXJZF") != null) {
							response.put("GRXJZF", "现金支付:" + map_YHYB.get("GRXJZF") + "");
						}
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
}
