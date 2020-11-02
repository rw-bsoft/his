/**
 * 个人基本信息综合模块(外部系统调用)
 * 
 * @author tianj
 */
$package("chis.application.mpi.script");

$import("util.Accredit", "app.modules.form.SimpleFormView",
		"app.modules.form.TableFormView", "app.modules.list.SimpleListView",
		"chis.application.mpi.script.SubTableList", "chis.application.mpi.script.EMPIDemographicInfoForm",
		"app.modules.common", "util.widgets.LookUpField", "chis.script.util.Vtype")

chis.application.mpi.script.EMPIInfoModule2 = function(cfg) {
	this.entryName = "chis.application.mpi.schemas.MPI_DemographicInfo";
	this._schemas = {};
	this._schemas[this.entryName] = {
		title : "基本信息",
		view : "form",
		refresh : true
	};
	this._schemas["MPI_Address"] = {
		title : "其他联系地址",
		view : "list",
		name : "addresses",
		refresh : true
	};
	this._schemas["MPI_Phone"] = {
		title : "其他联系电话",
		view : "list",
		name : "phones",
		refresh : true
	};
	this._schemas["MPI_Certificate"] = {
		title : "其他证件",
		view : "list",
		name : "certificates",
		refresh : true
	};

	this.addEvents({
				"winShow" : true
			});
	this.subLists = [];
	this.registServiceId = "chis.empiService";
	this.width = 820;
	this.modified = false;
	this.addressChange = false;
	this.certificateChange = false;
	this.telephoneChange = false;

	this.on("winShow", this.onWinShow, this);
	this.on("close", this.onWinHide, this);

	if (!cfg.serviceAction) {
		this.serviceAction = "submitPerson";
	} else {
		this.serviceAction = cfg.serviceAction || "updatePerson";
	}

	if (this.serviceAction == "submitPerson") {
		this.op = "create";
	} else {
		this.op = "update";
	}
	this.modal = cfg.modal;
	Ext.apply(this, app.modules.common);
	chis.application.mpi.script.EMPIInfoModule2.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.application.mpi.script.EMPIInfoModule2, app.desktop.Module, {
	initPanel : function(sc) {
		if (this.tab) {
			if (!this.isCombined) {
				this.addPanelToWin();
			}
			return this.tab;
		}
		var schema = sc;
		if (!schema) {
			var re = util.schema.loadSync(this.entryName);
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel);
				return;
			}
		}
		this.schema = schema;
		var defaultWidth = this.fldDefaultWidth || 200;
		var panels = [];
		for (var entry in this._schemas) {
			var sc = this._schemas[entry];
			panels.push({
						border : false,
						frame : false,
						layout : "fit",
						autoWidth : true,
						entry : entry,
						title : sc.title
					});
		}
		var tab = new Ext.TabPanel({
					buttonAlign : "center",
					deferredRender : false,
					border : false,
					frame : false,
					activeTab : 0,
					autoHeight : true,
					items : panels,
					defaults : {
						frame : false,
						autoHeight : true,
						autoWidth : true
					},
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
		tab.on("tabchange", this.onTabActive, this);
		this.tab = tab;
		return tab;
	},

	onTabActive : function(tab, panel) {
		var id = panel.entry;
		sc = this._schemas[id];
		if (!this.midiModules[id]) {
			var entryName = id;
			var subTabView;
			if (sc.view == "form") {
				subTabView = new chis.application.mpi.script.EMPIDemographicInfoForm({
							autoLoadSchema : false,
							autoLoadData : false,
							isCombined : true,
							colCount : 3,
							width : 600,
							labelAlign : "left",
							entryName : entryName,
							actions : [],
							mainApp : this.mainApp
						});
				subTabView.on("gotEmpi", function(empiId) {
							this.serviceAction = "updatePerson"
							this.setRecord(empiId)
						}, this);
				subTabView.on("loadData", function() {
							if (subTabView.serviceAction == "submitPerson") {
								subTabView.snap = subTabView.getQueryInfoSnap();
							}
						}, this), subTabView.on("queryFinished", function() {
							// this.doSave();
						}, this)

			} else if (sc.view == "list") {
				subTabView = new chis.application.mpi.script.SubTableList({
							autoLoadSchema : false,
							entryName : entryName,
							isCombined : true,
							formColCount : 1,
							formLabelAlign : "left",
							autoLoadData : false,
							actions : [{
										name : "新建",
										id : "create"
									}, {
										name : "修改",
										id : "update"
									}, {
										name : "删除",
										id : "remove"
									}],
							enableCnd : false,
							title : sc.title,
							showButtonOnTop : true
						});
				subTabView.on("contentChanged", function(subEntry) {
							this.modified = true;
							if (subEntry == "MPI_Address") {
								this.addressChange = true;
							}
							if (subEntry == "MPI_Certificate") {
								this.certificateChange = true;
							}
							if (subEntry == "MPI_Phone") {
								this.telephoneChange = true;
							}
						}, this);
			}
			panel.add(subTabView.initPanel());
			this.midiModules[id] = subTabView;
			tab.doLayout();
		}// end if

		if (this.empiId && sc.refresh == true) {
			if (sc.view == "list") {
				var initCnd = ['eq', ['$', 'empiId'], ['s', this.empiId]];
				this.midiModules[id].empiId = this.empiId;
				this.midiModules[id].requestData.cnd = initCnd;
				this.midiModules[id].refresh();
			} else if (sc.view = "form") {
				// 为FORM加载数据
				var form = this.midiModules[id];
				form.initDataId = this.empiId;
				form.loadData();
			}
			sc.refresh = false;
		}
	},

	getWin : function() {
		var win = this.win;
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width,
						autoHeight : true,
						iconCls : 'icon-form',
						closeAction : 'hide',
						shim : true,
						layout : "form",
						plain : true,
						autoScroll : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal,
						buttonAlign : 'center',
						items : this.initPanel()
					});

			win.on("show", function() {
						this.fireEvent("winShow");
					}, this);
			win.on("close", function() {
						this.fireEvent("close", this);
					}, this);
			win.on("hide", function() {
						this.fireEvent("close", this);
					}, this);
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
			this.win = win;
		}
		win.instance = this;
		return win;
	},

	doSave : function() {
		var demoInfoView = this.midiModules[this.entryName];
		if (!demoInfoView.validate()) {
			return;
		}
		// 查询未完成。
		if (demoInfoView.serviceAction == "submitPerson"
				&& demoInfoView.needsQuery()) {
			return;
		}
		if (!this.data) {
			this.data = {};
		}
		var values = {};
		var form = demoInfoView.form.getForm();
		for (var i = 0; i < this.schema.items.length; i++) {
			var item = this.schema.items[i];
			var field = form.findField(item.id);
			if (field) {
				var value = field.getValue();
				if (value == null || value == "") {
					if (!item.pkey && item["not-null"]) {
						Ext.Msg.alert("提示", item.alias + "不能为空");
						return;
					}
				}
				if (value && typeof(value) == "string") {
					value = value.trim();
				}
				values[item.id] = value;
			}
		}
		Ext.apply(this.data, values);
		// 如果执行保存，判断内容是否改变。
		if (this.serviceAction == "updatePerson") {
			if (!this.modified && !demoInfoView.needsUpdate()) {
				this.getWin().hide();
				this.fireEvent("onEmpiReturn",
						this.midiModules[this.entryName].data);
				this.openEhrView();
				return;
			}
			this.modified = false;
		}
		var listSc = this.getListTypeSchema();
		for (var sc in listSc) {
			var listView = this.midiModules[sc];
			if (listView) {
				count = listView.store.getCount();
				propName = listSc[sc].name;
				this.data[propName] = [];
				for (var i = 0; i < count; i++) {
					this.data[propName].push(listView.store.getAt(i).data);
				}
			}
		}
		if (this.serviceAction == "updatePerson") {
			this.data["addressChange"] = this.addressChange;
			this.data["certificateChange"] = this.certificateChange;
			this.data["telephoneChange"] = this.telephoneChange;

			this.data["lastModifyUser"] = this.mainApp.uid;
			this.data["lastModifyUnit"] = this.mainApp.deptId;
		} else {
			this.data["createUser"] = this.mainApp.uid;
			this.data["createUnit"] = this.mainApp.deptId;
		}

		var birthDay = this.data["birthday"];
		var personName = this.data["personName"];
		util.rmi.jsonRequest({
					serviceId : this.registServiceId,
					serviceAction : this.serviceAction,
					method:"execute",
					schema : this.entryName,
					body : this.data
				}, function(code, msg, json) {
					this.tab.el.unmask()
					if (code >= 300) {
						this.processReturnMsg(code, msg)
						return;
					}
					this.fireEvent("onSuccessed")
					if (this.serviceAction == "submitPerson") {
						var body = json["body"];
						this.empiId = body["empiId"];
						this.data["empiId"] = this.empiId;
					}
					this.openEhrView();
					this.fireEvent("onEmpiReturn", this.data);
					this.getWin().hide();
				}, this)
	},

	openEhrView : function() {
		var personName = this.data["personName"];
		var birthday = this.data["birthday"];
		var module = this.midiModules["HealthRecord_EHRView"];
		if (!module) {
			$import("chis.script.EHRView");
			var initModules = ['B_01', 'B_02', 'B_03', 'B_04', 'B_05'];
			if (this.checkAge(birthday)) {
				initModules = ['B_01', 'B_02', 'B_03', 'B_04', 'B_05', 'B_06'];
			}
			module = new chis.script.EHRView({
						initModules : initModules,
						empiId : this.empiId,
						closeNav : true,
						mainApp : this.mainApp
					});
			this.midiModules["HealthRecord_EHRView"] = module;
			module.ids["personName"] = personName;
			module.on("save", this.refresh, this);
		} else {
			if (!this.checkAge(birthday) && module.activeModules["B_06"]) {
				module.activeModules["B_06"] = false;
				if (module.mainTab.find("mKey", "B_06").length > 0) {
					module.mainTab
							.remove(module.mainTab.find("mKey", "B_06")[0]);
				}
			} else if (this.checkAge(birthday) && !module.activeModules["B_06"]) {
				if (module.mainTab.find("mKey", "B_06").length == 0) {
					module.activeModules["B_06"] = true;
					module.mainTab.add(module.getModuleCfg("B_06"));
				}
			}

			module.ids = {};
			module.actionName = "EHR_HealthRecord";
			module.ids["empiId"] = empiId;
			module.ids["personName"] = personName;
			module.refresh();
		}
		module.getWin().show();
	},

	checkAge : function(birthDay) {// >65岁return true
		var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
		var birth;
		if ((typeof birthDay == 'object') && birthDay.constructor == Date) {
			birth = birthDay;
		} else {
			birth = Date.parseDate(birthDay, "Y-m-d");
		}
		birth.setYear(birth.getFullYear() + 60);
		if (birth < currDate) {
			return true;
		} else {
			return false;
		}
	},

	setRecord : function(empiId) {
		if (empiId == this.empiId) {
			return;
		}
		this.clear();
		this.empiId = empiId;
		if (!this.data) {
			this.data = {};
		}
		this.data["empiId"] = this.empiId;
		this.serviceAction = "updatePerson";
		this.tab.activate(this.tab.items.itemAt(0));
		// EMPIID改变 ，重新触发tabchange事件。
		this.tab.fireEvent("tabchange", this.tab, this.tab.items.itemAt(0));
	},

	getListTypeSchema : function() {
		var schemas = [];
		for (var sc in this._schemas) {
			if (this._schemas[sc].view == "list") {
				schemas[sc] = this._schemas[sc];
			}
		}
		return schemas
	},

	/**
	 * 保存时判断内容是否已经改变。
	 */
	ifContentChanged : function() {
		var demoInfoForm = this.midiModules[this.entryName];
		var form = demoInfoForm.getForm();
		for (var i = 0; i < this.schema.items.length; i++) {
			var item = this.schema.items[i];
			var field = form.findField(item.id)
			if (field) {
				var value = field.getValue();
				var dataValue = data[item.id]
				if (item.dic) {

				}
			}
		}
	},

	/**
	 * 新建完成或者修改记录改变时，清理对上次操作时保存的临时数据。
	 */
	clear : function() {
		if (this.data){
			this.data = {}
		}
		if (this.midiModules[this.entryName]) {
			var demoInfoForm = this.midiModules[this.entryName];
			demoInfoForm.data = {};
			demoInfoForm.queryInfo = {};
			demoInfoForm.modified = false;
			if (this.serviceAction == "submitPerson"){
				demoInfoForm.addSearchEventListeners();
			} else if (this.serviceAction == "updatePerson"){
				demoInfoForm.removeSearchEventListeners();
			}
			demoInfoForm.serviceAction = this.serviceAction;
			demoInfoForm.doNew();
			var form = demoInfoForm.form.getForm();
			var focusField;
			if (this.serviceAction == "updatePerson") {
				focusField = form.findField("personName");
			} else {
				focusField = form.findField("cardNo");
			}
			form.findField("workCode").enable();
			form.findField("workPlace").enable();
			focusField.focus(true, true);
		}
		var listSc = this.getListTypeSchema();
		for (var sc in listSc) {
			var listView = this.midiModules[sc];
			if (listView) {
				listView.modified = false;
				listView.empiId = null;
				listView.store.removeAll();
			}
		}
		for (var sc in this._schemas) {
			this._schemas[sc].refresh = true;
		}
		this.empiId = null;
		this.addressChange = false;
		this.certificateChange = false;
		this.telephoneChange = false;
	},
	
	onWinShow : function() {
		if (this.op == "create"){
			this.clear();
		}
	},
	
	onWinHide : function() {
		if (this.op == "create"){
			this.serviceAction = "submitPerson";
		}
		if (this.op == "update"){
			this.serviceAction = "updatePerson";
		}

		// 关闭所有的子窗口
		var listSc = this.getListTypeSchema();
		for (var sc in listSc) {
			var listView = this.midiModules[sc];

			if (!listView){
				continue;
			}
			if (!listView.midiModules || listView.midiModules.length == 0){
				continue;
			}

			for (var module in listView.midiModules) {
				listView.midiModules[module].getWin().hide();
			}
		}
	},
	
	close : function() {
		// 判断窗口内容是否有改动。
		if (this.modified) {

		}
	},
	
	alt : function(obj) {
		var res = "";
		for (var i in obj) {
			res += i + ":" + obj[i] + "\n";
		}
		Ext.Msg.alert("信息", res);
	}
});