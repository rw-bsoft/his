/**
 * 药品私用信息-价格
 * 
 * @author : caijy
 */
$package("phis.application.mds.script")

$import("phis.script.SimpleList")

phis.application.mds.script.MedicinesPriceManageList = function(cfg) {
	cfg.disablePagingTbr = false;
	this.tbar = []
	cfg.group = "YPXH";
	cfg.groupTextTpl = "<table width='45%' style='color:#3764a0;font:bold !important;' border='0' cellspacing='0' cellpadding='0'><tr><td width='50%'>&nbsp;&nbsp;<b>{[values.rs[0].data.YPMC]}</b></td><td width='24%'><b>{[values.rs[0].data.YPGG]}</b></td><td width='6%'><div align='right'><b>{[values.rs[0].data.YPDW]}</b></div></td><td width='20%'><div align='left'><b>&nbsp;&nbsp;({[values.rs.length]} 条记录)</b></div></td></tr></table>"
	phis.application.mds.script.MedicinesPriceManageList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.mds.script.MedicinesPriceManageList,
		phis.script.SimpleList, {
			onRenderer_jg : function(value, metaData, r) {
				if (r.data.ZFPB == 1) {
					return "<img src='"
							+ ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/(00,04).png'/><font color='#ADADAD'>"
							+ value + "</font>"
				}
				return value;
			},
			// 加上鼠标移动提示记录是否已作废功能
			onReady : function() {
				phis.application.mds.script.MedicinesPriceManageList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
			},
			// 鼠标移动提示记录是否已作废功能
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record) {
						if (record.data.ZFPB == 1) {
							var rowEl = Ext.get(e.getTarget());
							rowEl.set({
										qtip : '价格已注销'
									}, false);

						}
					}
				}
			}

		});