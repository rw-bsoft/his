$package("chis.application.tr.script.screening")

$import("chis.script.BizSimpleListView")

chis.application.tr.script.screening.TumourScreeningCheckResultList = function(cfg){
	chis.application.tr.script.screening.TumourScreeningCheckResultList.superclass.constructor.apply(this,[cfg]);
	this.height=558;
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.screening.TumourScreeningCheckResultList,chis.script.BizSimpleListView,{
	loadData : function(){
		var initCnd = ['eq',['$','empiId'],['s',this.exContext.args.empiId||'']]
		if(this.exContext.args.screeningId){
			initCnd = ['and',['eq',['$','screeningId'],['s',this.exContext.args.screeningId || '']],['eq',['$','empiId'],['s',this.exContext.args.empiId||'']]]
		}
		var cndStr = $encode(this.requestData.cnd);
		if(this.requestData.cnd && cndStr.indexOf("empiId") == -1){
			this.requestData.cnd = ['and', this.requestData.cnd, initCnd];
		}else{
			this.requestData.cnd = initCnd;
		}
		chis.application.tr.script.screening.TumourScreeningCheckResultList.superclass.loadData.call(this);
	},
	onLoadData : function(store){
		//set turnHighRisk button status
		var nature = this.exContext.args.nature;
		var bts = this.grid.getTopToolbar();
		var turnHighRiskBtn = bts.find("cmd","turnHighRisk")[0];
		if(turnHighRiskBtn){
			if(nature == '1'){
				turnHighRiskBtn.enable();
			}else{
				turnHighRiskBtn.disable();
			}
		}
	},
	doTurnHighRisk : function(){
		var r = this.getSelectedRecord();
		if(!r){
			//没有检查记录
			Ext.Msg.alert("提示","没有检查记录，不能转高危！");
			return;
		}
		//是否符合转高危标准
		var isAccordWithCriterion = false;
		var body = {
		   empiId:this.exContext.args.empiId,
		   highRiskType:this.exContext.args.highRiskType,
		   recordId : this.exContext.args.screeningId,
		   phrId : this.exContext.args.phrId,
		   highRiskSource : this.exContext.args.highRiskSource
		}
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.tumourCriterionService",
					serviceAction : "checkTHRCriterion",
					method:"execute",
					body : body
				});
		if (result.code != 200) {
			this.processReturnMsg(result.code, result.msg);
			return null;
		}
		var rsBody = result.json.body;
		var isAccordWithCriterion = rsBody.passport;
		var turnTHRSucceed =rsBody.turnTHRSucceed;
		if(turnTHRSucceed){
			MyMessageTip.msg("提示","该病人已转入高危人群管理，请完善其高危人群档案信息！",true);
			var THRID = rsBody.THRID;
			var tsData = this.exContext.args.tsData;
			tsData.THRID = THRID || '';
			this.refresh();
			this.showTHROfEhrViewWin(tsData);
		}else{
			if(!isAccordWithCriterion){
				MyMessageTip.msg("提示","该病人的初筛记录不符合高危标准，不能转高危",true);
			}else{
				MyMessageTip.msg("提示","转高危 失败 , 请重试",true);
			}
		}
//		if(isAccordWithCriterion){
//			var tsData = this.exContext.args.tsData;
//			this.showTHROfEhrViewWin(tsData);
//		}else{
//			Ext.Msg.alert("提示信息","该人员不符合转高危标准，不能转为肿瘤高危管理！");
//		}
	},
	showTHROfEhrViewWin : function(tsData) {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['T_01','T_02','T_03'];
		cfg.mainApp = this.mainApp;
		cfg.activeTab = 0;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["TumourHighRisk_EHRView"];
		if (!module) {
			$import("chis.script.EHRView");
			module = new chis.script.EHRView(cfg);
			this.midiModules["TumourHighRisk_EHRView"] = module;
			module.exContext.ids["empiId"] = tsData.empiId;
			module.exContext.ids["highRiskType"] = tsData.highRiskType.key;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = tsData.empiId;
			module.exContext.ids["highRiskType"] = tsData.highRiskType.key;
			module.refresh();
		}
		module.exContext.ids.recordStatus = this.recordStatus;
		module.exContext.args = {};
		module.exContext.args.screeningId = tsData.recordId;
		module.exContext.args.highRiskType = tsData.highRiskType.key;
		module.exContext.args.turnHighRisk=true;
		module.exContext.args.tsData = tsData;
		module.getWin().show();
	}
});