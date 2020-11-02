$package("chis.application.tb.script.visit");

$import("chis.script.BizCombinedTabModule");

chis.application.tb.script.TuberculosisVisitModule = function(cfg) {
	cfg.autoLoadData = false;
	cfg.itemWidth = 170;
	chis.application.tb.script.TuberculosisVisitModule.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.tb.script.TuberculosisVisitModule, chis.script.BizCombinedModule2, {
	initPanel : function() {
		var panel = chis.application.tb.script.TuberculosisVisitModule.superclass.initPanel
				.call(this);
		this.panel = panel;
		this.list = this.midiModules[this.actions[0].id];
		this.list.on("loadData", this.onLoadGridData, this);
		this.list.on("remove", this.onRemove, this);
		this.grid = this.list.grid;
        this.grid.on("rowClick", this.onRowClick, this);
		this.form = this.midiModules[this.actions[1].id];
		this.form.on("save", this.onSave, this);
		this.form.list=this.list;
		return panel;
	},
	onLoadGridData : function(store) {
		Ext.apply(this.form.exContext,this.exContext);
		if (store.getCount() == 0) {
			if (this.exContext.ids.status == '0') {
				MyMessageTip.msg("提示信息", "没有计划,无需随访!", true);
			}
			return;
		}
		var index  = this.selectedIndex;
		if(this.form.initDataId){
			index= this.list.store.find("visitId", this.form.initDataId)
		}else if(!index) {
			index = 0;
		}
		this.list.selectRow(index);
		var r = this.list.grid.getSelectionModel().getSelected();
		if (!r) {
			return;
		}
		this.selectedIndex = index;
		this.process(r, index);
	},

	onRowClick : function(grid, index, e) {
		if (!this.list) {
			return;
		}
		var r = this.list.grid.getSelectionModel().getSelected();
		if (!r) {
			return;
		}
		this.selectedIndex = index;
		this.process(r, index);
	},

	process : function(r, index) {
		this.exContext.args.empiId = r.get('empiId');
		this.exContext.args.phrId = r.get('phrId');
		this.exContext.args.visitId = r.get("visitId");
		Ext.apply(this.form.exContext, this.exContext);
		this.form.initDataId = r.get("visitId");
		var formData = this.castListDataToForm(r.data,this.list.schema);
		this.form.initFormData(formData);
	},
	onSave : function() {
		this.list.refresh();
	},
	onRemove : function() {
		this.list.selectFirstRow();
		var r = this.list.grid.getSelectionModel().getSelected();
		if (!r) {
			return;
		}
		this.selectedIndex = 0;
		this.process(r, 0);
	}
});
