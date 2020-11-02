$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.ClinicProjectComboUseList = function(cfg) {
	cfg.cnds = ['eq',['$','SSLB'],['i',1]]; //modifyied by zhangxw
	cfg.initCnd = ['eq',['$','SSLB'],['i',1]];	//modifyied by zhangxw
	phis.application.cic.script.ClinicProjectComboUseList.superclass.constructor
			.apply(this, [cfg])

}
Ext.extend(phis.application.cic.script.ClinicProjectComboUseList,
		phis.script.SimpleList, {
//			openModule : function(cmd, r, xy) {
//				phis.application.cic.script.ClinicProjectComboUseList.superclass.openModule.call(this, cmd, r, xy);
//				var module = this.midiModules[cmd];
//				module.SSLB=this.SSLB;
//			}
		});
						