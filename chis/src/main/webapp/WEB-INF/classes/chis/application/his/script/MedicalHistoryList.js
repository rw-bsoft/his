$package("chis.application.his.script")

$import("chis.script.BizSelectListView")

chis.application.his.script.MedicalHistoryList = function(cfg) {
	cfg.enableRowBody = true;
	cfg.headerGroup = true;
	cfg.modal = true;
	cfg.mutiSelect = true;
	cfg.showRowNumber = true;
	this.serverParams = {
		serviceAction : cfg.serviceAction
	}
	chis.application.his.script.MedicalHistoryList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(chis.application.his.script.MedicalHistoryList,
		chis.script.BizSelectListView, {
			onStoreLoadData : function(store, records, ops) {
				chis.application.his.script.MedicalHistoryList.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.selectAllRecords();
			},
			selectAllRecords : function() {
				this.clearSelect();
				// Ext.fly(this.grid.getView().innerHd).addClass('x-grid3-hd-checker-on');
				this.sm.selectAllInAll();
				this.fireEvent("changeAll");
			},
			clearSelect : function() {
				this.selects = {};
				this.singleSelect = {};
				this.sm.clearSelectionsInAll();
				Ext.fly(this.grid.getView().innerHd)
						.child('.x-grid3-hd-checker')
						.removeClass('x-grid3-hd-checker-on');
			},
			getCM : function(items) {
				var cm = []
				var ac = util.Accredit;
				var expands = []
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				var sm = new Ext.grid.CheckboxSelectionModel({
					checkOnly : this.checkOnly,
					singleSelect : !this.mutiSelect,
					onHdMouseDown : function(e, t) {
						if (t.className == 'x-grid3-hd-checker') {
							e.stopEvent();
							var hd = Ext.fly(t.parentNode);
							var isChecked = hd
									.hasClass('x-grid3-hd-checker-on');
							if (isChecked) {
								hd.removeClass('x-grid3-hd-checker-on');
								this.clearSelectionsInAll();
							} else {
								hd.addClass('x-grid3-hd-checker-on');
								this.selectAllInAll();
							}
							this.fireEvent("changeAll");
						}
					},
					clearSelectionsInAll : function(fast) {
						if (this.isLocked()) {
							return;
						}
						if (fast !== true) {
							var ds = this.grid.store, s = this.selections;
							s.each(function(r) {
										this.deselectRowInAll(ds
												.indexOfId(r.id));
									}, this);
							s.clear();

						} else {
							this.selections.clear();
						}
						this.last = false;
					},
					selectAllInAll : function() {
						if (this.isLocked()) {
							return;
						}
						this.selections.clear();
						for (var i = 0, len = this.grid.store.getCount(); i < len; i++) {
							this.selectRowInAll(i, true);
						}
					},
					selectRowInAll : function(index, keepExisting,
							preventViewNotify) {
						if (this.isLocked()
								|| (index < 0 || index >= this.grid.store
										.getCount())
								|| (keepExisting && this.isSelected(index))) {
							return;
						}
						var r = this.grid.store.getAt(index);
						if (r
								&& this.fireEvent('beforerowselect', this,
										index, keepExisting, r) !== false) {
							if (!keepExisting || this.singleSelect) {
								this.clearSelections();
							}
							this.selections.add(r);
							this.last = this.lastActive = index;
							if (!preventViewNotify) {
								this.grid.getView().onRowSelect(index);
							}
							if (!this.silent) {
								this.fireEvent('rowselect', this, index, r,
										true);
							}
						}
					},
					deselectRowInAll : function(index, preventViewNotify) {
						if (this.isLocked()) {
							return;
						}
						if (this.last == index) {
							this.last = false;
						}
						if (this.lastActive == index) {
							this.lastActive = false;
						}
						var r = this.grid.store.getAt(index);
						if (r) {
							this.selections.remove(r);
							if (!preventViewNotify) {
								this.grid.getView().onRowDeselect(index);
							}
							this.fireEvent('rowdeselect', this, index, r, true);
						}
					}
				});
				this.sm = sm
				sm.on("rowselect", function(sm, rowIndex, record, flag) {
							if (this.mutiSelect) {
								this.selects[record.id] = record
							} else {
								this.singleSelect = record
							}
							if (!flag) {
								this.fireEvent("rowSelect");
							}
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record, flag) {
							if (this.mutiSelect) {
								delete this.selects[record.id]
							} else {
								this.singleSelect = {}
							}
							if (!flag) {
								this.fireEvent("rowSelect");
							}
						}, this);
				sm.on("changeAll", function() {
							this.fireEvent("changeAll");
						}, this);
				if (this.sm) {
					cm.push(this.sm)
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2 || it.hidden == true)
							|| !ac.canRead(it.acValue)) {
						continue
					}
					if (it.expand) {
						var expand = {
							id : it.dic ? it.id + "_text" : it.id,
							alias : it.alias,
							xtype : it.xtype
						}
						expands.push(expand)
						continue
					}
					if (!this.fireEvent("onGetCM", it)) { // **
						// fire一个事件，在此处可以进行其他判断，比如跳过某个字段
						continue;
					}
					var width = parseInt(it.width || 80)
					// if(width < 80){width = 80}
					var c = {
						header : it.alias,
						width : width,
						sortable : true,
						dataIndex : it.dic ? it.id + "_text" : it.id
					}
					if (!this.isCompositeKey && it.pkey) {
						c.id = it.id
					}
					switch (it.type) {
						case 'int' :
						case 'double' :
						case 'bigDecimal' :
							if (!it.dic) {
								c.css = "color:#00AA00;font-weight:bold;"
								c.align = "right"
								if (it.precision > 0) {
									var nf = '0.';
									for (var j = 0; j < it.precision; j++) {
										nf += '0';
									}
									c.renderer = Ext.util.Format
											.numberRenderer(nf);
								}
							}
							break
						case 'date' :
							c.renderer = function(v) {
								if (v && typeof v == 'string' && v.length > 10) {
									return v.substring(0, 10);
								}
								return v;
							}
							break
						case 'timestamp' :
						case 'datetime' :
							if (it.xtype == 'datefield') {
								c.renderer = function(v) {
									if (v && typeof v == 'string'
											&& v.length > 10) {
										return v.substring(0, 10);
									} else {
										return v;
									}
								}
							}
							break
					}
					if (it.renderer) {
						var func
						func = eval("this." + it.renderer)
						if (typeof func == 'function') {
							c.renderer = func
						}
					}
					if (this.fireEvent("addfield", c, it)) {
						cm.push(c)
					}
				}
				if (expands.length > 0) {
					this.rowExpander = this.getExpander(expands)
					cm = [this.rowExpander].concat(cm)
				}

				return cm;
			},
			loadData : function() {
				this.requestData.serviceAction = this.serviceAction;
				this.initCnd = [
						'and',
						['eq', ['$', 'a.JGID'], ['$', this.mainApp.deptId]],
						['eq', ['$', 'b.EMPIID'],
								['s', this.exContext.ids.empiId]]];
				this.requestData.cnd = [
						'and',
						['eq', ['$', 'a.JGID'], ['$', this.mainApp.deptId]],
						['eq', ['$', 'b.EMPIID'],
								['s', this.exContext.ids.empiId]]];
				chis.application.his.script.MedicalHistoryList.superclass.loadData
						.call(this)
			},
			onDblClick : function() {

			},

			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				// yzh ,
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				// var it = this.schema.items[index]
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}

				if (!it) {
					return;
				}
				this.resetFirstPage()
				var f = this.cndField;
				var v = f.getValue()

				if (v == null || v == "") {
					this.queryCnd = null;
					this.requestData.cnd = initCnd
					this.refresh()
					return
				}
				if (f.getXType() == "datefield") {
					v = v.format("Y-m-d")
				}
				if (f.getXType() == "datetimefield") {
					v = v.format("Y-m-d H:i:s")
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						var node = this.cndField.selectedNode
						if (!node.isLeaf()) {
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
						} else {
							cnd.push(['s', v])
						}
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							if (v.format) {
								v = v.format("Y-m-d")
							}
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
						case 'datetime' :
						case 'timestamp' :
							if (v.format) {
								v = v.format("Y-m-d H:i:s")
							}
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				// alert(cnd)
				cnd[2][1] = ["$", "d.cardNo"];
				this.requestData.cnd = cnd
				this.refresh()
			}
		});