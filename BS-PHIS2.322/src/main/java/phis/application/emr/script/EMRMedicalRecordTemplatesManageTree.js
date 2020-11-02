$package("phis.application.emr.script")
$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory","phis.script.widgets.TreeLoader")
phis.application.emr.script.EMRMedicalRecordTemplatesManageTree = function(cfg) {
	this.checkModel = 'cascade';// 'multiple':多选; 'single':单选; 'cascade':级联多选
	this.checkTree = false;// 是否构建可以选择的树
	this.showDepth = 1; // 显示的节点层数,0表示不限制
	phis.application.emr.script.EMRMedicalRecordTemplatesManageTree.superclass.constructor.apply(this, [cfg])
}
Ext.extend(phis.application.emr.script.EMRMedicalRecordTemplatesManageTree,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				var id = 'root';
				var text = '病历类别';
				var rootNode = new Ext.tree.AsyncTreeNode({
							id : id,
							text : text,
							attributes : {
								isRoot : true
							}
						});
				var uiProvider = "";
				if (this.checkTree && this.checkTree != 'false') {
					$import("org.ext.ux.TreeCheckNodeUI");
					uiProvider = Ext.tree.TreeCheckNodeUI;
				}
				this.requestData = {
					serviceId : "phis.searchService",
					serviceAction : "loadDicData",
					method : "execute",
					className : "TreeLoad"
				}
				var url = ClassLoader.serverAppUrl || "";
				var tree = new Ext.tree.TreePanel({
							root : rootNode,
							border : true,
							onlyLeafCheckable : false,
							rootVisible : true,
							autoScroll : true,
							animate : false,// 是否动画
							enableDD : true,// 是否支持拖放
							containerScroll : true,
							lines : true,
							checkModel : this.checkModel,
							loader : new phis.script.widgets.TreeLoader({
										dataUrl : url + '*.jsonRequest',
										requestMethod : "POST",
										jsonData : this.requestData,
										baseAttrs : {
											uiProvider : uiProvider
										}
									})
						});
				// 根据当前节点判断节点下数据获取方式
				tree.getLoader().on("load", this.onTreeLoad, this);
				tree.on('beforeload', this.beforeLoad, this);
				this.tree = tree
				tree.on("click", this.onNodeClick, this);
				rootNode.on("expand", function(node) {
					// if (node.hasChildNodes()) {
					// node.firstChild.select();
					// this.fireEvent("treeClick", node.firstChild.id);
					// } else {
					node.select();
					this.fireEvent("treeClick", node);
						// }
					}, this);
				// this.warpPanel = panel
				rootNode.expand();
				return tree
			},
			/**
			 * 重写该方法以实现从多张表获取数据
			 * 
			 * @param {}
			 *            node
			 */
			beforeLoad : function(node) {
				// queryFields
				// 按顺序，第一，二个参数默认为id和text，其余属性设置到node的attributes中
				if (this.showDepth && node.getDepth() == this.showDepth) {
					this.tree.loader.baseParams.leaf = '1';
				}else{
					this.tree.loader.baseParams.leaf = 'false'
				}
				this.tree.loader.baseParams.queryFields = "LBBH,LBMC,SJLBBH";
				// 查询的表名，可根据节点动态设置所要访问的表
				this.tree.loader.baseParams.entryName = 'EMR_KBM_BLLB';
				this.tree.loader.baseParams.orderBy = 'ZYPLXH';
				// 查询条件
				var cnd;
				if (node.id != "root") {
					cnd = "['and',['eq',['$','MLBZ'],['i',0]],['eq',['$','SJLBBH'],['i',"
							+ node.id + "]]]";
				} else {
					cnd = "['eq',['$','MLBZ'],['i',1]]";
				}
				this.tree.loader.baseParams.condition = cnd;
			},
			onTreeLoad : function(loader, node) {
				if (node) {
					if (node.getDepth() == 0) {
						if (node.hasChildNodes()) {
							node.firstChild.expand();
						}
					}
				}
			},
			/**
			 * 重写改方法实现点击事件
			 * 
			 * @param {}
			 *            node
			 * @param {}
			 *            e
			 */
			onNodeClick : function(node, e) {
//				 console.debug(node)
				// attributes中自定义的属性需要用如下方式获取。不知道为什么，需要在第二个attributes中才能取到
				// alert(node.attributes.id)
				// 刷新list
				this.fireEvent("treeClick", node);
			}
		})
