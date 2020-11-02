$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.IframeModule = function(cfg) {
	phis.application.cic.script.IframeModule.superclass.constructor.apply(this,
			[cfg])
			
}
Ext.extend(phis.application.cic.script.IframeModule,phis.script.SimpleModule, {
	initPanel : function(sc) {
		var url = this.url;
		if(url.indexOf("?")  < 0 ){
			url+="?";
		}
		url+='1=1';
		
		url+=this.getUrlParam();
		url=encodeURI(encodeURI(url));
		var html='<iframe height="100%" width="100%" src="{0}"></iframe>';
		html=String.format(html,url);
		console.log(html)
		var panel=new Ext.Panel({
			html:html,
			buttonAlign:'center',
	        autoScroll: false,
		});
		if(this.isWindow){
			panel.addButton({
				text:'关闭',
				handler:function(){
					this.getWin().hide();
				},
				scope:this,
				iconCls:'common_cancel'
			});
		}
		return panel;
	},
	getUrlParam:function(){
		var paramString='';
		for(k in this){
			if(k.length>2 && k.substring(0,2)=='p_'){
				var key=k.substring(2,k.length);
				var value=this[k];
				paramString+=String.format('&{0}={1}',key,value);
			}
		}
		if(this.params){
			for(k in this.params){
				var value=this.params[k];
				paramString+=String.format('&{0}={1}',k,value);
			}
		}
		return paramString;
	}
})