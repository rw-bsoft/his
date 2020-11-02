$package("phis.application.cic.script");

$import("phis.script.SimpleModule");

phis.application.cic.script.HealthRecipeImportModule = function(cfg) {
	phis.application.cic.script.HealthRecipeImportModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.overIndex = 0;
}
Ext.extend(phis.application.cic.script.HealthRecipeImportModule,
		phis.script.SimpleModule, {
			initPanel : function() {// 加载panel
				var actions = this.actions;
				var bar = this.createTopBar();
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : [bar],
							items : [{
										layout : "border",
										border : false,
										split : true,
										region : 'west',
										height : 580,
										width : 500,
										items : [{
													layout : "fit",
													border : false,
													split : true,
													region : 'north',
													height : 320,
													items : this
															.getRecipeList()
												}, {
													layout : "fit",
													border : false,
													split : true,
													region : 'center',
													items : this
															.getRecipeHasList()
												}]
									}, {
										layout : "fit",
										border : false,
										split : true,
										frame : true,
										region : 'center',
										items : this.getRecipeForm()
									}]
						});
				this.panel = panel;
				this.panel.on("render", this.onReady, this)
				return panel;
			},
			onReady : function() {
				this.recipeField.on("keydown", this.KeyUpField, this);
				this.nameField.on("keydown", this.KeyUpField, this);
				this.ICD10Field.on("keydown", this.KeyUpField, this);
			},
			KeyUpField : function(field, e) {
				if (e.getKey() == e.ENTER) {
					e.stopEvent();
					this.doCndQuery();
					return
				}
			},
			createTopBar : function() {
				var recipeLabel = new Ext.form.Label({
							html : "健康处方拼音码："
						});
				var recipeField = new Ext.form.TextField({
							width : this.cndFieldWidth || 100,
							enableKeyEvents : true,
							name : "recipeName"
						});
				this.recipeField = recipeField;
				var nameLabel = new Ext.form.Label({
							html : "疾病拼音码："
						});
				var nameField = new Ext.form.TextField({
							width : this.cndFieldWidth || 100,
							enableKeyEvents : true,
							name : "diagnoseName"
						});
				this.nameField = nameField;
				var ICD10Label = new Ext.form.Label({
							html : "疾病编码："
						});
				var ICD10Field = new Ext.form.TextField({
							width : this.cndFieldWidth || 100,
							enableKeyEvents : true,
							name : "ICD10"
						});
				this.ICD10Field = ICD10Field;
				ICD10Field.on("change", this.ICD10FieldChange, this);
				var queryBtn = new Ext.Button({
							text : "查询",
							width : 70,
							iconCls : "query"
						});
				this.queryBtn = queryBtn;
				queryBtn.on("click", this.doCndQuery, this);
				var importBtn = new Ext.Button({
							text : "调入",
							width : 70,
							iconCls : "healthDoc_import"
						});
				this.importBtn = importBtn;
				importBtn.on("click", this.doImportJKCF, this);
				var printBtn = new Ext.Button({
							text : "打印",
							width : 70,
							iconCls : "print"
						});
				this.printBtn = printBtn;
				printBtn.on("click", this.doPrintJKCF, this);
				return [recipeLabel, recipeField, '-', nameLabel, nameField,
						'-', ICD10Label, ICD10Field, '-', queryBtn, importBtn,
						printBtn]
			},
			saveText : function(recordId, config) {
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "chis.healthRecipelManageService",
							serviceAction : "saveHealthRecipelToCache",
							recordId : recordId,
							body : config
						});
				if (result.code > 200) {
					MyMessageTip.msg("提示", result.msg, true);
					return false;
				}
			},

			doPrintJKCF : function() {
				var r = this.RecipeHasList.getSelectedRecord();
				if (r) {
					var JKCFField = this.RecipeForm.getForm().findField('JKCF'
							+ this.fromId);
					var index = this.RecipeHasList.store.indexOf(r);
					var overValue = JKCFField.getValue();
					var overRecord = this.RecipeHasList.store
							.getAt(this.overIndex);
					if (overRecord && overValue) {
						this.RecipeHasList.store.getAt(this.overIndex).set(
								"healthTeach", overValue);
					}
					this.overIndex = index;
				}
				var store = this.RecipeHasList.store;
				var records = {};
				var i = 1;
				store.each(function(r) {
							var config = {
								diagnoseId : r.get("diagnoseId"),
								recordId : r.get("recordId"),
								healthTeach : r.get("healthTeach"),
								diagnoseName : r.get("diagnoseName")
							}
							this.saveText(r.get("diagnoseId"), config)
							records[i] = r.get("diagnoseId");
							i++;
						}, this);
				if (!records || records.length == 0) {
					return;
				}
				var url = "resources/chis.prints.template.HealthRecipelManage.print?type="
						+ 1 + "&body=" + encodeURI(Ext.encode(records));
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
			ICD10FieldChange : function(field) {
				var ICD10 = field.getValue();
				if (ICD10 == null || ICD10 == "") {
					this.ZZDICD10 = null;
				}
			},
			getRecipeHasList : function() {
				this.RecipeHasList = this.createModule("refRecipeHasList"
								+ this.fromId, this.refRecipeHasList);
				this.RecipeHasGrid = this.RecipeHasList.initPanel();
				this.RecipeHasGrid.on("rowdblclick", this.onListHasRowDbClick,
						this);
				this.RecipeHasGrid.on("rowclick", this.onListRowClick, this);
				return this.RecipeHasGrid;
			},
			getRecipeList : function() {
				this.RecipeList = this.createModule("RecipeList" + this.fromId,
						this.refRecipeList);
				this.RecipeGrid = this.RecipeList.initPanel();
				this.RecipeGrid.on("rowdblclick", this.onListRowDbClick, this);
				this.RecipeList.on("loadData", this.onListLoadData, this);
				this.RecipeList.on("beforeLoad", this.onListBeforeLoad, this);
				return this.RecipeGrid;
			},
			getRecipeForm : function() {
				var form = new Ext.form.FormPanel({
							layout : 'absolute',
							items : [{
										xtype : 'textarea',
										name : 'JKCF' + this.fromId,
										maxLength : 2000,
										anchor : '100% 100%'
									}]
						});
				this.RecipeForm = form;
				return form;
			},
			onListLoadData : function(store) {
				this.initRecords = [];
				store.each(function(r) {
							var record = {
								diagnoseId : r.get("diagnoseId"),
								healthTeach : r.get("healthTeach")
							}
							this.initRecords.push(record)
						}, this);
				this.RecipeHasList.store.removeAll();
				if (this.winShow && this.JKCFRecords) {
					for (var diagnoseId in this.JKCFRecords) {
						var record = this.JKCFRecords[diagnoseId];
						this.RecipeList.store.each(function(r) {
							if (r.get("diagnoseId") == record.diagnoseId) {
								r.set("healthTeach", record.healthTeach);
								this.RecipeHasList.store.add(r);
								this.RecipeList.store.remove(r);
								var index = this.RecipeHasList.store.indexOf(r);
								this.RecipeHasList.selectedIndex = index;
								this.RecipeHasList.selectRow(index);
								this.RecipeHasGrid.fireEvent("rowclick",
										this.RecipeHasGrid);
							}
						}, this);
					}
				}
				this.winShow = false;
			},
			onListRowClick : function(grid, index, e) {
				var r = this.RecipeHasList.getSelectedRecord();
				if (!r) {
					return;
				}
				var JKCFField = this.RecipeForm.getForm().findField('JKCF'
						+ this.fromId);
				var index = this.RecipeHasList.store.indexOf(r);
				var overValue = JKCFField.getValue();
				var overRecord = this.RecipeHasList.store.getAt(this.overIndex);
				if (overRecord && overValue) {
					this.RecipeHasList.store.getAt(this.overIndex).set(
							"healthTeach", overValue);
				}
				this.overIndex = index;
				var healthTeach = r.get("healthTeach");
				if (JKCFField) {
					JKCFField.setValue(healthTeach);
				}
			},
			onListRowDbClick : function(grid, rowIndex, e) {
				var r = this.RecipeList.getSelectedRecord();
				if (!r) {
					return;
				}
				this.RecipeHasList.store.each(function(record) {
							if (record.get("recordId") == r.get("recordId")) {
								MyMessageTip.msg("提示", "已选过相同健康处方！", true);
								return false;
							}
						}, this);
				this.RecipeHasList.store.add(r);
				this.RecipeList.store.remove(r);
				var index = this.RecipeHasList.store.indexOf(r);
				this.RecipeHasList.selectedIndex = index;
				this.RecipeHasList.selectRow(index);
				this.RecipeHasGrid.fireEvent("rowclick", this.RecipeHasGrid);
				this.overIndex = index;
			},
			onListHasRowDbClick : function(grid, rowIndex, e) {
				var r = this.RecipeHasList.getSelectedRecord();
				if (!r) {
					return;
				}
				this.RecipeHasList.store.remove(r);
				for (var i = 0; i < this.initRecords.length; i++) {
					var record = this.initRecords[i];
					if (r.get("diagnoseId") == record.diagnoseId) {
						r.set("healthTeach", record.healthTeach);
						break;
					}
				}
				this.RecipeList.store.add(r);
				var index = this.RecipeList.store.indexOf(r);
				this.RecipeList.selectedIndex = index;
				this.RecipeList.selectRow(index);
				if (this.RecipeHasList.store.getCount() > 0) {
					this.RecipeHasList.selectRow(0);
					this.overIndex = -1;
					this.RecipeHasGrid
							.fireEvent("rowclick", this.RecipeHasGrid);
					this.overIndex = 0;
				} else {
					var JKCFField = this.RecipeForm.getForm().findField('JKCF'
							+ this.fromId);
					JKCFField.setValue("");
				}
			},
			doImportJKCF : function() {
				var r = this.RecipeHasList.getSelectedRecord();
				if (r) {
					var JKCFField = this.RecipeForm.getForm().findField('JKCF'
							+ this.fromId);
					var index = this.RecipeHasList.store.indexOf(r);
					var overValue = JKCFField.getValue();
					var overRecord = this.RecipeHasList.store
							.getAt(this.overIndex);
					if (overRecord && overValue) {
						this.RecipeHasList.store.getAt(this.overIndex).set(
								"healthTeach", overValue);
					}
					this.overIndex = index;
				}
				var store = this.RecipeHasList.store;
				var records = [];
				store.each(function(r) {
							records.push(r.data)
						}, this);
				// 放回record数组
				this.fireEvent("importRecipe", records);
				if (this.win) {
					this.win.hide();
				}
			},
			onWinShow : function() {
				if (this.ZZDICD10) {
					this.ICD10Field.setValue(this.ZZDICD10);
				}
				this.winShow = true;
				this.doCndQuery();
			},
			onListBeforeLoad : function() {
				this.getCnd();
			},
			doCndQuery : function() {
				this.getCnd();
				this.RecipeList.refresh();
			},
			getCnd : function() {
				var JKCFField = this.RecipeForm.getForm().findField('JKCF'
						+ this.fromId);
				if (JKCFField) {
					JKCFField.setValue("");
				}
				var initCnd = this.initCnd;
				var nameField = this.nameField;
				var ICD10Field = this.ICD10Field;
				var name = nameField.getValue();
				var ICD10 = ICD10Field.getValue();
				var recipe = this.recipeField.getValue();
				var cnd = ['and', ['eq', ['s', '1'], ['s', '1']],
						['eq', ['s', '1'], ['s', '1']]];
				if (name != null && name != "") {
					cnd.push(['like', ['$', 'b.diagnoseNamePy'],
							['s', '%' + name.toLowerCase() + '%']]);
				}
				if (ICD10 != null && ICD10 != "") {
					cnd.push(['like', ['$', 'b.ICD10'],
							['s', '%' + ICD10.toUpperCase() + '%']]);
				} else if (this.ZZDICD10) {
					this.ICD10Field.setValue(this.ZZDICD10);
					cnd.push(['like', ['$', 'b.ICD10'],
							['s', '%' + this.ZZDICD10.toUpperCase() + '%']]);
				}
				if (recipe != null && recipe != "") {
					cnd.push(['like', ['$', 'a.recipeNamePy'],
							['s', '%' + recipe.toLowerCase() + '%']]);
				}
				this.queryCnd = cnd;
				if (initCnd) {
					cnd.push(initCnd)
				}
				this.RecipeList.requestData.cnd = cnd;
			}
		})