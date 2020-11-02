package phis.application.yb.example.HZ.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class MedicareModel_HZ_DR {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(MedicareModel_HZ_DR.class);
	protected SchemaItem item = null;
	protected Map<String, Object> yyxx = null;// 医院编号
	protected String ywzqh = null;// 业务周期号
	protected String centerCode = null;// 医院编码的第一位
	protected Map<String, Object> map_doctor = null;// 医生对应的医保用户ID和业务周期号(智慧结算用)
	public MedicareModel_HZ_DR(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-16
	 * @description 查询数据库有没该医保卡信息 (杭州没有该流程)
	 * @updateInfo
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYBKXX(Map<String, Object> body)
			throws ModelDataOperationException {
		return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-16
	 * @description 保存医保卡信息 (杭州没有该流程)
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveYBKXX(Map<String, Object> body)
			throws ModelDataOperationException {
		return null;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-17
	 * @description 获取医保病人性质(暂时只支持单医保,如果需要多医保自行添加)
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbbrxz(Context ctx)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("YBBRXZ", ParameterUtil.getParameter(ParameterUtil.getTopUnitId(), "YBBRXZ", ctx));
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-22
	 * @description 获取挂号登记,上传,预结算,结算参数
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbGhjscs(Map<String, Object> body,Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		Map<String, Object> map_ghdj = ybghdj(body, ctx);
		map_ret.put("DJ", map_ghdj);
		body.put("MZH", map_ghdj.get("MZH"));
		map_ret.put("MZH", map_ghdj.get("MZH"));
		map_ret.put("JYLSH", map_ghdj.get("JYLSH"));
		map_ret.put("SC", ybghsc(body,map_ret, ctx));
		map_ret.put("YJS", ybghyjs(body, ctx));
		map_ret.put("JS", ybghjs(body, ctx));
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-9-4
	 * @description 挂号登记
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ybghdj(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowDate = sdf.format(new Date());// 日期
		Map<String, Object> map_body = new HashMap<String, Object>();
		map_body.put("TYPE", MedicineUtils.parseInt(body.get("TYPE")));
		Map<String, Object> map_ret = getPar(map_body, ctx);
		map_ret.put("TransType", "3200");
		map_ret.put("ProcessingCode", "331200");
		String mzh = "G" + getMzh();// 门诊号
		map_ret.put("MZH", mzh);
		map_ret.put("JYLSH", map_ret.get("SenderSerialNo"));
		String ryrq = nowDate;
		Map<String, Object> spData = new HashMap<String, Object>();
		if (body.get("spData") != null) {
			spData = (Map<String, Object>) body.get("spData");
		}
		Map<String, Object> ghxx = (Map<String, Object>) body.get("ghxx");// 挂号的信息
		Map<String, Object> ybxx = (Map<String, Object>) body.get("ybxx");// 医保卡的信息
		StringBuffer par = new StringBuffer();
		// 卡号/证号|日期|医疗类别|住院流水号|操作员|审批编号|器官移植标志|入院诊断疾病编码|需要被撤销的医院交易流水号|住院凭证号|入院日期|转出医院编码|医院报销费用发生医疗机构|费用发生医疗机构名称|转诊单号|出院日期|就诊科室
		par.append(MedicineUtils.parseString(ybxx.get("SRKH")))// 卡号/证号###
				.append("|").append(nowDate)// 日期###
				.append("|").append(MedicineUtils.parseString(body.get("YLLB")))// 医疗类别###
				.append("|").append(mzh)// 门诊号###
				.append("|").append(getUserId(ctx))// 操作员###
				.append("|").append(MedicineUtils.parseString(spData.get("SPBH")))// 审批编号
				.append("|").append("")// 器官移植标志
				.append("|").append(MedicineUtils.parseString(spData.get("SPXX")))// 入院诊断疾病编码
				.append("|").append("")// 需要被撤销的医院交易流水号
				.append("|").append("")// 住院凭证号
				.append("|").append(ryrq)// 入院日期###
				.append("|").append("")// 转出医院编码
				.append("|").append("")// 医院报销费用发生医疗机构
				.append("|").append("")// 费用发生医疗机构名称
				.append("|").append("")// 转诊单号
				.append("|").append("")// 出院日期,退休门诊统筹医院报销时必须录入***
				.append("|").append(MedicineUtils.parseString(ghxx.get("KSMC")))// 就诊科室####
																	// 不知道是代码还是名称
																	// 需要问***
				.append("|");
		map_ret.put("InputStr", par.toString());
		map_ret.put("ItemNumberIndicator", 0);
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-9-5
	 * @description 挂号费用上传,就是传4个费用,挂号,专家,诊疗,病历
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> ybghsc(Map<String, Object> body,Map<String, Object> map_body_ret,
			Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user=UserRoleToken.getCurrent();
		String jgName = user.getManageUnitId();// 用户的机构名称
		// String jgid = user.get("manageUnit.id");// 用户的机构ID
		int type = MedicineUtils.parseInt(body.get("TYPE"));
		List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowDate = sdf.format(new Date());
		try {
			String spbh = "";
			String splx = "";
			Map<String, Object> spData = new HashMap<String, Object>();
			if (body.get("spData") != null) {
				spData = (Map<String, Object>) body.get("spData");
				spbh =  MedicineUtils.parseString(spData.get("SPBH"));
				StringBuffer hql_sp = new StringBuffer();
				hql_sp.append("select SPLX as SPLX from YB_SPBA where SPBH=:spbh");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("spbh",  MedicineUtils.parseLong(spbh));
				Map<String, Object> map_splx = dao.doLoad(hql_sp.toString(),
						map_par);
				if (map_splx != null) {
					splx =  MedicineUtils.parseString(map_splx.get("SPLX"));
				}
			}
			Map<String, Object> ghxx = (Map<String, Object>) body.get("ghxx");// 挂号的信息
			Map<String, Object> ybxx = (Map<String, Object>) body.get("ybxx");// 医保卡的信息
			StringBuffer hql_dzxx = new StringBuffer();// 查询对照信息
			hql_dzxx.append("select SFLB as SFLB,YBBM as SFXMBM,XMMC as SFXMMC,FYDJ as YPDJ from YB_FYDZ where YBPB=1 and FYXH=:fyxh and KSSJ<sysdate and (ZZSJ>sysdate or ZZSJ is null)");
			StringBuffer hql_xmxx = new StringBuffer();// 查询项目信息,找不到对照信息时用于显示错误.
			hql_xmxx.append("select FYMC as SFMC from GY_YLSF where FYXH=:fyxh");
			Map<String, Object> map_body = new HashMap<String, Object>();
			map_body.put("TYPE", type);
			double xyf=0,zlxyf=0,zcy=0,zlzcy=0,cyf=0,zlcyf=0,jcf=0,zljcf=0,tjf=0,zltjf=0,syf=0,zlsyf=0,ssf=0,zlssf=0,hyf=0,zlhyf=0,sxf=0,zlsxf=0,zcf=0,zlzcf=0,zlf=0,zlzlf=0,tzf=0,zltzf=0,hlf=0,cwf=0,qtf=0,sqbf=0,zjje=0,sbje=0;
			for (int i = 0; i < 4; i++) {
				Map<String, Object> map_ret = getPar(map_body, ctx);
				// 　ItemNumberIndicator:
				// 结束标志。(上传条数大于1：第一条标识为1,中间记录统一标识为2,最后一条标识为9 ；上传条数等于1：标识为0)
				if (i == 0) {
					map_ret.put("ItemNumberIndicator", 1);
				} else if (i == 3) {
					map_ret.put("ItemNumberIndicator", 9);
				} else {
					map_ret.put("ItemNumberIndicator", 2);
				}
				map_ret.put("TransType", "3800");
				map_ret.put("ProcessingCode", "031100");
				long xmxh = 0;
				String ypjg = "0";// 药品单价
				if (i == 0) {
					xmxh = parseLong("".equals(ParameterUtil.getParameter(
							ParameterUtil.getTopUnitId(),
							"GHFXM",
							BSPHISSystemArgument.defaultPubValue.get("GHFXM"),
							BSPHISSystemArgument.defaultPubAlias.get("GHFXM"),
							ctx)) ? BSPHISSystemArgument.defaultPubValue
							.get("GHFXM") : ParameterUtil.getParameter(
							ParameterUtil.getTopUnitId(),
							"GHFXM",
							BSPHISSystemArgument.defaultPubValue.get("GHFXM"),
							BSPHISSystemArgument.defaultPubAlias.get("GHFXM"),
							ctx));
					ypjg =  MedicineUtils.parseString(ghxx.get("GHJE"));
				} else if (i == 1) {
					xmxh = parseLong("".equals(ParameterUtil.getParameter(
							ParameterUtil.getTopUnitId(),
							"ZJFXM",
							BSPHISSystemArgument.defaultPubValue.get("ZJFXM"),
							BSPHISSystemArgument.defaultPubAlias.get("ZJFXM"),
							ctx)) ? BSPHISSystemArgument.defaultPubValue
							.get("ZJFXM") : ParameterUtil.getParameter(
							ParameterUtil.getTopUnitId(),
							"ZJFXM",
							BSPHISSystemArgument.defaultPubValue.get("ZJFXM"),
							BSPHISSystemArgument.defaultPubAlias.get("ZJFXM"),
							ctx));
					ypjg =  MedicineUtils.parseString(ghxx.get("ZJFY"));
				} else if (i == 2) {
					xmxh = parseLong("".equals(ParameterUtil.getParameter(
							ParameterUtil.getTopUnitId(),
							"ZLFXM",
							BSPHISSystemArgument.defaultPubValue.get("ZLFXM"),
							BSPHISSystemArgument.defaultPubAlias.get("ZLFXM"),
							ctx)) ? BSPHISSystemArgument.defaultPubValue
							.get("ZLFXM") : ParameterUtil.getParameter(
							ParameterUtil.getTopUnitId(),
							"ZLFXM",
							BSPHISSystemArgument.defaultPubValue.get("ZLFXM"),
							BSPHISSystemArgument.defaultPubAlias.get("ZLFXM"),
							ctx));
					ypjg =  MedicineUtils.parseString(ghxx.get("ZLJE"));
				} else {
					xmxh =  MedicineUtils.parseLong(""
							.equals(ParameterUtil.getParameter(ParameterUtil
									.getTopUnitId(),
									"BLFXM",
									BSPHISSystemArgument.defaultValue
											.get("BLFXM"),
									BSPHISSystemArgument.defaultAlias
											.get("BLFXM"), ctx)) ? BSPHISSystemArgument.defaultValue
							.get("BLFXM") : ParameterUtil
							.getParameter(ParameterUtil.getTopUnitId(),
									"BLFXM",
									BSPHISSystemArgument.defaultValue
											.get("BLFXM"),
									BSPHISSystemArgument.defaultAlias
											.get("BLFXM"), ctx));
					ypjg =  MedicineUtils.parseString(ghxx.get("BLJE"));
				}
				String sflb = "";
				String sfxmbm = "";
				String sfxmmc = "";
				String ypdj = "";
				// 该段代码 根据药品对照获取收费类别,收费项目编码,收费项目名称,药品等级
				Map<String, Object> map_par_dzxx = new HashMap<String, Object>();
				map_par_dzxx.put("fyxh", xmxh);
				List<Map<String, Object>> list_dzxx = dao.doQuery(
						hql_dzxx.toString(), map_par_dzxx);
				if (list_dzxx == null || list_dzxx.size() == 0) {
					Map<String, Object> map_xmxx = dao.doLoad(
							hql_xmxx.toString(), map_par_dzxx);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							map_xmxx.get("SFMC") + " 费用未对照,请先对照");
				}
				Map<String, Object> map_dzxx = list_dzxx.get(0);
				sflb =  MedicineUtils.parseString(map_dzxx.get("SFLB"));
				sfxmbm =  MedicineUtils.parseString(map_dzxx.get("SFXMBM"));
				sfxmmc =  MedicineUtils.parseString(map_dzxx.get("SFXMMC"));
				ypdj =  MedicineUtils.parseString(map_dzxx.get("YPDJ"));
				String sfyp = "0";// 是否是药品
				String ypsl = "1";// 药品数量
				// 获取自负比例
				// ...k54
				// double zlje = 0;// 自理金额 不知道怎么计算 需要问***
				// String grzfje = ypjg;// 个人自费金额 不知道怎么计算 需要问***
				Map<String, Object> map_jsbl = new HashMap<String, Object>();
				map_jsbl.put("FYXH", xmxh);
				map_jsbl.put("ZFPB", 0);
				map_jsbl.put("BLLX", ybxx.get("BLLX"));
				map_jsbl.put("FYSL", 1);
				map_jsbl.put("YPDJ", ypjg);
				Map<String, Double> map_zfzl = GSMedicareUtil.doQueryDzxx(dao,
						map_jsbl, ctx, 2);// 计算自负自理
				if (map_zfzl == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"自理自费计算失败,请检查参数和对照");
				}
				double zlje = map_zfzl.get("ld_grzl");// 自理金额 不知道怎么计算 需要问***
				double grzfje = map_zfzl.get("ld_grzf");// 个人自费金额 不知道怎么计算 需要问***
				zjje=parseDouble(ypjg);
				sbje=parseDouble(grzfje)+parseDouble(zlje);
				if("11".equals(sflb)){//西药费
					xyf=xyf+zjje;
					zlxyf=zlxyf+sbje;
				}else if("12".equals(sflb)){ //中成药  
					zcy =zcy +zjje;
					zlzcy=zlzcy+sbje;
				}else if("13".equals(sflb)){//中草药
					cyf =cyf +zjje;
					zlcyf=zlcyf+sbje;
				}else if("21".equals(sflb)){//检查费
					jcf =jcf +zjje;
					zljcf=zljcf+sbje;
				}else if("22".equals(sflb)){//特检费
					tjf =tjf +zjje;
					zltjf=zltjf+sbje;
				}else if("23".equals(sflb)){//输氧费
					syf  =syf  +zjje;
					zlsyf =zlsyf+sbje;
				}else if("24".equals(sflb)){//手术费
					ssf  =ssf  +zjje;
					zlssf =zlssf +sbje;
				}else if("25".equals(sflb)){//化验费
					hyf =hyf +zjje;
					zlhyf=zlhyf+sbje;
				}else if("26".equals(sflb)){//输血费
					sxf =sxf +zjje;
					zlsxf=zlsxf+sbje;
				}else if("27".equals(sflb)){//诊察费
					zcf =zcf +zjje;
					zlzcf=zlzcf+sbje;
				}else if("31".equals(sflb)){//治疗费
					zlf  =zlf  +zjje;
					zlzlf =zlzlf +sbje;
				}else if("32".equals(sflb)){//特治费
					tzf  =tzf  +zjje;
					zltzf =zltzf +sbje;
				}else if(parseDouble(map_zfzl.get("ld_zjje"))==parseDouble(map_zfzl.get("ld_zfje"))){//zfbl=1
					sbje=zjje;
					if("33".equals(sflb)){
						hlf=hlf+zjje;
					}else if("34".equals(sflb)){
						cwf=cwf+zjje;
					}else if("91".equals(sflb)){
						qtf=qtf+zjje;
					}else{
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								sfxmbm+"("+sfxmmc+")自负比例100%,归并为:"+sflb+"医保不使用");	
					}
				}
				else{
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							sfxmbm+"("+sfxmmc+")"+"自负比例不为100%,归并为"+sflb+"错误或门诊不使用");
				}
				sqbf = sqbf + (zjje - sbje);
				// 剂型,每次用量,使用频次,用法 不知道是从对照表取还是本条数据计算,需要问
				String jx = "1";// 剂型
				double mcyl = 1;// 每次用量
				String sypc = "1";// 使用频次
				String yf = "1";// 用法
				String yybz = "";// 职工医保二级保健干部用药标志,1、职工医保二级保健用药标志 0
									// 非职工医保二级保健用药标志 是否需要界面输入?
				Map<String, Object> map_yybm = getYybh(ctx);
				StringBuffer inputStr = new StringBuffer();
				inputStr.append("02").append("|")// 项目编码###
						.append(parseString(map_yybm.get("YYBM"))).append("|")// 服务机构编号###
						.append(body.get("MZH")).append("|")// 住院号（门诊号）###****
						.append(getGhCfh()).append("|")// 处方号
						.append(xmxh).append("|")// 医院收费项目内码###
						.append(1).append("|")// 交易类型,1 正交易 ###
						.append("000000").append("|")// 单据号,在录入费用时必须传入’000000’###
						.append(spbh).append("|")// 审批编号,如果有审批编码必须填写相应的审批类型***
						.append(nowDate).append("|")// 处方日期,接口文档里面是DATE类型
													// 不知道要怎么传###
						.append(sflb).append("|")// 收费类别###
						.append(sfxmbm).append("|")// 收费项目编码###
						.append(sfxmmc).append("|")// 收费项目名称###
						.append(sfyp).append("|")// 药品/非药品,0 非药品 1 药品###
						.append(ypdj).append("|")// 药品等级###
						.append(ypjg).append("|")// 单价###
						.append(ypsl).append("|")// 数量###
						.append(ypjg).append("|")// 金额### 挂号里面金额就是单价
						.append(zlje).append("|")// 自理金额###
						.append(grzfje).append("|")// 个人自费金额###
						.append(jx).append("|")// 剂型###
						.append(mcyl).append("|")// 每次用量###
						.append(sypc).append("|")// 使用频次###
						.append(yf).append("|")// 用法###
						.append(parseString(ghxx.get("KSMC"))).append("|")// 科别名称###
						.append("").append("|")// 执行天数
						.append("").append("|")// 1、已审核 0、未审核 不需填写
						.append("").append("|")// 剔除标志 不需填写
						.append("").append("|")// 作废标志
						.append("").append("|")// 全额自费标志
						.append(0).append("|")// 结算标志 录入费用时传入0
						.append(0).append("|")// 逻辑校验位 不需填写
						.append("").append("|")// 交易流水号 不需填写
						.append(i + 1).append("|")// 处方流水号 对应于每次录入费用的流水###
													// ***不知道是什么
													// 需要问
						.append("").append("|")// 医院流水号 不需填写
						.append("").append("|")// 撤销交易流水号 不需填写
						.append("").append("|")// 业务周期号 不需填写
						.append("").append("|")// 传输标志 不需填写
						.append(parseString(ybxx.get("GRBH"))).append("|")// 就诊病人个人编号###
						.append("").append("|")// 审核口款 不需填写
						.append("").append("|")// 审核扣款原因 不需填写
						.append("").append("|")// 结算日期 不需填写
						.append(nowDate).append("|")// 经办日期### 不知道需要传什么格式的 需要问
						.append("").append("|")// 冲正医院交易流水号 不需填写
						.append(splx).append("|")// 审批类型
						.append(yybz).append("|")// 职工医保二级保健干部用药标志
						.append("").append("|")// 临时字段 不需填写
						.append("").append("|")// 临时字段 不需填写
						.append(map_ret.get("BusiCycle")).append("|")// 医院上传明细业务周期号###
						.append(map_ret.get("SenderSerialNo")).append("|")// 医院上传明细医院交易流水号
																			// 不知道是不是取这个
																			// 需要问###***
						.append(getUserName(ctx)).append("|")// 开方医生姓名### 传操作员姓名
						.append("00").append("|")// 连锁机构编码 如果没有连锁机构传入00###
						.append(jgName).append("|")// 连锁机构名称
													// 如果没有连锁机构填写自己定点医疗机构名称###
				;
				map_ret.put("InputStr", inputStr.toString());
				list_ret.add(map_ret);
			}
			Map<String,Object> map_ghjsmx=new HashMap<String,Object>();//医保结算明细
			map_ghjsmx.put("XYF", xyf);
			map_ghjsmx.put("ZLXYF", zlxyf);
			map_ghjsmx.put("ZCY", zcy);
			map_ghjsmx.put("ZLZCY", zlzcy);
			map_ghjsmx.put("CYF", cyf);
			map_ghjsmx.put("ZLCYF", zlcyf);
			map_ghjsmx.put("JCF", jcf);
			map_ghjsmx.put("ZLJCF", zljcf);
			map_ghjsmx.put("TJF", tjf);
			map_ghjsmx.put("ZLTJF", zltjf);
			map_ghjsmx.put("SYF", syf);
			map_ghjsmx.put("ZLSYF", zlsyf);
			map_ghjsmx.put("SSF", ssf);
			map_ghjsmx.put("ZLSSF", zlssf);
			map_ghjsmx.put("HYF", hyf);
			map_ghjsmx.put("ZLHYF", zlhyf);
			map_ghjsmx.put("SXF", sxf);
			map_ghjsmx.put("ZLSXF", zlsxf);
			map_ghjsmx.put("ZCF", zcf);
			map_ghjsmx.put("ZLZCF", zlzcf);
			map_ghjsmx.put("ZLF", zlf);
			map_ghjsmx.put("ZLZLF", zlzlf);
			map_ghjsmx.put("TZF", tzf);
			map_ghjsmx.put("ZLTZF", zltzf);
			map_ghjsmx.put("HLF", hlf);
			map_ghjsmx.put("CWF", cwf);
			map_ghjsmx.put("QTF", qtf);
			map_ghjsmx.put("SQBF", sqbf);
			map_body_ret.put("YBGHMX", map_ghjsmx);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, e.getMessage());
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-22
	 * @description 挂号预结算参数查询,返回值类型根据具体需要修改
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ybghyjs(Map<String, Object> body,Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowDate = sdf.format(new Date());// 日期
		Map<String, Object> map_body = new HashMap<String, Object>();
		map_body.put("TYPE", 1);
		Map<String, Object> map_ret = getPar(map_body, ctx);
		map_ret.put("TransType", "3100");
		map_ret.put("ProcessingCode", "331120");
		String czrq = nowDate;// 操作日期,不知道接口说明里面是什么意思,需要问
		Map<String, Object> spData = new HashMap<String, Object>();
		if (body.get("spData") != null) {
			spData = (Map<String, Object>) body.get("spData");
		}
		Map<String, Object> ybxx = (Map<String, Object>) body.get("ybxx");// 医保卡的信息
		StringBuffer inputStr = new StringBuffer();
		inputStr.append(parseString(ybxx.get("SRKH"))).append("|")// 卡号/证号###
				.append(czrq).append("|")// 操作日期 医院报销传入时间表示：费用发生日期，不是系统日期（针对 16
											// 门诊报销结算类别应该传入系统时间）###***
				.append(parseString(body.get("JSLB"))).append("|")// 结算类别###
				.append("").append("|")// 单据号
										// 预结算单据号为’000000’###
				.append(parseString(body.get("MZH"))).append("|")// 门诊流水号###
				.append(getUserName(ctx)).append("|")// 操作员###
				.append("").append("|")// 转往医院等级（费用发生医院编码）当结算类别=
										// 6,7,8，9,12，13，16时表示费用发生医院编码
				.append(9).append("|")// 出院原因 1、康复
										// 2、转院 3、死亡
										// 9其他
				.append(parseString(spData.get("SPBH"))).append("|")// 审批编号
				.append("").append("|")// 器官移植标志
				.append(parseString(spData.get("SPXX"))).append("|")// 入院诊断
				.append("").append("|")// 需要被撤销的中心交易流水号 预结算和交易结算的时候传入值为空
				.append("").append("|")// 需要被撤销的医院交易流水号 预结算和交易结算的时候传入值为空
				.append(nowDate).append("|")// 住院日期
				.append(nowDate).append("|")// 出院日期
				.append(parseString(spData.get("SPXX"))).append("|")// 出院疾病诊断
				.append("").append("|")// 出院疾病次要诊断
				.append("").append("|")// 住院床号
				.append("").append("|")// 出院疾病诊断3
				.append("").append("|")// 出院疾病诊断4
				.append("").append("|")// 出院疾病诊断5
				.append("").append("|")// 出院疾病诊断6
				.append("").append("|")// 出院疾病诊断7
				.append("").append("|")// 出院疾病诊断8
				.append("").append("|")// 出院疾病诊断9
				.append("").append("|")// 出院疾病诊断10
				.append("").append("|")// 出院疾病诊断11
				.append("").append("|")// 出院疾病诊断12
				.append("").append("|")// 出院疾病诊断13
				.append("").append("|")// 出院疾病诊断14
				.append("").append("|")// 出院疾病诊断15
		;
		map_ret.put("InputStr", inputStr.toString());
		map_ret.put("ItemNumberIndicator", 0);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-22
	 * @description 挂号结算参数查询,返回值类型根据具体需要修改
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ybghjs(Map<String, Object> body,Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowDate = sdf.format(new Date());// 日期
		Map<String, Object> map_body = new HashMap<String, Object>();
		map_body.put("TYPE", 1);
		Map<String, Object> map_ret = getPar(map_body, ctx);
		map_ret.put("TransType", "3100");
		map_ret.put("ProcessingCode", "331130");
		String czrq = nowDate;// 操作日期,不知道接口说明里面是什么意思,需要问
		Map<String, Object> spData = new HashMap<String, Object>();

		if (body.get("spData") != null) {
			spData = (Map<String, Object>) body.get("spData");
		}
		Map<String, Object> ybxx = (Map<String, Object>) body.get("ybxx");// 医保卡的信息
		Map<String, Object> ghxx = (Map<String, Object>) body.get("ghxx");// 挂号的信息
		StringBuffer inputStr = new StringBuffer();
		inputStr.append(parseString(ybxx.get("SRKH"))).append("|")// 卡号/证号###
				.append(czrq).append("|")// 操作日期 医院报销传入时间表示：费用发生日期，不是系统日期（针对 16
											// 门诊报销结算类别应该传入系统时间）###***
				.append(parseString(body.get("JSLB"))).append("|")// 结算类别###
				.append(parseString(ghxx.get("JZHM"))).append("|")// 单据号
																	// 预结算单据号为’000000’###
				.append(parseString(body.get("MZH"))).append("|")// 门诊流水号###
				.append(getUserName(ctx)).append("|")// 操作员###
				.append("").append("|")// 转往医院等级（费用发生医院编码）当结算类别=
										// 6,7,8，9,12，13，16时表示费用发生医院编码
				.append(9).append("|")// 出院原因 1、康复
										// 2、转院 3、死亡
										// 9其他
				.append(parseString(spData.get("SPBH"))).append("|")// 审批编号
				.append("").append("|")// 器官移植标志
				.append(parseString(spData.get("SPXX"))).append("|")// 入院诊断
				.append("").append("|")// 需要被撤销的中心交易流水号 预结算和交易结算的时候传入值为空
				.append("").append("|")// 需要被撤销的医院交易流水号 预结算和交易结算的时候传入值为空
				.append(nowDate).append("|")// 住院日期
				.append(nowDate).append("|")// 出院日期
				.append(parseString(spData.get("SPXX"))).append("|")// 出院疾病诊断
				.append("").append("|")// 出院疾病次要诊断
				.append("").append("|")// 住院床号
				.append("").append("|")// 出院疾病诊断3
				.append("").append("|")// 出院疾病诊断4
				.append("").append("|")// 出院疾病诊断5
				.append("").append("|")// 出院疾病诊断6
				.append("").append("|")// 出院疾病诊断7
				.append("").append("|")// 出院疾病诊断8
				.append("").append("|")// 出院疾病诊断9
				.append("").append("|")// 出院疾病诊断10
				.append("").append("|")// 出院疾病诊断11
				.append("").append("|")// 出院疾病诊断12
				.append("").append("|")// 出院疾病诊断13
				.append("").append("|")// 出院疾病诊断14
				.append("").append("|")// 出院疾病诊断15
		;
		map_ret.put("InputStr", inputStr.toString());
		map_ret.put("ItemNumberIndicator", 0);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-22
	 * @description 保存医保挂号信息
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveYbghxx(Map<String, Object> body)throws ModelDataOperationException {
		try {
			dao.doSave("create", "phis.application.yb.schemas.YB_GHJS", body, false);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "保存医保挂号结算信息失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "保存医保挂号结算信息失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-22
	 * @description  医保挂号退号参数查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryYbThcs(Map<String, Object> body,Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Map<String, Object> map_body = new HashMap<String, Object>();
		map_body.put("TYPE", 1);
		Map<String, Object> map_ret = getPar(map_body, ctx);
		map_ret.put("TransType", "3100");
		map_ret.put("ProcessingCode", "331140");
		Map<String, Object> ybxx = (Map<String, Object>) body.get("YBXX");
		String sbxh = parseString(body.get("SBXH"));
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select b.JSRQ as JSRQ,b.YLLB as YLLB,b.JYXH as JYXH,b.ZXLSH as ZXLSH from ")
				.append(BSPHISEntryNames.MS_GHMX).append(" a,")
				.append("YB_GHJS")
				.append(" b where a.SBXH=b.SBXH and a.SBXH=:sbxh");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sbxh", parseLong(sbxh));
		try {
			Map<String, Object> jsxx = dao.doLoad(hql.toString(), map_par);
			String jslb = "1";// 结算类别 现在暂时只知道规定病种和普通门诊的取法
			if ("11".equals(parseString(jsxx.get("YLLB")))) {
				jslb = "3";
			}
			StringBuffer inputStr = new StringBuffer();
			inputStr.append(parseString(ybxx.get("SRKH"))).append("|")// 卡号/证号###
					.append(sdf.format(jsxx.get("JSRQ"))).append("|")// 操作日期
																		// 医院报销传入时间表示：费用发生日期，不是系统日期（针对
																		// 16
					// 门诊报销结算类别应该传入系统时间）###***
					.append(jslb).append("|")// 结算类别###
					.append(sbxh).append("|")// 单据号###
					.append(parseString(jsxx.get("JYXH"))).append("|")// 门诊流水号###
					.append(getUserName(ctx)).append("|")// 操作员
					.append("").append("|")// 转往医院等级（费用发生医院编码）当结算类别=
											// 6,7,8，9,12，13，16时表示费用发生医院编码
					.append("").append("|")// 出院原因 1、康复
											// 2、转院 3、死亡
											// 9其他
					.append("").append("|")// 审批编号
					.append("").append("|")// 器官移植标志
					.append("").append("|")// 入院诊断
					.append(parseString(jsxx.get("ZXLSH"))).append("|")// 需要被撤销的中心交易流水号
																		// ###
					.append(parseString(jsxx.get("YYLSH"))).append("|")// 需要被撤销的医院交易流水号
																		// ###
					.append("").append("|")// 住院日期
					.append("").append("|")// 出院日期
					.append("").append("|")// 出院疾病诊断
					.append("").append("|")// 出院疾病次要诊断
					.append("").append("|")// 住院床号
					.append("").append("|")// 出院疾病诊断3
					.append("").append("|")// 出院疾病诊断4
					.append("").append("|")// 出院疾病诊断5
					.append("").append("|")// 出院疾病诊断6
					.append("").append("|")// 出院疾病诊断7
					.append("").append("|")// 出院疾病诊断8
					.append("").append("|")// 出院疾病诊断9
					.append("").append("|")// 出院疾病诊断10
					.append("").append("|")// 出院疾病诊断11
					.append("").append("|")// 出院疾病诊断12
					.append("").append("|")// 出院疾病诊断13
					.append("").append("|")// 出院疾病诊断14
					.append("").append("|")// 出院疾病诊断15
			;
			map_ret.put("InputStr", inputStr.toString());
			map_ret.put("ItemNumberIndicator", 0);
			return map_ret;
		} catch (PersistentDataOperationException e) {
			logger.error("医保取消结算出错", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医保取消结算出错:"
							+ e.getMessage());
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-22
	 * @description 医保挂号退号保存
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveYbGhth(Map<String, Object> body)
			throws ModelDataOperationException {
		UserRoleToken user=UserRoleToken.getCurrent();
		StringBuffer hql = new StringBuffer();
		hql.append("update HZYB_JSJL set ZFPB=1,ZFRQ=sysdate,ZFGH=:ZFGH,ZFYWZQH=:ZFYWZQH,"
				+ "ZFYYJYLSH=:ZFYYJYLSH,ZFZXJYLSH=:ZFZXJYLSH where GHGL=:GHGL and JZLX=1");//此处需要增加结账类型，否则把门诊的也作废掉了
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("ZFGH", user.getId());// 20
		map_par.put("ZFYWZQH", body.get("ZFYWZQH") + "");
		map_par.put("ZFYYJYLSH", body.get("ZFYYJYLSH") + "");
		map_par.put("ZFZXJYLSH", body.get("ZFZXJYLSH") + "");
		map_par.put("GHGL", Long.parseLong(body.get("SBXH") + ""));
		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退号失败");
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 根据读卡信息查询病人的MZHM
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	public String queryOutpatientAssociation(String grbh, Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("select a.MZHM as MZHM from ")
				.append("MS_BRDA").append(" a,")
				.append("YB_CBRYJBXX")
				.append(" b where a.BRID=b.BRID and b.GRBH=:grbh");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("grbh", grbh);
		try {
			Map<String, Object> map_ret = dao.doLoad(hql.toString(), map_par);
			if (map_ret == null || map_ret.size() == 0) {
				return "0";
			}
			return parseString(map_ret.get("MZHM"));
		} catch (PersistentDataOperationException e) {
			logger.error("获取门诊号失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取门诊号失败:"
							+ e.getMessage());
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 获取门诊收费登记,上传,预结算,结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryYbMzjscs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		StringBuffer hql_brxx = new StringBuffer();
		hql_brxx.append(
				"select b.YLZH as YLZH,a.KSDM as KSDM,c.YLLB as YLLB,b.GRBH as GRBH from ")
				.append("MS_GHMX")
				.append(" a,")
				.append(" YB_CBRYJBXX b,YB_GHJS c where a.BRID=b.BRID and a.SBXH=:ghgl and c.SBXH=a.SBXH");
		StringBuffer hql_spxx = new StringBuffer();
		hql_spxx.append("select c.SPBH as SPBH,c.SPXX as SPXX from ")
				.append("MS_GHMX")
				.append(" a,")
				.append("YB_GHJS")
				.append(" b,")
				.append("YB_ZXSP")
				.append(" c where a.SBXH=b.SBXH and c.YYXH=b.JYLSH and a.SBXH=:ghgl");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("ghgl", parseLong(body.get("GHGL")));
		List<String> jbs = new ArrayList<String>();
		Map<String, Object> YBKXX = (Map<String, Object>) body.get("YBXX");
		body.put("SRKH", YBKXX.get("SRKH"));
		body.put("BLLX", YBKXX.get("BLLX"));
		try {
			// 报销和体检不需要去查询挂号信息
			if (YBKXX.containsKey("ISTJ")
					&& "1".equals(parseString(YBKXX.get("ISTJ")))) {// 体检
				body.put("GRBH", parseString(YBKXX.get("GRBH")));
				body.put("YLLB", "92");
				body.put("SPBH", parseString(YBKXX.get("SPBH")));
				map_ret.put("GRBH", parseString(YBKXX.get("GRBH")));
				map_ret.put("YLLB", "92");
			} else if (body.containsKey("ISBX")
					&& "1".equals(parseString(body.get("ISBX")))) {// 报销
				body.put("YLLB", "10");
				map_ret.put("YLLB", "10");
			} else {
				Map<String, Object> map_brxx = dao.doLoad(hql_brxx.toString(),
						map_par);
				if (map_brxx == null || map_brxx.size() == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "未找到挂号信息,或人员信息未保存");
				}
				Map<String, Object> map_spxx = dao.doLoad(hql_spxx.toString(),
						map_par);
				if (map_spxx != null && map_spxx.size() != 0) {
					body.putAll(map_spxx);
					jbs.add(parseString(map_spxx.get("SPXX")));
				}
				body.putAll(map_brxx);
				map_ret.putAll(map_brxx);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("门诊费用预结算失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "门诊费用预结算失败:"
							+ e.getMessage());
		}
		if (body.containsKey("ZD1") && body.get("ZD1") != null
				&& !"".equals(body.get("ZD1"))) {
			jbs.add(parseString(body.get("ZD1")));
		}
		if (body.containsKey("ZD2") && body.get("ZD2") != null
				&& !"".equals(body.get("ZD2"))) {
			jbs.add(parseString(body.get("ZD2")));
		}
		if (body.containsKey("ZD3") && body.get("ZD3") != null
				&& !"".equals(body.get("ZD3"))) {
			jbs.add(parseString(body.get("ZD1")));
		}
		body.put("JBS", jbs);
		// Map<String, Object> YBKXX = (Map<String, Object>) body.get("YBXX");
		// body.put("SRKH", YBKXX.get("SRKH"));
		// body.put("BLLX", YBKXX.get("BLLX"));
		Map<String, Object> map_mzdj = YbMzdj(body, ctx);
		map_ret.put("DJ", map_mzdj);
		body.put("MZH", map_mzdj.get("MZH"));
		map_ret.put("MZH", map_mzdj.get("MZH"));
		// map_ret.put("JYLSH", map_mzdj.get("JYLSH"));
		map_ret.put("SC", YbMzsc(body,map_ret, ctx));
		map_ret.put("YJS", YbMzyjs(body, ctx));
		Map<String, Object> mzjs = YbMzjs(body, ctx);
		map_ret.put("JS", mzjs);
		map_ret.put("YYLSH", mzjs.get("SenderSerialNo"));
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 医保结算成功,本地结算失败 用于查询取消结算的参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryMzqxjscs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			String fphm = parseString(body.get("FPHM"));
			UserRoleToken user=UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FPHM", fphm);
			parameters.put("JGID", manaUnitId);
			Map<String, Object> MZXX = dao
					.doLoad("select MZXH as MZXH,ZFPB as ZFPB,SFRQ as SFRQ ,FYZE as FYZE,JSLSH as JSLSH,CBXZ as CBXZ,BRXM as BRXM from "
							+ BSPHISEntryNames.MS_MZXX
							+ " where FPHM=:FPHM and JGID=:JGID", parameters);
			if (1 == Integer.parseInt(MZXX.get("ZFPB") + "")) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "该发票号码已作废");
			}
			parameters.remove("FPHM");
			parameters.put("MZXH", MZXX.get("MZXH"));
			long ll_found = dao.doCount(BSPHISEntryNames.MS_YJ01,
					"JGID=:JGID and MZXH=:MZXH and ZXPB>=1", parameters);
			if (ll_found > 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "该发票中的项目已执行过,不能作废!");
			}
			ll_found = 0;
			ll_found = dao.doCount(BSPHISEntryNames.MS_CF01,
					"JGID=:JGID and MZXH=:MZXH and FYBZ = 1", parameters);
			if (ll_found > 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "该发票中的药品已发出,不能作废!");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Map<String, Object> map_body = new HashMap<String, Object>();
			map_body.put("TYPE", 1);
			Map<String, Object> map_ret = getPar(map_body, ctx);
			map_ret.put("TransType", "3100");
			map_ret.put("ProcessingCode", "331140");
			Map<String, Object> ybxx = (Map<String, Object>) body.get("YBXX");

			StringBuffer hql = new StringBuffer();
			hql.append(
					"select b.JSRQ as JSRQ,b.YLLB as YLLB,b.JYXH as JYXH,b.ZXLSH as ZXLSH,a.MZXH as MZXH,b.YYLSH as YYLSH from ")
					.append(BSPHISEntryNames.MS_MZXX)
					.append(" a,")
					.append("YB_MZJS")
					.append(" b where a.MZXH=b.MZXH and a.FPHM=:fphm and a.JGID=:JGID");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("fphm", fphm);
			map_par.put("JGID", manaUnitId);
			Map<String, Object> jsxx = dao.doLoad(hql.toString(), map_par);
			String jslb = "1";// 结算类别 现在暂时只知道规定病种和普通门诊的取法
			if ("31".equals(parseString(jsxx.get("YLLB")))) {
				jslb = "3";
			}
			map_ret.put("MZXH", parseLong(jsxx.get("MZXH")));
			StringBuffer inputStr = new StringBuffer();
			inputStr.append(parseString(ybxx.get("SRKH"))).append("|")// 卡号/证号###
					.append(sdf.format(jsxx.get("JSRQ"))).append("|")// 操作日期
																		// 医院报销传入时间表示：费用发生日期，不是系统日期（针对
																		// 16
					// 门诊报销结算类别应该传入系统时间）###***
					.append(jslb).append("|")// 结算类别###
					.append(fphm).append("|")// 单据号###
					.append(parseString(jsxx.get("JYXH"))).append("|")// 门诊流水号###
					.append(getUserName(ctx)).append("|")// 操作员
					.append("").append("|")// 转往医院等级（费用发生医院编码）当结算类别=
											// 6,7,8，9,12，13，16时表示费用发生医院编码
					.append("").append("|")// 出院原因 1、康复
											// 2、转院 3、死亡
											// 9其他
					.append("").append("|")// 审批编号
					.append("").append("|")// 器官移植标志
					.append("").append("|")// 入院诊断
					.append(parseString(jsxx.get("ZXLSH"))).append("|")// 需要被撤销的中心交易流水号
																		// ###
					.append(parseString(jsxx.get("YYLSH"))).append("|")// 需要被撤销的医院交易流水号
																		// ###
					.append("").append("|")// 住院日期
					.append("").append("|")// 出院日期
					.append("").append("|")// 出院疾病诊断
					.append("").append("|")// 出院疾病次要诊断
					.append("").append("|")// 住院床号
					.append("").append("|")// 出院疾病诊断3
					.append("").append("|")// 出院疾病诊断4
					.append("").append("|")// 出院疾病诊断5
					.append("").append("|")// 出院疾病诊断6
					.append("").append("|")// 出院疾病诊断7
					.append("").append("|")// 出院疾病诊断8
					.append("").append("|")// 出院疾病诊断9
					.append("").append("|")// 出院疾病诊断10
					.append("").append("|")// 出院疾病诊断11
					.append("").append("|")// 出院疾病诊断12
					.append("").append("|")// 出院疾病诊断13
					.append("").append("|")// 出院疾病诊断14
					.append("").append("|")// 出院疾病诊断15
			;
			map_ret.put("InputStr", inputStr.toString());
			map_ret.put("ItemNumberIndicator", 0);
			return map_ret;
		} catch (PersistentDataOperationException e) {
			logger.error("医保取消结算出错", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医保取消结算出错:"
							+ e.getMessage());
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 获取门诊收费登记参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	public Map<String, Object> YbMzdj(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowDate = sdf.format(new Date());// 日期
		Map<String, Object> map_ret = getPar(body, ctx);
		map_ret.put("TransType", "3200");
		map_ret.put("ProcessingCode", "331200");
		String mzh = "M" + getMzh();// 住院/门诊流水号
		map_ret.put("MZH", mzh);
		String ryrq = nowDate;
		Map<String, Object> map_yyxx = getYybh(ctx);
		StringBuffer par = new StringBuffer();
		// 卡号/证号|日期|医疗类别|住院流水号|操作员|审批编号|器官移植标志|入院诊断疾病编码|需要被撤销的医院交易流水号|住院凭证号|入院日期|转出医院编码|医院报销费用发生医疗机构|费用发生医疗机构名称|转诊单号|出院日期|就诊科室
		par.append(parseString(body.get("SRKH")))// 卡号/证号###
				.append("|").append(nowDate)// 日期###
				.append("|").append(parseString(body.get("YLLB")))// 医疗类别###
				.append("|").append(mzh)// 住院/门诊流水号###
				.append("|").append(getUserId(ctx))// 操作员###
				.append("|").append(parseString(body.get("SPBH")))// 审批编号
				.append("|").append("")// 器官移植标志
				.append("|").append(parseString(body.get("JBBM")))// 入院诊断疾病编码
				.append("|").append("")// 需要被撤销的医院交易流水号
				.append("|").append("")// 住院凭证号
				.append("|").append(ryrq)// 入院日期###
				.append("|").append("")// 转出医院编码
				.append("|").append(parseString(map_yyxx.get("YYBM")))// 医院报销费用发生医疗机构
				.append("|").append(parseString(map_yyxx.get("YYMC")))// 费用发生医疗机构名称
				.append("|").append("")// 转诊单号
				.append("|").append(parseString(body.get("CYRQ")))// 出院日期,退休门诊统筹医院报销时必须录入***
				.append("|").append(parseString(body.get("KSDM")))// 就诊科室####
																	// 不知道是代码还是名称
																	// 需要问***
				.append("|");
		map_ret.put("InputStr", par.toString());
		map_ret.put("ItemNumberIndicator", 0);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 获取门诊收费上传参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>>  YbMzsc(Map<String, Object> body,Map<String,Object> map_body_ret,
			Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user=UserRoleToken.getCurrent();
		String jgName = user.getManageUnitName();// 用户的机构名称
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_cfmx = (List<Map<String, Object>>) body
				.get("CFMX");
		// Map<String,Object> map_cf01=(Map<String,Object>)body.get("CF01");
		int type = parseInt(body.get("TYPE"));
		int count = list_cfmx.size();
		int i = 0;
		StringBuffer hql_cfxx = new StringBuffer();// 查询处方其他信息,如频次 医生姓名等
		hql_cfxx.append(
				"select a.YSDM as YSDM,b.GYTJ as YPYF,b.YFDW as YFDW,a.KSDM as KSDM,b.YYTS as ZXTS,c.XMMC as YF,d.MRCS/d.ZXZQ as PC from ")
				.append(BSPHISEntryNames.MS_CF01)
				.append(" a,")
				.append(BSPHISEntryNames.MS_CF02)
				.append(" b,")
				.append(BSPHISEntryNames.ZY_YPYF)
				.append(" c,")
				.append(BSPHISEntryNames.GY_SYPC)
				.append(" d where a.CFSB=b.CFSB  and b.SBXH=:sbxh and b.GYTJ=c.YPYF and b.YPYF=d.PCBM");
		StringBuffer hql_cfjb = new StringBuffer();// 通过处方查出疾病
		hql_cfjb.append("select c.ICD10 as ICD10 from ")
				.append(BSPHISEntryNames.MS_CF01)
				.append(" a,")
				.append(BSPHISEntryNames.MS_CF02)
				.append(" b,")
				.append(BSPHISEntryNames.MS_BRZD)
				.append(" c")
				.append(" where a.CFSB=b.CFSB  and b.SBXH=:sbxh and a.JZXH=c.JZXH");
		StringBuffer hql_yjxx = new StringBuffer();// 查询医技其他信息,如频次 医生姓名等
		hql_yjxx.append(
				"select a.YSDM as YSDM,'' as YPYF,'' as YFDW,a.KSDM as KSDM,'' as ZXTS from ")
				.append(BSPHISEntryNames.MS_YJ01).append(" a,")
				.append(BSPHISEntryNames.MS_YJ02)
				.append(" b where a.YJXH=b.YJXH and b.SBXH=:sbxh");
		StringBuffer hql_yjjb = new StringBuffer();// 通过医技查出疾病
		hql_yjjb.append("select c.ICD10 as ICD10 from ")
				.append(BSPHISEntryNames.MS_YJ01)
				.append(" a,")
				.append(BSPHISEntryNames.MS_YJ02)
				.append(" b,")
				.append(BSPHISEntryNames.MS_BRZD)
				.append(" c where a.YJXH=b.YJXH and b.SBXH=:sbxh and a.JZXH=c.JZXH");
		StringBuffer hql_xmdzxx = new StringBuffer();// 查询项目对照信息
		hql_xmdzxx
				.append("select SFLB as SFLB,YBBM as SFXMBM,XMMC as SFXMMC,FYDJ as YPDJ from YB_FYDZ where YBPB=1 and FYXH=:fyxh and KSSJ<sysdate and (ZZSJ>sysdate or ZZSJ is null)");
		StringBuffer hql_ypdzxx = new StringBuffer();// 查询药品对照信息
		hql_ypdzxx
				.append("select SFLB as SFLB,YBBM as SFXMBM,ZWMC as SFXMMC,YPDJ as YPDJ from YB_YPDZ where YBPB=1 and YPXH=:fyxh and YPCD=:ypcd  and KSSJ<sysdate and (ZZSJ>sysdate or ZZSJ is null)");
		StringBuffer hql_xmxx = new StringBuffer();// 查询项目信息,找不到对照信息时用于显示错误.
		hql_xmxx.append("select FYMC as SFMC from GY_YLSF where FYXH=:fyxh");
		StringBuffer hql_ypxx = new StringBuffer();// 查询项目信息,找不到对照信息时用于显示错误.
		hql_ypxx.append("select YPMC as YPMC from YK_TYPK where YPXH=:fyxh");
		Date nowDate = new Date();
		List<String> list_jbs = (List<String>) body.get("JBS");
		try {
			int j = 1;
			Map<String, Object> map_body = new HashMap<String, Object>();
			map_body.put("TYPE", type);
			if (body.containsKey("ZHJS")) {
				map_body.put("ZHJS", body.get("ZHJS"));
			}
			double xyf=0,zlxyf=0,zcy=0,zlzcy=0,cyf=0,zlcyf=0,jcf=0,zljcf=0,tjf=0,zltjf=0,syf=0,zlsyf=0,ssf=0,zlssf=0,hyf=0,zlhyf=0,sxf=0,zlsxf=0,zcf=0,zlzcf=0,zlf=0,zlzlf=0,tzf=0,zltzf=0,hlf=0,cwf=0,qtf=0,sqbf=0,zjje=0,sbje=0;
			for (Map<String, Object> map_cfmx : list_cfmx) {
				j++;
				Map<String, Object> map_ret = getPar(map_body, ctx);
				// 　ItemNumberIndicator:
				// 结束标志。(上传条数大于1：第一条标识为1,中间记录统一标识为2,最后一条标识为9 ；上传条数等于1：标识为0)
				if (count == 1) {
					map_ret.put("ItemNumberIndicator", 0);
				} else {
					if((count-1)%50==0&&j==(count+1)){
						map_ret.put("ItemNumberIndicator", 0);
					}
					else if (i == 0) {
						map_ret.put("ItemNumberIndicator", 1);
					} else if (i == 49 || j - 1 == count) {
						map_ret.put("ItemNumberIndicator", 9);
						i = -1;
					} else {
						map_ret.put("ItemNumberIndicator", 2);
					}
					i++;
				}
				map_ret.put("TransType", "3800");
				map_ret.put("ProcessingCode", "031100");
				String spbh = "";
				String splx = "";
				// 该段代码 获取审批编号,审批类型
				// ...
				// if (body.containsKey("SPBH")) {
				// spbh = parseString(body.get("SPBH"));
				// splx = "0";
				// }
				if (map_cfmx.containsKey("SPBH")) {
					spbh = parseString(map_cfmx.get("SPBH"));
					splx = "0";
				}
				// 根据药品对照获取收费类别,收费项目编码,收费项目名称,药品等级
				String sfyp = "1";// 是否是药品
				Map<String, Object> map_dzxx = new HashMap<String, Object>();
				if (parseInt(map_cfmx.get("CFLX")) == 4
						|| parseInt(map_cfmx.get("CFLX")) == 0) {
					Map<String, Object> map_par_dzxx = new HashMap<String, Object>();
					map_par_dzxx.put("fyxh", parseLong(map_cfmx.get("YPXH")));
					List<Map<String, Object>> list_dzxx = dao.doQuery(
							hql_xmdzxx.toString(), map_par_dzxx);
					if (list_dzxx == null || list_dzxx.size() == 0) {
						Map<String, Object> map_xmxx = dao.doLoad(
								hql_xmxx.toString(), map_par_dzxx);
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								map_xmxx.get("SFMC") + " 费用未对照,请先对照");
					}
					map_dzxx = list_dzxx.get(0);
					sfyp = "0";
				} else {
					Map<String, Object> map_par_dzxx = new HashMap<String, Object>();
					map_par_dzxx.put("fyxh", parseLong(map_cfmx.get("YPXH")));
					map_par_dzxx.put("ypcd", parseLong(map_cfmx.get("YPCD")));
					List<Map<String, Object>> list_dzxx = dao.doQuery(
							hql_ypdzxx.toString(), map_par_dzxx);
					if (list_dzxx == null || list_dzxx.size() == 0) {
						map_par_dzxx.remove("ypcd");
						Map<String, Object> map_ypxx = dao.doLoad(
								hql_ypxx.toString(), map_par_dzxx);
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								map_ypxx.get("YPMC") + " 药品未对照,请先对照");
					}
					map_dzxx = list_dzxx.get(0);
				}
				double ypsl = parseDouble(map_cfmx.get("YPSL"));// 药品数量
				//double ypjg = parseDouble(map_cfmx.get("YPDJ"));// 药品单价
				double mcyl = 1;// 每次用量
				if (parseInt(map_cfmx.get("CFLX")) == 3) {
					ypsl = formatDouble(2,
							ypsl * parseDouble(map_cfmx.get("CFTS")));
					mcyl = parseDouble(map_cfmx.get("CFTS"));
				}
				// map_cfmx.put("YPSL", ypsl);
				//double ypje = formatDouble(2, formatDouble(4,ypsl * ypjg));
				double ypje =parseDouble(map_cfmx.get("HJJE"));//防止由于计算出错导致的差0.0几的问题,结算里面也从明细的HJJE取的
				map_cfmx.put("BLLX", body.get("BLLX"));
				double zlje = 0;
				double grzfje = 0;
				zjje=ypje;
				String sflb=parseString(map_dzxx.get("SFLB"));//收费类别
				if (map_cfmx.containsKey("ZFBL")
						&& parseDouble(map_cfmx.get("ZFBL")) == 1) {
					grzfje = ypje;
					sbje=grzfje;
				} else {
					// 获取自负比例
					Map<String, Double> map_zfzl = GSMedicareUtil.doQueryDzxx(
							dao, map_cfmx, ctx, "1".equals(sfyp) ? 1 : 2);// 计算自负自理
					if (map_zfzl == null) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								"自理自费计算失败,请检查参数和对照");
					}
					zlje = map_zfzl.get("ld_grzl");// 自理金额 不知道怎么计算 需要问***
					grzfje = map_zfzl.get("ld_grzf");// 个人自费金额 不知道怎么计算 需要问***
					sbje=parseDouble(zlje)+parseDouble(grzfje);
				}
				if("11".equals(sflb)){//西药费
					xyf=xyf+zjje;
					zlxyf=zlxyf+sbje;
				}else if("12".equals(sflb)){ //中成药  
					zcy =zcy +zjje;
					zlzcy=zlzcy+sbje;
				}else if("13".equals(sflb)){//中草药
					cyf =cyf +zjje;
					zlcyf=zlcyf+sbje;
				}else if("21".equals(sflb)){//检查费
					jcf =jcf +zjje;
					zljcf=zljcf+sbje;
				}else if("22".equals(sflb)){//特检费
					tjf =tjf +zjje;
					zltjf=zltjf+sbje;
				}else if("23".equals(sflb)){//输氧费
					syf  =syf  +zjje;
					zlsyf =zlsyf+sbje;
				}else if("24".equals(sflb)){//手术费
					ssf  =ssf  +zjje;
					zlssf =zlssf +sbje;
				}else if("25".equals(sflb)){//化验费
					hyf =hyf +zjje;
					zlhyf=zlhyf+sbje;
				}else if("26".equals(sflb)){//输血费
					sxf =sxf +zjje;
					zlsxf=zlsxf+sbje;
				}else if("27".equals(sflb)){//诊察费
					zcf =zcf +zjje;
					zlzcf=zlzcf+sbje;
				}else if("31".equals(sflb)){//治疗费
					zlf  =zlf  +zjje;
					zlzlf =zlzlf +sbje;
				}else if("32".equals(sflb)){//特治费
					tzf  =tzf  +zjje;
					zltzf =zltzf +sbje;
				}else if(map_cfmx.containsKey("ZFBL")
						&& parseDouble(map_cfmx.get("ZFBL")) == 1){
					if("33".equals(sflb)){
						hlf=hlf+zjje;
					}else if("34".equals(sflb)){
						cwf=cwf+zjje;
					}else if("91".equals(sflb)){
						qtf=qtf+zjje;
					}else{
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								parseString(map_dzxx.get("SFXMBM"))+"("+parseString(map_dzxx.get("SFXMMC"))+")自负比例100%,归并为:"+sflb+"医保不使用");	
					}
				}else{
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							parseString(map_dzxx.get("SFXMBM"))+"("+parseString(map_dzxx.get("SFXMMC"))+")自负比例100%,归并为:"+sflb+"医保不使用");	
				}
				sqbf = sqbf + (zjje - sbje);
				// double zlje =0;
				// double grzfje= ypje;
				// 剂型,每次用量,使用频次,用法 不知道是从对照表取还是本条数据计算,需要问
				String jx = "1";// 剂型
				String sypc = "1";// 使用频次
				String yf = "1";// 用法
				Map<String, Object> map_qtxx = new HashMap<String, Object>();
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("sbxh", parseLong(map_cfmx.get("SBXH")));
				if (parseInt(map_cfmx.get("CFLX")) != 4
						&& parseInt(map_cfmx.get("CFLX")) != 0) {
					map_qtxx = dao.doLoad(hql_cfxx.toString(), map_par);
					if (map_qtxx == null || map_qtxx.size() == 0) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								"有处方明细未维护用法或者频次,请检查处方单:");
					}
					yf = parseString(map_qtxx.get("YF"));
					sypc = parseString(map_qtxx.get("PC"));
					List<Map<String, Object>> list_cfjb = dao.doQuery(
							hql_cfjb.toString(), map_par);
					if (list_cfjb != null && list_cfjb.size() > 0) {
						for (Map<String, Object> map_cfjb : list_cfjb) {
							list_jbs.add(parseString(map_cfjb.get("ICD10")));
						}
					}
				} else {
					map_qtxx = dao.doLoad(hql_yjxx.toString(), map_par);
					List<Map<String, Object>> list_yjjb = dao.doQuery(
							hql_yjjb.toString(), map_par);
					if (list_yjjb != null && list_yjjb.size() > 0) {
						for (Map<String, Object> map_yjjb : list_yjjb) {
							list_jbs.add(parseString(map_yjjb.get("ICD10")));
						}
					}
				}
				map_qtxx.put("KSMC", "科别名称");
				map_qtxx.put("YSXM", "医生");
				if (map_qtxx != null && parseLong(map_qtxx.get("KSDM")) != 0) {
					Map<String, Object> map_ksmc = dao.doLoad(
							"GY_KSDM",
							parseLong(map_qtxx.get("KSDM")));
					if (map_ksmc != null) {
						map_qtxx.put("KSMC", parseString(map_ksmc.get("KSMC")));
					}
				}
				if (map_qtxx != null && parseLong(map_qtxx.get("YSDM")) != 0) {
					Map<String, Object> map_ksmc = dao.doLoad(
							BSPHISEntryNames.SYS_USERS,
							parseString(map_qtxx.get("YSDM")));
					if (map_ksmc != null) {
						map_qtxx.put("YSXM",
								parseString(map_ksmc.get("userName")));
					}
				}
				String yybz = "";// 职工医保二级保健干部用药标志,1、职工医保二级保健用药标志 0
									// 非职工医保二级保健用药标志 是否需要界面输入?
				Map<String, Object> map_yybm = getYybh(ctx);
				StringBuffer inputStr = new StringBuffer();
				inputStr.append("02")
						.append("|")
						// 项目编码###
						.append(parseString(map_yybm.get("YYBM")))
						.append("|")
						// 服务机构编号###
						.append(body.get("MZH"))
						.append("|")
						// 住院号（门诊号）###****
						.append(parseString(map_cfmx.get("SBXH")))
						.append("|")
						// 处方号
						// ###****
						.append(parseString(map_cfmx.get("YPXH")))
						.append("|")
						// 医院收费项目内码
						// ###
						.append(1)
						.append("|")
						// 交易类型,1 正交易 ###
						.append("000000")
						.append("|")
						// 单据号,在录入费用时必须传入’000000’###
						.append(spbh)
						.append("|")
						// 审批编号,如果有审批编码必须填写相应的审批类型***
						.append(sdf.format(sdf1.parse(parseString(map_cfmx
								.get("KFRQ")))))
						.append("|")
						// 处方日期,接口文档里面是DATE类型
						// 不知道要怎么传###
						.append(parseString(map_dzxx.get("SFLB")))
						.append("|")
						// 收费类别###
						.append(parseString(map_dzxx.get("SFXMBM")))
						.append("|")
						// 收费项目编码###
						.append(parseString(map_dzxx.get("SFXMMC")))
						.append("|")// 收费项目名称###
						.append(sfyp).append("|")// 药品/非药品,0 非药品 1 药品###
						.append(parseString(map_dzxx.get("YPDJ"))).append("|")// 药品等级###
						.append(map_cfmx.get("YPDJ")).append("|")// 单价###
						.append(ypsl).append("|")// 数量###
						.append(ypje).append("|")// 金额###
						.append(zlje).append("|")// 自理金额###
						.append(grzfje).append("|")// 个人自费金额###
						.append(jx).append("|")// 剂型###
						.append(mcyl).append("|")// 每次用量###
						.append(sypc).append("|")// 使用频次###
						.append(yf).append("|")// 用法###
						.append(map_qtxx.get("KSMC")).append("|")// 科别名称###
						.append("").append("|")// 执行天数
						.append("").append("|")// 1、已审核 0、未审核 不需填写
						.append("").append("|")// 剔除标志 不需填写
						.append("").append("|")// 作废标志
						.append("").append("|")// 全额自费标志
						.append(0).append("|")// 结算标志 录入费用时传入0
						.append(0).append("|")// 逻辑校验位 不需填写
						.append("").append("|")// 交易流水号 不需填写
						.append(j).append("|")// 处方流水号 对应于每次录入费用的流水### ***不知道是什么
												// 需要问
						.append("").append("|")// 医院流水号 不需填写
						.append("").append("|")// 撤销交易流水号 不需填写
						.append("").append("|")// 业务周期号 不需填写
						.append("").append("|")// 传输标志 不需填写
						.append(parseString(body.get("GRBH"))).append("|")// 就诊病人个人编号###
						.append("").append("|")// 审核口款 不需填写
						.append("").append("|")// 审核扣款原因 不需填写
						.append("").append("|")// 结算日期 不需填写
						.append(sdf.format(nowDate)).append("|")// 经办日期###
																// 不知道需要传什么格式的
																// 需要问
						.append("").append("|")// 冲正医院交易流水号 不需填写
						.append(splx).append("|")// 审批类型
						.append(yybz).append("|")// 职工医保二级保健干部用药标志
						.append("").append("|")// 临时字段 不需填写
						.append("").append("|")// 临时字段 不需填写
						.append(map_ret.get("BusiCycle")).append("|")// 医院上传明细业务周期号###
						.append(map_ret.get("SenderSerialNo")).append("|")// 医院上传明细医院交易流水号
																			// 不知道是不是取这个
																			// 需要问###***
						.append(map_qtxx.get("YSXM")).append("|")// 开方医生姓名###
						.append("00").append("|")// 连锁机构编码 如果没有连锁机构传入00###
						.append(jgName).append("|")// 连锁机构名称
													// 如果没有连锁机构填写自己定点医疗机构名称###
				;
				map_ret.put("InputStr", inputStr.toString());
				list_ret.add(map_ret);
			}
			Map<String,Object> map_mzjsmx=new HashMap<String,Object>();//医保结算明细
			map_mzjsmx.put("XYF", xyf);
			map_mzjsmx.put("ZLXYF", zlxyf);
			map_mzjsmx.put("ZCY", zcy);
			map_mzjsmx.put("ZLZCY", zlzcy);
			map_mzjsmx.put("CYF", cyf);
			map_mzjsmx.put("ZLCYF", zlcyf);
			map_mzjsmx.put("JCF", jcf);
			map_mzjsmx.put("ZLJCF", zljcf);
			map_mzjsmx.put("TJF", tjf);
			map_mzjsmx.put("ZLTJF", zltjf);
			map_mzjsmx.put("SYF", syf);
			map_mzjsmx.put("ZLSYF", zlsyf);
			map_mzjsmx.put("SSF", ssf);
			map_mzjsmx.put("ZLSSF", zlssf);
			map_mzjsmx.put("HYF", hyf);
			map_mzjsmx.put("ZLHYF", zlhyf);
			map_mzjsmx.put("SXF", sxf);
			map_mzjsmx.put("ZLSXF", zlsxf);
			map_mzjsmx.put("ZCF", zcf);
			map_mzjsmx.put("ZLZCF", zlzcf);
			map_mzjsmx.put("ZLF", zlf);
			map_mzjsmx.put("ZLZLF", zlzlf);
			map_mzjsmx.put("TZF", tzf);
			map_mzjsmx.put("ZLTZF", zltzf);
			map_mzjsmx.put("HLF", hlf);
			map_mzjsmx.put("CWF", cwf);
			map_mzjsmx.put("QTF", qtf);
			map_mzjsmx.put("SQBF", sqbf);
			map_body_ret.put("YBMZMX", map_mzjsmx);
		} catch (Exception e) {
			logger.error("门诊费用上传失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "门诊费用上传失败:"
							+ e.getMessage());
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 获取门诊收费预结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> YbMzyjs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowDate = sdf.format(new Date());// 日期
		Map<String, Object> map_body = new HashMap<String, Object>();
		map_body.put("TYPE", 1);
		if (body.containsKey("ZHJS")) {
			map_body.put("ZHJS", body.get("ZHJS"));
		}
		Map<String, Object> map_ret = getPar(map_body, ctx);
		map_ret.put("TransType", "3100");
		map_ret.put("ProcessingCode", "331120");
		String czrq = nowDate;// 操作日期,不知道接口说明里面是什么意思,需要问
		String jslb = "1";
		if ("31".equals(parseString(body.get("YLLB")))) {// 规定病种
			jslb = "3";
		} else if ("92".equals(parseString(body.get("YLLB")))) {// 体检
			jslb = "20";
		} else if ("10".equals(parseString(body.get("YLLB")))) {// 报销
			jslb = "16";
		}
		Map<String, Object> map_jbs = new HashMap<String, Object>();
		List<String> list_jbs = (List<String>) body.get("JBS");
		int count = list_jbs.size();
		if (count == 0) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "该病人没有相关诊断,请录入诊断");
		}
		if (count > 15) {
			count = 15;
		}
		for (int i = 0; i < count; i++) {
			map_jbs.put("JB" + i, list_jbs.get(i));
		}
		StringBuffer inputStr = new StringBuffer();
		inputStr.append(parseString(body.get("SRKH"))).append("|")// 卡号/证号###
				.append(czrq).append("|")// 操作日期 医院报销传入时间表示：费用发生日期，不是系统日期（针对 16
											// 门诊报销结算类别应该传入系统时间）###***
				.append(jslb).append("|")// 结算类别###
				.append("000000").append("|")// 单据号 预结算单据号为’000000’###
				.append(parseString(body.get("MZH"))).append("|")// 门诊流水号###
				.append(getUserName(ctx)).append("|")// 操作员###
				.append("").append("|")// 转往医院等级（费用发生医院编码）当结算类别=
										// 6,7,8，9,12，13，16时表示费用发生医院编码
				.append("").append("|")// 出院原因 1、康复 2、转院 3、死亡 9其他
				.append(parseString(body.get("SPBH"))).append("|")// 审批编号
				.append("").append("|")// 器官移植标志
				.append(parseString(body.get("SPXX"))).append("|")// 入院诊断
				.append("").append("|")// 需要被撤销的中心交易流水号 预结算和交易结算的时候传入值为空
				.append("").append("|")// 需要被撤销的医院交易流水号 预结算和交易结算的时候传入值为空
				.append(nowDate).append("|")// 住院日期
				.append(nowDate).append("|")// 出院日期
				.append(parseString(map_jbs.get("JB0"))).append("|")// 出院疾病诊断
				.append(parseString(map_jbs.get("JB1"))).append("|")// 出院疾病次要诊断
				.append("").append("|")// 住院床号
				.append(parseString(map_jbs.get("JB2"))).append("|")// 出院疾病诊断3
				.append(parseString(map_jbs.get("JB3"))).append("|")// 出院疾病诊断4
				.append(parseString(map_jbs.get("JB4"))).append("|")// 出院疾病诊断5
				.append(parseString(map_jbs.get("JB5"))).append("|")// 出院疾病诊断6
				.append(parseString(map_jbs.get("JB6"))).append("|")// 出院疾病诊断7
				.append(parseString(map_jbs.get("JB7"))).append("|")// 出院疾病诊断8
				.append(parseString(map_jbs.get("JB8"))).append("|")// 出院疾病诊断9
				.append(parseString(map_jbs.get("JB9"))).append("|")// 出院疾病诊断10
				.append(parseString(map_jbs.get("JB10"))).append("|")// 出院疾病诊断11
				.append(parseString(map_jbs.get("JB11"))).append("|")// 出院疾病诊断12
				.append(parseString(map_jbs.get("JB12"))).append("|")// 出院疾病诊断13
				.append(parseString(map_jbs.get("JB13"))).append("|")// 出院疾病诊断14
				.append(parseString(map_jbs.get("JB14"))).append("|")// 出院疾病诊断15
		;
		map_ret.put("InputStr", inputStr.toString());
		map_ret.put("ItemNumberIndicator", 0);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 获取门诊收费结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> YbMzjs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowDate = sdf.format(new Date());// 日期
		Map<String, Object> map_body = new HashMap<String, Object>();
		map_body.put("TYPE", 1);
		if (body.containsKey("ZHJS")) {
			map_body.put("ZHJS", body.get("ZHJS"));
		}
		Map<String, Object> map_ret = getPar(map_body, ctx);
		map_ret.put("TransType", "3100");
		map_ret.put("ProcessingCode", "331130");
		String czrq = nowDate;// 操作日期,不知道接口说明里面是什么意思,需要问
		String jslb = "1";
		if ("31".equals(parseString(body.get("YLLB")))) {
			jslb = "3";
		}
		Map<String, Object> map_jbs = new HashMap<String, Object>();
		List<String> list_jbs = (List<String>) body.get("JBS");
		int count = list_jbs.size();
		if (count == 0) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "该病人没有相关诊断,请录入诊断");
		}
		if (count > 15) {
			count = 15;
		}
		for (int i = 0; i < count; i++) {
			map_jbs.put("JB" + i, list_jbs.get(i));
		}
		StringBuffer inputStr = new StringBuffer();
		inputStr.append(parseString(body.get("SRKH"))).append("|")// 卡号/证号###
				.append(czrq).append("|")// 操作日期 医院报销传入时间表示：费用发生日期，不是系统日期（针对 16
											// 门诊报销结算类别应该传入系统时间）###***
				.append(jslb).append("|")// 结算类别###
				.append(parseString(body.get("DJH"))).append("|")// 单据号
																	// 预结算单据号为’000000’###
				.append(parseString(body.get("MZH"))).append("|")// 门诊流水号###
				.append(getUserName(ctx)).append("|")// 操作员###
				.append(parseString(body.get("ZWYYDJ"))).append("|")// 转往医院等级（费用发生医院编码）当结算类别=
																	// 6,7,8，9,12，13，16时表示费用发生医院编码
				.append(parseString(body.get("CYYY"))).append("|")// 出院原因 1、康复
																	// 2、转院 3、死亡
																	// 9其他
				.append(parseString(body.get("SPBH"))).append("|")// 审批编号
				.append(parseString(body.get("QGYZBZ"))).append("|")// 器官移植标志
				.append(parseString(body.get("SPXX"))).append("|")// 入院诊断
				.append("").append("|")// 需要被撤销的中心交易流水号 预结算和交易结算的时候传入值为空
				.append("").append("|")// 需要被撤销的医院交易流水号 预结算和交易结算的时候传入值为空
				.append(nowDate).append("|")// 住院日期
				.append(nowDate).append("|")// 出院日期
				.append(parseString(map_jbs.get("JB0"))).append("|")// 出院疾病诊断
				.append(parseString(map_jbs.get("JB1"))).append("|")// 出院疾病次要诊断
				.append("").append("|")// 住院床号
				.append(parseString(map_jbs.get("JB2"))).append("|")// 出院疾病诊断3
				.append(parseString(map_jbs.get("JB3"))).append("|")// 出院疾病诊断4
				.append(parseString(map_jbs.get("JB4"))).append("|")// 出院疾病诊断5
				.append(parseString(map_jbs.get("JB5"))).append("|")// 出院疾病诊断6
				.append(parseString(map_jbs.get("JB6"))).append("|")// 出院疾病诊断7
				.append(parseString(map_jbs.get("JB7"))).append("|")// 出院疾病诊断8
				.append(parseString(map_jbs.get("JB8"))).append("|")// 出院疾病诊断9
				.append(parseString(map_jbs.get("JB9"))).append("|")// 出院疾病诊断10
				.append(parseString(map_jbs.get("JB10"))).append("|")// 出院疾病诊断11
				.append(parseString(map_jbs.get("JB11"))).append("|")// 出院疾病诊断12
				.append(parseString(map_jbs.get("JB12"))).append("|")// 出院疾病诊断13
				.append(parseString(map_jbs.get("JB13"))).append("|")// 出院疾病诊断14
				.append(parseString(map_jbs.get("JB14"))).append("|")// 出院疾病诊断15
		;
		map_ret.put("InputStr", inputStr.toString());
		map_ret.put("ItemNumberIndicator", 0);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 保存医保门诊结算信息
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	
	public void saveYbMzjs(Map<String, Object> body)
			throws ModelDataOperationException {
		try {
			dao.doSave("create", "phis.application.yb.schemas.YB_MZJS", body, false);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "医保结算信息保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医保结算信息保存失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 获取医保发票作废参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryYbFpzfcs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
			try {
				String fphm = parseString(body.get("FPHM"));
				UserRoleToken user=UserRoleToken.getCurrent();
				String manaUnitId = user.getManageUnitId();// 用户的机构ID
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("FPHM", fphm);
				parameters.put("JGID", manaUnitId);
				Map<String, Object> MZXX = dao
						.doLoad("select MZXH as MZXH,ZFPB as ZFPB,SFRQ as SFRQ ,FYZE as FYZE,JSLSH as JSLSH,CBXZ as CBXZ,BRXM as BRXM from "
								+ BSPHISEntryNames.MS_MZXX
								+ " where FPHM=:FPHM and JGID=:JGID", parameters);
				if (1 == Integer.parseInt(MZXX.get("ZFPB") + "")) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "该发票号码已作废");
				}
				parameters.remove("FPHM");
				parameters.put("MZXH", MZXX.get("MZXH"));
				long ll_found = dao.doCount(BSPHISEntryNames.MS_YJ01,
						"JGID=:JGID and MZXH=:MZXH and ZXPB>=1", parameters);
				if (ll_found > 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "该发票中的项目已执行过,不能作废!");
				}
				ll_found = 0;
				ll_found = dao.doCount(BSPHISEntryNames.MS_CF01,
						"JGID=:JGID and MZXH=:MZXH and FYBZ = 1", parameters);
				if (ll_found > 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "该发票中的药品已发出,不能作废!");
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Map<String, Object> map_body = new HashMap<String, Object>();
				map_body.put("TYPE", 1);
				Map<String, Object> map_ret = getPar(map_body, ctx);
				map_ret.put("TransType", "3100");
				map_ret.put("ProcessingCode", "331140");
				Map<String, Object> ybxx = (Map<String, Object>) body.get("YBXX");

				StringBuffer hql = new StringBuffer();
				hql.append(
						"select b.JSRQ as JSRQ,b.YLLB as YLLB,b.JYXH as JYXH,b.ZXLSH as ZXLSH,a.MZXH as MZXH,b.YYLSH as YYLSH from ")
						.append(BSPHISEntryNames.MS_MZXX)
						.append(" a,")
						.append("YB_MZJS")
						.append(" b where a.MZXH=b.MZXH and a.FPHM=:fphm and a.JGID=:JGID");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("fphm", fphm);
				map_par.put("JGID", manaUnitId);
				Map<String, Object> jsxx = dao.doLoad(hql.toString(), map_par);
				String jslb = "1";// 结算类别 现在暂时只知道规定病种和普通门诊的取法
				if ("31".equals(parseString(jsxx.get("YLLB")))) {
					jslb = "3";
				}
				map_ret.put("MZXH", parseLong(jsxx.get("MZXH")));
				StringBuffer inputStr = new StringBuffer();
				inputStr.append(parseString(ybxx.get("SRKH"))).append("|")// 卡号/证号###
						.append(sdf.format(jsxx.get("JSRQ"))).append("|")// 操作日期
																			// 医院报销传入时间表示：费用发生日期，不是系统日期（针对
																			// 16
						// 门诊报销结算类别应该传入系统时间）###***
						.append(jslb).append("|")// 结算类别###
						.append(fphm).append("|")// 单据号###
						.append(parseString(jsxx.get("JYXH"))).append("|")// 门诊流水号###
						.append(getUserName(ctx)).append("|")// 操作员
						.append("").append("|")// 转往医院等级（费用发生医院编码）当结算类别=
												// 6,7,8，9,12，13，16时表示费用发生医院编码
						.append("").append("|")// 出院原因 1、康复
												// 2、转院 3、死亡
												// 9其他
						.append("").append("|")// 审批编号
						.append("").append("|")// 器官移植标志
						.append("").append("|")// 入院诊断
						.append(parseString(jsxx.get("ZXLSH"))).append("|")// 需要被撤销的中心交易流水号
																			// ###
						.append(parseString(jsxx.get("YYLSH"))).append("|")// 需要被撤销的医院交易流水号
																			// ###
						.append("").append("|")// 住院日期
						.append("").append("|")// 出院日期
						.append("").append("|")// 出院疾病诊断
						.append("").append("|")// 出院疾病次要诊断
						.append("").append("|")// 住院床号
						.append("").append("|")// 出院疾病诊断3
						.append("").append("|")// 出院疾病诊断4
						.append("").append("|")// 出院疾病诊断5
						.append("").append("|")// 出院疾病诊断6
						.append("").append("|")// 出院疾病诊断7
						.append("").append("|")// 出院疾病诊断8
						.append("").append("|")// 出院疾病诊断9
						.append("").append("|")// 出院疾病诊断10
						.append("").append("|")// 出院疾病诊断11
						.append("").append("|")// 出院疾病诊断12
						.append("").append("|")// 出院疾病诊断13
						.append("").append("|")// 出院疾病诊断14
						.append("").append("|")// 出院疾病诊断15
				;
				map_ret.put("InputStr", inputStr.toString());
				map_ret.put("ItemNumberIndicator", 0);
				return map_ret;
			} catch (PersistentDataOperationException e) {
				logger.error("医保取消结算出错", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "医保取消结算出错:"
								+ e.getMessage());
			}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 医保发票作废
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	
	public void saveYbFpzf(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		hql.append("update YB_MZJS set ZFPB=1 where ....");
		Map<String,Object> map_par=new HashMap<String,Object>();
		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医保发票作废本地保存失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 获取医保入院登记参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	public Map<String, Object> queryYbRydjcs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowDate = sdf.format(new Date());// 日期
		Map<String, Object> map_body = new HashMap<String, Object>();
		map_body.put("TYPE", 2);
		Map<String, Object> map_ret = getPar(map_body, ctx);
		map_ret.put("TransType", "3200");
		map_ret.put("ProcessingCode", "331200");
		String zylsh = "Z" + getMzh();// 住院/门诊流水号
		map_ret.put("ZYLSH", zylsh);
		Map<String, Object> map_yybm = getYybh(ctx);
		StringBuffer par = new StringBuffer();
		// 卡号/证号|日期|医疗类别|住院流水号|操作员|审批编号|器官移植标志|入院诊断疾病编码|需要被撤销的医院交易流水号|住院凭证号|入院日期|转出医院编码|医院报销费用发生医疗机构|费用发生医疗机构名称|转诊单号|出院日期|就诊科室
		par.append(parseString(body.get("SRKH")))// 卡号/证号###
				.append("|").append(nowDate)// 日期###
				.append("|").append(parseString(body.get("YLLB")))// 医疗类别###
				.append("|").append(zylsh)// 住院/门诊流水号###
				.append("|").append(getUserId(ctx))// 操作员###
				.append("|").append("")// 审批编号
				.append("|").append("")// 器官移植标志
				.append("|").append("")// 入院诊断疾病编码
				.append("|").append("")// 需要被撤销的医院交易流水号
				.append("|").append("")// 住院凭证号
				.append("|").append(nowDate)// 入院日期###
				.append("|").append("")// 转出医院编码
				.append("|").append(parseString(map_yybm.get("YYBM")))// 医院报销费用发生医疗机构
				.append("|").append(parseString(map_yybm.get("YYMC")))// 费用发生医疗机构名称
				.append("|").append("")// 转诊单号
				.append("|").append("")// 出院日期,退休门诊统筹医院报销时必须录入***
				.append("|").append(parseString(body.get("KSDM")))// 就诊科室####
																	// 不知道是代码还是名称
																	// 需要问***
				.append("|");
		map_ret.put("InputStr", par.toString());
		map_ret.put("ItemNumberIndicator", 0);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-13
	 * @description 删除入院登记信息(医保入院登记调用失败时)
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	
	public void deleteRydj(Map<String, Object> body)
			throws ModelDataOperationException {
		long zyh=MedicineUtils.parseLong(body.get("ZYH"));
		StringBuffer hql=new StringBuffer();
		hql.append("delete from ZY_BRRY where ZYH=:zyh");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("zyh", zyh);
		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医保入院登记失败,本地删除入院记录失败", e);
		}
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-13
	 * @description 更新入院登记信息(医保入院登记调用成功时)
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	
	public void updateRydj(Map<String, Object> body)
			throws ModelDataOperationException {
		//更新入院登记表,具体字段根据需要增加,用于后面转换性质和结算用 
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description  查询医保住院病人性质转换参数
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	
	public Map<String, Object> queryYbZyxzzhcs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		long zyh = parseLong(body.get("ZYH"));
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("code", 200);
		ret.put("msg", "ok");
		try {
			if (!body.containsKey("ZH")) {// 如果是注销
				Map<String, Object> BRXX = dao.doLoad(BSPHISEntryNames.ZY_BRRY,
						zyh);
				if (parseInt(BRXX.get("CYPB")) == 1) {
					ret.put("msg", "病人已通知出院，不能注销!");
					ret.put("code", 9000);
					return ret;
				}
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("zyh", zyh);
				long count = dao.doCount(BSPHISEntryNames.ZY_TBKK,
						"ZYH = :zyh", map_par);
				if (count > 0) {
					StringBuffer hql = new StringBuffer();
					hql.append("select sum(JKJE) as JKJE from ")
							.append(BSPHISEntryNames.ZY_TBKK)
							.append(" where ZYH = :zyh AND ZFPB = 0");
					Map<String, Object> JKJE = dao.doLoad(hql.toString(),
							map_par);
					if (JKJE.get("JKJE") != null) {
						if (parseDouble(JKJE.get("JKJE")) != 0) {
							ret.put("msg", "此病人已有缴款发生，不能进行注销操作!");
							ret.put("code", 9000);
							return ret;
						}
					}
				}
				count = dao
						.doCount(
								BSPHISEntryNames.ZY_BQYZ,
								"( ZYH = :zyh ) AND ( LSBZ = 0 OR (LSBZ= 2 AND QRSJ IS NULL) OR SYBZ = 1 ) AND ( JFBZ < 3 OR JFBZ = 9 )",
								map_par);
				if (count > 0) {
					ret.put("msg", "病人有未停未发医嘱，不能进行注销操作!");
					ret.put("code", 9000);
					return ret;
				}
				StringBuffer hql = new StringBuffer();
				hql.append("select COUNT(*) as COUNT,sum(ZJJE) as ZJJE from ")
						.append(BSPHISEntryNames.ZY_FYMX)
						.append(" WHERE ZYH = :zyh");
				Map<String, Object> ZY_FYMX = dao.doLoad(hql.toString(),
						map_par);
				count = parseLong(ZY_FYMX.get("COUNT"));
				double zjje = 0;
				if (ZY_FYMX.get("ZJJE") != null) {
					zjje = parseDouble(ZY_FYMX.get("ZJJE"));
				}
				if (count > 0) {
					if (zjje != 0) {
						ret.put("msg", "此病人已有费用发生，不能进行注销操作!");
						ret.put("code", 9000);
						return ret;
					}
				}
			} else {// 性质转换,医保转自费
				StringBuffer hql = new StringBuffer();
				hql.append(" ZYH=:zyh and SCBZ=1");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("zyh", zyh);
				long l = dao.doCount(BSPHISEntryNames.ZY_FYMX, hql.toString(),
						map_par);
				if (l > 0) {
					ret.put("msg", "已有费用上传,请先取消费用上传!");
					ret.put("code", 9000);
					return ret;
				}
			}
			Map<String, Object> map_body = new HashMap<String, Object>();
			map_body.put("TYPE", 2);
			Map<String, Object> map_cs = getPar(map_body, ctx);
			map_cs.put("TransType", "3240");
			map_cs.put("ProcessingCode", "331200");
			StringBuffer par = new StringBuffer();
			par.append(parseString(body.get("ZYLSH")))// 住院流水号###
					.append("|").append(parseString(body.get("YYLSH")))// 需要被撤销的医院交易流水号###
					.append("|");
			map_cs.put("InputStr", par.toString());
			map_cs.put("ItemNumberIndicator", 0);
			ret.put("body", map_cs);
		} catch (PersistentDataOperationException e) {
			logger.error("病人住院信息查询", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人住院信息查询:"
							+ e.getMessage());
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-14
	 * @description 根据医保卡信息查询住院号码
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	public Map<String, Object> queryZyhmByYbkxx(Map<String, Object> body)
			throws ModelDataOperationException {
		return null;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 获取医保费用上传,住院预结算,住院结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	public Map<String, Object> queryYbZyjscs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select to_char(RYRQ,'yyyy-mm-dd hh24:mi:ss') as RYRQ,to_char(CYRQ,'yyyy-mm-dd hh24:mi:ss') as CYRQ ,SRKH as SRKH,ZYLSH as ZYLSH,YYLSH as YYLSH from ")
				.append(BSPHISEntryNames.ZY_BRRY).append(" where ZYH=:zyh");
		StringBuffer hql_jbs = new StringBuffer();
		hql_jbs.append("select b.ICD10 as ICD10 from ")
				.append(BSPHISEntryNames.ZY_RYZD).append(" a,")
				.append(BSPHISEntryNames.GY_JBBM)
				.append(" b where a.ZDXH=b.JBXH and a.ZYH=:zyh");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zyh", parseLong(body.get("ZYH")));
		List<Map<String, Object>> list_brry;
		Map<String, Object> map_brry;
		List<Map<String, Object>> list_jbs;
		Map<String, Object> map_jbs = new HashMap<String, Object>();
		try {
			list_brry = dao.doSqlQuery(hql.toString(), map_par);
			if (list_brry == null || list_brry.size() == 0
					|| list_brry.get(0) == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "病人住院信息查询失败:");
			}
			map_brry = list_brry.get(0);
			list_jbs = dao.doQuery(hql_jbs.toString(), map_par);
			if (list_jbs == null || list_jbs.size() == 0
					|| list_jbs.get(0) == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR,
						"未查到病人的疾病诊断信息,请录入诊断再结算");
			}
			for (int i = 1; i < list_jbs.size() + 1; i++) {
				map_jbs.put("JB" + i, list_jbs.get(i - 1).get("ICD10"));
			}
		} catch (PersistentDataOperationException e) {
			logger.error("病人住院信息查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人住院信息查询失败:"
							+ e.getMessage());
		}
		body.putAll(map_brry);
		body.put("JBS", map_jbs);
		map_ret.put("SC", queryYbZysccs(body, ctx));
		map_ret.put("YJS", queryYbZyyjscs(body, ctx));
		Map<String, Object> zyjs = queryZyjscs(body, ctx);
		map_ret.put("JS", zyjs);
		map_ret.put("YYLSH", zyjs.get("SenderSerialNo"));
		return map_ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 医保住院费用上传(单)
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	public List<Map<String, Object>> queryYbZysccs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		String jgName = UserRoleToken.getCurrent().getManageUnitId();// 用户的机构名称
		List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
		long zyh = parseLong(body.get("ZYH"));
		String bllx = parseString(body.get("BLLX"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDate = sdf.format(new Date());
		StringBuffer hql = new StringBuffer();// 查询费用明细
		hql.append(
				"select  a.ZJJE as ZJJE,a.JLXH as JLXH,a.YPCD as YPCD,a.YPLX as YPLX,a.FYXM as FYXM,a.YZXH as YZXH,a.FYRQ as FYRQ,a.FYXH as FYXH,a.FYMC as FYMC,a.FYDJ as FYDJ,a.FYSL as FYSL,a.YSGH as YSGH,a.FYKS as FYKS from ")
				.append(BSPHISEntryNames.ZY_FYMX).append(" a")
				.append("  where  a.ZYH=:zyh and (SCBZ = 0 or SCBZ is null) ");
		StringBuffer hql_ysxm = new StringBuffer();// 查询医生姓名
		hql_ysxm.append("select userName as YSXM from ")
				.append(BSPHISEntryNames.SYS_USERS)
				.append(" where userId=:ysgh");
		StringBuffer hql_ksmc = new StringBuffer();// 查询科室名称
		hql_ksmc.append("select KSMC as KSMC from GY_KSDM").append(" where KSDM=:ksdm");
		StringBuffer hql_xmdzxx = new StringBuffer();// 查询项目对照信息
		hql_xmdzxx
				.append("select SFLB as SFLB,YBBM as SFXMBM,XMMC as SFXMMC,FYDJ as YPDJ from YB_FYDZ where YBPB=1 and FYXH=:fyxh and KSSJ<:fyrq and (ZZSJ>:fyrq or ZZSJ is null)");
		StringBuffer hql_ypdzxx = new StringBuffer();// 查询药品对照信息
		hql_ypdzxx
				.append("select SFLB as SFLB,YBBM as SFXMBM,ZWMC as SFXMMC,YPDJ as YPDJ from YB_YPDZ where YBPB=1 and YPXH=:fyxh and YPCD=:ypcd and KSSJ<:fyrq and (ZZSJ>:fyrq or ZZSJ is null)");
		StringBuffer hql_xmxx = new StringBuffer();// 查询项目信息,找不到对照信息时用于显示错误.
		hql_xmxx.append("select FYMC as SFMC from GY_YLSF where FYXH=:fyxh");
		StringBuffer hql_ypxx = new StringBuffer();// 查询项目信息,找不到对照信息时用于显示错误.
		hql_ypxx.append("select YPMC as YPMC from YK_TYPK where YPXH=:fyxh");
		StringBuffer hql_yzxx = new StringBuffer();// 查询医嘱信息,如频次,剂型 等
		hql_yzxx.append(
				"select a.ZFPB as ZFPB,a.YCSL as YCSL,b.XMMC as YF,c.MRCS/c.ZXZQ as PC,a.YPLX as YPLX from ")
				.append(BSPHISEntryNames.ZY_BQYZ)
				.append(" a,")
				.append(BSPHISEntryNames.ZY_YPYF)
				.append(" b,")
				.append(BSPHISEntryNames.GY_SYPC)
				.append(" c where a.JLXH=:yzxh and a.YPYF=b.YPYF and a.SYPC=c.PCBM");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("zyh", zyh);
			List<Map<String, Object>> list_fymx = dao.doQuery(hql.toString(),
					map_par);
			if (list_fymx == null || list_fymx.size() == 0) {
				return list_ret;
			}
			int count = list_fymx.size();
			int i = 0;
			int j = 1;
			Map<String, Object> map_body = new HashMap<String, Object>();
			map_body.put("TYPE", 2);
			for (Map<String, Object> map_fymx : list_fymx) {
				j++;
				Map<String, Object> map_ret = getPar(map_body, ctx);
				map_fymx.put("BLLX", bllx);
				// 　ItemNumberIndicator:
				// 结束标志。(上传条数大于1：第一条标识为1,中间记录统一标识为2,最后一条标识为9 ；上传条数等于1：标识为0)
				if (count == 1) {
					map_ret.put("ItemNumberIndicator", 0);
				} else {
					if((count-1)%50==0&&j==(count+1)){
						map_ret.put("ItemNumberIndicator", 0);
					}
					else if (i == 0) {
						map_ret.put("ItemNumberIndicator", 1);
					} else if (i == 49 || j - 1 == count) {
						map_ret.put("ItemNumberIndicator", 9);
						i = -1;
					} else {
						map_ret.put("ItemNumberIndicator", 2);
					}
					i++;
				}
				map_ret.put("TransType", "3800");
				map_ret.put("ProcessingCode", "031100");
				int spbh = 0;
				String splx = "";
				// 该段代码 获取审批编号,审批类型
				// ...
				String sfyp = "1";// 是否是药品
				Map<String, Object> map_dzxx = new HashMap<String, Object>();
				if (parseInt(map_fymx.get("YPLX")) == 0) {
					Map<String, Object> map_par_dzxx = new HashMap<String, Object>();
					map_par_dzxx.put("fyxh", parseLong(map_fymx.get("FYXH")));
					map_par_dzxx.put("fyrq", map_fymx.get("FYRQ"));

					List<Map<String, Object>> list_dzxx = dao.doQuery(
							hql_xmdzxx.toString(), map_par_dzxx);
					if (list_dzxx == null || list_dzxx.size() == 0) {
						map_par_dzxx.remove("fyrq");
						Map<String, Object> map_xmxx = dao.doLoad(
								hql_xmxx.toString(), map_par_dzxx);
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								map_xmxx.get("SFMC") + " 费用未对照,请先对照");
					}
					map_dzxx = list_dzxx.get(0);
					sfyp = "0";
				} else {
					Map<String, Object> map_par_dzxx = new HashMap<String, Object>();
					map_par_dzxx.put("fyxh", parseLong(map_fymx.get("FYXH")));
					map_par_dzxx.put("fyrq", map_fymx.get("FYRQ"));
					map_par_dzxx.put("ypcd", map_fymx.get("YPCD"));
					List<Map<String, Object>> list_dzxx = dao.doQuery(
							hql_ypdzxx.toString(), map_par_dzxx);
					if (list_dzxx == null || list_dzxx.size() == 0) {
						map_par_dzxx.remove("ypcd");
						map_par_dzxx.remove("fyrq");
						Map<String, Object> map_ypxx = dao.doLoad(
								hql_ypxx.toString(), map_par_dzxx);
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								map_ypxx.get("YPMC") + " 药品未对照,请先对照");
					}
					map_dzxx = list_dzxx.get(0);
				}
				double ypsl = parseDouble(map_fymx.get("FYSL"));// 药品数量
				double ypjg = parseDouble(map_fymx.get("FYDJ"));// 药品单价
				//double ypje = formatDouble(2, formatDouble(4, ypsl * ypjg));
				double ypje =parseDouble(map_fymx.get("ZJJE"));//防止计算导致的差0.0几的问题,直接从ZJJE取
				// double zlje = 0;// 自理金额 不知道怎么计算 需要问***
				// double grzfje = 0;// 个人自费金额 不知道怎么计算 需要问***
				// 剂型,每次用量,使用频次,用法 不知道是从对照表取还是本条数据计算,需要问
				String jx = "1";// 剂型
				String sypc = "1";// 使用频次
				String yf = "1";// 用法
				String mcyl = "1";// 每次用量
				if (parseLong(map_fymx.get("YZXH")) != 0) {
					Map<String, Object> map_par_yzxx = new HashMap<String, Object>();
					map_par_yzxx.put("yzxh", parseLong(map_fymx.get("YZXH")));
					Map<String, Object> map_yzxx = dao.doLoad(
							hql_yzxx.toString(), map_par_yzxx);
					if (map_yzxx != null) {
						sypc = parseString(map_yzxx.get("PC"));
						yf = parseString(map_yzxx.get("YF"));
						if (parseInt(map_yzxx.get("YPLX")) == 3) {
							mcyl = "7";
						} else {
							mcyl = parseString(map_yzxx.get("YCSL"));
						}
						map_fymx.put("ZFPB", parseString(map_yzxx.get("ZFPB")));
					}
				}
				// 获取自负比例
				Map<String, Double> map_zfzl = GSMedicareUtil.doQueryDzxx(dao,
						map_fymx, ctx, "1".equals(sfyp) ? 1 : 2);// 计算自负自理
				if (map_zfzl == null || map_zfzl.size() == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"自理自费计算失败,请检查参数和对照");
				}
				double zlje = map_zfzl.get("ld_grzl");// 自理金额 ***
				double grzfje = map_zfzl.get("ld_grzf");// 个人自费金额 ***
				String ysxm = "医生";
				String ksmc = "科室";
				if (map_fymx.get("YSGH") != null
						&& !"".equals(map_fymx.get("YSGH"))) {
					Map<String, Object> map_par_ysxm = new HashMap<String, Object>();
					map_par_ysxm.put("ysgh", map_fymx.get("YSGH") + "");
					Map<String, Object> map_ysxm = dao.doLoad(
							hql_ysxm.toString(), map_par_ysxm);
					if (map_ysxm != null && map_ysxm.get("YSXM") != null) {
						ysxm = map_ysxm.get("YSXM") + "";
					}
				}
				if (map_fymx.get("FYKS") != null
						&& !"".equals(map_fymx.get("FYKS"))) {
					Map<String, Object> map_par_ksmc = new HashMap<String, Object>();
					map_par_ksmc.put("ksdm", parseLong(map_fymx.get("FYKS")));
					Map<String, Object> map_ksmc = dao.doLoad(
							hql_ksmc.toString(), map_par_ksmc);
					if (map_ksmc != null && map_ksmc.get("KSMC") != null) {
						ksmc = map_ksmc.get("KSMC") + "";
					}
				}
				String yybz = "";// 职工医保二级保健干部用药标志,1、职工医保二级保健用药标志 0
									// 非职工医保二级保健用药标志 是否需要界面输入?
				Map<String, Object> map_yybm = getYybh(ctx);
				StringBuffer inputStr = new StringBuffer();
				inputStr.append("02")
						.append("|")
						// 项目编码###
						.append(parseString(map_yybm.get("YYBM")))
						.append("|")
						// 服务机构编号###
						.append(parseString(body.get("ZYLSH")))
						.append("|")
						// 住院号（门诊号）###****
						.append(parseString(map_fymx.get("JLXH")))
						.append("|")
						// 处方号
						// ###****
						.append(parseString(map_fymx.get("FYXH")))
						.append("|")
						// 医院收费项目内码
						// ###
						.append(1)
						.append("|")
						// 交易类型,1 正交易 ###
						.append("000000")
						.append("|")
						// 单据号,在录入费用时必须传入’000000’###
						.append(spbh)
						.append("|")
						// 审批编号,如果有审批编码必须填写相应的审批类型***
						.append(sdf.format(sdf1.parse(parseString(map_fymx
								.get("FYRQ")))))
						.append("|")
						// 处方日期,接口文档里面是DATE类型
						// 不知道要怎么传###
						.append(parseString(map_dzxx.get("SFLB")))
						.append("|")
						// 收费类别###
						.append(parseString(map_dzxx.get("SFXMBM")))
						.append("|")
						// 收费项目编码###
						.append(parseString(map_dzxx.get("SFXMMC")))
						.append("|")// 收费项目名称###
						.append(sfyp).append("|")// 药品/非药品,0 非药品 1 药品###
						.append(parseString(map_dzxx.get("YPDJ"))).append("|")// 药品等级###
						.append(ypjg).append("|")// 单价###
						.append(ypsl).append("|")// 数量###
						.append(ypje).append("|")// 金额###
						.append(zlje).append("|")// 自理金额###
						.append(grzfje).append("|")// 个人自费金额###
						.append(jx).append("|")// 剂型###
						.append(mcyl).append("|")// 每次用量###
						.append(sypc).append("|")// 使用频次###
						.append(yf).append("|")// 用法###
						.append(ksmc).append("|")// 科别名称###
						.append("").append("|")// 执行天数
						.append("").append("|")// 1、已审核 0、未审核 不需填写
						.append("").append("|")// 剔除标志 不需填写
						.append("").append("|")// 作废标志
						.append("").append("|")// 全额自费标志
						.append(0).append("|")// 结算标志 录入费用时传入0
						.append("").append("|")// 逻辑校验位 不需填写
						.append("").append("|")// 交易流水号 不需填写
						.append(j).append("|")// 处方流水号 对应于每次录入费用的流水### ***不知道是什么
												// 需要问
						.append("").append("|")// 医院流水号 不需填写
						.append("").append("|")// 撤销交易流水号 不需填写
						.append("").append("|")// 业务周期号 不需填写
						.append("").append("|")// 传输标志 不需填写
						.append(parseString(body.get("GRBH"))).append("|")// 就诊病人个人编号###
						.append("").append("|")// 审核口款 不需填写
						.append("").append("|")// 审核扣款原因 不需填写
						.append("").append("|")// 结算日期 不需填写
						.append(nowDate).append("|")// 经办日期### 不知道需要传什么格式的 需要问
						.append("").append("|")// 冲正医院交易流水号 不需填写
						.append(splx).append("|")// 审批类型
						.append(yybz).append("|")// 职工医保二级保健干部用药标志
						.append("").append("|")// 临时字段 不需填写
						.append("").append("|")// 临时字段 不需填写
						.append(map_ret.get("BusiCycle")).append("|")// 医院上传明细业务周期号###
						.append(map_ret.get("SenderSerialNo")).append("|")// 医院上传明细医院交易流水号
																			// 不知道是不是取这个
																			// 需要问###***
						.append(ysxm).append("|")// 开方医生姓名###
						.append("00").append("|")// 连锁机构编码 如果没有连锁机构传入00###
						.append(jgName).append("|")// 连锁机构名称
													// 如果没有连锁机构填写自己定点医疗机构名称###
				;
				map_ret.put("JLXH", parseString(map_fymx.get("JLXH")));
				map_ret.put("InputStr", inputStr.toString());
				list_ret.add(map_ret);
			}
		} catch (Exception e) {
			logger.error("住院费用上传查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "住院费用上传查询失败:"
							+ e.getMessage());
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 医保住院预结算参数查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryYbZyyjscs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//String nowDate = sdf.format(new Date());// 日期
		Map<String, Object> map_body = new HashMap<String, Object>();
		map_body.put("TYPE", 2);
		Map<String, Object> map_ret = getPar(map_body, ctx);
		map_ret.put("TransType", "3200");
		map_ret.put("ProcessingCode", "331240");
		//String czrq = nowDate;// 操作日期,不知道接口说明里面是什么意思,需要问
		StringBuffer inputStr = new StringBuffer();
		Map<String, Object> map_jbs = (Map<String, Object>) body.get("JBS");
		try {
			inputStr.append(parseString(body.get("SRKH")))
					.append("|")
					// 卡号/证号###
					.append(sdf.format(sdf1.parse(parseString(body.get("CYRQ")))))
					.append("|")
					// 操作日期 医院报销传入时间表示：费用发生日期，不是系统日期（针对 16
					// 门诊报销结算类别应该传入系统时间）###***
					.append(2)
					.append("|")
					// 结算类别###
					.append("000000")
					.append("|")
					// 单据号 预结算单据号为’000000’###
					.append(parseString(body.get("ZYLSH")))
					.append("|")
					// 门诊流水号###
					.append(getUserName(ctx))
					.append("|")
					// 操作员###
					.append("")
					.append("|")
					// 转往医院等级（费用发生医院编码）当结算类别=
					// 6,7,8，9,12，13，16时表示费用发生医院编码
					.append(1)
					.append("|")
					// 出院原因 1、康复
					// 2、转院 3、死亡
					// 9其他
					.append("")
					.append("|")
					// 审批编号
					.append("")
					.append("|")
					// 器官移植标志
					.append("")
					.append("|")
					// 入院诊断
					.append("")
					.append("|")
					// 需要被撤销的中心交易流水号 预结算和交易结算的时候传入值为空
					.append("")
					.append("|")
					// 需要被撤销的医院交易流水号 预结算和交易结算的时候传入值为空
					.append(sdf.format(sdf1.parse(parseString(body.get("RYRQ")))))
					.append("|")
					// 住院日期
					.append(sdf.format(sdf1.parse(parseString(body.get("CYRQ")))))
					.append("|")// 出院日期
					.append(parseString(map_jbs.get("JB1"))).append("|")// 出院疾病诊断
					.append(parseString(map_jbs.get("JB2"))).append("|")// 出院疾病次要诊断
					.append(parseString(body.get("BRCH"))).append("|")// 住院床号
					.append(parseString(map_jbs.get("JB3"))).append("|")// 出院疾病诊断3
					.append(parseString(map_jbs.get("JB4"))).append("|")// 出院疾病诊断4
					.append(parseString(map_jbs.get("JB5"))).append("|")// 出院疾病诊断5
					.append(parseString(map_jbs.get("JB6"))).append("|")// 出院疾病诊断6
					.append(parseString(map_jbs.get("JB7"))).append("|")// 出院疾病诊断7
					.append(parseString(map_jbs.get("JB8"))).append("|")// 出院疾病诊断8
					.append(parseString(map_jbs.get("JB9"))).append("|")// 出院疾病诊断9
					.append(parseString(map_jbs.get("JB10"))).append("|")// 出院疾病诊断10
					.append(parseString(map_jbs.get("JB11"))).append("|")// 出院疾病诊断11
					.append(parseString(map_jbs.get("JB12"))).append("|")// 出院疾病诊断12
					.append(parseString(map_jbs.get("JB13"))).append("|")// 出院疾病诊断13
					.append(parseString(map_jbs.get("JB14"))).append("|")// 出院疾病诊断14
					.append(parseString(map_jbs.get("JB15"))).append("|")// 出院疾病诊断15
			;
		} catch (ParseException e) {
			logger.error("入院或出院日期格式错误", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入院或出院日期格式错误");
		}
		map_ret.put("InputStr", inputStr.toString());
		map_ret.put("ItemNumberIndicator", 0);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 医保住院结算参数查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryZyjscs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//String nowDate = sdf.format(new Date());// 日期
		Map<String, Object> map_body = new HashMap<String, Object>();
		map_body.put("TYPE", 2);
		Map<String, Object> map_ret = getPar(map_body, ctx);
		map_ret.put("TransType", "3200");
		map_ret.put("ProcessingCode", "331220");
		//String czrq = nowDate;// 操作日期,不知道接口说明里面是什么意思,需要问
		StringBuffer inputStr = new StringBuffer();
		Map<String, Object> map_jbs = (Map<String, Object>) body.get("JBS");
		try {
			inputStr.append(parseString(body.get("SRKH")))
					.append("|")
					// 卡号/证号###
					.append(sdf.format(sdf1.parse(parseString(body.get("CYRQ")))))
					.append("|")
					// 操作日期 医院报销传入时间表示：费用发生日期，不是系统日期（针对 16
					// 门诊报销结算类别应该传入系统时间）###***
					.append(2)
					.append("|")
					// 结算类别###
					.append("000000")
					.append("|")
					// 单据号 预结算单据号为’000000’### 注:由于FPHM是在最后界面获取的 所以取000000 前台替换
					.append(parseString(body.get("ZYLSH")))
					.append("|")
					// 门诊流水号###
					.append(getUserName(ctx))
					.append("|")
					// 操作员###
					.append("")
					.append("|")
					// 转往医院等级（费用发生医院编码）当结算类别=
					// 6,7,8，9,12，13，16时表示费用发生医院编码
					.append(1)
					.append("|")
					// 出院原因 1、康复
					// 2、转院 3、死亡
					// 9其他
					.append("")
					.append("|")
					// 审批编号
					.append("")
					.append("|")
					// 器官移植标志
					.append("")
					.append("|")
					// 入院诊断
					.append("")
					.append("|")
					// 需要被撤销的中心交易流水号 预结算和交易结算的时候传入值为空
					.append("")
					.append("|")
					// 需要被撤销的医院交易流水号 预结算和交易结算的时候传入值为空
					.append(sdf.format(sdf1.parse(parseString(body.get("RYRQ")))))
					.append("|")
					// 住院日期
					.append(sdf.format(sdf1.parse(parseString(body.get("CYRQ")))))
					.append("|")// 出院日期
					.append(parseString(map_jbs.get("JB1"))).append("|")// 出院疾病诊断
					.append(parseString(map_jbs.get("JB2"))).append("|")// 出院疾病次要诊断
					.append(parseString(body.get("BRCH"))).append("|")// 住院床号
					.append(parseString(map_jbs.get("JB3"))).append("|")// 出院疾病诊断3
					.append(parseString(map_jbs.get("JB4"))).append("|")// 出院疾病诊断4
					.append(parseString(map_jbs.get("JB5"))).append("|")// 出院疾病诊断5
					.append(parseString(map_jbs.get("JB6"))).append("|")// 出院疾病诊断6
					.append(parseString(map_jbs.get("JB7"))).append("|")// 出院疾病诊断7
					.append(parseString(map_jbs.get("JB8"))).append("|")// 出院疾病诊断8
					.append(parseString(map_jbs.get("JB9"))).append("|")// 出院疾病诊断9
					.append(parseString(map_jbs.get("JB10"))).append("|")// 出院疾病诊断10
					.append(parseString(map_jbs.get("JB11"))).append("|")// 出院疾病诊断11
					.append(parseString(map_jbs.get("JB12"))).append("|")// 出院疾病诊断12
					.append(parseString(map_jbs.get("JB13"))).append("|")// 出院疾病诊断13
					.append(parseString(map_jbs.get("JB14"))).append("|")// 出院疾病诊断14
					.append(parseString(map_jbs.get("JB15"))).append("|")// 出院疾病诊断15
			;
		} catch (ParseException e) {
			logger.error("入院或出院日期格式错误", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入院或出院日期格式错误");
		}
		map_ret.put("InputStr", inputStr.toString());
		map_ret.put("ItemNumberIndicator", 0);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 更新费用表的费用上传标志
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	
	public void updateFyScbz(List<String> body)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("update ").append(BSPHISEntryNames.ZY_FYMX)
				.append(" set SCBZ=1 where JLXH=:fyxh");
		try {
			for (String fyxh : body) {
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("fyxh", parseLong(fyxh));
				dao.doUpdate(hql.toString(), map_par);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("费用上传更新失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "费用上传更新失败"
							+ e.getMessage());
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-14
	 * @description 医保住院结算数据保存
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	
	public void saveYbZyjs(Map<String, Object> body)
			throws ModelDataOperationException {
		try {
			dao.doSave("create", "phis.application.yb.schemas.YB_ZYJS", body, false);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "医保住院结算保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医保住院结算保存失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-14
	 * @description 查询住院取消结算参数(结算失败)
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryYbZyqxjscs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Map<String, Object> map_body = new HashMap<String, Object>();
		map_body.put("TYPE", 2);
		Map<String, Object> map_ret = getPar(map_body, ctx);
		map_ret.put("TransType", "3200");
		map_ret.put("ProcessingCode", "331230");
		Map<String, Object> ybxx = (Map<String, Object>) body.get("YBXX");
		String jslb = "2";
		StringBuffer inputStr = new StringBuffer();
		inputStr.append(parseString(ybxx.get("SRKH"))).append("|")// 卡号/证号###
				.append(sdf.format(new Date())).append("|")// 操作日期
															// 医院报销传入时间表示：费用发生日期，不是系统日期（针对
															// 16
				// 门诊报销结算类别应该传入系统时间）###***
				.append(jslb).append("|")// 结算类别###
				.append(parseString(body.get("DJH"))).append("|")// 单据号###
				.append(parseString(body.get("MZH"))).append("|")// 门诊流水号###
				.append(getUserName(ctx)).append("|")// 操作员
				.append("").append("|")// 转往医院等级（费用发生医院编码）当结算类别=
										// 6,7,8，9,12，13，16时表示费用发生医院编码
				.append("").append("|")// 出院原因 1、康复
										// 2、转院 3、死亡
										// 9其他
				.append("").append("|")// 审批编号
				.append("").append("|")// 器官移植标志
				.append("").append("|")// 入院诊断
				.append(parseString(body.get("ZXLSH"))).append("|")// 需要被撤销的中心交易流水号
																	// ###
				.append(parseString(body.get("YYLSH"))).append("|")// 需要被撤销的医院交易流水号
																	// ###
				.append("").append("|")// 住院日期
				.append("").append("|")// 出院日期
				.append("").append("|")// 出院疾病诊断
				.append("").append("|")// 出院疾病次要诊断
				.append("").append("|")// 住院床号
				.append("").append("|")// 出院疾病诊断3
				.append("").append("|")// 出院疾病诊断4
				.append("").append("|")// 出院疾病诊断5
				.append("").append("|")// 出院疾病诊断6
				.append("").append("|")// 出院疾病诊断7
				.append("").append("|")// 出院疾病诊断8
				.append("").append("|")// 出院疾病诊断9
				.append("").append("|")// 出院疾病诊断10
				.append("").append("|")// 出院疾病诊断11
				.append("").append("|")// 出院疾病诊断12
				.append("").append("|")// 出院疾病诊断13
				.append("").append("|")// 出院疾病诊断14
				.append("").append("|")// 出院疾病诊断15
		;
		map_ret.put("InputStr", inputStr.toString());
		map_ret.put("ItemNumberIndicator", 0);
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-17
	 * @description 查询住院结算作废参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryYbzyzfcs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			String manaUnitId = UserRoleToken.getCurrent().getManageUnitId();// 用户的机构ID
			long zyh = parseLong(body.get("ZYH"));
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", zyh);
			long count = dao
					.doCount(
							"ZY_BRRY a,ZY_ZYJS b",
							"a.ZYH = b.ZYH and b.ZFPB = 0 and b.JSLX <> 4 and a.ZYH = :ZYH",
							parameters);
			if (count == 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在发票作废病人列表!");
			}
			Map<String, Object> parameters2 = new HashMap<String, Object>();
			parameters2.put("BAHM", body.get("BAHM"));
			parameters2.put("JGID", manaUnitId);
			count = dao.doCount(BSPHISEntryNames.ZY_BRRY,
					"BAHM=:BAHM and JGID=:JGID and cypb<8", parameters2);
			if (count > 0) {
				if (("5".equals(body.get("JSBS") + ""))) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"当前病人已在院,不能进行发票作废!");
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Map<String, Object> map_body = new HashMap<String, Object>();
			map_body.put("TYPE", 2);
			Map<String, Object> map_ret = getPar(map_body, ctx);
			map_ret.put("TransType", "3200");
			map_ret.put("ProcessingCode", "331230");
			Map<String, Object> ybxx = (Map<String, Object>) body.get("YBXX");
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select a.JSRQ as JSRQ,a.YLLB as YLLB,b.ZYLSH as JYXH,a.ZXLSH as ZXLSH,a.YYLSH as YYLSH,a.FPHM as FPHM from YB_ZYJS")
					.append(" a,")
					.append(BSPHISEntryNames.ZY_BRRY)
					.append(" b where a.ZYH=:zyh and a.ZYH=b.ZYH and (a.ZFPB!=1 or a.ZFPB is null)");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("zyh", zyh);
			Map<String, Object> jsxx = dao.doLoad(hql.toString(), map_par);
			String jslb = "2";// 结算类别 现在暂时只知道住院
			// if ("31".equals(parseString(jsxx.get("YLLB")))) {
			// jslb = "3";
			// }
			map_ret.put("ZYH", zyh);
			StringBuffer inputStr = new StringBuffer();
			inputStr.append(parseString(ybxx.get("SRKH"))).append("|")// 卡号/证号###
					.append(sdf.format((Date) jsxx.get("JSRQ"))).append("|")// 操作日期
																			// 医院报销传入时间表示：费用发生日期，不是系统日期（针对
																			// 16
					// 门诊报销结算类别应该传入系统时间）###***
					.append(jslb).append("|")// 结算类别###
					.append(parseString(jsxx.get("FPHM"))).append("|")// 单据号###
					.append(parseString(jsxx.get("JYXH"))).append("|")// 门诊流水号###
					.append(getUserName(ctx)).append("|")// 操作员
					.append("").append("|")// 转往医院等级（费用发生医院编码）当结算类别=
											// 6,7,8，9,12，13，16时表示费用发生医院编码
					.append("").append("|")// 出院原因 1、康复
											// 2、转院 3、死亡
											// 9其他
					.append("").append("|")// 审批编号
					.append("").append("|")// 器官移植标志
					.append("").append("|")// 入院诊断
					.append(parseString(jsxx.get("ZXLSH"))).append("|")// 需要被撤销的中心交易流水号
																		// ###
					.append(parseString(jsxx.get("YYLSH"))).append("|")// 需要被撤销的医院交易流水号
																		// ###
					.append("").append("|")// 住院日期
					.append("").append("|")// 出院日期
					.append("").append("|")// 出院疾病诊断
					.append("").append("|")// 出院疾病次要诊断
					.append("").append("|")// 住院床号
					.append("").append("|")// 出院疾病诊断3
					.append("").append("|")// 出院疾病诊断4
					.append("").append("|")// 出院疾病诊断5
					.append("").append("|")// 出院疾病诊断6
					.append("").append("|")// 出院疾病诊断7
					.append("").append("|")// 出院疾病诊断8
					.append("").append("|")// 出院疾病诊断9
					.append("").append("|")// 出院疾病诊断10
					.append("").append("|")// 出院疾病诊断11
					.append("").append("|")// 出院疾病诊断12
					.append("").append("|")// 出院疾病诊断13
					.append("").append("|")// 出院疾病诊断14
					.append("").append("|")// 出院疾病诊断15
			;
			map_ret.put("InputStr", inputStr.toString());
			map_ret.put("ItemNumberIndicator", 0);
			return map_ret;
		} catch (PersistentDataOperationException e) {
			logger.error("医保住院取消结算出错", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医保住院取消结算出错:"
							+ e.getMessage());
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-17
	 * @description 作废医保住院结算
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	
	public void updateYbzyjsxx(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("update YB_ZYJS")
				.append(" set ZFLSH=:zflsh ,ZFPB=1,ZFRQ=:zfrq,ZFYWZQH=:zfywzqh where ZYH=:zyh and FPHM=:fphm");
		StringBuffer hql_fyqx = new StringBuffer();
		hql_fyqx.append("update ").append(BSPHISEntryNames.ZY_FYMX)
				.append(" set SCBZ=0 where ZYH=:zyh");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zflsh", parseString(body.get("ZFLSH")));
		map_par.put("zfywzqh", parseString(body.get("ZFYWZQH")));
		map_par.put("zfrq", new Date());
		map_par.put("zyh", parseLong(body.get("ZYH")));
		map_par.put("fphm", parseString(body.get("FPHM")));
		Map<String, Object> map_par_fymx = new HashMap<String, Object>();
		map_par_fymx.put("zyh", parseLong(body.get("ZYH")));
		try {
			dao.doUpdate(hql.toString(), map_par);
			dao.doUpdate(hql_fyqx.toString(), map_par_fymx);
		} catch (PersistentDataOperationException e) {
			logger.error("发票作废,本地医保数据保存失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "发票作废,本地医保数据保存失败:"
							+ e.getMessage());
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-8-16
	 * @description 获取参数的通用方法
	 * @updateInfo
	 * @param body
	 *            必须包含TYPE 1是门诊,2是住院 用于查询年业务周期号
	 * 
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getPar(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = new HashMap<String, Object>();
		Map<String, Object> map_yyxx = getYybh(ctx);
		String yybm = MedicineUtils.parseString(map_yyxx.get("YYBM"));
		String ddlsjgbm = MedicineUtils.parseString(map_yyxx.get("DDLSJGBM"));
		int type = MedicineUtils.parseInt(body.get("TYPE"));
		map_ret.put("HosCode", yybm + "|" + ddlsjgbm + "|");// 医院编号(长度4位)|定点连锁机构编码|
		if (body.containsKey("ZHJS")
				&& "1".equals(MedicineUtils.parseString(body.get("ZHJS")))) {
			Map<String, Object> map_doctor = getDoctorUser(ctx, type);
			map_ret.put("OPTRCode", MedicineUtils.parseString(map_doctor.get("USERID")));// 操作员编号(长度8位)
			map_ret.put("BusiCycle", MedicineUtils.parseString(map_doctor.get("YWZQH")));// 业务周期号（最大长度29位）
		} else {
			map_ret.put("OPTRCode", getUserId(ctx));// 操作员编号(长度8位)
			map_ret.put("BusiCycle", getYWZQH(ctx, type));// 业务周期号（最大长度29位）
		}
		map_ret.put("CenterCode", yybm.substring(0, 1));// 医院编码的第一位
		map_ret.put("SenderSerialNo", getYylsh(yybm, ctx));// 医院交易流水号
		// 保险卡号/医疗证号|保险卡密码|市民卡流水号|市民卡认证码| 不知道怎么取 先空着
		map_ret.put("InputStrAcc", "");
		return map_ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-8-15
	 * @description 获取医院编号
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getYybh(Context ctx)
			throws ModelDataOperationException {
		// Map<String, Object> ret = new HashMap<String, Object>();
		if (yyxx != null) {
			return yyxx;
		}
		try {
			UserRoleToken user=UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();// 用户的机构ID
			StringBuffer hql = new StringBuffer();
			hql.append("select YYBM as YYBM,DDLSJGBM as DDLSJGBM,YYMC as YYMC from YB_YYXX where JGID=:jgid and SZYB=1");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("jgid", jgid);
			Map<String, Object> map_yybh;
			map_yybh = dao.doLoad(hql.toString(), map_par);
			if (map_yybh == null || map_yybh.size() == 0
					|| map_yybh.get("YYBM") == null
					|| map_yybh.get("DDLSJGBM") == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR,
						"当前机构医院编号未维护或维护错误,请先维护");
			}
			return map_yybh;
		} catch (PersistentDataOperationException e) {
			logger.error("获取医院信息失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取医院信息失败");
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-10-2
	 * @description 获取医生对应的用户ID和业务周期号(智慧结算用)
	 * @updateInfo
	 * @param ctx
	 * @param type
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getDoctorUser(Context ctx, int type)
			throws ModelDataOperationException {
		if (map_doctor != null) {
			return map_doctor;
		}
		Map<String, Object> map_doctors = new HashMap<String, Object>();
		UserRoleToken user=UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		String doctorid = getYGDM(jgid, ctx, dao);
		if (doctorid.length() > 8) {
			doctorid = doctorid.substring(0, 8);
		}
		map_doctors.put("USERID", doctorid);
		StringBuffer hql = new StringBuffer();// 查询数据库里面保存的业务周期号
		// hql.append("select YWZQ as YWZQ from YB_QDJL where CZGH=:userid and TYPE=:type and ZZBZ=1");
		hql.append("select YWZQ as YWZQ from YB_QDJL where CZGH=:userid and ZZBZ=0 and XTSB=:type");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("userid", doctorid);
		map_par.put("type", type);
		try {
			List<Map<String, Object>> list_ywzqh = dao.doQuery(hql.toString(),
					map_par);
			if (list_ywzqh == null || list_ywzqh.size() == 0) {// 如果数据库里面没有,去签到
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "业务周期号查询失败");
			}
			Map<String, Object> map_ywzqh = list_ywzqh.get(0);
			ywzqh = map_ywzqh.get("YWZQ") + "";
			map_doctors.put("YWZQH", ywzqh);
			return map_doctors;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "业务周期号查询失败");
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-8-15
	 * @description 获取操作员编号 取用户ID 最长8位 过长需截取
	 * @updateInfo
	 * @param ctx
	 * @return
	 */
	public String getUserId(Context ctx) {
		UserRoleToken user=UserRoleToken.getCurrent();
		String userId = user.getUserId();
		if (userId.length() > 8) {
			userId = userId.substring(0, 8);
		}
		return userId;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-8-16
	 * @description 获取业务周期号
	 * @updateInfo
	 * @param ctx
	 * @param type
	 *            类型,1是门诊,2是住院
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getYWZQH(Context ctx, int type)
			throws ModelDataOperationException {
		if (ywzqh != null) {
			return ywzqh;
		}
		String czybh = getUserId(ctx);// 操作员编号
		StringBuffer hql = new StringBuffer();// 查询数据库里面保存的业务周期号
		// hql.append("select YWZQ as YWZQ from YB_QDJL where CZGH=:userid and TYPE=:type and ZZBZ=1");
		hql.append("select YWZQ as YWZQ from YB_QDJL where CZGH=:userid and ZZBZ=0 and XTSB=:type");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("userid", czybh);
		map_par.put("type", type);
		try {
			List<Map<String, Object>> list_ywzqh = dao.doQuery(hql.toString(),
					map_par);
			if (list_ywzqh == null || list_ywzqh.size() == 0) {// 如果数据库里面没有,去签到
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "业务周期号查询失败");
			}
			Map<String, Object> map_ywzqh = list_ywzqh.get(0);
			ywzqh = map_ywzqh.get("YWZQ") + "";
			return ywzqh;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "业务周期号查询失败");
		}

	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-8-15
	 * @description 获取医院交易流水号
	 * @updateInfo
	 * @param yybh
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getYylsh(String yybh, Context ctx)
			throws ModelDataOperationException {
		StringBuffer lsh = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		lsh.append(sdf.format(new Date())).append(yybh)
				.append(RandomStringUtils.random(8, false, true));
		return lsh.toString();
	}
	
	/**
	 * 根据私有参数BZLBFFHJGH获取员工代码
	 * 
	 * @param jgid
	 * @param ctx
	 * @param dao
	 * 
	 * @return 员工代码
	 * @throws ModelDataOperationException
	 */
	public static String getYGDM(String jgid, Context ctx, BaseDAO dao)
			throws ModelDataOperationException {
		try {
			String ygbh = ParameterUtil.getParameter(jgid,
					"BZLBFFHJGH", ctx);
			String cnd = "['and',['eq',['$', 'YGBH'], ['s', '" + ygbh
					+ "']],['eq',['$', 'JGID'],['s', '" + jgid + "']]]";
			Map<String, Object> map = dao.doLoad(CNDHelper.toListCnd(cnd),
					"GY_YGDM");
			if (map == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR,
						"根据私有参数BZLBFFHJGH 未获取到对应的员工代码!");
			} else {
				String ygdm = String.valueOf(map.get("YGDM"));
				return ygdm;
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR,
					"根据私有参数BZLBFFHJGH 未获取到对应的员工代码失败" + e.getMessage());
		}

	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-9-5
	 * @description 获取门诊号
	 * @updateInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getMzh() throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("select YB_MZH.nextval as MZH from dual");
		try {
			List<Map<String, Object>> list_mzh = dao.doSqlQuery(hql.toString(),
					null);
			return "mzzyh" + list_mzh.get(0).get("MZH");
		} catch (PersistentDataOperationException e) {
			logger.error("获取门诊号失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取门诊号失败");
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-9-5
	 * @description 获取挂号的费用上传的处方号
	 * @updateInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getGhCfh() throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("select YB_GHCFH.nextval as CFH from dual");
		try {
			List<Map<String, Object>> list_mzh = dao.doSqlQuery(hql.toString(),
					null);
			return "ghcfh" + list_mzh.get(0).get("CFH");
		} catch (PersistentDataOperationException e) {
			logger.error("获取挂号费用上传处方号失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取挂号费用上传处方号失败");
		}

	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-8-26
	 * @description 获取操作员姓名,最长10位(中文算2位) 过长需截取
	 * @updateInfo
	 * @param ctx
	 * @return
	 */
	public String getUserName(Context ctx) {
		UserRoleToken user=UserRoleToken.getCurrent();
		String userName = user.getUserName();// 操作员姓名
		String chinese = "[\u4e00-\u9fa5]";
		int len = 0;
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < userName.length(); i++) {
			if (len >= 10) {
				break;
			}
			String temp = userName.substring(i, i + 1);
			if (temp.matches(chinese)) {
				if (len >= 9) {
					break;
				}
				ret.append(temp);
				len += 2;
			} else {
				ret.append(temp);
				len += 1;
			}
		}
		return ret.toString();
	}
	public static String parseString(Object o) {
		if (o == null) {
			return "";
		}
		return o + "";
	}
	public static double formatDouble(int number, double data) {
		return BSPHISUtil.getDouble(data,number);
	}
	public static int parseInt(Object o) {
		if (o == null||"".equals(o)) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}
	public static double parseDouble(Object o) {
		if (o == null||"".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
	public static long parseLong(Object o) {
		if (o == null||"".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}
}
