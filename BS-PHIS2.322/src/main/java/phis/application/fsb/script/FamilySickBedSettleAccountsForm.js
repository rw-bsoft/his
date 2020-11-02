$package("phis.application.fsb.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.fsb.script.FamilySickBedSettleAccountsForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = false;
	// Ext.apply(this, phis.application.cfg.script.YbUtil);
	phis.application.fsb.script.FamilySickBedSettleAccountsForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedSettleAccountsForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form = new Ext.FormPanel({
							labelWidth : 55, // label settings here cascade
							// unless overridden
							frame : true,
							bodyStyle : 'padding:5px 5px 0',
							width : 280,
							height : 315,
							buttonAlign : 'center',
							items : [{
										xtype : 'fieldset',
										title : '基本信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 2,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										listeners : {
											"afterrender" : this.onReady,
											scope : this
										},
										defaultType : 'textfield',
										items : this.getItems('JBXX')
									}, {
										xtype : 'fieldset',
										id : 'FPHM',
										title : '结算金额 ',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 1,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('JSJE')
									}, {
										xtype : 'fieldset',
										title : '结算金额补退',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : {
											columns : 1,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										defaultType : 'textfield',
										items : this.getItems('JSJEBT')
									}],
							buttons : (this.tbar || []).concat(this
									.createButton())
						});
				if (!this.isCombined) {
					this.addPanelToWin();
				}

				var schema = sc
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
				this.combo = this.form.getForm().findField("FFFS");
				this.combo.on("select", this.calYSK, this);
				this.combo.getStore().on('load', this.calYSKLoadEvent, this);
				return this.form
			},
			// 回显付费方式事件
			calYSKLoadEvent : function(store, records) {
				var defIndex;
				store.each(function(item, index) {
							if (item.json.MRBZ == "1") {
								defIndex = index;
								this.combo.setValue(item.data.key)
								store.un('load', this.calYSKLoadEvent, this);
								return false;
							}
						}, this);

			},
			// 根据前台选择付款方式的类型从后台计算相应应收款.
			calYSK : function(comb, record, rownum) {
				var payType = {};
				Ext.apply(payType, record.json);
				var form = this.form.getForm();
				payType.YSK = this.round2(this.round2(this.data.ZFHJ, 2)
								- this.round2(this.data.ZHZF, 2)
								- this.round2(this.data.JKHJ, 2), 2);
				this.rmiYSKQuery(payType)
			},
			rmiYSKQuery : function(payType) {
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceAction : "queryPayTypes",
							serviceId : "clinicChargesProcessingService",
							payType : Ext.encode(payType)
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				var ysk = result.json.YSK;
				if (!Ext.isEmpty(ysk)) {
					this.form.getForm().findField("YSJE").setValue(ysk);
					if (ysk < 0) {
						this.form.getForm().findField("JKJE").setValue("0.00");
						this.form.getForm().findField("TZJE").setValue(this
								.round2(-ysk, 2));
						this.form.getForm().findField("YSJE").setValue(this
								.round2(-ysk, 2));
					}
					this.data.YSJE = ysk;
				}
				if (parseFloat(this.data.JSJE) != parseFloat(this.data.YSJE)) {
					this.data.WCJE = this
							.round2(
									this
											.round2(
													this
															.round2(
																	parseFloat(this.data.JSJE)
																			- parseFloat(this.data.YSJE),
																	2),
																	this.data.WCJD
															- 1),
									2)
				}
				var jkje = this.form.getForm().findField("JKJE");
				jkje.setValue();
				this.form.getForm().findField("TZJE").setValue(0);
				jkje.focus(false, 200)
			},
			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync(this.entryName)
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
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					if (it.id == "FYHJ" || it.id == "JKHJ" || it.id == "ZFHJ"
							|| it.id == "TCJE" || it.id == "ZHZF") {
						f.style += 'font-size:16px;font-weight:bold;color:blue;textAlign:right;';
					} else if (it.id == "YSJE" || it.id == "TZJE") {
						f.style += 'font-size:16px;font-weight:bold;color:red;textAlign:right;';
					} else if (it.id == "JKJE") {
						f.style = 'font-size:16px;font-weight:bold;color:red;textAlign:right;';
					}
					MyItems.push(f);
				}
				return MyItems;
			},
			createButton : function() {
				var actions = this.actions;
				var buttons = [];
				if (!actions) {
					return buttons;
				}
				var f1 = 112;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.accessKey = f1 + i;
					btn.cmd = action.id;
					btn.text = action.name + "(F" + (i + 1) + ")";
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.notReadOnly = action.notReadOnly;
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref
				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doCancel : function() {
				this.opener.doCancel();
			},
			doNew : function() {
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (it.id == "FFFS")
						continue;
					var f = form.findField(it.id)
					if (f) {
						f.setValue(it.defaultValue)
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}
						// add by yangl 2012-06-29
						if (it.dic && it.dic.defaultIndex) {
							if (f.store.getCount() == 0)
								return;
							if (isNaN(it.dic.defaultIndex)
									|| f.store.getCount() <= it.dic.defaultIndex)
								it.dic.defaultIndex = 0;
							f.setValue(f.store.getAt(it.dic.defaultIndex)
									.get('key'));
						}
					}
				}
			},
			loadData : function() {
				if (this.loading) {
					return
				}
				if (this.form.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				// if (!this.data.ZHSYBZ) {
				// this.data.ZFHJ = this.round2(parseFloat(this.data.ZFHJ)
				// + parseFloat(this.data.ZHZF), 2)
				// this.data.ZHZF = 0.00;
				// }
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : "familySickBedPatientSelectionService",
							serviceAction : "getSelectionFPHM"
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this_ = this;
								Ext.Msg.alert("提示", msg, function() {
											this_.doCancel();
										});
								return
							}
							if (json.FPHM) {
								this.data.FPHM = json.FPHM;
								var FPHM = this.form.findById("FPHM");
								FPHM.setTitle("结算金额 No." + json.FPHM);
							} else {
								this_ = this;
								Ext.Msg.alert("提示", "请先维护家床发票号码!", function() {
											this_.doCancel();
										});
							}
							if (json.body) {
								this.data.FKJD = json.body.FKJD;
								this.data.FKFS = json.body.FKFS;
								this.data.WCFS = json.body.WCFS;
								this.data.WCJD = json.body.WCJD;
								var form = this.form.getForm()
								var YSJE = form.findField("YSJE");
								var JKJE = form.findField("JKJE");
								var TZJE = form.findField("TZJE");
								var ZFHJ = form.findField("ZFHJ");
								var ZHZF = form.findField("ZHZF");
								ZFHJ.setValue(this.data.ZFHJ);
								ZHZF.setValue(this.data.ZHZF);
								var ysje = this.round2(this.round2(
												this.data.ZFHJ, 2)
												- this
														.round2(this.data.ZHZF,
																2)
												- this
														.round2(this.data.JKHJ,
																2), 2);
								this.data.JSJE = ysje;
								if (ysje < 0) {
									ysje = this.round2(this.round2(-ysje,
													json.body.FKJD - 1), 2);
									ysje = -ysje;
								} else {
									ysje = this.round2(this.round2(ysje,
													json.body.FKJD - 1), 2);
								}
								this.data.YSJE = ysje;
								this.data.WCJE = 0;
								if (parseFloat(this.data.JSJE) != parseFloat(this.data.YSJE)) {
									this.data.WCJE = this
											.round2(
													this
															.round2(
																	this
																			.round2(
																					parseFloat(this.data.JSJE)
																							- parseFloat(this.data.YSJE),
																					2),
																	json.body.WCJD
																			- 1),
													2)
								}
								YSJE.setValue(ysje);
								if (ysje < 0) {
									JKJE.setValue("0.00");
									TZJE.setValue(this.round2(-ysje, 2));
									var YSJEField = YSJE.el.parent().parent()
											.first();
									YSJEField.dom.innerHTML = "应找:";
									YSJE.setValue(this.round2(-ysje, 2));
								} else {
									var YSJEField = YSJE.el.parent().parent()
											.first();
									YSJEField.dom.innerHTML = "应收:";
								}
							}
						}, this)// jsonRequest
			},
			round2 : function(number, fractionDigits) {
				with (Math) {
					return (round(number * pow(10, fractionDigits)) / pow(10,
							fractionDigits)).toFixed(fractionDigits);
				}
			},
			JKJEblur : function() {
				if(this.JSLX==4){
					if (this.JKJEChang) {
						return;
					}
					this.JKJEChang = true;
					var form = this.form.getForm();
					var ZFHJ = form.findField("ZFHJ");
					var JKJE = form.findField("JKJE");
					var JKHJ = form.findField("JKHJ");
					var YSJE = form.findField("YSJE");
					var TZJE = form.findField("TZJE");
					if (JKJE.getValue() == null || JKJE.getValue().length == 0) {
						JKJE.setValue("0.00");
						TZJE.setValue(-YSJE.getValue());
					} else {
							TZJE.setValue(this.round2(JKJE.getValue()
											- this.data.YSJE, 2));
					}
					btns = this.form.buttons
					if (btns) {
						var n = btns.length;
						for (var i = 0; i < n; i++) {
							var btn = btns[i];
							if (btn.cmd == "commit") {
								btn.focus()
								this.JKJEChang = false;
								return;
							}
						}
					}
					return;
				}
				if (this.JKJEChang) {
					return;
				}
				this.JKJEChang = true;
				var form = this.form.getForm();
				var ZFHJ = form.findField("ZFHJ");
				var JKJE = form.findField("JKJE");
				var JKHJ = form.findField("JKHJ");
				var YSJE = form.findField("YSJE");
				var TZJE = form.findField("TZJE");
				if (JKJE.getValue() == null || JKJE.getValue().length == 0) {
					if (this.data.YSJE < 0) {
						JKJE.setValue("0.00");
						TZJE.setValue(YSJE.getValue());
					} else {
						JKJE.setValue(YSJE.getValue());
						TZJE.setValue("0.00");
					}
				} else {
					if (parseFloat(JKJE.getValue()) < parseFloat(this.data.YSJE)) {
						JKJE.setValue();
						Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!",
								function() {
									TZJE.setValue();
									JKJE.focus(false, 200);
									this.JKJEChang = false;
								}, this);
						return;
					} else {
						TZJE.setValue(this.round2(JKJE.getValue()
										- this.data.YSJE, 2));
					}
				}
				btns = this.form.buttons
				if (btns) {
					var n = btns.length;
					for (var i = 0; i < n; i++) {
						var btn = btns[i];
						if (btn.cmd == "commit") {
							btn.focus()
							this.JKJEChang = false;
							return;
						}
					}
				}
			},
			JKJECommit : function(f, e) {
				var key = e.getKey()
				if (key == e.ENTER) {
					if(this.JSLX==4){
						if (this.JKJEChang) {
							return;
						}
						this.JKJEChang = true;
						var form = this.form.getForm();
						var ZFHJ = form.findField("ZFHJ");
						var JKJE = form.findField("JKJE");
						var JKHJ = form.findField("JKHJ");
						var YSJE = form.findField("YSJE");
						var TZJE = form.findField("TZJE");
						if (JKJE.getValue() == null || JKJE.getValue().length == 0) {
							JKJE.setValue("0.00");
							TZJE.setValue(YSJE.getValue());
						} else {
							TZJE.setValue(this.round2(JKJE.getValue()
												- this.data.YSJE, 2));
						}
						btns = this.form.buttons
						if (btns) {
							var n = btns.length;
							for (var i = 0; i < n; i++) {
								var btn = btns[i];
								if (btn.cmd == "commit") {
									btn.focus()
									this.JKJEChang = false;
									return;
								}
							}
						}
						return;
					}
					if (this.JKJEChang) {
						return;
					}
					this.JKJEChang = true;
					var form = this.form.getForm();
					var ZFHJ = form.findField("ZFHJ");
					var JKJE = form.findField("JKJE");
					var JKHJ = form.findField("JKHJ");
					var YSJE = form.findField("YSJE");
					var TZJE = form.findField("TZJE");
					if (JKJE.getValue() == null || JKJE.getValue().length == 0) {
						if (this.data.YSJE < 0) {
							JKJE.setValue("0.00");
							TZJE.setValue(YSJE.getValue());
						} else {
							JKJE.setValue(YSJE.getValue());
							TZJE.setValue("0.00");
						}
					} else {
						if (parseFloat(JKJE.getValue()) < parseFloat(this.data.YSJE)) {
							JKJE.setValue();
							Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!",
									function() {
										TZJE.setValue();
										JKJE.focus(false, 200);
										this.JKJEChang = false;
									}, this);
							return;
						} else {
							TZJE.setValue(this.round2(JKJE.getValue()
											- this.data.YSJE, 2));
						}
					}
					btns = this.form.buttons
					if (btns) {
						var n = btns.length;
						for (var i = 0; i < n; i++) {
							var btn = btns[i];
							if (btn.cmd == "commit") {
								btn.focus()
								this.JKJEChang = false;
								return;
							}
						}
					}
				}
			},
			doCommit : function() {
				if(this.JSLX==4){
					this.saving = true
					var body = {};
					body.data = this.data;
					body.MZXX = this.MZXX;
					body.jsData = this.jsData;
					var form = this.form.getForm();
					var JKJE = form.findField("JKJE");
					var TZJE = form.findField("TZJE");
					if (JKJE.getValue() > 9999999.99) {
						Ext.Msg.alert("提示", "交款金额最大不能大于等于1000万!", function() {
									JKJE.setValue()
									TZJE.setValue();
									JKJE.focus(false, 200);
								});
						this.saving = false;
						return;
					}
					var YSJE = form.findField("YSJE");
					if (!JKJE.getValue()) {
						JKJE.setValue("0.00");
						TZJE.setValue(this.round2(-this.data.YSJE, 2));
					} else {
						TZJE.setValue(this.round2(JKJE.getValue()
									- this.data.YSJE, 2));
					}
					var JSLX = form.findField("JSLX").getValue();
					var ZFHJ = form.findField("ZFHJ").getValue();
					var ZYHM = form.findField("ZYHM").getValue();
					var JKHJ = form.findField("JKHJ").getValue();
					this.data.JKJE = JKJE.getValue();
					this.data.TZJE = TZJE.getValue();
					// this.data.WCFS=form.findField("FFFS").getValue();
					this.data.FKFS = form.findField("FFFS").getValue();
					var _ctr = this;
					this.form.el.mask("正在保存数据...", "x-mask-loading")
					var jsbody = {}
					jsbody.JSXX = this.data;
					phis.script.rmi.jsonRequest({
								serviceId : "familySickBedPatientSelectionService",
								serviceAction : "saveSettleAccounts",
								body : jsbody
							}, function(code, msg, json) {
								this.form.el.unmask()
								this.saving = false
								if (code > 300) {
									this.processReturnMsg(code, msg,
											this.saveToServer, [body]);
									return
								}
								MyMessageTip.msg("提示", "结算成功!", true);
								this.fireEvent("settlement", this);
							}, this)
					return;
				}
				this.saving = true
				var body = {};
				body.data = this.data;
				body.MZXX = this.MZXX;
				body.jsData = this.jsData;
				var form = this.form.getForm();
				var JKJE = form.findField("JKJE");
				var TZJE = form.findField("TZJE");
				if (JKJE.getValue() > 9999999.99) {
					Ext.Msg.alert("提示", "交款金额最大不能大于等于1000万!", function() {
								JKJE.setValue()
								TZJE.setValue();
								JKJE.focus(false, 200);
							});
					this.saving = false;
					return;
				}
				var YSJE = form.findField("YSJE");
				if (!JKJE.getValue()) {
					if (this.data.YSJE < 0) {
						JKJE.setValue("0.00");
						TZJE.setValue(this.round2(-this.data.YSJE, 2));
					} else {
						JKJE.setValue(this.data.YSJE);
						TZJE.setValue("0.00");
					}
				} else {
					if (parseFloat(JKJE.getValue()) < parseFloat(this.data.YSJE)) {
						JKJE.setValue("");
						Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!",
								function() {
									TZJE.setValue();
									JKJE.focus(false, 200);
								});
						this.saving = false;
						return;
					} else {
						TZJE.setValue(this.round2(JKJE.getValue()
										- this.data.YSJE, 2));
					}
				}

				var JSLX = form.findField("JSLX").getValue();
				var ZFHJ = form.findField("ZFHJ").getValue();
				var ZYHM = form.findField("ZYHM").getValue();
				var JKHJ = form.findField("JKHJ").getValue();
				if (JSLX == 1 && (ZFHJ * 1 == 0) && JKHJ * 1 == 0) {
					Ext.Msg.alert("提示", "该病人当前自负费用为0且无预交款,不允许进行中途结算！");
				} else {
					this.data.JKJE = JKJE.getValue();
					this.data.TZJE = TZJE.getValue();
					// this.data.WCFS=form.findField("FFFS").getValue();
					this.data.FKFS = form.findField("FFFS").getValue();
					var _ctr = this;
					this.form.el.mask("正在保存数据...", "x-mask-loading")
					if (this.data.YBJSCS) {// 市医保
						var data = this.data.YBJSCS;
						//医保结算代码
						//...
							var jsbody = {};
							jsbody.YBJS = zy_jxss;
							jsbody.JSXX = this.data;
							phis.script.rmi.jsonRequest({
										serviceId : "familySickBedPatientSelectionService",
										serviceAction : "saveSettleAccounts",
										body : jsbody
									}, function(code, msg, json) {
										this.form.el.unmask()
										this.saving = false
										if (code > 300) {
											this.processReturnMsg(code, msg,
													this.saveToServer, [body]);
											var b = {};
											var ret = phis.script.rmi.miniJsonRequestSync({
													serviceId : "medicareService",
													serviceAction : "queryYbZyqxjscs",
													body : b
												});
											if (ret.code > 300) {
											this.processReturnMsg(ret.code,
													ret.msg);
											return ;
										}
										var qxcs=ret.json.body;
											//如果本地保存失败,医保取消结算
											//...
											//取消结算成功,如果费用表有上传标志则将标志打成未上传
											//...
											return;
										}
										this.doPrintFp(this.data.FPHM)
										this.fireEvent("settlement", this);
									}, this)
						
					}  else {
						var jsbody = {}
						jsbody.JSXX = this.data;
						phis.script.rmi.jsonRequest({
									serviceId : "familySickBedPatientSelectionService",
									serviceAction : "saveSettleAccounts",
									body : jsbody
								}, function(code, msg, json) {
									this.form.el.unmask()
									this.saving = false
									if (code > 300) {
										this.processReturnMsg(code, msg,
												this.saveToServer, [body]);
										return
									}
									MyMessageTip.msg("提示", "结算成功!", true);
									this.doPrintFp(this.data.FPHM)
									this.fireEvent("settlement", this);
								}, this)
					}
				}
			},
			doPrintFp : function(fphm) {
				if(!fphm){
					return ;
				}
				this.opener.opener.opener.list.fphm = fphm;
			}
		});