/**
 * 药品私用信息模版
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.TabModule");

phis.application.sto.script.StorehouseMedicinesPirvateModule = function(cfg) {
	cfg.width = 1024;
	cfg.activateId = 0;
	cfg.modal = true;
	phis.application.sto.script.StorehouseMedicinesPirvateModule.superclass.constructor.apply(
			this, [cfg]);
	this.on("close", this.onClose, this)
}

Ext.extend(phis.application.sto.script.StorehouseMedicinesPirvateModule, phis.script.TabModule,
		{
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					tabItems.push({
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								id : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							title : "",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							autoHeight : true,
							buttonAlign : "center",
							defaults : {
								border : false,
								autoHeight : true,
								autoWidth : true
							},
							items : tabItems,
							buttons : [{
										xtype : "button",
										text : "确定",
										handler : this.doSave,
										scope : this
									}, {
										xtype : "button",
										text : "取消",
										handler : function() {
											this.getWin().hide()
										},
										scope : this
									}]
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			loadData : function() {
				if (this.midiModules["mdspropTab"]) {
					this.midiModules["mdspropTab"].isNewOpen = 1;
				}
				if (this.midiModules["mdsaliasTab"]) {
					this.midiModules["mdsaliasTab"].isNewOpen = 1;
				}
				if (this.midiModules["mdslimitTab"]) {
					this.midiModules["mdslimitTab"].isNewOpen = 1;
				}
				if (this.midiModules["priceTab"]) {
					this.midiModules["priceTab"].isNewOpen = 1;
				}

				if (this.tab.activeTab.id == "mdspropTab") {
					if (this.midiModules["mdspropTab"]) {
						this.moduleOperation("update",
								this.midiModules["mdspropTab"], null);
					}
				} else {
					this.tab.activate(0);
				}
				if (this.midiModules["mdsaliasTab"]) {
					this.moduleOperation("update",
							this.midiModules["mdsaliasTab"], null);
				}
				if (this.midiModules["mdslimittab"]) {
					this.moduleOperation("update",
							this.midiModules["mdslimitTab"], null);
				}
				if (this.midiModules["pricetab"]) {
					this.moduleOperation("update",
							this.midiModules["priceTab"], null);
				}
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					if (this.midiModules[newTab.id]) {
						this.moduleOperation(this.op,
								this.midiModules[newTab.id], null);
								}
					return;
				}

				var exCfg = newTab.exCfg
				var exContext = {
					readOnly : true
				}
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true
				}
				if (exCfg.id == "mdsaliasTab") {
					cfg.exContext = exContext;
				}
				Ext.apply(cfg, exCfg);
				var ref = exCfg.ref
				if (ref) {
					var result = this.mainApp.taskManager.loadModuleCfg(ref);
					if (result.code == 200) {
						Ext.apply(cfg, result.json.body)
					}
				} 
				var cls = cfg.script
				if (!cls) {
					return;
				}

				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$import(cls);
				$require(cls, [function() {
									var m = eval("new " + cls + "(cfg)")
									m.setMainApp(this.mainApp);
									this.midiModules[newTab.id] = m;
									var p = m.initPanel();
									this.moduleOperation(this.op, m, null)
									m.on("save", this.onSuperFormRefresh, this);
									newTab.add(p);
									newTab.__inited = true;
								}, this])
				this.tab.doLayout();
				this.tab.syncSize();
			},
			doSave : function() {
				var values = {};
				var actions = this.actions;
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					var module = this.midiModules[ac.id];
					if (module) {
						module.acid = ac.id;
						var k = this.moduleOperation("save", module, values);
						if (k == 1) {
							return;
						}
					}
				}
				this.tab.el.mask("正在执行操作...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.saveServiceActionId,
							op : this.op,
							body : values
						}, function(code, msg, json) {
							this.tab.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.fireEvent("save");
							this.getWin().hide();
						}, this);
			},
			moduleOperation : function(op, module, values) {
				var viewType = module.viewType;
				if (op == "update") {
					if (viewType == "form") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataBody = this.initDataBody;
							module.loadData();
							module.isNewOpen = 0;
						}
					} else if (viewType == "list") {
						module.isRead=true;
						if (module.isNewOpen == 0) {
						} else {
							module.requestData.cnd = ['eq',['$','YPXH'],['l',this.initDataBody['YPXH']]]
							module.loadData();
							module.isNewOpen = 0;
						}
					} else if (viewType == "editlist") {
						module.isRead=true;
						if (module.isNewOpen == 0) {
						} else {
							module.requestData.serviceId = module.fullserviceId;
							module.requestData.serviceAction = module.listMedicinesLimitServiceId;
							module.requestData.ypxh = this.initDataBody['YPXH'];
							module.loadData();
							module.isNewOpen = 0;
						}
					} else if (viewType == "priceList") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataBody = this.initDataBody;
							module.loadData();
							module.isNewOpen = 0;
						}
					}
				} else if (op == "save") {
					if (viewType == "form") {
						var re = util.schema.loadSync(module.entryName);
						var form = module.form.getForm();
						if(!form.isValid()){
						return 1;
						}
						if(!module.onGDCChange()){
						return 1;}
						for (var i = 0; i < re.schema.items.length; i++) {
							var item = re.schema.items[i];
							if (item.layout != "GDC") {
								continue;
							}
							if (item.pkey) {
								values[item.id] = this.initDataBody[item.id];
							}
							var field = form.findField(item.id);
							
							if (field) {
								var value = field.getValue();
								if ((!value || value == "" || value == null)
										&& value != 0) {
									if (!item.pkey && item["not-null"]) {
										Ext.Msg
												.alert("提示", item.alias
																+ "不能为空");
										return 1;
									}
								}
								if (value && typeof(value) == "string") {
									value = value.trim();
								}
								values[item.id] = value;
							} else if (item.defaultValue && !item.virtual
									&& !item.update) {
								var dvalue = item.defaultValue;
								if (typeof dvalue == "object") {
									values[item.id] = dvalue.key;
								} else {
									values[item.id] = dvalue;
								}
							}
						}
					} else if (viewType == "priceList") {
						values[module.acid] = [];
						count = module.store.getCount();
						for (var i = 0; i < count; i++) {
							var ypcd = module.store.getAt(i).data["YPCD"];
							if (ypcd == "" || ypcd == null) {
								continue;
							}
							if(module.store.getAt(i).data["PZWH"].length>30){
							MyMessageTip.msg("提示", "批准文号长度不能超过30位", true);
							module.grid.startEditing(i,5);
							return 1;
							}
							if(module.store.getAt(i).data["GMP"]==""&&module.store.getAt(i).data["GMP"]!=0){
							MyMessageTip.msg("提示", "GMP不能为空,请从下拉里面选值", true);
							module.grid.startEditing(i,6);
							return 1;
							}
							values[module.acid]
									.push(module.store.getAt(i).data);
						}

					}
					return 0;
				}

			},
			onClose : function() {
				this.tab.activate(0);
			}
		});