package phis.prints.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class InvoiceFile implements IHandler {

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		String fphm = request.get("fphm") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> MZXX = new HashMap<String, Object>();
		List<Map<String, Object>> SFXMS = new ArrayList<Map<String, Object>>();
		StringBuffer hql = new StringBuffer(
				"select nvl(a.HZJE,0) as HZJE,nvl(a.JJZFJE,0) as JJZFJE,nvl(a.MZJZJE,0) as MZJZJE,a.XJJE as XJJE,a.BRXZ as BRXZ,d.XZMC as JSFS,c.PERSONID as SFY,a.MZXH as MZXH,b.MZHM as XLH,b.BRXM as XM,a.QTYS as JZ,a.ZJJE as HJJE,a.ZFJE as ZFJE,to_char(a.SFRQ,'yyyy') as YYYY,to_char(a.SFRQ,'mm') as MM,to_char(a.SFRQ,'dd') as DD from ");
		hql.append("MS_MZXX a,");
		hql.append("MS_BRDA b,");
		hql.append("SYS_Personnel c,");
		hql.append("GY_BRXZ d where a.BRXZ = d.BRXZ and a.CZGH = c.PERSONID and a.BRID = b.BRID and a.FPHM = :FPHM and a.JGID = :JGID");
		parameters.put("FPHM", fphm);
		parameters.put("JGID", JGID);
		String[] upint = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		StringBuffer hql1 = new StringBuffer(
				"select b.MZGB as MZGB,sum(a.ZJJE) as ZJJE from ");
		hql1.append("MS_SFMX a,");
		hql1.append("GY_SFXM b where a.SFXM = b.SFXM and a.MZXH = :MZXH group by b.MZGB");
		try {
			MZXX = dao.doQuery(hql.toString(), parameters).get(0);
			parameters1.put("MZXH", MZXX.get("MZXH"));
			SFXMS = dao.doQuery(hql1.toString(), parameters1);
			String topUnitId = ParameterUtil.getTopUnitId();
//			String XYF = ParameterUtil.getParameter(topUnitId,
//					BSPHISSystemArgument.XYF, ctx);
//			String ZYF = ParameterUtil.getParameter(topUnitId,
//					BSPHISSystemArgument.ZYF, ctx);
//			String CYF = ParameterUtil.getParameter(topUnitId,
//					BSPHISSystemArgument.CYF, ctx);
			String GHF = ParameterUtil.getParameter(topUnitId,
					BSPHISSystemArgument.GHF, ctx);
			String JCG = ParameterUtil.getParameter(topUnitId,
					BSPHISSystemArgument.JCF, ctx);
			String FP_ZLF = ParameterUtil.getParameter(topUnitId,
					BSPHISSystemArgument.FP_ZLF, ctx);
			String YBZLF = ParameterUtil.getParameter(topUnitId,
					BSPHISSystemArgument.YBZLF, ctx);
			for (int i = 0; i < SFXMS.size(); i++) {
				Map<String, Object> SFXM = SFXMS.get(i);
//				if (XYF.equals(SFXM.get("MZGB") + "")) {
//					response.put("XYJE", SFXM.get("ZJJE") + "");
//				} else if (ZYF.equals(SFXM.get("MZGB") + "")) {
//					response.put("ZYJE", SFXM.get("ZJJE") + "");
//				} else if (CYF.equals(SFXM.get("MZGB") + "")) {
//					response.put("CYJE", SFXM.get("ZJJE") + "");
//				} else 
					if (JCG.equals(SFXM.get("MZGB") + "")) {
					response.put("JCJE", SFXM.get("ZJJE") + "");
				} else if (FP_ZLF.equals(SFXM.get("MZGB") + "")) {
					response.put("ZLJE", SFXM.get("ZJJE") + "");
				} else if (GHF.equals(SFXM.get("MZGB") + "")) {
					response.put("GHJE", SFXM.get("ZJJE") + "");
				} else if (YBZLF.equals(SFXM.get("MZGB") + "")) {
					response.put("YBZLF", SFXM.get("ZJJE") + "");
				} else {
					if (response.containsKey("QTJE")) {
						response.put(
								"QTJE",
								(Double.parseDouble(response.get("QTJE") + "") + Double
										.parseDouble(SFXM.get("ZJJE") + ""))
										+ "");
					} else {
						response.put("QTJE", SFXM.get("ZJJE") + "");
					}
				}
			}
			response.put("FPHM", fphm);
			response.put("XLH", MZXX.get("XLH") + "");
			response.put("YYYY", MZXX.get("YYYY") + "");
			response.put("MM", MZXX.get("MM") + "");
			response.put("DD", MZXX.get("DD") + "");
			response.put("XM", MZXX.get("XM") + "");
			response.put("HJJE", MZXX.get("HJJE") + "");
			response.put("GRJF", MZXX.get("XJJE") + "");
			response.put("JZ", MZXX.get("JZ") + "");
			response.put("JGMC", jgname);
			response.put("SFY", MZXX.get("SFY") + "");
			response.put("JSFS", MZXX.get("JSFS") + "");
			double hjje = Double.parseDouble(MZXX.get("HJJE") + "");
			int sw = (int) (hjje / 100000) % 10;
			int w = (int) (hjje / 10000) % 10;
			int q = (int) (hjje / 1000) % 10;
			int b = (int) (hjje / 100) % 10;
			int s = (int) (hjje / 10) % 10;
			int y = (int) (hjje) % 10;
			int j = (int) (hjje * 10) % 10;
			String fStr = String.format("%.0f", hjje * 100);
			int f = Integer.parseInt(fStr) % 10;
			if (f == 0) {
				String jStr = String.format("%.0f", hjje * 10);
				j = Integer.parseInt(jStr) % 10;
			}
			response.put("SW", upint[sw]);
			response.put("W", upint[w]);
			response.put("Q", upint[q]);
			response.put("B", upint[b]);
			response.put("S", upint[s]);
			response.put("Y", upint[y]);
			response.put("J", upint[j]);
			response.put("F", upint[f]);
			if ("6103".equals(MZXX.get("BRXZ") + "")) {
				response.put("BZ", "'小病'免费:3.00");
				response.put(
						"GRZF",
						"个人缴费:"
								+ String.format(
										"%1$.2f",
										(Double.parseDouble(MZXX.get("HJJE")
												+ "") - 3.00)));
			}
			if ("6104".equals(MZXX.get("BRXZ") + "")) {
				response.put("GRZF", "个人缴费:3.00");
				response.put(
						"BZ",
						"股民减免:"
								+ String.format(
										"%1$.2f",
										(Double.parseDouble(MZXX.get("HJJE")
												+ "") - 3.00)));
			}
			if ("6089".equals(MZXX.get("BRXZ") + "")) {
				response.put("BZ", "民政救助金额:" + MZXX.get("MZJZJE") + "");
			}
			// if (Double.parseDouble(MZXX.get("HJJE") + "") > Double
			// .parseDouble(MZXX.get("ZFJE") + "")) {
			// String bz = "核准金额:￥"+MZXX.get("HZJE")+"\n报销金额:￥" +
			// MZXX.get("JZ")+"\n自费金额:￥"+MZXX.get("XJJE");
			// response.put("BZ",bz);
			// }
			// response=MZXX;
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
