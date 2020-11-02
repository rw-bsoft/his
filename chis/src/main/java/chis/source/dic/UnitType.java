/*
 * @(#)BusinessType.java Created on 2012-3-9 下午4:23:32
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.dic;

/**
 * @description 机构类型的字典
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">yaozh</a>
 * 
 */
public abstract class UnitType {

	// 机构类型的字典
	public static final String SHENG = "a"; 
	public static final String SHI = "b"; 
	public static final String QU = "c"; 
	public static final String ZHONGXIN = "d";
	public static final String TUANDUI = "e";
	
	public static final int LEN_SHENG = 2; 
	public static final int LEN_SHI = 4; 
	public static final int LEN_QU = 6; 
	public static final int LEN_ZHONGXIN = 9;
	public static final int LEN_TUANDUI = 12;
	
	
	public static int getNextLevelLength(String manaUnitId){
		int len = manaUnitId.length() ;
		if(len<=LEN_SHI){
			return LEN_QU;
		}else if(len==LEN_QU){
			return LEN_ZHONGXIN ;
		}else if(len ==LEN_ZHONGXIN ){
			return LEN_TUANDUI;
		}else{ 
			return LEN_TUANDUI;
		}
	}
	
}
