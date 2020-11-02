/**
 * 转科处理
 * 
 * @author Liws
 */
$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.WarTransferDeptModule = function(cfg) {
	cfg.modal = true;
	this.exContext = {};
	this.width = 690
	phis.application.war.script.WarTransferDeptModule.superclass.constructor.apply(this,
			[cfg]);

	this.on("winShow", this.onWinShow, this);
	this.on("beforeWinShow", this.beforeWinShow, this);
}

Ext.extend(phis.application.war.script.WarTransferDeptModule,
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
							height : 500,
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 175,
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
							title : "退药医嘱未提交或未确认",
							items : [this
									.getErrorList(this.errorTab.refProsList)]
						})
				tabItems.push({
							id : this.errorTab.refRtnList,
							title : "退费单未确认",
							items : [this
									.getErrorList(this.errorTab.refRtnList)]
						})
				tabItems.push({
							id : this.errorTab.refRxmList,
							title : "项目医嘱未停或未执行",
							items : [this
									.getErrorList(this.errorTab.refRxmList)]
						})
				this.errorTab.tab.add(tabItems);
				this.panel = panel;

				return panel;
			},
			getForm : function() {
				this.module = this.createModule(
						"hospitalPatientDeptInformationForm",
						this.refHospitalPatientDeptInformationForm);
				this.module.opener = this;

				var form = this.module.initPanel();
				this.form = form;
				return form;
			},
			getErrorTab : function() {
				this.errorTab = this.createModule("errorTabs",
						this.refErrorTabs);
				var _ctx = this;
				this.errorTab.onTabChange = function(tabPanel, newTab, curTab) {
					var m = _ctx.midiModules[newTab.id];
					if (m.ZYH != _ctx.zyh) {
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
			canLegalZK : function(type) {
				// 判断是否能转科
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "wardTransferDeptService",
							serviceAction : "queryUnvalidRecord",
							body : {
								zyh : this.zyh,
								type : type
							}
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return false;
				}
				return json.record;
			},
			resetQueryInfo : function(tabId) {
				var m = this.createModule(tabId);
				if (tabId == this.errorTab.refMedsList) {
					m.requestData.cnd = [
							'and',
							['eq', ['$', 'ZYH'], ['i', this.zyh]],
							['ne', ['$', 'YPLX'], ['i', 0]],
							[
									'or',
									['eq', ['$', 'YSBZ'], ['i', 0]],
									['and', ['eq', ['$', 'YSBZ'], ['i', 1]],
											['eq', ['$', 'YSTJ'], ['i', 1]]]],
							[
									'or',
									['eq', ['$', 'LSBZ'], ['i', 0]],
									['and', ['eq', ['$', 'LSBZ'], ['i', 2]],
											['eq', ['$', 'SYBZ'], ['i', 0]]]],
							['or', ['lt', ['$', 'JFBZ'], ['i', 3]],
									['eq', ['$', 'JFBZ'], ['i', 9]]],
							['ne', ['$', 'FYSX'], ['i', 2]]];
				} else if (tabId == this.errorTab.refProsList) {
					m.requestData.cnd = [
							'and',
							['$', 'a.YPXH = b.YPXH'],
							['or', ['eq', ['$', 'a.TJBZ'], ['i', 1]],
									['$', 'a.TYRQ is null']],
							['eq', ['$', 'a.ZYH'], ['i', this.zyh]]];
				} else if (tabId == this.errorTab.refRtnList) {
					m.requestData.cnd = ['and',
							['eq', ['$', 'ZYH'], ['i', this.zyh]],
							['$', 'TFRQ is null']];
				} else if (tabId == this.errorTab.refRxmList) {
					m.requestData.cnd = [
							'and',
							['eq', ['$', 'ZYH'], ['i', this.zyh]],
							['eq', ['$', 'YPLX'], ['i', 0]],
							[
									'or',
									['eq', ['$', 'YSBZ'], ['i', 0]],
									['and', ['eq', ['$', 'YSBZ'], ['i', 1]],
											['eq', ['$', 'YSTJ'], ['i', 1]]]],
							[
									'or',
									['eq', ['$', 'LSBZ'], ['i', 0]],
									['and', ['eq', ['$', 'LSBZ'], ['i', 2]],
											['eq', ['$', 'SYBZ'], ['i', 0]]]],
							['or', ['lt', ['$', 'JFBZ'], ['i', 3]],
									['eq', ['$', 'JFBZ'], ['i', 9]]],
							['ne', ['$', 'FYSX'], ['i', 2]]];
				}
			},
			beforeWinShow : function() {
				var flag = false;
				var btn = this.panel.getTopToolbar().items.item(0);

//				if (this.operate > 0) {
					var legalO = this.canLegalZK(7);// 药品医嘱未停未提交验证
					var legalT = this.canLegalZK(2);// 退药医嘱未提交或未确认验证
					var legalS = this.canLegalZK(3);// 退费单未确认验证
					var legalX = this.canLegalZK(8);// 项目医嘱未停或未执行验证
					var tab = this.errorTab.tab;
					this.mess = "";
					var mess = "";

					var hasErrorList = -1;
					var medTab = tab.getItem(this.errorTab.refMedsList)
					var proTab = tab.getItem(this.errorTab.refProsList)
					var rtnTab = tab.getItem(this.errorTab.refRtnList)
					var rxmTab = tab.getItem(this.errorTab.refRxmList)
					if (legalO.length > 0) {
						mess += "该病人有药品医嘱未停未发药!<br>";
						hasErrorList = 0;
						tab.unhideTabStripItem(medTab)
						this.resetQueryInfo(this.errorTab.refMedsList);
						flag = true;
					} else {
						tab.hideTabStripItem(medTab)
					}
					if (legalT.length > 0) {
						mess += "该病人有退药医嘱未提交或未确认!<br>";
						if (hasErrorList == -1) {
							hasErrorList = 1;
						}
						tab.unhideTabStripItem(proTab)
						this.resetQueryInfo(this.errorTab.refProsList);
						flag = true;
					} else {
						tab.hideTabStripItem(proTab)
					}
					if (legalS.length > 0) {
						mess += "该病人有未确认的退费单!<br>";
						if (hasErrorList == -1) {
							hasErrorList = 2;
						}
						tab.unhideTabStripItem(rtnTab)
						this.resetQueryInfo(this.errorTab.refRtnList);
						flag = true;
					} else {
						tab.hideTabStripItem(rtnTab)
					}
					if (legalX.length > 0) {
						mess += "该病人有项目医嘱未停或未执行!<br>";
						if (hasErrorList == -1) {
							hasErrorList = 3;
						}
						tab.unhideTabStripItem(rxmTab);
						this.resetQueryInfo(this.errorTab.refRxmList);
						flag = true;
					} else {
						tab.hideTabStripItem(rxmTab)
					}
					if (hasErrorList >= 0) {
						tab.show();
						tab.setActiveTab(hasErrorList);
						var curTab = tab.getActiveTab();
						this.createModule(curTab.id).loadData();
						// 不能转科，设置高度为585
						this.win.setHeight(585);
					} else {
						tab.hide();
						this.win.setHeight(265);
					}
					this.mess = mess;
					this.module.initDataId = this.zyh;
					this.module.loadData();
//				}

				if (this.operate < 0) {
					btn.setText("取消转科");
					flag = false;
				} else {
					btn.setText("转科");
				}

				if (btn) {
					if (flag) {
						btn.disable();
					} else {
						btn.enable();
					}
				}
			},
			onWinShow : function() {
				if (this.form) {
					this.module.initDataId = this.zyh;
					this.module.loadData();
				}
				// 根据出院信息判断是否有未满足出院的条件
				if (this.mess.length > 0) {
					Ext.Msg.alert("提示", this.mess.substring(0, this.mess.length
											- 4));
				}
				this.win.center();
			},
			refreshModule : function() {
				this.beforeWinShow();
			},
			doSure : function() {
				var msg = null;
				var alias = this.operate;
				// 更新ZY_BRRY
				var form = this.form.getForm();

				var hhks = form.findField("HHKS").getValue();
				var hhbq = form.findField("HHBQ").getValue();
				var hhys = form.findField("HHYS").getValue();
				var data = {
					"zyh" : this.zyh,
					"zyhm" : this.zyhm,
					"brch" : this.brch,
					"brxm" : this.brxm,
					"brks" : this.brks,
					"brbq" : this.brbq,
					"zrys" : this.brys,
					"hhks" : hhks,
					"hhbq" : hhbq,
					"hhys" : form.findField("HHYS").getValue()
				};
				if (alias > 0) {
					if (hhks == null || hhks == '') {
						Ext.Msg.alert("提示", "换后科室不能为空!");
						return;
					} else if (hhbq == '' || hhbq == null) {
						Ext.MessageBox.alert("提示", "换后病区不能为空!");
						return;
					} else if (hhys == '' || hhys == null) {
						Ext.MessageBox.alert("提示", "换后医生不能为空!");
						return;
					} else if (this.brks == hhks) {
						Ext.MessageBox.alert("提示", "同科室不能转科,请使用转床处理!");
						return;

					} else if (this.brks == hhks && this.brbq == hhbq) {
						Ext.MessageBox.alert("提示", "同科室同病区内无需转科!");
						return;

					}
				}

				if (alias < 0) {
					msg = "是否对该记录进行取消转科处理?";
				} else {
					msg = "是否确认对该病人进行转科处理?";
				}
				Ext.Msg.show({
					title : '提示',
					msg : msg,
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							if (alias > 0) {
								// 确认转科
								// var rr = phis.script.rmi.miniJsonRequestSync({
								// serviceId : "wardTransferDeptService",
								// serviceAction : "queryInfo",
								// body : data
								// });
								// if (rr.json.count > 0) {
								// MyMessageTip.msg("提示", "该病人已经在转科中!", true);
								// return;
								// }
								var r = phis.script.rmi.miniJsonRequestSync({
											serviceId : "wardTransferDeptService",
											serviceAction : "saveorupdate",
											body : data
										});
								if (r.code > 300) {
									this.processReturnMsg(r.code, r.msg);
									return
								} else {
									MyMessageTip.msg("提示", "转科成功!", true);
									this.doClose();
									// add by yangl 转为抛出事件
									this.fireEvent("doSave");
									this.opener.loadData();
								}
							} else {
								// 取消转科
								var r = phis.script.rmi.miniJsonRequestSync({
											serviceId : "wardTransferDeptService",
											serviceAction : "updateUndo",
											body : data
										});
								if (r.code > 300) {
									this.processReturnMsg(r.code, r.msg);
									return
								} else {
									var val = r.json.body;
									if (val == 1)
										MyMessageTip.msg("提示",
												"该病人对方科室已经确认，不能取消转科!", true);
									else {
										MyMessageTip.msg("提示", "取消转科成功!", true);
									}
									this.doClose();
									// add by yangl 转为抛出事件
									this.fireEvent("doSave");
									this.opener.loadData();
								}
							}

						}
					},
					scope : this
				})
			},
			doClose : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			}
		});