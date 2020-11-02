$package("phis.application.sup.script")
$import("phis.script.SelectList")

phis.application.sup.script.ApplyRegisterList = function(cfg) {
	cfg.mutiSelect = true;
	cfg.mutiSelect = this.mutiSelect = true;
	var dat = new Date().format('Y-m-d');
	var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
	this.dateFromValue = dateFromValue;
	cfg.initCnd = [
			'and',
			[
					'and',
					[
							'and',
							[ 'ge', [ '$', "str(a.SLSJ,'yyyy-mm-dd')" ],
									[ 's', dateFromValue ] ],
							[ 'le', [ '$', "str(a.SLSJ,'yyyy-mm-dd')" ],
									[ 's', dat ] ] ],
					[ 'eq', [ '$', 'a.CKKF' ],
							[ '$', '%user.properties.treasuryId' ] ] ],
			[ 'in', [ '$', 'a.SLZT' ], [ -1, -9 ], 'd' ] ];
	phis.application.sup.script.ApplyRegisterList.superclass.constructor.apply(
			this, [ cfg ])
}
var inputValue = -1;
Ext
		.extend(
				phis.application.sup.script.ApplyRegisterList,
				phis.script.SelectList,
				{
					initPanel : function(sc) {
						if (!this.mainApp['phis'].treasuryId) {
							Ext.Msg.alert("提示", "未设置登录库房,请先设置");
							return null;
						}
						if (this.mainApp['phis'].treasuryCsbz == 0) {
							Ext.Msg.alert("提示", "该库房账册未初始化,不能进行业务操作!");
							return null;
						}
						if (this.mainApp['phis'].treasuryEjkf == 0) {
							Ext.MessageBox.alert("提示", "该库房不是二级库房!");
							return;
						}
						var grid = phis.application.sup.script.ApplyRegisterList.superclass.initPanel
								.call(this, sc);
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
							value : this.dateFromValue,
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
						this.statusRadio = new Ext.form.RadioGroup(
								{
									height : 20,
									width : 150,
									id : 'djztsl',
									name : 'djztsl', // 后台返回的JSON格式，直接赋值
									value : "-1",
									items : [ {
										boxLabel : '新增',
										name : 'djztsl',
										inputValue : -1
									}, {
										boxLabel : '提交',
										name : 'djztsl',
										inputValue : 0
									}, {
										boxLabel : '制单',
										name : 'djztsl',
										inputValue : 1
									} ],
									listeners : {
										change : function(group, newValue,
												oldValue) {
											inputValue = newValue.inputValue;
											this.clearSelect();
											if (newValue.inputValue == -1) {
												this.requestData.cnd = [
														'and',
														[
																'and',
																[
																		'and',
																		[
																				'ge',
																				[
																						'$',
																						"str(a.SLSJ,'yyyy-mm-dd')" ],
																				[
																						's',
																						this.dateField.value ] ],
																		[
																				'le',
																				[
																						'$',
																						"str(a.SLSJ,'yyyy-mm-dd')" ],
																				[
																						's',
																						this.dateFieldEnd.value ] ] ],
																[
																		'eq',
																		[ '$',
																				'a.CKKF' ],
																		[
																				'i',
																				this.mainApp['phis'].treasuryId ] ] ],
														[
																'in',
																[ '$', 'a.SLZT' ],
																[ -1, -9 ], 'd' ] ];
											} else {
												this.requestData.cnd = [
														'and',
														[
																'and',
																[
																		'and',
																		[
																				'ge',
																				[
																						'$',
																						"str(a.SLSJ,'yyyy-mm-dd')" ],
																				[
																						's',
																						this.dateField.value ] ],
																		[
																				'le',
																				[
																						'$',
																						"str(a.SLSJ,'yyyy-mm-dd')" ],
																				[
																						's',
																						this.dateFieldEnd.value ] ] ],
																[
																		'eq',
																		[ '$',
																				'a.CKKF' ],
																		[
																				'i',
																				this.mainApp['phis'].treasuryId ] ] ],
														[
																'eq',
																[ '$', 'a.SLZT' ],
																[
																		'i',
																		newValue.inputValue ] ] ];
											}
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
						var startDate = "";
						var endDate = "";
						if (this.dateField) {
							startDate = new Date(this.dateField.getValue())
									.format("Y-m-d");
						}
						if (this.dateFieldEnd) {
							endDate = new Date(this.dateFieldEnd.getValue())
									.format("Y-m-d");
						}
						if (inputValue == -1) {
							this.requestData.cnd = [
									'and',
									[
											'and',
											[
													'and',
													[
															'ge',
															[ '$',
																	"str(a.SLSJ,'yyyy-mm-dd')" ],
															[ 's', startDate ] ],
													[
															'le',
															[ '$',
																	"str(a.SLSJ,'yyyy-mm-dd')" ],
															[ 's', endDate ] ] ],
											[
													'eq',
													[ '$', 'a.CKKF' ],
													[
															'i',
															this.mainApp['phis'].treasuryId ] ] ],
									[ 'in', [ '$', 'a.SLZT' ], [ -1, -9 ], 'd' ] ];
						} else {
							this.requestData.cnd = [
									'and',
									[
											'and',
											[
													'and',
													[
															'ge',
															[ '$',
																	"str(a.SLSJ,'yyyy-mm-dd')" ],
															[ 's', startDate ] ],
													[
															'le',
															[ '$',
																	"str(a.SLSJ,'yyyy-mm-dd')" ],
															[ 's', endDate ] ] ],
											[
													'eq',
													[ '$', 'a.CKKF' ],
													[
															'i',
															this.mainApp['phis'].treasuryId ] ] ],
									[ 'eq', [ '$', 'a.SLZT' ],
											[ 'i', inputValue ] ] ];
						}
						this.loadData();
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
							if (records[i].json.SLZT != -1
									&& records[i].json.SLZT != -9) {
								MyMessageTip.msg("提示", "所选的单据已经提交！", true);
								return;
							}
							body["JLXH"] = records[i].json.JLXH;// 记录序号
							bodys[i] = body;
						}
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "applyRegisterService",
							serviceAction : "updateSlZT",
							body : bodys
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.doSave);
							return;
						}
						this.clearSelect();
						this.doRefresh();
						MyMessageTip.msg("提示", "提交成功！", true);
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
						if (r.get("SLZT") != -1 && r.get("SLZT") != -9) {
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
										serviceId : "applyRegisterService",
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
					doAdd : function(num) {
						this.applyRegisterForm = this.createModule(
								"applyRegisterForm", this.refAdd);
						this.applyRegisterForm.on("save", this.onSave, this);
						this.applyRegisterForm.on("refresh", this.doRefresh,
								this);
						if (num && num >= 1) {
							this.applyRegisterForm.oper = 'update';
							var r = this.getSelectedRecord();
							this.applyRegisterForm.JLXH = r.get("JLXH");
							this.applyRegisterForm.WZMC = r.get("WZMC");
							this.applyRegisterForm.WZXH = r.get("WZXH");
							this.applyRegisterForm.WZGG = r.get("WZGG");
							this.applyRegisterForm.WZDW = r.get("WZDW");
							this.applyRegisterForm.WZSL = r.get("WZSL");
							this.applyRegisterForm.KFXH1 = r.get("KFXH");
							this.applyRegisterForm.SLSJ = r.get("SLSJ");
							this.applyRegisterForm.BZXX = r.get("BZXX");
							this.applyRegisterForm.SLKS = r.get("SLKS");
							this.applyRegisterForm.SLKS_text = r
									.get("SLKS_text");
							this.applyRegisterForm.num = num;

							var WZKF = this.doQueryWZKF(r.get("WZXH"));
							if (WZKF) {
								this.applyRegisterForm.KFXH = WZKF;
							}
						} else if (inputValue > -1) {
							return;
						} else {
							this.applyRegisterForm.oper = 'create';
//							var KS = this.doQueryKS();
//							if (KS) {
//								this.applyRegisterForm.KSDM = KS.KSDM;
//								this.applyRegisterForm.KSMC = KS.KSMC;
//							}
						}
						// this.applyRegisterForm.initPanel();
						var win = this.applyRegisterForm.getWin();
						win.add(this.applyRegisterForm.initPanel());
						win.show();
						win.center();
						this.applyRegisterForm.doNew();
					},
					doOpen : function() {
						var r = this.getSelectedRecord();
						if (!r) {
							return;
						}
						var SLZT = r.get("SLZT");
						if (SLZT == -1) { // 如果是新增状态就能修改 否则只能查看。
							this.doAdd(1);
						} else {
							this.doAdd(2);
						}
					},
					doQueryWZKF : function(WZXH) {
						var body = {};
						body["WZXH"] = WZXH;
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "applyRegisterService",
							serviceAction : "queryWzkf",
							body : body
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.doLoadReport);
						}
						return ret.json.ret;
					},
					doQueryKS : function() {
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "applyRegisterService",
							serviceAction : "queryKs"
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.doLoadReport);
						}
						return ret.json.ret;
					}

				})