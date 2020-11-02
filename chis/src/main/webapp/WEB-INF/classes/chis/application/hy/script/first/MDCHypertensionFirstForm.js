$package("chis.application.hy.script.first")

$import("app.modules.common", "util.Accredit", "util.widgets.ImageField",
		"util.rmi.jsonRequest", "chis.script.BizModule", "chis.script.EHRView",
		"util.dictionary.RadioDicFactory")

chis.application.hy.script.first.MDCHypertensionFirstForm = function(cfg) {
	this.serviceId = "chis.hypertensionFirstService"
	this.entryName = "chis.application.hy.schemas.MDC_HypertensionFirst"
	this.detailEntryName = "chis.application.hy.schemas.MDC_HypertensionFirstDetail"
	this.serviceAction = "saveHypertensionFirst"
	chis.application.hy.script.first.MDCHypertensionFirstForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(chis.application.hy.script.first.MDCHypertensionFirstForm, chis.script.BizModule, {
	initPanel : function(sc) {
		if (this.form) {
			if (!this.isCombined) {
				this.addPanelToWin();
			}
			this.form.readOnly = this.readOnly;
			return this.form;
		}

		var group1 = util.dictionary.RadioDicFactory.createDic({
					id : "chis.dictionary.ifHypertensionHis"
				})
		group1.on("change", this.changeState, this)
		group1.fieldLabel = "高血压既往史"
		group1.setValue("0")
		group1.name = "group1"

		var group2 = new util.dictionary.RadioDicFactory.createDic({
					id : "chis.dictionary.hypertensionResult"
				})
		group2.fieldLabel = "结果"
		group2.name = "group2"
		group2.on("change", this.checkSave, this)

		var cfg = {
			readOnly : this.readOnly,
			iconCls : 'bogus',
			border : false,
			frame : true,
			autoHeight : true,
			width : 530,
			// autoWidth : true,
			// defaultType : 'textfield',
			shadow : true,
			buttonAlign : 'center',
			items : [{
				columnWidth : .5,
				layout : 'table',
				labelWidth : 45,
				border : false,
				items : [{
					xtype : "label",
					html : "血压:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				}, {
					xtype : "textfield",
					fieldLabel : "血压",
					name : "__con",
					width : "50",
					listeners : {
						change : this.checkSave,
						scope : this
					}
				}, {
					xtype : "label",
					html : "/"
				}, {
					xtype : "textfield",
					fieldLabel : "/",
					width : "50",
					name : "__dia",
					listeners : {
						change : this.checkSave,
						scope : this
					}
				}]
			}, group1, group2],
			buttons : [{
						text : "取消",
						id : "__save",
						handler : function() {
							this.win.hide()
						},
						scope : this
					}, {
						text : "保存",
						id : "__cancel",
						disabled : true,
						handler : this.doSave,
						scope : this
					}]
		}

		this.form = new Ext.FormPanel(cfg)
		this.form.on("afterrender", this.onReady, this);
		return this.form
	},
	
	onReady : function() {
		util.rmi.jsonRequest({
				serviceId : this.serviceId,
				schema : this.detailEntryName,
				empiId : this.empiId,
				empiSchema : "chis.application.mpi.schemas.MPI_DemographicInfo",
				hypertensionFirstSchema : this.entryName,
				serviceAction : "ifHypertensionFirst"
			}, function(code, msg, json) {
				if (code > 300) {
					this.processReturnMsg(code, msg, this.onBrush);
					return;
				}
				var hypertensionFirst = json["hypertensionFirst"]
				if (hypertensionFirst == "1") {
					this.doFirst = true;
				} else {
					this.doFirst = false;
				}
			}, this)
	},
	
	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : "首诊测压",
						width : 550,
						autoHeight : true,
						iconCls : 'icon-form',
						closeAction : 'hide',
						shim : true,
						layout : "form",
						plain : true,
						constrain:true,
						autoScroll : true,
						minimizable : false,
						maximizable : true,
						shadow : false,
						buttonAlign : 'center'
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
			win.add(this.initPanel())
			this.win = win
		}
		win.instance = this;
		return win;
	},
	setEmpiId : function(empiId) {
		this.empiId = empiId
	},
	changeState : function() {
		var form = this.form.getForm();
		var group1 = form.findField("group1");
		var group2 = form.findField("group2");
		if (group1.getValue() == "1") {
			group2.setDisabled(true);
			group2.setValue("1");
		} else {
			group2.setDisabled(false);
			group2.reset()
		}

		this.checkSave()
	},
	checkSave : function() {
		if (this.doFirst == false) {
			bt_save.setDisabled(true)
			return;
		}
		var form = this.form.getForm()
		var group1 = form.findField("group1");
		var bt_save = this.form.buttons[1];
		g1v = group1.getValue()
		if (!g1v || g1v.trim().length == 0) {
			bt_save.setDisabled(true)
			return;
		}

		var group2 = form.findField("group2");
		g2v = group2.getValue();
		if (!g2v || g2v.trim().length == 0) {
			bt_save.setDisabled(true)
			return;
		}

		var con = form.findField("__con");
		if (con.getValue().trim().length == 0) {
			bt_save.setDisabled(true)
			return;
		}

		var dia = form.findField("__dia");
		if (dia.getValue().trim().length == 0) {
			bt_save.setDisabled(true)
			return;
		}
		bt_save.setDisabled(false);
	},
	doSave : function() {
		var form = this.form.getForm();
		var con = form.findField("__con");
		var dia = form.findField("__dia");
		var group1 = form.findField("group1")
		var group2 = form.findField("group2")
		var requestData = {}
		requestData["empiId"] = this.empiId;
		requestData["constriction1"] = con.getValue().trim();
		requestData["diastolic1"] = dia.getValue().trim();
		requestData["diagnosisType"] = group2.getValue().trim();
		requestData["postHypertension"] = group1.getValue().trim();
		requestData["measureDoctor"] = this.mainApp.uid
		requestData["measureUnit"] = this.mainApp.deptId
		// this.mask("正在执行保存..")
		util.rmi.jsonRequest({
					serviceId : this.serviceId,
					schema : this.entryName,
					detailSchema : this.detailEntryName,
					op : "create",
					body : requestData,
					method:"execute",
					serviceAction : this.serviceAction
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg, this.doSave);
						return;
					}
					this.fireEvent("save");
					this.getWin().hide();
					if (group2.getValue() == 1) {
						this.ifCreateHypertensionDoc()
					}
				}, this)
	},
	ifCreateHypertensionDoc : function() {
		util.rmi.jsonRequest({
					serviceId : "chis.hypertensionService",
					schema : "chis.application.hy.schemas.MDC_HypertensionRecord",
					cnd : ['eq', ['$', 'a.empiId'], ['s', this.empiId]],
					method:"execute",
					serviceAction : "ifHypertensionRecordExist"
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg, this.ifCreateHypertensionDoc);
						return;
					}
					if (code == "200") {
						this.showEhrViewWin();
					}
				}, this)
	},

	showEhrViewWin : function() {
		var m = this.midiModules["ehrView"];
		if (!m) {
			m = new chis.script.EHRView({
						closeNav : true,
						initModules : ['C_01', 'C_02', 'C_03', 'C_05',
								'C_04'],
						mainApp : this.mainApp,
						empiId : this.empiId
					});
			this.midiModules["ehrView"] = m;
		} else {
			m.ids = {};
			m.ids["empiId"] = this.empiId;
			m.refresh();
		}
		m.getWin().show();
	}
});