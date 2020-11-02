/**
 * 个人基本信息列表
 * 
 * @author tianj
 */
$package("chis.application.mpi.script");

$import("chis.script.BizSimpleListView", "chis.application.mpi.script.EMPIInfoModule",
		"chis.application.mpi.script.CardList", "chis.application.mpi.script.ExpertQuery");

chis.application.mpi.script.EMPIList = function(cfg) {
	cfg.initCnd = ['eq', ['$', 'status'], ['s', '0']];
	chis.application.mpi.script.EMPIList.superclass.constructor.apply(this, [cfg]);
	this.width = 800;
	this.serviceId = "chis.empiService";
	this.height = 600;
}

Ext.extend(chis.application.mpi.script.EMPIList, chis.script.BizSimpleListView, {
	doExpertQuery : function() {
		var expertQuery = this.midiModules["EMPI.ExpertQuery"];
		if (!expertQuery) {
			expertQuery = new chis.application.mpi.script.ExpertQuery({});
			expertQuery.on("empiSelected", this.gotEmpi, this);
			this.midiModules["EMPI.ExpertQuery"] = expertQuery;
		}
		expertQuery.getWin().show();
	},

	doNew : function() {
		var createView = this.midiModules["EMPI.CreateView"];
		if (!createView) {
			var createView = new chis.application.mpi.script.EMPIInfoModule({
						serviceAction : "submitPerson",
						title : "个人基本信息新建",
						modal : true,
						mainApp : this.mainApp
					});
			this.midiModules["EMPI.CreateView"] = createView;
			createView.on("onSuccessed", function() {
						this.refresh()
					}, this);
			createView.on("onEmpiReturn", function(empi) {

					}, this);
		}
		var win = createView.getWin();
		win.setPosition(250, 100);
		win.show();
	},

	doModify : function() {
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		var updateView = this.midiModules["EMPI.UpdateView"];
		if (!updateView) {
			var updateView = new chis.application.mpi.script.EMPIInfoModule({
						serviceAction : "updatePerson",
						title : "个人基本信息修改",
						mainApp : this.mainApp
					});
			updateView.on("onSuccessed", function() {
						this.refresh()
					}, this);
			updateView.on("returnEmpiId", function(empi) {

					}, this);
			this.midiModules["EMPI.UpdateView"] = updateView;
		}
		updateView.getWin().setPosition(250, 100);
		updateView.getWin().show();
		updateView.setRecord(r.id);
	},

	doCard : function() {
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		var cardView = this.midiModules["EMPI.CardView"];
		if (!cardView) {
			var cardView = new chis.application.mpi.script.CardList({
						mainApp : this.mainApp
					});
			this.midiModules["EMPI.CardView"] = cardView;
		}
		cardView.getWin().setPosition(200, 150);
		cardView.getWin().show();
		cardView.setRecord(r);
	},

	gotEmpi : function(empi) {
		this.store.removeAll();
		this.store.add(empi);
	},

	onDblClick : function(grid, index, e) {
		this.doModify();
	}
});
