$package("phis.application.drc.script");

$import("phis.script.TableForm");

phis.application.drc.script.DRHospitalizationApplicationForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.drc.script.DRHospitalizationApplicationForm.superclass.constructor.apply(this,
			[cfg]);
	this.serviceId="referralService";
	this.saveServiceAction="saveZySendExchange";
}
Ext.extend(phis.application.drc.script.DRHospitalizationApplicationForm, phis.script.TableForm, {
	loadData : function(){
		console.debug(this.exContext)
		this.forms = this.form.getForm();
		this.initData("phoneNumber","LXDH");
		this.initData("address","LXDZ");
		this.initData("birthday","CSNY");
		this.initData("idCard","SFZH");
		this.forms.findField("submitDate").setValue(Date.getServerDate());
	},

	saveToServer : function(saveData) {
		this.form.el.mask("正在保存数据...", "x-mask-loading");
		phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : this.saveServiceAction,
					body : this.data
				}, function(code, msg, json) {
					this.form.el.unmask()
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,[saveData]);
						return
					}else{
						var hsz = this.exContext["mark"];
						if(hsz == 'HSZ'){//护士长或住院医生
							this.opener.list.midiModules['WAR07080201'].refresh();
						}else{
							this.opener.list.midiModules['HOS04030201'].refresh();
						}
						MyMessageTip.msg("提示", "住院转诊成功!", true);
					}
				}, this)
	},

	doSubmit : function(saveData) {
		if (!this.forms.isValid()) {
					return
				}
		this.beforeData("phoneNumber");
		this.beforeData("address");
		this.beforeData("birthday");
		this.beforeData("idCard");
		
		this.beforeData("exchangeReason");
		this.beforeData("announcements");
		this.beforeData("diseaseDescription");
		this.beforeData("agencyPhone");
		this.beforeData("doctorPhone");
		this.beforeData("submitDate");
		this.beforeData("submitAgency");
		
		var submitorDoctor = this.forms.findField("submitorDoctor").getRawValue()
		this.data["submitorDoctor"]=submitorDoctor;
		
		var submitAgency_text = this.forms.findField("submitAgency").getRawValue()
		this.data["submitAgency_text"]= submitAgency_text;
		
		this.data["empiId"]= this.exContext.empiId;
		
		this.data["personName"] = this.exContext["BRXM"];
		this.data["sexCode"] = this.exContext["BRXB"]+"";
		this.data["cardNo"] = this.exContext["YBKH"];
		this.data["ZYH"] = this.exContext["ZYH"];
		this.data["bizType"]= "2";// 业务类型  0挂号1门诊转门诊  2住院转住院  3门诊转住院
		this.saveToServer(this.data);
	},
	
	doCancel : function() {
		this.opener.win.hide();
	},
	
	initData : function (field,value){
		this.forms.findField(field).setValue(this.exContext[value]);
	},
	
	beforeData : function(field){
		this.data[field] = this.forms.findField(field).value;
	}
})