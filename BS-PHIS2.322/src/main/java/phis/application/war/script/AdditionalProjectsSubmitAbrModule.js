$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.AdditionalProjectsSubmitAbrModule = function(cfg) {
	phis.application.war.script.AdditionalProjectsSubmitAbrModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.war.script.AdditionalProjectsSubmitAbrModule,
		phis.script.SimpleModule, {
			// 页面初始化
			initPanel : function(sc) {
				if (!this.mainApp['phis'].wardId) {
					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
					return;
				}
				if (this.panel) {
					return this.panel;
				}
				var schema = sc
				this.schema = schema;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : this.leftTitle,
										region : 'west',
										width : 220,
										items : this.getLList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : this.rightTitle,
										region : 'center',
										items : this.getRList()
									}],
							tbar : this.createButtons()
						});
				this.panel = panel;
				return panel;
			},
			// 获取左边的list
			getLList : function() {
				this.leftList = this.createModule("leftList", this.refLList);
				this.leftList.on("selectRecord",this.onSelectRecord,this);
				return this.leftList.initPanel();
			},
			// 获取右边的list
			getRList : function() {
				this.rightList = this.createModule("rightList", this.refRList);
				this.rightList.on("afterLoadData",this.onAfterLoadData,this)
				return this.rightList.initPanel();
			},
			//左边选中刷新右边
			onSelectRecord:function(){
			var records=this.leftList.getSelectedRecords();
			this.rightList.clear();
			var length=records.length;
			if(length==0){
			return;}
			var zyhs=new Array();
			for(var i=0;i<length;i++){
			var r=records[i];
			zyhs.push(r.get("ZYH"));
			}
			this.panel.el.mask("正在查询数据...","x-mask-loading")
			this.rightList.requestData.ZYHS=zyhs;
			this.rightList.loadData();
			},
			//右边数据加载完后取消界面锁定
			onAfterLoadData:function(){
			this.panel.el.unmask()
			},
			//刷新
			doRefresh : function() {
			this.leftList.clear();
			this.rightList.clear();
			this.leftList.loadData();
			},
			//确认
			doConfirm : function() {
				Ext.Msg.confirm("请确认", "是否打印?", function(btn1) {
								if (btn1 == 'yes') {
								this.doPrint(1);
								this.confirm();
								}else{
								this.confirm();	
								}
								},this)				
				
			},
			confirm:function(){
				var records=this.rightList.getSelectedRecords();
				var length=records.length
				if (length == 0) {
					MyMessageTip.msg("提示", "没有可执行的附加计价项目!", true);
					return;
				}
				var body = [];
				var jlxhs="";//打印用
				for (var i = 0; i < length; i++) {
					var r = records[i];
					if (!r.get("QRSL") && r.get("QRSL") != 0) {
						MyMessageTip.msg("提示", "确认数量不能为空", true);
						return;
					}
					body.push(r.data);
					if(i!=length-1){
					jlxhs+=r.get("JLXH")+","
					}else{
					jlxhs+=r.get("JLXH")
					}
				}
				var body_wpjfbz = {};
				body_wpjfbz['bq'] = this.mainApp['phis'].wardId;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configLogisticsInventoryControlService",
							serviceAction : "verificationWPJFBZ",
							body : body_wpjfbz
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doConfirm);
					return;
				}
				Ext.Msg.confirm("请确认", "执行附加计价?", function(btn) {
							if (btn == 'yes') {
								this.panel.el.mask("正在保存数据...","x-mask-loading")
								phis.script.rmi.jsonRequest({
											serviceId : "doctorAdviceExecuteService",
											serviceAction : "SaveConfirmAdditional",
											body : body
										}, function(code, msg, json) {
											this.panel.el.unmask()
											if (code >= 300) {
												this.processReturnMsg(code,msg);
												return;
											}
											this.doRefresh();
											if(json.RES_MESSAGE){
												MyMessageTip.msg("提示", json.RES_MESSAGE, true);
											}else{
												MyMessageTip.msg("提示", "确认成功", true);
											}
										}, this)
							}
						}, this);
			},
			//打开界面刷新
			afterOpen:function(){
			this.doRefresh();
			},
			//打印
			doPrint:function(tag){
				var records=this.rightList.getSelectedRecords();
				var length=records.length
				if (length == 0) {
					MyMessageTip.msg("提示", "没有可执行的附加计价项目!", true);
					return;
				}
				var jlxhs="";
				var zxsls="";
				for (var i = 0; i < length; i++) {
					var r = records[i];
					if (!r.get("QRSL") && r.get("QRSL") != 0) {
						MyMessageTip.msg("提示", "确认数量不能为空", true);
						return;
					}
					if(i!=length-1){
					jlxhs+=r.get("JLXH")+",";
					zxsls+=r.get("QRSL")+",";
					}else{
					jlxhs+=r.get("JLXH");
					zxsls+=r.get("QRSL");
					}
				}
				var rs=this.leftList.getSelectedRecords();
				var l=rs.length;
				var zyhs="";
				for(var i=0;i<l;i++){
				var rr=rs[i]
				if(i!=l-1){
				zyhs+=rr.get("ZYH")+",";
				}else{
				zyhs+=rr.get("ZYH")
				}
				}
				var url = "resources/phis.prints.jrxml.AdditionalProjectsSubmit.print?type=1&JLXHS="
						+  jlxhs+"&ZXSLS="+zxsls+"&ZYHS="+zyhs;
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				if(tag==1){
				LODOP.PRINT();
				}else{
				LODOP.PREVIEW();
				}
				
			}
		});