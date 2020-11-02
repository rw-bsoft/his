$package("phis.application.war.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.war.script.WardLeaveHospitalForm = function(cfg) {
	cfg.hideTrigger = true;
	cfg.showButtonOnTop = false;
	cfg.modal = true;
	cfg.labelWidth = 60;
	cfg.loadServiceId = "leaveHospitalLoad"
	phis.application.war.script.WardLeaveHospitalForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.afterLoadData, this);
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.war.script.WardLeaveHospitalForm,
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
							buttons : this.createButtons(),
							buttonAlign : 'center',
							items : [{
										xtype : 'fieldset',
										title : '基本信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 4,
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
										title : '出院设置',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 4,
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
				var cyrq = this.form.getForm().findField("XCYRQ");
				// cyrq.setMinValue(this.form.getForm().findField("RYRQ"));
				cyrq.on("change", this.getDays, this)
			},
			onWinShow : function() {
				this.loadData();
			},
			afterLoadData : function() {
				var ycyrq = this.form.getForm().findField("CYRQ");
				var xcyrq = this.form.getForm().findField("XCYRQ");
				if (ycyrq.getValue()) {
					xcyrq.setValue(ycyrq.getValue());
					btns = this.form.buttons;
					if (btns) {
						var n = btns.length
						for (var i = 0; i < n; i++) {
							var btn = btns[i]
							if (btn.cmd == "cancel") {
								if (btn.rendered) {
									btn.setDisabled(false);
								}
								break;
							}
						}
					}
				} else {
					xcyrq.setValue(new Date().format('Y-m-d H:i:s'));
					btns = this.form.buttons;
					if (btns) {
						var n = btns.length
						for (var i = 0; i < n; i++) {
							var btn = btns[i]
							if (btn.cmd == "cancel") {
								if (btn.rendered) {
									btn.setDisabled(true);
								}
								break;
							}
						}
					}
				}
				this.getDays(xcyrq, xcyrq.getValue())
			},
			getDays : function(f, v) {
				var ryrq = this.form.getForm().findField("RYRQ").getValue();
				var days = this.form.getForm().findField("DAYS");
				var mil = Math.abs(Date.parseDate(v.substring(0, 10), 'Y-m-d')
						- Date.parseDate(ryrq.substring(0, 10), 'Y-m-d'));
				var d = parseInt(mil / 86400000);
				days.setValue(d);
			},
			doSave : function() {
				// 判断是否
				var body = {};
				var XCYRQ = this.form.getForm().findField("XCYRQ").getValue();
				var CYFS = this.form.getForm().findField("CYFS").getValue();
				var BZXX = this.form.getForm().findField("BZXX").getValue();
				var brxzf = this.form.getForm().findField("BRXZ");
				var brxz="";
				if(brxzf){
					brxz=brxzf.getValue();
				}
				var ybzyf = this.form.getForm().findField("YBZY");
				var ybzy=""
				if(ybzyf){
					ybzy=ybzyf.getValue();
				}
				if(CYFS=="7" && brxz=="2000"){
					if(ybzy.length ==0){
						MyMessageTip.msg("提示", "金保转院需要录入住院机构!", true);
						return;
					}
				}
				if (!CYFS) {
					MyMessageTip.msg("提示", "出院方式不能为空!", true);
					this.form.getForm().findField("CYFS").focus();
					return;
				}
				if (!XCYRQ) {
					MyMessageTip.msg("提示", "出院时间不能为空!", true);
					this.form.getForm().findField("XCYFS").focus();
					return;
				}
				if (BZXX.length > 127) {
					MyMessageTip.msg("提示", "建议长度不能超过127位", true);
					this.form.getForm().findField("BZXX").focus();
					return;
				}
				body.XCYRQ = XCYRQ;
				body.CYFS = CYFS;
				body.BZXX = BZXX;
				body.ZYH = this.initDataId;
				body.YBZY=ybzy;
				this.form.el.mask("数据提交中...");
				phis.script.rmi.jsonRequest({
							serviceId : "wardPatientManageService",
							serviceAction : "saveLeaveHospitalProve",
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
				Ext.Msg.confirm("提示", "确认取消出院证吗?", function(btn) {
							if (btn == "yes") {
								this.form.el.mask("数据提交中...");
								phis.script.rmi.jsonRequest({
											serviceId : "wardPatientManageService",
											serviceAction : "saveLeaveHospitalProve",
											body : {
												ZYH : this.initDataId,
												CYFS:this.form.getForm().findField("CYFS").getValue()

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
											MyMessageTip.msg("提示", "出院证取消成功!",
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
				var module = this.createModule("hospProve",
						"phis.application.war.WAR/WAR/WAR000401");
				var ZYH = this.data.ZYH;
				if (ZYH == null) {
					MyMessageTip.msg("提示", "打印失败：无法获得病人信息!", true);
					return;
				}
				var ZYHM = this.form.getForm().findField("ZYHM").getValue();
				var BRXM = this.form.getForm().findField("BRXM").getValue();
				var BRCH = this.form.getForm().findField("BRCH").getValue();
				var BRXB = this.form.getForm().findField("BRXB").getRawValue();
				var GZDW = this.form.getForm().findField("GZDW").getValue();
				var BRKS = this.form.getForm().findField("BRKS").getRawValue();
				var RYRQ = this.form.getForm().findField("RYRQ").getValue();

				var XCYRQ = this.form.getForm().findField("XCYRQ").getValue();
				var DAYS = this.form.getForm().findField("DAYS").getValue();
				var RYQK = this.form.getForm().findField("RYQK").getRawValue();
				var CYFS = this.form.getForm().findField("CYFS").getRawValue();
				var BZXX = this.form.getForm().findField("BZXX").getValue();
				module.ZYH = ZYH;
				module.ZYHM = ZYHM;
				module.BRXM = encodeURIComponent(BRXM);
				module.BRCH = BRCH;
				module.BRXB = encodeURIComponent(BRXB);
				module.GZDW = encodeURIComponent(GZDW);
				module.BRKS = encodeURIComponent(BRKS);
				module.RYRQ = encodeURIComponent(RYRQ);

				module.XCYRQ = XCYRQ;
				module.DAYS = DAYS;
				module.RYQK = encodeURIComponent(RYQK);
				module.CYFS = encodeURIComponent(CYFS);
				module.BZXX = encodeURIComponent(BZXX);
				module.initPanel();
				module.doPrint();
			}

		});
