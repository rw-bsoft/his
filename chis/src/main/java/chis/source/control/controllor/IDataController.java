/**
 * @(#)IDataController.java Created on 2012-2-7 下午03:24:06
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.control.controllor;


import java.util.Map;

import ctd.util.context.Context;

/**
 * @description execute中执行类中所有以if开头的方法，搜集返回值放入Map中返回
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public interface IDataController {
	public Map<String,Boolean> execute(Map<String,Map<String,Object>>data,Context ctx);
}
