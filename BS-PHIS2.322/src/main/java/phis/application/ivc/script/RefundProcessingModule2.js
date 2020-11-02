/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");

phis.application.ivc.script.RefundProcessingModule2 = function(cfg) {
	phis.application.ivc.script.RefundProcessingModule2.superclass.constructor
			.apply(this, [ cfg ]);
}

Ext.extend(phis.application.ivc.script.RefundProcessingModule2,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var form = this.createForm();
				var panel = new Ext.Panel({
					border : false,
					width : this.width,
					height : this.height,
					// frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					buttonAlign : 'center',
					items : [ {
						layout : "fit",
						border : false,
						// split : true,
						title : '',
						region : 'north',
						height : 41,
						items : form
					}, {
						layout : "fit",
						border : false,
						// split : true,
						title : '',
						region : 'center',
						items : this.getList()
					} ]
				});
				this.panel = panel;
				return panel;
			},
			createForm : function() {
				var form = new Ext.FormPanel({
					labelWidth : 40,
					frame : true,
					defaultType : 'textfield',
					layout : 'tableform',
					defaults : {
						bodyStyle : 'padding-left:3px;'
					},
					layoutConfig : {
						columns : 4,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : "2"
						}
					},
					items : [ {
						xtype : 'panel',
						layout : "table",
						items : [ {
							xtype : "panel",
							width : 40,
							html : "医生:"
						}, new Ext.form.TextField({
							name : "YSXM",
							disabled : true,
							width : 120
						}) ]
					}, {
						xtype : 'panel',
						layout : "table",
						items : [ {
							xtype : "panel",
							width : 40,
							html : "科室:"
						// style : "text-align:center;"
						}, new Ext.form.TextField({
							name : "KSMC",
							disabled : true,
							width : 120
						}) ]
					}, {
						xtype : 'panel',
						layout : "table",
						items : [ {
							xtype : "panel",
							width : 40,
							html : "帖数:"
						}, new Ext.form.TextField({
							hideParent : true,
							name : "CFTS",
							disabled : true,
							width : 120
						}) ]
					}, {
						xtype : 'panel',
						layout : "table",
						items : [ {
							xtype : "panel",
							width : 45,
							html : "<span style='font-size:18px;font-weight:bold;'>西</span>"
//							style : "font-size:20px;font-weight:bold;"
						}, new Ext.form.TextField({
							name : "CFLX",
							disabled : true,
							hidden : true,
							width : 120
						}) ]
					} ]
				});
				this.form = form
				return form
			},
			getList : function() {
				this.tfList2 = this.createModule("tfList2", this.refList);
				this.tfList2.opener = this;
				return this.tfList2.initPanel();
			},
			setDetails : function(details) {
				this.tfList2.setDetails(details);
			},
			setDListloadData : function(data) {
				var form = this.form.getForm();
				var CFTS = form.findField("CFTS");
				var CFLX = form.findField("CFLX");
				form.findField("YSXM").setValue(data.YSMC);
				form.findField("KSMC").setValue(data.KSMC);
				form.findField("CFTS").setValue(data.CFTS);
				CFLX.hide();
				var CFLXField = CFLX.el.parent().parent().first();
				var CFTSField = CFTS.el.parent().parent().first();
				CFTSField.dom.innerHTML = "";
//				CFLXField.dom.style.font-size = "font-size:20px;font-weight:bold;"; 
				CFTS.hide();
				if (data.CFLX == 1) {
					CFLXField.dom.innerHTML = "<span style='font-size:18px;font-weight:bold;'>西</span>";
				} else if (data.CFLX == 2) {
					CFLXField.dom.innerHTML = "<span style='font-size:18px;font-weight:bold;'>成</span>";
				} else if (data.CFLX == 3) {
					CFLXField.dom.innerHTML = "<span style='font-size:18px;font-weight:bold;'>草</span>";
					CFTSField.dom.innerHTML = '<div class=" x-panel" style="width: 40px;"><div class="x-panel-bwrap"><div class="x-panel-body x-panel-body-noheader" style="width: 60px;">帖数:</div></div></div>';
					CFTS.show();
				} else {
					CFLXField.dom.innerHTML = "<span style='font-size:18px;font-weight:bold;'>检</span>";
				}
				this.tfList2.loadData(data);
			},
			doNew : function(){
				this.tfList2.clear();
				var form = this.form.getForm();
				form.findField("YSXM").setValue();
				form.findField("KSMC").setValue();
				form.findField("CFTS").setValue();
			}
		});