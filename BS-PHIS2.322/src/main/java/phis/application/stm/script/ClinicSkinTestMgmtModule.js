$package("phis.application.stm.script")

$import("phis.script.SimpleModule")

phis.application.stm.script.ClinicSkinTestMgmtModule = function(cfg) {
	this.serviceId = 'phis.skintestManageService';
	phis.application.stm.script.ClinicSkinTestMgmtModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.stm.script.ClinicSkinTestMgmtModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				this.systemParams = this.loadSystemParams({
							privates : ['PSSJ', 'PSSXJG']
						});
				var panel = new Ext.Panel({
							border : false,
							// frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										border : false,
										region : 'west',
										width : 490,
										items : this.getLeftModule()
									}, {
										layout : "fit",
										border : false,
										region : 'center',
										items : this.getRightModule()
									}]
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this);
				this.panel.on("destroy", this.onClose, this);
				return panel;
			},
			onReady : function() {
				// 快捷键
				var btnAccessKeys = {}
				if (this.panel.getTopToolbar()) {
					var btns = this.panel.getTopToolbar().items;
					if (btns) {
						var n = btns.getCount()
						for (var i = 0; i < n; i++) {
							var btn = btns.item(i)
							var key = btn.accessKey
							if (key) {
								btnAccessKeys[key] = btn
							}
						}
					}
				}
				this.btnAccessKeys = btnAccessKeys
				// 自动刷新功能
				var ctx = this;
				var task = {
					run : function() {
						if (!this.taskPause) {
							var nowTime = new Date().getTime();
							var inputTime = this.leftModule.recordList.currentInputTime;
							if (!inputTime || (nowTime - inputTime > 30 * 1000)) {
								this.leftModule.recordList.loadData();
							}
						}
					},
					interval : (this.systemParams.PSSXJG || 20) * 1000,
					scope : ctx
				}
				var runner = new Ext.util.TaskRunner();
				runner.start(task);
				this.runner = runner;
			},
			onClose : function() {
				// 关闭定时任务
				if (this.runner) {
					this.runner.stopAll();
				}
				if (this.leftModule.doingList.runner) {
					this.leftModule.doingList.runner.stopAll();
				}
			},
			getLeftModule : function() {
				var module = this.createModule("refLeftModule",
						this.refSkinTestRecordModule);
				this.leftModule = module;
				this.leftModule.systemParams = this.systemParams;
				this.leftModule.on('skDblclick', this.loadCfDetail, this)
				return this.leftModule.initPanel();

			},
			getRightModule : function() {
				var module = this.createModule("refRightModule",
						this.refSkinTestPrescriptionModule);
				this.rightModule = module;
				this.rightModule.systemParams = this.systemParams;
				return this.rightModule.initPanel();
			},
			doStart : function() {
				var r = this.leftModule.recordList.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提醒", "请先选择需要开始的皮试记录!", true);
					return;
				}
				// 判断选中的是否是皮试药品
				var ypxx = this.rightModule.list.getSelectedRecord();
				if (!ypxx || ypxx.get("PSPB") != 1) {// 选择的不是皮试药品,则自动找第一个皮试药品
					for (var i = 0; i < this.rightModule.list.store.getCount(); i++) {
						var rd = this.rightModule.list.store.getAt(i);
						if (rd.get("PSPB") == 1) {
							ypxx = rd;
							break;
						}
					}
				}
				if (r.get("CFSB") != ypxx.get("CFSB")) {
					MyMessageTip.msg("提醒", "当前选中的病人处方信息与显示的不一致,已自动刷新,请确认后重试!",
							true);
					this.loadCfDetail(r);
					return;
				}
				if (ypxx.get("PSPB") == 2 && !r.get("FPHM")) {
					MyMessageTip.msg("提醒", "原液皮试药品需先收费才能开始皮试!", true);
					return;
				}
				this.taskPause = true;
				if (ypxx && ypxx.get("PSPB") > 0) {
					Ext.Msg.show({
						title : '请确认',
						msg : '是否开始患者【' + r.data.BRXM + '】的【' + ypxx.data.YPMC
								+ '】皮试操作？',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.YESNO,
						multiline : false,
						fn : function(btn, text) {
							this.taskPause = false;
							if (btn == "yes") {
								phis.script.rmi.jsonRequest({
											serviceId : this.serviceId,
											serviceAction : "saveStartSkinTest",
											body : {
												CFSB : ypxx.get("CFSB"),
												YPXH : ypxx.get("YPXH"),
												JGID : this.mainApp['phisApp'].deptId
											}
										}, function(code, msg, json) {
											if (code > 200) {
												this
														.processReturnMsg(code,
																msg)
												return;
											}
											this.doRefresh();
										}, this)
							}
						},
						scope : this
					});
				}
			},
			doStop : function() {
				var r = this.leftModule.doingList.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提醒", "请先选择需要停止的皮试记录!", true);
					return;
				}
				this.taskPause = true;
				Ext.Msg.show({
							title : '提示',
							msg : '是否确认停止【' + r.data.YPMC + '】的皮试操作？',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								this.taskPause = false;
								if (btn == "yes") {
									phis.script.rmi.jsonRequest({
												serviceId : this.serviceId,
												serviceAction : "saveStopSkinTest",
												body : {
													CFSB : r.get("CFSB"),
													YPXH : r.get("YPBH"),
													JGID : this.mainApp['phisApp'].deptId
												}
											}, function(code, msg, json) {
												if (code > 200) {
													this.processReturnMsg(code,
															msg)
													return;
												}
												this.doRefresh();
											}, this)
								}
							},
							scope : this
						});
			},
			doOver : function() {
				var r = this.leftModule.doingList.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提醒", "请先选择需要结束的皮试记录!", true);
					return;
				}
				var form = this.createModule("refSkinTestResultForm",
						this.refSkinTestResultForm);
				form.on('saveSuccess', this.doRefresh, this)
				if (!this.resultWin) {
					var win = form.getWin();
					win.add(form.initPanel())
					win.setWidth(300);
					this.resultWin = win;
				}
				form.data = r.data;
				this.resultWin.show();
			},
			loadCfDetail : function(r) {
				if (!r || !r.get("CFSB"))
					return;
				this.rightModule.initDataId = r.get("CFSB");
				this.rightModule.loadData();
			},
			doRefresh : function() {
				this.rightModule.doNew()
				this.leftModule.refresh();
			},
			doClose : function() {
				this.opener.closeCurrentTab();
			}
		});