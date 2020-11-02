$package("phis.application.stm.script")

$import("phis.script.SimpleModule")

phis.application.stm.script.ClinicSkinTestCancelModule = function(cfg) {
	this.serviceId = 'phis.skintestManageService';
	phis.application.stm.script.ClinicSkinTestCancelModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.stm.script.ClinicSkinTestCancelModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
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
										split : true,
										region : 'west',
										title : "皮试记录",
										width : 300,
										items : [this.getRecordList()]
									}, {
										title : "处方信息",
										layout : "fit",
										split : true,
										border : false,
										region : 'center',
										items : this.getClinicModule()
									}]
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
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
			},
			getClinicModule : function() {
				var module = this.createModule("refSkinTestCancelModule",
						this.refSkinTestCancelModule);
				this.module = module;
				this.module.systemParams = this.systemParams;
				return this.module.initPanel();

			},
			getRecordList : function() {
				var module = this.createModule("refSkinTestCancelList",
						this.refSkinTestCancelList);
				this.recordList = module;
				this.recordList.opener = this;
				this.recordList.on("skDblclick", function(r) {
							this.module.initDataId = r.get("CFSB");
							this.module.loadData();
						}, this)
				this.recordList.on('lookupRecord', this.lookupRecord, this);
				return this.recordList.initPanel();
			},
			lookupRecord : function(key) {
				// filter store
				var fiterField = isNaN(key) ? "BRXM" : "CFHM";// 是否汉字
				var store = this.recordList.grid.store;
				for (var i = 0; i < store.getCount(); i++) {
					var v = store.getAt(i).get(fiterField).toUpperCase();
					if (v.indexOf(key) >= 0) {
						this.recordList.grid.getSelectionModel().selectRow(i);
						break;
					}
				}
			},
			doConfirm : function() {
				var r = this.recordList.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提醒", "请先选择需要取消的皮试记录!", true);
					return;
				}
				if (r.get("CFSB") != this.module.initDataId) {
					MyMessageTip.msg("提醒", "当前选中的病人处方信息与显示的不一致,已自动刷新,请确认后重试!",
							true);
					this.module.initDataId = r.get("CFSB");
					this.module.loadData();
					return;
				}
				var ypxx = this.module.list.getSelectedRecord();
				if (!ypxx || ypxx.get("PSPB") < 1) {// 选择的不是皮试药品,则自动找第一个皮试药品
					for (var i = 0; i < this.rightModule.list.store.getCount(); i++) {
						var rd = this.module.list.store.getAt(i);
						if (rd.get("PSPB") > 0) {
							ypxx = rd;
							break;
						}
					}
				}
				if (r.get("FPHM") && ypxx.get("PSPB") == 1) {
					MyMessageTip.msg("提醒", "不能取消已收费的皮试记录!", true);
					return;
				}
				if (!ypxx) {
					MyMessageTip.msg("提醒", "未找到有效的皮试记录,请刷新后重试!", true);
					return;
				}
				this.taskPause = true;
				Ext.Msg.show({
					title : '提示',
					msg : '是否确认取消【' + ypxx.data.YPMC + '】的皮试记录？',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						this.taskPause = false;
						if (btn == "yes") {
							phis.script.rmi.jsonRequest({
										serviceId : "phis.skintestManageService",
										serviceAction : "removeSkinTest",
										body : {
											CFSB : ypxx.get("CFSB"),
											YPXH : ypxx.get("YPXH"),
											BRID : this.module.form.data['BRID'],
											PSJG : ypxx.get("PSJG"),
											JGID : this.mainApp['phisApp'].deptId
										}
									}, function(code, msg, json) {
										if (code > 200) {
											this.processReturnMsg(code, msg)
											return;
										}
										this.recordList.loadData();
									}, this)
									
									
									
		// 与公卫业务联动开始，过敏药品添加到个人既往史中
		if (!this.SFQYGWXT) {
			var publicParam = {
				"commons" : ['SFQYGWXT']
			}
			var SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
		}
		// 如果存在公卫系统，并且皮试结果是阳性则保存到公卫个人既往史中
		if (SFQYGWXT=='1'&&this.mainApp.chisActive) {
			var brempiid = this.exContext.record.data.EMPIID;
			var gmywlbs = {
				'1' : '0102',
				'2' : '0103',
				'3' : '0104'
			}
			// 删除本人当天操作的个人既往史中 过敏药物和诊断
			var delRecords = [];
				delRecords.push({
							empiId : brempiid,
							pastHisTypeCode : '01',
							ysid : this.mainApp.uid
						});
			if (delRecords.length > 0) {
				var comreq1 = util.rmi.miniJsonRequestSync({
							serviceId : "chis.CommonService",
							serviceAction : "delPastHistory",
							body : {
								empiId : brempiid,
								record : delRecords
							}
						});
				if (comreq1.code != 200) {
					this.processReturnMsg(comreq1.code, comreq1.msg);
					return;
				} else {
				}
			}
		}
		//						
													
									
						}
					},
					scope : this
				});
			},
			doRefresh : function() {
				this.recordList.loadData();
			},
			doClose : function() {
				this.opener.closeCurrentTab();
			}
		});