package phis.application.pacs.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class JckdModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(JckdModel.class);

	public JckdModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 申请单查询服务
	 * 
	 * @author caijy
	 * @createDate 2017-3-7
	 * @description
	 * @updateInfo
	 * @param xml
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public String exmrequestquery(String xml, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new LinkedHashMap<String, Object>();// 返回给检查的数据
		Map<String, Object> map_inXml;
		try {
			// 检查传过来的xml转成Map
			map_inXml = BSPHISUtil.xml2map_jc(xml);
		} catch (Exception e) {
			// 出现异常,返回错误响应
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "xml格式解析错误:" + e.getMessage()));
			return getReturnXml(map_ret);
		}
		List<Map<String, Object>> list_jcds = new ArrayList<Map<String, Object>>();// 所有满足条件的病人检查单数据
		// 判断下VISIT标签是否为空
		if (!map_inXml.containsKey("VISIT") || map_inXml.get("VISIT") == null
				|| !(map_inXml.get("VISIT") instanceof Map)) {
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "VISIT标签为空或者数据错误"));
			return getReturnXml(map_ret);
		}
		Map<String, Object> map_visit = (Map<String, Object>) map_inXml
				.get("VISIT");
		// 判断下EXMREQUEST标签是否为空
		if (!map_inXml.containsKey("EXMREQUEST")
				|| map_inXml.get("EXMREQUEST") == null
				|| !(map_inXml.get("EXMREQUEST") instanceof Map)) {
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "EXMREQUEST标签为空或者数据错误"));
			return getReturnXml(map_ret);
		}
		Map<String, Object> map_exmrequest = (Map<String, Object>) map_inXml
				.get("EXMREQUEST");
		// 以下2个参数是必传数据
		String jgid = MedicineUtils.parseString(map_visit
				.get("AUTHORORGANIZATION"));// 机构代码
		String jcdl = MedicineUtils.parseString(map_exmrequest
				.get("EXAMTYPECODE"));// 检查大类,跟GY_YLSF的JCDL对应
		if ("".equals(jgid) || "".equals(jcdl)) {
			// 数据不完整,返回异常
			map_ret.put(
					"MSGHEADER",
					getHeadMessage(false, 0,
							"AUTHORORGANIZATION或者EXAMTYPECODE为空,AUTHORORGANIZATION:"
									+ jgid + ",EXAMTYPECODE:" + jcdl));
			return getReturnXml(map_ret);
		}
		String patientType = MedicineUtils.parseString(map_visit
				.get("PATIENTTYPE"));// 患者类型,空是全部,01门诊,02急诊,03体检,04住院
		if ("".equals(patientType) || "01".equals(patientType)) {// 查询门诊检查单
			try {
				getMzJcds(map_visit, map_exmrequest, list_jcds);
			} catch (Exception e) {
				map_ret.put("MSGHEADER",
						getHeadMessage(false, 0, "数据查询失败:" + e.getMessage()));
				return getReturnXml(map_ret);
			}
		}
		if ("".equals(patientType) || "04".equals(patientType)) {// 查询住院检查单
			try {
				getZyJcds(map_visit, map_exmrequest, list_jcds);
			} catch (Exception e) {
				map_ret.put("MSGHEADER",
						getHeadMessage(false, 0, "数据查询失败:" + e.getMessage()));
				return getReturnXml(map_ret);
			}
		}
		map_ret.put("MSGHEADER", getHeadMessage(true, list_jcds.size(), ""));
		map_ret.put("EXMREQUESTS", list_jcds);
		return getReturnXml(map_ret);

	}

	/**
	 * 查询门诊检查单
	 * 
	 * @author caijy
	 * @createDate 2017-3-10
	 * @description
	 * @updateInfo
	 * @param map_inXml
	 *            条件map
	 * @param list_jcds
	 *            检查单集合
	 * @throws Exception
	 */
	public void getMzJcds(Map<String, Object> map_visit,
			Map<String, Object> map_exmrequest,
			List<Map<String, Object>> list_jcds) throws Exception {
		long jzxh = MedicineUtils.parseLong(map_visit.get("VISITID"));// 就诊序号,MS_YJ01中JZXH字段,ys_mz_jzls表主键
		String jgid = MedicineUtils.parseString(map_visit
				.get("AUTHORORGANIZATION"));// 机构代码
		String mzhm = MedicineUtils.parseString(map_visit.get("CLINICID"));// 门诊号,MS_BRDA字段
		long sqid = MedicineUtils.parseLong(map_exmrequest.get("REQUESTID"));// 申请id,ns_yj01中字段
		String jcdl = MedicineUtils.parseString(map_exmrequest
				.get("EXAMTYPECODE"));// 检查大类,01.超声类 02.放射类 03.内镜 ,
										// 表GY_YLSF中JCDL字段
		String s_sqsj_b = MedicineUtils.parseString(map_exmrequest
				.get("REQUESTDATETIME_B"));// 开单时间起始
		String s_sqsj_e = MedicineUtils.parseString(map_exmrequest
				.get("REQUESTDATETIME_E"));// 开单时间截至
		String jfbz = MedicineUtils
				.parseString(map_exmrequest.get("FEESTATUS"));// 计费标志,0.未计费
																// 1.已计费(默认)
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		// 查询满足条件的所有医技主键(明细里面如果有不同检查大类的明细 过滤掉)
		StringBuffer hql_yjxhs = new StringBuffer();
		hql_yjxhs
				.append("select distinct a.YJXH as YJXH from MS_YJ01 a,MS_YJ02 b,GY_YLSF c,MS_BRDA d ");
		hql_yjxhs
				.append("where a.YJXH=b.YJXH and b.YLXH=c.FYXH and a.BRID=d.BRID and a.JGID=:jgid and c.JCDL=:jcdl and a.ZXPB!=1 and (c.XMLX=11 or c.XMLX=5) ");
		hql_yjxhs
				.append(" and a.YJXH not in (select distinct x.YJXH from MS_YJ01 x, MS_YJ02 y, GY_YLSF o, MS_YJ02 z, GY_YLSF p where x.YJXH = y.YJXH and x.YJXH = z.YJXH  and z.SBXH != y.SBXH and y.YLXH = o.FYXH and z.YLXH = p.FYXH and ((o.XMLX = 11 and p.XMLX =11) or (o.XMLX = 5 and p.XMLX =5)) and (p.JCDL != o.JCDL or p.JCDL is null or o.JCDL is null))");
		Map<String, Object> map_par_yjxhs = new HashMap<String, Object>();
		map_par_yjxhs.put("jgid", jgid);
		map_par_yjxhs.put("jcdl", jcdl);
		if (jzxh != 0) {// 如果包含就诊号
			hql_yjxhs.append(" and a.JZXH=:jzxh");
			map_par_yjxhs.put("jzxh", jzxh);
		}
		if (!"".equals(mzhm)) {// 包含门诊号码
			hql_yjxhs.append(" and d.MZHM=:mzhm");
			map_par_yjxhs.put("mzhm", mzhm);
		}
		if (sqid != 0) {// 申请id
			hql_yjxhs.append(" and a.SQID=:sqid");
			map_par_yjxhs.put("sqid", sqid);
		}
		if (!"".equals(s_sqsj_b)) {// 开单开始时间
			hql_yjxhs.append(" and a.KDRQ>=:kdrq_b");
			map_par_yjxhs.put("kdrq_b", sdf.parse(s_sqsj_b));
		}
		if (!"".equals(s_sqsj_e)) {// 开单结束时间
			hql_yjxhs.append(" and a.KDRQ<=:kdrq_e");
			map_par_yjxhs.put("kdrq_e", sdf.parse(s_sqsj_e));
		}
		if (!"".equals(jfbz)) {
			if ("0".equals(jfbz)) {
				hql_yjxhs.append(" and a.FPHM is null");
			} else {
				hql_yjxhs.append(" and a.FPHM is not null");
			}
		}
		List<Map<String, Object>> list_yjs = dao.doSqlQuery(
				hql_yjxhs.toString(), map_par_yjxhs);
		if (list_yjs != null && list_yjs.size() != 0) {
			List<Long> l_yjs = new ArrayList<Long>();
			for (Map<String, Object> map_yj : list_yjs) {
				l_yjs.add(MedicineUtils.parseLong(map_yj.get("YJXH")));
			}
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("yjxhs", l_yjs);
			// 查询病人和yj01信息
			StringBuffer hql_brda = new StringBuffer();
			hql_brda.append("select a.YJXH as YJXH,b.BRID as BRID,a.JGID as JGID,SFZH as SFZH ");
			hql_brda.append(",b.BRXM as BRXM,CSNY as CSNY,BRXB as BRXB,LXDH as LXDH,a.JZXH as JZXH,b.MZHM as MZHM ");
			hql_brda.append(",a.SQID as SQID,c.ICD10 as ICD10,c.ZDMC as ZDMC,a.FPHM as FPHM");
			hql_brda.append(",d.ZSXX as ZSXX,XBS as XBS,a.YSDM as YSDM ,to_char(a.KDRQ,'yyyymmddhh24miss') as KDRQ,KSDM as KSDM,a.ZFPB as ZFPB,e.PERSONNAME as YSXM,f.OFFICENAME as KSMC");
			hql_brda.append(" from SYS_Personnel e,SYS_Office f,MS_BRDA b,MS_YJ01 a left outer join MS_BRZD c on a.JZXH=c.JZXH left outer join MS_BCJL d on a.JZXH=d.JZXH");
			hql_brda.append("  where e.PERSONID=a.YSDM and f.ID=a.KSDM and a.BRID=b.BRID and a.YJXH in (:yjxhs) order by a.KDRQ desc");
			List<Map<String, Object>> list_brda = dao.doSqlQuery(
					hql_brda.toString(), map_par);// 所有满足条件的病人信息
			// 查询明细数据
			StringBuffer hql_yj02 = new StringBuffer();
			/****************** modify by zhaojian 2017-06-05 检查开单功能改造，取消在处置里开单，改为使用专门开单页面进行开单 *********************/
			// hql_yj02.append("select a.YJXH as YJXH ,b.YLXH as YLXH,c.FYMC as FYMC,b.YLDJ as YLDJ,b.SBXH as SBXH,b.JCBWDM as BWDM,JCBWMS as BWMS");
			// hql_yj02.append(" from MS_YJ01 a,MS_YJ02 b,GY_YLSF c where a.YJXH=b.YJXH and b.YLXH=c.FYXH and a.YJXH in (:yjxhs)");
			hql_yj02.append("select a.YJXH as YJXH ,b.YLXH as YLXH,c.FYMC as FYMC,b.YLDJ as YLDJ,b.SBXH as SBXH,b.JCBWDM as BWDM,b.JCBWMC AS BWMC,b.JCBWMS as BWMS");
			hql_yj02.append(" from MS_YJ01 a,MS_YJ02 b,GY_YLSF c");
			hql_yj02.append(" where a.YJXH=b.YJXH and b.YLXH=c.FYXH and a.YJXH in (:yjxhs)");
			List<Map<String, Object>> list_yjmx = dao.doSqlQuery(
					hql_yj02.toString(), map_par);
			if (list_brda != null && list_brda.size() > 0) {
				for (Map<String, Object> map_brda : list_brda) {
					Map<String, Object> map_ExmRequests = new HashMap<String, Object>();
					map_ExmRequests.put("PATIENT", getPatient(map_brda, "OV"));
					map_ExmRequests.put("VISIT", getVisit_mz(map_brda));
					map_ExmRequests.put("EXMREQUEST",
							getExmRequest(map_brda, list_yjmx));
					list_jcds.add(map_ExmRequests);
				}
			}
		}

	}

	/**
	 * 住院检查单
	 * 
	 * @author caijy
	 * @createDate 2017-3-11
	 * @description
	 * @updateInfo
	 * @param map_visit
	 * @param map_exmrequest
	 * @param list_jcds
	 * @throws Exception
	 */
	public void getZyJcds(Map<String, Object> map_visit,
			Map<String, Object> map_exmrequest,
			List<Map<String, Object>> list_jcds) throws Exception {
		// long
		// jzxh=MedicineUtils.parseLong(map_visit.get("VISITID"));//就诊序号,住院里面不知道有什么用
		String jgid = MedicineUtils.parseString(map_visit
				.get("AUTHORORGANIZATION"));// 机构代码
		String zyh = MedicineUtils.parseString(map_visit.get("HOSPIZATIONID"));// 住院号
		long sqdh = MedicineUtils.parseLong(map_exmrequest.get("REQUESTID"));// 申请单号,ns_yj01中字段
		String jcdl = MedicineUtils.parseString(map_exmrequest
				.get("EXAMTYPECODE"));// 检查大类,01.超声类 02.放射类 03.内镜 ,
										// 表GY_YLSF中JCDL字段
		String s_sqsj_b = MedicineUtils.parseString(map_exmrequest
				.get("REQUESTDATETIME_B"));// 开单时间起始
		String s_sqsj_e = MedicineUtils.parseString(map_exmrequest
				.get("REQUESTDATETIME_E"));// 开单时间截至
		// String
		// jfbz=MedicineUtils.parseString(map_exmrequest.get("FEESTATUS"));//计费标志,0.未计费
		// 1.已计费(默认) 住院是先执行再计费的 所以该字段没啥意义
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		// 查询满足条件的所有医技主键(明细里面如果有不同检查大类的明细 过滤掉)
		StringBuffer hql_yjxhs = new StringBuffer();
		hql_yjxhs
				.append("select distinct a.YJXH as YJXH from YJ_ZY01 a,YJ_ZY02 b,GY_YLSF c,ZY_BRRY d ");
		hql_yjxhs
				.append("where a.YJXH=b.YJXH and b.YLXH=c.FYXH and a.ZYH=d.ZYH and a.JGID=:jgid and c.JCDL=:jcdl  and ZXPB!=1 and (c.XMLX=11 or c.XMLX=5)");
		hql_yjxhs
				.append(" and a.YJXH not in (select distinct x.YJXH from MS_YJ01 x, MS_YJ02 y, GY_YLSF o, MS_YJ02 z, GY_YLSF p where x.YJXH = y.YJXH and x.YJXH = z.YJXH  and z.SBXH != y.SBXH and y.YLXH = o.FYXH and z.YLXH = p.FYXH and ((o.XMLX = 11 and p.XMLX =11) or (o.XMLX = 5 and p.XMLX =5)) and (p.JCDL != o.JCDL or p.JCDL is null or o.JCDL is null))");
		Map<String, Object> map_par_yjxhs = new HashMap<String, Object>();
		map_par_yjxhs.put("jgid", jgid);
		map_par_yjxhs.put("jcdl", jcdl);
		if (!"".equals(zyh)) {// 包含住院号
			hql_yjxhs.append(" and d.ZYH=:zyh");
			map_par_yjxhs.put("zyh", zyh);
		}
		if (sqdh != 0) {// 申请id
			hql_yjxhs.append(" and a.SQDH=:sqdh");
			map_par_yjxhs.put("sqdh", sqdh);
		}
		if (!"".equals(s_sqsj_b)) {// 开单开始时间
			hql_yjxhs.append(" and a.TJSJ>=:kdrq_b");
			map_par_yjxhs.put("kdrq_b", sdf.parse(s_sqsj_b));
		}
		if (!"".equals(s_sqsj_e)) {// 开单结束时间
			hql_yjxhs.append(" and a.TJSJ<=:kdrq_e");
			map_par_yjxhs.put("kdrq_e", sdf.parse(s_sqsj_e));
		}
		List<Map<String, Object>> list_yjs = dao.doSqlQuery(
				hql_yjxhs.toString(), map_par_yjxhs);
		if (list_yjs != null && list_yjs.size() != 0) {
			List<Long> l_yjs = new ArrayList<Long>();
			for (Map<String, Object> map_yj : list_yjs) {
				l_yjs.add(MedicineUtils.parseLong(map_yj.get("YJXH")));
			}
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("yjxhs", l_yjs);
			// 查询病人和yj01信息
			StringBuffer hql_brda = new StringBuffer();
			hql_brda.append("select a.ZYH as ZYH,a.YJXH as YJXH,b.BRID as BRID,a.JGID as JGID,b.SFZH as SFZH ");
			hql_brda.append(",b.BRXM as BRXM,b.CSNY as CSNY,b.BRXB as BRXB,b.LXDH as LXDH ,b.BRCH as BRCH");
			hql_brda.append(",a.SQDH as SQID,b.RYZD as ZDMC,");
			hql_brda.append("a.YSDM as YSDM ,to_char(a.TJSJ,'yyyymmddhh24miss') as KDRQ,a.KSDM as KSDM,a.ZFPB as ZFPB,e.PERSONNAME as YSXM,f.OFFICENAME as KSMC");
			hql_brda.append(" from SYS_Personnel e,SYS_Office f,ZY_BRRY b,YJ_ZY01 a ");
			hql_brda.append("  where  a.ZYH=b.ZYH and a.YJXH in (:yjxhs) and a.YSDM=e.PERSONID and f.ID=a.KSDM order by a.KDRQ desc");
			List<Map<String, Object>> list_brda = dao.doSqlQuery(
					hql_brda.toString(), map_par);// 所有满足条件的病人信息
			// 查询明细数据
			StringBuffer hql_yj02 = new StringBuffer();
			/****************** modify by zhaojian 2017-06-07 检查开单功能改造，取消在处置里开单，改为使用专门开单页面进行开单 *********************/
			//hql_yj02.append("select a.YJXH as YJXH ,b.YLXH as YLXH,c.FYMC as FYMC,b.YLDJ as YLDJ,b.SBXH as SBXH,b.JCBWDM as JCBWDM,b.JCBWMS as JCBWMS");
			//hql_yj02.append(" from YJ_ZY01 a,YJ_ZY02 b,GY_YLSF c where a.YJXH=b.YJXH and b.YLXH=c.FYXH and a.YJXH in (:yjxhs)");
			hql_yj02.append("select a.YJXH as YJXH ,b.YLXH as YLXH,c.FYMC as FYMC,b.YLDJ as YLDJ,b.SBXH as SBXH,d.JCBWDM as BWDM,d.JCBWMC as BWMC,'' as BWMS");
			hql_yj02.append(" from YJ_ZY01 a,YJ_ZY02 b,GY_YLSF c,ZY_BQYZ d where a.YJXH=b.YJXH and b.YLXH=c.FYXH and b.YZXH=d.JLXH and a.YJXH in (:yjxhs)");
			List<Map<String, Object>> list_yjmx = dao.doSqlQuery(
					hql_yj02.toString(), map_par);
			if (list_brda != null && list_brda.size() > 0) {
				for (Map<String, Object> map_brda : list_brda) {
					Map<String, Object> map_ExmRequests = new LinkedHashMap<String, Object>();
					map_ExmRequests.put("PATIENT", getPatient(map_brda, "IV"));
					map_ExmRequests.put("VISIT", getVisit_zy(map_brda));
					map_ExmRequests.put("EXMREQUEST",
							getExmRequest(map_brda, list_yjmx));
					list_jcds.add(map_ExmRequests);
				}
			}
		}

	}

	/**
	 * 拼接Patient标签
	 * 
	 * @author caijy
	 * @createDate 2017-3-10
	 * @description
	 * @updateInfo
	 * @param map_brda
	 * @return
	 */
	public Map<String, Object> getPatient(Map<String, Object> map_brda,
			String tag) {
		Map<String, Object> map_ret = new LinkedHashMap<String, Object>();
		map_ret.put("SOURCEPATIENTID",
				MedicineUtils.parseString(map_brda.get("BRID")));// 患者档案号
		map_ret.put("SOURCEPATIENTIDTYPE", tag);// 患者档案号类型(业务系统)
		map_ret.put("EXAMEXECUTEORG",
				MedicineUtils.parseString(map_brda.get("JGID")));// 检查机构代码
		map_ret.put("IDCARD", MedicineUtils.parseString(map_brda.get("SFZH")));// 身份证件号码
		map_ret.put("IDCARDCODE", "01");// 身份证件类别代码
		map_ret.put("NAME", MedicineUtils.parseString(map_brda.get("BRXM")));// 患者姓名
		map_ret.put("BIRTHDATE",
				MedicineUtils.parseString(map_brda.get("CSNY")));// 出生日期
		map_ret.put("SEX", MedicineUtils.parseString(map_brda.get("BRXB")));// 性别代码
		map_ret.put("PATIENTPHONE",
				MedicineUtils.parseString(map_brda.get("LXDH")));// 患者电话号码
		return map_ret;
	}

	/**
	 * 拼接Visit标签(门诊)
	 * 
	 * @author caijy
	 * @createDate 2017-3-10
	 * @description
	 * @updateInfo
	 * @param map_brda
	 * @return
	 */
	public Map<String, Object> getVisit_mz(Map<String, Object> map_brda) {
		Map<String, Object> map_ret = new LinkedHashMap<String, Object>();
		map_ret.put("VISITID", MedicineUtils.parseString(map_brda.get("JZXH")));// 就诊流水号
		map_ret.put("PATIENTTYPE", "01");// 患者类型
		map_ret.put("CLINICID", MedicineUtils.parseString(map_brda.get("MZHM")));// 门（急）诊号
		map_ret.put("HOSPIZATIONID", "");// 住院号
		return map_ret;
	}

	/**
	 * 拼接Visit标签(住院)
	 * 
	 * @author caijy
	 * @createDate 2017-3-10
	 * @description
	 * @updateInfo
	 * @param map_brda
	 * @return
	 */
	public Map<String, Object> getVisit_zy(Map<String, Object> map_brda) {
		Map<String, Object> map_ret = new LinkedHashMap<String, Object>();
		map_ret.put("VISITID", MedicineUtils.parseString(map_brda.get("ZYH")));// 就诊流水号
		map_ret.put("PATIENTTYPE", "04");// 患者类型
		map_ret.put("CLINICID", "");// 门（急）诊号
		map_ret.put("HOSPIZATIONID",
				MedicineUtils.parseString(map_brda.get("ZYH")));// 住院号
		return map_ret;
	}

	/**
	 * 拼接exmRequest标签
	 * 
	 * @author caijy
	 * @createDate 2017-3-10
	 * @description
	 * @updateInfo
	 * @param map_brda
	 * @param list_yjmx
	 * @return
	 */
	public Map<String, Object> getExmRequest(Map<String, Object> map_brda,
			List<Map<String, Object>> list_yjmx) {
		Map<String, Object> map_ret = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> list_Items = MedicineUtils.getListRecord(
				list_yjmx, new String[] { "YJXH" }, map_brda,
				new String[] { "YJXH" });
		// String EXAMTYPECODE ="023";
		// String EXAMTYPENAME="MRI";
		StringBuffer EXAMTEXT = new StringBuffer();
		if (list_Items != null && list_Items.size() > 0) {
			for (int i = 0; i < list_Items.size(); i++) {
				// EXAMTYPECODE=MedicineUtils.parseString(list_Items.get(i).get("JCXL"));
				// EXAMTYPENAME="MRI";//字典暂时先空着
				// if(i>0){
				// EXAMTEXT.append(",");
				// }
				EXAMTEXT.append(MedicineUtils.parseString(list_Items.get(i)
						.get("FYMC")));
				if (i != (list_Items.size() - 1))
					EXAMTEXT.append(",");
			}
		}
		//modify by zhaojian 2017-06-19 修改申请单号为医技序号
		/*map_ret.put("REQUESTID",
				MedicineUtils.parseString(map_brda.get("SQID")));// 检查申请单编号
*/
		map_ret.put("REQUESTID",
				MedicineUtils.parseString(map_brda.get("YJXH")));
		map_ret.put("ISNEONATUS", 0);// 婴儿判别
		int age = 0;
		int monthage = 0;
		if (map_brda.get("CSNY") != null && !"".equals(map_brda.get("CSNY"))) {
			Map<String, Object> map_nl = BSPHISUtil.getPersonAge(
					(Date) map_brda.get("CSNY"), new Date());
			if (map_nl != null && map_nl.size() > 0
					&& map_nl.containsKey("ages")) {
				String ages = MedicineUtils.parseString(map_nl.get("ages"));
				if (ages.indexOf("岁") > -1) {
					age = MedicineUtils.parseInt(ages.substring(0,
							ages.indexOf("岁")));
				}
				if (ages.indexOf("月") > -1) {
					monthage = MedicineUtils.parseInt(ages.substring(
							ages.indexOf("岁") > -1 ? ages.indexOf("岁") + 1 : 0,
							ages.indexOf("月")));

				}
			}

		}
		map_ret.put("AGE", age);// 年龄(岁)
		map_ret.put("MONTHAGE", monthage);// 年龄(月)
		map_ret.put("DEPT", MedicineUtils.parseString(map_brda.get("KSDM")));// 患者科室
		map_ret.put("DEPTNAME", MedicineUtils.parseString(map_brda.get("KSMC")));// 患者科室名称
		map_ret.put("WARDAREA", MedicineUtils.parseString(map_brda.get("KSDM")));// 患者病区
		map_ret.put("WARDAREANAME",
				MedicineUtils.parseString(map_brda.get("KSMC")));// 患者病区名称
		map_ret.put("SICKBEDID",
				MedicineUtils.parseString(map_brda.get("BRCH")));// 病床号
		map_ret.put("DIAGNOSECODE",
				MedicineUtils.parseString(map_brda.get("ICD10")));// 疾病诊断编码
		map_ret.put("DIAGNOSENAME",
				MedicineUtils.parseString(map_brda.get("ZDMC")));// 疾病诊断名称
		map_ret.put("CHIEFCOMPLAINT",
				MedicineUtils.parseString(map_brda.get("ZSXX")));// 主诉
		map_ret.put("DISEASESHISTORY",
				MedicineUtils.parseString(map_brda.get("XBS")));// 简要病史
		map_ret.put("PHYSICALSIGN", "");// 症状与体征
		map_ret.put("REFLABRESULT", "");// 相关检验结果
		map_ret.put("EXAMPURPOSE", "");// 检查目的
		map_ret.put("ISEMERGENCY", 0);// 紧急标志
		map_ret.put("REQUESTDOCTOR",
				MedicineUtils.parseString(map_brda.get("YSDM")));// 申请医师代码
		map_ret.put("REQUESTDOCTORNAME",
				MedicineUtils.parseString(map_brda.get("YSXM")));// 申请医师代码
		map_ret.put("REQUESTDATETIME",
				MedicineUtils.parseString(map_brda.get("KDRQ")));// 申请时间
		map_ret.put("REQUESTDEPT",
				MedicineUtils.parseString(map_brda.get("KSDM")));// 申请科室代码
		map_ret.put("REQUESTDEPTNAME",
				MedicineUtils.parseString(map_brda.get("KSMC")));// 申请科室代码名称
		map_ret.put("EXAMTYPECODE", "");// 检查类别小类
		map_ret.put("EXAMTYPENAME", "");// 检查类别名称
		map_ret.put("EXAMTEXT", EXAMTEXT.toString());// 检查组合项目名称
		map_ret.put("REMARKINFO", "");// 备注
		map_ret.put("EFFECTIVEFLAG",
				MedicineUtils.parseString(map_brda.get("ZFPB")));// 有效标志
		List<Map<String, Object>> l_items = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map_items : list_Items) {// 拼接item数据
			Map<String, Object> m_item = new LinkedHashMap<String, Object>();
			m_item.put("ITEMCODE",
					MedicineUtils.parseString(map_items.get("YLXH")));// 检查项目代码
			m_item.put("EXAMPARTSTEXT",
					MedicineUtils.parseString(map_items.get("FYMC")));// 检查项目全称
			m_item.put("ITEMCOST",
					MedicineUtils.parseString(map_items.get("YLDJ")));// 项目费用
			m_item.put("FEESTATUS", !"".equals(MedicineUtils
					.parseString(map_brda.get("FPHM"))) ? "1" : "0");// 计费标志
			m_item.put("INVOICENO",
					MedicineUtils.parseString(map_brda.get("FPHM")));// (门诊)发票号码
			m_item.put("ORDERID",
					MedicineUtils.parseString(map_items.get("SBXH")));// 医生医嘱流水号
			Map<String, Object> map_Part = new HashMap<String, Object>();

			/****************** modify by zhaojian 2017-06-05 检查开单功能改造，取消在处置里开单，改为使用专门开单页面进行开单 *********************/
			/*
			 * map_Part.put("PARTSCODE",
			 * MedicineUtils.parseString(map_items.get("JCBWDM")));//检查部位代码
			 * map_Part.put("PARTSNAME", "");//检查部位名称 map_Part.put("PARTSDESC",
			 * MedicineUtils.parseString(map_items.get("JCBWMS")));//检查部位描述
			 */map_Part.put("PARTSCODE",
					MedicineUtils.parseString(map_items.get("BWDM")));// 检查部位代码
			map_Part.put("PARTSNAME",
					MedicineUtils.parseString(map_items.get("BWMC")));// 检查部位名称
			map_Part.put("PARTSDESC",
					MedicineUtils.parseString(map_items.get("BWMS")));// 检查部位描述

			m_item.put("PART", map_Part);
			l_items.add(m_item);
		}
		map_ret.put("ITEM", l_items);
		return map_ret;
	}

	/**
	 * 检查执行通知
	 * 
	 * @author caijy
	 * @createDate 2017-3-7
	 * @description
	 * @updateInfo
	 * @param xml
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public String exmrequestexecuted(String xml, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Map<String, Object> map_ret = new LinkedHashMap<String, Object>();// 返回给检查的数据
		Map<String, Object> map_inXml;
		try {
			// 检查传过来的xml转成Map
			map_inXml = BSPHISUtil.xml2map_jc(xml);
		} catch (Exception e) {
			// 出现异常,返回错误响应
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "xml格式解析错误:" + e.getMessage()));
			return getReturnXml(map_ret);
		}
		// 判断下EXMREQUEST标签是否为空
		if (!map_inXml.containsKey("EXMREQUEST")
				|| map_inXml.get("EXMREQUEST") == null
				|| !(map_inXml.get("EXMREQUEST") instanceof Map)) {
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "EXMREQUEST标签为空或者数据错误"));
			return getReturnXml(map_ret);
		}
		Map<String, Object> map_exmrequest = (Map<String, Object>) map_inXml
				.get("EXMREQUEST");
		// 数据完整性校验
		// *****************去除执行科室、执行医生相关入参代码****赵健 修改于2017.4.27************/
		// String s_sjjy=sjjy(map_exmrequest, new
		// String[]{"REQUESTID","EXAMEXECUTEORG","PATIENTTYPE","EXEDOCTOR","EXEDEPT","EXEDATETIME"});
		String s_sjjy = sjjy(map_exmrequest, new String[] { "REQUESTID",
				"EXAMEXECUTEORG", "PATIENTTYPE", "EXEDATETIME" });
		if (!"true".equals(s_sjjy)) {
			map_ret.put("MSGHEADER", getHeadMessage(false, 0, s_sjjy));
			return getReturnXml(map_ret);
		}
		long sqid = MedicineUtils.parseLong(map_exmrequest.get("REQUESTID"));// 申请单ID
		String jgid = MedicineUtils.parseString(map_exmrequest
				.get("EXAMEXECUTEORG"));// 检查机构代码
		// long
		// ksdm=MedicineUtils.parseLong(map_exmrequest.get("EXEDEPT"));//部门编码
		// String
		// ysdm=MedicineUtils.parseString(map_exmrequest.get("EXEDOCTOR"));//医生代码
		String zxrq = MedicineUtils.parseString(map_exmrequest
				.get("EXEDATETIME"));// 执行日期
		String patienttype = MedicineUtils.parseString(map_exmrequest
				.get("PATIENTTYPE"));// 患者类型01门诊,02急诊,03体检,04住院
		Date zxsj = null;
		try {
			zxsj = sdf.parse(zxrq);
		} catch (ParseException e) {
			map_ret.put(
					"MSGHEADER",
					getHeadMessage(false, 0, "执行时间格式错误:" + zxrq
							+ ",正确格式20110101120101"));
			return getReturnXml(map_ret);
		}
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sqid", sqid);
		//map_par.put("jgid", jgid);
		// map_par.put("ysdm", ysdm);
		// map_par.put("ksdm", ksdm);
		map_par.put("zxrq", zxsj);
		StringBuffer hql_update = new StringBuffer();
		if ("01".endsWith(patienttype)) {// 门诊
			/*hql_update
					.append("update MS_YJ01 set ZXPB=1,ZXRQ=:zxrq where SQID=:sqid and JGID=:jgid");*/
			hql_update
			.append("update MS_YJ01 set ZXPB=1,ZXRQ=:zxrq where YJXH=:sqid");
		} else {// 04住院
			//map_par.put("jgid", MedicineUtils.parseLong(jgid));
			/*hql_update
					.append("update YJ_ZY01 set ZXPB=1,ZXRQ=:zxrq where SQDH=:sqid and JGID=:jgid");*/
			hql_update
			.append("update YJ_ZY01 set ZXPB=1,ZXRQ=:zxrq where YJXH=:sqid");
		}
		int x;
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			ss.beginTransaction();
			x = dao.doUpdate(hql_update.toString(), map_par);
			ss.getTransaction().commit();
		} catch (PersistentDataOperationException e) {
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "数据库更新失败:" + e.getMessage()));
			ss.getTransaction().rollback();
			return getReturnXml(map_ret);
		}
		if (x == 0) {
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "数据库更新失败:未找到对应记录"));
			return getReturnXml(map_ret);
		}
		map_ret.put("MSGHEADER", getHeadMessage(true, 1, "更新检查状态成功"));
		return getReturnXml(map_ret);
	}

	/**
	 * 检查报告内容
	 * 
	 * @author caijy
	 * @createDate 2017-3-15
	 * @description
	 * @updateInfo
	 * @param xml
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public String exmreport(String xml, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Map<String, Object> map_ret = new LinkedHashMap<String, Object>();// 返回给检查的数据
		Map<String, Object> map_inXml;
		try {
			// 检查传过来的xml转成Map
			map_inXml = BSPHISUtil.xml2map_jc(xml);
		} catch (Exception e) {
			// 出现异常,返回错误响应
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "xml格式解析错误:" + e.getMessage()));
			return getReturnXml(map_ret);
		}
		// 判断下EXMREQUEST标签是否为空
		if (!map_inXml.containsKey("EXMREQUEST")
				|| map_inXml.get("EXMREQUEST") == null
				|| !(map_inXml.get("EXMREQUEST") instanceof Map)) {
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "EXMREQUEST标签为空或者数据错误"));
			return getReturnXml(map_ret);
		}
		Map<String, Object> map_exmrequest = (Map<String, Object>) map_inXml
				.get("EXMREQUEST");
		// 数据完整性校验
		String s_sjjy = sjjy(map_exmrequest, new String[] { "REQUESTID",
				"EXAMEXECUTEORG", "PATIENTTYPE", "EXAMTYPECODE", "EXAMNAME",
				"EXAMDESCRIPT", "EXAMDIAGNOSIS", "REPORTDOC", "REPDATETIME" });
		if (!"true".equals(s_sjjy)) {
			map_ret.put("MSGHEADER", getHeadMessage(false, 0, s_sjjy));
			return getReturnXml(map_ret);
		}
		long sqid = MedicineUtils.parseLong(MedicineUtils.parseString(
				map_exmrequest.get("REQUESTID")).trim());// 申请单ID
		String jgid = MedicineUtils.parseString(
				map_exmrequest.get("EXAMEXECUTEORG")).trim();// 检查机构代码
		String jclb = MedicineUtils.parseString(
				map_exmrequest.get("EXAMTYPECODE")).trim();// 检查类别代码01.超声类
															// 02.放射类 03.内镜
		String jcmc = MedicineUtils.parseString(map_exmrequest.get("EXAMNAME"))
				.trim();// 检查名称
		String yxsj = MedicineUtils.parseString(
				map_exmrequest.get("EXAMDESCRIPT")).trim();// 影像所见
		String hzlx = MedicineUtils.parseString(
				map_exmrequest.get("PATIENTTYPE")).trim();// 患者类型01门诊,02急诊,03体检,04住院
		String bgrq = MedicineUtils.parseString(
				map_exmrequest.get("REPDATETIME")).trim();// 报告时间
		String bgys = MedicineUtils
				.parseString(map_exmrequest.get("REPORTDOC")).trim();// 报告医生
		String yxzd = MedicineUtils.parseString(
				map_exmrequest.get("EXAMDIAGNOSIS")).trim();// 影像诊断
		if (!"".equals(yxsj)) {
			yxsj = yxsj.replaceAll("%br", "\n");
		}
		if (!"".equals(yxzd)) {
			yxzd = yxzd.replaceAll("%br", "\n");
		}
		Date bgsj = null;
		try {
			bgsj = sdf.parse(bgrq);
		} catch (ParseException e) {
			map_ret.put(
					"MSGHEADER",
					getHeadMessage(false, 0, "执行时间格式错误:" + bgrq
							+ ",正确格式20110101120101"));
			return getReturnXml(map_ret);
		}
		Map<String, Object> map_bgjg;
		// 查询报告是否已经存在
		StringBuffer hql_sqd_count = new StringBuffer();
		if ("01".equals(hzlx)) {
			hql_sqd_count.append(" SQID=:sqid");
		} else if ("04".equals(hzlx)) {
			hql_sqd_count.append(" SQDH=:sqid");
		} else {
			map_ret.put("MSGHEADER", getHeadMessage(false, 0, "患者类型错误:" + hzlx));
			return getReturnXml(map_ret);
		}
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sqid", sqid);
		try {
			map_bgjg = dao
					.doLoad("select SBXH as SBXH,SQID as SQID from PACS_BGJG where SQID=:sqid",
							map_par);
		} catch (PersistentDataOperationException e) {
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "查询数据是否存在失败:" + e.getMessage()));
			return getReturnXml(map_ret);
		}
		String op = "update";
		if (map_bgjg == null || map_bgjg.size() == 0) {
			op = "create";
			map_bgjg = new HashMap<String, Object>();
			map_bgjg.put("SQID", sqid);
		}
		map_bgjg.put("JGID", jgid);
		map_bgjg.put("JCLB", jclb);
		map_bgjg.put("JCMC", jcmc);
		map_bgjg.put("YXSJ", yxsj);
		map_bgjg.put("HZLX", hzlx);
		map_bgjg.put("BGSJ", bgsj);
		map_bgjg.put("BGYS", bgys);
		map_bgjg.put("YXZD", yxzd);
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			ss.beginTransaction();
			dao.doSave(op, "phis.application.pacs.schemas.PACS_BGJG", map_bgjg,
					false);
			StringBuffer hql_update = new StringBuffer();// 更新业务表的报告时间
			if ("01".equals(hzlx)) {
				hql_update
						.append(" update MS_YJ01 set BGSJ=:bgsj where SQID=:sqid");
			} else if ("04".equals(hzlx)) {
				hql_update
						.append(" update YJ_ZY01 set BGSJ=:bgsj where SQDH=:sqid");
			}
			Map<String, Object> map_par_update = new HashMap<String, Object>();
			map_par_update.put("bgsj", bgsj);
			map_par_update.put("sqid", sqid);
			int x = dao.doUpdate(hql_update.toString(), map_par_update);
			if (x == 0) {
				ss.getTransaction().rollback();
				map_ret.put(
						"MSGHEADER",
						getHeadMessage(false, 0, "未找到对应检查记录,患者类型:" + hzlx
								+ ",申请id:" + sqid));
				return getReturnXml(map_ret);
			}
		} catch (ValidateException e) {
			ss.getTransaction().rollback();
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "更新报告结果失败:" + e.getMessage()));
			return getReturnXml(map_ret);
		} catch (PersistentDataOperationException e) {
			ss.getTransaction().rollback();
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "更新报告结果失败:" + e.getMessage()));
			return getReturnXml(map_ret);
		}
		map_ret.put("MSGHEADER", getHeadMessage(true, 1, "报告接收成功"));
		return getReturnXml(map_ret);
	}

	/**
	 * 机构服务字典
	 * 
	 * @author caijy
	 * @createDate 2017-3-13
	 * @description
	 * @updateInfo
	 * @param xml
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String organization(String xml, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> list_org = new ArrayList<Map<String, Object>>();
		try {
			Dictionary d = DictionaryController.instance().get(
					"phis.dictionary.organizationDic_jcjy");
			if (d == null) {
				map_ret.put("MSGHEADER", getHeadMessage(false, 0, "没有机构数据"));
				return getReturnXml(map_ret);
			}
			for (DictionaryItem item : d.itemsList()) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("ORGANIZCODE ", item.getKey());// 组织机构代码
				map.put("REGISTERNUMBER", item.getProperty("registerNumber"));// 登记编码
				map.put("ORGANIZCODE ", item.getText());// 机构名称
				map.put("ADDRESS", item.getProperty("address"));// 机构位置
				map.put("ADMINDIVISION", item.getProperty("adminDivision"));// 行政区划
				map.put("PARENTID", item.getProperty("parentId"));// 上级机构代码
				map.put("ORGANIZTYPE", item.getProperty("organizType"));// 机构类型
				list_org.add(map);
			}
		} catch (ControllerException e) {
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "查询机构数据失败:" + e.getMessage()));
			return getReturnXml(map_ret);
		}
		map_ret.put("MSGHEADER",
				getHeadMessage(true, list_org.size(), "机构数据查询成功"));
		map_ret.put("ORGANIZATION ", list_org);
		return getReturnXml(map_ret);
	}

	/**
	 * 科室服务字典
	 * 
	 * @author caijy
	 * @createDate 2017-3-13
	 * @description
	 * @updateInfo
	 * @param xml
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public String depatment(String xml, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new LinkedHashMap<String, Object>();// 返回给检查的数据
		Map<String, Object> map_inXml;
		try {
			// 检查传过来的xml转成Map
			map_inXml = BSPHISUtil.xml2map_jc(xml);
		} catch (Exception e) {
			// 出现异常,返回错误响应
			map_ret.put("MsgHeader",
					getHeadMessage(false, 0, "xml格式解析错误:" + e.getMessage()));
			return getReturnXml(map_ret);
		}
		// 判断下ORGANIZATION标签是否为空
		if (!map_inXml.containsKey("ORGANIZATION")
				|| map_inXml.get("ORGANIZATION") == null
				|| !(map_inXml.get("ORGANIZATION") instanceof Map)) {
			map_ret.put("ORGANIZATION",
					getHeadMessage(false, 0, "ORGANIZATION标签为空或者数据错误"));
			return getReturnXml(map_ret);
		}
		Map<String, Object> map_jg = (Map<String, Object>) map_inXml
				.get("ORGANIZATION");
		String jgid = MedicineUtils.parseString(map_jg.get("ORGANIZCODE"));
		StringBuffer hql = new StringBuffer();// 查询机构下所有科室
		hql.append("select ID as OFFICEID,OFFICECODE as OFFICECODE,OFFICENAME as OFFICENAME,ADDRESS as ADDRESS,ORGANIZCODE as ORGANIZCODE,ORGANIZCODE as PARENTID,ORGANIZTYPE as ORGANIZTYPE from SYS_Office where ORGANIZCODE=:jgid");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("jgid", jgid);
		List<Map<String, Object>> list_office = new ArrayList<Map<String, Object>>();
		try {
			List<Map<String, Object>> list_ks = dao.doQuery(hql.toString(),
					map_par);
			if (list_ks == null || list_ks.size() == 0) {
				map_ret.put("MSGHEADER", getHeadMessage(false, 0, "没有科室数据"));
				return getReturnXml(map_ret);
			}
			for (Map<String, Object> map_ks : list_ks) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("OFFICEID",
						MedicineUtils.parseString(map_ks.get("OFFICEID")));// 科室ID
				map.put("OFFICECODE",
						MedicineUtils.parseString(map_ks.get("OFFICECODE")));// 科室编码
				map.put("OFFICENAME",
						MedicineUtils.parseString(map_ks.get("OFFICENAME")));// 科室名称
				map.put("ADDRESS",
						MedicineUtils.parseString(map_ks.get("ADDRESS")));// 科室位置
				map.put("ORGANIZCODE ",
						MedicineUtils.parseString(map_ks.get("ORGANIZCODE")));// 组织机构代码
				map.put("PARENTID ",
						MedicineUtils.parseString(map_ks.get("PARENTID")));// 上级科室代码
				map.put("ORGANIZTYPE ",
						MedicineUtils.parseString(map_ks.get("ORGANIZTYPE")));// 科室类别
				map.put("OFFICEPRINCIPAL", "");// 科室负责人
				list_office.add(map);
			}
		} catch (PersistentDataOperationException e) {
			map_ret.put("MSGHEADER",
					getHeadMessage(false, 0, "查询机构数据失败:" + e.getMessage()));
			return getReturnXml(map_ret);
		}
		map_ret.put("MSGHEADER",
				getHeadMessage(true, list_office.size(), "科室数据查询成功"));
		map_ret.put("ORGANIZATION ", list_office);
		return getReturnXml(map_ret);
	}

	/**
	 * 报告结果查询
	 * 
	 * @author caijy
	 * @createDate 2017-3-17
	 * @description
	 * @updateInfo
	 * @param sqid
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryBgjg(long sqid, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = null;
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sqid", sqid);
		try {
			map_ret = dao
					.doLoad("select JCMC as JCMC,YXSJ as YXSJ,YXZD as YXZD,BGYS as BGYS,BGSJ as BGSJ from PACS_BGJG where SQID=:sqid",
							map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "检查结果数据查询失败", e);
		}
		return map_ret;
	}

	/**
	 * 拼接返回的head数据
	 * 
	 * @author caijy
	 * @createDate 2017-3-10
	 * @description
	 * @updateInfo
	 * @param tag
	 *            是否成功,true是成功,false失败
	 * @param c
	 *            成功的记录条数,失败传0
	 * @param Detail
	 *            响应描述,成功可以直接传""
	 * @return
	 */
	public Map<String, Object> getHeadMessage(boolean tag, int c, String Detail) {
		Map<String, Object> map_head = new LinkedHashMap<String, Object>();
		map_head.put("SENDER", "基卫");
		map_head.put("STATUS", tag);
		map_head.put("CODE", tag ? 200 : 9000);
		map_head.put("DETAIL", "".equals(Detail) && tag ? "查询检查申请单成功" : Detail);
		map_head.put("RETURNCOUNT", c);
		return map_head;
	}

	/**
	 * 拼接返回xml
	 * 
	 * @author caijy
	 * @createDate 2017-3-10
	 * @description
	 * @updateInfo
	 * @param BSXml
	 * @return
	 */
	public String getReturnXml(Map<String, Object> BSXml) {
		Map<String, Object> map_ret = new LinkedHashMap<String, Object>();
		map_ret.put("BSXML", BSXml);
		return BSPHISUtil.map2xml_jc(map_ret);
	}

	/**
	 * 校验字段的完整性
	 * 
	 * @author caijy
	 * @createDate 2017-3-11
	 * @description
	 * @updateInfo
	 * @param map
	 * @param jyzd
	 * @return true是校验通过 否则返回失败字符串
	 */
	public String sjjy(Map<String, Object> map, String[] jyzd) {
		StringBuffer s_ret = new StringBuffer();
		for (String key : jyzd) {
			if (!map.containsKey(key)
					|| "".equals(MedicineUtils.parseString(map.get(key)))) {
				s_ret.append(key).append(",");
			}
		}
		if (s_ret.length() > 0) {
			return "数据校验失败:" + s_ret.toString() + "为空";
		}
		return "true";
	}
}
