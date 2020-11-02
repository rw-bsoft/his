$package("chis.application.dbs.script.visit")
$import("util.Accredit", "chis.script.BizTableFormView")
chis.application.dbs.script.visit.DiabetesVisitHealthTeachForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 200;
	cfg.autoLoadData = false
	chis.application.dbs.script.visit.DiabetesVisitHealthTeachForm.superclass.constructor
			.apply(this, [cfg]);
	this.JKCFRecords = {};
	this.formId = "_TNBSF";
}
var healthGuidance_ctx = null;
function onImageClick() {
	healthGuidance_ctx.openCardLayoutWin()
}
Ext.extend(chis.application.dbs.script.visit.DiabetesVisitHealthTeachForm,
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
							cellspacing : "2"
						}
					},
					items : []
				}
				if (!this.autoFieldWidth) {
					var forceViewWidth = (defaultWidth + (this.labelWidth || 80))
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
					if (it.id == "healthTeach") {
						var panel = new Ext.Panel({
							id : "healthTeach" + this.formId,
							border : false,
							colspan : 3,
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
						table.items.push(panel);
						continue;
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
					autoScroll : true,
					floating : false
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
			onReady : function() {
				healthGuidance_ctx = this;
				chis.application.dbs.script.visit.DiabetesVisitHealthTeachForm.superclass.onReady
						.call(this);
			},
			saveToServer : function(saveData) {
				saveData.wayId = this.exContext.args.r.get("visitId");
				saveData.examineUnit = this.mainApp.dept;
				saveData.empiId = this.exContext.ids.empiId;
				saveData.phrId = this.exContext.ids.phrId;
				saveData.guideWay = "03";
				if (this.JKCFRecords) {
					saveData.JKCF = this.JKCFRecords;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (this.initDataId == null) {
					this.op = "create";
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.diabetesVisitService",
							op : this.op,
							schema : this.entryName,
							body : saveData,
							method : "execute",
							serviceAction : "saveDiabetesHealthTeach"
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data)
							this.op = "update"
						}, this)
			},

			openCardLayoutWin : function() {
				var module = this.createCombinedModule(
						"refRecipeImportModule_TNBSF",
						this.refRecipeImportModule);
				module.exContext = this.exContext;
				module.on("importRecipe", this.onImportRecipe, this);
				module.fromId = "TNBSF";
				this.recipeImportModule = module;
				module.JKCFRecords=this.JKCFRecords;
				var win = module.getWin();
				win.setHeight(580);
				win.setWidth(1000);
				win.show();
				return;
			},
			initFormData : function(data) {
				chis.application.dbs.script.visit.DiabetesVisitHealthTeachForm.superclass.initFormData
						.call(this, data);
				var JKCFRecords = data.JKCFRecords;
				var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
				document.getElementById("div_JKJY" + this.formId).innerHTML = jkjyHtml;
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

			loadData : function() {
				this.doNew()
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesVisitService",
							serviceAction : "getDiabetesHealthTeach",
							method : "execute",
							body : {
								"wayId" : this.exContext.args.r.get("visitId")
							}
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.loadData);
					return
				}
				if (result.json.body) {
					this.initFormData(result.json.body)
					this
							.fireEvent("loadData", this.entryName,
									result.json.body);
					this.op = "update"
				} else {
					this.initDataId = null
				}
				// this.resetButton(this.exContext.control)
			},
			resetButton : function(data) {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount();
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					var obj = data["_actions"]
					if (obj) {
						var status = obj["update"]
						if (status) {
							btn.enable()
						} else {
							btn.disable()
						}
					}
				}
			},
			doPrintRecipe : function() {
				if(!this.exContext.args.r){
					return;
				}
				var url = "resources/chis.prints.template.HealthRecipelManage.print?type="
						+ 1 + "&wayId=" + this.exContext.args.r.get("visitId")+ "&guideWay=03";
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
			}
		});