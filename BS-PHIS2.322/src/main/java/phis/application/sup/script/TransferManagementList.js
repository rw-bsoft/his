$package("phis.application.sup.script")
$import("phis.script.SimpleList")
/**
 * 转科管理列表
 * 
 * @author gaof
 */
phis.application.sup.script.TransferManagementList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	phis.application.sup.script.TransferManagementList.superclass.constructor
			.apply(this, [ cfg ])
	this.on("beforeclose", this.doCancel, this);
}
Ext
		.extend(
				phis.application.sup.script.TransferManagementList,
				phis.script.SimpleList,
				{
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
							id : 'TransferManagementListdateFrom',
							name : 'TransferManagementListdateFrom',
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
							id : 'TransferManagementListdateTo',
							name : 'TransferManagementListdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
						return [ "<h1 style='text-align:center'>已记账转科单:</h1>",
								'-', datelable, this.dateFrom, tolable,
								this.dateTo, '-' ]
					},
					// 刷新页面
					doRefreshWin : function() {
						var addCndDjzt = [
								'and',
								[ 'eq', [ '$', 'ZBLB' ], [ 'i', this.zblb ] ],
								[
										'and',
										[
												'eq',
												[ '$', 'KFXH' ],
												[
														'i',
														this.mainApp['phis'].treasuryId ] ],
										[ 'eq', [ '$', 'DJZT' ], [ 'i', 2 ] ] ] ];
						var addCndDate = [
								'and',
								[
										'ge',
										[ '$', "str(JZRQ,'yyyy-mm-dd')" ],
										[
												's',
												this.dateFrom.getValue()
														.format('Y-m-d') ] ],
								[
										'le',
										[ '$', "str(JZRQ,'yyyy-mm-dd')" ],
										[
												's',
												this.dateTo.getValue().format(
														'Y-m-d') ] ] ]
						this.requestData.cnd = [ 'and', addCndDate, addCndDjzt ];
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
						this.tansferManagementModule = this.createModule(
								"tansferManagementModule", this.addRef);
						this.tansferManagementModule.zblb = this.zblb;
						this.tansferManagementModule.on("save", this.onSave,
								this);
						this.tansferManagementModule.on("winClose",
								this.onClose, this);
						var win = this.getWin();
						win.add(this.tansferManagementModule.initPanel());
						var djzt = r.data.DJZT;
						if (djzt == 0) {
							this.tansferManagementModule
									.changeButtonState("new");
						} else if (djzt == 1) {
							this.tansferManagementModule
									.changeButtonState("verified");
						} else if (djzt == 2) {
							this.tansferManagementModule
									.changeButtonState("commited");
						}
						win.show()
						win.center()
						if (!win.hidden) {
							this.tansferManagementModule.op = "update";
							this.tansferManagementModule.initDataBody = initDataBody;
							this.tansferManagementModule.loadData(initDataBody);
						}

					},
					onDblClick : function(grid, index, e) {
						var item = {};
						item.text = "查看";
						item.cmd = "look";
						this.doAction(item, e)

					},
					onClose : function() {
						this.getWin().hide();
					},
					doCancel : function() {
						if (this.storageModule) {
							return this.storageModule.doClose();
						}
					},
					onSave : function() {
						this.fireEvent("save", this);
					},
					doPrint : function() {
						var r = this.getSelectedRecord();
						if (r == null) {
							MyMessageTip.msg("提示", "打印失败：无效的转科单信息!", true);
							return;
						}
						var module = this.createModule("storeOfTransferPrint",
								this.refStoreOfTransferPrint)
						module.djxh = r.data.DJXH;
						module.zblb = this.zblb
						module.initPanel();
						module.doPrint();
					}
				})