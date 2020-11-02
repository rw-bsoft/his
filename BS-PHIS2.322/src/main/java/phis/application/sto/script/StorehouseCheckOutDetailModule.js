/**
 * 药库出库新增修改界面
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailModule");

phis.application.sto.script.StorehouseCheckOutDetailModule = function(cfg) {
	this.width = 1020;
	this.height = 550;
	cfg.modal = this.modal = true;
	phis.application.sto.script.StorehouseCheckOutDetailModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
	this.on("beforeclose", this.doClose, this);
}
Ext.extend(phis.application.sto.script.StorehouseCheckOutDetailModule,
		phis.application.sto.script.StorehouseMySimpleDetailModule, {
			unlock : function() {
				var initData = this.form.initDataBody;
				if (!initData)
					return;
				var p = {};
				p.YWXH = '1022';
				p.SDXH = initData.xtsb + '-' + initData.ckfs + '-'
						+ initData.ckdh;
				this.opener.bclUnlock(p);
			},
			doNew : function() {
				this.changeButtonState("new")
				this.form.op = "create";
				this.panel.items.items[0].setTitle("药品出库单");
				this.form.selectValue = this.selectValue;
				this.form.ksly = this.condition.ksly;
				this.form.yfsb = this.condition.yfsb;
				this.form.doNew();
				this.list.op = "create";
				this.list.remoteDicStore.baseParams = {
					"tag" : "ykck",
					"yfsb" : this.condition.yfsb
				}
				this.list.yfsb = this.condition.yfsb
				// this.list.yfsb = this.yfsb;
				this.list.doNew();
				this.list.doCreate();
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.CKDH);
			},
			// 保存
			doSave : function() {
				var ed = this.list.grid.activeEditor;
				if (!ed) {
					ed = this.list.grid.lastActiveEditor;
				}
				if (ed) {
					ed.completeEdit();
				}
				var body = {};
				var ck01 = this.form.getFormData();
				if (ck01 == null) {
					return;
				}
				this.panel.el.mask("正在保存...", "x-mask-loading");
				if (this.condition.ksly == 1) {
					if (ck01.CKKS == null || ck01.CKKS == ""
							|| ck01.CKKS == undefined) {
						MyMessageTip.msg("提示", "科室领用方式,出库科室不可为空!", true);
						this.panel.el.unmask();
						return;
					}
				}
				if (this.op == "create") {
					ck01["YFSB"] = this.condition.yfsb;
				}
				body["YK_CK01"] = ck01;
				var count = this.list.store.getCount();
				var ck02 = [];
				var _ctr = this;
				var whatsthetime = function() {
					for (var i = 0; i < count; i++) {
						if (_ctr.list.store.getAt(i).data["YPXH"] != ''
								&& _ctr.list.store.getAt(i).data["YPXH"] != null
								&& _ctr.list.store.getAt(i).data["YPXH"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != ''
								&& _ctr.list.store.getAt(i).data["YPCD"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != null
								&& _ctr.list.store.getAt(i).data["SQSL"] != ''
								&& _ctr.list.store.getAt(i).data["SQSL"] != 0
								&& _ctr.list.store.getAt(i).data["SQSL"] != null) {
							if (_ctr.list.store.getAt(i).data.LSJE > 99999999.99) {
								MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行零售金额超过最大值", true);
								_ctr.panel.el.unmask();
								return;
							}
							if (_ctr.list.store.getAt(i).data.JHJE > 99999999.99) {
								MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行进货金额超过最大值", true);
								_ctr.panel.el.unmask();
								return;
							}
							ck02.push(_ctr.list.store.getAt(i).data);
						}
					}
					body["YK_CK02"] = ck02;
					if (body["YK_CK02"].length < 1) {
						MyMessageTip.msg("提示", "无药品明细", true);
						_ctr.panel.el.unmask();
						return;
					}
					body["op"] = _ctr.op;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : _ctr.serviceId,
								serviceAction : _ctr.saveCheckOutActionId,
								body : body
							});
					if (r.code > 300) {
						_ctr.processReturnMsg(r.code, r.msg, _ctr.onBeforeSave);
						_ctr.panel.el.unmask();
						return;
					} else {
						_ctr.list.doInit();
						_ctr.fireEvent("winClose", _ctr);
						_ctr.fireEvent("save", _ctr);
					}
					_ctr.op = "update";
					_ctr.panel.el.unmask();
					MyMessageTip.msg("提示", "保存成功!", true);
					_ctr.list.isEdit = false;
					_ctr.doCancel();
				}
				whatsthetime.defer(500);
				this.list.isEdit = false;
			},
			doLoad : function(initDataBody) {
				this.list.isEdit = false;
				this.form.op = "update";
				initDataBody["ksly"] = this.condition.ksly;
				this.form.ksly = this.condition.ksly;
				this.form.initDataBody = initDataBody;
				this.form.loadData(initDataBody);
				this.list.op = "update";
				this.list.remoteDicStore.baseParams = {
					"tag" : "ykck",
					"yfsb" : this.condition.yfsb
				}
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'a.CKFS'], ['i', initDataBody.ckfs]],
						[
								'and',
								['eq', ['$', 'a.CKDH'],
										['i', initDataBody.ckdh]],
								['eq', ['$', 'a.XTSB'],
										['i', initDataBody.xtsb]]]];
				this.list.yfsb = this.condition.yfsb;
				this.list.loadData();
			}
		})