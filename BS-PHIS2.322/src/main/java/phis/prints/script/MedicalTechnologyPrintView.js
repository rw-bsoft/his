$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.SimpleDicFactory", "util.helper.Helper","phis.script.widgets.DatetimeField")

phis.prints.script.MedicalTechnologyPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.brxzValue = [];
	phis.prints.script.MedicalTechnologyPrintView.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.prints.script.MedicalTechnologyPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.MedicalTechnology";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.MedicalTechnology";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.MedicalTechnology";
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
					columns : 8,
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.MedicalTechnology\")'></iframe>"
		});
		this.panel = panel;
		return panel
	},
	getTbar : function() {
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
				+ "-01 00:00:00";
		var tbar = [];
		tbar.push(new Ext.form.Label({
					width : 80,
					text : "收费日期 : "
				}));
		tbar.push(new phis.script.widgets.DateTimeField({
					id : 'dateFrom_MT',
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
					id : 'dateTo_MT',
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
					text : " 开单医生: "
				}));
		// 医生
		var brxz = util.dictionary.SimpleDicFactory.createDic({
			id : 'phis.dictionary.user_YJSJYS',
			name : 'user_YJSJYS',
			sliceType : 0,
			width : 260
		});
		brxz.on("select",this.brxzSelect,this);
		tbar.push(brxz);
		tbar.push({
					xtype : "button",
					text : " 刷  新 ",
					iconCls : "query",
					scope : this,
					handler : this.doQuery,
					disabled : false
				});
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
	brxzSelect:function(combo,record,index){
		this.KDYS=combo.getValue();
	},
	doQuery : function() {
		if (this.KDYS == null||this.KDYS=="") {
			Ext.MessageBox.alert("提示", "请选择开单医生");
			return;
		}
		var tbar = this.panel.getTopToolbar();
		var dateFrom = Ext.getDom("dateFrom_MT").value;
		var dateTo = Ext.getDom("dateTo_MT").value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {};
		body["dateFrom"] = dateFrom;
		body["dateTo"] = dateTo;
		body["KDYS"] = this.KDYS;
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			//title : "医生医技检查收入统计表",
			page : "whole",
			file : "MedicalTechnologyFile",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
		//var url = "*.print?pages=phis.prints.jrxml.TrendsReportServlet&config="
		//	+ encodeURI(encodeURI(Ext.encode(printConfig)))
		//	+ "&silentPrint=1";
		var pages="phis.prints.jrxml.TrendsReportServlet";
		 var url="resources/"+pages+".print?config="
			+ encodeURI(encodeURI(Ext.encode(printConfig)))
			+ "&silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		if (this.KDYS == null||this.KDYS=="") {
			Ext.MessageBox.alert("提示", "请选择开单医生");
			return;
		}
		var tbar = this.panel.getTopToolbar();
		var dateFrom = Ext.getDom("dateFrom_MT").value;
		var dateTo = Ext.getDom("dateTo_MT").value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {};
		body["dateFrom"] = dateFrom;
		body["dateTo"] = dateTo;
		body["KDYS"] = this.KDYS;

		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			//title : "医生医技检查收入统计表",
			page : "whole",
			file : "MedicalTechnologyFile",
			requestData : body
		}
		var pages="phis.prints.jrxml.TrendsReportServlet";
		 var url="resources/"+pages+".print?config="
			+ encodeURI(encodeURI(Ext.encode(printConfig)))
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
	}

})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
