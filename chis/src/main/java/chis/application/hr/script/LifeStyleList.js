/**
 * 个人生活习惯列表页面
 * 
 * @author tianj
 */
$package("chis.application.hr.script")

$import("chis.script.BizSimpleListView")

chis.application.hr.script.LifeStyleList = function(cfg) {
	chis.application.hr.script.LifeStyleList.superclass.constructor.apply(this, [cfg]);
	this.createCls = "chis.application.hr.script.LifeStyleForm";
	this.updateCls = "chis.application.hr.script.LifeStyleForm";
}

Ext.extend(chis.application.hr.script.LifeStyleList, chis.script.BizSimpleListView, {
	doAction : function(item, e) {
		var cmd = item.cmd;
		var ref = item.ref;

		if (ref) {
			this.loadRemote(ref, item);
			return;
		}
		var script = item.script;

		if (cmd == "update" || cmd == "read") {
			var r = this.getSelectedRecord();
			if (r == null) {
				return;
			}
			if (!script) {
				script = this.updateCls;
			}
			this.loadModule(script, this.entryName, item, r);
			return;
		}
		cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1);
		if (script) {
			$require(script, [function() {
						eval(script + '.do' + cmd
								+ '.apply(this,[item,e])')
					}, this]);
		} else {
			var action = this["do" + cmd];
			if (action) {
				action.apply(this, [item, e]);
			}
		}
	},
	
	doAddLifeStyle : function(item, e) {
		this.item = item;
		var m = this.midiModules["lifeStyleModule"];
		if (!m) {
			$import("chis.application.hr.script.AddModule");
			m = new chis.application.hr.script.AddModule({});
			m.on("selectEMPI", this.onSelectEMPI, this);
			this.midiModules["lifeStyleModule"] = m;
		}
		m.getWin().show();
	},
	
	onSelectEMPI : function(empiId) {
		var m = this.midiModules["lifeStyleModule"];
		m.close();
		var empiId = empiId;
		var r;
		this.loadModule(this.createCls, this.entryName, this.item, r,
				empiId);
	},

	loadModule : function(cls, entryName, item, r, empiId) {
		var cmd = item.cmd;
		var cfg = {};
		cfg.title = this.title + '-' + item.text;
		cfg.entryName = entryName;
		cfg.op = cmd;
		cfg.exContext = {};
		cfg.empiId = empiId;
		cfg.actions = [{
					id : "save",
					name : "保存"
				}];
		cfg.readOnly = this.readOnly;
		Ext.apply(cfg.exContext, this.exContext);
		if (cmd == 'update' || cmd == 'read' || cmd == 'print') {
			cfg.initDataId = r.id;
			cfg.exContext[entryName] = r;
		}
		if (this.saveServiceId) {
			cfg.saveServiceId = this.saveServiceId;
		}
		if (this.serviceAction) {
			cfg.serviceAction = this.serviceAction
		}
		var m = this.midiModules[cmd]
		if (!m) {
			this.loading = true
			$require(cls, [function() {
								this.loading = false;
								cfg.autoLoadData = false;
								var module = eval("new " + cls + "(cfg)");
								module.on("save", this.onSave, this);
								module.on("close", this.active, this);
								module.opener = this;
								module.setMainApp(this.mainApp);
								this.midiModules[cmd] = module;
								this.fireEvent("loadModule", module);
								this.openModule(cmd, r, 100, 50);
							}, this])
		} else {
			Ext.apply(m, cfg);
			this.openModule(cmd, r);
		}
	},
	
	openModule : function(cmd, r, xy) {
		var module = this.midiModules[cmd];
		if (module) {
			var win = module.getWin();
			if (xy) {
				win.setPosition(xy[0], xy[1]);
			}
			win.setTitle(module.title);
			win.show();
			if (!win.hidden) {
				switch (cmd) {
					case "create" :
						module.doNew()
						break;
					case "read" :
					case "update" :
						module.loadData()
				}
			}
		}
	}
});