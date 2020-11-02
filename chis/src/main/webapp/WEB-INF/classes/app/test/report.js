$import("org.ext2.ext-base",
		"org.ext2.ext-all",
		"org.ext2.ext-lang-zh_CN",
		"app.biz.report.ReportController",
		"util.rmi.miniJsonRequestSync"
);
$styleSheet("ext2.ext-all")
$styleSheet("ext2.ext-patch")
//$styleSheet("app.desktop.plugins.DesktopIconView")
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
	
	var m = new app.biz.report.ReportController({
		actions:[
			{id:"DocByArea",name:"统计分析1",type:"chart"},
			{id:"DocByArea",name:"统计分析1",type:"report"},
			{id:"DctRootChart",name:"统计分析1",type:"chart"}
		]
	})
	
	//m.render(document.body)
	p = m.getWin()
	p.show()
	//var doc = new app.biz.PosSaleDoc({})
	//var p = doc.getWin()
	//p.render(document.body)
	//p.show()
})
