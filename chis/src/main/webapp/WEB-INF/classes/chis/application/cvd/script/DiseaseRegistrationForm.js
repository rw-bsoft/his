$package("chis.application.cvd.script")
$import("chis.script.BizFieldSetFormView","util.widgets.LookUpField")
chis.application.cvd.script.DiseaseRegistrationForm = function(cfg) {
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.colCount = 8
	cfg.labelWidth = 100;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 35;
	chis.application.cvd.script.DiseaseRegistrationForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeCreate", this.onBeforeCreate, this);
};
Ext.extend(chis.application.cvd.script.DiseaseRegistrationForm,
		chis.script.BizFieldSetFormView, {
			onBeforeCreate : function() {
				debugger;
				var me=this;
				var data=me.exContext.args.selectData.data;
				this.initFormData(data);
//				var result = util.rmi.miniJsonRequestSync({
//							serviceId : "chis.cvdService",
//							serviceAction : "initCvdAssessRegister",
//							method : "execute",
//							schema : this.entryName,
//							empiId : this.exContext.ids["empiId"]
//						})
//				if (result.json && result.json.body) {
//					this.initFormData(result.json.body);
//				}
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
			}

});