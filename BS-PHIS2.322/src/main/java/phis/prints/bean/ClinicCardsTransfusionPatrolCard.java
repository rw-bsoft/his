package phis.prints.bean;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.util.context.Context;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.SchemaUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * 输液巡视卡
 * 
 * @author gaof
 * 
 */
public class ClinicCardsTransfusionPatrolCard implements IHandler {

	int yzlb;// 医嘱卡片类型
	List<Map<String, Object>> list;// 取到的病人医嘱集合

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

		try {

			String dysj = request.get("dysj") + "";
			yzlb = Integer.parseInt((String) request.get("yzlb"));
			// yzlb = 2;// 卡片为4时 没数据 测试用

			List<Map<String, Object>> list = getList(request, ctx);
			Map<Long, List<Map<String, Object>>> brMap = new LinkedHashMap<Long, List<Map<String, Object>>>();
			Map<String, List<Map<String, Object>>> map_zh = new LinkedHashMap<String, List<Map<String, Object>>>();
			Map<Long, Integer> map_jj = new LinkedHashMap<Long, Integer>();
			// 用来判断有多少组医嘱
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				long YZZH = Long.parseLong(map.get("YPZH").toString());//
				long BRID = Long.parseLong(map.get("BRID").toString());// 病人ID
				List<Map<String, Object>> yzList = new ArrayList<Map<String, Object>>();
				map_zh.put(BRID + "&" + YZZH, yzList);
				List<Map<String, Object>> brEntry = new ArrayList<Map<String, Object>>();
				brMap.put(BRID, brEntry);
				map_jj.put(BRID, 1);
			}

			// 将同组的数据分别放到各自所属集合中
			for (int j = 0; j < list.size(); j++) {
				Map<String, Object> map = list.get(j);
				long BRID = Long.parseLong(map.get("BRID").toString());
				long YPZH = Long.parseLong(map.get("YPZH").toString());// 医嘱组号
				// Map<String,Object> pcmcMap = new HashMap<String, Object>();
				// map.put("PCMC", map.get("PCMC") + "");
				map.put("YZMC", (String) map.get("YPMC") + "");
				map.put("YCJL", map.get("YCJL") + "");
				// map.put("YCSL", map.get("YCSL") + "");
				map.put("JLDW", map.get("JLDW") + "");

				if (!map.containsKey("DS") || map.get("DS") == "") {
					map.put("DS", "");
				}
				if (!map.containsKey("QM") || map.get("QM") == "") {
					map.put("QM", "");
				}
				if (!map.containsKey("YCSL") || map.get("YCSL") == "") {
					map.put("YCSL", "");
				}
				if (!map.containsKey("PCMC") || map.get("PCMC") == "") {
					map.put("PCMC", "");
				}

				map.put("SJ", "");
				map_zh.get(BRID + "&" + YPZH).add(map);

			}
			List<Map<String, Object>> sortList2 = new ArrayList<Map<String, Object>>();
			Set<Entry<String, List<Map<String, Object>>>> set_zh = map_zh
					.entrySet();
			Iterator<Entry<String, List<Map<String, Object>>>> iter_zh = set_zh
					.iterator();

			while (iter_zh.hasNext()) {
				Entry<String, List<Map<String, Object>>> yzList = iter_zh
						.next();
				List<Map<String, Object>> zbList =  yzList.getValue();
				int zh_ = 0;
				Map<String, Object> map = null;
				for (int j = 0; j < zbList.size(); j++) {
					 map = yzList.getValue().get(j);
					 zh_ = map_jj.get(Long.parseLong(map.get("BRID") + ""));
					map.put("ZH", zh_ + "");
				}
				//map.put("ZH", zh_ + "");
				zh_++;
				map_jj.put(Long.parseLong(map.get("BRID") + ""), zh_);
				brMap.get(Long.parseLong(map.get("BRID") + "")).addAll(
						yzList.getValue());
				/*
				 * String YZZXSJ = map.get("YZZXSJ") + ""; int SRCS =
				 * Integer.parseInt(map.get("SRCS") + ""); String[] ZXSJ =
				 * YZZXSJ.split("-");
				 * 
				 * Timestamp KSSJ = (Timestamp) map.get("KSSJ"); Calendar KS =
				 * Calendar.getInstance(); KS.setTime(KSSJ); int ksMonth =
				 * KS.get(Calendar.MONTH) + 1; int ksYear =
				 * KS.get(Calendar.YEAR); int ksDay = KS.get(Calendar.DATE);
				 */

				/*
				 * if (ZXSJ.length != 0) { int j = 0;// 用于首日次数的打印限制 int x =
				 * ZXSJ.length - SRCS; for (int i = 0; i < ZXSJ.length; i++) {
				 * if (ZXSJ[i].length() == 2) { ZXSJ[i] = ZXSJ[i] + ":00"; }
				 * else if (ZXSJ[i].length() == 1) { ZXSJ[i] = "0" + ZXSJ[i] +
				 * ":00"; } Date zxDate = sdf.parse(nowYear + "-" + nowMouth +
				 * "-" + nowDay + " " + ZXSJ[i] + ":00");
				 * 
				 * if (ksYear == nowYear && ksMonth == nowMouth && ksDay ==
				 * nowDay) {// 如果开嘱时间与今天是同一天 // 则判断今天医嘱从什么时间开始打印 if (j < x) {
				 * j++; continue; } }
				 * 
				 * if (map.get("TZSJ") != null) { Timestamp TZSJ = (Timestamp)
				 * map.get("TZSJ"); Calendar TZ = Calendar.getInstance();
				 * TZ.setTime(TZSJ); int tzMonth = TZ.get(Calendar.MONTH) + 1;
				 * int tzYear = TZ.get(Calendar.YEAR); int tzDay =
				 * TZ.get(Calendar.DATE); if (tzYear == nowYear && tzMonth ==
				 * nowMouth && tzDay == nowDay) {// 如果停嘱时间与今天是同一天 //
				 * 则判断今天医嘱从什么时间开始打印 if (TZSJ.getTime() - zxDate.getTime() <= 0)
				 * { continue; } } } brMap.get(Long.parseLong(map.get("BRID") +
				 * "")).addAll(yzList.getValue()); } }
				 */

			}

			Set<Entry<Long, List<Map<String, Object>>>> set_br = brMap
					.entrySet();
			Iterator<Entry<Long, List<Map<String, Object>>>> iter_br = set_br
					.iterator();
			while (iter_br.hasNext()) {
				Entry<Long, List<Map<String, Object>>> yzEntry = iter_br.next();
				List<Map<String, Object>> yzList = yzEntry.getValue();
				Map<String, Object> map = yzList.get(0);

				Map<String, Object> o = new HashMap<String, Object>();
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
				o.put("Date", dysj);// 日期
				o.put("BRXM", map.get("BRXM") + "");
				o.put("MZHM", map.get("MZHM") + "");
				if (map.get("BRXB").toString().equals("1")) {
					o.put("BRXB", "男");
				} else {
					o.put("BRXB", "女");
				}
				o.put("CSNY", age + "");
				// o.put("KSMC", map.get("KSMC") + "");
				// o.put("BRCH", map.get("BRCH") + "");
				o.put("PCMC", PrintUtil.ds(yzEntry.getValue()));
				sortList2.add(o);

			}
			//listSort(sortList2);
			records.addAll(sortList2);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		// 获得项目的绝对路径 并将路径值传进SUBREPORT_DIR中,
		// 因子报表SUBREPORT_DIR参数值在不同服务器上路径不同,所以用动态路径传值
		String realPath = ClinicCardsTransfusionPatrolCard.class.getResource(
				"/").getPath();
		realPath = realPath.substring(1, realPath.indexOf("WEB-INF") + 7);
		String[] path = realPath.split("/");
		String usePath = "";
		for (int i = 0; i < path.length; i++) {
			if (i == 0) {
				usePath = path[i];
			} else {
				usePath += "\\\\" + path[i];
			}
		}
		usePath = usePath.replace("%20", " ");
		response.put("SUBREPORT_DIR", usePath
				+ "\\classes\\phis\\prints\\jrxml\\");
	}

	public List<Map<String, Object>> getList(Map<String, Object> request,
			Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		StringBuffer sql = new StringBuffer();

		List<Map<String, Object>> body = new ArrayList<Map<String, Object>>();

		try {
			// 获取表单参数
			String arr_zyh = (String) request.get("arr_zyh");// 获得选中的医嘱病人住院号的集合
			if (arr_zyh.length() != 0) {// 如果没有传进用户医嘱人
				String[] zyh=arr_zyh.split(",");
				List<String> l=new ArrayList<String>();
				for(String z:zyh){
					l.add(z);
				}
				// String wardId = (String) request.get("wardId");// 病区代码

				// 获得系统参数中关于 医嘱符合标识 的设置
				// "FHYZHJF","1","医嘱不复核是否可进行后续业务FHYZHJF，0:可以进行，1:不可以进行"
				/*
				 * int FHBZ = Integer.parseInt(ParameterUtil.getParameter(JGID,
				 * "FHYZHJF", ctx));
				 */

				sql.append("select b.BZXX as DS,a.fphm as FPHM,to_char(A.KFRQ,'yyyy-mm-dd') as KFRQ,");
				sql.append(" b.mrcs AS MRCS,B.YPXH AS YPXH, B.YPXH AS YPXH,   B.YCSL AS YCSL,  B.YCJL AS YCJL, B.YPYF AS YPYF, B.YPZH AS YPZH,");
				sql.append(" c.Jldw as JLDW,");
				sql.append(" E.BRXM AS BRXM,E.BRID AS BRID ,E.MZHM AS MZHM , E.BRXB AS BRXB,E.CSNY  as CSNY,D.PCMC as PCMC ,C.YPMC AS YPMC ");
				sql.append(" from ms_cf01 a, ms_cf02 b, YK_TYPK c, GY_SYPC d, ms_brda e");
				sql.append(" WHERE a.cfsb = b.cfsb and b.ypxh = c.ypxh  and e.brid = a.brid  and b.ypyf = d.pcbm and b.gytj in (select YPYF from zy_ypyf where xmlb = 4) ");
				sql.append(" and a.fphm in (:fphms)");
				sql.append("  and a.cflx = '1' and a.JGID = :JGID  ORDER BY  b.YPZH,b.SBXH");// a.fphm ASC, b.ypzh ASC

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("JGID", JGID);// 机构ID
				parameters.put("fphms", l);
				body = dao.doSqlQuery(sql.toString(), parameters);
				if(body !=null && body.size()>0){
					for(int i=0;i<body.size();i++){
						String ds=body.get(i).get("DS")+"";
						if (ds == null  ||  "".equals(ds) || "null".equals(ds)) {
							body.get(i).put("DS", "");
						}
					}
				}
			}

		} catch (Exception e) {
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
			records = SchemaUtil
					.setDictionaryMassageForList(records, "GY_SYPC");

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
				long a1 = Long.parseLong(o1.get("BRCH") + "");
				long a2 = Long.parseLong(o2.get("BRCH") + "");
				if (a1 > a2) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

}
