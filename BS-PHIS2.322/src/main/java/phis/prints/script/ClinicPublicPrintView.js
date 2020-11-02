$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup", "phis.script.util.DateUtil",
		"util.dictionary.TreeDicFactory", "util.helper.Helper","phis.script.widgets.DatetimeField")

phis.prints.script.ClinicPublicPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.brxzValue = [];
	phis.prints.script.ClinicPublicPrintView.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.prints.script.ClinicPublicPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_ClinicPublic";
		this.conditionFormId = "SimplePrint_form_ClinicPublic";
		this.mainFormId = "SimplePrint_mainform_ClinicPublic";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : this.title,
			tbar : {
				id : this.conditionFormId,
				xtype : "form",
				layout : "table",
				border : false,
				layoutConfig : {
					columns : 7,
					tableAttrs : {
						border : 0,
						cellpadding : '2',
						cellspacing : "5"
					}
				},
				padding : 2,
				frame : true,
				items : this.getTbar()
			},
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"ClinicPublic\")'></iframe>"
		});
		this.panel = panel;
		return panel
	},
	getTbar : function() {
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01 00:00:00";
		var tbar = [];
		tbar.push(new Ext.form.Label({
					width : 80,
					text : "收费日期 : "
				}));
		tbar.push(new phis.script.widgets.DateTimeField({
					id : 'dateFrom_public',
					name : 'dateFrom',
					value : dateFromValue,
					width : 160,
					hideLabel : true,
					allowBlank : false,
					altFormats : 'Y-m-d H:i:s',
					format : 'Y-m-d H:i:s',
					emptyText : '开始时间'
				}));
		tbar.push(new Ext.form.Label({
					width : 80,
					text : "  到  "
				}));
		tbar.push(new phis.script.widgets.DateTimeField({
					id : 'dateTo_public',
					name : 'dateTo',
					value : new Date(),
					width : 160,
					hideLabel : true,
					allowBlank : false,
					altFormats : 'Y-m-d H:i:s',
					format : 'Y-m-d H:i:s',
					emptyText : '结束时间'
				}));
		tbar.push(new Ext.form.Label({
					width : 80,
					text : "  性质: "
				}));
		// 病人性质树
		var brxz = util.dictionary.TreeDicFactory.createDic({
					id : 'phis.dictionary.patientProperties_tree',
					name : 'patientProperties_tree',
					sliceType : 0,
					parentKey : -2,
					width : 260
				});
		brxz.tree.on("click", this.onCatalogChanage, this);
		brxz.tree.expandAll()// 展开树
		tbar.push(brxz);
		tbar.push({
					xtype : "button",
					text : " 刷  新 ",
					iconCls : "query",
					scope : this,
					handler : this.doQuery,
					disabled : false
				});
		tbar.push(new Ext.form.Label({
					width : 80,
					text : "  发票号: "
				}));
		tbar.push(new Ext.form.TextField({
					width : 160,
					id : 'CFHM',
					name : 'CFHM' // 后台返回的JSON格式，直接赋值
				}));
		tbar.push(new Ext.form.Label({
					width : 80,
					text : "  姓名: "
				}));
		tbar.push(new Ext.form.TextField({
					width : 160,
					id : 'BRXM',
					name : 'BRXM'
				}));
		tbar.push(new Ext.form.Label({
					width : 80,
					text : "  收费员: "
				}));
		tbar.push(new Ext.form.TextField({
					width : 260,
					id : 'YSXM',
					name : 'YSXM'
				}));

		tbar.push({
					xtype : "button",
					text : " 打  印 ",
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
		this.brxzValue = [];
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
			Ext.MessageBox.alert("提示", "请选择性质");
			return;
		}
		var tbar = this.panel.getTopToolbar();
		var dateFrom = Ext.getDom("dateFrom_public").value;
		var dateTo = Ext.getDom("dateTo_public").value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		// var node = Ext.getDom("patientProperties_tree").value;
		var CFHM = Ext.getCmp('CFHM').getValue()
		var BRXM = Ext.getCmp('BRXM').getValue()
		var YSXM = Ext.getCmp('YSXM').getValue()
		var body = {};
		body["dateFrom"] = dateFrom;
		body["dateTo"] = dateTo;
		body["brxzValue"] = this.brxzValue;
		body["CFHM"] = CFHM;
		body["BRXM"] = BRXM;
		body["YSXM"] = YSXM;
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			title : "",
			page : "whole",
			file : "ClinicPublicFile",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");

		// 后台servelt
		//var url = this.printurl + "*.print?pages=phis.prints.jrxml.TrendsReportServlet&config="
		//		+ encodeURI(Ext.encode(printConfig))
		//		+ "&silentPrint=1";
		var pages="phis.prints.jrxml.TrendsReportServlet";
		 var url="resources/"+pages+".print?config="+ encodeURI(Ext.encode(printConfig))
			+ "&silentPrint=1";;
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		if (this.brxzValue.length == 0) {
			Ext.MessageBox.alert("提示", "请选择性质");
			return;
		}
		var tbar = this.panel.getTopToolbar();
		var dateFrom = Ext.getDom("dateFrom_public").value;
		var dateTo = Ext.getDom("dateTo_public").value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var CFHM = Ext.getCmp('CFHM').getValue()
		var BRXM = Ext.getCmp('BRXM').getValue()
		var YSXM = Ext.getCmp('YSXM').getValue()
		var body = {};
		body["dateFrom"] = dateFrom;
		body["dateTo"] = dateTo;
		body["brxzValue"] = this.brxzValue;
		body["CFHM"] = CFHM;
		body["BRXM"] = BRXM;
		body["YSXM"] = YSXM;
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			title : "",
			page : "whole",
			file : "ClinicPublicFile",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		var pages="phis.prints.jrxml.TrendsReportServlet";
	    var url="resources/"+pages+".print?config="+ encodeURI(Ext.encode(printConfig))
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
	}

})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
