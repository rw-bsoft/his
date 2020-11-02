package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.pub.source.PublicModel;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class TreatmentPrintFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		int n = 1;
		String yjxhsql = null;
		if (request.get("yjxh") != null) {
			yjxhsql = "select YJXH as YJXH from MS_YJ01 where YJXH in("
					+ request.get("yjxh") + ") and JGID=:JGID";
		}
		if(request.get("jzxh") != null) {
			yjxhsql = "select YJXH as YJXH from MS_YJ01 where JZXH in("
					+ request.get("jzxh") + ") and JGID=:JGID";
		}
		//add by Lizhi 2018-03-07 只查询未收费的项目
		yjxhsql+=" and FPHM is null order by YJXH asc";
		try {
			List<Map<String, Object>> yjxhlist = dao.doQuery(yjxhsql,
					parameters);
			for (int i = 0; i < yjxhlist.size(); i++) {
				Map<String, Object> parametersmx = new HashMap<String, Object>();
				if (yjxhlist.get(i).get("YJXH") != null && yjxhlist.get(i).get("YJXH") != "") {
					parametersmx.put("YJXH",Long.parseLong(yjxhlist.get(i).get("YJXH") + ""));
				} else {
					parametersmx.put("YJXH", 0L);
				}
				String sql = "select cf.YJXH as YJXH,yl.FYMC as FYMC,cf.YLDJ as YLDJ,cf.YLSL as FYSL,yl.FYDW as FYDW,(cf.YLDJ*cf.YLSL) as HJJE from MS_YJ02 cf,GY_YLSF yl where cf.YLXH=yl.FYXH and cf.YJXH=:YJXH order by cf.YJXH";
				List<Map<String, Object>> cflist = dao.doQuery(sql,
						parametersmx);
				for (int j = 0; j < cflist.size(); j++) {
					cflist.get(0).put("XH", n + "、");
					records.add(cflist.get(j));
				}
				n++;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		Long yjxh = 0L;
		Long jzxh = 0L;
		if (request.get("yjxh") != null) {
			yjxh = Long.parseLong(request.get("yjxh") + "");
		}
		if (request.get("jzxh") != null) {
			jzxh = Long.parseLong(request.get("jzxh") + "");
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		String jgid = user.getManageUnit().getId();
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametersjzhm = new HashMap<String, Object>();
		parameters.put("YJXH", yjxh);
		parameters.put("JGID", jgid);
		String sql2 = "select distinct br.BRXM as BRXM,br.BRXB as BRXB,br.CSNY as CSNY,yj.KSDM as KSDM,br.JZKH as JZKH,br.MZHM as MZHM,br.BRXZ as BRXZ,md.address as ADDRESS,md.mobileNumber as MOBILENUMBER,yj.YSDM as YSDM,yj.JZXH as JZXH,yj.KDRQ as KDRQ,yyj.GHXH as SBXH from MS_YJ01 yj left outer join YS_MZ_JZLS yyj on yj.JZXH=yyj.JZXH,MS_BRDA br,MPI_DemographicInfo md where yj.BRID=br.BRID and br.EMPIID=md.empiId and yj.JGID=:JGID and yj.YJXH=:YJXH";
		String sql3 = "select JZHM as JZHM from MS_GHMX where SBXH=:SBXH";
		try {
			List<Map<String, Object>> cfmap = new ArrayList<Map<String, Object>>();
			cfmap = dao.doSqlQuery(sql2, parameters);
			if (cfmap.size() > 0) {
				if (cfmap.get(0).get("SBXH") != null) {
					parametersjzhm.put("SBXH",Long.parseLong(cfmap.get(0).get("SBXH") + ""));
				} else {
					parametersjzhm.put("SBXH", 0L);
				}
				Map<String, Object> cfmapjzhm = dao.doLoad(sql3, parametersjzhm);
				if (cfmapjzhm != null) {
					if (cfmapjzhm.get("JZHM") != null) {
						response.put("JZHM", cfmapjzhm.get("JZHM"));
					} else {
						response.put("JZHM", "");
					}
				}
				String ksmc = "";
				if (cfmap.get(0).get("KSDM") != null) {
					ksmc = DictionaryController.instance().getDic("phis.dictionary.department")
							.getText(cfmap.get(0).get("KSDM") + "");
				}
				String xzmc = "";
				if (cfmap.get(0).get("BRXZ") != null) {
					xzmc = DictionaryController.instance().getDic("phis.dictionary.patientProperties")
							.getText(cfmap.get(0).get("BRXZ") + "");
				}
				String ygxm = "";
				if (cfmap.get(0).get("YSDM") != null) {
					ygxm = DictionaryController.instance().getDic("phis.dictionary.doctor_cfqx")
							.getText(cfmap.get(0).get("YSDM") + "");
				}
				String brxb = "";
				if (cfmap.get(0).get("BRXB") != null) {
					brxb = DictionaryController.instance().getDic("phis.dictionary.gender")
							.getText(cfmap.get(0).get("BRXB") + "");
				}
				PublicModel pm = new PublicModel();
				String age = "";
				if (cfmap.get(0).get("CSNY") != null) {
					pm.doPersonAge(BSHISUtil.toDate(cfmap.get(0).get("CSNY") + ""),request);
					age = request.get("body").toString().substring(
									request.get("body").toString().lastIndexOf("=") + 1,
									request.get("body").toString().length() - 1);
				}
				response.put("title", jgname);
				response.put("BRXM", cfmap.get(0).get("BRXM"));
				response.put("BRXB", brxb);
				response.put("BRXZ", xzmc);
				response.put("BRNL", age);
//				response.put("KFRQ", BSHISUtil.getDate());
				//modify by lizhi 2017-10-24申请日期取开单日期
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				response.put("KFRQ", formatter.format((Date)cfmap.get(0).get("KDRQ")));
				
				response.put("KSMC", ksmc);
				response.put("JZHM", cfmap.get(0).get("JZKH"));
				response.put("MZHM", cfmap.get(0).get("MZHM"));
				Map<String, Object> zdparameters = new HashMap<String, Object>();
				zdparameters.put("JZXH", jzxh);
				StringBuffer sb = new StringBuffer("");
				String str = "";
				String zdsql = "select ZDMC as ZDMC, ZDBW as ZDBW, ZHMC as ZHMC from MS_BRZD a left join EMR_ZYZH b on a.ZDBW = b.ZHBS where JZXH=:JZXH order  by ZZBZ desc,PLXH ASC";
				List<Map<String, Object>> zdlist = dao.doSqlQuery(zdsql,zdparameters);
				for (int i = 0; i < zdlist.size(); i++) {
					sb.append(zdlist.get(i).get("ZDMC"));
					if(zdlist.get(i).get("ZDBW") != null && zdlist.get(i).get("ZDBW") != ""){
						String strZDBW = "";
						if(zdlist.get(i).get("ZHMC") == null){
							int ZDBW = Integer.parseInt(zdlist.get(i).get("ZDBW").toString());							
							if (ZDBW == 1)
								strZDBW = "头部";
							if (ZDBW == 2)
								strZDBW = "手臂";
							if (ZDBW == 3)
								strZDBW = "脚踝";
							if (ZDBW == 4)
								strZDBW = "左臂";
							if (ZDBW == 5)
								strZDBW = "右臂";	
						}else{
							strZDBW = zdlist.get(i).get("ZHMC").toString();
						}
						if(!strZDBW.equals("")){
							sb.append("(");
							sb.append(strZDBW);
							sb.append(")");		
						}	
					}			
					if (i + 1 != zdlist.size()) {
						sb.append(" ");
					}
				}
				if (sb.length() > 35) {
					str = sb.substring(0, 35);
					str = str + "...";
				} else {
					str = sb.toString();
				}
				response.put("ZDMC", str);
				response.put("YSDM", ygxm);
				String address = "";
				if (cfmap.get(0).get("ADDRESS") != null) {
					address = cfmap.get(0).get("ADDRESS") + "";
				}
				String mobilenumber = "";
				if (cfmap.get(0).get("MOBILENUMBER") != null) {
					mobilenumber = cfmap.get(0).get("MOBILENUMBER") + "";
				}
				response.put("ADDRESS", mobilenumber + "/" + address);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}
}
