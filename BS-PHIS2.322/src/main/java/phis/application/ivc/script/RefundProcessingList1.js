$package("phis.application.ivc.script")

/**
 * 处方组套维护
 */
$import("phis.script.SimpleList")

phis.application.ivc.script.RefundProcessingList1 = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showRowNumber = true;
	cfg.autoLoadData = false
	phis.application.ivc.script.RefundProcessingList1.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.ivc.script.RefundProcessingList1,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.clinicChargesProcessingService";
				this.requestData.serviceAction = "queryTF01";
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
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				this.doCountTFJE();
				var r = this.getSelectedRecord();
				this.opener.setDListloadData(r.data);
			},
			doCountTFJE : function() {
				var store = this.grid.getStore();
				var n = store.getCount();
				var tf01s = [];
				var tf02s = [];
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (!r.data.BZXX || r.data.BZXX == '新处方'
							|| r.data.BZXX == '已发药' || r.data.BZXX == '已执行') {
						tf01s.push(r.data);
					}
					if (!r.data.BZXX || r.data.BZXX == '已发药'
							|| r.data.BZXX == '已执行') {
						tf02s.push(r.data);
					}
				}
				if (tf01s.length != n) {
					var ZFJE = 0;
					for (var i = 0; i < tf01s.length; i++) {
						var tf01 = tf01s[i];
						for (var j = 0; j < this.details.length; j++) {
							var ddata = this.details[j];
							if(ddata.ZFYP==1){
							continue
							}
							if (tf01.CFLX == ddata.CFLX
									&& tf01.CFSB == ddata.CFSB) {
								ZFJE = (parseFloat(ZFJE) + (parseFloat(ddata.HJJE) * parseFloat(ddata.ZFBL)))
										.toFixed(2);
							}
						}
					}
					var TFJE = (parseFloat(this.opener.opener.MZXX.ZFJE) - ZFJE)
							.toFixed(2);
					this.opener.opener.tfForm1.form.getForm().findField("TFJE")
							.setValue(TFJE);
				} else {
					this.opener.opener.tfForm1.form.getForm().findField("TFJE")
							.setValue("0.00");
				}
			},
			onRowClick : function(grid, index, e) {
				this.selectedIndex = index;
				var r = this.getSelectedRecord();
				this.opener.setDListloadData(r.data);
				this.opener.opener.tfForm1.form.getForm().findField("TFFP")
						.focus();
			},
			onDblClick : function(grid, index, e) {
				var r = this.getSelectedRecord();
				if (!r)
					return;
				if (r.get("BZXX") == "退费") {
					r.set("BZXX", "")
				} else if (!r.get("BZXX")) {
					r.set("BZXX", "退费")
				} else if (r.get("BZXX") == '已发药') {
					MyMessageTip.msg("提示", "当前处方中的药品已发药，请先进行退药操作!", true);
				} else if (r.get("BZXX") == '已执行') {
					MyMessageTip.msg("提示", "当前检查单已执行，不能进行退费操作!", true);
				}
				r.store.commitChanges();
				this.doCountTFJE();
			}
		})
