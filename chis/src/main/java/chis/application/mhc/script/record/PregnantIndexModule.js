/**
 * 孕妇体检整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizCombinedModule2")
chis.application.mhc.script.record.PregnantIndexModule = function(cfg) {
	cfg.autoLoadData = false
	chis.application.mhc.script.record.PregnantIndexModule.superclass.constructor.apply(this,
			[cfg])
	this.width = 780;
	this.height = 430
	this.itemWidth = 140 // ** 第一个Item的宽度
	this.itemHeight = 350 // ** 第一个Item的高度
	this.on("winShow", this.onWinShow, this)
	this.on("beforeLoadModule", this.beforeLoadModule, this)
}
Ext.extend(chis.application.mhc.script.record.PregnantIndexModule, chis.script.BizCombinedModule2,
		{
    		
			initPanel : function() {
				var panel = chis.application.mhc.script.record.PregnantIndexModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("recordSave", this.onFormSave, this);
				this.list.on("active", this.onActiveModule, this);
				this.grid = this.list.grid;
				return panel;
			},

			beforeLoadModule : function(moduleName, cfg) {
				if (moduleName == this.actions[0].id) {
					cfg.isVisitModule = this.isVisitModule;
				}
			},

			getFirstItem : function() {
				var tf = util.dictionary.TreeDicFactory.createDic({
							id : "chis.dictionary.pregnantIndex",
							onlySelectLeaf : "true"
						})
				var tree = tf.tree;
				tree.region = this.layOutRegion;
				tree.title = " "
				tree.split = true
				tree.width = this.itemWidth;
				tree.height = this.itemHeight;
				tree.on("click", this.onTabActive, this)
				this.tree = tree;
				return this.tree;
			},

			getSecondItem : function() {
				return this.getCombinedModule(this.actions[0].id,
						this.actions[0].ref);
			},

			onFormSave : function() {
				this.fireEvent("recordSave");
			},

			onActiveModule : function() {
				this.fireEvent("activeIndexModle");
			},

			onTabActive : function(node, e) {
				if (!node.leaf || node.parentNode == null) {
					return;
				}
				var id = node.id;
				var text = node.text;
				this.selectOneRow(text);
			},

			selectOneRow : function(text) {
				var index = this.list.store.find('indexName', text);
				if (index > -1) {
					this.grid.getSelectionModel().select(index, 0);
					this.grid.getView().focusRow(index);
				}
			},

			loadData : function() {
				if (this.__actived) {
					return;
				}
				this.refreshExContextData(this.list, this.exContext);
				if (!this.isVisitModule) {
					this.list.exContext.args.visitId = null;
				} else {
					this.list.exContext.args.visitId = this.exContext.args.visitId;
				}
				this.list.loadData();
			},

			reset : function() {
				this.list.store.removeAll();
			},

			getIndexData : function() {
				var record = [];
				for (var j = 0; j < this.list.store.getCount(); j++) {
					var storeItem = this.list.store.getAt(j);
					var items = this.list.schema.items
					var r = {};
					for (var i = 0; i < items.length; i++) {
						var it = items[i];
         
						if (it.id == "empiId") {
							r[it.id] = this.exContext.ids.empiId
						} else if (it.id == "pregnantId") {
							r[it.id] = this.exContext.ids["MHC_PregnantRecord.pregnantId"]
						} else if (it.id == "visitId") {
							r[it.id] = this.exContext.args.visitId || "";
						} else {
							r[it.id] = storeItem.get(it.id);
						}
                    
					}
					record.push(r);
				}
				this.list.store.commitChanges();
				return record;
			},

			getSaveData : function() {
				var values = this.getIndexData();
				if (!values || values.length < 1) {
					return null;
				} else {
					return values;
				}
			},

			onWinShow : function() {
				this.loadData();
			}
		});