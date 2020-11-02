$package("phis.application.pha.script")
$import("phis.script.SimpleModule");
phis.application.pha.script.PharmacyApplyRefundModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.pha.script.PharmacyApplyRefundModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.pha.script.PharmacyApplyRefundModule,
		phis.script.SimpleModule, {
		initPanel : function(sc) {
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				//进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				if (this.panel) {
					return this.panel;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
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
										ddGroup : "firstGrid",
										border : false,
										split : true,
										title : '未确定退药单',
										region : 'west',
										width : this.width,
										items : this.getUList()
									}, {
										layout : "fit",
										ddGroup : "secondGrid",
										border : false,
										split : true,
										title : '已确定退药单',
										region : 'center',
										items : this.getList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			// 未确定退药界面
			getUList : function() {
				this.uRefundList = this.createModule(
						"MedicinesUndeterminedApplyRefundList", this.refUList);
				this.uRefundList.on("storehouseSelect",
						this.onStorehouseSelectSelect, this);
				return this.uRefundList.initPanel();
			},
			// 确定申领界面
			getList : function() {
				this.RefundList = this.createModule("MedicinesApplyRefundList",
						this.refList)
				return this.RefundList.initPanel();
			},
			//选择药库同时刷新两边
			onStorehouseSelectSelect:function(record){
			this.uRefundList.yksb =record.data;
			this.uRefundList.requestData.serviceId=this.fullserviceId;
			this.uRefundList.requestData.serviceAction=this.queryDataServiceActionID;
			this.uRefundList.doRefreshWin();
			this.RefundList.yksb =record.data;
			this.RefundList.requestData.serviceId=this.fullserviceId;
			this.RefundList.requestData.serviceAction=this.queryDataServiceActionID;
			this.RefundList.doCndQuery();
			},
			afterOpen : function() {
				if(!this.uRefundList||!this.RefundList){return;}
				// 拖动操作
				if(this.uRefundList.yksb){
				this.uRefundList.doRefresh();}
				var firstGrid = this.RefundList.grid;
				var grid=this.uRefundList;
				var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'firstGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								var records = ddSource.dragData.selections;
								grid.doCommit();
								return true
							}
						});
			}
		});
