/**
 * 家床发药-左边
 * 
 * @author caijy
 */
$package("phis.application.fsb.script");

$import("phis.script.SimpleModule");

phis.application.fsb.script.FamilySickBedDispensingModule = function(cfg) {
	this.exContext = {};
	phis.application.fsb.script.FamilySickBedDispensingModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.fsb.script.FamilySickBedDispensingModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : true,
							frame : false,
							layout : 'border',
							defaults : {
								border : true
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										height : 250,
										items : this.getTopList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getUnderList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			getTopList : function() {
				this.topList = this.createModule("topList", this.refTopList);
				this.topList.on("recordClick", this.showRecord, this);
				this.topList.yzlx = 0
				return this.topList.initPanel();
			},
			getUnderList : function() {
				this.underList = this.createModule("underList",
						this.refUnderList);
				this.underList.on("recordClick", this.onUnderRecordClick, this);
				this.underList.yzlx = 0;
				return this.underList.initPanel();
			},
			// 上一级单击显示提交的发药记录
			showRecord : function(fyfs) {
				this.underList.requestData.cnd = ['and',['eq', ['$', 'a.FYFS'], ['d', fyfs]],this.underList.cnds];
				this.underList.initCnd = ['and',['eq', ['$', 'a.FYFS'], ['d', fyfs]],this.underList.cnds];
				this.underList.clearSelect();
				this.underList.loadData();
			},
			// 单击发药记录显示明细
			onUnderRecordClick : function(r) {
				this.fireEvent("recordClick", r)
			},
			// 获取发药数据
			getData : function() {
				return this.underList.getData();
			},
			// 发完药或者退病区后刷新页面
			doNew : function() {
				this.topList.yzlx = this.yzlx
				this.topList.refresh();
				this.underList.yzlx = this.yzlx
				this.underList.doNew();
			}
		});