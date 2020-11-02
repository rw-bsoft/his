package phis.application.war.source.temperature;

import java.math.BigDecimal;


/**
* @ClassName: TwTypesEnum
* @Description: TODO(体温类型)
* @author zhoufeng
* @date 2013-6-4 下午05:52:54
* 
*/
public enum TwTypesEnum {
	KW(1,"口温"),YW(2,"腋温"),GW(3,"肛温");
	private int type;
	private String name;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private TwTypesEnum(int type,String name){
		this.type=type;
		this.name=name;
	}
}
