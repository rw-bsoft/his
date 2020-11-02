// 高血压档案和服药情况
$package("chis.application.hy.script.record");

$import("chis.script.BizCombinedModule2");

chis.application.hy.script.record.HypertensionRecordModule = function(cfg) {
	// alert("HypertensionRecordModule=---->"+$encode(cfg.mainApp.exContext))
	// cfg.colCount = 3;
	// cfg.labelWidth = 90;
	// cfg.autoLoadData = false;
	// this.exContext = cfg.exContext || {};
	cfg.autoLoadData = false;
	chis.application.hy.script.record.HypertensionRecordModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "north";
	this.height = 500;
	this.itemHeight = 278;
	this.itemCollapsible = false;
	this.entryName = "chis.application.hy.schemas.MDC_HypertensionRecord";
	this.loadServiceId = "chis.hypertensionService";
	this.loadAction = "initializeRecord";
	this.on("loadData", this.onLoadData, this);
};

Ext.extend(chis.application.hy.script.record.HypertensionRecordModule,
		chis.script.BizCombinedModule2, {
			initPanel : function(sc) {
				var panel = chis.application.hy.script.record.HypertensionRecordModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.ehrview=this.ehrview;
				this.form.emrview=this.emrview;
				this.form.on("saveHyperRecord", this.onHyperRecordSave, this);
				this.form.on("addModule", this.onAddModule, this);
				this.list = this.midiModules[this.actions[1].id];
				return panel;
			},

			onAddModule : function() {
				var module = "B_10";
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.systemCommonManageService",
							serviceAction : "getSystemConfigValue",
							method : "execute",
							body : "healthCheckType"
						});
				if (result.code > 300) {
					alert("页面参数获取失败，默认为纸质页面！")
					return
				}
				if (result.json.body) {
					var value = result.json.body;
					if (value == "paper") {
						module = "B_10_HTML";
					}
				}
				this.fireEvent("addModule", module,true);
				this.fireEvent("activeModule", module, {
							dataSources : "3"
						});
			},

			getLoadRequest : function() {
				var body = {
					phrId : this.exContext.ids.phrId,
					empiId : this.exContext.ids.empiId
				};
				return body;
			},
			loadData : function() {
				chis.application.hy.script.record.HypertensionRecordModule.superclass.loadData
						.call(this);
				Ext.apply(this.form.exContext, this.exContext);
				Ext.apply(this.list.exContext, this.exContext);
			},
			onLoadData : function(entryName, body) {
				if (body) {
					this.list.manaDoctorId = body.manaDoctorId;
					if (body["MDC_HypertensionRecord_actions"]) {
						this.form.exContext.control = body["MDC_HypertensionRecord_actions"];
						this.form.resetButtons();
						if (!this.form.initDataId) {
							this.form.setButton(["check"], false);
						} else {
							this.form.setButton(["check"], true);
						}
					}
					if (body["MDC_HypertensionMedicine_actions"]) {
						this.list.exContext.control = body["MDC_HypertensionMedicine_actions"];
						this.list.resetButtons();
					}
				}
				// var formData = body[this.entryName + "_data"];
				// var op = formData.op;
				// var rdStatus = this.exContext.ids.recordStatus;
				// if(!rdStatus || rdStatus=='0'){
				// if(op && op=="create"){
				// this.list.resetButton(true);
				// return;
				// }else{
				// this.list.resetButton(false);
				// return;
				// }
				// }
				// if(rdStatus && rdStatus == '1'){
				// this.list.resetButton(true);
				// return;
				// }
			},
			onHyperRecordSave : function(entryName, op, json, data) {
				// this.list.resetButton(false);
				if (op == "create") {
					var listControl = data.MDC_HypertensionMedicine_actions;
					Ext.apply(this.list.exContext.control, listControl);
					this.list.resetButtons();
				}
				this.fireEvent("refreshData", "all");
				this.fireEvent("save", entryName, op, json, data);
				this.fireEvent("chisSave");// phis中用于通知刷新emrView左边树
			}
		});