$package("chis.application.hy.script.medicine");

$import("app.desktop.Module", "chis.script.BizSimpleListView",
		"util.dictionary.DictionaryLoader");

chis.application.hy.script.medicine.HypertensionMedicineList = function(cfg) {
	cfg.height = 420;
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	chis.application.hy.script.medicine.HypertensionMedicineList.superclass.constructor
			.apply(this, [cfg]);
	this.entryName = "chis.application.hy.schemas.MDC_HypertensionMedicine";
};

Ext.extend(chis.application.hy.script.medicine.HypertensionMedicineList,
		chis.script.BizSimpleListView, {
			doAdd : function(item, e) {
				var view = this.midiModules["medicineForm"];
				var config = this.loadModuleCfg(this.refModule);
				if (!config) {
					Ext.Msg.alert("错误", "服药情况明细模块加载失败！");
					return;
				}
				var cfg = {
					actions : config.actions,
					entryName : this.entryName,
					title : this.name + '-' + item.text,
					mainApp : this.mainApp,
					op : "create",
					saveServiceId : "chis.hypertensionService",
					phrId : this.phrId,
					visitId : this.visitId
				};
				if (!view) {
					$import(config.script);
					view = eval("new " + config.script + "(cfg)");
					this.midiModules["medicineForm"] = view;
					view.on("addItem", this.onAddItem, this);
				} else {
					Ext.apply(view, cfg);
				}
				view.doNew();
				view.getWin().show();
			},

			onAddItem : function(data) {
				if (!this.schema) {
					return;
				}
				var count = this.store.getCount();
				for (var i = 0; i < count; i++) {
					if (data.medicineName == '109999999999') {
						continue;
					}
					if (this.store.getAt(i).data.medicineName == data.medicineName) {
						Ext.Msg
								.alert(
										"提示",
										"已添加过药品： "
												+ this.store.getAt(i).data['medicineName_text']);
						return;
					}
				}
				var items = this.schema.items;
				var r = {};
				var useUnits = "";
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					r[it.id] = data[it.id];
					if (it.dic && data[it.id]) {
						r[it.id + '_text'] = data[it.id + '_text'];
						continue;
					}
				}
				r.useUnits = useUnits;
				r.visitId = this.exContext.args.visitId;
				r.phrId = this.exContext.args.phrId;
				r.createUser = this.mainApp.uid;
				r.lastModifyUser_text = this.mainApp.uname;
				r.createUnit = this.mainApp.deptId;
				r.createUnit_text = this.mainApp.dept;
				r.creaetDate = new Date(this.mainApp.serverDate)
						.format("Y-m-d");
				r.lastModifyUser = this.mainApp.uid;
				r.lastModifyUser_text = this.mainApp.uname;
				r.lastModifyDate = new Date(this.mainApp.serverDate)
						.format("Y-m-d");
				r.lastModifyUnit = this.mainApp.deptId;
				r.lastModifyUnit_text = this.mainApp.dept;
				r.phrId=this.exContext.ids.phrId;
				r.empiId= this.exContext.ids.empiId;
//				r.EHR_HealthRecord.status = this.exContext.ids["phrId.status"];
//				r.MDC_HypertensionRecord.status = this.exContext.ids["MDC_HypertensionRecord.phrId.status"];
//				r.MDC_HypertensionRecord.manaDoctorId = this.manaDoctorId;
//				r.recordNum= this.store.getCount();
				r.visitId="0000000000000000";
				var cp = {};
				cp["phrId"] = this.exContext.ids.phrId;
				cp["empiId"] = this.exContext.ids.empiId;
				cp["EHR_HealthRecord.status"] = this.exContext.ids["phrId.status"];
				cp["MDC_HypertensionRecord.status"] = this.exContext.ids["MDC_HypertensionRecord.phrId.status"];
				cp["MDC_HypertensionRecord.manaDoctorId"] = this.manaDoctorId;
				cp["recordNum"] = this.store.getCount();
				r.cp = cp;

				util.rmi.jsonRequest({
							serviceId : "chis.hypertensionService",
							serviceAction : "saveHyperMedicine",
							method : "execute",
							op : "create",
							body : r,
							schema : this.entryName
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg,
												this.onAddItem);
							}
							// this.refresh();
							var record = new Ext.data.Record(json.body);
							this.store.add(record);
							this.grid.getSelectionModel().selectLastRow();
							this.midiModules["medicineForm"].doCancel();
							this.fireEvent("save", this.entryName, "create",
									json);
							this.needAddMedicine = false;
							// this.restRDButton();
							this.exContext.control = json.body._actions;
							this.resetButtons();
						}, this);
			},

			doDelete : function(item, e) {
				Ext.Msg.show({
					title : "删除确认",
					msg : '是否确定要删除该项药品?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var r = this.getSelectedRecord(false);
							var rowIndex = this.store.indexOf(r);
							var body = {};
							body.phrId = this.exContext.ids.phrId;
							body.empiId = this.exContext.ids.empiId;
							body["EHR_HealthRecord.status"] = this.exContext.ids["phrId.status"];
							body["MDC_HypertensionRecord.status"] = this.exContext.ids["MDC_HypertensionRecord.phrId.status"];
							body["MDC_HypertensionRecord.manaDoctorId"] = this.manaDoctorId;
							body["recordNum"] = this.store.getCount();
							util.rmi.jsonRequest({
										serviceId : "chis.hypertensionService",
										serviceAction : "removeHyperMedicine",
										method : "execute",
										schema : this.entryName,
										pkey : r.get("recordId"),
										body : body
									}, function(code, msg, json) {
										if (code > 300) {
											this.processReturnMsg(code, msg,
													this.doDelete);
											return;
										}
										this.store.remove(r);
										// this.restRDButton();
										this.exContext.control = json.body._actions;
										this.resetButtons();
										rowIndex = rowIndex >= this.store
												.getCount()
												? rowIndex - 1
												: rowIndex;
										this.grid.getSelectionModel()
												.selectRow(rowIndex);

									}, this);
						}
					},
					scope : this
				});
			},

			doModify : function(item, e) {
				if (this.store.getCount() == 0) {
					return
				}
				var selected = this.getSelectedRecord();
				this.selectedId = selected.id;
				var view = this.midiModules["updateForm"];
				var config = this.loadModuleCfg(this.refModule);
				if (!config) {
					Ext.Msg.alert("错误", "服药情况明细模块加载失败！");
					return;
				}
				var cfg = {
					readOnly : this.readOnly,
					actions : config.actions,
					entryName : this.entryName,
					title : this.name + '-' + item.text,
					mainApp : this.mainApp,
					op : "update",
					saveServiceId : "chis.hypertensionService",
					record : selected,
					initDataId:this.selectedId
				};
				if (!view) {
					$import(config.script);
					view = eval("new " + config.script + "(cfg)");
					this.midiModules["updateForm"] = view;
					var form = view.initPanel()
					this.form = form
					view.on("updateItem", this.onUpdateItem, this);
				} else {
					Ext.apply(view, cfg);
				}
				var win = view.getWin()
				win.add(this.form)
				win.show();
//				view.getWin().show();
			},

			onDblClick : function() {
				this.doModify({
							cmd : "update",
							text : "修改"
						});
			},

			onUpdateItem : function(data) {
				if (!this.schema) {
					return;
				}
				var count = this.store.getCount();
				for (var i = 0; i < count; i++) {
					if (data.medicineName == '109999999999') {
						continue;
					}
					var rd = this.store.getAt(i).data;
					if (rd.recordId != data.recordId
							&& rd.medicineName == data.medicineName) {
						Ext.Msg
								.alert(
										"提示",
										"已添加过药品： "
												+ this.store.getAt(i).data['medicineName_text']);
						return;
					}
				}
				var selected = this.store.getById(this.selectedId);
				var sltData = selected.data;
				var items = this.schema.items;
				var useUnits = "";
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					if (it.id.substr(0, 10) == "lastModify") {
						continue;
					}
					if (it.dic && data[it.id]) {
						var dic = util.dictionary.DictionaryLoader.load(it.dic);
						if (!dic["wraper"][data[it.id]]) {
							continue;
						}
						sltData[it.id + "_text"] = dic["wraper"][data[it.id]].text;
						if (it.id == "medicineUnit") {
							useUnits = dic["wraper"][data[it.id]].text;
						}
						continue;
					}
					sltData[it.id] = data[it.id];
				}
				sltData.lastModifyDate = new Date(this.mainApp.serverDate)
						.format("Y-m-d");
				sltData.lastModifyUser = this.mainApp.uid;
				sltData.lastModifyUser_text = this.mainApp.uname;
				sltData.useUnits = useUnits;
				this.store.getById(this.selectedId).commit();

				util.rmi.jsonRequest({
							serviceId : "chis.hypertensionService",
							serviceAction : "saveHyperMedicine",
							method : "execute",
							op : "update",
							body : sltData,
							schema : this.entryName
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.onUpdateItem);
								return;
							}
							// this.refresh();
							this.grid.getSelectionModel()
									.selectRow(this.selectedId);
							this.midiModules["updateForm"].doCancel();
						}, this);

				this.midiModules["updateForm"].doCancel();
			},

			doImport : function(item, e) {
				var module = this.midiModules["HypertensionMedicineImportList"];
				var list = this.list;
				var cfg = {};
				cfg.title = "药品情况";
				cfg.actions = [{
							id : "import",
							name : "导入",
							iconCls : "healthDoc_import"
						}];
				cfg.empiId = this.exContext.ids.empiId;
				cfg.entryName = "chis.application.his.schemas.HIS_RecipeDetail";
				cfg.autoLoadData = false;
				cfg.autoLoadSchema = false;
				cfg.isCombined = false;
				cfg.showButtonOnTop = true;
				cfg.readOnly = this.readOnly;
				cfg.modal = true;
				if (!module) {
					$import("chis.application.hy.script.medicine.HypertensionMedicineImportList");
					module = new chis.application.hy.script.medicine.HypertensionMedicineImportList(cfg);
					module.on("import", this.onImport, this);
					this.midiModules["HypertensionMedicineImportList"] = module;
					list = module.initPanel();
					list.border = false;
					list.frame = false;
					this.list = list;
				} else {
					Ext.apply(module, cfg);
				}
				var win = module.getWin();
				win.add(list);
				win.show();
			},
			onImport : function(r) {
				var data = {};
				data.phrId = this.phrId;
				data.medicineName = r.get("drugCode");
				data.medicineName_text = r.get("drugName");
				data.medicineFrequency = r.get("frequency");
				data.totalCount = r.get("totalCount");
				data.medicineDate = r.get("createDate");
				data.medicineDosage = r.get("onesDose");
				data.useUnits = r.get("useUnits");

				var record = new Ext.data.Record(data);
				this.store.add(record);
				this.store.commitChanges();
			},
//			getMedicineList : function() {
//				var array = [];
//				for (var i = 0; i < this.store.getCount(); i++) {
//					var r = this.store.getAt(i);
//					array.push(r.data);
//				}
//				return array;
//			},
			resetButton : function(disable) {
				var btns = this.grid.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount();
				var recordNum = this.store.getCount();
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i);
					if (disable) {
						btn.disable();
					} else {
						btn.enable();
					}
					if (recordNum == 0 && (i == 1 || i == 2)) {
						btn.disable();
					}
				}
			},
			restRDButton : function() {
				var recordNum = this.store.getCount();
				var btns = this.grid.getTopToolbar().items;
				if (recordNum > 0)
					if (recordNum == 0) {
						if (btns) {
							btns.item(1).disable();
							btns.item(2).disable();
						}
					} else {
						if (btns) {
							btns.item(1).enable();
							btns.item(2).enable();
						}
					}
			}
		});