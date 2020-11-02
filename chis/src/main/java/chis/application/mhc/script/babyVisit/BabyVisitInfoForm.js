/**
 * 新生儿访视基本信息表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.babyVisit")
$import("chis.script.BizTableFormView", "util.widgets.LookUpField","chis.script.util.Vtype")
chis.application.mhc.script.babyVisit.BabyVisitInfoForm = function(cfg) {
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 135
	cfg.labelWidth = 100
	cfg.colCount = 4
	chis.application.mhc.script.babyVisit.BabyVisitInfoForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeCreate", this.beforeCreate, this);
}
Ext.extend(chis.application.mhc.script.babyVisit.BabyVisitInfoForm, chis.script.BizTableFormView, {

	beforeCreate : function() {
		this.data.empiId = this.exContext.ids.empiId;
		this.data.pregnantId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
		this.form.el.mask("正在初始化数据...", "x-mask-loading")
		util.rmi.jsonRequest({
			serviceId : this.saveServiceId,
			serviceAction : "initBabyVisitInfo",
			method:"execute",
			body : {
				"empiId" : this.exContext.ids.empiId,
				"pregnantId" : this.exContext.ids["MHC_PregnantRecord.pregnantId"]
			}
		}, function(code, msg, json) {
			this.form.el.unmask()
			if (code > 300) {
				this.processReturnMsg(code, msg);
				return
			}
			if (json.body) {
				var body = json.body;
				this.initFormData(body);
			}
		}, this)
	},

	onReady : function() {
		chis.application.mhc.script.babyVisit.BabyVisitInfoForm.superclass.onReady.call(this);

		var form = this.form.getForm();

		var pregnancyDisease = form.findField("pregnancyDisease")
		this.pregnancyDisease = pregnancyDisease
		pregnancyDisease.on("select", this.onPregnancyDisease, this)
		pregnancyDisease.on("keyup", this.onPregnancyDisease, this)

		var birthStatus = form.findField("birthStatus")
		this.birthStatus = birthStatus
		birthStatus.on("select", this.onBirthStatus, this)
		birthStatus.on("keyup", this.onBirthStatus, this)

		var malforMation = form.findField("malforMation")
		this.malforMation = malforMation
		malforMation.on("select", this.onMalforMation, this)
		malforMation.on("keyup", this.onMalforMation, this)

		var illnessScreening = form.findField("illnessScreening");
		this.illnessScreening = illnessScreening;
		illnessScreening.on("select", this.onIllnessScreening, this)
		illnessScreening.on("keyup", this.onIllnessScreening, this)
		
		var fatherNameField = form.findField("fatherName");
		if (fatherNameField) {
			
			fatherNameField.on("lookup", this.findNo, this);
		}

	},
	
	onIllnessScreening : function(){
		var form = this.form.getForm();
		if (this.illnessScreening.getValue() == "3") {
			form.findField("otherIllness").enable()
		} else {
			form.findField("otherIllness").disable()
			form.findField("otherIllness").setValue("")
		}
	},

	loadData : function() {
		this.doNew();
		var datas = this.exContext.args.formDatas
		if (!datas) {
			return;
		}
		this.initFormData(datas);
	},

	initFormData : function(data) {
		chis.application.mhc.script.babyVisit.BabyVisitInfoForm.superclass.initFormData.call(
				this, data)
		this.setWidgetStatus()
	},

	setWidgetStatus : function() {
		this.onPregnancyDisease()
		this.onBirthStatus()
		this.onMalforMation()
	},

	onPregnancyDisease : function() {
		var form = this.form.getForm();
		if (this.pregnancyDisease.getValue() == "3") {
			form.findField("otherDisease").enable()
		} else {
			form.findField("otherDisease").disable()
			form.findField("otherDisease").setValue("")
		}
	},

	onBirthStatus : function() {
		var form = this.form.getForm();
		if (this.birthStatus.getValue() == "7") {
			form.findField("otherStatus").enable()
		} else {
			form.findField("otherStatus").disable()
			form.findField("otherStatus").setValue("")
		}
	},

	onMalforMation : function() {
		var form = this.form.getForm();
		if (this.malforMation.getValue() == "y") {
			form.findField("malforMationDescription").enable()
		} else {
			form.findField("malforMationDescription").disable()
			form.findField("malforMationDescription").setValue("")
		}
	},

	findNo : function(field) {
		$import("chis.application.mpi.script.EMPIInfoModule")
		var expertQuery = this.midiModules["expertQuery"];
		if (!expertQuery) {
			expertQuery = new chis.application.mpi.script.EMPIInfoModule({
						entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
			this.midiModules["expertQuery"] = expertQuery;
		}
		expertQuery.on("onEmpiReturn", function(r) {
					if (!r) {
						return;
					}
					var sex = r.sexCode;
					if (sex != "1") {
						Ext.Msg.alert('提示信息', "性别不符!");
						return;
					}
					var form = this.form.getForm();
					form.findField("fatherName").setValue(r.personName);
					form.findField("fatherJob").setValue(r.workCode);
					form.findField("fatherPhone").setValue(r.contactPhone);
					form.findField("fatherBirth").setValue(r.birthday);
					this.data["fatherEmpiId"] = r.empiId;
				}, this);
		var win = expertQuery.getWin();
		win.setPosition(250, 100);
		win.show();
	}
});