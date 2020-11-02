$package("phis.application.ivc.script");

$import("phis.script.TableForm");

phis.application.ivc.script.ClinicPhysicalMrForm = function(cfg) {
	phis.application.ivc.script.ClinicPhysicalMrForm.superclass.constructor.apply(
			this, [cfg]);
};
Ext.extend(phis.application.ivc.script.ClinicPhysicalMrForm,
		phis.script.TableForm, {
	/**
	 * 重写父类doSave方法，将获取数据部分代码独立出来，方便独立调用
	 */
	doSave : function() {
		if (this.saving) {
			return
		}
		var values = this.getFormData();
		if (!values) {
			return;
		}
		Ext.apply(this.data, values);
		this.saveToServer(values);
	},

	/**
	 * 保存数据到后台数据库，请求中增加参数serviceAction，指定调用服务中的哪个方法
	 * 
	 * @param {}
	 *            saveData 需要保存的数据
	 */
	saveToServer : function(saveData) {
		this.saving = true;
		this.form.el.mask("正在保存数据...", "x-mask-loading")
		phis.script.rmi.jsonRequest({
					serviceId : "clinicChargesProcessingService",	
					serviceAction : "savePhysicalMr",
					method : "execute",
					op : this.op,
					schema : this.entryName,
					body : saveData
				}, function(code, msg, json) {
					this.form.el.unmask()
					this.saving = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData], json.body);
						return
					}
					Ext.apply(this.data, saveData);
					if (json.body) {
						this.initFormData(json.body)
						this.fireEvent("save", this.entryName, this.op, json,
								this.data)
					}
					this.op = "update"
					MyMessageTip.msg("提示", "保存成功!", true)
				}, this)// jsonRequest
	},
	loadData : function() {
		var key="GRXX_MR";
		phis.script.rmi.jsonRequest({
			serviceId : "clinicChargesProcessingService",	
			serviceAction : "queryPhysicalMr",
			method : "execute",
			body:key,
		}, function(code, msg, json) {
			if (code > 300) {
			}else{
				this.mrData=json;
				this.initFormData(this.mrData);
			}
		}, this);
	},
	initFormData : function(data) {
		Ext.apply(this.data, data)
		this.initDataId = this.data[this.schema.pkey]
		var form = this.form.getForm()
		var items = this.schema.items
		var n = items.length
		for (var i = 0; i < n; i++) {
			var it = items[i]
			var f = form.findField(it.id)
			if (f) {
				var v = data[it.id]
				if (v != undefined) {
					if (f.getXType() == "checkbox") {
						var setValue = "";
						if (it.checkValue && it.checkValue.indexOf(",") > -1) {
							var c = it.checkValue.split(",");
							checkValue = c[0];
							unCheckValue = c[1];
							if (v == checkValue) {
								setValue = true;
							} else if (v == unCheckValue) {
								setValue = false;
							}
						}
						if (setValue == "") {
							if (v == 1) {
								setValue = true;
							} else {
								setValue = false;
							}
						}
						f.setValue(setValue);
					} else {
						if (it.dic && v !== "" && v === 0) {// add by yangl
							// 解决字典类型值为0(int)时无法设置的BUG
							v = "0";
						}
						f.setValue(v)
						if (it.dic && v != "0" && f.getValue() != v) {
							f.counter = 1;
							this.setValueAgain(f, v, it);
						}

					}
				}
				if (it.update == "false") {
					f.disable();
				}
			}
			this.setKeyReadOnly(true)
			this.focusFieldAfter(-1, 800)
		}
	}
});