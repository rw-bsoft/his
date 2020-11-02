/**
 * 病区发药-左边tab
 * 
 * @author : caijy
 */
$package("phis.application.hph.script")

$import("phis.script.TabModule")

phis.application.hph.script.HospitalPharmacyHistoryDispensingLeftList = function(cfg) {
	phis.application.hph.script.HospitalPharmacyHistoryDispensingLeftList.superclass.constructor
			.apply(this, [ cfg ]);
}

Ext
.extend(
	phis.application.hph.script.HospitalPharmacyHistoryDispensingLeftList,
	phis.script.TabModule,{
		initPanel : function() {
			if (this.tab) {
				return this.tab;
			}
			var tabItems = []
			var actions = this.actions
			for ( var i = 0; i < actions.length; i++) {
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
				items : tabItems,
				tbar : []
			})
			tab.on("tabchange", this.onTabChange, this);
			tab.activate(this.activateId);
			this.tab = tab;
			return tab;
	},
	showRecord : function() {
		if (this.midiModules["tjd"]&&this.tab.activeTab.id=="tjd") {
			this.moduleOperation("loadData",
					this.midiModules["tjd"]);
		}
		if (this.midiModules["br"]&&this.tab.activeTab.id=="br") {
			this.moduleOperation("loadData",
					this.midiModules["br"]);
		}
	},
	moduleOperation : function(op, module) {
		var viewType = module.viewType;
		if (op == "loadData") {
			var datefrom = this.opener.simple.items.get(1).getValue();
			var dateTo = this.opener.simple.items.get(3).getValue();
			var lx = this.opener.simple.items.get(5).getValue();
			var bq = this.opener.simple.items.get(7).getValue();
			var yg = this.opener.simple.items.get(9).getValue();
			
			if (datefrom != null && dateTo != null && datefrom != ""
					&& dateTo != "") {
				try {
					var df = new Date(Date.parse(datefrom.replace(/-/g, "/")));
					var dt = new Date(Date.parse(dateTo.replace(/-/g, "/")));
					if (df.getTime() > dt.getTime()) {
						Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
						return;
					}
					if((dt.getTime()-df.getTime())/1000/60/60/12 >90){
						Ext.MessageBox.alert("提示", "只能查询90天范围内的数据");
						return;
					}
				} catch (e) {
					MyMessageTip.msg("提示",
							"时间格式错误,正确格式2000-01-01 01:01:01!", true);
					return;
				}
			}
//			if (viewType == "tjd") {
				var timeCnd = null;
				var body = {};
				if (datefrom != null && datefrom != ""
						&& (dateTo == null || dateTo == "")) {
					timeCnd = ['ge',
							['$', "to_char(FYSJ,'yyyy-mm-dd hh24:mi:ss')"],
							['s', datefrom]];
					body["dateFrom"] = datefrom;
				} else if (dateTo != null && dateTo != ""
						&& (datefrom == null || datefrom == "")) {
					timeCnd = ['le',
							['$', "to_char(FYSJ,'yyyy-mm-dd hh24:mi:ss')"],
							['s', dateTo]];
					body["dateTo"] = dateTo;
				} else if (dateTo != null && dateTo != "" && datefrom != null
						&& datefrom != "") {
					timeCnd = [
							'and',
							['ge', ['$', "to_char(FYSJ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', datefrom]],
							['le', ['$', "to_char(FYSJ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dateTo]]];
					body["dateFrom"] = datefrom;
					body["dateTo"] = dateTo;
				}
				var lxCnd = null;
				if (lx != null && lx != "" && lx != undefined) {
					lxCnd = ['eq', ['$', 'FYLX'], ['d', lx]];
					body["FYFS"] = lx;
				}
				var bqCnd = null;
				if (bq != null && bq != "" && bq != undefined) {
					bqCnd = ['eq', ['$', 'FYBQ'], ['d', bq]]
					body["FYBQ"] = bq;
				}
				var ygCnd = null;
				if (yg != null && yg != "" && yg != undefined) {
					ygCnd = ['eq', ['$', 'FYGH'], ['s', yg]]
					body["FYGH"] = yg;
				}
				var cnd = this.initCnd;
				if (timeCnd != null) {
					if (cnd != null) {
						cnd = ['and', cnd, timeCnd]
					} else {
						cnd = timeCnd;
					}
				}
				if (lxCnd != null) {
					if (cnd != null) {
						cnd = ['and', cnd, lxCnd]
					} else {
						cnd = lxCnd;
					}
				}
				if (bqCnd != null) {
					if (cnd != null) {
						cnd = ['and', cnd, bqCnd]
					} else {
						cnd = bqCnd;
					}
				}
				if (ygCnd != null) {
					if (cnd != null) {
						cnd = ['and', cnd, ygCnd]
					} else {
						cnd = ygCnd;
					}
				}
				if (cnd != null) {
					cnd = ['and', cnd, ['eq',['$','a.YFSB'],['l',this.mainApp['phis'].pharmacyId]]]
				} else {
					cnd = ['eq',['$','a.YFSB'],['l',this.mainApp['phis'].pharmacyId]];
				}
			    body["YF"]=this.mainApp['phis'].pharmacyId;
			    module.requestData.body = body;
				module.requestData.cnd = cnd;
				module.requestData.initCnd = cnd;
//			} else if (viewType == "br") {
//				
//			}
			module.requestData.serviceId = module.serviceId;
			module.requestData.serviceAction = module.queryActionId;
			module.loadData();
		}
	},
	onTabChange : function(tabPanel, newTab, curTab) {
		if (newTab.__inited) {
			debugger
			this.leftm.on("BeforeLoadData",
					this.onBeforeLoadDataLeft, this)
			this.leftm
					.on("loadData", this.onLoadDataLeft, this)
			this.moduleOperation("loadData",
					this.midiModules[newTab.id]);
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
		$require(cls, [
			function() {
				var m = eval("new " + cls + "(cfg)");
				m.setMainApp(this.mainApp);
				if (this.initDataId) {
					m.initDataId = this.initDataId;
				}
				this.midiModules[newTab.id] = m;
				this.leftm = m;
				m.on("BeforeLoadData",
						this.onBeforeLoadDataLeft, this)
				m.on("loadData", this.onLoadDataLeft, this)
				var p = m.initPanel();
				m.on("recordClick", this.onRecordClick,
						this)
				m.on("recordClick_br",
						this.onRecordClick_br, this)
				newTab.add(p);
				newTab.__inited = true
				this.tab.doLayout();
				this.moduleOperation("loadData",
							this.midiModules[newTab.id]);
				}, this ])
	},
	// 选中记录
	onRecordClick : function(r) {
		this.fireEvent("recordClick", r);
	},
	// 选中记录(病人TAB)
	onRecordClick_br : function(r) {
		this.fireEvent("recordClick_br", r);
	},
	getData : function() {
		var m = this.midiModules[this.tab.getActiveTab().id]
		var record = m.getSelectedRecord();
		return record;
	},
	onBeforeLoadDataLeft : function() {
		this.fireEvent("BeforeLoadDataLeft");
	},
	onLoadDataLeft : function() {
		this.fireEvent("LoadDataLeft");
	}
});