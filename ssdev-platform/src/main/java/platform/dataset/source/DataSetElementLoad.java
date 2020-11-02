package platform.dataset.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleLoadWithParentRelation;
import ctd.util.S;
import ctd.util.context.Context;

public class DataSetElementLoad extends SimpleLoadWithParentRelation {

	@Override
	public void doAfterExec(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		if((Integer)res.get(Service.RES_CODE) == 200){
			Map<String, Object> m =  (Map<String, Object>) res.get("body");
			if(m != null){
				if(StringUtils.isEmpty((String) m.get("CustomIdentify"))){
					m.put("CustomIdentify", m.get("bCustomIdentify"));
				}
				if(StringUtils.isEmpty((String) m.get("DName"))){
					m.put("DName", m.get("bDName"));
				}
				if(StringUtils.isEmpty((String) m.get("DComment"))){
					m.put("DComment", m.get("bDComment"));
				}
				Map<String, Object> dt = (Map<String, Object>) m.get("DataType");
				if(dt == null || dt.get("key") == null){
					m.put("DataType", m.get("bDataType"));
				}
				if(S.isEmpty((String) m.get("DataLength"))){
					m.put("DataLength", m.get("bDataLength"));
				}
				String dicName=S.obj2String(m.get("bCodeSystem"));
				if(!StringUtils.isEmpty(dicName)){
					String dataStandardId=S.obj2String(((Map)m.get("DataStandardId")).get("key"));
					if(!StringUtils.isEmpty(dataStandardId)){
						dicName="platform.dataset.dic.res."+dataStandardId+"."+dicName;
					}
					Dictionary dic = DictionaryController.instance().getDic(dicName);
					if (dic != null) {
						Map<String,String> dicItems=new HashMap<String, String>();
						List<DictionaryItem> items = dic.itemsList();
						for (DictionaryItem item : items) {
							dicItems.put(item.getKey(), item.getText());
						}
						m.put("dicItems", dicItems);
					}
				}
			}
		}
	}
	
}
