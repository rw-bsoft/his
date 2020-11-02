<?xml version="1.0" encoding="UTF-8"?>
<entry alias="健康卡发卡审核">
	<item id="gxdw" alias="管辖单位" width="120"></item>
	<item id="xjkk" alias="新健康卡"></item>
	<item id="jkkh" alias="健康卡号"></item>
	<item id="klx" alias="卡类型"></item>
	<item id="personName" alias="姓名" type="string" length="20"
		not-null="1" width="120" />
	<item id="sexCode" alias="性别" type="string" length="1" width="40"
		not-null="1" defalutValue="9" queryable="true">
		<dic id="chis.dictionary.gender" />
	</item>
	<item id="birthday" alias="出生日期" type="date" not-null="1"
		maxValue="%server.date.today" width="100" queryable="true" between="true"/>
	<item id="address" alias="地址" not-null="1" width="200" queryable="true"></item>
	<item id="team" alias="服务团队" not-null="1"></item>
	<item id="juwei" alias="居委" not-null="1"></item>
	<item id="qybz" alias="签约标志" not-null="1"></item>
	<item id="ybkh" alias="医保卡号" not-null="1"></item>
	<item id="bz" alias="备注" not-null="1"></item>
	<item id="nbbm" alias="内部编码" not-null="1"></item>
	<item id="zrys" alias="责任医生" not-null="1"></item>
</entry>