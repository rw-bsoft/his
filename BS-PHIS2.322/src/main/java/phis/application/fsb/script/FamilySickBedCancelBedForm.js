$package("phis.application.fsb.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.fsb.script.FamilySickBedCancelBedForm = function(cfg) {
	cfg.hideTrigger = true;
	// cfg.showButtonOnTop = false;
	cfg.modal = true;
	cfg.labelWidth = 60;
	cfg.height = 500;
	cfg.autoLoadData = false;
	cfg.loadServiceId = "familyPatientLoad"
	phis.application.fsb.script.FamilySickBedCancelBedForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.afterLoadData, this);
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.fsb.script.FamilySickBedCancelBedForm,
		phis.script.SimpleForm, {
			initPanel : function(sc) {

				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form = new Ext.FormPanel({
							labelWidth : 76,
							frame : true,
							bodyStyle : 'padding:5px 5px 0',
							tbar : this.createButtons(),
							buttonAlign : 'center',
							items : [{
										xtype : 'fieldset',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 3,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('JCXX')
									}, {
										xtype : 'fieldset',
										title : '病人信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 3,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('JBXX')
									}, {
										xtype : 'fieldset',
										title : '撤床信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 3,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('CYXX')
									}]
						});
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				this.form.on("afterrender", this.onReady, this)
				return this.form
			},
			onReady : function() {
				if (this.autoLoadData) {
					this.loadData();
				}
				var cyrq = this.form.getForm().findField("CYRQ");
				// cyrq.setMinValue(this.form.getForm().findField("RYRQ"));
				cyrq.on("change", this.setDays, this)
			},
			onWinShow : function() {
				if (this.initDataId) {
					this.loadData();
				}
			},
			afterLoadData : function(entryName, data) {
				// 判断出院状态
				if (data.CYPB > 0) {
					this.form.getForm().findField("CYRQ").setDisabled(true);
					this.form.getForm().findField("CYFS").setDisabled(true);
					this.form.getTopToolbar().items.item(0).setDisabled(true);
					this.form.getTopToolbar().items.item(1).setDisabled(false);
				} else {
					this.form.getForm().findField("CYRQ").setDisabled(false);
					this.form.getForm().findField("CYFS").setDisabled(false);
					this.form.getTopToolbar().items.item(0).setDisabled(false);
					this.form.getTopToolbar().items.item(1).setDisabled(true);
					this.form.getForm().findField("CYRQ").setValue(Date
							.getServerDate())
				}
				this.setDays();
			},
			setDays : function() {
				var ccrq = this.form.getForm().findField("CYRQ").getValue();
				var ryrq = this.form.getForm().findField("KSRQ").getValue();
				var days = this.form.getForm().findField("JCTS");
				if (ccrq && ryrq) {
					var mil = Math.abs(ccrq - ryrq);
					var d = parseInt(mil / 86400000);
					days.setValue(d);
				}
			},
			doSave : function() {
				// 判断是否
				var body = {};
				var XCYRQ = this.form.getForm().findField("CYRQ").getValue();
				var CYFS = this.form.getForm().findField("CYFS").getValue();
				var CCQK = this.form.getForm().findField("CCQK").getValue();
				// var ZYH = this.initDataId;
				if (!CYFS) {
					MyMessageTip.msg("提示", "转归情况不能为空!", true);
					this.form.getForm().findField("CYFS").focus();
					return;
				}
				if (!XCYRQ) {
					MyMessageTip.msg("提示", "撤床日期不能为空!", true);
					this.form.getForm().findField("CYRQ").focus();
					return;
				}
				// if (BZXX.length > 127) {
				// MyMessageTip.msg("提示", "建议长度不能超过127位", true);
				// this.form.getForm().findField("BZXX").focus();
				// return;
				// }
				body.CYRQ = XCYRQ;
				body.CYFS = CYFS;
				body.CYPB = 1;
				body.CCQK = CCQK;
				// body.BZXX = BZXX;
				body.ZYH = this.initDataId;
				this.form.el.mask("数据提交中...");
				phis.script.rmi.jsonRequest({
							serviceId : "familySickBedManageService",
							serviceAction : "saveCancelSickBed",
							body : body
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg, this.doSave,
										[body]);
								return
							}
							this.loadData();
							this.fireEvent("doSave")
							MyMessageTip.msg("提示", "保存成功!", true);
						}, this)
			},
			doCancel : function() {
				Ext.Msg.confirm("提示", "确认取消撤床证明吗?", function(btn) {
							if (btn == "yes") {
								this.form.el.mask("数据提交中...");
								phis.script.rmi.jsonRequest({
											serviceId : "familySickBedManageService",
											serviceAction : "saveCancelSickBed",
											body : {
												ZYH : this.initDataId,
												CYFS : "",
												CYPB : 0,
												CYRQ : "",
												CCQK : ""
											}
										}, function(code, msg, json) {
											this.form.el.unmask()
											if (code > 300) {
												this
														.processReturnMsg(code,
																msg);
												return
											}
											this.loadData();
											this.fireEvent("doSave")
											MyMessageTip.msg("提示", "撤床证取消成功!",
													true);
										}, this)
							}
						}, this);
			},
			doClose : function() {
				this.win.hide();
			},
			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel)
					return;
				}
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.layout || it.layout != para) {
						continue;
					}
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						// alert(it.acValue);
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			},
			doPrint : function() {
				this.printurl = util.helper.Helper.getUrl();
				var pages = "phis.prints.jrxml.FamilySickBedCancelBed";
				var url = "resources/" + pages + ".print?silentPrint=1"
				url += "&temp=" + new Date().getTime() + "&ZYH="
						+ this.initDataId + "&flag=true";
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				// 预览LODOP.PREVIEW();
				// 预览LODOP.PRINT();
				// LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				// 预览
				LODOP.PREVIEW();
			}

		});
