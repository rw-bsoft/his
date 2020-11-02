$package("chis.application.scm.sr.script")
$import("chis.script.BizTableFormView")

chis.application.scm.sr.script.NewServiceForm = function (cfg) {
    cfg.colCount = 2;
    chis.application.scm.sr.script.NewServiceForm.superclass.constructor.apply(
    this, [cfg]);
}
Ext.extend(chis.application.scm.sr.script.NewServiceForm, chis.script.BizTableFormView,
    {
        setDatas : function(){
        	if(this.selectedR.data.op && this.selectedR.data.op=="update"){
	            this.form.getForm().findField("serviceOrg").setValue({text:this.selectedR.data.serviceOrgName,key:this.selectedR.data.serviceOrg});
	            this.form.getForm().findField("serviceTeam").setValue({text:this.selectedR.data.serviceTeamName,key:this.selectedR.data.serviceTeam});
	            this.form.getForm().findField("servicer").setValue({text:this.selectedR.data.servicerName,key:this.selectedR.data.servicer});
	            this.form.getForm().findField("servicePack").setValue(this.selectedR.data.servicePack);
	            this.form.getForm().findField("serviceItems").setValue(this.selectedR.data.serviceItems);
	            this.form.getForm().findField("serviceObj").setValue(this.selectedR.data.serviceObj);
	            this.form.getForm().findField("serviceDate").setValue(this.selectedR.data.serviceDate);
	            //this.form.getForm().findField("detailedAddress").setValue(this.selectedR.data.detailedAddress);
	            //this.form.getForm().findField("gridAddress").setValue({text:this.selectedR.data.gridAddressName,key:this.selectedR.data.gridAddress});
	            this.form.getForm().findField("serviceMode").setValue({text:this.selectedR.data.serviceModeName,key:this.selectedR.data.serviceMode});
	            this.form.getForm().findField("serviceDesc").setValue(this.selectedR.data.serviceDesc);
        	}
        	else{
	            this.form.getForm().findField("servicePack").setValue(this.selectedR.data.packageName);
	            this.form.getForm().findField("serviceItems").setValue(this.selectedR.data.taskName);
	            this.form.getForm().findField("serviceObj").setValue(this.personInfo.data.personName);
	            this.form.getForm().findField("detailedAddress").setValue(this.personInfo.data.address);
        	}
        },
        doSave : function(){
        	debugger;
            //先取得表单中的数据
            var values = this.getFormData();
            values.taskId = this.selectedR.data.taskId;
            values.servicePackId = this.selectedR.data.SPID;
            values.taskCode = this.selectedR.data.taskCode;
            values.SCID = this.selectedR.data.SCID;
            values.SPIID = this.selectedR.data.SPIID;
            values.SCINID = this.selectedR.data.SCINID;
            values.SCIID = this.selectedR.data.SCIID;
            values.serviceItemsId = this.selectedR.data.taskCode;
            values.empiId = this.personInfo.data.favoreeEmpiId;
            if(this.selectedR.data.op && this.selectedR.data.op =="update"){
            	values.serviceId = this.selectedR.data.serviceId;
            	values.op = "update";
            }
            //ajax请求
            util.rmi.jsonRequest({
                serviceId : "chis.manualPerformanceService",
                serviceAction : "saveManualPerformance",
                method:"execute",
                body : {
                    data : values
                }
            }, function(code, msg, json) {
                this.form.el.unmask()
                if (code < 300) {
                  	this.doCancel();
					this.fireEvent("afterSave");
                } else {
                    this.processReturnMsg(code, msg)
                }
            }, this)
            
            //发送短信
            debugger;
            var phone = this.personInfo.data.mobileNumber;
            var operatorUnit_text = this.personInfo.data.operatorUnit_text;
            var serviceItemsId = values.serviceItemsId;
            var serviceItems = values.serviceItems;      
            var serviceDesc = values.serviceDesc;
            if(serviceItemsId == '10202'){
                util.rmi.miniJsonRequestAsync({
    				serviceId : "phis.messageService",
    				serviceAction : "sendJYLYMessage",
    				method : "execute",
    				body:{phone:phone, serviceItems:serviceItems, operatorUnit_text:operatorUnit_text, serviceDesc:serviceDesc}
    			}, function(code, msg, json) {	
    				if(json && json.body){
    					if(json.body.successSend!=""){
    						MyMessageTip.msg("提示", '已短信通知履约人（'+json.body.successSend+"） ", true);
    					}
    					if(json.body.failSend!=""){
    						MyMessageTip.msg("提示", '短信通知失败：'+json.body.failSend, true);
    					}
    				}
    			}, this);
            }     
        }
    })