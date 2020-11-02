/**
 * @(#)ConfigControllorFactory.java 创建于 2011-1-7 下午08:18:03
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.conf.control;

import java.util.HashMap;
import java.util.Map;

/**
 * @description 配置权限控制器生成工厂。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class ConfigControllorFactory {

	private Map<String, ConfigControllor> map = new HashMap<String, ConfigControllor>();

	private static final ConfigControllorFactory factory = new ConfigControllorFactory();

	private ConfigControllorFactory() {

	}

	public static ConfigControllorFactory getInstance() {
		return factory;
	}

	/**
	 * 获得一个配置权限控制器。
	 * 
	 * @param cls
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public ConfigControllor getConfigControllor(String cls)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		if (!map.containsKey(cls)) {
			ConfigControllor controllor = (ConfigControllor) Class.forName(cls)
					.newInstance();
			map.put(cls, controllor);
		}
		return map.get(cls);
	}
}
