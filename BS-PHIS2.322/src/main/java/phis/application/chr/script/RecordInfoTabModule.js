$package("phis.application.chr.script")

$import("phis.script.TabModule")

phis.application.chr.script.RecordInfoTabModule = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.autoLoadData = false;
	cfg.activateId = "allRecord";
	phis.application.chr.script.RecordInfoTabModule.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.chr.script.RecordInfoTabModule, phis.script.TabModule, {
	initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var bbar = [];
				var label = new Ext.form.Label({
					html : "<div id='info' align='left' style='color:blue'>提示：患者直接诊疗医师，不记录访问日志！</div>"
				})
				bbar.push(label);
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					tabItems.push({
								frame : this.frame,
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								iconCls : ac.iconCls,
								id : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							bbar:bbar,
							resizeTabs : this.resizeTabs,
							tabPosition : this.tabPosition || "top",
							defaults : {
								border : false
							},
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.on("beforetabchange", this.onBeforeTabChange, this);
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
	listRecordData : function(tabId, YWID1, records, YWID2) {
		this.tabId=tabId;
		if (tabId == "visitRecord") {
			this.midiModules["visitRecord"].requestData.serviceId = "phis.caseHistoryReviewService";
			this.midiModules["visitRecord"].requestData.serviceAction = "listRecordByTabId";
			this.midiModules["visitRecord"].requestData.YWID1 =YWID1;
			this.midiModules["visitRecord"].requestData.records = records;
			this.midiModules["visitRecord"].requestData.tabId = tabId;
			this.midiModules["visitRecord"].requestData.YWID2 = YWID2;
			this.midiModules["visitRecord"].loadData();
		}
		if (tabId == "updateRecord") {
			this.midiModules["updateRecord"].requestData.serviceId = "phis.caseHistoryReviewService";
			this.midiModules["updateRecord"].requestData.serviceAction = "listRecordByTabId";
			this.midiModules["updateRecord"].requestData.YWID1 =YWID1;
			this.midiModules["updateRecord"].requestData.records = records;
			this.midiModules["updateRecord"].requestData.tabId = tabId;
			this.midiModules["updateRecord"].requestData.YWID2 = YWID2;
			this.midiModules["updateRecord"].loadData();
		}
		if (tabId == "autographRecord") {
			this.midiModules["autographRecord"].requestData.serviceId = "phis.caseHistoryReviewService";
			this.midiModules["autographRecord"].requestData.serviceAction = "listRecordByTabId";
			this.midiModules["autographRecord"].requestData.YWID1 =YWID1;
			this.midiModules["autographRecord"].requestData.records = records;
			this.midiModules["autographRecord"].requestData.tabId = tabId;
			this.midiModules["autographRecord"].requestData.YWID2 = YWID2;
			this.midiModules["autographRecord"].loadData();
		}
		if (tabId == "printRecord") {
			this.midiModules["printRecord"].requestData.serviceId = "phis.caseHistoryReviewService";
			this.midiModules["printRecord"].requestData.serviceAction = "listRecordByTabId";
			this.midiModules["printRecord"].requestData.YWID1 =YWID1;
			this.midiModules["printRecord"].requestData.records = records;
			this.midiModules["printRecord"].requestData.tabId = tabId;
			this.midiModules["printRecord"].requestData.YWID2 = YWID2;
			this.midiModules["printRecord"].loadData();
		}
		if (tabId == "qualityInfo") {
			this.midiModules["qualityInfo"].requestData.serviceId = "phis.caseHistoryReviewService";
			this.midiModules["qualityInfo"].requestData.serviceAction = "listRecordByTabId";
			this.midiModules["qualityInfo"].requestData.YWID1 =YWID1;
			this.midiModules["qualityInfo"].requestData.records = records;
			this.midiModules["qualityInfo"].requestData.tabId = tabId;
			this.midiModules["qualityInfo"].requestData.YWID2 = YWID2;
			this.midiModules["qualityInfo"].loadData();
		}
		if (tabId == "allRecord") {
			this.midiModules["allRecord"].requestData.serviceId = "phis.caseHistoryReviewService";
			this.midiModules["allRecord"].requestData.serviceAction = "listRecordByTabId";
			this.midiModules["allRecord"].requestData.YWID1 =YWID1;
			this.midiModules["allRecord"].requestData.records = records;
			this.midiModules["allRecord"].requestData.tabId = tabId;
			this.midiModules["allRecord"].requestData.YWID2 = YWID2;
			this.midiModules["allRecord"].loadData();
		}
	},
	loadCountByTabID : function(YWID1,YWID2,records) {
		phis.script.rmi.jsonRequest({
					serviceId : "phis.caseHistoryReviewService",
					serviceAction : "loadCountByTabID",
					YWID1:YWID1,
					YWID2:YWID2,
					records:records
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg, this.loadCountByTabID);
						return
					}
					if(json.body){
						var counts=json.body;
						this.changeTitleCount(counts);
					}
				}, this)
	},
	changeTitleCount:function(counts){
		var count=counts.allRecord;
		this.changeCountInTitle(count,"allRecord");
		
		count=counts.qualityInfo;
		this.changeCountInTitle(count,"qualityInfo");
		
		count=counts.printRecord;
		this.changeCountInTitle(count,"printRecord");
		
		count=counts.autographRecord;
		this.changeCountInTitle(count,"autographRecord");
		
		count=counts.updateRecord;
		this.changeCountInTitle(count,"updateRecord");
		
		count=counts.visitRecord;
		this.changeCountInTitle(count,"visitRecord");
	},
	
	changeCountInTitle:function(count,tabId){
		var pan=this.tab.getItem(tabId);
		var oldTitle=pan.title;
		var newTitle=oldTitle.substring(0,4)+"("+count+")";
		pan.setTitle(newTitle);
	}
});