$package("chis.application.conf.script.mdc")
$import("chis.script.BizEditorListView")
chis.application.conf.script.mdc.HypertensionAssessmentConfigManageList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = true;
	cfg.mutiSelect = true;
	cfg.enableCnd = false;
	chis.application.conf.script.mdc.HypertensionAssessmentConfigManageList.superclass.constructor.apply(
			this, [cfg]);
	this.requestData.cnd = this.cnds;
}
Ext.extend(chis.application.conf.script.mdc.HypertensionAssessmentConfigManageList,
		chis.script.BizEditorListView, {
	getCM:function(items){
		var cm = []
		var fm = Ext.form
		var ac =  util.Accredit;
		for(var i = 0; i <items.length; i ++){
			var it = items[i]
			if(it.noList || it.hidden || !ac.canRead(it.acValue)){
				continue
			}			
			if (!this.fireEvent("onGetCM", it)) { // **
				// fire一个事件，在此处可以进行其他判断，比如跳过某个字段
				continue;
			}
			var width = parseInt(it.width || it.length || 80)
			var c = {
				header:it.alias,
				width:width,
				sortable:true,
				dataIndex: it.id,
				schemaItem:it
			}
			if(it.renderer){
				c.renderer = it.renderer
			}
			var editable = true;
			
			if((it.pkey && it.generator == 'auto')|| it.fixed){
				editable = false
			}
			if(it.evalOnServer && ac.canRead(it.acValue)){
				editable = false
			}
			var notNull = !(it['not-null'] == 'true')
			
			
			var editor = null;
			var dic = it.dic
			if(dic){
				dic.src = this.entryName + "." + it.id
				dic.defaultValue = it.defaultValue
				dic.width = width
				if(dic.render == "Radio" || dic.render == "Checkbox"){
					dic.render = ""
				}
				c.renderer = function(v, params, record,r,c,store){
					var cm = _ctx.grid.getColumnModel()
					var f = cm.getDataIndex(c)
					return record.get(f + "_text")
				}
				if(editable){
					editor = this.createDicField(dic)
					editor.isDic = true
					var _ctx = this
					c.isDic = true				
				}
			}
			else{
				if(!editable){
					cm.push(c);
					continue;
				}
				editor = new fm.TextField({allowBlank: notNull});
				var fm = Ext.form;
				switch(it.type){
					case 'string':
					case 'text':
						var cfg = {
							allowBlank:notNull,
							maxLength:it.length
						}
						if(it.inputType){
							cfg.inputType = it.inputType
						}
						editor = new fm.TextField(cfg)
	           			break;
	           		case 'date':
	           			var cfg = {
	           				allowBlank:notNull,
	           				emptyText:"请选择日期",
	           				format:'Y-m-d'
	           			}
						editor = new fm.DateField(cfg)
						break;
					case 'double':
					case 'bigDecimal':
					case 'int':
						if(!it.dic){
							c.css = "color:#00AA00;font-weight:bold;"
							c.align = "right"
						}					
						var cfg = {}
						if(it.type == 'int'){
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						}
						else{
							cfg.decimalPrecision = it.precision || 2;
						}
						if(it.min){
							cfg.minValue = it.min;
						}
						if(it.max){
							cfg.maxValue = it.max;
						}
						cfg.allowBlank = notNull
						editor = new fm.NumberField(cfg)
						break;
				}
			}
			c.editor = editor;
			cm.push(c);
		}
		return cm;
	}
});