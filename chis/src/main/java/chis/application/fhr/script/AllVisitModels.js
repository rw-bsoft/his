$package("chis.application.fhr.script");

$import("chis.script.BizSimpleListView", "chis.script.EHRView","chis.script.util.helper.Helper");
chis.application.fhr.script.AllVisitModels = {
	showEhrViewWin : function(v) {
		var cfg = {};
		cfg.closeNav = true;
		var visitModule = v;
		cfg.initModules = visitModule;
		cfg.mainApp = this.mainApp;
		cfg.activeTab = this.activeTab;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["FollowUpList_EHRView"];
		if (!module) {
			module = new chis.script.EHRView(cfg);
			this.midiModules["FollowUpList_EHRView"] = module;
			module.exContext.ids["empiId"] = this.empiId;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.refresh();
		}
		module.exContext.ids.recordStatus = this.recordStatus;
		module.getWin().show();
	},// 肿瘤随访
	showEhrViewWinTHRVisit : function(planId) {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['T_01','T_02','T_03'];
		cfg.mainApp = this.mainApp;
		cfg.activeTab = this.activeTab;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["TumourHighRisk_EHRView"];
		if (!module) {
			module = new chis.script.EHRView(cfg);
			this.midiModules["TumourHighRisk_EHRView"] = module;
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["THRID"] = this.THRID;
			module.exContext.ids["Pkey"] = this.THRID;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.exContext.ids.recordStatus = this.recordStatus;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args.selectedPlanId = planId;
			module.exContext.args.THRID = this.THRID;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["THRID"] = this.THRID;
			module.exContext.ids["Pkey"] = this.THRID;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.exContext.ids.recordStatus = this.recordStatus;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args.selectedPlanId = planId;
			module.exContext.args.THRID = this.THRID;
			module.refresh();
		}
		module.getWin().show();
	},//肿瘤现患随访
	showEhrViewWinTPRVisit : function() {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['T_04', 'T_05'];
		cfg.mainApp = this.mainApp;
		cfg.activeTab = this.activeTab;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["TumourPatientReportCard_EHRView"];
		if (!module) {
			module = new chis.script.EHRView(cfg);
			this.midiModules["TumourPatientReportCard_EHRView"] = module;
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["TPRCID"] = this.TPRCID;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["TPRCID"] = this.TPRCID;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.refresh();
		}
		module.exContext.ids.recordStatus = this.recordStatus;
		module.exContext.args = {};
		module.exContext.args["empiId"] = this.empiId;
		module.exContext.args["TPRCID"] = this.TPRCID;
		module.exContext.args["highRiskType"] = this.highRiskType;
		module.getWin().show();
	},
	// 高血压随访
	showEhrViewWinGXY : function() {
		var cfg = {};
		cfg.closeNav = true;
		var visitModule = ['C_01', 'C_02', 'C_03', 'C_05', 'C_04'];
		// if (this.mainApp.exContext.hypertensionType == 'paper') {
		// visitModule = ['C_01', 'C_02', 'C_03_HTML', 'C_05', 'C_04'];
		// }
		cfg.initModules = visitModule;
		cfg.mainApp = this.mainApp;
		cfg.activeTab = this.activeTab;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["HypertensionRecordListView_EHRView"];
		if (!module) {
			module = new chis.script.EHRView(cfg);
			this.midiModules["HypertensionRecordListView_EHRView"] = module;
			module.exContext.ids["empiId"] = this.empiId;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.refresh();
		}
		module.exContext.ids.recordStatus = this.recordStatus;
		module.getWin().show();
	},// 糖尿病随访
	showEhrViewWinTNB : function() {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['D_01', 'D_02', 'D_03', 'D_05', 'D_04']
		cfg.mainApp = this.mainApp;
		cfg.activeTab = this.activeTab;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["DiabetesRecordListView_EHRView"];
		if (!module) {
			module = new chis.script.EHRView(cfg);
			this.midiModules["DiabetesRecordListView_EHRView"] = module;
			module.exContext.ids["empiId"] = this.empiId;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.refresh();
		}
		module.getWin().show();
	},// 精神病随访
	showEHRViewMuleJSB : function() {
		var cfg = {};
		cfg.initModules = ['P_01', 'P_02', 'P_03', 'P_04'];
		cfg.closeNav = true;
		cfg.mainApp = this.mainApp;
		cfg.empiId = this.empiId;
		var module = this.midiModules["PsychosisRecordListView_EHRView"];
		if (!module) {
			$import("chis.script.EHRView");
			module = new chis.script.EHRView(cfg);
			module.on("save", this.onSave, this);
			this.midiModules["PsychosisRecordListView_EHRView"] = module;
		} else {
			module.exContext.ids["empiId"] = this.empiId;
			module.refresh();
		}
		module.getWin().show();
	},// 老年人
	showModuleLNR : function(data) {
		var empiId = data.empiId;
		var birthDay = data.birthday;
		var personName = data.personName;
		$import("chis.script.EHRView");
		var module = this.midiModules["OldPeopleRecord_EHRView"];
		if (!module) {
			module = new chis.script.EHRView({
						initModules : ['B_06'],
						empiId : empiId,
						closeNav : true,
						activeTab : 0,
						mainApp : this.mainApp
					});
			this.midiModules["OldPeopleRecord_EHRView"] = module;
			module.on("save", this.refresh, this);
		} else {
			module.exContext.ids["empiId"] = empiId;
			module.refresh();
		}
		module.actionName = "MDC_OldPeopleRecord";
		module.getWin().show();
	},// 离休干部//RetiredVeteranCadresRecordList
	showModuleLXGB : function(empiId) {
		var m = this.midiModules["RVCVisitView"];
		if (!m) {
			m = new chis.script.EHRView({
						closeNav : true,
						initModules : ['R_02'],
						mainApp : this.mainApp,
						empiId : empiId
					});
			this.midiModules["RVCVisitView"] = m;
			m.on("save", this.refresh, this);
		} else {
			m.exContext.ids["empiId"] = empiId;
			m.refresh()
		}
		m.getWin().show();
	},// 孕妇随访
	showModuleYFSF : function(empiId,pregnantId,visitId) {
		var module = this.midiModules["PregnantRecordVisit_EHRView"]
		if (!module) {
			module = new chis.script.EHRView({
						initModules : ['G_01', 'G_02'],
						activeTab : 1,
						empiId : empiId,
						closeNav : true,
						mainApp : this.mainApp
					})
			this.midiModules["PregnantRecordVisit_EHRView"] = module
			module.exContext.ids["pregnantId"] = pregnantId;
			module.exContext.args["selectVisitId"] = visitId;
			//module.on("save", this.refreshList, this);
		} else {
			module.exContext.ids = {}
			module.exContext.ids["empiId"] = empiId
			module.exContext.ids["pregnantId"] = pregnantId;
			module.exContext.args["selectVisitId"] = visitId;
			module.refresh();
		}
		module.getWin().show();
	},
	// 获取孕妇档案编号
	getPregnantId : function() {
		var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.pregnantRecordService",
							serviceAction : "getPregnantId",
							method : "execute",
							body : {
								empiId : this.empiId
							}
						});
						if (code > 300) {
								return
							}
						if(result.body){
						return result.body;
						}
	},
	showEHRViewChildrenCheck : function(empiId) {
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.debilityChildrenService",
					serviceAction : "checkDebilityChildrenRecordExists",
					method : "execute",
					body : {
						"empiId" : empiId
					}
				});
		var childrenRecordExists = false;
		if (result.code == 200) {
			if (result.json) {
				childrenRecordExists = result.json.body.recordExists;
				
			}
		}
		$import("chis.script.EHRView");
		var module = this.midiModules["ChildrenHealthRecord_EHRView"];
		var age = this.getAge();
		if (!module) {
			var initModules = ['H_01', 'H_08', 'H_03', 'H_04'];
			if (childrenRecordExists) {
				if (age < 1) {
					initModules = ['H_01', 'H_02', 'H_03', 'H_97',
							'H_09']
				} else if (age < 3) {
					initModules = ['H_01', 'H_02', 'H_03', 'H_97',
							'H_98', 'H_09']
				} else {
					initModules = ['H_01', 'H_02', 'H_03', 'H_97',
							'H_98', 'H_99', 'H_09'];
				}
			} else {
				if (age < 1) {
					initModules = ['H_01', 'H_02', 'H_03', 'H_97']
				} else if (age < 3) {
					initModules = ['H_01', 'H_02', 'H_03', 'H_97',
							'H_98']
				} else {
					initModules = ['H_01', 'H_02', 'H_03', 'H_97',
							'H_98', 'H_99'];
				}
			}
			module = new chis.script.EHRView({
						initModules : initModules,
						empiId : empiId,
						closeNav : true,
						activeTab : this.activeTab,
						mainApp : this.mainApp
					})
			this.midiModules["ChildrenHealthRecord_EHRView"] = module;
			module.exContext.args["maxMonth"] = this.getMaxMonth();
			module.on("save", this.refreshList, this);
			module.getWin().show();
		} else {
			module.exContext.ids["empiId"] = empiId;
			module.exContext.args["maxMonth"] = this.getMaxMonth();
			if (!childrenRecordExists
					&& module.activeModules["H_09"] == true) {
				module.activeModules["H_09"] = false;
				if (module.mainTab.find("mKey", "H_09")) {
					module.activeModules["H_09"] = false;
					module.mainTab.remove(module.mainTab.find("mKey",
							"H_09")[0]);
				}
			} else if (childrenRecordExists
					&& !module.activeModules["H_09"]) {
				module.activeModules["H_09"] = true;
				module.mainTab.add(module.getModuleCfg("H_09"));
			}
			if (age < 1) {
				if (module.activeModules["H_98"] == true) {
					module.activeModules["H_98"] = false;
					if (module.mainTab.find("mKey", "H_98")) {
						module.mainTab.remove(module.mainTab.find(
								"mKey", "H_98")[0]);
					}
				}
				if (module.activeModules["H_99"] == true) {
					module.activeModules["H_99"] = false;
					if (module.mainTab.find("mKey", "H_99")) {
						module.mainTab.remove(module.mainTab.find(
								"mKey", "H_99")[0]);
					}
				}
			} else if (age < 3) {
				if (!module.activeModules["H_98"]) {
					module.activeModules["H_98"] = true;
					module.mainTab.add(module.getModuleCfg("H_98"));
				}
				if (module.activeModules["H_99"] == true) {
					module.activeModules["H_99"] = false;
					if (module.mainTab.find("mKey", "H_99")) {
						module.mainTab.remove(module.mainTab.find(
								"mKey", "H_99")[0]);
					}
				}
			} else if (age >= 3) {
				if (!module.activeModules["H_98"]) {
					module.activeModules["H_98"] = true;
					module.mainTab.add(module.getModuleCfg("H_98"));
				}
				if (!module.activeModules["H_99"]) {
					module.activeModules["H_99"] = true;
					module.mainTab.add(module.getModuleCfg("H_99"));
				}
			}
			module.activeTab=this.activeTab;
			module.exContext.args = {} 
			module.refresh();
			module.getWin().show();
		}
	},
	getMaxMonth : function() {//计算月龄
		var d = new Date(this.birthday);
		var d1 = new Date(this.mainApp.serverDate);
		var d2 = d1.getYear() - d.getYear();
		var d3 = d1.getMonth() - d.getMonth();
		if (d2 > 0) {
			if (d2 > 18) {
				var maxMonth = d2 * 12 + d3;
			} else {
				var maxMonth = 18;
			}
		} else {
			var maxMonth = 18;
		}
		return maxMonth;
	},
	getAge : function() {//计算年龄
		this.currDate = Date
				.parseDate(this.mainApp.serverDate, "Y-m-d");
		var birth = Date.parseDate(this.birthday, "Y-m-d");
		return chis.script.util.helper.Helper.getAgeYear(birth, this.currDate);
	},
	showEhrViewDebilityChildrenVisit : function() {//体弱儿随访
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.childrenHealthRecordService",
					serviceAction : "checkHealthCardExists",
					method:"execute",
					body : {
						"empiId" : this.empiId
					}
				});
		var childrenRecordExists = false;
		if (result.code == 200) {
			if (result.json) {
				childrenRecordExists = result.json.body.recordExists;
			}
		}
		var m = this.midiModules["ehrView"];

		if (!m) {
			var modules = ['H_01', 'H_09'];
			if (childrenRecordExists) {
				modules = ['H_09'];
			}
			$import("chis.script.EHRView");
			m = new chis.script.EHRView({
						initModules : modules,
						mainApp : this.mainApp,
						empiId : this.empiId,
						activeTab : 0,
						closeNav : true
					});
			this.midiModules["ehrView"] = m;
			if (this.recordId) {
				m.exContext.ids["recordId"] = this.recordId;
				m.exContext.args["debilityRecordId"] = this.recordId;
			}
			m.on("save", this.refresh, this);
		} else {
			m.exContext.ids["empiId"] = this.empiId;
			if (this.recordId) {
				m.exContext.ids["recordId"] = this.recordId;
				m.exContext.args["debilityRecordId"] = this.recordId;
			}
			if (childrenRecordExists && m.activeModules["H_01"]) {
				m.activeTab = 0;
				m.activeModules["H_01"] = false;
				if (m.mainTab.find("mKey", "H_01")) {
					m.mainTab.remove(m.mainTab.find("mKey", "H_01")[0]);
				}
			} else if (!childrenRecordExists
					&& !m.activeModules["H_01"]) {
				m.activeModules["H_01"] = true;
				m.activeTab = 0;
				m.mainTab.insert(0, m.getModuleCfg("H_01"));
			}
			m.refresh();
		}
		m.getWin().show();
	},
	ShowEHRViewHypertensionRiskVisit : function(empiId){//高血压高危随访
		var cfg = {}
		cfg.empiId = empiId
		cfg.initModules = ['C_07']
		cfg.closeNav = true
		cfg.mainApp = this.mainApp
		cfg.exContext = this.exContext
		var module = this.midiModules["HypertensionRiskVisitListView_EHRView"]
		if (!module) {
			$import("chis.script.EHRView")
			module = new chis.script.EHRView(cfg)
			module.on("save", this.onSave, this)
			this.midiModules["HypertensionRiskVisitListView_EHRView"] = module
		} else {
			Ext.apply(module, cfg)
			module.exContext.ids = {}
			module.exContext.ids.empiId = empiId
			module.refresh()
		}
		module.getWin().show()
	},
	ShowEHRViewDiabetesOGTTVisit : function(empiId){//糖尿病高危随访
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['D_0101']
		cfg.mainApp = this.mainApp;
		cfg.activeTab = 0;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["DiabetesOGTTModule_EHRView"];
		if (!module) {
			module = new chis.script.EHRView(cfg);
			this.midiModules["DiabetesOGTTModule_EHRView"] = module;
			module.exContext.ids["empiId"] = empiId;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = empiId;
			module.refresh();
		}
		module.exContext.args["OGTTID"] = r.get("OGTTID");
		module.getWin().show();
	}
}