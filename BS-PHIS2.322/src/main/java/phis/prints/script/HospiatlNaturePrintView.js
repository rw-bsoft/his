$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")
/**
 * 住院性质费用汇总
 * 
 * @param cfg
 */
phis.prints.script.HospiatlNaturePrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.HospiatlNaturePrintView.superclass.constructor.apply(
			this, [cfg])
	this.brxzValue=[];
}
Ext.extend(phis.prints.script.HospiatlNaturePrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.HospiatlNaturePrintView";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.HospiatlNaturePrintView";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.HospiatlNaturePrintView";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.HospiatlNaturePrintView\")'></iframe>"
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
			text : "从:"
		}));
		// 定义开始时间
		tbar.push(new Ext.form.DateField({
					id : 'dateFrom_Nat',
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
					id : 'dateTo_Nat',
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
					text : " 性质: "
				}));
		// 病人性质树
		var brxz = util.dictionary.TreeDicFactory.createDic({
			id : 'phis.dictionary.patientProperties_tree',
			name : 'phis.dictionary.patientProperties_tree',
			sliceType : 5,
			parentKey : -2,
			width : 120
		});
		brxz.tree.on("click", this.onCatalogChanage, this);
		brxz.tree.on("beforeexpandnode", this.onExpandNode, this);
		brxz.tree.on("beforecollapsenode", this.onCollapseNode, this);
		brxz.tree.expandAll()// 展开树
		this.brxz=brxz;
		tbar.push(brxz);
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
	/**
	 * 寻找孩子节点
	 * 
	 * @param node
	 * @param value
	 */
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
	/**
	 * 病人性质树选择改变
	 * 
	 * @param node
	 * @param e
	 */
	onCatalogChanage : function(node, e) {
		this.queryType = "2";
		this.ref = "0";
		this.Nodetext = node.attributes.text;
		var value = [];
		var tbar = this.panel.getTopToolbar();
		if (node.hasChildNodes()) {// 有子节点
			// 得到所有子节点
			this.findChildNodes(node, value);
		} else {// 没有子节点
			value.push(node.id);
		}
		this.brxzValue = value;
	},
	doQuery : function() {
		if (this.brxzValue.length == 0) {
			Ext.MessageBox.alert("提示", "请选择病人性质");
			return;
		}
		var dateFrom = Ext.getDom("dateFrom_Nat").value;
		var dateTo = Ext.getDom("dateTo_Nat").value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {};
		body["dateFrom"] = dateFrom + " 00:00:00";
		body["dateTo"] = dateTo + " 23:59:59";
		body["tjxz"] = this.brxzValue;
		
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			//title : "住院性质费用汇总表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
				"x-mask-loading");

		// 后台servelt
		var pages="phis.prints.jrxml.HospiatlNatureSummaryServlet";
		 var url="resources/"+pages+".print?config="
			+ encodeURI(encodeURI(Ext.encode(printConfig)))
			+ "&silentPrint=1";
// var url =
// "*.print?pages=phis.prints.jrxml.HospiatlNatureSummaryServlet&config="
// + encodeURI(encodeURI(Ext.encode(printConfig)))
// + "&silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		if (this.brxzValue.length == 0) {
			Ext.MessageBox.alert("提示", "请选择病人性质");
			return;
		}
		var dateFrom = Ext.getDom("dateFrom_Nat").value;
		var dateTo = Ext.getDom("dateTo_Nat").value;
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
		body["tjxz"] = this.brxzValue;

		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			//title : "住院性质费用汇总表",
			page : "whole",
			requestData : body
		}
		var pages="phis.prints.jrxml.HospiatlNatureSummaryServlet";
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
		if (this.brxzValue.length == 0) {
			Ext.MessageBox.alert("提示", "请选择病人性质");
			return;
		}
		var dateFrom = Ext.getDom("dateFrom_Nat").value;
		var dateTo = Ext.getDom("dateTo_Nat").value;
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
		body["tjxz"] = this.brxzValue;

		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			//title : "住院性质费用汇总表",
			page : "whole",
			requestData : body
		}
		var pages="phis.prints.jrxml.HospiatlNatureSummaryServlet";
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
	onTreeNotifyDrop : function(dd, e, data) {
		var n = this.getTargetFromEvent(e);
		var r = dd.dragData.selections[0];
		var node = n.node
		var ctx = dd.grid.__this

		if (!node.leaf || node.id == r.data[ctx.navField]) {
			return false
		}
		var updateData = {}
		updateData[ctx.schema.pkey] = r.id
		updateData[ctx.navField] = node.attributes.key
		ctx.saveToServer(updateData, r)
		// node.expand()
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

	}
})

simplePrintMask = function(printId) {
Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
