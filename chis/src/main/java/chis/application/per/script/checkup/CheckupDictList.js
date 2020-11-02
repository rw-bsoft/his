$package("chis.application.per.script.checkup");

$import("chis.script.BizEditorListView");

chis.application.per.script.checkup.CheckupDictList = function(cfg){
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	chis.application.per.script.checkup.CheckupDictList.superclass.constructor.apply(this,[cfg]);
	this.pageSize=10;
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
};

Ext.extend(chis.application.per.script.checkup.CheckupDictList,chis.script.BizEditorListView,{
	initPanel:function(sc){
		var grid = app.modules.list.EditorListView.superclass.initPanel.call(this,sc);
		grid.on("afteredit",this.afterCellEdit,this);
		grid.on("beforeedit",this.beforeCellEdit,this);
//		grid.on("cellclick", this.setEdit, this);
		grid.on("celldblclick",this.toDelRow,this);
		return grid;
	},
	beforeCellEdit : function(e) {
		var f = e.field;
		var record = e.record;
		var op = record.get("_opStatus");
		var cm = this.grid.getColumnModel();

		var c = cm.config[e.column];
		var enditor = cm.getCellEditor(e.column);
		var it = c.schemaItem;
		var ac = util.Accredit;
		if (op == "create") {
			if (!ac.canCreate(it.acValue)) {
				return false;
			}
		} else {
			if (!ac.canUpdate(it.acValue)) {
				return false;
			}
		}
		if (it.dic) {
			e.value = {
				key : e.value,
				text : record.get(f + "_text")
			};
		} else {
			e.value = e.value || "";
		}
				
		if (this.fireEvent("beforeCellEdit", it, record, enditor.field,e.value, e)) {
			return true;
		}
	},
			
	afterCellEdit:function(e){
		var f = e.field;
		var v = e.value;
		var record = e.record;
		var cm = this.grid.getColumnModel();
		var enditor = cm.getCellEditor(e.column);
		var c = cm.config[e.column];
		var field = enditor.field;
		if(field.getRawValue){
			record.set(f + "_text",field.getRawValue());
		}	
	},
	getCM:function(items){
		var cm = [];
		var fm = Ext.form;
		var ac =  util.Accredit;
		if (this.showRowNumber) {
			cm.push(new Ext.grid.RowNumberer());
		}
		for(var i = 0; i <items.length; i ++){
			var it = items[i];
			if(it.noList || it.hidden || !ac.canRead(it.acValue)){
				continue;
			}			
			var width = parseInt(it.width || 80);
			var c = {
				header:it.alias,
				width:width,
				sortable:true,
				dataIndex: it.id,
				schemaItem:it
			};
			//** *********add by taoy*********** *//*
			if (it.xtype == "checkbox") {
				this.checkColumn.header = it.alias;
				this.checkColumn.dataIndex = it.dic ? it.id + "_text" : it.id;
				// this.checkColumn.sortable = true;
				this.checkColumn.width = width;
				this.checkColumn.fixed = it.fixed;
				cm.push(this.checkColumn);
				this.array.push(this.checkColumn);
				continue;
			}
			//** ******************** *//*
			if(it.renderer){
				var func;
				func = eval("this." + it.renderer);
				if (typeof func == 'function') {
					c.renderer = func;
				}
			}
					
			var editable = true;
					
			if((it.pkey && it.generator == 'auto')|| it.fixed){
				editable = false;
			}
			if(it.evalOnServer && ac.canRead(it.acValue)){
				editable = false;
			}
			var notNull = !(it["not-null"]=="1"||it['not-null'] == "true");
					
					
			var editor = null;
			var dic = it.dic;
			if(dic){
				dic.defaultValue = it.defaultValue;
				dic.width = width;
				if(dic.render == "Radio" || dic.render == "Checkbox"){
					dic.render = "";
				}
				if(editable){
					editor = this.createDicField(dic);
					editor.isDic = true;
				}
				var _ctx = this;
				c.isDic = true;		
				c.renderer = function(v, params, record,r,c,store){				
						var cm = _ctx.grid.getColumnModel();
						var f = cm.getDataIndex(c);
						return record.get(f + "_text");
					};
			}else{
				if(!editable){
					cm.push(c);
					continue;
				}
				editor = new fm.TextField({allowBlank: notNull});
				var fm = Ext.form;
				switch(it.type){
					case 'string','text':
						var cfg = {
									allowBlank:notNull,
									maxLength:it.length
							};
						if(it.inputType){
							cfg.inputType = it.inputType;
						}
						editor = new fm.TextField(cfg);
			           	break;
			        case 'date':
			           			var cfg = {
			           				allowBlank:notNull,
			           				emptyText:"请选择日期",
			           				format:'Y-m-d'
			           			};
								editor = new fm.DateField(cfg);
								break;
					case 'double':
					case 'bigDecimal':
					case 'int':
						if(!it.dic){
							c.css = "color:#00AA00;font-weight:bold;";
							c.align = "right";
						}					
						var cfg = {};
						if(it.type == 'int'){
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false;
						}else{
							cfg.decimalPrecision = it.precision || 2;
						}
						if(it.min){
							cfg.minValue = it.min;
						}
						if(it.max){
							cfg.maxValue = it.max;
						}
						cfg.allowBlank = notNull;
							editor = new fm.NumberField(cfg);
							break;
						}
					}
					c.editor = editor;
					cm.push(c);
				}
				return cm;
	},
	
	doSave : function() {
		this.fireEvent("save");
	},
	onBeforeCellEdit : function(it, record, field, value, e) {
		this.getDicItems();
		var referenceLower = record.data.referenceLower;
		var referenceUpper = record.data.referenceUpper;
		var cm = e.grid.getColumnModel();
					
		var isDic = false;
		var dicId = null;
		if (this.dicItems) {
			var rid = record.get("checkupProjectId");
			for (var i = 0; i < this.dicItems.length; i++) {
				if (rid == this.dicItems[i]["checkupProjectId"]) {
					dicId = this.dicItems[i]["checkupDic"];
					isDic = true;
					break;
				}
			}
		}
			
		// 对“提示”字段进行控制
		// if(isDic || referenceLower || referenceUpper){
		// cm.setEditable(4,false);
		// }
		
		// 动态生成“体检结果”字段
		if (e.column == 3) {
			if (isDic) {
				var dicMid = this.createSimpleDic(dicId, true);
				cm.setEditor(e.column, dicMid);
						
				e.value = {key:record.get("ifException"),text:record.get("ifException_text")};
				dicMid.on("select", function(scope, r, index) {
					var isNormal = r.get("isNormal");
					if (isNormal == '1') {
						record.set("checkupOutcome", "1");
						record.set("checkupOutcome_text", "正常");
					} else if (isNormal == '0') {
						record.set("checkupOutcome", "2");
						record.set("checkupOutcome_text", "异常");
					} else {
						record.set("checkupOutcome", "");
						record.set("checkupOutcome_text", "");
					}
					e.value = {key:r.get("key"),text:r.get("text")};
				}, this);
								
			} else if (referenceLower || referenceUpper) {
				var numberField = new Ext.form.NumberField({});
				cm.setEditor(e.column, numberField);
				numberField.on("blur", function(field) {
						var v = field.getValue();
						if (v == "") {
							record.set("checkupOutcome", "");
							record.set("checkupOutcome_text", "");
							return
						}
						record.set("checkupOutcome", "1");
						record.set("checkupOutcome_text", "正常");

						if (v < referenceLower || v > referenceUpper) {
							record.set("checkupOutcome", "2");
							record.set("checkupOutcome_text", "异常");
						}
				}, this);
			}else{
				var textField = new Ext.form.TextField({});
				cm.setEditor(e.column, textField);
			}
		}
	},
	
	changeDisplay:function(v,m,r){
		var t = r.get("ifException_text");
		if(t){
			return t;
		}
		return v;
	},

	showResoult:function(v,m,r){
		var t = r.get("checkupOutcome_text");
		if(t){
			return t;
		}
		return v;
	},
			
	createSimpleDic : function(dicId, isLoad) {
		var dic = util.dictionary.SimpleDicFactory.createDic({
			id : "chis.dictionary."+dicId,
			searchField : "isNormal"
		});
		if (isLoad) {
			dic.store.load();
		}
		return dic;
	},

	setEdit:function(g, row, col, e){
		if(col == 4){
	 		var cm = g.getColumnModel();
			cm.setEditable(col,true);			
		}
	},
			
	// 将体检字典项目汇总输出
	getDicItems : function() {
		if (!this.dicItems) {
			var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.checkupRecordService",
					serviceAction : "getCheckupDict",
					method:"execute",
					cnd : ['gt', ['$', 'length(checkupDic)'], ['i', '0']]
				});
			var resultBody = result.json.body;
			if (result.code == 200) {
				this.dicItems = result.json.body;
			} else {
				if (result.msg = "NotLogon") {
					this.mainApp.logon(this.loadModuleCfg, this, [id]);
				}
				return null;
			}
		}
	},

	getPagingToolbar : function(store) {
		return [{
				text : '备注: "提示"包含两个选项:正常(1),异常(2) 可输入代码(如1)回车调出对应选项!双击"体检项目名称"可将该体检项从表格中移除'
			}];
	},
	
	toDelRow : function(grid,rowIndex,columnIndex,e){
		if(columnIndex == 1){
			var delRecord = this.store.getAt(rowIndex);
			this.fireEvent("listDelRow",delRecord);
			this.store.removeAt(rowIndex);
		}
	}
	
	
});