$package("phis.application.sup.script");

$import("phis.script.SimpleModule", "phis.script.widgets.Spinner",
          "phis.script.widgets.Strategy");
phis.application.sup.script.SecondaryStorageOfMaterialsModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.SecondaryStorageOfMaterialsModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.sup.script.SecondaryStorageOfMaterialsModule,
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
							items : [{
										layout : "fit",
										ddGroup : "firstGrid",
										border : false,
										split : true,
										region : 'west',
										width : this.width,
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
			getUList : function() {
				this.uCheckInList = this.createModule(
						"undeterminedCheckInList", this.refUList);
				this.uCheckInList.on("save", this.onSave, this);
				this.uCheckInList.oper = this;
				this.uCheckInList.requestData.pageNo = 1;
				var addCnd = ['eq', ['$', 'KFXH'],
						['i', this.mainApp['phis'].treasuryId]];
				this.uCheckInList.requestData.cnd = ['and', addCnd,
						['eq', ['$', 'DJZT'], ['i', 0]]];
				this.uCheckInList.initCnd = ['and', addCnd,
						['eq', ['$', 'DJZT'], ['i', 0]]];
				return this.uCheckInList.initPanel();
			},
			getList : function() {
				this.checkInList = this.createModule("checkInList",
						this.refList);
				this.checkInList.on("save", this.onSave, this);
				this.checkInList.oper = this;
				var addCnd = ['eq', ['$', 'KFXH'],
						['i', this.mainApp['phis'].treasuryId]];
				this.checkInList.requestData.pageNo = 1;
				this.checkInList.requestData.cnd = ['and', addCnd,
						['eq', ['$', 'DJZT'], ['i', 2]]];
				this.checkInList.initCnd = ['and', addCnd,
						['eq', ['$', 'DJZT'], ['i', 2]]];
				return this.checkInList.initPanel();
			},
			afterOpen : function() {
				if (!this.checkInList || !this.uCheckInList) {
					return;
				}
				var firstGrid = this.checkInList.grid;
				var grid = this.uCheckInList;
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
				this.checkInList.refresh();
				this.uCheckInList.refresh();
			}
		});