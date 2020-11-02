$package("phis.application.reg.script");
// $import("app.biz.BizTableFormView", "app.biz.BizListView",
// "app.modules.common", "app.biz.BizModule");
$import("phis.script.SimpleModule", "util.helper.Helper",
"phis.script.util.FileIoUtil","phis.script.HealthCardReader");

phis.application.reg.script.RegistrationManageModule = function(cfg) {
	this.serviceId = cfg.serviceId;
	this.saveServiceAction = cfg.saveServiceAction;
	this.JZHM = "";
	this.printurl = util.helper.Helper.getUrl();
	// Ext.apply(this, phis.script.common);
	phis.application.reg.script.RegistrationManageModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("shortcutKey", this.shortcutKeyFunc, this);
};

Ext.extend(phis.application.reg.script.RegistrationManageModule,
		phis.script.SimpleModule, {
			keyManageFunc : function(keyCode, keyName) {
				this.formModule.doAction(this.formModule.btnAccessKeys[keyCode]);
				// MyMessageTip.msg("提示", "处方F1"+keyCode+keyName, true);
			},
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : 960,
							height : 480,
							items : [{
										layout : "fit",
										border : false,
										// split : true,
										title : '',
										region : 'north',
										width : 960,
										height : 150,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'west',
										height : 330,
										width : 450,
										items : this.getKSList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										height : 380,
										items : this.getYSList()
									}]
						});
				this.getJZHM();
				// add by yangl 初始化排队信息
				// this.initQueue();
				this.systemParams = this.loadSystemParams({
					"privates" : ['CARDORMZHM']
				})
				panel.on('afterrender', this.copyYSPB, this);
				return panel;
			},
			// initQueue : function() {
			// var r = phis.script.rmi.miniJsonRequestSync({
			// serviceId : "registeredManagementService",
			// serviceAction : "updateQueue"
			// });
			// if (r.code > 300) {
			// this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
			// return;
			// }
			// },
			copyYSPB : function() {
				var now = Date.parseDate(Date.getServerDateTime(),
						'Y-m-d H:i:s');
				var beginDay = now.add(Date.DAY, -now.getDay())
						.format('Y-m-d H:m:s');// 本周第一天（周日）的日期
				var endDay = now.add(Date.DAY, 7 - now.getDay())
						.format('Y-m-d H:m:s');// 下周第一天的日期
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "registrationDoctorPlanService",
							serviceAction : "save_copyYSPB",
							beginDay : beginDay,
							endDay : endDay
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.copyYSPB);
				}
			},
			getJZHM : function() {
				var this_ = this;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "registeredManagementService",
							serviceAction : "updateDoctorNumbers"
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.JZHM) {
						Ext.Msg.alert("提示", "请先维护就诊号码!", function() {
									this_.opener.closeCurrentTab();// 关闭收费模块
								});
						return;
					}
					this.BLJE = parseFloat(r.json.BLJE).toFixed(2);
					var form = this.formModule.form.getForm();
					var JZHM = form.findField("JZHM");
					var GHSJ = form.findField("GHSJ");
					var JZKH = form.findField("JZKH");
					var BLJE = form.findField("BLJE");
					this.ZBLB = r.json.ZBLB;
					JZHM.setValue(r.json.JZHM);
					this.GHSJ = r.json.GHSJ;
					if (!GHSJ.getValue()) {
						GHSJ.setValue(r.json.GHSJ);
					}
					BLJE.setValue("0.00");
					JZKH.focus(false, 200);
					return this.JZHM;
				}
			},
			onReady : function() {
			},
			getForm : function() {
				var module = this.createModule("regform", this.regForm);
				module.opener = this;
				this.formModule = module;
				// var ret = phis.script.rmi.miniJsonRequestSync({
				// serviceId : "medicareService",
				// serviceAction : "queryYbBrxz"
				// });
				// if (ret.code > 300) {
				// this.processReturnMsg(ret.code, ret.msg);
				// }
				// module.ybbrxz = ret.json.body;
				var form = module.initPanel();
				var jzkh = module.form.getForm().findField("JZKH");
				var ksmc = module.form.getForm().findField("KSMC");
				jzkh.focus(false, 200);
				jzkh.un("specialkey", module.onFieldSpecialkey, module);
				ksmc.un("specialkey", module.onFieldSpecialkey, module);
				jzkh.on("specialkey", this.doSpecialkey, this);
				ksmc.on("specialkey", this.doSpecialkey, this);
				module.on("refreshghks", this.doRefreshghks, this);
				module.on("getJTYGH", this.doGetJTYGH, this);
				module.on("ybqr", this.focusPYDM, this);
				return form;
			},
			getKSList : function() {
				var module = this.createModule("kslist", this.regKsList);
				module.opener = this;
				this.KSList = module;
				var list = module.initPanel();
				module.on("departmentChoose", this.departmentChoose, this);
				return list;
			},
			getYSList : function() {
				var module = this.createModule("yslist", this.regYsList);
				module.opener = this;
				this.YSList = module;
				var list = module.initPanel();
				module.on("doctorChoose", this.doctorChoose, this);
				module.on("doctorCancleChoose", this.doctorCancleChoose, this);
				return list;
			},
			doctorChoose : function(record) {
				var form = this.formModule.form.getForm();
				form.findField('JZYS').setValue();
				form.findField('ZJFY').setValue("0.00");
				this.formModule.GHXX.YSDM = "";
				this.formModule.GHXX.ZJFY = 0;
				if (!this.formModule.GHXX.YYBZ) {
					this_ = this;
					if (record.data.GHXE != 0
							&& record.data.GHXE <= (record.data.YGRS + record.data.YYRS)) {
						Ext.MessageBox.confirm("提示", "该医生挂号人数已满，是否继续!",
								function(btn) {
									if (btn == 'yes') {
										this_.YSList.cndField.setValue();
										var ygxm = record.data.PERSONNAME;
										var zjfy = record.data.EXPERTCOST;
										form.findField('JZYS').setValue(ygxm);
										if (this_.zjmz == '1') {
											form.findField('ZJFY')
													.setValue(zjfy);
											this_.formModule.GHXX.ZJFY = zjfy;
										}
										this_.formModule.GHXX.YSDM = record.data.YSDM;
										this_.formModule.doSave();
									} else {
										var view = this_.YSList.grid.getView();
										view.focusRow(0);
									}
								});
						return;
					}
				}
				this.YSList.cndField.setValue();
				var ygxm = record.data.PERSONNAME;
				var zjfy = record.data.EXPERTCOST;
				form.findField('JZYS').setValue(ygxm);
				if (this.zjmz == '1') {
					form.findField('ZJFY').setValue(zjfy);
					this.formModule.GHXX.ZJFY = zjfy;
				}
				this.formModule.GHXX.YSDM = record.data.YSDM;
				this.formModule.doSave();
			},
			doctorCancleChoose : function(record) {
				var lastIndex = this.KSList.grid.getSelectionModel().lastActive;
				this.KSList.selectRow(lastIndex);
			},
			departmentChoose : function(record) {
				var ksmc = record.data.KSMC;
				var ksdm = record.data.KSDM;
				var ghje = record.data.GHF;
				var zlje = record.data.ZLF;
				var ghrq = record.data.GHRQ;
				this.zjmz = record.data.ZJMZ;
				var form = this.formModule.form.getForm();
				if (this.formModule.GHXX) {
					form.findField('KSMC').setValue();
					form.findField('GHJE').setValue("0.00");
					form.findField('ZLJE').setValue("0.00");
					form.findField('ZJFY').setValue("0.00");
					form.findField('JZYS').setValue();
					this.formModule.GHXX.ZJFY = "";
					this.formModule.GHXX.KSDM = "";
					this.formModule.GHXX.GHJE = "";
					this.formModule.GHXX.ZLJE = "";
					this.formModule.GHXX.ZJFY = "";
					this.formModule.GHXX.GHRQ = "";
					this.YSList.BRXX = "";
					if (!this.formModule.GHXX.YYBZ && !this.ghyj) {
						this.formModule.GHXX.YSDM = "";
						this.YSList.YSDM = "";
						// this_ = this;
						if (record.data.GHXE != 0
								&& record.data.GHXE <= (record.data.YGRS + record.data.YYRS)) {
							Ext.MessageBox.confirm("提示", "该科室挂号人数已满，是否继续!",
									function(btn) {
										if (btn == 'yes') {
											this.YSList.KSDM = ksdm;
											this.YSList.requestData.cnd = [
													'eq', ['$', 'a.KSDM'],
													['i', ksdm]];
											this.YSList.loadData();
											if (this.KSList.BRXX) {
												this.KSList.cndField.setValue();
												form.findField('KSMC')
														.setValue(ksmc);
												if (this.BRXX.JTYGH == 1) {
													form.findField('GHJE')
															.setValue("0.00");
													form.findField('ZLJE')
															.setValue("0.00");
												} else {
													form
															.findField('GHJE')
															.setValue(parseFloat(ghje)
																			.toFixed(2));
													form
															.findField('ZLJE')
															.setValue(parseFloat(zlje)
																			.toFixed(2));
												}

												form.findField('ZJFY')
														.setValue("0.00");
												this.BRXX.KSDM = ksdm;
												this.YSList.BRXX = this.BRXX;
												this.formModule.GHXX.KSDM = record.data.KSDM;
												// 是否一天收一次挂号费
												if (this.BRXX.JTYGH == 1) {
													this.formModule.GHXX.GHJE = 0.00;
													this.formModule.GHXX.ZLJE = 0.00;
												} else {
													this.formModule.GHXX.GHJE = parseFloat(ghje)
															.toFixed(2);
													this.formModule.GHXX.ZLJE = parseFloat(zlje)
															.toFixed(2);
												}
												//2019-03-28-门诊挂号增加一般诊疗费的收取
												if(record.data.ZLF != 0 && this.BRXX.JTYGH != 1){
													this.formModule.GHXX.ZLXM = record.data.ZLSFXM;//一般诊疗费项目序号
												}else{
													delete this.formModule.GHXX.ZLXM;
												}
												this.formModule.GHXX.ZJFY = 0;
												this.formModule.GHXX.GHRQ = ghrq;
											}
										} else {
											var view = this.KSList.grid
													.getView();
											view.focusRow(0);
										}
									}, this);
							return;
						}
					}
				}
				this.YSList.KSDM = ksdm;
				if (this.yysign == 2) {
					this.YSList.GHSJ = this.formModule.form.getForm()
							.findField("GHSJ").getValue();
					this.YSList.GHLB = this.formModule.form.getForm()
							.findField("GHLB").getValue();
					this.YSList.requestData.cnd = ['eq', ['$', 'a.KSDM'],
							['i', ksdm]];
					this.YSList.requestData.ghsj = this.formModule.form
							.getForm().findField("GHSJ").getValue();
					this.YSList.requestData.ghlb = this.formModule.form
							.getForm().findField("GHLB").getValue();
				} else {
					this.YSList.GHSJ = "";
					this.YSList.GHLB = "";
					this.YSList.requestData.cnd = ['eq', ['$', 'a.KSDM'],
							['i', ksdm]];
					this.YSList.requestData.ghsj = "";
					this.YSList.requestData.ghlb = "";
				}
				this.YSList.loadData();
				if (this.KSList.BRXX) {
					this.KSList.cndField.setValue();
					form.findField('KSMC').setValue(ksmc);
					if (this.BRXX.JTYGH == 1) {
						form.findField('GHJE').setValue("0.00");
						form.findField('ZLJE').setValue("0.00");
					} else {
						form.findField('GHJE').setValue(parseFloat(ghje)
										.toFixed(2));
						form.findField('ZLJE').setValue(parseFloat(zlje)
										.toFixed(2));
					}
					//农合读卡没有挂号费
                    
					form.findField('ZJFY').setValue("0.00");
					this.BRXX.KSDM = ksdm;
					this.YSList.BRXX = this.BRXX;
					this.formModule.GHXX.KSDM = record.data.KSDM;
					// 是否一天收一次挂号费
					if (this.BRXX.JTYGH == 1) {
						this.formModule.GHXX.GHJE = 0.00;
						this.formModule.GHXX.ZLJE = 0.00;
					} else {
						this.formModule.GHXX.GHJE = parseFloat(ghje).toFixed(2);
						this.formModule.GHXX.ZLJE = parseFloat(zlje).toFixed(2);
					}
					var brxz = form.findField('BRXZ').getValue();
					//2019-03-28-门诊挂号增加一般诊疗费的收取
					if(record.data.ZLF != 0 && this.BRXX.JTYGH != 1){
						this.formModule.GHXX.ZLXM = record.data.ZLSFXM;//一般诊疗费项目序号
					}else{
						delete this.formModule.GHXX.ZLXM;
					}
					if((this.nhdk && this.nhdk=="1") || brxz=="3000" || brxz=="5000"){//新农合置为0
                    	form.findField('GHJE').setValue("0.00");
						form.findField('ZLJE').setValue("0.00");
						this.formModule.GHXX.GHJE = 0.00;
						this.formModule.GHXX.ZLJE = 0.00;
                    }
					this.formModule.GHXX.ZJFY = 0;
					this.formModule.GHXX.GHRQ = ghrq;
				}
			},
			doSpecialkey : function(field, e) {
				// remove by yangl 以下逻辑有问题，去参数不能放到这里 e.stopEvent()
//				if (!Ext.isIE) {
//					e.stopEvent();
//				}
//				var r = phis.script.rmi.miniJsonRequestSync({
//					serviceId : "clinicChargesProcessingService",
//					serviceAction : "checkCardOrMZHM"
//						// cardOrMZHM : data.cardOrMZHM
//					});
//				if (r.code > 300) {
//					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
//					return;
//				} else {
//					if (!r.json.cardOrMZHM) {
//						Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
//						f.focus(false, 100);
//						return;
//					}
//				}
				if (e.getKey() == Ext.EventObject.ENTER) { // 触发了listener后，如果按回车，执行相应的方法
					var name = field.getName();
					var value = field.getValue();				
					var cardOrMZHM = this.systemParams.CARDORMZHM
					if (name == "JZKH" && !value && cardOrMZHM == 1) {
						// 为空时，默认打开新建档案功能
						this.showModule(1);
					} else if (name == "JZKH" && value
							&& cardOrMZHM == 1) {
						this.getBRXX(field, 0, 0, 0);
					}
					// 门诊号码
					if (name == "JZKH" && !value && cardOrMZHM == 2) {
						// 为空时，默认打开新建档案功能
						this.showModule(2);
					} else if (name == "JZKH" && value
							&& cardOrMZHM == 2) {
						this.getBRXX(field, 0, 0, 0);
					}
					if (name == "JZKH") {
						if (this.yysign == 2) {
							var form = this.formModule.form.getForm();
							var GHSJ = form.findField("GHSJ");
							GHSJ.focus();
						} else {
							// this.KSList.cndField.focus();
						}
					}
				}
			},
			getBRXXByMZHM : function(mzhm, ksdm, ysdm, YYBZ) {
				var yysi = 1;
				if (this.yysign) {
					yysi = this.yysign;
				}
				var form = this.formModule.form.getForm();
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "queryPerson",
							MZHM : mzhm,
							GHDT : form.findField("GHSJ").getValue()
									.format('Y-m-d'),
							YYTAG : yysi
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该门诊号码不存在!");
						return;
					}
					var dates = r.json.body;
					this.BRXX = dates;
					var BRXZ = form.findField("BRXZ");
					var BRXM = form.findField("BRXM");
					var BRXB = form.findField("BRXB");
					var JZKH = form.findField("JZKH");
					JZKH.setDisabled(true);
					var btns = this.formModule.form.getTopToolbar().items;
					btns.item(1).setDisabled(true);
					btns.item(3).setDisabled(true);
					if (this.BRXX.BRXZ) {
						BRXZ.setValue(this.BRXX.BRXZ);
					} else {
						BRXZ.setValue(6);
						this.BRXX.BRXZ = 6;
					}
					if (this.BRXX.JZKH) {
						JZKH.setValue(this.BRXX.JZKH);
					}
					BRXB.setValue(this.BRXX.BRXB);
					BRXM.setValue(this.BRXX.BRXM);
					if (this.formModule.GHXX) {
						Ext.apply(this.formModule.GHXX, this.BRXX);
					} else {
						this.formModule.GHXX = this.BRXX;
					}
					this.formModule.GHXX.YYBZ = YYBZ;
					this.formModule.GHXX.JZHM = form.findField("JZHM")
							.getValue();
					this.formModule.GHXX.ZBLB = this.ZBLB;
					this.formModule.GHXX.BLJE = form.findField("BLJE")
							.getValue();
					this.formModule.GHXX.GHSJ = form.findField("GHSJ")
							.getValue();
					this.KSList.BRXX = this.BRXX;
					this.formModule.GHXX.YSDM = ysdm;
					this.YSList.YSDM = ysdm;
					if (ksdm) {
						var KSstore = this.KSList.grid.getStore();
						var n = KSstore.getCount();
						for (var i = 0; i < n; i++) {
							var r = KSstore.getAt(i);
							if (r.data.KSDM == ksdm) {
								this.KSList.selectRow(i);
								this.departmentChoose(r);
								break;
							}
						}
						if (!form.findField("KSMC").getValue()) {
							this_ = this;
							Ext.Msg.alert("提示", "该预约科室未排班!", function() {
										this_.formModule.doNew();
									});
						}
					}
				}
			},
			getBRXX : function(field, ksdm, ysdm, YYBZ) {
				var yysi = 1;
				if (this.yysign) {
					yysi = this.yysign;
				}
				var form = this.formModule.form.getForm();
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "queryPerson",
							JZKH : field.getValue(),
							GHDT : form.findField("GHSJ").getValue()
									.format('Y-m-d'),
							YYTAG : yysi
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该卡号不存在!", function() {
									field.focus(false, 100);
								});
						return;
					}
					var dates = r.json.body;
					this.BRXX = dates;
					var BRXZ = form.findField("BRXZ");
					var BRXM = form.findField("BRXM");
					var BRXB = form.findField("BRXB");
					var JZKH = form.findField("JZKH");
					JZKH.setDisabled(true);
					var btns = this.formModule.form.getTopToolbar().items;
					btns.item(1).setDisabled(true);
					btns.item(3).setDisabled(true);
					if (this.BRXX.BRXZ) {
						BRXZ.setValue(this.BRXX.BRXZ);
					} else {
						BRXZ.setValue(6);
						this.BRXX.BRXZ = 6;
					}
					this.formModule.initYBServer(this.BRXX.BRXZ);  //初始化医保接口
					BRXB.setValue(this.BRXX.BRXB);
					BRXM.setValue(this.BRXX.BRXM);
					if (this.formModule.GHXX) {
						Ext.apply(this.formModule.GHXX, this.BRXX);
					} else {
						this.formModule.GHXX = this.BRXX;
					}
					this.formModule.GHXX.YYBZ = YYBZ;
					this.formModule.GHXX.JZHM = form.findField("JZHM")
							.getValue();
					this.formModule.GHXX.ZBLB = this.ZBLB;
					this.formModule.GHXX.BLJE = form.findField("BLJE")
							.getValue();
					this.formModule.GHXX.GHSJ = form.findField("GHSJ")
							.getValue();
					this.KSList.BRXX = this.BRXX;
					this.formModule.GHXX.YSDM = ysdm;
					this.YSList.YSDM = ysdm;
					this.ghyj = false;				
					this.ybbhxx = this.formModule.GHXX;
					if (ksdm) {
						var KSstore = this.KSList.grid.getStore();
						var n = KSstore.getCount();
						for (var i = 0; i < n; i++) {
							var r = KSstore.getAt(i);
							if (r.data.KSDM == ksdm) {
								this.KSList.selectRow(i);
								this.departmentChoose(r);
								break;
							}
						}
						if (!form.findField("KSMC").getValue()) {
							this_ = this;
							Ext.Msg.alert("提示", "该预约科室未排班!", function() {
										this_.formModule.doNew();
									});
						}
					} else {
						this.queryGHYJ(this.BRXX.BRID, form.findField("GHSJ")
										.getValue().format('Y-m-d'), form
										.findField("GHLB").getValue());
					}
				}
			},
			queryGHYJ : function(BRID, YJRQ, ZBLB) {
				var module = this.midiModules["ghyjlist"];
				if (!module) {
					module = this.createModule("ghyjlist",
							"phis.application.reg.REG/REG/REG31");
					module.on("loadData", this.ghyjloadData, this);
					module.on("ghyjChoose", this.ghyjChoose, this);
					module.requestData.cnd = [
							'and',
							['eq', ['$', 'a.BRID'], ['s', BRID]],
							[
									'eq',
									['$', "a.YJRQ"],
									['todate', ['s', YJRQ], ['s', 'yyyy-mm-dd']]],
							['eq', ['$', 'a.ZBLB'], ['i', ZBLB]],
							['eq', ['$', 'a.JGID'],
									['s', this.mainApp['phisApp'].deptId]],
							['eq', ['$', 'a.GHBZ'], ['i', 0]]];
					this.midiModules["ghyjlist"] = module;
					module.opener = this;
					var sm = module.initPanel();
					var win = module.getWin();
					module.loadData();
					win.add(sm);
				} else {
					module.requestData.cnd = [
							'and',
							['eq', ['$', 'a.BRID'], ['s', BRID]],
							[
									'eq',
									['$', "a.YJRQ"],
									['todate', ['s', YJRQ], ['s', 'yyyy-mm-dd']]],
							['eq', ['$', 'a.ZBLB'], ['i', ZBLB]],
							['eq', ['$', 'a.JGID'],
									['s', this.mainApp['phisApp'].deptId]],
							['eq', ['$', 'a.GHBZ'], ['i', 0]]];
					module.loadData();
				}
			},
			ghyjloadData : function(store) {
				if (store.getCount() > 1) {
					var win = this.midiModules["ghyjlist"].getWin();
					win.show();
				} else if (store.getCount() == 1) {
					var row = store.getAt(0);
					this.ghyj = row.data.YJXH;
					this.KSDM = row.data.KSDM;
					this.YSDM = row.data.YSDM;
					this.formModule.GHXX.YSDM = this.YSDM;
					this.YSList.YSDM = this.YSDM;
					var KSstore = this.KSList.grid.getStore();
					var n = KSstore.getCount();
					for (var i = 0; i < n; i++) {
						var r = KSstore.getAt(i);
						if (r.data.KSDM == this.KSDM) {
							this.KSList.selectRow(i);
							this.departmentChoose(r);
							break;
						}
					}
				} else {
					this.KSList.cndField.focus();
				}
			},
			ghyjChoose : function(record) {
				this.ghyj = record.data.YJXH;
				this.KSDM = record.data.KSDM;
				this.YSDM = record.data.YSDM;
				this.formModule.GHXX.YSDM = this.YSDM;
				this.YSList.YSDM = this.YSDM;
				var win = this.midiModules["ghyjlist"].getWin();
				win.hide();
				var KSstore = this.KSList.grid.getStore();
				var n = KSstore.getCount();
				for (var i = 0; i < n; i++) {
					var r = KSstore.getAt(i);
					if (r.data.KSDM == this.KSDM) {
						this.KSList.selectRow(i);
						this.departmentChoose(r);
						break;
					}
				}
			},
			getArchivesWin : function() {
				var module = this.createModule("archivesWindow",
						this.regMpiForm);
				var archives = module.initPanel();
				return archives;
			},
			showModule : function() {				
				var pdms = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "checkCardOrMZHM"
						// cardOrMZHM : data.cardOrMZHM
					});
				if (pdms.code > 300) {
					this.processReturnMsg(pdms.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!pdms.json.cardOrMZHM) {
						Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
						return;
					}
				}
				var m = this.midiModules["healthRecordModule"];
				if (!m) {
					$import("phis.application.pix.script.EMPIInfoModule");
					m = new phis.application.pix.script.EMPIInfoModule({
						/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
						//entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
						entryName : "phis.application.pix.schemas.MPI_DemographicInfo_SMQ",
						/*********************end*******************/
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
					m.on("onEmpiReturn", this.checkRecordExist, this);
					this.midiModules["healthRecordModule"] = m;
				}
				var win = m.getWin();
				win.show();				
				// 1卡号
				var form = m.midiModules[m.entryName].form.getForm();		
				this.formModule.initYBServer(form.findField("BRXZ").getValue());  //初始化医保接口
				if (pdms.json.cardOrMZHM == 1) {
					form.findField("MZHM").setDisabled(true);
				}
				// 2门诊号码
				if (pdms.json.cardOrMZHM == 2) {
					form.findField("cardNo").setValue(form.findField("MZHM")
									.getValue());
					form.findField("personName").focus(true, 200);
				}
			},
			showYbModule : function() {
				/*************以下是医保接口代码************/
				try{
					var xmlDoc = loadXmlDoc("c:\\njyb\\mzghxx.xml");
					var elements = xmlDoc.getElementsByTagName("RECORD");
					var datas = {};
					for (var i = 0; i < elements.length; i++) {
						datas.TBR = (elements[i].getElementsByTagName("TBR")[0].firstChild)?(elements[i].getElementsByTagName("TBR")[0].firstChild.nodeValue):"";
						datas.XM = (elements[i].getElementsByTagName("XM")[0].firstChild)?(elements[i].getElementsByTagName("XM")[0].firstChild.nodeValue):"";
						datas.RYXZ = (elements[i].getElementsByTagName("RYXZ")[0].firstChild)?(elements[i].getElementsByTagName("RYXZ")[0].firstChild.nodeValue):"";
						datas.KSM = (elements[i].getElementsByTagName("KSM")[0].firstChild)?(elements[i].getElementsByTagName("KSM")[0].firstChild.nodeValue):"";
						datas.KSMC = (elements[i].getElementsByTagName("KSMC")[0].firstChild)?(elements[i].getElementsByTagName("KSMC")[0].firstChild.nodeValue):"";
						datas.GHXH = (elements[i].getElementsByTagName("GHXH")[0].firstChild)?(elements[i].getElementsByTagName("GHXH")[0].firstChild.nodeValue):"";
						datas.DJH = (elements[i].getElementsByTagName("DJH")[0].firstChild)?(elements[i].getElementsByTagName("DJH")[0].firstChild.nodeValue):"";
						datas.FYLB = (elements[i].getElementsByTagName("FYLB")[0].firstChild)?(elements[i].getElementsByTagName("FYLB")[0].firstChild.nodeValue):"";
						datas.BZM = (elements[i].getElementsByTagName("BZM")[0].firstChild)?(elements[i].getElementsByTagName("BZM")[0].firstChild.nodeValue):"";
						datas.BZMC = (elements[i].getElementsByTagName("BZMC")[0].firstChild)?(elements[i].getElementsByTagName("BZMC")[0].firstChild.nodeValue):"";
						datas.GHF = (elements[i].getElementsByTagName("GHF")[0].firstChild)?(elements[i].getElementsByTagName("GHF")[0].firstChild.nodeValue):0;
						datas.ZLF = (elements[i].getElementsByTagName("ZLF")[0].firstChild)?(elements[i].getElementsByTagName("ZLF")[0].firstChild.nodeValue):0;
						datas.GRZL = (elements[i].getElementsByTagName("GRZL")[0].firstChild)?(elements[i].getElementsByTagName("GRZL")[0].firstChild.nodeValue):0;
						datas.GRZF = (elements[i].getElementsByTagName("GRZF")[0].firstChild)?(elements[i].getElementsByTagName("GRZF")[0].firstChild.nodeValue):0;
						datas.YBZF = (elements[i].getElementsByTagName("YBZF")[0].firstChild)?(elements[i].getElementsByTagName("YBZF")[0].firstChild.nodeValue):0;
						datas.ZHZF = (elements[i].getElementsByTagName("ZHZF")[0].firstChild)?(elements[i].getElementsByTagName("ZHZF")[0].firstChild.nodeValue):0;
						datas.ZHYE = (elements[i].getElementsByTagName("ZHYE")[0].firstChild)?(elements[i].getElementsByTagName("ZHYE")[0].firstChild.nodeValue):0;
						datas.XJZF = (elements[i].getElementsByTagName("XJZF")[0].firstChild)?(elements[i].getElementsByTagName("XJZF")[0].firstChild.nodeValue):0;
						datas.XZMC = (elements[i].getElementsByTagName("XZMC")[0].firstChild)?(elements[i].getElementsByTagName("XZMC")[0].firstChild.nodeValue):"";
						var m = this.midiModules["healthRecordModule"];
						if (!m) {
							$import("phis.application.pix.script.EMPIInfoModule");
							m = new phis.application.pix.script.EMPIInfoModule({
								entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							});
							m.on("onEmpiReturn", this.checkRecordExist, this);
							this.midiModules["healthRecordModule"] = m;
						}
						datas.BRXZ = 3000;
						m.brybxx = datas;
						var win = m.getWin();
						win.show();	
						var form = m.midiModules[m.entryName].form.getForm();
						form.findField("MZHM").setDisabled(true);
//							form.findField("MZHM").setValue(datas.TBR);
						form.findField("cardNo").setValue(datas.TBR);
						form.findField("personName").setValue(datas.XM);
						var BRXZ = form.findField("BRXZ");
						BRXZ.setValue({
							key : 3000,
							text : "市医保"
						});
						BRXZ.disable();
					}
				}catch(e){
					MyMessageTip.msg("提示", "文件解析失败!", true);
				}
				/*************以下是医保接口代码结束************/
			},
			showZjyyModule : function(brxz,xzmc) {//add by lizhi 2017-12-25紫金数云预约新增病人	
				var pdms = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "checkCardOrMZHM"
				});
				if (pdms.code > 300) {
					this.processReturnMsg(pdms.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!pdms.json.cardOrMZHM) {
						Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
						return;
					}
				}
				var m = this.midiModules["healthRecordModule"];
				if (!m) {
					$import("phis.application.pix.script.EMPIInfoModule");
					m = new phis.application.pix.script.EMPIInfoModule({
						entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
					m.on("onEmpiReturn", this.checkRecordExist, this);
					this.midiModules["healthRecordModule"] = m;
				}
				var win = m.getWin();
				win.show();
				// 1卡号
				var form = m.midiModules[m.entryName].form.getForm();
				if (pdms.json.cardOrMZHM == 1) {
					form.findField("MZHM").setDisabled(true);
				}
				// 2门诊号码
				if (pdms.json.cardOrMZHM == 2) {
					form.findField("cardNo").setValue(form.findField("MZHM")
									.getValue());
					form.findField("personName").focus(true, 200);
				}
				if(this.formModule && this.formModule.zjyyData){
					form.findField("personName").setValue(this.formModule.zjyyData.PATIENTNAME);
					form.findField("mobileNumber").setValue(this.formModule.zjyyData.PATIENTMOBILE);
					form.findField("idCard").setValue(this.formModule.zjyyData.PATIENTCARD);
					var BRXZ = form.findField("BRXZ");
					BRXZ.setValue({
						key : brxz,
						text : xzmc
					});
//					BRXZ.disable();
				}
			},
			onUpdateBrid : function(empiid) {
				if (this.crno != "1" && this.crno != "2") {
					if (this.ybxx && this.ybxx != null && empiid
							&& empiid != null) {
						var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : "medicareSYBService",
									serviceAction : "updateSjybBrxx",
									body : {
										empiid : empiid,
										sfzhm : this.ybxx.SHBZH
									}
								});
						if (ret.code > 300) {
							this.formModule.doNew();
							this.processReturnMsg(ret.code, ret.msg);
							return;
						} else {
							MyMessageTip.msg("提示", "更新BRID成功！", true);
						}
					}
				}
			},
			checkRecordExist : function(data) {
				var yysi = 1;
				if (this.yysign) {
					yysi = this.yysign;
				}
				if(data.nhdk && data.nhdk=="1"){
					this.nhdk="1";
				}
				var form = this.formModule.form.getForm();
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "queryPerson",
							MZHM : data.MZHM,
							GHDT : form.findField("GHSJ").getValue()
									.format('Y-m-d'),
							YYTAG : yysi
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该门诊号码"+data.MZHM+"不存在!");
						f.focus(false, 100);
						return;
					}
					var dates = r.json.body;
					//2019-05-09 zhaojian 增加优惠金额功能，传输身份证号
					dates.IDCARD = data.idCard;
					this.BRXX = dates;
					if ((data.lastModifyTime + "") == "undefined") {
						form.findField("BLJE").setValue(this.BLJE);
					} else {
						form.findField("BLJE").setValue("0.00");
					}
					var JZKH = form.findField("JZKH");
					var BRXZ = form.findField("BRXZ");
					var BRXM = form.findField("BRXM");
					var BRXB = form.findField("BRXB");
					var GHSJ = form.findField("GHSJ");
					if (this.BRXX.BRXZ) {
						BRXZ.setValue(this.BRXX.BRXZ);
					} else {
						BRXZ.setValue(6);
						this.BRXX.BRXZ = 6;
					}
					if(this.BRXX.BRXZ==2000){
						var yllb="";
						var regform = this.formModule.form.getForm();
						var NJJBYLLB = regform.findField("NJJBYLLB");
						if(this.drnjjbkxx){
							yllb="11,51,53,54"
							NJJBYLLB.setValue("11");
							if(this.drnjjbkxx.YBMMZG=="1"){
								yllb+=",16"
								if(NJJBYLLB){
									NJJBYLLB.setValue("16");
									this.formModule.changeyllb();
								}
							}
							if(this.drnjjbkxx.YBMJZG=="1"){
								yllb+=",14"
							}
							if(this.drnjjbkxx.YBMAZG=="1"){
								yllb+=",15"
							}
							if(this.drnjjbkxx.YBMTZG=="1"){
								yllb+=",171"
								NJJBYLLB.setValue("171");
								this.formModule.changeyllb();
							}							
						}
						if(yllb.length >0 && yllb!="11" ){
							if(NJJBYLLB){
							var dic={};
							dic.id="phis.dictionary.ybyllb_mz";
							dic.filter="['and',['in',['$','item.properties.YLLB','i'],["+yllb+"]]]";
							NJJBYLLB.store.proxy = new util.dictionary.HttpProxy({
										method : "GET",
										url : util.dictionary.SimpleDicFactory.getUrl(dic)
									})
							NJJBYLLB.store.load();
							}
						}else{
							if(NJJBYLLB){
							var dic={};
							dic.id="phis.dictionary.ybyllb_mz";
							NJJBYLLB.store.proxy = new util.dictionary.HttpProxy({
										method : "GET",
										url : util.dictionary.SimpleDicFactory.getUrl(dic)
									})
							NJJBYLLB.store.load();
							}
						}						
					}
					if (this.BRXX.JZKH) {
						JZKH.setValue(this.BRXX.JZKH);
						JZKH.setDisabled(true);
					}
					var btns = this.formModule.form.getTopToolbar().items;
					btns.item(1).setDisabled(true);
					btns.item(3).setDisabled(true);
					BRXB.setValue(this.BRXX.BRXB);
					BRXM.setValue(this.BRXX.BRXM);
					this.formModule.GHXX = this.BRXX;
					this.formModule.GHXX.JZHM = form.findField("JZHM")
							.getValue();
					this.formModule.GHXX.ZBLB = this.ZBLB;
					this.formModule.GHXX.BLJE = form.findField("BLJE")
							.getValue();
					this.formModule.GHXX.GHSJ = form.findField("GHSJ")
							.getValue();
					this.KSList.BRXX = this.BRXX;
					if (this.yysign == 2) {
						GHSJ.focus();
					} else {
						this.KSList.cndField.focus();
					}
				}
			},
			doPrint : function(sbxh) {
				// this.conditionFormId =
				// "SimplePrint_form_registrationForm";
				// var form = Ext.getCmp(this.conditionFormId).getForm()
				// if (!form.isValid()) {
				// return
				// }
				// var items = form.items
				var pages = "";
				// var url = this.printurl + "*.print?sbxh="+ sbxh +
				// "&silentPrint=1&execJs=" + this.getExecJs();
				if (this.mainApp['phisApp'].deptId == '330105104') {// 祥符挂号单打印
					pages = "phis.prints.jrxml.RegistrationForm2";
				} else {// 其它医院
					pages = "phis.prints.jrxml.RegistrationForm";
				}

				var url = "resources/" + pages + ".print?sbxh=" + sbxh
						+ "&silentPrint=1&execJs=" + this.getExecJs();
				// for (var i = 0; i < items.getCount(); i++) {
				// var f = items.get(i)
				// url += "&" + f.getName() + "=" + f.getValue()
				// }
				url += "&temp="
						+ Date.parseDate(Date.getServerDateTime(),
								'Y-m-d H:i:s').getTime();
				var printWin = window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");

				printWin.onafterprint = function() {
					printWin.close();
				};
			},
			onSaveCBXX : function(empiid) {
				if (this.crno != "1" && this.crno != "2") {
					if (this.ybxx && this.ybxx != null) {
						this.ybxx.EMPIID = empiid;
						var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : "medicareService",
									serviceAction : "saveCBRYJBXX",
									body : this.ybxx
								});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg);
							return;
						} else {
							MyMessageTip.msg("提示", "保存成功!", true);
						}
					}
				}
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('ghfp');";
			},
			focusPYDM : function() {
				this.KSList.cndField.focus();
			},
			doRefreshghks : function(ghrq, zblb) {
				if (ghrq && zblb) {
					this.KSList.requestData.ghrq = ghrq;
					this.KSList.requestData.zblb = zblb;
					this.KSList.loadData();
				} else {
					this.KSList.requestData.ghrq = "";
					this.KSList.requestData.zblb = "";
					this.KSList.loadData();
				}
			},
			doGetJTYGH : function() {
				if (this.yysign == 2) {
					var form = this.formModule.form.getForm();
					var ghsjval = form.findField("GHSJ");
					var ghlbval = form.findField("GHLB");
					if (new Date(ghsjval.getValue()).format('Y-m-d') != new Date()
							.format('Y-m-d')) {
						ghlbval.setValue(1);
						ghlbval.setDisabled(false);
					} else {
						ghlbval.setValue(2);
						ghlbval.setDisabled(true);
					}
					if (!form.findField("JZKH").getValue()) {
						Ext.Msg.alert("提示", "请输入卡号!", function() {
									form.findField("JZKH").focus(false, 100);
								});
						return;
					}
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicChargesProcessingService",
								serviceAction : "queryPerson",
								JZKH : form.findField("JZKH").getValue(),
								GHDT : form.findField("GHSJ").getValue()
										.format('Y-m-d'),
								YYTAG : 2
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return;
					} else {
						if (!r.json.body) {
							Ext.Msg.alert("提示", "该卡号不存在!", function() {
										field.focus(false, 100);
									});
							return;
						}
						var dates = r.json.body;
						this.BRXX = dates;
					}
				}
			},
			doNhghdk:function(){
				this.midiModules["nhdjlist"]=null;
				var nhdjmodule = this.createModule("nhdjlist", "phis.application.reg.REG/REG/REG0108");
					nhdjmodule.on("nhdkreturn", this.checkRecordExist, this);
				var win = nhdjmodule.getWin();
				win.add(nhdjmodule.initPanel());
				win.show();
				nhdjmodule.selectRow(0);
			},
			doJkkghdk:function(){//add by lizhi 2017-07-22健康卡读卡
				this.initHtmlElement();
				var initResult = this.initCard();
//				alert("初始化结果："+initResult);
				var initArr = initResult.split("|");
				if(initArr.length>0 && initArr[0]>-1){
					var cardinfo = this.readCardInfo();
					this.OffCardRead();
//			        alert("读取卡信息："+cardinfo);
			        var arr = cardinfo.split("^");
					if(arr[0]<0){
						Ext.Msg.alert("提示", "读卡失败!");
						this.getWin().hide();
						return;
					}
			        if(arr.length<3){
			        	Ext.Msg.alert("提示", "读卡失败!");
			        	this.getWin().hide();
						return;
			        }
					var cardArr = arr[2].split("|");
					var datas = [];
					if(cardArr.length>4){
						datas["BRXM"] = cardArr[0];
						datas["BRXB"] = cardArr[1];
						datas["MZDM"] = cardArr[2];
						datas["CSNY"] = cardArr[3];
						datas["SFZH"] = cardArr[4];
		//	       		alert("姓名："+cardArr[0]+",性别："+cardArr[1]+",出生年月："+cardArr[3]+",身份证号："+cardArr[4]);
					}
					var m = this.midiModules["healthRecordModule"];
					if (!m) {
						$import("phis.application.pix.script.EMPIInfoModule");
						m = new phis.application.pix.script.EMPIInfoModule({
							entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
							title : "个人基本信息查询",
							height : 450,
							modal : true,
							mainApp : this.mainApp
						});
						m.on("onEmpiReturn", this.checkRecordExist, this);
						this.midiModules["healthRecordModule"] = m;
					}
					datas["BRXZ"] = 1000;
					m.brybxx = datas;
					var win = m.getWin();
					win.show();	
					var form = m.midiModules[m.entryName].form.getForm();
					form.findField("MZHM").setDisabled(true);
					form.findField("cardNo").setValue(datas.SFZH);
					form.findField("idCard").setValue(datas.SFZH);
					form.findField("personName").setValue(datas.BRXM);
					var BRXZ = form.findField("BRXZ");
					BRXZ.setValue({
						key : 1000,
						text : "自费"
					});
					BRXZ.disable();
					var sexCode = form.findField("sexCode");
					var xbStr = datas["BRXB"];
					if(xbStr){//性别
						var sexCodeStr = xbStr.replace(/0/, "");
						var dicName = {
		            		id : "phis.dictionary.gender"
		          		};
						var gender=util.dictionary.DictionaryLoader.load(dicName);
						var di = gender.wraper[sexCodeStr];
						var sexNameStr=""
						if (di) {
							sexNameStr = di.text;
						}
						sexCode.setValue({
							key : sexCodeStr,
							text : sexNameStr
						});
					}
					var nationCode = form.findField("nationCode");
					var mzdmStr = datas["MZDM"];
					if(mzdmStr){//民族
						if(mzdmStr.length==1){
							mzdmStr="0"+mzdmStr;
						}
						var dicName = {
		            		id : "phis.dictionary.ethnic"
		          		};
						var ethnic=util.dictionary.DictionaryLoader.load(dicName);
						var di = ethnic.wraper[mzdmStr];
						var mzmcStr=""
						if (di) {
							mzmcStr = di.text;
						}
						nationCode.setValue({
							key : mzdmStr,
							text : mzmcStr
						});
					}
					var birthday = form.findField("birthday");
					birthday.setValue(datas["CSNY"]);
				}else{
					Ext.Msg.alert("提示", "读写设备初始化失败!");
				}
			},
			donjjbform:function(data){
				this.drnjjbkxx=data;//读入南京金保卡信息
				var jbmodule = this.createModule("njjbkxxform",
						"phis.application.reg.REG/REG/REG0109");
				jbmodule.opener = this;
				var panel=jbmodule.initPanel();
				jbmodule.on("njjbdkreturn", this.checkRecordExist, this);
				var win = jbmodule.getWin();
				win.add(panel);
				win.show();
				jbmodule.initFormData(data);
			},
			doGetNo:function(){
				this.midiModules["appointmentlist"]=null;
				var getNomodule = this.createModule("appointmentlist", "phis.application.reg.REG/REG/REG08");
//					nhdjmodule.on("nhdkreturn", this.checkRecordExist, this);
				getNomodule.width = 700;
				getNomodule.opener = this;
				var win = getNomodule.getWin();
				win.add(getNomodule.initPanel());
				win.show();
				getNomodule.onCountClick();
				getNomodule.selectRow(0);
			}
			,openFsyyList:function(){
				this.midiModules["FsyyList"]=null;
				var FsyyModule = this.createModule("FsyyList","phis.application.reg.REG/REG/REG09");
				FsyyModule.width=880;
				FsyyModule.height=400;
				FsyyModule.opener = this;
				var win = FsyyModule.getWin();
				win.add(FsyyModule.initPanel());
				win.show();
			}
		});