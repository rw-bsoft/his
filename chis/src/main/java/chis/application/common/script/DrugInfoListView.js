$package("chis.application.common.script")
$import("chis.script.BizSelectListView")
chis.application.common.script.DrugInfoListView = function(cfg) {
	cfg.autoLoadSchema=false;
	debugger;
	var sfzh=cfg.exContext.empiData.idCard==undefined?null:cfg.exContext.empiData.idCard;
	//根据病人id查询最近一次开处方的时间
//	var lastKfrq=(new Date()).format('Y-m-d h:i:s');
//	if(brid){
//		var req=util.rmi.miniJsonRequestSync({
//			serviceId:'phis.PhisToChisService',
//			serviceAction:'getLastCfDate',
//			method:'execute',
//			body:{brid:brid}
//		});
//		if (req.code != 200) {
//			this.processReturnMsg(req.code, req.msg);
//			return;
//		} else {
//			lastKfrq=req.json.kfrq;
//		}
//
//	}
//	cfg.initCnd=['and',['eq',['$','c.BRID'],['i',brid]],['ge',['$','c.KFRQ'],['todate',['s',lastKfrq],['s','yyyy-mm-dd hh24:mi:ss']]]];
	cfg.initCnd=['and',['eq',['$','SFZH'],['s',sfzh]]];
	chis.application.common.script.DrugInfoListView.superclass.constructor.apply(this, [cfg])
	this.requestData.serviceId='phis.simpleQuery';
}
Ext.extend(chis.application.common.script.DrugInfoListView,chis.script.BizSelectListView, {
	loadData : function() {
		if(!this.dtype){
		var classId=this.opener.opener.classId;
		this.dtype=classId.split('.')[2];
		if(this.dtype=='hy')//高血压
		{
			this.initCnd.push(['eq',['$','YYBS'],['i',1]]);		
		}
		else if(this.dtype=='dbs')//糖尿病
		{	
			this.initCnd.push(['in',['$','YYBS'],[2,4]]);	
		}
		}
		chis.application.common.script.DrugInfoListView.superclass.loadData
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
		var py=new Ext.form.TextField({
			name:'py',
			width:90,
            listeners: {
                specialkey: function(field, e){
                    if (e.getKey() == e.ENTER) {
                    	me.doQuery();	
                    }
                    }
                }
		});
		me.py=py;
		var queryBtn = new Ext.Toolbar.Button({
					iconCls : "query",
					text : "查询",
					handler : me.doQuery,
					scope:me
				})
		var importBtn = new Ext.Toolbar.Button({
			iconCls : "add",
			text : "导入",
			handler : me.doImport,
			scope:me
		})
		me.queryBtn = queryBtn;
		me.importBtn = importBtn;
//		queryBtn.on("click", me.doQuery, me);
//		importBtn.on("click", me.doImport, me);
		return ['开方日期：', startDate,' 至 ',endDate,'拼音码: ',py,queryBtn,importBtn,'-']
	},
	doQuery:function()
	{
		var me=this;
		var startDate=me.startDate.getRawValue();
		var endDate=me.endDate.getRawValue();
		var py=me.py.getValue().trim();
		var sfzh=me.exContext.empiData.idCard==undefined?null:me.exContext.empiData.idCard;
		me.requestData.cnd=['and',
		                              ['eq',['$','SFZH'],['s',sfzh]],
		                              ['like',['$','PYDM'], ['s',Ext.util.Format.uppercase(py)+'%']],
									  ['le',['$',"kfrq"],
									   		['todate',['s',endDate+' 23:59:59'],['s','yyyy-mm-dd hh24:mi:ss']]
									  ]
										];
		if(startDate)
			me.requestData.cnd.push(['ge',['$',"kfrq"],['todate',['s',startDate+' 23:59:59'],['s','yyyy-mm-dd hh24:mi:ss']]]);
		
		if(me.dtype=='hy')
		{
			me.requestData.cnd.push(['eq',['$','YYBS'],['i',1]]);		
		}
		else if(me.dtype=='dbs')
		{	
			me.requestData.cnd.push(['in',['$','YYBS'],[1,2,4]]);		
		}
		
		me.refresh();
	},
	doImport:function()
	{
		var me=this;
		var records=me.getSelectedRecords();
		if(!records||records.length==0)
		{
			Ext.Msg.alert('提示', '请选择需要导入的药品');
			return;
		}
		me.saveDrugs(records);
	},
	onDblClick : function(grid, index, e) {
		var me=this;
		var r=grid.getStore().getAt(index);
		var records=[r];
		me.saveDrugs(records);

	},
	saveDrugs:function(records)
	{
		var me=this;
		if(me.dtype=='dbs')
			me.saveDbsDrugs(records);
		if(me.dtype=='hy')
			me.saveHyDrugs(records);
	},
	saveDbsDrugs:function(records)
	{
		var me=this;
		var saveData=[];
		var deptId=me.mainApp.deptId;
		var uid=me.mainApp.uid;
		var nowDate=(new Date()).format('Y-m-d H:i:s');
		
		for(var i=0;i<records.length;i++)
		{
			var record=records[i].data;
			var data={
					createData:nowDate,
					createUnit:deptId,
					createUser:uid,
					lastModifyDate:nowDate,
					lastModifyUnit:deptId,
					lastModifyUser:uid,
					days:record.YYTS,
					medicineDosage:record.YCJL,
					medicineFrequency:record.YPYF_text,
					medicineId:record.YPXH,
					medicineMoring:'',
					medicineName:record.YPMC,
					medicineNight:'',
					medicineNoon:'',
					medicineSleep:'',
					medicineTotalDosage:record.YPSL,
					medicineUnit:record.JLDW,
					medicineWay:'',
					otherMedicineDesc:'',
					phrId:me.exContext.ids.phrId,
					recordId:undefined,
					sideEffects:undefined,
					sideEffectsFlag:undefined,
					visitId:me.exContext.args.r.data.visitId
			}
			saveData.push(data);
		}
		util.rmi.miniJsonRequestAsync({
				serviceId:'chis.diabetesVisitService',
				serviceAction:'saveMedicines',
				method:'execute',
				op:'create',
				schema:'chis.application.dbs.schemas.MDC_DiabetesMedicine',
				module:me._mId,  //增加module的id
				body:{records:saveData}
			},function(code,msg,json){
					if(code > 300){
						Ext.Msg.alert('提示', '导入失败！请重新操作');
						return;
					}
					var list=me.opener.opener;
					list.refresh();
					me.opener.win.hide();
				},
				this)
		
	},
	saveHyDrugs:function(records)
	{
		var me=this;
		var saveData=[];
		var deptId=me.mainApp.deptId;
		var uid=me.mainApp.uid;
		var nowDate=(new Date()).format('Y-m-d H:i:s');
		for(var i=0;i<records.length;i++)
		{
			var record=records[i].data;
			var data={
					createData:nowDate,
					createUnit:deptId,
					createUser:uid,
					lastModifyDate:nowDate,
					lastModifyUnit:deptId,
					lastModifyUser:uid,
					days:record.YYTS,
					
					medicineDate:record.KFRQ,
						
					medicineDosage:record.YCJL,
					medicineFrequency:record.YPYF_text,
					medicineId:record.YPXH,
					medicineName:record.YPMC,
					medicineTotalDosage:record.YPSL,
					medicineUnit:record.JLDW,
					medicineWay:'',
					otherMedicineDesc:'',
					phrId:me.exContext.ids.phrId,
					recordId:undefined,
					sideEffects:undefined,
					sideEffectsFlag:undefined,
		
					totalCount:record.YPSL,
					useUnits:record.JLDW,
					
					visitId:this.exContext.args.visitId
			}
			saveData.push(data);
		}
		util.rmi.miniJsonRequestAsync({
				serviceId:'chis.hypertensionVisitService',
				serviceAction:'saveVisitMedicines',
				method:'execute',
				op:'create',
				schema:'chis.application.hy.schemas.MDC_HypertensionMedicine',
				module:me._mId,  //增加module的id
				body:{records:saveData}
			},function(code,msg,json){
					if(code > 300){
						Ext.Msg.alert('提示', '导入失败！请重新操作');
						return;
					}
					var list=me.opener.opener;
					list.refresh();
					me.opener.win.hide();
				},
				this)
		
	}
});