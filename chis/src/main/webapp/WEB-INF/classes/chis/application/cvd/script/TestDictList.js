$package("chis.application.cvd.script");
$import("chis.script.BizSimpleListView",
		"chis.application.cvd.script.TestList");
chis.application.cvd.script.TestDictList = function(cfg) {
	chis.application.cvd.script.TestDictList.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.cvd.script.TestDictList, chis.script.BizSimpleListView, {

		});