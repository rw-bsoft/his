$package("chis.application.fhr.script");
$import("chis.script.BizSimpleFormView")

chis.application.fhr.script.MovRecordForm = function(cfg) {
	cfg.autoLoadSchema = false// 防止重复
	chis.application.fhr.script.MovRecordForm.superclass.constructor.apply(
			this, [cfg]);
	this.colCount = 2;
	this.schema = "chis.application.fhr.schemas.EHR_HealthRecord";
	this.saveServiceId = "chis.familyRecordService";
	this.loadServiceId = "chis.familyRecordService";
	this.saveAction = "saveInfo";
	this.loadAction = "loadInfo";
};

Ext.extend(chis.application.fhr.script.MovRecordForm,
		chis.script.BizSimpleFormView, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var items = schema.items
				if (!this.fireEvent("changeDic", items)) {
					return
				}
				var colCount = this.colCount;

				var table = {
					layout : 'tableform',
					layoutConfig : {
						columns : colCount,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : '2'
						}
					},
					items : []
				}
				if (!this.autoFieldWidth) {
					var forceViewWidth = (defaultWidth + (this.labelWidth || 120))
							* colCount
					table.layoutConfig.forceWidth = forceViewWidth
				}
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1 || it.hidden == true)
							|| !ac.canRead(it.acValue)) {

						continue;
					}

					if ("form" == this.mainApp.exContext.areaGridShowType) {

						if (it.id == "regionCode") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
										name : it.id,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff.name)
										},
										triggerClass : 'x-form-search-trigger',
										fieldLabel : "<font  color=red>原网格地址:<font>",
										"width" : 225
									});
							this.ff = ff;
							this.ff.allowBlank = false;
							this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
							continue;

						}
						if (it.id == "targetArea") {
							var _ctr = this;
							var ff1 = new Ext.form.TriggerField({
										name : it.id,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff1.name)
										},
										triggerClass : 'x-form-search-trigger',
										fieldLabel : "<font  color=red>现网格地址:<font>",
										"width" : 225
									});
							this.ff1 = ff1;
							this.ff1.allowBlank = false;
							this.ff1.invalidText = "必填字段";
							this.ff1.regex = /(^\S+)/;
							this.ff1.regexText = "前面不能有空格字符";
							table.items.push(ff1)
							continue;

						}
					}
					var f = this.createField(it)
					f.index = i;

					f.anchor = it.anchor || "100%"

					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)

					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}
					table.items.push(f)
				}

				var cfg = {
					buttonAlign : 'center',
					labelAlign : this.labelAlign || "left",
					labelWidth : this.labelWidth || 80,
					frame : true,
					shadow : false,
					border : false,
					collapsible : false,
					autoWidth : true,
					autoHeight : true,
					floating : false
				}
				if (this.isCombined) {
					cfg.frame = true
					cfg.shadow = false
					cfg.width = this.width
					cfg.height = this.height
				} else {
					cfg.autoWidth = true
					cfg.autoHeight = true
				}
				this.changeCfg(cfg);
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)
				this.schema = schema;
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			onRegionCodeClick : function(r) {
				if ("update" == this.op && "regionCode" == r) {
					return;
				}
				var m = this.createCombinedModule("wgdz",
						"chis.application.hr.HR/HR/B3410101")
				m.on("qd", this.onQd, this);
				m.areaGridListId = r;
				var t = m.initPanel();
				var win = m.getWin();
				win.add(t)
				win.setPosition(400, 150);
				win.show();
				// m.loadData();
			},
			onQd : function(data) {
				if ("regionCode" == data.areaGridListId) {
					this.form.getForm().findField("regionCode")
							.setValue(data.regionCode_text);
					this.data.regionCode = data.regionCode;
					this.data.regionCode_text = data.regionCode_text;
				}
				if ("targetArea" == data.areaGridListId) {
					this.form.getForm().findField("targetArea")
							.setValue(data.regionCode_text);
					this.data.targetArea = data.regionCode;
					this.data.targetArea_text = data.regionCode_text;
				}
			},
			onReady : function() {
				chis.application.fhr.script.MovRecordForm.superclass.onReady
						.call(this);
				var targetDoctor = this.form.getForm()
						.findField("targetDoctor");
				if (targetDoctor) {
					targetDoctor.on("select", this.changeManaUnit, this);
				}

			},
			initFormData : function(data) {

				chis.application.fhr.script.MovRecordForm.superclass.initFormData
						.call(this, data);
				if ("form" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("targetArea")
							.setValue(data.targetArea.text);
					this.form.getForm().findField("regionCode")
							.setValue(data.regionCode.text);
					this.form.getForm().findField("regionCode").disable();
					this.data.targetArea = data.targetArea.key;
					this.data.regionCode = data.regionCode.key;
					this.data.regionCode_text = data.regionCode.text;
					this.data.targetArea_text = data.targetArea.text;
					this.data.sexCode = data.sexCode;
				}

			},
			getFormData : function() {
				// if (!this.validate()) {
				// return
				// }
				if (!this.schema) {
					return
				}
				var ac = util.Accredit;
				var values = {};
				var items = this.schema.items;
				Ext.apply(this.data, this.exContext);
				if (items) {
					var form = this.form.getForm();
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}
						var v = this.data[it.id] // ** modify by yzh
						// 2010-08-04
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()

							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
							if(it.dic){
								values[it.id+'_text'] = f.getRawValue();
							}
						}
						if ("form" == this.mainApp.exContext.areaGridShowType) {
							if ("targetArea" == it.id) {
								v = this.data.targetArea;
							}
						}
						if ("form" == this.mainApp.exContext.areaGridShowType) {
							if ("regionCode" == it.id) {
								v = this.data.regionCode;
							}
						}
						if (v == null || v === "") {
							// if("targetDoctor"==it.id){
							if (!(it.pkey)
									&& (it["not-null"] == "1" || it['not-null'] == "true")
									&& !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空");
								return;
							}
						}
						values[it.id] = v;
					}
				}
				if (this.data.sexCode) {
					values["sexCode"] = this.data.sexCode
				}
				return values;
			},
			changeManaUnit : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method : "execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						})
				this.setManaUnit(result.json.manageUnit)
			},
			setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("targetUnit");
				if (!combox) {
					return;
				}

				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}

				if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
					combox.setValue(manageUnit)
					combox.disable();
				} else {
					combox.enable();
					combox.reset();
				}
			},
			doSave : function() {

				var values = this.getFormData();
				if (!values) {
					return;
				}
				var _cfg = this;
				Ext.MessageBox.confirm("选择框", "确定该成员迁出该家庭?", callback);
				function callback(id) {
					if (id == "yes") {
						_cfg.saveToServer(values);
						_cfg.doClose();
						// _cfg.fireEvent("save");
					} else {
						return;

					}
				}

			},
			doClose : function() {
				this.getWin().hide();

			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction || "",
							schema : this.Schema,
							op : this.op,
							body : saveData,
							method : "execute"
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData],
										json.body);
								return
							}
							Ext.apply(this.data, saveData);

							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "update"
						}, this)// jsonRequest
			}
		});