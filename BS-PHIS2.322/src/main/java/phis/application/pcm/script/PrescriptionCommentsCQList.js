$package("phis.application.pcm.script")

$import("phis.script.EditorList")

phis.application.pcm.script.PrescriptionCommentsCQList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.remoteUrl = 'MedicineAll'
	cfg.remoteTpl = '<td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td>';
	phis.application.pcm.script.PrescriptionCommentsCQList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsCQList,
		phis.script.EditorList, {
	getRemoteDicReader : function() {
		return new Ext.data.JsonReader({
					root : 'mds',
					totalProperty : 'count'
				}, [{
							name : 'numKey'
						}, {
							name : 'YPXH'
						}, {
							name : 'YPMC'
						}]);
				},
			setBackInfo : function(obj, record) {
				obj.collapse();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var rowItem = griddata.itemAt(row);
				rowItem.set('YPXH', record.get("YPXH"));
				rowItem.set('YPMC', record.get("YPMC"));
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.remoteDic.lastQuery = "";
			}
		})