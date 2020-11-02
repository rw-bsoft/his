$package("phis.application.stm.script")

$import("phis.script.TableForm")

phis.application.stm.script.ClinicSkinTestResultForm = function(cfg) {
	cfg.colCount = 1;
	this.serviceId = 'phis.skintestManageService';
	phis.application.stm.script.ClinicSkinTestResultForm.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.stm.script.ClinicSkinTestResultForm,
		phis.script.TableForm, {
			onWinShow : function() {
				if (this.data) {
					var f = this.form.getForm();
					f.findField("YPMC").setValue(this.data.YPMC);
					f.findField("BRXM").setValue(this.data.BRXM);
					f.findField("PSJG").setValue(-1);
					f.findField("GMPH").setValue("");
				}
				f.findField("PSJG").focus(false, 200);
			},
			doConfirm : function() {
				var f = this.form.getForm();
				if (f.findField("PSJG").getValue() == "") {
					MyMessageTip.msg("提示", "请输入皮试结果!", true);
					f.findField("PSJG").focus(false, 200);
					return;
				}
				this.form.el.mask("保存中...")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "saveSkinTestResult",
							body : {
								CFSB : this.data.CFSB,
								YPXH : this.data.YPBH,
								BRID : this.data.BRBH,
								PSJG : f.findField("PSJG").getValue(),
								GMPH : f.findField("GMPH").getValue(),
								JGID : this.mainApp['phisApp'].deptId
							}
						}, function(code, msg, json) {
							if (code > 200) {
								this.processReturnMsg(code, msg)
								this.form.el.unmask();
								return;
							}
							this.form.el.unmask();
							MyMessageTip.msg("提示", "保存成功", true);
							this.fireEvent("saveSuccess");
							this.doCancel();
						}, this)
					
	
				//与公卫业务联动开始，过敏药品添加到个人既往史中
				var psjg=f.findField("PSJG").getValue();
				//如果皮试结果不是阳性或者过敏类别为空则不写到既往史
			    if(psjg!=1||!this.data.GMYWLB)
			    	return;	

			    //如果存在公卫系统则保存到公卫个人既往史中
			    if(!this.SFQYGWXT){
				var publicParam = {
						"commons" : ['SFQYGWXT']
					}
				this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
			    }
				if(this.SFQYGWXT=='1'&&this.mainApp.chisActive){
					var brempiid=this.data.EMPIID;
					var nowDate=(new Date()).format('Y-m-d');
					var gmywlb='0109';
					var gmywlbtext='其他';
					var gmywlbs={'1':'0102',
							     '2':'0103',
							     '3':'0104'
								}
					if(gmywlbs.hasOwnProperty(this.data.GMYWLB))
						gmywlb=gmywlbs[this.data.GMYWLB];
					if(this.data.GMYWLB_text)
					{
						gmywlbtext=this.data.GMYWLB_text;
					}
					//保存既往史
					util.rmi.miniJsonRequestAsync({
						serviceId : "chis.healthRecordService",
						serviceAction : "savePastHistoryHis",
						schema:'chis.application.hr.schemas.EHR_PastHistory',
						op:'create',
						body : {
							empiId:brempiid, 
							record:[
							{
								empiId:brempiid,
								pastHisTypeCode_text:'药物过敏史', 
								pastHisTypeCode:'01',
								methodsCode:'', 
								protect:'', 
								diseaseCode:gmywlb,
								diseaseText:gmywlbtext, 
								vestingCode:'', 
								startDate:'', 
								endDate:'', 
								confirmDate:nowDate, 
								recordUnit:this.mainApp.deptId, 
								recordUser:this.mainApp.uid, 
								recordDate:nowDate, 
								lastModifyUser:this.mainApp.uid, 
								lastModifyUnit:this.mainApp.deptId, 
								lastModifyDate:nowDate
							   }
							   ], 
							   delPastId:[]	
						}
					}, function(code, msg, json) {
						if (code < 300) {	
						} else {
							this.processReturnMsg(code, msg);
						}
					}, this);
				}
			//与公卫业务联动结束	
			},
			doCancel : function() {
				this.win.hide();
			}
		});