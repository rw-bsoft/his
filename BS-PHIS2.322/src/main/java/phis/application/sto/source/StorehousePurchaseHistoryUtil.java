package phis.application.sto.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.ServiceCode;
/**
 * 药库采购历史模块的有关操作(包括组装sql， 业务流程等)
 * @author Administrator
 *
 */
public class StorehousePurchaseHistoryUtil {
	
	//查询类型
	public static String QUERYTYPE = "QUERYTYPE";
	
	//1:按药品查询 方式并且为查询药品记录
	public static String DRUG_RECORD = "1";
	//2:按药品查询方式并且查询药品明细
	public static String DRUG_DETAILS = "2";
	//3: 按供应商查询方式并且查询供应商记录
	public static String SUPPLIER_RECORD = "3";
	//4:按供应商查询方式并且查询供应商明细
	public static String SUPPLIER_DETAILS = "4";
	
	/**
	 * 组装查询药品记录sql(不用)
	 * @param body
	 * @return
	 */
	public String drugRecord(Map<String, Object> body){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT b.YPMC as YPMC, b.YPGG as YPGG, b.YPDW as YPDW, a.CDMC as CDMC, b.PYDM as PYDM,");
		sqlBuilder.append(" b.WBDM as WBDM, b.JXDM as JXDM, b.QTDM as QTDM, c.YPXH as YPXH, c.YPCD as YPCD, 0.0000 as RKSL ,");
		sqlBuilder.append(" 0.0000 as JHJE, 0.0000 as PFJE, 0.0000 as LSJE  FROM YK_CDDZ a, YK_TYPK b, YK_YPCD c where 1 = 0");
		return sqlBuilder.toString();
	}
	/**
	 * 组装查询药品明细sql(不用)
	 * @param body
	 * @return
	 */
	public String drugDetails(Map<String, Object> body){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(" SELECT a.RKDH as RKDH, a.RKRQ as RKRQ, a.CWPB as CWPB, d.DWMC DWMC , d.PYDM as PYDM, b.LSJG as LSJG ,");
		sqlBuilder.append(" b.PFJG as PFJG, b.RKSL as RKSL, b.FPHM as FPHM, c.YPMC as YPMC, c.YPGG as YPGG, c.YPDW as YPDW, b.YPXH as YPXH,");
		sqlBuilder.append(" b.YPCD as YPCD, b.JHJG as JHJG, b.JHHJ as JHHJ, b.PFJE as PFJE, b.LSJE as LSJE, b.FKRQ as FKRQ, b.YPPH as YPPH,");
		sqlBuilder.append(" c.ZBLB as ZBLB, a.RKFS as RKFS FROM YK_RK01 a, YK_RK02 b, YK_TYPK c, YK_JHDW d ");
		return sqlBuilder.toString();
	}
	/**
	 * 组装查询供应商记录sql(不用)
	 * @param body
	 * @return
	 */
	public String supplierRecord(Map<String, Object> body){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT DWXH as DWXH, DWMC as DWMC, PYDM as PYDM, 0.0000 as JHJE, 0.0000 as PFJE, 0.0000 as LSJE FROM YK_JHDW"); 
		return sqlBuilder.toString();
	}
	/**
	 * 组装查询供应商明细sql(不用)
	 * @param body
	 * @return
	 */
	public String supplierDetails(Map<String, Object> body){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT a.RKDH as RKDH, a.RKRQ as RKRQ, a.CWPB as CWPB, b.LSJG as LSJG, b.PFJG as PFJG, b.RKSL as RKSL,");  
		sqlBuilder.append(" b.FPHM as FPHM, c.YPMC as YPMC, c.YPGG as YPGG, c.YPDW as YPDW, c.PYDM as PYDM, b.YPXH as YPXH,");
		sqlBuilder.append(" b.YPCD as YPCD, b.JHJG as JHJG, b.JHHJ as JHHJ, b.PFJE as PFJE, b.LSJE as LSJE, '' as CDMC");
		sqlBuilder.append(" FROM YK_RK01 a, YK_RK02 b, YK_TYPK c "); 
		return sqlBuilder.toString();
	}
	/**
	 * 组装数据源sql
	 * @param body  有以下参数
	 * 			QUERYTYPE 根据其值组装不同的查询条件，无值时不添加任何条件
	 * 					1 、按药品查询 方式并且为查询药品记录
	 * 		   			2、按药品查询方式并且查询药品明细
				   		3、 按供应商查询方式并且查询供应商记录
				   		4、按供应商查询方式并且查询供应商明细
				   		
				YPXH   QUERYTYPE为2时必需有值
				YPCD   QUERYTYPE为2时必需有值
				DWXH   QUERYTYPE为4时必需有值
	 * @return
	 */
	public String dataSource(Map<String, Object> body){
		/**
		 * 查询类型 1 、按药品查询 方式并且为查询药品记录
		 * 		   2、按药品查询方式并且查询药品明细
				   3、 按供应商查询方式并且查询供应商记录
				   4、按供应商查询方式并且查询供应商明细
		 */
		String queryType = String.valueOf(body.get(QUERYTYPE));
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,a.PYDM as PYDM,a.WBDM as WBDM,a.JXDM as JXDM,");
		sqlBuilder.append(" a.YPDM as YPDM,a.YPSX as YPSX, a.ZBLB as ZBLB, a.ABC as ABC,a.DJJB as DJJB,a.YPDC as YPDC, ");
		sqlBuilder.append(" a.TSYP as TSYP,b.CDMC as CDMC,d.YPXH as YPXH,d.YPCD as YPCD,d.RKSL as RKSL,d.JHJG as JHJG,d.PFJG as PFJG,");
		sqlBuilder.append(" d.LSJG as LSJG,c.DWXH as DWXH ,c.CWPB as CWPB,c.RKDH as RKDH,c.RKRQ as RKRQ,d.FPHM as FPHM,");
		sqlBuilder.append("'' as DWMC,'' as DWPYDM,d.JHHJ as JHHJ,d.PFJE as PFJE,d.LSJE as LSJE  FROM YK_TYPK a,YK_CDDZ b,YK_RK01 c,YK_RK02 d,YK_RKFS e,GY_XTPZ f ");
		sqlBuilder.append(" WHERE c.XTSB = d.XTSB and c.RKFS=d.RKFS and c.RKDH=d.RKDH  and c.XTSB=:al_yksb and c.XTSB = e.XTSB  and e.DYFS = f.DMSB AND f.DMLB = 15000 AND f.DMSB = 1 ");
//		sqlBuilder.append(" AND c.RKFS = e.RKFS AND e.GRPB=1 and c.RKPB=1  and ");
		sqlBuilder.append(" AND c.RKFS = e.RKFS AND  c.RKPB=1  and ");//与上一条少了e.GRPB=1 and
//		sqlBuilder.append(BSPHISUtil.toChar("c.RKRQ", format)).append(" >= :adt_begin  and ");
//		sqlBuilder.append(BSPHISUtil.toChar("c.RKRQ", format)).append(" < :adt_end  and a.YPXH=d.YPXH and b.YPCD=d.YPCD ");
		sqlBuilder.append("c.RKRQ　>= to_date(:adt_begin,'yyyy-mm-dd hh24:mi:ss') and ");
		sqlBuilder.append(" c.RKRQ < to_date(:adt_end,'yyyy-mm-dd hh24:mi:ss') and a.YPXH=d.YPXH and b.YPCD=d.YPCD ");
		sqlBuilder.append(" and c.JGID = :ls_jgid ");
		if(DRUG_RECORD.equals(queryType)){
			if(body.get("PYDM") != null && !"".equals(body.get("PYDM")+ "") && !"null".equals(body.get("PYDM")+ "")){
				sqlBuilder.append(" and a.PYDM like '").append(body.get("PYDM")).append("%' ");
			}
			//药品类型
			if(body.get("YPLX") != null && !"".equals(body.get("YPLX")+"") && !"null".equals(body.get("YPLX")+"") && !"0".equals((body.get("YPLX")+""))){
				sqlBuilder.append(" and a.TYPE = ").append(body.get("YPLX")+"");
			}
			sqlBuilder.append(" ORDER BY YPXH, YPCD ASC ");
		}else if(DRUG_DETAILS.equals(queryType)){
			String ll_ypxh = String.valueOf(body.get("YPXH"));
			String ll_ypcd = String.valueOf(body.get("YPCD"));
//			//药品类型
//			if(body.get("YPLX") != null && !"".equals(body.get("YPLX")+"") && !"null".equals(body.get("YPLX")+"") && !"0".equals((body.get("YPLX")+""))){
//				sqlBuilder.append(" and a.TYPE = ").append(body.get("YPLX")+"");
//			}
			sqlBuilder.append(" and d.YPXH = ").append(ll_ypxh).append(" and d.YPCD = ").append(ll_ypcd).append(" ORDER BY RKRQ ASC");
		}else if(SUPPLIER_RECORD.equals(queryType)){
			if(body.get("PYDM") != null && !"".equals(body.get("PYDM")+ "") && !"null".equals(body.get("PYDM")+ "")){
				sqlBuilder.append(" and a.PYDM like '").append(body.get("PYDM")).append("%' ");
			}
//			//药品类型
//			if(body.get("YPLX") != null && !"".equals(body.get("YPLX")+"") && !"null".equals(body.get("YPLX")+"") && !"0".equals((body.get("YPLX")+""))){
//				sqlBuilder.append(" and a.TYPE = ").append(body.get("YPLX")+"");
//			}
			sqlBuilder.append(" ORDER BY DWXH ASC ");
		}else if(SUPPLIER_DETAILS.equals(queryType)){
			String ll_dwxh = String.valueOf(body.get("DWXH"));
			sqlBuilder.append(" and c.DWXH = ").append(ll_dwxh);
//			//药品类型
//			if(body.get("YPLX") != null && !"".equals(body.get("YPLX")+"") && !"null".equals(body.get("YPLX")+"") && !"0".equals((body.get("YPLX")+""))){
//				sqlBuilder.append(" and a.TYPE = ").append(body.get("YPLX")+"");
//			}
		}
		return sqlBuilder.toString();
	}
	/**
	 * 药品记录流程处理
	 * @param body
	 * @param dataSourceList
	 * @param drugRecordList
	 */
	public void drugRecordHandle(Map<String, Object> body, List<Map<String, Object>> dataSourceList, List<Map<String, Object>> drugRecordList){
		if(dataSourceList != null){
			//药品序号、药品产地、入库数量
			long ll_ypxh = 0, ll_ypcd = 0,  ll_oldypxh = 0, ll_oldypcd = 0;
			//进货价格、批发价格、零售价格
			double ld_jhjg = 0, ld_pfjg = 0, ld_lsjg  = 0, ll_rksl = 0, ld_lsje = 0, ld_jhhj = 0 ;
			//临时变量   入库数量
			//临时变量   进货价格、批发价格、零售价格
			double  ld_pfjg_tmp = 0, ld_lsjg_tmp  = 0, ll_rksl_tmp = 0;
			Map<String, Object> dataSource = null;
			Map<String, Object> input = null;
			for(int i=0; i<dataSourceList.size(); i++){
				dataSource = dataSourceList.get(i);
				
				ll_ypxh = Long.parseLong(dataSource.get("YPXH") + "");
				ll_ypcd = Long.parseLong(dataSource.get("YPCD") + "");
				ll_rksl = Double.parseDouble(dataSource.get("RKSL") + "");
				ld_jhjg = Double.parseDouble(dataSource.get("JHJG") + "");
				ld_pfjg = Double.parseDouble(dataSource.get("PFJG") + "");
				ld_lsjg = Double.parseDouble(dataSource.get("LSJG") + "");
				ld_lsje = Double.parseDouble(dataSource.get("LSJE") + "");
				ld_jhhj = Double.parseDouble(dataSource.get("JHHJ") + "");
				if(ll_oldypxh != ll_ypxh || ll_oldypcd != ll_ypcd || i == 0){//新药品
					if(i != 0 ){//创建新药品前，将之前的药品保存到list中
						drugRecordList.add(input);
					}
					input = new HashMap<String, Object>();
					input.put("RKSL", ll_rksl);//入库数量
					input.put("JHJG", ld_jhjg);//进货价格
					input.put("PFJG", ld_pfjg);//批发价格
					input.put("LSJG",ld_lsjg);//零售价格
					input.put("YPXH", ll_ypxh);//药品序号
					input.put("YPCD", ll_ypcd);//药品产地
					input.put("YPMC", dataSource.get("YPMC"));//药品名称
					input.put("YPGG", dataSource.get("YPGG"));//药品规格
					input.put("YPDW", dataSource.get("YPDW"));//药品单位
					input.put("PYDM", dataSource.get("PYDM"));//拼音代码
					input.put("WBDM", dataSource.get("WBDM"));//五笔代码
					input.put("JXDM", dataSource.get("JXDM"));//角形代码
					input.put("CDMC", dataSource.get("CDMC"));//产地名称
					input.put("LSJE", ld_lsje);//零售合计
					input.put("JHHJ", ld_jhhj);//进货合计
					ll_oldypxh = ll_ypxh;
					ll_oldypcd = ll_ypcd;
				}else{
//					ld_jhjg_tmp = Double.parseDouble(input.get("JHJG") + "") + ld_jhjg;
					ld_pfjg_tmp = Double.parseDouble(input.get("LSJE") + "") + ld_lsje;
					ld_lsjg_tmp = Double.parseDouble(input.get("JHHJ") + "") + ld_jhhj;
					ll_rksl_tmp = Double.parseDouble(input.get("RKSL") + "") + ll_rksl;
					input.put("RKSL", ll_rksl_tmp);//入库数量
//					input.put("JHJG", ld_jhjg_tmp);//进货价格
//					input.put("PFJG", ld_pfjg_tmp);//批发价格
//					input.put("LSJG",ld_lsjg_tmp);//零售价格
					input.put("LSJE", ld_pfjg_tmp);//零售合计
					input.put("JHHJ", ld_lsjg_tmp);//进货合计
				}
			}
			if(input != null){//最后一个药品保存
				drugRecordList.add(input);
			}
		}
	}
	
	/**
	 * 供货商记录流程处理
	 * @param body
	 * @param dataSourceList
	 * @param supplierRecordList
	 * @param dao
	 */
	public void supplierRecordHandle(Map<String, Object> body, List<Map<String, Object>> dataSourceList, List<Map<String, Object>> supplierRecordList, BaseDAO dao)throws ModelDataOperationException{
		if(dataSourceList != null){
			try{
				//单位序号
				long ll_dwxh = 0, ll_olddwxh = 0;
				//进货价格、批发价格、零售价格
				double ld_jhjg = 0, ld_pfjg = 0, ld_lsjg  = 0, ll_rksl = 0;
				//临时变量   进货价格、批发价格、零售价格
				double ld_jhjg_tmp = 0, ld_pfjg_tmp = 0,  ld_lsje = 0, ld_jhhj = 0, ll_rksl_tmp = 0;
				Map<String, Object> dataSource = null, parameters = null, input = null;
				//查询单位名称和拼音代码sql
				String sql = "";
				List<Map<String, Object>> djfdwList = null;
				int x = 0;
				for(int i=0; i<dataSourceList.size(); i++){
					dataSource = dataSourceList.get(i);
					//单位序号为空时，ll_dwxh 为-1
					ll_dwxh = dataSource.get("DWXH") == null ? -1 : Long.parseLong(dataSource.get("DWXH") + "");
					ld_jhjg = Double.parseDouble(dataSource.get("JHJG") + "");
					ld_pfjg = Double.parseDouble(dataSource.get("PFJG") + "");
					ld_lsjg = Double.parseDouble(dataSource.get("LSJG") + "");
					ld_lsje = Double.parseDouble(dataSource.get("LSJE") + "");
					ld_jhhj = Double.parseDouble(dataSource.get("JHHJ") + "");
					ll_rksl = Double.parseDouble(dataSource.get("RKSL") + "");
					if(ll_dwxh != ll_olddwxh || i == 0 ){//新进货单位
						if(i != 0 ){//创建新药品前，将之前的药品保存到list中
							supplierRecordList.add(input);
						}
						input = new HashMap<String, Object>();
						if(ll_dwxh == -1){//单位序号为空
							
							input.put("DWMC", "<未知单位1>");
							input.put("PYDM", "");
							System.out.println((x+1)+"=========="+Double.parseDouble(dataSource.get("JHHJ") + ""));
						}else{
							sql = "SELECT DWMC as DWMC,PYDM as PYDM From YK_JHDW Where DWXH = :ll_dwxh";
							parameters = new HashMap<String, Object>();
							parameters.put("ll_dwxh", ll_dwxh);
							djfdwList = dao.doSqlQuery(sql, parameters);
							if(djfdwList != null && djfdwList.size() > 0){
								input.put("DWMC", djfdwList.get(0).get("DWMC"));
								input.put("PYDM", djfdwList.get(0).get("PYDM"));
							}else{
								input.put("DWMC", "<未知单位2>");
								input.put("PYDM", "");
							}
						}
						input.put("DWXH", ll_dwxh);//单位序号
						input.put("JHJG", ld_jhjg);//进货价格
						input.put("PFJG", ld_pfjg);//批发价格
						input.put("LSJG",ld_lsjg);//零售价格
						input.put("LSJE", ld_lsje);//零售合计
						input.put("JHHJ", ld_jhhj);//进货合计
						input.put("RKSL", ll_rksl);//入库数量
						ll_olddwxh = ll_dwxh;
					}else{
						ll_rksl_tmp = Double.parseDouble(input.get("RKSL") + "") + ll_rksl;
						input.put("RKSL", ll_rksl_tmp);//入库数量
						ld_jhjg_tmp = Double.parseDouble(input.get("LSJE") + "") + ld_lsje;
						ld_pfjg_tmp = Double.parseDouble(input.get("JHHJ") + "") + ld_jhhj;
//						ld_lsjg_tmp = Double.parseDouble(input.get("LSJG") + "") + ld_lsjg;
//						input.put("JHJG", ld_jhjg_tmp);//进货价格
//						input.put("PFJG", ld_pfjg_tmp);//批发价格
//						input.put("LSJG",ld_lsjg_tmp);//零售价格
						input.put("LSJE", ld_jhjg_tmp);//零售合计
						input.put("JHHJ", ld_pfjg_tmp);//进货合计
						
					}
					if(i==dataSourceList.size()- 1){
						supplierRecordList.add(input);
					}
				}
			}catch(Exception e){
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "查询供货商记录失败" + e.getMessage());
			}
		}
	}
	/**
	 * 药品明细处理流程
	 * @param body
	 * @param dataSourceList
	 * @param supplierRecordList
	 * @param dao
	 * @throws ModelDataOperationException
	 */
	public void durgDetailsHandle(Map<String, Object> body , List<Map<String, Object>> dataSourceList, List<Map<String, Object>> supplierRecordList, BaseDAO dao)throws ModelDataOperationException{
		if(dataSourceList != null){
			try{
				//单位序号
				long ll_dwxh = 0;
				Map<String, Object> parameters = null, dataSource = null;
				//查询单位名称和拼音代码sql
				String sql = "";
				List<Map<String, Object>> djfdwList = null;
				for(int i=0; i<dataSourceList.size(); i++){
					dataSource = dataSourceList.get(i);
					//单位序号为空时，ll_dwxh 为-1
					ll_dwxh = dataSource.get("DWXH") == null ? -1 : Long.parseLong(dataSource.get("DWXH") + "");
					if(ll_dwxh == -1){//单位序号为空
						dataSource.put("DWMC", "<未知单位3>");
						dataSource.put("PYDM", "");
					}else{
						sql = "SELECT DWMC as DWMC,PYDM as PYDM From YK_JHDW Where DWXH = :ll_dwxh";
						parameters = new HashMap<String, Object>();
						parameters.put("ll_dwxh", ll_dwxh);
						djfdwList = dao.doSqlQuery(sql, parameters);
						if(djfdwList != null && djfdwList.size() > 0){
							dataSource.put("DWMC", djfdwList.get(0).get("DWMC"));
							dataSource.put("PYDM", djfdwList.get(0).get("PYDM"));
						}else{
							dataSource.put("DWMC", "<未知单位4>");
							dataSource.put("PYDM", "");
						}
						
					}
				}
			}catch(Exception e){
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "查询供货商明细失败" + e.getMessage());
			}
		}
			
	}
}
