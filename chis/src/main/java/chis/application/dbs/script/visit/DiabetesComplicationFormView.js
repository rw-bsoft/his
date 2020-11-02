$package("chis.application.dbs.script.visit")
$import("util.Accredit", "chis.script.BizTableFormView")
chis.application.dbs.script.visit.DiabetesComplicationFormView = function(cfg) {
	cfg.autoLoadSchema = false
	cfg.showButtonOnTop = false
	cfg.autoLoadData = true
	cfg.isCombined = true
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesComplication"
	chis.application.dbs.script.visit.DiabetesComplicationFormView.superclass.constructor.apply(this,
			[cfg])
	this.width = 810
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(chis.application.dbs.script.visit.DiabetesComplicationFormView,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.dbs.script.visit.DiabetesComplicationFormView.superclass.onReady
						.call(this)
				var diagnosisDate = this.form.getForm()
						.findField("diagnosisDate")
				this.diagnosisDate = diagnosisDate
				diagnosisDate.on("keyup", this.onDiagnosisDate, this)
				diagnosisDate.setMaxValue(this.mainApp.serverDate)
			},
			onDiagnosisDate : function() {
				if (this.diagnosisDate.isValid()) {
					this.fireEvent("formInValid", true)
				} else {
					this.fireEvent("formInvalid", false)
				}

			},
			onWinShow : function() {
				this.doNew()
				var form = this.form.getForm()
				var f = form.findField("phrId")
				var g = form.findField("visitId")
				if (f && g) {
					f.setValue(this.exContext.ids.phrId)
					g.setValue(this.exContext.args.r.get("visitId"))
				}
			},
			doCancel : function() {
				this.getWin().hide()
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								title : this.title,
								width : this.width,
								height : 150,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								minimizable : true,
								constrain:true,
								maximizable : true,
								shadow : false,
								buttonAlign : 'center',
								modal : true,
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
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (this.initDataId == null) {
					this.op = "create";
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.diabetesVisitService",
							op : this.op,
							schema : this.entryName,
							body : saveData,
							method:"execute",
							serviceAction : "saveDiabetesComplications"
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "update"
						}, this)// jsonRequest
			},
			setPhrId : function(_phrId) {
				var form = this.form.getForm()
				var phrId = form.findField("phrId")
				if (phrId) {
					phrId.setValue(_phrId)
				}
			},
			setVisitId : function(_visitId) {
				var form = this.form.getForm()
				var visitId = form.findField("visitId")
				if (visitId) {
					visitId.setValue(_visitId)
				}
			},
			setComplication : function(_complication) {
				var form = this.form.getForm()
				var complication = form.findField("complicationCode")
				if (complication) {
					complication.setValue(_complication)
				}
			}
		});