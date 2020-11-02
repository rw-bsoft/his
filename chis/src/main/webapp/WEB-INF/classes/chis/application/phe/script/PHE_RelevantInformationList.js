$package("chis.application.phe.script")

$import("chis.script.BizSimpleListView")

chis.application.phe.script.PHE_RelevantInformationList=function(cfg){
	cfg.autoFieldWidth = false;
	chis.application.phe.script.PHE_RelevantInformationList.superclass.constructor.apply(this,[cfg]);
} 

Ext.extend(chis.application.phe.script.PHE_RelevantInformationList,chis.script.BizSimpleListView,{
	doCreateRelevant : function() {
	var module = this.createSimpleModule("DCPHE01_01",
			this.dcphe01form);
	module.on("save", this.onSave, this);
	module.initDataId = null;
	module.op = "create";
	module.exContext.control = {
		"update" : true
	};
	this.showWin(module);
	module.doNew();
},doModify : function() {
	var r = this.getSelectedRecord();
	if (!r) {
		return;
	}
	var module = this.createSimpleModule("DCPHE01_01",
			this.dcphe01form);
	module.on("save", this.onSave, this);
	module.initDataId = r.id;
	this.showWin(module);
	module.loadData();
},onDblClick : function(grid, index, e) {
	this.doModify();
},onSave : function() {
    this.refresh();
},doCndQuery : function(button, e, addNavCnd) {
	var initCnd = this.initCnd
	var itid = this.cndFldCombox.getValue()
	var items = this.schema.items
	var it
	for (var i = 0; i < items.length; i++) {
		if (items[i].id == itid) {
			it = items[i]
			break
		}
	}
	if (!it) {
		if (addNavCnd) {
			if (initCnd) {
				this.requestData.cnd = ['and', initCnd, this.navCnd];
			} else {
				this.requestData.cnd = this.navCnd;
			}
			this.refresh()
			return
		} else {
			return;
		}
	}
	this.resetFirstPage()
	var refAlias = it.refAlias || "a"
	var v = this.cndField.getValue()
//	['or', ['eq',['s','01'],['$','%user.aliesRole.id']], ['eq',['s','05'],['$','%user.aliesRole.id']]]
	var cnd = ['eq', ['$', refAlias + "." + it.id]]
	if (it.dic) {
		var values = v.split(",");
		var cnd =  ['and'] ; 
		for(var i = 0 ;i< values.length ; i++){
			cnd.push(['like',['$', refAlias + "." + it.id],['s','%'+values[i]+'%']]);
		}
	} else {
		switch (it.type) {
			case 'int' :
				cnd.push(['i', v])
				break;
			case 'double' :
			case 'bigDecimal' :
				cnd.push(['d', v])
				break;
			case 'string' :
				cnd[0] = 'like'
				cnd.push(['s', v + '%'])
				break;
			case "date" :
				v = v.format("Y-m-d")
				cnd[1] = ['$',
						"str(" + refAlias + "." + it.id + ",'yyyy-MM-dd')"]
				cnd.push(['s', v])
				break;
			case 'datetime' :
			case 'timestamp' :
				if (it.xtype == "datefield") {
					v = v.format("Y-m-d")
					cnd[1] = [
							'$',
							"str(" + refAlias + "." + it.id
									+ ",'yyyy-MM-dd')"]
					cnd.push(['s', v])
				} else {
					v = v.format("Y-m-d H:i:s")
					cnd[1] = [
							'$',
							"str(" + refAlias + "." + it.id
									+ ",'yyyy-MM-dd HH:mm:ss')"]
					cnd.push(['s', v])
				}
				break;
		}
	}
	this.queryCnd = cnd
	if (initCnd) {
		cnd = ['and', initCnd, cnd]
	}
	if (addNavCnd) {
		this.requestData.cnd = ['and', cnd, this.navCnd];
		this.refresh()
		return
	}
	this.requestData.cnd = cnd
	this.refresh()
}

});