package platform.dataset.source.service;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import ctd.dictionary.Dictionary;
import ctd.util.annotation.RpcService;

public interface DataElementLoader {
	
	@RpcService
	public Document getDataSetXSD(String id);
	
	@RpcService
	public Document getDataSetXSD(String id, String version);
	
	@RpcService
	public Document getDataSample(String id);
	
	@RpcService
	public Document getDataSample(String id, String version);
	
	@RpcService
	public Object getDataWord(List<Map<String,Object>> dataSets);
	
	@RpcService
	public String getSetMapping(String id);
	
	@RpcService
	public String getSetMapping(String id, String version);
	
	@RpcService
	public Map<String,Object> getDataElement(String id);
	
	@RpcService
	public Map<String,Object> getDataElement(String id, String version);
		
	@RpcService
	public Map<String,Object> getDataSet(String id);
	
	@RpcService
	public Map<String,Object> getDataSet(String id, String version);
	
	@RpcService
	public Map<String,String> getSetRelation(String id);
	
	@RpcService
	public Map<String,String> getSetRelation(String id, String version);
	
	@RpcService
	public Map<String,String> getSetDics(String id);
	
	@RpcService
	public Map<String,String> getSetDics(String id, String version);

	@RpcService
	public Dictionary getDataDic(String id);

	@RpcService
	public Dictionary getDataDic(String id, String version);
}
