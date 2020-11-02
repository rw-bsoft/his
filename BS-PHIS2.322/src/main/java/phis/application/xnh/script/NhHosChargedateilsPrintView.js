$package("phis.application.xnh.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"phis.script.util.DateUtil", "util.dictionary.TreeDicFactory",
		"util.helper.Helper")

phis.application.xnh.script.NhHosChargedateilsPrintView = function(cfg) {
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
	phis.application.xnh.script.NhHosChargedateilsPrintView.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.xnh.script.NhHosChargedateilsPrintView, phis.script.SimpleModule,
		{
			initPanel : function() {
				this.frameId = "SimplePrint_frame_phis.prints.jrxml.NhHosChargedateils";
				this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.NhHosChargedateils";
				this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.NhHosChargedateils";
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
							+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.NhHosChargedateils\")'></iframe>"
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
							handler : this.doPrint,
							disabled : true
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
				var pages = "phis.prints.jrxml.NhHosChargedateils";
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
				
				var pages = "phis.prints.jrxml.NhHosChargedateils";
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
				}
				document.getElementById(this.frameId).src = url
				bars.get(5).setDisabled(false);//打印
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