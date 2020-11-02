$package("chis.application.mov.script.ehr");

$import("chis.application.mov.script.ehr.EHRMoveUtilForm",
		"util.widgets.LookUpField",
		"chis.application.mpi.script.EMPIInfoModule",
		"chis.application.mov.script.util.QueryModule");

chis.application.mov.script.ehr.EHRMoveApplyForm = function(cfg) {
	cfg.autoLoadSchema = false
	chis.application.mov.script.ehr.EHRMoveApplyForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
	this.on("doNew", this.onDoNew, this);
	this.on("winShow", this.onWinShow, this);
	this.entryName = "chis.application.mov.schemas.MOV_EHRApply";
	this.saveServiceId = "chis.ehrMoveService";
	this.saveAction = "saveApplyEHRMove";
	this.colCount = 2;
	this.op1 = "";
	// this.sourceArea_textValue="";
	// this.targetArea_textValue="";

};

Ext.extend(chis.application.mov.script.ehr.EHRMoveApplyForm,
		chis.application.mov.script.ehr.EHRMoveUtilForm, {
			onWinShow : function() {
				if (!this.formType) {
					this.type = "part";
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.systemCommonManageService",
								serviceAction : "getSystemConfigValue",
								method : "execute",
								body : "areaGridType"
							});
					if (result.code > 300) {
						alert("页面参数获取失败.")
						return
					}
					if (result.json.body) {
						this.formType = result.json.body;
					}
				}
				var frm = this.form.getForm();
				var archiveType = frm.findField("archiveType");
				if (archiveType) {
					if (this.formType == "part") {
						archiveType.disable();
					} else {
						archiveType.enable();
					}
				}
			},
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
					var forceViewWidth = (defaultWidth + (this.labelWidth || 60))
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
					if ("tree" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "targetArea_text") {
							continue;
						}
						if (it.id == "sourceArea_text") {
							continue;
						}
					}
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "targetArea") {
							continue;
						}
						if (it.id == "sourceArea") {
							continue;
						}
					}
					if (it.id == "targetArea"||it.id == "sourceArea") {
						if ("pycode" == this.mainApp.exContext.areaGridShowType) {
							it.otherConfig={
									'not-null':'true',
									width:220,
									allowBlank:false,
									invalidText:"必填字段",
									disabled : it.id == "sourceArea"?true:false,
							};
							//it.filterType='falmily';
							//it.afterSelect=this.afterSelect;
							var areaGrid=this.createAreaGridField(it);
							table.items.push(areaGrid)
							continue;
						}
					}
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "targetArea_text") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
										name : it.id,

										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										// readOnly : true, //只读
										// disabled : true,
										fieldLabel : "<font  color=red>现网格地址:<font>",
										"width" : 220
									});
							this.ff = ff;
							this.ff.allowBlank = false;
							this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
							continue;
						}
						if (it.id == "sourceArea_text") {
							var _ctr = this;
							var ff1 = new Ext.form.TriggerField({
										name : it.id,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff1.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										// readOnly : true, //只读
										disabled : true,
										fieldLabel : "<font  color=red>原网格地址:<font>",
										"width" : 220
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
					if ("form" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
						f.anchor = it.anchor || "93%"
					} else {
						f.anchor = it.anchor || "100%"
					}
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
			initFormData : function(data) {
				chis.application.mov.script.ehr.EHRMoveApplyForm.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("targetArea_text")
							.setValue(data.targetArea_text);
					this.data.targetArea = data.targetArea;
					this.form.getForm().findField("sourceArea_text")
							.setValue(data.sourceArea_text);
					this.data.sourceArea = data.sourceArea;
					this.regionCode_key = data.sourceArea;
					this.regionCode_text = data.sourceArea_text;

				}
				if ("pycode" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("targetArea")
							.setValue(data.targetArea_text);
					this.data.targetArea = data.targetArea;
					this.data.targetArea_text = data.targetArea_text;
					
					this.form.getForm().findField("targetArea")
					.selectData.regionCode=data.targetArea.key;
				
					this.form.getForm().findField("sourceArea")
							.setValue(data.sourceArea_text);
					this.data.sourceArea = data.sourceArea;
					this.data.sourceArea_text = data.sourceArea_text;
					
					this.form.getForm().findField("sourceArea")
					.selectData.regionCode=data.sourceArea.key;
					
					this.regionCode_key = data.sourceArea;
					this.regionCode_text = data.sourceArea_text;

				}
			},
			onDoNew : function() {
				this.onToChangeTargetUnit();
				var frm = this.form.getForm();
				var personName = frm.findField("personName");
				personName.enable();
				if ("form" == this.mainApp.exContext.areaGridShowType) {

					this.form.getForm().findField("targetArea_text").enable();
				}
			},

			onLoadData : function(entryName, body) {
				var archiveType = body["archiveType"];
				if (archiveType) {
					this.changeLabelName(archiveType.key);
				}
				var frm = this.form.getForm();
				var archiveType = frm.findField("archiveType");
				var personName = frm.findField("personName");
				if (this.initDataId) {
					archiveType.disable();
					personName.disable();
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						this.form.getForm().findField("targetArea_text")
								.disable();
					}
				} else {
					if (this.type == "part") {
						archiveType.disable();
					} else {
						archiveType.enable();
					}
					personName.enable();
				}
				// this.onToChangeTargetUnit();
			},
			onRegionCodeClick : function(r) {
				if("sourceArea_text"==r){
				return;
				
				}
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

			onQd : function(data) {
				if ("sourceArea_text" == data.areaGridListId) {
					this.form.getForm().findField("sourceArea_text")
							.setValue(data.regionCode_text);
					this.data.sourceArea = data.regionCode;
				}
				if ("targetArea_text" == data.areaGridListId) {
					this.form.getForm().findField("targetArea_text")
							.setValue(data.regionCode_text);
					this.data.targetArea = data.regionCode;
				}
			},
			getFormData : function() {
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var ac = util.Accredit;
				var values = {};
				var valuess = {};
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
						if (it.id == "targetArea_text") {
							if ("pycode" == this.mainApp.exContext.areaGridShowType) {
								continue;
							}
						}
						if (f) {
							v = f.getValue()
							if(it.id=='sourceArea'||it.id=='targetArea')
							{
								if ("pycode" == this.mainApp.exContext.areaGridShowType) {
									values[it.id+'_text']=f.getValue();
									v = f.getAreaCodeValue();
								}
							}
							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "") {
									v = "";
								}
								// sourceArea_text targetArea_text赋值
								if ("tree" == this.mainApp.exContext.areaGridShowType) {
									if ("sourceArea" == it.id) {
										valuess["sourceArea_text"] = rawVal;
										// this.sourceArea_textValue=rawVal;
									}
									if ("targetArea" == it.id) {
										valuess["targetArea_text"] = rawVal;
										// this.targetArea_textValue=rawVal;
									}
								}
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
							// end
						}
						// 放在if (f) 外面原因是因为这个字段不是createfield创建的
						if ("form" == this.mainApp.exContext.areaGridShowType) {
							if ("sourceArea" == it.id) {
								if (typeof this.regionCode_key == "object") {
									v = this.regionCode_key.key;
								} else {
									v = this.regionCode_key;
								}
							}
						}
						if (v == null || v === "") {
							if (!(it.pkey)
									&& (it["not-null"] == "1" || it['not-null'] == "true")
									&& !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空");
								return;
							}
						}
						values[it.id] = v;
					}
					if (this.op1 != "update") {
						if ("form" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
							valuess["sourceArea_text"] = this.regionCode_text;
						}
						Ext.apply(values, valuess);
					}
				}
				
				return values;
			},
			doSave : function() {

				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				Ext.apply(this.data, values);
				this.saveToServer(values)
			}

		});