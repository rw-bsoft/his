package phis.application.ivc.source;

import java.math.BigDecimal;



/**
* @ClassName: PayStrategy
* @Description: TODO()
* @author zhoufeng
* @date 2013-5-9 下午01:47:35
* 
*/
public interface PayStrategy {
	
	BigDecimal calculate(BigDecimal amout,int scale);
}
