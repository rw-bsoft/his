$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.HospitalBedTurnoverPrintView = function(cfg) {
	this.width = 200 
	this.height = 200
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
	this.exContext={};
	phis.prints.script.HospitalBedTurnoverPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.HospitalBedTurnoverPrintView, phis.script.SimpleModule, {
	initPanel : function() {
		this.frameId = "SimplePrint_frame_HospitalBedTurnover";
		this.conditionFormId = "SimplePrint_form_HospitalBedTurnover";
		this.mainFormId = "SimplePrint_mainform_HospitalBedTurnover";
		var panel = new Ext.Panel({
			id : this.mainFormId,
			width : this.width,
			height : this.height,
			title : this.title,
			tbar : this.initConditionFields(),
			html : "<iframe id='"
					+ this.frameId
					+ "' width='100%' height='100%' onload='simplePrintMask(\"HospitalBedTurnover\")'></iframe>"
		})
		this.panel = panel
		return panel
	},
	initConditionFields : function() {
		var tbar = new Ext.Toolbar();
		var dateFrom = new Date();
		dateFrom.setDate(1);
		var simple = new Ext.FormPanel({
			labelWidth : 50, // label settings here cascade
			title : '',
			layout : "table",
			bodyStyle : 'padding:5px 5px 5px 5px',
			defaults : {},
			defaultType : 'textfield',
				items : [{
						xtype : "label",
						forId : "window",
						text : "统计日期 "
					}, new Ext.ux.form.Spinner({
								fieldLabel : '开始时间',
								name : 'dateFrom',
								value : dateFrom.format('Y-m-d'),
								strategy : {
									xtype : "date"
								},
									width : 100
								}), {
							xtype : "label",
							forId : "window",
							text : "至"
						}, new Ext.ux.form.Spinner({
									fieldLabel : '结束时间',
									name : 'dateTo',
									value : new Date()
											.format('Y-m-d'),
									strategy : {
										xtype : "date"
									},
									width : 100
								})]
			
			});
		this.simple = simple;
		tbar.add(simple, this.createButtons());
		return tbar;
	},
	doQuery : function() {
		var dateFrom = this.simple.items.get(1).getValue();
		var dateTo = this.simple.items.get(3).getValue();
		var time = /^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$/
		if(!dateFrom.match(time)){
			this.simple.items.get(1).setValue(new Date().format('Y-m-d'))
			dateFrom = new Date().format('Y-m-d');
		}
		if(!dateTo.match(time)){
			this.simple.items.get(3).setValue(new Date().format('Y-m-d'))
			dateTo = new Date().format('Y-m-d');
		}
		var strFrom = dateFrom.replace(/-/g,"/");
		var strTo = dateTo.replace(/-/g,"/");
		var From = new Date(strFrom);
		var to = new Date(strTo);
		if(From>new Date()){
			this.simple.items.get(1).setValue(new Date().format('Y-m-d'))
			dateFrom = new Date().format('Y-m-d');
		}
		if(to>new Date()){
			this.simple.items.get(3).setValue(new Date().format('Y-m-d'))
			dateTo = new Date().format('Y-m-d');
		}
		if (!dateFrom || !dateTo) {
			Ext.MessageBox.alert("提示", "请输入统计时间");
			return
		}
		if (dateFrom != null && dateTo != null && dateFrom != ""
				&& dateTo != "" && dateFrom > dateTo) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		Ext.getCmp(this.mainFormId).el.mask("正在生成报表...", "x-mask-loading");
		var pages="phis.prints.jrxml.HospitalBedTurnover";
		 var url="resources/"+pages+".print?type=1";
		url += "&dateFrom="+dateFrom+"&dateTo="+dateTo;
		document.getElementById(this.frameId).src = url;
	},
	doPrint : function() {
		if(this.dateFrom && this.dateTo){
			var pages="phis.prints.jrxml.HospitalBedTurnover";
			 var url="resources/"+pages+".print?type=1";
			url += "&dateFrom="+this.dateFrom+"&dateTo="+this.dateTo;
			var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
			rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
			rehtm.lastIndexOf("page-break-after:always;");
			rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			LODOP.PREVIEW();
		}else{
			MyMessageTip.msg("提示", "请先查询后,再打印!", true);
		}
	},
	doHelp : function(){
		Ext.MessageBox.alert("帮助说明", "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp1.实际开放总床日数：指年内医院各科每日夜晚12点开放病床数总和,不论该床是否被病人占用,都应计算在内。包括消毒和小修理等暂停使用的病床,超过半年的加床。不包括因病房扩建或大修而停用的病床及临时增设病床。<br/>"+
"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2.实际占用总床日数：指医院各科每日夜晚12点实际占用病床数(即每日夜晚12点住院人数)总和。包括实际占用的临时加床在内。病人入院后于当晚12点前死亡或因故出院的病人, 作为实际占用床位1天进行统计,同时亦应统计“出院者占用总床日数”1天,入院及出院人数各1人。<br/>"+
"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp3.出院者占用总床日数：指所有出院人数的住院床日之总和。包括正常分娩、未产出院、住院经检查无病出院、未治出院及健康人进行人工流产或绝育手术后正常出院者的住院床日数。<br/>"+
"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.平均开放病床数＝实际开放总床日数／统计期中的天数。<br/>"+
"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp5.病床使用率＝实际占用总床日数／实际开放总床日数X100％。<br/>"+
"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp6.病床周转次数＝出院人数／平均开放床位数。<br/>"+
"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp7.平均病床工作日＝实际占用总床日数／平均开放病床数。<br/>"+
"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp8.出院者平均住院日＝出院者占用总床日数／出院人数。<br/>"+
"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp9.床位工作效率 = 病床工作效率=平均病床工作日×病床周转次数= 统计期内占用床位总数平均开放病床数×出院人数/平均开放病床数=  统计期内占用床位总数×出院人数/平均开放病床数²。");
	}
})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}