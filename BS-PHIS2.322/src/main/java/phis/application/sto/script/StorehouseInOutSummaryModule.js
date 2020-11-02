/**
 * 药品出入库汇总
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleModule");

phis.application.sto.script.StorehouseInOutSummaryModule = function(cfg) {
	phis.application.sto.script.StorehouseInOutSummaryModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseInOutSummaryModule,
				phis.script.SimpleModule, {
			initPanel : function() {
				if (this.mainApp['phis'].storehouseId == null
								|| this.mainApp['phis'].storehouseId == ""
								|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				this.tbar=this.initConditionFields();
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
										title : '药品出库汇总',
										region : 'center',
										items :  this.getCKList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '药品入库汇总',
										region : 'west',
										width : "50%",
										items : this.getRKList()
									}],
							tbar : this.tbar//this.getTbar(4)
						});
				this.panel = panel;
				return panel;
			},
			//药品入库汇总
			getRKList : function() {
						this.rkList = this.createModule("refLList",
								this.refLList);
						this.rkList.opener = this;
						this.rkList.requestData.kfsb=this.mainApp['phis'].storehouseId;
						return this.rkList.initPanel();
					},
			//药品出库汇总
			getCKList : function() {
						this.ckList = this.createModule("refRList",
								this.refRList);
						this.ckList.opener = this;
						this.ckList.requestData.kfsb=this.mainApp['phis'].storehouseId;
						return this.ckList.initPanel();
			},
			//统计
			doStatistics:function(){
				this.rkList.refresh();
				this.ckList.refresh();
			},
			initConditionFields : function() {
				var items = [];
				items.push(new Ext.form.DateField({
							name : 'beginDate',
							value : Date.getServerDate(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
//							emptyText : '开始时间'
						}))
				items.push(new Ext.form.Label({
						   text:'至'
						  }))
				items.push(new Ext.form.DateField({
							name : 'endDate',
							value : Date.getServerDate(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
//							emptyText : '开始时间'
						}))
				items.push({
					text : "统计",
					iconCls : "default",
					handler : this.doStatistics,
					scope : this
				});
				return items
			}
});