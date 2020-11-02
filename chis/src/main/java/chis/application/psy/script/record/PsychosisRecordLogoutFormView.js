$package("chis.application.psy.script.record");

$import("chis.script.BizTableFormView");

chis.application.psy.script.record.PsychosisRecordLogoutFormView = function(cfg) {
//	this.autoLoadSchema = cfg.autoLoadSchema;
//	this.autoLoadData = cfg.autoLoadData;
//	this.isCombined = cfg.isCombined;
//	this.showButtonOnTop = cfg.showButtonOnTop;
//	this.colCount = cfg.colCount;
//	this.phrId = cfg.phrId;
	chis.application.psy.script.record.PsychosisRecordLogoutFormView.superclass.constructor.apply(
			this, [cfg]);
	this.entryName = "chis.application.pub.schemas.EHR_WriteOff"; //"PSY_PsychosisRecordLogout";
	this.idList = ["cancellationReason", "cancellationDate",
			"cancellationUser", "deadReason", "deadDate"];
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.psy.script.record.PsychosisRecordLogoutFormView, chis.script.BizTableFormView,
		{
			onWinShow : function() {
				this.doNew();
				this.data.phrId = this.phrId;
			},
			onReady : function() {
				chis.application.psy.script.record.PsychosisRecordLogoutFormView.superclass.onReady
						.call(this);
				var cancellationReason = this.form.getForm()
						.findField("cancellationReason");
				if (cancellationReason){
					cancellationReason.on("select", function(field) {
								var value = field.getValue();
								this.setDeadReason(value);
							}, this);
				}
			},
			setDeadReason : function(value) {
				var deadReason = this.form.getForm().findField("deadReason");
				var deadDate = this.form.getForm().findField("deadDate");
				if (value == "1") {
					deadReason.enable();
					deadReason.allowBlank = false;

					deadDate.enable();
					deadDate.allowBlank = false;
				} else {
					deadReason.reset();
					deadReason.disable();
					deadReason.allowBlank = true;

					deadDate.reset();
					deadDate.disable();
					deadDate.allowBlank = true;
				}
				deadReason.validate();
				deadDate.validate();
			},
			getSchemaItemById : function(id) {
				for (var i = 0; i < this.schema.items.length; i++) {
					var item = this.schema.items[i];
					if (id == item.id)
						return item;
				}
			},
			doSave : function(){
				if (!this.validate()) {
					return
				}
				var values = {};
				var form = this.form.getForm();
				Ext.apply(this.data, this.exContext);
				var n = this.idList.length;
				for (var i = 0; i < n; i++) {
					var it = this.getSchemaItemById(this.idList[i]);
					var v = this.data[it.id] || it.defaultValue;
					if (typeof v == "object") {
						v = v.key;
					}
					var f = form.findField(it.id);
					if (f) {
						v = f.getValue();
					}
					if (v == null || v == "") {
						if (!it.pkey && it["not-null"] && !it.ref) {
							Ext.Msg.alert("提示", it.alias + "不能为空");
							return;
						}
					}
					values[it.id] = v;
				}
				values["phrId"] = this.phrId;
				values["empiId"] = this.empiId;
				values["personName"] = this.personName;
				Ext.apply(this.data, values);
				// 如果是死亡注销，则填写死亡时间
				if (values.cancellationReason == 1){
					values.deadFlag = 'y';
				}
				Ext.Msg.show({
					title : '档案注销',
					msg : '档案注销后将无法操作，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.saveToServer(values);
						}
					},
					scope : this
				});
			},
			saveToServer : function(saveData) {
                var cancellationReason = saveData.cancellationReason;
                if(cancellationReason=='1' || cancellationReason == '2'){
                    //注销原因 为  死亡 或  迁出  调用健康档案注销全部档案
                	Ext.Msg.show({
									title : '确认注销[' + saveData.personName
											+ ']的所有档案',
									msg : saveData.personName
											+ '已死亡或迁出，将同时注销该人所有相关档案，是否继续？',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.YESNO,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "yes") {
											var req = {};
											req.serviceId = "chis.healthRecordService";
											req.serviceAction = "logoutAllRecords";
											req.body = saveData;
											this.logOut(req);
										}
									},
									scope : this
								});
                }else{//子档注销
                	var req = {};
					req.serviceId = "chis.psychosisRecordService";
					req.serviceAction = "logoutFormPsychosisRecord";
					req.body = saveData;
                	this.logOut(req);
                }
			},
			logOut : function(req) {
				this.form.el.mask("正在提交请求，请稍候...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : req.serviceId,
							serviceAction : req.serviceAction,
							method:"execute",
							body : req.body
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code < 300) {
								this.doCancel();
								this.fireEvent("writeOff",this.entryName,this.op,json,this.data);
							} else {
								this.processReturnMsg(code, msg);
							}
                             this.fireEvent("writeOff", this.entryName, this.op,
                                        json, this.data);
							 this.getWin().hide();
						}, this);
			},
			/*saveToServer : function(saveData) {
				saveData["status"] = "1";
				// 如果是死亡注销，则填写死亡时间
				if (saveData.cancellationReason == 1)
					saveData.deadFlag = '1';
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				// if(this.initDataId == null){
				// this.op = "create";
				// }
				this.saving = true;
				this.form.el.mask("正在保存数据...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : "chis.psychosisRecordService",
							serviceAction : "",
							body : saveData,
							op : "update",
							schema : "PSY_PsychosisRecord"
						}, function(code, msg, json) {
							this.form.el.unmask();
							this.saving = false;
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);

							// this.initFormData(json.body)
							this.doCancel();
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);

							this.op = "update";
						}, this);// jsonRequest
			},*/
			
			doCancel : function() {
				this.getWin().hide();
			},
			
			getWin : function() {
				var win = this.win;
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								autoHeight : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								autoScroll : true,
								minimizable : true,
								maximizable : true,
								constrain : true,
								shadow : false,
								buttonAlign : 'center',
								modal : true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				win.instance = this;
				return win;
			}
		});