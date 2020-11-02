$package("phis.application.ccl.script")
$import("phis.script.SimpleModule")

phis.application.ccl.script.CheckPointPutInModule = function(cfg) {
	phis.application.ccl.script.CheckPointPutInModule.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ccl.script.CheckPointPutInModule,
		phis.script.SimpleModule, {
			initPanel : function() {
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
										region : 'center',
										height : 200,
										width : 200,
										items : this.getCheckPointList()
									}],
							tbar : this.getTbar()
						});
				this.panel = panel;
				return panel;
			},
			getCheckPointList : function() {
				this.pointList = this.createModule("pointList",
						this.refPointList);
				this.pointList.opener = this;
				this.pointGrid = this.pointList.initPanel();
				return this.pointGrid;
			},
			getTbar : function() {
				var tbar = [];
				tbar.push({
							xtype : "button",
							text : "导入",
							iconCls : "add",
							scope : this,
							handler : this.doAdd
						});
				return tbar;
			},
			// 添加配送单信息到未确认入库单
			doAdd : function() {
				var lbid = this.opener.opener.midiModules["checkTypeList"].getSelectedRecord().data.LBID;//类别id
				var store = this.opener.grid.getStore();// 关系中已经存在的部位store
				var records = this.pointList.getSelectedRecords();// 导入列表中选中的记录
				// 没有选中的，给予提示
				if (records.length == 0) {
					this.pointList.clearSelect();
					Ext.Msg.alert("提示", "请先选择要导入的部位");
					return;
				}
				// 判断选中的部位是否已存在，已存在就不给于导入
				for (var i = 0; i < records.length; i++) {
					var selectBwid = records[i].data.BWID;//选中的部位id
					var selectBwmc = records[i].data.BWMC;//选中的部位名称
					for (var j = 0; j < store.getCount(); j++) {
						var bwid = store.getAt(j).get("BWID");//原先存在的部位id
						if (selectBwid == bwid) {
							Ext.Msg.alert("提示", selectBwmc + "已存在,请取消后导入");
							return
						}
					}
				}
				for (var i = 0; i < records.length; i++) {
					var bwid = records[i].data.BWID;
					var bwmc = records[i].data.BWMC;
					var pydm = records[i].data.PYDM;
					var record = new Ext.data.Record();
					record.set("LBID",lbid);
					record.set("BWID",bwid);
					record.set("BWMC",bwmc);
					record.set("PYDM",pydm);
					this.opener.store.add(record);
				}
				this.pointList.clearSelect();//清空选择
				this.getWin().hide();//隐藏
				this.opener.selectRow(this.opener.store.getCount()-1);//选中最新的
				this.opener.onRowClick();//模拟点击事件

			}
		})
