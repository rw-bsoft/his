$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")
/**
 * 在院病人汇总表
 * 
 * @param cfg
 */
phis.prints.script.InHospiatlPatientCollectPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.InHospiatlPatientCollectPrintView.superclass.constructor.apply(
			this, [cfg])
	this.depValue = [];
}
Ext.extend(phis.prints.script.InHospiatlPatientCollectPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.InHospiatlPatientCollectPrintView";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.InHospiatlPatientCollectPrintView";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.InHospiatlPatientCollectPrintView";
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
				items : this.getTbar1()
			},
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.InHospiatlPatientCollectPrintView\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar1 : function() {
// var dat = new Date().format('Y-m-d');
// var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
// + "-01";
		var tbar = [];
		tbar.push(new Ext.form.Label({
			text : "汇总时间:"
		}));
		tbar.push(new Ext.ux.form.Spinner({
			id : 'dateFrom_in',
			name : 'dateFrom',
			value : new Date()
					.format('Y-m'),
			strategy : {
				xtype : "month"
			},
				width : 100
			}));
		tbar.push(new Ext.form.Label({
					// width : 200,
					text : " 科室: "
				}));
		var depzyofice = this.createDicField({
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
		depzyofice.tree.on("click", this.onCatalogChanage, this);
		depzyofice.tree.on("beforeexpandnode", this.onExpandNode, this);
		depzyofice.tree.on("beforecollapsenode", this.onCollapseNode, this);
		depzyofice.tree.expandAll()// 展开树
		tbar.push(depzyofice);
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
		var dateFrom = Ext.getDom("dateFrom_in").value;
		if (!dateFrom) {
			return
		}
		var ym = dateFrom.split("-");
		if (ym.length < 2) {
			Ext.Msg.alert("提示", "财务月份输入有误!");
			return;
		}else{
			if(ym[0].length!=4){
				Ext.Msg.alert("提示", "财务月份输入有误!");
				return;
			}
			if(isNaN(ym[0])){
				Ext.Msg.alert("提示", "财务月份输入有误!");
				return;
			}
			if(ym[1].length!=2||ym[1]<1||ym[1]>12){
				Ext.Msg.alert("提示", "财务月份输入有误!");
				return;
			}
			if(isNaN(ym[1])){
				Ext.Msg.alert("提示", "财务月份输入有误!");
				return;
			}
		}
		// var node = Ext.getDom("patientProperties_tree").value;
		var body = {};
		body["dateFrom"] = dateFrom;
		body["ksdm"] = this.depValue;
		body["ZY_CY_FlAG"] = "InHospiatl";// 在院_出院标志 OutHospiatl 出院 InHospiatl
											// 出院
		
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			// title : "在院病人汇总表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
				"x-mask-loading");

		// 后台servelt
		var pages="phis.prints.jrxml.InHospiatlNatureServlet";
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
		var dateFrom = Ext.getDom("dateFrom_in").value;
		if (!dateFrom) {
			return
		}
		var ym = dateFrom.split("-");
		if (ym.length < 2) {
			Ext.Msg.alert("提示", "财务月份输入有误!");
			return;
		}else{
			if(ym[0].length!=4){
				Ext.Msg.alert("提示", "财务月份输入有误!");
				return;
			}
			if(isNaN(ym[0])){
				Ext.Msg.alert("提示", "财务月份输入有误!");
				return;
			}
			if(ym[1].length!=2||ym[1]<1||ym[1]>12){
				Ext.Msg.alert("提示", "财务月份输入有误!");
				return;
			}
			if(isNaN(ym[1])){
				Ext.Msg.alert("提示", "财务月份输入有误!");
				return;
			}
		}
		// var node = Ext.getDom("patientProperties_tree").value;
		var body = {};
		body["dateFrom"] = dateFrom;
		body["ksdm"] = this.depValue;
		body["ZY_CY_FlAG"] = "InHospiatl";// 在院_出院标志 OutHospiatl 出院 InHospiatl
											// 出院

		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			// title : "在院病人汇总表",
			page : "whole",
			requestData : body
		}
		var pages="phis.prints.jrxml.InHospiatlNatureServlet";
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
