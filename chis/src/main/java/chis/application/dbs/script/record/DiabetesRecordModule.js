$package("chis.application.dbs.script.record")
$import("chis.script.BizCombinedModule2",
		"chis.script.util.widgets.MyMessageTip")
chis.application.dbs.script.record.DiabetesRecordModule = function(cfg) {
	this.autoLoadData = false
	chis.application.dbs.script.record.DiabetesRecordModule.superclass.constructor
			.apply(this, [cfg])
	this.width = 1000
	this.layOutRegion = "north";
	this.height = 500;
	this.itemHeight = 250;
	// this.itemCollapsible = false;

}
Ext.extend(chis.application.dbs.script.record.DiabetesRecordModule,
		chis.script.BizCombinedModule2, {
			initPanel : function(sc) {
				var panel = chis.application.dbs.script.record.DiabetesRecordModule.superclass.initPanel
						.call(this, sc);
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.onSave, this);
				this.form.on("addModule", this.onAddModule, this);
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("loadData", this.onListLoadData, this)
				return panel;
			},

			onAddModule : function() {
				var module = "B_10";
				if (this.mainApp.exContext.healthCheckType == 'paper') {
					module = "B_10_HTML";
				}
				this.fireEvent("addModule", module, true);
				this.fireEvent("activeModule", module, {
							dataSources : "4"
						});
			},

			loadData : function() {
				this.panel.el.unmask()
				this.refreshExcontext()
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesRecordService",
							serviceAction : "initializeRecord",
							method : "execute",
							body : {
								empiId : this.exContext.ids.empiId,
								phrId : this.exContext.ids.phrId
							}
						})
						debugger;
				this.result = result
				if (!this.result.json.body) {
					return
				}
				this.midiModules["DiabetesRecordForm"]
						.loadLocalData(result.json.body)
				this.midiModules["DiabetesRecordForm"]
						.changeFieldByVisit(this.result.json.hasVisit);
				this.midiModules["DiabetesRecordMedicineList"].requestData.body = {
					empiId : this.exContext.ids.empiId,
					phrId : this.exContext.ids.phrId
				};
				this.midiModules["DiabetesRecordMedicineList"].loadData()
			},
			onListLoadData : function() {
				if (this.result.json.body.op == "create") {
					this.midiModules["DiabetesRecordMedicineList"]
							.disableButtons()
					return
				}
				this.midiModules["DiabetesRecordMedicineList"]
						.resetButton(this.result.json.body)
			},
			onSave : function(entryName, op, json, data) {
				data["MDC_DiabetesRecord.phrId"] = data.phrId
				if (this.result.json.body.op == "create") {
					this.midiModules["DiabetesRecordMedicineList"]
							.enableButtons()
				}
				this.fireEvent("save", entryName, op, json, data);
				this.fireEvent("refreshEhrView", "D_01");
				this.fireEvent("chisSave");// phis中用于通知刷新emrView左边树
				this.refreshEhrTopIcon();
				if (json && json.hasVisit == false) {
					MyMessageTip.msg("提示", "请进行首次随访", true)
					this.fireEvent("activeModule", "D_03");
				}
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								title : this.title,
								width : this.width,
								height : 570,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								minimizable : true,
								maximizable : true,
								constrain : true,
								shadow : false,
								buttonAlign : 'center',
								items : this.initPanel()
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win;
			}
		});