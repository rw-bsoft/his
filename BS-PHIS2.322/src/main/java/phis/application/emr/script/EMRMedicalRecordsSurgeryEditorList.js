$package("phis.application.emr.script")

$import("phis.script.EditorList")

phis.application.emr.script.EMRMedicalRecordsSurgeryEditorList = function(cfg) {
	cfg.disablePagingTbr = true;// 不分页
	// cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.remoteUrl = 'Surgery';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{#}.</td><td width="80px">{SSDM}</td><td width="200px">{SSMC}</td>';
	phis.application.emr.script.EMRMedicalRecordsSurgeryEditorList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRMedicalRecordsSurgeryEditorList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.emr.script.EMRMedicalRecordsSurgeryEditorList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				sm.onEditorKey = function(field, e) {
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (k == e.ENTER) {
						e.stopEvent();
						if (!ed) {
							ed = g.lastActiveEditor;
						}
						ed.completeEdit();
						if (e.shiftKey) {
							newCell = g.walkCells(ed.row, ed.col - 1, -1,
									sm.acceptsNav, sm);
						} else {
							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
						}

					} else if (k == e.TAB) {
						e.stopEvent();
						ed.completeEdit();
						if (e.shiftKey) {
							newCell = g.walkCells(ed.row, ed.col - 1, -1,
									sm.acceptsNav, sm);
						} else {
							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
						}
					} else if (k == e.ESC) {
						ed.cancelEdit();
					}
					if (newCell) {
						r = newCell[0];
						c = newCell[1];
						this.select(r, c);
						if (g.isEditor && !g.editing) {
							ae = g.activeEditor;
							if (ae && ae.field.triggerBlur) {
								ae.field.triggerBlur();
							}
							g.startEditing(r, c);
						}
					}

				};
				return grid
			},
			beforeCellEdit : function(e) {
				if (!this.exContext.SXQX) {
					return false;
				}
				var f = e.field
				var record = e.record
				var op = record.get("_opStatus")
				var cm = this.grid.getColumnModel()
				var c = cm.config[e.column]
				var enditor = cm.getCellEditor(e.column)
				var it = c.schemaItem
				var ac = util.Accredit;
				if (op == "create") {
					if (!ac.canCreate(it.acValue)) {
						return false
					}
				} else {
					if (!ac.canUpdate(it.acValue)) {
						return false
					}
				}
				// add by yangl 回写未修改下的中文名称
				e.value = e.value || ""
				return this.fireEvent("beforeCellEdit", it, record,
						enditor.field, e.value)
			},
			onReady : function() {
				this.requestData.serviceId = "phis.emrMedicalRecordsService";
				this.requestData.serviceAction = "zYSSJLLoad";
				this.requestData.cnd = ['eq', ['$', 'JZXH'],
						['i', this.exContext.empiData.ZYH]];
				this.requestData.schema = this.getMySchema(this.entryName).items;
				phis.application.emr.script.EMRMedicalRecordsSurgeryEditorList.superclass.onReady
						.call(this);
			},
			getMySchema : function(entryName) {
				var schema = "";
				if (this.opener.schema) {
					schema = this.opener.schema[entryName]
				}
				if (!schema) {
					var re = util.schema.loadSync(entryName)
					if (re.code == 200) {
						schema = re.schema;
						if (this.opener.schema) {
							this.opener.schema[entryName] = schema;
						} else {
							this.opener.schema = {}
							this.opener.schema[entryName] = schema;
						}
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				return schema;
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'ss',
							totalProperty : 'count',
							id : 'sssearch_a'
						}, [{
									name : 'SSNM'
								}, {
									name : 'SSDM'
								}, {
									name : 'SSMC'

								}]);
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				if (!obj.getValue())
					return
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var rowItem = griddata.itemAt(row);
				obj.collapse();
				rowItem.set('SSDM', record.get("SSDM"));
				rowItem.set('SSMC', record.get("SSMC"));
				rowItem.set('SSXH', record.get("SSNM"));
				rowItem.modified['SSMC'] = record.get("SSMC");
				obj.setValue(record.get("SSMC"));
				obj.triggerBlur();
				this.grid.startEditing(row, 5);
			},
			doInsertAfter : function(item, e, newGroup) {// 当前记录后插入一条记录
				var r = this.getSelectedRecord()
				var n = this.store.indexOf(r)
				if (n >= 7) {
					return;
				}
				if (r.get("SSDM") == null || r.get("SSDM") == ""
						|| r.get("SSDM") == 0) {
					return;
				}
				if (!r.data.SSBZ) {
					MyMessageTip.msg("提示", '手术操作标志不能为空!', true);
					return;
				}
				if (!r.data.SSJB) {
					MyMessageTip.msg("提示,", '手术级别不能为空!', true);
					return;
				}

				if (!r.data.SSYS) {
					MyMessageTip.msg("提示,", '手术者签名不能为空!', true);
					return;
				}
				if (!r.data.YZYS) {
					MyMessageTip.msg("提示,", 'I助签名不能为空!', true);
					return;
				}
				if (!r.data.EZYS) {
					MyMessageTip.msg("提示,", 'II助签名不能为空!', true);
					return;
				}
				if (!r.data.QKLB) {
					MyMessageTip.msg("提示,", '切口类别不能为空!', true);
					return;
				}
				if (!r.data.YHDJ) {
					MyMessageTip.msg("提示,", '愈合等级不能为空!', true);
					return;
				}
				if (!r.data.MZFS) {
					MyMessageTip.msg("提示,", '麻醉方式不能为空!', true);
					return;
				}
				if (!r.data.MZYS) {
					MyMessageTip.msg("提示,", '麻醉医师签名不能为空!', true);
					return;
				}
				if (!r.data.ASA && r.data.ASA != 0) {
					MyMessageTip.msg("提示,", 'ASA分级不能为空!', true);
					return;
				}
				if (!r.data.SSQCSJ && r.data.SSQCSJ != 0) {
					MyMessageTip.msg("提示,", '手术全程时间不能为空!', true);
					return;
				}
				this.grid.startEditing(n + 1, 4);
			},
			afterCellEdit : function(e) {
				var f = e.field
				var v = e.value
				var record = e.record
				var cm = this.grid.getColumnModel()
				var enditor = cm.getCellEditor(e.column, e.row)
				var c = cm.config[e.column]
				var it = c.schemaItem
				var field = enditor.field
				if (it.dic) {
					record.set(f + "_text", field.getRawValue())
				}
				if (it.type == "date") {
					if(v){
						var dt = new Date(v)
						v = dt.format('Y-m-d')
						record.set(f, v)
					}else{
						var dt = new Date()
						v = dt.format('Y-m-d')
						record.set(f, v)
					}
				}
				if (it.codeType)
					record.set(f, v.toUpperCase())
				if (this.CodeFieldSet) {
					var bField = {};
					for (var i = 0; i < this.CodeFieldSet.length; i++) {
						var CodeField = this.CodeFieldSet[i];
						var target = CodeField[0];
						var codeType = CodeField[1];
						var srcField = CodeField[2];
						if (it.id == target) {
							if (!bField.codeType)
								bField.codeType = [];
							bField.codeType.push(codeType);
							if (!bField.srcField)
								bField.srcField = [];
							bField.srcField.push(srcField);
						}
					}
					if (bField.srcField) {
						var body = {};
						body.codeType = bField.codeType;
						body.value = v;
						var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : "codeGeneratorService",
									serviceAction : "getCode",
									body : body
								});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.saveToServer, [saveData]);
							return;
						}
						for (var i = 0; i < bField.codeType.length; i++) {
							record
									.set(bField.srcField[i],
											eval('ret.json.body.'
													+ bField.codeType[i]));
						}
					}
				}
				// add by yangl 回写未修改下的中文名称
				// if (field.isSearchField) {
				// var patrn = /^[a-zA-Z0-9]+$/;
				// if ((v.trim() == "" || patrn.exec(v))
				// && this.beforeSearchQuery()) {
				// record.set(f, record.modified[f]);
				// } else {
				// record.modified[f] = v;
				// }
				// }
				if (field.isSearchField) {// 远程查询字段不允许修改
					if (!field.bySelect) {
						if (record.get(f)) {
							record.set(f, record.modified[f]);
						} else {
							record.set("SSDM", "");
							record.set("SSXH", "");
						}
					}
					field.bySelect = false;
				}
				this.fireEvent("afterCellEdit", it, record, field, v)
			},
        getListData : function(module) {
            if (!module) {
                return;
            }
            var store = module.grid.getStore();
            var n = store.getCount()
            var data = []
            for (var i = 0; i < n; i++) {
                var r = store.getAt(i)
                if (r.data.SSXH && r.data.SSDM && r.data.SSMC) {
                    if (!r.data.SSBZ) {
                        this.canSave = true;
                        MyMessageTip.msg("提示", '【手术操作标志】不能为空!', true);
                        return [];
                    }
                    if (!r.data.SSJB) {
                        this.canSave = true;
                        MyMessageTip.msg("提示,", '【手术级别】不能为空!', true);
                        return [];
                    }

                    if (!r.data.SSYS) {
                        this.canSave = true;
                        MyMessageTip.msg("提示,", '【手术者签名】不能为空!', true);
                        return [];
                    }
                    if (!r.data.YZYS && r.data.SSJB !=1) {
                        this.canSave = true;
                        MyMessageTip.msg("提示,", '【I助签名】不能为空!', true);
                        return [];
                    }
                    if (!r.data.EZYS&& r.data.SSJB !=1) {
                        this.canSave = true;
                        MyMessageTip.msg("提示,", '【II助签名】不能为空!', true);
                        return [];
                    }
                    if (!r.data.QKLB && r.data.SSJB!= 1) {
                        this.canSave = true;
                        MyMessageTip.msg("提示,", '【切口类别】不能为空!', true);
                        return [];
                    }
                    if (!r.data.YHDJ && r.data.SSJB!= 1) {
                        this.canSave = true;
                        MyMessageTip.msg("提示,", '【愈合等级】不能为空!', true);
                        return [];
                    }
                    if (!r.data.MZFS && r.data.SSJB!= 1) {
                        this.canSave = true;
                        MyMessageTip.msg("提示,", '【麻醉方式】不能为空!', true);
                        return [];
                    }
                    if (!r.data.MZYS) {
                        this.canSave = true;
                        MyMessageTip.msg("提示,", '【麻醉医师签名】不能为空!', true);
                        return [];
                    }
                    if (!r.data.ASA && r.data.ASA != 0 && r.data.SSJB!= 1) {
                        this.canSave = true;
                        MyMessageTip.msg("提示,", '【ASA分级】不能为空!', true);
                        return [];
                    }
                    if (!r.data.SSQCSJ && r.data.SSQCSJ != 0 && r.data.SSJB!= 1) {
                        this.canSave = true;
                        MyMessageTip.msg("提示,", '【手术全程时间】不能为空!', true);
                        return [];
                    }
                    data.push(r.data)
                }
            }
            return data
        },
        doRemove : function(){
            var cm = this.grid.getSelectionModel();
            var cell = cm.getSelectedCell();
            var r = this.getSelectedRecord();
            if (r == null) {
                return
            }
            if (r.get("JLXH") == null || r.get("JLXH") == ""
                || r.get("JLXH") == 0) {
                this.store.remove(r);
                // 移除之后焦点定位
                if (cell[0] == this.store.getCount()) {
                    this.doInsertAfter();
                } else {
                    var count = this.store.getCount();
                    if (count > 0) {
                        cm.select(cell[0] < count ? cell[0] : (count - 1),
                            cell[1]);
                    }
                }
                return;
            }
            Ext.Msg.show({
                title : '确认删除记录[' + r.get("SSMC") + ']',
                msg : '删除操作将无法恢复，是否继续?',
                modal : true,
                width : 300,
                buttons : Ext.MessageBox.OKCANCEL,
                multiline : false,
                fn : function(btn, text) {
                    if (btn == "ok") {
                        this.grid.el.mask("正在删除数据...", "x-mask-loading")
                        phis.script.rmi.jsonRequest({
                            serviceId : "emrMedicalRecordsService",
                            serviceAction : "deleteOperationRecord",
                            schema : this.entryName,
                            pkey : r.get("JLXH")
                        }, function(code, msg, json) {
                            this.grid.el.unmask()
                            if (code >= 300) {
                                this.processReturnMsg(code, msg);
                                return;
                            }
                            r.set("SSDM", "");
                            r.set("SSMC", "");
                            r.set("SSJB", "");
                            r.set("SSJB_text", "");
                            r.set("SSYS", "");
                            r.set("SSYS_text", "");
                            r.set("YZYS", "");
                            r.set("YZYS_text", "");
                            r.set("EZYS_text", "");
                            r.set("EZYS", "");
                            r.set("QKLB_text", "");
                            r.set("QKLB", "");
                            r.set("YHDJ_text", "");
                            r.set("YHDJ", "");
                            r.set("MZFS_text", "");
                            r.set("MZFS", "");
                            r.set("MZYS", "");
                            r.set("MZYS_text", "");
                            r.set("ASA", "");
                            r.set("ASA_text", "");
                            r.set("SSQCSJ", "");
                            r.set("ZQSS", "");
                            r.set("ZQSS_text", "");
//										this.store.remove(r);
                            var count = this.store.getCount();// 移除之后焦点定位
                            if (count > 0) {
                                cm.select(cell[0] < count
                                        ? cell[0]
                                        : (count - 1),
                                    cell[1]);
                            }
                            this.grid.getView().refresh();
                            this.grid.startEditing(0, 1);
                        }, this)
                    }
                },
                scope : this
            })
        }
		})