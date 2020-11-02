$package("phis.application.cic.script")

$import("phis.script.SimpleList", "phis.script.cookie.CookieOperater")

phis.application.cic.script.ClinicDiagnosisAllList = function(cfg) {
	cfg.showRowNumber = false;
	this.cookie = util.cookie.CookieOperater;
	phis.application.cic.script.ClinicDiagnosisAllList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cic.script.ClinicDiagnosisAllList,
		phis.script.SimpleList, {
			onReady : function() {
				phis.application.cic.script.ClinicDiagnosisAllList.superclass.onReady
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
			onDblClick : function() {
				var lastIndex = this.grid.getSelectionModel().lastActive;
				var record = this.grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("choose", record);
				}
			},
			getHTML : function(rowIndex) {
				var r = this.grid.getStore().getAt(rowIndex);
				if (!this.tpl) {
					var url = ClassLoader.appRootOffsetPath
							+ "phis/resources/css/app/desktop/images/icons/AB1.gif";
					var img = "<img src='" + url
							+ "' width='18px' height='15px' />";

					var html = "<span style='font-size:14px;color:blue;font-weight:bold;'>"
							+ img + "疾病【{JBMC}】简介</span>";
					html += "<p style='font-size:12px'>{BZXX}</p>";

					html = "<div style='padding:10px;1px solid #999;color:#555;background:#f9f9f9;max-height:620px;'>"
							+ html + "</div>";
					this.tpl = new Ext.XTemplate(html);
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "simpleLoad",
							schema : "phis.application.cfg.schemas.GY_JBBM",
							pkey : r.get("JBXH"),
							body : {},
							action : this.op, // 按钮事件
							module : this._mId
						});
				var data = resData.json.body;
				if (!data.BZXX) {
					data.BZXX = "暂无说明!";
				}
				return this.tpl.apply(data);
			}
		})
