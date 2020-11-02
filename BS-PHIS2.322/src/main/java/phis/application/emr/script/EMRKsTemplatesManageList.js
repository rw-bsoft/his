$package("phis.application.emr.script")

$import("phis.script.SelectList")

phis.application.emr.script.EMRKsTemplatesManageList = function(cfg) {
	phis.application.emr.script.EMRKsTemplatesManageList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRKsTemplatesManageList,
		phis.script.SelectList, {
			initPanel : function(sc) {
				var grid = phis.application.emr.script.EMRKsTemplatesManageList.superclass.initPanel
						.call(this, sc)
				var tbar = this.grid.getTopToolbar();
				var tbarItems = tbar.items.items[2];
				tbarItems.on("select", function() {
							this.doCndQuery();
						}, this);
				return grid
			},
			loadData : function() {
				this.clear();
				recordIds = [];
				this.requestData.serviceId = "phis.emrManageService";
				this.requestData.serviceAction = "loadTemplateList";
				this.requestData.type = "1";
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
				this.refresh();
			},
			doSave : function() {
				var s = this.opener.rlist.getSelectedRecord();
				var selt = this.grid.getSelectionModel();
				var rstore = this.grid.getStore();
				var seltArr = [];
				var rstoreArr = [];
				var n = 0;
				var chtcode, tablename;
				var row = 0;
				if (typeof(s) == 'undefined') {
					MyMessageTip.msg("提示", "请选择一个科室!", true);
					return;
				}
				selt.each(function(a, b) {
							if (a.data.ISHDRFTRTEMP == '1') {
								n++;
								chtcode = a.data.CHTCODE;
								tablename = a.data.TABLENAME;
							}
							seltArr.push(a.data);
						})
				rstore.each(function(a, b) {
							if (chtcode == a.data.CHTCODE
									&& tablename == a.data.TABLENAME) {
								row = b + 1;
							}
							rstoreArr.push(a.data);
						})
				if (n > 0) {
					MyMessageTip.msg("提示", "第" + row + "行为页眉页脚,不能订阅!", true);
					return;
				}
				var retr = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.emrManageService",
							serviceAction : "saveKsTemplateDy",
							body : rstoreArr,
							select : seltArr,
							ks : s.data
						});
				if (retr.code > 300) {
					this.processReturnMsg(retr.code, retr.msg);
					return;
				} else {
					MyMessageTip.msg("提示", "保存成功!", true);
				}
			},
			doReview : function() {
				this.opener.doReview();
			},
			doCndQuery : function() {
				phis.application.emr.script.EMRKsTemplatesManageList.superclass.doCndQuery
						.call(this)
				this.opener.rlist.selectedIndex = -1;
				this.opener.rlist.loadData();
			}

		})
