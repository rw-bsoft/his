/**
 * 初始化账簿
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule", "phis.script.rmi.miniJsonRequestSync");

phis.application.pha.script.PharmacyInitialBooksModule = function(cfg) {
	phis.application.pha.script.PharmacyInitialBooksModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyInitialBooksModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 进行是否药房初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryInitActionId
						});
						
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				//初始数据
				var ret_c = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initialSignsQueriesActionId
						});
				if (ret_c.code > 300) {
					this.processReturnMsg(ret_c.code, ret_c.msg, this.initPanel);
					return null;
				}
				if(ret_c.code == 201){
				this.isTransfer=1;
				}else{
				this.isTransfer=0;
				}
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
										title : '',
										region : 'center',
										width : 960,
										items : this.getList()
									}]
						});
				this.panel = panel;
				this.panel.on("beforeclose",this.onBeforeclose,this);
				return panel;
			},
			doNew : function() {
			},
			getList : function() {
				this.list = this.createModule("cshlist", this.cshList);
				if(this.list.editRecords){
					this.list.editRecords={};
				}
				if (this.isTransfer == 1){
				this.list.isTransfer=1;}
				this.list.on("listClose",this.onListClose,this);
				return this.list.initPanel();
			},
			onListClose:function(){
			this.getWin().close();
			},
			onBeforeclose:function(){
				if(!this.list){
				return true;
				}
				if(this.list.getEditRecords().length==0){
					return true;
				}else{
						Ext.Msg.show({
					title : "提示",
					msg : "有已修改未保存的数据,确定不保存直接关闭?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
						this.editRecords={};
						this.getWin().close();
						}
					},
					scope : this});
					return false;
				}
			}
		});