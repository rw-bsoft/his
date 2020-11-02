/**
 * 物资出库确认（二级）
 * 
 * @author gaof
 */
$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.SecondaryMaterialsOutModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.SecondaryMaterialsOutModule.superclass.constructor
			.apply(this, [ cfg ]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.sup.script.SecondaryMaterialsOutModule,
		phis.script.SimpleModule, {

			initPanel : function(sc) {
				if (this.mainApp['phis'].treasuryId == null
						|| this.mainApp['phis'].treasuryId == ""
						|| this.mainApp['phis'].treasuryId == undefined) {
					Ext.Msg.alert("提示", "未设置登录库房,请先设置");
					return null;
				}
				if (this.mainApp['phis'].treasuryEjkf == 0) {
					Ext.MessageBox.alert("提示", "该库房不是二级库房!");
					return;
				}
				if (this.mainApp['phis'].treasuryCsbz != 1) {
					Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
					return;
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
					items : [ {
						layout : "fit",
						ddGroup : "firstGrid",
						border : false,
						split : true,
						region : 'west',
						width : this.width,
						items : this.getUList(),
						selModel : new Ext.grid.RowSelectionModel({
							singleSelect : true
						})
					}, {
						layout : "fit",
						ddGroup : "secondGrid",
						border : false,
						split : true,
						region : 'center',
						items : this.getList(),
						selModel : new Ext.grid.RowSelectionModel({
							singleSelect : true
						})
					} ]

				});
				this.panel = panel;
				return panel;
			},
			// 未确定出库界面
			getUList : function() {
				this.uCheckOutList = this.createModule(
						"undeterminedcheckOutList", this.refUList);
				this.uCheckOutList.on("save", this.onSave, this);
				this.uCheckOutList.oper = this;
				// this.uCheckOutList.requestData.cnd = ['and',
				// ['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]],
				// ['and', ['le', ['$', 'DJLX'], ['i', 4]],
				// ['eq', ['$', 'DJZT'], ['i', 0]]]];
				this.uCheckOutList.requestData.DJZT = 0;
				this.uCheckOutList.requestData.pageNo = 1;
				return this.uCheckOutList.initPanel();
			},
			// 确定出库界面
			getList : function() {
				this.checkOutList = this.createModule("checkOutList",
						this.refList);
				this.checkOutList.on("save", this.onSave, this);
				this.checkOutList.oper = this;
				// this.checkOutList.requestData.cnd = [
				// 'and',
				// ['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]],
				// ['and', ['le', ['$', 'DJLX'], ['i', 4]],
				// ['eq', ['$', 'DJZT'], ['i', 2]]]];
				this.checkOutList.requestData.DJZT = 2;
				this.checkOutList.requestData.pageNo = 1;
				return this.checkOutList.initPanel();
			},
			afterOpen : function() {
				if (!this.checkOutList || !this.uCheckOutList) {
					return;
				}
				// 拖动操作
				var firstGrid = this.checkOutList.grid;
				var grid = this.uCheckOutList;
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
			},
			onSave : function() {
				this.checkOutList.refresh();
				this.uCheckOutList.refresh();
			}
		});