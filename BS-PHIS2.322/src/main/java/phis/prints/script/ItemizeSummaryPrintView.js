$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"phis.script.util.DateUtil", "util.dictionary.TreeDicFactory",
		"util.helper.Helper")

phis.prints.script.ItemizeSummaryPrintView = function(cfg) {
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.preview = [{
		value : "1",
		text : "网页预览"
	}, {
		value : "0",
		text : "PDF"
	}, {
		value : "2",
		text : "WORD"
	}, {
		value : "3",
		text : "EXCEL"
	}]
	this.conditions = []
	this.dyrq = "";
	phis.prints.script.ItemizeSummaryPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.ItemizeSummaryPrintView,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.frameId = "SimplePrint_frame_phis.prints.jrxml.ItemizeSummary";
				this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.ItemizeSummary";
				this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.ItemizeSummary";
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
							+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.ItemizeSummary\")'></iframe>"
				})
				this.panel = panel
				return panel
			},
			initConditionFields : function() {
				var items = []
				items.push(new Ext.form.Label({
					text : "从"
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
					text : "到"
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
				// var preview = this.createCommonDic("type")
				// preview.value = "1"
				// items.push(preview);
				items.push({
					xtype : "button",
					text : "刷新",
					iconCls : "query",
					scope : this,
					handler : this.doInquiry
				});
				items.push({
					xtype : "button",
					text : "打 印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint,
					disabled : true
				});
				items.push({
					xtype : "button",
					text : "导出",
					iconCls : "excel",
					scope : this,
					handler : this.doExcel,
					disabled : true
				})
				return items
			},
			doPrint : function() {
				var datefrom = this.panel.getTopToolbar().items.get(1)
						.getValue().format('Y-m-d');
				var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
						.format('Y-m-d');
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				if (datefrom == null || dateTo == null) {
					Ext.MessageBox.alert("提示", "开始时间和结束时间不能为空");
					return;
				}
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				// var url = this.printurl +
				// "*.print?pages=phis.prints.jrxml.ItemizeSummary&landscape=1";
				var pages = "phis.prints.jrxml.ItemizeSummary";
				var url = "resources/" + pages + ".print?landscape=1";
				url += "&datefrom=" + datefrom + "&dateTo=" + dateTo
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				// 预览LODOP.PREVIEW();
				// 预览LODOP.PRINT();
				// LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi
						.loadXML({
							url : url,
							httpMethod : "get"
						}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				// 预览
				LODOP.PREVIEW();
			},
			doExcel : function() {
				var datefrom = this.panel.getTopToolbar().items.get(1)
						.getValue().format('Y-m-d');
				var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
						.format('Y-m-d');
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				if (datefrom == null || dateTo == null) {
					Ext.MessageBox.alert("提示", "开始时间和结束时间不能为空");
					return;
				}
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				// var url = this.printurl +
				// "*.print?pages=phis.prints.jrxml.ItemizeSummary&landscape=1";
				var pages = "phis.prints.jrxml.ItemizeSummary";
				var url = "resources/" + pages + ".print?type=3&landscape=1";
				url += "&datefrom=" + datefrom + "&dateTo=" + dateTo
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
			doInquiry : function() {
				var datefrom = this.panel.getTopToolbar().items.get(1)
						.getValue().format('Y-m-d');
				var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
						.format('Y-m-d');
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				if (datefrom == null || dateTo == null) {
					Ext.MessageBox.alert("提示", "开始时间和结束时间不能为空");
					return;
				}
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				// var url = this.printurl +
				// "*.print?pages=phis.prints.jrxml.ItemizeSummary";
				var pages = "phis.prints.jrxml.ItemizeSummary";
				var url = "resources/" + pages + ".print?type=1";
				url += "&datefrom=" + datefrom + "&dateTo=" + dateTo
				document.getElementById(this.frameId).src = url;
				this.panel.getTopToolbar().items.get(5).setDisabled(false);
				this.panel.getTopToolbar().items.get(6).setDisabled(false);
			},
			createCommonDic : function(flag) {
				var fields
				var emptyText = "请选择"
				if (flag == "type") {
					fields = this.preview
					emptyText = "预览方式"
				} else {
					fields = []
					flag = ""
				}
				var store = new Ext.data.JsonStore({
					fields : ['value', 'text'],
					data : fields
				});
				var combox = new Ext.form.ComboBox({
					store : store,
					valueField : "value",
					displayField : "text",
					mode : 'local',
					triggerAction : 'all',
					emptyText : emptyText,
					selectOnFocus : true,
					width : 100,
					name : flag,
					allowBlank : false
				})
				return combox
			}
		})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}