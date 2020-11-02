$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.DepartmentCollectPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.DepartmentCollectPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.DepartmentCollectPrintView, app.desktop.Module, {
	initPanel : function() {
		if (this.mainApp.deptId != this.mainApp.topUnitId) {
			if (this.mainApp.treasuryId == null
					|| this.mainApp.treasuryId == ""
					|| this.mainApp.treasuryId == undefined) {
				Ext.Msg.alert("提示", "未设置登录库房,请先设置");
				return null;
			}
			if (this.mainApp.treasuryEjkf != 0) {
				Ext.MessageBox.alert("提示", "该库房不是一级库房!");
				return;
			}
			if (this.mainApp.treasuryCsbz != 1) {
				Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
				return;
			}
		}
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.DepartmentCollectServlet";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.DepartmentCollectServlet";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.DepartmentCollectServlet";
		var tree = util.dictionary.TreeDicFactory.createTree({
					id : "phis.dictionary.departmentwl_tree",
					"parentKey" : this.navParentKey,
					"parentText" : this.navParentText,
					"rootVisible" : true
				})
		tree.autoScroll = true
		tree.on("click", this.onTreeClick, this)
		// tree.expandAll()
		this.tree = tree;
		var panel = new Ext.Panel({
			id : this.mainFormId,
			region : "center",
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.DepartmentCollectServlet\")'></iframe>"
		})
		var recpanel = new Ext.Panel({
					height : this.height,
					layout : "border",
					items : [{
								layout : 'fit',
								// title : this.title,
								region : 'west',
								split : true,
								collapsible : true,
								width : 180,
								items : [tree]
							}, panel]
				})
		this.panel = recpanel
		return this.panel
	},
	createDicField : function(dic) {
		if (dic.id == "phis.dictionary.department_bq") {
			var arr = new Array();
			arr.push(31);
			// dic.filter="['eq',['$map',['s','JGID']],['s','1']]";
		}
		var cls = "util.dictionary.";
		if (!dic.render) {
			cls += "Simple";
		} else {
			cls += dic.render
		}
		cls += "DicFactory"
		$import(cls)
		var factory = eval("(" + cls + ")")
		var field = factory.createDic(dic)
		return field
	},
	onTreeClick : function(node, e) {
		var cls = this.childTab
		var id = node.id
		var text = node.text
		this.depValue = [];
		var value = [];
		if (node.parentNode) {
			value.push(node.id);
		}
		if (node.hasChildNodes()) {// 有子节点
			// 得到所有子节点
			this.findChildNodes(node, value);
		}
		this.depValue = value;
		this.doQuery();
	},
	findChildNodes : function(node, value) {
		var childnodes = node.childNodes;
		for (var i = 0; i < childnodes.length; i++) {
			var nd = childnodes[i];
			value.push(nd.id);
			if (nd.hasChildNodes()) {
				this.findChildNodes(nd, value);
			}
		}
	},
	getTbar : function() {
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
		var tbar = [];
		tbar.push(new Ext.form.Label({
					text : "会计日期 从:"
				}));
		this.dateFrom = new Ext.form.DateField({
					id : 'dateFromforly',
					name : 'dateFromforly',
					value : dateFromValue,
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				});
		tbar.push(this.dateFrom);
		tbar.push(new Ext.form.Label({
					text : " 到 "
				}));
		this.dateTo = new Ext.form.DateField({
					id : 'dateToforly',
					name : 'dateToforly',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				});
		tbar.push(this.dateTo);
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
	onCatalogChanage : function(node, e) {
		this.depValue = [];
		var value = [];
		value.push(node.id);
		if (node.hasChildNodes()) {// 有子节点
			// 得到所有子节点
			this.findChildNodes(node, value);
		}
		this.depValue = value;
	},
	doQuery : function() {
		var dateFrom = this.dateFrom.value;
		var dateTo = this.dateTo.value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		if (!this.depValue) {
			this.onTreeClick(this.tree.getRootNode());
		}
		var body = {
			"dateFrom" : dateFrom + " 00:00:00",
			"dateTo" : dateTo + " 23:00:00",
			"KSDM" : this.depValue
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
		//	title : "科室领用汇总",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
		//var url = "*.print?pages=phis.prints.jrxml.DepartmentCollectServlet&config="
		//	+ encodeURI(encodeURI(Ext.encode(printConfig)))
		//	+ "&silentPrint=1";
		var pages="phis.prints.jrxml.DepartmentCollectServlet";
		 var url="resources/"+pages+".print?config="+ encodeURI(encodeURI(Ext.encode(printConfig)))
			+ "&silentPrint=1";;
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		var dateFrom = this.dateFrom.value;
		var dateTo = this.dateTo.value;
		if (!dateFrom || !dateTo) {
			return;
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
				"dateFrom" : dateFrom + " 00:00:00",
				"dateTo" : dateTo + " 23:00:00",
				"KSDM" : this.depValue
			};
		// 把要传的参数放到body里去
		var printConfig = {
		//	title : "科室领用汇总报表",
			page : "whole",
			requestData : body
		}
		var pages="phis.prints.jrxml.DepartmentCollectServlet";
		 var url="resources/"+pages+".print?config="+ encodeURI(encodeURI(Ext.encode(printConfig)))
			+ "&silentPrint=1";
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
