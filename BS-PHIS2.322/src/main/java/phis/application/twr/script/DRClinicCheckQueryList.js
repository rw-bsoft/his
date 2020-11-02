$package("phis.application.twr.script")

$import("phis.script.SimpleList")

/**
 *@author : chzhxiang
 *@date : 2013.08.14
 */
phis.application.twr.script.DRClinicCheckQueryList = function(cfg) {
	phis.application.twr.script.DRClinicCheckQueryList.superclass.constructor.apply(this, [cfg])
}
var recordIds = {};
var datas = {};

Ext.extend(phis.application.twr.script.DRClinicCheckQueryList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.cnd = ['eq',['$','empiId'],['s',this.exContext.empiId]];
				this.clear();
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
			},
			
			onDblClick : function(){
				var r = this.getSelectedRecord();
				var m = this.midiModules["ClinicXxCheck"];
				if (!m) {
					$import("phis.application.drc.script.ClinicXxCheckModule");
				}
					m = new phis.application.twr.script.ClinicXxCheckModule({
								entryName : "phis.application.twr.schemas.DR_ClinicXxCheckHistory",
								title : "检查申请信息",
								height : 450,
								modal : true,
								mainApp : this.mainApp,
								baseInfo : r.data
							});
				this.midiModules["ClinicXxCheck"] = m;
				var win = m.getWin();
				win.setPosition(300, 100);
				win.show();
			}
		})