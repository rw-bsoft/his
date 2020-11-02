$package("chis.application.tb.script")
$import("chis.script.BizSimpleListView")

chis.application.tb.script.TuberculosisListView = function(cfg) {
	this.initCnd = ["and", ["eq", ["$", "a.status"], ["s", "0"]], ["like", ["$", "a.categoryBInfectious"], ["s", "%13%"]]]
	chis.application.tb.script.TuberculosisListView.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.tb.script.TuberculosisListView,
		chis.script.BizSimpleListView, {
	onDblClick : function() {
		this.doModify();
	},
	
	getPagingToolbar : function(store) {
		var cfg = {
			pageSize : 25,
			store : store,
			requestData : this.requestData,
			displayInfo : true,
			emptyMsg : "无相关记录"
		};
	
		var comb = util.dictionary.SimpleDicFactory.createDic({
					id : "chis.dictionary.finishStatus",
					forceSelection : true,
					defaultValue : {
						key : "",
						text : ""
					}
				});
		comb.on("select", this.radioChanged, this);
		comb.setWidth(100);
		var lab = new Ext.form.Label({
					html : "&nbsp;&nbsp;结案状态:"
				});
		cfg.items = [lab, comb];
		var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
		this.pagingToolbar = pagingToolbar;
		return pagingToolbar;
	},
	radioChanged : function(r) {
		var status = r.getValue();
		if(status=='') {
			return;
		}
		var navCnd = this.navCnd;
		var queryCnd = this.queryCnd;
		var statusCnd = ['eq', ['$', 'a.finishStatus'], ['s', status]];
		if(status=='0') {
			statusCnd=['or',['eq', ['$', 'a.finishStatus'], ['s', status]],['isNull',['$','a.finishStatus']]];
		}
		this.initCnd = statusCnd;
		var cnd = statusCnd;
		if (navCnd || queryCnd) {
			cnd = ['and', cnd];
			if (navCnd) {
				cnd.push(navCnd);
			}
			if (queryCnd) {
				cnd.push(queryCnd);
			}
		}
		this.requestData.cnd = cnd;
		this.requestData.pageNo = 1;
		this.refresh();
	},
	
	doCreateByEmpi : function() {
		var advancedSearchView = this.midiModules["IDR_Report_EMPIInfoModule"];
		if (!advancedSearchView) {
			$import("chis.application.mpi.script.EMPIInfoModule");
			advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
					{
						title : "个人基本信息查找",
						mainApp : this.mainApp,
						modal : true
					});
			advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
					this);
			this.midiModules["IDR_Report_EMPIInfoModule"] = advancedSearchView;
		}
		var win = advancedSearchView.getWin();
		win.setPosition(250, 100);
		win.show();
	},
	onEmpiSelected : function(data) {
		this.empiId = data.empiId;
		this.birthday = data.birthday;
		this.createIDR = true;
		// 检查健康档案是否和当前登录人为同一机构
		this.grid.el.mask("正在查询数据..")
		util.rmi.jsonRequest({
					serviceId : "chis.simpleLoad",
					method : "execute",
					schema : "chis.application.hr.schemas.EHR_HealthRecord",
					body : {
						fieldName : "empiId",
						fieldValue : this.empiId
					}
				}, function(code, msg, json) {
					this.grid.el.unmask()
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}

					if (this.mainApp.jobtitleId == "chis.system") {
						this.showEHRViewMule();
						return;
					}

					if (!json.body) {
						this.showEHRViewMule();
						return;
					}

					var docManageUnitId = json.body.manaUnitId.key;
					var userManaUnitId = this.mainApp.deptId;
					if (docManageUnitId.substring(0, 9) == userManaUnitId) {
						this.showEHRViewMule();
					} else {
						MyMessageTip.msg("提示", "无法对非本辖区人员进行操作!",true)
					}
				}, this)

	},
	showEHRViewMule : function() {
		var cfg = {};
		cfg.initModules = ['TB_Module'];
		cfg.closeNav = true;
		cfg.mainApp = this.mainApp;
		cfg.empiId = this.empiId;
		var module = this.midiModules["TuberculosisListView_EHRView"];
		if (!module) {
			$import('chis.script.EHRView');
			module = new chis.script.EHRView(cfg);
			this.midiModules["TuberculosisListView_EHRView"] = module
			module.on("save", this.refreshList, this);
		} else {
			module.ehrNavTreePanel.collapse(true);
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["phrId"] = this.phrId;
			//module.refresh();
		}
		module.exContext.args={};
		module.exContext.args.birthday = this.birthday;
		module.exContext.args.recordId = this.recordId;
		module.exContext.args.createIDR = this.createIDR;
		module.getWin().show();
	},
	doModify : function(item, e) {
		var r = this.grid.getSelectionModel().getSelected()
		this.initDataId = r.get("RecordID");
		this.empiId = r.get("empiId");
		this.phrId = r.get("phrId");
		this.recordId = r.get("RecordID");
		this.createIDR = false;
		this.showEHRViewMule();
	},
	refreshList : function(entryName, op, json, data) {
		if (entryName == this.entryName) {
			this.refresh();
		}
	}
});