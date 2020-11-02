$package("phis.application.fsb.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.fsb.script.FamilySickBedSettlementManagementQueryForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = false;
	cfg.labelWidth = 55;
	phis.application.fsb.script.FamilySickBedSettlementManagementQueryForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedSettlementManagementQueryForm,
		phis.script.TableForm, {
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
							f.setValue(v)
							if (it.dic && v != "0" && f.getValue() != v) {
								f.counter = 1;
								this.setValueAgain(f, v, it);
							}
						}
						f.disable();
					}
				}
				this.setKeyReadOnly(true)
			},
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.ldata) {
					return;
				}
				if (this.form.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.data = this.ldata;
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : "familySickBedPatientSelectionService",
							serviceAction : "querySelectionForm",
							body : this.ldata
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
//								this.opener.doNew(this.data.JSLX);
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								this.body = json.body;
								this.doNew()
								this.initFormData(json.body)
								// 计算域1=余款;计算域2=天数;计算域3=结算日期
								// 动态标签1 = 余款;动态标签2 = 结算日期
								var form = this.form.getForm();
								var JSJE = form.findField('JSJE')// 计算域1
								var JKHJ = form.findField('JKHJ')
								var JKHJField = JKHJ.el.parent().parent()
										.first();
								JKHJField.dom.innerHTML = "预缴款:";
								var JSJEField = JSJE.el.parent().parent()
										.first();// 动态标签1
								if ((json.body.JKHJ-json.body.ZFHJ)<=0) {
									JSJE.setValue(Math.abs(json.body.ZFHJ
											- json.body.JKHJ));
									JSJEField.dom.innerHTML = "结算缴款:";
								} else {
									JSJE.setValue(Math.abs(json.body.ZFHJ
											- json.body.JKHJ));
									JSJEField.dom.innerHTML = "结算找退:";
//									JSJE.setValue(json.body.ZFHJ
//											- json.body.JKHJ);
								}
								var ZYTS = form.findField('ZYTS')// 计算域2
								if (this.data.JSLX == 0) {
									ZYTS.setValue(this.DaysAfter(
											json.body.RYRQ, json.body.XTRQ));
								} else if (this.data.JSLX == 5) {
									ZYTS.setValue(this.DaysAfter(
											json.body.RYRQ, json.body.CYRQ));
								} else {
									ZYTS.setValue(this.DaysAfter(
											json.body.RYRQ, null));
								}
								var JSRQ = form.findField('JSRQ')// 计算域3
								if (this.data.JSLX == 0) {
									JSRQ.setValue(json.body.CYRQ);
								} else {
									JSRQ.setValue(json.body.JSRQ);
									if (this.data.JSLX == "-1") {
										JSRQ.setValue();
									}
								}
								this.opener.body = this.body;
//								this.fireEvent("loadData", this.opener);
							}
						}, this)// jsonRequest
			},
			DaysAfter : function(sDate1, sDate2) {
				var oDate1, oDate2, iDays
				var navigatorName = "Microsoft Internet Explorer";
				if (navigator.appName == navigatorName) {
					oDate1 = new Date(Date.parse(sDate1.substring(0, 10)
							.replace(/-/, "/")))
				} else {
					oDate1 = new Date(sDate1.substring(0, 10))
				}
				if (sDate2) {
					if (navigator.appName == navigatorName) {
						oDate2 = new Date(Date.parse(sDate2.substring(0, 10)
								.replace(/-/, "/")));
					} else {
						oDate2 = new Date(sDate2.substring(0, 10))
					}
				} else {
					oDate2 = new Date();
				}
				iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60
						/ 24)
				this.body.ZYTS = iDays;

				return iDays
			}
		});