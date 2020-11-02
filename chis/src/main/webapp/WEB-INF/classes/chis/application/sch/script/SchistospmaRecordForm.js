$package("chis.application.sch.script");

$import("chis.script.BizTableFormView");

chis.application.sch.script.SchistospmaRecordForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 125;
	cfg.labelWidth = 110;
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.isCombined = true;
	cfg.showButtonOnTop = true;
	chis.application.sch.script.SchistospmaRecordForm.superclass.constructor.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
	this.on("beforeSave", this.onBeforeSave, this);

};

Ext.extend(chis.application.sch.script.SchistospmaRecordForm, chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.sch.script.SchistospmaRecordForm.superclass.onReady.call(this);
				var checkType = this.form.getForm().findField("checkType");
				if (checkType) {
					checkType.on("select", this.checkType, this)
					checkType.on("blur", this.checkType, this)
				}
			},

			checkType : function(checkType) {
				var value = checkType.getValue();
				this.removecheckTypeField(value);
			},

			removecheckTypeField : function(value) {
				var form = this.form.getForm();
				var filed = ["shitCheckResult"];
				for (var i = 0; i < filed.length; i++) {
					var item = filed[i];
					var f = form.findField(item);
					if (f) {
						if (value == "1") {
							f.enable();
						} else {
							f.setValue("");
							f.disable();
						}
					}
				}
			},

			doClose : function() {
				if (!this.initDataId) {
					return
				}
				Ext.Msg.show({
							title : '结案确认',
							msg : '结案操作后无法恢复，是否继续？',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									util.rmi.jsonRequest({
												serviceId : this.saveServiceId,
												serviceAction : this.closeAction,
												method:"execute",
												body : {
													schisRecordId : this.initDataId
												}
											}, function(code, msg, json) {
												if (code > 300) {
													this.processReturnMsg(code,
															msg);
													return
												} else {
													// this.setButton([0, 1],
													// false);
													this.fireEvent("close",
															this.entryName,
															this.op, json,
															this.data);
												}
											}, this)
								}
							},
							scope : this
						})
			},

			doSave : function() {
				chis.application.sch.script.SchistospmaRecordForm.superclass.doSave.call(this);
			},

			onLoadData : function(body) {
				var checkType = body.checkType;
				if (checkType) {
					this.removecheckTypeField(checkType.key);
				}
				if (this.readOnly) {
					return;
				}
				if (body.status) {
					this.status = body.status.key;
				}
				if (body.closeFlag) {
					this.flag = body.closeFlag.key;
				}
				if (this.status == 1 || this.flag == 1) {
					this.setButton([0, 1], false);
				} else {
					this.setButton([0, 1], true);
					if (this.flag == 0) {
						this.setButton([2], false);
					} else {
						this.setButton([2], true);
					}
				}
				if (this.status == 1 && body.cancellationReason.key != 6) {
					this.setButton([2], false);
				} else {
					this.setButton([2], true);
				}
				if (!this.exContext.args.doFlag) {
					this.setButton([0, 1, 2], false);
				}
			},

			doNew : function() {
				this.empiId = this.exContext.ids.empiId;
				this.phrId = this.exContext.ids.phrId;
				this.initDataId = null;
				chis.application.sch.script.SchistospmaRecordForm.superclass.doNew.call(this);
				this.setButton([0], true);
			},

			doAdd : function() {
				if (this.empiId) {
					util.rmi.jsonRequest({
								serviceId : this.saveServiceId,
								serviceAction : this.addAction,
								method:"execute",
								empiId : this.empiId
							}, function(code, msg, json) {
								if (code > 300) {
									this.processReturnMsg(code, msg);
									return
								} else {
									if (json.body) {
										var code = json.body.code;
										var msg = json.body.msg;
										if (code != 200) {
											Ext.Msg.alert("提示信息", msg);
											return
										} else {
											// this.initDataId = null;
											this.doNew();
											// this.setButton([0], true);
											this.fireEvent("create")
										}
									}
								}
							}, this)
				}
			},

			setButton : function(m, flag) {
				var btns;
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
					btns = this.form.getTopToolbar().items;
				} else {
					btns = this.form.buttons;
				}

				if (btns) {
					var n = btns.getCount();
					for (var i = 0; i < m.length; i++) {
						var btn = btns.item(m[i]);
						if (btn) {
							(flag) ? btn.enable() : btn.disable();
						}
					}
				}
			},

			onBeforeSave : function(entryName, op, saveData) {
				saveData.phrId = this.phrId;
				saveData.empiId = this.empiId;
				return saveData;
			},

			afterSaveData : function(entryName, op, json, data) {
				this.setButton([1], true);
				this.fireEvent("save", entryName, op, json, data);
			},

			loadData : function() {
				this.doNew();
				var data = this.exContext.args.data;
				if (data) {
					data = this.castListDataToForm(data, this.schema);
					this.initFormData(data);
					this.exContext.args.status = data.status.key;
					this.onLoadData(data);
				} else {
					var doFlag = this.exContext.args.doFlag;
					if (doFlag) {
						this.setButton([0, 2], true);
					} else {
						this.setButton([0, 1, 2], false);
					}
				}
			}
		})