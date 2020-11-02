/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule",
		"phis.application.yb.script.MedicareCommonMethod",
		"phis.script.SMCardReader",
		"phis.script.Phisinterface","phis.script.DatamatrixReader",
		"phis.application.pay.script.PayCommon");

phis.application.ivc.script.ClinicFPZFChargesModule = function(cfg) {
	this.width = 1020;
	this.height = 550;
	cfg.modal = this.modal = true;
	Ext.apply(this,phis.script.Phisinterface);
	Ext.apply(this,phis.script.SMCardReader);
	Ext.apply(this,phis.application.yb.script.MedicareCommonMethod);
	Ext.apply(this,phis.script.DatamatrixReader);
	phis.application.ivc.script.ClinicFPZFChargesModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.ivc.script.ClinicFPZFChargesModule,
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
										height : 78,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}],
							tbar : (this.tbar || [])
									.concat(this.createButton())
						});
				this.panel = panel;
				return panel;
			},
			loadData : function(person) {
				this.formModule.MZXX = this.person;
				this.formModule.initDataId = this.initDataId;
				this.formModule.loadData();
				this.listModule.requestData.cnd = ['eq', ['$', 'c.FPHM'],
						["s", this.initDataId]];
				this.listModule.djs = this.djs;
				this.listModule.refresh();
				this.initYBServer(this.person.BRXZ);
				this.ybbhxx = this.person;
				if (this.ybxx) {
					this.ybxx = false;
				}
				var btns = this.panel.getTopToolbar();
				if (btns) {
					if (this.person.ZFPB) {
						var btn1 = btns.find("cmd", "fpzf");
						var btn2 = btns.find("cmd", "qxzf");
						if (btn1) {
							btn1[0].setDisabled(true);
						}
						if (btn2) {
							btn2[0].setDisabled(false);
						}
						if (!this.msgCt) {
							this.msgCt = Ext.DomHelper.insertFirst(
									this.listModule.grid.body, {
										id : 'charge-div',
										style : 'position:absolute;bottom:17px;right:5px;margin:0 auto;z-index:20001;'
									}, true);
						}
						if (this.m) {
							this.m.remove();
							this.m = Ext.DomHelper.append(this.msgCt, {
								html : "<img src='"
										+ ClassLoader.appRootOffsetPath
										+ "resources/phis/resources/images/cancelled.gif' />"
							}, true);
						} else {
							this.m = Ext.DomHelper.append(this.msgCt, {
								html : "<img src='"
										+ ClassLoader.appRootOffsetPath
										+ "resources/phis/resources/images/cancelled.gif' />"
							}, true);
						}
					} else {
						var btn1 = btns.find("cmd", "fpzf");
						var btn2 = btns.find("cmd", "qxzf");
						if (btn1) {
							btn1[0].setDisabled(false);
						}
						if (btn2) {
							btn2[0].setDisabled(true);
						}
						if (this.msgCt) {
							this.m.remove();
						}
					}
				}
			},
			doCflr : function() {
			},
			getForm : function() {
				var module = this.createModule("Form ", this.refForm);
				var formModule = module.initPanel();
				this.formModule = module;

				module.opener = this;
				var form = module.form.getForm()
				var f = form.findField("MZGL")
				if (f) {
					f.setDisabled(true);
				}
				var BRXB = form.findField("BRXB");
				BRXB.getStore().on("load", module.loadData, module);
				var BRXZ = form.findField("BRXZ");
				BRXZ.getStore().on("load", module.loadData, module);
				return formModule;
			},
			getList : function() {
				var module = this.createModule("List", this.refList);
				module.openby = "FPZF";
				var listModule = module.initPanel();
				this.listModule = module;
				module.opener = this;
				return listModule;
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
					buttons.push(btn, '-');
				}
				this.buttons = buttons;
				return buttons;
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
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			// 发票作废
			doFpzf : function() {
				var brxz=this.formModule.form.getForm().findField("BRXZ").getValue();
				var body = {};
				body["FPHM"] = this.initDataId;
				body.BRXZ=brxz;
				//发票预作废
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "validateBeforeVoidInvoice",
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (r.json.msg) {
						Ext.Msg.alert("提示", r.json.msg);
						return;
					}
				}
				if(brxz==6000){
					if(!this.zfnhdk || !this.zfnhdk=="1"){
						this.midiModules["nhdjlist"]=null;
						var nhdjmodule = this.createModule("nhdjlist", "phis.application.reg.REG/REG/REG0108");
						nhdjmodule.on("nhdkreturn", this.checkRecordExist, this);
						var win = nhdjmodule.getWin();
						win.add(nhdjmodule.initPanel());
						win.show();
						nhdjmodule.selectRow(0);
						return;
					}
				}
				//yx-2017-05-10-作废农合病人先读卡结束
				//yx-南京金保作废-b
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
						}
						else{
							Ext.Msg.alert("获取业务周期号失败！请确认是否签到。")
							return;
						}
					this.addPKPHISOBJHtmlElement();
					this.drinterfaceinit();
					var dkstr=this.buildstr("2100",this.ywzqh,"");
					var drdkre=this.drinterfacebusinesshandle(dkstr);
					var kxxarr=drdkre.split("^");
					if(kxxarr[0]=="0"){
						var canshu=kxxarr[2].split("|")
						if(this.person.SHBZKH==canshu[0]){
							//卡号一样可以进行下一步
							var NJJBZFXX={};
							//获取流水号
							var getbody={};
								getbody.ywlx="1";//挂号
								getbody.SBXH=this.person.GHGL;
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
							NJJBZFXX.DJH=this.initDataId;
							NJJBZFXX.JSCXRQ=new Date();
							NJJBZFXX.JBR=this.mainApp.uid;
							NJJBZFXX.SFBLCFBZ="0";
							NJJBZFXX.BY1="";
							NJJBZFXX.BY2="";
							NJJBZFXX.BY3="";
							NJJBZFXX.BY4="";
							NJJBZFXX.BY5="";
							NJJBZFXX.BY6="";	
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
						}else{
							MyMessageTip.msg("提示：","刷的卡与作废发票的卡号不一致，不能作废！", true);
							return;	
						}
					}else{
						MyMessageTip.msg("提示：",kxxarr[3], true);
						return;	
					}
				}
				//yx-南京金保作废-e
				// 发票作废前判断下是否有物资
				var body_mzfpzf = {};
				body_mzfpzf["FPHM"] = this.initDataId;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configLogisticsInventoryControlService",
							serviceAction : "verificationMzfpzf",
							body : body_mzfpzf
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doFpzf);
					return;
				}
				if(brxz==6000){
					//农合作废
					if(this.person.BXID && this.person.BXID.length >0){
					if(this.zfnhdk!="1"){
						MyMessageTip.msg("提示", "农合病人请先读卡！！！", true);
						return;
					}else {
						body.zfnhdk="1";
						body.nhkh=this.person.NHKH;
						body.bxid=this.person.BXID;
					}
				}
				}		
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "updateVoidInvoice",
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					this.zfnhdk="";
					if (r.json.msg) {
						Ext.Msg.alert("提示", r.json.msg);
						return;
					}
					this.opener.loadData();
					/*****begin 发票作废增加扫码支付退费功能 zhaojian 2018-09-18 *****/
					/*****增加app支付退费功能 zhaojian 2018-10-04 付款方式(35微信 36支付宝)*****/
					if(r.json.mzxx.FFFS==32 || r.json.mzxx.FFFS==33 || r.json.mzxx.FFFS==35 || r.json.mzxx.FFFS==36 || r.json.mzxx.FFFS==39 || r.json.mzxx.FFFS==40){
						var querydata ={};
					 	querydata.APIURL = getPayApiUrl("orderQuery");
						if(r.json.mzxx.FFFS==35 || r.json.mzxx.FFFS==36){
							querydata.IP = "127.0."+r.json.mzxx.JGID.substr(r.json.mzxx.JGID.length-1,1)+".1";//测试时使用
							querydata.COMPUTERNAME = "APP";//测试时使用
						}
						else{
							this.initHtmlElement();//获取客户端IP前先初始化插件
							//querydata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP
							//querydata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
							querydata.IP = "192.168.9.71";//测试时使用
							querydata.COMPUTERNAME = "DPSF01";//测试时使用
						}
					 	querydata.PAYSERVICE = "2";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
					 	querydata.PAYSERVICE_REFUND = "-2";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
						querydata.PATIENTID = r.json.mzxx.BRID+"";
						//querydata.ORGANIZATIONCODE = r.json.mzxx.JGID;
						querydata.ORGANIZATIONCODE = "320124003";//测试时使用
						querydata.VOUCHERNO = r.json.mzxx.FPHM;
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
									refunddata.PAYSERVICE = "-2";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
									if(getorders.json.body.orders[i].HOSPNO.indexOf("SF")<0){
										refunddata.IP = "127.0."+getorders.json.body.orders[i].ORGANIZATIONCODE.substr(getorders.json.body.orders[i].ORGANIZATIONCODE.length-1,1)+".1";//测试时使用
										refunddata.COMPUTERNAME = "APP";//测试时使用
									}else{
										//refunddata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP
										//refunddata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
										refunddata.IP = "192.168.9.71";//测试时使用
										refunddata.COMPUTERNAME = "DPSF01";//测试时使用
									}
									if(getorders.json.body.orders[i].HOSPNO_REFUND && getorders.json.body.orders[i].HOSPNO_REFUND!=""){
										refunddata.HOSPNO = getorders.json.body.orders[i].HOSPNO_REFUND;//医院流水号
									}
									else{
										refunddata.HOSPNO = generateHospno("TF",getorders.json.body.orders[i].VOUCHERNO);//医院流水号
									}
									refunddata.PAYMONEY = getorders.json.body.orders[i].PAYMONEY+"";//支付金额
									refunddata.VOUCHERNO = getorders.json.body.orders[i].VOUCHERNO;//就诊号码或发票号码
									//refunddata.ORGANIZATIONCODE = getorders.json.body.orders[i].ORGANIZATIONCODE;//机构编码
									refunddata.ORGANIZATIONCODE = "320124003";//测试时使用
									refunddata.PATIENTYPE = getorders.json.body.orders[i].PATIENTYPE;//病人性质
									refunddata.PATIENTID = getorders.json.body.orders[i].PATIENTID;//病人id
									refunddata.NAME = getorders.json.body.orders[i].NAME;//病人姓名
									refunddata.SEX = getorders.json.body.orders[i].SEX;//性别
									refunddata.IDCARD = getorders.json.body.orders[i].IDCARD;//身份证号
									refunddata.BIRTHDAY = getorders.json.body.orders[i].BIRTHDAY;//出生年月
									/*****增加app支付退费功能 zhaojian 2018-10-04 付款方式(35微信 36支付宝)*****/
									if(getorders.json.body.orders[i].HOSPNO.indexOf("SF")==0){
										refunddata.PAYSOURCE = "1";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
									}else if(getorders.json.body.orders[i].HOSPNO.indexOf("ZZ")==0){
								    /*****增加自组机退费功能 hujian 2020-04-28 自组机付款方式(39微信 40支付宝)*****/
										refunddata.PAYSOURCE = "2";
									}else{
										refunddata.PAYSOURCE = "3";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
									}
									//refunddata.TERMINALNO = "";//支付终端号 如POS01、1号窗
									refunddata.COLLECTFEESCODE = this.mainApp.uid;//操作员代码
									refunddata.COLLECTFEESNAME = this.mainApp.uname;//操作员姓名	
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
					//发票作废，家医签约自动解约
					var jymessage = phis.script.rmi.miniJsonRequestSync({
						serviceId : "clinicChargesProcessingService",
						serviceAction : "changeSCMStatus",
						body : {FPHM:this.initDataId,SIGNFLAG:"2"}
					});
					if(jymessage.code > 300){
						MyMessageTip.msg("提示", "家医自动解约失败！", true);
					}
				}
			},
			doCommit3 : function() {
					this.doCancel();
			},
			/*****end 发票作废增加扫码支付退费功能 zhaojian 2018-09-18 *****/	
			// 取消发票作废
			doQxzf : function() {
				debugger;
				//取消发票作废，家医签约恢复
				var jymessage = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "changeSCMStatus",
					body : {FPHM:this.initDataId,SIGNFLAG:"1"}
				});
				if(jymessage.code > 300){
					MyMessageTip.msg("提示", "家医恢复失败！", true);
				}
				
				// 医保相关代码
				var brxz=this.formModule.form.getForm().findField("BRXZ").getValue();
				//农合限制
				if(brxz=="6000"){
					MyMessageTip.msg("提示", "农合不允许取消作废！", true);
					return;
				}
				//南京金保限制
				if(brxz=="2000"){
					MyMessageTip.msg("提示", "医保不允许取消作废！", true);
					return;
				}
				var ybbrxz = this.getYbbrxz(brxz);// 查询当前病人信息 是否是医保
				if(ybbrxz==null){
				MyMessageTip.msg("提示", "查询病人是否医保失败", true);
				return;
				}
				if(ybbrxz>0){
						Ext.Msg.alert("提示", "医保病人发票作废后不能取消作废，!");
						return;
				}
				// 医保相关代码结束
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "updateCanceledVoidInvoice",
							body : this.initDataId
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (r.json.msg) {
						Ext.Msg.alert("提示", r.json.msg);
						return;
					}
					this.opener.loadData();
					this.doCancel();
				}
			},
			checkRecordExist : function(data) {
				var yysi = 1;
				if(data.nhdk && data.nhdk=="1"){
					this.nhdk="1";
				}
				if(data.NHKH && data.NHKH!=""){
					this.doCheck(data.NHKH);
				}
			},
			//农合刷卡作废功能
			doNhskzf:function(){
				//实现刷卡程序
				var v = window.location.pathname.split("/")
				var http = "http://"+window.location.host+"/"+v[1]+"/"+"xnhdk/ick_pk.html";
		        window.showModalDialog(http, this, "dialogWidth:200px; dialogHeight:50px;status:no; directories:yes;scrollbars:no;Resizable=no;dialogLeft:600px;dialogTop:400px;");
			},
			//农合读卡作废功能（火狐浏览器）  zhaojian 2017-09-21
			doNhskzf_hh:function(){				
				var _this = this;
				$.ajax({
        			type:'get',
        			url:"http://127.0.0.1:8888",
        			success:function(body,heads,status){
						//解决火狐浏览器读卡没有反应问题 zhaojian 2017-11-17
						try{
							_this.doCheck(body.split("|")[1]);
						}catch(e){
							_this.doCheck(status.responseText.split("|")[1]);
						}
        		},
        			error:function(XmlHttpRequest,textStatus, errorThrown){
  						alert("读卡失败："+XmlHttpRequest.responseText);
  				}
    			});
			},
			//市民卡读卡
			doSmkskzf:function(){
				this.initHtmlElement();
				this.doCheck(this.readCard());
			},
			doCheck:function(cardid){
				if(cardid==this.person.NHKH){
					this.zfnhdk="1";
					this.doFpzf();
				}else{
					this.zfnhdk="";
					MyMessageTip.msg("提示", "农合卡号不匹配，请校验退费发票与卡号信息！！！", true);
				}
			}	
		});