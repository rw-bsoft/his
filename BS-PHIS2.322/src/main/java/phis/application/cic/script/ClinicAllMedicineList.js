$package("phis.application.cic.script")

/**
 * 处方组套维护list zhangyq 2012.05.25
 */
$import("phis.script.SimpleList")

phis.application.cic.script.ClinicAllMedicineList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.autoLoadData = false;
	cfg.listServiceId = "medicineQuery";
	phis.application.cic.script.ClinicAllMedicineList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cic.script.ClinicAllMedicineList,
		phis.script.SimpleList, {
			initPanel : function() {
				if (this.openBy == "ward") {
					this.requestData.wardId = this.exContext.brxx.get("BRBQ")
				}
				var grid = phis.application.cic.script.ClinicAllMedicineList.superclass.initPanel
						.call(this);
				return grid;
			},
			onDblClick : function() {
				var lastIndex = this.grid.getSelectionModel().lastActive;
				var record = this.grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("choose", record);
				}
			},
			onReady : function() {
				phis.application.cic.script.ClinicAllMedicineList.superclass.onReady
						.call(this);
				this.grid.un('rowcontextmenu', this.onContextMenu, this);
				this.grid.on('rowcontextmenu', function(grid, rowIndex, e) {
							e.preventDefault();
							grid.getSelectionModel().selectRow(rowIndex);
							var tip = new Ext.ToolTip({
										html : this.getHTML(rowIndex),
										dismissDelay : 0,
										width : 500,
										autoScroll : true,
										style : "background:#f9f9f9;"
									});
							tip.showAt([0, 0])
						}, this);
			},
			getHTML : function(rowIndex) {
				var r = this.grid.getStore().getAt(rowIndex);
				if (!this.tpl) {
					var url = ClassLoader.appRootOffsetPath
							+ "phis/resources/css/app/desktop/images/icons/AB1.gif";
					var img = "<img src='" + url
							+ "' width='18px' height='15px' />";

					var html = "<span style='font-size:14px;color:blue;font-weight:bold;'>"
							+ img + "药品【{YPMC}】简介</span>";
					html += "<p style='font-size:12px;'>{MESS}</p>";

					html = "<div style='padding:10px;1px solid #999;color:#555;background:#f9f9f9;max-height:620px;'>"
							+ html + "</div>";
					this.tpl = new Ext.XTemplate(html);
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "simpleLoad",
							schema : "phis.application.mds.schemas.YK_TYPK",
							pkey : r.get("YPXH"),
							body : {},
							action : this.op, // 按钮事件
							module : this._mId
						});
				var data = resData.json.body;
				if (!data.MESS) {
					data.MESS = "暂无说明!";
				}
				return this.tpl.apply(data);
			}
		})
