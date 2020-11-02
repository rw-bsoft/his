package phis.application.hos.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class HospitalAntibacterialModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalAntibacterialModel.class);

	public HospitalAntibacterialModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doSaveAntibactril(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			dao.doSave("update", BSPHISEntryNames.AMQC_SYSQ01 + "_SQFORM",
					body, false);
		} catch (ValidateException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("保存抗菌药物信息失败!", e);
		} catch (PersistentDataOperationException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("保存抗菌药物信息失败!", e);
		}
	}

	/**
	 * 获取已经审批的数量
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doCheckApplyInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			body.put("JZXH", body.get("JZXH").toString());
			body.put("YPXH", Long.parseLong(body.get("YPXH").toString()));
			Map<String, Object> map1 = dao
					.doLoad("select sum(SPYL) as SPYL from AMQC_SYSQ01 where JZLX=1 and JZXH=:JZXH and YPXH=:YPXH and (SPJG=1 or SPJG=2) and ZFBZ=0 and JGID=:JGID group by YPXH",
							body);
			body.put("JZXH", Long.parseLong(body.get("JZXH").toString()));
			Map<String, Object> map2 = dao
					.doLoad("select sum(YCSL) as SYSL from ZY_BQYZ where ZYH=:JZXH and YPXH=:YPXH and ZFBZ=0 and JGID=:JGID group by YPXH",
							body);
			Double spsl = 0.0;
			Double sysl = 0.0;
			if (map1 != null && map1.get("SPYL") != null
					&& map1.get("SPYL").toString().length() > 0) {
				spsl = Double.parseDouble(map1.get("SPYL").toString());
			}
			if (map2 != null && map2.get("SYSL") != null
					&& map2.get("SYSL").toString().length() > 0) {
				sysl = Double.parseDouble(map2.get("SYSL").toString());
			}
			res.put("SPSL", spsl - sysl > 0 ? spsl - sysl : 0);
			return res;
		} catch (PersistentDataOperationException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("保存抗菌药物信息失败!", e);
		}
	}

	/**
	 * 抗菌药物打印信息
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryList(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String id = req.get("body") + "";
		String jzlx = req.get("JZLX").toString();
		String entityName = jzlx.equals("6") ? "JC_BRRY":"ZY_BRRY";
		long SQDH = Long.parseLong(id);
		try {
			parameters.put("SQDH", SQDH);
			parameters.put("JZLX", jzlx);
			StringBuffer hql = new StringBuffer(
					"select a.BRKS as BRKS,a.JZXH as JZXH,a.BRBQ as BRBQ,b.ZYHM as ZYHM,b.BRXM as BRXM,d.PERSONNAME as ZRYS,b.JCZD as JCZD"
							+ "  ,b.BRXB as BRXB,b.CSNY as CSNY,b.RYNL as RYNL ,a.BRCH as BRCH,c.YPMC as YPMC,c.YFGG as YFGG,a.RZYL as RZYL,a.YYLC as YYLC "
							+ "  ,a.GRBQYZZ as GRBQYZZ,a.MYGNDX as MYGNDX,a.SQYWMG as SQYWMG,a.QTYY as QTYY,a.QTYYXS as QTYYXS,a.SQYS as SQYS"
							+ "  ,a.SQRQ as SQRQ, a.SPJG as SPJG,a.SPYL as SPYL,a.DJZT as DJZT,a.JYBGH as JYBGH,a.HZKS as HZKS "
							+ "  ,a.DJZT as DJZT,a.SQKZR as SQKZR,c.YFBZ as YFBZ,c.YPJL as YPJL,NVL(b.MQZD,' ') as MQZD "
							+ " from AMQC_SYSQ01 a, "+entityName+" b ,YK_TYPK c,SYS_Personnel d "
							+ " where  d.PERSONID=b.ZRYS and a.JZXH=b.ZYH and c.YPXH=a.YPXH and JZLX=:JZLX and a.SQDH=:SQDH");
			List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
			list_ret = dao.doSqlQuery(hql.toString(), parameters);
			Map<String, Object> ksmcMap = new HashMap<String, Object>();
			if (!"".equals(list_ret) && list_ret != null && list_ret.size() > 0) {
				ksmcMap = list_ret.get(0);
				if (ksmcMap != null && !"null".equals(ksmcMap)
						&& !"".equals(ksmcMap)) {
					String BRXB = ksmcMap.get("BRXB") + "";
					if ("1".equals(BRXB)) {
						ksmcMap.put("BRXB", "男");
					} else if ("2".equals(BRXB)) {
						ksmcMap.put("BRXB", "女");
					} else {
						ksmcMap.put("BRXB", "");
					}
					if(ksmcMap.get("BRCH") == null) {
						ksmcMap.put("BRCH", "");
					}
					if(jzlx.equals("6")){
						ksmcMap.put("AGE", ksmcMap.get("RYNL"));
					}else{
						String csrq = ksmcMap.get("CSNY") + "";
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						Date dt2 = sdf.parse(csrq);
						Date dt1 = new Date();// 当前日期
						// String t1 =(date+"").replace('-','/');
						// String t2 = csrq.replace('-','/');
						// Date dt1= new Date(t1);
						// Date dt2= new Date(t2);
						// long i= (dt1.getTime() - dt2.getTime())/(1000*60*60*24);
						// long AGE=i/365;
						Map<String, Object> m = BSPHISUtil.getPersonAge(dt2, dt1);
						String ageString = m.get("ages").toString();
						ksmcMap.put("AGE", ageString);
					}
					String rzyl = ksmcMap.get("RZYL") + "";
					String yylc = ksmcMap.get("YYLC") + "";
					if (!"".equals(rzyl) && !"".equals(yylc)
							&& !"null".equals(rzyl) && !"null".equals(yylc)) {
						double k1 = Double.parseDouble(rzyl);
						double k2 = Double.parseDouble(yylc);
						double HJYL = k1 * k2;
						String HJYLString = String.format("%.2f", HJYL);
						ksmcMap.put("HJYL", HJYLString);
					} else {
						ksmcMap.put("HJYL", "");
					}

					String GRBQYZZ = ksmcMap.get("GRBQYZZ") + "";
					if ("1".equals(GRBQYZZ)) {
						ksmcMap.put("GRBQYZZ", " ［√］ 感染病情严重者");
					} else {
						ksmcMap.put("GRBQYZZ", " ［］ 感染病情严重者");
					}
					String MYGNDX = ksmcMap.get("MYGNDX") + "";
					if ("1".equals(MYGNDX)) {
						ksmcMap.put("MYGNDX", " ［√］免疫功能低下伴感染者");
					} else {
						ksmcMap.put("MYGNDX", " ［］ 免疫功能低下伴感染者");
					}
					String SQYWMG = ksmcMap.get("SQYWMG") + "";
					if ("1".equals(SQYWMG)) {
						ksmcMap.put("SQYWMG",
								" ［√］使用限制、非限制级抗菌药物超72小时，并经微生物培养和药敏验证实对这二级抗菌药物耐药，对申请药物敏感.");
					} else {
						ksmcMap.put("SQYWMG",
								" ［］ 使用限制、非限制级抗菌药物超72小时，并经微生物培养和药敏验证实对这二级抗菌药物耐药，对申请药物敏感.");
					}
					String JYBGH = ksmcMap.get("JYBGH") + "";
					if ("".equals(JYBGH) || "null".equals(JYBGH)
							|| JYBGH == null) {
						ksmcMap.put("JYBGH", "  ");
					}
					String QTYY = ksmcMap.get("QTYY") + "";
					if ("1".equals(QTYY)) {
						ksmcMap.put("QTYY", " ［√］其它");
					} else {
						ksmcMap.put("QTYY", " ［］ 其它");
					}

					String HZKS = ksmcMap.get("HZKS") + "";// 主治医师ZSYS
					if (!"".equals(HZKS) && !"null".equals(HZKS)
							&& HZKS != null) {
						StringBuffer sql_zsys2 = new StringBuffer(
								"select a.KSMC as KSMC  from GY_KSDM a where a.KSDM=:HZKS");
						Map<String, Object> parameters_zsys2 = new HashMap<String, Object>();
						parameters_zsys2.put("HZKS", HZKS);
						List<Map<String, Object>> list_zsys2 = dao.doSqlQuery(
								sql_zsys2.toString(), parameters_zsys2);
						Map<String, Object> ksmcMap_zsys2 = new HashMap<String, Object>();
						if (!"".equals(list_zsys2) && list_zsys2 != null
								&& list_zsys2.size() > 0) {
							ksmcMap_zsys2 = list_zsys2.get(0);
							ksmcMap.put("HZKS", "［" + ksmcMap_zsys2.get("KSMC")
									+ "］");// 会诊科室
						} else {
							ksmcMap.put("HZKS", "___");// 会诊科室
						}
					} else {
						ksmcMap.put("HZKS", "___");// 会诊科室
					}

					String QTYYXS = ksmcMap.get("QTYYXS") + "";
					if (!"".equals(QTYYXS) && !"null".equals(QTYYXS)) {
						ksmcMap.put("QTYYXS", "其他原因：" + QTYYXS);
					} else {
						ksmcMap.put("QTYYXS", "");
					}
					String DJZT = ksmcMap.get("DJZT") + "";
					if ("0".equals(DJZT)) {
						ksmcMap.put("DJZT", "新增");
					} else if ("1".equals(DJZT)) {
						ksmcMap.put("DJZT", "提交");
					} else if ("2".equals(DJZT)) {
						ksmcMap.put("DJZT", "科主任审批完成");
					} else if ("3".equals(DJZT)) {
						ksmcMap.put("DJZT", "专家组审批完成");
					} else if ("4".equals(DJZT)) {
						ksmcMap.put("DJZT", "医务科审批完成");
					} else {
						ksmcMap.put("DJZT", "");
					}

					String SQKZR = ksmcMap.get("SQKZR") + "";// 科主任审核
					if ("1".equals(SQKZR)) {
						ksmcMap.put("SQKZR", "已审核");
					} else {
						ksmcMap.put("SQKZR", "");
					}

					String SPYL = ksmcMap.get("SPYL") + "";// 审批用量
					if (!"".equals(SPYL) && !"null".equals(SPYL)
							&& SPYL != null) {
						double k = Double.parseDouble(SPYL + "");
						String kString = String.format("%.2f", k);
						ksmcMap.put("SPYL", kString);
					} else {
						ksmcMap.put("SPYL", "");
					}

					String YFGG = ksmcMap.get("YFGG") + "";// 审批用量
					if (!"".equals(YFGG) && !"null".equals(YFGG)
							&& YFGG != null) {
						ksmcMap.put("YFGG", YFGG);
					} else {
						ksmcMap.put("YFGG", "");
					}

					String SPJG = ksmcMap.get("SPJG") + "";// 审批结果
					if ("1".equals(SPJG)) {
						ksmcMap.put("status", "已审核 ");
						ksmcMap.put("SPJG", "［√］同意 ［］不同意 ");
					} else if ("9".equals(SPJG)) {
						ksmcMap.put("status", "已提交 ");
						ksmcMap.put("SPJG", "［］同意 ［√］不同意 ");
					} else {
						ksmcMap.put("status", "已提交 ");
						ksmcMap.put("SPJG", "［］同意 ［］不同意 ");
					}
					String SQYS = ksmcMap.get("SQYS") + "";// 申请医师
					if (!"".equals(SQYS) && !"null".equals(SQYS)
							&& SQYS != null) {
						StringBuffer sql = new StringBuffer(
								"select a.YGXM as SQYS  from GY_YGDM a where a.YGDM=:SQYS");
						Map<String, Object> parameters_sql = new HashMap<String, Object>();
						parameters_sql.put("SQYS", SQYS);
						List<Map<String, Object>> list_sql = dao.doSqlQuery(
								sql.toString(), parameters_sql);
						Map<String, Object> ksmcMap_sql = new HashMap<String, Object>();
						if (list_sql.size() > 0) {
							ksmcMap_sql = list_sql.get(0);
							ksmcMap.put("SQYS", ksmcMap_sql.get("SQYS") + "");// 申请医师
						} else {
							ksmcMap.put("SQYS", "");// 申请医师
						}
					} else {
						ksmcMap.put("SQYS", "");// 申请医师
					}

					// 每日用量
					String MRYL = ksmcMap.get("RZYL") + "";// 日用量
					String YFBZ = ksmcMap.get("YFBZ") + "";// 药房包装
					String YPJL = ksmcMap.get("YPJL") + "";// 剂量
					if (!"".equals(MRYL) && !"null".equals(MRYL)
							&& MRYL != null) {
						double k = Double.parseDouble(MRYL + "");
						String RZYLString = String.format("%.2f", k);
						ksmcMap.put("RZYL", RZYLString);// 每日用量
					}
					if (!"".equals(YPJL) && !"null".equals(YPJL)
							&& YPJL != null) {
						double k = Double.parseDouble(YPJL + "");
						String YPJLString = String.format("%.2f", k);
						ksmcMap.put("YPJL", YPJLString);
					}
					if (!"".equals(MRYL) && !"null".equals(MRYL)
							&& MRYL != null && !"".equals(YFBZ)
							&& !"null".equals(YFBZ) && YFBZ != null
							&& !"".equals(YPJL) && !"null".equals(YPJL)
							&& YPJL != null) {
						// Double.parseDouble(body.get("height")+"")
						double k1 = Double.parseDouble(MRYL + "");
						double k2 = Double.parseDouble(YFBZ);
						double k3 = Double.parseDouble(YPJL);
						double k = k1 / k2 / k3;
						String kString = String.format("%.2f", k);
						ksmcMap.put("MRYL", kString);
					} else {
						ksmcMap.put("MRYL", "");// 每日用量
					}

					// 病区获取
					String BRBQ = ksmcMap.get("BRBQ") + "";
					if (!"".equals(BRBQ) && !"null".equals(BRBQ)
							&& BRBQ != null) {
						Map<String, Object> pam = new HashMap<String, Object>();
						pam.put("BRBQ", BRBQ);
						String sb = " select OFFICENAME from SYS_Office where  id=:BRBQ";
						List<Map<String, Object>> rsMap = dao.doSqlQuery(
								sb.toString(), pam);
						if (!"".equals(rsMap) && rsMap != null
								&& rsMap.size() > 0) {
							String BQMC = rsMap.get(0) + "";
							ksmcMap.put("BRBQ", BQMC);// 病区
						} else {
							ksmcMap.put("BRBQ", "");// 病区
						}
					} else {
						ksmcMap.put("BRBQ", "");// 病区
					}

					/* 几个无法取到的值 */
					// d.OFFICENAME as BRKS, SYS_Office d and a.BRKS = d.id
					// e.ZSYS as ZSYS, ZY_BRRY e and b.ZYH = e.ZYH

					String ZYH = ksmcMap.get("JZXH") + "";// 主治医师ZSYS
					if (!"".equals(ZYH) && !"null".equals(ZYH) && ZYH != null) {
						StringBuffer sql_zsys = new StringBuffer(
								"select b.YGXM as ZSYS  from ZY_BRRY a,GY_YGDM b where a.ZSYS=b.YGDM and a.ZYH=:ZYH  ");
						Map<String, Object> parameters_zsys = new HashMap<String, Object>();
						parameters_zsys.put("ZYH", ZYH);
						List<Map<String, Object>> list_zsys = dao.doSqlQuery(
								sql_zsys.toString(), parameters_zsys);
						Map<String, Object> ksmcMap_zsys = new HashMap<String, Object>();
						if (!"".equals(list_zsys) && list_zsys != null
								&& list_zsys.size() > 0) {
							ksmcMap_zsys = list_zsys.get(0);
							ksmcMap.put("ZSYS", ksmcMap_zsys.get("ZSYS") + "");// 主治医师
						} else {
							ksmcMap.put("ZSYS", "");// 主治医师
						}
					} else {
						ksmcMap.put("ZSYS", "");// 主治医师
					}
					// 获取病人科室
					String BRKS = ksmcMap.get("BRKS") + "";
					if (!"".equals(BRKS) && !"null".equals(BRKS)
							&& BRKS != null) {
						Map<String, Object> pam_ks = new HashMap<String, Object>();
						pam_ks.put("BRKS", BRKS);
						String sb = " select OFFICENAME from SYS_Office where  id=:BRKS";
						List<Map<String, Object>> rsMap = dao.doSqlQuery(
								sb.toString(), pam_ks);
						if (!"".equals(rsMap) && rsMap != null
								&& rsMap.size() > 0) {
							String KSMC = rsMap.get(0) + "";
							ksmcMap.put("BRKS", KSMC);// 病区
						} else {
							ksmcMap.put("BRKS", "");// 病区
						}
					} else {
						ksmcMap.put("BRKS", "");// 病区
					}

				}
			}
			res.put("body", ksmcMap);
		} catch (PersistentDataOperationException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("抗菌药物打印信息失败!", e);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("抗菌药物打印信息失败!", e);
		}
	}
}
