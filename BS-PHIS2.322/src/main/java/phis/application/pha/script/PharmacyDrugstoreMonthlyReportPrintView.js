$package("phis.application.pha.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper","phis.script.widgets.Spinner", "phis.script.widgets.DatetimeField")

phis.application.pha.script.PharmacyDrugstoreMonthlyReportPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.application.pha.script.PharmacyDrugstoreMonthlyReportPrintView.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyDrugstoreMonthlyReportPrintView,
		app.desktop.Module,{
					initPanel : function() {
				this.frameId = "SimplePrint_frame_drugstoreMonthly";
				this.conditionFormId = "SimplePrint_form_drugstoreMonthly";
				this.mainFormId = "SimplePrint_mainform_drugstoreMonthly";
				//药房初始化建账验证
				var ret =phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryServiceAction
						});
				if (ret.code > 300) {
					Ext.Msg.alert("提示", "该药房还未初始建账!");
						return;	
				}
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
						items : this.getTbar()
					},
					html : "<iframe id='"
							+ this.frameId
							+ "' width='100%' height='100%' onload='simplePrintMask(\"drugstoreMonthly\")'></iframe>"
				})
				this.panel = panel
				return panel
			},
			getTbar : function() { 
				var tbar = [];
				tbar.push(new Ext.form.Label({
							text : "财务月份:"
						}));
				tbar.push(new Ext.ux.form.Spinner({ 
					name : 'dateFrom',
					value : this.getDate(),
					strategy : {
						xtype : "month"
					}
				}));
				this.YPLXStore = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : [{
										'value' : 1,
										'text' : '西药'
									}, {
										'value' : 2,
										'text' : '中药'
									}, {
										'value' : 3,
										'text' : '草药'
									}]
						});
				this.YPLXCombox = new Ext.form.ComboBox({
							name : 'YPLX',
							store : this.YPLXStore,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : "全部",
							selectOnFocus : true,
							forceSelection : true,
							width : 130
						});
				tbar.push(new Ext.form.Label({
							text : "药品类型:"
						}));
				tbar.push(this.YPLXCombox);
				tbar.push({
							xtype : "button",
							text : "查询",
							iconCls : "query",
							scope : this,
							handler : this.doQuery,
							disabled : false
						})
				tbar.push({
							xtype : "button",
							text : "打印",
							iconCls : "print",
							scope : this,
							handler : this.doPrint,
							disabled : true
						})
						
				tbar.push({
							xtype : "button",
							text :  "退出",
							iconCls : "close",
							scope : this,
							handler : this.doClose,
							disabled : false
						  })
				return tbar;
			},

			doQuery : function() { 
                var yplx = 0;//药品类型 全部
                if(this.YPLXCombox.value == 1){//西药
                 	yplx = 1;
                }
                 if(this.YPLXCombox.value == 2){//中药
                 	yplx = 2;
                }
                 if(this.YPLXCombox.value == 3){//草药
                 	yplx = 3;
                }
                this.yplx = yplx;
                var tbarIt = this.panel.getTopToolbar().items;
				var dateFrom = tbarIt.get(1).getValue();
				if (dateFrom != null && dateFrom != "") {
					var begin = dateFrom + "-01";// 页面财务月份起始时间
					var dateSplit = dateFrom.split("-");
					var end = this.getLastDay(dateSplit[0], dateSplit[1]);// 页面财务月份的结束时间
					var prior_begin = this.getLastMonth(dateSplit[0],
							dateSplit[1]);// 界面财务月份前一个月的月初时间
				} 
//				var reg =/^((((1[6-9]|[2-9]\d)\d{2})-(0?[13578]|1[02]))|(((1[6-9]|[2-9]\d)\d{2})-(0?[13456789]|1[012]))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29))$/;
//				if(!reg.test(dateFrom)){
//					Ext.MessageBox.alert("提示", "开始时间日期格式不合法");
//					return;
//				}
				if (!dateFrom ) {
					return;
				}
				Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
						"x-mask-loading");
				//var url = this.printurl + "*.print?pages=phis.prints.jrxml.DrugstoreMonthlyReport&execJs="+ this.getExecJs();  
				var pages="phis.prints.jrxml.DrugstoreMonthlyReport";
				var url="resources/"+pages+".print?execJs="+ this.getExecJs();
				url +="&yplx="+this.yplx+"&dateFrom="+dateFrom+"&end="+end+"&begin="+begin+"&prior_begin="+prior_begin;
				document.getElementById(this.frameId).src = url
				tbarIt.get(5).setDisabled(false);
				
			},
			doPrint : function() {
				var tbarIt = this.panel.getTopToolbar().items;
				var dateFrom = tbarIt.get(1).getValue();
				if (dateFrom != null && dateFrom != "") {
					var begin = dateFrom + "-01";// 页面财务月份起始时间
					var dateSplit = dateFrom.split("-");
					var end = this.getLastDay(dateSplit[0], dateSplit[1]);// 页面财务月份的结束时间
					var prior_begin = this.getLastMonth(dateSplit[0],
							dateSplit[1]);// 界面财务月份前一个月的月初时间
				} 
//				var reg =/^((((1[6-9]|[2-9]\d)\d{2})-(0?[13578]|1[02]))|(((1[6-9]|[2-9]\d)\d{2})-(0?[13456789]|1[012]))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29))$/;
//				if(!reg.test(dateFrom)){
//					Ext.MessageBox.alert("提示", "开始时间日期格式不合法");
//					return;
//				}
				if (!dateFrom)  {
					return
				}
				//var url = this.printurl + "*.print?pages=phis.prints.jrxml.DrugstoreMonthlyReport&execJs="+ this.getExecJs();
				var pages="phis.prints.jrxml.DrugstoreMonthlyReport";
				var url="resources/"+pages+".print?execJs="+ this.getExecJs();
				url += "&yplx="+this.yplx+"&dateFrom="+dateFrom+"&end="+end+"&begin="+begin+"&prior_begin="+prior_begin; 
				
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				//预览LODOP.PREVIEW();
				//预览LODOP.PRINT();
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				LODOP.PREVIEW();// 预览
			},
			doClose : function(){
				this.opener.closeCurrentTab();
			},
			// 获取该月最后一天
			getLastDay : function(year, month) {
				var new_year = year; // 取当前的年份
				var new_month = month;// 取下一个月的第一天，方便计算（最后一天不固定）
				if (new_month >= 12) // 如果当前大于12月，则年份转到下一年
				{
					new_month -= 12; // 月份减
					new_year++; // 年份增
				}
				var newnew_date = new Date(new_year, new_month, 1); // 取当年当月中的第一天
				return year
						+ "-"
						+ month
						+ "-"
						+ (new Date(newnew_date.getTime() - 1000 * 60 * 60 * 24))
								.getDate();// 获取当月最后一天日期
			},
			// 获取界面财务月份前一个月的月初时间
			getLastMonth : function(year, month) {
				var new_year = year; // 取当前的年份
				var new_month = month - 1;// 取上一个月
				if (month == 1) {
					new_month = 12;
					new_year--;
				}
				return new_year + "-" + new_month + "-01";
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('rb');"
			},
			getDate:function(){
			var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "pharmacyManageService",
							serviceAction : "initDateQuery"
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.getDate);
					return;
				}
				return r.json.date;
			}
		}
)
simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}