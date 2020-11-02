/**
 * 妇保新生儿访视记录整体页面
 * 
 * @author : zhouw
 */
$package("chis.application.mhc.script.deliveryHtml")
$import("util.Accredit", "chis.script.BizCombinedModule2")
chis.application.mhc.script.deliveryHtml.DeliveryChildrenvisitHtmlModul = function(cfg) {
	cfg.itemWidth = 176
	chis.application.mhc.script.deliveryHtml.DeliveryChildrenvisitHtmlModul.superclass.constructor.apply(this, [cfg])
	this.height = 550;
}

Ext.extend(chis.application.mhc.script.deliveryHtml.DeliveryChildrenvisitHtmlModul,
				chis.script.BizCombinedModule2, {
					initPanel : function() {
						var panel = chis.application.mhc.script.deliveryHtml.DeliveryChildrenvisitHtmlModul.superclass.initPanel.call(this)
						this.panel = panel;
						this.list = this.midiModules[this.actions[0].id];
						this.grid = this.list.grid;
						this.list.on("loadData", this.onLoadGridData, this)
						this.grid.on("rowclick", this.onRowClick, this)
						this.form = this.midiModules[this.actions[1].id];
						this.form.on("save", this.onFormSave, this);

						return panel;
					},
					onLoadGridData : function(store) {
						if (store.getCount() == 0) {
							return;
						}
						var index = 0;
						var selectRecord = this.exContext.args.visitId;
						if (selectRecord) {
							index = this.list.store.find("visitId",
									selectRecord);
						}
						this.list.selectedIndex = index > -1 ? index : 0;
						var r = store.getAt(this.list.selectedIndex).data;
						var data = {};
						data["visitId"] = r["visitId"];
						data["visitDate"] = r["visitDate"];
						var v = this.getBabyVisitRecord(data);
						this.form.doCreate();
						this.form.initFormData(v);
					},
					onRowClick : function() {
						var r = this.grid.getSelectionModel().getSelected().data;
						if (!r) {
							return;
						}
						var data = {};
						data["visitId"] = r["visitId"];
						data["visitDate"] = r["visitDate"];
						this.exContext.args.visitId = r["visitId"];
						var v = this.getBabyVisitRecord(data);
						this.form.doCreate();
						this.form.initFormData(v);
					},
					// 传递数据，也就是刷新数据
					refresh : function() {
						Ext.apply(this.form.exContext, this.exContext);
						Ext.apply(this.list.exContext, this.exContext);
						this.list.loadData();
						this.form.doCreate();
					},
					onFormSave : function(entryName, op, json, data) {
						Ext.apply(data, json);
						this.exContext.args.visitId = data.visitId;
						this.list.loadData();
					},
					onTabChangeTo : function() {// 切换tab触发
						this.list.loadData();
						this.form.doCreate();
					},
					getBabyVisitRecord : function(data) {
						if (!data) {
							return;
						}
						var result = util.rmi.miniJsonRequestSync({
									serviceId : "chis.pregnantRecordService",
									serviceAction : "selectBabyVisitRecord",
									method : "execute",
									body : data
								});
						var info = result.json.body;
						if (info) {
							return info;
						}

					}
				})