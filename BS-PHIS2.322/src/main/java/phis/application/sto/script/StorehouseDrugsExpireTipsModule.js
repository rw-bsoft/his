/**
 * 药品过期提示
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleList");

phis.application.sto.script.StorehouseDrugsExpireTipsModule = function(cfg) {
	phis.application.sto.script.StorehouseDrugsExpireTipsModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseDrugsExpireTipsModule,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				this.tbar = this.initConditionFields();
				return phis.application.sto.script.StorehouseDrugsExpireTipsModule.superclass.initPanel
						.apply(this, [sc]);
			},
			//加载数据
			loadData : function() {
				this.requestData.serviceId = "phis.storehouseManageService";
				this.requestData.serviceAction = this.serviceAction;
				var body=this.getBody();
				this.requestData.body = body;
				phis.application.sto.script.StorehouseDrugsExpireTipsModule.superclass.loadData
						.call(this);
			},
			//上面查询的时间框
			initConditionFields : function() {
				var items = [];
				items.push(new Ext.form.Label({
							text : '拼音代码：'
						}))
				this.pydmText = new Ext.form.TextField({})
				items.push(this.pydmText);
				items.push(new Ext.form.Label({
							text : '药品类型：'
						}))
				var yplxStore = new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : [[0, '全部'], [1, '西药'], [2, '中成药'], [3, '草药']]
						});

				this.yplxCombox = new Ext.form.ComboBox({
							store : yplxStore,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 80,
							value : 0
						});
				
				items.push(this.yplxCombox);
				items.push(new Ext.form.Label({
							text : '截止日期：'
						}))
				this.jzrqText=new Ext.form.DateField({
					name : 'beginDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				})
				items.push(this.jzrqText);
				items.push(new Ext.form.Label({
							text : '效期剩余：'
						}))
				var xqsyStore = new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : [[0, '全部'],[14, '已过期'],[1, '1月'], [2, '2月'], [3, '3月'], [4, '4月'], [5, '5月'], [6, '6月'], [7, '7月'], [8, '8月'], [9, '9月'], [10, '10月'], [11, '11月'], [12, '12月'], [13, '未维护']]
						});
				this.xqsyCombox = new Ext.form.ComboBox({
							store : xqsyStore,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 80,
							value : 0
						});
				items.push(this.xqsyCombox);
				return items;
			},
			//查询
			doQuery : function() {
				this.refresh();
			}
			,
			//update by caijy for zw2要求药库的也和药房一样查询日期后面一年药库过期的药品
//			querySX_PREALARM : function() {// 获取药品预警截至日期默认参数
//				var ret = phis.script.rmi.miniJsonRequestSync({
//							serviceId : this.serviceId,
//							serviceAction : this.queryDateAction
//						});
//				if (ret.code > 300) {
//					this.processReturnMsg(ret.code, ret.msg,
//							this.querySX_PREALARM);
//				} else {
//					return ret.json.SX_PREALARM;
//				}
//			},
			//打印
			doPrint : function() {
				var body=this.getBody();
				if(body==null){
				return;}
				var pages="phis.prints.jrxml.DrugsExpireTips";
			    var url="resources/"+pages+".print?type=1";
				url += "&JZRQ=" + body["JZRQ"];
				if(body["PYDM"]){
				url+="&PYDM="+body["PYDM"];
				}
				if(body["YPLX"]){
				url+="&YPLX="+body["YPLX"];
				}
				if(body["XQSY"]){
				url+="&XQSY="+body["XQSY"];
				}
				
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				//预览LODOP.PREVIEW();
				//预览LODOP.PRINT();
				//LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				LODOP.PREVIEW();// 预览
			},
			getBody:function(){
			var body={};
			var jzrq=this.jzrqText.getValue();
			if(!jzrq||jzrq==null||jzrq==""){
			MyMessageTips.msg("提示","截至日期输入错误",false);
			return;
			}
			if(jzrq&&jzrq!=null&&jzrq!=""){
			body["JZRQ"]=jzrq.format("Ymd")
			body["JZRQ_PRINT"]=jzrq.format("Y-m-d")
			}
			var pydm=this.pydmText.getValue();
			if(pydm&&pydm!=null&&pydm!=""){
			body["PYDM"]=pydm.toUpperCase();
			}
			var yplx=this.yplxCombox.getValue();
			if(yplx&&yplx!=0){
			body["YPLX"]=yplx;
			}
			var xqsy=this.xqsyCombox.getValue();
			if(xqsy&&xqsy!=0){
			body["XQSY"]=xqsy;
			}
			return body
			}
		});