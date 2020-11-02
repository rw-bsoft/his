$package("phis.application.ccl.script");
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckApplyProject_WH = function(cfg) {
	phis.application.ccl.script.CheckApplyProject_WH.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.ccl.script.CheckApplyProject_WH, phis.script.SimpleList,
		{	
			loadData : function() {
				var pym=this.cndField.getValue()
				if(pym&&pym!=null&&pym!=""){
					pym=pym.toUpperCase();
					this.requestData.cnd = 	['and',['eq', ['$', 'JGID'],['s', this.mainApp['phis'].phisApp.deptId]],['like', ['$', 'pydm'], ['s', pym]]];
				} else {
					this.requestData.cnd = 	['eq', ['$', 'JGID'],['s', this.mainApp['phis'].phisApp.deptId]];
				}
				phis.application.ccl.script.CheckApplyProject_WH.superclass.loadData
						.call(this)
			},
			doRemove : function() {
				var record = this.getSelectedRecord();
				if (record == undefined) {
					return;
				}
				var xmid = record.data.XMID;// 项目ID
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "checkApplyService",
							serviceAction : "checkBusinessData",
							body : {
								xmid : xmid
							}
						});
				if (res.code >= 300) {
					this.processReturnMsg(res.code, res.msg);
					return;
				}
				var hasData = res.json.hasData;
				if(hasData){
					Ext.Msg.alert("提示", "该项目已经存在业务数据，无法删除");
					return;
				}				
				Ext.Msg.show({
					title : '删除确认',
					msg : '删除该项目将同时删除①已维护的包含该项目的所有关系②包含该项目的费用绑定信息，是否继续？',
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
											xmid : xmid
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