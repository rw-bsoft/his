$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup", "util.DateUtil",
		"util.dictionary.TreeDicFactory", "util.helper.Helper","phis.script.common")
//按科室界面
phis.prints.script.AntimicrobialDrugDepartment_MZ_UseInfoList = function(cfg) {
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.sfrbrq = "";
	phis.prints.script.AntimicrobialDrugDepartment_MZ_UseInfoList.superclass.constructor.apply(this,
			[cfg])
	Ext.apply(this, phis.script.common);
}
Ext.extend(phis.prints.script.AntimicrobialDrugDepartment_MZ_UseInfoList, app.desktop.Module, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_AntimicrobialDrugDepartment_MZ_UseInfoList";
		this.conditionFormId = "SimplePrint_form_AntimicrobialDrugDepartment_MZ_UseInfoList";
		this.mainFormId = "SimplePrint_mainform_AntimicrobialDrugDepartment_MZ_UseInfoList";
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
					emptyText : '开始时间'
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
					emptyText : '结束时间'
				}))
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
		items.push({
					xtype : "button",
					text : "导 出",
					iconCls : "excel",
					scope : this,
					handler : this.doExport,
					disabled : true
				})		
		return items
	},
	getExecJs : function() {
		return "jsPrintSetup.setPrinter('rb');"
	},
	doExport : function(){
		this.type="3";
		this.doPrint();
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
		var type="1";
		if(this.type && this.type=="3"){
			type=this.type;
		}
		this.type="1";		
		var pages="phis.prints.jrxml.AntimicrobialDrugDepartment_MZ_UseInfo";
		var url="resources/"+pages+".print?type="+type;
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&flag=1"
		if(type=="3"){
			var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
			return printWin;
		}else{
			var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			// 预览
			LODOP.PREVIEW();
		}	
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
//		var url = this.printurl + ".print?pages=AntimicrobialDrugDoctorUseInformation";
		var pages="phis.prints.jrxml.AntimicrobialDrugDepartment_MZ_UseInfo";
		var url="resources/"+pages+".print?silentPrint=1";
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&flag=1"
			document.getElementById(_ctx.frameId).src = url
		this.panel.getTopToolbar().items.get(5).setDisabled(false);
		this.panel.getTopToolbar().items.get(6).setDisabled(false);
	},
	
		
	doQueryDetil: function(ksdm){
		var module = this.createModule("AntimicrobialDrugDoctorUseInfo",'phis.application.war.WAR/WAR/WAR57');
		module.ksdm=ksdm;
		var win = module.getWin();
		module.panel.getTopToolbar().items.get(1).setValue(this.panel.getTopToolbar().items.get(1).getValue());
		module.panel.getTopToolbar().items.get(3).setValue(this.panel.getTopToolbar().items.get(3).getValue());
		//win.add(module.initPanel());
		win.show();
		module.doquery();
	},
	
	
	
	/**
			 * 监听报表load事件,并增加表格事件监听
			 */
			onReady : function() {
				var _ctx = this;
				var iframe = Ext.isIE
						? document.frames[this.frameId]
						: document.getElementById(this.frameId);
				if (iframe.attachEvent) {
					iframe.attachEvent("onload", function() {
								_ctx .simplePrintMask(iframe,
												"AntimicrobialDrugDepartment_MZ_UseInfoList");
							});
				} else {
					iframe.onload = function() {
						_ctx.simplePrintMask(iframe, "AntimicrobialDrugDepartment_MZ_UseInfoList");
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
					var headerRow = 6;// 标题头占的行数,
					var footerRow = 6;// 标题尾占的行数
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
						tr.cells[13].style = "display:none;";//隐藏第13列（ksdm）
					}
					var lastSelectedRow = null;
					// *********选中行颜色变化*********
					function tronmousedown(obj) {
						if (obj != lastSelectedRow) {
							for (var j = 1; j < obj.cells.length - 1; j++) {
								obj.cells[j].style.backgroundColor = '#DFEBF2';
							}
							// obj.style.backgroundColor = '#DFEBF2';
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
							data.push(obj.cells[j].firstChild.firstChild.innerHTML);
						}
						// *****以下可以实现具体业务******
					}
					function ondblclick(obj) {
						// 组装数据,数据为每行单元格中的内容
						var data = [];
						for (var j = 1; j < obj.cells.length - 1; j++) {
							data.push(obj.cells[j].firstChild.firstChild.innerHTML);
						}
						_ctx.doQueryDetil(data[12]);
					}
				}
			}

		

	
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}