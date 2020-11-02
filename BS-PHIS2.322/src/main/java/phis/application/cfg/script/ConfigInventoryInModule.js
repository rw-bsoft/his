/**
 * 药品入库新增修改界面
 * 
 * @author caijy
 */
$package("phis.application.cfg.script");

$import("phis.script.SimpleModule");

phis.application.cfg.script.ConfigInventoryInModule = function(cfg) {
	cfg.width = 1024;
	cfg.height = 500;
	phis.application.cfg.script.ConfigInventoryInModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.cfg.script.ConfigInventoryInModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '初始账册-库存帐',
										region : 'north',
										height : 73,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
                this.form.operater=this;
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.gridlist = this.list.initPanel();
				return this.gridlist;
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doSave : function() {
				var _ctr = this;
				var whatsthetime = function() {
					var wzmc = _ctr.form.form.getForm().findField("WZMC")
							.getValue();
					if (_ctr.form.wzxh == 0 || wzmc == '') {
						MyMessageTip.msg("提示", "物资名称不能为空!", true);
						return;
					}
					if (_ctr.list.savesign == 1) {
						return;
					}
					var formdate = {
						"WZXH" : _ctr.form.wzxh,
						"CJXH" : _ctr.form.cjxh,
						"KFXH" : _ctr.mainApp.treasuryId,
						"ZBLB" : _ctr.form.zblb
					};
					var store = _ctr.gridlist.getStore();
					var n = store.getCount();
					var cskc = [];
					for (var i = 0; i < n; i++) {
						if (n == 1) {
							if (_ctr.list.store.getAt(i).data["WZJE"] == undefined
									|| _ctr.list.store.getAt(i).data["WZJE"] == 0) {
								MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行物资金额不能为空!", true);
								return;
							}
						}
						if (_ctr.list.store.getAt(i).data["WZSL"] != undefined
								&& _ctr.list.store.getAt(i).data["WZSL"] != 0) {
							if (_ctr.list.store.getAt(i).data["WZJE"] == undefined
									|| _ctr.list.store.getAt(i).data["WZJE"] == 0) {
								MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行物资金额不能为空!", true);
								return;
							}
						}
						if (_ctr.list.store.getAt(i).data["WZJE"] != undefined
								&& _ctr.list.store.getAt(i).data["WZJE"] != 0) {
							cskc.push(_ctr.list.store.getAt(i).data);
						}
					}
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "configInventoryInitialService",
								serviceAction : "saveInventoryIn",
								body : formdate,
								listBody : cskc
							});

					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return;
					} else {
						MyMessageTip.msg("提示", "保存成功!", true);
						_ctr.doCancel();
					}
				}
				whatsthetime.defer(500);
			},
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
				this.opener.refresh();
			},
			doUpdat : function(jlxh, wzxh, cjxh) {
				this.form.doUpdat(jlxh);
				this.list.requestData.cnd = [
						'and',
						[
								'and',
								[
										'and',
										[
												'and',
												['eq', ['$', 'a.WZXH'],
														['i', wzxh]],
												['eq', ['$', 'a.CJXH'],
														['i', cjxh]]],
										['eq', ['$', 'a.JGID'],
												['s', this.mainApp['phisApp'].deptId]]],
								['eq', ['$', 'a.CSLB'], ['i', 1]]],
						['eq', ['$', 'a.KFXH'], ['s', this.mainApp['phis'].treasuryId]]];
				this.list.initCnd = [
						'and',
						[
								'and',
								[
										'and',
										[
												'and',
												['eq', ['$', 'a.WZXH'],
														['i', wzxh]],
												['eq', ['$', 'a.CJXH'],
														['i', cjxh]]],
										['eq', ['$', 'a.JGID'],
												['s', this.mainApp['phisApp'].deptId]]],
								['eq', ['$', 'a.CSLB'], ['i', 1]]],
						['eq', ['$', 'a.KFXH'], ['s', this.mainApp['phis'].treasuryId]]];
				this.list.loadData();
			},
			doNew : function() {
				if (this.form) {
					this.form.doNew();
				}
				if (this.list) {
					this.list.doNew();
//					this.list.doCreate();
				}
			}
		});