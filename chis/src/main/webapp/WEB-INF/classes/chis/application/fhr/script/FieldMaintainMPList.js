$package("chis.application.fhr.script")

$import("chis.script.BizSimpleListView", "util.rmi.miniJsonRequestSync")

chis.application.fhr.script.FieldMaintainMPList = function(cfg) {
	this.schema = cfg.entryName;
	chis.application.fhr.script.FieldMaintainMPList.superclass.constructor.apply(this, [cfg]);
	this.removeServiceId = "chis.templateService";
	this.removeAction = "removeFieldRelation";
}
Ext.extend(chis.application.fhr.script.FieldMaintainMPList, chis.script.BizSimpleListView, {
			doAddInfo : function() {
				var m = this.midiModules["fieldSelectModule"];
				if (!m) {
					var cfg = this.loadModuleCfg(this.addModule);
					$import(cfg.script);
					cfg.autoLoadData = false;
					cfg.width = 850;
					cfg.mainApp = this.mainApp;
					m = eval("new " + cfg.script + "(cfg)")
					this.midiModules["fieldSelectModule"] = m;
				}
				m.masterplateId = this.masterplateId;
				m.on("save", this.onSave, this);
				m.on("close", this.active, this);
				m.on("refresh", this.refresh, this);
				m.getWin().show();
			},
			getFieldMaintainForm : function(r) {
				var m = this.midiModules["fieldMaintainForm"];
				if (!m) {
					var cfg = this.loadModuleCfg(this.refModule);
					$import(cfg.script);
					m = eval("new " + cfg.script + "(cfg)");
					m.on("save", this.refresh, this);
					m.on("close", this.active, this);
					this.midiModules["fieldMaintainForm"] = m;
				}
				m.initDataId = r.id;
				return m;
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var m = this.getFieldMaintainForm(r);
				m.op = "update";
				var formData = this.castListDataToForm(r.data, this.schema);
				var win = m.getWin();
				win.show();
				m.initFormData(formData);
			},
			doRemove : function(item, e) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				} else {
					Ext.Msg.show({
								title : '确认删除记录[' + r.id + ']',
								msg : '删除操作将无法恢复，是否继续?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.processRemove();
									}
								},
								scope : this
							})
				}
			},
			doBatchRemove : function() {
				var m = this.midiModules["fieldRemoveList"];
				if (!m) {
					var cfg = this.loadModuleCfg(this.removeModule);
					$import(cfg.script);
					cfg.autoLoadData = false;
					cfg.width = 850;
					cfg.enableCnd = false;
					cfg.mainApp = this.mainApp;
					m = eval("new " + cfg.script + "(cfg)")
					this.midiModules["fieldRemoveList"] = m;
				}
				m.masterplateId = this.masterplateId;
				m.on("remove", this.refresh, this);
				m.getWin().show();
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			getRemoveRequest : function(r) {
				return {
					pkey : r.id,
					masterplateId : this.masterplateId
				};
			},
			initQueryFeild : function() {
				this.cndFldCombox.setValue();
				this.cndField.setValue();
			},
			doCndQuery : function(button, e, addNavCnd) {
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', this.navCnd, initCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						return;
					}
				}
				this.resetFirstPage()
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v === "" || rawV == null || rawV == "") {
					var cnd = []
					this.queryCnd = null;
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', this.navCnd, initCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						if (initCnd)
							cnd = initCnd
					}
					this.requestData.cnd = cnd.length == 0 ? null : cnd;
					this.refresh()
					return
				}
				var refAlias = it.refAlias || "b"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						// var node = this.cndField.selectedNode
						// @@ modified by chinnsii 2010-02-28, add "!node"
						cnd[0] = 'eq'
						// if (!node || !node.isLeaf()) {
						// cnd[0] = 'like'
						// cnd.push(['s', v + '%'])
						// } else {
						cnd.push(['s', v])
						// }
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
							v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
						case 'datetime' :
						case 'timestamp' :
							if (it.xtype == "datefield") {
								v = v.format("Y-m-d")
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd')"]
								cnd.push(['s', v])
							} else {
								v = v.format("Y-m-d H:i:s")
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd HH:mm:ss')"]
								cnd.push(['s', v])
							}

							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', cnd, initCnd]
				}

				if (addNavCnd) {
					cnd = ['and', this.navCnd, cnd];
					if (this.masterplateId) {
						cnd.push(this.masterplateId)
					}
					this.requestData.cnd = cnd;
					this.refresh()
					return
				} else if (this.masterplateId) {
					cnd.push(this.masterplateId)
				}
				this.requestData.cnd = cnd
				this.refresh()
			},
			createNormalField : function(it) {
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					width : 150,
					value : it.defaultValue
				}
				var field;
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield"
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						field = new Ext.form.NumberField(cfg)
						break;
					case 'date' :
						cfg.xtype = 'datefield'
						cfg.emptyText = "请选择日期"
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						field = new Ext.form.DateField(cfg)
						break;
					case 'datetime' :
					case 'timestamp' :
						cfg.emptyText = "请选择日期时间"
						if (it.xtype == "datefield") {
							field = new Ext.form.DateField(cfg)
						} else {
							cfg.xtype = 'datetimefield'
							cfg.format = 'Y-m-d H:i:s'
							field = new util.widgets.DateTimeField(cfg)
						}
						break;
					case 'string' :
						field = new Ext.form.TextField(cfg)
						break;
				}
				return field;
			},
			onSave : function(entryName, op, json, rec) {
				this.refresh();
				this.fireEvent("save", entryName, op, json, rec);
			}

		});