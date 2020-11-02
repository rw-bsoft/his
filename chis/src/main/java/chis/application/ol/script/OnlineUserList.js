$package("chis.application.ol.script")

$import("chis.script.BizSimpleListView")

chis.application.ol.script.OnlineUserList = function(cfg) {
	chis.application.ol.script.OnlineUserList.superclass.constructor.apply(this,
			[cfg]);
	this.requestData.serviceId = "chis.onlineHandler";
}
Ext.extend(chis.application.ol.script.OnlineUserList, chis.script.BizSimpleListView, {
	doPrint : function() {
		var cm = this.grid.getColumnModel()
		var pWin = this.midiModules["printView"]
		var cfg = {
			title : this.title,
			requestData : this.requestData,
			cm : cm,
			dataInCtx:true
		}
		if (pWin) {
			Ext.apply(pWin, cfg)
			pWin.getWin().show()
			return
		}
		pWin = new app.modules.list.PrintWin(cfg)
		this.midiModules["printView"] = pWin
		pWin.getWin().show()
	}
})