$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")
/**
 * 住院收入核算表
 * 
 * @param cfg
 */
phis.prints.script.HospiatlIncomeAccountPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.HospiatlIncomeAccountPrintView.superclass.constructor.apply(
			this, [cfg])
	this.depValue = [];
	this.ib_ks = 1;
	this.ib_ys = 0;
}
Ext.extend(phis.prints.script.HospiatlIncomeAccountPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.HospiatlIncomeAccountPrintView";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.HospiatlIncomeAccountPrintView";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.HospiatlIncomeAccountPrintView";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : this.title,
			tbar : {
// id : this.conditionFormId,
// xtype : "form",
// layout : "hbox",
				layoutConfig : {
					pack : 'start',
					align : 'middle'
				},
				frame : true,
				enableOverflow : true,
				items : this.getTbar2()
			},
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.HospiatlIncomeAccountPrintView\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar2 : function() {
		var tbar = [];
		tbar.push(new Ext.form.Label({
			text : "从:"
		}));
		// 定义开始时间
		var dat=new Date().format('Y-m-d');
		dat=dat.substring(0,dat.lastIndexOf("-"))+'-01';
		tbar.push(new Ext.form.DateField({
					id : 'dateFrom_proAcc',
					name : 'dateFrom',
					value : dat,
					width : 90,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				}));
		tbar.push(new Ext.form.Label({
					text : " 到 "
				}));
		// 定义结束时间
		tbar.push(new Ext.form.DateField({
					id : 'dateTo_proAcc',
					name : 'dateTo',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				}));
		tbar.push(new Ext.form.Label({
					// width : 200,
					text : " 科室: "
				}));
		// 科室
		var BRKSdic = {
				"id" : "phis.dictionary.department_zy",
				"filter" : "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]",
				"src" : "SYS_Office.ID",
				"width" : 130,
				"emptyText" : '全部'
			};
		var depsyofice = this.createDicField({
			"src" : "",
			"defaultIndex" : "0",
			"width" : 120,
			"id" : "phis.dictionary.department_tree_tj",
			"render" : "Tree",
			"filter" : "['and',['eq',['$','item.properties.ORGANIZCODE'],['s','"+this.mainApp.deptRef+"']],['eq',['$','item.properties.HOSPITALDEPT'],['s','1']]]",
// "parentKey" : this.navParentKey || {},
			"parentKey" : this.mainApp.deptRef || {},
			"rootVisible" : "true",
			"editable":false
		});
		depsyofice.tree.on("click", this.onCatalogChanage, this);
		depsyofice.tree.on("beforeexpandnode", this.onExpandNode, this);
		depsyofice.tree.on("beforecollapsenode", this.onCollapseNode, this);
		depsyofice.tree.expandAll()// 展开树
		tbar.push(depsyofice);
		var boxLabels1 = ['科室', '医生'];
				for (var i = 0; i < boxLabels1.length; i++) {
					tbar.push({
						xtype : "radio",
						checked : (i + 1) == 1,
						boxLabel : boxLabels1[i],
						inputValue : i + 1,
						name : "ksys",
						listeners : {
							check : function(group, checked) {
								if (checked) {
									if (group.inputValue == 1) {
										this.ib_ks = 1;
										this.ib_ys = 0;
									} else if (group.inputValue == 2) {
										this.ib_ks = 0;
										this.ib_ys = 1;
									}
								}
							},
							scope : this
						}
					});
				}
		// 统计按钮
		tbar.push({
					xtype : "button",
					text : "统计",
					iconCls : "query",
					scope : this,
					handler : this.doQuery,
					disabled : false
				});
		// 打印按钮
		tbar.push({
					xtype : "button",
					text : "打印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint,
					disabled : false
				});	
		tbar.push({
					xtype : "button",
					text : "导出",
					iconCls : "excel",
					scope : this,
					handler : this.doExcel
				})
		return tbar;
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
	onCatalogChanage : function(node, e) {
		this.depValue = [];
		var value = [];
		if(!isNaN(node.id)){
			value.push(node.id);
		}
		if (node.hasChildNodes()) {// 有子节点
			// 得到所有子节点
			this.findChildNodes(node, value);
		}
		this.depValue = value;
	},
	doQuery : function() {
		if (this.depValue.length == 0) {
			Ext.MessageBox.alert("提示", "请选择住院科室");
			return;
		}
		var dateFrom = Ext.getDom("dateFrom_proAcc").value;
		var dateTo = Ext.getDom("dateTo_proAcc").value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		// var node = Ext.getDom("patientProperties_tree").value;
		var body = {};
		body["dateFrom"] = dateFrom + " 00:00:00";
		body["dateTo"] = dateTo + " 23:59:59";
		body["ksdm"] = this.depValue;
		body["ib_ks"] = this.ib_ks;
		body["ib_ys"] = this.ib_ys;
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			// title : "住院收入核算表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");

		// 后台servelt
// var url = "*.print?pages=phis.prints.jrxml.HospiatlNatureServlet&config="
// + encodeURI(encodeURI(Ext.encode(printConfig)))
// + "&silentPrint=1";
		var pages="phis.prints.jrxml.HospiatlNatureServlet";
		 var url="resources/"+pages+".print?config="
			+ encodeURI(encodeURI(Ext.encode(printConfig)))
			+ "&silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		if (this.depValue.length == 0) {
			Ext.MessageBox.alert("提示", "请选择住院科室");
			return;
		}
		var dateFrom = Ext.getDom("dateFrom_proAcc").value;
		var dateTo = Ext.getDom("dateTo_proAcc").value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		// var node = Ext.getDom("patientProperties_tree").value;
		var body = {};
		body["dateFrom"] = dateFrom + " 00:00:00";
		body["dateTo"] = dateTo + " 23:59:59";
		body["ksdm"] = this.depValue;
		body["ib_ks"] = this.ib_ks;
		body["ib_ys"] = this.ib_ys;

		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			// title : "住院收入核算表",
			page : "whole",
			requestData : body
		}
		var pages="phis.prints.jrxml.HospiatlNatureServlet";
		 var url="resources/"+pages+".print?config="
				+ encodeURI(encodeURI(Ext.encode(printConfig)))
				+ "&silentPrint=1";
		    var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			// 预览LODOP.PREVIEW();
			// 预览LODOP.PRINT();
			// LODOP.PRINT_DESIGN();
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			// 预览
			LODOP.PREVIEW();
	},
	doExcel : function() {
		if (this.depValue.length == 0) {
			Ext.MessageBox.alert("提示", "请选择住院科室");
			return;
		}
		var dateFrom = Ext.getDom("dateFrom_proAcc").value;
		var dateTo = Ext.getDom("dateTo_proAcc").value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		// var node = Ext.getDom("patientProperties_tree").value;
		var body = {};
		body["dateFrom"] = dateFrom + " 00:00:00";
		body["dateTo"] = dateTo + " 23:59:59";
		body["ksdm"] = this.depValue;
		body["ib_ks"] = this.ib_ks;
		body["ib_ys"] = this.ib_ys;

		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			// title : "住院收入核算表",
			page : "whole",
			requestData : body
		}
		var pages="phis.prints.jrxml.HospiatlNatureServlet";
		 var url="resources/"+pages+".print?type=3&config="
				+ encodeURI(encodeURI(Ext.encode(printConfig)))
				+ "&silentPrint=1";
		var printWin = window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
		return printWin;
	},
	onExpandNode : function(node) {
		if (node.getDepth() > 0 && !node.type) {
			node.setIcon(ClassLoader.appRootOffsetPath + "resources/phis/resources/images/open.png");
			// 判断node是否有type
		}
	},
	onCollapseNode : function(node) {
		if (node.getDepth() > 0 && !node.type) {
			node.setIcon(ClassLoader.appRootOffsetPath + "resources/phis/resources/images/close.png");
		}

	},
	createDicField : function(dic) {
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
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
