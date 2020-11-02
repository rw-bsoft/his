$package("phis.application.sup.script");
$import("phis.script.TabModule");

phis.application.sup.script.MaintenanceServiceModule = function(cfg) {
	cfg.width = 900;
	cfg.height = 650;
	cfg.activateId = 0;
	cfg.modal = this.modal = true;
	phis.application.sup.script.MaintenanceServiceModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("close", this.onClose, this);
	this.wxlb = 1;
}

Ext.extend(phis.application.sup.script.MaintenanceServiceModule,
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
							tbar : [{
										xtype : "button",
										id : "save",
										width : 80,
										cmd : "save",
										text : "保存",
										iconCls : "save",
										handler : this.doSave,
										scope : this
									}, {
										xtype : "button",
										width : 80,
										cmd : "cancel",
										text : "取消",
										iconCls : "common_cancel",
										handler : function() {
											this.getWin().hide();
											this.fireEvent("save");
										},
										scope : this
									}, {
										xtype : "button",
										id : "print1",
										width : 80,
										cmd : "print",
										text : "打印",
										iconCls : "print",
										handler : this.doPrint,
										scope : this
									}]
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.activate(this.activateId);
				this.tab = tab;
				return this.tab;
			},
			loadData : function() {
				if (this.midiModules["servicetab"]) {
					this.midiModules["servicetab"].isNewOpen = 1;
				}
				if (this.midiModules["equipmenttab"]) {
					this.midiModules["equipmenttab"].isNewOpen = 1;
				}
				if (this.tab.activeTab.id == "servicetab") {
					if (this.midiModules["servicetab"]) {
						this.midiModules["servicetab"].initDataId = this.WXXH;
						this.midiModules["servicetab"].loadData();
						this.midiModules["servicetab"].isNewOpen = 0;
					}
				} else {
					if (this.midiModules["equipmenttab"]) {
						this.midiModules["equipmenttab"].initDataId = this.WXXH;
						this.midiModules["equipmenttab"].loadData();
						this.midiModules["equipmenttab"].isNewOpen = 0;
					}
				}
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					this.fireEvent("tabchange", tabPanel, newTab, curTab);
					var tbar = this.tab.getTopToolbar();
					if (newTab.id == "equipmenttab") {
						tbar.getComponent('print1').hide();
						if(this.wxlb==2){
							this.midiModules["equipmenttab"].form.getForm().findField("WXDW").enable();
						}else{
							this.midiModules["equipmenttab"].form.getForm().findField("WXDW").setValue("");
							this.midiModules["equipmenttab"].form.getForm().findField("WXDW").disable();
						}
					} else {
						tbar.getComponent('print1').show();
					}
					if (this.midiModules[newTab.id]) {
						this.moduleOperation("update",
								this.midiModules[newTab.id], null);
					}
					if (this.SBWX == 0) {
						this.tab.activate(0);
					}
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
				$require(cls, [function() {
							var m = eval("new " + cls + "(cfg)");
							m.setMainApp(this.mainApp);
							if (this.exContext) {
								m.exContext = this.exContext;
							}
							this.midiModules[newTab.id] = m;
							m.oper = this;
							m.tabid=newTab.id;
							this.mthis = m;
							var p = m.initPanel();
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							if (newTab.id == "servicetab") {
								if(this.win){
									this.win.center();
								}
							}
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
							var tbar = this.tab.getTopToolbar();
							if (newTab.id == "equipmenttab") {
								tbar.getComponent('print1').hide();
								if(this.wxlb==2){
									this.midiModules["equipmenttab"].form.getForm().findField("WXDW").enable();
								}else{
									this.midiModules["equipmenttab"].form.getForm().findField("WXDW").setValue("");
									this.midiModules["equipmenttab"].form.getForm().findField("WXDW").disable();
								}
							} else {
								tbar.getComponent('print1').show();
							}
							this.moduleOperation("update",
									this.midiModules[newTab.id], null);
							m.on("loadData", this.onLoadData, this);
							if (this.SBWX == 0) {
								this.tab.activate(0);
							}
						}, this])
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
				if (this.wxlb == 1) {
					values["WXDW"] = "0";
				}
				if(!this.midiModules["equipmenttab"]){
				if(this.SBWX==1&&this.wxlb == 2){
					if(values["WXXH"]){
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "repairRequestrService",
							serviceAction : "queryWXDW",
							WXXH : values["WXXH"]
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.doSave);
							return;
						}else{
							if(ret.json.wxdw==0&&ret.json.wzxh!=0){
								 Ext.Msg.alert("提示", "该维修为外维修,维修单位不能为空!");
								 return;
							}
						}
					}
				}
				}
				if (this.midiModules["equipmenttab"]) {
					if(this.SBWX==1){
					values["WZXH"] = this.midiModules["equipmenttab"]
							.getFormData().WZXH;
					values["CJXH"] = this.midiModules["equipmenttab"]
							.getFormData().CJXH;
					values["ZBXH"] = this.midiModules["equipmenttab"]
							.getFormData().ZBXH;
					if(!this.midiModules["equipmenttab"].getFormData().WZXH){
						Ext.Msg.alert("提示", "物资名称不能为空!");
						return;
					}
					if (this.wxlb == 2) {
						if (this.midiModules["equipmenttab"].getFormData().WXDW == 0) {
							Ext.Msg.alert("提示", "该维修为外维修,维修单位不能为空!");
							return;
						}
					}
					if (this.midiModules["equipmenttab"].getFormData().SBXZ == 0) {
						Ext.Msg.alert("提示", "设备现状不能为空!");
						return;
					}
				}
				}
				this.tab.el.mask("正在执行操作...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "saveWXGLform",
							body : values
						}, function(code, msg, json) {
							this.tab.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							MyMessageTip.msg("提示", "保存成功！", true);
							this.fireEvent("save");
							this.getWin().hide();
						}, this);
			},
			moduleOperation : function(op, module, values) {
				var viewType = module.viewType;
				if (op == "update") {
					if (viewType == "form1") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.WXXH
							if (values) {
								values["WL_WXPJ"] = [];
								var count = module.list.store.getCount();
								for (var i = 0; i < count; i++) {
									if (module.list.store.getAt(i).data["WZXH"] == ""
											|| module.list.store.getAt(i).data["WZXH"] == null) {
										continue;
									}
									if (module.list.store.getAt(i).data["PJSL"] == 0
											|| module.list.store.getAt(i).data["PJSL"] == null) {
										Ext.Msg.alert("提示", "第" + (i + 1)
														+ "行数量为0");
										this.panel.el.unmask();
										return false;
									}
									if (module.list.store.getAt(i).data["PJJG"] == 0
											|| module.list.store.getAt(i).data["PJJG"] == null) {
										Ext.Msg.alert("提示", "第" + (i + 1)
														+ "行价格为0");
										this.panel.el.unmask();
										return false;
									}
									values["WL_WXPJ"].push(module.list.store
											.getAt(i).data);
								}
							}
							var whatsthetime = function() {
								module.loadData();
							}
							whatsthetime.defer(500);
							module.isNewOpen = 0;
						}

					} else if (viewType == "form2") {
						if (module.isNewOpen == 0) {
						} else {
							module.initDataId = this.WXXH
							module.loadData();
							module.isNewOpen = 0;
						}
					}
				} else if (op == "save") {
					if (viewType == "form1") {
						var re = util.schema.loadSync(module.entryName);
						var form = module.form.form.getForm();
						if (!form.isValid()) {
							return 0;
						}
						for (var i = 0; i < re.schema.items.length; i++) {
							var item = re.schema.items[i];
							if (item.pkey) {
								values[item.id] = this.WXXH;
							}
							var field = form.findField(item.id);
							if (field) {
								var value = field.getValue();
								if (value && typeof(value) == "string") {
									value = value.trim();
								}
								values[item.id] = value;
							} else if (item.defaultValue && !item.virtual) {
								var dvalue = item.defaultValue;
								if (typeof dvalue == "object") {
									values[item.id] = dvalue.key;
								} else {
									values[item.id] = dvalue;
								}
							}
						}
						values["WL_WXPJ"] = [];
						var count = module.list.store.getCount();
						for (var i = 0; i < count; i++) {
							if (module.list.store.getAt(i).data["PJMC"] == ""
									|| module.list.store.getAt(i).data["PJMC"] == null) {
								continue;
							}
							if (module.list.store.getAt(i).data["PJSL"] == 0
									|| module.list.store.getAt(i).data["PJSL"] == null) {
								Ext.Msg.alert("提示", "第" + (i + 1) + "行数量为0");
								this.panel.el.unmask();
								return false;
							}
							if (module.list.store.getAt(i).data["PJGG"] == 0
									|| module.list.store.getAt(i).data["PJGG"] == null) {
								Ext.Msg.alert("提示", "第" + (i + 1) + "行价格为0");
								this.panel.el.unmask();
								return false;
							}
							values["WL_WXPJ"]
									.push(module.list.store.getAt(i).data);
						}
					} else if (viewType == "form2") {
						var re = util.schema.loadSync(module.entryName);
						var form = module.form.getForm();
						if (!form.isValid()) {
							return 0;
						}
						for (var i = 0; i < re.schema.items.length; i++) {
							var item = re.schema.items[i];
							if (item.pkey) {
								values[item.id] = this.WXXH;
							}
							var field = form.findField(item.id);
							if (field) {
								var value = field.getValue();
								if (value && typeof(value) == "string") {
									value = value.trim();
								}
								values[item.id] = value;
							} else if (item.defaultValue && !item.virtual) {
								var dvalue = item.defaultValue;
								if (typeof dvalue == "object") {
									values[item.id] = dvalue.key;
								} else {
									values[item.id] = dvalue;
								}
							}
						}
					}
				}
				return 1;
			},
			onClose : function() {
				if (!this.WZXH) {
					this.sbxhcolumn.setValue(0);
				}
				this.tab.activate(0);
			},
			onLoadData : function() {
				if (this.midiModules["servicetab"]) {
					this.sbxhcolumn = this.midiModules["servicetab"].form.form
							.getForm().findField("SBWX");
					this.SBWX=this.sbxhcolumn.getValue();
					if (this.look == 0) {
						this.sbxhcolumn.enable();
					} else {
						this.sbxhcolumn.disable();
					}
					this.sbxhcolumn.on("check", this.oncheck, this);
					this.wxlbcolumn = this.midiModules["servicetab"].form.form.getForm().findField("WXLB");
					this.wxlb=this.wxlbcolumn.getValue();
					this.wxlbcolumn.on("select", this.onselect, this);
				}
			},
			oncheck : function() {
				if (this.alertmsg)
					return
				if (!this.WZXH) {
					if (this.sbxhcolumn.getValue() == true) {
						this.SBWX = 1;
						this.tab.activate(1);
					} else {
						this.SBWX = 0;
						this.tab.activate(0);
					}
				} else {
					if (this.sbxhcolumn.getValue() == false) {
						Ext.Msg.confirm("请确认", "取消后将清空设备维修状况信息,是否确定取消?",
								function(btn) {// 先提示是否删除
									if (btn == 'yes') {
										phis.script.rmi.jsonRequest({
													serviceId : this.serviceId,
													serviceAction : "updateWZBGWZXH",
													pkey : this.WXXH
												}, function(code, msg, json) {
													if (code >= 300) {
														this.processReturnMsg(
																code, msg);
														if (code != 604) {
															return;
														}
														this.SBWX=1;
													}else{
														this.SBWX=0;
													}
												}, this)
									} else {
										this.sbxhcolumn.setValue(1);
									}
								}, this);
						return;
					}
				}
			},
			onselect : function() {
				if (this.alertmsg)
					return
				this.wxlb = this.wxlbcolumn.getValue();
				if(this.midiModules["equipmenttab"]){
				if (this.mthis.form.getForm().findField("WXDW")) {
					if (this.wxlb == 2) {
						this.mthis.form.getForm().findField("WXDW").enable();
					} else {
						this.mthis.form.getForm().findField("WXDW").disable();
						this.mthis.form.getForm().findField("WXDW")
								.setValue("");
					}
				}
				}
			},
			doPrint : function() {
				this.printurl = util.helper.Helper.getUrl();
				var pages = "phis.prints.jrxml.MaintenanceMsgSearch";
				var url = "resources/" + pages + ".print?silentPrint=1"
				url += "&temp=" + new Date().getTime() + "&wxxh=" + this.WXXH
						+ "&flag=true";
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				// 预览LODOP.PREVIEW();
				// 预览LODOP.PRINT();
				// LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				// 预览
				LODOP.PREVIEW();
			}
		});