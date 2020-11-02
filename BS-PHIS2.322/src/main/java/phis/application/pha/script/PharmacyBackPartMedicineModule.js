/**
 * 药房发药
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory");

phis.application.pha.script.PharmacyBackPartMedicineModule = function(cfg) {
	this.exContext = {};
	phis.application.pha.script.PharmacyBackPartMedicineModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.pha.script.PharmacyBackPartMedicineModule,
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
										title : '',
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
							tbar : this.getTbar(4)
						});
				this.panel = panel;
				return panel;
			},
			doNew : function() {
			},
			// 左边已发药记录查询结果列表
			getList : function() {
				this.cfList = this.createModule("cfList", this.refList);
				this.cfList.on("cfSelect", this.onCfSelect, this)
				this.cfList.on("cfCancleSelect", this.onCfCancleSelect, this)
				this.cfList.on("query", this.onQuery, this)
				this.cfList.on("noRecord", this.onNoRecord, this);
				return this.cfList.initPanel();
			},
			// 右边已发药记录详情
			getDModule : function() {
				this.cfDetaiModule = this.createModule("cfDetaiModule",
						this.refModule);
				return this.cfDetaiModule.initPanel();
			}
			// 选中
			,
			onCfSelect : function(record) {
				var cfsb = record.data.CFSB;
				var cflx = record.data.CFLX;
				if (!this.cfDetaiModule) {
					return;
				}
				this.cfDetaiModule.showDetail(cfsb, cflx);
			},
			// 取消选中
			onCfCancleSelect : function() {
				var lastIndex = this.midiModules['cfList'].grid
						.getSelectionModel().lastActive;
				this.midiModules['cfList'].selectRow(lastIndex);
			},
			getTbar : function(num) {
				var buttons = this.createButton(num);
				this.topPanel = new Ext.FormPanel({
							labelWidth : 50, // label settings here cascade
							title : '',
							height : 20,
							layout : "table",
							defaults : {},
							defaultType : 'textfield',
							items : [{
										xtype : "label",
										forId : "window",
										text : "退药原因: "
									},this.getSelectBox(), {
										xtype : "label",
										forId : "window",
										text : "发药日期: "
									}, new Ext.ux.form.Spinner({
												fieldLabel : '',
												name : 'dateFrom',
												value : new Date()
														.format('Y-m-d'),
												strategy : {
													xtype : "date"
												}
											}), {
										xtype : "label",
										forId : "window",
										text : "-"
									}, new Ext.ux.form.Spinner({
												fieldLabel : '',
												name : 'dateTo',
												value : new Date()
														.format('Y-m-d'),
												strategy : {
													xtype : "date"
												}
											})]
						})
				var tbar = new Ext.Toolbar();
				tbar.add(this.topPanel, '-', buttons);
				return tbar;
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
			// 账户
			doAccount : function() {
				Ext.Msg.alert("提示", "功能暂未开发!");
			},
			// 确认
			doConfirm : function() {
				var r = this.cfList.getSelectedRecord();
				if (r == null) {
					return;
				}
				var body = {};
				body["CFSB"] = r.get("CFSB");
				body["TYSM"]=this.topPanel.items.get(1).getValue();
				var tymx=this.cfDetaiModule.getTymx();
				var tag=true;
				for(var id in tymx){
					if(id){
						tag=false;
					}
				}
				if(tag){
				MyMessageTip.msg("提示", "没有退药的记录", true);
				return;
				}
				var sfqt=this.cfDetaiModule.getSfqt();
				if(sfqt){
				body["QBTY"]=1;
				}
				body["TYMX"]=tymx;
				Ext.Msg.show({
					title : "提示",
					msg : "确定退药?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.panel.el.mask("正在退药,请耐心等待...",
									"x-mask-loading");
							var _ctr = this;
							var whatsthetime = function() {
								var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : _ctr.serviceId,
									serviceAction : _ctr.saveBackMedicineServiceActionID,
									body : body
								});
								if (ret.code > 300) {
									_ctr.panel.el.unmask();
									_ctr.processReturnMsg(ret.code, ret.msg,
											_ctr.onBeforeSave);
									return;
								}
								_ctr.panel.el.unmask();
								_ctr.cfList.refresh();
								_ctr.cfDetaiModule.doNew();
								MyMessageTip.msg("提示", "退药成功", true);
							}
							whatsthetime.defer(1000);
						}
					},
					scope : this
				});

			},
			// 查询
			onQuery : function() {
				var datefrom = this.topPanel.items.get(3).getValue();
				var dateTo = this.topPanel.items.get(5).getValue();
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var timeCnd = null;
				if (datefrom != null && datefrom != ""
						&& (dateTo == null || dateTo == "")) {
					timeCnd = ['ge', ['$', "str(a.FYRQ,'yyyy-mm-dd')"],
							['s', datefrom]];
				} else if (dateTo != null && dateTo != ""
						&& (datefrom == null || datefrom == "")) {
					timeCnd = ['le', ['$', "str(a.FYRQ,'yyyy-mm-dd')"],
							['s', dateTo]];
				} else if (dateTo != null && dateTo != "" && datefrom != null
						&& datefrom != "") {
					timeCnd = [
							'and',
							['ge', ['$', "str(a.FYRQ,'yyyy-mm-dd')"],
									['s', datefrom]],
							['le', ['$', "str(a.FYRQ,'yyyy-mm-dd')"],
									['s', dateTo]]];
				}
				if (timeCnd != null) {
					this.cfList.requestData.cnd = ['and',
							this.cfList.requestData.cnd, timeCnd,['eq',['$','a.FYBZ'],['i',1]],['notNull',['$','a.FPHM']]];
					this.cfList.refresh();
				}
			},
			// 当没发药记录时调用,清空明细
			onNoRecord : function() {
				this.cfDetaiModule.doNew();
			},
			afterOpen : function() {
				if (!this.cfList || !this.cfDetaiModule) {
					return;
				}
				this.onQuery();
			},
			// 获取退药说明下拉框
			getSelectBox : function() {
				var dic = {};
				dic.id = "phis.dictionary.tysm";
				dic.src = "phis.application.cic.schemas.MS_CF01.TYSM";
				dic.defaultValue = 0;
				dic.width = 150;
				return this.createDicField(dic)
			},
			createDicField : function(dic) {
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render
				}
				cls += "DicFactory"

				$import(cls)
				var factory = eval("(" + cls + ")")
				var field = factory.createDic(dic)
				return field
			}
		});