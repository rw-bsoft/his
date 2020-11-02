$package("chis.application.fhr.script")

$import("chis.script.BizTableFormView")

chis.application.fhr.script.PersonnalContractForm = function(cfg) {
	chis.application.fhr.script.PersonnalContractForm.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.fhr.script.PersonnalContractForm,
		chis.script.BizTableFormView, {
			loadinfobyidcard:function(f){
				if(f.length <18){
					MyMessageTip.msg("提示", "身份证号位数不正确", true);
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.healthRecordService",
					serviceAction : "getjmqyinfobyidcard",
					method:"execute",
					idcard : f
				})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				this.initFormData(result.json.data)
			},
			idcardEnter:function(){
				var idcard = this.form.getForm().findField("idCard");
				idcard.on("specialkey", function(f, e){ if (e.getKey() == e.ENTER) {
										this.loadinfobyidcard(f.getValue());
								}}, this);
			},
			setFormcolumndisable:function(){
				this.form.getForm().findField("idCard").disable();
				this.form.getForm().findField("FC_Party").disable();
				this.form.getForm().findField("FC_Party2").disable();
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
						if (v != undefined || this.flag == true) {
							if (it.dic && v !== "" && v === 0) {// add by
								v = "0";
							}
							f.setValue(v)
							if (it.dic && v != "0" && f.getValue() != v) {
								f.counter = 1;
							}
						}
					} else {
						var v = data[it.id]
						if (v != undefined || this.flag == true) {
							if (undefined == v) {
								v = ""
							}
							if (it.tag == "text") {
								this.setValueById(it.id, v)
							} else if (it.tag == "radioGroup") {
								if (it.controValue == v) {
									var strId = it.controId;
									var s = strId.indexOf(",");

									if (s == -1) {
										this.getObj(strId).disabled = false;
									} else {
										this.getObj(strId.substring(0, s)).disabled = false;// s处不要
										this.getObj(strId.substring(s + 1,
												strId.length)).disabled = false;
									}
								}
								this.setRadioValueMy(it.id, v)
							} else if (it.tag == "checkBox") {
								this.setCheckBoxValuesMy(it.id, v)
							} else if (it.tag == "selectgroup") {
								this.setSelectValues(it.id, v)
							}
						}
					}
					this.setKeyReadOnly(true)
				}
			},
			doSave:function(){
				debugger;
				this.form.el.mask("正在校验数据...", "x-mask-loading")
				var values = this.getFormData();
				values.FS_EmpiId=values.FC_Repre;
				values.FS_Id=values.FC_Id;
				var body=[];
				body.push(values);
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.healthRecordService",
					serviceAction : "savePsersonnalContract",
					method:"execute",
					body : body
				})
				this.form.el.unmask();
				if (result.code != 200) {
					MyMessageTip.msg("提示", "错误代码"+data.code+"错误信息"+data.msg, true);
					return;
				} else {
					this.win.close();
					this.fireEvent("save");
				}
			}
		});