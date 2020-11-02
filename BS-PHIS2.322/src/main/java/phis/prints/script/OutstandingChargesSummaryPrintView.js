$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup", "phis.script.util.DateUtil",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.OutstandingChargesSummaryPrintView = function(cfg) {
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.conditions = []
	phis.prints.script.OutstandingChargesSummaryPrintView.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.prints.script.OutstandingChargesSummaryPrintView,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.frameId = "SimplePrint_frame_phis.prints.jrxml.OutstandingChargesSummary";
				this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.OutstandingChargesSummary";
				this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.OutstandingChargesSummary";
				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					title : '',
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
							+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.OutstandingChargesSummary\")'></iframe>"
				})
				this.panel = panel
				return panel
			},
			initConditionFields : function() {
				var items = []
//				items.push(new Ext.form.Label({
//							text : "从"
//						}))
//				this.beginDate = new Ext.form.DateField({
//							name : 'beginDate',
//							value : new Date(),
//							width : 100,
//							allowBlank : false,
//							altFormats : 'Y-m-d',
//							format : 'Y-m-d',
//							emptyText : '开始时间'
//						})
//				items.push(this.beginDate)
//				items.push(new Ext.form.Label({
//							text : "到"
//						}))
//				this.endDate = new Ext.form.DateField({
//							name : 'endDate',
//							value : new Date(),
//							width : 100,
//							allowBlank : false,
//							altFormats : 'Y-m-d',
//							format : 'Y-m-d',
//							emptyText : '结束时间'
//						})
//				items.push(this.endDate)
//				var preview = this.createCommonDic("type")
//				preview.value = "1"
				// items.push(preview)
				items.push({
							xtype : "button",
							text : "刷新",
							iconCls : "query",
							scope : this,
							handler : this.doLoadReport
						})
				// items.push({
				// xtype : "button",
				// text : "结 帐",
				// iconCls : "commit",
				// scope : this,
				// handler : this.doCommit,
				// disabled : true
				// })
				// items.push({
				// xtype : "button",
				// text : "查询",
				// iconCls : "query",
				// scope : this,
				// handler : this.doInquiry
				// })
				items.push({
							xtype : "button",
							text : "打 印",
							iconCls : "print",
							scope : this,
							handler : this.doPrint
//							disabled : true
						})
				items.push({
							xtype : "button",
							text : "导出",
							iconCls : "excel",
							scope : this,
							handler : this.doExcel
						})
				return items
			},
			doPrint : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				} 
				 var pages="phis.prints.jrxml.OutstandingChargesSummary";
				 var url="resources/"+pages+".print?silentPrint=1&landscape=1";
				 /*
				   window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				 */
				    var LODOP=getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0","","","");
					//预览LODOP.PREVIEW();
					//预览LODOP.PRINT();
					//LODOP.PRINT_DESIGN();
					LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
					LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
					//预览
					LODOP.PREVIEW();
			},
			doExcel : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				} 
				 var pages="phis.prints.jrxml.OutstandingChargesSummary";
				 var url="resources/"+pages+".print?type=3&silentPrint=1&landscape=1";
				 var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				return printWin;
			},
			doLoadReport : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				 var pages="phis.prints.jrxml.OutstandingChargesSummary";
				 var url="resources/"+pages+".print?silentPrint=1";
				document.getElementById(this.frameId).src = url
			}
		})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}