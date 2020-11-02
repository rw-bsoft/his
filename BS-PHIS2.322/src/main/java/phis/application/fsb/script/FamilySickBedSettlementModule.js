$package("phis.application.fsb.script")

$import("phis.script.SimpleModule")

phis.application.fsb.script.FamilySickBedSettlementModule = function(cfg) {
	phis.application.fsb.script.FamilySickBedSettlementModule.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedSettlementModule,
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
										width : 240,
										collapsible : true,
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
				var module = this.createModule("refSettlementList",
						this.refList);
				module.on("dblClick", this.doDblClick, this);
				this.list = module;
				module.opener = this;
				var list = module.initPanel();
				module.schema = 'JC_BRRY_CYLB_ZC';
				module.requestData.cnd = ['and',
						['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['eq', ['$', 'a.CYPB'], ['i', 1]]];
				module.refresh();
				return list
			},
			getSettlementModule : function() {
				var module = this.createModule("refSettlementModule",
						this.refModule);
				module.on("settlement", this.settlement, this);
				this.module = module;
				module.opener = this;
				return module.initPanel();
			},
			doDblClick : function(data) {
				var form = this.module.form.form.getForm();
				if(!data.JSBS){
					if (this.JSLX) {
						data.JSBS = data.JSLX;//将该病人结算类型传值到后台
						data.JSLX = this.JSLX
					} else {
						data.JSBS = data.JSLX;
						data.JSLX = 5;
					}
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