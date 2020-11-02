$package("phis.application.hos.script")
$import("phis.script.SimpleModule","phis.application.yb.script.MedicareCommonMethod","phis.script.Phisinterface",
"phis.script.DatamatrixReader","phis.application.pay.script.PayCommon")

phis.application.hos.script.HospitalSettlementManagementModule = function(cfg) {
	Ext.apply(this, app.modules.common)
	Ext.apply(this,phis.application.yb.script.MedicareCommonMethod);
	Ext.apply(this,phis.script.Phisinterface);
	Ext.apply(this,phis.script.DatamatrixReader);
	phis.application.hos.script.HospitalSettlementManagementModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hos.script.HospitalSettlementManagementModule,
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
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 145,
										items : this.getForm()
									}],
							tbar : (this.tbar || [])
									.concat(this.createButton())
						});

				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
				var btns = this.panel.getTopToolbar();
				var btn = btns.find("cmd", "invalid");
				btn[0].hide();
				var btn1 = btns.find("cmd", "settle");
				btn1[0].show();
			},
			getList : function() {
				var module = this.createModule("refSettlementManagementList",
						this.refList);
				module.on("setSettle", this.setSettle, this)
				this.list = module;
				module.opener = this;
				return module.initPanel();
			},
			getForm : function() {
				var module = this.createModule("refSettlementManagementForm",
						this.refForm);
				module.on("loadData", this.listLoadData, this)
				this.form = module;
				var form = module.initPanel();
				module.opener = this;
				return form
			},
			listLoadData : function() {
				// this.list.ldata = this.data;
				this.list.loadData();
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
				var f1 = 112;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.accessKey = f1 + i;
					btn.cmd = action.id;
					btn.text = action.name + "(F" + (i + 1) + ")";
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.notReadOnly = action.notReadOnly;
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref
				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
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
			// 缴款
			doContributions : function() {
				if (!this.data) {
					MyMessageTip.msg("提示", "请先选择病人", true);
					return;
				}
				var module = this.createModule("jkjlList", this.jkjlList);
				// module.on("commit",this.doFillIn,this);
				var win = module.getWin();
				win.add(module.initPanel());
				module.requestData.cnd = ['and',
						['eq', ['$', 'a.ZFPB'], ['i', 0]],
						['eq', ['$', 'a.ZYH'], ['i', this.data.ZYH]]];
				module.refresh();
				// this.form.doNew();
				// this.list.store.removeAll();
				// module.doNew();
				// win.center();
				win.show();
			},
			// 清单
			doInventory : function() {
				if (!this.data) {
					MyMessageTip.msg("提示", "请先选择病人", true);
					return;
				}
				var module = this.createModule("fyqdModule", this.fyqdModule);
				module.data = this.body;
				var win = module.getWin();
				win.add(module.initPanel());
				win.show();
			},
			// 结算
			doSettle : function() {
				if (!this.data) {
					MyMessageTip.msg("提示", "请先选择病人", true);
					return;
				}
				var form = this.form.form.getForm();
				var brxz = form.findField("BRXZ").getValue();
				if(brxz=="6000"){
				if(this.zyjsnkdk && this.zyjsnkdk=="1"){
					var drgs=form.findField("DRGS").getValue()
					if(drgs && drgs.length >0){
						this.body.dbzjs="1";//单病种结算标志
						this.body.DRGS=drgs;
						this.body.DRGSTEXT=form.findField("DRGS").getRawValue();
					}else{
						this.body.dbzjs="";
					}
					var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "zyyjs",
							body:this.body
						});
					if (res.code > 300) {
						this.processReturnMsg(res.code, res.msg);
						return;
					} else {
						this.body.TCJE=res.json.yjsxx.SUM02;
						this.body.ZFHJ=this.body.FYHJ-this.body.TCJE;
						this.body.nhyjsxx=res.json.yjsxx;
						this.showZyjsModule();
					}
				}
				else{
					//打开农合读卡界面
					this.doZyjsnhdk();
				}
				}else if(brxz=="2000" && this.data.JSXZ=="医保"){
					var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.NjjbService",
							serviceAction : "checkMxSc",
							body:this.data
						});
					if(res.code<=300){
						/*if(res.json.WJSS_ZF>0){//自费部分未结算
							this.showZyjsModule();
							return;
						}*/
						if(res.json.WSCS==0){
							var module = this.createModule("njjbForm", this.njjbForm);
							module.on("qr", this.onNjjbQr, this);
							var win = module.getWin();
							win.add(module.initPanel());
							module.doNew();
							module.form.getForm().findField('YLLB').setValue({
								key : this.data.NJJBYLLB,
								text : '普通住院'
							});
							win.show();
							var kxx=module.doDk();
							kxx.YLLB=this.data.NJJBYLLB;
							module.initFormData(kxx)
						}else {
							MyMessageTip.msg("提示", "您还有"+res.json.WSCS+"条明细未上传，请在南京金保费用上传功能里上传明细！", true);
						}
					}else{
						this.processReturnMsg(res.code, res.msg);
						return;
					}
				} else {
					this.showZyjsModule();
				}
			},
			onNjjbQr : function(data) {
				this.ybxx = data;
				this.BRXZ = this.shiyb;
				data.BRXZ = this.shiyb;
				data.GRSFH = data.SFZH;
				this.data.GRBH = this.ybxx.GRBH;
				var form = this.form.form.getForm();
				var brxm = form.findField("BRXM").getValue();
				if (this.ybxx.XM != brxm) {
					Ext.Msg.alert("提示", "医保卡信息和发票信息不符!");
					return;
				}
				//下面是预结算
				var jsret = this.doXSzYjs();
				if (jsret == null) {
					return;
				}
				this.body.YBZF = Math.abs(parseFloat(jsret.BCDBJZZF)+parseFloat(jsret.BCDBBXZF)+parseFloat(jsret.BCMZBZZF)).toFixed(2);
//				alert("YBZF:"+this.body.YBZF);
				this.body.ZFHJ = parseFloat(jsret.BCXZZFZE).toFixed(2);
				this.body.TCJE=parseFloat(jsret.BCTCZFJE).toFixed(2);
				this.body.ZHZF = parseFloat(jsret.BCZHZFZE).toFixed(2);
				this.body.YBFYZE = parseFloat(jsret.BCYLFZE);
				this.body.NJJB = 1;
				this.body.zylsh = this.ZYLSH;
				this.body.zycs = this.ZYLSHCS;
				this.body.cs = this.CS     
				this.showZyjsModule();
			},
			doXSzYjs : function(){
				//去后台查询数据构建参数
				var remxts = phis.script.rmi.miniJsonRequestSync({
							serviceId : "NjjbService",
							serviceAction : "getZymxxx",
							ZYH : this.data.ZYH
						});
				if (remxts.code > 300) {
					this.processReturnMsg(remxts.code, remxts.msg);
					return;
				} else {
					this.ZYLSHCS = remxts.json.body.ZYLSHCS;
					this.ZYLSH = remxts.json.body.ZYLSH;
					this.CS = remxts.json.body.CS;
				}
				var data = {};
				var body={};
				body.USERID=this.mainApp.uid;
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.NjjbService",
					serviceAction : "getywzqh",
					body:body
				});
				if (ret.code <= 300) {
				var ywzqh=ret.json.YWZQH;
				this.addPKPHISOBJHtmlElement();
				this.drinterfaceinit();
				var str=this.buildstr_zy("2420",ywzqh,data);
//				//alert(this.CS)
				var drre=this.drinterfacebusinesshandle(str+this.ZYLSHCS+"|"+this.CS+"^");
				var arr=drre.split("^");
				if(arr[0]=="0"){
					var reformat = "BCYLFZE|BCTCZFJE|BCDBJZZF|BCDBBXZF|BCMZBZZF|BCZHZFZE|BCXZZFZE|BCZHZFZF|BCZHZFZL|BCXJZFZF|BCXJZFZL|YBFWNFY|ZHXFHYE|DBZBZBM|SMXX|YFHJ|ZLXMFHJ|BBZF|YLLB|BY6";
//				    //alert(arr[2])
				    var reData = this.StrToObj(reformat,arr[2],"|");
					var YBJSJGLBobj = reData;
				}else{
					MyMessageTip.msg("东软返回信息：",drre, true);
					this.panel.el.unmask();
					return;	
				}
				} else {
					MyMessageTip.msg("提示：",ret.msg, true);
					return;
				}
				
				return YBJSJGLBobj;
			},
			showZyjsModule : function() {
				var module = this.createModule("zyjsModule", this.zyjsModule);
				module.JSLX = this.JSLX;
				module.on("settlement", this.settlement, this);
				//zhaojian 2019-06-19 医保病人住院结算自费医保分离
				this.body.JSXZ = this.data.JSXZ;
				module.data = this.body;
				module.opener = this;
				var win = module.getWin();
				win.setSize(800,580);
				var m = module.initPanel();
				win.add(m);
				win.show();
			},
			//农合作废
			doNHInvalid: function(cardid) {
				//校验刷卡
				var body={};
				body.cardid=cardid;
				var form = this.form.form.getForm();
				body.ZYH = form.findField("ZYH").getValue();
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "checkBr",
							body:body
						});
				if (res.code > 300) {
					this.processReturnMsg(res.code, res.msg);
					this.nhzfflag="";
					reutrn;
				} else {
					if(res.json.body && res.json.body.nhdk && res.json.body.nhdk=="1"){
						this.body.NHKH=res.json.body.NHKH;
						this.body.GRBH=res.json.body.GRBH;
						this.nhzfflag="1";
						this.invalid=false;
						this.doInvalid();
					}
				}
			},
			// 作废
			doInvalid : function() {
				if (!this.data) {
					MyMessageTip.msg("提示", "请先选择病人", true);
					return;
				}
				if (this.invalid) {
					return;
				}
				//zhaojian 2019-06-19 
				this.body.JSXZ = this.data.JSXZ;
				this.body.FPHM = this.data.FPHM;
				Ext.MessageBox.confirm('提示', '确定发票作废吗?', function(btn) {
					if (btn == "yes") {
						this.invalid = true;
						this.panel.el.mask("正在作废...", "x-mask-loading")
						var form = this.form.form.getForm();
						//var brxz = form.findField("BRXZ").getValue();
						if(this.data.BRXZ==6000){
							if(this.nhzfflag && this.nhzfflag=="1"){
								
							}else{
								this.panel.el.unmask();
								this.midiModules["nhzflist"]=null;
								var nhzfmodule =this.createModule("nhzflist", "phis.application.hos.HOS/HOS/HOS080207");
								nhzfmodule.on("zfnhdkreturn", this.doNHInvalid, this);
								var win = nhzfmodule.getWin();
								win.add(nhzfmodule.initPanel());
								win.show();
								return;
							}
						}else if (this.data.BRXZ==2000 && this.data.JSXZ=="医保") {
							//南京金保进行作废
							var brshbzkh="";//病人社会保障卡号
							var re = phis.script.rmi.miniJsonRequestSync({
								serviceId : "phis.NjjbService",
								serviceAction : "getNjjbKh",
								brid:this.data.BRID
							});
							if(re.code<= 300){
								brshbzkh=re.json.SHBZKH;
							}else{
								Ext.Msg.alert("根据病人ID未获取到卡号")
							}
							var form = this.form.form.getForm();
							var fphm = form.findField("ZYHM").getValue();
							this.addPKPHISOBJHtmlElement();
							this.drinterfaceinit();
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
							}
							else{
								Ext.Msg.alert("获取业务周期号失败！请确认是否签到。")
								this.invalid = false;
								this.panel.el.unmask();
								return;
							}
							var dkstr=this.buildstr("2100",this.ywzqh,"");
							var drdkre=this.drinterfacebusinesshandle(dkstr);
							var kxxarr=drdkre.split("^");
							if(kxxarr[0]=="0"){
								var canshu=kxxarr[2].split("|")
								if(brshbzkh==canshu[0]){
									var NJJBZFXX={};
									NJJBZFXX.NJJBLSH=this.data.NJJBLSH;
									NJJBZFXX.DJH=fphm;
									NJJBZFXX.JSCXRQ=new Date();
									NJJBZFXX.JBR=this.mainApp.uid;
									NJJBZFXX.SFBLCFBZ="0";
									NJJBZFXX.BY1="";
									NJJBZFXX.BY2="";
									NJJBZFXX.BY3="";
									NJJBZFXX.BY4="";
									NJJBZFXX.BY5="";
									NJJBZFXX.BY6="";
									var njjbzfxx=this.buildstr("2430",this.ywzqh,NJJBZFXX);
									var drzfre=this.drinterfacebusinesshandle(njjbzfxx);
									var zfarr=drzfre.split("^");
									if(zfarr[0]=="0"){
									//作废成功
									}else{
										MyMessageTip.msg("金保返回提示：",zfarr[3], true);
										this.invalid = false;
										this.panel.el.unmask();
										return;	
									}
								}else{
									Ext.Msg.alert("此卡与病人卡号不一致，不能作废！")
									this.invalid = false;
									this.panel.el.unmask();
									return;
								}
							}else{
								Ext.Msg.alert("南京金保返回信息："+drdkre)
								this.invalid = false;
								this.panel.el.unmask();
								return;
							}
						}
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hospitalPatientSelectionService",
							serviceAction : "updateSettleAccounts",
							body : this.body
						});
						if (r.code > 300) {
							this.invalid = false;
							this.panel.el.unmask();
							Ext.Msg.alert("提示", r.msg, function() {
										this.doNew()
									}, this);
							return
						} else {
							if (r.json.msg) {
								this.invalid = false;
								Ext.Msg.alert("提示", r.json.msg);
								return;
							}
							/*****begin 发票作废增加扫码支付退费功能 zhaojian 2020-03-05 *****/
							if(this.form.data.FKFS==52 || this.form.data.FKFS==53){
								var querydata ={};
							 	querydata.APIURL = getPayApiUrl("orderQuery");
								this.initHtmlElement();//获取客户端IP前先初始化插件
								querydata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP
								querydata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
								//querydata.IP = "192.168.110.73";//测试时使用
								//querydata.COMPUTERNAME = "SHOUFEI3";//测试时使用
							 	querydata.PAYSERVICE = "4";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费，-3预交金退费，-4住院退费'
							 	querydata.PAYSERVICE_REFUND = "-4";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费，-3预交金退费，-4住院退费'
								querydata.PATIENTID =this.data.BRID+"";
								querydata.ORGANIZATIONCODE = this.data.JGID;
								//querydata.ORGANIZATIONCODE = "320124001";//测试时使用
								querydata.VOUCHERNO = this.data.FPHM;
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
											refunddata.PAYSERVICE = "-4";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费，-3预交金退费，-4住院退费'
											refunddata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP
											refunddata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
											//refunddata.IP = "192.168.110.73";//测试时使用
											//refunddata.COMPUTERNAME = "SHOUFEI3";//测试时使用
											if(getorders.json.body.orders[i].HOSPNO_REFUND && getorders.json.body.orders[i].HOSPNO_REFUND!=""){
												refunddata.HOSPNO = getorders.json.body.orders[i].HOSPNO_REFUND;//医院流水号
											}
											else{
												refunddata.HOSPNO = generateHospno("ZYTF",getorders.json.body.orders[i].VOUCHERNO);//医院流水号
											}
											refunddata.PAYMONEY = getorders.json.body.orders[i].PAYMONEY+"";//支付金额
											refunddata.VOUCHERNO = getorders.json.body.orders[i].VOUCHERNO;//就诊号码或发票号码
											refunddata.ORGANIZATIONCODE = getorders.json.body.orders[i].ORGANIZATIONCODE;//机构编码
											//refunddata.ORGANIZATIONCODE = "320124001";//测试时使用
											refunddata.PATIENTYPE = getorders.json.body.orders[i].PATIENTYPE;//病人性质
											refunddata.PATIENTID = getorders.json.body.orders[i].PATIENTID;//病人id
											refunddata.NAME = getorders.json.body.orders[i].NAME;//病人姓名
											refunddata.SEX = getorders.json.body.orders[i].SEX;//性别
											refunddata.IDCARD = getorders.json.body.orders[i].IDCARD;//身份证号
											refunddata.BIRTHDAY = getorders.json.body.orders[i].BIRTHDAY;//出生年月
											/*****增加app支付退费功能 zhaojian 2018-10-04 付款方式(35微信 36支付宝)*****/
											if(getorders.json.body.orders[i].HOSPNO.indexOf("ZYSF")==0){
												refunddata.PAYSOURCE = "1";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
											}else{
												refunddata.PAYSOURCE = "3";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
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
							this.doCommit3();
						}
					}
				}, this);
			},
			doCommit3 : function() {
				this.invalid = false;
				this.panel.el.unmask();
				this.nhzfflag="";
				MyMessageTip.msg("提示", "发票作废成功!", true);
				this.settlement(this);
			},
			// 选择
			doChoose : function(item, e) {
				this.data = "";
				var module = this.createModule("ssybjsModule",
						this.ssybjsModule);
				module.on("commit", this.doFillIn, this);
				var win = module.getWin();
				win.add(module.initPanel());
				this.form.doNew();
				this.list.store.removeAll();
				module.doNew();
				win.center();
				win.show();
			},
			doFillIn : function(data) {
				this.clearYbxx();//清除医保缓存
				this.zyjsnkdk="";//重置农合结算读卡
				this.nhzfflag="";//重置农合作废读卡
				var btns = this.panel.getTopToolbar();
				if (data.JSLX == 10) {
					this.JSLX = 10;
					var btn = btns.find("cmd", "invalid");
					btn[0].show();
					var btn1 = btns.find("cmd", "settle");
					btn1[0].hide();
					// var btn2 = btns.find("cmd", "hzyb");
					// btn2[0].hide();
				} else if (data.JSLX == 5) {
					this.JSLX = 5;
					var btn = btns.find("cmd", "invalid");
					btn[0].hide();
					var btn1 = btns.find("cmd", "settle");
					btn1[0].show();
					// var btn2 = btns.find("cmd", "hzyb");
					// btn2[0].show();
				} else if (data.JSLX == 4) {
					this.JSLX = 4;
					var btn = btns.find("cmd", "invalid");
					btn[0].hide();
					var btn1 = btns.find("cmd", "settle");
					btn1[0].show();
					// var btn2 = btns.find("cmd", "hzyb");
					// btn2[0].show();
				} else if (data.JSLX == 1) {
					this.JSLX = 1;
					var btn = btns.find("cmd", "invalid");
					btn[0].hide();
					var btn1 = btns.find("cmd", "settle");
					btn1[0].show();
					// var btn2 = btns.find("cmd", "hzyb");
					// btn2[0].hide();
				}
				this.data = data;
				this.form.ldata = data;
				this.list.ldata = data;
				this.form.loadData();
				// this.list.loadData(data);
			},
			doQuery : function(data) {
				this.opener.doQuery(data)
			},
			doNew : function(JSLX) {
				this.clearYbxx();//清除医保缓存
				var btns = this.panel.getTopToolbar();
				if (JSLX == 10) {
					this.JSLX = 10;
					var btn = btns.find("cmd", "invalid");
					btn[0].show();
					var btn1 = btns.find("cmd", "settle");
					btn1[0].hide();
					// var btn2 = btns.find("cmd", "hzyb");
					// btn2[0].hide();
				} else if (JSLX == 5) {
					this.JSLX = 5;
					var btn = btns.find("cmd", "invalid");
					btn[0].hide();
					var btn1 = btns.find("cmd", "settle");
					btn1[0].show();

					// var btn2 = btns.find("cmd", "hzyb");
					// btn2[0].show();
				} else if (JSLX == 4) {
					this.JSLX = 4;
					var btn = btns.find("cmd", "invalid");
					btn[0].hide();
					var btn1 = btns.find("cmd", "settle");
					btn1[0].show();

					// var btn2 = btns.find("cmd", "hzyb");
					// btn2[0].show();
				} else if (JSLX == 1) {
					this.JSLX = 1;
					var btn = btns.find("cmd", "invalid");
					btn[0].hide();
					var btn1 = btns.find("cmd", "settle");
					btn1[0].show();
					// var btn2 = btns.find("cmd", "hzyb");
					// btn2[0].hide();
				}
				this.data = null;
				this.form.doNew();
				this.list.clear();
				this.list.labelClear();
				this.zyjsnkdk="";
			},
			settlement : function() {
				this.fireEvent("settlement", this);
				this.doNew();
			},
			setSettle : function(sta) {
				var btns = this.panel.getTopToolbar();
				var btn1 = btns.find("cmd", "settle");
				if (btn1) {
					if (btn1[0]) {
						//btn1[0].setDisabled(sta);
					}
				}
			},
			//农合读卡
			doZyjsnhdk: function() {
				this.panel.el.mask();
				this.opener.list.mask();
				this.midiModules["nhzydjlist"]=null;
				var nhzydjmodule= this.createModule("nhzydjlist", "phis.application.hos.HOS/HOS/HOS0302");
					nhzydjmodule.on("zynhdkreturn", this.docheck, this);
					nhzydjmodule.on("close",function() {
						this.panel.el.unmask();
						this.opener.list.unmask();
					},this);
				var win = nhzydjmodule.getWin();
				win.add(nhzydjmodule.initPanel());
				win.show();
			},
			
			docheck: function(data) {
				//校验刷卡和结算病人
				var body={};
				body.cardid=data.NHKH;
				var form = this.form.form.getForm();
				body.ZYH = form.findField("ZYH").getValue();
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "checkBr",
							body:body
						});
				if (res.code > 300) {
					this.processReturnMsg(res.code, res.msg);
				} else {
					if(res.json.body && res.json.body.nhdk && res.json.body.nhdk=="1"){
						this.body.NHKH=res.json.body.NHKH;
						this.body.GRBH=res.json.body.GRBH;
						this.zyjsnkdk="1";
						this.doSettle();
						}
				}
				this.panel.el.unmask();
				this.opener.list.unmask();
			},
			// 下面医保相关代码
			doYbdk : function() {
				alert("医保功能完善中....");
				return;
				var ybModule = this.createModule("ybzyjsModule",
						"phis.application.yb.YB/YB/YB01");
				ybModule.initPanel();
				var win = ybModule.getWin();
				win.show();
				ybModule.on("qr", this.onQr_zyjs, this);
				ybModule.doNew();
				this.ybModule = ybModule;
			},
			// 医保部分结束
			//发票打印测试
			doPrint: function(){
				var LODOP=getLodop();  
//	    		LODOP.PRINT_INITA(3,8,643,680,"住院发票套打");
				var fphm="17006300001" ;
			var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "hospitalPatientSelectionService",
					serviceAction : "printMoth",
						fphm : fphm
					});
				this.fphm = false;
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				LODOP.SET_PRINT_STYLE("FontColor", "#0000FF");
				LODOP.SET_PRINT_PAGESIZE(0,'21cm','10.1cm',"")
				LODOP.ADD_PRINT_TEXT("10mm", "30mm", "20mm", "5mm", ret.json.ZYH);
				LODOP.ADD_PRINT_TEXT("10mm", "78mm", "20mm", "5mm", "非盈利");
				LODOP.ADD_PRINT_TEXT("10mm", "112mm", "20mm", "5mm", ret.json.BAHM);
				LODOP.ADD_PRINT_TEXT("10mm", "150mm", "20mm", "5mm", ret.json.ZYHM);
				LODOP.ADD_PRINT_TEXT("15mm", "30mm", "10mm", "5mm", ret.json.RYYY);
				LODOP.ADD_PRINT_TEXT("15mm", "47mm", "8mm", "5mm", ret.json.RYMM);
				LODOP.ADD_PRINT_TEXT("15mm", "55mm", "8mm", "5mm", ret.json.RYDD);
				LODOP.ADD_PRINT_TEXT("15mm", "70mm", "10mm", "5mm", ret.json.CYYY);
				LODOP.ADD_PRINT_TEXT("15mm", "80mm", "8mm", "5mm", ret.json.CYMM);
				LODOP.ADD_PRINT_TEXT("15mm", "90mm", "8mm", "5mm", ret.json.CYDD);
				LODOP.ADD_PRINT_TEXT("15mm", "130mm", "8mm", "5mm", ret.json.days);
				
				LODOP.ADD_PRINT_TEXT("20mm", "21mm", "20mm", "5mm", ret.json.XM);
				LODOP.ADD_PRINT_TEXT("20mm", "55mm", "15mm", "5mm", ret.json.XB);//性别
				LODOP.ADD_PRINT_TEXT("20mm", "74mm", "30mm", "5mm", ret.json.JSFS);//结算方式
				LODOP.ADD_PRINT_TEXT("20mm", "136mm", "40mm", "5mm", ret.json.YLZH);//社会保障号码
				//明细打印
				LODOP.ADD_PRINT_TEXT("32mm", "13mm","26mm", "5mm", ret.json.XMMC1);
				LODOP.ADD_PRINT_TEXT("32mm", "40mm", "20mm", "5mm", ret.json.XMJE1);
				LODOP.ADD_PRINT_TEXT("37mm", "13mm","26mm", "5mm", ret.json.XMMC4);
				LODOP.ADD_PRINT_TEXT("37mm", "40mm", "20mm", "5mm", ret.json.XMJE4);
				LODOP.ADD_PRINT_TEXT("42mm", "13mm","26mm", "5mm", ret.json.XMMC7);
				LODOP.ADD_PRINT_TEXT("42mm", "40mm", "20mm", "5mm", ret.json.XMJE7);				
				LODOP.ADD_PRINT_TEXT("47mm", "13mm","26mm", "5mm", ret.json.XMMC10);
				LODOP.ADD_PRINT_TEXT("47mm", "40mm", "20mm", "5mm", ret.json.XMJE10);
				LODOP.ADD_PRINT_TEXT("52mm", "13mm","26mm", "5mm", ret.json.XMMC13);
				LODOP.ADD_PRINT_TEXT("52mm", "40mm", "20mm", "5mm", ret.json.XMJE13);				
				LODOP.ADD_PRINT_TEXT("57mm", "13mm","26mm", "5mm", ret.json.XMMC16);
				LODOP.ADD_PRINT_TEXT("57mm", "40mm", "20mm", "5mm", ret.json.XMJE16);
				
				LODOP.ADD_PRINT_TEXT("32mm", "68mm", "26mm", "5mm", ret.json.XMMC2);
				LODOP.ADD_PRINT_TEXT("32mm", "96mm", "20mm", "5mm", ret.json.XMJE2);
				LODOP.ADD_PRINT_TEXT("37mm", "68mm", "26mm", "5mm", ret.json.XMMC5);
				LODOP.ADD_PRINT_TEXT("37mm", "96mm", "20mm", "5mm", ret.json.XMJE5);
				LODOP.ADD_PRINT_TEXT("42mm", "68mm", "26mm", "5mm", ret.json.XMMC8);
				LODOP.ADD_PRINT_TEXT("42mm", "96mm", "20mm", "5mm", ret.json.XMJE8);				
				LODOP.ADD_PRINT_TEXT("47mm", "68mm", "26mm", "5mm", ret.json.XMMC11);
				LODOP.ADD_PRINT_TEXT("47mm", "96mm", "20mm", "5mm", ret.json.XMJE11);
				LODOP.ADD_PRINT_TEXT("52mm", "68mm", "26mm", "5mm", ret.json.XMMC14);
				LODOP.ADD_PRINT_TEXT("52mm", "96mm", "20mm", "5mm", ret.json.XMJE14);				
				LODOP.ADD_PRINT_TEXT("57mm", "68mm", "26mm", "5mm", ret.json.XMMC17);
				LODOP.ADD_PRINT_TEXT("57mm", "96mm", "20mm", "5mm", ret.json.XMJE17);

				LODOP.ADD_PRINT_TEXT("32mm", "124mm", "26mm", "5mm", ret.json.XMMC3);
				LODOP.ADD_PRINT_TEXT("32mm", "151mm", "20mm", "5mm", ret.json.XMJE3);
				LODOP.ADD_PRINT_TEXT("37mm", "124mm", "26mm", "5mm", ret.json.XMMC6);
				LODOP.ADD_PRINT_TEXT("37mm", "151mm", "20mm", "5mm", ret.json.XMJE6);
				LODOP.ADD_PRINT_TEXT("42mm", "124mm", "26mm", "5mm", ret.json.XMMC9);
				LODOP.ADD_PRINT_TEXT("42mm", "151mm", "20mm", "5mm", ret.json.XMJE9);				
				LODOP.ADD_PRINT_TEXT("47mm", "124mm", "26mm", "5mm", ret.json.XMMC12);
				LODOP.ADD_PRINT_TEXT("47mm", "151mm", "20mm", "5mm", ret.json.XMJE12);
				LODOP.ADD_PRINT_TEXT("52mm", "124mm", "26mm", "5mm", ret.json.XMMC15);
				LODOP.ADD_PRINT_TEXT("52mm", "151mm", "20mm", "5mm", ret.json.XMJE15);				
				LODOP.ADD_PRINT_TEXT("57mm", "124mm", "26mm", "5mm", ret.json.XMMC18);
				LODOP.ADD_PRINT_TEXT("57mm", "151mm", "20mm", "5mm", ret.json.XMJE18);
				//小计金额
				LODOP.ADD_PRINT_TEXT("62mm", "13mm", "20mm", "5mm", "小计：");
				LODOP.ADD_PRINT_TEXT("62mm", "40mm", "40mm", "5mm", ret.json.XJJE1);
				LODOP.ADD_PRINT_TEXT("62mm", "96mm", "40mm", "5mm", ret.json.XJJE2);				
				LODOP.ADD_PRINT_TEXT("62mm", "151mm", "40mm", "5mm", ret.json.XJJE3);
				
				LODOP.ADD_PRINT_TEXT("68mm", "30mm", "80mm", "6mm", ret.json.DXZJE);
				LODOP.ADD_PRINT_TEXT("68mm", "120mm", "30mm", "6mm", ret.json.FYHJ);
				
				LODOP.ADD_PRINT_TEXT("74mm", "30mm", "20mm", "5mm", ret.json.JKHJ);
				//LODOP.ADD_PRINT_TEXT("74mm", "85mm", "20mm", "5mm", ret.json.BJJE);
				LODOP.ADD_PRINT_TEXT("74mm", "85mm", "20mm", "5mm", ret.json.BJXJ);//zhaojian 2017-09-29 解决补缴金额不显示问题
				LODOP.ADD_PRINT_TEXT("74mm", "130mm", "20mm", "5mm", ret.json.CYTK);

				LODOP.ADD_PRINT_TEXT("80mm", "30mm", "20mm", "5mm", ret.json.YBHJ);
				LODOP.ADD_PRINT_TEXT("80mm", "65mm", "20mm", "5mm", ret.json.GRZHZF);
				LODOP.ADD_PRINT_TEXT("80mm", "95mm", "20mm", "5mm", ret.json.QTYBZF);
				LODOP.ADD_PRINT_TEXT("80mm", "135mm", "20mm", "5mm", ret.json.ZFJE);
				LODOP.ADD_PRINT_TEXT("68mm", "145mm", "30mm", "16mm", ret.json.BZ);
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 7);
				
				LODOP.ADD_PRINT_TEXT("85mm", "30mm", "60mm", "5mm", ret.json.JGMC);
				LODOP.ADD_PRINT_TEXT("85mm", "100mm", "20mm", "5mm", ret.json.SYY);
				LODOP.ADD_PRINT_TEXT("85mm", "126mm", "10mm", "5mm", ret.json.N);
				LODOP.ADD_PRINT_TEXT("85mm", "138mm", "8mm", "5mm", ret.json.Y);
				LODOP.ADD_PRINT_TEXT("85mm", "148mm", "8mm", "5mm", ret.json.R);
				LODOP.PREVIEW();
				
			},
			doPrintSet : function(){
				var LODOP=getLodop();  

				LODOP.PRINT_INITA(3,8,643,680,"住院发票套打");
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.ADD_PRINT_TEXT(29,70,160,"5mm","发票号码");
				LODOP.ADD_PRINT_TEXT(55,70,100,"5mm","住院号");
				LODOP.ADD_PRINT_TEXT(55,410,40,"5mm","年");
				LODOP.ADD_PRINT_TEXT(55,470,40,"5mm","月");
				LODOP.ADD_PRINT_TEXT(55,530,40,"5mm","日");
				LODOP.ADD_PRINT_TEXT(80,70,160,"5mm","入院类别");
				LODOP.ADD_PRINT_TEXT(80,410,160,"5mm","工作单位");
				LODOP.ADD_PRINT_TEXT(105,70,160,"5mm","病人姓名");
				LODOP.ADD_PRINT_TEXT(105,410,160,"5mm","住院日期");
				LODOP.ADD_PRINT_TEXT(140,70,100,"5mm","收费项目1");
				LODOP.ADD_PRINT_TEXT(165,70,100,"5mm","收费项目2");
				LODOP.ADD_PRINT_TEXT(190,70,100,"5mm","收费项目3");
				LODOP.ADD_PRINT_TEXT(215,70,100,"5mm","收费项目4");
				LODOP.ADD_PRINT_TEXT(240,70,100,"5mm","收费项目5");
				LODOP.ADD_PRINT_TEXT(265,70,100,"5mm","收费项目6");
				LODOP.ADD_PRINT_TEXT(290,70,100,"5mm","收费项目7");
				LODOP.ADD_PRINT_TEXT(315,70,100,"5mm","收费项目8");
				LODOP.ADD_PRINT_TEXT(340,70,100,"5mm","收费项目9");
				LODOP.ADD_PRINT_TEXT(365,70,100,"5mm","收费项目10");
				LODOP.ADD_PRINT_TEXT(390,70,100,"5mm","收费项目11");
				LODOP.ADD_PRINT_TEXT(415,70,100,"5mm","收费项目12");
				LODOP.ADD_PRINT_TEXT(440,70,100,"5mm","收费项目13");
				LODOP.ADD_PRINT_TEXT(465,70,100,"5mm","收费项目14");
				LODOP.ADD_PRINT_TEXT(490,70,100,"5mm","收费项目15");
				LODOP.ADD_PRINT_TEXT(515,70,100,"5mm","收费项目16");
				LODOP.ADD_PRINT_TEXT(140,175,100,"5mm","项目金额1");
				LODOP.ADD_PRINT_TEXT(165,175,100,"5mm","项目金额2");
				LODOP.ADD_PRINT_TEXT(190,175,100,"5mm","项目金额3");
				LODOP.ADD_PRINT_TEXT(215,175,100,"5mm","项目金额4");
				LODOP.ADD_PRINT_TEXT(240,175,100,"5mm","项目金额5");
				LODOP.ADD_PRINT_TEXT(265,175,100,"5mm","项目金额6");
				LODOP.ADD_PRINT_TEXT(290,175,100,"5mm","项目金额7");
				LODOP.ADD_PRINT_TEXT(315,175,100,"5mm","项目金额8");
				LODOP.ADD_PRINT_TEXT(340,175,100,"5mm","项目金额9");
				LODOP.ADD_PRINT_TEXT(365,175,100,"5mm","项目金额10");
				LODOP.ADD_PRINT_TEXT(390,175,100,"5mm","项目金额11");
				LODOP.ADD_PRINT_TEXT(415,175,100,"5mm","项目金额12");
				LODOP.ADD_PRINT_TEXT(440,175,100,"5mm","项目金额13");
				LODOP.ADD_PRINT_TEXT(465,175,100,"5mm","项目金额14");
				LODOP.ADD_PRINT_TEXT(490,175,100,"5mm","项目金额15");
				LODOP.ADD_PRINT_TEXT(515,175,100,"5mm","项目金额16");
				LODOP.ADD_PRINT_TEXT(515,280,100,"5mm","费用合计");
				LODOP.ADD_PRINT_TEXT(540,114,317,"5mm","自费金额");
				LODOP.ADD_PRINT_TEXT(565,70,500,65,"备注");
				LODOP.ADD_PRINT_TEXT(635,450,120,"5mm","收费员");
				LODOP.ADD_PRINT_TEXT(140,470,100,"5mm","自费金额");
				LODOP.ADD_PRINT_TEXT(165,470,100,"5mm","本年账户支付");
				LODOP.ADD_PRINT_TEXT(190,470,100,"5mm","历年账户支付");
				LODOP.ADD_PRINT_TEXT(215,470,100,"5mm","医保总和");
				LODOP.ADD_PRINT_TEXT(240,470,100,"5mm","医保合计");
				LODOP.ADD_PRINT_TEXT(265,470,100,"5mm","缴款合计");
				LODOP.ADD_PRINT_TEXT(290,470,100,"5mm","合计金额");
				LODOP.ADD_PRINT_TEXT(315,470,100,"5mm","出院补缴");
				LODOP.ADD_PRINT_TEXT(340,470,100,"5mm","补缴现金");
				LODOP.ADD_PRINT_TEXT(365,470,100,"5mm","出院退款");
				LODOP.ADD_PRINT_TEXT(390,470,100,"5mm","退款现金");
				LODOP.PRINT_SETUP();
			}
	
		});
