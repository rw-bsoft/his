$package("phis.application.sup.script")

$import("phis.script.SelectList")

phis.application.sup.script.FixedAssetsList = function(cfg) {
	cfg.serviceId = "materialsOutService";
	cfg.listServiceId = "getFixedAssetsInformation";
	cfg.saveActionId = "getFixedAssets";
	cfg.modal = true;
	this.closeAction = true;
	cfg.width = 800;
	cfg.height = 500;
	this.showRowNumber = true;
	cfg.mutiSelect = true;
	cfg.selectFirst = false;// 取消默认选中
	phis.application.sup.script.FixedAssetsList.superclass.constructor.apply(
			this, [ cfg ])
	this.on("winShow", this.onWinShow, this);

}
var recordIds = new Array();
Ext.extend(phis.application.sup.script.FixedAssetsList, phis.script.SelectList,
		{
			expansion : function(cfg) {
				cfg.sm.handleMouseDown = Ext.emptyFn// 只允许点击check列选中
				cfg.sm.on("rowselect", this.rowSelect, this);
				cfg.sm.on("rowdeselect", this.rowdeSelect, this);
			},
			onStoreLoadData : function(store, records, ops) {
				var rows = [];
				_ctx = this;
				store.each(function(record, index) {
					if (_ctx.containsArray(recordIds, record.get("ZBXH"))) {
						rows.push(index);
					}
				});
				// this.grid.selModel.selectRows(rows, true);
			},
			rowSelect : function(e, rowIndex, record) {
				if (!this.containsArray(recordIds, record.get("ZBXH")))
					recordIds.push(record.get("ZBXH"));
			},
			rowdeSelect : function(e, rowIndex, record) {
				if (this.containsArray(recordIds, record.get("ZBXH"))) {
					this.RemoveArray(recordIds, record.get("ZBXH"));
				}
			},

			onWinShow : function() {
				if (this.grid) {
					if (this.cndField) {
						this.cndField.setValue("");
					}
				}
			},
			RemoveArray : function(array, attachId) {
				for ( var i = 0, n = 0; i < array.length; i++) {
					if (array[i] != attachId) {
						array[n++] = array[i]
					}
				}
				array.length -= 1;
			},
			containsArray : function(array, attachId) {
				for ( var i = 0; i < array.length; i++) {
					if (array[i] == attachId) {
						return true;
						break;
					}
				}
				return false;
			},
			openModule : function(cmd, r, xy) {

				com.bsoft.phis.cfg.ConfigCostEditorList.superclass.openModule
						.call(this, cmd, r, xy)
				var module = this.midiModules[cmd]
				var win = module.getWin();
				var default_xy = win.el.getAlignToXY(win.container, 'c-c');
				win.setPagePosition(default_xy[0], 100);

			},
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				recordIds = [];
				this.requestData.serviceId = "phis.materialsOutService";
				this.requestData.serviceAction = this.listServiceId;
				this.requestData.cnd = this.WZXH;
				this.requestData.djxh = this.DJXH;
				this.requestData.kcxh = this.KCXH;
				this.requestData.th = this.th;
				this.requestData.tableName = this.tableName;
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
			// 调入
			doCallIn : function() {
				this.doSave(recordIds);
			},
			doSave : function(daras) {
				this.saving = true
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : this.saveActionId,
					body : daras
				}, function(code, msg, json) {
					this.grid.el.unmask()
					this.saving = false
					if (json.body) {
						this.operater.zczb = json.body;
					}
					this.operater.doCre();

				}, this)// jsonRequest
			},
			doExit : function() {
				this.win.hide();
			}
		})