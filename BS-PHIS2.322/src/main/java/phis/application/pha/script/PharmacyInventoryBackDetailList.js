$package("phis.application.pha.script")

$import("phis.application.pha.script.PharmacyMySimpleDetailList")

phis.application.pha.script.PharmacyInventoryBackDetailList = function(cfg) {
	cfg.remoteUrl = "Medicines";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="40px">{LSJG}</td><td width="40px">{JHJG}</td><td width="80px">{YPPH}</td><td width="100px">{YPXQ}</td>';
	cfg.queryParams = {
		"tag" : "db"
	};
	phis.application.pha.script.PharmacyInventoryBackDetailList.superclass.constructor
			.apply(this, [cfg])

}
Ext.extend(phis.application.pha.script.PharmacyInventoryBackDetailList,
		phis.application.pha.script.PharmacyMySimpleDetailList, {
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				obj.collapse();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var kcsb = record.get("KCSB");
				for (var i = 0; i < griddata.length; i++) {
					if (griddata.itemAt(i).get("KCSB") == kcsb && i != row) {
						MyMessageTip.msg("提示", "该药品已存在,请修改此药品量", true);
						return;
					}
				}
				var rowItem = griddata.itemAt(row);
				rowItem.set('KCSL', record.get("KCSL"));
				rowItem.set('YPCD', record.get("YPCD"));
				rowItem.set('YPXH', record.get("YPXH"));
				rowItem.set('CDMC', record.get("CDMC"));
				rowItem.set('YPMC', record.get("YPMC"));
				rowItem.set('LSJG', record.get("LSJG"));
				rowItem.set('JHJG', record.get("JHJG"));
				rowItem.set('YFGG', record.get("YFGG"));
				rowItem.set('YFDW', record.get("YFDW"));
				rowItem.set('YFBZ', record.get("YFBZ"));
				rowItem.set('QRGG', record.get("YFGG"));
				rowItem.set('QRDW', record.get("YFDW"));
				rowItem.set('QRBZ', record.get("YFBZ"));
				rowItem.set('PFJG', record.get("PFJG"));
				rowItem.set('KCSB', record.get("KCSB"));
				rowItem.set('YPPH', record.get("YPPH"));
				rowItem.set('YPXQ', record.get("YPXQ"));
				if (rowItem.get("YPSL") != null && rowItem.get("YPSL") != ""
						&& rowItem.get("YPSL") != 0) {
					rowItem.set('LSJE', (record.get("LSJG") * rowItem
									.get("YPSL")).toFixed(4));
					rowItem.set('JHJE', (record.get("JHJG") * rowItem
									.get("YPSL")).toFixed(4));
				} else {
					rowItem.set('LSJE', 0);
					rowItem.set('JHJE', 0);
				}
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.remoteDic.lastQuery = "";
				this.doJshj();
				this.grid.startEditing(row, 7);
			},
			onAfterCellEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "YPMC") {
					if (v == null || v == "" || v == 0) {
						record.set('YPCD', "");
						record.set('YPXH', "");
						record.set('CDMC', "");
						record.set('YPMC', "");
						record.set('LSJG', 0);
						record.set('JHJG', 0);
						record.set('YFGG', "");
						record.set('YFDW', "");
						record.set('YFBZ', 0);
						record.set('QRGG', "");
						record.set('QRDW', "");
						record.set('QRBZ', 0);
						record.set('PFJG', 0);
						record.set('KCSB', "");
						record.set('YPPH', "");
						record.set('YPXQ', "");
						record.set('YPSL', 0);
						record.set('QRSL', 0);
						record.set('YPSL', 0);
						record.set('LSJE', 0);
						record.set('JHJE', 0);
						this.doJshj();
					}
				}
				if (it.id == "YPSL") {
					var pfje = 0;
					var jhje = 0;
					var lsje = 0;
					if (((v != null && v != "") || v == 0)
							&& record.get("YPXH") != undefined
							&& record.get("YPXH") != "") {
						if (-v > record.get("KCSL")) {
							MyMessageTip.msg("提示", "库存数量不足", true);
							v = -record.get("KCSL");
							record.set("YPSL", v);
						}
						record.set("QRSL", v);
						var pfje = (v * record.get("PFJG")).toFixed(4);
						var jhje = (v * record.get("JHJG")).toFixed(4);
						var lsje = (v * record.get("LSJG")).toFixed(4);
					}
					record.set("JHJE", jhje);
					record.set("PFJE", pfje);
					record.set("LSJE", lsje);
					if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);
					this.doJshj();
				}
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				var count = this.store.getCount();
				for (var i = 0; i < count; i++) {
					var body = {
						"YPXH" : this.store.getAt(i).get("YPXH"),
						"YPCD" : this.store.getAt(i).get("YPCD"),
						"MBBZ" : this.store.getAt(i).get("YFBZ"),
						"KCSB" : this.store.getAt(i).get("KCSB")
					};
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryKcslActionId,
								body : body
							});
					var kcsl = ret.json.body.KCSL;
					if (ret.code > 300) {
						if(!this.isRead){
						this.processReturnMsg(ret.code, ret.msg,
								this.onStoreLoadData);
						}
						kcsl = 0;
					}
					this.store.getAt(i).set("KCSL", kcsl);
				}
					this.doJshj();
			}
		})