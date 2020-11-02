$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"phis.script.util.DateUtil", "util.dictionary.TreeDicFactory",
		"util.helper.Helper")

phis.prints.script.CountryChargesPrintView = function(cfg) {
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
	this.jzDate = "";
	phis.prints.script.CountryChargesPrintView.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.prints.script.CountryChargesPrintView, phis.script.SimpleModule,
		{
			initPanel : function() {
				this.frameId = "SimplePrint_frame_phis.prints.jrxml.CountryCharges";
				this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.CountryCharges";
				this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.CountryCharges";
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
							+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.CountryCharges\")'></iframe>"
				})
				this.panel = panel
				return panel
			},
			initConditionFields : function() {
				var items = []
				items.push(new Ext.form.Label({
							text : "时间从"
						}))
				items.push(new Ext.form.DateField({
					name : 'beginDate',
					value : Date.getServerDate(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : ''
				}))
				items.push(new Ext.form.Label({
							text : "到"
						}))
				items.push(new Ext.form.DateField({
							name : 'endDate',
							value : Date.getServerDate(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : ''
						}))
				items.push({
							xtype : "button",
							text : "查询",
							iconCls : "query",
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
				items.push({
							xtype : "button",
							text : "导出",
							iconCls : "excel",
							scope : this,
							handler : this.doExcel
						})
				items.push({
					xtype : "button",
					text : "帮助说明",
					iconCls : "help",
					scope : this,
					handler : this.doHelp
				})			
				return items
			},
			doHelp : function() {
				Ext.MessageBox
						.alert(
								"卫生室收费汇总说明：",
								"1.总费用=合疗补偿+医保账户支付+医保补偿+实收费用+一般诊疗费减免<br>"
								        +"2.总费用=各种项目分类+一般诊疗费<br>"
										+ "3.一般诊疗费减免包含义诊减免、签约减免");
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('rb');"
			},
			onCancelWinShow : function() {
				this.cancelModule.loadDate();
			},
			doPrint : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm();
				var items = form.items
				var dateFrom;
				var dateTo
			for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "beginDate") {
						dateFrom=f.getValue().format("Y-m-d");
					}
					if (f.getName() == "endDate") {
						dateTo=f.getValue().format("Y-m-d");
					}	
			}			
			if (!dateFrom || !dateTo) {
				return
			}
			if (dateFrom != null && dateTo != null && dateFrom != ""
					&& dateTo != "" && dateFrom > dateTo) {
				Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
				return;
			}
			var body = {
				"dateFrom" : dateFrom + " 00:00:00",
				"dateTo" : dateTo + " 23:59:59"
			};
			// 必须配
			// 把要传的参数放到body里去
			var printConfig = {
				page : "whole",
				requestData : body
			}			
			var pages="phis.prints.jrxml.CountryCharges";
			var url="resources/"+pages+".print?config="+ encodeURI(encodeURI(Ext.encode(printConfig)))
					+ "&silentPrint=1";
			var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				// 预览
				LODOP.PREVIEW();
			},
			doExcel : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm();
				var items = form.items
				var dateFrom;
				var dateTo
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "beginDate") {
						dateFrom=f.getValue().format("Y-m-d");
					}
					if (f.getName() == "endDate") {
						dateTo=f.getValue().format("Y-m-d");
					}	
				}			
				if (!dateFrom || !dateTo) {
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
					&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var body = {
					"dateFrom" : dateFrom + " 00:00:00",
					"dateTo" : dateTo + " 23:59:59"
				};
				var printConfig = {
					page : "whole",
					requestData : body
				}			
				var pages="phis.prints.jrxml.CountryCharges";
				var url="resources/"+pages+".print?config="+ encodeURI(encodeURI(Ext.encode(printConfig)))
						+ "&silentPrint=1&type=3";
				var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				return printWin;
			},			
			doClose : function() {
				this.opener.closeCurrentTab();
			},
			unlock : function() {
				if (this.ownerLock) {
					var p = {};
					p.YWXH = '1008';
					p.SDXH = this.mainApp.uid
					this.bclUnlock(p)
				}
			},
		doLoadReport : function() {
			var form = Ext.getCmp(this.conditionFormId).getForm();
			var items = form.items
			var dateFrom;
			var dateTo
			for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "beginDate") {
						dateFrom=f.getValue().format("Y-m-d");
					}
					if (f.getName() == "endDate") {
						dateTo=f.getValue().format("Y-m-d");
					}	
			}			
			if (!dateFrom || !dateTo) {
				return
			}
			if (dateFrom != null && dateTo != null && dateFrom != ""
					&& dateTo != "" && dateFrom > dateTo) {
				Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
				return;
			}
			var body = {
				"dateFrom" : dateFrom + " 00:00:00",
				"dateTo" : dateTo + " 23:59:59"
			};
			// 必须配
			// 把要传的参数放到body里去
			var printConfig = {
				page : "whole",
				requestData : body
			}
			Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
			var pages="phis.prints.jrxml.CountryCharges";
			var url="resources/"+pages+".print?config="+ encodeURI(encodeURI(Ext.encode(printConfig)))
					+ "&silentPrint=1";
			document.getElementById(this.frameId).src = url;
			
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