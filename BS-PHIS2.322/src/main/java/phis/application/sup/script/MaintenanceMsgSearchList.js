$package("phis.application.sup.script")
$import("phis.script.SimpleList")
phis.application.sup.script.MaintenanceMsgSearchList = function(cfg) {
	var dat = new Date().format('Y-m-d');
	var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
	this.dateFromValue = dateFromValue;
	cfg.cnds = [
			'and',
			[
					'and',
					[
							'and',
							[ 'ge', [ '$', "str(a.SXRQ,'yyyy-mm-dd')" ],
									[ 's', dateFromValue ] ],
							[ 'le', [ '$', "str(a.SXRQ,'yyyy-mm-dd')" ],
									[ 's', dat ] ] ],
					[ 'eq', [ '$', 'a.KFXH' ],
							[ '$', '%user.properties.treasuryId' ] ] ],
			[ 'in', [ '$', 'a.WXZT' ], [ 0, 1 ], 'd' ] ];
	phis.application.sup.script.MaintenanceMsgSearchList.superclass.constructor
			.apply(this, [ cfg ])
}
var inputValueMsg = 1;
Ext
		.extend(
				phis.application.sup.script.MaintenanceMsgSearchList,
				phis.script.SimpleList,
				{
					init : function() {
						this.addEvents({
							"gridInit" : true,
							"beforeLoadData" : true,
							"loadData" : true,
							"loadSchema" : true
						})
						this.requestData = {
							serviceId : this.listServiceId,
							schema : this.entryName,
							ksType : 1,
							method : 'execute',
							dicValue : this.dicValue,
							cnd : this.cnds,
							pageSize : this.pageSize > 0 ? this.pageSize : 0,
							pageNo : 1
						}
						if (this.serverParams) {
							Ext.apply(this.requestData, this.serverParams)
						}
						if (this.autoLoadSchema) {
							this.getSchema();
						}
					},
					expansion : function(cfg) {
						// 顶部工具栏
						var label = new Ext.form.Label({
							text : "送修时间："
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
							text : "查询方式："
						})
						this.statusRadio = new Ext.form.RadioGroup(
								{
									height : 20,
									width : 150,
									name : 'sxzt1', // 后台返回的JSON格式，直接赋值
									value : "1",
									items : [ {
										boxLabel : '待修/在修',
										name : 'sxzt1',
										inputValue : 1
									}, {
										boxLabel : '完修',
										name : 'sxzt1',
										inputValue : 2
									} ],
									listeners : {
										change : function(group, newValue,
												oldValue) {
											inputValueMsg = newValue.inputValue;
											if (newValue.inputValue == 1) {
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
																						"str(a.SXRQ,'yyyy-mm-dd')" ],
																				[
																						's',
																						this.dateField.value ] ],
																		[
																				'le',
																				[
																						'$',
																						"str(a.SXRQ,'yyyy-mm-dd')" ],
																				[
																						's',
																						this.dateFieldEnd.value ] ] ],
																[
																		'eq',
																		[ '$',
																				'a.KFXH' ],
																		[
																				'i',
																				this.mainApp['phis'].treasuryId ] ] ],
														[
																'in',
																[ '$', 'a.WXZT' ],
																[ 0, 1 ], 'd' ] ];
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
																						"str(a.SXRQ,'yyyy-mm-dd')" ],
																				[
																						's',
																						this.dateField.value ] ],
																		[
																				'le',
																				[
																						'$',
																						"str(a.SXRQ,'yyyy-mm-dd')" ],
																				[
																						's',
																						this.dateFieldEnd.value ] ] ],
																[
																		'eq',
																		[ '$',
																				'a.KFXH' ],
																		[
																				'i',
																				this.mainApp['phis'].treasuryId ] ] ],
														[
																'eq',
																[ '$', 'a.WXZT' ],
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
						var startDate = "";// 开始时间
						var endDate = ""; // 结束时间
						if (this.dateField) {
							startDate = new Date(this.dateField.getValue())
									.format("Y-m-d");
						}
						if (this.dateFieldEnd) {
							endDate = new Date(this.dateFieldEnd.getValue())
									.format("Y-m-d");
						}
						if (inputValueMsg == 1) {
							this.requestData.cnd = [
									'and',
									[
											'and',
											[
													'and',
													[
															'ge',
															[ '$',
																	"str(a.SXRQ,'yyyy-mm-dd')" ],
															[ 's', startDate ] ],
													[
															'le',
															[ '$',
																	"str(a.SXRQ,'yyyy-mm-dd')" ],
															[ 's', endDate ] ] ],
											[
													'eq',
													[ '$', 'a.KFXH' ],
													[
															'i',
															this.mainApp['phis'].treasuryId ] ] ],
									[ 'in', [ '$', 'a.WXZT' ], [ 0, 1 ], 'd' ] ];
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
																	"str(a.SXRQ,'yyyy-mm-dd')" ],
															[ 's', startDate ] ],
													[
															'le',
															[ '$',
																	"str(a.SXRQ,'yyyy-mm-dd')" ],
															[ 's', endDate ] ] ],
											[
													'eq',
													[ '$', 'a.KFXH' ],
													[
															'i',
															this.mainApp['phis'].treasuryId ] ] ],
									[ 'eq', [ '$', 'a.WXZT' ],
											[ 'i', inputValueMsg ] ] ];
						}
						this.loadData();
					},
					doService : function() {
						var r = this.getSelectedRecord();
						if (r == null) {
							MyMessageTip.msg("提示", "请选择要维修的物资！", true);
							return;
						}
						if (r.data.WXZT > 1) {
							MyMessageTip.msg("提示", "该物资已经完修！", true);
							return;
						}
						this.serviceForm = this.createModule("serviceForm",
								"phis.application.sup.SUP/SUP/SUP510103");
						this.serviceForm.on("save", this.onSave, this);
						this.serviceForm.on("refresh", this.doRefresh, this);
						this.serviceForm.WXXH = r.get("WXXH");
						this.serviceForm.WZXH = r.get("WZXH");
						this.serviceForm.look = 0;
						var win = this.serviceForm.getWin();
						win.add(this.serviceForm.initPanel());
						win.show();
						if (!win.hidden) {
							this.serviceForm.tab.getTopToolbar().getComponent(
									'save').show()
							this.serviceForm.loadData();

						}
					},
					doBack : function() {
						var r = this.getSelectedRecord();
						if (r == null) {
							MyMessageTip.msg("提示", "请选择要退回的物资！", true);
							return;
						}
						if (r.data.WXZT != 0) {
							MyMessageTip.msg("提示", "该物资在维修或者待修,已不能退回！", true);
							return;
						}
						phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "auditWXGLform",
							body : r.data.WXXH,
							op : -1
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							MyMessageTip.msg("提示", "退回成功！", true);
							this.doRefresh();
						}, this);

					},
					doView : function() {
						if (inputValueMsg == 1) {
							var r = this.getSelectedRecord();
							if (!r) {
								return;
							}
							this.repairRequestrFormView = this.createModule(
									"repairRequestrFormView",
									"phis.application.sup.SUP/SUP/SUP5004");
							this.repairRequestrFormView.on("save", this.onSave,
									this);
							this.repairRequestrFormView.on("refresh",
									this.doRefresh, this);
							var r = this.getSelectedRecord();
							this.repairRequestrFormView.WXZT = 3;
							this.repairRequestrFormView.WXXH = r.get("WXXH");
							this.repairRequestrFormView.KFXH = r.get("KFXH");
							this.repairRequestrFormView.KFXH_text = r
									.get("KFXH_text");
							this.repairRequestrFormView.SYKS = r.get("SYKS");
							this.repairRequestrFormView.SYKS_text = r
									.get("SYKS_text");
							this.repairRequestrFormView.JJCD = r.get("JJCD");
							this.repairRequestrFormView.JJCD_text = r
									.get("JJCD_text");
							this.repairRequestrFormView.WXDW = r.get("WXDW");
							this.repairRequestrFormView.WXDW_text = r
									.get("WXDW_text");
							this.repairRequestrFormView.SQGH = r.get("SQGH");
							this.repairRequestrFormView.SQGH_text = r
									.get("SQGH_text");
							this.repairRequestrFormView.LXDH = r.get("LXDH");
							this.repairRequestrFormView.SXRQ = r.get("SXRQ");
							this.repairRequestrFormView.BZXX = r.get("BZXX");
							this.repairRequestrFormView.GZMS = r.get("GZMS");
							this.repairRequestrFormView.initPanel();
							var win = this.repairRequestrFormView.getWin();
							win.add(this.repairRequestrFormView.initPanel());
							win.show();
							win.center();
							this.repairRequestrFormView.doYS();
						} else {
							this.doLook();
						}
					},
					doLook : function() {
						var r = this.getSelectedRecord();
						if (r == null) {
							MyMessageTip.msg("提示", "请选择要查看的物资！", true);
							return;
						}
						this.serviceForm = this.createModule("serviceForm",
								"phis.application.sup.SUP/SUP/SUP510103");
						this.serviceForm.on("save", this.onSave, this);
						this.serviceForm.on("refresh", this.doRefresh, this);
						this.serviceForm.WXXH = r.get("WXXH");
						this.serviceForm.WZXH = r.get("WZXH");
						this.serviceForm.look = 1;
						var win = this.serviceForm.getWin();
						win.add(this.serviceForm.initPanel());
						win.show();
						win.center();
						if (!win.hidden) {
							this.serviceForm.tab.getTopToolbar().getComponent(
									'save').hide()
							this.serviceForm.loadData();
						}
					}
				})