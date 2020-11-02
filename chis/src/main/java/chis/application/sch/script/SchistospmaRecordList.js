$package("chis.application.sch.script");

$import("app.modules.list.SimpleListView")

chis.application.sch.script.SchistospmaRecordList = function(cfg) {
//	cfg.disablePagingTbr = true;
	chis.application.sch.script.SchistospmaRecordList.superclass.constructor.apply(this, [cfg]);
//	this.on("getStoreFields", this.onGetStoreFields, this)
}

Ext.extend(chis.application.sch.script.SchistospmaRecordList, chis.script.BizSimpleListView, {
			loadData : function() {
				this.requestData.cnd = ['eq', ['$', 'a.empiId'],
						['s', this.exContext.ids.empiId]];
				chis.application.sch.script.SchistospmaRecordList.superclass.loadData
						.call(this);
			}

//			onGetStoreFields : function(fields) {
//				fields.push({
//							name : "_actions",
//							type : "object"
//						});
//			}
		});
