package chis.source.dr.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;

import ctd.net.rpc.Client;

public class DRMPIModel {
	private BaseDAO dao;
	private String provider;
	private String writerProvider;
	private static DRMPIModel model = null;
	  
	  private DRMPIModel()
	  {
	    model = this;
	  }
	  
	  public static DRMPIModel getInstance()
	  {
	    return model;
	  }
	  
	  @SuppressWarnings("unchecked")
	public Map<String, Object> getMPI(String mpiId)
	    throws Exception{
	    try{
	    	String hql = "select mpiId as mpiId,cardTypeCode as cardTypeCode,cardNo as cardNo,personName as personName,idCard as idCard,sexCode as sexCode," +
		    					"birthday as birthday,contactNo as contactNo from DR_DemographicInfo where mpiId=:mpiId";
	    	Map<String,Object> parameters = new HashMap<String, Object>();
	    	parameters.put("mpiId", mpiId);
	    	return dao.doLoad(hql, parameters);
	    }catch (Exception e){
	      throw new Exception("获取MPI失败!", e);
	    }
	  }
	  
	  @SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMPI(Map<String, Object> body)
	    throws Exception {
	    try{
	    	List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
	    	if(body.containsKey("cards")){
		    	List<Map<String, Object>> cardsList = (List<Map<String, Object>>)body.get("cards");
		    	if(cardsList.size()>0){
	    			Map<String,Object> parameters = new HashMap<String, Object>();
		    		for(Map<String, Object> card:cardsList){
		    			String hql = "select mpiId as mpiId,cardTypeCode as cardTypeCode,cardNo as cardNo,personName as personName,idCard as idCard,sexCode as sexCode," +
		    					"birthday as birthday,contactNo as contactNo from DR_DemographicInfo where idCard=:idCard";
		    			parameters.clear();
		    			parameters.put("idCard", card.get("idCard")+"");
		    			Map<String,Object> demographicInfo = dao.doLoad(hql, parameters);
		    			dataList.add(demographicInfo);
		    		}
		    	}
		    }
	    	return dataList;
	    }catch (Exception e){
	      throw new Exception("获取MPI失败!", e);
	    }
	  }
	  
	  @SuppressWarnings("unchecked")
	public Map<String, Object> submitMPI(Map<String, Object> data)
	    throws Exception {
	    try{
	    	data.put("mpiId", data.get("idCard")+"");
	    	return dao.doSave("create", "DR_DemographicInfo", data, true);
	    }catch (Exception e){
	      throw new Exception("MPI!", e);
	    }
	  }
	  
	  public void setProvider(String provider)
	  {
	    this.provider = provider;
	  }
	  
	  public void setWriterProvider(String writerProvider)
	  {
	    this.writerProvider = writerProvider;
	  }
}
