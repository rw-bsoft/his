$package("phis.application.cfg.script")
/**
 * 给药途径维护from zhangyq 2012.05.25
 * 
 */
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigDoseRoadForm = function(cfg) {
	cfg.colCount = 1;
	cfg.width = 400;
	cfg.remoteUrl = "Project";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{FYMC}</td><td width="70px">{FYDW}</td><td width="20px">{FYDJ}</td>';
	this.serviceId = "configRouteAdministrationService";
	this.actionId = "saveRouteAdministration";
	phis.application.cfg.script.ConfigDoseRoadForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cfg.script.ConfigDoseRoadForm,
		phis.script.TableForm, {
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.actionId,
							op : this.op,
							method : "execute",
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data)
							this.op = "update"
						}, this)// jsonRequest
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'project',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'projectsearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'FYMC'
								}, {
									name : 'FYDW'
								}, {
									name : 'FYDJ'
								}, {
									name : 'FYXH'
								}]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				obj.collapse();
				this.form.getForm().findField("FYMC").setValue(record
						.get("FYMC"));
				this.data["FYXH"] = {
					"text" : record.get("FYMC"),
					"key" : record.get("FYXH")
				};
				// this.form.getForm().findField("FYXH").setValue(record.get("FYXH"));
			},
			// add by caijy for checkbox
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
								if (it.checkValue
										&& it.checkValue.indexOf(",") > -1) {
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
								if (it.dic && v !== "" && v === 0) {// add by
									// yangl
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
				if (data["FYXH"] && data["FYXH"].text != null
						&& data["FYXH"].text != ""
						&& data["FYXH"].text != undefined) {
					form.findField("FYMC").setValue(data["FYXH"].text);
				}
			}
			
		})