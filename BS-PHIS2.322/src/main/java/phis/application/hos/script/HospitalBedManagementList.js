$package("phis.application.hos.script")

//$import("com.bsoft.phis.SimpleList",
//		"app.modules.print.HospitalBedManagementPrintView")

$import("phis.script.SimpleList",'phis.prints.script.HospitalBedManagementPrintView')
phis.application.hos.script.HospitalBedManagementList = function(cfg) {
	phis.application.hos.script.HospitalBedManagementList.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.hos.script.HospitalBedManagementList,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.pageNo = 1;
				this.requestData.serviceId = "phis.hospitalBedInfoService";
				this.requestData.serviceAction = "getBedInfo";
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
			onReady : function() {
				phis.application.hos.script.HospitalBedManagementList.superclass.onReady
						.call(this);
				this.grid.on("keypress", this.onKeypress, this);
				this.grid.on("mouseover", this.onMouseover, this);
			},
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (String(index) == 'false')
					return;
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record.data.ZYH) {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
									qtip : '<div style="font-size: 12;">该床位已有人 </div>'
								}, false);
					}
				}
			},
			doAllocation : function() {
				var r = this.getSelectedRecord();
				var module = this.createModule(
						"hospitalBedspaceAllocationModule",
						this.refHospitalBedspaceAllocationModule)
				module.opener = this;
				this.hospitalbedspaceallocationmodule = module;
				if (r) {
					module.jgid = r.get("JGID");
					module.brch = r.get("BRCH");
					module.cwxb = r.get("CWXB");
					module.brbq = r.get("KSDM");
					module.jcpb = r.get("JCPB");
					module.cwks = r.get("CWKS");
				} else {
					module.jgid = "0";
					module.brch = "0";
					module.cwxb = "";
					module.brbq = "0";
					module.jcpb = "";
					module.cwks = "0";
				}
				var win = module.getWin();
				win.show();
			},
			doTobed : function() {
				var r = this.getSelectedRecord();
				var module = this.createModule("hospitalBedspaceToBedModule",
						this.refHospitalBedspaceToBedModule)
				module.opener = this;
				if (r) {
					if(r.get("CYPB")==1){
						Ext.Msg.alert("提示","当前病人已通知出院!");
						return;
					}
					module.jgid = r.get("JGID");
					module.brch = r.get("BRCH");
					module.cwxb = r.get("CWXB");
					module.brbq = r.get("KSDM");
					module.jcpb = r.get("JCPB");
					module.cwks = r.get("CWKS");
					module.zyh = r.get("ZYH");
					module.brxm = r.get("BRXM");
					module.brxb = r.get("BRXB");
				} else {
					module.jgid = "0";
					module.brch = "0";
					module.cwxb = "";
					module.brbq = "0";
					module.jcpb = "";
					module.cwks = "0";
					module.zyh = "0";
					module.brxm = "";
					module.brxb = -1;
				}
				var win = module.getWin();
				win.show();
			},
			doRetreatbed : function() {
				var r = this.getSelectedRecord();
				var Ll_zyh = r.get("ZYH");
				var Ls_brxm = r.get("BRXM");
				var Ls_cwhm = r.get("BRCH");
				if (Ls_brxm != "" && Ls_cwhm != "") {
					Ext.Msg.confirm("请确认", "确定退掉病人【" + Ls_brxm + "】的床位【"
									+ Ls_cwhm + "】吗?", function(btn) {
								if (btn == 'yes') {
									var data = {
										"Ll_zyh" : Ll_zyh,
										"Ls_cwhm" : Ls_cwhm
									};
									var r = phis.script.rmi.miniJsonRequestSync({
												serviceId : this.serviceId,
												serviceAction : this.serviceAction,
												body : data
											});
									if (r.code > 300) {
										this.processReturnMsg(r.code, r.msg);
										return
									} else {
										MyMessageTip.msg("提示", "退床成功!", true);
										this.loadData();
									}
								}
							}, this);
					return;
				}
			},
			// 单击时改变按钮状态
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				if (r.data.ZYH) {
					this.setButtonsState(['allocation'], false);
					this.setButtonsState(['tobed'], true);
					this.setButtonsState(['retreatbed'], true);
				} else {
					this.setButtonsState(['tobed'], false);
					this.setButtonsState(['allocation'], true);
					this.setButtonsState(['retreatbed'], false);
				}
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
			},
//			 刚打开页面时候默认选中数据,这时候判断下分配和转床状态
			onStoreLoadData : function(store, records, ops) {
				// this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.setButtonsState(['allocation'], false);
					this.setButtonsState(['tobed'], false);
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick();
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick();
				}
			},
			// 上下时改变分配和转床状态
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick();
				}
			},
			onRenderer : function(value, metaData, r) {
				var ZYH = r.get("ZYH");
				if (ZYH) {
					var src = "user";
					return "<img src='resources/phis/resources/images/" + src + ".png'/>";
				}
			},
			doRefresh : function() {
				this.clear();
				this.requestData.pageNo = 1;
				this.requestData.serviceId = "phis.hospitalBedInfoService";
				this.requestData.serviceAction = "getBedInfo";
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
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("BRCH"))
				}
				var pWin = this.midiModules["HospitalBedManagementPrintView"]
				var cfg = {
					requestData : ids
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.HospitalBedManagementPrintView(cfg)
				this.midiModules["HospitalBedManagementPrintView"] = pWin
				pWin.getWin().show()
			}
		})
