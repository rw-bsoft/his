﻿$package("chis.application.phe.script")
$import("chis.script.BizTableFormView");

chis.application.phe.script.PHE_RelevantInformationForm = function(cfg) {
	cfg.colCount = 4;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 110;
	cfg.labelWidth = 80;
	cfg.width = 780;
	cfg.height = Ext.getBody().getHeight() > 600 ? 510 : Ext.getBody()
			.getHeight()
			* 0.9;
	chis.application.phe.script.PHE_RelevantInformationForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
	this.on("beforePrint", this.onBeforePrint, this);
	this.on("beforeCreate",this.onCreate,this);
};
Ext.extend(chis.application.phe.script.PHE_RelevantInformationForm,
		chis.script.BizTableFormView, {
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
					// if("form"==this.mainApp.exContext.areaGridShowType){
					// var forceViewWidth = (defaultWidth + (this.labelWidth ||
					// 140))
					// * colCount
					//							
					// }else{
					var forceViewWidth = (defaultWidth + (this.labelWidth || 60))
							* colCount
					// }
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

						if (it.id == "reportArea") {
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
										fieldLabel : "<font  color=red>报告地区:<font>",
										"width" : 285

									});
							this.ff = ff;
							this.ff.allowBlank = false;
							this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
							continue;

						}
						if (it.id == "eventArea") {
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
										fieldLabel : "<font  color=red>发生地区:<font>",
										"width" : 285

									});
							this.ff1 = ff1;
							this.ff1.allowBlank = false;
							this.ff1.invalidText = "必填字段";
							this.ff1.regex = /(^\S+)/;
							this.ff1.regexText = "前面不能有空格字符";
							table.items.push(ff1)
							continue;

						}
						if (it.id == "detailAdreess") {
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
										fieldLabel : "<font  color=red>详细地点:<font>",
										"width" : 287

									});
							this.ff2 = ff2;
							this.ff2.allowBlank = false;
							this.ff2.invalidText = "必填字段";
							this.ff2.regex = /(^\S+)/;
							this.ff2.regexText = "前面不能有空格字符";
							table.items.push(ff2)
							continue;

						}
					}
					var f = this.createField(it)
					f.index = i;
					// if ("form" == this.mainApp.exContext.areaGridShowType) {
					// f.anchor = it.anchor || "90%"
					// } else {
					f.anchor = it.anchor || "100%"
					// }
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
				if ("reportArea" == data.areaGridListId) {
					this.form.getForm().findField("reportArea")
							.setValue(data.regionCode_text);
					this.data.reportArea_text = data.regionCode_text;
					this.data.reportArea = data.regionCode;
				}

				if ("eventArea" == data.areaGridListId) {
					this.form.getForm().findField("eventArea")
							.setValue(data.regionCode_text);
					this.data.eventArea_text = data.regionCode_text;
					this.data.eventArea = data.regionCode;
				}
				if ("detailAdreess" == data.areaGridListId) {
					this.form.getForm().findField("detailAdreess")
							.setValue(data.regionCode_text);
					this.data.detailAdreess_text = data.regionCode_text;
					this.data.detailAdreess = data.regionCode;
				}
			},
			initFormData : function(data) {

				chis.application.phe.script.PHE_RelevantInformationForm.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType) {
					if (data.reportArea) {
						if (data.reportArea.key) {
							this.form.getForm().findField("reportArea")
									.setValue(data.reportArea.text);
							this.form.getForm().findField("reportArea")
									.disable();
							this.data.reportArea = data.reportArea.key;
							this.data.reportArea_text = data.reportArea.text;
						}
					}
					if (data.eventArea) {
						if (data.eventArea.key) {
							this.form.getForm().findField("eventArea")
									.setValue(data.eventArea.text);
							this.form.getForm().findField("eventArea")
									.disable();
							this.data.eventArea = data.eventArea.key;
							this.data.eventArea_text = data.eventArea.text;
						}
					}
					if (data.detailAdreess) {
						if (data.detailAdreess.key) {
							this.form.getForm().findField("detailAdreess")
									.setValue(data.detailAdreess.text);
							this.form.getForm().findField("detailAdreess")
									.disable();
							this.data.detailAdreess = data.detailAdreess.key;
							this.data.detailAdreess_text = data.detailAdreess.text;
						}
					}
				}
			},
			getReportAreaRegion : function(comb, node) {
				var isFamily = node.attributes['isFamily'];
				if (isFamily <= 'c' && isFamily != '1') {
					return false;
				}
				return true;
			},
			getEventAreaRegion : function(comb, node) {
				var isFamily = node.attributes['isFamily'];
				if (isFamily <= 'c' && isFamily != '1') {
					return false;
				}
				return true;
			},
			onReady : function() {
				
				chis.application.phe.script.PHE_RelevantInformationForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var reportCatagory = form.findField("reportCatagory");
				if (reportCatagory) {
					reportCatagory.on("select", this.onSelectReportCatagory,
							this);
				}

				var cardinalSymptom = form.findField("cardinalSymptom");
				if (cardinalSymptom) {
					cardinalSymptom.on("select", this.onSelectCardinalSymptom,
							this);
				}

				var reportArea = form.findField("reportArea");
				if (reportArea) {
					reportArea.on("beforeselect", this.getReportAreaRegion,
							this);
				}
				var eventArea = form.findField("eventArea");
				if (eventArea) {
					eventArea.on("beforeselect", this.getEventAreaRegion, this);
				}

				var eventAdreess = form.findField("eventAdreess");
				if (eventAdreess) {
					eventAdreess.on("select", this.onSelectEventAdreess, this);
				}
			},
			onLoadData : function onLoadData(entry, body) {
				reportCatagory = body.reportCatagory;
				if (reportCatagory.key == 2) {
					this.changeFieldState(false, "reportNumber");
				} else {
					this.changeFieldState(true, "reportNumber");
				}
				var cardinalSymptom = body.cardinalSymptom;
				if (cardinalSymptom.key == 6) {
					this.changeFieldState(false, "otherSymptom");
				} else {
					this.changeFieldState(true, "otherSymptom");
				}

				var eventAdreess = body.eventAdreess.key;
				if (eventAdreess) {
					var has2, has1 = null;
					var adressArray = eventAdreess.split(",");
					for (var i = 0; i < adressArray.length; i++) {
						if (adressArray[i] == 2) { // 如果医疗机构选中
							has2 = true;
						} else if (adressArray[i] == 1) {// 学校
							has1 = true;

						}
					}
					if (has2) {
						this
								.changeFieldState(false,
										"medicalOrganizationsType");
						this.changeFieldState(false, "medicalOrganizationsDep");
					} else {
						this.changeFieldState(true, "medicalOrganizationsType");
						this.changeFieldState(true, "medicalOrganizationsDep");
					}
					if (has1) {
						this.changeFieldState(false, "schoolType");
					} else {
						this.changeFieldState(true, "schoolType");
					}
				}
			},
			onSelectReportCatagory : function(item) {
				if (item.getValue() == 2) {
					this.changeFieldState(false, "reportNumber");
				} else {
					this.changeFieldState(true, "reportNumber");
				}
			},
			onSelectCardinalSymptom : function(item) {
				if (item.getValue() == 6) {
					this.changeFieldState(false, "otherSymptom");
				} else {
					this.changeFieldState(true, "otherSymptom");
				}
			},
			onSelectEventAdreess : function(item) {

				var value = item.getValue();
				var has2, has1 = null;
				var adressArray = value.split(",");
				for (var i = 0; i < adressArray.length; i++) {
					if (adressArray[i] == 2) { // 如果医疗机构选中
						has2 = true;

					} else if (adressArray[i] == 1) {// 学校
						has1 = true;
					}
				}
				if (has2) {

					this.changeFieldState(false, "medicalOrganizationsType");
					this.changeFieldState(false, "medicalOrganizationsDep");
				} else {
					this.changeFieldState(true, "medicalOrganizationsType");
					this.changeFieldState(true, "medicalOrganizationsDep");
				}
				if (has1) {
					this.changeFieldState(false, "schoolType");
				} else {
					this.changeFieldState(true, "schoolType");
				}
			},
			onBeforePrint : function(type, pages, ids_str) {
				pages.value = ["chis.prints.template.burstPublicHealth"];
				ids_str.value = "&RecordID=" + this.initDataId;
				return true;
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
							// end
						}

						if ("form" == this.mainApp.exContext.areaGridShowType) {

							if ("reportArea" == it.id) {
								v = this.data.reportArea;
							}
							if ("eventArea" == it.id) {
								v = this.data.eventArea;
							}
							if ("detailAdreess" == it.id) {
								v = this.data.detailAdreess;
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
			},onCreate:function(){
			//创建页面的时候，做些动作
			this.form.getForm().findField("reportArea").enable();
			this.form.getForm().findField("eventArea").enable();
			this.form.getForm().findField("detailAdreess").enable();
			
			
			
			}
		});
