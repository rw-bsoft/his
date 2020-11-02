$package("phis.application.sup.script");

$import("phis.script.SimpleModule", "phis.script.widgets.Spinner","phis.script.widgets.Strategy");


phis.application.sup.script.InventoryInListModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.InventoryInListModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.sup.script.InventoryInListModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].treasuryId == null
						|| this.mainApp['phis'].treasuryId == ""
						|| this.mainApp['phis'].treasuryId == undefined) {
					Ext.Msg.alert("提示", "未设置登录库房,请先设置");
					return null;
				}
				if (this.mainApp['phis'].treasuryEjkf != 0) {
					Ext.MessageBox.alert("提示", "该库房不是一级库房!");
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
				var actions = this.actions;
				var bar = [];
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					bar.push({
								boxLabel : ac.name,
								inputValue : ac.properties.value,
								name : "stackpdlr",
								clearCls : true
							})
				}
				this.radioGroup = new Ext.form.RadioGroup({
							width : 200,
							disabled : false,
							items : bar,
							listeners : {
								change : function(group, newValue, oldValue) {
									if (this.uInventoryList) {
										this.uInventoryList
												.doRefreshWinGlfs(newValue.inputValue);
										this.uInventoryList.GLFS = newValue.inputValue;
									}
									if (this.inventoryList) {
										this.inventoryList
												.doRefreshWinGlfs(newValue.inputValue);
										this.inventoryList.GLFS = newValue.inputValue;
									}
								},
								scope : this
							}
						});
				this.radioGroup.setValue(1);
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : ['', this.radioGroup],
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
				this.uInventoryList = this.createModule("uInventoryList",
						this.refUList);
				this.uInventoryList.opener = this;
				this.uInventoryList.on("save", this.onSave, this);
				this.uInventoryList.GLFS = 1;
				return this.uInventoryList.initPanel();
			},
			getList : function() {
				this.inventoryList = this.createModule("inventoryList",
						this.refList);
				this.inventoryList.opener = this;
				this.inventoryList.on("save", this.onSave, this);
				this.inventoryList.GLFS = 1;
				return this.inventoryList.initPanel();
			},
			onSave : function() {
				this.inventoryList.refresh();
				this.uInventoryList.refresh();
			},
			afterOpen : function() {
				if (!this.inventoryList || !this.uInventoryList) {
					return;
				}
				// 拖动操作
				var firstGrid = this.inventoryList.grid;
				var grid = this.uInventoryList;
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