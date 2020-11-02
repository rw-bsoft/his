$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.TreatmentPrintView = function(cfg) {
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
	phis.prints.script.TreatmentPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.TreatmentPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_treatment";
		this.conditionFormId = "SimplePrint_form_treatment";
		this.mainFormId = "SimplePrint_mainform_treatment";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"treatment\")'></iframe>"
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
		var pages="phis.prints.jrxml.Treatment";
		var reprint = "";
		this.yjxh = this.arrayUnique(this.yjxh);
		//yx-需求变动-处置不组打
		var yjxh=0;
		for(var j = 0 ; j < this.yjxh.length ; j ++){
			if(this.yjxh[j]+''=='0')continue;
			yjxh=this.yjxh[j]
		}
		var url="resources/"+pages+".print?silentPrint=1";
		for (var i = 0; i < items.getCount(); i++) {
			var f = items.get(i)
			url += "&" + f.getName() + "=" + f.getValue()
		}
		url += "&temp=" + new Date().getTime()+"&yjxh="+yjxh+"&brid=" + this.brid+"&jzxh=" + this.jzxh
				reprint += util.rmi.loadXML({url:url,httpMethod:"get"})
		var LODOP=getLodop();
		LODOP.PRINT_INIT("打印控件");
		LODOP.SET_PRINT_PAGESIZE("0","","","A5");
		//解决分页ireport分页下一页的内容会打印到上一页的问题
		reprint=reprint.replace(/table style=\"/g, "table style=\"page-break-after:always;");
		reprint=reprint.replace("<body","<body style='margin: 0'");
		LODOP.ADD_PRINT_HTM("0","0","100%","100%",reprint);
		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
		//zhaojian 2017-09-15 处置笺打印增加条形码
		LODOP.ADD_PRINT_BARCODE(40, "5.0%", 138, 38, "128Auto", this.mzhm);
		//LODOP.PREVIEW();// 预览
		//直接打印
		LODOP.PRINT();
		
	},
	arrayUnique : function(arr) {//去重
		var n = {},r=[]; //n为hash表，r为临时数组
		for(var i = 0; i < arr.length; i++) //遍历当前数组
		{
			if (!n[arr[i]]) //如果hash表中没有当前项
			{
				n[arr[i]] = true; //存入hash表
				r.push(arr[i]); //把当前数组的当前项push到临时数组里面
			}
		}
		return r;
	},
	doLoadReport : function() {
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		var pages="phis.prints.jrxml.Treatment";
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