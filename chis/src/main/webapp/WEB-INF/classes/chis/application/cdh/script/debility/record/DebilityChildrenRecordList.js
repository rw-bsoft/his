/**
 * 体弱儿档案列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.debility.record");
$import("chis.script.BizSimpleListView");

chis.application.cdh.script.debility.record.DebilityChildrenRecordList = function(cfg) {
	this.initCnd = ['and', ['eq', ['$', 'a.status'], ['s', '0']]];
	chis.application.cdh.script.debility.record.DebilityChildrenRecordList.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.cdh.script.debility.record.DebilityChildrenRecordList,
		chis.script.BizSimpleListView, {
			doCreateByEmpi : function() {
				var advancedSearchView = this.midiModules["ChildInfoForm"];
				if (!advancedSearchView) {
					$import("chis.application.cdh.script.base.ChildInfoForm")
					advancedSearchView = new chis.application.cdh.script.base.ChildInfoForm({
								entryName : "chis.application.mpi.schemas.MPI_ChildBaseInfo",
								title : "儿童基本信息查询",
								height : 450,
								width : 780,
								modal : true,
								mainApp : this.mainApp,
								isDeadRegist : false
							})
					advancedSearchView.on("save", this.onEmpiSelected, this);
					this.midiModules["ChildInfoForm"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onEmpiSelected : function(entryName, op, json, data) {
				this.empiId = data.empiId;
				this.showEhrViewWin();
				this.recordId = null;
			},

			showEhrViewWin : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.childrenHealthRecordService",
							serviceAction : "checkHealthCardExists",
							method:"execute",
							body : {
								"empiId" : this.empiId
							}
						});
				var childrenRecordExists = false;
				if (result.code == 200) {
					if (result.json) {
						childrenRecordExists = result.json.body.recordExists;
					}
				}
				var m = this.midiModules["ehrView"];

				if (!m) {
					var modules = ['H_01', 'H_09'];
					if (childrenRecordExists) {
						modules = ['H_09'];
					}
					$import("chis.script.EHRView");
					m = new chis.script.EHRView({
								initModules : modules,
								mainApp : this.mainApp,
								empiId : this.empiId,
								activeTab : 0,
								closeNav : true
							});
					this.midiModules["ehrView"] = m;
					if (this.recordId) {
						m.exContext.ids["recordId"] = this.recordId;
						m.exContext.args["debilityRecordId"] = this.recordId;
					}
					m.on("save", this.refresh, this);
				} else {
					m.exContext.ids["empiId"] = this.empiId;
					if (this.recordId) {
						m.exContext.ids["recordId"] = this.recordId;
						m.exContext.args["debilityRecordId"] = this.recordId;
					}
					if (childrenRecordExists && m.activeModules["H_01"]) {
						m.activeTab = 0;
						m.activeModules["H_01"] = false;
						if (m.mainTab.find("mKey", "H_01")) {
							m.mainTab.remove(m.mainTab.find("mKey", "H_01")[0]);
						}
					} else if (!childrenRecordExists
							&& !m.activeModules["H_01"]) {
						m.activeModules["H_01"] = true;
						m.activeTab = 0;
						m.mainTab.insert(0, m.getModuleCfg("H_01"));
					}
					m.refresh();
				}
				m.getWin().show();
			},

			doModify : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.recordId = r.get("recordId");
				this.showEhrViewWin();
			},

			onDblClick : function() {
				this.doModify();
			},

			getPagingToolbar : function(store) {
				var pagingToolbar = chis.application.cdh.script.debility.record.DebilityChildrenRecordList.superclass.getPagingToolbar
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
				// chb 添加未结案标志的过滤条件
				if (status == "0") {
					statusCnd = ['and', ['eq', ['$', 'a.status'], ['s', '0']]];
				}
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
				var btn = bts.items[7];
				if (btn) {// @@ "注销"按钮
					if (status != "0") {
						btn.disable();
					} else {
						btn.enable();
					}
				}

				this.requestData.cnd = cnd
				this.requestData.pageNo = 1
				this.refresh()
			}

		});