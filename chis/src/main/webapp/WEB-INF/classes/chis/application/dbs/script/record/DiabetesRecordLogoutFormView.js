
$package("chis.application.dbs.script.record");

$import("chis.script.BizTableFormView", "util.Accredit");

chis.application.dbs.script.record.DiabetesRecordLogoutFormView = function(cfg) {
	cfg.entryName = "chis.application.pub.schemas.EHR_WriteOff";
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	this.title = "糖尿病档案注销";
	this.record = cfg.record;
	cfg.height = 450;
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = true
	cfg.showButtonOnTop = true;
	cfg.isCombined = true
	cfg.colCount = 3;
	cfg.fldDefaultWidth = 145;
	chis.application.dbs.script.record.DiabetesRecordLogoutFormView.superclass.constructor.apply(
			this, [cfg]);
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.dbs.script.record.DiabetesRecordLogoutFormView,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.dbs.script.record.DiabetesRecordLogoutFormView.superclass.onReady
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
									msg :  '注销原因是迁出或死亡，将同时注销['+values.personName+']所有相关档案，是否继续？',
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
									title : '糖尿病档案注销',
									msg : '['+values.personName+']糖尿病档案将被注销，确定是否继续？',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.OKCANCEL,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "ok") {
											var req = {};
											req.serviceId = "chis.diabetesRecordService";
											req.serviceAction = "logoutDiabetesRecord";
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
								maximizable : true,
								constrain:true,
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
