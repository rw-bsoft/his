$package("phis.application.reg.script");
$import("phis.script.SimpleList","util.helper.Helper");

phis.application.reg.script.AppointmentManageModule = function(cfg) {
	cfg.autoLoadData = true;
	phis.application.reg.script.AppointmentManageModule.superclass.constructor.apply(
			this, [cfg]);
	this.printurl = util.helper.Helper.getUrl();
};
Ext.extend(phis.application.reg.script.AppointmentManageModule,
		phis.script.SimpleList, {
	initPanel : function(sc) {
		this.tbar=this.initConditionFields();
		var grid = phis.application.reg.script.AppointmentManageModule.superclass.initPanel
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
			this.queryData ={"ds":ds,"de":de};
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
		phis.application.reg.script.AppointmentManageModule.superclass.loadData.call(this);
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
					value : new Date(),
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
//		items.push({
//					xtype : "button",
//					text : "取号",
//					iconCls : "page_save",
//					scope : this,
//					handler : this.onSaveClick
//				});
		items.push({
					xtype : "button",
					text : "自费",
					iconCls : "page_save",
					scope : this,
					handler : this.onZfClick
				});
		items.push({
					xtype : "button",
					text : "转农合",
					iconCls : "page_save",
					scope : this,
					handler : this.onNhClick
				});
		items.push({
					xtype : "button",
					text : "转医保",
					iconCls : "page_save",
					scope : this,
					handler : this.onYbClick
				});
//		items.push({
//					xtype : "button",
//					text : "打印",
//					iconCls : "print",
//					scope : this,
//					handler : this.doPrint
//				});
//		items.push({
//					xtype : "button",
//					text : "导出",
//					iconCls : "excel",
//					scope : this,
//					handler : this.doExport
//				});
		return items
	},
	onDblClick : function(grid, index, e) {
		var r=this.getSelectedRecord();
		if(!r){
			MyMessageTip.msg("提示", "请选择一条记录！！！", true);
			return;
		}
		//获取当前选中行
		var record=this.getSelectedRecord().data;
		if(this.opener.formModule){
			this.opener.formModule.zjyyData = record;
		}
		this.opener.showZjyyModule(1000,"自费");
		this.doCancel();
	},
	doClose : function(){
		this.opener.formModule.zjyyData = null;
//		this.opener.closeCurrentTab();
		var win = this.getWin();
		if(win)
			win.hide();
	},
	doCancel : function(){
		var win = this.getWin();
		if(win)
			win.hide();
	},
	doPrint : function() {
//		var type="1";
//		if(this.type && this.type=="3"){
//			type=this.type;
//		}
//		this.type="1";
//		var pages="phis.prints.jrxml.ReportForAntiMicrobialPurchaseAnalysis";
//		var url="resources/"+pages+".print?type="+type;
//		var param = "&ds="+this.queryData.ds+"&de="+this.queryData.de;
//		if(type=="3"){
//			var printWin = window.open(url+param,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
//			return printWin;
//		}else{
//			var LODOP=getLodop();
//			LODOP.PRINT_INIT("打印控件");
//			LODOP.SET_PRINT_PAGESIZE("0","","","");
//			LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url+param,httpMethod:"get"}));
//			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
//			LODOP.PREVIEW();
//		}
	},
	doExport : function() {
		this.type="3";
		this.doPrint();
	},
	onRenderder : function(value,metaData,r){
		if(r.data.STATUS == "0"){
			return "取消预约";
		}else if(r.data.STATUS == "1"){
			return "<font color='blue'>已预约</font>";
		}else if(r.data.STATUS == "2"){
			return "<font color='red'>已取号</font>";
		}else if(r.data.STATUS == "3"){
			return "<font color='green'>已就诊</font>";
		}
		return value;
	},
	onSaveClick : function(){
		//获取当前选中行
		var record=this.getSelectedRecord().data;
		Ext.Msg.show({
			title : '提示',
			msg : '为病人“'+record.PATIENTNAME+'”取号，不可恢复，是否确定取号？',
			modal : true,
			width : 400,
			buttons : Ext.MessageBox.YESNO,
			multiline : false,
			fn : function(btn, text) {
				if (btn == "yes") {
					var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : this.serviceId,
						serviceAction : this.saveAction,
						body : {sbxh:record.ID, status : 2}
					});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return null;
					}
					this.refresh();
				}
			},
			scope : this
		});
	},
	onZfClick : function() {
		var r=this.getSelectedRecord();
		if(!r){
			MyMessageTip.msg("提示", "请选择一条记录！！！", true);
			return;
		}
		//获取当前选中行
		var record=this.getSelectedRecord().data;
		if(this.opener.formModule){
			this.opener.formModule.zjyyData = record;
		}
		this.opener.showZjyyModule(1000,"自费");
		this.doCancel();
	},
	onNhClick : function(){
		var r=this.getSelectedRecord();
		if(!r){
			MyMessageTip.msg("提示", "请选择一条记录！！！", true);
			return;
		}
		//获取当前选中行
		var record=this.getSelectedRecord().data;
		if(this.opener.formModule){
			this.opener.formModule.zjyyData = record;
		}
		this.opener.formModule.doNhdk();
		this.doCancel();
	},
	onYbClick : function(){
		var r=this.getSelectedRecord();
		if(!r){
			MyMessageTip.msg("提示", "请选择一条记录！！！", true);
			return;
		}
		//获取当前选中行
		var record=this.getSelectedRecord().data;
		if(this.opener.formModule){
			this.opener.formModule.zjyyData = record;
		}
		this.opener.showZjyyModule(5000,"市医保");
//		this.opener.formModule.doYbdk();
		this.doCancel();
	}
});