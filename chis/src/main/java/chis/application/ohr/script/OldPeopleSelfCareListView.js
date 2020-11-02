$package("chis.application.ohr.script")

$import("chis.script.BizSimpleListView")

chis.application.ohr.script.OldPeopleSelfCareListView = function(cfg) {
	this.initCnd = ["eq", ["$", "d.status"], ["s", "0"]]
	chis.application.ohr.script.OldPeopleSelfCareListView.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.ohr.script.OldPeopleSelfCareListView, chis.script.BizSimpleListView, {
	refresh : function() {
		if (this.store) {
			this.store.load();
		}
	},
	
	onDblClick : function(grid, index, e) {
		this.doModify();
	},
	
	doModify : function() {
		var r = this.grid.getSelectionModel().getSelected();
		this.showModule(r.data);
	},
	
	showModule : function(data) {
		var empiId = data.empiId;
		var birthDay = data.birthday;
		var personName = data.personName;
		$import("chis.script.EHRView");
		var module = this.midiModules["OldPeopleRecord_EHRView"];
		if (!module) {
			module = new chis.script.EHRView({
				initModules : ['B_11'],
				empiId : empiId,
				closeNav : true,
				activeTab : 0,
				mainApp : this.mainApp
			})
			this.midiModules["OldPeopleRecord_EHRView"] = module;
			module.exContext.args.scid = data.scid;
			module.on("save", this.refresh, this);
		} else {
			module.actionName = "EHR_HealthRecord";
			module.exContext.ids.empiId = empiId;
			module.exContext.args.scid = data.scid;
			module.refresh();
		}
		module.getWin().show();
	}
});