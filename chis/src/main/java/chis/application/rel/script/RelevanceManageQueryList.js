$package("chis.application.rel.script")

$import("chis.script.BizSimpleListView")

chis.application.rel.script.RelevanceManageQueryList = function(cfg) {
	chis.application.rel.script.RelevanceManageQueryList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.rel.script.RelevanceManageQueryList,
		chis.script.BizSimpleListView, {
			loadData : function() {
				var fda;
				if (this.cndField.getValue()) {
					fda = this.cndField.getValue().key;
					if (!fda) {
						fda = this.cndField.getValue();
					}
				} else {
					if (this.mainApp.jobtitleId != "gp.101") {
						return;
					}
					fda = this.mainApp.uid;
				}
				this.requestData.cnd = ['eq',['$','fda'],['s',fda]];
				chis.application.rel.script.RelevanceManageQueryList.superclass.loadData
						.call(this);
			},

			getCndBar : function(items) {
				var cndField = this.createDicField();
				this.cndField = cndField
				var queryBtn = new Ext.Toolbar.Button({
							iconCls : "query",
							text : "查询"
						})
				this.queryBtn = queryBtn;
				queryBtn.on("click", this.loadData, this);
				return ['助理医生:', cndField, queryBtn]
			},

			createDicField : function() {
				var defaultValue = null;
				if (this.mainApp.jobtitleId == "gp.101") {
					defaultValue = {
						key : this.mainApp.uid,
						text : this.mainApp.logonName + "--"
								+ this.mainApp.jobtitle
					}
				}
				var dic = {
					id : "gp.dictionary.user101",
					defaultValue : defaultValue,
					width : 200,
					parentKey : "%user.manageUnit.id",
					onlySelectLeaf : "true"
				}
				var cls = "util.dictionary.TreeDicFactory";
				$import(cls)
				var factory = eval("(" + cls + ")")
				var field = factory.createDic(dic)
				field.on("select", this.loadData, this);
				return field
			}
		})