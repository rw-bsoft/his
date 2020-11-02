$package("chis.application.tr.script.pmh");

$import("chis.script.BizCombinedModule2");

chis.application.tr.script.pmh.TumourHealthQuestionView = function(cfg){
	chis.application.tr.script.pmh.TumourHealthQuestionView.superclass.constructor.apply(this,[cfg]);
	this.width=980;
	this.height = 600;
	this.itemWidth = 160;
	this.itemHeight=600;
	this.frame=true;
}

Ext.extend(chis.application.tr.script.pmh.TumourHealthQuestionView,chis.script.BizCombinedModule2,{
	initPanel : function() {
		var panel = chis.application.tr.script.pmh.TumourHealthQuestionView.superclass.initPanel.call(this);
		this.panel = panel;
		this.list = this.midiModules[this.actions[0].id];
		this.list.on("loadData", this.onLoadGridData, this);
		this.grid = this.list.grid;
		this.grid.on("rowClick", this.onRowClick, this);
		this.THQ = this.midiModules[this.actions[1].id];
		return panel;
	},
	loadData : function(){
		if(this.list){
			Ext.apply(this.list.exContext,this.exContext);
			var initCnd = ['eq',['$','empiId'],['s',this.exContext.args.empiId||'']]
			this.list.requestData.cnd=initCnd;
			this.list.loadData();
		}
		if(this.THQ){
			Ext.apply(this.THQ.exContext,this.exContext);
		}
	},
	onLoadGridData : function(store){
		if (this.gcId) {
			for (var i = 0; i < store.getCount(); i++) {
				var r = store.getAt(i);
				if (r.get("gcId") == this.gcId) {
					this.grid.getSelectionModel().selectRecords([r]);
					var n = store.indexOf(r);
					if (n > -1) {
						this.list.selectedIndex = n;
					}
					break;
				}
			}
		}
		if (!this.list.selectedIndex) {
			this.list.selectedIndex = 0;
		}
		var r = store.getAt(this.list.selectedIndex);
		this.process(r, this.list.selectedIndex);
	},
	onRowClick : function(grid, index, e){
		this.list.selectedIndex = index;
		var r = grid.store.getAt(index);
		this.process(r, index);
	},
	process : function(r, index) {
		if (!r) {
			return;
		}
		var gcId = r.get("gcId");
		var empiId = r.get("empiId");
		var masterplateId = r.get("masterplateId");
		var masterplateType = r.get("masterplateType");
		var JZXH = r.get("JZXH");
		this.exContext.args.gcId = gcId;
		this.exContext.args.empiId = empiId;
		this.exContext.args.masterplateId = masterplateId;
		this.exContext.args.masterplateType = masterplateType;
		this.exContext.args.JZXH = JZXH;
		if(this.THQ){
			Ext.apply(this.THQ.exContext,this.exContext);
			this.THQ.initDataId = null;
			this.THQ.loadData();
		}
	}
});