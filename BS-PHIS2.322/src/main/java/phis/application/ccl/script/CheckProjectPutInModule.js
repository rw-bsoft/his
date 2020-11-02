$package("phis.application.ccl.script")
$import("phis.script.SimpleModule")

phis.application.ccl.script.CheckProjectPutInModule = function(cfg) {
	phis.application.ccl.script.CheckProjectPutInModule.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ccl.script.CheckProjectPutInModule, phis.script.SimpleModule,
		{
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
										items : this.getCheckProjectList()
									}],
							tbar : this.getTbar()
						});
				this.panel = panel;
				return panel;
			},
			getCheckProjectList : function() {
				this.projectList = this.createModule(
						"projectList", this.refProjectList);
				this.projectList.opener=this;
				this.projectGrid = this.projectList.initPanel();
				return this.projectGrid;
			},
			getTbar : function(){
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
			//添加配送单信息到未确认入库单
			doAdd : function(){
				var records = this.projectList.getSelectedRecords();// 导入列表中选中的记录
				// 没有选中的，给予提示
				if (records.length == 0) {
					if(this.pointList){
						this.pointList.clearSelect();
					}
					Ext.Msg.alert("提示", "请先选择要导入的项目");
					return;
				}
				var lbid = this.opener.opener.midiModules["checkPointList"].getSelectedRecord().data.LBID;//类别id
				var bwid = this.opener.opener.midiModules["checkPointList"].getSelectedRecord().data.BWID;//部位id
				var store = this.opener.grid.getStore();// 关系中已经存在的项目store
				// 判断选中的部位是否已存在，已存在就不给于导入
				for (var i = 0; i < records.length; i++) {
					var selectXmid = records[i].data.XMID;//选中的项目id
					var selectXmmc = records[i].data.XMMC;//选中的项目名称
					for (var j = 0; j < store.getCount(); j++) {
						var xmid = store.getAt(j).get("XMID");//原先存在的项目id
						if (selectXmid == xmid) {
							Ext.Msg.alert("提示", selectXmmc + "已存在,请取消后导入");
							return
						}
					}
				}
				for (var i = 0; i < records.length; i++) {
					var xmid = records[i].data.XMID;
					var xmmc = records[i].data.XMMC;
					var pydm = records[i].data.PYDM;
					var bz = records[i].data.BZ;
					var record = new Ext.data.Record();
					record.set("LBID",lbid);
					record.set("BWID",bwid);
					record.set("XMID",xmid);
					record.set("XMMC",xmmc);
					record.set("PYDM",pydm);
					record.set("BZ",bz);
					this.opener.store.add(record);
				}
				this.projectList.clearSelect();//清空选择
				this.getWin().hide();//隐藏
				
			}
		})
