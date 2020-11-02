$package("chis.application.dc.script")

$import("chis.script.BizSimpleListView", "chis.script.EHRView",
		"chis.application.dc.script.RabiesRecordLogoutForm")

chis.application.dc.script.RabiesRecordListView = function(cfg) {
	this.initCnd = ["eq", ["$", "a.status"], ["s", "0"]];
	chis.application.dc.script.RabiesRecordListView.superclass.constructor.apply(this, [cfg]);
	this.exContext = {}
	this.exContext.control = {};
}

Ext.extend(chis.application.dc.script.RabiesRecordListView, chis.script.BizSimpleListView, {
	doCreateByEmpi : function(item, e) {
		var m = this.midiModules["EMPIInfoModule_R"];
		if (!m) {
			$import("chis.application.mpi.script.EMPIInfoModule")
			m = new chis.application.mpi.script.EMPIInfoModule({
						entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					})
			m.on("onEmpiReturn", this.onAddRabiesRecord, this)
			this.midiModules["EMPIInfoModule_R"] = m;
		}
		var win = m.getWin();
		win.setPosition(250, 100);
		win.show();
	},
	onAddRabiesRecord : function(data) {
		var empiId = data.empiId;
		util.rmi.jsonRequest({
					serviceId : 'chis.rabiesRecordService',
					serviceAction : "whetherNeedRabiesRecord",
					method:"execute",
					empiId : empiId
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg,
								this.onAddRabiesRecord, [data]);
						return;
					}
					if (json.body) {
						var needCreate = json.body.needCreate;
						if (needCreate == 0) {
							var rabiesId = json.body.rabiesId;
							if (rabiesId) {
								data.rabiesId = rabiesId;
								this.onEmpiSelected(data);
							}
						} else if (needCreate == 1) {
							this.onEmpiSelected(data);
						} else {
							Ext.Msg.alert("提示信息", json.body.message)
						}
					}
				}, this);
	},
	doModify : function(item, e) {
		var r = this.grid.getSelectionModel().getSelected();
		if (!r) {
			return;
		}
		var data = {};
		data.empiId = r.get("empiId");
		data.rabiesId = r.get("rabiesId");
		data.phrId = r.get("phrId");
		this.onEmpiSelected(data)
	},
	onEmpiSelected : function(data) {
		this.rabiesId = data.rabiesId;
		var empiId = data.empiId;
		var phrId = data.phrId;
		var module = this.midiModules["RabiesRecord_EHRView"];
		if (!module) {
			module = new chis.script.EHRView({
						initModules : ['DC_01'],
						empiId : empiId,
						phrId : phrId,
						closeNav : true,
						mainApp : this.mainApp
					});
			this.midiModules["RabiesRecord_EHRView"] = module;
			module.exContext.args.rabiesId = this.rabiesId;
		} else {
			module.exContext.ids = {};
			module.exContext.args = {};
			module.exContext.ids.empiId = empiId;
			module.exContext.args.rabiesId = this.rabiesId;
			module.refresh();
		}
		module.on("save", this.refresh, this);
		module.on("close", this.active, this);
		module.getWin().show();
	},
	onDblClick : function(grid, index, e) {
		this.doModify();
	},
	onStoreLoadData : function(store, records, ops) {
		chis.application.dc.script.RabiesRecordListView.superclass.onStoreLoadData.call(this,
				store, records, ops);
		this.onRowClick();
	},
	onRowClick : function() {
		var r = this.getSelectedRecord();
		if (!r) {
			return
		}
		var status = r.get("status");
		var bts = this.grid.getTopToolbar().items;
		if (bts.items.length >= 8) {
			if (status == "2") {
				bts.items[7].disable();
			} else if (status == "1") {
				bts.items[7].disable();
			} else {
				bts.items[7].enable();
			}
		}
	},
	getPagingToolbar : function(store) {
		var pagingToolbar = chis.application.dc.script.RabiesRecordListView.superclass.getPagingToolbar
				.call(this, store);
		var items = pagingToolbar.items;
		var lab = new Ext.form.Label({
					html : "&nbsp;&nbsp;状态:"
				});
		items.insert(13, "lab", lab);
		var comb = util.dictionary.SimpleDicFactory.createDic({
					id : "chis.dictionary.docStatu",
					forceSelection : true,
					defaultValue : {
						key : "0",
						text : "正常"
					}
				})
		comb.on("select", this.radioChanged, this);
		comb.setWidth(80);
		items.insert(14, "comb", comb);
		this.pagingToolbar = pagingToolbar;
		return pagingToolbar;
	},
	openModule:function(cmd,r,xy){
		var module = this.midiModules[cmd]
		if(module){
			module.listData=r.data;
			var win = module.getWin()
			if(xy){
				win.setPosition(xy[0] || this.xy[0], xy[1] || this.xy[1])
			}
			win.setTitle(module.title)
			win.show()
			this.fireEvent("openModule", module)
			if(!win.hidden){
				switch(cmd){
					case "create":
						module.doCreate()
						break;
					case "read":
					case "update":
						module.loadData()
				}
			}
		}
	},
	radioChanged : function(r) {
		var status = r.getValue()
		var navCnd = this.navCnd
		var queryCnd = this.queryCnd
		var statusCnd = ["eq", ["$", "a.status"], ["s", status]];
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
		if (btn) {
			if (status != "0") {
				btn.notReadOnly = false;
			} else {
				btn.notReadOnly = null;
			}
		}
		this.requestData.cnd = cnd
		this.requestData.pageNo = 1
		this.refresh()
	}
});