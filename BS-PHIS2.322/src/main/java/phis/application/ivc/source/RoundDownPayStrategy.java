package phis.application.ivc.source;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
* @ClassName: RoundFloorPayStrategy
* @Description: TODO(舍去零头)
* @author zhoufeng
* @date 2013-5-9 下午02:54:53
* 
*/
public class RoundDownPayStrategy implements PayStrategy{

	/* (非 Javadoc)
	* <p>Title: calculate</p>
	* <p>Description: </p>
	* @param amout
	* @param scale
	* @return
	* @see com.bsoft.bshis.paystrategy.PayStrategy#calculate(java.math.BigDecimal, int)
	*/
	@Override
	public BigDecimal calculate(BigDecimal amout, int scale) {
		return amout.setScale(scale,RoundingMode.DOWN);
	}

}
