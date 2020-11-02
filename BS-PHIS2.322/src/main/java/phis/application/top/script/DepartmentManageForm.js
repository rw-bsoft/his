$package("com.bsoft.phis.pub")

$import("com.bsoft.phis.TableForm")

com.bsoft.phis.pub.DepartmentManageForm = function(cfg) {
	cfg.colCount = 3;
	cfg.saveServiceId = "departmentManageService"
	cfg.saveAction = "saveDepartment";
	cfg.serviceAction = "departmentManageNameVerification";
	this.sysData = {};
	com.bsoft.phis.pub.DepartmentManageForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(com.bsoft.phis.pub.DepartmentManageForm, com.bsoft.phis.TableForm, {
	doDepartmentNew : function(node) {
		this.doNew();
		var pid = node.id;
		var ptext = node.text;
		var pjgid = node.attributes.JGID;
		if (pjgid == null)
			pjgid = pid;
		this.data["SJBM"] = pid;
		this.data["JGID"] = pjgid;
		this.data["SJKS"] = pid;
		//Ext.apply(this.sysData, this.data);
	},
	doNew : function() {
		this.op = "create";
		if (this.data) {
			//this.data = {}
			this.data = this.sysData;
		}
		if (!this.schema) {
			return;
		}
		var form = this.form.getForm();
		var items = this.schema.items;
		var n = items.length;
		for (var i = 0; i < n; i++) {
			var it = items[i];
			var f = form.findField(it.id);
			if (f) {
				f.setValue(it.defaultValue);
				if (!it.fixed && !it.evalOnServer) {
					f.enable();
				} else {
					f.disable();
				}
				if (it.type == "date") { // ** add by yzh 20100919 **
					if (it.minValue)
						f.setMinValue(it.minValue);
					if (it.maxValue)
						f.setMaxValue(it.maxValue);
				}
			}
		}
		//this.setKeyReadOnly(false);
		this.resetButtons(); // ** add by yzh **
		this.fireEvent("doNew");
		this.focusFieldAfter(0, 800);
		this.validate();
	},
	onBeforeSave:function(entryName, op, saveData){
				var data = {"KSMC":saveData.KSMC,"SJBM":saveData.SJBM};
					if(this.initDataId){
						data["KSDM"]=this.initDataId;
					}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId :this.saveServiceId,
							serviceAction : this.serviceAction,
							op:this.op,
							body : data
						});
				    		if (r.code==612) {
					 			MyMessageTip.msg("提示", r.msg, true);
					 			return false;
				     		} 
				     		if (r.code==613) {
					 			MyMessageTip.msg("提示", r.msg, true);
					 			return false;
				     		}
			}
})