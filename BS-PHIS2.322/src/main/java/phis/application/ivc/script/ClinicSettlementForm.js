$package("phis.application.ivc.script")

$import("phis.script.TableForm",
		"phis.script.util.FileIoUtil",
		"phis.script.Phisinterface",
		"phis.script.DatamatrixReader",
		"phis.application.pay.script.PayCommon")

phis.application.ivc.script.ClinicSettlementForm = function(cfg) {
	this.width = 280;
	this.height = 335;
	cfg.modal = this.modal = true;
	Ext.apply(this,phis.script.Phisinterface);
	Ext.apply(this,phis.script.DatamatrixReader);
	phis.application.ivc.script.ClinicSettlementForm.superclass.constructor.apply(this,
			[cfg])
	this.isFillYbFee = false;
}
Ext.extend(phis.application.ivc.script.ClinicSettlementForm, phis.script.TableForm, {
	initPanel : function(sc) {
		if (this.form) {
			if (!this.isCombined) {
				this.addPanelToWin();
			}
			return this.form;
		}
		this.form = new Ext.FormPanel({
					labelWidth : 85, // label settings here cascade
					frame : true,
					bodyStyle : 'padding:5px 5px 0',
					width : 280,
					height : 335,
					buttonAlign : 'center',
					items : [{
								xtype : 'fieldset',
								title : '发票情况',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 1,
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
								items : this.getItems('FPQK')
							}, {
								xtype : 'fieldset',
								title : '收款金额',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 1,
									tableAttrs : {
										border : 0,
										cellpadding : '2',
										cellspacing : "2"
									}
								},
								defaultType : 'textfield',
								items : this.getItems('SKJE')
							}],
					buttons : [{
								cmd : 'commit',
								text : '确定',
								iconCls : "save",
								handler : this.doCommit,
								scope : this
							}, {
								text : '取消',
								handler : this.doConcel,
								iconCls : "common_cancel",
								scope : this
							}]
				});
		if (!this.isCombined) {
			this.addPanelToWin();
		}
		var schema = sc
		if (!schema) {
			var re = util.schema.loadSync(this.entryName)
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel)
				return;
			}
		}
		this.schema = schema;
		this.combo = this.form.getForm().findField("FFFS");
		this.combo.on("select", this.calYSK, this);
		this.combo.getStore().on('load', this.calYSKLoadEvent, this);
		/*****begin 结算界面增加扫码支付 zhaojian 2018-08-24 *****/
		var ewm = this.form.getForm().findField("ewm");
		ewm.on("focus", this.onEWMfocus, this);
		ewm.on("blur", this.onEWMblur, this);
		ewm.on("change", this.onEWMchange, this);
		/*****end 结算界面增加扫码支付 zhaojian 2018-08-24 *****/
		return this.form
	},
	// 回显付费方式事件
	calYSKLoadEvent : function(store, records) {
		var delcount = 0;
		//过滤付款方式下拉框选项，只保留 现金、微信、支付宝
		store.each(function(item, index) {
			if(item.data.key!=1 && item.data.key!=30){
				store.removeAt(index+delcount);
				delcount --;
			}
		});
		var defIndex;
		store.each(function(item, index) {
					if (item.json.MRBZ == "1") {
						defIndex = index;
						this.combo.setValue(item.data.key)
						store.un('load', this.calYSKLoadEvent, this);
						return false;
					}
				}, this);

	},
	// 根据前台选择付款方式的类型从后台计算相应应收款.
	calYSK : function(comb, record, rownum) {
		this.form.getForm().findField("ewm").setValue("");
		if(!this.MRFFFS){
			this.MRFFFS = record.json;
		}
		this.FKFS = record.json;
		var payType = {};
		Ext.apply(payType, record.json);
		payType.YSK = this.form.getForm().findField("ZFJE").getValue()
				.toString();
		if(!payType.YSK){
			return
		}
		var result = phis.script.rmi.miniJsonRequestSync({
					serviceAction : "queryPayTypes",
					serviceId : "clinicChargesProcessingService",
					payType : Ext.encode(payType)
				});
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return
		}
		var ysk = result.json.YSK;
		if (!Ext.isEmpty(ysk)) {
			this.form.getForm().findField("YSK").setValue(ysk);
		}
		var jkje = this.form.getForm().findField("JKJE");
		jkje.setValue(ysk);
		this.form.getForm().findField("TZJE").setValue("0");
		//jkje.focus(false, 200)
		if(this.combo.value==30){
			this.form.getForm().findField("ewm").focus(false, 100);
		}
	},	
	onEWMchange : function(field){
		if(field.getValue().length==18){
			if(this.combo.value != 30){
				Ext.Msg.alert("提示", "付款方式有误，请重新选择！",
						function() {
							field.setValue("");
							this.running = false;
						}, this);
				return;
			}
			QRCODE = field.getValue();
			this.form.el.unmask();
			this.running = false;
			this.doCommit();
		}
	},
	/*****begin 结算界面增加扫码支付 zhaojian 2018-08-24 *****/
	onEWMfocus : function(){
		//判定付款方式是否为微信或支付宝
		if(this.combo.value==1){
			return;
		}
		this.form.el.mask("等待病人扫描付款码...", "x-mask-loading");	
		this.initHtmlElement();
	},
	onEWMblur : function(){
		if(this.combo.value==1){
			return;
		}
		this.form.el.unmask();
	},
	stopReadTask : function(){
		Ext.TaskMgr.stopAll();
	},
	/*****end 结算界面增加扫码支付 zhaojian 2018-08-24 *****/
	focusFieldAfter : function(index, delay) {
		if (this.running) {
			return;
		}
		this.running = true;
		var form = this.form.getForm();
		var jkje = form.findField("JKJE");
		var ysk = form.findField("YSK");
		if(this.MZXX && this.MZXX.BRXZ && this.MZXX.BRXZ==3000){
			this.focusOutField();
		}
		if (!jkje.getValue()) {
			form.findField("JKJE").setValue(ysk.getValue());
			form.findField("TZJE").setValue("0");
		} else {
			if (parseFloat(jkje.getValue()) < parseFloat(ysk.getValue())) {
				jkje.setValue("");
				Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!", function() {
							form.findField("TZJE").setValue("0");
							jkje.focus(false, 200);
							this.running = false;
						}, this);
				return;
			} else {
				var tzje = form.findField("TZJE");
				tzje.setValue(jkje.getValue() - ysk.getValue());
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
						this.running = false;
						return;
					}
				}
			}
		}
		this.running = false;
	},
	doCommit : function() {
		if (this.running || this.runningTask) {
			return;
		}
		this.running = true;
		var body = {};
		body.data = this.data;
		body.MZXX = this.MZXX;
		var form = this.form.getForm();
		/*****begin 结算界面增加扫码支付 zhaojian 2018-08-24 *****/
		if((this.combo.value==30) && (typeof(QRCODE)=='undefined' || QRCODE=="")){
			Ext.Msg.alert("提示", "未获取到付款码，请扫码支付！",
					function() {
						form.findField("ewm").setValue("");
						form.findField("ewm").focus(false,200);
						this.running = false;
					}, this);
			return;
		}
		/*****end 结算界面增加扫码支付 zhaojian 2018-08-24 *****/		
		if(!this.isFillYbFee && this.MZXX.BRXZ && this.MZXX.BRXZ==3000){//读取医保结算文件
			this.focusOutField();
			return;
		}
		var jkje = form.findField("JKJE");
		if (jkje.getValue() > 9999999.99) {
			Ext.Msg.alert("提示", "交款金额最大不能大于等于1000万!", function() {
						jkje.setValue()
						form.findField("TZJE").setValue("0");
						jkje.focus(false, 200);
						this.running = false;
					}, this);
			return;
		}
		var ysk = form.findField("YSK");
		if (!jkje.getValue()) {
			form.findField("JKJE").setValue(ysk.getValue());
			form.findField("TZJE").setValue("0");
		} else {
			if (parseFloat(jkje.getValue()) < parseFloat(ysk.getValue())) {
				jkje.setValue("");
				Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!", function() {
							form.findField("TZJE").setValue("0");
							jkje.focus(false, 200);
							this.running = false;
						}, this);
				return;
			} else {
				var tzje = form.findField("TZJE");
				tzje.setValue(jkje.getValue() - ysk.getValue());
			}
		}
		this.MZXX.FPHM = form.findField("FPHM").getValue();// 发票号码
		this.MZXX.JKJE = form.findField("JKJE").getValue();// 交款金额
		this.MZXX.TZJE = form.findField("TZJE").getValue();// 退找金额
		this.MZXX.ZFJE = form.findField("ZFJE").getValue();// 自负金额
		this.MZXX.ZJJE = form.findField("ZJJE").getValue();// 总计金额
		this.MZXX.YSK = form.findField("YSK").getValue();// 应收款
		this.MZXX.JJZF = form.findField("JJZF").getValue();// 其他应收
		this.MZXX.ZHZF = form.findField("ZHZF").getValue();// 帐户支付
		body.FFFS = form.findField("FFFS").getValue();// 付款方式
		if(body.FFFS==""){
			MyMessageTip.msg("提示", "请选择付款方式!", true);
			this.running = false;
			return;
		}
		this.saving = true
		var _ctr = this;
		
		if (this.jsData) {//如果是医保
			this.doYbmzjs();//医保确认结算
		} else {
			if(this.MZXX.BRXZ && this.MZXX.BRXZ==3000){//读取医保结算文件
				var fileData = {};
				try{
					var xmlDoc = loadXmlDoc("c:\\njyb\\mzjshz.xml");
					var elements = xmlDoc.getElementsByTagName("RECORD");
					for (var i = 0; i < elements.length; i++) {
						fileData.XM = (elements[i].getElementsByTagName("XM")[0].firstChild)?(elements[i].getElementsByTagName("XM")[0].firstChild.nodeValue):"";
						fileData.RYXZ = (elements[i].getElementsByTagName("RYXZ")[0].firstChild)?(elements[i].getElementsByTagName("RYXZ")[0].firstChild.nodeValue):"";
						fileData.ZFY = (elements[i].getElementsByTagName("ZFY")[0].firstChild)?(elements[i].getElementsByTagName("ZFY")[0].firstChild.nodeValue):0;
						fileData.YF = (elements[i].getElementsByTagName("YF")[0].firstChild)?(elements[i].getElementsByTagName("YF")[0].firstChild.nodeValue):0;
						fileData.XMF = (elements[i].getElementsByTagName("XMF")[0].firstChild)?(elements[i].getElementsByTagName("XMF")[0].firstChild.nodeValue):0;
						fileData.GRZL = (elements[i].getElementsByTagName("GRZL")[0].firstChild)?(elements[i].getElementsByTagName("GRZL")[0].firstChild.nodeValue):0;
						fileData.GRZF = (elements[i].getElementsByTagName("GRZF")[0].firstChild)?(elements[i].getElementsByTagName("GRZF")[0].firstChild.nodeValue):0;
						fileData.YBZF = (elements[i].getElementsByTagName("YBZF")[0].firstChild)?(elements[i].getElementsByTagName("YBZF")[0].firstChild.nodeValue):0;
						fileData.ZHZF = (elements[i].getElementsByTagName("ZHZF")[0].firstChild)?(elements[i].getElementsByTagName("ZHZF")[0].firstChild.nodeValue):0;
						fileData.ZHYE = (elements[i].getElementsByTagName("ZHYE")[0].firstChild)?(elements[i].getElementsByTagName("ZHYE")[0].firstChild.nodeValue):0;
						fileData.XJZF = (elements[i].getElementsByTagName("XJZF")[0].firstChild)?(elements[i].getElementsByTagName("XJZF")[0].firstChild.nodeValue):0;
						fileData.FYLB = (elements[i].getElementsByTagName("FYLB")[0].firstChild)?(elements[i].getElementsByTagName("FYLB")[0].firstChild.nodeValue):"";
						fileData.DJH = (elements[i].getElementsByTagName("DJH")[0].firstChild)?(elements[i].getElementsByTagName("DJH")[0].firstChild.nodeValue):"";
						fileData.XZMC = (elements[i].getElementsByTagName("XZMC")[0].firstChild)?(elements[i].getElementsByTagName("XZMC")[0].firstChild.nodeValue):"";
						fileData.YH1 = (elements[i].getElementsByTagName("YH1")[0].firstChild)?(elements[i].getElementsByTagName("YH1")[0].firstChild.nodeValue):0;
						fileData.YH2 = (elements[i].getElementsByTagName("YH2")[0].firstChild)?(elements[i].getElementsByTagName("YH2")[0].firstChild.nodeValue):0;
						fileData.YH3 = (elements[i].getElementsByTagName("YH3")[0].firstChild)?(elements[i].getElementsByTagName("YH3")[0].firstChild.nodeValue):0;
						fileData.TCZF = (elements[i].getElementsByTagName("TCZF")[0].firstChild)?(elements[i].getElementsByTagName("TCZF")[0].firstChild.nodeValue):0;
						fileData.DBZF = (elements[i].getElementsByTagName("DBZF")[0].firstChild)?(elements[i].getElementsByTagName("DBZF")[0].firstChild.nodeValue):0;
						fileData.BZZF = (elements[i].getElementsByTagName("BZZF")[0].firstChild)?(elements[i].getElementsByTagName("BZZF")[0].firstChild.nodeValue):0;
						fileData.MZBZ = (elements[i].getElementsByTagName("MZBZ")[0].firstChild)?(elements[i].getElementsByTagName("MZBZ")[0].firstChild.nodeValue):0;
						fileData.QHMC = (elements[i].getElementsByTagName("QHMC")[0].firstChild)?(elements[i].getElementsByTagName("QHMC")[0].firstChild.nodeValue):"";
						body.FILEDATA=fileData;
					}
				}catch(e){
					MyMessageTip.msg("提示", "文件解析失败!", true);
					return;
				}
				if(fileData.XM!="" && this.MZXX.BRXM!="" && fileData.XM!=this.MZXX.BRXM){//add by lizhi 2017-07-11增加姓名判断
					this.form.el.unmask()
					this.saving = false
					MyMessageTip.msg("提示", "请先去医保系统结算!", true);
					this.running = false;
					this.isFillYbFee = false;
					return;
				}
			}//南京金保结算
			else if(this.MZXX.BRXZ && this.MZXX.BRXZ==2000){
				if(this.MZXX.njjbyjsxx){
					var NJJBJSXX=this.MZXX.njjbyjsxx.NJJBYJSXX;
					NJJBJSXX.DJH=this.MZXX.FPHM;
					//this.addPKPHISOBJHtmlElement();
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
					//主动产生交易流水号，用于保存
					var jylsh=this.buildjylsh();
					NJJBJSXX.JYLSH=jylsh;
					var mzjsstr=this.buildstr("2410",this.ywzqh,NJJBJSXX);
					var remzjs=this.drinterfacebusinesshandle(mzjsstr);
					var mzjsarr=remzjs.split("^");
						var njjbmzjsxx={};
						if(mzjsarr[0]==0){
							var remzjs=mzjsarr[2].split("|");
							njjbmzjsxx.BCYLFZE=remzjs[0];//本次医疗费总额
							njjbmzjsxx.BCTCZFJE=remzjs[1];//本次统筹支付金额
							njjbmzjsxx.BCDBJZZF=remzjs[2];//本次大病救助支付
							njjbmzjsxx.BCDBBXZF=remzjs[3];//本次大病保险支付
							njjbmzjsxx.BCMZBZZF=remzjs[4];//本次民政补助支付
							njjbmzjsxx.BCZHZFZE=remzjs[5];//本次帐户支付总额
							njjbmzjsxx.BCXZZFZE=remzjs[6];//本次现金支付总额
							njjbmzjsxx.BCZHZFZF=remzjs[7];//本次帐户支付自付
							njjbmzjsxx.BCZHZFZL=remzjs[8];//本次帐户支付自理
							njjbmzjsxx.BCXJZFZF=remzjs[9];//本次现金支付自付
							njjbmzjsxx.BCXJZFZL=remzjs[10];//本次现金支付自理
							njjbmzjsxx.YBFWNFY=remzjs[11];//医保范围内费用
							njjbmzjsxx.ZHXFHYE=remzjs[12];//帐户消费后余额
							njjbmzjsxx.DBZBZBM=remzjs[13];//单病种病种编码
							njjbmzjsxx.SMXX=remzjs[14];//说明信息
							njjbmzjsxx.YFHJ=remzjs[15];//药费合计
							njjbmzjsxx.ZLXMFHJ=remzjs[16];//诊疗项目费合计
							njjbmzjsxx.BBZF=remzjs[17];//补保支付
							njjbmzjsxx.YLLB=remzjs[18];//医疗类别
							njjbmzjsxx.BY6=remzjs[19];//备用6
							njjbmzjsxx.LSH=NJJBJSXX.NJJBLSH;
							njjbmzjsxx.JYLSH=NJJBJSXX.JYLSH;
							njjbmzjsxx.FPHM=this.MZXX.FPHM;
							body.njjbmzjsxx=njjbmzjsxx;
						}else{
							MyMessageTip.msg("金保结算提示",mzjsarr[3], true);
							return;
						}
				}else{
					MyMessageTip.msg("提示：","金保预结算信息没获取到，请重新结算！", true);
					return;
				}
			}				
			this.BODY = body;
			/*****begin 挂号结算界面增加扫码支付 zhaojian 2018-08-24 *****/	
			//微信或支付宝付款成功后再继续HIS中的结算流程
			if((this.combo.value==30) && typeof(QRCODE)!='undefined' && QRCODE!=""){
				body.FFFS = getFKFS(QRCODE);
				var paydata ={};
				paydata.PAYSERVICE = "2";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
				paydata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP
				paydata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
				//paydata.IP = "192.168.9.71";//测试时使用
				//paydata.COMPUTERNAME = "DPSF01";//测试时使用
				paydata.HOSPNO = generateHospno("SF",this.MZXX.FPHM);//医院流水号
				this.MZXX.HOSPNO = paydata.HOSPNO;
				paydata.PAYMONEY = this.MZXX.JKJE;//支付金额
				paydata.VOUCHERNO = this.MZXX.FPHM;//就诊号码或发票号码
				paydata.ORGANIZATIONCODE = this.mainApp.deptId;//机构编码
				//paydata.ORGANIZATIONCODE = "320124003";//测试时使用
				paydata.PATIENTYPE = this.MZXX.BRXZ;//病人性质
				paydata.PATIENTID = this.MZXX.BRID;//病人id
				paydata.NAME = this.MZXX.BRXM;//病人姓名
				paydata.SEX = this.MZXX.BRXB;//性别
				paydata.IDCARD = this.MZXX.SFZH;//身份证号
				paydata.BIRTHDAY = this.MZXX.CSNY;//出生年月
				paydata.AUTH_CODE = QRCODE;//支付条码
				paydata.PAYSOURCE = "1";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
				paydata.TERMINALNO = "";//支付终端号 如POS01、1号窗
				paydata.PAYNO = "";//支付账号
				paydata.COLLECTFEESCODE = this.mainApp.uid;//操作员代码
				paydata.COLLECTFEESNAME = this.mainApp.uname;//操作员姓名
				//paydata.COLLECTFEESCODE = "0310581X";//测试时使用
				//paydata.COLLECTFEESNAME = "管理员";//测试时使用		
				paydata.STATUS = "0"//订单状态，0初始订单、1订单完成、2、订单关闭 3、订单失败
				paydata.PAYTYPE = getPaytype(QRCODE);//1支付宝 2微信
				QRCODE="";
				this.running = true;
				SubmitOrder(paydata,this);
				return;
			}
			/*****end 挂号结算界面增加扫码支付 zhaojian 2018-08-24 *****/	
			QRCODE="";
			this.doCommit2();
		}
	},
	doCommit2 : function() {			
		this.form.el.mask("正在保存数据...", "x-mask-loading")
		phis.script.rmi.jsonRequest({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "saveOutpatientSettlement",
					body : this.BODY
				}, function(code, msg, json) {
					this.JSON = json;//移动支付 zhaojian 2018-09-17
					this.form.el.unmask()
					this.saving = false
					if (code > 300) {
						if(code==902){
							MyMessageTip.msg("友情提示","本机构启用了药房排队叫号，但执行失败，请联系管理员！", true);
						}					
						this.processReturnMsg(code, msg, this.saveToServer,[this.BODY]);
						this.fireEvent("settlement", this);
						this.doConcel();					
						/*****begin 挂号结算界面增加扫码支付 zhaojian 2018-09-17 *****/
						//如果本地失败，调用支付平台取消微信支付宝付款	
						if(this.combo.value==30){
							var refunddata ={};
							refunddata.PAYSERVICE = "-2";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
							refunddata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP			
							refunddata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
							//refunddata.IP = "192.168.9.71";//测试时使用
			 				//refunddata.COMPUTERNAME = "DPSF01";//测试时使用
							refunddata.HOSPNO = generateHospno("TF",this.GHXX.JZHM);//医院流水号
							refunddata.PAYMONEY = this.MZXX.JKJE;//支付金额
							refunddata.VOUCHERNO = this.GHXX.JZHM;//就诊号码或发票号码
							refunddata.ORGANIZATIONCODE = this.mainApp.deptId;//机构编码
							//refunddata.ORGANIZATIONCODE = "320124003";//测试时使用
							refunddata.PATIENTYPE = this.GHXX.BRXZ;//病人性质
							refunddata.PATIENTID = this.GHXX.BRID;//病人id
							refunddata.NAME = this.GHXX.BRXM;//病人姓名
							refunddata.SEX = this.GHXX.BRXB;//性别
							refunddata.IDCARD = "";//身份证号
							refunddata.BIRTHDAY = this.GHXX.CSNY;//出生年月
							refunddata.PAYSOURCE = "1";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
							//refunddata.TERMINALNO = "";//支付终端号 如POS01、1号窗
							refunddata.COLLECTFEESCODE = this.mainApp.uid;//操作员代码
							refunddata.COLLECTFEESNAME = this.mainApp.uname;//操作员姓名	
							//refunddata.COLLECTFEESCODE = "0310581X";//测试时使用
							//refunddata.COLLECTFEESNAME = "管理员";//测试时使用		
							refunddata.HOSPNO_ORG = this.GHXX.HOSPNO;//退款交易时指向原HOSPNO
							refund(refunddata,this);
							return;
						}
						/*****end 挂号结算界面增加扫码支付 zhaojian 2018-09-17 *****/
						return;
					}
					this.doCommit3();
				}, this)
				this.running = false;
	},
	doCommit3: function(){
		this.opener.opener.JSXX = this.MZXX;
		if(this.opener.opener.MZXX){
			this.opener.opener.MZXX.mxsave = false;
		}
		this.fireEvent("settlementFinish", this);
		//清空本次病人收费信息
		this.fireEvent("settlement", this);
		this.doConcel();
		/**********以下是删除医保结算汇总文件************/
		this.delYbFiles();
		this.opener.doPrintFp(this.JSON.FPHM);//病历页面结算用
		this.opener.opener.opener.cfList.fphm = this.JSON.FPHM
	},
	doConcel : function() {
		win = this.opener.getWin();
		this.fireEvent("winClose", this);
		if (win)
			win.hide();
		/**********以下是删除医保结算汇总文件************/
		this.delYbFiles();
	},
	delYbFiles : function() {
		if(this.MZXX.BRXZ && this.MZXX.BRXZ==3000){//删除医保结算文件
			delXmlFile("c:\\njyb\\mzcfsj.xml");
			delXmlFile("c:\\njyb\\mzjzxx.xml");
			delXmlFile("c:\\njyb\\mzjshz.xml");
		}
	},
	getItems : function(para) {
		var ac = util.Accredit;
		var MyItems = [];
		var schema = null;
		var re = util.schema.loadSync(this.entryName)
		if (re.code == 200) {
			schema = re.schema;
		} else {
			this.processReturnMsg(re.code, re.msg, this.initPanel)
			return;
		}
		var items = schema.items
		for (var i = 0; i < items.length; i++) {
			var it = items[i]
			if (!it.layout || it.layout != para) {
				continue;
			}
			if ((it.display == 0 || it.display == 1) || !ac.canRead(it.acValue)) {
				continue;
			}
			var f = this.createField(it)
			f.labelSeparator = ":"
			f.index = i;
			f.anchor = it.anchor || "100%"
			delete f.width

			f.colspan = parseInt(it.colspan)
			f.rowspan = parseInt(it.rowspan)
			if (it.id == "JKJE") {
				f.style = 'font-size:16px;font-weight:bold;color:red;textAlign:right;';
			} else if (it.id == "YSK" || it.id == "TZJE") {
				f.style = 'background:#E6E6E6;cursor:default;font-size:16px;font-weight:bold;color:red;textAlign:right;';
			} else if (it.id == "ewm") {
				f.style = 'background:#E6E6E6;cursor:default;font-size:12px;color:gray;';
				f.emptyText = "请用鼠标点击此处激活扫码设备";
			}else if (it.id != "FPHM") {
				f.style = 'cursor:default;font-size:16px;font-weight:bold;color:blue;textAlign:right;';
			} 
			MyItems.push(f);
		}
		return MyItems;
	},
	setValue : function(datas, MZXX, jsData) {
		this.data = datas;
		this.MZXX = MZXX;
		this.jsData = jsData;
		if (jsData) {
			// 获取发票号码
			this.ybbz = 1;
			var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "clinicChargesProcessingService",
						serviceAction : "queryPayment",
						body : datas,
						jsxx : jsData
					});
		} else if(this.MZXX.NHYJSXX){
			var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "clinicChargesProcessingService",
						serviceAction : "queryPayment",
						body : datas,
						SUM01 : this.MZXX.NHYJSXX.SUM01
					});
		} else {
			// 获取发票号码
			this.ybbz = 0;
			var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "clinicChargesProcessingService",
						serviceAction : "queryPayment",
						body : datas
					});
		}
		if (!r.json.body) {
			this.jsxx = '';
			return;
		} else {
			this.jsxx = r.json.body;
		}

	},
	afterShow : function(thisform) {
		if (!thisform.jsxx) {
			Ext.Msg.alert("提示", "请先维护默认付款方式!", function() {
						thisform.doConcel();
					});
			return;
		}
		if (!thisform.jsxx.FPHM) {
			Ext.Msg.alert("提示", "请先维护发票号码!", function() {
						thisform.doConcel();
					});
			return;
		}
		if (!thisform.jsxx.lastFPHM) {
			Ext.Msg.alert("提示", "当前使用号段剩余发票已不够此次结算,"+'<br/>'+"请注销或使用完当前号段，并维护新号段再结算!", function() {
						thisform.doConcel();
					});
			return;
		}
		this.running = false;
		thisform.MZXX.FKFS = thisform.jsxx.FKFS
		thisform.MZXX.FPZS = thisform.jsxx.FPZS
		
		var form = thisform.form.getForm();
		form.findField("ZJJE").setValue(thisform.jsxx.ZJJE);
		form.findField("ZFJE").setValue(thisform.jsxx.ZFJE);
		form.findField("ZHZF").setValue("0.00");
		form.findField("YBZF").setValue("0.00");
		this.isFillYbFee = false;
		
		form.findField("YSK").setValue(thisform.jsxx.YSK);
		if (this.MRFFFS) {
			this.combo.setValue(this.MRFFFS.key)
			var payType = {};
			Ext.apply(payType, this.MRFFFS);
			payType.YSK = thisform.jsxx.ZFJE;
			var result = phis.script.rmi.miniJsonRequestSync({
						serviceAction : "queryPayTypes",
						serviceId : "clinicChargesProcessingService",
						payType : Ext.encode(payType)
					});
			if (result.code > 300) {
				this.processReturnMsg(result.code, result.msg);
				return
			}
			var ysk = result.json.YSK;
			if (!Ext.isEmpty(ysk)) {
				this.form.getForm().findField("YSK").setValue(ysk);
			}
		}
		form.findField("FPHM").setValue(thisform.jsxx.FPHM);
		if(this.MZXX.BRXZ && 
			(this.MZXX.BRXZ==1000 ||this.MZXX.BRXZ==6000||this.MZXX.BRXZ==5000)){
		form.findField("JJZF").setValue("0.00");
		if (thisform.jsxx.JJZF) {
			form.findField("JJZF").setValue(thisform.jsxx.JJZF);
		}
		if (form.findField("ZHZF")) {
			form.findField("ZHZF").setValue("0.00");
			if (thisform.jsxx.ZHZF) {
				form.findField("ZHZF").setValue(thisform.jsxx.ZHZF);
			}
		}
		}
		//莱斯医保
		var jkje = form.findField("JKJE");
		if(this.MZXX.BRXZ && this.MZXX.BRXZ==3000){
			jkje.on("specialkey",this.fillYbFee,this);
		}
		//南京金保
		if(this.MZXX.BRXZ && this.MZXX.BRXZ==2000){
			this.running=false;
			if(this.MZXX.njjbyjsxx){
				if(Math.abs(thisform.jsxx.ZJJE-this.MZXX.njjbyjsxx.BCYLFZE)>0.04){
					this.running=true;
				}else{
					var ybzf=parseFloat(this.MZXX.njjbyjsxx.BCTCZFJE)+parseFloat(this.MZXX.njjbyjsxx.BCDBJZZF)
					+parseFloat(this.MZXX.njjbyjsxx.BCDBBXZF)+parseFloat(this.MZXX.njjbyjsxx.BCMZBZZF);
					form.findField("ZHZF").setValue(this.MZXX.njjbyjsxx.BCZHZFZE);
					form.findField("YBZF").setValue(ybzf);
					form.findField("ZFJE").setValue(this.MZXX.njjbyjsxx.BCXZZFZE);
					form.findField("JJZF").setValue(parseFloat(this.MZXX.njjbyjsxx.BCYLFZE)-
						parseFloat(this.MZXX.njjbyjsxx.BCZHZFZE)-ybzf-parseFloat(this.MZXX.njjbyjsxx.BCXZZFZE));
					form.findField("YSK").setValue(this.MZXX.njjbyjsxx.BCXZZFZE);
				}
			}else{
				this.running=true;
			}
		}
		form.findField("JKJE").setValue("");
		form.findField("TZJE").setValue("");
		
		jkje.focus(false, 200);
	},
	fillYbFee : function(field, e){
		if (e.getKey() == 13 && !e.shiftKey) {
			if(this.isFillYbFee){//确定
				this.doCommit;
				this.isFillYbFee = false;
			}else{//以下是填充医保费用代码
					try{
						var xmlDoc = loadXmlDoc("c:\\njyb\\mzjshz.xml");
						var elements = xmlDoc.getElementsByTagName("RECORD");
						var form = this.form.getForm();
						for (var i = 0; i < elements.length; i++) {
							var ZFY = (elements[i].getElementsByTagName("ZFY")[0].firstChild)?(elements[i].getElementsByTagName("ZFY")[0].firstChild.nodeValue):0;
							var YBZF = (elements[i].getElementsByTagName("YBZF")[0].firstChild)?(elements[i].getElementsByTagName("YBZF")[0].firstChild.nodeValue):0;
							var ZHZF = (elements[i].getElementsByTagName("ZHZF")[0].firstChild)?(elements[i].getElementsByTagName("ZHZF")[0].firstChild.nodeValue):0;
							var XJZF = (elements[i].getElementsByTagName("XJZF")[0].firstChild)?(elements[i].getElementsByTagName("XJZF")[0].firstChild.nodeValue):0;
							var MZBZ = (elements[i].getElementsByTagName("MZBZ")[0].firstChild)?(elements[i].getElementsByTagName("MZBZ")[0].firstChild.nodeValue):0;
							form.findField("ZHZF").setValue(ZHZF);//账户支付
							form.findField("YBZF").setValue(YBZF);//医保支付
							form.findField("JJZF").setValue(parseFloat(YBZF)+parseFloat(MZBZ));//其他应收=医保支付+民政补助
							var zfje = form.findField("ZFJE");
							zfje.setValue(XJZF);
							var ysk = form.findField("YSK");
							ysk.setValue(XJZF);
							var jkje = form.findField("JKJE");
							jkje.setValue(ysk.getValue());
							var tzje = form.findField("TZJE");
							tzje.setValue(parseFloat(jkje.getValue()) - parseFloat(ysk.getValue()));
							this.isFillYbFee = true;
						}
						form.findField("JKJE").focus(false, 200);
					}catch(e){
						MyMessageTip.msg("提示", "文件解析失败!", true);
					}
			}
		}
	},
	focusOutField : function(){
		try{
			var xmlDoc = loadXmlDoc("c:\\njyb\\mzjshz.xml");
			var elements = xmlDoc.getElementsByTagName("RECORD");
			var form = this.form.getForm();
			for (var i = 0; i < elements.length; i++) {
				var ZFY = (elements[i].getElementsByTagName("ZFY")[0].firstChild)?(elements[i].getElementsByTagName("ZFY")[0].firstChild.nodeValue):0;
				var YBZF = (elements[i].getElementsByTagName("YBZF")[0].firstChild)?(elements[i].getElementsByTagName("YBZF")[0].firstChild.nodeValue):0;
				var ZHZF = (elements[i].getElementsByTagName("ZHZF")[0].firstChild)?(elements[i].getElementsByTagName("ZHZF")[0].firstChild.nodeValue):0;
				var XJZF = (elements[i].getElementsByTagName("XJZF")[0].firstChild)?(elements[i].getElementsByTagName("XJZF")[0].firstChild.nodeValue):0;
				var MZBZ = (elements[i].getElementsByTagName("MZBZ")[0].firstChild)?(elements[i].getElementsByTagName("MZBZ")[0].firstChild.nodeValue):0;
				form.findField("ZHZF").setValue(ZHZF);//账户支付
				form.findField("YBZF").setValue(YBZF);//医保支付
				form.findField("JJZF").setValue(parseFloat(YBZF)+parseFloat(MZBZ));//其他应收=医保支付+民政补助
				var zfje = form.findField("ZFJE");
				zfje.setValue(XJZF);
				var ysk = form.findField("YSK");
				ysk.setValue(XJZF);
				var jkje = form.findField("JKJE");
				var tzje = form.findField("TZJE");
				tzje.setValue(parseFloat(jkje.getValue()) - parseFloat(ysk.getValue()));
				this.isFillYbFee = true;
				this.running = false;
			}
		}catch(e){
			MyMessageTip.msg("提示", "文件解析失败!", true);
		}
	}
})