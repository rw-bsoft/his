$package("phis.script.report")
$import("phis.script.report.Module")
phis.script.report.Navigation = function(cfg){
	phis.script.report.Navigation.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(phis.script.report.Navigation,phis.script.report.Module,{
	getPanel:function()
	{
		var me=this;
		var newcfg=me.newcfg;
		var cfg={};
		newcfg.navType=newcfg.navType||'Cross';
		cfg.html=eval("me.get"+newcfg.navType+"NavigationHtml()");
		cfg.listeners={
		    	afterrender:function()
		    	{
		    		eval("me.set"+newcfg.navType+"NavActive(0)");
		    		eval("me.add"+newcfg.navType+"NavEvent()");
		    	}};
		Ext.apply(cfg,newcfg);
		var panel=new Ext.Panel(cfg);
		return panel;
	},
	getCrossNavigationHtml:function(){
		var me=this;
		if(me.zid)
			return me.navigationHtml;
		var id=Ext.id();
		me.zid=id;
		var newcfg=me.newcfg;
		var html='';
        var lis=newcfg.lis; 
        var all=!newcfg.all?false:newcfg.all;
        var navclass=newcfg.navaclass||"breadcrumbs";
        if(!lis||lis.length==0)
        {
        	html='<ul id="'+id+'_ul" class='+navclass+'>';
        	var count=1;
        	if(all){count=newcfg.cardPanel?newcfg.cardPanel.items.length:1}
        	
        	for(var i=0;i<count;i++)
					  html+='<li id="'+id+'_li'+i+'"><a id="'+id+'_a'+i+'" href="javascript:;" onclick="return false;" rel="'+i+'">第'+(i+1)+'层</a></li>'
        	html+='</ul>';
        }
        else
        {
        	html='<ul id="'+id+'_ul" class='+navclass+'>';
        	var count=1;
        	if(all){count=lis.length}
        	for(var i=0;i<count;i++)
        	{
        		html+='<li id="'+id+'_li'+i+'"><a id="'+id+'_a'+i+'" href="javascript:;" onclick="return false;" rel="'+i+'">'+lis[i]+'</a></li>'
        	}
        	html+='</ul>';
        }
        me.navigationHtml=html;
        return html; 
	},
	setCrossNavActive:function(n)
	{
	    var me=this;
		if(!me.zid)return;
		var id=me.zid;
		var ula=Ext.select('#'+id+'_ul a');
		var lis=ula.elements;	
		if(!lis[n])
			return;
		var lislength=lis.length;
			for(var i=0;i<lislength;i++)
			{	
				var li=Ext.get(id+'_a'+i);
				if(i==n){	
					li.addClass("current");
					me.currentli=i;
					}
				else
					{li.removeClass("current");} 	
			}
			
	},
	addCrossNavEvent:function()
	{
		var me=this;
		if(!me.zid)return;
		var id=me.zid;
		var ula=Ext.select('#'+id+'_ul a');
		var lis=ula.elements;	
		var lislength=lis.length;
			for(var i=0;i<lislength;i++)
			{
				var clickfunction=(function(){
					var k=i;
					var lisk=lis;
					return function(){
						if(me.currentli!=k){
						me.setCrossNavActive(k);
						me.setContentActive(k);
						}
						
					}
				})();
				Ext.get(id+'_li'+i).on('click',clickfunction);
			}	
	},
	goBack:function()
	{
		var me=this;
		if(!me.zid)return;
		var id=me.zid;
		var current=me.currentli;
		if(!current||current==0)
			return;
		me.setCrossNavActive(current-1);
		me.setContentActive(current-1);	
	},
	goNext:function()
	{
		var me=this;
		if(!me.zid)return;
		var id=me.zid;
		var newcfg=me.newcfg;
		var ula=Ext.select('#'+id+'_ul a');
		var lis=newcfg.lis;
		var lislength=ula.elements.length;
		var ul=Ext.get(id+'_ul');
		var all=newcfg.all;
		var nextli=me.currentli+1;
		var cards=newcfg.cardPanel?newcfg.cardPanel.items.length:1;
		
		
		if(all)
		{
			if(me.currentli==lislength-1)
				return;
			me.setCrossNavActive(nextli);
			me.setContentActive(nextli);

		}else{			
		if(!lis||lis.length==0){
			if(me.currentli!=lislength-1){
    			for(var i=(me.currentli+1);i<lislength;i++)
    			{
    				Ext.get(id+'_li'+i).remove();
    			}
    			ul.insertHtml('beforeEnd','<li id="'+id+'_li'+nextli+'"><a id="'+id+'_a'+nextli+'" href="javascript:;" onclick="return false;" rel="'+nextli+'">第'+(nextli+1)+'层</a></li>');	
			}else
				{
					if(newcfg.cardPanel&&lislength!=cards)
							ul.insertHtml('beforeEnd','<li id="'+id+'_li'+nextli+'"><a id="'+id+'_a'+nextli+'" href="javascript:;" onclick="return false;" rel="'+nextli+'">第'+(nextli+1)+'层</a></li>');	
					else
						return;
				}
		}
			else
			{
				if(me.currentli!=lislength-1){
	    			for(var i=(me.currentli+1);i<lislength;i++)
	    			{
	    				Ext.get(id+'_li'+i).remove();
	    				
	    			}
	    			ul.insertHtml('beforeEnd','<li id="'+id+'_li'+nextli+'"><a id="'+id+'_a'+nextli+'" href="javascript:;" onclick="return false;" rel="'+nextli+'">'+(!arguments[0]?lis[nextli]:arguments[0])+'</a></li>');
				}else
				{
					if(newcfg.cardPanel&&lislength!=cards)
		    			ul.insertHtml('beforeEnd','<li id="'+id+'_li'+nextli+'"><a id="'+id+'_a'+nextli+'" href="javascript:;" onclick="return false;" rel="'+nextli+'">'+(!arguments[0]?lis[nextli]:arguments[0])+'</a></li>');
					else
						return;
				}
			}
		me.addCrossNavEvent();
		me.setCrossNavActive(nextli);
		me.setContentActive(nextli);

		}
		
		
	},
//Vertical	
	getVerticalNavigationHtml:function(){
		var me=this;
		if(me.zid)
			return me.navigationHtml;
		var id=Ext.id();
		me.zid=id;
		var lis=me.newcfg.lis;
		var html='<div class="content greyBg"><div class="fleft"><ul id="'+id+'_ul" class="leftMenu">';
			if(!lis||lis.length<=0)
			{
		       html+='<li id="'+id+'_li0"><a href="javascript:void(0);">未配置导航内容</a></li>';
			}else
			{
				var lislength=lis.length;
				for(var i=0;i<lislength;i++)
				{
					html+='<li id="'+(id+'_li'+i)+'"><a href="javascript:void(0);">'+lis[i]+'</a></li>';
				}
			}
		html+='</ul></div></div>';
		me.navigationHtml=html;
		return html;
	},
	setVerticalNavActive:function(n)
	{
		var me=this;
		if(!me.zid)return;
		var id=me.zid;
		var lis=me.newcfg.lis;
		if(!lis[n])
			return;
		var lislength=lis.length;
			for(var i=0;i<lislength;i++)
			{	
				var li=Ext.get(id+'_li'+i);
				if(i==n)	
					{li.addClass("current");
					me.currentli=i;
					}
				else
					{li.removeClass("current");} 
				
			}
			if(me.newcfg.clickEvent)
				me.newcfg.clickEvent(n);
	},
	addVerticalNavEvent:function()
	{
		var me=this;
		if(!me.zid)return;
		var id=me.zid;
		var lis=me.newcfg.lis;
		var lislength=lis.length;
			for(var i=0;i<lislength;i++)
			{
				var clickfunction=(function(){
					var k=i;
					var lisk=lis;
					return function(){
						if(me.currentli!=k){
						me.setVerticalNavActive(k);
						me.setContentActive(k);}
					}
				})();
				Ext.get(id+'_li'+i).on('click',clickfunction);
			}	
	},
//public
	setContentActive:function(n)
	{
		var me=this;
		if(!me.zid)return;
		var cardPanel=me.newcfg.cardPanel
		if(!cardPanel)
			return;
		cardPanel.layout.setActiveItem(n);
		me.randomFx(cardPanel);
	},
	randomFx:function(content)
	  {
			var me=this;
			if(!me.zid)return;
	    	var fx=!me.newcfg.fx?false:me.newcfg.fx;
	    	if(!fx)
	    		return; 
	    	var activeItem=content.layout.activeItem;
	    	var div1=Ext.getDom(activeItem.id);
	    	var n=parseInt(Math.random()*10+1)
	    	var fx;
	    	switch(n%4)
	    	{
	    		case 1:{fx= 't'}break;
	    		case 2:{fx= 'b'}break;
	    		case 3:{fx= 'l'}break;
	    		default:{fx= 'r'}break;
	    	}    	
    		me.addmengban(div1);
	    	//content.body.slideIn(fx, {duration: 0.5});
	    },
	    addmengban:function(dom) {
			var me=this;
			if(!me.zid)return;
	    	Ext.DomHelper.insertHtml('afterbegin', dom, '<div id="'+id+'_mengban" class="mengban"></div>');
	    	Ext.defer(function() {
	    				Ext.get(id+'_mengban').fadeOut({
	    							remove : true
	    						});
	    			}, 350);	

	    }	    
})
