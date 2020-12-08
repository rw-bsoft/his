package phis.prints.bean;

import java.math.BigDecimal;
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
import java.util.TreeMap;

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
 * 输液巡视卡
 * 
 * @author gaof
 * 
 */
public class OrderCardsTransfusionPatrolCard implements IHandler {

	int yzlb;// 医嘱卡片类型
	List<Map<String, Object>> list;// 取到的病人医嘱集合

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

		try {

			yzlb = Integer.parseInt((String) request.get("yzlb"));
			// yzlb = 2;// 卡片为4时 没数据 测试用
			String dysj = request.get("dysj") + "";

			List<Map<String, Object>> list = getList(request, ctx);

			Map<Long, List<Map<String, Object>>> zhMap = new HashMap<Long, List<Map<String, Object>>>();
			// 用来判断有多少病人
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				long BRID = (Long) map.get("BRID");//
				List<Map<String, Object>> yzEntry = new ArrayList<Map<String, Object>>();
				zhMap.put(BRID, yzEntry);
			}

			// 将同组的数据分别放到各自所属集合中
			for (int j = 0; j < list.size(); j++) {
				Map<String, Object> map = list.get(j);
				long BRID = (Long) map.get("BRID");
				map.put("YZMC", (String) map.get("YZMC") + "");
				map.put("YCJL", map.get("YCJL") + "");
				map.put("JLDW", map.get("JLDW") == null ? "" : map.get("JLDW")
						+ "");
				map.put("YPYF",map.get("XMMC") == null ? "":map.get("XMMC") + "");//药品用法
				map.put("YPSL",map.get("YCSL") == null ? "" : map.get("YCSL") + "");//药品数量
				if (!map.containsKey("SJ") || map.get("SJ") == "") {
					map.put("SJ", "");
				}
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

				zhMap.get(BRID).add(map);
			}
			// 取出每个组的医嘱 并分别打印到前台页面
			Set<Entry<Long, List<Map<String, Object>>>> set = zhMap.entrySet();
			Iterator<Entry<Long, List<Map<String, Object>>>> iter = set
					.iterator();
			while (iter.hasNext()) {
				Entry<Long, List<Map<String, Object>>> yzEntry = iter.next();
				List<Map<String, Object>> yzList = yzEntry.getValue();
				Map<String, Object> map = yzList.get(0);
				// 处理组号
				long YZZH = 0;
				int zh = 1;
				for (int k = 0; k < yzList.size(); k++) {
					Map<String, Object> map_yz = yzList.get(k);
					if (k == 0) {
						YZZH = (Long) map_yz.get("YZZH");
						map_yz.put("ZH", zh + "");
						// zh++;
					} else {
						if ((Long) map_yz.get("YZZH") == YZZH) {

						} else {
							YZZH = (Long) map_yz.get("YZZH");
							zh++;
							map_yz.put("ZH", zh + "");
						}
					}
				}
				/**
				 * //增加4行空行 for(int a=0;a<4;a++){ Map<String, Object>
				 * map_add=new HashMap<String, Object>(); map_add.put("ZH", "");
				 * map_add.put("YZMC", ""); map_add.put("YCJL", "");
				 * map_add.put("JLDW", ""); map_add.put("SJ", "");
				 * map_add.put("DS", ""); map_add.put("QM", "");
				 * map_add.put("PCMC", ""); map_add.put("YCSL", "");
				 * yzList.add(map_add); }
				 */
				String csrq = map.get("CSNY") + "";
				String AGEString = "";
				if (!"".equals(csrq) && csrq != null) {
					String t2 = csrq.replace('-', '/');
					Date dt2 = new Date(t2);
					long i = (new Date().getTime() - dt2.getTime())
							/ (1000 * 60 * 60 * 24);
					long AGE = i / 365;
					AGEString = AGE + "";
				}

				Map<String, Object> o = new HashMap<String, Object>();
				o.put("Date",dysj.replaceAll("-", "."));// 日期
				o.put("BRXM", map.get("BRXM") + "");
				o.put("AGE", AGEString);
				o.put("BRCH", map.get("BRCH") + "");
				o.put("PCMC", PrintUtil.ds(yzEntry.getValue()));
				records.add(o);
			}
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
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		// 获得项目的绝对路径 并将路径值传进SUBREPORT_DIR中,
		// 因子报表SUBREPORT_DIR参数值在不同服务器上路径不同,所以用动态路径传值
		String realPath = OrderCardsTransfusionPatrolCard.class
				.getResource("/").getPath();
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
		String dysj=request.get("dysj")+"";
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

				sql.append("SELECT a.ZFYP as ZFYP,a.YZMC as YZMC,a.YPLX as YPLX,a.YPXH as YPXH,a.KSSJ as KSSJ,b.ZXSJ as SJ,"
						+ " a.YCSL as YCSL,a.YCJL as YCJL,a.YZZH as YZZH,a.MZCS as MZCS,a.SYPC as SYPC,"
						+ " d.BRCH as BRCH,d.BRXM as BRXM,d.CSNY as CSNY,d.BRID as BRID,a.MRCS as MRCS,b.PCMC as PCMC,c.XMMC as XMMC,"
						+ " a.BZXX as BZXX,a.JLXH as JLXH,NVL(e.JLDW,' ') as JLDW,d.ZYHM as ZYHM,a.SRKS as SRKS,"
						+ " a.BZXX as BZXX,'  ' AS ZH,'  ' AS BH"
						+ " FROM GY_SYPC b,ZY_BQYZ a,ZY_YPYF c,ZY_BRRY d,YK_TYPK e"
						+ " WHERE ( a.YPXH = e.YPXH ) and ( b.PCBM = a.SYPC ) and  ( a.ZYH = d.ZYH ) and"
						+ " ( c.YPYF = a.YPYF ) and ( d.CYPB = 0 ) AND ( d.JGID = :JGID ) AND"
						+ " ( a.ZYH in ("
						+ arr_zyh
						+ ") OR (a.ZYH = -1 AND SRKS = :BQDM)) AND "
						+ " ( a.LSBZ = 0 ) AND ( a.YCSL > 0 ) AND ( a.YPLX > 0 ) AND ( a.XMLX = 1 or a.XMLX = 2 or a.XMLX = 3  ) AND"
						+ " ( a.YZPB = 0 ) AND ( c.XMLB = 4 ) and ( :FHBZ = 0 or a.FHBZ = 1)"
						+ " and (a.TZSJ is null or to_char(a.TZSJ,'yyyy-mm-dd')>=:TZSJ )");

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("BQDM", Long.parseLong(wardId));// 当前病区
				parameters.put("JGID", JGID);// 机构ID
				parameters.put("FHBZ", FHBZ);// 系统配置参数中 符合标识
				parameters.put("TZSJ", dysj);
				if(lsbz!=3){//add by LIZHI 2018-01-08增加长临标志筛选
					sql.append(" and a.LSYZ=:LSBZ");
					parameters.put("LSBZ", lsbz);
				}
				sql.append(" ORDER BY d.BRCH ASC,a.YZZH ASC,b.ZXSJ ASC");
				body = dao.doQuery(sql.toString(), parameters);
				if (!"".equals(body) && body.size() > 0) {
					List<String> sjlist=new ArrayList<String>();
					List<Map<String, Object>> pcList = new ArrayList<Map<String, Object>>();// 更具频次返回的List
					Map<String, List<Map<String, Object>>> tempmap=new HashMap<String, List<Map<String,Object>>>();
					for (int k = 0; k < body.size(); k++) {
						Map<String, Object> keyMap = new HashMap<String, Object>();
						keyMap = body.get(k);
						String keySj = keyMap.get("SJ") + "";
						if (!"".equals(keySj) && keySj != null) {
							String temp[] = keySj.split("-");
							if (!"".equals(temp) && temp != null
									&& temp.length > 0) {
								for (int j = 0; j < temp.length; j++) {
									Map<String, Object> keyMap1 = new HashMap<String, Object>(keyMap);
									String pcSj = new String();
									pcSj = temp[j];
									int m=0;
									for(m=0;m<sjlist.size();m++){
										if(sjlist.get(m).equals(pcSj)){
											break;
										}
									}
									if(m==sjlist.size()){
										sjlist.add(pcSj);
									}
									keyMap1.put("SJ", pcSj);
									if(tempmap.get(pcSj)==null){
										List<Map<String, Object>> tempone=new ArrayList<Map<String,Object>>();
										tempone.add(keyMap1);
										tempmap.put(pcSj,tempone);
									}else{
										tempmap.get(pcSj).add(keyMap1);
									}
								}
							} else {
								pcList.add(0, keyMap);
							}
						}
					}
					for(int i=0;i<sjlist.size();i++){
						pcList.addAll(tempmap.get(sjlist.get(i)));
					}
					body = pcList;
				}
				body = SchemaUtil.setDictionaryMassageForList(body,
						BSPHISEntryNames.ZY_BQYZ);
				for (Map<String, Object> m : body) {
					if ("1".equals(m.get("ZFYP") + "")) {
						m.put("YZMC", "(自备)" + m.get("YZMC"));
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
}
