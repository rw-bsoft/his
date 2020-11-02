$package("chis.script.util.widgets")

if('function' !== typeof RegExp.escape) {
	RegExp.escape = function(s) {
		if('string' !== typeof s) {
			return s;
		}
		// Note: if pasting from forum, precede ]/\ with backslash manually
		return s.replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');
	}; // eo function escape
}

/**
 * @class chis.script.util.widgets.TreeCheckNodeUI
 * @extends Ext.tree.TreeNodeUI
 * 
 * 对 Ext.tree.TreeNodeUI 进行checkbox功能的扩展,后台返回的结点信息不用非要包含checked属性
 * 
 * 扩展的功能点有： 一、支持只对树的叶子进行选择 只有当返回的树结点属性leaf = true 时，结点才有checkbox可选
 * 使用时，只需在声明树时，加上属性 onlyLeafCheckable: true 既可，默认是false
 * 
 * 二、支持对树的单选 只允许选择一个结点 使用时，只需在声明树时，加上属性 checkModel: "single" 既可
 * 
 * 二、支持对树的级联多选
 * 当选择结点时，自动选择该结点下的所有子结点，或该结点的所有父结点（根结点除外），特别是支持异步，当子结点还没显示时，会从后台取得子结点，然后将其选中/取消选中
 * 使用时，只需在声明树时，加上属性 checkModel: "cascade" 或"parentCascade"或"childCascade"既可
 * 
 * 三、添加"check"事件 该事件会在树结点的checkbox发生改变时触发 使用时，只需给树注册事件,如：
 * tree.on("check",function(node,checked){...});
 * 
 * 默认情况下，checkModel为'multiple'，也就是多选，onlyLeafCheckable为false，所有结点都可选
 * 
 * 使用方法：在loader里加上 baseAttrs:{uiProvider:chis.script.util.widgets.TreeCheckNodeUI} 既可. 例如： var
 * tree = new Ext.tree.TreePanel({ el:'tree-ct', width:568, height:300,
 * checkModel: 'cascade', //对树的级联多选 onlyLeafCheckable: false,//对树所有结点都可选
 * animate: false, rootVisible: false, autoScroll:true, loader: new
 * Ext.tree.DWRTreeLoader({ dwrCall:Tmplt.getTmpltTree, baseAttrs: { uiProvider:
 * chis.script.util.widgets.TreeCheckNodeUI } //添加 uiProvider 属性 }), root: new
 * Ext.tree.AsyncTreeNode({ id:'0' }) });
 * tree.on("check",function(node,checked){alert(node.text+" = "+checked)});
 * //注册"check"事件 tree.render();
 * 
 */

chis.script.util.widgets.TreeCheckNodeUI = function() {
	// 多选: 'multiple'(默认)
	// 单选: 'single'
	// 级联多选: 'cascade'(同时选父和子);'parentCascade'(选父);'childCascade'(选子)
	this.checkModel = 'cascade';

	// only leaf can checked
	this.onlyLeafCheckable = false;

	chis.script.util.widgets.TreeCheckNodeUI.superclass.constructor.apply(this, arguments);
};

Ext.extend(chis.script.util.widgets.TreeCheckNodeUI, Ext.tree.TreeNodeUI, {
	
	// private
    onDblClick : function(e){
        e.preventDefault();
        if(this.disabled){
            return;
        }
//      if(this.checkbox){
//          this.toggleCheck();
//      }
        if(!this.animating && this.node.isExpandable()){
            this.node.toggle();
        }
        this.fireEvent("dblclick", this.node, e);
    },

	renderElements : function(n, a, targetNode, bulkRender) {
		var tree = n.getOwnerTree();
		this.checkModel = tree.checkModel || this.checkModel;
		this.onlyLeafCheckable = tree.onlyLeafCheckable || false;

		// add some indent caching, this helps performance when rendering a
		// large tree
		this.indentMarkup = n.parentNode
				? n.parentNode.ui.getChildIndent()
				: '';

		// var cb = typeof a.checked == 'boolean';
		var cb = (!this.onlyLeafCheckable || a.leaf);
		var href = a.href ? a.href : Ext.isGecko ? "" : "#";
		var buf = [
				'<li class="x-tree-node"><div ext:tree-node-id="',
				n.id,
				'" class="x-tree-node-el x-tree-node-leaf x-unselectable ',
				a.cls,
				'" unselectable="on">',
				'<span class="x-tree-node-indent">',
				this.indentMarkup,
				"</span>",
				'<img src="',
				this.emptyIcon,
				'" class="x-tree-ec-icon x-tree-elbow" />',
				'<img src="',
				a.icon || this.emptyIcon,
				'" class="x-tree-node-icon',
				(a.icon ? " x-tree-node-inline-icon" : ""),
				(a.iconCls ? " " + a.iconCls : ""),
				'" unselectable="on" />',
				cb
						? ('<input class="x-tree-node-cb" type="checkbox" ' + (a.checked
								? 'checked />'
								: '/>'))
						: '',
				'<a hidefocus="on" class="x-tree-node-anchor" href="', href,
				'" tabIndex="1" ',
				a.hrefTarget ? ' target="' + a.hrefTarget + '"' : "",
				'><span unselectable="on">', n.text, "</span></a></div>",
				'<ul class="x-tree-node-ct" style="display:none;"></ul>',
				"</li>"].join('');

		var nel;
		if (bulkRender !== true && n.nextSibling
				&& (nel = n.nextSibling.ui.getEl())) {
			this.wrap = Ext.DomHelper.insertHtml("beforeBegin", nel, buf);
		} else {
			this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf);
		}

		this.elNode = this.wrap.childNodes[0];
		this.ctNode = this.wrap.childNodes[1];
		var cs = this.elNode.childNodes;
		this.indentNode = cs[0];
		this.ecNode = cs[1];
		this.iconNode = cs[2];
		var index = 3;
		if (cb) {
			this.checkbox = cs[3];
			// fix for IE6
			if(Ext.isIE6)
				this.checkbox.defaultChecked = this.checkbox.checked;
			//给checkbox 注册事件 click 事件 参数为 null
			Ext.fly(this.checkbox).on('click',this.check.createDelegate(this, [null]));
			index++;
		}
		this.anchor = cs[index];
		this.textNode = cs[index].firstChild;
	},
	
	// private
	check : function(checked) {
		var n = this.node;
		var tree = n.getOwnerTree();
		this.checkModel = tree.checkModel || this.checkModel;

		if (checked === null) {
			checked = this.checkbox.checked;
		} else {
			this.checkbox.checked = checked;
		}
		// fix for IE6 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		if(Ext.isIE6)
			this.checkbox.defaultChecked = checked;
		n.attributes.checked = checked;
		//设置组件的值 setValue()
		var parentNode = n.parentNode;
		if (parentNode !== null) {
			this.parentCheck(parentNode, checked);
		}
		this.childCheck(n)
		tree.fireEvent('check', n, checked);
	},
	
	// private
	childCheck : function(node) {
		var a = node.attributes;
		if (!a.leaf) {
			var cs = node.childNodes;
			var csui;
			for (var i = 0; i < cs.length; i++) {
				csui = cs[i].getUI();
				if (csui.checkbox.checked ^ a.checked)
					csui.check(a.checked);
			}
		}
	},

	// private
	parentCheck : function(node, checked) {
		var checkbox = node.getUI().checkbox;
		if (typeof checkbox == 'undefined')
			return;
		if (!(checked ^ checkbox.checked))
			return;
		if(checked && !this.allChildChecked(node)){//如果节点被选中了
			return ;
		}
		checkbox.checked = checked;
		node.attributes.checked = checked;
		//node.getOwnerTree().fireEvent('check', node, checked);
			
		var parentNode = node.parentNode;
		if (parentNode !== null) {
			this.parentCheck(parentNode, checked);
		}
	},
	
	allBrotherChecked:function(node){
		var parentNode = node.parentNode ;
		var brothers = parentNode.childNodes ;
		for(var i = 0 ;i<brothers.length;i++){
			if (brothers[i].getUI().checkbox.checked){
				return false;
			}
		}
		return true;
	},
	
	allChildChecked:function(node){
		var childNodes = node.childNodes;
		if (childNodes || childNodes.length > 0) {
			for (var i = 0; i < childNodes.length; i++) {
				if (!childNodes[i].getUI().checkbox.checked)
					return false;
			}
		}
		return true;
	},

	// private
	childHasChecked : function(node) {
		var childNodes = node.childNodes;
		if (childNodes || childNodes.length > 0) {
			for (var i = 0; i < childNodes.length; i++) {
				if (childNodes[i].getUI().checkbox.checked)
					return true;
			}
		}
		return false;
	},

	toggleCheck : function(value) {
		var cb = this.checkbox;
		if (cb) {
			var checked = (value === undefined ? !cb.checked : value);
			this.check(checked);
		}
	}
});

// {{{
/*
 * 树状多选下拉框组件 
 */
chis.script.util.widgets.MyTreeCheckField = function() {
	this.treeId = Ext.id() + '-tree';
	this.maxHeight = arguments[0].maxHeight || arguments[0].height
			|| this.maxHeight;
	this.tpl = new Ext.Template('<tpl for="."><div style="height:'
			+ this.maxHeight + 'px"><div id="' + this.treeId
			+ '"></div></div></tpl>');
	this.store = new Ext.data.SimpleStore({
				fields : [],
				data : [[]]
			});
	this.selectedClass = '';
	this.mode = 'local';
	this.triggerAction = 'all';
	this.onSelect = Ext.emptyFn;
	this.onViewClick = Ext.emptyFn;   //避免点击消失
	this.editable = false;
	this.selectValueModel = 'leaf';

	chis.script.util.widgets.MyTreeCheckField.superclass.constructor.apply(this, arguments);
}

Ext.extend(chis.script.util.widgets.MyTreeCheckField, Ext.form.ComboBox, {

	reset:function(){
		this.clearValue()
		this.clearChecked()
		chis.script.util.widgets.MyTreeCheckField.superclass.reset.call(this);
	},
	checkField : 'checked',
	separator : ',',
	initEvents : function() {
		chis.script.util.widgets.MyTreeCheckField.superclass.initEvents.apply(this, arguments);
		this.keyNav.tab = false

	},

	initComponent : function() {
		chis.script.util.widgets.MyTreeCheckField.superclass.initComponent.call(this); 
		this.on({
			scope : this
		})		
	},
	expand : function() {
		chis.script.util.widgets.MyTreeCheckField.superclass.expand.call(this);
		if (!this.tree.rendered) {
			this.tree.height = this.maxHeight;
			this.tree.border = false;
			this.tree.autoScroll = true;
			if (this.tree.xtype) {
				this.tree = Ext.ComponentMgr.create(this.tree, this.tree.xtype);
			}
			this.tree.render(this.treeId);	
			
			var combox = this;
			this.tree.on('check', function(node, checked){combox.setValue(node)});
			this.tree.on('expandNode', function(node){
				if(node.getUI().checkbox.checked){//如果该节点被选中，下面的节点全部需要被勾选
					var children  = node.childNodes ;
					for(var i =0;i<children.length ;i++){
						var c = children[i];
						c.getUI().checkbox.checked = true ;
						c.attributes["checked"] = true ;
					}
				}
			});
			var root = this.tree.getRootNode();
			if (!root.isLoaded())
				root.reload();
		}
	},
	
	setRawValue : function(v){
		if(this.el)
        	return this.el.dom.value = (v === null || v === undefined ? '' : v);
    },
    
    arrayDelete:function(array,index){
    	if(index<0 || index>array.length-1){
    		return array
    	}
    	return array.slice(0,index).concat(array.slice(index+1,array.length))
    },
    
    arrayContains:function(array,o){
    	for(var i=0;i<array.length;i++){
    		if(array[i] == o){
    			return i
    		}
    	}
    	return -1
    },
    
	setValue : function(v) {
		//modify by yuhua
		if(!v){
			this.clearChecked()
			this.clearValue()
			return
		}
		var values = [];
		if(this.getValue() != ""){
			values = this.getValue().split(",")
		}
		var rawTexts = this.getRawValue() || ""
		var texts = [];
		if(rawTexts != ""){
			texts = rawTexts.split(",")
		}
		
		if(v){			
			// 点击树节点时候 增加或者减少一个选中的节点 无需清空所有的已选择的节点状态			
			if(v instanceof Ext.tree.TreeNode){
				var node = v
				var flag = node.attributes.checked
				var index = this.arrayContains(values,node.id)
				var index_text = this.arrayContains(texts,node.text)
				if(flag){
					if(index == -1){
						//如果一个节点被选中，应该确保他的子节点在value中被除去.
						var children = node.childNodes;
						for(var i = 0 ;i<children.length ; i++){
							var child = children[i];
							var cIdx = this.arrayContains(values,child.id)
							if(cIdx>-1){
								values= this.arrayDelete(values,cIdx);
								texts = this.arrayDelete(texts,cIdx);
							}
						}
						
//						values.push(node.id)
//						texts.push(node.text)
					}					
				}else{
					if(index > -1){
						values = this.arrayDelete(values,index)						
					}
					if(index_text > -1){
						texts = this.arrayDelete(texts,index_text)
					}
				}
			}
			// 初始化或者赋值的时候 应当清空所有已经选择的节点状态
			else if(typeof v == "object"){			
				//清空选择
				this.clearChecked()
				this.clearValue()
				//先转为 key .
				var txs = v.text
				v = v.key

				if(v && v.indexOf(",") != -1){
					values = v.split(",")
				}else if(v){
					values.push(v)
				}else{
				    values=[]
				}
				if(txs && txs.indexOf(",") != -1){
					texts = txs.split(",")
				}else if(txs){
					texts.push(txs)
				}else{
				    texts=[]
				}
				for(var i=0;i<values.length;i++){
					var node = this.tree.getNodeById(values[i])
					if(node){
						if(!txs){
							texts.push(node.text)
						}
						node.getUI().check(true)
					}					
				}
			}
		}
	
		this.value = values.join(this.separator);
		if(texts.length >= 0){
			this.setRawValue(texts.join(this.separator));
		}
		this.validate();
		this.texts = texts
		if (this.hiddenField) {
			this.hiddenField.value = this.value;
		}
	},
	
	clearChecked:function(){
		var checkedNodes = this.tree.getChecked();
		for (var i = 0; i < checkedNodes.length; i++) {
			var node = checkedNodes[i];
			node.getUI().check(false)
		}
	},

	//当前树的选中值动态获取，只取根节点.
	getValue : function() {
		var root = this.tree.root;
		var list = [] ;
		var selectedNodes = this.getSelectedNodes(root,list)
		var values = "";
		var texts = "";
		for(var i = 0 ;i<selectedNodes.length;i++){
			var n = selectedNodes[i];
			values+=n.attributes["key"]+",";
			texts+=n.attributes["text"]+",";
		}
		values = values.substring(0,values.length -1 );
		texts = texts.substring(0,texts.length -1 )
		this.value = values ;
		//this.setRawValue('');
		this.setRawValue(texts);
		return this.value || '';
	},
	
	getSelectedNodes:function(node,list){
		var checkbox = node.getUI().checkbox ;
		if(!checkbox)
			return list ;
		if(checkbox.checked){
			list.push(node)
			return list ;
		}
		
		var children = node.childNodes ;
		if(children && children.length>0){
			for(var i=0 ;i<children.length ;i++){
				list = this.getSelectedNodes(children[i],list)
			}
		}
		return list ;
	},
	

	clearValue : function() {
		this.value = '';
		this.setRawValue(this.value);
		if (this.hiddenField) {
			this.hiddenField.value = '';
		}

		this.tree.getSelectionModel().clearSelections();
	}
});
// }}}


Ext.reg('MyTreeCheckField', chis.script.util.widgets.MyTreeCheckField);