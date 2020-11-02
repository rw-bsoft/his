package chis.source.gis;

import java.util.HashMap;

import ctd.dictionary.DictionaryController;
import ctd.dictionary.CodeRule;
import ctd.dictionary.Dictionary;

public class LayerDic {
	public static HashMap<String,Integer> layerMapping=new HashMap<String, Integer>();
	static{
		layerMapping.put("4", 6);
		layerMapping.put("6", 8);
		layerMapping.put("8", 10);
		layerMapping.put("10", 13);
		layerMapping.put("13", 16);
	}
	
	public static HashMap<String,String> layerDic=new HashMap<String, String>();
	static{
		layerDic.put("4", "substring(regionCode,1,4)");
		layerDic.put("6", "substring(regionCode,1,6)");
		layerDic.put("9", "substring(regionCode,1,9)");
		layerDic.put("12", "substring(regionCode,1,12)");
		layerDic.put("15", "substring(regionCode,1,15)");
		layerDic.put("18", "substring(regionCode,1,18)");
	}
	
	public static int getManaUnitNextLayerLength (int length) {
		Dictionary dic = DictionaryController.instance().getDic("manageUnit");
		CodeRule codeRule = new CodeRule((String)dic.getProperty("manageRule"));
		return codeRule.getNextLength(length);
	}
	
//	static{
//		manageUnitDic.put("4", "6");
//		manageUnitDic.put("6", "9");
//		manageUnitDic.put("9", "11");
//	}
}
