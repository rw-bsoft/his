$package("phis.application.pha.script");

$import("phis.script.SimpleList");

phis.application.pha.script.PharmacyDrugsExpireTipsList = function(cfg) {
	phis.application.pha.script.PharmacyDrugsExpireTipsList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyDrugsExpireTipsList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (this.mainApp.pharmacyId == null
						|| this.mainApp.pharmacyId == ""
						|| this.mainApp.pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				return phis.application.pha.script.PharmacyDrugsExpireTipsList.superclass.initPanel.call(this,sc);
			},
			loadData : function() {
				var body=this.getBody();
				this.requestData.body = body;
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.serviceAction;
				phis.application.pha.script.PharmacyDrugsExpireTipsList.superclass.loadData
						.call(this);
			},
			doQuery : function() {
				this.loadData();
			},
			doPrint : function() {
				var body=this.getBody();
				var url = "resources/phis.prints.jrxml.PharmacyDrugsExpireTips.print?type=1&JZRQ="
						+ body["JZRQ"]+"&JZRQ_PRINT="+body["JZRQ_PRINT"];
				if(body["PYDM"]){
				url+="&PYDM="+body["PYDM"];
				}
				if(body["YPLX"]){
				url+="&YPLX="+body["YPLX"];
				}
				if(body["XQSY"]){
				url+="&XQSY="+body["XQSY"];
				}
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.PREVIEW();
			},
			doExport : function() {
				var body=this.getBody();
				var url = "resources/phis.prints.jrxml.PharmacyDrugsExpireTips.print?type=3&JZRQ="
						+ body["JZRQ"]+"&JZRQ_PRINT="+body["JZRQ_PRINT"];
				if(body["PYDM"]){
					url+="&PYDM="+body["PYDM"];
				}
				if(body["YPLX"]){
					url+="&YPLX="+body["YPLX"];
				}
				if(body["XQSY"]){
					url+="&XQSY="+body["XQSY"];
				}
				var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				return printWin;
			},
			getCndBar : function(items) {
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