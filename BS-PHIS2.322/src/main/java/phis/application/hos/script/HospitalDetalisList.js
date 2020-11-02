$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalDetalisList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.height = 181
	cfg.showRowNumber = false;
	phis.application.hos.script.HospitalDetalisList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalDetalisList,
		phis.script.SimpleList, {
	loadData : function(data){
		var records = new Array();
		for (var i = 0; i < data.length; i++) {
			var ddata = data[i];
//			if(data.CFLX == ddata.CFLX && data.CFSB == ddata.CFSB){
				records.push(new Ext.data.Record(ddata));
//			}
		}
		this.store.removeAll();
		this.store.add(records);
	},
	doCancel : function(){
		this.opener.getWin().hide();
	}
		});