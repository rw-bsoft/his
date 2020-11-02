$package("phis.application.xnh.script");

$import("phis.script.SimpleList");

phis.application.xnh.script.NhdetailsList = function(cfg) {
	this.exContext = {};
	this.searchFormId="NhdetailsForm";
	this.searchFormWidth = 700;
	this.searchFormHeight = 25;
	this.bodyStyle = 'border:0px';
	phis.application.xnh.script.NhdetailsList.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.xnh.script.NhdetailsList,
		phis.script.SimpleList, {
			getCndBar : function(items) {
				var simple = new Ext.form.FormPanel({
							id : this.searchFormId,
							frame : false,
							layout : 'column',
							bodyStyle : this.bodyStyle,
							width : this.searchFormWidth,
							height : this.searchFormHeight,
							items : this.initConditionFields()
						});
				this.simple = simple;
				return [simple];
			},
			// 生成查询框
			initConditionFields : function(items) {
				var items = [];
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
				items.push(new Ext.form.Label({
					text : "类别:"
						}))		
				var dzlx = util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.dzlx",
							editable : false,
							width : 130
						});
				dzlx.name="dzlx";		
				this.dzlx = dzlx;
				items.push(dzlx);
				items.push({
							xtype : "button",
							text : "查询",
							iconCls : "query",
							scope : this,
							handler : this.doLoadMsg
						})
				items.push({
							xtype : "button",
							text : "对账",
							iconCls : "update",
							scope : this,
							handler : this.doHldz
						})
				items.push({
							xtype : "button",
							text : "冲账",
							iconCls : "update",
							scope : this,
							handler : this.doHlcz
						})		
				return items;
			},
			doLoadMsg : function() {
				var body={};
				var form = Ext.getCmp(this.searchFormId).getForm();
				var items = form.items
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i);
					if(f.getName() == "beginDate") {
						body.Dzrq=f.getValue().format("Y-m-d");
					}
					if(f.getName() == "endDate") {
						body.Zzrq=f.getValue().format("Y-m-d");
					}
					if(f.getName() == "dzlx"){
						body.dzlx=f.getValue();
					}
				}
				if(body.dzlx.length < 1 ){
					MyMessageTip.msg("提示", "请选择类别！", true);
					return;
				}
				this.mask("正在请求农合报销记录！")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "mrdzMx",
							body:body
						});
				if (ret.code > 300) {
					MyMessageTip.msg("提示", ret.msg, true);
				} else {
					this.store.removeAll();
					if(ret.json.body.length){
						var records = this.getExtRecord(ret.json.body);
						this.store.add(records);
					}
				}
				this.unmask();
			},
		doHldz : function() {
				var body={};
				var form = Ext.getCmp(this.searchFormId).getForm();
				var items = form.items
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i);
					if(f.getName() == "beginDate") {
						body.Dzrq=f.getValue().format("Y-m-d");
					}
					if(f.getName() == "endDate") {
						body.Zzrq=f.getValue().format("Y-m-d");
					}
					if(f.getName() == "dzlx"){
						body.dzlx=f.getValue();
					}
				}
				var list=[];
				if(this.store.data.length==0){
					MyMessageTip.msg("提示", "请先查询在对账！", true);
					return;
				}
				if(this.store.data.length>1000){
					MyMessageTip.msg("提示", "对账条数太多，请缩短时间范围！", true);
					return;
				}
				for(var i=0;i <this.store.data.length;i++){
					list.push(this.store.data.items[i].data);
				}
				body.data=list;
				this.mask("正在对账！")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "mrnhdz",
							body:body
						});
				if (ret.code > 300) {
					MyMessageTip.msg("提示", ret.msg, true);
				} else {
					this.store.removeAll();
					if(ret.json.body.length){
						var records = this.getExtRecord(ret.json.body);
						this.store.add(records);
					}
				}
				this.unmask();
		},
		//处理下数据
		getExtRecord : function(data) {
		var records = [];
		for (var i = 0; i < data.length; i++) {
			var record = new Ext.data.Record(data[i]);
			records.push(record);
		}
		return records;
		}
		,doHlcz : function() {
			//合疗冲账
			var r=this.getSelectedRecord();
			if(!r){
				MyMessageTip.msg("提示","请选择一条记录！", true);
				return;
			}
			if(r.get("JYLX")=="-1"){
				MyMessageTip.msg("提示","负交易不需要冲账！", true);
				return;
			}
			if(r.get("DZJG")!="2"){
				MyMessageTip.msg("提示","对账结果不是农合报销医院无记录的不需要冲账！", true);
				return;
			}
			this.mask("正在冲账！")
			var body=r.data;
			body.DZLX=this.dzlx.value;
			var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.xnhService",
						serviceAction : "mrnhcz",
						body:body
				});
			MyMessageTip.msg("提示", ret.msg, true);
			this.unmask();
		}
		});
