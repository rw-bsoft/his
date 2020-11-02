$package("phis.application.drc.script");

$import("phis.script.TableForm");

phis.application.drc.script.EquipmentXxRecordModule = function(cfg) {
	cfg.actions = [{
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel",
				notReadOnly : true
			}];
	cfg.showButtonOnTop = false;
	this.baseInfo = cfg.baseInfo;
	phis.application.drc.script.EquipmentXxRecordModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.drc.script.EquipmentXxRecordModule,
		phis.script.TableForm, {
			
			loadData : function(){
				this.form = this.form.getForm();
				phis.script.rmi.jsonRequest({
							serviceId : "ReceiveRecordService",
							serviceAction : "getEquipmentRecordByEmpiidAndDh",
							body : {
								empiid : this.baseInfo.EMPIID,
								yuyuesqdbh : this.baseInfo.YUYUESQDBH
							}
						}, function(code, msg, json) {
							if (code < 300) {
								this.form.findField("BINGRENXM").setValue(json.body.BINGRENXM);
								this.form.findField("YUYUERQ").setValue(json.body.YUYUERQ);
								this.form.findField("JIANCHAYYMC").setValue(json.body.JIANCHAYYMC);
								this.form.findField("JIANCHAXMLX").setValue(json.body.JIANCHAXMLX);
								this.form.findField("YINGXIANGFX").setValue(json.body.YINGXIANGFX);
								this.form.findField("JIANCHABWMC").setValue(json.body.JIANCHABWMC);
								if(json.body.YUYUESF == '1'){
									this.form.findField("YUYUESF").setValue("已收费");
								}else if(json.body.YUYUESF == '0'){
									this.form.findField("YUYUESF").setValue("未收费");
								}
								this.form.findField("JIANCHASBMC").setValue(json.body.JIANCHASBMC);
								this.form.findField("JIANCHASBDD").setValue(json.body.JIANCHASBDD);
								this.form.findField("JIANCHAXMMC").setValue(json.body.JIANCHAXMMC);
								this.form.findField("YUYUESQDBH").setValue(json.body.YUYUESQDBH);
								this.form.findField("BINGRENMZH").setValue(json.body.BINGRENMZH);
								this.form.findField("YUYUEH").setValue(json.body.YUYUEH);
								this.form.findField("YIQIYUYUEH").setValue(json.body.YIQIYUYUEH);
								this.form.findField("YUYUESJ").setValue(json.body.YUYUESJ);
							}else{
								this.processReturnMsg(code, msg)
							}
						}, this);
				
			}
		})