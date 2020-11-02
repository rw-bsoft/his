$package("phis.application.pay.script");

$import("phis.script.SimpleList");

phis.application.pay.script.MobilepaydetailsList = function(cfg) {
	this.exContext = {};
	this.searchFormId="MobilepaydetailsForm";
	this.searchFormWidth = 700;
	this.searchFormHeight = 25;
	cfg.showRowNumber = false;
	this.bodyStyle = 'border:0px';
	phis.application.pay.script.MobilepaydetailsList.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.pay.script.MobilepaydetailsList,
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
							id : "phis.dictionary.dzlx_mobilepay",
							autoLoad : true,
							editable : false,
							width : 130
						});
				dzlx.name="dzlx";		
				this.dzlx = dzlx;
				this.dzlx.store.on("load", this.dzlxLoad, this);
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
							handler : this.doDz
						})
				items.push({
							xtype : "button",
							text : "冲账",
							iconCls : "update",
							scope : this,
							handler : this.doCz
						})
				return items;
			},
			dzlxLoad : function(store) {
				this.dzlx.setValue("2");
			},
			DzjgRenderer : function(value, metaData, r, row, col) {
				switch(value)
				{
					case "支付平台已支付医院已作废":
					case "支付平台已支付医院有支付记录无结算记录":
					case "支付平台已支付医院无记录":
					case "支付平台已退款医院未作废":
					  return "<font style='color:red'>"+value+"</font>";
					  break;
					case "支付平台订单交易失败":
					  return "<font style='color:blue'>"+value+"</font>";
					  break;
					default:
				}
				return value;
			},
			doLoadMsg : function() {
				var body={};
				var form = Ext.getCmp(this.searchFormId).getForm();
				var items = form.items
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i);
					if(f.getName() == "beginDate") {
						body.dzrq=f.getValue().format("Y-m-d");
					}
					if(f.getName() == "endDate") {
						body.zzrq=f.getValue().format("Y-m-d");
					}
					if(f.getName() == "dzlx"){
						body.dzlx=f.getValue();
					}
				}
				if(body.dzlx.length < 1 ){
					MyMessageTip.msg("提示", "请选择类别！", true);
					return;
				}
				this.mask("正在请求支付平台记录！")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.mobilePaymentService",
							serviceAction : "getDzMx",
							body:body
						});
				if (ret.code > 300) {
					MyMessageTip.msg("提示", "查询支付平台记录报错："+ret.msg, true);
				} else {
					this.store.removeAll();
					if(ret.json.body!=null && ret.json.body.length){
						var records = this.getExtRecord(ret.json.body);
						this.store.add(records);
					}else{
						if(ret.json.errormsg!=null){
							MyMessageTip.msg("提示", ret.json.errormsg, true);
						}else{
							MyMessageTip.msg("提示", "未查询到支付平台记录.", true);
						}
					}
				}
				this.unmask();
			},
		doDz : function() {
				var body={};
				var form = Ext.getCmp(this.searchFormId).getForm();
				var items = form.items
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i);
					if(f.getName() == "beginDate") {
						body.dzrq=f.getValue().format("Y-m-d");
					}
					if(f.getName() == "endDate") {
						body.zzrq=f.getValue().format("Y-m-d");
					}
					if(f.getName() == "dzlx"){
						body.dzlx=f.getValue();
					}
				}
				var list=[];
				if(this.store.data.length==0){
					MyMessageTip.msg("提示", "请先查询再对账！", true);
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
							serviceId : "phis.mobilePaymentService",
							serviceAction : "mobilepaydz",
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
		},
		doCz : function() {
			//冲账
			var r=this.getSelectedRecord();
			if(!r){
				MyMessageTip.msg("提示","请选择一条记录！", true);
				return;
			}
			if(!r.data.DZJG){
				MyMessageTip.msg("提示","请先对账后再冲账！", true);
				return;
			}
			switch(r.data.DZJG_text)
			{
				case "支付平台已支付医院已结算":
				case "支付平台已退款医院已作废":
				case "支付平台已退款医院无记录":
					MyMessageTip.msg("提示","对账结果为"+r.data.DZJG_text+"的不需要冲账！", true);
					return;
				  break;
				case "支付平台订单交易失败":
					MyMessageTip.msg("提示","支付平台订单交易失败无法冲账，如需退款请联系管理员！", true);
					return;
				  break;
				case "支付平台已退款医院未作废":
					MyMessageTip.msg("提示","支付平台已退款医院未作废的信息请手动进行退号或发票作废！", true);
					return;
				  break;
				default:
			}
			Ext.Msg.confirm("请确认", "确认冲账病人"+r.data.NAME+"的收费订单吗？",
			function(btn) {
				if (btn == 'yes') {
					this.mask("正在冲账......")
					var body=r.data;
					body.DZLX=this.dzlx.value;
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "phis.mobilePaymentService",
								serviceAction : "mobilepaycz",
								body:body
						});
					MyMessageTip.msg("提示", ret.msg, true);
					if (ret.code == 200) {
						this.doLoadMsg();
					}
					this.unmask();
				}
			}, this);
		}
});
