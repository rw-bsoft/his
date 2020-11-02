$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRmodeBlmbMZList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.emr.script.EMRmodeBlmbMZList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.afterLoadData, this);
}

Ext.extend(phis.application.emr.script.EMRmodeBlmbMZList, phis.script.SimpleList, {
			expansion : function(cfg) {
				this.requestData.serviceId = "phis.emrManageService";
				this.requestData.serviceAction = "loadMZBlmb";
				var labelText = new Ext.form.Label({
							text : "科室"
						});
				var depCombo = util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.department_mz",
							filter : "['eq',['$','item.properties.ORGANIZCODE']],['s','"
									+ this.mainApp['phisApp'].deptId + "']]",
							width : 100,
							autoLoad : true,
							editable : false
						});

					var radioGroup = [{
							xtype : "radio",
							boxLabel : '病历模版',
							inputValue : 1,
							name : 'mbfl',
							clearCls : true,
							checked : true,
							listeners : {
								check : this.radioChange,
								scope : this
							}
						}, {
							xtype : "radio",
							boxLabel : '个人模版',
							inputValue : 2,
							name : 'mbfl',
							clearCls : true,
							listeners : {
								check : this.radioChange,
								scope : this
							}
						}];
				this.radioGroup = radioGroup;
				this.depCombo = depCombo;
				this.depCombo.store.on("load", this.depComboLoad, this);
				//radioGroup.on("change", this.radioChange, this);
				depCombo.on("select", this.doRefresh, this);
				// depCombo.on("change", this.combChange, this);
				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push(labelText, '-', depCombo, '-', radioGroup, ['-'],
						tbar);
			},
			depComboLoad : function(store) {
//				var r1 = new Ext.data.Record({
//							key : "0",
//							text : "全院科室"
//						})
//				store.insert(0, [r1]);
				
				this.depCombo.setValue(this.mainApp['phis'].departmentId);
//				this.depCombo.on("select", this.doRefresh, this);
			},
			radioChange : function(radio, checked) {
				if (checked) {
					var sslb = radio.inputValue;
					this.sslb = sslb;
					if (sslb == 1) {
						this.grid.getColumnModel().setHidden(1, false);
						this.grid.getColumnModel().setHidden(2, true);
					} else {
						this.grid.getColumnModel().setHidden(1, true);
						this.grid.getColumnModel().setHidden(2, false);
					}
					this.doRefresh();
				}
			},
//			combChange : function() {
//				if (!this.depCombo.getValue() || this.depCombo.getValue() == "") {
//					this.doRefresh();
//				}
//			},
			afterLoadData : function(store) {
				if (this.node.BLLX == 1) {
					if (store.getCount() == 0) {
						this.opener.form.form.getForm().findField("YL")
								.setValue("")
					}
				}
			},
			selectRow : function(v) {
				if (!this.grid.hidden) {
					this.grid.el.focus()
				}
				try {
					if (this.grid && this.selectFirst) {
						var sm = this.grid.getSelectionModel()
						if (sm.selectRow) {
							sm.selectRow(v)
						}
						if (!this.grid.hidden) {
							var view = this.grid.getView()
							if (this.store.getCount() > 0) {
								view.focusRow(0)
							} else {
								var el = this.grid.el
								setTimeout(function() {
											el.focus()
										}, 300)
							}
						}
						this.fireEvent("changeYl", this)
					}
				} catch (e) {
				}
			},
			onRowClick : function() {
				this.fireEvent("changeYl");
			},
			doRefresh : function() {
				var sslb = this.sslb || 1;
				if (sslb == 1) {
					if (this.depCombo.getValue() && this.depCombo.getValue() != "0") {
						this.requestData.SSKS = this.depCombo.getValue();
					} else {
//						MyMessageTip.msg("提示", "请选择科室后在查询!", true);
						return
					}
				}
				var cnd = [];
				var sslb = this.sslb || 1;
				if (sslb == 1) {
					this.requestData.schema = "phis.application.emr.schemas.V_EMR_BLMB_MZ";
				} else {
					this.requestData.KSDM = this.mainApp['phis'].departmentId
					this.requestData.schema = "phis.application.emr.schemas.V_EMR_BLMB_PRI";
				}
				this.doCndQuery();
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				var sslb = this.sslb || 1;
				if (sslb == 1) {
					if (this.depCombo.getValue() && this.depCombo.getValue() != "0") {
						this.requestData.SSKS = this.depCombo.getValue();
					} else {
	//					delete this.requestData.SSKS;
						MyMessageTip.msg("提示", "请选择科室后在查询!", true);
						return
					}
				}// yzh ,
				// 2010-06-09 **
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
							this.requestData.cnd = ['and', initCnd, this.navCnd];
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
				if (v == null || v == "" || rawV == null || rawV == "") {
					var cnd = []
					this.queryCnd = null;
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
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
				var refAlias = it.refAlias || "a"
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
							// add by liyl 07.25 解决拼音码查询大小写问题
							if (it.id == "PYDM" || it.id == "WBDM") {
								v = v.toUpperCase();
							}
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
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					this.requestData.cnd = ['and', cnd, this.navCnd];
					this.refresh()
					return
				}
				this.requestData.cnd = cnd
				this.refresh()
			}
		})
