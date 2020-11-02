$package("phis.application.sup.script");

$import("phis.script.SimpleModule", "phis.script.widgets.Spinner","phis.script.widgets.Strategy");

phis.application.sup.script.ConsumptionModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.ConsumptionModule.superclass.constructor.apply(this,
			[cfg]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.sup.script.ConsumptionModule, phis.script.SimpleModule, {

			initPanel : function(sc) {
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
										region : 'west',
										width : 560,
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
			// δȷ���������
			getUList : function() {
				this.uCheckOutList = this.createModule(
						"undeterminedcheckOutList", this.refUList);
				this.uCheckOutList.on("save", this.onSave, this);
//				this.uCheckOutList.requestData.cnd = ['and',
//						['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]],
//						['and', ['eq', ['$', 'DJLX'], ['i', 2]],
//								['eq', ['$', 'DJZT'], ['i', 0]]]];
                this.uCheckOutList.requestData.DJZT = 0;
				this.uCheckOutList.requestData.pageNo = 1;
				return this.uCheckOutList.initPanel();
			},
			// ȷ���������
			getList : function() {
				this.checkOutList = this.createModule("checkOutList",
						this.refList);
				this.checkOutList.on("save", this.onSave, this);
//				this.checkOutList.requestData.cnd = [
//						'and',
//						['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]],
//						['and', ['eq', ['$', 'DJLX'], ['i', 2]],
//								['eq', ['$', 'DJZT'], ['i', 2]]]];
				this.checkOutList.requestData.pageNo = 1;
				return this.checkOutList.initPanel();
			},
			afterOpen : function() {
				if (!this.checkOutList || !this.uCheckOutList) {
					return;
				}
				// �϶�����
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