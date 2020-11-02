package phis.application.war.source.temperature.drawshape.factory;

import java.util.HashMap;
import java.util.Map;

/**
* @ClassName: CharFactories
* @Description: TODO(线和圆的工厂类)
* @author zhoufeng
* @date 2013-7-8 下午04:43:09
* 
*/
public class CharFactories {
	
	private static final Map<Class<?>,Class<?>> registry=new HashMap<Class<?>, Class<?>>();
	
	static{
		config(TemperatureRound.class, FactoryRound.class);
		config(TemperatureHollow.class, FactoryHollow.class);
		config(TemperatureCross.class, FactoryCross.class);
		config(TemperatureTWSolidLine.class, FactoryLine.class);
		config(TemperatureMBSolidLine.class, FactoryMBLine.class);
		config(TemperatureDXSolidLine.class, FactoryDXLine.class);
	}
	private CharFactories(){
		
	}
	
	/**
	* @Title: config
	* @Description: TODO(注册产品类型)
	* @param @param productType
	* @param @param factoryType    设定文件
	* @return void    返回类型
	* @throws
	*/
	public static void config(Class<?> productType,Class<?> factoryType){
		registry.put(productType, factoryType);
	}
	
	/**
	* @Title: newFactory
	* @Description: TODO(获得产品)
	* @param @param <T>
	* @param @param productType
	* @param @return
	* @param @throws ClassNotFoundException
	* @param @throws InstantiationException
	* @param @throws IllegalAccessException    设定文件
	* @return ChartFactory<T>    返回类型
	* @throws
	*/
	@SuppressWarnings("unchecked")
	public static <T> ChartFactory<T> newFactory(Class<?> productType) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		if(!registry.containsKey(productType)){
			throw new ClassNotFoundException();
		}
		return (ChartFactory<T>) registry.get(productType).newInstance();
	}
}
