package phis.application.hos.source;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Hcnservices.Exception_Exception;
import Hcnservices.HCNWebservices;
import Hcnservices.HCNWebservicesService;

import phis.application.xnh.source.XnhModel;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.account.user.UserRemoteLoader;
import ctd.dictionary.DictionaryController;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class HospitalAdmissionModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalAdmissionModel.class);

	public HospitalAdmissionModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void doQueryBrxx(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		// String userId = user.getUserId();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", manaUnitId);
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		try {
			String SJHM = BSPHISUtil.Getbillnumber("收据", dao, ctx);
			if ("false".equals(SJHM)) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "请先维护收据号码!");
			}
			if ("BAHM".equals(body.get("key"))) {
				String BAHM = BSPHISUtil.get_public_fillleft(body.get("value")
						+ "", "0",
						BSPHISUtil.getRydjNo(manaUnitId, "BAHM", "", dao)
								.length());
				parameters.put("BAHM", BAHM);
				parameters.remove("JGID");
				long count = dao.doCount("ZY_BRRY",
						"BAHM=:BAHM and cypb<8", parameters);
				parameters.put("JGID", manaUnitId);
				if (count > 0) {
					res.put("body", 1);
					res.put("BAHM", BAHM);
					return;
				}
				
				Map<String, Object> ZYH = dao.doLoad(
						"select max(ZYH) as ZYH,max(BRID) as BRID from "
								+ "ZY_BRRY"
								+ " where BAHM=:BAHM and JGID=:JGID",
						parameters);
				if (ZYH.get("ZYH") != null) {
					long BRID = Long.parseLong(ZYH.get("BRID") + "");
					Map<String, Object> parameters_jc = new HashMap<String, Object>();
					parameters_jc.put("BRID", BRID);
					long count_jc = dao.doCount("JC_BRRY",
							"BRID=:BRID and cypb<8", parameters_jc);
					if (count_jc > 0) {
						res.put("body", 2);
						res.put("BAHM", BAHM);
						return;
					}
					Map<String, Object> parametersYYBR = new HashMap<String, Object>();
					parametersYYBR.put("BRID", BRID);
					parametersYYBR.put("JGID", manaUnitId);
					parametersYYBR.put("JGBZ", 0);
					long lyybr = dao.doCount("ZY_YYBR",
							"BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ",
							parametersYYBR);
					if (lyybr > 0) {
						res.put(Service.RES_CODE, 608);
						res.put(Service.RES_MESSAGE,
								"该病人存在预约记录，需要从预约记录中调取，是否继续？");
					} else {
						parameters.remove("BAHM");
						parameters.put("BRID", BRID);
						parameters.remove("JGID");
						count = dao.doCount("ZY_BRRY",
								"BRID=:BRID and cypb<8",
								parameters);
						parameters.put("JGID", manaUnitId);
						if (count > 0) {
							res.put("body", 1);
							res.put("BAHM", BAHM);
							return;
						}
						Map<String, Object> BRXX = dao.doLoad(
								BSPHISEntryNames.MS_BRDA, BRID);
						BRXX.put("BAHM", BAHM);
						if (BRXX.get("CSNY") != null) {
							BRXX.put(
									"RYNL",
									BSPHISUtil.getPersonAge(
											(Date) BRXX.get("CSNY"), null).get(
											"ages"));
							if (BRXX.get("DWMC") != null
									&& BRXX.get("DWMC") != "") {
								BRXX.put("GZDW", BRXX.get("DWMC"));
							}
						}
						Map<String, Object> ZY_BRRY = dao.doLoad(
								BSPHISEntryNames.ZY_BRRY,
								Long.parseLong(ZYH.get("ZYH") + ""));
						BRXX.put("BRXZ", ZY_BRRY.get("BRXZ"));
						BRXX.put("YBKH", ZY_BRRY.get("YBKH"));
						if ("1".equals(ParameterUtil.getParameter(manaUnitId,
								"BAHMCXFP", ctx))) {
							BAHM = BSPHISUtil.getRydjNo(manaUnitId, "BAHM",
									"病案号码", dao);
							BRXX.put("BAHM", BAHM);
						} else {
							if ("1".equals(ParameterUtil.getParameter(
									manaUnitId, "BAHMDYZYHM", ctx))) {
								BRXX.put("ZYHM", ZY_BRRY.get("ZYHM"));
							}
						}
						BRXX.put("SJHM", SJHM);
						res.put("body", BRXX);
					}
				}
			} else if ("MZHM".equals(body.get("key"))) {
				parameters.put("MZHM", body.get("value"));
				parameters.remove("JGID");
				long count = dao.doCount("ZY_BRRY",
						"MZHM=:MZHM and cypb<8", parameters);
				parameters.put("JGID", manaUnitId);
				if (count > 0) {
					res.put("body", 1);
					return;
				}
				Map<String, Object> ZYH = dao.doLoad(
						"select max(ZYH) as ZYH,max(BAHM) as BAHM,max(BRID) as BRID from "
								+ "ZY_BRRY"
								+ " where MZHM=:MZHM and JGID=:JGID",
						parameters);
				if (ZYH.get("ZYH") != null) {
					long BRID = Long.parseLong(ZYH.get("BRID") + "");
					Map<String, Object> parameters_jc = new HashMap<String, Object>();
					parameters_jc.put("BRID", BRID);
					long count_jc = dao.doCount("JC_BRRY",
							"BRID=:BRID and cypb<8", parameters_jc);
					if (count_jc > 0) {
						res.put("body", 2);
						return;
					}
					Map<String, Object> parametersYYBR = new HashMap<String, Object>();
					parametersYYBR.put("BRID", BRID);
					parametersYYBR.put("JGID", manaUnitId);
					parametersYYBR.put("JGBZ", 0);
					long lyybr = dao.doCount("ZY_YYBR",
							"BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ",
							parametersYYBR);
					if (lyybr > 0) {
						res.put(Service.RES_CODE, 608);
						res.put(Service.RES_MESSAGE,
								"该病人存在预约记录，需要从预约记录中调取，是否继续？");
					} else {
						Map<String, Object> BRXX = dao.doLoad(
								BSPHISEntryNames.MS_BRDA, BRID);
						BRXX.put("BAHM", ZYH.get("BAHM"));
						// if("1".equals(ParameterUtil.getParameter(manaUnitId,
						// "BAHMDYZYHM", ctx))){
						// BRXX.put("ZYHM", ZYH.get("ZYHM"));
						// }
						if (BRXX.get("CSNY") != null) {
							BRXX.put(
									"RYNL",
									BSPHISUtil.getPersonAge(
											(Date) BRXX.get("CSNY"), null).get(
											"ages"));
							if (BRXX.get("DWMC") != null
									&& BRXX.get("DWMC") != "") {
								BRXX.put("GZDW", BRXX.get("DWMC"));
							}
						}
						Map<String, Object> ZY_BRRY = dao.doLoad(
								BSPHISEntryNames.ZY_BRRY,
								Long.parseLong(ZYH.get("ZYH") + ""));
						BRXX.put("BRXZ", ZY_BRRY.get("BRXZ"));
						BRXX.put("YBKH", ZY_BRRY.get("YBKH"));
						if ("1".equals(ParameterUtil.getParameter(manaUnitId,
								"BAHMCXFP", ctx))) {
							String BAHM = BSPHISUtil.getRydjNo(manaUnitId,
									"BAHM", "病案号码", dao);
							BRXX.put("BAHM", BAHM);
						} else {
							if ("1".equals(ParameterUtil.getParameter(
									manaUnitId, "BAHMDYZYHM", ctx))) {
								BRXX.put("ZYHM", ZY_BRRY.get("ZYHM"));
							}
						}
						BRXX.put("SJHM", SJHM);
						res.put("body", BRXX);
					}
				} else {
					parameters.remove("JGID");
					Map<String, Object> BRID = dao.doLoad(
							"select BRID as BRID,EMPIID as EMPIID from "
									+ "MS_BRDA" + " where MZHM=:MZHM",
							parameters);
					if (BRID != null) {
						Map<String, Object> parametersYYBR = new HashMap<String, Object>();
						String brid = (String) BRID.get("BRID");
						parametersYYBR.put("BRID", Long.parseLong(brid));
						parametersYYBR.put("JGID", manaUnitId);
						parametersYYBR.put("JGBZ", 0);
						long lyybr = dao.doCount("ZY_YYBR",
								"BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ",
								parametersYYBR);
						if (lyybr > 0) {
							res.put(Service.RES_CODE, 608);
							res.put(Service.RES_MESSAGE,
									"该病人存在预约记录，需要从预约记录中调取，是否继续？");
						} else {
							Map<String, Object> ZY_BRRYparameters = new HashMap<String, Object>();
							ZY_BRRYparameters.put("BRID", Long.parseLong(brid));
							long count1 = dao.doCount("ZY_BRRY",
									"BRID=:BRID and cypb<8",
									ZY_BRRYparameters);
							if (count1 > 0) {
								res.put("body", 1);
								return;
							}
							Map<String, Object> BRXX = dao.doLoad(
									BSPHISEntryNames.MS_BRDA, brid);
							if (BRXX.get("CSNY") != null) {
								BRXX.put(
										"RYNL",
										BSPHISUtil.getPersonAge(
												(Date) BRXX.get("CSNY"), null)
												.get("ages"));
								if (BRXX.get("DWMC") != null
										&& BRXX.get("DWMC") != "") {
									BRXX.put("GZDW", BRXX.get("DWMC"));
								}
							}
							BRXX.put("BAHM", BSPHISUtil.getRydjNo(manaUnitId,
									"BAHM", "病案号码", dao));
							BRXX.put("SJHM", SJHM);
							Map<String, Object> MPI_Cardparameters = new HashMap<String, Object>();
							MPI_Cardparameters
									.put("empiId", BRID.get("EMPIID"));
							long cardCount = dao.doCount("MPI_Card",
									"empiId=:empiId", MPI_Cardparameters);
							if (cardCount > 0) {
								List<Map<String, Object>> MPI_CardList = dao
										.doQuery("select cardNo as cardNo,status as status  from MPI_Card where cardTypeCode="+KLX+" and empiId=:empiId",
												MPI_Cardparameters);
								Map<String, Object> MPI_Card = new HashMap<String, Object>();
								if(MPI_CardList.size()>0){
									MPI_Card = MPI_CardList.get(0);
								}
								if (MPI_Card != null) {
									if(parseLong(MPI_Card.get("status"))==1){
										throw new ModelDataOperationException(
												ServiceCode.CODE_DATABASE_ERROR, "该卡号已挂失");
									}
									if(parseLong(MPI_Card.get("status"))==2){
										throw new ModelDataOperationException(
												ServiceCode.CODE_DATABASE_ERROR, "该卡号已注销");
									}
									if(parseLong(MPI_Card.get("status"))==3){
										throw new ModelDataOperationException(
												ServiceCode.CODE_DATABASE_ERROR, "该卡号已失效");
									}
									BRXX.put("YBKH", MPI_Card.get("cardNo"));
								} else {
									MPI_Card = dao
											.doQuery(
													"select cardNo as cardNo from MPI_Card where empiId=:empiId",
													MPI_Cardparameters).get(0);
									BRXX.put("YBKH", MPI_Card.get("cardNo"));
								}
							}
							res.put("body", BRXX);
						}
					}
				}
			} else if ("YBKH".equals(body.get("key"))) {
				parameters.put("YBKH", body.get("value"));
				parameters.remove("JGID");
				long count = dao.doCount("ZY_BRRY",
						"YBKH=:YBKH and cypb<8", parameters);
				parameters.put("JGID", manaUnitId);
				if (count > 0) {
					res.put("body", 1);
					return;
				}
				Map<String, Object> ZYH = dao
						.doLoad("select max(ZYH) as ZYH,max(BAHM) as BAHM,max(BRID) as BRID,max(ZYHM) as ZYHM from "
								+ "ZY_BRRY"
								+ " where YBKH=:YBKH and JGID=:JGID",
								parameters);
				if (ZYH.get("ZYH") != null) {
					long BRID = Long.parseLong(ZYH.get("BRID") + "");
					Map<String, Object> parameters_jc = new HashMap<String, Object>();
					parameters_jc.put("BRID", BRID);
					long count_jc = dao.doCount("JC_BRRY",
							"BRID=:BRID and cypb<8", parameters_jc);
					if (count_jc > 0) {
						res.put("body", 2);
						return;
					}
					Map<String, Object> parametersYYBR = new HashMap<String, Object>();
					parametersYYBR.put("BRID", BRID);
					parametersYYBR.put("JGID", manaUnitId);
					parametersYYBR.put("JGBZ", 0);
					long lyybr = dao.doCount("ZY_YYBR",
							"BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ",
							parametersYYBR);
					if (lyybr > 0) {
						res.put(Service.RES_CODE, 608);
						res.put(Service.RES_MESSAGE,
								"该病人存在预约记录，需要从预约记录中调取，是否继续？");
					} else {
						Map<String, Object> BRXX = dao.doLoad(
								BSPHISEntryNames.MS_BRDA, BRID);
						BRXX.put("YBKH", body.get("value"));
						BRXX.put("BAHM", ZYH.get("BAHM"));
						if ("1".equals(ParameterUtil.getParameter(manaUnitId,
								"BAHMCXFP", ctx))) {
							String BAHM = BSPHISUtil.getRydjNo(manaUnitId,
									"BAHM", "病案号码", dao);
							BRXX.put("BAHM", BAHM);
						} else {
							if ("1".equals(ParameterUtil.getParameter(
									manaUnitId, "BAHMDYZYHM", ctx))) {
								BRXX.put("ZYHM", ZYH.get("ZYHM"));
							}
						}
						if (BRXX.get("CSNY") != null) {
							BRXX.put(
									"RYNL",
									BSPHISUtil.getPersonAge(
											(Date) BRXX.get("CSNY"), null).get(
											"ages"));
							if (BRXX.get("DWMC") != null
									&& BRXX.get("DWMC") != "") {
								BRXX.put("GZDW", BRXX.get("DWMC"));
							}
						}
						BRXX.put("SJHM", SJHM);
						res.put("body", BRXX);
					}
				} else {
					parameters.remove("JGID");
					Map<String, Object> MPI_Card = dao
							.doLoad("select empiId as empiId,status as status from "
									+ "MPI_Card"
									+ " where cardNo=:YBKH and cardTypeCode="+KLX,
									parameters);
					if (MPI_Card != null) {
						if(parseLong(MPI_Card.get("status"))==1){
							throw new ModelDataOperationException(
									ServiceCode.CODE_DATABASE_ERROR, "该卡号已挂失");
						}
						if(parseLong(MPI_Card.get("status"))==2){
							throw new ModelDataOperationException(
									ServiceCode.CODE_DATABASE_ERROR, "该卡号已注销");
						}
						if(parseLong(MPI_Card.get("status"))==3){
							throw new ModelDataOperationException(
									ServiceCode.CODE_DATABASE_ERROR, "该卡号已失效");
						}
						Map<String, Object> BRIDparameters = new HashMap<String, Object>();
						BRIDparameters.put("EMPIID", MPI_Card.get("empiId"));
						Map<String, Object> BRID = dao.doLoad(
								"select BRID as BRID from " + "MS_BRDA"
										+ " where EMPIID=:EMPIID",
								BRIDparameters);
						if (BRID != null) {
							Map<String, Object> parametersYYBR = new HashMap<String, Object>();
							String brid = (String) BRID.get("BRID");
							parametersYYBR.put("BRID", Long.parseLong(brid));
							parametersYYBR.put("JGID", manaUnitId);
							parametersYYBR.put("JGBZ", 0);
							long lyybr = dao.doCount("ZY_YYBR",
									"BRID=:BRID and JGID=:JGID and JGBZ=:JGBZ",
									parametersYYBR);
							if (lyybr > 0) {
								res.put(Service.RES_CODE, 608);
								res.put(Service.RES_MESSAGE,
										"该病人存在预约记录，需要从预约记录中调取，是否继续？");
							} else {
								Map<String, Object> ZY_BRRYparameters = new HashMap<String, Object>();
								ZY_BRRYparameters.put("BRID",
										Long.parseLong(brid));
								long count1 = dao.doCount("ZY_BRRY",
										"BRID=:BRID and cypb<8",
										ZY_BRRYparameters);
								if (count1 > 0) {
									res.put("body", 1);
									return;
								}
								Map<String, Object> parameters_jc = new HashMap<String, Object>();
								parameters_jc.put("BRID", Long.parseLong(brid));
								long count_jc = dao.doCount("JC_BRRY",
										"BRID=:BRID and cypb<8", parameters_jc);
								if (count_jc > 0) {
									res.put("body", 2);
									return;
								}
								Map<String, Object> BRXX = dao.doLoad(
										BSPHISEntryNames.MS_BRDA, brid);

								if (BRXX.get("CSNY") != null) {
									BRXX.put(
											"RYNL",
											BSPHISUtil.getPersonAge(
													(Date) BRXX.get("CSNY"),
													null).get("ages"));
									if (BRXX.get("DWMC") != null
											&& BRXX.get("DWMC") != "") {
										BRXX.put("GZDW", BRXX.get("DWMC"));
									}
								}
								BRXX.put("BAHM", BSPHISUtil.getRydjNo(
										manaUnitId, "BAHM", "病案号码", dao));
								BRXX.put("SJHM", SJHM);
								BRXX.put("YBKH", body.get("value"));
								res.put("body", BRXX);
							}
						}
					}
				}
			}
			Map<String, Object> BRXX = (Map<String, Object>) res.get("body");
			if(BRXX!=null){
				parameters.put("BRID", Long.parseLong(BRXX.get("BRID")+""));
				parameters.put("JGID", manaUnitId);
				if(parameters.containsKey("YBKH")){
					parameters.remove("YBKH");
				}
				if(parameters.containsKey("MZHM")){
					parameters.remove("MZHM");
				}
				Long rycs = dao.doCount("ZY_BRRY", "brid=:BRID and jgid=:JGID and cypb=8", parameters);
				BRXX.put("RYCS", rycs+1);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	public void doQueryJZKH(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		long sbxh = Long.parseLong(req.get("sbxh") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("SBXH", sbxh);
		parameters.put("cardTypeCode", "04");
		try {
			Map<String, Object> jzkhMap = dao
					.doLoad("select c.cardNo as cardNo,b.BRID as BRID from ZY_YYBR a,MS_BRDA b,MPI_Card c where a.BRID=b.BRID and a.SBXH=:SBXH and c.cardTypeCode=:cardTypeCode and b.EMPIID=c.empiId",
							parameters);
			if (jzkhMap != null) {
				if (jzkhMap.containsKey("cardNo")) {
					res.put("cardNo", jzkhMap.get("cardNo") + "");
				}
				if (jzkhMap.containsKey("BRID")) {
					Map<String,Object> paramter = new HashMap<String, Object>();
					paramter.put("BRID", Long.parseLong(jzkhMap.get("BRID")+""));
					paramter.put("JGID", manaUnitId);
					Long rycs = dao.doCount("ZY_BRRY", "brid=:BRID and jgid=:JGID and cypb=8", paramter);
					res.put("RYCS", rycs+1);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	/**
	 * 获取住院号码和病案号码，收据号码
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveQueryZYHM(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> rebody = new HashMap<String, Object>();
		String ZYHM = BSPHISUtil.getRydjNo(JGID, "ZYHM", "住院号码", dao);
		String BAHM = BSPHISUtil.getRydjNo(JGID, "BAHM", "病案号码", dao);
		try {
			if ("1".equals(ParameterUtil.getParameter(JGID, "BAHMDYZYHM", ctx)
					+ "")) {
				if (!(BAHM).equals(ZYHM)) {
					Map<String, Object> GY_XTCSparameters = new HashMap<String, Object>();
					GY_XTCSparameters.put("CSZ", ZYHM);
					GY_XTCSparameters.put("CSMC", BSPHISSystemArgument.BAHM);
					GY_XTCSparameters.put("JGID", JGID);
					dao.doUpdate(
							"UPDATE GY_XTCS set CSZ = :CSZ where CSMC= :CSMC and JGID = :JGID",
							GY_XTCSparameters);
					BAHM = ZYHM;
				}
			}
			rebody.put("ZYHM", ZYHM);
			rebody.put("BAHM", BAHM);
			String SJHM = BSPHISUtil.Getbillnumber("收据", dao, ctx);
			if ("false".equals(SJHM)) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "请先维护收据号码!");
			}
			rebody.put("SJHM", SJHM);
			Map<String, Object> body = new HashMap<String, Object>();
			Map<String, Object> FKFS = dao
					.doLoad("select FKFS as FKFS from GY_FKFS where SYLX='2' and MRBZ='1'",
							body);
			if (FKFS == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "未维护默认住院付款方式!");
			}
			List<Map<String, Object>> list_FKFS = dao
					.doQuery(
							"select FKFS as FKFS,HMCD as HMCD from GY_FKFS where SYLX='2' and FKLB='2'",
							body);
			res.put("fkfss", list_FKFS);
			rebody.put("JKFS", FKFS.get("FKFS"));
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "付款方式维护错误!");
		}
		res.put("body", rebody);
	}

	/**
	 * 入院登记保存
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveRYDJ(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = (String) user.getUserId();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		body.put("DJRQ", new Date());
		String MZHM = body.get("MZHM") + "";
		long BRKS = Long.parseLong(body.get("BRKS") + "");
		String BRCH = body.get("BRCH") + "";
		String BAHM = body.get("BAHM") + "";
		String ZYHM = body.get("ZYHM") + "";
		long BRID = Long.parseLong(body.get("BRID") + "");
		if (body.get("BRXX") != null && body.get("BRXX") != "") {
			body.put("BRXX", Integer.parseInt(body.get("BRXX") + ""));
		}
		int ll_brxb = Integer.parseInt(body.get("BRXB") + "");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("MZHM", MZHM);
			parameters.put("JGID", JGID);
			long ll_Count = dao.doCount("ZY_BRRY",
					"MZHM = :MZHM AND JGID = :JGID AND CYPB < 8", parameters);
			if (ll_Count > 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR,
						"此门诊号码病人已入院不能重复入院，请修改!");
			}
			long Count_jc = dao.doCount("JC_BRRY",
					"MZHM = :MZHM AND JGID = :JGID AND CYPB < 8", parameters);
			if (Count_jc > 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR,
						"此门诊号码病人是家床病人，不能入院!");
			}
			if (BRCH != null && BRCH.length() > 0) {
				Map<String, Object> ZY_CWSZparameters = new HashMap<String, Object>();
				ZY_CWSZparameters.put("JGID", JGID);
				ZY_CWSZparameters.put("BRCH", BRCH);
				Map<String, Object> ZY_CWSZ = dao.doLoad(
						"SELECT ZYH as ZYH,CWKS as CWKS,CWXB as CWXB FROM "
								+ "ZY_CWSZ"
								+ " WHERE BRCH = :BRCH AND JGID = :JGID ",
						ZY_CWSZparameters);
				if (ZY_CWSZ == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病床不存在，请重新分配!");
				} else {
					if (ZY_CWSZ.get("ZYH") != null) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								"当前病床已分配给其他病人，请重新分配!");
					}
					if (Long.parseLong(ZY_CWSZ.get("CWKS") + "") != BRKS) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								"分配的病床与病人科室不符，请重新分配!");
					}
					if (Integer.parseInt(ZY_CWSZ.get("CWXB") + "") != 3
							&& Integer.parseInt(ZY_CWSZ.get("CWXB") + "") != ll_brxb) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								"分配的病床与病人性别不符，请重新分配!");
					}
				}
			}
			Map<String, Object> ZYHMparameters = new HashMap<String, Object>();
			Map<String, Object> GY_XTCSparameters1 = new HashMap<String, Object>();
			GY_XTCSparameters1.put("CSMC", BSPHISSystemArgument.ZYHM);
			GY_XTCSparameters1.put("JGID", JGID);
			// dao.doUpdate("UPDATE " + BSPHISEntryNames.GY_XTCS
			// + " set CSZ = CSZ where CSMC= :CSMC and JGID = :JGID",
			// GY_XTCSparameters1);
			String XTZYHM = BSPHISUtil.getRydjNo(JGID, "ZYHM", "", dao);
			if (!"1".equals(ParameterUtil.getParameter(JGID, "BAHMDYZYHM", ctx))) {
				body.put("ZYHM", XTZYHM);
			}
			ZYHMparameters.put("ZYHM", ZYHM);
			ZYHMparameters.put("JGID", JGID);
			ZYHMparameters.put("BRID", BRID);
			ll_Count = dao.doCount("ZY_BRRY",
					"ZYHM = :ZYHM and JGID = :JGID and BRID<>:BRID",
					ZYHMparameters);
			if (ll_Count > 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "住院号码已经被使用，请重新保存!");
			}
			if (ZYHM.equals(XTZYHM)) {
				String uZYHM = String.format("%0" + ZYHM.length() + "d",
						Long.parseLong(ZYHM) + 1);
				GY_XTCSparameters1.put("CSZ", uZYHM);
				dao.doUpdate("UPDATE " + "GY_XTCS"
						+ " set CSZ = :CSZ where CSMC= :CSMC and JGID = :JGID",
						GY_XTCSparameters1);
			}
			String XTBAHM = BSPHISUtil.getRydjNo(JGID, "BAHM", "", dao);
			if (BAHM.equals(XTBAHM)) {
				Map<String, Object> BAHMparameters = new HashMap<String, Object>();
				BAHMparameters.put("BAHM", BAHM);
				BAHMparameters.put("JGID", JGID);
				BAHMparameters.put("BRID", BRID);
				ll_Count = dao.doCount("ZY_BRRY",
						"BAHM = :BAHM and JGID = :JGID and BRID<>:BRID",
						BAHMparameters);
				if (ll_Count > 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "病案号码已经被使用，请重新保存!");
				}
				Map<String, Object> GY_XTCSparameters2 = new HashMap<String, Object>();
				String uBAHM = String.format("%0" + XTBAHM.length() + "d",
						Long.parseLong(XTBAHM) + 1);
				GY_XTCSparameters2.put("CSZ", uBAHM);
				GY_XTCSparameters2.put("CSMC", BSPHISSystemArgument.BAHM);
				GY_XTCSparameters2.put("JGID", JGID);
				dao.doUpdate("UPDATE " + "GY_XTCS"
						+ " set CSZ = :CSZ where CSMC= :CSMC and JGID = :JGID",
						GY_XTCSparameters2);
			} else {
				Map<String, Object> BAHMparameters = new HashMap<String, Object>();
				BAHMparameters.put("BAHM", BAHM);
				BAHMparameters.put("JGID", JGID);
				BAHMparameters.put("BRID", BRID);
				ll_Count = dao.doCount("ZY_BRRY",
						"BAHM = :BAHM and JGID = :JGID and BRID<>:BRID",
						BAHMparameters);
				if (ll_Count > 0) {
					body.put("BAHM", XTBAHM);
					BAHMparameters.put("BAHM", XTBAHM);
					ll_Count = dao.doCount("ZY_BRRY",
							"BAHM = :BAHM and JGID = :JGID and BRID<>:BRID",
							BAHMparameters);
					if (ll_Count > 0) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								"病案号码已经被使用，请重新保存!");
					}
					Map<String, Object> GY_XTCSparameters2 = new HashMap<String, Object>();
					String uBAHM = String.format("%0" + XTBAHM.length() + "d",
							Long.parseLong(XTBAHM) + 1);
					GY_XTCSparameters2.put("CSZ", uBAHM);
					GY_XTCSparameters2.put("CSMC", BSPHISSystemArgument.BAHM);
					GY_XTCSparameters2.put("JGID", JGID);
					dao.doUpdate(
							"UPDATE "
									+ BSPHISEntryNames.GY_XTCS
									+ " set CSZ = :CSZ where CSMC= :CSMC and JGID = :JGID",
							GY_XTCSparameters2);
				}
			}
			Map<String, Object> ZYCSparameters = new HashMap<String, Object>();
			ZYCSparameters.put("BAHM", BAHM);
			ZYCSparameters.put("JGID", JGID);
			ZYCSparameters.put("BRID", BRID);
			long ZYCS = dao
					.doCount(
							"ZY_BRRY",
							"CYPB!=99 and BAHM = :BAHM and JGID = :JGID and BRID = :BRID",
							ZYCSparameters);
			body.put("ZYCS", (int) (ZYCS + 1));
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.getTransaction().commit();
			ss.beginTransaction();
			body.remove("BRCH");
			Map<String, Object> ZY_BRRY = dao.doSave("create",
					BSPHISEntryNames.ZY_BRRY + "_RYDJ", body, false);
			Map<String, Object> parametersyybr = new HashMap<String, Object>();
			parametersyybr.put("JGBZ", 1);
			parametersyybr.put("JGID", JGID);
			parametersyybr.put("BRID", Long.parseLong(body.get("BRID") + ""));
			parametersyybr.put("JGBZW", 0);
			parametersyybr.put("BAHM", BAHM);
			dao.doUpdate(
					"update ZY_YYBR set JGBZ=:JGBZ,BAHM=:BAHM where JGID=:JGID and BRID=:BRID and JGBZ=:JGBZW",
					parametersyybr);
			// if(body.containsKey("SBKH")){
			// //这里代码,将ywlsh保存到入院登记里面.需要将床位号传到医保去,事务太乱 没看明白
			// JXMedicareModel jxmm = new JXMedicareModel(dao);
			// ZY_BRRY.put("GRBH", body.get("GRBH"));
			// ZY_BRRY.put("YBLB", Integer.parseInt(body.get("YBLB")+""));
			// Map<String,Object> ZYLSH = jxmm.savePatientRegistration(ZY_BRRY,
			// ctx);
			// ZYLSH.put("ZYH", ZY_BRRY.get("ZYH"));
			// dao.doSave("update", BSPHISEntryNames.ZY_BRRY + "_RYDJ", ZYLSH,
			// false);
			// }
			res.put("body", ZY_BRRY);
			Map<String, Object> MS_BRDA = new HashMap<String, Object>();
			MS_BRDA.put("BRXM", body.get("BRXM"));
			MS_BRDA.put("BRXB", body.get("BRXB"));
			MS_BRDA.put("CSNY", BSHISUtil.toDate(body.get("CSNY") + ""));
			MS_BRDA.put("SFZH", body.get("SFZH"));
			MS_BRDA.put("GJDM", body.get("GJDM"));
			MS_BRDA.put("MZDM", body.get("MZDM"));
			MS_BRDA.put("HYZK", body.get("HYZK"));
			MS_BRDA.put("ZYDM", body.get("ZYDM"));
			MS_BRDA.put("DWMC", body.get("GZDW"));
			MS_BRDA.put("JTDH", body.get("JTDH"));
			MS_BRDA.put("LXRM", body.get("LXRM"));
			MS_BRDA.put("LXDH", body.get("LXDH"));
			MS_BRDA.put("HKYB", body.get("HKYB"));
			MS_BRDA.put("LXDZ", body.get("LXDZ"));
			MS_BRDA.put("BRID", body.get("BRID"));
			MS_BRDA.put("XGSJ", new Date());
			/*********************/
			MS_BRDA.put("CSD_SQS", body.get("CSD_SQS"));
			MS_BRDA.put("CSD_S", body.get("CSD_S"));
			MS_BRDA.put("CSD_X", body.get("CSD_X"));
			MS_BRDA.put("JGDM_SQS", body.get("JGDM_SQS"));
			MS_BRDA.put("JGDM_S", body.get("JGDM_S"));
			MS_BRDA.put("XZZ_SQS", body.get("XZZ_SQS"));
			MS_BRDA.put("XZZ_S", body.get("XZZ_S"));
			MS_BRDA.put("XZZ_X", body.get("XZZ_X"));
			MS_BRDA.put("XZZ_QTDZ", body.get("XZZ_QTDZ"));
			MS_BRDA.put("XZZ_DH", body.get("XZZ_DH"));
			MS_BRDA.put("XZZ_YB", body.get("XZZ_YB"));
			MS_BRDA.put("HKDZ_SQS", body.get("HKDZ_SQS"));
			MS_BRDA.put("HKDZ_S", body.get("HKDZ_S"));
			MS_BRDA.put("HKDZ_X", body.get("HKDZ_X"));
			MS_BRDA.put("HKDZ_QTDZ", body.get("HKDZ_QTDZ"));
			MS_BRDA.put("DWDH", body.get("DWDH"));
			MS_BRDA.put("DWYB", body.get("DWYB"));
			MS_BRDA.put("LXGX", body.get("LXGX"));
			/******************************* */
			dao.doSave("update", BSPHISEntryNames.MS_BRDA, MS_BRDA, false);
			Map<String, Object> MPI_DemographicInfo = new HashMap<String, Object>();
			MPI_DemographicInfo.put("personName", body.get("BRXM"));
			MPI_DemographicInfo.put("sexCode", body.get("BRXB"));
			MPI_DemographicInfo.put("birthday",
					BSHISUtil.toDate(body.get("CSNY") + ""));
			MPI_DemographicInfo.put("idCard", body.get("SFZH"));
			MPI_DemographicInfo.put("nationalityCode", body.get("GJDM"));
			MPI_DemographicInfo.put("nationCode", body.get("MZDM"));
			MPI_DemographicInfo.put("maritalStatusCode", body.get("HYZK"));
			MPI_DemographicInfo.put("workCode", body.get("ZYDM"));
			MPI_DemographicInfo.put("workPlace", body.get("GZDW"));
			MPI_DemographicInfo.put("phoneNumber", body.get("JTDH"));
			MPI_DemographicInfo.put("contact", body.get("LXRM"));
			MPI_DemographicInfo.put("contactPhone", body.get("LXDH"));
			MPI_DemographicInfo.put("zipCode", body.get("HKYB"));
			MPI_DemographicInfo.put("address", body.get("LXDZ"));
			MPI_DemographicInfo.put(
					"empiId",
					dao.doLoad(BSPHISEntryNames.MS_BRDA, body.get("BRID")).get(
							"EMPIID"));
			dao.doSave("update", BSPHISEntryNames.MPI_DemographicInfo,
					MPI_DemographicInfo, false);
			ss.getTransaction().commit();
			// 床位分配
			if (BRCH != null && BRCH.length() > 0) {
				String RYRQ = (String) req.get("RYRQ");
				Map<String, Object> cwfp = new HashMap<String, Object>();
				cwfp.put("ZYH", ZY_BRRY.get("ZYH"));
				cwfp.put("BRCH", BRCH);
				cwfp.put("RYRQ", RYRQ);
				ss.beginTransaction();
				BSPHISUtil.cwgl_cwfp(cwfp, dao, ctx);
				ss.getTransaction().commit();
			}
			// 缴款
			ss.beginTransaction();
			String sjhm = BSPHISUtil.Getbillnumber("收据", dao, ctx);
			if (body.get("JKJE") != null
					&& (body.get("JKJE") + "").length() > 0
					&& Double.parseDouble(body.get("JKJE") + "") > 0) {
				Map<String, Object> ZY_TBKK = new HashMap<String, Object>();
				ZY_TBKK.put("JKJE", Double.parseDouble(body.get("JKJE") + ""));
				ZY_TBKK.put("JKFS", body.get("JKFS"));
				ZY_TBKK.put("ZPHM", body.get("ZPHM"));
				ZY_TBKK.put("ZYH", ZY_BRRY.get("ZYH"));
				ZY_TBKK.put("ZYHM", ZYHM);
				ZY_TBKK.put("BRXM", body.get("BRXM"));
				ZY_TBKK.put("BRCH", BRCH);
				ZY_TBKK.put("SJHM", body.get("SJHM"));
				ZY_TBKK.put("CZGH", userId);
				ZY_TBKK.put("JGID", JGID);
				ZY_TBKK.put("JKRQ", new Date());
				ZY_TBKK.put("ZFPB", 0);
				ZY_TBKK.put("ZCPB", 0);
				ZY_TBKK.put("JSCS", 0);
				if (!"false".equals(sjhm)) {
					if (!sjhm.equals(body.get("SJHM"))) {
						ZY_TBKK.put("SJHM", sjhm);
					}
				} else {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"您的收据已用完或尚未申领，不能进行缴款处理!");
				}
				BSPHISUtil.SetBillNumber("收据", sjhm, dao, ctx);
				Map<String, Object> JKXH = dao.doSave("create",
						BSPHISEntryNames.ZY_TBKK, ZY_TBKK, false);
				res.put("JKXH", JKXH.get("JKXH"));
			}
			//系统业务做完后进行农合登记
			if(body.containsKey("zynhdk") && body.get("zynhdk").toString().equals("1")){
				JSONObject request=new JSONObject();
				String qzjstr=DictionaryController.instance().getDic("phis.dictionary.Hcnqzj").getText(JGID.substring(0, 9)); 
				List<?> cnd = CNDHelper.createSimpleCnd("eq", "MZHM", "s", MZHM);
				Map<String, Object> xx=dao.doLoad(cnd, BSPHISEntryNames.MS_BRDA) ;
				String nhkh="";
				String grbh="";
				if(xx!=null && xx.size() >0){
					nhkh=xx.get("NHKH")+"";
					grbh=xx.get("GRBH")+"";
				}
				if(nhkh!=null && nhkh.length() >0 && grbh!=null && grbh.length()>0 ){
					if(qzjstr!=null && qzjstr.length() >0){
						String newargs[]=qzjstr.split(":");
						try {
							request.put("operator",user );
							request.put("ICD10","J06.903");
							request.put("jbmc","上呼吸道感染");
							request.put("cardid",nhkh);
							request.put("grbh",grbh);
							request.put("yybh", newargs[0]);
							Calendar d = Calendar.getInstance();
							d.setTime(new Date());
							request.put("year", d.get(Calendar.YEAR)+"");
							request.put("ryrq",(new java.text.SimpleDateFormat("yyyy-MM-dd")).format(d.getTime()));
							request.put("zyh", ZY_BRRY.get("ZYH"));
							request.put("ryzt", body.get("RYQK")+"");
							request.put("drgs", "0");
							
							request.put("ip",newargs[1] );
							request.put("port",newargs[2]);
							request.put("serviceId", "Zydj");
							request.put("msgType", "2");
							URL url=XnhModel.getwebserviceurl(JGID);
							HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
							HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
							try {
								String rezydj=hcn.doZydj(request.toString());
								JSONObject zydjxx=new JSONObject(rezydj);
								if(zydjxx.optString("code").equals("1")){
									Long djid=zydjxx.optLong("djid");
									Map<String, Object> p=new HashMap<String, Object>();
									p.put("NHDJID", djid);
									p.put("ZYH", Long.parseLong(ZY_BRRY.get("ZYH")+""));
									dao.doUpdate("update ZY_BRRY set NHDJID=:NHDJID where ZYH=:ZYH ", p);
								}else{
									res.put("code", "502");
									res.put("msg", zydjxx.optString("msg"));
									throw new ModelDataOperationException(zydjxx.optString("msg"));
								}
							} catch (Exception_Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else {
						throw new ModelDataOperationException("未找到该机构的农合信息，请确认该机构有报销权限！！！");
					}
				}else{
					throw new ModelDataOperationException("获取基本信息出错，缺失基本农合信息！！！");
				}
				
				
			}
			
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入院登记保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入院登记保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetBRCH(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long ksdm = Long.parseLong(body.get("KSDM") + "");
		int brxb = Integer.parseInt(body.get("BRXB") + "");
		try {
			parameters.put("KSDM", ksdm);
			parameters.put("BRXB", brxb);
			parameters.put("JGID", JGID);
			String hql = "select a.BRCH as value,a.BRCH as text from "
					+ "ZY_CWSZ"
					+ " a where a.CWKS = :KSDM and a.JGID = :JGID and (ZYH is null or ZYH =0) and (CWXB = :BRXB or CWXB = 3) order by BRCH";
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			logger.error("remove failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取床号失败.");
		}

	}
	
	public long parseLong(Object o) {
		if (o == null) {
			return 0L;
		}
		return Long.parseLong(o + "");
	}
	
	
	public void doPrintMoth(Map<String, Object> request,
			Map<String, Object> response, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String jgname = user.getManageUnitName();
		response.put("title", jgname);
		// String[] upint = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"
		// };
		Long jkxh = 0L;
		if (request.get("jkxh") != null) {
			jkxh = Long.parseLong(request.get("jkxh") + "");
		}
		parameters.put("JKXH", jkxh);
		try {
			List<Map<String, Object>> ZY_BRRY = dao
					.doSqlQuery(
							"select zt.SJHM as SJHM,zt.ZYH as ZYH,zb.ZYHM as ZYHM,zb.BRCH as BRCH,zb.BRXM as BRXM,zt.JKJE as JKJE,zt.JKFS as JKFS,zt.CZGH as CZGH,zt.ZPHM as ZPHM,zt.JKRQ as JKRQ,ks.OFFICENAME as KSMC,bq.OFFICENAME as BQMC from ZY_TBKK zt,ZY_BRRY zb left outer join SYS_Office ks on zb.BRKS=ks.ID left outer join SYS_Office bq on zb.BRBQ=bq.ID where zt.ZYH=zb.ZYH and zt.JKXH=:JKXH",
							parameters);
			if (ZY_BRRY.size() > 0) {
				if (ZY_BRRY.get(0).get("SJHM") != null) {
					response.put("PJHM", ZY_BRRY.get(0).get("SJHM") + "");
				}
				response.put("ZYHM", ZY_BRRY.get(0).get("ZYHM") + "");
				if (ZY_BRRY.get(0).get("BRCH") != null) {
					response.put("BRCH", ZY_BRRY.get(0).get("BRCH") + "");
				}
				if (ZY_BRRY.get(0).get("BRXM") != null) {
					response.put("BRXM", ZY_BRRY.get(0).get("BRXM") + "");
				}
				if (ZY_BRRY.get(0).get("KSMC") != null) {
					response.put("KSMC", ZY_BRRY.get(0).get("KSMC") + "");
				}
				if (ZY_BRRY.get(0).get("BQMC") != null) {
					response.put("BQMC", ZY_BRRY.get(0).get("BQMC") + "");
				}
				if (ZY_BRRY.get(0).get("JKJE") != null) {
					response.put("JKJE",
							String.format("%1$.2f", ZY_BRRY.get(0).get("JKJE")));
				}
				double jkje = Double.parseDouble(ZY_BRRY.get(0).get("JKJE")
						+ "");
				response.put("DXJE", BSPHISUtil.changeMoneyUpper(jkje));
				response.put("JKFS", DictionaryController.instance().getDic("phis.dictionary.payment")
						.getText(ZY_BRRY.get(0).get("JKFS") + ""));
				response.put("JSR", DictionaryController.instance().getDic("phis.dictionary.user")
						.getText(ZY_BRRY.get(0).get("CZGH") + ""));
				if (ZY_BRRY.get(0).get("ZPHM") != null) {
					response.put("PKHM", ZY_BRRY.get(0).get("ZPHM") + "");
				} else {
					response.put("PKHM", "");
				}
				response.put("JKRQ", sdf.format(ZY_BRRY.get(0).get("JKRQ")));
				Map<String, Object> BRXX = new HashMap<String, Object>();
				BRXX.put("JSLX", 0);
				BRXX.put("ZYH", ZY_BRRY.get(0).get("ZYH"));
				BRXX = BSPHISUtil.gf_zy_gxmk_getjkhj(BRXX, dao, ctx);
				double JKHJ = Double.parseDouble(BRXX.get("JKHJ") + "");
				response.put("JKHJ", String.format("%1$.2f", JKHJ));
				BRXX = BSPHISUtil.gf_zy_gxmk_getzfhj(BRXX, dao, ctx);
				double ZFHJ = Double.parseDouble(BRXX.get("ZFHJ") + "");
				response.put("ZFHJ", String.format("%1$.2f", ZFHJ));
				double SYHJ = BSPHISUtil.getDouble(JKHJ - ZFHJ, 2);
				response.put("SYHJ", String.format("%1$.2f", SYHJ));
				response.put(BSPHISSystemArgument.FPYL, ParameterUtil.getParameter(
						manaUnitId, BSPHISSystemArgument.FPYL, ctx));
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
}
