package phis.prints.bean;

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
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;


import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.util.context.Context;
/**
 * 医嘱卡片--执行单
 * 
 * @author Liws
 * 
 */
public class OrderCardsMouthCardZX implements IHandler {

	int yzlb;// 医嘱卡片类型

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

		yzlb = Integer.parseInt((String) request.get("yzlb"));
//		yzlb = 2;
		List<Map<String, Object>> list = getList(request, ctx);

		Map<String, List<Map<String, Object>>> zhMap = new HashMap<String, List<Map<String, Object>>>();
		// 用来判断有多少组医嘱
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			String ZYHM = (String) map.get("ZYHM");// 住院号码分组
			List<Map<String, Object>> yzList = new ArrayList<Map<String, Object>>();
			zhMap.put(ZYHM, yzList);
		}

		// 将同组的数据分别放到各自所属集合中
		for (int j = 0; j < list.size(); j++) {
			Map<String, Object> map = list.get(j);
			String ZYHM = (String) map.get("ZYHM");

			map.put("PCMC", map.get("PCMC") + "");
			map.put("YZMC", (String) map.get("YZMC") + "");
			map.put("YCJL", map.get("YCJL") + "");
			map.put("ZYHM", ZYHM);
			map.put("JLDW", map.get("JLDW") == null ? "":map.get("JLDW") + "");
			//当频次为 Q1H时 由于执行时间是 0-1-2-3-...-23 在前台执行单的执行时间显示不下 所以暂显示成00-23
			if(((String)map.get("PCMC")).equals("Q1H")){
				map.put("YZZXSJ", "00-23");
			}else{
				map.put("YZZXSJ", (String) map.get("YZZXSJ"));
			}
			if (map.get("BZXX") == null) {
				map.put("BZXX", "");
			}
			zhMap.get(ZYHM).add(map);
		}
		// 取出每个组的医嘱 并分别打印到前台页面
		Set<Entry<String, List<Map<String, Object>>>> set = zhMap.entrySet();
		Iterator<Entry<String, List<Map<String, Object>>>> iter = set
				.iterator();
		while (iter.hasNext()) {
			Entry<String, List<Map<String, Object>>> yzList = iter.next();

			Map<String, Object> map = yzList.getValue().get(0);

			Map<String, Object> o = new HashMap<String, Object>();

			Date csny = (Date) map.get("CSNY");// 出生日期

			// 出生日期
			Calendar cs = Calendar.getInstance();
			cs.setTime(csny);
			int borthYear = cs.get(Calendar.YEAR);
			int borthMouth = cs.get(Calendar.MONTH)+1;
			int borthDay = cs.get(Calendar.DATE);
			// 当前时间
			Date nowDate = new Date();
			Calendar c = Calendar.getInstance();// 可以对每个时间域单独修改
			c.setTime(nowDate);
			int nowYear = c.get(Calendar.YEAR);
			int nowMouth = c.get(Calendar.MONTH)+1;
			int nowDay = c.get(Calendar.DATE);
			// 计算年龄
			int age = nowYear - borthYear - 1;
			if (borthMouth < nowMouth || borthMouth == nowMouth
					&& borthDay <= nowDay) {
				age++;
			}

			o.put("BRNL", age + "");// 病人年龄
			o.put("BRXM", map.get("BRXM") + "");
			o.put("BRCH", map.get("BRCH") + "");
			o.put("ZYHM", PrintUtil.ds(yzList.getValue()));
			records.add(o);
		}
		/******************add by lizhi 2018-04-10根据床号排序*****************/
		Collections.sort(records, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer ch1 = Integer.valueOf(o1.get("BRCH").toString()) ;//ch1是从你list里面拿出来的一个 
                Integer ch2 = Integer.valueOf(o2.get("BRCH").toString()) ; //ch2是从你list里面拿出来的第二个BRCH
                return ch1.compareTo(ch2);
            }
        });
		/******************add by lizhi 2018-04-10根据床号排序*****************/
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		// 获得项目的绝对路径 并将路径值传进SUBREPORT_DIR中,
		// 因子报表SUBREPORT_DIR参数值在不同服务器上路径不同,所以用动态路径传值
		String realPath = OrderCardsMouthCardZX.class.getResource("/").getPath();
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
		response.put("SUBREPORT_DIR", xg+usePath
				+ xg+"WEB-INF"+xg+"classes"+xg+"phis"+xg+"prints"+xg+"jrxml"+xg);
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

				sql.append("SELECT b.ZFYP as ZFYP,b.YZMC as YZMC,b.YPLX as YPLX,b.YPXH as YPXH,b.KSSJ as KSSJ,b.TZSJ as TZSJ,b.YCSL as YCSL,b.YCJL as YCJL,"
						+ "b.YZZH as YZZH,b.SYPC as SYPC,d.BRCH as BRCH,d.BRXM as BRXM,d.CSNY as CSNY,b.MRCS as MRCS,a.PCMC as PCMC,"
						+ "c.XMMC as XMMC,b.BZXX as BZXX,b.JLXH as JLXH,e.JLDW as JLDW,d.ZYHM as ZYHM,b.SRKS as SRKS,b.YEPB as YEPB,"
						+ "b.YZZXSJ as YZZXSJ FROM GY_SYPC a,ZY_BQYZ b,ZY_YPYF c,ZY_BRRY d,YK_TYPK e WHERE ( b.YPXH = e.YPXH ) "
						+ "and ( a.PCBM = b.SYPC ) and  ( b.ZYH = d.ZYH ) and ( c.YPYF = b.YPYF ) and  ( d.CYPB = 0 ) AND  "
						+ "( b.ZYH in ("
						+ arr_zyh
						+ ")) AND  ( b.SRKS = :BQDM ) AND ( b.LSBZ = 0 ) AND  ( b.YCSL > 0) "
						+ "AND  ((((:YZLB = 2 OR :YZLB = 3 OR  :YZLB = 4) AND b.YPLX > 0 AND b.XMLX = 1 ) "
						+ "OR  :YZLB = 5 OR :YZLB = 9) AND  c.XMLB = :YZLB) AND "
						+ "( b.YSBZ = 0 OR (b.YSBZ = 1 AND b.YSTJ = 1)) AND ( b.YZPB = 0)  and "
						+ "b.JGID = :JGID and ( :FHBZ = 0 or b.FHBZ = 1)");

				Map<String, Object> parameters = new HashMap<String, Object>();
				// parameters.put("arr_zyh", zyh);
				if(yzlb==5)
					yzlb=4;
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
				body = SchemaUtil.setDictionaryMassageForList(body, BSPHISEntryNames.ZY_BQYZ);
				//update by caijy at 2014.10.9 for 自备药打印需要备注
				for(Map<String,Object> m:body){
					if("1".equals(m.get("ZFYP")+"")){
						m.put("YZMC", "(自备)"+m.get("YZMC"));
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return body;
	}
}
