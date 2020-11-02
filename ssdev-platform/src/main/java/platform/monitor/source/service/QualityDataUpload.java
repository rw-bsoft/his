package platform.monitor.source.service;

import java.util.List;
import java.util.Map;

import ctd.util.annotation.RpcService;

public interface QualityDataUpload {
	
	@RpcService
	public Boolean uploadData(List<Map<String,Object>> list);
	
	@RpcService
	public Boolean uploadUnFinishData(List<Map<String,Object>> list);
	
	
}
