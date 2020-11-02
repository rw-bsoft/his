$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup", "phis.script.util.DateUtil",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.ChargesSummaryPrintView_ls = function(cfg) {
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
	phis.prints.script.ChargesSummaryPrintView_ls.superclass.constructor.apply(
			this, [cfg])
}
var hztz = false;
Ext.extend(phis.prints.script.ChargesSummaryPrintView_ls, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.ChargesSummary_ls";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.ChargesSummary_ls";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.ChargesSummary_ls";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.ChargesSummary_ls\")'></iframe>"
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
					value : Date.getServerDate(),
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
					value : Date.getServerDate(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				}))
		items.push({
					xtype : "button",
					text : "查询",
					iconCls : "query",
					scope : this,
					handler : this.doInquiry
				})		
		items.push({
					xtype : "button",
					text : "打 印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint
				})
		
		return items
	},
	doInquiry : function() {
		var module = this.createModule("queryWin", this.refQueryList);
		module.autoLoadData = false;
		var _ctx = this;
		module.panel = this.panel;
		module.doSure = function() {
			var items = this.panel.getTopToolbar().items;
			//items.get(10).setDisabled(false);
			var r = module.getSelectedRecord();
			if (r == null) {
				return;
			}
			var hzrq = r.get("HZRQ");
			_ctx.hzDate = hzrq;
			var form = Ext.getCmp(_ctx.conditionFormId).getForm()
			if (!form.isValid()) {
				return
			}
			var pages="phis.prints.jrxml.ChargesSummary_ls";
			 var url="resources/"+pages+".print?type="+1;
			url += "&save=2&hzrq=" + encodeURI(encodeURI(hzrq))
			document.getElementById(_ctx.frameId).src = url
			_ctx.queryWin.hide();
		}
		module.doCancel = function() {
			_ctx.queryWin.hide();
		}

		module.onDblClick = function(grid, index, e) {
			module.doSure();
		}

		if (!this.queryWin) {
			this.queryWin = module.getWin();
			this.queryWin.add(module.initPanel());
			module.on("winShow", this.onQueryWinShow, this);
		}
		this.queryWin.show();
		this.queryWin.center();
	},
	getExecJs : function() {
		return "jsPrintSetup.setPrinter('rb');"
	},
	doPrint : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var pages="phis.prints.jrxml.ChargesSummary_ls";
		 var url="resources/"+pages+".print?silentPrint=1&landscape=1&execJs="+ this.getExecJs();
		url += "&save=2&hzrq=" + encodeURI(encodeURI(this.hzDate))
		var LODOP=getLodop();
		LODOP.PRINT_INIT("打印控件");
		LODOP.SET_PRINT_PAGESIZE("0","","","");
		LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
		// 预览
		LODOP.PREVIEW();
	},
	onQueryWinShow : function() {
		var module = this.createModule("queryWin", this.refQueryList);

		var datefrom = this.panel.getTopToolbar().items.get(1).getValue()
				.format('Y-m-d');
		var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
				.format('Y-m-d');

		if (datefrom != null && dateTo != null && datefrom != ""
				&& dateTo != "" && datefrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var timeCnd = null;
		if (datefrom != null && datefrom != ""
				&& (dateTo == null || dateTo == "")) {
			timeCnd = ['ge', ['$', "str(HZRQ,'yyyy-mm-dd')"], ['s', datefrom]];
		} else if (dateTo != null && dateTo != ""
				&& (datefrom == null || datefrom == "")) {
			timeCnd = ['le', ['$', "str(HZRQ,'yyyy-mm-dd')"], ['s', dateTo]];
		} else if (dateTo != null && dateTo != "" && datefrom != null
				&& datefrom != "") {
			timeCnd = ['and',
					['ge', ['$', "str(HZRQ,'yyyy-mm-dd')"], ['s', datefrom]],
					['le', ['$', "str(HZRQ,'yyyy-mm-dd')"], ['s', dateTo]]];
		}
		module.requestData.cnd = timeCnd;
		module.requestData.serviceAction = "querySQLList";
		module.loadData();
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