package phis.prints.bean;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class SecurityClinicFile implements IHandler {

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		try {
		BaseDAO dao = new BaseDAO(ctx);
		String fphm = request.get("fphm") + "";
		StringBuffer hql_sfmx = new StringBuffer();
		hql_sfmx.append(
				" select YPMC as YPMC,YPDJ as YPDJ,YPSL as YPSL,YPJE as YPJE ,FYGB as FYGB from (select d.YPMC as YPMC,c.YPDJ as YPDJ,c.YPSL as YPSL,c.HJJE as YPJE ,c.FYGB as FYGB   from ")
				.append("MS_MZXX a,")
				.append("MS_CF01 b,")
				.append("MS_CF02 c,")
				.append("YK_TYPK d where  a.MZXH=b.MZXH and b.CFSB=c.CFSB and c.YPXH=d.YPXH and a.FPHM=:fphm ").append("union all ").append("select d.FYMC as YPMC,c.YLDJ as YPDJ,c.YLSL as YPSL,c.HJJE as YPJE ,c.FYGB as FYGB  from MS_MZXX a,")
				.append("MS_YJ01 b,")
				.append("MS_YJ02 c,").append("GY_YLSF d where a.MZXH=b.MZXH and b.YJXH=c.YJXH and c.YLXH=d.FYXH and a.FPHM=:fphm").append(") order by FYGB") ;
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("fphm", fphm);
		List<Map<String,Object>> list_sfmx=dao.doSqlQuery(hql_sfmx.toString(), map_par);
		StringBuffer hql_sfgb=new StringBuffer();
		hql_sfgb.append("select sum(a.ZJJE) as ZYHM,a.SFXM as SFXM,b.SFMC as XMMC from MS_SFMX a, GY_SFXM b where a.SFXM=b.SFXM and a.FPHM=:fphm group by b.SFMC,a.SFXM");
		List<Map<String,Object>> list_xmgb=dao.doSqlQuery(hql_sfgb.toString(), map_par);
		for(Map<String,Object> map_xmgb:list_xmgb){
			int sfxm=Integer.parseInt(map_xmgb.get("SFXM")+"");
			for(Map<String,Object> map_sfmx:list_sfmx){
				int fygb=Integer.parseInt(map_sfmx.get("FYGB")+"");
				if(sfxm==fygb){
					map_sfmx.put("ZYHM", map_xmgb.get("ZYHM"));
					map_sfmx.put("XMMC", map_xmgb.get("XMMC"));
					break;
				}
			}
		}
		for(Map<String,Object> map_sfmx:list_sfmx){
			records.add(map_sfmx);
		}
		} catch (PersistentDataOperationException e) {
			throw new PrintException(ServiceCode.CODE_DATABASE_ERROR, "发票打印失败");
		}
		

	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
//		BaseDAO dao = new BaseDAO(ctx);
//		String fphm = request.get("fphm") + "";
//		User user = (User) ctx.get("user.instance");
//		String jgname = user.get("manageUnit.name");// 医院名称
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		String userName = user.getName();// 操作员姓名
//		//String sjh = "0";// 收据号,不知道怎么取 暂时先默认
//		//JXMedicareModel jxm = new JXMedicareModel(dao);
//		try {
//			Map<String, Object> map_par = new HashMap<String, Object>();
//			map_par.put("fphm", fphm);
//			StringBuffer  hql=new StringBuffer();
//			//这个是根据存在数据库里的社保卡信息去查询卡信息,优点 是能在任何电脑打印.缺点是 结算完后再读卡就会导致数据不准 而且只能打印最后一次结算发票
////			hql.append("select a.XM as XM,a.GRBH as GRBH,a.SBKH as SBKH,a.DWMC as DWMC,(a.ZHYE-c.BCZHZF) as ZHYE from ").append(BSPHISEntryNames.YB_YBKXX).append(" a,").append(BSPHISEntryNames.MS_MZXX).append(" b,").append(BSPHISEntryNames.YB_JSXX).append(" c where a.SBKH=c.SBKH and b.YWLSH=c.YWLSH and b.FPHM=:fphm");
////			//Map<String, Object> brxx = jxm.querySocialSecurityCard(ctx);
////			//Map<String, Object> brxx =new HashMap<String,Object>();
////			Map<String, Object> brxx=dao.doLoad(hql.toString(), map_par);
////			response.put("SJH", fphm);// 收据号
////			response.put("YYMC", jgname);// 医院名称
////			response.put("XM", brxx.get("XM") + "");// 姓名
////			response.put("YBBM", brxx.get("GRBH") + "");// 医保编号
////			response.put("ICKH", brxx.get("SBKH") + "");// IC卡号
////			response.put("DWMC", brxx.get("DWMC") + "");// 单位名称
////			response.put("DQGRZHYE", brxx.get("ZHYE") + "");// 账户余额
////			hql.append("select a.SFZH as SFZH,a.XM as XM from ").append(BSPHISEntryNames.YB_YBKXX).append(" a,").append(BSPHISEntryNames.MS_MZXX).append(" b,").append(BSPHISEntryNames.YB_JSXX).append(" c where a.SBKH=c.SBKH and b.YWLSH=c.YWLSH and b.FPHM=:fphm");
////			Map<String, Object> brxx=dao.doLoad(hql.toString(), map_par);
////			JXMedicareModel jxm = new JXMedicareModel(dao);
////			Map<String,Object> map_sbkxx=jxm.querySocialSecurityCardBySFZH(brxx,ctx);
////			response.put("SJH", fphm);// 收据号
////			response.put("YYMC", jgname);// 医院名称
////			response.put("XM", map_sbkxx.get("XM") + "");// 姓名
////			response.put("YBBM", map_sbkxx.get("GRBH") + "");// 医保编号
////			response.put("ICKH", map_sbkxx.get("SBKH") + "");// IC卡号
////			response.put("DWMC", map_sbkxx.get("DWMC") + "");// 单位名称
////			response.put("DQGRZHYE", map_sbkxx.get("ZHYE") + "");// 账户余额
//			hql.append("select a.XM as XM,a.GRBH as GRBH,a.SBKH as SBKH,a.DWMC as DWMC,c.YBYE as ZHYE from ").append(BSPHISEntryNames.YB_YBKXX).append(" a,").append(BSPHISEntryNames.MS_MZXX).append(" b,").append(BSPHISEntryNames.YB_JSXX).append(" c where a.SBKH=c.SBKH and b.YWLSH=c.YWLSH and b.FPHM=:fphm");
//			Map<String,Object> map_sbkxx = dao.doLoad(hql.toString(), map_par);
//			response.put("SJH", fphm);
//			response.put("YYMC", jgname);
//			response.put("XM", map_sbkxx.get("XM")+"");
//			response.put("YBBM", map_sbkxx.get("GRBH")+"");
//			response.put("ICKH", map_sbkxx.get("SBKH")+"");
//			response.put("DWMC", map_sbkxx.get("DWMC")+"");
//			response.put("DQGRZHYE", map_sbkxx.get("ZHYE")+"");
//			StringBuffer hql_ywlsh = new StringBuffer();// 查询业务流水号
//			hql_ywlsh.append("select YWLSH as YWLSH from ")
//					.append(BSPHISEntryNames.MS_MZXX)
//					.append(" where FPHM=:fphm");
//			
//			Map<String, Object> map_ywlsh = dao.doLoad(hql_ywlsh.toString(),
//					map_par);
//			StringBuffer hql_hjje = new StringBuffer();// 查询合计金额
//			hql_hjje.append("select sum(ZJJE) as ZJJE from ")
//					.append(BSPHISEntryNames.MS_SFMX)
//					.append(" where FPHM=:fphm");
//			Map<String, Object> map_hjje = dao.doLoad(hql_hjje.toString(),
//					map_par);
//			response.put("FYHJDX",
//					numberToRMB(Double.parseDouble(map_hjje.get("ZJJE") + "")));// 账户余额
//			long ywlsh = Long.parseLong(map_ywlsh.get("YWLSH") + "");
//			StringBuffer hql_jsxx = new StringBuffer();// 结算信息
//			hql_jsxx.append(
//					"select TCZC as TCZC,BCZHZF as BCZHZF,DBZC as DBZC,ZXJZFY as ZXJZFY from ")
//					.append(BSPHISEntryNames.YB_JSXX)
//					.append(" where YWLSH=:ywlsh");
//			map_par.clear();
//			map_par.put("ywlsh", ywlsh);
//			Map<String, Object> map_jsxx = dao.doLoad(hql_jsxx.toString(),
//					map_par);
//			response.put("TCZF", map_jsxx.get("TCZC"));// 统筹支出
//			response.put("GRZHZF", map_jsxx.get("BCZHZF"));// 个人账户支付
//			response.put("DBJZ", map_jsxx.get("DBZC"));// 大病救助
//			response.put("ZXJZZF", map_jsxx.get("ZXJZFY"));// 专项救助支付
//			response.put("SFY", userName);// 收费员
//			response.put("SKRQ", sdf.format(new Date()));// 收费员
//			if("1".equals(request.get("flag")))
//			{
//				response.put("BD", "补打");
//			}
//		} 
//		catch (PersistentDataOperationException e) {
//			throw new PrintException(ServiceCode.CODE_DATABASE_ERROR, "发票打印失败");
//		}
////		catch (ModelDataOperationException e) {
////			throw new PrintException(ServiceCode.CODE_DATABASE_ERROR, "发票打印失败:"+e.getMessage());
////		}

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
}
