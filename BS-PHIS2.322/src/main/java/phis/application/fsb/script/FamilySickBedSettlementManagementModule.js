$package("phis.application.fsb.script")
$import("phis.script.SimpleModule")

phis.application.fsb.script.FamilySickBedSettlementManagementModule = function(cfg) {
	Ext.apply(this, app.modules.common)
	// Ext.apply(this, phis.application.cfg.script.yb.YbUtil);
	phis.application.fsb.script.FamilySickBedSettlementManagementModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedSettlementManagementModule,
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
				var ybbrxz=this.getYbbrxz();
				if(ybbrxz==null){
				return;}
				if(brxz==ybbrxz.YBBRXZ){
				if(!this.ybkxx||this.ybkxx==null){
				MyMessageTip.msg("提示", "医保病人,未读卡,请先读卡", true);
				return;
				}
				this.doYjs();
				} else {
					// var ret_xzcs = phis.script.rmi.miniJsonRequestSync({
					// serviceId : "medicareService",
					// serviceAction : "queryYbBrxz"
					// });
					// if (ret_xzcs.code > 300) {
					// this.processReturnMsg(ret_xzcs.code, ret_xzcs.msg);
					// return;
					// } else {
					// if (brxz == ret_xzcs.json.body.SHENGYB) {// 如果是医保病人
					// var module = this.createModule("sjybxx_zy",
					// this.sjybxxForm);
					// this.zyzf = false;
					// module.on("szqr", this.onSzQr, this);
					// var win = module.getWin();
					// win.add(module.initPanel());
					// module.doNew();
					// module.mzorzy = "ZY";
					// win.show();
					// return;
					// } else {
					this.showZyjsModule();
					// }
					// }
					// module.onWinShow();
				}
			},
			showZyjsModule : function() {
				var module = this.createModule("zyjsModule", this.zyjsModule);
				module.JSLX = this.JSLX;
				module.on("settlement", this.settlement, this);
				// alert(Ext.encode(this.body))
				module.data = this.body;
				
				module.opener = this;
				var win = module.getWin();
				win.add(module.initPanel());
				win.show();
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
				// this.body.YWLSH = this.data.YWLSH
				Ext.MessageBox.confirm('提示', '确定发票作废吗?', function(btn) {
					if (btn == "yes") {
						this.invalid = true;
						this.panel.el.mask("正在作废...", "x-mask-loading")
						// 以下是医保
						var ybbrxz=this.getYbbrxz();
						if(ybbrxz==null){
						return;
						}
						var form = this.form.form.getForm();
						var brxz = form.findField("BRXZ").getValue();
						if (brxz == ybbrxz.YBBRXZ) {
							alert("医保功能完善中....");
							return;
							var ybModule = this.createModule("ybjszfModule",
									"phis.application.yb.YB/YB/YB01");
							ybModule.initPanel();
							var win = ybModule.getWin();
							win.show();
							ybModule.on("qr", this.onZfQr, this);
							ybModule.doNew();
							return;
						}
						phis.script.rmi.jsonRequest({
									serviceId : "familySickBedPatientSelectionService",
									serviceAction : "updateSettleAccounts",
									body : this.body
								}, function(code, msg, json) {
									this.panel.el.unmask()
									this.invalid = false
									if (code > 300) {
										Ext.Msg.alert("提示", msg, function() {
													this.doNew()
												}, this);
										return
									}
									MyMessageTip.msg("提示", "发票作废成功!", true);
									this.settlement(this);
								}, this)
					}
				}, this);

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
						btn1[0].setDisabled(sta);
					}
				}
			},
			// 下面医保相关代码
			// 打开杭州市医保界面
			doYbdk : function() {
				alert("医保功能完善中....");
				return;
				if(this.getYbbrxz()==null){//读卡前判断是否设置医保性质代码
				return;
				}
				var ybModule = this.createModule("ybzyjsModule",
						"phis.application.yb.YB/YB/YB01");
				ybModule.initPanel();
				var win = ybModule.getWin();
				win.show();
				ybModule.on("qr", this.onQr, this);
				ybModule.doNew();
				this.ybModule = ybModule;
			},
			onQr : function(data) {
				this.ybkxx = data;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryZyhmByYbkxx",
							body : this.ybkxx
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					this.doNew();
					return;
				}
				var zyhm = this.form.form.getForm().findField("ZYHM");
				zyhm.setValue(ret.json.ZYHM);
				var data = {};
				data.key = zyhm.getName();
				data.value = zyhm.getValue();
				this.doQuery(data);
			},
			// 预结算
			doYjs : function() {
				var body = {};
				body["ZYH"] = this.data.ZYH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicareService",
							serviceAction : "queryYbZyjscs",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				//医保上传,预结算代码
				//...
				//注:如果费用是一条一条上传的,上传时缓存成功上传的费用主键并后台更新,如果有上传失败的 则不能结算.ZY_FYMX增加上传标志 字段自行增加方法自行增加
				this.body.TCJE="";//统筹金额 医保报销部分  总金额-医保本年历年账户支付-个人现金支付
				this.body.ZHZF ="";//账户支付, 本年账户支付加上历年账户支付
				this.body.ZFHJ = "";//自负金额,本年+历年+现金
				this.body.YBKXX=this.ybkxx;
				this.body.YBJSCS=ret.json.body.ybjscs;
				//其他需要的自行添加
				this.showZyjsModule();
			},
			// 发票作废
			onZfQr : function(data) {
				var inInputStrAcc = data.SRKH + "|000000|" + data.SMKLSH + "|"
						+ data.SMKRZM + "|";
				this.body["YBXX"] = data;
				var ret = this.simpleDyyb("zyqxjs", this.body, "", 2,
						inInputStrAcc);
				if (ret.code == 1) {// 成功
					var ybxx = {};
					ybxx["ZFYWZQH"] = ret.CS.BusiCycle;
					ybxx["ZFLSH"] = ret.ReceiverSerialNo;
					ybxx["MZXH"] = ret.CS.MZXH;
					ybxx["ZYH"] = this.data.ZYH;
					this.body["YBXX"] = ybxx;
					phis.script.rmi.jsonRequest({
								serviceId : "familySickBedPatientSelectionService",
								serviceAction : "updateSettleAccounts",
								body : this.body
							}, function(code, msg, json) {
								this.panel.el.unmask()
								this.invalid = false
								if (code > 300) {
									Ext.Msg.alert("提示", "医保作废成功,本地作废失败:" + msg,
											function() {
												this.doNew()
											}, this);
									return
								}
								this.settlement(this);
							}, this);
				} else {// 失败
					MyMessageTip.msg("提示", ret.outputStr, true);
					return;
				}
			},
			// 获取医保病人性质代码
			getYbbrxz : function() {
				if (this.ybbrxz && this.ybbrxz != null) {
					return this.ybbrxz;
				} else {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicareService",
								serviceAction : "queryYbbrxz"
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return null;
					}
					this.ybbrxz = ret.json.body;
					return this.ybbrxz;
				}
			},
			// 清除医保信息缓存,根据需要添加其他需要删除的医保缓存
			clearYbxx:function(){
			this.ybkxx=null;
			},
			//作废确认
			onZfqr:function(data){
				//此处判断读卡信息和当前病人是否是同一个人
				//..
				var csbody={};
				//存放参数
				//..
				var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicareService",
								serviceAction : "queryYbzyzfcs",
								body:csbody
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return ;
					}
				var zfcs=ret.json.body;
				//调用医保取消结算
				//...
				var body={};
				Ext.apply(body,this.body);
				body["isYb"]=1;
				phis.script.rmi.jsonRequest({
									serviceId : "familySickBedPatientSelectionService",
									serviceAction : "updateSettleAccounts",
									body : body
								}, function(code, msg, json) {
									this.panel.el.unmask()
									this.invalid = false
									if (code > 300) {
										Ext.Msg.alert("提示", msg, function() {
													this.doNew()
												}, this);
										return
									}
									MyMessageTip.msg("提示", "发票作废成功!", true);
									this.settlement(this);
								}, this);
								
			},
			// 医保部分结束
			doPrintSet : function(){
				var LODOP=getLodop();  

				LODOP.PRINT_INITA(3,8,643,680,"家床发票套打");
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.ADD_PRINT_TEXT(29,70,160,25,"发票号码");
				LODOP.ADD_PRINT_TEXT(55,70,100,25,"家床号");
				LODOP.ADD_PRINT_TEXT(55,410,40,25,"年");
				LODOP.ADD_PRINT_TEXT(55,470,40,25,"月");
				LODOP.ADD_PRINT_TEXT(55,530,40,25,"日");
				LODOP.ADD_PRINT_TEXT(80,70,160,25,"入院类别");
				LODOP.ADD_PRINT_TEXT(80,410,160,25,"工作单位");
				LODOP.ADD_PRINT_TEXT(105,70,160,25,"病人姓名");
				LODOP.ADD_PRINT_TEXT(105,410,160,25,"开始日期");
				LODOP.ADD_PRINT_TEXT(140,70,100,25,"收费项目1");
				LODOP.ADD_PRINT_TEXT(165,70,100,25,"收费项目2");
				LODOP.ADD_PRINT_TEXT(190,70,100,25,"收费项目3");
				LODOP.ADD_PRINT_TEXT(215,70,100,25,"收费项目4");
				LODOP.ADD_PRINT_TEXT(240,70,100,25,"收费项目5");
				LODOP.ADD_PRINT_TEXT(265,70,100,25,"收费项目6");
				LODOP.ADD_PRINT_TEXT(290,70,100,25,"收费项目7");
				LODOP.ADD_PRINT_TEXT(315,70,100,25,"收费项目8");
				LODOP.ADD_PRINT_TEXT(340,70,100,25,"收费项目9");
				LODOP.ADD_PRINT_TEXT(365,70,100,25,"收费项目10");
				LODOP.ADD_PRINT_TEXT(390,70,100,25,"收费项目11");
				LODOP.ADD_PRINT_TEXT(415,70,100,25,"收费项目12");
				LODOP.ADD_PRINT_TEXT(440,70,100,25,"收费项目13");
				LODOP.ADD_PRINT_TEXT(465,70,100,25,"收费项目14");
				LODOP.ADD_PRINT_TEXT(490,70,100,25,"收费项目15");
				LODOP.ADD_PRINT_TEXT(515,70,100,25,"收费项目16");
				LODOP.ADD_PRINT_TEXT(140,175,100,25,"项目金额1");
				LODOP.ADD_PRINT_TEXT(165,175,100,25,"项目金额2");
				LODOP.ADD_PRINT_TEXT(190,175,100,25,"项目金额3");
				LODOP.ADD_PRINT_TEXT(215,175,100,25,"项目金额4");
				LODOP.ADD_PRINT_TEXT(240,175,100,25,"项目金额5");
				LODOP.ADD_PRINT_TEXT(265,175,100,25,"项目金额6");
				LODOP.ADD_PRINT_TEXT(290,175,100,25,"项目金额7");
				LODOP.ADD_PRINT_TEXT(315,175,100,25,"项目金额8");
				LODOP.ADD_PRINT_TEXT(340,175,100,25,"项目金额9");
				LODOP.ADD_PRINT_TEXT(365,175,100,25,"项目金额10");
				LODOP.ADD_PRINT_TEXT(390,175,100,25,"项目金额11");
				LODOP.ADD_PRINT_TEXT(415,175,100,25,"项目金额12");
				LODOP.ADD_PRINT_TEXT(440,175,100,25,"项目金额13");
				LODOP.ADD_PRINT_TEXT(465,175,100,25,"项目金额14");
				LODOP.ADD_PRINT_TEXT(490,175,100,25,"项目金额15");
				LODOP.ADD_PRINT_TEXT(515,175,100,25,"项目金额16");
				LODOP.ADD_PRINT_TEXT(515,280,100,25,"费用合计");
				LODOP.ADD_PRINT_TEXT(540,114,317,25,"自费金额");
				LODOP.ADD_PRINT_TEXT(565,70,500,65,"备注");
				LODOP.ADD_PRINT_TEXT(635,450,120,25,"收费员");
				LODOP.ADD_PRINT_TEXT(140,470,100,25,"自费金额");
				LODOP.ADD_PRINT_TEXT(165,470,100,25,"本年账户支付");
				LODOP.ADD_PRINT_TEXT(190,470,100,25,"历年账户支付");
				LODOP.ADD_PRINT_TEXT(215,470,100,25,"医保总和");
				LODOP.ADD_PRINT_TEXT(240,470,100,25,"医保合计");
				LODOP.ADD_PRINT_TEXT(265,470,100,25,"缴款合计");
				LODOP.ADD_PRINT_TEXT(290,470,100,25,"合计金额");
				LODOP.ADD_PRINT_TEXT(315,470,100,25,"结算补缴");
				LODOP.ADD_PRINT_TEXT(340,470,100,25,"补缴现金");
				LODOP.ADD_PRINT_TEXT(365,470,100,25,"结算退款");
				LODOP.ADD_PRINT_TEXT(390,470,100,25,"退款现金");
				LODOP.PRINT_SETUP();
			}
		});
