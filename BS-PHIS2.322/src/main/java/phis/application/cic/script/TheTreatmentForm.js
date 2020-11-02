$package("phis.application.cic.script")

/**
 * 员工维护form
 * 
 * @param {}
 *            cfg
 */

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.cic.script.TheTreatmentForm = function(cfg) {
	cfg.modal = true;
	cfg.colCount = 2;
	cfg.width = 700;
	cfg.showButtonOnTop = false;
	/* 注释拱墅区版本中有关医保的代码Ext.apply(this,com.bsoft.phis.yb.YbUtil); */
	phis.application.cic.script.TheTreatmentForm.superclass.constructor.apply(
			this, [ cfg ]);
			this.on("save",this.onSave,this);
}
Ext
		.extend(
				phis.application.cic.script.TheTreatmentForm,
				phis.script.TableForm,
				{
					initPanel : function(sc) {
						if (this.form) {
							if (!this.isCombined) {
								this.addPanelToWin();
							}
							return this.form;
						}
						var schema = sc
						if (!schema) {
							var re = util.schema.loadSync(this.entryName)
							if (re.code == 200) {
								schema = re.schema;
							} else {
								this.processReturnMsg(re.code, re.msg,
										this.initPanel)
								return;
							}
						}

						var ac = util.Accredit;
						var defaultWidth = this.fldDefaultWidth || 80
						var items = schema.items
						var colCount = this.colCount;
						var table = {
							labelAlign : "right",
							labelWidth : this.labelWidth || 50,
							iconCls : 'bogus',
							border : false,
							items : []
						}

						var g = new Ext.form.FieldSet({
							title : '此次就诊',
							animCollapse : false,
							defaultType : 'textfield',
							// autoWidth : true,
							// autoHeight : true,
							layout : 'tableform',
							layoutConfig : {
								columns : colCount,
								tableAttrs : {
									border : 0,
									cellpadding : '2',
									cellspacing : "2"
								}
							}
						})
						var size = items.length
						for ( var i = 0; i < size; i++) {
							var it = items[i]
							if ((it.display == 0 || it.display == 1)
									|| !ac.canRead(it.acValue)) {
								continue;
							}
							var f = this.createField(it)
							f.labelSeparator = ":"
							f.index = i;
							f.anchor = it.anchor || "95%"
							delete f.width

							f.colspan = parseInt(it.colspan)
							f.rowspan = parseInt(it.rowspan)

							if (!this.fireEvent("addfield", f, it)) {
								continue;
							}
							g.add(f)
						}
						table.items.push(g);
						var btn = {
							accessKey : 112,
							text : "保存(F1)",
							iconCls : "save",
							scale : "small",
							// ** add by yzh **
							handler : this.doSaveFZXX,
							enableToggle : false,
							scope : this
						}
						var jzxh2 = this.exContext.ids.clinicId; // 就诊序号
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "queryBrqxAndJkjy",
							body : {
								JZXH : jzxh2
							}
						});
						var brqx = 1;
						var jkjy = "";
						if (r.code == 200) {
							brqx = r.json.BRQX;
							jkjy = r.json.JKJY;

						}
						// 增加病人去向和健康教育
						var radiogroup = [ {
							xtype : "radio",
							boxLabel : '离院',
							inputValue : 1,
							width : 50,
							cmd : "brqx",
							name : "brqx",
							checked : true
						}, {
							xtype : "radio",
							boxLabel : '留院',
							cmd : "brqx",
							width : 50,
							name : "brqx",
							inputValue : 2
						}, {
							xtype : "radio",
							boxLabel : '住院',
							cmd : "brqx",
							width : 50,
							name : "brqx",
							inputValue : 3
						}, {
							xtype : "radio",
							boxLabel : '死亡',
							width : 50,
							cmd : "brqx",
							name : "brqx",
							inputValue : 4
						} ];
						// alert(Ext.encode(radiogroup.items))
						radiogroup[brqx - 1].checked = true;

/*						var f1 = new Ext.form.Field({
							name : "jkjy",
							labelSeparator : ':',
							value : jkjy

						});
					 var labelText = new Ext.form.Label({
							text : "健康教育:"
						});*/
					  var g5 = new Ext.form.FieldSet({
							title : '去向',
							// labelWidth : 10,
							animCollapse : false,
							defaultType : 'textfield',
							autoWidth : true,
							autoHeight : true,
							layout : 'tableform',
							tbar : [ radiogroup]
						// layoutConfig : {
						// columns : colCount,
						// tableAttrs : {
						// border : 0,
						// cellpadding : '2',
						// cellspacing : "2"
						// }
						// }
						})
						table.items.push(g5);

						var g2 = new Ext.form.FieldSet({
							title : '复诊预约',
							animCollapse : true,
							defaultType : 'textfield',
							collapsed : true,
							titleCollapse : true,
							collapsible : true,
							closable : true,
							checkboxToggle : true,
							hideCollapseTool : true,
							layout : 'tableform',
							layoutConfig : {
								columns : 3,
								tableAttrs : {
									border : 0,
									cellpadding : '2',
									cellspacing : "2"
								}
							},
							// tbar : [btn],
							items : []
						})
						this.g3 = g2;
						var fzyySchema;
						var re = util.schema.loadSync(this.fzyyEntryName);
						if (re.code == 200) {
							fzyySchema = re.schema;
						} else {
							this.processReturnMsg(re.code, re.msg,
									this.initPanel)
							return;
						}
						var fzyyItems = fzyySchema.items;
						for ( var i = 0; i < fzyyItems.length; i++) {
							var it = fzyyItems[i]
							if ((it.display == 0 || it.display == 1)
									|| !ac.canRead(it.acValue)) {
								continue;
							}
							var f = this.createField(it)
							f.labelSeparator = ":"
							f.index = i;
							f.anchor = it.anchor || "95%"
							delete f.width

							f.colspan = parseInt(it.colspan)
							f.rowspan = parseInt(it.rowspan)

							if (!this.fireEvent("addfield", f, it)) {
								continue;
							}
							g2.add(f)
						}
						table.items.push(g2);
						var cfg = {
							buttonAlign : 'center',
							labelAlign : this.labelAlign || "left",
							labelWidth : this.labelWidth || 80,
							frame : true,
							shadow : false,
							border : false,
							collapsible : false,
							autoWidth : true,
							autoHeight : true,
							floating : false
						}

						if (this.isCombined) {
							cfg.frame = true
							cfg.shadow = false
							cfg.width = this.width
							cfg.height = this.height
							// var xy=win.getPosition();
							// win.setPagePosition(xy[0],50);
						} else {
							cfg.autoWidth = true
							cfg.autoHeight = true
						}
						this.initBars(cfg);
						Ext.apply(table, cfg)
						this.form = new Ext.FormPanel(table)
						this.form.on("afterrender", this.onReady, this)
						this.schema = schema;
						this.setKeyReadOnly(true)
						if (!this.isCombined) {
							this.addPanelToWin();
						}
						return this.form
					},
					onReady : function() {
						phis.application.cic.script.TheTreatmentForm.superclass.onReady
								.call(this);
						if (this.exContext.systemParams.YSZJS != "1") {
							var settlement = this.form.buttons[0];
							settlement.hide();
						}
						/** 解决在initPanel中无法将预约日期(YYRQ)初始化为服务器时间 */
						var serverDate = Date.getServerDate();
						this.form.getForm().findField("YYRQ").setValue(
								serverDate);
						this.form.getForm().findField("YYRQ").setMinValue(
								serverDate);
						/** end */

						var now = this.form.getForm().findField("YYRQ")
								.getValue();// 拿到预约日期
						var newdate = new Date();
						var newtimems = now.getTime()
								+ (this.YYJGTS * 24 * 60 * 60 * 1000);
						newdate.setTime(newtimems);
						var maxDat = newdate.dateFormat('Y-m-d');// 根据天数算出最大日期
						var DaysToAdd = this.form.getForm().findField("YYRQ")
								.setMaxValue(maxDat);
						this.form.getForm().findField("YYRQ").setValue(maxDat);
						this.form.getForm().findField("GHFZ").on("check",
								this.onCheck, this);
						this.on("winShow", this.onWinShow, this);
					},
					onCheck : function() {
						if (this.form.getForm().findField("GHFZ").getValue() == false) {
							this.form.getForm().findField("ZBLB").setDisabled(
									true);
						} else {
							this.form.getForm().findField("ZBLB").setDisabled(
									false);
						}
					},
					onWinShow : function() {
						if (this.form.getForm().findField("GHFZ").getValue() == false) {
							this.form.getForm().findField("ZBLB").setDisabled(
									true);
						} else {
							this.form.getForm().findField("ZBLB").setDisabled(
									false);
						}
						var jzxh = this.exContext.ids.clinicId;// 就诊序号
						phis.script.rmi
								.jsonRequest(
										{
											serviceId : "clinicManageService",
											serviceAction : "loadSettlementInfo",
											body : {
												"brid" : this.exContext.ids.brid,
												"clinicId" : this.exContext.ids.clinicId
											}
										},
										function(code, msg, json) {
											if (code < 300) {
												var mzxx = json.MZXX;
												mzxx.BRNAME = this.exContext.empiData.personName;
												mzxx.BRXZ = this.exContext.empiData.BRXZ_text;
												// mzxx.FJFY = 0;
												var measures = json.measures;
												var disposal = json.disposal;
												var WFK = 0;
												if (measures.length == 0
														&& disposal.length == 0) {
													this.details = null;
												} else {
													for ( var i = 0; i < measures.length; i++) {
														measures[i].DJLX = "1";
														measures[i].DJLY = "1";
														WFK += measures[i].HJJE;
													}
													for ( var i = 0; i < disposal.length; i++) {
														disposal[i].DJLX = "2";
														disposal[i].DJLY = "1";
														WFK += disposal[i].HJJE;
													}
													this.details = measures
															.concat(disposal);
												}
												mzxx.WFK = WFK;
												mzxx.HJJE = mzxx.CFJE
														+ mzxx.JCJE;
												mzxx.YFK = mzxx.HJJE - WFK;

												this.initFormData(mzxx);
											} else {
												this
														.processReturnMsg(code,
																msg);
											}
										}, this);

					},
					getReferralAppointmentPanel : function() {
						var panel = new Ext.Panel({
							border : false,
							layout : "fit",
							items : this.getReferralAppointment()
						});
						this.panel = panel;
						return panel;
					},
					expansion : function(cfg) {// 扩展
						cfg.frame = false;
					},
					getReferralAppointment : function() {
						var getReferralAppointment = this.createModule(
								"getReferralAppointment",
								this.refReferralAppointment);
						getReferralAppointment.width = 700;
						getReferralAppointment.height = 70;
						getReferralAppointment.YYJGTS = this.YYJGTS
						getReferralAppointment.exContext = this.exContext;
						return getReferralAppointment.initPanel();
					},
					doSettlement : function() {
						var clinicSettlementModule = this.createModule(
								"clinicSettlementModule",
								this.refSettlementModule);
						clinicSettlementModule.opener = this;
						clinicSettlementModule.on("settlement", this.doSave,
								this);
						var mzxx = {};
						mzxx.BRID = this.exContext.ids.brid;
						mzxx.BRXM = this.exContext.empiData.personName;
						mzxx.BRXZ = this.exContext.empiData.BRXZ;
						mzxx.BRXB = this.exContext.empiData.sexCode;
						var win = clinicSettlementModule.getWin();
						this.SettlementWin = win;
						if (!this.details) {
							MyMessageTip.msg("提示", "没有结算信息或已结算!", true);
							return;
						}
						clinicSettlementModule.setData(this.details, mzxx);
						win.show();
						win.setHeight(420);
					},
					doSave : function() {
						if (this.g3.collapsed == false) {
							// if (confirm('是否保存复诊预约信息?')) {
							this.doSaveFZXX();
							return;
							// }
						}
						this.opener.BRQX = this.form.getForm().getValues()["brqx"];
						this.opener.JKJY = this.form.getForm().getValues()["jkjy"];
						this.opener.clinicFinish(9);
						ymPrompt.doHandler('doClose', true);
					},
					doSaveFZXX : function() {
						var GHFZV = 0;
						if (this.form.getForm().findField("GHFZ").getValue() == true) {
							GHFZV = 1;
						} else {
							GHFZV = 0;
						}
						if (!this.form.getForm().findField("YYRQ").getValue()) {
							MyMessageTip.msg("提示", "预约日期不能为空", true);
							return;
						}

						if (!this.form.getForm().findField("ZBLB").getValue()) {
							MyMessageTip.msg("提示", "值班类别不能为空", true);
							return;
						}
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "verificationReservationInfo",
							op : this.op,
							body : {
								BRID : this.exContext.ids.brid,
								clinicId : this.exContext.ids.clinicId,
								FZRQ : this.form.getForm().findField("YYRQ")
										.getValue().format('Y-m-d'),
								GHFZ : GHFZV,
								KSDM : this.mainApp['phis'].reg_departmentId,
								YSDM : this.mainApp.uid,
								ZBLB : this.form.getForm().findField("ZBLB")
										.getValue()
							}
						});
						if (GHFZV == 1) {
							var yyrq = this.form.getForm().findField("YYRQ")
									.getValue().format('Y-m-d');
							var zblb = this.form.getForm().findField("ZBLB")
									.getValue();
							var dqzblb = 0;
							if (new Date().getHours() < 12) {
								dqzblb = 1;
							} else {
								dqzblb = 2;
							}
							if (yyrq == new Date().format('Y-m-d')
									&& zblb == dqzblb) {
								MyMessageTip.msg("提示", "当天当前班次不能预约!", true);
								return;
							} else if (yyrq == new Date().format('Y-m-d')
									&& zblb == 1 && dqzblb == 2) {
								MyMessageTip.msg("提示", "当天下午不能预约上午的号!", true);
								return;
							}
							if (r.json.yspb) {// 如果医生已经排班
								if (parseInt(r.json.yspb.YYXE) <= parseInt(r.json.yspb.YYRS)
										&& r.json.yspb.YYXE != 0) {
									Ext.Msg.confirm("提示", "当天医生预约人数超限,是否继续?",
											function(btn) {
												if (btn == 'yes') {
													this.publicSave();
												}
												return;
											}, this);
								} else {
									this.publicSave();
								}
							} else {
								Ext.Msg.alert("提示", "当天医生未排班,请排班后再预约!");
								return;
//								if (r.json.kspb) {// 如果科室已经排班,医生未排班
//									if (parseInt(r.json.kspb.YYXE) <= parseInt(r.json.kspb.YYRS)
//											&& r.json.kspb.YYXE != 0) {
//										Ext.Msg.confirm("提示",
//												"当天医生未排班,科室预约人数超限,是否继续?",
//												function(btn) {// 先提示是否删除
//													if (btn == 'yes') {
//														this.publicSave();
//													}
//													return;
//												}, this);
//									} else {
//										Ext.Msg.confirm("提示",
//												"当天本科室已排班,医生未排班，是否继续?",
//												function(btn) {// 先提示是否删除
//													if (btn == 'yes') {
//														this.publicSave();
//													}
//													return;
//												}, this);
//									}
//								} else if (!r.json.kspb) {// 如果科室和医生没排班
//									Ext.Msg.confirm("提示",
//											"当天本科室,本医生都未排班，是否继续?",
//											function(btn) {
//												if (btn == 'yes') {
//													this.publicSave();
//												}
//												return;
//											}, this);
//								}
							}
						} else {
							MyMessageTip.msg("提示", "挂号预约操作成功!", true);
							this.opener.BRQX = this.form.getForm().getValues()["brqx"];
							this.opener.JKJY = this.form.getForm().getValues()["jkjy"];
							this.opener.clinicFinish(9);
						}
					},
					publicSave : function() {
						this.form.el.mask("正在保存数据...", "x-mask-loading")
						phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "saveReservationInfo",
							body : {
								BRID : this.exContext.ids.brid,
								clinicId : this.exContext.ids.clinicId,
								FZRQ : this.form.getForm().findField("YYRQ")
										.getValue().format('Y-m-d'),
								GHRQ : new Date().format('Y-m-d'),
								KSDM : this.mainApp['phis'].reg_departmentId,
								YSDM : this.mainApp.uid,
								GHBZ : 0,
								ZCID : 0,
								YYMM : 0,
								JZXH : 0,
								JGID : this.mainApp['phisApp'].deptId,
								ZBLB : this.form.getForm().findField("ZBLB")
										.getValue(),
								YYLB : 1
							}
						},
								function(code, msg, json) {
									this.form.el.unmask();
									this.opener.BRQX = this.form.getForm()
											.getValues()["brqx"];
									this.opener.JKJY = this.form.getForm()
											.getValues()["jkjy"];
									this.opener.clinicFinish(9);
									if (code > 300) {
										if (code == 701) {
											MyMessageTip.msg("提示", msg, true);
											return false;
										}
										this.processReturnMsg(code, msg);
										return;
									}
									MyMessageTip.msg("提示", "挂号预约操作成功!", true);
								}, this)
					},
					/**
					 * 结算计算
					 * 
					 * @param {}
					 *            jsData
					 * @param {}
					 *            datas
					 */
					callPayment : function(jsData, datas) {
						if (jsData) {
							// 获取发票号码
							this.ybbz = 1;
							var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicChargesProcessingService",
								serviceAction : "queryPayment",
								body : datas,
								jsxx : jsData
							});
						} else {
							// 获取发票号码
							this.ybbz = 0;
							var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicChargesProcessingService",
								serviceAction : "queryPayment",
								body : datas
							});
						}
						if (!r.json.body) {
							// Ext.Msg.alert("提示", "请先维护默认付款方式!");
							return;
						} else {
							return r.json.body;
						}
					},
					getExecJs : function() {
						return "jsPrintSetup.setPrinter('cffp');"
					}
				})