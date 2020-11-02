$package("phis.application.hos.script")

/**
 * 处方组套维护module zhangyq 2012.05.25
 */
$import("phis.script.SimpleModule","phis.script.widgets.DatetimeField")

phis.application.hos.script.HospitalBedspaceAllocationModule = function(cfg) {
	cfg.width = 700
	phis.application.hos.script.HospitalBedspaceAllocationModule.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.hos.script.HospitalBedspaceAllocationModule,
		phis.script.SimpleModule, {
			onWinShow : function() {
				if (this.form) {
					this.module.initDataBody = {
						"JGID" : this.jgid,
						"BRCH" : this.brch
					};
					this.module.loadData();
				}
				if (this.list) {
					this.hospitalPatientInformationList.requestData.cnd = this.cwks
							+ "#" + this.cwxb;
					this.hospitalPatientInformationList.requestData.pageNo = 1;
					this.hospitalPatientInformationList.requestData.serviceId = "phis.hospitalBrryInfoService";
					this.hospitalPatientInformationList.requestData.serviceAction = "getBrryInfo";
					this.hospitalPatientInformationList.loadData();
				}
			},
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							width : 610,
							height : 500,
							tbar : ['', this.initConditionButtons()],
							items : [{
										layout : "fit",
										region : 'center',
										items : this.getList()
									}, {
										layout : "fit",
										region : 'north',
										height : 132,
										items : this.getForm()
									}]
						});

				this.panel = panel
				return panel
			},
			initConditionButtons : function() {
				var items = []
				items.push({
							xtype : "button",
							text : "分配",
							iconCls : "brick_edit",
							scope : this,
							handler : this.doAllocation
						})
				items.push({
							xtype : "button",
							text : "关闭",
							iconCls : "common_cancel",
							scope : this,
							handler : this.doClose
						})
				return items
			},
			getForm : function() {
				var module = this.midiModules['hospitalBedInformationForm'];
				if (!module) {
					module = this.createModule("hospitalBedInformationForm",
							this.refHospitalBedInformationForm);
					module.initDataBody = {
						"JGID" : this.jgid,
						"BRCH" : this.brch
					};
					module.loadData();
					this.module = module;
					var form = module.initPanel();
					this.form = form;
				}
				return form;
			},
			getList : function() {
				var module = this.midiModules['hospitalPatientInformationList'];
				if (!module) {
					module = this.createModule(
							"hospitalPatientInformationList",
							this.refHospitalPatientInformationList);
					module.requestData.cnd = this.cwks + "#" + this.cwxb;
					module.requestData.pageNo = 1;
					module.requestData.serviceId = "phis.hospitalBrryInfoService";
					module.requestData.serviceAction = "getBrryInfo";
					module.loadData();
					// ['isNull', ['s', 'is'], ['$', 'BRCH']]];
					this.hospitalPatientInformationList = module;
					var list = module.initPanel();
					list.on("rowdblclick", this.onDblClick, this)
					this.list = list;
				}
				return list;
			},
			doAllocation : function() {
				var r = this.hospitalPatientInformationList.getSelectedRecord();
				var data = {
					"BRCH" : this.brch,
					"ZYH" : r.get("ZYH")
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							body : data
						});
				if (r.code == 601) {
					MyMessageTip.msg("提示", "性别不符，不能进行床位分配!", true);
				} else if (r.code == 602) {
					this.doTimeTested();
				} else {
					this.doDetermineAllocation();
				}
			},
			doTimeTested : function() {
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hospitalAdmissionService",
							serviceAction : "getDateTime"
						});
				var code = res.code;
				var msg = res.msg;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				var dateTime = res.json.body;
				this.dateForm = new Ext.FormPanel({
							frame : true,
							labelAlign : 'right',
							defaultType : 'textfield',
							shadow : true,
							items : [new phis.script.widgets.DateTimeField({
										id : 'inDate',
										xtype : 'datetimefield',
										value : dateTime,
										width : 150,
										fieldLabel : '临床入院时间',
										allowBlank : false,
										altFormats : 'Y-m-d H:i:s',
										format : 'Y-m-d H:i:s'
									})]
						})

				this.wind = new Ext.Window({
							layout : "form",
							title : '临床入院时间确认',
							width : 300,
							resizable : true,
							iconCls : 'x-logon-win',
							shim : true,
							buttonAlign : 'center',
							closeAction : 'hide',
							modal : true,
							buttons : [{
										text : '确定',
										handler : this.doDetermineAllocation,
										scope : this
									}]
						})
				this.wind.add(this.dateForm);
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hospitalAdmissionService",
							serviceAction : "getDateTime"
						});
						var code = res.code;
						var msg = res.msg;
						if (code >= 300) {
							this.processReturnMsg(code, msg);
							return;
						}
				var dateTime = res.json.body;
				this.dateForm.getForm().findField("inDate")
						.setValue(dateTime)
				this.wind.show();
				this.wind.on("hide", this.windhide, this);
			},
			doClose : function() {
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			},
			doDetermineAllocation : function() {
				var res = phis.script.rmi.miniJsonRequestSync({
					serviceId : "hospitalAdmissionService",
					serviceAction : "getDateTime"
				});
				var code = res.code;
				var msg = res.msg;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				var text = res.json.body;
				if (this.dateForm) {
					var form = this.dateForm.getForm();
					this.Field = form.findField("inDate");
					if (this.Field) {
						text = this.Field.getValue();
					}
				}
				var r = this.hospitalPatientInformationList.getSelectedRecord();
				var dataBody = {
					"BRCH" : this.brch,
					"ZYH" : r.get("ZYH"),
					"RYRQ" : text
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceActionBedSave,
							body : dataBody
						});

				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return
				} else {
					MyMessageTip.msg("提示", "床位分配成功!", true);
					if (this.wind) {
						this.wind.hide();
					}
					this.doClose();
					this.opener.loadData();
				}
			},
			onDblClick : function() {
				this.doAllocation();
			},
			windhide : function() {
				if (this.wind) {
					this.wind.destroy();
				}
			}
		})
