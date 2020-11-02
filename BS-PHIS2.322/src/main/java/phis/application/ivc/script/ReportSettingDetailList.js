$package("phis.application.ivc.script")
$import("phis.script.EditorList")

phis.application.ivc.script.ReportSettingDetailList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	this.BBBHValue;
	phis.application.ivc.script.ReportSettingDetailList.superclass.constructor.apply(
			this, [cfg])

}
Ext.extend(phis.application.ivc.script.ReportSettingDetailList,
		phis.script.EditorList, {
			loadData : function(bbbh) {
				this.clear();
				this.requestData.serviceId = "phis.reportSettingService";
				this.requestData.serviceAction = "XMGBQuery";
				this.requestData.cnd = bbbh;
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
			expansion : function(cfg) {
				var label = new Ext.form.Label({
					html : "<div id='totcount' align='center' style='color:black'>明细条数：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			doSave : function(type) {
				var store = this.grid.getStore();
				var n = store.getCount();
				var data = [];
				var bh;
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);

					// 判断归并项目是否能找到对应的收费项目
					var isExist = false;
					for (var j = 0; j < n; j++) {
						var o = store.getAt(j);
						// 如果找到
						if (r.get("GBXM") == o.get("SFXM")) {
							isExist = true;
							break;
						}
					}
					if (!isExist) {
						var line = i + 1;
						Ext.MessageBox.alert("错误", "第" + line
										+ "行归并项目没有对应的收费项目");
						return;
					}

					// 判断顺序号是否重复
					var isDuplicate = false;
					for (var j = 0; j < n; j++) {
						if (i == j) {
							break;
						}
						var o = store.getAt(j);
						// 如果找到
						if (r.get("SXH") == o.get("SXH")) {
							isDuplicate = true;
							break;
						}
					}
					if (isDuplicate) {
						Ext.MessageBox.alert("错误", "顺序号存在重复");
						return;
					}

					if (r.get("GBXM").constructor == String
							&& r.get("GBXM").trim().length == 0) {
						Ext.MessageBox.alert("错误", "归并项目不能为空");
						return;
					}
					if (r.get("SXH").constructor == String
							&& r.get("SXH").trim().length == 0) {
						Ext.MessageBox.alert("错误", "顺序号不能为空");
						return;
					}
					if (r.get("XMMC").trim().length == 0) {
						Ext.MessageBox.alert("错误", "显示名称不能为空");
						return;
					}
					if (r.get("XMMC").length > 10) {
						Ext.MessageBox.alert("错误", "显示名称长度不能超过10个字");
						return;
					}
					bh = r.get("BBBH");
					data.push(r.data);
				}
                
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "reportSettingService",
							serviceAction : "XMGBSave",
							schema : this.entryName,
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if (type == "beforeclose") {
								return;
							}
							this.loadData(bh);
						}, this);
                
				this.store.rejectChanges();

			},
			onStoreLoadData : function(store, records, ops) {
				// this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
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

			}
		})