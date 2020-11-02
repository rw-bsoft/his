$package("phis.application.fsb.script")
$import("phis.script.SelectList")

phis.application.fsb.script.FamilySickBedPlanChooseList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.fsb.script.FamilySickBedPlanChooseList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.fsb.script.FamilySickBedPlanChooseList,
		phis.script.SelectList, {
			expansion : function(cfg) {
				var items = [];
				items.push(new Ext.form.Label({
							text : "计划日期"
						}))
				this.beginDate = new Ext.form.DateField({
							name : 'beginDate',
							value : Date.getServerDate(),
							width : 100,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间：'
						});
				this.endDate = new Ext.form.DateField({
							name : 'endDate',
							value : Date.getServerDate(),
							width : 100,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
				items.push(this.beginDate);
				items.push(new Ext.form.Label({
							text : "至"
						}))
				items.push(this.endDate)
				items.push('-');
				items.push(new Ext.form.Label({
							text : "计划医生："
						}))
				this.docDic = util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.doctor_cfqx",
							width : 80,
							autoLoad : true
						});

				this.docDic.setEditable(false);
				this.docDic.store.on("load", this.docDicLoad, this);
				items.push(this.docDic)
				var tbar = cfg.tbar;
				cfg.tbar = items;
				cfg.tbar.push(tbar);
			},
			docDicLoad : function(store) {
				var r = new Ext.data.Record({
							key : "-1",
							text : "全部"
						})
				store.insert(0, [r]);
				this.docDic.setValue((this.mainApp.uid || "-1"))
				this.doQuery();
			},
			onWinShow : function() {
				this.clearSelect();
				if (this.docDic.getValue()) {
					this.doQuery();
				}
			},
			doQuery : function() {
				var exCnd = null;
				// 计划医生
				var ysdm = this.docDic.getValue();
				if (ysdm != '-1') {
					if (exCnd) {
						exCnd = ['and', exCnd,
								['eq', ['$', 'a.YSDM'], ['s', ysdm]]];
					} else {
						exCnd = ['eq', ['$', 'a.YSDM'], ['s', ysdm]];
					}
				}
				// 计划时间
				var datefrom = this.beginDate.getRawValue();
				var dateTo = this.endDate.getRawValue();
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "计划开始时间不能大于结束时间");
					return;
				}
				var timeCnd = null;
				if (datefrom != null && datefrom != ""
						&& (dateTo == null || dateTo == "")) {
					timeCnd = ['ge',
							['tochar', ['$', 'a.KSSJ'], ['s', 'yyyy-mm-dd']],
							['s', datefrom]];
				} else if (dateTo != null && dateTo != ""
						&& (datefrom == null || datefrom == "")) {
					timeCnd = ['le',
							['tochar', ['$', 'a.KSSJ'], ['s', 'yyyy-mm-dd']],
							['s', dateTo]];
				} else if (dateTo != null && dateTo != "" && datefrom != null
						&& datefrom != "") {
					timeCnd = [
							'and',
							[
									'ge',
									['tochar', ['$', 'a.KSSJ'],
											['s', 'yyyy-mm-dd']],
									['s', datefrom]],
							[
									'le',
									['tochar', ['$', 'a.KSSJ'],
											['s', 'yyyy-mm-dd']], ['s', dateTo]]];
				}
				if (exCnd) {
					exCnd = ['and', exCnd, timeCnd];
				} else {
					exCnd = timeCnd;
				}
				var initCnd = this.initCnd
						|| ['eq', ['$', 'a.JGID'], ['s', this.mainApp.deptId]]
				var cnd;
				if (initCnd) {
					cnd = initCnd
				}
				if (exCnd && cnd) {
					cnd = ['and', exCnd, cnd]
				}
				this.requestData.cnd = cnd
				this.refresh()
			},
			jldwRender : function(value, metaData, r, row, col, store) {
				if (r.get("XMLX") != 1)
					return null;
				return value;
			},
			showColor : function(v, params, data) {
				var YPZH = data.get("YPZH") % 2 + 1;
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
				return "";
			},
			getYpzh : function() {
				// yjzh = 1;
				ypzh = 0;
				var store = this.grid.getStore();
				var n = store.getCount()
				var YPZHs = [];
				for (var i = 0; i < n; i++) {
					if (i == 0) {
						ypzh = 1;
						var r = store.getAt(i)
						YPZHs.push(ypzh)
					} else {
						var r1 = store.getAt(i - 1)
						var r = store.getAt(i)
						if (r1.get('YPZH') == r.get('YPZH')) {
							YPZHs.push(ypzh)
						} else {
							YPZHs.push(++ypzh)
						}
					}
				}
				for (var i = 0; i < YPZHs.length; i++) {
					var r = store.getAt(i);
					r.set('YPZH', YPZHs[i]);
				}
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.getYpzh();
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				this.getYpzh();
				store.commitChanges();
				// var store = this.grid.getStore();
				var n = store.getCount();
			},
			doImport : function() {
				var records = this.getSelectedRecords();
				if (!records || records.length == 0) {
					MessageTip.msg("提示", "请先选择需要提交的数据!", true);
					return;
				}
				var body = [];
				for (var i = 0; i < records.length; i++) {
					var data = records[i].data;
					data.JHLX = data.XMLX;
					delete data.XMLX;
					body.push(data);
				}
				phis.script.rmi.jsonRequest({
							serviceId : "familySickBedManageService",
							serviceAction : "loadPlanSet",
							body : body,
							brxx : {
								ZYH : this.opener.initDataId,
								BRXZ : this.opener.exContext.brxx.get("BRXZ"),
								BRID : this.opener.exContext.brxx.get("BRID")
							}
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.fireEvent("choosePlan", json.body);
							this.doClose();
						}, this);
			},
			doClose : function() {
				this.win.hide();
			}

		})