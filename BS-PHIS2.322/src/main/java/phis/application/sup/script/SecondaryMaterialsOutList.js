/**
 * 物资出库确认（二级）
 * 
 * @author gaof
 */
$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.SecondaryMaterialsOutList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.showBtnOnLevel = true;
	phis.application.sup.script.SecondaryMaterialsOutList.superclass.constructor
			.apply(this, [ cfg ])
}
Ext
		.extend(
				phis.application.sup.script.SecondaryMaterialsOutList,
				phis.script.SimpleList,
				{
					loadData : function() {
						this.clear();
						this.requestData.serviceId = this.serviceId;
						this.requestData.serviceAction = "getCK01Info";
						this.requestData.DJZT = 2;
						this.requestData.JZRQQ = this.dateFrom.getValue()
								.format('Y-m-d');
						this.requestData.JZRQZ = this.dateTo.getValue().format(
								'Y-m-d');

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
						var bar = cfg.tbar;
						cfg.tbar = {
							enableOverflow : true,
							items : bar
						}
					},
					// 生成日期框
					getCndBar : function(items) {
						var dat = new Date().format('Y-m-d');
						var dateFromValue = dat.substring(0, dat
								.lastIndexOf("-"))
								+ "-01";
						var datelable = new Ext.form.Label({
							text : "记账日期:"
						})
						this.dateFrom = new Ext.form.DateField({
							name : 'SecondaryMaterialsOutListdateFrom',
							value : dateFromValue,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间'
						});
						var tolable = new Ext.form.Label({
							text : " 到 "
						});
						this.dateTo = new Ext.form.DateField({
							name : 'SecondaryMaterialsOutListdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
						return [ "<h1 style='text-align:center'>已记账出库单:</h1>",
								'-', datelable, this.dateFrom, tolable,
								this.dateTo, '-' ]
					},
					// 刷新页面
					doRefreshWin : function() {
						// var addCndDjzt = ['and',
						// ['eq', ['$', 'KFXH'], ['i',
						// this.mainApp['phis'].treasuryId]],
						// ['eq', ['$', 'DJZT'], ['i', 2]]];
						// var addCndDate = [
						// 'and',
						// ['ge', ['$', "str(JZRQ,'yyyy-mm-dd')"],
						// ['s', this.dateFrom.getValue().format('Y-m-d')]],
						// ['le', ['$', "str(JZRQ,'yyyy-mm-dd')"],
						// ['s', this.dateTo.getValue().format('Y-m-d')]]]
						// this.requestData.cnd = ['and', addCndDate,
						// addCndDjzt];
						this.requestData.DJZT = 2;
						this.requestData.JZRQQ = this.dateFrom.getValue()
								.format('Y-m-d');
						this.requestData.JZRQZ = this.dateTo.getValue().format(
								'Y-m-d');
						this.refresh();
						return;
					},
					// 查看
					doLook : function() {
						var r = this.getSelectedRecord()
						if (r == null) {
							return;
						}
						var initDataBody = {};
						initDataBody["DJXH"] = r.data.DJXH;
						this.storageOfMaterialsModule = this.oper.createModule(
								"secondaryMaterialsOutDetailModule",
								this.addRef);
						this.storageOfMaterialsModule.on("save", this.onSave,
								this);
						var win = this.storageOfMaterialsModule.getWin();
						win.add(this.storageOfMaterialsModule.initPanel());
						//var djzt = r.data.DJZT;
						var BRLY=r.data.BRLY;
						if(BRLY==1||BRLY==2){
						this.storageOfMaterialsModule
									.changeButtonState("ly");
						}else{
						this.storageOfMaterialsModule
									.changeButtonState("commited");
						}
//						if (djzt == 0) {
//							this.storageOfMaterialsModule
//									.changeButtonState("new");
//						} else if (djzt == 1) {
//							this.storageOfMaterialsModule
//									.changeButtonState("verified");
//						} else if (djzt == 2) {
//							this.storageOfMaterialsModule
//									.changeButtonState("commited");
					//	}
						win.show()
						win.center()
						if (!win.hidden) {
							this.storageOfMaterialsModule.op = "update";
							this.storageOfMaterialsModule.initDataBody = initDataBody;
							this.storageOfMaterialsModule
									.loadData(initDataBody);
						}

					},
					onDblClick : function(grid, index, e) {
						var item = {};
						item.text = "查看";
						item.cmd = "look";
						this.doAction(item, e)

					},
					onSave : function() {
						this.fireEvent("save", this);
					},
					doPrint : function() {
						var module = this.createModule("noStoreListPrint",
								this.refNoStoreListPrint)
						var r = this.getSelectedRecord()
						if (r == null) {
							MyMessageTip.msg("提示", "打印失败：无效的出库单信息!", true);
							return;
						}
						if (r.get("DJZT") == 0) {
							MyMessageTip.msg("提示", "打印失败：没有审核不能打印!", true);
							return;
						}
						module.DJXH = r.data.DJXH;
						module.initPanel();
						module.doPrint();
					}
				})