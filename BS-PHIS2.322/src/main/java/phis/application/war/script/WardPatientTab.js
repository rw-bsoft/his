$package("phis.application.war.script")

$import("phis.script.TabModule")

phis.application.war.script.WardPatientTab = function(cfg) {
	cfg.width = 700;
	cfg.height = 440;
	cfg.modal = true;
	phis.application.war.script.WardPatientTab.superclass.constructor.apply(
			this, [cfg])

	this.on("winShow", this.onWinShow, this);
	// this.on("beforeclose", this.beforeClose, this);
	this.on("tabchange", this.onMyTabChange, this);
}
Ext.extend(phis.application.war.script.WardPatientTab, phis.script.TabModule, {
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
		if (this.midiModules[(this.openBy == "doctor" ? "doc_" : "")
				+ "patientClinicTab"]) {
			this.midiModules[(this.openBy == "doctor" ? "doc_" : "")
					+ "patientClinicTab"].removeRecords = [];
		}
		if (this.midiModules[(this.openBy == "doctor" ? "doc_" : "")
				+ "patientAllergyMedTab"]) {
			this.midiModules[(this.openBy == "doctor" ? "doc_" : "")
					+ "patientAllergyMedTab"].removeRecords = [];
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
					if (id == "patientClinicTab"
							|| id == "doc_patientClinicTab") {
						curModule.ZYH = this.initDataId;
						if (curModule.requestData) {
							curModule.initCnd = ['eq', ['$', 'a.ZYH'],
									['d', this.initDataId]];
							curModule.requestData.cnd = ['eq', ['$', 'a.ZYH'],
									['d', this.initDataId]];
						}
					} else {
						var brid = "";
						if (this.exContext.record) {
							brid = this.exContext.record.get("BRID");
						} else {
							brid = this.exContext.ids.brid;
						}
						curModule.initCnd = [
								'and',
								['eq', ['$', 'a.BRID'], ['l', brid]],
								['eq', ['$', 'a.JGID'],
										['l', this.mainApp.deptId]]];

						curModule.requestData.cnd = [
								'and',
								['eq', ['$', 'a.BRID'], ['l', brid]],
								['eq', ['$', 'a.JGID'],
										['l', this.mainApp.deptId]]];
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
					serviceId : "wardPatientManageService",
					serviceAction : "savePatientInfo",
					ZYH : this.initDataId,
					openBy : this.openBy,
					body : data
				}, function(code, msg, json) {
					this.tab.el.unmask();
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					if (json.body) {
						if (json.body.JBPB) {
							if (json.body.JBPB.indexOf('01') >= 0) {
								MyMessageTip.msg("提示", "请完善高血压相关业务", true);
							}
							if (json.body.JBPB.indexOf('02') >= 0) {
								MyMessageTip.msg("提示", "请完善糖尿病相关业务", true);
							}
						}
						if (json.body.JBBGK) {
							if (json.body.JBBGK.indexOf("06") >= 0) {
								MyMessageTip.msg("提示", "请完善疾病报告卡相关业务", true);
							}
						}
					}
					if (this.midiModules[(this.openBy == "doctor" ? "doc_" : "")
							+ "patientClinicTab"]) {
						this.midiModules[(this.openBy == "doctor" ? "doc_" : "")
								+ "patientClinicTab"].removeRecords = [];
						this.midiModules[(this.openBy == "doctor" ? "doc_" : "")
								+ "patientClinicTab"].loadData();
					}
					if (this.midiModules[(this.openBy == "doctor" ? "doc_" : "")
							+ "patientAllergyMedTab"]) {
						this.midiModules[(this.openBy == "doctor" ? "doc_" : "")
								+ "patientAllergyMedTab"].removeRecords = [];
						this.midiModules[(this.openBy == "doctor" ? "doc_" : "")
								+ "patientAllergyMedTab"].loadData();
					}
					MyMessageTip.msg("提示", "保存成功!", true)
					if (this.opener) {
						this.opener.doRefresh();
					}
				}, this)

		// 与公卫业务联动开始，过敏药品添加到个人既往史中
		if (!this.SFQYGWXT) {
			var publicParam = {
				"commons" : ['SFQYGWXT']
			}
			var SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
		}
		// 如果存在公卫系统，并且皮试结果是阳性则保存到公卫个人既往史中
		if (SFQYGWXT == '1' && this.mainApp.chisActive) {
			var brempiid = this.exContext.record.data.EMPIID;
			var nowDate = (new Date()).format('Y-m-d');
			var gmywlbs = {
				'1' : '0102',
				'2' : '0103',
				'3' : '0104'
			}
			var jblbs = {
				'01' : '0202',
				'02' : '0203',
				'09' : '0298',
				'10' : '0204',
				'11' : '0205',
				'12' : '0206',
				'13' : '0207',
				'14' : '0208',
				'15' : '0209',
				'16' : '0210',
				'17' : '0211',
				'18' : '0212',
				'19' : '0213',
				'20' : '0214'
			}
			// 删除本人当天操作的个人既往史中 过敏药物和诊断

			var delRecords = [];
			if (data.patientAllergyMedTab) {
				delRecords.push({
							empiId : brempiid,
							pastHisTypeCode : '01',
							ysid : this.mainApp.uid
						});
			}
			if (data.patientClinicTab) {
				delRecords.push({
							empiId : brempiid,
							pastHisTypeCode : '02',
							ysid : this.mainApp.uid
						});

			}
			if (delRecords.length > 0) {
				var comreq1 = util.rmi.miniJsonRequestSync({
							serviceId : "chis.CommonService",
							serviceAction : "delPastHistory",
							body : {
								empiId : brempiid,
								record : delRecords
							}
						});
				if (comreq1.code != 200) {
					this.processReturnMsg(comreq1.code, comreq1.msg);
					return;
				} else {
				}
			}

			var historyRecords = [];
			var pasthistory = {};
			// 循环过敏药品
			var meds = data.patientAllergyMedTab;
			if (meds) {
				for (var i = 0; i < meds.length; i++) {
					var gmywlbtext = '其他';
					var med = meds[i];
					if (med.GMYWLB && med._opStatus != 'remove') {
						if (gmywlbs.hasOwnProperty(med.GMYWLB))
							gmywlb = gmywlbs[med.GMYWLB];
						if (med.GMYWLB_text) {
							gmywlbtext = med.GMYWLB_text;
						}
						var gmRecord = {
							empiId : brempiid,
							pastHisTypeCode_text : '药物过敏史',
							pastHisTypeCode : '01',
							methodsCode : '',
							protect : '',
							diseaseCode : gmywlb,
							diseaseText : gmywlbtext,
							vestingCode : '',
							startDate : '',
							endDate : '',
							confirmDate : nowDate,
							recordUnit : this.mainApp.deptId,
							recordUser : this.mainApp.uid,
							recordDate : nowDate,
							lastModifyUser : this.mainApp.uid,
							lastModifyUnit : this.mainApp.deptId,
							lastModifyDate : nowDate
						};
						if (!pasthistory.hasOwnProperty(gmywlb)) {
							historyRecords.push(gmRecord);
							pasthistory[gmywlb] = gmywlb;
						}

					}

				};
			}

			// 循环疾病史
			var jbss = data.patientClinicTab;
			if (jbss) {
				for (var k = 0; k < jbss.length; k++) {

					var jbs = jbss[k];
					// jbs可能存在多选的情况比如'01,02','高血压,糖尿病';
					if (jbs.JBPB && jbs._opStatus != 'remove') {
						var jbbms = jbs.JBPB.split(',');
						var jbmcs = jbs.JBPB_text.split(',');
						for (var i = 0; i < jbbms.length; i++) {
							var jbslb = '0299';
							var jbstext = '其他';
							if (jblbs.hasOwnProperty(jbbms[i]))
								jbslb = jblbs[jbbms[i]];
							if (jbmcs[i]) {
								jbstext = jbmcs[i];
							}

							var jbRecord = {
								empiId : brempiid,
								pastHisTypeCode_text : '疾病史',
								pastHisTypeCode : '02',
								methodsCode : '',
								protect : '',
								diseaseCode : jbslb,
								diseaseText : jbstext,
								vestingCode : '',
								startDate : '',
								endDate : '',
								confirmDate : nowDate,
								recordUnit : this.mainApp.deptId,
								recordUser : this.mainApp.uid,
								recordDate : nowDate,
								lastModifyUser : this.mainApp.uid,
								lastModifyUnit : this.mainApp.deptId,
								lastModifyDate : nowDate
							}
							if (!pasthistory.hasOwnProperty(jbslb)) {
								historyRecords.push(jbRecord);
								pasthistory[jbslb] = jbslb;
							}

						}

					}

				}
			}
			var comreq1 = util.rmi.miniJsonRequestSync({
						serviceId : "chis.healthRecordService",
						serviceAction : "savePastHistoryHis",
						schema : 'chis.application.hr.schemas.EHR_PastHistory',
						op : 'create',
						body : {
							empiId : brempiid,
							record : historyRecords,
							delPastId : []
						}

					});
			if (comreq1.code != 200) {
				this.processReturnMsg(comreq1.code, comreq1.msg);
				return;
			} else {
			}
		}
		// 与公卫业务联动结束
	},
	beforeClose : function() {
		// 判断是否有修改
	},
	doClose : function() {
		this.win.hide();
	}
});
