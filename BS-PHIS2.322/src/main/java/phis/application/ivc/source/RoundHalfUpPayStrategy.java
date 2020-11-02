package phis.application.ivc.source;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundHalfUpPayStrategy implements PayStrategy {

	/* (非 Javadoc)
	* <p>Title: calculate</p>
	* <p>Description: 四舍五入</p>
	* @param amout
	* @param scale
	* @return
	* @see com.bsoft.bshis.paystrategy.PayStrategy#calculate(java.math.BigDecimal, int)
	*/
	@Override
	public BigDecimal calculate(BigDecimal amout,int scale) {
		
		return amout.setScale(scale,RoundingMode.HALF_UP);
	}

}
