$package("phis.application.ivc.script")

$import("phis.script.TableForm")

phis.application.ivc.script.InvoiceNumberForm = function(cfg) {
	cfg.width = 750;
	phis.application.ivc.script.InvoiceNumberForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.ivc.script.InvoiceNumberForm, phis.script.TableForm, {
			onReady : function() {
				phis.application.ivc.script.InvoiceNumberForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				this.SYHM = form.findField("SYHM");
				this.QSHM = form.findField("QSHM");
				if (this.op == 'update') {
					if (this.SYHM) {
						this.SYHM.on("focus", this.onSYHMFocus, this);
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
						if (it.id != "SYHM") {
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
			onSYHMFocus : function() {
				this.SYHMVLAUE = this.SYHM.getValue();
			},
			onQSHMBlur : function() {
				this.SYHM.setValue(this.QSHM.getValue());
			},
			onBeforeSave : function(entryName, op, saveData) {
				// 判断所有号码是否存在
				var dataRepeat = {
					"QSHM" : saveData.QSHM,
					"ZZHM" : saveData.ZZHM,
					"SYHM" : saveData.SYHM
				};
				if (this.initDataId) {
					dataRepeat["JLXH"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "invoiceNumberService",
							serviceAction : "invoiceNumberVerification",
							schemaDetailsList : "MS_YGPJ",
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
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				
				if (saveData.QSHM.length != saveData.ZZHM.length
						&& saveData.QSHM != '' && saveData.ZZHM != '') {
					MyMessageTip.msg("提示", "起始号码和终止号码长度必须一致", true);
					return false;
				}
				if (saveData.QSHM.length != saveData.SYHM.length
						&& saveData.QSHM != '' && saveData.SYHM != '') {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", "起始号码和使用号码长度必须一致", true);
					return false;
				}
				if (saveData.ZZHM.length != saveData.SYHM.length
						&& saveData.ZZHM != '' && saveData.SYHM != '') {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", "终止号码和使用号码长度必须一致", true);
					return false;
				}
				var slenQSHM = 0 ;
				var qshmreverse=this.goreverse(saveData.QSHM);
				for(var i = 0 ; i < qshmreverse.length ; i ++){
					var chaStrQSHM = qshmreverse.charAt(i);
					if(isNaN(chaStrQSHM)){
						slenQSHM = i;
						break;
					}
				}
				var zmQSHM = qshmreverse.substring(slenQSHM);
				if(slenQSHM!=0){
				if(isNaN(qshmreverse.substring(0, 1))){
					MyMessageTip.msg("提示", "起始号码的最后一位必须是数字", true);
					return false;
				}
				}
				var slenZZHM = 0 ;
				var zzhmreverse=this.goreverse(saveData.ZZHM);
				for(var i = 0 ; i < zzhmreverse.length ; i ++){
					var chaStrZZHM = zzhmreverse.charAt(i);
					if(isNaN(chaStrZZHM)){
						slenZZHM = i;
						break;
					}
				}
				
				var zmZZHM = zzhmreverse.substring(slenZZHM);
				if(slenZZHM!=0){
				if(isNaN(zzhmreverse.substring(0, 1))){
					MyMessageTip.msg("提示", "终止号码的最后一位必须是数字", true);
					return false;
				}
				}
				var slenSYHM = 0 ;
				var syhmreverse=this.goreverse(saveData.SYHM);
				for(var i = 0 ; i < syhmreverse.length ; i ++){
					var chaStrSYHM = syhmreverse.charAt(i);
					if(isNaN(chaStrSYHM)){
						slenSYHM = i;
						break;
					}
				}
				
				var zmSYHM = syhmreverse.substring(slenSYHM);
				if(slenSYHM!=0){
				if(isNaN(syhmreverse.substring(0, 1))){
					MyMessageTip.msg("提示", "使用号码的最后一位必须是数字", true);
					return false;
				}
				}
				if(slenQSHM!=0&&slenZZHM!=0&&slenSYHM!=0){
				if(zmQSHM!=zmZZHM||zmZZHM!=zmSYHM||zmQSHM!=zmSYHM){
					MyMessageTip.msg("提示", "三个号码的规则应该保持一致.", true);
					return false;
				}
				}
				if (saveData.QSHM > saveData.SYHM
						&& saveData.QSHM != '' && saveData.SYHM != '') {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", "起始号码不能大于使用号码", true);
					return false;
				}
				if (saveData.ZZHM < saveData.SYHM
						&& saveData.ZZHM != '' && saveData.SYHM != '') {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", "终止号码 不能小于使用号码", true);
					return false;
				}
				// 判断号码是否段冲突
				var dataConflict = {
					"QSHM" : saveData.QSHM,
					"ZZHM" : saveData.ZZHM,
					"SYHM" : saveData.SYHM
				};
				if (this.initDataId) {
					dataConflict["JLXH"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "invoiceNumberService",
							serviceAction : "invoiceNumberRangeVerification",
							schemaDetailsList : "MS_YGPJ",
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
							this.SYHMVLAUE = this.SYHM.getValue();
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
			},
			goreverse:function(str){
				var result = ""; 
				for(var i = str.length; i > 0; i--){ 
				    result += str.charAt(i-1); 
				}
				return result;
			}
		})