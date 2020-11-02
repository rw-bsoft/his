package phis.application.reg.ws;

import java.sql.Connection;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.source.ModelDataOperationException;
import phis.source.service.ServiceCode;

public class HyglModel {
	protected Logger logger = LoggerFactory.getLogger(HyglModel.class);

	// 创建静态全局变量     
    public static Connection conn;
    public static Statement st;
    public static final int CODE_RECORD_NOT_FOUND = 5506;
	public static final int CODE_UNKNOWN_ERROR = 6000;
	public static final int CODE_DATABASE_ERROR = 7000;
//	/**
//	 * 生成对应值班类别的科室号源记录
//	 * 
//	 * @author caijy
//	 * @createDate 2016-10-12
//	 * @description
//	 * @updateInfo
//	 * @param body
//	 *            科室排班数据,必须有的字段:GHKS,ZBLB,KSPB,GHRQ
//	 */
//	public void saveHyForDepartment(List<Map<String, Object>> body, Context ctx)
//			throws ModelDataOperationException {
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String jgid = user.getManageUnit().getId();// 用户的机构ID
//		// 当前机构所有挂号科室数据
//		List<Map<String, Object>> list_ghks = null;
//		try {
//			list_ghks = dao.doList(
//					CNDHelper.createSimpleCnd("eq", "JGID", "s", jgid), null,
//					"phis.application.reg.schemas.MS_GHKS");
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "查询挂号科室失败:" + e.getMessage(),
//					e);
//		}
//		if (list_ghks == null || list_ghks.size() == 0) {
//			return;
//		}
//		String gzsj_sw = MedicineUtils.parseString(ParameterUtil.getParameter(
//				jgid, BSPHISSystemArgument.SWGZSJ, ctx));
//		String gzsj_xw = MedicineUtils.parseString(ParameterUtil.getParameter(
//				jgid, BSPHISSystemArgument.XWGZSJ, ctx));
//		StringBuffer hql_dehy = new StringBuffer();// 删除该日期以及往后对应周的号源数据
//		hql_dehy.append("delete from MS_HYB where GHKS=:ghks and ZBLB=:zblb and to_char(GZRQ,'yyyy-mm-dd') in (:gzrq) and JGID=:jgid");
//		StringBuffer hql_count = new StringBuffer();// 查询是否已经有号源
//		hql_count.append(" GHKS=:ksdm and ZBLB=:zblb and GZRQ=:gzrq");
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
//		for (Map<String, Object> map_kspb : body) {
//			long ghks = MedicineUtils.parseLong(map_kspb.get("GHKS"));
//			int zblb = MedicineUtils.parseInt(map_kspb.get("ZBLB"));
//			String gzsj = gzsj_sw;
//			if (zblb == 2) {
//				gzsj = gzsj_xw;
//			}
//			// 查询对应的挂号科室信息
//			Map<String, Object> map_ghks = MedicineUtils.getRecord(list_ghks,
//					new String[] { "KSDM" }, map_kspb, new String[] { "GHKS" });
//			if (map_ghks == null || map_ghks.size() == 0) {
//				throw new ModelDataOperationException(9000, "未找到对应的挂号科室,GHKS="
//						+ map_kspb.get("GHKS"));
//			}
//			if (MedicineUtils.parseInt(map_ghks.get("YYBZ")) == 0) {// 科室没开启预约,不处理
//				continue;
//			}
//			// 科室不排版,则删除科室下的所有号源(包括医生号源)
//			if (MedicineUtils.parseInt(map_kspb.get("KSPB")) == 0) {
//				// 存放对应周的日期以及期限下所有该日立周的日期
//				List<String> ghrq = new ArrayList<String>();
//				int yycsts = MedicineUtils.parseInt(map_ghks.get("YYCSTS"));// 预约产生天数
//				Calendar c = Calendar.getInstance();
//				// 获取预约产生天数下 所有该日立周的日期
//				for (int i = 0; i < yycsts; i++) {
//					if (i > 0) {
//						c.add(Calendar.DATE, 1);
//					}
//					// c的日立周和挂号日期一样,缓存日期
//					if (c.get(Calendar.DAY_OF_WEEK) == MedicineUtils
//							.parseInt(map_kspb.get("GHRQ"))) {
//						ghrq.add(sdf.format(c.getTime()));
//					}
//				}
//				if(ghrq.size()==0){
//					continue;
//				}
//				Map<String, Object> map_par = new HashMap<String, Object>();
//				map_par.put("gzrq", ghrq);
//				map_par.put("ghks", ghks);
//				map_par.put("zblb", zblb);
//				map_par.put("jgid", jgid);
//				try {
//					dao.doSqlUpdate(hql_dehy.toString(), map_par);
//				} catch (PersistentDataOperationException e) {
//					MedicineUtils.throwsException(logger,
//							"删除号源失败:" + e.getMessage(), e);
//				}
//			} else {// 科室排班,如果科室预约开启,生成号源数据
//				map_ghks.put("GZSJ", gzsj);
//				// 获取号源总数
//				Map<String, Object> map_hyzs = RegistrationUtil.getHyzs(
//						map_ghks, 1, ctx);
//				double hyzs = MedicineUtils.parseDouble(map_hyzs.get("HYZS"));// 号源总数
//				double ghxe = MedicineUtils.parseDouble(map_hyzs.get("GHXE"));// 挂号限额
//				double yyxe = MedicineUtils.parseDouble(map_hyzs.get("YYXE"));// 预约限额
//				double pjkzsj = MedicineUtils.parseDouble(map_hyzs
//						.get("PJKZSJ"));// 平均看诊时间
//				double zzyssl = MedicineUtils.parseDouble(map_ghks
//						.get("ZZYSSL"));// 坐诊医生数量
//				double wysl = MedicineUtils.parseDouble(map_hyzs.get("PTXE"));// 第三方平台使用数量，最大值为挂号限额
//				// double sybl = (wysl / ghxe) > 1 ? 1 : wysl / yyxe;// 计算比例
//				// // >1?1:sybl
//				// double rec_sybl = 0;// 比例的倒数，用于控制循环
//				// boolean isFYY = false;// 用于分辨大于还是小于一半
//				// if (sybl < 0.5) {// 小于一半时取给外部的比例倒数便于计算
//				// rec_sybl = 1 / sybl;
//				// isFYY = false;
//				// } else {//大于一半时取自用比例的倒数
//				// rec_sybl = 1 / (1 - sybl);
//				// isFYY = true;
//				// }
////				if (wysl > yyxe)
////					wysl = yyxe;
//				double sybl = wysl / hyzs;// 外用数量比例
//				double rec_sybl = 0;// 比例的倒数，用于控制循环
//				boolean isFYY = false;// 用于分辨大于还是小于一半
//				if (sybl < 0.5) {// 小于一半时取给外部的比例倒数便于计算
//					rec_sybl = 1 / sybl;
//					isFYY = false;
//				} else if(sybl <1.0){// 大于一半时取自用比例的倒数
//					rec_sybl = 1 / (1 - sybl);
//					isFYY = true;
//				}else{
//					rec_sybl = 1;
//					isFYY = false;
//				}
//				int hy_Inx = 0;
//				List hy_Index = new ArrayList();
//				for (int count = 1; count <= (hyzs / rec_sybl); count++) {
//					hy_Index.add((int) count * (int) rec_sybl);
//				}
//				for (int i = 0; i < wysl - (int) rec_sybl * wysl; i++) {
//					Random rand = new Random();
//					hy_Inx = rand.nextInt((int) hyzs);// 对没使用方没取完的号码进行补全
//					if (hy_Inx % (int) rec_sybl == 0) {// 过滤已存在的下标
//						i--;
//						// continue;
//					} else {
//						hy_Index.add(hy_Inx);
//						if (hy_Index.size() >= wysl - 1)
//							break;
//					}
//				}
//				// 存放对应周的日期以及期限下所有改日立周的日期
//				List<Date> ghrq = new ArrayList<Date>();
//				int yycsts = MedicineUtils.parseInt(map_ghks.get("YYCSTS"));// 预约产生天数
//				Calendar c = Calendar.getInstance();
//				// 获取预约产生天数下 所有该日立周的日期
//				for (int i = 0; i < yycsts; i++) {
//					if (i > 0) {
//						c.add(Calendar.DATE, 1);
//					}
//					// c的日立周和挂号日期一样,缓存日期
//					if (c.get(Calendar.DAY_OF_WEEK) == MedicineUtils
//							.parseInt(map_kspb.get("GHRQ"))) {
//						try {
//							ghrq.add(sdf.parse(sdf.format(c.getTime())));
//						} catch (ParseException e) {
//							MedicineUtils.throwsException(logger, "日期格式转换失败:"
//									+ e.getMessage(), e);
//						}
//					}
//				}
//				// 循环需要产生号源的日期,产生号源记录
//				for (int i = 0; i < ghrq.size(); i++) {
//					Date d = ghrq.get(i);
//					Map<String, Object> map_par_c = new HashMap<String, Object>();
//					map_par_c.put("ksdm", ghks);
//					map_par_c.put("zblb", zblb);
//					map_par_c.put("gzrq", d);
//					long l = 0;
//					try {
//						l = dao.doCount("MS_HYB", hql_count.toString(),
//								map_par_c);
//					} catch (PersistentDataOperationException e) {
//						MedicineUtils.throwsException(logger,
//								"查询号源是否存在失败:" + e.getMessage(), e);
//					}
//					if (l > 0) {// 如果号源已经产生
//						continue;
//					}
//					Calendar c2 = Calendar.getInstance();// 处理看诊时间
//					try {
//						c2.setTime(sdf_time.parse(gzsj.split("-")[0]));
//					} catch (ParseException e) {
//						MedicineUtils.throwsException(logger, "时间格式转换失败:"
//								+ gzsj.split("-")[0], e);
//					}
//					int j = 0;// 用于控制医生数量对应的时间增加.
//					for (int x = 0; x < hyzs; x++) {// 按号源总数增加记录条数
//						Map<String, Object> map_hy = new HashMap<String, Object>();
//						map_hy.put("JZXH", x + 1);
//						map_hy.put("GHKS", ghks);
//						map_hy.put("ZBLB", zblb);
//						map_hy.put("GZRQ", d);
//						map_hy.put("JGID", jgid);
//						map_hy.put("SYBZ", 0);
//						map_hy.put(
//								"JZSJ",
//								c2.get(Calendar.HOUR_OF_DAY)
//										+ ":"
//										+ (c2.get(Calendar.MINUTE) < 10 ? "0"
//												+ c2.get(Calendar.MINUTE) : c2
//												.get(Calendar.MINUTE)));
//						if (isFYY) {
//							for (int index = 0; index < hy_Index.size(); index++) {
//								
//								if (x == Integer.parseInt(hy_Index.get(index)
//										+ "")-1) {
//									map_hy.put("SYTJ", "0");// 系统自用
//									break;
//								} else {
//									map_hy.put("SYTJ", "1");// 省平台调用
//								}
//							}
//						} else {
//							for (int index = 0; index < hy_Index.size(); index++) {
//								if (x == Integer.parseInt(hy_Index.get(index)
//										+ "")-1) {
//									map_hy.put("SYTJ", "1");// 省平台调用
//									break;
//								} else {
//									map_hy.put("SYTJ", "0");// 系统自用
//								}
//							}
//						}
//						try {
//							dao.doSave("create",
//									"phis.application.reg.schemas.MS_HYB",
//									map_hy, false);
//						} catch (ValidateException e) {
//							MedicineUtils.throwsException(logger, "保存号源记录失败:"
//									+ e.getMessage(), e);
//						} catch (PersistentDataOperationException e) {
//							MedicineUtils.throwsException(logger, "保存号源记录失败:"
//									+ e.getMessage(), e);
//						}
//						j++;
//						// 如果j能整除医生数量则看诊时间增加. 比如有3个医生
//						// 开始工作时间是8:00,平均看诊时间是5分钟
//						// 那么前3个号源的就诊时间都是8:00 第4个到第6个是8:05
//						if (j % zzyssl == 0) {
//							c2.add(Calendar.MINUTE, (int)pjkzsj);// 按平均看诊时间增加分
//						}
//					}
//				}
//				//将今天以前的号源记录放到历史表中
//				StringBuffer hql_ls=new StringBuffer();
//				hql_ls.append("insert into MS_HYB_LS (select * from MS_HYB where to_char(GZRQ,'yyyy-mm-dd')<:gzrq)");
//				//删除今天以前的号源
//				StringBuffer hql_d=new StringBuffer();
//				hql_d.append("delete from MS_HYB where to_char(GZRQ,'yyyy-mm-dd')<:gzrq");
//				Map<String,Object> map_par=new HashMap<String,Object>();
//				map_par.put("gzrq", sdf.format(new Date()));
//				try {
//					dao.doSqlUpdate(hql_ls.toString(), map_par);
//					dao.doSqlUpdate(hql_d.toString(), map_par);
//				} catch (PersistentDataOperationException e) {
//					MedicineUtils.throwsException(logger, "处理过期号源失败:"
//							+ e.getMessage(), e);
//				}
//				
//			}
//		}
//	}
//	/**
//	 * 保存医生排班对应的号源记录
//	 * @author caijy
//	 * @createDate 2016-10-12
//	 * @description 
//	 * @updateInfo
//	 * @param body 医生排班数据,必须包含的字段:KSDM,YSDM,ZBLB,GZRQ
//	 * @param ctx
//	 * @throws ModelDataOperationException
//	 */
//	public void saveHyForDoctor(List<Map<String, Object>> body, Context ctx)
//			throws ModelDataOperationException {
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String jgid = user.getManageUnit().getId();// 用户的机构ID
//		// 当前机构所有挂号科室数据
//		List<Map<String, Object>> list_ghks = null;
//		try {
//			list_ghks = dao.doList(
//					CNDHelper.createSimpleCnd("eq", "JGID", "s", jgid), null,
//					"phis.application.reg.schemas.MS_GHKS");
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "查询挂号科室失败:" + e.getMessage(),
//					e);
//		}
//		String gzsj_sw = MedicineUtils.parseString(ParameterUtil.getParameter(
//				jgid, BSPHISSystemArgument.SWGZSJ, ctx));
//		String gzsj_xw = MedicineUtils.parseString(ParameterUtil.getParameter(
//				jgid, BSPHISSystemArgument.XWGZSJ, ctx));
//		if (list_ghks == null || list_ghks.size() == 0) {
//			return;
//		}
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
//		StringBuffer hql_count = new StringBuffer();// 查询是否已经有号源
//		hql_count.append(" GHKS=:ksdm and ZBLB=:zblb and GZRQ=:gzrq and YSDM=:ysdm");
//		for (Map<String, Object> map_yspb : body) {
//			// 查询对应的挂号科室信息
//			Map<String, Object> map_ghks = MedicineUtils.getRecord(list_ghks,
//					new String[] { "KSDM" }, map_yspb, new String[] { "KSDM" });
//			if (map_ghks == null || map_ghks.size() == 0) {
//				throw new ModelDataOperationException(9000, "未找到对应的挂号科室,GHKS="
//						+ map_yspb.get("KSDM"));
//			}
//			if (MedicineUtils.parseInt(map_ghks.get("YYBZ")) == 0) {// 科室没开启预约,不处理
//				continue;
//			}
//			int zblb=MedicineUtils.parseInt(map_yspb.get("ZBLB"));
//			long ksdm=MedicineUtils.parseLong(map_yspb.get("KSDM"));
//			Date gzrq=null;
//			try {
//				gzrq=sdf.parse(sdf.format((Date)map_yspb.get("GZRQ")));
//			} catch (ParseException e) {
//				MedicineUtils.throwsException(logger, "挂号日期转换失败:" +map_yspb.get("GZRQ"),
//						e);
//			}
//			String ysdm=MedicineUtils.parseString(map_yspb.get("YSDM"));
//			String gzsj=gzsj_sw;
//			if(zblb==2){
//				gzsj=gzsj_xw;
//			}
//			map_ghks.put("GZSJ", gzsj);
//			// 获取号源总数
//			Map<String, Object> map_hyzs = RegistrationUtil
//					.getHyzs(map_ghks,2, ctx);
//			double hyzs = MedicineUtils.parseDouble(map_hyzs.get("HYZS"));// 号源总数
//			double pjkzsj = MedicineUtils.parseDouble(map_hyzs.get("PJKZSJ"));// 平均看诊时间
//			// 循环需要产生号源的日期,产生号源记录
//				Map<String, Object> map_par_c = new HashMap<String, Object>();
//				map_par_c.put("ksdm", ksdm);
//				map_par_c.put("zblb", zblb);
//				map_par_c.put("gzrq", gzrq);
//				map_par_c.put("ysdm", ysdm);
//				long l = 0;
//				try {
//					l = dao.doCount("MS_HYB", hql_count.toString(),
//							map_par_c);
//				} catch (PersistentDataOperationException e) {
//					MedicineUtils.throwsException(logger,
//							"查询号源是否存在失败:" + e.getMessage(), e);
//				}
//				if (l > 0) {// 如果号源已经产生
//					continue;
//				}
//				Calendar c2 = Calendar.getInstance();// 处理看诊时间
//				try {
//					c2.setTime(sdf_time.parse(gzsj.split("-")[0]));
//				} catch (ParseException e) {
//					MedicineUtils.throwsException(logger, "时间格式转换失败:"
//							+ gzsj.split("-")[0], e);
//				}
//				for (int x = 0; x < hyzs; x++) {// 按号源总数增加记录条数
//					Map<String, Object> map_hy = new HashMap<String, Object>();
//					map_hy.put("JZXH", x + 1);
//					map_hy.put("GHKS", ksdm);
//					map_hy.put("ZBLB", zblb);
//					map_hy.put("GZRQ",gzrq);
//					map_hy.put("YSDM",ysdm);
//					map_hy.put("JGID", jgid);
//					map_hy.put("SYBZ", 0);
//					map_hy.put("JZSJ", c2.get(Calendar.HOUR_OF_DAY) + ":"
//							+ (c2.get(Calendar.MINUTE)<10?"0"+c2.get(Calendar.MINUTE):c2.get(Calendar.MINUTE)));
//					try {
//						dao.doSave("create",
//								"phis.application.reg.schemas.MS_HYB",
//								map_hy, false);
//					} catch (ValidateException e) {
//						MedicineUtils.throwsException(logger, "保存号源记录失败:"
//								+ e.getMessage(), e);
//					} catch (PersistentDataOperationException e) {
//						MedicineUtils.throwsException(logger, "保存号源记录失败:"
//								+ e.getMessage(), e);
//					}
//					c2.add(Calendar.MINUTE, (int)pjkzsj);// 按平均看诊时间增加分
//				}
//		}
//		// 将今天以前的号源记录放到历史表中
//		StringBuffer hql_ls = new StringBuffer();
//		hql_ls.append("insert into MS_HYB_LS (select * from MS_HYB where to_char(GZRQ,'yyyy-mm-dd')<:gzrq)");
//		// 删除今天以前的号源
//		StringBuffer hql_d = new StringBuffer();
//		hql_d.append("delete from MS_HYB where to_char(GZRQ,'yyyy-mm-dd')<:gzrq");
//		Map<String, Object> map_par = new HashMap<String, Object>();
//		map_par.put("gzrq", sdf.format(new Date()));
//		try {
//			dao.doSqlUpdate(hql_ls.toString(), map_par);
//			dao.doSqlUpdate(hql_d.toString(), map_par);
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "处理过期号源失败:" + e.getMessage(),
//					e);
//		}
//	}
//	/**
//	 * 挂号科室预约定义
//	 * @author caijy
//	 * @createDate 2016-10-12
//	 * @description 
//	 * @updateInfo
//	 * @param body
//	 * @param ctx
//	 * @throws ModelDataOperationException
//	 */
//	public void saveKsyydy(Map<String,Object> body,Context ctx)throws ModelDataOperationException {
//		int yybz=MedicineUtils.parseInt(body.get("YYBZ"));
//		if(yybz==0){//不开启预约,删除号源数据
//			StringBuffer hql_delete =new StringBuffer();
//			hql_delete.append("delete from MS_HYB where GHKS=:ksdm and ZBLB=:zblb and GZRQ>:gzrq");
//			Map<String,Object> map_par=new HashMap<String,Object>();
//			map_par.put("ksdm", MedicineUtils.parseLong(body.get("KSDM")));
//			map_par.put("zblb", MedicineUtils.parseInt(body.get("ZBLB")));
//			map_par.put("gzrq", new Date());
//			try {
//				dao.doUpdate(hql_delete.toString(), map_par);
//			} catch (PersistentDataOperationException e) {
//				MedicineUtils.throwsException(logger, "删除号源记录失败:"+e.getMessage(), e);
//			}
//		}else{//开启预约,新增号源数据
//			//先增加医生排班的数据
//			StringBuffer hql_yspb=new StringBuffer();//查询医生排班
//			hql_yspb.append("select KSDM as KSDM,YSDM as YSDM,ZBLB as ZBLB,GZRQ as GZRQ from MS_YSPB where KSDM=:ksdm and GZRQ>:gzrq");
//			Map<String,Object> map_par=new HashMap<String,Object>();
//			map_par.put("ksdm", MedicineUtils.parseLong(body.get("KSDM")));
//			map_par.put("gzrq", new Date());
//			List<Map<String, Object>> l_yspb=null;
//			try {
//				l_yspb = dao.doQuery(hql_yspb.toString(), map_par);
//			} catch (PersistentDataOperationException e) {
//				MedicineUtils.throwsException(logger, "查询医生排班数据失败:"+e.getMessage(), e);
//			}
//			//生成医生排班号源
//			if(l_yspb!=null&&l_yspb.size()>0){
//				saveHyForDoctor(l_yspb,ctx);
//			}
//			StringBuffer hql_kspb=new StringBuffer();//查询科室排班
//			hql_kspb.append("select GHKS as GHKS,ZBLB as ZBLB,1 as KSPB,GHRQ as GHRQ from MS_KSPB where GHKS=:ksdm");
//			map_par.remove("gzrq");
//			List<Map<String, Object>> l_kspb=null;
//			try {
//				l_kspb = dao.doQuery(hql_kspb.toString(), map_par);
//			} catch (PersistentDataOperationException e) {
//				MedicineUtils.throwsException(logger, "查询科室排班数据失败:"+e.getMessage(), e);
//			}
//			//生成科室排班号源
//			if(l_kspb!=null&&l_kspb.size()>0){
//				saveHyForDepartment(l_kspb,ctx);
//			}
//		}
//		try {
//			//更新挂号科室信息
//			dao.doSave("update", "phis.application.reg.schemas.MS_GHKS", body, false);
//		} catch (ValidateException e) {
//			MedicineUtils.throwsException(logger, "更新挂号科室信息失败:"+e.getMessage(), e);
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "更新挂号科室信息失败:"+e.getMessage(), e);
//		}
//	}
//	/**
//	 * 自动生成科室排班号源, 挂号界面每天上午和下午第一次打开的时候调用,生成在生成天数内未产生的号源
//	 * @author caijy
//	 * @createDate 2016-10-13
//	 * @description 
//	 * @updateInfo
//	 * @throws ModelDataOperationException
//	 */
//	public void zdsckshy(Context ctx)throws ModelDataOperationException{
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String jgid = user.getManageUnit().getId();// 用户的机构ID
//		StringBuffer hql=new StringBuffer();//查询开启预约排班的科室列表
//		hql.append("select a.GHKS as GHKS,a.ZBLB as ZBLB,1 as KSPB,a.GHRQ as GHRQ from MS_KSPB a,MS_GHKS b where a.GHKS=b.KSDM and b.YYBZ=1 and b.JGID=:jgid");
//		Map<String,Object> map_par=new HashMap<String,Object>();
//		map_par.put("jgid", jgid);
//		try {
//			List<Map<String,Object>> l=dao.doQuery(hql.toString(), map_par);
//			if(l!=null&&l.size()>0){
//				saveHyForDepartment(l,ctx);
//			}
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "查询挂号科室排班失败:"+e.getMessage(), e);
//		}
//	}
//	/**
//	 * 查询号源信息
//	 * @author caijy
//	 * @createDate 2016-10-13
//	 * @description 
//	 * @updateInfo
//	 * @param req
//	 * @param res
//	 * @param ctx
//	 * @throws ModelDataOperationException
//	 */
//	@SuppressWarnings("unchecked")
//	public void queryKsHy(Map<String,Object> req,Map<String,Object> res,Context ctx)throws ModelDataOperationException{
//		Map<String,Object> body=(Map<String,Object>)req.get("body");
//		String rq=MedicineUtils.parseString(body.get("RQ"));
//		int zblb=MedicineUtils.parseInt(body.get("ZBLB"));
//		String sj=!body.containsKey("SJ")?"0":MedicineUtils.parseString(body.get("SJ"));
//		UserRoleToken user = UserRoleToken.getCurrent();
//		long ksdm=body.containsKey("GHKS")?MedicineUtils.parseLong(body.get("GHKS")):MedicineUtils.parseLong(user.getProperty("reg_departmentId"));//当前挂号科室
//		String ysdm=!body.containsKey("YSDM")?"0":MedicineUtils.parseString(body.get("YSDM"));
//		//查询预约号源是否用光
//		Map<String,Object> map_ghks=new HashMap<String,Object>();
//		try {
//			map_ghks=dao.doLoad("phis.application.reg.schemas.MS_GHKS", ksdm);
//			if(map_ghks==null||map_ghks.size()==0){
//				return;
//			}
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "查询挂号科室失败:"+e.getMessage(), e);
//		}
//		int yybl=MedicineUtils.parseInt(map_ghks.get("YYBL"));
//		Map<String,Object> map_par=new HashMap<String,Object>();
//		map_par.put("ksdm", ksdm);
//		map_par.put("gzrq", rq);
//		StringBuffer hql_sum=new StringBuffer();
//		hql_sum.append("select sum(SYBZ) as SY,count(1) as ZS,ZBLB as ZBLB from MS_HYB where GHKS=:ksdm and GZRQ>sysdate and to_char(GZRQ,'yyyy-mm-dd')=:gzrq and SYTJ = '0' ");
//		if(!"0".equals(ysdm)){
//			hql_sum.append(" and YSDM=:ysdm");
//			map_par.put("ysdm", ysdm);
//		}
//		hql_sum.append(" group by ZBLB");
//		try {
//			List<Map<String,Object>> l_s=dao.doSqlQuery(hql_sum.toString(), map_par);
//			if(l_s==null||l_s.size()==0){
//				return;
//			}
//			int myzblb=0;//用于标识哪个值班类别的号源使用完,0标识上下午都有号源,1表示上午没号,2表示下午没号,3表示全天没号
//			for(Map<String,Object> m_s:l_s){
//			if(MedicineUtils.parseInt(m_s.get("ZS"))*yybl/100<=MedicineUtils.parseInt(m_s.get("SY"))){
//				myzblb+=MedicineUtils.parseInt(m_s.get("ZBLB"));
//			}	
//			}
//			if(myzblb==3){//全天都预约完了
//				return;
//			}
//			if(myzblb>0){
//				if(myzblb==zblb){//如果查询的是没号源的班别,直接return
//					return;
//				}
//				if(zblb==0){//如果查询的是全天的,那么只能查询有号源的,比如myzblb=1 那么ZBLB就是2反之为1
//					zblb=3-myzblb;
//				}
//			}
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "查询已预约比例失败:"+e.getMessage(), e);
//		}
//		
//		StringBuffer hql=new StringBuffer();
//		hql.append("select SBXH as SBXH ,GZRQ as GZRQ,ZBLB as ZBLB,JZSJ as JZSJ,JZXH as JZXH,YSDM as YSDM  from MS_HYB where GHKS=:ksdm and GZRQ>sysdate and to_char(GZRQ,'yyyy-mm-dd')=:gzrq and SYBZ = 0 and SYTJ = '0' ");
//		if(zblb!=0){
//			hql.append(" and ZBLB=:zblb");
//			map_par.put("zblb", zblb);
//		}
//		if(!"0".equals(ysdm)){
//			hql.append(" and YSDM=:ysdm");
//			//map_par.put("ysdm", ysdm);
//		}
//		if(!"0".equals(sj)){
//			hql.append(" and JZSJ like '"+sj+":%'");
//		}
//		hql.append(" order by GZRQ,ZBLB,JZXH");
//		if(!body.containsKey("DSFYY")){//本地调用
//			MedicineCommonModel model=new MedicineCommonModel(dao);
//			Map<String,Object> m=model.getPageInfoRecord(req,map_par,hql.toString(),"phis.application.cic.schemas.MS_ZJYY_LIST");
//			res.putAll(m);
//		}else{//第三方调用
//			try {
//				List<Map<String,Object>> l_data= dao.doSqlQuery(hql.toString(), map_par);
//				SchemaUtil.setDictionaryMassageForList(l_data, "phis.application.cic.schemas.MS_ZJYY_LIST");
//				res.put("data", changeListToLowerCase(l_data));
//			} catch (PersistentDataOperationException e) {
//				MedicineUtils.throwsException(logger, "查询号源失败:"+e.getMessage(), e);
//			}
//		}
//		
//	}
	
	/**
	 * 保存诊间预约
	 * @author caijy
	 * @createDate 2016-10-13
	 * @description 
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException 
	 */
	@SuppressWarnings("static-access")
	public void saveGhyy(HashMap<String,Object> yyghxx)throws ModelDataOperationException, ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long yyxh = 0;
		long sbxh =MedicineUtils.parseLong(yyghxx.get("sbxh"));
		String sfzh=MedicineUtils.parseString(yyghxx.get("brid"));
		String jgid= MedicineUtils.parseString(yyghxx.get("jgid"));
		String ksdm= MedicineUtils.parseString(yyghxx.get("ksdm"));
		String ysdm= MedicineUtils.parseString(yyghxx.get("ysdm"));
		String yyrq= MedicineUtils.parseString(yyghxx.get("yyrq"));
		int zblb= MedicineUtils.parseInt(yyghxx.get("zblb"));
		String ghrq=sdf.format(new Date());
		long jzxh=MedicineUtils.parseLong(yyghxx.get("jzxh"));
		long brid = 0;
		String errorMessage = "预约挂号失败";
		int serviceCode = CODE_UNKNOWN_ERROR;
		GetJdbcConnection conn = new GetJdbcConnection();
		try {
			//查询病人档案
			String sb = "select BRID as BRID from MS_BRDA where SFZH = '"+sfzh+"'";
			Map<String, Object> bridResult = conn.query(sb);
			if(bridResult!=null && bridResult.containsKey("BRID") && bridResult.get("BRID")!=null){
				brid = MedicineUtils.parseLong(bridResult.get("BRID"));
			}else{
				errorMessage = "未查到病人档案,请确认身份证信息是否正确";
				serviceCode = CODE_RECORD_NOT_FOUND;
				throw new ModelDataOperationException(5000,"未查到病人档案,请确认身份证信息是否正确");
			}
			if(brid==0){
				errorMessage = "未查到病人档案,请确认身份证信息是否正确";
				serviceCode = CODE_RECORD_NOT_FOUND;
				throw new ModelDataOperationException(5000,"未查到病人档案,请确认身份证信息是否正确");
			}
			String countSql = "select count(*) as num from MS_YYGH where KSDM = '"+ksdm+"' and ZBLB = "+zblb+" and to_char(YYRQ,'yyyy-mm-dd') = '"+yyrq+"' and BRID = "+brid;
			Map<String, Object> countResult = conn.query(countSql);
			if(countResult!=null && countResult.containsKey("NUM") && countResult.get("NUM")!=null){
				long l = MedicineUtils.parseLong(countResult.get("NUM"));
				if(l>0){
					errorMessage = "该病人已预约该科室当天号,不能重复预约";
					serviceCode = CODE_DATABASE_ERROR;
					throw new ModelDataOperationException(6000,"该病人已预约该科室当天号,不能重复预约");
				}
			}
			
			String sql = "select MAX(YYXH) as MAXINDEX from MS_YYGH";
			Map<String, Object> result = conn.query(sql);
			if(result!=null && result.containsKey("MAXINDEX") && result.get("MAXINDEX")!=null){
				yyxh = MedicineUtils.parseLong(result.get("MAXINDEX"));
				if(yyxh>0){
					yyxh+=1;
				}
				String addSql = "INSERT INTO MS_YYGH(YYXH, JGID, YYMM, KSDM, YSDM, BRID, YYRQ, ZBLB, GHRQ, HYXH, JZXH, GHBZ, ZCID)"
						+ " VALUES ("+yyxh+",'"+jgid+"', '0', '"+ksdm+"', '"+ysdm+"', "+brid+", to_date('"+yyrq+"','YYYY-MM-DD'),"+zblb+",to_date('"+ghrq+"','YYYY-MM-DD'),"+sbxh+","+jzxh+",0,0)";  // 插入数据的sql语句
				conn.insert(addSql);
			}
		} catch (Exception e) {
			logger.error(errorMessage, e);
			throw new ModelDataOperationException(serviceCode,
					errorMessage,e.getCause());
		}
	}
	/**
	 * 取消预约
	 * @author caijy
	 * @createDate 2016-10-25
	 * @description 
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void saveYyqx(long sbxh,String sfzh)throws ModelDataOperationException{
		GetJdbcConnection conn = new GetJdbcConnection();
		StringBuffer hql_count=new StringBuffer();
		long brid = 0;
		String errorMessage = "取消预约失败";
		int serviceCode = CODE_UNKNOWN_ERROR;
		try{
			//查询病人档案
			String sb = " select BRID as BRID from MS_BRDA where SFZH = '"+sfzh+"'";
			Map<String, Object> bridResult = conn.query(sb);
			if(bridResult!=null && bridResult.containsKey("BRID") && bridResult.get("BRID")!=null){
				brid = MedicineUtils.parseLong(bridResult.get("BRID"));
			}else{
				errorMessage = "未查到病人档案,请确认身份证信息是否正确";
				serviceCode = CODE_RECORD_NOT_FOUND;
				throw new ModelDataOperationException(5000,"未查到病人档案,请确认身份证信息是否正确");
			}
			if(brid==0){
				errorMessage = "未查到病人档案,请确认身份证信息是否正确";
				serviceCode = CODE_RECORD_NOT_FOUND;
				throw new ModelDataOperationException(5000,"未查到病人档案,请确认身份证信息是否正确");
			}
			//查询是否已经取消预约 或就诊日期小于今天
			hql_count.append("select count(*) as num from MS_YYGH where HYXH="+sbxh+" and BRID="+brid+" and to_char(YYRQ,'yyyy-mm-dd')>to_char(sysdate,'yyyy-mm-dd')");
			//			long l=dao.doCount("MS_YYGH", hql_count.toString(), map_par);
			Map<String, Object> countResult = conn.query(hql_count.toString());
			if(countResult!=null && countResult.containsKey("NUM")){
				long l = MedicineUtils.parseLong(countResult.get("NUM"));
				if(l<=0){
					errorMessage = "该病人已经取消过或就诊日期过期";
					serviceCode = CODE_DATABASE_ERROR;
					throw new ModelDataOperationException(8000,"该病人已经取消过或就诊日期过期");	
				}
			}
			StringBuffer hql_delete=new StringBuffer();//删除预约记录
			hql_delete.append("delete from MS_YYGH where HYXH="+sbxh+" and BRID="+brid);
			int x = conn.delete(hql_delete.toString());
			if(x<1){
				throw new ModelDataOperationException(9000,"预约记录不存在,删除失败!");
			}
		}catch(Exception e){
			logger.error(errorMessage, e);
			throw new ModelDataOperationException(serviceCode,
					errorMessage,e.getCause());
		}
	}
	
//	/**
//	 * 获取就诊序号
//	 * @author caijy
//	 * @createDate 2016-10-14
//	 * @description 
//	 * @updateInfo
//	 * @param map_ghmx
//	 * @throws ModelDataOperationException
//	 */
//	public boolean getJzxh(Map<String,Object> map_ghmx) throws ModelDataOperationException{
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Map<String,Object> map_ghks=null;
//		try {
//			map_ghks=dao.doLoad("phis.application.reg.schemas.MS_GHKS", MedicineUtils.parseLong(map_ghmx.get("KSDM")));
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "查询挂号科室失败:"+e.getMessage(), e);
//		}
//		if(map_ghks==null||map_ghks.size()==0){
//			throw new ModelDataOperationException("未找到对应的挂号科室:"+map_ghmx.get("KSDM"));
//		}
//		if(MedicineUtils.parseInt(map_ghks.get("YYBZ"))!=1||MedicineUtils.parseInt(map_ghks.get("YYBL"))==100){//韶关 增加只有预约没有挂号限额的验证
//			return false;
//		}
//		StringBuffer hql=new StringBuffer();
//		hql.append("select SBXH as SBXH,JZXH as JZXH from MS_HYB where SYBZ=0 and GHKS=:ksdm and ZBLB=:zblb and GZRQ=:gzrq");
//		Map<String,Object> map_par=new HashMap<String,Object>();
//		map_par.put("ksdm", MedicineUtils.parseLong(map_ghmx.get("KSDM")));
//		map_par.put("zblb", MedicineUtils.parseInt(map_ghmx.get("GHLB")));
//		try {
//			map_par.put("gzrq", sdf.parse(sdf.format(new Date())));
//		} catch (ParseException e) {
//			MedicineUtils.throwsException(logger, "时间转换错误:"+e.getMessage(), e);
//		}
//		if(map_ghmx.containsKey("YSDM")&&map_ghmx.get("YSDM")!=null){
//			hql.append(" and YSDM=:ysdm");
//			map_par.put("ysdm", MedicineUtils.parseString(map_ghmx.get("YSDM")));
//		}
//		hql.append(" order by JZXH");
//		try {
//			List<Map<String,Object>> l=dao.doQuery(hql.toString(), map_par);
//			if(l==null||l.size()==0){
//				throw new ModelDataOperationException("号源已用尽");
//			}
//			Map<String,Object> map_hyb=l.get(0);
//			map_ghmx.put("JZXH", MedicineUtils.parseInt(map_hyb.get("JZXH")));
//			int i=dao.doUpdate("update MS_HYB set SYBZ=1 where SBXH="+MedicineUtils.parseLong(map_hyb.get("SBXH")), null);
//			if(i<1){
//				throw new ModelDataOperationException("号源已被占用,请重试");
//			}
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "查询号源失败:"+e.getMessage(), e);
//		}
//		return true;
//	}
//	
//	//以下方法为第三方接口调用
//	/**
//	 * 保存病人信息
//	 * @author caijy
//	 * @createDate 2016-10-24
//	 * @description 
//	 * @updateInfo
//	 * @param brxx
//	 * @param ctx
//	 * @throws ModelDataOperationException
//	 */
//	public long saveBrxx(Map<String,Object> brxx, Context ctx) throws ModelDataOperationException{
//		Map<String,Object> map_va=new HashMap<String,Object>();//需要不能为空的字段,后续需要再添加
//		map_va.put("idCard", "身份证号");
//		map_va.put("cardNo", "卡号");
//		map_va.put("cardTypeCode", "卡类型");
//		map_va.put("personName", "病人姓名");
//		validataNull(brxx,map_va);//检验身份证是否为空
//		String idCard=MedicineUtils.parseString(brxx.get("idCard"));//身份证号
//		String idcardva;
//		try{
//			idcardva=EmpiUtil.IDCardValidate(MedicineUtils.parseString(brxx.get("idCard")));
//		}catch(Exception e){
//			logger.error("身份证格式校验失败:"+e.getMessage());
//			throw new ModelDataOperationException(9000,"身份证格式校验失败:"+e.getMessage());
//		}
//		if(!"".equals(idcardva)){//检验身份证号码格式是否正确
//			throw new ModelDataOperationException(9000,"身份证格式校验错误:"+idcardva);
//		}
//		String sex=EmpiUtil.getSexByPid(idCard);//性别
//		Date birthday=EmpiUtil.getBirthdayFromIdCard(idCard);//出生年月
//		// 先查询是否有病人档案
//		StringBuffer hql_brda = new StringBuffer();
//		hql_brda.append("select BRID as BRID from MS_BRDA where SFZH=:sfzh");
//		Map<String, Object> m = new HashMap<String, Object>();
//		m.put("sfzh", idCard);
//		try{
//		List<Map<String, Object>> l_brda = dao.doQuery(hql_brda.toString(), m);
//		if (l_brda != null && l_brda.size() > 0 && l_brda.get(0) != null) {
//			return MedicineUtils.parseLong(l_brda.get(0).get("BRID"));
//		}
//		}catch(Exception e){
//			logger.error("查询病人档案失败:" + e.getMessage());
//			throw new ModelDataOperationException(9000,"查询病人档案失败:" + e.getMessage());
//		}
//		StringBuffer hql = new StringBuffer();
//		hql.append("select SEQ_YYBR_MZHM.nextval as MZHM from dual");
//		String mzhm;
//		try {
//			List<Map<String, Object>> l = dao.doSqlQuery(hql.toString(), null);
//			if (l == null || l.size() == 0 || l.get(0).get("MZHM") == null) {
//				throw new ModelDataOperationException("本地序列未创建！");
//			}
//			mzhm = "YYGH" + l.get(0).get("MZHM");
////			mzhm = l.get(0).get("MZHM")+"";
//		} catch (PersistentDataOperationException e) {
//			logger.error("保存本地病人档案失败,查询门诊号码序列失败:" + e.getMessage());
//			throw new ModelDataOperationException("保存本地病人档案失败,查询门诊号码序列失败:" + e.getMessage());
//		}
//		// 卡信息
//		List<Map<String, Object>> list_cards = new ArrayList<Map<String, Object>>();
//		Map<String, Object> map_card = new HashMap<String, Object>();
//		map_card.put("status", 0);
//		map_card.put("cardTypeCode",MedicineUtils.parseString(brxx.get("cardTypeCode")));
//		map_card.put("cardNo", MedicineUtils.parseString(brxx.get("cardNo")));
//		list_cards.add(map_card);
//		// 档案信息
//		Map<String, Object> body = new HashMap<String, Object>();
//		body.put("MZHM", mzhm);
//		body.put("cards", list_cards);
//		body.put("createUnit", UserRoleToken.getCurrent().getManageUnit().getId());
//		body.put("personName", MedicineUtils.parseString(brxx.get("personName")));
//		body.put("sexCode", MedicineUtils.parseInt(sex));
//		body.put("idCard", idCard);
//		body.put("birthday", birthday);
//		// body.put("BRXZ", "32");
//		Map<String, Object> records = new HashMap<String, Object>();
//		records.putAll(body);
//		records = EmpiUtil.changeToPIXFormat(records);
//		records.put("photo", "");
//		Map<String, Object> result;
//		try {
//			result = EmpiUtil.submitPerson(dao, ctx, body,
//					records);
//		} catch (ServiceException e) {
//			logger.error("保存病人信息失败:" + e.getMessage());
//			throw new ModelDataOperationException("保存病人信息失败:" + e.getMessage());
//		}
//		return MedicineUtils.parseLong(result.get("BRID"));
//	}
//	/**
//	 * 根据机构代码查询开启预约的科室列表
//	 * @author caijy
//	 * @createDate 2016-10-24
//	 * @description 
//	 * @updateInfo
//	 * @param data
//	 * @param ctx
//	 * @return
//	 * @throws ModelDataOperationException
//	 */
//	public List<Map<String,Object>> queryGhks(Map<String,Object> data, Context ctx) throws ModelDataOperationException{
//		String jgid=MedicineUtils.parseString(data.get("jgid"));
//		StringBuffer hql=new StringBuffer();
//		hql.append("select KSDM as ghks,KSMC as ksmc from MS_GHKS where JGID=:jgid and YYBZ=1");
//		Map<String,Object> map_par=new HashMap<String,Object>();
//		map_par.put("jgid", jgid);
//		List<Map<String,Object>> l_ret=null;
//		try {
//			l_ret=dao.doQuery(hql.toString(), map_par);
//		} catch (PersistentDataOperationException e) {
//			logger.error("查询挂号科室信息失败:" + e.getMessage());
//			throw new ModelDataOperationException("查询挂号科室信息失败:" + e.getMessage());
//		}
//		return l_ret;
//	}
//	/**
//	 * 根据机构和科室 查询预约医生信息
//	 * @author caijy
//	 * @createDate 2016-10-24
//	 * @description 
//	 * @updateInfo
//	 * @param data
//	 * @param ctx
//	 * @return
//	 * @throws ModelDataOperationException
//	 */
//	public List<Map<String,Object>> queryYsxx(Map<String,Object> data, Context ctx) throws ModelDataOperationException{
//		String jgid=MedicineUtils.parseString(data.get("jgid"));
//		long ksdm=MedicineUtils.parseLong(data.get("ghks"));
//		StringBuffer hql=new StringBuffer();
//		hql.append("select distinct YSDM as ysdm,c.PERSONNAME as ysxm from MS_YSPB a,MS_GHKS b,SYS_Personnel c where a.KSDM=b.KSDM and a.YSDM=c.PERSONID and b.JGID=:jgid and b.KSDM=:ksdm and b.YYBZ=1 and to_char(a.GZRQ,'yyyy-mm-dd')>=to_char(sysdate+1,'yyyy-mm-dd')");
//		Map<String,Object> map_par=new HashMap<String,Object>();
//		map_par.put("jgid", jgid);
//		map_par.put("ksdm", ksdm);
//		List<Map<String,Object>> l_ret=null;
//		try {
//			l_ret=dao.doSqlQuery(hql.toString(), map_par);
//		} catch (PersistentDataOperationException e) {
//			logger.error("查询预约医生信息失败:" + e.getMessage());
//			throw new ModelDataOperationException("查询预约医生信息失败:" + e.getMessage());
//		}
//		return changeListToLowerCase(l_ret);
//	}
//	/**
//	 * 查询基础数据(字典数据查询)
//	 * @author caijy
//	 * @createDate 2016-10-25
//	 * @description 
//	 * @updateInfo
//	 * @param data
//	 * @param ctx
//	 * @return
//	 * @throws ModelDataOperationException
//	 */
//	public Map<String,Map<String,Object>> queryJcsj() throws ModelDataOperationException{
//		Map<String,Map<String,Object>> map_ret=new HashMap<String,Map<String,Object>>();
//		map_ret.put("klx", getZdsj("phis.dictionary.card"));//卡类型
//		return map_ret;
//	}
//	/**
//	 * 将list里面的map里面的Key转小写
//	 * @author caijy
//	 * @createDate 2016-10-26
//	 * @description 
//	 * @updateInfo
//	 * @param l
//	 * @return
//	 */
//	public List<Map<String,Object>> changeListToLowerCase (List<Map<String,Object>> l){
//		if(l==null||l.size()==0){
//			return null;
//		}
//		List<Map<String,Object>> l_ret=new ArrayList<Map<String,Object>>();
//		for(Map<String,Object> m:l){
//			Map<String,Object> map_ret=new HashMap<String,Object>();
//			for(String key:m.keySet()){
//				map_ret.put(key.toLowerCase(), m.get(key));
//			}
//			l_ret.add(map_ret);
//		}
//		return l_ret;
//	}
//	/**
//	 * 将字典数据转成map
//	 * @author caijy
//	 * @createDate 2016-10-25
//	 * @description 
//	 * @updateInfo
//	 * @param dicName
//	 * @return
//	 * @throws ModelDataOperationException
//	 */
//	public Map<String,Object> getZdsj(String dicName)throws ModelDataOperationException{
//		Map<String,Object> map_ret= new HashMap<String,Object>();
//		try {
//			Dictionary sexdic=DictionaryController.instance().get(dicName);
//			List<DictionaryItem> l_dici=sexdic.itemsList();
//			for(DictionaryItem d:l_dici){
//				map_ret.put(d.getKey(), d.getText());
//			}
//		} catch (ControllerException e) {
//			logger.error("查询字典数据失败字典:"+dicName+":" + e.getMessage());
//			throw new ModelDataOperationException("查询字典数据失败字典:"+dicName+":" + e.getMessage());
//		}
//		return map_ret;
//	}
//	/**
//	 * 检验数据对应字段是否为空
//	 * @author caijy
//	 * @createDate 2016-10-24
//	 * @description 
//	 * @updateInfo
//	 * @param data 需要检验的数据
//	 * @param va 需要检验的字段, key是字段名,value是对应的中文名
//	 * @throws ModelDataOperationException
//	 */
//	public void validataNull(Map<String,Object> data,Map<String,Object> va)throws ModelDataOperationException{
//		for(String key:va.keySet()){
//			if(!data.containsKey(key)){
//				throw new ModelDataOperationException(9000,va.get(key)+"不能为空!");
//			}
//		}
//	}
//
//	
//	public void doSynchorAll(Map req, Map res, BaseDAO dao, Context ctx){
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String jgid = user.getManageUnit().getId();
//		StringBuffer sb = new StringBuffer();
//		StringBuffer sb_del = new StringBuffer();
//		sb.append("Select ORGANIZCODE as JGID from SYS_ORGANIZATION where ORGANIZCODE like '").append(jgid).append("%'");
//		sb_del.append("delete from YYGH_TBKSPB where GHRQ < trunc(sysdate)");//删除今天之前的冗余数据
//		List<Map<String , Object>> jgids = new ArrayList<Map<String,Object>>();
//		Map<String , Object> par = new HashMap<String, Object>();
//		try {
//			jgids = dao.doSqlQuery(sb.toString(), par);
//		} catch (PersistentDataOperationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		for (Map<String, Object> map : jgids) {
//			try {
//				doSynchor(map, res, dao, ctx);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ModelDataOperationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		try {
//			dao.doSqlUpdate(sb_del.toString(), null);//注意
//		} catch (PersistentDataOperationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * 1.获取各个科室的预约天数，计算日期 2.与本地记录做比较，哪些日期该上传却没有上传 3.上传对应日期的科室排班信息 4.记录各个科室上传了的日期
//	 * 5.删除日期记录表今天以前的冗余信息
//	 * 
//	 * @author fengld
//	 * @createDate 2017-03-13
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 * @throws ParseException
//	 * @throws ModelDataOperationException
//	 */
//	public void doSynchor(Map req, Map res, BaseDAO dao, Context ctx)
//			throws ParseException, ModelDataOperationException {
//		UserRoleToken user = UserRoleToken.getCurrent();
////		String jgid = user.getManageUnit().getId();
//		String jgName = user.getManageUnit().getName();
//		String jgid = req.get("JGID")+"";
//		int sfqyhygl= MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, BSPHISSystemArgument.SFQYHYGL, ctx));
//		if (sfqyhygl == 0)//没开启号源管理不分配
//			return;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		StringBuffer sb_kss = new StringBuffer();// 查询机构排班的预约科室
//		StringBuffer sb_sjs = new StringBuffer();// 查询当前科室的提交时间
//		Map<String, Object> parameter = new HashMap<String, Object>();
//		Map<String, Object> par_ks = new HashMap<String, Object>();// 填入科室和机构id
//		sb_kss.append("select a.KSDM as KSDM , a.KSMC as KSMC , a.MZKS as MZKS , a.YYBZ as YYBZ , a.YYCSTS as YYCSTS , b.GHRQ-1 as GHRQ , b.ZBLB as ZBLB , b.YYXE as YYXE from MS_GHKS a, MS_KSPB b where a.KSDM = b.GHKS and a.JGID =:jgid and a.YYBZ ='1' ");
//		sb_sjs.append("select GHRQ as GHRQ , ZBLB as ZBLB from YYGH_TBKSPB where JGID =:jgid and KSDM=:ksdm ");
//		List<Map<String, Object>> list_kss = new ArrayList<Map<String, Object>>();
//		List<Map<String, Object>> list_kssj = new ArrayList<Map<String, Object>>();
//		parameter.put("jgid", jgid);
//		par_ks.put("jgid", jgid);
//		try {
//			list_kss = dao.doSqlQuery(sb_kss.toString(), parameter);
//		} catch (PersistentDataOperationException e) {
//			e.printStackTrace();
//		}
//		List<Map<String, Object>> list_tj = new ArrayList<Map<String, Object>>();
//		String GHRQ = "";
//		// 可能超过七天
//		for (Map<String, Object> map_ksInfo : list_kss) {
//
//			int YYCSTS = Integer.parseInt(map_ksInfo.get("YYCSTS") + "");
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(new Date());
//			int whichDay1 = cal.get(Calendar.DAY_OF_WEEK);
//			List<String> dates = getDates(YYCSTS + whichDay1);// 返回约定预约天数内的所有日期
//			int size = dates.size() / 7;
//			for (int i = 0; i < size + 1; i++) {
//				// 对取出的数据与系统储存的日期表做比较，如果已经存在纪录则不处理
//				if (Integer.parseInt(map_ksInfo.get("GHRQ") + "") + 7 * i
//						+ whichDay1 < dates.size()) {
//					GHRQ = dates.get(Integer.parseInt(map_ksInfo.get("GHRQ")
//							+ "")
//							+ 7 * i);
//					if (GHRQ.equals("expired") || GHRQ.equals(""))// 过滤已过期的日期
//						continue;
//					par_ks.put("ksdm", map_ksInfo.get("KSDM"));
//					try {
//						list_kssj = dao.doSqlQuery(sb_sjs.toString(), par_ks);
//					} catch (PersistentDataOperationException e) {
//						e.printStackTrace();
//					}
//					int temp = Integer.parseInt(map_ksInfo.get("GHRQ") + "");
//					map_ksInfo.put("GHRQ1", GHRQ);
//					Boolean flag = false;
//					if (list_kssj.size() > 0)
//						for (Map<String, Object> map_tj : list_kssj) {// 当前日期+YYCSTS区间内如果有未提交的数据，则提交
//							if (GHRQ.equals(map_tj.get("GHRQ") + "")
//									&& map_ksInfo.get("ZBLB").equals(
//											map_tj.get("ZBLB")))// 过滤已存在的日期
//								flag = true;
//							// 除去被过滤的时间其他都要传
//						}
//					Map<String, Object> map_temp = new HashMap<String, Object>();// 处理遍历时map地址不变引起的数据更改
//					if (!flag) {
//						map_ksInfo.put("JGNAME", jgName);
//						map_ksInfo.put("JGID", jgid);
//						map_temp.putAll(map_ksInfo);
//						list_tj.add(map_temp);
//					}
//					map_ksInfo.put("GHRQ", temp);
//					// （如果中途做出预约天数、号源数量、第三方限额的改变，也不做处理，等日期到了没上传过的日期再做处理）
//				}
//			}
//		}
//		if (list_tj.size() > 0)
//			SaveDataInfo(list_tj);
//		 	List<Map<String , Object>> brxx = DownLoadYYInfo(jgid, ctx);
//		 	doYYGH(brxx);//将处理过的预约病人信息存入MS_YYGH 1.今天 2.根据预约时间排序
//	}
//
//	/**
//	 * 下载预约信息，插入 预约挂号 下载的字段暂定
//	 * 
//	 * 排班ID SCHID VARCHAR2(40) 医院代码 ORGCODE VARCHAR2(200) 医院名称 ORGNAME
//	 * VARCHAR2(200) 预约人姓名 NAME VARCHAR2(60) 性别： 性别代码XBDM 0 未知的性别 1 男性 9 未说明的性别
//	 * 2 女性 SEX VARCHAR2(10) 联系电话 PHONE VARCHAR2(100) 出生日期 BIRTH DATE 证件类型
//	 * CARDTYPE VARCHAR2(10) 证件号 CARDNO VARCHAR2(100) 医保卡号 MEDICARECARD
//	 * VARCHAR2(100) 诊疗卡号 CLINICNO VARCHAR2(100) 科室代码 DEPCODE VARCHAR2(40) 科室名称
//	 * DEPNAME VARCHAR2(60) 医生代码 DOCCODE VARCHAR2(40) 医生姓名 DOCNAME VARCHAR2(60)
//	 * 出诊日期 SCHDATE DATE 预约状态：预约成功，预约失败，预约取消 BOOKSTATUS varchar2(2) 取消预约原因
//	 * CANNELREASON VARCHAR2(400) 预约ID ORDERID VARCHAR2(40)
//	 * 
//	 * @param jgid
//	 * @return
//	 * @throws ParseException 
//	 */
//	public List<Map<String, Object>> DownLoadYYInfo(String jgid , Context ctx) throws ParseException {
//		// YYGH00005();待定
//		// 假设获取解析后的字段，现需要插入到MS_YYGH
//
//		// 匹配获取到的病人与本地数据中的病人，获取病人ID 调用saveBrxx()校验
//
//		// 然后保存到MS_YYGH和MS_HYB中
//		// 对新建档病人不做验证，直接根据上个方法
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Map<String, Object> paramMap_Can = new HashMap<String, Object>();
//		Map<String, Object> dataMap_can = new HashMap<String, Object>();
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		List<Map<String , Object>> list_today = new ArrayList<Map<String , Object>>();
//		if (jgid != null && jgid != "") {
//			dataMap_can.put("ORGCODE", jgid);
//			String strXML_BRXX = HsService.YYGH00005(paramMap_Can, dataMap_can);
//			try {
//				Boolean flag = ParseXML.ParseXMLForRows(strXML_BRXX, list);
//				if (!flag)
//					System.out.println("数据解析失败");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		//数据校验和排序
//		//校验病人
////		String[] strs = {"341800199411260522","610330196606273165"};//测试
////		int i = 0 ;
//		for (Map<String, Object> map_brxx : list) {
//			
//			//过滤掉不是今天的数据
//			Date SCHDate = sdf.parse(map_brxx.get("SCHDATE").toString());
//			Date today = new Date();
//			if(!isSameDate(SCHDate, today))   //测试，先关闭
//				continue;
//			
//			Map<String , Object> map_save = new HashMap<String, Object>();
//			map_save.put("personName", map_brxx.get("NAME"));
//			if(map_brxx.get("CARDTYPE").toString().equals("1"))
//				map_save.put("idCard", map_brxx.get("CARDNO"));
////				map_save.put("idCard", strs[i]);//测试
//			if(map_brxx.containsKey("CLINICNO")&&map_brxx.get("CLINICNO")!=""){
//				map_save.put("cardTypeCode", "04");
//				map_save.put("cardNo", map_brxx.get("CLINICNO"));
//			}
////				else if(map_brxx.containsKey("MEDICARECARD")&&map_brxx.get("MEDICARECARD")!=""){
////				map_save.put("cardTypeCode", "98");
////				map_save.put("cardNo", map_brxx.get("MEDICARECARD"));
////			} 
//			try {
//				long brid = saveBrxx(map_save , ctx);//返回BRID
//				map_brxx.put("BRID", brid);
//			} catch (ModelDataOperationException e) {
//				e.printStackTrace();
//			}
//			list_today.add(map_brxx);
////			i++;
//		}
//		List<Map<String , Object>> newList = ASCbyTime(list_today);
//		return newList;
//	}
//	
//	/**
//	 * 将第三方预约病人插入系统 MS_YYGH MS_HYB
//	 * @param newList
//	 * @throws ParseException 
//	 * @throws ModelDataOperationException 
//	 */
//	private void doYYGH(List<Map<String, Object>> newList) throws ParseException, ModelDataOperationException {//流程同诊间预约
//		int count_am = 0;
//		int count_pm = 0;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");
//	    StringBuffer sql_hy = new StringBuffer();
//	    List<Map<String , Object>> kyhy  = new ArrayList<Map<String,Object>>();
//	    List<Map<String , Object>> am = new ArrayList<Map<String,Object>>();
//	    List<Map<String , Object>> pm = new ArrayList<Map<String,Object>>();
//	    sql_hy.append("select SBXH as SBXH, GHKS as GHKS, ZBLB as ZBLB, GZRQ as GZRQ, JZSJ as JZSJ, SYTJ as SYTJ, SYBZ as SYBZ from MS_HYB  " +
//	    		"where " +
//	    		"GZRQ = trunc(sysdate) and " +  //测试，先关闭
//	    		"SYBZ = 0 and SYTJ = 1 and YSDM is null order by sbxh");
//	    try {
//	    	kyhy = dao.doSqlQuery(sql_hy.toString() , new HashMap<String , Object>());
//		} catch (PersistentDataOperationException e) {
//			e.printStackTrace();
//		}
//		
//	    for (Map<String, Object> map2 : kyhy) {
//			if(Integer.parseInt(map2.get("ZBLB")+"") == 1)
//				am.add(map2);
//			else
//				pm.add(map2);
//			
//		}
//	    
//	    for (Map<String, Object> map : newList) {
//			Date schDate = sdf.parse(map.get("SCHDATE").toString());
//			Calendar cal = new GregorianCalendar();
//			cal.setTime(schDate);
//		    int zblb  = cal.get(GregorianCalendar.AM_PM)+1;//返回0：上午 / 1.下午   +1 与zblb统一
//		    //获取sbxh
//		    String SBXH = "";
//		    if(zblb == 1 && count_am < am.size())
//		    	SBXH = am.get(count_am++).get("SBXH")+"";
//		    else if(zblb == 2 && count_pm < pm.size())
//		    	SBXH = pm.get(count_pm++).get("SBXH")+"";
//		    else
//		    	break;
//		    long sbxh =MedicineUtils.parseLong(SBXH);
//			long brid=MedicineUtils.parseLong(map.get("BRID"));
//			if(brid == 0)
//				continue;
//			Map<String,Object> map_hy=null;
//			try {
//				map_hy=dao.doLoad("phis.application.reg.schemas.MS_HYB", sbxh);
//			} catch (PersistentDataOperationException e) {
//				MedicineUtils.throwsException(logger, "省平台,查询号源失败", e);
//			}
//			if(map_hy==null||map_hy.size()==0){
//				throw new ModelDataOperationException("未找到号源记录");
//			}
//			if(MedicineUtils.parseInt(map_hy.get("SYBZ"))==1){
//				throw new ModelDataOperationException("该号源已经被使用,请刷新界面");
//			}
//			Map<String,Object> map_par=new HashMap<String,Object>();
//			map_par.put("ksdm", MedicineUtils.parseLong(map_hy.get("GHKS")));
//			map_par.put("zblb", MedicineUtils.parseInt(map_hy.get("ZBLB")));
//			map_par.put("yyrq", sdf_date.format((Date)map_hy.get("GZRQ")));
//			map_par.put("brid", brid);
//			try {
//				long l=dao.doCount("MS_YYGH", " KSDM=:ksdm and ZBLB=:zblb and to_char(YYRQ,'yyyy-mm-dd')=:yyrq and BRID=:brid", map_par);
//				if(l>0){
//					throw new ModelDataOperationException("省平台,该病人已预约该科室当天号,不能重复预约");
//				}
//			} catch (PersistentDataOperationException e) {
//				MedicineUtils.throwsException(logger, "省平台,查询是否已预约失败", e);
//			}
//			Map<String,Object> map_yygh=new HashMap<String,Object>();
//			map_yygh.put("JGID", MedicineUtils.parseString(map_hy.get("JGID")));
//			map_yygh.put("KSDM",  MedicineUtils.parseLong(map_hy.get("GHKS")));
//			map_yygh.put("YSDM", map_hy.get("YSDM"));
//			map_yygh.put("BRID", brid);
//			map_yygh.put("YYRQ", map_hy.get("GZRQ"));
//			map_yygh.put("ZBLB", MedicineUtils.parseInt(map_hy.get("ZBLB")));
//			try {
//				map_yygh.put("GHRQ", sdf_date.parse(sdf_date.format(new Date())));
//			} catch (ParseException e1) {
//				MedicineUtils.throwsException(logger, "挂号日期转换失败", e1);
//			}
//			map_yygh.put("HYXH", sbxh);
//			map_yygh.put("JZXH",  MedicineUtils.parseLong(map_hy.get("JZXH")));
//			map_yygh.put("GHBZ", 0);
//			try {
//				dao.doSave("create", "phis.application.cic.schemas.MS_YYGH", map_yygh, false);
//			} catch (ValidateException e) {
//				MedicineUtils.throwsException(logger, "省平台,保存预约挂号失败", e);
//			} catch (PersistentDataOperationException e) {
//				MedicineUtils.throwsException(logger, "省平台,保存预约挂号失败", e);
//			}
//			try {
//				dao.doUpdate("update MS_HYB set SYBZ=1 where SBXH="+sbxh, null);
//			} catch (PersistentDataOperationException e) {
//				MedicineUtils.throwsException(logger, "更新号源表失败", e);
//			}
//		}
//	}
//
//	/**
//	 * 对操作进行纪录，上传排班情况， 1.上传 2.记录
//	 * 
//	 * @author fengld
//	 * @createDate 2017-03-13
//	 * @param list_tj
//	 * @throws ModelDataOperationException
//	 */
//	private void SaveDataInfo(List<Map<String, Object>> list_tj)
//			throws ModelDataOperationException {
//		try {
//			updatePbInfo(list_tj);
//			for (Map<String, Object> map : list_tj) {
//				updatePbTime(map);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 在本地记录上传的动作，防止上传已处理数据
//	 * 
//	 * @param map_ksInfo
//	 * @throws ModelDataOperationException
//	 * @throws ParseException
//	 */
//	private void updatePbTime(Map<String, Object> map_ksInfo)
//			throws ModelDataOperationException, ParseException {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Map<String, Object> map_time = new HashMap<String, Object>();
//		map_time.put("KSDM", map_ksInfo.get("KSDM"));
//		map_time.put("JGID", map_ksInfo.get("JGID"));
//		map_time.put("KSMC", map_ksInfo.get("KSMC"));
//		map_time.put("MZKS", map_ksInfo.get("MZKS"));
//		map_time.put("GHRQ", sdf.parse(map_ksInfo.get("GHRQ1") + ""));
//		map_time.put("ZBLB", map_ksInfo.get("ZBLB"));
//		String sql = "Insert into YYGH_TBKSPB (KSDM,JGID,KSMC,MZKS,GHRQ,ZBLB) values (:KSDM,:JGID,:KSMC,:MZKS,:GHRQ,:ZBLB)";
//		try {
//			dao.doSqlUpdate(sql, map_time);
//		} catch (PersistentDataOperationException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 上传排班信息
//	 * 
//	 * @param list_tj
//	 */
//	private void updatePbInfo(List<Map<String, Object>> list_tj) {
//		List<Map<String, Object>> list_pb = new ArrayList<Map<String, Object>>();
//		for (Map<String, Object> map : list_tj) {
//			Map map_ = new HashMap<String, Object>();
//			map_.put("ORGCODE", map.get("JGID"));
//			map_.put("ORGNAME", map.get("JGNAME"));
//			map_.put("DEPCODE", map.get("KSDM"));
//			map_.put("DEPNAME", map.get("KSMC"));
//			map_.put("DOCCODE", "0");
//			map_.put("DOCNAME", "无医生");
//			map_.put("SCHDATE", map.get("GHRQ1") + "");
//			map_.put("TIMETYPE", map.get("ZBLB").toString().equals("1") ? "am"
//					: "pm");
//			String time_start = map.get("ZBLB").toString().equals("1") ? "08:30:00"
//					: "14:00:00";
//			String time_end = map.get("ZBLB").toString().equals("1") ? "11:30:00"
//					: "18:00:00";
//			map_.put("STARTTIME", map.get("GHRQ1") + " " + time_start);
//			map_.put("ENDTIME", map.get("GHRQ1") + " " + time_end);
//			map_.put("AMT", "0");
//			map_.put("SRCMAX", map.get("YYXE"));
//			map_.put("SRCNUM", "0");
//			map_.put("OUTCALLTYPE", "1");
//			map_.put("DEPINTRO", "-");
//			map_.put("DOCINTRO", "-");
//			list_pb.add(map_);
//		}
//		String xml_PBInfo = HsService.YYGH00001(new HashMap<String, Object>(),
//				list_pb);
//		System.out.println(xml_PBInfo);
//	}
//	
//	/**
//	 * 星期对应的序号转换成日期
//	 * 
//	 * @author fengld
//	 * @return
//	 * @createDate 2017-03-13
//	 */
//	private Map<String, Object> transWeek2Date() {// 可以传入周数参数进行整周的日期加减获得跨周的日期
//		Map<String, Object> map_WeekData = new HashMap<String, Object>();
//		Calendar calendar = Calendar.getInstance();
//		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {// 获取周日的日期
//			calendar.add(Calendar.DATE, -1);
//		}
//		for (int i = 0; i < 7; i++) {
//			Date date = calendar.getTime();
//			map_WeekData.put(i + "", date);
//			calendar.add(Calendar.DATE, 1);
//		}
//		return map_WeekData;
//	}
//
//	/**
//	 * 改为本周日开始的日期 返回预期产生预约的日期集合 注意:这里返回的下标是周日为 0 MS_KSPB中是 1 已过期的日期返回 "expired"
//	 * 由于在定时器触发，所以起始时间点设为今天起始的前一秒
//	 * 
//	 * @author fengld
//	 * @createDate 2017-03-13
//	 * @param yYCSTS
//	 * @return list
//	 */
//	private List<String> getDates(int yYCSTS) {
//		Date today = new Date();
//		Date endDay = new Date(today.getTime() + yYCSTS * 24 * 60 * 60 * 1000);
//		Calendar cal_today = Calendar.getInstance();
//		Calendar cal_end = Calendar.getInstance();
//		cal_today.setTime(today);
//		cal_end.setTime(endDay);
//		cal_today.set(Calendar.HOUR_OF_DAY, 0);
//		cal_today.set(Calendar.MINUTE, 0);
//		cal_today.set(Calendar.SECOND, 0);
//		cal_today.add(Calendar.SECOND, -1);// 昨天最后一刻
//		cal_end.set(Calendar.HOUR_OF_DAY, 0);
//		cal_end.set(Calendar.MINUTE, 0);
//		cal_end.set(Calendar.SECOND, 0);// 约定日期后一天第一时刻
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		List<String> list = new ArrayList<String>();
//		Calendar cur_date = Calendar.getInstance();
//		cur_date.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//		while (cur_date.before(cal_end)) {
//			if (cur_date.before(cal_today))
//				list.add("expired");
//			else
//				list.add(sdf.format(cur_date.getTime()));
//			cur_date.add(Calendar.DATE, 1);
//		}
//		return list;
//	}
//	
//	/**
//	 * 判断是否同一天
//	 * @param date1
//	 * @param date2
//	 * @return
//	 */
//	private static boolean isSameDate(Date date1, Date date2) {
//	       Calendar cal1 = Calendar.getInstance();
//	       cal1.setTime(date1);
//
//	       Calendar cal2 = Calendar.getInstance();
//	       cal2.setTime(date2);
//
//	       boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
//	               .get(Calendar.YEAR);
//	       boolean isSameMonth = isSameYear
//	               && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
//	       boolean isSameDate = isSameMonth
//	               && cal1.get(Calendar.DAY_OF_MONTH) == cal2
//	                       .get(Calendar.DAY_OF_MONTH);
//
//	       return isSameDate;
//	   }
///**
// * 对预约信息进行时间排序
// * @param list
// * @return
// */
//	private static List<Map<String , Object>> ASCbyTime(List<Map<String , Object>> list){
//		List<Map<String, Object>> yybr = new ArrayList<Map<String, Object>>();
//		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Collections.sort(list, new Comparator<Map<String, Object>>() {
//			@Override
//			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//				try {
//					return (sdf.parse(o1.get("SCHDATE").toString())).compareTo(sdf.parse(o2.get("SCHDATE").toString()));
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				return 0;
//			}
//		});
//
//		return list;
//		
//	}
}
