$package("phis.application.hos.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout","phis.application.yb.script.MedicareCommonMethod",
		"phis.script.util.FileIoUtil","phis.script.Phisinterface",
		"phis.script.DatamatrixReader",
		"phis.application.pay.script.PayCommon")
debugger;
phis.application.hos.script.HospitalSettleAccountsForm = function(cfg) {
	cfg.autoLoadData=false;
	cfg.showButtonOnTop=false;
	Ext.apply(this,phis.application.yb.script.MedicareCommonMethod);
	Ext.apply(this,phis.script.Phisinterface);
	Ext.apply(this,phis.script.DatamatrixReader);
	phis.application.hos.script.HospitalSettleAccountsForm.superclass.constructor.apply(this, [cfg])
	this.isFillYbFee = false;
}

Ext.extend(phis.application.hos.script.HospitalSettleAccountsForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form = new Ext.FormPanel({
							labelWidth : 55, // label settings here cascade
							// unless overridden
							frame : true,
							bodyStyle : 'padding:5px 5px 0',
							width : 280,
							height : 315,
							buttonAlign : 'center',
							items : [{
										xtype : 'fieldset',
										title : '基本信息',
										autoHeight : true,
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
										items : this.getItems('JBXX')
									}, {
										xtype : 'fieldset',
										id : 'FPHM',
										title : '结算金额 ',
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
										items : this.getItems('JSJE')
									}, {
										xtype : 'fieldset',
										title : '结算金额补退',
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
										items : this.getItems('JSJEBT')
									}],
							buttons : (this.tbar||[]).concat(this.createButton())
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
				this.combo = this.form.getForm().findField("FKFS");
				this.combo.on("select", this.calYSK, this);
				this.combo.getStore().on('load', this.calYSKLoadEvent, this);
				/*****begin 结算界面增加扫码支付 zhaojian 2020-02-12 *****/
				var ewm = this.form.getForm().findField("ewm");
				ewm.on("focus", this.onEWMfocus, this);
				ewm.on("blur", this.onEWMblur, this);
				ewm.on("specialkey", this.onEWMchange, this);
				/*****end 结算界面增加扫码支付 zhaojian 2020-02-12 *****/
				return this.form
			},
			// 回显付费方式事件
			calYSKLoadEvent : function(store, records) {
				var delcount = 0;
				//过滤付款方式下拉框选项，只保留 现金、微信、支付宝
				store.each(function(item, index) {
					if(item.data.key!=6 && item.data.key!=50){
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
				var payType = {};
				Ext.apply(payType, record.json);
				//var form = this.form.getForm();
				payType.YSK = this.round2(this.round2(this.data.ZFHJ, 2)
								- this.round2(this.data.ZHZF, 2)
								- this.round2(this.data.JKHJ, 2), 2);
				this.rmiYSKQuery(payType)
			},
			/*****begin 结算界面增加扫码支付 zhaojian 2020-02-12 *****/
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
					this.doCommit();
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
			/*****end 结算界面增加扫码支付 zhaojian 2020-02-12 *****/
			rmiYSKQuery : function(payType) {
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
					this.form.getForm().findField("YSJE").setValue(ysk);
					if (ysk < 0) {
						this.form.getForm().findField("JKJE").setValue("0");
						this.form.getForm().findField("TZJE").setValue(this.round2(-ysk,2));
						this.form.getForm().findField("YSJE").setValue(this.round2(-ysk,2));
					}
					this.data.YSJE = ysk;
				}
				if (this.combo.value == 50){
					this.data.WCJE = 0;
				}else if(parseFloat(this.data.JSJE) != parseFloat(this.data.YSJE)) {
					this.data.WCJE=this.round2(this.round2(this.round2(parseFloat(this.data.JSJE)-parseFloat(this.data.YSJE),2),this.data.WCJD-1),2)
				}
				var jkje = this.form.getForm().findField("JKJE");
				jkje.setValue(ysk);
				this.form.getForm().findField("TZJE").setValue(0);
				if(this.combo.value==50){
					this.form.getForm().findField("ewm").focus(false, 100);
				}else{
					jkje.setValue();
					jkje.focus(false, 200)
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
					if ((it.display==0 || it.display==1)|| !ac.canRead(it.acValue)) {
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					if (it.id == "FYHJ" || it.id == "JKHJ" || it.id == "ZFHJ"
							|| it.id == "TCJE" || it.id == "ZHZF" || it.id == "YBZF") {
						f.style += 'font-size:16px;font-weight:bold;color:blue;textAlign:right;';
					} else if (it.id == "YSJE" || it.id == "TZJE") {
						f.style += 'font-size:16px;font-weight:bold;color:red;textAlign:right;';
					} else if (it.id == "JKJE") {
						f.style = 'font-size:16px;font-weight:bold;color:red;textAlign:right;';
					}
					MyItems.push(f);
				}
				return MyItems;
			},
			createButton : function() {
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
								eval(script + '.do'+cmd+'.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doCancel : function() {
				this.opener.doCancel();
			},
			doNew : function() {
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					if (it.id == "FKFS")
						continue;
					var f = form.findField(it.id)
					if (f) {
						f.setValue(it.defaultValue)
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}
						// add by yangl 2012-06-29
						if (it.dic && it.dic.defaultIndex) {
							if (f.store.getCount() == 0)
								return;
							if (isNaN(it.dic.defaultIndex)
									|| f.store.getCount() <= it.dic.defaultIndex)
								it.dic.defaultIndex = 0;
							f.setValue(f.store.getAt(it.dic.defaultIndex).get('key'));
						}
					}
				}
			},
			
			loadData : function() {
				if (this.loading) {
					return
				}
				if (this.form.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				// if (!this.data.ZHSYBZ) {
				// this.data.ZFHJ = this.round2(parseFloat(this.data.ZFHJ)
				// + parseFloat(this.data.ZHZF), 2)
				// this.data.ZHZF = 0.00;
				// }
				this.loading = true
				this.isFillYbFee = false;
				phis.script.rmi.jsonRequest({
							serviceId : "hospitalPatientSelectionService",
							serviceAction : "getSelectionFPHM"
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this_ = this;
								Ext.Msg.alert("提示", msg, function() {
											this_.doCancel();
										});
								return
							}
							if (json.FPHM) {
								this.data.FPHM = json.FPHM;
								var FPHM = this.form.findById("FPHM");
								FPHM.setTitle("结算金额 No." + json.FPHM);
							} else {
								this_ = this;
								Ext.Msg.alert("提示", "请先维护住院发票号码!", function() {
											this_.doCancel();
										});
							}
							if (json.body) {
								this.data.FKJD = json.body.FKJD;
								this.data.FKFS = json.body.FKFS;
								this.data.WCFS = json.body.WCFS;
								this.data.WCJD = json.body.WCJD;
								var form = this.form.getForm()
								var YSJE = form.findField("YSJE");
								var JKJE = form.findField("JKJE");
								var TZJE = form.findField("TZJE");
								var ZFHJ = form.findField("ZFHJ");
								var ZHZF = form.findField("ZHZF");
								var YBZF = this.data.YBZF==null?0:this.data.YBZF;
								if(this.data.BRXZ && this.data.BRXZ==3000){//add by lizhi 2017-11-03自负金额=总费用-医保支付-账户支付
									this.data.ZFHJ=this.round2((this.data.FYHJ-this.data.YBZF-this.data.ZHZF),2);
								}else if(this.data.BRXZ && this.data.BRXZ==2000){
									//南京金保不改自负
								}else if(this.data.TCJE){
									this.data.ZFHJ=this.round2((this.data.FYHJ-this.data.TCJE),2);
								}
								ZFHJ.setValue(this.data.ZFHJ);
								ZHZF.setValue(this.data.ZHZF);
								var ysje = this.round2(this.round2(this.data.FYHJ, 2)
												- this.round2(this.data.ZHZF,2)
												- this.round2(this.data.JKHJ,2)
												- this.round2(YBZF, 2), 2);
								if(this.data.TCJE){
									ysje=this.round2(ysje,2)-this.round2(this.data.TCJE,2);
								}				
								this.data.JSJE = ysje;
								if (ysje < 0) {
									ysje = this.round2(this.round2(-ysje,json.body.FKJD - 1), 2);
									ysje = -ysje;
								} else {
									ysje = this.round2(this.round2(ysje,json.body.FKJD - 1), 2);
								}
								this.data.YSJE = ysje;
								this.data.WCJE = 0;
								if (this.combo.value != 50 && parseFloat(this.data.JSJE) != parseFloat(this.data.YSJE)) {
									this.data.WCJE=this.round2(this.round2(this.round2(parseFloat(this.data.JSJE)-parseFloat(this.data.YSJE),2),json.body.WCJD-1),2)
								}
								YSJE.setValue(ysje);
								if (ysje < 0) {
									JKJE.setValue("0.00");
									TZJE.setValue(this.round2(-ysje, 2));
									var YSJEField = YSJE.el.parent().parent().first();
									YSJEField.dom.innerHTML = "应找:";
									YSJE.setValue(this.round2(-ysje, 2));
								} else {
									var YSJEField = YSJE.el.parent().parent().first();
									YSJEField.dom.innerHTML = "应收:";
								}
							}
						}, this)
			},
			round2 : function(number, fractionDigits) {
				with (Math) {
					return (round(number*pow(10, fractionDigits))/pow(10,fractionDigits)).toFixed(fractionDigits);
				}
			},
			JKJEblur : function() {
				this.focusFieldAfter();
				if(this.JSLX==4){
					if (this.JKJEChang) {
						return;
					}
					this.JKJEChang = true;
					var form = this.form.getForm();
					var ZFHJ = form.findField("ZFHJ");
					var JKJE = form.findField("JKJE");
					var JKHJ = form.findField("JKHJ");
					var YSJE = form.findField("YSJE");
					var TZJE = form.findField("TZJE");
					if (JKJE.getValue() == null || JKJE.getValue().length == 0) {
						JKJE.setValue("0.00");
						TZJE.setValue(-YSJE.getValue());
					} else {
							TZJE.setValue(this.round2(JKJE.getValue()-this.data.YSJE,2));
					}
					btns = this.form.buttons
					if (btns) {
						var n = btns.length;
						for (var i = 0; i < n; i++) {
							var btn = btns[i];
							if (btn.cmd == "commit") {
								btn.focus()
								this.JKJEChang = false;
								return;
							}
						}
					}
					return;
				}
				if (this.JKJEChang) {
					return;
				}
				this.JKJEChang = true;
				var form = this.form.getForm();
				var ZFHJ = form.findField("ZFHJ");
				var JKJE = form.findField("JKJE");
				var JKHJ = form.findField("JKHJ");
				var YSJE = form.findField("YSJE");
				var TZJE = form.findField("TZJE");
				if (JKJE.getValue() == null || JKJE.getValue().length == 0) {
					if (this.data.YSJE < 0) {
						JKJE.setValue("0.00");
						TZJE.setValue(YSJE.getValue());
					} else {
						JKJE.setValue(YSJE.getValue());
						TZJE.setValue("0.00");
					}
				} else {
					if (parseFloat(JKJE.getValue()) < parseFloat(this.data.YSJE)) {
						JKJE.setValue();
						Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!",
								function() {
									TZJE.setValue();
									JKJE.focus(false, 200);
									this.JKJEChang = false;
								}, this);
						return;
					} else {
						TZJE.setValue(this.round2(JKJE.getValue()-this.data.YSJE,2));
					}
				}
				btns = this.form.buttons
				if (btns) {
					var n = btns.length;
					for (var i = 0; i < n; i++) {
						var btn = btns[i];
						if (btn.cmd == "commit") {
							btn.focus()
							this.JKJEChang = false;
							return;
						}
					}
				}
			},
			JKJECommit : function(f, e) {
				var key = e.getKey()
				if (key == e.ENTER) {
					this.focusFieldAfter();
					if(this.JSLX==4){
						if (this.JKJEChang) {
							return;
						}
						this.JKJEChang = true;
						var form = this.form.getForm();
						var ZFHJ = form.findField("ZFHJ");
						var JKJE = form.findField("JKJE");
						var JKHJ = form.findField("JKHJ");
						var YSJE = form.findField("YSJE");
						var TZJE = form.findField("TZJE");
						if (JKJE.getValue() == null || JKJE.getValue().length == 0) {
							JKJE.setValue("0.00");
							TZJE.setValue(YSJE.getValue());
						} else {
							TZJE.setValue(this.round2(JKJE.getValue()-this.data.YSJE, 2));
						}
						btns = this.form.buttons
						if (btns) {
							var n = btns.length;
							for (var i = 0; i < n; i++) {
								var btn = btns[i];
								if (btn.cmd == "commit") {
									btn.focus()
									this.JKJEChang = false;
									return;
								}
							}
						}
						return;
					}
					if (this.JKJEChang) {
						return;
					}
					this.JKJEChang = true;
					var form = this.form.getForm();
					var ZFHJ = form.findField("ZFHJ");
					var JKJE = form.findField("JKJE");
					var JKHJ = form.findField("JKHJ");
					var YSJE = form.findField("YSJE");
					var TZJE = form.findField("TZJE");
					if (JKJE.getValue() == null || JKJE.getValue().length == 0) {
						if (this.data.YSJE < 0) {
							JKJE.setValue("0.00");
							TZJE.setValue(YSJE.getValue());
						} else {
							JKJE.setValue(YSJE.getValue());
							TZJE.setValue("0.00");
						}
					} else {
						if (parseFloat(JKJE.getValue()) < parseFloat(this.data.YSJE)) {
							JKJE.setValue();
							Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!",
									function() {
										TZJE.setValue();
										JKJE.focus(false, 200);
										this.JKJEChang = false;
									}, this);
							return;
						} else {
							TZJE.setValue(this.round2(JKJE.getValue()-this.data.YSJE,2));
						}
					}
					btns = this.form.buttons
					if (btns) {
						var n = btns.length;
						for (var i = 0; i < n; i++) {
							var btn = btns[i];
							if (btn.cmd == "commit") {
								btn.focus()
								this.JKJEChang = false;
								return;
							}
						}
					}
				}
			},
			//结算开始
			doCommit : function() {				
											//循环执行退款操作
											/*var refunddata ={};
											refunddata.ID_REFUND = "611";//退号单id
											refunddata.PAYSERVICE = "-4";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费，-3预交金退费，-4住院退费'
											//refunddata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP
											//refunddata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
											refunddata.IP = "192.168.110.73";//测试时使用
											refunddata.COMPUTERNAME = "SHOUFEI3";//测试时使用
											refunddata.HOSPNO = generateHospno("ZYTF","4490342");//医院流水号
											refunddata.PAYMONEY = "0.01";//支付金额
											refunddata.VOUCHERNO = "4490342";//就诊号码或发票号码
											//refunddata.ORGANIZATIONCODE = getorders.json.body.orders[i].ORGANIZATIONCODE;//机构编码
											refunddata.ORGANIZATIONCODE = "320124001";//测试时使用
											refunddata.PATIENTYPE = "2000";//病人性质
											refunddata.PATIENTID = "327620";//病人id
											refunddata.NAME = "赵晓青";//病人姓名
											refunddata.SEX = "1";//性别
											refunddata.IDCARD = "320124197410051437";//身份证号
											refunddata.BIRTHDAY = "1974-10-05";//出生年月
											refunddata.PAYSOURCE = "1";//支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
											//refunddata.TERMINALNO = "";//支付终端号 如POS01、1号窗
											//refunddata.COLLECTFEESCODE = this.mainApp.uid;//操作员代码
											//refunddata.COLLECTFEESNAME = this.mainApp.uname;//操作员姓名
											refunddata.COLLECTFEESCODE = "0310581X";//测试时使用
											refunddata.COLLECTFEESNAME = "管理员";//测试时使用
											refunddata.HOSPNO_ORG = "ZYSF202003072016394490342";//退款交易时指向原HOSPNO
											refund(refunddata,this);
											return;*/
				
				if (this.running || this.runningTask) {
					return;
				}
				this.running = true;
				/*****begin 结算界面增加扫码支付 zhaojian 2020-02-12 *****/
				var form = this.form.getForm();
				if((this.combo.value==50) && (typeof(QRCODE)=='undefined' || QRCODE=="")){
					Ext.Msg.alert("提示", "未获取到付款码，请扫码支付！",
							function() {
								form.findField("ewm").setValue("");
								form.findField("ewm").focus(false,200);
								this.running = false;
							}, this);
					return;
				}
				/*****end 结算界面增加扫码支付 zhaojian 2020-02-12 *****/
				//add-yx-校验金额
				if(this.data.BRXZ=="2000" && this.data.JSXZ=="医保"){
					if(Math.abs(parseFloat(this.data.FYHJ)-(parseFloat(this.data.TCJE)+parseFloat(this.data.ZFHJ)+parseFloat(this.data.YBZF)+parseFloat(this.data.ZHZF)))>0.04){
						Ext.Msg.alert("提示", "合计费用与其他费用之和不一致，请重新上传明细后再结算，如还有问题联系工程师！", function() {
							this.running = false;
						}, this);
						return;
					}
				} 
				this.data.BRID = this.opener.opener.data.BRID;
				if(this.JSLX==4){
					//出院终结业务实现
					this.saving = true
					var body = {};
					body.data = this.data;
					body.MZXX = this.MZXX;
					body.jsData = this.jsData;
					//var form = this.form.getForm();
					var JKJE = form.findField("JKJE");
					var TZJE = form.findField("TZJE");
					if (JKJE.getValue() > 9999999.99) {
						Ext.Msg.alert("提示", "交款金额最大不能大于等于1000万!", function() {
									JKJE.setValue()
									TZJE.setValue();
									JKJE.focus(false, 200);
									this.running = false;
								});
						this.saving = false;
						return;
					}
					var YSJE = form.findField("YSJE");
					if (!JKJE.getValue()) {
						JKJE.setValue("0.00");
						TZJE.setValue(this.round2(-this.data.YSJE, 2));
					} else {
						TZJE.setValue(this.round2(JKJE.getValue()-this.data.YSJE, 2));
					}
					var JSLX = form.findField("JSLX").getValue();
					var ZFHJ = form.findField("ZFHJ").getValue();
					var ZYHM = form.findField("ZYHM").getValue();
					var JKHJ = form.findField("JKHJ").getValue();
					this.data.JKJE = JKJE.getValue();
					this.data.TZJE = TZJE.getValue();
					this.data.FKFS = form.findField("FKFS").getValue();
					var _ctr = this;
					this.form.el.mask("正在保存数据...","x-mask-loading")
					var jsbody = {}
					jsbody.JSXX = this.data;
					phis.script.rmi.jsonRequest({
								serviceId : "hospitalPatientSelectionService",
								serviceAction : "saveSettleAccounts",
								body : jsbody
							}, function(code, msg, json) {
								this.form.el.unmask()
								this.saving = false
								if (code > 300) {
									this.processReturnMsg(code,msg,this.saveToServer,[body]);
									return
								}
								MyMessageTip.msg("提示", "结算成功!", true);
								this.fireEvent("settlement", this);
							}, this)
					return;
				}
				this.saving=true
				var body={};
				body.data=this.data;
				body.MZXX=this.MZXX;
				body.jsData=this.jsData;
				var form=this.form.getForm();
				var JKJE=form.findField("JKJE");
				var TZJE=form.findField("TZJE");
				if (JKJE.getValue() > 9999999.99) {
					Ext.Msg.alert("提示", "交款金额最大不能大于等于1000万!",function(){
						JKJE.setValue()
						TZJE.setValue();
						JKJE.focus(false, 200);
					});
					this.saving=false;
					return;
				}
				var YSJE = form.findField("YSJE");
				if (!JKJE.getValue()){
					if (this.data.YSJE < 0) {
						JKJE.setValue("0.00");
						TZJE.setValue(this.round2(-this.data.YSJE, 2));
					} else {
						JKJE.setValue(this.data.YSJE);
						TZJE.setValue("0.00");
					}
				}else{
					if (parseFloat(JKJE.getValue()) < parseFloat(this.data.YSJE)) {
						JKJE.setValue("");
						Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!",function() {
							TZJE.setValue();
							JKJE.focus(false, 200);
						});
						this.saving = false;
						return;
					} else {
						TZJE.setValue(this.round2(JKJE.getValue()-this.data.YSJE,2));
					}
				}
				var JSLX = form.findField("JSLX").getValue();
				var ZFHJ = form.findField("ZFHJ").getValue();
				var ZYHM = form.findField("ZYHM").getValue();
				var JKHJ = form.findField("JKHJ").getValue();
				if (JSLX == 1 && (ZFHJ * 1 == 0) && JKHJ * 1 == 0) {
					Ext.Msg.alert("提示", "该病人当前自负费用为0且无预交款,不允许进行中途结算！");
				}else {
					this.data.JKJE = JKJE.getValue();
					this.data.TZJE = TZJE.getValue();
					this.data.FKFS = form.findField("FKFS").getValue();
					var _ctr=this;
					this.form.el.mask("正在保存数据...", "x-mask-loading")
					var jsbody = {}
//					if (this.data.YBJSCS) {// 医保
//						var data = this.data.YBJSCS;
//						var zy_jxss=this.doYbzyjs(data);//医保结算,返回结算金额等信息
//						if(!zy_jxss){
//						return;
//						}
//						var jsbody = {};
//						jsbody.YBJS = zy_jxss;
//						jsbody.JSXX = this.data;
//						phis.script.rmi.jsonRequest({
//									serviceId : "hospitalPatientSelectionService",
//									serviceAction : "saveSettleAccounts",
//									body : jsbody
//								}, function(code, msg, json) {
//									this.form.el.unmask()
//									this.saving = false
//									if (code > 300) {
//										this.processReturnMsg(code, msg,this.saveToServer, [body]);
//										// 如果本地保存失败,医保取消结算
//										this.doYbzyjscx();
//										return;
//									}
//									this.doPrintFp(json.FPHM)
//									this.fireEvent("settlement", this);
//								}, this)
//						
//					}else 
					if(this.data.NJJB){
						//南京金保交易流程
						var data1 = this.data.jsxx;
						var data = {};
						var body={};
						body.USERID=this.mainApp.uid;
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId:"phis.NjjbService",
							serviceAction:"getywzqh",
							body:body
						});
						if (ret.code <= 300) {
							var ywzqh=ret.json.YWZQH;
							this.addPKPHISOBJHtmlElement();
							this.drinterfaceinit();
							var jylsh = this.buildjylsh();
							data.JYLSH = jylsh;
							//费用结算
							debugger;
							var str=this.buildstr_zy("2410",ywzqh,data);
							var drre=this.drinterfacebusinesshandle(str+this.data.zycs+this.data.FPHM+"|"+this.data.cs+"^");
							var arr=drre.split("^");
							if(arr[0]=="0"){
								var reformat = "BCYLFZE|BCTCZFJE|BCDBJZZF|BCDBBXZF|BCMZBZZF|BCZHZFZE|BCXZZFZE|BCZHZFZF|BCZHZFZL|BCXJZFZF|BCXJZFZL|YBFWNFY|ZHXFHYE|DBZBZBM|SMXX|YFHJ|ZLXMFHJ|BBZF|YLLB|BY6";
					    		var reData = this.StrToObj(reformat,arr[2],"|");
					    		reData.LSH = this.data.zylsh;
					    		reData.ZYH = this.data.ZYH
					    		reData.ZFPB = 0
					    		reData.JYLSH = jylsh
								var zy_jxss = reData
							}else{
								MyMessageTip.msg("提示：",arr[3], true);
								this.panel.el.unmask();
								return;	
							}
						}else {
							MyMessageTip.msg("提示：",ret.msg, true);
							return;
						}
						jsbody.JSXX = this.data;
						jsbody.YBJS = zy_jxss;
						jsbody.JSLX=this.JSLX;
						this.BODY = jsbody;
					}else {
						jsbody.JSXX = this.data;
						this.BODY = jsbody;
					}
					/*****begin 挂号结算界面增加扫码支付 zhaojian 2020-02-12 *****/	
					//微信或支付宝付款成功后再继续HIS中的结算流程
					if((this.combo.value==50) && typeof(QRCODE)!='undefined' && QRCODE!=""){
						jsbody.JSXX.FKFS = getFKFS(QRCODE,1);
						var paydata ={};
						paydata.PAYSERVICE = "4";//业务类型：1挂号 2收费 3住院预交金 4住院结算,5病历本, -1退号，-2退费，-3预交金退费，-4住院退费'
						paydata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP
						paydata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
						//paydata.IP = "192.168.110.73";//测试时使用
						//paydata.COMPUTERNAME = "SHOUFEI3";//测试时使用
						paydata.HOSPNO = generateHospno("ZYSF",this.data.FPHM);//医院流水号
						this.data.HOSPNO = paydata.HOSPNO;
						paydata.PAYMONEY = this.data.JKJE;//支付金额
						paydata.VOUCHERNO = this.data.FPHM;//就诊号码或发票号码
						paydata.ORGANIZATIONCODE = this.mainApp.deptId;//机构编码
						//paydata.ORGANIZATIONCODE = "320124001";//测试时使用
						paydata.PATIENTYPE = this.data.BRXZ;//病人性质
						paydata.PATIENTID = this.data.BRID;//病人id
						paydata.NAME = this.data.BRXM;//病人姓名
						paydata.SEX = this.data.BRXB;//性别
						paydata.IDCARD = this.data.SFZH;//身份证号
						paydata.BIRTHDAY = this.data.CSNY;//出生年月
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
					/*****end 挂号结算界面增加扫码支付 zhaojian 2020-02-12 *****/	
					QRCODE="";
					this.doCommit2();	
				}
			},
			doCommit2 : function() {			
				this.form.el.mask("正在保存数据...", "x-mask-loading")				
				phis.script.rmi.jsonRequest({
					serviceId : "hospitalPatientSelectionService",
					serviceAction : "saveSettleAccounts",
					body : this.BODY
				}, function(code, msg, json) {
					this.JSON = json;// 移动支付 zhaojian 2020-02-12
					this.form.el.unmask()
					this.saving = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[this.body]);
						this.fireEvent("settlement", this);
						//this.doConcel();
						/** ***begin 挂号结算界面增加扫码支付 zhaojian 2020-02-12 **** */
						// 如果本地失败，调用支付平台取消微信支付宝付款
						if (this.combo.value == 50) {
							var refunddata = {};
							refunddata.PAYSERVICE = "-4";// 业务类型：1挂号 2收费 3住院预交金 4住院结算, 5病历本, -1退号，-2退费，-3预交金退费，-4住院退费'
							refunddata.IP = this.GetIpAddressAndHostname().split(",")[0];//支付终端IP
							refunddata.COMPUTERNAME = this.GetIpAddressAndHostname().split(",")[1].toUpperCase();//支付终端电脑名称
							//refunddata.IP = "192.168.110.73";// 测试时使用
							//refunddata.COMPUTERNAME = "SHOUFEI3";// 测试时使用
							refunddata.HOSPNO = generateHospno("ZYTF", this.data.FPHM);// 医院流水号
							refunddata.PAYMONEY = this.data.JKJE;// 支付金额
							refunddata.VOUCHERNO = this.data.FPHM;// 就诊号码或发票号码
							refunddata.ORGANIZATIONCODE = this.mainApp.deptId;// 机构编码
							//refunddata.ORGANIZATIONCODE = "320124001";//测试时使用
							refunddata.PATIENTYPE = this.data.BRXZ;// 病人性质
							refunddata.PATIENTID = this.data.BRID;// 病人id
							refunddata.NAME = this.data.BRXM;// 病人姓名
							refunddata.SEX = this.data.BRXB;// 性别
							refunddata.IDCARD = "";// 身份证号
							refunddata.BIRTHDAY = this.data.CSNY;// 出生年月
							refunddata.PAYSOURCE = "1";// 支付来源：1窗口 2自助机 3App 4、pc网页支付 5、短信链接支付
							//refunddata.TERMINALNO = "";//支付终端号 如POS01、1号窗
							refunddata.COLLECTFEESCODE = this.mainApp.uid;//操作员代码
							refunddata.COLLECTFEESNAME = this.mainApp.uname;//操作员姓名
							//refunddata.COLLECTFEESCODE = "0310581X";// 测试时使用
							//refunddata.COLLECTFEESNAME = "管理员";// 测试时使用
							refunddata.HOSPNO_ORG = this.data.HOSPNO;// 退款交易时指向原HOSPNO
							refund(refunddata,this);
							return;
						}
						/** ***end 挂号结算界面增加扫码支付 zhaojian 2020-02-12 **** */
						return
					}
					this.doCommit3();
				}, this)
				this.running = false;
			},
			doCommit3: function(){
				MyMessageTip.msg("提示", "结算成功!", true);
				this.doPrintFp(this.JSON.FPHM)
				this.fireEvent("settlement", this);
			},
			doPrintFp : function(fphm) {
				if(!fphm){
					return ;
				}
				this.opener.opener.opener.list.fphm = fphm;
			},
			focusFieldAfter : function(index, delay) {
				if(this.data && this.data.BRXZ && this.data.BRXZ==3000){
					this.focusOutField();
				}
			},
			fillYbFee : function(field, e){
				if (e.getKey() == 13 && !e.shiftKey) {
					if(this.isFillYbFee){//确定
						this.doCommit;
						this.isFillYbFee = false;
					}else{//以下是填充医保费用代码
						try{
							var xmlDoc = loadXmlDoc("c:\\njyb\\cyjsd.xml");
							var elements = xmlDoc.getElementsByTagName("RECORD");
							var form = this.form.getForm();
							for (var i = 0; i < elements.length; i++) {
								var ZFY = (elements[i].getElementsByTagName("ZFY")[0].firstChild)?(elements[i].getElementsByTagName("ZFY")[0].firstChild.nodeValue):0;
								var YBZF = (elements[i].getElementsByTagName("YBZF")[0].firstChild)?(elements[i].getElementsByTagName("YBZF")[0].firstChild.nodeValue):0;
								var ZHZF = (elements[i].getElementsByTagName("ZHZF")[0].firstChild)?(elements[i].getElementsByTagName("ZHZF")[0].firstChild.nodeValue):0;
								var XJZF = (elements[i].getElementsByTagName("XJZF")[0].firstChild)?(elements[i].getElementsByTagName("XJZF")[0].firstChild.nodeValue):0;
								var TCZF = (elements[i].getElementsByTagName("TCZF")[0].firstChild)?(elements[i].getElementsByTagName("TCZF")[0].firstChild.nodeValue):0;
								form.findField("TCJE").setValue(TCZF);//统筹金额
								form.findField("ZFHJ").setValue(XJZF);//自负
								form.findField("YBZF").setValue(YBZF);//医保支付
								form.findField("ZHZF").setValue(ZHZF);//账户支付
								var ysk = form.findField("YSJE");
								var ysje = this.round2(this.round2(form.findField("FYHJ").getValue(), 2)
											- this.round2(form.findField("ZHZF").getValue(),2)
											- this.round2(form.findField("JKHJ").getValue(),2)
											- this.round2(YBZF, 2), 2);
								ysk.setValue(ysje);
								var jkje = form.findField("JKJE");
								jkje.setValue(ysk.getValue());
								var tzje = form.findField("TZJE");
								tzje.setValue(parseFloat(jkje.getValue()) - parseFloat(ysk.getValue()));
								
								this.data.TCJE = form.findField("TCJE").getValue();
								this.data.ZFHJ = form.findField("ZFHJ").getValue();
								this.data.YBZF = form.findField("YBZF").getValue();
								this.data.ZHZF = form.findField("ZHZF").getValue();
								this.data.YSJE = form.findField("YSJE").getValue();
								this.data.JKJE = form.findField("JKJE").getValue();
								this.data.TZJE = form.findField("TZJE").getValue();
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
					var xmlDoc = loadXmlDoc("c:\\njyb\\cyjsd.xml");
					var elements = xmlDoc.getElementsByTagName("RECORD");
					var form = this.form.getForm();
					for (var i = 0; i < elements.length; i++) {
						var ZFY = (elements[i].getElementsByTagName("ZFY")[0].firstChild)?(elements[i].getElementsByTagName("ZFY")[0].firstChild.nodeValue):0;
						var YBZF = (elements[i].getElementsByTagName("YBZF")[0].firstChild)?(elements[i].getElementsByTagName("YBZF")[0].firstChild.nodeValue):0;
						var ZHZF = (elements[i].getElementsByTagName("ZHZF")[0].firstChild)?(elements[i].getElementsByTagName("ZHZF")[0].firstChild.nodeValue):0;
						var XJZF = (elements[i].getElementsByTagName("XJZF")[0].firstChild)?(elements[i].getElementsByTagName("XJZF")[0].firstChild.nodeValue):0;
						var TCZF = (elements[i].getElementsByTagName("TCZF")[0].firstChild)?(elements[i].getElementsByTagName("TCZF")[0].firstChild.nodeValue):0;
						form.findField("TCJE").setValue(TCZF);//统筹金额
						form.findField("ZFHJ").setValue(XJZF);//自负
						form.findField("YBZF").setValue(YBZF);//医保支付
						form.findField("ZHZF").setValue(ZHZF);//账户支付
						var ysje = this.round2(this.round2(form.findField("FYHJ").getValue(), 2)
									- this.round2(form.findField("ZHZF").getValue(),2)
									- this.round2(form.findField("JKHJ").getValue(),2)
									- this.round2(YBZF, 2), 2);
						var ysk = form.findField("YSJE");
						ysk.setValue(ysje);
						var jkje = form.findField("JKJE");
						var tzje = form.findField("TZJE");
						tzje.setValue(parseFloat(jkje.getValue()) - parseFloat(ysk.getValue()));
						this.data.TCJE = form.findField("TCJE").getValue();
						this.data.ZFHJ = form.findField("ZFHJ").getValue();
						this.data.YBZF = form.findField("YBZF").getValue();
						this.data.ZHZF = form.findField("ZHZF").getValue();
						this.data.YSJE = form.findField("YSJE").getValue();
						this.data.JKJE = form.findField("JKJE").getValue();
						this.data.TZJE = form.findField("TZJE").getValue();
						this.isFillYbFee = true;
					}
				}catch(e){
					MyMessageTip.msg("提示", "文件解析失败!", true);
				}
			}
		});