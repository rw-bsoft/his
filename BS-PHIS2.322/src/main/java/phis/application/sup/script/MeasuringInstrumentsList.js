	$package("phis.application.sup.script")

$import("phis.script.SimpleList")

phis.application.sup.script.MeasuringInstrumentsList = function(cfg) {
	phis.application.sup.script.MeasuringInstrumentsList.superclass.constructor.apply(
			this, [cfg])
	this.sfdjztval = 0;
}
Ext.extend(phis.application.sup.script.MeasuringInstrumentsList,
		phis.script.SimpleList, {
			getCndBar : function(items) {
				var filelable = new Ext.form.Label({
							text : "状态:"
						})
				this.djztRadio = new Ext.form.RadioGroup({
							height : 20,
							width : 200,
							id : 'djztjl',
							name : 'djztjl', // 后台返回的JSON格式，直接赋值
							value : "0",
							items : [{
										boxLabel : '未登记信息',
										name : 'djztjl',
										inputValue : 0
									}, {
										boxLabel : '已登记信息',
										name : 'djztjl',
										inputValue : 1
									}],
							listeners : {
								change : function(group, newValue, oldValue) {
									this.sfdjztval = newValue.inputValue;
									this.doRefresh();
								},
								scope : this
							}
						});
				return [filelable, this.djztRadio, '-'];
			},
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.equipmentWeighingManagementService";
				this.requestData.serviceAction = "queryJlxxListDetails";
				this.requestData.sfdj = this.sfdjztval;
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
				if(this.sfdjztval==1){
					this.setButtonsState(['add'], false);
				}else{
					this.setButtonsState(['add'], true);
				}
			},
			doRefresh : function() {
				this.loadData();
			},
			doAdd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "请先选择一条数据数据！", true);
					return
				}
				this.refjlxxform = this.createModule("refjlxxform",
				this.refjlxxform);
				this.refjlxxform.djxx = r
				this.refjlxxform.oper = this;
				var win = this.refjlxxform.getWin();
				win.add(this.refjlxxform.initPanel());
				win.setHeight(400);
				win.setWidth(800);
				win.show()
				win.center()
				this.refjlxxform.loadData();
			},
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.grid.getTopToolbar();
				} else {
					btns = this.grid.buttons;
				}

				if (!btns) {
					return;
				}
				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			}
		})