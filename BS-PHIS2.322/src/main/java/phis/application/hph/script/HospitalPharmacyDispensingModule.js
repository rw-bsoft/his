/**
 * 医嘱发药-下面module
 * 
 * @author caijy
 */
$package("phis.application.hph.script");

$import("phis.script.SimpleModule");

phis.application.hph.script.HospitalPharmacyDispensingModule = function(cfg) {
	this.exContext = {};
	phis.application.hph.script.HospitalPharmacyDispensingModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(phis.application.hph.script.HospitalPharmacyDispensingModule,
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
							items : [ {
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
							} ],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
						this.panel = panel;
						return panel;
					},
					getTopList : function() {
						this.topList = this.createModule("topList",
								this.refTopList);
						this.topList.on("recordClick", this.showRecord, this);
						this.topList.yzlx = 0
						return this.topList.initPanel();
					},
					getUnderList : function() {
						this.underList = this.createModule("underList",
								this.refUnderList);
						this.underList.on("recordClick",
								this.onUnderRecordClick, this);
						this.underList.on("recordClick_br",
								this.onUnderRecordClick_br, this);
						this.underList.on("clear", this.onClear, this);
						this.underList.on("BeforeLoadDataLeft",
								this.onBeforeLoadDataLeft, this);
						this.underList.on("LoadDataLeft", this.onLoadDataLeft,
								this);
						this.underList.yzlx = 0;
						return this.underList.initPanel();
					},
					// 上一级单击显示提交的发药记录
					showRecord : function(tjbq, fyfs) {
						// this.underList.requestData.cnd=['and',['eq',['$','a.FYFS'],['d',fyfs]],['and',['eq',['$','a.TJBQ'],['s',tjbq]],this.underList.cnds]];
						// this.underList.initCnd=['and',['eq',['$','a.FYFS'],['d',fyfs]],['and',['eq',['$','a.TJBQ'],['s',tjbq]],this.underList.cnds]];
						// this.underList.clearSelect();
						// this.underList.loadData();
						this.underList.fyfs = fyfs;
						this.underList.tjbq = tjbq;
						this.underList.showRecord();
						this.onClear();
					},
					// 单击发药记录显示明细(提交单)
					onUnderRecordClick : function(r) {
						this.fireEvent("recordClick", r)
					},
					// 单击发药记录显示明细(病人)
					onUnderRecordClick_br : function(r) {
						this.fireEvent("recordClick_br", r)
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
					},
					// 切换tab时候刷新右边的发药明细
					onClear : function() {
						this.fireEvent("clear");
					},
					onBeforeLoadDataLeft : function() {
						this.fireEvent("BeforeLoadDataLeft");
					},
					onLoadDataLeft : function() {
						this.fireEvent("LoadDataLeft");
					}
				});