$package("phis.application.sup.script")

$import("phis.script.SimpleList")

phis.application.sup.script.MeasuringInstrumentsInfoList = function(cfg) {
	phis.application.sup.script.MeasuringInstrumentsInfoList.superclass.constructor
			.apply(this, [cfg])
	this.zfztjlval = 0;
}
Ext.extend(phis.application.sup.script.MeasuringInstrumentsInfoList,
		phis.script.SimpleList, {
			getCndBar : function(items) {
				var filelable = new Ext.form.Label({
							text : "状态:"
						})
				this.zfztRadio = new Ext.form.RadioGroup({
							height : 20,
							width : 100,
							id : 'zfztjl',
							name : 'zfztjl', // 后台返回的JSON格式，直接赋值
							value : "0",
							items : [{
										boxLabel : '在用',
										name : 'zfztjl',
										inputValue : 0
									}, {
										boxLabel : '作废',
										name : 'zfztjl',
										inputValue : 1
									}],
							listeners : {
								change : function(group, newValue, oldValue) {
									this.zfztjlval = newValue.inputValue;
									this.doRefresh();
								},
								scope : this
							}
						});
				return [filelable, this.zfztRadio, '-'];
			},
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.equipmentWeighingManagementService";
				this.requestData.serviceAction = "queryJlxxInfoDetails";
				this.requestData.zfzt = this.zfztjlval;
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
			doRefresh : function() {
				this.loadData();
				if(this.zfztjlval==1){
					this.setButtonsState(['upd','remove'], false);
				}else{
					this.setButtonsState(['upd', 'remove'], true);
				}
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				this.refjlxxinfoform = this.createModule("refjlxxinfoform", this.refjlxxinfoform);
				this.refjlxxinfoform.jlxh = r.get("JLXH")
				this.refjlxxinfoform.oper =this;
				var win = this.refjlxxinfoform.getWin();
				win.add(this.refjlxxinfoform.initPanel());
				win.setHeight(400);
				win.setWidth(800);
				win.show()
				win.center()
				this.refjlxxinfoform.loadData();
			},
			doRemove : function(){
				var r =  this.getSelectedRecord();
				if(r == null){
					MyMessageTip.msg("提示", "请选择要作废的物资！", true);
					return ;
				}
				phis.script.rmi.jsonRequest({
								serviceId :  "equipmentWeighingManagementService",
								serviceAction : "removeEquipment",
								body : r.data.JLXH,
								op : 1
							}, function(code, msg, json) {
								if (code >= 300) {
									if(code==601){
										MyMessageTip.msg("提示", "该记录已被检定,无法作废!", true);
									}else{
									this.processReturnMsg(code, msg);
									}
									return;
								}
								MyMessageTip.msg("提示", "作废成功！", true);
								this.doRefresh();
						}, this);
				
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