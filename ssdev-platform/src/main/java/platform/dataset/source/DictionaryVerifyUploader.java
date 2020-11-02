package platform.dataset.source;

import java.util.Map;

import org.dom4j.Document;

import ctd.util.annotation.RpcService;

public interface DictionaryVerifyUploader {

	/**
	 * 上传某个标准的字典
	 * @param id
	 * @param dataStandard
	 * @param doc
	 * @return
	 */
	@RpcService
	public Boolean uploadVerifyDictionary(String id,String dataStandard,Document doc);
	
//	/**
//	 * 检查某个标准下所有上传的字典的审核状态
//	 * @param dataStandard
//	 * @return	key为审核通过后从平台调用的字典id	value为审核状态
//	 */
//	@RpcService
//	public Map<String, Boolean> checkUploadStatus(String dataStandard);
	
//	/**
//	 * 检查某个标准下所有上传的字典的审核状态，当isSuccess为真时候至返回审核通过的，反之则审核拒绝的
//	 * @param dataStandard
//	 * @param isSuccess
//	 * @return key为审核通过后从平台调用的字典id	value为审核状态
//	 */
//	@RpcService
//	public Map<String, Boolean> checkUploadStatus(String dataStandard, Boolean isSuccess);
	
}
