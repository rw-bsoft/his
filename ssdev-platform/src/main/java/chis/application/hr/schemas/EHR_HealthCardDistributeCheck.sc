<?xml version="1.0" encoding="UTF-8"?>
<entry alias="健康卡发卡审核">
	<item id="cardNum" alias="健康卡号" width="120"></item>
	<item id="personcode" alias="个人编码" width="120"></item>
	<item id="personName" alias="姓名" type="string" length="20"
		not-null="1" width="120" />
	<item id="sexCode" alias="性别" type="string" length="1" width="40"
		not-null="1" defalutValue="9">
		<dic id="chis.dictionary.gender" />
	</item>
	<item id="birthday" alias="出生日期" type="date" not-null="1"
		maxValue="%server.date.today" width="100" />
	<item id="cardnum" alias="身份证件号码" type="string" width="150"
		not-null="1" length="25" />
	<item id="phoneNumber" alias="联系电话" type="string" length="20"
		not-null="1" queryable="true" width="160" />
	<item id="address" alias="地址" not-null="1" width="200"></item>
</entry>