$import("org.ext2.ext-base",
		"org.ext2.ext-all-debug",
		"org.ext2.ext-lang-zh_CN",
		"app.modules.combined.PortalView",
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
	var m = new app.modules.combined.PortalView({
		actions:[
			{id:"index1",name:"测试1",ref:"B08"},
			{id:"index2",name:"测试2",height:400,script:"app.modules.chart.DiggerChartView",entryName:"DctRootChart"}
		]
	})
	m.getWin().show()
	
})
