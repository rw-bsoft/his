$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup", "util.DateUtil",
		"util.dictionary.TreeDicFactory", "util.helper.Helper","phis.script.common")
//按科室界面
phis.prints.script.HospitalSpecialDrugsPrintView = function(cfg) {
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.sfrbrq = "";
	phis.prints.script.HospitalSpecialDrugsPrintView.superclass.constructor.apply(this,
			[cfg])
	Ext.apply(this, phis.script.common);
}
Ext.extend(phis.prints.script.HospitalSpecialDrugsPrintView, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_HospitalSpecialDrugsPrintView";
		this.conditionFormId = "SimplePrint_form_HospitalSpecialDrugsPrintView";
		this.mainFormId = "SimplePrint_mainform_HospitalSpecialDrugsPrintView";
		if (this.mainApp.pharmacyId == null
				|| this.mainApp.pharmacyId == ""
				|| this.mainApp.pharmacyId == undefined) {
			Ext.Msg.alert("提示", "未设置登录药房,请先设置");
			return null;
		}
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : "",
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
					+ "' width='100%' height='100%'></iframe>"
		})
		this.panel = panel
		this.panel.on("afterrender", this.onReady, this);
		return panel
	},
	
	initConditionFields : function() {
		var yplx = this.createDicField({
			"defaultIndex" : "0",
			"emptyText" : "全部",
			"width" : 100,
			"id" : "phis.dictionary.pecialMedicines"
			
		})
		var items = []
		items.push(new Ext.form.Label({
					text : "开方日期"
				}))
		items.push(new Ext.form.DateField({
					name : 'beginDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '时间从'
				}))
		items.push(new Ext.form.Label({
					text : "至"
				}))
		items.push(new Ext.form.DateField({
					name : 'endDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '时间'
				}))
		items.push({
					xtype : "label",
					forId : "window",
					text : "药品类型"
				})
		items.push(yplx);
		items.push({
					xtype : "button",
					text : "统计",
					iconCls : "query",
					scope : this,
					handler : this.doquery
				})
		items.push({
					xtype : "button",
					text : "打 印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint,
					disabled : true
				})
		this.yplx = yplx;		
		return items
	},
		createDicField : function(dic) {
		if (dic.id == "phis.dictionary.department_bq") {
			var arr = new Array();
			arr.push(31);
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
	getExecJs : function() {
		return "jsPrintSetup.setPrinter('rb');"
	},
	doPrint : function() {
		var _ctx = this;
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		var dateFrom = this.panel.getTopToolbar().items.get(1).getValue()
				.format('Y-m-d');
		var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
				.format('Y-m-d');
				var yplx=this.yplx.getValue();
		if(!yplx||yplx==null||yplx==""){
		yplx=0;
		}			
		var pages="phis.prints.jrxml.HospitalSpecialDrugs";
		var url="resources/"+pages+".print?silentPrint=1";
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&flag=1&yplx="+yplx;
		var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			// 预览
			LODOP.PREVIEW();
	},
	doClose : function() {
		this.opener.closeCurrentTab();
	},

	doquery : function() {
		var _ctx = this;
		var form = Ext.getCmp(this.conditionFormId).getForm()
		if (!form.isValid()) {
			return
		}
		var items = form.items
		var dateFrom = this.panel.getTopToolbar().items.get(1).getValue()
				.format('Y-m-d');
		var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
				.format('Y-m-d');
		var yplx=this.yplx.getValue();
		if(!yplx||yplx==null||yplx==""){
		yplx=0;
		}		
		var pages="phis.prints.jrxml.HospitalSpecialDrugs";
		var url="resources/"+pages+".print?silentPrint=1";
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&flag=1&yplx="+yplx;
			document.getElementById(_ctx.frameId).src = url
		this.panel.getTopToolbar().items.get(7).setDisabled(false);
	},
	
		
	doQueryDetil: function(ypxh){
		var module = this.createModule("HospitalSpecialDrugsDetails",'phis.application.pha.PHA/PHA/PHA2801');
		var win = module.getWin();
		module.setthis(this.mainApp.pharmacyId,ypxh,this.yplx.getValue());
		module.panel.getTopToolbar().items.get(1).setValue(this.panel.getTopToolbar().items.get(1).getValue());
		module.panel.getTopToolbar().items.get(3).setValue(this.panel.getTopToolbar().items.get(3).getValue());
		win.show();
		module.doquery();
	},
	
	
	
	//监听报表load事件,并增加表格事件监听
	onReady : function() {
		var _ctx = this;
		var iframe = Ext.isIE
				? document.frames[this.frameId]
				: document.getElementById(this.frameId);
		if (iframe.attachEvent) {
			iframe.attachEvent("onload", function() {
						_ctx .simplePrintMask(iframe,
										"HospitalSpecialDrugsPrintView");
					});
		} else {
			iframe.onload = function() {
				_ctx.simplePrintMask(iframe, "HospitalSpecialDrugsPrintView");
			};
		}
	},
	/**
	 * 实现报表事件监听及相应业务处理
	 */
	simplePrintMask : function(iframe, printId) {
		this.panel.el.unmask();
		// add by yangl 报表事件
		var _ctx = this;
		var iframeDoc = Ext.isIE
				? iframe.document
				: iframe.contentWindow.document
		var tables = iframeDoc.getElementsByTagName('table');
		if (tables.length > 1) {
			var trs = tables[1].getElementsByTagName('tr');
			// *********具体列数根据报表决定**************
			var headerRow = 7;// 标题头占的行数,
			var footerRow = 3;// 标题尾占的行数
			for (var i = headerRow; i < trs.length - footerRow; i++) {// 只监听表格内容列
				var tr = trs[i];
				tr.onmousedown = function() {
					tronmousedown(this);
				}
				tr.onclick = function() {
					tronclick(this);
				}
				tr.ondblclick = function() {
					ondblclick(this);
				}
				if(tr.cells[5] && tr.cells[5].style)
				tr.cells[5].style = "display:none;";//隐藏ypxh
			}
			var lastSelectedRow = null;
			// *********选中行颜色变化*********
			function tronmousedown(obj) {
				if (obj != lastSelectedRow) {
					for (var j = 1; j < obj.cells.length - 1; j++) {
						obj.cells[j].style.backgroundColor = '#DFEBF2';
					}
					if (lastSelectedRow) {
						for (var j = 1; j < obj.cells.length - 1; j++) {
							lastSelectedRow.cells[j].style.backgroundColor = '';
						}
					}
				}
				lastSelectedRow = obj;
			}
			// *********单击事件(或者监听双击也可以)*********
			function tronclick(obj) {
				
				// 组装数据,数据为每行单元格中的内容
				var data = [];
				for (var j = 1; j < obj.cells.length - 1; j++) {
					if(obj.cells[j].firstChild && obj.cells[j].firstChild.firstChild 
						&& obj.cells[j].firstChild.firstChild.innerHTML)
					data.push(obj.cells[j].firstChild.firstChild.innerHTML);
				}
				// *****以下可以实现具体业务******
			}
			function ondblclick(obj) {
				// 组装数据,数据为每行单元格中的内容
				var data = [];obj.cells[1].firstChild.innerText
				for (var j = 1; j < obj.cells.length - 1; j++) {
					//debugger;
					if(obj.cells[j].firstChild && obj.cells[j].firstChild.firstChild 
						&& obj.cells[j].firstChild.firstChild.innerHTML)
					data.push(obj.cells[j].firstChild.firstChild.innerHTML);
				}
				_ctx.doQueryDetil(data[3]);
			}
		}
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}