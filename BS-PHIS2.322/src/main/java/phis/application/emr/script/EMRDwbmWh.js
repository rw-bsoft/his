$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRDwbmWh = function(cfg) {
	cfg.showRowNumber = false;
	phis.application.emr.script.EMRDwbmWh.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.emr.script.EMRDwbmWh, phis.script.SimpleList, {
			lbbmRender : function(v, params, reocrd) {
				var bh = reocrd.get("LBBH")
				var sj = reocrd.get("SJLBBH")
				if (bh != sj) {
					return "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + v;
				}
				return v;

			},
			lbmcRender : function(v, params, reocrd) {
				var bh = reocrd.get("LBBH")
				var sj = reocrd.get("SJLBBH")
				if (bh != sj) {
					return "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							+ v;
				}
				return v;
			},
			zgkfRender : function(v, params, reocrd) {
				if (v == "") {
					return "<span style='float:right;'>" + 0 + "</span>";
				}
				return "<span style='float:right;'>" + v + "</span>";
			},
			zfRender : function(v, params, reocrd) {
				if (v == "") {
					return "<span style='float:right;'>否</span>";
				}
				if (v == "是") {
					return "<span style='float:right;color:red;'>" + v
							+ "</span>";
				}
				return "<span style='float:right;'>" + v + "</span>";
			}
		})
