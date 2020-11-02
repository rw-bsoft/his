<?xml version="1.0" encoding="UTF-8"?>
<entry alias="医保卡">
	<item id="cardNum" alias="SQ个人编码" ></item>
	<item id="personName" alias="姓名" type="string" length="20"
		not-null="1" />
	<item id="sexCode" alias="性别" type="string" length="1" width="40"
		not-null="1" defalutValue="9">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="birthday" alias="出生日期" type="date" not-null="1"
		maxValue="%server.date.today" />
	<item id="cardnum" alias="身份证件号码" type="string" width="150"
		not-null="1" length="25"/>
		
	<item id="mobileNumber" alias="电话" type="string" length="20"
		not-null="1" />
		
	<item id="address" alias="地址" colspan="2" not-null="1"></item>
	
</entry>





