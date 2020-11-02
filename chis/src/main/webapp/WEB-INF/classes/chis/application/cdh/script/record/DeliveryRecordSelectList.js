$package("chis.application.cdh.script.record")
$import("chis.script.BizSimpleListView")

chis.application.cdh.script.record.DeliveryRecordSelectList = function(cfg) {
	cfg.entryName ="MHC_DeliveryRecordSelect";
	cfg.actions=[{name:"选择",id:"select",iconCls:"common_select"}]
	cfg.autoLoadData = false ;
	cfg.disablePagingTbr = true ;
	cfg.width = 320 ; 
	this.autoHeight =false 
	cfg.height = 200 ;
	this.title = "从分娩记录导入数据"
	cfg.modal = true; 
	chis.application.cdh.script.record.DeliveryRecordSelectList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.cdh.script.record.DeliveryRecordSelectList,chis.script.BizSimpleListView, {

			onDblClick:function(){
				this.doSelect();
			},
      
			doSelect:function(){
				var  r = this.getSelectedRecord() ;
				if(!r)
					return ;
				this.fireEvent("onSelect",r);
				this.getWin().hide();
			},
      
			setData:function(array){
				if(!this.grid){
					this.grid = this.initPanel();
					this.store = this.grid.store;
				}
				this.store.removeAll();
				if(!array || array.length ==0 )
					return ;
				for(var i = 0 ; i<array.length ; i++){
					var r = new Ext.data.Record (array[i]);
					this.store.add(r);
				}
			}
		});