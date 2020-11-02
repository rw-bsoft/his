$package("phis.application.sto.script");
$import("phis.script.SimpleList","util.helper.Helper");

phis.application.sto.script.BasicDrugOutAnalysis = function(cfg) {
	cfg.autoLoadData = true;
	phis.application.sto.script.BasicDrugOutAnalysis.superclass.constructor.apply(
			this, [cfg]);
	this.printurl = util.helper.Helper.getUrl();
};
Ext.extend(phis.application.sto.script.BasicDrugOutAnalysis,
		phis.script.SimpleList, {
	initPanel : function(sc) {
		if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
		this.tbar=this.initConditionFields();
		var grid = phis.application.sto.script.BasicDrugOutAnalysis.superclass.initPanel
						.apply(this, sc);
		this.requestData.serviceId=this.serviceId;
		this.requestData.serviceAction=this.serviceAction;
		this.grid = grid;
		return grid;
	},		
	onCountClick : function() {
		delete this.queryData;
		with(this.tbar){
			var ds =this.dateStart.getValue().format('Y-m-d');//开始时间
			var de = this.dateEnd.getValue().format('Y-m-d');//结束时间
			this.queryData ={"yksb":this.mainApp['phis'].storehouseId,"ds":ds,"de":de};
			this.loadData();
		}
	},
	loadData: function() {
		if(!this.queryData){
			return;
		}
		this.requestData.serviceId=this.serviceId;
		this.requestData.serviceAction=this.serviceAction;
		this.requestData.body =  this.queryData;
		phis.application.sto.script.BasicDrugOutAnalysis.superclass.loadData.call(this);
	},
	onStoreLoadData : function(store, records, ops) {
		this.fireEvent("loadData", store); // ** 不管是否有记录，都fire出该事件
		if (records.length == 0) {
			return
		}
		if (!this.selectedIndex || this.selectedIndex >= records.length) {
			this.selectRow(0);
			this.onRowClick();
		} else {
			this.selectRow(this.selectedIndex);
			this.selectedIndex = 0;
			this.onRowClick();
		}
	},
	initConditionFields : function() {
		var items = [];
		var date = new Date().format('Y-m-d');
		var beginDate = date.substring(0, date.lastIndexOf("-"))
				+ "-01";
		items.push(new Ext.form.Label({
				   text:'从：'
				  }));
		var dateStart = new Ext.form.DateField({
					name : 'beginDate',
					value : beginDate,
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				});
		items.push(dateStart);
		this.dateStart=dateStart;
		items.push(new Ext.form.Label({
				   text:'至：'
				  }));
		var dateEnd = new Ext.form.DateField({
					name : 'endDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				});
		items.push(dateEnd);
		this.dateEnd=dateEnd;
		items.push({
					xtype : "button",
					text : "查询",
					iconCls : "query",
					scope : this,
					handler : this.onCountClick
				});
		items.push({
					xtype : "button",
					text : "打印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint
				});
		items.push({
					xtype : "button",
					text : "导出",
					iconCls : "excel",
					scope : this,
					handler : this.doExport
				});
		return items
	},
	/*onDblClick : function(grid, index, e) {
		//获取当前选中行
		var record=this.getSelectedRecord().data;
		var ds =this.dateStart.getValue().format('Y-m-d');//开始时间
		var de = this.dateEnd.getValue().format('Y-m-d');//结束时间
		record["ds"] = ds;
		record["de"] = de;
		if(!this.stDetails){
			var details = this.createModule("antiMicrobialStorehouseOutBoundDetails", this.AntiMicrobialStorehouseOutBoundDetails);
			this.stDetails = details;
			this.stDetails.requestData.body = record;
			var detailsWin = details.getWin();
			detailsWin.add(details.initPanel());
			detailsWin.setSize(1100,600);
			detailsWin.show();
		}else{
			this.stDetails.requestData.body = record;
			this.stDetails.refresh();
			var detailsWin = this.stDetails.getWin();
			detailsWin.setSize(1100,600);
			detailsWin.show();
		}
	},*/
	doClose : function(){
		this.opener.closeCurrentTab();
	},
	doPrint : function() {
		var type="1";
		if(this.type && this.type=="3"){
			type=this.type;
		}
		this.type="1";
		var pages="phis.prints.jrxml.ReportForBasicDrugOutAnalysis";
		var url="resources/"+pages+".print?type="+type;
		var param = "&ds="+this.queryData.ds+"&de="+this.queryData.de;
		if(type=="3"){
			var printWin = window.open(url+param,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
			return printWin;
		}else{
			var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url+param,httpMethod:"get"}));
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			//LODOP.PREVIEW();
			LODOP.PRINT();
		}
	},
	doExport : function() {
		this.type="3";
		this.doPrint();
	}
});