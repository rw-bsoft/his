package chis.source.quality;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.validator.ValidateException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
public class QualityControlModel implements BSCHISEntryNames {

	BaseDAO dao = null;
	public QualityControlModel(BaseDAO dao) {
		this.dao = dao;
	}
	public Map<String, Object> getQualityModel(String ID)
			throws ModelDataOperationException {
		try {
			// 质控QUALITY_ZK进行查看，是否有数据
			Map<String, Object> backMap = new HashMap<String, Object>();
			Map<String, Object> sizeMap = new HashMap<String, Object>();
			Map<String, Object> param_ZK = new HashMap<String, Object>();
			param_ZK.put("ID", ID);
			String hql = new StringBuffer("select ID as ID ,XMLB as XMLB," +
					" XMZLB as XMZLB,ZFS as ZFS,XMMC as XMMC,XMBS as XMBS," +
					" BZMS as BZMS,KXXas KXX ")
					.append(" from ").append(QUALITY_ZK)
					.append(" where ID = :ID").toString();
			sizeMap = dao.doLoad(hql, param_ZK);
			if (!"".equals(sizeMap) && !"null".equals(sizeMap)
					&& sizeMap != null) {
				backMap.put("ZK_form", sizeMap);
				String listXmxh=backMap.get("XMXH")+"";
				if (!"".equals(listXmxh) && !"null".equals(listXmxh)
						&& listXmxh != null) {
					
				}else{
					backMap.put("ZK_list", false);
				}
			}else{
				backMap.put("ZK_form", false);
			}
			return backMap;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询质控数据失败！");
		}
	}
	public Map<String, Object> onSaveList(Map<String, Object> body)
			throws ModelDataOperationException {
			// 质控QUALITY_ZK_GXSD进行保存
		    Map<String, Object> genValues=null;
			Map<String, Object> values = (Map<String, Object>) body.get("values");
			try {
				  genValues = dao.doInsert(QUALITY_ZK_GXSD, values, true);
			} catch (ValidateException e) {
				e.printStackTrace();
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			return genValues;
	}
	public Map<String, Object> saveGxyListYf(Map<String, Object> body)
			throws ModelDataOperationException {
			// 质控QUALITY_ZK_GXSD进行保存
		    Map<String, Object> genValues=null;
			Map<String, Object> values = (Map<String, Object>) body.get("values");
			Map<String, Object> map_zq = new HashMap<String, Object>();
			Map<String, Object>map_sql= new HashMap<String, Object>();
			try {
			//	Date dt = new Date();// 当前日期 
				 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//设置日期格式
				 String date=df.format(new Date())+"";// new Date()为获取当前系统时间
				// String t2 = csrq.replace('-','/');
				// Date dt1= new Date(t1);
				// Date dt2= new Date(t2);
				// long i= (dt1.getTime() - dt2.getTime())/(1000*60*60*24);
				// long AGE=i/365;
			//	String ZQLB=year+month;
		       map_sql.put("ZQLB", date);
				map_sql.put("ZKJB", values.get("ZKJB"));
				map_sql.put("ZKLB", values.get("ZKLB"));
				map_sql.put("MANAUNITID", values.get("GXJG"));
				map_sql.put("XMLB", values.get("XMLB"));
				String aql_zq = new StringBuffer(
						"select NO as NO ")
						.append(" from ")
						.append(QUALITY_ZK_ZQ)
						.append(" where ZQLB = :ZQLB and ZKJB = :ZKJB and ZKLB = :ZKLB and XMLB = :XMLB and MANAUNITID = :MANAUNITID ")
						.toString();
				  map_zq = dao.doLoad(aql_zq, map_sql);
				  if(!"".equals(map_zq) && !"null".equals(map_zq) && map_zq!=null){
					  map_sql.put("NO", map_zq.get("NO"));
					  dao.doSave("update",QUALITY_ZK_ZQ, map_sql, true);
					  genValues=map_zq;
				  }else{
					  map_sql.put("SFWC", '1');
					  genValues = dao.doSave("create",QUALITY_ZK_ZQ, map_sql, true);
				  }
				 
			} catch (ValidateException e) {
				e.printStackTrace();
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			return genValues;
	}
	public Map<String, Object> saveGxyList3(Map<String, Object> body)
			throws ModelDataOperationException {
			// 质控QUALITY_ZK_SJ进行保存
		    Map<String, Object> genValues=null;
			Map<String, Object> values = (Map<String, Object>) body.get("values");
			Map<String, Object> map_zq = new HashMap<String, Object>();
			Map<String, Object>map_sql= new HashMap<String, Object>();
			Map<String, Object>map_ysj= new HashMap<String, Object>();
			String VISITID=values.get("visitId")+"";
			try {
				map_sql.put("visitId", values.get("visitId"));
				map_sql.put("prartNo", values.get("NO"));
				String aql_zq = new StringBuffer(
						"select CODERNO as  CODERNO")
						.append(" from ")
						.append(QUALITY_ZK_SJ)
						.append(" where visitId = :visitId and prartNo = :prartNo  ")
						.toString();
				  map_zq = dao.doLoad(aql_zq, map_sql);
				  if(!"".equals(map_zq) && !"null".equals(map_zq) && map_zq!=null){
					  map_ysj=dao.doLoad(MDC_HypertensionVisit,VISITID);
					  map_ysj.put("CODERNO", map_zq.get("CODERNO"));
					  map_ysj.put("prartNo", values.get("NO"));
					  genValues = dao.doSave("update",QUALITY_ZK_SJ, map_ysj, true);
					  genValues.put("NO", values.get("NO"));
				  }else{
					  map_ysj=dao.doLoad(MDC_HypertensionVisit,VISITID);
					  map_ysj.put("prartNo", values.get("NO"));
					  map_ysj.put("ZKNO", '1');
					  map_ysj.put("SFZK", '1');
					  map_ysj.put("SFPF", '1');
					  genValues = dao.doSave("create",QUALITY_ZK_SJ, map_ysj, true);
					  genValues.put("NO", values.get("NO"));
				  }
				 
			} catch (ValidateException e) {
				e.printStackTrace();
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			return genValues;
	}
	public Map<String, Object> createZk(Map<String, Object> body)
			throws ModelDataOperationException {
			// 质控数据查询
		    Map<String, Object> genValues=null;
			Map<String, Object> sizeMap = new HashMap<String, Object>();
			Map<String, Object> param_ZK = new HashMap<String, Object>();
			Map<String, Object> map_sj = new HashMap<String, Object>();
			String ZKNO=body.get("CODERNO")+"";
			param_ZK.put("ZKNO", ZKNO);
			String hql = new StringBuffer(
					"select CODERNO as  CODERNO")
					.append(" from ")
					.append(QUALITY_ZK_SJ)
					.append(" where ZKNO = :ZKNO  ")
					.toString();
			try {
				//业务定义  质控数据和原数据是一对一关系，如查出多条为赃数据
				sizeMap = dao.doLoad(hql, param_ZK);
				if (!"".equals(sizeMap) && !"null".equals(sizeMap) && sizeMap != null) {
					String CODERNO=sizeMap.get("CODERNO")+"";
					 map_sj=dao.doLoad(QUALITY_ZK_SJ,CODERNO);
					 genValues=map_sj;
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			return genValues;
	}
	public Map<String, Object> saveFormZk(Map<String, Object> body)
			throws ModelDataOperationException {
			// 质控数据查询
		    Map<String, Object> genValues=null;
		    Map<String, Object> map_back=null;
			Map<String, Object> sizeMap = new HashMap<String, Object>();
			Map<String, Object> param_ZK = new HashMap<String, Object>();
			Map<String, Object> map_sj = new HashMap<String, Object>();
			Map<String, Object> data = (Map<String, Object>) body.get("data");
			Map<String, Object> map_update = new HashMap<String, Object>();
			String ZKNO=data.get("CODERNO")+"";
			String prartNo=data.get("prartNo")+"";
			param_ZK.put("ZKNO", ZKNO);
			String hql = new StringBuffer(
					"select CODERNO as  CODERNO")
					.append(" from ")
					.append(QUALITY_ZK_SJ)
					.append(" where ZKNO = :ZKNO  ")
					.toString();
				//业务定义  质控数据和原数据是一对一关系，如查出多条为赃数据
			try {
				sizeMap = dao.doLoad(hql, param_ZK);
				if (!"".equals(sizeMap) && !"null".equals(sizeMap) && sizeMap != null) {
					String CODERNO_ID=sizeMap.get("CODERNO")+"";
					Map<String, Object> data_UP =data;
					data_UP.remove("CODERNO");
					data_UP.put("ZKNO", ZKNO);
					data_UP.put("prartNo", prartNo);
					data_UP.put("CODERNO", CODERNO_ID);
					map_sj = dao.doSave("update",QUALITY_ZK_SJ, data_UP, true);
					map_back=dao.doLoad(QUALITY_ZK_SJ,CODERNO_ID);
				}else{
					Map<String, Object> data_SJ =data;
					data_SJ.remove("CODERNO");
					data_SJ.put("ZKNO", ZKNO);
					data_SJ.put("prartNo", prartNo);
					map_sj = dao.doSave("create",QUALITY_ZK_SJ, data_SJ, true);
					map_back=dao.doLoad(QUALITY_ZK_SJ,map_sj.get("CODERNO")+"");
					
					String sql = new StringBuffer("update ")
					.append("QUALITY_ZK_SJ")
					.append(" set  SFZK=:SFZK")
					.append(" where CODERNO =:CODERNO").toString();
					map_update.put("CODERNO", ZKNO);
					map_update.put("SFZK", "2");
		            dao.doUpdate(sql, map_update);
				}
				genValues=map_back;
			} catch (PersistentDataOperationException e1) {
				e1.printStackTrace();
			}catch (ValidateException e) {
				e.printStackTrace();
			}
			return genValues;
	}
	
	public Map<String, Object> creatGxyDzList(Map<String, Object> body)
			throws ModelDataOperationException {
			// 质控数据查询
		    Map<String, Object> genValuesMap=  new HashMap<String, Object>();
			List<Map<String, Object>> sizeMap = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> backList = new ArrayList<Map<String, Object>>();
			Map<String, Object> param_ZK = new HashMap<String, Object>();
			Map<String, Object>back_sj=new HashMap<String, Object>();//返回元数据
			Map<String, Object> back_zk=new HashMap<String, Object>();//返回质控数据
			String CODERNO=body.get("codeNo")+"";
			String  XMLB="GXYSF";//糖尿病随访标识
			String hql = new StringBuffer(
					"select XMMC as  XMMC")
					.append(" from ")
					.append(QUALITY_ZK)
					.append(" where XMLB = :XMLB  ")
					.toString();
			param_ZK.put("XMLB", XMLB);
			try {
				sizeMap =   dao.doQuery(hql, param_ZK);
				
				if (!"".equals(sizeMap) && !"null".equals(sizeMap) && sizeMap != null && sizeMap.size()>0) {
				//	genValues.put("KEY", "true");
					//系统质控评分标准维护判断判断
					
					Map<String, Object> map_sj = new HashMap<String, Object>();
					Map<String, Object> map_dz = new HashMap<String, Object>();
					map_sj=dao.doLoad(QUALITY_ZK_SJ,CODERNO);
					if (!"".equals(map_sj) && !"null".equals(map_sj) && map_sj != null) {
						for(int i=0;i<sizeMap.size();i++){
							String value=sizeMap.get(i).get("XMMC")+"";
							String putValue=map_sj.get(value)+"";
							back_sj.put(value, putValue);
						}
					//	genValues.put("YSJ", back_sj);
					}
					/***
					 * 以下为对质控数据判断获取
					 * */
					Map<String, Object> sizeMap2 = new HashMap<String, Object>();
					Map<String, Object> param_ZK2 = new HashMap<String, Object>();
					param_ZK2.put("CODERNO", CODERNO);
					String hql2 = new StringBuffer(
							"select ZKNO as  ZKNO")
							.append(" from ")
							.append(QUALITY_ZK_SJ)
							.append(" where CODERNO = :CODERNO  ")
							.toString();
						//业务定义  质控数据和原数据是一对一关系，如查出多条为赃数据
						sizeMap2 = dao.doLoad(hql2, param_ZK2);
						if (!"".equals(sizeMap2) && !"null".equals(sizeMap2) && sizeMap2 != null) {//对质控数据判断
							String ZKNO=sizeMap2.get("ZKNO")+"";
							map_dz=dao.doLoad(QUALITY_ZK_SJ,ZKNO);
							if (!"".equals(map_dz) && !"null".equals(map_dz) && map_dz != null) {
								for(int i=0;i<sizeMap.size();i++){
									String value=sizeMap.get(i).get("XMMC")+"";
									String putValue=map_dz.get(value)+"";
									back_zk.put(value, putValue);
								}
							//	genValues.put("ZKSJ", back_zk);
							}
						}
					/***
					 * 对随访值和质控值拼接
					 * */
			  if (!"".equals(back_sj) && !"null".equals(back_sj) && back_sj != null && back_sj.size()>0) {//对质控数据判断
				 //   System.out.println("---------------back_sj>"+back_sj.toString());
					 for(int k=0;k<sizeMap.size();k++){
						   String keyStr=sizeMap.get(k).get("XMMC")+"";
						   Map<String, Object> backSj = new HashMap<String, Object>();
						   backSj.put("XMMC", keyStr+"");
						   backSj.put("SFZ", back_sj.get(keyStr)+"");
						   backSj.put("ID", k+1);
						   backList.add(k,  backSj);
					  }
			     }
			  if (!"".equals(back_zk) && !"null".equals(back_zk) && back_zk != null && back_zk.size()>0) {//对质控数据判断
				//  System.out.println("---------------back_zk>"+back_zk.toString());
				  for(int i=0;i<sizeMap.size();i++){
						   String keyStr=sizeMap.get(i).get("XMMC")+"";
						   Map<String, Object> backSj = new HashMap<String, Object>();
						   for(int j=0;j<backList.size();j++){
							   Map<String, Object> pdMap=new HashMap<String, Object>();
							   pdMap=backList.get(i);
							   String valuePd=pdMap.get("XMMC")+"";
							   if(keyStr.equals(valuePd)){
  						          backList.get(i).put("ZKZ", back_zk.get(valuePd));
  						          //对关系判断，加入得分
  						          String SFZ=pdMap.get("SFZ")+"";
  						          String ZKZ=pdMap.get("ZKZ")+"";
  						          String dfKey=PdValue(SFZ,ZKZ);//返回得分方法
  						          backList.get(i).put("DF", dfKey);
							   }
						   }
					  }
			     }else{
			    	 for(int i=0;i<sizeMap.size();i++){
			    		 for(int j=0;j<backList.size();j++){ 
						     backList.get(i).put("ZKZ", back_zk.get(""));
						   }
					  }
			     }
			  backList = SchemaUtil.setDictionaryMessageForList(backList, "chis.application.quality.schemas.QUALITY_ZK_CKZKBG_DZ");
			  genValuesMap.put("body", backList);
				}else{
					genValuesMap.put("KEY", "系统质控评分标准判断没有维护！");
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			 System.out.println("---------2777"+genValuesMap.toString());
			return genValuesMap;
	}
	/**
	 * 传入参数 String随访值，String自控制
	 * 返回参数 String 得分
	 * */
	public String PdValue(String K1,String K2){
		String backKey="";
		if(K1.equals(K2)){
			backKey=10+"";
		}else{
			backKey=0+"";
		}
		return backKey;
	}
	public Map<String, Object> creatGxyDzform(Map<String, Object> body)
			throws ModelDataOperationException {
		 Map<String, Object> ValuesMap=  new HashMap<String, Object>();
		 List<Map<String, Object>> sizeMap = new ArrayList<Map<String, Object>>();
		 Map<String, Object> param_ZK=  new HashMap<String, Object>();
		 Map<String, Object> backMap=  new HashMap<String, Object>();
		 String CODERNO=body.get("pkey")+"";
		 try {
			Map<String, Object> map_dz=  new HashMap<String, Object>();
			map_dz=dao.doLoad(QUALITY_ZK_SJ,CODERNO);
			if (!"".equals(map_dz) && !"null".equals(map_dz) && map_dz != null) {
				//检查MPI_DemographicInfo表中的姓名
				String empiId=map_dz.get("empiId")+"";
				if (!"".equals(empiId) && !"null".equals(empiId) &&empiId != null) {
					Map<String, Object> map_mpi=  new HashMap<String, Object>();
					map_mpi=dao.doLoad(MPI_DemographicInfo,empiId);
					if (!"".equals(map_mpi) && !"null".equals(map_mpi) &&map_mpi != null) {
						map_dz.put("personName", map_mpi.get("personName"));
						map_dz.put("sexCode", map_mpi.get("sexCode"));
						map_dz.put("birthday", map_mpi.get("birthday"));
					}
				}
				String hql = new StringBuffer(
						"select ZKNO as  ZKNO")
						.append(" from ")
						.append(QUALITY_ZK_SJ)
						.append(" where CODERNO = :CODERNO  ")
						.toString();
				param_ZK.put("CODERNO", CODERNO);
					//业务定义  质控数据和原数据是一对一关系，如查出多条为赃数据
				backMap = dao.doLoad(hql, param_ZK);
				Map<String, Object> map_dz2=  new HashMap<String, Object>();
				if (!"".equals(backMap) && !"null".equals(backMap) &&backMap != null) {
					String CODERNO2=backMap.get("ZKNO")+"";
					map_dz2=dao.doLoad(QUALITY_ZK_SJ,CODERNO2);
					map_dz.put("ZDF", map_dz2.get("ZDF"));
				}
			}
			ValuesMap.put("body", map_dz);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return ValuesMap;
	}
	public Map<String, Object> creatGxyZkPf(Map<String, Object> body)
			throws ModelDataOperationException {
		// 质控数据查询
	    Map<String, Object> genValuesMap=  new HashMap<String, Object>();
		List<Map<String, Object>> sizeMap = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> backList = new ArrayList<Map<String, Object>>();
		Map<String, Object> param_ZK = new HashMap<String, Object>();
		Map<String, Object>back_sj=new HashMap<String, Object>();//返回元数据
		Map<String, Object> back_zk=new HashMap<String, Object>();//返回质控数据
		Map<String, Object> values = (Map<String, Object>) body.get("values");
		String CODERNO=values.get("CODERNO")+"";
		String  XMLB="GXYSF";//糖尿病随访标识
		String CODERNO2="";
		String hql = new StringBuffer(
				"select XMMC as  XMMC")
				.append(" from ")
				.append(QUALITY_ZK)
				.append(" where XMLB = :XMLB  ")
				.toString();
		param_ZK.put("XMLB", XMLB);
		try {
			sizeMap =   dao.doQuery(hql, param_ZK);
			
			if (!"".equals(sizeMap) && !"null".equals(sizeMap) && sizeMap != null && sizeMap.size()>0) {
			//	genValues.put("KEY", "true");
				//系统质控评分标准维护判断判断
				
				Map<String, Object> map_sj = new HashMap<String, Object>();
				Map<String, Object> map_dz = new HashMap<String, Object>();
				map_sj=dao.doLoad(QUALITY_ZK_SJ,CODERNO);
				if (!"".equals(map_sj) && !"null".equals(map_sj) && map_sj != null) {
					for(int i=0;i<sizeMap.size();i++){
						String value=sizeMap.get(i).get("XMMC")+"";
						String putValue=map_sj.get(value)+"";
						back_sj.put(value, putValue);
					}
				//	genValues.put("YSJ", back_sj);
				}
				/***
				 * 以下为对质控数据判断获取
				 * */
				Map<String, Object> sizeMap2 = new HashMap<String, Object>();
				Map<String, Object> param_ZK2 = new HashMap<String, Object>();
				param_ZK2.put("CODERNO", CODERNO);
				String hql2 = new StringBuffer(
						"select CODERNO as  CODERNO")
						.append(" from ")
						.append(QUALITY_ZK_SJ)
						.append(" where ZKNO = :CODERNO  ")
						.toString();
					//业务定义  质控数据和原数据是一对一关系，如查出多条为赃数据
					sizeMap2 = dao.doLoad(hql2, param_ZK2);
					if (!"".equals(sizeMap2) && !"null".equals(sizeMap2) && sizeMap2 != null) {//对质控数据判断
						 CODERNO2=sizeMap2.get("CODERNO")+"";
						map_dz=dao.doLoad(QUALITY_ZK_SJ,CODERNO2);
						if (!"".equals(map_dz) && !"null".equals(map_dz) && map_dz != null) {
							for(int i=0;i<sizeMap.size();i++){
								String value=sizeMap.get(i).get("XMMC")+"";
								String putValue=map_dz.get(value)+"";
								back_zk.put(value, putValue);
							}
						//	genValues.put("ZKSJ", back_zk);
						}
					}
				/***
				 * 对随访值和质控值拼接
				 * */
		  if (!"".equals(back_sj) && !"null".equals(back_sj) && back_sj != null && back_sj.size()>0) {//对质控数据判断
			 //   System.out.println("---------------back_sj>"+back_sj.toString());
				 for(int k=0;k<sizeMap.size();k++){
					   String keyStr=sizeMap.get(k).get("XMMC")+"";
					   Map<String, Object> backSj = new HashMap<String, Object>();
					   backSj.put("XMMC", keyStr+"");
					   backSj.put("SFZ", back_sj.get(keyStr)+"");
					   backSj.put("ID", k+1);
					   backList.add(k,  backSj);
				  }
		     }
		  if (!"".equals(back_zk) && !"null".equals(back_zk) && back_zk != null && back_zk.size()>0) {//对质控数据判断
			//  System.out.println("---------------back_zk>"+back_zk.toString());
			  for(int i=0;i<sizeMap.size();i++){
					   String keyStr=sizeMap.get(i).get("XMMC")+"";
					   Map<String, Object> backSj = new HashMap<String, Object>();
					   for(int j=0;j<backList.size();j++){
						   Map<String, Object> pdMap=new HashMap<String, Object>();
						   pdMap=backList.get(i);
						   String valuePd=pdMap.get("XMMC")+"";
						   if(keyStr.equals(valuePd)){
						          backList.get(i).put("ZKZ", back_zk.get(valuePd));
						          //对关系判断，加入得分
						          String SFZ=pdMap.get("SFZ")+"";
						          String ZKZ=pdMap.get("ZKZ")+"";
						          String dfKey=PdValue(SFZ,ZKZ);//返回得分方法
						          backList.get(i).put("DF", dfKey);
						   }
					   }
				  }
		     }else{
		    	 for(int i=0;i<sizeMap.size();i++){
		    		 for(int j=0;j<backList.size();j++){ 
					     backList.get(i).put("ZKZ", back_zk.get(""));
					   }
				  }
		     }
			  backList = SchemaUtil.setDictionaryMessageForList(backList, "chis.application.quality.schemas.QUALITY_ZK_CKZKBG_DZ");
			  if (!"".equals(backList) && !"null".equals(backList) && backList != null && backList.size()>0) {//对质控数据判断
				  int Zdf=0;
				  for(int t=0;t<backList.size();t++){
					  String dzDf=backList.get(t).get("DF")+"";
					  if (!"".equals(dzDf) && !"null".equals(dzDf) && dzDf != null) {
						 int dfGt= Integer.valueOf(dzDf);
						 Zdf=Zdf+dfGt;
					  }
				  }
				  genValuesMap.put("Zdf", Zdf);
			  }
			  if (!"".equals(genValuesMap) && !"null".equals(genValuesMap) && genValuesMap != null ) {//对质控数据判断
				  String pdDf=genValuesMap.get("Zdf")+"";
				  if (!"".equals(pdDf) && !"null".equals(pdDf) && pdDf != null ) {//对质控数据判断
					  Map<String, Object>map_update= new HashMap<String, Object>();
					  Map<String, Object>map_backUp= new HashMap<String, Object>();
					  Map<String, Object>map_load= new HashMap<String, Object>();
					  Map<String, Object>CODERNO_ID= new HashMap<String, Object>();
					  map_load=dao.doLoad(QUALITY_ZK_SJ,CODERNO2);
					  String sqlString=" update ";
					  map_update.put("ZDF", pdDf);
					  map_update.put("SFPF", "2");
					  map_update.put("CODERNO", CODERNO);
					  String sql = new StringBuffer("update ")
								.append("QUALITY_ZK_SJ")
								.append(" set ZDF=:ZDF,SFPF=:SFPF")
								.append(" where CODERNO =:CODERNO").toString();
					  dao.doUpdate(sql, map_update);
					   genValuesMap.put("Zdf", pdDf);
				  }
			  }
			}else{
				genValuesMap.put("KEY", "系统质控评分标准判断没有维护！");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		 System.out.println("---------2777"+genValuesMap.toString());
		return genValuesMap;
    }
	public Map<String, Object> deleteGxyZkPf(Map<String, Object> body)
			throws ModelDataOperationException {
			// 质控数据查询
		    Map<String, Object> genValues=null;
			Map<String, Object> sizeMap = new HashMap<String, Object>();
			Map<String, Object> param_ZK = new HashMap<String, Object>();
			Map<String, Object> map_sj = new HashMap<String, Object>();
			Map<String, Object> values =new HashMap<String, Object>(); 
			                                  values=(Map<String, Object>) body.get("values");
			String CODERNO=values.get("CODERNO")+"";
			param_ZK.put("ZKNO", CODERNO);
			String hql = new StringBuffer(
					"select CODERNO as  CODERNO")
					.append(" from ")
					.append(QUALITY_ZK_SJ)
					.append(" where ZKNO = :ZKNO  ")
					.toString();
			try {
				dao.doRemove(CODERNO, QUALITY_ZK_SJ);
				//业务定义  质控数据和原数据是一对一关系，如查出多条为赃数据
				sizeMap = dao.doLoad(hql, param_ZK);
				if (!"".equals(sizeMap) && !"null".equals(sizeMap) && sizeMap != null) {
					 dao.doRemove(sizeMap.get("CODERNO")+"", QUALITY_ZK_SJ);
				}
			//	genValues.put("KEY", true);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			return genValues;
	}
	/**
	 * 
	 *  
	 *           xuzb
	 *         质控糖尿病月份list
	 */
	public Map<String, Object> saveTnbListYf(Map<String, Object> body)
			throws ModelDataOperationException {
			// 质控QUALITY_ZK_GXSD进行保存
		    Map<String, Object> genValues=null;
			Map<String, Object> values = (Map<String, Object>) body.get("values");
			Map<String, Object> map_zq = new HashMap<String, Object>();
			Map<String, Object>map_sql= new HashMap<String, Object>();
			try {
			//	Date dt = new Date();// 当前日期 
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//设置日期格式
				String date=df.format(new Date())+"";// new Date()为获取当前系统时间
		        map_sql.put("ZQLB", date);
				map_sql.put("ZKJB", values.get("ZKJB"));
				map_sql.put("ZKLB", values.get("ZKLB"));
				map_sql.put("MANAUNITID", values.get("GXJG"));
				map_sql.put("XMLB", values.get("XMLB"));
				String aql_zq = new StringBuffer(
						"select NO as NO ")
						.append(" from ")
						.append(QUALITY_ZK_ZQ)
						.append(" where ZQLB = :ZQLB and ZKJB = :ZKJB and XMLB = :XMLB and ZKLB = :ZKLB and MANAUNITID = :MANAUNITID ")
						.toString();
				  map_zq = dao.doLoad(aql_zq, map_sql);
				  if(!"".equals(map_zq) && !"null".equals(map_zq) && map_zq!=null){
					  map_sql.put("NO", map_zq.get("NO"));
					  dao.doSave("update",QUALITY_ZK_ZQ, map_sql, true);
					  genValues=map_zq;
				  }else{
					  map_sql.put("SFWC", '1');
					  genValues = dao.doSave("create",QUALITY_ZK_ZQ, map_sql, true);
				  }
				 
			} catch (ValidateException e) {
				e.printStackTrace();
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			return genValues;
	}
	/**
	 *           xuzb
	 *         质控糖尿病 list3
	 */
	public Map<String, Object> saveTnbList3(Map<String, Object> body)
			throws ModelDataOperationException {
			// 质控QUALITY_ZK_SJ进行保存
		    Map<String, Object> genValues=null;
			Map<String, Object> values = (Map<String, Object>) body.get("values");
			Map<String, Object> map_zq = new HashMap<String, Object>();
			Map<String, Object>map_sql= new HashMap<String, Object>();
			Map<String, Object>map_ysj= new HashMap<String, Object>();
			String VISITID=values.get("visitId")+"";
			try {
				map_sql.put("visitId", values.get("visitId"));
				map_sql.put("prartNo", values.get("NO"));
				String aql_zq = new StringBuffer(
						"select CODERNO as  CODERNO")
						.append(" from ")
						.append(QUALITY_ZK_TNB)
						.append(" where visitId = :visitId and prartNo = :prartNo  ")
						.toString();
				  map_zq = dao.doLoad(aql_zq, map_sql);
				  if(!"".equals(map_zq) && !"null".equals(map_zq) && map_zq!=null){
					  map_ysj=dao.doLoad(MDC_DiabetesVisit,VISITID);
					  map_ysj.put("CODERNO", map_zq.get("CODERNO"));
					  map_ysj.put("prartNo", values.get("NO"));
					  genValues = dao.doSave("update",QUALITY_ZK_SJ, map_ysj, true);
					  genValues.put("NO", values.get("NO"));
				  }else{
					  map_ysj=dao.doLoad(MDC_DiabetesVisit,VISITID);
					  map_ysj.put("prartNo", values.get("NO"));
					  map_ysj.put("ZKNO", '1');//质控NO 默认1
					  map_ysj.put("SFZK", '1');//是否质控 默认否 参数1
					  map_ysj.put("SFPF", '1');//是否评分 默认否 参数1
					  genValues = dao.doSave("create",QUALITY_ZK_TNB, map_ysj, true);
					  genValues.put("NO", values.get("NO"));
				  }
				 
			} catch (ValidateException e) {
				e.printStackTrace();
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			return genValues;
	}
}
