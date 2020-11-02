$package("chis.application.dc.script");

$import("chis.script.BizSimpleListView");

chis.application.dc.script.RabiesRecordList = function(cfg) {
	this.showButtonOnPT = true;
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.autoLoadSchema= true;
	chis.application.dc.script.RabiesRecordList.superclass.constructor.apply(this,
			[cfg]);
	this.on("getStoreFields",this.onGetStoreFields,this)
};

Ext.extend(chis.application.dc.script.RabiesRecordList, chis.script.BizSimpleListView, {
	onStoreLoadData : function(store, records, ops) {
		if (records.length == 0) {
			this.fireEvent("noRecord");
			return;
		}
		this.fireEvent("loadData", store);
		if (this.win) {
			this.win.doLayout();
		}
	},
	onGetStoreFields : function(fields) {
				fields.push({
							name : "_actions",
							type : "object"
						});
			},

	doAdd : function(json) {
		if (!json) {
			return;
		}
		this.store.add(new Ext.data.Record(json));
		this.grid.getSelectionModel().selectLastRow();
	},
	loadData : function() {	
		this.clear(); 
		if (this.store) {
			if (this.disablePagingTbr) {
				this.store.load()
			} else {
				var pt = this.grid.getBottomToolbar()
				if (this.requestData.pageNo == 1) {
					pt.cursor = 0;
				}
				pt.doLoad(pt.cursor)
			}
		}
	}
});