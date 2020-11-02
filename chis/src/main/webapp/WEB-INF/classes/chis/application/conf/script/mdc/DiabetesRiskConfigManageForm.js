$package("chis.application.conf.script.mdc")
$import("chis.application.conf.script.RecordManageYearConfigForm")
chis.application.conf.script.mdc.DiabetesRiskConfigManageForm = function(cfg) {
	cfg.fldDefaultWidth = 158
//	this.groupName = "管理年度及生成方式"
	chis.application.conf.script.mdc.DiabetesRiskConfigManageForm.superclass.constructor.apply(
			this, [cfg])
	this.on("loadData", this.onLoadData, this)
//	this.on("beforeSave", this.onBeforeSave, this)
//	this.on("save", this.onSave, this)
}
Ext.extend(chis.application.conf.script.mdc.DiabetesRiskConfigManageForm,
		chis.application.conf.script.RecordManageYearConfigForm, {

//			onBeforeSave : function(entry, op, data) {
//				var planType1 = data.planType1;
//				var planType2 = planType2;
//				var planType3 = planType3;
//				if (data.planMode == 1) {
//					if (planType1 == planType2 || planType1 == planType3
//							|| planType2 && planType2 != ""
//							&& planType2 == planType3) {
//						Ext.MessageBox.alert("提示", "计划类型重复！")
//						return false;
//					}
//					if (planType1 == 11 || planType3 == 11 || planType2 == 11) {
//						Ext.MessageBox.alert("提示", "按随访结果时，其他计划类型不能配置为2周1次！")
//						return false;
//					}
//				}
//			},
//
//			onSave : function(entryName, op, json, data) {
//				var planMode = data.planMode;
//				this.mainApp.exContext.diabetesMode = planMode;
//			},
			isEmpty:function(d){
				for(var name in d){
					return false
				}
				return true
			}
			,
			changePlanMode : function(field, record, index, isloadData) {
				var value = field.getValue();
				var form = this.form.getForm();
				var items = this.schema.items
				var size = items.length
				this.doNew()
				if (!this.isEmpty(record)) {
					for (var i = 0; i < size; i++) {
						var it = items[i];
//						if (it.group != this.groupName) {
							var field = form.findField(it.id);
							field.reset();
							field.disable();
//							if (!isloadData) {
//								field.reset();
//								field.disable();
//							}
							field.allowBlank = true;
							it["not-null"] = false;
//							field.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:black'>"
//									+ it.alias + ":</span>";
//						}
					}
				} else {
					for (var i = 0; i < size; i++) {
						var it = items[i]
//						if (it.group != this.groupName) {
							var field = form.findField(it.id);
							it["not-null"] = true;
//							field.getEl().parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>"
//									+ it.alias + ":</span>";
							field.allowBlank = false;
							field.enable();
//							if (!isloadData) {
//								field.enable();
//							}
//						}
					}
				}
				this.initFormData(record)
				this.validate();
			},

			onLoadData : function(entryName,body) {
//				var planMode = this.form.getForm().findField("planMode");
//				this.changePlanMode(planMode, body, null, true);
			},
			
			onReady : function(){
				chis.application.conf.script.mdc.DiabetesRiskConfigManageForm.superclass.onReady.call(this);
				var form = this.form.getForm();
				form.findField("planType1").on("select", this.onPlanTypeChange, this); 
        		form.findField("planType1").on("change", this.onPlanTypeChange, this);
        		form.findField("planType1").on("blur", this.onPlanTypeChange, this);
			},
        	
        	onPlanTypeChange : function(combo, record, index){
				var nValue = combo.getValue();
				if(nValue == "01"){
					Ext.Msg.alert("提示","糖尿病高危随访计划周期最小为2周1次");
					combo.setValue("02");
					return ;
				}
			}

		});