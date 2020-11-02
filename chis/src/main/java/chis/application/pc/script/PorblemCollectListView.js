$package("chis.application.pc.script")

$import("chis.script.BizSimpleListView", "util.rmi.jsonRequest")

chis.application.pc.script.PorblemCollectListView = function(cfg) {
	cfg.xy = [250, 100];
	this.schema = cfg.entryName;
	chis.application.pc.script.PorblemCollectListView.superclass.constructor.apply(this,
			[cfg]);
	this.removeServiceId = "chis.porblemCollectService";
	this.removeAction = "removePorblemCollect"
}
Ext.extend(chis.application.pc.script.PorblemCollectListView, chis.script.BizSimpleListView, {
			doTreat : function(item, e) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var m = this.getPorblemTreatForm(true, r);
				m.op = "update";

				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
				var formData = this.castListDataToForm(r.data, this.schema);
				m.initFormData(formData);
			},
			doCreateInfo : function() {
				var r = {};
				var m = this.getPorblemTreatForm(false, r);
				m.op = "create";
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			getPorblemTreatForm : function(isTreat, r) {
				var m = this.midiModules["porblemTreatForm"];
				if (!m) {
					var cfg = {};
					cfg.mainApp=this.mainApp;
					var moduleCfg = this.mainApp.taskManager
							.loadModuleCfg("chis.application.pc.PC/PC/AB1_1");
					Ext.apply(cfg, moduleCfg.json.body);
					Ext.apply(cfg, moduleCfg.json.body.properties);
					var cls = cfg.script;
					$import(cls);
					m = eval("new " + cls + "(cfg)");
					m.on("save", this.refresh, this);
					m.on("close", this.active, this);
					this.midiModules["porblemTreatForm"] = m;
				} else {
					m.initDataId = r.id;
				}
				m.isTreat = isTreat;
				return m;
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var m = this.getPorblemTreatForm(false, r);
				m.op = "update";
				var createUser = r.get("createUser");
				var status = r.get("status");
				if (createUser != this.mainApp.uid || status != "0") {
					m.op = "read";
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
				var formData = this.castListDataToForm(r.data, this.schema);
				m.initFormData(formData);
			},
			doRemove : function(item, e) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				} else {
					var createUser = r.get("createUser");
					var status = r.get("status");
					if (createUser != this.mainApp.uid || status != "0") {
						Ext.Msg.alert("提示", "该记录无法删除!");
					} else {
						Ext.Msg.show({
									title : '确认删除记录[' + r.id + ']',
									msg : '删除操作将无法恢复，是否继续?',
									modal : true,
									width : 300,
									buttons : Ext.MessageBox.OKCANCEL,
									multiline : false,
									fn : function(btn, text) {
										if (btn == "ok") {
											this.processRemove();
										}
									},
									scope : this
								})
					}
				}
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				}

				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.problemStatus",
							forceSelection : true
						})
				comb.on("select", this.radioChanged, this)
				comb.setValue("01")
				comb.setWidth(80)
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				cfg.items = [lab, comb]
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar
				return pagingToolbar
			},
			radioChanged : function(r) {
				var status = r.getValue()
				var queryCnd = this.queryCnd
				var statusCnd = ['eq', ['$', 'a.status'], ['s', status]]
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (queryCnd) {
					cnd = ['and', cnd];
					if (navCnd) {
						cnd.push(navCnd)
					}
					if (queryCnd) {
						cnd.push(queryCnd)
					}
				}
				this.requestData.cnd = cnd
				this.requestData.pageNo = 1
				this.refresh()
			}
		})