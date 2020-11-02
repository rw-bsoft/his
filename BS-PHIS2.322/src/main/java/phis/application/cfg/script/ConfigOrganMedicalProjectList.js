$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigOrganMedicalProjectList = function(cfg) {
	this.initCnd = ['eq', ['$', 'FYFL'], ['$', '1']]; // modified by zhangxw
	this.closeAction = true;
	cfg.disablePagingTbr = false;
	cfg.winState = "center";
	phis.application.cfg.script.ConfigOrganMedicalProjectList.superclass.constructor
			.apply(this, [cfg])
	this.exContext['FYFL'] = "1"
}
Ext.extend(phis.application.cfg.script.ConfigOrganMedicalProjectList,
		phis.script.SimpleList, {
			onDblClick : function(grid, index, e) {
				var actions = this.actions
				if (!actions) {
					return;
				}
				this.selectedIndex = index
				var item = {};
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i]
					var cmd = action.id
					if (cmd == "update" || cmd == "detail") {
						item.text = action.name
						item.cmd = action.id
						item.ref = action.ref
						item.script = action.script
						if (cmd == "update") {
							break
						}
					}
				}
				if (item.cmd) {
					this.doAction(item, e)
				}
			},

			openModule : function(cmd, r, xy) {
				phis.application.cfg.script.ConfigOrganMedicalProjectList.superclass.openModule
						.call(this, cmd, r, xy)
				var module = this.midiModules[cmd]
				var win = module.getWin();
				var dxy = win.el.getAlignToXY(win.container, 'c-c');
				win.setPagePosition(dxy[0], 100);
			},
			loadRemote : function(ref, btn) {
				if (this.loading) {
					return
				}
				var r = this.getSelectedRecord()
				var cmd = btn.cmd
				if (cmd == "update" || cmd == "read") {
					if (r == null) {
						return
					}
				}
				var cfg = {}
				cfg._mId = this.grid._mId // 增加module的id
				cfg.title = this.name + '-' + btn.text
				cfg.op = cmd
				cfg.openWinInTaskbar = false
				cfg.autoLoadData = false;
				cfg.exContext = {}
				Ext.apply(cfg.exContext, this.exContext)
				if (cmd != 'create' && r != null) {
					cfg.initDataId = r.id
					cfg.exContext[this.entryName] = r;
					cfg.exContext["FYGB"] = r.id;
				} else {
					cfg.initDataId = null;
				}
				var module = this.midiModules[cmd]
				if (module) {
					Ext.apply(module, cfg)
					this.openModule(cmd, r)
				} else {
					this.loading = true
					this.mainApp.taskManager.loadInstance(ref, cfg, function(m,
									from) {
								this.loading = false
								m.on("save", this.onSave, this)
								m.on("close", this.active, this)
								m.on("destory", this.onDestory, this)
								this.midiModules[cmd] = m
								if (from == "local") {
									Ext.apply(m, cfg)
								}
								this.fireEvent("loadModule", m)
								this.openModule(cmd, r, [200, 50])
							}, this)
				}
			},
			// 全部调入
			doCallInAll : function() {
				if (this.saving)
					return;
				this.saving = true
				// var body = {
				// "FYGB" : this.initDataId
				// };
				var body = [];
				var count = this.store.getCount();
				if (count == 0) {
					MyMessageTip.msg("提示", "不存在项目，无法调入", true);
					return;
				}
				for (var i = 0; i < count; i++) {
					body.push(this.store.getAt(i).data["SFXM"]);
				}

				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "configDeptCostService",
							serviceAction : "saveAllCallin",
							method : "execute",
							body : body
						}, function(code, msg, json) {
							this.grid.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer);
								return
							}
							MyMessageTip.msg("提示", "调入成功", true);
							this.loadData();
						}, this)// jsonRequest
			}
		})