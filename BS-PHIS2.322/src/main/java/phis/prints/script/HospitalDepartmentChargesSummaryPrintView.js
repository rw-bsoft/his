$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup", "util.helper.Helper")

phis.prints.script.HospitalDepartmentChargesSummaryPrintView = function(cfg) {
	this.exContext={};
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
	phis.prints.script.HospitalDepartmentChargesSummaryPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.HospitalDepartmentChargesSummaryPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		if (this.mainApp.wardId == null
				|| this.mainApp.wardId == ""
				|| this.mainApp.wardId == undefined) {
			Ext.Msg.alert("提示", "未设置登录病区,请先设置");
			return null;
		}
		this.frameId = "SimplePrint_frame_HospitalDepartmentChargesSummary";
		this.conditionFormId = "SimplePrint_form_HospitalDepartmentChargesSummary";
		this.mainFormId = "SimplePrint_mainform_HospitalDepartmentChargesSummary";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : this.title,
			tbar :  this.getTbar()
			,
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"HospitalDepartmentChargesSummary\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar : function() {
		var tbar = [];
		tbar.push(new Ext.form.Label({
			text : "从:"
		}));
		// 定义开始时间
		var dat=new Date().format('Y-m-d');
		dat=dat.substring(0,dat.lastIndexOf("-"))+'-01';
		tbar.push(new Ext.form.DateField({
					id : 'dateFrom_KSHSys',
					name : 'dateFromys',
					value : dat,
					width : 90,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				}));
		tbar.push(new Ext.form.Label({
					text : " 到 "
				}));
		// 定义结束时间
		tbar.push(new Ext.form.DateField({
					id : 'dateTo_KSHSys',
					name : 'dateToys',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				}));
//		tbar.push(new Ext.form.Label({
//					// width : 200,
//					text : " 病区: "
//				}));
//		// 科室
//		var BQKSdicys = {
//				"id" : "phis.dictionary.department_bq",
//				"filter" : "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]",
//				"src" : "YF_FYJL_LSCX.FYBQ",
//				"width" : 130,
//				"emptyText" : '全部'
//			};
//		var BQKScomboxys = util.dictionary.SimpleDicFactory
//				.createDic(BQKSdicys);
//		this.BQKScomboxys = BQKScomboxys;
//		tbar.push(BQKScomboxys);
				
		// 统计按钮
		tbar.push({
					xtype : "button",
					text : "统计",
					iconCls : "query",
					scope : this,
					handler : this.doQuery,
					disabled : false
				});
		// 打印按钮
		tbar.push({
					xtype : "button",
					text : "打印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint,
					disabled : false
				});
		tbar.push({
					xtype : "button",
					text : "导出",
					iconCls : "excel",
					scope : this,
					handler : this.doExcel
				})
		return tbar;
	},
	doQuery : function() {
			var qxsb = "bqgl";
			var dateFrom = Ext.getDom("dateFrom_KSHSys").value;
			var dateTo = Ext.getDom("dateTo_KSHSys").value;
			//var bq=this.BQKScomboxys.getValue();
			if (!dateFrom || !dateTo) {
				Ext.MessageBox.alert("提示", "请输入统计时间");
				return
			}
			if (dateFrom != null && dateTo != null && dateFrom != ""
					&& dateTo != "" && dateFrom > dateTo) {
				Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
				return;
			}
			
			Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
			var pages="phis.prints.jrxml.HospitalDepartmentChargesSummary";
			 var url="resources/"+pages+".print?type=1";
			//url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&bq="+bq+"&qxsb="+qxsb;
			url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&qxsb="+qxsb;
			document.getElementById(this.frameId).src = url;
		},
	doPrint : function() {
		var qxsb = "bqgl";
		var dateFrom = Ext.getDom("dateFrom_KSHSys").value;
		var dateTo = Ext.getDom("dateTo_KSHSys").value;
		//var bq=this.BQKScomboxys.getValue();
		if (!dateFrom || !dateTo) {
			Ext.MessageBox.alert("提示", "请输入统计时间");
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		// var url = this.printurl +
		// "*.print?pages=phis.prints.jrxml.HospitalDepartmentChargesSummary";
		var pages="phis.prints.jrxml.HospitalDepartmentChargesSummary";
		 var url="resources/"+pages+".print?type=1";
		//url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&bq="+bq+"&qxsb="+qxsb;
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&qxsb="+qxsb;
		var LODOP=getLodop();
		LODOP.PRINT_INIT("打印控件");
		LODOP.SET_PRINT_PAGESIZE("0","","","");
		//预览LODOP.PREVIEW();
		//预览LODOP.PRINT();
		//LODOP.PRINT_DESIGN();
		LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
		// 预览
		LODOP.PREVIEW();
		
	},
	doExcel : function() {
		var qxsb = "bqgl";
		var dateFrom = Ext.getDom("dateFrom_KSHSys").value;
		var dateTo = Ext.getDom("dateTo_KSHSys").value;
		if (!dateFrom || !dateTo) {
			Ext.MessageBox.alert("提示", "请输入统计时间");
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var pages="phis.prints.jrxml.HospitalDepartmentChargesSummary";
		 var url="resources/"+pages+".print?type=3";
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo+"&qxsb="+qxsb;
		var printWin = window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
		return printWin;		
	}
})
simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}