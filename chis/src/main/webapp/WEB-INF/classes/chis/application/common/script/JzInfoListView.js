$package("chis.application.common.script")
$import("chis.script.BizSimpleListView")
chis.application.common.script.JzInfoListView = function(cfg) {
	cfg.autoLoadSchema=false;
	var IDCARD=cfg.exContext.empiData.idCard==undefined?null:cfg.exContext.empiData.idCard;
	cfg.initCnd=['and',['eq',['$','IDCARD'],['s',IDCARD]]];
	chis.application.common.script.JzInfoListView.superclass.constructor.apply(this, [cfg])
	this.requestData.serviceId='phis.simpleQuery';
}
Ext.extend(chis.application.common.script.JzInfoListView,chis.script.BizSimpleListView, {
	loadData : function() {
		chis.application.common.script.JzInfoListView.superclass.loadData
				.call(this);
	},
	getCndBar : function(items) {
		var me=this;
		var startDate=new Ext.form.DateField({
			margins:{left:20},
			name : 'startDate',
			width : 90,
			//allowBlank : false,
			altFormats : 'Y-m-d',
			format : 'Y-m-d',
			emptyText : '开始时间'
		});
		me.startDate=startDate;
		// 定义结束时间
		var endDate=new Ext.form.DateField({
			value:new Date(),
			name : 'endDate',
			width : 90,
			//allowBlank : false,
			altFormats : 'Y-m-d',
			format : 'Y-m-d',
			emptyText : '结束时间'
		});
		me.endDate=endDate;
		var queryBtn = new Ext.Toolbar.Button({
					iconCls : "query",
					text : "查询",
					handler : me.doJzQuery,
					scope:me
				})
		var importBtn = new Ext.Toolbar.Button({
			iconCls : "add",
			text : "导入",
			handler : me.doImportJzsz,
			scope:me
		})
		me.queryBtn = queryBtn;
		me.importBtn = importBtn;
//		queryBtn.on("click", me.doJzQuery, me);
//		importBtn.on("click", me.doImportJzsz, me);
		return ['就诊时间：', startDate,' 至 ',endDate,queryBtn,importBtn,'-']
	
	},
	doJzQuery:function()
	{
		var me=this;
		var startDate=me.startDate.getRawValue();
		var endDate=me.endDate.getRawValue();
		//var brid=me.exContext.ids.brid==undefined?null:me.exContext.ids.brid;
		var IDCARD=me.exContext.empiData.idCard==undefined?null:me.exContext.empiData.idCard;
		me.requestData.cnd=['and',
		                              ['eq',['$','IDCARD'],['s',IDCARD]],
									  ['le',['$',"KSSJ"],
									   		['todate',['s',endDate+' 23:59:59'],['s','yyyy-mm-dd hh24:mi:ss']]
									  ]
										];
		if(startDate)
			me.requestData.cnd.push(['ge',['$',"KSSJ"],['todate',['s',startDate+' 23:59:59'],['s','yyyy-mm-dd hh24:mi:ss']]]);
		me.refresh();
	},
	onDblClick : function(grid, index, e) {
		var me=this;
		var r=grid.getStore().getAt(index);
		var form=me.opener.opener.form;
				
		var idPostfix=me.opener.opener.idPostfix||me.opener.opener.id;
		debugger
		if(!idPostfix)//框架配置表单
		{
			var ssy=form.getForm().findField('constriction')||form.getForm().findField('sbp');
			var szy=form.getForm().findField('diastolic')||form.getForm().findField('dbp');
			var FZJC=form.getForm().findField('auxiliaryCheck'); 
			var KSSJ=form.getForm().findField('visitDate');  
			var XTK=form.getForm().findField('fbs');
			var XTC=form.getForm().findField('pbs');
			if(ssy)
				ssy.setValue(r.data.SSY);
			if(szy)
				szy.setValue(r.data.SZY);
		    if(FZJC)
			    FZJC.setValue(r.data.FZJC);
			 if(KSSJ)
			   KSSJ.setValue(r.data.KSSJ);
			 if(XTK)
			   XTK.setValue(r.data.XTK);
			 if(XTC)
			   XTC.setValue(r.data.XTC);			

		}
		else{
			//纸质表单
			var ssy=document.getElementById("constriction_"+idPostfix)||document.getElementById("constriction"+idPostfix);
			var szy=document.getElementById("diastolic_"+idPostfix)||document.getElementById("diastolic"+idPostfix);
			var FZJC=document.getElementById("auxiliaryCheck_"+idPostfix)||document.getElementById("auxiliaryCheck"+idPostfix);
			var KSSJ=document.getElementsByName("visitDate");
			var XTK=document.getElementById("fbs_"+idPostfix)||document.getElementById("fbs"+idPostfix);
			var XTC=document.getElementById("pbs_"+idPostfix)||document.getElementById("pbs"+idPostfix);
			//糖尿病传前台用药
		//	var YYMC=document.getElementsByName("medicineName_1_"+idPostfix)[0]||document.getElementsByName("medicineName_1"+idPostfix)[0];
		//	var YYC=document.getElementById("medicineFrequency_1_"+idPostfix)||document.getElementById("medicineFrequency_1"+idPostfix);
		//	var YYMR=document.getElementById("medicineDosage_1_"+idPostfix)||document.getElementById("medicineDosage_1"+idPostfix);
		//	var YYDW=document.getElementById("medicineUnit_1_"+idPostfix)||document.getElementById("medicineUnit_1"+idPostfix);
			//高血压传前台用药
		//	var GYYMC=document.getElementsByName("drugNames0_"+idPostfix)[0]||document.getElementsByName("drugNames0"+idPostfix)[0];
		//	var GYYC=document.getElementById("everyDayTime1_"+idPostfix)||document.getElementById("everyDayTime1"+idPostfix);
		//	var GYYMR=document.getElementById("oneDosage1_"+idPostfix)||document.getElementById("oneDosage1"+idPostfix);
		//	var GYYDW=document.getElementById("medicineUnit1_"+idPostfix)||document.getElementById("medicineUnit1"+idPostfix);
			if(ssy)
				ssy.value=r.data.SSY;
			if(szy)
				szy.value=r.data.SZY;
			if(FZJC)
				FZJC.value=r.data.FZJC;
		    if(KSSJ)
				KSSJ.value=r.data.KSSJ;
		    if(XTK)
				XTK.value=r.data.XTK;
		    if(XTC)
				XTC.value=r.data.XTC;
				//糖
		//	if(YYMC)
		//		YYMC.value=r.data.YYMC;
		//	if(YYC)
		//		YYC.value=r.data.YYC;
		//	if(YYMR)
		//		YYMR.value=r.data.YYMR;
		//	if(YYDW)
		//		YYDW.value=r.data.YYDW;
		//		//高血压
		//	if(GYYMC)
		//		GYYMC.value=r.data.YYMC;
		//	if(GYYC)
		//		GYYC.value=r.data.YYC;
		//	if(GYYMR)
		//		GYYMR.value=r.data.YYMR;
		//	if(GYYDW)
		//		GYYDW.value=r.data.YYDW;
				
			
			
		}
		me.opener.win.hide();
	},
	doImportJzsz:function()
	{
		var me=this;
		var index=me.grid.getSelectionModel().last;
		if(index===false)
		{
			Ext.Msg.alert('提示', '请选择需要导入的数据');
			return;
		}
		me.onDblClick(me.grid,index);
	}	
});