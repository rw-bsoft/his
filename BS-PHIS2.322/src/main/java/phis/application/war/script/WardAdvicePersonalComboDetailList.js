$package("phis.application.war.script")
$import("phis.script.SimpleList")

phis.application.war.script.WardAdvicePersonalComboDetailList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	phis.application.war.script.WardAdvicePersonalComboDetailList.superclass.constructor
			.apply(this, [cfg])

}
Ext.extend(phis.application.war.script.WardAdvicePersonalComboDetailList,
		phis.script.SimpleList, {
			showColor : function(v, params, data) {
				var YPZH = data.get("YPZH") % 2 + 1;
				switch (YPZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				return "";
			}
		})