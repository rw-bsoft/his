$package("chis.application.per.script.dictionary");

$import("chis.script.BizTreeNavListView");

chis.application.per.script.dictionary.CheckupDictionaryList = function(cfg) {
	chis.application.per.script.dictionary.CheckupDictionaryList.superclass.constructor.apply(
			this, [cfg]);
	this.removeServiceId = "chis.checkupDictionaryService";
	this.removeAction = "delDicItem";
};

Ext.extend(chis.application.per.script.dictionary.CheckupDictionaryList,
		chis.script.BizTreeNavListView, {
			warpPanel : function(grid) {
				if (!this.showNav) {
					return grid;
				}
				// var navDic = this.navDic;
				// @@ 如果没有父节点编码，this.navParentKey会是%user.prop.regionCode
				// @@ 这个时候不需要再显示父一级节点，否则会出现空的顶层节点。
				this.rootVisible = this.navParentKey
						&& ("" + this.navParentKey).charAt(0) != '%'
						? this.rootVisible
						: false;
				this.parentText;
				if (this.navDic == "chis.@manageUnit") {
					parentText = this.mainApp.dept;
				} else if (this.navDic == "chis.dictionary.areaGrid") {
					parentText = this.mainApp.regionText;
				}
				var tf = util.dictionary.TreeDicFactory.createDic({
							dropConfig : {
								ddGroup : 'gridDDGroup',
								notifyDrop : this.onTreeNotifyDrop,
								scope : this
							},
							id : this.navDic,
							parentKey : this.navParentKey,
							parentText : this.parentText,
							rootVisible : this.rootVisible || false
						});
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							items : [{
										layout : "fit",
										split : true,
										collapsible : true,
										title : '',
										region : 'west',
										width : this.westWidth,
										items : tf.tree
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : grid
									}]
						});
				this.tree = tf.tree;
				grid.__this = this;
				tf.tree.on("click", this.onCatalogChanage, this);
				// tf.tree.on("contextmenu", this.onAreaGridTreeContextmenu,
				// this);
				// this.warpPanel = panel
				tf.tree.expand();
				return panel;
			},

			onCatalogChanage : function(node, e) {
				var navField = this.navField;
				var initCnd = this.initCnd;
				var queryCnd = this.queryCnd;

				var cnd;
				if (node.leaf) {
					cnd = ['eq', ['$', navField], ['s', node.id]];
				} else {
					cnd = ['like', ['$', navField], ['s', node.id + '%']];
				}

				this.navCnd = cnd;
				if (initCnd || queryCnd) {
					cnd = ['and', cnd];
					if (initCnd) {
						cnd.push(initCnd);
					}
					if (queryCnd) {
						cnd.push(queryCnd);
					}
				}
				this.resetFirstPage();
				this.requestData.cnd = cnd;
				this.refresh();
			}
});