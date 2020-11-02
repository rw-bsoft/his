$package("chis.application.pub.script");

$import("chis.script.BizSimpleListView",
		"chis.application.pub.script.DrugImportModule",
		"chis.script.util.widgets.MyMessageTip");

chis.application.pub.script.PelpleHealthTeachList = function(cfg) {
	if(cfg.actions==undefined)
	{
		cfg.actions = [{
				id : "create",
				name : "新增",
				ref : "chis.application.pub.PUB/PHT/PHT01_01"
			},
			{
				id : "update",
				name : "查看",
				ref : "chis.application.pub.PUB/PHT/PHT01_01"
			}]
	}
	chis.application.pub.script.PelpleHealthTeachList.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.pub.script.PelpleHealthTeachList,
		chis.script.BizSimpleListView, {
			onReady : function() {
				this.recipeField.on("keydown", this.KeyUpField, this);
				chis.application.pub.script.PelpleHealthTeachList.superclass.onReady
						.call(this);
			},
			KeyUpField : function(field, e) {
				if (e.getKey() == e.ENTER) {
					e.stopEvent();
					this.doCndQuery();
					return
				}
			},
			fixRemoveCfg : function(removeCfg) {
				removeCfg.serviceId = "chis.simpleRemove"
			},
			fixRequestData : function(requestData) {
				requestData.serviceId = "chis.simpleQuery"
			},
			getSaveRequest : function(saveData) {
				return saveData;
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var title = r.id;
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.keys.length; i++) {
						title += r.get(this.schema.keys[i])
					}
				}
				Ext.Msg.show({
							title : '确认删除记录[' + title + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							},
							scope : this
						})
			},
			onSave : function(entryName, op, json, rec) {
				this.fireEvent("save", entryName, op, json, rec);
				if (op == "create") {
					this.recipeField.setValue();
					this.requestData.cnd = this.initCnd;
				}
				this.refresh()
			},
			getCndBar : function(items) {
				var recipeLabel = new Ext.form.Label({
							html : "健康处方拼音码："
						});
				var recipeField = new Ext.form.TextField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							name : "recipeName"
						});
				this.recipeField = recipeField;
				var queryBtn = new Ext.Button({
							text : "查询",
							iconCls : "query"
						});
				this.queryBtn = queryBtn;
				queryBtn.on("click", this.doCndQuery, this);
				return [recipeLabel, this.recipeField, '-', queryBtn]
			},

			doCndQuery : function() {
				var initCnd = this.initCnd;
				this.resetFirstPage();
				var recipe = this.recipeField.getValue();
				var cnd = ['and', ['eq', ['s', '1'], ['s', '1']],
						['eq', ['s', '1'], ['s', '1']]];
				if (recipe != null && recipe != "") {
					cnd.push(['like', ['$', 'recipeNamePy'],
							['s', '%' + recipe.toLowerCase() + '%']]);
				}
				this.queryCnd = cnd;
				if (initCnd) {
					cnd.push(initCnd)
				}
				this.requestData.cnd = cnd;
				this.refresh();
			}
		});