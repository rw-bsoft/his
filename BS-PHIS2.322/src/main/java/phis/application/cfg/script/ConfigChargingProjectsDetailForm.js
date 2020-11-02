$package("phis.application.cfg.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.cfg.script.ConfigChargingProjectsDetailForm = function(cfg) {
	cfg.entryName = this.entryName = "phis.application.cfg.schemas.GY_YLSF_XG";
	cfg.schema = this.schema = "phis.application.cfg.schemas.GY_YLSF_XG";
	phis.application.cfg.script.ConfigChargingProjectsDetailForm.superclass.constructor
			.apply(this, [cfg]);
			this.on("loadData",this.onAfterLoadData,this)
}
Ext.extend(phis.application.cfg.script.ConfigChargingProjectsDetailForm,
		phis.script.SimpleForm, {
			initPanel : function(sc) {

				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form = new Ext.FormPanel({
					labelWidth : 80, // label settings here cascade
					// unless overridden
					frame : true,
					bodyStyle : 'padding:5px 5px 0',
					width : 800,
					autoHeight : true,
					items : [{
								xtype : 'fieldset',
								title : '基本信息',
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
								listeners : {
									"afterrender" : this.onReady,
									scope : this
								},
								defaultType : 'textfield',
								items : this.getItems('JBXX')
							}, {
								xtype : 'fieldset',
								title : '代码属性',
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
								items : this.getItems('DMSX')
							}, {
								xtype : 'fieldset',
								title : '使用情况',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 10,
									tableAttrs : {
										border : 0,
										cellpadding : '2',
										cellspacing : "2"
									}
								},
								defaultType : 'textfield',
								items : this.getItems('SYQK')
							}, {
								xtype : 'fieldset',
								title : '其他属性',
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
								items : this.getItems('QT')
							}]
						// tbar : (this.tbar || []).concat(this.createButtons())
					});
				if (!this.isCombined) {
					this.addPanelToWin();
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
				var XMLX =this.form.getForm().findField("XMLX");
				XMLX.on("select", this.onProjectsDetail, this);
				
				this.schema = schema;
				return this.form
			},
			onProjectsDetail : function(){
				var XMLX =this.form.getForm().findField("XMLX");
				if(XMLX.value == "11"){  //检查项目为pacs时
					this.form.getForm().findField("JCDL").show();
				}else{
					this.form.getForm().findField("JCDL").hide();
				}
			},
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						f.setValue(it.defaultValue)
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}
						if (it.type == "date") { // ** add by yzh 20100919 **
							if (it.minValue)
								f.setMinValue(it.minValue)
							if (it.maxValue)
								f.setMaxValue(it.maxValue)
						}
					}
				}
				if(form.findField("XMLX").value != "11"){//11是pacs
					form.findField("JCDL").hide();
				}else{
					form.findField("JCDL").show();
				}
				this.setKeyReadOnly(false)
				this.fireEvent("doNew",this.form)
				this.focusFieldAfter(-1, 800)
				this.validate()
			},
			onAfterLoadData:function(){
			var form = this.form.getForm()
			if(form.findField("XMLX")){
			if(form.findField("XMLX").value != "11"){//11是pacs
					form.findField("JCDL").hide();
				}else{
					form.findField("JCDL").show();
				}
			}
			},
			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync(this.entryName)
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel)
					return;
				}
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.layout || it.layout != para) {
						continue;
					}
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						// alert(it.acValue);
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			},
			expand : function() {
//				this.win.center();
			}
		});
