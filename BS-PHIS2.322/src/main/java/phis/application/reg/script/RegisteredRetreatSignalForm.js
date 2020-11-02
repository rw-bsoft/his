$package("phis.application.reg.script");

$import("phis.script.TableForm",
		"phis.application.yb.script.MedicareCommonMethod",
		"phis.script.Phisinterface","phis.script.DatamatrixReader",
		"phis.application.pay.script.PayCommon")

phis.application.reg.script.RegisteredRetreatSignalForm = function(cfg) {
	cfg.modal = true;
	Ext.apply(this,phis.application.yb.script.MedicareCommonMethod);
	Ext.apply(this,phis.script.Phisinterface);
	Ext.apply(this,phis.script.DatamatrixReader);
	phis.application.reg.script.RegisteredRetreatSignalForm.superclass.constructor
			.apply(this, [cfg])
	this.on('winShow', this.onWinShow, this);
}
Ext.extend(phis.application.reg.script.RegisteredRetreatSignalForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.width = 460;
				var form = new Ext.FormPanel({
							labelWidth : 60,
							autoHeight : true,
							frame : true,
							defaultType : 'textfield',
							buttonAlign : 'center',
							layout : 'tableform',
							layoutConfig : {
								columns : 1,
								tableAttrs : {
									border : 0,
									cellpadding : '2',
									cellspacing : "2"
								}
							},
							items : [{
										xtype : 'fieldset',
										title : '查询',
										autoHeight : true,
										width : 420,
										layout : 'tableform',
										layoutConfig : {
											columns : 3,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										listeners : {
											"afterrender" : this.onReady,
											scope : this
										},
										defaultType : 'textfield',
										items : [{
													fieldLabel : '卡号:',
													name : 'QJZKH',
													maxLength : 32,
													colspan : 2,
													width : '90%'
												}, {
													xtype : 'panel',
													layout : "table",
													width : 150,
													items : [new Ext.Button({
																iconCls : 'query',
																width : 70,
																text : '查询',
																handler : this.doQuery,
																// style :
																// "padding-left:20px",
																scope : this
															})]
												}]
									}, {
										xtype : 'fieldset',
										id : 'GHXX',
										title : '挂号信息',
										autoHeight : true,
										width : 420,
										layout : 'tableform',
										layoutConfig : {
											columns : 2,
											tableAttrs : {
												border : 0,
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										listeners : {
											"afterrender" : this.onReady,
											scope : this
										},
										defaultType : 'textfield',
										defaults : {
											anchor : '99.5%'
										},
										items : [/*
													 * { fieldLabel : 'No:',
													 * labelStyle :
													 * "text-align:right", xtype :
													 * "label", name : 'MZHM',
													 * width : 327, colspan : 2 },
													 */{
													fieldLabel : '病人姓名:',
													name : 'BRXM',
													disabled : true
												}, {
													fieldLabel : '病人性质:',
													name : 'BRXZ',
													disabled : true
												}, {
													fieldLabel : '挂号科室:',
													name : 'GHKS',
													disabled : true
												}, {
													fieldLabel : '挂号医生:',
													name : 'GHYS',
													disabled : true
												}, {
													fieldLabel : '挂号费:',
													name : 'GHF',
													disabled : true
												}, {
													fieldLabel : '诊疗费:',
													name : 'ZLF',
													disabled : true
												}, {
													fieldLabel : '专家费:',
													name : 'ZJF',
													disabled : true
												}, {
													fieldLabel : '病历费:',
													name : 'BLF',
													disabled : true
												}, {
													fieldLabel : '合计:',
													name : 'HJJE',
													width : '96%',
													colspan : 2,
													disabled : true
												}, {
													fieldLabel : '其他:',
													name : 'QTJE',
													disabled : true
												}, {
													fieldLabel : '现金:',
													name : 'XJJE',
													disabled : true
												},{
													fieldLabel : '病人性质代码',
								                    name: "XZDM",
								                    xtype: "hidden"
								                }]
									}],
							buttons : [{
										cmd : 'commit',
										text : '确定',
										handler : this.doCommit,
										iconCls : "commit",
										scope : this
									}, {
										text : '取消',
										handler : this.doConcel,
										iconCls : "common_cancel",
										scope : this
									}]
						});
				this.form = form;
				var from = this.form.getForm();
				var qjzkh = from.findField("QJZKH");
				qjzkh.un("specialkey", this.onFieldSpecialkey, this);
				qjzkh.on("specialkey", this.doSpecialkey, this);
				return this.form
			},
			F1 : function() {
				this.doCommit();
			},
			F2 : function() {
				this.doConcel();
			},
			doSpecialkey : function(field, e) {
				if (e.getKey() == Ext.EventObject.ENTER) {
					var value = field.getValue();
					if (value) {
						this.doQuery();
					} else {
						this.showModule();
					}
				}
			},
			showModule : function() {
				var m = this.midiModules["healthRecordModule"];
				if (!m) {
					$import("phis.script.pix.EMPIInfoModule");
					m = new phis.script.pix.EMPIInfoModule({
								entryName : "MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							});
					m.on("onEmpiReturn", this.doMZHMQuery, this);
					this.midiModules["healthRecordModule"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			doMZHMQuery : function(data) {
				this.clearGHDJ();
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "registeredManagementService",
							serviceAction : "queryGhdj",
							MZHM : data.MZHM
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (r.json.count > 0) {
						if (r.json.count == 1) {
							this.setGHDJ(r.json.body);
						} else {
							this.showGHDJ(r.json.BRID);
						}
					} else if (r.json.count < 0) {
						Ext.Msg.alert("提示", "该卡号不存在!", function() {
									qjzkh.focus(false, 100);
								});
					} else {
						Ext.Msg.alert("提示", "该患者没有可退挂号单(未挂号或已诊)!", function() {
									qjzkh.focus(false, 100);
								});
					}
				}
			},
			doQuery : function(f, e) {
				this.clearGHDJ();
				var from = this.form.getForm();
				qjzkh = from.findField("QJZKH");
				var value = qjzkh.getValue();
				if (value) {
					var req = {
						serviceId : "registeredManagementService",
						serviceAction : "queryGhdj",
						JZKH : value
					}
					var r = phis.script.rmi.miniJsonRequestSync(req);
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return;
					} else {
						if (r.json.count > 0) {
							if (r.json.count == 1) {
								this.setGHDJ(r.json.body);
							} else {
								this.showGHDJ(r.json.BRID);
							}
						} else if (r.json.count < 0) {
							Ext.Msg.alert("提示", "该卡号不存在!", function() {
										qjzkh.focus(false, 100);
									});
						} else {
							Ext.Msg.alert("提示", "该患者没有可退挂号单(未挂号或已诊)!",
									function() {
										qjzkh.focus(false, 100);
									});
						}
					}
				}
			},
			clearGHDJ : function() {
				var form = this.form.getForm();
				var GHXX = this.form.findById("GHXX");
				GHXX.setTitle("挂号信息:");
				var BRXM = form.findField("BRXM");
				var BRXZ = form.findField("BRXZ");
				var GHKS = form.findField("GHKS");
				var GHYS = form.findField("GHYS");
				var GHF = form.findField("GHF");
				var ZLF = form.findField("ZLF");
				var ZJF = form.findField("ZJF");
				var BLF = form.findField("BLF");
				var XJJE = form.findField("XJJE");
				var HJJE = form.findField("HJJE");
				var QTJE = form.findField("QTJE");
				BRXM.setValue();
				BRXZ.setValue();
				GHKS.setValue();
				GHYS.setValue();
				GHF.setValue();
				ZLF.setValue();
				ZJF.setValue();
				BLF.setValue();
				XJJE.setValue();
				HJJE.setValue();
				QTJE.setValue();
			},
			setGHDJ : function(date) {
				this.data = date;
				var form = this.form.getForm();
				var GHXX = this.form.findById("GHXX");
				GHXX.setTitle("挂号信息  No:" + date.JZHM);
				var BRXM = form.findField("BRXM");
				var BRXZ = form.findField("BRXZ");
				var GHKS = form.findField("GHKS");
				var GHYS = form.findField("GHYS");
				var GHF = form.findField("GHF");
				var ZLF = form.findField("ZLF");
				var ZJF = form.findField("ZJF");
				var BLF = form.findField("BLF");
				var XJJE = form.findField("XJJE");
				var HJJE = form.findField("HJJE");
				var QTJE = form.findField("QTJE");
				
				// update by wuyidao 2016-8-5  start----
				// 病人性质代码 
				var XZDM = form.findField("XZDM");
				XZDM.setValue(date.XZDM);
				// update by wuyidao 2016-8-5  end----
				
				BRXM.setValue(date.BRXM);
				GHKS.setValue(date.GHKS);
				GHYS.setValue(date.GHYS);
				BRXZ.setValue(date.BRXZ);
				GHF.setValue(parseFloat(date.GHF).toFixed(2));
				ZLF.setValue(parseFloat(date.ZLF).toFixed(2));
				ZJF.setValue(parseFloat(date.ZJF).toFixed(2));
				BLF.setValue(parseFloat(date.BLF).toFixed(2));
				XJJE.setValue(parseFloat(date.XJJE).toFixed(2));
				var hjje = (parseFloat(date.GHF) + parseFloat(date.ZLF)
						+ parseFloat(date.ZJF) + parseFloat(date.BLF))
						.toFixed(2);
				HJJE.setValue(hjje);
				QTJE.setValue((parseFloat(hjje) - parseFloat(date.XJJE))
						.toFixed(2));

				this.SBXH = date.SBXH;
			},
			showGHDJ : function(BRID) {
				var module = this.midiModules["ghdModule"];
				if (!module) {
					module = this.createModule("ghdModule", this.thbrList);
					this.midiModules["ghdModule"] = module;
					module.opener = this
					module.BRID = BRID;
					var sm = module.initPanel();
					var win = module.getWin();
					// module.loadData();
					win.add(sm)
					win.show();
				} else {
					module.BRID = BRID;
					var win = module.getWin();
					module.loadData();
					win.show();
				}
			},
			onWinShow : function() {
				this.afterShow();
			},
			focusFieldAfter : function(index, delay) {
				var form = this.form.getForm();
				var jkje = form.findField("JKJE");
				var YSK = form.findField("YSK");
				if (!jkje.getValue()) {
					form.findField("JKJE").setValue(YSK.getValue());
					form.findField("TZJE").setValue("");
				} else {
					if (parseFloat(jkje.getValue()) < parseFloat(YSK.getValue())) {
						jkje.setValue("");
						Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!",
								function() {
									jkje.focus(false, 200);
								});
						return;
					} else {
						var tzje = form.findField("TZJE");
						tzje.setValue(parseFloat(jkje.getValue())
								- parseFloat(YSK.getValue()));
					}
				}
				if (this.showButtonOnTop) {
					btns = this.form.buttons
					if (btns) {
						var n = btns.length;
						for (var i = 0; i < n; i++) {
							var btn = btns[i];
							if (btn.cmd == "commit") {
								btn.focus()
								return;
							}
						}
					}
				}
			},
			doCommit : function() {
				if (!this.SBXH) {
					return;
				}
				var ybbrxz = this.getYbbrxz();
				if (ybbrxz == null) {
					return;
				}
				var brxz=this.form.getForm().findField("XZDM").getValue();
				var ybbrxz = this.getYbbrxz(brxz);// 查询当前病人信息 是否是医保
				if(ybbrxz==null){
					MyMessageTip.msg("提示", "查询病人是否医保失败", true);
					return;
				}
				if(ybbrxz>0){
					this.doYbghth();
				}
				//yx-南京金保业务-2017-07-24-b
				debugger;
				
				var yzbz = null;
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "registeredManagementService",
					serviceAction : "getYZBZ",
					body : {"SBXH":this.SBXH}
				});
				if(r.code < 300){
					var body = r.json.body;
					if(body.YZBZ != null){
						yzbz = body.YZBZ; 
					}			
				}

				if(brxz==2000){
				var njjbbody={};
					njjbbody.USERID=this.mainApp.uid;
					//获取业务周期号
					var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "getywzqh",
						body:njjbbody
						});
					if (ret.code <= 300) {
						this.ywzqh=ret.json.YWZQH;
					}else{
						Ext.Msg.alert("获取业务周期号失败！请确认是否签到。")
						this.running = false;
						return;
					}
					var NJJBZFXX={};
					//获取流水号
					var getbody={};
					getbody.ywlx="1";//挂号
					getbody.SBXH=this.SBXH;
					var getlsh = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "getnjjblshbyqx",
						body:getbody
						});
					if(getlsh.code >=300){
						MyMessageTip.msg("提示", getlsh.msg, true);
						return;
					}else{
						NJJBZFXX.NJJBLSH=getlsh.json.lsh.NJJBLSH;
					}					
					var zlf = this.form.getForm().findField("ZLF").getValue();
					if(zlf>0){
						NJJBZFXX.DJH=this.data.JZHM;
						NJJBZFXX.JSCXRQ=new Date();
						NJJBZFXX.JBR=this.mainApp.uid;
						NJJBZFXX.SFBLCFBZ="0";
						NJJBZFXX.BY1="";
						NJJBZFXX.BY2="";
						NJJBZFXX.BY3="";
						NJJBZFXX.BY4="";
						NJJBZFXX.BY5="";
						NJJBZFXX.BY6="";
						this.addPKPHISOBJHtmlElement();
						//费用结算撤销交易
						var njjbzfxx=this.buildstr("2430",this.ywzqh,NJJBZFXX);
						var drzfre=this.drinterfacebusinesshandle(njjbzfxx);
						var zfarr=drzfre.split("^");
						if(zfarr[0]=="0"){
							//作废成功
						}else{
							MyMessageTip.msg("金保返回提示：",zfarr[3], true);
							return;	
						}
					}
					//取消登记
					var gxghdata={};
					gxghdata.NJJBLSH=getlsh.json.lsh.NJJBLSH;
					gxghdata.JBR=this.mainApp.uid;
					//撤销登记2240
					var str=this.buildstr("2240",this.ywzqh,gxghdata);
					this.addPKPHISOBJHtmlElement();
					var drre=this.drinterfacebusinesshandle(str);
					var arr=drre.split("^");
					if(arr[0]=="0"){
						//撤销登记成功
					}else{
						if(arr[1].indexOf("没有查询到")>-1 && arr[3].indexOf(gxghdata.NJJBLSH)>-1){
							
						}else{
							MyMessageTip.msg("提示", arr[3], true);
							return;
						}
					}
				}
				//yx-南京金保业务-2017-07-24-e
				this.saving = true
				this.form.el.mask("正在退号...", "x-mask-loading")
				var body = {};
				body.SBXH = this.SBXH;
				body.BRXZ = brxz;
				phis.script.rmi.jsonRequest({
							serviceId : "registeredManagementService",
							serviceAction : "saveRetireRegistered",
							body : body
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							this.fireEvent("retire", this);
								/*****begin 挂号结算界面增加扫码支付 zhaojian 2018-09-18 *****/
								//如果本地失败，调用支付平台取消微信支付宝付款
								if(json.ms_ghmx.FFFS==32 || json.ms_ghmx.FFFS==33){
									var querydata ={};
								 	querydata.APIURL = getPayApiUrl("orderQuery");
									this.initHtmlElement();//获取客户端IP前先初始化插件
								 	querydata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP
								 	querydata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
									//querydata.IP = "192.168.9.71";//测试时使用
					 				//querydata.COMPUTERNAME = "DPSF01";//测试时使用
								 	querydata.PAYSERVICE = "1";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
								 	querydata.PAYSERVICE_REFUND = "-1";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
									querydata.PATIENTID = json.ms_ghmx.BRID+"";
									querydata.ORGANIZATIONCODE = json.ms_ghmx.JGID;
									//querydata.ORGANIZATIONCODE = "320124003";//测试时使用
									querydata.VOUCHERNO = json.ms_ghmx.JZHM;
									//获取需退款订单信息
									var getorders = phis.script.rmi.miniJsonRequestSync({
										serviceId : "phis.mobilePaymentService",
										serviceAction : "queryNeedRefundOrder",
										body:querydata
										});
									if(getorders.code <=300){
										if(getorders.json && getorders.json.body && getorders.json.body.orders){
											if (getorders.json.body.orders.length == 0) {
												MyMessageTip.msg("提示", "未找到可退款的扫码支付订单信息!", true);
												return;
											}
											for (var i = 0; i < getorders.json.body.orders.length; i++) {
												//循环执行退款操作
												var refunddata ={};
												refunddata.ID_REFUND = getorders.json.body.orders[i].ID_REFUND;//退号单id
												refunddata.PAYSERVICE = "-1";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
												refunddata.IP = querydata.IP;//支付终端IP
								 				refunddata.COMPUTERNAME = querydata.COMPUTERNAME;//支付终端电脑名称
												//refunddata.IP = "192.168.9.71";//测试时使用
								 				//refunddata.COMPUTERNAME = "DPSF01";//测试时使用
												if(getorders.json.body.orders[i].HOSPNO_REFUND && getorders.json.body.orders[i].HOSPNO_REFUND!=""){
													refunddata.HOSPNO = getorders.json.body.orders[i].HOSPNO_REFUND;//医院流水号
												}
												else{
													refunddata.HOSPNO = generateHospno("TH",getorders.json.body.orders[i].VOUCHERNO);//医院流水号
												}
												refunddata.PAYMONEY = getorders.json.body.orders[i].PAYMONEY+"";//支付金额
												refunddata.VOUCHERNO = getorders.json.body.orders[i].VOUCHERNO;//就诊号码或发票号码
												refunddata.ORGANIZATIONCODE = getorders.json.body.orders[i].ORGANIZATIONCODE;//机构编码
												//refunddata.ORGANIZATIONCODE = "320124003";//测试时使用
												refunddata.PATIENTYPE = getorders.json.body.orders[i].PATIENTYPE;//病人性质
												refunddata.PATIENTID = getorders.json.body.orders[i].PATIENTID;//病人id
												refunddata.NAME = getorders.json.body.orders[i].NAME;//病人姓名
												refunddata.SEX = getorders.json.body.orders[i].SEX;//性别
												refunddata.IDCARD = getorders.json.body.orders[i].IDCARD;//身份证号
												refunddata.BIRTHDAY = getorders.json.body.orders[i].BIRTHDAY;//出生年月
												refunddata.PAYSOURCE = "1";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
												//自组机扫码退费
												if(json.ms_ghmx.FFFS==39 || json.ms_ghmx.FFFS==40){
													refunddata.PAYSOURCE = "2";
												}
												//refunddata.TERMINALNO = "";//支付终端号 如POS01、1号窗
												refunddata.COLLECTFEESCODE = this.mainApp.uid;//操作员代码
												refunddata.COLLECTFEESNAME = this.mainApp.uname;//操作员姓名
												//refunddata.COLLECTFEESCODE = "0310581X";//测试时使用
												//refunddata.COLLECTFEESNAME = "管理员";//测试时使用	
												refunddata.HOSPNO_ORG = getorders.json.body.orders[i].HOSPNO;//退款交易时指向原HOSPNO
												refund(refunddata,this);
												return;
											}
										}else{
											MyMessageTip.msg("提示", "获取可退款的扫码支付订单信息失败！", true);
											this.running = false;
											return;	
										}
									}else{
										MyMessageTip.msg("提示", "获取可退款的扫码支付订单信息失败！", true);
										this.running = false;
										return;
									}
								}
								/*****end 挂号结算界面增加扫码支付 zhaojian 2018-09-18 *****/							
							this.doCommit3();
						}, this)
			},
			doCommit3 : function(){
				this.doConcel();
			},
			doConcel : function() {
				win = this.getWin();
				if (win)
					win.hide();
			},
			afterShow : function() {
				this.data = false;
				this.ybxx = false;
				var form = this.form.getForm();
				qjzkh = form.findField("QJZKH");
				qjzkh.focus(false, 200);
				var GHXX = this.form.findById("GHXX");
				GHXX.setTitle("挂号信息");
				var BRXM = form.findField("BRXM");
				var BRXZ = form.findField("BRXZ");
				var GHKS = form.findField("GHKS");
				var GHYS = form.findField("GHYS");
				var GHF = form.findField("GHF");
				var ZLF = form.findField("ZLF");
				var ZJF = form.findField("ZJF");
				var BLF = form.findField("BLF");
				var XJJE = form.findField("XJJE");
				var HJJE = form.findField("HJJE");
				var QTJE = form.findField("QTJE");
				var QJZKH = form.findField("QJZKH");
				BRXM.setValue();
				GHKS.setValue();
				GHYS.setValue();
				BRXZ.setValue();
				GHF.setValue();
				ZLF.setValue();
				ZJF.setValue();
				BLF.setValue();
				XJJE.setValue();
				HJJE.setValue();
				QTJE.setValue();
				QJZKH.setValue();
				this.SBXH = "";
				// 清空病人性质代码
				form.findField("XZDM").setValue();
			},
			getWin : function() {
				var win = this.win;
				this.width = 460;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								shadow : false,
								modal : this.modal,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				win.instance = this;
				return win;
			}
		})