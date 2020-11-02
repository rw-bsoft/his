$package("phis.application.pha.script")

$import("phis.script.TableForm")

phis.application.pha.script.PharmacyPrescriptionEntryForm = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.pha.script.PharmacyPrescriptionEntryForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyPrescriptionEntryForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.pha.script.PharmacyPrescriptionEntryForm.superclass.onReady
						.call(this);
				var dic_ks;
				// ksdm.setDisabled(false);
				var items = this.schema.items;
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (it.id == 'KSDM') {
						dic_ks = it.dic;
					}
				}
				var ksdm = this.form.getForm().findField("KSDM");
				var ysdm = this.form.getForm().findField("YSDM");
				ksdm.getStore().on("load", function() {
							if (this.ksdm) {
								ksdm.setValue(this.ksdm);
								ksdm.triggerBlur();
							} else {
								// ksdm.setValue(this.mainApp['phis'].departmentId);
							}
							if (this.opener.list && ksdm.getValue()) {
								this.opener.list.setKSDM(ksdm.getValue());
							}
						}, this);
				ksdm.on("select", function() {
							if (this.opener.list) {
								this.opener.list.setKSDM(ksdm.getValue());
							}
						}, this);
				ysdm.on("select", function() {
					var ysId = ysdm.getValue();
					this.opener.list.setYSDM(ysId);
					phis.script.rmi.jsonRequest({
								serviceId : "changeDoctorOrDepartmentService",
								serviceAction : "findKsdmByYsdm",
								body : {
									ysId : ysId
								}
							}, function(code, msg, json) {
								var arr1 = json.ksdm;
								this.ksdm = ksdm.getValue();
								ksdm.setValue("");
								ksdm.store.removeAll();
								// ksdm.clearValue();
								var filters1 = "['and',['in',['$','item.properties.ID','i'],"
										+ arr1
										+ "],['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]]";
								dic_ks.id = "phis.dictionary.department_leaf";
								dic_ks.filter = filters1;
								ksdm.store.proxy = new util.dictionary.HttpProxy(
										{
											method : "GET",
											url : util.dictionary.SimpleDicFactory
													.getUrl(dic_ks)
										})
								ksdm.store.load();
							}, this)
				}, this);
			},
			setCFLX : function(type) {
				var form = this.form.getForm();
				form.findField("CFLX").setValue(type);
				var CFTS = form.findField("CFTS");
				CFTS.hide();
				if (type == 3) {
					CFTS.show();
				} else {
					CFTS.hide();
				}
			},
			doReset : function() {
				if (this.opener.Brxx && this.opener.list.hasModify()) {
					Ext.Msg.confirm("确认", "当前数据发生修改，确认要重置吗？", function(btn) {
								if (btn == 'yes') {
									this.form.getForm().reset();
									this.form.getForm().findField("JZKH")
											.setDisabled(false);
									this.opener.list.store.removeAll();
									this.opener.afterOpen();
								}
							}, this);
				} else {
					this.form.getForm().reset();
					this.form.getForm().findField("JZKH").setDisabled(false);
					this.opener.list.store.removeAll();
					this.opener.afterOpen();
				}
			},
			doClose : function() {
				if (this.opener.Brxx && this.opener.list.hasModify()) {
					Ext.Msg.confirm("确认", "当前数据发生修改，确认要关闭吗？", function(btn) {
								if (btn == 'yes') {
									this.opener.opener.closeCurrentTab();
								}
							}, this);
				} else {
					this.opener.opener.closeCurrentTab();
				}

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
					// this.setKeyReadOnly(true)
				}
			}

		});