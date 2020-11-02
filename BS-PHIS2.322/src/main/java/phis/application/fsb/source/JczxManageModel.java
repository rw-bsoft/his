package phis.application.fsb.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

public class JczxManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(JczxManageModel.class);

	public JczxManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 家床申请 保存
	 * */
	@SuppressWarnings("unchecked")
	public void doSaveJcbrsq(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws PersistentDataOperationException,
			ExpException, ValidateException {
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isEmpty(op)) {
			op = "create";
		}
		// body.put("SQFS", Long.valueOf(body.get("SQFS").toString()));
		// body.put("CSNY", formatter.format(body.get("CSNY")+""));
		// body.put("CSNY", formatter.format(body.get("CSNY")+""));
		dao.doSave(op, schema, body, false);
	}

	/**
	 * 家床申请 列表查询
	 * */
	public void doLoadJcsqList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ExpException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		StringBuffer sql = new StringBuffer();
		StringBuffer cnd = new StringBuffer();
		cnd.append(" and b.JGID = '").append(jgid).append("'");
		if (req.containsKey("cnd")) {
			cnd.append(" and ");
			cnd.append(ExpressionProcessor.instance().toString(
					(List<?>) req.get("cnd")));
			cnd = new StringBuffer(cnd.toString().replace("a.BRXM", "b.BRXM"));
		}
		Long count = dao.doCount("JC_BRSQ b", " 1=1 " + cnd.toString(), null);
		if (count <= 0) {
			return;
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		int first = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
			first = (pageNo - 1) * pageSize;
		}
		sql = new StringBuffer();
		sql.append("select * from ( select row_.*, rownum rownum_ from (");
		sql.append("select id,mzhm,zyhm,brxm,brxb,csny,brxz,sfzh,lxdz,lxr,yhgx,");
		sql.append("lxdh,sqrq,a.sqzt,a.sqfs,a.jgid from JC_BRSQ a");
		sql.append(" where ").append(cnd);
		// sql.append(" UNION ALL ");
		// sql.append("select a.id,b.mzhm,'' as zyhm,b.brxm,b.brxb,b.csny,a.brxz,b.sfzh,a.lxdz,a.lxr,a.yhgx,");
		// sql.append("a.lxdh,a.sqrq,a.sqzt,a.sqfs,a.jgid from JC_BRSQ a inner join MS_BRDA b on a.JGID = b.JDJG");
		// sql.append(" and a.MZHM = b.MZHM and a.BRID = b.BRID");
		// sql.append(" where a.sqfs = 1");
		// sql.append(cnd.toString().replace("JGID", "JDJG"));
		sql.append(") row_ where rownum <= ").append(pageNo * pageSize)
				.append(") where rownum_ > ").append(first);
		sql.append(" order by id desc");

		List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(), null);
		list = SchemaUtil.setDictionaryMassageForList(list,
				BSPHISEntryNames.JC_BRSQ_LIST);
		res.put("body", list);
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		res.put("totalCount", count);
	}

	/**
	 * 家床申请 表单查询
	 * */
	public void doLoadJcsqForm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ExpException {

	}

	@SuppressWarnings("unchecked")
	public void doSelectBrdaByMzhm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ExpException,
			ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String hmV = (String) body.get("value");
		List<?> cnd = CNDHelper.createArrayCnd("and",
				CNDHelper.createSimpleCnd("eq", "MZHM", "s", hmV),
				CNDHelper.createSimpleCnd("ne", "SQZT", "i", 4));
		List<?> cndBrda = CNDHelper.createSimpleCnd("eq", "MZHM", "s", hmV);

		String cardOrMZHM = ParameterUtil.getParameter(jgid, "CARDORMZHM", "1",
				"卡号门诊号码判断,1为卡号2为门诊号码", ctx);
		if (parseInt(cardOrMZHM) == 1) {
			String KLX = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "KLX", "04",
					"01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JZKH", hmV);
			Map<String, Object> MPI_Card = dao
					.doLoad("select a.empiId as empiId,a.status as status,b.MZHM as MZHM from MPI_Card a,MS_BRDA b where "
							+ "a.empiId=b.EMPIID and a.cardNo=:JZKH and a.cardTypeCode="
							+ KLX, parameters);
			if (MPI_Card != null) {
				if (parseLong(MPI_Card.get("status")) == 1) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "该卡号已挂失");
				}
				if (parseLong(MPI_Card.get("status")) == 2) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "该卡号已注销");
				}
				if (parseLong(MPI_Card.get("status")) == 3) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "该卡号已失效");
				}
				String mzhm = (String) MPI_Card.get("MZHM");
				cnd = CNDHelper.createArrayCnd("and",
						CNDHelper.createSimpleCnd("eq", "MZHM", "s", mzhm),
						CNDHelper.createSimpleCnd("ne", "SQZT", "i", 4));
				cndBrda = CNDHelper.createSimpleCnd("eq", "MZHM", "s", mzhm);
			}
		}
		Map<String, Object> map_brda = null;

		Map<String, Object> map_jc_brdj = dao.doLoad(cnd,
				BSPHISEntryNames.JC_BRSQ_FORM, false);
		if (map_jc_brdj != null) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "该病人已登记申请!");
		}
		map_brda = dao.doLoad(cndBrda, BSPHISEntryNames.MS_BRDA, false);
		if (map_brda == null || "".equals(map_brda)) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "该号码不存在!");
		}
		map_brda.put("LXR", map_brda.get("LXRM"));
		if (map_brda.get("CSNY") != null) {
			map_brda.put(
					"RYNL",
					BSPHISUtil.getPersonAge(
							(Date) map_brda.get("CSNY"), null).get(
							"ages"));
		}
		Map<String, Object> lxgx_mpr = new HashMap<String, Object>();
		lxgx_mpr.put("mzhm", map_brda.get("MZHM"));
		String lxgx_sql = "select LXGX as LXGX from JC_BRRY where MZHM=:mzhm";
		Map<String, Object> lxgx_map = dao.doLoad(lxgx_sql, lxgx_mpr);
		if(lxgx_map!=null&&lxgx_map.size()>0){
			if(lxgx_map.containsKey("LXGX"))
				map_brda.put("YHGX", lxgx_map.get("LXGX"));
		}
		SchemaUtil.setDictionaryMassageForForm(map_brda, "phis.application.fsb.schemas.JC_BRSQ_FORM");
		res.put("MZBR", map_brda);
		cnd = CNDHelper.createSimpleCnd("eq", "JGID", "s", jgid);
		Map<String, Object> brzd = dao.doList(
				CNDHelper.createArrayCnd(
						"and",
						cnd,
						CNDHelper.toListCnd("['and',['eq',['$','BRID'],['i',"
								+ map_brda.get("BRID")
								+ "]],['eq',['$','ZZBZ'],['i',1]]]")),
				"ZDSJ desc", BSPHISEntryNames.MS_BRZD_CIC, 1, 1, null);

		List<?> brzds = (List<?>) brzd.get("body");
		if (brzds.size() > 0) {
			brzd = (Map<String, Object>) brzds.get(0);
			Map<String, Object> bcjl = dao.doLoad(CNDHelper.createArrayCnd(
					"and",
					cnd,
					CNDHelper.toListCnd("['and',['eq',['$','BRID'],['i',"
							+ map_brda.get("BRID")
							+ "]],['eq',['$','JZXH'],['i'," + brzd.get("JZXH")
							+ "]]]")), BSPHISEntryNames.MS_BCJL);
			res.put("BRZD", brzd);
			res.put("BCJL", bcjl);
		}
	}

	/**
	 * 住院 出院诊断记录
	 * 
	 * @param ZYHM
	 * @throws ModelDataOperationException
	 * */
	@SuppressWarnings("unchecked")
	public void doSelectZyzdjlByField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ExpException,
			ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String msg = "Success";
		Map<String, Object> map_brry = null;
		String zyhmN = (String) body.get("name");
		String zyhmV = BSPHISUtil.get_public_fillleft(body.get("value") + "",
				"0", BSPHISUtil.getRydjNo(jgid, "ZYHM", "", dao).length());
		List<?> cnd = CNDHelper.createArrayCnd("and",
				CNDHelper.createSimpleCnd("eq", "JGID", "s", jgid),
				CNDHelper.createSimpleCnd("eq", zyhmN, "s", zyhmV));
		map_brry = dao.doLoad(cnd, BSPHISEntryNames.ZY_BRRY, false);
		if (map_brry == null || "".equals(map_brry)) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "该住院号码不存在!");
		}
		if(parseInt(map_brry.get("CYPB"))<8){
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "在院病人不能家床申请!");
		}
		cnd = CNDHelper.createArrayCnd(
				"and",
				CNDHelper.createSimpleCnd("eq", "BRID", "s",
						map_brry.get("BRID")),
				CNDHelper.createSimpleCnd("ne", "SQZT", "i", 4));
		Map<String, Object> map_jc_brdj = dao.doLoad(cnd,
				BSPHISEntryNames.JC_BRSQ_FORM, false);
		if (map_jc_brdj != null) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "该病人已登记申请!");
		}

		cnd = CNDHelper
				.createSimpleCnd("eq", "BRID", "s", map_brry.get("BRID"));
		Map<String, Object> map_brda = null;
		map_brda = dao.doLoad(cnd, BSPHISEntryNames.MS_BRDA, false);
		map_brda.put("LXR", map_brda.get("LXRM"));
		map_brry.putAll(map_brda);
		// if (map_brry.containsKey("LXDH")) {
		// map_brry.remove("LXDH");
		// }
		res.put("BRRY", map_brry);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", parseLong(map_brry.get("ZYH")));
		String sql = "select a.ZYH as ZYH,b.JBMC as MSZD,b.ICD10 as JBBM,str(a.ZDSJ,'yyyy-mm-dd') as  ZDRQ from ZY_RYZD a,GY_JBBM b where a.ZDXH=b.JBXH and a.ZYH=:ZYH";
		List<Map<String, Object>> list_zyzdjl = dao.doQuery(sql, parameters);
		if (list_zyzdjl == null || list_zyzdjl.size() == 0) {
			msg = "该病人没有诊断信息!";
			res.put(Service.RES_CODE, 211);
			res.put(Service.RES_MESSAGE, msg);
			return;
		}
		res.put("ZYZD", list_zyzdjl.get(0));
	}

	public void doCheckRepetition(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		List<?> cnds = (List<?>) req.get("cnds");
		Map<String, Object> m = null;
		try {
			m = dao.doLoad(cnds, (String) req.get("entryName"), false);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		res.put("body", m);
	}

	/**
	 * 获取家床号（JCHM）和家床编号（JCBH）
	 */
	public void doSaveQueryJCHM(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> body = new HashMap<String, Object>();
		String JCHM = BSPHISUtil.getFsbRydjNo(JGID, "JCHM", "家床号码", dao);
		String JCBH = BSPHISUtil.getFsbRydjNo(JGID, "JCBH", "家床编号", dao);
		// String JCBH = BSPHISUtil.getFsbRydjNo(JGID, "JCBH", "家床编号", dao);
		// Map<String, Object> JCBH = dao.doSqlQuery(
		// "select nvl(max(to_number(JCBH)),0)+1 as JCBH from JC_BRRY",
		// new HashMap<String, Object>()).get(0);
		body.put("ZYHM", JCHM);
		body.put("JCBH", BSPHISUtil.get_public_fillleft(JCBH + "", "0",
				BSPHISUtil.getFsbRydjNo(JGID, "JCHM", "", dao).length()));
		String SJHM = BSPHISUtil.GetFsbBillnumber("收据", dao, ctx);
		if ("false".equals(SJHM)) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "请先维护收据号码!");
		}
		body.put("SJHM", SJHM);
		Map<String, Object> FKFS = dao.doLoad(
				"select FKFS as FKFS from GY_FKFS where SYLX='2' and MRBZ='1'",
				new HashMap<String, Object>());
		if (FKFS == null) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "未维护默认家床付款方式!");
		}
		List<Map<String, Object>> list_FKFS = dao
				.doQuery(
						"select FKFS as FKFS,HMCD as HMCD from GY_FKFS where SYLX='2' and FKLB='2'",
						new HashMap<String, Object>());
		res.put("fkfss", list_FKFS);
		body.put("JKFS", FKFS.get("FKFS"));
		res.put("body", body);
	}

	public void doSelectBrsqByField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		try {
			List<?> cnds = (List<?>) req.get("cnds");
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();
			if (req.containsKey("JZKH")) {
				String KLX = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "KLX", "04",
						"01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
				String JZKH = (String) req.get("JZKH");
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("JZKH", JZKH);
				Map<String, Object> MPI_Card = dao
						.doLoad("select a.empiId as empiId,a.status as status,b.MZHM as MZHM from MPI_Card a,MS_BRDA b where "
								+ "a.empiId=b.EMPIID and a.cardNo=:JZKH and a.cardTypeCode="
								+ KLX, parameters);
				if (MPI_Card != null) {
					if (parseLong(MPI_Card.get("status")) == 1) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "该卡号已挂失");
					}
					if (parseLong(MPI_Card.get("status")) == 2) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "该卡号已注销");
					}
					if (parseLong(MPI_Card.get("status")) == 3) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "该卡号已失效");
					}
					String mzhm = (String) MPI_Card.get("MZHM");
					cnds = CNDHelper.createSimpleCnd("eq", "MZHM", "s", mzhm);
				}
			}
			Map<String, Object> m = null;
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "JGID", "s", jgid);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "SQZT", "i", 2);

			Map<String, Object> p = dao.doLoad(cnds, BSPHISEntryNames.MS_BRDA,
					false);
			if (p != null) {
				res.put("message", "该门诊号码还未进行家床申请!");
			}
			List<?> cnd3 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<?> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnds);
			cnds = CNDHelper.createArrayCnd("and", cnd3, cnds);
			m = dao.doLoad(cnds, BSPHISEntryNames.JC_BRSQ_LIST, false);
			if (m == null || "".equals(m)) {
				List<Map<String, Object>> list = dao.doQuery(cnd4, "",
						BSPHISEntryNames.JC_BRSQ_LIST);
				if (list.size() == 0) {
					return;
				}
				res.put("message", "该门诊号码的家床未申请或申请未提交或已退回!");
				return;
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("BRID", m.get("BRID"));
			long ll_Count = dao.doCount("JC_BRRY",
					"BRID = :BRID and CYPB<8", parameters);
			if (ll_Count > 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "此病人已登记，请修改!");
			}
			long zy_Count = dao.doCount("ZY_BRRY",
					"BRID = :BRID and CYPB<8", parameters);
			if (zy_Count > 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "此病人已住院，请修改!");
			}
			List<Map<String, Object>> JC_BRRY = dao.doQuery(
					"select JCBH as JCBH from JC_BRRY where BRID=:BRID",
					parameters);
			if (JC_BRRY.size() > 0) {
				m.putAll(JC_BRRY.get(0));
			} else {
				String JGID = user.getManageUnit().getId();
				m.put("JCBH",
						BSPHISUtil.get_public_fillleft(
								BSPHISUtil.getFsbRydjNo(JGID, "JCBH", "家床编号",
										dao) + "", "0", BSPHISUtil
										.getFsbRydjNo(JGID, "JCHM", "", dao)
										.length()));
			}
			res.put("body", m);
		}catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取病人信息失败!");
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveBrdj(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("BRID", parseLong(body.get("BRID")));
		long ll_Count = dao.doCount("JC_BRRY",
				"BRID = :BRID and CYPB<8", parameters);
		if (ll_Count > 0) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "此病人已登记，请修改!");
		}
		long zy_Count = dao.doCount("ZY_BRRY",
				"BRID = :BRID and CYPB<8", parameters);
		if (zy_Count > 0) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "此病人已住院，请修改!");
		}
		parameters.put("JGID", JGID);
		Map<String, Object> parameters_JCBH = new HashMap<String, Object>();
		parameters_JCBH.put("BRID", parseLong(body.get("BRID")));
		parameters_JCBH.put("JGID", JGID);
		parameters_JCBH.put("JCBH", body.get("JCBH"));
//		ll_Count = dao.doCount("JC_BRRY",
//				"JCBH = :JCBH and JGID = :JGID and BRID<>:BRID",
//				parameters_JCBH);
//		if (ll_Count > 0) {
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "家床编号已经被使用，请重新保存!");
//		}
		dao.doUpdate(
				"update JC_BRSQ set SQZT=4 where BRID = :BRID and JGID = :JGID",
				parameters);
		Map<String, Object> genValues = null;
		String op = (String) req.get("op");
		body.put("BRID", parseLong(body.get("BRID")));
		body.put("BRXZ", parseInt(body.get("BRXZ")));
		body.put("BRXB", parseInt(body.get("BRXB")));
		body.put("LXGX", parseInt(body.get("LXGX")));
		body.put("JSCS", parseInt(body.get("JSCS")));
		body.put("JCLX", parseInt(body.get("JCLX")));
		body.put("SQFS", parseInt(body.get("SQFS")));
		body.put("DJRQ", new Date());
		body.put("RYRQ", new Date());
		doUpdateJchm(body);
		genValues = dao.doSave(op, BSPHISEntryNames.JC_BRRY_RYDJ, body, true);
		// 缴款
		String sjhm = BSPHISUtil.GetFsbBillnumber("收据", dao, ctx);
		if (body.get("JKJE") != null && (body.get("JKJE") + "").length() > 0
				&& Double.parseDouble(body.get("JKJE") + "") > 0) {
			Map<String, Object> JC_TBKK = new HashMap<String, Object>();
			JC_TBKK.put("JKJE", Double.parseDouble(body.get("JKJE") + ""));
			JC_TBKK.put("JKFS", body.get("JKFS"));
			JC_TBKK.put("ZPHM", body.get("ZPHM"));
			JC_TBKK.put("ZYH", parseLong(genValues.get("ZYH")));
			// ZY_TBKK.put("ZYHM", ZYHM);
			JC_TBKK.put("BRXM", body.get("BRXM"));
			JC_TBKK.put("SJHM", body.get("SJHM"));
			JC_TBKK.put("CZGH", user.getUserId());
			JC_TBKK.put("JGID", JGID);
			JC_TBKK.put("JKRQ", new Date());
			JC_TBKK.put("ZFPB", 0);
			JC_TBKK.put("ZCPB", 0);
			JC_TBKK.put("JSCS", 0);
			if (!"false".equals(sjhm)) {
				if (!sjhm.equals(body.get("SJHM"))) {
					JC_TBKK.put("SJHM", sjhm);
				}
			} else {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR,
						"您的收据已用完或尚未申领，不能进行缴款处理!");
			}
			BSPHISUtil.SetFsbBillNumber("收据", sjhm, dao, ctx);
			Map<String, Object> JKXH = dao.doSave("create",
					BSPHISEntryNames.JC_TBKK, JC_TBKK, false);
			res.put("JKXH", JKXH.get("JKXH"));
		}
		// genValues.putAll(doSaveJctbkk(op, body));
		
		// String jgid = user.getManageUnit().getId();
		// Map<String, Object> map_zyhm = new HashMap<String, Object>();
		// String JCHM = (String) body.get("ZYHM");
		// map_zyhm.put("JGID", jgid);
		// map_zyhm.put("CSMC", "JCHM");
		// String new_JCHM = String.format("%0" + JCHM.length() + "d",
		// Long.parseLong(JCHM) + 1);
		// map_zyhm.put("CSZ", new_JCHM);
		// dao.doSave("update", BSPHISEntryNames.GY_XTCS, map_zyhm, true);
//		ll_Count = dao.doCount("JC_BRRY",
//				"JCBH = :JCBH and JGID = :JGID and BRID<>:BRID",
//				parameters_JCBH);
//		if (ll_Count > 0) {
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "家床编号已经被使用，请重新保存!");
//		}
		res.put("body", genValues);
		}catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "家床登记保存失败!");
		}catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "家床登记保存失败!");
		}
	}

	/**
	 * 修改家床号（JCHM）和家床编号（JCBH）
	 */
	public void doUpdateJchm(Map<String, Object> obj) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> body = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String JCHM = (String) obj.get("ZYHM");
		long BRID = (Long) obj.get("BRID");
		body.put("JGID", jgid);
		body.put("CSMC", "JCHM");
		String new_JCHM = String.format("%0" + JCHM.length() + "d",
				Long.parseLong(JCHM) + 1);
		body.put("CSZ", new_JCHM);
		list.add(body);
		String JCBH = (String) obj.get("JCBH");
		Map<String,Object> parameters = new HashMap<String, Object>();
		parameters.put("JCBH", JCBH);
		parameters.put("JGID", jgid);
		parameters.put("BRID", BRID);
		try {
			String xtJCBH = BSPHISUtil.getFsbRydjNo(jgid, "JCBH", "家床编号", dao);
			long ll_Count = 0;
			if(JCBH.equals(xtJCBH)){
				ll_Count = dao.doCount("JC_BRRY",
						"JCBH = :JCBH and JGID = :JGID and BRID<>:BRID",
						parameters);
				if (ll_Count > 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "家床号码已经被使用，请重新保存!");
				}
			}else{
				ll_Count = dao.doCount("JC_BRRY",
						"JCBH = :JCBH and JGID = :JGID and BRID<>:BRID",
						parameters);
				if (ll_Count > 0) {
					JCBH = xtJCBH;
					parameters.put("JCBH", xtJCBH);
					ll_Count = dao.doCount("JC_BRRY",
							"JCBH = :JCBH and JGID = :JGID and BRID<>:BRID",
							parameters);
					if(ll_Count>0){
						throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "家床号码已经被使用，请重新保存!");
					}
				}
			}
			ll_Count = dao.doCount("JC_BRRY",
					"JCBH = :JCBH and JGID = :JGID and BRID=:BRID",
					parameters);
			if(ll_Count == 0){
				body = new HashMap<String, Object>();
				body.put("JGID", jgid);
				body.put("CSMC", "JCBH");
				String new_JCBH = String.format("%0" + JCBH.length() + "d",
						Long.parseLong(JCBH) + 1);
				body.put("CSZ", new_JCBH);
				list.add(body);
			}
			for (Map<String, Object> m : list) {
				dao.doSave("update", BSPHISEntryNames.GY_XTCS, m, true);
			}
		} catch (ModelDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "住院号码已经被使用，请重新保存!");
		} catch (PersistentDataOperationException e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "住院号码已经被使用，请重新保存!");
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "住院号码已经被使用，请重新保存!");
		}
	}

	/**
	 * 登记时保存预交款
	 * 
	 * @return
	 * */
	public Map<String, Object> doSaveJctbkk(String op, Map<String, Object> obj)
			throws ValidateException, PersistentDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		if (obj.get("JKJE") == null || "".equals(obj.get("JKJE"))) {
			return null;
		}
		Double jkje = parseDouble(obj.get("JKJE"));
		if (jkje > 0) {
			// obj.put("ZYH", obj.get("JCHM"));
			obj.put("CZGH", user.getUserId());
			obj.put("JKRQ", new Date());
			obj.put("ZFPB", 0);
			obj.put("ZCPB", 0);
			obj.put("JSCS", 0);
			return dao.doSave(op, BSPHISEntryNames.JC_TBKK, obj, false);
		}
		return null;
	}

	/**
	 * 住院号码查询 模糊查询
	 * 
	 * @throws PersistentDataOperationException
	 * */
	public String selectZyhm(String key, String JGID)
			throws PersistentDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String reStr;
		parameters.put("CSMC", key);
		parameters.put("JGID", JGID);
		Map<String, Object> BAHM = dao
				.doLoad("select CSZ as CSZ from GY_XTCS where CSMC= :CSMC and JGID = :JGID",
						parameters);
		if (BAHM == null) {
			return null;
		}
		reStr = (String) BAHM.get("CSZ");
		return reStr;
	}
	
	public void doGetExistJCBH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("BRID", parseLong(req.get("BRID")));
			long ll_Count = dao.doCount("JC_BRRY",
					"BRID = :BRID and CYPB<8", parameters);
			if (ll_Count > 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "此病人已登记，请修改!");
			}
			long zy_Count = dao.doCount("ZY_BRRY",
					"BRID = :BRID and CYPB<8", parameters);
			if (zy_Count > 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "此病人已住院，请修改!");
			}
			List<Map<String, Object>> JC_BRRY = dao.doQuery(
					"select JCBH as JCBH from JC_BRRY where BRID=:BRID",
					parameters);
			if (JC_BRRY.size() > 0) {
				res.putAll(JC_BRRY.get(0));
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取病人信息失败!");
		}
	}

	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

}
