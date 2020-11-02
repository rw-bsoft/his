$package("phis.application.emr.script")

$import("phis.script.TableForm")

phis.application.emr.script.EMRMedicalRecordTemplatesManageForm = function(cfg) {
	cfg.colCount = 2;
	// cfg.entryName = "phis.application.emr.schemas.EMR_KBM_BLLB_FORM";
	cfg.labelText = "修饰符：'(',')','+','[',']','【','】','-','_','空格','换行符'<br>"
			+ "病历属性:类别名称、模板名称、医生姓名、科室名称、记录日期{yyyy年mm月dd日 hh:mm}<br>"
			+ "<font color='FF0000'><font color='FF0000'>修饰符需要在前后位置都<font color='FF0000'>增加单引号！<br>"
			+ "<font color='000000'>参考：医生<font color='000000'>姓名 + 模板名称 + <font color='000000'>'</font></font></font></font></font></font><font color='FF0000'><font color='FF0000'><font color='FF0000'><font color='000000'><font color='000000'><font color='000000'><font color='000000'>【' + </font></font></font></font></font></font></font><font color='FF0000'><font color='FF0000'><font color='FF0000'><font color='000000'><font color='000000'><font color='000000'><font color='000000'>记录日期{yyyy年mm月dd日 hh:mm} +</font></font></font></font></font></font></font> '】'";
	phis.application.emr.script.EMRMedicalRecordTemplatesManageForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRMedicalRecordTemplatesManageForm,
		phis.script.TableForm, {
			//add by Dingxc
			doNew : function() {
				this.op = "create";
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm();
				form.reset();
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						if (!(arguments[0] == 1)) { // whether set defaultValue, it will
							// be setted when there is no args.
							var dv = it.defaultValue;
							if (dv) {
								if ((it.type == 'date' || it.xtype == 'datefield')
										&& typeof dv == 'string' && dv.length > 10) {
									dv = dv.substring(0, 10);
								}
								f.setValue(dv);
							}
						}
						if (!it.fixed && !it.evalOnServer) {// modify by yangl:remove
							// update
							f.enable();
						}
						// add by yangl 2012-06-29
						if (it.dic && it.dic.defaultIndex) {
							if (f.store.getCount() == 0)
								continue;
							if (isNaN(it.dic.defaultIndex)
									|| f.store.getCount() <= it.dic.defaultIndex)
								it.dic.defaultIndex = 0;
							f.setValue(f.store.getAt(it.dic.defaultIndex).get('key'));
						}
						f.validate();
					}
				}
				this.setKeyReadOnly(false)
				this.startValues = form.getValues(true);
				this.fireEvent("doNew", this.form)
				this.focusFieldAfter(-1, 800)
				this.afterDoNew();
				this.resetButtons();
				//add by Dingxc
				if(this.opener.SJLBBH){
					//不是根节点下打开的
					this.form.getForm().findField("MLBZ").setValue("0");
				}
			},
			onReady : function() {
				phis.application.emr.script.EMRMedicalRecordTemplatesManageForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				var YDLBBM = form.findField("YDLBBM");
				if (YDLBBM) {
					YDLBBM.on("select", this.onSelectYDLBBM, this);
				}
				// var MLBZ = form.findField("MLBZ");
				 //console.debug(this.opener.SJLBBH);
				 //alert("1");
				// if(this.opener.SJLBBH){
				// MLBZ.setValue("0");
				// }else{
				// MLBZ.setValue("1");
				// }
			},
			onSelectYDLBBM : function(item, record, e) {
				// console.debug(record)
				// this.form.getForm().findField("BLFZ").setValue(record.json.BLFZ);
				// alert(record.json.BLLX)
				this.data["LBBM"] = record.data.key;
				this.data["BLFZ"] = record.json.BLFZ;
				this.form.getForm().findField("BLLX").setValue(record.json.BLLX + "");
				if(this.opener.SJLBBH){
					//如果不是一级目录
					this.form.getForm().findField("BLLX").disable();
				}
				this.form.getForm().findField("DYWD").setValue(record.json.DYWD
						+ "");
				this.form.getForm().findField("LBMC")
						.setValue(record.json.YDLBMC);
				this.form.getForm().findField("ZYPLXH")
						.setValue(record.json.ZYPLXH);
				// if (record.data.key == 1 || record.data.key == 2
				// || record.data.key == 3) {
				// this.topForm.form.getForm().findField("YYSJ").enable();
				// this.topForm.form.getForm().findField("ZBLB").enable();
				// this.view.calendar.disable();
				// } else {
				// this.topForm.form.getForm().findField("YYSJ").disable();
				// this.topForm.form.getForm().findField("ZBLB").disable();
				// this.view.calendar.enable();
				//
				// }
			},
			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					value : it.defaultValue,
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent,
					labelSeparator : ":"
				}
				cfg.listeners = {
					specialkey : this.onFieldSpecialkey,
					// add by liyl 2012-06-17 去掉输入字符串首位空格
					blur : function(e) {
						if (typeof(e.getValue()) == 'string') {
							e.setValue(e.getValue().trim())
						}
					},
					scope : this
				}
				if (it.inputType) {
					cfg.inputType = it.inputType
				}
				if (it.editable) {
					cfg.editable = (it.editable == "true") ? true : false
				}
				if (it['not-null'] == "1" || it['not-null']) {
					cfg.allowBlank = false
					cfg.invalidText = "必填字段"
					cfg.regex = /(^\S+)/
					cfg.regexText = "前面不能有空格字符"
				}
				// add by yangl 增加readOnly属性
				if (it.readOnly) {
					cfg.readOnly = true
					// cfg.unselectable = "on";
					cfg.style = "background:#E6E6E6;cursor:default;";
					cfg.listeners.focus = function(f) {
						f.blur();
					}
				}
				if (it.fixed) {
					cfg.disabled = true
				}
				if (it.pkey && it.generator == 'auto') {
					cfg.disabled = true
				}
				if (it.evalOnServer && ac.canRead(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					cfg.disabled = true
				}
				if (this.op == "update" && !ac.canUpdate(it.acValue)) {
					cfg.disabled = true
				}
				// add by yangl,modify simple code Generation methods
				if (it.codeType) {
					if (!this.CodeFieldSet)
						this.CodeFieldSet = [];
					this.CodeFieldSet.push([it.target, it.codeType, it.id]);
				}
				if (it.properties && it.properties.mode == "remote") {
					// 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
					return this.createRemoteDicField(it);
				} else if (it.dic) {
					// add by lyl, check treecheck length
					if (it.dic.render == "TreeCheck") {
						if (it.length) {
							cfg.maxLength = it.length;
						}
					}

					it.dic.src = this.entryName + "." + it.id
					it.dic.defaultValue = it.defaultValue
					it.dic.width = defaultWidth
					if (it.dic.fields) {
						if (typeof(it.dic.fields) == 'string') {
							var fieldsArray = it.dic.fields.split(",")
							it.dic.fields = fieldsArray;
						}
					}
					var combox = this.createDicField(it.dic)
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
				}
				if (it.dic) {
					// add by lyl, check treecheck length
					if (it.dic.render == "TreeCheck") {
						if (it.length) {
							cfg.maxLength = it.length;
						}
					}
					it.dic.src = this.entryName + "." + it.id
					it.dic.defaultValue = it.defaultValue
					it.dic.width = defaultWidth		
					var combox = this.createDicField(it.dic)
					this.changeFieldCfg(it, cfg);
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
				}
				if (it.length) {
					cfg.maxLength = it.length;
				}
				if (it.maxValue) {
					cfg.maxValue = it.maxValue;
				}
				if (it.minValue) {
					cfg.minValue = it.minValue;
				}
				if (it.xtype) {
					if (it.xtype == "htmleditor") {
						cfg.height = it.height || 200;
					}
					if (it.xtype == "textarea") {
						cfg.height = it.height || 65
					}
					if (it.xtype == "datefield"
							&& (it.type == "datetime" || it.type == "timestamp")) {
						cfg.emptyText = "请选择日期"
						cfg.format = 'Y-m-d'
					}
					if (it.xtype == "label") {
						cfg.html = this.labelText;
					}
					this.changeFieldCfg(it, cfg);
					return cfg;
				}
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield";
						cfg.style = "color:#00AA00;font-weight:bold;text-align:right";
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						cfg.format = 'Y-m-d'
						if (it.maxValue && typeof it.maxValue == 'string'
								&& it.maxValue.length > 10) {
							cfg.maxValue = it.maxValue.substring(0, 10);
						}
						if (it.minValue && typeof it.minValue == 'string'
								&& it.minValue.length > 10) {
							cfg.minValue = it.minValue.substring(0, 10);
						}
						break;
					case 'datetime' :
						cfg.xtype = 'datetimefieldEx'
						cfg.emptyText = "请选择日期时间"
						cfg.format = 'Y-m-d H:i:s'
						break;
					case 'text' :
						cfg.xtype = "htmleditor"
						cfg.enableSourceEdit = false
						cfg.enableLinks = false
						cfg.width = 300
						cfg.height = 180
						break;
				}
				this.changeFieldCfg(it, cfg);
				return cfg;
			},
			doSave : function() {
				if (this.opener.SJLBBH && this.op == "create") {
					this.data["SJLBBH"] = this.opener.SJLBBH;
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				// console.debug(this)
				// console.debug("values", values)
				Ext.apply(this.data, values);
				if (this.op == "create") {
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "emrManageService",
								serviceAction : "saveMedicalRecord",
								body : values,
								op : this.op
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						this.panel.el.unmask();
						return false;
					} else {
						if(r.json.rmsg == 7){
							this.getWin().hide();
							MyMessageTip.msg("提示", "查询一级病历约定类别出错，请重试!", true);
							return;
						}else if(r.json.rmsg == 77){
							this.getWin().hide();
							MyMessageTip.msg("提示", "保存失败，存在重复的一级病历约定类别!", true);
							return;
						}else{
							// if (winclose != false) {
							this.getWin().hide();
							// }
							MyMessageTip.msg("提示", "保存成功!", true)
							// this.opener.getWin().hide();
							this.fireEvent("save", this);
							this.opener.opener.tree.tree.root.reload();
							// this.opener.opener.tree.tree.getLoader().load();
						}
					}
				} else {
					this.saveToServer(values);
					this.getWin().hide();
					//this.opener.opener.tree.tree.root.reload();
				}
			},
			//add by Dingxc
			//重写原saveToServer方法，原来的代码执行顺序因为异步的问题会有缺陷
			//把原来的this.opener.opener.tree.tree.root.reload();放到数据库操作之后
			saveToServer : function(saveData) {
				var saveRequest = this.getSaveRequest(saveData); // ** 获取保存条件数据
				if (!saveRequest) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op, saveRequest)) {
					return;
				}
				this.saving = true;
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction || "",
							method : "execute",
							op : this.op,
							schema : this.entryName,
							module : this._mId, // 增加module的id
							body : saveRequest
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.saveToServer,
										[saveData], json.body);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op, json,
										this.data)
							}
							this.op = "update"
							MyMessageTip.msg("提示", "保存成功!", true)
							this.opener.opener.tree.tree.root.reload();
						}, this)// jsonRequest
			}
		})
