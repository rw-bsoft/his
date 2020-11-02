$package("chis.application.tr.script.sms")

$import("chis.script.BizSimpleListView")

chis.application.tr.script.sms.TumourQuestionnaireCriterionList = function(cfg) {
	chis.application.tr.script.sms.TumourQuestionnaireCriterionList.superclass.constructor
			.apply(this, [cfg]);
	this.removeServiceId = "chis.tumourCriterionService";
	this.removeAction = "removeTQCriterion";
	this.removeMethod = "execute";
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.sms.TumourQuestionnaireCriterionList,
		chis.script.BizSimpleListView, {
			loadData : function() {
				if (this.exContext.args) {
					var myCnd = ["eq", ['$', 'hrcId'],['s', this.exContext.args.hrcId || '']];
					var cndStr = $encode(this.requestData.cnd);
					if(this.requestData.cnd && cndStr.indexOf("hrcId") == -1){
						this.requestData.cnd = ['and', this.requestData.cnd, myCnd];
					}else{
						this.requestData.cnd = myCnd;
					}
				}
				chis.application.tr.script.sms.TumourQuestionnaireCriterionList.superclass.loadData
						.call(this);
			},
			onLoadData : function(){
				if(this.recordId){
					var hasCurrentPage = false;
					for(var i=0,len=this.store.getCount();i<len;i++){
						var r = this.store.getAt(i);
						if (r.get("recordId") == this.recordId) {
							this.grid.getSelectionModel().selectRecords([r]);
							var n = this.store.indexOf(r);
							if (n > -1) {
								this.selectedIndex = n;
								hasCurrentPage = true;
							}
							break;
						}
					}
					if(hasCurrentPage){
						this.selectRow(this.selectedIndex);
					}else{
						this.grid.getSelectionModel().clearSelections();
					}
				}
			},
			doCreateCriterion : function() {
				var module = this.createSimpleModule(
						"TumourQuestionnaireCriterionModule", this.refModule);
				module.initPanel();
				module.on("save", this.afterSave, this);
				module.initDataId = null;
				Ext.apply(module.exContext, this.exContext);
				module.exContext.control = {};
				this.showWin(module);
				module.doNew();
			},

			afterSave : function(entryName, op, json, data) {
				if(op=="create"){
					this.recordId = json.body.recordId;
				}else{
					this.recordId = data.tqcRecord.recordId;
				}
				this.refresh();
			},

			doModify : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var recordId = r.get("recordId");
				var QMId = r.get("QMId");
				var module = this.createSimpleModule(
						"TumourQuestionnaireCriterionModule", this.refModule);
				module.initPanel();
				module.on("save", this.afterSave, this);
				module.initDataId = null;
				module.exContext.control = {};
				module.exContext.args = {
					recordId : recordId,
					QMId : QMId
				};
				this.showWin(module);
				module.loadData();
			},

			onDblClick : function() {
				this.doModify();
			},

			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.mask("正在删除数据...");
				var pkey = r.id;
				if (this.isCompositeKey) {
					pkey = {};
					for (var i = 0; i < this.schema.keys.length; i++) {
						pkey[this.schema.keys[i]] = r.get(this.schema.keys[i]);
					}
				}
				var body = {
					pkey : r.id,
					QMId : r.get("QMId")
				};
				var removeCfg = {
					serviceId : this.removeServiceId,
					serviceAction : this.removeAction,
					method : this.removeMethod,
					pkey : pkey,
					body : body,
					schema : this.entryName,
					action : "remove", // 按钮标识
					module : this.grid._mId
					// 增加module的id
				}
				this.fixRemoveCfg(removeCfg);
				util.rmi.jsonRequest(removeCfg, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.updatePagingInfo()
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			},
			doCndQuery : function() {
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
					return;
				}
				this.resetFirstPage()
				var f = this.cndField;
				var v = f.getValue()
				var rawV = f.getRawValue();
				var xtype = f.getXType();
				if ((Ext.isEmpty(v) || Ext.isEmpty(rawV))
						&& (xtype !== "MyRadioGroup" && xtype !== "MyCheckboxGroup")) {
					this.queryCnd = null;
					this.requestData.cnd = initCnd
					this.refresh()
					return
				}
				if (f.getXType() == "datefield") {
					v = v.format("Y-m-d")
				}
				if (f.getXType() == "datetimefield") {
					v = v.format("Y-m-d H:i:s")
				}
				// 替换'，解决拼sql语句查询的时候报错
				if(typeof v=="string"){
					v = v.replace(/'/g, "''")
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					var expType = this.getCndType(it.type)
					if (it.dic.render == "Tree") {
						var node = this.cndField.selectedNode;
						if (!node || node.isLeaf()) {
							cnd.push([expType, v]);
						} else {
							cnd[0] = 'like'
							cnd.push([expType, v + '%'])
						}
					} else {
						cnd.push([expType, v])
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
//							if (v.format) {
//								v = v.format("Y-m-d")
//							}
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
						case 'datetime' :
						case 'timestamp' :
							if (it.xtype == "datefield") {
								//v = v.format("Y-m-d")
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd')"]
								cnd.push(['s', v])
							} else {
								//v = v.format("Y-m-d H:i:s")
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd HH24:mi:ss')"]
								cnd.push(['s', v])
							}
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				this.requestData.cnd = cnd
				this.refresh()
			}

		});