/**
 * 儿童户籍地址迁移表单申请页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.cdh")
$import("chis.application.mov.script.cdh.CDHMoveUtilForm",
		"util.widgets.LookUpField")
chis.application.mov.script.cdh.CDHMoveApplyForm = function(cfg) {
	cfg.autoLoadSchema = false
	cfg.autoFieldWidth = false;
	chis.application.mov.script.cdh.CDHMoveApplyForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeCreate", this.beforeCreate, this);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("save", this.onSave, this);
	this.status_o=false;
	
};
Ext.extend(chis.application.mov.script.cdh.CDHMoveApplyForm,
		chis.application.mov.script.cdh.CDHMoveUtilForm, {
			doNew : function() {this.status_o=false;
				var form = this.form.getForm();
				var sourceHomeAddress = form.findField("sourceHomeAddress");
				sourceHomeAddress.enable();
				var targetHomeAddress = form.findField("targetHomeAddress");
				targetHomeAddress.enable();
				var targetManaUnitId = form.findField("targetManaUnitId");
				targetManaUnitId.enable();
				chis.application.mov.script.cdh.CDHMoveApplyForm.superclass.doNew
						.call(this);
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
					if ("form" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
						var forceViewWidth = (defaultWidth + (this.labelWidth || 120))
								* colCount

					} else {
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

					// if ("tree" == this.mainApp.exContext.areaGridShowType) {
					// if (it.id == "targetHomeAddress_text") {
					// // 把id为“regionCode_text”屏蔽掉,不显示出来,由于schema没有
					// // 配置displays属性
					// continue;
					// }
					// if (it.id == "sourceHomeAddress_text") {
					// // 把id为“regionCode_text”屏蔽掉,不显示出来,由于schema没有
					// // 配置displays属性
					// continue;
					// }
					// }
					// if ("form" == this.mainApp.exContext.areaGridShowType) {
					// if (it.id == "targetHomeAddress") {
					// // 把id为“regioncode”屏蔽掉,不显示出来,由于schema没有 配置displays属性
					// continue;
					// }
					// if (it.id == "sourceHomeAddress") {
					// // 把id为“regioncode”屏蔽掉,不显示出来,由于schema没有 配置displays属性
					// continue;
					// }
					// }
					
					if (it.id == "targetHomeAddress"||it.id == "sourceHomeAddress") {
						if ("pycode" == this.mainApp.exContext.areaGridShowType) {
							it.otherConfig={
									'not-null':'true',
									colspan:2,
									width:370,
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
										"width" : 370

									});
							this.ff = ff;
							this.ff.allowBlank = false;
							this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
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
										"width" : 370

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
				chis.application.mov.script.cdh.CDHMoveApplyForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var personNameField = form.findField("personName");
				if (personNameField) {
					personNameField.on("lookup", this.doQuery, this)
					personNameField.on("clear", this.doNew, this)
				}

				// if("tree" == this.mainApp.exContext.areaGridShowType){
				// var targetHomeAddressValue =
				// this.form.getForm().findField("targetHomeAddress");
				// if (targetHomeAddressValue) {
				// targetHomeAddressValue.on("select", this.getFamilyMess,
				// this);//
				//			 
				// }
				// }
			},

			doQuery : function(field) {
				if (!field.disabled) {
					var m = this.midiModules["ChildInfoForm"];

					if (!m) {

						$import("chis.application.cdh.script.base.ChildInfoForm")
						m = new chis.application.cdh.script.base.ChildInfoForm(
								{
									entryName : "chis.application.mpi.schemas.MPI_ChildBaseInfo",
									title : "儿童基本信息查询",
									height : 450,
									width : 868,
									modal : true,
									wi : 151,
									mainApp : this.mainApp,
									isDeadRegist : false
								})
						m.on("save", this.onChildSelect, this)
						this.midiModules["ChildInfoForm"] = m;

					}
					m.operen = this;
					var win = m.getWin();
					win.setPosition(250, 100);
					win.show();
				}
			},

			onChildSelect : function(entryName, op, json, data) {
				var empiId = data.childEmpiId
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "getChildrenMessage",
							method : "execute",
							body : {
								"empiId" : empiId
							}
						});
				var body = result.json.body;
				if (!body) {
					Ext.Msg.alert("提示", data.personName + "没有儿童档案！");
					return;
				} else {
					var targetCdhDoctorId = this.form.getForm()
							.findField("targetCdhDoctorId");
					// 判断迁入迁出
					if (this.startWith(body.sourceManaUnitId.key,
							this.mainApp.deptId)) {
						this.data["moveType"] = "2";
						targetCdhDoctorId.allowBlank = true;
					} else {
						this.data["moveType"] = "1";
						targetCdhDoctorId.allowBlank = false;
					}
					targetCdhDoctorId.validate();
					this.initFormData(body);
					this.status_o=true;
				}
			},

			onBeforeSave : function(entryName, op, saveData) {
				var moveType = saveData.moveType;
				if (moveType == "1") {
					var targetCdhDoctorId = saveData.targetCdhDoctorId;
					if (!targetCdhDoctorId) {
						Ext.Msg.alert("提示", "【现儿保医生】不能为空！");
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
			onRegionCodeClick : function(r) {
				
				if ("update" == this.op) {
					return;
				}
				
				if(this.status_o&&r=="sourceHomeAddress"){
				return;
				
				};
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

			},
			initFormData : function(data) {

				chis.application.mov.script.cdh.CDHMoveApplyForm.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType) {

					if (data.sourceHomeAddress.key) {

						this.form.getForm().findField("sourceHomeAddress")
								.setValue(data.sourceHomeAddress.text);
						this.form.getForm().findField("sourceHomeAddress")
								.disable();
						this.data.sourceHomeAddress = data.sourceHomeAddress.key;
						this.data.sourceHomeAddress_text = data.sourceHomeAddress.text;

					}

					if (data.targetHomeAddress && data.targetHomeAddress.key) {

						this.form.getForm().findField("targetHomeAddress")
								.setValue(data.targetHomeAddress.text);
						this.form.getForm().findField("targetHomeAddress")
								.disable();
						this.data.targetHomeAddress = data.targetHomeAddress.key;
						this.data.targetHomeAddress_text = data.targetHomeAddress.text;
					}

				}
				if ("tree" == this.mainApp.exContext.areaGridShowType) {

					if (data.sourceHomeAddress.key) {
						this.form.getForm().findField("sourceHomeAddress")
								.setValue(data.sourceHomeAddress.text);
						this.form.getForm().findField("sourceHomeAddress")
								.disable();
						this.data.sourceHomeAddress = data.sourceHomeAddress.key;
						this.data.sourceHomeAddress_text = data.sourceHomeAddress.text;

					}
				}
				
				if ("pycode" == this.mainApp.exContext.areaGridShowType) {

					if (data.sourceHomeAddress.key) {

						this.form.getForm().findField("sourceHomeAddress")
								.setValue(data.sourceHomeAddress.text);
						this.form.getForm().findField("sourceHomeAddress")
								.disable();
						this.form.getForm().findField("sourceHomeAddress")
						.selectData.regionCode=data.sourceHomeAddress.key;
						this.data.sourceHomeAddress = data.sourceHomeAddress.key;
						this.data.sourceHomeAddress_text = data.sourceHomeAddress.text;

					}
					
					if (data.targetHomeAddress && data.targetHomeAddress.key) {

						this.form.getForm().findField("targetHomeAddress")
								.setValue(data.targetHomeAddress.text);
						this.form.getForm().findField("targetHomeAddress")
								.disable();
						this.form.getForm().findField("targetHomeAddress")
						.selectData.regionCode=data.targetHomeAddress.key;
						this.data.targetHomeAddress = data.targetHomeAddress.key;
						this.data.targetHomeAddress_text = data.targetHomeAddress.text;
					}
				}

			},
			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				// this.fireEvent("afterSave");
				Ext.apply(this.data, values);
				this.saveToServer(values)

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
						
						if (it.id == "targetHomeAddress_text"||it.id == "sourceHomeAddress_text") {
							if ("pycode" == this.mainApp.exContext.areaGridShowType) {
								continue;
							}
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							if(it.id=='targetHomeAddress'||it.id=='sourceHomeAddress')
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
								if ("targetHomeAddress" == it.id) {
									this.vrr = rawVal;
								}

							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
							// end

						}
						if ("tree" == this.mainApp.exContext.areaGridShowType) {

							if ("targetHomeAddress_text" == it.id) {
								v = this.vrr;
							}

						}
						if ("form" == this.mainApp.exContext.areaGridShowType) {

							if ("sourceHomeAddress_text" == it.id) {
								v = this.data.sourceHomeAddress_text;
							}
							if ("sourceHomeAddress" == it.id) {

								v = this.data.sourceHomeAddress;
							}
							if ("targetHomeAddress_text" == it.id) {
								v = this.data.targetHomeAddress_text;
							}
							if ("targetHomeAddress" == it.id) {

								v = this.data.targetHomeAddress;
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
				if ("tree" == this.mainApp.exContext.areaGridShowType) {

					values["sourceHomeAddress"] = this.data.sourceHomeAddress;
				}

				return values;
			},
			saveToServer : function(saveData) {

				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				var saveRequest = this.getSaveRequest(saveData);
				var saveCfg = {
					serviceId : this.saveServiceId,
					method : this.saveMethod,
					action : this.saveAction,
					op : this.op,
					schema : this.entryName,
					module : this._mId, // 增加module的id
					body : saveRequest
				}
				this.fixSaveCfg(saveCfg);
				util.rmi.jsonRequest(saveCfg, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveRequest],
										json.body);
								this
										.fireEvent("exception", code, msg,
												saveData); // **进行异常处理
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {

								// this.initFormData(json.body);
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							this.afterSaveData(this.entryName, this.op, json,
									this.data);
							this.op = "update"
						}, this)// jsonRequest
			},
			onWinShow : function() {
				this.win.doLayout();
				this.manageUnits = null;
				if (this.form) {
					this.validate();
				}

				if (!this.initDataId) {
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						this.form.getForm().findField("sourceHomeAddress")
								.enable();
					}
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						this.form.getForm().findField("targetHomeAddress")
								.enable();
					}

				}
			}

		})