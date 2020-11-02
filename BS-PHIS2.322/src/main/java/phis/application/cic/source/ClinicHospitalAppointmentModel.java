package phis.application.cic.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.schedule.BedCostCalculationSchedule;
import phis.source.service.ServiceCode;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * 
 * @description 转科管理Model
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class ClinicHospitalAppointmentModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ClinicHospitalAppointmentModel.class);

	public ClinicHospitalAppointmentModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doQueryYyksInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String ref = user.getManageUnit().getRef();
		String jgid = user.getManageUnit().getId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> infoMap = new HashMap<String, Object>();
		parameters.put("ORGANIZCODE", ref);
		parameters.put("HOSPITALDEPT", "1");
		parameters.put("LOGOFF", "0");
		try {
			StringBuffer sql_list = new StringBuffer(
					"select a.ID as YYKS from SYS_Office a where a.ORGANIZCODE=:ORGANIZCODE and a.HOSPITALDEPT=:HOSPITALDEPT and LOGOFF=:LOGOFF and OFFICECODE not in(select PARENTID from SYS_Office where ORGANIZCODE <> PARENTID and LOGOFF=:LOGOFF and ORGANIZCODE=:ORGANIZCODE)");
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			for (int i = 0; i < inofList.size(); i++) {
				infoMap.put("KS",
						MedicineUtils.parseLong(inofList.get(i).get("YYKS")));
				infoMap.put("JGID", jgid);
				inofList.get(i)
						.put("CWZS",
								dao.doLoad(
										"select count(BRCH) as CWZS from ZY_CWSZ where CWKS=:KS and JGID=:JGID",
										infoMap).get("CWZS"));
				inofList.get(i)
						.put("KCWS",
								dao.doLoad(
										"select count(BRCH) as KCWS from ZY_CWSZ where CWKS=:KS and JGID=:JGID and ZYH is null",
										infoMap).get("KCWS"));
				infoMap.put("CYPB", 1);
				if ("0".equals(req.get("cnds") + "")) {
					infoMap.put("RQ", sdf.format(new Date()));
				} else {
					infoMap.put("RQ",
							req.get("cnds").toString().substring(0, 10));
				}
				inofList.get(i)
						.put("CYRS",
								dao.doLoad(
										"select count(ZYH) as CYRS from ZY_BRRY where BRKS=:KS and JGID=:JGID and CYPB=:CYPB and str(CYRQ,'YYYY-MM-DD')=:RQ",
										infoMap).get("CYRS"));
				infoMap.remove("CYPB");
				infoMap.put("JGBZ", 0);
				inofList.get(i)
						.put("YYRS",
								dao.doLoad(
										"select count(SBXH) as YYRS from ZY_YYBR where YYKS=:KS and JGID=:JGID and JGBZ=:JGBZ and str(YYRQ,'YYYY-MM-DD')=:RQ",
										infoMap).get("YYRS"));
				infoMap.remove("RQ");
				infoMap.remove("JGBZ");
			}
			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.MS_ZYYY_YYKS);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("查询数据失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询数据失败!");
		}
	}

	public void doQueryBrryinfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("CYPB", 8);
		parameters.put("JGID", jgid);
		parameters.put("BRID", Long.parseLong(req.get("BRID") + ""));
		try {
			long l = dao.doCount("ZY_BRRY",
					"CYPB<:CYPB and JGID=:JGID and BRID=:BRID", parameters);
			if (l > 0) {
				res.put(Service.RES_CODE, 600);
				res.put(Service.RES_MESSAGE, "该病人已入院!");
				return;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doSaveHospitalAppointment(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> yyinfo = new HashMap<String, Object>();
		Map<String, Object> saveMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String brid = body.get("BRID") + "";
			yyinfo.put("BRID", brid);
			yyinfo.put("JGID", jgid);
			yyinfo.put("JGBZ", 0);
			Map<String, Object> ksmcmap = dao.doLoad("select b.OFFICENAME as KSMC from ZY_YYBR a,SYS_Office b " +
					" where a.YYKS=b.ID and BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ",yyinfo);
			if (ksmcmap != null) {
				doUpdateHospitalAppointment(body,res,ctx);
//				if (ksmcmap.containsKey("KSMC")) {
//					if (ksmcmap.get("KSMC") != null) {
//						throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "当前病人已经预约在["
//										+ ksmcmap.get("KSMC") + "]请先取消预约!");
//					}
//				}
			}
			
			long yyks = Long.parseLong(body.get("YYKS") + "");
			parameters.put("BRID", brid);
			saveMap = dao.doLoad("select BRID as BRID,MZHM as MZHM,BRXM as BRXM,BRXZ as BRXZ,BRXB as BRXB," +
					" SFZH as SFZH,DWMC as GZDW,HYZK as HYZK,ZYDM as ZYDM,MZDM as MZDM,DWDH as DWDH," +
					" DWYB as DWYB,HKDZ as HKDZ,JTDH as JTDH,HKYB as HKYB,SFDM as SFDM,JGDM as JGDM," +
					" GJDM as GJDM,LXRM as LXRM,LXGX as LXGX,LXDZ as LXDZ,LXDH as LXDH,DBRM as DBRM," +
					" DBGX as DBGX,SBHM as SBHM,ZZTX as ZZTX,CSD_SQS as CSD_SQS,CSD_S as CSD_S," +
					" CSD_X as CSD_X,JGDM_SQS as JGDM_SQS,JGDM_S as JGDM_S,XZZ_SQS as XZZ_SQS," +
					" XZZ_S as XZZ_S,XZZ_X as XZZ_X,XZZ_YB as XZZ_YB,XZZ_DH as XZZ_DH,HKDZ_SQS as HKDZ_SQS," +
					" HKDZ_S as HKDZ_S,HKDZ_X as HKDZ_X,XZZ_QTDZ as XZZ_QTDZ,HKDZ_QTDZ as HKDZ_QTDZ " +
					" from MS_BRDA where BRID=:BRID",parameters);
			saveMap.put("JGID", jgid);
			saveMap.put("YYKS", yyks);
			saveMap.put("YYRQ", sdf.parse(body.get("YYRQ") + ""));
			saveMap.put("CSNY", sdf.parse(body.get("CSNY") + ""));
			saveMap.put("DJRQ", new Date());
			saveMap.put("JGBZ", 0);
			saveMap.put("CZGH", user.getUserId());
			Map<String, Object> bahmpar = new HashMap<String, Object>();
			bahmpar.put("BRID", Long.parseLong(brid));
			bahmpar.put("JGBZ", 1);
			bahmpar.put("JGID", jgid);
			List<Map<String, Object>> bahmList = new ArrayList<Map<String, Object>>();
			bahmList = dao.doQuery("select BAHM as BAHM from ZY_YYBR where BRID=:BRID and JGBZ=:JGBZ " +
					" and JGID=:JGID order by SBXH desc",bahmpar);
			if (bahmList.size() > 0) {
				if (bahmList.get(0) != null) {
					if (bahmList.get(0).get("BAHM") != null&& bahmList.get(0).get("BAHM") != "") {
						saveMap.put("BAHM", bahmList.get(0).get("BAHM") + "");
					}
				}
			}
			dao.doSave("create", BSPHISEntryNames.MS_ZYYY_YYKS, saveMap, false);
		} catch (ValidateException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "预约失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "预约失败");
		} catch (ParseException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "预约失败");
		}
	}

	public void doUpdateHospitalAppointment(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> yyinfo = new HashMap<String, Object>();
		try {
			String brid = body.get("BRID") + "";
			yyinfo.put("BRID", Long.parseLong(brid));
			yyinfo.put("JGID", jgid);
			yyinfo.put("JGBZ", 0);
			long l = dao.doCount("ZY_YYBR","BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ", yyinfo);
			if (l <= 0) {
				return;
//				throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "当前病人还没有预约!");
			}
			yyinfo.put("BZ", 2);
			dao.doUpdate("update ZY_YYBR set JGBZ=:BZ where BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ",yyinfo);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "取消预约失败");
		}
	}
	
	public void doSaveInpatientCertificate(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> yyinfo = new HashMap<String, Object>();
		Map<String, Object> saveMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String brid = body.get("BRID") + "";
			yyinfo.put("BRID", brid);
			yyinfo.put("JGID", jgid);
			yyinfo.put("JGBZ", 0);
			Map<String, Object> ksmcmap = dao.doLoad("select b.OFFICENAME as KSMC from ZY_YYBR a,SYS_Office b " +
					" where a.YYKS=b.ID and BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ",yyinfo);
			if (ksmcmap != null) {
				doUpdateInpatientCertificate(body,res,ctx);
			}
			/*****add by lizhi 2017-12-08住院证更新病人档案联系人、联系地址和联系电话等信息*****/
			boolean flag = false;
			String sql = "update MS_BRDA ";
			if(body.get("LXRM")!=null){
				sql+="set LXRM=:LXRM";
				parameters.put("LXRM", body.get("LXRM")+"");
				flag = true;
			}
			if(body.get("LXGX")!=null){
				sql += flag?",":"set";
				sql += " LXGX=:LXGX";
				parameters.put("LXGX", body.get("LXGX")+"");
				flag = true;
			}
			if(body.get("LXDZ")!=null){
				sql += flag?",":"set";
				sql += " LXDZ=:LXDZ";
				parameters.put("LXDZ", body.get("LXDZ")+"");
				flag = true;
			}
			if(body.get("LXDH")!=null){
				sql += flag?",":"set";
				sql += " LXDH=:LXDH";
				parameters.put("LXDH", body.get("LXDH")+"");
				flag = true;
			}
			if(body.get("BRDHHM")!=null){
				sql += flag?",":"set";
				sql += " BRDHHM=:BRDHHM";
				parameters.put("BRDHHM", body.get("BRDHHM")+"");
				flag = true;
			}
			sql+=" where BRID=:BRID";
			parameters.put("BRID", brid);
			if(flag){
				dao.doUpdate(sql, parameters);
			}
			/*****add by lizhi 2017-12-08住院证更新病人档案联系人、联系地址和联系电话等信息*****/
			long yyks = Long.parseLong(body.get("YYKS") + "");
			parameters.clear();
			parameters.put("BRID", brid);
			saveMap = dao.doLoad("select BRID as BRID,MZHM as MZHM,BRXM as BRXM,BRXZ as BRXZ,BRXB as BRXB," +
					" SFZH as SFZH,DWMC as GZDW,HYZK as HYZK,ZYDM as ZYDM,MZDM as MZDM,DWDH as DWDH," +
					" DWYB as DWYB,HKDZ as HKDZ,JTDH as JTDH,HKYB as HKYB,SFDM as SFDM,JGDM as JGDM," +
					" GJDM as GJDM,LXRM as LXRM,LXGX as LXGX,LXDZ as LXDZ,LXDH as LXDH,DBRM as DBRM," +
					" DBGX as DBGX,SBHM as SBHM,ZZTX as ZZTX,CSD_SQS as CSD_SQS,CSD_S as CSD_S," +
					" CSD_X as CSD_X,JGDM_SQS as JGDM_SQS,JGDM_S as JGDM_S,XZZ_SQS as XZZ_SQS,BRDHHM as BRDHHM," +
					" XZZ_S as XZZ_S,XZZ_X as XZZ_X,XZZ_YB as XZZ_YB,XZZ_DH as XZZ_DH,HKDZ_SQS as HKDZ_SQS," +
					" HKDZ_S as HKDZ_S,HKDZ_X as HKDZ_X,XZZ_QTDZ as XZZ_QTDZ,HKDZ_QTDZ as HKDZ_QTDZ " +
					" from MS_BRDA where BRID=:BRID",parameters);
			saveMap.put("JGID", jgid);
			saveMap.put("YYKS", yyks);
			saveMap.put("YYRQ", sdf.parse(body.get("YYRQ") + ""));
			saveMap.put("CSNY", sdf.parse(body.get("CSNY") + ""));
			saveMap.put("DJRQ", new Date());
			saveMap.put("JGBZ", 0);
			saveMap.put("CZGH", user.getUserId());
			/*****************add by lizhi 2017-12-08住院证增加字段******************/
			saveMap.put("BQDM", body.get("BQDM")!=null?Long.parseLong(body.get("BQDM") + ""):null);
			saveMap.put("BRCH", body.get("BRCH")!=null?(body.get("BRCH") + ""):"");
			saveMap.put("YZ", body.get("YZ")!=null?(body.get("YZ") + ""):"");
			saveMap.put("REMARK", body.get("REMARK")!=null?(body.get("REMARK") + ""):"");
			saveMap.put("INHOSMETHOD", body.get("INHOSMETHOD")!=null?(body.get("INHOSMETHOD") + ""):"");
			/*****************add by lizhi 2017-12-08住院证增加字段******************/
			Map<String, Object> bahmpar = new HashMap<String, Object>();
			bahmpar.put("BRID", Long.parseLong(brid));
			bahmpar.put("JGBZ", 1);
			bahmpar.put("JGID", jgid);
			List<Map<String, Object>> bahmList = new ArrayList<Map<String, Object>>();
			bahmList = dao.doQuery("select BAHM as BAHM from ZY_YYBR where BRID=:BRID and JGBZ=:JGBZ " +
					" and JGID=:JGID order by SBXH desc",bahmpar);
			if (bahmList.size() > 0) {
				if (bahmList.get(0) != null) {
					if (bahmList.get(0).get("BAHM") != null&& bahmList.get(0).get("BAHM") != "") {
						saveMap.put("BAHM", bahmList.get(0).get("BAHM") + "");
					}
				}
			}
			dao.doSave("create", BSPHISEntryNames.MS_ZYYY_YYKS, saveMap, false);
		} catch (ValidateException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "预约失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "预约失败");
		} catch (ParseException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "预约失败");
		}
	}

	public void doUpdateInpatientCertificate(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> yyinfo = new HashMap<String, Object>();
		try {
			String brid = body.get("BRID") + "";
			yyinfo.put("BRID", Long.parseLong(brid));
			yyinfo.put("JGID", jgid);
			yyinfo.put("JGBZ", 0);
			long l = dao.doCount("ZY_YYBR","BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ", yyinfo);
			if (l <= 0) {
				return;
			}
			yyinfo.put("BZ", 2);
			dao.doUpdate("update ZY_YYBR set JGBZ=:BZ where BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ",yyinfo);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "取消预约失败");
		}
	}

	public void doQueryZDMC(Map<String, Object> body, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> zdmcpar = new HashMap<String, Object>();
		try {
			long jzxh = 0l;
			if (body.get("jzxh") != null) {
				jzxh = Long.parseLong(body.get("jzxh") + "");
			}
			zdmcpar.put("JZXH", jzxh);
			zdmcpar.put("ZZBZ", 1);
			zdmcpar.put("JGID", jgid);
			Map<String, Object> jzdmcMap = dao
					.doLoad("select ZDMC as ZDMC from MS_BRZD where JZXH=:JZXH and ZZBZ=:ZZBZ and JGID=:JGID",
							zdmcpar);
			if (jzdmcMap != null) {
				if (jzdmcMap.get("ZDMC") != null) {
					res.put("ZDMC", jzdmcMap.get("ZDMC") + "");
				} else {
					res.put("ZDMC", "");
				}
			} else {
				res.put("ZDMC", "");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取消预约失败");
		}
	}
}
