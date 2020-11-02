package phis.application.yb.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class MedicareModel4ZQYB extends MedicareModel{
	public MedicareModel4ZQYB(BaseDAO dao) {
		super(dao);
		// TODO Auto-generated constructor stub
	}
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(MedicareModel4ZQYB.class);


	//读卡相关
	//******************************************************************************
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description 查询数据库有没该医保卡信息
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYBKXX(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		// 查询字段,如有需要别的字段请自行添加
		hql.append("select b.MZHM as MZHM from YB_YBKXX a,MS_BRDA b where a.BRID=b.BRID and a.ICKH=:ickh");
		//hql.append(" ");// 此处加上判断是否存在的条件,唯一确定该卡的条件
		Map<String, Object> map_par = new HashMap<String, Object>();
		// 将参数填充到map_par中
		// ...		
		map_par.put("ickh", body.get("ICKH").toString());
		Map<String, Object> ret = null;
		try {
			ret = dao.doLoad(hql.toString(), map_par);
			if (ret == null || ret.size() == 0) {
				ret = new HashMap<String, Object>();
				ret.put("code", 0);
			} else {
				ret.put("code", 1);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询医保卡信息失败", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description 保存医保卡信息
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveYBKXX(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql_brid = new StringBuffer();// 通过empiid查询Brid
		hql_brid.append("select BRID as BRID,MZHM as MZHM from MS_BRDA where EMPIID=:empiid");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("empiid", MedicineUtils.parseString(body.get("empiId")));
		Map<String, Object> map_brxx=null;
		long brid=0;
		try {
			 map_brxx = dao.doLoad(hql_brid.toString(),
					map_par);
			if (map_brxx == null || map_brxx.size() == 0) {
				throw new ModelDataOperationException("查询病人信息信息失败");
			}
			brid=MedicineUtils.parseLong(map_brxx.get("BRID"));
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询病人信息信息失败", e);
		}
		body.put("BRID", brid);
		// 如果传过来的body格式和scamel不对应 请重新写保存的
		try {
			dao.doSave("create", "phis.application.yb.schemas.YB_YBKXX_SGYB", body,
					false);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "保存医保卡信息失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "保存医保卡信息失败", e);
		}
		return map_brxx;
	}
	//挂号相关
	//************************************************************
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description  查询病人性质是否是医保,并返回具体是什么类型的医保
	 * @updateInfo
	 * @param brxz
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int queryYbbrxz(long brxz)
			throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		hql.append("select XZDL as XZDL from GY_BRXZ where BRXZ=:brxz");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("brxz", brxz);
		Map<String, Object> m;
		try {
			m = dao.doLoad(hql.toString(), map_par);
			if(m!=null&&m.size()>0){
				return MedicineUtils.parseInt(m.get("XZDL"));
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "病人性质是否是医保查询失败", e);
		}
		
		return 0;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description 获取挂号登记,上传,预结算,结算参数
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbGhjscs(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("ghcs", ybghdj(body,ctx));//登记参数
		map_ret.put("sccs", ybghsc(body,ctx));//上传参数
		map_ret.put("yjscs", ybghyjs(body,ctx));
		map_ret.put("jscs", ybghjs(body,ctx));
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description 挂号登记参数查询,返回值类型根据具体需要修改
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> ybghdj(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		//可能用到的参数
				//String ksmc=(Map<String, Object>)body.get("ghxx").get("KSMC")//挂号科室名称
				return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description 挂号上传参数查询,返回值类型根据具体需要修改
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> ybghsc(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		//可能用到的代码段,可参考代码
				/**Map<String, Object> ghxx = (Map<String, Object>) body.get("ghxx");// 挂号的信息
				Map<String, Object> ybkxx = (Map<String, Object>) body.get("ybkxx");// 医保卡的信息
				StringBuffer hql_dzxx = new StringBuffer();// 查询对照信息
				hql_dzxx.append("select  from YB_FYDZ where FYXH=:fyxh");
				StringBuffer hql_xmxx = new StringBuffer();// 查询项目信息,找不到对照信息时用于显示错误.
				hql_xmxx.append("select FYMC as SFMC from GY_YLSF where FYXH=:fyxh");
				long ghfxm = MedicineUtils.parseLong(ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), BSPHISSystemArgument.GHFXM, ctx));//挂号费参数,对应的费用序号.
				double ghje=MedicineUtils.parseDouble(ghxx.get("GHJE"));//挂号金额
				long zjfxm = MedicineUtils.parseLong(ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), BSPHISSystemArgument.ZJFXM, ctx));//专家费参数,对应的专家费序号
				double zjfy=MedicineUtils.parseDouble(ghxx.get("ZJFY"));//专家费用
				long zlfxm = MedicineUtils.parseLong(ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), BSPHISSystemArgument.ZLFXM, ctx));//诊疗费参数,对应的诊疗费序号
				double zlje=MedicineUtils.parseDouble(ghxx.get("ZLJE"));//诊疗金额
				long blfxm = MedicineUtils.parseLong(ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), BSPHISSystemArgument.BLFXM, ctx));//病历费参数,对应的病历费序号
				double blje=MedicineUtils.parseDouble(ghxx.get("BLJE"));//病历金额**/
				return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description 挂号预结算参数查询,返回值类型根据具体需要修改
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> ybghyjs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description 挂号结算参数查询,返回值类型根据具体需要修改
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> ybghjs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-16
	 * @description 保存医保挂号信息
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveYbghxx(Map<String, Object> body)
			throws ModelDataOperationException {
		try {
			dao.doSave("create", "phis.application.yb.schemas.YB_GHJS", body, false);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "保存医保挂号结算信息失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "保存医保挂号结算信息失败", e);
		}
		
	}
//退号相关
	//**********************************************************************
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-21
	 * @description  医保挂号退号参数查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryYbThcs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		long sbxh=MedicineUtils.parseLong(body.get("SBXH"));//挂号表的主键
		Map<String,Object> ybkxx=(Map<String,Object>)body.get("ybkxx");//界面读出来的医保卡信息
		StringBuffer hql=new StringBuffer();
		hql.append("select a.SBXH as SBXH from YB_GHJS a,MS_GHMX b where a.GHGL=b.SBXH and b.SBXH=:sbxh");//查询出挂号的相关信息,具体字段根据需要填写
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("sbxh", sbxh);
		try {
			Map<String,Object> map_ghxx=dao.doLoad(hql.toString(), map_par);
			//以下代码根据医保参数需要拼出参数
			//....
			map_ret.put("SBXH", MedicineUtils.parseLong(map_ghxx.get("SBXH")));
			map_ret.put("ybthcs", null);//存放退号参数
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "获取医保挂号退号参数失败", e);
		}
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-22
	 * @description 医保挂号退号保存
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveYbGhth(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		hql.append("update YB_GHJS set ZFPB=1 where SBXH=:sbxh");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("sbxh", MedicineUtils.parseLong(body.get("SBXH")));
		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医保挂号本地作废失败", e);
		}
		
	}
//门诊收费相关
//*************************************
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-27
	 * @description 根据读卡信息查询病人的MZHM
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryOutpatientAssociation(
			Map<String, Object> body) throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer();//查询Mzhm
		hql.append("select a.MZHM as MZGL from MS_BRDA a,YB_YBKXX b where a.BRID=b.BRID and b.**=:key");//根据卡信息的唯一键确定病人
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("key", body.get(""));
		try {
			map_ret=dao.doLoad(hql.toString(), map_par);
			if(map_ret==null||map_ret.size()==0){
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "未查到病人信息");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询病人信息失败", e);
		}
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-27
	 * @description 获取门诊收费登记,上传,预结算,结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbMzjscs(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("ybdjcs", YbMzdj(body));
		map_ret.put("ybsccs", YbMzsc(body));
		map_ret.put("ybyjscs", YbMzyjs(body));
		map_ret.put("ybjscs", YbMzjs(body));
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-27
	 * @description 医保结算成功,本地结算失败 用于查询取消结算的参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryMzqxjscs(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-27
	 * @description 获取门诊收费登记参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> YbMzdj(Map<String, Object> body)
			throws ModelDataOperationException {
		return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-27
	 * @description 获取门诊收费上传参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> YbMzsc(Map<String, Object> body)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_cfmx = (List<Map<String, Object>>) body
				.get("CFMX");
		StringBuffer hql_cfxx = new StringBuffer();// 查询处方其他信息,如频次 医生姓名等
		hql_cfxx.append(
				"select a.YSDM as YSDM,b.GYTJ as YPYF,b.YFDW as YFDW,a.KSDM as KSDM,b.YYTS as ZXTS,c.XMMC as YF,d.MRCS/d.ZXZQ as PC from MS_CF01  a,MS_CF02 b,ZY_YPYF c,GY_SYPC  d where a.CFSB=b.CFSB  and b.SBXH=:sbxh and b.GYTJ=c.YPYF and b.YPYF=d.PCBM");
		StringBuffer hql_cfjb = new StringBuffer();// 通过处方查出疾病
		hql_cfjb.append("select c.ICD10 as ICD10 from MS_CF01  a,MS_CF02  b,MS_BRZD  c  where a.CFSB=b.CFSB  and b.SBXH=:sbxh and a.JZXH=c.JZXH");
		StringBuffer hql_yjxx = new StringBuffer();// 查询医技其他信息,如频次 医生姓名等
		hql_yjxx.append(
				"select a.YSDM as YSDM,'' as YPYF,'' as YFDW,a.KSDM as KSDM,'' as ZXTS from MS_YJ01  a,MS_YJ02  b where a.YJXH=b.YJXH and b.SBXH=:sbxh");
		StringBuffer hql_yjjb = new StringBuffer();// 通过医技查出疾病
		hql_yjjb.append("select c.ICD10 as ICD10 from MS_YJ01 a,MS_YJ02 b,MS_BRZD c where a.YJXH=b.YJXH and b.SBXH=:sbxh and a.JZXH=c.JZXH");
		StringBuffer hql_xmdzxx = new StringBuffer();// 查询项目对照信息
		hql_xmdzxx
				.append("select from YB_FYDZ ");
		StringBuffer hql_ypdzxx = new StringBuffer();// 查询药品对照信息
		hql_ypdzxx
				.append("select  from YB_YPDZ ");
		StringBuffer hql_xmxx = new StringBuffer();// 查询项目信息,找不到对照信息时用于显示错误.
		hql_xmxx.append("select FYMC as SFMC from GY_YLSF where FYXH=:fyxh");
		StringBuffer hql_ypxx = new StringBuffer();// 查询项目信息,找不到对照信息时用于显示错误.
		hql_ypxx.append("select YPMC as YPMC from YK_TYPK where YPXH=:fyxh");
		Map<String, Object> map_dzxx = new HashMap<String, Object>();//存放药品或者项目的对照信息
		try{
		for (Map<String, Object> map_cfmx : list_cfmx) {//项目
			if (MedicineUtils.parseInt(map_cfmx.get("CFLX")) == 4
					|| MedicineUtils.parseInt(map_cfmx.get("CFLX")) == 0) {
				Map<String, Object> map_par_dzxx = new HashMap<String, Object>();
				map_par_dzxx.put("fyxh", MedicineUtils.parseLong(map_cfmx.get("YPXH")));
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
			} else {//药品
				Map<String, Object> map_par_dzxx = new HashMap<String, Object>();
				map_par_dzxx.put("fyxh", MedicineUtils.parseLong(map_cfmx.get("YPXH")));
				map_par_dzxx.put("ypcd", MedicineUtils.parseLong(map_cfmx.get("YPCD")));
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
			double ypsl = MedicineUtils.parseDouble(map_cfmx.get("YPSL"));// 药品数量
			//double ypjg = MedicineUtils.parseDouble(map_cfmx.get("YPDJ"));// 药品单价
			double mcyl = 1;// 每次用量
			if (MedicineUtils.parseInt(map_cfmx.get("CFLX")) == 3) {//草药的数量要乘以帖数
				ypsl = MedicineUtils.formatDouble(2,
						ypsl * MedicineUtils.parseDouble(map_cfmx.get("CFTS")));
				mcyl = MedicineUtils.parseDouble(map_cfmx.get("CFTS"));
			}
			double ypje =MedicineUtils.parseDouble(map_cfmx.get("HJJE"));//防止由于计算出错导致的差0.0几的问题,结算里面也从明细的HJJE取的
			String sypc = "1";// 使用频次
			String yf = "1";// 用法
			Map<String, Object> map_qtxx = new HashMap<String, Object>();//存放其他信息,包括频次,医生,科室等
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("sbxh", MedicineUtils.parseLong(map_cfmx.get("SBXH")));
			if (MedicineUtils.parseInt(map_cfmx.get("CFLX")) != 4
					&& MedicineUtils.parseInt(map_cfmx.get("CFLX")) != 0) {
				map_qtxx = dao.doLoad(hql_cfxx.toString(), map_par);
				if (map_qtxx == null || map_qtxx.size() == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"有处方明细未维护用法或者频次,请检查处方单:");
				}
				yf = MedicineUtils.parseString(map_qtxx.get("YF"));
				sypc = MedicineUtils.parseString(map_qtxx.get("PC"));
				List<Map<String, Object>> list_cfjb = dao.doQuery(
						hql_cfjb.toString(), map_par);
				if (list_cfjb != null && list_cfjb.size() > 0) {
					//查询出疾病,如果需要再处理
				}
			} else {
				map_qtxx = dao.doLoad(hql_yjxx.toString(), map_par);
				List<Map<String, Object>> list_yjjb = dao.doQuery(
						hql_yjjb.toString(), map_par);
				if (list_yjjb != null && list_yjjb.size() > 0) {
					for (Map<String, Object> map_yjjb : list_yjjb) {
						//查询出疾病,如果需要再处理
					}
				}
			}
			map_qtxx.put("KSMC", "科别名称");
			map_qtxx.put("YSXM", "医生");
			if (map_qtxx != null && MedicineUtils.parseLong(map_qtxx.get("KSDM")) != 0) {
				Map<String, Object> map_ksmc = dao.doLoad(
						BSPHISEntryNames.SYS_Office,
						MedicineUtils.parseLong(map_qtxx.get("KSDM")));
				if (map_ksmc != null) {
					map_qtxx.put("KSMC", MedicineUtils.parseString(map_ksmc.get("KSMC")));
				}
			}
			if (map_qtxx != null && MedicineUtils.parseLong(map_qtxx.get("YSDM")) != 0) {
				Map<String, Object> map_ksmc = dao.doLoad(
						BSPHISEntryNames.SYS_Personnel,
						MedicineUtils.parseString(map_qtxx.get("YSDM")));
				if (map_ksmc != null) {
					map_qtxx.put("YSXM",
							MedicineUtils.parseString(map_ksmc.get("userName")));
				}
			}
			//拼接医保需要的参数
			//....
		}
		}catch(PersistentDataOperationException e){
			MedicineUtils.throwsException(logger, "医保上传参数查询失败", e);
		}
		return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-27
	 * @description 获取门诊收费预结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> YbMzyjs(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-27
	 * @description 获取门诊收费结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> YbMzjs(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-27
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
	 * @createDate 2015-7-30
	 * @description 发票作废医保参数查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbFpzfcs(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		//查询参数前先判断下发票能不能作废.防止医保作废成功了 但是本地不能作废. 该判断以ClinicChargesProcessingModel.doUpdateVoidInvoice为准
		String fphm = body.get("FPHM") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FPHM", fphm);
		parameters.put("JGID", manaUnitId);
		Map<String, Object> MZXX;
		try {
			MZXX = dao
					.doLoad("select MZXH as MZXH,ZFPB as ZFPB,SFRQ as SFRQ ,BRXM as BRXM from "
							+ "MS_MZXX where FPHM=:FPHM and JGID=:JGID", parameters);
			if (1 == Integer.parseInt(MZXX.get("ZFPB") + "")) {
				throw new ModelDataOperationException("该发票号码已作废!");
			}
			parameters.remove("FPHM");
			parameters.put("MZXH", MZXX.get("MZXH"));
			long ll_found = dao.doCount("MS_YJ01",
					"JGID=:JGID and MZXH=:MZXH and ZXPB>=1", parameters);
			if (ll_found > 0) {
				throw new ModelDataOperationException("该发票中的项目已执行过,不能作废!");
			}
			ll_found = 0;
			ll_found = dao.doCount("MS_CF01",
					"JGID=:JGID and MZXH=:MZXH and FYBZ = 1", parameters);
			if (ll_found > 0) {
				throw new ModelDataOperationException("该发票中的药品已发出,不能作废!");
			}
		//查询医保取消参数代码
			//...
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "判断能否作废失败", e);
		}
		
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-29
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
	 * @createDate 2015-8-13
	 * @description 获取医保入院登记参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbRydjcs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-14
	 * @description  查询医保住院病人性质转换参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbZyxzzhcs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		long zyh = MedicineUtils.parseLong(body.get("ZYH"));
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("code", 200);
		ret.put("msg", "ok");
		try {
			if (!body.containsKey("ZH")) {// 如果是注销
				Map<String, Object> BRXX = dao.doLoad(BSPHISEntryNames.ZY_BRRY,
						zyh);
				if (MedicineUtils.parseInt(BRXX.get("CYPB")) == 1) {
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
						if (MedicineUtils.parseDouble(JKJE.get("JKJE")) != 0) {
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
				count = MedicineUtils.parseLong(ZY_FYMX.get("COUNT"));
				double zjje = 0;
				if (ZY_FYMX.get("ZJJE") != null) {
					zjje = MedicineUtils.parseDouble(ZY_FYMX.get("ZJJE"));
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
			//将需要的参数放到ret中
			//...
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
	 * @createDate 2015-8-14
	 * @description 医保性质转成成功,更新入院表
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void updateRydj(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		if(body.containsKey("ZFZYB")){//自费转医保
			//更新入院表医保字段,赋值
			//...
		}else if(body.containsKey("YBZZF")){//医保转自费
			//更新入院表医保字段,赋空
			//...
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-17
	 * @description 据医保卡信息查询住院号码
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String queryZyhmByYbkxx(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		hql.append("select a.ZYHM as ZYHM from ZY_BRRY a,YB_YBKXX b where a.BRID=b.BRID and a.CYPB<8 and b. =:key");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("key", "");
		String zyhm="";
		try {
			Map<String,Object> map=dao.doLoad(hql.toString(), map_par);
			if(map!=null&&map.size()>0){
				zyhm=MedicineUtils.parseString(map.get("ZYHM"))	;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("住院号码查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "住院号码查询失败:"
							+ e.getMessage());
		}
		return zyhm;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-18
	 * @description  获取医保费用上传,住院预结算,住院结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbZyjscs(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("YBSCCS", queryYbZysccs(body));//上传参数
		map_ret.put("YBYJSCS", queryYbZyyjscs(body));//预结算参数
		map_ret.put("YBJSCS", queryZyjscs(body));//结算参数
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-18
	 * @description 查询住院上传数据
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbZysccs(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();
		long zyh = MedicineUtils.parseLong(body.get("ZYH"));
		StringBuffer hql = new StringBuffer();// 查询费用明细
		hql.append(
				"select  a.ZJJE as ZJJE,a.JLXH as JLXH,a.YPCD as YPCD,a.YPLX as YPLX,a.FYXM as FYXM,a.YZXH as YZXH,a.FYRQ as FYRQ,a.FYXH as FYXH,a.FYMC as FYMC,a.FYDJ as FYDJ,a.FYSL as FYSL,a.YSGH as YSGH,a.FYKS as FYKS from ZY_FYMX a where  a.ZYH=:zyh and (SCBZ = 0 or SCBZ is null) ");
		StringBuffer hql_ysxm = new StringBuffer();// 查询医生姓名
		hql_ysxm.append("select PERSONNAME as YSXM from SYS_Personnel where PERSONID=:ysgh");
		StringBuffer hql_ksmc = new StringBuffer();// 查询科室名称
		hql_ksmc.append("select OFFICENAME as KSMC from SYS_Office  where ID=:ksdm");
		StringBuffer hql_xmdzxx = new StringBuffer();// 查询项目对照信息
		hql_xmdzxx.append("select ... from YB_FYDZ where ...");
		StringBuffer hql_ypdzxx = new StringBuffer();// 查询药品对照信息
		hql_ypdzxx.append("select ... from YB_YPDZ where ...");
		StringBuffer hql_xmxx = new StringBuffer();// 查询项目信息,找不到对照信息时用于显示错误.
		hql_xmxx.append("select FYMC as SFMC from GY_YLSF where FYXH=:fyxh");
		StringBuffer hql_ypxx = new StringBuffer();// 查询项目信息,找不到对照信息时用于显示错误.
		hql_ypxx.append("select YPMC as YPMC from YK_TYPK where YPXH=:fyxh");
		StringBuffer hql_yzxx = new StringBuffer();// 查询医嘱信息,如频次,剂型 等
		hql_yzxx.append(
				"select a.ZFPB as ZFPB,a.YCSL as YCSL,b.XMMC as YF,c.MRCS/c.ZXZQ as PC,a.YPLX as YPLX from ZY_BQYZ a,ZY_YPYF  b,GY_SYPC  c where a.JLXH=:yzxh and a.YPYF=b.YPYF and a.SYPC=c.PCBM");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("zyh", zyh);
			List<Map<String, Object>> list_fymx = dao.doQuery(hql.toString(),
					map_par);
			if (list_fymx == null || list_fymx.size() == 0) {
				return map_ret;
			}
			for (Map<String, Object> map_fymx : list_fymx) {
				Map<String, Object> map_dzxx = new HashMap<String, Object>();//对照的信息
				if (MedicineUtils.parseInt(map_fymx.get("YPLX")) == 0) {
					Map<String, Object> map_par_dzxx = new HashMap<String, Object>();
					map_par_dzxx.put("fyxh", MedicineUtils.parseLong(map_fymx.get("FYXH")));
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
				} else {
					Map<String, Object> map_par_dzxx = new HashMap<String, Object>();
					map_par_dzxx.put("fyxh", MedicineUtils.parseLong(map_fymx.get("FYXH")));
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
				double ypsl = MedicineUtils.parseDouble(map_fymx.get("FYSL"));// 药品数量
				double ypjg = MedicineUtils.parseDouble(map_fymx.get("FYDJ"));// 药品单价
				double ypje =MedicineUtils.parseDouble(map_fymx.get("ZJJE"));//防止计算导致的差0.0几的问题,直接从ZJJE取
				String sypc = "1";// 使用频次
				String yf = "1";// 用法
				String mcyl = "1";// 每次用量
				if (MedicineUtils.parseLong(map_fymx.get("YZXH")) != 0) {
					Map<String, Object> map_par_yzxx = new HashMap<String, Object>();
					map_par_yzxx.put("yzxh", MedicineUtils.parseLong(map_fymx.get("YZXH")));
					Map<String, Object> map_yzxx = dao.doLoad(
							hql_yzxx.toString(), map_par_yzxx);
					if (map_yzxx != null) {
						sypc = MedicineUtils.parseString(map_yzxx.get("PC"));
						yf = MedicineUtils.parseString(map_yzxx.get("YF"));
						if (MedicineUtils.parseInt(map_yzxx.get("YPLX")) == 3) {
							mcyl = "7";//草药每次用量默认为7 这个不知道怎么取 根据实际情况赋值(本地没有改字段)
						} else {
							mcyl = MedicineUtils.parseString(map_yzxx.get("YCSL"));
						}
						map_fymx.put("ZFPB", MedicineUtils.parseString(map_yzxx.get("ZFPB")));
					}
				}
				
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
					map_par_ksmc.put("ksdm", MedicineUtils.parseLong(map_fymx.get("FYKS")));
					Map<String, Object> map_ksmc = dao.doLoad(
							hql_ksmc.toString(), map_par_ksmc);
					if (map_ksmc != null && map_ksmc.get("KSMC") != null) {
						ksmc = map_ksmc.get("KSMC") + "";
					}
				}
				//注: 一般用到的处方号 用map_fymx.get("JLXH"),
			}
			
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医保结算参数查询失败", e);
		}
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-18
	 * @description 查询住院预结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbZyyjscs(Map<String, Object> body)
			throws ModelDataOperationException {
		return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-18
	 * @description 查询住院结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryZyjscs(Map<String, Object> body)
			throws ModelDataOperationException {
		return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-18
	 * @description 更新上传标志(可选)
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void updateFyScbz(Map<String, Object> body)
			throws ModelDataOperationException {
		
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-18
	 * @description 保存医保结算数据
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
	 * @createDate 2015-8-18
	 * @description 查询医保住院取消结算参数(结算失败)
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbZyqxjscs(Map<String, Object> body)
			throws ModelDataOperationException {
		return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-18
	 * @description 查询医保住院取消结算参数
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryYbzyzfcs(Map<String, Object> body)
			throws ModelDataOperationException {
		return null;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-18
	 * @description 取消结算成功,更新本地表
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void updateYbzyjsxx(Map<String, Object> body)
			throws ModelDataOperationException {
		long zyh=MedicineUtils.parseLong(body.get("zyh"));
		StringBuffer hql=new StringBuffer();
		hql.append("update YB_ZYJS set ZFPB=1 where ZYH=:zyh");
		//如果费用表有上传标志,还要更新上传标志
		//...
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("zyh", zyh);
		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医保本地作废失败", e);
		}
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-19
	 * @description 保存医保药品对照
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveYbypdz(List<Map<String, Object>> body)
			throws ModelDataOperationException {
		StringBuffer hql_delete=new StringBuffer();//删除所有需要更新的对照信息
		hql_delete.append("delete from YB_YPDZ where YPXH in (:ypxhs)");
		List<Long> list_ypxhs=new ArrayList<Long>();//存放需要删除的YPXH的对照记录
		List<Map<String,Object>> list_save=new ArrayList<Map<String,Object>>();//用于存放需要保存的记录
		for(Map<String,Object> map:body){
			list_ypxhs.add(MedicineUtils.parseLong(map.get("YPXH")));
			if(map.get("YBYPBM")!=null&&!"".equals(MedicineUtils.parseString(map.get("YBYPBM")))){
				Map<String,Object> map_temp=new HashMap<String,Object>();
				map_temp.putAll(map);
				list_save.add(map_temp);
			}
		}
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("ypxhs", list_ypxhs);
		try {
			dao.doUpdate(hql_delete.toString(), map_par);
			for(Map<String,Object> map:list_save){
				dao.doSave("create", "phis.application.yb.schemas.YB_YPDZ", map, false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医保药品对照保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "医保药品对照保存失败", e);
		}
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-19
	 * @description 医保药品对照,左边list数据查询
	 * @updateInfo
	 * @param cnd
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void queryYbypdz(List<?> cnd,Map<String, Object> req,Map<String, Object> res)
			throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		hql.append("select a.YPXH as YPXH, a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,a.PYDM as PYDM,b.YBYPBM as YBYPBM,b.YBYPMC as YBYPMC from YK_TYPK a left outer join YB_YPDZ b on a.YPXH=b.YPXH ");
		try {
		if(cnd!=null){
		hql.append(" where ").append(ExpressionProcessor.instance().toString(cnd));
		}
		MedicineCommonModel com=new MedicineCommonModel(dao);
		Map<String,Object> map_par=new HashMap<String,Object>();
		//如果有字典要转换的,最后一个参数传sc名(全路径)
		res.putAll(com.getPageInfoRecord(req, map_par, hql.toString(), null));
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "表达式有错误", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-19
	 * @description 保存医保费用对照
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveYbfydz(List<Map<String, Object>> body)
			throws ModelDataOperationException {
		StringBuffer hql_delete=new StringBuffer();//删除所有需要更新的对照信息
		hql_delete.append("delete from YB_FYDZ where FYXH in (:fyxhs)");
		List<Long> list_fyxhs=new ArrayList<Long>();//存放需要删除的FYXH的对照记录
		List<Map<String,Object>> list_save=new ArrayList<Map<String,Object>>();//用于存放需要保存的记录
		for(Map<String,Object> map:body){
			list_fyxhs.add(MedicineUtils.parseLong(map.get("FYXH")));
			if(map.get("YBFYBM")!=null&&!"".equals(MedicineUtils.parseString(map.get("YBFYBM")))){
				Map<String,Object> map_temp=new HashMap<String,Object>();
				map_temp.putAll(map);
				list_save.add(map_temp);
			}
		}
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("fyxhs", list_fyxhs);
		try {
			dao.doUpdate(hql_delete.toString(), map_par);
			for(Map<String,Object> map:list_save){
				dao.doSave("create", "phis.application.yb.schemas.YB_FYDZ", map, false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医保费用对照保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "医保费用对照保存失败", e);
		}
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-8-19
	 * @description  医保费用对照,左边list数据查询
	 * @updateInfo
	 * @param cnd
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void queryYbfydz(List<?> cnd,Map<String, Object> req,Map<String, Object> res)
			throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		hql.append("select a.FYXH as FYXH,a.FYMC as FYMC,a.FYDW as FYDW,a.PYDM as PYDM,b.YBFYBM as YBFYBM,b.YBFYMC as YBFYMC from GY_YLSF a left outer join YB_FYDZ b on a.FYXH=b.FYXH ");
		try {
		if(cnd!=null){
		hql.append(" where ").append(ExpressionProcessor.instance().toString(cnd));
		}
		MedicineCommonModel com=new MedicineCommonModel(dao);
		Map<String,Object> map_par=new HashMap<String,Object>();
		//如果有字典要转换的,最后一个参数传sc名(全路径)
		res.putAll(com.getPageInfoRecord(req, map_par, hql.toString(), null));
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "表达式有错误", e);
		}
	}
}
