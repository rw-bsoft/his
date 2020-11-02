$package("chis.application.mov.script.ehr");

$import("chis.application.mov.script.ehr.EHRMoveUtilForm",
		"util.widgets.LookUpField");

chis.application.mov.script.ehr.EHRMoveConfirmForm = function(cfg) {
	cfg.fldDefaultWidth = 142;
	cfg.colCount = 3;
	cfg.isCombined = false;
	chis.application.mov.script.ehr.EHRMoveConfirmForm.superclass.constructor
			.apply(this, [cfg]);
	this.entryName = "chis.application.mov.schemas.MOV_EHRConfirm";
	this.on("loadData", this.onLoadData, this);

};

Ext.extend(chis.application.mov.script.ehr.EHRMoveConfirmForm,
		chis.application.mov.script.ehr.EHRMoveUtilForm, {

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
				this.autoFieldWidth = false;

				if (!this.autoFieldWidth) {

					var forceViewWidth = (defaultWidth + (this.labelWidth || 93))
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
						if (it.id == "targetArea") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
										name : it.id,
										colspan : "3",
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式

										fieldLabel : "<font  color=red>现网格地址:<font>",
										"width" : 570
									});
							this.ff = ff;
							this.ff.allowBlank = false;
							this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
							continue;
						}
						if (it.id == "sourceArea") {
							var _ctr = this;
							var ff1 = new Ext.form.TriggerField({
										name : it.id,
										colspan : "3",
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff1.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										// readOnly : true, //只读
										disabled : true,
										fieldLabel : "<font  color=red>原网格地址:<font>",
										"width" : 570

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
					labelWidth : this.labelWidth || 70,
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
				if ("update" == this.op) {
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

			onLoadData : function(entryName, body) {

				var archiveType = body["archiveType"];
				if (archiveType) {
					this.changeLabelName(archiveType.key);
				}
				var status = body["status"];
				if (!status) {
					return;
				}
				var moveType = body.moveType;
				if (!moveType) {
					return;
				}
				if (status.key == "1") {
					// @@待确认 ### 初始化*确认机构*确认人*确认时间
					var form = this.form.getForm();
					var affirmUnitField = form.findField("affirmUnit");
					affirmUnitField.setValue({
								key : this.mainApp.deptId,
								text : this.mainApp.dept
							});
					var affirmUserField = form.findField("affirmUser");
					affirmUserField.setValue({
								key : this.mainApp.uid,
								text : this.mainApp.uname
							});
					var affirmDateField = form.findField("affirmDate");
					affirmDateField.setValue(this.mainApp.serverDate);
				}
				if (status.key != "1" && this.mainApp.jobtitleId == "system") {
					var btns = this.form.getTopToolbar().items;
					if (btns && btns.length > 0) {
						btns.item(0).disable();
						btns.item(1).disable();
					}
				}
			},

			doConfirm : function() {
				this.data["affirmType"] = "1";
				this.op = "update";
				this.saveServiceId = "chis.ehrMoveService";
				this.saveAction = "saveConfirmEHRMove";
				this.doSave();
			},

			doReject : function() {
				this.data["affirmType"] = "2";
				this.op = "update";
				this.saveServiceId = "chis.ehrMoveService";
				this.saveAction = "saveConfirmEHRMove";
				this.doSave();
			},
			initFormData : function(data) {
				
				chis.application.mov.script.ehr.EHRMoveConfirmForm.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType) {
					if (data.targetArea) {

						if (data.targetArea.key) {

							this.form.getForm().findField("targetArea")
									.setValue(data.targetArea.text);
							this.form.getForm().findField("targetArea")
									.disable();
							this.data.targetArea = data.targetArea.key;
							this.data.targetArea_text = data.targetArea.text;
						}
					}
					if (data.sourceArea) {
						if (data.sourceArea.key) {

							this.form.getForm().findField("sourceArea")
									.setValue(data.sourceArea.text);
							this.form.getForm().findField("sourceArea")
									.disable();
							this.data.sourceArea = data.sourceArea.key;
							this.data.sourceArea_text = data.sourceArea.text;
						}
					}
				}
			},
			doSave : function() {
				if (this.saving) {
					return
				}
				var ac = util.Accredit;
				var form = this.form.getForm()
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items
				Ext.apply(this.data, this.exContext)
				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}
						var v = it.defaultValue || this.data[it.id]
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
							// end
						}

						if ("form" == this.mainApp.exContext.areaGridShowType) {

							if ("targetArea" == it.id) {
								v = this.data.targetArea;
							}
							if ("sourceArea" == it.id) {
								v = this.data.sourceArea;
							}
							if ("targetArea_text" == it.id) {
								v = this.data.targetArea_text;
							}
							if ("sourceArea_text" == it.id) {
								v = this.data.sourceArea_text;
							}

						}
						if (v == null || v === "") {
							if (!(it.pkey == "true")
									&& (it["not-null"] == "1" || it['not-null'] == "true")
									&& !it.ref) {
								alert(it.alias + "不能为空")
								return;
							}
						}
						values[it.id] = v;
					}
				}
				
				Ext.apply(this.data, values);
				this.saveToServer(values)
			}
		});