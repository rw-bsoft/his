package phis.application.fsb.source;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.application.med.source.WardProjectModule;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class FamilySickBedProjectSubmissionModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedProjectSubmissionModel.class);

	public FamilySickBedProjectSubmissionModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-9
	 * @description 项目提交病人列表查询
	 * @updateInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryProjectSubmissionPatient(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		StringBuffer hql=new StringBuffer();
		hql.append("select distinct a.ZYH as ZYH,b.ZYHM as ZYHM,b.BRXM as BRXM from JC_BRYZ a,JC_BRRY b where a.ZYH=b.ZYH and a.JGID=b.JGID and (a.JFBZ=1 or a.JFBZ=9) and a.XMLX>3 and a.SYBZ=0 and (a.LSBZ=0 or a.LSBZ=2) and a.YZPB=0 and (a.YSBZ=0 or (a.YSBZ = 1 and a.YSTJ = 1)) and (a.QRSJ<=:now or a.QRSJ is null) and a.JGID = :jgid and (a.TJZX=0 or a.TJZX is null) ");
//		if(body.containsKey("YZLX")&&body.get("YZLX")!=null){
//			int yzlx=MedicineUtils.parseInt(body.get("YZLX"));
//			if(yzlx==1){
//				hql.append(" and a.LSYZ=1");
//			}
//		}
		Map<String,Object> map_par=new HashMap<String,Object>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		map_par.put("now", c.getTime());
		map_par.put("jgid", jgid);
		if(body.containsKey("ZYH")){
			hql.append(" and a.ZYH=:zyh");
			map_par.put("zyh", MedicineUtils.parseLong(body.get("ZYH")));
		}
		try {
			List<Map<String,Object>> list_temp=dao.doSqlQuery(hql.toString(), map_par);
			//过滤调没有明细的记录
			if(list_temp!=null&&list_temp.size()>0){
				for(Map<String,Object> map:list_temp){
					Map<String,Object> m=new HashMap<String,Object>();
					List<Long> list=new ArrayList<Long>();
					list.add(MedicineUtils.parseLong(map.get("ZYH")));
					m.put("ZYHS", list);
					List<Map<String, Object>> l=queryProjectSubmissionDetail(m,ctx);
					if(l!=null&&l.size()>0){
						list_ret.add(map);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "家床项目执行病人列表查询失败", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-9
	 * @description 项目提交明细查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryProjectSubmissionDetail(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		if(!body.containsKey("ZYHS")||body.get("ZYHS")==null){
			return list_ret;
		}
		List<Long> list_zyhs=(List<Long>)body.get("ZYHS");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		StringBuffer hql=new StringBuffer();
		hql.append("select a.YZZH as YZZH,a.JLXH as JLXH,b.ZYHM as ZYHM,b.BRXM as BRXM,a.YZMC as YZMC,a.MRCS as MRCS,a.YCSL as YCSL,a.ZXKS as ZXKS,a.YPDJ as YPDJ,a.YYTS as YYTS,a.YPLX as YPLX,a.SRKS as SRKS,a.BRKS as BRKS,a.YEPB as YEPB,a.XMLX as XMLX,a.FYFS as FYFS,a.LSYZ as LSYZ,a.ZYH as ZYH,a.YPXH as YPXH,a.YSGH as YSGH,a.XMLX as XMLX,a.YJZX as YJZX,b.BRXZ as BRXZ,to_char(a.KSSJ,'yyyy-mm-dd hh24:mi:ss') as KSSJ,to_char(a.QRSJ,'yyyy-mm-dd hh24:mi:ss') as QRSJ,to_char(a.TZSJ,'yyyy-mm-dd hh24:mi:ss') as TZSJ,a.SYPC as SYPC,a.YZZXSJ as YZZXSJ,a.LSYZ as LSYZ,a.SRCS as SRCS from JC_BRYZ a,JC_BRRY b where a.ZYH=b.ZYH and a.JGID=b.JGID and (a.JFBZ=1 or a.JFBZ=9) and a.XMLX>3 and a.SYBZ=0 and (a.LSBZ=0 or a.LSBZ=2) and a.YZPB=0 and (a.YSBZ=0 or (a.YSBZ = 1 and a.YSTJ = 1)) and (a.QRSJ<=:now or a.QRSJ is null) and a.JGID = :jgid and a.ZYH in (:zyhs) and (a.TJZX=0 or a.TJZX is null)");
//		if(body.containsKey("YZLX")&&body.get("YZLX")!=null){
//			int yzlx=MedicineUtils.parseInt(body.get("YZLX"));
//			if(yzlx==1){
//				hql.append(" and a.LSYZ=1");
//			}
//		}
		
		Map<String,Object> map_par=new HashMap<String,Object>();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		map_par.put("now", c.getTime());
		map_par.put("jgid", jgid);
		map_par.put("zyhs", list_zyhs);
		try {
		list_ret=dao.doSqlQuery(hql.toString(), map_par);
		SchemaUtil.setDictionaryMassageForList(list_ret, "phis.application.fsb.schemas.JC_BRYZ_XMTJ");
		filterData(list_ret, ctx);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "家床项目提交病人列表查询失败", e);
		} 
		return list_ret;
	}
	//从病区拿过来的过滤数据
	private void filterData(List<Map<String, Object>> result, Context ctx)
			throws ModelDataOperationException {
		if (result != null && result.size() > 0) {
			int ll_lsbz = 0, ll_zxcs = 0, ll_lsyz = 0;
			List<Map<String, Object>> listGY_SYPC = BSPHISUtil
					.u_his_share_yzzxsj(null, dao, ctx);
			Map<String, Object> parameters_cq = new HashMap<String, Object>();
			Map<String, Object> parameters_lsbz = new HashMap<String, Object>();
			Map<String, Object> map = null;
			for (int i = 0; i < result.size(); i++) {
				ll_zxcs = 0;
				map = result.get(i);
				parameters_lsbz = new HashMap<String, Object>();
				parameters_lsbz.put("ldt_kssj", map.get("KSSJ"));
				parameters_lsbz.put("ldt_qrsj", map.get("QRSJ"));
				parameters_lsbz.put("ldt_tzsj", map.get("TZSJ"));
				parameters_lsbz.put("ls_sypc", map.get("SYPC"));
				parameters_lsbz.put("ls_yzzxsj_str", map.get("YZZXSJ"));
				parameters_lsbz.put("ll_lsyz", map.get("LSYZ"));
				parameters_lsbz.put("al_ypbz", 0);
				parameters_lsbz.put("SRCS", map.get("SRCS"));
				ll_lsyz = Integer.parseInt(String.valueOf(map.get("LSYZ")));
				// 计算历史标志
				ll_lsbz = BSPHISUtil.uf_cacl_lsbz(listGY_SYPC, parameters_lsbz,
						dao, ctx);
				if (ll_lsbz == 1) {// 如果历史标志为1，则删除当前行
					parameters_lsbz.put("ll_yzxh", map.get("JLXH"));
					// 更新历史标志l
					uf_update_lsbz(parameters_lsbz, dao, ctx);
					result.remove(i);
					i--;
					continue;
				}
				if (ll_lsyz == 0) {// 长期医嘱
					parameters_cq = new HashMap<String, Object>();
					BSPHISUtil.uf_cacl_zxcs_cq(listGY_SYPC, parameters_lsbz,
							parameters_cq, dao, ctx);
					if (parameters_cq.get("al_zxcs") != null) {
						ll_zxcs = Integer.parseInt(String.valueOf(parameters_cq
								.get("al_zxcs")));
					}
				} else {// 临时医嘱
					for (int j = 0; j < listGY_SYPC.size(); j++) {
						if (String.valueOf(map.get("SYPC")).equals(
								listGY_SYPC.get(j).get("PCBM"))) {
							ll_zxcs = Integer.parseInt(listGY_SYPC.get(j).get(
									"MRCS")
									+ "");
						}
					}
				}
				if (ll_zxcs <= 0) {
					result.remove(i);
					i--;
				} else {
					double je = Double.parseDouble(String.valueOf(map
							.get("YPDJ")))
							* Double.parseDouble(String.valueOf(map.get("YCSL")));
					map.put("JE", je);
					map.put("C", ll_zxcs);
				}

			}
		}
	}
	
	public  void uf_update_lsbz(Map<String, Object> parameters,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		// User user = (User) ctx.get("user.instance");
		// String manageUnit = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();// 用户的机构ID
		try {
			Map<String, Object> parametersjlxh = new HashMap<String, Object>();
			if (parameters.get("ll_yzxh") != null) {
				parametersjlxh.put("JLXH",
						Long.parseLong(parameters.get("ll_yzxh") + ""));
			} else {
				parametersjlxh.put("JLXH", 0L);
			}
			parametersjlxh.put("JGID", manageUnit);
			dao.doUpdate(
					"update JC_BRYZ set LSBZ=1 where JLXH=:JLXH and JGID =:JGID and YZPB<>4",
					parametersjlxh);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("将指定医嘱转为历史医嘱失败!", e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-9
	 * @description 项目提交保存
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveProject(List<Map<String, Object>> body)throws ModelDataOperationException{
		StringBuffer hql=new StringBuffer();
		hql.append("update JC_BRYZ set ZXKS=:zxks where JLXH=:jlxh");
		for(Map<String,Object> map_bryz:body){
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("zxks", MedicineUtils.parseLong(map_bryz.get("ZXKS")));
			map_par.put("jlxh", MedicineUtils.parseLong(map_bryz.get("JLXH")));
			try {
				dao.doUpdate(hql.toString(), map_par);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "家床项目提交保存失败", e);
			}
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-9
	 * @description 项目提交
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> saveProjectSubmission(List<Map<String, Object>> body, Context ctx)throws ModelDataOperationException{
		Map<String,Object> map_ret=MedicineUtils.getRetMap();
		//存放同组的信息
		Map<String, List<Map<String, Object>>> rightGroup = new HashMap<String, List<Map<String,Object>>>();
		
		WardProjectModule model = new WardProjectModule(dao);
		List<Map<String, Object>> invalidList = model.queryUnInvalidProject(ctx);//获取所有作废的项目
		for(Map<String, Object> map : body){
			//校验执行科室不能为空
			if(map.get("ZXKS") == null || "".equals(map.get("ZXKS")) || "null".equals(map.get("ZXKS")) || "0".equals(map.get("ZXKS"))
					|| map.get("ZXKS_text") == null || "".equals(map.get("ZXKS_text")) || "null".equals(map.get("ZXKS_text"))){
				map_ret.put("code", "9000");
				map_ret.put("msg", "执行科室不能为空!");
				return map_ret;
			}
			//校验项目是否为作废项
			if(!model.checkInvalidProject(invalidList, map, map_ret)){
				map_ret.put("code", 9000);
				map_ret.put("msg", map_ret.get("x-response-msg"));
				//该项目为作废项，不进行提交
				return map_ret;
			}
			//判断是否已经在rightGroup中存在相同的组
			if(rightGroup.get(String.valueOf(map.get("YZZH"))) != null){
				map.put("YJZX", "0");
				rightGroup.get(String.valueOf(map.get("YZZH"))).add(map);
			}else{
				List<Map<String, Object>> list  = new ArrayList<Map<String,Object>>();
				map.put("YJZX", "1");//对于同一组的第一条，它的YJZX应为1
				list.add(map);
				rightGroup.put(String.valueOf(map.get("YZZH")), list);
			}
//			String je1 = (map.get("JE")+"").substring(0, (map.get("JE")+"").indexOf("x"));
//			String je2 = (map.get("JE")+"").substring((map.get("JE")+"").indexOf("x")+1,(map.get("JE")+"").length());
//			double JE = Double.parseDouble(je1)*Double.parseDouble(je2);
			map.put("JE", MedicineUtils.parseDouble(map.get("JE")));
			
		}
		//校验同组的执行科室是否相同
		if(!model.checkGroupIsEqualZXKS(rightGroup, map_ret)){
			//不相同直接返回，不进行提交
			map_ret.put("code", 9000);
			map_ret.put("msg", map_ret.get("msg"));
			return map_ret;
		}
		String msg = "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		StringBuffer hql_count=new StringBuffer();//判断医嘱是否已经提交
		hql_count.append("ZYH = :zyh and JLXH = :jlxh and SYBZ = 1  and JGID = :jgid");
		StringBuffer hql_count_t=new StringBuffer();
		hql_count_t.append(" ZYH = :zyh and JLXH = :jlxh and ( YSBZ = 0 or (YSBZ = 1 and YSTJ = 1) ) and JGID = :jgid");
		StringBuffer hql_update=new StringBuffer();
		hql_update.append("update JC_BRYZ set SYBZ = 1,YJXH = :yjxh,TJZX=1 where ZYH = :zyh and JLXH = :jlxh and ( YSBZ = 0 or (YSBZ = 1 and YSTJ = 1) ) and JGID = :jgid ");
		try {
		for(String key:rightGroup.keySet()){
			List<Map<String,Object>> list=rightGroup.get(key);
			Map<String,Object> map_temp=new HashMap<String,Object>();
			ArrearsPatientsQuery(list,ctx,map_temp);
			if (map_temp.get("RES_MESSAGE") != null&&!"".equals(MedicineUtils.parseString(map_temp.get("RES_MESSAGE")))) {
				map_ret.put("code", 9000);
				map_ret.put("msg",MedicineUtils.parseString(map_temp.get("RES_MESSAGE")));
				return map_ret;
			}
			long yjxh=0;
			for(Map<String,Object> map:list){
				if(yjxh==0){
					Map<String,Object> map_yj01=new HashMap<String,Object>();
					map_yj01.put("JGID", jgid);
					map_yj01.put("ZYH", MedicineUtils.parseLong(map.get("ZYH")));
					map_yj01.put("ZYHM",MedicineUtils.parseString(map.get("ZYHM")));// 住院号码
					map_yj01.put("BRXM",MedicineUtils.parseString(map.get("BRXM")));// 病人姓名
					map_yj01.put("KDRQ", new Date());// 开单日期
					map_yj01.put("TJSJ", new Date());// 提交时间
					map_yj01.put("KSDM",MedicineUtils.parseLong(map.get("BRKS")));// 科室代码，使用病人科室
					map_yj01.put("YSDM",MedicineUtils.parseString(map.get("YSGH")));// 医生代码，使用开嘱医生
					map_yj01.put("ZXKS",MedicineUtils.parseLong(map.get("ZXKS")));// 执行科室
					// 划价工号，使用操作工号
					map_yj01.put("HJGH", user.getUserId());// 划价工号，使用当前登录的提交医生
					map_yj01.put("DJZT", 1l);// 单据状态，使用默认1
					map_yj01.put("FYBQ",MedicineUtils.parseLong(map.get("SRKS")));// 费用病区，使用输入科室
					map_yj01.put("ZXPB", 0);// 执行判别默认给0
					map_yj01.put("ZFPB", 0);// 作废标志默认给0
					map_yj01 = dao.doSave("create", "phis.application.fsb.schemas.JC_YJ01",map_yj01, false);
					yjxh =MedicineUtils.parseLong(map_yj01.get("YJXH"));// 获取刚刚往YZ_ZY01插入的医技序号(YJXH)
				}
				Map<String,Object> map_par=new HashMap<String,Object>();
				map_par.put("zyh", MedicineUtils.parseLong(map.get("ZYH")));
				map_par.put("jlxh", MedicineUtils.parseLong(map.get("JLXH")));
				map_par.put("jgid",jgid);
				long count=dao.doCount("JC_BRYZ", hql_count.toString(), map_par);
				if (count == 0) {
					count = dao.doCount("JC_BRYZ", hql_count_t.toString(), map_par);
					if (count > 0) {
						// 更新病区记录
						map_par.put("yjxh", yjxh);
						dao.doUpdate(hql_update.toString(), map_par);
					}
					Map<String,Object> map_yj02=new HashMap<String,Object>();
					map_yj02.put("JGID", jgid);// 机构ID
					map_yj02.put("YJXH", yjxh);// 医技序号
					map_yj02.put("YLXH",MedicineUtils.parseLong(map.get("YPXH")));// 医疗序号，使用药品序号
					map_yj02.put("XMLX",MedicineUtils.parseInt(map.get("XMLX")));// 项目类型
					map_yj02.put("YJZX",MedicineUtils.parseInt(map.get("YJZX")));// 医技主项
					map_yj02.put("YLDJ", MedicineUtils.parseDouble(map.get("YPDJ")));// 医疗单价，使用药品单价
				//	double ycls =MedicineUtils.parseDouble(map.get("YCSL"));
				//	String cs =(map.get("JE")+"").substring((map.get("JE")+"").indexOf("x")+1,(map.get("JE")+"").length());// 在查询时已经计算好执行次数(界面显示金额后面的次数)
					map_yj02.put(
							"YLSL",MedicineUtils.parseDouble(map.get("YCSL")));
					// 根据费用序号获取费用归并
					long fygb = model.getFygbByFyxh(String.valueOf(map
							.get("YPXH")));
					map_yj02.put("FYGB", MedicineUtils.parseInt(fygb));// 费用归并
					Map<String, Object> map_t = new HashMap<String, Object>();
					map_t.put("TYPE", map.get("YPLX"));
					map_t.put("BRXZ", map.get("BRXZ"));
					map_t.put("FYXH", map.get("YPXH"));
					map_t.put("FYGB", fygb);
					// zfbl =
					// Double.parseDouble(String.valueOf(BSPHISUtil.getzfbl(body,
					// ctx, dao).get("ZFBL")))/100;
					double zfbl =MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(BSPHISUtil
							.getzfbl(map_t, ctx, dao).get("ZFBL")));
					map_yj02.put("ZFBL", zfbl);// 自负比例
					map_yj02.put("YZXH", MedicineUtils.parseLong(map.get("JLXH")));// 医嘱序号
					map_yj02.put("YEPB", 0);// 婴儿判别
					dao.doSave("create", "phis.application.fsb.schemas.JC_YJ02",
							map_yj02, true);
				}
			}
		}
		} catch (ValidateException e) {
		MedicineUtils.throwsException(logger, "家床项目提交失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "家床项目提交失败", e);
		}
		
		return map_ret;
	}
	
	//下面2个方法直接从病区那边拷贝过来的,改了下表名(病人是否欠费)
	public  boolean ArrearsPatientsQuery(
			List<Map<String, Object>> collardrugdetailslist, Context ctx, Map<String, Object> res)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		// 私有参数 是否管理收费（0不管，1管）
		String ZYQFKZ = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.JCQFKZ, ctx);
		int ll_qfkz = Integer.parseInt(ZYQFKZ);
		Map<String, Object> Ls_brxm = new HashMap<String, Object>();
		long ll_zyh = 0;
		String brxString = " ";
		String zyhString = " ";
		boolean flag = false;// 定义返回值，如果有超过余额的返回true 否则返回false
		for (int ll_row = 0; ll_row < collardrugdetailslist.size(); ll_row++) {
			Map<String, Object> rowMap = collardrugdetailslist.get(ll_row);
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("TYPE", rowMap.get("YPLX"));
			body.put("BRXZ", rowMap.get("BRXZ"));
			body.put("FYXH", rowMap.get("YPXH"));
			if (rowMap.get("FYGB") != null) {
				body.put("FYGB", rowMap.get("FYGB"));
			} else {
				body.put("FYGB", 0);
			}
			Map<String, Object> zfblMap = BSPHISUtil.getzfbl(body, ctx, dao);
			double zfbl = Double.parseDouble(zfblMap.get("ZFBL") + "");
			if (ll_qfkz > 0
					&& (rowMap.get("BRXZ") != null && !"6089".equals(rowMap
							.get("BRXZ").toString()))) {// 欠费管理 且不是市医保
				long ZYH = Long.parseLong(collardrugdetailslist.get(ll_row)
						.get("ZYH") + "");
				if ((ll_row == 0) || (ZYH != ll_zyh)) {
					ll_zyh = ZYH;
					double ld_hj = 0;
					for (int ll_row1 = ll_row; ll_row1 < collardrugdetailslist
							.size(); ll_row1++) {
						long ZYH1 = Long.parseLong(collardrugdetailslist.get(
								ll_row1).get("ZYH")
								+ "");
						double je = Double.parseDouble(collardrugdetailslist
								.get(ll_row1).get("JE") + "");
						if (ZYH1 == ll_zyh) {
							ld_hj += je;
						}
					}
					if (Wf_qfkz(ll_zyh, ld_hj * zfbl, jgid, Ls_brxm) == 1) {
						for (int ll_row1 = ll_row; ll_row1 < collardrugdetailslist
								.size(); ll_row1++) {
							long ZYH1 = Long.parseLong(collardrugdetailslist
									.get(ll_row1).get("ZYH") + "");
							if (collardrugdetailslist.get(ll_row).get("ok") != null) {
								String ok = collardrugdetailslist.get(ll_row)
										.get("ok") + "";
								if (ZYH1 == ll_zyh && ok.equals("1")) {
									collardrugdetailslist.get(ll_row1).put(
											"SFTJ", 1);
								}
							} else {
								if (ZYH1 == ll_zyh) {
									collardrugdetailslist.get(ll_row1).put(
											"SFTJ", 1);
								}
							}
						}
					} else {
						for (int ll_row1 = ll_row; ll_row1 < collardrugdetailslist
								.size(); ll_row1++) {
							long ZYH1 = Long.parseLong(collardrugdetailslist
									.get(ll_row1).get("ZYH") + "");
							if (collardrugdetailslist.get(ll_row).get("ok") != null) {
								String ok = collardrugdetailslist.get(ll_row)
										.get("ok") + "";
								if (ZYH1 == ll_zyh && ok.equals("1")) {
									collardrugdetailslist.get(ll_row1).put(
											"SFTJ", 0);
								}
							} else {
								if (ZYH1 == ll_zyh) {
									collardrugdetailslist.get(ll_row1).put(
											"SFTJ", 0);
								}
							}
						}
					}
				}
				res.put("RES_MESSAGE1", "");
				res.put("RES_ZYH", "");
				if (Ls_brxm.get("Ls_brxm") != null) {
					Map<String, Object> brxmMap = new HashMap<String, Object>();
					// int num = 0;
					try {
						for (int ll_row1 = ll_row + 1; ll_row1 < collardrugdetailslist
								.size(); ll_row1++) {
							long ZYH1 = Long.parseLong(collardrugdetailslist
									.get(ll_row1).get("ZYH") + "");
							if (ZYH1 == Long.parseLong(Ls_brxm.get("Ls_brxm")
									+ "")) {
								// num++;
								collardrugdetailslist.remove(ll_row1);
								ll_row1--;
							}

						}
						// if (num == 0) {
						brxmMap = dao.doLoad("phis.application.fsb.schemas.JC_BRRY",
								Long.parseLong(Ls_brxm.get("Ls_brxm") + ""));
						brxString += "'" + brxmMap.get("BRXM") + "'";
						zyhString += "'" + Ls_brxm.get("Ls_brxm") + "'";
						res.put("RES_MESSAGE1", brxString);
						res.put("RES_ZYH", zyhString);
						// }
						collardrugdetailslist.remove(ll_row);
						ll_row--;
						Ls_brxm.clear();
						if (brxString.length() > 0) {
							res.put("RES_MESSAGE", "病人  " + brxString
									+ " 预交款余额不足，不能确认!");
							flag = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("病人姓名查询失败！");
					}
				}
			}
		}
		return flag;
	}
	
	public  long Wf_qfkz(long al_zyh, double ad_fsje, String JGID,
			Map<String, Object> Ls_brxm) {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", JGID);
			parameters.put("ZYH", al_zyh);
			List<Map<String, Object>> jkhjList = dao
					.doQuery(
							"select sum(JKJE)as ld_jkhj From JC_TBKK Where JGID =:JGID and ZYH=:ZYH and ZFPB = 0 and JSCS = 0",
							parameters);
			List<Map<String, Object>> fyhjList = dao
					.doQuery(
							"Select sum(ZJJE) as ld_fyhj,sum(ZFJE) as ld_zfhj from JC_FYMX where JGID=:JGID and ZYH =:ZYH and JSCS = 0",
							parameters);
			double ld_jkhj = 0;
			double ld_zfhj = 0;
		//	double ld_fyhj = 0;
			if (jkhjList.size() > 0) {
				if (jkhjList.get(0) != null) {
					// System.out.println(jkhjList.get(0));
					ld_jkhj = Double.parseDouble(jkhjList.get(0) + "");
				}
			}
			if (fyhjList.size() > 0) {
//				if (fyhjList.get(0).get("ld_fyhj") != null) {
//					ld_fyhj = Double.parseDouble(fyhjList.get(0).get("ld_fyhj")
//							+ "");
//				}
				if (fyhjList.get(0).get("ld_zfhj") != null) {
					ld_zfhj = Double.parseDouble(fyhjList.get(0).get("ld_zfhj")
							+ "");
				}
			}
			double ld_qfje = ld_zfhj - ld_jkhj;
			String Ls_brxm_s = "";
			if ((ld_qfje >= 0) || (ld_qfje + ad_fsje > 0)) {
				long ls_brxm_new = al_zyh;
				if (Ls_brxm_s.equals("")) {
					Ls_brxm_s = ls_brxm_new + "";
				} else {
					Ls_brxm_s += "," + ls_brxm_new;
				}
				Ls_brxm.put("Ls_brxm", Ls_brxm_s);
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("费用控制查询失败！");
		}
		return 1;
	}
}
