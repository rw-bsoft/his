$package("chis.application.def.script.intellect");

$import("chis.script.BizSimpleListView");

chis.application.def.script.intellect.IntellectTrainingRecordList = function(cfg) {
	this.entryName = "chis.application.def.schemas.DEF_IntellectTrainingRecord"
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	chis.application.def.script.intellect.IntellectTrainingRecordList.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.def.script.intellect.IntellectTrainingRecordList, chis.script.BizSimpleListView,
		{

			doAdd : function() {
				var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF03_1_1_6")
				cfg.title = "残疾训练记录"
				cfg.height = 450
				cfg.modal= true
				cfg.mainApp = this.mainApp
				cfg.op = "create"
				cfg.initDataId = null
				cfg.exContext = this.exContext
				var m = this.midiModules["IntellectTrainingRecordForm"];
				if (!m) {
					$import("chis.application.def.script.intellect.IntellectTrainingRecordForm")
					m = new chis.application.def.script.intellect.IntellectTrainingRecordForm(cfg)
					m.on("save",this.onSave,this)
					this.form = m.initPanel()
					this.midiModules["IntellectTrainingRecordForm"] = m;
				}else{
					Ext.apply(m,cfg)
				}
				var win = m.getWin();
				win.add(this.form)
				win.setPosition(250, 100);
				win.show();
			},
			doModify : function() {
				var r = this.getSelectedRecord()
				if(!r){
					return
				}
				var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF03_1_1_6")
				cfg.title = "残疾训练记录"
				cfg.height = 450
				cfg.modal= true
				cfg.mainApp = this.mainApp
				cfg.initDataId = r.get("id")
				cfg.op = "update"
				var m = this.midiModules["IntellectTrainingRecordForm"];
				if (!m) {
					$import("chis.application.def.script.intellect.IntellectTrainingRecordForm")
					m = new chis.application.def.script.intellect.IntellectTrainingRecordForm(cfg)
					m.on("save",this.onSave,this)
					this.form = m.initPanel()
					this.midiModules["IntellectTrainingRecordForm"] = m;
				}else{
					Ext.apply(m,cfg)
				}
				var win = m.getWin();
				win.add(this.form)
				win.setPosition(250, 100);
				win.show();
			},
			onStoreBeforeLoad : function(store, op) {
			},
			onRowClick : function(grid, rowIndex, e) {
				this.selectedIndex = rowIndex
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store)
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
			},
			onSave:function(entryName, op, json, data){
				if (op == "create") {
					data = this.castFormDataToList(data,this.schema)
					var d = new Ext.data.Record(data)
					d.id = data.id
					this.store.add(d)
					this.selectRow(this.store.getCount()-1)
				} else {
					var index = this.grid.store.find("id", data.id);
					var r = this.grid.store.getAt(index);
					if (!r) {
						return
					}
					Ext.apply(r.data, data)
					r.commit();
				}
				this.store.commitChanges()
			},
			onReady : function() {
				chis.application.def.script.intellect.IntellectTrainingRecordList.superclass.onReady
						.call(this)
				
				this.grid.getColumnModel().getColumnsBy(
						function(c) {
							c.sortable = false
						});
			}
		});