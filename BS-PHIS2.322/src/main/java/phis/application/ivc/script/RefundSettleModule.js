/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");

phis.application.ivc.script.RefundSettleModule = function(cfg) {
	this.width = 480;
	this.height = 310;
	phis.application.ivc.script.RefundSettleModule.superclass.constructor
			.apply(this, [ cfg ]);
	this.on('winShow', this.onWinShow, this);
}

Ext.extend(phis.application.ivc.script.RefundSettleModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
//				var form = this.createForm();
				var panel = new Ext.Panel({
					border : false,
					width : this.width,
					height : this.height,
					// frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					buttonAlign : 'center',
					items : [ {
						layout : "fit",
						border : false,
						// split : true,
						title : '',
						region : 'west',
						width : 240,
						items : this.getLeftForm()
					}, {
						layout : "fit",
						border : false,
						// split : true,
						title : '',
						region : 'center',
						items : this.getRightForm()
					} ],
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
				this.panel = panel;
				return panel;
			},
			getLeftForm : function(){
				this.LeftForm = this.createModule("leftForm", this.leftForm);
				this.LeftForm.opener = this;
				var m = this.LeftForm.initPanel();
				var JKJE = m.form.findField('JKJE');
				JKJE.on("blur", this.LeftForm.focusFieldAfter, this.LeftForm);
				JKJE.on("specialkey", this.focusToButton, this);
				return m;
			},
			getRightForm : function() {
				this.RightForm = this.createModule("rightForm", this.rightForm);
				this.RightForm.opener = this;
				return this.RightForm.initPanel();
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
						// alert(it.acValue);
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width
					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			},
			doConcel : function() {
				win = this.getWin();
				this.fireEvent("winClose", this);
				if (win)
					win.hide();
			},
			onWinShow : function() {
				if (this.LeftForm) {
					this.LeftForm.afterShow();
				}
			},
			focusToButton : function(){
				btns = this.panel.buttons
				if (btns) {
					var n = btns.length;
					for (var i = 0; i < n; i++) {
						var btn = btns[i];
						if (btn.cmd == "commit") {
							btn.focus()
//							this.runing = false;
							return;
						}
					}
				}
			},
			doCommit:function(){
				if(this.saving)return;
				this.saving = true
				var body = {};
				body.data = this.LeftForm.data;
				body.MZXX = this.LeftForm.MZXX;
				var form = this.LeftForm.form.getForm();
				var jkje = form.findField("JKJE");
				if (jkje.getValue() > 9999999.99) {
					Ext.Msg.alert("提示", "交款金额最大不能大于等于1000万!", function() {
								jkje.setValue()
								form.findField("TZJE").setValue();
								jkje.focus(false, 200);
							}, this);
					this.saving = false
					return;
				}
				var ysk = form.findField("YSK");
				if (!jkje.getValue()) {
					form.findField("JKJE").setValue("0");
					form.findField("TZJE").setValue(-ysk.getValue());
				} else {
					if (parseFloat(jkje.getValue()) < parseFloat(ysk.getValue())) {
						jkje.setValue("");
						Ext.Msg.alert("提示", "交款金额不足,请按确定键,然后重新输入金额!", function() {
									form.findField("TZJE").setValue();
									jkje.focus(false, 200);
								}, this);
						this.saving = false
						return;
					} else {
						var tzje = form.findField("TZJE");
						tzje.setValue(jkje.getValue() - ysk.getValue());
					}
				}
//				this.MZXX.FPHM = form.findField("FPHM").getValue();// 发票号码
				body.MZXX.JKJE = body.MZXX.JKJE-body.MZXX.TZJE+form.findField("JKJE").getValue();// 交款金额
				body.MZXX.TZJE = form.findField("TZJE").getValue();// 退找金额
				body.MZXX.ZFJE = this.LeftForm.jsxx.ZFJE;// 其他应收
				body.MZXX.ZJJE = this.LeftForm.jsxx.ZJJE;// 总计金额
				body.MZXX.YSK = this.LeftForm.jsxx.YSK;// 自负金额
				body.MZXX.JJZF = 0;
				if(this.LeftForm.jsxx.JJZF){
					body.MZXX.JJZF = this.LeftForm.jsxx.JJZF;// 统筹支出
				}
//				this.MZXX.ZHZF = form.findField("ZHZF").getValue();// 帐户支付
				body.FFFS = this.LeftForm.jsxx.FKFS;// 付款方式
				
				this.panel.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "saveRefundSettle",
							body : body,
							FPHM : {"FPHM":this.opener.FPHM}
						}, function(code, msg, json) {
							this.panel.el.unmask();
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.saveToServer,
										[body]);
								return
							}
							this.opener.opener.JSXX = body.MZXX;
							this.fireEvent("settlement", this);
							this.doConcel();
							if (body.MZXX.YSK) {
								if(json.FPHM){
									this.doPrintFp(json.FPHM);
								}
							}
						}, this)
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('mzfp');"
			},
			doPrintFp : function(fphm){
				var LODOP=getLodop();  
//				LODOP.PRINT_INITA(10,10,762,533,"门诊收费发票2");
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "printMoth",
						fphm : fphm
					});
				this.fphm = false;
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				for(var i = 0 ; i < ret.json.mzfps.length ; i ++){
					var mzfp = ret.json.mzfps[i];
					LODOP.PRINT_INITA(10,10,950,450,"门诊收费发票2");
					LODOP.SET_PRINT_STYLE("ItemType",4);
					LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
					LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
					LODOP.ADD_PRINT_TEXT(25,60,130,25,mzfp.XLH);
					LODOP.ADD_PRINT_TEXT(25,390,150,25,mzfp.FPHM);
					LODOP.ADD_PRINT_TEXT(55,60,130,25,mzfp.XM);
					LODOP.ADD_PRINT_TEXT(55,390,60,25,mzfp.YYYY);
					LODOP.ADD_PRINT_TEXT(55,455,40,25,mzfp.MM);
					LODOP.ADD_PRINT_TEXT(55,500,40,25,mzfp.DD);
					LODOP.ADD_PRINT_TEXT(95,60,130,25,mzfp.JZ);
					LODOP.ADD_PRINT_TEXT(95,250,100,25,mzfp.GRJF);
					LODOP.ADD_PRINT_TEXT(95,390,100,25,mzfp.JSFS);
					LODOP.ADD_PRINT_TEXT(285,110,35,25,mzfp.SW);
					LODOP.ADD_PRINT_TEXT(285,160,25,25,mzfp.W);
					LODOP.ADD_PRINT_TEXT(285,200,25,25,mzfp.Q);
					LODOP.ADD_PRINT_TEXT(285,240,25,25,mzfp.B);
					LODOP.ADD_PRINT_TEXT(285,280,25,25,mzfp.S);
					LODOP.ADD_PRINT_TEXT(285,320,25,25,mzfp.Y);
					LODOP.ADD_PRINT_TEXT(285,360,25,25,mzfp.J);
					LODOP.ADD_PRINT_TEXT(285,400,25,25,mzfp.F);
					LODOP.ADD_PRINT_TEXT(285,440,100,25,mzfp.HJJE);
					LODOP.ADD_PRINT_TEXT(320,230,200,100,mzfp.BZ);
					LODOP.ADD_PRINT_TEXT(320,440,100,25,mzfp.GRZF);
					LODOP.ADD_PRINT_TEXT(395,10,100,25,mzfp.JGMC);
					LODOP.ADD_PRINT_TEXT(395,120,100,25,mzfp.SFY);
					
					LODOP.ADD_PRINT_TEXT(130,15,65,25,mzfp.SFXM1);
					LODOP.ADD_PRINT_TEXT(130,80,65,25,mzfp.XMJE1);
					LODOP.ADD_PRINT_TEXT(130,145,65,25,mzfp.SFXM5);
					LODOP.ADD_PRINT_TEXT(130,210,65,25,mzfp.XMJE5);
					LODOP.ADD_PRINT_TEXT(130,275,65,25,mzfp.SFXM9);
					LODOP.ADD_PRINT_TEXT(130,340,65,25,mzfp.XMJE9);
					LODOP.ADD_PRINT_TEXT(130,405,65,25,mzfp.SFXM13);
					LODOP.ADD_PRINT_TEXT(130,470,65,25,mzfp.XMJE13);
					LODOP.ADD_PRINT_TEXT(165,15,65,25,mzfp.SFXM2);
					LODOP.ADD_PRINT_TEXT(165,80,65,25,mzfp.XMJE2);
					LODOP.ADD_PRINT_TEXT(165,145,65,25,mzfp.SFXM6);
					LODOP.ADD_PRINT_TEXT(165,210,65,25,mzfp.XMJE6);
					LODOP.ADD_PRINT_TEXT(165,275,65,25,mzfp.SFXM10);
					LODOP.ADD_PRINT_TEXT(165,340,65,25,mzfp.XMJE10);
					LODOP.ADD_PRINT_TEXT(165,405,65,25,mzfp.SFXM14);
					LODOP.ADD_PRINT_TEXT(165,470,65,25,mzfp.XMJE14);
					LODOP.ADD_PRINT_TEXT(200,15,65,25,mzfp.SFXM3);
					LODOP.ADD_PRINT_TEXT(200,80,65,25,mzfp.XMJE3);
					LODOP.ADD_PRINT_TEXT(200,145,65,25,mzfp.SFXM7);
					LODOP.ADD_PRINT_TEXT(200,210,65,25,mzfp.XMJE7);
					LODOP.ADD_PRINT_TEXT(200,275,65,25,mzfp.SFXM11);
					LODOP.ADD_PRINT_TEXT(200,340,65,25,mzfp.XMJE11);
					LODOP.ADD_PRINT_TEXT(200,405,65,25,mzfp.SFXM15);
					LODOP.ADD_PRINT_TEXT(200,470,65,25,mzfp.XMJE15);
					LODOP.ADD_PRINT_TEXT(235,15,65,25,mzfp.SFXM4);
					LODOP.ADD_PRINT_TEXT(235,80,65,25,mzfp.XMJE4);
					LODOP.ADD_PRINT_TEXT(235,145,65,25,mzfp.SFXM8);
					LODOP.ADD_PRINT_TEXT(235,210,65,25,mzfp.XMJE8);
					LODOP.ADD_PRINT_TEXT(235,275,65,25,mzfp.SFXM12);
					LODOP.ADD_PRINT_TEXT(235,340,65,25,mzfp.XMJE12);
					LODOP.ADD_PRINT_TEXT(235,405,65,25,mzfp.SFXM16);
					LODOP.ADD_PRINT_TEXT(235,470,65,25,mzfp.XMJE16);
					LODOP.ADD_PRINT_TEXT(55,570,170,15,mzfp.MXMC1);
					LODOP.ADD_PRINT_TEXT(55,745,50,15,mzfp.MXDJ1);
					LODOP.ADD_PRINT_TEXT(55,800,40,15,mzfp.MXSL1);
					LODOP.ADD_PRINT_TEXT(55,845,60,15,mzfp.MXJE1);
					LODOP.ADD_PRINT_TEXT(70,570,170,15,mzfp.MXMC2);
					LODOP.ADD_PRINT_TEXT(70,745,50,15,mzfp.MXDJ2);
					LODOP.ADD_PRINT_TEXT(70,800,40,15,mzfp.MXSL2);
					LODOP.ADD_PRINT_TEXT(70,845,60,15,mzfp.MXJE2);
					LODOP.ADD_PRINT_TEXT(85,570,170,15,mzfp.MXMC3);
					LODOP.ADD_PRINT_TEXT(85,745,50,15,mzfp.MXDJ3);
					LODOP.ADD_PRINT_TEXT(85,800,40,15,mzfp.MXSL3);
					LODOP.ADD_PRINT_TEXT(85,845,60,15,mzfp.MXJE3);
					LODOP.ADD_PRINT_TEXT(100,570,170,15,mzfp.MXMC4);
					LODOP.ADD_PRINT_TEXT(100,745,50,15,mzfp.MXDJ4);
					LODOP.ADD_PRINT_TEXT(100,800,40,15,mzfp.MXSL4);
					LODOP.ADD_PRINT_TEXT(100,845,60,15,mzfp.MXJE4);
					LODOP.ADD_PRINT_TEXT(115,570,170,15,mzfp.MXMC5);
					LODOP.ADD_PRINT_TEXT(115,745,50,15,mzfp.MXDJ5);
					LODOP.ADD_PRINT_TEXT(115,800,40,15,mzfp.MXSL5);
					LODOP.ADD_PRINT_TEXT(115,845,60,15,mzfp.MXJE5);
					LODOP.ADD_PRINT_TEXT(130,570,170,15,mzfp.MXMC6);
					LODOP.ADD_PRINT_TEXT(130,745,50,15,mzfp.MXDJ6);
					LODOP.ADD_PRINT_TEXT(130,800,40,15,mzfp.MXSL6);
					LODOP.ADD_PRINT_TEXT(130,845,60,15,mzfp.MXJE6);
					LODOP.ADD_PRINT_TEXT(145,570,170,15,mzfp.MXMC7);
					LODOP.ADD_PRINT_TEXT(145,745,50,15,mzfp.MXDJ7);
					LODOP.ADD_PRINT_TEXT(145,800,40,15,mzfp.MXSL7);
					LODOP.ADD_PRINT_TEXT(145,845,60,15,mzfp.MXJE7);
					LODOP.ADD_PRINT_TEXT(160,570,170,15,mzfp.MXMC8);
					LODOP.ADD_PRINT_TEXT(160,745,50,15,mzfp.MXDJ8);
					LODOP.ADD_PRINT_TEXT(160,800,40,15,mzfp.MXSL8);
					LODOP.ADD_PRINT_TEXT(160,845,60,15,mzfp.MXJE8);
					LODOP.ADD_PRINT_TEXT(175,570,170,15,mzfp.MXMC9);
					LODOP.ADD_PRINT_TEXT(175,745,50,15,mzfp.MXDJ9);
					LODOP.ADD_PRINT_TEXT(175,800,40,15,mzfp.MXSL9);
					LODOP.ADD_PRINT_TEXT(175,845,60,15,mzfp.MXJE9);
					LODOP.ADD_PRINT_TEXT(190,570,170,15,mzfp.MXMC10);
					LODOP.ADD_PRINT_TEXT(190,745,50,15,mzfp.MXDJ10);
					LODOP.ADD_PRINT_TEXT(190,800,40,15,mzfp.MXSL10);
					LODOP.ADD_PRINT_TEXT(190,845,60,15,mzfp.MXJE10);
					LODOP.ADD_PRINT_TEXT(205,570,170,15,mzfp.MXMC11);
					LODOP.ADD_PRINT_TEXT(205,745,50,15,mzfp.MXDJ11);
					LODOP.ADD_PRINT_TEXT(205,800,40,15,mzfp.MXSL11);
					LODOP.ADD_PRINT_TEXT(205,845,60,15,mzfp.MXJE11);
					LODOP.ADD_PRINT_TEXT(220,570,170,15,mzfp.MXMC12);
					LODOP.ADD_PRINT_TEXT(220,745,50,15,mzfp.MXDJ12);
					LODOP.ADD_PRINT_TEXT(220,800,40,15,mzfp.MXSL12);
					LODOP.ADD_PRINT_TEXT(220,845,60,15,mzfp.MXJE12);
					LODOP.ADD_PRINT_TEXT(235,570,170,15,mzfp.MXMC13);
					LODOP.ADD_PRINT_TEXT(235,745,50,15,mzfp.MXDJ13);
					LODOP.ADD_PRINT_TEXT(235,800,40,15,mzfp.MXSL13);
					LODOP.ADD_PRINT_TEXT(235,845,60,15,mzfp.MXJE13);
					LODOP.ADD_PRINT_TEXT(250,570,170,15,mzfp.MXMC14);
					LODOP.ADD_PRINT_TEXT(250,745,50,15,mzfp.MXDJ14);
					LODOP.ADD_PRINT_TEXT(250,800,40,15,mzfp.MXSL14);
					LODOP.ADD_PRINT_TEXT(250,845,60,15,mzfp.MXJE14);
					LODOP.ADD_PRINT_TEXT(265,570,170,15,mzfp.MXMC15);
					LODOP.ADD_PRINT_TEXT(265,745,50,15,mzfp.MXDJ15);
					LODOP.ADD_PRINT_TEXT(265,800,40,15,mzfp.MXSL15);
					LODOP.ADD_PRINT_TEXT(265,845,60,15,mzfp.MXJE15);
					LODOP.ADD_PRINT_TEXT(280,570,170,15,mzfp.MXMC16);
					LODOP.ADD_PRINT_TEXT(280,745,50,15,mzfp.MXDJ16);
					LODOP.ADD_PRINT_TEXT(280,800,40,15,mzfp.MXSL16);
					LODOP.ADD_PRINT_TEXT(280,845,60,15,mzfp.MXJE16);
					LODOP.ADD_PRINT_TEXT(295,570,170,15,mzfp.MXMC17);
					LODOP.ADD_PRINT_TEXT(295,745,50,15,mzfp.MXDJ17);
					LODOP.ADD_PRINT_TEXT(295,800,40,15,mzfp.MXSL17);
					LODOP.ADD_PRINT_TEXT(295,845,60,15,mzfp.MXJE17);
					LODOP.ADD_PRINT_TEXT(310,570,170,15,mzfp.MXMC18);
					LODOP.ADD_PRINT_TEXT(310,745,50,15,mzfp.MXDJ18);
					LODOP.ADD_PRINT_TEXT(310,800,40,15,mzfp.MXSL18);
					LODOP.ADD_PRINT_TEXT(310,845,60,15,mzfp.MXJE18);
					LODOP.ADD_PRINT_TEXT(325,570,170,15,mzfp.MXMC19);
					LODOP.ADD_PRINT_TEXT(325,745,50,15,mzfp.MXDJ19);
					LODOP.ADD_PRINT_TEXT(325,800,40,15,mzfp.MXSL19);
					LODOP.ADD_PRINT_TEXT(325,845,60,15,mzfp.MXJE19);
					LODOP.ADD_PRINT_TEXT(340,570,170,15,mzfp.MXMC20);
					LODOP.ADD_PRINT_TEXT(340,745,50,15,mzfp.MXDJ20);
					LODOP.ADD_PRINT_TEXT(340,800,40,15,mzfp.MXSL20);
					LODOP.ADD_PRINT_TEXT(340,845,60,15,mzfp.MXJE20);
					LODOP.ADD_PRINT_TEXT(355,570,170,15,mzfp.MXMC21);
					LODOP.ADD_PRINT_TEXT(355,745,50,15,mzfp.MXDJ21);
					LODOP.ADD_PRINT_TEXT(355,800,40,15,mzfp.MXSL21);
					LODOP.ADD_PRINT_TEXT(355,845,60,15,mzfp.MXJE21);
					LODOP.ADD_PRINT_TEXT(370,570,170,15,mzfp.MXMC22);
					LODOP.ADD_PRINT_TEXT(370,745,50,15,mzfp.MXDJ22);
					LODOP.ADD_PRINT_TEXT(370,800,40,15,mzfp.MXSL22);
					LODOP.ADD_PRINT_TEXT(370,845,60,15,mzfp.MXJE22);
					LODOP.ADD_PRINT_TEXT(385,570,170,15,mzfp.MXMC23);
					LODOP.ADD_PRINT_TEXT(385,745,50,15,mzfp.MXDJ23);
					LODOP.ADD_PRINT_TEXT(385,800,40,15,mzfp.MXSL23);
					LODOP.ADD_PRINT_TEXT(385,845,60,15,mzfp.MXJE23);
					LODOP.ADD_PRINT_TEXT(400,570,170,15,mzfp.MXMC24);
					LODOP.ADD_PRINT_TEXT(400,745,50,15,mzfp.MXDJ24);
					LODOP.ADD_PRINT_TEXT(400,800,40,15,mzfp.MXSL24);
					LODOP.ADD_PRINT_TEXT(400,845,60,15,mzfp.MXJE24);
					LODOP.ADD_PRINT_TEXT(415,570,170,15,mzfp.MXMC25);
					LODOP.ADD_PRINT_TEXT(415,745,50,15,mzfp.MXDJ25);
					LODOP.ADD_PRINT_TEXT(415,800,40,15,mzfp.MXSL25);
					LODOP.ADD_PRINT_TEXT(415,845,60,15,mzfp.MXJE25);
					LODOP.ADD_PRINT_TEXT(430,525,60,20,mzfp.PAGE);
					if (LODOP.SET_PRINTER_INDEXA(ret.json.MZHJSFDYJMC)){
						if((ret.json.FPYL+"")=='1'){
							LODOP.PREVIEW();
						}else{
							LODOP.PRINT();
						}
					}else{
						LODOP.PREVIEW();
					}
				}
			}
		});