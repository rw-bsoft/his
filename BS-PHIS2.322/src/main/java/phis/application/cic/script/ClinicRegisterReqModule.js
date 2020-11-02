$package("phis.application.cic.script");

$import("phis.script.TableForm");

phis.application.cic.script.ClinicRegisterReqModule = function(cfg) {
	cfg.actions = [{
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel",
				notReadOnly : true
			}];
	cfg.showButtonOnTop = false;
	this.baseInfo = cfg.baseInfo;
	phis.application.cic.script.ClinicRegisterReqModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.cic.script.ClinicRegisterReqModule,
		phis.script.TableForm, {
			
			loadData : function(){
				this.form = this.form.getForm();
				phis.script.rmi.jsonRequest({
							serviceId : "ReceiveRecordService",
							serviceAction : "getRegisterReqHistoryByEmpiidAndDh",
							body : {
								empiid : this.baseInfo.EMPIID,
								quhaomm : this.baseInfo.QUHAOMM
							}
						}, function(code, msg, json) {
							if (code < 300) {
								this.form.findField("MZHM").setValue(json.body.MZHM);
								this.form.findField("QUHAOMM").setValue(json.body.QUHAOMM);
								this.form.findField("BINGRENXM").setValue(json.body.BINGRENXM);
								this.form.findField("YUYUERQ").setValue(json.body.YUYUERQ);
								this.form.findField("SHENQINGYS").setValue(json.body.SHENQINGYS);
								this.form.findField("ZHUANRUYYMC").setValue(json.body.ZHUANRUYYMC);
								this.form.findField("ZHUANRUKSMC").setValue(json.body.ZHUANRUKSMC);
								this.form.findField("YISHENGXM").setValue(json.body.YISHENGXM);
								this.form.findField("GUAHAOBC").setValue(json.body.GUAHAOBC);
								this.form.findField("JIUZHENSJ").setValue(json.body.JIUZHENSJ);
								this.form.findField("ZHUANZHENDH").setValue(json.body.ZHUANZHENDH);
								this.form.findField("JIUZHENDD").setValue(json.body.JIUZHENDD);
							}else{
								this.processReturnMsg(code, msg)
							}
						}, this);
				
			}
		})