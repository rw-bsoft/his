$package("chis.application.tr.script.tprc");

$import("chis.script.BizTableFormView", "util.widgets.LookUpField",
		"chis.script.util.query.QueryModule")

chis.application.tr.script.tprc.TumourPatientReportCardForm2 = function(cfg) {
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.colCount = 3
	cfg.labelWidth = 100;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 150;
	cfg.saveServiceId = "chis.tumourPatientReportCardService";
	cfg.saveAction = "saveTumourPatientReportDieCard";
	chis.application.tr.script.tprc.TumourPatientReportCardForm2.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
	this.on("winShow", this.onWinShow, this);
	this.on("save",this.onSave,this);
}

Ext.extend(chis.application.tr.script.tprc.TumourPatientReportCardForm2,
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
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "regionCode") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
										name : it.id,
										colspan : 3,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										// readOnly : true, //只读
										// disabled : true,
										fieldLabel : "<font  color=red>网格地址(组):<font>",
										"width" : 630
									});
							this.ff = ff;
							this.ff.allowBlank = false;
							this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
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
					this.form.getForm().findField("regionCode")
							.setValue(data.regionCode_text);
					if(!this.exContext.args){
						this.exContext.args = {};
					}
					this.exContext.args.regionCode_text = data.regionCode_text;
					this.exContext.args.regionCode = data.regionCode;
			},
			
			loadData : function() {
				chis.application.tr.script.tprc.TumourPatientReportCardForm2.superclass.loadData
						.call(this);
				this.onIsDeathSelect();
			},
			
			initRegionCode : function(){
				var regionCode = this.form.getForm().findField("regionCode");
				if ("form" == this.mainApp.exContext.areaGridShowType) {
					if(this.regionCode){
						regionCode.setValue(this.regionCode.text)
						if(!this.exContext.args){
							this.exContext.args={};
						}
						this.exContext.args.regionCode_text = this.regionCode.text;
						this.exContext.args.regionCode = this.regionCode.key;
					}
					if(this.exContext.args){
					   if(this.exContext.args.regionCode_text){
					   	regionCode.setValue(this.exContext.args.regionCode_text);
					   	return;
					   }
					   if(this.exContext.args.regionCode.text){
							regionCode.setValue(this.exContext.args.regionCode.text)
							return;
					   }
					}
				}else{
					regionCode.setValue(this.regionCode);
				}
			},

			onWinShow : function() {
				if (this.phrId) {
					var phrIdField = this.form.getForm().findField("phrId");
					phrIdField.setValue(this.phrId);
				}
			},

			getSaveRequest : function(savaData) {
				savaData.empiId = this.empiId;
				savaData.phrId = this.phrId;
				if ("form" == this.mainApp.exContext.areaGridShowType) {
					savaData.regionCode_text = this.exContext.args.regionCode_text;
					savaData.regionCode = this.exContext.args.regionCode;
				}
				return savaData;
			},

			onLoadData : function(entryName, body) {
				this.initRegionCode()
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.tumourPatientReportCardService",
							serviceAction : "getTPRCControl",
							method : "execute",
							body : body
						});
				var control = result.json.body;
				if(control){
					this.exContext.control = control
					var btns = this.form.getTopToolbar().items;
					if (!btns) {
						return;
					}
					var saveBtn = btns.items[0];
					if(this.initDataId){
						if(control.update){
							saveBtn.enable();
						}else{
							saveBtn.disable();
						}
					}else{
						if(control.create){
							saveBtn.enable();
						}else{
							saveBtn.disable();
						}
					}
				}
			},
			
			onSave : function(op,entryName,json,data){
				var regionCode = this.form.getForm().findField("regionCode");
				if ("form" == this.mainApp.exContext.areaGridShowType) {
					regionCode.setValue(data.regionCode.text)
				}
			},

			onReady : function() {
				chis.application.tr.script.tprc.TumourPatientReportCardForm2.superclass.onReady
						.call(this);
				var frm = this.form.getForm();
				var isDeathFld = frm.findField("isDeath");
				if (isDeathFld) {
					isDeathFld.on("select", this.onIsDeathSelect, this);
				}
				var reportDoctorFld = frm.findField("reportDoctor");
				if (reportDoctorFld) {
					reportDoctorFld.on("select", this.onReportDoctorSelect,
							this);
				}
				var manaDoctorId = frm.findField("manaDoctorId");
				if (manaDoctorId) {
					manaDoctorId.on("select", this.onManaDoctorIdSelect, this);
				}
				var tumourNameFld = frm.findField("tumourName");
				if (tumourNameFld) {
					tumourNameFld.on("lookup", this.doTNQuery, this);
					tumourNameFld.on("clear", this.doTNClear, this);
					tumourNameFld.validate();
				}
			},
			doTNQuery : function(field) {
				if (!field.disabled) {
					var tumourNameQuery = this.midiModules["tumourNameQuery"];
					if (!tumourNameQuery) {
						tumourNameQuery = new chis.script.util.query.QueryModule(
								{
									title : "肿瘤诊断名称选择",
									autoLoadSchema : true,
									isCombined : true,
									autoLoadData : false,
									mutiSelect : false,
									queryCndsType : "1",
									entryName : "chis.application.his.schemas.GY_JBBMQuery",
									buttonIndex : 3,
									selectFormColCount : 3,
									itemHeight : 125
								});
						this.midiModules["tumourNameQuery"] = tumourNameQuery;
					}
					tumourNameQuery.on("recordSelected", function(r) {
								if (!r) {
									return;
								}
								var frmData = r[0].data;
								var frm = this.form.getForm();
								var tumourNameFld = frm.findField("tumourName");
								if (tumourNameFld) {
									tumourNameFld.setValue(frmData.JBMC)
								}
								var ICD10CodeFld = frm.findField("ICD10Code");
								if (ICD10CodeFld) {
									ICD10CodeFld.setValue(frmData.ICD10)
								}
							}, this);
					var win = tumourNameQuery.getWin();
					win.setPosition(250, 100);
					win.show();
					tumourNameQuery.form.doSelect();
				}
			},
			doTNClear : function() {
				var frm = this.form.getForm();
				var tumourNameFld = frm.findField("tumourName");
				if (tumourNameFld) {
					tumourNameFld.setValue();
				}
				this.fireEvent("selectNoTumourName");
			},
			onIsDeathSelect : function() {
				var frm = this.form.getForm();
				var isDeathFld = frm.findField("isDeath");
				var deathDateFld = frm.findField("deathDate");
				var isDeath = isDeathFld.getValue();
				if (deathDateFld) {
					if (isDeath == 'y') {
						deathDateFld.enable();
						deathDateFld.allowBlank = false;
						deathDateFld["not-null"] = true;
						deathDateFld.getEl().up('.x-form-item')
								.child('.x-form-item-label')
								.update("<span style='color:red'>"
										+ deathDateFld.fieldLabel + ":</span>");
						frm.findField("agreeVisit").setValue({
									key : 'n',
									text : '否'
								});
					} else {
						deathDateFld.disable();
						deathDateFld.allowBlank = true;
						deathDateFld["not-null"] = false;
						deathDateFld.getEl().up('.x-form-item')
								.child('.x-form-item-label')
								.update(deathDateFld.fieldLabel + ":");
						frm.findField("agreeVisit").setValue({
									key : 'y',
									text : '是'
								});
					}
					deathDateFld.validate();
				}
			},

			onReportDoctorSelect : function(combo, node) {
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
						});
				var reportUnit = this.form.getForm().findField("reportUnit");
				this.setManaUnit(result.json.manageUnit, reportUnit);
			},

			onManaDoctorIdSelect : function(combo, node) {
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
						});
				var manaUnitId = this.form.getForm().findField("manaUnitId");
				this.setManaUnit(result.json.manageUnit, manaUnitId);
			},

			setManaUnit : function(manageUnit, combox) {
				if (!combox) {
					return;
				}
				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}
				if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
					combox.setValue(manageUnit);
					combox.disable();
				} else {
					combox.enable();
					combox.reset();
				}
			}
		});