$package("chis.application.hy.script.risk")
$import("util.Accredit", "chis.script.BizFieldSetFormView")

chis.application.hy.script.risk.HypertensionRiskVisitForm = function(cfg) {
	cfg.colCount = 3
	cfg.isCombined = false
	cfg.autoLoadSchema = true
	cfg.labelWidth = 100;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 160;
	cfg.isAutoScroll = true;
	chis.application.hy.script.risk.HypertensionRiskVisitForm.superclass.constructor
			.apply(this, [cfg]);
	this.JKCFRecords = {};
	this.formId = "_GXYGWSF";
	this.on("loadData", this.onLoadData, this);
}
var healthGuidance_ctx = null;
function onImageClick() {
	healthGuidance_ctx.openCardLayoutWin()
}
Ext.extend(chis.application.hy.script.risk.HypertensionRiskVisitForm,
		chis.script.BizFieldSetFormView, {
			initPanel : function(sc) {
				this.formId = "_GXYGWSF";
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
				var colCount = this.colCount;

				var table = {
					layout : 'tableform',
					layoutConfig : {
						columns : colCount,
						tableAttrs : {
							cellpadding : '2',
							cellspacing : "2"
						}
					},
					items : []
				}
				if (!this.autoFieldWidth) {
					this.forceViewWidth = (defaultWidth + (this.labelWidth || 80))
							* colCount
					table.layoutConfig.forceWidth = this.forceViewWidth
				}
				var groups = {};
				var otherItems = [];
				var items = schema.items
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					if (it.id == "healthTeach") {
						var panel = new Ext.Panel({
							id : "healthTeach" + this.formId,
							border : false,
							colspan : 8,
							html : '<table style="vertical-align:middle;"><tr>'
									+ '<th width="18%">健康教育：</th><td width="77%">'
									+ '<div id="div_JKJY'
									+ this.formId
									+ '"/></td>'
									+ '<td><p  style="float:right;margin:0 8px 0 0;"><a title="引入健康处方">'
									+ '<img id="importHER" onclick="onImageClick();" src="resources/chis/resources/app/biz/images/jkchufang.png"/>'
									+ '</a></p></td></tr>' + '</table>',
							frame : false,
							autoScroll : true
						});
						this.addPanel = panel;
						var gname = it.group
						if (!gname) {
							gname = "_default"
						}
						if (!groups[gname])
							groups[gname] = [];
						groups[gname].push(panel)
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)

					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}
					var gname = it.group
					if (!gname) {
						gname = "_default"
					}
					if (!groups[gname])
						groups[gname] = [];
					groups[gname].push(f)
				}
				for (s in groups) {
					var border = true;
					var collapsible = true;
					var title = "<font size='2'>" + s + "</font>";
					if (s == "_default") {
						border = false;
						collapsible = false;
						title = null;
					}
					var group = groups[s];
					if (group.length > 0) {
						var fs = new Ext.form.FieldSet({
									border : border,
									collapsible : collapsible,
									width : this.fldDefaultWidth || 100,
									autoHeight : true,
									anchor : "100%",
									colspan : this.colCount,
									bodyStyle : 'overflow-x:hidden; overflow-y:auto',
									style : {
										marginBottom : '5px'
									},
									items : {
										layout : 'tableform',
										layoutConfig : {
											columns : colCount,
											tableAttrs : {
												cellpadding : '2',
												cellspacing : "2"
											}
										},
										items : group
									}
								})
						if (title) {
							fs.title = title;
						}
						table.items.push(fs)
					}
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
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			openCardLayoutWin : function() {
				var module = this.createCombinedModule(
						"refRecipeImportModule_GXYGWSF",
						this.refRecipeImportModule);
				module.exContext = this.exContext;
				module.on("importRecipe", this.onImportRecipe, this);
				module.fromId = "GXYGWSF";
				this.recipeImportModule = module;
				module.JKCFRecords = this.JKCFRecords;
				var win = module.getWin();
				win.setHeight(500);
				win.setWidth(1000);
				win.show();
				return;
			},
			createAllJKJYHTML : function(JKCFRecords) {
				this.JKCFRecords = {};
				var html = '<table cellspacing="0" id="the-table' + this.formId
						+ '">';
				for (var i = 0; i < JKCFRecords.length; i++) {
					var r = JKCFRecords[i];
					var healthTeach = r.healthTeach;
					var diagnoseId = parseInt(r.diagnoseId);
					this.JKCFRecords[diagnoseId] = r;
					html += '<tr style="height:40px;border-bottom:1px solid #ccd3dc;"><td>'
							+ '<pre id="JKCF'
							+ this.formId
							+ diagnoseId
							+ '" style="white-space:pre-wrap;word-wrap:break-word;">'
							+ healthTeach + '</pre></td></tr>'
				}
				html += '</table>';
				return html;
			},
			onImportRecipe : function(JKCFRecords) {
				var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
				document.getElementById("div_JKJY" + this.formId).innerHTML = jkjyHtml;
			},
			onLoadData : function(entryName, body) {
				if (body && (body.status == "1" || body.status.key == "1")) {
					this.setButtonEnable(false);
					return;
				}
				this.setButtonEnable(true);
			},
			initFormData : function(data) {
				chis.application.hy.script.risk.HypertensionRiskVisitForm.superclass.initFormData
						.call(this, data);
				var JKCFRecords = data.JKCFRecords;
				var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
				document.getElementById("div_JKJY" + this.formId).innerHTML = jkjyHtml;
				this.onEstimateDate();
			},
			onEstimateDate : function() {
				var form = this.form.getForm()
				var month = chis.script.util.helper.Helper.getAgeMonths(Date
								.parseDate(this.exContext.empiData.birthday,
										"Y-m-d"), new Date());
				var age = month / 12
				form.findField("age").setValue(parseInt(age))
			},
			doNew : function() {
				chis.application.hy.script.risk.HypertensionRiskVisitForm.superclass.doNew
						.call(this);
				var JKCFRecords = {};
				var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
				document.getElementById("div_JKJY" + this.formId).innerHTML = jkjyHtml;
			},
			onReady : function() {
				healthGuidance_ctx = this;
				chis.application.hy.script.risk.HypertensionRiskVisitForm.superclass.onReady
						.call(this)

				var form = this.form.getForm()
				var weight = form.findField("weight")
				this.weight = weight
				if (weight) {
					weight.on("keyup", this.onWeightCheck, this)
				}

				var height = form.findField("height")
				this.height = height
				if (height) {
					height.on("keyup", this.onHeightCheck, this)
				}
				var visitEffect = form.findField("visitEffect");
				this.visitEffect = visitEffect;
				if (visitEffect) {
					visitEffect.on("keyup", this.onEffectCase, this);
					visitEffect.on("select", this.onEffectCase, this)
				}
			},
			onEffectCase : function() {
				var value = this.visitEffect.getValue();
				var form = this.form.getForm();
				var stopDate = form.findField("stopDate");
				var stopCause = form.findField("stopCause");
				if (value == "3") {
					stopDate.enable();
					stopCause.enable();
				} else {
					stopDate.disable();
					stopCause.disable();
				}
			},
			onHeightCheck : function() {
				if (this.height.getValue() == "") {
					this.form.getForm().findField("bmi").setValue()
					return
				}
				if (this.height.getValue() > 250
						|| this.height.getValue() < 130) {
					this.height.markInvalid("身高必须在130-250之间")
					this.height.focus()
					return
				}
				this.onCalculateBMI()
			},
			onWeightCheck : function() {
				if (this.weight.getValue() == "") {
					this.form.getForm().findField("bmi").setValue()
					return
				}
				if (this.weight.getValue() > 140 || this.weight.getValue() < 20) {
					this.weight.markInvalid("体重必须在20-140之间")
					this.weight.focus()
					return
				}
				this.onCalculateBMI()
			},
			onCalculateBMI : function() {
				var form = this.form.getForm()
				var bmi = form.findField("bmi")
				if (bmi) {
					var w = this.weight.getValue()
					var h = this.height.getValue()
					if (w == "" || h == "") {
						return
					}
					var b = (w / (h * h / 10000)).toFixed(2)
					bmi.setValue(b)
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
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.hypertensionService",
					serviceAction : "ifHypertensionRecordExist",
					method : "execute",
					cnd : [
							'and',
							['eq', ['$', 'a.status'], ['s', '0']],
							['eq', ['$', 'a.empiId'],
									['s', this.exContext.ids.empiId]]],
					schema : "chis.application.hy.schemas.MDC_HypertensionRecord"
				})
				var hasRecord = true;
				this.needCancellation = false;
				if (result.code == 200) {
					hasRecord = false;
				}
				if (hasRecord) {
					if (values && values.visitEffect == "1") {
						MyMessageTip.msg("提示", "该病人已建高血压档案，"
										+ "请核实确认后选择转归情况为终止管理，" + "否则无法继续保存。",
								true, 10);
						return;
					}
					if (values && values.visitEffect == "3") {
						if (values.stopCause != "1") {
							MyMessageTip.msg("提示", "该病人已建高血压档案，"
											+ "请选择终止原因为转高血压管理，" + "否则无法继续保存。",
									true, 10);
							return;
						}
						this.needCancellation = true;
					}
				} else {
					if (values && values.visitEffect == "3") {
						if (values.stopCause == "1") {
							var constriction = values.constriction;
							var diastolic = values.diastolic;
							if (parseInt(constriction) < 140
									&& parseInt(diastolic) < 90) {
								MyMessageTip.msg("提示", "收缩压或舒张压未达到转高血压条件，请核实！",
										true, 10);
								return;
							}
							Ext.Msg.show({
										title : '高血压档案',
										msg : '该病人尚未创建高血压档案，不能转入高血压管理，是否立即创建高血压档案？',
										modal : true,
										width : 300,
										buttons : Ext.MessageBox.YESNO,
										multiline : false,
										fn : function(btn, text) {
											if (btn == "yes") {
												this.fireEvent("addModule",
														"C_01");
											}
										},
										scope : this
									});
						} else {
							this.needCancellation = true;
						}
					}
				}
				Ext.apply(this.data, values);
				this.saveToServer(values)
			},
			saveToServer : function(saveData) {
				saveData.empiId = this.exContext.ids.empiId
				saveData.phrId = this.exContext.ids.phrId
				saveData.planId = this.exContext.args.planId
				saveData.planDate = this.exContext.args.planDate
				saveData.sn = this.exContext.args.sn
				saveData.riskId = this.exContext.ids.riskId
				if (this.JKCFRecords) {
					saveData.JKCF = this.JKCFRecords;
				}
				saveData.healthTeach =null;
				saveData.needCancellation = this.needCancellation || false;
				chis.application.hy.script.risk.HypertensionRiskVisitForm.superclass.saveToServer
						.call(this, saveData)
			},
			afterSaveData : function(entryName, op, json, data) {
				Ext.apply(data, json.body);
				var JKCFRecords = data.JKCFRecords;
				if (!JKCFRecords) {
					return;
				}
				var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
				document.getElementById("div_JKJY" + this.formId).innerHTML = jkjyHtml;
			},
			doPrintRecipe : function() {
				if (!this.initDataId) {
					return;
				}
				var url = "resources/chis.prints.template.HealthRecipelManage.print?type="
						+ 1 + "&wayId=" + this.initDataId + "&guideWay=07";
				url += "&temp=" + new Date().getTime()
				var win = window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

				if (Ext.isIE6) {
					win.print()
				} else {
					win.onload = function() {
						win.print()
					}
				}
			},
			setButtonEnable : function(status) {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				for (var i = 0; i < btns.getCount(); i++) {
					var btn = btns.item(i);
					if (status)
						btn.enable()
					else
						btn.disable()
				}
			}
		});