$package("phis.application.sto.script");
$import("phis.script.SimpleModule","util.helper.Helper");

phis.application.sto.script.StorehousePurchaseHistoryModule = function(cfg) {
	this.exContext = {};
	phis.application.sto.script.StorehousePurchaseHistoryModule.superclass.constructor.apply(
			this, [cfg]);
	this.printurl = util.helper.Helper.getUrl();
};
Ext.extend(phis.application.sto.script.StorehousePurchaseHistoryModule,
		phis.script.SimpleModule, {
	initPanel : function() {
		if(this.panel){
			return this.panel;
		}
		var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					buttonAlign : 'center',
					items : [{
								layout : "fit",
								border : false,
								split : true,
								region : 'center',
								items : this.getTabs()
							}],
					tbar : this.getTbar()
				})
		this.panel = panel;
		return this.panel;
	},
	/**
	 * 创建对照Tab模块
	 * @return {}
	 */
	getTabs : function(){
		var module = this.createModule("SPHTab", this.refSPHTab);
		if (module) {
			module.opener = this;
			return module.initPanel();
		}
	},
	/**
	 * Tab页切换后触发的事件
	 * 		药品 ： drugTab   供应商  ： supplierTab
	 * @param {} tabPanel
	 * @param {} newTab
	 * @param {} curTab
	 */
	beforetabchange : function(tabPanel, newTab, curTab){
		this.activeTab = newTab.id;//记录当前激活的TabID
	},
	getTbar : function() {
		var tbar = new Ext.Toolbar();
		this.YPLXStore = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : [{
										'value' : 0,
										'text' : '全部'
									}, 
									{
										'value' : 1,
										'text' : '西药'
									}, {
										'value' : 2,
										'text' : '中成药'
									}, {
										'value' : 3,
										'text' : '中草药'
									}]
						});
		var simple = new Ext.FormPanel({
					labelWidth : 50, // label settings here cascade
					title : '',
					layout : "table",
					bodyStyle : 'padding:5px 5px 5px 5px',
					defaults : {},
					defaultType : 'textfield',
					items : [{
								xtype : "label",
								forId : "window",
								text : "查询时间 "
							}, new Ext.ux.form.Spinner({
										fieldLabel : '查询时间开始',
										name : 'dateFrom',
										value : new Date()
												.format('Y-m-d'),
										strategy : {
											xtype : "date"
										},
										width : 150
									}), {
								xtype : "label",
								forId : "window",
								text : "-"
							}, new Ext.ux.form.Spinner({
										fieldLabel : '查询时间结束',
										name : 'dateTo',
										value : new Date()
												.format('Y-m-d'),
										strategy : {
											xtype : "date"
										},
										width : 150
									}),
								{
								xtype : "label",
								forId : "yplx",
								text : "药品类别 "									
								},this.YPLXComboBox = new Ext.form.ComboBox({
								name : 'yplx',
								store : this.YPLXStore,
								valueField : "value",
								displayField : "text",
								mode : 'local',
								triggerAction : 'all',
								emptyText : "",
								selectOnFocus : true,
								forceSelection : true,
								width : 100
								}),
							 {
								xtype : "label",
								forId : "window",
								text : "拼音代码 "
							}, this.ypmcField = new Ext.form.TextField({
									 id:"pydm"
							})]
				});
		this.ypmcField.on("specialkey", this.onQueryFieldEnter, this);//双击刷新
		this.simple = simple;
		tbar.add(simple, this.createButtons());
		return tbar;
	},
	onQueryFieldEnter : function(f, e) {
			if (e.getKey() == e.ENTER) {			
				this.doRefresh();
			}
		},
	/**
	 * 刷新按钮
	 */
	doRefresh : function(){
		var datefrom = this.simple.items.get(1).getValue();//开始时间
		var dateTo = this.simple.items.get(3).getValue();//结束时间
		var yplx = this.simple.items.get(5).getValue();//药品类别
		var pydm = this.simple.items.get(7).getValue();//拼音代码
		var parameter = {};
		parameter.KSSJ = datefrom;
		parameter.JSSJ = dateTo;
		parameter.YPLX = yplx;
		parameter.PYDM = pydm.toUpperCase();
		var tab = this.midiModules["SPHTab"];
		tab.midiModules[this.activeTab].refRightList.clear();//刷新时先将右边列表清空
		tab.midiModules[this.activeTab].refLeftList.requestData.body = parameter;
		tab.midiModules[this.activeTab].refLeftList.refresh();
	},
	/**
	 * 关闭按钮
	 */
	doClose : function(){
		var win = this.getWin();
		if (win)
			win.close();
	},
	doPrint1 : function(){
		var datefrom = this.simple.items.get(1).getValue();//开始时间
		var dateTo = this.simple.items.get(3).getValue();//结束时间
		var pydm = this.simple.items.get(5).getValue();//拼音代码
		var tab = this.midiModules["SPHTab"];
		var tabId = tab.midiModules[this.activeTab].id;
		//tab.midiModules[this.activeTab].refRightList
		var url = "";
		if(tabId=='DST3501'){
			var pages="phis.prints.jrxml.StorehousePurchaseHistoryDrugRecord";
			 url="resources/"+pages+".print?silentPrint=0&execJs="
				+ this.getExecJs();
			//url ="null.print?pages=StorehousePurchaseHistoryDrugRecord&silentPrint=0&execJs="
				//+ this.getExecJs();
		} else if(tabId=='DST3502'){
			var pages="phis.prints.jrxml.StorehousePurchaseHistorySupplierRecord";
			  url="resources/"+pages+".print?silentPrint=0&execJs="
				+ this.getExecJs();
			//url ="null.print?pages=StorehousePurchaseHistorySupplierRecord&silentPrint=0&execJs="
			//	+ this.getExecJs();
		}
		url += "&KSSJ="+datefrom+"&JSSJ="+dateTo+"&PYDM="+pydm.toUpperCase();
		
		var printWin = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		printWin.onafterprint = function() {
			printWin.close();
		};
	},
	doPrint2 : function(){
		var datefrom = this.simple.items.get(1).getValue();//开始时间
		var dateTo = this.simple.items.get(3).getValue();//结束时间
		//var pydm = this.simple.items.get(5).getValue();//拼音代码
		var tab = this.midiModules["SPHTab"];
		var tabId = tab.midiModules[this.activeTab].id;
		//tab.midiModules[this.activeTab].refRightList
		var url = "";
		if(tabId=='DST3501'){
			var pages="phis.prints.jrxml.StorehousePurchaseHistoryDrugDetails";
			   url="resources/"+pages+".print?silentPrint=0&execJs="
				+ this.getExecJs();
		//	url ="null.print?pages=StorehousePurchaseHistoryDrugDetails&silentPrint=0&execJs="
			//	+ this.getExecJs();
			url += "&YPXH="+this.ddd.YPXH+"&YPCD="+this.ddd.YPCD;
		} else if(tabId=='DST3502'){
			var pages="phis.prints.jrxml.StorehousePurchaseHistorySupplierDetails";
			 url="resources/"+pages+".print?silentPrint=0&execJs="
				+ this.getExecJs();
		//	url ="null.print?pages=StorehousePurchaseHistorySupplierDetails&silentPrint=0&execJs="
			//	+ this.getExecJs();
			url += "&DWXH="+this.ddd.DWXH;
		}
		url += "&KSSJ="+datefrom+"&JSSJ="+dateTo;
		
		var printWin = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		printWin.onafterprint = function() {
			printWin.close();
		};
	},
	getExecJs : function() {
		return "jsPrintSetup.setPrinter('A4');"
	},
	setValue : function(data) {
		this.ddd = data;
	},
	doPrint : function() {
		var dateFrom = this.simple.items.get(1).getValue();
		var dateTo = this.simple.items.get(3).getValue();
		var yplx = this.simple.items.get(5).getValue();//药品类别
		var pydm = this.simple.items.get(7).getValue();//拼音代码
		if (!dateFrom || !dateTo) {
			Ext.MessageBox.alert("提示", "请输入统计时间");
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var type="1";
		if(this.type && this.type=="3"){
			type=this.type;
		}
		this.type="1";
		var pages="phis.prints.jrxml.StorehousePurchaseHistory";
		 var url="resources/"+pages+".print?type="+type;
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&YPLX="+yplx+"&PYDM="+pydm;
		if(type=="3"){
			var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
			return printWin;
		}else{
			var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
			rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
			rehtm.lastIndexOf("page-break-after:always;");
			rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			LODOP.PREVIEW();
		}
	},
	doExport : function() {
		this.type="3";
		this.doPrint();
	}
});