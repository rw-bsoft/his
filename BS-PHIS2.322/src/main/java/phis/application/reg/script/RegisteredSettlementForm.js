$package("phis.application.reg.script");

$import("phis.script.TableForm",
		"phis.application.yb.script.MedicareCommonMethod",
		"phis.script.Phisinterface","phis.script.DatamatrixReader",
		"phis.application.pay.script.PayCommon")

phis.application.reg.script.RegisteredSettlementForm = function(cfg) {
	cfg.modal = true;
	Ext.apply(this,phis.application.yb.script.MedicareCommonMethod);
	Ext.apply(this,phis.script.Phisinterface);
	Ext.apply(this,phis.script.DatamatrixReader);
	phis.application.reg.script.RegisteredSettlementForm.superclass.constructor.apply(
			this, [cfg])
	this.on('winShow', this.onWinShow, this);
}
Ext.extend(phis.application.reg.script.RegisteredSettlementForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.exContext.systemParams = this.loadSystemParams({
							commons : ['BLF'],
							privates : ['GHFPDY','NHGHDY']
						});
				
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
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var items = schema.items
				if (!this.fireEvent("changeDic", items)) {
					return
				}
				var table = {
					// unless overridden
					frame : true,
					layout : 'tableform',
					bodyStyle : 'padding:5px 5px 0',
					layoutConfig : {
						columns : 1,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : "2"
						}
					},
					items : [],
					buttons : [{
								cmd : 'commit',
								text : '确定',
								handler : this.doCommit,
								scope : this
							}, {
								text : '取消',
								handler : this.doConcel,
								scope : this
							}]
				}
				if (!this.autoFieldWidth) {
					var forceViewWidth = (defaultWidth + (this.labelWidth || 80))
					table.layoutConfig.forceWidth = forceViewWidth
				}
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
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
						f.style = 'font-size:16px;font-weight:bold;color:red;textAlign:right';
					} else if (it.id == "YSK" || it.id == "TZJE") {
						f.style += 'font-size:16px;font-weight:bold;color:red;textAlign:right';
					} else if (it.id == "ewm") {
						f.style = 'background:#E6E6E6;cursor:default;font-size:12px;color:gray;';
						f.emptyText = "请用鼠标点击此处激活扫码设备";
					} else if (it.id != "JZHM") {
						f.style += 'font-size:16px;font-weight:bold;color:blue;textAlign:right';
					}
					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}					
					table.items.push(f)
				}
				var cfg = {
					buttonAlign : 'center',
					labelAlign : this.labelAlign || "left",
					labelWidth : this.labelWidth || 80,
					frame : true,
					shadow : false,
					border : false,
					collapsible : false,
					autoWidth : true,
					autoHeight : true,
					floating : false
				}
				if (this.isCombined) {
					this.width = 360
					cfg.frame = true
					cfg.shadow = false
					cfg.width = this.width
					cfg.height = this.height
				} else {
					cfg.autoWidth = true
					cfg.autoHeight = true
				}
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.expansion(table);// add by yangl
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)
				this.schema = schema;
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				/*****begin 挂号结算界面增加扫码支付 zhaojian 2018-08-14 *****/
				this.combo = this.form.getForm().findField("FKFS");
				this.combo.on("select", this.calYSK, this);
				this.combo.getStore().on('load', this.calYSKLoadEvent, this);
				var ewm = this.form.getForm().findField("ewm");
				ewm.on("focus", this.onEWMfocus, this);
				ewm.on("blur", this.onEWMblur, this);
				ewm.on("change", this.onEWMchange, this);
				/*****end 挂号结算界面增加扫码支付 zhaojian 2018-08-14 *****/
				/****begin zhaojian 2019-05-10 结算时增加优惠金额手动修改功能****/
				var yhje = this.form.getForm().findField('YHJE');
				yhje.on("blur", this.focusFieldAfterYhje, this);
				/****end zhaojian 2019-05-10 结算时增加优惠金额手动修改功能****/
				return this.form
			},
			/*****begin 挂号结算界面增加扫码支付 zhaojian 2018-08-14 *****/
			// 回显付费方式事件
			calYSKLoadEvent : function(store, records) {
				var delcount = 0;
				//过滤付款方式下拉框选项，只保留 现金、扫码付
				store.each(function(item, index) {
					if(item.data.key!=1 && item.data.key!=30){
						store.removeAt(index+delcount);
						delcount --;
					}
				});
				store.each(function(item, index) {
							if (item.json.MRBZ == "1") {
								this.combo.setValue(item.data.key);
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
				//增加优惠金额 zhaojian 2019-05-10
				payType.YSK = (this.form.getForm().findField("ZFJE").getValue()-this.form.getForm().findField("YHJE").getValue())
					.toString();
				//zhaojian 2019-04-04 增加义诊减免处理
				if(this.opener.opener.KSList && this.opener.opener.KSList.ghyzbz
					&& this.opener.opener.KSList.ghyzbz==true){
						payType.YSK = (parseFloat(this.form.getForm().findField("ZFJE").getValue()) - parseFloat(this.form.getForm().findField("YZJM").getValue())).toFixed(2).toString();
				}
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
				jkje.setValue();
				this.form.getForm().findField("TZJE").setValue("0");
				this.form.getForm().findField("FKFS").focus(false, 200);
				if(this.combo.value==30){
					this.form.getForm().findField("ewm").focus(false, 100);
				}
			},
			/****begin zhaojian 2019-05-10 结算时增加优惠金额手动修改功能****/
			focusFieldAfterYhje : function(index, delay) {
				this.running = true;
				var form = this.form.getForm();
				var zfje = form.findField("ZFJE");
				var yhje = form.findField("YHJE");
				var yzjm = form.findField("YZJM");
				var jkje = form.findField("JKJE");
				var ysk = form.findField("YSK");
				if (parseFloat(yhje.getValue())<0 || parseFloat(zfje.getValue()) < parseFloat(yhje.getValue())) {
					yhje.setValue(this.jsxx.YHJE);
					form.findField("YSK").setValue((parseFloat(zfje.getValue())-parseFloat(yhje.getValue())-parseFloat(yzjm.getValue())).toFixed(2));
					form.findField("JKJE").setValue(ysk.getValue());
					Ext.Msg.alert("提示", "优惠金额小于0或大于自负金额,请重新输入金额!", function() {
						form.findField("TZJE").setValue("0");
						yhje.focus(false, 200);
						this.running = false;
					}, this);
					return;
				}
				form.findField("YSK").setValue((parseFloat(zfje.getValue())-parseFloat(yhje.getValue())-parseFloat(yzjm.getValue())).toFixed(2));
				if(jkje.getValue()){
					form.findField("JKJE").setValue(ysk.getValue());
					form.findField("TZJE").setValue((parseFloat(ysk.getValue())-parseFloat(jkje.getValue())).toFixed(2));
				}
				this.running = false;
			},
			/****end zhaojian 2019-05-10 结算时增加优惠金额手动修改功能****/
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
			/*****end 挂号结算界面增加扫码支付 zhaojian 2018-08-14 *****/
			onWinShow : function() {
				this.afterShow();
			},
			onReady:function(){
				var field = this.form.getForm().findField('JKJE');
				field.on("blur", this.focusFieldAfter, this);
			},
			focusFieldAfter : function(index, delay) {
				if (this.running) {
					return;
				}
				this.running = true;
				var form = this.form.getForm();
				var jkje = form.findField("JKJE");
				var YSK = form.findField("YSK");
				if (!jkje.getValue()) {
					form.findField("JKJE").setValue(YSK.getValue());
					form.findField("TZJE").setValue("0");
				} else {
					jkje.setValue(parseFloat(jkje.getValue()).toFixed(2))
					if (jkje.getValue() > 9999999.99) {
						Ext.Msg.alert("提示", "交款金额最大不能大于等于1000万!", function() {
									jkje.setValue()
									jkje.focus(false, 200);
									this.running = false;
								}, this);
						return;
					}
					if (parseFloat(jkje.getValue()) < parseFloat(YSK.getValue())) {
						jkje.setValue("");
						Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!",
								function() {
									jkje.focus(false, 200);
									this.running = false;
								}, this);
						return;
					} else {
						var tzje = form.findField("TZJE");
						tzje
								.setValue((parseFloat(jkje.getValue()) - parseFloat(YSK
										.getValue())).toFixed(2));
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
				//zhaojian 2019-01-23 增加手动退费功能
				var dicName = {
            		 id : "phis.dictionary.CustomRefundInfo"
          		    };
				var refundinfo=util.dictionary.DictionaryLoader.load(dicName);
				var QYFLAG = refundinfo.wraper["QYFLAG"].text;
				if (QYFLAG=="1") {
					var targetip = refundinfo.wraper["CUSTOMIP"].text;
					this.initHtmlElement();
					var getip = this.GetIpAddressAndHostname().split(",")[0];//通过插件获取的当前手动退费电脑IP
					if(targetip==getip){
						var refunddata = {};
						refunddata.PAYSERVICE = refundinfo.wraper["PAYSERVICE"].text;// 业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
						refunddata.IP = refundinfo.wraper["IP"].text;// 测试时使用
						refunddata.COMPUTERNAME = refundinfo.wraper["COMPUTERNAME"].text;// 测试时使用
						refunddata.HOSPNO = refundinfo.wraper["HOSPNO"].text;// 医院流水号
						refunddata.PAYMONEY = refundinfo.wraper["PAYMONEY"].text;// 支付金额
						refunddata.VOUCHERNO = refundinfo.wraper["VOUCHERNO"].text;// 就诊号码或发票号码
						refunddata.ORGANIZATIONCODE = refundinfo.wraper["ORGANIZATIONCODE"].text;// 测试时使用
						refunddata.PATIENTYPE = refundinfo.wraper["PATIENTYPE"].text;// 病人性质
						refunddata.PATIENTID = refundinfo.wraper["PATIENTID"].text;// 病人id
						refunddata.NAME = refundinfo.wraper["NAME"].text;// 病人姓名
						refunddata.SEX = refundinfo.wraper["SEX"].text;// 性别
						refunddata.IDCARD = refundinfo.wraper["IDCARD"].text;// 身份证号
						refunddata.BIRTHDAY = refundinfo.wraper["BIRTHDAY"].text;// 出生年月
						refunddata.PAYSOURCE = refundinfo.wraper["PAYSOURCE"].text;// 支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
						refunddata.COLLECTFEESCODE = refundinfo.wraper["COLLECTFEESCODE"].text;// 操作员代码
						refunddata.COLLECTFEESNAME = refundinfo.wraper["COLLECTFEESNAME"].text;// 操作员姓名
						refunddata.HOSPNO_ORG = refundinfo.wraper["HOSPNO_ORG"].text;// 退款交易时指向原HOSPNO
						refund(refunddata, this);
						MyMessageTip.msg("提示", "手动退费成功！", true);
						return;					
					}
				}				
				if (this.running || this.runningTask) {
					return;
				}
				this.running = true;
				var form = this.form.getForm();				
				/*****begin 挂号结算界面增加扫码支付 zhaojian 2018-08-24 *****/
				if((this.combo.value==30) && (typeof(QRCODE)=='undefined' || QRCODE=="")){					
					Ext.Msg.alert("提示", "未获取到付款码，请扫码支付！",
							function() {
								form.findField("ewm").setValue("");
								form.findField("ewm").focus(true,200);
								this.running = false;
							}, this);
					return;
				}
				/*****end 挂号结算界面增加扫码支付 zhaojian 2018-08-24 *****/				
				var jkje = form.findField("JKJE");
				var YSK = form.findField("YSK");
				if (!jkje.getValue()) {
					form.findField("JKJE").setValue(YSK.getValue());
					form.findField("TZJE").setValue("0");
				} else {
					jkje.setValue(parseFloat(jkje.getValue()).toFixed(2))
					if (jkje.getValue() > 9999999.99) {
						Ext.Msg.alert("提示", "交款金额最大不能大于等于1000万!", function() {
									jkje.setValue()
									jkje.focus(false, 200);
									this.running = false;
								}, this);
						return;
					}
					if (parseFloat(jkje.getValue()) < parseFloat(YSK.getValue())) {
						jkje.setValue("");
						Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!",
								function() {
									jkje.focus(false, 200);
									this.running = false;
								}, this);
						return;
					} else {
						var tzje = form.findField("TZJE");
						tzje.setValue((parseFloat(jkje.getValue()) - parseFloat(YSK
										.getValue())).toFixed(2));
					}
				}
				this.GHXX.JKJE = form.findField("JKJE").getValue();// 交款金额
				this.GHXX.TZJE = form.findField("TZJE").getValue() | "0";// 退找金额
				this.GHXX.ZFJE = form.findField("ZFJE").getValue();// 自负金额
				this.GHXX.YHJE = form.findField("YHJE").getValue();//增加优惠金额 zhaojian 2019-05-10
				this.GHXX.JJZF = form.findField("JJZF").getValue();// 其他应收
				this.GHXX.ZHZF = form.findField("ZHZF").getValue();// 帐户支付
				this.GHXX.YBZF = form.findField("YBZF").getValue();// 医保支付
				this.GHXX.XJJE = form.findField("YSK").getValue();// 应收款
				this.GHXX.ZJJE = form.findField("ZJJE").getValue();
				this.GHXX.JZHM = form.findField("JZHM").getValue();
				if(form.findField("YZJM") && form.findField("YZJM").getValue() > 0){
					this.GHXX.YZJM=form.findField("YZJM").getValue();
					this.GHXX.YZBZ="1";
				}
				this.saving = true
				var body = {};
				if(this.GHXX.BRXZ==3000){
					var datas = {};
					try{
						var xmlDoc = loadXmlDoc("c:\\njyb\\mzghxx.xml");
						var elements = xmlDoc.getElementsByTagName("RECORD");
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
						}
					}catch(e){
						MyMessageTip.msg("提示", "文件解析失败!", true);
					}
					body["YBXX"]=datas;// 将需要保存的医保信息存到变量里
				}
				//南京金保业务处理
				//如果是义诊不调用金保接口
				debugger;
				if(this.GHXX.BRXZ==2000){
					if(this.GHXX.njjbyjsxx){
						var NJJBJSXX=this.GHXX.njjbyjsxx.NJJBYJSXX;
						NJJBJSXX.DJH=this.GHXX.JZHM;
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
						//主动产生交易流水号，用于保存
						var jylsh=this.buildjylsh();
						NJJBJSXX.JYLSH=jylsh;
						var mzjsstr=this.buildstr("2410",this.ywzqh,NJJBJSXX);
//						alert(mzjsstr);
						var remzjs=this.drinterfacebusinesshandle(mzjsstr);
//						alert(remzjs);
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
								njjbmzjsxx.FPHM=this.GHXX.JZHM;
								body.njjbmzjsxx=njjbmzjsxx;
							}else{
								MyMessageTip.msg("金保结算提示",mzjsarr[3], true);
								return;
							}
					//}else{
					//	MyMessageTip.msg("提示：","金保预结算信息没获取到，请重新结算！", true);
					//	return;
					}
				}
				body["GHXX"] = this.GHXX;
				body["YYTAG"] = this.yytag;
				body["GHYJ"] = this.opener.opener.ghyj;
				this.BODY = body;
				
				/*****begin 挂号结算界面增加扫码支付 zhaojian 2018-08-24 *****/	
				//微信或支付宝付款成功后再继续HIS中的结算流程
				this.GHXX.ZPJE = 0;
				if((this.combo.value==30) && typeof(QRCODE)!='undefined' && QRCODE!=""){
					this.GHXX.FKFS = getFKFS(QRCODE);
					this.GHXX.ZPJE = this.GHXX.XJJE;
					this.GHXX.XJJE = 0;
					var paydata ={};
					paydata.PAYSERVICE = "1";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
					paydata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP				
					paydata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
					//paydata.IP = "192.168.9.71";//测试时使用
	 				//paydata.COMPUTERNAME = "DPSF01";//测试时使用
					paydata.HOSPNO = generateHospno("GH",this.GHXX.JZHM);//医院流水号
					this.GHXX.HOSPNO = paydata.HOSPNO;
					paydata.PAYMONEY = this.GHXX.ZPJE;//支付金额
					paydata.VOUCHERNO = this.GHXX.JZHM;//就诊号码或发票号码
					paydata.ORGANIZATIONCODE = this.mainApp.deptId;//机构编码
					//paydata.ORGANIZATIONCODE = "320124003";//测试时使用
					paydata.PATIENTYPE = this.GHXX.BRXZ;//病人性质
					paydata.PATIENTID = this.GHXX.BRID;//病人id
					paydata.NAME = this.GHXX.BRXM;//病人姓名
					paydata.SEX = this.GHXX.BRXB;//性别
					paydata.IDCARD = "";//身份证号
					paydata.BIRTHDAY = this.GHXX.CSNY;//出生年月
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
					SubmitOrder(paydata,this);
					return;
				}
				/*****end 挂号结算界面增加扫码支付 zhaojian 2018-08-24 *****/	
				QRCODE="";
				this.doCommit2();
			},
			doCommit2: function(){
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "registeredManagementService",
							serviceAction : "saveRegisteredManagement",
							body : this.BODY
						}, function(code, msg, json) {
							debugger;
							this.JSON = json;//移动支付 zhaojian 2018-09-17
							this.form.el.unmask()
							this.saving = false
							if(code==902){
								MyMessageTip.msg("提示", "本院启用了排队叫号接口，接口调用不成功，但不影响使用", true);
							}else if (code > 300) {
								// 如果本地失败,调用医保取消结算接口
								debugger;
								if(this.GHXX.BRXZ==2000){
									//获取业务流水号
									var body={};
										body.ywlx="1";//挂号
										body.SBXH=this.GHXX.SBXH;
									var getlsh = phis.script.rmi.miniJsonRequestSync({
											serviceId : "phis.NjjbService",
											serviceAction : "getnjjblshbyqx",
											body:body
										});
									if(getlsh.code >=300){
										MyMessageTip.msg("提示", getlsh.msg, true);
										return;
									}else{
										var gxghdata={};
										gxghdata.NJJBLSH=getlsh.json.lsh.NJJBLSH;
										gxghdata.JBR=this.mainApp.uid;	
									}
									//撤销登记2240
									var str=this.buildstr("2240",this.ywzqh,gxghdata);
									this.addPKPHISOBJHtmlElement();
									var drre=this.drinterfacebusinesshandle(str);
									var arr=drre.split("|");
									if(arr[0]=="0"){
										//撤销登记成功
									}else{
										MyMessageTip.msg("南京金保返回提示", drre, true);
										return;
									}
								}
								/*****begin 挂号结算界面增加扫码支付 zhaojian 2018-08-24 *****/
								//如果本地失败，调用支付平台取消微信支付宝付款	
								if(this.combo.value==30){
									var refunddata ={};
									refunddata.PAYSERVICE = "-1";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费'
									refunddata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP							
									refunddata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称//
									//refunddata.IP = "192.168.9.71";//测试时使用
					 				//refunddata.COMPUTERNAME = "DPSF01";//测试时使用
									refunddata.HOSPNO = generateHospno("TH",this.GHXX.JZHM);//医院流水号
									refunddata.PAYMONEY = this.GHXX.ZPJE;//支付金额
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
								/*****end 挂号结算界面增加扫码支付 zhaojian 2018-08-24 *****/
							}
							this.doCommit3();
						}, this)
			},
			doCommit3: function(){
				debugger;
				this.opener.opener.KSList.SBXH = this.JSON.SBXH;
				this.fireEvent("settlement", this);
				this.running = false;
				this.doConcel();
				if(this.exContext.systemParams.GHFPDY=="1"){
					if((this.GHXX.BRXZ==6000 && this.exContext.systemParams.NHGHDY=="0")
						||this.GHXX.BRXZ==3000){
						
					}else{
						debugger;
						if(this.jsxx.JYGHYH == "true" || this.JSON.JYGHYH == "true"){					
							util.rmi.jsonRequest({
								serviceId : "chis.registeredPerformanceService",
								serviceAction : "saveRegisteredPerformance",
								method : "execute",
				                body : {
				                    data : {
				                    	"empiId":this.GHXX.EMPIID,
				                    	"SBXH":this.JSON.SBXH
				                    }
				                }
							});
						}
						
						util.rmi.jsonRequest({
							serviceId : "hai.hmsInterfaceService",
							serviceAction : "updateDownBizType",
							method : "execute",
					                body : {		                    
				                    	"idcard":this.GHXX.IDCARD,
				                    	"bizType":"1"             
					                }
						});	
						
						util.rmi.jsonRequest({
							serviceId : "hai.hmsInterfaceService",
							serviceAction : "updateUpBizType",
							method : "execute",
					                body : {		                    
				                    	"idcard":this.GHXX.IDCARD,
				                    	"bizType":"1"             
					                }
						});						
						this.doPrintFP(this.JSON.SBXH);
					}
				}
				/******************modify by lizhi 20170524删除医保文件**************/
				if(this.GHXX.BRXZ==3000){
					delXmlFile("c:\\njyb\\mzghxx.xml");
				}
				/******************modify by lizhi 20170524删除医保文件**************/
			},
			
			doConcel : function() {
				win = this.getWin();
				if (win)
					win.hide();
			},
			setData : function(GHXX, YBXX) {
				this.ybxx = null;
				this.GHXX = GHXX;
/*				if (YBXX && YBXX != null) {
					var yjsxx = YBXX["YJSXX"];// 预结算返回的信息,可以根据传过来的该参数名
					GHXX.GRXJZF = yjsxx.GRXJZF;// 个人需要支付的金额
				}*/
				// 获取发票号码
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "registeredManagementService",
							serviceAction : "queryRegisteredSettlement",
							body : GHXX
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return;
				} else {
					if (!r.json.body) {
						this.jsxx = '';
						return;
					} else {
						this.jsxx = r.json.body;
						if (YBXX && YBXX != null) {
								this.ybxx = YBXX;
								var yjsxx = YBXX["YJSXX"];
								this.jsxx["ZJJE"] = yjsxx.YLFZE;// 总金额
								this.jsxx["QTYS"] = yjsxx.GRXJZF;// 自己需要支付的部分(自己需要支付的现金)
						}
						//优惠金额初始值  zhaojian 2019-05-10
						this.GHXX.YHJEINIT = this.jsxx.YHJEINIT;
					}
				}
			},
			afterShow : function() {
				var this_ = this;
				if (!this.jsxx) {
					Ext.Msg.alert("提示", "请先维护默认付款方式!", function() {
								this_.doConcel();
							});
					return;
				}
				this.GHXX.FKFS = this.jsxx.FKFS
				this.GHXX.ZJJE = this.jsxx.ZJJE
				var form = this.form.getForm();
				form.findField("JZHM").setValue(this.GHXX.JZHM);
				form.findField("ZJJE").setValue(this.jsxx.ZJJE);// 总计金额
				form.findField("ZFJE").setValue(this.jsxx.QTYS);// 自负金额
				form.findField("ZHZF").setValue("0");//账户支付
				form.findField("YBZF").setValue("0");//医保支付
				form.findField("JJZF").setValue("0");//其他应收
				form.findField("YHJE").setValue(this.jsxx.YHJE);//优惠金额
				if(this.opener.opener.KSList && this.opener.opener.KSList.ghyzbz
					&& this.opener.opener.KSList.ghyzbz==true){
					//医保病人诊疗费不为0时，义诊减免现金部分
					if(this.jsxx.QTYS > 0 && this.GHXX.BRXZ==2000&&this.GHXX.njjbyjsxx){
//						form.findField("YZJM").setValue((parseFloat(this.jsxx.QTYS)-parseFloat(this.GHXX.njjbyjsxx.BCYLFZE)+parseFloat(this.GHXX.njjbyjsxx.BCXZZFZE)).toFixed(2));
//						var ybzf=parseFloat(this.GHXX.njjbyjsxx.BCTCZFJE)+parseFloat(this.GHXX.njjbyjsxx.BCDBJZZF)
//									+parseFloat(this.GHXX.njjbyjsxx.BCDBBXZF)+parseFloat(this.GHXX.njjbyjsxx.BCMZBZZF);
//						form.findField("ZHZF").setValue(this.GHXX.njjbyjsxx.BCZHZFZE);
//						form.findField("YBZF").setValue(ybzf);
//						form.findField("ZFJE").setValue((parseFloat(this.jsxx.QTYS)-parseFloat(this.GHXX.njjbyjsxx.BCYLFZE)+parseFloat(this.GHXX.njjbyjsxx.BCXZZFZE)).toFixed(2));
//						//其他应收
//						form.findField("JJZF").setValue(parseFloat(this.GHXX.njjbyjsxx.BCYLFZE)-
//							parseFloat(this.GHXX.njjbyjsxx.BCZHZFZE)-ybzf-parseFloat(this.GHXX.njjbyjsxx.BCXZZFZE));
//						form.findField("JKJE").setValue("0");
//						form.findField("TZJE").setValue("0");
//						form.findField("YSK").setValue("0");
//						form.findField("YHJE").setValue("0");//优惠金额
//						MyMessageTip.msg("提示", "您启用了义诊，预计义诊减免"+form.findField("YZJM").getValue()+"元！", true);
						this.jsxx.QTYS=0;
						this.jsxx.ZFJE=0;
						form.findField("JKJE").setValue("0");
						form.findField("TZJE").setValue("0");
						form.findField("YSK").setValue("0");
						form.findField("YHJE").setValue("0");//优惠金额
						form.findField("YZJM").setValue(this.jsxx.ZJJE);
						MyMessageTip.msg("提示", "您启用了义诊，预计义诊减免"+this.jsxx.ZJJE+"元！", true);
					}else{
						this.jsxx.QTYS=0;
						this.jsxx.ZFJE=0;
						form.findField("JKJE").setValue("0");
						form.findField("TZJE").setValue("0");
						form.findField("YSK").setValue("0");
						form.findField("YHJE").setValue("0");//优惠金额
						form.findField("YZJM").setValue(this.jsxx.ZJJE);
						MyMessageTip.msg("提示", "您启用了义诊，预计义诊减免"+this.jsxx.ZJJE+"元！", true);
					}
				}else{
					form.findField("YZJM").setValue("0");
					if(this.jsxx.QTYS > 0) {
						form.findField("YSK").setValue(this.jsxx.QTYS);
						if (this.GHXX.BRXZ == 2000) {
							//this.running=false;
							if (this.GHXX.njjbyjsxx) {
								var ybzf = parseFloat(this.GHXX.njjbyjsxx.BCTCZFJE) + parseFloat(this.GHXX.njjbyjsxx.BCDBJZZF)
									+ parseFloat(this.GHXX.njjbyjsxx.BCDBBXZF) + parseFloat(this.GHXX.njjbyjsxx.BCMZBZZF);
								form.findField("ZHZF").setValue(this.GHXX.njjbyjsxx.BCZHZFZE);
								form.findField("YBZF").setValue(ybzf);
								form.findField("ZFJE").setValue((parseFloat(this.jsxx.QTYS) - parseFloat(this.GHXX.njjbyjsxx.BCYLFZE) + parseFloat(this.GHXX.njjbyjsxx.BCXZZFZE)).toFixed(2));
								//其他应收
								form.findField("JJZF").setValue(parseFloat(this.GHXX.njjbyjsxx.BCYLFZE) -
									parseFloat(this.GHXX.njjbyjsxx.BCZHZFZE) - ybzf - parseFloat(this.GHXX.njjbyjsxx.BCXZZFZE));
								form.findField("YSK").setValue((parseFloat(this.jsxx.QTYS) - parseFloat(this.GHXX.njjbyjsxx.BCYLFZE) + parseFloat(this.GHXX.njjbyjsxx.BCXZZFZE)).toFixed(2));
							}//else{
							//	this.running=true;
							//}
						}
						if (this.jsxx.YHJE>0) {
							//zhaojian 2019-06-18-优化医保病人优惠金额大于0时，应收款未减去优惠金额的问题
							form.findField("YSK").setValue((parseFloat(form.findField("YSK").getValue()) - parseFloat(this.jsxx.YHJE)).toFixed(2));
						}
						form.findField("JKJE").setValue("");
						form.findField("TZJE").setValue("");
					}else{
						form.findField("YSK").setValue("0");
						form.findField("YHJE").setValue("0");//优惠金额
						form.findField("JKJE").setValue("0");
						form.findField("TZJE").setValue("0");
					}
				}
				var NJJBYLLB=form.findField("NJJBYLLB");
				if(NJJBYLLB){
					this.NJJBYLLB=NJJBYLLB;
					//this.NJJBYLLB.on("select",this.changeyllb,this);
					NJJBYLLB.setDisabled(true);
				}
				var YBMC=form.findField("YBMC");
				if(YBMC){
					this.ybmc=YBMC;
					YBMC.setDisabled(false);
				}
				if(this.GHXX.BRXZ==2000){				
					if(NJJBYLLB){
						NJJBYLLB.setValue(this.GHXX.YLLB);
					}
					if(YBMC){
						YBMC.setValue(this.GHXX.JBBM);
					}

				}else{
					if(NJJBYLLB){
						//form.findField("NJJBYLLB").setDisabled(true);
						this.NJJBYLLB.setValue("");
					}
					if(YBMC){
						//form.findField("YBMC").setDisabled(true);
						this.ybmc.setValue("");
					}
				}
				//form.findField("YHJE").focus(false, 200);
				//form.findField("JKJE").focus(false, 200);
				form.findField("FKFS").focus(false, 200);
			},
			changeyllb:function(){
				this.ybmc.setValue("");
				var v=this.NJJBYLLB.getValue();
				var dic={};
				dic.id="phis.dictionary.ybJbbm";
				var tsbz="";//特殊病种
				if(v==16){
					var arr=this.opener.opener.drnjjbkxx.YBMMBZ.split(",");
					var bz=""
					for(var i=0;i<arr.length;i++){
						bz+="'"+arr[i]+"',"
					}
					dic.filter="['and',['in',['$','item.properties.JBBM'],["+bz.substring(0,bz.length-1)+"]]]";
					if(arr.length>=1){
						tsbz=arr[0];
					}
				}else if(v==171){
					var arr=this.opener.opener.drnjjbkxx.YBMTBZ.split(",");
					var bz=""
					for(var i=0;i<arr.length;i++){
						bz+="'"+arr[i]+"',"
					}
					dic.filter="['and',['in',['$','item.properties.JBBM'],["+bz.substring(0,bz.length-1)+"]]]";
					if(arr.length==1){
						tsbz=arr[0];
					}
				}
				this.ybmc.store.proxy = new util.dictionary.HttpProxy({
									method : "GET",
									url : util.dictionary.SimpleDicFactory.getUrl(dic)
								})
				this.ybmc.store.load();
				if(tsbz.length >2){
					this.ybmc.setValue(tsbz);
					tsbz="";
				}
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : 360,
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
				return win;
			},
			doPrintFP : function(sbxh) {
				var LODOP=getLodop();  
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "registeredManagementService",
					serviceAction : "printMoth2",
						sbxh : sbxh
					});
				this.fphm = false;
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				LODOP.SET_PRINT_STYLE("ItemType",4);
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				LODOP.SET_PRINT_PAGESIZE(0,'21cm','12.7cm',"");
				LODOP.ADD_PRINT_TEXT("10mm", 60, 130, 25, ret.json.MZXH);
				LODOP.ADD_PRINT_TEXT("10mm", 240, 100, 25, "非盈利");
				LODOP.ADD_PRINT_TEXT("5mm", 350, 130, 25, ret.json.MZHM);//add by lizhi 2017-11-06 门诊号码显示在发票号码前
				LODOP.ADD_PRINT_TEXT("10mm", 490, 150, 25, ret.json.FPHM);
				LODOP.ADD_PRINT_TEXT("15mm", 30, 100, 25, ret.json.XM);//姓名
				LODOP.ADD_PRINT_TEXT("15mm", 140, 100, 25, ret.json.XB);//性别
				LODOP.ADD_PRINT_TEXT("15mm", 240, 80, 25, ret.json.JSFS);//结算方式
				LODOP.ADD_PRINT_TEXT("30mm", "2mm", "60mm", 20, ret.json.MXMC1);
				LODOP.ADD_PRINT_TEXT("30mm", 164, 25, 20, ret.json.MXSL1);
				LODOP.ADD_PRINT_TEXT("30mm", 190, "20mm", 20, ret.json.MXJE1);
				LODOP.ADD_PRINT_TEXT("40mm", 60, "100mm", 30, "家医减免："+(ret.json.JYJMBZ==null?"":ret.json.JYJMBZ));
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 18);
				LODOP.ADD_PRINT_TEXT("55mm", 60, "100mm", 30, "门诊号码："+ret.json.MZHM);
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 18);
				LODOP.ADD_PRINT_TEXT("70mm", 60, "100mm", 30, "就诊序号："+ret.json.KSMC+" "+ret.json.PLXH+"号");
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 18);
				LODOP.ADD_PRINT_TEXT("83mm", 60, 500, 25, ret.json.BXMX);//报销明细
            	LODOP.SET_PRINT_STYLEA(0, "FontSize", 11);
				LODOP.ADD_PRINT_TEXT("93mm", 120, 300, 25, ret.json.DXZJE);
				LODOP.ADD_PRINT_TEXT("93mm", 480, 100, 25, ret.json.HJJE);
				LODOP.ADD_PRINT_TEXT("98mm", 480, 80, 25, ret.json.XJJE);
				LODOP.ADD_PRINT_TEXT("103mm", 60, 180, 25, ret.json.JGMC);
				LODOP.ADD_PRINT_TEXT("103mm", 270, 100, 25, ret.json.SFY);
				LODOP.ADD_PRINT_TEXT("103mm", 400, 60, 25, ret.json.YYYY);
				LODOP.ADD_PRINT_TEXT("103mm", 465, 40, 25, ret.json.MM);
				LODOP.ADD_PRINT_TEXT("103mm", 510, 40, 25, ret.json.DD);
				LODOP.ADD_PRINT_TEXT("10mm", "185mm", 130, 15, ret.json.MZHM);
				LODOP.ADD_PRINT_TEXT("14mm", "185mm", 130, 15, ret.json.XM);
				LODOP.ADD_PRINT_TEXT("18mm", "185mm", 90, 15, ret.json.SFXM1);
				LODOP.ADD_PRINT_TEXT("22mm", "185mm", 90, 15, ret.json.XMJE1);
				LODOP.ADD_PRINT_TEXT("26mm", "185mm", 90, 15, ret.json.GHSJ);
				LODOP.ADD_PRINT_TEXT("30mm", "185mm", 90, 15, ret.json.SFY);
				LODOP.ADD_PRINT_TEXT("34mm", "185mm", 90, 15, ret.json.MZXH);
				LODOP.ADD_PRINT_TEXT("50mm", "185mm", "10mm", 60,"作");
            	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
            	LODOP.ADD_PRINT_TEXT("60mm", "185mm", "10mm", 70,"废");
            	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
            	LODOP.ADD_PRINT_TEXT("90mm", "185mm", "10mm", 60, "作");
            	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
            	LODOP.ADD_PRINT_TEXT("100mm", "185mm", 130, "10mm", "废");
            	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
				
				if (LODOP.SET_PRINTER_INDEXA(ret.json.GHDYJMC)){
					if((ret.json.FPYL+"")=='1'){
						LODOP.PREVIEW();
					}else{
						LODOP.PRINT();
					}
				}else{
					LODOP.PREVIEW();
				}
			}
		})