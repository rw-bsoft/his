$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.NurseRecordView = function(cfg) {
	this.exContext = {};
	this.width = 900
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.NurseRecordView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.NurseRecordView, phis.script.SimpleModule, {
	initPanel : function() {
		if (!this.mainApp.wardId) {
			MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
			return;
		}
		this.frameId = "SimplePrint_frame_phis.prints.jrxml.NurseRecordServlet";
		this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.NurseRecordServlet";
		this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.NurseRecordServlet";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : this.title,
			tbar : this.getTbar()
			,
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.NurseRecordServlet\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	getTbar : function() {
		var dat = new Date().format('Y-m-d');
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
		var tbar = [];
		tbar.push(new Ext.form.Label({
					text : "护理时间 从:"
				}));
		tbar.push(new Ext.form.DateField({
					id : 'dateFromHL',
					name : 'dateFromHL',
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
					id : 'dateToHL',
					name : 'dateToHL',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				}));
	
		tbar.push(new Ext.form.Label({
			text : "住院号:"
		}));
		this.zyhview=new Ext.form.TextField({
            width: 140,
            //allowBlank: false,
            maxLength: 20,
            name: 'zyh'
		})
		tbar.push(this.zyhview);
		tbar.push({
					xtype : "button",
					text : "统计",
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
		var dateFromHL = Ext.getDom("dateFromHL").value;
		var dateToHL = Ext.getDom("dateToHL").value;
		var zyh =0;
		if(this.zyhview.getValue()){
			zyh=this.zyhview.getValue();
		}
		if (!dateFromHL || !dateToHL) {
			return
		}
		if (dateFromHL != null && dateToHL != null && dateFromHL != ""
				&& dateToHL != "" && dateFromHL > dateToHL) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"wardId":this.mainApp.wardId,
			"zyh":zyh,
			"dateFromHL" : dateFromHL + " 00:00:00",
			"dateToHL" : dateToHL + " 23:00:00"
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			//title : "护理记录查询",
			page : "whole",
			requestData : body
		}
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		// 后台servelt
	// var url = "*.print?pages=phis.prints.jrxml.NursesAssessServlet&config="
		// + encodeURI(encodeURI(Ext.encode(printConfig)))
			// + "&silentPrint=1";
		var pages="phis.prints.jrxml.NurseRecordServlet";
		 var url="resources/"+pages+".print?config="
				+ encodeURI(encodeURI(Ext.encode(printConfig)))
				+ "&silentPrint=1";
		document.getElementById(this.frameId).src = url
	},
	doPrint : function() {
		var dateFromHL = Ext.getDom("dateFromHL").value;
		var dateToHL = Ext.getDom("dateToHL").value;
		var zyh =0;
		if(this.zyhview.getValue()){
			zyh=this.zyhview.getValue();
		}
		if (!dateFromHL || !dateToHL) {
			return
		}
		if (dateFromHL != null && dateToHL != null && dateFromHL != ""
				&& dateToHL != "" && dateFromHL > dateToHL) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var body = {
			"wardId":this.mainApp.wardId,
			"dateFromHL" : dateFromHL + " 00:00:00",
			"dateToHL" : dateToHL + " 23:00:00",
			"zyh":zyh
		};
		// 必须配
		// 把要传的参数放到body里去
		var printConfig = {
			//title : "护理记录查询",
			page : "whole",
			requestData : body
		}
		// 后台servelt
		// var url =
		// "*.print?pages=phis.prints.jrxml.NursesAssessServlet&config="
			// + encodeURI(encodeURI(Ext.encode(printConfig)))
			// + "&silentPrint=1";
    	 var pages="phis.prints.jrxml.NurseRecordServlet";
		 var url="resources/"+pages+".print?config="
				+ encodeURI(encodeURI(Ext.encode(printConfig)))
				+ "&silentPrint=1";
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
