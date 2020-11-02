$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.ClinicMedicalRecordForm = function(cfg) {
	cfg.colCount = 2;
	cfg.fldDefaultWidth = 600;
	cfg.defaultHeight = 150;
	this.plugins = ["undoRedo", "removeFmt", "subSuper", "speChar"];
	this.exContext = {};
	phis.application.cic.script.ClinicMedicalRecordForm.superclass.constructor.apply(
			this, [cfg]);
}
var lastField = null;
var cmr_ctx = null;
Ext.extend(phis.application.cic.script.ClinicMedicalRecordForm,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if(this.form) {
					cmr_ctx = this;
					return;
				}
				cmr_ctx = this;
				var label_array = [{
							"id" : 'zs',
							"label" : '主诉',
							"index" : "0"
						}, {
							"id" : 'xbs',
							"label" : '现病史',
							"index" : "1"
						}, {
							"id" : 'jws',
							"label" : '既往史',
							"index" : "2"
						}, {
							"id" : 'tgjc',
							"label" : '体格检查',
							"index" : "3"
						}, {
							"id" : 'qcjc',
							"label" : '实验室和器材检查',
							"index" : "4"
						}];
				var labelArray = [];
				for (var i = 0; i < label_array.length; i++) {
					var labelCfg = label_array[i];
					var label = new Ext.form.Label({
								id : labelCfg.id + '_label',
								fieldLabel : labelCfg.label,
								html : "<div id='" + labelCfg.id
										+ "' class='x-label-html' ></div>"
							});
					label.cfg = labelCfg;
					label.on("afterrender", function() {
						var cfg = this.cfg;
						Ext.get(cfg.id).on('click', function(field, e) {
							if (lastField && cmr_ctx.htmlEditor) {
								Ext.get(lastField).dom.innerHTML = cmr_ctx.htmlEditor
										.getValue();
								cmr_ctx.htmlEditor.destroy();
								cmr_ctx.htmlEditor = null
								cmr_ctx.form.findById(lastField + '_label').show()
							}
							cmr_ctx.form.findById(cfg.id + '_label').hide();
							var text = this.dom.innerHTML;
							cmr_ctx.form.getComponent(0).insert(cfg.index,
									cmr_ctx.getHtmlEditor(cfg.label, text));
							cmr_ctx.form.getComponent(0).doLayout();
							cmr_ctx.htmlEditor.deferFocus();
							lastField = cfg.id;
						});
					}, label);
					labelArray.push(label);
				}
				var g = new Ext.form.FieldSet({
							title : '病历书写',
							animCollapse : false,
							defaultType : 'textfield',
							width : 1018,
							autoHeight : true,
							collapsible : false
						})
				g.add(labelArray);
				g.add(this.getZDList());
				g.add(this.getCFList());
				g.add(this.getCZList());
				
				var tbar = this.createButtons();
				var formPanel = new Ext.FormPanel({
							labelAlign : "right",
							frame : true,
							autoScroll : true,
							tbar : tbar,
							items : [g]
						});
				this.form = formPanel;
				return formPanel;
			},
			getHtmlEditor : function(fLabel, value) {
				if (!this.htmlEditor) {
					this.htmlEditor = new Ext.form.HtmlEditor({
								//xtype : "htmleditor",
								id : "commHtmlEditor",
								fieldLabel : fLabel,
								width : 805,
								height : 150,
								value : value
							});
				}
				return this.htmlEditor;
			},
			getZDList : function() { 
				var exCfg = {width : 450};
				var p = this.createModule("zdlist","CLINIC03",exCfg);
				var panel = new Ext.Panel({
							border : false,
							layout : 'border',
							defaults : {
								border : false
							},
							height:200,
							width :915,
							items : [{
										layout : "fit",
										border : false,
										region : 'center',
										height : 250,
										items : p.initPanel()
									}, {
										layout : "fit",
										border : false,
										width : 105,
										region : 'west',
										style : "text-align:right;",
										html : "初步诊断:&nbsp;&nbsp;"
									}]
						});
				this.panel = panel;
				return panel;
			},
			getCFList : function() { 
				var exCfg = {width : 450};
				var p = this.createModule("cflist","CLINIC0402",exCfg);
				var panel = new Ext.Panel({
							border : false,
							layout : 'border',
							defaults : {
								border : false
							},
							height:300,
							width :915,
							items : [{
										layout : "fit",
										border : false,
										region : 'center',
										height : 180,
										items : p.initPanel()
									}, {
										layout : "fit",
										border : false,
										width : 105,
										region : 'west',
										style : "text-align:right;",
										html : "处方录入:&nbsp;&nbsp;"
									}]
						});
				this.panel = panel;
				return panel;
			},
			getCZList : function() { 
				var exCfg = {width : 450};
				var p = this.createModule("czlist","CLINIC05",exCfg);
				var panel = new Ext.Panel({
							border : false,
							layout : 'border',
							defaults : {
								border : false
							},
							height:300,
							width :915,
							items : [{
										layout : "fit",
										border : false,
										region : 'center',
										height : 180,
										items : p.initPanel()
									}, {
										layout : "fit",
										border : false,
										width : 105,
										region : 'west',
										style : "text-align:right;",
										html : "处置录入:&nbsp;&nbsp;"
									}]
						});
				this.panel = panel;
				return panel;
			},createButtons : function() {
		var actions = this.actions
		var buttons = []
		if (!actions) {
			return buttons
		}
		var f1 = 112
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			if (action.hide) {
				continue
			}

			// ** add by yzh **
			var btnFlag;
			if (action.notReadOnly)
				btnFlag = false
			else
				btnFlag = this.exContext.readOnly || false

			var btn = {
				accessKey : f1 + i,
				text : action.name + "(F" + (i + 1) + ")",
				ref : action.ref,
				target : action.target,
				cmd : action.delegate || action.id,
				iconCls : action.iconCls || action.id,
				enableToggle : (action.toggle == "true"),

				// ** add by yzh **
				disabled : btnFlag,
				notReadOnly : action.notReadOnly,

				script : action.script,
				handler : this.doAction,
				scope : this
			}
			buttons.push(btn)
		}
		return buttons

	}

		});
