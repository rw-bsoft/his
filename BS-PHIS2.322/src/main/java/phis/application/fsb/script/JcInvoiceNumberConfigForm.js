﻿$package("phis.application.fsb.script")

$import("phis.script.TableForm")

phis.application.fsb.script.JcInvoiceNumberConfigForm = function(cfg) {
	cfg.width = 750;
	phis.application.fsb.script.JcInvoiceNumberConfigForm.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.fsb.script.JcInvoiceNumberConfigForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.fsb.script.JcInvoiceNumberConfigForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				this.DQHM = form.findField("DQHM");
				this.QSHM = form.findField("QSHM");
				if (this.op == 'update') {
					if (this.DQHM) {
						this.DQHM.on("focus", this.onDQHMFocus, this);
					}
				}
				if (this.op == 'create') {
					if (this.QSHM) {
						this.QSHM.on("blur", this.onQSHMBlur, this);
					}
				}
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
						if (it.id != "DQHM") {
							f.setDisabled("true");
						}
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
								if (it.dic && v == 0) {// add by yangl
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
			},
			onDQHMFocus : function() {
				this.DQHMVLAUE = this.DQHM.getValue();
			},
			onQSHMBlur : function() {
				this.DQHM.setValue(this.QSHM.getValue());
			},
			onBeforeSave : function(entryName, op, saveData) {
				var sfqyfpgycs = phis.script.rmi.miniJsonRequestSync({
					serviceId : "jcInvoiceNumberConfigService",
					serviceAction : "queryZyfpCount",
					schemaDetailsList : "JC_YGPJ",
				});
				if (sfqyfpgycs.code == 607) {
					MyMessageTip.msg("提示", sfqyfpgycs.msg, true);
					return false;
				}
				var s = /^[a-zA-Z0-9]+$/i;
				if (!s.test(saveData.QSHM) && saveData.QSHM != '') {
					MyMessageTip.msg("提示", "起始号码只能是字母和数字组成,且字母居首", true);
					return false;
				}
				if (!s.test(saveData.ZZHM) && saveData.ZZHM != '') {
					MyMessageTip.msg("提示", "终止号码只能是字母和数字组成,且字母居首", true);
					return false;
				}
				if (!s.test(saveData.DQHM) && saveData.DQHM != '') {
					this.DQHM.setValue(this.DQHMVLAUE);
					MyMessageTip.msg("提示", "使用号码只能是字母和数字组成,且字母居首", true);
					return false;
				}
				// 判断所有号码是否存在
				var dataRepeat = {
					"QSHM" : saveData.QSHM,
					"ZZHM" : saveData.ZZHM,
					"DQHM" : saveData.DQHM
				};
				if (this.initDataId) {
					dataRepeat["JLXH"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "jcInvoiceNumberConfigService",
							serviceAction : "invoiceNumberConfigVerification",
							schemaDetailsList : "JC_YGPJ",
							op : this.op,
							body : dataRepeat
						});
				if (r.code == 608) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 609) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 610) {
					this.DQHM.setValue(this.DQHMVLAUE);
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (saveData.QSHM.length != saveData.ZZHM.length
						&& saveData.QSHM != '' && saveData.ZZHM != '') {
					MyMessageTip.msg("提示", "起始号码和终止号码长度必须一致", true);
					return false;
				}
				if (saveData.QSHM.length != saveData.DQHM.length
						&& saveData.QSHM != '' && saveData.DQHM != '') {
					this.DQHM.setValue(this.DQHMVLAUE);
					MyMessageTip.msg("提示", "起始号码和使用号码长度必须一致", true);
					return false;
				}
				if (saveData.ZZHM.length != saveData.DQHM.length
						&& saveData.ZZHM != '' && saveData.DQHM != '') {
					this.DQHM.setValue(this.DQHMVLAUE);
					MyMessageTip.msg("提示", "终止号码和使用号码长度必须一致", true);
					return false;
				}
				var slenQSHM = 0 ;
				for(var i = 0 ; i < saveData.QSHM.length ; i ++){
					var chaStrQSHM = saveData.QSHM.charAt(i);
					if(chaStrQSHM>='0' && chaStrQSHM<='9'){
						slenQSHM = i;
						break;
					}
				}
				var zmQSHM = saveData.QSHM.substring(0,slenQSHM);
				var szQSHM = saveData.QSHM.substring(slenQSHM);
				if(slenQSHM!=0){
					if(isNaN(szQSHM)){
						MyMessageTip.msg("提示", "当前起始号码不符合规则.", true);
						return false;
					}
				}
				var slenZZHM = 0 ;
				for(var i = 0 ; i < saveData.ZZHM.length ; i ++){
					var chaStrZZHM = saveData.ZZHM.charAt(i);
					if(chaStrZZHM>='0' && chaStrZZHM<='9'){
						slenZZHM = i;
						break;
					}
				}
				var zmZZHM = saveData.ZZHM.substring(0,slenZZHM);
				var szZZHM = saveData.ZZHM.substring(slenZZHM);
				if(slenZZHM!=0){
				if(isNaN(szZZHM)){
					MyMessageTip.msg("提示", "当前终止号码不符合规则.", true);
					return false;
				}
				}
				var slenDQHM = 0 ;
				for(var i = 0 ; i < saveData.DQHM.length ; i ++){
					var chaStrDQHM = saveData.DQHM.charAt(i);
					if(chaStrDQHM>='0' && chaStrDQHM<='9'){
						slenDQHM = i;
						break;
					}
				}
				var zmDQHM = saveData.DQHM.substring(0,slenDQHM);
				var szDQHM = saveData.DQHM.substring(slenDQHM);
				if(slenDQHM!=0){
				if(isNaN(szDQHM)){
					MyMessageTip.msg("提示", "当前使用号码不符合规则.", true);
					return false;
				}
				}
				if(slenQSHM!=0&&slenZZHM!=0&&slenDQHM!=0){
				if(zmQSHM!=zmZZHM||zmZZHM!=zmDQHM||zmQSHM!=zmDQHM){
					MyMessageTip.msg("提示", "三个号码的字母应该保持一致.", true);
					return false;
				}
				}
				if (saveData.QSHM> saveData.DQHM
						&& saveData.QSHM != '' && saveData.DQHM != '') {
					this.DQHM.setValue(this.DQHMVLAUE);
					MyMessageTip.msg("提示", "起始号码不能大于使用号码", true);
					return false;
				}
				if (saveData.ZZHM < saveData.DQHM
						&& saveData.ZZHM != '' && saveData.DQHM != '') {
					this.DQHM.setValue(this.DQHMVLAUE);
					MyMessageTip.msg("提示", "终止号码 不能小于使用号码", true);
					return false;
				}
				// 判断号码是否段冲突
				var dataConflict = {
					"QSHM" : saveData.QSHM,
					"ZZHM" : saveData.ZZHM,
					"DQHM" : saveData.DQHM
				};
				if (this.initDataId) {
					dataConflict["JLXH"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "jcInvoiceNumberConfigService",
							serviceAction : "invoiceNumberConflictVerification",
							schemaDetailsList : "JC_YGPJ",
							op : this.op,
							body : dataConflict
						});
				if (r.code >= 300) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							op : this.op,
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
							this.DQHMVLAUE = this.DQHM.getValue();
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "update"
						}, this)
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
					var f = form.findField(it.id)
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
						if (it.id == "LYRQ") {
							f.setValue(new Date().format('Y-m-d H:i:s'));
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
				this.focusFieldAfter(-1, 800)
				this.validate()
			}
		})