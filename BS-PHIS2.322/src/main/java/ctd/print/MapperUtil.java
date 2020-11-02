package ctd.print;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * @author wuk
 *	JsonMapper ������
 */
public class MapperUtil {

	private static ObjectMapper jsonMapper =  new ObjectMapper();
	static{
		jsonMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		jsonMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		jsonMapper.getSerializationConfig().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}
	public static ObjectMapper getJsonMapper(){
		return jsonMapper;
	}
}
