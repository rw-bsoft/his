$package("phis.application.sto.script")

$import("phis.application.sto.script.StorehouseMySimpleDetailList");

phis.application.sto.script.StorehouseCheckOutDetailList = function(cfg) {
	cfg.remoteUrl = "Medicines";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="40px">{LSJG}</td><td width="40px">{KCSL}</td>';
	cfg.minListWidth = 600;
	cfg.queryParams = {
		"tag" : "ykck"
	};
	//cfg.columnNum=6;//换行
	cfg.count=6;
	cfg.toColumnNum=1;//换行到下一行的哪列
	cfg.labelText=" 零售合计:0  进货合计:0 ";
	phis.application.sto.script.StorehouseCheckOutDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this)
}
Ext.extend(phis.application.sto.script.StorehouseCheckOutDetailList,
		phis.application.sto.script.StorehouseMySimpleDetailList, {
			onAfterCellEdit : function(it, record, field, v) {
				if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);
				this.isEdit = true;
				if (v == "" || v == undefined) {
					v = 0;
				}
				if (it.id == "SQSL") {
					record.set("JHJE", (parseFloat(v) * parseFloat(record
									.get("JHJG"))).toFixed(4));
					record.set("LSJE", (parseFloat(v) * parseFloat(record
									.get("LSJG"))).toFixed(4));
				}
				this.doJshj();
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'YPCD'
								}, {
									name : 'YPXH'
								}, {
									name : 'CDMC'
								}, {
									name : 'LSJG'
								}, {
									name : 'JHJG'
								}, {
									name : 'YPMC'
								}, {
									name : 'YFGG'
								}, {
									name : 'YFDW'
								}, {
									name : 'KWBM'
								}, {
									name : 'KCSL'
								}]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var ypcd = record.get("YPCD");
				var ypxh = record.get("YPXH");
				var yfsb = this.yfsb;
				for (var i = 0; i < griddata.length; i++) {
					if (griddata.itemAt(i).get("YPCD") == ypcd
							&& griddata.itemAt(i).get("YPXH") == ypxh
							&& i != row) {
						MyMessageTip.msg("提示", "该药品已存在,请修改此药品", true);
						return;
					}
				}
				var body = {
					"YPXH" : ypxh,
					"YPCD" : ypcd,
					"YFSB" : yfsb
				};
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryKcslActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doCommit);
					return;
				}
				obj.collapse();
				var rowItem = griddata.itemAt(row);
				rowItem.set('KCSL', ret.json.body.YKKC);
				rowItem.set('YFKCSL', ret.json.body.YFKC);
				rowItem.set('YPCD', record.get("YPCD"));
				rowItem.set('YPXH', record.get("YPXH"));
				rowItem.set('CDMC', record.get("CDMC"));
				rowItem.set('YPMC', record.get("YPMC"));
				rowItem.set('LSJG', record.get("LSJG"));
				rowItem.set('BZLJ', record.get("LSJG"));
				rowItem.set('JHJG', record.get("JHJG"));
				rowItem.set('YKJH', record.get("JHJG"));
				rowItem.set('YPGG', record.get("YFGG"));
				rowItem.set('YPDW', record.get("YFDW"));
				rowItem.set('PFJG', record.get("PFJG"));
				rowItem.set('KWBM', record.get("KWBM"));
				if (rowItem.get("SQSL") != null && rowItem.get("SQSL") != ""
						&& rowItem.get("SQSL") != 0) {
					rowItem.set('LSJE', (record.get("LSJG") * rowItem
									.get("SQSL")).toFixed(4));
					rowItem.set('JHJE', (record.get("JHJG") * rowItem
									.get("SQSL")).toFixed(4));
					this.doJshj();
				} else {
					rowItem.set('LSJE', 0);
					rowItem.set('JHJE', 0);
				}
				// obj.setValue(record.get("YPMC"));
				this.remoteDic.lastQuery = "";
				//this.remoteDic.clearValue();//注释掉防止第二次输入,全部为空
				obj.setValue(record.get("YPMC"));
				obj.triggerBlur();
				this.isEdit = true;
				this.grid.startEditing(row, 7);
			},
			onLoadData : function(store) {
				if (store == null || store.length == 0) {
					return;
				}
				var l = store.getCount();
				for (var i = 0; i < l; i++) {
					var data = store.getAt(i).data;
					alert(data.YFKCSB)
					var body = {
						"YPXH" : data.YPXH,
						"YPCD" : data.YPCD,
						"KCSB" : data.KCSB,
						"YFSB" : this.yfsb,
						"YFKCSB" : data.YFKCSB
					};
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryKcslActionId,
								body : body
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.doCommit);
						return;
					}
					store.getAt(i).set("KCSL", ret.json.body.YKKC);
					store.getAt(i).set("YFKCSL", ret.json.body.YFKC);
				}

			},
			doJshj : function() {
				if (!this.label) {
					return;
				}
				var n = this.store.getCount()
				var hjje = 0;
				var lsje = 0;
				for (var i = 0; i < n; i++) {
					var r = this.store.getAt(i);
					if (r.get("YPXH") == null || r.get("YPXH") == ""
							|| r.get("YPXH") == undefined) {
						continue;
					}
					if (r.get("JHJE") != null && r.get("JHJE") != ""
							&& r.get("JHJE") != undefined) {
						hjje += parseFloat(r.get("JHJE"));
					}
					if (r.get("LSJE") != null && r.get("LSJE") != ""
							&& r.get("LSJE") != undefined) {
						lsje += parseFloat(r.get("LSJE"));
					}

				}
				this.label.setText("合计   零售金额:" + lsje.toFixed(4) + " 进货金额:"
						+ hjje.toFixed(4));
			},
			onLoadData : function(store) {
				if (store == null || store.length == 0) {
					return;
				}
				var l = store.getCount();
				for (var i = 0; i < l; i++) {
					var data = store.getAt(i).data;
					var body = {
						"YPXH" : data.YPXH,
						"YPCD" : data.YPCD,
						"KCSB" : data.KCSB,
						"YFSB" : this.yfsb
					};
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryKcslActionId,
								body : body
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.doCommit);
						return;
					}
					store.getAt(i).set("KCSL", ret.json.body.YKKC);
					store.getAt(i).set("YFKCSL", ret.json.body.YFKC);
				}

			}
		})