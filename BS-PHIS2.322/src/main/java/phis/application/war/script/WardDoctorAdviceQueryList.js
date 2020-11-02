$package("phis.application.war.script")

$import("phis.script.SimpleList","org.ext.ux.CheckColumn")

phis.application.war.script.WardDoctorAdviceQueryList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.listServiceId = "wardAdviceQuery";
	cfg.selectOnFocus = true;
	cfg.disablePagingTbr = true;
	cfg.selectFirst = false;
	this.serviceId = "wardPatientManageService";
	phis.application.war.script.WardDoctorAdviceQueryList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.myLoadData, this);
}
Ext.extend(phis.application.war.script.WardDoctorAdviceQueryList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				var grid = phis.application.war.script.WardDoctorAdviceQueryList.superclass.initPanel
						.call(this, sc)
				var fjxxPanel = new Ext.Panel({
							title : "附加项目",
							layout : "fit",
							border : false,
							region : 'south',
							height : 200,
							items : this.getAppendList()
						});
				var panel = new Ext.Panel({
							layout : 'border',
							tbar : this.createButtons(),
							items : [fjxxPanel, {
										layout : "fit",
										border : false,
										region : 'center',
										items : grid
									}]
						});
				if (!this.exContext.systemParams) {
					this.exContext = this.opener.opener.exContext;
				}
				if ((this.opener.opener.openBy == "doctor" && this.exContext.systemParams.XSFJJJ_YS == 1)
						|| (this.opener.opener.openBy == "nurse" && this.exContext.systemParams.XSFJJJ_HS == 1)) {// 显示附加项目
					fjxxPanel.show();
				} else {
					fjxxPanel.hide();
				}
				return panel
			},
			getAppendList : function() {
				this.exList = this.createModule("appendList",
						this.refAppendAdviceList);
				this.exList.mainList = this;
				return this.exList.initPanel();
			},
			getRowClass : function(record, rowIndex, rowParams, store) {
				if (record.get("LSBZ") == 1) {
					return "x-grid-record-gray";
				}
				return ""
			},
			onRowClick : function(grid, index, e) {
				if (!this.exList)
					return;
				var r = this.getSelectedRecord();
				var records = [];
				var cacheStore = this.exList.totalStore;
				this.exList.store.removeAll();
				// if (r && r.get("YPLX") > 0) {
				for (var i = 0; i < cacheStore.getCount(); i++) {
					if (r.get("YZZH") > 0) {
						if (cacheStore.getAt(i).get("YZZH") == r.get("YZZH")
								&& cacheStore.getAt(i).get("_opStatus") != "remove") {
							records.push(cacheStore.getAt(i));
						}
					} else {
						if (r.get("uniqueId")
								&& cacheStore.getAt(i).get("uniqueId") == r
										.get("uniqueId")) {
							records.push(cacheStore.getAt(i));
						}
					}
				}
				// }
				this.exList.store.add(records);
			},
			dateFormat : function(value, params, r, row, col, store) {
				if (row > 0 && col < 5
						&& store.getAt(row - 1).get("YZZH") == r.get("YZZH")) {
					return "";
				}
				if (value) {
					return Ext.util.Format.date(Date.parseDate(value,
									"Y-m-d H:i:s"), 'Y.m.d H:i')
				}
				return "";
			},
			YstjRender : function(v, params, r) {
				// params.style = "text-align:left;font-weight:bold;";
				if (r.get("YSBZ") == 1) {
					if (v === 0) {
						return "<font color='#1E90FF'>新开</font>"
					} else if (v === 1 && r.get("FHBZ") != 1) {
						return "<font color='#32CD32'>提交</font>"
					} else if (v === 1 && r.get("FHBZ") == 1) {
						return "<font color='#FA8072'>复核</font>"
					}
				} else {
					if (r.get("FHBZ") != 1) {
						return "<font color='#1E90FF'>新开</font>"
					} else if (r.get("FHBZ") == 1) {
						return "<font color='#FA8072'>复核</font>"
					}
				}
			},
			ysbzRender : function(v, params, r) {
				params.style = "text-align:left;font-weight:bold;";
				if (v == 1) {
					return "<font color='red'>医</font>"
				}
				return "";
			},
			showColor : function(v, params, data) {
				var YZZH = data.get("YZZH_SHOW") % 2 + 1;
				switch (YZZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				return "";
			},
			srcsRender : function(value, metaData, r, row, col, store) {
				if (value == 0 || value == "") {
					return "";
				}
				return value;
			},
			myLoadData : function(store) {
				var count = this.store.getCount();
				var yzzh = 0;
				var lastYZZH = -1;
				for (var i = 0; i < count; i++) {
					var now_yzzh = this.store.getAt(i).get("YZZH");
					if (now_yzzh != lastYZZH) {
						yzzh++;
						this.store.getAt(i).set("YZZH_SHOW", yzzh);
						lastYZZH = now_yzzh;
					} else {
						this.store.getAt(i).set("YZZH_SHOW", yzzh);
					}
				}
				this.store.each(function(r) {
							r.set("ZFYP", r.data.ZFYP == 1 ? true : false);
						}, this);
				this.store.commitChanges();
			},
			getCM : function(items) {
				var cm = []
				var ac = util.Accredit;
				var expands = []
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				if (this.mutiSelect) {
					cm.push(this.sm);
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2)
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
					if (!this.isCompositeKey && it.pkey == "true") {
						c.id = it.id
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
					}
					switch (it.type) {
						case 'int' :
						case 'double' :
						case 'bigDecimal' :
							if (!it.dic) {
								c.css = "color:#00AA00;font-weight:bold;"
								c.align = "right"
								if (it.type == 'double'
										|| it.type == 'bigDecimal') {
									c.precision = it.precision;
									c.nullToValue = it.nullToValue;
									c.id = it.id;
									c.renderer = function(value, metaData, r,
											row, col, store) {
										if (r.get("JFBZ") == 3) {
											return "";
										}
										if (this.id == "YCJL") {
											if (value == 0 || value == "") {
												return "";
											}
										}
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
							break
						case 'timestamp' :
							// c.renderer = Ext.util.Format.dateRenderer('Y-m-d
							// HH:m:s')
					}
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
					if (it.xtype == "checkBox") {
									c.xtype = 'checkcolumn';
									editor = new Ext.ux.grid.CheckColumn();
									// editor.on("beforeedit",this.beforeCheckEdit,this)
								}
					if (this.fireEvent("addfield", c, it)) {
						cm.push(c)
					}
				}
				if (expands.length > 0) {
					this.rowExpander = this.getExpander(expands)
					cm = [this.rowExpander].concat(cm)
					this.array.push(this.rowExpander)// add by taoy
				}
				return cm
			},
			beforeCheckEdit : function(record) {
				return false;
			},
			onReady : function() {
				phis.application.war.script.WardDoctorAdviceQueryList.superclass.onReady
						.call(this);
				this.grid.on("beforeCheckedit", this.beforeCheckEdit, this)
			}
		});
