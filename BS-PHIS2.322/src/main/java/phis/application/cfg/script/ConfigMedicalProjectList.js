$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigMedicalProjectList = function(cfg) {
	this.initCnd = ['eq', ['$', 'FYFL'], ['$', '1']];  //modified by zhangxw
	this.closeAction = true;
	cfg.disablePagingTbr = false;
	phis.application.cfg.script.ConfigMedicalProjectList.superclass.constructor.apply(
			this, [cfg])
	this.exContext['FYFL'] = "1";

}
Ext.extend(phis.application.cfg.script.ConfigMedicalProjectList,
		phis.script.SimpleList, {
			openModule : function(cmd, r, xy) {
				phis.application.cfg.script.ConfigMedicalProjectList.superclass.openModule
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
			processRemove : function() {
				var r = this.getSelectedRecord()
				this.mask("正在删除数据...")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							method : "execute",
							body : {
								sfxm : r.id
							}
						}, function(code, msg, json) {
							this.unmask();
							if (code < 300) {
								this.store.remove(r)
							} else {
								MyMessageTip.msg("提示", msg, true);
								return false;
							}
						}, this);
			}

		})