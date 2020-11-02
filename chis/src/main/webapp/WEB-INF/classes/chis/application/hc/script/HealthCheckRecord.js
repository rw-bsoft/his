$package("chis.application.hc.script")

$import("chis.script.BizSimpleListView",
		"chis.script.EHRView",
		"chis.application.hc.script.HealthCheckRepeatRecord")

chis.application.hc.script.HealthCheckRecord = function(cfg) {
	this.initCnd =['and',['eq', ['s', '1'], ['s', '1']],['ne', ['$', 'a.createUnit'], ['s','320124011']],['ne', ['$', 'a.createUnit'], ['s','320124010']]]
	chis.application.hc.script.HealthCheckRecord.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.hc.script.HealthCheckRecord, chis.script.BizSimpleListView, {
	       getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				};

				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.year",
							forceSelection : true,
							defaultValue : {
								key : "2018",
								text : "2018年"
							}
						});
				comb.on("select", this.radioChanged, this);
				comb.setValue("2018");
				comb.setWidth(80);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;年度:"
						});
						
			var comb1 = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.tjlx",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "健康体检"
							}
						});
				comb1.on("select", this.tjChanged, this);
				comb1.setValue("0");
				comb1.setWidth(80);
				var lab1 = new Ext.form.Label({
							html : "&nbsp;&nbsp;体检类型:"
						});
						
						
				cfg.items = [lab, comb, lab1,comb1];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},
	
			tjChanged : function(r) {
			   var tjlx = r.getValue();	
			   var statusCnd =['and',['eq', ['s', '1'], ['s', '1']],['ne', ['$', 'a.createUnit'], ['s','320124011']],['ne', ['$', 'a.createUnit'], ['s','320124010']],["eq", ["$", "a.tjlx"], ["s", tjlx]]] ;
			   this.initCnd =statusCnd ;
			   var cnd = statusCnd;
			   this.requestData.cnd = cnd;
			   this.requestData.pageNo = 1;
			   this.refresh();
			},
			   
	        radioChanged : function(r) {
				var year = r.getValue();
//				var navCnd = this.navCnd;
//				var queryCnd = this.queryCnd;
				var statusCnd = ['and',['eq', ['s', '1'], ['s', '1']],['ne', ['$', 'a.createUnit'], ['s','320124011']],['ne', ['$', 'a.createUnit'], ['s','320124010']],["eq", ["$", "a.year"], ["s", year]]];				
				this.initCnd =statusCnd ;
				var cnd = statusCnd;
//				if (navCnd || queryCnd) {
//					cnd = ['and', cnd];
//					if (navCnd) {
//						cnd.push(navCnd);
//					}
//					if (queryCnd) {
//						cnd.push(queryCnd);
//					}
//				}

//				var bts = this.grid.getTopToolbar().items;
//				var btn = bts.items[8];
//				if (btn) {
//					if (status != "0") {
//						btn.disable();
//					} else {
//						btn.enable();
//					}
//				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},
	
			doCreateByEmpi : function(item, e) {
				var m = this.midiModules["EMPIInfoModule_HC"];
				if (!m) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					m = new chis.application.mpi.script.EMPIInfoModule({
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							})
					m.on("onEmpiReturn", this.onAddHealthCheck, this)
					this.midiModules["EMPIInfoModule_HC"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onAddHealthCheck : function(data) {
				this.showModule(data,"create");
			},

			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.showModule(r.data,"update")
			},

			showModule : function(data,op) {
				var empiId = data.empiId;
				var initModules = ['B_10'];
				if(this.mainApp.exContext.healthCheckType == 'paper'){
					initModules = ['B_10_HTML'];
				}
				var module = this.midiModules["HealthCheck_EHRView"]
				if (!module) {
					module = new chis.script.EHRView({
								initModules : initModules,
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
							})
					this.midiModules["HealthCheck_EHRView"] = module
					module.on("save", this.refreshData, this);
					module.exContext.args["healthCheck"] = data.healthCheck;
				} else {
					module.exContext.ids["empiId"] = empiId;
					module.exContext.args["healthCheck"] = data.healthCheck;
					module.refresh();
				}
				module.exContext.args["dataSources"] = "1";
				module.exContext.args["op"] = op;
				module.getWin().show();
			},

			onDblClick : function(grid, index, e) {
				this.doModify();
			},

			refreshData : function(entryName, op, json, data) {
				if (this.store) {
					this.store.load()
				}
			},
			doRepeat : function(item, e) {
				m = new chis.application.hc.script.HealthCheckRepeatRecord({
						entryName : "chis.application.hc.schemas.V_HealthCheck_Repeat",
						title : "体检查重",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					})
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			}
		});