$package("phis.application.xnh.script");

$import("phis.script.SimpleModule");

phis.application.xnh.script.NhFyxxCompareModule = function(cfg) {
	this.exContext = {};
	phis.application.xnh.script.NhFyxxCompareModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.xnh.script.NhFyxxCompareModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							height : 500,
							items : [{
										title : "农合收费明细",
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 600,
										items : [this.getMainList()]
									}, {
										title : "本院收费明细",
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getAppendList()
									}],
							tbar : (this.tbar || []).concat(this.createButton())
						});
				panel.on("beforeclose", this.beforeclose, this);
				this.panel = panel;
				return panel;
			},
			createButton : function() {
				var buttons=[];
				buttons.push(new Ext.form.Label({
							text : "开始:"
						}))
				buttons.push(new Ext.form.DateField({
					        id : 'beginDate',
							name : 'beginDate',
							value : Date.getServerDate(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间'
						}))
				buttons.push(new Ext.form.Label({
							text : "到"
						}))
				buttons.push(new Ext.form.DateField({
							id : 'endDate',
							name : 'endDate',
							value : Date.getServerDate(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						}))
				buttons.push({
							xtype : "button",
							text : "查询",
							iconCls : "query",
							scope : this,
							handler : this.doquery
						})
				buttons.push({
							xtype : "button",
							text : "导出",
							iconCls : "print",
							scope : this,
							handler : this.doPrint
						})
				return buttons;
			},
			doquery: function() {
				var begdate=document.getElementById("beginDate").value;
				var enddate=document.getElementById("endDate").value;
				this.mainList.requestData.cnd =['and',['ge', ['$', "str(JSRQ,'yyyy-mm-dd')"],['s',begdate]],
											   ['le',['$', "str(JSRQ,'yyyy-mm-dd')"],['s',enddate]],
											   ['eq',['$','a.JGID'],["$",this.mainApp.deptId]]];
				this.mainList.loadData();
			},
			doPrint : function() {
				this.mainList.doPrint();
			},
			getMainList : function() {
				this.mainList = this.createModule("mainList", this.nhfyxxList);
				var _ctx = this;
				this.mainList.onRowClick = function() {
					_ctx.onListRowClick();
				};
				this.mainList.on("loadData", this.onListLoadData, this);
				return this.mainList.initPanel();
			},
			getAppendList : function() {
				this.appendList = this.createModule("appendList",
						this.sfxxList);
				return this.appendList.initPanel();
			},
			onListLoadData : function(store) {
				// 如果第一次打开页面，默认选中第一行
				this.mainList.selectRow(0);
				this.onListRowClick();
			},
			onListRowClick : function() {
				this.beforeclose();
				var r = this.mainList.getSelectedRecord();
				if (!r) {
					return;
				}
				this.appendList.requestData.cnd =  
						['eq', ['$', 'a.MZXH'], ['s', r.get("MZXH")]]
				this.appendList.loadData();
			},
			beforeclose : function() {
				
			},
			// 关闭
			doClose : function() {

			}
		});
