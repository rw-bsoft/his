$package("phis.application.cic.script")

$import("phis.script.EditorList", "org.ext.ux.CheckColumn", "phis.script.util.DateUtil")

phis.application.cic.script.ClinicPrescriptionEntryList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	// cfg.summaryable = true;
	cfg.closeAction = "hide";
	cfg.sortable = false;
	cfg.minListWidth = 570;
	cfg.CFXYZYMXSL = 0;
	cfg.CFCYMXSL = 0;
	cfg.remoteTpl = '<td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="60px" style="color:red">{YYZBM}</td><td width="160px" title="{YPMC}">({YBFL_text}){YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="50px">{LSJG}</td><td width="50px">{KCSL}</td>';
	this.serviceId = "clinicManageService";
	/* 注释拱墅区版本中有关医保的代码Ext.apply(this, com.bsoft.phis.yb.YbUtil); */
	// this.ypzh = 1;
	phis.application.cic.script.ClinicPrescriptionEntryList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
	this.on("loadData", this.afterLoadData, this);
}
var ctx_cf = null;
/**************begin 增加合理用药接口 zhaojian 2017-12-18*****************/
McAjax.Ready();
/*************end**************/

Ext.extend(phis.application.cic.script.ClinicPrescriptionEntryList,
		phis.script.EditorList, {
			onReady : function() {
				ctx_cf = this;
				if (this.autoLoadData) {
					this.loadData();
				}
				var el = this.grid.el
				if (!el) {
					return
				}
				var actions = this.actions
				if (!actions) {
					return
				}
				var keyMap = new Ext.KeyMap(el)
				keyMap.stopEvent = true

				// index btns
				var btnAccessKeys = {}
				var keys = []
				if (this.showButtonOnTop) {
					var btns = this.grid.getTopToolbar().items;
					var n = btns.getCount()
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i)
						var key = btn.accessKey
						if (key) {
							btnAccessKeys[key] = btn
							keys.push(key)
						}
					}
				} else {
					var btns = this.grid.buttons
					for (var i = 0; i < btns.length; i++) {
						var btn = btns[i]
						var key = btn.accessKey
						if (key) {
							btnAccessKeys[key] = btn
							keys.push(key)
						}
					}
				}
				this.btnAccessKeys = btnAccessKeys
				keyMap.on(keys, this.onAccessKey, this)
				keyMap.on(Ext.EventObject.ENTER, this.onEnterKey, this)
				// this.grid.on("beforeCheckedit", this.beforeCheckEdit, this)
				this.grid.on("afterCheckedit", this.afterCheckedit, this)
			},
			onEnterKey : function() {
				Ext.EventObject.stopEvent();
			},
			afterLoadData : function(store) {
				if (store.getCount() > 0) {
					if (store.getAt(0).get("SFJG") == 1) {
						var tab = this.opener.tab.getActiveTab();
						if (tab.fphm || tab.zfpb == 1)// 收费或者已退费的情况下，不设置已审核状态
							return;
						this.opener.setChargeInfo(true, "audit.png");
					}
				}
			},
			setBtnStatus : function(disabled) {
				var btns = this.grid.getTopToolbar().items;
				for (var i = 0; i < btns.getCount(); i++) {
					var btn = btns.item(i);
					if (disabled) {
						if (i != 5) {
							btn.disable();
						}
					} else {
						btn.enable();
					}
				}
			},
			expansion : function(cfg) {
				var checkBox = new Ext.form.Checkbox({
					id : "autoNewGroup",
					boxLabel : "自动新组"
				});
				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push(checkBox, ['-'], tbar);

				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='cfmx_tjxx_"
							+ this.openedBy
							+ "' align='center' style='color:blue'>统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "合计金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "自负金额：0.00&nbsp;&nbsp;￥</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			onWinShow : function() {
				var pslist = this.midiModules["pslist"];
				pslist.refresh();
			},
			// 改变用法,变更附加项目
			ypyfSelect : function(f, record, index, r) {
				// console.log('ypyfSelect');
				if (!r) {
					r = this.getSelectedRecord()
					if (r == null) {
						return
					}
				}
				if (!record)
					return;
				// 判断是否用法是否修改
				if (r.get("GYTJ") == record.get("key") && index != -1)
					return;
				this.removeFjxm(r);
				this.exList.store.removeAll();
				if (record && record.get("FYXH") > 0) {// 存在附加项目
					if (r.get("YPXH") > 0
							|| (r.get("BZMC") && r.get("BZMC").length > 0)) {// 增加自备药
						// 根据FYXH查询是否有附加项目
						var ksdm = this.mainApp['phis'].departmentId;
						if (this.openedBy != "doctorStation") {
							ksdm = this.opener.form.getForm().findField("KSDM")
									.getValue();
							if (!ksdm) {
								// 收费处,KSDM为空时,只查所有的
								ksdm = "0";
							}
						}
						phis.script.rmi.jsonRequest({
							serviceId : "wardPatientManageService",
							serviceAction : "loadAppendAdvice",
							body : {
								FYXH : record.get("FYXH"),
								KSDM : ksdm,
								MZSY : "1"
							}
						}, function(code, msg, json) {
							if (code < 300) {
								var records = [];
								var body = json.body;
								var nowtime = new Date().getTime();
								var uniqueId = r.get("SBXH") > 0 ? r// 保存过的数据取医嘱组号，新记录用当前时间标记
										.get("YPZH") : nowtime;
								this.store.each(function(rs) {
									if (rs.get('YPZH_SHOW') == r
											.get('YPZH_SHOW')) {
										rs.set("uniqueId", uniqueId);
									}
								}, this)
								var ylsl = 1;
								if (r.get("MRCS")) {
									ylsl = r.get("MRCS") * r.get("YYTS");
								}
								for (var i = 0; i < body.length; i++) {
									var data = {
										_opStatus : 'create',
										SBXH : "",
										FYGB : body[i].FYGB,
										FYMC : body[i].YZMC,
										FYDW : body[i].FYDW,
										YLXH : body[i].YPXH,
										YLSL : ylsl * body[i].FYSL,
										YLSL_YS : body[i].FYSL,
										YLDJ : body[i].YPDJ,
										HJJE : body[i].YPDJ * ylsl
												* body[i].FYSL,
										YSGH : r.get("YSGH"),
										uniqueId : uniqueId
									};
									Ext.applyIf(data, r.data);
									var rd = new Ext.data.Record(data);
									this.exList.setZFBL(
											this.exContext.empiData.BRXZ, rd)
									records.push(rd);
								}
								this.exList.totalStore.add(records);
								this.exList.store.add(records);

								// console.log('存在附加费用');
								// console.log(r);
								// console.log(uniqueId);
								// 是否已经存在非原液皮试费用
								this.containYypsyp(r, uniqueId);
							} else {
								this.processReturnMsg(code, msg)
							}
						}, this)
					}
				} else {
					var nowtime = new Date().getTime();
					var uniqueId = r.get("SBXH") > 0 ? r// 保存过的数据取医嘱组号，新记录用当前时间标记
							.get("YPZH") : nowtime;
					// console.log('不存在附加费用________---:');
					// console.log(r);
					// console.log(uniqueId);

					// 如果是true则是新增的药品
					var addFlag = true;
					if (r.data._opStatus != 'create')
						addFlag = false;
					else {
						var selrow = this.getSelectedRecord();
						selrow.set("uniqueId", uniqueId)
					}
					// this.containYypsyp(r,uniqueId,addFlag);
					this.containYypsyp(r, uniqueId, addFlag);
				}
			},
			removeFjxm : function(r) {
				if (!this.exList)
					return;
				var uniqueId = (r.get("YPZH") > 0) ? "YPZH" : "uniqueId";
				for (var i = 0; i < this.exList.totalStore.getCount(); i++) {
					var record = this.exList.totalStore.getAt(i);
					if (r.get(uniqueId)
							&& record.get(uniqueId) === r.get(uniqueId)) {
						if (record.get("SBXH") > 0) {
							record.set("_opStatus", "remove");
						} else {
							this.exList.totalStore.removeAt(i);
							i--;
						}
					}
				}
			},
			/**
			 * 重写doRemove，当grid中的数据未保存在数据库时，直接从grid中删除，若删除的数据 已保存，则发起请求删除数据库中数据
			 */
			doRemove : function() {
				if (this.store.getCount() > 0) {
					var record = this.store.getAt(0);
					var sfjg = record.get('SFJG');
					if (sfjg && sfjg == 1) { // 审核通过的处方不能编辑
						MyMessageTip.msg("提示", '该处方已审核，不能编辑!', true, 5);
						return;
					}
				}
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}

				// 增加皮试药品判断，如果该药品是皮试药品已经在皮试了就不能删除
				// if(r.get("CFSB")&&r.get("PSPB")>0){
				// debugger;
				// var psreq = phis.script.rmi.miniJsonRequestSync({
				// serviceId : this.serviceId,
				// serviceAction : "loadSfps",
				// body : {
				// CFSB:r.get("CFSB"),
				// YPBH:r.get("YPXH")
				// }
				// });
				// if (psreq.code > 300) {
				// Ext.Msg.alert("提示", psreq.msg);
				// return;
				// }
				// if (psreq.json.body) {
				// var psdata = psreq.json.body;
				// if(data.sfps)
				// {
				// Ext.Msg.alert("提示","该药已经开始皮试处理，无法删除");
				// return;
				// }
				//
				// }
				// }
				// ---------------------
				if ((r.get("YPXH") == null || r.get("YPXH") == "")
						&& (r.get("YPMC") == null || r.get("YPMC") == "")) {
					this.store.remove(r);
					if (!this.hasEffectGroupRecord(r)) {
						this.removeFjxm(r);
					}
					this.reloadYPZH();
					this.opener.setCountInfo();
					// 移除之后焦点定位
					var count = this.store.getCount();
					if (count > 0) {
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
					} else {
						this.doNewGroup();
					}
					this.onRowClick();
					return;
				}
				// 判断是否已经开始皮试
				if (r.get("PSPB") > 0 && r.get("CFSB")) {
					if (!this.checkSkinTestStatus(r.data)) {
						return;
					}
				}

				if (confirm("确认删除药品名称为【" + r.data.YPMC + "】的处方明细吗？")) {// Ext的确认框被窗口遮挡
					// this.store.remove(r);
					// 移除之后焦点定位
					if (r.data.SBXH) {
						// 记录后台需要删除的处方识别
						this.fireEvent("doRemove", r.data);
					}
					this.store.remove(r);
					if (!this.hasEffectGroupRecord(r)) {
						this.removeFjxm(r);
					} else {
						// console.log(this.store);
						var fyypsfyflag = false;
						this.store.each(function(record) {
							// console.log(record);
							if (record.data.PSPB == 1)
								fyypsfyflag = true;

						})
						if (!fyypsfyflag) {
							this.delStorePsfy();
							this.delTotalStorePsfy(true);

						}
					}

					this.reloadYPZH();
					this.opener.setCountInfo();
					// 移除之后焦点定位
					var count = this.store.getCount();
					if (count > 0) {
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
					} else {
						this.doNewGroup();
					}
					this.onRowClick();
				}
			},
			/****************add by lizhi 2017-10-11增加删除组功能*****************/
			doRemoveGroup : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var ypzh = r.get("YPZH");
				var ypzh_show =r.get("YPZH_SHOW");
				if (this.store.getCount() > 0) {
					for (var i = 0; i < this.store.getCount(); i++) {
						var r = this.store.getAt(i);
						if (r == null) {
							return
						}
						if (r.get("YZZH") == ypzh){
							var sfjg = r.get('SFJG');
							if (sfjg && sfjg == 1) { // 审核通过的处方不能编辑
								MyMessageTip.msg("提示", '该处方已审核，不能编辑!', true, 5);
								return;
							}
						}
						if ((r.get("YPXH") == null || r.get("YPXH") == "")
								&& (r.get("YPMC") == null || r.get("YPMC") == "")) {
							this.store.remove(r);
							if (!this.hasEffectGroupRecord(r)) {
								this.removeFjxm(r);
							}
							this.reloadYPZH();
							this.opener.setCountInfo();
							// 移除之后焦点定位
							var count = this.store.getCount();
							if (count > 0) {
								cm.select(cell[0] < count ? cell[0] : (count - 1),
										cell[1]);
							} else {
								this.doNewGroup();
							}
							this.onRowClick();
							return;
						}
						// 判断是否已经开始皮试
						if (r.get("PSPB") > 0 && r.get("CFSB")) {
							if (!this.checkSkinTestStatus(r.data)) {
								return;
							}
						}
					}
				}
				Ext.Msg.show({
							title : '提示',
							msg : '确认删除同组处方明细？',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									for (var i = 0; i < this.store.getCount(); i++) {
										var r = this.store.getAt(i);
										if (r.get("YPZH") == ypzh
											&& r.get("YPZH_SHOW") == ypzh_show) {
											if (r.data.SBXH) {// 记录后台需要删除的处方识别
												this.fireEvent("doRemove", r.data);
											}
											this.store.remove(r);
											i--;
											if (!this.hasEffectGroupRecord(r)) {
												this.removeFjxm(r);
											} else {
												var fyypsfyflag = false;
												this.store.each(function(record) {
													if (record.data.PSPB == 1)
														fyypsfyflag = true;
												})
												if (!fyypsfyflag) {
													this.delStorePsfy();
													this.delTotalStorePsfy(true);
						
												}
											}
										}
									}
									this.reloadYPZH();
									this.doNewGroup();
								}
							},
							scope : this
						});
			},
			/****************add by lizhi 2017-10-11增加删除组功能*****************/
			checkSkinTestStatus : function(data) {
				if (this.exContext.systemParams.QYPSXT != 1)
					return true;

				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "querySkinTestStatus",
					body : data
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return false;
				} else {
					if (r.json.skintestRunning == 1) {
						MyMessageTip.msg("提示", "药品【" + data.YPMC
								+ "】已经皮试了，不允许删除!", true);
						return false;
					}
				}
				return true;
			},
			reloadYPZH : function() {
				var count = this.store.getCount();
				var ypzh = 0;
				var lastYPZH = -1;
				for (var i = 0; i < count; i++) {
					var now_ypzh = this.store.getAt(i).get("YPZH_SHOW");
					if (now_ypzh != lastYPZH) {
						ypzh++;
						this.store.getAt(i).set("YPZH_SHOW", ypzh);
						lastYPZH = now_ypzh;
					} else {
						this.store.getAt(i).set("YPZH_SHOW", ypzh);
					}
				}

			},
			doInsert : function(item, e, newGroup) {// 当前记录前插入一条记录
				if (this.opener.bclLocked)
					return;
				if (this.store.getCount() > 0) {
					var record = this.store.getAt(0);
					var sfjg = record.get('SFJG');
					if (sfjg && sfjg == 1) { // 审核通过的处方不能编辑
						MyMessageTip.msg("提示", '该处方已审核，不能编辑!', true, 5);
						return;
					}
				}
				var selectdRecord = this.getSelectedRecord();
				var selectRow = 0;
				if (selectdRecord) {
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if ((selectdRecord.get("YPXH") == null
							|| selectdRecord.get("YPXH") == "" || selectdRecord
							.get("YPXH") == 0)
							&& !selectdRecord.get("BZMC")) {
						selectRow = selectRow;
					} else {
						selectRow = this.store.indexOf(selectdRecord) + 1;
					}
				} else {
					if (this.store.getCount() > 0) {
						selectRow = this.store.getCount();
					}
				}
				var row = selectRow;
				var store = this.grid.getStore();

				/** modified by gaof 2013-9-26 根据系统参数限制每张处方明细条数 */
				var type = 1;
				if (this.store.getCount() > 0) {
					type = store.getAt(0).get("TYPE");
				}
				if (type == 1 || type == 2) {
					// 西药中药
					if (this.CFXYZYMXSL != 0) {
						if (store.getCount() >= this.CFXYZYMXSL) {
							MyMessageTip.msg("提示,纸张容量限制", '西药中药处方不能超过'
									+ (this.CFXYZYMXSL) + '条药品!', true, 5);
							return;
						}
					}
				} else if (type == 3) {
					// 草药
					if (this.CFCYMXSL != 0) {
						if (store.getCount() >= this.CFCYMXSL) {
							MyMessageTip.msg("提示,纸张容量限制", '草药处方不能超过'
									+ (this.CFCYMXSL) + '条药品!', true, 5);
							return;
						}
					}
				}

				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				var items = this.schema.items
				var factory = util.dictionary.DictionaryLoader
				var data = {
					'_opStatus' : 'create'
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					var v = null
					if (it.defaultValue) {
						v = it.defaultValue
						data[it.id] = v
						var dic = it.dic
						if (dic) {
							var o = factory.load(dic)
							if (o) {
								var di = o.wraper[v]
								if (di) {
									data[it.id + "_text"] = di.text
								}
							}
						}
					}
				}
				var r = new Record(data)
				try {
					store.insert(row, [r])
				} catch (e) {
					store.removeAll();
					return;
				}
				this.grid.getView().refresh()// 刷新行号
				var storeData = store.data;
				var maxIndex = store.getCount();				
				if (maxIndex == 1) {// 处方的第一条记录或者自动新组
					var rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("YYTS", 1);
					rowItem.set("YPZH_SHOW", 1);
					// this.ypzh++;
				} else {
					var upRowItem = storeData.itemAt(row - 1);
					var rowItem = storeData.itemAt(row);
					rowItem.set("YPZH_SHOW", upRowItem.get("YPZH_SHOW"));
					rowItem.set("YPZH", upRowItem.get("YPZH"));
					rowItem.set("YYTS", upRowItem.get("YYTS"));
					rowItem.set("YPYF", upRowItem.get("YPYF"));
					rowItem.set("YPYF_text", upRowItem.get("YPYF_text"));
					if (this.opener.form.getForm().findField("CFLX").getValue() != 3) {
						rowItem.set("GYTJ", upRowItem.get("GYTJ"));
						rowItem.set("GYTJ_text", upRowItem.get("GYTJ_text"));
					}
					if (this.opener.form.getForm().findField("CFLX").getValue() == 3) {
						rowItem.set("JZ", upRowItem.get("JZ"));
						rowItem.set("JZ_text", upRowItem.get("JZ_text"));
					}
					rowItem.set("MRCS", upRowItem.get("MRCS"));
					rowItem.set("uniqueId", upRowItem.get("uniqueId"));
				}
				this.grid.startEditing(row, 3);
				this.onRowClick();

			},
			doInsertAfter : function(item, e, newGroup, stopEditor) {// 当前记录后插入一条记录
				if (this.store.getCount() > 0) {
					var record = this.store.getAt(0);
					var sfjg = record.get('SFJG');
					if (sfjg && sfjg == 1) { // 审核通过的处方不能编辑
						MyMessageTip.msg("提示", '该处方已审核，不能编辑!', true, 5);
						return;
					}
				}
				this.removeEmptyRecord();
				var store = this.grid.getStore();
				var storeData = store.data;
				var maxIndex = store.getCount();

				/** modified by gaof 2013-9-26 根据系统参数限制每张处方明细条数 */
				var type = 1;
				if (this.store.getCount() > 0) {
					type = store.getAt(0).get("TYPE");
				}
				if (type == 1 || type == 2) {
					// 西药中药
					if (this.CFXYZYMXSL != 0) {
						if (store.getCount() >= this.CFXYZYMXSL) {
							MyMessageTip.msg("提示,纸张容量限制", '西药中药处方不能超过'
									+ (this.CFXYZYMXSL) + '条药品!', true, 5);
							return;
						}
					}
				} else if (type == 3) {
					// 草药
					if (this.CFCYMXSL != 0) {
						if (store.getCount() >= this.CFCYMXSL) {
							MyMessageTip.msg("提示,纸张容量限制", '草药处方不能超过'
									+ (this.CFCYMXSL) + '条药品!', true, 5);
							return;
						}
					}
				}

				this.doCreate();
				var autoNewGroup = this.grid.getTopToolbar()
						.get("autoNewGroup").getValue();
				if (newGroup == true)
					autoNewGroup = true;
				if (maxIndex == 0 || autoNewGroup) {// 处方的第一条记录或者自动新组
					var ypzh = 1;
					var upRowItem = storeData.itemAt(maxIndex - 1);
					if (maxIndex > 0) {
						ypzh = upRowItem.get("YPZH_SHOW") + 1;
					}
					var rowItem = storeData.itemAt(maxIndex);
					rowItem.set("YYTS", 1);
					rowItem.set("YPZH_SHOW", ypzh);
					// this.ypzh++;
				} else {
					var upRowItem = storeData.itemAt(maxIndex - 1);
					var rowItem = storeData.itemAt(maxIndex);
					rowItem.set("YPZH_SHOW", upRowItem.get("YPZH_SHOW"));
					rowItem.set("YPZH", upRowItem.get("YPZH"));
					rowItem.set("YYTS", upRowItem.get("YYTS"));
					rowItem.set("YPYF", upRowItem.get("YPYF"));
					rowItem.set("YPYF_text", upRowItem.get("YPYF_text"));
					if (this.opener.form.getForm().findField("CFLX").getValue() != 3) {
						rowItem.set("GYTJ", upRowItem.get("GYTJ"));
						rowItem.set("GYTJ_text", upRowItem.get("GYTJ_text"));
					}
					rowItem.set("MRCS", upRowItem.get("MRCS"));
					rowItem.set("uniqueId", upRowItem.get("uniqueId"));
				}
				if (!stopEditor) {
					this.grid.startEditing(maxIndex, 3);
				} else {
					this.grid.getSelectionModel().select(maxIndex, 0)
				}
				this.onRowClick();
			},
			setBackInfo : function(obj, record) {
				/** 2013-09-15 add by gaof 为拱墅区医保项目，增加(需要审批的药品和费用审批编号录入方法) * */
				// record.data.BRID = this.exContext.ids.brid;
				// record.data.BRXZ = this.exContext.empiData.BRXZ;
				// record.data.DJLX = 1;
				// r = phis.script.rmi.miniJsonRequestSync({
				// serviceId : this.serviceId,
				// serviceAction : "queryIsNeedVerify",
				// body : record.data
				// });
				// if (r.code > 300) {
				// this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
				// return;
				// } else {
				// if (r.json.isNeedVerify == 1) {
				// MyMessageTip.msg("提示", r.json.ZWMC
				// + "需要审批，请病人去收费处划价时准备好审批编号", true);
				// }
				// }
				/**
				 * 2013-09-09 add by gejj 为拱墅区医保项目，增加1.16.
				 * 医保二乙标志和因公因伤标志病人药品和费用的自负比例*
				 */
				/*
				 * 注释拱墅区版本中有关医保的代码 var json = {}; json.BRID =
				 * this.exContext.ids.brid; json.BRXZ =
				 * this.exContext.empiData.BRXZ; json.al_ypxh =
				 * record.data.YPXH;// 药品序号 json.al_ypcd = record.data.YPCD;//
				 * 药品产地 json.ai_yplx = record.data.TYPE;// 药品类型 var rs =
				 * this.requestServer("queryMedicareZFBL", json); var rValue =
				 * rs.json.body; var zfpb = 0; var _ctr = this; if (-1 ==
				 * rValue) {// 返回-1时直接返回，不插入选择项 obj.collapse(); return; } else
				 * if (-1000 == rValue) {// 在-1000的情况下弹出框 obj.collapse();
				 * Ext.MessageBox.show({ title : '提示', msg : '此项目因工因伤使用,是否继续:',
				 * width : 300, buttons : Ext.MessageBox.YESNOCANCEL, fn :
				 * function(btn, text) { if ("yes" == btn) { zfpb = 3; } else if
				 * ("no" == btn) { zfpb = 1; } else { zfpb = -1; obj.collapse();
				 * return; } record.data.ZFPB = zfpb; _ctr.setData(obj, record); } },
				 * this); } else {// rValue 0 -1 zfpb = rValue; record.data.ZFPB =
				 * zfpb; this.setData(obj, record); }
				 *//** 2013-09-09 end add* */
				this.setData(obj, record);
			},
			YYZBMRenderer : function(value, metaData, r, row, col) {
				if(value==undefined){
					return "";
				}
				return "<font style='color:red;font-weight:bold'>"+value+"</font>";
			},
			/**
			 * 2013-09-10 add by gejj
			 * 该方法中的代码是从setBackInfo中的代码，在原有setBackInfo方法中被修改成拱墅医保获取二乙标志和因公因伤标志*
			 */
			setData : function(obj, record) {
				Ext.EventObject.stopEvent();// 停止事件
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var griddata = this.grid.store.data;
				var rowItem = griddata.itemAt(row);
				record.data.YPZH_SHOW = rowItem.get("YPZH_SHOW");				
				if (!this.setMedRecordIntoList(record.data, rowItem, row)) {
					return;
				}
				obj.setValue(record.get("YPMC"));
				obj.collapse();
				obj.triggerBlur();
				// 获取药品自负比例信息
				this.opener.setMedQuantity(rowItem);
				this.getPayProportion(record.data, rowItem);
				// 调用异步判断
				this.step = 1;
				this.asyncLoopFunc(rowItem);
			},
			/**
			 * 处方信息录入代码重构,更好的支持异步循环(如组套调入,处方复制)
			 * 
			 * @records 需要处理的数组
			 * @全局step 递归时判断需要执行的步骤:大于0表示需要执行的步骤编号,-1表示失败结束,99表示成功结束
			 * @callBack 方法结束后需要执行的
			 */
			asyncLoopFunc : function(record, fn, scope) {
				if (fn) {
					this.params = {};
					this.params.fn = fn;
					this.params.scope = scope;
				}
				switch (this.step) {
					case 1 :
						// 皮试药品判别
						this.step++;
						this.getSkinTest(record);
						break;
					case 2 :
						// 抗菌药物判别
						this.step++;
						this.doRemainMedic(record)
						break;
					case 3 :
						// 重复用药判别(演示版本)
						this.step++;
						this.asyncLoopFunc(record)
						break;
					default :
						if (this.params && this.params.fn) {
							var tmp = {};
							Ext.apply(tmp, this.params);
							delete this.params;
							Ext.callback(tmp.fn, tmp.scope || this);
						} else {
							if (this.step == -1) {// 插入失败
								this.doNewGroup();
							} else {
								var cell = this.grid.getSelectionModel()
										.getSelectedCell();
								var row = cell[0];
								if (this.opener.form.getForm()
										.findField("CFLX").getValue() == 3) {
									this.grid.startEditing(row, 7);
								} else {
									this.grid.startEditing(row, 5);
								}
							}
						}
				}

			},
			setMedRecordIntoList : function(data, rowItem, curRow) {
				if (data.FYGB) {
					rowItem.set('FYGB', data.FYGB);
				} else {
					Ext.Msg.alert("提示", "查询药品账簿类别失败，请正确维护药品账簿类别信息!");
					return false;
				}
				// 将选中的记录设置到行数据中
				var store = this.grid.getStore();
				var n = store.getCount()				
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					// 特殊药品后台查询判别
					if (i == 0
							&& curRow != 0
							&& (this.tscfPb == "" || this.tscfPb == null || this.tscfPb == "null")) {
						var tsypHx = r.get("YPXH");
						if (tsypHx != null && tsypHx != "") {
							this.getTsypPb(tsypHx);
						} else {
							this.tscfPb = false;
							this.tsypBs = "";
						}
					}
					var curPB = this.getRowid("curPB");
					if (curRow == 0 || curPB == true) {
						this.tscfKey = true;
					} else {
						this.tscfKey = false;
					}
					// 特殊药品判别结束
					if (i != curRow
							&& r.get("YPZH_SHOW") == rowItem.get("YPZH_SHOW")) {
						if (r.get("YPXH") == data.YPXH) {
							MyMessageTip.msg("提示", "\"" + data.YPMC
									+ "\"在这组中已存在，请进行修改!", true);
							return false;
						}
					}
					//根据病人性质及药品对照情况进行判定提醒
					if((!curRow || i != curRow) && this.exContext.empiData.BRXZ==2000 && r.get("YYZBM")!=undefined && data.YYZBM!=undefined && r.get("YYZBM").replace(/\s+/g,"").length!=data.YYZBM.replace(/\s+/g,"").length){
							MyMessageTip.msg("提示", "医保病人的同一处方中只能开自费药品或医保药品！", true);
							return false;
					}
				}
				// rowItem.set('YPZH', data.YPZH);
				rowItem.set('PSPB', data.PSPB);
				rowItem.set('JYLX', data.JYLX);
				this.crcfPb = true;// 处方插入与处方复制的判别
				if (!this.checkDoctorPermission(data, rowItem))// 判断药品权限
					return false;
				this.crcfPb = false;
				// var rowItem = griddata.itemAt(row);
				rowItem.set('YYZBM', data.YYZBM);
				rowItem.set('YPMC', data.YPMC);
				rowItem.set('YFGG', data.YFGG);
				rowItem.set('JLDW', data.JLDW);
				rowItem.set('YFDW', data.YFDW);
				rowItem.set('YPXH', data.YPXH);
				rowItem.set('YPJL', data.YPJL);
				rowItem.set('YCJL', data.YCJL);
				rowItem.set('YFBZ', data.YFBZ);
				rowItem.set('YPDJ', data.LSJG);
				rowItem.set('TYPE', data.TYPE);
				rowItem.set('YPCD', data.YPCD);
				rowItem.set('ZFPB', data.ZFPB);// 2013-09-10 add by gejj 添加自负判别
				// rowItem.set('ZSSF', data.ZSSF);// 2013-10-03 add by Zhangxw
				// 抗生标志
				rowItem.set('KSBZ', data.KSBZ);
				rowItem.set('YQSYFS', data.YQSYFS);
				rowItem.set('KSSDJ', data.KSSDJ);
				rowItem.set('YCYL', data.YCYL);
				rowItem.set('ZFYP', data.ZFYP == 1 ? true : false);
				rowItem.set('ZBY', data.ZFYP);
				// 添加输液判别
				if (data.YPCD_text) {
					rowItem.set('YPCD_text', data.YPCD_text);
				} else {
					rowItem.set('YPCD_text', data.CDMC);
				}
				// 判断是否已经存在GYTJ GYFF
				if (rowItem.get("GYTJ") == null || rowItem.get("GYTJ") == ""
						|| rowItem.get("GYTJ") == 0) {
					rowItem.set("GYTJ", data.GYFF);
					rowItem.set("GYTJ_text", data.GYFF_text)
					// 关联附加项目
					var gytj = this.grid.getColumnModel().getColumnById("GYTJ").editor;
					var gytj_r = gytj.findRecord("key", data.GYFF);
					this.ypyfSelect(gytj, gytj_r, -1, rowItem);// 附加项目
				}
				return true;
			},
			getPayProportion : function(data, rowItem) {
				var body = {};
				body.BRXZ = this.exContext.empiData.BRXZ;
				body.TYPE = data.TYPE;
				body.FYGB = rowItem.get("FYGB");
				body.FYXH = data.YPXH;
				phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : "getPayProportion",
					body : body
				}, function(code, msg, json) {
					if (code >= 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					var zfbl = json.body.ZFBL;
					rowItem.set('ZFBL', zfbl);
					this.opener.setCountInfo();
				}, this);

			},
			checkDoctorPermission : function(data, rowItem) {
				// alert(Ext.encode(this.grid.getRow(1)))
				// var rowOne=this.grid.getRow(1);

				if (this.crcfPb) {// 处方插入与处方复制的判别
					this.crcfPb = false;
					if (this.tscfKey) {
						if (data.TSYP == 1 || data.TSYP == 7 || data.TSYP == 8) {
							this.tscfPb = true;
							this.tsypBs = data.TSYP;
						} else {
							this.tscfPb = false;
						}
					}
					if (!this.tscfPb) {
						if (data.TSYP == 1 || data.TSYP == 7 || data.TSYP == 8) {
							MyMessageTip.msg("提示",
									'该处方已开具普通药品，特殊药品不能和普通药品开具同一张处方上!', true);
							return false;
						}
					}
					if (this.tscfPb) {
						if (data.TSYP == 1 || data.TSYP == 7 || data.TSYP == 8) {
							if (this.tsypBs == 1 && data.TSYP != 1) {
								MyMessageTip.msg("提示",
										'该处方已开具麻醉特殊药品，非麻醉特殊药品不能开具在该处方上!', true);
								return false;
							} else if (this.tsypBs == 7 && data.TSYP != 7) {
								MyMessageTip.msg("提示",
										'该处方已开具“精一”特殊药品，非“精一”特殊药品不能开具在该处方上!',
										true);
								return false;
							} else if (this.tsypBs == 8 && data.TSYP != 8) {
								MyMessageTip.msg("提示",
										'该处方已开具“精二”特殊药品，非“精二”特殊药品不能开具在该处方上!',
										true);
								return false;
							}
						} else {
							MyMessageTip.msg("提示",
									'该处方已开具特殊药品，普通药品不能和特殊药品开具同一张处方上!', true);
							return false;
						}
					}
				}

				if (this.openedBy != "doctorStation")
					return true;
				if (data.TSYP > 0) {
					if (data.TSYP == this.exContext.systemParams.MZYP) {
						if (this.exContext.docPermissions.MZYQ == ""
								|| this.exContext.docPermissions.MZYQ == "0") {
							MyMessageTip.msg("提示", '药品【' + data.YPMC
									+ '】是麻醉类药品，你暂不能开麻醉类处方!', true);
							return false;
						}
					}
					if (data.TSYP == this.exContext.systemParams.JSYP) {
						if (this.exContext.docPermissions.JSYQ == ""
								|| this.exContext.docPermissions.JSYQ == "0") {
							MyMessageTip.msg("提示", '药品【' + data.YPMC
									+ '】是精神类药品，你暂不能开精神类处方!', true);
							return false;
						}
					}
				}
				// 增加抗生素管理功能 add by yangl
				// 1、判断是否抗生素（具体规则待需求完善）QYSJYSSQ
				if (this.exContext.systemParams.QYKJYWGL == 1 && data.KSBZ > 0) {
					if (!data.KSSDJ
							|| !this.exContext.docPermissions.KSSQX
							|| this.exContext.docPermissions.KSSQX
									.indexOf(data.KSSDJ) < 0) {// 医生没有权限
						// 判断药物提醒方式
						if (!data.YQSYFS || data.YQSYFS == '2') {
							MyMessageTip.msg("提示", "您没有抗菌药物【" + data.YPMC
									+ "】的使用权限！", true);
							return false;
						}
					}
				}
				return true;
			},
			doRemainMedic : function(rowItem) {
				// 显示提示信息
				var _ctx = this;
				var data = rowItem.data;
				if (this.exContext.systemParams.QYKJYWGL == 1 && data.KSBZ > 0) {
					if (!data.KSSDJ
							|| !this.exContext.docPermissions.KSSQX
							|| this.exContext.docPermissions.KSSQX
									.indexOf(data.KSSDJ) < 0) {// 医生没有权限
						// 判断药物提醒方式
						if (!data.YQSYFS || data.YQSYFS == '2') {
							this.asyncLoopFunc(rowItem);
							return;
						} else {
							setTimeout(toShowMsg, 200);// 延迟调用，解决焦点问题
						}
					} else {
						// 有权限
						if (this.doInputReason()) {
							this.asyncLoopFunc(rowItem);
						}
					}
				} else {
					this.asyncLoopFunc(rowItem);
				}
				function toShowMsg() {
					_ctx.grid.stopEditing();
					Ext.Msg.show({
						title : '提示',
						msg : '您没有抗菌药物（' + data.YPMC
								+ '）的使用权限！若越权使用，限一日剂量，是否确认要越权使用？',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.YESNO,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "yes") {
								var r = this.getSelectedRecord();
								r.set("YQSY", 1);
								// 弹出上级医生密码验证
								if (this.exContext.systemParams.QYSJYSSQ == 1) {
									this.mask("请稍等...")
									var win = this.getVerifyWin(data);
									win.show();
									this.unmask()
								} else {
									// 是否录入原因
									if (this.doInputReason()) {
										this.asyncLoopFunc(rowItem);
									}
								}
							} else {
								var r = this.getSelectedRecord();
								this.store.remove(r)
								if (!this.hasEffectGroupRecord(r)) {
									this.removeFjxm(r);
								}
								this.step = -1;
								this.asyncLoopFunc(rowItem);
							}
						},
						scope : _ctx
					})
				}
			},
			// 抗菌药物录入原因
			doInputReason : function() {
				if (this.exContext.systemParams.QYKJYWGL == 1
						&& this.exContext.systemParams.QYKJYYY == '1') {
					// 弹出录入原因界面
					var AntibioticsForm = this.createModule("AntibioticsForm",
							this.refAntibioticsReasonForm);
					if (!this.AntibioticsWin) {
						AntibioticsForm.on("AntibioticsConfirm",
								function(syyy) {
									var r = this.getSelectedRecord();
									r.set("SYYY", syyy);
									this.asyncLoopFunc(r);
								}, this);
						this.AntibioticsWin = AntibioticsForm.getWin();
						this.AntibioticsWin.add(AntibioticsForm.initPanel());
					}
					var r = this.getSelectedRecord();
					AntibioticsForm.syyy = r.get("SYYY");
					this.AntibioticsWin.show();
					return false;
				}
				return true;
			},
			// 验证上级医生权限
			getVerifyWin : function(data) {
				if (this.verifyWin) {
					this.form.KSSDJ = data.KSSDJ;
					return this.verifyWin;
				}
				if (!Ext.get('x-logo')) {
					Ext.DomHelper.append(document.body, {
						tag : "div",
						id : "x-logo"
					})
				}
				this.logo = new Ext.BoxComponent({
					el : 'x-logo',
					cls : 'x-logon-winlogo',
					height : 85
				})
				this.form = new Ext.FormPanel({
					frame : true,
					labelWidth : 75,
					labelAlign : 'top',
					// defaults: {width: '95%'},
					defaultType : 'textfield',
					shadow : true,
					items : [{
						fieldLabel : '登录名',
						name : 'uid',
						width : '95%'
					}, {
						fieldLabel : '密码',
						name : 'psw',
						inputType : 'password',
						width : '95%'
					}]
				})
				var fldUid = this.form.items.item(0)
				var fldPsw = this.form.items.item(1)

				// fldUid.on("blur", this.loadRole, this)
				// fldPsw.on("blur", this.loadRole, this)

				fldUid.on("specialkey", function(f, e) {
					if (e.getKey() == e.ENTER) {
						if (f.getValue) {
							this.form.items.item(1).focus(true);
						}
					}

				}, this)
				fldPsw.on("specialkey", function(f, e) {
					if (e.getKey() == e.ENTER) {
						this.doVerify();
					}

				}, this)
				this.form.KSSDJ = data.KSSDJ;
				var win = new Ext.Window({
					layout : "form",
					title : '上级医生授权',
					width : 400,
					autoHeight : true,
					resizable : true,
					modal : true,
					iconCls : 'x-logon-win',
					constrainHeader : true,
					shim : true,
					items : [this.logo, this.form],
					buttonAlign : 'center',
					closable : false,
					buttons : [{
						text : '确定',
						handler : this.doVerify,
						scope : this
					}, {
						text : '取消',
						handler : function() {
							// 删除当前记录
							var r = this.getSelectedRecord();
							MyMessageTip.msg("提示", "抗菌药物【" + r.data.YPMC
									+ "】的授权失败，禁止使用！", true);
							this.store.remove(r)
							if (!this.hasEffectGroupRecord(r)) {
								this.removeFjxm(r);
							}
							this.step = -1;
							this.asyncLoopFunc(r)
							// this.doNewGroup();
							this.verifyWin.hide();
						},
						scope : this
					}]
				})
				// win.render(document.body)
				// win.doLayout();
				win.on("close", this.doCanel, this);
				win.on("show", function() {
					var form = this.form.getForm()
					form.findField("uid").setValue("");
					form.findField("psw").setValue("");
					form.findField("uid").focus(true, 500);
				}, this)
				this.verifyWin = win;
				return win

			},
			doVerify : function() {
				var form = this.form.getForm()
				var uid = form.findField("uid").getValue();
				var psw = form.findField("psw").getValue();
				var kssdj = this.form.KSSDJ || 1;
				if (!uid || !psw) {
					this.verifyWin.setTitle("授权失败:请先输入上级医生工号和密码!");
					return;
				}
				this.form.el.mask("正在验证权限...", "x-mask-loading")
				var res = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "verifyDocInfo",
					body : {
						uid : uid,
						psw : psw,
						kssdj : kssdj
					}
				})
				if (res.code > 200) {
					this.form.el.unmask();
					this.verifyWin.setTitle("授权失败:" + res.msg);
					return;
				}
				var r = this.getSelectedRecord();
				r.set("SQYS", res.json.SQYS);
				if (this.doInputReason()) {
					this.asyncLoopFunc(r);
				}
				this.verifyWin.hide();
				this.form.el.unmask();
			},
			getSkinTest : function(rowItem) {
				var data = rowItem.data;
				if (data.PSPB != 1 && data.PSPB != 2) {
					this.asyncLoopFunc(rowItem);
					return;
				}
				var body = {};
				var psjg = 0;
				var _ctx = this;
				body.BRID = this.exContext.ids.brid;
				body.YPXH = data.YPXH;
				var brxm = this.exContext.empiData.personName
				phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : "getSkinTest",
					body : body
				}, function(code, msg, json) {
					if (code >= 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					if (json.body) {
						psjg = json.body.PSJG;
					}
					// 判断皮试结果
					setTimeout(toShowMsg, 200);

					function toShowMsg() {
						_ctx.grid.stopEditing();
						if (psjg == 1) {
							Ext.Msg.show({
								title : '提示',
								msg : "病人" + brxm + '对药品【' + data.YPMC + '】过敏!',
								modal : true,
								width : 300,
								buttons : {
									yes : '不使用',
									no : '使用',
									cancel : '再做皮试'
								},
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										// 删除当前记录
										var r = this.getSelectedRecord();
										this.store.remove(r)
										if (!this.hasEffectGroupRecord(r)) {
											this.removeFjxm(r);
										}
										this.step = -1;
										this.asyncLoopFunc(rowItem);
									} else if (btn == "no") {
										rowItem.set("PSJG", 1);
										this.asyncLoopFunc(rowItem);
									} else {
										// 插入皮试信息
										rowItem.set("saveSkinTest", '1');
										var tempUniqueId = rowItem.data.YPZH
												? rowItem.data.YPZH
												: rowItem.data.uniqueId;
										if (data.PSPB == 1)
											this.doLoadPssfxm(tempUniqueId);
										this.asyncLoopFunc(rowItem);
									}
								},
								scope : _ctx
							})
						} else {
							var message = '药品【' + data.YPMC + '】为皮试药品，是否要进行皮试?';
							if (psjg == -1) {
								message = brxm + '病人【' + data.YPMC
										+ '】皮试历史结果是阴性，是否继续做皮试?';
							}
							Ext.Msg.show({
								title : '提示',
								msg : message,
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										// 插入皮试信息 modify by
										// yangl 2014.09.01
										// 修改保存顺序
										rowItem.set("saveSkinTest", '1');

										// console.log(rowItem);
										// console.log(rowItem.data.YPZH);
										var tempUniqueId = rowItem.data.YPZH
												? rowItem.data.YPZH
												: rowItem.data.uniqueId;
										if (data.PSPB == 1)
											this.doLoadPssfxm(tempUniqueId);

									} else {
										rowItem.set("PSPB", 0);
										var tempUniqueId = rowItem.data.YPZH
												? rowItem.data.YPZH
												: rowItem.data.uniqueId;
										this.delTotalStorePsfy(false,
												tempUniqueId);
										this.delStorePsfy();
									}
									this.asyncLoopFunc(rowItem);
								},
								scope : _ctx
							})
						}
					}
				}, this);

			},
			// 是否含非原液皮试药品,有则添加皮试费用
			containYypsyp : function(r, uniqueId, addFlag)// r是选中的行，uniqueId是组号
			// addFlag新增标志
			{
				// alert('containYypsyp');
				var selectZh = uniqueId;
				// 不存在组号则是新处方不处理
				// console.log(selectZh);
				if (!selectZh) {
					return;
				}

				// 获取当前list中的所有药品
				var yps = this.store.data.items;
				var hasFyypsyp = false; // 默认不存在非原液皮试皮试费用

				// 循环药品查看同组药品中是否含有非原液皮试药品
				for (var i = 0; i < yps.length; i++) {
					var yp = yps[i].data;

					// console.log(yp);
					if ((yp.uniqueId == selectZh || yp.ypzh == selectZh)) {
						if (yp.PSPB == 1)// 同一组并且皮试判别是1 为非原液皮试药品
						{
							hasFyypsyp = true;
							break;
						}
					}
				}
				if (!hasFyypsyp) {
					return;
				}
				// console.log(selectZh)
				this.doLoadPssfxm(selectZh);
			},
			// 加载皮试收费项目数据
			doLoadPssfxm : function(uniqueId)// 药品组号
			{
				// alert('doLoadPssfxm');
				// clinicManageService
				// console.log(this.serviceId);
				// 如果已经存在皮试费用则不增加
				if (this.containPsfy(uniqueId)) {
					return;
				}
				// alert('不存在皮试费用')
				// 皮试费用的相关信息
				if (this.pssfxmData) {
					var records = [];
					var newobj = {};
					Ext.apply(newobj, this.pssfxmData);
					newobj.uniqueId = !uniqueId ? '' : uniqueId;
					newobj.YPZH = !uniqueId ? '' : uniqueId;

					var rd = new Ext.data.Record(newobj);
					records.push(rd);
					if (!this.containPsfyTotal(uniqueId)) {
						this.exList.totalStore.add(records);
						this.exList.store.add(records);
					}
					// console.log(this.exList);
					return;
				}
				// 去后台请求皮试费用相关信息
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "loadPssfxm",
					body : {
						BRXZ : this.exContext.empiData.BRXZ
					}
				});
				if (r.code > 300) {
					Ext.Msg.alert("提示", r.msg);
					return;
				}
				if (r.json.body) {
					var data = r.json.body;
					var records = [];
					data.uniqueId = uniqueId;
					data.YPZH = uniqueId;

					var rd = new Ext.data.Record(data);
					records.push(rd);

					// console.log(!this.containPsfyTotal());
					if (!this.containPsfyTotal(uniqueId)) {
						this.exList.totalStore.add(records);
						this.exList.store.add(records);
					}
					this.pssfxmData = data
					// console.log(this.exList);
				}
				// console.log("pssfxmData：");
				// console.log(this.pssfxmData);
				// alert('doLoadPssfxm 结束');
			},
			containPsfy : function(uniqueId) {
				// alert('containPsfy')
				var store = this.exList.store;
				var flag = false;
				if (!this.pssfdyxmParam)
					this.pssfdyxmParam = this.loadSystemParams({
						privates : ['PSSFDYXM']
					}).PSSFDYXM;
				store.each(function(record) {
					if ((record.data.YPZH == uniqueId)
							|| (record.data.uniqueId == uniqueId)) {
						if (record.data.YPXH == this.pssfdyxmParam
								|| record.data.YLXH == this.pssfdyxmParam) {
							flag = true;
						}
					}
				}, this)
				return flag;
			},
			containPsfyTotal : function(uniqueId) {
				// alert('containPsfyTotal')
				var store = this.exList.totalStore;
				var flag = false;
				if (!this.pssfdyxmParam)
					this.pssfdyxmParam = this.loadSystemParams({
						privates : ['PSSFDYXM']
					}).PSSFDYXM;
				store.each(function(record) {
					// console.log(record);
					if (record.data.YPZH == uniqueId
							|| record.data.uniqueId == uniqueId) {
						if ((record.data.YPXH == this.pssfdyxmParam || record.data.YLXH == this.pssfdyxmParam)
								&& record.data._opStatus != 'reomve') {
							flag = true;
						}
					}
				}, this)
				return flag;
			},
			getPssfdyxmParam : function() {
				if (!this.pssfdyxmParam)
					this.pssfdyxmParam = this.loadSystemParams({
						privates : ['PSSFDYXM']
					}).PSSFDYXM;
				return this.pssfdyxmParam;
			},
			delTotalStorePsfy : function(flag, uniqueId) {
				// alert('delTotalStorePsfy')
				var store = this.exList.totalStore;
				// console.log(store)
				store.each(function(record) {
					// console.log(record)
					if (record.data.YPZH == uniqueId
							|| record.data.uniqueId == uniqueId) {
						if (record.data.YPXH == this.getPssfdyxmParam()
								|| record.data.YLXH == this.getPssfdyxmParam()) {
							if (flag)
								record.set("_opStatus", "remove");
							else
								store.remove(record);
						}
					}
				}, this);
			},
			delStorePsfy : function() {
				// alert('delStorePsfy')
				var store = this.exList.store;
				// console.log(store)
				store.each(function(record) {
					// console.log(record)
					if (record.data.YPXH == this.getPssfdyxmParam()
							|| record.data.YLXH == this.getPssfdyxmParam()) {
						store.remove(record);
					}
				}, this);
			},
			doNewGroup : function(item, e) {
				this.doInsertAfter(item, e, true);
			},
			doNewClinic : function(item, e) {
				if (this.store.getModifiedRecords().length > 0) {
					for (var i = 0; i < this.store.getCount(); i++) {
						if (this.store.getAt(i).get("YPXH")) {
							if (confirm('处方录入模块数据已经修改，是否保存?')) {
								this.opener.unreload = true;
								if (this.opener.doSave()) {
									this.fireEvent("doNew");
								}
								return;
							} else {
								this.store.rejectChanges();
								break;
							}
						}
					}
				}
				this.fireEvent("doNew");
			},
			doSaveAsPS : function() {
				// this.grid.bbar.dom.align = 'center'
				// alert(this.mainApp.departmentName);
			},
			initHtmlElement: function () {
            var html = '<OBJECT id="DemoActiveX"  type="application/x-itst-activex" style="border:0px;width:0px;height:0px;" clsid="{354C50F1-89F5-4728-B041-76C6F13FFFDE}" codebase="DatamatrixDevice.cab"></OBJECT>'
            var node = document.getElementById("DemoActiveX");
            if (node) {
                node.parentNode.removeChild(node);
            }
            var ele = document.createElement("div");
            ele.setAttribute("width", "0px")
            ele.setAttribute("height", "0px")
            ele.innerHTML = html;
            document.body.appendChild(ele);
        },
			doSave : function() {
				//wy 智能提醒写在这里
				try {
					this.initHtmlElement();
					var obj = document.getElementById("DemoActiveX");
					var ip = obj.GetIpAddressAndHostname().split(",")[0];//支付终端IP
					var storecount = this.store.getCount();
					var list = []
					for (var i = 0; i < storecount; i++) {
						list.push(this.store.getAt(i).data);
					}
					phis.script.rmi.jsonRequest({
						serviceId: "clinicManageService",
						serviceAction: "getSbmByYpxh",
						body: {
							"list": list,
							"KH": this.exContext.empiData.idCard,
							"manageUnit": this.mainApp.deptId,
							"YYKSBM": this.mainApp.departmentId,//科室编码
							"YYYSGH": this.mainApp.uid,//医生工号
							"YSXM": this.mainApp.uname,//医生姓名
							"ip": ip
						}
					}, function (code, msg, json) {
						console.log(code);
						console.log(ip);
					}, this)// jsonRequest
				} catch (e) {
					console.log(e);
				}
				//获取病人id、病人身份证号
				if (this.opener.form.getForm().findField("CFLX").getValue() == 3) {
					for (var k = 0; k < this.store.getCount(); k++) {
						var ypsl = this.store.getAt(k).get("YPSL");
						this.store.getAt(k).set("YCJL", ypsl);
					}
				}
				if (this.exContext.empiData.idCard != ""
						&& this.store.getCount() > 0) {
					// 获取系统参数“是否开启重复用药提醒 QYCFYYTXBZ“
					var params = this.loadSystemParams({
						"privates" : ['QYCFYYTXBZ']
					})
					if (params.QYCFYYTXBZ == "1") {
						var infos = "";
						// 获取系统参数“重复用药判定周期”
						params = this.loadSystemParams({
							"privates" : ['CFYYPDZQ']
						})
						this.mainApp['phisApp'].cfyypdzq = (params.CFYYPDZQ != null
								&& params.CFYYPDZQ != "null" && params.CFYYPDZQ != "")
								? params.CFYYPDZQ
								: "15";						
						// ****************************************************溧水：调用大数据健康档案浏览器接口服务校验 zhaojian 2017-11-15*****************************************************//
						if (this.mainApp['phisApp'].deptId.indexOf("320124") == 0) {
							//根据省编码获取一二级医院已对照的药品名称
							var ypxhs="";
							for (var i = 0; i < this.store.getCount(); i++) {
								// 药品信息，可以有多组药品信息，每组药品信息以英文分号“;”区分，药品信息包括通用名、药品序号、规格、药品产地，互相之间以英文“,”区分，药品信息中为空的属性需要预留位置
								ypxhs += "'" + this.store.getAt(i).get("YPXH")
										+ "',";// 暂未使用系统参数控制使用哪种属性进行配置，后续可扩展
							}
							var body = {};
							body["YPXH"] = ypxhs.substr(0, ypxhs.length - 1);
							var res = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicManageService",
								serviceAction : "getYpFromSbm",
								body : body
							});
							if (!(res.code > 300)) {
								var data = res.json.body;
								if (data.length > 0 && data[0].YPMC != null) {
									infos = (data[0].YPMC + ",").replace(new RegExp(/(,)/g), ",,,;");
								}
							}			
							if (infos == "") {
								for (var i = 0; i < this.store.getCount(); i++) {
									// 药品信息，可以有多组药品信息，每组药品信息以英文分号“;”区分，药品信息包括通用名、药品序号、规格、药品产地，互相之间以英文“,”区分，药品信息中为空的属性需要预留位置
									infos += this.store.getAt(i).get("YPMC")
											+ ',,,;';// 暂未使用系统参数控制使用哪种属性进行配置，后续可扩展
								}
							}
							var start_date = Date.getDateTimeAfter("-"
										+ this.mainApp['phisApp'].cfyypdzq);
							var end_date = Date.getServerDateTime();
							var params_array = [{
								name : "idcard",
								value : this.exContext.empiData.idCard.replace(/(^\s*)|(\s*$)/g, "")
							}, {
								name : "start_date",
								value : start_date
							}, {
								name : "end_date",
								value : end_date
							}, {
								name : "infos",
								value : infos
							}, {
								name : "sys_organ_code",
								value : this.mainApp['phisApp'].deptId
							}, {
								name : "sys_code",
								value : "his"
							}, {
								name : "opeCode",
								value : this.mainApp.uid
							}, {
								name : "opeName",
								value : this.mainApp.uname
							}];
							util.rmi.jsonRequest({
								serviceId : "chis.desedeService",
								schema : "",
								serviceAction : "getDesInfo",
								method : "execute",
								params : JSON.stringify(params_array)
							}, function(code, msg, json) {
								if (msg == "Success") {
									var res = this.getEHRView(json,
											"getRepeatPrescriptionRecord", "1");
								}
							}, this)
						}
						// ****************************************************溧水：使用HIS库校验zhaojian2017-11-16*****************************************************//
						else if (this.mainApp['phisApp'].deptId.indexOf("320111") == 0) {
							for (var i = 0; i < this.store.getCount(); i++) {
								// 药品信息，可以有多组药品信息，每组药品信息以英文分号“;”区分，药品信息包括通用名、药品序号、规格、药品产地，互相之间以英文“,”区分，药品信息中为空的属性需要预留位置
								infos += "'" + this.store.getAt(i).get("YPXH")
										+ "',";// 暂未使用系统参数控制使用哪种属性进行配置，后续可扩展
							}
							
							var body = {};
							body["SFZH"] = this.exContext.empiData.idCard;
							body["BRID"] = this.exContext.empiData.BRID;
							body["YPXH"] = infos.substr(0, infos.length - 1);
							body["YLXH"] = "";
							var res = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicManageService",
								serviceAction : "checkYpsy",
								body : body
							});
							if (!(res.code > 300)) {
								var data = res.json.body;
								var cfyp = "";
								for (var i = 0; i < data.length; i++) {
									cfyp += (i + 1) + ". " + data[i][0] + "，"
											+ data[i][1] + "，" + data[i][2]
											+ "，" + data[i][3] + "<br>";
								}
								if (cfyp != "") {
									MyMessageTip.msg("该病人"+this.mainApp['phisApp'].cfyypdzq+"天内已开过如下药品：", cfyp, true);
								}
							}
						}
					}
				}	
				this.fireEvent("doSave");
			},
			loadData : function() {
				// 添加了phis.
				this.requestData.serviceId = "phis.configLogisticsInventoryControlService";
				this.requestData.serviceAction = "queryCfmx";
				this.clear(); // ** add by yzh , 2010-06-09 **
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
				// ** add by yzh **
				// this.resetButtons();//取消重置按钮
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if ((r.get("YPXH") == null || r.get("YPXH") == "" || (r
							.get("YPXH") == 0))
							&& (r.get("BZMC") == "" || r.get("BZMC") == undefined)) {
						store.remove(r);
					}
				}
			},
			// 判断是否有同组有医嘱
			hasEffectGroupRecord : function(record) {
				var uniqueId = (record.get("YPZH") > 0) ? "YPZH" : "uniqueId";
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					if (r.get(uniqueId) && r.get("YPXH")
							&& r.get(uniqueId) == record.get(uniqueId)) {
						return true;
					}
				}
				// this.doNewGroup();
				return false;
			},
			showColor : function(v, params, data) {
				var YPZH = data.get("YPZH_SHOW") % 2 + 1;
				var PSPB = data.get("PSPB");
				var JYLX = data.get("JYLX");
				switch (YPZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				var msg = data.get("msg");
				if (msg) {
					var msg_str = "";
					if (msg.error_kc) {
						msg_str += msg.error_kc;
					}
					if (msg.error_yrxl) {
						msg_str += msg.error_yrxl;
					}
					if (msg_str.length > 0) {
						return "<img src='"
								+ ClassLoader.appRootOffsetPath
								+ "resources/phis/resources/images/i_error.gif' title='错误："
								+ msg_str + "!'/>";
					}
				}
				if (PSPB > 0) {
					if (data.get("PSJG") == 1) {
						return "<h2 style='color:red'>阳</h2>"
					} else if (data.get("PSJG") == -1) {
						return "<h2 style='color:blue'>阴</h2>"
					}
					return "<a onclick='ctx_cf.openSkinTestWin("
							+ Ext.encode(data.data)
							+ ")'><h2 style='color:red'>皮</h2></a>";
				}
				if (JYLX == 1) {// 原来判断为0，因为药品基本信息维护有修改，去除了国家基本药物，统一用基药类型的1来判别
					return "<h2 style='color:red;font-size:14px;font-weight:bold'>非</h2>";
				}
				return "";
			},
			openSkinTestWin : function(data) {
				if (data.CFSB) {
					var tab = this.opener.tab.getActiveTab();
					if (data.PSPB == 2 && !tab.fphm)
						return;
					var module = this.createModule("skintestWin",
							this.refSkinTestForm);
					module.exContext = this.exContext;
					module.on("doSave", function() {
						this.loadData();
					}, this);
					this.skinTestModule = module;
					this.skinTestModule.sbxh = data.SBXH;
					this.skinTestModule.ypxh = data.YPXH;
					this.skinTestModule.cfsb = data.CFSB;
					var win = module.getWin();
					win.show();
				}
			},
			totalYYZL : function(v, params, record) {

				// return v == null
				// ? '0'
				// : ('<span style="font-size:14px;color:black;">总计:&#160;'
				// + v + '</span>');
			},
			totalYPSL : function(v, params, data) {
				return v == null
						? '0'
						: ('<span style="font-size:14px;color:black;">药品记录:&#160;'
								+ v + '</span>');
			},
			doClose : function() {
				if (this.opener.win) {
					this.opener.win.hide();
				} else {
					this.opener.opener.cardWin.hide();
				}
			},
			doPrint : function() {
				var mzhm = null;
				if(typeof this.exContext.empiData == "undefined" && typeof CF_MZHM != "undefined"){
					mzhm = CF_MZHM;
				}else{
					mzhm = this.exContext.empiData.MZHM;
				}
				if (this.exContext.clinicType == 3) {
					var module = this.createModule("cfzyprint",
							this.refPrescriptionChinePrint);
					var tab = this.opener.tab.activeTab;
					if (tab && tab.cfsb) {
						module.cfsb = tab.cfsb;
						module.mzhm = mzhm;
						module.initPanel();
						module.doPrint();
					} else {
						MyMessageTip.msg("提示", "打印失败：无效的处方信息!", true);
					}
				} else {
					var module = this.createModule("cfprint",
							this.refPrescriptionPrint)
					var tab = this.opener.tab.activeTab;
					if (tab && tab.cfsb) {
						module.cfsb = tab.cfsb;
						module.mzhm = mzhm;
						
						//【begin】2018-12-03 zhaojian 增加处方打印生成二维码必须字段						
						module.kfrq = this.opener.midiModules.prescriptionEntryForm.data.KFRQ;
						if(typeof this.exContext.empiData != "undefined"){
							module.sfzh = this.exContext.empiData.idCard;
						}
						
						//【end】2018-12-03 zhaojian 增加处方打印生成二维码必须字段
						module.initPanel();
						module.doPrint();
					} else {
						MyMessageTip.msg("提示", "打印失败：无效的处方信息!", true);
					}
				}
				// 此处写死提示是否需要打印注射卡
				// 改进，根据取到的gytj去表zy_ypyf查找KPDY字段，1为需要打印，0不需要
				// modify by zhaojian 2017-06-01 去除该提示
				/*
				 * if (this.store.getCount() > 0) { for (var k = 0; k <
				 * this.store.getCount(); k++) { var kpdy =
				 * this.store.getAt(k).get("KPDY"); if (kpdy == 1) { if
				 * (confirm("有需要注射药品，需要打印注射卡吗？")) { this.doInjectionCardPrint(); }
				 * break; } } }
				 */
			},
			doInjectionCardPrint : function() {
				if (this.exContext.clinicType == 1) {
					var module = this.createModule("injectionCardprint",
							this.refInjectionCardPrint)
					var tab = this.opener.tab.activeTab;
					if (tab && tab.cfsb) {
						module.cfsb = tab.cfsb;
						module.initPanel();
						module.doPrint();
					} else {
						MyMessageTip.msg("提示", "打印失败：无效的注射卡信息!", true);
					}
				}
			},
			onRowClick : function(grid, index, e) {
				// console.log('onRowClick');
				if (!this.exList)
					return;
				var r = this.getSelectedRecord();
				if (!r)
					return;
				var records = [];
				var cacheStore = this.exList.totalStore;
				this.exList.store.removeAll();
				if (!cacheStore.getCount)
					return;
				for (var i = 0; i < cacheStore.getCount(); i++) {
					if (r.get("YPZH") > 0) {
						if (cacheStore.getAt(i).get("YPZH") == r.get("YPZH")
								&& cacheStore.getAt(i).get("_opStatus") != "remove") {
							// 判断是否存在YLSL_YS原始数量
							// if (r.get("MRCS") && r.get("YYTS")
							// && !cacheStore.getAt(i).get("YLSL_YS")) {
							// cacheStore.getAt(i).set(
							// "YLSL_YS",
							// parseInt(cacheStore.getAt(i)
							// .get("YLSL")
							// / (r.get("MRCS") * r
							// .get("YYTS"))))
							// }
							records.push(cacheStore.getAt(i));
						}
					} else {
						if (r.get("uniqueId")
								&& cacheStore.getAt(i).get("uniqueId") == r
										.get("uniqueId")) {
							records.push(cacheStore.getAt(i));
						}
					}
				}
				this.exList.store.add(records);

				// 以下为判断是否有非原液皮试药品有加上皮试费用，没有就不加
				// 1.刚进入处方录入界面，触发rowclick事件
				// 如果是刚就诊的病人处方单为空，则选中的当前行中ypzh不存在,
				// uniqueid为undefiend，tempUniqueId为undefined什么都不加
				// 2.如果是已有保存的处方明细,则选中的当前行中ypzh存在,upiqueid为""，则tempUniqueId为药品组号，如果该组存在皮试费用则不加
				// 3.如果是没有保存的处方明细,则选中的当前行中ypzh不存在,upiqueid为""，则tempUniqueId临时的药品组号，如果该组存在皮试费用则不加
				// console.log(r);
				var tempUniqueId = r.get("YPZH") > 0 ? r.get("YPZH") : r
						.get("uniqueId");
				this.containYypsyp(r, tempUniqueId);
			},
			// beforeSearchQuery : function() {
			// var r = this.getSelectedRecord();
			// if (!r)
			// return false;
			// // 抗菌药物不允许修改
			// if (this.exContext.systemParams.QYKJYWGL == 1
			// && r.get("KSBZ") == 1) {
			// MyMessageTip.msg("提示", "当前处方明细为抗菌药物，不能直接修改，若要修改,需删除后重新录入!",
			// true);
			// return false;
			// }
			// var s = this.remoteDic.getValue();
			// if (s == null || s == "" || s.length == 0)
			// return true;
			// if (s.substr(0, 1) == '*') {
			// return false;
			// } else {
			// if (r.get("BZMC") && r.get("BZMC").length > 0) {
			// return false;
			// }
			//
			// return true;
			// }
			// },
			// 模糊查询前,传是否自备药参数
			beforeSearchQuery : function() {
				var r = this.getSelectedRecord();
				if (!r)
					return false;
				// 抗菌药物不允许修改
				if (this.exContext.systemParams.QYKJYWGL == 1
						&& r.get("KSBZ") == 1) {
					MyMessageTip.msg("提示", "当前处方明细为抗菌药物，不能直接修改，若要修改,需删除后重新录入!",
							true);
					return false;
				}
				var s = this.remoteDic.getValue();
				if (s.substr(0, 1) == '*') {
					return false;
				}
				this.remoteDic.lastQuery = "";
				var r = this.getSelectedRecord();
				if (!this.remoteDicStore.baseParams) {
					this.remoteDicStore.baseParams = {};
				}
				if (r.get("ZFYP")) {
					this.remoteDicStore.baseParams.ZFYP = 1;
				} else {
					this.remoteDicStore.baseParams.ZFYP = 0;
				}
				return true;
			},
			// 在父类的基础上加了一个ZSSF（注射收费）字段，
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
					root : 'mds',
					totalProperty : 'count',
					id : 'mdssearch'
				}, [{
					name : 'numKey'
				}, {
					name : 'YPXH'
				}, {
					name : 'YPMC'
				}, {
					name : 'YFGG'
				}, {
					name : 'YPDW'
				}, {
					name : 'YPSL'
				}, {
					name : 'JLDW'
				}, {
					name : 'YPJL'
				}, {
					name : 'YCJL'
				}, {
					name : 'PSPB'
				},		// 判断是否皮试药品
						{
							name : 'YFBZ'
						}, {
							name : 'GYFF'
						},// 药品用法
						{
							name : 'LSJG'
						}, {
							name : 'YPCD'
						}, {
							name : 'CDMC'
						}, {
							name : 'TYPE'
						}, {
							name : 'TSYP'
						}, {
							name : 'YFDW'
						}, {
							name : 'YBFL'
						}, {
							name : 'YBFL_text'
						}, {
							name : 'GYFF_text'
						}, {
							name : 'JYLX'
						}, {
							name : 'KCSL'
						}, {
							name : 'FYGB'
						},// 抗菌药物
						{
							name : 'KSBZ'
						}, {
							name : 'YQSYFS'
						}, {
							name : 'YCYL'
						}, {
							name : 'KSSDJ'
						}, {
							name : 'SFSP'
						}, {
							name : 'ZFYP'
						}, {
							name : 'YYZBM'
						}]);
			},
			/** 处方复制功能* */
			doCopyClinic : function() {
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					// 特殊药品后台查询判别
					if (i == 0
							&& (this.tscfPb == "" || this.tscfPb == null || this.tscfPb == "null")) {
						var tsypHx = r.get("YPXH");
						if (tsypHx != null && tsypHx != "") {
							this.getTsypPb(tsypHx);
						} else {
							this.tsypBs = null;
						}
					}
				}
				if (this.perscriptionCopyWin) {
					var module = this.midiModules['ClinicPrescriptionCopy'];
					module.initData(this.exContext.ids.brid,
							this.exContext.empiData.BRXZ,
							this.exContext.clinicType, this.tsypBs);
					module.reloadPrescription();
				} else {
					// 获取病人ID
					var brid = this.exContext.ids.brid;
					var module = this.createModule("ClinicPrescriptionCopy",
							this.rePrescriptionCopy);
					module.opener = this;
					module.initData(brid, this.exContext.empiData.BRXZ,
							this.exContext.clinicType, this.tsypBs);
					this.perscriptionCopyWin = module.getWin();
					// this.perscriptionCopyWin.add(module.initPanel());
					this.perscriptionCopyWin.setWidth(1200);
					this.perscriptionCopyWin.setHeight(700);
					this.perscriptionCopyWin.maximize();
				}
				this.perscriptionCopyWin.show();
			},
			/**
			 * 处方拷贝
			 */
			perscriptionCopy : function(json) {
				this.perscriptionCopyWin.hide();
				this.opener.perscriptionCopy(json, 'clinicPersonalSet');
			},
			/**
			 * 特殊药品判别后台获取参数
			 */
			getTsypPb : function(YPXH) {
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "getTsypPb",
					body : YPXH
				});
				if (r.code > 300) {
					this.tscfPb = false;
					this.tsypBs = "";
				} else {
					if (!r.json.body) {
						this.tscfPb = false;
						this.tsypBs = "";
					}
					var data = r.json.body;
				}
				if (data == "" || data == null) {
					this.tscfPb = false;
					this.tsypBs = "";
				} else {
					if (data == 1 || data == 7 || data == 8) {
						this.tscfPb = true;
						this.tsypBs = data;
					} else {
						this.tscfPb = false;
						this.tsypBs = "";
					}
					// this.tsypCx=true;//特殊药品查询
				}
			},

			onStoreLoadData : function(store, records, ops) {
				this.store.each(function(r) {
					r.set("ZFYP", r.data.ZFYP == 1 ? true : false);
				}, this);
				this.grid.store.commitChanges();
				this.fireEvent("loadData", store)
				if (records.length == 0) {
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
			},
			getCM : function(items) {
				var cm = []
				var fm = Ext.form
				var ac = util.Accredit;
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				if (this.mutiSelect) {
					cm.push(this.sm);
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2) || it.noList
							|| it.hidden || !ac.canRead(it.acValue)) {
						continue
					}
					// if(it.length < 80){it.length = 80}//
					// modify by yangl
					var width = parseInt(it.width
							|| (it.length < 80 ? 80 : it.length) || 80)
					var c = {
						id : it.id,
						header : it.alias,
						width : width,
						sortable : this.sortable,
						dataIndex : it.id,
						schemaItem : it
					}
					/** ******************** */
					if (it.renderer) {
						var func
						func = eval("this." + it.renderer)
						if (typeof func == 'function') {
							c.renderer = func
						}
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
						if (it.summaryRenderer) {
							var func = eval("this." + it.summaryRenderer)
							if (typeof func == 'function') {
								c.summaryRenderer = func
							}
						}
					}
					// add by yangl,modify simple code Generation methods
					if (it.codeType) {
						if (!this.CodeFieldSet)
							this.CodeFieldSet = [];
						this.CodeFieldSet.push([it.target, it.codeType, it.id]);
					}
					var editable = true;

					if ((it.pkey && it.generator == 'auto') || it.fixed) {
						editable = false
					}
					if (it.evalOnServer && ac.canRead(it.acValue)) {
						editable = false
					}
					var notNull = !(it['not-null'] == 'true')

					var editor = null;
					var dic = it.dic
					if (it.properties && it.properties.mode == "remote") {
						// 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
						editor = this.createRemoteDicField(it);
					} else if (dic) {
						dic.src = this.entryName + "." + it.id
						dic.defaultValue = it.defaultValue
						dic.width = width
						if (dic.fields) {
							if (typeof(dic.fields) == 'string') {
								var fieldsArray = dic.fields.split(",")
								dic.fields = fieldsArray;
							}
						}
						if (dic.render == "Radio" || dic.render == "Checkbox") {
							dic.render = ""
						}
						var _ctx = this
						c.isDic = true
						c.renderer = function(v, params, record, r, c, store) {
							var cm = _ctx.grid.getColumnModel()
							var f = cm.getDataIndex(c)
							return record.get(f + "_text")
						}
						if (editable) {
							editor = this.createDicField(dic)
							editor.isDic = true
							var _ctx = this
							c.isDic = true
						}
					} else {
						if (!editable) {
							if (it.type != "string" && it.type != "text"
									&& it.type != "date"
									&& it.type != "datetime") {
								c.align = "right";
								c.css = "color:#00AA00;font-weight:bold;"
								c.precision = it.precision;
								c.nullToValue = it.nullToValue;
								if (!c.renderer) {
									c.renderer = function(value, metaData, r,
											row, col, store) {
										if (value == null && this.nullToValue) {
											value = parseFloat(this.nullToValue)
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											try {
												r.set(this.id, retValue);
											} catch (e) {
												// 防止新增行报错
											}
											return retValue;
										}
										if (value != null) {
											value = parseFloat(value);
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											return retValue;
										}
									}
								}
							}
							cm.push(c);
							continue;
						}
						editor = new fm.TextField({
							allowBlank : notNull
						});
						var fm = Ext.form;
						switch (it.type) {
							// modify by liyunt
							case 'string' :
							case 'text' :
								var cfg = {
									allowBlank : notNull,
									maxLength : it.length
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								if (it.inputType) {
									cfg.inputType = it.inputType
								}
								editor = new fm.TextField(cfg)
								break;
							case 'date' :
								var cfg = {
									allowBlank : notNull,
									emptyText : "请选择日期",
									format : 'Y-m-d'
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new fm.DateField(cfg)
								break;
							case 'datetime' :
							case 'timestamp' :
							case 'datetimefield' :
								var cfg = {
									allowBlank : notNull,
									emptyText : "请选择日期"
								}
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new phis.script.widgets.DateTimeField(cfg)
								break;
							case 'double' :
							case 'bigDecimal' :
							case 'int' :
								if (!it.dic) {
									c.css = "color:#00AA00;font-weight:bold;"
									c.align = "right"
									if (it.type == 'double'
											|| it.type == 'bigDecimal') {
										c.precision = it.precision;
										c.nullToValue = it.nullToValue;
										c.renderer = function(value, metaData,
												r, row, col, store) {
											if (value == null
													&& this.nullToValue) {
												value = parseFloat(this.nullToValue)
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												try {
													r.set(this.id, retValue);
												} catch (e) {
													// 防止新增行报错
												}
												return retValue;
											}
											if (value && !isNaN(value)) {
												value = parseFloat(value);
												var retValue = this.precision
														? value
																.toFixed(this.precision)
														: value;
												return retValue;
											}
											return value;
										}
									}
								}
								var cfg = {}
								if (it.type == 'int') {
									cfg.decimalPrecision = 0;
									cfg.allowDecimals = false
								} else {
									cfg.decimalPrecision = it.precision || 2;
								}
								if (it.min) {
									cfg.minValue = it.min;
								} else {
									cfg.minValue = 0;
								}
								if (it.max) {
									cfg.maxValue = it.max;
								}
								cfg.allowBlank = notNull
								if (this.selectOnFocus) {
									cfg.selectOnFocus = true;
								}
								editor = new fm.NumberField(cfg)
								if (it.properties.xtype == "checkBox") {
									c.xtype = 'checkcolumn';
									editor = new Ext.ux.grid.CheckColumn();
									// editor.on("beforeedit",this.beforeCheckEdit,this)
								}
								break;
						}
					}
					if (editor) {
						editor.enableKeyEvents = true;
						editor.on("keydown", function(f, e) {
							var keyCode = e.getKey();
							if (e.ctrlKey == true) {
								// ctrl+c ctrl+v 等系统快捷键不屏蔽
								// 86, 90, 88, 67, 65
								if (keyCode == 86 || keyCode == 90
										|| keyCode == 88 || keyCode == 67
										|| keyCode == 65) {
									return true;
								}
							}
							if (e.ctrlKey || e.altKey
									|| (keyCode >= 112 && keyCode <= 123)) {
								e.preventDefault();// editor需要额外处理全键盘事件
							}

						}, this)
					}
					c.editor = editor;
					cm.push(c);
				}
				return cm;
			},
			afterCheckedit : function(id, column, value) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				this.grid.startEditing(row, 3);
			},
			getRowid : function(key) {// 特殊药品检测第一个药品
				if (key == "curPB") {
					var store = this.grid.getStore();
					var n = store.getCount()
					var r = store.getAt(0)
					// 特殊药品后台查询判别
					var tsypHx = r.get("YPXH");
					if (tsypHx != null && tsypHx != "") {
						this.getTsypPb(tsypHx);
					} else {
						return true;
					}
				} else {
					var store = this.grid.getStore();
					var n = store.getCount()
					var r = store.getAt(0)
					// 特殊药品后台查询判别
					var tsypHx = r.get("YPXH");
					if (tsypHx != null && tsypHx != "") {
						this.getTsypPb(tsypHx);
					} else {
						this.tscfPb = null;
					}
				}

			},
			onContextMenu : function(grid, rowIndex, e) {
				if (e) {
					e.stopEvent()
				}
				if (this.disableContextMenu) {
					return
				}
				// grid.getSelectionModel().selectRow(rowIndex)
				var r = this.store.getAt(rowIndex);
				if (!r.get("YPXH") || r.get("YPXH") == null
						|| r.get("YPXH") == "") {
					return;
				}
				/*** ************begin 增加合理用药接口 zhaojian 2017-12-18*****************/
				// 获取系统参数“启用合理用药标志 QYHLYYBZ“
				var params = this.loadSystemParams({
					"privates" : ['QYHLYYBZ']
				})
				if (params.QYHLYYBZ != "1") {
					var module = this.createModule("YSZHLYYModule",
							this.YSZHLYYModuleRef);
					module.name = "药品信息调阅";
					module.yszhlyy = true;
					module.op = "read";
					module.initDataId = r.get("YPXH")
					var win = module.getWin();
					win.add(module.initPanel());
					win.show();
					win.center();
					module.loadData("read");
				}
				else {					
				var cmenu = this.midiMenus['ContextMenu'];
				//if (!cmenu || cmenu.items.length==0) {
					var items = [];
					items.push({
								cmd : "1",
								iconCls : "antibiotics",
								//handler : this.doYpzyxx,
								handler : function(){
									this.MCInit();
									this.HisQueryData(r);
									MDC_DoRefDrug(51);
								},
								text : "药品重要信息",
								scope : this
							});
					items.push({
								cmd : "2",
								iconCls : "read",
								//handler : this.doYpsms,
								handler : function(){
									this.MCInit();
									this.HisQueryData(r);
									MDC_DoRefDrug(11);
								},
								text : "药品说明书",
								scope : this
							});
					cmenu = new Ext.menu.Menu({
								items : items
							})
					//}
					this.midiMenus['ContextMenu'] = cmenu;
					cmenu.showAt([e.getPageX() + 5, e.getPageY() + 5]);
				}
				/*************end**************/
			},
			/**************begin 增加合理用药接口 zhaojian 2017-12-18*****************/		
			MCInit : function() {
	           var pass = new Params_MC_PASSclient_In();
	            pass.HospID = this.mainApp["phis"].phisApp.deptId;   //医院编码           
	            pass.UserID =this.mainApp['phis'].phisApp.uid;
	            pass.UserName = this.mainApp['phis'].phisApp.uname;
	            pass.DeptID = this.mainApp['phis'].departmentId;
	            pass.DeptName =  this.mainApp['phis'].departmentName;
	            pass.CheckMode = MC_global_CheckMode;
	            MCPASSclient = pass;
	            
	            /*var pass = new Params_MC_PASSclient_In();
	            pass.HospID = "1609PA";   //医院编码           
	            pass.UserID = "";
	            pass.UserName = "";
	            pass.DeptID = "";
	            pass.DeptName = "";
	            pass.CheckMode = "mz";
	            MCPASSclient = pass;*/
     	   },
			HisQueryData : function(record) {
	            var drug = new Params_MC_queryDrug_In();
	            drug.ReferenceCode = record.get("YPXH")+"_"+record.get("YPCD"); //药品编码
	            drug.CodeName = record.get("YPMC");  //药品名称 
	            MC_global_queryDrug = drug;
	            
				/*if (record.get("YPMC") == "布洛芬片(糖衣)") {
					var drug = new Params_MC_queryDrug_In();
					drug.ReferenceCode = "54930"; //药品编码
					drug.CodeName = "布洛芬片"; //药品名称           
					MC_global_queryDrug = drug;
				}
				else if (record.get("YPMC") == "氨茶碱注射液") {
					var drug = new Params_MC_queryDrug_In();
					drug.ReferenceCode = "117281"; //药品编码
					drug.CodeName = "氨茶碱注射液"; //药品名称           
					MC_global_queryDrug = drug;
				}
				else {
					var drug = new Params_MC_queryDrug_In();
					drug.ReferenceCode = record.get("YPXH") + "_"
							+ record.get("YPCD"); // 药品编码
					drug.CodeName = record.get("YPMC"); // 药品名称
					MC_global_queryDrug = drug;
				}*/
      		}
			/*************end**************/

		});