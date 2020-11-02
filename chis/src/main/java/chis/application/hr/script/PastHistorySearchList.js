/**
 * 个人既往史查询列表
 * 
 * @author : tianj
 */
$package("chis.application.hr.script")

$import("chis.script.BizSimpleListView")

chis.application.hr.script.PastHistorySearchList = function(cfg) {
	chis.application.hr.script.PastHistorySearchList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.hr.script.PastHistorySearchList, chis.script.BizSimpleListView, {
	getPagingToolbar : function(store) {
		this.requestData.serviceId = this.saveServiceId
		this.requestData.serviceAction = this.serviceAction

		var cfg = {
			pageSize : this.pageSize || 25,
			store : store,
			requestData : this.requestData,
			displayInfo : true,
			emptyMsg : "无相关记录"
		}
		if (this.showButtonOnPT) {
			cfg.items = this.createButtons();
		}
		var pagingToolbar = new util.widgets.MyPagingToolbar(cfg)
		this.pagingToolbar = pagingToolbar
		return pagingToolbar
	},

	doModify : function() {
		var r = this.grid.getSelectionModel().getSelected();
		this.record = r.data;
		this.onSelectEMPI(r.data);
	},

	checkAge : function(birthDay) {
		var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
		var birth;
		if ((typeof birthDay == 'object')
				&& birthDay.constructor == Date) {
			birth = birthDay;
		} else {
			birth = Date.parseDate(birthDay, "Y-m-d");
		}
		currDate.setYear(currDate.getFullYear()
				- this.mainApp.exContext.oldPeopleAge);
		if (birth <= currDate) {
			return true;
		} else {
			return false;
		}
	},

	onSelectEMPI : function(r) {
		var empiId = r.empiId;
		var birthDay = r.birthday;

		var module = this.midiModules["HealthRecord_EHRView"];
		if (!module) {
			$import("chis.script.EHRView");
			var initModules = ['B_01', 'B_02', 'B_03', 'B_04', 'B_05'];
			if (this.checkAge(birthDay)) {
				initModules.push("B_07");
				initModules.push("B_06");
			}
			module = new chis.script.EHRView({
						initModules : initModules,
						activeTab : 2,
						empiId : empiId,
						closeNav : true,
						mainApp : this.mainApp
					});
			this.midiModules["HealthRecord_EHRView"] = module;
			module.on("save", this.refresh, this);
		} else {
			if (!this.checkAge(birthDay)
					&& module.activeModules["B_07"]
					&& module.activeModules["B_06"]) {
				module.activeModules["B_07"] = false
				module.activeModules["B_06"] = false
				if (module.mainTab.find("mKey", "B_07").length > 0) {
					module.mainTab.remove(module.mainTab.find("mKey",
							"B_07")[0]);
				}
				if (module.mainTab.find("mKey", "B_06").length > 0) {
					module.mainTab.remove(module.mainTab.find("mKey",
							"B_06")[0]);
				}
			} else if (this.checkAge(birthDay)
					&& !module.activeModules["B_07"]
					&& !module.activeModules["B_06"]) {
				if (module.mainTab.find("mKey", "B_07").length == 0) {
					module.activeModules["B_07"] = true;
					module.mainTab.add(module.getModuleCfg("B_07"));
				}
				if (module.mainTab.find("mKey", "B_06").length == 0) {
					module.activeModules["B_06"] = true;
					module.mainTab.add(module.getModuleCfg("B_06"));
				}
			}
			module.actionName = "EHR_HealthRecord";
			module.exContext.ids["empiId"] = empiId;
			module.refresh();
		}
		module.getWin().show();
	},

	onDblClick : function(grid, index, e) {
		this.doModify();
	}
});