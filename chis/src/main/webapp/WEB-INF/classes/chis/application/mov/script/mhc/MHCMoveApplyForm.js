﻿/**
	 * 孕妇户籍地址迁移表单申请页面
	 * 
	 * @author : yaozh
	 */
$package("chis.application.mov.script.mhc")
$import("chis.application.mov.script.mhc.MHCMoveUtilForm",
		"util.widgets.LookUpField")
chis.application.mov.script.mhc.MHCMoveApplyForm = function(cfg) {
	cfg.autoLoadSchema = false
	cfg.autoFieldWidth = false;// 2014.10.17加
	chis.application.mov.script.mhc.MHCMoveApplyForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeCreate", this.beforeCreate, this);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("save", this.onSave, this)
};
Ext.extend(chis.application.mov.script.mhc.MHCMoveApplyForm,
		chis.application.mov.script.mhc.MHCMoveUtilForm, {
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
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						var forceViewWidth = (defaultWidth + (this.labelWidth || 120))
								* colCount

					}else if("pycode" == this.mainApp.exContext.areaGridShowType)
					{
						var forceViewWidth = (defaultWidth + (this.labelWidth || 90))
						* colCount
					}else {
						var forceViewWidth = (defaultWidth + (this.labelWidth || 60))
								* colCount
					}
					table.layoutConfig.forceWidth = forceViewWidth
				}
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1 || it.hidden == true)
							|| !ac.canRead(it.acValue)) {

						continue;
					}
					if (it.id == "targetHomeAddress"||it.id == "sourceHomeAddress"||it.id=='targetRestRegion'||it.id=='sourceRestRegion') {
						if ("pycode" == this.mainApp.exContext.areaGridShowType) {
							var flag=true;
							if(it.id=='targetRestRegion'||it.id == "targetHomeAddress")
								flag=false;
							it.otherConfig={
									'not-null':'true',
									colspan:2,
									width:300,
									anchor:"100%",
									disabled:flag,
									allowBlank:false,
									invalidText:"必填字段"
							};
							//it.afterSelect=this.afterSelect;
							var areaGrid=this.createAreaGridField(it);
							table.items.push(areaGrid)
							continue;
						}
					}
			
					if ("form" == this.mainApp.exContext.areaGridShowType) {

						if (it.id == "targetHomeAddress") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
										name : it.id,
										colspan : 2,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										// readOnly : true, //只读
										// disabled : true,
										fieldLabel : "<font  color=red>现户籍地址:<font>",
										"width" : 360

									});
							this.ff = ff;
							this.ff.allowBlank = false;
							this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
							continue;

						}
						if (it.id == "sourceRestRegion") {
							var _ctr = this;
							var ff2 = new Ext.form.TriggerField({
										name : it.id,
										colspan : 2,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff2.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										// readOnly : true, //只读
										// disabled : true,
										fieldLabel : "<font  color=red>原产休地:<font>",
										"width" : 360

									});
							this.ff2 = ff2;
							this.ff2.allowBlank = false;
							this.ff2.invalidText = "必填字段";
							this.ff2.regex = /(^\S+)/;
							this.ff2.regexText = "前面不能有空格字符";
							table.items.push(ff2)
							continue;

						}
						if (it.id == "targetRestRegion") {
							var _ctr = this;
							var ff3 = new Ext.form.TriggerField({
										name : it.id,
										colspan : 2,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff3.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										// readOnly : true, //只读
										// disabled : true,
										fieldLabel : "<font  color=red>现产休地:<font>",
										"width" : 360

									});
							this.ff3 = ff3;
							this.ff3.allowBlank = false;
							this.ff3.invalidText = "必填字段";
							this.ff3.regex = /(^\S+)/;
							this.ff3.regexText = "前面不能有空格字符";
							table.items.push(ff3)
							continue;

						}
						if (it.id == "sourceHomeAddress") {
							var _ctr = this;
							var ff1 = new Ext.form.TriggerField({
										name : it.id,
										colspan : 2,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff1.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										// readOnly : true, //只读
										// disabled : true,
										fieldLabel : "<font  color=red>原户籍地址:<font>",
										"width" : 360

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
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						f.anchor = it.anchor || "90%"
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

				if ("targetHomeAddress" == data.areaGridListId) {
					this.form.getForm().findField("targetHomeAddress")
							.setValue(data.regionCode_text);
					this.data.targetHomeAddress_text = data.regionCode_text;
					this.data.targetHomeAddress = data.regionCode;

				}
				if ("sourceHomeAddress" == data.areaGridListId) {
					this.form.getForm().findField("sourceHomeAddress")
							.setValue(data.regionCode_text);
					this.data.sourceHomeAddress_text = data.regionCode_text;
					this.data.sourceHomeAddress = data.regionCode;

				}

				if ("sourceRestRegion" == data.areaGridListId) {
					this.form.getForm().findField("sourceRestRegion")
							.setValue(data.regionCode_text);
					this.data.sourceRestRegion_text = data.regionCode_text;
					this.data.sourceRestRegion = data.regionCode;

				}

				if ("targetRestRegion" == data.areaGridListId) {
					this.form.getForm().findField("targetRestRegion")
							.setValue(data.regionCode_text);
					this.data.targetRestRegion_text = data.regionCode_text;
					this.data.targetRestRegion = data.regionCode;

				}

			},
			initFormData : function(data) {
				chis.application.mov.script.mhc.MHCMoveApplyForm.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
					if (data.sourceHomeAddress) {
						if (data.sourceHomeAddress.key) {

							this.form.getForm().findField("sourceHomeAddress")
									.setValue(data.sourceHomeAddress.text);
							this.form.getForm().findField("sourceHomeAddress")
									.disable();
							if("pycode" == this.mainApp.exContext.areaGridShowType)
							{
								this.form.getForm().findField("sourceHomeAddress")
								.selectData.regionCode=data.sourceHomeAddress.key;
							}
							this.data.sourceHomeAddress = data.sourceHomeAddress.key;
							this.data.sourceHomeAddress_text = data.sourceHomeAddress.text;
						}
					}
					if (data.targetHomeAddress) {
						if (data.targetHomeAddress.key) {

							this.form.getForm().findField("targetHomeAddress")
									.setValue(data.targetHomeAddress.text);
							this.form.getForm().findField("targetHomeAddress")
									.disable();
							if("pycode" == this.mainApp.exContext.areaGridShowType)
							{
								this.form.getForm().findField("targetHomeAddress")
								.selectData.regionCode=data.targetHomeAddress.key;
							}
							this.data.targetHomeAddress = data.targetHomeAddress.key;
							this.data.targetHomeAddress_text = data.targetHomeAddress.text;
						}
					}
					if (data.sourceRestRegion) {
						if (data.sourceRestRegion.key) {

							this.form.getForm().findField("sourceRestRegion")
									.setValue(data.sourceRestRegion.text);
							this.form.getForm().findField("sourceRestRegion")
									.disable();
							if("pycode" == this.mainApp.exContext.areaGridShowType)
							{
								this.form.getForm().findField("sourceRestRegion")
								.selectData.regionCode=data.sourceHomeAddress.key;
							}
							this.data.sourceRestRegion = data.sourceRestRegion.key;
							this.data.sourceRestRegion_text = data.sourceRestRegion.text;
						}
					}
					if (data.targetRestRegion) {
						if (data.targetRestRegion.key) {

							this.form.getForm().findField("targetRestRegion")
									.setValue(data.targetRestRegion.text);
							this.form.getForm().findField("targetRestRegion")
									.disable();
							if("pycode" == this.mainApp.exContext.areaGridShowType)
							{
								this.form.getForm().findField("targetRestRegion")
								.selectData.regionCode=data.sourceHomeAddress.key;
							}
							this.data.targetRestRegion = data.targetRestRegion.key;
							this.data.targetRestRegion_text = data.targetRestRegion.text;
						}
					}

				}

			},
			beforeCreate : function() {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var saveBtn = btns.item(0)
				if (saveBtn) {
					saveBtn.enable();
				}
			},

			onReady : function() {
				chis.application.mov.script.mhc.MHCMoveApplyForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var personNameField = form.findField("personName");
				if (personNameField) {
					personNameField.on("lookup", this.doQuery, this)
					personNameField.on("clear", this.doNew, this)
				}

			},
			doQuery : function(field) {
				if (!field.disabled) {
					var m = this.midiModules["PregnantRecord"];
					if (!m) {
						$import("chis.application.mpi.script.EMPIInfoModule")
						m = new chis.application.mpi.script.EMPIInfoModule({
							entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
							title : "个人基本信息查询",
							height : 450,
							modal : true,
							mainApp : this.mainApp
						})
						m.on("onEmpiReturn", this.onPregnantSelect, this)
						this.midiModules["PregnantRecord"] = m;
					}
					var win = m.getWin();
					win.setPosition(250, 100);
					win.show();
				}
			},

			onPregnantSelect : function(data) {
				var empiId = data.empiId
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "getPregnantMessage",
							method : "execute",
							body : {
								"empiId" : empiId
							}
						});
				var res = result.json;
				if (res.noRecord) {
					Ext.Msg.alert("提示", data.personName + "没有孕妇档案！");
					return;
				}
				var body = res.body;
				if (body) {
					var targetMhcDoctorId = this.form.getForm()
							.findField("targetMhcDoctorId");
					// 判断迁入迁出
					if (this.startWith(body.sourceManaUnitId.key,
							this.mainApp.deptId)) {
						this.data["moveType"] = "2";
						targetMhcDoctorId.allowBlank = true;
					} else {
						this.data["moveType"] = "1";
						targetMhcDoctorId.allowBlank = false;
					}
					targetMhcDoctorId.validate();

					this.initFormData(body);
				}
			},

			onBeforeSave : function(entryName, op, saveData) {
				var moveType = saveData.moveType;
				if (moveType == "1") {
					var targetMhcDoctorId = saveData.targetMhcDoctorId;
					if (!targetMhcDoctorId) {
						Ext.Msg.alert("提示", "【现妇保医生】不能为空！");
						return false;
					}
				} else {
					return true;
				}
			},

			onSave : function(entryName, op, json, data) {
				this.doCancel();
				this.fireEvent("afterSave");
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
						if (it.id == "targetHomeAddress_text"||it.id == "sourceHomeAddress_text"||it.id == "sourceRestRegion_text"||it.id == "targetRestRegion_text" ){
							if ("pycode" == this.mainApp.exContext.areaGridShowType) {
								continue;
							}
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							if(it.id=='targetHomeAddress'||it.id=='sourceHomeAddress'||it.id=='sourceRestRegion'||it.id=='targetRestRegion')
							{
								if ("pycode" == this.mainApp.exContext.areaGridShowType) {
									values[it.id+'_text']=f.getValue();
									v = f.getAreaCodeValue();
								}
							}
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
							// end
						}

						if ("form" == this.mainApp.exContext.areaGridShowType) {

							if ("sourceHomeAddress" == it.id) {
								v = this.data.sourceHomeAddress;
							}
							if ("sourceRestRegion" == it.id) {

								v = this.data.sourceRestRegion;
							}
							if ("targetHomeAddress" == it.id) {
								v = this.data.targetHomeAddress;
							}
							if ("targetRestRegion" == it.id) {

								v = this.data.targetRestRegion;
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
				}
				return values;
			},
			onWinShow : function() {
				this.win.doLayout();
				this.manageUnits = null;
				if (this.form) {
					this.validate();
				}

				if (!this.initDataId) {
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						this.form.getForm().findField("targetHomeAddress")
								.enable();

						this.form.getForm().findField("sourceRestRegion")
								.enable();
						this.form.getForm().findField("targetRestRegion")
								.enable();

						this.form.getForm().findField("sourceHomeAddress")
								.enable();

					}

				}
			}
		})