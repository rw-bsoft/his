$package("chis.application.tr.script.seemingly")

$import("chis.script.BizSimpleListView")

chis.application.tr.script.seemingly.TumourSeeminglyList = function(cfg) {
	cfg.lockingGrid = true;
	chis.application.tr.script.seemingly.TumourSeeminglyList.superclass.constructor
			.apply(this, [cfg]);
	this.on("firstRowSelected", this.onFirstRowSelected, this);
}

Ext.extend(chis.application.tr.script.seemingly.TumourSeeminglyList,
		chis.script.BizSimpleListView, {
			onFirstRowSelected : function() {
				this.onRowClick();
			},
			onRowClick : function(grid, index, e) {
				this.selectedIndex = index
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var nature = r.get("nature");
				var recheckStatus = r.get("recheckStatus");
				var bts = this.grid.getTopToolbar();
				var recheckBtn = bts.find("cmd", "recheck");
				if (recheckBtn && recheckBtn[0]) {
					if (nature == "0" || nature == "2") {
						recheckBtn[0].enable();
					} else {
						recheckBtn[0].disable();
					}
				}
			},
			doRecheck : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var empiId = r.get("empiId");
				var recordId = r.get("recordId");
				var module = this.createSimpleModule(
						"TumourSeeminglyRecheckModule", this.refRecheckModule);
				module.initPanel();
				module.on("save", this.afterSave, this);
				module.initDataId = recordId;
				module.exContext.control = {};
				module.exContext.args.empiId = empiId;
				module.exContext.args.recordId = recordId;
				this.showWin(module);
				module.loadData();
			},
			doCreateTS : function() {
				var advancedSearchView = this.midiModules["TumourSeeminglyList_EMPIInfoModule"];
				if (!advancedSearchView) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
							{
								title : "个人基本信息查找",
								mainApp : this.mainApp,
								modal : true
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this)
					this.midiModules["TumourSeeminglyList_EMPIInfoModule"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onEmpiSelected : function(data) {
				var empiId = data.empiId
				var module = this.createSimpleModule("TumourSeeminglyModule",
						this.refModule);
				module.initPanel();
				module.on("save", this.afterSave, this);
				module.initDataId = null;
				module.exContext.control = {};
				module.exContext.args.empiId = empiId;
				this.showWin(module);
				module.doNew();
			},
			afterSave : function() {
				this.refresh();
			},
			doModify : function() {
				this.openModule()
			},
			onDblClick : function() {
				this.doModify();
			},
			doCheckResultInput : function() {
				this.openModule()
			},
			openModule : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var empiId = r.get("empiId");
				var recordId = r.get("recordId");
				var module = this.createSimpleModule("TumourSeeminglyModule",
						this.refModule);
				module.initPanel();
				module.on("save", this.afterSave, this);
				module.initDataId = recordId;
				module.exContext.control = {};
				module.exContext.args.empiId = empiId;
				module.exContext.args.recordId = recordId;
				module.exContext[this.entryName] = r;
				this.showWin(module);
				module.loadData();
			}
		});