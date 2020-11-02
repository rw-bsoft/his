/**
 * @(#)AbstractDataController.java Created on 2012-2-7 下午03:30:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.control.controllor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.util.context.Context;
  
/**
 * @description 
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class AbstractDataController implements IDataController {

	private Method[] methods= null;
	/* (non-Javadoc)调用类中所有if开头的方法。
	 * @see chis.source.rct.IDataController#execute(java.util.Map, ctd.util.context.Context)
	 */
	@Override
	public Map<String, Boolean> execute(Map<String,Map<String,Object>>data,Context ctx) {
		Map<String,Boolean> res = new HashMap<String,Boolean>();
		try {
			if(methods==null){
				this.getMethodsStartWithIf();
			}
			
			for(Method m : methods){
				String name = m.getName();
				String keyName = new StringBuffer(name.substring(2, 3).toLowerCase())
				.append(name.substring(3)).toString();
				Object b = m.invoke(this,data, ctx);
				if(b instanceof Boolean){
					res.put(keyName, (Boolean)b);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res ;
	}
	
	/**
	 * 
	 */
	protected void getMethodsStartWithIf(){
		List<Method> myMethods = new ArrayList<Method>();
		Method[] tempMethods =this.getClass().getMethods();
		for(Method m : tempMethods){
			String name = m.getName();
			if(name.startsWith("if")){
				myMethods.add(m);
			}
		}
		methods = new Method[myMethods.size()];
		for(int i = 0;i <myMethods.size() ;i++){
			methods[i] =myMethods.get(i);
		}
	} 
	
	
	
	
}
