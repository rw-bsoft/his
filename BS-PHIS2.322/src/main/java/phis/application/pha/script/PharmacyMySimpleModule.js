/**
 * 药房模块,左右结构Module模版
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule", "phis.script.rmi.jsonRequest",
		"phis.script.widgets.Spinner", "phis.script.widgets.Strategy");

phis.application.pha.script.PharmacyMySimpleModule = function(cfg) {
	cfg.width=this.width =window.screen.width/2;
	this.leftTitle="leftTitle";//界面左边List标题
	this.rightTitle="rightTitle";//界面左边List标题
	cfg.serviceID="pharmacyManageService";
	cfg.initializationServiceActionID="initialization";
	phis.application.pha.script.PharmacyMySimpleModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave',this.doSave,this);
}
Ext.extend(phis.application.pha.script.PharmacyMySimpleModule,phis.script.SimpleModule,
		{
			//页面初始化
			initPanel : function(sc) {
					if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				//进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceID,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
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
										border : false,
										ddGroup : "firstGrid",
										split : true,
										title : this.leftTitle,
										region : 'west',
										width:this.width,
										items : this.getUList(),
										selModel : new Ext.grid.RowSelectionModel(
												{
													singleSelect : true
												})
									}, {
										layout : "fit",
										ddGroup : "secondGrid",
										border : false,
										split : true,
										title : this.rightTitle,
										region : 'center',
										items : this.getList(),
										selModel : new Ext.grid.RowSelectionModel(
												{
													singleSelect : true
												})
									}]
						});
				this.panel = panel;
				return panel;
			},
			//获取左边的list
			getUList : function() {
				this.leftList = this.createModule(
						"leftList", this.refLList);
				this.leftList.on("select",
						this.onSelect, this)
				return this.leftList.initPanel();
			},
			//获取右边的list
			getList : function() {
				this.rightList = this.createModule("rightList",
						this.refRList);
				return this.rightList.initPanel();
			},
			//选中条件下拉框
			onSelect : function(record) {
				this.leftList.selectValue = parseInt(record.data.key);
				this.leftList.doRefreshWin();
				this.rightList.selectValue = parseInt(record.data.key);
				this.rightList.doCndQuery();
			},
			//打开界面后,主要用于拖动操作
			afterOpen : function() {
				if(!this.leftList||!this.rightList||!this.rightList.grid){return;}
				// 拖动操作
				var firstGrid = this.rightList.grid;
				var grid=this.leftList;
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