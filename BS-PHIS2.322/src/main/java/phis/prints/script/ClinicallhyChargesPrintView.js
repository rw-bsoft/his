$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"phis.script.util.DateUtil", "util.dictionary.TreeDicFactory",
		"util.helper.Helper")

phis.prints.script.ClinicallhyChargesPrintView = function(cfg) {
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
	phis.prints.script.ClinicallhyChargesPrintView.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.prints.script.ClinicallhyChargesPrintView, phis.script.SimpleModule,
		{
			initPanel : function() {
				this.frameId = "SimplePrint_frame_phis.prints.jrxml.clinicallhyCharges";
				this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.clinicallhyCharges";
				this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.clinicallhyCharges";
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
							+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.clinicallhyCharges\")'></iframe>"
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
				items.push(new Ext.form.ComboBox({
							name : 'xmlx',
		            		valueField : "value",
							displayField : "text",
		            		store : new Ext.data.ArrayStore({
		                        fields : ['value', 'text'],
		                        data : [[0, '全部'], [4, '检验'], [5, '检查'], [6, '手术'], [7, '治疗'], [8, '护理'], [9, '饮食'], [10, '材料'], [99, '其他']]
		                    }),
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 80,
							value : 0
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
							handler : this.doPrint,
							disabled : true
						})
				items.push({
							xtype : "button",
							text : "导出",
							iconCls : "excel",
							scope : this,
							handler : this.doExcel
						})
				return items
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('rb');"
			},
			onCancelWinShow : function() {
				this.cancelModule.loadDate();
			},
			doPrint : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var pages = "phis.prints.jrxml.clinicallhyCharges";
				var url = "resources/" + pages + ".print?type=" + 1
						+ "&execJs=" + this.getExecJs();
				var items = form.items
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "type"&& f.getValue() == "1") {
						Ext.getCmp(this.mainFormId).el.mask(
								"正在生成报表...", "x-mask-loading")
						}
						if (f.getName() == "beginDate"
								|| f.getName() == "endDate") {
							url += "&" + f.getName() + "="
									+ f.getValue().format("Y-m-d")
						}
						if (f.getName() == "xmlx"){
							url += "&" + f.getName() + "="+ f.getValue()
						}
				}		
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
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var pages = "phis.prints.jrxml.clinicallhyCharges";
				var url = "resources/" + pages + ".print?type=" + 3
						+ "&execJs=" + this.getExecJs();
				var items = form.items
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "type"&& f.getValue() == "1") {
						Ext.getCmp(this.mainFormId).el.mask(
								"正在生成报表...", "x-mask-loading")
						}
						if (f.getName() == "beginDate"
								|| f.getName() == "endDate") {
							url += "&" + f.getName() + "="
									+ f.getValue().format("Y-m-d")
						}
						if (f.getName() == "xmlx"){
							url += "&" + f.getName() + "="+ f.getValue()
						}
				}		
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
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var items1 = form.items;
				// 查询最后一次汇总时间
				var bars = this.panel.getTopToolbar().items;
				
				var pages = "phis.prints.jrxml.clinicallhyCharges";
				var url = "resources/" + pages + ".print?type=" + 1;
				var items = form.items
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "type"&& f.getValue() == "1") {
						Ext.getCmp(this.mainFormId).el.mask(
								"正在生成报表...", "x-mask-loading")
						}
						if (f.getName() == "beginDate"
								|| f.getName() == "endDate") {
							url += "&" + f.getName() + "="
									+ f.getValue().format("Y-m-d")
						}
						if (f.getName() == "xmlx"){//add by lizhi 2017-10-13
							url += "&" + f.getName() + "="+ f.getValue()
						}
				}
				document.getElementById(this.frameId).src = url
				bars.get(6).setDisabled(false);//打印
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