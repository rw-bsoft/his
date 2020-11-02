
$package("phis.application.ccl.script");

$import("phis.script.SimpleList")

phis.application.ccl.script.FeeBoundList = function(cfg) {
	phis.application.ccl.script.FeeBoundList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.ccl.script.FeeBoundList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.cnd = 	['eq', ['$', 'a.JGID'],['s', this.mainApp['phis'].phisApp.deptId]]
				phis.application.ccl.script.FeeBoundList.superclass.loadData
						.call(this)
			},
			doRemove : function(){
				var record = this.getSelectedRecord();
				if(record==undefined){
					return;
				}
				var lbid = record.data.LBID;
				var bwid = record.data.BWID;
				var xmid = record.data.XMID;
				var jgid = record.data.JGID;
				Ext.Msg.show({
					title : '删除确认',
					msg : '确认删除？',
					modal : false,
					width : 100,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							var res = util.rmi.miniJsonRequestSync({
							serviceId : "phis.checkApplyService",
							serviceAction : "removeCheckApplyFeeDetails",
							body:{
								lbid:lbid,
								bwid:bwid,
								xmid:xmid,
								jgid:jgid
							}
						});
						if (res.code >= 300) {
							this.processReturnMsg(res.code, res.msg);
							return;
						} 
						MyMessageTip.msg("提示", "删除成功!", true);
						this.refresh();
						this.opener.midiModules["checkList"].refresh();
						} else {
							return;
						}
					},
					scope : this
				});
				
			},
			doSave : function(){
				var store = this.grid.getStore();
				if(store.getCount()==0){
					Ext.Msg.alert("提示","列表没有数据,无法保存");
					return;
				}
				var list=[];
				for(var i=0;i<store.getCount();i++){
					list.push(store.data.items[i].data)
				}
				var res = util.rmi.miniJsonRequestSync({
					serviceId : "phis.checkApplyService",
					serviceAction : "saveCheckApplyFeeDetails",
					body : {
						list:list
					}
				});
				if (res.code >= 300) {
					this.processReturnMsg(res.code, res.msg);
					return;
				}
				this.refresh();
				MyMessageTip.msg("提示", "保存成功!", true);
				this.opener.midiModules["checkList"].refresh();
			}
			
		});