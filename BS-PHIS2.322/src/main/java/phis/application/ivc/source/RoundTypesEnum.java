package phis.application.ivc.source;

import java.math.BigDecimal;

/**
* @ClassName: RoundTypes
* @Description: TODO(几种精度的枚举)
* 舍入方式 | 系统配置舍入方式(2701).1.四舍五入 2.五舍六入 3.舍去零头 4.补整收取
* @author zhoufeng
* @date 2013-5-9 下午04:04:51
* 
*/
public enum RoundTypesEnum {
	HALF_UP(BigDecimal.ROUND_HALF_UP,1),HALF_DOWN(BigDecimal.ROUND_HALF_DOWN,2),DOWN(BigDecimal.ROUND_DOWN,3),UP(BigDecimal.ROUND_UP,4);
	private int scale;
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public int getRoundType() {
		return roundType;
	}
	public void setRoundType(int roundType) {
		this.roundType = roundType;
	}
	private int roundType;
	private RoundTypesEnum(int scale,int roundType){
		this.scale=scale;
		this.roundType=roundType;
	}
}
