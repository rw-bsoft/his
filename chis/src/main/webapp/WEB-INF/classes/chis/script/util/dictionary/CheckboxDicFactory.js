$package("chis.script.util.dictionary")
$import("util.dictionary.DictionaryLoader",
		"util.widgets.MyCheckboxGroup"
)

chis.script.util.dictionary.CheckboxDicFactory = {

	createDic:function(dic){
		var data={};
		if(dic.items){
			data=dic
		}else{
			data = util.dictionary.DictionaryLoader.load(dic)
		}
		var columns = []
		var width = dic.columnWidth || 100
		var cfg = {
			name:dic.id,
			value:dic.defaultValue,
			items:[]
		}
		if(data && data.items){
			var items = data.items
			var n = items.length
			for(var i = 0; i < n; i ++){
				var item = items[i]
				cfg.items.push({
					inputValue:item.key,
					boxLabel:item.text
				})
			}
			if(n > 4){
				n = dic.columns || 4;
			}
			for(var i = 0; i < n; i ++){
				columns.push(width)
			}				
		}
		if(dic.vertical){
			cfg.vertical=dic.vertical;
		}
		cfg.columns = columns;
		return new util.widgets.MyCheckboxGroup(cfg)
	}
}