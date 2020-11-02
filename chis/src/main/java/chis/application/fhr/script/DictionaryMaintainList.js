$package("chis.application.fhr.script")

$import("chis.script.BizSimpleListView")

chis.application.fhr.script.DictionaryMaintainList = function(cfg) {
	this.schema = cfg.entryName;
	chis.application.fhr.script.DictionaryMaintainList.superclass.constructor
			.apply(this, [cfg]);
	this.removeServiceId = "chis.templateService";
	this.removeAction = "removeDicMaintain";
}
Ext.extend(chis.application.fhr.script.DictionaryMaintainList, chis.script.BizSimpleListView, {
			doCreateInfo : function() {
				var r = {};
				var m = this.getDicMaintainForm(r);
				m.op = "create";
				m.hasUsed = false;
				m.doCreate();
				var win = m.getWin();
				win.show();
			},
			getDicMaintainForm : function(r) {
				var m = this.midiModules["dicMaintainForm"];
				if (!m) {
					var cfg = this.loadModuleCfg(this.refModule);
					$import(cfg.script);
					cfg.mainApp=this.mainApp;
					m = eval("new " + cfg.script + "(cfg)");
					m.on("save", this.refresh, this);
					m.on("close", this.active, this);
					m.on("beforeSave", this.onBeforeSave, this);
					this.midiModules["dicMaintainForm"] = m;
				}
				m.initDataId = r.id;
				m.fieldId = this.fieldId;
				return m;
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var m = this.getDicMaintainForm(r);
				m.op = "update";
				m.hasUsed = this.hasUsed;
				var formData = this.castListDataToForm(r.data, this.schema);
				m.formData = formData;
				this.formData=formData;
				var win = m.getWin();
				win.show();
			},
			doRemove : function(item, e) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				} else {
					Ext.Msg.show({
								title : '确认删除记录[' + r.id + ']',
								msg : '删除操作将无法恢复，是否继续?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.processRemove();
									}
								},
								scope : this
							})
				}
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			onBeforeSave : function(entryName, op, saveRequest) {
				var keys="";
				if(this.formData){
					keys=this.formData.keys
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.templateService",
							serviceAction : "checkHasSameDic",
							body : saveRequest,
							method:"execute",
							schema : entryName,
							keys:keys
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.onBeforeSave);
					return;
				}
				return result.json.hasNoSameDic;
			},
			getRemoveRequest : function(r) {
				return {
					pkey : r.id,
					fieldId : this.fieldId
				};
			},
			initQueryFeild : function() {
				this.cndFldCombox.setValue();
				this.cndField.setValue();
			},
			createNormalField : function(it) {
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					width : 150,
					value : it.defaultValue
				}
				var field;
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield"
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						field = new Ext.form.NumberField(cfg)
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						field = new Ext.form.DateField(cfg)
						break;
					case 'datetime' :
					case 'timestamp' :
						cfg.emptyText = "请选择日期时间"
						if (it.xtype == "datefield") {
							field = new Ext.form.DateField(cfg)
						} else {
							cfg.xtype = 'datetimefield'
							cfg.format = 'Y-m-d H:i:s'
							field = new util.widgets.DateTimeField(cfg)
						}
						break;
					case 'string' :
						field = new Ext.form.TextField(cfg)
						break;
				}
				return field;
			},
			setListButton : function(flag) {
				var btns = this.grid.getTopToolbar().items;
				var n = btns.getCount()
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i);
					if (!flag) {
						btn.disable();
					} else {
						btn.enable();
					}
				}
			}

		});