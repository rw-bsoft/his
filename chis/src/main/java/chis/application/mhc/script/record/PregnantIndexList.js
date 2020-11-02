/**
 * 孕妇体检列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizEditorListView", "util.dictionary.DictionaryLoader")
chis.application.mhc.script.record.PregnantIndexList = function(cfg) {
  chis.application.mhc.script.record.PregnantIndexList.superclass.constructor.apply(this,
      [cfg]);
  this.disablePagingTbr = true;
  this.pageSize = 100;
  this.autoLoadData = false;
  this.autoLoadSchema = false;
  this.enableCnd = false;
  this.on("loadData", this.onLoadData, this);
  this.on("getStoreFields", this.onGetStoreFields, this);
}

Ext.extend(chis.application.mhc.script.record.PregnantIndexList, chis.script.BizEditorListView, {

  loadData : function() {
    this.clear();
    this.initDataId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
    if (!this.isVisitModule) {
      if (this.exContext.ids["MHC_PregnantRecord.pregnantId"]) {
        this.initCnd = [
            'and',
            [
                'eq',
                ['$', 'pregnantId'],
                [
                    's',
                    this.exContext.ids["MHC_PregnantRecord.pregnantId"]]],
            ['isNull',['$', 'visitId']]]
        this.requestData.cnd = this.initCnd
        this.requestData.order = "indexId"
        this.refresh();
      } else {
        this.getGridStore();
      }
    } else {
      if (this.exContext.args.visitId) {
        this.initCnd = [
            'and',
            [
                'eq',
                ['$', 'pregnantId'],
                [
                    's',
                    this.exContext.ids["MHC_PregnantRecord.pregnantId"]]],
            ['eq', ['$', 'visitId'],
                ['s', this.exContext.args.visitId]]]
        this.requestData.cnd = this.initCnd
        this.requestData.order = "indexId"
        this.refresh();
      } else {
        this.getGridStore();
      }
    }
    this.resetButtons();
  },

  getGridStore : function() {
    var cls = "util.dictionary.DictionaryLoader";
    var dic;
    if (!dic)
      dic = eval("(" + cls + ")");
    var store = dic.load({
          id : "chis.dictionary.pregnantIndex"
        });
    for (var j = 0; j < store.items.length; j++) {
      var storeItem = store.items[j];
      var items = this.schema.items
      var r = {};
      for (var i = 0; i < items.length; i++) {
        var it = items[i];
        if (it.id == "indexName") {
          r[it.id] = storeItem["text"];
        } else if (it.id == "indexCode") {
          r[it.id] = storeItem.key;
        } else if (it.id == "indexValue") {
          r[it.id] = storeItem["class"] || "";
        } else {
          r[it.id] = "";
        }
      }
      var record = new Ext.data.Record(r, storeItem["key"]);
      this.store.add(record);
    }
    this.store.commitChanges();
  },

  doSave : function(item, e) {
    this.fireEvent("recordSave");
  },

  refresh : function() {
    var cm = this.grid.getColumnModel()
    var c = cm.config[1]
    c.renderer = function(v, params, record, r, c, store) {
      var f = cm.getDataIndex(c)
      return record.get(f + "_text")
    }
    if (this.store) {
      this.store.load()
    }
  },

  beforeCellEdit : function(e) {
    var f = e.field
    var record = e.record
    var op = record.get("_opStatus")
    var cm = this.grid.getColumnModel()
    var c = cm.config[e.column]
    var enditor = cm.getCellEditor(e.column)
    var it = c.schemaItem
    if (it.id == "indexValue") {
      it.dic = null;
      cm.setEditor(e.column, this.createNormalField(it));
      c.renderer = function(v, params, record, r, c, store) {
        var f = cm.getDataIndex(c)
        if (record.get(f + "_text")) {
          return record.get(f + "_text")
        }
        return record.get(f)
      }
      var indexName = record.get("indexName");
      var dicColu1 = ["尿蛋白", "尿糖", "尿酮体"]
      for (var i = 0; i < dicColu1.length; i++) {
        var value1 = dicColu1[i];
        if (indexName == value1) {
          it.dic = {
            id : "chis.dictionary.indexValue1"
          };
          break;
        }
      }
      var dicColu2 = ["HBSAG", "HBSAB", "HBEAG", "HBEAB", "HBCAB",
          "梅毒血清学试验", "HIV抗体检测", "弓形虫", "巨细胞病毒", "风疹病毒"]
      for (var i = 0; i < dicColu2.length; i++) {
        var value2 = dicColu2[i];
        if (indexName == value2) {
          it.dic = {
            id : "chis.dictionary.indexValue2"
          };
          break;
        }
      }

      if (indexName == "阴道分泌物") {
        it.dic = {
          id : "chis.dictionary.indexValue3"
        };
      }

      if (indexName == "阴道清洁度") {
        it.dic = {
          id : "chis.dictionary.indexValue4"
        };
      }

    }
    if (it.dic) {
      cm.setEditor(e.column, this.createDicField(it.dic));
    
    }
    var ac = util.Accredit;
    if (op == "create") {
      if (!ac.canCreate(it.acValue)) {
        return false
      }
    } else {
      if (!ac.canUpdate(it.acValue)) {
        return false
      }
    }
    if (it.dic) {
      e.value = {
        key : e.value,
        text : record.get(f + "_text")
      };
    } else {
      e.value = e.value || ""
    }
    if (this
        .fireEvent("beforeCellEdit", it, record, enditor.field, e.value)) {
      return true
    }
  },

  afterCellEdit : function(e) {
    var f = e.field
    var v = e.value
    var record = e.record
    var cm = this.grid.getColumnModel()
    var c = cm.config[e.column]
    var it = c.schemaItem
    if (it.id == "indexValue") {
      it.dic = null;
      var indexName = record.get("indexName");
      var dicColu1 = ["尿蛋白", "尿糖", "尿酮体"]
      for (var i = 0; i < dicColu1.length; i++) {
        var value1 = dicColu1[i];
        if (indexName == value1) {
          it.dic = {
            id : "chis.dictionary.indexValue1"
          };
          break;
        }
      }
      var dicColu2 = ["HBSAG", "HBSAB", "HBEAG", "HBEAB", "HBCAB",
          "梅毒血清学试验", "HIV抗体检测", "弓形虫", "巨细胞病毒", "风疹病毒"]
      for (var i = 0; i < dicColu2.length; i++) {
        var value2 = dicColu2[i];
        if (indexName == value2) {
          it.dic = {
            id : "chis.dictionary.indexValue2"
          };
          break;
        }
      }

      if (indexName == "阴道分泌物") {
        it.dic = {
          id : "chis.dictionary.indexValue3"
        };
      }
      if (indexName == "阴道清洁度") {
        it.dic = {
          id : "chis.dictionary.indexValue4"
        };
      }
    }
    if (it.id == "ifException" && v) {
      var nextEnditor = cm.getCellEditor(e.column + 1, e.row)
      var nextField = nextEnditor.field;
      if (v == "1") {
        nextField.reset();
        nextField.disable();
      } else {
        nextField.enable();
      }
    }
    var enditor = cm.getCellEditor(e.column, e.row)
    var field = enditor.field
    if (it.dic) {
      record.set(f + "_text", field.getRawValue())
    }
    if (it.type == "date") {
      var dt = new Date(v)
      v = dt.format('Y-m-d')
      record.set(f, v)
    }
    this.fireEvent("afterCellEdit", it, record, field, v)
  },

  onGetStoreFields : function(fields) {
    fields.push([{
          name : "indexValue_text",
          type : "string"
        }, {
          name : "_opStatus"
        }]);
  },

  onLoadData : function(store) {
    var dataCount = store.getCount();
    if (dataCount < 1) {
      this.getGridStore();
      return;
    }
    var vClom = [];
    for (var i = 0; i < dataCount; i++) {
      var record = store.getAt(i);
      var indexName = record.get("indexName");
      var indexValue = record.get("indexValue");
      if (indexValue == "")
        continue;
      var dicColu1 = ["尿蛋白", "尿糖", "尿酮体"]
      var inList = false;
      var isExists = false;
      for (var k = 0; k < dicColu1.length; k++) {
        var value1 = dicColu1[k];
        if (indexName == value1) {
          inList = true;
          var dicName = {
            id : "chis.dictionary.indexValue1"
          };
          var dic = util.dictionary.DictionaryLoader.load(dicName);
          var items = dic.items
          var n = items.length
          for (var j = 0; j < n; j++) {
            var item = items[j]
            if (indexValue == item.key) {
              isExists = true;
              break;
            }
          }
        }
      }
      if (isExists) {
        continue;
      }
      var dicColu2 = ["HBSAG", "HBSAB", "HBEAG", "HBEAB", "HBCAB",
          "梅毒血清学试验", "HIV抗体检测", "弓形虫", "巨细胞病毒", "风疹病毒"]
      for (var k = 0; k < dicColu2.length; k++) {
        var value2 = dicColu2[k];
        if (indexName == value2) {
          inList = true;
          var dicName = {
            id : "chis.dictionary.indexValue2"
          };
          var dic = util.dictionary.DictionaryLoader.load(dicName);
          var items = dic.items
          var n = items.length
          for (var j = 0; j < n; j++) {
            var item = items[j]
            if (indexValue == item.key) {
              isExists = true;
              break;
            }
          }
        }
      }
      if (isExists) {
        continue;
      }
      if (indexName == "阴道分泌物") {
        inList = true;
        var dicName = {
          id : "chis.dictionary.indexValue3"
        };
        var dic = util.dictionary.DictionaryLoader.load(dicName);
        var items = dic.items
        var n = items.length
        for (var j = 0; j < n; j++) {
          var item = items[j]
          if (indexValue == item.key) {
            isExists = true;
          }
        }
      }
      if (isExists) {
        continue;
      }
      if (indexName == "阴道清洁度") {
        inList = true;
        var dicName = {
          id : "chis.dictionary.indexValue4"
        };
        var dic = util.dictionary.DictionaryLoader.load(dicName);
        var items = dic.items
        var n = items.length
        for (var j = 0; j < n; j++) {
          var item = items[j]
          if (indexValue == item.key) {
            isExists = true;
          }
        }
      }
      if (!isExists && inList) {
        var indexMes = {
          "indexName" : indexName,
          "indexValue" : indexValue
        }
        vClom.push(indexMes);
      }
    }
    var itemCount = vClom.length;
    var msg = "";
    if (itemCount > 0) {
      for (var i = 0; i < itemCount; i++) {
        var item = vClom[i];
        if (item != null && item != "" && item != undefined) {
          msg += item.indexName;
          msg += ":";
          msg += item.indexValue;
          if (i != itemCount - 1)
            msg += ",";
        }
      }
    }
    if (msg != "") {
      Ext.Msg.show({
            title : '错误信息',
            msg : "[" + msg + "]选项填写错误，请修改!",
            modal : true,
            width : 400,
            buttons : Ext.MessageBox.OK,
            multiline : false,
            scope : this
          });
      this.fireEvent("active", msg);
    }
  }
});