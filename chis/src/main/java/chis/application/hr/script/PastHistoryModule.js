/**
 * 新建个人既往史记录模块
 * 
 * @author : tianj
 */
$package("chis.application.hr.script")

$import("util.Accredit", "app.modules.form.TableFormView")

chis.application.hr.script.PastHistoryModule = function(cfg) {
	cfg.labelAlign = "left";
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 249;
	cfg.colCount = 2;
	cfg.width = 880;
	chis.application.hr.script.PastHistoryModule.superclass.constructor.apply(this, [cfg]);
	this.on("close", this.onClose, this);
	this.on("winShow", this.onWinShow, this);
}

Ext.extend(chis.application.hr.script.PastHistoryModule, app.desktop.Module, {
	initPanel : function() {
		var tf = util.dictionary.TreeDicFactory.createDic({
					id : "chis.dictionary.pastHistory",
					onlySelectLeaf : "true"
				});
		tf.tree.expandAll();
		var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					width : this.width,
					height : this.height,
					items : [{
								layout : "fit",
								split : true,
								collapsible : true,
								title : '',
								region : 'west',
								width : this.westWidth || 150,
								items : tf.tree
							}, {
								layout : "fit",
								split : true,
								title : '',
								region : 'center',
								items : this.getMainPanel()
							}]
				});
		tf.tree.on("click", this.onTabActive, this);
		this.panel = panel;
		this.tree = tf.tree;
		return panel;
	},

	getWin : function() {
		var win = this.win;
		var closeAction = "hide";
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width || 800,
						height : this.height || 450,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : true,
						constrain : true,
						maximizable : true,
						shadow : false,
						modal : true,
						items : this.initPanel()
					});
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
			win.on("show", function() {
						this.win.doLayout();
						this.fireEvent("winShow", this);
					}, this);
			win.on("close", function() {
						this.fireEvent("close", this);
					}, this);
			win.on("hide", function() {
						this.fireEvent("close", this);
					}, this);
			this.win = win;
		}
		win.instance = this;
		return win;
	},

	getMainPanel : function() {
		var mainPanel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					width : this.width,
					height : this.height,
					items : [{
								layout : "fit",
								split : true,
								collapsible : true,
								title : '',
								region : 'north',
								height : 150,
								width : this.width,
								items : this.getForm()
							}, {
								layout : "fit",
								split : true,
								title : '',
								region : 'center',
								width : this.width,
								items : this.getGrid()
							}]
				});
		this.mainPanel = mainPanel;
		return mainPanel;
	},

	getForm : function() {
		var form = this.midiModules["formView"];
		if (!form) {
			$import("chis.application.hr.script.PastHistoryForm");
			form = new chis.application.hr.script.PastHistoryForm({
						isCombined : true,
						entryName : this.entryName,
						empiId : this.empiId,
						actions : []
					});
			this.midiModules["formView"] = form;
		} else {
			form.empiId = this.empiId;
		}
		this.form = form;
		form.on("cancel", this.onCancel, this);
		return form.initPanel();
	},

	getGrid : function() {
		var list = this.midiModules["listView"];
		if (!list) {
			$import("chis.application.hr.script.PastHistoryCreateList");
			list = new chis.application.hr.script.PastHistoryCreateList({
						mainApp : this.mainApp,
						entryName : this.entryName,
						empiId : this.empiId,
						saveServiceId : this.saveServiceId,
						saveAction : this.saveAction,
						disablePagingTbr : true,
						autoLoadData : false,
						showButtonOnTop : true,
						mutiSelect : true,
						enableCnd : false
					});
			this.midiModules["listView"] = list;
		} else {
			list.empiId = this.empiId;
		}
		list.delPast = [];
		this.list = list;
		this.grid = list.initPanel();
		this.list.on("beforeSave", this.onBeforeSave, this);
		this.list.on("afterSave", this.onAfterSave, this);
		this.list.on("chekExists", this.onChekExists, this);
		this.list.on("checkHasThis", this.onChekHasThis, this);
		this.list.on("beforeModify", this.onBeforeModify, this);
		this.list.on("chekTypeExists", this.onChekTypeExists, this);
		this.list.on("cancel", this.onCancel, this);
		this.list.on("save", this.onListSave, this);
		return this.grid;
	},

	onTabActive : function(node, e) {
		if (!node.leaf || node.parentNode == null) {
			return;
		}
		var id = node.id;
		var text = node.text;
		var parentId = node.parentNode.id;
		var parentText = node.parentNode.text;
		this.form.data = {};
		var form = this.form.form.getForm();
		var pastHisTypeCode = form.findField("pastHisTypeCode");
		var diseaseText = form.findField("diseaseText");
		if (pastHisTypeCode) {
			pastHisTypeCode.setValue({
						key : parentId,
						text : parentText
					});
			this.form.data["pastHisTypeCode"] = parentId;
			pastHisTypeCode.disable();
		}
		var protect = form.findField("protect");
		if (parentId == "12" && id != "1201") {
			protect.enable();
		} else {
			protect.disable();
		}
		this.form.data["diseaseCode"] = id;
		if (diseaseText) {
			if (text.indexOf("其他") > -1 || text.indexOf("有") > -1
					|| text.indexOf("恶性肿瘤") > -1 || id == "0212") {
				diseaseText.setValue("");
				this.form.data["diseaseText"] = "";
				diseaseText.enable();
			} else {
				diseaseText.setValue(text);
				diseaseText.disable();
				this.form.data["diseaseText"] = text;
			}
			diseaseText.validate();
		}
	},

	doCreate : function() {
		this.form.empiId = this.empiId;
		this.list.empiId = this.empiId;
		this.list.delPast = [];

		util.rmi.jsonRequest({
					serviceId : 'chis.simpleQuery',
					method:"execute",
					schema : "chis.application.mpi.schemas.MPI_DemographicInfo",
					cnd : ['eq', ['$', 'a.empiId'], ['s', this.empiId]]
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					if (json.body) {
						if (json.body.length != 0) {
							var data = json.body[0];
							this.maritalStatusCode = data.maritalStatusCode;
							this.list.maritalStatusCode = this.maritalStatusCode;
							// 根据婚姻状况过滤既往史树
							this.tree.filter.filterBy(this.filterTree, this);
						}
					}
				}, this)

		this.form.doNew();
	},

	filterTree : function(node) {
		if (!node || !this.maritalStatusCode) {
			return true;
		}
		var key = node.attributes["key"];
		// 婚姻状况为未婚时既往史类别为家族疾病史-子女不显示
		if (key == "10" && this.maritalStatusCode == "10") {
			return false;
		} else {
			return true;
		}
	},

	onChekExists : function(checkMessage, pastHisTypeCode, diseaseCode) {
		this.fireEvent("checkRecord", checkMessage, pastHisTypeCode,
				diseaseCode);
	},

	onChekHasThis : function(diseaseText, pastHisTypeCode) {
		return this.fireEvent("checkHasRecord", diseaseText, pastHisTypeCode);
	},

	onChekTypeExists : function(pastHisTypeCode) {
		return this.fireEvent("checkHasType", pastHisTypeCode);
	},

	onBeforeSave : function(values) {
		this.form.getFormData(values)
	},

	onBeforeModify : function(data) {
		this.form.initFormData(data);
	},

	onAfterSave : function() {
		this.form.doNew();
	},

	onCancel : function(delPastId) {
		this.getWin().hide();
	},

	onListSave : function(entryName, op, json, data) {
		this.fireEvent("save", entryName, op, json, data);
		this.getWin().hide();
	},

	onClose : function() {
		this.form.doNew();
		this.list.store.removeAll();
	},
	onWinShow:function(){
		this.list.empiId = this.empiId
		this.form.empiId = this.empiId
	}
	,
	doCreate : function(){}
});