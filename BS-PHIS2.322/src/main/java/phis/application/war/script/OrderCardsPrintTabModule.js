/**
 * 医嘱卡片Tab
 *     该JS类中主要加载页面上的"按病人List"和"多种卡片类型的Js"两个Tab页
 *     并控制一些属性
 * @author : liws
 */
$package("phis.application.war.script")

$import("phis.script.SimpleModule")

phis.application.war.script.OrderCardsPrintTabModule = function(cfg) {
	phis.application.war.script.OrderCardsPrintTabModule.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.war.script.OrderCardsPrintTabModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 215,
										collapsible : false,
										items : this.getSettlementList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getSettlementModule()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getSettlementList : function() {
				var module = this.createModule("OrderCardsList",
						this.refList);
				module.on("dblClick", this.doDblClick, this);
				module.opener = this;
				var list = module.initPanel();
				module.requestData.wardId = this.mainApp['phis'].wardId;
				module.refresh();
				
				this.list = module;
				return list
			},
			getSettlementModule : function() {
				var module = this.createModule("OrderCardsTypeTabModule",
						this.refModule);
				module.on("settlement", this.settlement, this);
				this.module = module;
				module.opener = this;
				return module.initPanel();
			},
			doDblClick : function(data) {
				var form = this.module.form.form.getForm();
				if (this.JSLX) {
					data.JSLX = this.JSLX
				} else {
					data.JSLX = 5;
				}
				// this.module.doChoose(data);
				this.doCommit(data);
			},
			doCommit : function(data) {
				if (!data) {
					return;
				}
				this.module.doFillIn(data);
			},
			doQuery : function(data) {
				this.list.doQuery(data)
			},
			settlement : function(){
				this.list.doRefresh()
			},
			doNew : function(){
				this.module.doNew(this.JSLX);
			}
		});