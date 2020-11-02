$package("chis.application.pub.script");

$import("chis.script.BizSimpleListView");

chis.application.pub.script.FamilyTeamList = function(cfg){
	this.initCnd=['like',['$','a.manaunitId'],['$','%user.manageUnit.id']];
	chis.application.pub.script.FamilyTeamList.superclass.constructor.apply(this,[cfg]);
};

Ext.extend(chis.application.pub.script.FamilyTeamList,chis.script.BizSimpleListView,{
	doAddTeam : function(){
		var m = this.midiModules["addFamilyTeam"];
		if (!m) {
			$import("chis.application.pub.script.FamilyTeamForm");
			m = new chis.application.pub.script.FamilyTeamForm({
				entryName : "chis.application.pub.schemas.PUB_FamilyTeam",
				title : "家庭团队维护",
				height : 450,
				modal : true,
				op:"create",
				mainApp : this.mainApp
			});
			this.midiModules["addFamilyTeam"] = m;
		}
		m.initPanel();
		m.on("save", this.refresh, this);
		var win = m.getWin();
		win.setPosition(250, 100);
		win.show();
		var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getFamilyteamId",
							method : "execute"
						});
		if(result.json && result.json.familyteamId){
			m.form.getForm().findField("familyTeamId").setValue(result.json.familyteamId);
		}else{
			alert("未找到本机构的编码生成规则，请联系管理员！");
		}
	},
	doAddPerson:function(){
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		if(this.midiModules["addPerson"]){
			this.midiModules["addPerson"]=null;
		}
		var m = this.midiModules["addPerson"];
		if (!m) {
			$import("chis.application.pub.script.FamilyDoctorList");
			m = new chis.application.pub.script.FamilyDoctorList({
				entryName : "chis.application.pub.schemas.PUB_FamilyDoctor",
				title : "家庭成员维护",
				height : 450,
				modal : true,
				op:"create",
				mainApp : this.mainApp,
				initCnd:['eq',['$','a.familyTeamId'],['s',r.get('familyTeamId')]]
			});
			this.midiModules["addPerson"] = m;
		}
		m.initPanel();
		m.familyTeam={};
		m.familyTeam.key=r.get('familyTeamId');
		m.familyTeam.text=r.get('familyTeamName');
		m.on("save", this.refresh, this);
		var win = m.getWin();
		win.setPosition(250, 100);
		win.show();
		
//		var module = this.createSimpleModule("addPerson",this.refModule);
//		module.initCnd=['eq',['$','a.familyTeamId'],['s',r.get('familyTeamId')]];
//		module.initPanel();
//		module.familyTeam={};
//		module.familyTeam.key=r.get('familyTeamId');
//		module.familyTeam.text=r.get('familyTeamName');
//		module.requestData.cnd=['eq',['$','a.familyTeamId'],['s',r.get('familyTeamId')]];
//		this.showWin(module);
//		module.loadData();
	}
});