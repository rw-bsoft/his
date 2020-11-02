$package("phis.prints.script")
$import("app.desktop.Module", "util.helper.Helper")

phis.prints.script.SuppliesStockYjSearchPrintView = function(cfg) {
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.SuppliesStockYjSearchPrintView.superclass.constructor
			.apply(this, [ cfg ])
}
Ext
		.extend(
				phis.prints.script.SuppliesStockYjSearchPrintView,
				app.desktop.Module,
				{
					initPanel : function() {
						var printForm = new Ext.FormPanel({
							frame : true,
							labelWidth : 75,
							defaults : {
								width : '95%'
							},
							shadow : true,
							items : new util.widgets.MyRadioGroup({
								hideLabel : true,
								value : 'single',
								items : [ {
									boxLabel : '当前页',
									name : 'gird-print-1',
									inputValue : 'single'
								}, {
									boxLabel : '全部页',
									name : 'gird-print-1',
									inputValue : 'whole'
								} ]
							})
						})
						this.printPage = printForm.items.itemAt(0)
						return printForm
					},
					printPreview : function(btn) {
						var page = this.printPage.getValue()
						var printWin;
						var type = 0
						if (btn && btn.iconCls) {
							if (btn.iconCls == "print") {
								type = 1
							} else if (btn.iconCls == "doc") {
								type = 2
							} else if (btn.iconCls == "excel") {
								type = 3
							}
						}
						if (page == 'single') {
							var pages="phis.prints.jrxml.SuppliesStockEjSearch";
							 var url="resources/"+pages+".print?silentPrint=1&type="
									+ type + "&ZBLB=" + this.requestData;
						/*		*/
							if(type == 1){
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
							}else{
								printWin = window
								.open(
										url,
										"",
										"height="
												+ (screen.height - 100)
												+ ", width="
												+ (screen.width - 10)
												+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
					
							}
						} else {
							var pages="phis.prints.jrxml.SuppliesStockEjSearch";
							 var url="resources/"+pages+".print?silentPrint=1&type="
									+ type + "&ZBLB=" + this.requestData;
						/*		*/
							 if(type == 1){
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
							 }else{
								 printWin = window
									.open(
											url,
											"",
											"height="
													+ (screen.height - 100)
													+ ", width="
													+ (screen.width - 10)
													+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
					
							 }
						}
						printWin.onafterprint = function() {
							printWin.close();
						};
						return printWin
					},
					getWin : function() {
						var win = this.win
						if (!win) {
							win = new Ext.Window({
								title : "打印设置",
								width : 360,
								closeAction : "hide",
								items : this.initPanel(),
								modal : true,// add by liyl 2012-05-30
								constrainHeader : true,// add by liyl
								// 2012-06-17
								tbar : [ {
									text : "打印预览",
									iconCls : "print",
									handler : this.printPreview,
									scope : this
								}, {
									text : "WORD",
									iconCls : "doc",
									handler : this.printPreview,
									scope : this
								}, {
									text : "PDF",
									iconCls : "pdf",
									handler : this.printPreview,
									scope : this
								}, {
									text : "EXCEL",
									iconCls : "excel",
									handler : this.printPreview,
									scope : this
								}, {
									text : "关闭",
									iconCls : "common_cancel",
									handler : function() {
										win.hide();
									},
									scope : this
								} ]
							})
							var renderToEl = this.getRenderToEl()
							if (renderToEl) {
								win.render(renderToEl)
							}
							win.on("add", function() {
								this.win.doLayout()
							}, this)
							this.win = win
						}
						return win
					}
				})