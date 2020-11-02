package phis.prints.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.util.context.Context;

/**
 * 静滴卡
 * 
 * @author Liws
 * 
 */
public class OrderCardsStillDripCard implements IHandler {

	int yzlb;// 医嘱卡片类型
	List<Map<String, Object>> list;// 取到的病人医嘱集合

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

		try {

			yzlb = Integer.parseInt((String) request.get("yzlb"));
			String dysj = request.get("dysj") + "";
			// yzlb = 2;// 卡片为4时 没数据 测试用

			List<Map<String, Object>> list = getList(request, ctx);
			List<Map<String, Object>> sortList = new ArrayList<Map<String, Object>>();

			Map<String, List<Map<String, Object>>> zhMap = new HashMap<String, List<Map<String, Object>>>();
			// 用来判断有多少组医嘱
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				long YZZH = (Long) map.get("YZZH");// 医嘱组号
				List<Map<String, Object>> yzList = new ArrayList<Map<String, Object>>();
				zhMap.put(YZZH + "", yzList);
			}

			// 将同组的数据分别放到各自所属集合中
			for (int j = 0; j < list.size(); j++) {
				Map<String, Object> map = list.get(j);
				long YZZH = (Long) map.get("YZZH");

				// Map<String,Object> pcmcMap = new HashMap<String, Object>();
				map.put("PCMC", map.get("PCMC") + "");
				map.put("YZMC", (String) map.get("YZMC") + "");
				map.put("YCJL", map.get("YCJL") + "");
				map.put("YCSL", map.get("YCSL") + "");
				map.put("JLDW", map.get("JLDW") == null ? "" : map.get("JLDW")
						+ "");

				zhMap.get(YZZH + "").add(map);
			}
			// 取出每个组的医嘱 并分别打印到前台页面
			Set<Entry<String, List<Map<String, Object>>>> set = zhMap
					.entrySet();
			Iterator<Entry<String, List<Map<String, Object>>>> iter = set
					.iterator();
			while (iter.hasNext()) {
				Entry<String, List<Map<String, Object>>> yzList = iter.next();

				Map<String, Object> map = yzList.getValue().get(0);

				Date csny = (Date) map.get("CSNY");// 出生日期
				// 出生日期
				Calendar cs = Calendar.getInstance();
				cs.setTime(csny);
				int borthYear = cs.get(Calendar.YEAR);
				int borthMouth = cs.get(Calendar.MONTH) + 1;
				int borthDay = cs.get(Calendar.DATE);
				// 当前时间
				Date nowDate = new Date();
				Calendar c = Calendar.getInstance();// 可以对每个时间域单独修改
				c.setTime(nowDate);
				int nowYear = c.get(Calendar.YEAR);
				int nowMouth = c.get(Calendar.MONTH) + 1;
				int nowDay = c.get(Calendar.DATE);
				// 计算年龄
				int age = nowYear - borthYear - 1;
				if (borthMouth < nowMouth || borthMouth == nowMouth
						&& borthDay <= nowDay) {
					age++;
				}

				String YZZXSJ = map.get("YZZXSJ") + "";
				int SRCS = Integer.parseInt(map.get("SRCS") + "");
				String[] ZXSJ = YZZXSJ.split("-");

				Timestamp KSSJ = (Timestamp) map.get("KSSJ");
				Calendar KS = Calendar.getInstance();
				KS.setTime(KSSJ);
				int ksMonth = KS.get(Calendar.MONTH) + 1;
				int ksYear = KS.get(Calendar.YEAR);
				int ksDay = KS.get(Calendar.DATE);

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");

				if (ZXSJ.length != 0) {
					int j = 0;// 用于首日次数的打印限制
					int x = ZXSJ.length - SRCS;
					for (int i = 0; i < ZXSJ.length; i++) {
						Map<String, Object> o = new HashMap<String, Object>();

						if (ZXSJ[i].length() == 2) {
							ZXSJ[i] = ZXSJ[i] + ":00";
						} else if (ZXSJ[i].length() == 1) {
							ZXSJ[i] = "0" + ZXSJ[i] + ":00";
						}
						Date zxDate = sdf.parse(nowYear + "-" + nowMouth + "-"
								+ nowDay + " " + ZXSJ[i] + ":00");

						if (ksYear == nowYear && ksMonth == nowMouth
								&& ksDay == nowDay) {// 如果开嘱时间与今天是同一天
							// 则判断今天医嘱从什么时间开始打印
							if (j < x) {
								j++;
								continue;
							}
							// if (KSSJ.getTime() - zxDate.getTime() >= 0) {
							// continue;
							// }
						}

						if (map.get("TZSJ") != null) {
							Timestamp TZSJ = (Timestamp) map.get("TZSJ");
							Calendar TZ = Calendar.getInstance();
							TZ.setTime(TZSJ);
							int tzMonth = TZ.get(Calendar.MONTH) + 1;
							int tzYear = TZ.get(Calendar.YEAR);
							int tzDay = TZ.get(Calendar.DATE);
							if (tzYear == nowYear && tzMonth == nowMouth
									&& tzDay == nowDay) {// 如果停嘱时间与今天是同一天
								// 则判断今天医嘱从什么时间开始打印
								if (TZSJ.getTime() - zxDate.getTime() <= 0) {
									continue;
								}
							}
						}

						o.put("BRNL", age + "");// 病人年龄
						o.put("Date", dysj.replaceAll("-","."));// 日期
						// o.put("Time", ZXSJ[i]);// 医嘱执行时间
						o.put("BRXM", map.get("BRXM") + "");
						o.put("BRCH", map.get("BRCH") + "");
						o.put("PCMC", PrintUtil.ds(yzList.getValue()));
						sortList.add(o);
					}
				}
			}
			listSort(sortList);
			
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();
			List<Map<String, Object>> doubleList = new ArrayList<Map<String, Object>>();
			if(ParameterUtil.getParameter(jgid, "JDKLS", ctx).equals("1")){
				for (int i = 0; i < sortList.size() ; i++) {
					Map<String, Object> map_ = new HashMap<String, Object>();
					map_.putAll(sortList.get(i));
					doubleList.add(map_);
				}
			}else{
			// 竖直排序两列
			int sisi = sortList.size() % 2 == 0 ? sortList.size() / 2
					: sortList.size() / 2 + 1;
			for (int i = 0; i < (sortList.size() % 2 == 0 ? sortList.size() / 2
					: sortList.size() / 2 + 1); i++) {
				Map<String, Object> map_ = new HashMap<String, Object>();
				map_.putAll(sortList.get(i));
				if (sisi < sortList.size()) {
					Map<String, Object> map_2 = sortList.get(sisi);
					Set<String> key_set = map_2.keySet();
					for (String key : key_set) {
						map_.put(key + "2", map_2.get(key));
					}
					sisi++;
				}
				doubleList.add(map_);
			}
			}
			// records.addAll(sortList);
			records.addAll(doubleList);
			/******************add by lizhi 2018-04-10根据床号排序*****************/
			Collections.sort(records, new Comparator<Map<String, Object>>() {
	            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
	                String BRCH1=(String) o1.get("BRCH");
					String BRCH2=(String) o2.get("BRCH");
	                Integer ch1 = Integer.valueOf(BRCH1.replace("-", "")) ;//ch1是从你list里面拿出来的一个 
	                Integer ch2 = Integer.valueOf(BRCH2.replace("-", "")) ; //ch2是从你list里面拿出来的第二个BRCH
	                return ch1.compareTo(ch2);
	            }
	        });
			/******************add by lizhi 2018-04-10根据床号排序*****************/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		// 获得项目的绝对路径 并将路径值传进SUBREPORT_DIR中,
		// 因子报表SUBREPORT_DIR参数值在不同服务器上路径不同,所以用动态路径传值
		String realPath = OrderCardsStillDripCard.class.getResource("/")
				.getPath();
		realPath = realPath.substring(1, realPath.indexOf("WEB-INF"));
		String[] path = realPath.split("/");
		String usePath = "";
		String xg = System.getProperty("file.separator");
		for (int i = 0; i < path.length; i++) {
			if (i == 0) {
				usePath = path[i];
			} else {
				usePath += xg + path[i];
			}
		}
		usePath = usePath.replace("%20", " ");
		response.put("SUBREPORT_DIR", xg + usePath + xg + "WEB-INF" + xg
				+ "classes" + xg + "phis" + xg + "prints" + xg + "jrxml" + xg);
	}

	public List<Map<String, Object>> getList(Map<String, Object> request,
			Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		StringBuffer sql = new StringBuffer();
		int lsbz = 3;
		if(request.get("orderTypeValue")!=null && !"undefined".equals(request.get("orderTypeValue")+"")){
			lsbz = Integer.parseInt(request.get("orderTypeValue")+"");
		}
		List<Map<String, Object>> body = new ArrayList<Map<String, Object>>();

		try {
			// 获取表单参数
			String arr_zyh = (String) request.get("arr_zyh");// 获得选中的医嘱病人住院号的集合
			if (arr_zyh.length() != 0) {// 如果没有传进用户医嘱人

				String wardId = (String) request.get("wardId");// 病区代码

				// 获得系统参数中关于 医嘱符合标识 的设置
				// "FHYZHJF","1","医嘱不复核是否可进行后续业务FHYZHJF，0:可以进行，1:不可以进行"
				int FHBZ = Integer.parseInt(ParameterUtil.getParameter(JGID,
						"FHYZHJF", ctx));

				sql.append("SELECT b.ZFYP as ZFYP,b.YCSL as YCSL,b.YZMC as YZMC,b.YPLX as YPLX,b.YPXH as YPXH,b.KSSJ as KSSJ,b.TZSJ as TZSJ,b.YCSL as YCSL,b.YCJL as YCJL,"
						+ "b.YZZH as YZZH,b.SYPC as SYPC,d.BRCH as BRCH,d.BRXM as BRXM,d.CSNY as CSNY,b.MRCS as MRCS,b.SRCS as SRCS,a.PCMC as PCMC,"
						+ "c.XMMC as XMMC,b.BZXX as BZXX,b.JLXH as JLXH,e.JLDW as JLDW,d.ZYHM as ZYHM,b.SRKS as SRKS,b.YEPB as YEPB,"
						+ "b.YZZXSJ as YZZXSJ FROM GY_SYPC a,ZY_BQYZ b,ZY_YPYF c,ZY_BRRY d,YK_TYPK e WHERE ( b.YPXH = e.YPXH ) "
						+ "and ( a.PCBM = b.SYPC ) and  ( b.ZYH = d.ZYH ) and ( c.YPYF = b.YPYF ) and  ( d.CYPB = 0 ) AND  "
						+ "( b.ZYH in ("
						+ arr_zyh
						+ ")) AND  ( b.SRKS = :BQDM ) AND ( b.LSBZ = 0 ) AND  ( b.YCSL > 0) "
						+ "AND  ((((:YZLB = 2 OR :YZLB = 3 OR  :YZLB = 4) AND b.YPLX > 0 AND (b.XMLX = 1 or b.XMLX = 2 or b.XMLX = 3 ) ) "
						+ "OR  :YZLB = 5 OR :YZLB = 9) AND  c.XMLB = :YZLB) AND "
						+ "( b.YSBZ = 0 OR (b.YSBZ = 1 AND b.YSTJ = 1)) AND ( b.YZPB = 0)  and "
						+ "b.JGID = :JGID and ( :FHBZ = 0 or b.FHBZ = 1)");

				Map<String, Object> parameters = new HashMap<String, Object>();
				// parameters.put("arr_zyh", zyh);
				parameters.put("BQDM", Long.parseLong(wardId));// 当前病区
				parameters.put("YZLB", yzlb);// 卡片类型
				parameters.put("JGID", JGID);// 机构ID
				parameters.put("FHBZ", FHBZ);// 系统配置参数中 符合标识
				if(lsbz!=3){//add by LIZHI 2018-01-08增加长临标志筛选
					sql.append(" and b.LSYZ=:LSBZ");
					parameters.put("LSBZ", lsbz);
				}
				sql.append(" ORDER BY d.BRCH ASC,b.YZZH ASC");
				body = dao.doQuery(sql.toString(), parameters);
				body = SchemaUtil.setDictionaryMassageForList(body,
						BSPHISEntryNames.ZY_BQYZ);

				Iterator<Map<String, Object>> iter = body.iterator();
				while (iter.hasNext()) {
					Map<String, Object> rec = iter.next();
					String sypc = (String) rec.get("SYPC");// 使用频次
					Date kssj = (Date) rec.get("KSSJ");// 开嘱时间
					Date nowDate = new Date();
					int zxbz = validOrders(sypc, kssj, nowDate, ctx);
					if (zxbz != 1) {
						// iter.remove();
						body.remove(rec);
					}
				}
				for (Map<String, Object> m : body) {
					if ("1".equals(m.get("ZFYP") + "")) {
						m.put("YZMC", "(自备)" + m.get("YZMC"));
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return body;
	}

	/**
	 * 判断医嘱有效性
	 * 
	 * @param sypc
	 *            使用频次
	 * @param kssj
	 *            开嘱时间
	 * @param nowDate
	 *            当前时间
	 * @return 1:需执行 0:不需执行 -1:有错误发生
	 */
	public int validOrders(String sypc, Date kssj, Date nowDate, Context ctx) {

		try {
			BaseDAO dao = new BaseDAO(ctx);
			List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
			StringBuffer sql = new StringBuffer();
			sql.append("select PCBM as PCBM,PCMC as PCMC,MRCS as MRCS,ZXZQ as ZXZQ,RLZ as RLZ,ZXSJ as ZXSJ,RZXZQ as RZXZQ from GY_SYPC where PCBM = :sypc");

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("sypc", sypc);

			records = dao.doSqlQuery(sql.toString(), parameters);
			records = SchemaUtil.setDictionaryMassageForList(records,
					BSPHISEntryNames.GY_SYPC);

			if (records.size() != 0) {// PCBM 是主键,最多只能搜到一条数据
				int zxbz = 0;

				Map<String, Object> map = records.get(0);
				int zxzq = ((BigDecimal) map.get("ZXZQ")).intValue();
				int rlz = ((BigDecimal) map.get("RLZ")).intValue();
				String rzxzq = (String) map.get("RZXZQ");
				if (zxzq == 1) {
					return 1;// 最小周期为1,则必定执行
				}
				if (rzxzq.length() != zxzq) {
					return -1;// 最小周期和日执行周期中字符个数不符
				}
				if (rlz == 1) {
					int zqbz = dayNumber(nowDate);// 周期标志
					zxbz = Integer.parseInt(rzxzq.substring(zqbz, zqbz + 1));
				} else {
					int days = (int) ((nowDate.getTime() - kssj.getTime()) / (1000 * 60 * 60 * 24)) + 1;
					days = days % zxzq;
					if (days == 0) {
						days = zxzq;
					}
					zxbz = Integer.parseInt(rzxzq.substring(days, days + 1));// 0或1
				}
				return zxbz;
			} else {
				return -1;// 无法找到使用频次
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/**
	 * 根据日期判断星期几 确定周期执行标志：星期日,1;星期一,2;星期二,3;星期三,4;星期四,5;星期五,6;星期六,7; 再根据周期执行标志
	 * 截取字符串 并将其转换成数字
	 * 
	 * @param nowDate
	 * @return
	 */
	public int dayNumber(Date nowDate) {

		Calendar c = Calendar.getInstance();
		c.setTime(nowDate);
		int dayForWeek = c.get(Calendar.DAY_OF_WEEK);

		return dayForWeek;
	}

	public void listSort(List<Map<String, Object>> resultList) {
		Collections.sort(resultList, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String BRCH1=(String) o1.get("BRCH");
				String BRCH2=(String) o2.get("BRCH");
				long a1 = Long.parseLong(BRCH1.replace("-", ""));
				long a2 = Long.parseLong(BRCH2.replace("-", ""));
				if (a1 > a2) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}
}
