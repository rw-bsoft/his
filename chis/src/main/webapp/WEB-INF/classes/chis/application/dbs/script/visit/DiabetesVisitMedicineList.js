$package("chis.application.dbs.script.visit");

$import("app.desktop.Module", "chis.script.BizSimpleListView",
		"chis.application.dbs.script.visit.DiabetesVisitMedicineForm",
		"util.dictionary.DictionaryLoader");

chis.application.dbs.script.visit.DiabetesVisitMedicineList = function(cfg) {
	cfg.initCnd = ["eq", ['$', 'visitId'], ['s', cfg.visitId]]
	cfg.height = 406;
	cfg.entryName = "chis.application.dbs.schemas.MDC_DiabetesMedicine"
	cfg.listServiceId = "chis.diabetesVisitService"
	cfg.removeServiceId = "chis.diabetesVisitService"
	cfg.removeAction = "removeMedine"
	chis.application.dbs.script.visit.DiabetesVisitMedicineList.superclass.constructor
			.apply(this, [cfg]);
	this.createCls = "chis.application.dbs.script.visit.DiabetesVisitMedicineForm"
	this.updateCls = "chis.application.dbs.script.visit.DiabetesVisitMedicineForm"
	this.requestData.serviceAction = "getVisitMedine";
};

Ext.extend(chis.application.dbs.script.visit.DiabetesVisitMedicineList,
		chis.script.BizSimpleListView, {
			doAdd : function(item) {
				var module = this.midiModules["DiabetesVisitMedicineForm"];
				var cfg = {}
				cfg.mainApp = this.mainApp
				cfg.showButtonOnTop = true
				cfg.autoLoadSchema = false
				cfg.autoLoadData = false
				cfg.isCombined = true
				cfg.op = "create"
				cfg.title = "增加用药种类"
				cfg.initDataId = null
				cfg.entryName = this.entryName
				cfg.exContext = this.exContext
				if (!module) {
					module = new chis.application.dbs.script.visit.DiabetesVisitMedicineForm(cfg);
					this.midiModules["DiabetesVisitMedicineForm"] = module;
					var form = module.initPanel()
					this.form = form
					module.on("save", this.onSave, this);
					module.on("beforeSave", this.onBeforeSave, this)
				} else {
					Ext.apply(module,cfg)
				}
				var win = module.getWin()
				win.add(this.form)
				win.show();
			},
			doModify : function(item) {
				var r = this.grid.getSelectionModel().getSelected()
				if(!r){
					return 
				}
				var module = this.midiModules["DiabetesVisitMedicineForm"];
				var cfg = {}
				cfg.mainApp = this.mainApp
				cfg.showButtonOnTop = true
				cfg.autoLoadSchema = false
				cfg.autoLoadData = false
				cfg.isCombined = true
				cfg.op = "update"
				cfg.title = "查看"
				cfg.initDataId = r.get("recordId")
				cfg.r = r
				cfg.entryName = this.entryName
				cfg.exContext = this.exContext
				if (!module) {
					module = new chis.application.dbs.script.visit.DiabetesVisitMedicineForm(cfg);
					this.midiModules["DiabetesVisitMedicineForm"] = module;
					var form = module.initPanel()
					this.form = form
					module.on("save", this.onSave, this);
					module.on("beforeSave",this.onBeforeSave,this)
				} else {
					Ext.apply(module,cfg)
				}
				var win = module.getWin()
				win.add(this.form)
				win.show();
			},
			onSave : function(entryName, op, json, data) {
				if (op == "update") {
					var index = this.grid.store.find("recordId", data.recordId);
					var r = this.grid.store.getAt(index);
					if (!r) {
						return
					}
					Ext.apply(r.data, data)
					r.commit();
				} else {
					var records = []
					var record = new Ext.data.Record(data)
					records.push(record)
					this.store.add(records)
				}
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
									this.processRemove();
								}
							},
							scope : this
						});
			},
			getRemoveRequest:function(r){
				return {
					pkey : r.get("recordId")
				};
			},
			onBeforeSave : function(entryName, op, saveData) {
				if(op == "update"){
					for (var i = 0; i < this.store.getCount(); i++) {
						if (this.store.getAt(i).data.medicineName == saveData.medicineName
								&& saveData.medicineName != '109999999998'
								&& this.store.getAt(i).data.recordId != saveData.recordId) {
							Ext.Msg.alert("提示", "药品重复");
							return false
						}
					}
				}else{
					for (var i = 0; i < this.store.getCount(); i++) {
						if (this.store.getAt(i).data.medicineName == saveData.medicineName
								&& saveData.medicineName != '109999999998') {
							Ext.Msg.alert("提示", "药品重复");
							return false
						}
					}
				}
				return true
			},
			loadData : function() {
				this.requestData.serviceAction = "getVisitMedine";
				this.requestData.r = this.exContext.args.r.data
				chis.application.dbs.script.visit.DiabetesVisitMedicineList.superclass.loadData
						.call(this)
				this.resetButton(this.exContext.control)
			},
			resetButton : function(data) {
				var btns = this.grid.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount();
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					var obj = data["_actions"]
					if (obj) {
						var status = obj["update"]
						if (status) {
							btn.enable()
						} else {
							btn.disable()
						}
					}
				}
			},
			onStoreLoadData : function(store, records, ops) {
				if (this.midiModules["DiabetesVisitMedicineForm"]) {
					// this.midiModules["DiabetesVisitMedicineForm"].getWin().items.itemAt(0).buttons[1].show();
					this.midiModules["DiabetesVisitMedicineForm"]
							.doCreateMedicine();
				}
				this.fireEvent("loadData", store);
				this.doAdd();
			}
		});