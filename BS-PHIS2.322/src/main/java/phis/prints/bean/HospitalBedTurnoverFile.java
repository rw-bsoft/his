package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalBedTurnoverFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		String dateFrom = request.get("dateFrom")+"";
		String dateEnd = request.get("dateTo")+"";
		/*
1.实际开放总床日数：指年内医院各科每日夜晚12点开放病床数总和,不论该床是否被病人占用,都应计算在内。包括消毒和小修理等暂停使用的病床,超过半年的加床。不包括因病房扩建或大修而停用的病床及临时增设病床。
2.实际占用总床日数：指医院各科每日夜晚12点实际占用病床数(即每日夜晚12点住院人数)总和。包括实际占用的临时加床在内。病人入院后于当晚12点前死亡或因故出院的病人, 作为实际占用床位1天进行统计,同时亦应统计“出院者占用总床日数”1天,入院及出院人数各1人。
3.出院者占用总床日数：指所有出院人数的住院床日之总和。包括正常分娩、未产出院、住院经检查无病出院、未治出院及健康人进行人工流产或绝育手术后正常出院者的住院床日数。
4.平均开放病床数＝实际开放总床日数／统计期中的天数
5.病床使用率＝实际占用总床日数／实际开放总床日数X100％。
6.病床周转次数＝出院人数／平均开放床位数。
7.平均病床工作日＝实际占用总床日数／平均开放病床数。
8.出院者平均住院日＝出院者占用总床日数／出院人数。
9.床位工作效率 = 病床工作效率=平均病床工作日×病床周转次数= 统计期内占用床位总数平均开放病床数×出院人数/平均开放病床数=  统计期内占用床位总数×出院人数/平均开放病床数2
10.统计日期默认月初到当前日期，打印格式按参考页面展示格式打印
11.增加帮助功能对算法进行说明，可以通过增加帮助按钮打开说明的方式
ZY_HCMX
ZY_CWTJ
ZY_CWSZ
		 */
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String ref = user.getManageUnit().getRef();
		parameter.put("ref", ref);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			List<Map<String, Object>> ks_list = dao.doQuery("select a.ID as ID,a.OFFICENAME as OFFICENAME from SYS_Office a where a.HOSPITALDEPT=1 and ORGANIZCODE=:ref", parameter);
			for (int i = 0; i < ks_list.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("KS", ks_list.get(i).get("OFFICENAME"));
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("dateEnd", dateEnd);
				parameters.put("dateFrom",dateFrom);
				parameters.put("ksdm", ks_list.get(i).get("ID"));
				//实际开放总床日数
				List<Map<String, Object>> SJKFZCRS_list = dao.doSqlQuery("select nvl(sum(ZCRS),0) as SJKFZCRS from (select sum(trunc(to_date(:dateEnd,'yyyy-mm-dd'),'DD') - trunc(JCRQ,'DD')+1) as ZCRS from ZY_CWSZ where CWKS=:ksdm and to_char(JCRQ,'yyyy-mm-dd')>:dateFrom and to_char(JCRQ,'yyyy-mm-dd')<=:dateEnd union all select sum(trunc(to_date(:dateEnd,'yyyy-mm-dd'),'DD') - trunc(to_date(:dateFrom,'yyyy-mm-dd'),'DD')+1) as ZCRS from ZY_CWSZ where CWKS=:ksdm and to_char(JCRQ,'yyyy-mm-dd')<=:dateFrom)", parameters);
				if(Integer.parseInt(SJKFZCRS_list.get(0).get("SJKFZCRS")+"")==0)
					continue;
				int SJKFZCRS = Integer.parseInt(SJKFZCRS_list.get(0).get("SJKFZCRS")+"");
				map.put("SJKFZCRS", SJKFZCRS);
				//实际占用总床日数
				List<Map<String, Object>> SJZYZCRS_List = dao.doSqlQuery("select XSYS as XSYS, to_char(CZRQ,'yyyy-mm-dd') as CZRQ from ZY_CWTJ where JLXH in (select max(JLXH) from ZY_CWTJ where BRKS=:ksdm and to_char(CZRQ,'yyyy-mm-dd')>=:dateFrom and to_char(CZRQ,'yyyy-mm-dd')<=:dateEnd and BQPB=0 and CZRQ is not null group by CZRQ) order by CZRQ", parameters);
				parameters.remove("dateEnd");
				List<Map<String, Object>> SJZYZCRS_last = dao.doSqlQuery("select XSYS as XSYS, to_char(CZRQ,'yyyy-mm-dd') as CZRQ from ZY_CWTJ where JLXH in (select max(JLXH) from ZY_CWTJ where BRKS=:ksdm and to_char(CZRQ,'yyyy-mm-dd')<:dateFrom and BQPB=0 and CZRQ is not null group by CZRQ) order by CZRQ", parameters);
				int SJZYZCRS = 0;
				if(SJZYZCRS_last.size()>0){
					if(SJZYZCRS_List.size()>0){
						for(int j = 0 ; j < SJZYZCRS_List.size() ; j ++){
							if(j+1<SJZYZCRS_List.size()){
								Map<String, Object> SJZYZCRS_map1 = SJZYZCRS_List.get(j);
								Map<String, Object> SJZYZCRS_map2 = SJZYZCRS_List.get(j+1);
								Date CZRQ1 = sdf.parse(SJZYZCRS_map1.get("CZRQ").toString());
								Date CZRQ2 = sdf.parse(SJZYZCRS_map2.get("CZRQ").toString());
								SJZYZCRS += Integer.parseInt(SJZYZCRS_map1.get("XSYS")+"")*(int)((CZRQ2.getTime()-CZRQ1.getTime())/(1000*60*60*24));
							}else{
								Map<String, Object> SJZYZCRS_map = SJZYZCRS_List.get(j);
								if(SJZYZCRS_map.get("CZRQ")==null){
									SJZYZCRS = 0;
								}else{
									Date CZRQ = sdf.parse(SJZYZCRS_map.get("CZRQ").toString());
									SJZYZCRS += Integer.parseInt(SJZYZCRS_map.get("XSYS")+"")*(int) ((sdf.parse(dateEnd).getTime()-CZRQ.getTime())/(1000*60*60*24)+1);
								}
							}
						}
//						map.put("SJZYZCRS", SJZYZCRS);
					}else{
						int XSYS = Integer.parseInt(SJZYZCRS_last.get(0).get("XSYS")+"");
						SJZYZCRS += XSYS*(int) ((sdf.parse(dateEnd).getTime()-sdf.parse(dateFrom).getTime())/(1000*60*60*24)+1);
//						map.put("SJZYZCRS", SJZYZCRS);
					}
				}else if(SJZYZCRS_List.size()>0){
//					int SJZYZCRS = 0;
					for(int j = 0 ; j < SJZYZCRS_List.size() ; j ++){
						if(j+1<SJZYZCRS_List.size()){
							Map<String, Object> SJZYZCRS_map1 = SJZYZCRS_List.get(j);
							Map<String, Object> SJZYZCRS_map2 = SJZYZCRS_List.get(j+1);
							Date CZRQ1 = sdf.parse(SJZYZCRS_map1.get("CZRQ").toString());
							Date CZRQ2 = sdf.parse(SJZYZCRS_map2.get("CZRQ").toString());
							SJZYZCRS += Integer.parseInt(SJZYZCRS_map1.get("XSYS")+"")*(int) ((CZRQ2.getTime()-CZRQ1.getTime())/(1000*60*60*24));
						}else{
							Map<String, Object> SJZYZCRS_map = SJZYZCRS_List.get(j);
							Date CZRQ = sdf.parse(SJZYZCRS_map.get("CZRQ").toString());
							SJZYZCRS += Integer.parseInt(SJZYZCRS_map.get("XSYS")+"")*(int) ((sdf.parse(dateEnd).getTime()-CZRQ.getTime())/(1000*60*60*24)+1);
						}
					}
//					map.put("SJZYZCRS", SJZYZCRS);
				}else{
					SJZYZCRS = 0;
//					map.put("SJZYZCRS", 0);
				}
				map.put("SJZYZCRS", SJZYZCRS);
				//出院者占用总床日数
				parameters.put("dateEnd", dateEnd);
				List<Map<String, Object>> CYZZYZCRS_List = dao.doSqlQuery("select nvl(sum(TS),0) as CYZZYZCRS from (select trunc(b.JSRQ,'DD') - trunc(b.KSRQ,'DD')+1 as TS from ZY_BRRY a,ZY_ZYJS b where a.ZYH=b.ZYH and a.BRKS=:ksdm and a.CYPB>=8 and b.ZFPB=0 and to_char(b.KSRQ,'yyyy-mm-dd')>=:dateFrom and to_char(b.JSRQ,'yyyy-mm-dd')<=:dateEnd union all select trunc(to_date(:dateEnd,'yyyy-mm-dd'),'DD') - trunc(b.KSRQ,'DD')+1 as TS from ZY_BRRY a,ZY_ZYJS b where a.ZYH=b.ZYH and a.BRKS=:ksdm and a.CYPB>=8 and b.ZFPB=0 and to_char(b.KSRQ,'yyyy-mm-dd')>=:dateFrom and to_char(b.JSRQ,'yyyy-mm-dd')>:dateEnd and to_char(b.KSRQ,'yyyy-mm-dd')<=:dateEnd union all select trunc(b.JSRQ,'DD') - trunc(to_date(:dateFrom,'yyyy-mm-dd'),'DD')+1 as TS from ZY_BRRY a,ZY_ZYJS b where a.ZYH=b.ZYH and a.BRKS=:ksdm and a.CYPB>=8 and b.ZFPB=0 and to_char(b.KSRQ,'yyyy-mm-dd')<:dateFrom and to_char(b.JSRQ,'yyyy-mm-dd')<=:dateEnd and to_char(b.JSRQ,'yyyy-mm-dd')>=:dateFrom union all select trunc(to_date(:dateEnd,'yyyy-mm-dd'),'DD') - trunc(to_date(:dateFrom,'yyyy-mm-dd'),'DD')+1 as TS from ZY_BRRY a,ZY_ZYJS b where a.ZYH=b.ZYH and a.BRKS=:ksdm and a.CYPB>=8 and b.ZFPB=0 and to_char(b.KSRQ,'yyyy-mm-dd')<:dateFrom and to_char(b.JSRQ,'yyyy-mm-dd')>:dateEnd)", parameters);
				int CYZZYZCRS = Integer.parseInt(CYZZYZCRS_List.get(0).get("CYZZYZCRS")+"");
				map.put("CYZZYZCRS", CYZZYZCRS);
//				4.平均开放病床数＝实际开放总床日数／统计期中的天数
				int TJQZDTS = (int) ((sdf.parse(dateEnd).getTime()-sdf.parse(dateFrom).getTime())/(1000*60*60*24))+1;
				int PJKFBCS = 0;
				if(TJQZDTS != 0){
					PJKFBCS= SJKFZCRS/TJQZDTS;
				}
				map.put("PJKFBCS", PJKFBCS);
//				5.病床使用率＝实际占用总床日数／实际开放总床日数X100％。
				double CWSYL = 0;
				if(SJKFZCRS != 0 ){
					CWSYL = BSPHISUtil.getDouble(Double.parseDouble(SJZYZCRS*100+"")/Double.parseDouble(SJKFZCRS+""),1);
				}
				map.put("CWSYL", String.format("%1$.1f",CWSYL));
//				6.病床周转次数＝出院人数／平均开放床位数。
				List<Map<String, Object>> CYRS_List = dao.doSqlQuery("select sum(RS) as CYRS from (select count(*) as RS from ZY_BRRY a,ZY_ZYJS b where a.ZYH=b.ZYH and a.BRKS=:ksdm and a.CYPB>=8 and b.ZFPB=0 and to_char(b.KSRQ,'yyyy-mm-dd')>=:dateFrom and to_char(b.JSRQ,'yyyy-mm-dd')<=:dateEnd union all select count(*) as RS from ZY_BRRY a,ZY_ZYJS b where a.ZYH=b.ZYH and a.BRKS=:ksdm and a.CYPB>=8 and b.ZFPB=0 and to_char(b.KSRQ,'yyyy-mm-dd')>=:dateFrom and to_char(b.JSRQ,'yyyy-mm-dd')>:dateEnd and to_char(b.KSRQ,'yyyy-mm-dd')<=:dateEnd union all select count(*) as RS from ZY_BRRY a,ZY_ZYJS b where a.ZYH=b.ZYH and a.BRKS=:ksdm and a.CYPB>=8 and b.ZFPB=0 and to_char(b.KSRQ,'yyyy-mm-dd')<:dateFrom and to_char(b.JSRQ,'yyyy-mm-dd')<=:dateEnd and to_char(b.JSRQ,'yyyy-mm-dd')>=:dateFrom union all select count(*) as RS from ZY_BRRY a,ZY_ZYJS b where a.ZYH=b.ZYH and a.BRKS=:ksdm and a.CYPB>=8 and b.ZFPB=0 and to_char(b.KSRQ,'yyyy-mm-dd')<:dateFrom and to_char(b.JSRQ,'yyyy-mm-dd')>:dateEnd)", parameters);
				int CYRS = Integer.parseInt(CYRS_List.get(0).get("CYRS")+"");
				double CWZZCS = 0;
				if(CYRS != 0 && PJKFBCS!=0){
					CWZZCS = BSPHISUtil.getDouble(Double.parseDouble(CYRS+"")/Double.parseDouble(PJKFBCS+""),2);
				}
				map.put("CWZZCS", String.format("%1$.2f",CWZZCS));
//				7.平均病床工作日＝实际占用总床日数／平均开放病床数。
				double PJBCGZR = 0;
				if(SJZYZCRS!=0 && PJKFBCS != 0 ){
					PJBCGZR = BSPHISUtil.getDouble(Double.parseDouble(SJZYZCRS+"")/Double.parseDouble(PJKFBCS+""),2);
				}
				map.put("PJBCGZR", String.format("%1$.2f",PJBCGZR));
//				8.出院者平均住院日＝出院者占用总床日数／出院人数。
				double CYZPJZYR = 0;
				if(CYZZYZCRS!=0 && CYRS != 0 ){
					CYZPJZYR = BSPHISUtil.getDouble(Double.parseDouble(CYZZYZCRS+"")/Double.parseDouble(CYRS+""),2);
				}
				map.put("CYZPJBCGZR", String.format("%1$.2f",CYZPJZYR));
//				9.床位工作效率 = 病床工作效率=平均病床工作日×病床周转次数= 统计期内占用床位总数平均开放病床数×出院人数/平均开放病床数=  统计期内占用床位总数×出院人数/平均开放病床数2
				double CWGZXL = BSPHISUtil.getDouble(PJBCGZR*CWZZCS,2);
				map.put("CWGZXL", String.format("%1$.2f",CWGZXL));
//				10.统计日期默认月初到当前日期，打印格式按参考页面展示格式打印
//				11.增加帮助功能对算法进行说明，可以通过增加帮助按钮打开说明的方式
				records.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
//		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String TITLE = user.getManageUnitName();
//		String userName = user.getUserName();// 用户名
//		response.put("CZY", userName);// 操作员
//		String JGID = user.getManageUnitId();// 用户的机构ID
//		Long YF = 0L;
		String dateFrom = "";
		String dateEnd = "";
//		String yplx = "全部";
//		if (user.getProperty("pharmacyId") != null
//				&& user.getProperty("pharmacyId") != "") {
//			YF = Long.parseLong(user.getProperty("pharmacyId") + "");
//		}
		try {
			if (null != request.get("dateFrom")) {
				dateFrom = request.get("dateFrom") + "";
			}
			if (null != request.get("dateTo")) {
				dateEnd = request.get("dateTo") + "";
			}
//			if (null != request.get("yplx") && !request.get("yplx").equals("")&&!"0".equals(request.get("yplx"))) {
//				String ypdm = (String) request.get("yplx");
//				yplx = DictionaryController.instance()
//						.getDic("phis.dictionary.pecialMedicines")
//						.getText(ypdm);
//
//			}
			response.put("TITLE", TITLE + "住院床位使用情况统计");
			response.put("SJ", dateFrom + "至" + dateEnd);
//			response.put("YPLX", yplx);
//			if (YF != 0) {
//				Map<String, Object> parameter = new HashMap<String, Object>();
//				parameter.put("yfsb", YF);
//				parameter.put("jgid", JGID);
//				String sqlString = "SELECT YFMC FROM YF_YFLB WHERE YFSB =:yfsb and JGID = :jgid";
//				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString,
//						parameter);
//				response.put("YFMC", rklist.get(0) + "");
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
