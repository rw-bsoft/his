package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class ItemizeSummaryFile implements IHandler {

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		String datefrom = (String) request.get("datefrom");
		String dateto = (String) request.get("dateTo");
		String sql1 = "select a.SFXM as SFXM,sum(a.SFJE) as SFJE "
				+ "from MS_RBMX a,MS_HZRB b " + "where b.CZGH = a.CZGH "
				+ "and b.JZRQ = a.JZRQ " + "and b.JGID =:al_jgid "
				+ "and b.HZRQ >= :adt_begin " + "and b.HZRQ <=:adt_end "
				+ "group by a.SFXM";
		String sql2 = "select FYMC XMMC,0.00 XMJE from GY_YLSF";
		String sql3 = "SELECT SFXM as SFXM,FYFL as FYFL,SFMC as SFMC FROM GY_SFXM where SFXM=:SFXM";
		String sql4 = "select sum(b.GHJE) as GHJE,sum(b.ZLJE) as ZLJE,"
				+ "sum(b.ZJFY) as ZJFY,sum(b.BLJE) as BLJE "
				+ "from MS_GHRB a,MS_GRMX b where a.CZGH = b.CZGH "
				+ "and a.JZRQ = b.JZRQ " + "and a.JGID = :al_jgid "
				+ "and a.HZRQ >= :adt_begin " + "and a.HZRQ <= :adt_end";
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("al_jgid", JGID);
			parameters.put("adt_begin", sdfdate.parse(datefrom + " 00:00:00"));
			parameters.put("adt_end", sdfdate.parse(dateto + " 23:59:59"));
			List<Map<String, Object>> mxList = dao.doQuery(sql1, parameters);
			List<Map<String, Object>> ghList = dao.doQuery(sql4, parameters);
			List<Map<String, Object>> zhList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> ypList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> fyList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> gresult = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < mxList.size(); i++) {
				Map<String, Object> m = mxList.get(i);
				parameters.clear();
				parameters.put("SFXM",
						Long.parseLong(m.get("SFXM").toString()));
				Map<String, Object> xmMap = dao.doLoad(sql3, parameters);
				m.put("FYFL", xmMap.get("FYFL"));
				m.put("SFMC", xmMap.get("SFMC"));
				if ("1".equals(xmMap.get("FYFL").toString())) {
					fyList.add(m);
				} else if ("2".equals(xmMap.get("FYFL").toString())) {
					ypList.add(m);
				} else if ("3".equals(xmMap.get("FYFL").toString())) {
					fyList.add(m);
				}
				zhList.add(m);
			}
			int row = Math.max(5, ypList.size());
			row = Math.max(row, fyList.size() / 2 + 1);
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> m = ghList.get(0);
			map.put("SFMC1", "挂号金额");
			map.put("SFJE1", String.format("%1$.2f",m.get("GHJE")));
			gresult.add(map);
			map = new HashMap<String, Object>();
			map.put("SFMC1", "诊疗金额");
			map.put("SFJE1", String.format("%1$.2f",m.get("ZLJE")));
			gresult.add(map);
			map = new HashMap<String, Object>();
			map.put("SFMC1", "专家费用");
			map.put("SFJE1", String.format("%1$.2f",m.get("ZJFY")));
			gresult.add(map);
			map = new HashMap<String, Object>();
			map.put("SFMC1", "病历金额");
			map.put("SFJE1", String.format("%1$.2f",m.get("BLJE")));
			gresult.add(map);
			map = new HashMap<String, Object>();
			map.put("SFMC1", "就诊卡金额");
			map.put("SFJE1", "");
			gresult.add(map);
			for (int i = 0; i < row; i++) {
				if (gresult.size() > i) {
					map = gresult.get(i);
				} else {
					map = new HashMap<String, Object>();
				}
				if (map == null) {
					map = new HashMap<String, Object>();
				}
				m = null;
				if (ypList.size() > i) {
					m = ypList.get(i);
					map.put("SFMC2", m.get("SFMC"));
					map.put("SFJE2", String.format("%1$.2f",m.get("SFJE")));
				}
				if (fyList.size() > i * 2) {
					m = fyList.get(i * 2);
					map.put("SFMC3", m.get("SFMC"));
					map.put("SFJE3", String.format("%1$.2f",m.get("SFJE")));
				}
				if (fyList.size() > i * 2 + 1) {
					m = fyList.get(i * 2 + 1);
					map.put("SFMC4", m.get("SFMC"));
					map.put("SFJE4", String.format("%1$.2f",m.get("SFJE")));
				}
				result.add(map);
			}
			records.addAll(result);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		String rq = sdf.format(new Date());
		String datefrom = request.get("datefrom").toString();
		String dateto = request.get("dateTo").toString();
		response.put("ZBRQ", rq);
		response.put("ZBDW", user.getManageUnitName());
		response.put("ZBR", user.getUserName());
		
		String sql1 = "select a.SFXM as SFXM,sum(a.SFJE) as SFJE,max(b.HZRQ) as JSRQ "
				+ "from MS_RBMX a,MS_HZRB b "
				+ "where b.CZGH = a.CZGH "
				+ "and b.JZRQ = a.JZRQ "
				+ "and b.JGID =:al_jgid "
				+ "and b.HZRQ >= :adt_begin "
				+ "and b.HZRQ <=:adt_end "
				+ "group by a.SFXM";
//		String sql2 = "select FYMC XMMC,0.00 XMJE from GY_YLSF";
		String sql3 = "SELECT SFXM as SFXM,FYFL as FYFL,SFMC as SFMC FROM GY_SFXM where SFXM=:SFXM";
		String sql4 = "select sum(b.GHJE) as GHJE,sum(b.ZLJE) as ZLJE,"
				+ "sum(b.ZJFY) as ZJFY,sum(b.BLJE) as BLJE,min(a.HZRQ) as KSRQ,sum(a.ZHJE) as ZFZF,sum(b.YZJM) as YZJM,sum(a.YHJE) as GHYHHJ "//增加挂号优惠合计 zhaojian 2019-05-12
				+ "from MS_GHRB a,MS_GRMX b where a.CZGH = b.CZGH "
				+ "and a.JZRQ = b.JZRQ " + "and a.JGID = :al_jgid "
				+ "and a.HZRQ >= :adt_begin " + "and a.HZRQ <= :adt_end";
		String sql5 = "select sum(b.QTYS) as XJJE,sum(b.QTYS) as JZJE,sum(b.ZFZF) as ZFZF "
			+ " from MS_HZRB b "
			+ "where b.JGID =:al_jgid "
			+ "and b.HZRQ >= :adt_begin "
			+ "and b.HZRQ <=:adt_end ";
		String sql6 = "select sum(b.ZHJE) as ZFZF "
				+ " from MS_GHRB b "
				+ "where b.JGID =:al_jgid "
				+ "and b.HZRQ >= :adt_begin "
				+ "and b.HZRQ <=:adt_end ";
		String sql7 = "select sum(a.YHJE) as GHYHHJ "//增加挂号优惠合计 hujian 2020-04-30
				+ "from MS_GHRB a where  "
				+ "  a.JGID = :al_jgid "
				+ "and a.HZRQ >= :adt_begin " + "and a.HZRQ <= :adt_end";
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("al_jgid", JGID);
			parameters.put("adt_begin", sdfdate.parse(datefrom + " 00:00:00"));
			parameters.put("adt_end", sdfdate.parse(dateto + " 23:59:59"));
			List<Map<String, Object>> mxList = dao.doQuery(sql1, parameters);
			List<Map<String, Object>> ghList = dao.doQuery(sql4, parameters);
			List<Map<String, Object>> ghyhList = dao.doQuery(sql7, parameters);
			Map<String, Object> ghmap = dao.doLoad(sql5, parameters);
			Map<String, Object> ghmap1 = dao.doLoad(sql6, parameters);
			
			double HJ = 0;
			double GHSF = 0;
			double YPSF = 0;
			double SFXM = 0;
			double XJJE = 0;
			double YZJM = 0;//挂号义诊减免
			double GHYHHJ = 0;//挂号优惠合计
			//double ZHJE =0;
			//double JZJE =0;
			Date JSRQ = null;
//			String fkxx="";
			for (int i = 0; i < mxList.size(); i++) {
				Map<String, Object> m = mxList.get(i);
//				if (JSRQ == null) {
//					JSRQ = (Date) m.get("JSRQ");
//				}else{
//					if(JSRQ.getTime()<((Date)m.get("JSRQ")).getTime()){
//						JSRQ = (Date) m.get("JSRQ");
//					}
//				}
				parameters.clear();
				parameters.put("SFXM",
						Long.parseLong(m.get("SFXM").toString()));
				Map<String, Object> xmMap = dao.doLoad(sql3, parameters);
				m.put("FYFL", xmMap.get("FYFL"));
				m.put("SFMC", xmMap.get("SFMC"));
				if ("1".equals(xmMap.get("FYFL").toString())) {
					SFXM += (Double) m.get("SFJE");
				} else if ("2".equals(xmMap.get("FYFL").toString())) {
					YPSF += (Double) m.get("SFJE");
				} else if ("3".equals(xmMap.get("FYFL").toString())) {
					SFXM += (Double) m.get("SFJE");
				}
				HJ += (Double) m.get("SFJE");
			}
			if (ghList != null && ghList.size() > 0) {
				GHSF += getDoubleValue(ghList.get(0).get("GHJE"));
				HJ += getDoubleValue(ghList.get(0).get("GHJE"));
				GHSF += getDoubleValue(ghList.get(0).get("ZLJE"));
				HJ += getDoubleValue(ghList.get(0).get("ZLJE"));
				GHSF += getDoubleValue(ghList.get(0).get("ZJFY"));
				HJ += getDoubleValue(ghList.get(0).get("ZJFY"));
				GHSF += getDoubleValue(ghList.get(0).get("BLJE"));
				HJ += getDoubleValue(ghList.get(0).get("BLJE"));
				YZJM = BSHISUtil.doublesum(YZJM,getDoubleValue(ghList.get(0).get("YZJM")));
				GHYHHJ = BSHISUtil.doublesum(GHYHHJ,getDoubleValue(ghyhList.get(0).get("GHYHHJ")));
				
			}
			String KSRQ=null;
//			if(ghList != null && ghList.size() > 0&&ghList.get(0).get("KSRQ")!=null){
//				KSRQ=sdfdate.format(ghList.get(0).get("KSRQ"));
//			}
//			if(KSRQ==null){
//				KSRQ=datefrom + " 00:00:00";
//			}
			//汇总时间显示为查询
			KSRQ=datefrom;
			response.put("KSRQ", KSRQ);
//			if(JSRQ==null){
//				JSRQ=sdfdate.parse(dateto + " 23:59:59");
//			}
//			response.put("JSRQ", sdfdate.format(JSRQ));
			XJJE = BSHISUtil.doublesub(HJ,BSHISUtil.doublesum(getDoubleValue(ghmap.get("JZJE")),BSHISUtil.doublesum(YZJM,GHYHHJ)));
			response.put("JSRQ", dateto);
			response.put("GHSF", String.format("%1$.2f", GHSF));
			response.put("YPSF", String.format("%1$.2f", YPSF));
			response.put("SFXM", String.format("%1$.2f", SFXM));
			response.put("HJ", String.format("%1$.2f", HJ));
			response.put("HJDX", BSPHISUtil.changeMoneyUpper(String.format("%1$.2f", HJ)));
//			response.put("ZHJE", String.format("%1$.2f", getDoubleValue(ghmap.get("ZHJE"))));
			response.put("XJJE", String.format("%1$.2f", XJJE));
			response.put("JZJE", String.format("%1$.2f", getDoubleValue(ghmap.get("JZJE"))));
//			response.put("TCZF", String.format("%1$.2f", getDoubleValue(ghmap.get("TCZF"))));
			
			parameters.clear();
			parameters.put("al_jgid", JGID);
			parameters.put("adt_begin", sdfdate.parse(datefrom + " 00:00:00"));
			parameters.put("adt_end", sdfdate.parse(dateto + " 23:59:59"));
//			fkxx="现金:"+String.format("%1$.2f", XJJE)+" ";
			
//       //性质不为“0”时的按收费性质统计的QTYS
//			List<Map<String, Object>> map_xztj = new ArrayList<Map<String, Object>>();
//			StringBuffer sql_xztj = new StringBuffer();
//			sql_xztj.append(" select c.XZDM as XZDM,d.XZMC as XZMC,c.QTYS as QTYS from (select  b.BRXZ as XZDM,sum(a.QTYS) as QTYS ");
//			sql_xztj.append(" from MS_MZXX a left join GY_BRXZ b  on a.brxz = b.brxz ");
//			sql_xztj.append(" where a.JGID=:al_jgid and a.HZRQ>=:adt_begin and a.HZRQ <=:adt_end  and b.DBPB!='0' ");
//			sql_xztj.append("  group by  b.BRXZ) c left join GY_BRXZ d on c.XZDM=d.brxz "); 
//			map_xztj = dao.doSqlQuery(sql_xztj.toString(), parameters);
//			if (map_xztj != null && map_xztj.size() != 0) {
//				 for(int i=0;i<map_xztj.size();i++){
//					 fkxx = fkxx +map_xztj.get(i).get("XZMC")+ ":"
//								+ String.format("%1$.2f",map_xztj.get(i).get("QTYS"))
//								+ " ";
//				 }
//			} 
//			
//		 //性质为“0”时的统计QTYS
//			List<Map<String, Object>> map_qtys = new ArrayList<Map<String, Object>>();
//			StringBuffer sql_qtys = new StringBuffer();
//			sql_qtys.append(" select a.CZGH,b.DBPB as DBPB,sum(a.QTYS) as QTYS ");
//			sql_qtys.append("from MS_MZXX a left join GY_BRXZ b on a.brxz=b.brxz ");
//			sql_qtys.append(" where a.JGID=:al_jgid and a.HZRQ>=:adt_begin and a.HZRQ <=:adt_end  and b.DBPB='0' ");
//			sql_qtys.append(" group by b.DBPB , a.CZGH "); 
//			map_qtys = dao.doSqlQuery(sql_qtys.toString(), parameters);
//			if (map_qtys != null && map_qtys.size() != 0) {
//				    fkxx = fkxx + "记账:"
//								+ String.format("%1$.2f",map_qtys.get(0).get("QTYS"))
//								+ " ";
//			}
			String sql_fkfs = "select c.FKFS as FKFS,sum(c.FKJE) as FKJE,d.FKMC as FKMC from ("+
					"select a.FKFS as FKFS,a.FKJE as FKJE from MS_FKXX a,MS_MZXX b where a.MZXH = b.MZXH and b.JGID = :jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq"+
					" union all "+
					"select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_FKXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID = :jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq"+
					" union all "+
					"select a.FKFS as FKFS,a.FKJE as FKJE from MS_GH_FKXX a,MS_GHMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq"+
					" union all "+
					"select a.FKFS as FKFS,(-1*a.FKJE) as FKJE from MS_GH_FKXX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID = :jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq"+
					") c left outer join GY_FKFS d on c.FKFS = d.FKFS group by c.FKFS,d.FKMC order by c.FKFS";
			String sql_brxz = "select sum(c.QTYS) as QTYS,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("+
					"select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_MZXX a where a.JGID=:jgid and a.HZRQ >=:ksrq and a.HZRQ <=:jsrq"+
					" union all "+
					"select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_MZXX a,MS_ZFFP b where a.MZXH = b.MZXH and b.JGID=:jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq" +
					" union all "+
					"select a.BRXZ as BRXZ,a.QTYS as QTYS from MS_GHMX a where a.JGID=:jgid and a.HZRQ >=:ksrq and a.HZRQ <=:jsrq"+
					" union all "+
					"select a.BRXZ as BRXZ,(-1*a.QTYS) as QTYS from MS_GHMX a,MS_THMX b where a.SBXH = b.SBXH and b.JGID=:jgid and b.HZRQ >=:ksrq and b.HZRQ <=:jsrq" +
					") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
			Map<String, Object> parameters2 = new HashMap<String, Object>();
			parameters2.put("jgid", JGID);
//			parameters2.put("mzlb",Long.parseLong(BSPHISUtil.getMZLB(JGID, dao) + ""));
//			parameters2.put("hzrq", cdate.getTime());
			parameters2.put("ksrq", sdfdate.parse(datefrom + " 00:00:00"));
			parameters2.put("jsrq", sdfdate.parse(dateto + " 23:59:59"));
			List<Map<String, Object>> ids_fkfs = dao.doSqlQuery(sql_fkfs,parameters2);
			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(sql_brxz,parameters2);
			String  qtysFb="";
			String jzjeSt="0.00";
			if (ids_fkfs  != null && ids_fkfs .size() != 0) {
				 for(int n=0;n<ids_fkfs.size();n++){
						 qtysFb = qtysFb +ids_fkfs.get(n).get("FKMC")+ ":"
								+ String.format("%1$.2f",ids_fkfs.get(n).get("FKJE"))
								+ " ";
				 }
			}
			qtysFb = qtysFb + "挂号减免:"+BSHISUtil.doublesum(YZJM,GHYHHJ) + " ";//增加优惠合计 zhaojian 2018-06-20
			if (ids_brxz  != null && ids_brxz .size() != 0) {
				 for(int n=0;n<ids_brxz.size();n++){
					 if(ids_brxz.get(n).get("DBPB") != null && Integer.parseInt(ids_brxz.get(n).get("DBPB")+"")==0){
						 jzjeSt= String.format("%1$.2f",parseDouble(jzjeSt) +parseDouble(ids_brxz.get(n).get("QTYS")+ ""));
					 }else{
						 qtysFb = qtysFb +ids_brxz.get(n).get("XZMC")+ ":"
								+ String.format("%1$.2f",parseDouble(ids_brxz.get(n).get("QTYS")+ ""))
								+ " ";
					 }
				 }
				HashMap<String, Object> temp=new HashMap<String, Object>();
				temp.put("ZFZF",Double.parseDouble(ghmap.get("ZFZF")==null?"0":ghmap.get("ZFZF")+"")
						+Double.parseDouble(ghmap1.get("ZFZF")==null?"0":ghmap1.get("ZFZF")+""));
				 qtysFb = qtysFb+"账户:"+temp.get("ZFZF")+" 记账 :"+jzjeSt+" ";
				 //qtysFb = qtysFb+" "+"记账 :"+jzjeSt+" ";
			}
			response.put("fkxx",qtysFb);
			
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private double getDoubleValue(Object value) {
		if (value == null) {
			return 0.0;
		}
		return (Double) value;

	}
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
}
