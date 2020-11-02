$package("phis.application.fsb.script")

$import("phis.script.SimpleList", "phis.application.fsb.script.FDRView")

phis.application.fsb.script.FamilySickBedPatientList = function(cfg) {
	cfg.sortable = false;
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = false;
	this.serviceId = "familySickBedManageService";
	phis.application.fsb.script.FamilySickBedPatientList.superclass.constructor
			.apply(this, [cfg]);
	this.on('loadData', this.afterLoadData, this)
}

Ext.extend(phis.application.fsb.script.FamilySickBedPatientList,
		phis.script.SimpleList, {
			initPanel : function() {
				var grid = phis.application.fsb.script.FamilySickBedPatientList.superclass.initPanel
						.call(this);
				var panel = new Ext.Panel({
							border : false,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : [this.createButtons()],
							items : [{
										layout : "fit",
										border : false,
										region : 'center',
										items : [grid]
									}]
						})
				this.panel = panel;
				this.panel.on("afterrender", this.panelOnReady, this)
				return panel;
			},
			panelOnReady : function() {
				var tbar = this.panel.getTopToolbar();
				if (this.mainApp['phisApp'].roleType == "group_13") {
					tbar.find('cmd', 'hljh')[0].hide();
					tbar.find('cmd', 'hljl')[0].hide();
					tbar.find('cmd', 'yzcl')[0].hide();
					tbar.find('cmd', 'tysq')[0].hide();
					//tbar.find('cmd','amqcApply')[0].hide();
				} else if (this.mainApp['phisApp'].roleType == "group_14") {
					tbar.find('cmd', 'zljh')[0].hide();
					tbar.find('cmd', 'ccgl')[0].hide();
					//tbar.find('cmd', 'zlbl')[0].hide();
				}
				this.systemParams = this.loadSystemParams({
							'privates' : ['JCZZLJTS','QYKJYWGL']
						})
				if(this.systemParams.QYKJYWGL==0){
					tbar.find('cmd','amqcApply')[0].hide();
				}
				this.today = Date.getServerDate();
//				if(this.systemParams == 0 ){
//					tbar.find('cmd','amqcApply')[0].hide();
//				}
			},
			// 计算家床天数
			jctsRender : function(v, p, r) {
				var KSRQ = r.get("KSRQ");
				var JSRQ = r.get("JSRQ");
				if (KSRQ && JSRQ) {
					var mil = Date.parseDate(JSRQ, 'Y-m-d')
							- Date.parseDate(KSRQ, 'Y-m-d');
					var d = parseInt(mil / 86400000);
					return d+1;
				}
				return "";
			},
			afterLoadData : function(store, records) {
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("JSRQ")) {
						var mil = Date.parseDate(
								r.get("JSRQ").substring(0, 10), 'Y-m-d')
								- Date.parseDate(this.today, 'Y-m-d');
						var d = parseInt(mil / 86400000);
						if (this.systemParams && this.systemParams.JCZZLJTS) {
							if (d <= this.systemParams.JCZZLJTS) {
								if (d < 1) {
									this.grid.getView().getRow(i).style.color = 'red';
								} else {
									this.grid.getView().getRow(i).style.color = 'blue';
								}
							}
						}
					}
				}
				// 提醒功能-长期医嘱停嘱
				if (store.getCount() > 0) {
					var openBy = '';
					if (this.mainApp['phisApp'].roleType == "group_13") {
						openBy = "doctor"
					} else if (this.mainApp['phisApp'].roleType == "group_14") {
						openBy = "nurse"
					}
					phis.script.rmi.jsonRequest({
								serviceId : this.serviceId,
								serviceAction : "loadStopPatients",
								body : {
									"openBy" : openBy,
									"cnd" : this.requestData.cnd
								}
							}, function(code, msg, json) {
								if (code >= 300) {
									this.processReturnMsg(code, msg);
									return;
								}
								var patients = json.body;
								if (patients && patients.length > 0) {
									var msg = "";
									for (var i = 0; i < patients.length; i++) {
										if (msg) {
											msg += "<br />";
										}
										msg += "病人【" + patients[i].BRXM
												+ "】存在需停嘱的医嘱,请及时处理!"
									}
									ymPrompt.alert({
												message : msg,
												title : '提示 - '
														+ Date
																.getServerDateTime(),
												winPos : 'rb',
												hashcode : "jcbryztztx",
												showMask : false,
												useSlide : true
											})
								}
							}, this);
				}

			},
			expansion : function(cfg) {
				delete cfg.buttons;
				var radiogroup = [{
							xtype : "radio",
							boxLabel : '全 部',
							inputValue : 0,
							name : "jczt",
							checked : true,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}, {
							xtype : "radio",
							boxLabel : '家 床',
							name : "jczt",
							inputValue : 2,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}, {
							xtype : "radio",
							boxLabel : '撤床',
							name : "jczt",
							inputValue : 9,
							listeners : {
								check : this.afterCheck,
								scope : this
							}
						}];
				var label = new Ext.form.Label({
							text : (this.mainApp['phisApp'].roleType == "group_13"
									? "责任医生"
									: "责任护士")
						});
				var docDic = util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.doctor_cfqx_kjg",
							// defaultIndex : "0",
							width : 80
						});

				docDic.setEditable(false);
				docDic.on("select", this.docDicSelect, this);
				this.docDic = docDic;
				this.docDic.store.on("load", this.docDicLoad, this);
				this.docDic.store.load();
				cfg.tbar.push([radiogroup, '->', label, '-', docDic]);
			},
			docDicLoad : function(store) {
				var r = new Ext.data.Record({
							key : "-1",
							text : "全部"
						})
				store.insert(0, [r]);
				this.docDic.setValue((this.mainApp.uid || "-1"))
			},
			docDicSelect : function() {
				this.doCndQuery();
			},
			afterCheck : function(radio, checked) {
				if (checked) {
					// this.refresh();
					this.jczt = radio.inputValue;
					this.doCndQuery();
				}

			},
			doCndQuery : function() {
				var exCnd = null;
				// 家床状态
				var jczt = this.jczt;
				if (jczt == 2) {
					exCnd = ['eq', ['$', 'a.CYPB'], ['i', 0]];
				} else if (jczt == 9) {
					exCnd = ['eq', ['$', 'a.CYPB'], ['i', 1]];
				} else {
					exCnd = ['le', ['$', 'a.CYPB'], ['i', 1]];
				}
				// 责任护士
				var zrhs = this.docDic.getValue();
				if (zrhs != '-1') {
					if (exCnd) {
						exCnd = ['and', exCnd,
								['eq', ['$', (this.mainApp.roleType == "group_13"
									? 'a.ZRYS'
									: 'a.ZRHS')], ['s', zrhs]]];
					} else {
						exCnd = ['eq', ['$', (this.mainApp.roleType == "group_13"
									? 'a.ZRYS'
									: 'a.ZRHS')], ['s', zrhs]];
					}
				}
				var initCnd = ['eq', ['$', 'a.JGID'],
						['s', this.mainApp.deptId]]
				this.initCnd = ['and', exCnd, initCnd];
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					return;
				}
				this.resetFirstPage()
				var f = this.cndField;
				var v = f.getValue()
				var rawV = f.getRawValue();
				var xtype = f.getXType();
				if ((Ext.isEmpty(v) || Ext.isEmpty(rawV))
						&& (xtype !== "MyRadioGroup" && xtype !== "MyCheckboxGroup")) {
					this.queryCnd = null;
					if (exCnd) {
						this.requestData.cnd = ['and', exCnd, initCnd]
					} else {
						this.requestData.cnd = initCnd
					}
					this.refresh()
					return
				}
				if (f.getXType() == "datefield") {
					v = v.format("Y-m-d")
				}
				if (f.getXType() == "datetimefield") {
					v = v.format("Y-m-d H:i:s")
				}
				// 替换'，解决拼sql语句查询的时候报错
				v = v.replace(/'/g, "''")
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					var expType = this.getCndType(it.type)
					if (it.dic.render == "Tree") {
						var node = this.cndField.selectedNode;
						if (!node || node.isLeaf()) {
							cnd.push([expType, v]);
						} else {
							cnd[0] = 'like'
							cnd.push([expType, v + '%'])
						}
					} else {
						cnd.push([expType, v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							if(it.id == 'ZYHM') {
								v = '%'+v;
							}
							cnd[0] = 'like'
							cnd.push(['s', '%' +v + '%'])
							break;
						case "date" :
							if (v.format) {
								v = v.format("Y-m-d")
							}
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
						case 'datetime' :
						case 'timestamp' :
							if (it.xtype == "datefield") {
								if (v.format) {
									v = v.format("Y-m-d")
								}
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd')"]
								cnd.push(['s', v])
							} else {
								if (v.format) {
									v = v.format("Y-m-d H:i:s")
								}
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd HH24:mi:ss')"]
								cnd.push(['s', v])
							}
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (exCnd) {
					cnd = ['and', exCnd, cnd]
				}
				this.requestData.cnd = cnd
				this.refresh()
			},
			// 基本信息
			doBrxx : function() {
				var r = this.getSelectedRecord();
				var module = this
						.createModule("brxxModule", this.refBrxxModule);
				if (module) {
					module.initDataId = r.get("ZYH");
					module.exContext.record = r;
					if (this.brxxWin) {
						this.brxxWin.show();
						return;
					}
					module.opener = this;
					this.brxxWin = module.getWin();
					this.brxxWin.setHeight(440);
					this.brxxWin.show();
				}
			},
			// 诊疗计划
			doZljh : function() {
				var r = this.getSelectedRecord();
				var module = this.createModule("refZljhModule",
						this.refZljhModule);
				if (module) {
					module.initDataId = r.get("ZYH");
					if (this.zljhWin) {
						this.zljhWin.show();
						return;
					}
					module.opener = this;
					this.zljhWin = module.getWin();
					// this.hljhWin.setHeight(440);
					this.zljhWin.show();
					this.zljhWin.maximize();
				}
			},

			doYzcl : function() {
				var r = this.getSelectedRecord();
				if (!r)
					return;  
				if (r.get("CYPB") > 0) {
					var module = this.createModule("fsbAdviceQueryModule",
							this.refFsbAdviceQueryModule);
					if (module) {           
						module.initDataId = r.get("ZYH");
						module.opener = this;
						module.openBy = "BRGL";
						if (this.adviceQueryWin) {
							this.adviceQueryWin.show();
							return;
						}
						// module.opener = this;
						this.adviceQueryWin = this.getWin();
						this.on("winShow", module.onWinShow, module);
						var panel = module.initPanel();
						if (panel) {
							this.adviceQueryWin.add(panel);
							this.adviceQueryWin.setHeight(600);
							this.adviceQueryWin.setWidth(1024);
							this.adviceQueryWin.maximize();
							this.adviceQueryWin.show();
						}
					}
					return;
				}
				var module = this.createModule("refCcglModule",
						this.refCcglModule);
				if (module) {
					module.initDataId = r.get("ZYH");
					if (!module.exContext) {
						module.exContext = {};
					}
					module.openBy = "nurse";
					module.exContext.brxx = r;
					if (this.ccglWin) {
						this.ccglWin.show();
						return;
					}
					module.opener = this;
					var panel = module.initPanel();
					if (panel) {
						this.ccglWin = module.getWin();
						// this.hljhWin.setHeight(440);
						this.ccglWin.show();
						this.ccglWin.maximize();
					}
				}
			},
			// 查床管理
			doCcgl : function() {
				var r = this.getSelectedRecord();
				if (!r)
					return;
				var exContext = {};
				var m = new phis.application.fsb.script.FDRView({
							empiId : r.get("EMPIID"),
							clinicId : r.get("ZYH"),
							brid : r.get("BRID")
						})
				m.on("close", this.doCndQuery, this);
				exContext.brxx = r;
				Ext.applyIf(m.exContext, exContext);
				m.setMainApp(this.mainApp);
				m.getWin().show()
				m.getWin().maximize()
			},
			// 护理记录
			doHljl : function() {

			},
			doZkgl : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var data = {};
				data = r.data;
				data.JSLX = 10;
				if (r.data.ZFPB == 1) {
					MyMessageTip.msg("提示", "该结算记录已作废!", true);
					return
				}
				var module = this
						.createModule("brzkModule", this.refZkglModule);
				r.data.JSLX = 0
				r.data.JSCS = 0;
				module.data = r.data;
				// module.on("commit",this.doFillIn,this);
				var win = module.getWin();
				win.add(module.initPanel());
				module.doFillIn(r.data);
				win.show();
			},
			// 撤床证管理
			doCczgl : function() {
				var r = this.getSelectedRecord();
				if (!r)
					return;
				// 调用计算历史医嘱的方法
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "saveCaclLsbz",
							body : {
								ZYH : r.get("ZYH")
							}
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
						}, this);
				var module = this.createModule("refCczglForm",
						this.refCczglForm);
				if (module) {
					module.initDataId = r.get("ZYH");
					if (!module.exContext) {
						module.exContext = {};
					}
					if (this.cczglWin) {
						this.cczglWin.show();
						return;
					}
					module.opener = this;
					module.on('doSave', this.loadData, this);
					this.cczglWin = module.getWin();
					this.cczglWin.add(module.initPanel());
					this.cczglWin.setHeight(500);
					this.cczglWin.show();
				}
			},
			doTysq : function() {
				var r = this.getSelectedRecord();
				if (!r)
					return;
				var module = this.createModule("retTysqModule",
						this.retTysqModule);
				if (module) {
					module.initDataId = r.get("ZYH");
					if (!module.exContext) {
						module.exContext = {};
					}
					if (this.tysqWin) {
						this.tysqWin.show();
						return;
					}
					module.opener = this;
					this.tysqWin = module.getWin();
					this.tysqWin.add(module.initPanel());
					this.tysqWin.setHeight(600);
					this.tysqWin.show();
				}
			},
			/**
			 * 护理记录
			 */
			doHljh : function() {
				var r = this.getSelectedRecord();
				var module = this.createModule(this.refHljh, this.refHljh);
				if (module) {
					var data = {};
					data.ZYH = r.get("ZYH");// 住院号
					data.BRXM = r.get("BRXM");// 病人姓名
					data.ZYHM = r.get("ZYHM");// 住院号码
					data.BRXB_text = r.get("BRXB_text");// 性别中文
					data.AGE = r.get("AGE");// 年龄
					if (r.get("JBMC")) {
						data.JBMC = r.get("JBMC");
					} else if (r.get("MQZD")) {
						data.JBMC = r.get("MQZD");
					} else {
						data.JBMC = "";
					}// 疾病名称
					data.BRQK = r.get("BRQK");// 病人情况
					data.GMYW = r.get("GMYW");// 过敏药物
					// data.ICU = r.get("ICU");
					if (this.hljhWin) {
						module.doFillIn(data);
						this.hljhWin.show();
						return;
					}
					// module.data = data;
					// module.opener = this;
					module.doInitData(data);
					this.hljhWin = module.getWin();
					this.hljhWin.add(module.initPanel());
					this.hljhWin.setWidth(1024);
					this.hljhWin.setHeight(600);
					this.hljhWin.maximize();
					this.hljhWin.show();
					// module.doFillIn(data);
				}
			},
			doHljl : function() {
				var r = this.getSelectedRecord();
				var module = this.createModule(this.refHljl, this.refHljl);
				if (module) {
					var data = {};
					data.ZYH = r.get("ZYH");// 住院号
					data.BRXM = r.get("BRXM");// 病人姓名
					data.ZYHM = r.get("ZYHM");// 住院号码
					data.BRXB_text = r.get("BRXB_text");// 性别中文
					data.AGE = r.get("AGE");// 年龄
					if (r.get("JBMC")) {
						data.JBMC = r.get("JBMC");
					} else if (r.get("MQZD")) {
						data.JBMC = r.get("MQZD");
					} else {
						data.JBMC = "";
					}// 疾病名称
					data.BRQK = r.get("BRQK");// 病人情况
					data.GMYW = r.get("GMYW");// 过敏药物
					// data.ICU = r.get("ICU");
					if (this.hljlWin) {
						module.doFillIn(data);
						this.hljlWin.show();
						return;
					}
					// module.data = data;
					// module.opener = this;
					module.doInitData(data);
					this.hljlWin = module.getWin();
					this.hljlWin.add(module.initPanel());
					this.hljlWin.setWidth(1024);
					this.hljlWin.setHeight(600);
					this.hljlWin.maximize();
					this.hljlWin.show();
					// module.doFillIn(data);
				}
			},
			doAmqcApply : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中病人信息!", true);
					return;
				}
				if (r.get("CYPB") > 1) {
					MyMessageTip.msg("提示","该病人已结束家床,不能操作此功能!",true);
					return;
				}
				var module = this.createModule("amqcApplyList",
						this.refAmqcApplyList);
				module.initDataId = r.get("ZYH");
				module.brxx = r.data;
				module.openBy = 'fsb';
				module.suffix = "_out";
				if (!this.amqcApplyListWin) {
					this.amqcApplyListWin = module.getWin();
					this.amqcApplyListWin.add(module.initPanel());
					this.amqcApplyListWin.setWidth(1000);
					this.amqcApplyListWin.setHeight(500);
				}
				this.amqcApplyListWin.show();
			}

		});