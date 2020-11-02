$package("phis.application.sup.script")
$import("phis.script.SimpleList")

/**
 * 申领管理
 * 
 * @author gaof
 */
phis.application.sup.script.ApplyManagementList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	phis.application.sup.script.ApplyManagementList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sup.script.ApplyManagementList, phis.script.SimpleList, {
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
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
						+ "-01";
				var datelable = new Ext.form.Label({
							text : "记账日期:"
						})
				this.dateFrom = new Ext.form.DateField({
							id : 'ApplyManagementListdateFrom',
							name : 'ApplyManagementListdateFrom',
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
							id : 'ApplyManagementListdateTo',
							name : 'ApplyManagementListdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
				return ["<h1 style='text-align:center'>已记账申领单:</h1>", '-',
						datelable, this.dateFrom, tolable, this.dateTo, '-']
			},
			// 刷新页面
			doRefreshWin : function() {
				var addCndDjzt = [
						'and',
						[
								'and',
								[
										'and',
										['eq', ['$', 'ZBLB'], ['i', this.zblb]],
										['eq', ['$', 'KFXH'],
												['i', this.mainApp['phis'].treasuryId]]],
								['eq', ['$', 'DJZT'], ['i', 2]]],
						['eq', ['$', 'DJLX'], ['i', 6]]];
				var addCndDate = [
						'and',
						['ge', ['$', "str(JZRQ,'yyyy-mm-dd')"],
								['s', this.dateFrom.getValue().format('Y-m-d')]],
						['le', ['$', "str(JZRQ,'yyyy-mm-dd')"],
								['s', this.dateTo.getValue().format('Y-m-d')]]]
				this.requestData.cnd = ['and', addCndDate, addCndDjzt];
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
				this.applyManagementModule = this.opener.createApplyManagementModule();
				//this.createModule("applyManagementModule", this.addRef);
				this.applyManagementModule.on("save", this.onSave, this);
				this.applyManagementModule.on("winClose", this.onClose, this);
				this.applyManagementModule.zblb = this.zblb;
				var win = this.getWin();
				win.add(this.applyManagementModule.initPanel());
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.applyManagementModule.changeButtonState("new");
				} else if (djzt == 1) {
					this.applyManagementModule.changeButtonState("verified");
				} else if (djzt == 2) {
					this.applyManagementModule.changeButtonState("commited");
				}
				win.show()
				win.center()
				if (!win.hidden) {
					this.applyManagementModule.op = "update";
					this.applyManagementModule.initDataBody = initDataBody;
					this.applyManagementModule.loadData(initDataBody);
					this.applyManagementModule.list.grid.getColumnModel()
							.setHidden(
									this.applyManagementModule.list.grid
											.getColumnModel()
											.getIndexById("SLSL"), true);
					this.applyManagementModule.list.grid.getColumnModel()
							.setHidden(
									this.applyManagementModule.list.grid
											.getColumnModel()
											.getIndexById("WFSL"), true);
					this.applyManagementModule.list.grid.getColumnModel()
							.setHidden(
									this.applyManagementModule.list.grid
											.getColumnModel()
											.getIndexById("SLSJ"), true);
					this.applyManagementModule.list.setButtonsState(
							["remove"], false);
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
			onSave : function() {
				this.fireEvent("save", this);
			}
		})