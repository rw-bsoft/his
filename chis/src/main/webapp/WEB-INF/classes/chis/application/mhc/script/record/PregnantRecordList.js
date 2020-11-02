/**
 * 孕妇档案列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizSimpleListView", "chis.script.EHRView","chis.script.util.helper.Helper")
chis.application.mhc.script.record.PregnantRecordList = function(cfg) {
	this.initCnd = cfg.cnds = ["eq", ["$", "a.status"], ["s", "0"]]
	chis.application.mhc.script.record.PregnantRecordList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.mhc.script.record.PregnantRecordList, chis.script.BizSimpleListView, {

	doCreateByEmpi : function(item, e) {
		var m = this.midiModules["PregnantRecord"];
		if (!m) {
			$import("chis.application.mpi.script.EMPIInfoModule")
			m = new chis.application.mpi.script.EMPIInfoModule({
						entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					})
			m.on("onEmpiReturn", this.onAddPregnant, this)
			this.midiModules["PregnantRecord"] = m;
		}
		var win = m.getWin();
		win.setPosition(250, 100);
		win.show();
	},

	onAddPregnant : function(data) {
		if (data.sexCode == "1") {
			Ext.Msg.alert("提示信息", "性别不符")
			return
		}
		var birth = Date.parseDate(data.birthday, "Y-m-d");
		var month = chis.script.util.helper.Helper.getAgeMonths(birth, Date
						.parseDate(this.mainApp.serverDate, "Y-m-d"));
		var age = month / 12;
		if (age < 18) {
			Ext.Msg.alert("提示信息", "年龄不符")
			return
		}
		this.empiId = data.empiId;
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : "whetherNeedPregnantRecord",
					method:"execute",
					body : {
						"empiId" : this.empiId
					}
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg, this.onAddPregnant,
								[data]);
						return
					}
					if (json.body) {
						var body = json.body;
						this.needCreate = body.needCreate;
						var result = body.result;
						this.message = body.message;
						if (this.needCreate == 1 && !result) { // ** 不存在档案,可以建档
							this.initDataId = null;
							this.showModule();
						} else if (result) { // ** 有档案存在
							var selectView = this.midiModules["DeliveryRecordSelectList"];
							if (!selectView) {
								$import("chis.application.mhc.script.record.PregnantRecordSelectList")
								selectView = new chis.application.mhc.script.record.PregnantRecordSelectList(
										{
											formRecordList : true
										});
								selectView.on("update", function(pregnantId) {
											this.initDataId = pregnantId;
											this.showModule();
										}, this);
								selectView.on("create", function() {
											if (this.needCreate == 0) { // **
												// 档案已经存在
												Ext.Msg.alert("提示信息",
														this.message);
											} else if (this.needCreate == 1) { // **
												// 可以建档
												this.initDataId = null;
												this.showModule();
											}
										}, this);
								this.midiModules["DeliveryRecordSelectList"] = selectView;
							}
							var win = selectView.getWin();
							win.setPosition(300, 100);
							win.show();
							selectView.setData(result);
						}
					}
				}, this)
	},

	doModify : function(item, e) {
		var r = this.grid.getSelectionModel().getSelected()
		this.initDataId = r.id;
		this.empiId = r.get("empiId");
		this.showModule();
	},

	showModule : function() {
		var module = this.midiModules["PregnantRecord_EHRView"]
		if (!module) {
			module = new chis.script.EHRView({
						initModules : ['G_01'],
						empiId : this.empiId,
						closeNav : true,
						mainApp : this.mainApp
					})
			this.midiModules["PregnantRecord_EHRView"] = module
			module.exContext.ids["pregnantId"] = this.initDataId;
			module.on("save", this.refreshList, this);
		} else {
			module.exContext.ids = {}
			module.exContext.ids["empiId"] = this.empiId
			module.exContext.ids["pregnantId"] = this.initDataId;
			module.refresh();
		}
		module.getWin().show();
	},

	refreshList : function(entryName, op, json, data) {
		if (entryName == this.entryName) {
			this.refresh();
		}
	},

	doScreen : function(item, e) {
		var r = this.grid.getSelectionModel().getSelected()
		if (!r) {
			return;
		}
		this.initDataId = r.id;
		var empiId = r.get("empiId");
		var module = this.midiModules["PregnantScreen_EHRView"]
		if (!module) {
			module = new chis.script.EHRView({
						initModules : ['G_01', 'G_15'],
						activeTab : 1,
						empiId : empiId,
						closeNav : true,
						mainApp : this.mainApp
					})
			this.midiModules["PregnantScreen_EHRView"] = module
			module.exContext.ids["pregnantId"] = this.initDataId;
		} else {
			module.exContext.ids = {}
			module.exContext.ids["empiId"] = empiId
			module.exContext.ids["pregnantId"] = this.initDataId;
			module.refresh();
		}
		module.getWin().show()
	},

	// doVisit : function(item, e) {
	// var r = this.grid.getSelectionModel().getSelected()
	// if (!r)
	// return;
	// this.initDataId = r.id;
	// var empiId = r.get("empiId");
	// var module = this.midiModules["PregnantVisit_EHRView"]
	// if (!module) {
	// module = new chis.script.EHRView({
	// initModules : ['G_01', 'G_02'],
	// activeTab : 1,
	// empiId : empiId,
	// closeNav : true,
	// mainApp : this.mainApp
	// })
	// this.midiModules["PregnantVisit_EHRView"] = module
	// module.exContext.ids["pregnantId"] = this.initDataId;
	// module.on("save", this.refreshList, this);
	// } else {
	// module.exContext.ids = {}
	// module.exContext.ids["empiId"] = empiId
	// module.exContext.ids["pregnantId"] = this.initDataId;
	// module.refresh();
	// }
	// module.getWin().show()
	// },

	onDblClick : function(grid, index, e) {
		// if (this.id == "G0101") {
		this.doModify();
		// } else if (this.id == "G0201") {
		// this.doVisit();
		// }
	},

	getPagingToolbar : function(store) {
		var pagingToolbar = chis.application.mhc.script.record.PregnantRecordList.superclass.getPagingToolbar
				.call(this, store);
		var items = pagingToolbar.items;
		var lab = new Ext.form.Label({
					html : "&nbsp;&nbsp;状态:"
				});
		items.insert(13, "lab", lab);
		var comb = util.dictionary.SimpleDicFactory.createDic({
					id : "chis.dictionary.docStatu",
					forceSelection : true
				})
		comb.on("select", this.radioChanged, this);
		comb.setValue("01");
		comb.setWidth(80);
		items.insert(14, "comb", comb);
		this.pagingToolbar = pagingToolbar;
		return pagingToolbar;
	},

	radioChanged : function(r) {
		var status = r.getValue()
		var navCnd = this.navCnd
		var queryCnd = this.queryCnd
		var statusCnd = ['eq', ['$', 'a.status'], ['s', status]]
		this.initCnd = statusCnd;
		var cnd = statusCnd;
		if (navCnd || queryCnd) {
			cnd = ['and', cnd];
			if (navCnd) {
				cnd.push(navCnd)
			}
			if (queryCnd) {
				cnd.push(queryCnd)
			}
		}
		var bts = this.grid.getTopToolbar().items;
		var btn1 = bts.items[8];
		var btn2 = bts.items[9];
		if (btn1) {
			if (status != "0") {
				btn1.disable();
			} else {
				btn1.enable();
			}
		}
		if (btn2) {
			if (status != "0") {
				btn2.disable();
			} else {
				btn2.enable();
			}
		}
		this.requestData.cnd = cnd
		this.requestData.pageNo = 1
		this.refresh()
	}
});