$package("chis.application.tr.script.screening")

$import("chis.script.BizTableFormView","util.widgets.LookUpField","chis.script.util.query.QueryModule")

chis.application.tr.script.screening.TumourScreeningCheckResultForm = function(cfg){
	chis.application.tr.script.screening.TumourScreeningCheckResultForm.superclass.constructor.apply(this,[cfg]);
	this.on("beforeCreate",this.onBeforeCreate,this);
	this.on("loadData",this.onLoadData,this);
	this.saveServiceId="chis.tumourScreeningService";
	this.saveAction="saveCheckResult";
}

Ext.extend(chis.application.tr.script.screening.TumourScreeningCheckResultForm,chis.script.BizTableFormView,{
	onBeforeCreate : function(){
		this.data.screeningId = this.exContext.args.screeningId;
		this.data.empiId = this.exContext.args.empiId;
		var frm = this.form.getForm();
		var highRiskTypeFld = frm.findField("highRiskType");
		if(highRiskTypeFld){
			if(this.exContext.args && this.exContext.args.highRiskType){
				highRiskTypeFld.setValue({key:this.exContext.args.highRiskType,text:this.exContext.args.highRiskType_text});
			}
		}
	},
	onLoadData : function(entryName,body){
		var control = this.exContext.control;
		var create = control.create;
		var update = control.update;
		var btns = this.form.getTopToolbar().items;
		if(btns){
			var createBtn = btns.item(0);
			var saveBtn = btns.item(1);
			if(create){
				createBtn.enable();
			}else{
				createBtn.disable();
			}
			if(update || create){
				saveBtn.enable();
			}else{
				saveBtn.disable();
			}
		}
	},
	onReady : function(){
		var frm = this.form.getForm();
		var checkItemFld = frm.findField("checkItem");
		if(checkItemFld){
			checkItemFld.on("lookup", this.doInspectionItemNameQuery, this);
			checkItemFld.on("clear", this.doNew, this);
			checkItemFld.validate();
		}
		chis.application.tr.script.screening.TumourScreeningCheckResultForm.superclass.onReady.call(this);
	},
	doInspectionItemNameQuery : function(field){
		if (!field.disabled) {
			var checkItemQuery = this.midiModules["checkItemQuery"];
			if (!checkItemQuery) {
				checkItemQuery = new chis.script.util.query.QueryModule(
						{
							title : "筛查项目查询",
							autoLoadSchema : true,
							isCombined : true,
							autoLoadData : false,
							mutiSelect : false,
							queryCndsType : "1",
							entryName : "chis.application.tr.schemas.MDC_TumourInspectionItemCommonQuery",
							buttonIndex : 3,
							itemHeight : 125
						});
				this.midiModules["checkItemQuery"] = checkItemQuery;
			}
			checkItemQuery.on("recordSelected", function(r) {
						if (!r) {
							return;
						}
						var frmData = r[0].data;
						this.setInspectionItem(frmData);
					}, this);
			var win = checkItemQuery.getWin();
			win.setPosition(250, 100);
			win.show();
			var highRiskType = this.exContext.args.highRiskType;
			if(highRiskType){
				var itemTypeFld = checkItemQuery.form.form.getForm().findField("itemType");
				if(itemTypeFld){
					itemTypeFld.setValue({key:highRiskType,text:this.exContext.args.highRiskType_text});
					itemTypeFld.disable();
				}
			}
			checkItemQuery.form.doSelect();
		}
	},
	setInspectionItem : function(frmData){
		var inspectionItem = frmData.itemId;
		var inspectionItemName = frmData.definiteItemName;
		this.data.itemId = inspectionItem;
		var frm = this.form.getForm();
		var checkItemFld = frm.findField("checkItem");
		if(checkItemFld){
			checkItemFld.setValue(inspectionItemName);
		}
	},
	doSave: function(){
		if(this.saving){
			return
		}
		var values = this.getFormData();
		if(!values){
			return;
		}
		Ext.apply(this.data,values);
		values.planId = this.exContext.args.planId || '';
		var tsData = {};
		tsData.empiId = this.exContext.args.empiId;
		tsData.phrId = this.exContext.args.phrId;
		tsData.recordId = this.exContext.args.screeningId;
		tsData.highRiskType = this.exContext.args.highRiskType;
		tsData.highRiskSource = this.exContext.args.highRiskSource
		values.tsData = tsData;
		var checkResult = values.checkResult;
		if(checkResult=="4"){
			var visitEffect = this.exContext.args.visitEffect;
			if(visitEffect && visitEffect == '3'){
				this.saveToServer(values)
			}else if(visitEffect && visitEffect != '3'){
				this.saveToServer(values)
			}else{
				var tcrData = this.exContext.args.tsData || tsData;
				if(this.exContext.args.planId){
					tcrData.confirmedSource = {"key":"2","text":"高危人群"};
				}else{
					tcrData.confirmedSource = {"key":"1","text":"初筛"};
				}
				tcrData.notification = {"key":"n","text":"否"};
				tcrData.status = {"key":"0","text":"正常"};
				tcrData.nature = {"key":"4","text":"确诊"};
				this.showTCEhrViewWin(tcrData,values);
			}
		}else{
			this.saveToServer(values)
		}
	},
	showTCEhrViewWin : function(tcrData,values) {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['T_06'];
		cfg.mainApp = this.mainApp;
		cfg.activeTab = 0;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["TumourConfirmed_EHRView"];
		if (!module) {
			$import("chis.script.EHRView");
			module = new chis.script.EHRView(cfg);
			this.midiModules["TumourConfirmed_EHRView"] = module;
			module.exContext.ids.empiId = tcrData.empiId;
			module.exContext.ids.highRiskType = tcrData.highRiskType.key || '';
			module.exContext.ids.TCID = tcrData.TCID;
			module.exContext.ids.recordStatus = 0;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args.empiId = tcrData.empiId;;
			module.exContext.args.highRiskType = tcrData.highRiskType.key || '';
			module.exContext.args.tcrData = tcrData;
			module.exContext.args.turnConfirmed = true;
			module.exContext.args.recordId = tcrData.recordId;
			module.exContext.args.saveServiceId = "chis.tumourConfirmedService";
			module.exContext.args.saveAction = "saveTumourScreeningToConfirmed";
			module.exContext.args.loadServiceId = "chis.tumourConfirmedService";
			module.exContext.args.loadAction="getTCByEH";
			module.exContext.control = {};
			module.on("save", function(){
				this.saveToServer(values);
			}, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids.empiId = tcrData.empiId;
			module.exContext.ids.highRiskType = tcrData.highRiskType.key || '';
			module.exContext.ids.TCID = tcrData.TCID;
			module.exContext.ids.recordStatus = 0;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args.empiId = tcrData.empiId;;
			module.exContext.args.highRiskType = tcrData.highRiskType.key || '';
			module.exContext.args.tcrData = tcrData;
			module.exContext.args.turnConfirmed = true;
			module.exContext.args.recordId = tcrData.recordId;
			module.exContext.args.saveServiceId = "chis.tumourConfirmedService";
			module.exContext.args.saveAction = "saveTumourScreeningToConfirmed";
			module.exContext.args.loadServiceId = "chis.tumourConfirmedService";
			module.exContext.args.loadAction="getTCByEH";
			module.exContext.control = {};
			module.refresh();
		}
		module.getWin().show();
	}
	
});