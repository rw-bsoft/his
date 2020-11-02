$package("chis.application.psy.script.visit");

$import("chis.script.BizSimpleListView",
		"chis.application.psy.script.visit.PsychosisVisitMedicineForm",
		"util.dictionary.DictionaryLoader");

chis.application.psy.script.visit.PsychosisVisitMedicineList = function(cfg) {
	cfg.initCnd = ["eq", ['$', 'visitId'], ['s', cfg.exContext.args.visitId]];
	cfg.height = 406;
	cfg.entryName = "chis.application.psy.schemas.PSY_PsychosisVisitMedicine";
	cfg.disablePagingTbr = true;
	chis.application.psy.script.visit.PsychosisVisitMedicineList.superclass.constructor.apply(this, [cfg]);
	this.autoLoadData = false;
};

Ext.extend(chis.application.psy.script.visit.PsychosisVisitMedicineList, chis.script.BizSimpleListView, {
			doAdd : function() {
				var module = this.midiModules["PsychosisVisitMedicineForm"];
				var cfg = {};
				cfg.mainApp = this.mainApp;
				cfg.showButtonOnTop = true;
				cfg.autoLoadSchema = false;
				cfg.autoLoadData = false;
				cfg.isCombined = true;
				cfg.op = "create";
				cfg.title = "增加用药种类";
				cfg.phrId = this.exContext.ids.phrId;
				cfg.visitId = this.exContext.args.visitId;
				cfg.entryName = this.entryName;
				cfg.mainApp = this.mainApp;
				if (!module) {
					module = new chis.application.psy.script.visit.PsychosisVisitMedicineForm(cfg);
					this.midiModules["PsychosisVisitMedicineForm"] = module;
					var form = module.initPanel();
					this.form = form;
					module.on("addItem", this.onAddItem, this);
				} else {
					module.phrId = this.exContext.ids.phrId;
					module.visitId = this.exContext.args.visitId;
				}
				var win = module.getWin();
				win.add(this.form);
				win.show();
			},
			onAddItem : function(data) {
				if (!this.schema) {
					return;
				}
				var items = this.schema.items;
				var r = {};
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					if (it.type == "date" && data[it.id]) {
						r[it.id] = data[it.id].format("Y-m-d");
						continue;
					}
					if (it.dic && data[it.id]) {
						var dic = util.dictionary.DictionaryLoader.load(it.dic);
						r[it.id] = data[it.id];
						r[it.id + "_text"] = dic["wraper"][data[it.id]].text;
						continue;
					}
					r[it.id] = data[it.id];
				}
				r.createUnit = this.mainApp.deptId;
				r.createUnit_text = this.mainApp.dept;
				r.createDate = this.mainApp.serverDate;
				r.createUser = this.mainApp.uid;
				r.createUser_text = this.mainApp.uname;
				r.lastModifyUnit = this.mainApp.deptId;
				r.lastModifyUnit_text = this.mainApp.dept;
				r.lastModifyDate = this.mainApp.serverDate;
				r.lastModifyUser = this.mainApp.uid;
				r.lastModifyUser_text = this.mainApp.uname;
				
				var record = new Ext.data.Record(r);
				this.store.add(record);
				this.grid.getSelectionModel().selectLastRow();
				this.fireEvent("medicineModified");
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
									if (!r.get("recordId")) {
										var rowIndex = this.store.indexOf(r);
										this.store.remove(r);
										rowIndex = rowIndex >= this.store
												.getCount()
												? rowIndex - 1
												: rowIndex;
										this.grid.getSelectionModel()
												.selectRow(rowIndex);
									} else {
										this.processRemove();
									}
								}
							},
							scope : this
						});
			},
			getMedicineList : function() {
				var array = [];
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					array.push(r.data);
				}
				return array;
			},
			delAllMedicine : function(){
				var recordNum = this.store.getCount();
				if(recordNum > 0){
					this.store.removeAll();
					this.store.commitChanges();
				}
			},
			loadData : function() {
				if (this.exContext.args.visitId == null) {
					this.store.removeAll();
					return
				} else {
					this.requestData.cnd = ['eq', ['$', 'a.visitId'],
							['s', this.exContext.args.visitId]];
				}
				chis.application.psy.script.visit.PsychosisVisitMedicineList.superclass.loadData
						.call(this);
			}
		});