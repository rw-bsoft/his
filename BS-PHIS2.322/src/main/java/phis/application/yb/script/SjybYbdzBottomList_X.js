$package("phis.application.yb.script");
$import("phis.script.SimpleList","phis.script.Phisinterface")
phis.application.yb.script.SjybYbdzBottomList_X = function(cfg){
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false ;
	Ext.apply(this,phis.script.Phisinterface);
	phis.application.yb.script.SjybYbdzBottomList_X.superclass.constructor.apply(this,[cfg]);
};
Ext.extend(phis.application.yb.script.SjybYbdzBottomList_X,phis.script.SimpleList,{
	getCndBar : function(items) {
		return ["<h1 style='text-align:center'>新省医保对账</h1>"];
	},
	loadYbDzbdData : function(mzxx,zyxx) {
		this.clear();
		this.requestData.pageNo = 1;
		var body = {}
		body["mzxx"] = mzxx;
		body["zyxx"] = zyxx;
		this.requestData.serviceId = this.serviceId ;
		this.requestData.serviceAction = "queryXForYbdzList";
		this.requestData.body = body
		if (this.store) {
			if (this.disablePagingTbr) {
				this.store.load()
			} else {
				var pt = this.grid.getBottomToolbar()
				if (this.requestData.pageNo == 1) {
					pt.cursor = 0;
				}
				pt.doLoad(pt.cursor)
			}
		}
		this.resetButtons();
	}
});
