$package("phis.application.sup.script");
$import("phis.script.SelectList")

phis.application.sup.script.ApplyManagementImportDetailList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	phis.application.sup.script.ApplyManagementImportDetailList.superclass.constructor
			.apply(this, [cfg])

}
Ext.extend(phis.application.sup.script.ApplyManagementImportDetailList,
		phis.script.SelectList, {
			loadData : function(SLKS, SLGH) {
				this.clear();
				this.requestData.serviceId = "phis.applyManagementService";
				this.requestData.serviceAction = "sLXXDetailQuery";
				var body = {};
				body["ZBLB"] = this.zblb;
				body["KFXH"] = this.mainApp['phis'].treasuryId;
				body["KSDM"] = this.ksdm;
				body["SLKS"] = SLKS;
				body["SLGH"] = SLGH;
				this.requestData.cnd = body;
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
			doCommit : function() {
				var records = this.getSelectedRecords()
				if (records == null) {
					return;
				}
				this.clearSelect();
				this.fireEvent("checkData", records);
			},
			doReject : function() {
				var records = this.getSelectedRecords()
				if (records.length == 0) {
					return;
				}
				var body = [];
				Ext.each(records, function() {
							body.push(this.data);
						});
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "rejectSLXX",
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					MyMessageTip.msg("提示", "退回成功!", true);
					// this.loadData(this.initDataBody);
					// this.changeButtonState("new");
					// // this.fireEvent("winClose", this);
					this.fireEvent("afterReject", this);
                    this.clearSelect();
				}
			},
			expansion : function(cfg) {
				var label = new Ext.form.Label({
					html : "<div id='totcount' align='center' style='color:black'>明细条数：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			onStoreLoadData : function(store, records, ops) {
				// this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					document.getElementById("totcount").innerHTML = "明细条数：0";
					// 如果左边所选中的右边的明细没有的话  左边对应的数据不显示。
					if(this._ctr){
						var r = this._ctr.getSelectedRecord();
						this._ctr.store.remove(r);
						this._ctr.grid.getView().refresh();// 刷新行号
						if (this._ctr.store.getCount() > 0) {
							this._ctr.selectRow(0);
						}
					}
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				store.commitChanges();
				// var store = this.grid.getStore();
				var n = store.getCount();
				document.getElementById("totcount").innerHTML = "明细条数：" + n;

			}

		})