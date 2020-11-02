$package("phis.application.sto.script")

$import("phis.script.EditorList")

phis.application.sto.script.StorehouseFinancialAcceptanceDetailList = function(cfg) {
	this.mutiSelect = cfg.mutiSelect || true;
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.minListWidth = 600;
	cfg.modal = this.modal = true;
	phis.application.sto.script.StorehouseFinancialAcceptanceDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}
Ext.extend(phis.application.sto.script.StorehouseFinancialAcceptanceDetailList,
		phis.script.EditorList, {
			onRenderer : function(value, metaData, r) {
				return (parseFloat(r.data.LSJE) - parseFloat(r.data.JHHJ))
						.toFixed(4);
			},
			// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				if (it.id == "JHJG") {
					if(v==null||v==undefined){
					v=0;
					record.set("JHJG",0);
					}
					var jhhj = (parseFloat(v) * parseFloat(record.get("RKSL")))
							.toFixed(4);
					record.set("JHHJ", jhhj);
					record.set("JXCJ", (parseFloat(record.get("LSJE")) - jhhj)
									.toFixed(4));
					record.set("FKJE", (parseFloat(YPKL) * jhhj).toFixed(4));
					this.doJshj();
				}
				if (it.id == "YPKL") {
					if (v == null || v == undefined) {
						record.set("FKJE", 0);
						record.set("YPKL", 0);
					} else {
						record.set("FKJE", (parseFloat(v) * parseFloat(record
										.get("JHHJ"))).toFixed(2));
					}
				}
				if (it.id == "FKJE") {
					if (v > parseFloat(record.get("JHHJ"))) {
						MyMessageTip.msg("提示", "付款金额大于进货合计!", true);
						record.set("FKJE", parseFloat(record.get("JHHJ")));
					}
					if (v == null || v == undefined) {
						record.set("YPKL", 0);
						record.set("FKJE", 0);
					} else {
						record
								.set(
										"YPKL",
										(parseFloat(record.get("FKJE")) / parseFloat(record
												.get("JHHJ"))).toFixed(2));
					}
				}
				if (it.id == "JHHJ") {
					if(v==null||v==undefined){
					v=0;
					record.set("JHHJ",0);
					}
					var jhjg = (parseFloat(v) / parseFloat(record.get("RKSL")))
							.toFixed(4);
					record.set("JHJG", jhjg);
					record.set("JXCJ",
							(parseFloat(record.get("LSJE")) - parseFloat(v))
									.toFixed(4));
					record.set("YFJE", (parseFloat(YPKL) * parseFloat(v))
									.toFixed(4));
					this.doJshj();
				}
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				this.label = new Ext.form.Label({
					text : "合计   零售金额:0  进货金额:0 进销差价:0"
				})
				cfg.bbar = [];
				cfg.bbar.push(this.label);
			},
			doJshj : function() {
				var n = this.store.getCount()
				var hjje = 0;
				var lsje = 0;
				for (var i = 0; i < n; i++) {
					var r = this.store.getAt(i);
					if(r.get("YPXH")==null||r.get("YPXH")==""||r.get("YPXH")==undefined){
					continue;
					}
					hjje += parseFloat(r.get("JHHJ"));
					lsje += parseFloat(r.get("LSJE"));
				}
				this.label.setText("合计   零售金额:"+ lsje.toFixed(4) + " 进货金额:" + hjje.toFixed(4)+ " 进销差价:" + (lsje-hjje).toFixed(4));
//				document.getElementById("YK_RK02").innerHTML = "合计   零售金额:"
//						+ lsje.toFixed(2) + " 进货金额:" + hjje.toFixed(2)
//						+ " 进销差价:" + (lsje - hjje).toFixed(2);
			},
			init : function() {
				this.addEvents({
							"select" : true
						})
				if (this.mutiSelect) {
					this.selectFirst = false
				}
				this.selects = {}
				// this.singleSelect = {}
				phis.script.SimpleList.superclass.init.call(this)
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (records.length == 0 || !this.selects || !this.mutiSelect) {
					return
				}
				var selRecords = []
				for (var id in this.selects) {
					var r = store.getById(id)
					if (r) {
						selRecords.push(r)
					}
				}
				this.grid.getSelectionModel().selectRecords(selRecords);
				this.doJshj();
				var count=store.getCount();
				var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryActionId
							});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onStoreLoadData);
					return;
				}
				for(var i=0;i<count;i++){
				store.getAt(i).set("YPKL",ret.json.ypkl);
				store.getAt(i).set("FKJE", (parseFloat(ret.json.ypkl) * parseFloat(store.getAt(i).data["JHHJ"])).toFixed(4));
				}
			},
			getCM : function(items) {
				var cm = phis.application.sto.script.StorehouseFinancialAcceptanceDetailList.superclass.getCM
						.call(this, items)
				sm = this.sm;
				sm.on("rowselect", function(sm, rowIndex, record) {
							this.selects[record.id] = record
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							delete this.selects[record.id]
						}, this)
				return cm;
			},
			clearSelect : function() {
				this.selects = {};
				// this.singleSelect = {};
				this.sm.clearSelections();
			},
			getSelectedRecords : function() {
				var records = []
				var selects=this.getSelectedRecord(true);
				for (var id in selects) {
					//2013-07-17 gejj修改添加|| id=="indexOf",在IE下会有该问题
					if(id=="remove" || id=="indexOf"){continue;}
					records.push(selects[id])
				}
				return records
			}
		})