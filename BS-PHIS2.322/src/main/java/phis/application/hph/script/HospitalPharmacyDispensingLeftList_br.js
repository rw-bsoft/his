/**
 * 医嘱发药-待发药病区选择List
 * 
 * @author : caijy
 */
$package("phis.application.hph.script")

$import("phis.script.SelectList")

phis.application.hph.script.HospitalPharmacyDispensingLeftList_br = function(
		cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	// cfg.cnds=['and',['eq',['$','a.FYBZ'],['i',0]],['eq',['$','a.YZLX'],['i',1]]];
	// cfg.initCnd=['eq',['$','1'],['i',2]];
	phis.application.hph.script.HospitalPharmacyDispensingLeftList_br.superclass.constructor
			.apply(this, [ cfg ])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyDispensingLeftList_br,
		phis.script.SelectList, {
			onStoreBeforeLoad : function(store, op) {
				var r = this.getSelectedRecord()
				var n = this.store.indexOf(r)
				if (n > -1) {
					this.selectedIndex = n
				}
				this.fireEvent("BeforeLoadData");
			},
			// 单击时调出医嘱发药和汇总发药
			onRowClick : function() {
				var r = this.getSelectedRecords();
				if (r == null) {
					return;
				}
				this.fireEvent("recordClick_br", r);
			},
			doNew : function() {
				this.clear();
				this.clearSelect();
			},
			getCM : function(items) {
				var cm = phis.script.SelectList.superclass.getCM.call(this,
						items)
				var sm = new Ext.grid.CheckboxSelectionModel({
					checkOnly : this.checkOnly,
					singleSelect : !this.mutiSelect,
//					header : ""
				})
				this.sm = sm
				sm.on("rowselect", function(sm, rowIndex, record) {
					this.selects[record.id] = record
					this.onRowClick();
				}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
					delete this.selects[record.id]
					this.onRowClick();
				}, this)
				
				var _ctr=this;
				this.sm.selectAll=function(){
				if(this.isLocked()){return}
				_ctr.selects = {};
				var count=_ctr.store.getCount();
				this.suspendEvents(false);
				this.selections.clear();
				for(var i=0;i<count;i++){
					this.selectRow(i,true)
				_ctr.selects[_ctr.store.getAt(i).id] = _ctr.store.getAt(i)
				}
				this.resumeEvents();
				_ctr.onRowClick();
				}
				
				this.sm.clearSelections=function(a){
					_ctr.selects = {};
					if(this.isLocked()){return}
						this.suspendEvents(false);
					if(a!==true){var c=this.grid.store,b=this.selections;b.each(function(d){this.deselectRow(c.indexOfId(d.id))},this);b.clear()}else{this.selections.clear()}this.last=false
				this.resumeEvents();
				_ctr.onRowClick();
				}
				
				return [ sm ].concat(cm)
			},
			onStoreLoadData:function(store,records,ops){
				this.fireEvent("loadData", store)// ** 不管是否有记录，都fire出该事件
				if(records.length == 0){
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				}
				else{
					this.selectRow(this.selectedIndex);
				}
			}
		// ,
		// // 去掉全选框
		// initPanel : function(sc) {
		// if (this.grid) {
		// if (!this.isCombined) {
		// this.fireEvent("beforeAddToWin", this.grid)
		// this.addPanelToWin();
		// }
		// return this.grid;
		// }
		// var schema = sc
		// if (!schema) {
		// var re = util.schema.loadSync(this.entryName)
		// if (re.code == 200) {
		// schema = re.schema;
		// } else {
		// this.processReturnMsg(re.code, re.msg, this.initPanel)
		// return;
		// }
		// }
		// this.schema = schema;
		// this.isCompositeKey = schema.isCompositeKey;
		// var items = schema.items
		// if (!items) {
		// return;
		// }
		//
		// this.store = this.getStore(items)
		// if (this.mutiSelect) {
		// this.sm = new Ext.grid.CheckboxSelectionModel({
		// header : ""
		// })
		// }
		// this.cm = new Ext.grid.ColumnModel(this.getCM(items))
		// var cfg = {
		// stripeRows : true,
		// border : false,
		// store : this.store,
		// cm : this.cm,
		// sm : this.sm,
		// height : this.height,
		// loadMask : {
		// msg : '正在加载数据...',
		// msgCls : 'x-mask-loading'
		// },
		// buttonAlign : 'center',
		// clicksToEdit : true,
		// frame : true,
		// plugins : this.rowExpander,// modife
		// // by
		// // taoy
		// // /*this.rowExpander
		// // stripeRows : true,
		// viewConfig : {
		// // forceFit : true,
		// enableRowBody : this.enableRowBody,
		// getRowClass : this.getRowClass
		// }
		// }
		// if (this.group)
		// cfg.view = new Ext.grid.GroupingView({
		// // forceFit : true,
		// showGroupName : true,
		// enableNoGroups : false,
		// hideGroupedColumn : true,
		// enableGroupingMenu : false,
		// columnsText : "表格字段",
		// groupByText : "使用当前字段进行分组",
		// showGroupsText : "表格分组",
		// groupTextTpl : this.groupTextTpl
		// });
		// if (this.gridDDGroup) {
		// cfg.ddGroup = this.gridDDGroup;
		// cfg.enableDragDrop = true
		// }
		// if (this.summaryable) {
		// $import("org.ext.ux.grid.GridSummary");
		// var summary = new org.ext.ux.grid.GridSummary();
		// cfg.plugins = [summary]
		// this.summary = summary;
		// }
		// var cndbars = this.getCndBar(items)
		// if (!this.disablePagingTbr) {
		// cfg.bbar = this.getPagingToolbar(this.store)
		// } else {
		// cfg.bbar = this.bbar
		// }
		// if (!this.showButtonOnPT) {
		// if (this.showButtonOnTop) {
		// cfg.tbar = (cndbars.concat(this.tbar || []))
		// .concat(this.createButtons())
		// } else {
		// cfg.tbar = cndbars.concat(this.tbar || [])
		// cfg.buttons = this.createButtons()
		// }
		// }
		//
		// if (this.disableBar) {
		// delete cfg.tbar;
		// delete cfg.bbar;
		// cfg.autoHeight = true;
		// cfg.frame = false;
		// }
		// this.expansion(cfg);// add by yangl
		// this.grid = new this.gridCreator(cfg)
		// this.schema = schema;
		// this.grid.on("afterrender", this.onReady, this)
		// this.grid.on("contextmenu", function(e) {
		// e.stopEvent()
		// })
		// this.grid.on("rowcontextmenu", this.onContextMenu, this)
		// this.grid.on("rowdblclick", this.onDblClick, this)
		// this.grid.on("rowclick", this.onRowClick, this)
		// this.grid.on("keydown", function(e) {
		// if (e.getKey() == e.PAGEDOWN) {
		// e.stopEvent()
		// this.pagingToolbar.nextPage()
		// return
		// }
		// if (e.getKey() == e.PAGEUP) {
		// e.stopEvent()
		// this.pagingToolbar.prevPage()
		// return
		// }
		// if (e.getKey() == e.ESC) {
		// if (this.onESCKey) {
		// this.onESCKey();
		// }
		// return
		// }
		// }, this)
		// if (!this.isCombined) {
		// this.fireEvent("beforeAddToWin", this.grid)
		// this.addPanelToWin();
		// }
		// return this.grid
		// }
		});