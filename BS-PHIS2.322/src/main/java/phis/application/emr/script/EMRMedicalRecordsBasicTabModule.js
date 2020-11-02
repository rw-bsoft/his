$package("phis.application.emr.script");

$import("phis.script.TabModule");

phis.application.emr.script.EMRMedicalRecordsBasicTabModule = function(cfg) {
	// this.showemrRootPage = true
	phis.application.emr.script.EMRMedicalRecordsBasicTabModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.emr.script.EMRMedicalRecordsBasicTabModule,
		phis.script.TabModule, {
			initPanel : function() {
				var autoUpdate = this.getAutoUpdate("phis.application.emr.schemas.EMR_BASY");
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrMedicalRecordsService",
							serviceAction : "loadMedicalRecords",
							ZYH : this.exContext.empiData.ZYH,
							autoUpdate : autoUpdate,
							BLLB : this.node.key,
							cnd : ['eq', ['$', 'JZXH'],
									['i', this.exContext.empiData.ZYH]]
						});
				var code = res.code;
				var msg = res.msg;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				if (!res.json.CKQX) {
					MyMessageTip.msg("提示", "当前用户没有查看权限！", true);
					return;
				}
				this.exContext.SYQX = res.json.SYQX;// 审阅权限
				this.exContext.SXQX = res.json.SXQX;// 书写权限
				this.exContext.DYQX = res.json.DYQX;// 打印权限
				this.exContext.BRXX = res.json.JBXX;
				this.exContext.ZYSSJL = res.json.ZYSSJL;
				this.exContext.ZYZDJL = res.json.ZYZDJL;
				this.NEW = res.json.NEW;
				if (res.json.count) {
					this.showUpdate = true;
				} else {
					this.showUpdate = false;
				}
				if (this.mainApp.dept) {
					this.exContext.BRXX.YLJGMC = this.mainApp.dept;
				}
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					if (!ac.ref)
						continue;
					tabItems.push({
								frame : this.frame,
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								id : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							resizeTabs : this.resizeTabs,
							tabPosition : this.tabPosition || "top",
							// autoHeight : true,
							defaults : {
								border : false
							},
							items : tabItems,
							tbar : (this.tbar || [])
									.concat(this.createButton())
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.on("beforetabchange", this.onBeforeTabChange, this);
				tab.on("afterrender", this.onReady, this)
				tab.on("beforeclose", this.beforeClose, this);
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			onReady : function() {
				var toolBar = this.tab.getTopToolbar();
				var btn = toolBar.find("cmd", "save");
				var btn1 = toolBar.find("cmd", "update");
				if (this.exContext.SXQX) {
					btn[0].enable();
					btn1[0].enable();
					if (this.showUpdate) {
						this.showUpdateModule(this.autoUpdate);
					}
				} else {
					btn[0].disable();
					btn1[0].disable();
				}
				var btn2 = toolBar.find("cmd", "print");
				if (this.exContext.DYQX) {
					btn2[0].enable();
				} else {
					btn2[0].disable();
				}
			},
			showUpdateModule : function(udData) {
				var module = this.createModule("gxxxList", this.refgxxxList);
				module.on("commit", this.submitUpdate, this)
				var win = module.getWin();
				win.add(module.initPanel());
				module.requestData.serviceId = "phis.emrMedicalRecordsService";
				module.requestData.serviceAction = "queryUpdate";
				module.requestData.ZYH = this.exContext.empiData.ZYH;
				module.requestData.BASY = this.exContext.BRXX;
				module.requestData.udData = udData;
				module.refresh();
				win.show();
			},
			doUpdate : function() {
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrMedicalRecordsService",
							serviceAction : "queryUpdateCount",
							ZYH : this.exContext.empiData.ZYH,
							udData : this.updated,
							BASY : this.exContext.BRXX
						});

				var code = res.code;
				var msg = res.msg;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				if (res.json.count > 0) {
					this.showUpdateModule(this.updated);
				}
			},
			submitUpdate : function(recordIds) {
				for (var i = 0; i < recordIds.length; i++) {
					var recordId = recordIds[i]
					this.exContext.BRXX[recordId.id] = recordId.value;
					if (document.getElementById(recordId.id)) {
						document.getElementById(recordId.id).value = recordId.value;
					}
					if (this.midiModules["WAR2102"]) {
						if (document.getElementById("JBXX_" + recordId.id)) {
							document.getElementById("JBXX_" + recordId.id).value = recordId.value;
						}
						if (recordId.id == 'CSNY') {
							this.midiModules["WAR2102"].CSNY
									.setValue(recordId.value)
						}
						if (recordId.id == 'RYRQ') {
							this.midiModules["WAR2102"].RYRQ
									.setValue(recordId.value)
						}
						if (recordId.id == 'CYRQ') {
							this.midiModules["WAR2102"].CYRQ
									.setValue(recordId.value)
						}
					}
					this.XGYZ = true;
				}
				this.midiModules["WAR2101"].exContext = this.exContext;
			},
			getAutoUpdate : function(entryName) {
				var schema = "";
				if (!schema) {
					var re = util.schema.loadSync(entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				var items = schema.items
				var size = items.length
				var autoUpdate = [];
				this.updated = [];
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if (it.autoUpdate) {
						autoUpdate.push({
									id : it.id,
									name : it.alias,
									type : it.type
								})
					}
					if (it.updated) {
						this.updated.push({
									id : it.id,
									name : it.alias,
									type : it.type
								})
					}
				}
				var fyschema = "";
				var fyre = util.schema.loadSync("phis.application.emr.schemas.EMR_BASY_FY")
				if (fyre.code == 200) {
					fyschema = fyre.schema;
				} else {
					this.processReturnMsg(fyre.code, fyre.msg,
							this.getAutoUpdate)
					return;
				}
				var fyitems = fyschema.items
				var fysize = fyitems.length
				for (var i = 0; i < fysize; i++) {
					var fyit = fyitems[i]
					autoUpdate.push({
								id : fyit.id,
								name : fyit.alias,
								type : fyit.type
							})
					this.updated.push({
									id : fyit.id,
									name : fyit.alias,
									type : fyit.type
								})
				}
				this.autoUpdate = autoUpdate;
				return autoUpdate;
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
					if (action.ref)
						continue;
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
			onBeforeTabChange : function(tabPanel, newTab, curTab) {
			},
			getRadioValue : function(name) {
				var radioObj = document.getElementsByName(name);
				for (var i = 0; i < radioObj.length; i++) {
					if(radioObj[i].type=="radio"){
						if (radioObj[i].checked==true) {
							return radioObj[i].value;
						}
					}
				}
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.id == 'WAR2101') {
					if (this.midiModules["WAR2102"]) {
						var JBXXObj = document.getElementsByName("JBXX");
						for (var i = 0; i < JBXXObj.length; i++) {
							if (JBXXObj[i].value) {
								var obj = document.getElementById(JBXXObj[i].id
										.substring(5));
								if (obj) {
									obj.value = JBXXObj[i].value;
								}
								if (document.getElementById("ZL_"
										+ JBXXObj[i].id.substring(5))) {
									document.getElementById("ZL_"
											+ JBXXObj[i].id.substring(5)).value = JBXXObj[i].value;
								}
								if (document.getElementById(JBXXObj[i].id
										.substring(5)
										+ "_text")) {
									if (JBXXObj[i].options[JBXXObj[i].selectedIndex].text == '--请选择--') {
										document.getElementById(JBXXObj[i].id
												.substring(5)
												+ "_text").value = "";
									} else {
										document.getElementById(JBXXObj[i].id
												.substring(5)
												+ "_text").value = JBXXObj[i].options[JBXXObj[i].selectedIndex].text
									}
								}
							}
						}
						if (document.getElementById("ZL_BRXB")) {
							if (this.getRadioValue("BRXB")) {
								document.getElementById("ZL_BRXB").value = this
										.getRadioValue("BRXB");
							}
						}
						if (document.getElementById("HYDM")) {
							if (this.getRadioValue("HYDM")) {
								document.getElementById("HYDM").value = this
										.getRadioValue("HYDM");
							}
						}
						document.getElementById("CSNY").value = this.midiModules["WAR2102"].CSNY
								.getRawValue();
						document.getElementById("ZL_RYRQ").value = this.midiModules["WAR2102"].RYRQ
								.getRawValue();
						document.getElementById("CYRQ").value = this.midiModules["WAR2102"].CYRQ
								.getRawValue();
					}
					if (this.midiModules["WAR2105"]) {
						var FYXXObj = document.getElementsByName("FYXX");

						for (var i = 0; i < FYXXObj.length; i++) {
							if (FYXXObj[i].value || FYXXObj[i].value == 0) {
								if (FYXXObj[i].id == 'FYXX_ZFJE') {
									var obj = document
											.getElementById('ZL_ZFJE');
									if (obj) {
										obj.value = FYXXObj[i].value;
									}
								} else {
									var obj = document
											.getElementById(FYXXObj[i].id
													.substring(5));
									if (obj) {
										obj.value = FYXXObj[i].value;
									}
								}
							}
						}
					}
					if (this.midiModules["WAR2106"]) {
						var YSQMObj = document.getElementsByName("YSQM");
						for (var i = 0; i < YSQMObj.length; i++) {
							if (YSQMObj[i].value) {
								var obj = document.getElementById(YSQMObj[i].id
										.substring(5));
								if (obj) {
									obj.value = YSQMObj[i].value;
								}
							}
							if (document.getElementById(YSQMObj[i].id
									.substring(5)
									+ "_text")) {
								if (YSQMObj[i].options[YSQMObj[i].selectedIndex].text == '--请选择--') {
									document.getElementById(YSQMObj[i].id
											.substring(5)
											+ "_text").value = "";
								} else {
									document.getElementById(YSQMObj[i].id
											.substring(5)
											+ "_text").value = YSQMObj[i].options[YSQMObj[i].selectedIndex].text
								}

							}
							if (YSQMObj[i].id == "YSQM_LYFS") {
								if (YSQMObj[i].value == 2) {
									document.getElementById("NJSYLJLMC_2").value = document
											.getElementById("YSQM_NJSYLJLMC").value;
									document.getElementById("NJSYLJLMC_3").value = "";
								} else if (YSQMObj[i].value == 3) {
									document.getElementById("NJSYLJLMC_2").value = "";
									document.getElementById("NJSYLJLMC_3").value = document
											.getElementById("YSQM_NJSYLJLMC").value;
								} else {
									document.getElementById("NJSYLJLMC_2").value = "";
									document.getElementById("NJSYLJLMC_3").value = "";
								}
							}
						}
						if (document.getElementById("GMYWBZ")) {
							if (this.getRadioValue("GMYWBZ")) {
								document.getElementById("GMYWBZ").value = this
										.getRadioValue("GMYWBZ");
							}
						}
						if (document.getElementById("SJBZ")) {
							if (this.getRadioValue("SJBZ")) {
								document.getElementById("SJBZ").value = this
										.getRadioValue("SJBZ");
							}
						}
						if (document.getElementById("RHXXDM")) {
							if (this.getRadioValue("RHXXDM")) {
								document.getElementById("RHXXDM").value = this
										.getRadioValue("RHXXDM");
							}
						}
						if (document.getElementById("ABOXXDM")) {
							if (this.getRadioValue("ABOXXDM")) {
								document.getElementById("ABOXXDM").value = this
										.getRadioValue("ABOXXDM");
							}
						}
						if (document.getElementById("CY31ZYBZ")) {
							if (this.getRadioValue("CY31ZYBZ")) {
								document.getElementById("CY31ZYBZ").value = this
										.getRadioValue("CY31ZYBZ");
							}
						}
						if (document.getElementById("BAZL")) {
							if (this.getRadioValue("BAZL")) {
								document.getElementById("BAZL").value = this
										.getRadioValue("BAZL");
							}
						}
						document.getElementById("ZKRQ").value = this.midiModules["WAR2106"].ZKRQ
								.getRawValue();
					}
					if (this.midiModules["WAR2104"]) {
						this.midiModules["WAR2104"].grid.stopEditing()
						var store = this.midiModules["WAR2104"].grid.getStore();
						var n = store.getCount()
						var data = []
						for (var i = 0; i < n; i++) {
							var r = store.getAt(i)
							if(!r.data["SSDM"]){
								continue
							}
							document.getElementById("SSDM_" + i).innerHTML = '&nbsp;'
									+ r.data["SSDM"];
							document.getElementById("SSRQ_" + i).innerHTML = '&nbsp;'
									+ r.data["SSRQ"];
							document.getElementById("SSJB_text_" + i).innerHTML = '&nbsp;'
									+ r.data["SSJB_text"];
							document.getElementById("SSMC_" + i).innerHTML = '&nbsp;'
									+ r.data["SSMC"];
							document.getElementById("SSYS_text_" + i).innerHTML = '&nbsp;'
									+ r.data["SSYS_text"];
							document.getElementById("YZYS_text_" + i).innerHTML = '&nbsp;'
									+ r.data["YZYS_text"];
							document.getElementById("EZYS_text_" + i).innerHTML = '&nbsp;'
									+ r.data["EZYS_text"];
							document.getElementById("YHDJ_text_" + i).innerHTML = '&nbsp;'
									+ r.data["YHDJ_text"];
							document.getElementById("MZFS_text_" + i).innerHTML = '&nbsp;'
									+ r.data["MZFS_text"];
							document.getElementById("MZYS_text_" + i).innerHTML = '&nbsp;'
									+ r.data["MZYS_text"];
						}
					}
					if (this.midiModules["WAR2103"]) {
						this.midiModules["WAR2103"].grid.stopEditing()
						var store = this.midiModules["WAR2103"].grid.getStore();
						var n = store.getCount()
						var data = []
						for (var i = 0; i < 22; i++){
							if (i > 3) {
								if (i == 4) {
									document.getElementById("MSZD_" + (i - 4)).innerHTML = '<span style="font-weight:bold;">&nbsp;主要诊断:</span>'
								} else if (i == 5 || i == 15) {
									document.getElementById("MSZD_" + (i - 4)).innerHTML = '<span style="font-weight:bold;">&nbsp;其他诊断:</span>'
								} else {
									document.getElementById("MSZD_" + (i - 4)).innerHTML = '&nbsp;'
								}
								document.getElementById("JBBM_" + (i - 4)).innerHTML = '&nbsp;'
								document.getElementById("RYBQDM_" + (i - 4)).innerHTML = '&nbsp;'
							} else if (i == 1) {
								document.getElementById("MZZD").value = "";
								document.getElementById("MZ_JBBM").value = "";
							} else if (i == 2) {
								document.getElementById("BLZD").value = "";
								document.getElementById("BL_JBBM").value = "";
							} else if (i == 3) {
								document.getElementById("SSZD").value = "";
								document.getElementById("SS_JBBM").value = "";
							}
						}
						for (var i = 0; i < n; i++) {
							if (i > 3) {
								var r = store.getAt(i)
								if (i == 4) {
									document.getElementById("MSZD_" + (i - 4)).innerHTML = '<span style="font-weight:bold;">&nbsp;主要诊断:</span>'
											+ r.data["MSZD"]
								} else if (i == 5 || i == 15) {
									document.getElementById("MSZD_" + (i - 4)).innerHTML = '<span style="font-weight:bold;">&nbsp;其他诊断:</span>'
											+ r.data["MSZD"]
								} else {
									document.getElementById("MSZD_" + (i - 4)).innerHTML = '&nbsp;'
											+ r.data["MSZD"]
								}
								document.getElementById("JBBM_" + (i - 4)).innerHTML = '&nbsp;'
										+ r.data["JBBM"];
								if(r.data["RYBQDM"]+""=="null"){
									document.getElementById("RYBQDM_"
											+ (i - 4)).innerHTML = '&nbsp;';
								}else{
									document.getElementById("RYBQDM_"
										+ (i - 4)).innerHTML = '&nbsp;'
										+ r.data["RYBQDM"];
								}
							} else if (i == 1) {
								var r = store.getAt(i)
								document.getElementById("MZZD").value = r.data["MSZD"];
								document.getElementById("MZ_JBBM").value = r.data["JBBM"];
							} else if (i == 2) {
								var r = store.getAt(i)
								document.getElementById("BLZD").value = r.data["MSZD"];
								document.getElementById("BL_JBBM").value = r.data["JBBM"];
							} else if (i == 3) {
								var r = store.getAt(i)
								document.getElementById("SSZD").value = r.data["MSZD"];
								document.getElementById("SS_JBBM").value = r.data["JBBM"];
							}
						}
					}
				}
				// if (newTab.id == 'WAR2102') {
				// if (this.midiModules[newTab.id]) {
				// m = this.midiModules[newTab.id];
				// // m.doNew();
				// }
				// }
				if (newTab.__inited) {
					this.fireEvent("tabchange", tabPanel, newTab, curTab);
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
							m.opener = this;
							this.midiModules[newTab.id] = m;
							var p = m.initPanel();
							m.on("save", this.onSuperRefresh, this)
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
						}, this])
			},
			getData : function(entryName) {
				var re = util.schema.loadSync(entryName)
				var schema
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel)
					return;
				}
				var values = {};
				var items = schema.items
				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var v = "";
						if (it.defaultValue) {
							v = it.defaultValue;
						}
						if (this.exContext.BRXX[it.id]
								|| this.exContext.BRXX[it.id] == 0) {
							v = this.exContext.BRXX[it.id];
						}
						if (it.layout) {
							if (it.layout.indexOf("radio") >= 0) {
								var RadioValue = this.getRadioValue(it.id);
								if (RadioValue) {
									v = RadioValue;
								}
//								if ((v == null || v === "") && it["not-null"]) {
//									if (it.type != "string") {
//										this.canSave = true;
//										var index = 1;
//										if (it.layout.substring(0, 4) == "JBXX") {
//											index = 1;
//										} else if (it.layout.substring(0, 4) == "FYTJ") {
//											index = 4;
//										} else if (it.layout.substring(0, 4) == "YSQM") {
//											index = 5;
//										} else if (it.layout.substring(0, 4) == "FJXM") {
//											index = 6;
//										}
//										this.changeTab(index)
//										MyMessageTip.msg("提示", "【" + it.alias
//														+ "】不能为空1!", true);
//										var radioObj = document
//												.getElementsByName(name);
//										if (radioObj.length > 0) {
//											radioObj[0].focus(false, 1000)
//										}
//										return {};
//									}
//								}
							} else {
								var f = document.getElementById(it.layout + "_"
										+ it.id)
								if (f) {
									v = f.value;
								} else {
									f = document.getElementById(it.id)
									if (f) {
										v = f.value;
									}
								}
								if (it.id == 'CSNY' || it.id == 'RYRQ'
										|| it.id == 'CYRQ' || it.id == 'ZKRQ') {
									if (this.midiModules["WAR2102"]) {
										if (it.id == 'CSNY') {
											v = this.midiModules["WAR2102"].CSNY
													.getRawValue();
										}
										if (it.id == 'RYRQ') {
											v = this.midiModules["WAR2102"].RYRQ
													.getValue();
										}
										if (it.id == 'CYRQ') {
											v = this.midiModules["WAR2102"].CYRQ
													.getValue();
										}
									}
									if (this.midiModules["WAR2106"]) {
										if (it.id == 'ZKRQ') {
											v = this.midiModules["WAR2106"].ZKRQ
													.getRawValue();
										}
									}
								}
//								if ((v == null || v === "") && it["not-null"]) {
//									if (it.type != "string") {
//										this.canSave = true;
//										var index = 1;
//										if (it.layout == "JBXX") {
//											index = 1;
//										} else if (it.layout == "FYXX") {
//											index = 4;
//										} else if (it.layout == "YSQM") {
//											index = 5;
//										} else if (it.layout == "FJXM") {
//											index = 6;
//										}
//										this.changeTab(index)
//										MyMessageTip.msg("提示", "【" + it.alias+ "】不能为空2!", true);
//										f.focus(false, 1000)
//										return {};
//									}
//								} else 
								if (it.type == 'int'
										|| it.type == 'long'
										|| it.type == 'double') {
									if (isNaN(v) && v.length >0 ) {
										this.canSave = true;
										var index = 1;
										if (it.layout == "JBXX") {
											index = 1;
										} else if (it.layout == "FYXX") {
											index = 4;
										} else if (it.layout == "YSQM") {
											index = 5;
										} else if (it.layout == "FJXM") {
											index = 6;
										}
										this.changeTab(index)
										MyMessageTip.msg("提示", "【" + it.alias
														+ "】输入错误!", true);
										f.focus(false, 1000)
										return {};
									}
								}
							}
						} else {
							var f = document.getElementById(it.id)
							if (f) {
								v = f.value;
							}
						}
						if (v == null || v === "") {
							if (it["not-null"]) {
//								values[it.id] = "not-null";
								if(it.type == 'string'){
									values[it.id] = "/";
								}else{
									values[it.id] = "";
								}
							} else {
								values[it.id] = "";
							}
						} else {
							values[it.id] = v;
						}
					}
				}
				return values;
			},
			changeTab : function(index) {
				var nextTab = this.tab.items.itemAt(index);
				this.tab.activate(nextTab);
				nextTab.focus(false, 10);
			},
			getListData : function(module, id) {
				if (!module) {
					return;
				}
				var store = module.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (id == "WAR2104") {
						if (r.data.SSXH && r.data.SSDM && r.data.SSMC) {
							if (!r.data.SSBZ) {
								this.canSave = true;
								this.changeTab(3)
								MyMessageTip.msg("提示", '【手术操作标志】不能为空!', true);
								return [];
							}
							if (!r.data.SSJB) {
								this.canSave = true;
								this.changeTab(3)
								MyMessageTip.msg("提示,", '【手术级别】不能为空!', true);
								return [];
							}

							if (!r.data.SSYS) {
								this.canSave = true;
								this.changeTab(3)
								MyMessageTip.msg("提示,", '【手术者签名】不能为空!', true);
								return [];
							}
//							if (!r.data.YZYS) {
//								this.canSave = true;
//								this.changeTab(3)
//								MyMessageTip.msg("提示,", '【I助签名】不能为空!', true);
//								return [];
//							}
//							if (!r.data.EZYS) {
//								this.canSave = true;
//								this.changeTab(3)
//								MyMessageTip.msg("提示,", '【II助签名】不能为空!', true);
//								return [];
//							}
							if (!r.data.QKLB) {
								this.canSave = true;
								this.changeTab(3)
								MyMessageTip.msg("提示,", '【切口类别】不能为空!', true);
								return [];
							}
							if (!r.data.YHDJ) {
								this.canSave = true;
								this.changeTab(3)
								MyMessageTip.msg("提示,", '【愈合等级】不能为空!', true);
								return [];
							}
//							if (!r.data.MZFS) {
//								this.canSave = true;
//								this.changeTab(3)
//								MyMessageTip.msg("提示,", '【麻醉方式】不能为空!', true);
//								return [];
//							}
//							if (!r.data.MZYS) {
//								this.canSave = true;
//								this.changeTab(3)
//								MyMessageTip.msg("提示,", '【麻醉医师签名】不能为空!', true);
//								return [];
//							}
//							if (!r.data.ASA && r.data.ASA != 0) {
//								this.canSave = true;
//								this.changeTab(3)
//								MyMessageTip.msg("提示,", '【ASA分级】不能为空!', true);
//								return [];
//							}
							if (!r.data.SSQCSJ && r.data.SSQCSJ != 0) {
								this.canSave = true;
								this.changeTab(3)
								MyMessageTip.msg("提示,", '【手术全程时间】不能为空!', true);
								return [];
							}
							data.push(r.data)
						}
					} else if ("WAR2103") {
						if (r.data.JBXH && r.data.JBBM && r.data.MSZD) {
							if (!r.data.ZXLB) {
								this.canSave = true;
								this.changeTab(2)
								MyMessageTip.msg("提示", '【中西类别】不能为空!', true);
								return [];
							}
							if (!r.data.ZDYS) {
								this.canSave = true;
								this.changeTab(2)
								MyMessageTip.msg("提示", '【诊断医生】不能为空!', true);
								return [];
							}
							if (!r.data.ZDRQ) {
								this.canSave = true;
								this.changeTab(2)
								MyMessageTip.msg("提示", '【诊断日期】不能为空!', true);
								return [];
							}
							if (r.data.ZDLB == 51) {
								if (!r.data.RYBQDM) {
									this.canSave = true;
									this.changeTab(2)
									MyMessageTip.msg("提示",
											'出院诊断的【入院病情诊断】不能为空!', true);
									return [];
								}
								if (!r.data.CYZGDM) {
									this.canSave = true;
									this.changeTab(2)
									MyMessageTip.msg("提示",
											'出院诊断的【出院转归情况】不能为空!', true);
									return [];
								}
								if (!this.ZZMC) {
									this.ZZMC = r.data.MSZD;
								}
							}
							if (!r.data.JBBW) {
								r.data.JBBW = 0;
							}
							if (!r.data.FJBS) {
								r.data.FJBS = 0;
							}
							if (!r.data.RYBQDM) {
								r.data.RYBQDM = 0;
							}
							if (!r.data.CYZGDM) {
								r.data.CYZGDM = 0;
							}

							data.push(r.data)
						} else if (r.data.ZDLB == 51 && r.data.ZZBZ == 1) {
							this.ZZBZ = true;
						}
					}
				}
				return data
			},
			doSave : function(QMYS) {
				this.ZZMC = "";
				this.ZZBZ = "";
				if (this.midiModules["WAR2102"]) {
					if (!this.midiModules["WAR2102"].CSNY.validateValue()) {
						this.changeTab(1);
						MyMessageTip.msg("提示", '【出生年月】为空或格式错误!', true);
						return;
					}
					if (!this.midiModules["WAR2102"].RYRQ.validateValue()) {
						this.changeTab(1);
						MyMessageTip.msg("提示", '【入院日期】为空或格式错误!', true);
						return;
					}
					if (!this.midiModules["WAR2102"].CYRQ.validateValue()) {
						this.changeTab(1);
						MyMessageTip.msg("提示", '【出院日期】为空或格式错误!', true);
						return;
					}
					var RYRQ = this.midiModules["WAR2102"].RYRQ.getValue();
					var CYRQ = this.midiModules["WAR2102"].CYRQ.getValue();
					if(CYRQ!="" && RYRQ>CYRQ){
						this.changeTab(1);
						MyMessageTip.msg("提示", "出院日期不能小于入院日期！", true);
						return;
					}
				}
				if (this.midiModules["WAR2106"]) {
					if (!this.midiModules["WAR2106"].ZKRQ.validateValue()) {
						this.changeTab(5);
						MyMessageTip.msg("提示", '【质控日期】格式错误!', true);
						return;
					}
				}
				if (this.saving) {
					return;
				}
				var EMR_BASY = this.exContext.BRXX;
				if (QMYS && QMYS.length) {
					EMR_BASY.QMYS = QMYS;
				}
				// if (this.midiModules["WAR2102"]) {
				Ext.apply(EMR_BASY, this.getData("phis.application.emr.schemas.EMR_BASY"));
				if (this.canSave) {
					this.canSave = false;
					return;
				}
				// }
				var EMR_BASY_FY = {};
				// if (this.midiModules["WAR2105"]) {
				//					
				// }
				Ext.apply(EMR_BASY_FY, this.getData("phis.application.emr.schemas.EMR_BASY_FY"));
				if (this.canSave) {
					this.canSave = false;
					return;
				}
				var EMR_ZYSSJL = this.exContext.ZYSSJL;
				if (this.midiModules["WAR2104"]) {
					this.midiModules["WAR2104"].grid.stopEditing()
					EMR_ZYSSJL = this.getListData(this.midiModules["WAR2104"],
							"WAR2104");
					if (this.canSave) {
						this.canSave = false;
						return;
					}
				}
				var EMR_ZYZDJL = this.exContext.ZYZDJL;
				if (this.midiModules["WAR2103"]) {
					this.midiModules["WAR2103"].grid.stopEditing()
					EMR_ZYZDJL = this.getListData(this.midiModules["WAR2103"],
							"WAR2103");
					if (this.canSave) {
						this.canSave = false;
						return;
					}
					if (EMR_ZYZDJL.length > 0 && this.ZZBZ) {
						Ext.MessageBox.alert('提示', '出院诊断【' + this.ZZMC
										+ "】已被设为主诊断!");
					}
				}
				var info = this.getInfo(EMR_BASY.SFZJHM);
				if (info.length != 0) {
					var sex = info[1];
					var birthday = info[0];
					if (EMR_BASY.BRXB != sex) {
						MyMessageTip.msg("提示,", '病人性别和身份证号码不相符!', true);
						return;
					}
					if (EMR_BASY.CSNY != birthday) {
						MyMessageTip.msg("提示,", '出生日期和身份证号码不相符!', true);
						return;
					}
				} else {
					return;
				}
				this.saving = true
				this.tab.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "emrMedicalRecordsService",
							serviceAction : "saveMedicalRecords",
							ZYH : this.exContext.empiData.ZYH,
							EMR_BASY : EMR_BASY,
							EMR_BASY_FY : EMR_BASY_FY,
							EMR_ZYSSJL : EMR_ZYSSJL,
							EMR_ZYZDJL : EMR_ZYZDJL,
							BLLB : this.node.key
						}, function(code, msg, json) {
							debugger;
							this.tab.el.unmask()
							this.saving = false
							if (code > 300) {
								if (QMYS && QMYS.length) {
									this.processReturnMsg(code, msg,
											this.doSave, QMYS);
								} else {
									this.processReturnMsg(code, msg,
											this.doSave);
								}
								return
							}
							if (this.midiModules["WAR2104"]) {
								this.midiModules["WAR2104"].grid.store
										.commitChanges();
								this.midiModules["WAR2104"].refresh();
							}
							if (this.midiModules["WAR2103"]) {
								this.midiModules["WAR2103"].grid.store
										.commitChanges();
								this.midiModules["WAR2103"].refresh();
							}
							this.XGYZ = false;
							if (QMYS && QMYS.length) {
								EMR_BASY.QMYS = null
								if (this.midiModules["WAR2106"]) {
									MyMessageTip.msg("提示", "签名成功!", true);
//									document.getElementById("YSQM_" + QMYS).value = this.mainApp.uid;
//									document.getElementById("YSQM_" + QMYS).disabled = true;
//									// document.getElementById(this.qmId).disabled
//									// = true;
//									document.getElementById("YSQM_ZRYSQM").onclick = null;
//									document.getElementById("YSQM_ZZYSQM").onclick = null;
//									document.getElementById("YSQM_ZYYSQM").onclick = null;
//									this.midiModules["WAR2106"].initSignature();
								}
							} else {
								if(this.NEW){
									this.opener.loadEmrTreeNode();
								}
								this.NEW = 0;
								if(json.body){
									if(json.body.JLXH){
										this.exContext.BRXX.JLXH = json.body.JLXH
									}
								}
								MyMessageTip.msg("提示", "保存成功！", true);
							}
						}, this)// jsonRequest
			},
			doPrint : function() {
				if(this.NEW){
					MyMessageTip.msg("提示", "请先保存后再打印！", true);
					return;
				}
				if (this.contrast() || this.XGYZ) {
					Ext.MessageBox.alert('提示', '病案首页数据已经修改,请保存后在打印!');
				} else {
					var module = this.createModule("basyprint", this.basyprint)
					if (!this.exContext.DYQX) {
						MyMessageTip.msg("打印失败", "无打印权限!", true);
						return;
					}
					module.ZYH = this.exContext.empiData.ZYH;
					module.initPanel();
					module.doPrint();
				}
			},
			doClearSignature : function() {
				var ysqm = ["KZRQM","ZRYSQM","ZZYSQM","ZYYSQM","ZRHSQM","JXYSQM","SXYSQM","BABMYQM","ZKYSQM","ZKHSQM"];
				this.qmys = {
					"KZRQM" : "科主任",
					"ZRYSQM" : "主任（副主任）医师",
					"ZZYSQM" : "主治医师",
					"ZYYSQM" : "住院医师",
					"ZRHSQM" : "责任护士",
					"JXYSQM" : "进修医师",
					"SXYSQM" : "实习医师",
					"BABMYQM" : "病案编码员",
					"ZKYSQM" : "质控医师",
					"ZKHSQM" : "质控护士"
				}
				if (this.midiModules["WAR2106"]) {
					var j = 0;
					for(var i = 0 ; i < ysqm.length ; i ++){
						if(document.getElementById("YSQM_"+ysqm[i]).value){
							j++;
							this.QMYS = ysqm[i];
							this.SYYS = document.getElementById("YSQM_"+ysqm[i]).value;
						}
					}
					if(j==1){
						this.showPSWWIN({QMYS:this.QMYS,SYYS:this.SYYS})
					}else if(j>1){
						this.showQcqmWIN()
					}else{
						MyMessageTip.msg("提示", "没有可清除的签名!", true);
					}
				}else{
					var j = 0;
					for(var i = 0 ; i < ysqm.length ; i ++){
						if(this.exContext.BRXX[ysqm[i]]){
							j++;
							this.QMYS = ysqm[i];
							this.SYYS = this.exContext.BRXX[ysqm[i]]
						}
					}
					if(j==1){
						this.showPSWWIN({QMYS:this.QMYS,SYYS:this.SYYS})
					}else if(j>1){
						this.showQcqmWIN()
					}else{
						MyMessageTip.msg("提示", "没有可清除的签名!", true);
					}
				}
			},
			showQcqmWIN : function(udData) {
				var module = this.createModule("qcqmList", this.refysqmList);
				module.on("commit", this.showPSWWIN, this)
				var win = module.getWin();
				win.add(module.initPanel());
				module.requestData.cnd = ['eq',['$','BLBH'],['i',this.exContext.BRXX.JLXH]]
//				module.requestData.serviceAction = "queryUpdate";
//				module.requestData.ZYH = this.exContext.empiData.ZYH;
//				module.requestData.BASY = this.exContext.BRXX;
//				module.requestData.udData = udData;
				module.refresh();
				win.show();
			},
			showPSWWIN : function(data) {
				this.QMYS = data.QMYS;
				this.SYYS = data.SYYS;
				if (this.midiModules["WAR2106"]) {
					var obj = document.getElementById("YSQM_"+this.QMYS);
					this.YSNAME = obj.options[obj.selectedIndex].text;
				}else{
					this.YSNAME = this.exContext.BRXX[this.QMYS+"_text"]
				}
				if (!this.form) {
					this.form = new Ext.FormPanel({
								frame : true,
								labelWidth : 75,
								labelAlign : 'top',
								defaults : {
									width : '95%'
								},
								defaultType : 'textfield',
								shadow : true,
								items : [{
									
											fieldLabel : '请输入'+this.qmys[this.QMYS]+'【'+this.YSNAME+'】的登录密码',
											name : 'psw',
											inputType : 'password'
										}]
							})
				} else {
//					var form = this.form.getForm()
//					this.psw = form.findField("psw");
					this.psw.setValue();
					var pswField = this.psw.el.parent().parent()
										.first(); // 动态标签2
					pswField.dom.innerHTML = '请输入'+this.qmys[this.QMYS]+'【'+this.YSNAME+'】的登录密码';
				}
				// this.Field.setValue();
				if (!this.chiswin) {
					var win = new Ext.Window({
								layout : "form",
								title : '请输入...',
								width : 300,
								height : 126,
								resizable : true,
								modal : true,
								iconCls : 'x-logon-win',
								constrainHeader : true,
								shim : true,
								// items:this.form,
								buttonAlign : 'center',
								closable : false,
								buttons : [{
											text : '确定',
											handler : this.doQcqm,
											scope : this
										}, {
											text : '取消',
											handler : this.doCancel,
											scope : this
										}]
							})
					this.chiswin = win
					this.chiswin.add(this.form);
				}
				this.chiswin.show();
				var form = this.form.getForm()
				this.psw = form.findField("psw");
				this.psw.focus(false, 50);
			},
			doQcqm : function(){
//				var form = this.form.getForm()
//				this.psw = form.findField("psw");
				var psw = this.psw.getValue();
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrMedicalRecordsService",
							serviceAction : "updateSignature",
							BLBH : this.exContext.BRXX.JLXH,
							ZYH : this.exContext.empiData.ZYH,
							QMYS : this.QMYS,
							uid : this.SYYS,
							psw : psw
						});
				var code = res.code;
				var msg = res.msg;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				if(res.json.body){
					Ext.MessageBox.alert('提示', res.json.body,function(){
						this.psw.focus(true, 50);
					},this);
					return;
				}
				this.dochiswinhide()
				MyMessageTip.msg("提示", "清除签名成功！", true);
				this.exContext.BRXX[this.QMYS] = "";
				if (this.midiModules["WAR2106"]) {
					document.getElementById("YSQM_" + this.QMYS).value = "";
					document.getElementById("YSQM_" + this.QMYS).disabled = false;
					this.midiModules["WAR2106"].exContext.BRXX[this.QMYS] = "";
				}
			},
			doCancel : function(){
				this.dochiswinhide();
			},
			dochiswinhide : function() {
				this.chiswin.hide();
			},
			beforeClose : function() {
				var toolBar = this.tab.getTopToolbar();
				var btn = toolBar.find("cmd", "save");
				btn[0].focus();
				if (this.NEW) {
					if (confirm('病案首页还未保存，是否保存?')) {
						// this.needToClose = true;
						this.doSave()
						return false;
					} else {
						return true;
					}
				} else if (!this.contrast() && !this.XGYZ) {
					return true;
				} else {
					if (confirm('病案首页数据已经修改，是否保存?')) {
						// this.needToClose = true;
						this.doSave()
						return false;
					} else {
						return true;
					}
				}
			},
			contrast : function() {
				if (this.midiModules["WAR2104"]) {
					if (this.midiModules["WAR2104"].grid.store
							.getModifiedRecords().length > 0) {
						return true;
					}
				}
				if (this.midiModules["WAR2103"]) {
					if (this.midiModules["WAR2103"].grid.store
							.getModifiedRecords().length > 0) {
						return true;
					}
				}
				return false;
			},
			checkIdcard : function(pId) {
				var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
				var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
				var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
				if (pId.length != 15 && pId.length != 18) {
					MyMessageTip.msg("提示", "身份证号共有 15 码或18位！", true);
					return "身份证号共有 15 码或18位";
				}
				var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0,
						6)
						+ "19" + pId.slice(6, 16);
				if (!/^\d+$/.test(Ai)) {
					MyMessageTip.msg("提示", "身份证除最后一位外，必须为数字！", true);
					return "身份证除最后一位外，必须为数字！";
				}
				var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
						.slice(12, 14);
				var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
						.getMonth(), day = d.getDate(), now = Date.parseDate(
						this.mainApp.serverDate, "Y-m-d");
				if (year != yyyy || mon + 1 != mm || day != dd || d > now
						|| now.getFullYear() - year > 110
						|| !this.isValidDate(dd, mm, yyyy)) {
					MyMessageTip.msg("提示", "身份证输入错误！", true);
					return "身份证输入错误！";
				}
				for (var i = 0, ret = 0; i < 17; i++) {
					ret += Ai.charAt(i) * Wi[i];
				}
				Ai += arrVerifyCode[ret %= 11];
				if (pId.length == 18 && pId.toLowerCase() != Ai) {
					MyMessageTip.msg("提示", "身份证输入错误！", true);
					return "身份证输入错误！"
				} else {
					return Ai;
				}
			},
			getInfo : function(id) {
				// 根据身份证取 省份,生日，性别
				id = this.checkIdcard(id);
				var fid = id.substring(0, 16), lid = id.substring(17);
				if (isNaN(fid) || (isNaN(lid) && (lid != "x"))) {
					return [];
				}
				var id = String(id), sex = id.slice(14, 17) % 2 ? "1" : "2";
				var birthday = id.slice(6, 10) + "-" + id.slice(10, 12) + "-"
						+ id.slice(12, 14);
				return [birthday, sex];
			},
			isValidDate : function(day, month, year) {
				if (month == 2) {
					var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
					if (day > 29 || (day == 29 && !leap)) {
						return false;
					}
				}
				return true;
			}
		})