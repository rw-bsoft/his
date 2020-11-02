/**
 * 物资出库管理
 * 
 * @author shiwy
 */
$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.MaterialsOutModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.MaterialsOutModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.sup.script.MaterialsOutModule,
				phis.script.SimpleModule,
				{

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

						// 账簿类别
						var zblbItems = this.getZblb();
						var radioGroup = new Ext.form.RadioGroup(
								{
									height : 20,
									width : 768,
									id : 'zblbck',
									name : 'zblbck', // 后台返回的JSON格式，直接赋值
									value : zblbItems[0].initialConfig.inputValue,
									items : zblbItems,
									listeners : {
										change : function(group, newValue,
												oldValue) {
											if (this.materialsUndeterminedOutList) {
												this.materialsUndeterminedOutList.zblb = newValue.inputValue
												this.materialsUndeterminedOutList
														.doRefreshWin();
											}
											if (this.materialsOutList) {
												this.materialsOutList.zblb = newValue.inputValue
												this.materialsOutList
														.doRefreshWin();
											}
										},
										scope : this
									}
								});
						this.radioGroup = radioGroup;
						if (this.panel) {
							return this.panel;
						}
						var schema = sc
						if (!schema) {
							var re = util.schema.loadSync(this.entryName)
							if (re.code == 200) {
								schema = re.schema;
							} else {
								this.processReturnMsg(re.code, re.msg,
										this.initPanel)
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
							tbar : [ '', radioGroup ],
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
					// 未确定入库界面
					getUList : function() {
						this.materialsUndeterminedOutList = this.createModule(
								"materialsUndeterminedOutList", this.refUList);
						this.materialsUndeterminedOutList.oper = this;
						this.materialsUndeterminedOutList.on("save",
								this.onSave, this);
						this.materialsUndeterminedOutList.zblb = this.radioGroup.value;
						return this.materialsUndeterminedOutList.initPanel();
					},
					// 确定入库界面
					getList : function() {
						this.materialsOutList = this.createModule(
								"materialsOutList", this.refList)
						this.materialsOutList.oper = this;
						this.materialsOutList.on("save", this.onSave, this);
						this.materialsOutList.zblb = this.radioGroup.value;
						return this.materialsOutList.initPanel();
					},
					afterOpen : function() {
						if (!this.materialsOutList
								|| !this.materialsUndeterminedOutList) {
							return;
						}
						// 拖动操作
						var firstGrid = this.materialsOutList.grid;
						var grid = this.materialsUndeterminedOutList;
						var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
						var firstGridDropTarget = new Ext.dd.DropTarget(
								firstGridDropTargetEl,
								{
									ddGroup : 'firstGridDDGroup',
									notifyDrop : function(ddSource, e, data) {
										var records = ddSource.dragData.selections;
										grid.doComit();
										return true
									}
								});
					},
					getZblb : function() {
						var kfxh = this.mainApp['phis'].treasuryId;
						var body = {};
						body["KFXH"] = kfxh;
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getZblbByKfxh",
							body : body
						});
						var kfzblb = [];
						kfzblb = r.json.list;
						var items = [];
						for ( var i = 0; i < kfzblb.length; i++) {
							var item = new Ext.form.Radio({
								boxLabel : kfzblb[i][1],
								name : 'zblbck',
								inputValue : kfzblb[i][0]
							})
							items.push(item);
						}
						return items;
					},
					onSave : function() {
						this.materialsUndeterminedOutList.refresh();
						this.materialsOutList.refresh();
					}
				});