$package("phis.application.sto.script")

$import("phis.script.EditorList")

phis.application.sto.script.StorehouseInitialBooksList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.sto.script.StorehouseInitialBooksList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseInitialBooksList,
		phis.script.EditorList, {
			// // 新增行
			doCreate : function(item, e) {
				this.fireEvent("create", this);
				var swkc = this.getKcls();
				if (swkc == this.createData.KCSL) {
					MyMessageTip.msg("提示", "实物库存等于账册库存,不能增加明细", true);
					return;
				}
				if (swkc > this.createData.KCSL) {
					MyMessageTip.msg("提示", "实物库存大于账册库存,不能增加明细", true);
					return;
				}
				// 相同价格,相同批号,相同效期判别
				var count = this.store.getCount();
				for (var i = 0; i < count; i++) {
					var ypxh = this.store.getAt(i).data.YPXQ;
					var ypph = this.store.getAt(i).data.YPPH;
					if (ypxh) {
						ypxh = ypxh.trim();
					}
					if (ypph) {
						ypph = ypph.trim();
					}
					if ((ypxh == null || ypxh == undefined || ypxh == "")
							&& (ypph == null || ypph == undefined || ypph == "")
							&& this.store.getAt(i).data.TYPE == 1) {
						MyMessageTip.msg("提示", "已有相同库存性质,批号,效期的药!", true);
						return;
					}
				}
				this.createData["KCSL"] = this.createData.KCSL - swkc;
				this.createData["LSJE"] = Math.round(this.createData["LSJG"]
						* this.createData["KCSL"] * 100)
						/ 100
				this.createData["JHJE"] = Math.round(this.createData["JHJG"]
						* this.createData["KCSL"] * 100)
						/ 100
				this.createData["TYPE_text"] = "合格";
				this.createData["TYPE"] = "1";
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				var items = this.schema.items
				var factory = util.dictionary.DictionaryLoader
				var r = new Record(this.createData);
				store.add([r])
				this.grid.getView().refresh();
				this.fireEvent("jsjg", this.getJes());
				this.doJshjje();
			},
			// 删行
			doRemove : function() {
				phis.application.sto.script.StorehouseInitialBooksList.superclass.doRemove
						.call(this);
				this.fireEvent("jsjg", this.getJes());
				this.doJshjje();
			},
			// 获取总的kcsl
			getKcls : function() {
				var kcsl = 0;
				var count = this.store.getCount();
				for (var i = 0; i < count; i++) {
					kcsl += parseFloat(this.store.getAt(i).data["KCSL"]);
				}
				return kcsl;
			},
			// 数量和价格编辑完后
			afterCellEdit : function(e) {
				var f = e.field
				var originalValue = e.originalValue;
				var value = e.value;
				var record = e.record;
				var cm = this.grid.getColumnModel();
				var enditor = cm.getCellEditor(e.column, e.row);
				var c = cm.config[e.column];
				var it = c.schemaItem;
				var field = enditor.field
				var row = e.row;
				if (it.dic) {
					record.set(f + "_text", field.getRawValue())
				}
				if (it.type == "date") {
					var dt = new Date(value)
					value = dt.format('Y-m-d')
					record.set(f, value)
				}
				if (it.id == "KCSL") {
					if (!this.topKcsl) {
						this.topKcsl = this.fireEvent("getTopKcsl", this);
					}
					var kcsl = this.getKcls();

					if (this.topKcsl < kcsl) {
						MyMessageTip.msg("提示", "实物库存数与账册库存数不匹配!", true);
						// record.set("KCSL", originalValue);
						// return;
					}
					this.doJs(record);
					this.fireEvent("jsjg", this.getJes());

				} else if (it.id == "LSJG" || it.id == "JHJG") {
					if (value == originalValue) {
						return;
					}
					this.fireEvent("jgjs", value, it, record, originalValue)
				}
				this.doJshjje();
			},
			doJs : function(record) {
				record.set("JHJE", Math.round(record.get("JHJG")
								* record.get("KCSL") * 100)
								/ 100);
				record.set("LSJE", Math.round(record.get("LSJG")
								* record.get("KCSL") * 100)
								/ 100);
			},
			// 获取金额
			getJes : function() {
				var body = {};
				var lsje = 0;
				var jhje = 0;
				var count = this.store.getCount();
				for (var i = 0; i < count; i++) {
					lsje += parseFloat(this.store.getAt(i).data["LSJE"])
					jhje += parseFloat(this.store.getAt(i).data["JHJE"])
				}
				body["lsje"] = lsje;
				body["jhje"] = jhje;
				return body;
			},
			// 清空
			doNew : function() {
				this.clear()
				document.getElementById("CSZC").innerHTML = "合计   库存数量:0   零售金额:0  进货金额:0";
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='CSZC' align='center' style='color:blue'>合计   库存数量:0   零售金额:0  进货金额:0</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
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
				var store = this.grid.getStore();
				var n = store.getCount()
				var kcsl = 0;
				var lsje = 0;
				var jhje = 0;
				this.CFSBS = new Array();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					kcsl += parseFloat(r.get("KCSL"));
					lsje += parseFloat(r.get("LSJE"));
					jhje += parseFloat(r.get("JHJE"));
				}
				document.getElementById("CSZC").innerHTML = "合计   库存数量:" + kcsl
						+ "  零售金额:" + lsje + "  进货金额:" + jhje;
			},
			// 增删行重新计算合计金额
			doJshjje : function() {
				var jes = this.getJes();
				var kcsl = this.getKcls();
				document.getElementById("CSZC").innerHTML = "合计   库存数量:"
						+ Math.round(kcsl * 100) / 100 + "  零售金额:"
						+ Math.round(jes.lsje * 100) / 100 + "  进货金额:"
						+ Math.round(jes.jhje * 100) / 100;
			}
		})