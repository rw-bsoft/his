$package("phis.application.war.script");

$import("phis.script.SimpleList")
phis.application.war.script.WardRemainInList_Out = function(cfg) {
	phis.application.war.script.WardRemainInList_Out.superclass.constructor.apply(this,[cfg]);
}
Ext.extend(phis.application.war.script.WardRemainInList_Out, phis.script.SimpleList, {
      loadData : function() {
				this.clear(); 
				this.requestData.serviceId = "phis.remainService";
				this.requestData.serviceAction = "queryOutList";
				this.requestData.warId = this.mainApp['phis'].wardId;
				phis.application.war.script.WardRemainInList_Out.superclass.loadData.call(this);
			},doClose:function(){
				
				
				var win = this.getWin();
				if (win)
					win.hide();
				
			}
		});