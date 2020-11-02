$package("phis.application.cfg.script")
$import("phis.script.SimpleList")
phis.application.cfg.script.ManufacturerForWZList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.showRowNumber = true;
	cfg.winState = "center";// cfg.winState=[100,50]两个写法都可以
	phis.application.cfg.script.ManufacturerForWZList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cfg.script.ManufacturerForWZList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				// this.mainApp['phis'].treasuryId = 50;
				if (!this.mainApp['phis'].treasuryId
						&& (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId)) {
					Ext.MessageBox.alert("提示", "您还没有选择库房， 请先选择库房 !");
					return;
				}
				if (this.mainApp['phis'].treasuryEjkf != 0
						&& this.mainApp['phisApp'].deptId != this.mainApp.topUnitId) {
					Ext.MessageBox.alert("提示", "该库房不是一级库房!");
					return;
				}
				return phis.application.cfg.script.ManufacturerForWZList.superclass.initPanel
						.apply(this, [sc]);
			},
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.configManufacturerForWZService";
				this.requestData.serviceAction = "manufacturerQuery";

				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
			// 刚打开页面时候默认选中第一条数据,这时候判断下作废按钮
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick();
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick();
				}
			},
			/*// 页面显示厂家状态的图标
			onRenderer : function(value, metaData, r) {
				var CJZT = r.get("CJZT");
				var src = (CJZT == 1) ? "yes" : "no";
				return "<img src='images/" + src + ".png'/>";
			},*/
			// 单击时改变作废按钮
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "canceled");
				btn = btn[0];
				if (r.data.CJZT == -1) {
					if (btn.getText().indexOf("取消") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("注销", "取消注销"));
				} else {
					btn.setText(btn.getText().replace("取消注销", "注销"));
				}

			},

			// 作废和取消作废
			doCanceled : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var body = {};
				body["CJXH"] = r.data.CJXH;
				body["CJZT"] = r.data.CJZT;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configManufacturerForWZService",
							serviceAction : "canceled",
							schemaList : "WL_SCCJ",
							method : "execute",
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doCanceled);
				} else {
					var btns = this.grid.getTopToolbar();
					var btn = btns.find("cmd", "canceled");
					btn = btn[0];
					if (r.data.CJZT == 1) {
						if (btn.getText().indexOf("取消") > -1) {
							return;
						}
						btn.setText(btn.getText().replace("注销", "取消注销"));
					} else {
						btn.setText(btn.getText().replace("取消注销", "注销"));
					}
					this.refresh();
				}
			},

			doRefresh : function() {
				this.refresh();
			},
			doClose : function() {
				this.closed();
			}

		})
