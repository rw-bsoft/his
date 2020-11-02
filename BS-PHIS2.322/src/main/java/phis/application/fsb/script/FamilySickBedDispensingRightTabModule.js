/**
 * 家床发药-右边tab
 * 
 * @author : caijy
 */
$package("phis.application.fsb.script")

$import("phis.script.TabModule")

phis.application.fsb.script.FamilySickBedDispensingRightTabModule = function(
		cfg) {
	cfg.tag = 1;
	phis.application.fsb.script.FamilySickBedDispensingRightTabModule.superclass.constructor
			.apply(this, [cfg]);
	this.yzlx = 0;
}

Ext.extend(phis.application.fsb.script.FamilySickBedDispensingRightTabModule,
		phis.script.TabModule, {
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					tabItems.push({
								frame : false,
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								id : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							resizeTabs : this.resizeTabs,
							tabPosition : this.tabPosition || "top",
							defaults : {
								border : false
							},
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			showRecord : function(r) {
				this.doNew();
				this.records = r;
				if (this.tab.getActiveTab().id == "hzfy") {
					this.tab.activate(0);
				}
				if (this.midiModules["yzfy"]) {
					this.midiModules["yzfy"].isNew = true;
					this.moduleOperation("loadData", this.midiModules["yzfy"],
							r);
				}
				if (this.midiModules["hzfy"]) {
					this.midiModules["hzfy"].isNew = true;
					this.moduleOperation("loadData", this.midiModules["hzfy"],
							r);
				}
				if (this.midiModules["thbq"]) {
					this.midiModules["thbq"].isNew = true;
					this.moduleOperation("loadData", this.midiModules["thbq"],
							r);
				}
			},
			moduleOperation : function(op, module, record) {
				var viewType = module.viewType;
				if (op == "loadData") {
					if (viewType == "th" || viewType == "mx") {
						if (!module.isNew) {
							return;
						}
						module.isNew = false;
						var arr = new Array();
						if (record.length == 0) {
							this.doNew();
							return;
						}
						var yzlx = this.yzlx;
						var cnd;
						var fyfs = 0;
						var tjbq = 0
						for (var i = 0; i < record.length; i++) {
							arr.push(record[i].json.ZYH);
							fyfs = record[i].json.FYFS;
							tjbq = record[i].json.TJBQ;
						}
						cnd = [
								'and',
								module.cnds,
								['in', ['$', 'c.ZYH'], arr],
								['eq', ['$', 'd.FYFS'], ['d', fyfs]],
								['eq', ['$', 'd.TJYF'],
										['l', this.mainApp['phis'].pharmacyId]],
								['eq', ['$', 'a.JGID'],
										['s', this.mainApp['phisApp'].deptId]]];
						if (yzlx == 1) {
							cnd = ['and', ['eq', ['$', 'd.YZLX'], ['i', 1]],
									['eq', ['$', 'a.LSYZ'], ['i', 1]], cnd]

						} else if (yzlx == 2) {
							cnd = ['and', cnd,
									['eq', ['$', 'a.LSYZ'], ['i', 0]],
									['eq', ['$', 'd.YZLX'], ['i', 1]]]
						}
						module.requestData.cnd = cnd
						module.clearSelect();
						module.loadData();
					} else if (viewType == "hz") {
						// 退回的数据从医嘱发药的选中记录里取
						if (!this.midiModules["yzfy"]) {
							return;
						}
						module.clear();
						var module1 = this.midiModules["yzfy"];
						var r = [];
						var datas = module1.getSelectedRecords();
						for (var i = 0; i < datas.length; i++) {
							var data = datas[i].json;
							var x = 0;
							for (var j = 0; j < r.length; j++) {
								if (data.YPXH == r[j].YPXH
										&& data.YPCD == r[j].YPCD) {
									r[j].YCSL = r[j].YCSL + data.YCSL;
									r[j].FYJE = (r[j].YCSL * r[j].YPDJ)
											.toFixed(2);
									x = 1;
									break;
								}
							}
							if (x == 0) {
								var x = {};
								for (var a in data) {
									x[a] = data[a];
								}
								r.push(x);
							}
						}
						module.loadData(r);
					}

				}
			},
			doNew : function() {
				this.records = null;
				if (this.midiModules["yzfy"]) {
					this.midiModules["yzfy"].clear();
					this.midiModules["yzfy"].clearSelect();
				}
				if (this.midiModules["hzfy"]) {
					this.midiModules["hzfy"].clear();
				}
				if (this.midiModules["thbq"]) {
					this.midiModules["thbq"].clear();
					this.midiModules["thbq"].clearSelect();
				}
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					if (newTab.id == "yzfy" || newTab.id == "hzfy") {
						this.fireEvent("checkTab", 1);
					} else {
						this.fireEvent("checkTab", 2);
					}
					this.moduleOperation("loadData",
							this.midiModules[newTab.id], this.records);
					return;
				}
				var exCfg = newTab.exCfg
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true
				}
				Ext.apply(cfg, exCfg);
				var ref = exCfg.ref
				if (ref) {
					var body = this.loadModuleCfg(ref);
					Ext.apply(cfg, body)
				}
				var cls = cfg.script
				if (!cls) {
					return;
				}

				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$require(cls, [function() {
							var m = eval("new " + cls + "(cfg)");
							m.setMainApp(this.mainApp);
							if (this.initDataId) {
								m.initDataId = this.initDataId;
							}
							this.midiModules[newTab.id] = m;
							this.rightm = m;
							var p = m.initPanel();
							// m.on("save", this.onSuperRefresh,
							// this)
							m.on("fy", this.onRecordCheckedForButton, this)
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							if (newTab.id == "hzfy") {
								this.moduleOperation("loadData",
										this.midiModules[newTab.id],
										this.records);
								this.fireEvent("checkTab", 1);
							} else if (newTab.id == "thbq") {
								if (this.records && this.records != null) {
									this.midiModules[newTab.id].isNew = true;
									this.moduleOperation("loadData",
											this.midiModules[newTab.id],
											this.records);
								}
								this.fireEvent("checkTab", 2);
							}
						}, this])
				// this.fireEvent("tabchange", tabPanel, newTab,
				// curTab);
			},
			// 获取发药数据
			getData : function(tag) {
				var record;
				if (tag == "fy") {
					record = this.midiModules["yzfy"].getSelectedRecords();
				} else {
					record = this.midiModules["thbq"].getSelectedRecords();
				}
				var ret = new Array();
				for (var i = 0; i < record.length; i++) {
					ret.push(record[i].json);
				}
				if (tag == "fy") {
					var count = this.midiModules["yzfy"].store.getCount();
					for (var i = 0; i < count; i++) {
						var data = this.midiModules["yzfy"].store.getAt(i).data;
						if (data.ZT == "停嘱") {
							ret.push(data);
						}
					}
				}
				return ret;

			},
			// 判断有没记录选中来改变按钮的状态
			onRecordCheckedForButton : function(tag) {
				if (this.tab.getActiveTab().id == "thbq" && tag == 1) {
					return;
				} else if (this.tab.getActiveTab().id == "yzfy" && tag != 1) {
					return;
				}
				this.fireEvent("checkTab", tag);
			}
		});