$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.ClinicOutpatientWorkloadPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.ClinicOutpatientWorkloadPrintView.superclass.constructor.apply(
			this, [cfg])
	this.ib_ks = 1;
	this.ib_ys = 0;
	this.depValue = [];
}
Ext.extend(phis.prints.script.ClinicOutpatientWorkloadPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.ClinicOutpatientWorkload";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.ClinicOutpatientWorkload";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.ClinicOutpatientWorkload";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : this.title,
			tbar : {
				layoutConfig : {
					pack : 'start',
					align : 'middle'
				},
				frame : true,
				enableOverflow : true,
				items : this.getTbar()
			},
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.ClinicOutpatientWorkload\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar : function() {
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
		var tbar = [];
		tbar.push(new Ext.form.Label({
					text : "从:"
				}));
		tbar.push(new Ext.form.DateField({
					id : 'dateFrom',
					name : 'dateFrom',
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
					id : 'dateTo',
					name : 'dateTo',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				})); 
		var boxLabels = ['科室','医生'];
		for(var i = 0 ; i < boxLabels.length ; i ++){
			tbar.push({
				xtype : "radio",
				checked : (i+1) == 1,
				boxLabel : boxLabels[i],
				inputValue : i+1,
				name : "ksys",
				listeners : {
					check : function(group,checked) {
						if(checked){
							if (group.inputValue == 1) {
								this.ib_ks = 1;
								this.ib_ys = 0;
							} else if (group.inputValue == 2) {
								this.ib_ys = 1;
								this.ib_ks = 0;
							}
						}
					},
					scope : this
				}
			});
		}
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
					disabled : false
				})
		return tbar;
	},
	doQuery : function() {
		var dateFrom = Ext.getDom("dateFrom").value;
		var dateTo = Ext.getDom("dateTo").value;
		var reg =/\d{4}-\d{2}-\d{2}/ ;
		 if (!reg.test(dateFrom)) {  
			 Ext.MessageBox.alert("提示", "开始时间格式不正确！");
				return 
		}
		 if (!reg.test(dateTo)) {  
			 Ext.MessageBox.alert("提示", "结束时间格式不正确！");
				return 
		}
		if (dateFrom=="开始时间") {
			Ext.MessageBox.alert("提示", "开始时间不能为空！");
			return
		}
		if (dateTo=="结束时间") {
			Ext.MessageBox.alert("提示", "结束时间不能为空！");
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"ib_ks" : this.ib_ks,
			"ib_ys" : this.ib_ys,
			"dateFrom" : dateFrom + " 00:00:00",
			"dateTo" : dateTo + " 23:00:00"
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
		// var url =
		// "*.print?pages=phis.prints.jrxml.DoctorsAccountingServlet&config="
		// + encodeURI(encodeURI(Ext.encode(printConfig)))
		// + "&silentPrint=1";
		var pages="phis.prints.jrxml.ClinicOutpatientWorkload";
			 var url="resources/"+pages+".print?config="+ encodeURI(encodeURI(Ext.encode(printConfig)))
				+ "&silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		var dateFrom = Ext.getDom("dateFrom").value;
		var dateTo = Ext.getDom("dateTo").value;
		var reg =/\d{4}-\d{2}-\d{2}/ ;
		 if (!reg.test(dateFrom)) {  
			 Ext.MessageBox.alert("提示", "开始时间格式不正确！");
				return 
		}
		 if (!reg.test(dateTo)) {  
			 Ext.MessageBox.alert("提示", "结束时间格式不正确！");
				return 
		}
		if (dateFrom=="开始时间") {
			Ext.MessageBox.alert("提示", "开始时间不能为空！");
			return
		}
		if (dateTo=="结束时间") {
			Ext.MessageBox.alert("提示", "结束时间不能为空！");
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"ib_ks" : this.ib_ks,
			"ib_ys" : this.ib_ys,
			"dateFrom" : dateFrom + " 00:00:00",
			"dateTo" : dateTo + " 23:00:00"
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			page : "whole",
			requestData : body
		}
		// 后台servelt
		// var url =
		// "*.print?pages=phis.prints.jrxml.DoctorsAccountingServlet&landscape=1&silentPrint=1&config="
		// + encodeURI(encodeURI(Ext.encode(printConfig)));
		var pages="phis.prints.jrxml.ClinicOutpatientWorkload";
		 var url="resources/"+pages+".print?landscape=1&silentPrint=1&config="
		 + encodeURI(encodeURI(Ext.encode(printConfig)));
	/*
	 * window .open( url, "", "height=" + (screen.height - 100) + ", width=" +
	 * (screen.width - 10) + ", top=0, left=0, toolbar=no, menubar=yes,
	 * scrollbars=yes, resizable=yes,location=no, status=no")
	 */
		 var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			// 预览LODOP.PREVIEW();
			// 预览LODOP.PRINT();
			// LODOP.PRINT_DESIGN();
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			// 预览
			LODOP.PREVIEW();
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
