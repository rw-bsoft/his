$package("phis.application.sto.script")

$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.application.sto.script.StorehouseMonthlyReportPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.application.sto.script.StorehouseMonthlyReportPrintView.superclass.constructor.apply(this, [cfg])
	// this.on("loadData",this.onLoadData,this);
}
Ext.extend(phis.application.sto.script.StorehouseMonthlyReportPrintView,
		app.desktop.Module, {
			initPanel : function() {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				this.frameId = "SimplePrint_frame_storehouseMonthly";
				this.conditionFormId = "SimplePrint_form_storehouseMonthly";
				this.mainFormId = "SimplePrint_mainform_storehouseMonthly";
				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					title : this.title,
					tbar : {
						id : this.conditionFormId,
						xtype : "form",
						layout : "hbox",
						layoutConfig : {
							pack : 'start',
							align : 'middle'
						},
						frame : true,
						items : this.getTbar()
					},
					html : "<iframe id='"
							+ this.frameId
							+ "' width='100%' height='100%' onload='simplePrintMask(\"storehouseMonthly\")'></iframe>"
				})
				this.panel = panel
				return panel
			},
			getTbar : function() {
				var dat = new Date().format('Y-m-d');
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
						+ "-01";
				var tbar = [];
				tbar.push(new Ext.form.Label({
							text : "收费日期 从:"
						}));
				tbar.push(new Ext.form.DateField({
							id : 'dateFrom',
							name : 'dateFrom',
							value : dateFromValue,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间'
						}));
				tbar.push(new Ext.form.Label({
							text : " 到 "
						}));
				tbar.push(new Ext.form.DateField({
							id : 'dateTo',
							name : 'dateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						}));
				this.ZBLBStore = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : [{
										'value' : 1,
										'text' : '西药'
									}, {
										'value' : 2,
										'text' : '中成药'
									}, {
										'value' : 3,
										'text' : '中草药'
									}]
						});
				this.ZBLBCombox = new Ext.form.ComboBox({
							name : 'ZBLB',
							store : this.ZBLBStore,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : "全部",
							selectOnFocus : true,
							forceSelection : true,
							width : 130
						});
				tbar.push(new Ext.form.Label({
							text : "账簿类别:"
						}));
				tbar.push(this.ZBLBCombox);
				tbar.push({
							xtype : "button",
							text : "刷新",
							iconCls : "query",
							scope : this,
							handler : this.doQuery,
							disabled : false
						})
				tbar.push({
							xtype : "button",
							text : "打印",
							iconCls : "print",
							scope : this,
							handler : this.doPrint,
							disabled : false
						})
				return tbar;
			},

			doQuery : function() {
				// if (this.ZBLBCombox.value.length == 0) {
				// Ext.MessageBox.alert("提示", "请选择账簿类别");
				// return;
				// }
				// if (this.panel.getTopToolbar().items.get(4).value == ""
				// || this.panel.getTopToolbar().items.get(4).value ==
				// undefined) {
				// this.depValue = [];
				// }
				// alert(this.ZBLBCombox.value)
				var dateFrom = Ext.getDom("dateFrom").value;
				var dateTo = Ext.getDom("dateTo").value;
				if (!dateFrom || !dateTo) {
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var zblb;
				if (!this.ZBLBCombox.value||this.ZBLBCombox.value.length == 0) {
					zblb = 0;
				} else {
					zblb = this.ZBLBCombox.value;
				}
				//console.debug(this.mainApp);
				Ext.getCmp(this.mainFormId).el.mask("正在生成报表...","x-mask-loading");
				//var url = this.printurl
				//		+ "*.print?pages=phis.prints.jrxml.StorehouseMonthlyReport&execJs="
				//		+ this.getExecJs();
				var pages="phis.prints.jrxml.StorehouseMonthlyReport";
				 var url="resources/"+pages+".print?execJs="
						+ this.getExecJs();
				url += "&dateFrom=" + dateFrom + "&dateTo=" + dateTo + "&zblb="
						+ zblb + "&xtsb=" + this.mainApp['phis'].storehouseId
				document.getElementById(this.frameId).src = url
			},
			doPrint : function() {
				var dateFrom = Ext.getDom("dateFrom").value;
				var dateTo = Ext.getDom("dateTo").value;
				if (!dateFrom || !dateTo) {
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var zblb;
				if (!this.ZBLBCombox.value||this.ZBLBCombox.value.length == 0) {
					zblb = 0;
				} else {
					zblb = this.ZBLBCombox.value;
				} 
				//var url = this.printurl
				//		+ "*.print?pages=phis.prints.jrxml.StorehouseMonthlyReport&execJs="
				//		+ this.getExecJs();
				var pages="phis.prints.jrxml.StorehouseMonthlyReport";
				 var url="resources/"+pages+".print?execJs="
						+ this.getExecJs();
				url += "&dateFrom=" + dateFrom + "&dateTo=" + dateTo + "&zblb="
						+ zblb + "&xtsb=" + this.mainApp['phis'].storehouseId
			/*	window
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
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");

				//预览
				LODOP.PREVIEW();			
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('rb');"
			}
		})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}