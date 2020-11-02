package phis.prints.bean; 
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context; 

public class SummaryMedicinesOutOfStorageFile implements IHandler {

	public static  List<Map<String,Object>>  Listrecords;
	public void getFields(Map<String, Object> req,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	
		records.addAll(Listrecords);
	}

	@Override
	public void getParameters(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGMC = user.getManageUnitName();
		//	String jgid = user.get("manageUnit.id");// 用户的机构ID
			String jgid = user.getManageUnitId();// 用户的机构ID  
		Long yksb =  Long.parseLong(req.get("kfsb")+"");
		String ldt_start = req.get("ksrq") + "";
		String ldt_end = req.get("jsrq") + "";
		String hql_CKFS = "select CKFS as CKFS,FSMC as FSMC from YK_CKFS where XTSB = :al_yksb and jgid = :al_jgid";
		String hql_crkhz_1 = "select min(CKDH) as CKDH_b , max(CKDH) as CKDH_e, count(CKDH) as DJSL  from YK_CK01 where JGID = :GL_JGID  AND to_char(CKRQ,'yyyymmdd') >= :ldt_start and to_char(CKRQ,'yyyymmdd') <= :ldt_end and CKFS = :li_ckfs  and XTSB = :il_yksb  and CKPB = 1";
		String hql_crkhz_2 = "select nvl(sum(YK_CK02.JHJE),0) as JJHJ, nvl(sum(YK_CK02.PFJE),0) as PFHJ, nvl(sum(YK_CK02.LSJE),0) as LSHJ "+
							 " from YK_CK01 as YK_CK01 , YK_CK02 as YK_CK02"+
							" where to_char(YK_CK01.CKRQ,'yyyymmdd') >= :ldt_start "+
							"  and to_char(YK_CK01.CKRQ,'yyyymmdd') <= :ldt_end"+
							"  and YK_CK02.CKFS = :li_ckfs"+
							"  and YK_CK02.XTSB = :il_yksb"+
							"  AND YK_CK01.XTSB = YK_CK02.XTSB"+
							"  AND YK_CK01.JGID = :GL_JGID"+
							"  and YK_CK01.CKFS = YK_CK02.CKFS"+
							"  and YK_CK01.CKDH = YK_CK02.CKDH"+
							"  and YK_CK01.CKPB = 1";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_yksb", yksb);
		parameters.put("al_jgid", jgid);
		Map<String, Object> parameters_2 = new HashMap<String, Object>();
		parameters_2.put("GL_JGID", jgid);
		parameters_2.put("ldt_start", ldt_start);
		parameters_2.put("ldt_end", ldt_end);
		parameters_2.put("il_yksb", yksb);
		try {
			List<Map<String,Object>> ckfs_list = dao.doQuery(hql_CKFS, parameters); //���ⷽʽ����
			List<Map<String,Object>> crkhz_list = new ArrayList<Map<String,Object>>();
			DecimalFormat df = new DecimalFormat("#0.00");
			double DJSLHJ = 0d;
			double JJHJHJ = 0d;
			double PFHJHJ = 0d;
			double LSHJHJ = 0d;
			double CEHJ = 0d;
			//����ÿ�ֳ��ⷽʽ��������ݡ�
			for(Map<String,Object> map : ckfs_list){
				parameters_2.put("li_ckfs", map.get("CKFS"));
				Map<String,Object> temp_map =new HashMap<String, Object>();
				temp_map.putAll(dao.doQuery(hql_crkhz_1, parameters_2).get(0));
				temp_map.putAll((dao.doQuery(hql_crkhz_2, parameters_2).get(0)));
				temp_map.put("CKFS", map.get("CKFS"));
				double ce = Double.parseDouble(temp_map.get("LSHJ")+"")-Double.parseDouble(temp_map.get("JJHJ")+"");
				if(df.format(ce).equals("-0.00"))
					ce=0.00;
				temp_map.put("CE",ce);
				//��ŷ�Χ
				if(!"null".equals(temp_map.get("CKDH_b")+"") && !"null".equals(temp_map.get("CKDH_e")+"")){
					temp_map.put("BHFW", temp_map.get("CKDH_b")+" - "+temp_map.get("CKDH_e"));
				}else if("null".equals(temp_map.get("CKDH_b")+"") && "null".equals(temp_map.get("CKDH_e")+"")){
					temp_map.put("BHFW","-");
				}else {
					temp_map.put("BHFW","null".equals(temp_map.get("CKDH_b"))?"null".equals(temp_map.get("CKDH_e")):"null".equals(temp_map.get("CKDH_b")));
				}
				DJSLHJ += Double.parseDouble(temp_map.get("DJSL")+"");
				JJHJHJ += Double.parseDouble(temp_map.get("JJHJ")+"");
				PFHJHJ += Double.parseDouble(temp_map.get("PFHJ")+"");
				LSHJHJ += Double.parseDouble(temp_map.get("LSHJ")+"");
				CEHJ += Double.parseDouble(temp_map.get("CE")+"");
				
				temp_map.put("FSMC", map.get("FSMC"));
				temp_map.put("JJHJ", df.format(Double.parseDouble(temp_map.get("JJHJ")+"")));
				temp_map.put("PFHJ", df.format(Double.parseDouble(temp_map.get("PFHJ")+"")));
				temp_map.put("LSHJ", df.format(Double.parseDouble(temp_map.get("LSHJ")+"")));
				temp_map.put("CE", df.format(Double.parseDouble(temp_map.get("CE")+"")));
				
			    crkhz_list.add(temp_map);
			}
			Listrecords=crkhz_list;
			res.put("JGMC", JGMC);
			res.put("JJHJHJ", df.format(JJHJHJ));
			res.put("PFHJHJ", df.format(PFHJHJ));
			res.put("LSHJHJ", df.format(LSHJHJ));
			res.put("CEHJ", df.format(CEHJ));
			res.put("DJSLHJ", df.format(DJSLHJ));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
