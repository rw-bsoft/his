package phis.application.ivc.source;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public final class JacksonUtil {
	private static volatile ObjectMapper mapper; 
	
	/**
	* @Title: getMapperInstance
	* @Description: TODO(创建单例的ObjectMapper)
	* @param @param createNew
	* @param @return    设定文件
	* @return ObjectMapper    返回类型
	* @throws
	*/
	public static ObjectMapper getMapperInstance() {
		if (mapper==null){
			synchronized (ObjectMapper.class) {
				if (mapper==null){
					mapper = new ObjectMapper();
				}
			}
		}
		return mapper;
	}	
	
	public static Object jsonToBean(String json, Class<?> cls) throws JsonParseException, JsonMappingException, IOException{
		Object res = getMapperInstance().readValue(json, cls);  
		return res;
	}
	public static String beanToJson(Map<String, Object> map) throws JsonParseException, JsonMappingException, IOException{
		return getMapperInstance().writeValueAsString(map);
	}
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("name", "张三");
		JacksonUtil.beanToJson(map);
	}
}
