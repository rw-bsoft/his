$package("phis.application.sup.script");

$import("phis.script.SimpleModule", "phis.script.widgets.Spinner","phis.script.widgets.Strategy");

phis.application.sup.script.ResetBusinessModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.ResetBusinessModule.superclass.constructor.apply(this,[cfg]);
	//this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.sup.script.ResetBusinessModule, phis.script.SimpleModule,
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
				var radioGroup = new Ext.form.RadioGroup({
					height : 20,
					width : 520,
		            id : 'zblbcz',
				  //	name : 'zblbcz',ֵ
					value : zblbItems[0].initialConfig.inputValue,
					items : zblbItems,
					listeners : {
						change : function(group, newValue, oldValue) {
							if (this.resetBusinessUList) {
								this.resetBusinessUList.zblb = newValue.inputValue;
								this.resetBusinessUList.doRefreshWin();
							}
							if (this.resetBusinessList) {
								this.resetBusinessList.zblb = newValue.inputValue;
								this.resetBusinessList.doRefreshWin();
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
							tbar : ['', radioGroup],
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
				this.resetBusinessUList = this.createModule( "resetBusinessUList", this.refUList);
				this.resetBusinessUList.on("save", this.onSave, this);
				this.resetBusinessUList.zblb = this.radioGroup.value;
				return this.resetBusinessUList.initPanel();
			},
			getList : function() {
				this.resetBusinessList = this.createModule("resetBusinessList",this.refList)
				this.resetBusinessList.on("save", this.onSave, this);
			   this.resetBusinessList.zblb = this.radioGroup.value;
				return this.resetBusinessList.initPanel();
			},
			afterOpen : function() {
				if (!this.resetBusinessList || !this.resetBusinessUList) {
					return;
				}
				var firstGrid = this.resetBusinessList.grid;
				var grid = this.resetBusinessUList;
				var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'firstGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								var records = ddSource.dragData.selections;
								grid.doComit();
								return true
							}
						});
			},
			onSave : function(){
				this.resetBusinessList.doRefreshWin();
				this.resetBusinessList.doRefreshWin();
			},
			getZblb : function() {
				var kfxh = this.mainApp['phis'].treasuryId;
				var body = {};
				body["KFXH"] = kfxh;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "resetBusinessService",
							serviceAction : "getZblbByKfxh",
							body : body
						});
				var kfzblb = [];
				kfzblb = r.json.list;
				var items = [];
				for (var i = 0; i < kfzblb.length; i++) {
					var item = new Ext.form.Radio({
								boxLabel : kfzblb[i][1],
								name : 'zblbcz',
								inputValue : kfzblb[i][0]
							})
					items.push(item);
				}
				return items;
			}
			
		});