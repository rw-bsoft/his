$package("phis.application.war.script")

$import("phis.script.SimpleList","phis.script.widgets.DatetimeField")

phis.application.war.script.WardBedAllotList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.modal = true;
	phis.application.war.script.WardBedAllotList.superclass.constructor.apply(this,
			[cfg])

	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.war.script.WardBedAllotList, phis.script.SimpleList, {
			onWinShow : function() {
				this.requestData.cnd = [
						'and',
						['and', ['le', ['$', 'CYPB'], ['i', 1]],
								['isNull', ['$', 'BRCH']]],
						['eq', ['$', 'BRKS'], ['d', this.cwks]]];
				this.loadData();
			},
			onDblClick : function() {
				this.doConfirm();
			},
			dateFormat : function(value, params, r, row, col) {
				return Ext.util.Format.date(Date
								.parseDate(value, "Y-m-d H:i:s"), 'Y.m.d')
			},
			doConfirm : function() {
				// 分配床位信息
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var data = {
					"BRCH" : this.cwhm,
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
										value : dateTime,
										width : 200,
										fieldLabel : '临床入院时间',
										allowBlank : false,
										altFormats : 'Y-m-d H:i:s',
										format : 'Y-m-d H:i:s'
									})]
						})
				this.wind = new Ext.Window({
							layout : "form",
							title : '临床入院时间确认',
							width : 350,
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
			doDetermineAllocation : function() {
				var text = new Date().format('Y-m-d H:i:s');
				if (this.dateForm) {
					var form = this.dateForm.getForm();
					this.Field = form.findField("inDate");
					if (this.Field) {
						text = this.Field.getValue();
					}
				}
				var r = this.getSelectedRecord();
				var dataBody = {
					"BRCH" : this.cwhm,
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
					// this.doClose();
					if (this.wind) {
						this.wind.hide();
					}
					this.doClose();
					this.opener.doRefresh();
				}
			},
			doClose : function() {
				this.win.hide();
			},
			windhide : function() {
				if (this.wind) {
					this.wind.destroy();
				}
			}
		});
