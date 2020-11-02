package phis.application.ivc.source;

import phis.application.ivc.source.RoundTypesEnum;
import phis.application.ivc.source.RoundHalfUpPayStrategy;
import phis.application.ivc.source.RoundHalfDownPayStrategy;
import phis.application.ivc.source.RoundDownPayStrategy;

public abstract class AbstractPayContext {
	/**
	* @Title: searchStrategy
	* @Description: TODO(取得对应的某个付款類型)
	* @param @param bo    设定文件
	* @return void    返回类型
	* @throws
	*/
	public PayStrategy searchStrategy(ClinicPayBO bo){
		/*for(RoundTypesEnum re:RoundTypesEnum.values()){
			if(bo.getPayType()==re.getRoundType())
		}*/
		if(bo.getPayType()==RoundTypesEnum.HALF_UP.getRoundType()){
			return new RoundHalfUpPayStrategy();
		}else if(bo.getPayType()==RoundTypesEnum.HALF_DOWN.getRoundType()){
			return new RoundHalfDownPayStrategy();
		}else if(bo.getPayType()==RoundTypesEnum.UP.getRoundType()){
			return new RoundUpPayStrategy();
		}else if(bo.getPayType()==RoundTypesEnum.DOWN.getRoundType()){
			return new RoundDownPayStrategy();
		}else{
			return new RoundHalfUpPayStrategy();
		}
	}
}
