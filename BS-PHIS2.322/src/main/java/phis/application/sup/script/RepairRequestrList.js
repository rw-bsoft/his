$package("phis.application.sup.script")
$import("phis.script.SelectList")
phis.application.sup.script.RepairRequestrList = function(cfg) {
	cfg.mutiSelect = true;
	cfg.mutiSelect = this.mutiSelect = true;
	phis.application.sup.script.RepairRequestrList.superclass.constructor
			.apply(this, [ cfg ])
}
var dat = new Date().format('Y-m-d');
var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
Ext
		.extend(
				phis.application.sup.script.RepairRequestrList,
				phis.script.SelectList,
				{
					initPanel : function(sc) {
						var grid = phis.application.sup.script.RepairRequestrList.superclass.initPanel
								.call(this, sc);
						if (!this.mainApp['phis'].treasuryId) {
							Ext.Msg.alert("提示", "未设置登录库房,请先设置");
							return null;
						}
						if (this.mainApp['phis'].treasuryCsbz == 0) {
							Ext.Msg.alert("提示", "该库房账册未初始化,不能进行业务操作!");
							return null;
						}
						if (this.mainApp.roleType == "group_12") {
							if (this.mainApp['phis'].treasuryEjkf == 0) {
								Ext.MessageBox.alert("提示", "该库房不是二级库房!");
								return;
							}
						} else {
							if (this.mainApp['phis'].treasuryEjkf != 0) {
								Ext.MessageBox.alert("提示", "该库房不是一级库房!");
								return;
							}
						}
						if (this.mainApp['phis'].treasuryPdzt == 1) {
							Ext.MessageBox.alert("提示", "该库房处于盘点状态,不能维修!");
							return;
						}
						this.grid = grid;
						return grid;
					},
					expansion : function(cfg) {
						// 顶部工具栏
						var label = new Ext.form.Label({
							text : "单据日期："
						});
						this.dateField = new Ext.form.DateField({
							name : 'storeDate',
							value : dateFromValue,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
						this.dateFieldEnd = new Ext.form.DateField({
							name : 'storeDate',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
						// 状态
						var filelable = new Ext.form.Label({
							text : "状态："
						})
						this.statusRadio = new Ext.form.RadioGroup({
							height : 20,
							width : 200,
							id : 'wxsqd',
							name : 'wxsqd', // 后台返回的JSON格式，直接赋值
							value : "-1",
							items : [ {
								boxLabel : '新增',
								name : 'wxsqd',
								inputValue : -1
							}, {
								boxLabel : '提交',
								name : 'wxsqd',
								inputValue : 0
							}, {
								boxLabel : '完修',
								name : 'wxsqd',
								inputValue : 2
							}, {
								boxLabel : '验收',
								name : 'wxsqd',
								inputValue : 3
							} ],
							listeners : {
								change : function(group, newValue, oldValue) {
									this.inputValue = newValue.inputValue;
									this.clearSelect();
									dat = this.dateFieldEnd.value;
									dateFromValue = this.dateField.value;
									this.loadData();
								},
								scope : this
							}
						});
						var tbar = cfg.tbar;
						cfg.tbar = [];
						cfg.tbar.push([ label, this.dateField, "至",
								this.dateFieldEnd, '-', filelable,
								this.statusRadio, '-', tbar ]);
					},
					doRefresh : function() {
						this.clear();
						this.clearSelect();
						if (this.dateField) {
							dateFromValue = new Date(this.dateField.getValue())
									.format("Y-m-d");
						}
						if (this.dateFieldEnd) {
							dat = new Date(this.dateFieldEnd.getValue())
									.format("Y-m-d");
						}
						dat = this.dateFieldEnd.value;
						dateFromValue = this.dateField.value;
						this.loadData();
					},
					loadData : function() {
						this.clear();
						this.requestData.serviceId = "phis.repairRequestrService";
						this.requestData.serviceAction = "queryWXDJ";
						var body = {};
						if (this.inputValue != undefined) {
							body["WXZT"] = this.inputValue;
						} else {
							body["WXZT"] = -1;
						}
						body["formDate"] = dateFromValue;
						body["endDate"] = dat;
						this.requestData.cnd = body;
						if (this.store) {
							if (this.disablePagingTbr) {
								this.store.load()
							} else {
								var pt = this.grid.getBottomToolbar()
								if (this.requestData.pageNo == 1) {
									pt.cursor = 0;
								}
								pt.doLoad(pt.cursor)
							}
						}
						this.resetButtons();
					},
					doCommit : function() {
						var records = this.getSelectedRecords();
						if (records.length == 0) {
							MyMessageTip.msg("提示", "未选中任何记录", true);
							return;
						}
						var bodys = [];
						for ( var i = 0; i < records.length; i++) {
							var body = {};
							if (records[i].json.WXZT != -1
									&& records[i].json.WXZT != -9) {
								MyMessageTip.msg("提示", "所选的单据已经提交！", true);
								return;
							}
							body["WXXH"] = records[i].json.WXXH;// 记录序号
							bodys[i] = body;
						}
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "repairRequestrService",
							serviceAction : "commit",
							body : bodys
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.doSave);
							return;
						}
						// 执行完操作 去掉钩钩。
						this.doRefresh();
						MyMessageTip.msg("提示", "提交成功！", true);
					},
					doAcceptance : function() {
						var r = this.getSelectedRecord();
						if (r.length == 0) {
							MyMessageTip.msg("提示", "未选中任何记录", true);
							return;
						}
						if (r.length > 1) {
							MyMessageTip.msg("提示", "每次只能验收一条单据", true);
							return;
						}
						if (r.get("WXZT") < 2) {
							MyMessageTip.msg("提示", "没有修完不能验收!", true);
							return;
						}
						this.repairRequestrYSForm = this.createModule(
								"repairRequestrYSForm", this.refYs);
						this.repairRequestrYSForm.on("save", this.onSave, this);
						this.repairRequestrYSForm.on("refresh", this.doRefresh,
								this);
						this.repairRequestrYSForm.WXZT = r.get("WXZT")
						this.repairRequestrYSForm.WXXH = r.get("WXXH");
						this.repairRequestrYSForm.SYKS = r.get("SYKS");
						this.repairRequestrYSForm.SYKS_text = r
								.get("SYKS_text");
						this.repairRequestrYSForm.JJCD = r.get("JJCD");
						this.repairRequestrYSForm.JJCD_text = r
								.get("JJCD_text");
						this.repairRequestrYSForm.JSCD = r.get("JSCD");
						this.repairRequestrYSForm.JSCD_text = r
								.get("JSCD_text");
						this.repairRequestrYSForm.MYCD = r.get("MYCD");
						this.repairRequestrYSForm.MYCD_text = r
								.get("MYCD_text");
						this.repairRequestrYSForm.KFXH = r.get("KFXH");
						this.repairRequestrYSForm.KFXH_text = r
								.get("KFXH_text");
						this.repairRequestrYSForm.SQGH = r.get("SQGH");
						this.repairRequestrYSForm.SQGH_text = r
								.get("SQGH_text");
						this.repairRequestrYSForm.LXDH = r.get("LXDH");
						this.repairRequestrYSForm.SXRQ = r.get("SXRQ");
						this.repairRequestrYSForm.BZXX = r.get("BZXX");
						this.repairRequestrYSForm.GZMS = r.get("GZMS");
						this.repairRequestrYSForm.initPanel();
						var win = this.repairRequestrYSForm.getWin();
						win.add(this.repairRequestrYSForm.initPanel());
						win.show();
						win.center();
						this.repairRequestrYSForm.doYS();
					},
					doRemove : function() {
						var r = this.getSelectedRecord();
						if (!r) {
							return;
						}
						if (r.length > 1) {
							MyMessageTip.msg("提示", "每次只能删除一条", true);
							return;
						}
						if (r.get("WXZT") != -1 && r.get("WXZT") != -9) {
							MyMessageTip.msg("提示", "该行不符合删除状态", true);
							return;
						}
						var title = r.id;
						if (this.removeByFiled && r.get(this.removeByFiled)) {
							title = r.get(this.removeByFiled);
						}
						Ext.Msg.show({
							title : '确认删除记录',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.mask("在正删除数据...");
									phis.script.rmi.jsonRequest({
										serviceId : "repairRequestrService",
										serviceAction : "remove",
										body : title
									}, function(code, msg, json) {
										this.unmask();
										if (code < 300) {
											this.doRefresh();
											return true;
										} else {
											this.processReturnMsg(code, msg,
													this.doRemove)
										}
									}, this)
								}
							},
							scope : this
						})
					},
					doAdd : function() {
						this.repairRequestrForm = this.createModule(
								"repairRequestrForm", this.refwxfrom);
						this.repairRequestrForm.on("save", this.onSave, this);
						this.repairRequestrForm.on("refresh", this.doRefresh,
								this);
						this.repairRequestrForm.WXXH = "";
						this.repairRequestrForm.SYKS = "";
						this.repairRequestrForm.SYKS_text = "";
						this.repairRequestrForm.JJCD = "";
						this.repairRequestrForm.JJCD_text = "";
						this.repairRequestrForm.KFXH = "";
						this.repairRequestrForm.KFXH_text = "";
						this.repairRequestrForm.SQGH = "";
						this.repairRequestrForm.SQGH_text = "";
						this.repairRequestrForm.LXDH = "";
						this.repairRequestrForm.SXRQ = "";
						this.repairRequestrForm.BZXX = "";
						this.repairRequestrForm.GZMS = "";
						this.repairRequestrForm.initPanel();
						var win = this.repairRequestrForm.getWin();
						win.add(this.repairRequestrForm.initPanel());
						win.show();
						win.center();
						this.repairRequestrForm.doNew();
					},
					doOpen : function() {
						var r = this.getSelectedRecord();
						if (!r) {
							return;
						}
						if (r.get("WXZT") != -1 && r.get("WXZT") != -9) {
							MyMessageTip.msg("提示", "该行不符合修改状态", true);
							return;
						}
						var WXZT = r.get("WXZT");
						if (WXZT == -1) { // 如果是新增状态就能修改 否则只能查看。
							this.repairRequestrForm = this.createModule(
									"repairRequestrForm", this.refwxfrom);
							this.repairRequestrForm.on("save", this.onSave,
									this);
							this.repairRequestrForm.on("refresh",
									this.doRefresh, this);
							var r = this.getSelectedRecord();
							this.repairRequestrForm.WXXH = r.get("WXXH");
							this.repairRequestrForm.SYKS = r.get("SYKS");
							this.repairRequestrForm.SYKS_text = r
									.get("SYKS_text");
							this.repairRequestrForm.JJCD = r.get("JJCD");
							this.repairRequestrForm.JJCD_text = r
									.get("JJCD_text");
							this.repairRequestrForm.KFXH = r.get("KFXH");
							this.repairRequestrForm.KFXH_text = r
									.get("KFXH_text");
							this.repairRequestrForm.SQGH = r.get("SQGH");
							this.repairRequestrForm.SQGH_text = r
									.get("SQGH_text");
							this.repairRequestrForm.LXDH = r.get("LXDH");
							this.repairRequestrForm.SXRQ = r.get("SXRQ");
							this.repairRequestrForm.BZXX = r.get("BZXX");
							this.repairRequestrForm.GZMS = r.get("GZMS");
							this.repairRequestrForm.initPanel();
							var win = this.repairRequestrForm.getWin();
							win.add(this.repairRequestrForm.initPanel());
							win.show();
							win.center();
							this.repairRequestrForm.doUpdate();
						}
					}
				})