$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.WarehousingEjSummaryPrintView = function(cfg) {
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.conditions = []
	phis.prints.script.WarehousingEjSummaryPrintView.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.prints.script.WarehousingEjSummaryPrintView, app.desktop.Module,
		{
			initPanel : function() {
				if (this.mainApp.deptId != this.mainApp.topUnitId) {
					if (this.mainApp.treasuryId == null
							|| this.mainApp.treasuryId == ""
							|| this.mainApp.treasuryId == undefined) {
						Ext.Msg.alert("提示", "未设置登录库房,请先设置");
						return null;
					}
					if (this.mainApp.treasuryEjkf == 0) {
						Ext.MessageBox.alert("提示", "该库房不是二级库房!");
						return;
					}
					if (this.mainApp.treasuryCsbz != 1) {
						Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
						return;
					}
				}
				this.frameId = "SimplePrint_frame_warehousingEjSummary";
				this.conditionFormId = "SimplePrint_form_warehousingEjSummary";
				this.mainFormId = "SimplePrint_mainform_warehousingEjSummary";
				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					title : this.name,
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
							+ "' width='100%' height='100%' onload='simplePrintMask(\"warehousingEjSummary\")'></iframe>"
				})
				this.panel = panel
				return panel
			},
			initConditionFields : function() {
				var dat = new Date().format('Y-m-d');
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
						+ "-01";
				var filter = "['eq',['$','item.properties.JGID'],['s','"
						+ this.mainApp.deptId + "']]";
				var items = []
				items.push(new Ext.form.Label({
							text : "会计日期 从:"
						}));
				items.push(new Ext.form.DateField({
							id : 'dateFromREj',
							name : 'dateFromREj',
							value : dateFromValue,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间'
						}));
				items.push(new Ext.form.Label({
							text : " 到 "
						}));
				items.push(new Ext.form.DateField({
							id : 'dateToREj',
							name : 'dateToREj',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						}));
				items.push(new Ext.form.Label({
							text : "库房名称:"
						}));
				items.push(util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.treasury",
							defaultIndex : '0',
							width : 100,
							filter : filter
						}));
				items.push({
							xtype : "button",
							text : "生成报表",
							iconCls : "default",
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
				return items
			},
			doPrint : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var datefrom = this.panel.getTopToolbar().items.get(1)
						.getValue().format('Y-m-d');
				var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
						.format('Y-m-d');
				var kfxh = this.panel.getTopToolbar().items.get(5).getValue();
				var items = form.items
				//var url = this.printurl
				//		+ "*.print?pages=phis.prints.jrxml.WarehousingEjSummary&silentPrint=1";
				var pages="phis.prints.jrxml.WarehousingEjSummary";
				 var url="resources/"+pages+".print?silentPrint=1";
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					var gg=f.getValue()
					if(f.getName()=="dateFromREj" || f.getName()=="dateToREj"){
						 gg=encodeURI(encodeURI(new Date(f.getValue()).format("Y-m-d H:i:s")))
					}
					url += "&" + f.getName() + "=" + gg
				}
				url += "&temp=" + new Date().getTime() + "&kfxh=" + kfxh
						+ "&dateForm=" + datefrom + "&dateTo=" + dateTo
						var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				//预览LODOP.PREVIEW();
				//预览LODOP.PRINT();
				//LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				//预览
				LODOP.PREVIEW();
			},
			doLoadReport : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var datefrom = this.panel.getTopToolbar().items.get(1)
						.getValue().format('Y-m-d');
				var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
						.format('Y-m-d');
				var kfxh = this.panel.getTopToolbar().items.get(5).getValue();
				var items = form.items
				//var url = this.printurl + "*.print?pages=phis.prints.jrxml.WarehousingEjSummary";
				var pages="phis.prints.jrxml.WarehousingEjSummary";
				 var url="resources/"+pages+".print?type=1";
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "type" && f.getValue() == "1") {
						Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
								"x-mask-loading")
					}
				}

				url += "&temp=" + new Date().getTime() + "&kfxh=" + kfxh
						+ "&dateForm=" + datefrom + "&dateTo=" + dateTo
				document.getElementById(this.frameId).src = url
			}
		})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}