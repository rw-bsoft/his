package phis.application.ivc.source;

import java.math.BigDecimal;

/**
* @ClassName: PayContext
* @Description: TODO()
* @author zhoufeng
* @date 2013-5-9 下午01:49:37
* 
*/
public interface PayContext {
	
	/**
	* @Title: setPayStrategy
	* @Description: TODO(注入付款策略对象)
	* @param @param strategy
	* @param @return    设定文件
	* @return PayContext    返回类型
	* @throws
	*/
	PayContext setPayStrategy(PayStrategy strategy);
	
	
	
	/**
	* @Title: convertAmount
	* @Description: TODO(当前策略计算总金额)
	* @param @param amount
	* @param @return    设定文件
	* @return Double    返回类型
	* @throws
	*/
	BigDecimal convertAmount();
	
}
