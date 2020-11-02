$package("phis.application.war.script")

$import("phis.script.SimpleList")

phis.application.war.script.WardPatientListView = function(cfg) {
	cfg.autoLoadData = false;
	cfg.listServiceId = "wardPatientQuery";
	phis.application.war.script.WardPatientListView.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.war.script.WardPatientListView, phis.script.SimpleList, {
			onRowClick : function(grid, index) {
				this.fireEvent("click", grid, index);
			},
			onDblClick : function(view, index, item, e) {
				// 打开医嘱处理
				this.fireEvent("mydblClick");
			},
			cypbRender : function(v, params, reocrd) {
				if (v == '出院证明') {
					return "<font color='red'>" + v + "</font>"
				}
				return v;
			},
			brqkRender : function(v, params, reocrd) {
				if (v == '危重') {
					// params.style = "color:red;";
					return "<font color='red'>" + v + "</font>"
				}
				return v;
			},
			brxbRender : function(v, params, reocrd) {
				if (v == '男') {
					params.style = "color:blue;";
				} else if (v == '女') {
					params.style = "color:#FF64B2;";
				}
				return v;
			},
			dateFormat : function(value, params, r, row, col) {
				return Ext.util.Format.date(Date
								.parseDate(value, "Y-m-d H:i:s"), 'Y.m.d')
			},
			onContextMenu : function(grid, rowIndex, e) {
				this.selectRow(rowIndex);
				this.parent.onContextMenu(grid, rowIndex, e);
			},
			selectRow : function(v) {
				if (!this.grid.hidden) {
					this.grid.el.focus()
				}
				try {
					if (this.grid && this.selectFirst) {
						var sm = this.grid.getSelectionModel()
						if (sm.selectRow) {
							sm.selectRow(v)
						}
						if (!this.grid.hidden) {
							var view = this.grid.getView()
							if (this.store.getCount() > 0) {
								view.focusRow(0)
							} else {
								var el = this.grid.el
								setTimeout(function() {
											el.focus()
										}, 300)
							}
						}
						this.fireEvent("click", this.grid, v)// 改变抛出事件名称
					}
				} catch (e) {
				}
			}
		});
