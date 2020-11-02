$import("org.ext2.ext-base",
		"org.ext2.ext-all-debug",
		"org.ext2.ext-lang-zh_CN",
		"chis.script.CardReader"
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
	var cr = chis.script.CardReader;
	cr.init()
	var ret = cr.readCardInfo()
	alert(Ext.encode(ret))
})
