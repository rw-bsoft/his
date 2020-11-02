$package("phis.application.fsb.script");

$import("phis.script.SimpleModule");
phis.application.fsb.script.FamilySickBedMedicalBackApplicationRightModule = function(cfg) {
	 this.exContext={};
	 this.width = 1024;
	this.height = 550;
	phis.application.fsb.script.FamilySickBedMedicalBackApplicationRightModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.fsb.script.FamilySickBedMedicalBackApplicationRightModule,
		phis.script.SimpleModule, {
				initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							height:this.height,
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
										height : 220,
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
				this.topList = this.createModule("refTopList", this.refTopList);
				this.topList.on("leftLoad",this.onLeftLoad,this);
				return this.topList.initPanel();
			},
			getUnderList : function() {
				this.underList = this.createModule("refUnderList", this.refUnderList);
				return this.underList.initPanel();
			},
			onLeftLoad:function(tag){
			this.fireEvent("leftLoad", tag);
			}
		});