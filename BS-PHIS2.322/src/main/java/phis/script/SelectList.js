/**
* @include "SimpleListView.js"
*/
$package("phis.script")

$import("phis.script.SimpleList")

phis.script.SelectList = function(cfg){
	this.width = 620;
	this.mutiSelect = cfg.mutiSelect || true;
	this.checkOnly = cfg.checkOnly || false;
	this.actions = [
			{id:"confirmSelect",name:"确定",iconCls:"read"},
			{id:"showOnlySelected",name:"查看已选",iconCls:"update"}
	]
	phis.script.SelectList.superclass.constructor.apply(this,[cfg])
}
Ext.extend(phis.script.SelectList, phis.script.SimpleList,{
	
	init:function(){
		this.addEvents({
			"select":true
		})
		if(this.mutiSelect){
			this.selectFirst = false
		}
		this.selects = {}
		this.singleSelect = {}
		phis.script.SelectList.superclass.init.call(this)
	},
	initPanel:function(schema){
		return phis.script.SelectList.superclass.initPanel.call(this,schema)
	},
	onStoreLoadData:function(store,records,ops){
		phis.script.SelectList.superclass.onStoreLoadData.call(this,store,records,ops)
		if(records.length == 0 ||  !this.selects || !this.mutiSelect){
			return
		}
		var selRecords = []
		for(var id in this.selects){
			var r = store.getById(id)
			if(r){
				selRecords.push(r)
			}
		}
		this.grid.getSelectionModel().selectRecords(selRecords)
		
	},
	getCM:function(items){
		var cm = phis.script.SelectList.superclass.getCM.call(this,items)
		var sm = new Ext.grid.CheckboxSelectionModel({
			checkOnly:this.checkOnly,
			singleSelect:!this.mutiSelect
		})
		this.sm = sm
		sm.on("rowselect",function(sm,rowIndex,record){
			if(this.mutiSelect){
				this.selects[record.id] = record
			}
			else{
				this.singleSelect = record
			}
		},this)
		sm.on("rowdeselect",function(sm,rowIndex,record){
			if(this.mutiSelect){
				delete this.selects[record.id]
      		} else{
				this.singleSelect = {}
			}
		},this)
		return [sm].concat(cm)
	},
	onDblClick:function(grid,index,e){
		this.doConfirmSelect()
	},
	clearSelect:function(){
		this.selects = {};
		this.singleSelect = {};
		this.sm.clearSelections();
		var checker = Ext.fly(this.grid.getView().innerHd)
						.child('.x-grid3-hd-checker')
				if (checker) {
					checker.removeClass('x-grid3-hd-checker-on');
				}
		//Ext.fly(this.grid.getView().innerHd).child('.x-grid3-hd-checker').removeClass('x-grid3-hd-checker-on');
	},
	doConfirmSelect:function(){
		this.fireEvent("select",this.getSelectedRecords(),this)
		this.clearSelect();
    	var win = this.getWin();
     	if (win) {
			win.hide();
      	}
	},
	doShowOnlySelected:function(){
		this.store.removeAll()
		var records = this.getSelectedRecords()
		this.store.add(records)
		this.grid.getSelectionModel().selectRecords(records)
	},
	getSelectedRecords:function(){
		var records = []
		if(this.mutiSelect){
			for(var id in this.selects){
				records.push(this.selects[id])
			}
		}
		else{
			records[0] = this.singleSelect
		}
		return records
	}
});