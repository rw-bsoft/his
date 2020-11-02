$package("phis.application.cic.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.cic.script.ClinicDiagnosisEntryForm = function(cfg) {
	cfg.width = 900
	cfg.fldDefaultWidth = 180;
	phis.application.cic.script.ClinicDiagnosisEntryForm.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.cic.script.ClinicDiagnosisEntryForm,
		phis.script.SimpleForm, {
			initPanel : function() {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form = new Ext.FormPanel({
							labelWidth : 75, // label settings here cascade
							// unless overridden
							frame : true,
							bodyStyle : 'padding:5px 5px 0',
							width : 1000,
							autoHeight  : true,
							items : {
								xtype : 'tabpanel',
								activeTab : 0,
								defaults : {
									autoHeight : true,
									bodyStyle : 'padding:10px'
								},
								buttonAlign : "center",
								items : [{
									title : '西医诊疗',
									items : [{
												xtype : 'fieldset',
												title : '诊断信息',
												// collapsible : true,
												autoHeight : true,
												layout : 'tableform',
												layoutConfig : {
													columns : 3,
													tableAttrs : {
														border : 0,
														cellpadding : '2',
														cellspacing : "2"
													}
												},
												defaultType : 'textfield',
												items : this
														.getItems('MS_BRZD')
											}, {
												id : "FSZD_SET",
												xtype : 'fieldset',
												checkboxToggle : true,
												title : '子诊断',
												autoHeight : true,
												collapsed : true,
												items : this.getFSList()
											},{
												autoHeight : true,
												layout : 'tableform',
												layoutConfig : {
													columns : 2,
													tableAttrs : {
														border : 0,
														cellpadding : '2',
														cellspacing : "2"
													}
												},
												defaultType : 'textfield',
												items : this.getItems('MS_BRZD_LOW')
											}]
								}
//								, {
//									title : '中医诊断',
//									disabled : true
//								}, {
//									title : '复诊诊断',
//									disabled : true
//								}
								],

								buttons : [{
											text : '确定',
											handler  : this.doSave,
											scope :this
										}, {
											text : '取消',
											handler  : this.doCancel,
											scope :this
										}]
							}
						});
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			getItems : function(entryName) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync(entryName)
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel)
					return;
				}
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						// alert(it.acValue);
						continue;
					}
					var f = this.createField(it)
					
					MyItems.push(f);
				}
				return MyItems;
			},
			doSave : function() {
				//var data = {};//待保存数据
				//var fszdbz =  this.form.findById("FSZD_SET").collapsed ;
				//if(!fszdbz) {
//					var zdmc = this.form.getForm().findField("ZDMC_FS").getValue();
//					var icd10 = this.form.getForm().findField("ICD10_FS").getValue();
//					if(zdmc=="" || icd10=="") {
//						alert("附属诊断不能为空！");
//						return;
//					}else {
//						//设置保存的附属诊断数据
//					}
				//}
				//设置保存的诊断数据
			},
			getFSList : function() {
				//子诊断list
				var list = this.createModule("ClinicDiagnosisSubEntryList",
						"CLINIC0302");
				var grid =  list.initPanel();
				//grid.on("expand",this.expand,this);
				list.doCreate();
				return grid;
			},
			expand : function() {
				this.win.center();
			}
		});