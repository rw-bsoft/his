/**
 * @include "SimpleListView.js"
 */
$package("chis.script")

$import("app.modules.list.EditorListView")

chis.script.SelectList = function(cfg){
    this.width = 620;
    this.mutiSelect = cfg.mutiSelect || true;
    this.checkOnly = cfg.checkOnly || true;
    this.showButtonOnTop = cfg.showButtonOnTop || true;
    this.actions = [
        {id:"confirmSelect",name:"确定",iconCls:"read"},
        {id:"showOnlySelected",name:"查看已选",iconCls:"update"}
    ]
    chis.script.SelectList.superclass.constructor.apply(this,[cfg])
}
Ext.extend(chis.script.SelectList, app.modules.list.EditorListView,{

    init:function(){
        this.addEvents({
            "select":true
        })
        if(this.mutiSelect){
            this.selectFirst = false
        }
        this.selects = {}
        this.singleSelect = {}
        chis.script.SelectList.superclass.init.call(this)
    },
    initPanel:function(schema){
        return chis.script.SelectList.superclass.initPanel.call(this,schema)
    },
    onStoreLoadData:function(store,records,ops){
        chis.script.SelectList.superclass.onStoreLoadData.call(this,store,records,ops)
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
    getCM:function(items,sm){
        var cm = chis.script.SelectList.superclass.getCM.call(this,items)
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