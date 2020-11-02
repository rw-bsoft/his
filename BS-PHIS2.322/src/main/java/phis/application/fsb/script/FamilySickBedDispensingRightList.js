/**
 * 家床发药-右边发药明细
 * 
 * @author : caijy
 */
$package("phis.application.fsb.script")

$import("phis.script.SelectList")

phis.application.fsb.script.FamilySickBedDispensingRightList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.cnds = [
			'and',['eq', ['$', 'a.FYBZ'], ['i', 0]],['eq', ['$', 'd.FYBZ'], ['i', 0]]];
	this.chall=true;
//	cfg.group = "BRCH";
//	cfg.groupTextTpl = "<table width='50%' style='color:#3764a0;font:bold !important;' border='0' cellspacing='0' cellpadding='0'><tr><td width='20%'>&nbsp;&nbsp;<b>床号:{[values.rs[0].data.BRCH]}</b></td><td width='24%'><b>姓名:{[values.rs[0].data.BRXM]}</b></td><td width='28%'><b>住院号:{[values.rs[0].data.ZYHM]}</b></td><td width='20%'><div align='left'><b>&nbsp;&nbsp;({[values.rs.length]} 条记录)</b></div></td></tr></table>"
	phis.application.fsb.script.FamilySickBedDispensingRightList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(phis.application.fsb.script.FamilySickBedDispensingRightList,
		phis.script.SelectList, {
		onStoreBeforeLoad:function(store,op){
			var r = this.getSelectedRecord()
			var n = this.store.indexOf(r)
			if(n > -1){
				this.selectedIndex = n
			}
			this.fireEvent("BeforeLoadData");
		},
			loadData : function() {
				this.fireEvent("loading", 1);
				this.requestData.serviceId = this.fullserviceId;
				this.requestData.serviceAction = this.queryServiceActionID;
				phis.application.fsb.script.FamilySickBedDispensingRightList.superclass.loadData
						.call(this);
			},
			getCM : function(items) {
				var cm = phis.script.SelectList.superclass.getCM.call(this,
						items)
				var sm = new Ext.grid.CheckboxSelectionModel({
							checkOnly : this.checkOnly,
							singleSelect : !this.mutiSelect,
							header : ""
						})
				this.sm = sm
				this.sm.handleMouseDown = Ext.emptyFn
				sm.on("rowselect", function(sm, rowIndex, record) {
							this.selects[record.json.JLXH] = record;
							this.fireEvent("fy", 1);
							if(!this.isLoad){
							this.checkAll(sm, rowIndex, record);
							}
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							delete this.selects[record.json.JLXH];
							if (this.getSelectedRecords().length == 0) {
								this.fireEvent("fy", 1);
							}
							if(!this.isLoad){
							this.unCheckAll(sm, rowIndex, record);}
						}, this)
				var _ctr = this;
				sm.selectRow = function(index, keepExisting, preventViewNotify) {
					if (this.isLocked()
							|| (index < 0 || index >= this.grid.store
									.getCount())
							|| (keepExisting && this.isSelected(index))) {
						return;
					}
					if(this.isSelected(index)){
					return;}
					if(_ctr.getTimeBetween()<500&&!_ctr.chall){//防止双击
					return
					}
					var r = this.grid.store.getAt(index);
					if (r
							&& this.fireEvent("beforerowselect", this, index,
									keepExisting, r) !== false) {
						if (!keepExisting || this.singleSelect) {
							this.clearSelections();
						}
						this.selections.add(r);
						this.last = this.lastActive = index;
						if (!preventViewNotify) {
							this.grid.getView().onRowSelect(index);
						}
						this.fireEvent("rowselect", this, index, r);
						this.fireEvent("selectionchange", this);
					}
				}
				sm.deselectRow = function(index, preventViewNotify) {
					if (this.isLocked()) {
						return;
					}
					if(!this.isSelected(index)){
					return;}
					if(_ctr.getTimeBetween()<500&&!_ctr.chall){//防止双击
					return
					}
					if (this.last == index) {
						this.last = false;
					}
					if (this.lastActive == index) {
						this.lastActive = false;
					}
					var r = this.grid.store.getAt(index);
					if (r) {
						this.selections.remove(r);
						if (!preventViewNotify) {
							this.grid.getView().onRowDeselect(index);
						}
						this.fireEvent("rowdeselect", this, index, r);
						this.fireEvent("selectionchange", this);
					}
				}
				sm.on("beforerowselect", function(SelectionModel, rowIndex,
								keepExisting, record) {
							if (record.data.ZT == "停嘱"
									|| record.data.ZT == "缺药") {
								return false;
							}
						}, this)
				return [sm].concat(cm)
			},
			// 选中一条记录,该记录上面的相同医嘱的全选
			checkAll : function(sm, rowIndex, record) {
				if(record.data.QZCL==0){//按次取整
				return
				}else{
				var yzxh = record.data.YZXH;
				if (rowIndex - 1 >= 0) {
					var data = this.store.getAt(rowIndex - 1).data;
					if (data["ZT"] == "可发" && yzxh == data["YZXH"]) {
						this.chall=true;
						sm.selectRow(rowIndex - 1, true);
						this.chall=false;
					}
				}
				if(record.data.QZCL==1){//按天取整的,上面和下面都选中
				var count = this.store.getCount();
				if (rowIndex + 1 < count) {
					var data = this.store.getAt(rowIndex + 1).data;
					if (data["ZT"] == "可发" && yzxh == data["YZXH"]) {
						this.chall=true;
						sm.selectRow(rowIndex + 1, true);
						this.chall=false;
					}
				}
				}
				}
			},
			// 取消选中一条,该记录下面的相同的医嘱全部取消选中
			unCheckAll : function(sm, rowIndex, record) {
				if(record.data.QZCL==0){//按次取整
				return
				}else{
				var yzxh = record.data.YZXH;
				var count = this.store.getCount();
				if (rowIndex + 1 < count) {
					var data = this.store.getAt(rowIndex + 1).data;
					if (data["ZT"] == "可发" && yzxh == data["YZXH"]) {
						this.chall=true;
						sm.deselectRow(rowIndex + 1);
						this.chall=false;
					}
				}
				if(record.data.QZCL==1){//按天取整的,上面和下面都取消选中
					if (rowIndex - 1 >= 0) {
					var data = this.store.getAt(rowIndex - 1).data;
					if (data["ZT"] == "可发" && yzxh == data["YZXH"]) {
						this.chall=true;
						sm.deselectRow(rowIndex - 1);
						this.chall=false;
					}
				}
				}
				
				}
			},
			// 加载数据时全选
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				this.isLoad=true
				this.sm.selectAll();
				this.isLoad=false;
			},
			onRenderer : function(value, metaData, r) {
				if (r.data.ZT != "可发") {
					return "<font color='red'>" + value + "</font>";
				}
				return value;
			},
			onLoadData : function() {
				this.fireEvent("loading", 2);
			},
			onRendererNull : function(value, metaData, r) {
				if (value == null || value == "null") {
					return "";
				} else {
					return value;
				}
			},
			//获取间隔时间,解决鼠标双击点记录(不点勾选框) 导致出现无数个提示的问题(zww提的2.4.2BUG89)
			getTimeBetween:function(){
			var t=new Date().getTime()
			if(!this.t){
			this.t=t;
			return 0;
			}
			var b= t-this.t;
			if(b==0){
			b=1;
			}
			this.t=t;
			return b;
			}
		});