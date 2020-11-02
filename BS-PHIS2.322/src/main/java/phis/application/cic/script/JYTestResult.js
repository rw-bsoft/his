$package("phis.application.cic.script")

$import("phis.script.SelectList")

phis.application.cic.script.JYTestResult = function(cfg) {
	cfg.autoLoadData = true;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = false;
	cfg.entryName = this.entryName || "phis.application.pub.schemas.V_LIS_TESTRESULT";
	phis.application.cic.script.JYTestResult.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.cic.script.JYTestResult, phis.script.SelectList, {
	doCancel : function() {
		this.fireEvent("cancel", this);
	},
	doRefresh : function() {
		var cnd = this.getCnd();
		this.requestData.cnd = cnd;
		this.initCnd = cnd
		this.loadData();
	},
	onLoadData : function() {
		this.clearSelect();
	},
	getCnd : function() {
		var cnd1;
		if (this.ZYH) {
			cnd1 = ['eq', ['$', 'a.VISITID'], ['i', this.ZYH]];
		}
		var cnd2
		if (this.ZYH) {
			cnd2 = ['eq', ['$', 'a.PATIENTID'], ['i', this.ZYHM]];
		}
		var cnd
		if(cnd1 && cnd1.length >1){
			cnd=cnd1;
		}
		if(cnd && cnd.length > 1 ){
			if(cnd2 && cnd2.length > 1 ){
				cnd=['and',cnd1,cnd2];
			}
		}else if(cnd2 && cnd2.length > 1){
			cnd=cnd2;
		}
		return cnd;
	},
	doAppoint : function() {
		var records = this.getSelectedRecords();
		if (records == null || records.length == 0) {
			return
		}
		var data = "";
		for (var i = 0; i < records.length; i++) {
			var str = ""
			var r = records[i];
			var CHINESENAME = r.get("CHINESENAME") || "";
			var TESTRESULT = r.get("TESTRESULT") || "";
			var UNIT = r.get("UNIT") || "";
			var HINT = r.get("HINT") || "";
			str = CHINESENAME + ":" + TESTRESULT + UNIT+ HINT +",";
			data += str;
//			if (i != (records.length - 1)) {
//				data += "\n";
//			}
		}
		this.fireEvent("appoint", data, 2);
	}
});