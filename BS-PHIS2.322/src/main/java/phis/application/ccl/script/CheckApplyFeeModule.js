$package("phis.application.ccl.script");
$import("phis.script.SimpleModule")

phis.application.ccl.script.CheckApplyFeeModule = function(cfg) {
	phis.application.ccl.script.CheckApplyFeeModule.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.ccl.script.CheckApplyFeeModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : "检查项目",
										region : 'west',
										width : "28%",
										items : this.getCheckList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "公用项目组套(双击查看明细)",
										region : 'center',
										items : this.getStackList(),
										tbar : this.getCenterTbar()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "费用绑定",
										region : 'east',
										width : "35%",
										items : this.getFeeBoundList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getCheckList : function() {
				this.checkList = this.createModule("checkList",
						this.refCheckList);
				this.checkList.opener = this;
				this.checkGrid = this.checkList.initPanel();
				return this.checkGrid;

			},
			getStackList : function() {
				this.stackList = this.createModule("stackList",
						this.refStackList);
				this.stackList.opener = this;
				this.stackGrid = this.stackList.initPanel();
				return this.stackGrid;

			},
			getFeeBoundList : function() {
				this.feeBoundList = this.createModule("feeBoundList",
						this.refFeeBoundList);
				this.feeBoundList.opener = this;
				this.feeBoundGrid = this.feeBoundList.initPanel();
				return this.feeBoundGrid;
			},
			getCenterTbar : function() {
				var tbar = [];
				tbar.push(new Ext.form.Label({
							text : "拼音代码"
						}));
				tbar.push(new Ext.form.TextField({
							id : "tcpydm",
							width : 100
						}));
				tbar.push({
							xtype : "button",
							text : "查询",
							iconCls : "query",
							scope : this.stackList,
							handler : this.stackList.refresh
						});
				tbar.push({
							xtype : "button",
							text : "添加",
							iconCls : "add",
							scope : this.stackList,
							handler : this.stackList.doAdd
						});
				return tbar;
			}
		})
