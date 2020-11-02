/**
 * 家床发药
 * 
 * @author caijy
 */
$package("phis.application.fsb.script");

$import("phis.script.SimpleModule");

phis.application.fsb.script.FamilySickBedDispensing = function(cfg) {
	this.exContext = {};
	this.width = 1024;
	this.height = 550;
	phis.application.fsb.script.FamilySickBedDispensing.superclass.constructor
			.apply(this, [cfg]);
	this.yzlx = 0;
	this.yzlxls = 1;
	this.yzlxcq = 1;
}
Ext.extend(phis.application.fsb.script.FamilySickBedDispensing,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.initializationServiceID,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				Ext.apply(this,this.loadSystemParams({"privates":['JCKCGL']}));
				if(this.JCKCGL!=3){
				Ext.Msg.alert("提示", "当前设置药品不在药房发药!");
				return null;}
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : false,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										title : '家床发药',
										region : 'west',
										width : 300,
										items : this.getLists()
									}, {
										layout : "fit",
										border : false,
										title : '',
										region : 'center',
										items : this.getRModule()
									}],
							tbar : (this.getTbar() || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			getLists : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("recordClick", this.onRecordClick, this);
				this.list.on("clear", this.onClear, this);
				this.list.opener = this;
				return this.list.initPanel();
			},
			getRModule : function() {
				this.module = this.createModule("module", this.refModule);
				this.module.on("checkTab", this.onTabCheck, this);
				return this.module.initPanel();
			},
			// 左下单击 右边数据查询(提交单)
			onRecordClick : function(r) {
				this.module.yzlx = this.yzlx;
				this.module.showRecord(r);
			},
			// 发药
			doFy : function() {
				Ext.Msg.show({
					title : "提示",
					msg : "确定要进行发药吗?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var bq = this.list.getData();// 病区提交01记录
							if (bq.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要发的药", true);
								return;
							}
							var fymx = this.module.getData("fy");// 病区提交02记录
							if (fymx.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要发的药", true);
								return;
							}
							var body = {};
							body["bq"] = bq;
							body["fymx"] = fymx;
							this.panel.el.mask("正在保存数据...", "x-mask-loading")
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.saveActionID,
										body : body
									});
							this.panel.el.unmask();
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doAction);
								return;
							}
							if (ret.msg != null && ret.msg.length > 0
									&& ret.msg != 'Success') {
								MyMessageTip.msg("提示", ret.msg + " 库存不够", true);
							}
							// var FYSJ = ret.json.otherRet.FYSJ;
							// var FYBQ = ret.json.otherRet.FYBQ;
							var JLID = ret.json.otherRet.JLID;
							this.doIsPrint(JLID);
							this.doSx();
						}
					},
					scope : this
				});

			},
			doIsPrint : function(JLID) { // 打印费用明细
				Ext.Msg.show({
					title : "提示",
					msg : "是否打印发药明细信息?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var url = "resources/phis.prints.jrxml.FamilySickBedDispensingDetails.print?type=1&JLID="
									+ JLID;
							var LODOP = getLodop();
							LODOP.PRINT_INIT("打印控件");
							LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
							var rehtm = util.rmi.loadXML({
										url : url,
										httpMethod : "get"
									})
							rehtm = rehtm.replace(/table style=\"/g,
									"table style=\"page-break-after:always;")
							rehtm.lastIndexOf("page-break-after:always;");
							rehtm = rehtm
									.substr(
											0,
											rehtm
													.lastIndexOf("page-break-after:always;"))
									+ rehtm
											.substr(rehtm
													.lastIndexOf("page-break-after:always;")
													+ 24);
							LODOP
									.ADD_PRINT_HTM("0", "0", "100%", "100%",
											rehtm);
							LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT",
									"Full-Width");
							LODOP.PREVIEW();
						} else {
							return true;
						}
					},
					scope : this
				});
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.panel.getTopToolbar();
				} else {
					btns = this.panel.buttons;
				}

				if (!btns) {
					return;
				}

				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			// 选择汇总发药, 发药按钮变灰
			onTabCheck : function(tag) {
				this.setButtonsState(["fy"], false);
				this.setButtonsState(["qt"], false);
				this.setButtonsState(["thzx"], false);
				if (tag == 1) {
					if (this.module.getData("fy").length > 0) {
						this.setButtonsState(["fy"], true);
					}
					this.setButtonsState(["qt"], false);
					this.setButtonsState(["thzx"], false);
				} else {
					this.setButtonsState(["fy"], false);
					if (this.module.getData("th").length > 0) {
						this.setButtonsState(["qt"], true);
						this.setButtonsState(["thzx"], true);
					}
				}

			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref

				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
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
			// 刷新
			doSx : function() {
				// this.yzlx = this.getRadioValue();
				this.list.yzlx = this.yzlx;
				this.list.doNew();
				this.module.doNew();
				this.module.tab.activate(0);
				this.setButtonsState(["fy"], false);
				this.setButtonsState(["qt"], false);
				this.setButtonsState(["thzx"], false);
			},
			// 全退
			doQt : function() {
				Ext.Msg.show({
					title : "提示",
					msg : "确定要将所选医嘱全都退回病区吗?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var bq = this.list.getData();// 病区提交01记录
							if (bq.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要退回的记录", true);
								return;
							}
							var body = {};
							body["bq"] = bq;
							this.panel.el.mask("正在保存数据...", "x-mask-loading")
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.saveFullRefundActionID,
										body : body
									});
							this.panel.el.unmask();
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doAction);
								return;
							}
							this.doSx();
						}
					},
					scope : this
				});
			},
			// 退回病区
			doThzx : function() {
				Ext.Msg.show({
					title : "提示",
					msg : "确定要将所选医嘱退回中心吗?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var bq = this.list.getData();// 病区提交01记录
							if (bq.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要退回的药", true);
								return;
							}
							var fymx = this.module.getData("th");// 病区提交02记录
							if (fymx.length == 0) {
								MyMessageTip.msg("提示", "请先选中需要退回的药", true);
								return;
							}
							var body = {};
							body["bq"] = bq;
							body["fymx"] = fymx;
							this.panel.el.mask("正在保存数据...", "x-mask-loading")
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.saveRefundActionID,
										body : body
									});
							this.panel.el.unmask();
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doAction);
								return;
							}
							this.doSx();
						}
					},
					scope : this
				});
			},
			onReady : function() {
				this.setButtonsState(["fy"], false);
				this.setButtonsState(["qt"], false);
				this.setButtonsState(["thzx"], false);
			},
			onClear : function() {
				this.module.doNew();
				this.module.tab.activate(0);
			},
			getTbar : function() {
				this.radio1 = new Ext.form.Radio({
							xtype : "radio",
							checked : true,
							boxLabel : '全部',
							inputValue : 0,
							name : "yzlxfy",
							id : "qb",
							clearCls : true
						});
				this.radio1.on('check', this.onChange, this);
				this.radio2 = new Ext.form.Radio({
							xtype : "radio",
							id : "ls",
							boxLabel : '临时',
							inputValue : 1,
							name : "yzlxfy",
							clearCls : true
						});
				this.radio2.on('check', this.onChange, this);
				this.radio3= new Ext.form.Radio({
							xtype : "radio",
							id : "cq",
							boxLabel : '长期',
							inputValue : 2,
							name : "yzlxfy",
							clearCls : true
						});
				this.radio3.on('check', this.onChange, this);
				return [this.radio1,this.radio2,this.radio3]
			},
			onChange:function(){
				if(this.radio1.getValue()==true){
				this.yzlx=0;
				}else if(this.radio2.getValue()==true){
				this.yzlx=1;
				}else if(this.radio3.getValue()==true){
				this.yzlx=2;
				}
				this.doSx();
			}
		});