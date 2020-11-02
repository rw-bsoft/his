/**
 * 孕妇高危因素整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.visit")
$import("chis.script.BizCombinedModule2")
chis.application.mhc.script.visit.HighRiskVisitReasonModule = function(cfg) {
	chis.application.mhc.script.visit.HighRiskVisitReasonModule.superclass.constructor.apply(
			this, [cfg])
	this.width = 780;
	this.height = 400
	this.itemWidth = 300 // ** 第一个Item的宽度
	this.itemHeight = 350 // ** 第一个Item的高度
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.mhc.script.visit.HighRiskVisitReasonModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.mhc.script.visit.HighRiskVisitReasonModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("confirm", this.onListConfirm, this)
				this.grid = this.list.grid;
				this.grid.setAutoScroll(false);
				return panel;
			},

			getFirstItem : function() {
				var tf = util.dictionary.TreeDicFactory.createDic({
							id : "chis.dictionary.highRiskScore",
							onlySelectLeaf : true,
							autoScroll : true
						})
				var tree = tf.tree;
				tree.region = this.layOutRegion;
				tree.title = " "
				tree.split = true
				tree.width = this.itemWidth;
				tree.height = this.itemHeight;
				tree.on("click", this.onLeafClick, this)
				this.tree = tree;
				return this.tree;
			},

			getSecondItem : function() {
				return this.getCombinedModule(this.actions[0].id, this.actions[0].ref);
			},

			onWinShow : function() {
				if (this.exContext.args.initRisknesses && this.exContext.args.initRisknesses.length > 0) {
					this.initHighRiskness();
				}else{
					this.loadData();
				}
			},

			onListConfirm : function(record) {
				this.fireEvent("moduleClose", record, this.list.store)
				this.getWin().hide()
			},

			onLeafClick : function(node, e) {
				if (!node.leaf || node.parentNode == null) {
					return
				}
				var store = this.list.store
				var id = node.id
				var text = node.text
				var group = node.attributes["group"]
				var frequence = node.attributes["frequence"]
				var score = this.getScore(group);

				if (this.checkWeek(id)) {
					return
				}
				var data = []
				data["highRiskReasonId"] = id
				data["highRiskReasonId_text"] = text
				data["highRiskScore"] = score
				data["highRiskLevel"] = group
				data["frequence"] = frequence
				var record = new Ext.data.Record(data)

				if (store) {
					var length = store.data.length
					for (var i = 0; i < length; i++) {
						if (id == store.getAt(i).get("highRiskReasonId")) {
							this.list.selectRow(i)
							return
						}
					}
					if (this.checkIfConflict(store, id)) {
						return
					}
					store.add(record);
				}
			},

			getScore : function(group) {
				if (group == "A") {
					return 5;
				} else if (group == "B") {
					return 10;
				} else {
					return 20;
				}
			},

			loadData : function() {
				if (this.__actived) {
					return;
				}
				this.tree.collapseAll()
				this.refreshExContextData(this.list, this.exContext);
				this.list.loadData();
			},

			checkIfConflict : function(store, id) {
				var length = store.data.length
				for (var i = 0; i < length; i++) {
					var storeId = store.getAt(i).get("highRiskReasonId")
					var text = store.getAt(i).get("highRiskReasonId_text")
					var cutId = id.slice(1)
					var cutStoreId = storeId.slice(1)
					if (cutId == cutStoreId) {
						Ext.Msg.alert("提示信息", "已选择高危因素：" + text)
						this.list.selectRow(i)
						return true
					}
				}
			},

			checkWeek : function(id) {
				var w = this.exContext.args.week
				if (w < 32 && id.slice(1) == "30201") {
					Ext.Msg.alert("提示信息", "孕周未到32周")
					return true
				}
				if (w < 34 && id == "a30301") {
					Ext.Msg.alert("提示信息", "孕周未到34周")
					return true
				}
				if ((w < 34 || w > 37) && id == "a30401") {
					Ext.Msg.alert("提示信息", "孕周未到34周或者超过37周")
					return true
				}
				if (w > 34 && (id == "b30301" || id == "b30401")) {
					Ext.Msg.alert("提示信息", "孕周超过34周")
					return true
				}
				if (w < 41 && id == "a30501") {
					Ext.Msg.alert("提示信息", "孕周未到41周")
					return true
				}
				if (w < 42 && id == "b30501") {
					Ext.Msg.alert("提示信息", "孕周未到42周")
					return true
				}
			},

			initHighRiskness : function() {
				this.list.resetButtons();
				this.list.store.removeAll();
				if (this.exContext.args.initRisknesses.length == 0) {
					return;
				}
				var dic = util.dictionary.DictionaryLoader.load({
							id : "chis.dictionary.highRiskScore"
						});
				var dicWraper = dic["wraper"];
				for (var i = 0; i < this.exContext.args.initRisknesses.length; i++) {
					var risk = this.exContext.args.initRisknesses[i];
					var group = dicWraper[risk].group;
					this.pack(risk, dicWraper[risk].text, this.getScore(group),
							group, dicWraper[risk].frequence);
				}
				delete this.exContext.args.initRisknesses;
			},

			pack : function(id, text, score, group, frequence) {
				var data = []
				data["highRiskReasonId"] = id
				data["highRiskReasonId_text"] = text
				data["highRiskScore"] = score
				data["highRiskLevel"] = group
				data["frequence"] = frequence
				var record = new Ext.data.Record(data, id)
				this.list.store.add(record)
			},

			getWin : function() {
				var win = this.win
				var closeAction = "hide"
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width || 800,
								height : this.height || 450,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								constrain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : true,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.win.doLayout()
								this.fireEvent("winShow", this)
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			}
		})