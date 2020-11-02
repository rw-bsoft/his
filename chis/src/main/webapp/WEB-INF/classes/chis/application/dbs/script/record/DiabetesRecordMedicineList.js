$package("chis.application.dbs.script.record");

$import("app.desktop.Module", "chis.script.BizSimpleListView",
		"chis.application.dbs.script.record.DiabetesRecordMedicineForm", "util.dictionary.DictionaryLoader");

chis.application.dbs.script.record.DiabetesRecordMedicineList = function(cfg) {
	this.height = 180
	this.width = 980
	this.disablePagingTbr = true
	cfg.entryName = "chis.application.dbs.schemas.MDC_DiabetesMedicine"
	cfg.removeServiceId = "chis.diabetesRecordService"
	cfg.removeAction = "removeMedine"
	cfg.listServiceId = "chis.diabetesRecordService"
	cfg.listAction = "getRecordMedicine"
	chis.application.dbs.script.record.DiabetesRecordMedicineList.superclass.constructor.apply(this, [cfg]);
	this.createCls = "chis.application.dbs.script.record.DiabetesRecordMedicineForm"
	this.updateCls = "chis.application.dbs.script.record.DiabetesRecordMedicineForm"
};

Ext.extend(chis.application.dbs.script.record.DiabetesRecordMedicineList, chis.script.BizSimpleListView, {
			doAdd : function(item) {
				var module = this.midiModules["DiabetesRecordMedicineForm"];
				var cfg = {}
				cfg.mainApp = this.mainApp
				cfg.showButtonOnTop = true
				cfg.autoLoadSchema = false
				cfg.autoLoadData = false
				cfg.isCombined = true
				cfg.op = "create"
				cfg.title = "增加用药种类"
				cfg.visitId = '0000000000000000'
				cfg.initDataId = null
				cfg.entryName = this.entryName
				cfg.exContext = this.exContext
				if (!module) {
					module = new chis.application.dbs.script.record.DiabetesRecordMedicineForm(cfg);
					this.midiModules["DiabetesRecordMedicineForm"] = module;
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
			doModify : function(item) {
				var r = this.grid.getSelectionModel().getSelected()
				if(!r){
					return 
				}
				var module = this.midiModules["DiabetesRecordMedicineForm"];
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
					module = new chis.application.dbs.script.record.DiabetesRecordMedicineForm(cfg);
					this.midiModules["DiabetesRecordMedicineForm"] = module;
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
			onSave : function(entryName, op,json, data) {
				if(op == "update"){
					var index = this.grid.store.find("recordId", data.recordId);
					var r = this.grid.store.getAt(index);
					if (!r) {
						return
					}
					Ext.apply(r.data,data)
					r.commit();
				}else{
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
			}
			,
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
			}
			,
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
						var status = obj[data.op]
						if (status) {
							btn.enable()
						} else {
							btn.disable()
						}
					}
				}
			}
			,
			
			disableButtons:function(){
				var btns = this.grid.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount();
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					btn.disable()
				}
			}
			,
			enableButtons:function(){
				var btns = this.grid.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount();
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					btn.enable()
				}
			}
// ,
//			onStoreLoadData:function(store, records, ops){
//				if(this.midiModules["DiabetesRecordMedicineForm"]){
//					this.midiModules["DiabetesRecordMedicineForm"].getWin().items.itemAt(0).buttons[1].show();
//					this.midiModules["DiabetesRecordMedicineForm"].doCreateMedicine();
//				}
//				this.fireEvent("loadData", store)
//			}
		});