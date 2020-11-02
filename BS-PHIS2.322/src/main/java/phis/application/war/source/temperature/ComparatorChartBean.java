package phis.application.war.source.temperature;

import java.util.Comparator;

/**
* @ClassName: ComparatorChartBean
* @Description: TODO(chartBean������)
* @author zhoufeng
* @date 2013-7-4 ����03:29:04
* 
*/
public class ComparatorChartBean implements Comparator<ChartBean>{

	@Override
	public int compare(ChartBean o1, ChartBean o2) {
		if(o1.getzIndex()==null||o2.getzIndex()==null)return o1.getzIndex()==null?1:-1;
		return -(o1.getzIndex().compareTo(o2.getzIndex()));
	}
	
}
