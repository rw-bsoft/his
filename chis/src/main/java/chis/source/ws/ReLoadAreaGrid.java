/**
 * @(#)GetEmpiId.java Created on 2009-10-23 上午11:03:37
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ws;

import ctd.dictionary.DictionaryController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chis.source.util.SendDictionaryReloadSynMsg;

/**
 * 获取个人唯一号以及附属信息。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class ReLoadAreaGrid extends AbstractWsService {

	private static final Log logger = LogFactory.getLog(ReLoadAreaGrid.class);
	
	private static boolean IS_RELOADING = false ;

	/**
	 * 执行reload网格地址。
	 * 
	 */
	public String execute(String request) {
		if(IS_RELOADING){
			return "0";
		}
		Thread reloadThead =new  ReloadThead();
		reloadThead.start() ;
		return "1";
	}
	
	class ReloadThead extends Thread{
		public void run(){
			IS_RELOADING = true ;
			try {
				Thread.sleep(1000*60*30);
				DictionaryController.instance().reload("chis.dictionary.areaGrid");
				SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.areaGrid");
			} catch (Exception e) {
				logger.error("failed to reload areagrid!",e);
			}finally{
				IS_RELOADING = false ;
			}
		}
	}
}
