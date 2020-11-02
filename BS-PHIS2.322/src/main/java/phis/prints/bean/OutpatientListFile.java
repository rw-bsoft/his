package phis.prints.bean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class OutpatientListFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		/**
		 * 2013-08-26 modify by gejj对该方法中的三条sql查询条件添加JGID,因为在一个发票号码在多个机构中存在时会有问题
		 **/
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String fphm = request.get("fphm") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FPHM", fphm);
		parameters.put("JGID", JGID);
		try {
			List<Map<String, Object>> yj_List = dao
					.doQuery(
							"select d.SFMC as FYDL,c.FYMC as FYMC,c.FYDW as DW,a.YLDJ as DJ,a.YLSL as SL,a.HJJE as JE from "
									+ "MS_YJ02 a,"
									+ "MS_YJ01 b,"
									+ "GY_YLSF c,"
									+ "GY_SFXM d where a.YJXH = b.YJXH and a.YLXH=c.FYXH and a.FYGB = d.SFXM and b.FPHM=:FPHM and b.JGID=:JGID",
							parameters);
			List<Map<String, Object>> cf_List = dao
					.doQuery(
							/**
							 * 2013-08-28 modify by gejj 修改2321bug 将e.YPDM
							 * 使用MS_CF02表中的药房单位(YFDW)
							 **/
							// "select d.SFMC as FYDL,e.YPMC as FYMC,e.YPDW as DW,a.YPDJ as DJ,a.YPSL as SL,a.HJJE as JE from "
							"select d.SFMC as FYDL,e.YPMC as FYMC,a.YFDW as DW,a.YPDJ as DJ,a.YPSL as SL,a.HJJE as JE,a.ZFYP as ZFYP from "
									+ "MS_CF02 a,"
									+ "MS_CF01 b,"
									+ "GY_SFXM d,"
									+ "YK_TYPK e where a.CFSB = b.CFSB and a.FYGB = d.SFXM and a.YPXH=e.YPXH and b.FPHM=:FPHM and b.JGID=:JGID",
							parameters);
			for(Map<String,Object> m:cf_List){
				if(MedicineUtils.parseInt(m.get("ZFYP"))==1){
					m.put("FYMC", "(自备)"+m.get("FYMC"));
				}
			}
			records.addAll(yj_List);
			Map<String, Object> MS_MZXX = dao.doLoad(
					"select a.BRXM as XM,a.FPHM as FPH,a.SFRQ as SFRQ,a.ZJJE as JEHJXX from "
							+ "MS_MZXX a where a.FPHM=:FPHM and a.JGID=:JGID",
					parameters);
			String RMB = numberToRMB(Double.parseDouble(MS_MZXX.get("JEHJXX")
					+ ""));
			MS_MZXX.put("JEHJDX", RMB);
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
			MS_MZXX.put("SFRQ", matter.format((Date) MS_MZXX.get("SFRQ")));
			// response.putAll(MS_MZXX);
			MS_MZXX.put("FYDL", "合计金额:");
			MS_MZXX.put("FYMC", RMB);
			MS_MZXX.put("DW", "小写:");
			MS_MZXX.put("JE", MS_MZXX.get("JEHJXX"));
			cf_List.add(MS_MZXX);
			records.addAll(cf_List);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String TITLE = user.getManageUnit().getName();// 用户的机构ID
		/**
		 * 2013-08-26 modify by gejj对该方法中的一条sql查询条件添加JGID,因为在一个发票号码在多个机构中存在时会有问题
		 **/
		response.put("title", TITLE + "门诊费用清单");
		String fphm = request.get("fphm") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FPHM", fphm);
		parameters.put("JGID", JGID);
		try {
			Map<String, Object> MS_MZXX = dao
					.doLoad("select a.BRXM as XM,a.FPHM as FPH,a.SFRQ as SFRQ,a.ZJJE as JEHJXX from "
							+ "MS_MZXX a where a.FPHM=:FPHM and a.JGID = :JGID",
							parameters);
			String RMB = numberToRMB(Double.parseDouble(MS_MZXX.get("JEHJXX")
					+ ""));
			MS_MZXX.put("JEHJDX", RMB);
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
			MS_MZXX.put("SFRQ", matter.format((Date) MS_MZXX.get("SFRQ")));
			response.putAll(MS_MZXX);
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
			strRMB = s1 + s2.charAt(0) + "角" + s2.charAt(1) + "分";
		} else {
			strRMB = strRMB + "圆整";
		}
		if (rmb < 1) {
			strRMB = "零" + strRMB;
		}
		return strRMB;
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public int parseInt(Object o) {
		if (o == null) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}
}
