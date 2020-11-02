$package("chis.application.tr.script.highRiskCriterion")

$import("chis.script.BizTableFormView","util.widgets.LookUpField","chis.script.util.query.QueryModule")

chis.application.tr.script.highRiskCriterion.TumourHRCDetailForm = function(cfg){
	cfg.colCount = 2;
	chis.application.tr.script.highRiskCriterion.TumourHRCDetailForm.superclass.constructor.apply(this,[cfg]);
	this.saveServiceId = "chis.tumourCriterionService";
	this.saveAction = "saveTumourHRCDetail";
}

Ext.extend(chis.application.tr.script.highRiskCriterion.TumourHRCDetailForm,chis.script.BizTableFormView,{
	getSaveRequest:function(savaData){
		savaData.criterionSerialNumber = this.exContext.args.hrcId;
		return savaData;
	},
	onReady : function(){
		var frm = this.form.getForm();
		var inspectionItemNameFld = frm.findField("inspectionItemName");
		if (inspectionItemNameFld) {
			inspectionItemNameFld.on("lookup", this.doInspectionItemNameQuery, this);
			inspectionItemNameFld.on("clear", this.doNew, this);
			inspectionItemNameFld.validate();
		}
		var criterionResultFld = frm.findField("criterionResult");
		if(criterionResultFld){
			criterionResultFld.on("select",this.onCriterionResultSelect,this);
		}
		chis.application.tr.script.highRiskCriterion.TumourHRCDetailForm.superclass.onReady.call(this);
	},
	doInspectionItemNameQuery : function(field){
		if (!field.disabled) {
			var inspectionItemNameQuery = this.midiModules["inspectionItemNameQuery"];
			if (!inspectionItemNameQuery) {
				inspectionItemNameQuery = new chis.script.util.query.QueryModule(
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
				this.midiModules["inspectionItemNameQuery"] = inspectionItemNameQuery;
			}
			inspectionItemNameQuery.on("reset",function(form){
				var highRiskType = this.exContext.args.highRiskType;
				if(highRiskType){
					var itemTypeFld = form.getForm().findField("itemType");
					if(itemTypeFld){
						itemTypeFld.setValue({key:highRiskType,text:this.exContext.args.highRiskType_text});
						itemTypeFld.disable();
					}
				}
			},this);
			inspectionItemNameQuery.on("recordSelected", function(r) {
						if (!r) {
							return;
						}
						var frmData = r[0].data;
						this.setInspectionItem(frmData);
					}, this);
			var win = inspectionItemNameQuery.getWin();
			win.setPosition(250, 100);
			win.show();
			var highRiskType = this.exContext.args.highRiskType;
			if(highRiskType){
				var itemTypeFld = inspectionItemNameQuery.form.form.getForm().findField("itemType");
				if(itemTypeFld){
					itemTypeFld.setValue({key:highRiskType,text:this.exContext.args.highRiskType_text});
					itemTypeFld.disable();
				}
			}
		}
	},
	setInspectionItem : function(frmData){
		var inspectionItem = frmData.itemId;
		var inspectionItemName = frmData.definiteItemName;
		this.data.inspectionItem = inspectionItem;
		var frm = this.form.getForm();
		var inspectionItemNameFld = frm.findField("inspectionItemName");
		if(inspectionItemNameFld){
			inspectionItemNameFld.setValue(inspectionItemName);
		}
	},
	onCriterionResultSelect : function(){
		var criterionResultFld = this.form.getForm().findField("criterionResult");
		if(criterionResultFld){
			var crv = criterionResultFld.getValue();
			if(crv == "4"){
				criterionResultFld.setValue();
				criterionResultFld.validate();
				Ext.Msg.alert("提示","无需在高危标准里设置确诊，检查结果为“确诊”的病人可直接转入确诊人群管理。");
			}else if(crv == "5"){
				criterionResultFld.setValue();
				criterionResultFld.validate();
				Ext.Msg.alert("提示","标准中结果只能选择“阴性”、“阳性”或“疾病”三项");
			}
		}
	}
});