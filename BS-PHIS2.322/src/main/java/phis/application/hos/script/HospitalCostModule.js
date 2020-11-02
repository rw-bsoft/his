$package("phis.application.hos.script")
$import("phis.script.SimpleModule","util.dictionary.TreeDicFactory", 
		"util.helper.Helper","phis.script.widgets.DatetimeField","util.rmi.miniJsonRequestSync")
/**
 * 住院一日清单
 */
phis.application.hos.script.HospitalCostModule= function(cfg) {
	phis.application.hos.script.HospitalCostModule.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.hos.script.HospitalCostModule, phis.script.SimpleModule, {
	initPanel:function()
	{
		var me=this;
		var formid=Ext.id();
		this.HospitalCostMxgs='phis.prints.jrxml.HospitalCostMxgs'; 
		this.HospitalCostHzgs='phis.prints.jrxml.HospitalCostHzgs'; 
		this.HospitalCostYzgs='phis.prints.jrxml.HospitalCostYzgs'; 
		var leftPanel=new Ext.Panel({
			title:'病人列表',
			region:'west',
			layout:'fit',
			width:250,
			split:true,
			collapsible:true,
			items:[this.getHospiatlCostPatientList()]
		});	
		var centerPanel=new Ext.TabPanel({
			id:formid,
			region:'center',
			activeTab: 0,
		    items: [{
		        title: '明细格式',
				html : "<iframe id='"
					+ this.HospitalCostMxgs
					+ "' width='100%' height='100%' onload='simplePrintMask(\""+formid+"\")'></iframe>"
		    },{
		        title: '汇总格式',
				html : "<iframe id='"
					+ this.HospitalCostHzgs
					+ "' width='100%' height='100%' onload='simplePrintMask(\""+formid+"\")'></iframe>"
		    },{
		        title: '医嘱格式',
				html : "<iframe id='"
					+ this.HospitalCostYzgs
					+ "' width='100%' height='100%' onload='simplePrintMask(\""+formid+"\")'></iframe>"
		    }
		    ],
		    listeners:{
		    	tabchange:function(t,tab)
		    	{
//		    		console.log(me.centerPanel.ZYH);
//		    		console.log(me.ZYH);
//		    		if(me.centerPanel.ZYH!=me.ZYH)
		    			if(tab.ZYH!=me.ZYH)
		    				{me.doReportQyery();}
		    			
		    	}
		    	}
		});
		this.centerPanel=centerPanel;
		var panel = new Ext.Panel({
			layout:'border',
			tbar : {
				layoutConfig : {
					pack : 'start',
					align : 'middle'
				},
				frame : true,
				enableOverflow : true,
				items : this.getTbar()
			},
			items:[leftPanel,centerPanel]
		});
		this.panel = panel;
		return panel;
	},
	getTbar : function() {
		var me=this;
		var tbar = [];
		tbar.push(new Ext.form.Label({
			text : " 费用日期:"
		}));
		// 定义开始时间
		var nowDate=new Date();
		var startDateValue=new Date(nowDate.getFullYear(),nowDate.getMonth(),nowDate.getDate());
		var startDate=new phis.script.widgets.DateTimeField({
			name : 'startDate',
			width : 145,
			allowBlank : false,
			altFormats : 'Y-m-d',
			format : 'Y-m-d',
			value:startDateValue,
			emptyText : '开始时间'
		});
		tbar.push(startDate);
		this.startDate=startDate;
		tbar.push(new Ext.form.Label({
					text : " 至 "
				}));
		// 定义结束时间
		var endDate=new phis.script.widgets.DateTimeField({
			name : 'dateTo',
			value : nowDate,
			width : 145,
			allowBlank : false,
			altFormats : 'Y-m-d',
			format : 'Y-m-d',
			emptyText : '结束时间'
		});
		tbar.push(endDate);
		this.endDate=endDate;
		tbar.push(new Ext.form.Label({
					// width : 200,
					text : " 病人科室: "
				}));
		
		var BRKSdic = {
				"id" : "phis.dictionary.department_zy",
				"filter" : "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]",
				"src" : "SYS_Office.ID",
				"width" : 130
			};
			var BRKScombox = util.dictionary.SimpleDicFactory
					.createDic(BRKSdic)
			BRKScombox.name = 'BRKS';
//			BRKScombox.fieldLabel = '病人科室';
//			BRKScombox.emptyText = "病人科室"
			this.BRKScombox=BRKScombox;
			tbar.push(BRKScombox);
		
		//住院号吗
		tbar.push(new Ext.form.Label({
			text : "住院号码: "
		}));
		var zyhmfield=new Ext.form.TextField({
			name:'zyh',
			width:'120',
            listeners: {
                specialkey: function(field, e){
                    if (e.getKey() == e.ENTER) {
                    	me.doQuery();
                    	
                    }
                    }
                }
		});
		this.zyhmfield=zyhmfield;
		tbar.push(zyhmfield);
		// 统计按钮
		tbar.push({
					xtype : "button",
					text : "查询",
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
		return tbar;
		
	},
	doQuery:function()
	{
//		console.log(this.refList);
//		console.log(this.depcyofice.getValue());
//		console.log(this.zyhfield.getValue());
		var brks=this.BRKScombox.getValue();
		var zyhm=this.zyhmfield.getValue();
		if(zyhm&&zyhm.length<10){
			var zerocount=10-zyhm.length;
			for(var i=0;i<zerocount;i++)
				{
					zyhm='0'+zyhm;
				}
		}
		var cnd = this.refList.initCnd||['and',['eq',['i', 1], ['i',1]]];
		if(brks)
			cnd.push(['eq',['$', 'a.BRKS'], ['s',brks]]);
		if(zyhm)
		    cnd.push(['eq',['$', 'a.ZYHM'], ['s',zyhm]]);
		this.refList.requestData.cnd=cnd;
		this.refList.grid.getSelectionModel().clearSelections();
		this.refList.refresh();
		this.ZYH=null;
	},
	getHospiatlCostPatientList : function() {
		var module = this.createModule("refList",
				this.refList);
		this.refList = module;
		module.opener = this;
		var patientList=module.initPanel();
		//this.patientList=patientList;
		patientList.on('rowclick',this.selectEvent,this);
		return patientList;
	},
	selectEvent:function(t,r,e)
	{
		var record=t.getStore().getAt(r);
		var ZYH=record.data.ZYH;
//		console.log(this.refList.getSelectedRecords());
		var records=this.refList.getSelectedRecords();
		if(ZYH!=this.ZYH&&records&&records.length==1)
			{
			this.selectRecord=records[0].data;
			this.ZYH=ZYH;
			this.doReportQyery();
			}
		else{
			this.selectRecord=null;
		}
	},
	doReportQyery:function()
	{
		if(!this.selectRecord){return;}
		this.centerPanel.el.mask("正在生成报表...","x-mask-loading");
		var pages=this.HospitalCostMxgs;
		if(this.centerPanel.activeTab.title=='汇总格式')
		{
			pages=this.HospitalCostHzgs;
		}
		if(this.centerPanel.activeTab.title=='医嘱格式')
		{
			pages=this.HospitalCostYzgs	
		}
		var startDate=this.startDate.getValue();
		var endDate=this.endDate.getValue();
		var param={
				record:this.selectRecord,
				startDate:startDate,
				endDate:endDate
			};
//		console.log(this.selectRecord);
		var url="resources/"+pages+".print?param="
			+ encodeURI(encodeURI(Ext.encode(param)))
			+ "&silentPrint=1";
		document.getElementById(pages).src = url;
		this.centerPanel.activeTab.ZYH=this.ZYH;
		
	},
	doPrint:function()
	{
		var records=this.refList.getSelectedRecords();
		var ZYHS=[];
		if(records&&records.length==0){Ext.MessageBox.alert("提示","请选择需要打印的病人");return;}
		for(var i=0;i<records.length;i++){
				ZYHS.push(records[i].data);
			}
		var pages=this.HospitalCostMxgs;
		if(this.centerPanel.activeTab.title=='汇总格式')
		{
			pages=this.HospitalCostHzgs;
		}
		if(this.centerPanel.activeTab.title=='医嘱格式')
		{
			pages=this.HospitalCostYzgs	
		}
		var startDate=this.startDate.getValue();
		var endDate=this.endDate.getValue();
		
		
	   var LODOP=getLodop();		
	   for(var j=0;j<ZYHS.length;j++)
	   {
		var param={
				record:ZYHS[j],
				ZYH:ZYHS[j].ZYH,
				startDate:startDate,
				endDate:endDate
			};
		
		//判断是否有费用，没有费用就不打印，有费用就打印
		var res = util.rmi.miniJsonRequestSync({
					serviceId : "phis.hospitalCostProcessingService",
					serviceAction : "QueryCostMx",
					body : param
				});
		if (res.code > 200) {
			Ext.MessageBox.alert("提示","费用查询失败...");
		}
		if(res.json.FYMX<1)
			continue;
		var url="resources/"+pages+".print?param="
			+ encodeURI(encodeURI(Ext.encode(param)))
			+ "&silentPrint=1";
		
			LODOP.PRINT_INIT("打印控件"+j);
			LODOP.SET_PRINT_PAGESIZE("0","","","");
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			// 预览LODOP.PREVIEW();
			// LODOP.PRINT_DESIGN();
			LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
			// 预览
			//LODOP.PREVIEW();
			LODOP.PRINT();
		
//			// 预览LODOP.PREVIEW();
//			// LODOP.PRINT_DESIGN();			
//			var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
//			rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
//			rehtm.lastIndexOf("page-break-after:always;");
//			rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
//			LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
//			LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
//			
//			//LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
//			// 预览
//			LODOP.PREVIEW();
//			//LODOP.PRINT();
	   }
		
	},
	DaysAfter : function(sDate1, sDate2) {
		var oDate1, oDate2, iDays
		var navigatorName = "Microsoft Internet Explorer";
		if (navigator.appName == navigatorName) {
			oDate1 = new Date(Date.parse(sDate1.substring(0, 10)
					.replace(/-/, "/")))
		} else {
			oDate1 = new Date(sDate1.substring(0, 10))
		}
		if (sDate2) {
			if (navigator.appName == navigatorName) {
				oDate2 = new Date(Date.parse(sDate2.substring(0, 10)
						.replace(/-/, "/")));
			} else {
				oDate2 = new Date(sDate2.substring(0, 10))
			}
		} else {
			oDate2 = new Date();
		}
		iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60
				/ 24)
		this.body.ZYTS = iDays;

		return iDays
	}

})
simplePrintMask = function(printId) {
	Ext.getCmp(printId).el.unmask();
}
