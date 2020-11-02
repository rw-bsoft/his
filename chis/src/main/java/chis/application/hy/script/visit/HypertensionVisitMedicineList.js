$package("chis.application.hy.script.visit");

$import("app.desktop.Module", "chis.script.BizSimpleListView",
		"chis.application.hy.script.visit.HypertensionVisitMedicineForm",
		"util.dictionary.DictionaryLoader");

chis.application.hy.script.visit.HypertensionVisitMedicineList = function(cfg) {
	cfg.height = 420;
	//cfg.cnds = ['eq', ['$', 'visitId'], ['s', this.visitId]];
	chis.application.hy.script.visit.HypertensionVisitMedicineList.superclass.constructor.apply(this,
			[cfg]);
	this.phrId = cfg.phrId;
	this.visitId = cfg.visitId;
	this.recordLst = [];
	this.on("phrIdChange", this.onPhrIdChange, this);
	this.on("visitIdChange", this.onVisitIdChange, this);
	this.on("empiIdChange", this.onEmpiIdChange, this);
	this.on("readOnly", this.onReadOnly, this);
	this.on("loadData", this.onLoadData, this);
	//this.on("refresh", this.onRefresh, this);
	Ext.apply(this, { resetButtons : Ext.emptyFn});
};

Ext.extend(chis.application.hy.script.visit.HypertensionVisitMedicineList, chis.script.BizSimpleListView, {
    
    loadData:function(){
          this.requestData.serviceId = "chis.hypertensionVisitService";
          this.requestData.serviceAction = "getHyperVisitMedicine";
          this.requestData.visitId = this.exContext.args.visitId;
//        this.initCnd = ['eq', ['$', 'visitId'], ['s', this.exContext.args.visitId]];
//        this.requestData.cnd = this.initCnd;
        chis.application.hy.script.visit.HypertensionVisitMedicineList.superclass.loadData.call(this);
        this.exContext.control = this.getTopBtnControl();
        this.resetButtons();
    	//this.setBtnableByRecordSataus();
    },
        
	onLoadData : function(store) {
//		this.setBtnByRecordNum();
		
        var count = store.getCount();
        this.fireEvent("activeHyperVisDesForm",count);
        for (var i = 0; i < count; i++) {
            if (!store.getAt(i)) {
                break;
            }
            var data = store.getAt(i).data;
            data.phrId = this.phrId;
            store.getAt(i).commit();
        }
        store.commitChanges();
        this.openAddWin();
    },

    openAddWin : function() {
    	var add = false;
    	if(this.notNeedAddMedicine){
    		return;
    	}
        if (this.needAddMedicine) {
        	add = true;
            var item = {text:'服药情况添加(F1)'};
            this.doAdd(item);
        }
        var recordNum = this.store.getCount();
        if(add == false && recordNum == 0){
        	var item = {text:'服药情况添加(F1)'};
            this.doAdd(item);
        }
    },
    
    onReadOnly : function(op) {
        var bts = this.grid.getTopToolbar().items;
        if (!bts) {
            return;
        }
        if (op == "read") {
            this.readOnly = true;
        } else {
            this.readOnly = false;
        }
        this.resetButtons();
    },
    doAdd : function(item, e) {
        var view = this.midiModules["medicineForm"];
        if (!view) {
            var config = this.loadModuleCfg(this.refModule);
            if (!config) {
                Ext.Msg.alert("错误", "服药情况明细模块加载失败！");
                return;
            }
            var cfg = {
                        actions : config.actions,
                        entryName : this.entryName,
                        title : this.name + '-' + item.text,
                        mainApp : this.mainApp,
                        op : "create",
                        saveServiceId : "chis.hypertensionService",
                        phrId : this.phrId,
                        visitId : this.visitId,
                        exContext : this.exContext
                    };
            view = eval("new " + config.script + "(cfg)");
            this.midiModules["medicineForm"] = view;
            view.on("addItem", this.onAddItem, this);
            view.on("close", this.onMedicineItemFormClose, this);
        } else {
            view.phrId = this.phrId;
            view.visitId = this.visitId;
        }
        view.doNew();
        var win = view.getWin();
        win.show();
    },
    
    loadAppConfig : function(id) {
        var result = util.rmi.miniJsonRequestSync({
                    serviceId : "moduleConfigLocator",
                    id : id
                });
        if (result.code == 200) {
            return result.json.body;
        }
        if (result.code > 300) {
            this.processReturnMsg(result.code, result.msg, this.loadAppConfig);
            return;
        }
    },
    
    onMedicineItemFormClose : function() {
        if (!this.needGroup && this.groupAlarm != 2) {
            return;
        }
        if (this.groupAlarm != 2) {
                Ext.Msg.show({
                title : "提示", 
                msg : "[危险因素]，[靶器官损害]，[并发症]发生变化，需要重新进行评估。",
                modal : true,
                icon: Ext.MessageBox.QUESTION,
                buttons: Ext.Msg.YESNO,
                fn : function(btn, text) {
                    if (btn == "no") {
                        return;
                    }
                    this.fireEvent("activeModule", "C02", "create");
                }, 
                scope : this});
            } else {
                Ext.Msg.alert(
                    "提示", 
                    "需要进行年度评估",
                    function() {
                        this.fireEvent("activeModule", "C02", "create");
                    }, 
                    this);
            }
        this.needGroup = false;
        this.groupAlarm = -1;
    },
    
    onAddItem : function(data) {
        if (!this.schema) {
            return;
        }
        var count = this.store.getCount();
        for (var i = 0; i < count; i++) {
            if (data.medicineName == '109999999999') {
                continue;
            }
            if (this.store.getAt(i).data.medicineName == data.medicineName) {
                Ext.Msg.alert("提示", "已添加过药品： " + this.store.getAt(i).data['medicineName_text']);
                return;
            }
        }
        var items = this.schema.items;
        var r = {};
        var useUnits = "";
       	for (var i = 0; i < items.length; i++) {
			var it = items[i];
			r[it.id] = data[it.id];
			if (it.dic && data[it.id]) {
				r[it.id+'_text'] = data[it.id+'_text'];
				continue;
			}
		}
        r.useUnits = useUnits;
        r.visitId = this.exContext.args.visitId;
        r.phrId = this.exContext.args.phrId;
        r.createUser = this.mainApp.uid;
        r.lastModifyUser_text = this.mainApp.uname;
        r.createUnit = this.mainApp.deptId;
        r.createUnit_text = this.mainApp.dept;
        r.creaetDate = new Date(this.mainApp.serverDate).format("Y-m-d");
		r.lastModifyUser = this.mainApp.uid;
		r.lastModifyUser_text = this.mainApp.uname;
        r.lastModifyDate = new Date(this.mainApp.serverDate).format("Y-m-d");
		r.lastModifyUnit = this.mainApp.deptId;
		r.lastModifyUnit_text = this.mainApp.dept;

		if(!r.visitId){
			var record = new Ext.data.Record(r);
			this.store.add(record);
			this.store.commitChanges();
			this.grid.getSelectionModel().selectLastRow();
			this.setBtnByRecordNum();
		}else{
	        this.addItemSaveToServer(r);
        }
        this.midiModules["medicineForm"].doCancel();
        this.fireEvent("addItem")
    },
    
    addItemSaveToServer : function(saveData){
    	util.rmi.jsonRequest({
	                serviceId : "chis.hypertensionVisitService",
	                serviceAction : "saveVisitMedicine",
	                method:"execute",
	                op : "create",
	                body : saveData,
	                schema : this.entryName
	            }, function(code,msg,json){
	                if (code > 300) {
	                    this.processReturnMsg(code, msg, this.onAddItem);
	                }
	                this.refresh();
	                this.grid.getSelectionModel().selectLastRow();
		            if (this.midiModules["medicineForm"]) {
//		                this.midiModules["medicineForm"].doCancel();
		                this.midiModules["medicineForm"].doCreateMedicine();
		            }
	                this.fireEvent("save", this.entryName, "create", json);
	                this.needAddMedicine = false;
	            }, this);
    },
    
    doDelete : function(item, e) {
    	var r = this.getSelectedRecord()
		if(r == null){
			return
		}
        Ext.Msg.show({
            title : "删除确认",
            msg : '是否确定要删除该项药品?',
            modal : true,
            width : 300,
            buttons : Ext.MessageBox.OKCANCEL,
            multiline : false,
            fn : function(btn, text) {
                if (btn == "ok") {
                    var r = this.getSelectedRecord(false);
                    var rowIndex = this.store.indexOf(r);
                    var pkey = r.get("recordId");
                    if(!pkey){
                    	this.store.remove(r);
                    	this.store.commitChanges();
                    	this.setBtnByRecordNum();
                    }else{
	                    util.rmi.jsonRequest({
	                        serviceId : "chis.hypertensionVisitService",
	                        serviceAction : "DelVisitMedicine",
	                        method:"execute",
	                        schema : this.entryName,
	                        pkey : pkey
	                        }, function(code,msg,json){
	                            if (code > 300) {
	                                this.processReturnMsg(code, msg, this.doDelete);
	                                return;
	                            }
	                            this.refresh();
	                            rowIndex = rowIndex >= this.store
	                                    .getCount()
	                                    ? rowIndex - 1
	                                    : rowIndex;
	                            this.grid.getSelectionModel()
	                                    .selectRow(rowIndex);
	                            this.fireEvent("changeMedicine");
	                        }, this);
                    }
//                    if (this.store.getCount() == 0) {
//                        this.fireEvent("activeHyperVisDesForm",0);
//                    }
                }
            },
            scope : this
        });
        
    },

    doModify : function(item, e) {
        if(this.store.getCount() == 0){
            return;
        }
        var selected = this.getSelectedRecord();
        this.selectedId = selected.id;
        selected.data.medicineUnit_text = selected.data.useUnits;
        var view = this.midiModules["updateForm"];
        if (!view) {
            var config = this.loadModuleCfg(this.refModule);
            if (!config) {
                Ext.Msg.alert("错误", "服药情况明细模块加载失败！");
                return;
            }
            var cfg = {
                        readOnly : this.readOnly,
                        actions : config.actions,
                        entryName : this.entryName,
                        title : this.name + '-' + item.text,
                        mainApp : this.mainApp,
                        op : "update",
                        saveServiceId : "chis.hypertensionService",
                        record : selected,
                        exContext : this.exContext
                    };
            view = eval("new " + config.script + "(cfg)");
            this.midiModules["updateForm"] = view;
            view.on("updateItem", this.onUpdateItem, this);
        } else {
            view.record = selected;
            view.readOnly = this.readOnly;
        }
        view.op = "update";
        view.getWin().show();
    },

    onDblClick : function() {
        this.doModify({
                    cmd : "update",
                    text : "修改"
                });
    },

    onUpdateItem : function(data) {
        if (!this.schema) {
            return;
        }
        var count = this.store.getCount();
        for (var i = 0; i < count; i++) {
            if (data.medicineName == '109999999999') {
                continue;
            }
            var rd = this.store.getAt(i).data;
            if (rd.recordId != data.recordId && rd.medicineName == data.medicineName) {
                Ext.Msg.alert("提示", "已添加过药品： " + this.store.getAt(i).data['medicineName']);
                return;
            }
        }
        var selected = this.store.getById(this.selectedId);
        var sltData = selected.data;
        var items = this.schema.items;
        var useUnits="";
        for (var i = 0; i < items.length; i++) {
			var it = items[i];
			sltData[it.id] = data[it.id];
			sltData[it.id+'_text'] = data[it.id+'_text'];
			if (it.dic && data[it.id]) {
				continue;
			}
		}
		
        sltData.useUnits = useUnits;
        sltData.visitId = this.exContext.args.visitId;
        sltData.phrId = this.exContext.args.phrId;
        sltData.lastModifyUser = this.mainApp.uid;
		sltData.lastModifyUser_text = this.mainApp.uname;
        sltData.lastModifyDate = new Date(this.mainApp.serverDate).format("Y-m-d");
		sltData.lastModifyUnit = this.mainApp.deptId;
		sltData.lastModifyUnit_text = this.mainApp.dept;

        if(!sltData.visitId && !sltData.recordId){
	        this.store.getById(this.selectedId).commit();
        }else{
	        util.rmi.jsonRequest({
	            serviceId : "chis.hypertensionVisitService",
	            serviceAction : "saveVisitMedicine",
	            method:"execute",
	            op : "update",
	            body : sltData,
	            schema : this.entryName
	            }, function(code,msg,json){
	                if (code > 300) {
	                    this.processReturnMsg(code, msg, this.onUpdateItem);
	                    return;
	                }
	                this.refresh();
	                this.fireEvent("changeMedicine");
	                this.grid.getSelectionModel().selectRow(this.selectedId);
	//                this.midiModules["updateForm"].doCancel();
	            }, this
	        );
        }
        
        this.midiModules["updateForm"].doCancel();
    },

    doImport : function(item, e) {
        var module = this.midiModules["HypertensionMedicineImportList"];
        var list = this.list;
        var cfg = {};
        cfg.title = "药品情况";
        cfg.actions = [{id:"import",name:"导入",iconCls:"healthDoc_import"}];
        cfg.empiId = this.exContext.args.empiId;
        cfg.entryName = "chis.application.his.schemas.HIS_RecipeDetail";
        cfg.autoLoadData = false;
        cfg.autoLoadSchema = false;
        cfg.isCombined = false;
        cfg.showButtonOnTop = true;
        cfg.readOnly = this.readOnly;
        cfg.modal = true;
        if (!module) {
            $import("chis.application.hy.script.medicine.HypertensionMedicineImportList");
            module = new chis.application.hy.script.medicine.HypertensionMedicineImportList(cfg);
            module.on("import",this.onImport,this);
            this.midiModules["HypertensionMedicineImportList"] = module;
            list = module.initPanel();
            list.border = false;
            list.frame = false;
            this.list = list;
        }else{
            Ext.apply(module,cfg);
        }
        var win = module.getWin();
        win.add(list);
        win.show();
    },
    
    onImport:function(r){
        var data = {};
        data.phrId = this.phrId;
        data.medicineName = r.get("drugCode");
        data.medicineName_text =  r.get("drugName");
        data.medicineFrequency = r.get("frequency");
        data.totalCount = r.get("totalCount");
        data.medicineDate = r.get("createDate");
        data.medicineDosage = r.get("onesDose");
        data.useUnits = r.get("useUnits");
        
        this.onAddItem(data);
    },
    
    onEmpiIdChange : function(newEmpiId) {
        this.empiId = newEmpiId;
    },

    onPhrIdChange : function(newPhrId) {
        this.phrId = newPhrId;
    },

    onVisitIdChange : function(newVisitId) {
        this.visitId = newVisitId;
        this.requestData.cnd = ['eq', ['$', "visitId"], ['s', this.visitId]];
        this.refresh();
    },
    
    getMedicineList : function() {
		var array = [];
		for (var i = 0; i < this.store.getCount(); i++) {
			var r = this.store.getAt(i);
			array.push(r.data);
		}
		return array;
	},
	
	getMedicineRecordNum : function(){
		return this.store.getCount();
	},
	
    setBtnableByRecordSataus : function(){
    	var btns = this.grid.getTopToolbar().items;
		if(!btns.item(0)){
			return;
		}
		var rdStatus = this.exContext.ids.recordStatus;
		if(rdStatus && rdStatus == '1'){
			for(var i = 0;i<btns.getCount();i++){
				var btn = btns.item(i);
				if(btn){
					btn.disable();
				}
			}
		}
    },
    
    setBtnByRecordNum : function(){
    	var count = this.store.getCount();
        var btns = this.grid.getTopToolbar().items;
        if(count == 0){
        	if(btns.item(1)){
        		this.changeButtonStatus(btns.item(1),false);
        	}
        	if(btns.item(2)){
        		this.changeButtonStatus(btns.item(2),false);
        	}
        }else{
        	if(btns.item(1)){
        		this.changeButtonStatus(btns.item(1),true);
        	}
        	if(btns.item(2)){
        		this.changeButtonStatus(btns.item(2),true);
        	}
        }
    },
    
    getTopBtnControl : function(){
        var result = util.rmi.miniJsonRequestSync({
            serviceId : "chis.hypertensionVisitService",
            serviceAction : "getHyperVisitMedicineControl",
            method:"execute",
            body : {
                empiId : this.exContext.ids.empiId,
                phrId : this.exContext.ids.phrId,
                visitId : this.exContext.args.visitId
            }
        });
        if (result.code > 300) {
            this.processReturnMsg(result.code, result.msg, this.loadInitData);
            return;
        }
        return result.json.body._actions;
    }
});