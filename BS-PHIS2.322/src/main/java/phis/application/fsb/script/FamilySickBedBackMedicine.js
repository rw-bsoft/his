$package("phis.application.fsb.script");

$import("phis.script.SimpleModule");

phis.application.fsb.script.FamilySickBedBackMedicine = function(cfg) {
	this.exContext = {};
	this.width = 1024;
	this.height = 550;
	phis.application.fsb.script.FamilySickBedBackMedicine.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.fsb.script.FamilySickBedBackMedicine,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "pharmacyManageService",
							serviceAction : "initialization"
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				Ext.apply(this,this.loadSystemParams({"privates":['JCKCGL']}));
				if(this.JCKCGL!=3){
				Ext.Msg.alert("提示", "当前设置药品不在药房退药!");
				return null;}
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '家床退药',
										region : 'west',
										width : 200,
										items : this.getLists()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getUnderModule()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			getLists : function() {
				this.lists = this.createModule("lists", this.refList);
				this.lists.on("recordClick", this.onRecordClick, this);
				return this.lists.initPanel();
			},
			getUnderModule : function() {
				this.module = this.createModule("module", this.refModule);
				this.module.on("checkTab", this.onTabCheck, this);
				return this.module.initPanel();
			},
			// 左单击 右边数据查询
			onRecordClick : function(r) {
				this.module.showRecord(r);
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.panel.getTopToolbar();
				} else {
					btns = this.panel.buttons;
				}

				if (!btns) {
					return;
				}

				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			onTabCheck : function(tag) {
				if (tag == "tycl") {
					this.setButtonsState(["ty"], true);
					this.setButtonsState(["thzx"], false);
					this.setButtonsState(["qt"], false);
				} else {
					this.setButtonsState(["ty"], false);
					this.setButtonsState(["thzx"], true);
					this.setButtonsState(["qt"], true);
				}

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
			// 刷新
			doSx : function() {
				this.lists.loadData();
				this.module.doNew();
			},
			// 退药
			doTy : function() {
				Ext.Msg.show({
							title : "提示",
							msg : "确定要进行退药吗?",
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var r = this.module.getRecords();
									if (!r || r.length == 0) {
										MyMessageTip.msg("提示", "请先选中需要退的药",
												true);
										return;
									}
									this.panel.el.mask("正在保存数据...",
											"x-mask-loading")
									var ret = phis.script.rmi.miniJsonRequestSync({
												serviceId : this.serviceId,
												serviceAction : this.saveActionID,
												body : r
											});
									this.panel.el.unmask();
									if (ret.code > 300) {
										this.processReturnMsg(ret.code,
												ret.msg, this.doAction);
										return;
									}
									MyMessageTip.msg("提示", "退药成功", true);
									//var FYSJ = ret.json.otherRet.FYSJ;
									//var FYBQ = ret.json.otherRet.FYBQ;
									//var JLID = ret.json.otherRet.JLID;
									//this.doIsPrint(FYSJ, FYBQ, JLID);
									this.doSx();
								}
							},
							scope : this
						});
			},
			doIsPrint : function(FYSJ, FYBQ, JLID) { // 打印费用明细
				Ext.Msg.show({
					title : "提示",
					msg : "是否打印退药明细信息?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.doPrint();
						}else {
							return true;
						}
					},
					scope : this
				});

			},
			// 退回中心
			doThzx : function() {
				Ext.Msg.show({
					title : "提示",
					msg : "确定要退回中心吗?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var r = this.module.getRecords();
							if (!r || r.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要退的药", true);
								return;
							}
							this.panel.el.mask("正在保存数据...", "x-mask-loading")
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.saveRefundActionID,
										body : r
									});
							this.panel.el.unmask();
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doAction);
								return;
							}
							MyMessageTip.msg("提示", "退回成功", true);
							this.doSx();
						}
					},
					scope : this
				});
			},
			// 全部退回中心
			doQt : function() {
				Ext.Msg.show({
					title : "提示",
					msg : "确定要全部退回病区吗?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var r = this.lists.getRecords();
							if (!r || r.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要退的记录", true);
								return;
							}
							this.panel.el.mask("正在保存数据...", "x-mask-loading")
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.saveFullRefundActionID,
										body : r
									});
							this.panel.el.unmask();
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doAction);
								return;
							}
							MyMessageTip.msg("提示", "退回成功", true);
							this.doSx();
						}
					},
					scope : this
				});
			},
			onReady : function() {
				this.setButtonsState(["ty"], true);
				this.setButtonsState(["thzx"], false);
				this.setButtonsState(["qt"], false);
			}
		});