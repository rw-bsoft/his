package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.pub.source.PublicModel;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PrescriptionChineFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		Long cfsb = 0L;
		if (request.get("cfsb") != null) {
			cfsb = Long.parseLong(request.get("cfsb") + "");
		}
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("CFSB", cfsb);
		String sql = "select cf.ZFYP as ZFYP,yt.YPMC as YPMC,cf.YPSL as YPJL,cf.YFDW as JLDW,zy.XMMC as YFMC, " +
				" cf.JZ as JZ from MS_CF02 cf,YK_TYPK yt,ZY_YPYF zy where cf.YPXH=yt.YPXH and cf.CFSB=:CFSB and zy.YPYF=cf.GYTJ";
		try {
			List<Map<String, Object>> cflist = dao.doQuery(sql, parameters);
			for(Map<String,Object> map_cf:cflist){//特殊处理自备药的名字
				if(Integer.parseInt(map_cf.get("ZFYP")+"")==1){
					map_cf.put("YPMC", "(自备)"+map_cf.get("YPMC"));
				}
			}
			int size=cflist.size();
			for (int i = 0; i <size; i = i + 2) {
//			for (int i = 0; i <size; i = i + 3) {
				Map<String, Object> cf = new HashMap<String, Object>();
				cf.put("YPMC1", cflist.get(i).get("YPMC")+"");
				if (cflist.get(i).get("YPJL") != null && cflist.get(i).get("YPJL") != "") {
					cf.put("YPJL1",(int) Math.floor((Double) cflist.get(i).get("YPJL"))+ "");
				}
				if (cflist.get(i).get("JLDW") != null && cflist.get(i).get("JLDW") != "") {
					cf.put("JLDW1", cflist.get(i).get("JLDW") + "");
				}
				if (cflist.get(i).get("YFMC") != null && cflist.get(i).get("YFMC") != "") {
					if(cflist.get(i).get("YFMC").equals("煎服")){
						cf.put("YFMC1", " ");
					} else {
						cf.put("YFMC1", "(" + (cflist.get(i).get("YFMC") + "").substring(0, 2)+")");
					}
				}
				cf.put("JZ1", this.getjzmc(cflist.get(i).get("JZ")+""));
				if (i + 1 < size) {
					cf.put("YPMC2", cflist.get(i+1).get("YPMC")+"");
					if (cflist.get(i + 1).get("YPJL") != null && cflist.get(i + 1).get("YPJL") != "") {
						cf.put("YPJL2",(int) Math.floor((Double) cflist.get(i + 1).get("YPJL")) + "");
					}
					if (cflist.get(i + 1).get("JLDW") != null && cflist.get(i + 1).get("JLDW") != "") {
						cf.put("JLDW2", cflist.get(i + 1).get("JLDW") + "");
					}
					if (cflist.get(i + 1).get("YFMC") != null && cflist.get(i + 1).get("YFMC") != "") {
						if(cflist.get(i + 1).get("YFMC").equals("煎服")){
							cf.put("YFMC2", " ");
						} else {
							cf.put("YFMC2", "(" + (cflist.get(i + 1).get("YFMC") + "").substring(0, 2)+")");
						}
					}
					cf.put("JZ2", this.getjzmc(cflist.get(i+1).get("JZ")+""));
				}
//				if (i + 2 <size) {
//					cf.put("YPMC3", cflist.get(i+2).get("YPMC")+"");
//					if (cflist.get(i + 2).get("YPJL") != null && cflist.get(i + 2).get("YPJL") != "") {
//						cf.put("YPJL3", (int) Math.floor((Double) cflist.get(i + 2).get("YPJL")) + "");
//					}
//					if (cflist.get(i + 2).get("JLDW") != null && cflist.get(i + 2).get("JLDW") != "") {
//						cf.put("JLDW3", cflist.get(i + 2).get("JLDW") + "");
//					}
//					if (cflist.get(i + 2).get("YFMC") != null && cflist.get(i + 2).get("YFMC") != "") {
//						if(cflist.get(i + 2).get("YFMC").equals("煎服")){
//							cf.put("YFMC3", " ");
//						} else {
//							cf.put("YFMC3", "(" + (cflist.get(i + 2).get("YFMC") + "").substring(0, 2)+")");
//						}
//					}
//					cf.put("JZ3", this.getjzmc(cflist.get(i+2).get("JZ")+""));
//				}
				if(i+1==size){
					cf.put("YPMC2", "(以下空白)");
				}
//				if(i+2==size){
//					cf.put("YPMC3", "(以下空白)");
//				}
				records.add(cf);
			}
			if(size%2==0){
//			if(size%3==0){
				Map<String, Object> cf = new HashMap<String, Object>();
				cf.put("YPMC1", "(以下空白)");
				records.add(cf);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	//获取脚注名称
	public String getjzmc(String jz){
		String jsstr="";
		if(jz==null || jz.equals("null") || jz.length()==0 ){
			return "";
		}
		try {
			jsstr=DictionaryController.instance().get("phis.dictionary.suggestedCY").getText(jz);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		if(jsstr.length() > 0 ){
			return "("+jsstr+")";
		}else{
			return "";
		}
	}
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		Long cfsb = 0L;
		if (request.get("cfsb") != null) {
			cfsb = Long.parseLong(request.get("cfsb") + "");
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametersjzhm = new HashMap<String, Object>();
		Map<String, Object> parametersgh = new HashMap<String, Object>();
		parameters.put("CFSB", cfsb);
		String sql = "select br.BRID as BRID,br.LXDZ as DZ,br.JTDH as DH ,cf.CFSB as CFSB,br.BRXM as BRXM,cf.CFHM as CFHM,br.BRXB as BRXB,br.CSNY as CSNY,cf.KFRQ as KFRQ,cf.KSDM as KSDM,br.JZKH as JZKH,br.MZHM as MZHM,br.BRXZ as BRXZ,cf.JZXH as JZXH,cf.YSDM as YSDM,cf.PYGH as PYGH,cf.HDGH as HDGH,cf.FYGH as FYGH,br.FYZH as FYZH,cf.MZXH as MZXH,yj.GHXH as SBXH from MS_CF01 cf left outer join YS_MZ_JZLS yj on cf.JZXH=yj.JZXH,MS_BRDA br,SYS_Personnel dm where cf.BRID=br.BRID and cf.YSDM=dm.PERSONID and cf.CFSB=:CFSB";
		String sql1 = "select count(cf.XMLX) as CYLX,sum(cf.HJJE) as YPJE,sum(cf.YPDJ*cf.YPSL) as DJHJ,sum(cf.YCJL) as YPZL from MS_CF02 cf where cf.CFSB=:CFSB";
		String sql2 = "select JZHM as JZHM from MS_GHMX where SBXH=:SBXH";
		String sql3 = "select MAX(SBXH) as SBXH from MS_GHMX where BRID=:BRID";
		
		try {
			Map<String, Object> jlparameters = new HashMap<String, Object>();
			jlparameters.put("CFSB", cfsb);
			String jlsql = "select distinct a.CFTS as CFTS,a.MRCS as MRCS,a.GYTJ as YPZS from MS_CF02 a where CFSB=:CFSB";
			List<Map<String, Object>> jlmapList = dao.doQuery(jlsql,
					jlparameters);
			String ypzs = "";
			boolean isJF = false;//是否煎服
			int cfts = 1;
			if (jlmapList!=null && jlmapList.size() > 0) {
				for(Map<String,Object> map : jlmapList){
					ypzs = DictionaryController.instance().getDic("phis.dictionary.drugMode")
							.getText(map.get("YPZS") + "");
					if(ypzs.equals("煎服")){
						isJF = true;
					}
				}
				if(isJF){
					response.put("YPZS", "药品用法:" + "煎服");
				} else {
					response.put("YPZS", "药品用法:" + ypzs);
				}
				
				if (jlmapList.get(0).get("CFTS") != null) {
					response.put("CFTS", jlmapList.get(0).get("CFTS") + "");
					cfts = Integer.parseInt(jlmapList.get(0).get("CFTS") + "");
				}
				if (jlmapList.get(0).get("MRCS") != null) {
					response.put("MRCS", jlmapList.get(0).get("MRCS") + "");
				}
				//response.put("YPZS", "药品用法:" + "煎服");
			}
			Map<String, Object> ypjemap = dao.doLoad(sql1, parameters);
			if (ypjemap != null) {
				if (ypjemap.get("YPJE") != null) {
					response.put("YPJE",
							String.format("%1$.2f", ypjemap.get("YPJE")));
					response.put("DJHJ",
							String.format("%1$.2f", Double.parseDouble(ypjemap.get("YPJE")+"")/cfts));
				}
				if (ypjemap.get("YPZL") != null) {
					response.put("YPZL",
							(int) Math.floor((Double) ypjemap.get("YPZL")) + "");
				}
				if (ypjemap.get("CYLX") != null) {
					System.out.println(ypjemap.get("CYLX"));
					response.put("CYLX",ypjemap.get("CYLX") + "");
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> cfmap = dao.doSqlQuery(sql, parameters).get(0);
			if (cfmap.get("SBXH") != null) {
				parametersjzhm.put("SBXH",
						Long.parseLong(cfmap.get("SBXH") + ""));
			} else {
				parametersgh
						.put("BRID", Long.parseLong(cfmap.get("BRID") + ""));
				Map<String, Object> ypjemapgh = dao.doLoad(sql3, parametersgh);
				parametersjzhm.put("SBXH",
						Long.parseLong(ypjemapgh.get("SBXH") + ""));
			}
			Map<String, Object> cfmapjzhm = dao.doQuery(sql2, parametersjzhm)
					.get(0);
			if (cfmapjzhm != null) {
				if (cfmapjzhm.get("JZHM") != null) {
					response.put("JZHM", cfmapjzhm.get("JZHM") + "");
				} else {
					response.put("JZHM", "");
				}
			}
			if (cfmap != null && cfmap.size() > 0) {
				String ksmc = "";
				if (cfmap.get("KSDM") != null && cfmap.get("KSDM") != "") {
					ksmc = DictionaryController.instance().getDic("phis.dictionary.department_leaf")
							.getText(cfmap.get("KSDM") + "");
				}
				String xzmc = "";
				if (cfmap.get("BRXZ") != null && cfmap.get("BRXZ") != "") {
					xzmc = DictionaryController.instance().getDic("phis.dictionary.patientProperties")
							.getText(cfmap.get("BRXZ") + "");
				}
				String ygxm = "";
				if (cfmap.get("YSDM") != null && cfmap.get("YSDM") != "") {
					ygxm = DictionaryController.instance().getDic("phis.dictionary.doctor_cfqx")
							.getText(cfmap.get("YSDM") + "");
				}
				String brxb = "";
				if (cfmap.get("BRXB") != null && cfmap.get("BRXB") != "") {
					brxb = DictionaryController.instance().getDic("phis.dictionary.gender")
							.getText(cfmap.get("BRXB") + "");
				}
				String dp = "";
				if (cfmap.get("PYGH") != null && cfmap.get("PYGH") != "") {
					dp = DictionaryController.instance().getDic("phis.dictionary.doctor")
							.getText(cfmap.get("PYGH") + "");
				}
				String hd = "";
				if (cfmap.get("HDGH") != null && cfmap.get("HDGH") != "") {
					hd = DictionaryController.instance().getDic("phis.dictionary.doctor_cfqx")
							.getText(cfmap.get("HDGH") + "");
				}
				String fy = "";
				if (cfmap.get("FYGH") != null && cfmap.get("FYGH") != "") {
					fy = DictionaryController.instance().getDic("phis.dictionary.doctor_cfqx")
							.getText(cfmap.get("FYGH") + "");
				}
				PublicModel pm = new PublicModel();
				String age = "";
				if (cfmap.get("CSNY") != null && cfmap.get("CSNY") != "") {
					pm.doPersonAge(BSHISUtil.toDate(cfmap.get("CSNY") + ""),
							request);
					age = request
							.get("body")
							.toString()
							.substring(
									request.get("body").toString()
											.lastIndexOf("=") + 1,
									request.get("body").toString().length() - 1);
				}
				String pygh = "";
				if (cfmap.get("PYGH") != null && cfmap.get("PYGH") != "") {
					pygh = DictionaryController.instance().getDic("phis.dictionary.doctor_cfqx")
							.getText(cfmap.get("PYGH") + "");
				}
				String fygh = "";
				if (cfmap.get("FYGH") != null && cfmap.get("FYGH") != "") {
					fygh = DictionaryController.instance().getDic("phis.dictionary.doctor_cfqx")
							.getText(cfmap.get("FYGH") + "");
				}

//				response.put("title", jgname + "处方笺");
				response.put("title", jgname);
				response.put("CFBH", cfmap.get("CFHM"));
				response.put("BRXM", cfmap.get("BRXM"));
				response.put("BRXB", brxb);
				if (cfmap.get("KFRQ") != null && cfmap.get("KFRQ") != "") {
					response.put("KFRQ", sdf.format(BSHISUtil.toDate(cfmap
							.get("KFRQ") + "")));
				} else {
					response.put("KFRQ", "");
				}
				response.put("AGE", age);
				response.put("KB", ksmc);
				// response.put("JZHM", cfmap.get("JZKH"));
				response.put("MZHM", cfmap.get("MZHM"));
				if (cfmap.get("MZXH") != null) {
					response.put("BLH", cfmap.get("MZXH") + "");
				}
				response.put("BRXZ", xzmc);
				response.put("ZZ", cfmap.get("ADDRESS"));

				// // 增加地址、电话
				// if (cfmap.get("HKDZ") != null || cfmap.get("HKDZ") != null) {
				// response.put("DZ", cfmap.get("HKDZ") + "");
				// }
				// if (cfmap.get("LXDH") != null || cfmap.get("LXDH") != null) {
				// response.put("DH", cfmap.get("LXDH") + "");
				// }
				// 联系电话、家庭地址
				if (cfmap.get("DZ") != null || cfmap.get("DZ") != null) {
					response.put("DZ", cfmap.get("DZ") + "");
				}
				if (cfmap.get("DH") != null || cfmap.get("DH") != null) {
					response.put("DH", cfmap.get("DH") + "");
				}

				Map<String, Object> zdparameters = new HashMap<String, Object>();
				if (cfmap.get("JZXH") != null && cfmap.get("JZXH") != "") {
					zdparameters.put("JZXH",
							Long.parseLong(cfmap.get("JZXH") + ""));
				} else {
					zdparameters.put("JZXH", 0L);
				}
				StringBuffer sb = new StringBuffer("");
				String str = "";
				StringBuffer sb1 = new StringBuffer("");
				String str1 = "";
				String zdsql = "select a.ZDMC as ZDMC,b.ZHMC as ZHMC from MS_BRZD a left join EMR_ZYZH b on a.ZDBW=b.ZHBS where a.JZXH=:JZXH";
				List<Map<String, Object>> zdlist = dao.doSqlQuery(zdsql,
						zdparameters);
				for (int i = 0; i < zdlist.size(); i++) {
					sb.append(zdlist.get(i).get("ZDMC")==null?"":zdlist.get(i).get("ZDMC"));
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
				for (int i = 0; i < zdlist.size(); i++) {
					sb1.append((zdlist.get(i).get("ZHMC")==null?"":zdlist.get(i).get("ZHMC")));
					if (i + 1 != zdlist.size()) {
						sb1.append(" ");
					}
				}
				if (sb1.length() > 35) {
					str1 = sb1.substring(0, 35);
					str1 = str1 + "...";
				} else {
					str1 = sb1.toString();
				}
				response.put("ZDMC", str+";"+str1);
				response.put("YSDM", ygxm);
				response.put("PYGH", dp);
				response.put("FYYS", hd + "、" + fy);
				response.put("YLZH", cfmap.get("FYZH"));
				response.put("TPYS", pygh);
				response.put("FYYS", fygh);
			}
			
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
