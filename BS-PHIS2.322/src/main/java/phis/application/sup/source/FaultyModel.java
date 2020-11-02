package phis.application.sup.source;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.CNDHelper;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.dao.exception.DataAccessException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
 * @ClassName: FaultyModel
 * @Description: TODO(报损单)
 * @date 2013-5-21 上午10:51:48
 * 
 */
public class FaultyModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(FaultyModel.class);

	public FaultyModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 * @Title: saveFaulty
	 * @Description: TODO(保存报损)
	 * @param @param req
	 * @param @param res
	 * @param @param ctx 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings({ "unchecked" })
	public void doSaveOrUpdaterFaulty(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ValidateException,
			PersistentDataOperationException, JsonParseException,
			JsonMappingException, IOException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_BS02");// 获得报损单明细
		Long[] delIds = (Long[]) body.get("delIds");
		String op = (String) body.get("op");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> faulty = (Map<String, Object>) body.get("WL_BS01");// 获得报损单据
		faulty.put("KFXH", user.getProperty("treasuryId"));
		faulty.put("ZDRQ", new Date());
		faulty.put("ZDGH", user.getUserId());
		faulty.put("DJZT", 0);
		faulty.put("JGID", JGID);
		BigDecimal DJJE = new BigDecimal(0);
		if ("create".equals(op)) {// create
			faulty = dao.doSave(op, BSPHISEntryNames.WL_BS01_FORM_CON, faulty,
					false);
			Long djxh = Long.parseLong(faulty.get("DJXH").toString());
			for (Map<String, Object> mat : mats) {
				mat.put("DJXH", djxh);
				mat = dao
						.doSave("create", BSPHISEntryNames.WL_BS02, mat, false);
			}
		} else if ("update".equals(op)) {
			long DJXH = Long.parseLong(faulty.get("DJXH") + "");
			dao.removeByFieldValue("DJXH", DJXH, BSPHISEntryNames.WL_BS02);
			for (Map<String, Object> mat : mats) {
				mat.put("DJXH", DJXH);
				mat = dao
						.doSave("create", BSPHISEntryNames.WL_BS02, mat, false);

			}
			// 根据delIds删除将要删除的记录
			if (delIds != null && delIds.length > 0) {
				for (Long id : delIds) {
					dao.removeByFieldValue("JLXH", id, BSPHISEntryNames.WL_BS02);
				}
			}
		}
		// 计算单据金额
		for (Map<String, Object> mat : mats) {
			BigDecimal WZJE = new BigDecimal(mat.get("WZJE").toString());
			DJJE = DJJE.add(WZJE);
		}
		faulty.put("DJJE", DJJE.doubleValue());
		dao.doSave("update", BSPHISEntryNames.WL_BS01_FORM_CON, faulty, false);
		res.put("DJXH", Long.parseLong(faulty.get("DJXH").toString()));
	}

	public void doGetDjztByDjxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (body.get("DJXH") == null) {
			res.put("error", true);
			return;
		}
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad(
					BSPHISEntryNames.WL_BS01_FORM_CON,
					Long.parseLong(body.get("DJXH") + ""));
			if (map == null || map.size() == 0) {
				res.put("error", true);
				return;
			}
			res.put("djzt", map.get("DJZT"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "报损记录读取失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void findButtonbarStaus(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (body.get("DJXH") == null) {
			return;
		}
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad(
					BSPHISEntryNames.WL_BS01_FORM_CON,
					Long.parseLong(body.get("DJXH") + ""));
			if (map.size() == 0) {
				res.put("error", true);
				return;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			res.put("error", true);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "报损记录读取失败");
		}
	}

	/**
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws ModelDataOperationException
	 * @Title: verify
	 * @Description: TODO(审核报损单)
	 * @param @param req
	 * @param @param res
	 * @param @param ctx
	 * @param @throws ValidateException
	 * @param @throws PersistentDataOperationException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings({ "unchecked" })
	public void verify(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ValidateException,
			PersistentDataOperationException, ModelDataOperationException,
			JsonParseException, JsonMappingException, IOException {
		doSaveOrUpdaterFaulty(req, res, ctx);// 保存报损单
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> faulty = (Map<String, Object>) body.get("WL_BS01");// 获得报损单据
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_BS02");// 获得报损单明细
		faulty = dao.doLoad(BSPHISEntryNames.WL_BS01_FORM_CON,
				Long.parseLong(faulty.get("DJXH") + ""));
		Integer djzt = Integer.parseInt(faulty.get("DJZT").toString());
		if (djzt != 0) {// 单据状态为零才能审核.
			res.put("DJZT", true);
			return;
		}
		// 根据报损失方式进行判断库存、科室数量和资产数量是否满足报损数量
		Long bsfs = Long.parseLong(faulty.get("BSFS").toString());
		for (Map<String, Object> mat : mats) {
			Map<String, Object> wzdic = dao.doLoad(BSPHISEntryNames.WL_WZZD,
					Long.parseLong(mat.get("WZXH") + ""));
			Long kcxh = Long.parseLong(mat.get("KCXH").toString());
			double bssl = Double.parseDouble(mat.get("WZSL").toString());
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("KCXH", kcxh);
			parameters
					.put("KFXH", Integer.parseInt(user
							.getProperty("treasuryId").toString()));
			Integer glfs = Integer.parseInt(wzdic.get("GLFS").toString());// 物资字典的管理方式
			if (bsfs == 0) {// 在库报损
				Map<String, Object> wzkcObj = findWZKCByParams(ctx, kcxh);
				double wzsl = 0;
				double yksl = 0;
				if (wzkcObj != null) {
					wzsl = Double.parseDouble(wzkcObj.get("WZSL").toString());
					yksl = Double.parseDouble(wzkcObj.get("YKSL").toString());
				}
				if (glfs != 3 && (wzsl - yksl) < bssl) {
					res.put("isNotEnoughFaulty", true);
					throw new ModelDataOperationException(
							ServiceCode.CODE_SERVICE_ERROR, "报损数量大于库存数量!");
				}
				yksl += bssl;
				BigDecimal b = new BigDecimal(yksl);
				double retYKSL = b.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				parameters.put("YKSL", retYKSL);
				if (glfs == 3) {
					Map<String, Object> zbxhObj = dao.doLoad(
							BSPHISEntryNames.WL_ZCZB,
							Long.parseLong(mat.get("ZBXH") + ""));
					zbxhObj.put("CLBZ", 1);// 处理标识
					zbxhObj.put("WZZT", 2);// 待报损状态
					dao.doSave("update", BSPHISEntryNames.WL_ZCZB, zbxhObj,
							false);
				} else {
					dao.doUpdate(
							"update WL_WZKC SET YKSL=:YKSL where  KCXH=:KCXH and KFXH=:KFXH ",
							parameters);
				}
			} else if (bsfs == 1) {// 科室报损
				parameters.put("KSDM",
						Long.parseLong(faulty.get("BSKS").toString()));
				Map<String, Object> kszcObj = dao
						.doLoad("select YKSL as YKSL,WZSL as WZSL from WL_KSZC where  KCXH=:KCXH and KFXH=:KFXH and KSDM=:KSDM ",
								parameters);
				double yksl = Double
						.parseDouble(kszcObj.get("YKSL").toString());// 预扣数量
				double wzsl = Double
						.parseDouble(kszcObj.get("WZSL").toString());
				if (glfs != 3 && (wzsl - yksl) < bssl) {
					res.put("isNotEnoughFaulty", true);
					throw new ModelDataOperationException(
							ServiceCode.CODE_SERVICE_ERROR, "报损数量大于库存数量!");
				}
				yksl += bssl;
				BigDecimal b = new BigDecimal(yksl);
				double retYKSL = b.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				parameters.put("YKSL", retYKSL);
				if (glfs == 3) {
					Map<String, Object> zbxhObj = dao.doLoad(
							BSPHISEntryNames.WL_ZCZB,
							Long.parseLong(mat.get("ZBXH") + ""));
					zbxhObj.put("CLBZ", 1);// 处理标识
					zbxhObj.put("WZZT", 2);// 待报损状态
					dao.doSave("update", BSPHISEntryNames.WL_ZCZB, zbxhObj,
							false);
				} else {

					dao.doUpdate(
							"update WL_KSZC SET YKSL=:YKSL where  KCXH=:KCXH and KFXH=:KFXH and KSDM=:KSDM",
							parameters);
				}
			}
		}// for end

		faulty.put("LZDH", getLzdh());
		faulty.put("DJZT", 1);
		faulty.put("SHRQ", new Date());
		faulty.put("SHGH", user.getUserId());
		faulty = dao.doSave("update", BSPHISEntryNames.WL_BS01_FORM_CON,
				faulty, false);// 保存报损单
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.flush();
	}

	/**
	 * @Description: TODO(弃审操作)
	 * @param @param req
	 * @param @param res
	 * @param @param ctx 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public void unVerify(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws NumberFormatException,
			PersistentDataOperationException, ValidateException,
			ModelDataOperationException, ExpException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> faulty = (Map<String, Object>) body.get("WL_BS01");// 获得报损单据
		faulty = dao.doLoad(BSPHISEntryNames.WL_BS01_FORM_CON,
				Long.parseLong(faulty.get("DJXH") + ""));
		Long djxh = Long.parseLong(faulty.get("DJXH").toString());
		List<Map<String, Object>> mats = dao.doList(
				CNDHelper.toListCnd("['eq',['$','DJXH'],['i'," + djxh + "]]"),
				"JLXH", BSPHISEntryNames.WL_BS02);
		Integer djzt = Integer.parseInt(faulty.get("DJZT").toString());
		if (djzt != 1) {// 单据状态为1才能弃审.
			res.put("DJZT", true);
			return;
		}

		// 根据报损失方式进行判断库存、科室数量和资产数量是否满足报损数量
		Long bsfs = Long.parseLong(faulty.get("BSFS").toString());
		for (Map<String, Object> mat : mats) {
			Map<String, Object> wzdic = dao.doLoad(BSPHISEntryNames.WL_WZZD,
					Long.parseLong(mat.get("WZXH") + ""));
			Long kcxh = Long.parseLong(mat.get("KCXH").toString());

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("KCXH", kcxh);
			parameters
					.put("KFXH", Integer.parseInt(user
							.getProperty("treasuryId").toString()));
			Integer glfs = Integer.parseInt(wzdic.get("GLFS").toString());// 物资字典的管理方式
			double bssl = Double.parseDouble(mat.get("WZSL").toString());
			if (bsfs == 0) {// 在库报损

				Map<String, Object> wzkcObj = findWZKCByParams(ctx, kcxh);
				double yksl = Double
						.parseDouble(wzkcObj.get("YKSL").toString());// 预扣数量
				yksl -= bssl;
				BigDecimal b = new BigDecimal(yksl);
				double retYKSL = b.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				parameters.put("YKSL", retYKSL);

				if (glfs == 3) {
					Map<String, Object> zbxhObj = dao.doLoad(
							BSPHISEntryNames.WL_ZCZB,
							Long.parseLong(mat.get("ZBXH") + ""));
					zbxhObj.put("CLBZ", 0);// 处理标识
					zbxhObj.put("WZZT", 0);// 待报损状态
					dao.doSave("update", BSPHISEntryNames.WL_ZCZB, zbxhObj,
							false);
				} else {
					dao.doUpdate(
							"update WL_WZKC SET YKSL=:YKSL where  KCXH=:KCXH and KFXH=:KFXH ",
							parameters);
				}
			} else if (bsfs == 1) {// 科室报损
				parameters.put("KSDM",
						Long.parseLong(faulty.get("BSKS").toString()));
				Map<String, Object> kszcObj = dao
						.doLoad("select YKSL as YKSL from WL_KSZC where  KCXH=:KCXH and KFXH=:KFXH and KSDM=:KSDM",
								parameters);
				double yksl = Double
						.parseDouble(kszcObj.get("YKSL").toString());// 预扣数量
				yksl -= bssl;
				BigDecimal b = new BigDecimal(yksl);
				double retYKSL = b.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				parameters.put("YKSL", retYKSL);
				if (glfs == 3) {
					Map<String, Object> zbxhObj = dao.doLoad(
							BSPHISEntryNames.WL_ZCZB,
							Long.parseLong(mat.get("ZBXH") + ""));
					zbxhObj.put("CLBZ", 0);// 处理标识
					zbxhObj.put("WZZT", 1);// 待报损状态
					dao.doSave("update", BSPHISEntryNames.WL_ZCZB, zbxhObj,
							false);
				} else {

					dao.doUpdate(
							"update WL_KSZC SET YKSL=:YKSL where  KCXH=:KCXH and KFXH=:KFXH and KSDM=:KSDM",
							parameters);
				}
			}
		}// for end

		faulty.put("LZDH", getLzdh());
		faulty.put("DJZT", 0);
		faulty.put("SHRQ", null);
		faulty.put("SHGH", null);
		faulty = dao.doSave("update", BSPHISEntryNames.WL_BS01_FORM_CON,
				faulty, false);// 保存报损单
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.flush();
	}

	/**
	 * @Title: doGetTjslByWzzd
	 * @Description: TODO(回填推荐数量)
	 * @param @param req
	 * @param @param res
	 * @param @param ctx
	 * @param @throws PersistentDataOperationException
	 * @param @throws ModelDataOperationException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public void doGetTjslByWzzd(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws PersistentDataOperationException,
			ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Long kcxh = Long.parseLong(body.get("KCXH").toString());
		Long wzxh = Long.parseLong(body.get("WZXH").toString());
		Long zbxh = null;
		if (body.get("ZBXH") != null) {
			zbxh = Long.parseLong(body.get("ZBXH").toString());
		}
		Integer zblb = Integer.parseInt(body.get("ZBLB").toString());
		Integer bsfs = Integer.parseInt(body.get("BSFS").toString());
		Long bsks = null;
		if (bsfs == 1 && body.get("BSKS") != null) {
			bsks = Long.parseLong(body.get("BSKS").toString());
		}
		Map<String, Object> wzkcObj = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		parameters.put("KFXH",
				Integer.parseInt(user.getProperty("treasuryId").toString()));
		parameters.put("KCXH", kcxh);
		parameters.put("JGID", JGID);
		parameters.put("WZXH", wzxh);
		parameters.put("ZBLB", zblb);
		if (zbxh != null) {
			parameters = new HashMap<String, Object>();
			parameters
					.put("KFXH", Integer.parseInt(user
							.getProperty("treasuryId").toString()));
			parameters.put("ZBXH", zbxh);
			wzkcObj = dao
					.doLoad("select ZBXH as ZBXH from WL_ZCZB where KFXH=:KFXH and ZBXH=:ZBXH",
							parameters);
			if (wzkcObj != null) {
				res.put("TJSL", 1f);
			} else {
				res.put("TJSL", 0f);
			}
			return;
		} else {
			if (bsfs == 0) {
				wzkcObj = dao
						.doLoad("select (WZSL-NVL(YKSL,0) ) as TJSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH  and JGID=:JGID and ZBLB=:ZBLB and WZXH=:WZXH",
								parameters);
				res.put("TJSL",
						wzkcObj == null ? 0 : Double.parseDouble(wzkcObj.get(
								"TJSL").toString()));
			} else if (bsfs == 1) {
				parameters.put("KSDM", bsks);
				wzkcObj = dao
						.doLoad("select (WZSL-NVL(YKSL,0) ) as TJSL from WL_KSZC where KCXH=:KCXH and KFXH=:KFXH and JGID=:JGID and ZBLB=:ZBLB and WZXH=:WZXH and KSDM=:KSDM",
								parameters);
				res.put("TJSL",
						wzkcObj == null ? 0 : Double.parseDouble(wzkcObj.get(
								"TJSL").toString()));
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void doGetZCBH(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws PersistentDataOperationException,
			ModelDataOperationException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			Long zbxh = 0l;
			if (body.get("ZBXH") != null) {
				zbxh = Long.parseLong(body.get("ZBXH") + "");
			}
			Map<String, Object> ret = dao
					.doLoad(BSPHISEntryNames.WL_ZCZB, zbxh);
			if (ret != null) {
				if (ret.get("WZBH") != null) {
					res.put("ZCBH", ret.get("WZBH"));
				}
			} else {
				res.put("ZCBH", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: doCommit
	 * @Description: TODO(记账)
	 * @param @param req
	 * @param @param res
	 * @param @param ctx
	 * @param @throws NumberFormatException
	 * @param @throws PersistentDataOperationException
	 * @param @throws ValidateException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCommit(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws NumberFormatException,
			PersistentDataOperationException, ValidateException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> faulty = (Map<String, Object>) body.get("WL_BS01");// 获得报损单据
		Long lzfs = Long.parseLong(faulty.get("LZFS").toString());

		Integer bsfs = Integer.parseInt(faulty.get("BSFS").toString());
		Long bsks = null;
		if (bsfs == 1) {
			bsks = Long.parseLong(faulty.get("BSKS").toString());
		}
		Integer zblb = Integer.parseInt(faulty.get("ZBLB").toString());
		UserRoleToken user = UserRoleToken.getCurrent();
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_BS02");// 获得报损单明细
		for (Map<String, Object> mat : mats) {
			mat.put("LZFS", lzfs);
			if (bsfs == 1) {
				mat.put("KSDM", bsks);
			}
			mat.put("YWFS", bsfs);
			mat.put("ZBLB", zblb);
		}
		faulty = dao.doLoad(BSPHISEntryNames.WL_BS01_FORM_CON,
				Long.parseLong(faulty.get("DJXH") + ""));
		BSPHISUtil.Uf_access(mats, lzfs, dao, ctx);
		faulty.put("DJZT", 2);
		faulty.put("JZRQ", new Date());
		faulty.put("JZGH", user.getUserId());
		faulty = dao.doSave("update", BSPHISEntryNames.WL_BS01_FORM_CON,
				faulty, false);// 保存报损单
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.flush();
	}

	private Map<String, Object> findWZKCByParams(Context ctx, Long kcxh)
			throws PersistentDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		parameters.put("KFXH",
				Integer.parseInt(user.getProperty("treasuryId").toString()));
		parameters.put("KCXH", kcxh);
		Map<String, Object> wzkcObj = dao
				.doLoad("select (WZSL-NVL(YKSL,0) ) as KTSL,YKSL as YKSL,WZSL as WZSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
						parameters);
		return wzkcObj;
	}

	@SuppressWarnings("unchecked")
	public void doGetKSZCByks(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws DataAccessException {
		UserRoleToken user = UserRoleToken.getCurrent();
		// 库房序号
		Integer kfxh = Integer
				.parseInt(user.getProperty("treasuryId") == null ? "0" : user
						.getProperty("treasuryId") + "");// 库房序号
		String JGID = user.getManageUnit().getId();
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		Long bsks = null;// 科室代码
		Long bsfs = null;// 报损方式

		if (req.get("BSKS") != null) {
			bsks = Long.parseLong(req.get("BSKS") + "");
		}
		if (req.get("BSFS") != null) {
			bsfs = Long.parseLong(req.get("BSFS") + "");
		}
		Long zblb = 0L;
		if (req.get("ZBLB") != null) {
			zblb = Long.parseLong(req.get("ZBLB") + "");
		}
		StringBuffer sql = new StringBuffer();

		if (bsfs == 1 && bsks != null) {
			sql.append(
					"SELECT WL_SCCJ.CJMC as CJMC,WL_KSZC.JLXH as JLXH,WL_KSZC.KSDM as KSDM,WL_KSZC.KFXH as KFXH,WL_KSZC.ZBLB as ZBLB,WL_KSZC.WZXH as WZXH,WL_KSZC.CJXH as CJXH,WL_KSZC.SCRQ as SCRQ,WL_KSZC.SXRQ as SXRQ,WL_KSZC.WZPH as WZPH,WL_KSZC.WZSL - WL_KSZC.YKSL as WZSL,WL_KSZC.WZJG as WZJG,WL_KSZC.WZJE as WZJE,WL_KSZC.KCXH as KCXH,WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_WZZD.WZDW as WZDW,WL_WZZD.PYDM as PYDM,WL_WZZD.WBDM as WBDM,WL_WZZD.JXDM as JXDM,WL_WZZD.QTDM as QTDM,WL_WZZD.GLFS as GLFS,0.00 as ZRSL,0 as ZBXH,0 as XZBZ,'' as WZBH")
					.append(" from WL_WZZD,WL_KSZC,WL_SCCJ ")
					.append(" where WL_KSZC.WZXH = WL_WZZD.WZXH AND")
					.append(" WL_SCCJ.CJXH = WL_KSZC.CJXH ").append(" AND ")
					.append("( WL_KSZC.KSDM = ").append(bsks).append(" AND ")
					.append(bsfs + " = 1 ) AND ").append("WL_KSZC.ZBLB = ")
					.append("'" + zblb + "'").append(" AND ")
					.append("WL_KSZC.KFXH = ").append("'" + kfxh + "'")
					.append(" AND ").append("WL_WZZD.GLFS = 2 AND ")
					.append(" WL_KSZC.WZSL > WL_KSZC.YKSL AND")
					.append(" WL_KSZC.JGID ='" + JGID + "'");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = que[4].substring(0, que[4].indexOf("]"))
						.trim();
				String qur = "and ( WL_WZZD.WZMC  like '" + parString
						+ "' or WL_WZZD.PYDM LIKE '" + parString.toUpperCase()
						+ "' or WL_WZZD.WBDM LIKE '" + parString.toUpperCase()
						+ "' )";
				sql.append(qur);
			}

			sql.append(" UNION ");

			sql.append("SELECT WL_SCCJ.CJMC as CJMC,WL_ZCZB.ZBXH as JLXH,WL_ZCZB.ZYKS as KSDM,WL_ZCZB.KFXH as KFXH,WL_ZCZB.ZBLB as ZBLB,WL_ZCZB.WZXH as WZXH,WL_ZCZB.CJXH as CJXH,null as SCRQ,null as SXRQ,null as WZPH,1 as WZSL,WL_ZCZB.CZYZ as WZJG,WL_ZCZB.CZYZ as WZJE,WL_ZCZB.KCXH as KCXH,WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_WZZD.WZDW as WZDW,WL_WZZD.PYDM as PYDM,WL_WZZD.WBDM as WBDM,WL_WZZD.JXDM as JXDM,WL_WZZD.QTDM as QTDM,WL_WZZD.GLFS as GLFS,0.00  as ZRSL,WL_ZCZB.ZBXH as ZBXH,0  as  XZBZ,WL_ZCZB.WZBH as WZBH");
			sql.append(" FROM WL_ZCZB, WL_WZZD ,WL_SCCJ ")
					.append(" WHERE WL_ZCZB.WZXH = WL_WZZD.WZXH AND")
					.append(" WL_SCCJ.CJXH = WL_ZCZB.CJXH ")
					.append(" AND (( WL_ZCZB.ZYKS = " + bsks + " AND " + bsfs
							+ " = 1 And WL_ZCZB.WZZT = 1 )")
					.append(" Or ( " + bsfs
							+ " = 0 And  WL_ZCZB.WZZT = 0 )) And ")
					.append("WL_ZCZB.ZBLB = '" + zblb + "' AND ")
					.append("WL_ZCZB.KFXH = '" + kfxh + "' AND ")
					.append(" WL_ZCZB.CLBZ = 0 AND")
					.append(" WL_ZCZB.JGID ='" + JGID + "'");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = que[4].substring(0, que[4].indexOf("]"))
						.trim();
				String qur = "and ( WL_WZZD.WZMC  like '" + parString
						+ "' or WL_WZZD.PYDM LIKE '" + parString.toUpperCase()
						+ "' or WL_WZZD.WBDM LIKE '" + parString.toUpperCase()
						+ "' )";
				sql.append(qur);
			}

		} else if (bsfs == 0) {// 在库报损
			sql.append("SELECT WL_SCCJ.CJMC as CJMC,WL_WZKC.JLXH as JLXH,0 as KSDM,WL_WZKC.KFXH as KFXH,WL_WZKC.ZBLB as ZBLB,WL_WZKC.WZXH as WZXH,WL_WZKC.CJXH as CJXH,WL_WZKC.SCRQ as SCRQ,WL_WZKC.SXRQ as SXRQ,WL_WZKC.WZPH as WZPH,WL_WZKC.WZSL - WL_WZKC.YKSL as WZSL,WL_WZKC.WZJG as WZJG,WL_WZKC.WZJE as WZJE,WL_WZKC.KCXH as KCXH,WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_WZZD.WZDW as WZDW,WL_WZZD.PYDM as PYDM,WL_WZZD.WBDM as WBDM,WL_WZZD.JXDM as JXDM,WL_WZZD.QTDM as QTDM,WL_WZZD.GLFS as GLFS,0.00 as ZRSL,0 as ZBXH,0 as XZBZ,'' as WZBH");
			sql.append(" FROM WL_WZKC, WL_WZZD ,WL_SCCJ ")
					.append(" WHERE WL_WZKC.WZXH = WL_WZZD.WZXH AND ")
					.append(" WL_SCCJ.CJXH = WL_WZKC.CJXH AND")
					.append(" WL_WZKC.ZBLB=").append("'" + zblb + "'")
					.append(" AND ").append(" WL_WZKC.KFXH=")
					.append("'" + kfxh + "'").append(" AND ")
					.append("( WL_WZZD.GLFS = 1 OR WL_WZZD.GLFS = 2 ) AND ")
					.append(bsfs + "=0 AND ")
					.append("WL_WZKC.WZSL > WL_WZKC.YKSL AND")
					.append(" WL_WZKC.JGID ='" + JGID + "'");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = que[4].substring(0, que[4].indexOf("]"))
						.trim();
				String qur = "and ( WL_WZZD.WZMC  like '" + parString
						+ "' or WL_WZZD.PYDM LIKE '" + parString.toUpperCase()
						+ "' or WL_WZZD.WBDM LIKE '" + parString.toUpperCase()
						+ "' )";
				sql.append(qur);
			}

			sql.append(" UNION ");

			sql.append("SELECT WL_SCCJ.CJMC as CJMC,WL_ZCZB.ZBXH as JLXH,WL_ZCZB.ZYKS as KSDM,WL_ZCZB.KFXH as KFXH,WL_ZCZB.ZBLB as ZBLB,WL_ZCZB.WZXH as WZXH,WL_ZCZB.CJXH as CJXH,null as SCRQ,null as SXRQ,null as WZPH,1 as WZSL,WL_ZCZB.CZYZ as WZJG,WL_ZCZB.CZYZ as WZJE,WL_ZCZB.KCXH as KCXH,WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_WZZD.WZDW as WZDW,WL_WZZD.PYDM as PYDM,WL_WZZD.WBDM as WBDM,WL_WZZD.JXDM as JXDM,WL_WZZD.QTDM as QTDM,WL_WZZD.GLFS as GLFS,0.00  as ZRSL,WL_ZCZB.ZBXH as ZBXH,0  as  XZBZ,WL_ZCZB.WZBH as WZBH");
			sql.append(" FROM WL_ZCZB, WL_WZZD ,WL_SCCJ ")
					.append(" WHERE WL_ZCZB.WZXH = WL_WZZD.WZXH AND")
					.append(" WL_SCCJ.CJXH = WL_ZCZB.CJXH ")
					.append(" AND (( WL_ZCZB.ZYKS = " + bsks + " AND " + bsfs
							+ " = 1 And WL_ZCZB.WZZT = 1 )")
					.append(" Or ( " + bsfs
							+ " = 0 And  WL_ZCZB.WZZT = 0 )) And ")
					.append("WL_ZCZB.ZBLB = '" + zblb + "' AND ")
					.append("WL_ZCZB.KFXH = '" + kfxh + "' AND ")
					.append(" WL_WZZD.GLFS = 3 AND ")
					.append(" WL_ZCZB.CLBZ = 0 AND")
					.append(" WL_ZCZB.JGID ='" + JGID + "'");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = que[4].substring(0, que[4].indexOf("]"))
						.trim();
				String qur = "and ( WL_WZZD.WZMC  like '" + parString
						+ "' or WL_WZZD.PYDM LIKE '" + parString.toUpperCase()
						+ "' or WL_WZZD.WBDM LIKE '" + parString.toUpperCase()
						+ "' )";
				sql.append(qur);
			}
		}
		StringBuffer sql_count = new StringBuffer();
		sql_count.append("select count(*) as TOTAL from (")
				.append(sql.toString()).append(")");
		Session ss;
		if (ctx.has(Context.DB_SESSION)) {
			ss = (Session) ctx.get(Context.DB_SESSION);
		} else {
			throw new DataAccessException("MissingDatebaseConnection");
		}

		List<Map<String, Object>> l = ss.createSQLQuery(sql_count.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		Long count = Long.parseLong(l.get(0).get("TOTAL") + "");
		List<Map<String, Object>> mats = ss.createSQLQuery(sql.toString())
				.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		res.put("totalCount", count);
		res.put("body", mats);
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
	}

	/**
	 * @Title: getLzdh
	 * @Description: TODO(获得报损单的流转单号)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	private String getLzdh() {
		String lzdh = "BS";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		lzdh += sdf.format(new Date());
		return lzdh;
	}

	/**
	 * @throws PersistentDataOperationException
	 * @Title: doDelFaultyById
	 * @Description: TODO(删除未审核的报损单)
	 * @param @param req
	 * @param @param res
	 * @param @param ctx 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public void doDelFaultyById(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws PersistentDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Long djxh = Long.parseLong(body.get("DJXH").toString());
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", djxh);
		StringBuffer deleteHql = new StringBuffer();
		deleteHql.append("delete from WL_BS02 where DJXH=:DJXH");
		dao.doUpdate(deleteHql.toString(), parameters);
		dao.removeByFieldValue("DJXH", djxh, BSPHISEntryNames.WL_BS01);

	}
}
