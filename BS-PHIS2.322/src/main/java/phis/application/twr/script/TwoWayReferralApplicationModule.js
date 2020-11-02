$package("phis.application.twr.script");

$import("phis.script.SimpleModule");

phis.application.twr.script.TwoWayReferralApplicationModule = function(cfg) {
	phis.application.twr.script.TwoWayReferralApplicationModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.twr.script.TwoWayReferralApplicationModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					this.form.zhuanzhendh = this.zhuanzhendh;
					this.form.loadData();
					this.list.zhuanzhendh = this.zhuanzhendh;
					this.list.quhaomm = this.quhaomm;
					this.list.loadData();
//					var pages = "phis.prints.jrxml.RegisterReqOrder";
//					var url = "resources/" + pages
//							+ ".print?silentPrint=1&zhuanzhendh="
//							+ this.zhuanzhendh + "&quhaomm=" + this.quhaomm;
//					document.getElementById(this.list.frameId).src = url
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										width : 960,
										height : 280,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
										items : this.getList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				this.form = this.createModule("form", this.formModule);
				this.form.zhuanzhendh = this.zhuanzhendh;
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.listModule);
				this.list.zhuanzhendh = this.zhuanzhendh;
				this.list.quhaomm = this.quhaomm;
				this.list.grid = this.list.initPanel();
				return this.list.grid;
			},
			doPrint : function(){
				this.list.doPrint();
			}
		});