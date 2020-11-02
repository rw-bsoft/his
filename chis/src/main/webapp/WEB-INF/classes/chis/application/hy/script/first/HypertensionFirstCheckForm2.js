$package("chis.application.hy.script.first")
$import("chis.script.BizSimpleListView", "util.Accredit",
		"chis.script.EHRView", "util.dictionary.TreeDicFactory",
		"chis.application.hy.script.record.HypertensionRecordListView");
chis.application.hy.script.first.HypertensionFirstCheckForm2 = function(cfg) {
	if (!cfg)
		cfg = {}
	this.serviceId = "chis.hypertensionFirstService"
	this.HypertensionFirstEntryName = "chis.application.hy.schemas.MDC_HypertensionFirst"
	this.serviceAction = "saveHypertensionFirst"
	this.hypertensionFirstId = cfg.hypertensionFirstId;
	this.empiId = cfg.empiId
	this.initCnd = ['eq', ['$', 'recordNumber'],
			['s', this.hypertensionFirstId]]
	cfg.autoLoadData = true;
	this.entryName = "chis.application.hy.schemas.MDC_HypertensionFirstDetail"
	cfg.height = 350
	cfg.width = 700
	cfg.enableCnd = false
	chis.application.hy.script.first.HypertensionFirstCheckForm2.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(chis.application.hy.script.first.HypertensionFirstCheckForm2,
		chis.script.BizSimpleListView, {
			warpPanel : function(grid) {
				var panel = new Ext.form.FormPanel({
					border : false,
					frame : true,
					layout : 'border',
					width : this.width,
					height : this.height,
					items : [{
						layout : "fit",
						split : true,
						// collapsible:true,
						title : '',
						height : 70,
						region : 'north',
						items : [{
									id : "__szcy_fieldSet",
									xtype : "fieldset",
									title : "血压",
									height : 50,
									width : 500,
									layout : "form",
									layoutConfig : {
										columns : 4
									},
									items : [{
												columnWidth : .5,
												layout : 'table',
												labelWidth : 45,
												items : [{
															xtype : "label",
															html : "收缩压/舒张压:"
														}, {
															id : "__con",
															xtype : "textfield",
															fieldLabel : "血压",
															// anchor:'100%',
															width : 50
														}, {
															xtype : "label",
															html : "/"
														}, {
															id : "__dia",
															xtype : "textfield",
															fieldLabel : "/",
															// anchor:'100%',
															width : 50
														}
												// {
												// xtype : "label",
												// html :
												// "&nbsp;&nbsp;医生:&nbsp;&nbsp;"
												// }, {
												// id : "__doc",
												// dic:"user01",
												// xtype : "combo",
												// fieldLabel : "医生",
												// disabled : true,
												// value :
												// {key:this.mainApp.uid,text:this.mainApp.userName},
												// // anchor:'100%',
												// width : 150
												// }, {
												// xtype : "label",
												// html :
												// "&nbsp;&nbsp;复查时间:&nbsp;&nbsp;"
												// }, {
												// id : "__date",
												// xtype : "datefield",
												// fieldLabel : "复查时间",
												// disabled : true,
												// value : new Date(),
												// // anchor:'100%',
												// width : 150
												// }
												]
											}]
								}]
					}, {
						layout : "fit",
						split : true,
						title : '',
						region : 'center',
						height : 400,
						width : this.width,
						items : grid
					}],
					buttons : [{
								xtype : "button",
								text : "确定",
								handler : this.doSave,
								scope : this
							}, {
								xtype : "button",
								text : "取消",
								handler : function() {
									this.close()
								},
								scope : this
							}]
				});
				this.panel = panel;
				var fieldSet = panel.items.itemAt(0).items.itemAt(0);
				this.fieldSet = fieldSet;
				grid.__this = this
				return panel
			},
			addPanelToWin : function() {
				if (!this.fireEvent("panelInit", this.grid)) {
					return;
				};
				var win = this.getWin();
				win.setTitle("高血压核实");
				var panel = this.warpPanel(this.grid)
				win.add(panel)
				win.doLayout()
				if (!this.hypertensionFirstId && this.empiId) {
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.hypertensionFirstService",
								serviceAction : "getHypertensionFirstId",
								method:"execute",
								empiId : this.empiId
							})
					if (result.code != 200) {
						return;
					}

					this.hypertensionFirstId = result.json.body[0];
					this.requestData.cnd = ['eq', ['$', 'recordNumber'],
							['s', this.hypertensionFirstId]]
				}
			},
			close : function() {
				this.win.hide()
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								title : this.title,
								width : this.width || 800,
								height : this.height || 450,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : "hide",
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								constrain : true,
								shadow : false
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			},
			setArgs : function(hyptFirstId, empiId) {
				this.empiId = empiId
				this.hypertensionFirstId = hyptFirstId;
				this.requestData["cnd"] = ['eq', ['$', 'recordNumber'],
						['s', this.hypertensionFirstId]]
				this.refresh()
			},
			loadData : function() {
				if (!this.hypertensionFirstId && this.empiId) {
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.hypertensionFirstService",
								serviceAction : "getHypertensionFirstId",
								method:"execute",
								empiId : this.empiId
							})

					if (result.code != 200) {
						alert("首诊测压数据加载失败,请先做首诊测压!");
						return;
					}

					this.hypertensionFirstId = result.json.body[0];
					this.requestData.cnd = ['eq', ['$', 'recordNumber'],
							['s', this.hypertensionFirstId]]
				}

				if (this.store)
					this.store.load();

				// 判断是否需要进行高血压核实，如果不需要 则保存按钮显灰
				util.rmi.jsonRequest({
							serviceId : "chis.simpleQuery",
							schema : this.HypertensionFirstEntryName,
							method:"execute",
							cnd : ['eq', ['$', 'recordNumber'],
									['s', this.hypertensionFirstId]]
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData);
								return;
							}
							var body = json["body"]
							if (!body || body.length == 0)
								return;
							var bt_save = this.panel.buttons[0];
							var record = body[0];
							var checkResult = record["diagnosisType"]
							if (checkResult == "1" || checkResult == "3") {
								bt_save.setDisabled(true)
							} else {
								bt_save.setDisabled(false)
							}
							// //判断是否需已经建档 若没有则弹出建档窗口。
							if (checkResult == "1") {
								this.ifCreateHypertensionDoc()
							}
						}, this)
			},
			ifCreateHypertensionDoc : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.simpleQuery",
							method:"execute",
							schema : "chis.application.hy.schemas.MDC_HypertensionRecord",
							cnd : ['eq', ['$', "a.empiId"], ['s', this.empiId]]
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}

				var docs = result.json.body.length;
				if (docs == 0) {

					Ext.Msg.show({
						title : '提示信息',
						msg : '该人已经被确诊为高血压患者，是否现在建立高血压档案?',
						modal : true,
						minWidth : 300,
						maxWidth : 600,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								var m = this.midiModules["ehrView"];
								if (!m) {
									m = new chis.script.EHRView({
										closeNav : true,
										initModules : ['B_01', 'B_02', 'B_03',
												'B_04', 'B_05', 'C_01', 'C_02'],
										mainApp : this.mainApp,
										empiId : this.empiId,
										activeTab : 5
									});
									this.midiModules["ehrView"] = m;
								} else {
									m.ids = {};
									m.ids["empiId"] = this.empiId;
									m.refresh();
								}
								m.getWin().show();
							}
							return;
						},
						scope : this
					})
				}
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
			},

			doSave : function() {
				var con = this.fieldSet.findById("__con")
				var conValue = con.getValue().trim()

				if (conValue.length == 0)
					return;
				var dia = this.fieldSet.findById("__dia")
				var diaValue = dia.getValue().trim()
				if (diaValue.length == 0)
					return;

				var data = {};
				data["constriction1"] = conValue
				data["diastolic1"] = diaValue
				// this.mask("正在执行保存..")
				var requestData = {}
				requestData["hypertensionFirstId"] = this.hypertensionFirstId;
				requestData["constriction1"] = con.getValue().trim();
				requestData["diastolic1"] = dia.getValue().trim();
				requestData["empiId"] = this.empiId
				requestData["measureDoctor"] = this.mainApp.uid
				requestData["measureUnit"] = this.mainApp.deptId
				// this.mask("正在执行保存..")
				util.rmi.jsonRequest({
							serviceId : this.serviceId,
							schema : this.HypertensionFirstEntryName,
							detailSchema : this.entryName,
							method:"execute",
							body : requestData,
							serviceAction : this.serviceAction
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg, this.doSave);
								return;
							}
							con.reset();
							dia.reset();
							this.fireEvent("save");
							this.refresh();
						}, this)
			}
		})
