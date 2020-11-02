$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup", "util.DateUtil",
		"util.dictionary.TreeDicFactory", "util.helper.Helper","phis.script.SimpleModule")
/**
 *家庭病床费用统计
 * 
 */
phis.prints.script.FamilySickBedCostStatisticalListPrintView = function(cfg) {
	this.width = 1200
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.conditions = []
	this.sfrbrq = "";
	phis.prints.script.FamilySickBedCostStatisticalListPrintView.superclass.constructor.apply(this,
			[cfg])
	Ext.apply(this, phis.script.SimpleModule);
}
Ext.extend(phis.prints.script.FamilySickBedCostStatisticalListPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_FamilySickBedCostStatistical";
		this.conditionFormId = "SimplePrint_form_FamilySickBedCostStatistical";
		this.mainFormId = "SimplePrint_mainform_FamilySickBedCostStatistical";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : "",
			tbar : {
				id : this.conditionFormId,
				xtype : "form",
				layout : "hbox",
				layoutConfig : {
					pack : 'start',
					align : 'middle'
				},
				frame : true,
				items : this.initConditionFields()
			},
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' '></iframe>"
		})
		this.panel = panel
		this.panel.on("afterrender", this.onReady, this);
		return panel
	},
	initConditionFields : function() {
		var items = []
		items.push(new Ext.form.Label({
					text : "日期："
				}))
		items.push(new Ext.form.DateField({
					name : 'beginDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				}))
		items.push(new Ext.form.Label({
					text : "至"
				}))
		items.push(new Ext.form.DateField({
					name : 'endDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				}))
		items.push(new Ext.form.Label({
					text : "统计方式"
				}))
		var tjfsStore = new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : [[1, '按病人统计'], [2, '按医生统计']]
						});
		var tjfsCombox = new Ext.form.ComboBox({
							store : tjfsStore,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 80,
							value : 1
						});
		items.push(tjfsCombox)
		items.push({
					xtype : "button",
					text : "统计",
					iconCls : "query",
					scope : this,
					handler : this.doquery
				})
		items.push({
					xtype : "button",
					text : "打 印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint,
					disabled : true
				})
		items.push({
					xtype : "button",
					text : "关闭",
					iconCls : "common_cancel",
					scope : this,
					handler : this.doClose,
					disabled : false
				})
		return items
	},
	doPrint : function() {
		var _ctx = this;
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		var dateFrom = this.panel.getTopToolbar().items.get(1).getValue()
				.format('Y-m-d');
		var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
				.format('Y-m-d');
		var tjfs = this.panel.getTopToolbar().items.get(5).getValue();
		var pages="phis.prints.jrxml.FamilySickBedCostStatistical";
		var url="resources/"+pages+".print?silentPrint=1";		
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&tjfs="+tjfs
		//window.open(url)
		var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			LODOP.PREVIEW();
	},
	doquery : function() {
		var _ctx = this;
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		var dateFrom = this.panel.getTopToolbar().items.get(1).getValue()
				.format('Y-m-d');
		var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
				.format('Y-m-d')+1;
				
		var tjfs = this.panel.getTopToolbar().items.get(5).getValue();
		var pages="phis.prints.jrxml.FamilySickBedCostStatistical";
		var url="resources/"+pages+".print?silentPrint=1";	
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&tjfs="+tjfs
		document.getElementById(_ctx.frameId).src = url
		this.panel.getTopToolbar().items.get(7).setDisabled(false);
	},
				onReady : function() {
				var _ctx = this;
				var iframe = Ext.isIE
						? document.frames[this.frameId]
						: document.getElementById(this.frameId);
				if (iframe.attachEvent) {
					iframe.attachEvent("onload", function() { 
								_ctx
										.simplePrintMask(
												"FamilySickBedCostStatistical");
							});
				} else {
					iframe.onload = function() {
						_ctx.simplePrintMask(
								"FamilySickBedCostStatistical");
					};
				}
			},
	doClose : function() {
		this.opener.closeCurrentTab();
	}
	
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}