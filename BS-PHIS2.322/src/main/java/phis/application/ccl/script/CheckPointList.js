$package("phis.application.ccl.script");
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckPointList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.ccl.script.CheckPointList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckPointList,
		phis.script.SimpleList, {
			onRowClick : function(){
				var record = this.getSelectedRecord();
				var checkProjectList = this.opener.midiModules["checkProjectList"];
				/*checkProjectList.requestData.cnd = ['and',['eq', ['$', 'lbid'],
						['i', lbid]],['eq', ['$', 'bwid'],['i', bwid]]];*/
				checkProjectList.requestData.lbid = record.data.LBID;//类别ID
				checkProjectList.requestData.bwid = record.data.BWID;//部位ID
				checkProjectList.refresh();	
			},
			doRemove : function() {
				var record = this.getSelectedRecord();
				if(record==undefined){
					return;
				}
				var lbid = record.data.LBID;// 类别ID
				var bwid = record.data.BWID;//部位ID
				Ext.Msg.show({
					title : '删除确认',
					msg : '删除部位将删除①选中类别和部位对应的所有关系②选中类别和部位对应关系的费用绑定信息，是否继续？',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							//根据lbid,bwid删除关系
							var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "checkApplyService",
							serviceAction : "removeCheckApplyRelation",
							body:{
								lbid:lbid,
								bwid:bwid
							}
						});
						if (res.code >= 300) {
							this.processReturnMsg(res.code, res.msg);
							return;
						} 
						this.opener.midiModules["checkTypeList"].refresh();
						this.opener.midiModules["checkProjectList"].refresh();
						this.refresh();
						} else {
							return;
						}
					},
					scope : this
				});
				
			},
			doAdd : function(){
//				//首先刷新部位列表，显示对应类别存在的部位
//				var lbid = this.opener.midiModules["checkTypeList"].getSelectedRecord().data.LBID;
//				this.requestData.serviceId = "checkApplyService";
//				this.requestData.serviceAction = "getCheckPaintList";
//				this.requestData.lbid=lbid;	
//				this.refresh();
				//清空项目列表
//				this.opener.midiModules["checkProjectList"].store.removeAll();
				var module = this.createModule("pointModule",this.refPointModule);
				if (module) {
					var win = module.getWin();
					win.setWidth(500);
					win.setHeight(400);
					win.show();
					module.opener = this;
					module.pointList.refresh();
				}
			},
			// 刚打开页面时候默认选中数据
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick();
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick();
				}
			}
		});