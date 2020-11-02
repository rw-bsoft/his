$package("chis.application.tr.script.pmh");

$import("chis.script.BizCombinedModule2");

chis.application.tr.script.pmh.TumourPastMedicalHistoryModule = function(cfg){
	chis.application.tr.script.pmh.TumourPastMedicalHistoryModule.superclass.constructor.apply(this,[cfg]);
	this.width=980;
	this.height = 558
	this.itemWidth = 200;
	this.itemHeight=558;
	this.frame=true;
}

Ext.extend(chis.application.tr.script.pmh.TumourPastMedicalHistoryModule,chis.script.BizCombinedModule2,{
	initPanel : function() {
		var panel = chis.application.tr.script.pmh.TumourPastMedicalHistoryModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.list = this.midiModules[this.actions[0].id];
		this.list.on("loadData", this.onLoadGridData, this);
		this.grid = this.list.grid;
		this.grid.on("rowClick", this.onRowClick, this);
		this.form = this.midiModules[this.actions[1].id];
		this.form.on("save",this.onSaveForm,this);
		this.formItem = panel.items.items[1];
		return panel;
	},
	loadData : function(){
		if(!this.isNotFirstLoad){
			this.isNotFirstLoad = true;
		}else{
			this.formItem.doLayout();
		}
		if(this.list){
			Ext.apply(this.list.exContext,this.exContext);
			var initCnd = ['eq',['$','empiId'],['s',this.exContext.args.empiId||'']]
			this.list.requestData.cnd=initCnd;
			this.list.loadData();
		}
		if(this.form){
			Ext.apply(this.form.exContext,this.exContext);
			this.form.resetButtons();
		}
	},
	onSaveForm : function(entryName, op, json, data){
		if (op == "create") {
			this.TPMHID = json.body.TPMHID;
		} else {
			this.TPMHID = data.TPMHID;
		}
		this.fireEvent("PMHSave",entryName,op,json,data);
		this.list.refresh();
	},
	onLoadGridData : function(store){
		if (this.TPMHID) {
			for (var i = 0; i < store.getCount(); i++) {
				var r = store.getAt(i);
				if (r.get("TPMHID") == this.TPMHID) {
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
	onRowClick : function(grid, index, e){
		this.list.selectedIndex = index;
		var r = grid.store.getAt(index);
		this.process(r, index);
	},
	process : function(r, index) {
		if (!r) {
			this.form.initDataId=null;
			this.form.doNew();
			return;
		}
		var TPMHID = r.get("TPMHID");
		this.form.initDataId = TPMHID;
		var formData = this.castListDataToForm(r.data, this.list.schema);
		this.form.initFormData(formData);
		this.form.validate();
	}
});