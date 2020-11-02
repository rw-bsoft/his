package phis.application.ivc.source;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
* @ClassName: RoundHalfDownPayStrategy
* @Description: TODO()
* @author zhoufeng
* @date 2013-5-9 下午02:42:28
* 
*/
public class RoundHalfDownPayStrategy implements PayStrategy{
	
	/* (非 Javadoc)
	* <p>Title: calculate</p>
	* <p>Description:五舍六入计算方法</p>
	* @param amout
	* @param scale
	* @return
	* @see com.bsoft.bshis.paystrategy.PayStrategy#calculate(java.math.BigDecimal, int)
	*/
	@Override
	public BigDecimal calculate(BigDecimal amout, int scale) {
		
		return amout.setScale(scale,RoundingMode.HALF_DOWN);
	}

}
