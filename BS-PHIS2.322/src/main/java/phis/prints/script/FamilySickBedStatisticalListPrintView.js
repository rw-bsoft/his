$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup", "util.DateUtil",
		"util.dictionary.TreeDicFactory", "util.helper.Helper","phis.script.SimpleModule")
/**
 * 家庭病床建床情况统计
 * 
 */
phis.prints.script.FamilySickBedStatisticalListPrintView = function(cfg) {
	this.width = 1200
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.conditions = []
	this.sfrbrq = "";
	phis.prints.script.FamilySickBedStatisticalListPrintView.superclass.constructor.apply(this,
			[cfg])
	Ext.apply(this, phis.script.SimpleModule);
}
Ext.extend(phis.prints.script.FamilySickBedStatisticalListPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_FamilySickBedStatistical";
		this.conditionFormId = "SimplePrint_form_FamilySickBedStatistical";
		this.mainFormId = "SimplePrint_mainform_FamilySickBedStatistical";
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
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
		items.push(new Ext.form.DateField({
					name : 'beginDate',
					value : dateFromValue,
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
	getExecJs : function() {
		return "jsPrintSetup.setPrinter('rb');"
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
//		var url = this.printurl + ".print?pages=FamilySickBedStatistical";
		var pages="phis.prints.jrxml.FamilySickBedStatistical";
		var url="resources/"+pages+".print?silentPrint=1";		
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&ksdm="+this.ksdm
		var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			// 预览LODOP.PREVIEW();
			// 预览LODOP.PRINT();
			// LODOP.PRINT_DESIGN();
			
			var restr = util.rmi.loadXML({url:url,httpMethod:"get"});
            restr = restr.replace(/table style=\"width: 937px; border-collapse: collapse\"/g,"table style=\"page-break-before:always;width: 937px; border-collapse: collapse;\"");
			 restr = restr.replace(/<div style='page-break-before:always;'><\/div>/g,"");
            restr = restr.substr(0,restr.indexOf("page-break-before:always;"))
			+ restr.substr(restr.indexOf("page-break-before:always;") + 25);
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",restr);
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			// 预览
			LODOP.PREVIEW();
	},
	doClose : function() {
		this.opener.closeCurrentTab();
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
				.format('Y-m-d');
//		var url = this.printurl + ".print?pages=FamilySickBedStatistical";
		var pages="phis.prints.jrxml.FamilySickBedStatistical";
		var url="resources/"+pages+".print?silentPrint=1";	
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&ksdm="+this.ksdm
			document.getElementById(_ctx.frameId).src = url
		this.panel.getTopToolbar().items.get(5).setDisabled(false);
	},
				onReady : function() {
				var _ctx = this;
				var iframe = Ext.isIE
						? document.frames[this.frameId]
						: document.getElementById(this.frameId);
				if (iframe.attachEvent) {
					iframe.attachEvent("onload", function() { 
								_ctx
										.simplePrintMask(iframe,
												"FamilySickBedStatistical");
							});
				} else {
					iframe.onload = function() {
						_ctx.simplePrintMask(iframe,
								"FamilySickBedStatistical");
					};
				}
			},
			/**
			 * 实现报表事件监听及相应业务处理
			 */
			simplePrintMask : function(iframe, printId) {
				this.panel.el.unmask();
				// add by yangl 报表事件
				var _ctx = this;
				var iframeDoc = Ext.isIE
						? iframe.document
						: iframe.contentWindow.document
				var tables = iframeDoc.getElementsByTagName('table');
				if (tables.length > 1) {
					for(var j=1;j<tables.length;j++){
					var trs = tables[j].getElementsByTagName('tr');
					// *********具体列数根据报表决定**************
					var headerRow = 5;// 标题头占的行数,
					var footerRow = 2;// 标题尾占的行数
					for (var i = headerRow; i < trs.length - footerRow; i++) {// 只监听表格内容列
						trs[i].onmousedown = function() {
							tronmousedown(this);
						}
						trs[i].onclick = function() {
							tronclick(this);
						}
						trs[i].ondblclick = function() {
//							ondblclick(this);
						}
					}
					}
					
					var lastSelectedRow = null;
					// *********选中行颜色变化*********
					function tronmousedown(obj) {
						if (obj != lastSelectedRow) {
							for (var j = 1; j < obj.cells.length - 1; j++) {
								obj.cells[j].style.backgroundColor = '#DFEBF2';
							}
							// obj.style.backgroundColor = '#DFEBF2';
							if (lastSelectedRow) { 
								for (var j = 1; j < lastSelectedRow.cells.length - 1; j++) {
									lastSelectedRow.cells[j].style.backgroundColor = '';
								}
							}
						}
						lastSelectedRow = obj;
					}
					// *********单击事件(或者监听双击也可以)*********
					function tronclick(obj) {
						// 组装数据,数据为每行单元格中的内容
						var data = [];
						for (var j = 1; j < obj.cells.length - 1; j++) {
							data
									.push(obj.cells[j].firstChild.firstChild.innerHTML);
						}
						// *****以下可以实现具体业务******
					}
					// function ondblclick(obj) {
					// // 组装数据,数据为每行单元格中的内容
					// var data = [];
					// for (var j = 1; j < obj.cells.length - 1; j++) {
					// data.push(obj.cells[j].firstChild.firstChild.innerHTML);
					// }
					// _ctx.doQueryDetil(data[1]);
					// }
				}
			}
	
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}