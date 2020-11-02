package phis.application.fsb.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class FamilySickBedProjectExecutionModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedProjectExecutionModel.class);

	public FamilySickBedProjectExecutionModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-10
	 * @description 项目执行病人列表查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryProjectExecutionPatient(Map<String, Object> body, Context ctx) throws ModelDataOperationException {
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		StringBuffer hql=new StringBuffer();
		hql.append("select distinct a.ZYH as ZYH,b.ZYHM as ZYHM,b.BRXM as BRXM,b.BRXZ as BRXZ " +
				" from JC_BRYZ a, JC_BRRY b " +
				" WHERE  a.ZYH = b.ZYH   and (a.JFBZ = 2 OR a.JFBZ = 9) and (a.XMLX > 3) and a.LSBZ = 0 and a.YZPB = 0  and (a.SYBZ <> 1) and (a.QRSJ <=:qrsj or a.QRSJ is null) and (a.JGID =:jgid) and (a.TJZX=0 or a.TJZX is null) and (a.YSBZ = 0 or a.YSBZ = 1 and a.YSTJ = 1)");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("jgid", jgid);
		map_par.put("qrsj", new Date());
		if(body.containsKey("ZYH")){
			hql.append(" and a.ZYH=:zyh");
			map_par.put("zyh", MedicineUtils.parseLong(body.get("ZYH")));
		}
		try {
			List<Map<String,Object>> list= dao.doSqlQuery(
					hql.toString(), map_par);
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> inof = list.get(i);
					Map<String,Object> map_temp=new HashMap<String,Object>();
					doDetailChargeQuery(inof, map_temp, ctx);
					if (map_temp.get("body") != null) {
						if (((List<Map<String, Object>>) map_temp.get("body"))
								.size() > 0) {
							list_ret.add(inof);
						}
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "项目执行病人查询失败", e);
		}
		return list_ret;
	}
	
	
	
	//查询明细,从病区拿过来的 由于逻辑太复杂 没有全部重写
	@SuppressWarnings("unchecked")
	public void doDetailChargeQuery(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		String zyhString = "";
		if(body.containsKey("ZYHS")){
			zyhString += " and (t.ZYH in(:zyhs)) ";
			List<Object> l=(List<Object>)body.get("ZYHS");
			List<Long> zyhs=new ArrayList<Long>();
			for(Object o:l){
				zyhs.add(MedicineUtils.parseLong(o));
			}
			parameters.put("zyhs",zyhs);
		}
		if(body.containsKey("JLXHS")){//打印用
			zyhString += " and (t.JLXH in(:jlxhs)) ";
			List<Object> l=(List<Object>)body.get("JLXHS");
			List<Long> jlxhs=new ArrayList<Long>();
			for(Object o:l){
				jlxhs.add(MedicineUtils.parseLong(o));
			}
			parameters.put("jlxhs",jlxhs);
		}
		String date = BSHISUtil.getDate();
		Date QRSJ = BSHISUtil.toDate(date);// 确认时间
		parameters.put("JGID", JGID);
		parameters.put("QRSJ", QRSJ);

		try {
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT t.YSGH as YSGH,t.YZMC as YZMC,t.YPDJ as YPDJ,t.MRCS as MRCS,t.YCSL as YCSL,' ' AS je,'0' AS OK,");
			sql_list.append("t.SYBZ as SYBZ,t.YPCD as YPCD,t.SRKS as SRKS,to_char(t.QRSJ,'YYYY-MM-DD HH24:MI:SS')as QRSJ, t.YPXH as YPXH, t.YPLX as YPLX, t.ZYH as ZYH, t.JLXH as JLXH,");
			sql_list.append("to_char(t.KSSJ,'YYYY-MM-DD HH24:MI:SS') as KSSJ,t.YZZH as YZXH,to_char(t.TZSJ,'YYYY-MM-DD HH24:MI:SS') as TZSJ, t.MZCS MZCS,t.XMLX as XMLX, t.CZGH as CZGH, t.ZXKS as ZXKS, t.YEPB as YEPB,");
			sql_list.append("t.LSBZ as LSBZ,t.LSYZ as LSYZ, t.YZZH as YZZH, t.ZFPB as ZFPB,t1.BRXM as BRXM,t1.BRXZ as BRXZ,");
			sql_list.append("t.BRKS as BRKS, t.SYPC as SYPC,0 AS ts,0 AS FYCS, ");
			sql_list.append("t.YSTJ as YSTJ,t.SRCS as SRCS, t.YZZXSJ as YZZXSJ,t1.ZYHM as ZYHM FROM ");
			sql_list.append("JC_BRYZ t, ");
			sql_list.append("JC_BRRY t1 ");
			sql_list.append("WHERE t.ZYH = t1.ZYH and t1.CYPB = 0 and (t.JFBZ = 2 or t.JFBZ = 9) and t.XMLX > 3 and t.LSBZ = 0 and t.YZPB = 0  ");
			sql_list.append("and (t.SYBZ <> 1)  and (t.YSBZ = 0 or (t.YSBZ = 1 and t.YSTJ = 1)) and ZFBZ = 0 ");
			sql_list.append("and(t.QRSJ <=:QRSJ or t.QRSJ IS NULL) and t.JGID =:JGID and (t.TJZX=0 or t.TJZX is null)  ");
			sql_list.append(zyhString);
			sql_list.append(" ORDER BY t.ZYH ASC ");

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			int FJBZ = 0; // 为了区分附加计价执行
			if (inofList.size() > 0) {
				List<Map<String, Object>> listGY_SYPC = BSPHISUtil
						.u_his_share_yzzxsj(parameters, dao, ctx);
				uf_comp_yzzx(listGY_SYPC, inofList, ctx);
				for (int i = 0; i < inofList.size(); i++) {
					double FYCS = Double.parseDouble(inofList.get(i)
							.get("FYCS") + "");
					double YCSL = Double.parseDouble(inofList.get(i)
							.get("YCSL") + "");
					double YPDJ = Double.parseDouble(inofList.get(i)
							.get("YPDJ") + "");

					int LSBZ = Integer.parseInt(inofList.get(i).get("LSBZ")
							+ "");
					String QRSJ1 = inofList.get(i).get("QRSJ") + "";
					if (FYCS == 0) {
						inofList.remove(i);
						i--;
					} else {
						inofList.get(i).put("FYCS", FYCS);
						inofList.get(i).put("LSBZ", LSBZ);
						inofList.get(i).put("QRSJ", QRSJ1);
						inofList.get(i).put("JE", (FYCS * YCSL * YPDJ));
						inofList.get(0).put("FJBZ", FJBZ);
					}
				}
			}
			res.put("body", inofList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "刷新失败！");
		}
	}
	
	public  boolean uf_comp_yzzx(List<Map<String, Object>> listGY_SYPC,
			List<Map<String, Object>> projectList, Context ctx)
			throws ModelDataOperationException {
		try {
			SimpleDateFormat sdfdatetime = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
//			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
//			long ll_yzzh_old = 0;
//			int fysx = 0;
//			int ll_jfbz = 0;
//			Date currentDate = null;
			/**
			 * 2013-08-20 modify by gejj
			 * 在病区项目执行中的附加计价显示时，计费标识为9时未进行过滤，通过张伟确认现修改如下代码 1、增加ll_yjxh变量
			 * 2、在sqlString中增加, min(YJXH) as LL_YJXH 3、增加ll_yjxh =
			 * Long.parseLong(list_fysx.get(0).get("LL_YJXH") + "");代码 4、将原有的if
			 * (ll_jfbz == 1) { 修改为if (ll_jfbz == 1 || (ll_yjxh > 0 && ll_jfbz
			 * == 9)) { 5、将原有的LL_JFBZ小写改成大小(共三处)
			 * **/
		//	long ll_yjxh = 0;
			for (int i = 0; i < projectList.size(); i++) {
				Map<String, Object> parameters_lsbz = new HashMap<String, Object>();
				Map<String, Object> parameters_up_lsbz = new HashMap<String, Object>();
				Map<String, Object> parameters_cq = new HashMap<String, Object>(); // 作为uf_cacl_zxcs_cq
																					// 的返回值

				String QRSJ = null;
				if (projectList.get(i).get("QRSJ") != null
						&& projectList.get(i).get("QRSJ") != "") {
					QRSJ = projectList.get(i).get("QRSJ") + "";// 确认时间
				}
				String TZSJ = null;
				if (projectList.get(i).get("TZSJ") != null
						&& projectList.get(i).get("TZSJ") != "") {
					TZSJ = projectList.get(i).get("TZSJ") + "";// 停医嘱时间
				}
				String KSSJ = null;
				if (projectList.get(i).get("KSSJ") != null
						&& projectList.get(i).get("KSSJ") != "") {
					KSSJ = projectList.get(i).get("KSSJ") + "";// 开始时间
				}
				String YZZXSJ = projectList.get(i).get("YZZXSJ") + "";// 医嘱执行时间
				String SYPC = projectList.get(i).get("SYPC") + "";// 使用频次
				long YZXH = Long.parseLong(projectList.get(i).get("JLXH") + "");// 医嘱序号
				int LSYZ = Integer
						.parseInt(projectList.get(i).get("LSYZ") + "");// 临时医嘱//
																		// 1,长期医嘱
																		// 0

				int SRCS = 0;
				if (projectList.get(i).get("SRCS") != null) {
					SRCS = Integer
							.parseInt(projectList.get(i).get("SRCS") + "");// 首日次数
				}
			//	int FJBZ = 0; // 附加计价标志
//				if (projectList.get(0).get("FJBZ") != null) {
//					FJBZ = Integer
//							.parseInt(projectList.get(0).get("FJBZ") + "");// 首日次数
//				}

				int ZXCS_TOTAL = 0;// 执行次数

				parameters_lsbz.put("ldt_kssj", KSSJ);
				parameters_lsbz.put("ldt_qrsj", QRSJ);
				parameters_lsbz.put("ldt_tzsj", TZSJ);
				parameters_lsbz.put("ls_sypc", SYPC);
				parameters_lsbz.put("ls_yzzxsj_str", YZZXSJ);
				parameters_lsbz.put("ll_lsyz", LSYZ);
				parameters_lsbz.put("al_ypbz", 0);
				parameters_lsbz.put("SRCS", SRCS);
				// 得到历史标志
				int ll_lsbz = BSPHISUtil.uf_cacl_lsbz(listGY_SYPC, parameters_lsbz, dao,
						ctx);

				if (ll_lsbz == 1) {// 在执行前已经不再需要执行,即可置为历史医嘱
					parameters_up_lsbz.put("ll_yzxh", YZXH);
					uf_update_lsbz(parameters_up_lsbz, dao, ctx); // 更新历史标志
					projectList.get(i).put("FYCS", 0);
					continue;
				}

				
				if (LSYZ == 0) {// 长期医嘱
					ZXCS_TOTAL =BSPHISUtil.uf_cacl_zxcs_cq(listGY_SYPC, parameters_lsbz,
							parameters_cq, dao, ctx);

					double al_zxcs = 0;
					if (parameters_cq.get("al_zxcs") != null) {
						al_zxcs = Double.parseDouble(parameters_cq
								.get("al_zxcs") + "");
					}
					projectList.get(i).put("FYCS", al_zxcs);
					if (ZXCS_TOTAL > 0) {
						String currentTime = null;
						if (parameters_cq.get("currentTime") != null) {
							currentTime = sdfdatetime.format(parameters_cq
									.get("currentTime"));
						}
						// 当前最大时间放入表中
						projectList.get(i).put("QRSJ", currentTime);
						// 把最大时间当做QRSJ传入 重新获取 ll_lsbz(历史标志)
						parameters_lsbz.put("ldt_qrsj", currentTime);
						ll_lsbz = BSPHISUtil.uf_cacl_lsbz(listGY_SYPC, parameters_lsbz,
								dao, ctx);
						if (ll_lsbz == 1) {
							projectList.get(i).put("LSBZ", 1);
						}
					}
				} else { // 临时医嘱
					// 得到频次的每日次数
					int count_MRCS = 0;
					for (int j = 0; j < listGY_SYPC.size(); j++) {
						if (SYPC.equals(listGY_SYPC.get(j).get("PCBM"))) {
							count_MRCS = Integer.parseInt(listGY_SYPC.get(j)
									.get("MRCS") + "");
						}
					}
					if (count_MRCS > 0) {
						projectList.get(i).put("QRSJ", KSSJ);
						projectList.get(i).put("LSBZ", 1);
					}
					projectList.get(i).put("FYCS", count_MRCS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException("时间转换失败!", e);
		}

		return true;
	}
	
	// 将指定医嘱转为历史医嘱
		/**
		 * 入参：parameters里的参数 long al_yzxh 医嘱序号
		 */
		public  void uf_update_lsbz(Map<String, Object> parameters,
				BaseDAO dao, Context ctx) throws ModelDataOperationException {
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
		 * @createDate 2015-3-11
		 * @description 项目执行
		 * @updateInfo
		 * @param body
		 * @param ctx
		 * @throws ModelDataOperationException
		 */
		public void saveProjectExecution(List<Map<String, Object>> body,Map<String, Object> res ,Context ctx) throws ModelDataOperationException {
			try {
				UserRoleToken user = UserRoleToken.getCurrent();
				String JGID = user.getManageUnitId();// 用户的机构ID
				long ZYH = 0;
				if (body != null && body.size() > 0
						&& body.get(0).get("ZYH") != null) {
					ZYH = Long.parseLong(body.get(0).get("ZYH") + "");
				}

				Map<String, Object> parameters = new HashMap<String, Object>();
				String zyhString = "";
				if (ZYH != 0) {
					parameters.put("ZYH", ZYH);
					zyhString = "and (t.ZYH =:ZYH) ";
				}
				String date = BSHISUtil.getDate();
				Date QRSJ_new = BSHISUtil.toDate(date);// 确认时间
				parameters.put("JGID", JGID);
				parameters.put("QRSJ", QRSJ_new);
				// 返回list的查询语句
				StringBuffer sql_list = new StringBuffer(
						"SELECT t.YSGH as YSGH,t.YZMC as YZMC,t.YPDJ as YPDJ,t.MRCS as MRCS,t.YCSL as YCSL,' ' AS je,'0' AS OK,");
				sql_list.append("t.SYBZ as SYBZ,t.YPCD as YPCD,t.SRKS as SRKS,to_char(t.QRSJ,'YYYY-MM-DD HH24:MI:SS')as QRSJ, t.YPXH as YPXH, t.YPLX as YPLX, t.ZYH as ZYH, t.JLXH as JLXH,");
				sql_list.append("to_char(t.KSSJ,'YYYY-MM-DD HH24:MI:SS')as KSSJ,t.YZZH as YZXH,to_char(t.TZSJ,'YYYY-MM-DD HH24:MI:SS')as TZSJ, t.MZCS MZCS,t.XMLX as XMLX, t.CZGH as CZGH, t.ZXKS as ZXKS, t.YEPB as YEPB,");
				sql_list.append("t.LSBZ as LSBZ,t.LSYZ as LSYZ, t.YZZH as YZZH, t.ZFPB as ZFPB, t1.BRXZ as BRXZ,");
				sql_list.append("t.BRKS as BRKS, t.SYPC as SYPC,0 AS ts,0 AS FYCS,");
				sql_list.append("t.YSTJ as YSTJ,t.SRCS as SRCS, t.YZZXSJ as YZZXSJ FROM ");
				sql_list.append("JC_BRYZ t, ");
				sql_list.append("JC_BRRY t1 ");
				sql_list.append("WHERE  t.ZYH = t1.ZYH and t1.CYPB = 0 and (t.JFBZ = 2 or t.JFBZ = 9) and t.XMLX > 3 and t.LSBZ = 0 and t.YZPB = 0  ");
				sql_list.append("and (t.SYBZ <> 1)  and (t.YSBZ = 0 or (t.YSBZ = 1 and t.YSTJ = 1)) and ZFBZ = 0 ");
				sql_list.append(" and(t.QRSJ <=:QRSJ or t.QRSJ IS NULL) and t.JGID =:JGID  ");
				sql_list.append(zyhString);
				sql_list.append(" ORDER BY t.ZYH ASC ");

				List<Map<String, Object>> inofList = dao.doSqlQuery(
						sql_list.toString(), parameters);
				int FJBZ = 0; // 为了区分附加计价执行
				if (inofList.size() > 0) {
					List<Map<String, Object>> listGY_SYPC = BSPHISUtil
							.u_his_share_yzzxsj(null, dao, ctx);
					uf_comp_yzzx(listGY_SYPC, inofList, ctx);
					for (int i = 0; i < inofList.size(); i++) {
						double FYCS = Double.parseDouble(inofList.get(i)
								.get("FYCS") + "");
						double YCSL = Double.parseDouble(inofList.get(i)
								.get("YCSL") + "");
						double YPDJ = Double.parseDouble(inofList.get(i)
								.get("YPDJ") + "");

						int LSBZ = Integer.parseInt(inofList.get(i).get("LSBZ")
								+ "");
						String QRSJ1 = inofList.get(i).get("QRSJ") + "";
						if (FYCS == 0) {
							inofList.remove(i);
							i--;
						} else {
							inofList.get(i).put("FYCS", FYCS);
							inofList.get(i).put("LSBZ", LSBZ);
							inofList.get(i).put("QRSJ", QRSJ1);
							inofList.get(i).put("JE", ( YCSL * YPDJ));
							inofList.get(0).put("FJBZ", FJBZ);
						}
					}
				}
				/** 2013-08-13 modify by gejj 直接将主项和附加项合并为一个list中然后进行判断是否超过余额 **/
				// 判断欠费病人
				List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
				tmpList.addAll(body);
				if(tmpList!=null&&tmpList.size()>0){
					for(Map<String,Object> m:tmpList){
						m.put("JE", MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(m.get("YCSL"))*MedicineUtils.parseDouble(m.get("YPDJ"))));
					}
				}
				boolean flag = BSPHISUtil.FSBArrearsPatientsQuery(tmpList, ctx, dao,
						res);// 2013-08--13 修改使用body判断,infoList中为全部医技项目
				if (flag) {// 重新过滤将不需要执行的病人项目去除！(根据住院号判断)
					boolean f = true;
					Map<String, Object> tmpMap = null;
					for (int i = 0; i < body.size(); i++) {
						tmpMap = body.get(i);
						f = true;
						for (Map<String, Object> map : tmpList) {
							if (tmpMap.get("ZYH").equals(map.get("ZYH"))) {
								f = false;
								continue;
							}
						}
						if (f) {
							res.put("code", 9000);
							res.put("msg", res.get("RES_MESSAGE"));
							body.remove(i);
							i--;
						}
					}
					// 主项和附加项都没有时直接返回
					if ((body == null || body.size() == 0)) {
						return;
					}
				}
				/** 2013-08-13 end **/
				List<Map<String, Object>> listForputFYMX = new ArrayList<Map<String, Object>>();
 
				uf_insert_jc_fymx(body, listForputFYMX, dao, ctx);
				boolean tag=false;//用于判断是否有项目被执行(先打开执行界面,然后提交项目,不刷新执行界面直接提交)
				for (int i = 0; i < body.size(); i++) {
					Map<String, Object> parametersForputFYMX = listForputFYMX
							.get(i);
					Map<String, Object> parameters_update = new HashMap<String, Object>();
					for (int j = 0; j < inofList.size(); j++) {
						long JLXH = Long
								.parseLong(inofList.get(j).get("JLXH") + "");
						long JLXH_l = Long.parseLong(body.get(i).get("JLXH") + "");
						String QRSJ = inofList.get(j).get("QRSJ") + "";
						int LSBZ = Integer.parseInt(inofList.get(j).get("LSBZ")
								+ "");
						parameters_update.put("JLXH", JLXH);
						parameters_update.put("QRSJ", BSHISUtil.toDate(QRSJ));
						parameters_update.put("LSBZ", LSBZ);

						if (JLXH == JLXH_l) {
							dao.doUpdate(
									"update JC_BRYZ set QRSJ=:QRSJ,LSBZ=:LSBZ,TJZX=2 where JLXH =:JLXH",
									parameters_update);
							dao.doSave("create", "phis.application.fsb.schemas.JC_FYMX",
									parametersForputFYMX, false);
							tag=true;
						}
					}
				}
				if(!tag){
					throw new ModelDataOperationException(9000,"没有需要执行的项目！");
				}
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "确认提交医嘱失败!", e);
			} catch (ParseException e) {
				MedicineUtils.throwsException(logger, "时间转换失败!", e);
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "确认提交医嘱失败!", e);
			} catch (ServiceException e) {
				MedicineUtils.throwsException(logger, "确认提交医嘱失败!", e);
			}
		}
		
		public static boolean uf_insert_jc_fymx(
				List<Map<String, Object>> list_FYMX,
				List<Map<String, Object>> listForputFYMX, BaseDAO dao, Context ctx)
				throws ModelDataOperationException, ServiceException,
				ParseException {
			// User user = (User) ctx.get("user.instance");
			// String JGID = user.get("manageUnit.id");
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();// 用户的机构ID
			long BQ = 0;
//			if (user.getProperty("wardId") != null) {
//				// BQ = Long.parseLong(user.getProperty("wardId"));// 当前病区
//				BQ = Long.parseLong(user.getProperty("wardId") + "");// 当前病区
//			} else {
//				if (list_FYMX != null && list_FYMX.size() > 0)
//					BQ = Long.parseLong(String
//							.valueOf(list_FYMX.get(0).get("FYBQ")));
//			}
			String YGGH = user.getUserId();// 当前操作员工号
			try {
				for (int i = 0; i < list_FYMX.size(); i++) {
					long ZXKS = 0;
					double ZFBL = 0;
					double ZFJE = 0;
					double ZLJE = 0;
					double DZBL = 0;
					double ZJJE = 0;
					if (list_FYMX.get(i).get("ZFBL") != null) {
						ZFBL =MedicineUtils.parseDouble(list_FYMX.get(i).get("ZFBL")) ;
					}
					if (list_FYMX.get(i).get("ZFJE") != null) {
						ZFJE =MedicineUtils.parseDouble(list_FYMX.get(i).get("ZFJE")) ;
					}
					if (list_FYMX.get(i).get("ZLJE") != null) {
						ZLJE =MedicineUtils.parseDouble(list_FYMX.get(i).get("ZLJE")) ;
					}
					if (list_FYMX.get(i).get("DZBL") != null) {
						DZBL =MedicineUtils.parseDouble(list_FYMX.get(i).get("DZBL")) ;
					}
					if (list_FYMX.get(i).get("ZJJE") != null) {
						ZJJE =MedicineUtils.parseDouble(list_FYMX.get(i).get("ZJJE")) ;
					}
					long ZYH =MedicineUtils.parseLong(list_FYMX.get(i).get("ZYH"));
					long FYKS =MedicineUtils.parseLong(list_FYMX.get(i).get("FYKS")) ;
					int YPLX =MedicineUtils.parseInt(list_FYMX.get(i).get("YPLX"));
					long BRXZ =MedicineUtils.parseLong(list_FYMX.get(i).get("BRXZ"));
					double FYDJ =MedicineUtils.parseDouble(list_FYMX.get(i).get("YPDJ"));
					double FYSL =MedicineUtils.parseDouble(list_FYMX.get(i).get("YCSL")) ;
					String YSGH = list_FYMX.get(i).get("YSGH") + "";
					long YPCD =MedicineUtils.parseLong(list_FYMX.get(i).get("YPCD"));
					// 执行科室为空时 默认为费用科室
					if (list_FYMX.get(i).get("ZXKS") == null
							|| list_FYMX.get(i).get("ZXKS") == ""
							|| list_FYMX.get(i).get("ZXKS").equals("null")) {
						ZXKS = FYKS;
					} else {
						ZXKS = Long.parseLong(list_FYMX.get(i).get("ZXKS") + "");
					}
					// 判断主治医生是否为空
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("ZYH", ZYH);
					parameters.put("JGID", JGID);
					Map<String, Object> map_ZSYS = dao
							.doLoad("SELECT ZRYS as YSGH From JC_BRRY where JGID = :JGID and ZYH = :ZYH",
									parameters);
					if (map_ZSYS.get("YSGH") != null) {
						YSGH = map_ZSYS.get("YSGH") + "";
					}
					// 住院费用明细表的用于插入的Map
					Map<String, Object> zyfymx_map = (Map<String, Object>) list_FYMX
							.get(i);
					// 费用性质 YPLX_c 参数药品类型。
					long YPLX_c = Long.parseLong(list_FYMX.get(i).get("YPLX") + "");
					long FYXH = Long.parseLong(list_FYMX.get(i).get("YPXH") + "");
					long FYXM = BSPHISUtil.getfygb(YPLX_c, FYXH, dao, ctx);
					zyfymx_map.put("FYXM", FYXM);
					// YPLX 为0表示费用
					if (YPLX == 0) {
						if (FYSL < 0) {
							zyfymx_map.put("ZFBL", ZFBL);
							zyfymx_map.put("ZJJE", ZJJE);
							zyfymx_map.put("ZFJE", ZFJE);
							zyfymx_map.put("ZLJE", ZLJE);
						} else {
							if (ZFJE > 0) {
								zyfymx_map.put("ZFBL", ZFBL);
								zyfymx_map.put("ZJJE", ZJJE);
								zyfymx_map.put("ZFJE", ZFJE);
								zyfymx_map.put("ZLJE", ZLJE);
							} else {
								Map<String, Object> FYXX = BSPHISUtil.getje(YPLX, BRXZ, FYXH,
										FYXM, FYDJ, FYSL, dao, ctx);
								zyfymx_map.put("ZFBL", FYXX.get("ZFBL"));
								zyfymx_map.put("ZJJE", FYXX.get("ZJJE"));
								zyfymx_map.put("ZFJE", FYXX.get("ZFJE"));
								zyfymx_map.put("ZLJE", FYXX.get("ZLJE"));
							}
						}
					} else {
						// 否则就是药品
						if (FYSL < 0) {
							zyfymx_map.put("ZFBL", ZFBL);
							zyfymx_map.put("ZJJE", ZJJE);
							zyfymx_map.put("ZFJE", ZFJE);
							zyfymx_map.put("ZLJE", ZLJE);
						} else {
							if (ZFJE > 0) {
								zyfymx_map.put("ZFBL", ZFBL);
								zyfymx_map.put("ZJJE", ZJJE);
								zyfymx_map.put("ZFJE", ZFJE);
								zyfymx_map.put("ZLJE", ZLJE);
								zyfymx_map.put("FYXM", FYXM);
							} else {
								Map<String, Object> FYXX = BSPHISUtil.getje(YPLX, BRXZ, FYXH,
										FYXM, FYDJ, FYSL, dao, ctx);
								zyfymx_map.put("ZFBL", FYXX.get("ZFBL"));
								zyfymx_map.put("ZJJE", FYXX.get("ZJJE"));
								zyfymx_map.put("ZFJE", FYXX.get("ZFJE"));
								zyfymx_map.put("ZLJE", FYXX.get("ZLJE"));
								zyfymx_map.put("FYXM", FYXX.get("FYGB"));
							}
						}
					}
					// 判断发药日期是否为空
					if (list_FYMX.get(i).get("FYRQ") == null
							|| list_FYMX.get(i).get("FYRQ") == "") {
						zyfymx_map.put("FYRQ", new Date());
					} else {
						String FYRQ = list_FYMX.get(i).get("FYRQ") + "";
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date date = list_FYMX.get(i).get("FYRQ") instanceof Date ? (Date) list_FYMX
								.get(i).get("FYRQ") : sdf.parse(FYRQ);
						zyfymx_map.put("FYRQ", date);
					}
					// 判断计费日期是否为空
					if (list_FYMX.get(i).get("JFRQ") == null
							|| list_FYMX.get(i).get("JFRQ") == "") {
						zyfymx_map.put("JFRQ", new Date());
					} else {
						String JFRQ = list_FYMX.get(i).get("JFRQ") + "";
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date date = list_FYMX.get(i).get("JFRQ") instanceof Date ? (Date) list_FYMX
								.get(i).get("JFRQ") : sdf.parse(JFRQ);
						zyfymx_map.put("JFRQ", date);
					}
					// 判断药品类型
					if (YPLX == 0) {
						zyfymx_map.put("YPCD", 0);
					} else {
						zyfymx_map.put("YPCD", YPCD);
					}
					if (list_FYMX.get(i).get("JLXH") != null) {
						zyfymx_map.put("YZXH",
								Long.parseLong(list_FYMX.get(i).get("JLXH") + ""));// 医嘱序号
					}
					zyfymx_map.put("ZYH", ZYH);// 住院号
					zyfymx_map.put("FYXH", FYXH);// 发药序号
					zyfymx_map.put("FYMC", list_FYMX.get(i).get("YZMC"));// 费用名称
					zyfymx_map.put("FYSL", FYSL);// 发药数量
					zyfymx_map.put("FYDJ", FYDJ);// 发药单价
					zyfymx_map.put("QRGH", YGGH);// 当前操作员工号
					zyfymx_map.put("FYKS", FYKS);// 费用科室 long
					zyfymx_map.put("ZXKS", ZXKS);// 执行科室 long
					zyfymx_map.put("XMLX", 4);// 项目类型// int
					zyfymx_map.put("YPLX", YPLX);// 药品类型
					zyfymx_map.put("YSGH", YSGH);// 医生工号
					zyfymx_map.put("FYBQ", BQ);// 费用病区 long
					zyfymx_map.put("DZBL", DZBL);
					zyfymx_map.put("JSCS", 0);
					zyfymx_map.put("YEPB", 0);
					zyfymx_map.put("JGID", JGID);
					zyfymx_map.put("JFRQ", new Date());
					listForputFYMX.add(i, zyfymx_map);
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
				throw new RuntimeException("确认失败！");
			}
			return true;
		}
}
