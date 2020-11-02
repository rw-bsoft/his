$package("chis.application.def.script.intellect")
$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.def.script.intellect.IntellectDeformityRecordLogoutFormView = function(cfg) {
	cfg.entryName = "chis.application.hr.schemas.EHR_MultitermWriteOff";
	this.autoLoadSchema = cfg.autoLoadSchema
	this.autoLoadData = cfg.autoLoadData
	this.isCombined = cfg.isCombined
	this.showButtonOnTop = cfg.showButtonOnTop
	this.colCount = cfg.colCount
	chis.application.def.script.intellect.IntellectDeformityRecordLogoutFormView.superclass.constructor.apply(this,
			[cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(chis.application.def.script.intellect.IntellectDeformityRecordLogoutFormView,
		chis.script.BizTableFormView, {

			onWinShow : function() {
				this.doNew()
			},
			onReady : function() {
				chis.application.def.script.intellect.IntellectDeformityRecordLogoutFormView.superclass.onReady
						.call(this);
				var cancellationReason = this.form.getForm()
						.findField("cancellationReason");
				if (cancellationReason)
					cancellationReason.on("select", function(field) {
								var value = field.getValue();
								this.setDeadReason(value);
							}, this);
				var cancellationUser = this.form.getForm()
						.findField("cancellationUser");
				if (cancellationUser)
					cancellationUser.disable();

				var cancellationDate = this.form.getForm()
						.findField("cancellationDate");
				if (cancellationDate)
					cancellationDate.disable();
			},
			setDeadReason : function(value) {
				var deadReason = this.form.getForm().findField("deadReason");
				var deadDate = this.form.getForm().findField("deadDate");
				if (value == "1") {
					deadReason.enable();
					deadReason.allowBlank = false;

					deadDate.enable();
					deadDate.allowBlank = false;
				} else {
					deadReason.reset();
					deadReason.disable();
					deadReason.allowBlank = true;

					deadDate.reset();
					deadDate.disable();
					deadDate.allowBlank = true;
				}
				deadReason.validate();
				deadDate.validate();
			},
			saveToServer : function(saveData) {
				// 如果是死亡注销，则填写死亡时间
				var values = {}
				Ext.applyIf(saveData, this.record.data)
				Ext.apply(values, saveData);
				// 如果是死亡注销，则填写死亡标志
				var cancellationReason = values.cancellationReason;
				if (cancellationReason == "1") {
					values.deadFlag = "y"
				}
				Ext.Msg.show({
					title : '档案注销',
					msg : '档案注销后将无法操作，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							if (cancellationReason == "1"
									|| cancellationReason == "2") { // **
								// 死亡或迁出注销所有档案
								Ext.Msg.show({
									title : '确认注销[' + values.personName
											+ ']的所有档案',
									msg : values.personName
											+ '已死亡或迁出，将同时注销该人所有相关档案，是否继续？',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "yes") {
											var req = {};
											req.serviceId = "chis.healthRecordService";
											req.serviceAction = "logoutAllRecords";
											req.body = values;
											this.logOut(req);
										}
									},
									scope : this
								})
							} else {
								Ext.Msg.show({
									title : '残疾人档案注销',
									msg : values.personName
											+ '的残疾人档案将被注销且不能恢复，确定是否继续？',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.OKCANCEL,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "ok") {
											var req = {};
											req.serviceId = "chis.defIntellectService";
											req.serviceAction = "saveLogoutIntellectDeformityRecord";
											req.body = values;
											this.logOut(req);
										}
									},
									scope : this
								});

							}
						}
					},
					scope : this
				});
			},
			logOut : function(req) {
				this.form.el.mask("正在提交请求，请稍候...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : req.serviceId,
							serviceAction : req.serviceAction,
							method:"execute",
							body : req.body
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code < 300) {
								this.doCancel();
								this.fireEvent("writeOff",this.entryName,this.op,json,this.record.data)
							} else {
								this.processReturnMsg(code, msg)
							}
						}, this)
			},
			
			doCancel : function() {
				this.getWin().hide();
			},
			onWinShow : function() {
				this.doNew();
				this.data.phrId = this.record.get("phrId")
				var deadReason = this.form.getForm().findField("deadReason");

				deadReason.reset();
				deadReason.disable();
				deadReason.allowBlank = true;

				this.form.getForm().findField("cancellationDate")
						.setValue(this.mainApp.serverDate);
				this.form.getForm().findField("cancellationReason")
						.setValue(this.record.get("cancellationReason"));
				this.form.getForm().findField("cancellationUser").setValue({
							key : this.mainApp.uid,
							text : this.mainApp.uname
						});
				this.validate();
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								autoHeight : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								autoScroll : true,
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
			}
		});