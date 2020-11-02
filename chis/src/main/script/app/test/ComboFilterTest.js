﻿$import("org.ext2.ext-base",
		"org.ext2.ext-all-debug",
		"org.ext2.ext-lang-zh_CN",
		"util.dictionary.LovComboDicFactory",
		"util.rmi.miniJsonRequestSync"
);
$styleSheet("ext2.ext-all")
$styleSheet("ext2.ext-patch")
$styleSheet("app.desktop.plugins.DesktopIconView")
$styleSheet("app.desktop.Desktop")
__docRdy = false
Ext.onReady(function(){
	if(__docRdy){
		return
	}
	__docRdy = true	
	Ext.BLANK_IMAGE_URL = ClassLoader.appRootOffsetPath +"inc/resources/s.gif"
	var result = util.rmi.miniJsonRequestSync({
		serviceId:"logon",
		uid:"system",
		psw:"123"
	})
	
	/*
	var lc = new util.widgets.LovCombo({
		 width:300
		,hideOnSelect:false,
		forceSelection:false
		,maxHeight:200
		,store:[
			 [1,"好"],
			 [2,"满意"],
			 [3,"非常满意"]
		]
		,triggerAction:'all'
		,mode:'local'
	});
	lc.on("select",function(cb){
		alert(cb.getValue())
		
	})
	var form = new Ext.FormPanel({
		title:"测试",
		items:lc
	})
	form.render(document.body)
	form.show()
	*/
	var f = util.dictionary.LovComboDicFactory.createDic({id:"manageUnit",parentKey:"3301"})
	var p = new Ext.Panel({
		frame:true,
		layout:"form",
		items:f,
		width:500,
		buttons:[
			{
				text:"测试",
				handler:function(){
					f.store.filter("key","330103")
				},
				scope:this
			}
		]
	})
	p.render(document.body)
	p.show()
	
})
