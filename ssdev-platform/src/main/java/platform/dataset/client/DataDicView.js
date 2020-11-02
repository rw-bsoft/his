$package("platform.dataset.client")
$styleSheet("platform.dataset.client.resources.dataset")
$import("util.dictionary.DictionaryBuilder")

platform.dataset.client.DataDicView = function(cfg) {
	platform.dataset.client.DataDicView.superclass.constructor.apply(this, [cfg])
}
Ext.extend(platform.dataset.client.DataDicView, util.dictionary.DictionaryBuilder, {
	initPanel:function(){
		this.dicId = "dictionaries.platform.dataset.dic.res."+this.resDataStandard;
		var panel=platform.dataset.client.DataDicView.superclass.initPanel.apply(this);
		return panel;
	},
	processDicRemove:function(){
		var fields = this.getFields();
		var id = fields.dicId.getValue()
		if(id == ""){
			return;
		}
		var result = this.saveToServer("removeDic4Meta",{key:id})
		if(result.code == 200){
			var node = this.selectedNode
			if(node){
				var next = node.nextSibling || node.previousSibling
				node.parentNode.removeChild(node)
				this.form.getForm().reset()
				if(next){
					this.onTreeClick(next)
					next.ensureVisible()
				}
			}
		}
	},
	reset:function(){
		this.tree.getLoader().url = "dictionaries.platform.dataset.dic.res."+this.resDataStandard+".dic"
		this.tree.getLoader().load(this.tree.getRootNode());
	}
});
