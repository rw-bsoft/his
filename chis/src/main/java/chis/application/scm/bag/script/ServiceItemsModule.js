$package("chis.application.scm.bag.script")
$import("chis.script.BizCombinedModule2")
chis.application.scm.bag.script.ServiceItemsModule = function(cfg) {
	chis.application.scm.bag.script.ServiceItemsModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.scm.bag.script.ServiceItemsModule,
		chis.script.BizCombinedModule2, {
			getPanelItems : function() {
				var firstItem = this.getFirstItem();
				var secondItem = this.getSecondItem();
				this.getModules();
				this.temp_r;
				var items = [{
							layout : "fit",
							border : false,
							frame : false,
							split : true,
							title : '',
							region : this.layOutRegion,
							width : 400,
							collapsible : false,
							items : firstItem
						}, {
							layout : "fit",
							border : false,
							frame : false,
							split : true,
							title : '',
							region : 'center',
							items : secondItem
						}]
				return items;
			},
			getModules : function() {
				this.leftList = this.midiModules[this.actions[0].id]
				this.rightList = this.midiModules[this.actions[1].id]
				this.leftList.opener = this;
				this.rightList.opener = this;
				this.leftList.on("leftDbClick",this.sendData,'l')

			},
			loadData : function(){
				this.leftList.SPID = this.SPID||"null";
				this.rightList.SPID = this.SPID||"null";
                this.rightList.requestData.cnd=['eq',['$','a.SPID'],['s',this.rightList.SPID]];
				this.rightList.loadData();
			}
	});