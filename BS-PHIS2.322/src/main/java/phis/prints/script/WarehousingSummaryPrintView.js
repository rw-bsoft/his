$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.WarehousingSummaryPrintView = function(cfg) {
	this.exContext = {};
	this.width = 900
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.WarehousingSummaryPrintView.superclass.constructor.apply(
			this, [cfg])
	this.zblb = 1;
	this.wzzd = 0;
	this.lzfs = 0;
	this.depValue = [];
}
Ext.extend(phis.prints.script.WarehousingSummaryPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		if (this.mainApp.deptId != this.mainApp.topUnitId) {
			if (this.mainApp.treasuryId == null
					|| this.mainApp.treasuryId == ""
					|| this.mainApp.treasuryId == undefined) {
				Ext.Msg.alert("提示", "未设置登录库房,请先设置");
				return null;
			}
			if (this.mainApp.treasuryEjkf != 0) {
				Ext.MessageBox.alert("提示", "该库房不是一级库房!");
				return;
			}
			if (this.mainApp.treasuryCsbz != 1) {
				Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
				return;
			}
		}
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.WarehousingSummaryServlet";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.WarehousingSummaryServlet";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.WarehousingSummaryServlet";
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
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.WarehousingSummaryServlet\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar : function() {
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
		var tbar = [];
		tbar.push(new Ext.form.Label({
					text : "会计日期 从:"
				}));
		tbar.push(new Ext.form.DateField({
					id : 'dateFromR',
					name : 'dateFromR',
					value : dateFromValue,
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				}));
		tbar.push(new Ext.form.Label({
					text : " 到 "
				}));
		tbar.push(new Ext.form.DateField({
					id : 'dateToR',
					name : 'dateToR',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				}));
		tbar.push(new Ext.form.Label({
			text : "账簿核算类别:"
		}));
		var dep = this.createDicField({
					"src" : "",
					"defaultIndex" : "0",
					"width" : 200,
					"id" : "phis.dictionary.AccountingCategory_tree",
					// "sliceType" : 1,
					"render" : "Tree",
					"parentKey" : this.navParentKey || {},
					filter:"['in',['$','item.properties.ZBLB'],["+ this.mainApp.treasuryKfzb + "]]"
				});
		dep.tree.on("click", this.onCatalogChanage, this);
		dep.tree.expandAll()// 展开树
		tbar.push(dep);
		tbar.push(new Ext.form.RadioGroup({
					height : 20,
					width : 240,
					name : 'typesr', // 后台返回的JSON格式，直接赋值
					value : "1",
					items : [{
								boxLabel : '按供货单位',
								name : 'typesr',
								inputValue : 1
							}, {
								boxLabel : '按物资',
								name : 'typesr',
								inputValue : 2
							}, {
								boxLabel : '按流转方式',
								name : 'typesr',
								inputValue : 3
							}],
					listeners : {
						change : function(group, newValue, oldValue) {
							if (newValue.inputValue == 1) {
								this.zblb = 1;
								this.wzzd = 0;
								this.lzfs = 0;
							} else if (newValue.inputValue == 2) {
								this.zblb = 0;
								this.wzzd = 1;
								this.lzfs = 0;
							} else if (newValue.inputValue == 3) {
								this.zblb = 0;
								this.wzzd = 0;
								this.lzfs = 1;
							}
						},
						scope : this
					}
				}));
		tbar.push({
					xtype : "button",
					text : "刷新",
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
					disabled : false
				})
		return tbar;
	},
	findChildNodes : function(node, value) {
		var childnodes = node.childNodes;
		for (var i = 0; i < childnodes.length; i++) {
			var nd = childnodes[i];
			value.push(nd.id);
			if (nd.hasChildNodes()) {
				this.findChildNodes(nd, value);
			}
		}
	},
	onCatalogChanage : function(node, e) {
		this.depValue = [];
		this.hslbValue = [];
		var value = [];
		var values = [];
		value.push(node.id);
		this.depValue = value;
		values.push(node.id);
		if (node.hasChildNodes()) {// 有子节点
			// 得到所有子节点
			this.findChildNodes(node, values);
		}
		this.hslbValue = values;
	},
	doQuery : function() {
		if (this.depValue.length == 0) {
			Ext.MessageBox.alert("提示", "请选择账簿核算类别!");
			return;
		}
		if (this.panel.getTopToolbar().items.get(5).value == ""
				|| this.panel.getTopToolbar().items.get(5).value == undefined) {
			this.depValue = [];
		}
		var dateFrom = Ext.getDom("dateFromR").value;
		var dateTo = Ext.getDom("dateToR").value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"zblb" : this.zblb,
			"wzzd" : this.wzzd,
			"lzfs" : this.lzfs,
			"lbbm" : this.depValue,
			"hslb" : this.hslbValue,
			"dateFrom" : dateFrom + " 00:00:00",
			"dateTo" : dateTo + " 23:00:00"
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			//title : "入库汇总报表",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
		//var url = "*.print?pages=phis.prints.jrxml.WarehousingSummaryServlet&config="
		//		+ encodeURI(encodeURI(Ext.encode(printConfig)))
		//		+ "&silentPrint=1";
		var pages="phis.prints.jrxml.WarehousingSummaryServlet";
		 var url="resources/"+pages+".print?config="
				+ encodeURI(encodeURI(Ext.encode(printConfig)))
				+ "&silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		if (this.depValue.length == 0) {
			Ext.MessageBox.alert("提示", "请选择账簿核算类别");
			return;
		}
		if (this.panel.getTopToolbar().items.get(5).value == ""
				|| this.panel.getTopToolbar().items.get(5).value == undefined) {
			this.depValue = [];
		}
		var dateFrom = Ext.getDom("dateFromR").value;
		var dateTo = Ext.getDom("dateToR").value;
		if (!dateFrom || !dateTo) {
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"zblb" : this.zblb,
			"wzzd" : this.wzzd,
			"lzfs" : this.lzfs,
			"lbbm" : this.depValue,
			"hslb" : this.hslbValue,
			"dateFrom" : dateFrom + " 00:00:00",
			"dateTo" : dateTo + " 23:00:00"
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			//title : "入库汇总报表",
			page : "whole",
			requestData : body
		}
		var pages="phis.prints.jrxml.WarehousingSummaryServlet";
		 var url="resources/"+pages+".print?config="
				+ encodeURI(encodeURI(Ext.encode(printConfig)))
				+ "&silentPrint=1";
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
	createDicField : function(dic) {
		// dic.filter = "['in',['$','ZBLB'],["+ this.mainApp.treasuryKfzb + "]]"
		var cls = "util.dictionary.";
		if (!dic.render) {
			cls += "Simple";
		} else {
			cls += dic.render
		}
		cls += "DicFactory"
		$import(cls)
		var factory = eval("(" + cls + ")")
		var field = factory.createDic(dic)
		return field
	},
	doAction : function(item, e) {
		var cmd = item.cmd
		var ref = item.ref

		if (ref) {
			this.loadRemote(ref, item)
			return;
		}
		var script = item.script
		if (cmd == "create") {
			if (!script) {
				script = this.createCls
			}
			this.loadModule(script, this.entryName, item)
			return
		}
		if (cmd == "update" || cmd == "read") {
			var r = this.getSelectedRecord()
			if (r == null) {
				return
			}
			if (!script) {
				script = this.updateCls
			}
			this.loadModule(script, this.entryName, item, r)
			return
		}
		cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
		if (script) {
			$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
		} else {
			var action = this["do" + cmd]
			if (action) {
				action.apply(this, [item, e])
			}
		}
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
