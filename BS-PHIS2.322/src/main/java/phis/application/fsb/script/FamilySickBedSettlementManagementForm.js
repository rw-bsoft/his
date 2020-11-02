$package("phis.application.fsb.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.fsb.script.FamilySickBedSettlementManagementForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = false;
	cfg.labelWidth = 55;
	cfg.remoteUrl = 'YBDisease';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{JBMC}</td></td>';
	cfg.minListWidth = 250;
	phis.application.fsb.script.FamilySickBedSettlementManagementForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedSettlementManagementForm,
		phis.script.TableForm, {
			onReady : function() {

				var form = this.form.getForm();
				var ZYHM = form.findField("ZYHM");

				ZYHM.addEvents("changeLabel");
				ZYHM.on("changeLabel", function(JSLX) {
					this.JSLX = JSLX;
							var ZYHMField = ZYHM.el.parent().parent().first();// 动态标签1
							if (JSLX == 10) {
								ZYHMField.dom.innerHTML = "发票号码:";
								ZYHM.maxLength = 20;
// ZYHM.el.dom.name = "FPHM";
							} else {
// ZYHM.el.dom.name = "ZYHM";
								ZYHMField.dom.innerHTML = "家床号码:";
								ZYHM.maxLength = 10;
							}
						}, this);

				ZYHM.un("specialkey", this.onFieldSpecialkey, this)
				ZYHM.on("specialkey", function(ZYHM, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								if (ZYHM.getValue()) {
									var data = {};
									if (this.JSLX == 10) {
										data.key = "FPHM";
									} else {
										data.key = ZYHM.getName();
									}
									data.value = ZYHM.getValue();
									this.opener.doQuery(data)
								}
							}
						}, this);
				ZYHM.focus(false, 200);
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
							f.setValue(v)
							if (it.dic && v != "0" && f.getValue() != v) {
								f.counter = 1;
								this.setValueAgain(f, v, it);
							}
						}
						if(it.id!='ZHSYBZ'&&it.id!='CYZDMC'&&it.id!='SSBZ'){
						f.disable();
						}
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
							serviceAction : "getSelectionForm",
							body : this.ldata
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.opener.doNew(this.data.JSLX);
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								json.body.JSLX = this.data.JSLX
								json.body.JSCS = this.data.JSCS
								json.body.JSBS = this.data.JSBS// 将该病人结算类型传值到后台
								if (!json.body.CYRQ) {
									json.body.CYRQ = null;
								} else {
									json.body.CYRQ = json.body.CYRQ;
								}
								if (!json.body.JSRQ) {
									json.body.JSRQ = new Date();
								} else {
									json.body.JSRQ = json.body.JSRQ;
								}
								this.body = json.body;
								this.doNew()
								this.initFormData(json.body)
								// 计算域1=余款;计算域2=天数;计算域3=结算日期
								// 动态标签1 = 余款;动态标签2 = 结算日期
								var form = this.form.getForm();
								var ZFHJ = form.findField('ZFHJ')
								ZFHJ.setValue(this.round2(json.body.ZFHJ,2)+this.round2(json.body.ZHZF,2));
								var JSJE = form.findField('JSJE')// 计算域1
								if (this.data.JSLX == 0 || this.data.JSLX == 5 || this.data.JSLX == 4
										|| this.data.JSLX == 1 || this.data.JSLX == 10) {
									JSJE.setValue(Math.abs(json.body.ZFHJ
											- json.body.JKHJ));
								} else {
									JSJE.setValue(json.body.ZFHJ
											- json.body.JKHJ);
								}
								var ZYTS = form.findField('ZYTS')// 计算域2
								if (this.data.JSLX == 0) {
									ZYTS.setValue(this.DaysAfter(
											json.body.RYRQ, json.body.XTRQ));
								} else if (this.data.JSLX == 5) {
									ZYTS.setValue(this.DaysAfter(
											json.body.RYRQ, json.body.CYRQ));
								} else if (this.data.JSLX == 4) {
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
								var JSJEField = JSJE.el.parent().parent()
										.first();// 动态标签1
								if (this.data.JSLX == 0 || this.data.JSLX == 5 || this.data.JSLX == 4
										|| this.data.JSLX == 1 || this.data.JSLX == 10) {
									if ((json.body.ZFHJ - json.body.JKHJ) > 0) {
										JSJEField.dom.innerHTML = "欠款:";
									} else {
										JSJEField.dom.innerHTML = "余款:";
									}
								} else {
									JSJEField.dom.innerHTML = "结算:";
								}
								var JSRQField = JSRQ.el.parent().parent()
										.parent().first(); // 动态标签2
								if (this.data.JSLX == 0) {
									if (json.body.CYPB == 1) {
										JSRQField.dom.innerHTML = "证明日期:";
									} else {
										JSRQField.dom.innerHTML = "结算日期:";
									}
								} else {
									JSRQField.dom.innerHTML = "结算日期:";
								}
								this.opener.body = this.body;
								this.fireEvent("loadData", this.opener);
							}
						}, this)// jsonRequest
			},
			round2 : function(number, fractionDigits) {
				with (Math) {
					return (round(number * pow(10, fractionDigits)) / pow(10,
							fractionDigits)).toFixed(fractionDigits);
				}
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
			},
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = null;
// if(this.JSLX == 10 && i == 0){
// f = form.findField("FPHM");
// }else{
						f = form.findField(it.id);
// }
// if(this.JSLX == 10 && i == 0){
// f.enable();
// }
					
					if (f) {
						f.setValue(it.defaultValue)
						// @@ 2010-01-07 modified by chinnsii, changed the
						// condition
						// "it.update" to "!=false"
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}

						if (it.type == "date") { // ** add by yzh 20100919 **
							if (it.minValue)
								f.setMinValue(it.minValue)
							if (it.maxValue)
								f.setMaxValue(it.maxValue)
						}
						// add by yangl 2012-06-29
						if (it.dic && it.dic.defaultIndex) {
							if (f.store.getCount() == 0)
								return;
							if (isNaN(it.dic.defaultIndex)
									|| f.store.getCount() <= it.dic.defaultIndex)
								it.dic.defaultIndex = 0;
							f.setValue(f.store.getAt(it.dic.defaultIndex)
									.get('key'));
						}
					}
				}
				this.setKeyReadOnly(false)
				this.resetButtons(); // ** add by yzh **
				this.fireEvent("doNew")
				this.focusFieldAfter(-1, 1)
				this.validate()
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'disease',
							totalProperty : 'count',
							id : 'mdssearch_a'
						}, [{
									name : 'numKey'
								}, {
									name : 'JBXH'
								}, {
									name : 'JBMC'

								}, {
									name : 'ICD10'

								}]);
			},
			setBackInfo : function(obj, record) {
				obj.collapse();
				this.data.CYZDBM=record.get("ICD10");
				obj.setValue(record.get("JBMC"));
			}
		});