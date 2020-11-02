$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigProjectsCallinList = function(cfg) {
	cfg.serviceId = "configDeptCostService";
	cfg.listServiceId = "costCallList";
	cfg.actionId = "saveCallin";
	cfg.modal = true;
	this.closeAction = true;
	cfg.width = 800;
	cfg.height = 500;
	this.showRowNumber = true;
	cfg.mutiSelect = true;
	cfg.selectFirst = false;// 取消默认选中
	phis.application.cfg.script.ConfigProjectsCallinList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
	this.sm = new Ext.grid.CheckboxSelectionModel()

}
var recordIds = new Array();
Ext.extend(phis.application.cfg.script.ConfigProjectsCallinList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				cfg.sm.handleMouseDown = Ext.emptyFn// 只允许点击check列选中
				cfg.sm.on("rowselect", this.rowSelect, this);
				cfg.sm.on("rowdeselect", this.rowdeSelect, this);
				cfg.listeners = {
					"beforeclose" : this.beforeclose,
					scope : this
				}
			},
			onStoreLoadData : function(store, records, ops) {
				var rows = [];
				_ctx = this;
				store.each(function(record, index) {
							if (_ctx.containsArray(recordIds, record
											.get("FYXH"))) {
								rows.push(index);
							}
						});
				this.grid.selModel.selectRows(rows, true);
			},
			rowSelect : function(e, rowIndex, record) {
				if (!this.containsArray(recordIds, record.get("FYXH")))
					recordIds.push(record.get("FYXH"));
			},
			rowdeSelect : function(e, rowIndex, record) {
				if (this.containsArray(recordIds, record.get("FYXH"))) {
					this.RemoveArray(recordIds, record.get("FYXH"));
				}
			},

			onWinShow : function() {
				if (this.grid) {
					this.cndField.setValue("");
				}
			},

			beforeclose : function() {
				if (recordIds.length > 0) {
					if (confirm("当前存在已勾选未调入的修改数据，是否调入？")) {
						this.doCallIn();
					}
					recordIds = [];// 清空recordIds
				}
			},
			RemoveArray : function(array, attachId) {
				for (var i = 0, n = 0; i < array.length; i++) {
					if (array[i] != attachId) {
						array[n++] = array[i]
					}
				}
				array.length -= 1;
			},
			containsArray : function(array, attachId) {
				for (var i = 0; i < array.length; i++) {
					if (array[i] == attachId) {
						return true;
						break;
					}
				}
				return false;
			},
			openModule : function(cmd, r, xy) {

				phis.application.cfg.script.ConfigCostEditorList.superclass.openModule
						.call(this, cmd, r, xy)
				var module = this.midiModules[cmd]
				var win = module.getWin();
				var default_xy = win.el.getAlignToXY(win.container, 'c-c');
				win.setPagePosition(default_xy[0], 100);

			},
			loadData : function() {

				this.clear(); // ** add by yzh , 2010-06-09 **
				var body = {
					"FYGB" : this.initDataId
				};
				this.requestData.serviceId = "phis." + this.serviceId;
				this.requestData.serviceAction = this.listServiceId;
				this.requestData.body = body;
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
				// ** add by yzh **
				this.resetButtons();
			},
			// 调入
			doCallIn : function() {
				// alert(recordIds)
				/*
				 * return if (this.saving) return; var rs =
				 * this.grid.getSelectionModel().getSelections(); var daras =
				 * []; for (var i = 0; i < rs.length; i++) { var r = rs[i]; var
				 * data = {}; data["FYXH"] = r.get("FYXH"); data["FYDJ"] = "0";
				 * data["ZFPB"] = "0"; data["DZBL"] = "0"; data["_opStatus"] =
				 * "create"; daras.push(r.get("FYXH")); }
				 */
				this.doSave(recordIds);
			},
			doSave : function(daras) {
				this.saving = true
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.actionId,
							method : "execute",
							body : daras
						}, function(code, msg, json) {
							this.grid.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							if (json.body) {
								this.initFormData(json.body);
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							recordIds = [];
							this.loadData();
						}, this)// jsonRequest
			},
			getCM : function(items) {
				var cm = []
				var ac = util.Accredit;
				var expands = []
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				if(this.sm){
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
				return cm
			},
			doExit : function() {
				this.beforeclose();
				this.win.hide();
			}
		})