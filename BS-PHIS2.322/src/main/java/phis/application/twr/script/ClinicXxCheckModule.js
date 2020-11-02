$package("phis.application.drc.script");

$import("phis.script.TableForm");

phis.application.drc.script.ClinicXxCheckModule = function(cfg) {
	cfg.actions = [{
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel",
				notReadOnly : true
			}];
	cfg.showButtonOnTop = false;
	this.baseInfo = cfg.baseInfo;
	phis.application.drc.script.ClinicXxCheckModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.drc.script.ClinicXxCheckModule,
		phis.script.TableForm, {
			
			loadData : function(){
				this.form = this.form.getForm();
				phis.script.rmi.jsonRequest({
							serviceId : "ReceiveRecordService",
							serviceAction : "getCheckHistoryByEmpiidAndDh",
							body : {
								empiid : this.baseInfo.EMPIID,
								jianchasqdh : this.baseInfo.JIANCHASQDH
							}
						}, function(code, msg, json) {
							if (code < 300) {
								this.form.findField("MZHM").setValue(json.body.MZHM);
								this.form.findField("BINGRENXM").setValue(json.body.BINGRENXM);
								this.form.findField("JIANCHASQDH").setValue(json.body.JIANCHASQDH);
								if(json.body.STATUS == '1'){
									this.form.findField("STATUS").setValue("接收申请待检查");
								}else if(json.body.STATUS == '0'){
									this.form.findField("STATUS").setValue("申请提交异常");
								}
								this.form.findField("SONGJIANKSMC").setValue(json.body.SONGJIANKSMC);
								
								this.form.findField("SONGJIANYS").setValue(json.body.SONGJIANYS);
								this.form.findField("SONGJIANRQ").setValue(json.body.SONGJIANRQ);
								this.form.findField("JIUZHENKH").setValue(json.body.JIUZHENKH);
								if(json.body.JIUZHENKLX == '3'){
									this.form.findField("JIUZHENKLX").setValue("就诊卡");
								}else if(json.body.JIUZHENKLX == '2'){
									this.form.findField("JIUZHENKLX").setValue("社保卡");
								}
								
								if(json.body.SHOUFEISB == '0'){
									this.form.findField("SHOUFEISB").setValue("未收费");
								}else if(json.body.SHOUFEISB == '1'){
									this.form.findField("SHOUFEISB").setValue("已收费");
								}
								this.form.findField("BINGQINGMS").setValue(json.body.BINGQINGMS);
								this.form.findField("ZHENDUAN").setValue(json.body.ZHENDUAN);
								this.form.findField("BINGRENTZ").setValue(json.body.BINGRENTZ);
								this.form.findField("QITAJC").setValue(json.body.QITAJC);
								this.form.findField("BINGRENZS").setValue(json.body.BINGRENZS);
								this.form.findField("BINGRENSFZH").setValue(json.body.BINGRENSFZH);
							}else{
								this.processReturnMsg(code, msg)
							}
						}, this);
				
			}
		})