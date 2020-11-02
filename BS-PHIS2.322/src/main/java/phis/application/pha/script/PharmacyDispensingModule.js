/**
 * 药房发药
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyDispensingModule = function(cfg) {
	this.exContext = {};
	phis.application.pha.script.PharmacyDispensingModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.pha.script.PharmacyDispensingModule,
		phis.script.SimpleModule, {
			F1 : function() {
						this.doDispensing();
			},
			initPanel : function() {
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
					return null;
				}
				if (this.panel) {
					return this.panel;
				}
				this.QYFYCK = ret.json.QYFYCK;
				if (this.QYFYCK == '1') {
					// 获取窗口设置相关信息
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.loadPharmacyWindowInfoActionID
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return;
					}
					this.FYCK = ret.json.body;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 250,
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getDModule()
									}],
							tbar : this.getTbar(4),
							temp : this,
							// add by chzhxiang 关闭tab Panel时,关闭列表刷新任务
							listeners : {
								'beforedestroy' : function() {
									this.temp.cfList.fireEvent("stopRefresh");
								}
							},
							autoDestroy : true,
							closeAction : 'destroy'
						});
				this.panel = panel;
				this.panel.on("beforeclose", this.beforeClose, this);
				return panel;
			},
			beforeClose : function() {
				if (this.QYFYCK == '1') {
					this.changePharmacyStatus(false);
				}
			},
			doNew : function() {
			},
			getList : function() {
				this.cfList = this.createModule("cfList", this.refList);
				this.cfList.initCnd = [
						'and',
						['and', ['eq', ['$', 'a.FYBZ'], ['i', 0]],
								['notNull', ['$', 'a.FPHM']]],
						['eq', ['$', 'a.ZFPB'], ['i', 0]]];
				this.cfList.requestData.cnd = [
						'and',
						['and', ['eq', ['$', 'a.FYBZ'], ['i', 0]],
								['notNull', ['$', 'a.FPHM']]],
						['eq', ['$', 'a.ZFPB'], ['i', 0]]];
				if (this.QYFYCK == '1') {
					this.cfList.initCnd = [
							'and',
							[
									'and',
									['and', ['eq', ['$', 'a.FYBZ'], ['i', 0]],
											['notNull', ['$', 'a.FPHM']]],
									['eq', ['$', 'a.ZFPB'], ['i', 0]]],
							['eq', ['$', 'a.FYCK'], ['i', this.FYCK.CKBH]]];
					this.cfList.requestData.cnd = [
							'and',
							[
									'and',
									['and', ['eq', ['$', 'a.FYBZ'], ['i', 0]],
											['notNull', ['$', 'a.FPHM']]],
									['eq', ['$', 'a.ZFPB'], ['i', 0]]],
							[
									'or',
									['eq', ['$', 'a.FYCK'],
											['i', this.FYCK.CKBH]],
									['eq', ['$', 'a.FYCK'], ['i', -1]]]];
				}
				this.cfList.on("cfSelect", this.onCfSelect, this)
				this.cfList.on("cfCancleSelect", this.onCfCancleSelect, this)
				this.cfList.on("noRecord", this.onNoRecord, this);
				//this.cfList.on("cfDbClick", this.doDispensing, this);// 双击直接发药
				return this.cfList.initPanel();
			},

			getDModule : function() {
				this.cfDetaiModule = this.createModule("cfDetaiModule",
						this.refModule);
					this.cfDetaiModule.on("afterFormload",this.onAfterFormload,this)
				return this.cfDetaiModule.initPanel();
			},
			onAfterFormload:function(){
			if(this.cfList.isDbClick){
			this.cfList.isDbClick=false;
			this.doDispensing();
			}
			},
			onCfSelect : function(record) {
				var cfsb = record.data.CFSB;
				var cflx = record.data.CFLX;
				if (!this.cfDetaiModule) {
					return;
				}
				this.cfDetaiModule.showDetail(cfsb, cflx);
			},
			onCfCancleSelect : function() {
				var lastIndex = this.midiModules['cfList'].grid
						.getSelectionModel().lastActive;
				this.midiModules['cfList'].selectRow(lastIndex);
			},
			doDispensing : function() {
				var r = this.cfList.getSelectedRecord();
				if (!r) {
					return;
				}
				if (r.data.SJFYBZ == 1
						&& r.data.SJYF != this.mainApp['phis'].pharmacyId) {
					MyMessageTip.msg("提示", "上级发药,本药房不能发", true);
					return;
				}

				var body = {};
				body["cfsb"] = r.data.CFSB;
				if (this.QYFYCK == '1') {
					body["ckbh"] = this.FYCK.CKBH
					if (this.FYCK.QYPB == 0) {
						MyMessageTip.msg("提示", "窗口未启用,不能发药", true);
						return;
					}
				}
				if (this.cfDetaiModule.cfForm.loading) {// modify by yangl
														// 去掉发药的一秒延迟，增加数据加载中判断
					MyMessageTip.msg("提示", "数据尚未加载完成,等稍后重试!", true);
					return;
				}
				this.cfList.fireEvent("stopRefresh");
				var form = this.cfDetaiModule.cfForm.form.getForm();
				body["fygh"] = form.findField("FYGH").getValue();
				// body["fyck"] = form.findField("CKMC").getValue();
				var showWin = Ext.Msg.show({
					title : "提示",
					msg : "确定发药?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.panel.el.mask("正在发药,请耐心等待...",
									"x-mask-loading");
							var _ctr = this;
							// var whatsthetime = function() {
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : _ctr.serviceId,
										serviceAction : _ctr.dispensingServiceActionID,
										body : body
									});
							if (ret.code > 300) {
								_ctr.panel.el.unmask();
								_ctr.processReturnMsg(ret.code, ret.msg,
										_ctr.onBeforeSave);
								// _ctr.cfList.refresh();
								_ctr.cfList.fireEvent("startRefresh");
								return;
							}
							_ctr.panel.el.unmask();
							// _ctr.cfList.refresh();
							_ctr.cfList.fireEvent("startRefresh");
							MyMessageTip.msg("提示", "发药成功", true);

							// }
							// whatsthetime.defer(1000);
						} else {
							var _ctr = this;
							var dytime = function() {
							}
							dytime.defer(1000);
							_ctr.cfList.fireEvent("startRefresh");
						}
					},
					scope : this
				});
				showWin.focus();
				// alert(Ext.encode(btns))

			},
			getTbar : function(num) {
				var buttons = this.createButton(num);
				var tbar = new Ext.Toolbar();
				// tbar.add(buttons, "->", panel);
				tbar.add(buttons)
				if (this.QYFYCK == '1') {

					var body = this.FYCK;
					var stateRadio = new Ext.form.RadioGroup({
						// fieldLabel : "窗口状态",
						hideLabel : true,
						width : 80,
						items : [{
									checked : (body.QYPB == 1),
									boxLabel : "开",
									name : "status",
									inputValue : "1",
									clearCls : true,
									listeners : {
										check : function(checkbox, checked) {
											if (checked) {
												this.changePharmacyStatus(true);
											}
										},
										scope : this

									}
								}, {
									checked : !(body.QYPB == 1),
									boxLabel : "关",
									name : "status",
									inputValue : "0",
									clearCls : true,
									listeners : {
										check : function(checkbox, checked) { // 选中时,调用的事件
											if (checked) {
												this
														.changePharmacyStatus(false);
											}
										},
										scope : this
									}
								}]
					});

					var panel = new Ext.FormPanel({
								labelWidth : 50, // label settings here
								// cascade
								title : '',
								height : 20,
								layout : "table",
								defaults : {},
								defaultType : 'textfield',
								items : [{
											xtype : "label",
											forId : "window",
											text : "窗口 "
										}, {
											name : "window",
											xtype : "textfield",
											value : body.CKMC,
											inputId : "window",
											disabled : true,
											width : 70,
											hideLabel : true
										},

										{
											xtype : "displayfield",
											value : '窗口状态',
											style : "padding-left:10px;"
										}, stateRadio]

							})
					tbar.add("->", panel);
				}
				return tbar;

			},
			changePharmacyStatus : function(status) {
				// 设置药房对应窗口启用/关闭
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.savePharmacyWindowStatusActionID,
							body : {
								QYPB : (status ? 1 : 0),
								CKBH : this.FYCK.CKBH
							}
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				this.FYCK.QYPB = status ? 1 : 0;
			},
			doInjectionCardPrint : function() {
				var r = this.cfList.getSelectedRecord();
				if (!r)
					return;
				if (r.data.CFLX == 1) {
					var module = this.createModule("injectionCardprint",
							this.refInjectionCardPrint)
					if (r.data.CFSB) {
						module.cfsb = r.data.CFSB;
						module.initPanel();
						module.doPrint();
					} else {
						MyMessageTip.msg("提示", "打印失败：无效的注射卡信息!", true);
					}
				}
			},
			// 参数num表示前几个显示在左边(从0开始)
			createButton : function(num) {
				if (this.op == 'read') {
					return []
				}
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				var f1 = 112
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {}
					btn.accessKey = f1 + i, btn.cmd = action.id
					btn.text = action.name + "(F" + (i + 1) + ")", btn.iconCls = action.iconCls
							|| action.id
					btn.script = action.script
					btn.handler = this.doAction;
					btn.notReadOnly = action.notReadOnly
					btn.scope = this;
					if (i <= num) {
						buttons.push(btn, '-')
					} else {
						// ->将按钮显示到右边去
						buttons.push(btn, '->')
					}
				}
				return buttons
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
			// 当没发药记录时调用,清空明细
			onNoRecord : function() {
				this.cfDetaiModule.doNew();
			},
			doPrint : function() {
				var r = this.cfList.getSelectedRecord();
				if (r == null) {
					MyMessageTip.msg("提示", "请先选择要打印的处方!", true);
				}
				if (r.data.CFLX == 3) {
					var module = this.createModule("cfzyprint",
							this.refPrescriptionChinePrint)
					module.cfsb = r.data.CFSB;
					module.initPanel();
					module.doPrint();
				} else {
					var module = this.createModule("cfprint",
							this.refPrescriptionPrint)
					module.cfsb = r.data.CFSB;
					module.initPanel();
					module.doPrint();
				}
			}
		});