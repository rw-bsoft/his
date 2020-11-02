$package("phis.application.war.script");

$import("phis.script.SimpleList");
phis.application.war.script.MedicalExpressionList = function(cfg) {
	cfg.autoLoadData = true;
	cfg.autoLoadSchema = false;
	// cfg.disablePagingTbr = true;
	// cfg.enableCnd=false;
	cfg.listServiceId = "medicalExpMaintainService";
	cfg.serverParams = {
		serviceAction : "listMedicalExpRecords"
	};
	cfg.cnds = ['eq', ['$', 'ZXBZ'], ['s', '0']];
	phis.application.war.script.MedicalExpressionList.superclass.constructor.apply(this,
			[cfg]);
},

Ext.extend(phis.application.war.script.MedicalExpressionList, phis.script.SimpleList,
		{
			doLogout : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				Ext.Msg.show({
					title : "注销",
					msg : "是否注销医学表达式[" + r.get("BDSMC") + "],注销操作不可恢复！",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : "medicalExpMaintainService",
										serviceAction : "logoutMedicalExp",
										pkey : r.id
									});
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doLogout);
							} else {
								this.refresh();
							}
						}
					},
					scope : this
				})
			},
			openModule : function(cmd, r, xy) {
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin();
					win.on("hide",this.formWinHide,this);
					win.setPosition(300, 200);
					win.setTitle(module.title);
					this.formWin = win;
					win.show()
					var formData = this.castListDataToForm(r.data, this.schema);
					this.fireEvent("openModule", module)
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								this.checkData=null;
								module.doNew()
								break;
							case "read" :
							case "update" :
								module.op = "update"
								module.initFormData(formData)
						}
					}
				}
			},
			formWinHide:function(){
				this.fireEvent("formWinHide", this)
			},
			castListDataToForm : function(data, schema) {
				var formData = {};
				var items = schema.items;
				var n = items.length;
				for (var i = 0; i < n; i++) {
					var it = items[i];
					var key = it.id;
					if (it.dic) {
						var dicData = {
							"key" : data[key],
							"text" : data[key + "_text"]
						};
						formData[key] = dicData;
					} else {
						formData[key] = data[key];
					}
				}
				Ext.applyIf(formData, data)
				return formData;
			},
			onSave : function(entryName, op, json, data) {
				this.checkData = data;
				this.fireEvent("save", entryName, op, json, data);
				this.refresh();
				if (this.formWin) {
					this.formWin.hide();
				}
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null || this.recordId == r.id) {
					return;
				}
				this.recordId = r.id;
				this.checkData = r.data;
				this.fireEvent("listRowClick");
			},
			doEditExp : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				this.checkData = r.data;
				this.fireEvent("save");
			},
			getCheckData : function() {
				return this.checkData;
			}

		});