package phis.prints.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.JSONUtils;
import ctd.util.context.Context;

public class ClinicOutpatientWorkloadFile implements IHandler {
	@SuppressWarnings("unchecked")
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		String strConfig = (String) request.get("config");
		try {
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HashMap<String, Object> config = JSONUtils.parse(strConfig,
				HashMap.class);
		HashMap<String, Object> requestData = (HashMap<String, Object>) config
				.get("requestData");
		String beginDate = requestData.get("dateFrom") + "";
		String endDate = requestData.get("dateTo") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		Date adt_Begin = null;
		parameters.put("JGID", JGID);
		String xyfssql = "select count(b.CFSB) as XYFS,a.KSDM as KSDM,c.OFFICENAME as TJFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Office c where a.JZXH=b.JZXH and a.KSDM=c.ID and b.CFLX=1 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End group by a.KSDM,c.OFFICENAME";
		String zyfssql = "select count(b.CFSB) as ZYFS,a.KSDM as KSDM,c.OFFICENAME as TJFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Office c where a.JZXH=b.JZXH and a.KSDM=c.ID and b.CFLX=2 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End group by a.KSDM,c.OFFICENAME";
		String cyfssql = "select count(b.CFSB) as CYFS,a.KSDM as KSDM,c.OFFICENAME as TJFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Office c where a.JZXH=b.JZXH and a.KSDM=c.ID and b.CFLX=3 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End group by a.KSDM,c.OFFICENAME";
		String jcdssql = "select count(b.YJXH) as JCDS,a.KSDM as KSDM,c.OFFICENAME as TJFS from YS_MZ_JZLS a,MS_YJ01 b,SYS_Office c where a.JZXH=b.JZXH and a.KSDM=c.ID and a.JGID=:JGID and b.MZXH is not null and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End group by a.KSDM,c.OFFICENAME";
		String ybscfsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
		String lxscfsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
		String qtscfsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
		String ybsjcsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
		String lxsjcsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
		String qtsjcsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
		String rcscfsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
		String rcsyjsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
		if ("1".equals(requestData.get("ib_ys") + "")) {
			xyfssql = "select count(b.CFSB) as XYFS,replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,c.PERSONNAME as TJFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Personnel c where a.JZXH=b.JZXH and a.YSDM=c.PERSONID and b.CFLX=1 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End group by a.YSDM,c.PERSONNAME";
			zyfssql = "select count(b.CFSB) as ZYFS,replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,c.PERSONNAME as TJFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Personnel c where a.JZXH=b.JZXH and a.YSDM=c.PERSONID and b.CFLX=2 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End group by a.YSDM,c.PERSONNAME";
			cyfssql = "select count(b.CFSB) as CYFS,replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,c.PERSONNAME as TJFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Personnel c where a.JZXH=b.JZXH and a.YSDM=c.PERSONID and b.CFLX=3 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End group by a.YSDM,c.PERSONNAME";
			jcdssql = "select count(b.YJXH) as JCDS,replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,c.PERSONNAME as TJFS from YS_MZ_JZLS a,MS_YJ01 b,SYS_Personnel c where a.JZXH=b.JZXH and a.YSDM=c.PERSONID and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End group by a.YSDM,c.PERSONNAME";
			ybscfsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
			lxscfsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
			qtscfsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
			ybsjcsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
			lxsjcsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
			qtsjcsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
			rcscfsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
			rcsyjsql = "select count (JZXH) as RS, KSDM as KSDM from (select distinct replace(replace(a.YSDM,'X','99'),'x','99') as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a group by KSDM";
		}
		try {
			if (beginDate != null && beginDate != "") {
				adt_Begin = sdf.parse(beginDate.subSequence(0, 10)
						+ " 00:00:00");
			}
			Date adt_End = null;
			if (endDate != null && endDate != "") {
				adt_End = sdf.parse(endDate.subSequence(0, 10) + " 23:59:59");
			}
			parameters.put("adt_Begin", adt_Begin);
			parameters.put("adt_End", adt_End);
			List<Map<String, Object>> kdxxList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> xyfsList = dao.doQuery(xyfssql,
					parameters);
			List<Map<String, Object>> zyfsList = dao.doQuery(zyfssql,
					parameters);
			List<Map<String, Object>> cyfsList = dao.doQuery(cyfssql,
					parameters);
			List<Map<String, Object>> jcdsList = dao.doQuery(jcdssql,
					parameters);

			List<Map<String, Object>> ybscfList = dao.doSqlQuery(ybscfsql,
					parameters);
			List<Map<String, Object>> lxscfList = dao.doSqlQuery(lxscfsql,
					parameters);
			List<Map<String, Object>> qtscfList = dao.doSqlQuery(qtscfsql,
					parameters);
			List<Map<String, Object>> ybsjcList = dao.doSqlQuery(ybsjcsql,
					parameters);
			List<Map<String, Object>> lxsjcList = dao.doSqlQuery(lxsjcsql,
					parameters);
			List<Map<String, Object>> qtsjcList = dao.doSqlQuery(qtsjcsql,
					parameters);
			List<Map<String, Object>> rcscfList = dao.doSqlQuery(rcscfsql,
					parameters);
			List<Map<String, Object>> rcssyjList = dao.doSqlQuery(rcsyjsql,
					parameters);
			int sign1 = 0;
			for (int i = 0; i < xyfsList.size(); i++) {
				for (int j = 0; j < zyfsList.size(); j++) {
					if (parseLong(xyfsList.get(i).get("KSDM")) == parseLong(zyfsList
							.get(j).get("KSDM"))) {
						xyfsList.get(i)
								.put("ZYFS", zyfsList.get(j).get("ZYFS"));
						zyfsList.remove(j);
						sign1 = 1;
					} else {
						zyfsList.get(j).put("XYFS", "");
					}
				}
				if (sign1 == 0) {
					xyfsList.get(i).put("ZYFS", "");
				}
			}
			int sign2 = 0;
			for (int i = 0; i < xyfsList.size(); i++) {
				for (int j = 0; j < cyfsList.size(); j++) {
					if (parseLong(xyfsList.get(i).get("KSDM")) == parseLong(cyfsList
							.get(j).get("KSDM"))) {
						xyfsList.get(i)
								.put("CYFS", cyfsList.get(j).get("CYFS"));
						cyfsList.remove(j);
						sign2 = 1;
					} else {
						cyfsList.get(j).put("XYFS", "");
					}
				}
				if (sign2 == 0) {
					xyfsList.get(i).put("CYFS", "");
				}
			}
			int sign3 = 0;
			for (int i = 0; i < xyfsList.size(); i++) {
				for (int j = 0; j < jcdsList.size(); j++) {
					if (parseLong(xyfsList.get(i).get("KSDM")) == parseLong(jcdsList
							.get(j).get("KSDM"))) {
						xyfsList.get(i)
								.put("JCDS", jcdsList.get(j).get("JCDS"));
						jcdsList.remove(j);
						sign3 = 1;
					} else {
						jcdsList.get(j).put("XYFS", "");
					}
				}
				if (sign3 == 0) {
					xyfsList.get(i).put("JCDS", "");
				}
			}
			int sign4 = 0;
			for (int i = 0; i < zyfsList.size(); i++) {
				for (int j = 0; j < cyfsList.size(); j++) {
					if (parseLong(zyfsList.get(i).get("KSDM")) == parseLong(cyfsList
							.get(j).get("KSDM"))) {
						zyfsList.get(i)
								.put("CYFS", cyfsList.get(j).get("CYFS"));
						cyfsList.remove(j);
						sign4 = 1;
					} else {
						cyfsList.get(j).put("ZYFS", "");
					}
				}
				if (sign4 == 0) {
					zyfsList.get(i).put("CYFS", "");
				}
			}
			int sign5 = 0;
			for (int i = 0; i < zyfsList.size(); i++) {
				for (int j = 0; j < jcdsList.size(); j++) {
					if (parseLong(zyfsList.get(i).get("KSDM")) == parseLong(jcdsList
							.get(j).get("KSDM"))) {
						zyfsList.get(i)
								.put("JCDS", jcdsList.get(j).get("JCDS"));
						jcdsList.remove(j);
						sign5 = 1;
					} else {
						jcdsList.get(j).put("ZYFS", "");
					}
				}
				if (sign5 == 0) {
					zyfsList.get(i).put("JCDS", "");
				}
			}
			int sign6 = 0;
			for (int i = 0; i < cyfsList.size(); i++) {
				for (int j = 0; j < jcdsList.size(); j++) {
					if (parseLong(cyfsList.get(i).get("KSDM")) == parseLong(jcdsList
							.get(j).get("KSDM"))) {
						cyfsList.get(i)
								.put("JCDS", jcdsList.get(j).get("JCDS"));
						jcdsList.remove(j);
						sign6 = 1;
					} else {
						jcdsList.get(j).put("ZYFS", "");
					}
				}
				if (sign6 == 0) {
					cyfsList.get(i).put("JCDS", "");
				}
			}
			kdxxList.addAll(xyfsList);
			kdxxList.addAll(zyfsList);
			kdxxList.addAll(cyfsList);
			kdxxList.addAll(jcdsList);
			for (int i = 0; i < ybscfList.size(); i++) {
				for (int j = 0; j < kdxxList.size(); j++) {
					if (parseLong(ybscfList.get(i).get("KSDM")) == parseLong(kdxxList
							.get(j).get("KSDM"))) {
						kdxxList.get(j)
								.put("YBRCS", ybscfList.get(i).get("RS"));
						break;
					}
				}
			}
			for (int i = 0; i < lxscfList.size(); i++) {
				for (int j = 0; j < kdxxList.size(); j++) {
					if (parseLong(lxscfList.get(i).get("KSDM")) == parseLong(kdxxList
							.get(j).get("KSDM"))) {
						kdxxList.get(j).put("LXRC", lxscfList.get(i).get("RS"));
						break;
					}
				}
			}
			for (int i = 0; i < qtscfList.size(); i++) {
				for (int j = 0; j < kdxxList.size(); j++) {
					if (parseLong(qtscfList.get(i).get("KSDM")) == parseLong(kdxxList
							.get(j).get("KSDM"))) {
						kdxxList.get(j).put("QTRC", qtscfList.get(i).get("RS"));
						break;
					}
				}
			}
			for (int i = 0; i < rcscfList.size(); i++) {
				for (int j = 0; j < kdxxList.size(); j++) {
					if (parseLong(rcscfList.get(i).get("KSDM")) == parseLong(kdxxList
							.get(j).get("KSDM"))) {
						kdxxList.get(j).put("RCS", rcscfList.get(i).get("RS"));
						break;
					}
				}
			}
			for (int i = 0; i < ybsjcList.size(); i++) {
				for (int j = 0; j < kdxxList.size(); j++) {
					if (parseLong(ybsjcList.get(i).get("KSDM")) == parseLong(kdxxList
							.get(j).get("KSDM"))) {
						int ybrc = 0;
						if (kdxxList.get(j).containsKey("YBRCS")) {
							ybrc = parseInt(kdxxList.get(j).get("YBRCS"));
						}
						kdxxList.get(j).put(
								"YBRCS",
								(ybrc + parseInt(ybsjcList.get(i).get("RS")))
										+ "");
					}
				}
			}
			for (int i = 0; i < lxsjcList.size(); i++) {
				for (int j = 0; j < kdxxList.size(); j++) {
					if (parseLong(lxsjcList.get(i).get("KSDM")) == parseLong(kdxxList
							.get(j).get("KSDM"))) {
						int lxrc = 0;
						if (kdxxList.get(j).containsKey("LXRC")) {
							lxrc = parseInt(kdxxList.get(j).get("LXRC"));
						}
						kdxxList.get(j).put(
								"LXRC",
								(lxrc + parseInt(lxsjcList.get(i).get("RS")))
										+ "");
						break;
					}
				}
			}
			for (int i = 0; i < qtsjcList.size(); i++) {
				for (int j = 0; j < kdxxList.size(); j++) {
					if (parseLong(qtsjcList.get(i).get("KSDM")) == parseLong(kdxxList
							.get(j).get("KSDM"))) {
						int qtrc = 0;
						if (kdxxList.get(j).containsKey("QTRC")) {
							qtrc = parseInt(kdxxList.get(j).get("QTRC"));
						}
						kdxxList.get(j).put(
								"QTRC",
								(qtrc + parseInt(qtsjcList.get(i).get("RS")))
										+ "");
						break;
					}
				}
			}
			for (int i = 0; i < rcssyjList.size(); i++) {
				for (int j = 0; j < kdxxList.size(); j++) {
					if (parseLong(rcssyjList.get(i).get("KSDM")) == parseLong(kdxxList
							.get(j).get("KSDM"))) {
						int rcs = 0;
						if (kdxxList.get(j).containsKey("RCS")) {
							rcs = parseInt(kdxxList.get(j).get("RCS"));
						}
						kdxxList.get(j).put(
								"RCS",
								(rcs + parseInt(rcssyjList.get(i).get("RS")))
										+ "");
						break;
					}
				}
			}
			records.addAll(kdxxList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		String strConfig = (String) request.get("config");
		String TITLE = user.getManageUnitName();
		try {
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HashMap<String, Object> config = JSONUtils.parse(strConfig,
				HashMap.class);
		HashMap<String, Object> requestData = (HashMap<String, Object>) config
				.get("requestData");
		if ("1".equals(requestData.get("ib_ks") + "")) {
			response.put("title", TITLE + "科室工作量");
			response.put("LB", "科室名称");
		}
		if ("1".equals(requestData.get("ib_ys") + "")) {
			response.put("title", TITLE + "医生工作量");
			response.put("LB", "医生姓名");
		}
		String beginDate = requestData.get("dateFrom") + "";
		String endDate = requestData.get("dateTo") + "";
		response.put("startDate", beginDate.substring(0, 10));
		response.put("endDate", endDate.substring(0, 10));
		response.put("Lister", user.getUserName());
		response.put("DateTabling", sdf.format(new Date()));
		Map<String, Object> parameters = new HashMap<String, Object>();
		Date adt_Begin = null;
		parameters.put("JGID", JGID);
		String xyfssql = "select count(b.CFSB) as XYFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Office c where a.JZXH=b.JZXH and a.KSDM=c.ID and b.CFLX=1 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End";
		String zyfssql = "select count(b.CFSB) as ZYFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Office c where a.JZXH=b.JZXH and a.KSDM=c.ID and b.CFLX=2 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End";
		String cyfssql = "select count(b.CFSB) as CYFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Office c where a.JZXH=b.JZXH and a.KSDM=c.ID and b.CFLX=3 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End";
		String jcdssql = "select count(b.YJXH) as JCDS from YS_MZ_JZLS a,MS_YJ01 b,SYS_Office c where a.JZXH=b.JZXH and a.KSDM=c.ID and a.JGID=:JGID and b.MZXH is not null and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End";
		String ybscfsql = "select count (JZXH) as RS from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
		String lxscfsql = "select count (JZXH) as RS from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
		String qtscfsql = "select count (JZXH) as RS from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
		String ybsjcsql = "select count (JZXH) as RS from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
		String lxsjcsql = "select count (JZXH) as RS from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
		String qtsjcsql = "select count (JZXH) as RS from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
		String rcscfsql = "select count (JZXH) as RS from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End)";
		String rcsyjsql = "select count (JZXH) as RS from (select distinct a.KSDM as KSDM,b.JZXH as JZXH,c.OFFICENAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Office c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.KSDM = c.ID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
		if ("1".equals(requestData.get("ib_ys") + "")) {
			xyfssql = "select count(b.CFSB) as XYFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Personnel c where a.JZXH=b.JZXH and a.YSDM=c.PERSONID and b.CFLX=1 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End";
			zyfssql = "select count(b.CFSB) as ZYFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Personnel c where a.JZXH=b.JZXH and a.YSDM=c.PERSONID and b.CFLX=2 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End";
			cyfssql = "select count(b.CFSB) as CYFS from YS_MZ_JZLS a,MS_CF01 b,SYS_Personnel c where a.JZXH=b.JZXH and a.YSDM=c.PERSONID and b.CFLX=3 and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End";
			jcdssql = "select count(b.YJXH) as JCDS from YS_MZ_JZLS a,MS_YJ01 b,SYS_Personnel c where a.JZXH=b.JZXH and a.YSDM=c.PERSONID and b.MZXH is not null and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End";
			ybscfsql = "select count (JZXH) as RS from (select distinct a.YSDM as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
			lxscfsql = "select count (JZXH) as RS from (select distinct a.YSDM as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
			qtscfsql = "select count (JZXH) as RS from (select distinct a.YSDM as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
			ybsjcsql = "select count (JZXH) as RS from (select distinct a.YSDM as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=1 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
			lxsjcsql = "select count (JZXH) as RS from (select distinct a.YSDM as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and f.XZDL=2 and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
			qtsjcsql = "select count (JZXH) as RS from (select distinct a.YSDM as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and (f.XZDL=3 or f.XZDL is null) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
			rcscfsql = "select count (JZXH) as RS from (select distinct a.YSDM as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
			rcsyjsql = "select count (JZXH) as RS from (select distinct a.YSDM as KSDM,b.JZXH as JZXH,c.PERSONNAME as TJFS from YS_MZ_JZLS a, MS_YJ01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and b.JZXH not in (select distinct b.JZXH as JZXH from YS_MZ_JZLS a, MS_CF01 b, SYS_Personnel c,MS_MZXX e,GY_BRXZ f where a.JZXH = b.JZXH and a.YSDM = c.PERSONID and b.MZXH=e.MZXH and e.BRXZ=f.BRXZ and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) and a.JGID=:JGID and a.KSSJ>=:adt_Begin and a.KSSJ<=:adt_End) a";
		}
		try {
			if (beginDate != null && beginDate != "") {
				adt_Begin = sdf.parse(beginDate.subSequence(0, 10)
						+ " 00:00:00");
			}
			Date adt_End = null;
			if (endDate != null && endDate != "") {
				adt_End = sdf.parse(endDate.subSequence(0, 10) + " 23:59:59");
			}
			parameters.put("adt_Begin", adt_Begin);
			parameters.put("adt_End", adt_End);
			List<Map<String, Object>> xyfsList = dao.doQuery(xyfssql,
					parameters);
			List<Map<String, Object>> zyfsList = dao.doQuery(zyfssql,
					parameters);
			List<Map<String, Object>> cyfsList = dao.doQuery(cyfssql,
					parameters);
			List<Map<String, Object>> jcdsList = dao.doQuery(jcdssql,
					parameters);

			List<Map<String, Object>> ybscfList = dao.doSqlQuery(ybscfsql,
					parameters);
			List<Map<String, Object>> lxscfList = dao.doSqlQuery(lxscfsql,
					parameters);
			List<Map<String, Object>> qtscfList = dao.doSqlQuery(qtscfsql,
					parameters);
			List<Map<String, Object>> ybsjcList = dao.doSqlQuery(ybsjcsql,
					parameters);
			List<Map<String, Object>> lxsjcList = dao.doSqlQuery(lxsjcsql,
					parameters);
			List<Map<String, Object>> qtsjcList = dao.doSqlQuery(qtsjcsql,
					parameters);
			List<Map<String, Object>> rcscfList = dao.doSqlQuery(rcscfsql,
					parameters);
			List<Map<String, Object>> rcssyjList = dao.doSqlQuery(rcsyjsql,
					parameters);
			if (parseInt(rcscfList.get(0).get("RS"))
					+ parseInt(rcssyjList.get(0).get("RS")) > 0) {
				response.put(
						"RCSALL",
						(parseInt(rcscfList.get(0).get("RS")) + parseInt(rcssyjList
								.get(0).get("RS"))) + "");
			} else {
				response.put("RCSALL", "");
			}
			if (parseInt(jcdsList.get(0).get("JCDS")) > 0) {
				response.put("JCDSALL", (jcdsList.get(0).get("JCDS")) + "");
			} else {
				response.put("JCDSALL", "");
			}
			if (parseInt(xyfsList.get(0).get("XYFS")) > 0) {
				response.put("XYFSALL", (xyfsList.get(0).get("XYFS")) + "");
			} else {
				response.put("XYFSALL", "");
			}
			if (parseInt(zyfsList.get(0).get("ZYFS")) > 0) {
				response.put("ZYFSALL", (zyfsList.get(0).get("ZYFS")) + "");
			} else {
				response.put("ZYFSALL", "");
			}
			if (parseInt(cyfsList.get(0).get("CYFS")) > 0) {
				response.put("CYFSALL", (cyfsList.get(0).get("CYFS")) + "");
			} else {
				response.put("CYFSALL", "");
			}
			if (parseInt(ybscfList.get(0).get("RS"))
					+ parseInt(ybsjcList.get(0).get("RS")) > 0) {
				response.put(
						"YBRCSALL",
						(parseInt(ybscfList.get(0).get("RS")) + parseInt(ybsjcList
								.get(0).get("RS"))) + "");
			} else {
				response.put("YBRCSALL", "");
			}
			if (parseInt(lxscfList.get(0).get("RS"))
					+ parseInt(lxsjcList.get(0).get("RS")) > 0) {
				response.put(
						"LXRCALL",
						(parseInt(lxscfList.get(0).get("RS")) + parseInt(lxsjcList
								.get(0).get("RS"))) + "");
			} else {
				response.put("LXRCALL", "");
			}
			if (parseInt(qtscfList.get(0).get("RS"))
					+ parseInt(qtsjcList.get(0).get("RS")) > 0) {
				response.put(
						"QTRCALL",
						(parseInt(qtscfList.get(0).get("RS")) + parseInt(qtsjcList
								.get(0).get("RS"))) + "");
			} else {
				response.put("QTRCALL", "");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public long parseLong(Object o) {
		if (o == null || "null".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	public int parseInt(Object o) {
		if (o == null || "null".equals(o)) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}
}
