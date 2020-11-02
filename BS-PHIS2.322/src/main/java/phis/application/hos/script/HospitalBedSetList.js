$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.hos.script.HospitalBedSetList = function(cfg) {
	cfg.selectFirst = false
	phis.application.hos.script.HospitalBedSetList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.hos.script.HospitalBedSetList, phis.script.SimpleList, {
			onReady : function() {
				phis.application.hos.script.HospitalBedSetList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
			},
			doPrint : function() {
				MyMessageTip.msg("提示", "请选择机构科室后,再新增员工", true);
			},

			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (String(index) == 'false')
					return;
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record.data.ZYH) {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
									qtip : '<div style="font-size: 12;">该床位已有人 </div>'
								}, false);
					}
				}
			},
			onRenderer : function(value, metaData, r) {
				var ZYH = r.get("ZYH");
				if (ZYH) {
					var src = "user";
					return "<img src='resources/phis/resources/images/" + src + ".png'/>";
				}
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get('ZYH')) {
					MyMessageTip.msg("提示", "该床位已有病人,不能删除!", true);
					return;
				}
				phis.application.hos.script.HospitalBedSetList.superclass.doRemove
						.call(this);
			}
		});