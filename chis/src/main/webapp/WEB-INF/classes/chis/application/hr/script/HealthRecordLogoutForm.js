/**
 * 个人健康档案注销页面
 * 
 * @author : tianj
 */
$package("chis.application.hr.script")

$import("chis.script.BizTableFormView")

chis.application.hr.script.HealthRecordLogoutForm = function(cfg) {
	cfg.autoLoadSchema = false
	cfg.entryName = "chis.application.pub.schemas.EHR_WriteOff";
	cfg.saveServiceId = "chis.healthRecordService";
	cfg.saveAction = "logoutAllRecords";
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.hr.script.HealthRecordLogoutForm.superclass.constructor.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("save", this.onSave, this);
}

Ext.extend(chis.application.hr.script.HealthRecordLogoutForm, chis.script.BizTableFormView, {
	onWinShow : function() {
		this.doNew();
	    this.setDeadReason(0);
		this.validate();
	},

	onReady : function() {
		chis.application.hr.script.HealthRecordLogoutForm.superclass.onReady.call(this);

		var cancellationReason = this.form.getForm()
				.findField("cancellationReason");
		if (cancellationReason) {
			cancellationReason.on("select", function(field) {
						var value = field.getValue();
						this.setDeadReason(value);
					}, this);
		}
	},

	doSave : function() {
		if (this.saving) {
			return
		}
		var values = this.getFormData();
		if (!values) {
			return;
		}
		values["empiId"] = this.empiId;
		var msg ='['+this.personName + ']的健康档案及子档将全部被注销，确定是否继续？';
		if(values["cancellationReason"]=="1"){
			msg ='['+this.personName + ']的健康档案及子档将全部被注销、以及所有的家庭医生签约记录将被解约，确定是否继续？';
		}
		Ext.Msg.show({
					title : '档案注销',
					msg : msg,
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							Ext.apply(this.data, values);
							if(values["cancellationReason"]=="1"){
								if(this.cancelAllSignRecord(values["empiId"])){
									this.saveToServer(values);
								}
							}else{
								this.saveToServer(values);
							}
						}
					},
					scope : this
				});
	},
	cancelAllSignRecord : function(empiid){	
		var res = util.rmi.miniJsonRequestSync({
			serviceId : "chis.signContractRecordService",
			serviceAction : "cancelAllSignRecord",
			method:"execute",
			body : {
				empiid : empiid
			}
		});
		if (res.code > 300) {
			MyMessageTip.msg("提示", res.msg, true);
			return false;
		}
		return true;
	},
	getSaveRequest : function(saveData) {
		saveData.phrId = this.phrId;
		if (saveData.cancellationReason == "1") {
			saveData.deadFlag = "y"
		}
		return saveData;
	},

	onSave : function() {
		this.fireEvent("remove");
		this.getWin().hide();
	},

	setDeadReason : function(value) {
		var items = this.schema.items;
		var form = this.form.getForm();
		
		if (value == 1) {// @@ 恢复原必填项。
			for (var i = 0; i < items.length; i++) {
				var field = form.findField(items[i].id);
				if (items[i].id == "deadReason" || items[i].id == "deadDate") {
					field.allowBlank = false;
					field.enable();
					items[i]["not-null"] = true;
					Ext.getCmp(field.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>" + items[i].alias
									+ ":</span>");
				}
			}
		}else{
			for (var i = 0; i < items.length; i++) {
				if (items[i].id == 'cancellationReason') {
					continue;
				}
				if (items[i].id == "deadReason" || items[i].id == "deadDate") {
					var field = form.findField(items[i].id);
					field.setValue();
				}

				var field = form.findField(items[i].id);
				if (field) {
					field.allowBlank = true;
					field.disable();
					items[i]["not-null"] = false;
					Ext.getCmp(field.id).getEl().up('.x-form-item')
							.child('.x-form-item-label').update(items[i].alias
									+ ":");
				}
			}
		}
		this.validate();
	}
});