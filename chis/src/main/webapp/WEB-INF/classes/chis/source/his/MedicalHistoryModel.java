package chis.source.his;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.impl.SessionFactoryImpl;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.util.exp.ExpressionProcessor;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;

public class MedicalHistoryModel extends EmpiModel {

	private BaseDAO dao;

	public MedicalHistoryModel(BaseDAO dao) {
		super(dao);
		this.dao = dao;
	}

	/**
	 * 载入病历信息
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> loadClinicInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> clinicInfo = new HashMap<String, Object>();
		String type = (String) body.get("type");// type 1:病历 2：诊断 3：处方 处置 5：所有
		Long clinicId = Long.parseLong(((Integer) body.get("clinicId"))
				.toString());
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		String[] types = type.split(",");
		try {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals("1") || types[i].equals("5")) {
					String hql = "select ZSXX as ZSXX,XBS as XBS,JWS as JWS,TGJC as TGJC,FZJC as FZJC,T as T,P as P,R as R,SSY as SSY,SZY as SZY,KS as KS,YT as YT,HXKN as HXKN,OT as OT,FT as FT,FX as FX,PZ as PZ,QT as QT from MS_BCJL where JZXH=:JZXH";
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JZXH", clinicId);

					clinicInfo.put("ms_bcjl", dao.doLoad(hql, parameters));

				}
				if (types[i].equals("2") || types[i].equals("5")) {// 诊断
					StringBuffer hql = new StringBuffer(
							"select ICD10 as ICD10,ZDMC as ZDMC,DEEP as DEEP,ZZBZ as ZZBZ,FZBZ as FZBZ from ");
					hql.append(" MS_BRZD where JZXH=:JZXH and JGID=:JGID order by PLXH ");
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JZXH", clinicId);
					parameters.put("JGID", manageUnit);
					clinicInfo.put("ms_brzd",
							dao.doSqlQuery(hql.toString(), parameters));

				}
				if (types[i].equals("3") || types[i].equals("5")) {// 处方处置
					Map<String, Object> res = loadCF01(clinicId, ctx);
					List<Map<String, Object>> cfsbList = (List<Map<String, Object>>) res
							.get("cf01s");
					String cfsbs = "";
					for (Map<String, Object> cfsb : cfsbList) {
						if (cfsbs.length() > 0) {
							cfsbs += ",";
						}
						cfsbs += cfsb.get("CFSB");
					}
					if (cfsbs.trim().length() > 0) {
						String cnd = "['in',['$','a.CFSB'],[" + cfsbs + "]]";
						// clinicInfo.put("measures", dao.doList(
						// CNDHelper.toListCnd(cnd),
						// "c.CFLX,a.CFSB,a.SBXH",
						// BSPHISEntryNames.MS_CF02 + "_BL"));
						clinicInfo.put("measures",
								queryCfmx(CNDHelper.toListCnd(cnd), ctx));
					}
					String cnd = "['eq', ['$', 'c.JZXH'],['d', " + clinicId
							+ "]]";
					clinicInfo.put("disposal", dao.doList(
							CNDHelper.toListCnd(cnd), "YJZH ,SBXH",
							BSCHISEntryNames.MS_YJ02_CIC));
				}
			}
		} catch (Exception e) {
			throw new ModelDataOperationException("载入病人就诊信息失败，请联系管理员!", e);
		}
		return clinicInfo;
	}
	
	public Map<String, Object> loadCF01(Object jzxh, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		if (jzxh != null) {
			String hql = "select CFSB as CFSB,CFHM as CFHM,CFLX as CFLX,FPHM as FPHM from MS_CF01 where JZXH=:JZXH ORDER BY CFLX,CFHM";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JZXH", jzxh);
			try {
				List<Map<String, Object>> list = dao.doSqlQuery(hql, params);
				res.put("cf01s", list);
			} catch (PersistentDataOperationException e) {
				logger.error(
						"fail to load ms_cf02 information by database reason",
						e);
				throw new ModelDataOperationException("处方信息查找失败！", e);
			}
		}
		return res;
	}
	
	public List<Map<String, Object>> queryCfmx(List<?> cnd, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		try {
			String where = ExpressionProcessor.instance().toString(cnd);
			StringBuffer hql = new StringBuffer();
			// String sql=HQLHelper.buildQueryHql(cnd, null, "MS_CF02_CF", ctx);
			hql.append(
					"select a.SBXH as SBXH,a.YPXH as YPXH,a.CFSB as CFSB,a.YPZH as YPZH,b.YPMC as YPMC,a.YFGG as YFGG,a.YFBZ as YFBZ,a.CFTS as CFTS,a.YCJL as YCJL,b.JLDW as JLDW,b.YPJL as YPJL,a.YPYF as YPYF,a.MRCS as MRCS,a.YYTS as YYTS,a.YPSL as YPSL,a.YFDW as YFDW,a.GYTJ as GYTJ,a.YPZS as YPZS,a.YPCD as YPCD,a.YPDJ as YPDJ,a.ZFBL as ZFBL,a.BZXX as BZXX,a.HJJE as HJJE,a.PSPB as PSPB,a.PSJG as PSJG,a.FYGB as FYGB,b.KSBZ as KSBZ, b.YCYL as YCYL,b.TYPE as TYPE,b.TSYP as TSYP,b.JBYWBZ as JBYWBZ,a.BZMC as BZMC,a.SFJG as SFJG, a.ZFPB as ZFPB ,c.KPDY as KPDY")
					.append(" from MS_CF02 a left outer join YK_TYPK b on a.YPXH=b.YPXH left outer join ZY_YPYF c on a.GYTJ=c.YPYF where ")
					.append(where);
			ret = dao.doSqlQuery(hql.toString(), null);
			if (ret == null) {
				return ret;
			}

			// Schema schema =
			// SchemaController.instance().getSchema("MS_CF02_CF");
			// List<SchemaItem> itemList = schema.getAllItemsList();
			Schema schema = SchemaController.instance().get(
					"phis.application.cic.schemas.MS_CF02_CF");
			List<SchemaItem> itemList = schema.getItems();
			for (Map<String, Object> map_cf02 : ret) {
				if (parseLong(map_cf02.get("YPXH")) == 0) {
					map_cf02.put("YPMC", map_cf02.get("BZMC"));
					map_cf02.put("YPJL", 1);
				}
				for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
						.hasNext();) {
					SchemaItem item = iterator.next();
					if (item.isCodedValue()) {
						String schemaKey = item.getId();
						if (map_cf02.get(schemaKey) == null) {
							continue;
						}
						String key = StringUtils.trimToEmpty(map_cf02.get(
								schemaKey).toString());
						if (!key.equals("") && key != null) {
							// Map<String, String> dicValue = new
							// HashMap<String, String>();
							// dicValue.put("key", key);
							// dicValue.put("text", item.getDisplayValue(key));
							map_cf02.put(schemaKey + "_text",
									item.getDisplayValue(key));
						}
					}
				}
			}
		} catch (ExpException e) {
			logger.error("Prescription detail query fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处方明细查询失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Prescription detail query fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处方明细查询失败");
		} catch (ControllerException e) {
			logger.error("Prescription detail query fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处方明细查询失败");
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-30
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null || "".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	public void doQueryHistoryList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		// User user = (User) ctx.get("user.instance");
		// String JGID = user.get("manageUnit.id");// 用户的机构ID
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			// parameters.put("JGID", JGID);
			// long total =
			// dao.doCount(BSPHISEntryNames.MS_BRDA+" a,"+BSPHISEntryNames.MPI_DemographicInfo+" b",
			// "a.EMPIID = b.empiId and a.JDJG=:JGID", parameters);
			StringBuffer hql = new StringBuffer(
					"select a.JZXH as JZXH,a.GHXH as GHXH,a.BRBH as BRBH,b.EMPIID as EMPIID,a.JGID as JGID,c.JZHM as JZHM,d.cardNo as JZKH,");
			hql.append("b.MZHM as MZHM,b.BRXZ as BRXZ,b.BRXM as BRXM,b.BRXB as BRXB,b.CSNY as CSNY,"
					+ toChar("c.GHSJ", "yyyy-MM-dd hh24:mi:ss")
					+ " as GHSJ,c.CZPB as CZPB,a.KSDM as KSDM,a.YSDM as YSDM,");
			hql.append("a.ZYZD as ZYZD,"
					+ toChar("a.KSSJ", "yyyy-MM-dd hh24:mi:ss")
					+ " as KSSJ,"
					+ toChar("a.JSSJ", "yyyy-MM-dd hh24:mi:ss")
					+ " as JSSJ,a.JZZT as JZZT,a.YYXH as YYXH,a.FZRQ as FZRQ,a.GHFZ as GHFZ from ");
			hql.append(" YS_MZ_JZLS a,");
			hql.append(" MS_BRDA b left outer join ");
			hql.append(" MPI_Card d on d.cardTypeCode='04' and d.empiId = b.EMPIID,");
			hql.append(" MS_GHMX c where a.BRBH = b.BRID and a.GHXH=c.SBXH ");
			if (req.get("cnd") != null) {
				List<Object> cnd = (List<Object>) req.get("cnd");
				hql.append(" and " + ExpressionProcessor.instance().toString(cnd));
			}
			hql.append(" order by c.GHSJ");
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					parameters);
			list=SchemaUtil.setDictionaryMessageForList(list,
					"chis.application.his.schemas.HIS_MedicalHistory");
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("cardNo", list.get(i).get("CARDNO"));
				list.get(i).put("idCard", list.get(i).get("IDCARD"));
				list.get(i).put("JZXH", list.get(i).get("JZXH") != null ? Long
						.parseLong(list.get(i).get("JZXH") + "") : 0);
				list.get(i).put("GHXH", list.get(i).get("GHXH") != null ? Long
						.parseLong(list.get(i).get("GHXH") + "") : 0);
				list.get(i).put("BRBH", list.get(i).get("BRBH") != null ? Long
						.parseLong(list.get(i).get("BRBH") + "") : 0);
			}
			res.put("totalCount", list.size());
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * tochar函数转化
	 * 
	 * @param prop
	 * @param format
	 * @return
	 */
	public static String toChar(String prop, String format) {
		// 由于使用的是sql查询，无法使用hql中的str自动转化，增加判断收工更改，只适用oracle与DB2
		String tochar = "";
		// WebApplicationContext wac = AppContextHolder.get();
		// SessionFactory sf = (SessionFactory) wac.getBean("mySessionFactory");
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		String dialectName = ((SessionFactoryImpl) sf).getDialect().getClass()
				.getName();
		if (dialectName.contains("MyDB2")) {
			tochar = "char(" + prop + ")";
		} else {
			tochar += "to_char(" + prop + ",'" + format + "')";
		}
		return tochar;
	}

}
