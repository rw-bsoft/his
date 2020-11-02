$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.ClinicChargesPrintView = function(cfg) {
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
	phis.prints.script.ClinicChargesPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.ClinicChargesPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_invoice";
					this.conditionFormId = "SimplePrint_form_invoice";
					this.mainFormId = "SimplePrint_mainform_invoice";
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
				items : this.initConditionFields()
			},
			html : "<iframe id='"
								+ this.frameId
								+ "' width='100%' height='100%' onload='simplePrintMask(\"invoice\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	initConditionFields : function() {
		var items = []
		items.push(new Ext.form.DateField({
					name : 'beginDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
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
		var preview = this.createCommonDic("type")
		preview.value = "1"
		items.push(preview)
		items.push({
					xtype : "button",
					text : "产 生",
					iconCls : "default",
					scope : this,
					handler : this.doLoadReport
				})
		items.push({
					xtype : "button",
					text : "结 帐",
					iconCls : "commit",
					scope : this,
					handler : this.doCommit,
					disabled : true
				})
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
					handler : this.doPrint,
					disabled : true
				})
		return items
	},
	doInquiry : function() {
		var module = this.createModule("queryWin", this.refQueryList);
		var _ctx = this;
		module.panel = this.panel;
		module.doSure = function() {
			var r = module.getSelectedRecord();
			if (r == null) {
				return;
			}
			var hzrq = r.get("HZRQ");
			dyrq = r.get("HZRQ");
			var form = Ext.getCmp(_ctx.conditionFormId).getForm()
			if (!form.isValid()) {
				return
			}
		//	var url = _ctx.printurl + ".print?pages=chargesSummary";
			var pages="chargesSummary";
			 var url="resources/"+pages+".print?type="+1;
			url += "&save=2&hzrq=" + hzrq
			document.getElementById(_ctx.frameId).src = url
			_ctx.queryWin.hide();
		}
		module.doCancel = function() {
			_ctx.queryWin.hide();
		}

		module.onDblClick = function(grid, index, e) {
			var items = this.panel.getTopToolbar().items;
			items.get(6).setDisabled(false);
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
	doPrint : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
					return
				}
				var items = form.items
				this.printurl = util.helper.Helper.getUrl();
				var pages="phis.prints.jrxml.Invoice";
				 var url="resources/"+pages+".print?silentPrint=1";
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					url += "&" + f.getName() + "=" + f.getValue()
				}
				url += "&temp=" + new Date().getTime() + "&fphm=" + this.fphm;
				
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
	doCommit : function() {
		dyrq = new Date().format("Y-m-d H:i:s");
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		//var url = this.printurl + ".print?pages=chargesSummary";
		var pages="chargesSummary";
		 var url="resources/"+pages+".print?type=1";
		url += "&save=1"
		document.getElementById(this.frameId).src = url
		var items = this.panel.getTopToolbar().items;
		items.get(4).setDisabled(true);
		items.get(6).setDisabled(false);
	},
	doLoadReport : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
	//	var url = this.printurl + ".print?pages=chargesSummary";
		var pages="chargesSummary";
		 var url="resources/"+pages+".print?type=1";
		for (var i = 0; i < items.getCount(); i++) {
			var f = items.get(i)
			if (f.getName() == "type" && f.getValue() == "1") {
				Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
						"x-mask-loading")
			}
		}

		url += "&temp=" + new Date().getTime()
		document.getElementById(this.frameId).src = url
		var items = this.panel.getTopToolbar().items;
		items.get(4).setDisabled(false);
		items.get(6).setDisabled(true);
	},
	onQueryWinShow : function() {
		var module = this.createModule("queryWin", this.refQueryList);

		var datefrom = this.panel.getTopToolbar().items.get(0).getValue()
				.format('Y-m-d');
		var dateTo = this.panel.getTopToolbar().items.get(1).getValue()
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

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}