/**
 * 医生排班功能
 * 
 * @author liyl
 */
$package("phis.application.reg.script");

$import("phis.script.SimpleModule");

phis.application.reg.script.RegCountModule = function(cfg) {
	phis.application.reg.script.RegCountModule.superclass.constructor.apply(
			this, [ cfg ]);
}
Ext.extend(phis.application.reg.script.RegCountModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					// activeItem : this.activateId,
					defaults : {
						border : false
					},
					items : [ {
						layout : "fit",
						border : false,
						split : true,
						title : '',
						region : 'center',
						// height : 380,
						// width : '60%',
						items : this.getList()
					}, {
						layout : "fit",
						border : false,
						split : true,
						title : '',
						region : 'east',
						width : '77%',
						// height : 380,
						items : this.getTXModel()
					} ],
					tbar : this.getTbar()
				});
				this.panel = panel;
				return panel;
			},
			getList : function() {
				var ghtjmodule = this.createModule("ghtjList", this.refList);
				this.ghtjmodulethis = ghtjmodule;
				var ghtjList = ghtjmodule.initPanel();
				this.ghtjmodulethis.beginDate=new Date().format('Y-m-d');
				this.ghtjmodulethis.endDate=new Date().format('Y-m-d');
				this.ghtjmodulethis.tjfs=1;
				this.ghtjmodulethis.doQuery();
				return ghtjList;
			},
			getTXModel : function() {
				var txmodel = this.createModule("txmodel", this.refTXModel);
				this.txmodelthis = txmodel;
				var txmodelpal = txmodel.initPanel();
				return txmodelpal;
			},
			getTbar : function() {
				var tbar = new Ext.Toolbar();
				this.tjfsStore = new Ext.data.JsonStore({
					fields : [ 'value', 'text' ],
					data : [ {
						'value' : 1,
						'text' : '科室'
					}, {
						'value' : 2,
						'text' : '性质'
					} ]
				});
				this.tjfsComboBox = new Ext.form.ComboBox({
					name : 'tjfs',
					store : this.tjfsStore,
					valueField : "value",
					displayField : "text",
					mode : 'local',
					triggerAction : 'all',
					emptyText : "",
					selectOnFocus : true,
					forceSelection : true,
					width : 100
				});
				this.tjfsComboBox.setValue("1");
				this.txfsStore = new Ext.data.JsonStore({
					fields : [ 'value', 'text' ],
					data : [ {
						'value' : 1,
						'text' : '饼型'
					}, {
						'value' : 2,
						'text' : '柱型'
					}, {
						'value' : 3,
						'text' : '线型'
					}, {
						'value' : 4,
						'text' : '三维饼型'
					} ]
				});
				this.txfsComboBox = new Ext.form.ComboBox({
					name : 'tjfs',
					store : this.txfsStore,
					valueField : "value",
					displayField : "text",
					mode : 'local',
					triggerAction : 'all',
					emptyText : "",
					selectOnFocus : true,
					forceSelection : true,
					width : 100
				});
				this.txfsComboBox.setValue("1");
				var simple = new Ext.FormPanel({
					labelWidth : 50, // label settings here cascade
					title : '',
					layout : "table",
					bodyStyle : 'padding:5px 5px 5px 5px',
					defaults : {},
					defaultType : 'textfield',
					items : [ {
						xtype : "label",
						forId : "window",
						text : "挂号时间 "
					}, new Ext.ux.form.Spinner({
						fieldLabel : '挂号时间开始',
						name : 'dateFrom',
						value : new Date().format('Y-m-d'),
						strategy : {
							xtype : "date"
						},
						width : 150
					}), {
						xtype : "label",
						forId : "window",
						text : "-"
					}, new Ext.ux.form.Spinner({
						fieldLabel : '挂号时间结束',
						name : 'dateTo',
						value : new Date().format('Y-m-d'),
						strategy : {
							xtype : "date"
						},
						width : 150
					}), {
						xtype : "label",
						forId : "window",
						text : "统计方式"
					}, this.tjfsComboBox, {
						xtype : "label",
						forId : "window",
						text : "图形方式"
					}, this.txfsComboBox ]
				});
				this.simple = simple;
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			doQuery : function() {
				this.ghtjmodulethis.beginDate = this.simple.items.get(1)
						.getValue();// 开始时间
				this.ghtjmodulethis.endDate = this.simple.items.get(3)
						.getValue();// 结束时间
				this.ghtjmodulethis.tjfs = this.tjfsComboBox.getValue();
				this.ghtjmodulethis.doQuery();
				this.txmodelthis.beginDate = this.simple.items.get(1)
						.getValue();// 开始时间
				this.txmodelthis.endDate = this.simple.items.get(3).getValue();// 结束时间
				this.txmodelthis.tjfs = this.tjfsComboBox.getValue();
				this.txmodelthis.txfs = this.txfsComboBox.getValue();
				this.txmodelthis.loadDataf();
			}
		});