/**
 * 个人既往史列表页面
 * 
 * @author : tianj
 */
$package("chis.application.hr.script")

$import("chis.script.BizSimpleListView")

chis.application.hr.script.PastHistoryList = function(cfg) {
	chis.application.hr.script.PastHistoryList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.hr.script.PastHistoryList, chis.script.BizSimpleListView, {
	loadData : function() {
		this.initCnd = ["eq", ["$", "empiId"], ["s", this.exContext.ids.empiId]];
		this.requestData.cnd = this.initCnd;
		chis.application.hr.script.PastHistoryList.superclass.loadData.call(this);
	},

	onSave : function(entryName, op, json, data) {
		this.fireEvent("refreshData","B_04");
		this.refresh();
	},

	refresh : function() {
		if (this.store) {
			this.store.load();
		}
	},

	getRemoveRequest : function(r) {
		return r.data;
	},

	loadModule : function(cls, entryName, item, r) {
		if (this.loading) {
			return;
		}
		var cmd = item.cmd;
		var cfg = {};
		cfg.title = this.title + '-' + item.text;
		cfg.entryName = entryName;
		cfg.op = cmd;
		cfg.empiId = this.exContext.ids.empiId;
		cfg.exContext = {};
		Ext.apply(cfg.exContext, this.exContext);

		if (cmd != 'create') {
			cfg.initDataId = r.id;
			cfg.exContext[entryName] = r;
		}
		if (this.saveServiceId) {
			cfg.saveServiceId = this.saveServiceId;
		}
		if (this.saveAction) {
			cfg.saveAction = this.saveAction;
		}
		var m = this.midiModules[cmd];
		if (!m) {
			this.loading = true;
			$require(cls, [function() {
						this.loading = false;
						cfg.autoLoadData = false;
						var module = eval("new " + cls + "(cfg)");
						module.on("save", this.onSave, this);
						module.on("close", this.active, this);
						module.on("checkRecord", this.onCheckRecord, this);
						module.on("checkHasRecord", this.onCheckHasRecord,
										this);
						module.on("checkHasType", this.onCheckHasType, this);
						module.opener = this;
						module.setMainApp(this.mainApp);
						this.midiModules[cmd] = module;
						this.fireEvent("loadModule", module);
						this.openModule(cmd, r, [100, 50]);
					}, this])
		} else {
			Ext.apply(m, cfg);
			this.openModule(cmd, r);
		}
	},

	onCheckRecord : function(checkMessage, pastHisTypeCode, diseaseCode) {
		for (var i = 0; i < this.store.getCount(); i++) {
			var storeItem = this.store.getAt(i);
			var disText = storeItem.get('diseaseText');
			var disCode = storeItem.get('diseaseCode');
			var typeCode = storeItem.get('pastHisTypeCode');
			if (pastHisTypeCode == typeCode) {
				var nullDisCode = typeCode + "01";
				if ((diseaseCode != nullDisCode && disCode == nullDisCode)
						|| (diseaseCode == nullDisCode && disCode != nullDisCode)) {
					checkMessage.push(storeItem);
				}
			}
		}
	},

	onCheckHasRecord : function(diseaseText, pastHisTypeCode) {
		var listCount = this.store.getCount();
		if (listCount < 1) {
			return false;
		}
		for (var i = 0; i < listCount; i++) {
			var storeItem = this.store.getAt(i);
			var disText = storeItem.get('diseaseText');
			var disCode = storeItem.get('diseaseCode');
			var typeCode = storeItem.get('pastHisTypeCode');
			if (disText == diseaseText && typeCode == pastHisTypeCode) {
				return true;
			}
			if (i == this.store.getCount() - 1) {
				return false;
			}
		}
	},

	onCheckHasType : function(pastHisTypeCode) {
		var index = this.store.find("pastHisTypeCode", pastHisTypeCode);
		if (index >= 0) {
			return true;
		} else {
			return false;
		}
	},

	getRemoveRequest : function(r) {
		return r.data
	}
	,doRefresh : function() {
		this.refresh();
	}
});