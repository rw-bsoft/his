$package("chis.application.idr.script")

$import("chis.script.BizCombinedModule2")

chis.application.idr.script.IDR_ReportModule = function(cfg){
	chis.application.idr.script.IDR_ReportModule.superclass.constructor.apply(this,[cfg]);
	this.autoLoadData = false;
}

Ext.extend(chis.application.idr.script.IDR_ReportModule,chis.script.BizCombinedModule2,{
	initPanel : function() {
		var panel = chis.application.idr.script.IDR_ReportModule.superclass.initPanel.call(this);
		this.list=this.midiModules[this.actions[0].id];
		this.list.on("loadData", this.onLoadGridData, this);
		this.grid = this.list.grid;
		this.grid.on("rowclick", this.onRowClick, this);
		this.form = this.midiModules[this.actions[1].id];
		this.form.on("save",this.onFormSave,this);
		this.panel = panel;
		return panel;
	},
	loadData : function() {
		Ext.apply(this.list.exContext, this.exContext);
		Ext.apply(this.form.exContext, this.exContext);
		this.recordId = this.exContext.args.recordId;
		this.createIDR = this.exContext.args.createIDR || false;
		this.list.loadData();
	},
	onLoadGridData : function(store) {
		if(this.createIDR){
			this.form.doCreate();
			this.createIDR = false;
			return;
		}
		if (this.recordId) {
			for (var i = 0; i < store.getCount(); i++) {
				var r = store.getAt(i);
				if (r.get("RecordID") == this.recordId) {
					this.grid.getSelectionModel().selectRecords([r]);
					var n = store.indexOf(r);
					if (n > -1) {
						this.list.selectedIndex = n;
					}
					break;
				}
			}
		}
		if (!this.list.selectedIndex) {
			this.list.selectedIndex = 0;
		}
		var r = store.getAt(this.list.selectedIndex);
		this.process(r, this.list.selectedIndex);
	},

	onRowClick : function(grid, index, e) {
		this.list.selectedIndex = index;
		var r = grid.store.getAt(index);
		this.process(r, index);
	},

	process : function(r, n) {
//		var recordNum = this.list.store.getCount();
//		var control = this.getFrmControl(recordNum);
//		Ext.apply(this.form.exContext.control, control);
//		this.form.resetButtons();

		if (!r) {
			this.form.doNew();
			// this.form.setSaveBtnable(true);
			return;
		}
		var recordId = r.get("RecordID");
		this.form.initDataId = recordId;
		var formData = this
				.castListDataToForm(r.data, this.list.schema);
		this.form.initFormData(formData);
		this.form.validate();
		this.form.onLoadData(this.entryName,formData);
	},
	
	onFormSave : function(entryName,op,json,data){
		this.list.refresh();
		this.fireEvent("save",entryName,op,json,data);
	}
});