$package("phis.application.ccl.script");
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckTypeList = function(cfg) {
	phis.application.ccl.script.CheckTypeList.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.ccl.script.CheckTypeList, phis.script.SimpleList,
		{	
			
			loadData : function() {
				this.requestData.cnd = 	['eq', ['$', 'JGID'],['s', this.mainApp['phis'].phisApp.deptId]]
				phis.application.ccl.script.CheckTypeList.superclass.loadData
						.call(this)
			},
			onRowClick : function() {
				var record = this.getSelectedRecord();
				var lbid = record.data.LBID;// 类别ID
				var checkPointList = this.opener.midiModules["checkPointList"];
				var checkProjectList = this.opener.midiModules["checkProjectList"];
				checkPointList.requestData.serviceId = "phis.checkApplyService";
				checkPointList.requestData.serviceAction = "getCheckPaintList";
				checkPointList.requestData.lbid = lbid;
				checkPointList.refresh();
				checkProjectList.store.removeAll();
			},
			doRemove : function() {
				var record = this.getSelectedRecord();
				if (record == undefined) {
					return;
				}
				var lbid = record.data.LBID;// 类别ID

				Ext.Msg.show({
					title : '删除确认',
					msg : '删除类别将删除①选中类别对应的所有关系②选中类别对应关系的费用绑定信息，是否继续？',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							// 根据lbid,bwid,xmid删除关系
							var res = phis.script.rmi.miniJsonRequestSync({
										serviceId : "checkApplyService",
										serviceAction : "removeCheckApplyRelation",
										body : {
											lbid : lbid
										}
									});
							if (res.code >= 300) {
								this.processReturnMsg(res.code, res.msg);
								return;
							}
							this.opener.midiModules["checkProjectList"].store
									.removeAll();
							this.opener.midiModules["checkPointList"].store
									.removeAll();
							this.refresh();
						} else {
							return;
						}
					},
					scope : this
				});
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