$package("phis.application.sup.script")

$import("phis.script.TableForm")

phis.application.sup.script.MaintenanceServiceForSbForm = function(cfg) {
	//cfg.width = 900;
	//cfg.height = 700;
	cfg.colCount = 4;
	cfg.autoLoadData = false;
	cfg.remoteUrl = "MaintenanceServiceForSb";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{WZMC}</td><td width="60px">{WZGG}</td><td width="100px">{WZBH}</td>';
	cfg.minListWidth=450
	phis.application.sup.script.MaintenanceServiceForSbForm.superclass.constructor.apply(this,[cfg])
}
Ext.extend(phis.application.sup.script.MaintenanceServiceForSbForm, phis.script.TableForm, {
			loadData : function() {
					if (this.loading) {
						return
					}
					if (!this.schema) {
						return
					}
					if (!this.initDataId && !this.initDataBody) {
						return;
					}
					if (!this.fireEvent("beforeLoadData", this.entryName, this.initDataId,
							this.initDataBody)) {
						return
					}
					if (this.form && this.form.el) {
						this.form.el.mask("正在载入数据...", "x-mask-loading")
					}
					this.loading = true
					phis.script.rmi.jsonRequest({
						serviceId : "repairRequestrService",
						serviceAction :"querySbForm",
						pkey : this.initDataId
						}, function(code, msg, json) {
						if (this.form && this.form.el) {
							this.form.el.unmask()
						}
						this.loading = false
						if (code > 300) {
							this.processReturnMsg(code, msg, this.loadData)
							return
						}
						if (json.body) {
							this.oper.alertmsg = true;
							this.doNew();
							this.initFormData(json.body);
							this.oper.alertmsg = false;
							this.fireEvent("loadData", this.entryName, json.body);
						}
						if (this.op == 'create') {
							this.op = "update"
						}
					}, this)// jsonRequest
				},
			saveToServer : function(saveData) {
					if (!this.fireEvent("beforeSave", this.entryName, this.op, saveData)) {
						return;
					}
					if(this.WXXH){
						saveData.WXXH = this.WXXH;
					}
					this.saving = true
					this.form.el.mask("正在保存数据...", "x-mask-loading")
					phis.script.rmi.jsonRequest({
								serviceId : "repairRequestrService",
							    serviceAction :"saveform",
								// schema : "WL_WXBG",
								body : saveData,
								op : this.op
							}, function(code, msg, json) {
								this.form.el.unmask()
								this.saving = false
								if (code > 300) {
									this.processReturnMsg(code, msg, this.saveToServer,
											[saveData]);
									return
								}
								Ext.apply(this.data, saveData);
								if (json.body) {
									this.initFormData(json.body)
									this.fireEvent("save", this.entryName, this.op, json,
											this.data)
								}
								this.op = "update"
								MyMessageTip.msg("提示", "保存成功！", true);
								this.fireEvent("refresh", this);
								this.doCancel();
							}, this)
				},
			 getRemoteDicReader : function() {
				  return new Ext.data.JsonReader({
							root : 'mats',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mtssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'WZXH'
								}, {
									name : 'WZMC'
								}, {
									name : 'WZGG'
								}, {
									name : 'WZDW'
								}, {
									name : 'WZBH'
								}, {
									name : 'CJMC'
								}, {
									name : 'CJXH'
								}, {
									name : 'ZBXH'
								}, {
									name : 'GHDW'
								}]);
			},
			// 快速录入后 数据回填
			setBackInfo : function(obj, record) {
				obj.collapse();
				this.form.getForm().findField("WZMC").setValue(record.get("WZMC"));
				this.form.getForm().findField("WZBH").setValue(record.get("WZBH"));
				this.data["ZBXH"] = record.get("ZBXH");
				this.form.getForm().findField("DWMC").setValue(record.get("DWMC"));
				if(record.get("CJMC") == "null" || record.get("CJMC") == ""){
					this.form.getForm().findField("CJMC").setValue("");
				}else{
					this.form.getForm().findField("CJMC").setValue(record.get("CJMC"));
				}	
				if(record.get("WZGG") == "null" || record.get("WZGG") == ""){
					this.form.getForm().findField("WZGG").setValue("");
				}else{
					this.form.getForm().findField("WZGG").setValue(record.get("WZGG"));
				}
				this.form.getForm().findField("WZDW").setValue(record.get("WZDW"));
				this.data["WZXH"] = {
					"text" : record.get("WZMC"),
					"key" : record.get("WZXH")
				};
				this.data["CJXH"] = {
					"text" : record.get("CJMC"),
					"key" : record.get("CJXH")
				};
			}
		})