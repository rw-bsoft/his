$package("chis.application.pub.script");

$import("chis.script.BizSimpleListView");

chis.application.pub.script.FamilyDoctorList = function(cfg){
	cfg.saveServiceId="chis.simpleSave";
	cfg.loadServiceId="chis.simpleLoad";
	cfg.actions = [{
				id : "addDoctor",
				name : "新建",
				iconCls:"new"
			},{
				id : "update",
				name : "查看",
				iconCls : "update"
			}];
	chis.application.pub.script.FamilyDoctorList.superclass.constructor.apply(this,[cfg]);
};

Ext.extend(chis.application.pub.script.FamilyDoctorList,chis.script.BizSimpleListView,{
	doAddDoctor : function(){
		var m = this.midiModules["addFamilyDoctor"];
		if (!m) {
			$import("chis.application.pub.script.FamilyDoctorForm");
			m = new chis.application.pub.script.FamilyDoctorForm({
				entryName : "chis.application.pub.schemas.PUB_FamilyDoctor",
				title : "家庭医生维护",
				height : 450,
				modal : true,
				op:"create",
				mainApp : this.mainApp
			});
			this.midiModules["addFamilyDoctor"] = m;
		}
		m.initPanel();
		m.form.getForm().findField("familyTeamId").setValue(this.familyTeam);
		m.on("save",this.refresh, this);
		var win = m.getWin();
		win.setPosition(250, 100);
		win.show();
	}
});