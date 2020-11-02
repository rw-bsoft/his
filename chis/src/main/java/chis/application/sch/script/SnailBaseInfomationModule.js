$package("chis.application.sch.script.sch");

$import("chis.script.BizModule", "chis.application.sch.script.SnailFindInfomationListView",
		"chis.application.sch.script.SnailKillInfomationListView",
		"chis.application.sch.script.SnailBaseInfomationFormView");

chis.application.sch.script.SnailBaseInfomationModule = function(cfg) {
	cfg.height = 378;
	cfg.width = Ext.getBody().getWidth()-200;
	cfg.itemWidth = 600;
	cfg.itemHeight = 197;
	chis.application.sch.script.SnailBaseInfomationModule.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.sch.script.SnailBaseInfomationModule, chis.script.BizModule, {

	onWinShow : function() {
		if (this.op == "create") {
			this.centerPanel.disable();
			this.midiModules["snailBaseInfomationForm"].doCreate();
			this.midiModules["snailFindInfomationList"].clear();
			this.midiModules["snailKillInfomationList"].clear();
			this.midiModules["snailFindInfomationList"].resetCnd();
			this.midiModules["snailKillInfomationList"].resetCnd();
		}
	},

	initPanel : function() {
		var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					width : this.width,
					height : this.height,
					items : [{
								layout : "fit",
								split : true,
								collapsible : false,
								title : '',
								region : 'north',
								height : this.itemHeight,
								width : this.itemHeight,
								items : this.getSnailBaseInfomation()
							}, {
								layout : "fit",
								split : true,
								collapsible : false,
								title : '',
								region : 'center',
								items : this.getCenterPanel()
							}]
				});
		this.panel = panel
		return this.panel
	},

	getCenterPanel : function() {
		var centerPanel = this.centerPanel;
		if (centerPanel) {
			return centerPanel;
		}
		centerPanel = new Ext.TabPanel({
					deferredRender : false,
					activeTab : 0,
					viewConfig : {
						forceFit : true
					},
					defaults : {
						autoScroll : true,
						border : false,
						autoHeight : true
					},
					items : [{
								title : '查螺记录',
								id : "find",
								name : 0,
								items : this.getSnailFindInfomation()
							}, {
								title : '灭螺记录',
								id : "kill",
								name : 1,
								items : []
							}]
				})
		centerPanel.on("tabchange", this.onTabchange, this);
		this.centerPanel = centerPanel;
		return this.centerPanel;
	},
	onTabchange : function(tabPanel, newTab, curTab) {
		var snailKillInfomationList = this.getSnailKillInfomation();
		snailKillInfomationList.requestData.cnd = snailKillInfomationList.initCnd = [
				"eq", ["$", "a.snailBaseInfoId"], ["s", this.snailBaseInfoId]];
		snailKillInfomationList.snailBaseInfoId = this.snailBaseInfoId;
		snailKillInfomationList.autoLoadData = true;
		if (newTab.name == 1) {
			if (!this.hasLoad) {
				this.hasLoad = true;
				this.initDataId = this.snailBaseInfoId;
				newTab.add(snailKillInfomationList.initPanel());
			} else {
				if (this.initDataId != this.snailBaseInfoId) {
					this.initDataId = this.snailBaseInfoId;
					snailKillInfomationList.refresh();
				}
			}
		}
	},

	// 基本信息表单
	getSnailBaseInfomation : function() {
		var snailBaseInfomationForm = this.midiModules["snailBaseInfomationForm"];
		if (!snailBaseInfomationForm) {
			var cfg = this.loadModuleCfg("chis.application.sch.SCH/SCH/X0402");
			cfg.showButtonOnTop = true;
			cfg.op = this.op;
			cfg.labelWidth = 100;
			cfg.mainApp=this.mainApp;
			cfg.autoLoadSchema = true
			snailBaseInfomationForm = eval("new " + cfg.script + "(cfg)");
			this.midiModules["snailBaseInfomationForm"] = snailBaseInfomationForm;
		}
		snailBaseInfomationForm.initDataId = this.snailBaseInfoId;
		snailBaseInfomationForm
				.on("save", this.onSaveSnailBaseInfomation, this);
		snailBaseInfomationForm.on("loadData", this.onLoadSnailBaseInfomation,
				this);
		return snailBaseInfomationForm.initPanel();
	},

	// 查螺信息列表
	getSnailFindInfomation : function() {
		var snailFindInfomationList = this.midiModules["snailFindInfomationList"];
		if (!snailFindInfomationList) {
			var cfg = this.loadModuleCfg("chis.application.sch.SCH/SCH/X05");
			cfg.isCombined = true;
			cfg.op = this.op;
			cfg.height = this.height - this.itemHeight;
			cfg.showButtonOnTop = true;
			cfg.autoLoadSchema = true
			cfg.autoLoadData = true;
			cfg.mainApp=this.mainApp;
			if (this.op == "create") {
				cfg.autoLoadData = false;
			}
			cfg.autoLoadSchema = false
			cfg.snailBaseInfoId = this.snailBaseInfoId;
			cfg.initCnd = ["eq", ["$", "a.snailBaseInfoId"],
					["s", this.snailBaseInfoId]];
			snailFindInfomationList = eval("new " + cfg.script + "(cfg)");
			this.midiModules["snailFindInfomationList"] = snailFindInfomationList;
			return snailFindInfomationList.initPanel();
		}
	},

	// 灭螺信息列表
	getSnailKillInfomation : function() {
		var snailKillInfomationList = this.midiModules["snailKillInfomationList"];
		if (!snailKillInfomationList) {
			var cfg = this.loadModuleCfg("chis.application.sch.SCH/SCH/X06");
			cfg.isCombined = true;
			cfg.op = this.op;
			cfg.height = this.height - this.itemHeight;
			cfg.showButtonOnTop = true;
			cfg.autoLoadData = false;
			cfg.autoLoadSchema = false
			cfg.snailBaseInfoId = this.snailBaseInfoId;
			cfg.mainApp=this.mainApp;
			cfg.initCnd = ["eq", ["$", "a.snailBaseInfoId"],
					["s", this.snailBaseInfoId]];
			snailKillInfomationList = eval("new " + cfg.script + "(cfg)");
			this.midiModules["snailKillInfomationList"] = snailKillInfomationList;
		}
		return snailKillInfomationList;
	},

	onSaveSnailBaseInfomation : function(entryName, op, json, saveData) {
		if (json.body) {
			this.centerPanel.enable();
			this.centerPanel.setActiveTab("find");
			if (json.body.snailBaseInfoId) {
				this.snailBaseInfoId = json.body.snailBaseInfoId;
			}
			this.midiModules["snailFindInfomationList"].snailBaseInfoId = this.snailBaseInfoId;
			this.midiModules["snailKillInfomationList"].snailBaseInfoId = this.snailBaseInfoId;
			this.fireEvent("save", this);
		}
	},

	onLoadSnailBaseInfomation : function(entryName, body) {
		if (body) {
			this.snailBaseInfoId = body.snailBaseInfoId;
			this.midiModules["snailFindInfomationList"].snailBaseInfoId = this.snailBaseInfoId;
			this.midiModules["snailKillInfomationList"].snailBaseInfoId = this.snailBaseInfoId;
		}
	},

	refresh : function() {
		this.centerPanel.enable();
		this.centerPanel.setActiveTab("find");
		this.midiModules["snailFindInfomationList"].requestData.cnd = this.midiModules["snailFindInfomationList"].initCnd = [
				"eq", ["$", "snailBaseInfoId"], ["s", this.snailBaseInfoId]];
		this.midiModules["snailFindInfomationList"].snailBaseInfoId = this.snailBaseInfoId;
		this.midiModules["snailFindInfomationList"].refresh();
	},

	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : Ext.getBody().getWidth()-300,
						autoHeight : true,
						iconCls : 'icon-form',
						closeAction : 'hide',
						shim : true,
						constrain : true,
						layout : "fit",
						plain : true,
						autoScroll : true,
						minimizable : false,
						maximizable : false,
						shadow : false,
						buttonAlign : 'center',
						modal : true,
						items : this.initPanel()
					})
			win.on("show", function() {
						this.fireEvent("winShow")
					}, this)
			win.on("close", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("hide", function() {
						this.fireEvent("close", this)
					}, this)
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			this.win = win
		}
		win.instance = this;
		return win
	},

	doNew : function() {

	},

	loadData : function(data) {
		var formData = {};
		if (data) {
			formData = this.castListDataToForm(data, this.schema);
		}
		this.midiModules["snailBaseInfomationForm"].initFormData(formData);
		this.midiModules["snailFindInfomationList"].resetCnd();
		this.midiModules["snailKillInfomationList"].resetCnd();
	}
});
