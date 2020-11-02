$package("chis.application.wl.script");

$import("chis.application.hr.script.HealthRecordList");

chis.application.wl.script.MyWorkHealthRecordList = function(cfg) {
	this.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	chis.application.wl.script.MyWorkHealthRecordList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.wl.script.MyWorkHealthRecordList, chis.application.hr.script.HealthRecordList, {
	getPagingToolbar : function(store) {
		var cfg = {
			pageSize : 25,
			store : store,
			requestData : this.requestData,
			displayInfo : true,
			emptyMsg : "无相关记录"
		}
		var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
		this.pagingToolbar = pagingToolbar;
		return pagingToolbar;
	},
	
	onSelectEMPI : function(r) {
		var empiId = r.empiId;
		var birthDay = r.birthday;
		var personName = r.personName;
		var module = this.midiModules["HealthRecord_EHRView"]
		if (!module) {
			$import("chis.script.EHRView")
			var initModules = ['B_01', 'B_02', 'B_03', 'B_04', 'B_05']
			if (this.checkHasAssessRegisterModule(birthDay)) {
				initModules.push("M_01")
			}
			if (this.checkAge(birthDay)) {
				initModules.push("B_06")
			}
			module = new chis.script.EHRView({
						initModules : initModules,
						empiId : empiId,
						closeNav : true,
						mainApp : this.mainApp,
						activeTab : 3
					})
			this.midiModules["HealthRecord_EHRView"] = module
			module.on("save", this.refresh, this);
		} else {
			if (!this.checkAge(birthDay) && module.activeModules["B_06"]) {
				module.activeModules["B_06"] = false
				if (module.mainTab.find("mKey", "B_06").length > 0) {
					module.mainTab
							.remove(module.mainTab.find("mKey", "B_06")[0]);
				}
			} else if (this.checkAge(birthDay) && !module.activeModules["B_06"]) {
				if (module.mainTab.find("mKey", "B_06").length == 0) {
					module.activeModules["B_06"] = true
					module.mainTab.add(module.getModuleCfg("B_06"))
				}
			}
	
			if (!this.checkHasAssessRegisterModule(birthDay)
					&& module.activeModules["M_01"]) {
				module.activeModules["M_01"] = false
				if (module.mainTab.find("mKey", "M_01").length > 0) {
					module.mainTab
							.remove(module.mainTab.find("mKey", "M_01")[0]);
				}
			} else if (this.checkHasAssessRegisterModule(birthDay)
					&& !module.activeModules["M_01"]) {
				if (module.mainTab.find("mKey", "M_01").length == 0) {
					module.activeModules["M_01"] = true
					module.mainTab.add(module.getModuleCfg("M_01"))
				}
			}
//			module.ids = {}
			module.actionName = "EHR_HealthRecord"
//			module.ids["empiId"] = empiId
//			module.ids["personName"] = personName
			module.exContext.ids["empiId"] = empiId;
			module.refresh();
		}
		module.getWin().show() 
	}
});