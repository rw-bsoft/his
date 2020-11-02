$package("chis.application.fhr.script")
$import("util.Accredit", "chis.script.BizSimpleListView")
chis.application.fhr.script.FamilyContractBaseList = function(cfg) {
	cfg.disablePagingTbr = true
	cfg.showButtonOnTop = true;
	cfg.aotuLoadData = false;
	// this.initDataId = cfg.initDataId
	// this.entryName = "chis.application.fhr.schemas.EHR_FamilyContractBase"
	chis.application.fhr.script.FamilyContractBaseList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.fhr.script.FamilyContractBaseList,
		chis.script.BizSimpleListView, {

			getCndBar : function() {
				return [];
			},
			onRowClick : function(grid, rowIndex, e) {
				this.selectedIndex = rowIndex
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store)
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
			},
			doNew : function() {
				this.father.form.op = "create";
				this.father.form.contractId = "";
				this.father.form.doNew()
			},
			setButtonDisable : function(status) {
				var btn = this.toolBar.find("cmd", "remove");
				if (btn && btn[0]) {
					if (status) {
						btn[0].disable();
					} else {
						btn[0].enable()
					}
				}
			},
			setBtnStatus : function(s, iselse) {
				var toolBar = this.grid.getTopToolbar();
				this.toolBar = toolBar;
				if (toolBar) {
					var btn = toolBar.find("cmd", iselse);
					if (!btn || "" == btn)
						return
					if (!btn) {
						s = true;
					}
					if (s) {
						btn[0].enable()
					} else {
						btn[0].disable()
					}
				}

			},
			doRemove : function() {
				var today = new Date();
				if (this.FC_CreateDate.getTime() + 86400000 < today.getTime()) {
					alert("签约超过一天，不能删除该记录")
					return;
				}
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				Ext.Msg.show({
							title : '确认删除此记录',
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
			},
			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.mask("正在删除数据...")
				util.rmi.jsonRequest({
							serviceId : "chis.familyRecordService",
							pkey : r.id,
							familyId : this.exContext.args.initDataId,
							serviceAction : "remove",
							method : "execute"
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
							this.father.loadData();
							this.setBtnStatus(true, "new");
						}, this)
			},

			onStoreLoadData : function(store, records, ops) {
				if (records.length == 0) {
					this.fireEvent("loadNoData");
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				this.fireEvent("loadData", store)
			}

		});