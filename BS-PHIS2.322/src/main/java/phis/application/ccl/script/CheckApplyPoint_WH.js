$package("phis.application.ccl.script")
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckApplyPoint_WH = function(cfg) {
	phis.application.ccl.script.CheckApplyPoint_WH.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.ccl.script.CheckApplyPoint_WH, phis.script.SimpleList,
		{	
			loadData : function() {
				this.requestData.cnd = 	['eq', ['$', 'JGID'],['s', this.mainApp['phis'].phisApp.deptId]]
				phis.application.ccl.script.CheckApplyPoint_WH.superclass.loadData
						.call(this)
			},
			doRemove : function() {
				var record = this.getSelectedRecord();
				if (record == undefined) {
					return;
				}
				var bwid = record.data.BWID;// 部位ID
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "checkApplyService",
							serviceAction : "checkBusinessData",
							body : {
								bwid : bwid
							}
						});
				if (res.code >= 300) {
					this.processReturnMsg(res.code, res.msg);
					return;
				}
				var hasData = res.json.hasData;
				if(hasData){
					Ext.Msg.alert("提示", "该部位已经存在业务数据，无法删除");
					return;
				}				
				Ext.Msg.show({
					title : '删除确认',
					msg : '删除该部位将同时删除①已维护的包含该部位的所有关系②包含该部位的费用绑定信息，是否继续？',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							var res = phis.script.rmi.miniJsonRequestSync({
										serviceId : "checkApplyService",
										serviceAction : "removeCheckApplyWH",
										body : {
											bwid : bwid
										}
									});
							if (res.code >= 300) {
								this.processReturnMsg(res.code, res.msg);
								return;
							}
							this.refresh();
						} 
					},
					scope : this
				});
			}

		});