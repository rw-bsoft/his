$package("chis.application.dbs.script.fixgroup")
$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.dbs.script.fixgroup.DiabetesFixGroupForm = function(cfg) {
	cfg.showButtonOnTop = true;
	this.empiId = cfg.empiId
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesFixGroup"
	chis.application.dbs.script.fixgroup.DiabetesFixGroupForm.superclass.constructor
			.apply(this, [cfg])
	this.on("aboutToSave", this.onAboutToSave, this);
	this.serviceId="chis.diabetesRecordService";
	var status= this.exContext.ids["MDC_DiabetesRecord.phrId.status"];
	if(status=='1'){
   		Ext.Msg.alert("友情提醒：","糖尿病档案已注销，请先恢复糖尿病档案！");
   		return;
    }
}
Ext.extend(chis.application.dbs.script.fixgroup.DiabetesFixGroupForm,
		chis.script.BizTableFormView, {
			// loadData : function() {
			// this.doNew()
			// if (!this.schema) {
			// return
			// }
			//
			// if (!this.fireEvent("beforeLoadData", this.entryName,
			// this.initDataId)) {
			// return
			// }
			// if (this.form && this.form.el) {
			// this.form.el.mask("正在载入数据...", "x-mask-loading")
			// }
			// this.loading = true
			// util.rmi.jsonRequest({
			// serviceId : this.loadServiceId,
			// schema : this.entryName,
			// pkey : this.initDataId
			// }, function(code, msg, json) {
			// if (this.form && this.form.el) {
			// this.form.el.unmask()
			// }
			// this.loading = false
			// if (code > 300) {
			// this.processReturnMsg(code, msg, this.loadData)
			// return
			// }
			// if (json.body) {
			// this.initFormData(json.body)
			// this.fireEvent("loadData", this.entryName, json.body);
			// }
			// if (this.op == 'create') {
			// this.op = "update"
			// }
			//
			// }, this)// jsonRequest
			// },
			// saveToServer : function(saveData) {
			// if (!this.fireEvent("beforeSave", this.entryName, this.op,
			// saveData)) {
			// return;
			// }
			// if (this.initDataId == null) {
			// this.op = "create";
			// }
			// saveData.empiId = this.empiId
			// this.saving = true
			// this.form.el.mask("正在保存数据...", "x-mask-loading")
			// util.rmi.jsonRequest({
			// serviceId : "chis.diabetesService",
			// op : this.op,
			// schema : this.entryName,
			// serviceAction : "saveDiabetesFixGroup",
			// body : saveData
			// }, function(code, msg, json) {
			// this.form.el.unmask()
			// this.saving = false
			// if (code > 300) {
			// this.processReturnMsg(code, msg, this.saveToServer,
			// [saveData]);
			// return
			// }
			// Ext.apply(this.data, saveData);
			// if (json.body) {
			// this.initFormData(json.body)
			// this.fireEvent("save", this.entryName, this.op, json,
			// this.data)
			// }
			// this.op = "update"
			// }, this)// jsonRequest
			// },
			setBg : function(fbs, pbs) {
				var form = this.form.getForm()
				var _fbs = form.findField("fbs")
				if (fbs) {
					_fbs.setValue(fbs)
				}
				var _pbs = form.findField("pbs")
				if (pbs) {
					_pbs.setValue(pbs)
				}
			},
			setButtonsEnable : function() {
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
					var btns = this.form.getTopToolbar().items;
					var n = btns.getCount()
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i)
						btn.enable()
					}
				} else {
					var btns = this.form.buttons
					for (var i = 0; i < btns.length; i++) {
						var btn = btns[i]
						btn.enable()
					}
				}
			},
			setButtonsDisable : function() {
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
					var btns = this.form.getTopToolbar().items;
					var n = btns.getCount()
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i)
						btn.disable()
					}
				} else {
					var btns = this.form.buttons
					for (var i = 0; i < btns.length; i++) {
						var btn = btns[i]
						btn.disable()
					}
				}
			},
			initFormData : function(data) {
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v != undefined) {
							if (it.id == "complication") {
								f.clearValue();
								f.setValue(v);
							} else {
								if ((it.type == 'date' || it.xtype == 'datefield')
										&& typeof v == 'string'
										&& v.length > 10) {
									v = v.substring(0, 10)
								} else if (it.type == 'datetime') {
									v = v.substring(0, 16)
								}

								f.setValue(v)
							}
						}

						if (this.initDataId) {
							if (it.update == false || it.update == "false") {
								f.disable();
							}
						}
					}
				}
				this.setKeyReadOnly(true)
				this.focusFieldAfter(-1, 800)
			},
			doNew : function() {
				var status= this.exContext.ids["MDC_DiabetesRecord.phrId.status"];
	            if(status=='1'){
   		           Ext.Msg.alert("友情提醒：","糖尿病档案已注销，请先恢复糖尿病档案！");
   		           return;
                }
				chis.application.dbs.script.fixgroup.DiabetesFixGroupForm.superclass.doNew
						.call(this)
				// var result = util.rmi.miniJsonRequestSync({
				// serviceId : "chis.simpleQuery",
				// schema : "MDC_DiabetesRecord",
				// cnd : ['eq', ['$', "a.empiId"], ['s', this.empiId]]
				// })
				//				
				// if (result.json.body[0]) {
				// this.data.phrId = result.json.body[0].phrId
				// var fbs = result.json.body[0].fbs
				// var pbs = result.json.body[0].pbs
				// this.setBg(fbs, pbs)
				// this.op == 'create'
				// }
			},
			// resetButtons : function() {
			// var btns = this.form.getTopToolbar().items;
			// if (!btns) {
			// return;
			// }
			// var n = btns.getCount()
			// for (var i = 0; i < n; i++) {
			// var btn = btns.item(i)
			// if (this.readOnly) {
			// if (btn.notReadOnly) {
			// btn.enable();
			// } else {
			// btn.disable();
			// }
			// }
			// }
			// },
			doFixGroup : function() {
				this.doNew()
				if (this.form.getTopToolbar()) {
					this.form.getTopToolbar().items.item(0).enable()
				}
				this.form.getForm().findField("fixType").setValue({
							key : "2",
							text : "维持原组不变"
						})
				this.fireEvent("fixGroup")
			},
			doSave : function() {
				var status= this.exContext.ids["MDC_DiabetesRecord.phrId.status"];
	            if(status=='1'){
   		          Ext.Msg.alert("友情提醒：","糖尿病档案已注销，请先恢复糖尿病档案！");
   		         return;
                }
				chis.application.dbs.script.fixgroup.DiabetesFixGroupForm.superclass.doSave
						.call(this);
			},
			saveToServer : function(saveData) {
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
					this.save(saveData);
			},
			onAboutToSave : function(entryName, op, saveData) {

			},
			save : function(saveData) {
				this.fireEvent("aboutToSave", this.entryName, this.op,
								saveData);
				this.form.el.mask("正在保存数据...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : 'saveDiabetesFixGroup',
							method : "execute",
							schema : this.entryName,
							body : saveData,
							op : this.op
						}, function(code, msg, json) {
							this.form.el.unmask();
							this.saving = false;
							if (code > 300) {
								this.processReturnMsg(code, msg, this.save,
										[saveData]);
								return;
							}
							//this.grid.loadData();
							this.op = "update";
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
//							this.fireEvent("afterCreate", this.entryName,
//									this.op, json, this.data);
//							this.fireEvent("refreshData", 'all');
						}, this);// jsonRequest
			},
			doLook : function(){
				Ext.Msg.alert("友情提醒：","先分组评估再做随访，如先做了一条随访请把分组日期推算到下次随访时间，不然计划不能正常生成！");
			}
		});