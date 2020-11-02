$package("chis.application.quality.script")

$import("chis.script.BizCombinedModule3")
chis.application.quality.script.QualityControl_gxy_module = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.quality.script.QualityControl_gxy_module.superclass.constructor.apply(
			this, [cfg]);
   	this.itemCollapsible = false
 	this.exContext = {};
 	this.frame = true;
}
Ext.extend(chis.application.quality.script.QualityControl_gxy_module,
		chis.script.BizCombinedModule3, {
		 initPanel : function() {
//				if (this.panel) {
//					return this.panel;
//				}
				var items = this.getPanelItems();
				var panel = new Ext.Panel({
							border : false,
							split : true,
							hideBorders : true,
							//frame : this.frame || false,
							layout : 'table',
							layoutConfig: {
						        // 这里指定总列数
						        columns: 2
						    },
							items : items
						});
				this.panel = panel
				panel.on("afterrender", this.onReady, this)
				
				this.list1 = this.midiModules[this.actions[0].id];
				this.list2 = this.midiModules[this.actions[1].id];
				this.list3 = this.midiModules[this.actions[2].id];
				this.list3.on("addYB",this.addYbMod,this);//添加样本事件
				this.list1.on("addYbYZ",this.addYbYzMod,this);//添加样本事件验证
				this.list2.on("addBackNo",this.addYbZKMod,this);//添加样本事件验证
				this.list1.on("onread",this.doOnread2,this);//添加list2查询事件
				this.list2.on("onread2",this.doOnread3,this);//添加list3查询事件
				this.list2.on("list2Wczk",this.doList2Wczk,this);//添加list2完成质控按钮
				return panel
			},
			 getPanelItems : function() {
			    var firstItem = this.getFirstItem();
			    this.firstItem=firstItem;
			    
				var secondItem = this.getSecondItem();
				 this.secondItem=secondItem;
				 
				var thirdItem = this.getThirdItem();
				 this.thirdItem=thirdItem;
				var items = [{
							layout : "fit",
							border : false,
							frame : false,
							split : true,
							title : '',
							region : "north",
							heigth : 270,
							colspan: 2,
							collapsible : this.itemCollapsible,
							items : firstItem
						}, {
							collapsible : this.itemCollapsible,
							layout : "fit",
							border : false,
							frame : false,
							split : true,
							title : '',
							region :"west",
							width : 460,
							items : secondItem
						}, {
							layout : "fit",
							border : false,
							frame : false,
							split : true,
							title : '',
							region : "center",
							items : thirdItem
						}];
				return items;
			},addYbMod:function(){//list3 添加样本，list1获取元数据信息
			   this.list1.doAddYbYz();
			},addYbYzMod:function(data){//list2 添加NO，返回数据 list2添加
				 var values=data;
			     this.list2.addYf(values);
			},addYbZKMod:function(data){//获取list2 原数据，list3添加质控数据
				 this.list3.addZK(data);
			},doOnread2:function(values){//list1查询时，list2,执行相应的查询
				 this.list2.doQuery(values);
				 this.list3.doQuery(values);
			},doOnread3:function(values){//list1查询时，list3,执行相应的查询
				this.list3.doQuery(values);
			},doList2Wczk:function(prartNo){//list2完成质控按钮,传prartNo
				this.list3.doList2Wczk(prartNo);
			}
		})