/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");

phis.application.ivc.script.ClinicChargesModule = function(cfg) {
	this.width = 1020;
	this.height = 550;
	cfg.modal = this.modal = true;
	phis.application.ivc.script.ClinicChargesModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ivc.script.ClinicChargesModule,
		phis.script.SimpleModule, {
			initPanel : function() {
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
							buttonAlign : 'center',
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										height : 75,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}],
							tbar : new Ext.Toolbar({
										enableOverflow : true,
										items : (this.tbar || []).concat([this
												.createButton()])
									})
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this);
				this.getFphm();
				return panel;
			},
			ctrl_X : function() {
				this.doJs();
			},
			// ctrl_D : function() {
			// MyMessageTip.msg("提示", "收费大页面的ctrl_D", true);
			// },
			onReady : function() {
				// alert(1)
				this.exContext.systemParams = this.loadSystemParams({
							privates : ['YXWGHBRJZ'],
							commons : ['SHIYB', 'SHENGYB']
						});
				if (this.exContext.systemParams.YXWGHBRJZ != 1) {
					this.panel.getTopToolbar().items.item(0).hide();
				}
			},
			// loadSystemParams : function() {
			// alert(11)
			// var res = phis.script.rmi.miniJsonRequestSync({
			// serviceId : "clinicManageService",
			// serviceAction : "loadSystemParams",
			// body : {
			// // 私有参数
			// privates : ['YXWGHBRJZ'],
			// commons : ['SHIYB', 'SHENGYB']
			// }
			// });
			//
			// var code = res.code;
			// var msg = res.msg;
			// if (code >= 300) {
			// alert(msg)
			// this.processReturnMsg(code, msg);
			// return;
			// }
			// if (this.exContext.systemParams) {
			// Ext.apply(this.exContext.systemParams, res.json.body)
			// } else {
			// this.exContext.systemParams = res.json.body;
			// }
			//
			// },
			getForm : function() {
				var module = this.createModule("Form ", this.refForm);
				var formModule = module.initPanel();
				this.formModule = module;
				module.opener = this;
				var form = module.form.getForm()
				var f = form.findField("MZGL")
				if (f) {
					// f.on("change", m.doMZHMChange, m)
					f.un("specialkey", module.onFieldSpecialkey, this)
					f.on("specialkey", function(f, e) {
								var key = e.getKey()
								if (key == e.ENTER) {
									module.doENTER(f)
								}
							}, this)
					f.focus(false, 200);
				}
				return formModule;
			},
			getList : function() {
				var module = this.createModule("List", this.refList);
				module.openby = "MZSF";
				var listModule = module.initPanel();
				this.listModule = module;
				module.opener = this;
				return listModule;
			},
			doFz : function() {
				if (!this.MZXX) {
					MyMessageTip.msg("提示", "请先调入病人信息", true);
					return;
				}
				var dqfphm = this.formModule.form.getForm().findField("FPHM")
						.getValue();
				if (!dqfphm) {
					Ext.Msg.alert("提示", "请先维护发票号码!");
					return;
				}
				Ext.Msg.show({
							title : '对话框',
							msg : '请输入复制的发票号码:',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							prompt : true,
							fn : function(btn, fphm) {
								if (btn == "ok") {
									this.copyFphm(fphm);
								}
							},
							scope : this
						})
			},
			// 新病人
			doNewPerson : function() {
				var m = this.midiModules["newHealthRecordModule"];
				if (!m) {
					$import("phis.application.pix.script.EMPIInfoModule");
					m = new phis.application.pix.script.EMPIInfoModule({
						entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
					m.on("onEmpiReturn", this.newPerson, this);
					this.midiModules["newHealthRecordModule"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			// 新病人
			// doNewPerson : function() {
			// // 新建 判断 1 卡号,2门诊号码
			// var r = phis.script.rmi.miniJsonRequestSync({
			// serviceId : "clinicChargesProcessingService",
			// serviceAction : "checkCardOrMZHM"
			// // cardOrMZHM : data.cardOrMZHM
			// });
			// if (r.code > 300) {
			// this.processReturnMsg(r.code, r.msg,
			// this.onBeforeSave);
			// return;
			// } else {
			// if (!r.json.cardOrMZHM) {
			// Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
			// f.focus(false, 100);
			// return;
			// }
			// }
			// var m = this.midiModules["newHealthRecordModule"];
			// if (!m) {
			// $import("phis.script.pix.EMPIInfoModule");
			// m = new phis.script.pix.EMPIInfoModule({
			// entryName : "MPI_DemographicInfo",
			// title : "个人基本信息查询",
			// height : 450,
			// modal : true,
			// mainApp : this.mainApp
			// });
			// m.on("onEmpiReturn", this.newPerson, this);
			// this.midiModules["newHealthRecordModule"] = m;
			// }
			// var win = m.getWin();
			// win.setPosition(250, 100);
			// win.show();
			// var form = m.midiModules[m.entryName].form.getForm();
			// if (r.json.cardOrMZHM == 1) {// 卡号
			// form.findField("MZHM").setDisabled(true);
			// } else {// 门诊号码
			// form.findField("cardNo").setValue(
			// form.findField("MZHM").getValue());
			// form.findField("personName").focus(true, 200);
			// }
			// },
			newPerson : function(data, ifNew) {
				if (!ifNew)
					return;
				var body = {
					serviceId : "clinicChargesProcessingService",
					serviceAction : "queryPerson"
				}
				body.MZHM = data.MZHM
				body.newPerson = true;
				var r = phis.script.rmi.miniJsonRequestSync(body);
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return;
				}
				MyMessageTip.msg("提示", "病人【" + r.json.body.BRXM + "】建档成功!",
						true);
				// else {
				// if (!r.json.body) {
				// Ext.Msg.alert("提示", "该卡号不存在!");
				// return;
				// }
				// if (!r.json.body.GHGL) {
				// if (r.json.body.chooseGhks) {
				// this.showKsxzWin(r.json.body, ifNew);
				// return;
				// }
				// }
				// }

			},
			// 处置录入
			doCzlr : function() {
				if (!this.MZXX) {
					MyMessageTip.msg("提示", "请先调入病人信息", true);
					return;
				}
				var module = this.midiModules["czList"];
				if (!module) {
					module = this.createModule("czList",
							"phis.application.ivc.IVC/IVC/IVC010106");
					this.midiModules["czList"] = module;
					module.on("doSave", this.doCZSave, this);
					module.on("doRemove", this.doRemove, this);
					module.on("doCflr", this.doCflr, this);
				}
				module.exContext = {};
				module.exContext.ids = {
					empiId : this.MZXX.EMPIID,
					recordId : "",
					clinicId : "0",// 就诊序号
					brid : this.MZXX.BRID,
					djly : 6,
					brxm : this.MZXX.BRXM,
					ghgl : this.MZXX.GHGL
				}
				module.exContext.empiData = {};
				module.exContext.empiData.BRXZ = this.MZXX.BRXZ;
				// module.exContext.pdgdbz = this.MZXX.pdgdbz;// 规定病种
				module.opener = this
				module.openedBy = "clinicStation";
				module.yjxhs = this.getYjxhs();
				var win = module.getWin();
				win.show();
			},
			// 处方录入
			doCflr : function(tabId) {
				if (!this.MZXX) {
					MyMessageTip.msg("提示", "请先调入病人信息", true);
					return;
				}
				var module = this.midiModules["cfList"];

				if (!module) {
					module = this.createModule("cfList",
							"phis.application.ivc.IVC/IVC/IVC010104");
					// module.loadSystemParams();
					this.midiModules["cfList"] = module;
					module.on("doSave", this.doCFSave, this);
					module.on("doRemove", this.doRemove, this);
					module.on("doCzlr", this.doCzlr, this);
					module.loadSystemParam();
				}
				if (module.exContext.systemParams.YS_MZ_FYYF_XY == -1
						|| module.exContext.systemParams.YS_MZ_FYYF_ZY == -1
						|| module.exContext.systemParams.YS_MZ_FYYF_CY == -1
						|| module.exContext.systemParams.YS_MZ_FYYF_XY == ""
						|| module.exContext.systemParams.YS_MZ_FYYF_ZY == ""
						|| module.exContext.systemParams.YS_MZ_FYYF_CY == "") {
					MyMessageTip.msg("错误", "请先设置门诊发药药房!", true);
					return;
				}
				module.exContext.ids = {
					empiId : this.MZXX.EMPIID,
					recordId : "",
					clinicId : "",// 就诊序号
					brid : this.MZXX.BRID,
					brxm : this.MZXX.BRXM
				}
				module.exContext.empiData = {};
				module.exContext.empiData.BRXZ = this.MZXX.BRXZ;

				// module.exContext.pdgdbz = this.MZXX.pdgdbz;// 规定病种
				module.opener = this
				module.openedBy = "clinicStation";
				module.cfsbs = this.getDjxhs('1');
				if (!isNaN(tabId)) {
					module.tabId = tabId;
				} else {
					module.tabId = 0;
				}
				var win = module.getWin();
				win.show();
			},
			doRemove : function(DJXH) {
				for (var i = 0; i < this.djs.length; i++) {
					if (this.djs[i].DJXH == DJXH) {
						this.djs.splice(i, 1);
					}
				}
				this.setCFD(this.djs);
			},
			doCFSave : function(DJXH, YJXHS) {
				var same = false;
				for (var i = 0; i < this.djs.length; i++) {
					if (this.djs[i].DJXH == DJXH) {
						same = true;
					}
				}
				if (!same) {
					var dj = {};
					dj.DJXH = DJXH;
					dj.DJLX = "1";
					this.djs.push(dj);
				}
				if (YJXHS) {
					var same = false;
					for (var i = 0; i < YJXHS.length; i++) {
						for (var j = 0; j < this.djs.length; j++) {
							if (this.djs[j].DJXH == YJXHS[i].YJXH) {
								same = true;
							}
						}
						if (!same) {
							var dj = {};
							dj.DJXH = YJXHS[i].YJXH;
							dj.DJLX = "2";
							this.djs.push(dj);
						}
					}
					this.setCFD(this.djs);
				} else {
					this.setCFD(this.djs);
				}
			},
			doCZSave : function(DJXH) {
				var same = false;
				for (var i = 0; i < DJXH.length; i++) {
					for (var j = 0; j < this.djs.length; j++) {
						if (this.djs[j].DJXH == DJXH[i]) {
							same = true;
						}
					}
					if (!same) {
						var dj = {};
						dj.DJXH = DJXH[i];
						dj.DJLX = "2";
						this.djs.push(dj);
					}
				}
				this.setCFD(this.djs);
			},
			getYjxhs : function() {
				var store = this.listModule.grid.getStore();
				var n = store.getCount()
				var djxhs = ['0']
				var same = false;
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					var data = {};
					if (r.get("DJLY") == 6) {
						for (var j = 0; j < djxhs.length; j++) {
							if (djxhs[j].CFSB == r.get("CFSB")) {
								same = true;
							}
						}
						if (!same) {
							if (r.get("DJLX") == '2') {
								djxhs.push(r.get("CFSB"))
							}
						}
					}
					same = false;
				}
				return djxhs;
			},
			getDjxhs : function(s) {
				var store = this.listModule.grid.getStore();
				var n = store.getCount()
				var djxhs = []
				var same = false;
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					var data = {};
					if (r.get("DJLY") == 6) {
						for (var j = 0; j < djxhs.length; j++) {
							if (djxhs[j].CFSB == r.get("CFSB")) {
								same = true;
							}
						}
						if (!same) {
							if (r.get("DJLX") == s) {
								data.CFSB = r.get("CFSB");
								data.CFLX = r.get("CFLX");
								data.CFHM = r.get("CFHM");
								djxhs.push(data)
							}
						}
					}
					same = false;
				}
				return djxhs;
			},
			doXg : function() {
				var dqfphm = this.formModule.form.getForm().findField("FPHM")
						.getValue();
				if (!dqfphm) {
					Ext.Msg.alert("提示", "请先维护发票号码!");
					return;
				}
				Ext.Msg.show({
							title : '对话框',
							msg : '输入当前发票号:',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							prompt : true,
							value : dqfphm,
							fn : function(btn, fphm) {
								if (btn == "ok") {
									if (!fphm || fphm == dqfphm) {
										return;
									}
									if (fphm.length != dqfphm.length) {
										Ext.Msg.alert("提示",
												"修改后的发票号码与原发票号码长度不相等");
										return;
									}
									if (parseInt(fphm) < parseInt(dqfphm)) {
										Ext.Msg
												.alert("提示",
														"修改后的发票号码不能小于原发票号码");
										return;
									}
									this.updateFphm(fphm);
								}
							},
							scope : this
						})
			},
			// 复制发票号码
			copyFphm : function(fphm) {
				var body = {
					"FPHM" : fphm,
					"MZXX" : this.MZXX
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "saveCopyFphm",
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该发票号码无收费数据!");
						return;
					}
					if (r.json.body.msg) {
						Ext.Msg.alert("提示", r.json.body.msg);
						return;
					}
					var cfsbs = r.json.body;
					this.setCFD(cfsbs);
				}
			},
			// 修改发票号码
			updateFphm : function(fphm) {
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "updateNotesNumber",
							body : fphm
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "修改后的发票号码不能过大!");
						return;
					}
					var fphm = r.json.body;
					var form = this.formModule.form.getForm();
					var FPHM = form.findField("FPHM");
					FPHM.setValue(fphm);
				}
			},
			// 获取发票号码
			getFphm : function() {
				var mzsf = this;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "getNotesNumber"
						});
				if (r.code > 300) {
					Ext.Msg.alert('提示', r.msg, function() {
								mzsf.opener.opener.closeCurrentTab();// 关闭收费模块
							});
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "请先维护发票号码!", function() {
									mzsf.opener.opener.closeCurrentTab();// 关闭收费模块
								});
						return;
					}
					var fphm = r.json.body;
					var form = this.formModule.form.getForm();
					var FPHM = form.findField("FPHM");
					FPHM.setValue(fphm);
				}
			},
			doJs : function(item, e, callBack) {
				var store = this.listModule.grid.getStore();
				var n = store.getCount();
				var data = [];
				var fyyfArry = [];
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					data.push(r.data);
					if (r.get("DJLX") == 1) {// 单据类型为1的表示药品
						fyyfArry.push(r.get("YFSB"));
					}
				}
				// alert(Ext.encode(data))
				if (!this.MZXX) {
					MyMessageTip.msg("提示", "请先调入病人信息", true);
					return;
				}
				if (store.getCount() == 0) {
					Ext.Msg.alert("提示", "总计金额为零,不能结算!");
					return;
				}
				// 判断是否有效的发药窗口
				if (this.QYFYCK !== '0' && !callBack) {
					var ret_fy = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicChargesProcessingService",
								serviceAction : "loadOpenPharmacyWin",
								body : fyyfArry
							});
					if (ret_fy.code > 300) {
						this.processReturnMsg(ret_fy.code, ret_fy.msg);
						return;
					}
					var body = ret_fy.json.body;
					this.QYFYCK = body.QYFYCK;
					if (this.QYFYCK == '1') {// 启用发药窗口
						if (body.warnMsg) {
							Ext.Msg.confirm("提示", body.warnMsg + "是否继续结算？",
									function(btn) {
										if (btn == 'yes') {
											this.doJs(null, null, true)// 回调本方法
										}
									}, this);
							return;
						}
					}
				}
				// 默认医保病人
				var form = this.formModule.form.getForm();
				// 物资
				var wzbody = {};
				wzbody["GHGL"] = this.MZXX.GHGL;
				wzbody["GHKS"] = this.MZXX.GHKS;
				var ret_wz = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configLogisticsInventoryControlService",
							serviceAction : "verificationWPJFBZ",
							body : wzbody
						});
				if (ret_wz.code > 300) {
					this.processReturnMsg(ret_wz.code, ret_wz.msg);
					return;
				}
				if (this.ybkxx && this.ybkxx != null) {// 如果读过卡
					this.ybbhxx = this.MZXX;	//病号信息					
					this.ybcfxx = data;		//处方明细数据								
					this.doYbmzyjs();   //医保预结算	
				} else {
					var ybbrxz = this.getYbbrxz(this.MZXX.BRXZ);
					if (ybbrxz != null
							&& ybbrxz.YBBRXZ == form.findField("BRXZ")
									.getValue()) {// 如果病人性质是市医保 并且没读卡
						// 则不能完成
						MyMessageTip.msg("提示", "医保病人未读卡,请先读卡", true);
						return;
					}
					var module = this.midiModules["jsModule"];
					if (!module) {
						module = this.createModule("jsModule", this.jsModule);
						this.midiModules["jsModule"] = module;
						var sm = module.initPanel();
						module.opener = this
						module.on("settlement", this.doQx, this);
					}
					var win = module.getWin();
					module.setData(data, this.MZXX);
					win.show();
				}

			},
			doQx : function() {
				this.clearYbxx();
				this.formModule.doNew();
				this.formModule.form.getForm().findField("MZGL").enable();
				this.listModule.clear();
				this.getFphm();
				this.MZXX = false;
				this.listModule.doTJXXclear();
				this.opener.cfList.loadData();
			},
			showCFD : function(MZXX) {
				this.MZXX = MZXX;
				var module = this.midiModules["cfdModule"];
				if (!module) {
					module = this.createModule("cfdModule", this.cfdList);
					this.midiModules["cfdModule"] = module;
					module.opener = this
					module.MZXX = MZXX;
					// if (!this.ybxx || this.ybxx == null) {
					// this.ybxx = this.BLLX;
					// }
					// module.YBXX = this.ybxx;
					var sm = module.initPanel();
					var win = module.getWin();
					// module.loadData();
					win.add(sm)
					win.show();
				} else {
					module.MZXX = MZXX;
					var win = module.getWin();
					module.loadData();
					win.show();
				}
			},
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			createButton : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = this.actions;
				var buttons = [];
				if (!actions) {
					return buttons;
				}
				if (this.butRule) {
					var ac = util.Accredit;
					if (ac.canCreate(this.butRule)) {
						this.actions.unshift({
									id : "create",
									name : "新建"
								});
					}
				}
				// var f1 = 112

				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					// btn.accessKey = f1 + i + this.buttonIndex,
					btn.cmd = action.id;
					btn.text = action.name;
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.prop = {};
					Ext.apply(btn.prop, action);
					Ext.apply(btn.prop, action.properties);
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},
			// createButton : function() {
			// if (this.op == 'read') {
			// return [];
			// }
			// var actions = this.actions;
			// var buttons = [];
			// if (!actions) {
			// return buttons;
			// }
			// var f1 = 112;
			// // 检索是否开启社保功能,如没开启则按钮变灰
			// // var ret = phis.script.rmi.miniJsonRequestSync({
			// // serviceId : this.serviceId,
			// // serviceAction : this.queryEnableActionId
			// // });
			// // if (ret.code > 300) {
			// // this.processReturnMsg(ret.code, ret.msg,
			// // this.createButton);
			// // return;
			// // }
			// // var enable = ret.json.enable;
			// // for (var i = 0; i < actions.length; i++) {
			// // var action = actions[i];
			// // var btn = {};
			// // // btn.accessKey = f1 + i;
			// // btn.cmd = action.id;
			// // // if (action.id == "sbk") {
			// // // if (enable == "1") {
			// // // btn.disabled = false;
			// // // } else {
			// // // btn.disabled = true;
			// // // }
			// // // }
			// // btn.text = action.name;
			// // btn.iconCls = action.iconCls || action.id;
			// // btn.script = action.script;
			// // btn.handler = this.doAction;
			// // btn.notReadOnly = action.notReadOnly;
			// // btn.scope = this;
			// // // btn.iconAlign = "top";
			// // buttons.push(btn, '-');
			// // }
			// return buttons;
			// },
			setCFD : function(djs) {
				this.listModule.brxz = this.formModule.form.getForm()
						.findField("BRXZ").getValue();
				// for (var i = 0; i < djs.length; i++) {
				// djs[i].BLLX = this.ybxx.BLLX;
				// }
				this.djs = djs;
				this.listModule.djs = djs;
				this.listModule.refresh();
				this.listModule.doTJXXclear();
			},
			doSPLR : function(djs, data) {
				/**
				 * 2013-09-16 add by gaof 为拱墅区医保项目，增加(需要审批的药品和费用审批编号录入方法) *
				 */
				// console.debug(this.brxz,this.exContext)
				// var body = {};
				// body["BRXZ"] = this.formModule.form.getForm()
				// .findField("BRXZ").getValue();
				// body["DJS"] = djs;
				// body["Ddtails"] = data;
				// body["dylb"] = this.ybxx.DYLB;
				// re = phis.script.rmi.miniJsonRequestSync({
				// serviceId : "clinicChargesProcessingService",
				// serviceAction : "queryIsNeedVerify",
				// body : body
				// });
				// if (re.code > 300) {
				// this.processReturnMsg(re.code, re.msg,
				// this.onBeforeSave);
				// return;
				// } else {
				// if (re.json.isNeedVerify == 1) {
				// // console.debug(re.json.list_dj)
				// this.examineEnteringDetailList = this
				// .createModule(
				// "examineEnteringDetailList",
				// "IVC01010101");
				// this.examineEnteringDetailList.on("winClose",
				// function() {
				// this.formModule.getWin().hide();
				// }, this);
				// this.examineEnteringDetailList.YBXX = this.ybxx;
				// this.examineEnteringDetailList.listModuleoper =
				// this.listModule;
				// var win = this.formModule.getWin();
				// win.add(this.examineEnteringDetailList
				// .initPanel());
				// win.show();
				// win.center();
				// var store = this.examineEnteringDetailList.grid
				// .getStore();
				//
				// store.removeAll();
				// var o = this.examineEnteringDetailList
				// .getStoreFields(this.examineEnteringDetailList.schema.items)
				// var Record = Ext.data.Record.create(o.fields)
				// for ( var i = 0; i < re.json.list_dj.length; i++) {
				// var r = new Record(re.json.list_dj[i]);
				// store.add([ r ])
				// }
				//
				// // store.add(re.json.list_dj);
				// // console.debug(store)
				// } else if (re.json.isNeedVerify == 2) {
				// // console.debug(re.json.list_dj)
				// this.examineEnteringDetailList = this
				// .createModule(
				// "examineEnteringDetailList",
				// "IVC01010101");
				// this.examineEnteringDetailList.on("winClose",
				// function() {
				// this.formModule.getWin().hide();
				// }, this);
				// this.examineEnteringDetailList.YBXX = this.ybxx;
				// this.examineEnteringDetailList.sybsign = 1;
				// var win = this.formModule.getWin();
				// win.add(this.examineEnteringDetailList
				// .initPanel());
				// win.show();
				// win.center();
				// var store = this.examineEnteringDetailList.grid
				// .getStore();
				//
				// store.removeAll();
				// var o = this.examineEnteringDetailList
				// .getStoreFields(this.examineEnteringDetailList.schema.items)
				// var Record = Ext.data.Record.create(o.fields)
				// for ( var i = 0; i < re.json.list_dj.length; i++) {
				// var r = new Record(re.json.list_dj[i]);
				// store.add([ r ])
				// }
				//
				// // store.add(re.json.list_dj);
				// // console.debug(store)
				// }
				// }
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
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
			doZDCR : function() {
				if (!this.MZXX) {
					MyMessageTip.msg("提示", "请先调入病人信息", true);
					return;
				}
				this.MZXX.msg = 'no';
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "saveZDCR",
							body : this.MZXX
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
					return;
				} else {
					if (ret.json.YJXH) {
						var DJXH = [];
						DJXH.push(ret.json.YJXH.YJXH)
						this.doCZSave(DJXH);
					}
					if (ret.json.msg) {
						Ext.Msg.show({
							title : "提示",
							msg : ret.json.msg,
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var YJXH = null;
									if (ret.json.YJXH) {
										YJXH = ret.json.YJXH;
									}
									var r = phis.script.rmi
											.miniJsonRequestSync({
												serviceId : "clinicChargesProcessingService",
												serviceAction : "saveMS_YJ02",
												body : ret.json.body,
												list_FYXH : ret.json.list_FYXH,
												YJXH : YJXH
											});
									if (r.json.YJXH) {
										var DJXH = [];
										DJXH.push(r.json.YJXH.YJXH)
										this.doCZSave(DJXH);
									}
								}
							},
							scope : this
						});
					}
				}
			},			
			doPrintSet : function(){
				this.CreateDataBill();
				LODOP.PRINT_SETUP();
			},
			doPrint : function(){
				this.CreateDataBill1();
				LODOP.PREVIEW();
			},
			CreateDataBill : function() {
				var LODOP=getLodop();  
				LODOP.PRINT_INITA(10,10,762,533,"打印控件功能演示_Lodop功能_发票套打");
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.ADD_PRINT_TEXT(22,62,136,25,"序列号");
				LODOP.ADD_PRINT_TEXT(56,61,138,25,"姓名");
				LODOP.ADD_PRINT_TEXT(91,166,104,25,"就诊");
				LODOP.ADD_PRINT_TEXT(169,14,59,25,"西药");
				LODOP.ADD_PRINT_TEXT(169,72,88,25,"西药金额");
				LODOP.ADD_PRINT_TEXT(169,161,89,25,"检查费");
				LODOP.ADD_PRINT_TEXT(169,249,89,24,"检查金额");
				LODOP.ADD_PRINT_TEXT(169,337,100,25,"治疗费");
				LODOP.ADD_PRINT_TEXT(169,434,83,25,"治疗金额");
				LODOP.ADD_PRINT_TEXT(169,516,100,25,"其他");
				LODOP.ADD_PRINT_TEXT(169,615,114,25,"其他金额");
				LODOP.ADD_PRINT_TEXT(22,564,161,25,"发票号码");
				LODOP.ADD_PRINT_TEXT(49,580,40,20,"年");
				LODOP.ADD_PRINT_TEXT(49,627,40,20,"月");
				LODOP.ADD_PRINT_TEXT(49,676,40,20,"日");
				LODOP.ADD_PRINT_TEXT(96,413,112,25,"GRJF");
				LODOP.ADD_PRINT_TEXT(96,629,100,25,"结算方式");
				LODOP.ADD_PRINT_TEXT(206,13,61,25,"中成药");
				LODOP.ADD_PRINT_TEXT(206,71,93,25,"成药金额");
				LODOP.ADD_PRINT_TEXT(201,517,98,25,"挂号费");
				LODOP.ADD_PRINT_TEXT(201,615,114,25,"挂号金额");
				LODOP.ADD_PRINT_TEXT(241,13,60,25,"中草药");
				LODOP.ADD_PRINT_TEXT(241,71,94,25,"草药金额");
				LODOP.ADD_PRINT_TEXT(237,516,96,25,"一般诊疗费");
				LODOP.ADD_PRINT_TEXT(237,611,121,25,"一般诊疗费金额");
				LODOP.ADD_PRINT_TEXT(324,166,35,25,"十万");
				LODOP.ADD_PRINT_TEXT(325,224,26,25,"万");
				LODOP.ADD_PRINT_TEXT(325,276,25,25,"千");
				LODOP.ADD_PRINT_TEXT(325,325,23,25,"百");
				LODOP.ADD_PRINT_TEXT(325,381,23,25,"十");
				LODOP.ADD_PRINT_TEXT(325,435,23,25,"一");
				LODOP.ADD_PRINT_TEXT(325,489,23,25,"j");
				LODOP.ADD_PRINT_TEXT(325,535,23,25,"f");
				LODOP.ADD_PRINT_TEXT(324,614,115,25,"HJJE");
				LODOP.ADD_PRINT_TEXT(361,383,225,53,"备注");
				LODOP.ADD_PRINT_TEXT(369,613,114,25,"GRZF");
				LODOP.ADD_PRINT_TEXT(484,9,116,25,"机构名称");
				LODOP.ADD_PRINT_TEXT(485,159,107,25,"收费员");

	    	},
	    	CreateDataBill1 : function() {
	    		var LODOP=getLodop();  
				LODOP.PRINT_INITA(10,10,762,533,"打印控件功能演示_Lodop功能_发票套打");
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "printMoth",
						fphm : "700086"
					});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.ADD_PRINT_TEXT(22,62,136,25,ret.json.XLH);
				LODOP.ADD_PRINT_TEXT(70,61,138,25,ret.json.XM);
				LODOP.ADD_PRINT_TEXT(115,166,122,25,ret.json.JZ);
				LODOP.ADD_PRINT_TEXT(169,14,59,25,"西药");
				LODOP.ADD_PRINT_TEXT(169,72,88,25,ret.json.XYJE);
				LODOP.ADD_PRINT_TEXT(169,161,89,25,"检查费");
				LODOP.ADD_PRINT_TEXT(169,249,89,25,ret.json.JCJE);
				LODOP.ADD_PRINT_TEXT(169,337,100,25,"治疗费");
				LODOP.ADD_PRINT_TEXT(169,434,83,25,ret.json.ZLJE);
				LODOP.ADD_PRINT_TEXT(169,516,100,25,"其他");
				LODOP.ADD_PRINT_TEXT(169,615,114,25,ret.json.QTJE);
				LODOP.ADD_PRINT_TEXT(22,570,155,25,ret.json.FPHM);
				LODOP.ADD_PRINT_TEXT(49,565,23,25,ret.json.YYYY);
				LODOP.ADD_PRINT_TEXT(49,631,22,25,ret.json.MM);
				LODOP.ADD_PRINT_TEXT(50,693,17,25,ret.json.DD);
				LODOP.SET_PRINT_STYLEA(0,"FontName","System");
				LODOP.ADD_PRINT_TEXT(113,413,112,25,ret.json.GRJF);
				LODOP.ADD_PRINT_TEXT(110,629,100,25,ret.json.JSFS);
				LODOP.ADD_PRINT_TEXT(206,13,61,20,"中成药");
				LODOP.ADD_PRINT_TEXT(206,71,93,25,ret.json.CYJE);
				LODOP.ADD_PRINT_TEXT(201,517,98,25,"挂号费");
				LODOP.ADD_PRINT_TEXT(201,615,114,25,ret.json.GHJE);
				LODOP.ADD_PRINT_TEXT(244,13,60,25,"中草药");
				LODOP.ADD_PRINT_TEXT(244,71,94,25,ret.json.CYJE);
				LODOP.ADD_PRINT_TEXT(237,516,96,25,"一般诊疗费");
				LODOP.ADD_PRINT_TEXT(237,611,121,25,ret.json.YBZLF);
				LODOP.ADD_PRINT_TEXT(324,166,35,25,ret.json.SW);
				LODOP.ADD_PRINT_TEXT(325,224,26,25,ret.json.W);
				LODOP.ADD_PRINT_TEXT(325,276,25,25,ret.json.Q);
				LODOP.ADD_PRINT_TEXT(325,325,23,25,ret.json.B);
				LODOP.ADD_PRINT_TEXT(325,381,23,25,ret.json.S);
				LODOP.ADD_PRINT_TEXT(325,435,23,25,ret.json.Y);
				LODOP.ADD_PRINT_TEXT(325,489,23,25,ret.json.J);
				LODOP.ADD_PRINT_TEXT(325,535,23,25,ret.json.F);
				LODOP.ADD_PRINT_TEXT(324,614,115,25,ret.json.HJJE);
				LODOP.ADD_PRINT_TEXT(361,383,225,53,ret.json.BZ);
				LODOP.ADD_PRINT_TEXT(369,613,114,25,ret.json.GRZF);
				LODOP.ADD_PRINT_TEXT(484,9,116,25,ret.json.JGMC);
				LODOP.ADD_PRINT_TEXT(485,159,107,25,ret.json.SFY);
				
	    	}


		});