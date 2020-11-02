/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.war.script");

$import("phis.script.SimpleModule", "util.dictionary.DictionaryLoader");

phis.application.war.script.WardDoctorAdviceQueryModule = function(cfg) {
	cfg.width = "1024";
	cfg.modal = true;
	cfg.listServiceId = "wardPatientManageService";
	this.serviceId = "wardPatientManageService";
	phis.application.war.script.WardDoctorAdviceQueryModule.superclass.constructor
			.apply(this, [ cfg ]);

	this.on("winShow", this.onWinShow, this);
}

Ext
		.extend(
				phis.application.war.script.WardDoctorAdviceQueryModule,
				phis.script.SimpleModule,
				{
					initPanel : function(sc) {
						if (this.panel) {
							return this.panel;
						}
						var checkBoxs = []
						checkBoxs.push({
							boxLabel : '长期',
							name : 'filter',
							id : 'cqbz',
							inputValue : '1',
							xtype : 'checkbox',
							style : 'margin-left:10px;',
							listeners : {
								'check' : this.checkHandler,
								scope : this
							},
							checked : true
						});
						checkBoxs.push({
							boxLabel : '临时',
							name : 'filter',
							id : 'lsbz',
							inputValue : '2',
							xtype : 'checkbox',
							style : 'margin-left:10px;',
							listeners : {
								'check' : this.checkHandler,
								scope : this
							},
							checked : true
						});
						checkBoxs.push({
							boxLabel : '急诊',
							name : 'filter',
							id : 'jzbz',
							inputValue : '5',
							xtype : 'checkbox',
							style : 'margin-left:10px;',
							listeners : {
								'check' : this.checkHandler,
								scope : this
							},
							checked : true
						}), checkBoxs.push({
							boxLabel : '带药',
							name : 'filter',
							id : 'dybz',
							inputValue : '6',
							xtype : 'checkbox',
							style : 'margin-left:10px;',
							listeners : {
								'check' : this.checkHandler,
								scope : this
							},
							checked : true
						})
						checkBoxs.push({
							boxLabel : '药品',
							name : 'filter',
							id : 'ypbz',
							inputValue : '3',
							xtype : 'checkbox',
							style : 'margin-left:40px;',
							listeners : {
								'check' : this.checkHandler,
								scope : this
							},
							checked : true
						})
						checkBoxs.push({
							boxLabel : '项目',
							name : 'filter',
							id : 'xmbz',
							inputValue : '4',
							xtype : 'checkbox',
							style : 'margin-left:10px;',
							listeners : {
								'check' : this.checkHandler,
								scope : this
							},
							checked : true
						})

						var panel = new Ext.Panel({
							frame : true,
							layout : 'border',
							tbar : [ this.createMyButtons(), '->', checkBoxs ],
							items : [ {
								layout : "fit",
								border : false,
								region : 'north',
								height : 80,
								items : this.getForm()
							}, {
								layout : "fit",
								border : false,
								region : 'center',
								items : this.getList()
							} ]
						});
						this.panel = panel;
						this.panel.on("afterrender", this.onReady, this)
						return this.panel;
					},
					createMyButtons : function(level) {
						var actions = this.actions
						var buttons = []
						if (!actions) {
							return buttons
						}
						if (this.butRule) {
							var ac = util.Accredit
							if (ac.canCreate(this.butRule)) {
								this.actions.unshift({
									id : "create",
									name : "新建"
								})
							}
						}
						var f1 = 112
						for ( var i = 0; i < actions.length; i++) {
							var action = actions[i];
							if (action.hide) {
								continue
							}
							level = level || 'one';
							if (action.properties) {
								action.properties.level = action.properties.level
										|| 'one';
							} else {
								action.properties = {};
								action.properties.level = 'one';
							}
							if (action.properties
									&& action.properties.level != level) {
								continue;
							}
							// ** add by yzh **
							var btnFlag;
							if (action.notReadOnly)
								btnFlag = false
							else
								btnFlag = (this.exContext && this.exContext.readOnly) || false

							if (action.properties && action.properties.scale) {
								action.scale = action.properties.scale
							}
							var btn = {
								accessKey : f1 + i,
								text : action.name + "(F" + (i + 1) + ")",
								ref : action.ref,
								target : action.target,
								cmd : action.delegate || action.id,
								iconCls : action.iconCls || action.id,
								enableToggle : (action.toggle == "true"),
								scale : action.scale || "small",
								// ** add by yzh **
								disabled : btnFlag,
								notReadOnly : action.notReadOnly,

								script : action.script,
								handler : this.doAction,
								scope : this
							}
							buttons.push(btn)
						}
						return buttons

					},
					checkHandler : function() {
						this.loadData();
					},
					onReady : function() {
						// if (!this.win) {
						// this.panel.getTopToolbar().items.item(0).hide()
						// }
					},
					// 赋空
					doAssignedEmpty : function() {
						var r = this.list.getSelectedRecord();
						if (r) {
							// if (this.openBy == "nurse" && r.get("YSBZ") == 1)
							// {
							// MyMessageTip.msg("提示", "不能操作医生开出的医嘱信息!", true);
							// return;
							// }
							// if (this.openBy != "nurse"
							// && r.get("YSGH") != this.mainApp.uid) {
							// MyMessageTip.msg("提示", "不能操作其他医生开出的医嘱信息!", true);
							// return;
							// }
							if (!r.get("TZSJ") || r.get("LSYZ")) {// 不存在单停时间
								MyMessageTip
										.msg("提示", "只有停嘱的长期医嘱才能取消停嘱!", true);
								return;
							}
							var isGroupFirst = false;
							var yzzh = r.get("YZZH");
							var count = 0;
							this.list.store.each(function(record) {
								if (record.get("YZZH") == yzzh) {
									count++;
									if (r == record && count == 1) {
										isGroupFirst = true
									}
								}
							})
							if (count > 1 && isGroupFirst) {
								Ext.Msg.confirm("提示", "是否赋空同组的医嘱？", function(
										btn) {
									if (btn == 'yes') {
										this.execEmpty(r, true);
									} else {
										this.execEmpty(r, false);
									}
								}, this);
							} else {
								this.execEmpty(r, false);
							}
						}
					},
					execEmpty : function(r, isGroup) {
						phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "updateAdviceStatus",
							body : r.data,
							isGroup : isGroup
						}, function(code, msg, json) {
							if (code > 200) {
								this.processReturnMsg(code, msg)
								return;
							}
							MyMessageTip.msg("提示", "医嘱取消停嘱成功!", true);
							this.list.loadData();
							this.opener.doRefresh();
						}, this)
					},
					onWinShow : function() {
						this.resetCheckValue();
						this.form.initDataId = this.initDataId;
						this.form.loadData();
						this.loadData();
						this.list.exList.requestData.cnd = [
								'and',
								[ 'eq', [ '$', 'ZYH' ],
										[ 'd', this.initDataId ] ],
								[ 'ne', [ '$', 'YZPB' ], [ 'i', 0 ] ] ]
						this.list.exList.loadData();
					},
					resetCheckValue : function() {
						Ext.getCmp("cqbz").setValue(true);
						Ext.getCmp("lsbz").setValue(true);
						Ext.getCmp("ypbz").setValue(true);
						Ext.getCmp("xmbz").setValue(true);
						Ext.getCmp("jzbz").setValue(true);
						Ext.getCmp("dybz").setValue(true);
					},
					getCheckValue : function() {
						var condition = {};
						condition.cqbz = Ext.get("cqbz").dom.checked;
						condition.lsbz = Ext.get("lsbz").dom.checked;
						condition.ypbz = Ext.get("ypbz").dom.checked;
						condition.xmbz = Ext.get("xmbz").dom.checked;
						condition.jzbz = Ext.get("jzbz").dom.checked;
						condition.dybz = Ext.get("dybz").dom.checked;
						return condition;
					},
					getForm : function() {
						var module = this.createModule("wardDoctorAdviceForm",
								this.refWardDoctorAdviceForm);
						if (module) {
							this.form = module;
							return module.initPanel();
						}
					},
					getList : function() {
						var module = this.createModule("wardDoctorAdviceList",
								this.refWardDoctorAdviceList);
						if (module) {
							module.exContext = this.exContext;
							module.opener = this;
							this.list = module;
							return module.initPanel();
						}
					},
					loadData : function() {
						if (this.form && this.list) {
							if (this.initDataId) {
								var d = this.getCheckValue();
								// if ((!d.cqbz && !d.lsbz) || (!d.ypbz &&
								// !d.xmbz)) {
								// this.list.store.removeAll();
								// return;
								// }
								// var lsbzCnd;
								// if ((d.cqbz && d.lsbz) || (!d.cqbz &&
								// !d.lsbz)) {
								// if ((d.cqbz && d.lsbz)) {
								// lsbzCnd = [
								// 'or',
								// [ 'eq', [ '$', 'LSYZ' ], [ 'i', 0 ] ],
								// [ 'eq', [ '$', 'LSYZ' ], [ 'i', 1 ] ] ];
								// lsbzCnd = "";
								// } else if (d.cqbz) {
								// lsbzCnd = [ 'eq', [ '$', 'LSYZ' ],
								// [ 'i', 0 ] ];
								// } else if (d.lsbz) {
								// lsbzCnd = [ 'eq', [ '$', 'LSYZ' ],
								// [ 'i', 1 ] ];
								// }
								// var yplxCnd;
								// if ((d.ypbz && d.xmbz) || (!d.ypbz &&
								// !d.xmbz)) {
								// if ((d.ypbz && d.xmbz)) {
								// yplxCnd = [ 'ge', [ '$', 'YPLX' ],
								// [ 'i', 0 ] ];
								// } else if (d.xmbz) {
								// yplxCnd = [ 'eq', [ '$', 'YPLX' ],
								// [ 'i', 0 ] ];
								// } else if (d.ypbz) {
								// yplxCnd = [ 'gt', [ '$', 'YPLX' ],
								// [ 'i', 0 ] ];
								// }
								var cnd = [
										'and',
										[ 'eq', [ '$', 'ZYH' ],
												[ 'd', this.initDataId ] ],
										[ 'eq', [ '$', 'YZPB' ], [ 'i', 0 ] ] ];
								this.list.requestData.cnd = cnd;
								this.list.requestData.zyh = this.initDataId;
								this.list.requestData.cqbz = d.cqbz;
								this.list.requestData.lsbz = d.lsbz;
								this.list.requestData.jzbz = d.jzbz;
								this.list.requestData.dybz = d.dybz;
								this.list.requestData.ypbz = d.ypbz;
								this.list.requestData.xmbz = d.xmbz;
								this.list.loadData();
							}
						}
					},
					doClose : function() {
						this.opener.adviceQueryWin.hide();
					}

				});