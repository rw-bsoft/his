$package("phis.application.fsb.script")

$import("phis.script.TabModule")

phis.application.fsb.script.FamilySickBedPatientTab = function(cfg) {
	cfg.width = 700;
	cfg.height = 440;
	cfg.modal = true;
	phis.application.fsb.script.FamilySickBedPatientTab.superclass.constructor
			.apply(this, [cfg])

	this.on("winShow", this.onWinShow, this);
	// this.on("beforeclose", this.beforeClose, this);
	this.on("tabchange", this.onMyTabChange, this);
}
Ext.extend(phis.application.fsb.script.FamilySickBedPatientTab,
		phis.script.TabModule, {
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					if (ac.type == "tab") {
						tabItems.push({
									frame : true,
									layout : "fit",
									title : ac.name,
									exCfg : ac,
									id : ac.id
								})
					}
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
								// autoHeight : true,
								// autoWidth : true
							},
							buttons : this.createMyButtons(),
							buttonAlign : "center",
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.on("afterrender", this.onReady, this)
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			onReady : function() {
				var btnAccessKeys = {};
				var keys = [];
				var el = this.tab.el
				var keyMap = new Ext.KeyMap(el);
				keyMap.stopEvent = true;
				var btns = this.tab.buttons
				if (btns) {
					for (var i = 0; i < btns.length; i++) {
						var btn = btns[i]
						var key = btn.accessKey
						if (key) {
							btnAccessKeys[key] = btn
							keys.push(key)
						}
					}
				}
				this.btnAccessKeys = btnAccessKeys
				keyMap.on(keys, this.onAccessKey, this)
				if (this.win) {
					keyMap.on({
								key : Ext.EventObject.ESC,
								shift : true
							}, this.onEsc, this)
				}
			},
			onAccessKey : function(key, e) {
				e.stopEvent()
				var btn = this.btnAccessKeys[key]
				if (!btn.disabled) {
					if (btn.enableToggle) {
						btn.toggle(!btn.pressed)
					}
					this.doAction(btn)
				}
				var ev = window.event
				try {
					ev.keyCode = 0;
					ev.returnValue = false
					return false
				} catch (e) {
				}
			},
			onWinShow : function() {
				this.tab.setActiveTab(0);
				// 清空上次打开记录
				if (this.midiModules["patientAllergyMedTab"]) {
					this.midiModules["patientAllergyMedTab"].removeRecords = [];
				}
				var tab = this.tab.getActiveTab();
				if (tab && this.initDataId) {
					this.moduleLoadData(tab.id, true);
				}
			},
			onMyTabChange : function(tabPanel, newTab, curTab) {
				this.moduleLoadData(newTab.id);
			},
			moduleLoadData : function(id, reload) {
				var curModule = this.midiModules[id];
				if (curModule) {
					if (curModule.initDataId != this.initDataId || reload) {
						curModule.initDataId = this.initDataId;
						if (curModule.requestData) {
							curModule.initCnd = ['eq', ['$', 'a.ZYH'],
									['d', this.initDataId]];
							if (id == "patientClinicTab") {
								curModule.requestData.cnd = ['eq',
										['$', 'a.ZYH'], ['d', this.initDataId]];
							} else if (id == "patientAllergyMedTab") {
								curModule.requestData.cnd = [
										'eq',
										['$', 'a.BRID'],
										['l', this.exContext.record.get("BRID")]];
							}
						}
						curModule.loadData();
					}
				}
			},
			createMyButtons : function() {
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				var f1 = 112
				var accessI = 0;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.hide) {
						continue
					}
					if (action.type == "button") {
						// ** add by yzh **
						var btnFlag;
						if (action.notReadOnly)
							btnFlag = false
						else
							btnFlag = this.exContext.readOnly || false

						var btn = {
							accessKey : f1 + accessI,
							text : action.name + "(F" + (accessI + 1) + ")",
							ref : action.ref,
							target : action.target,
							cmd : action.delegate || action.id,
							iconCls : action.iconCls || action.id,
							enableToggle : (action.toggle == "true"),
							scale : action.scale || "small",
							// ** add by yzh **
							disabled : btnFlag,
							notReadOnly : action.notReadOnly,
							script : action.script,
							handler : this.doAction,
							scope : this
						}
						buttons.push(btn)
						accessI++;
					}
				}
				return buttons

			},
			doSave : function() {
				// 保存病人信息
				var data = {};
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					if (ac.type == "tab") {
						var module = this.midiModules[ac.id];
						if (module) {
							var ret = module.getSaveData();
							if (!ret)
								return;
							data[ac.id] = ret;
						}
					}
				}
				this.tab.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "familySickBedManageService",
							serviceAction : "savePatientInfo",
							ZYH : this.initDataId,
							body : data
						}, function(code, msg, json) {
							this.tab.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (this.midiModules["patientClinicTab"]) {
								this.midiModules["patientClinicTab"].removeRecords = [];
								this.midiModules["patientClinicTab"].loadData();
							}
							if (this.midiModules["patientAllergyMedTab"]) {
								this.midiModules["patientAllergyMedTab"].removeRecords = [];
								this.midiModules["patientAllergyMedTab"]
										.loadData();
							}
							MyMessageTip.msg("提示", "保存成功!", true)
							// if (this.opener) {
							// this.opener.doRefresh();
							// }
						}, this)

			},
			beforeClose : function() {
				// 判断是否有修改
			},
			doClose : function() {
				this.win.hide();
			}
		});
