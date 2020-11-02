$package("phis.application.cic.script")

$import("phis.script.SimpleList", "util.dictionary.DictionaryLoader")

phis.application.cic.script.ClinicDiagnosisPositionList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.showRowNumber = false;
	cfg.disablePagingTbr = true;
	phis.application.cic.script.ClinicDiagnosisPositionList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cic.script.ClinicDiagnosisPositionList,
		phis.script.SimpleList, {
			onReady : function() {
				phis.application.cic.script.ClinicDiagnosisPositionList.superclass.onReady
						.call(this);
				this.fillStore();
			},
			onStoreLoadData : function() {
				this.fillStore();
			},
			onDblClick : function() {
				var lastIndex = this.grid.getSelectionModel().lastActive;
				var record = this.grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("choose", record);
				}
			},
			fillStore : function() {
				if (!this.store) {
					return;
				}
				// 字典项目填充。
				if (!this.dic) {
					this.dic = util.dictionary.DictionaryLoader.load({
								id : "phis.dictionary.position"
							});
				}

				var records = new Array();
				for (var i = 0; i < this.dic.items.length; i++) {
					var rec = {
						"ZDBW" : this.dic.items[i].key,
						"BWMC" : this.dic.items[i].text
					};
					records.push(new Ext.data.Record(rec));
				}
				this.store.removeAll();
				this.store.add(records);
			}
		});