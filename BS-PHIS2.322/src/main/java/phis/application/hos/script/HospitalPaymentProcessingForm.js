$package("phis.application.hos.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout","phis.script.DatamatrixReader",
		"phis.application.pay.script.PayCommon")

phis.application.hos.script.HospitalPaymentProcessingForm = function(cfg) {
	// cfg.colCount = 2;
	cfg.autoLoadData = false;
	cfg.labelWidth = 65;
	// cfg.showButtonOnTop = true;
	Ext.apply(this,phis.script.DatamatrixReader);
	phis.application.hos.script.HospitalPaymentProcessingForm.superclass.constructor
			.apply(this, [cfg])
	this.showButtonOnTop = true;
}

Ext.extend(phis.application.hos.script.HospitalPaymentProcessingForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombiyined) {
						this.addPanelToWin();
					}
					return this.form;
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
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var items = schema.items
				if (!this.fireEvent("changeDic", items)) {
					return
				}
				var colCount = this.colCount;

				var table = {
					layout : 'tableform',
					layoutConfig : {
						columns : colCount,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : "2"
						}
					},
					items : []
				}
				if (!this.autoFieldWidth) {
					var forceViewWidth = (defaultWidth + (this.labelWidth || 80))
							* colCount
					table.layoutConfig.forceWidth = forceViewWidth
				}
				table.items.push({
					xtype : "label",
					html : "<br/><div style='font-size:20px;font-weight:bold;text-align:center;letter-spacing:3px' >住院病人预缴医药费收据</div>"
				})
				table.items.push({
					xtype : "label",
					html : "<br/><div style='font-weight:bolder;text-align:center' >"
							+ this.mainApp.dept + "</div><br/><br/>"
				})
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
					if (it.id == 'SJHM') {
						f.style += 'color:red';
					}
					if (it.id == 'JKJE') {
						f.style = "font-size:16px;font-weight:bold;color:red;";
					} else if (it.id == 'JKHJ' || it.id == 'ZFHJ'
							|| it.id == 'SYHJ') {
						f.style += "font-size:16px;font-weight:bold;color:blue;";
					}else if (it.id == "ewm") {
						f.style = 'background:#E6E6E6;cursor:default;font-size:12px;color:gray;';
						f.emptyText = "请用鼠标点击此处激活扫码设备";
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
					autoScroll : true,
					// collapsible : false,
					// autoWidth : true,
					// autoHeight : true,
					floating : false
				}

				if (this.isCombined) {
					cfg.frame = true
					cfg.shadow = false
					// cfg.width = this.width
					// cfg.height = this.height
				} else {
					// cfg.autoWidth = true
					// cfg.autoHeight = true
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
				return this.form
			},
			onReady : function() {
				var form = this.form.getForm();
				var JKFS = form.findField("JKFS");
				JKFS.on("select", this.JKFSselect, this)
				var JKRQ = form.findField("JKRQ");
				JKRQ.un("specialkey", this.onFieldSpecialkey, this)
				JKRQ.on("specialkey", function(JKRQ, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								if (JKRQ.getValue()) {
									this.doBeforeSave();
								}
							}
						}, this);
				/*****begin 挂号结算界面增加扫码支付 zhaojian 2019-11-12 *****/
				this.combo = form.findField("JKFS");
				this.combo.getStore().on('load', this.calYSKLoadEvent, this);
				var ewm = form.findField("ewm");
				ewm.on("focus", this.onEWMfocus, this);
				ewm.on("blur", this.onEWMblur, this);
				ewm.on("change", this.onEWMchange, this);
				/*****end 挂号结算界面增加扫码支付 zhaojian 2019-11-12 *****/
			},
			doBeforeSave : function() {
				var res = phis.script.rmi.miniJsonRequestSync({
					serviceId : "hospitalAdmissionService",
					serviceAction : "getDateTime"
				});
				var code = res.code;
				var msg = res.msg;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				var dateTime = res.json.body;
				var sysdate = new Date(dateTime.replace(/-/g, "/"));
				var form = this.form.getForm();
				var JKRQ = form.findField("JKRQ");
				if (form.findField("JKFS").getValue()
						&& form.findField("ZYHM").getValue()
						&& form.findField("JKJE").getValue()) {
					var RYRQStr = this.redata.RYRQ.replace(/-/g, "/");
					var str = JKRQ.getValue().replace(/-/g, "/");
					if ((new Date(RYRQStr) - new Date(str)) > 0) {
						Ext.Msg.alert("提示", "缴款日期不能早于入院日期(" + this.redata.RYRQ
										+ ")!", function() {
									JKRQ.focus(true, 100);
								}, this);
						return;
					}
					if ((new Date(str) - sysdate) > 0) {
						Ext.Msg.alert("提示", "缴款日期不能大于当前日期!", function() {
									JKRQ.focus(true, 100);
								}, this);
						return;
					}
					/*****begin 挂号结算界面增加扫码支付 zhaojian 2019-11-12 *****/
					if((this.combo.value==50) && (typeof(QRCODE)=='undefined' || QRCODE=="")){					
						Ext.Msg.alert("提示", "未获取到付款码，请扫码支付！",
								function() {
									form.findField("ewm").setValue("");
									form.findField("ewm").focus(true,200);
									this.running = false;
								}, this);
						return;
					}
					/*****end 挂号结算界面增加扫码支付 zhaojian 2019-11-12 *****/	
					Ext.MessageBox.confirm('确认保存', '是否保存当前缴款记录', function(btn,
									text) {
								if (btn == "yes") {
									this.doSave();
								}
							}, this);
				}
			},
			JKFSselect : function(none) {
				this.form.getForm().findField("ewm").setValue("");
				if (this.fkfss) {
					for (var i = 0; i < this.fkfss.length; i++) {
						var form = this.form.getForm();
						var ZPHM = form.findField("ZPHM");
						if (this.fkfss[i].FKFS == none.value) {
							ZPHM.setDisabled(false);
							if (this.fkfss[i].HMCD) {
								ZPHM.maxLength = this.fkfss[i].HMCD;
							}
							return;
						} else {
							ZPHM.setDisabled(true);
						}
					}
				}
				if(this.combo.value==50){
					if(this.form.getForm().findField("JKJE").getValue()==""){
						this.form.getForm().findField("JKJE").focus(false, 300);
						return;
					}
					this.form.getForm().findField("ewm").focus(false, 100);
				}
			},
			// 回显付费方式事件
			calYSKLoadEvent : function(store, records) {
				var delcount = 0;
				//过滤付款方式下拉框选项，只保留 现金、扫码付
				store.each(function(item, index) {
					if(item.data.key!=6 && item.data.key!=50){
						store.removeAt(index+delcount);
						delcount --;
					}
				});
			},
			onEWMchange : function(field){
				if(field.getValue().length==18){
					if(this.combo.value != 50){
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
					this.doSave();
				}
			},
			onEWMfocus : function(){
				//判定付款方式是否为微信或支付宝
				if(this.combo.value==6){
					return;
				}
				this.form.el.mask("等待病人扫描付款码...", "x-mask-loading");	
				this.initHtmlElement();
			},
			onEWMblur : function(){
				if(this.combo.value==6){
					return;
				}
				this.form.el.unmask();
			},
			stopReadTask : function(){
				Ext.TaskMgr.stopAll();
			},
			doEnter : function(field) {
				this.needQuery = true;
				if (this.loading) {
					return
				}
				var Data = {};
				Data.key = field.getName();
				Data.value = field.getValue();
				this.form.el.mask("正在加载数据...", "x-mask-loading")
				this.loading = true;
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalPaymentProcessingService",
							serviceAction : "queryBrxx",
							body : Data
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								Ext.Msg.alert("提示", msg, function() {
											this.opener.opener.tab
													.getItem("paymentprocessing")
													.setDisabled(true);
											this.opener.opener.tab.activate(1)
										}, this);
								return
							}
							if (json.body) {
								this.needQuery = false;
								this.doNew()
								if (json.msg) {
									Ext.Msg.alert("提示", json.msg, function() {
												this.opener.opener.tab
														.getItem("paymentprocessing")
														.setDisabled(true);
												this.opener.opener.tab
														.activate(1)
												if (this.redata) {
													json.body.JKFS = this.redata.JKFS;
												} else {
													this.loadData();
												}
//												json.body.JKRQ = new Date()
//														.format('Y-m-d H:i:s');
												this.redata = json.body
												this.initFormData(json.body)
												var form = this.form.getForm();
												form.findField("JKJE").focus(
														false, 300);
												this.fireEvent("loadData",
														json.body.ZYH);
											}, this);
									return;
								}
								if (this.redata) {
									if (!json.body.SJHM) {
										json.body.SJHM = this.redata.SJHM;
									}
									json.body.JKFS = this.redata.JKFS;
								} else {
									// alert(1)
									this.loadData();
								}
//								json.body.JKRQ = new Date()
//										.format('Y-m-d H:i:s');
								this.redata = json.body
								this.initFormData(json.body)
								var form = this.form.getForm();
								form.findField("JKJE").focus(false, 300);
								this.fireEvent("loadData", json.body.ZYH);
							} else {
								var msg = "";
								if (Data.key == 'ZYHM') {
									msg = '住院号码';
								} else {
									msg = '床位号';
								}
								Ext.Msg.alert("提示", "该" + msg + "不存在!",
										function() {
											this.doNew();
											this.loadData();
											field.focus(false, 300);
										}, this);
							}
						}, this)
			},
			initFormData : function(data) {
				if (data.JKHJ) {
					data.JKHJ = parseFloat(data.JKHJ).toFixed(2);
				} else {
					data.JKHJ = '0.00'
				}
				if (data.ZFHJ) {
					data.ZFHJ = parseFloat(data.ZFHJ).toFixed(2);
				} else {
					data.ZFHJ = '0.00'
				}
				if (data.SYHJ) {
					data.SYHJ = parseFloat(data.SYHJ).toFixed(2);
				} else {
					data.SYHJ = '0.00'
				}
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v != undefined) {
							f.setValue(v)
						}
						if (this.needQuery) {
							if (it.update == "true") {
								f.disable();
							}
						} else {
							if (it.update == "true") {
								f.setDisabled(false);
							}
						}
					}
				}
				form.findField("ewm").setValue("");
			},
			saveToServer : function(saveData) {
				//手动退费
/*				var dicName = {
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
				}*/
				if (this.saving || this.runningTask) {
					return;
				}				
				if (!saveData.SJHM) {
					Ext.Msg.alert("提示", "请先维护收据号码!");
					return
				}
				if (!this.redata.ZYH) {
					Ext.Msg.alert("提示", "请先查询病人信息后再缴款!", function() {
								var form = this.form.getForm();
								var ZYHM = form.findField("ZYHM");
								ZYHM.focus(false, 300);
							}, this);
					return;
				}
				/*****begin 缴款管理预交款（扫码支付） zhaojian 2019-11-12 *****/
				if((this.combo.value==50) && (typeof(QRCODE)=='undefined' || QRCODE=="")){
					Ext.Msg.alert("提示", "未获取到付款码，请扫码支付！",
							function() {
								this.form.getForm().findField("ewm").setValue("");
								this.form.getForm().findField("ewm").focus(false,200);
								this.saving = false;
							}, this);
					return;
				}
				/*****end  缴款管理预交款（扫码支付） zhaojian 2019-11-12 *****/	
				saveData.ZYH = this.redata.ZYH;
				saveData.ZYHM = this.redata.ZYHM;
				saveData.BRCH = this.redata.BRCH;
				saveData.BRXZ = this.redata.BRXZ;
				saveData.BRID = this.redata.BRID;
				saveData.BRXB = this.redata.BRXB;
				saveData.CSNY = this.redata.CSNY;
				saveData.SFZH = this.redata.SFZH==null?"":this.redata.SFZH;
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.saveData = saveData;
				/*****begin 缴款管理预交款（扫码支付） zhaojian 2019-11-12 *****/	
				//微信或支付宝付款成功后再继续HIS中的结算流程
				if((this.combo.value==50) && typeof(QRCODE)!='undefined' && QRCODE!=""){
					saveData.JKFS = getFKFS(QRCODE,1);
					var paydata ={};
					paydata.PAYSERVICE = "3";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费，-3住院预交金退费'
					paydata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP				
					paydata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
					//paydata.IP = "192.168.9.71";//测试时使用
	 				//paydata.COMPUTERNAME = "DPSF01";//测试时使用
					paydata.HOSPNO = generateHospno("ZYYJK",saveData.SJHM);//医院流水号
					saveData.HOSPNO = paydata.HOSPNO;
					paydata.PAYMONEY = saveData.JKJE;//支付金额
					paydata.VOUCHERNO = saveData.SJHM;//就诊号码或发票号码
					paydata.ORGANIZATIONCODE = this.mainApp.deptId;//机构编码
					//paydata.ORGANIZATIONCODE = "320124003";//测试时使用
					paydata.PATIENTYPE = saveData.BRXZ;//病人性质
					paydata.PATIENTID = saveData.BRID;//病人id
					paydata.NAME = saveData.BRXM;//病人姓名
					paydata.SEX = saveData.BRXB;//性别
					paydata.IDCARD = saveData.SFZH;//身份证号
					paydata.BIRTHDAY = saveData.CSNY;//出生年月
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
				/*****end 缴款管理预交款（扫码支付） zhaojian 2019-11-12 *****/	
				QRCODE="";
				this.doCommit2();
			},
			doCommit2: function(){
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalPaymentProcessingService",
							serviceAction : "savePayment",
							body : this.saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [this.saveData]);
								/*****begin 缴款管理预交款（扫码支付） zhaojian 2019-11-12 *****/
								//如果本地失败，调用支付平台取消微信支付宝付款	
								if(this.combo.value==50){
									var refunddata ={};
									refunddata.PAYSERVICE = "-3";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费 -3住院预交金退费'
									refunddata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP							
									refunddata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称//
									//refunddata.IP = "192.168.9.71";//测试时使用
					 				//refunddata.COMPUTERNAME = "DPSF01";//测试时使用
									refunddata.HOSPNO = generateHospno("ZYYJKTF",this.saveData.SJHM);//医院流水号
									refunddata.PAYMONEY = this.saveData.JKJE;//支付金额
									refunddata.VOUCHERNO = this.saveData.SJHM;//就诊号码或发票号码
									refunddata.ORGANIZATIONCODE = this.mainApp.deptId;//机构编码
									//refunddata.ORGANIZATIONCODE = "320124003";//测试时使用
									refunddata.PATIENTYPE = this.saveData.BRXZ;//病人性质
									refunddata.PATIENTID = this.saveData.BRID;//病人id
									refunddata.NAME = this.saveData.BRXM;//病人姓名
									refunddata.SEX = this.saveData.BRXB;//性别
									refunddata.IDCARD = this.saveData.SFZH;//身份证号
									refunddata.BIRTHDAY = this.saveData.CSNY;//出生年月
									refunddata.PAYSOURCE = "1";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
									//refunddata.TERMINALNO = "";//支付终端号 如POS01、1号窗
									refunddata.COLLECTFEESCODE = this.mainApp.uid;//操作员代码
									refunddata.COLLECTFEESNAME = this.mainApp.uname;//操作员姓名	
									//refunddata.COLLECTFEESCODE = "0310581X";//测试时使用
									//refunddata.COLLECTFEESNAME = "管理员";//测试时使用			
									refunddata.HOSPNO_ORG = this.saveData.HOSPNO;//退款交易时指向原HOSPNO
									refund(refunddata,this);
									return;
								}
								/*****end 缴款管理预交款（扫码支付） zhaojian 2019-11-12 *****/
								return
							}
							var form = this.form.getForm();
							var ZYHM = form.findField("ZYHM");
							ZYHM.setValue(this.saveData.ZYHM);
							this.opener.JKXH = json.body.JKXH;
							this.doEnter(ZYHM);
//							this.doPrint(json.body.JKXH);
						}, this)// jsonRequest
			},
			doReSet : function() {
				this.loadData();
				this.fireEvent("reSet", this);
			},
			loadData : function() {
				this.needQuery = true;
				if (this.loading) {
					return
				}
				if (this.form.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalPaymentProcessingService",
							serviceAction : "getReceiptNumber"
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								Ext.Msg.alert("提示", msg, function() {
											this.opener.opener.tab
													.getItem("paymentprocessing")
													.setDisabled(true);
											this.opener.opener.tab.activate(1)
										}, this);
								return
							}
							if (json.fkfss) {
								this.fkfss = json.fkfss;
							}
							if (json.body) {
//								json.body.JKRQ = new Date()
//										.format('Y-m-d H:i:s');
								this.redata = json.body;
								this.doNew();
								this.initFormData(json.body);
							} else {
								Ext.Msg.alert("提示", "请先维护收据号码后再缴款!",
										function() {
											this.opener.opener.tab
													.getItem("paymentprocessing")
													.setDisabled(true);
											this.opener.opener.tab.activate(1)
										}, this);
							}
						}, this)// jsonRequest
			},
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						f.setValue(it.defaultValue)
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}

						if (it.type == "date") { // ** add by yzh 20100919 **
							if (it.minValue)
								f.setMinValue(it.minValue)
							if (it.maxValue)
								f.setMaxValue(it.maxValue)
						}
						// add by yangl 2012-06-29
						if (it.dic && it.dic.defaultIndex) {
							if (f.store.getCount() == 0)
								return;
							if (isNaN(it.dic.defaultIndex)
									|| f.store.getCount() <= it.dic.defaultIndex)
								it.dic.defaultIndex = 0;
							f.setValue(f.store.getAt(it.dic.defaultIndex)
									.get('key'));
						}
					}
				}
				this.setKeyReadOnly(false)
				this.resetButtons(); // ** add by yzh **
				this.fireEvent("doNew")
				this.validate()
			},
			doPrintSet : function(jkxh) {
				var LODOP=getLodop();
				LODOP.PRINT_INITA(10,12,390,300,"住院缴款打印");
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.ADD_PRINT_TEXT(17,114,160,25,"机构名称");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(47,182,30,25,"NO.");
				LODOP.ADD_PRINT_TEXT(47,212,140,25,"票据号码");
				LODOP.ADD_PRINT_TEXT(72,34,80,25,"住院号码");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(72,114,80,25,"住院号码值");
				LODOP.ADD_PRINT_TEXT(72,194,80,25,"床号");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(72,274,80,25,"床号值");
				LODOP.ADD_PRINT_TEXT(97,34,80,25,"姓名");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(97,114,80,25,"姓名值");
				LODOP.ADD_PRINT_TEXT(97,194,80,25,"缴款方式");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(97,274,80,25,"缴款方式值");
				LODOP.ADD_PRINT_TEXT(122,34,80,25,"科室");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(122,114,80,25,"科室值");
				LODOP.ADD_PRINT_TEXT(122,194,80,25,"病区");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(122,274,80,25,"病区值");
				LODOP.ADD_PRINT_TEXT(147,34,80,25,"预缴款金额");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(147,114,240,25,"预缴款金额值");
				LODOP.ADD_PRINT_TEXT(172,34,80,25,"人民币大写");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(172,114,240,25,"人民币大写值");
				LODOP.ADD_PRINT_TEXT(197,34,80,25,"缴款合计");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(197,114,80,25,"缴款合计值");
				LODOP.ADD_PRINT_TEXT(197,194,80,25,"票(卡)号码");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(197,274,80,25,"票(卡)号码值");
				LODOP.ADD_PRINT_TEXT(222,34,80,25,"自负合计");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(222,114,80,25,"自负合计值");
				LODOP.ADD_PRINT_TEXT(222,194,80,25,"剩余合计");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(222,274,80,25,"剩余合计值");
				LODOP.ADD_PRINT_TEXT(250,47,60,25,"收款员");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(250,107,80,25,"收款员值");
				LODOP.ADD_PRINT_TEXT(250,191,60,25,"缴款日期");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(250,251,86,25,"缴款日期值");
				LODOP.PRINT_SETUP();
			},
			doPrint : function(jkxh) {
				this.opener.JKXH = false;
				var LODOP=getLodop();  
				LODOP.PRINT_INITA(10,12,390,300,"住院缴款打印");
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "hospitalAdmissionService",
					serviceAction : "printMoth",
					jkxh : jkxh
					});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.ADD_PRINT_TEXT(17,114,160,25,ret.json.title);
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(47,182,30,25,"NO.");
				LODOP.ADD_PRINT_TEXT(47,212,140,25,ret.json.PJHM);
				LODOP.ADD_PRINT_TEXT(72,34,80,25,"住院号码");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(72,114,80,25,ret.json.ZYHM);
				LODOP.ADD_PRINT_TEXT(72,194,80,25,"床号");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(72,274,80,25,ret.json.BRCH);
				LODOP.ADD_PRINT_TEXT(97,34,80,25,"姓名");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(97,114,80,25,ret.json.BRXM);
				LODOP.ADD_PRINT_TEXT(97,194,80,25,"缴款方式");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(97,274,80,25,ret.json.JKFS);
				LODOP.ADD_PRINT_TEXT(122,34,80,25,"科室");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(122,114,80,25,ret.json.KSMC);
				LODOP.ADD_PRINT_TEXT(122,194,80,25,"病区");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(122,274,80,25,ret.json.BQMC);
				LODOP.ADD_PRINT_TEXT(147,34,80,25,"预缴款金额");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(147,114,240,25,ret.json.JKJE);
				LODOP.ADD_PRINT_TEXT(172,34,80,25,"人民币大写");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(172,114,240,25,ret.json.DXJE);
				LODOP.ADD_PRINT_TEXT(197,34,80,25,"缴款合计");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(197,114,80,25,ret.json.JKHJ);
				LODOP.ADD_PRINT_TEXT(197,194,80,25,"票(卡)号码");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(197,274,80,25,ret.json.PKHM);
				LODOP.ADD_PRINT_TEXT(222,34,80,25,"自负合计");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(222,114,80,25,ret.json.ZFHJ);
				LODOP.ADD_PRINT_TEXT(222,194,80,25,"剩余合计");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(222,274,80,25,ret.json.SYHJ);
				LODOP.ADD_PRINT_TEXT(250,47,60,25,"收款员");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(250,107,80,25,ret.json.JSR);
				LODOP.ADD_PRINT_TEXT(250,191,60,25,"缴款日期");
				LODOP.SET_PRINT_STYLEA(0,"Alignment",2);
				LODOP.ADD_PRINT_TEXT(250,251,86,25,ret.json.JKRQ);
				if((ret.json.FPYL+"")=='1'){
					LODOP.PREVIEW();
				}else{
					LODOP.PRINT();
				}
			}
//			doPrint : function(jkxh) {
//				this.opener.JKXH = false;
//				var module = this.createModule("paymentprint", this.refPayment)
//				var form = this.form.getForm();
//				if (form) {
//					module.jkxh = jkxh;
//					module.initPanel();
//					module.doPrint();
//				} else {
//					MyMessageTip.msg("提示", "打印失败：无效的缴款信息!", true);
//				}
//			}
		});