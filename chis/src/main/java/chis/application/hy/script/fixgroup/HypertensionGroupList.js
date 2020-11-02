// 转组列表。
$package("chis.application.hy.script.fixgroup");

$import("chis.script.BizSimpleListView");

chis.application.hy.script.fixgroup.HypertensionGroupList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.enableCnd = false;
	cfg.disablePagingTbr = true;// 分页工具栏 
	chis.application.hy.script.fixgroup.HypertensionGroupList.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.hy.script.fixgroup.HypertensionGroupList,
		chis.script.BizSimpleListView, {
			loadData : function() {
				this.initCnd = ['eq', ['$', 'a.empiId'],
						['s', this.exContext.ids.empiId]];
				this.requestData.cnd = this.initCnd;
				chis.application.hy.script.fixgroup.HypertensionGroupList.superclass.loadData
						.call(this);
			},
			doAdd : function(data) {
				if (!data) {
					return;
				}
				this.store.add(new Ext.data.Record(data));
				this.grid.getSelectionModel().selectLastRow();
				var r = this.getSelectedRecord();
				var n = this.store.indexOf(r);
				this.fireEvent("toLoadFrom",this.grid,n);
			}

		});