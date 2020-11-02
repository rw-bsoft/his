$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper","phis.script.SimpleModule")

phis.prints.script.InventoryLedgerEjPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.InventoryLedgerEjPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.InventoryLedgerEjPrintView, app.desktop.Module, {
	initPanel : function() {
		if (this.mainApp.deptId != this.mainApp.topUnitId) {
			if (this.mainApp.treasuryId == null
					|| this.mainApp.treasuryId == ""
					|| this.mainApp.treasuryId == undefined) {
				Ext.Msg.alert("提示", "未设置登录库房,请先设置");
				return null;
			}
			if (this.mainApp.treasuryEjkf == 0) {
				Ext.MessageBox.alert("提示", "该库房不是二级库房!");
				return;
			}
			if (this.mainApp.treasuryCsbz != 1) {
				Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
				return;
			}
		}
		this.frameId = "SimplePrint_frame_inventoryLedgerEj";
		this.conditionFormId = "SimplePrint_form_inventoryLedgerEj";
		this.mainFormId = "SimplePrint_mainform_inventoryLedgerEj";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : this.name,
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"inventoryLedgerEj\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar : function() {
		var tbar = [];
		tbar.push(new Ext.form.Label({
					text : "会计月份:"
				}));
		var date = {
			name : 'date',
			fieldLabel : '会计日期',
			xtype : "uxspinner",
			strategy : {
				xtype : "month"
			},
			width : 80,
			height : 20,
			value : new Date().format('Y-m')
		}
		tbar.push(date);
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
		var date = this.panel.getTopToolbar().items.get(1).getValue();
		if (!date) {
			return
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		//var url = this.printurl + "*.print?pages=phis.prints.jrxml.InventoryLedgerej";
		var pages="phis.prints.jrxml.InventoryLedgerej";
		 var url="resources/"+pages+".print?type=1";
		url += "&date=" + date;
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var date = this.panel.getTopToolbar().items.get(1).getValue();
		if (!date) {
			return
		}
		//var url = this.printurl + "*.print?pages=phis.prints.jrxml.InventoryLedgerej";
		var pages="phis.prints.jrxml.InventoryLedgerej";
		 var url="resources/"+pages+".print?type=1";
		url += "&date=" + date;
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
	doAction : function(item, e) {
		var cmd = item.cmd
		var ref = item.ref

		if (ref) {
			this.loadRemote(ref, item)
			return;
		}
		var script = item.script
		if (cmd == "create") {
			if (!script) {
				script = this.createCls
			}
			this.loadModule(script, this.entryName, item)
			return
		}
		if (cmd == "update" || cmd == "read") {
			var r = this.getSelectedRecord()
			if (r == null) {
				return
			}
			if (!script) {
				script = this.updateCls
			}
			this.loadModule(script, this.entryName, item, r)
			return
		}
		cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
		if (script) {
			$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
		} else {
			var action = this["do" + cmd]
			if (action) {
				action.apply(this, [item, e])
			}
		}
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
