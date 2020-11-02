$package("chis.application.cvd.script");
$import("app.desktop.Module", "chis.script.BizTableFormView");
chis.application.cvd.script.DiseaseVerificationForm = function(cfg) {
	debugger;
	cfg.colCount = 4;
	cfg.fldDefaultWidth = 102;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.labelWidth = 105;
	chis.application.cvd.script.DiseaseVerificationForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeCreate", this.onBeforeCreate, this);
};
Ext.extend(chis.application.cvd.script.DiseaseVerificationForm, chis.script.BizTableFormView, {
			onBeforeCreate : function() {
				debugger;
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.cvdService",
							serviceAction : "initDiseaseVerification",
							method : "execute",
							schema : this.entryName,
							empiId : this.form.opener.exContext.ids["empiId"],
							recordId:this.form.opener.exContext.args.selectRecordId
						})
				if (result.json && result.json.body) {
					this.initFormData(result.json.body);
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
			onReady : function() {
				debugger;
				chis.application.cvd.script.DiseaseVerificationForm.superclass.onReady
						.call(this)
				var form = this.form.getForm()

		        this.onBeforeCreate();
			},
			initFormData : function(data) {
				debugger;
				chis.application.cvd.script.DiseaseVerificationForm.superclass.initFormData
						.call(this, data)
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (this.initDataId == null) {
					this.op = "create";
				}

				if (this.op == "update") {
				}
				this.proccessSave(saveData)
			},
			proccessSave : function(saveData) {
				debugger;
				saveData.empiId = this.form.opener.exContext.ids["empiId"];
				saveData.precordId = this.form.opener.exContext.args["recordId"];
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.cvdService",
							op : this.op,
							method : "execute",
							schema : this.entryName,
							body : saveData,
							serviceAction : "saveDiseaseVerification"
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
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.form.opener.exContext.opener.list.loadData();
							this.op = "update"
						}, this)// jsonRequest
			}
		});