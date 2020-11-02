package phis.application.pha.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;

/**
 * 药房初始账簿Model
 * 
 * @author caijy
 * 
 */
public class PharmacyInitialBooksModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyInitialBooksModel.class);

	public PharmacyInitialBooksModel(BaseDAO dao) {
		this.dao = dao;
	}

	// 药房账簿初始化跳转判断
	public Map<String, Object> initialSignsQueries(Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		if (!MedicineUtils.verificationPharmacyId(ctx)) {
			return MedicineUtils.getRetMap("请先设置药房", 9002);
		}
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", jgid);
		parameters.put("yfsb", yfsb);
		try {
			StringBuffer yjHql = new StringBuffer();
			yjHql.append(" YFSB=:yfsb and CKBH=0 and JGID=:jgid");// 判断有无月结记录,有月结则初始化已完成
			Long lo = dao.doCount("YF_JZJL", yjHql.toString(), parameters);
			if (lo > 0) {
				return MedicineUtils.getRetMap("初始化完成", 201);
			} else {
				StringBuffer yfInitHql = new StringBuffer();
				yfInitHql.append(" YFSB =:yfsb ");
				parameters.remove("jgid");
				Long l = dao.doCount("YF_YPXX", yfInitHql.toString(),
						parameters);
				if (l == 0) {
					return MedicineUtils.getRetMap("请先进行药品信息数据导入操作!", 9002);
				} else {
					parameters.put("jgid", jgid);
					StringBuffer drsjSql = new StringBuffer();
					drsjSql.append("select yk.YPXH as YPXH,cd.YPCD as YPCD from YK_TYPK yk,YF_YPXX yf,YK_CDXX cd,YK_YPXX yp,YK_CDDZ dz  where yk.ZFPB!=1 and yp.YKZF!=1 and yf.YFZF!=1 and cd.ZFPB!=1 and  dz.YPCD=cd.YPCD and yp.JGID=yf.JGID and cd.JGID=yf.JGID and yp.YPXH=yk.YPXH and cd.YPXH=yf.YPXH  and yk.YPXH=yf.YPXH and yf.JGID=:jgid and yf.YFSB=:yfsb AND yk.YPXH not in (select YPXH from  YF_KCMX  where YFSB =:yfsb and JGID=:jgid) ");
					List<Map<String, Object>> result = dao.doSqlQuery(
							drsjSql.toString(), parameters);
					if (result == null) {
						return MedicineUtils.getRetMap("没有需要新导入的数据", 202);
					} else {
						Schema sc = SchemaController.instance().get(
								"phis.application.pha.schemas.YF_KCMX_CSH");
						SchemaItem item = sc.getKeyItem();
						Long ypxh;
						Long ypcd;
						Long pkey;
						int ckbh = 0;// 窗口编号,暂时先定死
						Map<String, Object> parameter = new HashMap<String, Object>();
						for (int i = 0; i < result.size(); i++) {
							ypxh = MedicineUtils.parseLong(result.get(i).get(
									"YPXH"));
							ypcd = MedicineUtils.parseLong(result.get(i).get(
									"YPCD"));
							//yx-2017-06-19-主键获取改为取序列值-b
							Map<String, Object> keymap = new HashMap<String, Object>();
							Map<String, Object> p = new HashMap<String, Object>();
							p.put("keyvalue", 1);
							keymap = dao.doSqlLoad("select seq_yf_kcmx.nextval as KEY from dual where 1=:keyvalue", p);
							pkey=Long.parseLong(keymap.get("KEY")+"");
							//yx-2017-06-19-主键获取改为取序列值-e
							String trunc = "round";
							StringBuffer insertsql = new StringBuffer();
							insertsql.append("insert into YF_KCMX (JGID,SBXH,YFSB ,CKBH ,YPXH  ,YPCD  ,YPPH ,YPSL,JYBZ,LSJG ,PFJG,JHJG ,LSJE ,PFJE,JHJE,YKJH ,YKLJ ,YKJJ ,YKPJ ,YWLX) select yf.JGID ,"
											+ pkey
											+ ","
											+ yfsb
											+ ","
											+ ckbh
											+ ",yk.YPXH,cd.YPCD,'',0.0,0,"
											+ trunc
											+ "("
											+ trunc
											+ "((yf.YFBZ/yk.ZXBZ),6)*cd.LSJG,4) ,"
											+ trunc
											+ "("
											+ trunc
											+ "((yf.YFBZ/yk.ZXBZ),6)*cd.PFJG,4) ,"
											+ trunc
											+ "("
											+ trunc
											+ "((yf.YFBZ/yk.ZXBZ),6)*cd.JHJG,4),0.0,0.0,0.0,0.0,0.0,0.0,0.0,0 from YK_TYPK yk,YF_YPXX yf,YK_CDXX cd,YK_YPXX yp,YK_CDDZ dz where  dz.YPCD=cd.YPCD  and yp.JGID=yf.JGID and cd.JGID=yf.JGID and yp.YPXH=yk.YPXH and cd.YPXH=yf.YPXH and yk.YPXH="
											+ ypxh
											+ "  and yk.YPXH=yf.YPXH and yf.JGID='"
											+ jgid
											+ "'  and yf.YFSB="
											+ yfsb
											+ "  and cd.YPCD=" + ypcd);
							dao.doSqlUpdate(insertsql.toString(), parameter);
						}
						return MedicineUtils.getRetMap("导入" + result.size()
								+ "条记录", 202);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药房账簿初始化失败", e);
		} catch (NumberFormatException e) {
			MedicineUtils.throwsException(logger, "药房账簿初始化失败", e);
		} catch (ControllerException e) {
			MedicineUtils.throwsException(logger, "药房账簿初始化失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 保存账簿初始化数据
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveInventory(List<Map<String, Object>> body)
			throws ModelDataOperationException {
		for (Map<String, Object> kcmx : body) {
			try {
				dao.doSave("update", BSPHISEntryNames.YF_KCMX_CSH, kcmx, false);
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "初始化数据保存验证失败", e);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "初始化数据保存验证失败", e);
			}
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 药房账簿初始化转账
	 * @updateInfo
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void pharmacyTransfer(Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		if (!MedicineUtils.verificationPharmacyId(ctx)) {
			return;
		}
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		// StringBuffer cwyf = new StringBuffer();// 财务月份
		int yjDate=32;
		try{
			yjDate = MedicineUtils.parseInt(ParameterUtil.getParameter(jgid,
					"YJSJ_YF" + yfsb,
					BSPHISSystemArgument.defaultValue.get("YJSJ_YF"),BSPHISSystemArgument.defaultAlias.get("YJSJ_YF"),BSPHISSystemArgument.defaultCategory.get("YJSJ_YF"), ctx));// 月结日
		}catch(Exception e){
			MedicineUtils.throwsSystemParameterException(logger, "YJSJ_YF" + yfsb, e);
		}
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int nowDate = c.get(Calendar.DATE);
		if (yjDate > nowDate) {
			c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
		}
		c.set(Calendar.DATE, 10);
		savePharmacycheckout(jgid, yfsb, sdf.format(c.getTime()));// 保存药房结账记录
		deleteInvalidInventory(jgid, yfsb);// 删除表YF_KCMX中ypsl为0的数据
		saveMonthlyData(jgid, yfsb, sdf.format(c.getTime()));// 药房月结结果插入月结的数据
		savePharmacyDaily(jgid, yfsb);// 保存药房日报记录
		savePharmacyDailyDetail(jgid, yfsb);// 保存药房日报明细
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 保存药房结账记录
	 * @updateInfo
	 * @param jgid
	 * @param yfsb
	 * @param cwyf
	 * @throws ModelDataOperationException
	 */
	public void savePharmacycheckout(String jgid, long yfsb, String cwyf)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer hql = new StringBuffer();
		parameters.put("jgid", jgid);
		hql.append("INSERT INTO YF_JZJL  (JGID,CWYF,QSSJ,ZZSJ,YFSB,CKBH) Values (:jgid,:cwyf,:qssj,:qssj,:yfsb,0)");
		try {
			parameters.put("yfsb", yfsb);
			parameters.put("cwyf", sdf.parse(cwyf));
			parameters.put("qssj", new Date());
			dao.doSqlUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "保存药房结账记录失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "保存药房结账记录失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 删除表YF_KCMX中ypsl为0的数据
	 * @updateInfo
	 * @param jgid
	 * @param yfsb
	 * @throws ModelDataOperationException
	 */
	public void deleteInvalidInventory(String jgid, long yfsb)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("delete from YF_KCMX where YFSB=" + yfsb
				+ " and YPSL =0 and CKBH =0 and JGID='" + jgid + "'");
		try {
			dao.doUpdate(hql.toString(), null);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "删除数量为0的库存失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 药房月结结果插入月结的数据
	 * @updateInfo
	 * @param jgid
	 * @param yfsb
	 * @param cwyf
	 * @throws ModelDataOperationException
	 */
	public void saveMonthlyData(String jgid, long yfsb, String cwyf)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO YF_YJJG  (JGID,YFSB,CKBH,CWYF,YPXH,YPCD,YPGG,YFDW,YFBZ,KCSL,LSJG,PFJG,LSJE,PFJE,JHJG,JHJE) SELECT a.JGID,a.YFSB,a.CKBH, "
					+ BSPHISUtil.toDate(cwyf, "yyyy-mm-dd")
					+ ",a.YPXH,a.YPCD,c.YPGG,b.YFDW,b.YFBZ,a.YPSL,a.LSJG,a.PFJG,a.LSJE,a.PFJE,a.JHJG,a.JHJE FROM YF_KCMX a,YF_YPXX b,YK_TYPK c  WHERE a.YPXH = b.YPXH and b.YPXH=c.YPXH AND b.YFZF = 0 AND b.YFSB = a.YFSB AND a.YFSB = "
					+ yfsb
					+ " AND a.CKBH = 0 AND b.JGID = a.JGID AND a.JGID = '"
					+ jgid + "'");
			dao.doSqlUpdate(sql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药房月结结果新增失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 保存药房日报
	 * @updateInfo
	 * @param jgid
	 * @param yfsb
	 * @throws ModelDataOperationException
	 */
	public void savePharmacyDaily(String jgid, long yfsb)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		GregorianCalendar cdr = new GregorianCalendar();
		cdr.add(Calendar.DAY_OF_MONTH, -1);
		String date = BSPHISUtil.toDate(BSHISUtil.toString(cdr.getTime()),
				"yyyy-mm-dd");
		String nowDate = BSPHISUtil.toDate(BSHISUtil.toString(new Date()),
				"yyyy-mm-dd");
		StringBuffer hql = new StringBuffer();
		hql.append("insert into YF_YFRB  (JGID,YFSB,RBRQ,QSSJ,ZZSJ) Values ('"
				+ jgid + "'," + yfsb + "," + date + "," + nowDate + ","
				+ nowDate + ")");
		try {
			dao.doSqlUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药房日报记录保存失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 保存药房日报明细
	 * @updateInfo
	 * @param jgid
	 * @param yfsb
	 * @throws ModelDataOperationException
	 */
	public void savePharmacyDailyDetail(String jgid, long yfsb)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		GregorianCalendar cdr = new GregorianCalendar();
		cdr.add(Calendar.DAY_OF_MONTH, -1);
		StringBuffer hql = new StringBuffer();
		hql.append("INSERT INTO YF_RBMX (JGID,YFSB,RBRQ,YPXH,YPCD,YFBZ,BRSR,BRZC,BRJC) SELECT a.JGID,"
				+ yfsb
				+ ","
				+ BSPHISUtil.toDate(BSHISUtil.toString(cdr.getTime()),
						"yyyy-mm-dd")
				+ ",a.YPXH,a.YPCD,c.YFBZ,0,0,a.YPSL FROM YF_KCMX  a,YK_TYPK b,YF_YPXX c,YK_YPXX d  Where a.YPXH = b.YPXH And d.YPXH = b.YPXH And c.YPXH = b.YPXH  And c.YFSB = a.YFSB And b.TSYP <> 0 AND a.JGID = d.JGID AND c.JGID = d.JGID And a.YFSB = "
				+ yfsb + " AND a.JGID ='" + jgid + "'");
		try {
			dao.doSqlUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药房日报明细新增失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-9
	 * @description 检查药房账簿是否已经初始化
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> initialization(Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = MedicineUtils.getRetMap();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer yjHql = new StringBuffer();
		yjHql.append(" YFSB=:yfsb and CKBH=0 ");// 判断有无月结记录,有月结则初始化已完成
		try {
			parameters.put("yfsb", yfsb);
			Long lo = dao.doCount("YF_JZJL", yjHql.toString(), parameters);
			if (lo == 0) {
				return MedicineUtils.getRetMap("药房账簿未初始转账",
						ServiceCode.NO_RECORD);
			}
			String qyfyck = ParameterUtil.getParameter(user.getManageUnit()
					.getId(), BSPHISSystemArgument.QYFYCK, ctx);
			map_ret.put("qyfyck", qyfyck);

		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "账簿是否初始化查询失败", e);
		}
		return map_ret;
	}
}
