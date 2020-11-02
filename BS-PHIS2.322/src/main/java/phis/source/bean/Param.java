package phis.source.bean;

import java.util.HashMap;
import java.util.Map;
/**
 * 快速创建map并放入数据
 * @author wangjianbo
 *
 */
public class Param extends HashMap<String, Object> implements Map<String, Object>{
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		if(m!=null){
			super.putAll(m);
		}
	}

	private static final long serialVersionUID = 1L;
	private Param(){}
	public static final Param instance(){
		return new Param();
	}
	@SuppressWarnings("unchecked")
	public static final Param instance(Object obj) throws Exception{
		Param p=instance();
		if(obj instanceof Map){
			p.putAll((Map<String, Object>)obj);
		}
		return p;
	}
	
	public Param put(String key,Object value){
		super.put(key, value);
		return this;
	}
}
