$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")
/**
 * 出院病人汇总表
 * 
 * @param cfg
 */
phis.prints.script.OutHospiatlPatientCollectPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.OutHospiatlPatientCollectPrintView.superclass.constructor.apply(
			this, [cfg])
	this.depValue = [];
}
Ext.extend(phis.prints.script.OutHospiatlPatientCollectPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.OutHospiatlPatientCollectPrintView";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.OutHospiatlPatientCollectPrintView";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.OutHospiatlPatientCollectPrintView";
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
				items : this.getTbar()
			},
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.OutHospiatlPatientCollectPrintView\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar : function() {
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
				+ "-01";
		var tbar = [];
		var rqCom = new Ext.form.ComboBox({
							name : 'rqlx',
		            		valueField : "value",
							displayField : "text",
		            		store : new Ext.data.ArrayStore({
		                        fields : ['value', 'text'],
//		                        data : [[1, '出院日期'], [2, '入院日期'], [3, '收费日期'], [4, '结帐日期'], [5, '汇总日期']]
		                        data : [[3, '出院日期'], [1, '入院日期']]
		                    }),
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 80,
							value : 1
	            });
	    this.rqlxCom = rqCom;
		tbar.push(rqCom)
		tbar.push(new Ext.form.Label({
			text : "从:"
		}));
		// 定义开始时间
		tbar.push(new Ext.form.DateField({
					id : 'dateFrom_out',
					name : 'dateFrom',
					value : dateFromValue,
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
					id : 'dateTo_out',
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
		var depcyofice = this.createDicField({
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
		depcyofice.tree.on("click", this.onCatalogChanage, this);
		depcyofice.tree.on("beforeexpandnode", this.onExpandNode, this);
		depcyofice.tree.on("beforecollapsenode", this.onCollapseNode, this);
		depcyofice.tree.expandAll()// 展开树
		tbar.push(depcyofice);
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
			Ext.MessageBox.alert("提示", "请选择科室");
			return;
		}
		var dateFrom = Ext.getDom("dateFrom_out").value;
		var dateTo = Ext.getDom("dateTo_out").value;
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
		body["rqlx"] = this.rqlxCom.getValue();
		body["ksdm"] = this.depValue;
		body["ZY_CY_FlAG"] = "OutHospiatl";// 在院_出院标志 OutHospiatl 出院 InHospiatl
											// 出院
		// 把要传的参数放到body里去
		var printConfig = {
			// title : "出院病人汇总表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
				"x-mask-loading");

		// 后台servelt
		var pages="phis.prints.jrxml.OutHospiatlNatureServlet";
		 var url="resources/"+pages+".print?config="
			+ encodeURI(encodeURI(Ext.encode(printConfig)))
			+ "&silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		if (this.depValue.length == 0) {
			Ext.MessageBox.alert("提示", "请选择科室");
			return;
		}
		var dateFrom = Ext.getDom("dateFrom_out").value;
		var dateTo = Ext.getDom("dateTo_out").value;
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
		body["rqlx"] = this.rqlxCom.getValue();
		body["ksdm"] = this.depValue;
		body["ZY_CY_FlAG"] = "OutHospiatl";// 在院_出院标志 OutHospiatl 出院 InHospiatl
											// 出院
		// 把要传的参数放到body里去
		var printConfig = {
			// title : "出院病人汇总表",
			page : "whole",
			requestData : body
		}
		var pages="phis.prints.jrxml.OutHospiatlNatureServlet";
		 var url="resources/"+pages+".print?config="
			+ encodeURI(encodeURI(Ext.encode(printConfig)))
			+ "&silentPrint=1";
	/*
	 * window .open( url, "", "height=" + (screen.height - 100) + ", width=" +
	 * (screen.width - 10) + ", top=0, left=0, toolbar=no, menubar=yes,
	 * scrollbars=yes, resizable=yes,location=no, status=no")
	 */
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
			Ext.MessageBox.alert("提示", "请选择科室");
			return;
		}
		var dateFrom = Ext.getDom("dateFrom_out").value;
		var dateTo = Ext.getDom("dateTo_out").value;
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
		body["rqlx"] = this.rqlxCom.getValue();
		body["ksdm"] = this.depValue;
		body["ZY_CY_FlAG"] = "OutHospiatl";// 在院_出院标志 OutHospiatl 出院 InHospiatl
											// 出院
		// 把要传的参数放到body里去
		var printConfig = {
			// title : "出院病人汇总表",
			page : "whole",
			requestData : body
		}
		var pages="phis.prints.jrxml.OutHospiatlNatureServlet";
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
