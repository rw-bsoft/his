$package("chis.application.hy.script.first")
$import("chis.script.BizTableFormView")

chis.application.hy.script.first.HypertensionFCBPPHISForm = function(cfg) {
	cfg.colCount = 2
	cfg.labelWidth = 100;
	cfg.autoFieldWidth = true;
	this.entryName="chis.application.hy.schemas.MDC_Hypertension_FCBP_PHIS";
	this.saveServiceId="chis.hypertensionFCBPService";
	this.saveAction="saveHyperFCBPPHIS";
	chis.application.hy.script.first.HypertensionFCBPPHISForm.superclass.constructor
			.apply(this, [cfg]);
	this.on('save',this.onSave);
}
Ext.extend(chis.application.hy.script.first.HypertensionFCBPPHISForm,
		chis.script.BizTableFormView, {
			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				if(this.empiId){
					values.empiId=this.empiId
				}else{
					values.empiId = this.exContext.ids.empiId;
				}
				values.BRID = this.exContext.ids.brid;// 病人id
				values.JZXH = this.exContext.ids.clinicId; // 就诊序号
				Ext.apply(this.data, values);
				this.savingData=values;
				this.saveServiceId="chis.hypertensionFCBPService";
				this.saveAction="saveHyperFCBPPHIS";
//				alert(this.saveServiceId)
				//this.saveToServer(values)
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction :this.saveAction,
							method : "execute",
							body : values
						});
				if (result.code > 300) {
					alert("保存失败");
					return
				}else{
					this.parent.loadData();
					alert("保存成功");
					this.win.close();
				}
			},
			onSave:function()
			{
				if(!this.savingData){return;}
				//如果不是本年的测压，则返回
				var measureYear=this.savingData.measureDate.split('-')[0];
				var nowYear=new Date().format('Y');
				if(measureYear!=nowYear){return;}
				if(true||this.op=='create')
				{
					var SSY=document.getElementById("SSY");
					var SZY=document.getElementById("SZY");
					if(SSY)
						SSY.value=this.savingData.constriction;
					if(SZY)
						SZY.value=this.savingData.diastolic;
				}
			}
		});