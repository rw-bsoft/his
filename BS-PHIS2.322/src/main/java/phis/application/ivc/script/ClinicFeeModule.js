/**
 * 药房发药
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");

phis.application.ivc.script.ClinicFeeModule = function(cfg) {
	this.exContext = {};
	cfg.noDefaultBtnKey = true;
	phis.application.ivc.script.ClinicFeeModule.superclass.constructor.apply(
			this, [cfg]);
	this.on('doSave', this.doSave, this);
	this.on("shortcutKey", this.shortcutKeyFunc, this);// 监听快捷键
}
Ext.extend(phis.application.ivc.script.ClinicFeeModule,
		phis.script.SimpleModule, {
			loadSystemParam : function() {
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "publicService",
							serviceAction : "loadSystemParams",
							body : {
								privates : ['XSFJJJ', 'HQFYYF']
							}
						});
				this.exContext.systemParams = resData.json.body;
			},
			initPanel : function() {
				this.loadSystemParam();
				var brlist = new Ext.Panel({
							layout : "fit",
							border : false,
							split : true,
							region : 'west',
							width : 240,
							collapsible : true,
							collapsed : false,
							animCollapse : false,
							items : this.getList()
						});
				this.brlist = brlist;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [brlist, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getFeeModule2()
									}],
							tbar : new Ext.Toolbar({
										enableOverflow : true,
										items : (this.tbar || []).concat([this
												.createButton()])
									})
						});
				this.panel = panel;
				this.panel.on("beforeclose", this.beforeClose, this);
				this.panel.on("destroy", function() {
							// add by yangl 增加业务锁
							if (this.FeeModule2.MZXX) {
								var locks = [];
								var p = {};
								p.YWXH = '1003';
								p.BRID = this.FeeModule2.MZXX.BRID;
								locks[0] = p;
								var p1 = {};
								p1.YWXH = '1009';
								p1.SDXH = this.mainApp.uid
								locks[1] = p1;
								if (!this.FeeModule2.bclUnlock(locks))
									return;
							}
						}, this)
				return panel;
			},
			doNew : function() {
			},
			getList : function() {
				this.cfList = this.createModule("cfList", this.refList);
				this.cfList.on("brselect", this.onbrselect, this);
				this.cfList.on("brCanclebrselect", this.onbrCanclebrselect,
						this);
				this.cfList.opener = this;
				// this.cfList.on("brChoose", this.onbrChoose, this);
				return this.cfList.initPanel();
			},
			onbrselect : function(record) {
				var field = this.FeeModule2.formModule.form.getForm()
						.findField("MZGL");
				field.setValue();
				this.FeeModule2.BLLX = {
					"BLLX" : record.data.BLLX
				};
				this.FeeModule2.formModule.doJZKHChange(record.data);
			},
			onbrCanclebrselect : function() {
				this.FeeModule2.doQx();
			},
			// onbrChoose : function(){},
			getFeeModule2 : function() {
				this.FeeModule2 = this.createModule("feeModule2",
						this.refModule);
				this.FeeModule2.exContext = this.exContext;
				this.FeeModule2.opener = this;
				this.FeeModule2.on("brselect", this.onbrselect, this);
				return this.FeeModule2.initPanel();
			},
			onCfSelect : function(record) {
				var cfsb = record.data.CFSB;
				var cflx = record.data.CFLX;
				if (!this.FeeModule2) {
					return;
				}
				this.FeeModule2.showDetail(cfsb, cflx);
			},
			onCfCancleSelect : function() {
				var lastIndex = this.midiModules['cfList'].grid
						.getSelectionModel().lastActive;
				this.midiModules['cfList'].selectRow(lastIndex);
			},
			// 当没发药记录时调用,清空明细
			onNoRecord : function() {
				this.FeeModule2.doNew();
			},
			beforeClose : function() {
				var store = this.FeeModule2.module.list.grid.getStore();
				if (store.getCount() > 0) {
					var r = store.getAt(0);
					if (r.data.YPXH) {
						if (this.FeeModule2.MZXX.mxsave) {
							Ext.Msg.confirm("确认", "当前收费明细已发生变化,是否预保存当前信息?",
									function(btn) {
										if (btn == 'yes') {
											this.FeeModule2.doSave();
											this.opener.closeCurrentTab();
										} else {
											this.opener.closeCurrentTab();
										}
									}, this);
							return false;
						}
					}
				}
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
				if (this.butRule) {
					var ac = util.Accredit;
					if (ac.canCreate(this.butRule)) {
						this.actions.unshift({
									id : "create",
									name : "新建"
								});
					}
				}
				// var f1 = 112

				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					// btn.accessKey = f1 + i + this.buttonIndex,
					btn.cmd = action.id;
					btn.text = action.name;
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.prop = {};
					Ext.apply(btn.prop, action);
					Ext.apply(btn.prop, action.properties);
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},

			F3 : function() {
				this.FeeModule2.module.list.doXY();
			},
			F4 : function() {
				this.FeeModule2.module.list.doZY();
			},
			F5 : function() {
				this.FeeModule2.module.list.doCY();
			},
			F2 : function() {
				this.FeeModule2.module.list.doJX();
			},
			ctrl_Q : function() {
				this.FeeModule2.module.list.doInsert();
			},
			ctrl_W : function() {
				this.FeeModule2.module.list.doNewGroup();
			},
			ctrl_R : function() {
				this.FeeModule2.module.list.doRemove();
			},
			ctrl_D : function() {
				this.FeeModule2.module.list.doDelGroup();
			},
			F1 : function() {
				this.FeeModule2.doNewPerson();
			},
			F8 : function() {
				this.FeeModule2.doSave();
			},
			F9 : function() {
				this.FeeModule2.doJs();
			},
			ctrl_S : function() {
				this.FeeModule2.doQx();
			},
			F6 : function() {
				this.FeeModule2.doFz();
			},
			F7 : function() {
				this.FeeModule2.doXg();
			},
			F10 : function() {
				this.FeeModule2.doZDCR();
			},
			F11 : function() {
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "publicService",
							serviceAction : "loadSystemParams",
							body : {
								// 私有参数
								privates : ['SFQYJKJSAN']
							}
						});
				var code = res.code;
				var msg = res.msg;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				if (res.json.body && res.json.body.SFQYJKJSAN == '0') {
					return;
				}
				this.FeeModule2.doNewPhysical();
			}
		});