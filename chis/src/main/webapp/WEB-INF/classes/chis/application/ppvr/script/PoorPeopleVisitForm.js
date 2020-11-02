/**
 * 贫困人群随访module中右边的表单
 * 
 * @author : tianj
 */
$package("chis.application.ppvr.script")

$import("chis.script.BizTableFormView")

chis.application.ppvr.script.PoorPeopleVisitForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 135;
	cfg.autoLoadSchema = false;
	cfg.isCombined = true;
	cfg.showButtonOnTop = true;
	cfg.autoLoadData = false;
	chis.application.ppvr.script.PoorPeopleVisitForm.superclass.constructor.apply(this, [cfg]);
	this.on("beforeCreate", this.onBeforeCreate, this);
}

Ext.extend(chis.application.ppvr.script.PoorPeopleVisitForm, chis.script.BizTableFormView, {
			doNew : function() {
				this.initDataId = this.exContext.args.initDataId;
				chis.application.ppvr.script.PoorPeopleVisitForm.superclass.doNew.call(this);
			},

			onReady : function() {
				chis.application.ppvr.script.PoorPeopleVisitForm.superclass.onReady.call(this);
				var isSick = this.form.getForm().findField("isSick");
				if (isSick) {
					this.isSick = isSick;
					isSick.on("select", this.onIsSickChange, this);
					isSick.on("blur", this.onIsSickChange, this);
					isSick.on("keyup", this.onIsSickChange, this);
				}

				var diseaseType = this.form.getForm().findField("diseaseType");
				if (diseaseType) {
					this.diseaseType = diseaseType;
					diseaseType.on("select", this.onDiseaseTypeChange, this);
					diseaseType.on("blur", this.onDiseaseTypeChange, this);
					diseaseType.on("keyup", this.onDiseaseTypeChange, this);
				}
			},

			onIsSickChange : function() {
				var value = this.isSick.getValue();
				var form = this.form.getForm();
				if (value == "y") {
					form.findField("diseaseType").enable();
					this.onDiseaseTypeChange();
				} else {
					form.findField("diseaseType").setValue();
					form.findField("diseaseType").disable();
					form.findField("otherDisease").setValue();
					form.findField("otherDisease").disable();
				}
			},

			onDiseaseTypeChange : function() {
				var v = this.diseaseType.getValue();
				var otherDisease = this.form.getForm()
						.findField("otherDisease");
				var vArray = v.split(",");
				if (vArray.indexOf("8") != -1) {
					otherDisease.enable();
				} else {
					otherDisease.setValue();
					otherDisease.disable();
				}
			},

			doCreate : function() {
				this.exContext.args.initDataId = null;
				this.doNew();
			},

			onBeforeCreate : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "loadInitPoorPeopleRecord",
							method:"execute",
							schema : this.entryName,
							body : {
								"empiId" : this.exContext.args.empiId
							}
						});

				this.initFormData(result.json.body);
				this.onIsSickChange();
			},

			getSaveRequest : function(saveData) {
				saveData.phrId = this.exContext.args.phrId;
				saveData.empiId = this.exContext.args.empiId;
				saveData.updateIncomeSource = this.exContext.args.updateIncomeSource;
				return saveData;
			},

			loadData : function() {
				this.doNew();
				var data = this.exContext.args.data;
				if (!data) {
					this.resetButtons();
					return;
				}
				data = this.castListDataToForm(data, this.schema);
				this.initFormData(data);
			}
		});