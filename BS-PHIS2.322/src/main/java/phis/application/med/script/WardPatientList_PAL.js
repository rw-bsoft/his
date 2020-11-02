$package("phis.application.med.script");

$import("phis.script.SelectList");
phis.application.med.script.WardPatientList_PAL = function(cfg) {
	this.serverParams = {
		serviceAction : cfg.serviceAction
	};
	phis.application.med.script.WardPatientList_PAL.superclass.constructor.apply(this,
			[cfg]);

},

Ext.extend(phis.application.med.script.WardPatientList_PAL, phis.script.SelectList, {
			init : function() {
				// 定义数据加载时的数据集
				this.dataList = [];
				// 定义右边列表选取框临时值
				this.rightTemList = [];
				//定义在右边先加载时会将数据保存到this.rightTemList中，true是右边先加载，false为左边先加载
				this.rightFlag = false;

				phis.application.med.script.WardPatientList_PAL.superclass.init
						.call(this);
			},
			initPanel : function(schema) {
				var grids = phis.application.med.script.WardPatientList_PAL.superclass.initPanel
						.call(this, schema);
				this.grid.getBottomToolbar().hide();// 隐藏分页bbar
				return grids;
			},
			/**
			 * 左边选择更改
			 */
			changeSelect : function() {
				var selectList = [];
				var allItems = this.store.data.items;
				var tmp;
				for (var i = 0; i < allItems.length; i++) {
					tmp = allItems[i];
					if (this.grid.getSelectionModel().isSelected(i)) {
						selectList.push(tmp);
					}
				}
				return selectList;
				// this.fireEvent("changeSelect",selectList);
			},
			onStoreLoadData : function(store, records, ops) {
				phis.application.med.script.WardPatientList_PAL.superclass.onStoreLoadData.call(this,
						store, records, ops);
				if (records.length == 0 || !this.selects || !this.mutiSelect) {
					return
				}
				/*
				 * var selRecords = [] for (var id in this.selects) { var r =
				 * store.getById(id) if (r) { selRecords.push(r) } }
				 * this.grid.getSelectionModel().selectRecords(selRecords)
				 */
				// this.initSelect();
				if (this.rightFlag) {
					this.compareData(this.rightTemList);
					this.rightTemList = [];
				}
				this.rightFlag = false;
			},
			/**
			 * 初始化js类时自动调用
			 * 
			 * @param items
			 * @returns
			 */
			getCM : function(items) {
				var cm = phis.application.med.script.WardPatientList_PAL.superclass.getCM
						.call(this, items);
				sm = this.sm;
				sm.on("rowselect", function(sm, rowIndex, record) {
							var resData = phis.script.rmi.miniJsonRequestSync({
										serviceId : "doctorAdviceExecuteService",
										serviceAction : "queryFHSFXM",
										zyh : record.data.ZYH
									});
							var code = resData.code;
							var msg = resData.msg;
							var json = resData.json;
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return false;
							}
							if (json.body == 1) {
								if (json.count > 0) {
									MyMessageTip
											.msg("提示", "发现未复核医嘱，请复核!", true);
								}
							}
							if (this.mutiSelect) {
								this.selects[record.id] = record;
							} else {
								this.singleSelect = record;
							}
							// this.changeSelect(record);
							this.calRightChange();
						}, this);
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								delete this.selects[record.id];
							}
							this.calRightChange();
						}, this);

				return cm;
			},
			/**
			 * 调用右边更改事件
			 */
			calRightChange : function() {
				var selectList = this.changeSelect();
				this.fireEvent("initSelect", selectList, "patient");
			},
			initSelect : function() {
//				var selectList = [];
//				this.grid.getSelectionModel().selectFirstRow();
//				var tmp;
//				tmp = this.grid.getSelectionModel().getSelections()[0];
//				selectList.push(tmp);
				this.grid.getSelectionModel().selectAll();
				// this.fireEvent("initSelect",selectList, "patient");
			},
			/**
			 * 重新刷新数据
			 */
			refreshData : function() {
				this.requestData.pageNo = 1;
				this.store.load({
					callback : function(records, options, success) {
						// this.initSelect(); // 调用重新加载列表
					},
					scope : this
						// 作用域为this。必须加上否则this.initSelect(); 无法调用
					});
			},
			/**
			 * 同步左右两个列表方法 因右边列表后台会根据数据进行过滤，所以需要将左边列表也将不需要的数据删除
			 * 
			 * @param data
			 *            右边列表数据
			 */
			syncData : function(data) {
				if (this.store.data.items.length > 0) {
					this.compareData(data);
				} else {
					this.rightTemList = data;
					this.rightFlag = true;
				}
			},
			/**
			 * 对比数据，将当前列表中的病人在右边项目列表中存在的显示
			 * 
			 * @param data
			 */
			compareData : function(data) {
				this.dataList = [];
				var allItems = this.store.data.items;
				var tmp;
				var zyh;
				if (data) {
					for (var i = 0; i < allItems.length; i++) {
						tmp = allItems[i];
						zyh = tmp.data.ZYH;
						for (var j = 0; j < data.length; j++) {
							// 左边病人列表的住院号和右边项目列表的住院号判断是否相等
							if (data[j].data.ZYH == zyh) {
								this.dataList.push(tmp);
								break;
							}
						}
					}
				}
				this.store.removeAll();
				this.store.add(this.dataList);
				this.initSelect();
			}
		});