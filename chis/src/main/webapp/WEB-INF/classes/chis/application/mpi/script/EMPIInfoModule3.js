/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("chis.application.mpi.script");

$import("util.Accredit", "app.modules.form.SimpleFormView",
		"app.modules.form.TableFormView", "app.modules.list.SimpleListView",
		"chis.application.mpi.script.SubTableList", "chis.application.mpi.script.SubTableEditList",
		"chis.application.mpi.script.EMPIDemographicInfoForm", "app.modules.common",
		"util.widgets.LookUpField", "chis.script.util.Vtype");

chis.application.mpi.script.EMPIInfoModule3 = function(cfg) {
	this.entryName = "chis.application.mpi.schemas.MPI_Card";
	this._schemas = {};
//	this._schemas[this.entryName] = {
//		title : "基本信息",
//		view : "form",
//		refresh : true
//	};
//	this._schemas["chis.application.mpi.schemas.MPI_Address"] = {
//		title : "其他联系地址",
//		view : "list",
//		name : "addresses",
//		refresh : true
//	};
//	this._schemas["chis.application.mpi.schemas.MPI_Phone"] = {
//		title : "其他联系电话",
//		view : "list",
//		name : "phones",
//		refresh : true
//	};
//	this._schemas["chis.application.mpi.schemas.MPI_Certificate"] = {
//		title : "其他证件",
//		view : "list",
//		name : "certificates",
//		refresh : true
//	};
	this._schemas["chis.application.mpi.schemas.MPI_Card"] = {
		title : "卡管理",
		view : "list",
		name : "cards",
		refresh : true
	};
//	this._schemas["chis.application.mpi.schemas.MPI_Extension"] = {
//		title : "其他属性",
//		view : "editList",
//		name : "extensions",
//		refresh : true
//	};

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
	this.extensionChange = false;
	this.cardChange = false;

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
	chis.application.mpi.script.EMPIInfoModule3.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.mpi.script.EMPIInfoModule3, app.desktop.Module, {
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
								handler : this.onBeforeSave,
								scope : this
							}, {
								xtype : "button",
								text : "取消",
								handler : function() {
									this.getWin().hide()
								},
								scope : this
							}]
				});
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
							this.serviceAction = "updatePerson";
							this.setRecord(empiId);
						}, this);
				subTabView.on("loadFormData", function(value) {
							this.idCardValue = value;
						}, this);
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
							if (subEntry == "chis.application.mpi.schemas.MPI_Address") {
								this.addressChange = true;
							}
							if (subEntry == "chis.application.mpi.schemas.MPI_Certificate") {
								this.certificateChange = true;
							}
							if (subEntry == "chis.application.mpi.schemas.MPI_Phone") {
								this.telephoneChange = true;
							}
							if (subEntry == "chis.application.mpi.schemas.MPI_Card") {
								this.cardChange = true;
							}
						}, this);
			} else if (sc.view == "editList") {
				subTabView = new chis.application.mpi.script.SubTableEditList({
							autoLoadSchema : false,
							entryName : entryName,
							isCombined : true,
							formColCount : 1,
							formLabelAlign : "left",
							autoLoadData : false,
							enableCnd : false,
							title : sc.title,
							showButtonOnTop : true
						});
				subTabView.on("contentChanged", function() {
							this.extensionChange = true;
						}, this);
			}

			panel.add(subTabView.initPanel());
			this.midiModules[id] = subTabView;
			tab.doLayout();
		}// end if
		if (this.empiId && sc.refresh == true) {
			if (sc.view == "list" || sc.view == "editList") {
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
						constrain : true,
						buttonAlign : 'center',
						items : this.initPanel()
					});

			win.on("show", function(win) {
						win.center();
						this.fireEvent("winShow");
					}, this);
			win.on("close", function() {
						this.fireEvent("close", this);
					}, this);
			win.on("hide", function() {
						this.fireEvent("close", this)
					}, this);
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
		}
		win.instance = this;
		this.win = win;
		return win;
	},

	onBeforeSave : function() {
		
		// 判断是否需要提示加卡纳入管理
		var demoInfoView = this.midiModules[this.entryName];
		
		if (!demoInfoView.validate()) {
			return;
		}
		var demoSnap = demoInfoView.snap;
		var form = demoInfoView.form.getForm();

		var cardNo = form.findField("cardNo").getValue();

		if ((cardNo.trim().length != 0)) {

			// 提示是否纳入卡管理。
			Ext.Msg.show({
						title : '新卡提示',
						msg : '是否将新卡纳入管理?',
						buttons : Ext.Msg.YESNO,
						fn : function(btn, text) {
							if (btn == "no") {
								this.doSave();
								return;
							}
							var cardForm = this.midiModules["cardForm"];
							if (!cardForm) {
								cardForm = new chis.application.mpi.script.SubTableForm({
											entryName : "chis.application.mpi.schemas.MPI_Card",
											isCombined : false,
											title : "新增卡",
											width : 300,
											op : "create",
											colCount : 1,// 在LIST窗口控制新建、修改窗口打开时的列数
											labelAlign : "left" // 在LIST窗口控制新建、修改窗口打开时的文字对齐方式
										});
								cardForm.on("save", function() {
											this.cardChange = true;
											this.doSave();
										}, this)
								cardForm.on("cancel", function() {
											this.doSave();
										}, this)
								this.midiModules["cardForm"] = cardForm;
								
							}
							cardForm.empiId = this.empiId;
							var cardListTab = this.tab.items.itemAt(4);
							this.tab.activate(cardListTab);
							var CardList = this.midiModules["chis.application.mpi.schemas.MPI_Card"];
							cardForm.dataChanged = false;
							cardForm.store = CardList.store;
							cardForm.getWin().show();
							cardForm.initFormData({
										cardNo : cardNo
									})
						},
						scope : this,
						animEl : 'elId',
						icon : Ext.MessageBox.QUESTION
					});
		} else {
			this.doSave();
		}
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
		var demoInfoView = this.midiModules[this.entryName];
		// 如果执行保存，判断内容是否改变。
		if (this.serviceAction == "updatePerson") {
			if (!this.modified && !demoInfoView.needsUpdate()) {
				this.getWin().hide();
				this.fireEvent("onEmpiReturn",
						this.midiModules[this.entryName].data);
				return;
			}
			this.modified = false;
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
			} else if (demoInfoView.data[item.id]) {
				if (item.dic) {
					values[item.id] = demoInfoView.data[item.id].key;
				} else {
					values[item.id] = demoInfoView.data[item.id];
				}
			}
		}
		values["cardNo"] = "";
		// 更新个人姓名服务
		if (this.serviceAction == "updatePerson"
				&& demoInfoView.needsUpdateName()) {
			this.tab.el.mask("正在更新姓名..");
			var result = util.rmi.miniJsonRequestSync({
						serviceId : 'chis.healthRecordService',
						serviceAction : 'updatePersonName',
						method:"execute",
						body : {
							personName : values["personName"],
							empiId : demoInfoView.data["empiId"]
						}
					})
			this.tab.el.unmask();
			if (result.code > 300) {
				this.processReturnMsg(result.code, result.msg);
				return;
			}
		}

		Ext.apply(this.data, values);
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
			this.data["extensionChange"] = this.extensionChange;
			this.data["cardChange"] = this.cardChange;
			this.data["lastModifyUser"] = this.mainApp.uid;
			this.data["lastModifyUnit"] = this.mainApp.deptId;
		} else {
			this.data["createUser"] = this.mainApp.uid;
			this.data["createUnit"] = this.mainApp.deptId;
		}
		if ("noValue" == this.idCardValue) {
			this.data.noIdCard = true;
		}
		this.tab.el.mask("正在执行操作...");
		util.rmi.jsonRequest({
					serviceId : this.registServiceId,
					serviceAction : this.serviceAction,
					method:"execute",
					schema : this.entryName,
					body : this.data
				}, function(code, msg, json) {
					this.tab.el.unmask();
					if (code >= 300 && code != 4000) {
						this.processReturnMsg(code, msg);
						return;
					}
					if (code == 4000) {
						Ext.Msg.alert("提示", msg, function() {
									this.fireEvent("onSuccessed");
									var body = json["body"];
									if (this.serviceAction == "submitPerson") {
										this.empiId = body["empiId"];
										this.data["empiId"] = this.empiId;
									}
									demoInfoView.data = values;
									this.data.visitPlanChanged = body.visitPlanChanged;
									this.fireEvent("onEmpiReturn", this.data);
									this.getWin().hide();
								}, this);
					} else {
						this.fireEvent("onSuccessed");
						var body = json["body"];
						if (this.serviceAction == "submitPerson") {
							this.empiId = body["empiId"];
							this.data["empiId"] = this.empiId;
						}
						demoInfoView.data = values;
						this.data.visitPlanChanged = body.visitPlanChanged;
						this.fireEvent("onEmpiReturn", this.data);
						this.getWin().hide();
					}
				}, this)
	},

	setRecord : function(empiId, topClick) {
		if (topClick) {
			this.midiModules[this.entryName].topClick = topClick;
		}
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

	// 外部接口通过empiId调用，empiId数据可能来自平台，该方法会从平台调入一些数据初始化。
	interfaceInvoke : function(empiId) {
		this.clear();
		if (!this.data) {
			this.data = {};
		}

		util.rmi.jsonRequest({
					serviceId : "chis.empiService",
					schema : "MPI_DemographicInfo",
					serviceAction : "queryByEmpiId",
					method:"execute",
					body : {
						empiId : empiId
					}
				}, function(code, msg, json) {
					if (code == 403) {
						this.processReturnMsg(result.code, result.msg);
						return;
					}
					var data = json["body"];
					if (!data || data.length == 0)
						return;
					this.dataSource = json.dataSource || "chis";
					var empiInfoView = this.midiModules[this.entryName];
					if (data.length == 1) {
						// 如果数据是从pix服务器取得,只作为默认值填入。
						if (this.dataSource == "pix") {
							empiInfoView.setDefaultData(data[0]);
							empiInfoView.data["empiId"] = empiId;
							return;
						}
					}

					this.serviceAction = "updatePerson";
					this.tab.activate(this.tab.items.itemAt(0));
					// EMPIID改变 ，重新触发tabchange事件。
					this.tab.fireEvent("tabchange", this.tab, this.tab.items
									.itemAt(0));
				}, this)
	},

	getListTypeSchema : function() {
		var schemas = [];
		for (var sc in this._schemas) {
			if (this._schemas[sc].view == "list"
					|| this._schemas[sc].view == "editList") {
				schemas[sc] = this._schemas[sc];
			}
		}
		return schemas;
	},

	/**
	 * 保存时判断内容是否已经改变。
	 */
	ifContentChanged : function() {
		var demoInfoForm = this.midiModules[this.entryName];
		var form = demoInfoForm.getForm();
		for (var i = 0; i < this.schema.items.length; i++) {
			var item = this.schema.items[i];
			var field = form.findField(item.id);
			if (field) {
				var value = field.getValue();
				var dataValue = data[item.id];
				if (item.dic) {

				}
			}
		}
	},

	/**
	 * 新建完成或者修改记录改变时，清理对上次操作时保存的临时数据。
	 */
	clear : function(clearSnap) {
		if (this.data) {
			this.data = {};
		}
		if (this.midiModules[this.entryName]) {
			var demoInfoForm = this.midiModules[this.entryName];
			demoInfoForm.data = {};
			demoInfoForm.queryInfo = {};
			demoInfoForm.snap = {};
			demoInfoForm.queryBy = false;
			demoInfoForm.modified = false;
			if (this.serviceAction == "submitPerson") {
				demoInfoForm.addSearchEventListeners();
			} else if (this.serviceAction == "updatePerson") {
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
		this.cardChange = false;
		this.tab.activate(this.tab.items.itemAt(0));
		var extensionList = this.midiModules["chis.application.mpi.schemas.MPI_Extension"];
		if (extensionList) {
			extensionList.fillStore();
		}
	},

	onWinShow : function() {
		if (this.op == "create") {
			this.clear();
			this.midiModules[this.entryName].form.getForm().findField("idCard")
					.enable();
			this.midiModules[this.entryName].form.getForm()
					.findField("birthday").enable();
			this.midiModules[this.entryName].form.getForm()
					.findField("sexCode").enable();
		}
	},

	onWinHide : function() {
		if (this.op == "create") {
			this.serviceAction = "submitPerson";
		}
		if (this.op == "update") {
			this.serviceAction = "updatePerson";
		}

		// 关闭所有的子窗口
		var listSc = this.getListTypeSchema();
		for (var sc in listSc) {
			var listView = this.midiModules[sc];
			if (!listView) {
				continue;
			}
			if (!listView.midiModules || listView.midiModules.length == 0) {
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
		alert(res);
	}
});