$package("phis.application.chr.script")

$import("phis.script.SimpleModule")
/**
 * 需传入EMR_BL01的BLBH，以this.BLBH接收
 * 
 * @param {}
 *            cfg
 */
phis.application.chr.script.CaseHistoryReviewModule = function(cfg) {
	phis.application.chr.script.CaseHistoryReviewModule.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.chr.script.CaseHistoryReviewModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 340,
										items : this.getLModule()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getRModule()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getLModule : function() {
				var module = this.createModule("logLModule", this.refLModule);
				this.lModule = module;
				module.on("tabchange", this.onlModuleTabChange, this);
				module.on("hasLoadData", this.onHasLoadData, this);
				module.on("recordClick", this.onRowClickOrSelect, this);
				var m = module.initPanel()
				return m;
			},
			getRModule : function() {
				var module = this.createModule("logRModule", this.refRModule);
				this.rModule = module;
				module.on("tabchange", this.onrModuleTabChange, this);
				return module.initPanel();
			},

			initFormData : function(recordId) {
				if (recordId == null) {
					return;
				}
				var r = util.rmi.miniJsonRequestSync({
							serviceId : "phis.caseHistoryReviewService",
							serviceAction : "getFormDataFromDB",
							BLBH : recordId
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.getFileContent);
					return
				}
				if (r.json.body) {
					var formData = r.json.body
					var blbh = formData.BLBH;
					var jzxh = formData.JZXH;
					this.YWID2 = blbh;
					this.YWID1 = jzxh;
					this.lModule.initFormData(formData);
				}
			},
			onHasLoadData : function() {
				this.onRowClickOrSelect();
			},
			onRowClickOrSelect : function() {
				var records = this.lModule.getSelectedRecords();
				var str = this.changeRecordsToId(records);
				if (this.rmTabId == "allRecord") {
					this.rModule.listRecordData("allRecord", this.YWID1, str);
					this.rModule.loadCountByTabID(this.YWID1, null, str);
				} else {
					this.rModule.tab.activate("allRecord");
				}
			},
			changeRecordsToId : function(records) {
				var str = "";
				for (var i = 0; i < records.length; i++) {
					str += records[i].get("BLBH") + ",";
				}
				return str;
			},
			onlModuleTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.id == "allCase") {
					this.single = false;
					if (this.YWID1 == null) {
							return;
					}
					this.lModule.loadAllData();
					if (this.rmTabId == "allRecord") {
						var records = this.lModule.getSelectedRecords();
						var str = this.changeRecordsToId(records);
						this.rModule.listRecordData("allRecord", this.YWID1,
								str);
						this.rModule.loadCountByTabID(this.YWID1, null, str);
					}
				} else if (newTab.id == "caseInfo") {
					this.single = true;
					this.initFormData(this.BLBH);
					if (this.rmTabId == "allRecord") {
						if (this.YWID1 == null) {
							return;
						}
						this.rModule.listRecordData("allRecord", this.YWID1,
								null, this.YWID2);
						this.rModule.loadCountByTabID(this.YWID1, this.YWID2,
								null);
					}
				}
				this.rModule.tab.activate("allRecord");
			},
			onrModuleTabChange : function(tabPanel, newTab, curTab) {
				this.rmTabId = newTab.id;
				if (this.YWID1 == null) {
					return;
				}
				if (this.single) {
					this.rModule.listRecordData(newTab.id, this.YWID1, null,
							this.YWID2);
					this.rModule.loadCountByTabID(this.YWID1, this.YWID2, null);
				} else {
					var records = this.lModule.getSelectedRecords();
					var str = this.changeRecordsToId(records);
					this.rModule.listRecordData(newTab.id, this.YWID1, str);
					this.rModule.loadCountByTabID(this.YWID1, null, str);
				}
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "close";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			}
		});