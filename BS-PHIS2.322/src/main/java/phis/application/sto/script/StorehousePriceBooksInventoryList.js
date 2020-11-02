$package("phis.application.sto.script")

$import("phis.script.SimpleList","phis.application.sto.script.StorehousePriceBooksInventoryPrintView",
		"phis.application.mds.script.MySimplePagingToolbar")

phis.application.sto.script.StorehousePriceBooksInventoryList = function(cfg) {
	phis.application.sto.script.StorehousePriceBooksInventoryList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehousePriceBooksInventoryList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.pageNo = 1;
				this.requestData.serviceId = this.fullserviceId;
				this.requestData.serviceAction = this.serviceActionId;
				this.requestData.body = {};
				phis.application.sto.script.StorehousePriceBooksInventoryList.superclass.loadData
						.call(this);
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					document.getElementById("ZCKC_JE").innerHTML = "当页进货金额合计:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;当页零售金额合计:￥0.00";
					this.fireEvent("noRecord", this);
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick(this.grid);
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick(this.grid);
				}
				var store = this.grid.getStore();
				var JHHJ = 0;
				var LSJE = 0;
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (r.get("JHJE")) {
						JHHJ += r.get("JHJE");
					}
					if (r.get("LSJE")) {
						LSJE += r.get("LSJE");
					}
				}
				document.getElementById("ZCKC_JE").innerHTML = "当页进货金额合计:￥"
						+ JHHJ.toFixed(4)
						+ "&nbsp;&nbsp;&nbsp;&nbsp;当页零售金额合计:￥"
						+ LSJE.toFixed(4);
			},
			onStoreBeforeLoad : function(store, op) {
				var showZero = this.grid.getTopToolbar().get("showZero")
						.getValue();
				this.requestData.body.showZero = showZero;
				var r = this.getSelectedRecord()
				var n = this.store.indexOf(r)
				if (n > -1) {
					this.selectedIndex = n
				}
			},
			expansion : function(cfg) {
				this.checkBox = new Ext.form.Checkbox({
							id : "showZero",
							boxLabel : "显示零库存",
							height:24
						});
				this.checkBox.on("check", this.loadData, this);
				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push(this.checkBox, ['-'], tbar);
				// var label = new Ext.form.Label({
				// html : "<div id='totcount' align='center'
				// style='color:blue'>药品条数：</div>"
				// })
				// cfg.bbar = [];
				// cfg.bbar.push(label);
				// checkBox.disable();
				// 统计信息
				// var summary = new Ext.ux.grid.GridSummary();
				// cfg.plugins = [summary];
			},
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : this.pageSize || 25, // ** modify by yzh ,
					// 2010-06-18 **
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录",
					divHtml:"<div id='ZCKC_JE' align='center' style='color:blue'>当页进货金额合计:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;当页零售金额合计:￥0.00</div>"
				}
				if (this.showButtonOnPT) {
					cfg.items = this.createButtons();
				}
				var pagingToolbar = new phis.application.mds.script.MySimplePagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar
				this.pagingToolbar.on("beforePageChange",
						this.beforeStorechange);
				return pagingToolbar
			},
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("YPXH"))
				}
				var showZero = this.grid.getTopToolbar().get("showZero")
						.getValue();
				var pWin = this.midiModules["StorehousePriceBooksInventoryPrintView"]
				var cfg = {
					requestData : ids,
					showZero : showZero
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.application.sto.script.StorehousePriceBooksInventoryPrintView(cfg)
				this.midiModules["StorehousePriceBooksInventoryPrintView"] = pWin
				pWin.getWin().show()
			},
	onCndFieldSelect : function(item, record, e) {
		var tbar = this.grid.getTopToolbar()
		var tbarItems = tbar.items.items
		var itid = record.data.value
		var items = this.schema.items
		var it
		for (var i = 0; i < items.length; i++) {
			if (items[i].id == itid) {
				it = items[i]
				break
			}
		}
		var field = this.cndField
		// field.destroy()
		field.hide();
		var f = this.midiComponents[it.id]
		if (!f) {
			if (it.dic) {
				it.dic.src = this.entryName + "." + it.id
				it.dic.defaultValue = it.defaultValue
				it.dic.width = 150
				f = this.createDicField(it.dic)
			} else {
				f = this.createNormalField(it)
			}
			f.on("specialkey", this.onQueryFieldEnter, this)
			this.midiComponents[it.id] = f
		} else {
			f.on("specialkey", this.onQueryFieldEnter, this)
			// f.rendered = false
			f.show();
		}
		this.cndField = f
		tbarItems[4] = f
		tbar.doLayout()
	}
		})