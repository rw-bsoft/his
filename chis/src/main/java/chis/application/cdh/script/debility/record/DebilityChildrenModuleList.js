/**
 * 体弱儿档案列表页面 (整体模块中的)
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.debility.record");
$import("chis.script.BizSimpleListView");
chis.application.cdh.script.debility.record.DebilityChildrenModuleList = function(cfg) {
	chis.application.cdh.script.debility.record.DebilityChildrenModuleList.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false
	this.selectedIndex = 0
	this.disablePagingTbr = true
	this.on("getStoreFields", this.onGetStoreFields, this)
};

Ext.extend(chis.application.cdh.script.debility.record.DebilityChildrenModuleList,
		chis.script.BizSimpleListView, {

			onGetStoreFields : function(fields) {
				fields.push({
							name : "_actions",
							type : "object"
						});
			},

			onStoreBeforeLoad : function() {

			},

			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store, this.selectedIndex)
				this.selectRow(this.selectedIndex);
			}

		});