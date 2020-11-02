$package("chis.application.idr.script")

$import("chis.script.BizCombinedModule2")

chis.application.idr.script.IDR_ReportPhisModule = function(cfg){
	cfg.width=1380;
	cfg.height=735;
	chis.application.idr.script.IDR_ReportPhisModule.superclass.constructor.apply(this,[cfg]);
	this.autoLoadData = false;
}

Ext.extend(chis.application.idr.script.IDR_ReportPhisModule,chis.script.BizCombinedModule2,{
	initPanel : function() {
		var panel = chis.application.idr.script.IDR_ReportPhisModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.list=this.midiModules[this.actions[0].id];
		this.list.on("loadData", this.onLoadGridData, this);
		this.grid = this.list.grid;
		this.grid.on("rowclick", this.onRowClick, this);
		this.rModule = this.midiModules[this.actions[1].id];
		this.rModule.on("ZDListRowClick",this.onZDListRowClick,this);
		this.rModule.on("IDRFormNew",this.onIDRFormNew,this);
		this.rModule.on("IDRFormSave",this.onIDRFormSave,this);
		this.loadData();
		return panel;
	},
	
	loadData : function() {
		Ext.apply(this.list.exContext, this.exContext);
		Ext.apply(this.rModule.exContext, this.exContext);
		this.recordId = this.exContext.args.recordId;
		this.rModule.loadData();
		this.list.loadData();
		this.createIDR = this.exContext.args.createIDR || false;
	},
	onLoadGridData : function(store) {
		if(this.createIDR){
			this.rModule.doCreate();
			this.createIDR = false;
			return;
		}
		if (this.recordId) {
			for (var i = 0,len=store.getCount(); i < len; i++) {
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
		if (!r) {
			this.rModule.doCreate();
			// this.form.setSaveBtnable(true);
			return;
		}
		debugger;
		var recordId = r.get("RecordID");
		var MS_BRZD_JLBH = r.get("MS_BRZD_JLBH");
		var ICD10 = r.get("icd10");
		if(!MS_BRZD_JLBH){
			this.rModule.cancelZDListSelect();
		}else{
			this.rModule.selectZDRecord(MS_BRZD_JLBH);
		}
		this.rModule.form.exContext.args.MS_BRZD_JLBH = MS_BRZD_JLBH;
		this.rModule.form.exContext.args.ICD10 = ICD10;
		this.rModule.initDataId = recordId;
		var formData = this
				.castListDataToForm(r.data, this.list.schema);
		this.rModule.initFormData(formData);
		this.rModule.validate();
		this.rModule.onLoadData(this.entryName,formData);
	},
	
	onZDListRowClick : function(JLBH,ICD10){
		var hasIDReport = false;
		if (JLBH) {
			for (var i = 0,len=this.list.store.getCount(); i < len; i++) {
				var r = this.list.store.getAt(i);
				if (r.get("MS_BRZD_JLBH") == JLBH) {
					this.grid.getSelectionModel().selectRecords([r]);
					var n = this.list.store.indexOf(r);
					if (n > -1) {
						this.list.selectedIndex = n;
					}
					hasIDReport = true;
					this.process(r,n);
					break;
				}
			}
		}
		if(!hasIDReport){
			this.list.grid.getSelectionModel().clearSelections();
			this.rModule.doNew(JLBH,ICD10);
		}
	},
	onIDRFormNew : function(){
		this.list.grid.getSelectionModel().clearSelections();
		this.rModule.cancelZDListSelect();
	},
	onIDRFormSave : function(entryName,op,json,data){
		if(op == "create"){
			this.exContext.args.recordId = json.body.RecordID;
			this.recordId = json.body.RecordID;
		}
		this.list.refresh();
	}
});