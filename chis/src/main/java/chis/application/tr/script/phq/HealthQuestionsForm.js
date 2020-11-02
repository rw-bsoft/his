$package("chis.application.tr.script.phq")

$import("chis.script.BizTableFormView")

chis.application.tr.script.phq.HealthQuestionsForm = function(cfg){
	chis.application.tr.script.phq.HealthQuestionsForm.superclass.constructor.apply(this,[cfg]);
	this.colCount = 1;
	this.labelWidth=500;
	this.heigth=450;
	//用于loadDataByLocal
	this.entryName="THQ"
}

Ext.extend(chis.application.tr.script.phq.HealthQuestionsForm,chis.script.BizTableFormView,{
	initPanel : function() {
//		if(this.form){
//			if(!this.isCombined){
//				this.addPanelToWin();
//			}			
//			return this.form;
//		}
		var formFields = {};
		if(this.exContext.args.masterplateId){
			var ret = util.rmi.miniJsonRequestSync({
						serviceId : "chis.tumourQuestionnaireService",
						serviceAction : "getQuestionnaireMasterplate",
						method:"execute",
						body : {"masterplateId":this.exContext.args.masterplateId,"gcId":this.exContext.args.gcId || ''}
					});
			if (ret.code > 300) {
				this.processReturnMsg(ret.code, ret.msg, this.exContext.args.masterplateId);
				return;
			}
			formFields = ret.json.body;
		}
		var items = formFields;
		var schema = {
			"items" : formFields
		}
		var ac =  util.Accredit;
		var defaultWidth = this.fldDefaultWidth || 200
		if (!this.fireEvent("changeDic", items)) {
			return
		}
		var colCount = this.colCount || 1;
		
		var table = {
			layout:'tableform',
			layoutConfig:{
				columns:colCount,
				tableAttrs:{
					border:0,
					cellpadding:'2',
					cellspacing:"2"
				}
			},
			items:[]
		}
		if(!this.autoFieldWidth){
			var forceViewWidth = (defaultWidth + (this.labelWidth || 80)) * colCount
			table.layoutConfig.forceWidth = forceViewWidth
		}
		var size = items.length
		for(var i = 0; i < size; i ++){
			var it = items[i]
			if((it.display == 0 || it.display == 1|| it.hidden == true)  || !ac.canRead(it.acValue)){
				continue;
			}
			var f = this.createField(it)
			f.index = i;
			f.anchor = it.anchor || "100%"
			delete f.width
			
			f.colspan = parseInt(it.colspan)
			f.rowspan = parseInt(it.rowspan)
			
			if(!this.fireEvent("addfield",f,it)){
				continue;
			}
			table.items.push(f)
		}
		
		var cfg = {
			buttonAlign:'center',
			labelAlign: this.labelAlign || "left",
			labelWidth:this.labelWidth || 80,
			frame: true,		
			shadow:false,
			border:false,
			collapsible: false,
			autoWidth:true,
			autoHeight:true,
			floating:false
		}
		if(this.isCombined){
			cfg.frame = true
			cfg.shadow = false
			cfg.width = this.width
			cfg.height = this.height
		}
		else{
			cfg.autoWidth = true
			cfg.autoHeight = true
		}
		this.changeCfg(cfg);
		//this.initBars(cfg);
		Ext.apply(table,cfg)
		this.form = new Ext.FormPanel(table)
		this.form.on("afterrender",this.onReady,this)

		this.schema = schema;
		this.setKeyReadOnly(true)
		if(!this.isCombined){
			this.addPanelToWin();
		}
		return this.form
	},
	createField:function(it){
		var ac =  util.Accredit;
		var defaultWidth = this.fldDefaultWidth || 200
		var cfg = {
			name:it.id,
			fieldId:it.fieldId,
			fieldLabel:it.alias,
			xtype:it.xtype || "textfield",
			vtype:it.vtype,
			width:defaultWidth,
			value:it.defaultValue,
			enableKeyEvents:it.enableKeyEvents,
			validationEvent : it.validationEvent
		}
		if(it.xtype == 'title'){
			var title = new Ext.form.Label({
				autoWidth:true,
				//tr中的style="border-bottom-style:solid;border-bottom-color:#000000;"
				html:'<table width="100%"><tr><td width="85%" align="center" style="font-size:20px;font-weight:bold;">'+it.alias+'</td><td width="15%" align="left" style="font-size:12px;font-weight:bold;">'+it.judgementResult+'</td></tr></table>'
			});
			return title;
		}else{
			cfg.labelStyle='border-bottom-style:dotted;border-bottom-color:#FF7F50';
		}
		cfg.listeners = {
			specialkey:this.onFieldSpecialkey,
			scope:this
		}
		if(it.onblur){
			var func = eval("this."+it.onblur)
			if(typeof func == 'function'){
				Ext.apply(cfg.listeners, {blur:func})
			}
		}
		if(it.inputType){
			cfg.inputType = it.inputType
		}
		if(it.editable){
		   cfg.editable = (it.editable == "true")?true:false
		}
		if(it['not-null'] == "1" || it['not-null'] == "true"){
			cfg.allowBlank = false
			cfg.invalidText  = "必填字段"
			cfg.regex = /(^\S+)/
			cfg.regexText = "前面不能有空格字符"
		}
		if(it.fixed){
			cfg.disabled = true
		}
		if(it.pkey && it.generator == 'auto'){
			cfg.disabled = true
		}
		if(it.evalOnServer && ac.canRead(it.acValue)){
			cfg.disabled = true
		}
		if(this.op == "create" && !ac.canCreate(it.acValue)){
			cfg.disabled = true
		}
		if(this.op == "update" && !ac.canUpdate(it.acValue)){
			cfg.disabled = true
		}
		if(it.dic){
			// add by lyl, check treecheck length
			if (it.dic.render == "TreeCheck") {
				if (it.length) {
					cfg.maxLength = it.length;
				}
			}
			//it.dic.src = this.entryName + "." + it.id
			it.dic.defaultValue = it.defaultValue
			it.dic.width = defaultWidth
			var combox = this.createDicField(it.dic)
			//alert(1)
			//this.changeFieldCfg(it, cfg);
			Ext.apply(combox,cfg)
			//combox.on("specialkey",this.onFieldSpecialkey,this)
			return combox;
		}
		if(it.length){
			cfg.maxLength = it.length;
		}
		if(it.maxValue){
			cfg.maxValue = it.maxValue;
		}
		if(typeof(it.minValue) != 'undefined'){
			cfg.minValue = it.minValue;
		}
		if(it.xtype){
			if(it.xtype == "htmleditor"){
				cfg.height = it.height || 200;
			}
			if(it.xtype == "textarea"){
			    cfg.height = it.height || 65
			}
			if(it.xtype == "datefield" && (it.type == "datetime" || it.type == "timestamp")){
				cfg.emptyText  = "请选择日期"	
				cfg.format = 'Y-m-d'
			}
			this.changeFieldCfg(it, cfg);
			return cfg;
		}
		switch(it.type){
			case 'int':
			case 'double':
			case 'bigDecimal':
				cfg.xtype = "numberfield"
				if(it.type == 'int'){
					cfg.decimalPrecision = 0;
					cfg.allowDecimals = false
				}
				else{
					cfg.decimalPrecision = it.precision || 2;
				}
				break;
			case 'date':
				cfg.xtype = 'datefield'
				cfg.emptyText  = "请选择日期"	
				cfg.format = 'Y-m-d'
				if(it.maxValue && typeof it.maxValue == 'string' && it.maxValue.length>10){
					cfg.maxValue = it.maxValue.substring(0,10);
				}
				if(it.minValue && typeof it.minValue == 'string' && it.minValue.length>10){
					cfg.minValue = it.minValue.substring(0,10);
				}
				break;
			case 'datetime':
				cfg.xtype = 'datetimefield'
				cfg.emptyText  = "请选择日期时间"
				cfg.format = 'Y-m-d H:i:s'
				break;
			case 'text':
				cfg.xtype = "htmleditor"
				cfg.enableSourceEdit = false
				cfg.enableLinks = false
				cfg.width = 300
				cfg.height = 180
				break;
		}
		this.changeFieldCfg(it, cfg);
		return cfg;
	},
	createDicField:function(dic){
		var cls = "";
		if(dic.items && dic.items.length > 0){
			cls = "chis.script.util.dictionary.";
		}else{
			cls = "util.dictionary.";
		}
		if(!dic.render){
			cls += "Simple";
		}
		else{
			cls += dic.render
		}
		cls += "DicFactory"
		
		$import(cls)
		var factory = eval("(" + cls + ")")
		var field = factory.createDic(dic)
		return field
	},
	getFormData:function(){
		if(!this.validate()){
			return
		}
		if(!this.schema){
			return
		}
		var ac =  util.Accredit;
		var values = {};
		var items = this.schema.items;
		Ext.apply(this.data,this.exContext);
		if(items){
			var form = this.form.getForm();
			var n = items.length
			for(var i = 0; i < n; i ++){
				var it = items[i]
				if(this.op == "create" && !ac.canCreate(it.acValue)){
					continue;
				}
				var v = this.data[it.id] // ** modify by yzh 2010-08-04
				if (v == undefined) {
					v = it.defaultValue
				}
				if (v != null && typeof v == "object") {
					v = v.key
				}
				var f = form.findField(it.id)
				if(f){
					v = f.getValue()
					// add by huangpf
					if (f.getXType() == "treeField") {
						var rawVal = f.getRawValue();
						if (rawVal == null || rawVal == "")
							v = "";
					}
					if (f.getXType() == "datefield" && v != null && v != "") {
						v = v.format('Y-m-d');
					}
					// end
				}
				if(v == null || v === ""){
					if(!(it.pkey) && (it["not-null"]=="1"||it['not-null']=="true") && !it.ref){
						Ext.Msg.alert("提示", it.alias + "不能为空");
						return;
					}
				}
				values[it.fieldId+"."+it.id] = v;
			}
		}
		return values;
	},
	onFieldSpecialkey:function(f,e){
		var key = e.getKey()
		if(key == e.ENTER){
			e.stopEvent();
			this.quickPickMCode(f);
			this.focusFieldAfter(f.index);
		}
	},
	doNew: function(){
		//alert('m................doNew...')
	},
	onReady:function(){
		this.fireEvent("onReady");
		chis.application.tr.script.phq.HealthQuestionsForm.superclass.onReady.call(this);
	},
	loadData:function(){
		alert('..........f...........loadData..')
	}
});