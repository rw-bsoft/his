/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.LeaveHospitalNoticeModule = function(cfg) {
	cfg.modal = true;
	this.exContext = {};
	this.width = 800
	phis.application.war.script.LeaveHospitalNoticeModule.superclass.constructor
			.apply(this, [cfg]);

	this.on("winShow", this.onWinShow, this);
	this.on("beforeWinShow", this.beforeWinShow, this);
}

Ext.extend(phis.application.war.script.LeaveHospitalNoticeModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							height : 550,
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 170,
										items : [this.getForm()]
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : [this.getErrorTab()]
									}]
						});
				var tabItems = [];
				tabItems.push({
							id : this.errorTab.refMedsList,
							title : "药品医嘱未停未发药",
							items : [this
									.getErrorList(this.errorTab.refMedsList)]
						})
				tabItems.push({
							id : this.errorTab.refProsList,
							title : "项目医嘱未停或未执行",
							items : [this
									.getErrorList(this.errorTab.refProsList)]
						})
				tabItems.push({
							id : this.errorTab.refRtnList,
							title : "退药单未提交或未确认",
							items : [this
									.getErrorList(this.errorTab.refRtnList)]
						})
				this.errorTab.tab.add(tabItems);
				this.panel = panel;
				if (this.opener.openBy == "nurse") {
					if (this.opener.exContext.systemParams.ZYYSQY == "1") {
						this.panel.getTopToolbar().items.item(0).hide()
					}
				}
				return panel;
			},
			getForm : function() {
				this.patientForm = this.createModule("patientForm",
						this.refPatientForm);
				this.patientForm.labelWidth = 60;
				this.patientForm.on("loadData", this.afterLoadData, this);
				return this.patientForm.initPanel();
			},
			getErrorTab : function() {
				this.errorTab = this.createModule("errorTabs",
						this.refErrorTabs);
				var _ctx = this;
				this.errorTab.onTabChange = function(tabPanel, newTab, curTab) {
					var m = _ctx.midiModules[newTab.id];
					if (m.ZYH != _ctx.initDataId) {
						m.loadData();
					}
				}
				return this.errorTab.initPanel();
			},
			getErrorList : function(refId) {
				var module = this.createModule(refId, refId, {
							height : 310
						});
				return module.initPanel();
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref

				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
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
			afterLoadData : function(entry, data) {
				if (data.CYPB >= 1) {
					this.panel.getTopToolbar().items.items[0].setDisabled(true);
					this.panel.getTopToolbar().items.items[1].setDisabled(true);
					this.panel.getTopToolbar().items.items[2]
							.setDisabled(false);
				} else {
					if (!this.mess) {
						this.panel.getTopToolbar().items.items[0]
								.setDisabled(false);
						this.panel.getTopToolbar().items.items[1]
								.setDisabled(false);
					} else {
						this.panel.getTopToolbar().items.items[1]
								.setDisabled(true);
					}
					this.panel.getTopToolbar().items.items[2].setDisabled(true);
				}
				var ryrq = this.patientForm.form.getForm().findField("RYRQ")
						.getValue();
				var cyrq = this.patientForm.form.getForm().findField("CYRQ")
						.getValue();
				var days = this.patientForm.form.getForm().findField("DAYS")
				if (!cyrq) {
					return;
				}
				var mil = Math.abs(Date.parseDate(cyrq.substring(0, 10),
						'Y-m-d')
						- Date.parseDate(ryrq.substring(0, 10), 'Y-m-d'));
				var d = parseInt(mil / 86400000);
				days.setValue(d);
			},
			canLeaveHospital : function() {
				// 判断是否能出院
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "wardPatientManageService",
							serviceAction : "canLeaveHospital",
							body : {
								ZYH : this.initDataId,
								JGID : this.mainApp['phisApp'].deptId
							}
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return false;
				}
				return json.body;
			},
			resetQueryInfo : function(tabId) {
				var m = this.createModule(tabId);
				if (tabId == this.errorTab.refMedsList) {
					m.requestData.cnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'and',
													['eq', ['$', 'LSBZ'],
															['i', 0]],
													['ne', ['$', 'JFBZ'],
															['i', 3]]],
											['eq', ['$', 'ZFBZ'], ['i', 0]]],
									['gt', ['$', 'YPLX'], ['i', 0]]],
							['eq', ['$', 'ZYH'], ['i', this.initDataId]]];
				} else if (tabId == this.errorTab.refProsList) {
					m.requestData.ZYH = this.initDataId;
					m.requestData.cnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'and',
													['eq', ['$', 'LSBZ'],['i', 0]],														
													['ne', ['$', 'JFBZ'],
															['i', 3]]],
											['eq', ['$', 'ZFBZ'], ['i', 0]]],
									['eq', ['$', 'YPLX'], ['i', 0]]],
							['eq', ['$', 'ZYH'], ['i', this.initDataId]]];
				} else if (tabId == this.errorTab.refRtnList) {
					m.requestData.cnd = [
							'and',
							['or', ['eq', ['$', 'a.TJBZ'], ['i', 1]],
									['isNull', ['$', 'a.TYRQ']]],
							['eq', ['$', 'a.ZYH'], ['i', this.initDataId]]];
				}
			},
			beforeWinShow : function() {
				var body = this.canLeaveHospital();
				var tab = this.errorTab.tab;
				this.mess = "";
				var mess = "";
				if (!body.hasLeaveHosProve) {
					// MyMessageTip.msg("提示", "该病人未办理出院证!", true);
					mess += "该病人未办理出院证!<br>";
				}
				var hasErrorList = -1;
				var medTab = tab.getItem(this.errorTab.refMedsList)
				var proTab = tab.getItem(this.errorTab.refProsList)
				var rtnTab = tab.getItem(this.errorTab.refRtnList)
				if (body.hasErrorMeds) {
					mess += "该病人有药品医嘱未停或未发药!<br>";
					hasErrorList = 0;
					tab.unhideTabStripItem(medTab)
					this.resetQueryInfo(this.errorTab.refMedsList);
				} else {
					tab.hideTabStripItem(medTab)
				}
				if (body.hasErrorPros) {
					mess += "该病人有项目医嘱未停或未执行!<br>";
					if (hasErrorList == -1) {
						hasErrorList = 1;
					}
					tab.unhideTabStripItem(proTab)
					this.resetQueryInfo(this.errorTab.refProsList);
				} else {
					tab.hideTabStripItem(proTab)
				}
				if (body.hasErrorRetMed) {
					mess += "该病人有未提交或未确认的退药单!<br>";
					if (hasErrorList == -1) {
						hasErrorList = 2;
					}
					tab.unhideTabStripItem(rtnTab)
					this.resetQueryInfo(this.errorTab.refRtnList);
				} else {
					tab.hideTabStripItem(rtnTab)
				}
				if (hasErrorList >= 0) {
					tab.show();
					tab.setActiveTab(hasErrorList);
					var curTab = tab.getActiveTab();
					this.createModule(curTab.id).loadData();
					// 不能出院，设置高度为585
					this.win.setHeight(585);
				} else {
					tab.hide();
					this.win.setHeight(265);
				}
				this.mess = mess;
				this.patientForm.initDataId = this.initDataId;
				this.patientForm.loadData();
				// var btn = this.panel.getTopToolbar().items.items[1];
				// if (this.mess.length > 0) {
				// btn.setDisabled(true);
				// } else {
				// btn.setDisabled(false);
				// }
			},
			onWinShow : function() {
				// 根据出院信息判断是否有未满足出院的条件
				if (this.mess.length > 0) {
					Ext.Msg.alert("提示", this.mess.substring(0, this.mess.length
											- 4));
				}
				this.win.center();
			},
			refreshModule : function() {
				this.opener.doRefresh();
				this.beforeWinShow();
			},
			doCyzbl : function() {
				var module = this.createModule("cyzglModule",
						this.refCyzglModule);
				if (module) {
					module.initDataId = this.initDataId;
					if (this.cyzWin) {
						this.cyzWin.show();
						return;
					}
					module.on("doSave", this.refreshModule, this)
					this.cyzWin = module.getWin();
					this.cyzWin.add(module.initPanel());
					this.cyzWin.setHeight(400);
					this.cyzWin.show();
				}
			},
			doConfirm : function() {
				// 确认出院
				this.panel.el.mask("数据处理中...");
				phis.script.rmi.jsonRequest({
							serviceId : "wardPatientManageService",
							serviceAction : "saveLeaveHospital",
							body : {
								ZYH : this.initDataId,
								JGID : this.mainApp['phisApp'].deptId
							}
						}, function(code, msg, json) {
							this.panel.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							MyMessageTip.msg("提示", '出院通知确认成功!', true);
							this.patientForm.loadData();
							this.opener.doRefresh();
						}, this);
			},
			doCancel : function() {
				Ext.Msg.confirm("提示", "确认取消出院通知吗?", function(btn) {
							if (btn == "yes") {
								// 取消出院
								this.panel.el.mask("数据处理中...");
								phis.script.rmi.jsonRequest({
											serviceId : "wardPatientManageService",
											serviceAction : "saveCancelLeaveHospital",
											body : {
												ZYH : this.initDataId,
												JGID : this.mainApp['phisApp'].deptId
											}
										}, function(code, msg, json) {
											this.panel.el.unmask();
											if (code >= 300) {
												this
														.processReturnMsg(code,
																msg);
												return;
											}
											MyMessageTip.msg("提示", '出院通知取消成功!',
													true);
											this.patientForm.loadData();
											this.opener.doRefresh();
										}, this);
							}
						}, this);
			},
			doClose : function() {
				this.win.hide();
			}

		});