$package("chis.application.scm.item.script")
$import("chis.script.BizEditorListView")

chis.application.scm.item.script.HisServicePackageItemsListRight = function(cfg) {
    chis.application.scm.item.script.HisServicePackageItemsListRight.superclass.constructor
        .apply(this, [cfg]);
    this.requestData.cnd=['eq',['$','a.SPID'],['s',this.SPID||"null"]];
    this.on("loadData", this.getLeftCnd, this)
}

Ext.extend(chis.application.scm.item.script.HisServicePackageItemsListRight,
    chis.script.BizEditorListView, {
        loadData : function() {
            chis.application.scm.item.script.HisServicePackageItemsListRight.superclass.loadData
                .call(this);
        },
        onDblClick : function(grid, index, e) {
            var r = this.grid.store.getAt(index);
            if (!r) {
                return
            }

            r.data.isNew = 0;
            r.data.LOGOFF && r.data.LOGOFF == 1 ? this.doLogOn(r) : this
                .doLogOff(r)
        },
        doLogOn : function(r) {
            util.rmi.jsonRequest({
                serviceId : "chis.signContractRecordService",
                method : "execute",
                serviceAction : "logOnPackagetoServiceItem",
                body : r.data
            }, function(code, msg, json) {
                if (code > 300) {
                    return
                } else {
                    this.refresh()
                }
            }, this);
        },
        doLogOff : function(r) {
            util.rmi.jsonRequest({
                serviceId : "chis.signContractRecordService",
                method : "execute",
                serviceAction : "logOffPackagetoServiceItem",
                body : r.data
            }, function(code, msg, json) {
                if (code > 300) {
                    return
                } else {
                    this.refresh()
                }
            }, this);
        },
        doClear : function(r) {
            util.rmi.jsonRequest({
                serviceId : "chis.signContractRecordService",
                method : "execute",
                serviceAction : "logOffPackagetoServiceItem",
                body : r.data
            }, function(code, msg, json) {
                if (code > 300) {
                    return
                }
            }, this);
            this.store.remove(r)
        },
        addCol : function(r) {
            debugger;
            // this.SPID = this.opener.opener.SPID;
            r.data.itemCode = this.SPID + r.data.FYXH;
            r.data.parentCode = this.SPID;
            r.data.itemName = r.data.FYMC;
            r.data.isBottom = 'y';
            r.data.ITEMNATURE = 'y';
            r.data.isBottom_text = '是';
            r.data.itemNature = '2';
            r.data.itemNature_text = '增值项目';
            r.data.serviceTimes = 1;// 业务暂时默认全为1
            r.data.isNew = 1;
            r.data.discount=70;
            r.data.price = r.data.FYDJ;
            r.data.SPID = this.SPID;
            r.data.realPrice=Math.ceil(r.data.price*r.data.discount)/100;
            r.data.LOGOFF = 0;
            r.data.fyxh = r.data.FYXH;
            this.store.add(r)
        },
        afterCellEdit : function(e) {
            debugger;
            var f = e.field;
            var v = e.value;
            var record = e.record;
            var cm = this.grid.getColumnModel();
            var enditor = cm.getCellEditor(e.column, e.row);
            var c = cm.config[e.column];
            var it = c.schemaItem;
            var field = enditor.field;
            if (it.dic) {
                record.set(f + "_text", field.getRawValue())
            }
            if (it.type == "date") {
                var dt = new Date(v);
                v = dt.format('Y-m-d');
                record.set(f, v)
            }
            if (it.codeType)
                record.set(f, v.toUpperCase());
            if(f == "discount"){
                record.set("realPrice",Math.ceil(record.get("price")*v)/100);
            }
            if (this.CodeFieldSet) {
                var bField = {};
                for (var i = 0; i < this.CodeFieldSet.length; i++) {
                    var CodeField = this.CodeFieldSet[i];
                    var target = CodeField[0];
                    var codeType = CodeField[1];
                    var srcField = CodeField[2];
                    if (it.id == target) {
                        if (!bField.codeType)
                            bField.codeType = [];
                        bField.codeType.push(codeType);
                        if (!bField.srcField)
                            bField.srcField = [];
                        bField.srcField.push(srcField);
                    }
                }
                this.fireEvent("afterCellEdit", it, record, field, v)
            }
        },
        getLeftCnd : function() {
            var records = this.getStoreData();
            var itemCodes = [];

            for (var i in records) {
                var record = records[i];
                if (record.fyxh)
                    itemCodes.push(record.fyxh)
            }
            // todo 增加原始FYXH用于标记 取消此处的左右notein
            // if (itemCodes.length > 0)
            //     this.opener.leftList.requestData.cnd = ['and',
            //         ['eq', ['$', 'a.itemType'], ['s', '4']],
            //         ['notin', ['$', 'a.itemCode'], itemCodes]];
            // else
            //     this.opener.leftList.requestData.cnd = ['eq',
            //         ['$', 'a.itemType'], ['s', '4']];
            // if(itemCodes.length > 0){
                // this.opener.leftList.requestData.cnd = ['notin', ['$', 'a.itemCode'], itemCodes];
            // }
            this.opener.leftList.loadData();

        },
        doSave : function() {
            debugger
            if(!this.SPID || this.SPID == "null" || this.SPID == "")
                return MyMessageTip.msg("提示", "请先填写保存服务包信息！", true);
            var records = this.getStoreData();
            util.rmi.jsonRequest({
                serviceId : "chis.signContractRecordService",
                method : "execute",
                serviceAction : "saveHisServiceItem",
                body : records
            }, function(code, msg, json) {
                if (code > 300) {
                    return
                } else {
                    this.refresh()
                }

            }, this);
            for (i in records) {
                var record = records[i];
                if (record.isNew)
                    record.isNew = 0;
            }

        },
        getStoreData : function() {
            var count = this.store.getCount();
            var lst = [];
            for (var i = 0; i < count; i++) {
                var record = this.store.getAt(i);
                lst.push(record.data);
            }
            return lst;
        }
    });