$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.PrescriptionPrintView = function(cfg) {
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
	phis.prints.script.PrescriptionPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.PrescriptionPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_prescription";
		this.conditionFormId = "SimplePrint_form_prescription";
		this.mainFormId = "SimplePrint_mainform_prescription";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"prescription\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	initConditionFields : function() {
		var items = []
		var preview = this.createCommonDic("type")
		preview.value = "1"
		items.push(preview)
		items.push({
					xtype : "button",
					text : "生成报表",
					iconCls : "default",
					scope : this,
					handler : this.doLoadReport
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
	doPrint : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		//var url = this.printurl + "*.print?pages=phis.prints.jrxml.Prescription&silentPrint=1&execJs="
			//			+ this.getExecJs();
		var pages="phis.prints.jrxml.Prescription";
		//type:1:西药 2：中药
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicManageService",
					serviceAction : "queryCfjmb",
					body : {type:'1'}
				});
		if (r.code <= 300) {
			if(r.json.cfjmbmc && r.json.cfjmbmc!=""){
				pages = "phis.prints.jrxml."+r.json.cfjmbmc;
			}
		}
		 var url="resources/"+pages+".print?silentPrint=1&execJs="
						+ this.getExecJs();
		for (var i = 0; i < items.getCount(); i++) {
			var f = items.get(i)
			url += "&" + f.getName() + "=" + f.getValue()
		}
		url += "&temp=" + new Date().getTime()+"&cfsb="+this.cfsb;
		/*
		var printWin=window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
		 printWin.onafterprint = function() {
			printWin.close();
		};
		*/
		var LODOP=getLodop();
		LODOP.PRINT_INIT("打印控件");
		//LODOP.SET_PRINT_PAGESIZE("0","","","");
		LODOP.SET_PRINT_PAGESIZE("0","","","A5");
		var rehtml =util.rmi.loadXML({url:url,httpMethod:"get"});
		//解决分页ireport分页下一页的内容会打印到上一页的问题
		rehtml=rehtml.replace(/table style=\"/g, "table style=\"page-break-after:always;");
		rehtml=rehtml.replace("<body","<body style='margin: 0'");
		LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtml);
		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
		//预览LODOP.PREVIEW();
		//【begin】2018-12-03 zhaojian 处方打印增加APP诊间支付使用的二维码
		if(this.sfzh){
			LODOP.ADD_PRINT_BARCODE(55, "60", 120, 38, "128Auto", this.mzhm);
			var ewm = this.mainApp.deptId+";"+this.sfzh+";"+this.kfrq.substring(0,10)+";"+this.kfrq.substring(0,10);
			LODOP.ADD_PRINT_BARCODE(55, "5", 50, 50, "QRCode", ewm);
		}
		else{
			LODOP.ADD_PRINT_BARCODE(55, "15%", 120, 38, "128Auto", this.mzhm);
		}
		//【end】2018-12-03 zhaojian 处方打印增加APP诊间支付使用的二维码
		//直接打印
		LODOP.PRINT();
	},
	getExecJs : function() {
		return "jsPrintSetup.setPrinter('cffp');"
	},
	doLoadReport : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		//var url = this.printurl + "*.print?pages=phis.prints.jrxml.Prescription";
		var pages="phis.prints.jrxml.Prescription";
		//type:1:西药 2：中药
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicManageService",
					serviceAction : "queryCfjmb",
					body : {type:'1'}
				});
		if (r.code <= 300) {
			if(r.json.cfjmbmc && r.json.cfjmbmc!=""){
				pages = "phis.prints.jrxml."+r.json.cfjmbmc;
			}
		}
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