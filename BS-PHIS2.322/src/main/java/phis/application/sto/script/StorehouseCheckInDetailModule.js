/**
 * 采购入库详细module
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailModule");

phis.application.sto.script.StorehouseCheckInDetailModule = function(cfg) {
	cfg.title = "药品入库单";// module的标题
	phis.application.sto.script.StorehouseCheckInDetailModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseCheckInDetailModule,
		phis.application.sto.script.StorehouseMySimpleDetailModule, {
			unlock : function() {
				var initData = this.form.initDataBody;
				if (!initData)
					return;
				var p = {};
				p.YWXH = '1023';
				p.SDXH = initData.XTSB + '-' + initData.RKFS + '-'
						+ initData.RKDH;
						if(this.opener&&this.opener.bclUnlock){
						this.opener.bclUnlock(p);
						}
			},
			doSave : function() {
				var ed = this.list.grid.activeEditor;
				if (!ed) {
					ed = this.list.grid.lastActiveEditor;
				}
				if (ed) {
					ed.completeEdit();
				}
				var body = {};
				body["YK_RK02"] = [];
				body["YK_RK01"] = this.form.getFormData();
				if (!body["YK_RK01"]) {
					return;
				}
				body["YK_RK01"].XTSB = this.mainApp['phis'].storehouseId;
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var count = this.list.store.getCount();
				var sfkz = this.getSfkz();
				var _ctr = this;
				var whatsthetime = function() {
					for (var i = 0; i < count; i++) {
						if (_ctr.list.store.getAt(i).data["YPXH"] == null
								|| _ctr.list.store.getAt(i).data["YPXH"] == ""
								|| _ctr.list.store.getAt(i).data["YPXH"] == undefined) {
							continue;
						}
						if ((_ctr.list.store.getAt(i).data["YPXH"] != ''
								&& _ctr.list.store.getAt(i).data["YPXH"] != null
								&& _ctr.list.store.getAt(i).data["YPXH"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != ''
								&& _ctr.list.store.getAt(i).data["YPCD"] != 0 && _ctr.list.store
								.getAt(i).data["YPCD"] != null)
								&& (_ctr.list.store.getAt(i).data["RKSL"] == ''
										|| _ctr.list.store.getAt(i).data["RKSL"] == 0 || _ctr.list.store
										.getAt(i).data["RKSL"] == null)) {
							MyMessageTip.msg("提示", "第" + (i + 1) + "行数量为0",
									true);
							_ctr.panel.el.unmask();
							return;
						}
						if (_ctr.list.store.getAt(i).data["YPXH"] != ''
								&& _ctr.list.store.getAt(i).data["YPXH"] != null
								&& _ctr.list.store.getAt(i).data["YPXH"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != ''
								&& _ctr.list.store.getAt(i).data["YPCD"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != null
								&& _ctr.list.store.getAt(i).data["RKSL"] != ''
								&& _ctr.list.store.getAt(i).data["RKSL"] != 0
								&& _ctr.list.store.getAt(i).data["RKSL"] != null) {
							if (_ctr.list.store.getAt(i).data.LSJE > 99999999.99) {
								MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行零售金额超过最大值", true);
								_ctr.panel.el.unmask();
								return;
							}
							if (_ctr.list.store.getAt(i).data.JHHJ > 99999999.99) {
								MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行进货金额超过最大值", true);
								_ctr.panel.el.unmask();
								return;
							}
							if (_ctr.list.store.getAt(i).data.JHJG == ""
									|| _ctr.list.store.getAt(i).data.JHJG == undefined
									|| _ctr.list.store.getAt(i).data.JHJG == 0) {
								MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行进货价格不能为空或0!", true);
								_ctr.panel.el.unmask();
								return;
							}
							if (_ctr.list.store.getAt(i).data["FPHM"] != undefined
									&& _ctr.list.store.getAt(i).data["FPHM"].length > 10) {
								MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行发票号码过长", true);
								_ctr.panel.el.unmask();
								return;
							}
							if (_ctr.list.store.getAt(i).data["SHHM"] != undefined
									&& _ctr.list.store.getAt(i).data["SHHM"].length > 12) {
								MyMessageTip.msg("提示",
										"第" + (i + 1) + "行随货号过长", true);
								_ctr.panel.el.unmask();
								return;
							}
							// 如果中心控制价格,判断下限制价格有无被修改
							if (sfkz > 0) {
								var body_jgkz = {};
								body_jgkz["YPXH"] = _ctr.list.store.getAt(i).data["YPXH"];
								body_jgkz["YPCD"] = _ctr.list.store.getAt(i).data["YPCD"];
								body_jgkz["ROW"] = i + 1;
								body_jgkz["TAG"] = "rk";
								var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : _ctr.queryPriceChangesServiceId,
									serviceAction : _ctr.queryPriceChangesServiceAction,
									body : body_jgkz
								});
								if (ret.code > 300) {
									_ctr.processReturnMsg(ret.code, ret.msg,
											_ctr.doSave);
									_ctr.panel.el.unmask();
									return;
								}
							}
							// 增加效期判断
							var ypxq = _ctr.list.store.getAt(i).data["YPXQ"];
							if (!Ext.isEmpty(ypxq)) {
								var today = new Date().format('Ymd');
								//这样转成数字比较是因为ie8不支持date的gettime
								var d=(ypxq.substring(0,10)).split('-');
								var date = d[0]+d[1]+d[2];
								if (today > date) {
									MyMessageTip
											.msg(
													"提示",
													"第"
															+ (i + 1)
															+ "药品已过期，请确认效期是否填写正确",
													true);
									_ctr.panel.el.unmask();
									return;
								}
							}
							_ctr.list.store.getAt(i).data.XTSB = _ctr.mainApp.storehouseId;
							body["YK_RK02"].push(_ctr.list.store.getAt(i).data);
						}
					}
					if (body["YK_RK02"].length < 1) {
						_ctr.panel.el.unmask();
						MyMessageTip.msg("提示", "无药品明细", true);
						return;
					}
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : _ctr.serviceId,
								serviceAction : _ctr.saveCheckInActionId,
								body : body,
								op : _ctr.op
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
				}
				whatsthetime.defer(500);
			},
			doCommit : function() {
				// 判断是否票已到但货物未到
				var formData = this.form.getFormData();
				if (formData.PWD == 2) {
					MyMessageTip.msg("提示", "本入库单发票已到但货物未到，不能进行实物验收!", true);
					return;
				}
				// 提交前判断是否中心控制价格和有没超过限制价格
				var sfkz = this.getSfkz();
				if (sfkz > 0) {
					var count = this.list.store.getCount();
					for (var i = 0; i < count; i++) {
						var body_jgkz = {};
						body_jgkz["YPXH"] = this.list.store.getAt(i).data["YPXH"];
						body_jgkz["YPCD"] = this.list.store.getAt(i).data["YPCD"];
						body_jgkz["ROW"] = i + 1;
						body_jgkz["TAG"] = "rk";
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.queryPriceChangesServiceId,
							serviceAction : this.queryPriceChangesServiceAction,
							body : body_jgkz
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.doSave);
							return;
						}
					}
				}
				var body = {};
				body["RKFS"] = this.initDataBody.RKFS;
				body["RKDH"] = this.initDataBody.RKDH;
				body["XTSB"] = this.initDataBody.XTSB;
				body["YK_RK02"] = [];
				body["tag"] = "cgrk";
				var count = this.list.store.getCount();
				for (var i = 0; i < count; i++) {
					var mxdata = this.list.store.getAt(i).data;
					if (mxdata.CPSL > 0) {
						if (mxdata.TYPE == 0 || mxdata.TYPE == undefined
								|| mxdata.TYPE == "" || mxdata.TYPE == null) {
							MyMessageTip.msg("提示", "第" + (i + 1)
											+ "行,有次品 请选择次品类型", true);
							return;
						}
					}
					body["YK_RK02"].push(mxdata);
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveCheckInToInventoryActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doCommit);
					return;
				}
				MyMessageTip.msg("提示", "入库单提交成功", true);
				this.list.doInit();
				this.setButtonsState(["commit"], false);
				// this.fireEvent("winClose", this);
				this.fireEvent("commit", this);
				this.getWin().hide();
			},
			// 修改,查看,提交数据回填
			doLoad : function(initDataBody) {
				this.form.op = "update";
				this.form.initDataBody = initDataBody;
				this.form.loadData();
				this.list.op = "create";
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'RKFS'], ['i', initDataBody.RKFS]],
						[
								'and',
								['eq', ['$', 'RKDH'], ['i', initDataBody.RKDH]],
								['eq', ['$', 'XTSB'], ['l', initDataBody.XTSB]]]];
				this.list.loadData();
			},
			// 获取是否需要控制价格
			getSfkz : function() {
				if (this.sfkz == undefined) {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryControlPricesServiceAction
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.getSfkz);
						return null;
					}
					this.sfkz = ret.json.sfkz;
				}
				return this.sfkz;
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.RKDH);
			},
			doNew : function() {
				this.changeButtonState("new")
				this.form.op = "create";
				this.panel.items.items[0].setTitle(this.title);
				this.form.selectValue = this.selectValue;
				this.form.doNew();
				this.list.op = "create";
				this.list.doNew();
				// this.list.doCreate();
			},
			//计划单引入
			doJhd:function(){
				this.jhdList = this.createModule("jhdList", this.refJhd);
				this.jhdList.on("confirm", this.onConfirm, this);
				var win = this.jhdList.getWin();
				win.add(this.jhdList.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
				this.jhdList.clearSelect();
				this.jhdList.loadData();
				}
			},
			//计划单确认引入
			onConfirm:function(r,bz,dwxh,dwmc){
			if(dwxh){
			this.form.data["DWXH"]=dwxh;
			this.form.form.getForm().findField("DWMC").setValue(dwmc);
			}
			this.form.form.getForm().findField("RKBZ").setValue(bz);
			var store = this.list.grid.getStore();
			var l=r.length;
			var s=store.getCount();
			for(var i=0;i<l;i++){
				var t=false;
				for(var j=0;j<s;j++){//判断如果计划单已经引入则不重复引入
				if(r[i].JHSBXH==store.getAt(j).get("JHSBXH")){
				t=true;
				break;
				}
				}
				if(t){
				continue;
				}
			var record=new Ext.data.Record(r[i]);
			store.add(record);
			}
			this.list.doJshj();
			}
		});