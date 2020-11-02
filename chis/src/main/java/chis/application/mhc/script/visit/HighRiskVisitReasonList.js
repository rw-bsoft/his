/**
 * 孕妇高危因素列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.visit")
$import("chis.script.BizSimpleListView")
chis.application.mhc.script.visit.HighRiskVisitReasonList = function(cfg) {
	cfg.autoLoadSchema = false
	cfg.autoLoadData = false
	cfg.disablePagingTbr = true
	cfg.pageSize = 50
	chis.application.mhc.script.visit.HighRiskVisitReasonList.superclass.constructor.apply(
			this, [cfg])
	this.width = 600;
	this.height = 400
}

Ext.extend(chis.application.mhc.script.visit.HighRiskVisitReasonList,
		chis.script.BizSimpleListView, {

			doConfirm : function() {
				var records = []
				var score = 0
				var group = ""
				var remark = ""
				for (var i = 0; i < this.store.getCount(); i++) {
					var storeItem = this.store.getAt(i)
					score += storeItem.get("highRiskScore")
					group += storeItem.get("highRiskLevel")
					remark += storeItem.get("highRiskReasonId_text")
					var r = {}
					var items = this.schema.items
					for (var j = 0; j < items.length; j++) {
						var it = items[j]
						if (it.dic) {
							r[it.id + "_text"] = storeItem.get(it.id + "_text")
						}
						r[it.id] = storeItem.get(it.id)
					}
					records.push(r);
					remark += ",";
				}
				if (group.indexOf("C") != -1) {
					group = "C"
				} else if (group.indexOf("B") != -1) {
					group = "B"
				} else if (group.indexOf("A") != -1) {
					group = "A"
				}

				var record = {
					"highRiskScore" : score,
					"highRiskLevel" : group,
					"highRiskReason" : remark.substring(0, remark.length - 1),
					"highRisknesses" : records
				}
				this.fireEvent("confirm", record);
			},

			doRemove : function() {
				var r = this.getSelectedRecord(true)
				if (r == null) {
					return
				}
				for (var i = 0; i < r.length; i++) {
					var record = r[i];
					this.store.remove(record)
				}
			},

			loadData : function() {
				this.requestData.cnd = [
						'and',
						['eq', ["$", "visitId"],
								['s', this.exContext.args.visitId]],
						[
								'eq',
								["$", "pregnantId"],
								[
										's',
										this.exContext.ids["MHC_PregnantRecord.pregnantId"] || ""]]];
				chis.application.mhc.script.visit.HighRiskVisitReasonList.superclass.loadData
						.call(this);
			},

			onDblClick : function(grid, index, e) {
				if (this.actions.length > 0)
					this.doRemove();
			}
		})
