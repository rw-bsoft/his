package phis.application.emr.source;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.cic.source.EMRTreeModule;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPEMRUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.utils.T;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.util.PyConverter;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class EmrManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(EmrManageModel.class);

	public EmrManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	public List<Map<String, Object>> doLoadBllb(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		String hql = "SELECT a.LBBH as LBBH,a.YDLBBM ,a.LBMC ,a.LBBM ,a.ZYPLSX , b.BJQLB,b.YDLBMC FROM EMR_KBM_BLLB a,EMR_KBM_YDBLLB b WHERE a.YDLBBM = b.YDLBBM AND a.MLBZ = 1 order by ";
		Integer zybz = (Integer) body.get("ZYBZ");
		if (zybz == 1) {
			hql += " a.ZYPLXH";
		} else {
			hql += " a.CYPLXH";
		}
		try {
			return dao.doSqlQuery(hql, null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据库发生异常!请联系管理员.", e);
		}
	}

	/**
	 * 加载接口元素配置表信息
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> doLoadJkys()
			throws ModelDataOperationException {
		try {
			return dao.doList(null, null, BSPHISEntryNames.EMR_JKYS);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取接口元素配置信息错误，请联系管理员!", e);
		}
	}

	public Map<String, Object> doLoadPrtSetup(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			Map<String, Object> r = dao.doLoad(BSPHISEntryNames.EMR_KBM_BLLB,
					body.get("BLLB"));
			if (r != null) {
				Blob blob = (Blob) r.get("PRINTSETUP");
				StringBuffer prtSetup = new StringBuffer();
				if (blob != null) {
					InputStream gbk_is = blob.getBinaryStream();
					Reader reader = new InputStreamReader(gbk_is, "GBK");

					int charValue = 0;
					while ((charValue = reader.read()) != -1) {
						prtSetup.append((char) charValue);
					}
				}
				r.put("prtSetup", prtSetup.toString());
				r.remove("PRINTSETUP");
				res.put("body", r);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取打印参数错误，请联系管理员!", e);
		} catch (SQLException e) {
			throw new ModelDataOperationException("获取打印参数错误，请联系管理员!", e);
		} catch (IOException e) {
			throw new ModelDataOperationException("获取打印参数错误，请联系管理员!", e);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doLoadRefItems(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> items = (List<Map<String, Object>>) body
				.get("items");
		Object zyh = body.get("ZYH");
		Object brks = body.get("BRKS");
		Map<String, Map<String, Object>> cache = null;
		try {
			if (body.containsKey("MZSY")) {
				cache = cacheMzRefItems(zyh, brks);
			} else {
				cache = cacheRefItems(zyh, brks);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("基础信息错误，请联系管理员", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("基础信息错误，请联系管理员", e);
		}
		for (Map<String, Object> item : items) {
			try {
				analyzeRefMap(item, cache, true, ctx);
			} catch (Exception e) {
				logger.error("ref item[" + item.get("Ref") + "] load faild", e);
			}
		}
		return items;
	}

	/**
	 * 病历锁管理
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public void doSaveEmrLockManage(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		String op = (String) body.get("type");
		Long blbh = 0l;
		if (body.containsKey("BLBH")) {
			blbh = Long.parseLong(body.get("BLBH").toString());
		}

		UserRoleToken user = UserRoleToken.getCurrent();

		try {
			if (op.equals("lock")) {
				Map<String, Object> m = dao.doLoad(BSPHISEntryNames.EMR_BLSD,
						blbh);
				if (m == null) {
					Map<String, Object> blsd = new HashMap<String, Object>();
					blsd.put("BLBH", blbh);
					blsd.put("SDYG", (String) user.getUserId());
					blsd.put("SDSJ", new Date());
					blsd.put("SDIP", " ");
					blsd.put("SDZT", 1);
					blsd.put("JGID", user.getManageUnitId());
					dao.doSave("create", BSPHISEntryNames.EMR_BLSD, blsd, false);
				} else if ((Integer) m.get("SDZT") != 1
						|| ((String) user.getUserId()).equals(m.get("SDYG"))) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("SDYG", (String) user.getUserId());
					parameters.put("BLBH", blbh);
					parameters.put("SDSJ", new Date());
					dao.doUpdate(
							"update EMR_BLSD set SDYG=:SDYG,SDZT=1,SDSJ=:SDSJ where BLBH=:BLBH",
							parameters);
				} else if ((new Date().getTime() - ((Date) m.get("SDSJ"))// 超过5分钟自动解锁
						.getTime()) / 1000 / 60 > 5) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("SDYG", (String) user.getUserId());
					parameters.put("BLBH", blbh);
					parameters.put("SDSJ", new Date());
					dao.doUpdate(
							"update EMR_BLSD set SDYG=:SDYG,SDZT=1,SDSJ=:SDSJ where BLBH=:BLBH",
							parameters);
				} else {

					String uname = DictionaryController.instance()
							.get("phis.dictionary.doctor")
							.getText(m.get("SDYG").toString());
					throw new ModelDataOperationException(515, "该病历/病程已被医生【"
							+ uname + "】锁定，无法进行编辑操作!");
				}
			} else {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("SDYG", (String) user.getUserId());
				parameters.put("BLBH", blbh);
				dao.doUpdate(
						"update EMR_BLSD set SDZT=0 where BLBH=:BLBH and SDZT=1 and SDYG=:SDYG",
						parameters);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("操作病历锁发生异常", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("操作病历锁发生异常", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("获取数据字典失败", e);
		}
	}

	/**
	 * 病程引病历
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doLoadParaRefItems(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		List<Map<String, Object>> items = (List<Map<String, Object>>) body
				.get("items");
		List<String> refBlnrArray = new ArrayList<String>();
		Object zyh = body.get("ZYH");
		try {
			for (int i = 0; i < items.size(); i++) {
				Map<String, Object> m = items.get(i);
				Map<String, Object> paraElem = dao.doLoad(
						BSPHISEntryNames.PARAELEMENT, m.get("Value"));
				if (paraElem != null) {
					Object petype = paraElem.get("EMRTYPECODE");
					if (petype != null && petype.toString().length() > 0
							&& Integer.parseInt(petype.toString()) > 0) {
						Map<String, Object> parameters = new HashMap<String, Object>();
						parameters.put("JZXH", zyh);
						parameters.put("BLLB",
								Integer.parseInt(petype.toString()));
						List<Map<String, Object>> list = dao
								.doQuery(
										"select b.BLNR as BLNR from EMR_BL01 a,EMR_BL02 b where a.BLBH=b.BLBH and a.BLLB=:BLLB and JZXH=:JZXH and a.BLZT<>9 order by a.BLBH desc",
										parameters);
						if (list.size() > 0) {
							Blob blob = (Blob) list.get(0).get("BLNR");
							StringBuffer blnr = new StringBuffer();
							if (blob != null) {
								InputStream is = blob.getBinaryStream();
								Reader reader = new InputStreamReader(is,
										"UTF-8");

								int charValue = 0;
								while ((charValue = reader.read()) != -1) {
									blnr.append((char) charValue);
								}
							}
							refBlnrArray.add(blnr.toString());
							continue;
						}
					}
				} else {
					throw new ModelDataOperationException("请在模板编辑器--业务菜单中设置参数【"
							+ m.get("Value") + "】的默认值");
				}
				refBlnrArray.add("");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("更新引用元素错误，请联系管理员", e);
		} catch (SQLException e) {
			throw new ModelDataOperationException("更新引用元素错误，请联系管理员", e);
		} catch (IOException e) {
			throw new ModelDataOperationException("更新引用元素错误，请联系管理员", e);
		}
		res.put("retItems", items);
		res.put("refBlnrArray", refBlnrArray);
		return res;
	}

	public void analyzeRefMap(Map<String, Object> item,
			Map<String, Map<String, Object>> cache, boolean needDic, Context ctx)
			throws PersistentDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		StringBuffer sql = new StringBuffer("select ");
		String ref = (String) item.get("Ref");
		String dict = (String) item.get("Dict");
		String fmt = (String) item.get("Fmt");
		String[] refArry = ref.split("\\|");
		String value = refArry[1];
		sql.append(" a." + refArry[1] + " as " + refArry[1] + ",");
		if (dict.length() > 0) {
			String[] dictArry = dict.split("\\|");
			sql.append(" b." + dictArry[2] + " as NAME");
			sql.append(" from " + refArry[0] + " a," + dictArry[0] + " b");
			sql.append(" where a." + refArry[1] + "=" + "b." + dictArry[1]
					+ " and a." + refArry[1] + "=:Para");
		} else {
			sql.deleteCharAt(sql.length() - 1).append(
					" from " + refArry[0] + " a where ");
		}
		// where 条件
		Map<String, Object> r = new HashMap<String, Object>();
		if ("ZY_BRRY".equals(refArry[0])) {
			r = cache.get("ZY_BRRY");
		} else if ("JC_BRRY".equals(refArry[0])) {
			r = cache.get("JC_BRRY");
		} else if ("MS_JZLS".equals(refArry[0])) {
			r = cache.get("MS_JZLS");
		} else if ("EMR_YY_TSYS".equals(refArry[0])) {
			if ("JLRQ".equals(value)) {
				r.put(value, new Date());
			} else if ("ZYTS".equals(value)) {
				r.put(value,
						BSHISUtil.getDifferDays(new Date(),
								(Date) cache.get("ZY_BRRY").get("RYRQ")));
			} else if ("JGMC".equals(value)) {
				r.put(value, user.getManageUnitName());
			} else if ("DRMC".equals(value)) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("organizCode", user.getManageUnit().getRef());
				Map<String, Object> orgn = dao
						.doLoad("select organizSecondName as SEC_NAME from SYS_Organization where organizCode=:organizCode",
								parameters);
				if (orgn != null) {
					r.put(value, orgn.get("SEC_NAME"));
				}
			}
		} else {
			item.put("Value", "");
			return;
		}
		if (fmt.length() > 0 && fmt.startsWith("fmt02_")) {
			String format = fmt.split("_")[1];
			SimpleDateFormat sdf = new SimpleDateFormat(format
					.replace("m", "M").replace("n", "m").replace("hh", "HH"));
			item.put("Value", sdf.format(r.get(value)));
		} else {
			Object v = r.get(value) == null ? "" : r.get(value);
			if (dict.length() > 0) {
				String keyId = "MS_JZLS".equals(refArry[0]) ? "JZXH" : "ZYH";
				Map<String, Object> para = new HashMap<String, Object>();
				sql.append(" and a." + keyId + " = :ZYH");
				para.put("Para", "0".equals(v.toString()) ? "00" : v);
				para.put("ZYH", Long.parseLong(r.get(keyId).toString()));
				List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(),
						para);
				if (list.size() > 0) {
					if (needDic) {
						item.put("Value", v + "," + list.get(0).get("NAME"));
					} else {
						item.put("Value", list.get(0).get("NAME"));
					}
				}
			} else {
				item.put("Value", v);
			}
		}
	}

	public Map<String, Map<String, Object>> cacheMzRefItems(Object zyh,
			Object brks) throws PersistentDataOperationException, ExpException {
		Map<String, Map<String, Object>> cache = new HashMap<String, Map<String, Object>>();
		String cnd = "['eq',['$','JZXH'],['d'," + zyh + "]]";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("JZXH", Long.parseLong(zyh.toString()));
		Map<String, Object> ms_jzls = null;
		List<Map<String, Object>> l = dao
				.doSqlQuery(
						"select JZXH as JZXH,GHXH,BRBH,KSDM,YSDM,ZYZD,KSSJ,JSSJ,JZZT,YYXH,FZRQ,GHFZ,JGID,BRID,MZHM,BRXM,FYZH,SFZH,BRXZ,BRXB,CSNY,HYZK,ZYDM,MZDM,XXDM,GMYW,DWXH,DWMC,DWDH,DWYB,HKDZ,JTDH,HKYB,JZCS,JZRQ,CZRQ,JZKH,SFDM,JGDM,GJDM,LXRM,LXGX,LXDZ,LXDH,DBRM,DBGX,SBHM,YBKH,ZZTX,JDJG,JDSJ,JDR,EMPIID from MS_JZLS where JZXH=:JZXH",
						params);
		if (l.size() > 0) {
			ms_jzls = l.get(0);
		}
		cache.put("MS_JZLS", ms_jzls);
		cnd = "['eq',['$','ID'],['d'," + brks + "]]";
		Map<String, Object> SYS_Office = dao.doLoad(CNDHelper.toListCnd(cnd),
				BSPHISEntryNames.SYS_Office);
		cache.put("SYS_Office", SYS_Office);
		return cache;
	}

	public Map<String, Map<String, Object>> cacheRefItems(Object zyh,
			Object brks) throws PersistentDataOperationException, ExpException {
		Map<String, Map<String, Object>> cache = new HashMap<String, Map<String, Object>>();
		String cnd = "['eq',['$','ZYH'],['d'," + zyh + "]]";
		Map<String, Object> zy_brry = dao.doLoad(CNDHelper.toListCnd(cnd),
				BSPHISEntryNames.ZY_BRRY);
		cache.put("ZY_BRRY", zy_brry);
		Map<String, Object> jc_brry = dao.doLoad(CNDHelper.toListCnd(cnd),
				BSPHISEntryNames.JC_BRRY);
		cache.put("JC_BRRY", jc_brry);
		cnd = "['eq',['$','ID'],['d'," + brks + "]]";
		Map<String, Object> SYS_Office = dao.doLoad(CNDHelper.toListCnd(cnd),
				BSPHISEntryNames.SYS_Office);
		cache.put("SYS_Office", SYS_Office);
		return cache;
	}

	public Map<String, Object> doLoadNavTree(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		// SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
		// "emrSessionFactory");
		// Session ss = sf.openSession();
		// BaseDAO emrDao = new BaseDAO(ctx, ss);
		String hql = "SELECT a.LBBH as id,a.LBMC as text,a.LBMC,a.LBMC as MBMC,a.DYWD,a.BLLX,a.XSMC,a.LBBH as MBLB  FROM EMR_KBM_BLLB a,EMR_KBM_YDBLLB b WHERE a.YDLBBM = b.YDLBBM AND a.MLBZ = 1 and b.BJQLB<>13 order by ";
		Integer zybz = (Integer) (body.get("ZYBZ") == null ? 1 : body
				.get("ZYBZ"));
		Long jzh = Long.parseLong(body.get("JZH").toString());
		if (zybz == 1) {
			hql += " a.ZYPLXH";
		} else {
			hql += " a.CYPLXH";
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dao.doSqlQuery(hql, null);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JZH", jzh);
			for (Map<String, Object> map : list) {
				if ("1".equals(map.get("DYWD")))
					continue;
				String mxHql = "SELECT a.BLLB as id,a.BLMC as text,a.BLBH,a.BLLX,b.MBMC,a.MBLB FROM EMR_BL01 a left join V_EMR_BLMB b on (a.MBBH=b.MBBH and b.BLLX=a.BLLX) WHERE (a.JZXH = :JZH) AND a.BLLB = :BLLB AND a.BLZT <> 9 and a.DLLB <>-1 order by a.JZXH ASC,a.BLLB ASC,a.JLSJ ASC,a.BLBH  ASC";
				params.put("BLLB", map.get("ID"));
				List<Map<String, Object>> mxList = dao
						.doSqlQuery(mxHql, params);
				if (mxList.size() > 0) {
					for (Map<String, Object> mxMap : mxList) {
						mxMap.put("LBMC", map.get("TEXT"));
						mxMap.put("XSMC", map.get("XSMC"));
						mxMap.put("DYWD", map.get("DYWD"));
					}
					map.put("expanded", true);
					map.put("children", mxList);
				} else {
					map.put("leaf", true);
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据库发生异常!请联系管理员.", e);
		}
		List<Map<String, Object>> ehrList = this.getWorkList(body, ctx);
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("EMR", list);
		resMap.put("EHR", ehrList);
		return resMap;
	}

	/**
	 * 门诊emr导航
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadEMRNavTree(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		// SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
		// "emrSessionFactory");
		// Session ss = sf.openSession();
		// BaseDAO emrDao = new BaseDAO(ctx, ss);
		String hql = "SELECT a.LBBH as id,a.LBMC as text,a.LBMC,a.LBMC as MBMC,a.DYWD,a.BLLX,a.XSMC,a.LBBH as MBLB,a.YDLBBM as YDLBBM  FROM EMR_KBM_BLLB a,EMR_KBM_YDBLLB b WHERE a.YDLBBM = b.YDLBBM AND a.MLBZ = 1 and b.BJQLB=13 order by ";
		Integer zybz = (Integer) (body.get("ZYBZ") == null ? 1 : body
				.get("ZYBZ"));
		Long jzh = Long.parseLong(body.get("JZH").toString());
		if (zybz == 1) {
			hql += " a.ZYPLXH";
		} else {
			hql += " a.CYPLXH";
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dao.doSqlQuery(hql, null);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JZH", jzh);
			for (Map<String, Object> map : list) {
				if ("1".equals(map.get("DYWD")))
					continue;
				String mxHql = "SELECT a.BLLB as id,a.BLMC as text,a.BLBH,a.BLLX,b.MBMC,a.MBLB FROM OMR_BL01 a left join V_EMR_BLMB b on (a.MBBH=b.MBBH and b.BLLX=a.BLLX) WHERE (a.JZXH = :JZH) AND a.BLLB = :BLLB AND a.BLZT <> 9 and a.DLLB <>-1 order by a.JZXH ASC,a.BLLB ASC,a.JLSJ ASC,a.BLBH  ASC";
				params.put("BLLB", map.get("ID"));
				List<Map<String, Object>> mxList = dao
						.doSqlQuery(mxHql, params);
				if (mxList.size() > 0) {
					for (Map<String, Object> mxMap : mxList) {
						mxMap.put("LBMC", map.get("TEXT"));
						mxMap.put("XSMC", map.get("XSMC"));
						mxMap.put("DYWD", map.get("DYWD"));
						mxMap.put("YDLBBM", map.get("YDLBBM"));
						mxMap.put("emrEditor", true);
					}
					map.put("children", mxList);
				} else {
					map.put("leaf", true);
				}
				map.put("emrEditor", true);
			}
			HashMap<String, Object> hashmap = new HashMap<String, Object>();
			hashmap.put("ID", "B02");
			hashmap.put("TEXT", "就诊历史");
			hashmap.put("requireKeys", "empiId");
			hashmap.put("ref", "phis.application.cic.CIC/CIC/CIC0201");
			hashmap.put("controlKey", "EHR_HealthRecord_readOnly");
			hashmap.put("expanded", true);
			hashmap.put("emrEditor", false);
			list.add(hashmap);
			HashMap<String, Object> hashmap1 = new HashMap<String, Object>();
			hashmap1.put("ID", "B07");
			hashmap1.put("TEXT", "住院历史病历");
			hashmap1.put("requireKeys", "empiId");
			hashmap1.put("ref", "phis.application.cic.CIC/CIC/CIC0203");
			hashmap1.put("controlKey", "EHR_HealthRecord_readOnly");
			hashmap1.put("expanded", true);
			hashmap1.put("emrEditor", false);
			list.add(hashmap1);
			// HashMap<String,Object> map = new HashMap<String,Object>();
			// map.put("key", "B02");
			// map.put("text", "就诊历史");
			// map.put("requireKeys", "empiId");
			// map.put("ref", "phis.application.cic.CIC/CIC/CIC0201");
			// map.put("controlKey", "EHR_HealthRecord_readOnly");
			// list.add(map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据库发生异常!请联系管理员.", e);
		}
		EMRTreeModule emrm = new EMRTreeModule(dao);
		List<Map<String, Object>> ehrList = emrm.loadTJJYData(ctx);
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("EMR", list);
		resMap.put("EHR", ehrList);
		return resMap;
	}

	@SuppressWarnings("rawtypes")
	private List<Map<String, Object>> getWorkList(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> rolesList = new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		// String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String uid = (String) user.getUserId();
		String empiId = (String) body.get("empiId");
		String topUnitId = ParameterUtil.getTopUnitId();
		String SFQYGWXT = ParameterUtil.getParameter(topUnitId,
				BSPHISSystemArgument.SFQYGWXT, ctx);
		SFQYGWXT = "0";
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			if ("1".equals(SFQYGWXT)) {
				parameters.put("YGDM", uid);
				// List<Map<String, Object>> USER_COLLATES = dao
				// .doQuery(
				// "select USERID as USERID,PASSWORD as PASSWORD,MANAUNITID||'@'||JOBID as JOBID from SYS_USERCOLLATE where YGDM=:YGDM",
				// parameters);
				// if (USER_COLLATES.size() > 0) {
				// Map<String, Object> USER_COLLATE = USER_COLLATES.get(0);
				// parameters.remove("YGDM");
				// String userId = (String) USER_COLLATE.get("USERID");
				// String passWord = (String) USER_COLLATE.get("PASSWORD");
				// String role = (String) USER_COLLATE.get("JOBID");
				Map<String, Object> jkda = new HashMap<String, Object>();
				jkda.put("key", "B");
				jkda.put("text", "健康档案");
				jkda.put("pkey", "phrId");
				jkda.put("expanded", true);
				List<Map<String, Object>> jkdaList = new ArrayList<Map<String, Object>>();
				parameters.put("empiId", empiId);
				// String xmlText = getWorkListStub(manaUnitId, userId,
				// passWord, role, empiId, ctx);

				// if (xmlText == null) {
				// throw new ModelDataOperationException("无法获取公卫信息!");
				// }
				Document doc = null;
				// try {
				// doc = DocumentHelper.parseText(xmlText);
				// } catch (DocumentException e) {
				// throw new ModelDataOperationException("公卫信息解析失败!", e);
				// }
				Element root = doc.getRootElement();
				Element code = root.element("code");
				// 取得节点属性
				String text = code.getText();

				Map<String, Object> cacheTarget = new HashMap<String, Object>();
				if (text != null && text.equals("200")) {
					Element taskList = root.element("taskList");
					Iterator iterator_taskList = taskList
							.elementIterator("task");
					while (iterator_taskList.hasNext()) {
						Element task = (Element) iterator_taskList.next();
						String mustDo = task.element("mustDo").getText();
						Map<String, Object> roleMap = new HashMap<String, Object>();
						if ((task.element("actionName").getText().indexOf("需要") >= 0
								|| task.element("actionName").getText()
										.indexOf("随访") >= 0
								|| task.element("actionName").getText()
										.indexOf("核实") >= 0 || task
								.element("actionName").getText()
								.indexOf("健康档案") >= 0)
								&& mustDo.equals("1")) {// create
							roleMap.put("text", "<span style='color:red'>"
									+ task.element("actionName").getText()
									+ "</span>");
						} else {
							roleMap.put("text", task.element("actionName")
									.getText());
						}
						roleMap.put("actionType", task.element("actionType")
								.getText());
						roleMap.put("mustDo", task.element("mustDo").getText());
						String url = task.element("url").getText();
						roleMap.put("id", url);
						roleMap.put("leaf", true);
						jkdaList.add(roleMap);
						cacheTarget.put(task.element("actionName").getText(),
								roleMap);
					}
					List<Map<String, Object>> GXYL = dao
							.doSqlQuery(
									"select d.JBPB as JBPB,d.JBBGK as JBBGK from GY_JBBM d where d.jbxh in (select c.ZDXH from "
											+ "ZY_RYZD c where c.ZYH in (select b.ZYH from "
											+ "MS_BRDA a ,ZY_BRRY b where a.BRID = b.BRID and a.EMPIID=:empiId))",
									parameters);
					boolean GXY = false;
					boolean TLB = false;
					boolean BGK = false;
					for (int i = 0; i < GXYL.size(); i++) {
						Map<String, Object> map = GXYL.get(i);
						if (map.get("JBPB") != null
								&& (map.get("JBPB") + "").indexOf("01") >= 0) {
							GXY = true;
						} else if (map.get("JBPB") != null
								&& (map.get("JBPB") + "").indexOf("02") >= 0) {
							TLB = true;
						}
						if (map.get("JBBGK") != null
								&& (map.get("JBBGK") + "").indexOf("06") >= 0) {
							BGK = true;
						}
					}
					// if (GXY) {
					// String xmlText2 = CreateDocumentStub(manaUnitId,
					// userId, passWord, role, empiId,
					// "hypertensionRecord", ctx);
					// if (xmlText2 == null) {
					// throw new ModelDataOperationException(
					// "无法获取公卫信息!");
					// }
					// Document doc2 = null;
					// try {
					// doc2 = DocumentHelper.parseText(xmlText2);
					// } catch (DocumentException e) {
					// throw new ModelDataOperationException(
					// "公卫信息解析失败!", e);
					// }
					// Element root2 = doc2.getRootElement();
					// Element code2 = root2.element("code");
					// // 取得节点属性
					// String text2 = code2.getText();
					// if (text2 != null && !text2.equals("200")) {
					// String msg2 = root2.element("msg").getText();
					// throw new ModelDataOperationException(
					// "载入公卫信息错误：" + msg2);
					// }
					// Element task2 = root2.element("task");
					// if (task2 != null) {
					// String mustDo = task2.element("mustDo")
					// .getText();
					// Map<String, Object> task2Map = new HashMap<String,
					// Object>();
					// if ((task2.element("actionName").getText()
					// .indexOf("需要") >= 0
					// || task2.element("actionName")
					// .getText().indexOf("随访") >= 0 || task2
					// .element("actionName").getText()
					// .indexOf("核实") >= 0)
					// && mustDo.equals("1")) {
					// task2Map.put(
					// "text",
					// "<span style='color:red'>"
					// + task2.element(
					// "actionName")
					// .getText()
					// + "</span>");
					// } else {
					// task2Map.put("text",
					// task2.element("actionName")
					// .getText());
					// }
					// task2Map.put("actionType",
					// task2.element("actionType").getText());
					// task2Map.put("mustDo", task2.element("mustDo")
					// .getText());
					// String url = task2.element("url").getText();
					// task2Map.put("id", url);
					// task2Map.put("leaf", true);
					//
					// if (cacheTarget.containsKey("需要高血压首诊测压")) { //
					// 如果是诊断录入那么不用首诊测压
					// jkdaList.remove(cacheTarget
					// .get("需要高血压首诊测压"));
					// }
					// if (cacheTarget.containsKey(task2.element(
					// "actionName").getText())) { // 如果工作列表存在移除
					// // jkdaList.remove(cacheTarget.get(task2
					// // .element("actionName").getText()));
					// } else {
					// jkdaList.add(task2Map);
					// }
					// }
					// }
					// if (TLB) {
					// String xmlText2 = CreateDocumentStub(manaUnitId,
					// userId, passWord, role, empiId,
					// "diabetesRecord", ctx);
					// if (xmlText2 == null) {
					// throw new ModelDataOperationException(
					// "无法获取公卫信息!");
					// }
					// Document doc2 = null;
					// try {
					// doc2 = DocumentHelper.parseText(xmlText2);
					// } catch (DocumentException e) {
					// throw new ModelDataOperationException(
					// "公卫信息解析失败!", e);
					// }
					// Element root2 = doc2.getRootElement();
					// Element code2 = root2.element("code");
					// // 取得节点属性
					// String text2 = code2.getText();
					// if (text2 != null && !text2.equals("200")) {
					// String msg2 = root2.element("msg").getText();
					// throw new ModelDataOperationException(
					// "载入公卫信息错误：" + msg2);
					// }
					// Element task2 = root2.element("task");
					// if (task2 != null) {
					// String mustDo = task2.element("mustDo")
					// .getText();
					// Map<String, Object> task2Map = new HashMap<String,
					// Object>();
					// if ((task2.element("actionName").getText()
					// .indexOf("需要") >= 0
					// || task2.element("actionName")
					// .getText().indexOf("随访") >= 0 || task2
					// .element("actionName").getText()
					// .indexOf("核实") >= 0)
					// && mustDo.equals("1")) {
					// task2Map.put(
					// "text",
					// "<span style='color:red'>"
					// + task2.element(
					// "actionName")
					// .getText()
					// + "</span>");
					// } else {
					// task2Map.put("text",
					// task2.element("actionName")
					// .getText());
					// }
					// task2Map.put("actionType",
					// task2.element("actionType").getText());
					// task2Map.put("mustDo", task2.element("mustDo")
					// .getText());
					// String url = task2.element("url").getText();
					// task2Map.put("id", url);
					// task2Map.put("leaf", true);
					// if (cacheTarget.containsKey(task2.element(
					// "actionName").getText())) { // 如果工作列表存在
					// // 不执行添加操作
					// // jkdaList.remove(cacheTarget.get(task2
					// // .element("actionName").getText()));
					// } else {
					// jkdaList.add(task2Map);
					// }
					//
					// }
					// }
					//
					// if (BGK) {
					// String xmlText2 = CreateDocumentStub(manaUnitId,
					// userId, passWord, role, empiId,
					// "idrReport", ctx);
					// if (xmlText2 == null) {
					// throw new ModelDataOperationException(
					// "无法获取公卫信息!");
					// }
					// Document doc2 = null;
					// try {
					// doc2 = DocumentHelper.parseText(xmlText2);
					// } catch (DocumentException e) {
					// throw new ModelDataOperationException(
					// "公卫信息解析失败!", e);
					// }
					// Element root2 = doc2.getRootElement();
					// Element code2 = root2.element("code");
					// // 取得节点属性
					// String text2 = code2.getText();
					// if (text2 != null && !text2.equals("200")) {
					// String msg2 = root2.element("msg").getText();
					// throw new ModelDataOperationException(
					// "载入公卫信息错误：" + msg2);
					// }
					// Element task2 = root2.element("task");
					// if (task2 != null) {
					// String mustDo = task2.element("mustDo")
					// .getText();
					// Map<String, Object> task2Map = new HashMap<String,
					// Object>();
					// if ((task2.element("actionName").getText()
					// .indexOf("需") >= 0)
					// && mustDo.equals("1")) {
					// task2Map.put(
					// "text",
					// "<span style='color:red'>"
					// + task2.element(
					// "actionName")
					// .getText()
					// + "</span>");
					// task2Map.put("actionType",
					// task2.element("actionType")
					// .getText());
					// task2Map.put("mustDo",
					// task2.element("mustDo").getText());
					// String url = task2.element("url").getText();
					// task2Map.put("id", url);
					// task2Map.put("leaf", true);
					// if (cacheTarget.get("需建立传染病报告卡") == null)
					// jkdaList.add(task2Map);
					// }
					// }
					// }
					jkda.put("children", jkdaList);
					rolesList.add(jkda);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rolesList;
	}

	@SuppressWarnings("deprecation")
	public void doSaveYxbds(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		String bdsnr = body.get("BDSNR").toString();
		body.put("BDSNR", Hibernate.createBlob(bdsnr.getBytes()));
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("BLBDSBH",
					Long.parseLong(body.get("BLBDSBH").toString()));
			long l = dao
					.doCount("EMR_YXBDS_BL", "BLBDSBH=:BLBDSBH", parameters);
			dao.doSave(l > 0 ? "update" : "create",
					BSPHISEntryNames.EMR_YXBDS_BL, body, false);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("数据库发生异常!请联系管理员.", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据库发生异常!请联系管理员.", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveZkks(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Integer ZKLB = (Integer) body.get("ZKLB");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();// 用户的机构ID
		List<Map<String, Object>> zkks = (List<Map<String, Object>>) body
				.get("ksdms");
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("tableName", BSPHISEntryNames.EMR_ZKKS);
			params.put("JGID", manageUnit);
			dao.doUpdate("delete from EMR_ZKKS where ZKFL='" + ZKLB
					+ "' and JGID='" + manageUnit + "'", null);
			for (int i = 0; i < zkks.size(); i++) {
				Map<String, Object> ks = zkks.get(i);
				ks.put("JGID", manageUnit);
				ks.put("ZKFL", ZKLB);
				dao.doSave("create", BSPHISEntryNames.EMR_ZKKS, ks, false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("科室对照保存失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("科室对照保存失败!", e);
		}
	}

	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public Object doLoadYxbds(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		String queryType = (String) body.get("queryType");
		try {
			if (queryType.equals("1")) {
				Map<String, Object> params = new HashMap<String, Object>();
				// params.put("SSKS",
				// Long.parseLong(body.get("SSKS").toString()));
				List<Map<String, Object>> list = dao
						.doQuery(
								"select a.BDSMC as BDSMC,a.SSZK as SSZK,a.BDSNR as BDSNR from EMR_YXBDS_DY a where  ZXBZ<>1",
								params);
				for (Map<String, Object> m : list) {
					Blob blnr = (Blob) m.get("BDSNR");
					if (blnr == null) {
						m.put("BDSNR", "");
					} else {
						InputStream is = blnr.getBinaryStream();
						Reader reader = new InputStreamReader(is, "GBK");
						StringBuffer Buf = new StringBuffer();
						int charValue = 0;
						while ((charValue = reader.read()) != -1) {
							Buf.append((char) charValue);
						}
						m.put("BDSNR", Buf.toString());
					}
				}
				return list;
			} else {
				Map<String, Object> m = dao.doLoad(
						BSPHISEntryNames.EMR_YXBDS_BL, body.get("BLBDSBH"));
				Blob blnr = (Blob) m.get("BDSNR");
				if (blnr == null) {
					m.put("BDSNR", "");
				} else {
					InputStream is = blnr.getBinaryStream();
					Reader reader = new InputStreamReader(is, "GBK");
					StringBuffer Buf = new StringBuffer();
					int charValue = 0;
					while ((charValue = reader.read()) != -1) {
						Buf.append((char) charValue);
					}
					m.put("BDSNR", Buf.toString());
				}
				return m;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("载入医学表达式错误!", e);
		} catch (SQLException e) {
			throw new ModelDataOperationException("载入医学表达式错误!", e);
		} catch (IOException e) {
			throw new ModelDataOperationException("载入医学表达式错误!", e);
		}
	}

	public Map<String, Object> doLoadTemplateData(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		int step = (Integer) body.get("step");
		Object zyh = (Integer) body.get("ZYH");
		Integer bllx = null;
		if (body.containsKey("BLLX")) {
			bllx = Integer.parseInt(body.get("BLLX") + "");
		}
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			if (step == 1) {// 新建
				List<Map<String, Object>> ret = dao
						.doSqlQuery(
								"select  PARAEXTEND as PARAEXTEND from PARAMETER where PARANAME='EmrEdit_v3'",
								parameters);
				if (ret.size() > 0) {
					Map<String, Object> map = ret.get(0);
					Blob blob = (Blob) map.get("PARAEXTEND");
					InputStream is = blob.getBinaryStream();
					Reader utf8_reader = new InputStreamReader(is, "GBK");
					StringBuffer utf8Text = new StringBuffer();
					int charValue = 0;
					while ((charValue = utf8_reader.read()) != -1) {
						utf8Text.append((char) charValue);
					}
					res.put("docText", utf8Text.toString());
				}
			} else if (step == 2) {// 加载EMR_BL02数据
				// parameters.put("CHTCODE", 10000689);
				Object lastSign = body.get("lastSign");
				Map<String, Object> curBl01 = null;
				Long blbh = Long.parseLong(body.get("BLBH").toString());
				if (bllx == 1) {// 病程
					String cnd = "['and',['and',['and',['and',['eq',['$','a.BLLX'],['i',1]],['eq',['$','a.JZXH'],['d',"
							+ zyh
							+ "]]],['ne',['$','a.BLZT'],['i',9]]],['eq',['$','a.BLLB'],['i',"
							+ body.get("BLLB")
							+ "]]],['ne',['$','a.DLLB'],['i',-1]]]";
					List<Map<String, Object>> bl01s = dao.doList(
							CNDHelper.toListCnd(cnd),
							"a.BLLB ASC,a.JLSJ ASC,a.BLBH ASC",
							"phis.application.emr.schemas.EMR_BL01_BC");
					if (bl01s.size() <= 0) {
						res.put("BL01List", bl01s);
						return res;
					}
					int curLine = -1;
					int nullLine = -1;
					if (lastSign != null && "1".equals(lastSign.toString())) {
						curLine = bl01s.size();
						curBl01 = bl01s.get(curLine - 1);
					} else {
						for (int i = 1; i < bl01s.size() + 1; i++) {
							Map<String, Object> bl01 = bl01s.get(i - 1);
							if (curLine == -1
									&& Long.parseLong(bl01.get("BLBH")
											.toString()) == blbh) {
								curLine = i;
								curBl01 = bl01;
							}
							if (nullLine == -1
									&& (bl01.get("YMJL") == null || bl01
											.get("YMJL").toString().trim()
											.length() == 0)) {
								nullLine = i;
							}
						}
						if (nullLine != -1 && curLine == nullLine) {// 当前打开行YMJL为空
							curLine = curLine - 1;
						} else if (curLine > nullLine && nullLine != -1) {
							curLine = nullLine;
						}
					}
					// int firstLine = curLine > nullLine ? nullLine : curLine;
					// 参数值从前N份打开
					String N = ParameterUtil.getParameter(manageUnit,
							BSPHISSystemArgument.QNYDK, ctx);
					if (N == null || !isNumeric(N)) {
						throw new ModelDataOperationException("病程参数设置错误!");
					}
					if (Integer.parseInt(N) <= 0) {
						throw new ModelDataOperationException(
								"病程参数从前N份打开的值必须大于0!");
					}
					curLine = (curLine - Integer.parseInt(N)) > 0 ? (curLine - Integer
							.parseInt(N)) : 0;
					bl01s = bl01s.subList(curLine, bl01s.size());
					res.put("BL01List", bl01s);
					Map<String, Object> bl02Map = new HashMap<String, Object>();
					for (Map<String, Object> bl01 : bl01s) {
						Map<String, Object> map = dao.doLoad(
								BSPHISEntryNames.EMR_BL02, bl01.get("BLBH"));
						Blob blnr = (Blob) map.get("BLNR");
						InputStream is = blnr.getBinaryStream();
						Reader utf8_reader = new InputStreamReader(is, "UTF-8");
						StringBuffer blnrBuf = new StringBuffer();
						int charValue = 0;
						while ((charValue = utf8_reader.read()) != -1) {
							blnrBuf.append((char) charValue);
						}
						Blob blht = (Blob) map.get("BLHT");
						InputStream is_blht = blht.getBinaryStream();
						Reader blht_reader = new InputStreamReader(is_blht,
								"UTF-8");
						StringBuffer blhtBuf = new StringBuffer();
						charValue = 0;
						while ((charValue = blht_reader.read()) != -1) {
							blhtBuf.append((char) charValue);
						}
						map.put("BLNR", blnrBuf.toString());
						map.put("BLHT", blhtBuf.toString());
						if (map != null) {
							bl02Map.put("BLBH_" + bl01.get("BLBH"), map);
						}
					}
					res.put("BL02Map", bl02Map);
				} else {
					parameters.put("BLBH", blbh);
					Map<String, Object> bl01 = dao.doLoad(
							BSPHISEntryNames.EMR_BL01, blbh);
					curBl01 = bl01;
					res.put("BL01", bl01);
					List<Map<String, Object>> bl02 = dao
							.doSqlQuery(
									"select BLNR as BLNR,BLBH,BLHT,JGID from EMR_BL02 where BLBH=:BLBH",
									parameters);
					if (bl02.size() > 0) {
						Map<String, Object> map = bl02.get(0);
						Blob blnr = (Blob) map.get("BLNR");
						InputStream is = blnr.getBinaryStream();
						Reader utf8_reader = new InputStreamReader(is, "UTF-8");
						StringBuffer blnrBuf = new StringBuffer();
						int charValue = 0;
						while ((charValue = utf8_reader.read()) != -1) {
							blnrBuf.append((char) charValue);
						}
						Blob blht = (Blob) map.get("BLHT");
						InputStream is_blht = blht.getBinaryStream();
						Reader blht_reader = new InputStreamReader(is_blht,
								"UTF-8");
						StringBuffer blhtBuf = new StringBuffer();
						charValue = 0;
						while ((charValue = blht_reader.read()) != -1) {
							blhtBuf.append((char) charValue);
						}
						map.put("BLNR", blnrBuf.toString());
						map.put("BLHT", blhtBuf.toString());
						res.put("BL02", map);
					}
				}
				if ("1".equals((body.get("BLLX") == null ? "" : body
						.get("BLLX").toString()))) {
					parameters.clear();
					parameters.put("ZYH",
							Long.parseLong(body.get("ZYH").toString()));
					List<Map<String, Object>> bl01List = dao
							.doQuery(
									"select BLBH as BLBH from EMR_BL01 where JZXH=:ZYH and DLLB=-1 and DLJ='-HdrFtr' and BLZT<>9",
									parameters);
					if (bl01List.size() <= 0) {
						throw new ModelDataOperationException("未找到有效的病程页眉页脚信息!");
					}
					parameters.clear();
					parameters.put("BLBH", bl01List.get(0).get("BLBH"));
					List<Map<String, Object>> bl02_ymyj = dao
							.doSqlQuery(
									"select a.BLNR as BLNR,a.BLBH,a.BLHT,a.JGID from EMR_BL02 a where a.BLBH=:BLBH",
									parameters);
					if (bl02_ymyj.size() > 0) {
						Map<String, Object> map = bl02_ymyj.get(0);
						Blob blnr = (Blob) map.get("BLNR");
						InputStream is = blnr.getBinaryStream();
						Reader utf8_reader = new InputStreamReader(is, "UTF-8");
						StringBuffer blnrBuf = new StringBuffer();
						int charValue = 0;
						while ((charValue = utf8_reader.read()) != -1) {
							blnrBuf.append((char) charValue);
						}
						Blob blht = (Blob) map.get("BLNR");
						InputStream is_blht = blht.getBinaryStream();
						Reader blht_reader = new InputStreamReader(is_blht,
								"UTF-8");
						StringBuffer blhtBuf = new StringBuffer();
						charValue = 0;
						while ((charValue = blht_reader.read()) != -1) {
							blhtBuf.append((char) charValue);
						}
						map.put("BLNR", blnrBuf.toString());
						map.put("BLHT", blhtBuf.toString());
						res.put("BL02_YMYJ", map);
					}
					// 加载页眉元素
					StringBuffer ymysStr = new StringBuffer();
					String cnd = "['eq',['$','JZXH'],['d'," + zyh
							+ "]],['eq',['$','BLLB'],['d'," + body.get("BLLB")
							+ "]]";
					List<Map<String, Object>> ymysList = dao.doQuery(
							CNDHelper.toListCnd(cnd), null,
							BSPHISEntryNames.EMR_BLYMYS);
					// 组装页眉元素
					for (Map<String, Object> ymys : ymysList) {
						ymysStr.append(ymys.get("DLMC") + "#10"
								+ ymys.get("YSMC") + "#10" + ymys.get("XSMC")
								+ "#10" + ymys.get("YSLX") + "#9");
					}
					res.put("ymysText", ymysStr.toString());
				}
				// 保存操作日志
				ss.beginTransaction();
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("RZNR", curBl01.get("BLMC"));
				record.put("YWID1", curBl01.get("JZXH"));
				record.put("YWID2", blbh);
				record.put("YEID3", blbh);
				BSPEMRUtil
						.doSaveEmrOpLog(BSPEMRUtil.OP_VISIT, record, dao, ctx);
				ss.flush();
				ss.getTransaction().commit();
			} else if (step == 3) {// 模版
				Object chtCode = body.get("CHTCODE");
				Object Mblb = body.get("MBLB");
				if (chtCode == null || chtCode.toString().equals("")) {// 页眉页脚
					Object bllb = body.get("BLLB");
					Object ksdm = body.get("KSDM");
					Map<String, Object> params = new HashMap<String, Object>();
					String filterSql = "";
					if (ksdm != null && ksdm.toString().equals("-1")) {
						filterSql = "";
					} else {
						filterSql = "a.KSDM =:KSDM AND";
						params.put("KSDM", ksdm);
					}
					String sql = "Select a.MBBH as MBBH From EMR_KSMB_YMYJ a Where "
							+ filterSql
							+ " a.JGID=:JGID AND Exists (Select b.BLLB From V_EMR_BLMB b Where a.MBBH = b.MBBH And b.MBFL = 1 And b.TYBZ <> 1 And ZYMZ = 0 And b.BLLB =:BLLB)";

					params.put("BLLB", bllb);
					params.put("JGID", manageUnit);
					List<Map<String, Object>> list = dao
							.doSqlQuery(sql, params);
					if (list.size() == 0) {
						throw new ModelDataOperationException(
								"当前科室没有配置病程的页眉页脚信息!");
					}
					chtCode = list.get(0).get("MBBH");
					res.put("ymyjMbbh", chtCode);
				}
				parameters.put("CHTCODE", Integer.parseInt(chtCode.toString()));
				String sql = null;
				if (Mblb != null && Mblb.toString().equals("1")) {
					if ("1".equals((body.get("BLLX") == null ? "" : body.get(
							"BLLX").toString()))) {
						// 病程流程
						sql = "select  CHRTXMLTEXTTEXT as XMLTEXTPAT from CHRECORDTEMPLATE where CHRTCODE= :CHTCODE";
					} else {
						sql = "select  XMLTEXTPAT as XMLTEXTPAT from CHTEMPLATE where CHTCODE= :CHTCODE";
					}
					List<Map<String, Object>> ret = dao.doSqlQuery(sql,
							parameters);
					if (ret.size() > 0) {
						Map<String, Object> map = ret.get(0);
						Blob blob = (Blob) map.get("XMLTEXTPAT");
						InputStream is = blob.getBinaryStream();
						Reader utf8_reader = new InputStreamReader(is, "UTF-8");
						StringBuffer utf8Text = new StringBuffer();
						int charValue = 0;
						while ((charValue = utf8_reader.read()) != -1) {
							utf8Text.append((char) charValue);
						}
						InputStream gbk_is = blob.getBinaryStream();
						Reader gbk_reader = new InputStreamReader(gbk_is, "GBK");
						StringBuffer gbkText = new StringBuffer();
						charValue = 0;
						while ((charValue = gbk_reader.read()) != -1) {
							gbkText.append((char) charValue);
						}
						res.put("gbkText", gbkText.toString());
						res.put("uft8Text", utf8Text.toString());
					}
				} else {
					sql = "select  PTTEMPLATE as PTTEMPLATE,PTTEMPLATETEXT as PTTEMPLATETEXT from PRIVATETEMPLATE where PTID= :CHTCODE";
					List<Map<String, Object>> ret = dao.doSqlQuery(sql,
							parameters);
					if (ret.size() > 0) {
						Map<String, Object> map = ret.get(0);
						Blob blob = (Blob) map.get("PTTEMPLATETEXT");
						int charValue = 0;
						StringBuffer gbkText = new StringBuffer();
						if (blob != null) {
							InputStream is = blob.getBinaryStream();
							Reader gbk_reader = new InputStreamReader(is, "GBK");
							while ((charValue = gbk_reader.read()) != -1) {
								gbkText.append((char) charValue);
							}
						}
						Blob pt_blob = (Blob) map.get("PTTEMPLATE");
						StringBuffer utf8Text = new StringBuffer();
						charValue = 0;
						if (pt_blob != null) {
							InputStream gbk_is = pt_blob.getBinaryStream();
							Reader utf8_reader = new InputStreamReader(gbk_is,
									"GBK");
							while ((charValue = utf8_reader.read()) != -1) {
								utf8Text.append((char) charValue);
							}
						}
						res.put("uft8Text", gbkText.toString());
						res.put("gbkText", utf8Text.toString());
					}
				}

			} else if (step == 4) {// 病程页眉元素
				Object bllb = body.get("BLLB");
				Object ksdm = body.get("KSDM");
				Map<String, Object> params = new HashMap<String, Object>();
				String filterSql = "";
				if (ksdm != null && ksdm.toString().equals("-1")) {
					filterSql = "";
				} else {
					filterSql = "a.KSDM =:KSDM AND";
					params.put("KSDM", ksdm);
				}
				String sql = "Select a.MBBH as MBBH From EMR_KSMB_YMYJ a Where "
						+ filterSql
						+ " a.JGID=:JGID AND Exists (Select b.BLLB From V_EMR_BLMB b Where a.MBBH = b.MBBH And b.MBFL = 1 And b.TYBZ <> 1 And ZYMZ = 0 And b.BLLB =:BLLB)";
				params.put("BLLB", bllb);
				params.put("JGID", manageUnit);
				List<Map<String, Object>> list = dao.doSqlQuery(sql, params);
				if (list.size() == 0) {
					throw new ModelDataOperationException("当前科室没有配置病程的页面页脚信息!");
				}
				Object mbbh = list.get(0).get("MBBH");
				parameters.put("MBBH", Integer.parseInt(mbbh.toString()));
				List<Map<String, Object>> ymyspzList = dao
						.doQuery(
								"select YSMC as YSMC,YSLX as YSLX from EMR_KBM_YMYSPZ where MBBH=:MBBH",
								parameters);
				if (ymyspzList.size() <= 0) {
					// remove by yangl 去掉提示
					// throw new
					// ModelDataOperationException("页眉元素没有配置，请先配置页眉元素!");
				}
				Map<String, Map<String, Object>> cache = cacheRefItems(zyh,
						null);
				StringBuffer ymysStr = new StringBuffer();
				String cnd = "['eq',['$','JZXH'],['d'," + zyh
						+ "]],['eq',['$','BLLB'],['d'," + body.get("BLLB")
						+ "]]";
				List<Map<String, Object>> ymysList = dao.doQuery(
						CNDHelper.toListCnd(cnd), null,
						BSPHISEntryNames.EMR_BLYMYS);
				// 组装页眉元素
				for (Map<String, Object> ymys : ymysList) {
					ymysStr.append(ymys.get("DLMC") + "#10" + ymys.get("YSMC")
							+ "#10" + ymys.get("XSMC") + "#10"
							+ ymys.get("YSLX") + "#9");
				}
				StringBuffer ymysStr_new = new StringBuffer();
				for (Map<String, Object> m : ymyspzList) {
					String ysmc = (String) m.get("YSMC");
					Map<String, Object> refEle = dao.doLoad(
							BSPHISEntryNames.REFERENCEELEMENT, ysmc);
					if (refEle == null) {
						throw new ModelDataOperationException(
								"配置的页眉模板元素错误,请立刻处理!");
					}
					String refXmlText = (String) refEle.get("REFXMLTEXT");
					String[] textArray = refXmlText.split("＃");
					if (textArray.length < 2) {
						throw new ModelDataOperationException(
								"配置的页眉模板元素错误,请立刻处理!");
					}
					Map<String, Object> refMap = new HashMap<String, Object>();
					refMap.put("Ref",
							textArray[1] + "|" + refEle.get("QUERYNAME"));
					if (refEle.get("REFDIC") != null
							&& refEle.get("REFDIC").toString().equals("1")) {
						refMap.put(
								"Dict",
								refEle.get("DICNAME") + "|"
										+ refEle.get("DICCODE") + "|"
										+ refEle.get("DICDESC"));
					} else {
						refMap.put("Dict", "");
					}
					refMap.put("Fmt", "");
					analyzeRefMap(refMap, cache, false, ctx);
					Object value = refMap.get("Value");
					ymysStr_new.append("element..Illrc_1_" + body.get("BLBH")
							+ "#10" + ysmc + "#10" + value + "#10"
							+ m.get("YSLX") + "#9");

				}
				ymysStr.append(ymysStr_new);
				res.put("ymysText_new", ymysStr_new);
				res.put("ymysText", ymysStr.toString());
			} else if (step == 5) {

			}
		} catch (Exception e) {
			throw new ModelDataOperationException(e.getMessage(), e);
		}
		return res;
	}

	public Map<String, Object> doLoadMZTemplateData(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		int step = (Integer) body.get("step");
		Object zyh = (Integer) body.get("ZYH");
		Integer bllx = null;
		if (body.containsKey("BLLX")) {
			bllx = Integer.parseInt(body.get("BLLX") + "");
		}
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			if (step == 1) {// 新建
				List<Map<String, Object>> ret = dao
						.doSqlQuery(
								"select  PARAEXTEND as PARAEXTEND from PARAMETER where PARANAME='EmrEdit_v3'",
								parameters);
				if (ret.size() > 0) {
					Map<String, Object> map = ret.get(0);
					Blob blob = (Blob) map.get("PARAEXTEND");
					InputStream is = blob.getBinaryStream();
					Reader utf8_reader = new InputStreamReader(is, "GBK");
					StringBuffer utf8Text = new StringBuffer();
					int charValue = 0;
					while ((charValue = utf8_reader.read()) != -1) {
						utf8Text.append((char) charValue);
					}
					res.put("docText", utf8Text.toString());
				}
			} else if (step == 2) {// 加载EMR_BL02数据
				// parameters.put("CHTCODE", 10000689);
				Object lastSign = body.get("lastSign");
				Map<String, Object> curBl01 = null;
				Long blbh = Long.parseLong(body.get("BLBH").toString());
				if (bllx == 1) {// 病程
					String cnd = "['and',['and',['and',['and',['eq',['$','a.BLLX'],['i',1]],['eq',['$','a.JZXH'],['d',"
							+ zyh
							+ "]]],['ne',['$','a.BLZT'],['i',9]]],['eq',['$','a.BLLB'],['i',"
							+ body.get("BLLB")
							+ "]]],['ne',['$','a.DLLB'],['i',-1]]]";
					List<Map<String, Object>> bl01s = dao.doList(
							CNDHelper.toListCnd(cnd),
							"a.BLLB ASC,a.JLSJ ASC,a.BLBH ASC",
							"phis.application.emr.schemas.OMR_BL01_BC");
					if (bl01s.size() <= 0) {
						res.put("BL01List", bl01s);
						return res;
					}
					int curLine = -1;
					int nullLine = -1;
					if (lastSign != null && "1".equals(lastSign.toString())) {
						curLine = bl01s.size();
						curBl01 = bl01s.get(curLine - 1);
					} else {
						for (int i = 1; i < bl01s.size() + 1; i++) {
							Map<String, Object> bl01 = bl01s.get(i - 1);
							if (curLine == -1
									&& Long.parseLong(bl01.get("BLBH")
											.toString()) == blbh) {
								curLine = i;
								curBl01 = bl01;
							}
							if (nullLine == -1
									&& (bl01.get("YMJL") == null || bl01
											.get("YMJL").toString().trim()
											.length() == 0)) {
								nullLine = i;
							}
						}
						if (nullLine != -1 && curLine == nullLine) {// 当前打开行YMJL为空
							curLine = curLine - 1;
						} else if (curLine > nullLine && nullLine != -1) {
							curLine = nullLine;
						}
					}
					// int firstLine = curLine > nullLine ? nullLine : curLine;
					// 参数值从前N份打开
					String N = ParameterUtil.getParameter(manageUnit,
							BSPHISSystemArgument.QNYDK, ctx);
					if (N == null || !isNumeric(N)) {
						throw new ModelDataOperationException("病程参数设置错误!");
					}
					if (Integer.parseInt(N) <= 0) {
						throw new ModelDataOperationException(
								"病程参数从前N份打开的值必须大于0!");
					}
					curLine = (curLine - Integer.parseInt(N)) > 0 ? (curLine - Integer
							.parseInt(N)) : 0;
					bl01s = bl01s.subList(curLine, bl01s.size());
					res.put("BL01List", bl01s);
					Map<String, Object> bl02Map = new HashMap<String, Object>();
					for (Map<String, Object> bl01 : bl01s) {
						Map<String, Object> map = dao.doLoad(
								BSPHISEntryNames.OMR_BL02, bl01.get("BLBH"));
						Blob blnr = (Blob) map.get("BLNR");
						InputStream is = blnr.getBinaryStream();
						Reader utf8_reader = new InputStreamReader(is, "UTF-8");
						StringBuffer blnrBuf = new StringBuffer();
						int charValue = 0;
						while ((charValue = utf8_reader.read()) != -1) {
							blnrBuf.append((char) charValue);
						}
						Blob blht = (Blob) map.get("BLHT");
						InputStream is_blht = blht.getBinaryStream();
						Reader blht_reader = new InputStreamReader(is_blht,
								"UTF-8");
						StringBuffer blhtBuf = new StringBuffer();
						charValue = 0;
						while ((charValue = blht_reader.read()) != -1) {
							blhtBuf.append((char) charValue);
						}
						map.put("BLNR", blnrBuf.toString());
						map.put("BLHT", blhtBuf.toString());
						if (map != null) {
							bl02Map.put("BLBH_" + bl01.get("BLBH"), map);
						}
					}
					res.put("BL02Map", bl02Map);
				} else {
					parameters.put("BLBH", blbh);
					Map<String, Object> bl01 = dao.doLoad(
							BSPHISEntryNames.OMR_BL01, blbh);
					curBl01 = bl01;
					res.put("BL01", bl01);
					List<Map<String, Object>> bl02 = dao
							.doSqlQuery(
									"select BLNR as BLNR,BLBH,BLHT,JGID from OMR_BL02 where BLBH=:BLBH",
									parameters);
					if (bl02.size() > 0) {
						Map<String, Object> map = bl02.get(0);
						Blob blnr = (Blob) map.get("BLNR");
						InputStream is = blnr.getBinaryStream();
						Reader utf8_reader = new InputStreamReader(is, "UTF-8");
						StringBuffer blnrBuf = new StringBuffer();
						int charValue = 0;
						while ((charValue = utf8_reader.read()) != -1) {
							blnrBuf.append((char) charValue);
						}
						Blob blht = (Blob) map.get("BLHT");
						InputStream is_blht = blht.getBinaryStream();
						Reader blht_reader = new InputStreamReader(is_blht,
								"UTF-8");
						StringBuffer blhtBuf = new StringBuffer();
						charValue = 0;
						while ((charValue = blht_reader.read()) != -1) {
							blhtBuf.append((char) charValue);
						}
						map.put("BLNR", blnrBuf.toString());
						map.put("BLHT", blhtBuf.toString());
						res.put("BL02", map);
					}
				}
				if ("1".equals((body.get("BLLX") == null ? "" : body
						.get("BLLX").toString()))) {
					parameters.clear();
					parameters.put("ZYH",
							Long.parseLong(body.get("ZYH").toString()));
					List<Map<String, Object>> bl01List = dao
							.doQuery(
									"select BLBH as BLBH from OMR_BL01 where JZXH=:ZYH and DLLB=-1 and DLJ='-HdrFtr' and BLZT<>9",
									parameters);
					if (bl01List.size() <= 0) {
						throw new ModelDataOperationException("未找到有效的病程页眉页脚信息!");
					}
					parameters.clear();
					parameters.put("BLBH", bl01List.get(0).get("BLBH"));
					List<Map<String, Object>> bl02_ymyj = dao
							.doSqlQuery(
									"select a.BLNR as BLNR,a.BLBH,a.BLHT,a.JGID from OMR_BL02 a where a.BLBH=:BLBH",
									parameters);
					if (bl02_ymyj.size() > 0) {
						Map<String, Object> map = bl02_ymyj.get(0);
						Blob blnr = (Blob) map.get("BLNR");
						InputStream is = blnr.getBinaryStream();
						Reader utf8_reader = new InputStreamReader(is, "UTF-8");
						StringBuffer blnrBuf = new StringBuffer();
						int charValue = 0;
						while ((charValue = utf8_reader.read()) != -1) {
							blnrBuf.append((char) charValue);
						}
						Blob blht = (Blob) map.get("BLNR");
						InputStream is_blht = blht.getBinaryStream();
						Reader blht_reader = new InputStreamReader(is_blht,
								"UTF-8");
						StringBuffer blhtBuf = new StringBuffer();
						charValue = 0;
						while ((charValue = blht_reader.read()) != -1) {
							blhtBuf.append((char) charValue);
						}
						map.put("BLNR", blnrBuf.toString());
						map.put("BLHT", blhtBuf.toString());
						res.put("BL02_YMYJ", map);
					}
					// 加载页眉元素
					StringBuffer ymysStr = new StringBuffer();
					String cnd = "['eq',['$','JZXH'],['d'," + zyh
							+ "]],['eq',['$','BLLB'],['d'," + body.get("BLLB")
							+ "]]";
					List<Map<String, Object>> ymysList = dao.doQuery(
							CNDHelper.toListCnd(cnd), null,
							BSPHISEntryNames.EMR_BLYMYS);
					// 组装页眉元素
					for (Map<String, Object> ymys : ymysList) {
						ymysStr.append(ymys.get("DLMC") + "#10"
								+ ymys.get("YSMC") + "#10" + ymys.get("XSMC")
								+ "#10" + ymys.get("YSLX") + "#9");
					}
					res.put("ymysText", ymysStr.toString());
				}
				// 保存操作日志
				ss.beginTransaction();
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("RZNR", curBl01.get("BLMC"));
				record.put("YWID1", curBl01.get("JZXH"));
				record.put("YWID2", blbh);
				record.put("YEID3", blbh);
				BSPEMRUtil
						.doSaveEmrOpLog(BSPEMRUtil.OP_VISIT, record, dao, ctx);
				ss.flush();
				ss.getTransaction().commit();
			} else if (step == 3) {// 模版
				Object chtCode = body.get("CHTCODE");
				Object Mblb = body.get("MBLB");
				if (chtCode == null || chtCode.toString().equals("")) {// 页眉页脚
					Object bllb = body.get("BLLB");
					Object ksdm = body.get("KSDM");
					Map<String, Object> params = new HashMap<String, Object>();
					String filterSql = "";
					if (ksdm != null && ksdm.toString().equals("-1")) {
						filterSql = "";
					} else {
						filterSql = "a.KSDM =:KSDM AND";
						params.put("KSDM", ksdm);
					}
					String sql = "Select a.MBBH as MBBH From EMR_KSMB_YMYJ a Where "
							+ filterSql
							+ " a.JGID=:JGID AND Exists (Select b.BLLB From V_EMR_BLMB b Where a.MBBH = b.MBBH And b.MBFL = 1 And b.TYBZ <> 1 And ZYMZ = 0 And b.BLLB =:BLLB)";

					params.put("BLLB", bllb);
					params.put("JGID", manageUnit);
					List<Map<String, Object>> list = dao
							.doSqlQuery(sql, params);
					if (list.size() == 0) {
						throw new ModelDataOperationException(
								"当前科室没有配置病程的页眉页脚信息!");
					}
					chtCode = list.get(0).get("MBBH");
					res.put("ymyjMbbh", chtCode);
				}
				parameters.put("CHTCODE", Integer.parseInt(chtCode.toString()));
				String sql = null;
				if (Mblb != null && Mblb.toString().equals("1")) {
					if ("1".equals((body.get("BLLX") == null ? "" : body.get(
							"BLLX").toString()))) {
						// 病程流程
						sql = "select  CHRTXMLTEXTTEXT as XMLTEXTPAT from CHRECORDTEMPLATE where CHRTCODE= :CHTCODE";
					} else {
						sql = "select  XMLTEXTPAT as XMLTEXTPAT from CHTEMPLATE where CHTCODE= :CHTCODE";
					}
					List<Map<String, Object>> ret = dao.doSqlQuery(sql,
							parameters);
					if (ret.size() > 0) {
						Map<String, Object> map = ret.get(0);
						Blob blob = (Blob) map.get("XMLTEXTPAT");
						InputStream is = blob.getBinaryStream();
						Reader utf8_reader = new InputStreamReader(is, "UTF-8");
						StringBuffer utf8Text = new StringBuffer();
						int charValue = 0;
						while ((charValue = utf8_reader.read()) != -1) {
							utf8Text.append((char) charValue);
						}
						InputStream gbk_is = blob.getBinaryStream();
						Reader gbk_reader = new InputStreamReader(gbk_is, "GBK");
						StringBuffer gbkText = new StringBuffer();
						charValue = 0;
						while ((charValue = gbk_reader.read()) != -1) {
							gbkText.append((char) charValue);
						}
						res.put("gbkText", gbkText.toString());
						res.put("uft8Text", utf8Text.toString());
					}
				} else {
					sql = "select  PTTEMPLATE as PTTEMPLATE,PTTEMPLATETEXT as PTTEMPLATETEXT from PRIVATETEMPLATE where PTID= :CHTCODE";
					List<Map<String, Object>> ret = dao.doSqlQuery(sql,
							parameters);
					if (ret.size() > 0) {
						Map<String, Object> map = ret.get(0);
						Blob blob = (Blob) map.get("PTTEMPLATETEXT");
						int charValue = 0;
						StringBuffer gbkText = new StringBuffer();
						if (blob != null) {
							InputStream is = blob.getBinaryStream();
							Reader gbk_reader = new InputStreamReader(is, "GBK");
							while ((charValue = gbk_reader.read()) != -1) {
								gbkText.append((char) charValue);
							}
						}
						Blob pt_blob = (Blob) map.get("PTTEMPLATE");
						StringBuffer utf8Text = new StringBuffer();
						charValue = 0;
						if (pt_blob != null) {
							InputStream gbk_is = pt_blob.getBinaryStream();
							Reader utf8_reader = new InputStreamReader(gbk_is,
									"GBK");
							while ((charValue = utf8_reader.read()) != -1) {
								utf8Text.append((char) charValue);
							}
						}
						res.put("uft8Text", gbkText.toString());
						res.put("gbkText", utf8Text.toString());
					}
				}

			} else if (step == 4) {// 病程页眉元素
				Object bllb = body.get("BLLB");
				Object ksdm = body.get("KSDM");
				Map<String, Object> params = new HashMap<String, Object>();
				String filterSql = "";
				if (ksdm != null && ksdm.toString().equals("-1")) {
					filterSql = "";
				} else {
					filterSql = "a.KSDM =:KSDM AND";
					params.put("KSDM", ksdm);
				}
				String sql = "Select a.MBBH as MBBH From EMR_KSMB_YMYJ a Where "
						+ filterSql
						+ " a.JGID=:JGID AND Exists (Select b.BLLB From V_EMR_BLMB b Where a.MBBH = b.MBBH And b.MBFL = 1 And b.TYBZ <> 1 And ZYMZ = 0 And b.BLLB =:BLLB)";
				params.put("BLLB", bllb);
				params.put("JGID", manageUnit);
				List<Map<String, Object>> list = dao.doSqlQuery(sql, params);
				if (list.size() == 0) {
					throw new ModelDataOperationException("当前科室没有配置病程的页面页脚信息!");
				}
				Object mbbh = list.get(0).get("MBBH");
				parameters.put("MBBH", Integer.parseInt(mbbh.toString()));
				List<Map<String, Object>> ymyspzList = dao
						.doQuery(
								"select YSMC as YSMC,YSLX as YSLX from EMR_KBM_YMYSPZ where MBBH=:MBBH",
								parameters);
				if (ymyspzList.size() <= 0) {
					throw new ModelDataOperationException("页眉元素没有配置，请先配置页眉元素!");
				}
				Map<String, Map<String, Object>> cache = cacheRefItems(zyh,
						null);
				StringBuffer ymysStr = new StringBuffer();
				String cnd = "['eq',['$','JZXH'],['d'," + zyh
						+ "]],['eq',['$','BLLB'],['d'," + body.get("BLLB")
						+ "]]";
				List<Map<String, Object>> ymysList = dao.doQuery(
						CNDHelper.toListCnd(cnd), null,
						BSPHISEntryNames.EMR_BLYMYS);
				// 组装页眉元素
				for (Map<String, Object> ymys : ymysList) {
					ymysStr.append(ymys.get("DLMC") + "#10" + ymys.get("YSMC")
							+ "#10" + ymys.get("XSMC") + "#10"
							+ ymys.get("YSLX") + "#9");
				}
				StringBuffer ymysStr_new = new StringBuffer();
				for (Map<String, Object> m : ymyspzList) {
					String ysmc = (String) m.get("YSMC");
					Map<String, Object> refEle = dao.doLoad(
							BSPHISEntryNames.REFERENCEELEMENT, ysmc);
					if (refEle == null) {
						throw new ModelDataOperationException(
								"配置的页眉模板元素错误,请立刻处理!");
					}
					String refXmlText = (String) refEle.get("REFXMLTEXT");
					String[] textArray = refXmlText.split("＃");
					if (textArray.length < 2) {
						throw new ModelDataOperationException(
								"配置的页眉模板元素错误,请立刻处理!");
					}
					Map<String, Object> refMap = new HashMap<String, Object>();
					refMap.put("Ref",
							textArray[1] + "|" + refEle.get("QUERYNAME"));
					if (refEle.get("REFDIC") != null
							&& refEle.get("REFDIC").toString().equals("1")) {
						refMap.put(
								"Dict",
								refEle.get("DICNAME") + "|"
										+ refEle.get("DICCODE") + "|"
										+ refEle.get("DICDESC"));
					} else {
						refMap.put("Dict", "");
					}
					refMap.put("Fmt", "");
					analyzeRefMap(refMap, cache, false, ctx);
					Object value = refMap.get("Value");
					ymysStr_new.append("element..Illrc_1_" + body.get("BLBH")
							+ "#10" + ysmc + "#10" + value + "#10"
							+ m.get("YSLX") + "#9");

				}
				ymysStr.append(ymysStr_new);
				res.put("ymysText_new", ymysStr_new);
				res.put("ymysText", ymysStr.toString());
			} else if (step == 5) {

			}
		} catch (Exception e) {
			throw new ModelDataOperationException(e.getMessage(), e);
		}
		return res;
	}

	public Map<String, Object> doLoadEmrEditorData(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		try {
			Map<String, Object> res = new HashMap<String, Object>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("CHTCODE", body.get("CHTCODE"));
			String sql = null;
			if ("1".equals((body.get("BLLX") == null ? "" : body.get("BLLX")
					.toString()))) {
				// 病程流程
				sql = "select  CHRTXMLTEXTTEXT as XMLTEXTPAT from CHRECORDTEMPLATE where CHRTCODE= :CHTCODE";
			} else {
				sql = "select  XMLTEXTPAT as XMLTEXTPAT from CHTEMPLATE where CHTCODE= :CHTCODE";
			}
			List<Map<String, Object>> ret = dao.doSqlQuery(sql, params);
			if (ret.size() > 0) {
				Map<String, Object> map = ret.get(0);
				Blob blob = (Blob) map.get("XMLTEXTPAT");
				InputStream gbk_is = blob.getBinaryStream();
				Reader gbk_reader = new InputStreamReader(gbk_is, "GBK");
				StringBuffer gbkText = new StringBuffer();
				int charValue = 0;
				while ((charValue = gbk_reader.read()) != -1) {
					gbkText.append((char) charValue);
				}
				res.put("gbkText", gbkText.toString());
			}
			return res;
		} catch (Exception e) {
			throw new ModelDataOperationException("获取模版数据错误!无法切换到编辑模式.", e);
		}
	}

	public Long getBl01Key(Context ctx) throws KeyManagerException {

		String key = "";
		try {
			Schema sc = SchemaController.instance().get(
					BSPHISEntryNames.EMR_BL01 + "_BLBH");

			List<SchemaItem> items = sc.getItems();
			SchemaItem item = null;
			for (SchemaItem sit : items) {
				if ("BLBH".equals(sit.getId())) {
					item = sit;
					break;
				}
			}
			key = KeyManager.getKeyByName("EMR_BL01", item.getKeyRules(),
					item.getId(), ctx);
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Long.parseLong(key);
	}

	public Long getOMR_Bl01Key(Context ctx) throws KeyManagerException {

		String key = "";
		try {
			Schema sc = SchemaController.instance().get(
					BSPHISEntryNames.OMR_BL01 + "_BLBH");

			List<SchemaItem> items = sc.getItems();
			SchemaItem item = null;
			for (SchemaItem sit : items) {
				if ("BLBH".equals(sit.getId())) {
					item = sit;
					break;
				}
			}
			key = KeyManager.getKeyByName("OMR_BL01", item.getKeyRules(),
					item.getId(), ctx);
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Long.parseLong(key);
	}

	/**
	 * 获取医学表达式主键
	 * 
	 * @param ctx
	 * @return
	 * @throws KeyManagerException
	 */
	public Long getYXBDSKey(Context ctx) throws KeyManagerException {
		String key = "";
		try {
			Schema sc = SchemaController.instance().get(
					BSPHISEntryNames.EMR_YXBDS_BL + "_KEY");
			List<SchemaItem> items = sc.getItems();
			SchemaItem item = null;
			for (SchemaItem sit : items) {
				if ("BLBDSBH".equals(sit.getId())) {
					item = sit;
					break;
				}
			}
			key = KeyManager.getKeyByName("EMR_YXBDS_BL", item.getKeyRules(),
					item.getId(), ctx);
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Long.parseLong(key);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> doSaveEmrData(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> BL01 = (Map<String, Object>) body.get("BL01");
		Map<String, Object> BL02 = (Map<String, Object>) body.get("BL02");
		Map<String, Object> BL03 = (Map<String, Object>) body.get("BL03");
		Map<String, Object> Node = (Map<String, Object>) body.get("Node");
		Long openBlbh = Long.parseLong((body.get("openBlbh") == null ? "-1"
				: body.get("openBlbh").toString()));// 修改标题后重载的BLBH
		Long blbh = Long.parseLong(BL01.get("BLBH").toString());
		boolean reload = false;
		String op = "create";
		try {
			// 判断是否单一文档，若是，判断是否已经存在
			Integer dywd = (Integer) Node.get("DYWD");
			if (dywd == 1) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("BLLB", Node.get("key"));
				parameters.put("JZXH",
						Long.parseLong(BL01.get("JZXH").toString()));
				// parameters.put("MBLB", BL01.get("MBLB"));
				parameters.put("BLBH",
						Long.parseLong(BL01.get("BLBH").toString()));
				long l = dao
						.doCount(
								"EMR_BL01",
								"BLLB=:BLLB and JZXH=:JZXH and BLZT <> 9 and BLBH<>:BLBH",
								parameters);
				if (l > 0) {
					throw new ModelDataOperationException("已存在同类别的病历数据，保存失败!");
				}
			}
			// 判断是否被其他医生锁定
			Map<String, Object> blsd = dao.doLoad(BSPHISEntryNames.EMR_BLSD,
					blbh);
			if (!(blsd == null || (Integer) blsd.get("SDZT") != 1 || (user
					.getUserId().equals(blsd.get("SDYG"))))) {
				throw new ModelDataOperationException("保存失败：病历锁获取失败!");
			}
			// 保存病历emr_bl01
			// 病程判断是否有其它病程被修改
			if ("1".equals(Node.get("BLLX").toString())) {
				params.put("BLLB", BL01.get("BLLB"));
				params.put("JZXH", BL01.get("JZXH"));
				params.put("BLBH", blbh);
				// params.put("DKSJ", BL01.get("DKSJ"));
				String hql = "select BLBH from EMR_BL01 where BLLX=1 and BLLB=:BLLB and JZXH=:JZXH and BLZT<>9  and BLBH<>:BLBH";
				List<Map<String, Object>> l = dao.doSqlQuery(hql, params);
				if (l.size() > 0) {
					params.clear();
					String cnd = "['and',['in',['$','BLBH','l']," + l
							+ "],['gt',['$','XGSJ'],['todate',['s','"
							//zhaojian 2017-09-25 解决电子病历保存失败问题
							//+ BL01.get("DKSJ")
							+((BL01.get("DKSJ")==null)?(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())):(BL01.get("DKSJ")))
							+ "'],['s','yyyy-mm-dd hh24:mi:ss']]]]";
					List<Map<String, Object>> xgList = dao.doQuery(
							CNDHelper.toListCnd(cnd), "XGSJ desc",
							BSPHISEntryNames.EMR_BLXG);
					if (xgList.size() > 0) {
						res.put("reload", true);
						reload = true;
					}
				}
			}

			// 判断是否已经存在
			params.put("BLBH", blbh);
			Map<String, Object> bl01Old = dao.doLoad(BSPHISEntryNames.EMR_BL01,
					blbh);
			// long l = dao.doCount(BSPHISEntryNames.EMR_BL01, "BLBH=:BLBH",
			// params);
			if (bl01Old != null) {// 更新操作
				op = "update";
				if (bl01Old.get("YMJL") != null
						&& bl01Old.get("YMJL").toString().trim().length() == 0) {
					reload = true;
					BL01.remove("YMJL");
					BL01.remove("BLYM");
					res.put("reload", true);
				}
				if (BL01.containsKey("JLSJ_new")) {
					BL01.put("JLSJ", T.parse(BL01.get("JLSJ_new").toString(),
							"yyyy-MM-dd hh:mm:ss"));
				} else {
					BL01.remove("JLSJ");
				}
				if (openBlbh > 0) {
					BL01.put("YMJL", " ");
					BL01.put("BLYM", "0");
				}
				BL01.remove("XTSJ");
				BL01.remove("BRNL");
				BL01.remove("BLZT");
				BL01.remove("WCSJ");
			} else {
				BL01.put("XTSJ", new Date());
				BL01.put("JLSJ", T.parse(BL01.get("JLSJ").toString(),
						"yyyy-MM-dd hh:mm:ss"));
			}
			// 保存医学表达式
			if (body.get("EMR_YXBDS_BL") != null) {
				List<Map<String, Object>> yxbds = (List<Map<String, Object>>) body
						.get("EMR_YXBDS_BL");
				for (Map<String, Object> m : yxbds) {
					doSaveYxbds(m, ctx);
				}
			}
			if (body.get("SYJL") != null) {
				BL01.put("BLZT", 1);
				BL01.put("WCSJ", new Date());
			}
			dao.doSave(op, BSPHISEntryNames.EMR_BL01, BL01, false);
			if (openBlbh > 0) {
				reload = true;
				String cnd = "['and',['and',['and',['and',['eq',['$','BLLX'],['i',1]],['eq',['$','JZXH'],['d',"
						+ BL01.get("JZXH")
						+ "]]],['ne',['$','BLZT'],['i',9]]],['eq',['$','BLLB'],['i',"
						+ BL01.get("BLLB")
						+ "]]],['ne',['$','DLLB'],['i',-1]]]";
				// 获取重新排序后的信息
				List<Map<String, Object>> bl01s = dao.doList(
						CNDHelper.toListCnd(cnd), "BLLB ASC,JLSJ ASC,BLBH ASC",
						BSPHISEntryNames.EMR_BL01);
				if (bl01s.size() <= 0) {
					throw new ModelDataOperationException("数据发生异常错误!");
				}
				for (int i = 0; i < bl01s.size(); i++) {
					if (openBlbh == Long.parseLong(bl01s.get(i).get("BLBH")
							.toString())) {
						if (i > 0) {
							res.put("reloadBlbh", bl01s.get(i - 1).get("BLBH"));
						} else {
							if (openBlbh != blbh) {
								Map<String, Object> parameters = new HashMap<String, Object>();
								parameters.put("BLBH", openBlbh);
								dao.doUpdate(
										"update EMR_BL01 set BLYM=0,YMJL=' ' where BLBH=:BLBH",
										parameters);
							}
							res.put("reloadBlbh", openBlbh);
						}
					}
				}
			}
			// 保存页眉元素EMR_BLYMYS remove by yangl 2014.09.25 暂时去掉页眉元素
			// Object ymysText = BL01.get("ymysText");
			// if (ymysText != null) {
			// String[] ymysArray = ymysText.toString().split("#9");
			// for (int i = 0; i < ymysArray.length; i++) {
			// String blymys = ymysArray[i];
			// String[] blymysArray = blymys.split("#10");
			// Map<String, Object> blymysMap = new HashMap<String, Object>();
			// blymysMap.put("JZXH", BL01.get("JZXH"));
			// blymysMap.put("BLLB", BL01.get("BLLB"));
			// blymysMap.put("BLBH", BL01.get("BLBH"));
			// blymysMap.put("JGID", BL01.get("JGID"));
			// blymysMap.put("DLMC", blymysArray[0]);
			// blymysMap.put("YSMC", blymysArray[1]);
			// blymysMap.put(
			// "XSMC",
			// (blymysArray[2].contains(",") ? blymysArray[2]
			// .split(",")[1] : blymysArray[2]));
			// blymysMap.put("YSLX", blymysArray[3]);
			// dao.doSave("create", BSPHISEntryNames.EMR_BLYMYS,
			// blymysMap, false);
			// }
			// }
			// 保存修改记录
			Map<String, Object> emr_blxg = new HashMap<String, Object>();
			emr_blxg.put("BLBH", blbh);
			emr_blxg.put("XGGH", (String) user.getUserId());
			emr_blxg.put("XGSJ", new Date());
			if (BL02.get("HJNR") != null) {
				emr_blxg.put(
						"HJNR",
						Hibernate.createBlob(BL02.get("HJNR").toString()
								.getBytes()));
			}
			emr_blxg.put("JGID", manageUnit);
			dao.doSave("create", BSPHISEntryNames.EMR_BLXG, emr_blxg, false);
			// ss_phis.flush();
			// 保存病历emr_bl02
			String blnr = (String) BL02.get("BLNR");
			String blht = (String) BL02.get("BLHT");
			BL02.put("BLNR", Hibernate.createBlob(blnr.getBytes("UTF-8")));
			BL02.put("BLHT", Hibernate.createBlob(blht.getBytes("UTF-8")));
			dao.doSave(op, BSPHISEntryNames.EMR_BL02, BL02, false);
			// 保存操作日志
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("RZNR", BL01.get("BLMC"));
			record.put("YWID1", BL01.get("JZXH"));
			record.put("YWID2", blbh);
			record.put("YEID3", blbh);
			String logType = op.equals("create") ? BSPEMRUtil.OP_CREATE
					: BSPEMRUtil.OP_UPDATE;
			if (body.get("SYJL") != null) {
				logType = BSPEMRUtil.OP_SIGNED;
			}
			BSPEMRUtil.doSaveEmrOpLog(logType, record, dao, ctx);
			// 判断是否需要保存页眉页脚
			if (BL01.get("YMYJ") != null) {
				reload = true;
				Long blbh_ymyj = getBl01Key(ctx);
				BL01.put("BLBH", blbh_ymyj);
				BL01.put("DLLB", -1);
				BL01.put("DLJ", "-HdrFtr");
				BL01.put("BLMC", "页眉页脚");
				dao.doSave(op, BSPHISEntryNames.EMR_BL01, BL01, false);
				BL02.put("BLBH", blbh_ymyj);
				BL02.put(
						"BLNR",
						Hibernate.createBlob(BL02.get("YMYJNR").toString()
								.getBytes("UTF-8")));
				BL02.put(
						"BLHT",
						Hibernate.createBlob(BL02.get("YMYJHT").toString()
								.getBytes("GBK")));
				dao.doSave(op, BSPHISEntryNames.EMR_BL02, BL02, false);
			}
			// 保存病历03数据
			params.clear();
			params.put("BLBH", blbh);
			long l = dao.doCount("EMR_BL03", "BLBH=:BLBH", params);
			if (l > 0) {
				params.put(
						"WDNR",
						Hibernate.createBlob(BL03.get("HTML").toString()
								.getBytes()));
				dao.doUpdate(
						"update EMR_BL03 set WDNR=:WDNR where BLBH=:BLBH and WDLX=1",
						params);
				params.put(
						"WDNR",
						Hibernate.createBlob(BL03.get("XML").toString()
								.getBytes()));
				dao.doUpdate(
						"update EMR_BL03 set WDNR=:WDNR where BLBH=:BLBH and WDLX=3",
						params);
			} else {
				BL03.put(
						"WDNR",
						Hibernate.createBlob(BL03.get("HTML").toString()
								.getBytes()));
				BL03.put("WDLX", 1);
				dao.doSave("create", BSPHISEntryNames.EMR_BL03, BL03, false);
				BL03.put(
						"WDNR",
						Hibernate.createBlob(BL03.get("XML").toString()
								.getBytes()));
				BL03.put("WDLX", 3);
				dao.doSave("create", BSPHISEntryNames.EMR_BL03, BL03, false);
			}
			if (BL03.get("IMAGE") != null) {
				List<Map<String, Object>> images = (List<Map<String, Object>>) BL03
						.get("IMAGE");
				for (Map<String, Object> image : images) {
					String fileId = image.get("name").toString();
					String imageFile = image.get("value").toString();
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("FJMC", fileId);
					long count = dao.doCount("EMR_BL04", "FJMC=:FJMC",
							parameters);
					parameters.put("FJNR",
							Hibernate.createBlob(imageFile.getBytes()));
					if (count > 0) {
						dao.doUpdate(
								"update EMR_BL04 set FJNR=:FJNR where FJMC=:FJMC",
								parameters);
					} else {
						Map<String, Object> r = new HashMap<String, Object>();
						r.put("FJMC", fileId);
						r.put("FJNR",
								Hibernate.createBlob(imageFile.getBytes()));
						r.put("WDBH", blbh);
						r.put("FJLX", 1);
						r.put("JGID", manageUnit);
						dao.doSave("create", BSPHISEntryNames.EMR_BL04, r,
								false);
					}
				}
			}
			// 是否需要保存签名
			if (body.get("SYJL") != null) {
				// BL01.put("BLZT", 1);
				// BL01.put("WCSJ", new Date());
				Map<String, Object> SYJL = (Map<String, Object>) body
						.get("SYJL");
				SYJL.put("SYYS", (String) user.getUserId());
				SYJL.put("SYSJ", new Date());
				SYJL.put("JLSJ", new Date());
				SYJL.put("QMLX", 1);
				SYJL.put("QMLSH", 0);
				// SYJL.put("ZJBJ", 0);
				SYJL.put("BZXX", "/");
				dao.doSave("create", BSPHISEntryNames.EMR_BLSY, SYJL, false);
			}
			if (!reload) {
				doUpdateYmxx(BL01, ctx);
			}
			return res;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存病历/病程数据失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("病历/病程数据验证失败!", e);
		} catch (UnsupportedEncodingException e) {
			throw new ModelDataOperationException("保存病历/病程数据失败!", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("保存病历/病程数据失败!", e);
		} catch (KeyManagerException e) {
			throw new ModelDataOperationException("获取病历/病程主键失败!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("日期格式解析错误!", e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> doSaveOmrData(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> BL01 = (Map<String, Object>) body.get("BL01");
		Map<String, Object> BL02 = (Map<String, Object>) body.get("BL02");
		Map<String, Object> BL03 = (Map<String, Object>) body.get("BL03");
		Map<String, Object> Node = (Map<String, Object>) body.get("Node");
		Long openBlbh = Long.parseLong((body.get("openBlbh") == null ? "-1"
				: body.get("openBlbh").toString()));// 修改标题后重载的BLBH
		Long blbh = Long.parseLong(BL01.get("BLBH").toString());
		boolean reload = false;
		String op = "create";
		try {
			// 判断是否单一文档，若是，判断是否已经存在
			Integer dywd = (Integer) Node.get("DYWD");
			if (dywd == 1) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("BLLB", Node.get("key"));
				parameters.put("JZXH",
						Long.parseLong(BL01.get("JZXH").toString()));
				// parameters.put("MBLB", BL01.get("MBLB"));
				parameters.put("BLBH",
						Long.parseLong(BL01.get("BLBH").toString()));
				long l = dao
						.doCount(
								"OMR_BL01",
								"BLLB=:BLLB and JZXH=:JZXH and BLZT <> 9 and BLBH<>:BLBH",
								parameters);
				if (l > 0) {
					throw new ModelDataOperationException("已存在同类别的病历数据，保存失败!");
				}
			}
			// 判断是否被其他医生锁定
			Map<String, Object> blsd = dao.doLoad(BSPHISEntryNames.EMR_BLSD,
					blbh);
			if (!(blsd == null || (Integer) blsd.get("SDZT") != 1 || (user
					.getUserId().equals(blsd.get("SDYG"))))) {
				throw new ModelDataOperationException("保存失败：病历锁获取失败!");
			}
			// 保存病历emr_bl01
			// 病程判断是否有其它病程被修改
			if ("1".equals(Node.get("BLLX").toString())) {
				params.put("BLLB", BL01.get("BLLB"));
				params.put("JZXH", BL01.get("JZXH"));
				params.put("BLBH", blbh);
				// params.put("DKSJ", BL01.get("DKSJ"));
				String hql = "select BLBH from OMR_BL01 where BLLX=1 and BLLB=:BLLB and JZXH=:JZXH and BLZT<>9  and BLBH<>:BLBH";
				List<Map<String, Object>> l = dao.doSqlQuery(hql, params);
				if (l.size() > 0) {
					params.clear();
					String cnd = "['and',['in',['$','BLBH','l']," + l
							+ "],['gt',['$','XGSJ'],['todate',['s','"
							+ BL01.get("DKSJ")
							+ "'],['s','yyyy-mm-dd hh24:mi:ss']]]]";
					List<Map<String, Object>> xgList = dao.doQuery(
							CNDHelper.toListCnd(cnd), "XGSJ desc",
							BSPHISEntryNames.EMR_BLXG);
					if (xgList.size() > 0) {
						res.put("reload", true);
						reload = true;
					}
				}
			}

			// 判断是否已经存在
			params.put("BLBH", blbh);
			Map<String, Object> bl01Old = dao.doLoad(BSPHISEntryNames.OMR_BL01,
					blbh);
			// long l = dao.doCount(BSPHISEntryNames.EMR_BL01, "BLBH=:BLBH",
			// params);
			if (bl01Old != null) {// 更新操作
				op = "update";
				if (bl01Old.get("YMJL") != null
						&& bl01Old.get("YMJL").toString().trim().length() == 0) {
					reload = true;
					BL01.remove("YMJL");
					BL01.remove("BLYM");
					res.put("reload", true);
				}
				if (BL01.containsKey("JLSJ_new")) {
					BL01.put("JLSJ",
							BSHISUtil.toDate(BL01.get("JLSJ_new").toString()));
				} else {
					BL01.remove("JLSJ");
				}
				if (openBlbh > 0) {
					BL01.put("YMJL", " ");
					BL01.put("BLYM", "0");
				}
				BL01.remove("XTSJ");
				BL01.remove("BRNL");
				BL01.remove("BLZT");
				BL01.remove("WCSJ");
			} else {
				BL01.put("XTSJ", new Date());
				BL01.put("JLSJ", BSHISUtil.toDate(BL01.get("JLSJ").toString()));
			}
			// 保存医学表达式
			if (body.get("EMR_YXBDS_BL") != null) {
				List<Map<String, Object>> yxbds = (List<Map<String, Object>>) body
						.get("EMR_YXBDS_BL");
				for (Map<String, Object> m : yxbds) {
					doSaveYxbds(m, ctx);
				}
			}
			if (body.get("SYJL") != null) {
				BL01.put("BLZT", 1);
				BL01.put("WCSJ", new Date());
			}
			if ("create".equals(op)) {
				BL01.put("CJSJ", new Date());
			}
			dao.doSave(op, BSPHISEntryNames.OMR_BL01, BL01, false);
			if (openBlbh > 0) {
				reload = true;
				String cnd = "['and',['and',['and',['and',['eq',['$','BLLX'],['i',1]],['eq',['$','JZXH'],['d',"
						+ BL01.get("JZXH")
						+ "]]],['ne',['$','BLZT'],['i',9]]],['eq',['$','BLLB'],['i',"
						+ BL01.get("BLLB")
						+ "]]],['ne',['$','DLLB'],['i',-1]]]";
				// 获取重新排序后的信息
				List<Map<String, Object>> bl01s = dao.doList(
						CNDHelper.toListCnd(cnd), "BLLB ASC,JLSJ ASC,BLBH ASC",
						BSPHISEntryNames.OMR_BL01);
				if (bl01s.size() <= 0) {
					throw new ModelDataOperationException("数据发生异常错误!");
				}
				for (int i = 0; i < bl01s.size(); i++) {
					if (openBlbh == Long.parseLong(bl01s.get(i).get("BLBH")
							.toString())) {
						if (i > 0) {
							res.put("reloadBlbh", bl01s.get(i - 1).get("BLBH"));
						} else {
							if (openBlbh != blbh) {
								Map<String, Object> parameters = new HashMap<String, Object>();
								parameters.put("BLBH", openBlbh);
								dao.doUpdate(
										"update OMR_BL01 set BLYM=0,YMJL=' ' where BLBH=:BLBH",
										parameters);
							}
							res.put("reloadBlbh", openBlbh);
						}
					}
				}
			}
			// 保存页眉元素EMR_BLYMYS
			Object ymysText = BL01.get("ymysText");
			if (ymysText != null) {
				String[] ymysArray = ymysText.toString().split("#9");
				for (int i = 0; i < ymysArray.length; i++) {
					String blymys = ymysArray[i];
					String[] blymysArray = blymys.split("#10");
					Map<String, Object> blymysMap = new HashMap<String, Object>();
					blymysMap.put("JZXH", BL01.get("JZXH"));
					blymysMap.put("BLLB", BL01.get("BLLB"));
					blymysMap.put("BLBH", BL01.get("BLBH"));
					blymysMap.put("JGID", BL01.get("JGID"));
					blymysMap.put("DLMC", blymysArray[0]);
					blymysMap.put("YSMC", blymysArray[1]);
					blymysMap.put(
							"XSMC",
							(blymysArray[2].contains(",") ? blymysArray[2]
									.split(",")[1] : blymysArray[2]));
					blymysMap.put("YSLX", blymysArray[3]);
					dao.doSave("create", BSPHISEntryNames.EMR_BLYMYS,
							blymysMap, false);
				}
			}
			// 保存修改记录
			Map<String, Object> emr_blxg = new HashMap<String, Object>();
			emr_blxg.put("BLBH", blbh);
			emr_blxg.put("XGGH", (String) user.getUserId());
			emr_blxg.put("XGSJ", new Date());
			if (BL02.get("HJNR") != null) {
				emr_blxg.put(
						"HJNR",
						Hibernate.createBlob(BL02.get("HJNR").toString()
								.getBytes()));
			}
			emr_blxg.put("JGID", manageUnit);
			dao.doSave("create", BSPHISEntryNames.EMR_BLXG, emr_blxg, false);
			// ss_phis.flush();
			// 保存病历emr_bl02
			String blnr = (String) BL02.get("BLNR");
			String blht = (String) BL02.get("BLHT");
			BL02.put("BLNR", Hibernate.createBlob(blnr.getBytes("UTF-8")));
			BL02.put("BLHT", Hibernate.createBlob(blht.getBytes("UTF-8")));
			dao.doSave(op, BSPHISEntryNames.OMR_BL02, BL02, false);
			// 保存操作日志
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("RZNR", BL01.get("BLMC"));
			record.put("YWID1", BL01.get("JZXH"));
			record.put("YWID2", blbh);
			record.put("YEID3", blbh);
			String logType = op.equals("create") ? BSPEMRUtil.OP_CREATE
					: BSPEMRUtil.OP_UPDATE;
			if (body.get("SYJL") != null) {
				logType = BSPEMRUtil.OP_SIGNED;
			}
			BSPEMRUtil.doSaveEmrOpLog(logType, record, dao, ctx);
			// 判断是否需要保存页眉页脚
			if (BL01.get("YMYJ") != null) {
				reload = true;
				Long blbh_ymyj = getBl01Key(ctx);
				BL01.put("BLBH", blbh_ymyj);
				BL01.put("DLLB", -1);
				BL01.put("DLJ", "-HdrFtr");
				BL01.put("BLMC", "页眉页脚");
				BL01.put("CJSJ", new Date());
				dao.doSave(op, BSPHISEntryNames.OMR_BL01, BL01, false);
				BL02.put("BLBH", blbh_ymyj);
				BL02.put(
						"BLNR",
						Hibernate.createBlob(BL02.get("YMYJNR").toString()
								.getBytes("UTF-8")));
				BL02.put(
						"BLHT",
						Hibernate.createBlob(BL02.get("YMYJHT").toString()
								.getBytes("GBK")));
				dao.doSave(op, BSPHISEntryNames.OMR_BL02, BL02, false);
			}
			// 保存病历03数据
			params.clear();
			params.put("BLBH", blbh);
			long l = dao.doCount("EMR_BL03", "BLBH=:BLBH", params);
			if (l > 0) {
				params.put(
						"WDNR",
						Hibernate.createBlob(BL03.get("HTML").toString()
								.getBytes()));
				dao.doUpdate(
						"update EMR_BL03 set WDNR=:WDNR where BLBH=:BLBH and WDLX=1",
						params);
				params.put(
						"WDNR",
						Hibernate.createBlob(BL03.get("XML").toString()
								.getBytes()));
				dao.doUpdate(
						"update EMR_BL03 set WDNR=:WDNR where BLBH=:BLBH and WDLX=3",
						params);
			} else {
				BL03.put(
						"WDNR",
						Hibernate.createBlob(BL03.get("HTML").toString()
								.getBytes()));
				BL03.put("WDLX", 1);
				dao.doSave("create", BSPHISEntryNames.EMR_BL03, BL03, false);
				BL03.put(
						"WDNR",
						Hibernate.createBlob(BL03.get("XML").toString()
								.getBytes()));
				BL03.put("WDLX", 3);
				dao.doSave("create", BSPHISEntryNames.EMR_BL03, BL03, false);
			}
			if (BL03.get("IMAGE") != null) {
				List<Map<String, Object>> images = (List<Map<String, Object>>) BL03
						.get("IMAGE");
				for (Map<String, Object> image : images) {
					String fileId = image.get("name").toString();
					String imageFile = image.get("value").toString();
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("FJMC", fileId);
					long count = dao.doCount("EMR_BL04", "FJMC=:FJMC",
							parameters);
					parameters.put("FJNR",
							Hibernate.createBlob(imageFile.getBytes()));
					if (count > 0) {
						dao.doUpdate(
								"update EMR_BL04 set FJNR=:FJNR where FJMC=:FJMC",
								parameters);
					} else {
						Map<String, Object> r = new HashMap<String, Object>();
						r.put("FJMC", fileId);
						r.put("FJNR",
								Hibernate.createBlob(imageFile.getBytes()));
						r.put("WDBH", blbh);
						r.put("FJLX", 1);
						r.put("JGID", manageUnit);
						dao.doSave("create", BSPHISEntryNames.EMR_BL04, r,
								false);
					}
				}
			}
			// 是否需要保存签名
			if (body.get("SYJL") != null) {
				// BL01.put("BLZT", 1);
				// BL01.put("WCSJ", new Date());
				Map<String, Object> SYJL = (Map<String, Object>) body
						.get("SYJL");
				SYJL.put("SYYS", (String) user.getUserId());
				SYJL.put("SYSJ", new Date());
				SYJL.put("JLSJ", new Date());
				SYJL.put("QMLX", 1);
				SYJL.put("QMLSH", 0);
				// SYJL.put("ZJBJ", 0);
				SYJL.put("BZXX", "/");
				dao.doSave("create", BSPHISEntryNames.EMR_BLSY, SYJL, false);
			}
			if (!reload) {
				doUpdateYmxx(BL01, ctx);
			}
			return res;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存病历/病程数据失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("病历/病程数据验证失败!", e);
		} catch (UnsupportedEncodingException e) {
			throw new ModelDataOperationException("保存病历/病程数据失败!", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("保存病历/病程数据失败!", e);
		} catch (KeyManagerException e) {
			throw new ModelDataOperationException("获取病历/病程主键失败!", e);
		}
	}

	/**
	 * 打印日志
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSavePrintLog(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		BSPEMRUtil.doSaveEmrOpLog(BSPEMRUtil.OP_PRINT, body, dao, ctx);
	}

	/**
	 * 另存为个人模版
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveAsPrivatePlate(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		body.put("REGISTRAR", (String) user.getUserId());
		body.put("JGID", manageUnit);
		body.put("PTSTATE", 0);
		body.put("SJBZ", 0);
		body.put("BZMBBH", 0);
		body.put(
				"PTTEMPLATE",
				Hibernate.createBlob(body.get("PTTEMPLATE").toString()
						.getBytes()));
		body.put(
				"PTTEMPLATETEXT",
				Hibernate.createBlob(body.get("PTTEMPLATETEXT").toString()
						.getBytes()));
		try {
			dao.doSave("create", BSPHISEntryNames.PRIVATETEMPLATE, body, false);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("另存为个人模版失败!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("另存为个人模版失败!", e);
		}
	}

	/**
	 * 删除电子病历
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doRemoveEmrData(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			Long blbh = Long.parseLong(body.get("BLBH").toString());
			// 判断是否有审阅记录
			parameters.put("BLBH", blbh);
			long l = dao.doCount("EMR_BLSY", "BLBH=:BLBH and QMLX<>0",
					parameters);
			if (l > 0) {
				throw new ModelDataOperationException("当前病历已经有签名记录，不能删除!");
			}
			// if (body.containsKey("BL01")) {
			// res.putAll(doSaveEmrData(body, ctx));
			// }
			parameters.clear();
			Map<String, Object> curBl01 = null;
			if ("1".equals(body.get("BLLX").toString())) {
				// 判断是否有下一份病程
				String cnd = "['and',['and',['and',['and',['eq',['$','BLLX'],['i',1]],['eq',['$','JZXH'],['d',"
						+ body.get("JZXH")
						+ "]]],['ne',['$','BLZT'],['i',9]]],['eq',['$','BLLB'],['i',"
						+ body.get("BLLB")
						+ "]]],['ne',['$','DLLB'],['i',-1]]]";
				List<Map<String, Object>> bl01s = dao.doList(
						CNDHelper.toListCnd(cnd), "BLLB ASC,XTSJ ASC,BLBH ASC",
						BSPHISEntryNames.EMR_BL01);
				if (bl01s.size() <= 0) {
					throw new ModelDataOperationException("删除失败：当前记录已不存在!");
				}
				if (bl01s.size() == 1) {
					// 删除所有病程信息
					if (blbh != Long.parseLong(bl01s.get(0).get("BLBH")
							.toString())) {
						throw new ModelDataOperationException("删除失败：当前记录已不存在!");
					}
					parameters.put("JZXH",
							Long.parseLong(body.get("JZXH").toString()));
					dao.doUpdate(
							"update EMR_BL01 set BLZT=9 where JZXH=:JZXH and BLLX=1",
							parameters);
					curBl01 = bl01s.get(0);
					res.put("removeAll", true);
				} else {
					Map<String, Object> nextBl01 = null;
					for (int i = 0; i < bl01s.size(); i++) {
						Map<String, Object> bl01 = bl01s.get(i);
						if (Long.parseLong(bl01.get("BLBH").toString()) == blbh) {
							curBl01 = bl01;
							if (i < (bl01s.size() - 1)) {
								nextBl01 = bl01s.get(i + 1);
								break;
							}
						}

					}
					// 设置下一份病程的YMJL为空 BLYM=0
					if (nextBl01 != null) {
						parameters
								.put("BLBH", Long.parseLong(nextBl01
										.get("BLBH").toString()));
						dao.doUpdate(
								"update EMR_BL01 set BLYM=0,YMJL=' ' where BLBH=:BLBH",
								parameters);
					}
				}
				parameters.clear();
				parameters.put("BLBH", blbh);
				dao.doUpdate("update EMR_BL01 set BLZT=9 where BLBH=:BLBH",
						parameters);
			} else {
				curBl01 = dao.doLoad(BSPHISEntryNames.EMR_BL01, blbh);
				parameters.put("BLBH", blbh);
				dao.doUpdate("update EMR_BL01 set BLZT=9 where BLBH=:BLBH",
						parameters);
				res.put("removeAll", true);
			}
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("RZNR", curBl01.get("BLMC"));
			record.put("YWID1", curBl01.get("JZXH"));
			record.put("YWID2", blbh);
			record.put("YEID3", blbh);
			BSPEMRUtil.doSaveEmrOpLog(BSPEMRUtil.OP_DELETE, record, dao, ctx);
			return res;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除病历失败!", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("删除病历失败!", e);
		}
	}

	/**
	 * 删除电子病历
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doRemoveOmrData(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			Long blbh = Long.parseLong(body.get("BLBH").toString());
			// 判断是否有审阅记录
			parameters.put("BLBH", blbh);
			long l = dao.doCount("EMR_BLSY", "BLBH=:BLBH and QMLX<>0",
					parameters);
			if (l > 0) {
				throw new ModelDataOperationException("当前病历已经有签名记录，不能删除!");
			}
			// if (body.containsKey("BL01")) {
			// res.putAll(doSaveEmrData(body, ctx));
			// }
			parameters.clear();
			Map<String, Object> curBl01 = null;
			if ("1".equals(body.get("BLLX").toString())) {
				// 判断是否有下一份病程
				String cnd = "['and',['and',['and',['and',['eq',['$','BLLX'],['i',1]],['eq',['$','JZXH'],['d',"
						+ body.get("JZXH")
						+ "]]],['ne',['$','BLZT'],['i',9]]],['eq',['$','BLLB'],['i',"
						+ body.get("BLLB")
						+ "]]],['ne',['$','DLLB'],['i',-1]]]";
				List<Map<String, Object>> bl01s = dao.doList(
						CNDHelper.toListCnd(cnd), "BLLB ASC,XTSJ ASC,BLBH ASC",
						BSPHISEntryNames.OMR_BL01);
				if (bl01s.size() <= 0) {
					throw new ModelDataOperationException("删除失败：当前记录已不存在!");
				}
				if (bl01s.size() == 1) {
					// 删除所有病程信息
					if (blbh != Long.parseLong(bl01s.get(0).get("BLBH")
							.toString())) {
						throw new ModelDataOperationException("删除失败：当前记录已不存在!");
					}
					parameters.put("JZXH",
							Long.parseLong(body.get("JZXH").toString()));
					dao.doUpdate(
							"update OMR_BL01 set BLZT=9 where JZXH=:JZXH and BLLX=1",
							parameters);
					curBl01 = bl01s.get(0);
					res.put("removeAll", true);
				} else {
					Map<String, Object> nextBl01 = null;
					for (int i = 0; i < bl01s.size(); i++) {
						Map<String, Object> bl01 = bl01s.get(i);
						if (Long.parseLong(bl01.get("BLBH").toString()) == blbh) {
							curBl01 = bl01;
							if (i < (bl01s.size() - 1)) {
								nextBl01 = bl01s.get(i + 1);
								break;
							}
						}

					}
					// 设置下一份病程的YMJL为空 BLYM=0
					if (nextBl01 != null) {
						parameters
								.put("BLBH", Long.parseLong(nextBl01
										.get("BLBH").toString()));
						dao.doUpdate(
								"update OMR_BL01 set BLYM=0,YMJL=' ' where BLBH=:BLBH",
								parameters);
					}
				}
				parameters.clear();
				parameters.put("BLBH", blbh);
				dao.doUpdate("update OMR_BL01 set BLZT=9 where BLBH=:BLBH",
						parameters);
			} else {
				curBl01 = dao.doLoad(BSPHISEntryNames.OMR_BL01, blbh);
				parameters.put("BLBH", blbh);
				dao.doUpdate("update OMR_BL01 set BLZT=9 where BLBH=:BLBH",
						parameters);
				res.put("removeAll", true);
			}
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("RZNR", curBl01.get("BLMC"));
			record.put("YWID1", curBl01.get("JZXH"));
			record.put("YWID2", blbh);
			record.put("YEID3", blbh);
			BSPEMRUtil.doSaveEmrOpLog(BSPEMRUtil.OP_DELETE, record, dao, ctx);
			return res;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除病历失败!", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("删除病历失败!", e);
		}
	}

	public Map<String, Object> doCheckEditor(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		String datetime = (String) body.get("DKSJ");
		try {
			Date dksj = T.parseDateTime(datetime);
			body.put("BLBH", Long.parseLong(body.get("BLBH").toString()));
			body.put("DKSJ", dksj);
			long l = dao.doCount("EMR_BLXG", "BLBH=:BLBH and XGSJ>:DKSJ", body);
			if (l > 0) {
				res.put("hasEditor", true);
			} else {
				res.put("hasEditor", false);
			}
			return res;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("病历数据错误，请联系管理员", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("时间格式错误!", e);
		}
	}

	public Map<String, Object> doCheckPermission(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String bllb = body.get("BLLB").toString();
		Map<String, Object> emrPermissions = new HashMap<String, Object>();
		emrPermissions.put(BSPEMRUtil.SXQX, BSPEMRUtil.getCaseHistoryContory(
				bllb, BSPEMRUtil.SXQX, dao, ctx));
		emrPermissions.put(BSPEMRUtil.CKQX, BSPEMRUtil.getCaseHistoryContory(
				bllb, BSPEMRUtil.CKQX, dao, ctx));
		emrPermissions.put(BSPEMRUtil.SYQX, BSPEMRUtil.getCaseHistoryContory(
				bllb, BSPEMRUtil.SYQX, dao, ctx));
		emrPermissions.put(BSPEMRUtil.DYQX, BSPEMRUtil.getCaseHistoryContory(
				bllb, BSPEMRUtil.DYQX, dao, ctx));
		Map<String, Object> role = BSPEMRUtil.getDocRoleByUid(
				(String) user.getUserId(), dao);
		emrPermissions.put("JSJB", role.get("JSJB"));
		res.put("emrPermissions", emrPermissions);
		return res;
	}

	/**
	 * 更新页眉距离等信息
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateYmxx(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> paraLineInfo = (List<Map<String, Object>>) body
				.get("ParaLineInfo");
		if (paraLineInfo == null)
			return;
		Object curBlbh = body.get("BLBH");
		boolean begin = false;
		for (Map<String, Object> pMap : paraLineInfo) {
			String paraKey = (String) pMap.get("ParaKey");
			String pageNum = (String) pMap.get("PageNum");
			String rowNum = (String) pMap.get("RowNum");
			if (paraKey.indexOf("SpacePara") >= 0)
				continue;
			if (paraKey.indexOf("Illrc_1_") <= 0) {
				continue;
			}
			String blbh = paraKey.split("Illrc_1_")[1];
			if (!begin && curBlbh != null) {
				if (blbh.equals(curBlbh.toString())) {
					begin = true;
				}
			} else {
				begin = true;
			}
			if (begin) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("BLYM", Integer.parseInt(pageNum));
				parameters.put("YMJL", rowNum.trim());
				parameters.put("BLBH", Long.parseLong(blbh));
				try {
					dao.doUpdate(
							"update EMR_BL01 set BLYM=:BLYM, YMJL=:YMJL where BLBH=:BLBH",
							parameters);
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException("保存页眉距离信息错误!", e);
				}
			}
		}
	}

	public Map<String, Object> doGetMbXsmc(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		Object mbbh = body.get("MBBH");
		String cnd = "['eq',['$','MBBH'],['i'," + mbbh + "]]";
		try {
			List<Map<String, Object>> list = dao
					.doList(CNDHelper.toListCnd(cnd), null,
							BSPHISEntryNames.V_EMR_BLMB);
			if (list.size() > 0) {
				Object mbxx = list.get(0);
				res.put("MBXX", mbxx);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取病历模版显示名称错误!", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("获取病历模版显示名称错误!", e);
		}

		return res;
	}

	/**
	 * 获取审阅记录
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadReadRecord(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		Object blbh = body.get("BLBH");
		String cnd = "['eq',['$','BLBH'],['d'," + blbh + "]]";
		try {
			List<Map<String, Object>> list = dao.doList(
					CNDHelper.toListCnd(cnd), null, BSPHISEntryNames.EMR_BLSY);
			res.put("records", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取病历模版显示名称错误!", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("获取病历模版显示名称错误!", e);
		}
		return res;
	}

	/**
	 * 判断是否有审阅记录
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doCheckSigned(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("BLBH", Long.parseLong(body.get("BLBH").toString()));
		try {
			long l = dao.doCount("EMR_BLSY", "BLBH=:BLBH and QMLX<>0",
					parameters);
			res.put("hasReview", l > 0);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("当前病历编号不存在，请联系管理员!", e);
		}
		return res;
	}

	/**
	 * 签名
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doSigned(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		Object yljs = user.getProperty("YLJS");
		try {
			Map<String, Object> yljsMap = dao.doLoad(BSPHISEntryNames.EMR_YLJS,
					yljs);
			if (yljsMap == null) {
				throw new ModelDataOperationException("医疗角色丢失，请联系管理员!");
			}
			String op = (String) body.get("op");
			if (op.equals("remove")) {
				Object blbh = body.get("BLBH");
				if (blbh == null || blbh.toString().length() == 0) {
					throw new ModelDataOperationException("当前病历编号不存在，请联系管理员!");
				}
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("BLBH", Long.parseLong(blbh.toString()));
				parameters.put("SYYS", (String) user.getUserId());
				// long l = dao.doCount(BSPHISEntryNames.EMR_BLSY,
				// "BLBH=:BLBH and SYYS=:SYYS", parameters);
				String cnd = "['and',['eq',['$','BLBH'],['d'," + blbh
						+ "]],['eq',['$','SYYS'],['s','"
						+ (String) user.getUserId() + "']]]";
				Map<String, Object> records = dao.doList(
						CNDHelper.toListCnd(cnd), "SYSJ",
						BSPHISEntryNames.EMR_BLSY, 0, 0, "");
				res.put("records", records);
			} else {
				String cnd = null;
				if (body.get("elemName") != null
						&& body.get("elemName").toString().length() > 0) {
					cnd = "['and',['like',['$','GLYS '],['s','%,"
							+ yljsMap.get("JSMC")
							+ ",%']],['eq',['$','JKLB'],['i',2]]]";
				} else {
					cnd = "['and',['eq',['$','JKYS '],['s','"
							+ yljsMap.get("JSMC")
							+ "']],['eq',['$','JKLB'],['i',2]]]";
				}
				List<Map<String, Object>> list = dao.doList(
						CNDHelper.toListCnd(cnd), null,
						BSPHISEntryNames.EMR_JKYS);
				if (list.size() > 0) {
					Object glys = list.get(0).get("GLYS");
					Object jkkz = list.get(0).get("JKKZ");
					if (glys == null || glys.toString().trim().length() == 0
							|| jkkz == null
							|| jkkz.toString().trim().length() == 0) {
						throw new ModelDataOperationException(
								"签名失败：签名接口元素配置错误!");
					}
					String[] glysArray = glys.toString().split(",");
					String[] jkkzArray = jkkz.toString().split(",");
					if (glysArray.length != jkkzArray.length) {
						throw new ModelDataOperationException(
								"签名失败：签名接口元素配置错误!");
					}
					String eleName = glysArray[1];
					String eleValue = jkkzArray[1];
					res.put("eleName", eleName);
					res.put("eleValue", eleValue);
				} else {
					throw new ModelDataOperationException("签名失败：请配置签名接口元素!");
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("签名失败，请联系管理员!", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("签名失败，请联系管理员!", e);
		}
		return res;
	}

	/**
	 * 删除签名记录
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doRemoveSigned(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			dao.doRemove(Long.parseLong(body.get("JLXH").toString()),
					BSPHISEntryNames.EMR_BLSY);
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("RZNR", body.get("BLMC"));
			record.put("YWID1", body.get("JZXH"));
			record.put("YWID2", body.get("BLBH"));
			record.put("YEID3", body.get("JLXH"));
			BSPEMRUtil.doSaveEmrOpLog(BSPEMRUtil.OP_UNSIGNED, record, dao, ctx);
		} catch (NumberFormatException e) {
			throw new ModelDataOperationException("删除签名失败，请联系管理员!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除签名失败，请联系管理员!", e);
		}
	}

	public Map<String, Object> doLoadEmrHjnr(Object pkey, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			Map<String, Object> blxg = dao.doLoad(BSPHISEntryNames.EMR_BLXG,
					pkey);
			Blob blnr = (Blob) blxg.get("HJNR");
			InputStream is = blnr.getBinaryStream();
			Reader utf8_reader = new InputStreamReader(is, "GBK");
			StringBuffer hjnrBuf = new StringBuffer();
			int charValue = 0;
			while ((charValue = utf8_reader.read()) != -1) {
				hjnrBuf.append((char) charValue);
			}
			blxg.put("HJNR", hjnrBuf.toString());
			res.put("body", blxg);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取痕迹内容错误，请联系管理员!", e);
		} catch (SQLException e) {
			throw new ModelDataOperationException("获取痕迹内容错误，请联系管理员!", e);
		} catch (UnsupportedEncodingException e) {
			throw new ModelDataOperationException("获取痕迹内容错误，请联系管理员!", e);
		} catch (IOException e) {
			throw new ModelDataOperationException("获取痕迹内容错误，请联系管理员!", e);
		}

		return res;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Object> listDicInformation(String schema, List queryCnd,
			String queryCndsType, String sortInfo, int pageSize, int pageNo,
			String fieldId, String cndValue) throws ModelDataOperationException {
		Map<String, Object> body = new HashMap<String, Object>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			if (cndValue != null && !cndValue.equals("")) {
				if (schema.endsWith("GY_JBBM")) {
					if (queryCnd != null) {
						List<Object> cnd = CNDHelper.createSimpleCnd("like",
								"PYDM", "s", cndValue.toUpperCase() + "%");
						queryCnd = CNDHelper.createArrayCnd("and", queryCnd,
								cnd);
					} else {
						queryCnd = CNDHelper.createSimpleCnd("like", "PYDM",
								"s", cndValue.toUpperCase() + "%");
					}
					body = dao.doList(queryCnd, sortInfo, schema, pageNo,
							pageSize, queryCndsType);
					return body;
				}
				List<Map<String, Object>> re = dao.doList(queryCnd, sortInfo,
						schema);
				for (int i = 0; i < re.size(); i++) {
					Map<String, Object> m = re.get(i);
					String fieldValue = (String) m.get(fieldId);
					String pyCode = PyConverter.getFirstLetter(fieldValue);
					if (pyCode.startsWith(cndValue.toUpperCase())) {
						result.add(m);
					}
				}
				if (pageSize * (pageNo - 1) >= result.size()) {
					body.put("pageNo", 1);
					body.put("totalCount", result.size());
					int endIndex = pageSize;
					if (pageSize > result.size()) {
						endIndex = result.size();
					}
					result = result.subList(0, endIndex);
				} else {
					body.put("totalCount", result.size());
					int endIndex = pageNo * pageSize;
					if (pageNo * pageSize > result.size()) {
						endIndex = result.size();
					}
					result = result.subList(pageSize * (pageNo - 1), endIndex);
				}
				body.put("body", result);
			} else {
				body = dao.doList(queryCnd, sortInfo, schema, pageNo, pageSize,
						queryCndsType);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取字典元素失败!", e);
		}
		return body;
	}

	public void saveForcedUnlock(String BLBH, Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> para = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		hql.append("update EMR_BLSD set SDZT='0' where BLBH='").append(BLBH)
				.append("'");
		try {
			int c = dao.doUpdate(hql.toString(), para);
			if (c == 1) {
				Map<String, Object> body = new HashMap<String, Object>();
				body.put("BLBH", BLBH);
				body.put("JSYG", (String) user.getUserId());
				body.put("JSSJ", new Date());
				body.put("JSIP", " ");
				body.put("JGID", user.getManageUnitId());
				dao.doSave("create", "phis.application.cfg.schemas.EMR_QZJSJL",
						body, true);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("强制解锁失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("强制解锁失败!", e);
		}
	}

	// private static String getWorkListStub(String manaUnitId, String user,
	// String password, String role, String empiId, Context ctx) {
	// // 330102001001
	// GetWorkListStub gwl = null;
	// try {
	// String topUnitId = ParameterUtil.getTopUnitId();
	// String GWWEBSERVICE_ADDRESS = ParameterUtil.getParameter(topUnitId,
	// BSPHISSystemArgument.GWWEBSERVICE_ADDRESS, ctx);
	// gwl = new GetWorkListStub(GWWEBSERVICE_ADDRESS, 1);
	// GetWorkListStub.Execute exec = new GetWorkListStub.Execute();
	// StringBuffer reqParams = new StringBuffer();
	// String ssIP = ParameterUtil.getURLIP(GWWEBSERVICE_ADDRESS);
	// reqParams.append("<request reqClient=\"").append(ssIP)
	// .append("\">");
	// reqParams.append("<empiId>");
	// reqParams.append(empiId);
	// reqParams.append("</empiId>");
	// reqParams.append("<user>");
	// reqParams.append(user);
	// reqParams.append("</user>");
	// reqParams.append("<password>");
	// reqParams.append(password);
	// reqParams.append("</password>");
	// reqParams.append("<role>");
	// reqParams.append(role);
	// reqParams.append("</role>");
	// reqParams.append("</request>");
	// exec.setReq(reqParams.toString());
	// GetWorkListStub.ExecuteResponse res = gwl.execute(exec);
	// return res.get_return();
	// } catch (AxisFault e) {
	// e.printStackTrace();
	// return null;
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// return null;
	// }
	// }

	// private static String CreateDocumentStub(String manaUnitId, String user,
	// String password, String role, String empiId, String taskName,
	// Context ctx) {
	// // 330102001001
	// CreateDocumentStub gus = null;
	// try {
	// String topUnitId = ParameterUtil.getTopUnitId();
	// String GWWEBSERVICE_ADDRESS = ParameterUtil.getParameter(topUnitId,
	// BSPHISSystemArgument.GWWEBSERVICE_ADDRESS, ctx);
	// gus = new CreateDocumentStub(GWWEBSERVICE_ADDRESS, 1);
	// CreateDocumentStub.Execute exec = new CreateDocumentStub.Execute();
	// StringBuffer reqParams = new StringBuffer();
	// String ssIP = ParameterUtil.getURLIP(GWWEBSERVICE_ADDRESS);
	// reqParams.append("<request reqClient=\"").append(ssIP)
	// .append("\">");
	// reqParams.append("<empiId>");
	// reqParams.append(empiId);
	// reqParams.append("</empiId>");
	// reqParams.append("<user>");
	// reqParams.append(user);
	// reqParams.append("</user>");
	// reqParams.append("<password>");
	// reqParams.append(password);
	// reqParams.append("</password>");
	// reqParams.append("<role>");
	// reqParams.append(role);
	// reqParams.append("</role>");
	// reqParams.append("<taskName>");
	// reqParams.append(taskName);
	// reqParams.append("</taskName>");
	// reqParams.append("</request>");
	// exec.setReq(reqParams.toString());
	// CreateDocumentStub.ExecuteResponse res = gus.execute(exec);
	// return res.get_return();
	// } catch (AxisFault e) {
	// e.printStackTrace();
	// return null;
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// return null;
	// }
	// }

	public void saveCancellationMode(String pkey)
			throws ModelDataOperationException {
		String hql = "update PRIVATETEMPLATE set PTSTATE='9' where PTID='"
				+ pkey + "'";
		Map<String, Object> parameters = null;
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("注销个人模版失败!", e);
		}

	}

	public void saveRenewMode(String pkey) throws ModelDataOperationException {
		String hql = "update PRIVATETEMPLATE set PTSTATE='0' where PTID='"
				+ pkey + "'";
		Map<String, Object> parameters = null;
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("恢复个人模版失败!", e);
		}

	}

	public void saveChangeModeRecords(List<Map<String, Object>> records)
			throws ModelDataOperationException {
		for (Map<String, Object> map : records) {
			String hql = "update PRIVATETEMPLATE set PTNAME='"
					+ map.get("PTNAME") + "' where PTID='" + map.get("PTID")
					+ "'";
			Map<String, Object> parameters = null;
			try {
				dao.doUpdate(hql, parameters);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException("修改个人模版名称失败!", e);
			}
		}
	}

	public String getTempLook(String pTID, String mBBH)
			throws ModelDataOperationException {
		String resultString = null;
		Map<String, Object> map = null;
		try {
			if (pTID != null) {
				map = dao.doLoad(BSPHISEntryNames.PRIVATETEMPLATE, pTID);
			} else if (mBBH != null) {
				List<Object> cnd = CNDHelper.createSimpleCnd("eq",
						"TEMPLATECODE", "s", mBBH);
				map = dao.doLoad(cnd, BSPHISEntryNames.PRIVATETEMPLATE);
			} else {
				return resultString;
			}
			Blob blob = (Blob) map.get("PTTEMPLATETEXT");
			if (blob == null) {
				return resultString;
			}
			InputStream is = blob.getBinaryStream();
			Reader utf8_reader = new InputStreamReader(is, "GBK");
			StringBuffer utf8Text = new StringBuffer();
			int charValue = 0;
			while ((charValue = utf8_reader.read()) != -1) {
				utf8Text.append((char) charValue);
			}
			resultString = utf8Text.toString();
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载个人模版预览失败!", e);
		} catch (SQLException e) {
			throw new ModelDataOperationException("加载个人模版预览失败!", e);
		} catch (IOException e) {
			throw new ModelDataOperationException("加载个人模版预览失败!", e);
		}
		return resultString;
	}

	public void savePTTemplateData(String pkey, String xmlData, String textData)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			Blob xmlBlob = Hibernate.createBlob(xmlData.getBytes());
			Blob textBlob = Hibernate.createBlob(textData.getBytes());
			String hql = "update PRIVATETEMPLATE set PTTEMPLATE=:PTTEMPLATE, PTTEMPLATETEXT=:PTTEMPLATETEXT where PTID='"
					+ pkey + "'";
			parameters.put("PTTEMPLATE", xmlBlob);
			parameters.put("PTTEMPLATETEXT", textBlob);
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("修改个人模版内容失败!", e);
		}
	}

	public boolean checkHasPersonalName(String PTNAME)
			throws ModelDataOperationException {
		boolean flag = false;
		Map<String, Object> parameters = null;
		String hql = "select PTID as PTID from PRIVATETEMPLATE where PTNAME='"
				+ PTNAME + "'";
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);
			if (list != null && list.size() > 0) {
				flag = true;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("检查个人模版名称失败!", e);
		}
		return flag;
	}

	/**
	 * 页眉页脚设置查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryHeadersFootersSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		try {
			int ksfl = Integer.parseInt(req.get("body") + "");
			String whereStr = "";
			if (ksfl == 1) {// 门诊
				whereStr = " and a.OUTPATIENTCLINIC = 1";
			} else if (ksfl == 2) {// 医技
				whereStr = " and a.MEDICALLAB = 1";
			} else if (ksfl == 3) {// 住院
				whereStr = " and a.HOSPITALDEPT = 1";
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String ref = user.getManageUnit().getRef();// 用户的机构ID
			String jgid = user.getManageUnitId();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ref", ref);
			parameters.put("jgid", jgid);
			List<Map<String, Object>> list = dao
					.doSqlQuery(
							"select a.ID as ID,b.JLXH as JLXH,b.MBBH as MBBH,b.KSFL as KSFL,b.JGID as JGID from SYS_Office a left outer join EMR_KSMB_YMYJ b on a.ID=b.KSDM where a.OFFICECODE not in (select PARENTID from SYS_Office where ORGANIZCODE != PARENTID and LOGOFF=0 and ORGANIZCODE=:ref) and a.LOGOFF=0 and a.ORGANIZCODE = :ref and (b.JGID=:jgid or b.JGID is null)"
									+ whereStr, parameters);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.emr.schemas.SYS_Office_YMYJ");
			res.put("body", list);
			res.put("totalCount", list.size());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("页眉页脚设置查询失败!", e);
		}
	}

	/**
	 * 页眉页脚设置保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHeadersFootersSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		try {
			List<Map<String, Object>> body = (List<Map<String, Object>>) req
					.get("body");
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jgid", jgid);
			dao.doRemove("JGID", jgid, BSPHISEntryNames.EMR_KSMB_YMYJ);
			Map<String, Object> CHTparameters = new HashMap<String, Object>();
			for (Map<String, Object> data : body) {
				CHTparameters
						.put("mbbh", Long.parseLong(data.get("MBBH") + ""));
				dao.doUpdate(
						"update CHTEMPLATE set ISHDRFTRTEMP = 1 where CHTCODE = :mbbh",
						CHTparameters);
				data.put("KSDM", data.get("ID"));
				data.put("JGID", jgid);
				dao.doSave("create", BSPHISEntryNames.EMR_KSMB_YMYJ, data,
						false);
			}
			// List<Map<String,Object>> list =
			// dao.doSqlQuery("select a.ID as ID,b.JLXH as JLXH,b.MBBH as MBBH,b.KSFL as KSFL,b.JGID as JGID from SYS_Office a left outer join EMR_KSMB_YMYJ b on a.ID=b.KSDM where a.OFFICECODE not in (select PARENTID from SYS_Office where ORGANIZCODE != PARENTID and LOGOFF=0 and ORGANIZCODE=:ref) and a.LOGOFF=0 and a.HOSPITALDEPT = 1 and a.ORGANIZCODE = :ref and (b.JGID=:jgid or b.JGID is null)",
			// parameters);
			// SchemaUtil.setDictionaryMassageForList(list,
			// "phis.application.emr.schemas.SYS_Office_YMYJ");
			// res.put("body", list);
			// res.put("totalCount",list.size());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("页眉页脚设置保存失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("页眉页脚设置保存失败!", e);
		}
	}

	/**
	 * 公有模板、科室模板订阅查询
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void doLoadTemplateList(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> ks = (Map<String, Object>) req.get("ks");
		// type=1 为科室模板订阅
		String type = (String) req.get("type");
		StringBuffer where = new StringBuffer(" where 1=1");
		StringBuffer cht = new StringBuffer(
				"select a.CHTCODE as CHTCODE,a.CHTNAME as CHTNAME,a.CHTNAME as CHTNAME_t,a.FRAMEWORKCODE as FRAMEWORKCODE,a.TEMPLATETYPE as TEMPLATETYPE,a.INOROUTTYPE as INOROUTTYPE,a.PYDM as PYDM,'chtemplate' as TABLENAME,a.ISHDRFTRTEMP,a.NOTAVAILABLE as ISTY");
		if (type != null && "1".equals(type)) {
			where.append(" and NOTAVAILABLE = 0 and FRAMEWORKCODE is not null and TEMPLATETYPE is not null and TEMPLATETYPE != 'null' ");
		}
		if (ks != null && ks.get("SYS_OFFICE_ID") != null
				&& !"".equals(ks.get("SYS_OFFICE_ID"))) {
			cht.append(
					",case when (select id from emr_ksmbdy where a.chtcode = template_id and sys_office_id =")
					.append(ks.get("SYS_OFFICE_ID"))
					.append(" and T_TABLE_NAME = 'chtemplate'")
					.append(") is not null then 1 else 0 end as ISDY");
		} else {
			cht.append(",0 as ISDY");
		}
		StringBuffer chrt = new StringBuffer(
				"select b.CHRTCODE as CHTCODE,b.CHRTHNAME as CHTNAME,b.CHRTHNAME as CHTNAME_t,b.FRAMEWORKCODE as FRAMEWORKCODE,b.TEMPLACETYPE as TEMPLATETYPE,b.INOROUTTYPE as INOROUTTYPE,b.PYDM as PYDM,'chrecordtemplate' as TABLENAME,0 as ISHDRFTRTEMP,b.NOTAVAILABLE as ISTY");
		if (ks != null && ks.get("SYS_OFFICE_ID") != null
				&& !"".equals(ks.get("SYS_OFFICE_ID"))) {
			chrt.append(
					",case when (select id from emr_ksmbdy where b.CHRTCODE = template_id and sys_office_id =")
					.append(ks.get("SYS_OFFICE_ID"))
					.append(" and T_TABLE_NAME = 'chrecordtemplate'")
					.append(") is not null then 1 else 0 end as ISDY");
		} else {
			chrt.append(",0 as ISDY");
		}
		cht.append(" from CHTEMPLATE a");
		chrt.append(" from CHRECORDTEMPLATE b");
		if (req.containsKey("cnd") && req.get("cnd") != null) {
			List<?> cnd = (List<?>) req.get("cnd");
			where.append(" and ").append(
					ExpressionProcessor.instance().toString(cnd));
			if (where.toString().contains("a.PYDM")) {
				where = new StringBuffer(where.toString().replace("a.PYDM",
						"upper(PYDM)"));
			}
			if (where.toString().contains("a.MBLX")) {
				where = new StringBuffer(where.toString().replace("a.MBLX =",
						"4 >"));
			}
		}
		if (req.containsKey("treeCnd") && req.get("treeCnd") != null) {
			List<?> cnd = (List<?>) req.get("treeCnd");
			where.append(" and ").append(
					ExpressionProcessor.instance().toString(cnd));
		}
		StringBuffer sql = new StringBuffer();
		if (where.toString().contains("4 > '1'")) {// 1=病历模板
			sql.append(cht);
			sql.append(where);
			// sql.append(" and ishdrftrtemp <> 1 ");
		} else if (where.toString().contains("4 > '2'")) {// 2=段落模板
			sql.append(chrt);
			sql.append(where.toString().replace("TEMPLATETYPE", "TEMPLACETYPE"));
		} else if (where.toString().contains("4 > '3'")) {// 3=页眉页脚
			sql.append(cht);
			sql.append(where);
			// sql.append(" and ishdrftrtemp = 1");
		} else {
			sql.append(cht).append(where).append(" union all ").append(chrt);
			sql.append(where.toString().replace("TEMPLATETYPE", "TEMPLACETYPE"));
		}
		list = dao.doSqlQuery(sql.toString(), null);
		list = SchemaUtil.setDictionaryMassageForList(list,
				"phis.application.emr.schemas.CHTEMPLATE_CHRECORDTEMPLATE");
		res.put("body", list);
	}

	@SuppressWarnings("unchecked")
	public void doSaveMedicalRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			body.put("CYPLXH", body.get("ZYPLXH"));
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> map_maxlbbh = dao.doQuery(
					"select nvl(Max(LBBH),0) as MAXLBBH from EMR_KBM_BLLB",
					parameters).get(0);
			Integer maxlbbh = Integer.parseInt(map_maxlbbh.get("MAXLBBH") + "") + 1;
			body.put("LBBH", maxlbbh);
			if (!body.containsKey("SJLBBH") || body.get("SJLBBH") == null
					|| body.get("SJLBBH") == "") {
				body.put("SJLBBH", maxlbbh);
			}
			dao.doSave("create", BSPHISEntryNames.EMR_KBM_BLLB, body, false);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("病历类别保存失败!", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("病历类别保存失败!", e);
		}
	}

	/**
	 * 科室模板订阅查询
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ExpException
	 */
	public void doLoadKsmbdy(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, ExpException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String code = user.getManageUnit().getRef();
		StringBuffer sql = new StringBuffer();
		sql.append("select a.ID as SYS_OFFICE_ID,a.OFFICENAME,a.ORGANIZCODE");
		sql.append(" from SYS_OFFICE a");
		sql.append(" where a.ORGANIZCODE like '%").append(code).append("%'");
		sql.append(" and a.LOGOFF = 0");
		try {
			list = dao.doSqlQuery(sql.toString(), null);
			list = SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.emr.schemas.EMR_KSMBDY");
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 病历病程模板类别修改
	 */
	public void doUpdateEmrTemplatesType(List<Map<String, Object>> body,
			Context ctx) throws ModelDataOperationException {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			String tableName = "";
			String templateType = "";
			String chtName = "";
			String isTy = "";
			String code = "";
			String pydm = "";
			for (Map<String, Object> m : body) {
				pydm = "";
				if (!m.get("CHTNAME").equals(m.get("CHTNAME_T"))) {
					pydm = PyConverter
							.getFirstLetter((String) m.get("CHTNAME"));
					pydm = "c.PYDM = '" + pydm.toUpperCase() + "',";
				}
				if (m.get("TABLENAME").equals("chtemplate")) {
					tableName = "CHTEMPLATE";
					templateType = "TEMPLATETYPE";
					chtName = "CHTNAME";
					code = "CHTCODE";
				} else if (m.get("TABLENAME").equals("chrecordtemplate")) {
					tableName = "CHRECORDTEMPLATE";
					templateType = "TEMPLACETYPE";
					chtName = "CHRTHNAME";
					code = "CHRTCODE";
				}
				if (m.get("FRAMEWORKCODE") == null) {
					m.put("FRAMEWORKCODE", "");
				}
				if (m.get("TEMPLATETYPE") == null) {
					m.put("TEMPLATETYPE", "");
				}
				isTy = "NOTAVAILABLE";
				StringBuffer sql = new StringBuffer();
				sql.append("update ").append(tableName);
				sql.append(" c set c.FRAMEWORKCODE = '")
						.append(m.get("FRAMEWORKCODE")).append("',");
				sql.append("c.").append(templateType).append(" = '")
						.append(m.get("TEMPLATETYPE")).append("',");
				sql.append("c.INOROUTTYPE = ").append(m.get("INOROUTTYPE"))
						.append(",");
				sql.append("c.").append(chtName).append(" = '")
						.append(m.get("CHTNAME")).append("',");
				sql.append(pydm);
				sql.append("c.").append(isTy).append(" = ")
						.append(m.get("ISTY"));
				sql.append(" where c.").append(code).append(" = ")
						.append(m.get("CHTCODE"));
				dao.doSqlUpdate(sql.toString(), parameters);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("保存失败!", e);
		}
	}

	/**
	 * 科室模板订阅保存
	 * 
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveKsTemplateDy(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ValidateException,
			PersistentDataOperationException {
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		List<Map<String, Object>> select = (List<Map<String, Object>>) req
				.get("select");
		Map<String, Object> m = (Map<String, Object>) req.get("ks");
		if (body.size() == 0) {
			return;
		}
		for (Map<String, Object> o : body) {
			sql = new StringBuffer();
			sql.append("delete from EMR_KSMBDY where TEMPLATE_ID =").append(
					o.get("CHTCODE"));
			sql.append(" and T_TABLE_NAME='").append(o.get("TABLENAME"))
					.append("'");
			sql.append(" and SYS_OFFICE_ID = ").append(m.get("SYS_OFFICE_ID"));
			dao.doUpdate(sql.toString(), null);
		}
		for (Map<String, Object> o : select) {
			Map<String, Object> s = new HashMap<String, Object>();
			s.put("SYS_OFFICE_ID",
					Integer.parseInt(m.get("SYS_OFFICE_ID") + ""));
			s.put("TEMPLATE_ID", Integer.parseInt(o.get("CHTCODE") + ""));
			s.put("T_TABLE_NAME", o.get("TABLENAME"));
			s.put("ORGANIZCODE", m.get("ORGANIZCODE"));
			dao.doSave("create", "phis.application.emr.schemas.EMR_KSMBDY", s,
					false);
		}
	}

	/**
	 * 病历病程模板预览
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void doLoadChTemplate(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StringBuffer sql = new StringBuffer();
		try {
			if ("1".equals(body.get("MBLB").toString())) {
				sql = new StringBuffer();
				sql.append("select CHTCODE as code,CHTNAME,XMLTEXTPAT as xml from CHTEMPLATE");
				sql.append(" where CHTCODE = ").append(body.get("CHTCODE"));
			} else if ("2".equals(body.get("MBLB").toString())) {
				sql = new StringBuffer();
				sql.append("select CHRTCODE as code,CHRTHNAME as CHTNAME,CHRTXMLTEXTTEXT as xml from CHRECORDTEMPLATE");
				sql.append(" where CHRTCODE = ").append(body.get("CHTCODE"));
			}
			list = dao.doSqlQuery(sql.toString(), parameters);
			Map<String, Object> map = list.get(0);
			Blob blob = (Blob) map.get("XML");
			InputStream is = blob.getBinaryStream();
			Reader utf8_reader = new InputStreamReader(is, "UTF-8");
			StringBuffer utf8Text = new StringBuffer();
			int charValue = 0;
			while ((charValue = utf8_reader.read()) != -1) {
				utf8Text.append((char) charValue);
			}
			res.put("uft8Text", utf8Text.toString());
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新签名元素
	 * */
	@SuppressWarnings("unchecked")
	public void doLoadQmys(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, Exception {
		List<Map<String, Object>> list_js = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		list_js = dao.doList(null, "JSXH asc",
				"phis.application.emr.schemas.EMR_YLJS");
		if (list_js.size() > body.size()) {
			for (int i = body.size(); i < list_js.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("JKYS", list_js.get(i).get("JSMC"));
				map.put("GLYS", "," + list_js.get(i).get("JSMC"));
				map.put("BZXX", "");
				map.put("JKLB", "2");
				map.put("JKKZ", "," + list_js.get(i).get("JSMC"));
				list.add(map);
			}
			res.put("add", true);
		}
		if (list_js.size() < body.size()) {
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> b = body.get(i);
				for (int j = 0; j < list_js.size(); j++) {
					Map<String, Object> js = list_js.get(j);
					if (js.get("JSMC").equals(b.get("JKYS"))) {
						list.add(b);
					}
				}
			}
			res.put("add", false);
		}
		if (list_js.size() == body.size()) {
			res.put("add", true);
		}
		list = SchemaUtil.setDictionaryMassageForList(list,
				"phis.application.emr.schemas.EMR_JKYS");
		res.put("body", list);
	}

	/**
	 * 保存签名元素
	 * */
	@SuppressWarnings("unchecked")
	public void doSaveQmys(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, Exception {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		StringBuffer sql = new StringBuffer();
		sql.append("delete from EMR_JKYS");
		dao.doSqlUpdate(sql.toString(), null);
		for (Map<String, Object> o : body) {
			dao.doSave("create", "phis.application.emr.schemas.EMR_JKYS", o,
					false);
		}
	}

	/**
	 * 特殊符号检查
	 * */
	@SuppressWarnings("unchecked")
	public void doSaveTsfh(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, Exception {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		StringBuffer sql = new StringBuffer();
		sql.append("select XMBH from EMR_BLZD where XMMC='")
				.append(body.get("XMMC")).append("'");
		sql.append(" and XMQZ='").append(body.get("XMQZ")).append("'");
		list = dao.doSqlQuery(sql.toString(), null);
		if (list.size() > 0) {
			res.put("code", "301");
			res.put("msg", "同类别下不能添加重复的符号样式!");
			return;
		}
	}

	public void doGetBLLBInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, ExpException {
		Map<String, Object> record = (Map<String, Object>) req.get("body");
		String lbbh = record.get("LBBH") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LBBH", lbbh);
		String sql = "select nvl(count(*),0) as NUM from EMR_KBM_BLLB where SJLBBH=:LBBH and LBBH!=:LBBH";
		try {
			long num = (Long) dao.doLoad(sql, parameters).get("NUM");
			res.put("num", num);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doSaveHLZDPre(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, ExpException {
		Map<String, Object> record = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZDMC", record.get("ZDMC") + "");
		String sql = "";
		if (record.containsKey("ZDXH") && record.get("ZDXH") != null
				&& record.get("ZDXH") != "") {
			parameters.put("ZDXH", Long.parseLong(record.get("ZDXH") + ""));
			sql = "select nvl(count(*),0) as NUM from EMR_HLZD where ZDXH!=:ZDXH and ZDMC=:ZDMC";
		} else {
			sql = "select nvl(count(*),0) as NUM from EMR_HLZD where ZDMC=:ZDMC";
		}
		try {
			long num = (Long) dao.doLoad(sql, parameters).get("NUM");
			res.put("num", num);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		// String lbbh = record.get("LBBH") + "";
		// Map<String, Object> parameters = new HashMap<String, Object>();
		// parameters.put("LBBH", lbbh);
		// String sql =
		// "select nvl(count(*),0) as NUM from EMR_KBM_BLLB where SJLBBH=:LBBH and LBBH!=:LBBH";
		// try {
		// long num = (Long) dao.doLoad(sql, parameters).get("NUM");
		// res.put("num", num);
		// } catch (PersistentDataOperationException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 
	 * @Description:查询病历类别下面第一级病历是不是存在重复的记录
	 * @date：2014-9-15下午4:45:07
	 * @Author:Dingxc
	 * @ReturnType:long(返回一共几条记录，没有就返回：0)
	 */
	public long doGetBLYDLBBMInfo(String YDLBBM) {
		String sql = "select count(*) as NUM from EMR_KBM_BLLB where MLBZ = 1 and YDLBBM = :YDLBBM";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("YDLBBM", YDLBBM);
		try {
			long num = (Long) dao.doLoad(sql, parameters).get("NUM");
			return num;
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveTimeLimitSet(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, Exception {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		StringBuffer sql = new StringBuffer();
		sql.append("delete from EMR_SXSJ where JGID=:JGID");
		dao.doSqlUpdate(sql.toString(), parameters);
		for (Map<String, Object> o : body) {
			o.put("JGID", jgid);
			dao.doSave("create", "phis.application.emr.schemas.EMR_SXSJ", o,
					false);
		}
	}

	public List<Map<String, Object>> listTimeLimitSet(String schema)
			throws ModelDataOperationException {
		List<Map<String, Object>> result = null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		try {
			String hql = "select GZXH as GZXH,GZMC as GZMC ,KSSJ as KSSJ,SXSX as SXSX,WCBL as WCBL, JGID as JGID from EMR_SXSJ";
			Long num = dao.doCount("EMR_SXSJ", "JGID=:JGID", parameters);
			if (num > 0) {
				result = dao.doQuery(hql + " where JGID=:JGID", parameters);
			} else {
				result = dao.doQuery(hql + " where JGID=null", null);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("list timeLimitSet fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "时限时间列表查询失败！");
		}
		return result;
	}
}
