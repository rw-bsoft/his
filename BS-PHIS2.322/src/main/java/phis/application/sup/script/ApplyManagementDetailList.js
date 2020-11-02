$package("phis.application.sup.script")

$import("phis.script.EditorList")
/**
 * 申领管理新增修改界面可编辑列表
 * 
 * @author gaof
 */
phis.application.sup.script.ApplyManagementDetailList = function(cfg) {
	cfg.autoLoadData = false;
    cfg.disablePagingTbr = true;
	phis.application.sup.script.ApplyManagementDetailList.superclass.constructor.apply(
			this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
    this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}
Ext.extend(phis.application.sup.script.ApplyManagementDetailList,
		phis.script.EditorList, {
			loadData : function() {
				this.clear();
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 25;
				this.requestData.serviceId = "phis.applyManagementService";
				this.requestData.serviceAction = "getCK02Info";
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
			// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "WZSL") {
					// 判断实发数量是否大于推荐数量
					if ((Number(record.get("WZSL"))) > (Number(record
							.get("TJSL")))) {
						MyMessageTip.msg("提示", "实发数量不能大于推荐数量", true);
						return;
					}
					if ((Number(record.get("WZSL"))) > (Number(record
							.get("SLSL")))) {
						MyMessageTip.msg("提示", "实发数量不能大于申领数量", true);
						return;
					}

					if (record.get("WZSL") == null || record.get("WZSL") == "") {
						return;
					}
					wfsl = (Number(record.get("SLSL")) - Number(record
							.get("WZSL"))).toFixed(2);
					record.set("WFSL", wfsl);
					if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);
					// this.calculatEmount();
					// alert(record.get("GLFS"))
// if (record.get("GLFS") == 3) {
// this.slsl = record.get("SLSL");
// this.slxh = record.get("SLXH");
// var module = this.createModule("getSUP05010104",
// "SUP05010104");
// module.operater = this;
// this.module = module;
//
// module.WZXH = record.get("WZXH")
// this.rr = this.getSelectedRecord()
// if (this.rr == null) {
// return
// }
// this.win1 = module.getWin();
// this.win1.add(module.initPanel());
// module.loadData();
// this.win1.show();
//
// }
				}

			},
            // 数量操作前
            onBeforeCellEdit : function(it, record, field, v) {
                var cell = this.grid.getSelectionModel().getSelectedCell();
                var row = cell[0];
                if (it.id == "WZSL") {
                    if (record.get("GLFS") == 3) {
                        this.slsl = record.get("SLSL");
                        this.slxh = record.get("SLXH");
                        var module = this.createModule("getSUP05010104",
                                "phis.application.sup.SUP/SUP/SUP05010104");
                        module.operater = this;
                        this.module = module;

                        module.WZXH = record.get("WZXH")
                        this.rr = this.getSelectedRecord()
                        if (this.rr == null) {
                            return
                        }
                        this.win1 = module.getWin();
                        this.win1.add(module.initPanel());
                        module.loadData();
                        this.win1.show();

                    }
                }

            },
			// 选择固定资产后
			doCre : function() {
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				if (this.zczb) {
					if (this.zczb.length > this.slsl) {
						MyMessageTip.msg("提示", "实发数量不能大于申领数量", true);
						return;
					}
					this.store.remove(this.rr);
					var n = store.getCount();
					// if (n > 0) {
					for (var i = 0; i < this.zczb.length; i++) {
						var sign = 0;
						this.zczb[i].SLXH = this.slxh;
						this.zczb[i].SLSL = 1;
						this.zczb[i].WFSL = 0;
						var r = new Record(this.zczb[i]);
						// alert("zczb:"+this.zczb[i].ZBXH);
						for (var j = 0; j < n; j++) {
							var re = store.getAt(j);
							// alert("store:"+re.get("ZBXH"));
							if (re.get("ZBXH") == this.zczb[i].ZBXH) {
								sign = 1
							}
						}
						if (sign == 0) {
							store.add([r])
						}
					}
					if (this.slsl > this.zczb.length) {
						// 增加一行
						var wf = {};
						wf.CJXH = this.zczb[0].CJXH;
						wf.GLFS_text = this.zczb[0].GLFS_text;
						wf.WZJE = this.zczb[0].WZJE;
						wf.WZMC = this.zczb[0].WZMC;
						wf.WZXH = this.zczb[0].WZXH;
						wf.WZDW = this.zczb[0].WZDW;
						wf.CJMC = this.zczb[0].CJMC;
						wf.TJCKSL = this.zczb[0].TJCKSL;
						wf.WZJG = this.zczb[0].WZJG;
						wf.WZGG = this.zczb[0].WZGG;
						wf.GLFS = this.zczb[0].GLFS;

						wf.SLXH = this.slxh;
						wf.SLSL = this.slsl - this.zczb.length;
						wf.WZSL = 0;
						wf.WFSL = this.slsl - this.zczb.length;
						wf.ZBXH = null;
						wf.KCXH = -1;
						store.add([new Record(wf)])
					}
					// }
					// if (n == 0) {
					// for (var i = 0; i < this.zczb.length; i++) {
					// this.zczb[i].SLXH = this.slxh;
					// this.zczb[i].SLSL = 1;
					// this.zczb[i].WFSL = 0;
					// var r = new Record(this.zczb[i]);
					// store.add([r])
					// }
					// }
				}
				this.win1.hide();
			},
			// 设置按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.grid.getTopToolbar();
				if (!btns) {
					return;
				}
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
			}
		})