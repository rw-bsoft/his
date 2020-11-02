$import("util.rmi.miniJsonRequestSync")


var result = util.rmi.miniJsonRequestSync({
	serviceId:"logon",
	uid:"system",
	psw:"123",
	rid:"system"
})

var result = util.rmi.miniJsonRequestSync({
	serviceId:"testService",
	serviceAction : "testService"
})


//if(result.code == 200){
	alert(Ext.encode(result))
//}

//$import("chis.script.util.helper.Helper" ,"app.desktop.myDesktop");
//
//Ext.Msg.alert('error', "fsfsasd");