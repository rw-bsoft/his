/**
 * 儿童意外情况列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.accident")
$import("chis.script.BizSimpleListView")
chis.application.cdh.script.accident.ChildrenAccidentList = function(cfg) {
	cfg.inModule = true;
	cfg.enableCnd = false;
	cfg.actions = {};
	chis.application.cdh.script.accident.ChildrenAccidentList.superclass.constructor.apply(
			this, [cfg]);
	this.selectedIndex = 0
	this.on("onGetCM", this.onGetCM, this)
}

Ext.extend(chis.application.cdh.script.accident.ChildrenAccidentList,
		chis.script.BizSimpleListView, {

			onGetCM : function(it) {
				if (this.inModule) {
					if (it.id != "accidentDate" && it.id != "accidentType") {
						return false;
					}
				}
			},

			loadData : function() {
				this.initCnd = ["eq", ["$", "phrId"],
						["s", this.exContext.ids["CDH_HealthCard.phrId"]]]
				this.requestData.cnd = this.initCnd
				chis.application.cdh.script.accident.ChildrenAccidentList.superclass.loadData
						.call(this);
			}

		});