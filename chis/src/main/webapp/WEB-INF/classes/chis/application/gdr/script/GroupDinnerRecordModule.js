﻿$package("chis.application.gdr.script")

$import("chis.script.BizModule", "app.desktop.TaskManager",
		"app.modules.combined.TabModule")

chis.application.gdr.script.GroupDinnerRecordModule = function(cfg) {
	chis.application.gdr.script.GroupDinnerRecordModule.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.gdr.script.GroupDinnerRecordModule, chis.script.BizModule, {
	initPanel : function() {
		if (this.mainTab) {
			return this.mainTab;
		}
		var tabItems = [];
		tabItems.push({
					layout : "fit",
					title : "群宴登记",
					name : "GroupDinnerRecordForm"
				});
		tabItems.push({
					layout : "fit",
					title : "群宴首次指导",
					name : "FirstGuideForm"
				});
		tabItems.push({
					layout : "fit",
					title : "群宴第二次指导",
					name : "SecondGuideForm"
				});
		tabItems.push({
					layout : "fit",
					title : "回访信息",
					name : "VisitForm"
				});
		var mainTab = new Ext.TabPanel({
					title : " ",
					border : false,
					activeTab : 0,
					width : 780,
					frame : false,
					autoHeight : true,
					defaults : {
						border : false,
						autoHeight : true,
						autoWidth : true
					},
					items : tabItems
				});
		mainTab.on("tabchange", this.onTabChange, this);
		this.mainTab = mainTab;
		return mainTab;
	},

	onTabChange : function(tabPanel, newTab, oldTab) {
		if (!newTab) {
			return;
		}
		if (newTab.__inited) {
			if (this.midiModules[newTab.name].gdrId == this.initDataId) {
				return;
			}
			this.midiModules[newTab.name].gdrId = this.initDataId;
			if (newTab.name == "GroupDinnerRecordForm") {
				this.midiModules["GroupDinnerRecordForm"].initDataId = this.initDataId;
			}
			this.midiModules[newTab.name].loadData();
			return;
		}
		var p = {};
		if (newTab.name == "GroupDinnerRecordForm") {
			p = this.getGroupDinnerRecordFormPanel();
		} else if (newTab.name == "FirstGuideForm") {
			p = this.getFirstGuideFormPanel();
		} else if (newTab.name == "SecondGuideForm") {
			p = this.getSecondGuideFormPanel();
		} else if (newTab.name == "VisitForm") {
			p = this.getVisitFormPanel();
		}
		newTab.add(p);
		newTab.__inited = true;
		this.mainTab.doLayout();
		if(this.op=="create"){
			return;
		}
		this.midiModules[newTab.name].loadData();
	},
	
	getVisitFormPanel : function() {
		var module = this.midiModules["VisitForm"]
		if (!module) {
			var cfg = {};
			$import("chis.application.gdr.script.VisitForm");
			cfg= this.loadModuleCfg("chis.application.gdr.GDR/GDR/Q01-4");
			cfg.isCombined = true;
			cfg.autoLoadSchema = true;
			cfg.showButtonOnTop = true;
			cfg.autoLoadData = false;
			cfg.gdrId = this.initDataId;
			cfg.readOnly = this.readOnly;
			cfg.mainApp = this.mainApp;
			cfg.colCount = 3;
			cfg.autoFieldWidth = false;
			cfg.fldDefaultWidth = 140;
			cfg.labelWidth = 120;
			module = new chis.application.gdr.script.VisitForm(cfg);
			this.midiModules["VisitForm"] = module;
			module.on("loadData",this.visitFormLoadData,this)
			var form = module.initPanel();
			this.visitForm = form;
			return this.visitForm;
		}
	},

	getSecondGuideFormPanel : function() {
		var module = this.midiModules["SecondGuideForm"];
		if (!module) {
			var cfg = {};
			$import("chis.application.gdr.script.SecondGuideForm");
			cfg= this.loadModuleCfg("chis.application.gdr.GDR/GDR/Q01-3");
			cfg.isCombined = true;
			cfg.autoLoadSchema = true;
			cfg.autoLoadData = false;
			cfg.showButtonOnTop = true;
			cfg.gdrId = this.initDataId;
			cfg.readOnly = this.readOnly;
			cfg.mainApp = this.mainApp;
			cfg.colCount = 3;
			cfg.autoFieldWidth = false;
			cfg.fldDefaultWidth = 140;
			cfg.labelWidth = 120;
			module = new chis.application.gdr.script.SecondGuideForm(cfg);
			module.on("loadData",this.secondGuideFormLoadData,this)
			module.on("controlDate",this.secondGuideDateSelect,this)
			this.midiModules["SecondGuideForm"] = module;
			var form = module.initPanel();
			return form;
		}
	},

	getFirstGuideFormPanel : function() {
		var module = this.midiModules["FirstGuideForm"];
		if (!module) {
			var cfg = {};
			$import("chis.application.gdr.script.FirstGuideForm");
			cfg= this.loadModuleCfg("chis.application.gdr.GDR/GDR/Q01-2");
			cfg.isCombined = true;
			cfg.autoLoadSchema = true;
			cfg.showButtonOnTop = true;
			cfg.autoLoadData = false;
			cfg.gdrId = this.initDataId;
			cfg.readOnly = this.readOnly;
			cfg.mainApp = this.mainApp;
			cfg.colCount = 3;
			cfg.autoFieldWidth = false;
			cfg.fldDefaultWidth = 140;
			cfg.labelWidth = 120;
			module = new chis.application.gdr.script.FirstGuideForm(cfg);
			module.on("save", this.onFirstGuideFormSave, this);
			module.on("loadData",this.firstGuideFormLoadData,this)
			module.on("controlDate",this.firstGuideDateSelect,this)
			this.midiModules["FirstGuideForm"] = module;
			var form = module.initPanel();
			this.firstGuideForm = form;
			return form;
		}
	},

	getGroupDinnerRecordFormPanel : function() {
		var module = this.midiModules["GroupDinnerRecordForm"];
		if (!module) {
			var cfg = {};
			$import("chis.application.gdr.script.GroupDinnerRecordForm");
			cfg= this.loadModuleCfg("chis.application.gdr.GDR/GDR/Q01-1");
			cfg.isCombined = true;
			cfg.autoLoadSchema = true;
			cfg.showButtonOnTop = true;
			cfg.autoLoadData = false;
			cfg.phrId = this.phrId;
			cfg.empiId = this.empiId;
			cfg.readOnly = this.readOnly;
			cfg.mainApp = this.mainApp;
			cfg.colCount = 3;
			cfg.autoFieldWidth = false;
			cfg.fldDefaultWidth = 140;
			cfg.labelWidth = 120;
			module = new chis.application.gdr.script.GroupDinnerRecordForm(cfg);
			module.on("save", this.onGroupDinnerRecordSave, this);
			module.on("loadData", this.onGroupDinnerRecordLoadData, this);
			module.on("meetingDateSelect",this.onMeetingDateSelect,this)
			module.on("applyDateSelect",this.onApplyDateSelect,this)
			this.midiModules["GroupDinnerRecordForm"] = module;
			var form = module.initPanel();
			this.groupDinnerRecordForm = form;
			return this.groupDinnerRecordForm;
		}
	},

	doCreate : function() {
		this.initDataId = null;
		this.mainTab.activate(0);
		this.midiModules["GroupDinnerRecordForm"].readOnly = this.readOnly;
		this.midiModules["GroupDinnerRecordForm"].doCreate();
		this.midiModules["GroupDinnerRecordForm"].onSelectDate();
		if (this.midiModules["FirstGuideForm"]) {
			this.midiModules["FirstGuideForm"].gdrId = null;
			this.midiModules["FirstGuideForm"].readOnly = this.readOnly;
			this.midiModules["FirstGuideForm"].doCreate();
		}
		if (this.midiModules["SecondGuideForm"]) {
			this.midiModules["SecondGuideForm"].gdrId = null;
			this.midiModules["SecondGuideForm"].readOnly = this.readOnly;
			this.midiModules["SecondGuideForm"].doCreate();
		}
		if (this.midiModules["VisitForm"]) {
			this.midiModules["VisitForm"].gdrId = null;
			this.midiModules["VisitForm"].readOnly = this.readOnly;
			this.midiModules["VisitForm"].doCreate();
		}
		this.mainTab.items.itemAt(1).disable();
		this.mainTab.items.itemAt(2).disable();
		this.mainTab.items.itemAt(3).disable();
	},

	loadData : function() {
		this.mainTab.activate(0);
		if (this.midiModules["GroupDinnerRecordForm"].initDataId == this.initDataId) {
			return;
		}
		this.midiModules["GroupDinnerRecordForm"].initDataId = this.initDataId;
		this.midiModules["GroupDinnerRecordForm"].gdrId = this.initDataId;
		this.midiModules["GroupDinnerRecordForm"].readOnly = this.readOnly;
		this.midiModules["GroupDinnerRecordForm"].loadData();
		if (this.midiModules["FirstGuideForm"]) {
			this.midiModules["FirstGuideForm"].readOnly = this.readOnly;
		}
		if (this.midiModules["SecondGuideForm"]) {
			this.midiModules["SecondGuideForm"].readOnly = this.readOnly;
		}
		if (this.midiModules["VisitForm"]) {
			this.midiModules["VisitForm"].readOnly = this.readOnly;
		}
		this.mainTab.items.itemAt(1).enable();
		this.mainTab.items.itemAt(2).enable();
		this.mainTab.items.itemAt(3).enable();
	},

	onFirstGuideFormSave : function() {
		this.mainTab.items.itemAt(2).enable();
		this.mainTab.items.itemAt(3).enable();
	},

	onGroupDinnerRecordSave : function(entryName, op, json, rec) {
		this.fireEvent("save", entryName, op, json, rec);
		this.initDataId = rec.gdrId;
		this.midiModules["GroupDinnerRecordForm"].gdrId = this.initDataId;
		if (this.midiModules["FirstGuideForm"]) {
			this.midiModules["FirstGuideForm"].gdrId = this.initDataId;
		}
		if (this.midiModules["SecondGuideForm"]) {
			this.midiModules["SecondGuideForm"].gdrId = this.initDataId;
		}
		if (this.midiModules["VisitForm"]) {
			this.midiModules["VisitForm"].gdrId = this.initDataId;
		}
		this.mainTab.items.itemAt(1).enable();
	},

	onGroupDinnerRecordLoadData : function(entryName, body) {
		var hasFirstGuide = body.hasFirstGuide;
		this.mainTab.items.itemAt(1).enable();
		this.midiModules["GroupDinnerRecordForm"].onApplyDateSelect();
		if (hasFirstGuide) {
			this.mainTab.items.itemAt(2).enable();
			this.mainTab.items.itemAt(3).enable();
		} else {
			this.mainTab.items.itemAt(2).disable();
			this.mainTab.items.itemAt(3).disable();
		}
//		if(this.midiModules["FirstGuideForm"]){
//			this.midiModules["FirstGuideForm"].form.getForm().findField("teachDate").setMinValue(this.midiModules["GroupDinnerRecordForm"].form.getForm().findField("applyDate").getValue())
//		}
	},
	firstGuideFormLoadData:function(){
		this.midiModules["FirstGuideForm"].form.getForm().findField("teachDate").setMinValue(this.midiModules["GroupDinnerRecordForm"].form.getForm().findField("applyDate").getValue())
		this.midiModules["FirstGuideForm"].form.getForm().findField("teachDate").setMaxValue(this.midiModules["GroupDinnerRecordForm"].form.getForm().findField("meetingDate").getValue())
	}
	,
	secondGuideFormLoadData:function(){
		this.midiModules["SecondGuideForm"].form.getForm().findField("guideDate").setMinValue(this.midiModules["FirstGuideForm"].form.getForm().findField("teachDate").getValue())
		this.midiModules["SecondGuideForm"].form.getForm().findField("guideDate").setMaxValue(this.midiModules["GroupDinnerRecordForm"].form.getForm().findField("meetingDate").getValue())
	}
	,
	visitFormLoadData:function(){
		this.midiModules["VisitForm"].form.getForm().findField("visitDate").setMinValue(this.midiModules["GroupDinnerRecordForm"].form.getForm().findField("meetingDate").getValue()) 
	}
	,
	onMeetingDateSelect:function(){
		if(this.midiModules["SecondGuideForm"]){
			this.midiModules["SecondGuideForm"].form.getForm().findField("guideDate").setMaxValue(this.midiModules["GroupDinnerRecordForm"].form.getForm().findField("meetingDate").getValue())
		}
		if(this.midiModules["VisitForm"]){
			this.midiModules["VisitForm"].form.getForm().findField("visitDate").setMinValue(this.midiModules["GroupDinnerRecordForm"].form.getForm().findField("meetingDate").getValue())
		}
	}
	,
	onApplyDateSelect:function(){
		if(this.midiModules["FirstGuideForm"]){
			this.midiModules["FirstGuideForm"].form.getForm().findField("teachDate").setMinValue(this.midiModules["GroupDinnerRecordForm"].form.getForm().findField("applyDate").getValue())
		}
	}
	,
	firstGuideDateSelect:function(){
		if(this.midiModules["SecondGuideForm"]){
			this.midiModules["SecondGuideForm"].form.getForm().findField("guideDate").setMinValue(this.midiModules["FirstGuideForm"].form.getForm().findField("teachDate").getValue())
		}
	}
	,
	secondGuideDateSelect:function(){
		if(this.midiModules["VisitForm"]){
			this.midiModules["VisitForm"].form.getForm().findField("visitDate").setMinValue(this.midiModules["GroupDinnerRecordForm"].form.getForm().findField("meetingDate").getValue())
		}
		this.midiModules["FirstGuideForm"].form.getForm().findField("teachDate").setMaxValue(this.midiModules["SecondGuideForm"].form.getForm().findField("guideDate").getValue())
	}
	,
	getWin : function() {
		var win = this.win;
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title,
						autoWidth : true,
						autoHeight : true,
						iconCls : 'icon-form',
						closeAction : 'hide',
						shim : true,
						layout : "fit",
						plain : true,
						autoScroll : false,
						minimizable : true,
						maximizable : true,
						constrain : true,
						shadow : false,
						buttonAlign : 'center',
						modal : true,
						items : this.initPanel()
					})
			win.on("show", function() {
						this.fireEvent("winShow");
					}, this)
			win.on("close", function() {
						this.fireEvent("close", this);
					}, this)
			win.on("hide", function() {
						this.fireEvent("close", this);
					}, this)
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
			this.win = win;
		}
		win.instance = this;
		return win;
	}
})