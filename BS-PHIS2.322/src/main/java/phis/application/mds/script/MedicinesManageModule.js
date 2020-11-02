/**
 * 
 * 药品公用信息模版
 * 
 * @author tianj
 */
$package("phis.application.mds.script");

$import("phis.script.TabModule");

phis.application.mds.script.MedicinesManageModule = function(cfg) {
	cfg.width = 1024;
	cfg.activateId = 0;
	cfg.modal = true;
	cfg.autoLoadData = false;
	phis.application.mds.script.MedicinesManageModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("close", this.onClose, this)
}

Ext.extend(phis.application.mds.script.MedicinesManageModule,
		phis.script.TabModule, {
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
				var buttons=[];
				if(!this.yszhlyy){//update by caijy at 2015.1.19 for 医生站合理用药信息调阅,界面只读
				buttons=[{
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
				}
				var tab = new Ext.TabPanel({
							deferredRender : false,
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : false,
							autoHeight : true,
							buttonAlign : "center",
							defaults : {
								frame : false,
								autoHeight : true,
								autoWidth : true
							},
							items : tabItems,
							buttons : buttons
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.activate(this.activateId)
				this.tab = tab
				return this.tab;
			},

			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (this.midiModules["mdsproptab"]) {
					this.midiModules["mdsproptab"].isNewOpen = 1;
				}
				if (this.midiModules["mdsaliastab"]) {
					this.midiModules["mdsaliastab"].isNewOpen = 1;
				}
				if (this.midiModules["mdslimittab"]) {
					this.midiModules["mdslimittab"].isNewOpen = 1;
				}
				if (this.midiModules["mdspricetab"]) {
					this.midiModules["mdspricetab"].isNewOpen = 1;
				}
				if (this.midiModules["ypsmtab"]) {
					this.midiModules["ypsmtab"].isNewOpen = 1;
				}
				if (this.tab.activeTab.id == "mdsproptab") {
					if (this.midiModules["mdsproptab"]) {
						this.midiModules["mdsproptab"].doNew();
						this.midiModules["mdsproptab"].isNewOpen = 0;
					}
				} else {
					this.tab.activate(0);
				}
				if (this.midiModules["mdsaliastab"]) {
					this.moduleOperation("create",
							this.midiModules["mdsaliastab"], null);
				}
				if (this.midiModules["mdslimittab"]) {
					this.moduleOperation("create",
							this.midiModules["mdslimittab"], null);
				}
				if (this.midiModules["mdspricetab"]) {
					this.moduleOperation("create",
							this.midiModules["mdspricetab"], null);
				}
				if (this.midiModules["ypsmtab"]) {
					this.moduleOperation("create", this.midiModules["ypsmtab"],
							null);
				}
			},
			loadData : function(op_ysz) {
				var op="update"
				if(op_ysz){
					op=op_ysz
				}
				if(op_ysz=="read"){
				this.tab.activate(4);
				}
				if (this.midiModules["mdsproptab"]) {
					this.midiModules["mdsproptab"].isNewOpen = 1;
				}
				if (this.midiModules["mdsaliastab"]) {
					this.midiModules["mdsaliastab"].isNewOpen = 1;
				}
				if (this.midiModules["mdslimittab"]) {
					this.midiModules["mdslimittab"].isNewOpen = 1;
				}
				if (this.midiModules["mdspricetab"]) {
					this.midiModules["mdspricetab"].isNewOpen = 1;
				}
				if (this.midiModules["ypsmtab"]) {
					this.midiModules["ypsmtab"].isNewOpen = 1;
				}
				if (this.tab.activeTab.id == "mdsproptab") {
					if (this.midiModules["mdsproptab"]) {
						this.midiModules["mdsproptab"].initDataId = this.initDataId;
						this.midiModules["mdsproptab"].loadData();
						this.midiModules["mdsproptab"].isNewOpen = 0;
					}
				} else {
					if(op_ysz!="read"){
					this.tab.activate(0);
					}
				}
				if (this.midiModules["mdsaliastab"]) {
					this.moduleOperation(op,
							this.midiModules["mdsaliastab"], null);
				}
				if (this.midiModules["mdslimittab"]) {
					this.moduleOperation(op,
							this.midiModules["mdslimittab"], null);
				}
				if (this.midiModules["mdspricetab"]) {
					this.moduleOperation(op,
							this.midiModules["mdspricetab"], null);
				}
				if (this.midiModules["ypsmtab"]) {
					this.moduleOperation(op, this.midiModules["ypsmtab"],
							null);
				}
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					if (this.midiModules[newTab.id]) {
						this.moduleOperation(this.op,
								this.midiModules[newTab.id], null);
					}
					this.tab.doLayout();
					return;
				}
				var exCfg = newTab.exCfg
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true
				}
				Ext.apply(cfg, exCfg);
				var ref = exCfg.ref
				if (ref) {
					var body = this.loadModuleCfg(ref);
					Ext.apply(cfg, body)
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
									var m = eval("new " + cls + "(cfg)");
									m.yszhlyy=this.yszhlyy
									m.setMainApp(this.mainApp);
									this.midiModules[newTab.id] = m;
									var p = m.initPanel();
									this.moduleOperation(this.op, m, null);
									m.on("save", this.onSuperFormRefresh, this);
									newTab.add(p);
									newTab.__inited = true;
								}, this])
				this.tab.doLayout();
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
						if (k == 0) {
							return;
						}
					}
				}
				if ((!values["mdsaliastab"] || values["mdsaliastab"].length == 0)
						&& this.op == "create") {
					if (this.midiModules["mdsproptab"]) {
						var form = this.midiModules["mdsproptab"].form
								.getForm();
						var ypmc = form.findField("YPMC").getValue();
						var pydm = form.findField("PYDM").getValue();
						var wbdm = form.findField("WBDM").getValue();
						var jxdm = form.findField("JXDM").getValue();
						var bhdm = form.findField("BHDM").getValue();
						var qtdm = form.findField("QTDM").getValue();
						values["mdsaliastab"] = [];
						var data = {
							'_opStatus' : 'create'
						};
						data["YPXH"] = null;
						data["YPMC"] = ypmc;
						data["PYDM"] = pydm;
						data["WBDM"] = wbdm;
						data["JXDM"] = jxdm;
						data["BHDM"] = bhdm;
						data["QTDM"] = qtdm;
						data["BMFL"] = 1;
						values["mdsaliastab"].push(data);
					}
				}
				if (this.op == "update" && values["mdsaliastab"]
						&& values["mdsaliastab"].length == 0) {

					Ext.Msg.alert("提示", "别名不能为空");
					return;
				}
				this.tab.el.mask("正在执行操作...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.saveServiceAction,
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
							// this.op = "update";
					}, this);
			},
			moduleOperation : function(op, module, values) {
				var viewType = module.viewType;
				if (op == "read") {//update by caijy at 2015.1.19 for 医生站合理用药信息调阅,界面只读
				module.isRead=true;
				if (viewType == "form") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.initDataId
							var whatsthetime = function() {
								module.doRead();
							}
							whatsthetime.defer(500);
							module.isNewOpen = 0;
						}

					} else if (viewType == "list") {
						if (module.isNewOpen == 0) {
						} else {
							
							module.requestData.cnd = ['eq', ['$', 'ypxh'],
									['i', this.initDataId]];
							module.doRead();
							module.isNewOpen = 0;
						}
					} else if (viewType == "editlist") {
						if (module.isNewOpen == 0) {
						} else {
							module.requestData.serviceId = module.serviceId;
							module.requestData.serviceAction = module.listMedicinesLimitServiceId;
							module.requestData.ypxh = this.initDataId;
							module.loadData();
							module.isNewOpen = 0;
						}
					} else if (viewType == "priceList") {
						module.op = "update";
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.initDataId
							module.doRead();
							module.isNewOpen = 0;
						}
					} else if (viewType == "text") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.initDataId
							module.doRead();
							module.isNewOpen = 0;
						}
					}
				}else if (op == "create") {
					if (viewType == "form") {
						if (module.isNewOpen == 0) {
						} else {
							module.doNew();
							module.isNewOpen = 0;
						}
					} else if (viewType == "list") {
						if (module.isNewOpen == 0) {
						} else {
							module.clear();
							module.isNewOpen = 0;
						}
					} else if (viewType == "editlist") {
						if (module.isNewOpen == 0) {
						} else {
							module.requestData.serviceId = module.serviceId;
							module.requestData.serviceAction = module.listMedicinesLimitServiceId;
							module.requestData.ypxh = 0;
							module.loadData();
							module.isNewOpen = 0;
						}

					} else if (viewType == "priceList") {
						module.op = "create";
						if (module.isNewOpen == 0) {
						} else {
							module.clear();
							module.isNewOpen = 0;
						}
					} else if (viewType == "text") {
						if (module.isNewOpen == 0) {
						} else {
							module.doNew();
							module.isNewOpen = 0;
						}
					}
				} else if (op == "update") {
					if (viewType == "form") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.initDataId
							var whatsthetime = function() {
								module.loadData();
							}
							whatsthetime.defer(500);
							module.isNewOpen = 0;
						}

					} else if (viewType == "list") {
						if (module.isNewOpen == 0) {
						} else {
							module.requestData.cnd = ['eq', ['$', 'ypxh'],
									['i', this.initDataId]];
							module.loadData();
							module.isNewOpen = 0;
						}
					} else if (viewType == "editlist") {
						if (module.isNewOpen == 0) {
						} else {
							module.requestData.serviceId = module.serviceId;
							module.requestData.serviceAction = module.listMedicinesLimitServiceId;
							module.requestData.ypxh = this.initDataId;
							module.loadData();
							module.isNewOpen = 0;
						}
					} else if (viewType == "priceList") {
						module.op = "update";
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.initDataId
							module.loadData();
							module.isNewOpen = 0;
						}
					} else if (viewType == "text") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.initDataId
							module.loadData();
							module.isNewOpen = 0;
						}
					}
				} else if (op == "save") {
					if (viewType == "form") {
						var i = module.doValidation();
						if (i == 0) {
							return 0;
						}
						var re = util.schema.loadSync(module.entryName);
						var form = module.form.getForm();
						if (!form.isValid()) {
							return 0;
						}
						for (var i = 0; i < re.schema.items.length; i++) {
							var item = re.schema.items[i];
							if (item.pkey) {
								values[item.id] = this.initDataId;
							}
							var field = form.findField(item.id);
							if (field) {
								var value = field.getValue();
								if(item.id=="YPDM"){
									if(field.getRawValue()==null||field.getRawValue()==""){
									value="";
									}
								}
								if (value == null || value === "") {
									if (item.id == "TSYP" ) {
										value = 0;
									}else if(item.id == "QZCL"){
									value = 2;
									}
									if (!item.pkey && item["not-null"]) {
										Ext.Msg
												.alert("提示", item.alias
																+ "不能为空");
										return 0;
									}
								}
								if (value && typeof(value) == "string") {
									value = value.trim();
								}
								if (item.id == "YFBZ") {
									if (!/^[1-9]+[0-9]*]*$/.test(value)) {
										Ext.Msg.alert("提示", item.alias
														+ "必须是整数");
										return 0;
									};

								}
								values[item.id] = value;
							} else if ((item.defaultValue && !item.virtual)
									|| item.defaultValue == 0) {
								var dvalue = item.defaultValue;
								if (typeof dvalue == "object") {
									values[item.id] = dvalue.key;
								} else {
									values[item.id] = dvalue;
								}
							}
						}
					} else if (viewType == "list") {
						values[module.acid] = [];
						count = module.store.getCount();
						for (var i = 0; i < count; i++) {
							if (module.store.getAt(i).data["YPMC"] == null
									|| module.store.getAt(i).data["YPMC"] == '') {
								continue;
							}
							for (var j = 0; j < count; j++) {
								if (i == j) {
									continue;
								}
								if (module.store.getAt(i).data["YPMC"] == module.store
										.getAt(j).data["YPMC"]) {
									Ext.Msg.alert("提示", "别名不能重复");
									return 0;
								}
								if (module.store.getAt(i).data["PYDM"] == module.store
										.getAt(j).data["PYDM"]) {
									Ext.Msg.alert("提示", "拼音码不能重复");
									return 0;
								}
							}
							values[module.acid]
									.push(module.store.getAt(i).data);
						}

					} else if (viewType == "editlist") {
						values[module.acid] = [];
						count = module.store.getCount();
						for (var i = 0; i < count; i++) {
							if (module.store.getAt(i).data["ZFBL"] != ''
									&& module.store.getAt(i).data["ZFBL"] != null) {
								module.store.getAt(i).data["ZFBL"] = parseFloat((module.store
										.getAt(i).data["ZFBL"] / 100))
										.toFixed(3);
								values[module.acid]
										.push(module.store.getAt(i).data);
							}

						}
					} else if (viewType == "priceList") {
						values[module.acid] = [];
						count = module.store.getCount();
						for (var i = 0; i < count; i++) {
							var zfpb = module.store.getAt(i).data["ZFPB"];
							if (zfpb == 1) {
								continue;
							}
							var ypcd = module.store.getAt(i).data["YPCD"];
							if (ypcd == "" || ypcd == null) {
								continue;
							}
							if (module.store.getAt(i).data["PZWH"].length > 30) {
								MyMessageTip.msg("提示", "批准文号长度不能超过30位", true);
								module.grid.startEditing(i, 5);
								return 0;
							}
							if (module.store.getAt(i).data["GMP"] == ""
									&& module.store.getAt(i).data["GMP"] != 0) {
								MyMessageTip
										.msg("提示", "GMP不能为空,请从下拉里面选值", true);
								module.grid.startEditing(i, 6);
								return 0;
							}
							values[module.acid]
									.push(module.store.getAt(i).data);
						}

					} else if (viewType == "text") {
						var form = module.form.getForm();
						values["MESS"] = form.findField("MESS").getValue();
					}
				}
				return 1;
			},
			onClose : function() {
				this.tab.activate(0);
			}
		});