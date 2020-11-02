$package("phis.application.cic.script")

$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory",
		"phis.script.widgets.TreeLoader")
phis.application.cic.script.BLSXXDModule = function(cfg) {
	this.checkModel = 'cascade';// 'multiple':多选; 'single':单选; 'cascade':级联多选
	phis.application.cic.script.BLSXXDModule.superclass.constructor
			.apply(this, [cfg])

}
Ext.extend(phis.application.cic.script.BLSXXDModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if(this.panel){
				return this.panel}
				var uiProvider = "";
				var rootNode = new Ext.tree.AsyncTreeNode({
							id : "root",
							text : "病历书写向导",
							attributes : {
								isRoot : true
							}
						});
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
							rootVisible : this.rootVisible != 'false',
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
				this.form=new Ext.form.TextArea();
				this.panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : 500,
							height : 500,
							items : [{
										title : '',
										layout : "fit",
										split : true,
										//collapsible : true,
										region : 'north',
										height : 380,
										items : tree
									}, {
										title : '',
										layout : "fit",
										split : true,
										region : 'center',
										items : this.form
									}],
							bbar:this.createButtons()
						});
				this.tree = tree
				tree.on("click", this.onTreeClick, this)
				tree.on('beforeload', this.beforeLoad, this);
				//this.tree.expandAll();
				return this.panel
			},
				onTreeClick : function(node, e) {
				this.form.setValue(node.attributes.attributes["XMNR"])
				},
				doComfire:function(){
				this.fireEvent("comfire",this.docid,this.form.getValue(),this)
				this.doCancel();
				},
				doCancel:function(){
				this.getWin().hide();
				},
				beforeLoad : function(node) {
				this.tree.loader.baseParams.queryFields = "XMBM,XMMC,XMNR,XDBH";//第一个对应id,第二个对应key,后面的都放在attributes里面
				// 查询的表名，可根据节点动态设置所要访问的表
				this.tree.loader.baseParams.entryName = 'YS_MZ_BLXD02';
				this.tree.loader.baseParams.orderBy = 'XMBM';
				if (node.id != "root") {
					cnd = "['and',['eq',['$','XDBH'],['l',"+this.xdbh+"]],['eq',['substr',['$','XMBM'],0,"+(node.id.length)+"],['s','"+node.id+"']],['eq',['len','XMBM'],['i',"+(node.id.length+4)+"]]]";
				} else {
					cnd = "['eq',['$','XDBH'],['l',"+this.xdbh+"]]";
				}
				this.tree.loader.baseParams.condition = cnd;
				}
		});
