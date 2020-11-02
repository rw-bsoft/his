$package("phis.application.cfg.script")

$import("phis.script.EditorList", "org.ext.ux.CheckColumn",
		"phis.script.widgets.DatetimeField")

phis.application.cfg.script.CaseHistoryControlRList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.cfg.script.CaseHistoryControlRList.superclass.constructor
			.apply(this, [cfg]);
	this.valueChange = false;
}

Ext.extend(phis.application.cfg.script.CaseHistoryControlRList,
		phis.script.EditorList, {
			expansion : function(cfg) {
				var oldTbar = cfg.tbar;
				this.checkBoxSX = new Ext.form.Checkbox({
							id : "SXQX",
							boxLabel : "书写权限",
							style : 'margin-left:220px;'
						});
				this.checkBoxSX.on("check", this.onCheckAllBox, this);
				this.checkBoxCK = new Ext.form.Checkbox({
							id : "CKQX",
							boxLabel : "查看权限",
							style : 'margin-left:14px;'
						});
				this.checkBoxCK.on("check", this.onCheckAllBox, this);
				this.checkBoxSY = new Ext.form.Checkbox({
							id : "SYQX",
							boxLabel : "审阅权限",
							style : 'margin-left:14px;'
						});
				this.checkBoxSY.on("check", this.onCheckAllBox, this);
				this.checkBoxDY = new Ext.form.Checkbox({
							id : "DYQX",
							boxLabel : "打印权限",
							style : 'margin-left:14px;'
						});
				this.checkBoxDY.on("check", this.onCheckAllBox, this);
				cfg.tbar = oldTbar.concat(this.checkBoxSX)
						.concat(this.checkBoxCK).concat(this.checkBoxSY)
						.concat(this.checkBoxDY)
			},
			onCheckAllBox : function(box, flag) {
				this.store.each(function(r) {
							r.set(box.id, flag)
						}, this);
				this.setHasChange(true);
			},
			doNewCheckBox : function() {
				this.checkBoxSX.setValue(false);
				this.checkBoxCK.setValue(false);
				this.checkBoxSY.setValue(false);
				this.checkBoxDY.setValue(false);
				this.setHasChange(false);
			},
			getTrueOrFalse : function(value) {
				var flag = false;
				if (value == "1") {
					flag = true;
				}
				return flag;
			},
			onStoreLoadData : function(store, records, ops) {
				this.store.each(function(r) {
							r.set("SXQX", this.getTrueOrFalse(r.data.SXQX));
							r.set("CKQX", this.getTrueOrFalse(r.data.CKQX));
							r.set("SYQX", this.getTrueOrFalse(r.data.SYQX));
							r.set("DYQX", this.getTrueOrFalse(r.data.DYQX));
						}, this);
				this.grid.store.commitChanges();
				this.fireEvent("loadData", store)
				if (records.length == 0) {
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
			},
			getCM : function(items) {
				var cm = []
				var fm = Ext.form
				var ac = util.Accredit;
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				if (this.mutiSelect) {
					cm.push(this.sm);
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2) || it.noList
							|| it.hidden || !ac.canRead(it.acValue)) {
						continue
					}
					// if(it.length < 80){it.length = 80}//
					// modify by yangl
					var width = parseInt(it.width
							|| (it.length < 80 ? 80 : it.length) || 80)
					var c = {
						id : it.id,
						header : it.alias,
						width : width,
						sortable : this.sortable,
						dataIndex : it.id,
						schemaItem : it
					}
					/** ******************** */
					if (it.renderer) {
						var func
						func = eval("this." + it.renderer)
						if (typeof func == 'function') {
							c.renderer = func
						}
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
						if (it.summaryRenderer) {
							var func = eval("this." + it.summaryRenderer)
							if (typeof func == 'function') {
								c.summaryRenderer = func
							}
						}
					}
					// add by yangl,modify simple code Generation methods
					if (it.codeType) {
						if (!this.CodeFieldSet)
							this.CodeFieldSet = [];
						this.CodeFieldSet.push([it.target, it.codeType, it.id]);
					}
					var editable = true;

					if ((it.pkey && it.generator == 'auto') || it.fixed) {
						editable = false
					}
					if (it.evalOnServer && ac.canRead(it.acValue)) {
						editable = false
					}
					var notNull = !(it['not-null'] == 'true')

					var editor = null;
					var dic = it.dic
					if (it.properties && it.properties.mode == "remote") {
						// 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
						editor = this.createRemoteDicField(it);
					} else if (dic) {
						dic.src = this.entryName + "." + it.id
						dic.defaultValue = it.defaultValue
						dic.width = width
						if (dic.fields) {
							if (typeof(dic.fields) == 'string') {
								var fieldsArray = dic.fields.split(",")
								dic.fields = fieldsArray;
							}
						}
						if (dic.render == "Radio" || dic.render == "Checkbox") {
							dic.render = ""
						}
						var _ctx = this
						c.isDic = true
						c.renderer = function(v, params, record, r, c, store) {
							var cm = _ctx.grid.getColumnModel()
							var f = cm.getDataIndex(c)
							return record.get(f + "_text")
						}
						if (editable) {
							editor = this.createDicField(dic)
							editor.isDic = true
							var _ctx = this
							c.isDic = true
						}
					} else {
						if (!editable) {
							if (it.type != "string" && it.type != "text"
									&& it.type != "date") {
								c.align = "right";
								c.css = "color:#00AA00;font-weight:bold;"
								c.precision = it.precision;
								c.nullToValue = it.nullToValue;
								if (!c.renderer) {
									c.renderer = function(value, metaData, r,
											row, col, store) {
										if (value == null && this.nullToValue) {
											value = parseFloat(this.nullToValue)
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											try {
												r.set(this.id, retValue);
											} catch (e) {
												// 防止新增行报错
											}
											return retValue;
										}
										if (value != null) {
											value = parseFloat(value);
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											return retValue;
										}
									}
								}
							}
							cm.push(c);
							continue;
						}
						editor = new fm.TextField({
									allowBlank : notNull
								});
						var fm = Ext.form;
						switch (it.type) {
							// modify by liyunt
							case 'string' :
							case 'text' :
								var cfg = {
									allowBlank : notNull,
									maxLength : it.length
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								if (it.inputType) {
									cfg.inputType = it.inputType
								}
								editor = new fm.TextField(cfg)
								break;
							case 'date' :
								var cfg = {
									allowBlank : notNull,
									emptyText : "请选择日期",
									format : 'Y-m-d'
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new fm.DateField(cfg)
								break;
							case 'datetime' :
							case 'timestamp' :
							case 'datetimefield' :
								var cfg = {
									allowBlank : notNull,
									emptyText : "请选择日期"
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new phis.script.widgets.DateTimeField(cfg)
								break;
							case 'double' :
							case 'bigDecimal' :
							case 'int' :
								if (!it.dic) {
									c.css = "color:#00AA00;font-weight:bold;"
									c.align = "right"
									if (it.type == 'double'
											|| it.type == 'bigDecimal') {
										c.precision = it.precision;
										c.nullToValue = it.nullToValue;
										c.renderer = function(value, metaData,
												r, row, col, store) {
											if (value == null
													&& this.nullToValue) {
												value = parseFloat(this.nullToValue)
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												try {
													r.set(this.id, retValue);
												} catch (e) {
													// 防止新增行报错
												}
												return retValue;
											}
											if (value != null) {
												value = parseFloat(value);
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												return retValue;
											}
										}
									}
								}
								var cfg = {}
								if (it.type == 'int') {
									cfg.decimalPrecision = 0;
									cfg.allowDecimals = false
								} else {
									cfg.decimalPrecision = it.precision || 2;
								}
								if (it.min) {
									cfg.minValue = it.min;
								} else {
									cfg.minValue = 0;
								}
								if (it.max) {
									cfg.maxValue = it.max;
								}
								cfg.allowBlank = notNull
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new fm.NumberField(cfg)
								if (it.properties.xtype == "checkBox") {
									c.xtype = 'checkcolumn';
									editor = new Ext.ux.grid.CheckColumn();
								}
								break;
						}
					}
					if (it.id == "LBMC" || it.id == "BLLB") {
						editor = null;
					}
					c.editor = editor;
					cm.push(c);
				}
				return cm;
			},

			afterCellEdit : function() {
				this.setHasChange(true);
			},
			setHasChange : function(flag) {
				this.valueChange = flag;
			},
			getHasChange : function() {
				return this.valueChange
			},

			doSaveContory : function(item, e) {
				Ext.Msg.show({
							title : '确认保存',
							msg : '确认保存所有的角色病历权限记录?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.saveContory(item, e);
								}
							},
							scope : this
						})
			},

			saveContory : function(item, e) {
				var store = this.grid.getStore();
				var data = []
				store.each(function(r) {
							data.push(r.data);
						}, this);
				phis.script.rmi.jsonRequest({
							serviceId : "caseHistoryControlService",
							serviceAction : "saveCaseContory",
							method : "execute",
							op : this.op,
							schema : this.entryName,
							body : data
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveContory, [data]);
								return
							}
							this.setHasChange(false);
							MyMessageTip.msg("提示", "保存成功!", true);
						}, this)
			}
		});