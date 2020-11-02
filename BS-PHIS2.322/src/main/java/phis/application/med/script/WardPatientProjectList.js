$package("phis.application.med.script");
$import("phis.script.EditorList", "phis.script.widgets.MyMessageTip");
phis.application.med.script.WardPatientProjectList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.mutiSelect = true;// 添加勾选框
	cfg.modal = true;
	// Ext.apply(cfg, phis.script.EditorList);
	phis.application.med.script.WardPatientProjectList.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("beforeclose", this.beforeClose, this);
	this.on("hide", this.onWinClose, this);// add by yangl 关闭时抛出事件
}, Ext.extend(phis.application.med.script.WardPatientProjectList,
		phis.script.EditorList, {
			expansion : function(cfg) {
				var tbar = cfg.tbar;
				this.lable = new Ext.form.Label({
							id : 'label',
							text : '',
							style : {
								'font-size' : '15',
								'color' : 'red'
							}
						});
				tbar.push(['->', this.lable]);
			},
			initPanel : function(schema) {
				var grids = phis.application.med.script.WardPatientProjectList.superclass.initPanel
						.call(this, schema);
				this.editor = false;
				this.on("afterCellEdit", this.afterGridEdit, this);
				return grids;
			},
			onWinShow : function() {
				var fhbz = this.getFHBZ();
				var cnd = [
						'and',
						[
								'and',
								['eq', ['$', 'b.CYPB'], ['d', 0]],
								['eq', ['$', 'a.BRBQ'],
										['s', this.mainApp['phis'].wardId]],
								['or', ['eq', ['$', 'a.JFBZ'], ['d', 1]],
										['eq', ['$', 'a.JFBZ'], ['d', 2]],
										['eq', ['$', 'a.JFBZ'], ['d', 9]]],
								['ge', ['$', 'a.XMLX'], ['d', '3']],
								['eq', ['$', 'a.SYBZ'], ['d', 0]],
								['or', ['eq', ['$', 'a.LSBZ'], ['d', 0]],
										['eq', ['$', 'a.LSBZ'], ['d', 2]]],
								['eq', ['$', 'a.YZPB'], ['d', 0]],
								[
										'or',
										['eq', ['$', 'a.YSBZ'], ['d', 0]],
										[
												'and',
												['eq', ['$', 'a.YSBZ'],
														['d', 1]],
												['eq', ['$', 'a.YSTJ'],
														['d', 1]]]],
								[
										'or',
										[
												'le',
												['$',
														"str(a.QRSJ,'yyyy-mm-dd hh24:mi:ss')"],
												[
														's',
														Date.getServerDate()
																+ ' 00:00:00']],
										['isNull', ['$', 'a.QRSJ']]],
								['eq', ['$', 'a.ZFBZ'], ['d', 0]],
								['eq', ['$', 'a.ZFPB'], ['d', 0]],//zhaojian 2017-09-28 过滤检验项目
								['ne', ['$', 'a.FHGH'], ['d', null]],
								['eq', ['$', 'a.JGID'],
										['s', this.mainApp['phisApp'].deptId]],
								['or', ['eq', ['$', fhbz], ['d', 0]],
										['eq', ['$', 'a.FHBZ'], ['d', 1]]]],
						['eq', ['$', 'b.ZYH'], ['d', this.initDataId]]];
				// modified by gaof 2014-12-31 增加yplx!=3的条件
				var extraCnd = ['eq', ['$', 'a.YPLX'], ['d', '0']];
				this.requestData.cnd = ['and', cnd, extraCnd];
//				this.requestData.cnd = cnd;
				this.requestData.serviceAction = this.serviceAction;
				this.refresh();
			},
			/**
			 * 初始化js类时自动调用
			 * 
			 * @param items
			 * @returns
			 */
			getCM : function(items) {
				var cm = phis.application.med.script.WardPatientProjectList.superclass.getCM
						.call(this, items);
				var sm = this.sm;
				sm.on("rowselect", function(sm, rowIndex, record) {
							this.addCommonProject(record, rowIndex);
						}, this);
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							this.delCommonProject(record);
						}, this);
				return cm;
			},
			/**
			 * 添加同组提交项目
			 * 
			 * @param record
			 */
			addCommonProject : function(record, rowIndex) {
				var selectYzzh = record.data.YZZH;
				var tempYzzh;
				var allItems = this.store.data.items;
				var tmp;
				for (var i = 0; i < allItems.length; i++) {
					tmp = allItems[i];
					tempYzzh = tmp.data.YZZH;
					if (selectYzzh == tempYzzh) {
						if (!this.grid.getSelectionModel().isSelected(i)) {
							this.grid.getSelectionModel().selectRow(i, true);
						}
					}
				}
			},
			/**
			 * 取消选中时触发取消其它同组选择
			 * 
			 * @param record
			 */
			delCommonProject : function(record) {
				var selectYzzh = record.data.YZZH;
				var tempYzzh;
				var allItems = this.store.data.items;
				var tmp;
				for (var i = 0; i < allItems.length; i++) {
					tmp = allItems[i];
					tempYzzh = tmp.data.YZZH;
					if (selectYzzh == tempYzzh) {// 是否为同组
						if (this.grid.getSelectionModel().isSelected(i)) {
							this.grid.getSelectionModel().deselectRow(i);// 取消同组行
						}
					}
				}
				// }
			},
			doRefresh : function() {
				this.onWinShow();
			},
			doClose : function() {
				this.win.hide();
			},
			beforeClose : function() {
				if (this.editor) {
					if (!confirm('数据已修改,是否直接关闭?')) {
						return false;
					}
				}
				return true;
			},
			/**
			 * 保存按钮
			 */
			doSave : function() {
				if (this.grid.activeEditor != null) {
					this.grid.activeEditor.completeEdit();
				}
				var mod = this.grid.getStore().getModifiedRecords();
				var modified = this.grid.store.modified;
				var json = [];
				Ext.each(modified, function(item) {
							json.push(item.data);
						});
				if (json.length == 0) {
					Ext.Msg.alert("提示", "请选择需要修改执行科室的项目!");
					return;
				}
				this.miniJsonRequest2Return(this.listServiceId, "save", json,
						"保存成功!");
				// 提交全部有变化的Record集
				this.grid.getStore().commitChanges();
				// 刷新
				this.doRefresh();
				this.editor = false;
			},
			/**
			 * 确认按钮
			 */
			doConfirm : function() {
				if (this.grid.activeEditor != null) {
					this.grid.activeEditor.completeEdit();
				}
				var json = [];
				if (this.editor) {
					// Ext.Msg.alert("提示", "数据已修改,请先保存!");
					// return;
					var modified = this.grid.store.modified;
					Ext.each(modified, function(item) {
								json.push(item.data);
							});
					if (json.length == 0) {
						MyMessageTip.msg("提示", "请选择需要修改执行科室的项目!", true);
						return;
					}
					var result = this.miniJsonRequest2Return(
							this.listServiceId, "save", json);
					if (result.code != 200) {
						MyMessageTip.msg("提示", "保存执行科室失败!", true);
						return;
					}
					// 提交全部有变化的Record集
					this.grid.getStore().commitChanges();
					this.editor = false;
				}
				var records = [];
				var selects = this.grid.getSelectionModel().getSelections();
				for (var i = 0; i < selects.length; i++) {
					records.push(selects[i]);
				}
				var leftJson = [];
				var rightJson = [];
				if (records.length == 0) {
					MyMessageTip.msg("提示", "未选择确认项目!", true);
					return;
				}
				var flag = true;
				// 选中列表放入rightJson中
				for (var i = 0; i < records.length; i++) {
					flag = true;
					if (json && json.length > 0) {
						for (var j = 0; j < json.length; j++) {
							if (selects[i].data.JLXH == json[j].JLXH) {
								rightJson.push(json[j]);
								flag = false;
								break;
							}
						}
						if (flag) {
							rightJson.push(records[i].json);
						}
					} else {
						rightJson.push(records[i].json);
					}
				}
				// 数据提交
				var result = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.listServiceId,
					serviceAction : "saveDetermine",
					leftJson : leftJson,// 左边列表
					rightJson : rightJson,// 右边列表
					commitType : 'patientCommitTab',// 提交类型
					jgid : this.mainApp['phisApp'].deptId
						// 机构ID
					});
				// add gejj 增加判断RES_MESSAGE条件，表明有预交款不足
				if (result.json.RES_MESSAGE) {
					MyMessageTip.msg("提示", result.json.RES_MESSAGE, true);
					// 刷新
					this.doRefresh();
				} else if (result.code == 200) {
					Ext.Msg.alert("提示", "确认成功!");
					// 刷新
					this.doRefresh();
				} else {
					MyMessageTip.msg("提示", result.msg, true);
					// 刷新
					this.doRefresh();
					// Ext.Msg.alert("提示", result.msg);
				}
			},
			onWinClose : function() {
				this.fireEvent("doSave");
			},
			getFHBZ : function() {
				var fhbzTmp = "1";
				// 经张伟确认：输入科室是指当前登录病区
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "wardProjectService",
							serviceAction : "getFHBZ",
							body : this.mainApp['phis'].wardId,
							ZYH : this.initDataId,
							type : 'personal'// 个人医技
						});
				if (result.code == 200) {
					fhbzTmp = result.msg;
					if (fhbzTmp == 1 && result.json.body) {
						this.lable.setText('发现' + result.json.body
								+ '个未复核医嘱，请复核');
					} else {
						this.lable.setText('');
					}
				} else {
					Ext.Msg.alert('状态:', result.msg);
				}
				return fhbzTmp;
			},
			/**
			 * JSON同步提交请求
			 * 
			 * @param sId
			 *            配置的ServiceID
			 * @param sAction
			 *            请求的方法名(例如在Service中的方法名为doQuery，此时应传入query)
			 * @param parameter
			 *            需要发送给服务器的参数值，可在req.get("body")获取
			 * @param showMsg
			 *            请求成功后弹出的提示框 诺不传该参数则不弹出提示框 诺传入参数，成功：弹出showMsg;
			 *            失败：弹出"execute fail"消息框
			 * @returns
			 */
			miniJsonRequest2Return : function(sId, sAction, parameter, showMsg) {
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : sId,
							serviceAction : sAction,
							body : parameter
						});
				if (showMsg != null) {
					if (result.code == 200) {
						Ext.Msg.alert('状态:', showMsg);
					} else {
						Ext.Msg.alert('状态:', "执行失败!");
					}
				}
				return result;
			},
			afterGridEdit : function(it, record, field, v) {
				if (it.id == "ZXKS") {
					this.editor = true;
				}
			},
			/**
			 * 重置数据
			 */
			resetData : function() {
				this.editor = false;
			}

		});
