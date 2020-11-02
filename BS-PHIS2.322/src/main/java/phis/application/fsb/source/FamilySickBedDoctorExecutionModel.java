package phis.application.fsb.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.application.pha.source.PharmacyInventoryManageModel;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class FamilySickBedDoctorExecutionModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedDoctorExecutionModel.class);

	public FamilySickBedDoctorExecutionModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-27
	 * @description 药品提交病人查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryJcDrugSubmissionPatient(
			Map<String, Object> body) throws ModelDataOperationException {
		List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
		StringBuffer hql = new StringBuffer();
		hql.append("select b.BRXM as BRXM,a.ZYH as ZYH,b.ZYHM as ZYHM from JC_BRYZ a,JC_BRRY b where a.ZYH=b.ZYH and (a.TJZX=0 or a.TJZX is null) and a.YPLX>0 and a.ZFYP!=1  and (a.YSBZ = 0 or a.YSBZ = 1 and a.YSTJ = 1)");
		Map<String, Object> map_par = new HashMap<String, Object>();
		if (body.containsKey("YFSB")) {
			hql.append(" and YFSB=:yfsb");
			map_par.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
		}
		if (body.containsKey("YZLX")) {
			int yzlx = MedicineUtils.parseInt(body.get("YZLX"));
			if (yzlx == 1) {
				hql.append(" and LSYZ=1");
			} else {
				hql.append(" and LSYZ=0");
			}
		}
		if(body.containsKey("ZYH")){
			hql.append(" and a.ZYH=:zyh");
			map_par.put("zyh", MedicineUtils.parseLong(body.get("ZYH")));
		}
		try {
			list_ret = dao.doQuery(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品提交病人查询失败", e);
		}
		return list_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-30
	 * @description 家床药品提交
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveJcDrugSubmission(
			List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = MedicineUtils.getRetMap();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String JCKCGL = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.JCKCGL, ctx);
		Date d = new Date();
		try {
			
			if ("1".equals(JCKCGL)) {// 直接计费 不发药
				StringBuffer hql_update_yz = new StringBuffer();
				hql_update_yz
						.append("update JC_BRYZ set TJZX=1,QRSJ=:qrsj where JLXH in (:yzxh)");
				List<Long> list_yzxhs = saveFymx(body, d, ctx);
				// List<Long> list_yzxhs=(List<Long>)map_ret.get("YZXHS");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yzxh", list_yzxhs);
				map_par.put("qrsj", d);
				dao.doUpdate(hql_update_yz.toString(), map_par);
			} else if ("2".equals(JCKCGL)) {// 直接提交计费发药扣库存
				StringBuffer hql_update_yz = new StringBuffer();
				hql_update_yz
						.append("update JC_BRYZ set TJZX=2,QRSJ=:qrsj where JLXH in (:yzxh)");
				for (Map<String, Object> map_yzmx : body) {
					map_yzmx.put("YPSL",
							MedicineUtils.parseDouble(map_yzmx.get("YCSL")));
				}
				PharmacyInventoryManageModel model = new PharmacyInventoryManageModel(
						dao);
				List<Map<String, Object>> list_ret = model
						.queryAndLessInventory(body, ctx);//减库存
				if (list_ret == null
						|| (list_ret.size() == 1 && list_ret.get(0)
								.containsKey("ypxh"))) {

					StringBuffer s_kcbg = new StringBuffer();
					s_kcbg.append("[药品:").append(list_ret.get(0).get("ypmc"))
							.append("]库存不够");
					map_ret.put("code", 9000);
					map_ret.put("msg", s_kcbg.toString());
					return map_ret;
				}
				List<Long> list_yzxhs = saveFymx(body, d, ctx);//保存费用明细
				// List<Long> list_yzxhs=(List<Long>)map_ret.get("YZXHS");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yzxh", list_yzxhs);
				map_par.put("qrsj", d);
				dao.doUpdate(hql_update_yz.toString(), map_par);//更新医嘱信息
				saveFyjl(body,list_ret,d,ctx);//保存发药记录
			} else if ("3".equals(JCKCGL)) {// 生成提交单,提交到药房发药
				StringBuffer hql_update_yz = new StringBuffer();
				hql_update_yz
						.append("update JC_BRYZ set TJZX=1 where JLXH in (:yzxh)");
				List<Long> list_yzxhs = saveTjjl(body, d,JCKCGL, ctx);
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yzxh", list_yzxhs);
				dao.doUpdate(hql_update_yz.toString(), map_par);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "家床药品提交失败", e);
		}
		return map_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-2
	 * @description 保存费用明细
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Long> saveFymx(List<Map<String, Object>> body, Date d,
			Context ctx) throws ModelDataOperationException {
		// Map<String,Object> ret=new HashMap<String,Object>();
		List<Long> list_yzxhs = new ArrayList<Long>();
		// List<Long> list_jfids=new ArrayList<Long>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId();
		try {
			for (Map<String, Object> map_yzmx : body) {
				list_yzxhs.add(MedicineUtils.parseLong(map_yzmx.get("JLXH")));
				Map<String, Object> map_fymx = new HashMap<String, Object>();
				map_fymx.put("JGID",
						user.getManageUnit().getId());
				map_fymx.put("ZYH",
						MedicineUtils.parseLong(map_yzmx.get("ZYH")));
				map_fymx.put("FYRQ", d);
				map_fymx.put("FYXH",
						MedicineUtils.parseLong(map_yzmx.get("YPXH")));
				map_fymx.put("FYMC",
						MedicineUtils.parseString(map_yzmx.get("YZMC")));
				map_fymx.put("YPCD",
						MedicineUtils.parseLong(map_yzmx.get("YPCD")));
				map_fymx.put("FYSL",
						MedicineUtils.parseDouble(map_yzmx.get("YCSL")));
				map_fymx.put("FYDJ",
						MedicineUtils.parseDouble(map_yzmx.get("YPDJ")));
				map_fymx.put(
						"ZJJE",
						MedicineUtils.formatDouble(
								2,
								MedicineUtils.parseDouble(map_yzmx.get("YPDJ"))
										* MedicineUtils.parseDouble(map_yzmx
												.get("YCSL"))));
				int yplx = MedicineUtils.parseInt(map_yzmx.get("YPLX"));
				long fyxm = BSPHISUtil
						.getfygb(yplx,
								MedicineUtils.parseLong(map_yzmx.get("YPXH")),
								dao, ctx);// 费用项目
				double zfbl = getZfbl(
						MedicineUtils.parseLong(map_fymx.get("ZYH")),
						MedicineUtils.parseLong(map_fymx.get("YPXH")), fyxm);
				map_fymx.put(
						"ZFJE",
						MedicineUtils.formatDouble(
								2,
								zfbl
										* MedicineUtils.parseDouble(map_fymx
												.get("ZJJE"))));
				map_fymx.put("YSGH",
						MedicineUtils.parseString(map_yzmx.get("YSGH")));
				map_fymx.put("SRGH", userid);
				map_fymx.put("QRGH", userid);
				map_fymx.put("FYBQ",
						MedicineUtils.parseLong(map_yzmx.get("SRKS")));
				map_fymx.put("ZXKS",
						MedicineUtils.parseLong(map_yzmx.get("ZXKS")));
				map_fymx.put("JFRQ", d);
				map_fymx.put("XMLX", 2);
				map_fymx.put("YPLX", yplx);
				map_fymx.put("FYXM", fyxm);
				map_fymx.put("JSCS", 0);
				map_fymx.put("ZFBL", zfbl);
				map_fymx.put("YZXH",
						MedicineUtils.parseLong(map_yzmx.get("JLXH")));
				map_fymx.put("ZLJE", 0);
				map_fymx.put("YEPB",
						MedicineUtils.parseInt(map_yzmx.get("YEPB")));
				map_fymx.put("DZBL", 0);
				map_fymx.put("FYKS", MedicineUtils.parseLong(map_yzmx.get("BRKS")));
				map_fymx = dao
						.doSave("create",
								"phis.application.fsb.schemas.JC_FYMX",
								map_fymx, false);
				map_yzmx.put("JFID",
						MedicineUtils.parseLong(map_fymx.get("JLXH")));
				// list_jfids.add(MedicineUtils.parseLong(map_fymx.get("JLXH")));
			}
			// ret.put("YZXHS", list_yzxhs);
			// ret.put("JFIDS", list_jfids);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "费用明细保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "费用明细保存失败", e);
		}
		return list_yzxhs;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-2
	 * @description 获取自负比例
	 * @updateInfo
	 * @param zyh
	 *            住院号
	 * @param ypxh
	 *            药品序号
	 * @param fyxm
	 *            GY_ZFBL的sfxm
	 * @return
	 * @throws ModelDataOperationException
	 */
	public double getZfbl(long zyh, long ypxh, long fyxm)
			throws ModelDataOperationException {
		double d = 1;
		StringBuffer hql_brzx = new StringBuffer();// 查询病人性质
		hql_brzx.append("select BRXZ as BRXZ from JC_BRRY where ZYH=:zyh");
		StringBuffer hql_ypjy = new StringBuffer();// 从gy_ypjy中查询自付比例
		hql_ypjy.append("select ZFBL as ZFBL from GY_YPJY  where BRXZ=:brxz and YPXH=:ypxh");
		StringBuffer hql_zfbl = new StringBuffer();// 从GY_ZFBL中查询自付比例
		hql_zfbl.append("select ZFBL as ZFBL from GY_ZFBL where BRXZ=:brxz and SFXM=:fyxm");
		Map<String, Object> map_par = new HashMap<String, Object>();
		double zfbl = 1;
		try {
			map_par.put("zyh", zyh);
			Map<String, Object> map_brxz = dao.doLoad(hql_brzx.toString(),
					map_par);
			if (map_brxz == null) {
				return d;
			}
			map_par.clear();
			map_par.put("brxz", MedicineUtils.parseLong(map_brxz.get("BRXZ")));
			map_par.put("ypxh", ypxh);
			Map<String, Object> map_zfbl = dao.doLoad(hql_ypjy.toString(),
					map_par);
			if (map_zfbl != null) {
				return MedicineUtils.parseDouble(map_zfbl.get("ZFBL"));
			}
			map_par.remove("ypxh");
			map_par.put("fyxm", fyxm);
			map_zfbl = dao.doLoad(hql_zfbl.toString(), map_par);
			if (map_zfbl == null) {
				return d;
			}
			zfbl = MedicineUtils.parseDouble(map_zfbl.get("ZFBL"));
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "自付比例查询失败", e);
		}
		return zfbl;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-3
	 * @description 保存提交记录
	 * @updateInfo
	 * @param body
	 * @param d
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Long> saveTjjl(List<Map<String, Object>> body, Date d,String JCKCGL,
			Context ctx) throws ModelDataOperationException {
		List<Long> list_yzxhs = new ArrayList<Long>();
		List<Map<String, Object>> list_tj01 = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map_yzmx : body) {
			boolean tag = false;
			list_yzxhs.add(MedicineUtils.parseLong(map_yzmx.get("JLXH")));
			for (Map<String, Object> map_tj01 : list_tj01) {
				if (MedicineUtils.compareMaps(map_tj01, new String[] { "TJYF",
						"YZLX", "FYFS", "LSYZ" }, map_yzmx, new String[] {
						"YFSB", "XMLX", "FYFS", "LSYZ" })) {
					tag = true;
					saveTj02(map_yzmx,
							MedicineUtils.parseLong(map_tj01.get("TJXH")),JCKCGL, d);
					break;
				}
			}
			if (!tag) {
				long tjxh = saveTj01(map_yzmx, list_tj01, d);
				saveTj02(map_yzmx, tjxh,JCKCGL, d);
			}
		}

		return list_yzxhs;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-3
	 * @description 提交主表保存
	 * @updateInfo
	 * @param map_yzmx
	 * @param d
	 * @return
	 * @throws ModelDataOperationException
	 */
	public long saveTj01(Map<String, Object> map_yzmx,
			List<Map<String, Object>> list_tj01, Date d)
			throws ModelDataOperationException {
		long tjxh = 0;
		Map<String, Object> map_tj01 = new HashMap<String, Object>();
		map_tj01.put("JGID", UserRoleToken.getCurrent().getManageUnit().getId());
		map_tj01.put("TJYF", MedicineUtils.parseLong(map_yzmx.get("YFSB")));
		map_tj01.put("YZLX", MedicineUtils.parseInt(map_yzmx.get("XMLX")));
		map_tj01.put("FYFS", MedicineUtils.parseLong(map_yzmx.get("FYFS")));
		map_tj01.put("XMLX", 1);
		map_tj01.put("TJSJ", d);
		map_tj01.put(
				"TJBQ",
				MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty(
						"wardId")));
		map_tj01.put("TJGH", UserRoleToken.getCurrent().getUserId());
		map_tj01.put("FYBZ", 0);
		try {
			Map<String, Object> map_tj01_pk = dao.doSave("create",
					"phis.application.fsb.schemas.JC_TJ01", map_tj01, false);
			tjxh = MedicineUtils.parseLong(map_tj01_pk.get("TJXH"));
			map_tj01.put("TJXH", tjxh);
			list_tj01.add(map_tj01);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "提交主表保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "提交主表保存失败", e);
		}
		return tjxh;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-3
	 * @description 保存提交明细表
	 * @updateInfo
	 * @param map_yzmx
	 * @param tjxh
	 * @param d
	 * @throws ModelDataOperationException
	 */
	public void saveTj02(Map<String, Object> map_yzmx, long tjxh,String JCKCGL, Date d)
			throws ModelDataOperationException {
		try {
			int fybz=0;
			double fyje=0;
			String fygh="";
			if("2".equals(JCKCGL)){
				fybz=1;
				fyje=MedicineUtils.formatDouble(
						2,
						MedicineUtils.parseDouble(map_yzmx.get("YPDJ"))
								* MedicineUtils.parseDouble(map_yzmx
										.get("YCSL")));
				fygh= UserRoleToken.getCurrent().getUserId();
			}
			Map<String, Object> map_tj02 = new HashMap<String, Object>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			map_tj02.put("TJXH", tjxh);
			map_yzmx.put("TJXH", tjxh);
			map_tj02.put("YZXH", MedicineUtils.parseLong(map_yzmx.get("JLXH")));
			map_tj02.put("ZYH", MedicineUtils.parseLong(map_yzmx.get("ZYH")));
			map_tj02.put("YPXH", MedicineUtils.parseLong(map_yzmx.get("YPXH")));
			map_tj02.put("YPCD", MedicineUtils.parseLong(map_yzmx.get("YPCD")));
			map_tj02.put("KSSJ",
					sdf.parse(MedicineUtils.parseString(map_yzmx.get("KSSJ"))));
			map_tj02.put("YCSL",
					MedicineUtils.parseDouble(map_yzmx.get("YCSL")));
			map_tj02.put("YTCS", MedicineUtils.parseInt(map_yzmx.get("MRCS")));
			map_tj02.put("JFRQ", d);
			map_tj02.put("SYPC",
					MedicineUtils.parseString(map_yzmx.get("SYPC")));
			map_tj02.put("YPDJ",
					MedicineUtils.parseDouble(map_yzmx.get("YPDJ")));
			map_tj02.put("YFDW",
					MedicineUtils.parseString(map_yzmx.get("YFDW")));
			map_tj02.put("YFGG",
					MedicineUtils.parseString(map_yzmx.get("YFGG")));
			map_tj02.put("YFBZ", MedicineUtils.parseInt(map_yzmx.get("YFBZ")));
			map_tj02.put("QRRQ", d);
			map_tj02.put("LSYZ", MedicineUtils.parseInt(map_yzmx.get("LSYZ")));
			map_tj02.put("FYJE", fyje);
			map_tj02.put("FYBZ", fybz);
			if(!"".equals(fygh)){
				map_tj02.put("FYGH", fygh);
			}
			map_tj02.put("QZCL", 0);
			map_tj02.put("SJFYBZ", 0);
			map_tj02.put("FYSL",
					MedicineUtils.parseDouble(map_yzmx.get("YCSL")));
			map_tj02.put("JGID", UserRoleToken.getCurrent().getManageUnit()
					.getId());
			map_tj02.put("YEPB", MedicineUtils.parseInt(map_yzmx.get("YEPB")));
			map_tj02.put("FYKS", MedicineUtils.parseLong(map_yzmx.get("BRKS")));
			map_tj02.put("TJYF", MedicineUtils.parseLong(map_yzmx.get("YFSB")));
			map_tj02.put("FYFS", MedicineUtils.parseLong(map_yzmx.get("FYFS")));
			map_tj02.put("YZLX", MedicineUtils.parseInt(map_yzmx.get("XMLX")));
			dao.doSave("create", "phis.application.fsb.schemas.JC_TJ02",
					map_tj02, false);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "提交明细保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "提交明细保存失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "提交明细保存失败", e);
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-3
	 * @description 保存药房发药记录和明细
	 * @updateInfo
	 * @param body
	 * @param d
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public void saveFyjl(List<Map<String, Object>> body,List<Map<String, Object>> list_kcmx, Date d, Context ctx)
			throws ModelDataOperationException {
		String jgid=UserRoleToken.getCurrent().getManageUnit().getId();
		//long yfsb = MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("pharmacyId"));// 用户的药房识别
		String userid=UserRoleToken.getCurrent().getUserId();
		// 更新YF_FYJL
		Map<String, Object> map_yf_fyjl_data = new HashMap<String, Object>();// yf_fyjl保存记录
		map_yf_fyjl_data.put("JGID", jgid);
		map_yf_fyjl_data.put("FYSJ", d);
		map_yf_fyjl_data.put("FYGH", userid);
		map_yf_fyjl_data.put("FYBQ",MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty("wardId")));
		if (body.size() > 0) {
			if (MedicineUtils.parseInt(body.get(0).get("XMLX")) == 2) {
				map_yf_fyjl_data.put("FYLX", 2);
			} else if (MedicineUtils.parseInt(body.get(0).get("XMLX")) == 3) {
				map_yf_fyjl_data.put("FYLX", 3);
			} else {
				map_yf_fyjl_data.put("FYLX", 1);
			}
		}
		map_yf_fyjl_data.put("YFSB", MedicineUtils.parseLong(body.get(0).get("YFSB")));
		map_yf_fyjl_data.put("FYFS",
				MedicineUtils.parseLong(body.get(0).get("FYFS")));
		map_yf_fyjl_data.put("DYPB", 0);
		try {
			map_yf_fyjl_data = dao.doSave("create","phis.application.fsb.schemas.JC_FYJL" ,
					map_yf_fyjl_data, false);
			long jlid=MedicineUtils.parseLong(map_yf_fyjl_data.get("JLID"));
			StringBuffer hql_ypxx=new StringBuffer();
			hql_ypxx.append("select a.YPXH as YPXH,a.YFBZ as YFBZ,b.JYLX as JYLX from YF_YPXX a,YK_TYPK b where a.YPXH=b.YPXH and a.YFSB=:yfsb and a.YPXH=:ypxh");
			for(Map<String,Object> map_yzmx:body){
				Map<String,Object> map_par_ypxx=new HashMap<String,Object>();
				map_par_ypxx.put("ypxh", MedicineUtils.parseLong(map_yzmx.get("YPXH")));
				map_par_ypxx.put("yfsb", MedicineUtils.parseLong(map_yzmx.get("YFSB")));
				Map<String,Object> map_ypxx=dao.doLoad(hql_ypxx.toString(), map_par_ypxx);
				if(map_ypxx==null){
					throw new ModelDataOperationException("未找到"+MedicineUtils.parseString(map_yzmx.get("YZMC"))+"的药房药品信息");
				}
				Map<String,Object> map_kcmx_temp=new HashMap<String,Object>();
				for(Map<String,Object> map_kcmx:list_kcmx){
					if(MedicineUtils.compareMaps(map_kcmx, new String[]{"YPXH","YPCD","YFSB","LSJG"}, map_yzmx, new String[]{"YPXH","YPCD","YFSB","YPDJ"})){
						map_kcmx_temp.putAll(map_kcmx);
						break;
					}
				}
				int yfbz=MedicineUtils.parseInt(map_ypxx.get("YFBZ"));
				Map<String, Object> map_yf_jcfymx_data = new HashMap<String, Object>();
				map_yf_jcfymx_data.put("JGID", jgid);
				map_yf_jcfymx_data.put("YFSB", MedicineUtils.parseLong(map_yzmx.get("YFSB")));
				map_yf_jcfymx_data.put("CKBH", 0);
				map_yf_jcfymx_data.put("FYLX", 1);
				map_yf_jcfymx_data.put("ZYH", MedicineUtils.parseLong(map_yzmx.get("ZYH")));
				map_yf_jcfymx_data.put("FYRQ", d);
				map_yf_jcfymx_data.put("YPXH", MedicineUtils.parseLong(map_yzmx.get("YPXH")));
				map_yf_jcfymx_data.put("YPCD", MedicineUtils.parseLong(map_yzmx.get("YPCD")));
				map_yf_jcfymx_data.put("YPGG", MedicineUtils.parseString(map_yzmx.get("YFGG")));
				map_yf_jcfymx_data.put("YFDW", MedicineUtils.parseString(map_yzmx.get("YFDW")));
				map_yf_jcfymx_data.put("YFBZ", MedicineUtils.parseInt(map_yzmx.get("YFBZ")));
				map_yf_jcfymx_data.put("YPSL", MedicineUtils.formatDouble(
						4,
						MedicineUtils.parseDouble(map_yzmx.get("YCSL"))
								* yfbz
								/ MedicineUtils.parseInt(map_yzmx
										.get("YFBZ"))));
				map_yf_jcfymx_data.put("YPDJ", MedicineUtils.formatDouble(
						4,
						MedicineUtils.parseDouble(map_yzmx.get("YPDJ"))
								* MedicineUtils.parseInt(map_yzmx
										.get("YFBZ")) / yfbz));
				int yplx = MedicineUtils.parseInt(map_yzmx.get("YPLX"));
				long fyxm = BSPHISUtil
						.getfygb(yplx,
								MedicineUtils.parseLong(map_yzmx.get("YPXH")),
								dao, ctx);// 费用项目
				double zfbl = getZfbl(
						MedicineUtils.parseLong(map_yzmx.get("ZYH")),
						MedicineUtils.parseLong(map_yzmx.get("YPXH")), fyxm);
				map_yf_jcfymx_data.put("ZFBL", zfbl);
				map_yf_jcfymx_data.put("QRGH", userid);
				map_yf_jcfymx_data.put("JFRQ", d);
				map_yf_jcfymx_data.put("YPLX", yplx);
				map_yf_jcfymx_data.put("FYKS", MedicineUtils.parseLong(map_yzmx.get("BRKS")));
				map_yf_jcfymx_data.put("LYBQ", MedicineUtils.parseLong(map_yzmx.get("SRKS")));
				map_yf_jcfymx_data.put(
						"ZXKS",MedicineUtils.parseLong(map_yzmx.get("ZXKS")));
				map_yf_jcfymx_data.put("YZXH", MedicineUtils.parseLong(map_yzmx.get("JLXH")));
				map_yf_jcfymx_data.put("YEPB", MedicineUtils.parseInt(map_yzmx.get("YEPB")));
				map_yf_jcfymx_data.put("ZFPB", zfbl == 1 ? 0 : 1);// zfbl =
																	// 1时是0
																	// 否则是1
				map_yf_jcfymx_data.put("FYFS",MedicineUtils.parseLong(map_yzmx.get("FYFS")));
				map_yf_jcfymx_data.put("LSJG", MedicineUtils.formatDouble(
						4,
						MedicineUtils.parseDouble(map_yzmx.get("YPDJ"))
								* MedicineUtils.parseInt(map_yzmx
										.get("YFBZ")) / yfbz));
				map_yf_jcfymx_data.put("PFJG", MedicineUtils.formatDouble(
						4,
						MedicineUtils.parseDouble(map_kcmx_temp.get("PFJG"))
								* MedicineUtils.parseInt(map_yzmx
										.get("YFBZ")) / yfbz));
				map_yf_jcfymx_data.put("JHJG", MedicineUtils.formatDouble(
						4,
						MedicineUtils.parseDouble(map_kcmx_temp.get("JHJG"))
								* MedicineUtils.parseInt(map_yzmx
										.get("YFBZ")) / yfbz));
				map_yf_jcfymx_data.put("FYJE", MedicineUtils.formatDouble(
						2,
						MedicineUtils.parseDouble(map_yf_jcfymx_data
								.get("YPSL"))
								* MedicineUtils
										.parseDouble(map_yf_jcfymx_data
												.get("YPDJ"))));
				map_yf_jcfymx_data.put("LSJE", MedicineUtils.formatDouble(
						2,
						MedicineUtils.parseDouble(map_yf_jcfymx_data
								.get("LSJG"))
								* MedicineUtils
										.parseDouble(map_yf_jcfymx_data
												.get("YPSL"))));
				map_yf_jcfymx_data.put("PFJE", MedicineUtils.formatDouble(
						2,
						MedicineUtils.parseDouble(map_yf_jcfymx_data
								.get("PFJG"))
								* MedicineUtils
										.parseDouble(map_yf_jcfymx_data
												.get("YPSL"))));
				map_yf_jcfymx_data.put("JHJE", MedicineUtils.formatDouble(
						2,
						MedicineUtils.parseDouble(map_yf_jcfymx_data
								.get("JHJG"))
								* MedicineUtils
										.parseDouble(map_yf_jcfymx_data
												.get("YPSL"))));
				map_yf_jcfymx_data.put("YPPH", map_kcmx_temp.get("YPPH"));
				map_yf_jcfymx_data.put("YPXQ", map_kcmx_temp.get("YPXQ"));
				map_yf_jcfymx_data.put("TYGL", 0);
				map_yf_jcfymx_data.put(
						"JBYWBZ",MedicineUtils.parseInt(map_ypxx.get("JYLX")));
				map_yf_jcfymx_data.put("KCSB", map_kcmx_temp.get("KCSB"));
				map_yf_jcfymx_data.put("TJXH", map_yzmx.get("TJXH"));
				map_yf_jcfymx_data.put("TYXH", 0);
				map_yf_jcfymx_data.put("JLID", jlid);
				map_yf_jcfymx_data.put("JFID", map_yzmx.get("JFID"));
				dao.doSave("create", "phis.application.fsb.schemas.YF_JCFYMX",
						map_yf_jcfymx_data, false);
			}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "保存发药记录失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "保存发药记录失败", e);
		}
	}

}
