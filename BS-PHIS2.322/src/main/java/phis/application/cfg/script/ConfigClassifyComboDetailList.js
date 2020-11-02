$package("phis.application.cfg.script")

/**
 * 分类类别维护 gaof 2013.03.18
 */
$import("phis.script.EditorList")

phis.application.cfg.script.ConfigClassifyComboDetailList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	phis.application.cfg.script.ConfigClassifyComboDetailList.superclass.constructor
			.apply(this, [cfg])

}

Ext.extend(phis.application.cfg.script.ConfigClassifyComboDetailList,
		phis.script.EditorList, {
			expansion : function(cfg) {
				var label = new Ext.form.Label({
					html : "<div id='totcount' align='center' style='color:black'>明细条数：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					document.getElementById("totcount").innerHTML = "明细条数：0";
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				store.commitChanges();
				// var store = this.grid.getStore();
				var n = store.getCount();
				document.getElementById("totcount").innerHTML = "明细条数：" + n;

			},
			doInsert : function(item, e, newGroup) {
				// 某一分类下已维护了物资分类，则不能修改分类长度，只能修改分类名称
				var data = {};
				data["LBXH"] = this.LBXH;
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "isClassifyUsed",
							body : data
						}, function(code, msg, json) {
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							}
                            if (json.isStartUsing) {
                                MyMessageTip
                                        .msg("提示", "当前分类已启用，不能修改", true);
                                return;
                            }
							if (0 != json.countKFXX) {
								MyMessageTip
										.msg("提示", "当前分类在库房中已使用，不能修改", true);
								return;
							}
							if (0 != json.countFLZD) {
								MyMessageTip
										.msg("提示", "当前分类已有分类的物资，不能修改", true);
								return;
							}

							// 当前记录后插入一条记录
							// this.removeEmptyRecord();
							var store = this.grid.getStore();
							var storeData = store.data;
							var maxIndex = store.getCount();
							/*
							 * if (maxIndex > 29) {
							 * MyMessageTip.msg("提示,纸张容量限制", '每张项目不能超过30条药品!',
							 * true, 5); return; }
							 */
							this.doCreate();
							var rowItem = storeData.itemAt(maxIndex);
							rowItem.set("LBXH", this.LBXH);
							if (maxIndex == 0) {// 项目的第一条记录
								var GZCC = 1;
								rowItem.set("GZCC", GZCC);
							} else {
								var upRowItem = storeData.itemAt(maxIndex - 1);
								rowItem.set("GZCC", upRowItem.get("GZCC") + 1);
							}
							this.grid.startEditing(maxIndex, 2);
							var n = store.getCount();
							document.getElementById("totcount").innerHTML = "项目条数："
									+ n;

						}, this)

			},
			doSave : function() {

				// 某一分类下已维护了物资分类，则不能修改分类长度，只能修改分类名称
				var data = {};
				data["LBXH"] = this.LBXH;
				// alert(this.LBXH)
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "isClassifyUsed",
							body : data
						}, function(code, msg, json) {
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							}
                            if (json.isStartUsing) {
                                MyMessageTip
                                        .msg("提示", "当前分类已启用，不能修改", true);
                                return;
                            }
							if (0 != json.countKFXX) {
								MyMessageTip
										.msg("提示", "当前分类在库房中已使用，不能修改", true);
								return false;
							}
							if (0 != json.countFLZD) {
								MyMessageTip
										.msg("提示", "当前分类已有分类的物资，不能修改", true);
								return false;
							}
							var detailStore = [];
							var countDetailList = this.store.getCount();
							if (countDetailList == 0) {
								detailStore.push({
											"LBXH" : this.LBXH
										});
							} else {
								for (var i = 0; i < countDetailList; i++) {
									if (this.store.getAt(i).data["GZMC"] == null
											|| this.store.getAt(i).data["GZMC"] == "") {
										Ext.Msg.alert("提示", "规则名称不能为空");
										return false;
									}
                                    if (this.store.getAt(i).data["GZMC"].length>15) {
                                        Ext.Msg.alert("提示", "规则名称长度过长");
                                        return false;
                                    }
                                    if (this.store.getAt(i).data["GZCD"]>99) {
                                        Ext.Msg.alert("提示", "规则长度过长");
                                        return false;
                                    }
									detailStore.push(this.store.getAt(i).data);
								}
							}
							// alert(Ext.encode(detailStore))
							this.grid.el.mask("正在执行操作...");
							phis.script.rmi.jsonRequest({
										serviceId : this.serviceId,
										serviceAction : "saveDetailList",
										body : detailStore
									}, function(code, msg, json) {
										this.grid.el.unmask();
										if (code >= 300) {
											this.processReturnMsg(code, msg);
											return false;
										}
										// Ext.Msg.alert("提示", "保存成功");
										this.refresh();
										return true;
										// this.op = "update";
								}, this);
						}, this)
			},
			doRemove : function() {
				// 某一分类下已维护了物资分类，则不能修改分类长度，只能修改分类名称
				var data = {};
				data["LBXH"] = this.LBXH;
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "isClassifyUsed",
							body : data
						}, function(code, msg, json) {
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							}
                            if (json.isStartUsing) {
                                MyMessageTip
                                        .msg("提示", "当前分类已启用，不能修改", true);
                                return;
                            }
							if (0 != json.countKFXX) {
								MyMessageTip
										.msg("提示", "当前分类在库房中已使用，不能修改", true);
								return;
							}
							if (0 != json.countFLZD) {
								MyMessageTip
										.msg("提示", "当前分类已有分类的物资，不能修改", true);
								return;
							}
							// com.bsoft.phis.cfg.ConfigClassifyComboDetailList.superclass.doRemove
							// .call(this);
							var cm = this.grid.getSelectionModel();
							var cell = cm.getSelectedCell();
							var r = this.getSelectedRecord()
							if (r == null) {
								return
							}
							if (r.get("GZXH") == null || r.get("GZXH") == ""
									|| r.get("GZXH") == 0) {
								this.store.remove(r);
								// 移除之后焦点定位
								var count = this.store.getCount();
								if (count > 0) {
									cm.select(cell[0] < count
													? cell[0]
													: (count - 1), cell[1]);
								}
								return;
							}
							Ext.Msg.show({
								title : "确认删除分类规则【" + r.data.GZMC + "】",
								msg : '删除操作将无法恢复，是否继续?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.store.remove(r);
										// 移除之后焦点定位
										var count = this.store.getCount();
										if (count > 0) {
											cm.select(cell[0] < count
															? cell[0]
															: (count - 1),
													cell[1]);
										}
										phis.script.rmi.jsonRequest({
													serviceId : this.serviceId,
													serviceAction : "deleteFLGZ",
													body : r.get("GZXH")
												}, function(code, msg, json) {
													if (code >= 300) {
														MyMessageTip.msg("提示",
																msg, true);
													}
												}, this)

									}
								},
								scope : this
							})
						}, this)

			}
		})