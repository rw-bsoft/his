package phis.source.schedule;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @CopyRight：版权所有bsoft,保留所有权力。
 * @Date:OtherSchedule.java created on Apr 10, 2010 2:43:40 PM
 * @Author:<a href="mailto:yangwf@bsoft.com.cn">weifeng yang</a>
 * @Description:
 * @Modify:
 */
public class BedCostCalculationSchedule extends AbstractJobSchedule {

	@Override
	public void doJob(BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
//		System.out.println("定时任务执行");
//		String TopUnitId = ParameterUtil.getTopUnitId();
//		Dictionary manage = DictionaryController.instance().getDic("phis.@manageUnit");
		String TopUnitId = ParameterUtil.getTopUnitId();
		System.out.println(TopUnitId);
		String CWFZDLJ = ParameterUtil.getParameter(TopUnitId, "CWFZDLJ", ctx);
		System.out.println("CWFZDLJ:"+CWFZDLJ);
		if("1".equals(CWFZDLJ)){
			uf_fylj(dao, ctx);
		}
	}
	
/***********************************费用累加**********************************/
//	public static String getTopUnitId(){
//		Dictionary manage = DictionaryController.instance().getDic("phis.@manageUnit");
//		String TopUnitId = manage.getSlice("", 3, null).get(0).getKey();
//		return TopUnitId;
//	}
	
	public static Map<String, Object> istr_cwfy = new HashMap<String, Object>();
	public static Map<String, Object> istr_fymx = new HashMap<String, Object>();
	public static Map<String, Object> istr_brxx = new HashMap<String, Object>();
	public static Map<String, Object> istr_cwfyxx = new HashMap<String, Object>();
	public static boolean ib_tsfy = false;
	public static boolean ib_GetCWFYXX = false;
	
	/**
	 * 说明：费用累加
	 * @param dao
	 * @param ctx
	 * @return
	 */
	public static boolean uf_fylj(BaseDAO dao, Context ctx) {
		istr_cwfy = new HashMap<String, Object>();
		istr_fymx = new HashMap<String, Object>();
		istr_brxx = new HashMap<String, Object>();
		istr_cwfyxx = new HashMap<String, Object>();
		ib_tsfy = false;
		ib_GetCWFYXX = false;
		Session ss = null;
		try {
//			User user = (User) ctx.get("user.instance");
//			String JGID = user.get("manageUnit.id");// 用户的机构ID
			String TopUnitId = ParameterUtil.getTopUnitId();
			if (!fylj_verify(dao, ctx)) {
//				System.out.println("发生错误");
				ss = (Session) ctx.get(Context.DB_SESSION);
				ss.beginTransaction();
				gf_insert_htrz(TopUnitId,
						"系统选项中床位费、自负床位费、诊疗费序号、ICU费序号或自动累加费用设置有误，请检查修正，否则自动累加费用将发生错误！",
						2, dao, ctx);
				ss.getTransaction().commit();
				return false;
			}
			
//			Map<String, Object> jg_param = new HashMap<String, Object>();
//			jg_param.put("CSMC", "CWFZDLJ");
//			String hql = "select JGID as JGID from GY_XTCS where CSMC like :CSMC";
//			Map<String, Object> csz = dao.doQuery(hql, BSPHISEntryNames.GY_XTCS);
//			if(csz != null){ 
//				jgid = csz.get("JGID")+"";
//			}
			
			Date ldt_ljzzrq = new Date();
			// 累加病人表单检索数据，参数：ldt_ljzzrq;
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("adt_CurDate", ldt_ljzzrq);
//			parameters.put("al_jgid", JGID);
			List<Map<String, Object>> ZY_BRRYs = dao
					.doQuery("SELECT a.ZYH as ZYH,a.ZYHM as ZYHM,a.BRXM as BRXM,a.BRCH as BRCH,a.JGID as JGID FROM ZY_BRRY a WHERE a.CYPB = 0 or a.CYPB = 1 and a.CYRQ >= :adt_CurDate",//and a.JGID = :al_jgid
							parameters);
			int ll_rowcount = ZY_BRRYs.size();
			if (ll_rowcount == 0) {
				return false;
			}
			// 遍历累加病人表单，ll_row是遍历序号
			for (int i = 0; i < ZY_BRRYs.size(); i++) {
//				System.out.println("for("+i+")");
				Map<String, Object> am_ZY_BRRY = ZY_BRRYs.get(i);
				long ll_zyh = Long.parseLong(am_ZY_BRRY.get("ZYH") + "");
				// String ls_zyhm = ZY_BRRY.get("ZYHM") + "";
				// String ls_brxm = ZY_BRRY.get("BRXM") + "";
				// String ls_brch = ZY_BRRY.get("BRCH") + "";
				
//				SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
//				"mySessionFactory");
				// 　　　事务开始
//				ss = sf.openSession();
				ss = (Session) ctx.get(Context.DB_SESSION);
				ss.beginTransaction();
				if (fylj(ll_zyh, ldt_ljzzrq, false, dao, ctx)) {
					// 提交事务
					
					ss.getTransaction().commit();
				} else {
					// 　　　回滚事务
					ss.getTransaction().rollback();
//					System.out.println("机构发生错误");
//					ss.getTransaction().
					ss = (Session) ctx.get(Context.DB_SESSION);
					ss.beginTransaction();
					gf_insert_htrz(am_ZY_BRRY.get("JGID")+"",
							"系统选项中床位费、自负床位费、诊疗费序号、ICU费序号或自动累加费用设置有误，请检查修正，否则自动累加费用将发生错误！",
							2, dao, ctx);
					ss.getTransaction().commit();
					return false;
				}
			}
			ss = (Session) ctx.get(Context.DB_SESSION);
			ss.beginTransaction();
			gf_insert_htrz(TopUnitId,"费用累加成功!", 1, dao, ctx);
			ss.getTransaction().commit();
			return true;
		} catch (PersistentDataOperationException e) {
			ss.getTransaction().rollback();
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 说明：向后台日志表 GY_CSRZ中插入操作日志记录
	 * 
	 * @param as_rzxx
	 *            日志信息
	 * @param ai_rzlx
	 *            日志类型 1:成功 2:失败 3:无数据
	 */
	public static void gf_insert_htrz(String jgid,String as_rzxx, int ai_rzlx, BaseDAO dao,
			Context ctx) throws PersistentDataOperationException{
		Date ldt_rzsj = new Date();
//		Session ss = null;
		try {
			Map<String, Object> am_GY_CSRZ = new HashMap<String, Object>();
			am_GY_CSRZ.put("RZSJ", ldt_rzsj);
			am_GY_CSRZ.put("RZLX", ai_rzlx);
			am_GY_CSRZ.put("RZXX", as_rzxx);
			am_GY_CSRZ.put("JGID", jgid);
//			ss = (Session) ctx.get(Context.DB_SESSION);
//			ss.beginTransaction();
			dao.doSave("create", BSPHISEntryNames.GY_CSRZ, am_GY_CSRZ, false);
//			ss.getTransaction().commit();
		}catch (ValidateException e) {
//			ss.getTransaction().rollback();
			e.printStackTrace();
		}
	}

	/**
	 * 说明：判断各项累加费用序号及费用归并是否正确
	 * 
	 * @return
	 */
	public static boolean fylj_verify(BaseDAO dao, Context ctx) {
//		User user = (User) ctx.get("user.instance");
//		String JGID = user.get("manageUnit.id");// 用户的机构ID
		String[] ls_ljfy = { "CWFXH", "ZFCWF", "ZLFXH" };//"ICUXH"
		try {
			for (int i = 0; i < ls_ljfy.length; i++) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ls_ljfy", ls_ljfy[i]);
				String TopUnitId = ParameterUtil.getTopUnitId();
				String ls_Value = ParameterUtil.getParameter(TopUnitId, ls_ljfy[i],
						ctx);
				// dao.doQuery("SELECT CSZ as ls_Value FROM GY_XTCS WHERE CSMC = :ls_ljfy",
				// parameters) ;
				if(!(ls_Value.length()>0)){
					return false;
				}
				long ll_fyxh = Long.parseLong(ls_Value);
				if (!(ll_fyxh > 0)) {
					return false;
				}
				Map<String, Object> fyxhParameters = new HashMap<String, Object>();
				fyxhParameters.put("ll_fyxh", ll_fyxh);
				int ll_fygb = Integer
						.parseInt(dao
								.doQuery(
										"SELECT FYGB as FYGB FROM GY_YLSF WHERE FYXH = :ll_fyxh AND ZYSY = 1",
										fyxhParameters).get(0).get("FYGB")
								+ "");
				// ll_fygb = 0
				// SELECT FYGB INTO :ll_fygb FROM GY_YLSF WHERE FYXH = :ll_fyxh
				// AND ZYSY = 1 ;
				if (!(ll_fygb > 0)) {
					return false;
				}
				Map<String, Object> sfxmParameters = new HashMap<String, Object>();
				sfxmParameters.put("ll_fygb", Long.parseLong(ll_fygb+""));//2013.11.20
				int ll_Count = Integer
						.parseInt(dao
								.doLoad("SELECT COUNT(*) as COUNT FROM GY_SFXM WHERE SFXM = :ll_fygb AND ZYSY = 1",
										sfxmParameters).get("COUNT")
								+ "");
				if (!(ll_Count > 0)) {
					return false;
				}
			}
//			Map<String, Object> parameters = new HashMap<String, Object>();
//			parameters.put("al_jgid", JGID);
			List<Map<String, Object>> lds_ljfy = dao
					.doQuery(
							"SELECT a.FYXH as FYXH,b.FYMC as FYMC,b.FYDW as FYDW,a.FYDJ as FYDJ,a.KSRQ as KSRQ,a.JSRQ as JSRQ,a.LJFS as LJFS,a.XZFS as XZFS FROM ZY_LJFY a,GY_YLSF b WHERE a.FYXH = b.FYXH",// and a.JGID = :al_jgid
							null);
			for (int i = 0; i < lds_ljfy.size(); i++) {
				Map<String, Object> ljfy = lds_ljfy.get(i);
				long al_FYXH = Long.parseLong(ljfy.get("FYXH") + "");
				if (!(al_FYXH > 0)) {
					return false;
				}
				Map<String, Object> fyxhParameters = new HashMap<String, Object>();
				fyxhParameters.put("ll_fyxh", al_FYXH);
				int ll_fygb = Integer
						.parseInt(dao
								.doLoad("SELECT a.FYGB as FYGB FROM GY_YLSF a WHERE FYXH = :ll_fyxh AND ZYSY = 1",
										fyxhParameters).get("FYGB")
								+ "");
				if (!(ll_fygb > 0)) {
					return false;
				}
				Map<String, Object> fygbParameters = new HashMap<String, Object>();
				fygbParameters.put("ll_fygb", ll_fygb);
				int ll_Count = Integer
						.parseInt(dao
								.doLoad("SELECT COUNT(*) as COUNT FROM GY_SFXM WHERE SFXM = :ll_fygb AND ZYSY = 1",
										fygbParameters).get("COUNT")
								+ "");
				if (!(ll_Count > 0)) {
					return false;
				}
			}
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 说明：累加病人连续费用
	 * 
	 * @param al_zyh
	 * @param adt_enddate
	 * @param ab_autoadjust
	 * @return
	 */
	public static boolean fylj(long al_zyh, Date adt_enddate,
			boolean ab_autoadjust, BaseDAO dao, Context ctx) throws PersistentDataOperationException{
		String ls_czgh = null;
		return fylj(al_zyh, adt_enddate, ab_autoadjust, ls_czgh, dao, ctx);
	}

	/**
	 * 说明：累加病人连续费用(若终止日期小于开始日期则将原来累加的费用冲掉)
	 * 
	 * @param al_zyh
	 * @param adt_enddate
	 * @param ab_autoadjust
	 * @param as_czgh
	 * @return
	 */
	public static boolean fylj(long al_zyh, Date adt_enddate,
			boolean ab_autoadjust, String as_czgh, BaseDAO dao, Context ctx) throws PersistentDataOperationException{
//		User user = (User) ctx.get("user.instance");
//		String JGID = user.get("manageUnit.id");// 用户的机构ID
//		Session ss = null;
		try {
//			Map<String, Object> istr_brxx = new HashMap<String, Object>();
			gf_zy_GetBRXX(al_zyh, dao);
			if (istr_brxx.get("brch") == null) {
				return true;
			}
			if (istr_brxx.get("brbq") == null) {
				return true;
			}
			if (istr_brxx.get("ljrq") == null) {
				istr_brxx.put("ljrq", istr_brxx.get("ryrq"));
			}
			// //DaysAfter是后面参数-前面参数 的天数
			int ll_AdjustValue = 0;
			System.out.println("后面参数-前面参数  = "+istr_brxx.get("ljrq")+"||"+adt_enddate);
			int ll_ComputeDays = BSHISUtil.getPeriod(
					(Date) istr_brxx.get("ljrq"), adt_enddate);
			System.out.println("ll_ComputeDays = "+ll_ComputeDays);
			if (ll_ComputeDays == 0) {
				return true;
			} else if (ll_ComputeDays > 0) {
				ll_AdjustValue = 1;
			}
			if (!InsertCWFY(istr_brxx, adt_enddate, as_czgh, dao, ctx)) {
				System.out.println("InsertCWFY error");
				return false;
			}
//			Map<String, Object> lstr_fymx = new HashMap<String, Object>();
			istr_fymx.put("zyh", istr_brxx.get("zyh")); // 住院号
			istr_fymx.put("jgid", istr_brxx.get("jgid")); // 住院号
			istr_fymx.put("brxz", istr_brxx.get("brxz")); // 病人性质
			istr_fymx.put("fyks", istr_brxx.get("brks")); // 费用科室(病人科室)
			istr_fymx.put("fybq", istr_brxx.get("brbq")); // 费用病区(病人病区)
			istr_fymx.put("zxks", istr_brxx.get("brks")); // 执行科室(病人科室)
			istr_fymx.put("xmlx", 9); // 项目类型(自动累计)
			istr_fymx.put("yplx", 0); // 药品类型
			istr_fymx.put("ypcd", 0); // 药品产地
			istr_fymx.put("srgh", as_czgh); // 输入工号
			istr_fymx.put("qrgh", as_czgh); // 确认工号
			istr_fymx.put("ysgh", istr_brxx.get("ysgh")); // 医生工号
			istr_fymx.put("yzxh", 0); // 医嘱序号
			istr_fymx.put("zlxz", istr_brxx.get("zlxz")); // 诊疗小组
			istr_fymx.put("jfrq", new Date()); // 记费日期
			Map<String, Object> parameters = new HashMap<String, Object>();
//			parameters.put("il_jgid",JGID);
			parameters.put("al_zyh",al_zyh);
			Map<String, Object> am_COUNT = dao
			.doLoad("SELECT COUNT(*) as COUNT FROM ZY_CWSZ WHERE ZYH = :al_zyh AND JCPB < 2",//JGID = :go_jgxx.il_jgid and
					parameters);
			int ll_cwsl = 0;
			if (am_COUNT != null) {
				ll_cwsl = Integer.parseInt(am_COUNT.get("COUNT") + "");
			}
			List<Map<String, Object>> lds_ljfy = dao
					.doQuery(
							"SELECT a.FYXH as FYXH,b.FYMC as FYMC,b.FYDW as FYDW,a.FYDJ as FYDJ,a.KSRQ as KSRQ,a.JSRQ as JSRQ,a.LJFS as LJFS,a.XZFS as XZFS FROM ZY_LJFY a,GY_YLSF b WHERE a.FYXH = b.FYXH",// and a.JGID = :al_jgid
							null);
			for (int i = 0; i < lds_ljfy.size(); i++) {
				Map<String, Object> ljfy = lds_ljfy.get(i);
				Date ldt_ksrq = (Date) ljfy.get("KSRQ"); // 开始日期
				Date ldt_jsrq = (Date) ljfy.get("JSRQ"); // 结束日期
				int ll_ljfs = Integer.parseInt(ljfy.get("ljfs") + ""); // 累加方式
				istr_fymx.put("fyxh", ljfy.get("fyxh")); // 取累加费用序号
				istr_fymx.put("fymc", ljfy.get("fymc")); // 取累加费用名称
				istr_fymx.put("fydw", ljfy.get("fydw")); // 取累加费用单位
				istr_fymx.put("fydj", ljfy.get("fydj")); // 取累加费用单价
				// int lc_fydj = 0;
				Map<String,Object> ZY_LJXZparameters = new HashMap<String, Object>();
				ZY_LJXZparameters.put("fyxh", istr_fymx.get("fyxh"));
				ZY_LJXZparameters.put("brch", istr_brxx.get("brch"));
				Map<String, Object> ZY_LJXZ = dao
						.doLoad("SELECT FYXH as FYXH,FYDJ as FYDJ FROM ZY_LJXZ WHERE FYXH = :fyxh AND BRCH = :brch",//JGID = :gl_jgid and 
								ZY_LJXZparameters);
				long ll_fyxh = Long.parseLong(ZY_LJXZ.get("FYXH") + "");
				double lc_fydj = Double.parseDouble(ZY_LJXZ.get("FYDJ") + "");
				if (ll_fyxh > 0) {
					istr_fymx.put("fydj", lc_fydj);
				}
				if (istr_fymx.get("fydj") == null) {
					istr_fymx.put("fydj", 0);
				}
				if (Double.parseDouble(istr_fymx.get("fydj") + "") <= 0) {
					continue;
				}
				Map<String,Object> GY_YLSFparameters = new HashMap<String, Object>();
				GY_YLSFparameters.put("fyxh", istr_fymx.get("fyxh"));
				istr_fymx
						.put("fyxm",
								dao.doLoad(
										"SELECT FYGB as FYGB FROM GY_YLSF WHERE FYXH = :fyxh",
										GY_YLSFparameters).get("FYGB"));

				Map<String, Object> body = new HashMap<String, Object>();
				body.put("TYPE", 0);
				body.put("BRXZ", istr_brxx.get("brxz"));
				body.put("FYXH", istr_fymx.get("fyxh"));
				body.put("FYGB", istr_fymx.get("fyxm"));
				Date ld_ComputeDate;
				if (ll_AdjustValue > 0) {
					ld_ComputeDate = (Date) (istr_brxx.get("ljrq"));
				} else {
					ld_ComputeDate = (Date) adt_enddate;
				}
				for (int j = 0; j < ll_ComputeDays; j++) {
					if (ld_ComputeDate.getTime() >= ((Date) ldt_ksrq).getTime()
							&& ld_ComputeDate.getTime() <= ((Date) ldt_jsrq)
									.getTime()) {
						istr_fymx.put("fyrq", (Date) ld_ComputeDate); // 费用日期
						if (ll_ljfs == 1) {
							istr_fymx.put("fysl", ll_AdjustValue * 1);
						} else {
							istr_fymx.put("fysl", ll_AdjustValue * ll_cwsl);
						}
						if (Double.parseDouble(istr_fymx.get("fysl") + "") != 0) {
//							Schema sc = SchemaController.instance().getSchema(
//									BSPHISEntryNames.ZY_FYMX);
//							List<SchemaItem> items = sc.getAllItemsList();
//							SchemaItem item = null;
//							for (SchemaItem sit : items) {
//								if ("JLXH".equals(sit.getId())) {
//									item = sit;
//									break;
//								}
//							}
//							Long jlxh = Long.parseLong(KeyManagerCreator
//									.execute(BSPHISEntryNames.ZY_FYMX,
//											item.getKeyRules(), item.getId(),
//											ctx));
//							istr_fymx.put("jlxh", jlxh);
							Map<String,Object> am_fyje = BSPHISUtil.getje(0, Long.parseLong(istr_brxx.get("brxz")+""), Long.parseLong(istr_fymx.get("fyxh")+""), Long.parseLong(istr_fymx.get("fyxm")+""),Double.parseDouble(istr_fymx.get("fydj")+""), Double.parseDouble(istr_fymx.get("fysl")+""), dao, ctx);
							istr_fymx.put("zfbl", am_fyje.get("ZFBL"));
							istr_fymx.put("ZJJE", am_fyje.get("ZJJE"));
							istr_fymx.put("ZFJE", am_fyje.get("ZFJE"));
							istr_fymx.put("ZLJE", am_fyje.get("ZLJE"));
							if (!gf_his_share_InsertFYMX(istr_fymx,dao,ctx)) {
								System.out.println("gf_his_share_InsertFYMX error");
								return false;
							}
						}
					}
					// RelativeDate = 第一个日期参数+第二个参数的天数
					ld_ComputeDate = new Date(
							(ld_ComputeDate.getTime() + (24 * 60 * 60 * 1000)));// 下一天
				}
			}
			parameters.put("adt_enddate", adt_enddate);
//			ss = (Session) ctx.get(Context.DB_SESSION);
//			ss.beginTransaction();
			dao.doUpdate(
					"UPDATE ZY_BRRY SET KSRQ = :adt_enddate WHERE ZYH = :al_zyh",//JGID = :go_jgxx.il_jgid and 
					parameters);
//			ss.getTransaction().commit();
		} catch (ModelDataOperationException e) {
//			ss.getTransaction().rollback();
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 说明：根据住院号获取病人信息
	 * @param al_zyh
	 * @param astr_brxx
	 * @param dao
	 * @param ctx
	 * @return
	 */
	public static boolean gf_zy_GetBRXX(long al_zyh,BaseDAO dao) {
//		User user = (User) ctx.get("user.instance");
//		String jgid = user.get("manageUnit.id");// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_zyh", al_zyh);
//		parameters.put("gl_jgid", jgid);
		try {
			istr_brxx = dao
					.doLoad("SELECT ZYH as ll_zyh,JGID as jgid,ZYHM as zyhm,BAHM as bahm,MZHM as mzhm,BRXM as brxm,BRQM as brqm,BRXB as brxb,BRXZ as brxz,BRKS as brks,BRBQ as brbq,BRCH as brch,ZSYS as ysgh,RYRQ as ryrq,CYRQ as cyrq,KSRQ as ljrq,CYPB as cypb,GZDW as gzdw,ZLXZ as zlxz FROM ZY_BRRY WHERE ZYH = :al_zyh",//JGID = :gl_jgid and 
							parameters);
			System.out.println(istr_brxx.get("ll_zyh")+" ljrq="+istr_brxx.get("ljrq"));
			if (istr_brxx.get("gzdw") == null) {
				istr_brxx.put("gzdw", "");
			}
			if (al_zyh > 0) {
				istr_brxx.put("zyh", al_zyh);
				return true;
			} else {
				return false;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 说明：插入床位费
	 * @param astr_brxx
	 * @param adt_enddate
	 * @param as_czgh
	 * @param dao
	 * @param ctx
	 * @return
	 */
	public static boolean InsertCWFY(Map<String, Object> astr_brxx,
			Date adt_enddate, String as_czgh, BaseDAO dao, Context ctx) {
//		User user = (User) ctx.get("user.instance");
//		String jgid = user.get("manageUnit.id");// 用户的机构ID
		int ll_ComputeDays = BSHISUtil.getPeriod((Date) astr_brxx.get("ljrq"),
				adt_enddate); // 取床位费天数
		int ll_AdjustValue;
		Date ld_ComputeDate;
		System.out.println("取床位费天数:"+ll_ComputeDays);
		if (ll_ComputeDays == 0) {
			return true;
		} else if (ll_ComputeDays > 0) {
			ll_AdjustValue = 1;
			ld_ComputeDate = (Date) astr_brxx.get("ljrq");
		} else {
			ll_AdjustValue = -1;
			ll_ComputeDays = -1 * ll_ComputeDays;
			ld_ComputeDate = adt_enddate;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		String hql = "select a.SBXH as sbxh,a.FYXH as fyxh,a.FYDJ as fydj,a.BRCH as BRCH,b.FYDW as fydw,b.FYGB as fygb,b.FYMC as fymc,a.FYMC as zy_zdycwfymc from ZY_ZDYCW a,GY_YLSF b,ZY_CWSZ c where a.JGID = c.JGID and a.BRCH = c.BRCH and c.ZDYCW = 1 and a.FYXH = b.FYXH and c.ZYH = :al_zyh";// and a.JGID = :al_jgid
		try {
			parameters.put("al_zyh", astr_brxx.get("zyh"));
//			parameters.put("al_jgid", jgid);
			List<Map<String, Object>> listcwfbd;
			listcwfbd = dao.doQuery(hql, parameters);
			if (listcwfbd.size() > 0) {
				ib_tsfy = true;
			}
			// GetCWFY(long al_zyh,String s_zy_cwfy,Map<String,Object>
			// astr_BedFee,BaseDAO dao,Context ctx)
			GetCWFY(Integer.parseInt(astr_brxx.get("zyh") + ""),
					dao, ctx);
			// 自定义床位费表单检索数据，参数：gl_jgid,astr_brxx.zyh
			// if 自定义床位费表单记录数 > 0 then //有特殊费用
			// lb_tsfy = true
			// end if
			// GetCWFY(astr_brxx.zyh,lstr_cwfy)
			// // 保存床位费公共部分信息
			istr_fymx.put("zyh", astr_brxx.get("zyh")); // 住院号
			istr_fymx.put("brxz", astr_brxx.get("brxz")); // 病人性质
			istr_fymx.put("fyks", astr_brxx.get("brks")); // 费用科室(病人科室)
			istr_fymx.put("fybq", astr_brxx.get("brbq")); // 费用病区(病人病区)
			istr_fymx.put("zxks", astr_brxx.get("brks")); // 执行科室(病人科室)
			istr_fymx.put("xmlx", 9); // 项目类型(自动累计)
			istr_fymx.put("yplx", 0); // 药品类型
			istr_fymx.put("ypcd", 0); // 药品产地
			istr_fymx.put("srgh", as_czgh); // 输入工号
			istr_fymx.put("qrgh", as_czgh); // 确认工号
			istr_fymx.put("ysgh", astr_brxx.get("ysgh")); // 医生工号
			istr_fymx.put("yzxh", 0); // 医嘱序号
			istr_fymx.put("zlxz", astr_brxx.get("zlxz")); // 诊疗小组
			istr_fymx.put("fysl", ll_AdjustValue); // 费用数量
			istr_fymx.put("jfrq", new Date());
			//
			for (int i = 0; i < ll_ComputeDays; i++) {
				istr_fymx.put("fyrq", ld_ComputeDate);
				if (ib_tsfy) {
					int ll_count;
					// String ls_fydw;
//					Map<String, Object> ZY_BRRYparameters = new HashMap<String, Object>();
//					ZY_BRRYparameters.put("ZYH", istr_fymx.get("zyh"));
//					Map<String, Object> ZY_BRRY = dao
//							.doLoad("SELECT BRXZ as BRXZ FROM ZY_BRRY WHERE ZYH = :ZYH",//JGID = :gl_jgid and 
//									ZY_BRRYparameters);
					ll_count = listcwfbd.size();
					for (int j = 0; j < ll_count; j++) {
//						Schema sc = SchemaController.instance().getSchema(
//								BSPHISEntryNames.ZY_FYMX);
//						List<SchemaItem> items = sc.getAllItemsList();
//						SchemaItem item = null;
//						for (SchemaItem sit : items) {
//							if ("JLXH".equals(sit.getId())) {
//								item = sit;
//								break;
//							}
//						}
//						Long jlxh = Long.parseLong(KeyManagerCreator.execute(
//								BSPHISEntryNames.ZY_FYMX, item.getKeyRules(),
//								item.getId(), ctx));
//						istr_fymx.put("jlxh", jlxh);
						// istr_fymx.put("jlxh",获取表ZY_FYMX的一个主键);
						istr_fymx.put("fyxh", listcwfbd.get(j).get("fyxh"));
						istr_fymx.put("fyxm", listcwfbd.get(j).get("fygb"));
						if (listcwfbd.get(j).get("fydw") == null) {
							listcwfbd.get(j).put("fydw", "");
						}
						istr_fymx.put("fymc", listcwfbd.get(j).get("fymc"));
						istr_fymx.put("fydj", listcwfbd.get(j).get("fydj"));
						Map<String, Object> body = new HashMap<String, Object>();
						body.put("TYPE", 0);
						body.put("BRXZ", astr_brxx.get("brxz"));
						body.put("FYXH", listcwfbd.get(j).get("fyxh"));
						body.put("FYGB", listcwfbd.get(j).get("fygb"));
//						istr_fymx.put("zfbl", BSPHISUtil.getzfbl(body, ctx, dao));
						Map<String,Object> am_fyje = BSPHISUtil.getje(0, Long.parseLong(istr_brxx.get("brxz")+""), Long.parseLong(istr_fymx.get("fyxh")+""), Long.parseLong(istr_fymx.get("fyxm")+""),Double.parseDouble(istr_fymx.get("fydj")+""), Double.parseDouble(istr_fymx.get("fysl")+""), dao, ctx);
						istr_fymx.put("zfbl", am_fyje.get("ZFBL"));
						istr_fymx.put("ZJJE", am_fyje.get("ZJJE"));
						istr_fymx.put("ZFJE", am_fyje.get("ZFJE"));
						istr_fymx.put("ZLJE", am_fyje.get("ZLJE"));
						// 性质自负管理.GetZFBL(0,LL_BRXZ,自定义床位费表单.fyxh[ll_row],自定义床位费表单.fygb[ll_row])
						if (!gf_his_share_InsertFYMX(istr_fymx,dao,ctx)) {
							System.out.println("gf_his_share_InsertFYMX");
							return false;
						}
					}
				}
				// 插入床位费
				System.out.println("床位费判断……序号："+istr_cwfy.get("cwfxh")+"金额"+istr_cwfy.get("cwfje"));
				if (Integer.parseInt(istr_cwfy.get("cwfxh") + "") > 0
						&& Double.parseDouble(istr_cwfy.get("cwfje") + "") > 0) {
//					Schema sc = SchemaController.instance().getSchema(
//							BSPHISEntryNames.ZY_FYMX);
//					List<SchemaItem> items = sc.getAllItemsList();
//					SchemaItem item = null;
//					for (SchemaItem sit : items) {
//						if ("CFSB".equals(sit.getId())) {
//							item = sit;
//							break;
//						}
//					}
//					Long jlxh = Long.parseLong(KeyManagerCreator.execute(
//							BSPHISEntryNames.ZY_FYMX, item.getKeyRules(),
//							item.getId(), ctx));
//					istr_fymx.put("jlxh", jlxh);
					// lstr_fymx.get("jlxh",获取表ZY_FYMX的一个主键);
					if (ll_AdjustValue > 0) {
						istr_fymx.put("fydj", istr_cwfy.get("cwfje"));
					} else {
						Map<String, Object> ZY_FYMXparameters = new HashMap<String, Object>();
						ZY_FYMXparameters.put("cwfxh", istr_cwfy.get("cwfxh"));
						ZY_FYMXparameters.put("ld_ComputeDate", ld_ComputeDate);
						istr_fymx
								.put("fydj",
										dao.doLoad(
												"SELECT SUM(FYDJ * FYSL) as FYDJ FROM ZY_FYMX WHERE ZYH = :astr_brxx.zyh AND FYXH = :cwfxh AND FYRQ = :ld_ComputeDate",//JGID = :go_jgxx.il_jgid and 
												new HashMap<String, Object>())
												.get("FYDJ"));
					}
					istr_fymx.put("fyxh", istr_cwfy.get("cwfxh"));
					istr_fymx.put("jgid", istr_brxx.get("jgid"));
					istr_fymx.put("fyxm", istr_cwfy.get("cwfxm"));
					istr_fymx.put("fymc", istr_cwfy.get("cwfmc"));
//					istr_fymx.put("zfbl", istr_cwfy.get("cwfzfbl"));
					Map<String,Object> am_fyje = BSPHISUtil.getje(0, Long.parseLong(istr_brxx.get("brxz")+""), Long.parseLong(istr_fymx.get("fyxh")+""), Long.parseLong(istr_fymx.get("fyxm")+""),Double.parseDouble(istr_fymx.get("fydj")+""), Double.parseDouble(istr_fymx.get("fysl")+""), dao, ctx);
					istr_fymx.put("zfbl", am_fyje.get("ZFBL"));
					istr_fymx.put("ZJJE", am_fyje.get("ZJJE"));
					istr_fymx.put("ZFJE", am_fyje.get("ZFJE"));
					istr_fymx.put("ZLJE", am_fyje.get("ZLJE"));
					System.out.println("插入床位费");
					if (!gf_his_share_InsertFYMX(istr_fymx,dao,ctx)) {
						return false;
					}
				}
				// // 插入自负床位费
//				System.out.println("自负床位费判断……序号："+istr_cwfy.get("zfcwfxh")+"金额"+istr_cwfy.get("zfcwfje"));
				if (Integer.parseInt(istr_cwfy.get("zfcwfxh") + "") > 0
						&& Double.parseDouble(istr_cwfy.get("zfcwfje") + "") > 0) {
//					Schema sc = SchemaController.instance().getSchema(
//							BSPHISEntryNames.ZY_FYMX);
//					List<SchemaItem> items = sc.getAllItemsList();
//					SchemaItem item = null;
//					for (SchemaItem sit : items) {
//						if ("JLXH".equals(sit.getId())) {
//							item = sit;
//							break;
//						}
//					}
//					Long jlxh = Long.parseLong(KeyManagerCreator.execute(
//							BSPHISEntryNames.ZY_FYMX, item.getKeyRules(),
//							item.getId(), ctx));
//					istr_fymx.put("jlxh", jlxh);
					// lstr_fymx.get("jlxh",获取表ZY_FYMX的一个主键);
					istr_fymx.put("fydj", istr_cwfy.get("zfcwfje"));
					istr_fymx.put("fyxh", istr_cwfy.get("zfcwfxh"));
					istr_fymx.put("fyxm", istr_cwfy.get("zfcwfxm"));
					istr_fymx.put("fymc", istr_cwfy.get("zfcwfmc"));
//					istr_fymx.put("zfbl", istr_cwfy.get("zfcwfzfbl"));
//					Map<String,Object> am_fyje = BSPHISUtil.getje(0, Long.parseLong(istr_brxx.get("brxz")+""), Long.parseLong(istr_fymx.get("fyxh")+""), Long.parseLong(istr_fymx.get("fyxm")+""),Double.parseDouble(istr_fymx.get("fydj")+""), Double.parseDouble(istr_fymx.get("fysl")+""), dao, ctx);
					istr_fymx.put("zfbl", 1);
					istr_fymx.put("ZJJE", Double.parseDouble(istr_cwfy.get("zfcwfje")+"")*Double.parseDouble(istr_fymx.get("fysl")+""));
					istr_fymx.put("ZFJE", Double.parseDouble(istr_cwfy.get("zfcwfje")+"")*Double.parseDouble(istr_fymx.get("fysl")+""));
					istr_fymx.put("ZLJE", Double.parseDouble(istr_cwfy.get("zfcwfje")+"")*Double.parseDouble(istr_fymx.get("fysl")+""));
//					System.out.println("插入自负床位费");
					if (!gf_his_share_InsertFYMX(istr_fymx,dao,ctx)) {
						return false;
					}
				}
				// // 插入诊疗费
//				System.out.println("诊疗费判断……诊疗费序号："+istr_cwfy.get("zlfxh")+"金额"+istr_cwfy.get("zlfje"));
				if (Integer.parseInt(istr_cwfy.get("zlfxh") + "") > 0
						&& Double.parseDouble(istr_cwfy.get("zlfje") + "") > 0) {
//					Schema sc = SchemaController.instance().getSchema(
//							BSPHISEntryNames.ZY_FYMX);
//					List<SchemaItem> items = sc.getAllItemsList();
//					SchemaItem item = null;
//					for (SchemaItem sit : items) {
//						if ("JLXH".equals(sit.getId())) {
//							item = sit;
//							break;
//						}
//					}
//					Long jlxh = Long.parseLong(KeyManagerCreator.execute(
//							BSPHISEntryNames.ZY_FYMX, item.getKeyRules(),
//							item.getId(), ctx));
//					istr_fymx.put("jlxh", jlxh);
					// lstr_fymx.get("jlxh",获取表ZY_FYMX的一个主键);
					istr_fymx.put("fydj", istr_cwfy.get("zlfje"));
					istr_fymx.put("fyxh", istr_cwfy.get("zlfxh"));
					istr_fymx.put("fyxm", istr_cwfy.get("zlfxm"));
					istr_fymx.put("fymc", istr_cwfy.get("zlfmc"));
//					istr_fymx.put("zfbl", istr_cwfy.get("zlfzfbl"));
					Map<String,Object> am_fyje = BSPHISUtil.getje(0, Long.parseLong(istr_brxx.get("brxz")+""), Long.parseLong(istr_fymx.get("fyxh")+""), Long.parseLong(istr_fymx.get("fyxm")+""),Double.parseDouble(istr_fymx.get("fydj")+""), Double.parseDouble(istr_fymx.get("fysl")+""), dao, ctx);
					istr_fymx.put("zfbl", am_fyje.get("ZFBL"));
					istr_fymx.put("ZJJE", am_fyje.get("ZJJE"));
					istr_fymx.put("ZFJE", am_fyje.get("ZFJE"));
					istr_fymx.put("ZLJE", am_fyje.get("ZLJE"));
//					System.out.println("插入诊疗费");
					if (!gf_his_share_InsertFYMX(istr_fymx,dao,ctx)) {
						return false;
					}
				}
				// // 插入ICU费
//				System.out.println("ICU费判断……序号："+istr_cwfy.get("icuxh")+"金额"+istr_cwfy.get("icuje"));
				if (istr_cwfy.get("icuxh")!=null && Integer.parseInt(istr_cwfy.get("icuxh") + "") > 0
						&& Double.parseDouble(istr_cwfy.get("icuje") + "") > 0) {
//					Schema sc = SchemaController.instance().getSchema(
//							BSPHISEntryNames.ZY_FYMX);
//					List<SchemaItem> items = sc.getAllItemsList();
//					SchemaItem item = null;
//					for (SchemaItem sit : items) {
//						if ("JLXH".equals(sit.getId())) {
//							item = sit;
//							break;
//						}
//					}
//					Long jlxh = Long.parseLong(KeyManagerCreator.execute(
//							BSPHISEntryNames.ZY_FYMX, item.getKeyRules(),
//							item.getId(), ctx));
//					istr_fymx.put("jlxh", jlxh);
					// lstr_fymx.get("jlxh",获取表ZY_FYMX的一个主键);
					istr_fymx.put("fydj", istr_cwfy.get("icuje"));
					istr_fymx.put("fyxh", istr_cwfy.get("icuxh"));
					istr_fymx.put("fyxm", istr_cwfy.get("icuxm"));
					istr_fymx.put("fymc", istr_cwfy.get("icumc"));
//					istr_fymx.put("zfbl", istr_cwfy.get("icuzfbl"));
					Map<String,Object> am_fyje = BSPHISUtil.getje(0, Long.parseLong(istr_brxx.get("brxz")+""), Long.parseLong(istr_fymx.get("fyxh")+""), Long.parseLong(istr_fymx.get("fyxm")+""),Double.parseDouble(istr_fymx.get("fydj")+""), Double.parseDouble(istr_fymx.get("fysl")+""), dao, ctx);
					istr_fymx.put("zfbl", am_fyje.get("ZFBL"));
					istr_fymx.put("ZJJE", am_fyje.get("ZJJE"));
					istr_fymx.put("ZFJE", am_fyje.get("ZFJE"));
					istr_fymx.put("ZLJE", am_fyje.get("ZLJE"));
//					System.out.println("插入ICU费");
					if (!gf_his_share_InsertFYMX(istr_fymx,dao,ctx)) {
						return false;
					}
				}
				ld_ComputeDate = new Date(
						(ld_ComputeDate.getTime() + (24 * 60 * 60 * 1000)));// 下一天
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 说明：写费用明细
	 */
	public static boolean gf_his_share_InsertFYMX(Map<String, Object> astr_fymx,BaseDAO dao, Context ctx)  throws PersistentDataOperationException{
//		User user = (User) ctx.get("user.instance");
		// String uid = user.getId();
//		String gl_jgid = user.get("manageUnit.id");// 用户的机构ID
		astr_fymx.put("fygg", (astr_fymx.get("fygg") + "").trim()); // 规格
		if (astr_fymx.get("fygg") == null || "null".equals(astr_fymx.get("fygg"))) {
			astr_fymx.put("fygg", "");
		} else if ((astr_fymx.get("fygg") + "").length() > 0) {
			astr_fymx.put("fygg", "/" + astr_fymx.get("fygg"));
		}
		astr_fymx.put("fydw", (astr_fymx.get("fydw") + "").trim()); // 单位
		if (astr_fymx.get("fydw") == null || "null".equals(astr_fymx.get("fydw"))) {
			astr_fymx.put("fydw", "");
		} else if ((astr_fymx.get("fydw") + "").length() > 0) {
			astr_fymx.put("fydw", "/" + astr_fymx.get("fydw"));
		}
		astr_fymx.put(
				"fymc",
				"" + astr_fymx.get("fymc") + astr_fymx.get("fygg")
						+ astr_fymx.get("fydw"));
		Map<String, Object> am_ZY_FYMX = new HashMap<String, Object>();
//		ZY_FYMX.put("JLXH", astr_fymx.get("jlxh"));
		am_ZY_FYMX.put("ZYH", astr_fymx.get("zyh"));
		am_ZY_FYMX.put("FYRQ", astr_fymx.get("fyrq"));
		am_ZY_FYMX.put("JFRQ", astr_fymx.get("jfrq"));
		am_ZY_FYMX.put("FYKS", astr_fymx.get("fyks"));
		am_ZY_FYMX.put("FYBQ", astr_fymx.get("fybq"));
		am_ZY_FYMX.put("ZXKS", astr_fymx.get("zxks"));
		am_ZY_FYMX.put("FYXH", astr_fymx.get("fyxh"));
		am_ZY_FYMX.put("FYMC", astr_fymx.get("fymc"));
		am_ZY_FYMX.put("FYXM", astr_fymx.get("fyxm"));
		am_ZY_FYMX.put("XMLX", astr_fymx.get("xmlx"));
		am_ZY_FYMX.put("YPLX", astr_fymx.get("yplx"));
		am_ZY_FYMX.put("YPCD", astr_fymx.get("ypcd"));
		am_ZY_FYMX.put("FYSL", astr_fymx.get("fysl"));
		am_ZY_FYMX.put("FYDJ", astr_fymx.get("fydj"));
		am_ZY_FYMX.put("ZFBL", astr_fymx.get("zfbl"));
//		am_ZY_FYMX.put("ZJJE", BSPHISUtil.getDouble(Double.parseDouble(istr_fymx.get("fydj")+""), 2)*istr_fymx.get("fysl"));
//		Map<String, Object> am_fyje = new HashMap<String, Object>();
//		try {
//			am_fyje = BSPHISUtil.getje(0, Long.parseLong(istr_brxx.get("brxz")+""), Long.parseLong(astr_fymx.get("fyxh")+""), Long.parseLong(astr_fymx.get("fyxm")+""),Double.parseDouble(astr_fymx.get("fydj")+""), Double.parseDouble(astr_fymx.get("fysl")+""), dao, ctx);
//		} catch (NumberFormatException e1) {
//			e1.printStackTrace();
//		} catch (ModelDataOperationException e1) {
//			e1.printStackTrace();
//		}
		am_ZY_FYMX.put("ZJJE", astr_fymx.get("ZJJE"));
		am_ZY_FYMX.put("ZFJE", astr_fymx.get("ZFJE"));
		am_ZY_FYMX.put("ZLJE", astr_fymx.get("ZLJE"));
//		am_ZY_FYMX.put("ZJJE", istr_fymx.get("zjje"));
//		am_ZY_FYMX.put("ZFJE", istr_fymx.get("zfje"));
//		am_ZY_FYMX.put("ZLJE", istr_fymx.get("zlje"));
		am_ZY_FYMX.put("SRGH", astr_fymx.get("srgh"));
		am_ZY_FYMX.put("QRGH", astr_fymx.get("qrgh"));
		am_ZY_FYMX.put("YSGH", astr_fymx.get("ysgh"));
		am_ZY_FYMX.put("YZXH", astr_fymx.get("yzxh"));
		am_ZY_FYMX.put("JSCS", 0);
		am_ZY_FYMX.put("ZLXZ", astr_fymx.get("zlxz"));
		am_ZY_FYMX.put("YEPB", 0);
		am_ZY_FYMX.put("JGID", istr_brxx.get("jgid"));
		am_ZY_FYMX.put("DZBL",1d);
//		Session ss = null;
		try {
//			ss = (Session) ctx.get(Context.DB_SESSION);
//			ss.beginTransaction();
			dao.doSave("create", BSPHISEntryNames.ZY_FYMX, am_ZY_FYMX, false);
//			System.out.println("save@@@@"+am_ZY_FYMX);
//			ss.getTransaction().commit();
		} catch (ValidateException e) {
//			ss.getTransaction().rollback();
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 说明：获得床位费、诊疗费、ICU费用及其费用序号、项目
	 */
	public static boolean GetCWFY(long al_zyh,
			BaseDAO dao, Context ctx) {
//		User user = (User) ctx.get("user.instance");
//		String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("al_zyh", al_zyh);
//			parameters.put("il_jgid", manaUnitId);
			Map<String, Object> am_ZY_BRRY = dao
					.doLoad("SELECT BRXZ as BRXZ FROM ZY_BRRY WHERE ZYH = :al_zyh",//JGID = :gl_jgid and 
							parameters);
			long ll_brxz = Long.parseLong(am_ZY_BRRY.get("BRXZ") + "");
			Map<String, Object> GY_BRXZparameters = new HashMap<String, Object>();
			GY_BRXZparameters.put("ll_brxz", ll_brxz);
			Map<String, Object> am_GY_BRXZ = dao
					.doLoad("SELECT CFXJ as CFXJ FROM GY_BRXZ WHERE BRXZ = :ll_brxz",
							GY_BRXZparameters);
			double lc_cfxj = 0;
			if (am_GY_BRXZ.get("CFXJ") != null) {
				lc_cfxj = Double.parseDouble(am_GY_BRXZ.get("CFXJ") + "");
			}
			istr_cwfy.put("cwfje", 0d);
			istr_cwfy.put("icuje", 0d);
			Map<String,Object> am_CWFJE = dao.doLoad(
					"SELECT sum(a.CWFY) as CWFJE FROM ZY_CWSZ a WHERE a.ZYH = :al_zyh and ( a.ZDYCW = 0 or a.ZDYCW is null or ( a.ZDYCW = 1 and not exists (select 1 from ZY_ZDYCW b where a.JGID = b.JGID and a.BRCH = b.BRCH ) ) )  GROUP BY a.ZYH",// and a.JGID = :gl_jgid 
					parameters);
			if (am_CWFJE != null) {
				istr_cwfy.put("cwfje", am_CWFJE.get("CWFJE"));
			} else {
				istr_cwfy.put("cwfje", 0d);
			}
			Map<String,Object> am_ICUJE = dao.doLoad(
					"SELECT sum(a.ICU) as ICUJE FROM ZY_CWSZ a WHERE a.ZYH = :al_zyh GROUP BY ZYH",// and a.JGID = :gl_jgid 
					parameters);
			if (am_ICUJE != null) {
				istr_cwfy.put("icuje", am_ICUJE.get("ICUJE"));
			} else {
				istr_cwfy.put("icuje", 0d);
			}
//			if (astr_bedfee.get("cwfje") == null) {
//				astr_bedfee.put("cwfje", 0);
//			}
//			if (astr_bedfee.get("icuje") == null) {
//				astr_bedfee.put("icuje", 0);
//			}
			// 计算床位费、自负床位费(床位费大于床费限价，多出部分作为自负床位费)
			if (Double.parseDouble(istr_cwfy.get("cwfje") + "") > lc_cfxj
					&& lc_cfxj > 0) {
				istr_cwfy.put("zfcwfje",
						Double.parseDouble(istr_cwfy.get("cwfje") + "")
								- lc_cfxj);
				istr_cwfy.put("cwfje", lc_cfxj);
			} else {
				istr_cwfy.put("zfcwfje", 0);
			}
			if (!ib_GetCWFYXX) {
				GetCWFYXX(dao, ctx);
				ib_GetCWFYXX = true;
			}
			Map<String, Object> cwf = new HashMap<String, Object>();
			cwf.put("TYPE", 0);
			cwf.put("BRXZ", ll_brxz);
			cwf.put("FYXH", istr_cwfyxx.get("cwfxh"));
			cwf.put("FYGB", istr_cwfyxx.get("cwfxm"));
			/* 床位费 */
			istr_cwfy.put("cwfxh", istr_cwfyxx.get("cwfxh"));
			istr_cwfy.put("cwfxm", istr_cwfyxx.get("cwfxm"));
			istr_cwfy.put("cwfmc", istr_cwfyxx.get("cwfmc"));
//			istr_cwfy.put("cwfzfbl", BSPHISUtil.getzfbl(cwf, ctx, dao).get("ZFBL"));
			/* 自负床位费 */
			istr_cwfy.put("zfcwfxh", istr_cwfyxx.get("zfcwfxh"));
			istr_cwfy.put("zfcwfxm", istr_cwfyxx.get("zfcwfxm"));
			istr_cwfy.put("zfcwfmc", istr_cwfyxx.get("zfcwfmc"));
//			istr_cwfy.put("zfcwfzfbl", 1);
			Map<String, Object> zlf = new HashMap<String, Object>();
			zlf.put("TYPE", 0);
			zlf.put("BRXZ", ll_brxz);
			zlf.put("FYXH", istr_cwfyxx.get("zlfxh"));
			zlf.put("FYGB", istr_cwfyxx.get("zlfxm"));
			/* 诊疗费 */
			istr_cwfy.put("zlfxh", istr_cwfyxx.get("zlfxh"));
			istr_cwfy.put("zlfxm", istr_cwfyxx.get("zlfxm"));
			istr_cwfy.put("zlfmc", istr_cwfyxx.get("zlfmc"));
			String ls_Value = ParameterUtil.getParameter(istr_brxx.get("jgid")+"", "ZLFYDJ", "0", "诊疗费单价", ctx);
			istr_cwfy.put("zlfje", BSPHISUtil.getDouble(Double.parseDouble(ls_Value), 2));
//			istr_cwfy.put("zlfzfbl", BSPHISUtil.getzfbl(zlf, ctx, dao).get("ZFBL"));
			Map<String, Object> icu = new HashMap<String, Object>();
			icu.put("TYPE", 0);
			icu.put("BRXZ", ll_brxz);
			icu.put("FYXH", istr_cwfyxx.get("icuxh"));
			icu.put("FYGB", istr_cwfyxx.get("icuxm"));
			/* ICU */
			istr_cwfy.put("icuxh", istr_cwfyxx.get("icuxh"));
			istr_cwfy.put("icuxm", istr_cwfyxx.get("icuxm"));
			istr_cwfy.put("icumc", istr_cwfyxx.get("icumc"));
//			istr_cwfy.put("icuzfbl", BSPHISUtil.getzfbl(icu, ctx, dao).get("ZFBL"));
			
			return true;
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
//		} catch (ModelDataOperationException e) {
//			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 说明：获得床位费、诊疗费、ICU费费用序号、项目项目、费用名称
	 * @param astr_bedfeeinfo
	 * @param dao
	 * @param ctx
	 */
	public static void GetCWFYXX(BaseDAO dao,Context ctx){
		Map<String,Object> bedfeeinfo = GetFYXX(6,"CWFXH",dao,ctx);
		istr_cwfyxx.put("cwfxh", bedfeeinfo.get("al_fyxh"));
		istr_cwfyxx.put("cwfmc", bedfeeinfo.get("as_fymc"));
		istr_cwfyxx.put("cwfxm", bedfeeinfo.get("al_fyxm"));
		// 取自负床位费序号、项目、名称、单位
		bedfeeinfo = GetFYXX(6,"ZFCWF",dao,ctx);
		istr_cwfyxx.put("zfcwfxh", bedfeeinfo.get("al_fyxh"));
		istr_cwfyxx.put("zfcwfmc", bedfeeinfo.get("as_fymc"));
		istr_cwfyxx.put("zfcwfxm", bedfeeinfo.get("al_fyxm"));
		// 取住院诊疗费序号、项目、名称、单位
		bedfeeinfo = GetFYXX(6,"ZLFXH",dao,ctx);
		istr_cwfyxx.put("zlfxh", bedfeeinfo.get("al_fyxh"));
		istr_cwfyxx.put("zlfmc", bedfeeinfo.get("as_fymc"));
		istr_cwfyxx.put("zlfxm", bedfeeinfo.get("al_fyxm"));
		// 取ICU费序号、项目、名称、单位
//		bedfeeinfo = GetFYXX(6,"ICUXH",dao,ctx);
//		istr_cwfyxx.put("icuxh", bedfeeinfo.get("al_fyxh"));
//		istr_cwfyxx.put("icumc", bedfeeinfo.get("as_fymc"));
//		istr_cwfyxx.put("icuxm", bedfeeinfo.get("al_fyxm"));
		
//		String ls_Value = ParameterUtil.getParameter(istr_brxx.get("jgid")+"", "ZLFYDJ", ctx);
		
	}
	
	/**
	 * 说明：从系统参数表中获得某项费用的序号、项目、名称
	 */
	public static Map<String,Object> GetFYXX(int ai_xtsb,String as_csmc,BaseDAO dao,Context ctx) {
		String TopUnitId = ParameterUtil.getTopUnitId();
		String ls_Value = ParameterUtil.getParameter(TopUnitId, as_csmc, ctx);
		long al_fyxh = Long.parseLong(ls_Value);
		Map<String,Object> astr_bedfeeinfo = new HashMap<String, Object>();
		astr_bedfeeinfo.put("al_fyxh", al_fyxh);
		try {
			if (al_fyxh > 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("al_fyxh", al_fyxh);
				Map<String, Object> am_GY_YLSF = dao
						.doLoad("SELECT FYGB as al_fyxm,FYMC as as_fymc,FYDW as ls_fydw FROM GY_YLSF WHERE FYXH = :al_fyxh",
								parameters);
				String ls_fydw = (am_GY_YLSF.get("ls_fydw") + "").trim();
				if (ls_fydw == null) {
					ls_fydw = "";
				}
				if (ls_fydw.length() > 0) {
					ls_fydw = "/" + ls_fydw;
				}
				String as_fymc = am_GY_YLSF.get("as_fymc") + ls_fydw;
				// al_fyxm = Long.parseLong(GY_YLSF.get("FYGB")+"");
				// istr_cwfyxx.put("ai_xtsb", al_fyxm);
				// istr_cwfyxx.put("as_csmc", al_fyxm);
				// istr_cwfyxx.put("al_fyxh", al_fyxm);
				// istr_cwfyxx.put("al_fyxm", GY_YLSF.get("al_fyxm"));
				astr_bedfeeinfo.put("as_fymc", as_fymc);
				astr_bedfeeinfo.put("al_fyxm", am_GY_YLSF.get("al_fyxm"));
				return astr_bedfeeinfo;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return null;
	}
	/***********************************费用累加**********************************/
}
