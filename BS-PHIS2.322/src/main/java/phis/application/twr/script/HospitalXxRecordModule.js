$package("phis.application.drc.script");

$import("phis.script.TableForm");

phis.application.drc.script.HospitalXxRecordModule = function(cfg) {
	cfg.actions = [{
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel",
				notReadOnly : true
			}];
	cfg.showButtonOnTop = false;
	this.baseInfo = cfg.baseInfo;
	phis.application.drc.script.HospitalXxRecordModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.drc.script.HospitalXxRecordModule,
		phis.script.TableForm, {
			
			loadData : function(){
				this.form = this.form.getForm();
				phis.script.rmi.jsonRequest({
							serviceId : "ReceiveRecordService",
							serviceAction : "getRecordHistoryByEmpiidAndDh",
							body : {
								empiid : this.baseInfo.EMPIID,
								zhuanzhendh : this.baseInfo.ZHUANZHENDH,
								yewulx : this.baseInfo.YEWULX
							}
						}, function(code, msg, json) {
							if (code < 300) {
								this.form.findField("BINGRENCSRQ").setValue(json.body.BINGRENCSRQ);
								this.form.findField("BINGRENLXDH").setValue(json.body.BINGRENLXDH);
								this.form.findField("BINGRENFYLB").setValue(json.body.BINGRENFYLB);
								this.form.findField("BINGRENLXDZ").setValue(json.body.BINGRENLXDZ);
								this.form.findField("BINGRENNL").setValue(json.body.BINGRENNL);
								this.form.findField("BINGRENSFZH").setValue(json.body.BINGRENSFZH);
								if(json.body.BINGRENXB == '1'){
									this.form.findField("BINGRENXB").setValue("男");
								}else if(json.body.BINGRENXB == '2'){
									this.form.findField("BINGRENXB").setValue("女");
								}
								this.form.findField("BINGRENXM").setValue(json.body.BINGRENXM);
								this.form.findField("BINQINGMS").setValue(json.body.BINQINGMS);
								this.form.findField("JIUZHENKH").setValue(json.body.JIUZHENKH);
								if(json.body.JIUZHENKLX == '3'){
									this.form.findField("JIUZHENKLX").setValue("就诊卡");
								}else if(json.body.JIUZHENKLX == '2'){
									this.form.findField("JIUZHENKLX").setValue("社保卡");
								}
								this.form.findField("MZHM").setValue(json.body.MZHM);
								this.form.findField("SHENQINGJGLXDH").setValue(json.body.SHENQINGJGLXDH);
								this.form.findField("SHENQINGJGMC").setValue(json.body.SHENQINGJGMC);
								this.form.findField("SHENQINGRQ").setValue(json.body.SHENQINGRQ);
								this.form.findField("SHENQINGYS").setValue(json.body.SHENQINGYS);
								this.form.findField("SHENQINGYSDH").setValue(json.body.SHENQINGYSDH);
								if(json.body.STATUS == '1'){
									this.form.findField("STATUS").setValue("接收待入院");
								}else if(json.body.STATUS == '0'){
									this.form.findField("STATUS").setValue("转诊异常");
								}
								if(json.body.YEWULX == '1'){
									this.form.findField("YEWULX").setValue("门诊转诊");
								}else if(json.body.YEWULX == '2'){
									this.form.findField("YEWULX").setValue("住院转诊");
								}else if(json.body.YEWULX == '3'){
									this.form.findField("YEWULX").setValue("门诊转住院");
								}
								this.form.findField("YIBAOKLX").setValue(json.body.YIBAOKLX);
								this.form.findField("YIBAOKXX").setValue(json.body.YIBAOKXX);
								this.form.findField("ZHUANRUKSMC").setValue(json.body.ZHUANRUKSMC);
								this.form.findField("ZHUANZHENDH").setValue(json.body.ZHUANZHENDH);
								this.form.findField("ZHUANZHENYY").setValue(json.body.ZHUANZHENYY);
								this.form.findField("ZHUANZHENZYSX").setValue(json.body.ZHUANZHENZYSX);
							}else{
								this.processReturnMsg(code, msg)
							}
						}, this);
				
			}
		})