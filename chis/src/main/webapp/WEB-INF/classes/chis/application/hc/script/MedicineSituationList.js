$package("chis.application.hc.script");

$import("chis.script.BizSimpleListView");

chis.application.hc.script.MedicineSituationList = function(cfg) {
	chis.application.hc.script.MedicineSituationList.superclass.constructor.apply(this,[cfg]);
	this.enableCnd=false;
	this.disablePagingTbr = true;
	this.autoLoadData = false;
	this.selectedIndex = 0;
	this.on("remove", this.selectFirstRow, this);
};

Ext.extend(chis.application.hc.script.MedicineSituationList, chis.script.BizSimpleListView, {
	doAction:function(item,e){
		var btns = this.grid.getTopToolbar().items
		if(btns.items[1].disabled)
			return;
		chis.application.hc.script.MedicineSituationList.superclass.doAction.call(this,item,e);
	},
	
	onSave:function(entryName, op, json, data){
		var store = this.store;
		var body = json.body;
		if(op == 'create'){
			var record = new Ext.data.Record(body, body[this.schema.pkey]);
			store.add(record);
			store.commitChanges();
			var index = store.getCount()-1;
			this.selectedIndex = index;
			this.selectRow(index);
			return;
		}
		var index = store.find("situationId", body.situationId);
		var r = store.getAt(index);
		if (!r) {
			return;
		}
		this.setRecordValues(r, body);
	},
	
	/**
	 * yub
	 * update时，更新grid中一条record的数据
	 * @param {} r 需要修改的record
	 * @param {} body 新数据，用于覆盖record
	 */
	setRecordValues : function(r, body){
		var items = this.schema.items;
		var n = items.length;
		for(var i = 0; i < n; i++){
			var it = items[i];
			var key = it.id;
			if(it.dic){
				r.set(key,body[key]);
				r.set(key+"_text",body[key+"_text"]);
			}else{
				r.set(key,body[key]);
			}
		}
		r.commit();
	}
});