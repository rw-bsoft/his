/**
 * @(#)HypertensionRecordRule.java Created on 2012-2-7 下午03:22:53
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.control.controllor;

import java.util.Map;

import ctd.util.context.Context;

/**
 * @description 
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class HypertensionRecordController extends AbstractDataController {
	public Boolean ifCreate(Map<String, Map<String, Object>> data,
			Context ctx){
		return false;
	}
	
	public Boolean ifWriteOff(Map<String, Map<String, Object>> data,
			Context ctx){
		return true;
	}
	
	public static void main(String args[]){
		for(int i = 0 ;i<1000;i++){
			HypertensionRecordController c = new HypertensionRecordController();
			long s = System.currentTimeMillis();
			System.out.println(c.execute(null, null));
			System.out.println(System.currentTimeMillis()-s);
		}
	}
}
