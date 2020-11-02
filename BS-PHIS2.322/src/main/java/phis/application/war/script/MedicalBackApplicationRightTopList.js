$package("phis.application.war.script")

$import("phis.script.EditorList")

phis.application.war.script.MedicalBackApplicationRightTopList = function(cfg) {
	cfg.gridDDGroup = "secondGridDDGroup";
	cfg.autoLoadData = true;
	phis.application.war.script.MedicalBackApplicationRightTopList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this);
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}

Ext.extend(phis.application.war.script.MedicalBackApplicationRightTopList,
		phis.script.EditorList, {
			// 左往右拖 执行数据操作
			addRecord : function(data) {
				// 查询可退数量
				var body = {};
				body["lybq"] = this.mainApp['phis'].wardId;
				body["zyh"] = data["ZYH"];
				body["ypxh"] = data["YPXH"];
				body["ypcd"] = data["YPCD"];
				body["yzxh"] = data["YZXH"];
				body["ypdj"] = data["YPDJ"];
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.addRecord);
					return;
				}
				var ktsl = ret.json.ktsl;
				if (ktsl <= 0) {
					Ext.Msg.alert("提示", "无可退数量");
					return;
				}
				count = this.store.getCount();
				for (var i = 0; i < count; i++) {
					var mx = this.store.getAt(i).data;
					if (data["YPXH"] == mx["YPXH"]
							&& data["YPDJ"] == mx["YPJG"]
							&& data["YPCD"] == mx["YPCD"]
							&& data["ZFBL"] == mx["ZFBL"]
							&& data["YZXH"] == mx["YZID"]) {
						var ybcsl = 0;
						// 防止2条以上相同的药品和医嘱的退药记录被退回时数量叠加超过总数量的问题
						for (var j = i + 1; j < count; j++) {
							var mxt = this.store.getAt(j).data;
							if (mxt["YPXH"] == mx["YPXH"]
									&& mxt["YPJG"] == mx["YPJG"]
									&& mxt["YPCD"] == mx["YPCD"]
									&& mxt["ZFBL"] == mx["ZFBL"]
									&& mxt["YZID"] == mx["YZID"]) {
								ybcsl += mxt["YPSL"];
							}
						}
						var ypsl = mx["YPSL"] + 1;
						if (mx["YPSL"] + 1+ybcsl > ktsl) {
							ypsl = ktsl-ybcsl;
						}
						this.store.getAt(i).set("YPSL", ypsl);
						this.store.getAt(i).set("KTSL", ktsl);
						this.fireEvent("leftLoad", 2);
						return;
					}
				}
				data["TYBQ"] = data["LYBQ"];
				data["TJBZ"] = 0;
				data["YZID"] = data["YZXH"];
				data["YPJG"] = data["YPDJ"];
				data["YPSL"] = ktsl >= 1 ? 1 : ktsl;
				data["KTSL"] = ktsl;
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items);
				var Record = Ext.data.Record.create(o.fields);
				var r = new Record(data);
				store.add([r]);
				this.fireEvent("leftLoad", 2);
				if(!this.addnum){
					this.addnum = 1;
				}
			},
			expansion : function(cfg) {
				cfg.sm = new Ext.grid.RowSelectionModel();
			},
			loadData : function(zyh) {
				if (zyh == null || zyh == "" || zyh == undefined) {
					return;
				}
				this.requestData.cnd = [
						'and',
						['isNull', ['$', 'a.TYRQ']],
						['and', ['eq', ['$', 'a.ZYH'], ['d', zyh]],
								['eq', ['$', 'a.TJBZ'], ['i', 0]]]];
				phis.application.war.script.MedicalBackApplicationRightTopList.superclass.loadData
						.call(this);
			},
			// 数据加载后改变按钮状态
			onLoadData : function(store) {
				if (store.getCount() > 0) {
					this.fireEvent("leftLoad", 1);
					for (var i = 0; i < store.getCount(); i++) {
						store.getAt(i).set("YPSL",
								-(store.getAt(i).data["YPSL"]));
					}
				} else {
					this.fireEvent("leftLoad", 0);
				}
				this.addnum = 0;
			},
			// 编辑完数量后提交按钮变灰,保存变亮
			onAfterCellEdit : function(it, record, field, v, oldValue) {
				var ktsl = record.get("KTSL");
				if (ktsl == null || ktsl == "" || ktsl == undefined) {
					var body = {};
					body["lybq"] = this.mainApp['phis'].wardId;
					body["zyh"] = record.get("ZYH");
					body["ypxh"] = record.get("YPXH");
					body["ypcd"] = record.get("YPCD");
					body["yzxh"] = record.get("YZID");
					body["ypdj"] = record.get("YPJG");
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryActionId,
								body : body
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg,
								this.beforeCellEdit);
						return;
					}
					ktsl = ret.json.ktsl;
				}
				var count = this.store.getCount();
				var sumNum = 0;
				for (var i = 0; i < count; i++) {
					var data = this.store.getAt(i).data;
					if (data["YPXH"] == record.get("YPXH")
							&& data["YPJG"] == record.get("YPJG")
							&& data["YPCD"] == record.get("YPCD")
							&& data["ZFBL"] == record.get("ZFBL")
							&& data["YZID"] == record.get("YZID")) {
						sumNum += data["YPSL"];
					}
				}
				if (sumNum > ktsl) {
					MyMessageTip.msg("提示", "退药数量大于已发数量", true);
					var otherNum=0
					for(var i = 0; i < count; i++){
						if (data["YPXH"] == record.get("YPXH")
							&& data["YPJG"] == record.get("YPJG")
							&& data["YPCD"] == record.get("YPCD")
							&& data["ZFBL"] == record.get("ZFBL")
							&& data["YZID"] == record.get("YZID")&&data["JLXH"]!=record.get("JLXH")) {
						otherNum += data["YPSL"];
					}
					}
					v = ktsl-otherNum;
				}
				record.set("YPSL", v);
				this.fireEvent("leftLoad", 2);
				if(!this.addnum){
					this.addnum = 1;
				}
			},
			onStoreBeforeLoad:function(store,op){
				var r = this.getSelectedRecord()
				var n = this.store.indexOf(r)
				if(n > -1){
					this.selectedIndex = n
				}
				if(this.addnum){
					Ext.Msg.show({
						   title: '确认刷新记录',
						   msg: '刷新后，新增加还未保存的数据将消失，是否继续?',
						   modal:true,
						   width: 300,
						   buttons: Ext.MessageBox.OKCANCEL,
						   multiline: false,
						   fn: function(btn, text){
						   	 if(btn == "ok"){
						   		this.addnum = 0;
						   		this.pagingToolbar.doLoad(0);
						   	 }
						   },
						   scope:this
						});
					return false;
				}
			},
			afterCellEdit : function(e) {
				var f = e.field
				var v = e.value
				var oldValue = e.originalValue
				var record = e.record
				var cm = this.grid.getColumnModel()
				var enditor = cm.getCellEditor(e.column, e.row)
				var c = cm.config[e.column]
				var it = c.schemaItem
				var field = enditor.field
				if (it.dic) {
					record.set(f + "_text", field.getRawValue())
				}
				if (it.type == "date") {
					var dt = new Date(v)
					v = dt.format('Y-m-d')
					record.set(f, v)
				}
				if (it.codeType)
					record.set(f, v.toUpperCase())
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
					if (bField.srcField) {
						var body = {};
						body.codeType = bField.codeType;
						body.value = v;
						var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : "codeGeneratorService",
									serviceAction : "getCode",
									body : body
								});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.saveToServer, [saveData]);
							return;
						}
						for (var i = 0; i < bField.codeType.length; i++) {
							record
									.set(bField.srcField[i],
											eval('ret.json.body.'
													+ bField.codeType[i]));
						}
					}
				}
				// add by yangl 回写未修改下的中文名称
				if (field.isSearchField) {
					var patrn = /^[a-zA-Z]+$/;
					if ((v.trim() == "" || patrn.exec(v))
							&& this.beforeSearchQuery()) {
						record.set(f, record.modified[f]);
					} else {
						record.modified[f] = v;
					}
				}
				this.fireEvent("afterCellEdit", it, record, field, v, oldValue)
			},
		onRendererNull : function(value, metaData, r) {
				if(value==null||value=="null"){
				return "";}else{
				return value;
				}
			}
		});